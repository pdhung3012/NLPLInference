package download;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;

import utils.FileIO;
import consts.PathConstanct;

public class SplitSetRequiredProjectToLibrary {

	public static String[] arr5LibPrefix={"android","com.google.gwt","com.thoughtworks.xstream","org.hibernate","org.joda.time"};

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String folderForLib=PathConstanct.PATH_PROJECT_TRAIN_TEST_NAME+"5LibSequence"+File.separator;
		String folderStSequence=PathConstanct.PATH_PROJECT_STATTYPE_TYPE_SEQUENCES;
		String fpRequiredProject=PathConstanct.PATH_PROJECT_TRAIN_TEST_NAME+"requiredProjects.txt";
		String fpOverlapProjectInST=PathConstanct.PATH_PROJECT_TRAIN_TEST_NAME+"overlapBetweenLib.txt";
		
		new File(folderForLib).mkdir();
		
		HashMap<String,String> mapProject=new LinkedHashMap<String, String>();
		StringBuilder sbOverlap=new StringBuilder();
		for(int i=0;i<arr5LibPrefix.length;i++){
			String folderItem5Lib=folderStSequence+arr5LibPrefix[i]+File.separator;
			File fopItem=new File(folderItem5Lib);
			File[] arrItem=fopItem.listFiles();
			for(int j=0;j<arrItem.length;j++){
				if(arrItem[j].isDirectory()){
					String projName=arrItem[j].getName().replaceFirst("_","-");
					if(projName.equals("apache-incubator-streams")){
						projName="apache-streams";
					}
					String libItem=mapProject.get(projName);
					if(libItem==null){
						mapProject.put(projName, arr5LibPrefix[i]);
					} else{
						sbOverlap.append(projName+"\t"+libItem+"\t"+arr5LibPrefix[i]+"\n");
					}
				}
			}
			FileIO.writeStringToFile("", folderForLib+ arr5LibPrefix[i]+".txt");
		}
		FileIO.writeStringToFile(sbOverlap.toString(), fpOverlapProjectInST);
		
		String[] arrProjs=FileIO.readStringFromFile(fpRequiredProject).split("\n");
		for(int i=0;i<arrProjs.length;i++){
			String libName=mapProject.get(arrProjs[i]);
			if(libName!=null){
				FileIO.appendStringToFile(arrProjs[i]+"\n", folderForLib+ libName+".txt");
			}
		}
		
		
		
	}

}
