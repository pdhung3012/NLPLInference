package invocations;

public class RunMethodGenerator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		https://github.com/apache/pig/
		String inPath="/Users/hungphan/git/pig/";
		String outPath="/Users/hungphan/git/NLPLTranslation/sequences/pig/";
		String outputIdLocation = "/Users/hungphan/Documents/workspace/OutputMethodId/";
		MethodContextSequenceGenerator mcsg=new MethodContextSequenceGenerator(inPath,outputIdLocation);
		mcsg.generateSequences(outPath);
		mcsg.generateAlignment(true);
		
		
	}

}
