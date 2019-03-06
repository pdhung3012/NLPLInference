package download;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.eclipse.jgit.errors.TransportException;

import utils.FileIO;
import utils.GithubClient;
import consts.GithubConfig;
import consts.PathConstanct;

public class DownloadGithubProject {

	// aa
//	 "android.",
//	"com.google.gwt.", "org.hibernate.", "org.joda.time.",
//	"com.thoughtworks.xstream."
	final static String[] keywords = new String[] {"android."
			 };
	
	public static void main(String[] args) {
		downloadProjects();
	}

	public static void downloadProjects() {
		final GithubClient gitClient = new GithubClient();
		ExecutorService pool = Executors.newFixedThreadPool(8);
		for (String keyword : keywords) {
			String listContent = FileIO
					.readStringFromFile(PathConstanct.fopListLibraryLocation+File.separator+"repos-5stars-50commits-lib-"+keyword+".csv");
			Scanner sc = new Scanner(listContent);
			int index = 0;
			String[] arrProjectName=FileIO.readStringFromFile(PathConstanct.fopListLibraryLocation+"downloaded-"+keyword+".txt").split("\n");
			HashSet<String> setExistProjects=new HashSet<String>();
			for(int i=0;i<arrProjectName.length;i++) {
				setExistProjects.add(arrProjectName[i].trim());
			}
			File fUnable=new File(PathConstanct.fopListLibraryLocation+"unable-"+keyword+".txt");
			if(!fUnable.exists()){
				FileIO.writeStringToFile("", fUnable.getAbsolutePath());
			} else{
				String[] arrUnable=FileIO.readStringFromFile(PathConstanct.fopListLibraryLocation+"unable-"+keyword+".txt").split("\n");
				for(int i=0;i<arrUnable.length;i++) {
					setExistProjects.add(arrUnable[i].trim().split("\t")[0].trim());
				}
			}
			
			while (sc.hasNextLine()) {
				String projectContent = sc.nextLine().split(",")[0];
				String[] arrContent = projectContent.split("/");
				final String username = arrContent[0];
				final String repos = arrContent[1];
				final String keyw=keyword;
				String keyCheck=username+"_"+repos;
				index++;
				if(!setExistProjects.contains(keyCheck)){
					File currentFile = new File(PathConstanct.fopProjectLocation+File.separator+username+"_"+repos+"/");
				    currentFile.delete();
					pool.execute(new Runnable() {
						@Override
						public void run() {

							while (true) {
								boolean gotIt = false;
								try {
									System.out.println("begin download "+username+"-" +repos);
									
//									gotIt = gitClient.downloadRepoContent(GithubConfig.urlGithub+username+"/"+repos+".git", GithubConfig.accessTokens,
//											"master", PathConstanct.fopProjectLocation+File.separator+username+"_"+repos+"/");
									String branch = gitClient.downloadRepoContentCheck(GithubConfig.urlGithub+username+"/"+repos+".git", GithubConfig.accessTokens,
									"master", PathConstanct.fopProjectLocation+File.separator+username+"_"+repos+"/.git/");
									if(!branch.isEmpty()){
										System.out.println("begin download branch "+branch+" of repo"+username+"-" +repos);
										gotIt = gitClient.downloadRepoContentByArchiveDownload(GithubConfig.urlGithub+username+"/"+repos+"/archive/"+branch+".zip", GithubConfig.accessTokens,
												 PathConstanct.fopProjectLocation+File.separator+username+"_"+repos+".zip");
									}
									
								}
								catch (IOException ex) {
									ex.printStackTrace();
									FileIO.appendStringToFile(username+"_"+repos+"\t"+ex.getMessage()+"\tjava.io.IOException\n",PathConstanct.fopListLibraryLocation+"unable-"+keyw+".txt");
									break;
								}
								
								catch (org.eclipse.jgit.api.errors.TransportException ex) {
									ex.printStackTrace();
									System.out.println("exception here");
									FileIO.appendStringToFile(username+"_"+repos+"\t"+ex.getMessage()+"\torg.eclipse.jgit.api.errors.TransportException\n",PathConstanct.fopListLibraryLocation+"unable-"+keyw+".txt");
									break;
								}
								catch (Exception ex) {
									ex.printStackTrace();
								}
								if (gotIt){
									System.out.println(" project "+username+"-" +repos + " downloaded");
									FileIO.appendStringToFile(username+"_"+repos+"\n",PathConstanct.fopListLibraryLocation+"downloaded-"+keyw+".txt");
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
