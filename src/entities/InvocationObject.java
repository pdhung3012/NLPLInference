package entities;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;

import utils.FileIO;
import utils.StringUtil;

public class InvocationObject {
	private String id;
	private String strCodeRepresent;
	private String strQuestionMarkTypes;
	private String strMethodInfo;
	private LinkedHashSet<String> setImportedAPIs;
	private ArrayList<String> listQuestionMarkTypes;
	private String strIDRepresent;
	private String strIdentifier;
	private ArrayList<String> listOfRelatedWordsTarget=new ArrayList<String>();
	private ArrayList<String> listOfRelatedWordsSource=new ArrayList<String>();
	
	
	
	
	public ArrayList<String> getListOfRelatedWordsTarget() {
		return listOfRelatedWordsTarget;
	}
	public void setListOfRelatedWordsTarget(
			ArrayList<String> listOfRelatedWordsTarget) {
		this.listOfRelatedWordsTarget = listOfRelatedWordsTarget;
	}
	public ArrayList<String> getListOfRelatedWordsSource() {
		return listOfRelatedWordsSource;
	}
	public void setListOfRelatedWordsSource(
			ArrayList<String> listOfRelatedWordsSource) {
		this.listOfRelatedWordsSource = listOfRelatedWordsSource;
	}
	public String getStrIdentifier() {
		return strIdentifier;
	}
	public void setStrIdentifier(String strIdentifier) {
		this.strIdentifier = strIdentifier;
	}
	public String getStrIDRepresent() {
		return strIDRepresent;
	}
	public void setStrIDRepresent(String strIDRepresent) {
		this.strIDRepresent = strIDRepresent;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getStrCodeRepresent() {
		return strCodeRepresent;
	}
	public void setStrCodeRepresent(String strCodeRepresent) {
		this.strCodeRepresent = strCodeRepresent;
	}
	public String getStrQuestionMarkTypes() {
		return strQuestionMarkTypes;
	}
	public void setStrQuestionMarkTypes(String strQuestionMarkTypes) {
		this.strQuestionMarkTypes = strQuestionMarkTypes;
	}
	public LinkedHashSet<String> getSetImportedAPIs() {
		return setImportedAPIs;
	}
	public void setSetImportedAPIs(LinkedHashSet<String> setImportedAPIs) {
		this.setImportedAPIs = setImportedAPIs;
	}
	public ArrayList<String> getListQuestionMarkTypes() {
		return listQuestionMarkTypes;
	}
	public void setListQuestionMarkTypes(ArrayList<String> listQuestionMarkTypes) {
		this.listQuestionMarkTypes = listQuestionMarkTypes;
	}
	
	
	
	public String getStrMethodInfo() {
		return strMethodInfo;
	}
	public void setStrMethodInfo(String strMethodInfo) {
		this.strMethodInfo = strMethodInfo;
	}
	
	public String setIDRepresent(){
		StringBuilder sbContent=new StringBuilder();
		sbContent.append(strCodeRepresent.replaceAll("\n", "").trim()+"_");
		String contentMarkType="";
		for(int i=0;i<listQuestionMarkTypes.size();i++){
			contentMarkType+=listQuestionMarkTypes.get(i);
			if(i!=listQuestionMarkTypes.size()-1){
				contentMarkType+="#";
			}
		}
		sbContent.append(contentMarkType.replaceAll("\n", "").trim()+"_");
		String contentImportType="";
		for(String strAPI:setImportedAPIs){
			contentImportType+=strAPI+"#";
		}
		sbContent.append(contentImportType.replaceAll("\n", "").trim());
		strIDRepresent=sbContent.toString().trim();
		return strIDRepresent;
	}
	
	public static String getAllInfoInFile(String file){
		
		String[] arrContent=FileIO.readStringFromFile(file).split("\n");
		StringBuilder sb=new StringBuilder();
		if(arrContent.length>=7){
			sb.append(arrContent[0]+"$%$");
			sb.append(arrContent[1]+"$%$");
			sb.append(arrContent[2]+"$%$");
			sb.append(arrContent[3]+"$%$");
			sb.append(arrContent[4]+"$%$");
			sb.append(arrContent[5]+"$%$");
			sb.append(arrContent[6]+"$%$");
		} else if(arrContent.length>=5){
			sb.append(arrContent[0]+"$%$");
			sb.append(arrContent[1]+"$%$");
			sb.append(arrContent[2]+"$%$");
			sb.append(arrContent[3]+"$%$");
			sb.append(arrContent[4]+"$%$");
		}
		return sb.toString();
	}
	
	public String getStrFromList(ArrayList<String> lst){
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<lst.size();i++){
			sb.append(lst.get(i)+" ");
		}
		return sb.toString().trim();
	}
	
	public void saveToFile(String file){
		StringBuilder sbContent=new StringBuilder();
		sbContent.append(strCodeRepresent.replaceAll("\n", "")+"\n");
//		sbContent.append(id+"\n");
		String contentMarkType="";
		for(int i=0;i<listQuestionMarkTypes.size();i++){
			contentMarkType+=listQuestionMarkTypes.get(i);
			if(i!=listQuestionMarkTypes.size()-1){
				contentMarkType+="#";
			}
		}
		sbContent.append(contentMarkType.replaceAll("\n", "")+"\n");
		String contentImportType="";
		for(String strAPI:setImportedAPIs){
			contentImportType+=strAPI+"#";
		}
		sbContent.append(contentImportType.replaceAll("\n", "")+"\n");
		String strListSource=getStrFromList(listOfRelatedWordsSource);
		String strListTarget=getStrFromList(listOfRelatedWordsTarget);
		sbContent.append(strMethodInfo.replaceAll("\n", "")+"\n");
		sbContent.append(strListSource.replaceAll("\n", "")+"\n");
		sbContent.append(strListTarget.replaceAll("\n", "")+"\n");
		sbContent.append(strIdentifier.replaceAll("\n", "")+"\n");
		FileIO.writeStringToFile(sbContent.toString(), file);
		
	}
	
}
