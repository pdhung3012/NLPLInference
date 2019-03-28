package invocations;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;

import utils.FileUtil;
import consts.PathConstanct;

public class UpdateTuneData {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fop_input=PathConstanct.PATH_PROJECT_TTT_CUR_EVAL_DATA+File.separator;
		String fop_newTrain=fop_input+"newTrain"+File.separator;
		new File(fop_newTrain).mkdir();
		
		String fp_tuneLocation=fop_input+"tune.locations.txt";
		
		String fp_trainLocation=fop_input+"train.locations.txt";
		String fp_trainSource=fop_input+"train.s";
		String fp_trainTarget=fop_input+"train.t";
		String fp_newTrainSource=fop_newTrain+"train.s";
		String fp_newTrainTarget=fop_newTrain+"train.t";
		String fp_newTrainLoc=fop_newTrain+"train.locations.txt";
		
		ArrayList<String> arrTrainLocation=FileUtil.getFileStringArray(fp_trainLocation);
		ArrayList<String> arrTrainSource=FileUtil.getFileStringArray(fp_trainSource);
		ArrayList<String> arrTrainTarget=FileUtil.getFileStringArray(fp_trainTarget);
		ArrayList<String> arrTuneLocation=FileUtil.getFileStringArray(fp_tuneLocation);
		
		HashSet<String> setTuneLocs=new LinkedHashSet<>();
		for(int i=0;i<arrTuneLocation.size();i++){
			setTuneLocs.add(arrTuneLocation.get(i));
		}
		
		PrintStream ptTrainSource=null,ptTrainTarget=null,ptTrainLoc=null;
		try{
			ptTrainSource=new PrintStream(new FileOutputStream(fp_newTrainSource));
			ptTrainTarget=new PrintStream(new FileOutputStream(fp_newTrainTarget));
			ptTrainLoc=new PrintStream(new FileOutputStream(fp_newTrainLoc));
			
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		System.out.println("tune "+setTuneLocs.size()+" old train "+arrTrainLocation.size()+" "+(arrTrainLocation.size()-setTuneLocs.size()));
		for(int i=0;i<arrTrainLocation.size();i++){
			if(!setTuneLocs.contains(arrTrainLocation.get(i))){
				ptTrainLoc.print(arrTrainLocation.get(i));
				ptTrainSource.print(arrTrainSource.get(i));
				ptTrainTarget.print(arrTrainTarget.get(i));
			}
		}
		
		try{
			ptTrainLoc.close();
			ptTrainSource.close();
			ptTrainTarget.close();
		}catch(Exception ex){
			
		}
	}

}
