package com.zelix.klassmaster.obfuscator.exclude;

import com.zelix.klassmaster.classfile.AbstractFieldInfo;
import com.zelix.klassmaster.classfile.AbstractMethodInfo;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.MemberInfo;
import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyQuery;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ast.ASTComplexAnnotationSpecifier;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

public abstract class AbstractExclusionSpec implements ClassHierarchyQuery {
    public final ClassRepository classRepository;
    public final List exclusionStatements;
    public final ScriptEnvironment scriptEnvironment;
    public final PrintWriter logWriter;

    public static String formatMethodSignature(AbstractMethodInfo abstractMethodInfo, long ba) throws IOException {
        return formatMethod(abstractMethodInfo, false, (ClassHierarchyQuery) null);
    }

    @Override
    public ClassResolver getClassResolver() {
        return this.classRepository.getClassResolver();
    }

    public static final String formatField(AbstractFieldInfo abstractFieldInfo, boolean bl, ClassHierarchyQuery classHierarchyQuery) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        if (bl) {
            stringBuilder.append(abstractFieldInfo.formatAnnotations(classHierarchyQuery));
        }

        stringBuilder.append(abstractFieldInfo.getModifierString());
        String string = abstractFieldInfo.getOriginalDescriptor();
        if (string.length() == 1) {
            stringBuilder.append(ConstantPoolEntry.getPrimitiveTypeName(string));
        } else {
            stringBuilder.append(ZkmUtils.descriptorToJavaName(string));
        }

        stringBuilder.append(" ");
        stringBuilder.append(abstractFieldInfo.getOriginalMemberName());
        if (abstractFieldInfo.isRenamed() || abstractFieldInfo.isDescriptorChanged()) {
            stringBuilder.append(" (");
            String string1 = abstractFieldInfo.getTypeName();
            stringBuilder.append(string1);
            stringBuilder.append(" ");
            stringBuilder.append(abstractFieldInfo.getSourceName());
            stringBuilder.append(')');
        }

