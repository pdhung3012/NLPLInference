package download;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import utils.FileIO;
import utils.FileUtil;
import consts.PathConstanct;

public class ReGenerateAlignment {

	public static String[] arr5LibPrefix={"android","com.google.gwt","com.thoughtworks.xstream","org.hibernate","org.joda.time"};

	/**
	 * 
	 * @param inPath
	 * @param doVerify
	 * @return 	numbers[0]: 0-same number of sequences, 1-different numbers of sequences;
	 * 			numbers[1]: number of sequences with different lengths;
	 * 			numbers[2]: number of sequences with non-aligned tokens;
	 * 			numbers[3]: number of non-aligned tokens 
	 */
	public static int[] generateTotalAlignment(String fopPath,String fpSource,String fpTarget,String fpAlignST,String fpAlignTS, boolean doVerify) {
		int[] numbers = new int[]{0, 0, 0, 0};
		ArrayList<String> sourceSequences = FileUtil.getFileStringArray(fpSource), 
				targetSequences = FileUtil.getFileStringArray(fpTarget);
		if (doVerify)
			if (sourceSequences.size() != targetSequences.size()) {
				numbers[0]++;
//				throw new AssertionError("Numbers of source and target sequences are not the same!!!");
			}
		File dir = new File(fopPath);
		if (!dir.exists())
			dir.mkdirs();
		PrintStream psS2T = null, psT2S = null;
		try {
			psS2T = new PrintStream(new FileOutputStream(fpAlignST,true));
			psT2S = new PrintStream(new FileOutputStream(fpAlignTS,true));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			if (psS2T != null)
				psS2T.close();
			if (psT2S != null)
				psT2S.close();
			e.printStackTrace();
			return null;
		}
		for (int i = 0; i < sourceSequences.size(); i++) {
			String source = sourceSequences.get(i), target = targetSequences.get(i);
			String[] sTokens = source.trim().split(" "), tTokens = target.trim().split(" ");
			if (doVerify) {
				if (sTokens.length != tTokens.length) {
					numbers[1]++;
//					throw new AssertionError("Lengths of source and target sequences are not the same!!!");
				}
				boolean aligned = true;
				for (int j = 0; j < sTokens.length; j++) {
					String s = sTokens[j], t = tTokens[j];
					if ((t.contains(".") && !t.substring(t.lastIndexOf('.')+1).equals(s.substring(s.lastIndexOf('.')+1))) || (!t.contains(".") && !t.equals(s))) {
						numbers[3]++;
						aligned = false;
//						throw new AssertionError("Source and target are not aligned!!!");
					}
				}
				if (!aligned)
					numbers[2]++;
			}
			String headerS2T = generateHeader(sTokens, tTokens, i), headerT2S = generateHeader(tTokens, sTokens, i);
			psS2T.println(headerS2T);
			psT2S.println(headerT2S);
			psS2T.println(target);
			psT2S.println(source);
			String alignmentS2T = generateAlignment(sTokens), alignmentT2S = generateAlignment(tTokens);
			psS2T.println(alignmentS2T);
			psT2S.println(alignmentT2S);
		}
		psS2T.flush();
		psT2S.flush();
		psS2T.close();
		psT2S.close();
		if (doVerify) {
			if (sourceSequences.size()*3 != FileUtil.countNumberOfLines(fpAlignST)
					|| targetSequences.size()*3 != FileUtil.countNumberOfLines(fpAlignTS))
				numbers[0]++;
		}
		return numbers;
	}
	
	private static String generateHeader(String[] sTokens, String[] tTokens, int i) {
		return "# sentence pair (" + i + ") source length " + sTokens.length + " target length " + tTokens.length + " alignment score : 0";
	}
	
	private static String generateAlignment(String[] tokens) {
		StringBuilder sb = new StringBuilder();
		sb.append("NULL ({  })");
		for (int i = 0; i < tokens.length; i++) {
			String t = tokens[i];
			sb.append(" " + t + " ({ " + (i+1) + " })");
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fopProjectTTTLibrary = PathConstanct.PATH_PROJECT_TRAIN_TEST_NAME
				+ "5LibSequence" + File.separator;
		String fopOutput = PathConstanct.PATH_PROJECT_TTT_ADDTERMS_DATA;

		String fpTempForWrite = PathConstanct.PATH_PROJECT_TTT_ADDTERMS_DATA
				+ "tempForWrite.txt";
		for(int i=0;i<arr5LibPrefix.length;i++){
			
			FileIO.writeStringToFile("", fopOutput+arr5LibPrefix[i]+".training.s-t.A3");
			FileIO.writeStringToFile("", fopOutput+arr5LibPrefix[i]+".training.t-s.A3");
			generateTotalAlignment(fopOutput, fopOutput+arr5LibPrefix[i]+".source.txt", fopOutput+arr5LibPrefix[i]+".target.txt", fopOutput + arr5LibPrefix[i]
					+ ".training.s-t.A3", fopOutput + arr5LibPrefix[i]
					+ ".training.t-s.A3", false);
			
		}
	}

}
