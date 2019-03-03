package invocations;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import utils.FileIO;
import utils.FileUtil;
import consts.PathConstanct;

public class RunOnLargeScaleData {

	private static final int MYTHREADS = 10;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String inputProjectPath = PathConstanct.PATH_INPUT_IDENTIFER_PROJECT;
		String outputProjectPath = PathConstanct.PATH_OUTPUT_IDENTIFER_PROJECT;
		String fpOutputLog=outputProjectPath+File.separator+"alog.txt";
		ExecutorService executor = Executors.newFixedThreadPool(MYTHREADS);
		
		if(!new File(fpOutputLog).isFile()){
			FileUtil.writeToFile(fpOutputLog, "");
		}
		

		File fInput = new File(inputProjectPath);
		File fOutput = new File(outputProjectPath);
		File[] arrFilesInput = fInput.listFiles();

		for (int i = 0; i < arrFilesInput.length; i++) {
			if (arrFilesInput[i].isDirectory()) {
				String itemInputPath = arrFilesInput[i].getAbsolutePath()
						+ File.separator;
				String itemOutputPath = outputProjectPath + File.separator
						+ arrFilesInput[i].getName() + File.separator;
				ExtractSequenceForProjectRunnable thread = new ExtractSequenceForProjectRunnable(itemInputPath,
						itemOutputPath, i,fpOutputLog);
//				thread.run();
				executor.execute(thread);
			}
		}

	}
}

class ExtractSequenceForProjectRunnable implements Runnable {
	private String inputPath = "", outputPath = "";
	private int index = 0;
	private String logPath="";

	ExtractSequenceForProjectRunnable(String inputPath, String outputPath, int index,String logPath) {
		this.inputPath = inputPath;
		this.outputPath = outputPath;
		this.index = index;
		this.logPath=logPath;
	}

	@Override
	public void run() {
		File fIn=new File(inputPath);
		try {
			
			File fOut=new File(outputPath);
			File fSourceOut=new File(outputPath+"source.txt");
			if(fSourceOut.isFile() && fSourceOut.length()>100){
				System.out.println(index+"\tAlready for " + outputPath);
				FileIO.appendStringToFile(index+"\t"+fIn.getName()+"\tDownloaded\n", logPath);
			} else{
				MethodContextSequenceGenerator mcsg = new MethodContextSequenceGenerator(
						inputPath);
				mcsg.generateSequences(outputPath);
				mcsg.generateAlignment(true);
				System.out.println(index+"\tFinish success for " + outputPath);
				FileIO.appendStringToFile(index+"\t"+fIn.getName()+"\tSuccess\n", logPath);
			}
			
		} catch (Exception ex) {
			System.out.println((index+1)+"\tFinish failed for " + outputPath);
			ex.printStackTrace();
			FileIO.appendStringToFile((index+1)+"\t"+fIn.getName()+"\tFailed\n", logPath);
		}

	}
}