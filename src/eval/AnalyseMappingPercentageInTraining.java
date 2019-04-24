package eval;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import utils.FileIO;
import utils.FileUtil;
import utils.MapUtil;
import utils.SortUtil;
import consts.PathConstanct;

public class AnalyseMappingPercentageInTraining {

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
			String fqn=getInvocationReceiverInLibrary(val);
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
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fopCurrentOutAnalysis=PathConstanct.PATH_PROJECT_TTT_CUR_EVAL_DATA;
		String fop_mapTotalId=PathConstanct.PATH_PROJECT_TTT_CUR_EVAL_DATA+File.separator+"map"+File.separator;
		HashMap<String,String> mapTotalId=MapUtil.getHashMapFromFile(fop_mapTotalId+"a_mapTotalIdAndContent.txt");
		HashMap<String,String> mapIdVars=getVarInfo(mapTotalId);
		String fn_trainSource="train.s";
		String fn_trainTarget="train.t";
		String fn_anaMapParamResult="analyseMappingParamResult.txt";
		
		ArrayList<String> arrTrainSource=FileUtil.getFileStringArray(fopCurrentOutAnalysis+fn_trainSource);
		ArrayList<String> arrTrainTarget=FileUtil.getFileStringArray(fopCurrentOutAnalysis+fn_trainTarget);
		
		HashMap<String,String> mapExpressions=new LinkedHashMap<String, String>();
		HashMap<String,HashSet<String>> mapAnalyseExprs=new LinkedHashMap<String, HashSet<String>>();
		
		for(int i=0;i<arrTrainTarget.size();i++){
			String[] itemSource=arrTrainSource.get(i).trim().split("\\s+");
			
			String[] itemTarget=arrTrainTarget.get(i).trim().split("\\s+");
			for(int j=0;j<itemTarget.length;j++){
				if(itemTarget[j].startsWith("E-Total")){
					if(!mapExpressions.containsKey(itemTarget[j])){
						mapExpressions.put(itemTarget[j], itemSource[j]);
						String idAnalyse=itemSource[j]+"_"+mapIdVars.get(itemTarget[j]);
						if(!mapAnalyseExprs.containsKey(idAnalyse)){
							HashSet<String> set=new LinkedHashSet<String>();
							set.add(itemTarget[j]);
							mapAnalyseExprs.put(idAnalyse, set);
						}else{
							mapAnalyseExprs.get(idAnalyse).add(itemTarget[j]);
						}
					}
					
				}
				
			}
									
		}
		
		SortUtil.sortHashMapStringHSStringByValueDesc(mapAnalyseExprs);
		
		ArrayList<String> lstNum=new ArrayList<>();
		lstNum.add("1");
		lstNum.add("2-10");
		lstNum.add("11-20");
		lstNum.add("21-50");
		lstNum.add("51-100");
		lstNum.add("GreaterThan100");
		
		HashMap<String,Integer> mapContent=new LinkedHashMap<String, Integer>();
		for(String key:lstNum){
			mapContent.put(key, 0);
		}
		StringBuilder sbAna=new StringBuilder();
		int totalCount=0;
		for(String key:mapAnalyseExprs.keySet()){
			int count=mapAnalyseExprs.get(key).size();
			if(count==1){
				String strOff=lstNum.get(0);
				mapContent.put(strOff, mapContent.get(strOff)+count);
			} else if (count>=2 && count<=10){
				String strOff=lstNum.get(1);
				mapContent.put(strOff, mapContent.get(strOff)+count);				
			} else if (count>=11 && count<=20){
				String strOff=lstNum.get(2);
				mapContent.put(strOff, mapContent.get(strOff)+count);
			} else if (count>=21 && count<=50){
				String strOff=lstNum.get(3);
				mapContent.put(strOff, mapContent.get(strOff)+count);
			} else if (count>=51 && count<=100){
				String strOff=lstNum.get(4);
				mapContent.put(strOff, mapContent.get(strOff)+count);
			} else {
				String strOff=lstNum.get(5);
				mapContent.put(strOff, mapContent.get(strOff)+count);
			}
			totalCount+=count;
		}
		
		for(String key:lstNum){
			int co=mapContent.get(key);
			sbAna.append(key+"\t"+co+"\t"+totalCount+"\t"+(co*100.0/totalCount)+"\n");
		}
		FileIO.writeStringToFile(sbAna.toString()+"\n", fop_mapTotalId+fn_anaMapParamResult);
		sbAna=new StringBuilder();
		for(String ket:mapAnalyseExprs.keySet()){
			sbAna.append(ket+"\t"+mapAnalyseExprs.get(ket).size()+"\t"+mapAnalyseExprs.get(ket).toString());
		}
		FileIO.writeStringToFile(sbAna.toString()+"\n", fop_mapTotalId+"ana_countOrder.txt");
	}

}
