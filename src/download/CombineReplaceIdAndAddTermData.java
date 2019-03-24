package download;

import invocations.CombineSequenceFromProjects;
import invocations.CreateTrainingData;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import utils.FileIO;
import utils.FileUtil;
import utils.MapUtil;
import consts.PathConstanct;

public class CombineReplaceIdAndAddTermData {

	public static String[] arr5LibPrefix = { "android", "com.google.gwt",
			"com.thoughtworks.xstream", "org.hibernate", "org.joda.time" };

	public static String SplitInvocationCharacter = "\\$\\%\\$";

	public static void extractGoodPercentage(ArrayList<Integer> listNumbers,
			ArrayList<String> listLocations, ArrayList<String> listSources,
			ArrayList<String> listFilterLocations,
			ArrayList<String> listFilterSources) {
		for (int i = 0; i < listSources.size(); i++) {
			String[] arrLocationInfo = listLocations.get(i).trim()
					.split("\\s+");
			String[] arrTokenInSource = listSources.get(i).trim().split("\\s+");
			String percentageResolve = arrLocationInfo[arrLocationInfo.length - 1];
			if (arrTokenInSource.length <= PathConstanct.NUM_CHARACTER_MAXIMUM
					&& percentageResolve.equals("100%")) {
				listNumbers.add(i);
				listFilterLocations.add(listLocations.get(i));
				listFilterSources.add(listSources.get(i));
			}
		}
	}

	public static void extractGoodPercentageAndTarget(
			ArrayList<Integer> listNumbers, ArrayList<String> listLocations,
			ArrayList<String> listSources, ArrayList<String> listTarget,
			ArrayList<String> listFilterLocations,
			ArrayList<String> listFilterSources) {
		HashSet<Integer> setHaveMethods = new LinkedHashSet<>();
		for (int i = 0; i < listTarget.size(); i++) {
			String strItem = listTarget.get(i);
			if (strItem.contains("E-")) {
				setHaveMethods.add(i);
			}
		}

		for (int i = 0; i < listSources.size(); i++) {
			if (!setHaveMethods.contains(i)) {
				continue;
			}
			String[] arrLocationInfo = listLocations.get(i).trim()
					.split("\\s+");
			String[] arrTokenInSource = listSources.get(i).trim().split("\\s+");
			String percentageResolve = arrLocationInfo[arrLocationInfo.length - 1];
			if (arrTokenInSource.length <= PathConstanct.NUM_CHARACTER_MAXIMUM
					&& percentageResolve.equals("100%")) {
				listNumbers.add(i);
				listFilterLocations.add(listLocations.get(i));
				listFilterSources.add(listSources.get(i));
			}
		}
	}

