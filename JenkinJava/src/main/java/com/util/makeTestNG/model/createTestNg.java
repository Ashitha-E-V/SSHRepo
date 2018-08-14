package com.util.makeTestNG.model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

public class createTestNg {

	public static XmlSuite getsuite(String suiteName, String parallelName, int threadCount, int verbose, boolean allowReturnValues, String testName,boolean preserveOrder,
			ArrayList<String> impactClassList){

		//Instance of XML Suite and assigning a name for it
		XmlSuite xmlsuite = new XmlSuite();
		xmlsuite.setName(suiteName);
		xmlsuite.setParallel(parallelName);
		xmlsuite.setThreadCount(threadCount);
		xmlsuite.setVerbose(verbose);
		xmlsuite.setAllowReturnValues(allowReturnValues);
		//xmlsuite.setConfigFailurePolicy("Continue");

		//Instance of xmlTest and assiging a name for it
		XmlTest xmltest = new XmlTest(xmlsuite);
		xmltest.setName(testName);
		xmltest.setPreserveOrder(preserveOrder);

		ArrayList<XmlClass> classNameList = new ArrayList<XmlClass>();

		for(String list : impactClassList) {
			XmlClass csString = new XmlClass(list);
			classNameList.add(csString);
		}

		xmltest.setXmlClasses(classNameList);

		return xmlsuite;
	}


	public static void write(XmlSuite suite, String xmlFilePath) {
		try
		{
			FileOutputStream outputStream = new FileOutputStream(xmlFilePath);

			System.out.println(suite.toXml());
			byte[] b = suite.toXml().getBytes();
			outputStream.write(b);


			outputStream.close();
		}catch(IOException i)
		{
			i.printStackTrace();
		}

	}

}
