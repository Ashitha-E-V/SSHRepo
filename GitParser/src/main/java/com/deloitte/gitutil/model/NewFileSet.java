package com.deloitte.gitutil.model;

import java.io.Serializable;

public class NewFileSet implements Serializable{
	
	private static final long serialVersionUID = 1L;

	String className;
	String packageName;
	
	
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	@Override
	public String toString() {
		return packageName+","+className+"\n";
		
	}

}
