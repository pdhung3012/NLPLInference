/*******************************************************************************
 * Copyright (c) 2000, 2018 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package invocations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;

import javax.swing.text.html.StyleSheet.ListPainter;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.internal.compiler.parser.ScannerHelper;

import consts.PathConstanct;
import entities.LocalEntity;
import entities.LocalForMethod;

/**
 * Internal AST visitor for serializing an AST in a quick and dirty fashion. For
 * various reasons the resulting string is not necessarily legal Java code; and
 * even if it is legal Java code, it is not necessarily the string that
 * corresponds to the given AST. Although useless for most purposes, it's fine
 * for generating debug print strings.
 * <p>
 * Example usage: <code>
 * <pre>
 *    NaiveASTFlattener p = new NaiveASTFlattener();
 *    node.accept(p);
 *    String result = p.getResult();
 * </pre>
 * </code> Call the <code>reset</code> method to clear the previous result
 * before reusing an existing instance.
 * </p>
 * 
 * @since 2.0
 */
@SuppressWarnings("rawtypes")
public class InvocationAbstractorVisitor extends ASTVisitor {
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
	 * Internal synonym for {@link AST#JLS8}. Use to alleviate deprecation
	 * warnings.
	 * 
	 * @deprecated
	 * @since 3.14
	 */
	private static final int JLS8 = AST.JLS8;

	/**
	 * Internal synonym for {@link AST#JLS9}. Use to alleviate deprecation
	 * warnings.
	 * 
	 * @deprecated
	 * @since 3.14
	 */
	// private static final int JLS9 = AST.JLS9;

	/**
	 * The string buffer into which the serialized representation of the AST is
	 * written.
	 */
	private int indent = 0;
	private static final String SEPARATOR = "#";
	public static String ANNOTATION_VAR="#var";

	private HashMap<String, String> setSequencesOfMethods, setOfUnResolvedType;
	private LinkedHashSet<LocalEntity> setFields, setArguments,
			setLocalVariables;
	private LinkedHashSet<String> setRequiredAPIsForMI = new LinkedHashSet<String>();
	private StringBuilder sbAbstractInformation = new StringBuilder();
	private ArrayList<String> listAbstractTypeQuestionMark = new ArrayList<String>();
	private ArrayList<String> listPatialTypeRequiredParam = new ArrayList<String>();
	private ArrayList<String> listFQNTypeRequiredParam = new ArrayList<String>();

	private String strSplitCharacter = " ";

	private boolean isVisitMethod = false;
	private int typeOfTraverse = 0;
	private boolean isParsingType;
	private boolean isVisitInsideMethodDeclaration = false,
			isSimpleNameMethod = false;
	private boolean isGetInfoForIdentifer = false;
	private StringBuffer unresolvedBuffer;

	ASTParser parser = ASTParser.newParser(AST.JLS4);
	String[] classpath = { PathConstanct.PATH_JAVA_CLASSPATH };
	HashMap<String, CompilationUnit> mapCU;
	LinkedHashMap<String, LocalForMethod> mapLocalcontextForMethod = new LinkedHashMap<String, LocalForMethod>();
	private boolean isAbstractMethod = false;
	private StringBuilder sbTotalBuilder = new StringBuilder();
	private LocalForMethod currentLocalMethod = null;
	private MethodDeclaration currentMethodDecl = null;
	private int levelOfTraverMD = 0;
	private String fopInvocationObject;
	private String hashIdenPath;
	private String currentStrParentType = "";
	private String currentMethodDeclaration = "";
	private String currentClassDeclaration = "";
	private ArrayList<String> listOfRelatedWordsTarget=new ArrayList<String>();
	private ArrayList<String> listOfRelatedWordsSource=new ArrayList<String>();
	public static String CamelCaseRegex="([^_A-Z])([A-Z])";

	public void sortRequiredAPI() {
		ArrayList<String> lst = new ArrayList<String>();
		for (String str : setRequiredAPIsForMI) {
			lst.add(str);
		}
		Collections.sort(lst);
		setRequiredAPIsForMI.clear();
		for (int i = 0; i < lst.size(); i++) {
			setRequiredAPIsForMI.add(lst.get(i));
		}
	}

	public String getCurrentMethodDeclaration() {
		return currentMethodDeclaration;
	}

	public void setCurrentMethodDeclaration(String currentMethodDeclaration) {
		this.currentMethodDeclaration = currentMethodDeclaration;
	}

	public String getCurrentClassDeclaration() {
		return currentClassDeclaration;
	}

	public void setCurrentClassDeclaration(String currentClassDeclaration) {
		this.currentClassDeclaration = currentClassDeclaration;
	}

	/**
	 * Creates a new AST printer.
	 */
	public InvocationAbstractorVisitor() {
		this.sbAbstractInformation = new StringBuilder();
	}

	/**
	 * Internal synonym for {@link ClassInstanceCreation#getName()}. Use to
	 * alleviate deprecation warnings.
	 * 
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
		return this.sbAbstractInformation.toString();
	}

	/**
	 * Internal synonym for {@link MethodDeclaration#getReturnType()}. Use to
	 * alleviate deprecation warnings.
	 * 
	 * @deprecated
	 * @since 3.4
	 */
	private static Type getReturnType(MethodDeclaration node) {
		return node.getReturnType();
	}

	/**
	 * Internal synonym for {@link TypeDeclaration#getSuperclass()}. Use to
	 * alleviate deprecation warnings.
	 * 
	 * @deprecated
	 * @since 3.4
	 */
	private static Name getSuperclass(TypeDeclaration node) {
		return node.getSuperclass();
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

	void processAndAddWordToRelatedWords(String strItem){
		try{
			String[] arr=strItem.replaceAll("([^_A-Z])([A-Z])", "$1 $2").split("\\s+");
			for(int i=0;i<arr.length;i++){
				String strLowerItemSource=arr[i].toLowerCase().trim()+"#term";
				String strLowerItemTarget=arr[i].toLowerCase().trim()+"#"+strItem+"#ele";
				listOfRelatedWordsTarget.add(strLowerItemTarget);
				listOfRelatedWordsSource.add(strLowerItemSource);
			}
		}catch(Exception ex){
			
		}
	}
	void printIndent() {
		for (int i = 0; i < this.indent; i++)
			this.sbAbstractInformation.append("  "); //$NON-NLS-1$
	}

	public void refreshInformation() {
		sbAbstractInformation = new StringBuilder();
		listAbstractTypeQuestionMark = new ArrayList<String>();
		setRequiredAPIsForMI = new LinkedHashSet<String>();
		listPatialTypeRequiredParam=new ArrayList<String>();
		listFQNTypeRequiredParam=new ArrayList<String>();
		listOfRelatedWordsSource=new ArrayList<String>();
		listOfRelatedWordsTarget=new ArrayList<String>();
	}

	
	
	public ArrayList<String> getListOfRelatedWordsTarget() {
		return listOfRelatedWordsTarget;
	}

	public void setListOfRelatedWordsTarget(
			ArrayList<String> listOfRelatedWordsTarget) {
		this.listOfRelatedWordsTarget = listOfRelatedWordsTarget;
	}

	public ArrayList<String> getListOfRelatedWordsSource() {
		return listOfRelatedWordsSource;
	}

	public void setListOfRelatedWordsSource(
			ArrayList<String> listOfRelatedWordsSource) {
		this.listOfRelatedWordsSource = listOfRelatedWordsSource;
	}

	public ArrayList<String> getListAbstractTypeQuestionMark() {
		return listAbstractTypeQuestionMark;
	}

	public void setListAbstractTypeQuestionMark(
			ArrayList<String> listAbstractTypeQuestionMark) {
		this.listAbstractTypeQuestionMark = listAbstractTypeQuestionMark;
	}

	public LinkedHashSet<String> getSetRequiredAPIsForMI() {
		return setRequiredAPIsForMI;
	}

	public void setSetRequiredAPIsForMI(
			LinkedHashSet<String> setRequiredAPIsForMI) {
		this.setRequiredAPIsForMI = setRequiredAPIsForMI;
	}

	public StringBuilder getSbAbstractInformation() {
		return sbAbstractInformation;
	}

	public void setSbAbstractInformation(StringBuilder sbAbstractInformation) {
		this.sbAbstractInformation = sbAbstractInformation;
	}

	public String getPartialParamSequence(){
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<listPatialTypeRequiredParam.size();i++){
			sb.append(listPatialTypeRequiredParam.get(i)+ANNOTATION_VAR+" ");
		}
		return sb.toString();
		
	}
	
	public String getFQNParamSequence(){
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<listFQNTypeRequiredParam.size();i++){
			sb.append(listFQNTypeRequiredParam.get(i)+ANNOTATION_VAR+" ");
		}
		return sb.toString();		
	}
	
	/**
	 * Appends the text representation of the given modifier flags, followed by
	 * a single space. Used for JLS2 modifiers.
	 * 
	 * @param modifiers
	 *            the modifier flags
	 */
	void printModifiers(int modifiers) {
		if (Modifier.isPublic(modifiers)) {
			this.sbAbstractInformation.append("public ");//$NON-NLS-1$
		}
		if (Modifier.isProtected(modifiers)) {
			this.sbAbstractInformation.append("protected ");//$NON-NLS-1$
		}
		if (Modifier.isPrivate(modifiers)) {
			this.sbAbstractInformation.append("private ");//$NON-NLS-1$
		}
		if (Modifier.isStatic(modifiers)) {
			this.sbAbstractInformation.append("static ");//$NON-NLS-1$
		}
		if (Modifier.isAbstract(modifiers)) {
			this.sbAbstractInformation.append("abstract ");//$NON-NLS-1$
		}
		if (Modifier.isFinal(modifiers)) {
			this.sbAbstractInformation.append("final ");//$NON-NLS-1$
		}
		if (Modifier.isSynchronized(modifiers)) {
			this.sbAbstractInformation.append("synchronized ");//$NON-NLS-1$
		}
		if (Modifier.isVolatile(modifiers)) {
			this.sbAbstractInformation.append("volatile ");//$NON-NLS-1$
		}
		if (Modifier.isNative(modifiers)) {
			this.sbAbstractInformation.append("native ");//$NON-NLS-1$
		}
		if (Modifier.isStrictfp(modifiers)) {
			this.sbAbstractInformation.append("strictfp ");//$NON-NLS-1$
		}
		if (Modifier.isTransient(modifiers)) {
			this.sbAbstractInformation.append("transient ");//$NON-NLS-1$
		}
	}

