package download;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import consts.PathConstanct;
import utils.FileUtil;

public class GenerateStatMethodIdenTenFolds {

	public static void evaluateLibraryPerMethod(
			HashMap<String, Integer> mapMethod, String targetToken) {
		String[] arrTargetTokens = targetToken.split("\\s+");
		for (int i = 0; i < arrTargetTokens.length; i++) {

			for (String itemKey : mapMethod.keySet()) {
				if (arrTargetTokens[i].contains(itemKey)) {
					mapMethod.put(itemKey, mapMethod.get(itemKey) + 1);
				}
			}
		}
	}

	public static int randInt(int min, int max) {

		// NOTE: This will (intentionally) not run as written so that folks
		// copy-pasting have to think about how to initialize their
		// Random instance. Initialization of the Random instance is outside
		// the main scope of the question, but some decent options are to have
		// a field that is initialized once and then re-used as needed or to
		// use ThreadLocalRandom (if using at least Java 1.7).
		Random rand;

		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);

		return randomNum;
	}

	public static String[] arr5LibPrefix = { "android", "com.google.gwt",
			"com.thoughtworks.xstream", "org.hibernate", "org.joda.time" };

	public void extractGoodPercentage(ArrayList<Integer> listNumbers,
			String[] listLocations, String[] listSources) {
		for (int i = 0; i < listLocations.length; i++) {
			String[] arrLocationInfo = listLocations[i].split("\t");
			String[] arrTokenInSource = listSources[i].trim().split("\\s+");
		}
	}

	public void filterGoodPercentage(ArrayList<Integer> listNumbers,
			ArrayList<String> listLocations) {

	}

	public static void main(String[] args) {
		// TODO Auto-generated method
		String fop_output = PathConstanct.PATH_PROJECT_TTT_DATA;
		// System.exit(0);
		System.out.println("Start created 10 fold");
		// System.out.println("Start created 10 fold");

		// create folder to store 10 fold
		for (int i = 1; i <= 10; i++) {
			String fn_fold = "fold-" + i;
			File fFold = new File(fop_output + File.separator + fn_fold
					+ File.separator);
			if (!fFold.isDirectory()) {
				fFold.mkdir();
			}
		}

		PrintStream[] arrPrtTestSource = new PrintStream[10];
		PrintStream[] arrPrtTestTarget = new PrintStream[10];
		PrintStream[] arrPrtTrainSource = new PrintStream[10];
		PrintStream[] arrPrtTrainTarget = new PrintStream[10];
		PrintStream[] arrPrtTrainAlignS2T = new PrintStream[10];
		PrintStream[] arrPrtTrainAlignT2S = new PrintStream[10];
		PrintStream[] arrPrtTuneSource = new PrintStream[10];
		PrintStream[] arrPrtTuneTarget = new PrintStream[10];
		PrintStream[] arrPrtTuneLocation = new PrintStream[10];
		PrintStream[] arrPrtTestLocation = new PrintStream[10];
		PrintStream[] arrPrtTrainLocation = new PrintStream[10];
		PrintStream[] arrPrtTuneLine = new PrintStream[10];
		PrintStream[] arrPrtTestLine = new PrintStream[10];
		PrintStream[] arrPrtTrainLine = new PrintStream[10];

		for (int j = 0; j < 10; j++) {

			try {

				arrPrtTestTarget[j] = new PrintStream(new FileOutputStream(
						fop_output + "\\fold-" + (j + 1) + "\\test.t", true));
				arrPrtTestSource[j] = new PrintStream(new FileOutputStream(
						fop_output + "\\fold-" + (j + 1) + "\\test.s", true));
				arrPrtTrainSource[j] = new PrintStream(new FileOutputStream(
						fop_output + "\\fold-" + (j + 1) + "\\train.s", true));
				arrPrtTrainTarget[j] = new PrintStream(new FileOutputStream(
						fop_output + "\\fold-" + (j + 1) + "\\train.t", true));
				arrPrtTuneSource[j] = new PrintStream(new FileOutputStream(
						fop_output + "\\fold-" + (j + 1) + "\\tune.s", true));
				arrPrtTuneTarget[j] = new PrintStream(new FileOutputStream(
						fop_output + "\\fold-" + (j + 1) + "\\tune.t", true));
				arrPrtTuneLocation[j] = new PrintStream(new FileOutputStream(
						fop_output + "\\fold-" + (j + 1)
								+ "\\tune.locations.txt", true));
				arrPrtTestLocation[j] = new PrintStream(new FileOutputStream(
						fop_output + "\\fold-" + (j + 1)
								+ "\\test.locations.txt", true));
				arrPrtTrainLocation[j] = new PrintStream(new FileOutputStream(
						fop_output + "\\fold-" + (j + 1)
								+ "\\train.locations.txt", true));
				arrPrtTuneLine[j] = new PrintStream(new FileOutputStream(
						fop_output + "\\fold-" + (j + 1) + "\\tune.lines.txt",
						true));
				arrPrtTestLine[j] = new PrintStream(new FileOutputStream(
						fop_output + "\\fold-" + (j + 1) + "\\test.lines.txt",
						true));
				arrPrtTrainLine[j] = new PrintStream(new FileOutputStream(
						fop_output + "\\fold-" + (j + 1) + "\\train.lines.txt",
						true));
				arrPrtTrainAlignS2T[j] = new PrintStream(new FileOutputStream(
						fop_output + "\\fold-" + (j + 1) + "\\training.s-t.A3",
						true));
				arrPrtTrainAlignT2S[j] = new PrintStream(new FileOutputStream(
						fop_output + "\\fold-" + (j + 1) + "\\training.t-s.A3",
						true));

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		ArrayList<String> arrFold1TestLoc = FileUtil
				.getFileStringArray(fop_output
						+ "\\StatNLPLInfer_v3_allfolds\\b1_v4_ExpressionInference\\"
						+ "test.locations.txt");
		// create hash set to store all possible test line after each fold;
		HashSet<String> setTestOld = new HashSet<String>();
		for (int j = 0; j < arrFold1TestLoc.size(); j++) {
			setTestOld.add(arrFold1TestLoc.get(j));
		}
		System.out.println(arrFold1TestLoc.size() + " fold1 size "
				+ setTestOld.size());

		for (int i = 0; i < arr5LibPrefix.length; i++) {
			// if(arrLstFiles[i].getName().equals("org.apache.commons")){
			// continue;
			// }
			ArrayList<String> arrSource = FileUtil
					.getFileStringArray(fop_output + arr5LibPrefix[i]
							+ ".source.txt");
			ArrayList<String> arrTarget = FileUtil
					.getFileStringArray(fop_output + arr5LibPrefix[i]
							+ ".target.txt");
			ArrayList<String> arrLocation = FileUtil
					.getFileStringArray(fop_output + arr5LibPrefix[i]
							+ ".locations.txt");

			int lengthOfPairs = arrSource.size();

			HashMap<Integer, HashSet<Integer>> mapTestOver10Fold = new HashMap<Integer, HashSet<Integer>>();
			HashMap<Integer, HashSet<Integer>> mapTuneOver10Fold = new HashMap<Integer, HashSet<Integer>>();

			int numberForTestInLib = lengthOfPairs / 10;
			int numberForTuneInLib = lengthOfPairs / 10;
			System.out.println("begin lib " + arr5LibPrefix[i]);

			// from fold 1 to fold 10, the list will be reduced
			ArrayList<Integer> listPossibleTestPerEachFold = new ArrayList<Integer>();
			ArrayList<String> setTestOldPerLib = new ArrayList<String>();
			HashSet<Integer> setTestForFold1 = new HashSet<Integer>();
			for (int j = 0; j < arrSource.size(); j++) {
				if (setTestOld.contains(arrLocation.get(j))) {
					setTestOldPerLib.add(arrLocation.get(j));
					setTestForFold1.add(j);
				} else {
					listPossibleTestPerEachFold.add(j);
				}
			}
			System.out.println(setTestForFold1.size() + " possible size "
					+ listPossibleTestPerEachFold.size());

			// System.out.println(setTestForFold1.size()+" tests is from previous");

			for (int indexFold = 1; indexFold <= 10; indexFold++) {
				int indexForTest = 0;
				HashSet<Integer> setTestPerFold = new HashSet<Integer>();
				HashSet<Integer> setTunePerFold = new HashSet<Integer>();
				if (indexFold == 1) {
					if (setTestForFold1.size() > 0) {
						setTestPerFold = setTestForFold1;
						indexForTest = setTestPerFold.size();
					} else {
						while (indexForTest <= numberForTestInLib) {
							indexForTest++;
							int randomIndexForTest = randInt(0,
									listPossibleTestPerEachFold.size() - 1);
							if (!setTestPerFold.contains(randomIndexForTest)) {
								setTestPerFold.add(listPossibleTestPerEachFold
										.get(randomIndexForTest));
								listPossibleTestPerEachFold
										.remove(randomIndexForTest);
							}
						}
					}

				}
				if (indexFold < 10) {
					while (indexForTest <= numberForTestInLib) {
						indexForTest++;
						int randomIndexForTest = randInt(0,
								listPossibleTestPerEachFold.size() - 1);
						if (!setTestPerFold.contains(randomIndexForTest)) {
							setTestPerFold.add(listPossibleTestPerEachFold
									.get(randomIndexForTest));
							listPossibleTestPerEachFold
									.remove(randomIndexForTest);
						}
					}
				} else {
					while (listPossibleTestPerEachFold.size() > 0) {
						setTestPerFold.add(listPossibleTestPerEachFold.get(0));
						listPossibleTestPerEachFold.remove(0);
					}
				}

				int indexForTune = 0;
				while (indexForTune < numberForTuneInLib) {
					if (!setTestPerFold.contains(indexForTune)) {
						setTunePerFold.add(indexForTune);
					}
					indexForTune++;
				}

				mapTestOver10Fold.put(indexFold, setTestPerFold);
				mapTuneOver10Fold.put(indexFold, setTunePerFold);

			}

			for (int indexFold = 1; indexFold <= 10; indexFold++) {
				HashSet<Integer> setTest = mapTestOver10Fold.get(indexFold);
				HashSet<Integer> setTune = mapTuneOver10Fold.get(indexFold);

				// source & target & location
				for (int j = 0; j < arrSource.size(); j++) {

					if (setTest.contains(j)) {
						arrPrtTestSource[indexFold - 1].print(arrSource.get(j)
								+ "\n");
						arrPrtTestTarget[indexFold - 1].print(arrTarget.get(j)
								+ "\n");
						arrPrtTestLocation[indexFold - 1].print(arrLocation
								.get(j) + "\n");
						arrPrtTestLine[indexFold - 1].print((j + 1) + "\t"
								+ arr5LibPrefix[i] + "\n");

					} else {
						arrPrtTrainSource[indexFold - 1].print(arrSource.get(j)
								+ "\n");
						arrPrtTrainTarget[indexFold - 1].print(arrTarget.get(j)
								+ "\n");
						arrPrtTrainLocation[indexFold - 1].print(arrLocation
								.get(j) + "\n");
						arrPrtTrainLine[indexFold - 1].print((j + 1) + "\t"
								+ arr5LibPrefix[i] + "\n");

						if (setTune.contains(j)) {
							arrPrtTuneSource[indexFold - 1].print(arrSource
									.get(j) + "\n");
							arrPrtTuneTarget[indexFold - 1].print(arrTarget
									.get(j) + "\n");
							arrPrtTuneLocation[indexFold - 1].print(arrLocation
									.get(j) + "\n");
							arrPrtTuneLine[indexFold - 1].print((j + 1) + "\t"
									+ arr5LibPrefix[i] + "\n");
						}

					}
				}
				System.out.println("fold " + indexFold);
				// break;
			}

			arrSource.clear();
			arrTarget.clear();
			arrLocation.clear();

			// append for align s2t
			ArrayList<String> arrTrainSt = FileUtil
					.getFileStringArray(fop_output + arr5LibPrefix[i]
							+ ".training.s-t.A3");
			for (int indexFold = 1; indexFold <= 10; indexFold++) {
				HashSet<Integer> setTest = mapTestOver10Fold.get(indexFold);

				// source & target & location
				for (int j = 0; j < lengthOfPairs; j++) {

					if (setTest.contains(j)) {

					} else {
						arrPrtTrainAlignS2T[indexFold - 1].print(arrTrainSt
								.get(j * 3) + "\n");
						arrPrtTrainAlignS2T[indexFold - 1].print(arrTrainSt
								.get(j * 3 + 1) + "\n");
						arrPrtTrainAlignS2T[indexFold - 1].print(arrTrainSt
								.get(j * 3 + 2) + "\n");

					}
				}
				System.out.println("fold " + indexFold);
				// break;

			}
			arrTrainSt.clear();
			// append for align t2s
			ArrayList<String> arrTrainTs = FileUtil
					.getFileStringArray(fop_output + arr5LibPrefix[i]
							+ ".training.t-s.A3");
			for (int indexFold = 1; indexFold <= 10; indexFold++) {
				HashSet<Integer> setTest = mapTestOver10Fold.get(indexFold);

				// source & target & location
				for (int j = 0; j < lengthOfPairs; j++) {

					if (setTest.contains(j)) {

					} else {
						arrPrtTrainAlignT2S[indexFold - 1].print(arrTrainTs
								.get(j * 3) + "\n");
						arrPrtTrainAlignT2S[indexFold - 1].print(arrTrainTs
								.get(j * 3 + 1) + "\n");
						arrPrtTrainAlignT2S[indexFold - 1].print(arrTrainTs
								.get(j * 3 + 2) + "\n");

					}
				}
				System.out.println("fold " + indexFold);
				// break;

			}
			arrTrainTs.clear();

		}

		for (int j = 0; j < 10; j++) {

			try {

				arrPrtTestTarget[j].close();
				;
				arrPrtTestSource[j].close();
				arrPrtTrainSource[j].close();
				arrPrtTrainTarget[j].close();
				;
				arrPrtTuneSource[j].close();
				arrPrtTuneTarget[j].close();
				arrPrtTuneLocation[j].close();
				arrPrtTestLocation[j].close();
				arrPrtTrainLocation[j].close();
				arrPrtTuneLine[j].close();
				arrPrtTestLine[j].close();
				arrPrtTrainLine[j].close();
				arrPrtTrainAlignS2T[j].close();
				arrPrtTrainAlignT2S[j].close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
