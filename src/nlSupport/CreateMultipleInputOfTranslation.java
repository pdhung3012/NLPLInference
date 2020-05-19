package nlSupport;

import java.io.File;
import java.util.ArrayList;

import consts.PathConstanct;
import utils.FileIO;

public class CreateMultipleInputOfTranslation {

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
//		String fpInputSource=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport\\step0_sequence\\source.txt";
//		String fopInputTextMetaData=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport\\IVC\\a_NLAndMethodNames\\";
		String fopInputPrePostfix=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport\\step1_prepostfix\\";
		String fopInputMnames=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport\\step2_methodNames\\";
		String fopOutputMnames=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport\\step3_inputSequence\\";
		
		
		String fname_Prefix="ele_prefix.txt";
		String fname_Postfix="ele_postfix.txt";
		String fname_varInNaturalLanguage="ele_varInfo.txt";
		String fname_lemmTokenInNL="ele_lemmToken.txt";
		String fname_methods="methods.txt";
		String fname_index="indexMethod.txt";
		String fname_testSource="test.s";
		String fname_testTarget="ref0";
		
		String strSplitNameToken="SpecialTokenForSplitting";
		
		String unknownMethodToken="unknownMethod#identifier";
		String splitContent="AABBAA";
		
		new File(fopInputPrePostfix).mkdir();
		
		ArrayList<String> listNumbers=new ArrayList<String>();
		
		for(int i=1;i<=100;i++) {
			String item=String.format("%03d", i);
			listNumbers.add(item);
		}
		
//		write pre post fix to output, split the code of the string
//		String[] arrSourceContent=FileIO.readStringFromFile(fpInputSource).split("\n");
		
//		String[] arrTextContent=new String[100];
		StringBuilder sbTotalToken=new StringBuilder();
		StringBuilder sbIndexes=new StringBuilder();
		int numMethods=50;
		for(int i=1;i<=100;i++) {
			String nameOfFile=String.format("%03d", i);
			
			String prefix=FileIO.readStringFromFile(fopInputPrePostfix+fname_Prefix);
			String postfix=FileIO.readStringFromFile(fopInputPrePostfix+fname_Postfix);
			String strVar=FileIO.readStringFromFile(fopInputPrePostfix+fname_varInNaturalLanguage);
			String strTokenLemm=FileIO.readStringFromFile(fopInputPrePostfix+fname_lemmTokenInNL);
			String strListMName=FileIO.readStringFromFile(fopOutputMnames+fname_methods);
			
			
			String[] arrPrefix=prefix.split("\\s+");
//			String[] arrPostfix=postfix.split("\\s+");
			String[] arrVar=strVar.split("\n");
			String[] arrTerms=strTokenLemm.split("\\s+");
			String[] arrMNames=strListMName.split("\n");
			
			StringBuilder sbTokenVar=new StringBuilder();
			int indexItem=arrPrefix.length;
			for(int j=0;j<arrVar.length;j++) {
				String[] infoVar=arrVar[j].split("\t");
				if(infoVar.length>=2) {
					String[] arrTypeInfo=infoVar[1].split(".");					
					String typeName=arrTypeInfo[arrTypeInfo.length-1];
					sbTokenVar.append(typeName+" ");
					indexItem++;
				}
			}
			
			StringBuilder sbTokenTerm=new StringBuilder();
			
			for(int j=0;j<arrTerms.length;j++) {
				sbTokenTerm.append(arrTerms[j]+" ");
			}
			
			
			for(int j=0;j<Math.min(arrMNames.length,numMethods);j++) {
				if(!arrMNames[j].isEmpty()) {
					String strTokens=prefix+" "+sbTokenVar.toString()+" "+arrMNames[j]+" "+sbTokenTerm.toString()+" "+postfix+" ";
					sbTotalToken.append(strTokens+"\n");
					sbIndexes.append(indexItem+"\n");
				}
			}
			sbTotalToken.append(strSplitNameToken+"\n");
			sbIndexes.append((-1)+"\n");
			
		}
		String strTargetToken=getTargetTokens(sbTotalToken).toString();
		FileIO.writeStringToFile(sbTotalToken.toString(), fopOutputMnames+fname_testSource);
		FileIO.writeStringToFile(strTargetToken, fopOutputMnames+fname_testTarget);
		
	}

}