	/**
	 * Appends the text representation of the given modifier flags, followed by
	 * a single space. Used for 3.0 modifiers and annotations.
	 * 
	 * @param ext
	 *            the list of modifier and annotation nodes (element type:
	 *            <code>IExtendedModifiers</code>)
	 */
	void printModifiers(List ext) {
		for (Iterator it = ext.iterator(); it.hasNext();) {
			ASTNode p = (ASTNode) it.next();
			p.accept(this);
			this.sbAbstractInformation.append(" ");//$NON-NLS-1$
		}
	}

	private void printTypes(List<Type> types, String prefix) {
		if (types.size() > 0) {
			this.sbAbstractInformation.append(" " + prefix + " ");//$NON-NLS-1$ //$NON-NLS-2$
			Type type = types.get(0);
			type.accept(this);
			for (int i = 1, l = types.size(); i < l; ++i) {
				this.sbAbstractInformation.append(","); //$NON-NLS-1$
				type = types.get(0);
				type.accept(this);
			}
		}
	}

	/**
	 * reference node helper function that is common to all the difference
	 * reference nodes.
	 * 
	 * @param typeArguments
	 *            list of type arguments
	 */
	private void visitReferenceTypeArguments(List typeArguments) {
		this.sbAbstractInformation.append("::");//$NON-NLS-1$
		if (!typeArguments.isEmpty()) {
			this.sbAbstractInformation.append('<');
			for (Iterator it = typeArguments.iterator(); it.hasNext();) {
				Type t = (Type) it.next();
				t.accept(this);
				if (it.hasNext()) {
					this.sbAbstractInformation.append(',');
				}
			}
			this.sbAbstractInformation.append('>');
		}
	}

	private void visitTypeAnnotations(AnnotatableType node) {
		if (node.getAST().apiLevel() >= JLS8) {
			visitAnnotationsList(node.annotations());
		}
	}

	private void visitAnnotationsList(List annotations) {
		for (Iterator it = annotations.iterator(); it.hasNext();) {
			Annotation annotation = (Annotation) it.next();
			annotation.accept(this);
			this.sbAbstractInformation.append(' ');
		}
	}

	/**
	 * Resets this printer so that it can be used again.
	 */
	public void reset() {
		this.sbAbstractInformation.setLength(0);
	}

	/**
	 * Internal synonym for {@link TypeDeclaration#superInterfaces()}. Use to
	 * alleviate deprecation warnings.
	 * 
	 * @deprecated
	 * @since 3.4
	 */
	private List superInterfaces(TypeDeclaration node) {
		return node.superInterfaces();
	}

	@Override
	public boolean visit(AnnotationTypeDeclaration node) {
		if (node.getJavadoc() != null) {
			node.getJavadoc().accept(this);
		}
		printIndent();
		printModifiers(node.modifiers());
		this.sbAbstractInformation.append("@interface ");//$NON-NLS-1$
		node.getName().accept(this);
		this.sbAbstractInformation.append(" {");//$NON-NLS-1$
		for (Iterator it = node.bodyDeclarations().iterator(); it.hasNext();) {
			BodyDeclaration d = (BodyDeclaration) it.next();
			d.accept(this);
		}
		this.sbAbstractInformation.append("}\n");//$NON-NLS-1$
		return false;
	}

	@Override
	public boolean visit(AnnotationTypeMemberDeclaration node) {
		if (node.getJavadoc() != null) {
			node.getJavadoc().accept(this);
		}
		printIndent();
		printModifiers(node.modifiers());
		node.getType().accept(this);
		this.sbAbstractInformation.append(" ");//$NON-NLS-1$
		node.getName().accept(this);
		this.sbAbstractInformation.append("()");//$NON-NLS-1$
		if (node.getDefault() != null) {
			this.sbAbstractInformation.append(" default ");//$NON-NLS-1$
			node.getDefault().accept(this);
		}
		this.sbAbstractInformation.append(";\n");//$NON-NLS-1$
		return false;
	}

	@Override
	public boolean visit(AnonymousClassDeclaration node) {
		this.sbAbstractInformation.append("{\n");//$NON-NLS-1$
		this.indent++;
		for (Iterator it = node.bodyDeclarations().iterator(); it.hasNext();) {
			BodyDeclaration b = (BodyDeclaration) it.next();
			b.accept(this);
		}
		this.indent--;
		printIndent();
		this.sbAbstractInformation.append("}\n");//$NON-NLS-1$
		return false;
	}

	public String getFQN(Expression exp) {
		ITypeBinding itype = exp.resolveTypeBinding();
		return itype != null ? itype.getQualifiedName() : "Unknown";
	}

	public void addRequiredAPIForImport(Expression exp) {
		String requiredType = getFQN(exp);
		if (!requiredType.equals("Unknown")) {
			setRequiredAPIsForMI.add(requiredType);
		}
	}

	public void addRequiredAPIForImport(ITypeBinding exp) {
		String requiredType = exp != null ? exp.getQualifiedName() : "Unknown";
		if (!requiredType.equals("Unknown")) {
			setRequiredAPIsForMI.add(requiredType);
		}
	}

	@Override
	public boolean visit(ArrayAccess node) {
		addRequiredAPIForImport(node.getArray());

		node.getArray().accept(this);
		this.sbAbstractInformation.append("[");//$NON-NLS-1$
		node.getIndex().accept(this);
		this.sbAbstractInformation.append("]");//$NON-NLS-1$
		return false;
	}

	@Override
	public boolean visit(ArrayCreation node) {
		this.sbAbstractInformation.append("new ");//$NON-NLS-1$
		ArrayType at = node.getType();
		int dims = at.getDimensions();
		Type elementType = at.getElementType();
		elementType.accept(this);
		for (Iterator it = node.dimensions().iterator(); it.hasNext();) {
			this.sbAbstractInformation.append("[");//$NON-NLS-1$
			Expression e = (Expression) it.next();
			e.accept(this);
			this.sbAbstractInformation.append("]");//$NON-NLS-1$
			dims--;
		}
		// add empty "[]" for each extra array dimension
		for (int i = 0; i < dims; i++) {
			this.sbAbstractInformation.append("[]");//$NON-NLS-1$
		}
		if (node.getInitializer() != null) {
			node.getInitializer().accept(this);
		}
		return false;
	}

	@Override
	public boolean visit(ArrayInitializer node) {
		this.sbAbstractInformation.append("{");//$NON-NLS-1$
		for (Iterator it = node.expressions().iterator(); it.hasNext();) {
			Expression e = (Expression) it.next();
			e.accept(this);
			if (it.hasNext()) {
				this.sbAbstractInformation.append(",");//$NON-NLS-1$
			}
		}
		this.sbAbstractInformation.append("}");//$NON-NLS-1$
		return false;
	}

	@Override
	public boolean visit(ArrayType node) {
		if (node.getAST().apiLevel() < JLS8) {
			visitComponentType(node);
			this.sbAbstractInformation.append("[]");//$NON-NLS-1$
		} else {
			node.getElementType().accept(this);
			List dimensions = node.dimensions();
			int size = dimensions.size();
			for (int i = 0; i < size; i++) {
				Dimension aDimension = (Dimension) dimensions.get(i);
				aDimension.accept(this);
			}
		}
		return false;
	}

	@Override
	public boolean visit(AssertStatement node) {
		printIndent();
		this.sbAbstractInformation.append("assert ");//$NON-NLS-1$
		node.getExpression().accept(this);
		if (node.getMessage() != null) {
			this.sbAbstractInformation.append(" : ");//$NON-NLS-1$
			node.getMessage().accept(this);
		}
		this.sbAbstractInformation.append(";\n");//$NON-NLS-1$
		return false;
	}

	@Override
	public boolean visit(Assignment node) {
		node.getLeftHandSide().accept(this);
		this.sbAbstractInformation.append(node.getOperator().toString());
		node.getRightHandSide().accept(this);
		return false;
	}

	@Override
	public boolean visit(Block node) {
		this.sbAbstractInformation.append("{\n");//$NON-NLS-1$
		this.indent++;
		for (Iterator it = node.statements().iterator(); it.hasNext();) {
			Statement s = (Statement) it.next();
			s.accept(this);
		}
		this.indent--;
		printIndent();
		this.sbAbstractInformation.append("}\n");//$NON-NLS-1$
		return false;
	}

	@Override
	public boolean visit(BlockComment node) {
		printIndent();
		this.sbAbstractInformation.append("/* */");//$NON-NLS-1$
		return false;
	}

	@Override
	public boolean visit(BooleanLiteral node) {
		if (node.booleanValue() == true) {
			//			this.sbAbstractInformation.append("true");//$NON-NLS-1$
			this.sbAbstractInformation.append("#");
			listAbstractTypeQuestionMark.add("boolean");
			listPatialTypeRequiredParam.add("boolean");
			listFQNTypeRequiredParam.add("boolean");
		} else {
			//			this.sbAbstractInformation.append("false");//$NON-NLS-1$
			this.sbAbstractInformation.append("#");
			listAbstractTypeQuestionMark.add("boolean");
			listPatialTypeRequiredParam.add("boolean");
			listFQNTypeRequiredParam.add("boolean");
		}
		return false;
	}

