package ppa;

import java.io.File;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Name;

import ca.mcgill.cs.swevo.ppa.PPAOptions;
import ca.mcgill.cs.swevo.ppa.ui.PPAUtil;
import utils.FileIO;

public class RunTestPPA {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 File srcFile = new File("/Users/hungphan/git/NLPLInference/src/ppa/CheckSourceCodeByPPAVisitor.java");
		    
		    // Obtaining a compilation unit using the default options.
		 String src=FileIO.readStringFromFile(srcFile.getAbsolutePath());
		 
//		 PPAUtil.cleanUpAll();
		   CompilationUnit cu = PPAUtil.getCU(src, new PPAOptions());
		   System.out.println(cu==null);
//		     Obtain a name object and the corresponding binding
//		    Name nameNode = null;
//		    IBinding binding = nameNode.resolveBinding();
//		    ITypeBinding typeBinding = nameNode.resolveTypeBinding();
	}

}
