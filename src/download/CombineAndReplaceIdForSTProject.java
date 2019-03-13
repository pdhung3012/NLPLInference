package download;

import invocations.CombineSequenceFromProjects;
import invocations.CreateTrainingData;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import utils.FileIO;
import utils.FileUtil;
import consts.PathConstanct;

public class CombineAndReplaceIdForSTProject {

	public static String[] arr5LibPrefix={"android","com.google.gwt","com.thoughtworks.xstream","org.hibernate","org.joda.time"};

	public static void extractGoodPercentage(ArrayList<Integer> listNumbers,ArrayList<String> listLocations,ArrayList<String> listSources,ArrayList<String> listFilterLocations,ArrayList<String> listFilterSources){
		for(int i=0;i<listSources.size();i++){
			String[] arrLocationInfo=listLocations.get(i).trim().split("\\s+");
			String[] arrTokenInSource=listSources.get(i).trim().split("\\s+");
			String percentageResolve=arrLocationInfo[arrLocationInfo.length-1];
			if(arrTokenInSource.length<=PathConstanct.NUM_CHARACTER_MAXIMUM && percentageResolve.equals("100%")){
				listNumbers.add(i);
				listFilterLocations.add(listLocations.get(i));
				listFilterSources.add(listSources.get(i));
			}
		}
	}
	
