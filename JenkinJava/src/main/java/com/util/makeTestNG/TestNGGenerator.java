package com.util.makeTestNG;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.testng.xml.XmlSuite;

import com.util.makeTestNG.model.ReadClassNames;
import com.util.makeTestNG.model.createTestNg;
import com.util.makeTestNG.model.AddFile;
import com.util.runJenkins.ConfigFile;

import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class TestNGGenerator{
	
	public static void main(String[] args) throws RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, IOException, GitAPIException {

		HashMap<String, ArrayList<String>> impactedEnvClassMap = ReadClassNames.getImpactedEnvClassMap(ConfigFile.environmentMappingFilePath); //new HashMap<String, ArrayList<String>>();
		String xmlFilePath = new String();
		
		//Initializing Git Object
		FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
		Repository repository = repositoryBuilder.setGitDir(new File(ConfigFile.gitRepo))
                .readEnvironment() // scan environment GIT_* variables
                .findGitDir() // scan up the file system tree
                .setMustExist(true)
                .build();
		System.out.println("Repository Opened");
		Git git = new Git(repository);
		System.out.println("Git Object Initiated");
		git.checkout().setName(ConfigFile.branchName).call();
		System.out.println("Inside branch " + ConfigFile.branchName);
		

		boolean delOldFiles = true;
		XmlSuite suite = new XmlSuite(); 
		for (java.util.Map.Entry<String, ArrayList<String>> entry : impactedEnvClassMap.entrySet()) {
			ArrayList<String> impactClassList = new ArrayList<String>();
			String key = entry.getKey();
			for(String className :entry.getValue() ) {
				impactClassList.add(className);
			}
			suite = createTestNg.getsuite("Vishal_DLP_Refactoring.xml", "methods", 3, 1, true, "Vishal_DLP_Refactoring.xml_DLR_Refactoring.xml", 
					true, impactClassList);
			xmlFilePath = ConfigFile.testNGLoc+key+ConfigFile.testNGNameAppender;
			
			File xmlFileDir = new File(ConfigFile.testNGLoc);
			if(delOldFiles) {
			AddFile.commitAndPushTheDelete(git);
			delOldFiles = false;
			}
			createTestNg.write(suite,xmlFilePath);
		}
		
		AddFile.addThenCommitThenPush(git);

	
	}
	

	
}
