package invocations;

import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedHashSet;

import utils.FileIO;
import consts.PathConstanct;

public class GenerateVocabularyPerLibrary {

	public static void getVocabulary(String fpFile,String fpVocab){
		HashSet<String> setVocab=new LinkedHashSet<String>();
		try (BufferedReader br = Files.newBufferedReader(Paths.get(fpFile), StandardCharsets.UTF_8)) {
		    
			for (String line = null; (line = br.readLine()) != null;) {
		    	String[] arrItems=line.trim().split("\\s+");
				for(int j=0;j<arrItems.length;j++){
					setVocab.add(arrItems[j]);
				}
		    }
		} catch(Exception ex){
			ex.printStackTrace();
		}
		
		FileIO.writeStringToFile("",fpVocab);
		StringBuilder sbResult=new StringBuilder();
		sbResult.append("<unk>\n<s>\n</s>\n");
		int index=0;
		for(String strItem:setVocab){
			index++;
			sbResult.append(strItem+"\n");
			if(index%1000000==0){
				FileIO.appendStringToFile(sbResult.toString(),fpVocab);
				sbResult=new StringBuilder();
			}
		}
		if(!sbResult.toString().isEmpty()){
			FileIO.appendStringToFile(sbResult.toString(),fpVocab);
//			sbResult=new StringBuilder();
		}
		
	}
	
	public static String[] arrCompactLibaryName={"android",
		"gwt","xstream",
		"hibernate","jodatime"};
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int selectLibIndex=1;
		String fopVocab=PathConstanct.PATH_PROJECT_TTT_ALIAS+arrCompactLibaryName[selectLibIndex]+File.separator;
		String fopSplit=PathConstanct.PATH_PROJECT_TTT_SPLIT_ALIAS+arrCompactLibaryName[selectLibIndex]+File.separator;
		new File(fopSplit).mkdir();
		getVocabulary(fopVocab+"train.source.txt", fopSplit+"vocab.source.txt");
		getVocabulary(fopVocab+"train.target.txt", fopSplit+"vocab.target.txt");
	}

}
