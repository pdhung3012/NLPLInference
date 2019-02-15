package entities;

import java.util.LinkedHashSet;

import org.eclipse.jdt.core.dom.MethodDeclaration;

public class LocalForMethod {
	private LinkedHashSet<LocalEntity> setFields, setArguments,
	setLocalVariables;
	private MethodDeclaration method;
	
	public LocalForMethod(){
		
	}
	public LinkedHashSet<LocalEntity> getSetFields() {
		return setFields;
	}
	public void setSetFields(LinkedHashSet<LocalEntity> setFields) {
		this.setFields = setFields;
	}
	public LinkedHashSet<LocalEntity> getSetArguments() {
		return setArguments;
	}
	public void setSetArguments(LinkedHashSet<LocalEntity> setArguments) {
		this.setArguments = setArguments;
	}
	public LinkedHashSet<LocalEntity> getSetLocalVariables() {
		return setLocalVariables;
	}
	public void setSetLocalVariables(LinkedHashSet<LocalEntity> setLocalVariables) {
		this.setLocalVariables = setLocalVariables;
	}
	public MethodDeclaration getMethod() {
		return method;
	}
	public void setMethod(MethodDeclaration method) {
		this.method = method;
	}
	
	
	
}
