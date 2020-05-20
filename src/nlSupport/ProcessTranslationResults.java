package nlSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import consts.PathConstanct;
import utils.FileIO;

public class ProcessTranslationResults {
	
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

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fopInputPrePostfix=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport\\step1_prepostfix\\";
		String fopInputMnames=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport\\step2_methodNames\\";
		String fopInputSequences=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport\\step3_inputSequence\\";
		String fopInputTrans=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport\\step4_trans\\";
		String fopOutputSplitTrans=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport\\step5_splitTrans\\";
		
		new File(fopOutputSplitTrans).mkdir();
//		String fname_methods="methods.txt";
		String fname_index="indexMethod.txt";
		String fname_testSource="test.s";
		String fname_testTarget="ref0";
		String fname_transResult="trans.txt";
		
		
		String strSplitNameToken="SpecialTokenForSplitting";		
		String unknownMethodToken="unknownMethod#identifier";
		
		String[] arrTrans=FileIO.readStringFromFile(fopInputTrans+fname_transResult).split("\n");
		String[] arrTestSource=FileIO.readStringFromFile(fopInputSequences+fname_testSource).split("\n");
		String[] arrTestTarget=FileIO.readStringFromFile(fopInputSequences+fname_testTarget).split("\n");
		String[] arrIndexes=FileIO.readStringFromFile(fopInputSequences+fname_index).split("\n");
		
		LinkedHashMap<Integer,ArrayList<String>> mapTestSource=new LinkedHashMap<Integer, ArrayList<String>>();
		LinkedHashMap<Integer,ArrayList<String>> mapTestTarget=new LinkedHashMap<Integer, ArrayList<String>>();
		LinkedHashMap<Integer,ArrayList<String>> mapTestTranslatedResult=new LinkedHashMap<Integer, ArrayList<String>>();
		
		int indexOfTest=1;
		for(int i=0;i<arrTestSource.length;i++) {
			String strItem=arrTestSource[i].trim();
			if(!strItem.isEmpty()) {
				if(strItem.equals(strSplitNameToken)) {
					indexOfTest++;
				}
				else if(!mapTestSource.containsKey(indexOfTest)) {
					ArrayList<String> lstItem=new ArrayList<String>();
					lstItem.add(i+"\t"+arrTestSource[i]);
					mapTestSource.put(indexOfTest, lstItem);
					
					ArrayList<String> lstItemTarget=new ArrayList<String>();
					lstItemTarget.add(i+"\t"+arrTestTarget[i]);
					mapTestTarget.put(indexOfTest, lstItemTarget);
					
					
				} else {
					mapTestSource.get(indexOfTest).add(i+"\t"+arrTestSource[i]);
					mapTestTarget.get(indexOfTest).add(i+"\t"+arrTestTarget[i]);					
				}
			}
			
		}
		
		indexOfTest=1;
		for(int i=0;i<arrTrans.length;i++) {
			String strItem=arrTrans[i].trim();
			
			String[] arrItems=strItem.split("\\|\\|\\|");
			
			if(arrItems.length>=3) {
				String codeItem=arrItems[2].trim();
				
				if(codeItem.equals(strSplitNameToken)) {
					System.out.println("aaa "+codeItem);
					indexOfTest++;
				} else {
					if(!mapTestTranslatedResult.containsKey(indexOfTest)) {
						ArrayList<String> lstItem=new ArrayList<String>();
						lstItem.add(i+"\t"+arrTrans[i]);
						mapTestTranslatedResult.put(indexOfTest, lstItem);
						
						
						
					} else {
						mapTestTranslatedResult.get(indexOfTest).add(i+"\t"+arrTrans[i]);
					}
				}
			}
		}
		
		System.out.println(mapTestSource.size()+"\t"+mapTestTarget.size()+"\t"+mapTestTranslatedResult.size());
		
		for(int i=1;i<=100;i++) {
			String nameFolder=String.format("%03d", i);			
			String fopItem=fopOutputSplitTrans+nameFolder+File.separator;
			new File(fopItem).mkdir();
			
			StringBuilder sbTestSource=new StringBuilder();
			StringBuilder sbTestRef=new StringBuilder();
			StringBuilder sbTestTrans=new StringBuilder();
			
			ArrayList<String> lstSource=mapTestSource.get(i);
			ArrayList<String> lstTarget=mapTestTarget.get(i);
			ArrayList<String> lstTrans=mapTestTranslatedResult.get(i);
			
			for(String str:lstSource) {
				sbTestSource.append(str+"\n");
			}
			
			for(String str:lstTarget) {
				sbTestRef.append(str+"\n");
			}
			
			for(String str:lstTrans) {
				sbTestTrans.append(str+"\n");
			}
			
			FileIO.writeStringToFile(sbTestSource.toString(), fopItem+fname_testSource);
			FileIO.writeStringToFile(sbTestRef.toString(), fopItem+fname_testTarget+".txt");
			FileIO.writeStringToFile(sbTestTrans.toString(), fopItem+fname_transResult);
			
		}
		
		
		
		
		

	}

}
