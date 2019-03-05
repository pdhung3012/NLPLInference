package invocations;

public class RunTestOnSmallProject {

	public static String[] arrLibraryPrefix={"android","com.google.gwt","com.thoughtworks.xstream","org.hibernate","org.joda.time","java"};
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String inPath="/Users/hungphan/Documents/workspace/SampleMethodInvocationProject/";
		String outPath="/Users/hungphan/git/NLPLTranslation/sequences/SampleMethodInvocationProject/";
		MethodContextSequenceGenerator mcsg=new MethodContextSequenceGenerator(inPath,arrLibraryPrefix);
		mcsg.generateSequences(outPath);
		mcsg.generateAlignment(true);
	}

}
