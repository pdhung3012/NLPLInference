 package invocations;

import java.io.File;
import java.util.HashMap;

import consts.PathConstanct;
import utils.FileIO;

public class GenerateTrainTestTuneAsText {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fopSequence=PathConstanct.PATH_OUTPUT_IDENTIFER_PROJECT;
		String fopProjectTTTList=PathConstanct.PATH_PROJECT_TRAIN_TEST_NAME;
		String fopOutput=PathConstanct.PATH_PROJECT_TTT_DATA;
		
		String[] arrTrainName=FileIO.readStringFromFile(fopProjectTTTList+"train.project.txt").split("\n");
		String[] arrTuneName=FileIO.readStringFromFile(fopProjectTTTList+"tune.project.txt").split("\n");
		String[] arrTestName=FileIO.readStringFromFile(fopProjectTTTList+"test.project.txt").split("\n");
		
		FileIO.writeStringToFile("", fopOutput+"train.locations.txt");
		FileIO.writeStringToFile("", fopOutput+"train.source.txt");
		FileIO.writeStringToFile("", fopOutput+"train.target.txt");
		
		for(int i=0;i<arrTrainName.length;i++){
			String fopProjSeq=fopSequence+arrTrainName[i]+File.separator;
			String fpLocation=fopProjSeq
					+ File.separator+"locations.txt";
			String fpSource=fopProjSeq
					+ File.separator+"source.txt";
			String fpTarget=fopProjSeq
					+ File.separator+"target.txt";
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
				FileIO.appendStringToFile(strNewTarget, fopOutput+"train.target.txt");
				String strSource=FileIO.readStringFromFile(fpSource);
				FileIO.appendStringToFile(strSource, fopOutput+"train.source.txt");
				String strLocation=FileIO.readStringFromFile(fpLocation);
				FileIO.appendStringToFile(strLocation, fopOutput+"train.locations.txt");
				
			}
			System.out.println(i + " finish " + arrTrainName[i]
					+ " size " );
		}
		
		FileIO.writeStringToFile("", fopOutput+"tune.locations.txt");
		FileIO.writeStringToFile("", fopOutput+"tune.source.txt");
		FileIO.writeStringToFile("", fopOutput+"tune.target.txt");
		
		for(int i=0;i<arrTuneName.length;i++){
			String fopProjSeq=fopSequence+arrTuneName[i]+File.separator;
			String fpLocation=fopProjSeq
					+ File.separator+"locations.txt";
			String fpSource=fopProjSeq
					+ File.separator+"source.txt";
			String fpTarget=fopProjSeq
					+ File.separator+"target.txt";
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
				FileIO.appendStringToFile(strNewTarget, fopOutput+"tune.target.txt");
				String strSource=FileIO.readStringFromFile(fpSource);
				FileIO.appendStringToFile(strSource, fopOutput+"tune.source.txt");
				String strLocation=FileIO.readStringFromFile(fpLocation);
				FileIO.appendStringToFile(strLocation, fopOutput+"tune.locations.txt");
				
			}
			System.out.println(i + " tune finish " + arrTuneName[i]
					+ " size " );
		}
		
		FileIO.writeStringToFile("", fopOutput+"test.locations.txt");
		FileIO.writeStringToFile("", fopOutput+"test.source.txt");
		FileIO.writeStringToFile("", fopOutput+"test.target.txt");
		
		for(int i=0;i<arrTestName.length;i++){
			String fopProjSeq=fopSequence+arrTestName[i]+File.separator;
			String fpLocation=fopProjSeq
					+ File.separator+"locations.txt";
			String fpSource=fopProjSeq
					+ File.separator+"source.txt";
			String fpTarget=fopProjSeq
					+ File.separator+"target.txt";
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
				FileIO.appendStringToFile(strNewTarget, fopOutput+"test.target.txt");
				String strSource=FileIO.readStringFromFile(fpSource);
				FileIO.appendStringToFile(strSource, fopOutput+"test.source.txt");
				String strLocation=FileIO.readStringFromFile(fpLocation);
				FileIO.appendStringToFile(strLocation, fopOutput+"test.locations.txt");
				
			}
			System.out.println(i + " test finish " + arrTuneName[i]
					+ " size " );
		}
		
		
	}

}
