package invocations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Stack;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import entities.LocalEntity;
import invocations.MethodSourceTokenGenerator;
import parser.ClassPathUtil;
import parser.ClassPathUtil.PomFile;
import utils.FileIO;
import utils.FileUtil;
import utils.StanfordLemmatizer;

public class MethodSourceTokenGenerator {

	private static final boolean PARSE_INDIVIDUAL_SRC = false, SCAN_FILES_FRIST = false;

	private String inPath, outPath;
	private boolean testing = false;
	private PrintStream stLocations, stSourceSequences, stLog;
	private HashSet<String> badFiles = new HashSet<>();
	private String fopInvocationObject, fopQueryObject;
	private String idenHashPath;
	private LinkedHashMap<String, String> mapIDAndIden;
	private LinkedHashMap<String, Integer> mapIDAppear;
	private LinkedHashMap<String, String> mapIdenAndID;
	private String[] arrPrefix;
	private StanfordLemmatizer lemm;

	public MethodSourceTokenGenerator(String inPath, String[] arrPrefix, StanfordLemmatizer lemm) {
		this.inPath = inPath;
		this.arrPrefix = arrPrefix;
		this.lemm = lemm;
	}

	public MethodSourceTokenGenerator(String inPath, String[] arrPrefix, boolean testing, StanfordLemmatizer lemm) {
		this(inPath, arrPrefix, lemm);
		this.testing = testing;

	}

	public int generateSequences(String outPath) {
		return generateSequences(true, null, outPath);
	}

	public LinkedHashMap<String, String> getMapIDAndIden() {
		return mapIDAndIden;
	}

	public void setMapIDAndIden(LinkedHashMap<String, String> mapIDAndIden) {
		this.mapIDAndIden = mapIDAndIden;
	}

	public LinkedHashMap<String, String> getMapIdenAndID() {
		return mapIdenAndID;
	}

	public void setMapIdenAndID(LinkedHashMap<String, String> mapIdenAndID) {
		this.mapIdenAndID = mapIdenAndID;
	}

