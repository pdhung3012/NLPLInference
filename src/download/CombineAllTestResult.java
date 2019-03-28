package download;

import java.io.File;

import utils.FileIO;
import consts.PathConstanct;

public class CombineAllTestResult {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fop_input=PathConstanct.PATH_PROJECT_TTT_CUR_EVAL_DATA;
		String fop_output=PathConstanct.PATH_PROJECT_TTT_CUR_EVAL_DATA+"b11_all"+File.separator;
		new File(fop_output).mkdir();
		
		String fn_loc="test.locations.txt";
		String fn_ts="test.s";
		String fn_tt="test.t";
		String fn_trans="correctOrderTranslatedResult.txt";
		
		FileIO.writeStringToFile("", fop_output+fn_loc);
		FileIO.writeStringToFile("", fop_output+fn_ts);
		FileIO.writeStringToFile("", fop_output+fn_tt);
		FileIO.writeStringToFile("", fop_output+fn_trans);
		
		for(int i=1;i<=10;i++){
			String fop_curr=fop_input.replaceAll("b11_fold-1","b11_fold-"+i);
			String strLoc=FileIO.readStringFromFile(fop_curr+fn_loc);
			String strTs=FileIO.readStringFromFile(fop_curr+fn_ts);
			String strTt=FileIO.readStringFromFile(fop_curr+fn_tt);
			String strTrans=FileIO.readStringFromFile(fop_curr+fn_trans);
			FileIO.appendStringToFile(strLoc, fop_output+fn_loc);
			FileIO.appendStringToFile(strTs, fop_output+fn_ts);
			FileIO.appendStringToFile(strTt, fop_output+fn_tt);
			FileIO.appendStringToFile(strTrans, fop_output+fn_trans);
		}
	}

}
