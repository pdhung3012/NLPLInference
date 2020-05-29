package nlSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import consts.PathConstanct;
import utils.FileIO;
import utils.StanfordLemmatizer;

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
	
	public static Object[] getVarAppearInNLDescription(String varInCode,String nlDescription,HashMap<String,String> mapStringLiterals) {
		StringBuilder sb=new StringBuilder();
		String[] arrVarsInNL=nlDescription.split("\\s+");
		String[] arrVarsInCode=varInCode.split("ABCABC");
		LinkedHashSet<String> setNLTokens=new LinkedHashSet<String>();
		HashSet<String> setVarsAndAbstractInfo=new LinkedHashSet<String>();
		LinkedHashMap<String,String> mapTotalVarAndTypes=new LinkedHashMap<String, String>();
		for(int i=0;i<arrVarsInNL.length;i++) {
			setNLTokens.add(arrVarsInNL[i].trim());
			if(isNumeric(arrVarsInNL[i])) {
				if(arrVarsInNL[i].contains(".")) {
					mapTotalVarAndTypes.put(arrVarsInNL[i], "double");
					setVarsAndAbstractInfo.add(arrVarsInNL[i]);
				} else {
					mapTotalVarAndTypes.put(arrVarsInNL[i], "int");
					setVarsAndAbstractInfo.add(arrVarsInNL[i]);
				}
//				sb.append(arrVarsInNL[i]+"\t"+"int"+"\n");
			} 
			else if(arrVarsInNL[i].startsWith("StringLit_")) {
//				sb.append(arrVarsInNL[i]+"\t"+"String"+"\n");
				setVarsAndAbstractInfo.add(arrVarsInNL[i]);
				String strValue=mapStringLiterals.get(arrVarsInNL[i]);
				mapTotalVarAndTypes.put(strValue, "String");
			}
			else if(arrVarsInNL[i].equals("this")) {
//				sb.append(arrVarsInNL[i]+"\t"+"String"+"\n");
				setVarsAndAbstractInfo.add("this");
				
				mapTotalVarAndTypes.put("this", "Object");
			}
		}
//		System.out.println(setNLTokens.toString());
		
		for(int i=0;i<arrVarsInCode.length;i++) {
			String[] itemVarAndType=arrVarsInCode[i].split("\t");
			if(itemVarAndType.length<2) {
				continue;
			}
			itemVarAndType[0]=itemVarAndType[0].trim();
			itemVarAndType[1]=itemVarAndType[1].trim();
//			System.out.println(itemVarAndType[0]+"\t"+setNLTokens.contains(itemVarAndType[0].trim()));
			if(setNLTokens.contains(itemVarAndType[0])) {
//				sb.append(itemVarAndType[0]+"\t"+itemVarAndType[1]+"\n");
				setVarsAndAbstractInfo.add(itemVarAndType[0]);
				mapTotalVarAndTypes.put(itemVarAndType[0], itemVarAndType[1]);
			}
		}
		sb=new StringBuilder();
		for(String key:mapTotalVarAndTypes.keySet()) {
			sb.append(key+"\t"+mapTotalVarAndTypes.get(key)+"\n");
		}
		Object[] results=new Object[3];
		results[0]=sb.toString();
		results[1]=setVarsAndAbstractInfo;
		results[2]=mapTotalVarAndTypes;
		return results;
	}
	public static String regexCamelCase="(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])";
	public static String getTokenInformation(String nlDescription,HashSet<String> setVarsAndAbss,StanfordLemmatizer lemm) {
		StringBuilder sbTokens=new StringBuilder();
		String[] arrInputTokens=nlDescription.split("\\s+");
		for(int i=0;i<arrInputTokens.length;i++) {
			String[] arrItems=arrInputTokens[i].split(regexCamelCase);
			if(setVarsAndAbss.contains(arrInputTokens[i])) {
				continue;
			}
			for(int j=0;j<arrItems.length;j++) {
				
				sbTokens.append(arrItems[j]+" ");
			}
		}
		String strResult=lemm.lemmatizeToString(sbTokens.toString());
		String[] arr=strResult.split("\\s+");
		sbTokens=new StringBuilder();
		for(int i=0;i<arr.length;i++) {
			sbTokens.append(arr[i].toLowerCase()+"#term ");
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
	
	public static Object[] abstractStringLiteralAndNumberLiteral(String input) {
		StringBuilder sbItem=new StringBuilder();
		StringBuilder sbTotal=new StringBuilder();
		int indexQuote=0;
		boolean isInQuoteMode=false;
		HashMap<String,String> mapCodeEntities=new HashMap<String,String>();
		for(int i=0;i<input.length();i++) {
			char cIndex=input.charAt(i);
			if(cIndex=='\"') {
				indexQuote++;
				if(indexQuote%2==1) {
					sbItem=new StringBuilder();
					sbItem.append(cIndex);
					isInQuoteMode=true;
				}else {
					isInQuoteMode=false;
					sbItem.append(cIndex);
					int numberIndex=indexQuote%2;
					String key="StringLit_"+numberIndex;
					sbTotal.append(" "+key+" ");
//					System.out.println("update "+sbItem.toString());
					mapCodeEntities.put(key, sbItem.toString());
					
				}
				
			} else {
				if(!isInQuoteMode) {
					sbTotal.append(cIndex);
				}else {
					sbItem.append(cIndex);
				}
			}
			
		}
		Object[] results=new Object[2];
		results[0]=sbTotal.toString();
		results[1]=mapCodeEntities;
		return results;
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
		StanfordLemmatizer lemm=new StanfordLemmatizer();
//		String[] arrTextContent=new String[100];
		
		StringBuilder sbTotalPrefix=new StringBuilder();
		StringBuilder sbTotalPostfix=new StringBuilder();
		StringBuilder sbTotalvarInNaturalLanguage=new StringBuilder();
		StringBuilder sbTotalLemmTokenInNL=new StringBuilder();
		
		for(int i=1;i<=100;i++) {
//			if(i!=34) {
//				continue;
//			}
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
			Object[] results=abstractStringLiteralAndNumberLiteral(nlDescription);
			String nlDesAfterNormalize=(String)results[0];
			HashMap<String,String> mapLiterals=(HashMap<String, String>)results[1];
			nlDesAfterNormalize=nlDesAfterNormalize.replaceAll(","," ").replaceAll("\\("," ").replaceAll("\\)"," ").replaceAll("\\."," ");
			
						
			results=getVarAppearInNLDescription(strVarInfoInCode,nlDesAfterNormalize,mapLiterals);
			String listVariablesInNLAndType=(String)results[0];
//			System.out.println(nlDesAfterNormalize+"\t"+listVariablesInNLAndType+"\n"+strVarInfoInCode);
			LinkedHashSet<String> setVarAndAbs=(LinkedHashSet<String>) results[1];			
			LinkedHashMap<String,String> mapTotalVarAndTypes=(LinkedHashMap<String, String>) results[2];
			
			FileIO.writeStringToFile(listVariablesInNLAndType, fopOutputElement+fname_varInNaturalLanguage);
			String nlTokens=getTokenInformation(nlDesAfterNormalize,setVarAndAbs,lemm);
			System.out.println(i+"\t"+setVarAndAbs.size()+"\t"+setVarAndAbs.toString()+"\t"+mapTotalVarAndTypes.size()+"\t"+mapLiterals.size());
			FileIO.writeStringToFile(nlTokens, fopOutputElement+fname_lemmTokenInNL);	
			sbTotalPrefix.append(ppfx.getPrefix().replaceAll("\n", " , ")+"\n");
			sbTotalPostfix.append(ppfx.getPostfix().replaceAll("\n", " , ")+"\n");
			sbTotalvarInNaturalLanguage.append(listVariablesInNLAndType.replaceAll("\n", " , ")+"\n");
			sbTotalLemmTokenInNL.append(nlTokens.replaceAll("\n", " , ")+"\n");
//			String prefix
		}
		
		FileIO.writeStringToFile(sbTotalPrefix.toString(), fopOutputPrePostfix+fname_Prefix);
		FileIO.writeStringToFile(sbTotalPostfix.toString(), fopOutputPrePostfix+fname_Postfix);
		FileIO.writeStringToFile(sbTotalvarInNaturalLanguage.toString(), fopOutputPrePostfix+fname_varInNaturalLanguage);
		FileIO.writeStringToFile(sbTotalLemmTokenInNL.toString(), fopOutputPrePostfix+fname_lemmTokenInNL);	
		
		
		
	}
	
	

}
