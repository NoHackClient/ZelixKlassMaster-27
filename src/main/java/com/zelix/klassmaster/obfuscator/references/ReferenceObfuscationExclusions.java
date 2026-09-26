package com.zelix.klassmaster.obfuscator.references;

import com.zelix.klassmaster.classfile.AbstractFieldInfo;
import com.zelix.klassmaster.classfile.AbstractMethodInfo;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ClassMemberRef;
import com.zelix.klassmaster.classfile.MemberInfo;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.classfile.insn.MethodBytecode;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.exclude.AbstractExclusionSpec;
import com.zelix.klassmaster.obfuscator.exclude.ContainedInSpecList;
import com.zelix.klassmaster.obfuscator.exclude.FixedClassesExclusionSet;
import com.zelix.klassmaster.obfuscator.flow.StaticInitCalleeAnalyzer;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ast.ASTRenameFilterParameter;
import com.zelix.klassmaster.script.parser.ast.ParameterListStatement;
import com.zelix.klassmaster.util.ArrayEnumeration;
import com.zelix.klassmaster.util.NestedMultiMap;
import com.zelix.klassmaster.util.SetMultiMap;
import com.zelix.klassmaster.util.TwoKeySetMultiMap;
import com.zelix.klassmaster.util.VisitableNode;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

public class ReferenceObfuscationExclusions extends ContainedInSpecList {
    private static VisitableNode[] visitableNodes;
    private static long minInitialCapacity;
    public SetMultiMap includedFieldReferences = new SetMultiMap();
    private SetMultiMap includedMethodReferences = new SetMultiMap();
    public SetMultiMap memberReferences;
    public List referencedClasses;

    public boolean isMethodReferenceIncluded(AbstractMethodInfo abstractMethodInfo, Object object) {
        if (!this.includedMethodReferences.containsKey(abstractMethodInfo)) {
            return false;
        } else {
            return this.includedMethodReferences.containsValue(abstractMethodInfo, null)
                    ? true
                    : this.includedMethodReferences.containsValue(abstractMethodInfo, object);
        }
    }

    public Set getReferencingMethods(Object object) {
        return ZkmUtils.createHashSetFrom(this.memberReferences.getValues(object));
    }

    @Override
    public Enumeration getMemberOwnerClasses() {
        return ArrayEnumeration.fromCollection(this.referencedClasses);
    }

    public void warnHigherLevelIgnored(ASTRenameFilterParameter aSTRenameFilterParameter, String string) {
        if (aSTRenameFilterParameter.isContainedInMethodSpec() && aSTRenameFilterParameter.containedInHasCaretTag()) {
            super.scriptEnvironment
                    .logWarning(
                            "Reference Obfuscation : Parameter '"
                                    + aSTRenameFilterParameter
                                    + "' in '"
                                    + string
                                    + "' has a '"
                                    + "containedIn"
                                    + "' clause which matches higher levels. The higher level specification will be ignored.",
                            true
                    );
        }

        if (aSTRenameFilterParameter.isContainedInMethodSpec() && aSTRenameFilterParameter.hasCaretTag()) {
            super.scriptEnvironment
                    .logWarning(
                            "Reference Obfuscation : Parameter '"
                                    + aSTRenameFilterParameter
                                    + "' in '"
                                    + string
                                    + "' cannot match higher levels (e.g. containing class or package). The higher level specification will be ignored.",
                            true
                    );
        }
    }

