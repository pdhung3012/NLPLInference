package nlSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import consts.PathConstanct;
import utils.FileIO;

public class ReorderTranslatedResult {

	public static StringBuilder getTargetTokens(StringBuilder sbSource) {
		String[] arrInput=sbSource.toString().split("\n");
		StringBuilder sbOutput=new StringBuilder();
		for(int i=0;i<arrInput.length;i++) {
			String[] arrItem=arrInput[i].split("\\s+");
			for(int j=0;j<arrItem.length;j++) {
				if(arrItem[j].endsWith("#identifier")) {
					sbOutput.append("E-Total-00001 ");
				} else {
					sbOutput.append(arrItem[j]+" ");
				}
			}
			sbOutput.append("\n");
			
		}
		return sbOutput;
	}
	
	public static boolean isEndWith(String sourceItem, String transItem) {
		boolean check = false;
		if (sourceItem.endsWith("#identifier")) {
			if (transItem.equals(sourceItem) || transItem.startsWith("E-Total")) {
				check = true;
			} else {
				check = false;
			}

		} else {
			return transItem.endsWith(sourceItem);
		}
		return check;
	}
	
	public static void reorderingTokens(String fpInputSource, String fpInputTarget, String fpInputTransResult,
			String fpOutputTrans2Result,String fpOutputIndexInSource) {
		String[] arrInputSource = FileIO.readStringFromFile(fpInputSource).trim().split("\n");
		String[] arrInputTrans = FileIO.readStringFromFile(fpInputTransResult).trim().split("\n");
//		StringBuilder sbResult = new StringBuilder();
		for (int i = 0; i < arrInputSource.length; i++) {
			String[] arrItemSource = arrInputSource[i].trim().split("\\s+");
			ArrayList<String> lstTransLists=new ArrayList<String>();
			ArrayList<Integer> lstTransIndexes=new ArrayList<Integer>();
			for(int j=0;j<arrInputTrans.length;j++) {
				String[] arrTransElements=arrInputTrans[i].trim().split("\\|\\|\\|");
				if(arrTransElements.length>=3) {
					int indexInS=Integer.parseInt(arrTransElements[0].split("\t")[1].trim());
					String transCandidate=arrTransElements[2].trim();
					if(indexInS==i) {
						lstTransLists.add(transCandidate);
						lstTransIndexes.add(i);
					}
				}
			}
			
			ArrayList<String> lstOrderedTrans=new ArrayList<String>();
			
			for(int indexTrans=0;indexTrans<lstTransLists.size();indexTrans++) {
				String[] arrItemTrans = lstTransLists.get(indexTrans).trim().split("\\s+");
				String[] arrItemReordered = new String[arrItemTrans.length];
				for (int j = 0; j < arrItemSource.length; j++) {
					if(j<arrItemTrans.length) {
						if (isEndWith(arrItemSource[j],arrItemTrans[j])) {
							arrItemReordered[j] = arrInputTrans[j];
						} else {
							// find first occurence of ordered and change position
							for (int k = j + 1; k < arrItemTrans.length; k++) {
								if (isEndWith(arrItemSource[j], arrItemTrans[k])) {
									String temp = arrItemTrans[j];
									arrItemTrans[j] = arrItemTrans[k];
									arrItemTrans[k] = temp;
									arrItemReordered[j] = arrInputTrans[j];
									break;
								}
							}

						}
					} 
					
				}

				String strItemOrdered = "";
				for (int j = 0; j < arrItemTrans.length; j++) {
					strItemOrdered += arrItemTrans[j] + " ";
				}
//				System.out.println(i+" reorder: "+strItemOrdered);
				lstOrderedTrans.add(strItemOrdered.trim());
			}
			
			StringBuilder sbIndexesItem=new StringBuilder();
			StringBuilder sbOrderedItem=new StringBuilder();
			for(int index=0;index<lstOrderedTrans.size();index++) {
				sbIndexesItem.append(lstTransIndexes.get(index)+"\n");
				sbOrderedItem.append(lstTransLists.get(index)+"\n");
			}
			FileIO.writeStringToFile(sbIndexesItem.toString(), fpOutputIndexInSource);
			FileIO.writeStringToFile(sbOrderedItem.toString(), fpOutputTrans2Result);
		}
		
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		String fopInputPrePostfix=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport\\step1_prepostfix\\";
//		String fopInputMnames=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport\\step2_methodNames\\";
//		String fopInputSequences=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport\\step3_inputSequence\\";
//		String fopInputTrans=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport\\step4_trans\\";
		String fopSplitTrans=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport\\step5_splitTrans\\";
		
//		new File(fopOutputSplitTrans).mkdir();
//		String fname_methods="methods.txt";
		String fname_index="indeInSource.txt";
		String fname_testSource="test.s";
		String fname_testTarget="ref0.txt";
		String fname_transResult="trans.txt";
		String fname_reorderedResult="reordered.txt";
		
		
		String strSplitNameToken="SpecialTokenForSplitting";		
		String unknownMethodToken="unknownMethod#identifier";
		
		
		
		for(int i=1;i<=100;i++) {
			String nameFolder=String.format("%03d", i);	
			String fpInputSource=fopSplitTrans+nameFolder+File.separator+fname_testSource;
			String fpInputTarget=fopSplitTrans+nameFolder+File.separator+fname_testTarget;
			String fpInputTransResult=fopSplitTrans+nameFolder+File.separator+fname_transResult;
			String fpOutputTrans2Result=fopSplitTrans+nameFolder+File.separator+fname_reorderedResult;
			String fpOutputIndexInSource=fopSplitTrans+nameFolder+File.separator+fname_index;
			
			reorderingTokens(fpInputSource,fpInputTarget, fpInputTransResult,fpOutputTrans2Result,fpOutputIndexInSource);
			
		}
		
		
		
		
		

	}

}
