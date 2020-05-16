package nlSupport;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

import consts.PathConstanct;
import utils.FileIO;
import utils.FileUtil;
import utils.StanfordLemmatizer;

public class MakeContextDatasetForMethodName {
	
	public static String regexCamelCase="(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])";

	public static String getCamelCaseTokensInMethod(String methodToken,StanfordLemmatizer lemm) {
		String strInput=methodToken.replaceAll("#identifier", "");
		StringBuilder sbResult=new StringBuilder();
		for(String item:strInput.split(regexCamelCase)) {
//			item=lemm.lemmatizeToString(item);
			sbResult.append(item+" ");
		}
		String strResult=lemm.lemmatizeToString(sbResult.toString());
		String[] arr=strResult.split("\\s+");
		sbResult=new StringBuilder();
		for(int i=0;i<arr.length;i++) {
			sbResult.append(arr[i].toLowerCase()+"#term ");
		}
		return sbResult.toString();
	}
	
	public static PrePostFixObject getPrepostfix(String[] arr,int j,StanfordLemmatizer lemm) {
		StringBuilder sbPrefix=new StringBuilder();
		StringBuilder sbPostfix=new StringBuilder();
		String methodInfo=getCamelCaseTokensInMethod(arr[j],lemm);
		for(int i=0;i<arr.length;i++) {
			if(i<j) {
				sbPrefix.append(arr[i]+" ");
			} else if(i>j){
				sbPostfix.append(arr[i]+" ");
			} else {
				sbPrefix.append(methodInfo+" ");
				sbPostfix.append(methodInfo+" ");
			}
		}
		
		PrePostFixObject obj=new PrePostFixObject();
		obj.setPrefix(sbPrefix.toString());
		obj.setPostfix(sbPostfix.toString());
		return obj;
		
		
	}
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fopInput=PathConstanct.PATH_PROJECT_NL_SUPPORT;
		String fopOutput=fopInput+"nlSupport/";
		String idenTag="#identifier";
		
		String fpTrainS=fopInput+"train.s";
		String fpOutputPrefix=fopOutput+"prefix.txt";
		String fpOutputPostfix=fopOutput+"postfix.txt";
		String fpOutputMethodName=fopOutput+"methods.txt";
		
		StanfordLemmatizer lemm=new StanfordLemmatizer();
		new File(fopOutput).mkdir();
		
		ArrayList<String> listMethodSources=FileUtil.getFileStringArray(fpTrainS);
		int countOfPrefixPostfix=0;
		StringBuilder sbTotalPrefix=new StringBuilder();
		StringBuilder sbTotalPostfix=new StringBuilder();
		StringBuilder sbTotalMName=new StringBuilder();
		int countRefresh=1000;
		FileIO.writeStringToFile("", fpOutputPrefix);
		FileIO.writeStringToFile("", fpOutputPostfix);
		FileIO.writeStringToFile("", fpOutputMethodName);
		
		for(int i=0;i<listMethodSources.size();i++) {
			String[] arrTokens=listMethodSources.get(i).split("\\s+");
			for(int j=0;j<arrTokens.length;j++) {
				if(arrTokens[j].endsWith(idenTag)) {
					PrePostFixObject object=getPrepostfix(arrTokens, j,lemm);
					sbTotalPrefix.append(object.getPrefix()+"\n");
					sbTotalPostfix.append(object.getPostfix()+"\n");
					sbTotalMName.append(arrTokens[j]+"\n");
					
					countOfPrefixPostfix++;
					if(countOfPrefixPostfix%countRefresh==0) {
						FileIO.appendStringToFile(sbTotalPrefix.toString(), fpOutputPrefix);
						FileIO.appendStringToFile(sbTotalPostfix.toString(), fpOutputPostfix);
						FileIO.appendStringToFile(sbTotalMName.toString(), fpOutputMethodName);
						sbTotalPrefix=new StringBuilder();
						sbTotalPostfix=new StringBuilder();
						sbTotalMName=new StringBuilder();
					}
					
				}
				
			}
			System.out.println("count at index "+i+" has num of tokens as "+countOfPrefixPostfix);
			
			if(i==listMethodSources.size()-1 && !sbTotalPrefix.toString().isEmpty()) {
				FileIO.appendStringToFile(sbTotalPrefix.toString(), fpOutputPrefix);
				FileIO.appendStringToFile(sbTotalPostfix.toString(), fpOutputPostfix);
				FileIO.appendStringToFile(sbTotalMName.toString(), fpOutputMethodName);
			}
		}
		System.out.println("total method extracted: "+countOfPrefixPostfix);
	}
	

}

class PrePostFixObject{
	private String prefix;
	private String postfix;
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getPostfix() {
		return postfix;
	}
	public void setPostfix(String postfix) {
		this.postfix = postfix;
	}
	
	
}
