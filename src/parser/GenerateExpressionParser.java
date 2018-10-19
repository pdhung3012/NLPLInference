package parser;

import java.io.File;

public class GenerateExpressionParser {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		String fop_githubProject="/Users/hungphan/git/jdk8Source/src/java/";
		String fop_githubProject="/Users/hungphan/Documents/workspace/ExampleVerbInference/";
		
		String fop_sequence="/Users/hungphan/git/jdk8Source/sequences//";
		
		File f=new File(fop_githubProject);
		
		ProjectExpressionGenerator generator=new ProjectExpressionGenerator(f.getAbsolutePath());
		generator.generateSequences(fop_sequence+"/"+f.getName()+"/");	
	}

}
