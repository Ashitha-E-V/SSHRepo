package com.util.runJenkins;

import java.net.URI;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.google.common.collect.ImmutableMap;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Job;
import com.offbytwo.jenkins.model.JobWithDetails;
//import com.offbytwo.jenkins.model.

public class InitializeTestExecution {

	public static void main(String[] args) {

		try {
			ConfigFile.init(args[0], args[1]);
			JenkinsServer jenkins = new JenkinsServer(new URI(ConfigFile.jenkinsUrl), ConfigFile.userName, ConfigFile.password);

			Map<String, Job> jobs = jenkins.getJobs();
			
			final ImmutableMap<String, String> params = ImmutableMap.of(
					ConfigFile.suiteXmlFile,ConfigFile.suiteXmlFileValue,
					ConfigFile.repo,ConfigFile.repoValue,
					ConfigFile.grid,ConfigFile.gridValue,
					ConfigFile.environment,ConfigFile.environmentValue	
					);

			JobWithDetails jobwithBuild = jenkins.getJob(ConfigFile.jobName);
			jobwithBuild.build(params);
			int i = jobwithBuild.getLastBuild().getNumber();
			System.out.println("Job Running...");
			while(true) {
				if (i+1 == jobwithBuild.getLastCompletedBuild().getNumber()) {
					break;
				}
				jobwithBuild = jenkins.getJob(ConfigFile.jobName);
//				System.out.println("not yet");
			}
			
			
			System.out.println(jobwithBuild.getLastBuild().getTestResult().getPassCount());
			System.out.println(jobwithBuild.getLastBuild().details().getConsoleOutputText());
			
			parseXml();
			jenkins.close();
		} catch (Exception e) {
			System.out.println("Exception caught");
			e.printStackTrace();
		}
	}

	static void parseXml() throws IOException, AddressException, MessagingException {

		List<TestScriptResults> scriptResults = new ArrayList<TestScriptResults>();
		try {
			// First, create a new XMLInputFactory
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			// Setup a new eventReader
			InputStream in = new FileInputStream(ConfigFile.filePathToTestNgResultsXmlFile);
			XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
			// read the XML document
			TestScriptResults scriptResult = null;
			String className = null;
			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();
				
				if (event.isStartElement()) {
					StartElement startElement = event.asStartElement();
					
					if (startElement.getName().getLocalPart()
							.equals("class")) {

						className = "";
						Iterator<Attribute> attributes = startElement
								.getAttributes();
						while (attributes.hasNext()) {
							Attribute attribute = attributes.next();
							if (attribute.getName().toString().equals("name")) {
								className = attribute.getValue();
							}

						}
					}
					if (startElement.getName().getLocalPart()
							.equals("test-method")) {
						scriptResult = new TestScriptResults();
						scriptResult.setscriptName(className);
						Iterator<Attribute> attributes = startElement
								.getAttributes();
						while (attributes.hasNext()) {
							Attribute attribute = attributes.next();
							if (attribute.getName().toString().equals("status")) {
								scriptResult.setResult(attribute.getValue());
							}
							if (attribute.getName().toString().equals("signature")) {
								scriptResult.setmethodName(attribute.getValue());
							}

						}
						
					}

				}
				// If we reach the end of an scriptResult element, we add it to
				// the list
				if (event.isEndElement()) {
					EndElement endElement = event.asEndElement();
					if (endElement.getName().getLocalPart()
							.equals("test-method")) {
						scriptResults.add(scriptResult);
					}
				}

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
		
		
		System.out.println(scriptResults.toString());
		sendMail(scriptResults);
	}
	static void sendMail(List<TestScriptResults> testList) throws AddressException, MessagingException, IOException{
		System.out.println("Configuring SMTP");

		Properties props = new Properties();



		props.put("mail.smtp.host", ConfigFile.mailConfig.getSmtp_host());
		props.put("mail.smtp.auth",ConfigFile.mailConfig.getSmtp_auth());
		props.put("mail.smtp.port", ConfigFile.mailConfig.getSmtp_port());

		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.port", ConfigFile.mailConfig.getSmtp_socketFactory_port());

		props.put("mail.smtp.starttls.enable", ConfigFile.mailConfig.getSmtp_starttls_enable());

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(ConfigFile.mailConfig.getUsername(), ConfigFile.mailConfig.getPassword());
			}
		});

		session.setDebug(true);

		Message message = new MimeMessage(session);

		message.setFrom(new InternetAddress(ConfigFile.mailConfig.getUsername()));

		message.addRecipients(Message.RecipientType.CC, 
				InternetAddress.parse(ConfigFile.mailConfig.getMail_to()));

		message.setSubject(ConfigFile.mailConfig.getSubject());
		String messageBody = ConfigFile.mailConfig.getBody();
		String changeSet = "";

		for(TestScriptResults test : testList){
			changeSet=changeSet+"<tr>";
			changeSet=changeSet+"<td>"+test.getscriptName()+"</td>"+"<td>"+test.getmethodName()+"</td>"+"<td>"+test.getresult()+"</td>\n";
			changeSet=changeSet+"</tr>";
		}

		messageBody += changeSet+"</table>";
		messageBody += ConfigFile.mailConfig.getRegards();
		message.setContent(messageBody,"text/html" );  

		Transport.send(message);

	}
	
}


