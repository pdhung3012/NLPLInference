package nlSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import consts.PathConstanct;
import utils.FileIO;
import utils.MapUtil;
import utils.StanfordLemmatizer;

public class SortExpressionAndProvideFinalOutput {
	
	public static String SplitInvocationCharacter="\\$\\%\\$";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fopInputPrePostfix=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport\\step1_prepostfix\\";
//		String fopInputMnames=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport\\step2_methodNames\\";
		String fopInputSequences=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport\\step3_inputSequence\\";
//		String fopInputTrans=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport\\step4_trans\\";
		String fopInpurSplitTrans=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport\\step5_splitTrans\\";
		String fopInputExpression=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport\\step6_expr\\";
		String fopOutputRankingCandidates=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport\\step7_ranking\\";
		String fopMapTotalId=PathConstanct.PATH_PROJECT_NL_SUPPORT+"map"+File.separator;
		
		new File(fopOutputRankingCandidates).mkdir();
//		String fname_methods="methods.txt";
		String fname_indexInSource="indeInSource.txt";
		String fname_indexMethods="indexMethod.txt";
		String fname_testSource="test.s";
		String fname_testTarget="ref0.txt";
//		String fname_transResult="trans.txt";
		String fname_reorderedResult="reordered.txt";
		String fname_codeInfo="code_allInfo.txt";
		String fname_codeExprId="code_exprId.txt";
		String fname_codeOnly="code_only.txt";
		String fname_codeVarInfo="code_varInfo.txt";
		String fname_codeImport="code_import.txt";
		String fname_codeFinal="code_final.txt";
		String fname_codeScore="scoreOfCandidates.txt";
		
		String fname_varInfo="ele_varInfo.txt";
		String fname_lemm="ele_lemmToken.txt";
		
		String fn_mapTotalId="a_mapTotalIdAndContent.txt";
		
		String strSplitNameToken="SpecialTokenForSplitting";		
		String unknownMethodToken="unknownMethod#identifier";
		StanfordLemmatizer st=new StanfordLemmatizer();
				
		
		
