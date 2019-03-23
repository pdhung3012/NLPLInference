package invocations;

import utils.StanfordLemmatizer;

public class RunMethodGenerator {

	public static String[] arrLibraryPrefix={"android","com.google.gwt","com.thoughtworks.xstream","org.hibernate","org.joda.time","java"};
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		https://github.com/apache/pig/
		String inPath="/Users/hungphan/git/pig/";
		String outPath="/Users/hungphan/git/NLPLTranslation/sequences/pig/";
		StanfordLemmatizer lemm=new StanfordLemmatizer();
		MethodContextSequenceGenerator mcsg=new MethodContextSequenceGenerator(inPath,arrLibraryPrefix,lemm);
		mcsg.generateSequences(outPath);
		mcsg.generateAlignment(true);
		
		
	}

}
