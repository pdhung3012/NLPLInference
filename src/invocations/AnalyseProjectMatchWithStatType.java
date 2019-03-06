package invocations;

import java.util.HashSet;
import java.util.LinkedHashSet;

import utils.FileIO;
import consts.PathConstanct;

public class AnalyseProjectMatchWithStatType {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fopIdenIfer=PathConstanct.PATH_PROJECT_TRAIN_TEST_NAME;
		String fopStatType=PathConstanct.PATH_PROJECT_STATTYPE_DATA;
		
		String[] allProjectCurrent=FileIO.readStringFromFile(fopIdenIfer+"all.project.txt").split("\n");
		String[] arrStatType=FileIO.readStringFromFile(fopStatType+"allProjectName.txt").split("\n");
		int countAppear=0;
		
		HashSet<String> setST=new LinkedHashSet<String>();
		for(int i=0;i<arrStatType.length;i++){
			setST.add(arrStatType[i]);
		
		}
		
		HashSet<String> setNLPLInference=new LinkedHashSet<String>();
		for(int i=0;i<allProjectCurrent.length;i++){
			setNLPLInference.add(allProjectCurrent[i]);
		
		}
		
		StringBuilder sbResult=new StringBuilder();
		for(int i=0;i<allProjectCurrent.length;i++){
			boolean exist=setST.contains(allProjectCurrent[i]);
			if(exist){
				countAppear++;
			}
			sbResult.append(allProjectCurrent[i]+"\t"+exist+"\n");
		}
		
		int countInStatType=0;
		StringBuilder sbR2=new StringBuilder();
		for(int i=0;i<arrStatType.length;i++){
			boolean exist=setNLPLInference.contains(arrStatType[i]);
			if(exist){
				countInStatType++;
			}
			sbR2.append(arrStatType[i]+"\t"+exist+"\n");
		}
		
		
		FileIO.writeStringToFile(sbResult.toString(), fopIdenIfer+"overlapInNLPLInference.txt");
		FileIO.writeStringToFile(sbR2.toString(), fopIdenIfer+"overlapInST.txt");
		System.out.println("count overlap in NLPL: "+countAppear);
		System.out.println("count overlap in StatType: "+countInStatType);
		
		
	}

}
