package download;

import java.io.File;

import utils.FileIO;

public class CombineTenFoldResultIntrinsicEval {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fnTest2OnlySurrounding="";
		String fnTestCodeContext="";
		String fnTestRandomTerm="";
		String fnTestFull="testFull";
		String fopOutput="G:\\projectAddTermData\\v5_allFolds\\b12_tune_fold-1"
				 + "\\";
		
		String[] nameTestFolder={"test2FinalOnlySurrounding","test3CodeContext","test4RandomTextContext","test5Full"};
		
		StringBuilder[] sbTestArr=new StringBuilder[4];
		
		for(int i=0;i<sbTestArr.length;i++){
			sbTestArr[i]=new StringBuilder();
		}
		
		for (int i = 1; i <= 10; i++) {
			String fopEvaluation = "G:\\projectAddTermData\\v5_allFolds\\b12_tune_fold-"
					+ i + "\\";
			for(int j=0;j<nameTestFolder.length;j++){
				String fpEval=fopEvaluation+File.separator+nameTestFolder[j]+File.separator+"eval"+File.separator+"result_all.txt";
				String[] arrEvalContent=FileIO.readFromLargeFile(fpEval).trim().split("\n");
				sbTestArr[j].append("fold-"+i+"\n");
				for(int k=arrEvalContent.length-6;k<arrEvalContent.length;k++){
					String libName=arrEvalContent[k].trim().split(":")[0];
					String[] arrNums=arrEvalContent[k].trim().split(":")[1].trim().split("\\s+");
					sbTestArr[j].append(libName+"\t"+arrNums[0]+"\t"+arrNums[1]+"\t"+arrNums[2]+"\t"+arrNums[3]+"\t"+arrNums[4]+"\n");
				}
			}
			System.out.println("end fold "+i);
		}
		
		for(int i=0;i<sbTestArr.length;i++){
			FileIO.writeStringToFile(sbTestArr[i].toString()+"\n", fopOutput+"intrinsic_"+nameTestFolder[i]+".txt");
		}
	}

}
