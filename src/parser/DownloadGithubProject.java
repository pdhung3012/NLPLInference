package parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import utils.FileIO;
import utils.GithubClient;
import consts.GithubConfig;

public class DownloadGithubProject {

	final static String[] keywords = new String[] { "android.",
			"com.google.gwt.", "org.hibernate.", "org.joda.time.",
			"com.thoughtworks.xstream." };
	final static String fopProjectLocation = "/Users/hungphan/git/NLPLInference/data/downloaded/";
	final static String fopListLibraryLocation = "/Users/hungphan/git/NLPLInference/data/StatTypeProjects/";

	public static void main(String[] args) {
		downloadProjects();
	}

	public static void downloadProjects() {
		final GithubClient gitClient = new GithubClient();

		for (String keyword : keywords) {
			String listContent = FileIO
					.readStringFromFile(fopListLibraryLocation+"/repos-5stars-50commits-lib-"+keyword+".csv");
			Scanner sc = new Scanner(listContent);
			int index = 0;
			while (sc.hasNextLine()) {
				String projectContent = sc.nextLine().split(",")[0];
				String[] arrContent = projectContent.split("/");
				final String username = arrContent[0];
				final String repos = arrContent[1];
				index++;
				if(index==1){
					new Thread(new Runnable() {
						@Override
						public void run() {

							while (true) {
								boolean gotIt = false;
								try {
									gotIt = gitClient.downloadRepoContent(GithubConfig.urlGithub+username+"/"+repos+".git", GithubConfig.accessTokens,
											"master", fopProjectLocation+File.separator+username+"-"+repos+"/");
								} catch (Exception ex) {
									ex.printStackTrace();
								}
								if (gotIt){
									System.out.println("project " +repos + " downloaded");
									break;
								}
									
								try {
									System.out
											.println("Waiting for resetting limit.");
									Thread.sleep(1000);
								} catch (InterruptedException e1) {
									e1.printStackTrace();
								}
							}
//							System.out.println("project index " +index);
							
						}
					}).start();
				}
				break;
			}
		}
	}

}
