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
import utils.SortUtil;
import consts.PathConstanct;
import eval.EvalInOutPrecRecallExpressionInference;

public class AnalyzeTranslatedResultCorrectMapping {
	
	
	
	public static void main(String[] args){
		String fopInput=PathConstanct.PATH_PROJECT_TTT_CUR_EVAL_DATA;
		String fopMap=fopInput+"map"+File.separator;
		String fopEval=fopInput+"eval"+File.separator;
		String fpTestLocation=fopInput+"test.locations.txt";
		String fpTestSource=fopInput+"test.s";
		String fpTestTarget=fopInput+"test.t";
		String fpTestTranslatedResult=fopInput+"correctOrderTranslatedResult.txt";
		String fpIdAndAllContent=fopMap+"a_mapIdAndContent.txt";
		
		ArrayList<String> lstLocations=FileUtil.getFileStringArray(fpTestLocation);
		ArrayList<String> lstSourceSequences=FileUtil.getFileStringArray(fpTestSource);
		ArrayList<String> lstTargetSequences=FileUtil.getFileStringArray(fpTestTarget);
		ArrayList<String> lstTransSequences=FileUtil.getFileStringArray(fpTestTranslatedResult);
		
		String fpStatIdenByNumCSV=fopEval+"statCorrectNum.csv";
		String fpStatIdenBySetCSV=fopEval+"statCorrectSetTarget.csv";		
		String fpStatLineCSV=fopEval+"statCorrectLine.csv";
		String fpStatReceiverCSV=fopEval+"statCorrectReceiver.csv";
		String fpStatIdenBySetTXT=fopEval+"statCorrectSetTarget.txt";		
		String fpStatLineTXT=fopEval+"statCorrectLine.txt";
		String fpStatReceiverTXT=fopEval+"statCorrectReceiver.txt";
		
		HashMap<String, String> mapIdAndTotalContent = MapUtil
				.getHashMapFromFile(fpIdAndAllContent);
		HashMap<String,String> mapTotalLib=EvalInOutPrecRecallExpressionInference.getLibraryInfo(mapIdAndTotalContent);
		
		
		
//		1. find number of identifer have most correct mapping set of lines of mapping
//		2. find number of identifer with most type of mapping, extract the set of id mapping
//		3.find correct with maximum question marks
		
		HashMap<String,Integer> mapICAppear=new LinkedHashMap<String, Integer>();
		HashMap<String,HashSet<String>> mapICSetTarget=new LinkedHashMap<String,HashSet<String>>();
		HashMap<String,HashSet<String>> mapICReceiver=new LinkedHashMap<String,HashSet<String>>();
		HashMap<String,HashSet<Integer>> mapICLine=new LinkedHashMap<String,HashSet<Integer>>();
		for(int i=0;i<lstLocations.size();i++){
			String[] arrItemSource=lstSourceSequences.get(i).split("\\s+");
			String[] arrItemTarget=lstTargetSequences.get(i).split("\\s+");
			String[] arrItemTrans=lstTransSequences.get(i).split("\\s+");
			
			for(int j=0;j<arrItemSource.length;j++){
				if(arrItemTarget[j].startsWith("E-Total")){
					if( arrItemTarget[j].equals(arrItemTrans[j])){
						if(!mapICAppear.containsKey(arrItemSource[j])){
							mapICAppear.put(arrItemSource[j], 1);
						} else{
							mapICAppear.put(arrItemSource[j], mapICAppear.get(arrItemSource[j]+1));
						}
						
						if(!mapICSetTarget.containsKey(arrItemSource[j])){
							HashSet<String> setIt=new LinkedHashSet<String>();
							setIt.add(arrItemTarget[j]);
							mapICSetTarget.put(arrItemSource[j], setIt);
						} else{
							mapICSetTarget.get(arrItemSource[j]).add(arrItemTarget[j]);
						}
						
						String libItem=mapTotalLib.get(arrItemTarget[j]);
						if(!mapICReceiver.containsKey(arrItemSource[j])){
							HashSet<String> setIt=new LinkedHashSet<String>();
							setIt.add(libItem);
							mapICReceiver.put(arrItemSource[j], setIt);
						} else{
							mapICReceiver.get(arrItemSource[j]).add(libItem);
						}
						
						if(!mapICLine.containsKey(arrItemSource[j])){
							HashSet<Integer> setIt=new LinkedHashSet<Integer>();
							setIt.add((i+1));
							mapICLine.put(arrItemSource[j], setIt);
						} else{
							mapICLine.get(arrItemSource[j]).add((i+1));
						}
					}
					
				}
			}
		}
		
		System.out.println("Finish collect line!");
		
		mapICAppear=SortUtil.sortHashMapStringIntByValueDesc(mapICAppear);
		mapICLine=SortUtil.sortHashMapStringIntSetByValueDesc(mapICLine);
		mapICReceiver=SortUtil.sortHashMapStringStringSetByValueDesc(mapICReceiver);
		mapICSetTarget=SortUtil.sortHashMapStringStringSetByValueDesc(mapICSetTarget);		
		System.out.println("End sort!");
		
		StringBuilder sbCSV=new StringBuilder();
		StringBuilder sbTXT=new StringBuilder();
		
		sbCSV=new StringBuilder();
		sbCSV.append("Identifier,Num Correct\n");
		for(String strKey:mapICAppear.keySet()){
			sbCSV.append(strKey+","+mapICAppear.get(strKey)+"\n");
		}
		FileIO.writeStringToFile(sbCSV.toString()+"\n", fpStatIdenByNumCSV);
		
		sbCSV=new StringBuilder();
		sbTXT=new StringBuilder();
		for(String key:mapICReceiver.keySet()){
			HashSet<String> mapItem=mapICReceiver.get(key);
			sbCSV.append(key+","+mapItem.size()+"\n");
			sbTXT.append(key+"\t");
			for(String it:mapItem){
				sbTXT.append(it+",");
			}
			sbTXT.append("\n");
		}
		FileIO.writeStringToFile(sbCSV.toString()+"\n", fpStatReceiverCSV);
		FileIO.writeStringToFile(sbTXT.toString()+"\n", fpStatReceiverTXT);
		
		sbCSV=new StringBuilder();
		sbTXT=new StringBuilder();
		for(String key:mapICSetTarget.keySet()){
			HashSet<String> mapItem=mapICSetTarget.get(key);
			sbCSV.append(key+","+mapItem.size()+"\n");
			sbTXT.append(key+"\t");
			for(String it:mapItem){
				sbTXT.append(it+",");
			}
			sbTXT.append("\n");
		}
		FileIO.writeStringToFile(sbCSV.toString()+"\n", fpStatIdenBySetCSV);
		FileIO.writeStringToFile(sbTXT.toString()+"\n", fpStatIdenBySetTXT);
		
		sbCSV=new StringBuilder();
		sbTXT=new StringBuilder();
		for(String key:mapICLine.keySet()){
			HashSet<Integer> mapItem=mapICLine.get(key);
			sbCSV.append(key+","+mapItem.size()+"\n");
			sbTXT.append(key+"\t");
			for(Integer it:mapItem){
				sbTXT.append(it+",");
			}
			sbTXT.append("\n");
		}
		FileIO.writeStringToFile(sbCSV.toString()+"\n", fpStatLineCSV);
		FileIO.writeStringToFile(sbTXT.toString()+"\n", fpStatLineTXT);
		
		System.out.println("end")
		
		
		
		
		
		
		
	}
}
