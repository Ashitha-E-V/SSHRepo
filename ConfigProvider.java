package com.deloitte.gitutil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public  class  ConfigProvider {
	
	public static String storagePath;
	public static String headRevPath;
	public static String prevRevPath;
	public static String gitRepoPaht;
	public static Integer timeDuration;
	public static String disneyURL;
	public static String disneyLocalRepoLocation;
	public static String filterAuthorNames;
	public static String branchName;
	public static String passPhrase;

	public final static  String HEAD_REV = "HeadRev/" ;
	public final static String PREV_REV = "PrevRev/" ;
	public final static String CSV_HEADER_PROJECT_NAME = "project";
	public final static String CSV_HEADER_PACKAGE_NAME = "package";
	public final static String CSV_HEADER_CLASS_NAME = "class";
	public final static String CSV_HEADER_METHOD_NAME = "method";
	public final static String CSV_DELIMITER = ":::";
	
	
	public static void init(String configFilePath) throws IOException {
		Properties prop = new Properties();
//		InputStream input = new FileInputStream("config.properties");
		InputStream input;
		try {
		input = new FileInputStream(configFilePath);
		prop.load(input);
		storagePath =   prop.getProperty("storage");  
		headRevPath = storagePath  + HEAD_REV;
		prevRevPath =  storagePath  + PREV_REV;	
		gitRepoPaht = prop.getProperty("gitRepo");
		timeDuration = new Integer(24);
		disneyURL = prop.getProperty("disneyURL");
		branchName = prop.getProperty("branchName");
		passPhrase = prop.getProperty("passPhrase");

		disneyLocalRepoLocation = prop.getProperty("disneyRep");
		if (prop.getProperty("timeDurationInHours")!=null) {
			timeDuration = Integer.parseInt(prop.getProperty("timeDurationInHours"));
		}
		if(prop.getProperty("authorNames")!=null) {
			filterAuthorNames = prop.getProperty("authorNames");
		}
		} catch (FileNotFoundException e) {
			System.out.println("config file not found at ::::" +configFilePath );
			throw e;
		} catch (IOException iOE) {
			System.out.println("config file not reable ::::" +configFilePath );
			throw iOE;
		}
	}

}
