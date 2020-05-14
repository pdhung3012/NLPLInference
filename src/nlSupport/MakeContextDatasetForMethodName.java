package nlSupport;

import java.io.File;
import java.util.ArrayList;

import utils.FileIO;
import utils.FileUtil;

public class MakeContextDatasetForMethodName {
	
	

	public static PrePostFixObject getPrepostfix(String[] arr,int j) {
		StringBuilder sbPrefix=new StringBuilder();
		StringBuilder sbPostfix=new StringBuilder();
		
		for(int i=0;i<arr.length;i++) {
			if(i<j) {
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
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fopInput="";
		String fopOutput="";
		String idenTag="#identifier";
		
		String fpTrainS=fopInput+"train.s";
		String fpOutputPrefix=fopOutput+"prefix.txt";
		String fpOutputPostfix=fopOutput+"postfix.txt";
		String fpOutputMethodName=fopOutput+"methods.txt";
		
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
					PrePostFixObject object=getPrepostfix(arrTokens, j);
					sbTotalPrefix.append(object.getPrefix()+"\n");
					sbTotalPostfix.append(object.getPostfix()+"\n");
					sbTotalMName.append(arrTokens[j]+"\n");
					
					countOfPrefixPostfix++;
					if(countOfPrefixPostfix%countRefresh==0) {
						FileIO.appendStringToFile(sbTotalPrefix.toString(), fpOutputPrefix);
						FileIO.appendStringToFile(sbTotalPostfix.toString(), fpOutputPostfix);
						FileIO.appendStringToFile(sbTotalPostfix.toString(), fpOutputMethodName);
						sbTotalPrefix=new StringBuilder();
						sbTotalPostfix=new StringBuilder();
						sbTotalMName=new StringBuilder();
					}
					
				}
				
			}
			
			if(i==listMethodSources.size()-1 && !sbTotalPrefix.toString().isEmpty()) {
				FileIO.appendStringToFile(sbTotalPrefix.toString(), fpOutputPrefix);
				FileIO.appendStringToFile(sbTotalPostfix.toString(), fpOutputPostfix);
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
