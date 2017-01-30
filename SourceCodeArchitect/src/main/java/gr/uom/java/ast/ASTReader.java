package gr.uom.java.ast;

import gr.uom.java.ast.decomposition.AbstractExpression;
import gr.uom.java.ast.decomposition.MethodBodyObject;
import gr.uom.java.ast.decomposition.cfg.CFG;
import gr.uom.java.ast.decomposition.cfg.PDG;
import gr.uom.java.ast.util.StatementExtractor;
import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jface.text.IDocument;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ASTReader {

	private static SystemObject systemObject;
	private static IJavaProject examinedProject;
	public static final int JLS = AST.JLS8;


	public static List<AbstractTypeDeclaration> getRecursivelyInnerTypes(AbstractTypeDeclaration typeDeclaration) {
		List<AbstractTypeDeclaration> innerTypeDeclarations = new ArrayList<AbstractTypeDeclaration>();
		StatementExtractor statementExtractor = new StatementExtractor();
		List<BodyDeclaration> bodyDeclarations = typeDeclaration.bodyDeclarations();
		for(BodyDeclaration bodyDeclaration : bodyDeclarations) {
			if(bodyDeclaration instanceof MethodDeclaration) {
				MethodDeclaration methodDeclaration = (MethodDeclaration)bodyDeclaration;
				if(methodDeclaration.getBody() != null) {
					List<Statement> statements = statementExtractor.getTypeDeclarationStatements(methodDeclaration.getBody());
					for(Statement statement : statements) {
						TypeDeclarationStatement typeDeclarationStatement = (TypeDeclarationStatement)statement;
						AbstractTypeDeclaration declaration = typeDeclarationStatement.getDeclaration();
						if(declaration instanceof TypeDeclaration) {
							innerTypeDeclarations.add((TypeDeclaration)declaration);
						}
					}
				}
			}
			else if(bodyDeclaration instanceof TypeDeclaration) {
				TypeDeclaration type = (TypeDeclaration)bodyDeclaration;
				innerTypeDeclarations.add(type);
				innerTypeDeclarations.addAll(getRecursivelyInnerTypes(type));
			}
			else if(bodyDeclaration instanceof EnumDeclaration) {
				EnumDeclaration type = (EnumDeclaration)bodyDeclaration;
				innerTypeDeclarations.add(type);
				innerTypeDeclarations.addAll(getRecursivelyInnerTypes(type));
			}
		}
		return innerTypeDeclarations;
	}

	private List<ClassObject> parseAST(CompilationUnit compilationUnit, IFile iFile) {
//		ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager();
//		IPath path = compilationUnit.getJavaElement().getPath();
//		try {
//			bufferManager.connect(path, LocationKind.IFILE, null);
//		} catch (CoreException e) {
//			e.printStackTrace();
//		}
//		ITextFileBuffer textFileBuffer = bufferManager.getTextFileBuffer(path, LocationKind.IFILE);
//		IDocument document = textFileBuffer.getDocument();
				IDocument document = null;

		List<Comment> comments = compilationUnit.getCommentList();
		List<ClassObject> classObjects = new ArrayList<ClassObject>();
        List<AbstractTypeDeclaration> topLevelTypeDeclarations = compilationUnit.types();
        for(AbstractTypeDeclaration abstractTypeDeclaration : topLevelTypeDeclarations) {
        	if(abstractTypeDeclaration instanceof TypeDeclaration) {
        		TypeDeclaration topLevelTypeDeclaration = (TypeDeclaration)abstractTypeDeclaration;
        		List<AbstractTypeDeclaration> typeDeclarations = new ArrayList<AbstractTypeDeclaration>();
        		typeDeclarations.add(topLevelTypeDeclaration);
        		typeDeclarations.addAll(getRecursivelyInnerTypes(topLevelTypeDeclaration));
        		for(AbstractTypeDeclaration typeDeclaration : typeDeclarations) {
        			if(typeDeclaration instanceof TypeDeclaration) {
        				final ClassObject classObject = processTypeDeclaration(iFile, document, (TypeDeclaration)typeDeclaration, comments);
        				classObjects.add(classObject);
        			}
        			else if(typeDeclaration instanceof EnumDeclaration) {
        				final ClassObject classObject = processEnumDeclaration(iFile, document, (EnumDeclaration)typeDeclaration, comments);
        				classObjects.add(classObject);
        			}
        		}
        	}
        	else if(abstractTypeDeclaration instanceof EnumDeclaration) {
        		EnumDeclaration enumDeclaration = (EnumDeclaration)abstractTypeDeclaration;
        		final ClassObject classObject = processEnumDeclaration(iFile, document, enumDeclaration, comments);
	        	classObjects.add(classObject);
        	}
        }
        return classObjects;
	}

	private ClassObject processTypeDeclaration(IFile iFile, IDocument document, TypeDeclaration typeDeclaration, List<Comment> comments) {
		final ClassObject classObject = new ClassObject();
		classObject.setIFile(iFile);
		ITypeBinding typeDeclarationBinding = typeDeclaration.resolveBinding();
		if(typeDeclarationBinding.isLocal()) {
			ITypeBinding declaringClass = typeDeclarationBinding.getDeclaringClass();
			String className = declaringClass.getQualifiedName() + "." + typeDeclarationBinding.getName();
			classObject.setName(className);
		}
		else {
			classObject.setName(typeDeclarationBinding.getQualifiedName());
		}
		classObject.setAbstractTypeDeclaration(typeDeclaration);
		
		if(typeDeclaration.isInterface()) {
			classObject.setInterface(true);
		}
		
		int modifiers = typeDeclaration.getModifiers();
		if((modifiers & Modifier.ABSTRACT) != 0)
			classObject.setAbstract(true);
		
		if((modifiers & Modifier.PUBLIC) != 0)
			classObject.setAccess(Access.PUBLIC);
		else if((modifiers & Modifier.PROTECTED) != 0)
			classObject.setAccess(Access.PROTECTED);
		else if((modifiers & Modifier.PRIVATE) != 0)
			classObject.setAccess(Access.PRIVATE);
		else
			classObject.setAccess(Access.NONE);
		
		if((modifiers & Modifier.STATIC) != 0)
			classObject.setStatic(true);
		
		Type superclassType = typeDeclaration.getSuperclassType();
		if(superclassType != null) {
			ITypeBinding binding = superclassType.resolveBinding();
			String qualifiedName = binding.getQualifiedName();
			TypeObject typeObject = TypeObject.extractTypeObject(qualifiedName);
			classObject.setSuperclass(typeObject);
		}
		
		List<Type> superInterfaceTypes = typeDeclaration.superInterfaceTypes();
		for(Type interfaceType : superInterfaceTypes) {
			ITypeBinding binding = interfaceType.resolveBinding();
			String qualifiedName = binding.getQualifiedName();
			TypeObject typeObject = TypeObject.extractTypeObject(qualifiedName);
			classObject.addInterface(typeObject);
		}
		
		FieldDeclaration[] fieldDeclarations = typeDeclaration.getFields();
		for(FieldDeclaration fieldDeclaration : fieldDeclarations) {
			processFieldDeclaration(classObject, fieldDeclaration);
		}
		
		MethodDeclaration[] methodDeclarations = typeDeclaration.getMethods();
		for(MethodDeclaration methodDeclaration : methodDeclarations) {
			processMethodDeclaration(classObject, methodDeclaration);
		}
		return classObject;
	}

	private ClassObject processEnumDeclaration(IFile iFile, IDocument document, EnumDeclaration enumDeclaration, List<Comment> comments) {
		final ClassObject classObject = new ClassObject();
		classObject.setEnum(true);
		classObject.setIFile(iFile);
		classObject.setName(enumDeclaration.resolveBinding().getQualifiedName());
		classObject.setAbstractTypeDeclaration(enumDeclaration);
		
		int modifiers = enumDeclaration.getModifiers();
		if((modifiers & Modifier.ABSTRACT) != 0)
			classObject.setAbstract(true);
		
		if((modifiers & Modifier.PUBLIC) != 0)
			classObject.setAccess(Access.PUBLIC);
		else if((modifiers & Modifier.PROTECTED) != 0)
			classObject.setAccess(Access.PROTECTED);
		else if((modifiers & Modifier.PRIVATE) != 0)
			classObject.setAccess(Access.PRIVATE);
		else
			classObject.setAccess(Access.NONE);
		
		if((modifiers & Modifier.STATIC) != 0)
			classObject.setStatic(true);
		
		List<Type> superInterfaceTypes = enumDeclaration.superInterfaceTypes();
		for(Type interfaceType : superInterfaceTypes) {
			ITypeBinding binding = interfaceType.resolveBinding();
			String qualifiedName = binding.getQualifiedName();
			TypeObject typeObject = TypeObject.extractTypeObject(qualifiedName);
			classObject.addInterface(typeObject);
		}
		
		List<EnumConstantDeclaration> enumConstantDeclarations = enumDeclaration.enumConstants();
		for(EnumConstantDeclaration enumConstantDeclaration : enumConstantDeclarations) {
			EnumConstantDeclarationObject enumConstantDeclarationObject = new EnumConstantDeclarationObject(enumConstantDeclaration.getName().getIdentifier());
			enumConstantDeclarationObject.setEnumName(classObject.getName());
			enumConstantDeclarationObject.setEnumConstantDeclaration(enumConstantDeclaration);
			List<Expression> arguments = enumConstantDeclaration.arguments();
			for(Expression argument : arguments) {
				AbstractExpression abstractExpression = new AbstractExpression(argument);
				enumConstantDeclarationObject.addArgument(abstractExpression);
			}
			classObject.addEnumConstantDeclaration(enumConstantDeclarationObject);
		}
		
		List<BodyDeclaration> bodyDeclarations = enumDeclaration.bodyDeclarations();
		for(BodyDeclaration bodyDeclaration : bodyDeclarations) {
			if(bodyDeclaration instanceof MethodDeclaration) {
				processMethodDeclaration(classObject, (MethodDeclaration)bodyDeclaration);
			}
			else if(bodyDeclaration instanceof FieldDeclaration) {
				processFieldDeclaration(classObject, (FieldDeclaration)bodyDeclaration);
			}
		}
		return classObject;
	}

	private void processFieldDeclaration(final ClassObject classObject, FieldDeclaration fieldDeclaration) {
		Type fieldType = fieldDeclaration.getType();
		ITypeBinding binding = fieldType.resolveBinding();
		int fieldDeclarationStartPosition = fieldDeclaration.getStartPosition();
		int fieldDeclarationEndPosition = fieldDeclarationStartPosition + fieldDeclaration.getLength();

		List<VariableDeclarationFragment> fragments = fieldDeclaration.fragments();
		for(VariableDeclarationFragment fragment : fragments) {
			String qualifiedName = binding.getQualifiedName();
			TypeObject typeObject = TypeObject.extractTypeObject(qualifiedName);
			typeObject.setArrayDimension(typeObject.getArrayDimension() + fragment.getExtraDimensions());
			FieldObject fieldObject = new FieldObject(typeObject, fragment.getName().getIdentifier());
			fieldObject.setClassName(classObject.getName());
			fieldObject.setVariableDeclarationFragment(fragment);

			int fieldModifiers = fieldDeclaration.getModifiers();
			if((fieldModifiers & Modifier.PUBLIC) != 0)
				fieldObject.setAccess(Access.PUBLIC);
			else if((fieldModifiers & Modifier.PROTECTED) != 0)
				fieldObject.setAccess(Access.PROTECTED);
			else if((fieldModifiers & Modifier.PRIVATE) != 0)
				fieldObject.setAccess(Access.PRIVATE);
			else
				fieldObject.setAccess(Access.NONE);
			
			if((fieldModifiers & Modifier.STATIC) != 0)
				fieldObject.setStatic(true);
			
			classObject.addField(fieldObject);
		}
	}

	private void processMethodDeclaration(final ClassObject classObject, MethodDeclaration methodDeclaration) {
		String methodName = methodDeclaration.getName().getIdentifier();
		final ConstructorObject constructorObject = new ConstructorObject();
		constructorObject.setMethodDeclaration(methodDeclaration);
		constructorObject.setName(methodName);
		constructorObject.setClassName(classObject.getName());
		int methodDeclarationStartPosition = methodDeclaration.getStartPosition();
		int methodDeclarationEndPosition = methodDeclarationStartPosition + methodDeclaration.getLength();

		int methodModifiers = methodDeclaration.getModifiers();
		if((methodModifiers & Modifier.PUBLIC) != 0)
			constructorObject.setAccess(Access.PUBLIC);
		else if((methodModifiers & Modifier.PROTECTED) != 0)
			constructorObject.setAccess(Access.PROTECTED);
		else if((methodModifiers & Modifier.PRIVATE) != 0)
			constructorObject.setAccess(Access.PRIVATE);
		else
			constructorObject.setAccess(Access.NONE);
		
		List<SingleVariableDeclaration> parameters = methodDeclaration.parameters();
		for(SingleVariableDeclaration parameter : parameters) {
			Type parameterType = parameter.getType();
			ITypeBinding binding = parameterType.resolveBinding();
			String qualifiedName = binding.getQualifiedName();
			TypeObject typeObject = TypeObject.extractTypeObject(qualifiedName);
			typeObject.setArrayDimension(typeObject.getArrayDimension() + parameter.getExtraDimensions());
			if(parameter.isVarargs()) {
				typeObject.setArrayDimension(1);
			}
			ParameterObject parameterObject = new ParameterObject(typeObject, parameter.getName().getIdentifier(), parameter.isVarargs());
			parameterObject.setSingleVariableDeclaration(parameter);
			constructorObject.addParameter(parameterObject);
		}
		
		Block methodBody = methodDeclaration.getBody();
		if(methodBody != null) {
			MethodBodyObject methodBodyObject = new MethodBodyObject(methodBody);
			constructorObject.setMethodBody(methodBodyObject);
		}
		
		for(AnonymousClassDeclarationObject anonymous : constructorObject.getAnonymousClassDeclarations()) {
			anonymous.setClassObject(classObject);
			AnonymousClassDeclaration anonymousClassDeclaration = anonymous.getAnonymousClassDeclaration();
			int anonymousClassDeclarationStartPosition = anonymousClassDeclaration.getStartPosition();
			int anonymousClassDeclarationEndPosition = anonymousClassDeclarationStartPosition + anonymousClassDeclaration.getLength();
		}
		
		if(methodDeclaration.isConstructor()) {
			classObject.addConstructor(constructorObject);
		}
		else {
			MethodObject methodObject = new MethodObject(constructorObject);
			List<IExtendedModifier> extendedModifiers = methodDeclaration.modifiers();
			for(IExtendedModifier extendedModifier : extendedModifiers) {
				if(extendedModifier.isAnnotation()) {
					Annotation annotation = (Annotation)extendedModifier;
					if(annotation.getTypeName().getFullyQualifiedName().equals("Test")) {
						methodObject.setTestAnnotation(true);
						break;
					}
				}
			}
			Type returnType = methodDeclaration.getReturnType2();
			ITypeBinding binding = returnType.resolveBinding();
			String qualifiedName = binding.getQualifiedName();
			TypeObject typeObject = TypeObject.extractTypeObject(qualifiedName);
			methodObject.setReturnType(typeObject);
			
			if((methodModifiers & Modifier.ABSTRACT) != 0)
				methodObject.setAbstract(true);
			if((methodModifiers & Modifier.STATIC) != 0)
				methodObject.setStatic(true);
			if((methodModifiers & Modifier.SYNCHRONIZED) != 0)
				methodObject.setSynchronized(true);
			if((methodModifiers & Modifier.NATIVE) != 0)
				methodObject.setNative(true);
			
			classObject.addMethod(methodObject);
			FieldInstructionObject fieldInstruction = methodObject.isGetter();
			if(fieldInstruction != null)
				systemObject.addGetter(methodObject.generateMethodInvocation(), fieldInstruction);
			fieldInstruction = methodObject.isSetter();
			if(fieldInstruction != null)
				systemObject.addSetter(methodObject.generateMethodInvocation(), fieldInstruction);
			fieldInstruction = methodObject.isCollectionAdder();
			if(fieldInstruction != null)
				systemObject.addCollectionAdder(methodObject.generateMethodInvocation(), fieldInstruction);
		}
	}

    public static SystemObject getSystemObject() {
		return systemObject;
	}

	public static IJavaProject getExaminedProject() {
		return examinedProject;
	}

	public static AST getAST() {
		if(systemObject.getClassNumber() > 0) {
			return systemObject.getClassObject(0).getAbstractTypeDeclaration().getAST();
		}
		return null;
	}

    public static String getFileContents(String filePath) throws IOException {
        File file = new File(filePath);
        if(file.exists() && file.isFile()) {
            return FileUtils.readFileToString(file, Charset.defaultCharset());
        }
        return null;
    }

    public static CompilationUnit createCompilationUnit(String filePath, String[] sources, String[] classpathEntries) throws IOException {
		String fileContents = getFileContents(filePath);

		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setSource(fileContents.toCharArray());
		parser.setResolveBindings(true);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setBindingsRecovery(true);

		Map options = JavaCore.getOptions();
		JavaCore.setComplianceOptions(JavaCore.VERSION_1_8, options);
		parser.setCompilerOptions(options);
		parser.setUnitName("CompilationUnit");
		parser.setEnvironment(classpathEntries, sources, new String[]{"UTF-8"}, true);

		CompilationUnit unit = (CompilationUnit) parser.createAST(null);
		return unit;
	}

	public static void main(String[] args) throws IOException {
//		String file = "/Users/Ferooz/Documents/Workspaces/TestJdeo/eclipse.commandline/src/eclipse/commandline/Test.java";
//		String srcPath = "/Users/Ferooz/Documents/Workspaces/TestJdeo";

		String file = "/Users/Ferooz/Downloads/crawler4j/src/main/java/edu/uci/ics/crawler4j/crawler/WebCrawler.java";
        String srcPath = "/Users/Ferooz/Downloads/crawler4j/src/main/java";
        List<File> filesjar = new ArrayList<>();
        File file1 = new File("/Users/Ferooz/Downloads/crawler4j/target/dependency/");
        if(file1.exists() && file1.isDirectory()) {
            filesjar =  (List<File>) FileUtils.listFiles(file1, new String[]{"jar"}, true);
        }

        List<String> classpathEntries = new ArrayList<>();

        for(File f : filesjar){
            classpathEntries.add(f.getAbsolutePath());
        }
        String[] classpath =  classpathEntries.toArray(new String[0]);
        System.out.println(classpath.length);

		CompilationUnit compilationUnit = createCompilationUnit(file, new String[]{srcPath}, classpath);

		CompilationUnitCache.compilationUnitList.add(compilationUnit);
        ASTInformationGenerator.setCurrentCompilationUnit(compilationUnit);
        ASTReader astReader= new ASTReader();
        systemObject = new SystemObject();

        List<ClassObject> classObjects = astReader.parseAST(compilationUnit, null);
		systemObject.addClasses(classObjects);
		System.out.println(classObjects.get(0));
		System.out.println("\n\n");

		CFG cfg = new CFG(classObjects.get(0).getMethodList().get(17));
        System.out.println(cfg);

        PDG pdg = new PDG(cfg, null, classObjects.get(0).getFieldsAccessedInsideMethod(classObjects.get(0).getMethodList().get(17)), null);

        System.out.println(pdg.getEdges());
    }
}