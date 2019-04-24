package invocations;

import utils.StanfordLemmatizer;

public class RunTestGetSourceOnly {
	public static String[] arrLibraryPrefix={"android","com.google.gwt","com.thoughtworks.xstream","org.hibernate","org.joda.time","java"};
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String inPath="/Users/hungphan/Documents/workspace/TestExpInference/";
		String outPath="/Users/hungphan/git/NLPLTranslation/sequences/TestExpInference/";
		StanfordLemmatizer lemm=new StanfordLemmatizer();
		OnlySourceSequenceGenerator mcsg=new OnlySourceSequenceGenerator(inPath,arrLibraryPrefix,lemm);
		mcsg.generateSequences(outPath);
		mcsg.generateAlignment(true);
	}

}
