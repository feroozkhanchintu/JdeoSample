package com.ferooz.sourcecodearchitect.util;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Ferooz on 04/01/17.
 */
public class ASTUtil {

    public static CompilationUnit createCompilationUnit(String filePath, String[] sources, String[] classpathEntries) throws IOException {
        if (!FileUtil.fileExists(filePath))
            return null;

        String fileContents = FileUtil.getFileContents(filePath);

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

}
