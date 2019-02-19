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
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.CreationReference;
import org.eclipse.jdt.core.dom.Dimension;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionMethodReference;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.FileASTRequestor;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.IntersectionType;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.LambdaExpression;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberRef;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import parser.AnnotationType;
import utils.DateUtil;
import utils.JavaASTUtil;
import utils.StringUtil;
import entities.InvocationObject;
import entities.LocalEntity;
import entities.LocalForMethod;

public class MethodEncoderVisitor extends ASTVisitor {

	/**
	 * Internal synonym for {@link AST#JLS2}. Use to alleviate
	 * deprecation warnings.
	 * @deprecated
	 * @since 3.4
	 */
	private static final int JLS2 = AST.JLS2;
	
	/**
	 * Internal synonym for {@link AST#JLS3}. Use to alleviate
	 * deprecation warnings.
	 * @deprecated
	 * @since 3.4
	 */
	private static final int JLS3 = AST.JLS3;

	/**
	 * Internal synonym for {@link AST#JLS4}. Use to alleviate
	 * deprecation warnings.
	 * @deprecated
	 * @since 3.10
	 */
	private static final int JLS4 = AST.JLS4;
	/**
	 * The string buffer into which the serialized representation of the AST is
	 * written.
	 */
	
	protected StringBuffer buffer=new StringBuffer();
	private HashMap<String, String> setSequencesOfMethods, setOfUnResolvedType;
	private LinkedHashSet<LocalEntity> setFields, setArguments,
			setLocalVariables;
	private LinkedHashSet<String> setRequiredAPIsForMI=new LinkedHashSet<String>();;
	private String strSplitCharacter=" ";

	private int indent = 0;
	private boolean isVisitMethod = false;
	private int typeOfTraverse = 0;
	private boolean isParsingType;
	private boolean isVisitInsideMethodDeclaration=false,isSimpleNameMethod=false;
	private boolean isGetInfoForIdentifer=false;
	private StringBuffer unresolvedBuffer;

	ASTParser parser = ASTParser.newParser(AST.JLS4);
	String[] classpath = { "C:\\Program Files\\Java\\jre1.8.0_51\\lib\\rt.jar" };
	HashMap<String, CompilationUnit> mapCU;
	LinkedHashMap<String, LocalForMethod> mapLocalcontextForMethod = new LinkedHashMap<String, LocalForMethod>();
	private boolean isAbstractMethod = false;
	private StringBuilder sbAbstractInformation = new StringBuilder();
	private ArrayList<String> listAbstractTypeQuestionMark=new ArrayList<String>();
	private StringBuilder sbTotalBuilder = new StringBuilder();
	private LocalForMethod currentLocalMethod = null;
	private MethodDeclaration currentMethodDecl = null;
	private int levelOfTraverMD = 0;
	private String fopInvocationObject;


	/**
	 * Internal synonym for {@link TypeDeclarationStatement#getTypeDeclaration()}. Use to alleviate
	 * deprecation warnings.
	 * @deprecated
	 * @since 3.4
	 */
	private static TypeDeclaration getTypeDeclaration(TypeDeclarationStatement node) {
		return node.getTypeDeclaration();
	}

	/**
	 * Internal synonym for {@link MethodDeclaration#thrownExceptions()}. Use to alleviate
	 * deprecation warnings.
	 * @deprecated
	 * @since 3.10
	 */
	private static List thrownExceptions(MethodDeclaration node) {
		return node.thrownExceptions();
	}
	
	public boolean isParsingType() {
		return isParsingType;
	}

	public void setParsingType(boolean isParsingType) {
		this.isParsingType = isParsingType;
	}

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
		setSequencesOfMethods=new LinkedHashMap<String, String>();
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
			typeOfTraverse = 3;
			isParsingType=true;
			CompilationUnit cu = mapCU.get(fileLocation);
			cu.accept(this);
			typeOfTraverse = 0;
			isParsingType=false;
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
//		if (node.getAST().apiLevel() == JLS2) {
//			getTypeDeclaration(node).accept(this);
//		}
//		if (node.getAST().apiLevel() >= JLS3) {
//			node.getDeclaration().accept(this);
//		}

		return true;
	}

	public boolean visit(FieldDeclaration node) {
		// node.fragments();
		return false;
	}
	
	/*
	 * @see ASTVisitor#visit(FieldDeclaration)
	 */
//	public boolean visit(FieldDeclaration node) {
////		if (node.getJavadoc() != null) {
////			node.getJavadoc().accept(this);
////		}
////		printIndent();
////		if (node.getAST().apiLevel() == JLS2) {
////			printModifiers(node.getModifiers());
////		}
////		if (node.getAST().apiLevel() >= JLS3) {
////			printModifiers(node.modifiers());
////		}
////		node.getType().accept(this);
////		this.buffer.append(" ");//$NON-NLS-1$
////		for (Iterator it = node.fragments().iterator(); it.hasNext(); ) {
////			VariableDeclarationFragment f = (VariableDeclarationFragment) it.next();
////			f.accept(this);
////			if (it.hasNext()) {
////				this.buffer.append(", ");//$NON-NLS-1$
////			}
////		}
////		this.buffer.append(";\n");//$NON-NLS-1$
//		return false;
//	}

	public boolean visit(MethodDeclaration node) {
		this.buffer=new StringBuffer();
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

		}else if (typeOfTraverse == 3) {
			String strSignature = JavaASTUtil.buildAllSigIngo(node);
			sbTotalBuilder = new StringBuilder();
			currentMethodDecl = node;
			levelOfTraverMD = 0;
			currentLocalMethod = mapLocalcontextForMethod.get(strSignature);
			isVisitInsideMethodDeclaration=true;
			if (node.getBody() != null) {
				node.getBody().accept(this);
			}
			String methodSig = JavaASTUtil.buildAllSigIngo(node);
			System.out.println("Method " + methodSig);
			System.out.println("Content " + this.buffer.toString());
			setSequencesOfMethods.put(methodSig, this.buffer.toString());
		} else {
			if (node.getBody() != null) {
				node.getBody().accept(this);
			}
		}

		return false;
	}
	
	/*
	 * @see ASTVisitor#visit(MethodDeclaration)
	 */
