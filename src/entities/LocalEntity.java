package entities;

import java.util.HashSet;

public class LocalEntity {
	private String strCodeReprensent;
	private String strTypeOfEntity;
	HashSet<String> setCodeLocal;
	
	
	public String getStrCodeReprensent() {
		return strCodeReprensent;
	}

	public boolean checkCodeInLocalRepresent(String content){
		
		if(setCodeLocal==null){
			setCodeLocal=new HashSet<String>();
			setCodeLocal.add(strCodeReprensent);
			setCodeLocal.add("this."+strCodeReprensent);
		}
		return setCodeLocal.contains(content);
	}


	public void setStrCodeReprensent(String strCodeReprensent) {
		this.strCodeReprensent = strCodeReprensent;
	}



	public String getStrTypeOfEntity() {
		return strTypeOfEntity;
	}



	public void setStrTypeOfEntity(String strTypeOfEntity) {
		this.strTypeOfEntity = strTypeOfEntity;
	}



	public LocalEntity(){
		strCodeReprensent="";
		strTypeOfEntity="";
	}
}
