package com.deloitte.gitutil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.CommitTimeRevFilter;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

import com.deloitte.disney.util.PullDisney;
import com.deloitte.gitutil.model.ChangeSet;

public class GitActionMain {
	
	public static Date dt;
	public static HashMap<String, Integer> modifiedFileToStatusMap = new HashMap();
	public static HashMap<String, String> modifiedFileToRevCodeMap = new HashMap();
	public static HashMap<String, Integer> modifiedFileToStatusMapWithoutDeletions = new HashMap();
	
	
	public static void main(String[] args) throws IOException, NoHeadException, GitAPIException {
		
		
		if(args.length > 0 ) {
			System.out.println("loading config file from ::" +args[0]);
			ConfigProvider.init(args[0]);
		}else {
			System.out.println("loading config file classpath");
			ConfigProvider.init("config.properties");
		}
		
		ConfigProvider.init("config.properties");
		
		HashMap<String, ArrayList<ChangeSet>> fileToModifiedMethodsMap = new HashMap();
		ArrayList<ChangeSet> changeList = new ArrayList<ChangeSet>();
		
		(new PullDisney()).should_connect_to_public_ssh(new ConfigProvider());
		
		dt = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		cal.add(Calendar.HOUR, ConfigProvider.timeDuration*(-1));
		dt = cal.getTime();
		FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
		Repository repository = repositoryBuilder.setGitDir(new File(ConfigProvider.gitRepoPaht))
                .readEnvironment() // scan environment GIT_* variables
                .findGitDir() // scan up the file system tree
                .setMustExist(true)
                .build();
		System.out.println("Repository Opened");
		Git git = new Git(repository);
		System.out.println("Git Object Initiated");
		
		ObjectId head = repository.resolve(Constants.HEAD);
		
		//Find modified files in the given duration
		commitHistory(git, head, repository);
		
		//Store files inside a folder
		modifiedFileToStatusMapWithoutDeletions = (HashMap<String, Integer>) modifiedFileToStatusMap.clone();
		readRevFile(repository);
		MethodDiff md = new MethodDiff();
		
		//Get modified methods for already present files
		for (String s: modifiedFileToStatusMap.keySet()) {
			if (s.endsWith(".java") && modifiedFileToStatusMap.get(s) == 1) {
				changeList.addAll(md.methodDiffInClass(
						ConfigProvider.prevRevPath + s,
						ConfigProvider.headRevPath + s));
				if(!fileToModifiedMethodsMap.containsKey(s)) {
					fileToModifiedMethodsMap.put(s, changeList);
					System.out.println(s+ "  "+fileToModifiedMethodsMap.get(s));
				}
			}
		}
		if (!fileToModifiedMethodsMap.isEmpty()) {
			System.out.println(changeList.size());
			System.out.println("Done finding modified methods");
			write(changeList);
		}
		else {
			write(changeList);
			System.out.println("Either only new files were added or no '.java' files were modified");
		}
		
		//Logic to store files and package names of newly created files
		
		
	}

	
	public static void writeNewFile(Repository repository) throws RevisionSyntaxException, AmbiguousObjectException, IncorrectObjectTypeException, IOException {
		for (String s: modifiedFileToRevCodeMap.keySet()) {
			if (modifiedFileToStatusMap.get(s) == 0) {
				ObjectId headCom = repository.resolve(Constants.HEAD);
				RevWalk rw = new RevWalk(repository);
				RevCommit rcNew = rw.parseCommit(headCom);
				RevTree rtNew = rcNew.getTree();
				System.out.println();
				
				try (TreeWalk treeWalkOld = new TreeWalk(repository)) {
                    treeWalkOld.addTree(rtNew);
                    treeWalkOld.setRecursive(true);

                    treeWalkOld.setFilter(PathFilter.create(s));
                    if (!treeWalkOld.next()) {
                        throw new IllegalStateException("Did not find expected file " + s);
                    }
                    
                    ObjectId objectId = treeWalkOld.getObjectId(0);
                    ObjectLoader loader = repository.open(objectId);
                    
                    
                    Path path = Paths.get(ConfigProvider.headRevPath+s);
                    Files.createDirectories(path.getParent());                                       
                                      
                    File f = new File(ConfigProvider.headRevPath+s);                    
                    if(f.exists()) {
                    	f.delete();
                    }
                    f.createNewFile();
                                        
                    // and then one can call the loader to read the file
                    OutputStream outp = new FileOutputStream(f);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    try {
						loader.copyTo(out);
					} catch (Exception e) {
						e.printStackTrace();
					}
                    out.writeTo(outp);
                }
				System.out.println("New File Downloaded");
				
			}
		}
	}
	
