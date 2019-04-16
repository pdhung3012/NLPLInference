package eval;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import utils.FileIO;
import utils.FileUtil;
import utils.MapUtil;
import utils.ReorderingTokens;
import consts.PathConstanct;

public class AnalysisRelation {

//	/sensitivity_5fold_ressult\\
	
	public static String SplitInvocationCharacter="\\$\\%\\$";
	
	public static boolean checkAPIsInLibrary(HashSet<String> setLib,String token){
		boolean check=false;
		for(String str:setLib){
			if(token.startsWith(str)){
				//System.out.println(token);
				check=true;
				break;
			}
		}
		return check;
	}
	
	public static String getInvocationReceiverInLibrary(String info){
		String result="";
		String[] arrLine=info.split(SplitInvocationCharacter);
		//System.out.println(arrLine[0]);
		if(arrLine.length>4){
			String sigInfo=arrLine[arrLine.length-4];
			
			String[] arrSigs=sigInfo.split("#");
			if(arrSigs.length>=2){
				result=arrSigs[1];
			}
		}
		
		return result;
	}
	
	public static HashMap<String,String> getLibraryInfo(HashMap<String,String> mapTotalId){
		HashMap<String,String> map=new LinkedHashMap<String, String>();
		for(String key:mapTotalId.keySet()){
			String val=mapTotalId.get(key);
			String fqn=getInvocationReceiverInLibrary(val);
			map.put(key, fqn);
		}
		return map;
	}
	
	
	
	public static boolean checkIdentifierInfo(String tokenSource){
		boolean check=false;
		if(tokenSource.endsWith("#identifier")){
			check=true;
		}
		return check;
	}
	
	public static String getPackageAPIsInLibrary(HashSet<String> setLib,String token){
		String result="";
		for(String str:setLib){
			if(token.startsWith(str)){
				//System.out.println(token);
				result=str;
				break;
			}
		}
		return result;
	}
	
