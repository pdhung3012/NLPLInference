package download;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import utils.MapUtil;
import consts.PathConstanct;

public class GenerateTheDamnOnlySurroundCodeTestData {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		for (int i = 1; i <= 10; i++) {
			String fopEvaluation = "G:\\projectAddTermData\\v5_allFolds\\b12_tune_fold-"
					+ i + "\\";
			String fopEvaluationMap = PathConstanct.PATH_PROJECT_TTT_CUR_EVAL_DATA
					+ "map" + File.separator;
			String fopTestFullTextAndCode = fopEvaluation + "test5Full"
					+ File.separator;
			String fopTestTempOnlySurroundingCode = fopEvaluation
					+ "test1TempOnlySurrounding" + File.separator;
			String fopTestOnlySurroundingCode = fopEvaluation
					+ "test2FinalOnlySurrounding" + File.separator;
			String fopTestCodeContext = fopEvaluation + "test3CodeContext"
					+ File.separator;
			String fopTestRandomTextContext = fopEvaluation
					+ "test4RandomTextContext" + File.separator;

			String fpIdAndMapContent = fopEvaluationMap
					+ "a_mapTotalIdAndContent.txt";

			new File(fopTestFullTextAndCode).mkdir();
			new File(fopTestTempOnlySurroundingCode).mkdir();
			new File(fopTestOnlySurroundingCode).mkdir();
			new File(fopTestCodeContext).mkdir();
			new File(fopTestRandomTextContext).mkdir();

			String fnTestSource = "test.s";
			String fnTestTarget = "test.t";

			// remove this when run the second time
			// FileIO.copyFileReplaceExist(fopEvaluation+fnTestSource,fopTestFullTextAndCode+fnTestSource);
			// FileIO.copyFileReplaceExist(fopEvaluation+fnTestTarget,fopTestFullTextAndCode+fnTestTarget);

			HashMap<String, String> mapIdAndTotalContent = MapUtil
					.getHashMapFromFile(fpIdAndMapContent);
			HashMap<String, String> mapAddTermSource = new LinkedHashMap<>();
			HashMap<String, String> mapAddTermTarget = new LinkedHashMap<>();
			HashMap<String, ArrayList<String>> mapAddTermListSource = new LinkedHashMap<>();
			HashMap<String, ArrayList<String>> mapAddTermListTarget = new LinkedHashMap<>();
			System.out.println("map");
			ReplaceIdAndAddTermForTest.collectSourceAndTargetTerm(
					mapIdAndTotalContent, mapAddTermSource, mapAddTermTarget,
					mapAddTermListSource, mapAddTermListTarget);
			System.out.println("1");
			// ReplaceIdAndAddTermForTest.refineRemoveSuggestion(fopTestFullTextAndCode+fnTestSource,fopTestFullTextAndCode+fnTestTarget,fopTestTempOnlySurroundingCode+fnTestSource,fopTestTempOnlySurroundingCode+fnTestTarget);
			// System.out.println("2");
			ReplaceIdAndAddTermForTest.refineRemoveSuggestionAddVarContext(
					fopTestTempOnlySurroundingCode + fnTestSource,
					fopTestTempOnlySurroundingCode + fnTestTarget,
					fopTestOnlySurroundingCode + fnTestSource,
					fopTestOnlySurroundingCode + fnTestTarget);
			System.out.println("3");
			// ReplaceIdAndAddTermForTest.removeFromFullToCodeContextOnly(fopTestFullTextAndCode+fnTestSource,fopTestFullTextAndCode+fnTestTarget,fopTestCodeContext+fnTestSource,fopTestCodeContext+fnTestTarget);
			// System.out.println("4");
			// ReplaceIdAndAddTermForTest.addTerm50PercentToOriginSourceAndTargetForFile(fopTestCodeContext+fnTestSource,fopTestCodeContext+fnTestTarget,
			// mapAddTermSource,
			// mapAddTermTarget,fopTestRandomTextContext+fnTestSource,fopTestRandomTextContext+fnTestTarget);
			// System.out.println("5");

		}
	}

}
