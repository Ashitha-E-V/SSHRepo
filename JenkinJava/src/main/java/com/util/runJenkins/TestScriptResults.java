package com.util.runJenkins;

public class TestScriptResults {
	String scriptName;
	String methodName;
	String result;
	
	public String getscriptName(){
		return scriptName;
	}
	public String getmethodName(){
		return methodName;
	}
	public void setscriptName(String scriptName){
		this.scriptName= scriptName;
	}
	public void setmethodName(String methodName){
		this.methodName= methodName;
	}
	public String getresult(){
		return result;
	}
	public void setResult(String result){
		this.result= result;
	}
}
