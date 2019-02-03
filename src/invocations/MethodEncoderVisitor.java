package invocations;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.FileASTRequestor;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import utils.JavaASTUtil;
import entities.LocalEntity;

public class MethodEncoderVisitor extends ASTVisitor {

	protected StringBuffer buffer;
	private HashMap<String, String> setSequencesOfMethods, setOfUnResolvedType;
	private LinkedHashSet<LocalEntity> setFields, setArguments,
			setLocalVariables;
	private boolean isVisistMethod=false;

	ASTParser parser = ASTParser.newParser(AST.JLS4);
	String[] classpath = { "C:\\Program Files\\Java\\jre1.8.0_51\\lib\\rt.jar" };
	HashMap<String, CompilationUnit> mapCU;

	public HashMap<String, String> getSetSequencesOfMethods() {
		return setSequencesOfMethods;
	}

	public void setSetSequencesOfMethods(
			HashMap<String, String> setSequencesOfMethods) {
		this.setSequencesOfMethods = setSequencesOfMethods;
	}

	public HashMap<String, String> getSetOfUnResolvedType() {
		return setOfUnResolvedType;
	}

	public void setSetOfUnResolvedType(
			HashMap<String, String> setOfUnResolvedType) {
		this.setOfUnResolvedType = setOfUnResolvedType;
	}

	public void parseProject(String projectLocation,String jdkPath) {
		Map<String, String> options = JavaCore.getOptions();
		String[] arrChildJars=utils.FileIO.findAllJarFiles(projectLocation);
		String[] jarPaths=utils.FileIO.combineFilesToArray(jdkPath, arrChildJars);
//		String[] jarPaths = { jdkPath };
//		File f = new File(fileLocation);
		String[] filePaths = utils.FileIO.findAllJavaFiles(projectLocation);
		String[] sources = { projectLocation
				+ File.separator };
//		System.out.println(f.getParentFile().getAbsolutePath());
		// System.out.println("jdk :" +this.jdkPath);
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		JavaCore.setComplianceOptions(JavaCore.VERSION_1_8, options);
		parser.setCompilerOptions(options);
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		parser.setEnvironment(jarPaths, sources, null, true);
		
//		String strCode=FileIO.readStringFromFile(fileLocation );
//		parser.setSource(strCode.toCharArray());
		mapCU=new LinkedHashMap<String, CompilationUnit>();
		setArguments=new LinkedHashSet<>();
		setLocalVariables=new LinkedHashSet<LocalEntity>();
		setFields=new LinkedHashSet<LocalEntity>();
		final MethodEncoderVisitor visitor = this;
		parser.createASTs(filePaths, null, new String[] {},
				new FileASTRequestor() {
					@Override
					public void acceptAST(String sourceFilePath,
							CompilationUnit javaUnit) {
//						javaUnit.accept(visitor);
						mapCU.put(sourceFilePath, javaUnit);
					}
				}, null);
		
	}
	
	public void parseFile(String fileLocation) {
		try{
			CompilationUnit cu=mapCU.get(fileLocation);
			cu.accept(this);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public StringBuilder viewAllLocalInformation(){
		StringBuilder sb=new StringBuilder();
		sb.append("setLocalVariable "+setLocalVariables.size()+": ");
		for(LocalEntity ent:setLocalVariables){
			sb.append(ent.getStrCodeReprensent()+" - "+ent.getStrTypeOfEntity()+",");
		}
		sb.append("\n");
		sb.append("setArgument "+setArguments.size()+": ");
		for(LocalEntity ent:setArguments){
			sb.append(ent.getStrCodeReprensent()+" - "+ent.getStrTypeOfEntity()+",");
		}
		sb.append("\n");
		sb.append("setField "+setFields.size()+": ");
		for(LocalEntity ent:setFields){
			sb.append(ent.getStrCodeReprensent()+" - "+ent.getStrTypeOfEntity()+",");
		}
		sb.append("\n");
		
		
		return sb;
	}
	

	public boolean visit(TypeDeclaration node) {
		setFields.clear();
		FieldDeclaration[] arrFields = node.getFields();
		for (int i = 0; i < arrFields.length; i++) {
			List<VariableDeclarationFragment> arrDeclaration = arrFields[i]
					.fragments();
			for (int j = 0; j < arrDeclaration.size(); j++) {
				VariableDeclarationFragment item = arrDeclaration.get(j);
				IVariableBinding varBind = item.resolveBinding();
				SimpleName varName = item.getName();
				if (varBind != null && varName != null) {
					ITypeBinding typeBind = varBind.getType();
					if (typeBind != null) {
						LocalEntity le = new LocalEntity();
						le.setStrCodeReprensent(varName.getIdentifier());
						le.setStrTypeOfEntity(typeBind.getQualifiedName());
						setFields.add(le);
					}

				}
			}

		}
		return true;
	}

	public boolean visit(FieldDeclaration node) {
		// node.fragments();
		return false;
	}

	public boolean visit(MethodDeclaration node) {

		setArguments.clear();
		setLocalVariables.clear();
		Iterator<SingleVariableDeclaration> parameters = node.parameters()
				.iterator();
		while (parameters.hasNext()) {
			SingleVariableDeclaration parameter = parameters.next();
			IVariableBinding parameterType = parameter.resolveBinding();
			SimpleName varName=parameter.getName();
			if (parameterType!= null && varName != null) {
				ITypeBinding typeBind = parameterType.getType();
				if (typeBind != null) {
					LocalEntity le = new LocalEntity();
					le.setStrCodeReprensent(varName.getIdentifier());
					le.setStrTypeOfEntity(typeBind.getQualifiedName());
					setArguments.add(le);
				}

			}
		}
		
		
		 if (node.getBody() == null) {
				//	this.buffer.append(";\n");//$NON-NLS-1$
		 } else {
			 isVisistMethod=true;
			 node.getBody().accept(this);
			 isVisistMethod=false;
		 }
		String strSignature=JavaASTUtil.buildAllSigIngo(node);
		String strInformation= viewAllLocalInformation().toString();
		System.out.println(strSignature+"\n"+strInformation);
		return false;
	}
	
	public boolean visit(VariableDeclarationStatement node) {
		if(isVisistMethod){
			List<VariableDeclarationFragment> listFrags=(List<VariableDeclarationFragment> )node.fragments();
			for(int i=0;i<listFrags.size();i++){
				VariableDeclarationFragment item=listFrags.get(i);
				IVariableBinding varBind = item.resolveBinding();
				SimpleName varName = item.getName();
				if (varBind != null && varName != null) {
					ITypeBinding typeBind = varBind.getType();
					if (typeBind != null) {
						LocalEntity le = new LocalEntity();
						le.setStrCodeReprensent(varName.getIdentifier());
						le.setStrTypeOfEntity(typeBind.getQualifiedName());
						setLocalVariables.add(le);
					}

				}
			}
		}
		return false;
	}
	
	public static void main(String[] args){
		String projectLocation="/Users/hungphan/Documents/workspace/SampleMethodInvocationProject/";
		String jdkPath="/Library/Java/JavaVirtualMachines/jdk1.8.0_141.jdk/Contents/Home/jre/lib/rt.jar";
		String fileLocation="/Users/hungphan/Documents/workspace/SampleMethodInvocationProject/src/examples/CalGetInstance.java";
		MethodEncoderVisitor visitor=new MethodEncoderVisitor();
		visitor.parseProject(projectLocation, jdkPath);
		visitor.parseFile(fileLocation);
	}

}