        return stringBuilder.toString();
    }

    public boolean canUseFieldNameIndex() {
        return !HiddenOptionFlags.MATCH_ORIGINAL_NAMES || !this.classRepository.isHierarchySealed();
    }

    @Override
    public final boolean isHierarchySealed() {
        return this.classRepository.isHierarchySealed();
    }

    @Override
    public final Set getClassAnnotations(Object object, String string, Integer integer, boolean bl) throws ZkmException, IOException {
        boolean bl1 = (Boolean) object;
        ClassRepository classRepository1 = this.classRepository;
        Boolean boolean1 = bl;
        Integer integer1 = integer;
        String string1 = string;
        return classRepository1.getClassAnnotations(bl1, string1, integer1, boolean1);
    }

    @Override
    public final boolean isSubclass(String string, String string1) throws ZkmException, IOException {
        return this.classRepository.isSubclass(string, string1);
    }

    @Override
    public boolean isObjectMethodSignature(Object object) {
        MethodSignature methodSignature1 = (MethodSignature) object;
        return this.classRepository.isObjectMethodSignature(methodSignature1);
    }

    @Override
    public final boolean extendsAnnotatedClass(String string, String string1, ASTComplexAnnotationSpecifier aSTComplexAnnotationSpecifier) throws ZkmException, IOException {
        return this.classRepository.extendsAnnotatedClass(string, string1, aSTComplexAnnotationSpecifier);
    }

    @Override
    public final Set getClassAnnotations(String string, Integer integer, boolean bl) throws ZkmException, IOException {
        Boolean boolean1 = bl;
        Integer integer1 = integer;
        String string1 = string;
        return this.getClassAnnotations(true, string1, integer1, boolean1);
    }

    public static String getClassMatchName(ClassFileBase classFileBase) {
        return HiddenOptionFlags.MATCH_ORIGINAL_NAMES ? classFileBase.getOriginalPackagePath() : classFileBase.getPackagePath();
    }

    @Override
    public final boolean implementsInterface(String string, String string1) throws ZkmException, IOException {
        return this.classRepository.implementsInterface(string, string1);
    }

    @Override
    public final boolean isHierarchyMarked() {
        return this.classRepository.isHierarchyMarked();
    }

    public boolean canUseMethodNameIndex() {
        return !HiddenOptionFlags.MATCH_ORIGINAL_NAMES || !this.classRepository.isHierarchyMarked();
    }

    public static String getMethodMatchName(AbstractMethodInfo abstractMethodInfo) {
        return HiddenOptionFlags.MATCH_ORIGINAL_NAMES ? abstractMethodInfo.getOriginalMemberName() : abstractMethodInfo.getJvmName();
    }

    public static final String formatClassWithModifiers(ClassFileBase classFileBase, ClassHierarchyQuery classHierarchyQuery) throws ZkmException, IOException {
        return formatClass(classFileBase, classHierarchyQuery, true);
    }

    public AbstractExclusionSpec(ClassRepository classRepository1, List list1, ScriptEnvironment scriptEnvironment1) {
        this.classRepository = classRepository1;
        this.exclusionStatements = list1;
        this.scriptEnvironment = scriptEnvironment1;
        this.logWriter = scriptEnvironment1.getLogWriter();
    }

    @Override
    public final boolean implementsAnnotatedInterface(String string, String string1, ASTComplexAnnotationSpecifier aSTComplexAnnotationSpecifier) throws ZkmException, IOException {
        return this.classRepository.implementsAnnotatedInterface(string, string1, aSTComplexAnnotationSpecifier);
    }

    public static String getClassSimpleMatchName(ClassFileBase classFileBase) {
        return HiddenOptionFlags.MATCH_ORIGINAL_NAMES ? classFileBase.getOriginalSimpleName() : classFileBase.getSimpleName();
    }

    @Override
    public final boolean implementsAnnotatedInterfaceByOriginalName(String string, String string1, ASTComplexAnnotationSpecifier aSTComplexAnnotationSpecifier) throws ZkmException, IOException {
        return this.classRepository.implementsAnnotatedInterfaceByOriginalName(string, string1, aSTComplexAnnotationSpecifier);
    }

    @Override
    public final boolean implementsInterfaceByOriginalName(String string, String string1) throws ZkmException, IOException {
        return this.classRepository.implementsInterfaceByOriginalName(string, string1);
    }

    public static String formatMethodWithModifiers(AbstractMethodInfo abstractMethodInfo, ClassHierarchyQuery classHierarchyQuery) throws IOException {
        return formatMethod(abstractMethodInfo, true, classHierarchyQuery);
    }

    public static String getFieldMatchName(AbstractFieldInfo abstractFieldInfo) {
        return HiddenOptionFlags.MATCH_ORIGINAL_NAMES ? abstractFieldInfo.getOriginalMemberName() : abstractFieldInfo.getSourceName();
    }

    public static String formatMethod(AbstractMethodInfo abstractMethodInfo, boolean bl, ClassHierarchyQuery classHierarchyQuery) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        if (bl) {
            stringBuilder.append(abstractMethodInfo.formatAnnotations(classHierarchyQuery));
        }

        stringBuilder.append(abstractMethodInfo.getModifierString());
        stringBuilder.append(abstractMethodInfo.toOriginalDisplayString());
        StringBuilder stringBuilder1;
        String string;
        if (!abstractMethodInfo.isRenamed()) {
            if (!abstractMethodInfo.isDescriptorChanged()) {
                return stringBuilder.toString();
            }

            stringBuilder1 = stringBuilder;
            string = " (";
        } else {
            stringBuilder1 = stringBuilder;
            string = " (";
        }

        stringBuilder1.append(string);
        stringBuilder.append(abstractMethodInfo.buildDeclaration(false).trim());
        stringBuilder.append(')');
        return stringBuilder.toString();
    }

    public final String describeClass(ClassFileBase classFileBase) throws ZkmException, IOException {
        return formatClass(classFileBase, this.classRepository, true);
    }

    public static final String formatFieldWithModifiers(AbstractFieldInfo abstractFieldInfo, ClassHierarchyQuery classHierarchyQuery) throws IOException {
        return formatField(abstractFieldInfo, true, classHierarchyQuery);
    }

    public static String getMemberMatchDescriptor(MemberInfo memberInfo1) {
        return HiddenOptionFlags.MATCH_ORIGINAL_NAMES ? memberInfo1.getOriginalDescriptor() : memberInfo1.getDescriptor();
    }

    @Override
    public String getOriginalClassName(Object object) {
        String string = (String) object;
        return this.classRepository.getOriginalClassName(string);
    }

    @Override
    public final boolean extendsAnnotatedClassByOriginalName(String string, String string1, ASTComplexAnnotationSpecifier aSTComplexAnnotationSpecifier) throws ZkmException, IOException {
        return this.classRepository.extendsAnnotatedClassByOriginalName(string, string1, aSTComplexAnnotationSpecifier);
    }

    public static final String formatClass(ClassFileBase classFileBase, ClassHierarchyQuery classHierarchyQuery, boolean bl) throws ZkmException, IOException {
        StringBuilder stringBuilder = new StringBuilder();
        if (bl) {
            stringBuilder.append(classFileBase.formatInheritedAnnotations(classHierarchyQuery));
        }

        if (stringBuilder.length() > 0 && stringBuilder.charAt(stringBuilder.length() - 1) != ' ') {
            stringBuilder.append(' ');
        }

        stringBuilder.append(classFileBase.getModifierString());
        if (stringBuilder.length() > 0 && stringBuilder.charAt(stringBuilder.length() - 1) != ' ') {
            stringBuilder.append(' ');
        }

        stringBuilder.append(classFileBase.getOriginalDottedName());
        if (classFileBase.isRenamed()) {
            stringBuilder.append(" (");
            stringBuilder.append(classFileBase.getDottedClassName());
            stringBuilder.append(')');
        }

        return stringBuilder.toString();
    }

    @Override
    public final boolean hasOriginalNameCaches() {
        return this.classRepository.hasOriginalNameCaches();
    }

    @Override
    public final boolean isSubclassByOriginalName(String string, String string1) throws ZkmException, IOException {
        return this.classRepository.isSubclassByOriginalName(string, string1);
    }
}
