package download;

import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;

import sun.misc.FpUtils;
import utils.FileIO;
import utils.MapUtil;
import utils.SortUtil;
import consts.PathConstanct;

public class AnalyzeNLPWMT2016Data {

	public static String tryGetLine(BufferedReader br) {
		String line=null;
		try {
			line = br.readLine();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return line;
	}
	
public static void addToHashMapCount(HashMap<String,Integer> mapVocabStrInt,String fpFile){
		
		try (BufferedReader br = Files.newBufferedReader(Paths.get(fpFile), StandardCharsets.ISO_8859_1)) {
		    for (String line = null; (line = tryGetLine(br)) != null;) {
		    	//System.out.println(line);
		    	String[] arrItems=line.trim().split("\\s+");
				for(int j=0;j<arrItems.length;j++){
					
					if(!mapVocabStrInt.containsKey(arrItems[j])){
						mapVocabStrInt.put(arrItems[j], 1);
					} else{
						mapVocabStrInt.put(arrItems[j],mapVocabStrInt.get(arrItems[j])+ 1);
					}
				}
		    }
		} catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
	
	public static void addVocabToHashMapCount(HashMap<String,Integer> mapVocabStrInt,String fpFile){
		
		try (BufferedReader br = Files.newBufferedReader(Paths.get(fpFile), StandardCharsets.ISO_8859_1)) {
		    for (String line = null; (line = tryGetLine(br)) != null;) {
		    	//System.out.println(line);
		    	String strItems=line.trim();
		    	if(!mapVocabStrInt.containsKey(strItems)){
					mapVocabStrInt.put(strItems, 1);
				} else{
					mapVocabStrInt.put(strItems,mapVocabStrInt.get(strItems)+ 1);
				}
		    }
		} catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fopInput=PathConstanct.PATH_PROJECT_NLP_DATA;
		String fopOutputEval=PathConstanct.PATH_PROJECT_NLP_DATA+"analysis"+File.separator;
		new File(fopOutputEval).mkdir();
		String fnTrainSource="train.tok.clean.bpe.32000.de";
		String fnTrainTarget="train.tok.clean.bpe.32000.en";
		String fnAnaTrainSource="ana_vocab.s";
		String fnAnaTrainTarget="ana_vocab.t";
		String fnTuneSource="";
		String fnTuneTarget="";
		String fnTestSource="";
		String fnTestTarget="";
		String fnVocabSource="vocab.bpe.32000.de";
		String fnVocabTarget="vocab.bpe.32000.en";
		
		HashMap<String,Integer> mapTrainSource=new LinkedHashMap<String, Integer>();
		HashMap<String,Integer> mapTrainTarget=new LinkedHashMap<String, Integer>();
		HashMap<String,Integer> mapIndexTrainSource=new LinkedHashMap<String, Integer>();
		HashMap<String,Integer> mapIndexTrainTarget=new LinkedHashMap<String, Integer>();
		HashMap<String,Integer> mapVocabStatusSource=new LinkedHashMap<String, Integer>();
		HashMap<String,Integer> mapVocabStatusTarget=new LinkedHashMap<String, Integer>();
		
		addToHashMapCount(mapTrainSource, fopInput+fnTrainSource);
		addToHashMapCount(mapTrainTarget, fopInput+fnTrainTarget);
		addVocabToHashMapCount(mapVocabStatusSource, fopInput+fnVocabSource);
		addVocabToHashMapCount(mapVocabStatusTarget, fopInput+fnVocabTarget);
		
		SortUtil.sortHashMapStringIntByValueDesc(mapTrainSource);
		SortUtil.sortHashMapStringIntByValueDesc(mapTrainTarget);
		SortUtil.sortHashMapStringIntByValueDesc(mapVocabStatusSource);
		SortUtil.sortHashMapStringIntByValueDesc(mapVocabStatusTarget);
		mapIndexTrainSource=SortUtil.getOrderInHM(mapTrainSource);
		mapIndexTrainTarget=SortUtil.getOrderInHM(mapTrainTarget);
		
		
		MapUtil.saveToFile(mapTrainSource, fopOutputEval+fnTrainSource);
		MapUtil.saveToFile(mapTrainTarget, fopOutputEval+fnTrainTarget);
		
		StringBuilder sb=new StringBuilder();
		for(String item:mapVocabStatusSource.keySet()){
			if(mapTrainSource.containsKey(item)){
				int numAppear=mapTrainSource.get(item);
				sb.append(item+"\ttrue\t"+mapIndexTrainSource.get(item)+"\t"+numAppear+"\t"+mapTrainSource.size()+"\t"+(numAppear*1.0/mapTrainSource.size())+"\n");
			} else{
				sb.append(item+"\tfalse\t0\t"+0+"\t"+mapVocabStatusSource.size()+"\t"+0+"\n");
			}
		}
		FileIO.writeStringToFile(sb.toString()+"\n", fopOutputEval+fnAnaTrainSource);
		
		sb=new StringBuilder();
		for(String item:mapVocabStatusTarget.keySet()){
			if(mapTrainTarget.containsKey(item)){
				int numAppear=mapTrainTarget.get(item);
				sb.append(item+"\ttrue\t"+mapIndexTrainTarget.get(item)+"\t"+numAppear+"\t"+mapTrainTarget.size()+"\t"+(numAppear*1.0/mapTrainTarget.size())+"\n");
			} else{
				sb.append(item+"\tfalse\t0\t"+0+"\t"+mapVocabStatusTarget.size()+"\t"+0+"\n");
			}
		}
		FileIO.writeStringToFile(sb.toString()+"\n", fopOutputEval+fnAnaTrainTarget);
		
		
		
		MapUtil.saveToFile(mapVocabStatusSource, fopOutputEval+fnVocabSource);
		MapUtil.saveToFile(mapVocabStatusTarget, fopOutputEval+fnVocabTarget);
		
		
	}

}
