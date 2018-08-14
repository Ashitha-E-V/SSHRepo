package com.util.makeTestNG.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.util.runJenkins.ConfigFile;

public class ReadClassNames {

	@SuppressWarnings("resource")
	public static HashMap<String, ArrayList<String>> getImpactedEnvClassMap(String impactedEnvClassFilePath){
		try {

			ArrayList<ImpactClassList> impactedClassList = getImpactClassList(ConfigFile.impactedClassListFilePath);
			BufferedReader reader = new BufferedReader(new FileReader(impactedEnvClassFilePath));
			String line = null;

			String env1 = null;
			String env2 = null;
			String env3 = null;
			String env4 = null;
			String env5 = null;

			ArrayList <String> c1 = new ArrayList<String>();
			ArrayList <String> c2 = new ArrayList<String>();
			ArrayList <String> c3 = new ArrayList<String>();
			ArrayList <String> c4 = new ArrayList<String>();
			ArrayList <String> c5 = new ArrayList<String>();


			HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();

			while ((line = reader.readLine()) != null) {
				for(ImpactClassList classList : impactedClassList) {

					String[] icString = line.split(ConfigFile.CSV_DELIMITER);
					if(env1 == null && classList.getClassName().equalsIgnoreCase(icString[0])) {
						env1 = icString[1];
					}
					else if(env1 != null && !env1.isEmpty() && !env1.equalsIgnoreCase(icString[1]) && env2==null && classList.getClassName().equalsIgnoreCase(icString[0])) {
						env2 = icString[1];
					}
					else if(env2 != null && !env2.isEmpty() && !env1.equalsIgnoreCase(icString[1]) && !env2.equalsIgnoreCase(icString[1]) && env3==null && classList.getClassName().equalsIgnoreCase(icString[0])) {
						env3 = icString[1];
					}
					else if(env3 != null && !env3.isEmpty() && !env1.equalsIgnoreCase(icString[1]) && !env2.equalsIgnoreCase(icString[1]) && !env3.equalsIgnoreCase(icString[1]) && env4==null && classList.getClassName().equalsIgnoreCase(icString[0])) {
						env4 = icString[1];
					}
					else if(env4 != null && !env4.isEmpty() && !env1.equalsIgnoreCase(icString[1]) && !env2.equalsIgnoreCase(icString[1]) && !env3.equalsIgnoreCase(icString[1])
							&& !env4.equalsIgnoreCase(icString[1]) && env5==null && classList.getClassName().equalsIgnoreCase(icString[0])) {
						env5 = icString[1];
					}

					if(env1 != null && env1.equalsIgnoreCase(icString[1]) && classList.getClassName().equalsIgnoreCase(icString[0])) {
						c1.add(classList.getPackageName()+"."+icString[0]);
					}
					else if(env2 != null && env2.equalsIgnoreCase(icString[1]) && classList.getClassName().equalsIgnoreCase(icString[0])) {
						c2.add(classList.getPackageName()+"."+icString[0]);
					}
					else if(env3 != null && env3.equalsIgnoreCase(icString[1]) && classList.getClassName().equalsIgnoreCase(icString[0])) {
						c3.add(classList.getPackageName()+"."+icString[0]);
					}
					else if(env4 != null && env4.equalsIgnoreCase(icString[1]) && classList.getClassName().equalsIgnoreCase(icString[0])) {
						c4.add(classList.getPackageName()+"."+icString[0]);
					}
					else if(env5 != null && env5.equalsIgnoreCase(icString[1]) && classList.getClassName().equalsIgnoreCase(icString[0])) {
						c5.add(classList.getPackageName()+"."+icString[0]);
					}
				}
			}
			if(env1 != null) {
				map.put(env1, c1);	
			}
			if(env2 != null) {
				map.put(env2, c2);
			}
			if(env3 != null) {
				map.put(env3, c3);
			}
			if(env4 != null) {
				map.put(env4, c4);
			}
			if(env5 != null) {
				map.put(env5, c5);
			}

			return map;
		} catch ( IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	public static ArrayList<ImpactClassList> getImpactClassList(String impactedClassListFilePath) {
		try {

			BufferedReader reader = new BufferedReader(new FileReader(impactedClassListFilePath));
			String line = null;
			ArrayList<ImpactClassList>  impactClassList = new ArrayList<>();
			while ((line = reader.readLine()) != null) {
				ImpactClassList cs = new ImpactClassList();
				String[] csString = line.split(ConfigFile.CSV_DELIMITER);
				cs.setProjectName(csString[0]);
				cs.setPackageName(csString[1]);
				cs.setClassName(csString[2]);
				impactClassList.add(cs);
			}

			return impactClassList;
		} catch ( IOException e) {
			e.printStackTrace();
		}

		return null;


	}

}
