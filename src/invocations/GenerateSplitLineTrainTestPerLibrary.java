package invocations;

import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import utils.FileIO;
import consts.PathConstanct;

public class GenerateSplitLineTrainTestPerLibrary {

	public static void getSplitData(String fpIn,String fpOut,String fpLine,int number){
		FileIO.writeStringToFile("",fpOut);
		StringBuilder sbResult=new StringBuilder();
		StringBuilder sbLine=new StringBuilder();
		int numLine=0;

		FileIO.writeStringToFile("",fpOut);
		FileIO.writeStringToFile("",fpLine);
		try (BufferedReader br = Files.newBufferedReader(Paths.get(fpIn), StandardCharsets.UTF_8)) {
		    
			for (String line = null; (line = br.readLine()) != null;) {
		    	String[] arrItems=line.trim().split("\\s+");
				numLine++;
		    	StringBuilder sbPerLine=new StringBuilder();
		    	for(int j=0;j<arrItems.length;j++){
		    		sbPerLine.append(arrItems[j]+" ");
		    		if(j+1%numLine==0){
		    			sbResult.append(sbPerLine.toString().trim()+"\n");
		    			sbLine.append(numLine+"\n");
		    			sbPerLine=new StringBuilder();
		    		} else if (j==arrItems.length-1){
		    			sbResult.append(sbPerLine.toString().trim()+"\n");
		    			sbLine.append(numLine+"\tendLine"+"\n");
		    			sbPerLine=new StringBuilder();
		    		}
				}
		    	
		    	if(numLine%1000000==0){
					FileIO.appendStringToFile(sbResult.toString(),fpOut);
					FileIO.appendStringToFile(sbLine.toString(),fpLine);
					sbResult=new StringBuilder();
					sbLine=new StringBuilder();
				}
//				prevLine=line;
		    }
		} catch(Exception ex){
			ex.printStackTrace();
		}
		
		if(!sbResult.toString().isEmpty()){
			FileIO.appendStringToFile(sbResult.toString(),fpOut);
			FileIO.appendStringToFile(sbLine.toString(),fpLine);
//			sbResult=new StringBuilder();
		}
		
		
	}
	
	public static String[] arrCompactLibaryName={"android",
		"gwt","xstream",
		"hibernate","jodatime"};
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int selectLibIndex=1;
		String fopIn=PathConstanct.PATH_PROJECT_TTT_ALIAS+arrCompactLibaryName[selectLibIndex]+File.separator;
		String fopOut=PathConstanct.PATH_PROJECT_TTT_SPLIT_ALIAS+arrCompactLibaryName[selectLibIndex]+File.separator;
		new File(fopOut).mkdir();
		int numLine=30;
		getSplitData(fopIn+"train.source.txt", fopOut+"train.source.txt", fopOut+"train.source.line.txt",numLine);
		getSplitData(fopIn+"train.target.txt", fopOut+"train.target.txt", fopOut+"train.target.line.txt",numLine);
		getSplitData(fopIn+"tune.source.txt", fopOut+"tune.source.txt", fopOut+"tune.source.line.txt",numLine);
		getSplitData(fopIn+"tune.target.txt", fopOut+"tune.target.txt", fopOut+"tune.target.line.txt",numLine);
		getSplitData(fopIn+"test.source.txt", fopOut+"test.source.txt", fopOut+"test.source.line.txt",numLine);
		getSplitData(fopIn+"test.target.txt", fopOut+"test.target.txt", fopOut+"test.target.line.txt",numLine);
		
	}

}