//	public boolean visit(MethodDeclaration node) {
//		//System.out.println("Node information");
////		if (node.getJavadoc() != null) {
////			node.getJavadoc().accept(this);
////		}
////		printIndent();
////		if (node.getAST().apiLevel() == JLS2) {
////			printModifiers(node.getModifiers());
////		}
////		if (node.getAST().apiLevel() >= JLS3) {
////			printModifiers(node.modifiers());
////			if (!node.typeParameters().isEmpty()) {
////				this.buffer.append("<");//$NON-NLS-1$
////				for (Iterator it = node.typeParameters().iterator(); it.hasNext(); ) {
////					TypeParameter t = (TypeParameter) it.next();
////					t.accept(this);
////					if (it.hasNext()) {
////						this.buffer.append(",");//$NON-NLS-1$
////					}
////				}
////				this.buffer.append(">");//$NON-NLS-1$
////			}
////		}
////		if (!node.isConstructor()) {
////			if (node.getAST().apiLevel() == JLS2) {
////				getReturnType(node).accept(this);
////			} else {
////				if (node.getReturnType2() != null) {
////					node.getReturnType2().accept(this);
////				} else {
////					// methods really ought to have a return type
////					this.buffer.append("void");//$NON-NLS-1$
////				}
////			}
////			this.buffer.append(" ");//$NON-NLS-1$
////		}
////		node.getName().accept(this);
////		this.buffer.append("(");//$NON-NLS-1$
////		if (node.getAST().apiLevel() >= AST.JLS8) {
////			Type receiverType = node.getReceiverType();
////			if (receiverType != null) {
////				receiverType.accept(this);
////				this.buffer.append(' ');
////				SimpleName qualifier = node.getReceiverQualifier();
////				if (qualifier != null) {
////					qualifier.accept(this);
////					this.buffer.append('.');
////				}
////				this.buffer.append("this"); //$NON-NLS-1$
////				if (node.parameters().size() > 0) {
////					this.buffer.append(',');
////				}
////			}
////		}
////		for (Iterator it = node.parameters().iterator(); it.hasNext(); ) {
////			SingleVariableDeclaration v = (SingleVariableDeclaration) it.next();
////			v.accept(this);
////			if (it.hasNext()) {
////				this.buffer.append(",");//$NON-NLS-1$
////			}
////		}
////		this.buffer.append(")");//$NON-NLS-1$
////		int size = node.getExtraDimensions();
////		if (node.getAST().apiLevel() >= AST.JLS8) {
////			List dimensions = node.extraDimensions();
////			for (int i = 0; i < size; i++) {
////				visit((Dimension) dimensions.get(i));
////			}
////		} else {
////			for (int i = 0; i < size; i++) {
////				this.buffer.append("[]"); //$NON-NLS-1$
////			}
////		}
////		if (node.getAST().apiLevel() < AST.JLS8) {
////			if (!thrownExceptions(node).isEmpty()) {
////				this.buffer.append(" throws ");//$NON-NLS-1$
////				for (Iterator it = thrownExceptions(node).iterator(); it.hasNext(); ) {
////					Name n = (Name) it.next();
////					n.accept(this);
////					if (it.hasNext()) {
////						this.buffer.append(", ");//$NON-NLS-1$
////					}
////				}				
////				this.buffer.append(" ");//$NON-NLS-1$
////			} 
////		} else {
////			if (!node.thrownExceptionTypes().isEmpty()) {				
////				this.buffer.append(" throws ");//$NON-NLS-1$
////				for (Iterator it = node.thrownExceptionTypes().iterator(); it.hasNext(); ) {
////					Type n = (Type) it.next();
////					n.accept(this);
////					if (it.hasNext()) {
////						this.buffer.append(", ");//$NON-NLS-1$
////					}
////				}	
////				this.buffer.append(" ");//$NON-NLS-1$				
////			}
////		}
//		
//		
//		
//		if (node.getBody() == null) {
//		//	this.buffer.append(";\n");//$NON-NLS-1$
//		} else {
//			numTotalTypeResolve=0;
//			numAbleTypeResolve=0;
//			String strMethodSig=getMethodSignature(node);
//		//	System.out.println("Method sig: "+strMethodSig);
//			isVisitInsideMethodDeclaration=true;
//			
//			setOfUnResolvedType=new HashMap<String, String>();
//			node.getBody().accept(this);
//			isVisitInsideMethodDeclaration=false;
//			
////			this.buffer.append(strSplitCharacter);
////			this.buffer.append(numAbleTypeResolve+"/"+numTotalTypeResolve);
//			setSequencesOfMethods.put(fp_currentFile+"\t"+strMethodSig+"\t"+numAbleTypeResolve+"/"+numTotalTypeResolve,buffer.toString().trim());			
//			setOfUnResolvedType.put(fp_currentFile+"\t"+strMethodSig+"\t"+numAbleTypeResolve+"/"+numTotalTypeResolve,this.unresolvedBuffer.toString().trim());
//			buffer=new StringBuffer();
//			this.unresolvedBuffer=new StringBuffer();
//		}
//		return false;
//	}

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
	
