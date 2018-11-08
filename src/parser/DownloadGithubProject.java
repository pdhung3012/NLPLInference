package parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import utils.FileIO;
import utils.GithubClient;
import consts.GithubConfig;
import consts.PathConstanct;

public class DownloadGithubProject {

	final static String[] keywords = new String[] { "android.",
			"com.google.gwt.", "org.hibernate.", "org.joda.time.",
			"com.thoughtworks.xstream." };
	
	public static void main(String[] args) {
		downloadProjects();
	}

	public static void downloadProjects() {
		final GithubClient gitClient = new GithubClient();
		ExecutorService pool = Executors.newFixedThreadPool(5);
		for (String keyword : keywords) {
			String listContent = FileIO
					.readStringFromFile(PathConstanct.fopListLibraryLocation+File.separator+"repos-5stars-50commits-lib-"+keyword+".csv");
			Scanner sc = new Scanner(listContent);
			int index = 0;
			FileIO.writeStringToFile("",PathConstanct.fopListLibraryLocation+"downloaded-"+keyword+".txt");
			while (sc.hasNextLine()) {
				String projectContent = sc.nextLine().split(",")[0];
				String[] arrContent = projectContent.split("/");
				final String username = arrContent[0];
				final String repos = arrContent[1];
				final String keyw=keyword;
				index++;
				if(true){
					pool.execute(new Runnable() {
						@Override
						public void run() {

							while (true) {
								boolean gotIt = false;
								try {
									gotIt = gitClient.downloadRepoContent(GithubConfig.urlGithub+username+"/"+repos+".git", GithubConfig.accessTokens,
											"master", PathConstanct.fopProjectLocation+File.separator+username+"-"+repos+"/");
								} catch (Exception ex) {
									ex.printStackTrace();
								}
								if (gotIt){
									System.out.println("project " +repos + " downloaded");
									FileIO.appendStringToFile(username+"-"+repos+"\n",PathConstanct.fopListLibraryLocation+"downloaded-"+keyw+".txt");
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
					});
				}
			}
		}
		
		pool.shutdown();
		// wait for them to finish for up to one minute.
//		try{
//			pool.awaitTermination(1, TimeUnit.MINUTES);
//		} catch(Exception ex){
//			ex.printStackTrace();
//		}
		
	}

}