	@Override
	public boolean visit(BreakStatement node) {
		printIndent();
		this.sbAbstractInformation.append("break");//$NON-NLS-1$
		if (node.getLabel() != null) {
			this.sbAbstractInformation.append(" ");//$NON-NLS-1$
			node.getLabel().accept(this);
		}
		this.sbAbstractInformation.append(";\n");//$NON-NLS-1$
		return false;
	}

	@Override
	public boolean visit(CastExpression node) {
		this.sbAbstractInformation.append("(");//$NON-NLS-1$
		addRequiredAPIForImport(node.getType().resolveBinding());
		node.getType().accept(this);
		this.sbAbstractInformation.append(")");//$NON-NLS-1$
		addRequiredAPIForImport(node.getExpression());
		node.getExpression().accept(this);
		return false;
	}

	@Override
	public boolean visit(CatchClause node) {
		this.sbAbstractInformation.append("catch (");//$NON-NLS-1$
		node.getException().accept(this);
		this.sbAbstractInformation.append(") ");//$NON-NLS-1$
		node.getBody().accept(this);
		return false;
	}

	@Override
	public boolean visit(CharacterLiteral node) {
		this.sbAbstractInformation.append("");
		listAbstractTypeQuestionMark.add("character");
		listPatialTypeRequiredParam.add("character");
		listFQNTypeRequiredParam.add("character");
		return false;
	}

	@Override
	public boolean visit(ClassInstanceCreation node) {
		if (node.getExpression() != null) {
			addRequiredAPIForImport(node.getExpression());
			node.getExpression().accept(this);
			this.sbAbstractInformation.append(".");//$NON-NLS-1$
		}
		this.sbAbstractInformation.append("new ");//$NON-NLS-1$
		if (node.getAST().apiLevel() == JLS2) {
			getName(node).accept(this);
		}
		if (node.getAST().apiLevel() >= JLS3) {
			if (!node.typeArguments().isEmpty()) {
				this.sbAbstractInformation.append("<");//$NON-NLS-1$
				for (Iterator it = node.typeArguments().iterator(); it
						.hasNext();) {
					Type t = (Type) it.next();
					t.accept(this);
					if (it.hasNext()) {
						this.sbAbstractInformation.append(",");//$NON-NLS-1$
					}
				}
				this.sbAbstractInformation.append(">");//$NON-NLS-1$
			}
			node.getType().accept(this);
		}
		this.sbAbstractInformation.append("(");//$NON-NLS-1$
		for (Iterator it = node.arguments().iterator(); it.hasNext();) {
			Expression e = (Expression) it.next();
			addRequiredAPIForImport(e);
			e.accept(this);
			if (it.hasNext()) {
				this.sbAbstractInformation.append(",");//$NON-NLS-1$
			}
		}
		this.sbAbstractInformation.append(")");//$NON-NLS-1$
		if (node.getAnonymousClassDeclaration() != null) {
			addRequiredAPIForImport(node.getAnonymousClassDeclaration()
					.resolveBinding());
			node.getAnonymousClassDeclaration().accept(this);
		}
		return false;
	}

	@Override
	public boolean visit(CompilationUnit node) {
		// if (node.getAST().apiLevel() >= JLS9) {
		// if (node.getModule() != null) {
		// node.getModule().accept(this);
		// }
		// }
		if (node.getPackage() != null) {
			node.getPackage().accept(this);
		}
		for (Iterator it = node.imports().iterator(); it.hasNext();) {
			ImportDeclaration d = (ImportDeclaration) it.next();
			d.accept(this);
		}
		for (Iterator it = node.types().iterator(); it.hasNext();) {
			AbstractTypeDeclaration d = (AbstractTypeDeclaration) it.next();
			d.accept(this);
		}
		return false;
	}

	@Override
	public boolean visit(ConditionalExpression node) {
		addRequiredAPIForImport(node.getExpression());
		node.getExpression().accept(this);
		this.sbAbstractInformation.append(" ? ");//$NON-NLS-1$
		addRequiredAPIForImport(node.getThenExpression());
		node.getThenExpression().accept(this);
		this.sbAbstractInformation.append(" : ");//$NON-NLS-1$
		addRequiredAPIForImport(node.getElseExpression());
		node.getElseExpression().accept(this);
		return false;
	}

	@Override
	public boolean visit(ConstructorInvocation node) {
		printIndent();
		if (node.getAST().apiLevel() >= JLS3) {
			if (!node.typeArguments().isEmpty()) {
				this.sbAbstractInformation.append("<");//$NON-NLS-1$
				for (Iterator it = node.typeArguments().iterator(); it
						.hasNext();) {
					Type t = (Type) it.next();
					t.accept(this);
					if (it.hasNext()) {
						this.sbAbstractInformation.append(",");//$NON-NLS-1$
					}
				}
				this.sbAbstractInformation.append(">");//$NON-NLS-1$
			}
		}
		this.sbAbstractInformation.append("this(");//$NON-NLS-1$
		for (Iterator it = node.arguments().iterator(); it.hasNext();) {
			Expression e = (Expression) it.next();
			addRequiredAPIForImport(e);
			e.accept(this);
			if (it.hasNext()) {
				this.sbAbstractInformation.append(",");//$NON-NLS-1$
			}
		}
		this.sbAbstractInformation.append(");\n");//$NON-NLS-1$
		return false;
	}

	@Override
	public boolean visit(ContinueStatement node) {
		printIndent();
		this.sbAbstractInformation.append("continue");//$NON-NLS-1$
		if (node.getLabel() != null) {
			this.sbAbstractInformation.append(" ");//$NON-NLS-1$
			node.getLabel().accept(this);
		}
		this.sbAbstractInformation.append(";\n");//$NON-NLS-1$
		return false;
	}

	@Override
	public boolean visit(CreationReference node) {
		node.getType().accept(this);
		visitReferenceTypeArguments(node.typeArguments());
		this.sbAbstractInformation.append("new");//$NON-NLS-1$
		return false;
	}

	@Override
	public boolean visit(Dimension node) {
		List annotations = node.annotations();
		if (annotations.size() > 0)
			this.sbAbstractInformation.append(' ');
		visitAnnotationsList(annotations);
		this.sbAbstractInformation.append("[]"); //$NON-NLS-1$
		return false;
	}

	@Override
	public boolean visit(DoStatement node) {
		printIndent();
		this.sbAbstractInformation.append("do ");//$NON-NLS-1$
		node.getBody().accept(this);
		this.sbAbstractInformation.append(" while (");//$NON-NLS-1$
		node.getExpression().accept(this);
		this.sbAbstractInformation.append(");\n");//$NON-NLS-1$
		return false;
	}

	@Override
	public boolean visit(EmptyStatement node) {
		printIndent();
		this.sbAbstractInformation.append(";\n");//$NON-NLS-1$
		return false;
	}

	@Override
	public boolean visit(EnhancedForStatement node) {
		printIndent();
		this.sbAbstractInformation.append("for (");//$NON-NLS-1$
		node.getParameter().accept(this);
		this.sbAbstractInformation.append(" : ");//$NON-NLS-1$
		node.getExpression().accept(this);
		this.sbAbstractInformation.append(") ");//$NON-NLS-1$
		node.getBody().accept(this);
		return false;
	}

	@Override
	public boolean visit(EnumConstantDeclaration node) {
		if (node.getJavadoc() != null) {
			node.getJavadoc().accept(this);
		}
		printIndent();
		printModifiers(node.modifiers());
		node.getName().accept(this);
		if (!node.arguments().isEmpty()) {
			this.sbAbstractInformation.append("(");//$NON-NLS-1$
			for (Iterator it = node.arguments().iterator(); it.hasNext();) {
				Expression e = (Expression) it.next();
				e.accept(this);
				if (it.hasNext()) {
					this.sbAbstractInformation.append(",");//$NON-NLS-1$
				}
			}
			this.sbAbstractInformation.append(")");//$NON-NLS-1$
		}
		if (node.getAnonymousClassDeclaration() != null) {
			node.getAnonymousClassDeclaration().accept(this);
		}
		return false;
	}

	@Override
	public boolean visit(EnumDeclaration node) {
		if (node.getJavadoc() != null) {
			node.getJavadoc().accept(this);
		}
		printIndent();
		printModifiers(node.modifiers());
		this.sbAbstractInformation.append("enum ");//$NON-NLS-1$
		node.getName().accept(this);
		this.sbAbstractInformation.append(" ");//$NON-NLS-1$
		if (!node.superInterfaceTypes().isEmpty()) {
			this.sbAbstractInformation.append("implements ");//$NON-NLS-1$
			for (Iterator it = node.superInterfaceTypes().iterator(); it
					.hasNext();) {
				Type t = (Type) it.next();
				t.accept(this);
				if (it.hasNext()) {
					this.sbAbstractInformation.append(", ");//$NON-NLS-1$
				}
			}
			this.sbAbstractInformation.append(" ");//$NON-NLS-1$
		}
		this.sbAbstractInformation.append("{");//$NON-NLS-1$
		for (Iterator it = node.enumConstants().iterator(); it.hasNext();) {
			EnumConstantDeclaration d = (EnumConstantDeclaration) it.next();
			d.accept(this);
			// enum constant declarations do not include punctuation
			if (it.hasNext()) {
				// enum constant declarations are separated by commas
				this.sbAbstractInformation.append(", ");//$NON-NLS-1$
			}
		}
		if (!node.bodyDeclarations().isEmpty()) {
			this.sbAbstractInformation.append("; ");//$NON-NLS-1$
			for (Iterator it = node.bodyDeclarations().iterator(); it.hasNext();) {
				BodyDeclaration d = (BodyDeclaration) it.next();
				d.accept(this);
				// other body declarations include trailing punctuation
			}
		}
		this.sbAbstractInformation.append("}\n");//$NON-NLS-1$
		return false;
	}