	public static String convertFromArrayListToString(ArrayList<String> list){
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<list.size();i++){
			sb.append(list.get(i)+"\n");
		}
		return sb.toString();
	}
	
	public static String getFilterSourceTarget(ArrayList<Integer> lstNumber,String input,String fpTempWrite){
		StringBuilder sb=new StringBuilder();
		String[] arrInput=input.split("\n");
		FileIO.writeStringToFile("", fpTempWrite);
		for(int i=0;i<lstNumber.size();i++){
			sb.append(arrInput[lstNumber.get(i)]+"\n");
			if((i+1)%100000==0||i+1==lstNumber.size()){
				FileIO.appendStringToFile(sb.toString(), fpTempWrite);
				sb=new StringBuilder();
			}
		}
		String str=FileIO.readStringFromFile(fpTempWrite);
		return str;
	}
	
	public static String getFilterAlignment(ArrayList<Integer> lstNumber,String input,String fpTempWrite){
		StringBuilder sb=new StringBuilder();
		String[] arrInput=input.split("\n");
		FileIO.writeStringToFile("", fpTempWrite);
		for(int i=0;i<lstNumber.size();i++){
			sb.append(arrInput[lstNumber.get(i)*3]+"\n");
			sb.append(arrInput[lstNumber.get(i)*3+1]+"\n");
			sb.append(arrInput[lstNumber.get(i)*3+2]+"\n");
			if((i+1)%50000==0||i+1==lstNumber.size()){
				FileIO.appendStringToFile(sb.toString(), fpTempWrite);
				sb=new StringBuilder();
			}
		}
		String str=FileIO.readStringFromFile(fpTempWrite);
		return str;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.exit(0);
		String fopSequence=PathConstanct.PATH_OUTPUT_IDENTIFER_PROJECT;
		String fopProjectTTTLibrary=PathConstanct.PATH_PROJECT_TRAIN_TEST_NAME+"5LibSequence"+File.separator;
		String fopOutput=PathConstanct.PATH_PROJECT_TTT_DATA;
		
		String fpTempForWrite=PathConstanct.PATH_PROJECT_TTT_DATA+"tempForWrite.txt";
		
		
		for(int i=0;i<arr5LibPrefix.length;i++){
			if(i<=3) {
				continue;
			}
			String[] arrProjLibName=FileIO.readStringFromFile(fopProjectTTTLibrary+arr5LibPrefix[i]+".txt").split("\n");
			FileIO.writeStringToFile("", fopOutput+arr5LibPrefix[i]+".locations.txt");
			FileIO.writeStringToFile("", fopOutput+arr5LibPrefix[i]+".source.txt");
			FileIO.writeStringToFile("", fopOutput+arr5LibPrefix[i]+".target.txt");
			FileIO.writeStringToFile("", fopOutput+arr5LibPrefix[i]+".training.s-t.A3");
			FileIO.writeStringToFile("", fopOutput+arr5LibPrefix[i]+".training.t-s.A3");
			
			for(int j=0;j<arrProjLibName.length;j++){
			
				
				String fopProjSeq=fopSequence+arrProjLibName[j]+File.separator;
				String fpLocation=fopProjSeq
						+ File.separator+"locations.txt";
				String fpSource=fopProjSeq
						+ File.separator+"source.txt";
				String fpTarget=fopProjSeq
						+ File.separator+"target.txt";
				String fpTrainingSoTa=fopProjSeq
						+ File.separator+"/-alignment/training.s-t.A3";
				String fpTrainingReverse=fopProjSeq
						+ File.separator+"/-alignment/training.t-s.A3";
				String fpMapIdenAndId = fopProjSeq
						+ File.separator + "hash" + File.separator
						+ "mapIdenAndId.txt";
				
				File fileMapIdenAndId = new File(fpMapIdenAndId);
				if (fileMapIdenAndId.isFile()) {
					String fpMapReplaceId = fopProjSeq
							+ File.separator + "hash" + File.separator
							+ "mapReplaceId.txt";
					
//					String strSource=FileIO.readStringFromFile(fpSource);					
//					String strLocation=FileIO.readStringFromFile(fpLocation);
					ArrayList<String> listSource=FileUtil.getFileStringArray(fpSource);
					ArrayList<String> listLocation=FileUtil.getFileStringArray(fpLocation);
					
					ArrayList<Integer> listNumbers=new ArrayList<Integer>();
					ArrayList<String> listFilterLocations=new ArrayList<>();
					ArrayList<String> listFilterSources=new ArrayList<>();
					extractGoodPercentage(listNumbers,listLocation,listSource,listFilterLocations,listFilterSources);
					String strFilterSource=convertFromArrayListToString(listFilterSources);
					String strFilterLocation=convertFromArrayListToString(listFilterLocations);
					
					FileIO.appendStringToFile(strFilterSource, fopOutput+arr5LibPrefix[i]+".source.txt");
					FileIO.appendStringToFile(strFilterLocation, fopOutput+arr5LibPrefix[i]+".locations.txt");
					
					HashMap<String,String> mapReplaceId=CombineSequenceFromProjects.getMapFromFileStringString(fpMapReplaceId);				
					String strTarget=FileIO.readStringFromFile(fpTarget);
					String strNewTarget=CreateTrainingData.replaceTargetWithTotalId(strTarget, mapReplaceId);
					FileIO.writeStringToFile(strNewTarget, fopProjSeq+"totalIdTarget.txt");
					
					String strFilterForNewTarget=getFilterSourceTarget(listNumbers,strNewTarget,fpTempForWrite);
					FileIO.appendStringToFile(strFilterForNewTarget, fopOutput+arr5LibPrefix[i]+".target.txt");
					
					String strTrainSoTa=FileIO.readFromLargeFile(fpTrainingSoTa);
					String strNewTrainSoTa=CreateTrainingData.replaceTargetWithTotalId(strTrainSoTa, mapReplaceId,fpTempForWrite);
					String strFilterTrainSoTa=getFilterAlignment(listNumbers,strNewTrainSoTa,fpTempForWrite);
					FileIO.writeStringToFile(strNewTrainSoTa, fopProjSeq+"total.training.s-t.txt");
					FileIO.appendStringToFile(strFilterTrainSoTa, fopOutput+arr5LibPrefix[i]+".training.s-t.A3");

					String strTrainReverse=FileIO.readFromLargeFile(fpTrainingReverse);
					String strNewTrainReverse=CreateTrainingData.replaceTargetWithTotalId(strTrainReverse, mapReplaceId,fpTempForWrite);
					String strFilterTrainReverse=getFilterAlignment(listNumbers,strNewTrainReverse,fpTempForWrite);
					FileIO.writeStringToFile(strNewTrainReverse, fopProjSeq+"total.training.t-s.txt");
					FileIO.appendStringToFile(strFilterTrainReverse, fopOutput+arr5LibPrefix[i]+".training.t-s.A3");
					
					
					//
					
				}
				System.out.println(j + " finish " + arrProjLibName[j]
						+ " size " );
			}
			
			
		}

	}

}
