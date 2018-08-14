package com.util.runJenkins;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigFile {
	public final static String jenkinsUrl = "http://localhost:8383";
	public final static String userName = "Ashitha";
	public final static String password = "Chaitu@98";
	public final static String jobName = "Second";
	
	public final static String suiteXmlFile = "suiteXmlFile";
	public final static String suiteXmlFileValue = "Env1TestNg.xml";
	public final static String testNGNameAppender = "testNg.xml";
	
	public final static String repo="Repo";
	public final static String repoValue="https://gitlab.com/mailsforprk/code-change-impact-analyzer";
	
	public final static String grid="grid";
	public final static String gridValue="http://localhost:4444/";
	
	public final static String environment="environment";
	public final static String environmentValue="stage";
	
	public static String filePathToTestNgResultsXmlFile;
	public static String remoteGitURI = "https://gitlab.com/mailsforprk/code-change-impact-analyzer.git";
	public static String branchName = "phase2_development";
	
	public static  MailConfigModel mailConfig;
	
	public final static String CSV_DELIMITER = ":::";
	public final static String PATH = "C:/Users/vinadubey/Desktop/Diana/code-change-impact-analyzer";
	public final static String environmentMappingFilePath = "../CSVFiles/EnvironmentMapping.csv";
	public final static String testNGLoc = "../SampleJavaProjToTest/src/test/resources/";
	public final static String impactedClassListFilePath = "../CSVFiles/Impacted_Test_Scripts.csv";
	public final static String gitRepo = "../.git";


	public static void init(String mailConfigFilePath, String testNGConfigFilePath) throws IOException {

		Properties testNGProp = new Properties();
		Properties mailProp = new Properties();
		InputStream input, input1;
		input = new FileInputStream(mailConfigFilePath);
		input1 = new FileInputStream(testNGConfigFilePath);
		mailProp.load(input);
		testNGProp.load(input1);
		filePathToTestNgResultsXmlFile = testNGProp.getProperty("TestNGResultFile");
		remoteGitURI = testNGProp.getProperty("remoteGitURI");
		branchName = testNGProp.getProperty("gitBranch");
		mailConfig =new MailConfigModel();
		mailConfig.setUsername(mailProp.getProperty("mail.username"));
		mailConfig.setPassword(mailProp.getProperty("mail.password"));
		mailConfig.setSmtp_host(mailProp.getProperty("mail.smtp.host"));
		mailConfig.setSmtp_auth(mailProp.getProperty("mail.smtp.auth"));
		if(mailProp.getProperty("mail.smtp.starttls.enable") != null) {
			mailConfig.setSmtp_starttls_enable(mailProp.getProperty("mail.smtp.starttls.enable"));	
		}
		if(mailProp.getProperty("mail.smtp.socketFactory.port") != null) {
			mailConfig.setSmtp_socketFactory_port(mailProp.getProperty("mail.smtp.socketFactory.port"));
		}

		mailConfig.setSmtp_port(mailProp.getProperty("mail.smtp.port"));
		mailConfig.setMail_to(mailProp.getProperty("mail.to"));
		mailConfig.setSubject(mailProp.getProperty("mail.subject"));
		mailConfig.setBody(mailProp.getProperty("mail.body"));
		mailConfig.setRegards(mailProp.getProperty("mail.regards"));
	}	
}