//	/*
//	 * @see ASTVisitor#visit(MethodInvocation)
//	 */
//	public boolean visit(MethodInvocation node) {
//		this.buffer.append(strSplitCharacter);
//		if (node.getExpression() != null) {
//			node.getExpression().accept(this);
//		//	if(node.getExpression())
//			this.buffer.append(AnnotationType.Variable);
//			this.buffer.append(strSplitCharacter);
//			this.buffer.append(".");//$NON-NLS-1$
//		}
//		if (node.getAST().apiLevel() >= JLS3) {
////			if (!node.typeArguments().isEmpty()) {
////				this.buffer.append("<");//$NON-NLS-1$
////				for (Iterator it = node.typeArguments().iterator(); it.hasNext(); ) {
////					Type t = (Type) it.next();
////					t.accept(this);
////					if (it.hasNext()) {
////						this.buffer.append(",");//$NON-NLS-1$
////					}
////				}
////				this.buffer.append(">");//$NON-NLS-1$
////			}
//		}
//		//this.buffer.append(strSplitCharacter);
//		isSimpleNameMethod=true;
////		handle identifiers
//		node.getName().accept(this);		
//		isSimpleNameMethod=false;
//		this.buffer.append(strSplitCharacter);//$NON-NLS-1$		
//		this.buffer.append("(");//$NON-NLS-1$
//		for (Iterator it = node.arguments().iterator(); it.hasNext(); ) {
//			Expression e = (Expression) it.next();
//			this.buffer.append(strSplitCharacter);//$NON-NLS-1$		
//			e.accept(this);
//			if (it.hasNext()) {
//				this.buffer.append(strSplitCharacter);
//				this.buffer.append(",");
//
//				//$NON-NLS-1$
//			}
//		}
//		this.buffer.append(strSplitCharacter);
//		this.buffer.append(")");//$NON-NLS-1$
//		return false;
//	}
	
	public boolean visit(MethodInvocation node) {
		levelOfTraverMD++;
		if(typeOfTraverse==3){
			if (levelOfTraverMD == 1) {
				sbAbstractInformation = new StringBuilder();
				listAbstractTypeQuestionMark = new ArrayList<String>();
				setRequiredAPIsForMI=new LinkedHashSet<String>();
			}
			
//			this.buffer.append(strSplitCharacter);
//			if (node.getExpression() != null) {
//				node.getExpression().accept(this);
//			//	if(node.getExpression())
//				this.buffer.append(AnnotationType.Variable);
//				this.buffer.append(strSplitCharacter);
//				this.buffer.append(".");//$NON-NLS-1$
//			}
//			
//			//this.buffer.append(strSplitCharacter);
//			isSimpleNameMethod=true;
////			handle identifiers
//			node.getName().accept(this);		
//			isSimpleNameMethod=false;
//			this.buffer.append(strSplitCharacter);//$NON-NLS-1$		
//			this.buffer.append("(");//$NON-NLS-1$
//			for (Iterator it = node.arguments().iterator(); it.hasNext(); ) {
//				Expression e = (Expression) it.next();
//				this.buffer.append(strSplitCharacter);//$NON-NLS-1$		
////				e.accept(this);
//				if (it.hasNext()) {
//					this.buffer.append(strSplitCharacter);
//					this.buffer.append(",");
//
//					//$NON-NLS-1$
//				}
//			}
//			this.buffer.append(strSplitCharacter);
//			this.buffer.append(")");//$NON-NLS-1$
//			this.buffer.append(strSplitCharacter);
//			System.out.println("level "+levelOfTraverMD);
			
			Expression exRetriever = node.getExpression();
			IMethodBinding iMethod=node.resolveMethodBinding();
			String selectedType =  viewSelectedTypeReceiver(iMethod);
//			System.out.println("choose select type "+selectedType);
			String receiverType = exRetriever.resolveTypeBinding().getQualifiedName();
//			System.out.println("set "+setRequiredAPIsForMI.toString());
			
			this.buffer.append(strSplitCharacter);
			

			

			
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
					isGetInfoForIdentifer=false;
					exRetriever.accept(this);
					isGetInfoForIdentifer=true;
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
					isGetInfoForIdentifer=false;
					exRetriever.accept(this);
					isGetInfoForIdentifer=true;
				} else {
//					sbAbstractInformation.append(exRetriever.toString());
					exRetriever.accept(this);
				}
			}
			else {
				exRetriever.accept(this);
			}
			
			this.buffer.append(AnnotationType.Variable);
			this.buffer.append(strSplitCharacter);
			this.buffer.append(".");//$NON-NLS-1$
			
			isSimpleNameMethod=true;
			isGetInfoForIdentifer=false;
	//		handle identifiers
			node.getName().accept(this);
			isGetInfoForIdentifer=true;
			isSimpleNameMethod=false;
			this.buffer.append(strSplitCharacter);//$NON-NLS-1$		
			this.buffer.append("(");//$NON-NLS-1$
			
			
			sbAbstractInformation.append("."+node.getName().getIdentifier()+"(");
			List<Expression> listArgument = node.arguments();
