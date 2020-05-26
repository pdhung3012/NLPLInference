package nlSupport;

import java.util.HashSet;
import java.util.LinkedHashSet;

public class ObjectMatchedVariablesInNL {
	
	private String varName;
	private String varType;
	private boolean isMatch=false;
	private HashSet<Integer> matchPositionInCode=new LinkedHashSet<Integer>();

	
	
	public String getVarName() {
		return varName;
	}



	public void setVarName(String varName) {
		this.varName = varName;
	}



	public String getVarType() {
		return varType;
	}



	public void setVarType(String varType) {
		this.varType = varType;
	}



	public boolean isMatch() {
		return isMatch;
	}



	public void setMatch(boolean isMatch) {
		this.isMatch = isMatch;
	}



	



	public HashSet<Integer> getMatchPositionInCode() {
		return matchPositionInCode;
	}
	
	public void addMatchPositionInCode(int pos) {
		if(matchPositionInCode==null) {
			matchPositionInCode=new LinkedHashSet<Integer>();
		}
		matchPositionInCode.add(pos);
	}



	public void setMatchPositionInCode(HashSet<Integer> matchPositionInCode) {
		this.matchPositionInCode = matchPositionInCode;
	}



	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
