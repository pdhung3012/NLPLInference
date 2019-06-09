package download;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import utils.FileIO;
import utils.FileUtil;
import utils.MapUtil;
import consts.PathConstanct;

public class GenerateTrainRemoveVarAndTerm {
	

public static String SplitInvocationCharacter="\\$\\%\\$";
	
	public static boolean checkAPIsInLibrary(HashSet<String> setLib,String token){
		boolean check=false;
		for(String str:setLib){
			if(token.startsWith(str)){
				//System.out.println(token);
				check=true;
				break;
			}
		}
		return check;
	}
	public static HashMap<String,String> getLibraryInfo(HashMap<String,String> mapTotalId){
		HashMap<String,String> map=new LinkedHashMap<String, String>();
		for(String key:mapTotalId.keySet()){
			String val=mapTotalId.get(key);
			String fqn=getInvocationReceiverInLibrary(val);
			map.put(key, fqn);
		}
		return map;
	}
	
	public static HashMap<String,String> getVarInfo(HashMap<String,String> mapTotalId){
		HashMap<String,String> map=new LinkedHashMap<String, String>();
		for(String key:mapTotalId.keySet()){
			String val=mapTotalId.get(key);
			String fqn=getInvocationVarInLibrary(val);
			map.put(key, fqn);
		}
		return map;
	}
	
	
	
	public static String getInvocationReceiverInLibrary(String info){
		String result="";
		String[] arrLine=info.split(SplitInvocationCharacter);
		//System.out.println(arrLine[0]);
		if(arrLine.length>4){
			String sigInfo=arrLine[arrLine.length-4];
			
			String[] arrSigs=sigInfo.split("#");
			if(arrSigs.length>=2){
				result=arrSigs[1];
			}
		}
		
		return result;
	}
	
	public static String getInvocationVarInLibrary(String info){
		String result="";
		String[] arrLine=info.split(SplitInvocationCharacter);
		//System.out.println(arrLine[0]);
		if(arrLine.length>4){
			String varInfo=arrLine[1];
			result=varInfo;
			
		}
		
		return result;
	}
	
	public static void refineSourceTarget(String fpTermSource,String fpTermTarget,String fpNewSource,String fpNewTarget,HashMap<String,String> mapVar){
		String[] arrSource=FileIO.readStringFromFile(fpTermSource).split("\n");
		String[] arrTarget=FileIO.readStringFromFile(fpTermTarget).split("\n");
		StringBuilder strNewSource=new StringBuilder(),strNewTarget=new StringBuilder();
		for(int i=0;i<arrSource.length;i++){
			String[] arrItS=arrSource[i].split("\\s+");
			String[] arrItT=arrTarget[i].split("\\s+");
			String strLineS="",strLineT="";
			LinkedHashSet<Integer> setRemove=new LinkedHashSet<Integer>();
			for(int j=0;j<arrItS.length;j++){
				if(arrItS[j].endsWith("#identifier")){
//					String targetID=arrItT[j];
					int start=j-1;
					int stopStartIndex=0;
					String strLstVar=mapVar.get(arrItT[j]);
					if(!strLstVar.isEmpty()){
						stopStartIndex=strLstVar.split("#").length;
						//System.out.println(strLstVar+" "+stopStartIndex);
					}
					for(int q=0;q<stopStartIndex;q++){
						start--;
					}
					if(start<-1) {
						start=j-1;
					}
					
					int end =j+1;
					while(end<arrItS.length && arrItS[end].endsWith("#term")){
						end++;
					}
					//System.out.println(arrTarget[i]);
					for(int k=start+1;k<end;k++){
//						strLineS+=arrItS[k]+" ";
//						strLineT+=arrItT[k]+" ";
						if(j!=k){
							setRemove.add(k);
						}
					}
				}
			}
			for(int j=0;j<arrItS.length;j++){
				if(!setRemove.contains(j)){
					strLineS+=arrItS[j]+" ";
					strLineT+=arrItT[j]+" ";
				}
			}
			strNewSource.append(strLineS+"\n");
			strNewTarget.append(strLineT+"\n");
		}
		FileIO.writeStringToFile(strNewSource.toString()+"\n", fpNewSource);
		FileIO.writeStringToFile(strNewTarget.toString()+"\n", fpNewTarget);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fopInput=PathConstanct.PATH_PROJECT_TTT_CUR_EVAL_DATA+"originData"+File.separator;
		String fopOutput=PathConstanct.PATH_PROJECT_TTT_CUR_EVAL_DATA+File.separator;	
		String fop_mapTotalId=PathConstanct.PATH_PROJECT_TTT_CUR_EVAL_DATA+File.separator+"map"+File.separator;
		HashMap<String,String> mapTotalId=MapUtil.getHashMapFromFile(fop_mapTotalId+"a_mapTotalIdAndContent.txt");
		HashMap<String,String> mapIdVars=getVarInfo(mapTotalId);
		
		String fn_trainSource="train.s";
		String fn_trainTarget="train.t";
		String fn_testSource="test.s";
		String fn_testTarget="test.t";
		String fn_tuneSource="tune.s";
		String fn_tuneTarget="tune.t";
		new File(fopOutput).mkdir();
		
		
		refineSourceTarget(fopInput+fn_testSource,fopInput+fn_testTarget,fopOutput+fn_testSource,fopOutput+fn_testTarget,mapIdVars);
		//refineSourceTarget(fopInput+fn_tuneSource,fopInput+fn_tuneTarget,fopOutput+fn_tuneSource,fopOutput+fn_tuneTarget,mapIdVars);
		//refineSourceTarget(fopInput+fn_trainSource,fopInput+fn_trainTarget,fopOutput+fn_trainSource,fopOutput+fn_trainTarget,mapIdVars);
		/*
		ReGenerateAlignment.generateTotalAlignment(fopOutput, fopOutput+fn_trainSource, fopOutput+fn_trainTarget,  fopOutput 
				+ "training.s-t.A3", fopOutput 
				+ "training.t-s.A3", true);
		
		*/
	}

}
