package invocations;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

import consts.PathConstanct;
import utils.FileIO;

public class AliasingParallelCorpusToInt {

	public static void addToHashMap(HashMap<String,Integer> mapVocabStrInt,String fpFile){
		String[] arrContent=FileIO.readStringFromFile(fpFile).trim().split("\n");
		
		for(int i=0;i<arrContent.length;i++){
			String[] arrItems=arrContent[i].trim().split("\\s+");
			for(int j=0;j<arrItems.length;j++){
				if(!mapVocabStrInt.containsKey(arrItems[j])){
					mapVocabStrInt.put(arrItems[j], mapVocabStrInt.size()+1);
				}
			}
		}
		
		
	}
	
	public static void main(String[] args) {
		String folderInput=PathConstanct.PATH_PROJECT_TTT_DATA;
		String folderNormalize=PathConstanct.PATH_PROJECT_TTT_ALIAS;

		String fpInTrainSource=folderInput+"train.source.txt";
		String fpInTrainTarget=folderInput+"train.target.txt";
		String fpInTuneSource=folderInput+"tune.source.txt";
		String fpInTuneTarget=folderInput+"tune.target.txt";
		String fpInTestSource=folderInput+"test.source.txt";
		String fpInTestTarget=folderInput+"test.target.txt";
		
		String fpOutTrainSource=folderNormalize+"train.source.txt";
		String fpOutTrainTarget=folderNormalize+"train.target.txt";
		String fpOutTuneSource=folderNormalize+"tune.source.txt";
		String fpOutTuneTarget=folderNormalize+"tune.target.txt";
		String fpOutTestSource=folderNormalize+"test.source.txt";
		String fpOutTestTarget=folderNormalize+"test.target.txt";
		String fpAlias=folderNormalize+"alias.txt";
		
		HashMap<String,Integer> mapVocabStrInt=new LinkedHashMap<>();
		
		addToHashMap(mapVocabStrInt, fpInTrainSource);
		addToHashMap(mapVocabStrInt, fpInTrainTarget);
		addToHashMap(mapVocabStrInt, fpInTuneSource);
		addToHashMap(mapVocabStrInt, fpInTuneTarget);
		addToHashMap(mapVocabStrInt, fpInTestSource);
		addToHashMap(mapVocabStrInt, fpInTestTarget);
		
		System.out.println("Done library of alias "+mapVocabStrInt.size());
		
		normalize(fpInTrainSource,fpOutTrainSource,mapVocabStrInt);
		normalize(fpInTuneSource,fpOutTuneSource,mapVocabStrInt);
		normalize(fpInTestSource,fpOutTestSource,mapVocabStrInt);
		
		StringBuilder sbResult=new StringBuilder();
		FileIO.writeStringToFile("",fpAlias);
		int indexLine=0;
		for(String strItem:mapVocabStrInt.keySet()){
			indexLine++;
			sbResult.append(strItem+"\t"+mapVocabStrInt.get(strItem)+"\n");
			if(indexLine==1000000 || indexLine==mapVocabStrInt.size()){
				FileIO.appendStringToFile(sbResult.toString(),fpAlias);
				sbResult=new StringBuilder();
			}
		}
		

	}
	
	public static void normalize(String fpInput,String fpOutput,HashMap<String,Integer> mapVocabStrInt){
		String[] arrTrainS=FileIO.readStringFromFile(fpInput).trim().split("\n");
		StringBuilder sbResult=new StringBuilder();
		FileIO.writeStringToFile("", fpOutput);
		for(int i=0;i<arrTrainS.length;i++){
			String[] arrItemsS=arrTrainS[i].trim().split("\\s+");
			StringBuilder strItem=new StringBuilder();
			
			for(int j=0;j<arrItemsS.length;j++){
				if(!mapVocabStrInt.containsKey(arrItemsS[j])){
					mapVocabStrInt.put(arrItemsS[j], mapVocabStrInt.size()+1);
				}
				strItem.append(mapVocabStrInt.get(arrItemsS[j])+" ");
				
			}
			sbResult.append(strItem.toString().trim()+"\n");
			
			if(i+1==1000000 || i+1==arrTrainS.length){
				FileIO.appendStringToFile(sbResult.toString(),fpOutput);
				sbResult=new StringBuilder();
			}
		}
		
		
				
	}

}
