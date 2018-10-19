package parser;

import java.util.Collections;
import java.util.HashSet;

import javax.imageio.stream.FileImageOutputStream;

import utils.FileIO;
import utils.FileUtil;

public class ExtractVocabulary {
	
	public static void normalize(String folderInput,String folderOutput,String fname,int size){
		String[] arrTrainS=FileIO.readStringFromFile(folderInput+fname).trim().split("\n");
		StringBuilder sbResult=new StringBuilder();
		for(int i=0;i<size;i++){
			String[] arrItemsS=arrTrainS[i].trim().split("\\s+");
			String strItem="";
			for(int j=0;j<arrItemsS.length;j++){
				strItem+=arrItemsS[j].replaceAll("\\.","DOT")+" ";
			}
			sbResult.append(strItem+"\n");
		
		}
		FileIO.writeStringToFile(sbResult.toString(), folderOutput+fname);
		
	}

	public static void normalize(String folderInput,String folderOutput,String fname){
		String[] arrTrainS=FileIO.readStringFromFile(folderInput+fname).trim().split("\n");
		StringBuilder sbResult=new StringBuilder();
		for(int i=0;i<arrTrainS.length;i++){
			String[] arrItemsS=arrTrainS[i].trim().split("\\s+");
			String strItem="";
			for(int j=0;j<arrItemsS.length;j++){
				strItem+=arrItemsS[j].replaceAll("\\.","DOT")+" ";
			}
			sbResult.append(strItem+"\n");
		}
		FileIO.writeStringToFile(sbResult.toString(), folderOutput+fname);
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String folderInput="/Users/hungphan/git/nmt/stattype/origin/";
		String folderNormalize="/Users/hungphan/git/nmt/stattype/";
		String fpTrainSource=folderInput+"train.s";
		String fpTrainTarget=folderInput+"train.t";
		String fpVocabSource=folderNormalize+"vocab.s";
		String fpVocabTarget=folderNormalize+"vocab.t";
		
		String[] arrTrainS=FileIO.readStringFromFile(fpTrainSource).trim().split("\n");
		String[] arrTrainT=FileIO.readStringFromFile(fpTrainTarget).trim().split("\n");
		
		HashSet<String> setVocabS=new HashSet<>();
		HashSet<String> setVocabT=new HashSet<>();
		
		for(int i=0;i<arrTrainS.length;i++){
			String[] arrItemsS=arrTrainS[i].trim().split("\\s+");
			String[] arrItemsT=arrTrainT[i].trim().split("\\s+");
			for(int j=0;j<arrItemsS.length;j++){
				setVocabS.add(arrItemsS[j]);
				setVocabT.add(arrItemsT[j]);
			}
			
		}
		StringBuilder sbResult=new StringBuilder();
		for(String strItem:setVocabS){
			String strNormalize=strItem.replaceAll("\\.","DOT");
			sbResult.append(strNormalize+"\n");
		}
		FileIO.writeStringToFile(sbResult.toString(),fpVocabSource);
		
		sbResult=new StringBuilder();
		for(String strItem:setVocabT){
			String strNormalize=strItem.replaceAll("\\.","DOT");
			sbResult.append(strNormalize+"\n");
		}
		FileIO.writeStringToFile(sbResult.toString(),fpVocabTarget);
//		normalize(folderInput,folderNormalize,"train.s");
//		normalize(folderInput,folderNormalize,"train.t");
		normalize(folderInput,folderNormalize,"tune.s",1000);
		normalize(folderInput,folderNormalize,"tune.t",1000);
//		normalize(folderInput,folderNormalize,"test.s");
//		normalize(folderInput,folderNormalize,"test.t");
		
		
	}

}
