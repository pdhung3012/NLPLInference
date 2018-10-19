package utils;

import javax.imageio.stream.FileImageOutputStream;

public class ReorderingTokens {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		String folderInput="/Users/hungphan/git/7_fold/data/";
//		String fpInputSource=folderInput+"test4/test.s";
//		String fpInputTarget=folderInput+"refs/test4/ref0";
		String folderInput="/Users/hungphan/git/nmt/stattype/origin/";
		String fpInputSource=folderInput+"test.s";
		String fpInputTarget=folderInput+"test.t";
		String fpInputTransResult=folderInput+"test.tune.baseline.trans";
		String fpOutputTrans2Result=folderInput+"ordered.test.tune.baseline.trans";
		String[] arrInputSource=FileIO.readStringFromFile(fpInputSource).trim().split("\n");
		String[] arrInputTrans=FileIO.readStringFromFile(fpInputTransResult).trim().split("\n");
		StringBuilder sbResult=new StringBuilder();
		for(int i=0;i<arrInputSource.length;i++){
			String[] arrItemSource=arrInputSource[i].trim().split("\\s+");
			String[] arrItemTrans=arrInputTrans[i].trim().split("\\s+");
			String[] arrItemReordered=new String[arrInputTrans.length]; 
			for(int j=0;j<arrItemSource.length;j++){
				if(arrItemTrans[j].endsWith(arrItemSource[j])){
					arrItemReordered[j]=arrInputTrans[j];
				} else{
					// find first occurence of ordered and change position
					for(int k=j+1;k<arrItemTrans.length;k++){
						if(arrItemTrans[k].endsWith(arrItemSource[j])){
							String temp=arrItemTrans[j];
							arrItemTrans[j]=arrItemTrans[k];
							arrItemTrans[k]=temp;
							arrItemReordered[j]=arrInputTrans[j];
							break;
						}
					}
					
				}
			}
			
			String strItemOrdered="";
			for(int j=0;j<arrItemTrans.length;j++){
				strItemOrdered+=arrItemTrans[j]+" ";
			}
			sbResult.append(strItemOrdered.trim()+"\n");
					
		}
		FileIO.writeStringToFile(sbResult.toString(), fpOutputTrans2Result);
		
	}

}
