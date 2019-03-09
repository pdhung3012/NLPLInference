package download;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import utils.FileIO;
import utils.GithubClient;
import consts.GithubConfig;
import consts.PathConstanct;

public class DownloadSpecialProjectInStatType {

	public static void main(String[] args) {
	
			// TODO Auto-generated method stub
			final GithubClient gitClient = new GithubClient();
			ExecutorService pool = Executors.newFixedThreadPool(8);
			// for (String keyword : keywords) {
			String listContent = FileIO
					.readStringFromFile(PathConstanct.PATH_PROJECT_TRAIN_TEST_NAME+"specialProject.txt");
			String keyword="stattype";
			Scanner sc = new Scanner(listContent);
			int index = 0;
			
			if(!new File(PathConstanct.PATH_PROJECT_TRAIN_TEST_NAME + "downloaded-" + keyword
							+ ".txt").isFile()){
				FileIO.writeStringToFile("", PathConstanct.PATH_PROJECT_TRAIN_TEST_NAME + "downloaded-" + keyword
							+ ".txt");
			}
			
			String[] arrProjectName = FileIO.readStringFromFile(
					PathConstanct.PATH_PROJECT_TRAIN_TEST_NAME + "downloaded-" + keyword
							+ ".txt").split("\n");
			HashSet<String> setExistProjects = new HashSet<String>();
			for (int i = 0; i < arrProjectName.length; i++) {
				setExistProjects.add(arrProjectName[i].trim());
			}
			File fUnable = new File(PathConstanct.PATH_PROJECT_TRAIN_TEST_NAME
					+ "unable-" + keyword + ".txt");
			if (!fUnable.exists()) {
				FileIO.writeStringToFile("", fUnable.getAbsolutePath());
			} else {
				String[] arrUnable = FileIO.readStringFromFile(
						PathConstanct.PATH_PROJECT_TRAIN_TEST_NAME + "unable-stattype" 
								+ ".txt").split("\n");
				for (int i = 0; i < arrUnable.length; i++) {
					setExistProjects.add(arrUnable[i].trim().split("\t")[0].trim());
				}
			}

			while (sc.hasNextLine()) {
				String line=sc.nextLine();
				final String repository = line.split("\t")[0];
				final String projectName = line.split("\t")[1];
				
				final String keyCheck = repository+"-"+projectName;
				index++;
				if (!setExistProjects.contains(keyCheck)) {
					File currentFile = new File(PathConstanct.fopProjectLocation
							+ File.separator+ keyCheck + "/");
					currentFile.delete();
					pool.execute(new Runnable() {
						@Override
						public void run() {

							while (true) {
								boolean gotIt = false;
								try {
									System.out.println("begin download " + keyCheck);

									// gotIt =
									// gitClient.downloadRepoContent(GithubConfig.urlGithub+username+"/"+repos+".git",
									// GithubConfig.accessTokens,
									// "master",
									// PathConstanct.fopProjectLocation+File.separator+username+"_"+repos+"/");
									String projectSubRul=repository+"/"+projectName;
									String branch = gitClient
											.downloadRepoContentCheck(
													GithubConfig.urlGithub
															+projectSubRul + ".git",
													GithubConfig.accessTokens,
													"master",
													PathConstanct.fopProjectLocation
															+ File.separator
															+keyCheck + "/.git/");
									if (!branch.isEmpty()) {
										System.out.println("begin download branch "
												+ branch + " of repo" + keyCheck);
										gotIt = gitClient
												.downloadRepoContentByArchiveDownload(
														GithubConfig.urlGithub
																+projectSubRul
																+ "/archive/"
																+ branch + ".zip",
														GithubConfig.accessTokens,
														PathConstanct.fopProjectLocation
																+ File.separator
																+keyCheck + ".zip");
									}

								} catch (IOException ex) {
									ex.printStackTrace();
									FileIO.appendStringToFile(keyCheck + "\t" + ex.getMessage()
											+ "\tjava.io.IOException\n",
											PathConstanct.PATH_PROJECT_TRAIN_TEST_NAME
													+ "unable-stattype.txt");
									break;
								}

								catch (org.eclipse.jgit.api.errors.TransportException ex) {
									ex.printStackTrace();
									System.out.println("exception here");
									FileIO.appendStringToFile(
											keyCheck
													+ "\t"
													+ ex.getMessage()
													+ "\torg.eclipse.jgit.api.errors.TransportException\n",
													PathConstanct.PATH_PROJECT_TRAIN_TEST_NAME
													+ "unable-stattype.txt");
									break;
								} catch (Exception ex) {
									ex.printStackTrace();
								}
								if (gotIt) {
									System.out.println(" project " + keyCheck + " downloaded");
									FileIO.appendStringToFile(keyCheck + "\n",
											PathConstanct.PATH_PROJECT_TRAIN_TEST_NAME
													+ "downloaded-stattype.txt");
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


						}
					});
				}
			}
			// }

			pool.shutdown();
			// wait for them to finish for up to one minute.
			// try{
			// pool.awaitTermination(1, TimeUnit.MINUTES);
			// } catch(Exception ex){
			// ex.printStackTrace();
			// }
		}
	

}
