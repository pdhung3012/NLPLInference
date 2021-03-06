package invocations;

import java.io.File;

import consts.PathConstanct;
import utils.StanfordLemmatizer;

public class GenerateTestingFromProjects {

	public static String[] arrLibraryPrefix={"android","com.google.gwt","com.thoughtworks.xstream","org.hibernate","org.joda.time","java"};
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String inPath=PathConstanct.PATH_PROJECT_TTT_TEST_INPUT_PROJECT+"TestExpInference"+File.separator;
		String outPath=PathConstanct.PATH_PROJECT_TTT_TEST_IDENTIFIER_PROJECT+"TestExpInference"+File.separator;
		StanfordLemmatizer lemm=new StanfordLemmatizer();
		MethodContextSequenceGenerator mcsg=new MethodContextSequenceGenerator(inPath,arrLibraryPrefix,lemm);
		mcsg.generateSequences(outPath);
		mcsg.generateAlignment(true);
	}

}
