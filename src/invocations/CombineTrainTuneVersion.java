package invocations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.ArrayList;

import consts.PathConstanct;
import download.ReGenerateAlignment;
import utils.FileIO;
import utils.FileUtil;

public class CombineTrainTuneVersion {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fopInput=PathConstanct.PATH_PROJECT_TTT_CUR_EVAL_DATA;
		String fopTrainTuneRaw=fopInput+"raw"+File.separator;
		String fopTrainTuneCombine=fopInput+"context"+File.separator;
		String fopTrainTuneOutput=fopInput+"outputCombine"+File.separator;
	
		
		
		PrintStream psNewTrainSource = null, psNewTrainTarget = null,psNewTuneSource = null, psNewTuneTarget = null;
		try {
			psNewTrainSource = new PrintStream(new FileOutputStream(fopTrainTuneOutput +"train.s"));
			psNewTrainTarget = new PrintStream(new FileOutputStream(fopTrainTuneOutput +"train.t"));
			psNewTuneSource = new PrintStream(new FileOutputStream(fopTrainTuneOutput +"tune.s"));
			psNewTuneTarget = new PrintStream(new FileOutputStream(fopTrainTuneOutput +"tune.t"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			if (psNewTrainSource != null)
				psNewTrainSource.close();
			if (psNewTrainTarget != null)
				psNewTrainTarget.close();
			if (psNewTuneSource != null)
				psNewTuneSource.close();
			if (psNewTuneTarget != null)
				psNewTuneTarget.close();

			e.printStackTrace();
		}
		
		
		ArrayList<String>  arrRawTrainSource=FileUtil.getFileStringArray(fopTrainTuneRaw +"train.s");
		ArrayList<String>  arrCombineTrainSource=FileUtil.getFileStringArray(fopTrainTuneCombine+"train.s");
		for(int i=0;i<arrRawTrainSource.size();i++) {
			psNewTrainSource.println(arrRawTrainSource.get(i));
			psNewTrainSource.println(arrCombineTrainSource.get(i));
		}
		
		ArrayList<String>  arrRawTrainTarget=FileUtil.getFileStringArray(fopTrainTuneRaw+"train.t");
		ArrayList<String>  arrCombineTrainTarget=FileUtil.getFileStringArray(fopTrainTuneCombine+"train.t");
		for(int i=0;i<arrRawTrainTarget.size();i++) {
			psNewTrainTarget.println(arrRawTrainTarget.get(i));
			psNewTrainTarget.println(arrCombineTrainTarget.get(i));
		}
		
		String[] arrRawTuneSource=FileIO.readStringFromFile(fopTrainTuneRaw +"tune.s").trim().split("\n");
		String[] arrRawTuneTarget=FileIO.readStringFromFile(fopTrainTuneRaw+"tune.t").trim().split("\n");
		String[] arrCombineTuneSource=FileIO.readStringFromFile(fopTrainTuneCombine+"tune.s").trim().split("\n");
		String[] arrCombineTuneTarget=FileIO.readStringFromFile(fopTrainTuneCombine+"tune.t").trim().split("\n");
		System.out.println("ok");
		
		for(int i=0;i<arrRawTuneSource.length;i++) {
			psNewTuneSource.println(arrRawTuneSource[i]);
			psNewTuneSource.println(arrCombineTuneSource[i]);
			psNewTuneTarget.println(arrRawTuneTarget[i]);
			psNewTuneTarget.println(arrCombineTuneTarget[i]);
		}
		
		try {
			psNewTrainSource.close();
			psNewTrainTarget.close();
			psNewTuneSource.close();
			psNewTuneTarget.close();
		} catch (Exception ex) {

		}
		
		ReGenerateAlignment.generateTotalAlignment(fopTrainTuneOutput, fopTrainTuneOutput+"train.s", fopTrainTuneOutput+"train.t", fopTrainTuneOutput+"training.s-t.A3",
				 fopTrainTuneOutput+"training.t-s.A3", false);
		
	}

}