	// @Override
	// public boolean visit(ExportsDirective node) {
	//		return visit(node, "exports"); //$NON-NLS-1$
	// }

	@Override
	public boolean visit(ExpressionMethodReference node) {
		node.getExpression().accept(this);
		visitReferenceTypeArguments(node.typeArguments());
		node.getName().accept(this);
		return false;
	}

	@Override
	public boolean visit(ExpressionStatement node) {
		printIndent();
		node.getExpression().accept(this);
		this.sbAbstractInformation.append(";\n");//$NON-NLS-1$
		return false;
	}

	@Override
	public boolean visit(FieldAccess node) {
		if (node.getExpression().toString().equals("this")) {
			ITypeBinding iType = node.resolveTypeBinding();
			currentStrParentType = iType != null ? iType.getQualifiedName()
					: "Unknown";
			this.sbAbstractInformation.append("#");
			listAbstractTypeQuestionMark.add(currentStrParentType);
			setRequiredAPIsForMI.add(currentStrParentType);
		} else {
			addRequiredAPIForImport(node.getExpression());
			node.getExpression().accept(this);
			this.sbAbstractInformation.append(".");//$NON-NLS-1$
			node.getName().accept(this);
		}

		return false;
	}

	@Override
	public boolean visit(FieldDeclaration node) {
		if (node.getJavadoc() != null) {
			node.getJavadoc().accept(this);
		}
		printIndent();
		if (node.getAST().apiLevel() == JLS2) {
			printModifiers(node.getModifiers());
		}
		if (node.getAST().apiLevel() >= JLS3) {
			printModifiers(node.modifiers());
		}
		node.getType().accept(this);
		this.sbAbstractInformation.append(" ");//$NON-NLS-1$
		for (Iterator it = node.fragments().iterator(); it.hasNext();) {
			VariableDeclarationFragment f = (VariableDeclarationFragment) it
					.next();
			f.accept(this);
			if (it.hasNext()) {
				this.sbAbstractInformation.append(", ");//$NON-NLS-1$
			}
		}
		this.sbAbstractInformation.append(";\n");//$NON-NLS-1$
		return false;
	}

	@Override
	public boolean visit(ForStatement node) {
		printIndent();
		this.sbAbstractInformation.append("for (");//$NON-NLS-1$
		for (Iterator it = node.initializers().iterator(); it.hasNext();) {
			Expression e = (Expression) it.next();
			e.accept(this);
			if (it.hasNext())
				this.sbAbstractInformation.append(", ");//$NON-NLS-1$
		}
		this.sbAbstractInformation.append("; ");//$NON-NLS-1$
		if (node.getExpression() != null) {
			node.getExpression().accept(this);
		}
		this.sbAbstractInformation.append("; ");//$NON-NLS-1$
		for (Iterator it = node.updaters().iterator(); it.hasNext();) {
			Expression e = (Expression) it.next();
			e.accept(this);
			if (it.hasNext())
				this.sbAbstractInformation.append(", ");//$NON-NLS-1$
		}
		this.sbAbstractInformation.append(") ");//$NON-NLS-1$
		node.getBody().accept(this);
		return false;
	}

	@Override
	public boolean visit(IfStatement node) {
		printIndent();
		this.sbAbstractInformation.append("if (");//$NON-NLS-1$
		node.getExpression().accept(this);
		this.sbAbstractInformation.append(") ");//$NON-NLS-1$
		node.getThenStatement().accept(this);
		if (node.getElseStatement() != null) {
			this.sbAbstractInformation.append(" else ");//$NON-NLS-1$
			node.getElseStatement().accept(this);
		}
		return false;
	}

	@Override
	public boolean visit(ImportDeclaration node) {
		printIndent();
		this.sbAbstractInformation.append("import ");//$NON-NLS-1$
		if (node.getAST().apiLevel() >= JLS3) {
			if (node.isStatic()) {
				this.sbAbstractInformation.append("static ");//$NON-NLS-1$
			}
		}
		node.getName().accept(this);
		if (node.isOnDemand()) {
			this.sbAbstractInformation.append(".*");//$NON-NLS-1$
		}
		this.sbAbstractInformation.append(";\n");//$NON-NLS-1$
		return false;
	}

	@Override
	public boolean visit(InfixExpression node) {
		addRequiredAPIForImport(node.getLeftOperand());
		node.getLeftOperand().accept(this);
		this.sbAbstractInformation.append(' '); // for cases like x= i - -1; or
												// x= i++ + ++i;
		this.sbAbstractInformation.append(node.getOperator().toString());
		this.sbAbstractInformation.append(' ');
		addRequiredAPIForImport(node.getRightOperand());
		node.getRightOperand().accept(this);
		final List extendedOperands = node.extendedOperands();
		if (extendedOperands.size() != 0) {
			this.sbAbstractInformation.append(' ');
			for (Iterator it = extendedOperands.iterator(); it.hasNext();) {
				this.sbAbstractInformation
						.append(node.getOperator().toString()).append(' ');
				Expression e = (Expression) it.next();
				addRequiredAPIForImport(e);
				e.accept(this);
			}
		}
		return false;
	}

	@Override
	public boolean visit(Initializer node) {
		if (node.getJavadoc() != null) {
			node.getJavadoc().accept(this);
		}
		if (node.getAST().apiLevel() == JLS2) {
			printModifiers(node.getModifiers());
		}
		if (node.getAST().apiLevel() >= JLS3) {
			printModifiers(node.modifiers());
		}
		node.getBody().accept(this);
		return false;
	}

	@Override
	public boolean visit(InstanceofExpression node) {
		addRequiredAPIForImport(node.getLeftOperand());
		node.getLeftOperand().accept(this);
		this.sbAbstractInformation.append(" instanceof ");//$NON-NLS-1$
		addRequiredAPIForImport(node.getRightOperand().resolveBinding());
		node.getRightOperand().accept(this);
		return false;
	}

	@Override
	public boolean visit(IntersectionType node) {
		for (Iterator it = node.types().iterator(); it.hasNext();) {
			Type t = (Type) it.next();
			addRequiredAPIForImport(t.resolveBinding());
			t.accept(this);
			if (it.hasNext()) {
				this.sbAbstractInformation.append(" & "); //$NON-NLS-1$
			}
		}
		return false;
	}

	@Override
	public boolean visit(Javadoc node) {
		printIndent();
		this.sbAbstractInformation.append("/** ");//$NON-NLS-1$
		for (Iterator it = node.tags().iterator(); it.hasNext();) {
			ASTNode e = (ASTNode) it.next();
			e.accept(this);
		}
		this.sbAbstractInformation.append("\n */\n");//$NON-NLS-1$
		return false;
	}

	@Override
	public boolean visit(LabeledStatement node) {
		printIndent();
		node.getLabel().accept(this);
		this.sbAbstractInformation.append(": ");//$NON-NLS-1$
		node.getBody().accept(this);
		return false;
	}

	@Override
	public boolean visit(LambdaExpression node) {
		boolean hasParentheses = node.hasParentheses();
		if (hasParentheses)
			this.sbAbstractInformation.append('(');
		for (Iterator it = node.parameters().iterator(); it.hasNext();) {
			VariableDeclaration v = (VariableDeclaration) it.next();
			v.accept(this);
			if (it.hasNext()) {
				this.sbAbstractInformation.append(",");//$NON-NLS-1$
			}
		}
		if (hasParentheses)
			this.sbAbstractInformation.append(')');
		this.sbAbstractInformation.append(" -> "); //$NON-NLS-1$
		node.getBody().accept(this);
		return false;
	}

	@Override
	public boolean visit(LineComment node) {
		this.sbAbstractInformation.append("//\n");//$NON-NLS-1$
		return false;
	}

	@Override
	public boolean visit(MarkerAnnotation node) {
		this.sbAbstractInformation.append("@");//$NON-NLS-1$
		node.getTypeName().accept(this);
		return false;
	}

	@Override
	public boolean visit(MemberRef node) {
		if (node.getQualifier() != null) {
			node.getQualifier().accept(this);
		}
		this.sbAbstractInformation.append("#");//$NON-NLS-1$
		node.getName().accept(this);
		return false;
	}

