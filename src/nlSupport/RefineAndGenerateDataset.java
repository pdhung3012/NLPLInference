package nlSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import consts.PathConstanct;
import utils.FileIO;
import utils.FileUtil;

public class RefineAndGenerateDataset {

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		If triple duplicate then only one triple stored in db
		String fopInput=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport/";
		String fopOutput=fopInput+"filter/";
		String idenTag="#identifier";
		
		String fpInputPrefix=fopInput+"prefix.txt";
		String fpInputPostfix=fopInput+"postfix.txt";
		String fpInputMethodName=fopInput+"methods.txt";
		
		String fpOutputPrefix=fopOutput+"prefix.txt";
		String fpOutputPostfix=fopOutput+"postfix.txt";
		String fpOutputMethodName=fopOutput+"methods.txt";
		String fpOutputCount=fopOutput+"count.txt";
		
		ArrayList<String> listMNames=FileUtil.getFileStringArray(fpInputMethodName);
		ArrayList<String> listPrefix=FileUtil.getFileStringArray(fpInputPrefix);
		ArrayList<String> listPostfix=FileUtil.getFileStringArray(fpInputPostfix);
		
		System.out.println("done step loading ");
		new File(fopOutput).mkdir();
		
		String splitContent="AABBAA";
		HashMap<Integer,String> mapKey=new HashMap<Integer, String>();
		HashMap<Integer,Integer> mapCount=new HashMap<Integer, Integer>();
		System.out.println(listMNames.size()+"\t"+listPrefix.size()+"\t"+listPostfix.size());
		for(int i=0;i<listPrefix.size();i++) {
			String key=listMNames.get(i)+splitContent+listPrefix.get(i)+splitContent+listPostfix.get(i);
			int valKey=key.hashCode();
			if(!mapKey.containsKey(valKey)) {
				mapKey.put(valKey, key);
				mapCount.put(valKey, 1);
			}else {
				mapCount.put(valKey, mapCount.get(valKey)+1);
			}
		}
		
		System.out.println("done step 1 "+mapKey.size());
		
		StringBuilder sbTotalPrefix=new StringBuilder();
		StringBuilder sbTotalPostfix=new StringBuilder();
		StringBuilder sbTotalMName=new StringBuilder();
		StringBuilder sbTotalCount=new StringBuilder();
		int countRefresh=1000;
		FileIO.writeStringToFile("", fpOutputPrefix);
		FileIO.writeStringToFile("", fpOutputPostfix);
		FileIO.writeStringToFile("", fpOutputMethodName);
		FileIO.writeStringToFile("", fpOutputCount);
		int countOfPrefixPostfix=0;
		
		for(Integer valKey:mapKey.keySet()) {
			countOfPrefixPostfix++;
			String[] arrVals=mapKey.get(valKey).split(splitContent);
			int count=mapCount.get(valKey);
			sbTotalPrefix.append(arrVals[1]+"\n");
			sbTotalPostfix.append(arrVals[2]+"\n");
			sbTotalMName.append(arrVals[0]+"\n");
			sbTotalCount.append(count+"\n");
			if(countOfPrefixPostfix%countRefresh==0 || countOfPrefixPostfix==mapKey.size()) {
				FileIO.appendStringToFile(sbTotalPrefix.toString(), fpOutputPrefix);
				FileIO.appendStringToFile(sbTotalPostfix.toString(), fpOutputPostfix);
				FileIO.appendStringToFile(sbTotalMName.toString(), fpOutputMethodName);
				FileIO.appendStringToFile(sbTotalCount.toString(), fpOutputCount);
				sbTotalPrefix=new StringBuilder();
				sbTotalPostfix=new StringBuilder();
				sbTotalMName=new StringBuilder();
				sbTotalCount=new StringBuilder();
			}
		}
		System.out.println("complete part 2");
		
		
		
		
	}

}

class Triple{
	private String mname;
	private String prefix;
	private String postfix;
	public String getMname() {
		return mname;
	}
	public void setMname(String mname) {
		this.mname = mname;
	}
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