	public static String getMappingNumRange(ArrayList<String> lst,int numMap){
		String strResult="";
		if(numMap<=1){
			strResult=lst.get(0);
		} else if(numMap>=2 && numMap<=10){
			strResult=lst.get(1);
		} else if(numMap>=11 && numMap<=20){
			strResult=lst.get(2);
		} else if(numMap>=21 && numMap<=50){
			strResult=lst.get(3);
		} else if(numMap>=51 && numMap<=100){
			strResult=lst.get(4);
		} else {
			strResult=lst.get(5);
		}
		return strResult;
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String fopCurrentOutAnalysis=PathConstanct.PATH_PROJECT_TTT_CUR_EVAL_DATA;
		String fop_mapTotalId=PathConstanct.PATH_PROJECT_TTT_CUR_EVAL_DATA+File.separator+"map"+File.separator;
		String fnTestConfiguration="test4RandomTextContext";
		String fnAnalysis="analysis_"+fnTestConfiguration+".txt";
		HashMap<String,String> mapTotalId=MapUtil.getHashMapFromFile(fop_mapTotalId+"a_mapTotalIdAndContent.txt");
		HashMap<String,String> mapIdLibrary=getLibraryInfo(mapTotalId);
		String name_correct="Correct";
		String name_incorrect="Incorrect";
		String name_OOS="OOS";
		String name_OOT="OOT";
		
		String name_map_1="map_1";
		String name_map_2_10="map_2-10";
		String name_map_11_20="map_11-20";
		String name_map_21_50="map_21-50";
		String name_map_51_100="map_51-100";
		String name_map_greaterThan_100="map_greaterThan100";
		
		ArrayList<String> lstNameMap=new ArrayList<String>();
		lstNameMap.add(name_map_1);
		lstNameMap.add(name_map_2_10);
		lstNameMap.add(name_map_11_20);
		lstNameMap.add(name_map_21_50);
		lstNameMap.add(name_map_51_100);
		lstNameMap.add(name_map_greaterThan_100);
		
		
		for(int indexFold=1;indexFold<=10;indexFold++){
			String fnTuneName="b12_tune_fold-"+indexFold;
			String fop_input=PathConstanct.PATH_PROJECT_TTT_TOTAL_TUNE_DATA+File.separator+fnTuneName+File.separator;
			String fopTestDataAndResult=fop_input+fnTuneName+File.separator+fnTestConfiguration+File.separator;			
			String fop_output=fop_input+fnTuneName+File.separator+"eval_inside"+File.separator;
			
			new File(fop_output).mkdir();
			
			String fn_trainSource="train.s";
			String fn_trainTarget="train.t";
			String fn_vocabSource="vocab_train.s";
			String fn_vocabTarget="vocab_train.t";
			String fn_testSource="test.s";
			String fn_testTarget="test.t";
			String fn_testTranslation="test.tune.baseline.trans";
			String fn_result="result_all.txt";
			String fn_log_incorrect="log_incorrect.txt";
			String fn_log_outVocab="log_outVocab.txt";
			String fn_correctOrderTranslated="correctOrderTranslatedResult.txt";
			String fn_statisticCorrectMapping="correct_mapping.txt";
			String fn_statisticIncorrectMapping="incorrect_mappxting.txt";
			
			
			System.out.println(mapTotalId.size()+" Map total ID loaded!");
			ReorderingTokens.reorderingTokens(fopTestDataAndResult+fn_testSource,fopTestDataAndResult+fn_testTarget, fopTestDataAndResult+fn_testTranslation, fopTestDataAndResult+fn_correctOrderTranslated, mapTotalId);
			System.out.println("Finish reorder!");
			
			ArrayList<String> arrTrainSource=FileUtil.getFileStringArray(fopTestDataAndResult+fn_trainSource);
			ArrayList<String> arrTestSource=FileUtil.getFileStringArray(fopTestDataAndResult+fn_testSource);
			ArrayList<String> arrTestTarget=FileUtil.getFileStringArray(fopTestDataAndResult+fn_testTarget);
			ArrayList<String> arrTestTranslation=FileUtil.getFileStringArray(fopTestDataAndResult+fn_correctOrderTranslated);
			HashSet<String> setVocabTrainSource=new HashSet<String>();
			HashSet<String> setVocabTrainTarget=new HashSet<String>();
			HashSet<String> setVocabTrainMapping=new HashSet<String>();
//			HashMap<String,Integer> mapVocabTraining=new HashMap<String, Integer>();
			HashSet<String> set5Libraries=new HashSet<String>();
			HashMap<String,HashSet<String>> mapMapForEachIdentifiersDistinct=new LinkedHashMap<String, HashSet<String>>();
			HashMap<String,Integer> mapMapForEachIdentifiersCount=new LinkedHashMap<String, Integer>();

//			HashMap<String,HashMap<String,HashMap<String,HashSet<String>>>> mapAnalysisDistinctMapping=new LinkedHashMap<String, HashMap<String,HashMap<String,HashSet<String>>>>();
			HashMap<String,HashMap<String,HashMap<String,Integer>>> mapAnalysisAll=new LinkedHashMap<String, HashMap<String,HashMap<String,Integer>>>();
			
			set5Libraries.add("android");
			set5Libraries.add("com.google.gwt");
			set5Libraries.add("com.thoughtworks.xstream");
			set5Libraries.add("org.hibernate");
			set5Libraries.add("org.joda.time");		
					//set5Libraries.add("org.apache.commons.");
			set5Libraries.add("java");

			
			
			HashMap<String,HashMap<String,Integer>> mapCountPerLibrary=new HashMap<String, HashMap<String,Integer>>();
			
			for(String strItem:set5Libraries){
				HashMap<String,Integer> mapElement=new HashMap<String, Integer>();
				mapElement.put("Correct", 0);
				mapElement.put("Incorrect", 0);
				mapElement.put("OOT", 0);
				mapElement.put("OOS", 0);
				mapCountPerLibrary.put(strItem, mapElement);
				
				HashMap<String,HashMap<String,Integer>> mapMapStoreMapInt=new LinkedHashMap<String, HashMap<String,Integer>>();
				for(int q=0;q<lstNameMap.size();q++){
					HashMap<String,Integer> mapInt=new LinkedHashMap<String, Integer>();
					mapInt.put(name_correct, 0);
					mapInt.put(name_incorrect, 0);
//					mapInt.put(name_OOS, 0);
//					mapInt.put(name_OOT, 0);
					mapMapStoreMapInt.put(lstNameMap.get(q),mapInt);					
					
				}
				mapAnalysisAll.put(strItem, mapMapStoreMapInt);
				
				
				
			}
			
			
			ArrayList<String> arrTrainTarget=FileUtil.getFileStringArray(fop_input+fn_trainTarget);
			
			
			for(int i=0;i<arrTrainTarget.size();i++){
				String[] itemSource=arrTrainSource.get(i).trim().split("\\s+");
				
				String[] itemTarget=arrTrainTarget.get(i).trim().split("\\s+");
				for(int j=0;j<itemTarget.length;j++){
									
					if(!setVocabTrainTarget.contains(itemTarget[j])){
						setVocabTrainTarget.add(itemTarget[j]);
					}
					
					
					if(!setVocabTrainSource.contains(itemSource[j])){
						setVocabTrainSource.add(itemSource[j]);
					}
					String strMap=itemSource[j]+"_"+itemTarget[j];
					if(!setVocabTrainMapping.contains(strMap)){
						setVocabTrainMapping.add(strMap);
					}
					
					
					HashSet<String> setItemTargets=null;
					if(!mapMapForEachIdentifiersDistinct.containsKey(itemSource[j])){
						setItemTargets=new LinkedHashSet<String>();
						setItemTargets.add(itemTarget[j]);
						mapMapForEachIdentifiersDistinct.put(itemSource[j], setItemTargets);
						mapMapForEachIdentifiersCount.put(itemSource[j],1);
					} else{
						mapMapForEachIdentifiersDistinct.get(itemSource[j]).add(itemTarget[j]);
						mapMapForEachIdentifiersCount.put(itemSource[j],mapMapForEachIdentifiersCount.get(itemSource[j])+1);
					}
				}
										
			}
			arrTrainTarget.clear();
			
			
//			save to file
			StringBuilder sbVocab=new StringBuilder();
			for(String s:setVocabTrainSource){
				sbVocab.append(s+"\n");
			}
			FileIO.writeStringToFile(sbVocab.toString()+"\n",fop_input+fn_vocabSource);
			sbVocab=new StringBuilder();
			for(String s:setVocabTrainTarget){
				sbVocab.append(s+"\n");
			}
			FileIO.writeStringToFile(sbVocab.toString()+"\n",fop_input+fn_vocabTarget);
			
			int countOutOfSource=0,countOutOfTarget=0,countAllOutOfVocab=0,countIncorrect=0,countCorrect=0;
			FileUtil.writeToFile(fop_output+fn_result, "Correct"+"\t"+"Incorrect"+"\t"+"Out_of_source"+"\t"+"Out_of_target"+"\t"+"Out_of_vocab"+"\n");
			FileUtil.writeToFile(fop_output+fn_log_incorrect, "");
			
			PrintStream ptResult=null,ptIncorrect=null,ptOutVocab=null,ptCorrect_map=null,ptIncorrect_map=null,ptCorrectLibs[]=null,ptIncorrectLibs[]=null;
			try{
				ptResult=new PrintStream(new FileOutputStream(fop_output+fn_result));
				ptIncorrect=new PrintStream(new FileOutputStream(fop_output+fn_log_incorrect));
				// ptCorrectTranslated=new PrintStream(new FileOutputStream(fop_output+fn_correctOrderTranslated));
				ptOutVocab=new PrintStream(new FileOutputStream(fop_output+fn_log_outVocab));
				ptCorrect_map=new PrintStream(new FileOutputStream(fop_output+fn_statisticCorrectMapping));
				ptIncorrect_map=new PrintStream(new FileOutputStream(fop_output+fn_statisticIncorrectMapping));
				
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			for(int i=0;i<arrTestSource.size();i++){
//				if(!lstNotReorderedLine.contains(i+1)){
//					continue;
//				}
				HashSet<String> setIncorrect=new HashSet<String>();
				HashSet<String> setOutSource=new HashSet<String>();
				HashSet<String> setOutTarget=new HashSet<String>();
				String[] itemSource=arrTestSource.get(i).trim().split("\\s+");
				String[] itemTarget=arrTestTarget.get(i).trim().split("\\s+");
				String[] itemTrans=arrTestTranslation.get(i).trim().split("\\s+");
				String strIncorrectLog="",strOutSource="",strOutTarget="";
				
				
				
				int numCSourceLine=0,numCTargetLine=0,numIncorrect=0,numCorrect=0;
				System.out.println("Line "+i);
				for(int j=0;j<itemSource.length;j++){
					
					//&&(!itemTrans[j].startsWith("."))
					if(checkIdentifierInfo( itemSource[j]) && checkAPIsInLibrary(set5Libraries, mapIdLibrary.get(itemTarget[j]))){
						/*System.out.println("target info "+mapIdLibrary.get(itemTarget[j]));
						System.out.println("source info "+itemSource[j]);
						System.out.println("all info "+mapTotalId.get(itemTarget[j]));
						*/
					String strTargetCode=mapIdLibrary.get(itemTarget[j]);
					String strTargetAPIInfo=mapTotalId.get(itemTarget[j]);
					String strTransAPIInfo=mapTotalId.get(itemTrans[j]);
					String strPackageName=getPackageAPIsInLibrary(set5Libraries, strTargetCode);
					
					
					
					if(!setVocabTrainSource.contains(itemSource[j])){
							numCSourceLine++;
							int currentNumber=mapCountPerLibrary.get(strPackageName).get("OOS");
							mapCountPerLibrary.get(strPackageName).put("OOS",currentNumber+1);						
							if(!setOutSource.contains(itemSource[j])){
								strOutSource+=itemSource[j]+" ";
								setOutSource.add(itemSource[j]);

							}
//							HashMap<String,Integer> mapMappingInfo=mapAnalysisAll.get(strPackageName).g
						}
					//itemSource[j]+"_"+itemTarget[j]
					else if(!setVocabTrainMapping.contains(itemSource[j]+"_"+itemTarget[j])){
						//else if(!setVocabTrainTarget.contains(itemTarget[j])){
							numCTargetLine++;
							
							int currentNumber=mapCountPerLibrary.get(strPackageName).get("OOT");
							mapCountPerLibrary.get(strPackageName).put("OOT",currentNumber+1);
							
							
							if(!setOutTarget.contains(itemTarget[j])){
								strOutTarget+=itemTarget[j]+" ";
								setOutTarget.add(itemTarget[j]);
							}
						}else if(itemTarget[j].equals(itemTrans[j])){
							numCorrect++;
							
							int currentNumber=mapCountPerLibrary.get(strPackageName).get("Correct");
							mapCountPerLibrary.get(strPackageName).put("Correct",currentNumber+1);
							ptCorrect_map.print((i+1)+"\t"+itemSource[j]+"\t"+itemTarget[j]+"\t"+strTargetAPIInfo+"\n");
//							mapCorrectPrintScreen.get(strPackageName).print(itemSource[j]+","+mapVocabTraining.get(itemSource[j])+"\n");
							int numMapPerSource=mapMapForEachIdentifiersCount.get(i);
							String strItemUpdateMap=getMappingNumRange(lstNameMap, numMapPerSource);
							HashMap<String,Integer> mapMapInside = mapAnalysisAll.get(strPackageName).get(strItemUpdateMap);
							mapMapInside.put(name_correct, mapMapInside.get(name_correct)+1);
						} else{
							numIncorrect++;
							int currentNumber=mapCountPerLibrary.get(strPackageName).get("Incorrect");
							mapCountPerLibrary.get(strPackageName).put("Incorrect",currentNumber+1);
						//	if(!setIncorrect.contains(itemTrans[j]+"(Correct: "+itemTarget[j]+") ")){
								strIncorrectLog+=itemTrans[j]+"(Correct: "+itemTarget[j]+") ";
								setIncorrect.add(itemTrans[j]+"(Correct: "+itemTarget[j]+") ");
							//}
//								if(itemSource[j].equals("OnPreDrawListener()")){
//									System.out.println("line "+i);
//									Scanner sc=new Scanner(System.in);
//									sc.next();
//								}
								//
								
								ptIncorrect_map.print((i+1)+"\t"+itemSource[j]+"\t"+itemTrans[j]+"\t"+itemTarget[j]+"\t"+strTransAPIInfo+"\t"+strTargetAPIInfo+"\n");
//								mapIncorrectPrintScreen.get(strPackageName).print(itemSource[j]+","+mapVocabTraining.get(itemSource[j])+","+itemTarget[j]+"\n");
								int numMapPerSource=mapMapForEachIdentifiersCount.get(i);
								
								String strItemUpdateMap=getMappingNumRange(lstNameMap, numMapPerSource);
								HashMap<String,Integer> mapMapInside = mapAnalysisAll.get(strPackageName).get(strItemUpdateMap);
								mapMapInside.put(name_incorrect, mapMapInside.get(name_incorrect)+1);
						}
					}
													
				}
				countCorrect+=numCorrect;
				countIncorrect+=numIncorrect;
				countOutOfSource+=numCSourceLine;
				countOutOfTarget+=numCTargetLine;
				countAllOutOfVocab+=numCSourceLine+numCTargetLine;
				ptResult.print("Line "+(i+1)+" (correct/incorrect/OOS/OOT/OOV): "+numCorrect+"\t"+numIncorrect+"\t"+numCSourceLine+"\t"+numCTargetLine+"\t"+(numCSourceLine+numCTargetLine)+"\n");
				ptIncorrect.print("Line "+(i+1)+" : "+strIncorrectLog+"\n");
				ptOutVocab.print("Line "+(i+1)+" : "+strOutSource.trim()+" ||| "+strOutTarget.trim()+"\n");
				//FileUtil.appendToFile(fop_input+fn_result, numCorrect+"\t"+numIncorrect+"\t"+numCSourceLine+"\t"+numCTargetLine+"\t"+(numCSourceLine+numCTargetLine)+"\n");
		//		FileUtil.appendToFile(fop_input+fn_log_incorrect, strIncorrectLog+"\n");
				
			}
			
			try{
				ptResult.close();
				ptIncorrect.close();
			//	ptCorrectTranslated.close();
				ptOutVocab.close();
				ptCorrect_map.close();
				ptIncorrect_map.close();
				
			}catch(Exception ex){
				
			}
			double precision=countCorrect*1.0/(countCorrect+countIncorrect);
			double recall=countCorrect*1.0/(countCorrect+countIncorrect+countAllOutOfVocab);
			double f1score=precision*recall*2/(precision+recall);
			
			FileUtil.appendToFile(fop_output+fn_result, countCorrect+"\t"+countIncorrect+"\t"+countOutOfSource+"\t"+countOutOfTarget+"\t"+countAllOutOfVocab+"\t"+precision+"\t"+recall+"\t"+f1score+"\n");
			FileUtil.appendToFile(fop_output+fn_result, "Precision in-vocab: "+countCorrect*1.0/(countCorrect+countIncorrect)+"\n");
			FileUtil.appendToFile(fop_output+fn_result, "Recall out-vocab: "+countCorrect*1.0/(countCorrect+countIncorrect+countAllOutOfVocab)+"\n");
			
			for(String strItem:mapCountPerLibrary.keySet()){
				HashMap<String,Integer> mapTemp=mapCountPerLibrary.get(strItem);
				precision=mapTemp.get("Correct")*1.0/(mapTemp.get("Correct")+mapTemp.get("Incorrect"));
				recall=mapTemp.get("Correct")*1.0/(mapTemp.get("Correct")+mapTemp.get("Incorrect")+(mapTemp.get("OOS")+mapTemp.get("OOT")));
				f1score=precision*recall*2/(precision+recall);
				FileUtil.appendToFile(fop_output+fn_result, strItem+": "+mapTemp.get("Correct")+"\t"+mapTemp.get("Incorrect")+"\t"+mapTemp.get("OOS")+"\t"+mapTemp.get("OOT")+"\t"+(mapTemp.get("OOS")+mapTemp.get("OOT"))+"\t"+precision+"\t"+recall+"\t"+f1score+"\n");
			}
			StringBuilder sbInfoAnalysisMap=new StringBuilder();
			sbInfoAnalysisMap.append("\t");
			for(String strItem:lstNameMap){
				sbInfoAnalysisMap.append(strItem+"\t\t");
			}
			sbInfoAnalysisMap.append("\n");
			
			for(String strLib:set5Libraries){
				sbInfoAnalysisMap.append(strLib+"\t");
				for(String strItem:lstNameMap){
					HashMap<String,Integer> mapInt=mapAnalysisAll.get(strLib).get(strItem);
					sbInfoAnalysisMap.append(mapInt.get(name_correct)+"\t"+mapInt.get(name_incorrect)+"\t");
				}
				sbInfoAnalysisMap.append("\n");
			}
			FileIO.writeStringToFile(sbInfoAnalysisMap.toString()+"\n", fopCurrentOutAnalysis+fnAnalysis);
		}
		
		
		
		
	}

}
