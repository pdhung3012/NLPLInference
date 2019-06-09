package plugins;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;

import consts.PathConstanct;
import utils.FileIO;
import utils.SortUtil;
import utils.StanfordLemmatizer;

public class QueryMethodCompareEngine {

	private StanfordLemmatizer lemm;
	private String regexCamelCase="([^_A-Z])([A-Z])";
	
	public QueryMethodCompareEngine(StanfordLemmatizer lemm) {
		this.lemm=lemm;
	}
	
	public static double getJaccardExactSimilary(String query, String candidate) {
		double result = 0;
		return result;
	}

	public static double editDistanceSimilarity(String s1, String s2) {
		String longer = s1, shorter = s2;
		if (s1.length() < s2.length()) { // longer should always have greater length
			longer = s2;
			shorter = s1;
		}
		int longerLength = longer.length();
		if (longerLength == 0) {
			return 1.0;
			/* both strings are zero length */ }
		/*
		 * // If you have Apache Commons Text, you can use it to calculate the edit
		 * distance: LevenshteinDistance levenshteinDistance = new
		 * LevenshteinDistance(); return (longerLength -
		 * levenshteinDistance.apply(longer, shorter)) / (double) longerLength;
		 */
		return (longerLength - editDistance(longer, shorter)) / (double) longerLength;

	}

	// Example implementation of the Levenshtein Edit Distance
	// See http://rosettacode.org/wiki/Levenshtein_distance#Java
	public static int editDistance(String s1, String s2) {
		s1 = s1.toLowerCase();
		s2 = s2.toLowerCase();

		int[] costs = new int[s2.length() + 1];
		for (int i = 0; i <= s1.length(); i++) {
			int lastValue = i;
			for (int j = 0; j <= s2.length(); j++) {
				if (i == 0)
					costs[j] = j;
				else {
					if (j > 0) {
						int newValue = costs[j - 1];
						if (s1.charAt(i - 1) != s2.charAt(j - 1))
							newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
						costs[j - 1] = lastValue;
						lastValue = newValue;
					}
				}
			}
			if (i > 0)
				costs[s2.length()] = lastValue;
		}
		return costs[s2.length()];
	}
	
	public String doLemm(String strItem){
		String strResult="";
		try{
			
			strResult=lemm.lemmatizeToString(strItem);
			
		}catch(Exception ex){
			
		}
		return strResult;
	}

	public String splitByCamelCaseAndLemm(String strItem){
		String strResult="";
		try{
			
			strResult=lemm.lemmatizeToString(strItem.replaceAll(regexCamelCase, "$1 $2"));
			
		}catch(Exception ex){
			
		}
		return strResult;
	}
	
	public HashMap<String,Double> calculateSimilarInvocationByEditSimilarity(String query, File fileDictionary,File fileDictLemma){
		String[] arrDictionary=FileIO.readStringFromFile(fileDictionary.getAbsolutePath()).split("\n");
		if(!fileDictLemma.exists()) {
			StringBuilder sbItem=new StringBuilder();
			for(int i=0;i<arrDictionary.length;i++) {
				String[] arrItemDict=arrDictionary[i].split("\t");
				String lemmItem=splitByCamelCaseAndLemm(arrItemDict[0].replaceAll("#identifier","")).replaceAll("\n", " ").trim();
				sbItem.append(lemmItem+"\n");
			}
			FileIO.writeStringToFile(sbItem.toString()+"\n", fileDictLemma.getAbsolutePath());
		}

		HashMap<String,Double> mapResult=new LinkedHashMap<>();
		String[] arrDictLemma=FileIO.readStringFromFile(fileDictLemma.getAbsolutePath()).split("\n");
		query=doLemm(query);
		for(int i=0;i<arrDictLemma.length;i++) {
			String[] arrItemDict=arrDictionary[i].split("\t");
			double scoreItem=editDistanceSimilarity(query,arrDictLemma[i]);
			mapResult.put(arrItemDict[0], scoreItem);
		}
		mapResult=SortUtil.sortHashMapStringDoubleByValueDesc(mapResult);
		return mapResult;
		
	}
	
	
	
	public void processMatchQueryToMethodName(File fileQuery,File fileOutput,int topResultNum, File fileDictionary,File fileDictLemma) {
		String[] arrQueries=FileIO.readStringFromFile(fileQuery.getAbsolutePath()).split("\n");
		StringBuilder sbResult=new StringBuilder();
		for(int i=0;i<arrQueries.length;i++) {
			long startTime = System.nanoTime();
			HashMap<String,Double> mapQueryResult=calculateSimilarInvocationByEditSimilarity(arrQueries[i], fileDictionary, fileDictLemma);
			long endTime   = System.nanoTime();
			long totalTime = endTime - startTime;
			
			int indexPrintOut=0;
			sbResult.append("Query "+(i+1)+": "+arrQueries[i]+"\n");
			sbResult.append("Time : "+totalTime+"\n");
			for(String strItemMethod:mapQueryResult.keySet()) {
				indexPrintOut++;
				sbResult.append(indexPrintOut+". "+strItemMethod+"\t"+mapQueryResult.get(strItemMethod)+"\n");
				if(indexPrintOut==topResultNum) {
					break;
				}
			}
			sbResult.append("\n\n");
		}
		
		FileIO.writeStringToFile(sbResult.toString()+"\n", fileOutput.getAbsolutePath());
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String fop=PathConstanct.PATH_PROJECT_TTT_CUR_EVAL_DATA;
		String fn_query="queryTextDesc.txt";
		String fn_dictionary="dictionary_mn.txt";
		String fn_dict_lemma="dictionary_lemma.txt";
		String fn_output="queryTextDesc_result.txt";
		int topResultNum=20;
		
		StanfordLemmatizer lemm=new StanfordLemmatizer();
		QueryMethodCompareEngine qmce=new QueryMethodCompareEngine(lemm);
		qmce.processMatchQueryToMethodName(new File(fop+fn_query), new File(fop+fn_output), topResultNum, new File(fop+fn_dictionary), new File(fop+fn_dict_lemma));
		
	}

}
