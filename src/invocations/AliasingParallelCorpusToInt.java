package invocations;

import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

import consts.PathConstanct;
import utils.FileIO;

public class AliasingParallelCorpusToInt {

	public static String tryGetLine(BufferedReader br) {
		String line=null;
		try {
			line = br.readLine();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return line;
	}
	
	public static void addToHashMap(HashMap<String,Integer> mapVocabStrInt,String fpFile){
		
		try (BufferedReader br = Files.newBufferedReader(Paths.get(fpFile), StandardCharsets.ISO_8859_1)) {
		    for (String line = null; (line = tryGetLine(br)) != null;) {
		    	//System.out.println(line);
		    	String[] arrItems=line.trim().split("\\s+");
				for(int j=0;j<arrItems.length;j++){
					if(!mapVocabStrInt.containsKey(arrItems[j])){
						mapVocabStrInt.put(arrItems[j], mapVocabStrInt.size()+1);
					}
				}
		    }
		} catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		String folderInput=PathConstanct.PATH_PROJECT_TTT_CUR_EVAL_DATA;
		String folderNormalize=folderInput+"alias"+File.separator;
		new File(folderNormalize).mkdir();
		int numRestricted=40000;
		

		String fpInTrainSource=folderInput+"train.s";
		String fpInTrainTarget=folderInput+"train.t";
		String fpInTuneSource=folderInput+"tune.s";
		String fpInTuneTarget=folderInput+"tune.t";
		String fpInTestSource=folderInput+"test.s";
		String fpInTestTarget=folderInput+"test.t";
		
		String fpOutTrainSource=folderNormalize+"train.s";
		String fpOutTrainTarget=folderNormalize+"train.t";
		String fpOutTuneSource=folderNormalize+"tune.s";
		String fpOutTuneTarget=folderNormalize+"tune.t";
		String fpOutTestSource=folderNormalize+"test.s";
		String fpOutTestTarget=folderNormalize+"test.t";
		String fpAlias=folderNormalize+"alias.txt";
		
		HashMap<String,Integer> mapVocabStrInt=new LinkedHashMap<>();
		
		addToHashMap(mapVocabStrInt, fpInTrainSource);
		System.out.println("Done hash");
		addToHashMap(mapVocabStrInt, fpInTrainTarget);
		System.out.println("Done hash");
		addToHashMap(mapVocabStrInt, fpInTuneSource);
		System.out.println("Done hash");
		addToHashMap(mapVocabStrInt, fpInTuneTarget);
		System.out.println("Done hash");
		addToHashMap(mapVocabStrInt, fpInTestSource);
		System.out.println("Done hash");
		addToHashMap(mapVocabStrInt, fpInTestTarget);
		System.out.println("Done hash");
		System.out.println("Done library of alias "+mapVocabStrInt.size());
		
		normalize(fpInTrainSource,fpOutTrainSource,mapVocabStrInt);
		normalize(fpInTuneSource,fpOutTuneSource,mapVocabStrInt);
		normalize(fpInTestSource,fpOutTestSource,mapVocabStrInt);
		normalize(fpInTrainTarget,fpOutTrainTarget,mapVocabStrInt);
		normalize(fpInTuneTarget,fpOutTuneTarget,mapVocabStrInt);
		normalize(fpInTestTarget,fpOutTestTarget,mapVocabStrInt);
		
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
		
		GenerateVocabulary.getVocabulary(folderNormalize+"train.s", folderNormalize+"vocab.s");
		GenerateVocabulary.getVocabulary(folderNormalize+"train.t", folderNormalize+"vocab.t");
		

	}
	
	public static void normalize(String fpInput,String fpOutput,HashMap<String,Integer> mapVocabStrInt){
		StringBuilder sbResult=new StringBuilder();
		FileIO.writeStringToFile("", fpOutput);
		int i=0;
		try (BufferedReader br = Files.newBufferedReader(Paths.get(fpInput), StandardCharsets.ISO_8859_1)) {
		    for (String line = null; (line = tryGetLine(br)) != null;) {
		    	//System.out.println(line);
		    	i++;
		    	String[] arrItemsS=line.trim().split("\\s+");
				StringBuilder strItem=new StringBuilder();
				
				for(int j=0;j<arrItemsS.length;j++){
					if(!mapVocabStrInt.containsKey(arrItemsS[j])){
						mapVocabStrInt.put(arrItemsS[j], mapVocabStrInt.size()+1);
					}
					strItem.append(mapVocabStrInt.get(arrItemsS[j])+" ");
					
				}
				sbResult.append(strItem.toString().trim()+"\n");
				
				if(i+1==1000000 ){
					FileIO.appendStringToFile(sbResult.toString(),fpOutput);
					sbResult=new StringBuilder();
				}
		    }
		 
		} catch(Exception ex){
			ex.printStackTrace();
		}
		if(!sbResult.toString().isEmpty()) {
			FileIO.appendStringToFile(sbResult.toString(),fpOutput);
			sbResult=new StringBuilder();
		}
		
		
				
	}

}
