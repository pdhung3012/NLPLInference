package invocations;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;

import consts.PathConstanct;
import download.ReGenerateAlignment;
import utils.FileUtil;

public class FilterSmallSampleOfData {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		//

		String fop_root = PathConstanct.PATH_PROJECT_TTT_CUR_EVAL_DATA + File.separator;
		;
		String fop_input = fop_root + "full" + File.separator;
		String fop_newTrain = fop_root + "small" + File.separator;
		new File(fop_newTrain).mkdir();

		int maxTrain=1000;
		int maxTune=10;
		int maxTest=10;
		String fp_tuneLocation = fop_input + "tune.locations.txt";

//		String fp_trainLocation = fop_input + "train.locations.txt";
		String fp_trainSource = fop_input + "train.s";
		String fp_trainTarget = fop_input + "train.t";
		String fp_tuneSource = fop_input + "tune.s";
		String fp_tuneTarget = fop_input + "tune.t";
		String fp_testSource = fop_input + "test.s";
		String fp_testTarget = fop_input + "test.t";
		
		String fp_newTrainSource = fop_newTrain + "train.s";
		String fp_newTrainTarget = fop_newTrain + "train.t";
		String fp_newTuneSource = fop_newTrain + "tune.s";
		String fp_newTuneTarget = fop_newTrain + "tune.t";
		String fp_newTestSource = fop_newTrain + "test.s";
		String fp_newTestTarget = fop_newTrain + "test.t";
		
//		String fp_newTrainLoc = fop_newTrain + "train.locations.txt";
		String fp_newTrainST = fop_newTrain + "training.s-t.A3";
		String fp_newTrainTS = fop_newTrain + "training.t-s.A3";

		ArrayList<String> arrTrainSource = FileUtil.getFileStringArray(fp_trainSource);
		ArrayList<String> arrTrainTarget = FileUtil.getFileStringArray(fp_trainTarget);
		
		
		PrintStream ptTrainSource = null, ptTrainTarget = null;
		PrintStream ptTestSource = null, ptTestTarget = null;
		PrintStream ptTuneSource = null, ptTuneTarget = null;
		try {
			ptTrainSource = new PrintStream(new FileOutputStream(fp_newTrainSource));
			ptTrainTarget = new PrintStream(new FileOutputStream(fp_newTrainTarget));
			ptTestSource =  new PrintStream(new FileOutputStream(fp_newTestSource)); 
			ptTestTarget =   new PrintStream(new FileOutputStream(fp_newTestTarget)); 
			ptTuneSource =  new PrintStream(new FileOutputStream(fp_newTuneSource)); 
			ptTuneTarget =   new PrintStream(new FileOutputStream(fp_newTuneTarget)); 


		} catch (Exception ex) {
			ex.printStackTrace();
		}
//		System.out.println("tune " + setTuneLocs.size() + " old train " + arrTrainLocation.size() + " "
//				+ (arrTrainLocation.size() - setTuneLocs.size()));
		for (int i = 0; i < maxTrain; i++) {
			ptTrainSource.print(arrTrainSource.get(i) + "\n");
			ptTrainTarget.print(arrTrainTarget.get(i) + "\n");
			
		}

		try {
			ptTrainSource.close();
			ptTrainTarget.close();
		} catch (Exception ex) {

		}
		
		ArrayList<String> arrTuneSource = FileUtil.getFileStringArray(fp_tuneSource);
		ArrayList<String> arrTuneTarget = FileUtil.getFileStringArray(fp_tuneTarget);
		
		for (int i = 0; i < maxTune; i++) {
			ptTuneSource.print(arrTuneSource.get(i) + "\n");
			ptTuneTarget.print(arrTuneTarget.get(i) + "\n");
			
		}

		try {
			ptTuneSource.close();
			ptTuneTarget.close();
		} catch (Exception ex) {

		}
		
		ArrayList<String> arrTestSource = FileUtil.getFileStringArray(fp_testSource);
		ArrayList<String> arrTestTarget = FileUtil.getFileStringArray(fp_testTarget);
		
		for (int i = 0; i < maxTune; i++) {
			ptTestSource.print(arrTestSource.get(i) + "\n");
			ptTestTarget.print(arrTestTarget.get(i) + "\n");
			
		}

		try {
			ptTestSource.close();
			ptTestTarget.close();
		} catch (Exception ex) {

		}

		ReGenerateAlignment.generateTotalAlignment(fop_newTrain, fp_newTrainSource, fp_newTrainTarget, fp_newTrainST,
				fp_newTrainTS, false);
	}

}
