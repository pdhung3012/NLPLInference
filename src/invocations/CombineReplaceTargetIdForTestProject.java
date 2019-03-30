package invocations;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;

import utils.FileIO;
import consts.PathConstanct;
import entities.InvocationObject;

public class CombineReplaceTargetIdForTestProject {

	public static HashMap<String,String> getMapFromFileStringString(String fp){
		HashMap<String,String> map=new LinkedHashMap<String, String>();
		String[] arrContent=FileIO.readStringFromFile(fp).split("\n");
		for(int i=0;i<arrContent.length;i++){
			String[] arrItem=arrContent[i].split("\t");
			if(arrItem.length>=2){
				map.put(arrItem[0],arrItem[1]);
			}
		}
		return map;
	}
	
	public static HashMap<String,Integer> getMapFromFileStringInt(String fp){
		HashMap<String,Integer> map=new LinkedHashMap<String, Integer>();
		String[] arrContent=FileIO.readStringFromFile(fp).split("\n");
		for(int i=0;i<arrContent.length;i++){
			String[] arrItem=arrContent[i].split("\t");
			if(arrItem.length>=2){
				map.put(arrItem[0],Integer.parseInt(arrItem[1]));
			}
		}
		return map;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String inputFolder=PathConstanct.PATH_PROJECT_TTT_TEST_IDENTIFIER_PROJECT;
		String mapTrainFolder=PathConstanct.PATH_PROJECT_TTT_TEST_IDENTIFIER_PROJECT+"orgTrainMap"+File.separator;
		String mapTestFolder=PathConstanct.PATH_PROJECT_TTT_TEST_IDENTIFIER_PROJECT+"orgTestMap"+File.separator;
//		String totalSignatureFolder=PathConstanct.PATH_COMBINE_SIG_PROJECT;
		
		File fInFolder=new File(inputFolder);
		
		File[] arrIn=fInFolder.listFiles();
HashMap<String,Integer> mapTotalIdenAndAppear=getMapFromFileStringInt(mapTrainFolder+"a_mapTotalIdenAppear.txt");
		HashMap<String,String> mapTotalIdenAndId=getMapFromFileStringString(mapTrainFolder+"a_mapTotalIdenAndId.txt");
		HashMap<String,String> mapTotalIdAndAllContent=getMapFromFileStringString(mapTrainFolder+"a_mapTotalIdAndContent.txt");
		
		for(int i=0;i<arrIn.length;i++){
			if(arrIn[i].isDirectory()){
				String fpMapIdenAndId=arrIn[i].getAbsolutePath()+File.separator+"hash"+File.separator+"mapIdenAndId.txt";
				File fileMapIdenAndId=new File(fpMapIdenAndId);
				if(fileMapIdenAndId.isFile()){
					String fpMapIdAndIden=arrIn[i].getAbsolutePath()+File.separator+"hash"+File.separator+"mapIdAndIden.txt";
					String fpMapIdAppear=arrIn[i].getAbsolutePath()+File.separator+"hash"+File.separator+"mapIdAppear.txt";
					String fopHash=arrIn[i].getAbsolutePath()+File.separator+"hash"+File.separator;
					HashMap<String,String> mapIdenAndId=getMapFromFileStringString(fpMapIdenAndId);
					HashMap<String,String> mapIdAndIden=getMapFromFileStringString(fpMapIdAndIden);
					HashMap<String,Integer> mapIdAppear=getMapFromFileStringInt(fpMapIdAppear);
					
					HashMap<String,String> mapIdAndTotalId=new LinkedHashMap<String, String>();
					StringBuilder sbIdAndTotalId=new StringBuilder();
					for(String iden:mapIdenAndId.keySet()){
						String id=mapIdenAndId.get(iden);
						String idenAllContent=InvocationObject.getAllInfoInFile(arrIn[i].getAbsolutePath()+File.separator+"hash"+File.separator+id+".txt");
						if(mapIdAndIden.containsKey(id)){
							String totalId=mapTotalIdenAndId.get(iden);
							
							if(totalId==null){
								totalId="E-Total-"+String.format("%09d" , mapTotalIdenAndAppear.size()+1);
								int numAppear=mapIdAppear.get(id);
								mapTotalIdenAndAppear.put(iden,numAppear);
								mapTotalIdenAndId.put(iden, totalId);
								mapIdAndTotalId.put(id, totalId);
								mapTotalIdAndAllContent.put(totalId, idenAllContent);
//								FileIO.copyFileUsingChannel(new File(fopHash+id+".txt"), new File(totalSignatureFolder+totalId+".txt"));
							} else{
								int numAppear=mapIdAppear.get(id);
								// totalId=mapTotalIdenAndId.get(iden);
								mapTotalIdenAndAppear.put(iden, mapTotalIdenAndAppear.get(iden)+numAppear);
							}
							sbIdAndTotalId.append(id+"\t"+totalId+"\n");
						}
					}
					FileIO.writeStringToFile(sbIdAndTotalId.toString(), fopHash+"mapReplaceId.txt");
				}
				System.out.println(i+" finish "+arrIn[i].getName()+" size "+mapTotalIdenAndAppear.size());
			}
		}
		System.out.println("Compose total mapIdenAndAppear");
		StringBuilder sbAppear=new StringBuilder();
		StringBuilder sbId=new StringBuilder();
		int indexCount=0;
//		FileIO.writeStringToFile("", inputFolder+"a_mapTotalIdenAndId.txt");
//		FileIO.writeStringToFile("", inputFolder+"a_mapTotalIdenAppear.txt");
		for(String iden:mapTotalIdenAndAppear.keySet()){
			indexCount++;
			sbAppear.append(iden+"\t"+mapTotalIdenAndAppear.get(iden)+"\n");
			sbId.append(iden+"\t"+mapTotalIdenAndId.get(iden)+"\n");
			if(indexCount%100000==0 || indexCount==mapTotalIdenAndAppear.size()){
				FileIO.appendStringToFile(sbId.toString(), mapTestFolder+"a_mapTestTotalIdenAndId.txt");
				FileIO.appendStringToFile(sbAppear.toString(), mapTestFolder+"a_mapTestTotalIdenAppear.txt");
				sbId=new StringBuilder();
				sbAppear=new StringBuilder();
			}
			
		}
		
		StringBuilder sbTotalIdAndAllContent=new StringBuilder();
		FileIO.writeStringToFile("", mapTestFolder+"a_mapTestTotalIdAndContent.txt");
		indexCount=0;
		for(String id:mapTotalIdAndAllContent.keySet()){
			indexCount++;
			sbTotalIdAndAllContent.append(id+"\t"+mapTotalIdAndAllContent.get(id)+"\n");
			if(indexCount%100000==0 || indexCount==mapTotalIdenAndAppear.size()){
				FileIO.appendStringToFile(sbTotalIdAndAllContent.toString(), mapTestFolder+"a_mapTestTotalIdAndContent.txt");
				sbTotalIdAndAllContent=new StringBuilder();			
			}
			
		}
	}

}
