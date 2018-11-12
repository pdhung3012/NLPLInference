package parser;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import utils.FileIO;
import utils.GithubClient;
import utils.ZipUtil;
import consts.GithubConfig;
import consts.PathConstanct;
import net.lingala.zip4j.exception.ZipException;

public class ExtractProjectGithub {

//	 "android.",
//	"com.google.gwt.", "org.hibernate.", "org.joda.time.",
//	"com.thoughtworks.xstream."
	final static String[] keywords = new String[] {"android.",
		"com.google.gwt.", "org.hibernate.", "org.joda.time.",
		"com.thoughtworks.xstream."
			 };
	
	public static void main(String[] args) {
		extractGithubProjects();
	}

	public static void extractGithubProjects() {
		final GithubClient gitClient = new GithubClient();
		ExecutorService pool = Executors.newFixedThreadPool(8);
		for (String keyword : keywords) {
			String listContent = FileIO
					.readStringFromFile(PathConstanct.fopListLibraryLocation+File.separator+"repos-5stars-50commits-lib-"+keyword+".csv");
			Scanner sc = new Scanner(listContent);
			int index = 0;
			File fExtractedList=new File(PathConstanct.fopListLibraryLocation+"extracted-"+keyword+".txt");
			if(!fExtractedList.exists()){
				FileIO.writeStringToFile("", fExtractedList.getAbsolutePath());
				
			}
			String[] arrProjectName=FileIO.readStringFromFile(fExtractedList.getAbsolutePath()).split("\n");
			HashSet<String> setExistProjects=new HashSet<String>();
			for(int i=0;i<arrProjectName.length;i++) {
				setExistProjects.add(arrProjectName[i].trim());
			}
			
			File fUnable=new File(PathConstanct.fopListLibraryLocation+"unable-"+keyword+".txt");
			if(!fUnable.exists()){
				FileIO.writeStringToFile("", fUnable.getAbsolutePath());
			} else{
				String[] arrUnable=FileIO.readStringFromFile(fUnable.getAbsolutePath()).split("\n");
				for(int i=0;i<arrUnable.length;i++) {
					setExistProjects.add(arrUnable[i].trim().split("\t")[0].trim());
				}
			}
			
			File fNotExtracted=new File(PathConstanct.fopListLibraryLocation+"notExtracted-"+keyword+".txt");
			if(!fNotExtracted.exists()){
				FileIO.writeStringToFile("", fNotExtracted.getAbsolutePath());
			} 
			/*
			else{
				String[] arrUnable=FileIO.readStringFromFile(fNotExtracted.getAbsolutePath()).split("\n");
				for(int i=0;i<arrUnable.length;i++) {
					setExistProjects.add(arrUnable[i].trim().split("\t")[0].trim());
				}
			}
			*/
			
			while (sc.hasNextLine()) {
				String projectContent = sc.nextLine().split(",")[0];
				String[] arrContent = projectContent.split("/");
				final String username = arrContent[0];
				final String repos = arrContent[1];
				final String keyw=keyword;
				String keyCheck=username+"_"+repos;
				
				final File zipFile=new File(PathConstanct.fopProjectLocation+File.separator+username+"_" +repos+".zip");
				
				if(!setExistProjects.contains(keyCheck) &&zipFile.exists() && index<=300){	
					index++;
					File currentFile = new File(PathConstanct.fopProjectLocation+File.separator+username+"_"+repos+"/");
				    currentFile.delete();
					pool.execute(new Runnable() {
						@Override
						public void run() {

							while (true) {
								boolean gotIt = false;
								try {
									System.out.println("begin extracted "+username+"-" +repos);
									File fNewLocation=new File(PathConstanct.fopExtractedLocation+File.separator+username+"-" +repos+File.separator);
									/*if(!fNewLocation.isDirectory()){
										fNewLocation.mkdir();
									}*/
									System.out.println( fNewLocation.getAbsolutePath());
									gotIt = ZipUtil.extractZipFileToFolder(zipFile.getAbsolutePath(), fNewLocation.getAbsolutePath());
									
									
								}
								catch (ZipException ex) {
									ex.printStackTrace();
									FileIO.appendStringToFile(username+"_"+repos+"\t"+ex.getMessage()+"\tnet.lingala.zip4j.exception.ZipException\n",PathConstanct.fopListLibraryLocation+"notExtracted-"+keyw+".txt");
									break;
								}
								catch (Exception ex) {
									ex.printStackTrace();
								}
								if (gotIt){
									System.out.println(" project "+username+"-" +repos + " extracted");
									
									FileIO.appendStringToFile(username+"_"+repos+"\n",PathConstanct.fopListLibraryLocation+"extracted-"+keyw+".txt");
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
	}


}
