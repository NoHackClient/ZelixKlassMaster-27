package com.zelix.klassmaster.obfuscator.trim;

import com.zelix.klassmaster.classfile.AbstractMethodInfo;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.classfile.insn.MethodBytecode;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.exclude.AbstractExclusionSpec;
import com.zelix.klassmaster.obfuscator.exclude.ContainedInSpecList;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ast.ASTRenameFilterParameter;
import com.zelix.klassmaster.script.parser.ast.ParameterListStatement;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.SetMultiMap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class RemoveMethodCallsHandler extends ContainedInSpecList {
    public ListMultimap callersByMethod;
    public SetMultiMap removalCallers;

    public void warnHigherLevelSpec(ASTRenameFilterParameter aSTRenameFilterParameter, String string) {
        if (aSTRenameFilterParameter.isContainedInMethodSpec() && aSTRenameFilterParameter.containedInHasCaretTag()) {
            super.scriptEnvironment
                    .logWarning(
                            "Remove Method Calls : Parameter '"
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
                            "Remove Method Calls : Parameter '"
                                    + aSTRenameFilterParameter
                                    + "' in '"
                                    + string
                                    + "' cannot match higher levels (e.g. containing class or package). The higher level specification will be ignored.",
                            true
                    );
        }
    }

    public final void excludeMethodCall(AbstractMethodInfo abstractMethodInfo, MethodBytecode methodBytecode1, String string) throws ZkmException, IOException {
        if (this.removalCallers.containsKey(abstractMethodInfo)) {
            if (methodBytecode1 == null) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("\tMatched for remove method calls exclusion method \"");
                stringBuilder.append(AbstractExclusionSpec.formatMethodWithModifiers(abstractMethodInfo, this));
                stringBuilder.append("\" in class \"");
                stringBuilder.append(this.describeClass(abstractMethodInfo.getOwningClass()));
                stringBuilder.append("\" referenced from any methods because of \"");
                stringBuilder.append(string);
                stringBuilder.append("\"");
                super.logWriter.println(stringBuilder.toString());
                this.unmarkForRemoval(abstractMethodInfo);
                this.removalCallers.removeKey(abstractMethodInfo);
            } else {
                SetMultiMap setMultiMap;
                if (this.removalCallers.containsValue(abstractMethodInfo, null)) {
                    this.removalCallers.removeValue(abstractMethodInfo, null);
                    this.removalCallers.addValues(abstractMethodInfo, this.callersByMethod.getValues(abstractMethodInfo));
                    setMultiMap = this.removalCallers;
                } else {
                    setMultiMap = this.removalCallers;
                }

                boolean bl = setMultiMap.removeValue(abstractMethodInfo, methodBytecode1);
                if (!this.removalCallers.containsKey(abstractMethodInfo)) {
                    this.unmarkForRemoval(abstractMethodInfo);
                }

                if (bl && super.scriptEnvironment.isVerbose() && super.logWriter != null) {
                    StringBuilder stringBuilder1 = new StringBuilder();
                    stringBuilder1.append("\tMatched for remove method calls exclusion method \"");
                    stringBuilder1.append(AbstractExclusionSpec.formatMethodWithModifiers(abstractMethodInfo, this));
                    stringBuilder1.append("\" in class \"");
                    stringBuilder1.append(this.describeClass(abstractMethodInfo.getOwningClass()));
                    stringBuilder1.append("\" contained in \"");
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

    @Override
    public Enumeration getMemberOwnerClasses() {
        return null;
    }

    public Set getCallers(AbstractMethodInfo abstractMethodInfo) {
        return ZkmUtils.createHashSetFrom(this.callersByMethod.getValues(abstractMethodInfo));
    }

    public RemoveMethodCallsHandler(ClassRepository classRepository1, ListMultimap listMultimap, List list1, List list2, ScriptEnvironment scriptEnvironment1) throws ZkmException, IOException {
        super(classRepository1, list1, list2, scriptEnvironment1);
        this.callersByMethod = listMultimap;
        if (classRepository1.hasProgramClasses()) {
            label35:
            {
                Set set1 = this.callersByMethod.keySet();
                int ba = ZkmUtils.getPrimeCapacity(set1.size());
                super.unmatchedMethods = ZkmUtils.createHashSet(ba);
                super.matchedMethods = ZkmUtils.createHashSet(ba);
                this.removalCallers = new SetMultiMap(ba);
                HashSet hashSet;
                if (list1 != null && list1.size() != 0) {
                    hashSet = super.unmatchedMethods;
                } else if (list2 != null) {
                    if (list2.size() > 0) {
                        super.matchedMethods.addAll(set1);
                        Iterator iterator = set1.iterator();

                        while (true) {
                            if (!iterator.hasNext()) {
                                break label35;
                            }

                            AbstractMethodInfo abstractMethodInfo = (AbstractMethodInfo) iterator.next();
                            this.removalCallers.addValue(abstractMethodInfo, (MethodBytecode) null);
                        }
                    }

                    hashSet = super.unmatchedMethods;
                } else {
                    hashSet = super.unmatchedMethods;
                }

                hashSet.addAll(set1);
            }

            this.applyStatements();
        }

        this.callersByMethod = null;
    }

    public final void applyStatements() throws ZkmException, IOException {
        int ba = super.exclusionStatements == null ? 0 : super.exclusionStatements.size();
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
            super.logWriter.println("\tremoveMethodCallsInclude parameters:");

            for (int i = arrayList.size() - 1; i >= 0; i += -1) {
                super.logWriter.println("\t\t" + arrayList.get(i));
            }
        }

        for (int i = arrayList.size() - 1; i >= 0; i += -1) {
            ASTRenameFilterParameter aSTRenameFilterParameter2 = (ASTRenameFilterParameter) arrayList.get(i);
            if (this.validateParameter(aSTRenameFilterParameter2, "removeMethodCallsInclude")) {
                this.warnHigherLevelSpec(aSTRenameFilterParameter2, "removeMethodCallsInclude");
                aSTRenameFilterParameter2.applyRemoveMethodCallsInclusion(this);
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
            super.logWriter.println("\tremoveMethodCallsExclude parameters:");

            for (int i = vector.size() - 1; i >= 0; i += -1) {
                super.logWriter.println("\t\t\"" + vector.elementAt(i) + "\"");
            }
        }

        for (int i = vector.size() - 1; i >= 0; i += -1) {
            ASTRenameFilterParameter aSTRenameFilterParameter3 = (ASTRenameFilterParameter) vector.elementAt(i);
            if (this.validateParameter(aSTRenameFilterParameter3, "removeMethodCallsExclude")) {
                this.warnHigherLevelSpec(aSTRenameFilterParameter3, "removeMethodCallsExclude");
                aSTRenameFilterParameter3.applyRemoveMethodCallsExclusion(this);
            }
        }
    }

    public final void markForRemoval(AbstractMethodInfo abstractMethodInfo) {
        if (super.unmatchedMethods.remove(abstractMethodInfo)) {
            super.matchedMethods.add(abstractMethodInfo);
        }
    }

    public boolean validateParameter(ASTRenameFilterParameter aSTRenameFilterParameter, String string) {
        boolean bl = true;
        if (!aSTRenameFilterParameter.isMethodSpecifier()) {
            super.scriptEnvironment
                    .logWarning(
                            "Remove Method Calls : Parameter '"
                                    + aSTRenameFilterParameter
                                    + "' in '"
                                    + string
                                    + "' statement is not a method exclusion parameter. It will be ignored.",
                            true
                    );
            bl = false;
        }

        if (aSTRenameFilterParameter.isClassSpecifier() && aSTRenameFilterParameter.hasLinkClassName()) {
            super.scriptEnvironment
                    .logWarning(
                            "Remove Method Calls : Parameter '"
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
                            "Remove Method Calls : Parameter '"
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
                            "Remove Method Calls : Parameter '"
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
                            "Remove Method Calls : Parameter '"
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
                            "Remove Method Calls : Parameter '"
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
                            "Remove Method Calls : Parameter '"
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

    public final void unmarkForRemoval(AbstractMethodInfo abstractMethodInfo) {
        if (super.matchedMethods.remove(abstractMethodInfo)) {
            super.unmatchedMethods.add(abstractMethodInfo);
        }
    }

    public final void includeMethodCall(AbstractMethodInfo abstractMethodInfo, MethodBytecode methodBytecode1, String string) throws ZkmException, IOException {
        if (this.removalCallers.containsValue(abstractMethodInfo, null)) {
        }

        if (methodBytecode1 == null) {
            this.removalCallers.removeKey(abstractMethodInfo);
        }

        this.markForRemoval(abstractMethodInfo);
        if (this.removalCallers.addValue(abstractMethodInfo, methodBytecode1) && super.scriptEnvironment.isVerbose() && super.logWriter != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("\tMatched for remove method calls inclusion method \"");
            stringBuilder.append(AbstractExclusionSpec.formatMethodWithModifiers(abstractMethodInfo, this));
            stringBuilder.append("\" in class \"");
            stringBuilder.append(this.describeClass(abstractMethodInfo.getOwningClass()));
            StringBuilder stringBuilder1;
            String string1;
            if (methodBytecode1 != null) {
                stringBuilder.append("\" contained in \"");
                stringBuilder.append(AbstractExclusionSpec.formatMethodWithModifiers(methodBytecode1.getMethod(), this));
                stringBuilder.append("\" in class \"");
                stringBuilder.append(this.describeClass(methodBytecode1.getOwningClass()));
                stringBuilder.append("\"");
                stringBuilder1 = stringBuilder;
                string1 = " because of \"";
            } else {
                stringBuilder.append("\" contained in any methods");
                stringBuilder1 = stringBuilder;
                string1 = " because of \"";
            }

            stringBuilder1.append(string1);
            stringBuilder.append(string);
            stringBuilder.append("\"");
            super.logWriter.println(stringBuilder.toString());
        }
    }

    public boolean shouldRemoveCall(AbstractMethodInfo abstractMethodInfo, Object object) {
        return this.removalCallers.containsValue(abstractMethodInfo, null) || this.removalCallers.containsValue(abstractMethodInfo, object);
    }
}
