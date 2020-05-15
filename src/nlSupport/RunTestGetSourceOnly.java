package nlSupport;

import consts.PathConstanct;
import invocations.OnlySourceSequenceGenerator;
import utils.StanfordLemmatizer;

public class RunTestGetSourceOnly {
	public static String[] arrLibraryPrefix={"android","com.google.gwt","com.thoughtworks.xstream","org.hibernate","org.joda.time","java"};
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String inPath=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport/ICV/";
		String outPath=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport/step0_sequence/";
		StanfordLemmatizer lemm=new StanfordLemmatizer();
		OnlySourceSequenceGenerator mcsg=new OnlySourceSequenceGenerator(inPath,arrLibraryPrefix,lemm);
		mcsg.generateSequences(outPath);
		mcsg.generateAlignment(true);
	}

}
