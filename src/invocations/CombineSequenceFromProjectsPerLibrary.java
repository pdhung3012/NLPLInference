package invocations;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;

import utils.FileIO;
import consts.PathConstanct;

public class CombineSequenceFromProjectsPerLibrary {

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
	
	public static String[] arrCompactLibaryName={"android",
		"gwt","xstream",
		"hibernate","jodatime"};

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int selectLibIndex=1;
		String inputFolder=PathConstanct.PATH_OUTPUT_IDENTIFER_PROJECT;
		String totalSignatureFolder=PathConstanct.PATH_COMBINE_SIG_PROJECT+arrCompactLibaryName[selectLibIndex]+File.separator;
		new File(totalSignatureFolder).mkdir();
		File fInFolder=new File(inputFolder);
		
		File[] arrIn=fInFolder.listFiles();
		HashMap<String,Integer> mapTotalIdenAndAppear=new LinkedHashMap<String, Integer>();
		HashMap<String,String> mapTotalIdenAndId=new LinkedHashMap<String, String>();
		
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
						if(mapIdAndIden.containsKey(id)){
							String totalId=mapTotalIdenAndId.get(iden);
							if(totalId==null){
								totalId="E-Total-"+String.format("%09d" , mapTotalIdenAndAppear.size()+1);
								int numAppear=mapIdAppear.get(id);
								mapTotalIdenAndAppear.put(iden,numAppear);
								mapTotalIdenAndId.put(iden, totalId);
								mapIdAndTotalId.put(id, totalId);
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
		FileIO.writeStringToFile("", inputFolder+"a_mapTotalIdenAndId.txt");
		FileIO.writeStringToFile("", inputFolder+"a_mapTotalIdenAppear.txt");
		for(String iden:mapTotalIdenAndAppear.keySet()){
			indexCount++;
			sbAppear.append(iden+"\t"+mapTotalIdenAndAppear.get(iden)+"\n");
			sbId.append(iden+"\t"+mapTotalIdenAndId.get(iden)+"\n");
			if(indexCount%100000==0 || indexCount==mapTotalIdenAndAppear.size()){
				FileIO.appendStringToFile(sbId.toString(), inputFolder+"a_mapTotalIdenAndId.txt");
				FileIO.appendStringToFile(sbAppear.toString(), inputFolder+"a_mapTotalIdenAppear.txt");
				sbId=new StringBuilder();
				sbAppear=new StringBuilder();
			}
			
		}
		
		
		
		
	}


}