    public boolean isValidReferenceParameter(ASTRenameFilterParameter aSTRenameFilterParameter, String string) {
        boolean bl = true;
        if (!aSTRenameFilterParameter.isMethodSpecifier() && !aSTRenameFilterParameter.isFieldSpecifier()) {
            super.scriptEnvironment
                    .logWarning(
                            "Reference Obfuscation : Parameter '"
                                    + aSTRenameFilterParameter
                                    + "' in '"
                                    + string
                                    + "' statement is not a field or method exclusion parameter. It will be ignored.",
                            true
                    );
            bl = false;
        }

        if (aSTRenameFilterParameter.isClassSpecifier() && aSTRenameFilterParameter.hasLinkClassName()) {
            super.scriptEnvironment
                    .logWarning(
                            "Reference Obfuscation : Parameter '"
                                    + aSTRenameFilterParameter
                                    + "' in '"
                                    + string
                                    + "' statement cannot use the '<link>' syntax. It will be ignored.",
                            true
                    );
            bl = false;
        }

        if (aSTRenameFilterParameter.isMethodSpecifier() && aSTRenameFilterParameter.hasLinkMethodSignature()) {
            super.scriptEnvironment
                    .logWarning(
                            "Reference Obfuscation : Parameter '"
                                    + aSTRenameFilterParameter
                                    + "' in '"
                                    + string
                                    + "' statement cannot use the '<link>' syntax. It will be ignored.",
                            true
                    );
            bl = false;
        }

        if (aSTRenameFilterParameter.isMethodSpecifier() && aSTRenameFilterParameter.hasPlusSignatureClasses()) {
            super.scriptEnvironment
                    .logWarning(
                            "Reference Obfuscation : Parameter '"
                                    + aSTRenameFilterParameter
                                    + "' in '"
                                    + string
                                    + "' cannot use the '"
                                    + "+signatureClasses"
                                    + "' keyword. It will be ignored.",
                            true
                    );
            bl = false;
        }

        if (!aSTRenameFilterParameter.isContainedInMethodSpec()) {
            super.scriptEnvironment
                    .logWarning(
                            "Reference Obfuscation : Parameter '"
                                    + aSTRenameFilterParameter
                                    + "' in '"
                                    + string
                                    + "' has a '"
                                    + "containedIn"
                                    + "' clause which does not contain a method specification parameter. It will be ignored.",
                            true
                    );
            bl = false;
        }

        if (aSTRenameFilterParameter.isContainedInMethodSpec() && aSTRenameFilterParameter.containedInHasLinkMethod()) {
            super.scriptEnvironment
                    .logWarning(
                            "Reference Obfuscation : Parameter '"
                                    + aSTRenameFilterParameter
                                    + "' in '"
                                    + string
                                    + "' has a '"
                                    + "containedIn"
                                    + "' clause which uses the '<link>' syntax. It will be ignored.",
                            true
                    );
            bl = false;
        }

        if (aSTRenameFilterParameter.isContainedInMethodSpec() && aSTRenameFilterParameter.containedInHasPlusSignature()) {
            super.scriptEnvironment
                    .logWarning(
                            "Reference Obfuscation : Parameter '"
                                    + aSTRenameFilterParameter
                                    + "' in '"
                                    + string
                                    + "' has a '"
                                    + "containedIn"
                                    + "' clause which uses the '"
                                    + "+signatureClasses"
                                    + "' keyword. It will be ignored.",
                            true
                    );
            bl = false;
        }

        return bl;
    }

