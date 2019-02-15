package invocations;

import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
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
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.FileASTRequestor;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import utils.DateUtil;
import utils.JavaASTUtil;
import utils.StringUtil;
import entities.InvocationObject;
import entities.LocalEntity;
import entities.LocalForMethod;

public class MethodEncoderVisitor extends ASTVisitor {

	protected StringBuffer buffer;
	private HashMap<String, String> setSequencesOfMethods, setOfUnResolvedType;
	private LinkedHashSet<LocalEntity> setFields, setArguments,
			setLocalVariables;
	private LinkedHashSet<String> setRequiredAPIsForMI;

	private boolean isVisitMethod = false;
	private int typeOfTraverse = 0;

	ASTParser parser = ASTParser.newParser(AST.JLS4);
	String[] classpath = { "C:\\Program Files\\Java\\jre1.8.0_51\\lib\\rt.jar" };
	HashMap<String, CompilationUnit> mapCU;
	LinkedHashMap<String, LocalForMethod> mapLocalcontextForMethod = new LinkedHashMap<String, LocalForMethod>();
	private boolean isAbstractMethod = false;
	private StringBuilder sbAbstractInformation = new StringBuilder();
	private ArrayList<String> listAbstractTypeQuestionMark;
	private StringBuilder sbTotalBuilder = new StringBuilder();
	private LocalForMethod currentLocalMethod = null;
	private MethodDeclaration currentMethodDecl = null;
	private int levelOfTraverMD = 0;
	private String fopInvocationObject;

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

	public void parseProject(String projectLocation,String fopInvocationObject, String jdkPath) {
		this.fopInvocationObject=fopInvocationObject;
		Map<String, String> options = JavaCore.getOptions();
		String[] arrChildJars = utils.FileIO.findAllJarFiles(projectLocation);
		String[] jarPaths = utils.FileIO.combineFilesToArray(jdkPath,
				arrChildJars);
		// String[] jarPaths = { jdkPath };
		// File f = new File(fileLocation);
		String[] filePaths = utils.FileIO.findAllJavaFiles(projectLocation);
		String[] sources = { projectLocation + File.separator };
		// System.out.println(f.getParentFile().getAbsolutePath());
		// System.out.println("jdk :" +this.jdkPath);
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		JavaCore.setComplianceOptions(JavaCore.VERSION_1_8, options);
		parser.setCompilerOptions(options);
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		parser.setEnvironment(jarPaths, sources, null, true);

		// String strCode=FileIO.readStringFromFile(fileLocation );
		// parser.setSource(strCode.toCharArray());
		mapCU = new LinkedHashMap<String, CompilationUnit>();
		setArguments = new LinkedHashSet<>();
		setLocalVariables = new LinkedHashSet<LocalEntity>();
		setFields = new LinkedHashSet<LocalEntity>();
		final MethodEncoderVisitor visitor = this;
		parser.createASTs(filePaths, null, new String[] {},
				new FileASTRequestor() {
					@Override
					public void acceptAST(String sourceFilePath,
							CompilationUnit javaUnit) {
						// javaUnit.accept(visitor);
						mapCU.put(sourceFilePath, javaUnit);
					}
				}, null);

	}