	public int generateSequences(final boolean keepUnresolvables, final String lib, final String outPath) {
		this.outPath = outPath;
		String[] jarPaths = getJarPaths();
		ArrayList<String> rootPaths = getRootPaths();

		new File(outPath).mkdirs();
		String hashIdenPath = outPath + "/hash/";
		this.idenHashPath = hashIdenPath;
		new File(hashIdenPath).mkdirs();

		String fopQueryPath = outPath + "/query/";
		this.fopQueryObject = fopQueryPath;
		new File(fopQueryPath).mkdirs();

		File fDictMethod = new File(fopQueryPath + File.separator + "dictionaryQuery.txt");
		if (!fDictMethod.exists()) {
			FileIO.writeStringToFile("", fDictMethod.getAbsolutePath());
		}

		try {
			stLocations = new PrintStream(new FileOutputStream(outPath + "/locations.txt"));
			stSourceSequences = new PrintStream(new FileOutputStream(outPath + "/source.txt"));
//			stTargetSequences = new PrintStream(new FileOutputStream(outPath + "/target.txt"));
			stLog = new PrintStream(new FileOutputStream(outPath + "/log.txt"));
		} catch (FileNotFoundException e) {
			if (testing)
				System.err.println(e.getMessage());
			return 0;
		}
		int numOfSequences = 0;
		for (String rootPath : rootPaths) {
			String[] sourcePaths = getSourcePaths(rootPath, new String[] { ".java" });

			@SuppressWarnings("rawtypes")
			Map options = JavaCore.getOptions();
			options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
			options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_8);
			options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
			ASTParser parser = ASTParser.newParser(AST.JLS8);
			parser.setCompilerOptions(options);
			parser.setEnvironment(jarPaths, new String[] {}, new String[] {}, true);
			parser.setResolveBindings(true);
			parser.setBindingsRecovery(true);

			SourceTokenASTRequestor r = new SourceTokenASTRequestor(keepUnresolvables, lib, hashIdenPath, fopQueryPath,
					lemm);
			mapIDAndIden = new LinkedHashMap<String, String>();
			mapIdenAndID = new LinkedHashMap<String, String>();
			mapIDAppear = new LinkedHashMap<String, Integer>();
			r.setMapIDAndIden(mapIDAndIden);
			r.setMapIdenAndID(mapIdenAndID);
			r.setMapIDAppear(mapIDAppear);
			r.setArrLibNames(this.arrPrefix);
			try {
				parser.createASTs(sourcePaths, null, new String[0], r, null);
			} catch (Throwable t) {
				t.printStackTrace(stLog);
				if (testing) {
					System.err.println(t.getMessage());
					t.printStackTrace();
				}
			}
			saveMapToFile(mapIDAndIden, hashIdenPath + "/mapIDAndIden.txt");
			saveMapToFile(mapIdenAndID, hashIdenPath + "/mapIdenAndID.txt");
			saveMapIntToFile(mapIDAppear, hashIdenPath + "/mapIDAppear.txt");

			numOfSequences += r.numOfSequences;
		}
		return numOfSequences;
	}

	public void saveMapToFile(LinkedHashMap<String, String> map, String fpFile) {
		StringBuilder sb = new StringBuilder();
		for (String item : map.keySet()) {
			sb.append(item + "\t" + map.get(item) + "\n");
		}
		FileIO.writeStringToFile(sb.toString(), fpFile);
	}

	public void saveMapIntToFile(LinkedHashMap<String, Integer> map, String fpFile) {
		StringBuilder sb = new StringBuilder();
		for (String item : map.keySet()) {
			sb.append(item + "\t" + map.get(item) + "\n");
		}
		FileIO.writeStringToFile(sb.toString(), fpFile);
	}

	private class SourceTokenASTRequestor extends FileASTRequestor {
		int numOfSequences = 0;
		private boolean keepUnresolvables;
		private String lib;
		private String idenPath;
		private String fopQueryPath;
		private LinkedHashMap<String, String> mapIDAndIden;
		private LinkedHashMap<String, String> mapIdenAndID;
		private LinkedHashMap<String, Integer> mapIDAppear;
		private String[] arrLibNames;
		private StanfordLemmatizer lemm;

		public String[] getArrLibNames() {
			return arrLibNames;
		}

		public void setArrLibNames(String[] arrLibNames) {
			this.arrLibNames = arrLibNames;
		}

		public LinkedHashMap<String, Integer> getMapIDAppear() {
			return mapIDAppear;
		}

		public void setMapIDAppear(LinkedHashMap<String, Integer> mapIDAppear) {
			this.mapIDAppear = mapIDAppear;
		}

		public LinkedHashMap<String, String> getMapIDAndIden() {
			return mapIDAndIden;
		}

		public void setMapIDAndIden(LinkedHashMap<String, String> mapIDAndIden) {
			this.mapIDAndIden = mapIDAndIden;
		}

		public LinkedHashMap<String, String> getMapIdenAndID() {
			return mapIdenAndID;
		}

		public void setMapIdenAndID(LinkedHashMap<String, String> mapIdenAndID) {
			this.mapIdenAndID = mapIdenAndID;
		}

		public SourceTokenASTRequestor(boolean keepUnresolvables, String lib, String idenPath, String fopQueryPath,
				StanfordLemmatizer lemm) {
			this.keepUnresolvables = keepUnresolvables;
			this.lib = lib;
			this.idenPath = idenPath;
			this.fopQueryPath = fopQueryPath;
			this.lemm = lemm;
		}

		@Override
		public void acceptAST(String sourceFilePath, CompilationUnit ast) {
			if (ast.getPackage() == null)
				return;
			if (lib != null) {
				boolean hasLib = false;
				if (ast.getPackage().getName().getFullyQualifiedName().startsWith(lib))
					hasLib = true;
				if (!hasLib && ast.imports() != null) {
					for (int i = 0; i < ast.imports().size(); i++) {
						ImportDeclaration ic = (ImportDeclaration) ast.imports().get(i);
						if (ic.getName().getFullyQualifiedName().startsWith(lib)) {
							hasLib = true;
							break;
						}
					}
				}
				if (!hasLib)
					return;
			}
			if (testing)
				System.out.println(sourceFilePath);
			stLog.println(sourceFilePath);

			for (int i = 0; i < ast.types().size(); i++) {
				if (ast.types().get(i) instanceof TypeDeclaration) {
					TypeDeclaration td = (TypeDeclaration) ast.types().get(i);
					numOfSequences += generateSequence(keepUnresolvables, lib, td, sourceFilePath,
							ast.getPackage().getName().getFullyQualifiedName(), "");
				}
			}
		}
	}

	private ArrayList<String> getRootPaths() {
		ArrayList<String> rootPaths = new ArrayList<>();
		if (PARSE_INDIVIDUAL_SRC)
			getRootPaths(new File(inPath), rootPaths);
		else {
			if (SCAN_FILES_FRIST)
				getRootPaths(new File(inPath), rootPaths);
			rootPaths = new ArrayList<>();
			rootPaths.add(inPath);
		}
		return rootPaths;
	}

	private void getRootPaths(File file, ArrayList<String> rootPaths) {
		if (file.isDirectory()) {
			System.out.println(rootPaths);
			for (File sub : file.listFiles())
				getRootPaths(sub, rootPaths);
		} else if (file.getName().endsWith(".java")) {
			Map options = JavaCore.getOptions();
			options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
			options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_8);
			options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
			ASTParser parser = ASTParser.newParser(AST.JLS8);
			parser.setCompilerOptions(options);
			parser.setSource(FileUtil.getFileContent(file.getAbsolutePath()).toCharArray());
			try {
				CompilationUnit ast = (CompilationUnit) parser.createAST(null);
				if (ast.getPackage() != null && !ast.types().isEmpty()
						&& ast.types().get(0) instanceof TypeDeclaration) {
					String name = ast.getPackage().getName().getFullyQualifiedName();
					name = name.replace('.', '\\');
					String p = file.getParentFile().getAbsolutePath();
					if (p.endsWith(name))
						add(p.substring(0, p.length() - name.length() - 1), rootPaths);
				} /*
					 * else badFiles.add(file.getAbsolutePath());
					 */
			} catch (Throwable t) {
				badFiles.add(file.getAbsolutePath());
			}
		}
	}

	private void add(String path, ArrayList<String> rootPaths) {
		int index = Collections.binarySearch(rootPaths, path);
		if (index < 0) {
			index = -index - 1;
			int i = rootPaths.size() - 1;
			while (i > index) {
				if (rootPaths.get(i).startsWith(path))
					rootPaths.remove(i);
				i--;
			}
			i = index - 1;
			while (i >= 0) {
				if (path.startsWith(rootPaths.get(i)))
					return;
				i--;
			}
			rootPaths.add(index, path);
		}
	}

	private int generateSequence(boolean keepUnresolvables, String lib, TypeDeclaration td, String path,
			String packageName, String outer) {
		int numOfSequences = 0;
		String name = outer.isEmpty() ? td.getName().getIdentifier() : outer + "." + td.getName().getIdentifier();
		String className = td.getName().getIdentifier(), superClassName = null;
		LinkedHashSet<LocalEntity> setFieldsForTD = MethodEncoderVisitor.setInfoOfFieldDeclaration(td);
		if (td.getSuperclassType() != null)
			superClassName = MethodEncoderVisitor.getUnresolvedType(td.getSuperclassType());

//		System.out.println("size "+td.getMethods().length);
		for (MethodDeclaration method : td.getMethods()) {
			stLog.println(path + "\t" + name + "\t" + method.getName().getIdentifier() + "\t" + getParameters(method));
			OnlySourceEncoderVisitor sg = new OnlySourceEncoderVisitor(className, superClassName);
			sg.setSetFields(setFieldsForTD);
			sg.setArrLibrariesPrefix(this.arrPrefix);
			sg.setFopInvocationObject(fopInvocationObject);
			sg.setFopDictionaryTextDescription(fopQueryObject);
			sg.setHashIdenPath(this.idenHashPath);
			sg.setMapIDAndIden(mapIDAndIden);
			sg.setMapIdenAndID(mapIdenAndID);
			sg.setMapIDAppear(mapIDAppear);
			sg.setLemm(lemm);
//			System.out.println("here "+method.toString());
			method.accept(sg);
			int numofExpressions = sg.getNumOfExpressions(),
					numOfResolvedExpressions = sg.getNumOfResolvedExpressions();
			String source = sg.getPartialSequence();
			String[] sTokens = sg.getPartialSequenceTokens();

//					this.locations.add(path + "\t" + name + "\t" + method.getName().getIdentifier() + "\t" + getParameters(method) + "\t" + numofExpressions + "\t" + numOfResolvedExpressions + "\t" + (numOfResolvedExpressions * 100 / numofExpressions) + "%");
//					this.sourceSequences.add(source);
//					this.targetSequences.add(target);
//					this.sourceSequenceTokens.add(sTokens);
//					this.targetSequenceTokens.add(tTokens);
			if(sTokens.length > 2 && numofExpressions > 0) {
				stLocations.print(path + "\t" + packageName + "\t" + name + "\t" + method.getName().getIdentifier() + "\t"
						+ getParameters(method) + "\t" + numofExpressions + "\t" + numOfResolvedExpressions + "\t"
						+ (numOfResolvedExpressions * 100 / numofExpressions) + "%" + "\n");
				stSourceSequences.print(source + "\n");
				numOfSequences++;

			}
			
		}
		for (TypeDeclaration inner : td.getTypes())
			numOfSequences += generateSequence(keepUnresolvables, lib, inner, path, packageName, name);
		return numOfSequences;
	}

	private String getParameters(MethodDeclaration method) {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		for (int i = 0; i < method.parameters().size(); i++) {
			SingleVariableDeclaration d = (SingleVariableDeclaration) (method.parameters().get(i));
			String type = MethodEncoderVisitor.getUnresolvedType(d.getType());
			sb.append("\t" + type);
		}
		sb.append("\t)");
		return sb.toString();
	}

	private String[] getSourcePaths(String path, String[] extensions) {
		HashSet<String> exts = new HashSet<>();
		for (String e : extensions)
			exts.add(e);
		HashSet<String> paths = new HashSet<>();
		getSourcePaths(new File(path), paths, exts);
		paths.removeAll(badFiles);
		return (String[]) paths.toArray(new String[0]);
	}

	private void getSourcePaths(File file, HashSet<String> paths, HashSet<String> exts) {
		if (file.isDirectory()) {
			for (File sub : file.listFiles())
				getSourcePaths(sub, paths, exts);
		} else if (exts.contains(getExtension(file.getName())))
			paths.add(file.getAbsolutePath());
	}

	private Object getExtension(String name) {
		int index = name.lastIndexOf('.');
		if (index < 0)
			index = 0;
		return name.substring(index);
	}

	private String[] getJarPaths() {
		HashMap<String, File> jarFiles = new HashMap<>();
		HashSet<String> globalRepoLinks = new HashSet<>();
		globalRepoLinks.add("http://central.maven.org/maven2/");
		HashMap<String, String> globalProperties = new HashMap<>();
		HashMap<String, String> globalManagedDependencies = new HashMap<>();
		Stack<ClassPathUtil.PomFile> parentPomFiles = new Stack<>();
		getJarFiles(new File(inPath), jarFiles, globalRepoLinks, globalProperties, globalManagedDependencies,
				parentPomFiles);
		String[] paths = new String[jarFiles.size()];
		int i = 0;
		for (File file : jarFiles.values())
			paths[i++] = file.getAbsolutePath();
		return paths;
	}

	private void getJarFiles(File file, HashMap<String, File> jarFiles, HashSet<String> globalRepoLinks,
			HashMap<String, String> globalProperties, HashMap<String, String> globalManagedDependencies,
			Stack<PomFile> parentPomFiles) {
		if (file.isDirectory()) {
			int size = parentPomFiles.size();
			ArrayList<File> dirs = new ArrayList<>();
			for (File sub : file.listFiles()) {
				if (sub.isDirectory())
					dirs.add(sub);
				else
					getJarFiles(sub, jarFiles, globalRepoLinks, globalProperties, globalManagedDependencies,
							parentPomFiles);
			}
			for (File dir : dirs)
				getJarFiles(dir, jarFiles, globalRepoLinks, globalProperties, globalManagedDependencies,
						parentPomFiles);
			if (parentPomFiles.size() > size)
				parentPomFiles.pop();
		} else if (file.getName().endsWith(".jar")) {
			File f = jarFiles.get(file.getName());
			if (f == null || file.lastModified() > f.lastModified())
				jarFiles.put(file.getName(), file);
		} else if (file.getName().equals("build.gradle")) {
			try {
				ClassPathUtil.getGradleDependencies(file, this.inPath + "/lib");
			} catch (Throwable t) {
				t.printStackTrace();
			}
		} else if (file.getName().equals("pom.xml")) {
			try {
				ClassPathUtil.getPomDependencies(file, this.inPath + "/lib", globalRepoLinks, globalProperties,
						globalManagedDependencies, parentPomFiles);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