	@Override
	public boolean visit(MemberValuePair node) {
		node.getName().accept(this);
		this.sbAbstractInformation.append("=");//$NON-NLS-1$
		node.getValue().accept(this);
		return false;
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		if (node.getJavadoc() != null) {
			node.getJavadoc().accept(this);
		}
		printIndent();
		if (node.getAST().apiLevel() == JLS2) {
			printModifiers(node.getModifiers());
		}
		if (node.getAST().apiLevel() >= JLS3) {
			printModifiers(node.modifiers());
			if (!node.typeParameters().isEmpty()) {
				this.sbAbstractInformation.append("<");//$NON-NLS-1$
				for (Iterator it = node.typeParameters().iterator(); it
						.hasNext();) {
					TypeParameter t = (TypeParameter) it.next();
					t.accept(this);
					if (it.hasNext()) {
						this.sbAbstractInformation.append(",");//$NON-NLS-1$
					}
				}
				this.sbAbstractInformation.append(">");//$NON-NLS-1$
			}
		}
		if (!node.isConstructor()) {
			if (node.getAST().apiLevel() == JLS2) {
				getReturnType(node).accept(this);
			} else {
				if (node.getReturnType2() != null) {
					node.getReturnType2().accept(this);
				} else {
					// methods really ought to have a return type
					this.sbAbstractInformation.append("void");//$NON-NLS-1$
				}
			}
			this.sbAbstractInformation.append(" ");//$NON-NLS-1$
		}
		node.getName().accept(this);
		this.sbAbstractInformation.append("(");//$NON-NLS-1$
		if (node.getAST().apiLevel() >= JLS8) {
			Type receiverType = node.getReceiverType();
			if (receiverType != null) {
				receiverType.accept(this);
				this.sbAbstractInformation.append(' ');
				SimpleName qualifier = node.getReceiverQualifier();
				if (qualifier != null) {
					qualifier.accept(this);
					this.sbAbstractInformation.append('.');
				}
				this.sbAbstractInformation.append("this"); //$NON-NLS-1$
				if (node.parameters().size() > 0) {
					this.sbAbstractInformation.append(',');
				}
			}
		}
		for (Iterator it = node.parameters().iterator(); it.hasNext();) {
			SingleVariableDeclaration v = (SingleVariableDeclaration) it.next();
			v.accept(this);
			if (it.hasNext()) {
				this.sbAbstractInformation.append(",");//$NON-NLS-1$
			}
		}
		this.sbAbstractInformation.append(")");//$NON-NLS-1$
		int size = node.getExtraDimensions();
		if (node.getAST().apiLevel() >= JLS8) {
			List dimensions = node.extraDimensions();
			for (int i = 0; i < size; i++) {
				visit((Dimension) dimensions.get(i));
			}
		} else {
			for (int i = 0; i < size; i++) {
				this.sbAbstractInformation.append("[]"); //$NON-NLS-1$
			}
		}
		if (node.getAST().apiLevel() < JLS8) {
			if (!thrownExceptions(node).isEmpty()) {
				this.sbAbstractInformation.append(" throws ");//$NON-NLS-1$
				for (Iterator it = thrownExceptions(node).iterator(); it
						.hasNext();) {
					Name n = (Name) it.next();
					n.accept(this);
					if (it.hasNext()) {
						this.sbAbstractInformation.append(", ");//$NON-NLS-1$
					}
				}
				this.sbAbstractInformation.append(" ");//$NON-NLS-1$
			}
		} else {
			if (!node.thrownExceptionTypes().isEmpty()) {
				this.sbAbstractInformation.append(" throws ");//$NON-NLS-1$
				for (Iterator it = node.thrownExceptionTypes().iterator(); it
						.hasNext();) {
					Type n = (Type) it.next();
					n.accept(this);
					if (it.hasNext()) {
						this.sbAbstractInformation.append(", ");//$NON-NLS-1$
					}
				}
				this.sbAbstractInformation.append(" ");//$NON-NLS-1$				
			}
		}
		if (node.getBody() == null) {
			this.sbAbstractInformation.append(";\n");//$NON-NLS-1$
		} else {
			node.getBody().accept(this);
		}
		return false;
	}

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
		if (arrBindArgs.length < i + 1) {
			return "";
		}
		String strType = arrBindArgs[i] != null ? arrBindArgs[i]
				.getQualifiedName() : "";
		return strType;
	}

	@Override
	public boolean visit(MethodInvocation node) {
		IMethodBinding iMethod = node.resolveMethodBinding();
		if (node.getExpression() == null) {
			this.sbAbstractInformation.append("#");
			if (iMethod != null) {
				currentStrParentType = iMethod.getReturnType() != null ? iMethod
						.getReturnType().getQualifiedName() : "Unknown";
				if (iMethod.getReturnType() != null) {
					listPatialTypeRequiredParam.add(getQualifiedName(iMethod
							.getReturnType()));
					listFQNTypeRequiredParam.add(getName(iMethod
							.getReturnType()));
				}

			} else {
				currentStrParentType = "Unknown";
			}
			listAbstractTypeQuestionMark.add(currentStrParentType);
			return false;
		} else {

			Expression exRetriever = node.getExpression();
			addRequiredAPIForImport(exRetriever);
			currentStrParentType = viewSelectedTypeReceiver(iMethod);
			// System.out.println("choose select type "+selectedType);
			String currentStrImmediateType = exRetriever.resolveTypeBinding() != null ? exRetriever
					.resolveTypeBinding().getQualifiedName()
					: currentStrParentType;
			// setRequiredAPIsForMI.add(currentStrImmediateType);
			node.getExpression().accept(this);
			this.sbAbstractInformation.append(".");//$NON-NLS-1$
		}
		if (node.getAST().apiLevel() >= JLS3) {
			if (!node.typeArguments().isEmpty()) {
				this.sbAbstractInformation.append("<");//$NON-NLS-1$
				for (Iterator it = node.typeArguments().iterator(); it
						.hasNext();) {
					Type t = (Type) it.next();
					t.accept(this);
					if (it.hasNext()) {
						this.sbAbstractInformation.append(",");//$NON-NLS-1$
					}
				}
				this.sbAbstractInformation.append(">");//$NON-NLS-1$
			}
		}
		node.getName().accept(this);
		this.sbAbstractInformation.append("(");//$NON-NLS-1$
		int indexParam = -1;
		for (Iterator it = node.arguments().iterator(); it.hasNext();) {
			indexParam++;
			Expression e = (Expression) it.next();
			addRequiredAPIForImport(e);
			currentStrParentType = viewSelectedTypeParam(iMethod, indexParam);
			String paramIType = e.resolveTypeBinding() != null ? e
					.resolveTypeBinding().getQualifiedName()
					: currentStrParentType;
			// setRequiredAPIsForMI.add(paramIType);
			e.accept(this);
			if (it.hasNext()) {
				this.sbAbstractInformation.append(",");//$NON-NLS-1$
			}
		}
		this.sbAbstractInformation.append(")");//$NON-NLS-1$
		return false;
	}

	@Override
	public boolean visit(MethodRef node) {
		if (node.getQualifier() != null) {
			node.getQualifier().accept(this);
		}
		this.sbAbstractInformation.append("#");//$NON-NLS-1$
		node.getName().accept(this);
		this.sbAbstractInformation.append("(");//$NON-NLS-1$
		for (Iterator it = node.parameters().iterator(); it.hasNext();) {
			MethodRefParameter e = (MethodRefParameter) it.next();
			e.accept(this);
			if (it.hasNext()) {
				this.sbAbstractInformation.append(",");//$NON-NLS-1$
			}
		}
		this.sbAbstractInformation.append(")");//$NON-NLS-1$
		return false;
	}

	@Override
	public boolean visit(MethodRefParameter node) {
		node.getType().accept(this);
		if (node.getAST().apiLevel() >= JLS3) {
			if (node.isVarargs()) {
				this.sbAbstractInformation.append("...");//$NON-NLS-1$
			}
		}
		if (node.getName() != null) {
			this.sbAbstractInformation.append(" ");//$NON-NLS-1$
			node.getName().accept(this);
		}
		return false;
	}

	@Override
	public boolean visit(Modifier node) {
		this.sbAbstractInformation.append(node.getKeyword().toString());
		return false;
	}

	// @Override
	// public boolean visit(ModuleDeclaration node) {
	// if (node.getJavadoc() != null) {
	// node.getJavadoc().accept(this);
	// }
	// printModifiers(node.annotations());
	// if (node.isOpen())
	//			this.sbAbstractInformation.append("open "); //$NON-NLS-1$
	//		this.sbAbstractInformation.append("module"); //$NON-NLS-1$
	//		this.sbAbstractInformation.append(" "); //$NON-NLS-1$
	// node.getName().accept(this);
	//		this.sbAbstractInformation.append(" {\n"); //$NON-NLS-1$
	// this.indent++;
	// for (ModuleDirective stmt :
	// (List<ModuleDirective>)node.moduleStatements()) {
	// stmt.accept(this);
	// }
	// this.indent--;
	//		this.sbAbstractInformation.append("}"); //$NON-NLS-1$
	// return false;
	// }

	// @Override
	/*
	 * @see ASTVisitor#visit(ModuleModifier)
	 * 
	 * @since 3.14
	 */
	// public boolean visit(ModuleModifier node) {
	// this.sbAbstractInformation.append(node.getKeyword().toString());
	// return false;
	// }
	//
	// private boolean visit(ModulePackageAccess node, String keyword) {
	// printIndent();
	// this.sbAbstractInformation.append(keyword);
	//		this.sbAbstractInformation.append(" ");//$NON-NLS-1$
	// node.getName().accept(this);
	//		printTypes(node.modules(), "to"); //$NON-NLS-1$
	//		this.sbAbstractInformation.append(";\n");//$NON-NLS-1$
	// return false;
	// }

	@Override
	public boolean visit(NameQualifiedType node) {
		node.getQualifier().accept(this);
		this.sbAbstractInformation.append('.');
		visitTypeAnnotations(node);
		node.getName().accept(this);
		return false;
	}

	@Override
	public boolean visit(NormalAnnotation node) {
		this.sbAbstractInformation.append("@");//$NON-NLS-1$
		node.getTypeName().accept(this);
		this.sbAbstractInformation.append("(");//$NON-NLS-1$
		for (Iterator it = node.values().iterator(); it.hasNext();) {
			MemberValuePair p = (MemberValuePair) it.next();
			p.accept(this);
			if (it.hasNext()) {
				this.sbAbstractInformation.append(",");//$NON-NLS-1$
			}
		}
		this.sbAbstractInformation.append(")");//$NON-NLS-1$
		return false;
	}

	@Override
	public boolean visit(NullLiteral node) {
		sbAbstractInformation.append("#");
		currentStrParentType = "java.lang.Object";
		listAbstractTypeQuestionMark.add(currentStrParentType);
		listPatialTypeRequiredParam.add("Object");
		listFQNTypeRequiredParam.add("java.lang.Object");

		//		this.sbAbstractInformation.append("null");//$NON-NLS-1$
		return false;
	}

	@Override
	public boolean visit(NumberLiteral node) {
		sbAbstractInformation.append("#");
		String strValue = node.getToken();
		if (strValue.endsWith("f")) {
			currentStrParentType = "float";
		} else if (strValue.contains(".")) {
			currentStrParentType = "double";
		} else {
			long val = 0;
			try {
				val = Long.parseLong(strValue);

			} catch (Exception ex) {

			}
			if (val <= 2147483647 && val >= -2147483648) {
				currentStrParentType = "int";
			} else {
				currentStrParentType = "long";
			}
		}

		listAbstractTypeQuestionMark.add(currentStrParentType);
		listPatialTypeRequiredParam.add(currentStrParentType);
		listFQNTypeRequiredParam.add(currentStrParentType);
		// this.sbAbstractInformation.append(node.getToken());
		return false;
	}

	// @Override
	// public boolean visit(OpensDirective node) {
	//		return visit(node, "opens"); //$NON-NLS-1$
	// }

	@Override
	public boolean visit(PackageDeclaration node) {
		if (node.getAST().apiLevel() >= JLS3) {
			if (node.getJavadoc() != null) {
				node.getJavadoc().accept(this);
			}
			for (Iterator it = node.annotations().iterator(); it.hasNext();) {
				Annotation p = (Annotation) it.next();
				p.accept(this);
				this.sbAbstractInformation.append(" ");//$NON-NLS-1$
			}
		}
		printIndent();
		this.sbAbstractInformation.append("package ");//$NON-NLS-1$
		node.getName().accept(this);
		this.sbAbstractInformation.append(";\n");//$NON-NLS-1$
		return false;
	}

	@Override
	public boolean visit(ParameterizedType node) {
		node.getType().accept(this);
		this.sbAbstractInformation.append("<");//$NON-NLS-1$
		for (Iterator it = node.typeArguments().iterator(); it.hasNext();) {
			Type t = (Type) it.next();
			t.accept(this);
			if (it.hasNext()) {
				this.sbAbstractInformation.append(",");//$NON-NLS-1$
			}
		}
		this.sbAbstractInformation.append(">");//$NON-NLS-1$
		return false;
	}

	@Override
	public boolean visit(ParenthesizedExpression node) {
		this.sbAbstractInformation.append("(");//$NON-NLS-1$
		addRequiredAPIForImport(node.getExpression());
		node.getExpression().accept(this);
		this.sbAbstractInformation.append(")");//$NON-NLS-1$
		return false;
	}

	@Override
	public boolean visit(PostfixExpression node) {
		addRequiredAPIForImport(node.getOperand());
		node.getOperand().accept(this);
		this.sbAbstractInformation.append(node.getOperator().toString());
		return false;
	}

	@Override
	public boolean visit(PrefixExpression node) {
		this.sbAbstractInformation.append(node.getOperator().toString());
		addRequiredAPIForImport(node.getOperand());
		node.getOperand().accept(this);
		return false;
	}

	@Override
	public boolean visit(PrimitiveType node) {
		visitTypeAnnotations(node);
		this.sbAbstractInformation.append(node.getPrimitiveTypeCode()
				.toString());
		return false;
	}

	// @Override
	// public boolean visit(ProvidesDirective node) {
	// printIndent();
	//		this.sbAbstractInformation.append("provides");//$NON-NLS-1$
	//		this.sbAbstractInformation.append(" ");//$NON-NLS-1$
	// node.getName().accept(this);
	//		printTypes(node.implementations(), "with"); //$NON-NLS-1$
	//		this.sbAbstractInformation.append(";\n");//$NON-NLS-1$
	// return false;
	// }

	@Override
	public boolean visit(QualifiedName node) {
		node.getQualifier().accept(this);
		this.sbAbstractInformation.append(".");//$NON-NLS-1$
		node.getName().accept(this);
		return false;
	}

	@Override
	public boolean visit(QualifiedType node) {
		node.getQualifier().accept(this);
		this.sbAbstractInformation.append(".");//$NON-NLS-1$
		visitTypeAnnotations(node);
		node.getName().accept(this);
		return false;
	}

	// @Override
	// public boolean visit(RequiresDirective node) {
	// printIndent();
	//		this.sbAbstractInformation.append("requires");//$NON-NLS-1$
	//		this.sbAbstractInformation.append(" ");//$NON-NLS-1$
	// printModifiers(node.modifiers());
	// node.getName().accept(this);
	//		this.sbAbstractInformation.append(";\n");//$NON-NLS-1$
	// return false;
	// }

	@Override
	public boolean visit(ReturnStatement node) {
		printIndent();
		this.sbAbstractInformation.append("return");//$NON-NLS-1$
		if (node.getExpression() != null) {
			this.sbAbstractInformation.append(" ");//$NON-NLS-1$
			node.getExpression().accept(this);
		}
		this.sbAbstractInformation.append(";\n");//$NON-NLS-1$
		return false;
	}

	public boolean checkVarInLocalField(IVariableBinding var) {
		String classKey = "";
		if (!var.isField()) {
			return false;
		}
		if (var.getDeclaringClass() != null) {
			classKey = var.getDeclaringClass().getQualifiedName();
		}
		// System.out.println("content "+classKey+" frfrf "+currentClassDeclaration);
		if (currentClassDeclaration.equals(classKey)) {
			return true;
		}
		return false;
	}

	public boolean checkVarInLocalMethod(IVariableBinding var) {
		String methodKey = "";
		if (var.getDeclaringMethod() != null) {
			methodKey = var.getDeclaringMethod().getKey();
		}
		if (currentMethodDeclaration.equals(methodKey)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean visit(SimpleName node) {
		// this.sbAbstractInformation.append(node.getIdentifier());
		// if(node.getIdentifier().equals("executor")){
		// System.out.println(node.resolveBinding().getClass());
		// System.out.println(checkVarInLocalField(((IVariableBinding)
		// node.resolveBinding())));
		// System.out.println(checkVarInLocalMethod(((IVariableBinding)
		// node.resolveBinding())));
		// Scanner sc=new Scanner(System.in);
		// sc.next();
		// }
		
		if (node.resolveBinding() instanceof IVariableBinding
				&& (checkVarInLocalField(((IVariableBinding) node
						.resolveBinding())) || checkVarInLocalMethod(((IVariableBinding) node
						.resolveBinding())))) {
			ITypeBinding iType = node.resolveTypeBinding();
			currentStrParentType = iType != null ? iType.getQualifiedName()
					: "Unknown";
			if (iType != null) {
				listPatialTypeRequiredParam.add(getName(iType));
				listFQNTypeRequiredParam.add(getQualifiedName(iType));
			}
			sbAbstractInformation.append("#");
			listAbstractTypeQuestionMark.add(currentStrParentType);
			setRequiredAPIsForMI.add(currentStrParentType);
		} else {
			ITypeBinding iType = node.resolveTypeBinding();
			String strTypeImport = iType != null ? iType.getQualifiedName()
					: "Unknown";
			if (!strTypeImport.equals("Unknown")) {
				setRequiredAPIsForMI.add(strTypeImport);
			}
			sbAbstractInformation.append(node.getIdentifier());
			processAndAddWordToRelatedWords(node.getIdentifier());
		}
		return false;
	}

	@Override
	public boolean visit(SimpleType node) {
		visitTypeAnnotations(node);
		node.getName().accept(this);
		return false;
	}

	@Override
	public boolean visit(SingleMemberAnnotation node) {
		this.sbAbstractInformation.append("@");//$NON-NLS-1$
		node.getTypeName().accept(this);
		this.sbAbstractInformation.append("(");//$NON-NLS-1$
		node.getValue().accept(this);
		this.sbAbstractInformation.append(")");//$NON-NLS-1$
		return false;
	}

	@Override
	public boolean visit(SingleVariableDeclaration node) {
		printIndent();
		if (node.getAST().apiLevel() == JLS2) {
			printModifiers(node.getModifiers());
		}
		if (node.getAST().apiLevel() >= JLS3) {
			printModifiers(node.modifiers());
		}
		node.getType().accept(this);
		if (node.getAST().apiLevel() >= JLS3) {
			if (node.isVarargs()) {
				if (node.getAST().apiLevel() >= JLS8) {
					List annotations = node.varargsAnnotations();
					if (annotations.size() > 0) {
						this.sbAbstractInformation.append(' ');
					}
					visitAnnotationsList(annotations);
				}
				this.sbAbstractInformation.append("...");//$NON-NLS-1$
			}
		}
		this.sbAbstractInformation.append(" ");//$NON-NLS-1$
		node.getName().accept(this);
		int size = node.getExtraDimensions();
		if (node.getAST().apiLevel() >= JLS8) {
			List dimensions = node.extraDimensions();
			for (int i = 0; i < size; i++) {
				visit((Dimension) dimensions.get(i));
			}
		} else {
			for (int i = 0; i < size; i++) {
				this.sbAbstractInformation.append("[]"); //$NON-NLS-1$
			}
		}
		if (node.getInitializer() != null) {
			this.sbAbstractInformation.append("=");//$NON-NLS-1$
			node.getInitializer().accept(this);
		}
		return false;
	}

	@Override
	public boolean visit(StringLiteral node) {
		sbAbstractInformation.append("#");
		currentStrParentType = "java.lang.String";
		listAbstractTypeQuestionMark.add(currentStrParentType);
		listPatialTypeRequiredParam.add("String");
		listFQNTypeRequiredParam.add("java.lang.String");

		// this.sbAbstractInformation.append(node.getEscapedValue());
		return false;
	}

	@Override
	public boolean visit(SuperConstructorInvocation node) {
		printIndent();
		if (node.getExpression() != null) {
			node.getExpression().accept(this);
			this.sbAbstractInformation.append(".");//$NON-NLS-1$
		}
		if (node.getAST().apiLevel() >= JLS3) {
			if (!node.typeArguments().isEmpty()) {
				this.sbAbstractInformation.append("<");//$NON-NLS-1$
				for (Iterator it = node.typeArguments().iterator(); it
						.hasNext();) {
					Type t = (Type) it.next();
					t.accept(this);
					if (it.hasNext()) {
						this.sbAbstractInformation.append(",");//$NON-NLS-1$
					}
				}
				this.sbAbstractInformation.append(">");//$NON-NLS-1$
			}
		}
		IMethodBinding b = node.resolveConstructorBinding();
		ITypeBinding tb = null;
		if (b != null && b.getDeclaringClass() != null)
			tb = b.getDeclaringClass().getTypeDeclaration();
		currentStrParentType = "Unknown";
		if (tb != null) {
			currentStrParentType = getQualifiedName(tb);
			listPatialTypeRequiredParam.add(getName(tb));
			listFQNTypeRequiredParam.add(getQualifiedName(tb));
		}
		this.sbAbstractInformation.append("#(");//$NON-NLS-1$
		listAbstractTypeQuestionMark.add(currentStrParentType);
		setRequiredAPIsForMI.add(currentStrParentType);
		for (Iterator it = node.arguments().iterator(); it.hasNext();) {
			Expression e = (Expression) it.next();
			e.accept(this);
			if (it.hasNext()) {
				this.sbAbstractInformation.append(",");//$NON-NLS-1$
			}
		}
		this.sbAbstractInformation.append(");\n");//$NON-NLS-1$
		return false;
	}

	@Override
	public boolean visit(SuperFieldAccess node) {
		if (node.getQualifier() != null) {
			node.getQualifier().accept(this);
			this.sbAbstractInformation.append(".");//$NON-NLS-1$
		}

		ITypeBinding tb = node.resolveTypeBinding();
		currentStrParentType = "Unknown";
		if (tb != null) {
			currentStrParentType = getQualifiedName(tb);
			listPatialTypeRequiredParam.add(getName(tb));
			listFQNTypeRequiredParam.add(getQualifiedName(tb));
		}
		this.sbAbstractInformation.append("#.");//$NON-NLS-1$
		listAbstractTypeQuestionMark.add(currentStrParentType);
		setRequiredAPIsForMI.add(currentStrParentType);

		node.getName().accept(this);
		return false;
	}

	@Override
	public boolean visit(SuperMethodInvocation node) {
		IMethodBinding iMethod = node.resolveMethodBinding();
		if (node.getQualifier() != null) {
			node.getQualifier().accept(this);
			this.sbAbstractInformation.append(".");//$NON-NLS-1$
		}
		IMethodBinding b = node.resolveMethodBinding();
		ITypeBinding tb = null;
		if (b != null && b.getDeclaringClass() != null)
			tb = b.getDeclaringClass().getTypeDeclaration();
		currentStrParentType = "Unknown";
		if (tb != null) {
			currentStrParentType = getQualifiedName(tb);
			listPatialTypeRequiredParam.add(getName(tb));
			listFQNTypeRequiredParam.add(getQualifiedName(tb));
		}

		//		this.sbAbstractInformation.append("super.");//$NON-NLS-1$
		this.sbAbstractInformation.append("#.");
		listAbstractTypeQuestionMark.add(currentStrParentType);

		if (node.getAST().apiLevel() >= JLS3) {
			if (!node.typeArguments().isEmpty()) {
				this.sbAbstractInformation.append("<");//$NON-NLS-1$
				for (Iterator it = node.typeArguments().iterator(); it
						.hasNext();) {
					Type t = (Type) it.next();
					t.accept(this);
					if (it.hasNext()) {
						this.sbAbstractInformation.append(",");//$NON-NLS-1$
					}
				}
				this.sbAbstractInformation.append(">");//$NON-NLS-1$
			}
		}
		node.getName().accept(this);
		this.sbAbstractInformation.append("(");//$NON-NLS-1$

		int indexParam = -1;
		for (Iterator it = node.arguments().iterator(); it.hasNext();) {
			indexParam++;
			Expression e = (Expression) it.next();
			// currentStrParentType = viewSelectedTypeParam(iMethod,
			// indexParam);
			// String paramIType =
			// e.resolveTypeBinding()!=null?e.resolveTypeBinding().getQualifiedName():currentStrParentType;
			// setRequiredAPIsForMI.add(paramIType);
			addRequiredAPIForImport(e);
			e.accept(this);
			if (it.hasNext()) {
				this.sbAbstractInformation.append(",");//$NON-NLS-1$
			}
		}
		this.sbAbstractInformation.append(")");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(SuperMethodReference)
	 * 
	 * @since 3.10
	 */
	@Override
	public boolean visit(SuperMethodReference node) {
		if (node.getQualifier() != null) {
			node.getQualifier().accept(this);
			this.sbAbstractInformation.append('.');
		}
		this.sbAbstractInformation.append("super");//$NON-NLS-1$
		visitReferenceTypeArguments(node.typeArguments());
		node.getName().accept(this);
		return false;
	}

	@Override
	public boolean visit(SwitchCase node) {
		if (node.isDefault()) {
			this.sbAbstractInformation.append("default :\n");//$NON-NLS-1$
		} else {
			this.sbAbstractInformation.append("case ");//$NON-NLS-1$
			node.getExpression().accept(this);
			this.sbAbstractInformation.append(":\n");//$NON-NLS-1$
		}
		this.indent++; // decremented in visit(SwitchStatement)
		return false;
	}

	@Override
	public boolean visit(SwitchStatement node) {
		this.sbAbstractInformation.append("switch (");//$NON-NLS-1$
		node.getExpression().accept(this);
		this.sbAbstractInformation.append(") ");//$NON-NLS-1$
		this.sbAbstractInformation.append("{\n");//$NON-NLS-1$
		this.indent++;
		for (Iterator it = node.statements().iterator(); it.hasNext();) {
			Statement s = (Statement) it.next();
			s.accept(this);
			this.indent--; // incremented in visit(SwitchCase)
		}
		this.indent--;
		printIndent();
		this.sbAbstractInformation.append("}\n");//$NON-NLS-1$
		return false;
	}

	@Override
	public boolean visit(SynchronizedStatement node) {
		this.sbAbstractInformation.append("synchronized (");//$NON-NLS-1$
		node.getExpression().accept(this);
		this.sbAbstractInformation.append(") ");//$NON-NLS-1$
		node.getBody().accept(this);
		return false;
	}

	@Override
	public boolean visit(TagElement node) {
		if (node.isNested()) {
			// nested tags are always enclosed in braces
			this.sbAbstractInformation.append("{");//$NON-NLS-1$
		} else {
			// top-level tags always begin on a new line
			this.sbAbstractInformation.append("\n * ");//$NON-NLS-1$
		}
		boolean previousRequiresWhiteSpace = false;
		if (node.getTagName() != null) {
			this.sbAbstractInformation.append(node.getTagName());
			previousRequiresWhiteSpace = true;
		}
		boolean previousRequiresNewLine = false;
		for (Iterator it = node.fragments().iterator(); it.hasNext();) {
			ASTNode e = (ASTNode) it.next();
			// Name, MemberRef, MethodRef, and nested TagElement do not include
			// white space.
			// TextElements don't always include whitespace, see
			// <https://bugs.eclipse.org/206518>.
			boolean currentIncludesWhiteSpace = false;
			if (e instanceof TextElement) {
				String text = ((TextElement) e).getText();
				if (text.length() > 0
						&& ScannerHelper.isWhitespace(text.charAt(0))) {
					currentIncludesWhiteSpace = true; // workaround for
														// https://bugs.eclipse.org/403735
				}
			}
			if (previousRequiresNewLine && currentIncludesWhiteSpace) {
				this.sbAbstractInformation.append("\n * ");//$NON-NLS-1$
			}
			previousRequiresNewLine = currentIncludesWhiteSpace;
			// add space if required to separate
			if (previousRequiresWhiteSpace && !currentIncludesWhiteSpace) {
				this.sbAbstractInformation.append(" "); //$NON-NLS-1$
			}
			e.accept(this);
			previousRequiresWhiteSpace = !currentIncludesWhiteSpace
					&& !(e instanceof TagElement);
		}
		if (node.isNested()) {
			this.sbAbstractInformation.append("}");//$NON-NLS-1$
		}
		return false;
	}

	@Override
	public boolean visit(TextElement node) {
		this.sbAbstractInformation.append(node.getText());
		return false;
	}

	@Override
	public boolean visit(ThisExpression node) {
		if (node.getQualifier() != null) {
			node.getQualifier().accept(this);
			this.sbAbstractInformation.append(".");//$NON-NLS-1$
		}
		this.sbAbstractInformation.append("#");//$NON-NLS-1$
		ITypeBinding itype = node.resolveTypeBinding();
		currentStrParentType = itype != null ? itype.getQualifiedName()
				: "Unknown";
		this.listAbstractTypeQuestionMark.add(currentStrParentType);
		if(itype!=null){
			listPatialTypeRequiredParam.add(getName(itype));
			listFQNTypeRequiredParam.add(getQualifiedName(itype));
		}
		addRequiredAPIForImport(itype);
		return false;
	}

	@Override
	public boolean visit(ThrowStatement node) {
		printIndent();
		this.sbAbstractInformation.append("throw ");//$NON-NLS-1$
		node.getExpression().accept(this);
		this.sbAbstractInformation.append(";\n");//$NON-NLS-1$
		return false;
	}

	@Override
	public boolean visit(TryStatement node) {
		printIndent();
		this.sbAbstractInformation.append("try ");//$NON-NLS-1$
		if (node.getAST().apiLevel() >= JLS4) {
			List resources = node.resources();
			if (!resources.isEmpty()) {
				this.sbAbstractInformation.append('(');
				for (Iterator it = resources.iterator(); it.hasNext();) {
					Expression variable = (Expression) it.next();
					variable.accept(this);
					if (it.hasNext()) {
						this.sbAbstractInformation.append(';');
					}
				}
				this.sbAbstractInformation.append(')');
			}
		}
		node.getBody().accept(this);
		this.sbAbstractInformation.append(" ");//$NON-NLS-1$
		for (Iterator it = node.catchClauses().iterator(); it.hasNext();) {
			CatchClause cc = (CatchClause) it.next();
			cc.accept(this);
		}
		if (node.getFinally() != null) {
			this.sbAbstractInformation.append(" finally ");//$NON-NLS-1$
			node.getFinally().accept(this);
		}
		return false;
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		if (node.getJavadoc() != null) {
			node.getJavadoc().accept(this);
		}
		if (node.getAST().apiLevel() == JLS2) {
			printModifiers(node.getModifiers());
		}
		if (node.getAST().apiLevel() >= JLS3) {
			printModifiers(node.modifiers());
		}
		this.sbAbstractInformation
				.append(node.isInterface() ? "interface " : "class ");//$NON-NLS-2$//$NON-NLS-1$
		node.getName().accept(this);
		if (node.getAST().apiLevel() >= JLS3) {
			if (!node.typeParameters().isEmpty()) {
				this.sbAbstractInformation.append("<");//$NON-NLS-1$
				for (Iterator it = node.typeParameters().iterator(); it
						.hasNext();) {
					TypeParameter t = (TypeParameter) it.next();
					t.accept(this);
					if (it.hasNext()) {
						this.sbAbstractInformation.append(",");//$NON-NLS-1$
					}
				}
				this.sbAbstractInformation.append(">");//$NON-NLS-1$
			}
		}
		this.sbAbstractInformation.append(" ");//$NON-NLS-1$
		if (node.getAST().apiLevel() == JLS2) {
			if (getSuperclass(node) != null) {
				this.sbAbstractInformation.append("extends ");//$NON-NLS-1$
				getSuperclass(node).accept(this);
				this.sbAbstractInformation.append(" ");//$NON-NLS-1$
			}
			if (!superInterfaces(node).isEmpty()) {
				this.sbAbstractInformation
						.append(node.isInterface() ? "extends " : "implements ");//$NON-NLS-2$//$NON-NLS-1$
				for (Iterator it = superInterfaces(node).iterator(); it
						.hasNext();) {
					Name n = (Name) it.next();
					n.accept(this);
					if (it.hasNext()) {
						this.sbAbstractInformation.append(", ");//$NON-NLS-1$
					}
				}
				this.sbAbstractInformation.append(" ");//$NON-NLS-1$
			}
		}
		if (node.getAST().apiLevel() >= JLS3) {
			if (node.getSuperclassType() != null) {
				this.sbAbstractInformation.append("extends ");//$NON-NLS-1$
				node.getSuperclassType().accept(this);
				this.sbAbstractInformation.append(" ");//$NON-NLS-1$
			}
			if (!node.superInterfaceTypes().isEmpty()) {
				this.sbAbstractInformation
						.append(node.isInterface() ? "extends " : "implements ");//$NON-NLS-2$//$NON-NLS-1$
				for (Iterator it = node.superInterfaceTypes().iterator(); it
						.hasNext();) {
					Type t = (Type) it.next();
					t.accept(this);
					if (it.hasNext()) {
						this.sbAbstractInformation.append(", ");//$NON-NLS-1$
					}
				}
				this.sbAbstractInformation.append(" ");//$NON-NLS-1$
			}
		}
		this.sbAbstractInformation.append("{\n");//$NON-NLS-1$
		this.indent++;
		for (Iterator it = node.bodyDeclarations().iterator(); it.hasNext();) {
			BodyDeclaration d = (BodyDeclaration) it.next();
			d.accept(this);
		}
		this.indent--;
		printIndent();
		this.sbAbstractInformation.append("}\n");//$NON-NLS-1$
		return false;
	}

	@Override
	public boolean visit(TypeDeclarationStatement node) {
		if (node.getAST().apiLevel() == JLS2) {
			getTypeDeclaration(node).accept(this);
		}
		if (node.getAST().apiLevel() >= JLS3) {
			node.getDeclaration().accept(this);
		}
		return false;
	}

	@Override
	public boolean visit(TypeLiteral node) {
		// sbAbstractInformation.append("?");
		// listAbstractTypeQuestionMark.add(currentStrParentType);
		addRequiredAPIForImport(node.getType().resolveBinding());

		node.getType().accept(this);
		this.sbAbstractInformation.append(".class");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(TypeMethodReference)
	 * 
	 * @since 3.10
	 */
	@Override
	public boolean visit(TypeMethodReference node) {
		node.getType().accept(this);
		visitReferenceTypeArguments(node.typeArguments());
		node.getName().accept(this);
		return false;
	}

	@Override
	public boolean visit(TypeParameter node) {
		if (node.getAST().apiLevel() >= JLS8) {
			printModifiers(node.modifiers());
		}
		node.getName().accept(this);
		if (!node.typeBounds().isEmpty()) {
			this.sbAbstractInformation.append(" extends ");//$NON-NLS-1$
			for (Iterator it = node.typeBounds().iterator(); it.hasNext();) {
				Type t = (Type) it.next();
				t.accept(this);
				if (it.hasNext()) {
					this.sbAbstractInformation.append(" & ");//$NON-NLS-1$
				}
			}
		}
		return false;
	}

	@Override
	public boolean visit(UnionType node) {
		for (Iterator it = node.types().iterator(); it.hasNext();) {
			Type t = (Type) it.next();
			t.accept(this);
			if (it.hasNext()) {
				this.sbAbstractInformation.append('|');
			}
		}
		return false;
	}

	// @Override
	// public boolean visit(UsesDirective node) {
	// printIndent();
	//		this.sbAbstractInformation.append("uses");//$NON-NLS-1$
	//		this.sbAbstractInformation.append(" ");//$NON-NLS-1$
	// node.getName().accept(this);
	//		this.sbAbstractInformation.append(";\n");//$NON-NLS-1$
	// return false;
	// }

	@Override
	public boolean visit(VariableDeclarationExpression node) {
		if (node.getAST().apiLevel() == JLS2) {
			printModifiers(node.getModifiers());
		}
		if (node.getAST().apiLevel() >= JLS3) {
			printModifiers(node.modifiers());
		}
		node.getType().accept(this);
		this.sbAbstractInformation.append(" ");//$NON-NLS-1$
		for (Iterator it = node.fragments().iterator(); it.hasNext();) {
			VariableDeclarationFragment f = (VariableDeclarationFragment) it
					.next();
			f.accept(this);
			if (it.hasNext()) {
				this.sbAbstractInformation.append(", ");//$NON-NLS-1$
			}
		}
		return false;
	}

	@Override
	public boolean visit(VariableDeclarationFragment node) {
		node.getName().accept(this);
		int size = node.getExtraDimensions();
		if (node.getAST().apiLevel() >= JLS8) {
			List dimensions = node.extraDimensions();
			for (int i = 0; i < size; i++) {
				visit((Dimension) dimensions.get(i));
			}
		} else {
			for (int i = 0; i < size; i++) {
				this.sbAbstractInformation.append("[]");//$NON-NLS-1$
			}
		}
		if (node.getInitializer() != null) {
			this.sbAbstractInformation.append("=");//$NON-NLS-1$
			node.getInitializer().accept(this);
		}
		return false;
	}

	@Override
	public boolean visit(VariableDeclarationStatement node) {
		printIndent();
		if (node.getAST().apiLevel() == JLS2) {
			printModifiers(node.getModifiers());
		}
		if (node.getAST().apiLevel() >= JLS3) {
			printModifiers(node.modifiers());
		}
		node.getType().accept(this);
		this.sbAbstractInformation.append(" ");//$NON-NLS-1$
		for (Iterator it = node.fragments().iterator(); it.hasNext();) {
			VariableDeclarationFragment f = (VariableDeclarationFragment) it
					.next();
			f.accept(this);
			if (it.hasNext()) {
				this.sbAbstractInformation.append(", ");//$NON-NLS-1$
			}
		}
		this.sbAbstractInformation.append(";\n");//$NON-NLS-1$
		return false;
	}

	@Override
	public boolean visit(WhileStatement node) {
		printIndent();
		this.sbAbstractInformation.append("while (");//$NON-NLS-1$
		node.getExpression().accept(this);
		this.sbAbstractInformation.append(") ");//$NON-NLS-1$
		node.getBody().accept(this);
		return false;
	}

	@Override
	public boolean visit(WildcardType node) {
		visitTypeAnnotations(node);
		this.sbAbstractInformation.append("?");//$NON-NLS-1$
		Type bound = node.getBound();
		if (bound != null) {
			if (node.isUpperBound()) {
				this.sbAbstractInformation.append(" extends ");//$NON-NLS-1$
			} else {
				this.sbAbstractInformation.append(" super ");//$NON-NLS-1$
			}
			bound.accept(this);
		}
		return false;
	}

	/**
	 * @deprecated
	 */
	private void visitComponentType(ArrayType node) {
		node.getComponentType().accept(this);
	}

	private String getQualifiedName(ITypeBinding tb) {
		if (tb.isArray())
			return getQualifiedName(tb.getComponentType().getTypeDeclaration())
					+ getDimensions(tb.getDimensions());
		return tb.getQualifiedName();
	}

	private String getName(ITypeBinding tb) {
		if (tb.isArray())
			return getName(tb.getComponentType().getTypeDeclaration())
					+ getDimensions(tb.getDimensions());
		return tb.getName();
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
}