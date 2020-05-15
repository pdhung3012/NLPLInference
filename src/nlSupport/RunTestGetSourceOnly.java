package nlSupport;

import consts.PathConstanct;
import utils.StanfordLemmatizer;

public class RunTestGetSourceOnly {
	public static String[] arrLibraryPrefix={"android","com.google.gwt","com.thoughtworks.xstream","org.hibernate","org.joda.time","java"};
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String inPath=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport/ICV/b_code-missingMIs/";
		String outPath=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport/step0_sequence/";
		StanfordLemmatizer lemm=new StanfordLemmatizer();
		OnlySourceSequenceGeneratorForNL mcsg=new OnlySourceSequenceGeneratorForNL(inPath,arrLibraryPrefix,lemm);
		mcsg.generateSequences(outPath);
		mcsg.generateAlignment(true);
	}

}
