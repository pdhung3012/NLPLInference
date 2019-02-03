package entities;

public class LocalEntity {
	private String strCodeReprensent;
	private String strTypeOfEntity;
	
	
	
	public String getStrCodeReprensent() {
		return strCodeReprensent;
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