		for(int i=1;i<=100;i++) {
			String nameFolder=String.format("%03d", i);	
			String fopOutputItem=fopOutputRankingCandidates+nameFolder+File.separator;
			new File(fopOutputItem).mkdir();
			
			String strVarInNL=FileIO.readStringFromFile(fopInputPrePostfix+nameFolder+File.separator+fname_varInfo);
			String strTermInfo=FileIO.readStringFromFile(fopInputPrePostfix+nameFolder+File.separator+fname_lemm);
			String[] arrVarInCode=FileIO.readStringFromFile(fopInputExpression+nameFolder+File.separator+fname_codeVarInfo).split("\n");
			String[] arrCodeOnly=FileIO.readStringFromFile(fopInputExpression+nameFolder+File.separator+fname_codeOnly).split("\n");
			String[] arrCodeInfo=FileIO.readStringFromFile(fopInputExpression+nameFolder+File.separator+fname_codeInfo).split("\n");
			String[] arrExprId=FileIO.readStringFromFile(fopInputExpression+nameFolder+File.separator+fname_codeExprId).split("\n");
			String[] arrImport=FileIO.readStringFromFile(fopInputExpression+nameFolder+File.separator+fname_codeImport).split("\n");
			
			ArrayList<ObjectTranslatedCandidate> listTransCandidates=new ArrayList<ObjectTranslatedCandidate>();
			
			for(int j=0;j<arrCodeOnly.length;j++) {
				if(!arrCodeOnly[j].isEmpty()) {
					ObjectTranslatedCandidate itemTransCands=new ObjectTranslatedCandidate();
					itemTransCands.setStrCodeInfo(arrCodeOnly[j]);
					ArrayList<ObjectMatchedVariablesInNL> listMatchedVarsInNL=getMatchedVarsInNL(strVarInNL);
					ArrayList<ObjectTermInNL> listTermsInNL=getTermsInNL(strTermInfo);
					itemTransCands.setListMatchedVarsInNL(listMatchedVarsInNL);					
					itemTransCands.setListTermInNL(listTermsInNL);
					String[] arrItemVarsInCode =arrVarInCode[j].split("#");
					itemTransCands.setStrCodeVarInfo(arrVarInCode[j]);
					ArrayList<ObjectMatchedVarTypeInCode> lstVarsInCode=new ArrayList<ObjectMatchedVarTypeInCode>();
					for(int k=0;k<arrItemVarsInCode.length;k++) {
						if(!arrItemVarsInCode[k].trim().isEmpty()) {
							String className=getClassName(arrItemVarsInCode[k].trim());
							ObjectMatchedVarTypeInCode omtc=new ObjectMatchedVarTypeInCode();
							omtc.setClassName(className);
							lstVarsInCode.add(omtc);
						}
					}
					itemTransCands.setListMatchedVarType(lstVarsInCode);	
					
					LinkedHashSet<String> setTermsInCode=new LinkedHashSet<String>();
					getSetOfTermInCode(arrCodeOnly[j], setTermsInCode, st);
					itemTransCands.setSetTermsInCode(setTermsInCode);
					itemTransCands.calculateScoreAndMatchVariable();
					listTransCandidates.add(itemTransCands);
				}
			}
			
			Collections.sort(listTransCandidates, Collections.reverseOrder());   
			
			StringBuilder sbCodeFinal=new StringBuilder();
			StringBuilder sbScoreFinal=new StringBuilder();
			
			for(int j=0;j<listTransCandidates.size();j++) {
				ObjectTranslatedCandidate item=listTransCandidates.get(j);
				sbCodeFinal.append(item.getStrCodeFinal()+"\n");
				sbScoreFinal.append(item.getScoreTotal()+"\n");
			}
			
			FileIO.writeStringToFile(sbCodeFinal.toString(), fopOutputItem+fname_codeFinal);
			FileIO.writeStringToFile(sbScoreFinal.toString(), fopOutputItem+fname_codeScore);
			
			
			
		}
	}
	
	public static void getSetOfTermInCode(String strCodeInfo,LinkedHashSet<String> setTerms,StanfordLemmatizer st) {
		String[] arrCodeTokens=strCodeInfo.trim().replaceAll("\\."," ").replaceAll("\\,"," ").replaceAll("\\("," ").replaceAll("\\)"," ").split("\\s+");
		setTerms=new LinkedHashSet<String>();
		for(int i=0;i<arrCodeTokens.length;i++) {
			String[] arrItemToken=getCamelCaseSplit(arrCodeTokens[i].trim()).split("\\s+");
			for(int j=0;j<arrItemToken.length;j++) {
				String itemLemm=st.lemmatizeToString(arrItemToken[j]);
				setTerms.add(itemLemm);
			}
		}
	
	}
	
	public static String getCamelCaseSplit(String str) {
		StringBuilder res = new StringBuilder();
		for(int i = 0; i < str.length(); i++) {
		   Character ch = str.charAt(i);
		     if(Character.isUpperCase(ch))
		       res.append( " " + Character.toLowerCase(ch));
		     else
		       res.append(ch);
		}
		return res.toString();
	}
	
	public static String getClassName(String fullName) {
		String[] arr=fullName.split("\\.");
		if(arr.length>0) {
			return arr[arr.length-1];
		}
		return "";
	}
	
	public static ArrayList<ObjectMatchedVariablesInNL> getMatchedVarsInNL(String strVarInNL){
		ArrayList<ObjectMatchedVariablesInNL> lstVars=new ArrayList<ObjectMatchedVariablesInNL>();
		String[] arrVarInNL=strVarInNL.split("\n");
		for(int i=0;i<arrVarInNL.length;i++) {
			String[] arrItemVarNL=arrVarInNL[i].trim().split("\t");
			if(arrItemVarNL.length>=2) {
				ObjectMatchedVariablesInNL itemNL=new ObjectMatchedVariablesInNL();
				itemNL.setVarName(arrItemVarNL[0]);
				itemNL.setVarType(arrItemVarNL[1]);
				lstVars.add(itemNL);
			}
		}
		return lstVars;
	}
	
	public static ArrayList<ObjectTermInNL> getTermsInNL(String strTermInNL){
		ArrayList<ObjectTermInNL> lstTerms=new ArrayList<ObjectTermInNL>();
		String[] arrTermInNL=strTermInNL.split("\\s+");
		for(int i=0;i<arrTermInNL.length;i++) {
			String itemTerm=arrTermInNL[i].replaceAll("#term","");
			if(!itemTerm.isEmpty()) {
				ObjectTermInNL term=new ObjectTermInNL();
				term.setTerm(itemTerm);
				lstTerms.add(term);
			}
			
		}	
		return lstTerms;
	}

}
