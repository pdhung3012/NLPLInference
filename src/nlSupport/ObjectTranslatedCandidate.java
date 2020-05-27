package nlSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class ObjectTranslatedCandidate implements Comparable< ObjectTranslatedCandidate >{

	private String strCodeInfo;
	private String strCodeExprId;
	private String sreCodeOnly;
	private String strCodeVarInfo;
	private String strCodeImport;
	
	private ArrayList<ObjectMatchedVariablesInNL> listMatchedVarsInNL;
	private ArrayList<ObjectTermInNL> listTermInNL;
	private ArrayList<ObjectMatchedVarTypeInCode> listMatchedVarType;
	LinkedHashSet<String> setTermsInCode;
	
	private int numResovedTypesInNL=0;
	private int totalTypesInNL=0;
	private int numWordMatchInNL=0;
	private int totalWordMatchInNL=0;
	private int numResolvedTypeInCode=0;
	private int totalResolvedTypeInCode=0;
	
	private double scoreTotal=0.0;
	private String strCodeFinal;
	
	
	
	
	
	
	
	
	public String getStrCodeFinal() {
		return strCodeFinal;
	}
	public void setStrCodeFinal(String strCodeFinal) {
		this.strCodeFinal = strCodeFinal;
	}
	public double getScoreTotal() {
		return scoreTotal;
	}
	public void setScoreTotal(double scoreTotal) {
		this.scoreTotal = scoreTotal;
	}
	public LinkedHashSet<String> getSetTermsInCode() {
		return setTermsInCode;
	}
	public void setSetTermsInCode(LinkedHashSet<String> setTermsInCode) {
		this.setTermsInCode = setTermsInCode;
	}
	public ArrayList<ObjectMatchedVarTypeInCode> getListMatchedVarType() {
		return listMatchedVarType;
	}
	public void setListMatchedVarType(ArrayList<ObjectMatchedVarTypeInCode> listMatchedVarType) {
		this.listMatchedVarType = listMatchedVarType;
	}
	public ArrayList<ObjectMatchedVariablesInNL> getListMatchedVarsInNL() {
		return listMatchedVarsInNL;
	}
	public void setListMatchedVarsInNL(ArrayList<ObjectMatchedVariablesInNL> listMatchedVarsInNL) {
		this.listMatchedVarsInNL = listMatchedVarsInNL;
	}
	public ArrayList<ObjectTermInNL> getListTermInNL() {
		return listTermInNL;
	}
	public void setListTermInNL(ArrayList<ObjectTermInNL> listTermInNL) {
		this.listTermInNL = listTermInNL;
	}
	public String getStrCodeInfo() {
		return strCodeInfo;
	}
	public void setStrCodeInfo(String strCodeInfo) {
		this.strCodeInfo = strCodeInfo;
	}
	public String getStrCodeExprId() {
		return strCodeExprId;
	}
	public void setStrCodeExprId(String strCodeExprId) {
		this.strCodeExprId = strCodeExprId;
	}
	public String getSreCodeOnly() {
		return sreCodeOnly;
	}
	public void setSreCodeOnly(String sreCodeOnly) {
		this.sreCodeOnly = sreCodeOnly;
	}
	public String getStrCodeVarInfo() {
		return strCodeVarInfo;
	}
	public void setStrCodeVarInfo(String strCodeVarInfo) {
		this.strCodeVarInfo = strCodeVarInfo;
	}
	public String getStrCodeImport() {
		return strCodeImport;
	}
	public void setStrCodeImport(String strCodeImport) {
		this.strCodeImport = strCodeImport;
	}
	public int getNumResovedTypesInNL() {
		return numResovedTypesInNL;
	}
	public void setNumResovedTypesInNL(int numResovedTypesInNL) {
		this.numResovedTypesInNL = numResovedTypesInNL;
	}
	public int getTotalTypesInNL() {
		return totalTypesInNL;
	}
	public void setTotalTypesInNL(int totalTypesInNL) {
		this.totalTypesInNL = totalTypesInNL;
	}
	public int getNumWordMatchInNL() {
		return numWordMatchInNL;
	}
	public void setNumWordMatchInNL(int numWordMatchInNL) {
		this.numWordMatchInNL = numWordMatchInNL;
	}
	public int getTotalWordMatchInNL() {
		return totalWordMatchInNL;
	}
	public void setTotalWordMatchInNL(int totalWordMatchInNL) {
		this.totalWordMatchInNL = totalWordMatchInNL;
	}
	public int getNumResolvedTypeInCode() {
		return numResolvedTypeInCode;
	}
	public void setNumResolvedTypeInCode(int numResolvedTypeInCode) {
		this.numResolvedTypeInCode = numResolvedTypeInCode;
	}
	public int getTotalResolvedTypeInCode() {
		return totalResolvedTypeInCode;
	}
	public void setTotalResolvedTypeInCode(int totalResolvedTypeInCode) {
		this.totalResolvedTypeInCode = totalResolvedTypeInCode;
	}
	
	public boolean isMatch(String nlType,String codeType) {
		boolean result=false;
		if(codeType.equals(nlType)) {
			result=true;
		}
		return result;
	}
	
	public double calculateScoreAndMatchVariable() {
		double resultScore=0;
		numResovedTypesInNL=0;
		numResolvedTypeInCode=0;
		totalTypesInNL=listMatchedVarsInNL.size();
		totalResolvedTypeInCode=listMatchedVarType.size();
		HashSet<String> setVariables=new LinkedHashSet<String>();
		
		for(int j=0;j<listMatchedVarType.size();j++) {
			ObjectMatchedVarTypeInCode itemCode=listMatchedVarType.get(j);
//			loop 1: prioritize to non match variable first
			boolean isMatchForItemVarInCode=false;
			for(int i=0;i<listMatchedVarsInNL.size();i++) {
//				finding match for vars in NL
				ObjectMatchedVariablesInNL itemNL=listMatchedVarsInNL.get(i);
				
				if(!itemNL.isMatch() && isMatch(itemNL.getVarType(), itemCode.getClassName())) {
					itemNL.setMatch(true);
					itemNL.addMatchPositionInCode(j);
					itemCode.setMatch(true);
					isMatchForItemVarInCode=true;
				}
				setVariables.add(itemNL.getVarName().toLowerCase());
				break;
				
			}
			
//			loop 2: match any possible values
			if(!isMatchForItemVarInCode) {
				for(int i=0;i<listMatchedVarsInNL.size();i++) {
//					finding match for vars in NL
					ObjectMatchedVariablesInNL itemNL=listMatchedVarsInNL.get(i);
					
					if(isMatch(itemNL.getVarType(), itemCode.getClassName())) {
						itemNL.setMatch(true);
						itemNL.addMatchPositionInCode(j);
						itemCode.setMatch(true);
					}
					setVariables.add(itemNL.getVarName().toLowerCase());
					break;
					
				}
			}
			
			
		}
		
		
		for(int i=0;i<listMatchedVarsInNL.size();i++) {
			ObjectMatchedVariablesInNL itemNL=listMatchedVarsInNL.get(i);
			if(itemNL.isMatch()) {
				numResovedTypesInNL++;
			}
		}
		for(int i=0;i<listMatchedVarType.size();i++) {
			ObjectMatchedVarTypeInCode itemCode=listMatchedVarType.get(i);
			
			if(itemCode.isMatch()) {
				numResolvedTypeInCode++;
			}
		}
		
		
		double scoreMatchedVarInNL=totalTypesInNL!=0?(numResovedTypesInNL*1.0/totalTypesInNL):1;
		double scoreMatchedVarInCode=totalResolvedTypeInCode!=0?(numResolvedTypeInCode*1.0/totalResolvedTypeInCode):1;
		double scoreOfEmptyExpression=(totalTypesInNL==0 && totalResolvedTypeInCode==0)?0.8:1;
		numWordMatchInNL=0;
		totalWordMatchInNL=listTermInNL.size()-setVariables.size();
				
		for(int i=0;i<listTermInNL.size();i++) {
			ObjectTermInNL itemTerm=listTermInNL.get(i);
			if(setTermsInCode.contains(itemTerm.getTerm()) && !setVariables.contains(itemTerm.getTerm())){
				numWordMatchInNL++;
			}
		}
		System.out.println(setTermsInCode.toString());
		
		double scoreMatchedWordInNL=totalWordMatchInNL!=0?(numWordMatchInNL*1.0/totalWordMatchInNL):1;
		
		resultScore=0.6*scoreMatchedVarInNL+0.2*scoreMatchedVarInCode+0.1*scoreOfEmptyExpression+0.1*scoreMatchedWordInNL;
		scoreTotal=resultScore;
		
//		get code in final:
		HashMap<Integer,ObjectMatchedVariablesInNL> mapPositionsInCode=new HashMap<Integer, ObjectMatchedVariablesInNL>(); 
		for(int i=0;i<listMatchedVarsInNL.size();i++) {
			ObjectMatchedVariablesInNL itemNL=listMatchedVarsInNL.get(i);
			HashSet<Integer> setPos=itemNL.getMatchPositionInCode();
			for(Integer key:setPos) {
				mapPositionsInCode.put(key, itemNL);
			}
		}
		
		StringBuilder res = new StringBuilder();
		int indexOfVarInCode=0;
		for(int i = 0; i < strCodeInfo.length(); i++) {
		   Character ch = strCodeInfo.charAt(i);
		     if(ch=='#') {
		    	 if(mapPositionsInCode.containsKey(indexOfVarInCode)) {
		    		 String varName=mapPositionsInCode.get(indexOfVarInCode).getVarName();
		    		 res.append( varName); 
			    	 
		    	 } else {
		    		 res.append("#");
		    	 }
		    	 indexOfVarInCode++;
		     } else {
		    	 res.append(ch); 
		     }
		       
		}
		strCodeFinal=res.toString();
		
		return resultScore;
	}
	@Override
	public int compareTo(ObjectTranslatedCandidate o) {
		// TODO Auto-generated method stub
		return new Double(this.getScoreTotal()).compareTo(o.getScoreTotal());
	}
	
	

}
