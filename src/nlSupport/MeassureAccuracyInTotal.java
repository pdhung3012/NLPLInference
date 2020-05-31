package nlSupport;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import consts.PathConstanct;
import utils.FileIO;
import utils.SortUtil;

public class MeassureAccuracyInTotal {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fopInputTextMetaData=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport\\IVC\\a_NLAndMethodNames\\";
		String fopInputPrePostfix=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport\\step1_prepostfix\\";
		String fopInputMnames=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport\\step2_methodNames\\";
		String fopInRankingCandidates=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport\\step7_ranking\\";
		
		String fopOutputAccMnames=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport\\accuracy\\";
		String fopOutputAccResultDetails=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport\\accuracy\\details\\";
		String fname_methods="methods.txt";
		String fname_accByExactMatch="accuracy_exactMatch.txt";
		String fname_accByCosineSimilarity="accuracy_cosineSim.txt";
		String fname_codeFinal="code_final.txt";
		
		new File(fopOutputAccMnames).mkdir();
		new File(fopOutputAccResultDetails).mkdir();
		
		StringBuilder sbTotalExactAcc=new StringBuilder();
		StringBuilder sbTotalSimText=new StringBuilder();
		
		int topK=10;
		
		for(int indexK=1;indexK<=10;indexK++) {
			int exactMatchCount=0;
			double averageDistance=0;
			StringBuilder sbTotalExactMatch=new StringBuilder();
			StringBuilder sbTotalExpected=new StringBuilder();
			for(int i=1;i<=100;i++) {
				String nameOfFile=String.format("%03d", i);
				String fonIndexFile=File.separator+nameOfFile+File.separator;
				
				String[] arrNL=FileIO.readStringFromFile(fopInputTextMetaData+nameOfFile+".java").split("\n");
				String mExpectedCode=arrNL[0].trim();
				String mExpectedMethodName=arrNL[2].trim();
				String mExpectedNL=arrNL[1].trim();
				mExpectedCode=preprocessText(mExpectedCode);
				sbTotalExpected.append(mExpectedCode+"\n");
				
				HashMap<String,Double> mapTrans=new LinkedHashMap<String,Double>();
				String[] arrTrans=FileIO.readStringFromFile(fopInRankingCandidates+fonIndexFile+fname_codeFinal).split("\n");
				for(int k=0;k<Math.min(arrTrans.length, indexK);k++) {
					String strK= preprocessText(arrTrans[k].trim());
					double scoreK=StringSimilarity.similarity(mExpectedCode, strK);
					mapTrans.put(strK, scoreK);					
				}
				
				mapTrans=SortUtil.sortHashMapStringDoubleByValueDesc(mapTrans);
				boolean isMatch=mapTrans.containsKey(mExpectedCode);
				
				
				if(isMatch) {
					exactMatchCount++;
				}
				double scoreSim=0;
				if(mapTrans.size()>0) {
					String top1Trans=(String)mapTrans.keySet().toArray()[0];					
					scoreSim=(double)mapTrans.values().toArray()[0];
					averageDistance+=scoreSim;
					sbTotalExactMatch.append(isMatch+"\t"+scoreSim+"\t"+mExpectedCode+"\t"+top1Trans+"\t"+mExpectedMethodName+"\t"+mExpectedNL+"\n");
				} else {
					sbTotalExactMatch.append(isMatch+"\t"+scoreSim+"\t"+mExpectedCode+"\t"+"NO_TRANS"+"\t"+mExpectedMethodName+"\t"+mExpectedNL+"\n");
				}
				
				
				
				
			}
			double matchedScore=exactMatchCount*1.0/100;
			averageDistance=averageDistance/100;
			
			FileIO.writeStringToFile(sbTotalExactMatch.toString(), fopOutputAccResultDetails+"top-"+indexK+".txt");
			sbTotalExactAcc.append("Top-"+indexK+"\t"+matchedScore+"\n");
			sbTotalSimText.append("Top-"+indexK+"\t"+averageDistance+"\n");
			
			
		}
		FileIO.writeStringToFile(sbTotalExactAcc.toString(), fopOutputAccMnames+fname_accByExactMatch);
		FileIO.writeStringToFile(sbTotalSimText.toString(), fopOutputAccMnames+fname_accByCosineSimilarity);
		
		
		
		

	}
	
	public static String preprocessText(String input) {
		String out=input.replaceAll("\\s+", "");
		return out;
	}

}
