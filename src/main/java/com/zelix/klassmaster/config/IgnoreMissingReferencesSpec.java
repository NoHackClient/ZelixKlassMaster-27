package com.zelix.klassmaster.config;

import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyQuery;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ast.ASTComplexAnnotationSpecifier;
import com.zelix.klassmaster.script.parser.ast.ASTRenameFilterParameter;
import com.zelix.klassmaster.script.parser.ast.ParameterListStatement;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.TwoKeySetMultiMap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class IgnoreMissingReferencesSpec implements ClassHierarchyQuery {
    public final Set ignoredClassNames = ZkmUtils.createHashSet();
    public final TwoKeySetMultiMap ignoredMemberRefs = new TwoKeySetMultiMap();
    public final ClassRepository classRepository;
    public final ScriptEnvironment scriptEnvironment;
    public final List filterParameters;

    @Override
    public final boolean isHierarchySealed() {
        return this.classRepository.isHierarchySealed();
    }

    @Override
    public String getOriginalClassName(Object object) {
        String string = (String) object;
        return this.classRepository.getOriginalClassName(string);
    }

    @Override
    public Set getClassAnnotations(Object object, String string, Integer integer, boolean bl) throws ZkmException, IOException {
        return this.classRepository.getClassAnnotations(string, integer, bl);
    }

    public boolean isMissingFieldIgnored(ClassFileBase classFileBase, String string, String string1, ObservableHolder observableHolder) throws ZkmException, IOException {
        observableHolder.clearValue();
        if (this.filterParameters.isEmpty()) {
            observableHolder.setValue("No explicit specifications so ALL ignored by default");
            this.ignoredMemberRefs.addValue(classFileBase.getClassName(), string, string1);
            return true;
        }

        Iterator iterator = this.filterParameters.iterator();

        while (iterator.hasNext()) {
            ASTRenameFilterParameter aSTRenameFilterParameter = (ASTRenameFilterParameter) iterator.next();
            if (aSTRenameFilterParameter.isFieldSpecifier() && aSTRenameFilterParameter.matchesFieldSpec(this, classFileBase, string, string1)) {
                observableHolder.setValue(aSTRenameFilterParameter.toScriptText());
                this.ignoredMemberRefs.addValue(classFileBase.getClassName(), string, string1);
                return true;
            }

            if (aSTRenameFilterParameter.isClassSpecifier() && aSTRenameFilterParameter.isClassPlusSpec()) {
                observableHolder.setValue(aSTRenameFilterParameter.toScriptText());
                this.ignoredMemberRefs.addValue(classFileBase.getClassName(), string, string1);
                return true;
            }
        }

        return false;
    }

    @Override
    public final boolean implementsAnnotatedInterface(String string, String string1, ASTComplexAnnotationSpecifier aSTComplexAnnotationSpecifier) throws ZkmException, IOException {
        return this.classRepository.implementsAnnotatedInterface(string, string1, aSTComplexAnnotationSpecifier);
    }

    public boolean isClassIgnored(Object object) {
        return this.ignoredClassNames.contains(object);
    }

    @Override
    public final boolean extendsAnnotatedClassByOriginalName(String string, String string1, ASTComplexAnnotationSpecifier aSTComplexAnnotationSpecifier) throws ZkmException, IOException {
        return this.classRepository.extendsAnnotatedClassByOriginalName(string, string1, aSTComplexAnnotationSpecifier);
    }

    public ScriptEnvironment getScriptEnvironment() {
        return this.scriptEnvironment;
    }

    @Override
    public final boolean implementsInterface(String string, String string1) throws ZkmException, IOException {
        return this.classRepository.implementsInterface(string, string1);
    }

    @Override
    public final boolean isObjectMethodSignature(Object object) {
        MethodSignature methodSignature1 = (MethodSignature) object;
        return this.classRepository.isObjectMethodSignature(methodSignature1);
    }

    @Override
    public final boolean isSubclassByOriginalName(String string, String string1) throws ZkmException, IOException {
        return this.classRepository.isSubclassByOriginalName(string, string1);
    }

    @Override
    public final boolean hasOriginalNameCaches() {
        return this.classRepository.hasOriginalNameCaches();
    }

    @Override
    public Set getClassAnnotations(String string, Integer integer, boolean bl) throws ZkmException, IOException {
        Boolean boolean1 = bl;
        Integer integer1 = integer;
        String string1 = string;
        return this.getClassAnnotations(true, string1, integer1, boolean1);
    }

    @Override
    public final boolean implementsAnnotatedInterfaceByOriginalName(String string, String string1, ASTComplexAnnotationSpecifier aSTComplexAnnotationSpecifier) throws ZkmException, IOException {
        return this.classRepository.implementsAnnotatedInterfaceByOriginalName(string, string1, aSTComplexAnnotationSpecifier);
    }

    @Override
    public final boolean isHierarchyMarked() {
        return this.classRepository.isHierarchyMarked();
    }

    @Override
    public final boolean extendsAnnotatedClass(String string, String string1, ASTComplexAnnotationSpecifier aSTComplexAnnotationSpecifier) throws ZkmException, IOException {
        return this.classRepository.extendsAnnotatedClass(string, string1, aSTComplexAnnotationSpecifier);
    }

    public IgnoreMissingReferencesSpec(ClassRepository classRepository1, List list1, ScriptEnvironment scriptEnvironment1) {
        this.classRepository = classRepository1;
        this.scriptEnvironment = scriptEnvironment1;
        this.filterParameters = this.collectFilterParameters(list1);
        this.validateFilterParameters();
    }

    @Override
    public final boolean isSubclass(String string, String string1) throws ZkmException, IOException {
        return this.classRepository.isSubclass(string, string1);
    }

    public boolean isMissingMethodIgnored(ClassFileBase classFileBase, String string, String string1, ObservableHolder observableHolder) throws ZkmException, IOException {
        observableHolder.clearValue();
        if (this.filterParameters.isEmpty()) {
            observableHolder.setValue("No explicit specifications so ALL ignored by default");
            this.ignoredMemberRefs.addValue(classFileBase.getClassName(), string, string1);
            return true;
        }

        Iterator iterator = this.filterParameters.iterator();

        while (iterator.hasNext()) {
            ASTRenameFilterParameter aSTRenameFilterParameter = (ASTRenameFilterParameter) iterator.next();
            if (aSTRenameFilterParameter.isMethodSpecifier() && aSTRenameFilterParameter.matchesMethodSpec(this, classFileBase, string, string1)) {
                observableHolder.setValue(aSTRenameFilterParameter.toScriptText());
                this.ignoredMemberRefs.addValue(classFileBase.getClassName(), string, string1);
                return true;
            }

            if (aSTRenameFilterParameter.isClassSpecifier() && aSTRenameFilterParameter.isClassPlusSpec()) {
                observableHolder.setValue(aSTRenameFilterParameter.toScriptText());
                this.ignoredMemberRefs.addValue(classFileBase.getClassName(), string, string1);
                return true;
            }
        }

        return false;
    }

    @Override
    public ClassResolver getClassResolver() {
        return this.classRepository.getClassResolver();
    }

    public List collectFilterParameters(List list1) {
        HashSet hashSet = ZkmUtils.createHashSet();
        ArrayList arrayList = new ArrayList();

        for (int i = 0; i < list1.size(); i++) {
            Enumeration enumeration = ((ParameterListStatement) list1.get(i)).getRenameFilterParameters();

            while (enumeration.hasMoreElements()) {
                ASTRenameFilterParameter aSTRenameFilterParameter = (ASTRenameFilterParameter) enumeration.nextElement();
                if (hashSet.add(aSTRenameFilterParameter)) {
                    arrayList.add(aSTRenameFilterParameter);
                }
            }
        }

        return arrayList;
    }

    @Override
    public final boolean implementsInterfaceByOriginalName(String string, String string1) throws ZkmException, IOException {
        return this.classRepository.implementsInterfaceByOriginalName(string, string1);
    }

    public void validateFilterParameters() {
        Iterator iterator = this.filterParameters.iterator();

        while (iterator.hasNext()) {
            ASTRenameFilterParameter aSTRenameFilterParameter = (ASTRenameFilterParameter) iterator.next();
            if (aSTRenameFilterParameter.isPackageSpecifier()) {
                this.scriptEnvironment
                        .logMessage(
                                "Parameter '" + aSTRenameFilterParameter + "' in 'ignoreMissingReferences' statement is a 'package' level parameter. It will be ignored.",
                                true
                        );
                iterator.remove();
            } else if (aSTRenameFilterParameter.isClassSpecifier() && aSTRenameFilterParameter.hasLinkClassName()) {
                this.scriptEnvironment
                        .logWarning(
                                "Parameter '" + aSTRenameFilterParameter + "' in 'ignoreMissingReferences' statement cannot use the '<link>' syntax. It will be ignored.",
                                true
                        );
                iterator.remove();
            } else if (aSTRenameFilterParameter.isMethodSpecifier() && aSTRenameFilterParameter.hasLinkMethodSignature()) {
                this.scriptEnvironment
                        .logWarning(
                                "Parameter '" + aSTRenameFilterParameter + "' in 'ignoreMissingReferences' statement cannot use the '<link>' syntax. It will be ignored.",
                                true
                        );
                iterator.remove();
            } else if (aSTRenameFilterParameter.isClassSpecifier() && aSTRenameFilterParameter.hasContainingClause()) {
                this.scriptEnvironment
                        .logWarning(
                                "Parameter '"
                                        + aSTRenameFilterParameter
                                        + "' in 'ignoreMissingReferences' statement cannot use the 'containing{...}' syntax. It will be ignored.",
                                true
                        );
            } else if (aSTRenameFilterParameter.isClassSpecifier() && aSTRenameFilterParameter.hasExtendsClause()) {
                this.scriptEnvironment
                        .logWarning(
                                "Parameter '" + aSTRenameFilterParameter + "' in 'ignoreMissingReferences' statement cannot use the 'extends' syntax. It will be ignored.",
                                true
                        );
            } else if (aSTRenameFilterParameter.isClassSpecifier() && aSTRenameFilterParameter.isStandaloneAnnotation()) {
                this.scriptEnvironment
                        .logWarning(
                                "Parameter '"
                                        + aSTRenameFilterParameter
                                        + "' in 'ignoreMissingReferences' statement cannot use the standalone annotation syntax. It will be ignored.",
                                true
                        );
            } else if (aSTRenameFilterParameter.isClassSpecifier() && aSTRenameFilterParameter.hasImplementsClause()) {
                this.scriptEnvironment
                        .logWarning(
                                "Parameter '" + aSTRenameFilterParameter + "' in 'ignoreMissingReferences' statement cannot use the 'implements' syntax. It will be ignored.",
                                true
                        );
            } else if (aSTRenameFilterParameter.isMethodSpecifier() && aSTRenameFilterParameter.hasPlusSignatureClasses()) {
                this.scriptEnvironment
                        .logWarning(
                                "Parameter '"
                                        + aSTRenameFilterParameter
                                        + "' in 'ignoreMissingReferences' cannot use the '"
                                        + "+signatureClasses"
                                        + "' keyword. It will be ignored.",
                                true
                        );
            } else if (aSTRenameFilterParameter.isFieldSpecifier() && aSTRenameFilterParameter.hasClassCaretTag()) {
                this.scriptEnvironment
                        .logWarning(
                                "Parameter '"
                                        + aSTRenameFilterParameter
                                        + "' in 'ignoreMissingReferences' specification of containing class will ignored. However, field specification will be effective.",
                                true
                        );
            } else if (aSTRenameFilterParameter.isMethodSpecifier() && aSTRenameFilterParameter.hasClassCaretTag()) {
                this.scriptEnvironment
                        .logWarning(
                                "Parameter '"
                                        + aSTRenameFilterParameter
                                        + "' in 'ignoreMissingReferences' specification of containing class will ignored. However, method specification will be effective.",
                                true
                        );
            }
        }
    }

    public boolean isMissingClassIgnored(String string, ObservableHolder observableHolder) throws ZkmException, IOException {
        observableHolder.clearValue();
        if (this.filterParameters.isEmpty()) {
            observableHolder.setValue("No explicit specifications so ALL ignored by default");
            this.ignoredClassNames.add(string);
            return true;
        }

        Iterator iterator = this.filterParameters.iterator();

        while (iterator.hasNext()) {
            ASTRenameFilterParameter aSTRenameFilterParameter = (ASTRenameFilterParameter) iterator.next();
            if (aSTRenameFilterParameter.isClassSpecifier() && aSTRenameFilterParameter.matchesQualifiedClassName(string)) {
                observableHolder.setValue(aSTRenameFilterParameter.toScriptText());
                this.ignoredClassNames.add(string);
                return true;
            }

            if ((aSTRenameFilterParameter.isFieldSpecifier() || aSTRenameFilterParameter.isMethodSpecifier())
                    && aSTRenameFilterParameter.matchesCaretClassName(string)) {
                observableHolder.setValue(aSTRenameFilterParameter.toScriptText());
                this.ignoredClassNames.add(string);
                return true;
            }
        }

        return false;
    }
}
