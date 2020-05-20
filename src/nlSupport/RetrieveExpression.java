package nlSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import consts.PathConstanct;
import utils.FileIO;
import utils.MapUtil;

public class RetrieveExpression {
	
	public static String SplitInvocationCharacter="\\$\\%\\$";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		String fopInputPrePostfix=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport\\step1_prepostfix\\";
//		String fopInputMnames=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport\\step2_methodNames\\";
		String fopInputSequences=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport\\step3_inputSequence\\";
//		String fopInputTrans=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport\\step4_trans\\";
		String fopSplitTrans=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport\\step5_splitTrans\\";
		String fopExpression=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport\\step6_expr\\";
		String fopMapTotalId=PathConstanct.PATH_PROJECT_NL_SUPPORT+"map"+File.separator;
		
		new File(fopExpression).mkdir();
//		String fname_methods="methods.txt";
		String fname_indexInSource="indeInSource.txt";
		String fname_indexMethods="indexMethod.txt";
		String fname_testSource="test.s";
		String fname_testTarget="ref0.txt";
		String fname_transResult="trans.txt";
		String fname_reorderedResult="reordered.txt";
		String fname_codeInfo="code_allInfo.txt";
		String fname_codeExprId="code_exprId.txt";
		String fname_codeOnly="code_only.txt";
		String fname_codeVarInfo="code_varInfo.txt";
		String fname_codeImport="code_import.txt";
		String fn_mapTotalId="a_mapTotalIdAndContent.txt";
		
		String strSplitNameToken="SpecialTokenForSplitting";		
		String unknownMethodToken="unknownMethod#identifier";
		
		
		HashMap<String,String> mapTotalId=MapUtil.getHashMapFromFile(fopMapTotalId+fn_mapTotalId);
		System.out.println(mapTotalId.size()+"\t"+mapTotalId.get("E-Total-000179080"));
		
		String[] arrIndexOfUnknownPosition=FileIO.readStringFromFile(fopInputSequences+fname_indexMethods).split("\n");
		LinkedHashMap<Integer,Integer> mapPositions=new LinkedHashMap<Integer, Integer>();
		
		int indexOfTest=1;
		for(int i=0;i<arrIndexOfUnknownPosition.length;i++) {
			String[] arrItem=arrIndexOfUnknownPosition[i].trim().split("\t");
			if(arrItem.length>0) {
				if(arrItem[0].trim().equals("-1")) {
					indexOfTest++;
				}
				else if(!mapPositions.containsKey(indexOfTest)) {					
					int lineOfSource=Integer.parseInt(arrItem[0].trim());
					mapPositions.put(indexOfTest, lineOfSource);
					
				}
			}
			
		}
		System.out.println(mapPositions.toString());
		
		for(int i=1;i<=100;i++) {
			String nameFolder=String.format("%03d", i);	
			
			String fopOutputItem=fopExpression+nameFolder+File.separator;
			String fopInputItemTrans=fopSplitTrans+nameFolder+File.separator;
			new File(fopOutputItem).mkdir();
			int indexOfTranslatedPosition=mapPositions.get(i);
			String[] arrTransResults=FileIO.readStringFromFile(fopInputItemTrans+fname_reorderedResult).split("\n");
			ArrayList<String> lstTransId=new ArrayList<String>();
			
			StringBuilder sbCodeInfo=new StringBuilder();
			StringBuilder sbCodeExprId=new StringBuilder();
			StringBuilder sbCodeOnly=new StringBuilder();
			StringBuilder sbCodeVarInfo=new StringBuilder();
			StringBuilder sbCodeImport=new StringBuilder();
			
			for(int j=0;j<arrTransResults.length;j++) {
				String[] transTokens=arrTransResults[j].trim().split("\\s+");
				String exprResult=transTokens[indexOfTranslatedPosition];
				lstTransId.add(exprResult);
				if(mapTotalId.containsKey(exprResult)) {
					String exprInfo=mapTotalId.get(exprResult);
					String[] arrExprInfo=exprInfo.split(SplitInvocationCharacter);
					if(arrExprInfo.length>=4) {
						sbCodeInfo.append(exprInfo+"\n");
						sbCodeExprId.append(exprResult+"\n");
						sbCodeOnly.append(arrExprInfo[0]+"\n");
						sbCodeVarInfo.append(arrExprInfo[1]+"\n");
						sbCodeImport.append(arrExprInfo[2]+"\n");
					}
					
				}
			}
			
			FileIO.writeStringToFile(sbCodeExprId.toString(), fopOutputItem+fname_codeExprId);
			FileIO.writeStringToFile(sbCodeInfo.toString(), fopOutputItem+fname_codeInfo);
			FileIO.writeStringToFile(sbCodeOnly.toString(), fopOutputItem+fname_codeOnly);
			FileIO.writeStringToFile(sbCodeVarInfo.toString(), fopOutputItem+fname_codeVarInfo);
			FileIO.writeStringToFile(sbCodeImport.toString(), fopOutputItem+fname_codeImport);
			
			
			
			
		}
	}

}
