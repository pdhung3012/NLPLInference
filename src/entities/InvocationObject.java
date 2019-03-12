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
		
		sbContent.append(strMethodInfo.replaceAll("\n", "")+"\n");
		sbContent.append(strIdentifier.replaceAll("\n", "")+"\n");
		FileIO.writeStringToFile(sbContent.toString(), file);
		
	}
	
}
