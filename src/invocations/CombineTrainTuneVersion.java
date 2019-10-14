package invocations;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import download.ReGenerateAlignment;
import utils.FileIO;

public class CombineTrainTuneVersion {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fopInput="";
		String fopTrainTuneRaw="";
		String fopTrainTuneCombine="";
		String fopTrainTuneOutput="";
		
		String[] arrRawTrainSource=FileIO.readStringFromFile(fopTrainTuneRaw +"train.s").trim().split("\n");
		String[] arrRawTrainTarget=FileIO.readStringFromFile(fopTrainTuneRaw+"train.t").trim().split("\n");
		String[] arrCombineTrainSource=FileIO.readStringFromFile(fopTrainTuneCombine+"train.s").trim().split("\n");
		String[] arrCombineTrainTarget=FileIO.readStringFromFile(fopTrainTuneCombine+"train.t").trim().split("\n");
		
		String[] arrRawTuneSource=FileIO.readStringFromFile(fopTrainTuneRaw +"tune.s").trim().split("\n");
		String[] arrRawTuneTarget=FileIO.readStringFromFile(fopTrainTuneRaw+"tune.t").trim().split("\n");
		String[] arrCombineTuneSource=FileIO.readStringFromFile(fopTrainTuneCombine+"tune.s").trim().split("\n");
		String[] arrCombineTuneTarget=FileIO.readStringFromFile(fopTrainTuneCombine+"tune.t").trim().split("\n");
		
		
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
			return null;
		}
		
		for(int i=0;i<arrRawTrainSource.length;i++) {
			psNewTrainSource.println(arrRawTrainSource[i]);
			psNewTrainSource.println(arrCombineTrainSource[i]);
			psNewTrainTarget.println(arrRawTrainTarget[i]);
			psNewTrainTarget.println(arrCombineTrainTarget[i]);
		}
		
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
