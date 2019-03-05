package invocations;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Random;

import utils.FileIO;
import consts.PathConstanct;

public class ChooseJavaProjectPerLibrary {
	public static String[] arrExtractLibaryName={"extracted-android..txt",
		"extracted-com.google.gwt..txt","extracted-com.thoughtworks.xstream..txt",
		"extracted-org.hibernate..txt","extracted-org.joda.time..txt"};
	
	public static String[] arrCompactLibaryName={"android",
		"gwt","xstream",
		"hibernate","jodatime"};
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int selectLibIndex=1;
		String fopPathLogProject=PathConstanct.PATH_PROJECT_LOG;
		String fopProjectSequences=PathConstanct.PATH_OUTPUT_IDENTIFER_PROJECT;
		String fopOutput=PathConstanct.PATH_PROJECT_TRAIN_TEST_NAME+File.separator+arrCompactLibaryName[selectLibIndex]+File.separator;
		new File(fopOutput).mkdir();
		int numProjectRequired=200;
		int numTestRequired=20;
		HashSet<String> listTotalProjNames=new HashSet<>();
		
		FileIO.writeStringToFile("", fopOutput+"train.project.txt");
		FileIO.writeStringToFile("", fopOutput+"test.project.txt");
		FileIO.writeStringToFile("", fopOutput+"tune.project.txt");
		
		for(int i=0;i<arrExtractLibaryName.length;i++){
			String[] arrProjectName=FileIO.readStringFromFile(fopPathLogProject+arrExtractLibaryName[i]).trim().split("\n");
			ArrayList<String> listName=new ArrayList<String>();
			for(int j=0;j<arrProjectName.length;j++){
				String projectFolder=arrProjectName[j].replaceFirst("_", "-");
				File fileProjectLocations=new File(fopProjectSequences+projectFolder+File.separator+"locations.txt");
//				System.out.println(fileProjectLocations.getAbsolutePath());
				if(fileProjectLocations.isFile() && fileProjectLocations.length()>0){
					if(!listTotalProjNames.contains(projectFolder)){
						
						listName.add(projectFolder);
						listTotalProjNames.add(projectFolder);
						if(listName.size()==numProjectRequired){
							break;
						}
					}
				}
			}
			
			HashSet<String> setTestProjects=new LinkedHashSet<String>();
			Random rand = new Random();
			while(setTestProjects.size()<numTestRequired){
				int positionIndex=rand.nextInt(listName.size());				
				String strProjectName=listName.get(positionIndex);
				setTestProjects.add(strProjectName);
				listName.remove(positionIndex);
			}
			
			HashSet<String> setTuneProjects=new LinkedHashSet<String>();
			while(setTuneProjects.size()<numTestRequired){
				int positionIndex=rand.nextInt(listName.size());				
				String strProjectName=listName.get(positionIndex);
				setTuneProjects.add(strProjectName);
				listName.remove(positionIndex);
			}
			
			StringBuilder sbTrain=new StringBuilder();
			for(int j=0;j<listName.size();j++){
				sbTrain.append(listName.get(j)+"\n");
			}
			
			StringBuilder sbTune=new StringBuilder();
			for(String str:setTuneProjects){
				sbTune.append(str+"\n");
			}
			
			StringBuilder sbTest=new StringBuilder();
			for(String str:setTestProjects){
				sbTest.append(str+"\n");
			}
			
			FileIO.appendStringToFile(sbTrain.toString(), fopOutput+"train.project.txt");
			FileIO.appendStringToFile(sbTest.toString(), fopOutput+"test.project.txt");
			FileIO.appendStringToFile(sbTune.toString(), fopOutput+"tune.project.txt");
			
			
			
			
		}

}
