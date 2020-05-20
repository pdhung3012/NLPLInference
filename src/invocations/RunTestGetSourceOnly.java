package invocations;

import consts.PathConstanct;
import utils.StanfordLemmatizer;

public class RunTestGetSourceOnly {
	public static String[] arrLibraryPrefix={"android","com.google.gwt","com.thoughtworks.xstream","org.hibernate","org.joda.time","java"};
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String inPath=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlProcess/";
		String outPath=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlProcess/step0_parseFiles/";
		StanfordLemmatizer lemm=new StanfordLemmatizer();
		OnlySourceSequenceGenerator mcsg=new OnlySourceSequenceGenerator(inPath,arrLibraryPrefix,lemm);
		mcsg.generateSequences(outPath);
		mcsg.generateAlignment(true);
	}

}
