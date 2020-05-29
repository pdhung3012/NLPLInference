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
		String fname_methods="methods.txt";
		String fname_accByExactMatch="accuracy_exactMatch.txt";
		String fname_accByCosineSimilarity="accuracy_cosineSim.txt";
		String fname_codeFinal="code_final.txt";
		
		new File(fopOutputAccMnames).mkdir();
		
		StringBuilder sbTotalExactAcc=new StringBuilder();
		StringBuilder sbTotalSimText=new StringBuilder();
		
		int topK=10;
		
		for(int indexK=1;indexK<=10;indexK++) {
			int exactMatchCount=0;
			double averageDistance=0;
			for(int i=1;i<=100;i++) {
				String nameOfFile=String.format("%03d", i);
				String fonIndexFile=File.separator+nameOfFile+File.separator;
				
				String mExpectedCode=FileIO.readStringFromFile(fopInputTextMetaData+nameOfFile+".java").split("\n")[0].trim();
				mExpectedCode=preprocessText(mExpectedCode);
				
				HashMap<String,Double> mapTrans=new LinkedHashMap<String,Double>();
				String[] arrTrans=FileIO.readStringFromFile(fopInRankingCandidates+fonIndexFile+fname_codeFinal).split("\n");
				for(int k=0;k<Math.min(arrTrans.length, indexK);k++) {
					String strK= preprocessText(arrTrans[k].trim());
					double scoreK=StringSimilarity.similarity(mExpectedCode, strK);
					mapTrans.put(strK, scoreK);					
				}
				mapTrans=SortUtil.sortHashMapStringDoubleByValueDesc(mapTrans);
				if(mapTrans.containsKey(mExpectedCode)) {
					exactMatchCount++;
				}
				if(mapTrans.size()>0) {
					averageDistance+=(double)mapTrans.values().toArray()[0];
				}
				
			}
			double matchedScore=exactMatchCount*1.0/100;
			averageDistance=averageDistance/100;
			
			
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
