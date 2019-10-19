package invocations;

import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import consts.PathConstanct;
import utils.FileIO;

public class SubsetTestingDataForRNN {

	public static void getSubSetTest(String fpIn,String fpOut,String fpLine,int numberSubset){
		FileIO.writeStringToFile("",fpOut);
//		StringBuilder sbResult=new StringBuilder();
//		StringBuilder sbLine=new StringBuilder();
		int numLine=0;

		FileIO.writeStringToFile("",fpOut);
		FileIO.writeStringToFile("",fpLine);
		try (BufferedReader br = Files.newBufferedReader(Paths.get(fpIn), StandardCharsets.ISO_8859_1)) {
		    
			for (String line = null; (line = br.readLine()) != null;) {
//		    	String[] arrItems=line.trim().split("\\s+");
				numLine++;
//		    	StringBuilder sbPerLine=new StringBuilder();
//		    	for(int j=0;j<arrItems.length;j++){
//		    		sbPerLine.append(arrItems[j]+" ");
//		    		if((j+1)%number==0){
//		    			sbResult.append(sbPerLine.toString().trim()+"\n");
//		    			sbLine.append(numLine+"\n");
//		    			sbPerLine=new StringBuilder();
//		    		} else if (j==arrItems.length-1){
//		    			sbResult.append(sbPerLine.toString().trim()+"\n");
//		    			sbLine.append(numLine+"\tendLine"+"\n");
//		    			sbPerLine=new StringBuilder();
//		    		}
//				}
		    	
		    	if(numLine<=numberSubset){
					FileIO.appendStringToFile(line.trim()+"\n",fpOut);
//					FileIO.appendStringToFile(sbLine.toString(),fpLine);
//					sbResult=new StringBuilder();
//					sbLine=new StringBuilder();
				}
//				prevLine=line;
		    }
		} catch(Exception ex){
			ex.printStackTrace();
		}
		
//		if(!sbResult.toString().isEmpty()){
//			FileIO.appendStringToFile(sbResult.toString(),fpOut);
//			FileIO.appendStringToFile(sbLine.toString(),fpLine);
////			sbResult=new StringBuilder();
//		}
		
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fopIn=PathConstanct.PATH_PROJECT_TTT_CUR_EVAL_DATA+"outputCombine"+File.separator;
		String fopOut=PathConstanct.PATH_PROJECT_TTT_CUR_EVAL_DATA+"outputSubsetTest"+File.separator;
		new File(fopOut).mkdir();
		int numTest=1000;
//		getSplitData(fopIn+"train.s", fopOut+"train.s", fopOut+"train.s.line.txt",numLine);
//		getSplitData(fopIn+"train.t", fopOut+"train.t", fopOut+"train.t.line.txt",numLine);
//		getSplitData(fopIn+"tune.s", fopOut+"tune.s", fopOut+"tune.s.line.txt",numLine);
//		getSplitData(fopIn+"tune.t", fopOut+"tune.t", fopOut+"tune.t.line.txt",numLine);
		getSubSetTest(fopIn+"test.s", fopOut+"test.s", fopOut+"test.s.line.txt",numTest);
		getSubSetTest(fopIn+"test.t", fopOut+"test.t", fopOut+"test.t.line.txt",numTest);
		
	}

}
