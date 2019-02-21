package invocations;

public class RunTestOnSmallProject {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String inPath="/Users/hungphan/Documents/workspace/SampleMethodInvocationProject/";
		String outPath="/Users/hungphan/git/NLPLTranslation/sequences/SampleMethodInvocationProject/";
		MethodContextSequenceGenerator mcsg=new MethodContextSequenceGenerator(inPath);
		mcsg.generateSequences(outPath);
		mcsg.generateAlignment(true);
	}

}
