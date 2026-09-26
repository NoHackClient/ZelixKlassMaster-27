package com.zelix.klassmaster.classfile.hierarchy;

import com.zelix.klassmaster.classfile.FieldInfo;
import com.zelix.klassmaster.classfile.FieldNameTypeSignature;
import com.zelix.klassmaster.classfile.FieldSignature;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.ProgramClass;

import java.util.Enumeration;
import java.util.List;

public interface ClassMemberLookup extends ClassHierarchyQuery {
    List getNewFieldNames();

    boolean containsField(FieldSignature fieldSignature, ProgramClass programClass1);

    Enumeration enumerateProgramClasses();

    FieldInfo findField(String string, String string1, String string2);

    MethodInfo[] findMatchingMethods(ProgramClass programClass1, FieldNameTypeSignature fieldNameTypeSignature);

    void registerField(ProgramClass programClass1, FieldInfo fieldInfo);

    void registerMethod(MethodInfo methodInfo1, ProgramClass programClass1);

    Enumeration enumerateFieldsNamed(String string);

    Enumeration enumerateMethodSignaturesNamed(String string);

    Enumeration enumerateClassesDeclaringMethod(MethodSignature methodSignature1);

    boolean declaresMethod(MethodSignature methodSignature1, ProgramClass programClass1);

    boolean hasFieldNamed(String string, String string1);

    List getNewMethodNames();

    FieldInfo[] findFieldsByName(String string, String string1);

    FieldInfo findDeclaredField(Object object, Object object1);

    FieldInfo[] findFieldsBySignature(Object object);

    MethodInfo findDeclaredMethod(Object object, Object object1);
}
