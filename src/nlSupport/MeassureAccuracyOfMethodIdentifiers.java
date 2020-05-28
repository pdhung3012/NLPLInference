package nlSupport;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedHashSet;

import consts.PathConstanct;
import utils.FileIO;

public class MeassureAccuracyOfMethodIdentifiers {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fopInputTextMetaData=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport\\IVC\\a_NLAndMethodNames\\";
		String fopInputPrePostfix=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport\\step1_prepostfix\\";
		String fopInputMnames=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport\\step2_methodNames\\";
		String fopOutputAccMnames=PathConstanct.PATH_PROJECT_NL_SUPPORT+"nlSupport\\accuracy\\";
		String fname_methods="methods.txt";
		String fname_accMethodIden="identifyMethodNames.txt";
		
		new File(fopOutputAccMnames).mkdir();
		
		StringBuilder sbTotal=new StringBuilder();
		int correctCount=0;
		for(int i=1;i<=100;i++) {
			String nameOfFile=String.format("%03d", i);
			String fonIndexFile=File.separator+nameOfFile+File.separator;
			
			HashSet<String> setMethodsByDoc2Vecs=new LinkedHashSet<String>();
			String[] arrMNames=FileIO.readStringFromFile(fopInputMnames+fonIndexFile+fname_methods).split("\n");
			for(String str:arrMNames) {
				if(!str.trim().isEmpty()) {
					setMethodsByDoc2Vecs.add(str.trim().replaceAll("#identifier", ""));
				}
			}
			
			String mExpectedName=FileIO.readStringFromFile(fopInputTextMetaData+nameOfFile+".java").split("\n")[2].trim();
			boolean isCheck=setMethodsByDoc2Vecs.contains(mExpectedName);
			if(isCheck) {
				correctCount++;
			}
			sbTotal.append(isCheck+"\n");
			
		}
		double finalScore=correctCount*1.0/100;
		sbTotal.append(finalScore+"\n");
		FileIO.writeStringToFile(sbTotal.toString(), fopOutputAccMnames+fname_accMethodIden);
		
		

	}

}