    public final void excludeMethodReference(AbstractMethodInfo abstractMethodInfo, MethodBytecode methodBytecode1, String string) throws ZkmException, IOException {
        if (this.includedMethodReferences.containsKey(abstractMethodInfo)) {
            if (methodBytecode1 == null) {
                if (super.scriptEnvironment.isVerbose() && super.logWriter != null) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("\tMatched for obfuscate references exclusion method \"");
                    stringBuilder.append(AbstractExclusionSpec.formatMethodWithModifiers(abstractMethodInfo, this));
                    stringBuilder.append("\" in class \"");
                    stringBuilder.append(this.describeClass(abstractMethodInfo.getOwningClass()));
                    stringBuilder.append("\" referenced from any methods because of \"");
                    stringBuilder.append(string);
                    stringBuilder.append("\"");
                    super.logWriter.println(stringBuilder.toString());
                }

                this.markMethodNotIncluded(abstractMethodInfo);
                this.includedMethodReferences.removeKey(abstractMethodInfo);
            } else {
                if (this.includedMethodReferences.containsValue(abstractMethodInfo, null)) {
                    this.includedMethodReferences.removeValue(abstractMethodInfo, null);
                    this.includedMethodReferences.putValueSet(abstractMethodInfo, this.memberReferences.getValues(abstractMethodInfo));
                }

                boolean bl = this.includedMethodReferences.removeValue(abstractMethodInfo, methodBytecode1);
                if (!this.includedMethodReferences.containsKey(abstractMethodInfo)) {
                    this.markMethodNotIncluded(abstractMethodInfo);
                }

                if (bl && super.scriptEnvironment.isVerbose() && super.logWriter != null) {
                    StringBuilder stringBuilder1 = new StringBuilder();
                    stringBuilder1.append("\tMatched for obfuscate references exclusion method \"");
                    stringBuilder1.append(AbstractExclusionSpec.formatMethodWithModifiers(abstractMethodInfo, this));
                    stringBuilder1.append("\" in class \"");
                    stringBuilder1.append(this.describeClass(abstractMethodInfo.getOwningClass()));
                    stringBuilder1.append("\" referenced from \"");
                    stringBuilder1.append(AbstractExclusionSpec.formatMethodWithModifiers(methodBytecode1.getMethod(), this));
                    stringBuilder1.append("\" in class \"");
                    stringBuilder1.append(this.describeClass(methodBytecode1.getOwningClass()));
                    stringBuilder1.append("\"");
                    stringBuilder1.append(" because of \"");
                    stringBuilder1.append(string);
                    stringBuilder1.append("\"");
                    super.logWriter.println(stringBuilder1.toString());
                }
            }
        }
    }

    public NestedMultiMap collectIncludedReferences(
            TwoKeySetMultiMap twoKeySetMultiMap, StaticInitCalleeAnalyzer staticInitCalleeAnalyzer1, FixedClassesExclusionSet fixedClassesExclusionSet1
    ) throws ZkmException, IOException {
        NestedMultiMap nestedMultiMap = new NestedMultiMap(HiddenOptionFlags.USE_PARALLEL);

        for (Iterator iterator = twoKeySetMultiMap.entrySet().iterator(); iterator.hasNext(); iterator.remove()) {
            Entry entry = (Entry) iterator.next();
            ClassMemberRef classMemberRef = (ClassMemberRef) entry.getKey();
            boolean field = classMemberRef.isField();
            MemberInfo memberInfo1 = classMemberRef.getMember();
            if (field && this.isFieldIncluded((AbstractFieldInfo) memberInfo1) || !field && this.isMethodIncluded((AbstractMethodInfo) memberInfo1)) {
                Iterator iterator1 = ((SetMultiMap) entry.getValue()).entrySet().iterator();

                while (iterator1.hasNext()) {
                    Entry entry1 = (Entry) iterator1.next();
                    MethodBytecode methodBytecode1 = (MethodBytecode) entry1.getKey();
                    if (fixedClassesExclusionSet1 != null && fixedClassesExclusionSet1.isMatchedClass((ProgramClass) methodBytecode1.getOwningClass())) {
                        if (super.scriptEnvironment.isVerbose()) {
                            String string = methodBytecode1.getOriginalDottedName();
                            super.scriptEnvironment
                                    .logMessage(
                                            "Reference Obfuscation : References in method '"
                                                    + methodBytecode1.getMethod().toOriginalDisplayString()
                                                    + "' in class '"
                                                    + string
                                                    + "' will not be obfuscated because the containing class '"
                                                    + string
                                                    + "' is specified in a 'fixedClasses' statement."
                                    );
                        }
                    } else if (field && this.isFieldReferenceIncluded((AbstractFieldInfo) memberInfo1, methodBytecode1)
                            || !field && this.isMethodReferenceIncluded((AbstractMethodInfo) memberInfo1, methodBytecode1)) {
                        AbstractMethodInfo abstractMethodInfo = methodBytecode1.getMethod();
                        if (!abstractMethodInfo.isProgramMember()
                                || abstractMethodInfo.isStaticInitializer()
                                || !staticInitCalleeAnalyzer1.isCalledBySubclassInitializer((MethodInfo) abstractMethodInfo)) {
                            nestedMultiMap.addValues(classMemberRef, methodBytecode1, (Collection) entry1.getValue());
                        } else if (super.scriptEnvironment.isVerbose()) {
                            super.scriptEnvironment
                                    .logMessage(
                                            "Reference Obfuscation : References in method '"
                                                    + abstractMethodInfo.toOriginalDisplayString()
                                                    + "' in class '"
                                                    + abstractMethodInfo.getOriginalDottedName()
                                                    + "' will not be obfuscated because the method is called by the static initializer of a super class."
                                    );
                        }
                    }
                }
            }
        }

        return nestedMultiMap;
    }

    public final void markFieldIncluded(AbstractFieldInfo abstractFieldInfo) {
        if (super.unmatchedFields.remove(abstractFieldInfo)) {
            super.matchedFields.add(abstractFieldInfo);
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
            super.logWriter.println("\tobfuscateReferencesInclude parameters:");

            for (int i = arrayList.size() - 1; i >= 0; i += -1) {
                super.logWriter.println("\t\t" + arrayList.get(i));
            }
        }

        for (int i = arrayList.size() - 1; i >= 0; i += -1) {
            ASTRenameFilterParameter aSTRenameFilterParameter2 = (ASTRenameFilterParameter) arrayList.get(i);
            if (this.isValidReferenceParameter(aSTRenameFilterParameter2, "obfuscateReferencesInclude")) {
                this.warnHigherLevelIgnored(aSTRenameFilterParameter2, "obfuscateReferencesInclude");
                aSTRenameFilterParameter2.applyReferenceInclusion(this);
            }
        }

        int be;
        if (super.unexclusionStatements == null) {
            be = 0;
        } else {
            be = super.unexclusionStatements.size();
        }

        HashMap hashMap = ZkmUtils.createHashMap();
        Vector vector = new Vector();

        for (int i = 0; i < be; i++) {
            ParameterListStatement parameterListStatement1 = (ParameterListStatement) super.unexclusionStatements.get(i);
            Enumeration enumeration1 = parameterListStatement1.getRenameFilterParameters();

            while (enumeration1.hasMoreElements()) {
                ASTRenameFilterParameter aSTRenameFilterParameter1 = (ASTRenameFilterParameter) enumeration1.nextElement();
                if (!hashMap.containsKey(aSTRenameFilterParameter1)) {
                    hashMap.put(aSTRenameFilterParameter1, aSTRenameFilterParameter1);
                    vector.addElement(aSTRenameFilterParameter1);
                }
            }
        }

        if (super.scriptEnvironment.isVerbose() && vector.size() > 0) {
            super.logWriter.println("\tobfuscateReferencesExclude parameters:");

            for (int i = vector.size() - 1; i >= 0; i += -1) {
                super.logWriter.println("\t\t\"" + vector.elementAt(i) + "\"");
            }
        }

        for (int i = vector.size() - 1; i >= 0; i += -1) {
            ASTRenameFilterParameter aSTRenameFilterParameter3 = (ASTRenameFilterParameter) vector.elementAt(i);
            if (this.isValidReferenceParameter(aSTRenameFilterParameter3, "obfuscateReferencesExclude")) {
                this.warnHigherLevelIgnored(aSTRenameFilterParameter3, "obfuscateReferencesExclude");
                aSTRenameFilterParameter3.applyReferenceExclusion(this);
            }
        }
    }

    public final void includeFieldReference(AbstractFieldInfo abstractFieldInfo, MethodBytecode methodBytecode1, String string) throws ZkmException, IOException {
        if (this.includedFieldReferences.containsValue(abstractFieldInfo, null)) {
        }

        if (methodBytecode1 == null) {
            this.includedFieldReferences.removeKey(abstractFieldInfo);
        }

        this.markFieldIncluded(abstractFieldInfo);
        if (this.includedFieldReferences.addValue(abstractFieldInfo, methodBytecode1) && super.scriptEnvironment.isVerbose() && super.logWriter != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("\tMatched for obfuscate references inclusion field \"");
            stringBuilder.append(AbstractExclusionSpec.formatFieldWithModifiers(abstractFieldInfo, this));
            stringBuilder.append("\" in class \"");
            stringBuilder.append(this.describeClass(abstractFieldInfo.getOwningClass()));
            if (methodBytecode1 != null) {
                stringBuilder.append("\" referenced from \"");
                stringBuilder.append(AbstractExclusionSpec.formatMethodWithModifiers(methodBytecode1.getMethod(), this));
                stringBuilder.append("\" in class \"");
                stringBuilder.append(this.describeClass(methodBytecode1.getOwningClass()));
                stringBuilder.append("\"");
            } else {
                stringBuilder.append("\" referenced from any methods");
            }

            stringBuilder.append(" because of \"");
            stringBuilder.append(string);
            stringBuilder.append("\"");
            super.logWriter.println(stringBuilder.toString());
        }
    }

    public final void collectReferencedMembers(Set set1, Set set2, Set set3, boolean bl) {
        HashSet hashSet = ZkmUtils.createHashSet();
        Iterator iterator = this.memberReferences.keySet().iterator();

        while (iterator.hasNext()) {
            MemberInfo memberInfo1 = (MemberInfo) iterator.next();
            ClassFileBase classFileBase = memberInfo1.getOwningClass();
            if (memberInfo1.isField()) {
                AbstractFieldInfo abstractFieldInfo = (AbstractFieldInfo) memberInfo1;
                set2.add(abstractFieldInfo);
                if (bl) {
                    this.includedFieldReferences.addValue(abstractFieldInfo, (MethodBytecode) null);
                }
            } else {
                AbstractMethodInfo abstractMethodInfo = (AbstractMethodInfo) memberInfo1;
                set3.add(abstractMethodInfo);
                if (bl) {
                    this.includedMethodReferences.addValue(abstractMethodInfo, (MethodBytecode) null);
                }
            }

            if (memberInfo1.isStatic()) {
                set1.add(classFileBase);
                hashSet.add(classFileBase);
            }
        }

        this.referencedClasses = new ArrayList(hashSet);
    }

    public final void markMethodIncluded(AbstractMethodInfo abstractMethodInfo) {
        if (super.unmatchedMethods.remove(abstractMethodInfo)) {
            super.matchedMethods.add(abstractMethodInfo);
        }
    }

    public final void includeMethodReference(AbstractMethodInfo abstractMethodInfo, MethodBytecode methodBytecode1, String string) throws ZkmException, IOException {
        if (this.includedMethodReferences.containsValue(abstractMethodInfo, null)) {
        }

        if (methodBytecode1 == null) {
            this.includedMethodReferences.removeKey(abstractMethodInfo);
        }

        this.markMethodIncluded(abstractMethodInfo);
        if (this.includedMethodReferences.addValue(abstractMethodInfo, methodBytecode1) && super.scriptEnvironment.isVerbose() && super.logWriter != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("\tMatched for obfuscate references inclusion method \"");
            stringBuilder.append(AbstractExclusionSpec.formatMethodWithModifiers(abstractMethodInfo, this));
            stringBuilder.append("\" in class \"");
            stringBuilder.append(this.describeClass(abstractMethodInfo.getOwningClass()));
            if (methodBytecode1 != null) {
                stringBuilder.append("\" referenced from \"");
                stringBuilder.append(AbstractExclusionSpec.formatMethodWithModifiers(methodBytecode1.getMethod(), this));
                stringBuilder.append("\" in class \"");
                stringBuilder.append(this.describeClass(methodBytecode1.getOwningClass()));
                stringBuilder.append("\"");
            } else {
                stringBuilder.append("\" referenced from any methods");
            }

            stringBuilder.append(" because of \"");
            stringBuilder.append(string);
            stringBuilder.append("\"");
            super.logWriter.println(stringBuilder.toString());
        }
    }

    public void initSets(int ba) {
        super.unmatchedClasses = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(ba));
        super.matchedClasses = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(ba));
        super.unmatchedFields = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(ba * 5));
        super.matchedFields = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(ba * 5));
        super.unmatchedMethods = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(ba * 5));
        super.matchedMethods = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(ba * 5));
    }

    public final void includeAllMethodReferences(AbstractMethodInfo abstractMethodInfo, String string) throws ZkmException, IOException {
        if (this.includedMethodReferences.addValue(abstractMethodInfo, (MethodBytecode) null)) {
            if (HiddenOptionFlags.LOG_REFERENCE_INCLUSION_MATCHES && super.scriptEnvironment.isVerbose() && super.logWriter != null) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("\tMatched for obfuscate references inclusion method \"");
                stringBuilder.append(AbstractExclusionSpec.formatMethodWithModifiers(abstractMethodInfo, this));
                stringBuilder.append("\" in class \"");
                stringBuilder.append(this.describeClass(abstractMethodInfo.getOwningClass()));
                stringBuilder.append("\" referenced from any methods because of \"");
                stringBuilder.append(string);
                stringBuilder.append("\"");
                super.logWriter.println(stringBuilder.toString());
            }

            super.matchedMethods.add(abstractMethodInfo);
        }
    }

    public final boolean isFieldIncluded(Object object) {
        return this.includedFieldReferences.containsKey(object);
    }

    public static void setVisitableNodes() {
        visitableNodes = null;
    }

    public static VisitableNode[] getVisitableNodes() {
        return visitableNodes;
    }

    public boolean isFieldReferenceIncluded(AbstractFieldInfo abstractFieldInfo, Object object) {
        if (!this.includedFieldReferences.containsKey(abstractFieldInfo)) {
            return false;
        } else {
            return this.includedFieldReferences.containsValue(abstractFieldInfo, null)
                    ? true
                    : this.includedFieldReferences.containsValue(abstractFieldInfo, object);
        }
    }

    public final void markMethodNotIncluded(AbstractMethodInfo abstractMethodInfo) {
        if (super.matchedMethods.remove(abstractMethodInfo)) {
            super.unmatchedMethods.add(abstractMethodInfo);
        }
    }

    public ReferenceObfuscationExclusions(
            ClassRepository classRepository1, SetMultiMap setMultiMap, List list1, List list2, ScriptEnvironment scriptEnvironment1
    ) throws ZkmException, IOException {
        super(classRepository1, list1, list2, scriptEnvironment1);
        this.memberReferences = new SetMultiMap(setMultiMap.getKeyCount());
        Iterator iterator = setMultiMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            this.memberReferences.addValues(((ClassMemberRef) entry.getKey()).getMember(), (Collection) entry.getValue());
        }

        if (classRepository1.hasProgramClasses()) {
            label40:
            {
                int ba = this.memberReferences.getKeyCount() / 5 + this.memberReferences.getKeyCount();
                int bb = Math.max(ba, (int) minInitialCapacity);
                this.initSets(bb);
                ReferenceObfuscationExclusions referenceObfuscationExclusions1;
                HashSet hashSet;
                if (list1 != null && list1.size() != 0) {
                    referenceObfuscationExclusions1 = this;
                    hashSet = super.unmatchedClasses;
                } else if (list2 != null) {
                    if (list2.size() > 0) {
                        this.collectReferencedMembers(super.matchedClasses, super.matchedFields, super.matchedMethods, true);
                        break label40;
                    }

                    referenceObfuscationExclusions1 = this;
                    hashSet = super.unmatchedClasses;
                } else {
                    referenceObfuscationExclusions1 = this;
                    hashSet = super.unmatchedClasses;
                }

                referenceObfuscationExclusions1.collectReferencedMembers(hashSet, super.unmatchedFields, super.unmatchedMethods, false);
            }

            this.applyScriptParameters();
            this.memberReferences = null;
        } else {
            this.memberReferences = null;
        }

        this.referencedClasses = null;
    }

    public final void excludeFieldReference(AbstractFieldInfo abstractFieldInfo, MethodBytecode methodBytecode1, String string) throws ZkmException, IOException {
        if (this.includedFieldReferences.containsKey(abstractFieldInfo)) {
            if (methodBytecode1 == null) {
                if (super.scriptEnvironment.isVerbose() && super.logWriter != null) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("\tMatched for obfuscate references exclusion field \"");
                    stringBuilder.append(AbstractExclusionSpec.formatFieldWithModifiers(abstractFieldInfo, this));
                    stringBuilder.append("\" in class \"");
                    stringBuilder.append(this.describeClass(abstractFieldInfo.getOwningClass()));
                    stringBuilder.append("\" referenced from any methods because of \"");
                    stringBuilder.append(string);
                    stringBuilder.append("\"");
                    super.logWriter.println(stringBuilder.toString());
                }

                this.markFieldNotIncluded(abstractFieldInfo);
                this.includedFieldReferences.removeKey(abstractFieldInfo);
            } else {
                if (this.includedFieldReferences.containsValue(abstractFieldInfo, null)) {
                    this.includedFieldReferences.removeValue(abstractFieldInfo, null);
                    this.includedFieldReferences.putValueSet(abstractFieldInfo, this.memberReferences.getValues(abstractFieldInfo));
                }

                boolean bl = this.includedFieldReferences.removeValue(abstractFieldInfo, methodBytecode1);
                if (!this.includedFieldReferences.containsKey(abstractFieldInfo)) {
                    this.markFieldNotIncluded(abstractFieldInfo);
                }

                if (bl && super.scriptEnvironment.isVerbose() && super.logWriter != null) {
                    StringBuilder stringBuilder1 = new StringBuilder();
                    stringBuilder1.append("\tMatched for obfuscate references exclusion field \"");
                    stringBuilder1.append(AbstractExclusionSpec.formatFieldWithModifiers(abstractFieldInfo, this));
                    stringBuilder1.append("\" in class \"");
                    stringBuilder1.append(this.describeClass(abstractFieldInfo.getOwningClass()));
                    stringBuilder1.append("\" referenced from \"");
                    stringBuilder1.append(AbstractExclusionSpec.formatMethodWithModifiers(methodBytecode1.getMethod(), this));
                    stringBuilder1.append("\" in class \"");
                    stringBuilder1.append(this.describeClass(methodBytecode1.getOwningClass()));
                    stringBuilder1.append("\"");
                    stringBuilder1.append(" because of \"");
                    stringBuilder1.append(string);
                    stringBuilder1.append("\"");
                    super.logWriter.println(stringBuilder1.toString());
                }
            }
        }
    }

    public final void includeAllFieldReferences(AbstractFieldInfo abstractFieldInfo, String string) throws ZkmException, IOException {
        if (this.includedFieldReferences.addValue(abstractFieldInfo, (MethodBytecode) null)) {
            if (HiddenOptionFlags.LOG_REFERENCE_INCLUSION_MATCHES && super.scriptEnvironment.isVerbose() && super.logWriter != null) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("\tMatched for obfuscate references inclusion field \"");
                stringBuilder.append(AbstractExclusionSpec.formatFieldWithModifiers(abstractFieldInfo, this));
                stringBuilder.append("\" in class \"");
                stringBuilder.append(this.describeClass(abstractFieldInfo.getOwningClass()));
                stringBuilder.append("\" referenced from any methods because of \"");
                stringBuilder.append(string);
                stringBuilder.append("\"");
                super.logWriter.println(stringBuilder.toString());
            }

            super.matchedFields.add(abstractFieldInfo);
        }
    }

    public final void markFieldNotIncluded(AbstractFieldInfo abstractFieldInfo) {
        if (super.matchedFields.remove(abstractFieldInfo)) {
            super.unmatchedFields.add(abstractFieldInfo);
        }
    }

    public final boolean isMethodIncluded(Object object) {
        return this.includedMethodReferences.containsKey(object);
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
        setVisitableNodes();
        minInitialCapacity = 1350190181440815204L;
    }
}
