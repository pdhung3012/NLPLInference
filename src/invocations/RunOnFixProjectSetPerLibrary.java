package invocations;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import utils.FileIO;
import utils.FileUtil;
import utils.StanfordLemmatizer;
import consts.PathConstanct;

public class RunOnFixProjectSetPerLibrary {

	private static final int MYTHREADS = 8;
	public static String[] arrLibraryPrefix={"android","com.google.gwt","com.thoughtworks.xstream","org.hibernate","org.joda.time","java"};
	public static String[] arrCompactLibaryName={"android",
		"gwt","xstream",
		"hibernate","jodatime"};
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int selectLibIndex=1;
		String fopProjectName=PathConstanct.PATH_PROJECT_TRAIN_TEST_NAME;
		String fopOutput=PathConstanct.PATH_OUTPUT_IDENTIFER_PROJECT;
		String inputProjectPath = PathConstanct.PATH_INPUT_IDENTIFER_PROJECT;
		StanfordLemmatizer lemm=new StanfordLemmatizer();
		String fpOutputLog=fopOutput+File.separator+"alog.txt";
		if(!new File(fpOutputLog).isFile()){
			FileUtil.writeToFile(fpOutputLog, "");
		}
		ExecutorService executor = Executors.newFixedThreadPool(MYTHREADS);
		
		String[] arrTrainProjects=FileIO.readStringFromFile(fopProjectName+"train.project.txt").split("\n");
		String[] arrTestProjects=FileIO.readStringFromFile(fopProjectName+"test.project.txt").split("\n");
		String[] arrTuneProjects=FileIO.readStringFromFile(fopProjectName+"tune.project.txt").split("\n");
		
		ArrayList<String> listRequiredProjects=new ArrayList<>();
		StringBuilder sbResult=new StringBuilder();
		
		for(int i=(selectLibIndex)*160;i<(selectLibIndex+1)*160;i++){
			listRequiredProjects.add(arrTrainProjects[i]);
			sbResult.append(arrTrainProjects[i]+"\n");
		}
		
		for(int i=(selectLibIndex)*20;i<(selectLibIndex+1)*20;i++){
			listRequiredProjects.add(arrTestProjects[i]);
			sbResult.append(arrTestProjects[i]+"\n");
		}
		
		for(int i=(selectLibIndex)*20;i<(selectLibIndex+1)*20;i++){
			listRequiredProjects.add(arrTuneProjects[i]);
			sbResult.append(arrTuneProjects[i]+"\n");
		}
		
		FileIO.writeStringToFile(sbResult.toString(), fopOutput+"_"+arrLibraryPrefix[selectLibIndex]+".txt");
		
		for(int i=0;i<listRequiredProjects.size();i++){
			String folderName=listRequiredProjects.get(i).replaceFirst("_","-");
			String itemInputPath=inputProjectPath+folderName+File.separator;
			String itemOutputPath=fopOutput+folderName+File.separator;
			ExtractSequenceForProjectRunnable thread = new ExtractSequenceForProjectRunnable(itemInputPath,
					itemOutputPath, arrLibraryPrefix,i,fpOutputLog,lemm);
//			thread.run();
			executor.execute(thread);
		}
	}

}