	public static String convertFromArrayListToString(ArrayList<String> list) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			sb.append(list.get(i) + "\n");
		}
		return sb.toString();
	}

	public static String getFilterSourceTarget(ArrayList<Integer> lstNumber,
			String input, String fpTempWrite) {
		StringBuilder sb = new StringBuilder();
		String[] arrInput = input.split("\n");
		FileIO.writeStringToFile("", fpTempWrite);
		for (int i = 0; i < lstNumber.size(); i++) {
			sb.append(arrInput[lstNumber.get(i)] + "\n");
			if ((i + 1) % 100000 == 0 || i + 1 == lstNumber.size()) {
				FileIO.appendStringToFile(sb.toString(), fpTempWrite);
				sb = new StringBuilder();
			}
		}
		String str = FileIO.readStringFromFile(fpTempWrite);
		return str;
	}

	public static String getFilterAlignment(ArrayList<Integer> lstNumber,
			String input, String fpTempWrite) {
		StringBuilder sb = new StringBuilder();
		String[] arrInput = input.split("\n");
		FileIO.writeStringToFile("", fpTempWrite);
		for (int i = 0; i < lstNumber.size(); i++) {
			sb.append(arrInput[lstNumber.get(i) * 3] + "\n");
			sb.append(arrInput[lstNumber.get(i) * 3 + 1] + "\n");
			sb.append(arrInput[lstNumber.get(i) * 3 + 2] + "\n");
			if ((i + 1) % 50000 == 0 || i + 1 == lstNumber.size()) {
				FileIO.appendStringToFile(sb.toString(), fpTempWrite);
				sb = new StringBuilder();
			}
		}
		String str = FileIO.readStringFromFile(fpTempWrite);
		return str;
	}
	
	public static void collectSourceAndTargetTerm(HashMap<String,String> mapIn,HashMap<String,String> mapSource,HashMap<String,String> mapTarget){
		for(String key:mapIn.keySet()){
			String info=mapIn.get(key);
			String[] arrLine=info.split(SplitInvocationCharacter);
			for(int i=0;i<arrLine.length;i++){
				if(arrLine.length>=4){
					String strSource=arrLine[arrLine.length-4];
					String strTarget=arrLine[arrLine.length-4];
					mapSource.put(key, strSource);
					mapTarget.put(key, strTarget);
				}
			}
		}
	}
	
	public static void addTermToOriginSourceAndTarget(String strFilterSource,String strFilterTarget,HashMap<String,String> mapSource,HashMap<String,String> mapTarget,String fpTermSource,String fpTermTarget,String fpAppendSource,String fpAppendTarget){
		String[] arrSource=strFilterSource.split("\n");
		String[] arrTarget=strFilterTarget.split("\n");
		StringBuilder sbTotalSource=new StringBuilder();
		StringBuilder sbTotalTarget=new StringBuilder();
		for(int i=0;i<arrSource.length;i++){
			String[] arrItS=arrSource[i].split("\\s+");
			String[] arrItT=arrTarget[i].split("\\s+");
			int len=arrItS.length;
			int countIden=0;
			for(int j=0;j<arrItS.length;j++){
				if(arrItS[j].endsWith("#identifier")){
//					String targetID=arrItT[j];
					countIden++;
				}
			}
			int maxTermRequired=(int)Math.floor((255-len)*1.0/countIden);
			
			StringBuilder sbNewSource=new StringBuilder();
			StringBuilder sbNewTarget=new StringBuilder();
			
			for(int j=0;j<arrItS.length;j++){
				sbNewSource.append(arrItS[j]+" ");
				sbNewTarget.append(arrItT[j]+" ");
				if(arrItS[j].endsWith("#identifier")){
					String targetID=arrItT[j];
					String[] arrTermSource=mapSource.get(targetID).trim().split("\\s+");
					String[] arrTermTarget=mapTarget.get(targetID).trim().split("\\s+");
					int min=Math.min(maxTermRequired, arrTermSource.length);
					for(int k=0;k<min;k++){
						sbNewSource.append(arrTermSource[k]+" ");
						sbNewTarget.append(arrTermTarget[k]+" ");
					}
					
				}
				sbTotalSource.append(sbNewSource.toString().trim()+"\n");
				sbTotalTarget.append(sbNewTarget.toString().trim()+"\n");
			}
			String strTS=sbTotalSource.toString();
			String strTT=sbTotalTarget.toString();
			FileIO.writeStringToFile(strTS, fpTermSource);
			FileIO.writeStringToFile(strTT, fpTermTarget);
			FileIO.appendStringToFile(strTS, fpAppendSource);
			FileIO.appendStringToFile(strTT, fpAppendTarget);
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// System.exit(0);
		String fopSequence = PathConstanct.PATH_OUTPUT_IDENTIFER_PROJECT;
		String fopProjectTTTLibrary = PathConstanct.PATH_PROJECT_TRAIN_TEST_NAME
				+ "5LibSequence" + File.separator;
		String fopOutput = PathConstanct.PATH_PROJECT_TTT_ADDTERMS_DATA;

		String fpTempForWrite = PathConstanct.PATH_PROJECT_TTT_ADDTERMS_DATA
				+ "tempForWrite.txt";
		String fpIdAndMapContent = PathConstanct.PATH_OUTPUT_IDENTIFER_PROJECT
				+ "a_mapTotalIdAndContent.txt";
		int maximumLine = 100000;

		HashMap<String, String> mapIdAndTotalContent = MapUtil
				.getHashMapFromFile(fpIdAndMapContent);
		HashMap<String,String> mapAddTermSource=new LinkedHashMap<>();
		HashMap<String,String> mapAddTermTarget=new LinkedHashMap<>();
		collectSourceAndTargetTerm(mapIdAndTotalContent, mapAddTermSource, mapAddTermTarget);
		

		for (int i = 0; i < arr5LibPrefix.length; i++) {
			/*
			 * if(i!=3) { continue; }
			 */
			String[] arrProjLibName = FileIO.readStringFromFile(
					fopProjectTTTLibrary + arr5LibPrefix[i] + ".txt").split(
					"\n");
			FileIO.writeStringToFile("", fopOutput + arr5LibPrefix[i]
					+ ".locations.txt");
			FileIO.writeStringToFile("", fopOutput + arr5LibPrefix[i]
					+ ".source.txt");
			FileIO.writeStringToFile("", fopOutput + arr5LibPrefix[i]
					+ ".target.txt");
			FileIO.writeStringToFile("", fopOutput + arr5LibPrefix[i]
					+ ".training.s-t.A3");
			FileIO.writeStringToFile("", fopOutput + arr5LibPrefix[i]
					+ ".training.t-s.A3");
			int numLine = 0;
			for (int j = 0; j < arrProjLibName.length; j++) {

				String fopProjSeq = fopSequence + arrProjLibName[j]
						+ File.separator;
				String fpLocation = fopProjSeq + File.separator
						+ "locations.txt";
				String fpSource = fopProjSeq + File.separator + "source.txt";
				String fpTarget = fopProjSeq + File.separator + "target.txt";
				// String fpTrainingSoTa=fopProjSeq
				// + File.separator+"/-alignment/training.s-t.A3";
				// String fpTrainingReverse=fopProjSeq
				// + File.separator+"/-alignment/training.t-s.A3";
				String fpMapIdenAndId = fopProjSeq + File.separator + "hash"
						+ File.separator + "mapIdenAndId.txt";

				File fileMapIdenAndId = new File(fpMapIdenAndId);
				if (fileMapIdenAndId.isFile()) {
					String fpMapReplaceId = fopProjSeq + File.separator
							+ "hash" + File.separator + "mapReplaceId.txt";

					// String strSource=FileIO.readStringFromFile(fpSource);
					// String strLocation=FileIO.readStringFromFile(fpLocation);
					ArrayList<String> listSource = FileUtil
							.getFileStringArray(fpSource);
					ArrayList<String> listTarget = FileUtil
							.getFileStringArray(fpTarget);
					ArrayList<String> listLocation = FileUtil
							.getFileStringArray(fpLocation);
					numLine += listLocation.size();

					ArrayList<Integer> listNumbers = new ArrayList<Integer>();
					ArrayList<String> listFilterLocations = new ArrayList<>();
					ArrayList<String> listFilterSources = new ArrayList<>();
					extractGoodPercentageAndTarget(listNumbers, listLocation,
							listSource, listTarget, listFilterLocations,
							listFilterSources);
					String strFilterSource = convertFromArrayListToString(listFilterSources);
					String strFilterLocation = convertFromArrayListToString(listFilterLocations);
					
					FileIO.appendStringToFile(strFilterLocation, fopOutput
							+ arr5LibPrefix[i] + ".locations.txt");

					HashMap<String, String> mapReplaceId = CombineSequenceFromProjects
							.getMapFromFileStringString(fpMapReplaceId);
					String strTarget = FileIO.readStringFromFile(fpTarget);
					String strNewTarget = CreateTrainingData
							.replaceTargetWithTotalId(strTarget, mapReplaceId);
					FileIO.writeStringToFile(strNewTarget, fopProjSeq
							+ "totalIdTarget.txt");
					String strFilterForNewTarget = getFilterSourceTarget(
							listNumbers, strNewTarget, fpTempForWrite);
					String fpTermSource=fopProjSeq+"terms.source.txt";
					String fpTermTarget=fopProjSeq+"terms.target.txt";
					addTermToOriginSourceAndTarget(strFilterSource, strFilterForNewTarget, mapAddTermSource, mapAddTermTarget, fpTermSource, fpTermTarget, fopOutput+arr5LibPrefix[i]+".source.txt", fopOutput+arr5LibPrefix[i]+".target.txt");

					// String
					// strTrainSoTa=FileIO.readFromLargeFile(fpTrainingSoTa);
					// String
					// strNewTrainSoTa=CreateTrainingData.replaceTargetWithTotalId(strTrainSoTa,
					// mapReplaceId,fpTempForWrite);
					// String
					// strFilterTrainSoTa=getFilterAlignment(listNumbers,strNewTrainSoTa,fpTempForWrite);
					// FileIO.writeStringToFile(strNewTrainSoTa,
					// fopProjSeq+"total.training.s-t.txt");
					// FileIO.appendStringToFile(strFilterTrainSoTa,
					// fopOutput+arr5LibPrefix[i]+".training.s-t.A3");
					//
					// String
					// strTrainReverse=FileIO.readFromLargeFile(fpTrainingReverse);
					// String
					// strNewTrainReverse=CreateTrainingData.replaceTargetWithTotalId(strTrainReverse,
					// mapReplaceId,fpTempForWrite);
					// String
					// strFilterTrainReverse=getFilterAlignment(listNumbers,strNewTrainReverse,fpTempForWrite);
					// FileIO.writeStringToFile(strNewTrainReverse,
					// fopProjSeq+"total.training.t-s.txt");
					// FileIO.appendStringToFile(strFilterTrainReverse,
					// fopOutput+arr5LibPrefix[i]+".training.t-s.A3");

					// if(numLine>maximumLine) break;
					//

				}
				System.out.println(j + " finish " + arrProjLibName[j]
						+ " size ");
			}

		}

	}

}
