package download;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.StaticDataFetcher;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import utils.FileIO;
import utils.GithubClient;

import static graphql.schema.idl.RuntimeWiring.newRuntimeWiring;

import consts.GithubConfig;
import consts.PathConstanct;

public class DownloadRepositoryStarInfoInGithub {
	
	public static boolean DownloadWebPage(String webpage,String fpOutput) 
    { 
		boolean result=false;
        try { 
  
            // Create URL object 
            URL url = new URL(webpage); 
            BufferedReader readr =  
              new BufferedReader(new InputStreamReader(url.openStream())); 
  
            // Enter filename in which you want to download 
            BufferedWriter writer =  
              new BufferedWriter(new FileWriter(fpOutput)); 
              
            // read each line from stream till end 
            String line; 
            while ((line = readr.readLine()) != null) { 
                writer.write(line); 
            } 
  
            readr.close(); 
            writer.close(); 
            System.out.println("Successfully Downloaded."); 
            result=true;
        } 
  
        // Exceptions 
        catch (MalformedURLException mue) { 
            System.out.println("Malformed URL Exception raised"); 
        } 
        catch (IOException ie) { 
            System.out.println("IOException raised"); 
        } 
        return result;
    } 
//    public static void mainDownDirectly(String args[]) 
//        throws IOException 
//    { 
//    	String inputUrl = "https://api.github.com/repos/jasonrudolph/keyboard";
//		String fopOutput="/Users/hungphan/Desktop/sampleProjectInfo/";
//		new File(fopOutput).mkdir();
//		for(int i=0;i<=10000;i++) {
//			String fpOutput=fopOutput+(i+1)+".txt";
//			boolean check=false;
//			int index=0;
//			while((!check) && index<=10) {
//				check=DownloadWebPage(inputUrl,fpOutput);
//				if(!check) {
//					index++;
//					try {
//						Thread.sleep(30000);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			}
//			System.out.println("time "+(i+1)+" "+check);
//			 
//		}
//        
//    } 

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String urlAPI="https://api.github.com/repos/";
		String fopInputProjectFolder = "G:\\gitAlon18Projects\\raw\\java-large\\validation\\";
		String fopOutputProjects="G:\\gitAlon18Projects\\githubStarPredictProjects\\projectInfos\\";
		String fopOutputLogs="G:\\gitAlon18Projects\\githubStarPredictProjects\\";
		String fpAllRepoInfos="G:\\gitAlon18Projects\\githubStarPredictProjects\\repos.txt";
		
		new File(fopOutputProjects).mkdir();
		
		StringBuilder sbResult=new StringBuilder();
		File[] arrFolders=new File(fopInputProjectFolder).listFiles();
		for(int i=0;i<arrFolders.length;i++) {
			if(arrFolders[i].isDirectory()) {
				String[] arrItems=arrFolders[i].getName().split("__");
				if(arrItems.length>=2) {
					String ownName=arrItems[0];
					String projName=arrItems[1];
					sbResult.append(ownName+"\t"+projName+"\n");
				}
			}
		}
		FileIO.writeStringToFile(sbResult.toString(), fpAllRepoInfos);
		
		if(!new File(fopOutputLogs + "downloaded" 
						+ ".txt").exists()) {
			FileIO.writeStringToFile("", fopOutputLogs + "downloaded" 
						+ ".txt");
		}
		
		if(!new File(fopOutputLogs + "unable" 
				+ ".txt").exists()) {
	FileIO.writeStringToFile("", fopOutputLogs + "unable" 
				+ ".txt");
}

//		File destinationFile = new File(fopOutput);
		final GithubClient gitClient = new GithubClient();
		ExecutorService pool = Executors.newFixedThreadPool(8);
//
		String listContent = FileIO
				.readStringFromFile(fpAllRepoInfos);
		Scanner sc = new Scanner(listContent);
		int index = 0;
		String[] arrProjectName = FileIO.readStringFromFile(
				fopOutputLogs + "downloaded" 
						+ ".txt").split("\n");
		HashSet<String> setExistProjects = new HashSet<String>();
		for (int i = 0; i < arrProjectName.length; i++) {
			setExistProjects.add(arrProjectName[i].trim());
		}
		File fUnable = new File(fopOutputLogs
				+ "unable.txt");
		if (!fUnable.exists()) {
			FileIO.writeStringToFile("", fUnable.getAbsolutePath());
		} else {
			String[] arrUnable = FileIO.readStringFromFile(
					fopOutputLogs + "unable.txt"
							).split("\n");
			for (int i = 0; i < arrUnable.length; i++) {
				setExistProjects.add(arrUnable[i].trim().split("\t")[0].trim());
			}
		}

