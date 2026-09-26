package com.zelix.klassmaster.obfuscator.parameters;

import com.zelix.klassmaster.archive.ZkmFileUtils;
import com.zelix.klassmaster.changelog.ChangeLogMapping;
import com.zelix.klassmaster.classfile.AbstractMethodInfo;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.classfile.hierarchy.MethodOverrideAnalyzer;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.exclude.AbstractExclusionSpec;
import com.zelix.klassmaster.obfuscator.exclude.ExclusionHandlerBase;
import com.zelix.klassmaster.obfuscator.exclude.FixedClassesExclusionSet;
import com.zelix.klassmaster.obfuscator.exclude.NameExclusionSet;
import com.zelix.klassmaster.obfuscator.reflection.ReflectionAccessMatcher;
import com.zelix.klassmaster.obfuscator.trim.ClassMemberSets;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.ZkmScriptTokenMgrError;
import com.zelix.klassmaster.script.parser.ZkmScriptParseException;
import com.zelix.klassmaster.script.parser.ZkmScriptParser;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;
import com.zelix.klassmaster.script.parser.ast.ASTDefaultMethodParameterChangesExcludeInput;
import com.zelix.klassmaster.script.parser.ast.ASTDefaultMethodParameterObfuscationExcludeInput;
import com.zelix.klassmaster.script.parser.ast.ASTRenameFilterParameter;
import com.zelix.klassmaster.script.parser.ast.ParameterListStatement;
import com.zelix.klassmaster.util.ArrayEnumeration;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.NumericStringUtil;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.SetMultiMap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class MethodParameterExclusions extends ExclusionHandlerBase {
    public static final String DEFAULT_CHANGES_EXCLUDE = " *.* extends java.util.ResourceBundle <init>() and //Resources "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "                              *.* $deserializeLambda$(java.lang.invoke.SerializedLambda) and //Serialized lambda"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "                              interface *.* implements java.lang.annotation.Annotation public *(*) //Annotations"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "                              ;"
            + HiddenOptionFlags.LINE_SEPARATOR;
    public static final String DEFAULT_OBFUSCATION_EXCLUDE = " *.* extends java.util.ResourceBundle <init>() and //Resources "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "                              *.* $deserializeLambda$(java.lang.invoke.SerializedLambda) and //Serialized lambda"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "                              *.* lambda$*(*) and //Serialized lambda"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "                              interface *.* implements java.lang.annotation.Annotation public *(*) and //Annotations"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "                              *.* *(@*.* *) //Parameter annotations "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "                              ;"
            + HiddenOptionFlags.LINE_SEPARATOR;
    public final boolean parameterObfuscation;
    public final String featureName;
    public final String featureNameLower;
    public final boolean hasExplicitExclusions;

    public void excludeTrimExcludedMethods(ClassMemberSets classMemberSets) throws ZkmException, IOException {
        if (classMemberSets != null) {
            this.excludeMethods(classMemberSets.getMethods(), "Direct specification in Trim exclusion");
        }
    }

    public void includeMethodsAndLinked(
            Collection collection1,
            String string,
            ChangeLogMapping changeLogMapping1,
            MethodOverrideAnalyzer methodOverrideAnalyzer,
            FixedClassesExclusionSet fixedClassesExclusionSet1
    ) throws ZkmException, IOException {
        Iterator iterator = collection1.iterator();

        while (iterator.hasNext()) {
            AbstractMethodInfo abstractMethodInfo = (AbstractMethodInfo) iterator.next();
            if (abstractMethodInfo.isProgramMember()) {
                boolean bl = false;
                AbstractMethodInfo abstractMethodInfo1 = null;
                if (!abstractMethodInfo.isStrictlyPrivate() && !abstractMethodInfo.isStatic()) {
                    abstractMethodInfo1 = methodOverrideAnalyzer.findRootMethod(abstractMethodInfo);
                }

                if (!this.isMethodExcluded((MethodInfo) abstractMethodInfo)) {
                    this.includeMethod((MethodInfo) abstractMethodInfo, string);
                    bl = true;
                    if (fixedClassesExclusionSet1 != null) {
                        fixedClassesExclusionSet1.reportMethodChanged((MethodInfo) abstractMethodInfo, changeLogMapping1.getChangeLogName(), false);
                    }
                }

                if (abstractMethodInfo1 != null && abstractMethodInfo1.isProgramMember() && !this.isMethodExcluded((MethodInfo) abstractMethodInfo1)) {
                    this.includeMethod(
                            (MethodInfo) abstractMethodInfo1,
                            string
                                    + " for a linked method in the class '"
                                    + AbstractExclusionSpec.formatClassWithModifiers(abstractMethodInfo.getOwningClass(), this.classRepository)
                                    + "'"
                    );
                    bl = true;
                    if (fixedClassesExclusionSet1 != null) {
                        fixedClassesExclusionSet1.reportMethodChanged((MethodInfo) abstractMethodInfo1, changeLogMapping1.getChangeLogName(), false);
                    }
                }

                if (bl) {
                    ClassHierarchyNode classHierarchyNode = ClassHierarchyNode.findNode(abstractMethodInfo.getClassName());
                    MethodSignature methodSignature1 = abstractMethodInfo.getSignature();
                    ObservableHolder observableHolder = methodOverrideAnalyzer.getInterfaceMethodGroup(classHierarchyNode, methodSignature1);
                    if (observableHolder != null) {
                        Iterator iterator1 = ((List) observableHolder.getValue()).iterator();

                        while (iterator1.hasNext()) {
                            ClassHierarchyNode classHierarchyNode1 = (ClassHierarchyNode) iterator1.next();
                            if (classHierarchyNode1.isProgramClass() && classHierarchyNode1 != classHierarchyNode) {
                                MethodInfo methodInfo1 = this.classRepository.findDeclaredMethod(classHierarchyNode1.getProgramClass(), methodSignature1);
                                if (methodInfo1 != null) {
                                    this.includeMethod(
                                            methodInfo1,
                                            string
                                                    + " for a linked method in the class '"
                                                    + AbstractExclusionSpec.formatClassWithModifiers(abstractMethodInfo.getOwningClass(), this.classRepository)
                                                    + "'"
                                    );
                                    if (fixedClassesExclusionSet1 != null) {
                                        fixedClassesExclusionSet1.reportMethodChanged(methodInfo1, changeLogMapping1.getChangeLogName(), false);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void excludeRecordMethods(Enumeration enumeration) throws ZkmException, IOException {
        while (enumeration.hasMoreElements()) {
            ProgramClass programClass1 = (ProgramClass) enumeration.nextElement();
            if (programClass1.getSuperclassName().equals("java/lang/Record")) {
                for (MethodInfo methodInfo1 : programClass1.getMethodInfos()) {
                    if (isEligible(methodInfo1)) {
                        this.excludeMethod(methodInfo1, "Containing class is a Record");
                    }
                }
            }
        }
    }

    public void excludeFixedClassMethods(FixedClassesExclusionSet fixedClassesExclusionSet1, ListMultimap listMultimap) throws ZkmException, IOException {
        if (fixedClassesExclusionSet1 != null) {
            SetMultiMap setMultiMap = new SetMultiMap();
            Iterator iterator = listMultimap.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                MethodInfo methodInfo1 = (MethodInfo) entry.getKey();
                Iterator iterator1 = ((List) entry.getValue()).iterator();

                while (iterator1.hasNext()) {
                    MethodInfo methodInfo2 = (MethodInfo) iterator1.next();
                    setMultiMap.addValue(methodInfo2, methodInfo1);
                }
            }

            Enumeration enumeration = fixedClassesExclusionSet1.getMatchedClasses();

            while (enumeration.hasMoreElements()) {
                ProgramClass programClass1 = (ProgramClass) enumeration.nextElement();
                String string1 = "Fixed class '" + this.describeClass(programClass1) + "'";

                for (MethodInfo methodInfo3 : programClass1.getMethodInfos()) {
                    this.excludeMethod(methodInfo3, string1);
                    Set set1 = setMultiMap.getValues(methodInfo3);
                    if (set1 != null) {
                        Iterator iterator2 = set1.iterator();

                        while (iterator2.hasNext()) {
                            AbstractMethodInfo abstractMethodInfo = (AbstractMethodInfo) iterator2.next();
                            if (abstractMethodInfo.isProgramMember() && !fixedClassesExclusionSet1.isMatchedClass((ProgramClass) abstractMethodInfo.getOwningClass())) {
                                String string = "Fixed class '" + this.describeClass(programClass1) + "' accesses it";
                                this.excludeMethod((MethodInfo) abstractMethodInfo, string);
                            }
                        }
                    }
                }
            }
        }
    }

    public MethodParameterExclusions(
            boolean parameterObfuscation,
            ClassRepository classRepository1,
            int ba,
            List list1,
            List list2,
            ClassMemberSets classMemberSets,
            Set set1,
            Map map1,
            Set set2,
            Set set3,
            ListMultimap listMultimap,
            NameExclusionSet nameExclusionSet,
            ReflectionAccessMatcher reflectionAccessMatcher,
            FixedClassesExclusionSet fixedClassesExclusionSet1,
            ScriptEnvironment scriptEnvironment1
    ) throws ZkmException, IOException {
        super(classRepository1, list1, list2, scriptEnvironment1);
        this.parameterObfuscation = parameterObfuscation;
        if (parameterObfuscation) {
            this.featureName = "Method Parameter Obfuscation";
        } else {
            this.featureName = "Method Parameter List Changing";
        }

        this.featureNameLower = this.featureName.toLowerCase();
        if (classRepository1.hasProgramClasses()) {
            if (list1 != null && list1.size() != 0) {
                this.initExcludeAll(classRepository1.enumerateProgramClasses(), classRepository1.getClassCount());
            } else {
                this.initIncludeAll(classRepository1.enumerateProgramClasses(), classRepository1.getClassCount());
            }

            this.applyScriptParameters();
            this.excludeTrimExcludedMethods(classMemberSets);
            this.excludeMethods(set1, "Determined to be accessed by Reflection");
            this.excludeMethodsWithReasons(map1);
            this.excludeMethods(nameExclusionSet.getPrefixOnlyLinkedMethods(), "Specified as having a retained prefix");
            if (parameterObfuscation) {
                this.excludeMethods(set2, "Referenced by invokedynamic");
                this.excludeMethods(set3, "Referenced in bootstrap initialization");
            } else {
                HashSet hashSet = new HashSet(set2);
                if (!HiddenOptionFlags.FOLLOW_LAMBDA_METHOD_HANDLES) {
                    this.excludeMethods(set2, "Referenced by invokedynamic");
                } else {
                    HashSet hashSet1 = new HashSet(13);
                    Iterator iterator = set2.iterator();

                    while (iterator.hasNext()) {
                        MethodInfo methodInfo1 = (MethodInfo) iterator.next();
                        if (!methodInfo1.isLambdaImplementation() || methodInfo1.getLambdaArgCount() == -1) {
                            hashSet.remove(methodInfo1);
                            hashSet1.add(methodInfo1);
                        }
                    }

                    if (!hashSet1.isEmpty()) {
                        this.excludeMethods(hashSet1, "Referenced by invokedynamic");
                    }
                }

                HashSet hashSet2 = new HashSet(set3);
                hashSet2.removeAll(hashSet);
                this.excludeMethods(hashSet2, "Referenced in bootstrap initialization");
            }

            this.excludeReflectionAccessed(reflectionAccessMatcher);
            this.excludeFixedClassMethods(fixedClassesExclusionSet1, listMultimap);
            if (ba != 1) {
                this.excludeSyntheticAccessors(classRepository1.enumerateProgramClasses(), ba, nameExclusionSet);
            }

            this.excludeMultiReleaseMethods(classRepository1.enumerateProgramClasses());
            this.excludeRecordMethods(classRepository1.enumerateProgramClasses());
            this.hasExplicitExclusions = classMemberSets != null || list1 != null && list1.size() > 0 || list2 != null && list2.size() > 0;
        } else {
            this.hasExplicitExclusions = false;
        }
    }

    @Override
    public final void unexcludeClass(ProgramClass programClass1, String string) throws ZkmException, IOException {
        if (super.excludedClasses.remove(programClass1) != null) {
            super.includedClasses.put(programClass1, programClass1);
            if (super.scriptEnvironment.isVerbose()) {
                super.logWriter
                        .println(
                                "\tMatched as excluded from possible method parameter changes \"" + this.describeClass(programClass1) + "\" because of \"" + string + "\""
                        );
            }
        }
    }

    public final void excludeLinkedMethod(MethodInfo methodInfo1, MethodInfo methodInfo2) throws ZkmException, IOException {
        if (!methodInfo1.isStaticInitializer()) {
            ProgramClass programClass1 = (ProgramClass) this.excludedMethods.remove(methodInfo1);
            if (programClass1 != null) {
                this.includedMethods.put(methodInfo1, programClass1);
                if (super.scriptEnvironment.isVerbose() && super.logWriter != null) {
                    super.logWriter
                            .println(
                                    "\tEXcluding from possible "
                                            + this.featureNameLower
                                            + " method \""
                                            + AbstractExclusionSpec.formatMethod(methodInfo1, false, this.classRepository)
                                            + "\" in class \""
                                            + AbstractExclusionSpec.formatClass(methodInfo1.getOwnerProgramClass(), this.classRepository, false)
                                            + "\" because method \""
                                            + AbstractExclusionSpec.formatMethod(methodInfo2, false, this.classRepository)
                                            + "\" in class \""
                                            + AbstractExclusionSpec.formatClass(methodInfo2.getOwnerProgramClass(), this.classRepository, false)
                                            + "\" has been excluded"
                            );
                }
            }
        }
    }

    public final void includeMethod(MethodInfo methodInfo1, String string) throws ZkmException, IOException {
        if (!methodInfo1.isStaticInitializer()) {
            ProgramClass programClass1 = (ProgramClass) this.includedMethods.remove(methodInfo1);
            if (programClass1 != null) {
                this.excludedMethods.put(methodInfo1, programClass1);
                super.scriptEnvironment
                        .logWarning(
                                "Including method \""
                                        + AbstractExclusionSpec.formatMethodWithModifiers(methodInfo1, this)
                                        + "\" in class \""
                                        + this.describeClass(methodInfo1.getOwnerProgramClass())
                                        + "\" for possible "
                                        + this.featureNameLower
                                        + " because \""
                                        + string
                                        + "\""
                        );
            }
        }
    }

    public ParameterListStatement parseDefaultExclusions(ScriptEnvironment scriptEnvironment1, BufferedReader bufferedReader) throws ZkmException, ZkmScriptParseException, IOException {
        ZkmScriptParser zkmScriptParser = new ZkmScriptParser(bufferedReader);

        ZkmScriptSimpleNode zkmScriptSimpleNode;
        try {
            if (this.isParameterObfuscation()) {
                zkmScriptSimpleNode = zkmScriptParser.DefaultMethodParameterObfuscationExcludeInput();
            } else {
                zkmScriptSimpleNode = zkmScriptParser.DefaultMethodParameterChangesExcludeInput();
            }

            zkmScriptSimpleNode.execute(null, scriptEnvironment1);
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException iOException) {
            }
        }

        ParameterListStatement parameterListStatement;
        if (this.isParameterObfuscation()) {
            parameterListStatement = ((ASTDefaultMethodParameterObfuscationExcludeInput) zkmScriptSimpleNode).getParameterListStatement();
        } else {
            parameterListStatement = ((ASTDefaultMethodParameterChangesExcludeInput) zkmScriptSimpleNode).getParameterListStatement();
        }

        return parameterListStatement;
    }

    public void excludeSyntheticAccessors(Enumeration enumeration, int ba, NameExclusionSet nameExclusionSet) throws ZkmException, IOException {
        String string = "'keepInnerClassInfo' specified as '" + (ba == 0 ? "true" : "ifNameNotObfuscated") + "'";

        while (enumeration.hasMoreElements()) {
            ProgramClass programClass1 = (ProgramClass) enumeration.nextElement();

            for (MethodInfo methodInfo1 : programClass1.getMethodInfos()) {
                if (methodInfo1.isSynthetic() && !methodInfo1.isRenamed()) {
                    String string1 = methodInfo1.getSourceName();
                    if (string1.startsWith("access$") && NumericStringUtil.isInteger(string1.substring("access$".length()))) {
                        if (ba != 0
                                && ba == 2
                                && !nameExclusionSet.isPackageExcluded(programClass1.getPackagePath())
                                && !nameExclusionSet.isClassExcluded(programClass1)) {
                        }

                        this.excludeMethod(methodInfo1, string);
                    }
                }
            }
        }
    }

    public void excludeReflectionAccessed(ReflectionAccessMatcher reflectionAccessMatcher) throws ZkmException, IOException {
        if (reflectionAccessMatcher != null) {
            Enumeration enumeration = reflectionAccessMatcher.getExcludedMethods();

            while (enumeration.hasMoreElements()) {
                MethodInfo methodInfo1 = (MethodInfo) enumeration.nextElement();
                if (isEligible(methodInfo1)) {
                    this.excludeMethod(methodInfo1, "Specified as accessed by Reflection");
                }
            }
        }
    }

    public final void excludeMethod(MethodInfo methodInfo1, String string) throws ZkmException, IOException {
        ProgramClass programClass1 = (ProgramClass) this.excludedMethods.remove(methodInfo1);
        if (programClass1 != null) {
            this.includedMethods.put(methodInfo1, programClass1);
            if (super.scriptEnvironment.isVerbose() && super.logWriter != null) {
                ProgramClass programClass2 = methodInfo1.getOwnerProgramClass();
                super.logWriter
                        .println(
                                "\tMatched as EXcluded from possible "
                                        + this.featureNameLower
                                        + " \""
                                        + AbstractExclusionSpec.formatMethodWithModifiers(methodInfo1, this)
                                        + "\" in class \""
                                        + this.describeClass(programClass2)
                                        + "\" because of \""
                                        + string
                                        + "\""
                        );
            }
        }
    }

    public final void includeMatchedMethod(MethodInfo methodInfo1, String string) throws ZkmException, IOException {
        ProgramClass programClass1 = (ProgramClass) this.includedMethods.remove(methodInfo1);
        if (programClass1 != null) {
            this.excludedMethods.put(methodInfo1, programClass1);
            if (super.scriptEnvironment.isVerbose() && super.logWriter != null) {
                ProgramClass programClass2 = methodInfo1.getOwnerProgramClass();
                super.logWriter
                        .println(
                                "\tMatched as included for possible "
                                        + this.featureNameLower
                                        + " \""
                                        + AbstractExclusionSpec.formatMethodWithModifiers(methodInfo1, this)
                                        + "\" in class \""
                                        + this.describeClass(programClass2)
                                        + "\" because of \""
                                        + string
                                        + "\""
                        );
            }
        }
    }

    public ParameterListStatement parseInternalDefaultExclusions(ScriptEnvironment scriptEnvironment1, Throwable throwable) throws ZkmException, IOException {
        String string;
        if (this.parameterObfuscation) {
            string = scriptEnvironment1.getDefaultParamObfuscationExcludeFile();
        } else {
            string = scriptEnvironment1.getDefaultParamChangesExcludeFile();
        }

        if (throwable != null) {
            System.err.println("\"" + string + "\" had a parse error and will be ignored. See \"" + scriptEnvironment1.getLogFileName() + "\" for more detail.");
            scriptEnvironment1.logWarning(
                    "\""
                            + string
                            + "\" had a parse error :"
                            + HiddenOptionFlags.LINE_SEPARATOR
                            + throwable.getMessage()
                            + HiddenOptionFlags.LINE_SEPARATOR
                            + "Will use the internal default method parameter "
                            + (this.parameterObfuscation ? "obfuscation" : "changes")
                            + " statement and ignore \""
                            + string
                            + "\"."
            );
        }

        String string1 = (this.parameterObfuscation ? "methodParameterObfuscationExclude" : "methodParameterChangesExclude")
                + (this.parameterObfuscation ? DEFAULT_OBFUSCATION_EXCLUDE : DEFAULT_CHANGES_EXCLUDE);
        StringReader stringReader = new StringReader(string1);
        BufferedReader bufferedReader = new BufferedReader(stringReader);

        try {
            return this.parseDefaultExclusions(scriptEnvironment1, bufferedReader);
        } catch (ZkmScriptParseException zkmScriptParseException) {
        } catch (ZkmScriptTokenMgrError zkmScriptTokenMgrError) {
        }

        return null;
    }

    public void excludeMethods(Set set1, String string) throws ZkmException, IOException {
        Iterator iterator = set1.iterator();

        while (iterator.hasNext()) {
            AbstractMethodInfo abstractMethodInfo = (AbstractMethodInfo) iterator.next();
            if (isEligible(abstractMethodInfo)) {
                this.excludeMethod((MethodInfo) abstractMethodInfo, string);
            }
        }
    }

    public void includeChangeLogChangedMethods(
            ChangeLogMapping changeLogMapping1, MethodOverrideAnalyzer methodOverrideAnalyzer, FixedClassesExclusionSet fixedClassesExclusionSet1
    ) throws ZkmException, IOException {
        if (changeLogMapping1 != null) {
            List list1 = changeLogMapping1.getMethodsWithAddedParameters();
            this.includeMethodsAndLinked(
                    list1, "Input ChangeLog specifies changed parameters", changeLogMapping1, methodOverrideAnalyzer, fixedClassesExclusionSet1
            );
        }
    }

    public static String getPackedDescriptor(MethodInfo methodInfo1) {
        return "([Ljava/lang/Object;)" + methodInfo1.getReturnDescriptor();
    }

    @Override
    public final boolean excludeClass(ProgramClass programClass1, String string) throws ZkmException, IOException {
        Object object = super.includedClasses.remove(programClass1);
        if (object != null) {
            super.excludedClasses.put(programClass1, programClass1);
            if (super.scriptEnvironment.isVerbose() && super.logWriter != null) {
                super.logWriter
                        .println(
                                "\tMatched as included for possible method parameter changes \"" + this.describeClass(programClass1) + "\" because of \"" + string + "\""
                        );
            }
        }

        return object != null;
    }

    public boolean isParameterObfuscation() {
        return this.parameterObfuscation;
    }

    public void warnHigherLevelIgnored(ASTRenameFilterParameter aSTRenameFilterParameter, String string) {
        if (aSTRenameFilterParameter.hasCaretTag()) {
            super.scriptEnvironment
                    .logWarning(
                            this.featureName
                                    + " : Parameter '"
                                    + aSTRenameFilterParameter
                                    + "' in '"
                                    + string
                                    + "' cannot match higher levels (e.g. containing class or package). The higher level specification will be ignored.",
                            true
                    );
        }
    }

    public static boolean isEligible(AbstractMethodInfo abstractMethodInfo) {
        return abstractMethodInfo.isProgramMember()
                && !abstractMethodInfo.isNative()
                && !abstractMethodInfo.isStaticInitializer()
                && abstractMethodInfo.getParameterCount() + (abstractMethodInfo.isStatic() ? 0 : 1) <= 253
                && (
                !abstractMethodInfo.isPublic()
                        || !abstractMethodInfo.isStatic()
                        || !abstractMethodInfo.getSignature().equals(MainMethodConstants.MAIN_METHOD_SIGNATURE)
        );
    }

    public void includeChangeLogObfuscatedMethods(
            ChangeLogMapping changeLogMapping1, MethodOverrideAnalyzer methodOverrideAnalyzer, FixedClassesExclusionSet fixedClassesExclusionSet1
    ) throws ZkmException, IOException {
        if (changeLogMapping1 != null) {
            Set set1 = changeLogMapping1.getParameterObfuscatedMethods();
            if (set1 != null) {
                this.includeMethodsAndLinked(
                        set1, "Input ChangeLog specifies obfuscated parameters", changeLogMapping1, methodOverrideAnalyzer, fixedClassesExclusionSet1
                );
            }
        }
    }

    public void excludeUnobfuscatedNames(Enumeration enumeration) throws ZkmException, IOException {
        while (enumeration.hasMoreElements()) {
            ProgramClass programClass1 = (ProgramClass) enumeration.nextElement();

            for (MethodInfo methodInfo1 : programClass1.getMethodInfos()) {
                if (isEligible(methodInfo1)
                        && (!methodInfo1.isConstructor() && !methodInfo1.isRenamed() || methodInfo1.isConstructor() && !programClass1.isRenamed())) {
                    this.excludeUnobfuscatedMethod(
                            methodInfo1,
                            methodInfo1.isConstructor()
                                    ? "No trimExclude, methodParameterChangesInclude or methodParameterChangesExclude statements active and class name not obfuscated"
                                    : "No trimExclude, methodParameterChangesInclude or methodParameterChangesExclude statements active and method name not obfuscated"
                    );
                }
            }
        }
    }

    public final void restoreMethod(MethodInfo methodInfo1) {
        ProgramClass programClass1 = (ProgramClass) this.includedMethods.remove(methodInfo1);
        if (programClass1 != null) {
            this.excludedMethods.put(methodInfo1, programClass1);
        }
    }

    public final void initIncludeAll(Enumeration enumeration, int ba) {
        this.initMaps(ba);

        while (enumeration.hasMoreElements()) {
            ProgramClass programClass1 = (ProgramClass) enumeration.nextElement();
            super.excludedClasses.put(programClass1, programClass1);
            ArrayEnumeration arrayEnumeration = programClass1.enumerateMethods();

            while (arrayEnumeration.hasMoreElements()) {
                MethodInfo methodInfo1 = (MethodInfo) arrayEnumeration.nextElement();
                if (isEligible(methodInfo1)) {
                    this.excludedMethods.put(methodInfo1, methodInfo1.getOwnerProgramClass());
                } else {
                    this.includedMethods.put(methodInfo1, methodInfo1.getOwnerProgramClass());
                }
            }
        }
    }

    public final void applyScriptParameters() throws ZkmException, IOException {
        int ba;
        if (super.exclusionStatements == null) {
            ba = 0;
        } else {
            ba = super.exclusionStatements.size();
        }

        HashSet hashSet = ZkmUtils.createHashSet();
        ArrayList arrayList = new ArrayList();

        for (int i = 0; i < ba; i++) {
            ParameterListStatement parameterListStatement = (ParameterListStatement) super.exclusionStatements.get(i);
            Enumeration enumeration = parameterListStatement.getRenameFilterParameters();

            while (enumeration.hasMoreElements()) {
                ASTRenameFilterParameter aSTRenameFilterParameter = (ASTRenameFilterParameter) enumeration.nextElement();
                if (hashSet.add(aSTRenameFilterParameter)) {
                    arrayList.add(aSTRenameFilterParameter);
                }
            }
        }

        if (super.scriptEnvironment.isVerbose() && arrayList.size() > 0) {
            super.logWriter.println("\t" + (this.parameterObfuscation ? "methodParameterObfuscationInclude" : "methodParameterChangesInclude") + " parameters:");

            for (int i = arrayList.size() - 1; i >= 0; i += -1) {
                super.logWriter.println("\t\t" + arrayList.get(i));
            }
        }

        for (int i = arrayList.size() - 1; i >= 0; i += -1) {
            ASTRenameFilterParameter aSTRenameFilterParameter4 = (ASTRenameFilterParameter) arrayList.get(i);
            if (this.isValidExclusionParameter(
                    aSTRenameFilterParameter4, this.parameterObfuscation ? "methodParameterObfuscationInclude" : "methodParameterChangesInclude"
            )) {
                this.warnHigherLevelIgnored(
                        aSTRenameFilterParameter4, this.parameterObfuscation ? "methodParameterObfuscationInclude" : "methodParameterChangesInclude"
                );
                aSTRenameFilterParameter4.applyMethodParameterInclusion(this);
            }
        }

        int be;
        if (super.unexclusionStatements == null) {
            be = 0;
        } else {
            be = super.unexclusionStatements.size();
        }

        HashSet hashSet1 = ZkmUtils.createHashSet();
        ArrayList arrayList1 = new ArrayList();

        for (int i = 0; i < be; i++) {
            ParameterListStatement parameterListStatement1 = (ParameterListStatement) super.unexclusionStatements.get(i);
            Enumeration enumeration1 = parameterListStatement1.getRenameFilterParameters();

            while (enumeration1.hasMoreElements()) {
                ASTRenameFilterParameter aSTRenameFilterParameter1 = (ASTRenameFilterParameter) enumeration1.nextElement();
                if (hashSet1.add(aSTRenameFilterParameter1)) {
                    arrayList1.add(aSTRenameFilterParameter1);
                }
            }
        }

        if (super.scriptEnvironment.isVerbose() && arrayList1.size() > 0) {
            super.logWriter.println("\t" + (this.parameterObfuscation ? "methodParameterObfuscationExclude" : "methodParameterChangesExclude") + " parameters:");

            for (int i = arrayList1.size() - 1; i >= 0; i += -1) {
                super.logWriter.println("\t\t\"" + arrayList1.get(i) + "\"");
            }
        }

        for (int i = arrayList1.size() - 1; i >= 0; i += -1) {
            ASTRenameFilterParameter aSTRenameFilterParameter5 = (ASTRenameFilterParameter) arrayList1.get(i);
            if (this.isValidExclusionParameter(
                    aSTRenameFilterParameter5, this.parameterObfuscation ? "methodParameterObfuscationExclude" : "methodParameterChangesExclude"
            )) {
                this.warnHigherLevelIgnored(
                        aSTRenameFilterParameter5, this.parameterObfuscation ? "methodParameterObfuscationExclude" : "methodParameterChangesExclude"
                );
                aSTRenameFilterParameter5.applyMethodParameterExclusion(this);
            }
        }

        ParameterListStatement parameterListStatement2 = this.loadDefaultExclusions(super.scriptEnvironment);
        if (parameterListStatement2 != null) {
            HashMap hashMap = ZkmUtils.createHashMap();
            ArrayList arrayList2 = new ArrayList();
            Enumeration enumeration2 = parameterListStatement2.getRenameFilterParameters();

            while (enumeration2.hasMoreElements()) {
                ASTRenameFilterParameter aSTRenameFilterParameter2 = (ASTRenameFilterParameter) enumeration2.nextElement();
                if (!hashMap.containsKey(aSTRenameFilterParameter2)) {
                    hashMap.put(aSTRenameFilterParameter2, aSTRenameFilterParameter2);
                    arrayList2.add(aSTRenameFilterParameter2);
                }
            }

            Collections.sort(arrayList2);
            if (super.scriptEnvironment.isVerbose() && arrayList2.size() > 0) {
                super.logWriter.println("\tDefault method parameter " + (this.parameterObfuscation ? "obfuscation" : "changes") + " exclusion parameters:");

                for (int i = arrayList2.size() - 1; i >= 0; i += -1) {
                    super.logWriter.println("\t\t\"" + arrayList2.get(i) + "\"");
                }
            }

            for (int i = arrayList2.size() - 1; i >= 0; i += -1) {
                ASTRenameFilterParameter aSTRenameFilterParameter3 = (ASTRenameFilterParameter) arrayList2.get(i);
                if (this.isValidExclusionParameter(
                        aSTRenameFilterParameter3, this.parameterObfuscation ? "methodParameterObfuscationExclude" : "methodParameterChangesExclude"
                )) {
                    this.warnHigherLevelIgnored(
                            aSTRenameFilterParameter3, this.parameterObfuscation ? "methodParameterObfuscationExclude" : "methodParameterChangesExclude"
                    );
                    aSTRenameFilterParameter3.applyMethodParameterExclusion(this);
                }
            }
        }
    }

    public boolean isValidExclusionParameter(ASTRenameFilterParameter aSTRenameFilterParameter, String string) {
        if (!aSTRenameFilterParameter.isMethodSpecifier()) {
            super.scriptEnvironment
                    .logWarning(
                            this.featureName
                                    + " : Parameter '"
                                    + aSTRenameFilterParameter
                                    + "' in '"
                                    + string
                                    + "' statement is not a method exclusion parameter. It will be ignored.",
                            true
                    );
            return false;
        } else if (aSTRenameFilterParameter.isClassSpecifier() && aSTRenameFilterParameter.hasLinkClassName()) {
            super.scriptEnvironment
                    .logWarning(
                            this.featureName
                                    + " : Parameter '"
                                    + aSTRenameFilterParameter
                                    + "' in '"
                                    + string
                                    + "' statement cannot use the '<link>' syntax. It will be ignored.",
                            true
                    );
            return false;
        } else if (aSTRenameFilterParameter.isMethodSpecifier() && aSTRenameFilterParameter.hasLinkMethodSignature()) {
            super.scriptEnvironment
                    .logWarning(
                            this.featureName
                                    + " : Parameter '"
                                    + aSTRenameFilterParameter
                                    + "' in '"
                                    + string
                                    + "' statement cannot use the '<link>' syntax. It will be ignored.",
                            true
                    );
            return false;
        } else if (aSTRenameFilterParameter.isMethodSpecifier() && aSTRenameFilterParameter.hasPlusSignatureClasses()) {
            super.scriptEnvironment
                    .logWarning(
                            this.featureName
                                    + " : Parameter '"
                                    + aSTRenameFilterParameter
                                    + "' in '"
                                    + string
                                    + "' cannot use the '"
                                    + "+signatureClasses"
                                    + "' keyword. It will be ignored.",
                            true
                    );
            return false;
        } else {
            return true;
        }
    }

    public void excludeMultiReleaseMethods(Enumeration enumeration) throws ZkmException, IOException {
        while (enumeration.hasMoreElements()) {
            ProgramClass programClass1 = (ProgramClass) enumeration.nextElement();
            if (programClass1.isMultiRelease()) {
                for (MethodInfo methodInfo1 : programClass1.getMethodInfos()) {
                    if (isEligible(methodInfo1)) {
                        this.excludeMethod(methodInfo1, "Multi-release versions of containing class");
                    }
                }
            }
        }
    }

    public void applyDefaultNameExclusions() throws ZkmException, IOException {
        if (!this.hasExplicitExclusions) {
            this.excludeUnobfuscatedNames(this.classRepository.enumerateProgramClasses());
        }
    }

    public static String getPackedSignatureDescriptor(MethodSignature methodSignature1) {
        return "([Ljava/lang/Object;)" + methodSignature1.getReturnDescriptor();
    }

    public void initMaps(int ba) {
        super.includedClasses = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba));
        super.excludedClasses = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba));
        this.includedMethods = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba * 5));
        this.excludedMethods = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba * 5));
    }

    public final void excludeUnobfuscatedMethod(MethodInfo methodInfo1, String string) throws ZkmException, IOException {
        ProgramClass programClass1 = (ProgramClass) this.excludedMethods.remove(methodInfo1);
        if (programClass1 != null) {
            this.includedMethods.put(methodInfo1, programClass1);
            if (super.scriptEnvironment.isVerbose() && super.logWriter != null) {
                super.logWriter
                        .println(
                                "\tEXcluding from possible "
                                        + this.featureNameLower
                                        + " method \""
                                        + AbstractExclusionSpec.formatMethodWithModifiers(methodInfo1, this)
                                        + "\" in class \""
                                        + AbstractExclusionSpec.formatClass(methodInfo1.getOwnerProgramClass(), this.classRepository, false)
                                        + "\" because of \""
                                        + string
                                        + "\""
                        );
            }
        }
    }

    public void excludeMethodsWithReasons(Map map1) throws ZkmException, IOException {
        Iterator iterator = map1.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            MethodInfo methodInfo1 = (MethodInfo) entry.getKey();
            if (isEligible(methodInfo1)) {
                this.excludeMethod(methodInfo1, (String) entry.getValue());
            }
        }
    }

    public final void initExcludeAll(Enumeration enumeration, int ba) {
        this.initMaps(ba);

        while (enumeration.hasMoreElements()) {
            ProgramClass programClass1 = (ProgramClass) enumeration.nextElement();
            super.includedClasses.put(programClass1, programClass1);
            ArrayEnumeration arrayEnumeration = programClass1.enumerateMethods();

            while (arrayEnumeration.hasMoreElements()) {
                MethodInfo methodInfo1 = (MethodInfo) arrayEnumeration.nextElement();
                this.includedMethods.put(methodInfo1, methodInfo1.getOwnerProgramClass());
            }
        }
    }

    public ParameterListStatement loadDefaultExclusions(ScriptEnvironment scriptEnvironment1) throws ZkmException, IOException {
        String string;
        if (this.parameterObfuscation) {
            string = scriptEnvironment1.getDefaultParamObfuscationExcludeFile();
        } else {
            string = scriptEnvironment1.getDefaultParamChangesExcludeFile();
        }

        boolean bl = false;

        BufferedReader bufferedReader;
        try {
            File file1 = new File(string);
            bufferedReader = ZkmFileUtils.openReader(file1, HiddenOptionFlags.SCRIPT_ENCODING);
            scriptEnvironment1.logLine(
                    "Using '" + string + "' for default method parameter " + (this.parameterObfuscation ? "obfuscation" : "changes") + " exclusions"
            );
        } catch (FileNotFoundException fileNotFoundException) {
            String string1 = (this.parameterObfuscation ? "methodParameterObfuscationExclude" : "methodParameterChangesExclude")
                    + (this.parameterObfuscation ? DEFAULT_OBFUSCATION_EXCLUDE : DEFAULT_CHANGES_EXCLUDE);
            StringReader stringReader = new StringReader(string1);
            bufferedReader = new BufferedReader(stringReader);
            bl = true;
            scriptEnvironment1.logLine(
                    "Using internal default method parameter "
                            + (this.parameterObfuscation ? "obfuscation" : "changes")
                            + " exclusions. File '"
                            + string
                            + "' not found"
            );
        } catch (IOException iOException) {
            String string2 = (this.parameterObfuscation ? "methodParameterObfuscationExclude" : "methodParameterChangesExclude")
                    + (this.parameterObfuscation ? DEFAULT_OBFUSCATION_EXCLUDE : DEFAULT_CHANGES_EXCLUDE);
            StringReader stringReader1 = new StringReader(string2);
            bufferedReader = new BufferedReader(stringReader1);
            bl = true;
            scriptEnvironment1.logLine(
                    "Using internal default method parameter "
                            + (this.parameterObfuscation ? "obfuscation" : "changes")
                            + " exclusions. Error opening file '"
                            + string
                            + "' : "
                            + iOException
            );
        }

        try {
            return this.parseDefaultExclusions(scriptEnvironment1, bufferedReader);
        } catch (ZkmScriptParseException zkmScriptParseException) {
            if (!bl) {
                return this.parseInternalDefaultExclusions(scriptEnvironment1, zkmScriptParseException);
            }
        } catch (ZkmScriptTokenMgrError zkmScriptTokenMgrError) {
            if (!bl) {
                return this.parseInternalDefaultExclusions(scriptEnvironment1, zkmScriptTokenMgrError);
            }
        }

        return null;
    }
}
