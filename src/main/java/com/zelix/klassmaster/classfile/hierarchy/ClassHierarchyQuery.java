package com.zelix.klassmaster.classfile.hierarchy;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.parser.ast.ASTComplexAnnotationSpecifier;

import java.io.IOException;
import java.util.Set;

public interface ClassHierarchyQuery {
    ClassResolver getClassResolver();

    boolean implementsInterfaceByOriginalName(String string, String string1) throws ZkmException, IOException;

    boolean isHierarchySealed();

    String getOriginalClassName(Object object);

    boolean hasOriginalNameCaches();

    boolean isSubclassByOriginalName(String string, String string1) throws ZkmException, IOException;

    Set getClassAnnotations(String string, Integer integer, boolean bl) throws ZkmException, IOException;

    boolean extendsAnnotatedClass(String string, String string1, ASTComplexAnnotationSpecifier aSTComplexAnnotationSpecifier) throws ZkmException, IOException;

    Set getClassAnnotations(Object object, String string, Integer integer, boolean bl) throws ZkmException, IOException;

    boolean isSubclass(String string, String string1) throws ZkmException, IOException;

    boolean isObjectMethodSignature(Object object);

    boolean implementsAnnotatedInterface(String string, String string1, ASTComplexAnnotationSpecifier aSTComplexAnnotationSpecifier) throws ZkmException, IOException;

    boolean implementsAnnotatedInterfaceByOriginalName(String string, String string1, ASTComplexAnnotationSpecifier aSTComplexAnnotationSpecifier) throws ZkmException, IOException;

    boolean implementsInterface(String string, String string1) throws ZkmException, IOException;

    boolean extendsAnnotatedClassByOriginalName(String string, String string1, ASTComplexAnnotationSpecifier aSTComplexAnnotationSpecifier) throws ZkmException, IOException;

    boolean isHierarchyMarked();
}
