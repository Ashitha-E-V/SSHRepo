package com.util.makeTestNG.model;

import java.io.Serializable;

public class ImpactClassList implements Serializable{
	
	private static final long serialVersionUID = 1L;

	String className;
	String methodName;
	String projectName;
	String packageName;
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	
	public ImpactClassList() {
		
	}
	
	public ImpactClassList(String className, String projectName,
			String packageName) {
		super();
		this.className = className;
		this.projectName = projectName;
		this.packageName = packageName;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}


}