	public void parseFile(String fileLocation) {
		try {
			typeOfTraverse =1;
			mapLocalcontextForMethod.clear();
			CompilationUnit cu = mapCU.get(fileLocation);
			cu.accept(this);
			typeOfTraverse=0;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void parseForAbstractingMethodInvocation(String fileLocation) {
		try {
			typeOfTraverse = 2;
			CompilationUnit cu = mapCU.get(fileLocation);
			cu.accept(this);
			typeOfTraverse = 0;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public StringBuilder viewAllLocalInformation() {
		StringBuilder sb = new StringBuilder();
		sb.append("setLocalVariable " + setLocalVariables.size() + ": ");
		for (LocalEntity ent : setLocalVariables) {
			sb.append(ent.getStrCodeReprensent() + " - "
					+ ent.getStrTypeOfEntity() + ",");
		}
		sb.append("\n");
		sb.append("setArgument " + setArguments.size() + ": ");
		for (LocalEntity ent : setArguments) {
			sb.append(ent.getStrCodeReprensent() + " - "
					+ ent.getStrTypeOfEntity() + ",");
		}
		sb.append("\n");
		sb.append("setField " + setFields.size() + ": ");
		for (LocalEntity ent : setFields) {
			sb.append(ent.getStrCodeReprensent() + " - "
					+ ent.getStrTypeOfEntity() + ",");
		}
		sb.append("\n");

		return sb;
	}

	public boolean visit(TypeDeclaration node) {
		if (typeOfTraverse == 1) {
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
		}

		return true;
	}

	public boolean visit(FieldDeclaration node) {
		// node.fragments();
		return false;
	}

	public boolean visit(MethodDeclaration node) {
		if (typeOfTraverse == 1) {
			setArguments.clear();
			setLocalVariables.clear();
			Iterator<SingleVariableDeclaration> parameters = node.parameters()
					.iterator();
			while (parameters.hasNext()) {
				SingleVariableDeclaration parameter = parameters.next();
				IVariableBinding parameterType = parameter.resolveBinding();
				SimpleName varName = parameter.getName();
				if (parameterType != null && varName != null) {
					ITypeBinding typeBind = parameterType.getType();
					if (typeBind != null) {
						LocalEntity le = new LocalEntity();
						le.setStrCodeReprensent(varName.getIdentifier());
						le.setStrTypeOfEntity(typeBind.getQualifiedName());
						setArguments.add(le);
					}
				}
			}

			if (node.getBody() != null) {
				isVisitMethod = true;
				node.getBody().accept(this);
				isVisitMethod = false;
			}
			String strSignature = JavaASTUtil.buildAllSigIngo(node);
			String strInformation = viewAllLocalInformation().toString();
			System.out.println(strSignature + "\n" + strInformation);
			LocalForMethod lfm = new LocalForMethod();
			lfm.setMethod(node);
			lfm.setSetArguments((LinkedHashSet<LocalEntity>) setArguments
					.clone());
			lfm.setSetLocalVariables((LinkedHashSet<LocalEntity>) setLocalVariables
					.clone());
			lfm.setSetFields((LinkedHashSet<LocalEntity>) setFields.clone());
//			currentLocalMethod = lfm;
			mapLocalcontextForMethod.put(strSignature, lfm);
		} else if (typeOfTraverse == 2) {
			String strSignature = JavaASTUtil.buildAllSigIngo(node);
			sbTotalBuilder = new StringBuilder();
			currentMethodDecl = node;
			levelOfTraverMD = 0;
			currentLocalMethod = mapLocalcontextForMethod.get(strSignature);
			if (node.getBody() != null) {
				node.getBody().accept(this);
			}
			String methodSig = JavaASTUtil.buildAllSigIngo(node);
			System.out.println("Method " + methodSig);
			System.out.println("Content " + sbTotalBuilder.toString());

		} else {
			if (node.getBody() != null) {
				node.getBody().accept(this);
			}
		}

		return false;
	}

	public boolean visit(VariableDeclarationStatement node) {
		if (isVisitMethod) {
			List<VariableDeclarationFragment> listFrags = (List<VariableDeclarationFragment>) node
					.fragments();
			for (int i = 0; i < listFrags.size(); i++) {
				VariableDeclarationFragment item = listFrags.get(i);
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


	public String viewSelectedTypeReceiver(IMethodBinding iMethod){
		
		String strType=iMethod!=null ?(iMethod.getDeclaringClass() != null ? iMethod.getDeclaringClass()
				.getQualifiedName()
				: ""):":";
		return strType;
	}
	
	public String viewSelectedTypeParam(IMethodBinding iMethod,int i){
		if(iMethod==null){
			return "";
		}
		ITypeBinding[] arrBindArgs= iMethod.getParameterTypes();
		if(arrBindArgs==null){
			return "";
		}
		String strType=arrBindArgs[i] != null ? arrBindArgs[i].getQualifiedName() : "";
		return strType;
	}
	
	public boolean visit(MethodInvocation node) {
		levelOfTraverMD++;
		if (typeOfTraverse == 2) {
			if (levelOfTraverMD == 1) {
				sbAbstractInformation = new StringBuilder();
				listAbstractTypeQuestionMark = new ArrayList<String>();
				setRequiredAPIsForMI=new LinkedHashSet<String>();
			}
			Expression exRetriever = node.getExpression();
			IMethodBinding iMethod=node.resolveMethodBinding();
			String selectedType =  viewSelectedTypeReceiver(iMethod);
//			System.out.println("choose select type "+selectedType);
			String receiverType = exRetriever.resolveTypeBinding().getQualifiedName();
			setRequiredAPIsForMI.add(receiverType);
			if (exRetriever instanceof SimpleName) {
				SimpleName nameRetriever = (SimpleName) exRetriever;
				String strVariable = nameRetriever.getIdentifier();
				boolean isLocalEntity = false;
				
				for (LocalEntity ent : currentLocalMethod.getSetArguments()) {
					if (ent.getStrCodeReprensent().equals(strVariable)) {
						isLocalEntity = true;
						break;
					}
				}

				for (LocalEntity ent : currentLocalMethod
						.getSetLocalVariables()) {
					if (ent.getStrCodeReprensent().equals(strVariable)) {
						isLocalEntity = true;
						break;
					}
				}

				for (LocalEntity ent : currentLocalMethod.getSetFields()) {
					if (ent.checkCodeInLocalRepresent(strVariable)) {
						isLocalEntity = true;
						break;
					}
				}
				if (isLocalEntity) {
					sbAbstractInformation.append("?");
					listAbstractTypeQuestionMark.add(selectedType);
				} else {
//					sbAbstractInformation.append(exRetriever.toString());
					exRetriever.accept(this);
				}
			} 
			else if(exRetriever instanceof FieldAccess){
				FieldAccess nameRetriever = (FieldAccess) exRetriever;
				String strVariable = nameRetriever.toString();
				boolean isLocalEntity = false;

				for (LocalEntity ent : currentLocalMethod.getSetFields()) {
					if (ent.checkCodeInLocalRepresent(strVariable)) {
						isLocalEntity = true;
						break;
					}
				}
				if (isLocalEntity) {
					sbAbstractInformation.append("?");
					listAbstractTypeQuestionMark.add(selectedType);
				} else {
//					sbAbstractInformation.append(exRetriever.toString());
					exRetriever.accept(this);
				}
			}
			else {
				exRetriever.accept(this);
			}
			sbAbstractInformation.append("."+node.getName().getIdentifier()+"(");
			List<Expression> listArgument = node.arguments();
//			ITypeBinding[] arrBindArgs= iMethod.getParameterTypes();
			for (int i = 0; i < listArgument.size(); i++) {
				Expression exParam = listArgument.get(i);
				String selectedParamType = viewSelectedTypeParam(iMethod,i);
				String paramIType = exParam.resolveTypeBinding().getQualifiedName();
				setRequiredAPIsForMI.add(paramIType);
				if (exParam instanceof SimpleName) {
					SimpleName nameParam = (SimpleName) exParam;
					String strVariable = nameParam.getIdentifier();
					boolean isLocalEntity = false;
					
					for (LocalEntity ent : currentLocalMethod.getSetArguments()) {
						if (ent.getStrCodeReprensent().equals(strVariable)) {
							isLocalEntity = true;
							break;
						}
					}

					for (LocalEntity ent : currentLocalMethod
							.getSetLocalVariables()) {
						if (ent.getStrCodeReprensent().equals(strVariable)) {
							isLocalEntity = true;
							break;
						}
					}

					for (LocalEntity ent : currentLocalMethod.getSetFields()) {
						if (ent.checkCodeInLocalRepresent(strVariable)) {
							isLocalEntity = true;
							break;
						}
					}
					if (isLocalEntity) {
						sbAbstractInformation.append("?");
						listAbstractTypeQuestionMark.add(selectedParamType);
					} else {
//						sbAbstractInformation.append(nameParam.toString());
						exParam.accept(this);
					}
				} 
				else if(exParam instanceof FieldAccess){
					FieldAccess nameParam = (FieldAccess) exParam;
					String strVariable = nameParam.toString();
					boolean isLocalEntity = false;

//					System.out.println("param "+strVariable+" "+selectedType);
					for (LocalEntity ent : currentLocalMethod.getSetFields()) {
						if (ent.checkCodeInLocalRepresent(strVariable)) {
							isLocalEntity = true;
							break;
						}
					}
					if (isLocalEntity) {
						sbAbstractInformation.append("?");
						listAbstractTypeQuestionMark.add(selectedParamType);
					} else {	
						exParam.accept(this);
					}
				}
				else {
					exParam.accept(this);
				}
				if(i!=listArgument.size()-1){
					sbAbstractInformation.append(",");
				}
			}
			sbAbstractInformation.append(")");
			if(levelOfTraverMD==1){
				
//				System.out.println("variable: "+sbAbstractTypeQuestionMark.toString());
				InvocationObject io=new InvocationObject();
				String methodInfo=JavaASTUtil.buildAllSigIngo(node);
				io.setStrMethodInfo(methodInfo);
				io.setStrCodeRepresent(sbAbstractInformation.toString());
				io.setListQuestionMarkTypes(listAbstractTypeQuestionMark);
				io.setSetImportedAPIs(setRequiredAPIsForMI);
				String id="E-"+System.nanoTime()+"";
				io.setId(id);
				io.saveToFile(fopInvocationObject);
				sbTotalBuilder.append(id+" ");
				sbAbstractInformation = new StringBuilder();
				listAbstractTypeQuestionMark = new ArrayList<String>();
				setRequiredAPIsForMI=new LinkedHashSet<String>();
			}

		}
		levelOfTraverMD--;
		return false;
	}

	public boolean visit(FieldAccess node) {
		if (typeOfTraverse==2) {
			sbAbstractInformation.append(node.toString());
//			String strVariable=node.toString();
//			boolean isLocalEntity=false;
//			
//			for (LocalEntity ent : currentLocalMethod.getSetFields()) {
//				if (ent.checkCodeInLocalRepresent(strVariable)) {
//					isLocalEntity = true;
//					break;
//				}
//			}
//			if (isLocalEntity) {
//				sbAbstractInformation.append("?");
//				sbAbstractTypeQuestionMark.append(selectedType + "\n");
//			} else {
//				sbAbstractInformation.append(nameParam.toString());
//				exParam.accept(this);
//			}
//			sbAbstractInformation.append(node.toString());
		}
		return false;
	}
	public boolean visit(SimpleName node) {
		if (typeOfTraverse==2) {
			sbAbstractInformation.append(node.toString());
		}
		return false;
	}

	public static void main(String[] args) {
		String projectLocation = "/Users/hungphan/Documents/workspace/SampleMethodInvocationProject/";
		String outputIdLocation = "/Users/hungphan/Documents/workspace/OutputMethodId/";
		String jdkPath = "/Library/Java/JavaVirtualMachines/jdk1.8.0_141.jdk/Contents/Home/jre/lib/rt.jar";
		String fileLocation = "/Users/hungphan/Documents/workspace/SampleMethodInvocationProject/src/examples/CalGetInstance.java";
		MethodEncoderVisitor visitor = new MethodEncoderVisitor();
		visitor.parseProject(projectLocation,outputIdLocation, jdkPath);
		visitor.parseFile(fileLocation);
		visitor.parseForAbstractingMethodInvocation(fileLocation);
	}

}