	//Read contents of a modified file from head and past commit
	public static void readRevFile(Repository repository) throws RevisionSyntaxException, AmbiguousObjectException, IncorrectObjectTypeException, IOException {
		
		for (String s: modifiedFileToRevCodeMap.keySet()) {
			if (modifiedFileToStatusMap.get(s) == 1) {
				
				ObjectId pastCom = repository.resolve(modifiedFileToRevCodeMap.get(s));
				ObjectId headCom = repository.resolve(Constants.HEAD);
				RevWalk rw = new RevWalk(repository);
				RevCommit rcOld = rw.parseCommit(pastCom);
				RevCommit rcNew = rw.parseCommit(headCom);
				RevTree rtOld = rcOld.getTree();
				RevTree rtNew = rcNew.getTree();
				System.out.println();
				System.out.println(s);
				try (TreeWalk treeWalkHead = new TreeWalk(repository)) {
                    treeWalkHead.addTree(rtNew);
                    treeWalkHead.setRecursive(true);
                    treeWalkHead.setFilter(PathFilter.create(s));
                    if (!treeWalkHead.next()) {
                        System.out.println("Did not find the file in Head --> Deletion --> Skipping this");
                        modifiedFileToStatusMap.remove(s);
                        continue;
                    }
                    
                    ObjectId objectId = treeWalkHead.getObjectId(0);
                    ObjectLoader loader = repository.open(objectId);
                    
                    
                    Path path = Paths.get(ConfigProvider.headRevPath+s);
                    Files.createDirectories(path.getParent());                                       
                                      
                    File f = new File(ConfigProvider.headRevPath+s);                    
                    if(f.exists()) {
                    	f.delete();
                    }
                    f.createNewFile();
                                        
                    // and then one can the loader to read the file
                    OutputStream outp = new FileOutputStream(f);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    try {
						loader.copyTo(out);
					} catch (Exception e) {
						e.printStackTrace();
					}
                    out.writeTo(outp);
                }
				System.out.println("Head Rev File downloaded");
				
				try (TreeWalk treeWalkOld = new TreeWalk(repository)) {
                    treeWalkOld.addTree(rtOld);
                    treeWalkOld.setRecursive(true);

                    treeWalkOld.setFilter(PathFilter.create(s));
                    if (!treeWalkOld.next()) {
                        throw new IllegalStateException("Did not find expected file " + s);
                    }
                    
                    ObjectId objectId = treeWalkOld.getObjectId(0);
                    ObjectLoader loader = repository.open(objectId);
                    
                    
                    Path path = Paths.get(ConfigProvider.prevRevPath+s);
                    Files.createDirectories(path.getParent());                                       
                                      
                    File f = new File(ConfigProvider.prevRevPath+s);                    
                    if(f.exists()) {
                    	f.delete();
                    }
                    f.createNewFile();
                                        
                    // and then one can call the loader to read the file
                    OutputStream outp = new FileOutputStream(f);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    try {
						loader.copyTo(out);
					} catch (Exception e) {
						e.printStackTrace();
					}
                    out.writeTo(outp);
                }
				System.out.println("PastRev File Downloaded");
				
			}
		}
		
	}
	
	
	//Will return the files changed against commit IDs
	public static void commitHistory(Git git, ObjectId head, Repository repository) throws NoHeadException, GitAPIException, IncorrectObjectTypeException, CorruptObjectException, IOException 
	{	
		RevFilter lastConfiguredHours = CommitTimeRevFilter.after(dt);
		List<String> authorNamesFilter = new ArrayList<String>();
		if (ConfigProvider.filterAuthorNames != null && !ConfigProvider.filterAuthorNames.isEmpty()) {
			authorNamesFilter = Arrays.asList(ConfigProvider.filterAuthorNames.split("\\s*,\\s*"));
		} 
	    Iterable<RevCommit> logs = git.log().setRevFilter(lastConfiguredHours).call();
	    int k = 0;
	    for (RevCommit commit : logs) {
	        String commitID = commit.getName();
	        if (commitID != null && !commitID.isEmpty())
	        {
	            TreeWalk tw = new TreeWalk(repository);
	            tw.setRecursive(true);
	            RevCommit commitToCheck = commit;
	            tw.addTree(commitToCheck.getTree());
	            for (RevCommit parent : commitToCheck.getParents())
	            {
	                tw.addTree(parent.getTree());
	            }
	            while (tw.next())
	            {
	                int similarParents = 0;
	                for (int i = 1; i < tw.getTreeCount(); i++)
	                    if (tw.getFileMode(i) == tw.getFileMode(0) && tw.getObjectId(0).equals(tw.getObjectId(i)))
	                        similarParents++;
	           
	                if ((new Date(commit.getCommitTime() * 1000L)).after(dt) &&  (authorNamesFilter.isEmpty() || authorNamesFilter.contains(commit.getAuthorIdent().getName())) && !modifiedFileToStatusMap.containsKey(tw.getPathString()) && similarParents==0 && tw.getNameString().endsWith(".java")) {
						modifiedFileToStatusMap.put(tw.getPathString(), 0);

						System.out.println("Revision: "+ commitID+ "  Time: "+ (new Date(commit.getCommitTime() * 1000L))+ "  File names: "+ tw.getNameString() + "  "+ tw.getPathString());
					
					}
	                
	            }
	        }
	    }

	    System.out.println();
	    Date da = new Date();
	    Calendar caa = Calendar.getInstance();
	    caa.setTime(da);
	    caa.add(Calendar.MINUTE, 60);
	    da = caa.getTime();
	    
	    for(String s: modifiedFileToStatusMap.keySet()) {
	    	for (RevCommit rc: git.log().add(head).addPath(s).call()) {
	    		if ((new Date(rc.getCommitTime() * 1000L)).before(dt)) {
	    			modifiedFileToRevCodeMap.put(s, rc.getName());
	    			modifiedFileToStatusMap.put(s, modifiedFileToStatusMap.get(s)+1);
	    			break;
	    		}
	    		System.out.println("Path: "+ s + " Time: "+ (new Date(rc.getCommitTime() * 1000L)));
	    	}
	    }
	    
	    System.out.println();
	    for(String s: modifiedFileToRevCodeMap.keySet()) {
	    	System.out.println("Path: "+ s + " Revision: "+ modifiedFileToRevCodeMap.get(s));
	    }
	    
	}
	public static void write(ArrayList<ChangeSet> changeSet) throws IOException {
		try
		{
			PrintWriter pw = new PrintWriter(ConfigProvider.storagePath+"changeSet.csv");
			pw.flush();	
			pw.append(ConfigProvider.CSV_HEADER_PROJECT_NAME+ConfigProvider.CSV_DELIMITER+ConfigProvider.CSV_HEADER_PACKAGE_NAME+ConfigProvider.CSV_DELIMITER+ConfigProvider.CSV_HEADER_CLASS_NAME+ConfigProvider.CSV_DELIMITER+ConfigProvider.CSV_HEADER_METHOD_NAME+"\n");
			for(ChangeSet cs : changeSet) {
				System.out.println(cs);
				pw.append(cs.toString());
			}
			            
			pw.close();
			if(changeSet.isEmpty()){
			System.out.println("Serialized data is saved in changeSet.csv");
			}

		}catch(IOException i)
		{
			i.printStackTrace();
			throw i;
		}

	}
	
	
}
