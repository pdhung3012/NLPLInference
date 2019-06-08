package plugins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import consts.PathConstanct;
import utils.FileIO;
import utils.FileUtil;

public class GenerateDictionaryMethodName {

	public static void main(String[] args) {
		String fopInput=PathConstanct.PATH_PROJECT_TTT_CUR_EVAL_DATA;
				
		String fn_trainSource="train.s";
		String fn_dict="dictionary_mn.txt";
		
		ArrayList<String> lstOrgSources=FileUtil.getFileStringArray(fopInput+fn_trainSource);
		HashMap<String,Integer> mapDictionary=new LinkedHashMap<>();
		
		for(int i=0;i<lstOrgSources.size();i++){
			String[] arrItemSource=lstOrgSources.get(i).trim().split("\\s+");
			
			for(int j=0;j<arrItemSource.length;j++){
				if(!arrItemSource[j].endsWith("#identifier")){
					if(!mapDictionary.containsKey(arrItemSource[j])) {
						mapDictionary.put(arrItemSource[j], 1);
					} else {
						mapDictionary.put(arrItemSource[j],mapDictionary.get(arrItemSource[j])+ 1);
					}
				}
			}
		}
		
		Set<String> setDic=mapDictionary.keySet();
		List<String> sortedList = new ArrayList<>(setDic);
		Collections.sort(sortedList);
		
		StringBuilder sbDict=new StringBuilder();
		for(int i=0;i<sortedList.size();i++) {
			String strItem=sortedList.get(i);
			sbDict.append(strItem+"\t"+mapDictionary.get(strItem)+"\n");
			
		}
		FileIO.writeStringToFile(sbDict.toString()+"\n", fopInput+fn_dict);
		
		
		
		
	}
}
