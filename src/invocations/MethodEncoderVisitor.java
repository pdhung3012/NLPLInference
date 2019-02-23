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
import org.eclipse.jdt.core.dom.*;

import parser.AnnotationType;
import utils.DateUtil;
import utils.JavaASTUtil;
import utils.StringUtil;
import entities.InvocationObject;
import entities.LocalEntity;
import entities.LocalForMethod;

public class MethodEncoderVisitor extends ASTVisitor {

	// sequence generator properties
	private static final String SEPARATOR = "#";
	private String className, superClassName;
	private int numOfExpressions = 0, numOfResolvedExpressions = 0;
	private StringBuilder fullTokens = new StringBuilder(),
			partialTokens = new StringBuilder();
	private String fullSequence = null, partialSequence = null;
	private String[] fullSequenceTokens, partialSequenceTokens;
	private LinkedHashMap<String,String> mapIdenAndID,mapIDAndIden;
	private LinkedHashMap<String,Integer> mapIDAppear;
	private InvocationAbstractorVisitor iaVisitor;
	
	
	
	public LinkedHashMap<String, Integer> getMapIDAppear() {
		return mapIDAppear;
	}

	public void setMapIDAppear(LinkedHashMap<String, Integer> mapIDAppear) {
		this.mapIDAppear = mapIDAppear;
	}

	// end
	/**
	 * Internal synonym for {@link AST#JLS2}. Use to alleviate deprecation
	 * warnings.
	 * 
	 * @deprecated
	 * @since 3.4
	 */
	private static final int JLS2 = AST.JLS2;

	/**
	 * Internal synonym for {@link AST#JLS3}. Use to alleviate deprecation
	 * warnings.
	 * 
	 * @deprecated
	 * @since 3.4
	 */
	private static final int JLS3 = AST.JLS3;

	/**
	 * Internal synonym for {@link AST#JLS4}. Use to alleviate deprecation
	 * warnings.
	 * 
	 * @deprecated
	 * @since 3.10
	 */
	private static final int JLS4 = AST.JLS4;
	/**
	 * The string buffer into which the serialized representation of the AST is
	 * written.
	 */

	protected StringBuffer buffer = new StringBuffer();
	private HashMap<String, String> setSequencesOfMethods, setOfUnResolvedType;
	private LinkedHashSet<LocalEntity> setFields, setArguments,
			setLocalVariables;
//	private LinkedHashSet<String> setRequiredAPIsForMI = new LinkedHashSet<String>();;
	private String strSplitCharacter = " ";

	private int indent = 0;
	private boolean isVisitMethod = false;
	private int typeOfTraverse = 0;
	private boolean isParsingType;
	private boolean isVisitInsideMethodDeclaration = false,
			isSimpleNameMethod = false;
	private StringBuffer unresolvedBuffer;

	ASTParser parser = ASTParser.newParser(AST.JLS4);
	String[] classpath = { "/Library/Java/JavaVirtualMachines/jdk1.8.0_141.jdk/Contents/Home/jre/lib/rt.jar" };
	HashMap<String, CompilationUnit> mapCU;
	LinkedHashMap<String, LocalForMethod> mapLocalcontextForMethod = new LinkedHashMap<String, LocalForMethod>();
	private boolean isAbstractMethod = false;
//	private StringBuilder sbAbstractInformation = new StringBuilder();
//	private ArrayList<String> listAbstractTypeQuestionMark = new ArrayList<String>();
	private StringBuilder sbTotalBuilder = new StringBuilder();
	private LocalForMethod currentLocalMethod = null;
	private MethodDeclaration currentMethodDecl = null;
	private int levelOfTraverMD = 0;
	private String fopInvocationObject;
	private String hashIdenPath;
	
	
	
	
	public String getHashIdenPath() {
		return hashIdenPath;
	}

	public void setHashIdenPath(String hashIdenPath) {
		this.hashIdenPath = hashIdenPath;
	}

	/**
	 * Internal synonym for
	 * {@link TypeDeclarationStatement#getTypeDeclaration()}. Use to alleviate
	 * deprecation warnings.
	 * 
	 * @deprecated
	 * @since 3.4
	 */
	private static TypeDeclaration getTypeDeclaration(
			TypeDeclarationStatement node) {
		return node.getTypeDeclaration();
	}

	/**
	 * Internal synonym for {@link MethodDeclaration#thrownExceptions()}. Use to
	 * alleviate deprecation warnings.
	 * 
	 * @deprecated
	 * @since 3.10
	 */
	private static List thrownExceptions(MethodDeclaration node) {
		return node.thrownExceptions();
	}

	
	
	public LinkedHashMap<String, String> getMapIdenAndID() {
		return mapIdenAndID;
	}

	public void setMapIdenAndID(LinkedHashMap<String, String> mapIdenAndID) {
		this.mapIdenAndID = mapIdenAndID;
	}

	public LinkedHashMap<String, String> getMapIDAndIden() {
		return mapIDAndIden;
	}

	public void setMapIDAndIden(LinkedHashMap<String, String> mapIDAndIden) {
		this.mapIDAndIden = mapIDAndIden;
	}

	public LinkedHashSet<LocalEntity> getSetFields() {
		return setFields;
	}

