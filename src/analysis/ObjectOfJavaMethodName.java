package analysis;

import java.util.Comparator;

public class ObjectOfJavaMethodName {

	private int lineNumber;
	private String strContentInLine;
	private double precisionPerLine;
	private int numberOfMethodTokens;
	
	public ObjectOfJavaMethodName() {
		precisionPerLine=0;
		numberOfMethodTokens=0;
		lineNumber=-1;
		strContentInLine="";
	}
	
	/*Comparator for sorting the list by Student Name*/
    public static Comparator<ObjectOfJavaMethodName> NumberMethodTokensComparator = new Comparator<ObjectOfJavaMethodName>() {

	public int compare(ObjectOfJavaMethodName s1, ObjectOfJavaMethodName s2) {
			int result=0;
			if(s1.numberOfMethodTokens!=s2.numberOfMethodTokens) {
				result=new Integer(s1.numberOfMethodTokens).compareTo(new Integer(s2.numberOfMethodTokens));
			} else {
				result=new Double(s1.precisionPerLine).compareTo(new Double(s2.precisionPerLine));
			}
			return result;
	       }};

	
	public double getPrecisionPerLine() {
		return precisionPerLine;
	}



	public void setPrecisionPerLine(double precisionPerLine) {
		this.precisionPerLine = precisionPerLine;
	}



	public int getNumberOfMethodTokens() {
		return numberOfMethodTokens;
	}



	public void setNumberOfMethodTokens(int numberOfMethodTokens) {
		this.numberOfMethodTokens = numberOfMethodTokens;
	}



	public int getLineNumber() {
		return lineNumber;
	}



	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}



	public String getStrContentInLine() {
		return strContentInLine;
	}



	public void setStrContentInLine(String strContentInLine) {
		this.strContentInLine = strContentInLine;
	}



	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
