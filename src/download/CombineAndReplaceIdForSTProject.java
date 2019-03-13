package download;

import invocations.CombineSequenceFromProjects;
import invocations.CreateTrainingData;

import java.io.File;
import java.util.HashMap;

import utils.FileIO;
import consts.PathConstanct;

public class CombineAndReplaceIdForSTProject {

	public static String[] arr5LibPrefix={"android","com.google.gwt","com.thoughtworks.xstream","org.hibernate","org.joda.time"};

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fopSequence=PathConstanct.PATH_OUTPUT_IDENTIFER_PROJECT;
		String fopProjectTTTLibrary=PathConstanct.PATH_PROJECT_TRAIN_TEST_NAME+"5LibSequence"+File.separator;
		String fopOutput=PathConstanct.PATH_PROJECT_TTT_DATA;
		
		
		for(int i=0;i<arr5LibPrefix.length;i++){
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
					HashMap<String,String> mapReplaceId=CombineSequenceFromProjects.getMapFromFileStringString(fpMapReplaceId);				
					String strTarget=FileIO.readStringFromFile(fpTarget);
					String strNewTarget=CreateTrainingData.replaceTargetWithTotalId(strTarget, mapReplaceId);
					FileIO.writeStringToFile(strNewTarget, fopProjSeq+"totalIdTarget.txt");
					FileIO.appendStringToFile(strNewTarget, fopOutput+arr5LibPrefix[i]+".target.txt");
					
					String strTrainSoTa=FileIO.readStringFromFile(fpTrainingSoTa);
					String strNewTrainSoTa=CreateTrainingData.replaceTargetWithTotalId(strTrainSoTa, mapReplaceId);
					FileIO.writeStringToFile(strNewTrainSoTa, fopProjSeq+"total.training.s-t.txt");
					FileIO.appendStringToFile(strNewTrainSoTa, fopOutput+arr5LibPrefix[i]+".training.s-t.A3");

					String strTrainReverse=FileIO.readStringFromFile(fpTrainingReverse);
					String strNewTrainReverse=CreateTrainingData.replaceTargetWithTotalId(strTrainReverse, mapReplaceId);
					FileIO.writeStringToFile(strNewTrainReverse, fopProjSeq+"total.training.t-s.txt");
					FileIO.appendStringToFile(strNewTrainReverse, fopOutput+arr5LibPrefix[i]+".training.t-s.A3");
					
					
					String strSource=FileIO.readStringFromFile(fpSource);
					FileIO.appendStringToFile(strSource, fopOutput+arr5LibPrefix[i]+".source.txt");
					String strLocation=FileIO.readStringFromFile(fpLocation);
					FileIO.appendStringToFile(strLocation, fopOutput+arr5LibPrefix[i]+".locations.txt");
					
				}
				System.out.println(i + " finish " + arrProjLibName[j]
						+ " size " );
			}
			
			
		}

	}

}
