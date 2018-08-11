package com.deloitte.disney.util;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;

import com.deloitte.gitutil.ConfigProvider;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class PullDisney {

	public void should_connect_to_public_ssh(ConfigProvider cp) throws IOException, GitAPIException {
		
		cp.init("config.properties");

		SshSessionFactory sshSessionFactory = new JschConfigSessionFactory() {
			@Override
			protected void configure(OpenSshConfig.Host host, Session session) {
				session.setUserInfo(new UserInfo() {
					@Override
					public String getPassphrase() {
						return "Deloitte.20";
					}

					@Override
					public String getPassword() {
						return null;
					}

					@Override
					public boolean promptPassword(String message) {
						return false;
					}

					@Override
					public boolean promptPassphrase(String message) {
						return true;
					}

					@Override
					public boolean promptYesNo(String message) {
						return false;
					}

					@Override
					public void showMessage(String message) {
					}
				});
			}
		};

		Repository locRep = new FileRepository(cp.disneyLocalRepoLocation + "/.git");
		Git git = new Git(locRep);
		PullCommand pullCmd = git.pull();

		try {
			PullResult result = pullCmd.setRemote(cp.disneyURL)
					.setTransportConfigCallback(transport -> {
						SshTransport sshTransport = (SshTransport) transport;
						sshTransport.setSshSessionFactory(sshSessionFactory);
					}).call();
			if (result.isSuccessful()) {
				System.out.println("Disney Pull Completed!");
			}
			else {
				System.out.println("Disney Pull Failed!");
			}
			System.out.println();
			System.out.println();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
