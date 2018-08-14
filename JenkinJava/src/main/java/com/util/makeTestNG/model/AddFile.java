package com.util.makeTestNG.model;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.RmCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.util.runJenkins.ConfigFile;

public class AddFile {

	
public static void addThenCommitThenPush(Git git) throws IOException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException {
		
		String name = "rahulshetty43";
        String password = "BeAware.1";
        
        CredentialsProvider cp = new UsernamePasswordCredentialsProvider(name, password);
		
//		FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
//		Repository repository = repositoryBuilder.setGitDir(new File(ConfigFile.gitRepo))
//                .readEnvironment() // scan environment GIT_* variables
//                .findGitDir() // scan up the file system tree
//                .setMustExist(true)
//                .build();
//		System.out.println("Repository Opened");
//		Git git = new Git(repository);
//		System.out.println("Git Object Initiated");
//		git.checkout().setName(ConfigFile.branchName).call();
//		System.out.println("Inside branch " + ConfigFile.branchName);
		
		
		AddCommand addComm = git.add();
		addComm.addFilepattern(ConfigFile.testNGLoc);
		addComm.call();
		Status status = git.status().call();
		Set<String> added = status.getAdded();
        for(String add : added) {
            System.out.println("Added: " + add);
        }	
		
		CommitCommand comComm = git.commit();
		comComm.setCommitter("rahulshetty43", "rahulcshetty43@gmail.com").setMessage("Comitting the new TestNG");
		comComm.call();
		
		
		PushCommand pushComm = git.push();
		pushComm.setRemote(ConfigFile.remoteGitURI).setCredentialsProvider(cp).call();
		System.out.println("The file has been successfully pushed to the remote repository.");
		
		
	}


public static void commitAndPushTheDelete(Git git) throws IOException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException {
	
	String name = "rahulshetty43";
    String password = "BeAware.1";
    
    
    CredentialsProvider cp = new UsernamePasswordCredentialsProvider(name, password);
	
//	FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
//	Repository repository = repositoryBuilder.setGitDir(new File(ConfigFile.gitRepo))
//            .readEnvironment() // scan environment GIT_* variables
//            .findGitDir() // scan up the file system tree
//            .setMustExist(true)
//            .build();
//	System.out.println("Repository Opened");
//	Git git = new Git(repository);
//	System.out.println("Git Object Initiated");
//	git.checkout().setName(ConfigFile.branchName).call();
//	System.out.println("Inside branch " + ConfigFile.branchName);
	
	
    RmCommand rmComm = git.rm();
    rmComm.addFilepattern(ConfigFile.testNGLoc+"/Env1testNg.xml");
    rmComm.call();
    Status status = git.status().call();
	Set<String> removed = status.getRemoved();
	for(String rmd : removed) {
      System.out.println("Removed: " + rmd);
	}
    
//	AddCommand addComm = git.add();
//	addComm.addFilepattern(ConfigFile.testNGLoc);
//	addComm.call();
//	Status status = git.status().call();
//	Set<String> added = status.getAdded();
//    for(String add : added) {
//        System.out.println("Added: " + add);
//    }	
	
	CommitCommand comComm = git.commit();
	comComm.setCommitter("rahulshetty43", "rahulcshetty43@gmail.com").setMessage("Committing the Delete");
	comComm.call();
	
	
	PushCommand pushComm = git.push();
	pushComm.setRemote(ConfigFile.remoteGitURI).setCredentialsProvider(cp).call();
	System.out.println("The testNG location has been emptied in the remote repository");
	
}



}
