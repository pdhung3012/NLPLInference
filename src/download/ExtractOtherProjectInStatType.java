package download;

import java.io.File;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.lingala.zip4j.exception.ZipException;
import utils.FileIO;
import utils.GithubClient;
import utils.ZipUtil;
import consts.PathConstanct;

public class ExtractOtherProjectInStatType {

	final static String[] keywords = new String[] { "android.",
			"com.google.gwt.", "org.hibernate.", "org.joda.time.",
			"com.thoughtworks.xstream." };

	public static void main(String[] args) {
		extractGithubProjects();
	}

	public static void extractGithubProjects() {
		final GithubClient gitClient = new GithubClient();
		ExecutorService pool = Executors.newFixedThreadPool(8);

		String listContent = FileIO
				.readStringFromFile(PathConstanct.PATH_PROJECT_TRAIN_TEST_NAME
						+ File.separator + "downloaded-stattype.txt");
		Scanner sc = new Scanner(listContent);
		int index = 0;
		File fExtractedList = new File(
				PathConstanct.PATH_PROJECT_TRAIN_TEST_NAME + "extracted-stattype"
						+ ".txt");
		if (!fExtractedList.exists()) {
			FileIO.writeStringToFile("", fExtractedList.getAbsolutePath());

		}
		String[] arrProjectName = FileIO.readStringFromFile(
				fExtractedList.getAbsolutePath()).split("\n");
		HashSet<String> setExistProjects = new HashSet<String>();
		for (int i = 0; i < arrProjectName.length; i++) {
			setExistProjects.add(arrProjectName[i].trim());
		}

		File fUnable = new File(PathConstanct.PATH_PROJECT_TRAIN_TEST_NAME
				+ "unable-stattype" + ".txt");
		if (!fUnable.exists()) {
			FileIO.writeStringToFile("", fUnable.getAbsolutePath());
		} else {
			String[] arrUnable = FileIO.readStringFromFile(
					fUnable.getAbsolutePath()).split("\n");
			for (int i = 0; i < arrUnable.length; i++) {
				setExistProjects.add(arrUnable[i].trim().split("\t")[0].trim());
			}
		}

		File fNotExtracted = new File(
				PathConstanct.PATH_PROJECT_TRAIN_TEST_NAME + "notExtracted-stattype"
						 + ".txt");
		if (!fNotExtracted.exists()) {
			FileIO.writeStringToFile("", fNotExtracted.getAbsolutePath());
		}
		/*
		 * else{ String[]
		 * arrUnable=FileIO.readStringFromFile(fNotExtracted.getAbsolutePath
		 * ()).split("\n"); for(int i=0;i<arrUnable.length;i++) {
		 * setExistProjects.add(arrUnable[i].trim().split("\t")[0].trim()); } }
		 */

		while (sc.hasNextLine()) {
			String keyword = sc.nextLine();
			
			final String keyw = keyword;
			String keyCheck = keyword;

			final File zipFile = new File(PathConstanct.fopProjectLocation
					+ File.separator +keyCheck + ".zip");

			if (!setExistProjects.contains(keyCheck) && zipFile.exists()
					&& index <= 300) {
				index++;
				File currentFile = new File(PathConstanct.fopProjectLocation
						+ File.separator + keyCheck + "/");
				currentFile.delete();
				pool.execute(new Runnable() {
					@Override
					public void run() {

						while (true) {
							boolean gotIt = false;
							try {
								System.out.println("begin extracted "
										+ keyw);
								File fNewLocation = new File(
										PathConstanct.fopExtractedLocation
												+ File.separator + keyw + File.separator);
								/*
								 * if(!fNewLocation.isDirectory()){
								 * fNewLocation.mkdir(); }
								 */
								System.out.println(fNewLocation
										.getAbsolutePath());
								gotIt = ZipUtil.extractZipFileToFolder(
										zipFile.getAbsolutePath(),
										fNewLocation.getAbsolutePath());

							} catch (ZipException ex) {
								ex.printStackTrace();
								FileIO.appendStringToFile(
										keyw
												+ "\t"
												+ ex.getMessage()
												+ "\tnet.lingala.zip4j.exception.ZipException\n",
										PathConstanct.PATH_PROJECT_TRAIN_TEST_NAME
												+ "notExtracted-"
												+ keyw
												+ ".txt");
								break;
							} catch (Exception ex) {
								ex.printStackTrace();
							}
							if (gotIt) {
								System.out.println(" project " + keyw + " extracted");

								FileIO.appendStringToFile(
										keyw + "\n",
										PathConstanct.PATH_PROJECT_TRAIN_TEST_NAME
												+ "extracted-" + keyw + ".txt");
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

}
