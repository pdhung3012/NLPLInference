package nlSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;

import consts.PathConstanct;
import utils.FileIO;

public class AllocatingInformationFromNL {
	
	public static PrePostFixObject getPrepostfixOfUnkToken(String[] arr,String unkToken) {
		StringBuilder sbPrefix=new StringBuilder();
		StringBuilder sbPostfix=new StringBuilder();
		
		boolean isFoundToken=false;
		for(int i=0;i<arr.length;i++) {
			if(arr[i].equals(unkToken)) {
				isFoundToken=true;
			}
			if(!isFoundToken) {
				sbPrefix.append(arr[i]+" ");
			} else {
				sbPostfix.append(arr[i]+" ");
			}
		}
		
		PrePostFixObject obj=new PrePostFixObject();
		obj.setPrefix(sbPrefix.toString());
		obj.setPostfix(sbPostfix.toString());
		return obj;
		
		
	}
	
	public static String getVarAppearInNLDescription(String varInCode,String nlDescription) {
		StringBuilder sb=new StringBuilder();
		String[] arrVarsInNL=nlDescription.split("\\s+");
		String[] arrVarsInCode=varInCode.split(",");
		LinkedHashSet<String> setNLTokens=new LinkedHashSet<String>();
		for(int i=0;i<arrVarsInNL.length;i++) {
			setNLTokens.add(arrVarsInNL[i]);
			if(isNumeric(arrVarsInNL[i])) {
				sb.append(arrVarsInNL[i]+"\t"+"int"+"\n");
			} else if(arrVarsInNL[i].startsWith("\"") && arrVarsInNL[i].endsWith("\"") ) {
				sb.append(arrVarsInNL[i]+"\t"+"String"+"\n");
			}
		}
		
		
		for(int i=0;i<arrVarsInCode.length;i++) {
			String[] itemVarAndType=arrVarsInCode[i].split("\t");
			if(itemVarAndType.length<2) {
				continue;
			}
			if(setNLTokens.contains(itemVarAndType[0])) {
				sb.append(itemVarAndType[0]+"\t"+itemVarAndType[1]+"\n");
			}
		}
		return sb.toString();
	}
	public static String regexCamelCase="(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])";
	public static String getTokenInformation(String nlDescription) {
		StringBuilder sbTokens=new StringBuilder();
		String[] arrInputTokens=nlDescription.split("\\s+");
		for(int i=0;i<arrInputTokens.length;i++) {
			String[] arrItems=arrInputTokens[i].split(regexCamelCase);
			for(int j=0;j<arrItems.length;j++) {
				sbTokens.append(arrItems[j]+" ");
			}
		}
		return sbTokens.toString();
	}
	
	public static boolean isNumeric(String strNum) {
	    if (strNum == null) {
	        return false;
	    }
	    try {
	        double d = Double.parseDouble(strNum);
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	    return true;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fpInputSource=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport\\step0_sequence\\source.txt";
		String fopInputTextMetaData=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport\\IVC\\a_NLAndMethodNames\\";
		String fopOutputPrePostfix=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport\\step1_prepostfix\\";
		String fname_Prefix="ele_prefix.txt";
		String fname_Postfix="ele_postfix.txt";
		String fname_varInNaturalLanguage="ele_varInfo.txt";
		String fname_lemmTokenInNL="ele_lemmToken.txt";
		String unknownMethodToken="unknownMethod#identifier";
		String splitContent="AABBAA";
		
		new File(fopOutputPrePostfix).mkdir();
		
		ArrayList<String> listNumbers=new ArrayList<String>();
		
		for(int i=1;i<=100;i++) {
			String item=String.format("%03d", i);
			listNumbers.add(item);
		}
		
//		write pre post fix to output, split the code of the string
		String[] arrSourceContent=FileIO.readStringFromFile(fpInputSource).split("\n");
		
//		String[] arrTextContent=new String[100];
		
		for(int i=1;i<=100;i++) {
			String nameOfFile=String.format("%03d", i);
			String fopOutputElement=fopOutputPrePostfix+File.separator+nameOfFile+File.separator;
			new File(fopOutputElement).mkdir();
			String strCodeToken=arrSourceContent[i-1].split(splitContent)[0];
			String strVarInfoInCode=arrSourceContent[i-1].split(splitContent)[1];
			String[] arrTokens=strCodeToken.split("\\s+");
			PrePostFixObject ppfx=getPrepostfixOfUnkToken(arrTokens, unknownMethodToken);
			FileIO.writeStringToFile(ppfx.getPrefix(), fopOutputElement+fname_Prefix);
			FileIO.writeStringToFile(ppfx.getPostfix(), fopOutputElement+fname_Postfix);
			String nlDescription=FileIO.readStringFromFile(fopInputTextMetaData+nameOfFile+".java").split("\n")[1];
//			System.out.println(nlDescription);
			nlDescription=nlDescription.replaceAll(","," ").replaceAll("\\("," ").replaceAll("\\)"," ").replaceAll("\\."," ");
//			System.out.println(nlDescription);
			String listVariablesInNLAndType=getVarAppearInNLDescription(strVarInfoInCode,nlDescription);
			FileIO.writeStringToFile(listVariablesInNLAndType, fopOutputElement+fname_varInNaturalLanguage);
			String nlTokens=getTokenInformation(nlDescription);
			FileIO.writeStringToFile(nlTokens, fopOutputElement+fname_lemmTokenInNL);			
//			String prefix
		}
		
		
	}

}
