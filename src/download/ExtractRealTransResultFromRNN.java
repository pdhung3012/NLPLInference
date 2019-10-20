package download;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;

import consts.PathConstanct;
import utils.FileIO;

public class ExtractRealTransResultFromRNN {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String folderOrigin=PathConstanct.PATH_PROJECT_TTT_CUR_EVAL_DATA+"outputCombine/alias/real"+File.separator;
		String folderInput=PathConstanct.PATH_PROJECT_TTT_CUR_EVAL_DATA+"outputCombine/alias"+File.separator;
		String fpTestSourceNumber=folderInput+"test.s";
		String fpTranslatedResult=folderInput+"pred.txt";
		String fpMixOutput=folderOrigin+"pred.txt";
		String fpMapAlias=folderInput+"alias.txt";
		
//		String[] arrNumber=FileIO.readStringFromFile(fpTranslatedNumber).trim().split("\n");
		String[] arrResult=FileIO.readStringFromFile(fpTranslatedResult).trim().split("\n");
		String[] arrTestS=FileIO.readStringFromFile(fpTestSourceNumber).trim().split("\n");
		String[] arrAlias=FileIO.readStringFromFile(fpMapAlias).trim().split("\n");
		
		HashMap<Integer,String> mapAlias=new HashMap<Integer, String>();
		for(int i=0;i<arrAlias.length;i++){
			String[] arrItemAlias=arrAlias[i].trim().split("\t");
			mapAlias.put(Integer.parseInt(arrItemAlias[1]), arrItemAlias[0]);
		}
		
//		HashMap<Integer,StringBuilder> mapResult=new LinkedHashMap<Integer, StringBuilder>();
//		HashMap<Integer,StringBuilder> mapSource=new LinkedHashMap<Integer, StringBuilder>();
//		for(int i=0;i<arrResult.length;i++){
//			int itemNumber=Integer.parseInt(arrNumber[i].split("\t")[0].trim());
//			String itemResult=arrResult[i].trim();
//			if(!mapResult.containsKey(itemNumber)){
//				mapResult.put(itemNumber, new StringBuilder());
//			}
//			mapResult.get(itemNumber).append(itemResult+" ");
//			
//			String itemSource=arrTestS[i].trim();
//			if(!mapSource.containsKey(itemNumber)){
//				mapSource.put(itemNumber, new StringBuilder());
//			}
//			mapSource.get(itemNumber).append(itemSource+" ");
//		}
		
		StringBuilder sbResult=new StringBuilder();
		for(int i=0;i<100;i++){
			String itemArrayNumber=arrResult[i].trim();
			String itemArraySource=arrTestS[i].trim();
			String[] arrItemNum=itemArrayNumber.split("\\s+");
			String[] arrItemSource=itemArraySource.split("\\s+");
			String strResult="";
			System.out.println(i+"\n"+itemArraySource);
			for(int j=0;j<arrItemSource.length;j++){
				String strNum="";
				if(j<arrItemNum.length){
					if(arrItemSource[j].equals("<unk>")){
						strNum=arrItemSource[j].trim();
					} else{
						strNum=arrItemNum[j].trim();
					}
					
				} else{
					strNum=arrItemSource[j].trim();
				}
				strResult+=mapAlias.get(Integer.parseInt(strNum))+" ";
				
			}
			sbResult.append(strResult.trim()+"\n");			
		}
		FileIO.writeStringToFile(sbResult.toString(),fpMixOutput);
		
	}

}