//			ITypeBinding[] arrBindArgs= iMethod.getParameterTypes();
			for (int i = 0; i < listArgument.size(); i++) {
				Expression exParam = listArgument.get(i);
				String selectedParamType = viewSelectedTypeParam(iMethod,i);
				String paramIType = exParam.resolveTypeBinding().getQualifiedName();
				setRequiredAPIsForMI.add(paramIType);
				
				this.buffer.append(strSplitCharacter);//$NON-NLS-1$		
////			e.accept(this);
				
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
						isGetInfoForIdentifer=false;
						exParam.accept(this);
						isGetInfoForIdentifer=true;
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
						isGetInfoForIdentifer=false;
						exParam.accept(this);
						isGetInfoForIdentifer=true;
						
					} else {	
						exParam.accept(this);
					}
				}
				else {
					exParam.accept(this);
				}
				if(i!=listArgument.size()-1){
					this.buffer.append(strSplitCharacter);
					this.buffer.append(",");
					sbAbstractInformation.append(",");
				}
			}
			sbAbstractInformation.append(")");
			
			this.buffer.append(strSplitCharacter);
			this.buffer.append(")");//$NON-NLS-1$
			this.buffer.append(strSplitCharacter);
			
			if(levelOfTraverMD==1){
				if(!isParsingType){
					this.buffer.append(node.getName().getIdentifier()+" ");
				} else{
					InvocationObject io=new InvocationObject();
					String methodInfo=JavaASTUtil.buildAllSigIngo(node);
					io.setStrMethodInfo(methodInfo);
					io.setStrCodeRepresent(sbAbstractInformation.toString());
					io.setListQuestionMarkTypes(listAbstractTypeQuestionMark);
					io.setSetImportedAPIs(setRequiredAPIsForMI);
					String id="E-"+System.nanoTime()+"";
					io.setId(id);
					io.saveToFile(fopInvocationObject);
					this.buffer.append(id+" ");
					
				}
				sbAbstractInformation = new StringBuilder();
				listAbstractTypeQuestionMark = new ArrayList<String>();
				setRequiredAPIsForMI=new LinkedHashSet<String>();
				
			}

			
		}
		
		else if (typeOfTraverse == 2) {
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
//			System.out.println("set "+setRequiredAPIsForMI.toString());
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
					isGetInfoForIdentifer=false;
					exRetriever.accept(this);
					isGetInfoForIdentifer=true;
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
					isGetInfoForIdentifer=false;
					exRetriever.accept(this);
					isGetInfoForIdentifer=true;
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
				if(!isParsingType){
					this.buffer.append(node.getName().getIdentifier()+" ");
				} else{
					InvocationObject io=new InvocationObject();
					String methodInfo=JavaASTUtil.buildAllSigIngo(node);
					io.setStrMethodInfo(methodInfo);
					io.setStrCodeRepresent(sbAbstractInformation.toString());
					io.setListQuestionMarkTypes(listAbstractTypeQuestionMark);
					io.setSetImportedAPIs(setRequiredAPIsForMI);
					String id="E-"+System.nanoTime()+"";
					io.setId(id);
					io.saveToFile(fopInvocationObject);
					this.buffer.append(id+" ");
					
				}
				sbAbstractInformation = new StringBuilder();
				listAbstractTypeQuestionMark = new ArrayList<String>();
				setRequiredAPIsForMI=new LinkedHashSet<String>();
				
			}

		}
		levelOfTraverMD--;
		return false;
	}

	public boolean visit(FieldAccess node) {
		this.buffer.append(strSplitCharacter);		
		node.getExpression().accept(this);
		this.buffer.append(strSplitCharacter);
		this.buffer.append(".");//$NON-NLS-1$
		node.getName().accept(this);
		if (typeOfTraverse==2|| typeOfTraverse==3) {
			if(isGetInfoForIdentifer){
				sbAbstractInformation.append(node.toString());
			}
			
		}
		return false;
	}
	

//	/*
//	 * @see ASTVisitor#visit(FieldAccess)
//	 */
//	public boolean visit(FieldAccess node) {
//		this.buffer.append(strSplitCharacter);
//		
//		node.getExpression().accept(this);
//		this.buffer.append(strSplitCharacter);
//		this.buffer.append(".");//$NON-NLS-1$
//		node.getName().accept(this);
//		return false;
//	}
	
	public boolean visit(SimpleName node) {
		if(isVisitInsideMethodDeclaration){
			
			//IType typeBind=iBind.
			if(isSimpleNameMethod){
				this.buffer.append(node.getIdentifier());
			}else{
				ITypeBinding iTypeBind=node.resolveTypeBinding();
//				numTotalTypeResolve++;
				if(iTypeBind!=null){
					
//					numAbleTypeResolve++;
					if(isParsingType){
						this.buffer.append(iTypeBind.getQualifiedName());
					}else{
						this.buffer.append(iTypeBind.getName());
					}
					
				} else{
					this.unresolvedBuffer.append(node.getIdentifier());
					this.unresolvedBuffer.append(strSplitCharacter);
//					if(!setOfUnResolvedType.containsKey(node.getIdentifier())){
//						setOfUnResolvedType.put(node.getIdentifier(), node.getIdentifier());					
//					}
				}
				
			}
			
		//	node.
		//	this.buffer.append(node.getIdentifier());			
		}
		if (typeOfTraverse==2 || typeOfTraverse==3) {
			if(isGetInfoForIdentifer){
				sbAbstractInformation.append(node.toString());
			}
			
		}
		return false;
	}
	