	public void setSetFields(LinkedHashSet<LocalEntity> setFields) {
		this.setFields = setFields;
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

	public MethodEncoderVisitor(String className, String superClassName) {
		super(false);
		this.className = className;
		this.superClassName = superClassName;
	}

	public void parseProject(String projectLocation,
			String fopInvocationObject, String jdkPath) {
		this.fopInvocationObject = fopInvocationObject;
		setSequencesOfMethods = new LinkedHashMap<String, String>();
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
			typeOfTraverse = 1;
			mapLocalcontextForMethod.clear();
			CompilationUnit cu = mapCU.get(fileLocation);
			cu.accept(this);
			typeOfTraverse = 0;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void parseForAbstractingMethodInvocation(String fileLocation) {
		try {
			typeOfTraverse = 3;
			isParsingType = true;
			CompilationUnit cu = mapCU.get(fileLocation);
			cu.accept(this);
			typeOfTraverse = 0;
			isParsingType = false;
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
	
	

	public String getFopInvocationObject() {
		return fopInvocationObject;
	}

	public void setFopInvocationObject(String fopInvocationObject) {
		this.fopInvocationObject = fopInvocationObject;
	}

	public String[] getFullSequenceTokens() {
		if (fullSequenceTokens == null)
			buildFullSequence();
		return fullSequenceTokens;
	}

	public String[] getPartialSequenceTokens() {
		if (partialSequenceTokens == null)
			buildPartialSequence();
		return partialSequenceTokens;
	}

	public String getFullSequence() {
		if (fullSequence == null)
			buildFullSequence();
		return fullSequence;
	}

	public String getPartialSequence() {
		if (partialSequence == null)
			buildPartialSequence();
		return partialSequence;
	}

	private void buildFullSequence() {
		ArrayList<String> parts = buildSequence(fullTokens);
		this.fullSequence = parts.get(0);
		this.fullSequenceTokens = new String[parts.size() - 1];
		for (int i = 1; i < parts.size(); i++)
			this.fullSequenceTokens[i - 1] = parts.get(i);
	}

	private void buildPartialSequence() {
		ArrayList<String> parts = buildSequence(partialTokens);
		this.partialSequence = parts.get(0);
		this.partialSequenceTokens = new String[parts.size() - 1];
		for (int i = 1; i < parts.size(); i++)
			this.partialSequenceTokens[i - 1] = parts.get(i);
	}

	private ArrayList<String> buildSequence(StringBuilder tokens) {
		tokens.append(" ");
		ArrayList<String> l = new ArrayList<>();
		StringBuilder sequence = new StringBuilder(), token = null;
		for (int i = 0; i < tokens.length(); i++) {
			char ch = tokens.charAt(i);
			if (ch == ' ') {
				if (token != null) {
					String t = token.toString();
					l.add(t);
					sequence.append(t + " ");
					token = null;
				}
			} else {
				if (token == null)
					token = new StringBuilder();
				token.append(ch);
			}
		}
		l.add(0, sequence.toString());
		return l;
	}

	public static LinkedHashSet<LocalEntity> setInfoOfFieldDeclaration(
			TypeDeclaration node) {
		LinkedHashSet<LocalEntity> setFields = new LinkedHashSet<LocalEntity>();
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
		return setFields;
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
		// if (node.getAST().apiLevel() == JLS2) {
		// getTypeDeclaration(node).accept(this);
		// }
		// if (node.getAST().apiLevel() >= JLS3) {
		// node.getDeclaration().accept(this);
		// }

		return false;
	}

//	public boolean visit(FieldDeclaration node) {
//		// node.fragments();
//		return false;
//	}

	// @Override
	// public boolean visit(MethodDeclaration node) {
	// if (node.getBody() != null && !node.getBody().statements().isEmpty())
	// node.getBody().accept(this);
	// return false;
	// }

	private String currentMethodDeclaration="";
	private String currentClassDeclaration="";
	public boolean visit(MethodDeclaration node) {
		if(setArguments==null){
			setArguments=new LinkedHashSet<LocalEntity>();
		}
		if(setLocalVariables==null){
			setLocalVariables=new LinkedHashSet<LocalEntity>();
		}
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
		String strSignature = JavaASTUtil.buildAllSigIngo(node);
		String strInformation = viewAllLocalInformation().toString();
//		System.out.println(strSignature + "\n" + strInformation);
		LocalForMethod lfm = new LocalForMethod();
		lfm.setMethod(node);
		lfm.setSetArguments((LinkedHashSet<LocalEntity>) setArguments.clone());
		lfm.setSetLocalVariables((LinkedHashSet<LocalEntity>) setLocalVariables
				.clone());
		lfm.setSetFields((LinkedHashSet<LocalEntity>) setFields.clone());
		// currentLcalMethod = lfm;
		mapLocalcontextForMethod.put(strSignature, lfm);

		sbTotalBuilder = new StringBuilder();
		currentMethodDecl = node;
		levelOfTraverMD = 0;
		currentLocalMethod = mapLocalcontextForMethod.get(strSignature);
		isVisitInsideMethodDeclaration = true;
		IMethodBinding bindM=node.resolveBinding();
		if(bindM!=null){
			currentMethodDeclaration=bindM.getKey();
			ITypeBinding bindT=bindM.getDeclaringClass();
			if(bindT!=null){
				currentClassDeclaration=bindT.getQualifiedName();
			}
		}
		if(iaVisitor==null){
			iaVisitor=new InvocationAbstractorVisitor();
		}
		iaVisitor.setCurrentClassDeclaration(currentClassDeclaration);
		iaVisitor.setCurrentMethodDeclaration(currentMethodDeclaration);
		
		if (node.getBody() != null) {
			node.getBody().accept(this);
		}
//		System.out.println(this.partialTokens.toString());
//		System.out.println(this.fullTokens.toString());
//		String methodSig = JavaASTUtil.buildAllSigIngo(node);
//		System.out.println("Method " + methodSig);
		// System.out.println("Content " + this.buffer.toString());
		// setSequencesOfMethods.put(methodSig, this.buffer.toString());

		return false;
	}

	// @Override
	// public boolean visit(VariableDeclarationStatement node) {
	// ITypeBinding tb = node.getType().resolveBinding();
	// if (tb != null && tb.getTypeDeclaration().isLocal())
	// return false;
	// String utype = getUnresolvedType(node.getType()), rtype =
	// getResolvedType(node.getType());
	// this.partialTokens.append(" " + utype + " ");
	// this.fullTokens.append(" " + rtype + " ");
	// for (int i = 0; i < node.fragments().size(); i++)
	// ((ASTNode) node.fragments().get(i)).accept(this);
	// return false;
	// }

//	public boolean visit(VariableDeclarationStatement node) {
//		if (isVisitMethod) {
//			List<VariableDeclarationFragment> listFrags = (List<VariableDeclarationFragment>) node
//					.fragments();
//			for (int i = 0; i < listFrags.size(); i++) {
//				VariableDeclarationFragment item = listFrags.get(i);
//				IVariableBinding varBind = item.resolveBinding();
//				SimpleName varName = item.getName();
//				if (varBind != null && varName != null) {
//					ITypeBinding typeBind = varBind.getType();
//					if (typeBind != null) {
//						LocalEntity le = new LocalEntity();
//						le.setStrCodeReprensent(varName.getIdentifier());
//						le.setStrTypeOfEntity(typeBind.getQualifiedName());
//						setLocalVariables.add(le);
//					}
//
//				}
//			}
//		}
//		return false;
//	}

	public String viewSelectedTypeReceiver(IMethodBinding iMethod) {

		String strType = iMethod != null ? (iMethod.getDeclaringClass() != null ? iMethod
				.getDeclaringClass().getQualifiedName() : "")
				: ":";
		return strType;
	}

	public String viewSelectedTypeParam(IMethodBinding iMethod, int i) {
		if (iMethod == null) {
			return "";
		}
		ITypeBinding[] arrBindArgs = iMethod.getParameterTypes();
		if (arrBindArgs == null) {
			return "";
		}
		if(arrBindArgs.length<i+1){
			return "";
		}
		String strType = arrBindArgs[i] != null ? arrBindArgs[i]
				.getQualifiedName() : "";
		return strType;
	}

	public int getNumOfExpressions() {
		return numOfExpressions;
	}

	public int getNumOfResolvedExpressions() {
		return numOfResolvedExpressions;
	}

	private Type getType(VariableDeclarationFragment node) {
		ASTNode p = node.getParent();
		if (p instanceof VariableDeclarationExpression)
			return ((VariableDeclarationExpression) p).getType();
		if (p instanceof VariableDeclarationStatement)
			return ((VariableDeclarationStatement) p).getType();
		return null;
	}

	private String getSignature(IMethodBinding method) {
		StringBuilder sb = new StringBuilder();
		sb.append(method.getDeclaringClass().getTypeDeclaration()
				.getQualifiedName());
		sb.append("." + method.getName());
		sb.append("(");
		sb.append(SEPARATOR);
		for (ITypeBinding tb : method.getParameterTypes())
			sb.append(tb.getTypeDeclaration().getName() + "#");
		sb.append(")");
		return sb.toString();
	}

	public static String getUnresolvedType(Type type) {
		if (type.isArrayType()) {
			ArrayType t = (ArrayType) type;
			return getUnresolvedType(t.getElementType())
					+ getDimensions(t.getDimensions());
		} else if (type.isIntersectionType()) {
			IntersectionType it = (IntersectionType) type;
			@SuppressWarnings("unchecked")
			ArrayList<Type> types = new ArrayList<>(it.types());
			String s = getUnresolvedType(types.get(0));
			for (int i = 1; i < types.size(); i++)
				s += " & " + getUnresolvedType(types.get(i));
			return s;
		} else if (type.isParameterizedType()) {
			ParameterizedType t = (ParameterizedType) type;
			return getUnresolvedType(t.getType());
		} else if (type.isUnionType()) {
			UnionType it = (UnionType) type;
			@SuppressWarnings("unchecked")
			ArrayList<Type> types = new ArrayList<>(it.types());
			String s = getUnresolvedType(types.get(0));
			for (int i = 1; i < types.size(); i++)
				s += " | " + getUnresolvedType(types.get(i));
			return s;
		} else if (type.isNameQualifiedType()) {
			NameQualifiedType qt = (NameQualifiedType) type;
			return qt.getQualifier().getFullyQualifiedName() + "."
					+ qt.getName().getIdentifier();
		} else if (type.isPrimitiveType()) {
			return type.toString();
		} else if (type.isQualifiedType()) {
			QualifiedType qt = (QualifiedType) type;
			return getUnresolvedType(qt.getQualifier()) + "."
					+ qt.getName().getIdentifier();
		} else if (type.isSimpleType()) {
			return type.toString();
		} else if (type.isWildcardType()) {
			WildcardType wt = (WildcardType) type;
			String s = "?";
			if (wt.getBound() != null) {
				if (wt.isUpperBound())
					s += "extends ";
				else
					s += "super ";
				s += getUnresolvedType(wt.getBound());
			}
			return s;
		}

		return null;
	}

	private static String getDimensions(int dimensions) {
		String s = "";
		for (int i = 0; i < dimensions; i++)
			s += "[]";
		return s;
	}

	static String getResolvedType(Type type) {
		ITypeBinding tb = type.resolveBinding();
		if (tb == null || tb.isRecovered())
			return getUnresolvedType(type);
		tb = tb.getTypeDeclaration();
		if (tb.isLocal() || tb.getQualifiedName().isEmpty())
			return getUnresolvedType(type);
		if (type.isArrayType()) {
			ArrayType t = (ArrayType) type;
			return getResolvedType(t.getElementType())
					+ getDimensions(t.getDimensions());
		} else if (type.isIntersectionType()) {
			IntersectionType it = (IntersectionType) type;
			@SuppressWarnings("unchecked")
			ArrayList<Type> types = new ArrayList<>(it.types());
			String s = getResolvedType(types.get(0));
			for (int i = 1; i < types.size(); i++)
				s += " & " + getResolvedType(types.get(i));
			return s;
		} else if (type.isParameterizedType()) {
			ParameterizedType t = (ParameterizedType) type;
			return getResolvedType(t.getType());
		} else if (type.isUnionType()) {
			UnionType it = (UnionType) type;
			@SuppressWarnings("unchecked")
			ArrayList<Type> types = new ArrayList<>(it.types());
			String s = getResolvedType(types.get(0));
			for (int i = 1; i < types.size(); i++)
				s += " | " + getResolvedType(types.get(i));
			return s;
		} else if (type.isNameQualifiedType()) {
			return tb.getQualifiedName();
		} else if (type.isPrimitiveType()) {
			return type.toString();
		} else if (type.isQualifiedType()) {
			return tb.getQualifiedName();
		} else if (type.isSimpleType()) {
			return tb.getQualifiedName();
		} else if (type.isWildcardType()) {
			WildcardType wt = (WildcardType) type;
			String s = "?";
			if (wt.getBound() != null) {
				if (wt.isUpperBound())
					s += "extends ";
				else
					s += "super ";
				s += getResolvedType(wt.getBound());
			}
			return s;
		}

		return null;
	}

	@Override
	public void preVisit(ASTNode node) {
		if (node instanceof Expression) {
			numOfExpressions++;
			Expression e = (Expression) node;
			if (e.resolveTypeBinding() != null
					&& !e.resolveTypeBinding().isRecovered())
				numOfResolvedExpressions++;
		} else if (node instanceof Statement) {
			if (node instanceof ConstructorInvocation) {
				numOfExpressions++;
				if (((ConstructorInvocation) node).resolveConstructorBinding() != null
						&& !((ConstructorInvocation) node)
								.resolveConstructorBinding().isRecovered())
					numOfResolvedExpressions++;
			} else if (node instanceof SuperConstructorInvocation) {
				numOfExpressions++;
				if (((SuperConstructorInvocation) node)
						.resolveConstructorBinding() != null
						&& !((SuperConstructorInvocation) node)
								.resolveConstructorBinding().isRecovered())
					numOfResolvedExpressions++;
			}
		} else if (node instanceof Type) {
			numOfExpressions++;
			Type t = (Type) node;
			if (t.resolveBinding() != null && !t.resolveBinding().isRecovered())
				numOfResolvedExpressions++;
		}
	}

	// @Override
	// public boolean visit(MethodInvocation node) {
	// if (node.getExpression() != null && node.getExpression() instanceof
	// TypeLiteral) {
	// TypeLiteral lit = (TypeLiteral) node.getExpression();
	// String utype = getUnresolvedType(lit.getType()), rtype =
	// getResolvedType(lit.getType());
	// this.fullTokens.append(" " + rtype + ".class." +
	// node.getName().getIdentifier() + "() ");
	// this.partialTokens.append(" " + utype + ".class." +
	// node.getName().getIdentifier() + "(" + node.arguments().size() + ") ");
	// } else {
	// IMethodBinding b = node.resolveMethodBinding();
	// ITypeBinding tb = null;
	// if (b != null) {
	// tb = b.getDeclaringClass();
	// if (tb != null) {
	// tb = tb.getTypeDeclaration();
	// if (tb.isLocal() || tb.getQualifiedName().isEmpty())
	// return false;
	// }
	// }
	// this.fullTokens.append(" ");
	// this.partialTokens.append(" ");
	// if (node.getExpression() != null) {
	// node.getExpression().accept(this);
	// } else {
	// if (tb != null) {
	// this.partialTokens.append(" " + getName(tb) + " ");
	// this.fullTokens.append(" " + getQualifiedName(tb) + " ");
	// } else {
	// this.partialTokens.append(" this ");
	// this.fullTokens.append(" this ");
	// }
	// }
	// String name = "."+ node.getName().getIdentifier() + "(" +
	// node.arguments().size() + ")";
	// this.partialTokens.append(" " + name + " ");
	// if (tb != null)
	// name = getSignature(b.getMethodDeclaration());
	// this.fullTokens.append(" " + name + " ");
	// }
	// for (int i = 0; i < node.arguments().size(); i++)
	// ((ASTNode) node.arguments().get(i)).accept(this);
	// return false;
	// }

	public boolean visit(MethodInvocation node) {
		levelOfTraverMD++;
		if (levelOfTraverMD == 1) {
			iaVisitor.refreshInformation();
		}

		if (node.getExpression() != null
				&& node.getExpression() instanceof TypeLiteral) {
			TypeLiteral lit = (TypeLiteral) node.getExpression();
			String utype = getUnresolvedType(lit.getType()), rtype = getResolvedType(lit
					.getType());
			this.fullTokens.append(" " + rtype + ".class."
					+ node.getName().getIdentifier() + "() ");
			this.partialTokens.append(" " + utype + ".class."
					+ node.getName().getIdentifier() + "("
					+ node.arguments().size() + ") ");
		} else {
			IMethodBinding b = node.resolveMethodBinding();
			ITypeBinding tb = null;
			if (b != null) {
				tb = b.getDeclaringClass();
				if (tb != null) {
					tb = tb.getTypeDeclaration();
					if (tb.isLocal() || tb.getQualifiedName().isEmpty())
						return false;
				}
			}
			this.fullTokens.append(" ");
			this.partialTokens.append(" ");
			
			if (node.getExpression() != null) {
				Expression exRetriever = node.getExpression();
				exRetriever.accept(this);
			} else {
				if (tb != null) {
					this.partialTokens.append(" " + getName(tb) + " ");
					this.fullTokens.append(" " + getQualifiedName(tb) + " ");
				} else {
					this.partialTokens.append(" this ");
					this.fullTokens.append(" this ");
				}
			}
			String name = "." + node.getName().getIdentifier() + "("
					+ node.arguments().size() + ")";
			this.partialTokens.append(" " + name + " ");
			if (tb != null)
				name = getSignature(b.getMethodDeclaration());
			this.fullTokens.append(" " + name + " ");
		}
		IMethodBinding iMethod=node.resolveMethodBinding();
		List<Expression> listArgument = node.arguments();
		for (int i = 0; i < listArgument.size(); i++) {
			Expression exParam = listArgument.get(i);			
			exParam.accept(this);
		}
		
		if (levelOfTraverMD == 1) {
			InvocationObject io = new InvocationObject();
			String methodInfo = JavaASTUtil.buildAllSigIngo(node);
			io.setStrMethodInfo(methodInfo);
			if(iaVisitor!=null){
				node.accept(iaVisitor);				
			}
			if(!iaVisitor.getSbAbstractInformation().toString().equals("#")){
				iaVisitor.sortRequiredAPI();
				io.setStrCodeRepresent(iaVisitor.getSbAbstractInformation().toString());
				io.setListQuestionMarkTypes(iaVisitor.getListAbstractTypeQuestionMark());
				io.setSetImportedAPIs(iaVisitor.getSetRequiredAPIsForMI());
				String idenInfo = io.setIDRepresent();
				String id="";
				if(!mapIdenAndID.containsKey(idenInfo)){
					id="E-"+String.format("%09d" , mapIDAndIden.size()+1);
//					
					mapIdenAndID.put(idenInfo, id);
					mapIDAndIden.put(id,idenInfo);
					mapIDAppear.put(id, 1);
					io.saveToFile(hashIdenPath+"/"+id+".txt");
				}else{
					String existId=mapIdenAndID.get(idenInfo);
					id=existId;
					mapIDAppear.put(existId,mapIDAppear.get(existId)+1);
				}
				
				this.partialTokens.append(node.getName().getIdentifier()+"#identifier" + " ");
				this.fullTokens.append(id + " ");

			}
			
			iaVisitor.refreshInformation();
		}
		levelOfTraverMD--;
		return false;
	}

	// @Override
	// public boolean visit(FieldAccess node) {
	// IVariableBinding b = node.resolveFieldBinding();
	// ITypeBinding tb = null;
	// if (b != null) {
	// tb = b.getDeclaringClass();
	// if (tb != null) {
	// tb = tb.getTypeDeclaration();
	// if (tb.isLocal() || tb.getQualifiedName().isEmpty())
	// return false;
	// }
	// }
	// this.fullTokens.append(" ");
	// this.partialTokens.append(" ");
	// node.getExpression().accept(this);
	// String name = "." + node.getName().getIdentifier();
	// this.partialTokens.append(" " + name + " ");
	// if (b != null) {
	// if (tb != null)
	// name = getQualifiedName(tb.getTypeDeclaration()) + name;
	// /*else
	// name = "Array" + name;*/
	// }
	// this.fullTokens.append(" " + name + " ");
	// return false;
	// }

//	public boolean visit(FieldAccess node) {
//		IVariableBinding b = node.resolveFieldBinding();
//		ITypeBinding tb = null;
//		if (b != null) {
//			tb = b.getDeclaringClass();
//			if (tb != null) {
//				tb = tb.getTypeDeclaration();
//				if (tb.isLocal() || tb.getQualifiedName().isEmpty())
//					return false;
//			}
//		}
//		this.fullTokens.append(" ");
//		this.partialTokens.append(" ");
//		node.getExpression().accept(this);
//		String name = "." + node.getName().getIdentifier();
//		this.partialTokens.append(" " + name + " ");
//		if (b != null) {
//			if (tb != null)
//				name = getQualifiedName(tb.getTypeDeclaration()) + name;
//			/*
//			 * else name = "Array" + name;
//			 */
//		}
//		this.fullTokens.append(" " + name + " ");		
////		if (isGetInfoForReceiver) {
////			sbAbstractInformation.append("?");
////			listAbstractTypeQuestionMark.add(currentReceiverArgType);
////		} else if(isGetInfoForParamI){
////			sbAbstractInformation.append("?");
////			listAbstractTypeQuestionMark.add(currentParamIArgType);
////		}
//		return false;
//	}

	// @Override
	// public boolean visit(SimpleName node) {
	// IBinding b = node.resolveBinding();
	// if (b != null) {
	// if (b instanceof IVariableBinding) {
	// IVariableBinding vb = (IVariableBinding) b;
	// ITypeBinding tb = vb.getType();
	// if (tb != null) {
	// tb = tb.getTypeDeclaration();
	// if (tb.isLocal() || tb.getQualifiedName().isEmpty())
	// return false;
	// this.fullTokens.append(" " + getQualifiedName(tb) + " ");
	// this.partialTokens.append(" " + getName(tb) + " ");
	// }
	// } else if (b instanceof ITypeBinding) {
	// ITypeBinding tb = (ITypeBinding) b;
	// tb = tb.getTypeDeclaration();
	// if (tb.isLocal() || tb.getQualifiedName().isEmpty())
	// return false;
	// this.fullTokens.append(" " + getQualifiedName(tb) + " ");
	// this.partialTokens.append(" " + getName(tb) + " ");
	// }
	// } else {
	// this.fullTokens.append(" " + node.getIdentifier() + " ");
	// this.partialTokens.append(" " + node.getIdentifier() + " ");
	// }
	// return false;
	// }

	
	
//	public boolean visit(SimpleName node) {
//		IBinding b = node.resolveBinding();
//		if (b != null) {
//			if (b instanceof IVariableBinding) {
//				IVariableBinding vb = (IVariableBinding) b;
//				ITypeBinding tb = vb.getType();
//				if (tb != null) {
//					tb = tb.getTypeDeclaration();
//					if (tb.isLocal() || tb.getQualifiedName().isEmpty())
//						return false;
//					this.fullTokens.append(" " + getQualifiedName(tb) + " ");
//					this.partialTokens.append(" " + getName(tb) + " ");
//				}
//			} else if (b instanceof ITypeBinding) {
//				ITypeBinding tb = (ITypeBinding) b;
//				tb = tb.getTypeDeclaration();
//				if (tb.isLocal() || tb.getQualifiedName().isEmpty())
//					return false;
//				this.fullTokens.append(" " + getQualifiedName(tb) + " ");
//				this.partialTokens.append(" " + getName(tb) + " ");
//			}
//		} else {
//			this.fullTokens.append(" " + node.getIdentifier() + " ");
//			this.partialTokens.append(" " + node.getIdentifier() + " ");
//		}
//		return false;
//	}

	@Override
	public boolean visit(ArrayAccess node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(ArrayCreation node) {
		String utype = getUnresolvedType(node.getType()), rtype = getResolvedType(node.getType());
		this.partialTokens.append(" new " + utype + " ");
		this.fullTokens.append(" new " + rtype + " ");
		if (node.getInitializer() != null)
			node.getInitializer().accept(this);
		else
			for (int i = 0; i < node.dimensions().size(); i++)
				((Expression) (node.dimensions().get(i))).accept(this);
		return false;
	}

	@Override
	public boolean visit(ArrayInitializer node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(AssertStatement node) {
		this.fullTokens.append(" assert ");
		this.partialTokens.append(" assert ");
		return super.visit(node);
	}

	@Override
	public boolean visit(Assignment node) {
		node.getLeftHandSide().accept(this);
		this.fullTokens.append(" "+node.getOperator().toString()+" ");
		this.partialTokens.append(" "+node.getOperator().toString()+" ");
		node.getRightHandSide().accept(this);
		return false;
	}

	@Override
	public boolean visit(Block node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(BooleanLiteral node) {
		this.fullTokens.append(" boolean ");
		this.partialTokens.append(" boolean ");
		return false;
	}

	@Override
	public boolean visit(BreakStatement node) {
		return false;
	}

	@Override
	public boolean visit(CastExpression node) {
		String utype = getUnresolvedType(node.getType()), rtype = getResolvedType(node.getType());
		this.fullTokens.append(" " + rtype + " <cast> ");
		this.partialTokens.append(" " + utype + " <cast> ");
		node.getExpression().accept(this);
		return false;
	}

	@Override
	public boolean visit(CatchClause node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(CharacterLiteral node) {
		this.fullTokens.append(" char ");
		this.partialTokens.append(" char ");
		return false;
	}

	@Override
	public boolean visit(ClassInstanceCreation node) {
		ITypeBinding tb = node.getType().resolveBinding();
		if (tb != null && tb.getTypeDeclaration().isLocal())
			return false;
		String utype = getUnresolvedType(node.getType());
		IMethodBinding b = node.resolveConstructorBinding();
		if (b == null)
			this.fullTokens.append(" new " + utype + "(" + node.arguments().size() + ") ");
		else
			this.fullTokens.append(" new " + getSignature(b.getMethodDeclaration()) + " ");
		this.partialTokens.append(" new " + utype + "(" + node.arguments().size() + ") ");
		for (Iterator it = node.arguments().iterator(); it.hasNext(); ) {
			Expression e = (Expression) it.next();
			e.accept(this);
		}
		if (node.getAnonymousClassDeclaration() != null)
			node.getAnonymousClassDeclaration().accept(this);
		return false;
	}

	@Override
	public boolean visit(ConditionalExpression node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(ConstructorInvocation node) {
		IMethodBinding b = node.resolveConstructorBinding();
		ITypeBinding tb = null;
		if (b != null && b.getDeclaringClass() != null)
			tb = b.getDeclaringClass().getTypeDeclaration();
		if (tb != null) {
			if (tb.isLocal() || tb.getQualifiedName().isEmpty())
				return false;
		}
		String name = "." + className + "(" + node.arguments().size() + ")";
		this.partialTokens.append(" " + name + " ");
		if (tb != null)
			name = getSignature(b.getMethodDeclaration());
		this.fullTokens.append(" " + name + " ");
		for (int i = 0; i < node.arguments().size(); i++)
			((ASTNode) node.arguments().get(i)).accept(this);
		return false;
	}

	@Override
	public boolean visit(ContinueStatement node) {
		return false;
	}

	@Override
	public boolean visit(CreationReference node) {
		return false;
	}

	@Override
	public boolean visit(Dimension node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(DoStatement node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(EmptyStatement node) {
		return false;
	}

	@Override
	public boolean visit(EnhancedForStatement node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(EnumConstantDeclaration node) {
		return false;
	}

	@Override
	public boolean visit(EnumDeclaration node) {
		return false;
	}

	@Override
	public boolean visit(ExpressionMethodReference node) {
		return false;
	}

	@Override
	public boolean visit(ExpressionStatement node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(FieldAccess node) {
		IVariableBinding b = node.resolveFieldBinding();
		ITypeBinding tb = null;
		if (b != null) {
			tb = b.getDeclaringClass();
			if (tb != null) {
				tb = tb.getTypeDeclaration();
				if (tb.isLocal() || tb.getQualifiedName().isEmpty())
					return false;
			}
		}
		this.fullTokens.append(" ");
		this.partialTokens.append(" ");
		node.getExpression().accept(this);
		String name = "." + node.getName().getIdentifier();
		this.partialTokens.append(" " + name + " ");
		if (b != null) {
			if (tb != null)
				name = getQualifiedName(tb.getTypeDeclaration()) + name;
			/*else
				name = "Array" + name;*/
		}
		this.fullTokens.append(" " + name + " ");
		return false;
	}

	@Override
	public boolean visit(FieldDeclaration node) {
		return false;
	}

	@Override
	public boolean visit(ForStatement node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(IfStatement node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(ImportDeclaration node) {
		return false;
	}

	@Override
	public boolean visit(InfixExpression node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(Initializer node) {
		return false;
	}

	@Override
	public boolean visit(InstanceofExpression node) {
		this.fullTokens.append(" ");
		this.partialTokens.append(" ");
		node.getLeftOperand().accept(this);
		this.fullTokens.append(" <instanceof> ");
		this.partialTokens.append(" <instanceof> ");
		String rtype = getResolvedType(node.getRightOperand()), utype = getUnresolvedType(node.getRightOperand());
		this.fullTokens.append(rtype + " ");
		this.partialTokens.append(utype + " ");
		return false;
	}

	@Override
	public boolean visit(LabeledStatement node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(LambdaExpression node) {
		return false;
	}

//	@Override
//	public boolean visit(MethodDeclaration node) {
//		if (node.getBody() != null && !node.getBody().statements().isEmpty())
//			node.getBody().accept(this);
//		return false;
//	}

//	@Override
//	public boolean visit(MethodInvocation node) {
//		if (node.getExpression() != null && node.getExpression() instanceof TypeLiteral) {
//			TypeLiteral lit = (TypeLiteral) node.getExpression();
//			String utype = getUnresolvedType(lit.getType()), rtype = getResolvedType(lit.getType());
//			this.fullTokens.append(" " + rtype + ".class." + node.getName().getIdentifier() + "() ");
//			this.partialTokens.append(" " + utype + ".class." + node.getName().getIdentifier() + "(" + node.arguments().size() + ") ");
//		} else {
//			IMethodBinding b = node.resolveMethodBinding();
//			ITypeBinding tb = null;
//			if (b != null) {
//				tb = b.getDeclaringClass();
//				if (tb != null) {
//					tb = tb.getTypeDeclaration();
//					if (tb.isLocal() || tb.getQualifiedName().isEmpty())
//						return false;
//				}
//			}
//			this.fullTokens.append(" ");
//			this.partialTokens.append(" ");
//			if (node.getExpression() != null) {
//				node.getExpression().accept(this);
//			} else {
//				if (tb != null) {
//					this.partialTokens.append(" " + getName(tb) + " ");
//					this.fullTokens.append(" " + getQualifiedName(tb) + " ");
//				} else {
//					this.partialTokens.append(" this ");
//					this.fullTokens.append(" this ");
//				}
//			}
//			String name = "."+ node.getName().getIdentifier() + "(" + node.arguments().size() + ")";
//			this.partialTokens.append(" " + name + " ");
//			if (tb != null)
//				name = getSignature(b.getMethodDeclaration());
//			this.fullTokens.append(" " + name + " ");
//		}
//		for (int i = 0; i < node.arguments().size(); i++)
//			((ASTNode) node.arguments().get(i)).accept(this);
//		return false;
//	}

	@Override
	public boolean visit(Modifier node) {
		return false;
	}

	@Override
	public boolean visit(NormalAnnotation node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(NullLiteral node) {
		this.fullTokens.append(" null ");
		this.partialTokens.append(" null ");
		return false;
	}

	@Override
	public boolean visit(NumberLiteral node) {
		this.fullTokens.append(" number ");
		this.partialTokens.append(" number ");
		return false;
	}

	@Override
	public boolean visit(PackageDeclaration node) {
		return false;
	}

	@Override
	public boolean visit(ParenthesizedExpression node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(PostfixExpression node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(PrefixExpression node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(QualifiedName node) {
		IBinding b = node.resolveBinding();
		IVariableBinding vb = null;
		ITypeBinding tb = null;
		if (b != null) {
			if (b instanceof IVariableBinding) {
				vb = (IVariableBinding) b;
				tb = vb.getDeclaringClass();
				if (tb != null) {
					tb = tb.getTypeDeclaration();
					if (tb.isLocal() || tb.getQualifiedName().isEmpty())
						return false;
				}
			} else if (b instanceof ITypeBinding) {
				tb = ((ITypeBinding) b).getTypeDeclaration();
				if (tb.isLocal() || tb.getQualifiedName().isEmpty())
					return false;
				this.partialTokens.append(" " + node.getFullyQualifiedName() + " ");
				this.fullTokens.append(" " + getQualifiedName(tb) + " ");
				return false;
			}
		} else {
			this.partialTokens.append(" " + node.getFullyQualifiedName() + " ");
			this.fullTokens.append(" " + node.getFullyQualifiedName() + " ");
			return false;
		}
		node.getQualifier().accept(this);
		String name = "." + node.getName().getIdentifier();
		this.partialTokens.append(" " + name + " ");
		if (b != null) {
			if (b instanceof IVariableBinding) {
				if (tb != null)
					name = getQualifiedName(tb.getTypeDeclaration()) + name;
				/*else
					name = "Array" + name;*/
			}
		}
		this.fullTokens.append(" " + name + " ");
		return false;
	}

	@Override
	public boolean visit(ReturnStatement node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(SimpleName node) {
		IBinding b = node.resolveBinding();
		if (b != null) {
			if (b instanceof IVariableBinding) {
				IVariableBinding vb = (IVariableBinding) b;
				ITypeBinding tb = vb.getType();
				if (tb != null) {
					tb = tb.getTypeDeclaration();
					if (tb.isLocal() || tb.getQualifiedName().isEmpty())
						return false;
					this.fullTokens.append(" " + getQualifiedName(tb) + " ");
					this.partialTokens.append(" " + getName(tb) + " ");
				}
			} else if (b instanceof ITypeBinding) {
				ITypeBinding tb = (ITypeBinding) b;
				tb = tb.getTypeDeclaration();
				if (tb.isLocal() || tb.getQualifiedName().isEmpty())
					return false;
				this.fullTokens.append(" " + getQualifiedName(tb) + " ");
				this.partialTokens.append(" " + getName(tb) + " ");
			}
		} else {
			this.fullTokens.append(" " + node.getIdentifier() + " ");
			this.partialTokens.append(" " + node.getIdentifier() + " ");
		}
		return false;
	}

	@Override
	public boolean visit(SingleMemberAnnotation node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(SingleVariableDeclaration node) {
		ITypeBinding tb = node.getType().resolveBinding();
		if (tb != null && tb.getTypeDeclaration().isLocal())
			return false;
		String utype = getUnresolvedType(node.getType()), rtype = getResolvedType(node.getType());
		this.partialTokens.append(" " + utype + " ");
		this.fullTokens.append(" " + rtype + " ");
		if (node.getInitializer() != null) {
			this.partialTokens.append("= ");
			this.fullTokens.append("= ");
			node.getInitializer().accept(this);
		}
		return false;
	}

	@Override
	public boolean visit(StringLiteral node) {
		this.fullTokens.append(" java.lang.String ");
		this.partialTokens.append(" java.lang.String ");
		return false;
	}

	@Override
	public boolean visit(SuperConstructorInvocation node) {
		IMethodBinding b = node.resolveConstructorBinding();
		ITypeBinding tb = null;
		if (b != null && b.getDeclaringClass() != null)
			tb = b.getDeclaringClass().getTypeDeclaration();
		if (tb != null) {
			if (tb.isLocal() || tb.getQualifiedName().isEmpty())
				return false;
		}
		String name = "." + superClassName + "(" + node.arguments().size() + ")";
		this.partialTokens.append(" " + name + " ");
		if (tb != null)
			name = getSignature(b.getMethodDeclaration());
		this.fullTokens.append(" " + name + " ");
		for (int i = 0; i < node.arguments().size(); i++)
			((ASTNode) node.arguments().get(i)).accept(this);
		return false;
	}

	@Override
	public boolean visit(SuperFieldAccess node) {
		IVariableBinding b = node.resolveFieldBinding();
		ITypeBinding tb = null;
		if (b != null && b.getDeclaringClass() != null) {
			tb = b.getDeclaringClass().getTypeDeclaration();
			if (tb.isLocal() || tb.getQualifiedName().isEmpty())
				return false;
			this.partialTokens.append(" " + getName(tb) + " ");
			this.fullTokens.append(" " + getQualifiedName(tb) + " ");
		} else {
			this.partialTokens.append(" super ");
			this.fullTokens.append(" super ");
		}
		String name = "." + node.getName().getIdentifier();
		this.partialTokens.append(" " + name + " ");
		if (tb != null)
			name = getQualifiedName(tb) + name;
		this.fullTokens.append(" " + name + " ");
		return false;
	}

	@Override
	public boolean visit(SuperMethodInvocation node) {
		IMethodBinding b = node.resolveMethodBinding();
		ITypeBinding tb = null;
		if (b != null && b.getDeclaringClass() != null)
			tb = b.getDeclaringClass().getTypeDeclaration();
		if (tb != null) {
			if (tb.isLocal() || tb.getQualifiedName().isEmpty())
				return false;
			this.partialTokens.append(" " + getName(tb) + " ");
			this.fullTokens.append(" " + getQualifiedName(tb) + " ");
		} else {
			this.partialTokens.append(" super ");
			this.fullTokens.append(" super ");
		}
		String name = "." + node.getName().getIdentifier() + "(" + node.arguments().size() + ")";
		this.partialTokens.append(" " + name + " ");
		if (tb != null)
			name = getSignature(b.getMethodDeclaration());
		this.fullTokens.append(" " + name + " ");
		for (int i = 0; i < node.arguments().size(); i++)
			((ASTNode) node.arguments().get(i)).accept(this);
		return false;
	}

	@Override
	public boolean visit(SuperMethodReference node) {
		return false;
	}

	@Override
	public boolean visit(SwitchCase node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(SwitchStatement node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(SynchronizedStatement node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(ThisExpression node) {
		ITypeBinding b = node.resolveTypeBinding();
		if (b != null) {
			b = b.getTypeDeclaration();
			if (b.isLocal() || b.getQualifiedName().isEmpty())
				return false;
			this.partialTokens.append(" " + getName(b) + " ");
			this.fullTokens.append(" " + getQualifiedName(b) + " ");
		} else {
			this.partialTokens.append(" this ");
			this.fullTokens.append(" this ");
		}
		return false;
	}

	@Override
	public boolean visit(ThrowStatement node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(TryStatement node) {
		return super.visit(node);
	}

//	@Override
//	public boolean visit(TypeDeclaration node) {
//		return false;
//	}

	@Override
	public boolean visit(TypeDeclarationStatement node) {
		return false;
	}

	@Override
	public boolean visit(TypeLiteral node) {
		String utype = getUnresolvedType(node.getType()), rtype = getResolvedType(node.getType());
		this.fullTokens.append(" " + rtype + ".class ");
		this.partialTokens.append(" " + utype + ".class ");
		return false;
	}

	@Override
	public boolean visit(TypeMethodReference node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(TypeParameter node) {
		return super.visit(node);
	}
	
	@Override
	public boolean visit(VariableDeclarationExpression node) {
		ITypeBinding tb = node.getType().resolveBinding();
		if (tb != null && tb.getTypeDeclaration().isLocal())
			return false;
		String utype = getUnresolvedType(node.getType()), rtype = getResolvedType(node.getType());
		this.partialTokens.append(" " + utype + " ");
		this.fullTokens.append(" " + rtype + " ");
		for (int i = 0; i < node.fragments().size(); i++)
			((ASTNode) node.fragments().get(i)).accept(this);
		return false;
	}

	@Override
	public boolean visit(VariableDeclarationStatement node) {
		ITypeBinding tb = node.getType().resolveBinding();
		if (tb != null && tb.getTypeDeclaration().isLocal())
			return false;
		String utype = getUnresolvedType(node.getType()), rtype = getResolvedType(node.getType());
		this.partialTokens.append(" " + utype + " ");
		this.fullTokens.append(" " + rtype + " ");
		for (int i = 0; i < node.fragments().size(); i++)
			((ASTNode) node.fragments().get(i)).accept(this);
		return false;
	}

	@Override
	public boolean visit(VariableDeclarationFragment node) {
		Type type = getType(node);
		String utype = getUnresolvedType(type), rtype = getResolvedType(type);
		this.partialTokens.append(" " + utype + " ");
		this.fullTokens.append(" " + rtype + " ");
		if (node.getInitializer() != null) {
			this.partialTokens.append("= ");
			this.fullTokens.append("= ");
			node.getInitializer().accept(this);
		}
		return false;
	}

	@Override
	public boolean visit(WhileStatement node) {
		return super.visit(node);
	}
	
	@Override
	public boolean visit(ArrayType node) {
		return false;
	}
	
	@Override
	public boolean visit(IntersectionType node) {
		return false;
	}
	
	@Override
	public boolean visit(ParameterizedType node) {
		return false;
	}
	
	@Override
	public boolean visit(UnionType node) {
		return false;
	}
	
	@Override
	public boolean visit(NameQualifiedType node) {
		return false;
	}
	
	@Override
	public boolean visit(PrimitiveType node) {
		return false;
	}
	
	@Override
	public boolean visit(QualifiedType node) {
		return false;
	}
	
	@Override
	public boolean visit(SimpleType node) {
		return false;
	}
	
	@Override
	public boolean visit(WildcardType node) {
		return false;
	}

	private String getQualifiedName(ITypeBinding tb) {
		if (tb.isArray())
			return getQualifiedName(tb.getComponentType().getTypeDeclaration()) + getDimensions(tb.getDimensions());
		return tb.getQualifiedName();
	}

	private String getName(ITypeBinding tb) {
		if (tb.isArray())
			return getName(tb.getComponentType().getTypeDeclaration()) + getDimensions(tb.getDimensions());
		return tb.getName();
	}


	public static void main(String[] args) {
		String projectLocation = "/Users/hungphan/Documents/workspace/SampleMethodInvocationProject/";
		String outputIdLocation = "/Users/hungphan/Documents/workspace/OutputMethodId/";
		String jdkPath = "/Library/Java/JavaVirtualMachines/jdk1.8.0_141.jdk/Contents/Home/jre/lib/rt.jar";
		String fileLocation = "/Users/hungphan/Documents/workspace/SampleMethodInvocationProject/src/examples/CalGetInstance.java";
		MethodEncoderVisitor visitor = new MethodEncoderVisitor("", "");
		visitor.parseProject(projectLocation, outputIdLocation, jdkPath);
		visitor.parseFile(fileLocation);
		visitor.parseForAbstractingMethodInvocation(fileLocation);
	}

}
