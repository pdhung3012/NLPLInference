package invocations;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import utils.FileIO;
import consts.PathConstanct;

public class AnalyzeStatTypeData {
	
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
		
		try (BufferedReader br = Files.newBufferedReader(Paths.get(fpFile), StandardCharsets.US_ASCII)) {
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
	
	public static String fopOldProjectLoc="G:AAAgithubAAArepos-5stars-50commitsAAA";
	
	public static void statisticProject(HashSet<String> setProject,HashSet<String> setPercentage,String fpFile){
		
		try (BufferedReader br = Files.newBufferedReader(Paths.get(fpFile), StandardCharsets.US_ASCII)) {
		    for (String line = null; (line = tryGetLine(br)) != null;) {
		    	String strProjectItem=line.trim().replaceAll("\\\\", "AAA").replaceFirst(fopOldProjectLoc,"");
		    	//System.out.println(strProjectItem);
		    	
		    	String[] arrItems=strProjectItem.split("AAA");
		    	if(arrItems.length>=3){
		    		setProject.add(arrItems[0]+"-"+arrItems[1]);
		    		String[] arrTemp=arrItems[arrItems.length-1].split("\\s+");
		    		String percen=arrTemp[arrTemp.length-1];
		    		setPercentage.add(percen);
		    	}
				
		    }
		} catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
	
	public static void main(String[] args){
		String fopStatType=PathConstanct.PATH_PROJECT_STATTYPE_DATA;
		String fpTrainSource=fopStatType+"train.s";
		String fpTrainTarget=fopStatType+"train.t";
		String fpTrainLocation=fopStatType+"train.locations.txt";	
		String fpTuneSource=fopStatType+"tune.s";
		String fpTuneTarget=fopStatType+"tune.t";
		String fpTuneLocation=fopStatType+"tune.locations.txt";	
		String fpTestSource=fopStatType+"test.s";
		String fpTestTarget=fopStatType+"test.t";
		String fpTestLocation=fopStatType+"test.locations.txt";	
		
		HashSet<String> setProjects=new LinkedHashSet<>();
		HashSet<String> setPercentage=new LinkedHashSet<>();
		
		statisticProject(setProjects,setPercentage,fopStatType+"train.locations.txt");
		statisticProject(setProjects,setPercentage,fopStatType+"tune.locations.txt");
		statisticProject(setProjects,setPercentage,fopStatType+"test.locations.txt");
		
		System.out.println("done statistics");
		
		int indexCount=0;
		StringBuilder sbResult=new StringBuilder();
		FileIO.writeStringToFile("", fopStatType+"allProjectName.txt");
		for(String iden:setProjects){
			indexCount++;
			sbResult.append(iden+"\n");
			
			if(indexCount%100000==0 || indexCount==+setProjects.size()){
				FileIO.appendStringToFile(sbResult.toString(), fopStatType+"allProjectName.txt");
				sbResult=new StringBuilder();
			}
			
		}
		
		indexCount=0;
		sbResult=new StringBuilder();
		FileIO.writeStringToFile("", fopStatType+"allPercentage.txt");
		for(String iden:setPercentage){
			indexCount++;
			sbResult.append(iden+"\n");
			
			if(indexCount%100000==0 || indexCount==+setPercentage.size()){
				FileIO.appendStringToFile(sbResult.toString(), fopStatType+"allPercentage.txt");
				sbResult=new StringBuilder();
			}
			
		}

		
		HashMap<String,Integer> mapVocab=new LinkedHashMap<String, Integer>();
		
		addToHashMap(mapVocab, fpTrainSource);
		addToHashMap(mapVocab, fpTrainTarget);
		addToHashMap(mapVocab, fpTuneSource);
		addToHashMap(mapVocab, fpTuneTarget);
		addToHashMap(mapVocab, fpTestSource);
		addToHashMap(mapVocab, fpTestTarget);
		
		System.out.println("done add");
		
		FileIO.writeStringToFile("", fopStatType+"fullVocab.txt");
		indexCount=0;
		sbResult=new StringBuilder();
		for(String iden:mapVocab.keySet()){
			indexCount++;
			sbResult.append(iden+"\t"+mapVocab.get(iden)+"\n");
			
			if(indexCount%100000==0 || indexCount==+mapVocab.size()){
				FileIO.appendStringToFile(sbResult.toString(), fopStatType+"fullVocab.txt");
				sbResult=new StringBuilder();
			}
			
		}
		
				
		System.out.println("OK");
		
	}
}
