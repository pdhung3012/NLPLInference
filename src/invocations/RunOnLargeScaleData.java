package invocations;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import consts.PathConstanct;

public class RunOnLargeScaleData {

	private static final int MYTHREADS = 4;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String inputProjectPath = PathConstanct.PATH_INPUT_IDENTIFER_PROJECT;
		String outputProjectPath = PathConstanct.PATH_OUTPUT_IDENTIFER_PROJECT;
		ExecutorService executor = Executors.newFixedThreadPool(MYTHREADS);

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
						itemOutputPath, i);
				thread.run();
			}
		}

	}
}

class ExtractSequenceForProjectRunnable implements Runnable {
	private String inputPath = "", outputPath = "";
	private int index = 0;

	ExtractSequenceForProjectRunnable(String inputPath, String outputPath, int index) {
		this.inputPath = inputPath;
		this.outputPath = outputPath;
		this.index = index;
	}

	@Override
	public void run() {

		try {
			MethodContextSequenceGenerator mcsg = new MethodContextSequenceGenerator(
					inputPath);
			mcsg.generateSequences(outputPath);
			mcsg.generateAlignment(true);
			System.out.println("Finish success for " + outputPath);
		} catch (Exception ex) {
			System.out.println("Finish failed for " + outputPath);
			ex.printStackTrace();
		}

	}
}