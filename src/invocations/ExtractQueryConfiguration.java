package invocations;

public class ExtractQueryConfiguration {

	public static int TypeMLModel_Compact=1;
	public static int TypeMLModel_EmbedInfo=2;
	
	private int typeOfMLModel=TypeMLModel_EmbedInfo;
	private String fopMappedDictionaryTrain="";
	private String fopMappedDictionaryTest="";
	
	
	
	public int getTypeOfMLModel() {
		return typeOfMLModel;
	}



	public void setTypeOfMLModel(int typeOfMLModel) {
		this.typeOfMLModel = typeOfMLModel;
	}



	public String getFopMappedDictionaryTrain() {
		return fopMappedDictionaryTrain;
	}



	public void setFopMappedDictionaryTrain(String fopMappedDictionaryTrain) {
		this.fopMappedDictionaryTrain = fopMappedDictionaryTrain;
	}



	public String getFopMappedDictionaryTest() {
		return fopMappedDictionaryTest;
	}



	public void setFopMappedDictionaryTest(String fopMappedDictionaryTest) {
		this.fopMappedDictionaryTest = fopMappedDictionaryTest;
	}



	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
