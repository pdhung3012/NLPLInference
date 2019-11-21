package analysis;

import java.io.File;

import consts.PathConstanct;
import utils.FileIO;

public class CountTokensInTestS {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fpTest=PathConstanct.PATH_PROJECT_TTT_CUR_EVAL_DATA+File.separator+"test.s";
		String[] arrTests=FileIO.readStringFromFile(fpTest).trim().split("\n");
		int countTokens=0;
		for(int i=0;i<arrTests.length;i++) {
			String[] arrItems=arrTests[i].split("\\s+");
			for(String strItem:arrItems) {
				if(!strItem.isEmpty()) {
					countTokens++;
				}
			}
		}
		System.out.println("Num tokens: "+countTokens);

	}

}
