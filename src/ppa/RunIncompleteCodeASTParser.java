package ppa;

import java.io.File;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

import utils.FileIO;

public class RunIncompleteCodeASTParser {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setResolveBindings(true);
		parser.setKind(ASTParser.K_STATEMENTS);
		parser.setBindingsRecovery(true);
		parser.setStatementsRecovery(true);
		
		Map options = JavaCore.getOptions();
		parser.setCompilerOptions(options);
		parser.setUnitName("test");

		File srcFile = new File("/Users/hungphan/git/GrouminerProject/TestCodeSnippet.txt");

		// Obtaining a compilation unit using the default options.
		String src = FileIO.readStringFromFile(srcFile.getAbsolutePath());
//		String src = "System.out.println(\"test\");";
		String[] sources = {};
		String[] classpath = { "/Library/Java/JavaVirtualMachines/jdk1.8.0_141.jdk/Contents/Home/jre/lib/rt.jar" };

		parser.setEnvironment(classpath, sources, new String[] {}, true);
		parser.setSource(src.toCharArray());
		final Block block = (Block) parser.createAST(null);
//		System.out.println("aaaa "+block.toString());
		block.accept(new ASTVisitor() {
			
			public boolean visit(MethodDeclaration node) {
				System.out.println(node);
				return false;
			}
			
			public boolean visit(MethodInvocation node) {
				System.out.println(node);
				return false;
			}
		});
	}

}