		while (sc.hasNextLine()) {
			//String projectContent = sc.nextLine().split(",")[0];
			String[] arrContent = sc.nextLine().split("\t");
			final String username = arrContent[0].trim();
			final String repos = arrContent[1].trim();
			String keyCheck = username + "__" + repos;
			index++;
			if (!setExistProjects.contains(keyCheck)) {
//				File currentFile = new File(PathConstanct.fop2ndProjectLocation
//						+ File.separator + username + "_" + repos + "/");
//				currentFile.delete();
				pool.execute(new Runnable() {
					@Override
					public void run() {

						while (true) {
							boolean gotIt = false;
							try {
								System.out.println("begin download " + username
										+ "__" + repos);

								String urlGithubRepo=urlAPI+ username+"/"+repos;
								String destinationJsonFile=fopOutputProjects+keyCheck+".txt";
								// gotIt =
								// gitClient.downloadRepoContent(GithubConfig.urlGithub+username+"/"+repos+".git",
								// GithubConfig.accessTokens,
								// "master",
								// PathConstanct.fopProjectLocation+File.separator+username+"_"+repos+"/");
								gotIt=gitClient
										.downloadJsonDataOfProject(
												urlGithubRepo,destinationJsonFile
												);
//								if (!branch.isEmpty()) {
//									System.out.println("begin download branch "
//											+ branch + " of repo" + username
//											+ "-" + repos);
//									gotIt = gitClient
//											.downloadRepoContentByArchiveDownload(
//													GithubConfig.urlGithub
//															+ username + "/"
//															+ repos
//															+ "/archive/"
//															+ branch + ".zip",
//													GithubConfig.accessTokens,
//													PathConstanct.fop2ndProjectLocation
//															+ File.separator
//															+ username
//															+ "_"
//															+ repos + ".zip");
//								}

							}
							catch (FileNotFoundException ex) {
								ex.printStackTrace();
								System.out.println("exception here");
								FileIO.appendStringToFile(
										username
												+ "_"
												+ repos
												+ "\t"
												+ ex.getMessage()
												+ "\tFileNotFoundException\n",
												fopOutputLogs
												+ "unable.txt");
								break;
							}
							catch (IOException ex) {
								ex.printStackTrace();
//								FileIO.appendStringToFile(username + "_"
//										+ repos + "\t" + ex.getMessage()
//										+ "\tjava.io.IOException\n",
//										fopOutputLogs
//												+ "unable.txt");
//								break;
							}

							catch (org.eclipse.jgit.api.errors.TransportException ex) {
								ex.printStackTrace();
								System.out.println("exception here");
								FileIO.appendStringToFile(
										username
												+ "_"
												+ repos
												+ "\t"
												+ ex.getMessage()
												+ "\torg.eclipse.jgit.api.errors.TransportException\n",
												fopOutputLogs
												+ "unable.txt");
								break;
							}
							
							 catch (Exception ex) {
								ex.printStackTrace();
							}
							if (gotIt) {
								System.out.println(" project " + username + "-_"
										+ repos + " downloaded");
								FileIO.appendStringToFile(username + "__"
										+ repos + "\n",
										fopOutputLogs
												+ "downloaded.txt");
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
						// System.out.println("project index " +index);

					}
				});
			}

		}

		pool.shutdown();
	}
	
	public static boolean copy(InputStream input, OutputStream output, int bufferSize) {
		byte[] buf = new byte[bufferSize];
		boolean result = false;
		try {
			int n = input.read(buf);
			while (n >= 0) {
				output.write(buf, 0, n);
				n = input.read(buf);
			}
			output.flush();
			result = true;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return result;
	}

}
