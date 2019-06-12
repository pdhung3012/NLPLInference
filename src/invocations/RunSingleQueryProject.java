package invocations;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import consts.PathConstanct;
import utils.FileIO;
import utils.FileUtil;
import utils.StanfordLemmatizer;

public class RunSingleQueryProject {
	private static final int MYTHREADS = 10;
	public static String[] arrLibraryPrefix={"android","com.google.gwt","com.thoughtworks.xstream","org.hibernate","org.joda.time","java"};
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String inputProjectPath = PathConstanct.PATH_PROJECT_TTT_QUERY_INPUT_PROJECT;
		String outputProjectPath = PathConstanct.PATH_PROJECT_TTT_QUERY_IDENTIFIER_PROJECT;
		String fpOutputLog=outputProjectPath+File.separator+"alog.txt";
		ExecutorService executor = Executors.newFixedThreadPool(MYTHREADS);
		StanfordLemmatizer lemm=new StanfordLemmatizer();
		if(!new File(fpOutputLog).isFile()){
			FileUtil.writeToFile(fpOutputLog, "");
		}
		

		File fInput = new File(inputProjectPath);
//		File fOutput = new File(outputProjectPath);
		File[] arrFilesInput = fInput.listFiles();

		for (int i = 0; i < arrFilesInput.length; i++) {
			if (arrFilesInput[i].isDirectory()) {
				String itemInputPath = arrFilesInput[i].getAbsolutePath()
						+ File.separator;
				String itemOutputPath = outputProjectPath + File.separator
						+ arrFilesInput[i].getName() + File.separator;
				ExtractSourceQueryRunnable thread = new ExtractSourceQueryRunnable(itemInputPath,
						itemOutputPath, arrLibraryPrefix,i,fpOutputLog,lemm);
//				thread.run();
				executor.execute(thread);
			}
		}
	}
	
	

}

class ExtractSourceQueryRunnable implements Runnable {
	private String inputPath = "", outputPath = "";
	private int index = 0;
	private String logPath="";
	private String[] arrLibNames;
	private StanfordLemmatizer lemm;

	ExtractSourceQueryRunnable(String inputPath, String outputPath, String[] arrLibName, int index,String logPath,StanfordLemmatizer lemm) {
		this.inputPath = inputPath;
		this.outputPath = outputPath;
		this.arrLibNames=arrLibName;
		this.index = index;
		this.logPath=logPath;
		this.lemm=lemm;
	}

	@Override
	public void run() {
		File fIn=new File(inputPath);
		try {
			
//			File fOut=new File(outputPath);
//			File fSourceOut=new File(outputPath+"source.txt");
			System.out.println(index+"\tFinish success for " + outputPath);
			MethodSourceTokenGenerator mcsg = new MethodSourceTokenGenerator(
					inputPath,arrLibNames,lemm);
			mcsg.generateSequences(outputPath);
			
//			FileIO.appendStringToFile(index+"\t"+fIn.getName()+"\tSuccess\n", logPath);
			
		} catch (Exception ex) {
			System.out.println((index+1)+"\tFinish failed for " + outputPath);
			ex.printStackTrace();
			FileIO.appendStringToFile((index+1)+"\t"+fIn.getName()+"\tFailed\n", logPath);
		}

	}
}
