package invocations;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import utils.FileIO;
import utils.FileUtil;
import consts.PathConstanct;

public class RunOnFixProjectSet {

	private static final int MYTHREADS = 4;

	public static String[] arrLibraryPrefix={"android","com.google.gwt","com.thoughtworks.xstream","org.hibernate","org.joda.time","java"};

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fpProjectList=PathConstanct.PATH_PROJECT_TRAIN_TEST_NAME+"requiredProjects.txt";
		String inputProjectPath = PathConstanct.PATH_INPUT_IDENTIFER_PROJECT;
		String outputProjectPath = PathConstanct.PATH_OUTPUT_IDENTIFER_PROJECT;
		String fpOutputLog=outputProjectPath+File.separator+"alog.txt";
		ExecutorService executor = Executors.newFixedThreadPool(MYTHREADS);
		
		File fInput = new File(inputProjectPath);
		File fOutput = new File(outputProjectPath);
		
		if(!fOutput.isDirectory()){
			fOutput.mkdir();
		}
		if(!new File(fpOutputLog).isFile()){
			FileUtil.writeToFile(fpOutputLog, "");
		}
		

		
		String[] arrProjectName = FileIO.readStringFromFile(fpProjectList).split("\n");

		for (int i = 0; i < arrProjectName.length; i++) {
			File fopItemProject=new File(inputProjectPath+File.separator+arrProjectName[i]+File.separator);
			if (fopItemProject.isDirectory()) {
				String itemInputPath = fopItemProject.getAbsolutePath()
						+ File.separator;
				String itemOutputPath = outputProjectPath + File.separator
						+ arrProjectName[i] + File.separator;
				ExtractSequenceForProjectRunnable thread = new ExtractSequenceForProjectRunnable(itemInputPath,
						itemOutputPath, arrLibraryPrefix,i,fpOutputLog);
//				thread.run();
				executor.execute(thread);
			}
		}
		
		executor.shutdown();

	}

}

