package download;

import java.io.File;
import java.util.ArrayList;

import utils.FileIO;
import utils.FileUtil;
import consts.PathConstanct;

public class GenerateAssumeTrainData {

	public static void getAssumeSourceData(String fpOrgSource,String fpOrgTarget,String fpNewSource,String fpNewTarget){
		FileIO.copyFileReplaceExist(fpOrgTarget,fpNewTarget);
		
		ArrayList<String> lstOrgSources=FileUtil.getFileStringArray(fpOrgSource);
		ArrayList<String> lstOrgTargets=FileUtil.getFileStringArray(fpOrgTarget);
		
		int numLineBuffer=200000;
		
		FileIO.writeStringToFile("", fpNewSource);
		StringBuilder sb=new StringBuilder();
		
		for(int i=0;i<lstOrgSources.size();i++){
			String[] arrItemSource=lstOrgSources.get(i).trim().split("\\s+");
			String[] arrItemTarget=lstOrgTargets.get(i).trim().split("\\s+");
			
			for(int j=0;j<arrItemSource.length;j++){
				if(arrItemSource[j].endsWith("#identifier")){
					sb.append(arrItemTarget[j]);
				}else{
					sb.append(arrItemSource[j]);
				}
				
				if(j!=arrItemSource.length-1){
					sb.append(" ");
				}
			}
			
			if((i+1)%numLineBuffer==0){
				FileIO.appendStringToFile(sb.toString().trim()+"\n", fpNewTarget);
				sb=new StringBuilder();
			}
			
		}
		
		String str=sb.toString().trim();
		if(!str.isEmpty()){
			FileIO.appendStringToFile(str+"\n", fpNewTarget);
		}
		
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fopInput=PathConstanct.PATH_PROJECT_TTT_CUR_EVAL_DATA+"originData"+File.separator;
		String fopOutput=PathConstanct.PATH_PROJECT_TTT_CUR_EVAL_DATA+"realTrain";
		
		String fn_trainSource="train.s";
		String fn_trainTarget="train.t";
		String fn_testSource="test.s";
		String fn_testTarget="test.t";
		String fn_tuneSource="tune.s";
		String fn_tuneTarget="tune.t";
		
		new File(fopOutput).mkdir();
		
		getAssumeSourceData(fopInput+fn_trainSource, fopInput+fn_trainTarget, fopOutput+fn_trainSource, fopOutput+fn_trainTarget);
		getAssumeSourceData(fopInput+fn_tuneSource, fopInput+fn_tuneTarget, fopOutput+fn_tuneSource, fopOutput+fn_tuneTarget);
		getAssumeSourceData(fopInput+fn_testSource, fopInput+fn_testTarget, fopOutput+fn_testSource, fopOutput+fn_testTarget);
		
		ReGenerateAlignment.generateTotalAlignment(fopOutput, fopInput+fn_trainSource, fopInput+fn_trainTarget,  fopOutput 
				+ "training.s-t.A3", fopOutput 
				+ "training.t-s.A3", true);
		
		
	}

}
