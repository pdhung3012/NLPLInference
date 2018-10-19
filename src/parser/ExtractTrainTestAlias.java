package parser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

import utils.FileIO;

public class ExtractTrainTestAlias {

	public static void normalize(String folderInput,String tempInput,String folderOutput,HashMap<String,Integer> mapVocabStrInt,String fname,int size){
		String[] arrTrainS=FileIO.readStringFromFile(folderInput+fname).trim().split("\n");
		StringBuilder sbResult=new StringBuilder();
		StringBuilder sbLine=new StringBuilder();
		
		for(int i=0;i<size;i++){
			String[] arrItemsS=arrTrainS[i].trim().split("\\s+");
			String strItem="";
			int numTokenPerLine=0;
			for(int j=0;j<arrItemsS.length;j++){
				numTokenPerLine++;
				if(!mapVocabStrInt.containsKey(arrItemsS[j])){
					mapVocabStrInt.put(arrItemsS[j], mapVocabStrInt.size()+1);
				}
				strItem+=mapVocabStrInt.get(arrItemsS[j])+" ";
				if(numTokenPerLine==20||j==arrItemsS.length-1){
					sbResult.append(strItem.trim()+"\n");
					sbLine.append((i+1)+"\n");
					numTokenPerLine=0;
					strItem="";
				}
			}
			
		
		}
		FileIO.writeStringToFile(sbResult.toString(), folderOutput+fname);
		FileIO.writeStringToFile(sbLine.toString(), tempInput+fname);
		
	}

	public static void normalize(String folderInput,String tempInput,String folderOutput,HashMap<String,Integer> mapVocabStrInt,String fname){
		String[] arrTrainS=FileIO.readStringFromFile(folderInput+fname).trim().split("\n");
		StringBuilder sbResult=new StringBuilder();
		StringBuilder sbLine=new StringBuilder();		
		for(int i=0;i<arrTrainS.length;i++){
			String[] arrItemsS=arrTrainS[i].trim().split("\\s+");
			String strItem="";
			int numTokenPerLine=0;
			for(int j=0;j<arrItemsS.length;j++){
				numTokenPerLine++;
				if(!mapVocabStrInt.containsKey(arrItemsS[j])){
					mapVocabStrInt.put(arrItemsS[j], mapVocabStrInt.size()+1);
				}
				strItem+=mapVocabStrInt.get(arrItemsS[j])+" ";
				if(numTokenPerLine==50||j==arrItemsS.length-1){
					sbResult.append(strItem.trim()+"\n");
					sbLine.append((i+1)+"\n");
					numTokenPerLine=0;
					strItem="";
				}
			}
		}
		FileIO.writeStringToFile(sbResult.toString(), folderOutput+fname);
		FileIO.writeStringToFile(sbLine.toString(), tempInput+fname);
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String folderInput="/Users/hungphan/git/nmt/stattype/origin/";
		String folderNormalize="/Users/hungphan/git/nmt/stattype/";
		String folderTemp="/Users/hungphan/git/nmt/stattype//temp/";
		String fpTrainSource=folderInput+"train.s";
		String fpTrainTarget=folderInput+"train.t";
		String fpVocabSource=folderNormalize+"vocab.s";
		String fpVocabTarget=folderNormalize+"vocab.t";
		String fpAlias=folderNormalize+"alias.txt";
		
		String[] arrTrainS=FileIO.readStringFromFile(fpTrainSource).trim().split("\n");
		String[] arrTrainT=FileIO.readStringFromFile(fpTrainTarget).trim().split("\n");
		HashMap<String,Integer> mapVocabStrInt=new LinkedHashMap<String, Integer>(); 
		
		HashSet<String> setVocabS=new HashSet<>();
		HashSet<String> setVocabT=new HashSet<>();
		
		for(int i=0;i<arrTrainS.length;i++){
			String[] arrItemsS=arrTrainS[i].trim().split("\\s+");
			String[] arrItemsT=arrTrainT[i].trim().split("\\s+");
			for(int j=0;j<(arrItemsS.length>=10?10:arrItemsS.length);j++){
				
				if(!mapVocabStrInt.containsKey(arrItemsS[j])){
					mapVocabStrInt.put(arrItemsS[j], mapVocabStrInt.size()+1);
				}
				if(!mapVocabStrInt.containsKey(arrItemsT[j])){
					mapVocabStrInt.put(arrItemsT[j], mapVocabStrInt.size()+1);
				}
				setVocabS.add(mapVocabStrInt.get(arrItemsS[j])+"");
				setVocabT.add(mapVocabStrInt.get(arrItemsT[j])+"");
			}
			
		}
		StringBuilder sbResult=new StringBuilder();
		sbResult.append("<unk>\n<s>\n</s>\n");
		for(String strItem:setVocabS){

			sbResult.append(strItem+"\n");
		}
		FileIO.writeStringToFile(sbResult.toString(),fpVocabSource);
		
		sbResult=new StringBuilder();
		sbResult.append("<unk>\n<s>\n</s>\n");
		for(String strItem:setVocabT){
			sbResult.append(strItem+"\n");
		}
		FileIO.writeStringToFile(sbResult.toString(),fpVocabTarget);
		
		
		
		normalize(folderInput,folderTemp,folderNormalize,mapVocabStrInt,"train.s");
		normalize(folderInput,folderTemp,folderNormalize,mapVocabStrInt,"train.t");
		normalize(folderInput,folderTemp,folderNormalize,mapVocabStrInt,"tune.s");
		normalize(folderInput,folderTemp,folderNormalize,mapVocabStrInt,"tune.t");
		normalize(folderInput,folderTemp,folderNormalize,mapVocabStrInt,"test.s");
		normalize(folderInput,folderTemp,folderNormalize,mapVocabStrInt,"test.t");
		
		sbResult=new StringBuilder();
		for(String strItem:mapVocabStrInt.keySet()){
			sbResult.append(strItem+"\t"+mapVocabStrInt.get(strItem)+"\n");
		}
		FileIO.writeStringToFile(sbResult.toString(),fpAlias);
		
		
	}

}