//	Add TypeResolution code
	/**
	 * Internal synonym for {@link ClassInstanceCreation#getName()}. Use to alleviate
	 * deprecation warnings.
	 * @deprecated
	 * @since 3.4
	 */
	private Name getName(ClassInstanceCreation node) {
		return node.getName();
	}

	/**
	 * Returns the string accumulated in the visit.
	 *
	 * @return the serialized
	 */
	public String getResult() {
		return this.buffer.toString();
	}

	void printIndent() {
		for (int i = 0; i < this.indent; i++)
			this.buffer.append("  "); //$NON-NLS-1$
	}
	/**
	 * @deprecated
	 */
	private void visitComponentType(ArrayType node) {
		node.getComponentType().accept(this);
	}
	
	private void visitAnnotationsList(List annotations) {
		for (Iterator it = annotations.iterator(); it.hasNext(); ) {
			Annotation annotation = (Annotation) it.next();
			annotation.accept(this);
			this.buffer.append(' ');
		}
	}
	
	/**
	 * reference node helper function that is common to all
	 * the difference reference nodes.
	 * 
	 * @param typeArguments list of type arguments 
	 */
	private void visitReferenceTypeArguments(List typeArguments) {
		this.buffer.append("::");//$NON-NLS-1$
//		if (!typeArguments.isEmpty()) {
//			this.buffer.append('<');
//			for (Iterator it = typeArguments.iterator(); it.hasNext(); ) {
//				Type t = (Type) it.next();
//				t.accept(this);
//				if (it.hasNext()) {
//					this.buffer.append(',');
//				}
//			}
//			this.buffer.append('>');
//		}
	}
	
	/*
	 * @see ASTVisitor#visit(AnnotationTypeDeclaration)
	 * @since 3.1
	 */
	public boolean visit(AnnotationTypeDeclaration node) {
//		if (node.getJavadoc() != null) {
//			node.getJavadoc().accept(this);
//		}
//		printIndent();
//		printModifiers(node.modifiers());
//		this.buffer.append("@interface ");//$NON-NLS-1$
//		node.getName().accept(this);
//		this.buffer.append(" {");//$NON-NLS-1$
//		for (Iterator it = node.bodyDeclarations().iterator(); it.hasNext(); ) {
//			BodyDeclaration d = (BodyDeclaration) it.next();
//			d.accept(this);
//		}
//		this.buffer.append("}\n");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(AnnotationTypeMemberDeclaration)
	 * @since 3.1
	 */
	public boolean visit(AnnotationTypeMemberDeclaration node) {
//		if (node.getJavadoc() != null) {
//			node.getJavadoc().accept(this);
//		}
//		printIndent();
//		printModifiers(node.modifiers());
//		node.getType().accept(this);
//		this.buffer.append(" ");//$NON-NLS-1$
//		node.getName().accept(this);
//		this.buffer.append("()");//$NON-NLS-1$
//		if (node.getDefault() != null) {
//			this.buffer.append(" default ");//$NON-NLS-1$
//			node.getDefault().accept(this);
//		}
//		this.buffer.append(";\n");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(AnonymousClassDeclaration)
	 */
	public boolean visit(AnonymousClassDeclaration node) {
		//this.buffer.append("{\n");//$NON-NLS-1$
		this.indent++;
		for (Iterator it = node.bodyDeclarations().iterator(); it.hasNext(); ) {
			BodyDeclaration b = (BodyDeclaration) it.next();
			b.accept(this);
		}
		this.indent--;
		//printIndent();
	//	this.buffer.append("}\n");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(ArrayAccess)
	 */
	public boolean visit(ArrayAccess node) {
		this.buffer.append(strSplitCharacter);
		node.getArray().accept(this);		
		this.buffer.append(strSplitCharacter);
		this.buffer.append("[");
		this.buffer.append(strSplitCharacter);		
		node.getIndex().accept(this);
		this.buffer.append(strSplitCharacter);
		this.buffer.append("]");//$NON-NLS-1$
		this.buffer.append(strSplitCharacter);
		return false;
	}

	/*
	 * @see ASTVisitor#visit(ArrayCreation)
	 */
	public boolean visit(ArrayCreation node) {
		this.buffer.append(" new ");//$NON-NLS-1$
		ArrayType at = node.getType();
		int dims = at.getDimensions();
		Type elementType = at.getElementType();
		elementType.accept(this);
		this.buffer.append(AnnotationType.Type);
		this.buffer.append(strSplitCharacter);
		for (Iterator it = node.dimensions().iterator(); it.hasNext(); ) {
			this.buffer.append(" [ ");//$NON-NLS-1$
			Expression e = (Expression) it.next();
			e.accept(this);
			this.buffer.append(" ] ");//$NON-NLS-1$
			dims--;
		}
		// add empty "[]" for each extra array dimension
		for (int i= 0; i < dims; i++) {
			this.buffer.append(" [] ");//$NON-NLS-1$
		}
		this.buffer.append(strSplitCharacter);//$NON-NLS-1$

		if (node.getInitializer() != null) {
			node.getInitializer().accept(this);
		}
		this.buffer.append(strSplitCharacter);//$NON-NLS-1$

		return false;
	}

	/*
	 * @see ASTVisitor#visit(ArrayInitializer)
	 */
	public boolean visit(ArrayInitializer node) {
		this.buffer.append(strSplitCharacter);//$NON-NLS-1$
		for (Iterator it = node.expressions().iterator(); it.hasNext(); ) {
			Expression e = (Expression) it.next();
			e.accept(this);
			if (it.hasNext()) {
				this.buffer.append(" , ");//$NON-NLS-1$
			}
		}
		this.buffer.append(strSplitCharacter);
		return false;
	}

	/*
	 * @see ASTVisitor#visit(ArrayType)
	 */
	public boolean visit(ArrayType node) {
		this.buffer.append(strSplitCharacter);
		if (node.getAST().apiLevel() < AST.JLS8) {
			visitComponentType(node);
			this.buffer.append("[]");//$NON-NLS-1$
		} else {
			node.getElementType().accept(this);
			List dimensions = node.dimensions();
			int size = dimensions.size();
			for (int i = 0; i < size; i++) {
				Dimension aDimension = (Dimension) dimensions.get(i);
				aDimension.accept(this);
			}
		}
		this.buffer.append(strSplitCharacter);
		return false;
	}

	/*
	 * @see ASTVisitor#visit(AssertStatement)
	 */
	public boolean visit(AssertStatement node) {
		//printIndent();
		this.buffer.append(" assert ");//$NON-NLS-1$
		node.getExpression().accept(this);
		if (node.getMessage() != null) {
			this.buffer.append(" : ");//$NON-NLS-1$
			node.getMessage().accept(this);
			this.buffer.append(strSplitCharacter);
		}
		this.buffer.append(strSplitCharacter);//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(Assignment)
	 */
	public boolean visit(Assignment node) {
		this.buffer.append(" ");
		node.getLeftHandSide().accept(this);
		this.buffer.append(" ");
		this.buffer.append(node.getOperator().toString());
		this.buffer.append(" ");
		node.getRightHandSide().accept(this);
		return false;
	}

	/*
	 * @see ASTVisitor#visit(Block)
	 */
	public boolean visit(Block node) {
		this.buffer.append(" ");//$NON-NLS-1$
		this.indent++;
		for (Iterator it = node.statements().iterator(); it.hasNext(); ) {
			Statement s = (Statement) it.next();
			this.buffer.append(strSplitCharacter);
			
			s.accept(this);
			this.buffer.append(strSplitCharacter);
			
		}
		this.indent--;
		printIndent();
		this.buffer.append(" ");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(BlockComment)
	 * @since 3.0
	 */
	public boolean visit(BlockComment node) {
		printIndent();
		this.buffer.append("/* */");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(BooleanLiteral)
	 */
	public boolean visit(BooleanLiteral node) {
		
		if(isParsingType){
			this.buffer.append("java.lang.Boolean#lit");
			
		}else{
			this.buffer.append("Boolean#lit");
			
		}
		
//		if (node.booleanValue() == true) {
//			this.buffer.append("true");//$NON-NLS-1$
//		} else {
//			this.buffer.append("false");//$NON-NLS-1$
//		}
		return false;
	}

	/*
	 * @see ASTVisitor#visit(BreakStatement)
	 */
	public boolean visit(BreakStatement node) {
		printIndent();
		this.buffer.append("break");//$NON-NLS-1$
		if (node.getLabel() != null) {
			this.buffer.append(" ");//$NON-NLS-1$
			node.getLabel().accept(this);
		}
		this.buffer.append(";\n");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(CastExpression)
	 */
	public boolean visit(CastExpression node) {
		this.buffer.append(" ( ");//$NON-NLS-1$
		node.getType().accept(this);
		this.buffer.append(AnnotationType.Type);//$NON-NLS-1$		
		this.buffer.append(" ) ");//$NON-NLS-1$
		node.getExpression().accept(this);
		this.buffer.append(strSplitCharacter);
		return false;
	}

	/*
	 * @see ASTVisitor#visit(CatchClause)
	 */
	public boolean visit(CatchClause node) {
		this.buffer.append(" catch ");//$NON-NLS-1$
		node.getException().accept(this);
		this.buffer.append(strSplitCharacter);//$NON-NLS-1$
		node.getBody().accept(this);
		this.buffer.append(strSplitCharacter);//$NON-NLS-1$		
		return false;
	}

	/*
	 * @see ASTVisitor#visit(CharacterLiteral)
	 */
	public boolean visit(CharacterLiteral node) {
		if(isParsingType){
			this.buffer.append("java.lang.");
		}
		this.buffer.append("Char");
		
		//this.buffer.append(node.getEscapedValue());
		this.buffer.append(AnnotationType.Literal);
		return false;
	}

	/*
	 * @see ASTVisitor#visit(ClassInstanceCreation)
	 */
	public boolean visit(ClassInstanceCreation node) {
		this.buffer.append(strSplitCharacter);
		if (node.getExpression() != null) {
			node.getExpression().accept(this);
			//this.buffer.append(".");//$NON-NLS-1$
		}
		this.buffer.append(" .new ");//$NON-NLS-1$
		if (node.getAST().apiLevel() == JLS2) {
			getName(node).accept(this);
			this.buffer.append(AnnotationType.Type);
			
		}
		if (node.getAST().apiLevel() >= JLS3) {
//			if (!node.typeArguments().isEmpty()) {
//				this.buffer.append("<");//$NON-NLS-1$
//				for (Iterator it = node.typeArguments().iterator(); it.hasNext(); ) {
//					Type t = (Type) it.next();
//					t.accept(this);
//					if (it.hasNext()) {
//						this.buffer.append(",");//$NON-NLS-1$
//					}
//				}
//				this.buffer.append(">");//$NON-NLS-1$
//			}
			node.getType().accept(this);
			this.buffer.append(AnnotationType.Type);
			
		}
		this.buffer.append(" ( ");//$NON-NLS-1$
		for (Iterator it = node.arguments().iterator(); it.hasNext(); ) {
			Expression e = (Expression) it.next();
			e.accept(this);
			if (it.hasNext()) {
				this.buffer.append(" , ");//$NON-NLS-1$
			}
		}
		this.buffer.append(" ) ");//$NON-NLS-1$
		if (node.getAnonymousClassDeclaration() != null) {
			node.getAnonymousClassDeclaration().accept(this);
		}
		return false;
	}

	/*
	 * @see ASTVisitor#visit(CompilationUnit)
	 */
	public boolean visit(CompilationUnit node) {
		if (node.getPackage() != null) {
			node.getPackage().accept(this);
		}
		for (Iterator it = node.imports().iterator(); it.hasNext(); ) {
			ImportDeclaration d = (ImportDeclaration) it.next();
			d.accept(this);
		}
		for (Iterator it = node.types().iterator(); it.hasNext(); ) {
			AbstractTypeDeclaration d = (AbstractTypeDeclaration) it.next();
			d.accept(this);
		}
		return false;
	}

	/*
	 * @see ASTVisitor#visit(ConditionalExpression)
	 */
	public boolean visit(ConditionalExpression node) {
		this.buffer.append(strSplitCharacter);
		node.getExpression().accept(this);
		this.buffer.append(" ? ");//$NON-NLS-1$
		node.getThenExpression().accept(this);
		this.buffer.append(" : ");//$NON-NLS-1$
		node.getElseExpression().accept(this);
		this.buffer.append(strSplitCharacter);
		return false;
	}

	/*
	 * @see ASTVisitor#visit(ConstructorInvocation)
	 */
	public boolean visit(ConstructorInvocation node) {
		printIndent();
//		if (node.getAST().apiLevel() >= JLS3) {
//			if (!node.typeArguments().isEmpty()) {
//				this.buffer.append("<");//$NON-NLS-1$
//				for (Iterator it = node.typeArguments().iterator(); it.hasNext(); ) {
//					Type t = (Type) it.next();
//					t.accept(this);
//					if (it.hasNext()) {
//						this.buffer.append(",");//$NON-NLS-1$
//					}
//				}
//				this.buffer.append(">");//$NON-NLS-1$
//			}
//		}
		this.buffer.append(" this ( ");//$NON-NLS-1$
		for (Iterator it = node.arguments().iterator(); it.hasNext(); ) {
			Expression e = (Expression) it.next();
			e.accept(this);
			if (it.hasNext()) {
				this.buffer.append(",");//$NON-NLS-1$
			}
		}
		this.buffer.append(" ) ");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(ContinueStatement)
	 */
	public boolean visit(ContinueStatement node) {
		printIndent();
		this.buffer.append(" continue ");//$NON-NLS-1$
		if (node.getLabel() != null) {
			this.buffer.append(" ");//$NON-NLS-1$
			node.getLabel().accept(this);
			this.buffer.append("#lit");
		}
		//this.buffer.append(";\n");//$NON-NLS-1$
		return false;
	}
	
	/*
	 * @see ASTVisitor#visit(CreationReference)
	 * 
	 * @since 3.10
	 */
	public boolean visit(CreationReference node) {
		node.getType().accept(this);
		visitReferenceTypeArguments(node.typeArguments());
		this.buffer.append("new");//$NON-NLS-1$
		return false;
	}

	public boolean visit(Dimension node) {
		List annotations = node.annotations();
		if (annotations.size() > 0)
			this.buffer.append(' ');
		visitAnnotationsList(annotations);
		this.buffer.append("[]"); //$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(DoStatement)
	 */
	public boolean visit(DoStatement node) {
		printIndent();
		this.buffer.append(" do ");//$NON-NLS-1$
		node.getBody().accept(this);
		this.buffer.append(" while ( ");//$NON-NLS-1$
		node.getExpression().accept(this);
		this.buffer.append(" ) ");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(EmptyStatement)
	 */
	public boolean visit(EmptyStatement node) {
		printIndent();
		this.buffer.append(";\n");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(EnhancedForStatement)
	 * @since 3.1
	 */
	public boolean visit(EnhancedForStatement node) {
		printIndent();
		this.buffer.append(" for ( ");//$NON-NLS-1$
		node.getParameter().accept(this);
		this.buffer.append(" : ");//$NON-NLS-1$
		node.getExpression().accept(this);
		this.buffer.append(" ) ");//$NON-NLS-1$
		node.getBody().accept(this);
		this.buffer.append(" ");
		return false;
	}

	/*
	 * @see ASTVisitor#visit(EnumConstantDeclaration)
	 * @since 3.1
	 */
	public boolean visit(EnumConstantDeclaration node) {
//		if (node.getJavadoc() != null) {
//			node.getJavadoc().accept(this);
//		}
//		printIndent();
//		printModifiers(node.modifiers());
//		node.getName().accept(this);
//		if (!node.arguments().isEmpty()) {
//			this.buffer.append("(");//$NON-NLS-1$
//			for (Iterator it = node.arguments().iterator(); it.hasNext(); ) {
//				Expression e = (Expression) it.next();
//				e.accept(this);
//				if (it.hasNext()) {
//					this.buffer.append(",");//$NON-NLS-1$
//				}
//			}
//			this.buffer.append(")");//$NON-NLS-1$
//		}
//		if (node.getAnonymousClassDeclaration() != null) {
//			node.getAnonymousClassDeclaration().accept(this);
//		}
		return false;
	}

	/*
	 * @see ASTVisitor#visit(EnumDeclaration)
	 * @since 3.1
	 */
	public boolean visit(EnumDeclaration node) {
//		if (node.getJavadoc() != null) {
//			node.getJavadoc().accept(this);
//		}
//		printIndent();
//		printModifiers(node.modifiers());
//		this.buffer.append("enum ");//$NON-NLS-1$
//		node.getName().accept(this);
//		this.buffer.append(" ");//$NON-NLS-1$
//		if (!node.superInterfaceTypes().isEmpty()) {
//			this.buffer.append("implements ");//$NON-NLS-1$
//			for (Iterator it = node.superInterfaceTypes().iterator(); it.hasNext(); ) {
//				Type t = (Type) it.next();
//				t.accept(this);
//				if (it.hasNext()) {
//					this.buffer.append(", ");//$NON-NLS-1$
//				}
//			}
//			this.buffer.append(" ");//$NON-NLS-1$
//		}
//		this.buffer.append("{");//$NON-NLS-1$
//		for (Iterator it = node.enumConstants().iterator(); it.hasNext(); ) {
//			EnumConstantDeclaration d = (EnumConstantDeclaration) it.next();
//			d.accept(this);
//			// enum constant declarations do not include punctuation
//			if (it.hasNext()) {
//				// enum constant declarations are separated by commas
//				this.buffer.append(", ");//$NON-NLS-1$
//			}
//		}
//		if (!node.bodyDeclarations().isEmpty()) {
//			this.buffer.append("; ");//$NON-NLS-1$
//			for (Iterator it = node.bodyDeclarations().iterator(); it.hasNext(); ) {
//				BodyDeclaration d = (BodyDeclaration) it.next();
//				d.accept(this);
//				// other body declarations include trailing punctuation
//			}
//		}
//		this.buffer.append("}\n");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(ExpressionMethodReference)
	 * 
	 * @since 3.10
	 */
	public boolean visit(ExpressionMethodReference node) {
		node.getExpression().accept(this);
		visitReferenceTypeArguments(node.typeArguments());
		node.getName().accept(this);
		return false;
	}	

	/*
	 * @see ASTVisitor#visit(ExpressionStatement)
	 */
	public boolean visit(ExpressionStatement node) {
		printIndent();
		this.buffer.append(strSplitCharacter);
		
		node.getExpression().accept(this);
		this.buffer.append(strSplitCharacter);
		
		//this.buffer.append(";\n");//$NON-NLS-1$
		return false;
	}


	

	/*
	 * @see ASTVisitor#visit(ForStatement)
	 */
	public boolean visit(ForStatement node) {
		printIndent();
		this.buffer.append(strSplitCharacter);
		this.buffer.append("for ( ");//$NON-NLS-1$
		for (Iterator it = node.initializers().iterator(); it.hasNext(); ) {
			Expression e = (Expression) it.next();
			e.accept(this);
			if (it.hasNext()) this.buffer.append(" , ");//$NON-NLS-1$
		}
		//this.buffer.append("; ");//$NON-NLS-1$
		this.buffer.append(strSplitCharacter);		
		if (node.getExpression() != null) {
			node.getExpression().accept(this);
		}
		this.buffer.append(strSplitCharacter);
		
		//this.buffer.append("; ");//$NON-NLS-1$
		for (Iterator it = node.updaters().iterator(); it.hasNext(); ) {
			Expression e = (Expression) it.next();
			this.buffer.append(strSplitCharacter);			
			e.accept(this);
			this.buffer.append(strSplitCharacter);
			
			if (it.hasNext()) this.buffer.append(" , ");//$NON-NLS-1$
		}
		this.buffer.append(" ) ");//$NON-NLS-1$
		node.getBody().accept(this);
		this.buffer.append(strSplitCharacter);
		
		return false;
	}

	/*
	 * @see ASTVisitor#visit(IfStatement)
	 */
	public boolean visit(IfStatement node) {
		printIndent();
		this.buffer.append(strSplitCharacter);		
		this.buffer.append(" if ( ");//$NON-NLS-1$
		node.getExpression().accept(this);
		this.buffer.append(" ) ");//$NON-NLS-1$
		node.getThenStatement().accept(this);
		if (node.getElseStatement() != null) {
			this.buffer.append(" else ");//$NON-NLS-1$
			node.getElseStatement().accept(this);
		}
		this.buffer.append(strSplitCharacter);
		
		return false;
	}

	/*
	 * @see ASTVisitor#visit(ImportDeclaration)
	 */
	public boolean visit(ImportDeclaration node) {
//		printIndent();
//		this.buffer.append(" import ");//$NON-NLS-1$
//		if (node.getAST().apiLevel() >= JLS3) {
//			if (node.isStatic()) {
//				this.buffer.append("static ");//$NON-NLS-1$
//			}
//		}
//		node.getName().accept(this);
//		if (node.isOnDemand()) {
//			this.buffer.append(".*");//$NON-NLS-1$
//		}
//		this.buffer.append(";\n");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(InfixExpression)
	 */
	public boolean visit(InfixExpression node) {
		this.buffer.append(strSplitCharacter);
		node.getLeftOperand().accept(this);
		this.buffer.append(' ');  // for cases like x= i - -1; or x= i++ + ++i;
		this.buffer.append(node.getOperator().toString());
		this.buffer.append(' ');
		node.getRightOperand().accept(this);
		final List extendedOperands = node.extendedOperands();
		if (extendedOperands.size() != 0) {
			this.buffer.append(' ');
			for (Iterator it = extendedOperands.iterator(); it.hasNext(); ) {
				this.buffer.append(node.getOperator().toString()).append(' ');
				Expression e = (Expression) it.next();
				e.accept(this);
			}
		}
		this.buffer.append(strSplitCharacter);
		return false;
	}

	/*
	 * @see ASTVisitor#visit(Initializer)
	 */
	public boolean visit(Initializer node) {
//		if (node.getJavadoc() != null) {
//			node.getJavadoc().accept(this);
//		}
//		if (node.getAST().apiLevel() == JLS2) {
//			printModifiers(node.getModifiers());
//		}
//		if (node.getAST().apiLevel() >= JLS3) {
//			printModifiers(node.modifiers());
//		}
//		node.getBody().accept(this);
		return false;
	}

	/*
	 * @see ASTVisitor#visit(InstanceofExpression)
	 */
	public boolean visit(InstanceofExpression node) {
		node.getLeftOperand().accept(this);
		this.buffer.append(" instanceof ");//$NON-NLS-1$
		node.getRightOperand().accept(this);
		return false;
	}

	/*
	 * @see ASTVisitor#visit(IntersectionType)
	 * @since 3.7
	 */
	public boolean visit(IntersectionType node) {
		for (Iterator it = node.types().iterator(); it.hasNext(); ) {
			Type t = (Type) it.next();
			t.accept(this);
			if (it.hasNext()) {
				this.buffer.append(" & "); //$NON-NLS-1$
			}
		}
		return false;
	}

	/*
	 * @see ASTVisitor#visit(Javadoc)
	 */
	public boolean visit(Javadoc node) {
//		printIndent();
//		this.buffer.append("/** ");//$NON-NLS-1$
//		for (Iterator it = node.tags().iterator(); it.hasNext(); ) {
//			ASTNode e = (ASTNode) it.next();
//			e.accept(this);
//		}
//		this.buffer.append("\n */\n");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(LabeledStatement)
	 */
	public boolean visit(LabeledStatement node) {
		printIndent();
		this.buffer.append(strSplitCharacter);
		node.getLabel().accept(this);
		this.buffer.append(AnnotationType.Type);
		this.buffer.append(" : ");//$NON-NLS-1$
		node.getBody().accept(this);
		return false;
	}

	/*
	 * @see ASTVisitor#visit(LambdaExpression)
	 */
	public boolean visit(LambdaExpression node) {
		boolean hasParentheses = node.hasParentheses();
		this.buffer.append(strSplitCharacter);
		if (hasParentheses){
			this.buffer.append('(');
			this.buffer.append(strSplitCharacter);
		}
		for (Iterator it = node.parameters().iterator(); it.hasNext(); ) {
			
			VariableDeclaration v = (VariableDeclaration) it.next();
			v.accept(this);
			this.buffer.append(strSplitCharacter);
			if (it.hasNext()) {
				this.buffer.append(strSplitCharacter);
				this.buffer.append(",");//$NON-NLS-1$
				this.buffer.append(strSplitCharacter);
			}
		}
		if (hasParentheses){
			this.buffer.append(strSplitCharacter);
			this.buffer.append(')');
			this.buffer.append(strSplitCharacter);
		
		}
		
		this.buffer.append(" -> "); //$NON-NLS-1$
		node.getBody().accept(this);
		this.buffer.append(strSplitCharacter);
		return false;
	}

	/*
	 * @see ASTVisitor#visit(LineComment)
	 * @since 3.0
	 */
	public boolean visit(LineComment node) {
		//this.buffer.append("//\n");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(MarkerAnnotation)
	 * @since 3.1
	 */
	public boolean visit(MarkerAnnotation node) {
		//this.buffer.append("@");//$NON-NLS-1$
		node.getTypeName().accept(this);
		return false;
	}

	/*
	 * @see ASTVisitor#visit(MemberRef)
	 * @since 3.0
	 */
	public boolean visit(MemberRef node) {
//		if (node.getQualifier() != null) {
//			node.getQualifier().accept(this);
//		}
//		this.buffer.append("#");//$NON-NLS-1$
//		node.getName().accept(this);
		return false;
	}

	/*
	 * @see ASTVisitor#visit(MemberValuePair)
	 * @since 3.1
	 */
	public boolean visit(MemberValuePair node) {
		this.buffer.append(strSplitCharacter);
		node.getName().accept(this);
		this.buffer.append("=");//$NON-NLS-1$
		this.buffer.append(strSplitCharacter);
		node.getValue().accept(this);
		this.buffer.append(strSplitCharacter);
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
