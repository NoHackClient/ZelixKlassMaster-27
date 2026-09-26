package com.zelix.klassmaster.changelog;

import com.zelix.klassmaster.archive.TempFileManager;
import com.zelix.klassmaster.archive.ZkmFileUtils;
import com.zelix.klassmaster.changelog.parser.ChangeLogSimpleNode;
import com.zelix.klassmaster.classfile.AbstractFieldInfo;
import com.zelix.klassmaster.classfile.AbstractMethodInfo;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.FieldInfo;
import com.zelix.klassmaster.classfile.FieldNameTypeSignature;
import com.zelix.klassmaster.classfile.FieldSignature;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.classfile.hierarchy.ClassMemberLookup;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.classfile.hierarchy.ClasspathClassLoader;
import com.zelix.klassmaster.classfile.hierarchy.CommonSuperTypeResolver;
import com.zelix.klassmaster.classfile.hierarchy.MethodOverrideAnalyzer;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.exclude.NameExclusionSet;
import com.zelix.klassmaster.obfuscator.parameters.AddedParameter;
import com.zelix.klassmaster.obfuscator.parameters.ChangedMethodDescriptor;
import com.zelix.klassmaster.obfuscator.parameters.MethodParameterExclusions;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.CollectionSnapshotEnumeration;
import com.zelix.klassmaster.util.ComparableKeyValue;
import com.zelix.klassmaster.util.EnumerationBackedList;
import com.zelix.klassmaster.util.IdentityValueHolder;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.MutableLong;
import com.zelix.klassmaster.util.NestedMultiMap;
import com.zelix.klassmaster.util.ObjectPair;
import com.zelix.klassmaster.util.ObjectTriple;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.SetMultiMap;
import com.zelix.klassmaster.util.ThreeKeyMultiMap;
import com.zelix.klassmaster.util.TwoKeyMap;
import com.zelix.klassmaster.util.ZkmStringUtils;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Map.Entry;

public class ChangeLogMapping extends AbstractChangeLog {
    public Set parameterObfuscatedMethods;
    public Map removedClassMappings;
    public boolean errorsFound;
    public TwoKeyMap reverseFieldMappings;
    public ObjectTriple methodParameterChangeClasses;
    public int packageCount;
    public int classCount;
    public String autoReflectionClass;
    public boolean parsingComplete;
    public String sourceFileSuffix;
    public ThreeKeyMultiMap reverseMethodMappings;
    public TwoKeyMap methodMappings;
    public TwoKeyMap fieldMappings;
    public Map classMappings;
    public Map paramChangeNodeDataByMethod;
    public Map packageMappings;
    public Map paramChangeNodeDataByClass;
    public NestedMultiMap removedClassFieldMappings;
    public TwoKeyMap reversePackageMappings;
    public NestedMultiMap lineNumberMappings;
    public String referenceObfuscationClass;
    public Map parameterChangeKeysByClass;
    public NestedMultiMap rawMethodMappings;
    public Map parameterChangeKeysByMethod;
    public Map reverseClassMappings;
    public Map sourceNameMappings;
    public boolean flowObfuscationDataPresent;
    public Map addedParametersByMethod;
    public NestedMultiMap rawFieldMappings;
    public Set traceBackEntries = ZkmUtils.createHashSet();
    public SetMultiMap forwardClassEntries = new SetMultiMap();
    public Set flowDataClasses = ZkmUtils.createHashSet();
    public List flowEntryList = new ArrayList();
    public Map memberClassEntries = ZkmUtils.createHashMap();
    public Map moduleAutoReflectionClasses = ZkmUtils.createHashMap();
    public Map moduleReferenceObfuscationClasses = ZkmUtils.createHashMap();
    public Map moduleMethodParameterChangeClasses = ZkmUtils.createHashMap();
    public boolean fieldsHaveTypes = true;
    public boolean methodsHaveReturnTypes = true;
    public final Map canonicalClassTriples = ZkmUtils.createHashMap();
    public String changeLogName;
    public ScriptEnvironment scriptEnvironment;
    public final boolean looseChangeLog;

    public void applyLooseParameterExclusions(MethodParameterExclusions methodParameterExclusions, Set set1, HashMap hashMap) {
        if (this.addedParametersByMethod != null) {
            boolean bl = false;
            HashMap hashMap1 = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(this.addedParametersByMethod.size()));
            Iterator iterator = this.addedParametersByMethod.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                AbstractMethodInfo abstractMethodInfo = (AbstractMethodInfo) entry.getKey();
                AddedParameter[] addedParameters1 = (AddedParameter[]) entry.getValue();
                if (abstractMethodInfo.isProgramMember()) {
                    if (!methodParameterExclusions.isMethodExcluded((MethodInfo) abstractMethodInfo)) {
                        if (addedParameters1.length > 0) {
                            hashMap1.put(abstractMethodInfo, AddedParameter.EMPTY_ARRAY);
                            MethodSignature methodSignature1 = createChangedMethodSignature(abstractMethodInfo, addedParameters1);
                            this.logWarning(
                                    "Method '"
                                            + abstractMethodInfo.getModifierString()
                                            + " "
                                            + abstractMethodInfo.getOriginalNameWithParameters()
                                            + "' in class '"
                                            + abstractMethodInfo.getOriginalDottedName()
                                            + "' could not have its parameter list changed to '"
                                            + methodSignature1.formatSignature()
                                            + "' because it has been excluded and the input change log is specified as loose. Its parameter list will not be changed."
                            );
                            bl = true;
                        } else {
                            hashMap1.put(abstractMethodInfo, addedParameters1);
                        }
                    } else if (addedParameters1.length == 0) {
                        this.logWarning(
                                "Method '"
                                        + abstractMethodInfo.getModifierString()
                                        + " "
                                        + abstractMethodInfo.getOriginalNameWithParameters()
                                        + "' in class '"
                                        + abstractMethodInfo.getOriginalDottedName()
                                        + "' appears in the change log as not having its parameter list changed but it has not been excluded from parameter list changing. The method may have its parameter list changed because the change log is specified as loose. (A)"
                        );
                        bl = true;
                    } else {
                        hashMap1.put(abstractMethodInfo, addedParameters1);
                    }
                } else {
                    hashMap1.put(abstractMethodInfo, addedParameters1);
                }
            }

            if (bl) {
                Map map1 = this.addedParametersByMethod;
                this.addedParametersByMethod = hashMap1;
                map1.clear();
                this.checkAddedParameterClashes(hashMap);
            }
        } else {
            Iterator iterator1 = set1.iterator();

            while (iterator1.hasNext()) {
                MethodInfo methodInfo1 = (MethodInfo) iterator1.next();
                this.logWarning(
                        "Method '"
                                + methodInfo1.getModifierString()
                                + " "
                                + methodInfo1.getOriginalNameWithParameters()
                                + "' in class '"
                                + methodInfo1.getOriginalDottedName()
                                + "' appears in the change log as not having its parameter list changed but it has not been excluded from parameter list changing. The method may have its parameter list changed because the change log is specified as loose. (B)"
                );
            }
        }
    }

    public List getMethodsWithAddedParameters() {
        if (this.addedParametersByMethod != null) {
            ArrayList arrayList = new ArrayList(this.addedParametersByMethod.size());
            Iterator iterator = this.addedParametersByMethod.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                AddedParameter[] addedParameters1 = (AddedParameter[]) entry.getValue();
                if (addedParameters1 != null && addedParameters1.length > 0) {
                    arrayList.add(entry.getKey());
                }
            }

            return arrayList;
        } else {
            return new ArrayList();
        }
    }

    public void putPackageMapping(String string, String string1, boolean bl) {
        com.zelix.klassmaster.util.TwoKeyMap twoKeyMap = null;
        if (this.packageMappings == null) {
            int ba = ZkmUtils.getPrimeCapacity(Math.min(this.packageCount, 5));
            this.packageMappings = ZkmUtils.createHashMap(ba);
            this.reversePackageMappings = new TwoKeyMap(ba);
        }

        Map map1;
        if (string.length() > 0) {
            if (string1.length() == 0) {
                this.reportError("Default package is mapped to \"" + string + "\". Current version cannot handle remapping of default package name.");
                map1 = this.packageMappings;
            } else {
                map1 = this.packageMappings;
            }
        } else {
            map1 = this.packageMappings;
        }

        String string4 = ((java.lang.String) (map1.put(string1, string)));
        if (string4 != null && !string4.equals(string)) {
            this.reportError("Package \"" + string1 + "\" is mapped to \"" + string4 + "\" and \"" + string + "\"");
        }

        StringTokenizer stringTokenizer = new StringTokenizer(string1, ".");
        StringBuffer stringBuffer = new StringBuffer(string1.length());

        while (true) {
            if (!stringTokenizer.hasMoreTokens()) {
                twoKeyMap = this.reversePackageMappings;
                break;
            }

            String string2 = stringTokenizer.nextToken();
            if (stringBuffer.length() > 0) {
                stringBuffer.append(".");
            }

            stringBuffer.append(string2);
            if (stringBuffer.length() == string1.length()) {
                twoKeyMap = this.reversePackageMappings;
                break;
            }

            String string3 = stringBuffer.toString();
            if (!this.packageMappings.containsKey(string3)) {
                if (bl) {
                    this.reportError("Package \"" + string1 + "\" is not preceded by a mapping for \"" + string3 + "\"");
                } else {
                    this.packageMappings.put(string3, string3);
                    this.reversePackageMappings.putValue(string3, string3, string3);
                }
            }
        }

        String string5 = (String) twoKeyMap.putValue(string, string1, string1);
    }

    public void resolveMethodMappings(
            HashMap hashMap,
            HashMap hashMap1,
            ClassMemberLookup classMemberLookup1,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ClasspathClassLoader classpathClassLoader1
    ) throws ZkmException, IOException {
        this.methodMappings = new TwoKeyMap(this.classCount);
        if (this.rawMethodMappings != null) {
            TwoKeyMap twoKeyMap = new TwoKeyMap(this.classCount);
            TwoKeyMap twoKeyMap1 = null;
            TwoKeyMap twoKeyMap2 = null;
            NestedMultiMap nestedMultiMap;
            if (this.hasParameterChangeData()) {
                twoKeyMap1 = new TwoKeyMap(this.classCount);
                twoKeyMap2 = new TwoKeyMap(this.classCount);
                Iterator iterator = this.addedParametersByMethod.entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry entry = (Entry) iterator.next();
                    AbstractMethodInfo abstractMethodInfo = (AbstractMethodInfo) entry.getKey();
                    twoKeyMap2.putValue(abstractMethodInfo.getClassName(), abstractMethodInfo.getSignature(), entry.getValue());
                }

                nestedMultiMap = this.rawMethodMappings;
            } else {
                nestedMultiMap = this.rawMethodMappings;
            }

            Enumeration enumeration = nestedMultiMap.keys();

            while (enumeration.hasMoreElements()) {
                String string11 = (String) enumeration.nextElement();
                String string12 = ZkmUtils.dotsToSlashes(string11);
                String string1 = (String) hashMap.get(string12);
                String string;
                if (string1 == null) {
                    string1 = string12;
                    string = string11;
                } else {
                    string = ZkmUtils.slashesToDots(string1);
                }

                String string2 = ClassFileBase.stripPackage(string12);
                ClassFileBase.stripPackage(string1);
                ClassFileBase classFileBase = ClassHierarchyNode.findClassFile(string1);
                if (classFileBase == null) {
                    classFileBase = classpathClassLoader1.findClassFile(string12);
                    nestedMultiMap = this.rawMethodMappings;
                } else {
                    nestedMultiMap = this.rawMethodMappings;
                }

                Iterator iterator1 = nestedMultiMap.getMultimap(string11).entrySet().iterator();

                while (iterator1.hasNext()) {
                    Entry entry1 = (Entry) iterator1.next();
                    ChangeLogMemberKey changeLogMemberKey = (ChangeLogMemberKey) entry1.getKey();
                    String string3 = changeLogMemberKey.getName();
                    String string4 = changeLogMemberKey.getType();
                    String string5 = changeLogMemberKey.getParameterTypes();
                    String string6 = MethodSignature.parseParameterList(string5, hashMap);
                    List list1 = (List) entry1.getValue();
                    if (this.methodsHaveReturnTypes) {
                        int bc = list1.size();
                        if (bc == 1) {
                            NewNameMapping newNameMapping2 = (NewNameMapping) list1.get(0);
                            String string13 = MethodSignature.javaTypeToDescriptor(string4, hashMap);
                            String string14 = changeLogMemberKey.getName();
                            String string15 = newNameMapping2.getNewName();
                            ChangeLogMethodSignature changeLogMethodSignature1 = new ChangeLogMethodSignature(
                                    string15, string6, string13, newNameMapping2.isParametersObfuscated()
                            );
                            MethodSignature methodSignature2 = new MethodSignature(string14, string6, string13);
                            if (classFileBase.isProgramClass()) {
                                if (classMemberLookup1.declaresMethod(methodSignature2, (ProgramClass) classFileBase)) {
                                    this.putResolvedMethodMapping(
                                            string11, string, methodSignature2, changeLogMethodSignature1, hashMap1, twoKeyMap, twoKeyMap1, twoKeyMap2
                                    );
                                }
                            } else {
                                AbstractMethodInfo abstractMethodInfo1 = classFileBase.findMethod(methodSignature2);
                                if (abstractMethodInfo1 != null) {
                                    this.putResolvedMethodMapping(
                                            string11, string, methodSignature2, changeLogMethodSignature1, hashMap1, twoKeyMap, twoKeyMap1, twoKeyMap2
                                    );
                                } else if (!changeLogMemberKey.isPrivate() && !string14.equals("<clinit>")) {
                                    this.logWarning(
                                            "Contains method '"
                                                    + methodSignature2.formatDeclaration(hashMap1)
                                                    + "' in class '"
                                                    + string11
                                                    + "' but there is no corresponding method in "
                                                    + classFileBase.getLocationName()
                                                    + " (1)."
                                    );
                                }
                            }
                        } else if (bc > 1) {
                            this.reportError(
                                    "Methods with the signature \""
                                            + string3
                                            + "("
                                            + string5
                                            + ")\" in class \""
                                            + string11
                                            + "\" appears "
                                            + bc
                                            + " times but there are no method return types to distinguish them."
                            );
                        }
                    } else {
                        String string7 = null;

                        for (int i = 0; i < list1.size(); i++) {
                            NewNameMapping newNameMapping = (NewNameMapping) list1.get(i);
                            if (string7 == null) {
                                string7 = newNameMapping.getNewName();
                            } else if (!newNameMapping.getNewName().equals(string7)) {
                                this.reportError(
                                        "Two methods with the signature \""
                                                + string3
                                                + "("
                                                + string5
                                                + ")\" in class \""
                                                + string11
                                                + "\" are mapped to the different new method names \""
                                                + string7
                                                + "\" and \""
                                                + newNameMapping.getNewName()
                                                + "\""
                                );
                            }
                        }

                        FieldNameTypeSignature fieldNameTypeSignature = new FieldNameTypeSignature(string3, string6);
                        AbstractMethodInfo[] abstractMethodInfos;
                        if (classFileBase.isProgramClass()) {
                            abstractMethodInfos = classMemberLookup1.findMatchingMethods((ProgramClass) classFileBase, fieldNameTypeSignature);
                        } else {
                            abstractMethodInfos = classFileBase.findMethodsByNameAndType(fieldNameTypeSignature);
                        }

                        if (abstractMethodInfos == null) {
                            if (!classFileBase.isProgramClass()) {
                                this.logWarning(
                                        "Contains method '"
                                                + fieldNameTypeSignature.getNameWithParameters(hashMap1)
                                                + "' in class '"
                                                + string11
                                                + "' but there is no corresponding method in "
                                                + classFileBase.getLocationName()
                                                + " (2)."
                                );
                            }
                        } else {
                            Comparator<AbstractMethodInfo> comparator1 = (abstractMethodInfo2, abstractMethodInfo3) -> {
                                try {
                                    if (abstractMethodInfo2.equals(abstractMethodInfo3)) {
                                        return 0;
                                    } else {
                                        return ClassRepository.hasCompatibleSignature(abstractMethodInfo2, abstractMethodInfo3, commonSuperTypeResolver1) ? 1 : -1;
                                    }
                                } catch (Throwable throwable) {
                                    throw ZkmUtils.sneakyThrow(throwable);
                                }
                            };
                            Arrays.sort(abstractMethodInfos, comparator1);

                            for (int i = 0; i < list1.size(); i++) {
                                if (i < abstractMethodInfos.length) {
                                    String string8 = abstractMethodInfos[0].getSignature().getReturnDescriptor();
                                    NewNameMapping newNameMapping1 = (NewNameMapping) list1.get(i);
                                    String string9 = changeLogMemberKey.getName();
                                    String string10 = newNameMapping1.getNewName();
                                    if (string9.equals(string2)) {
                                        string9 = "<init>";
                                        string10 = "<init>";
                                    }

                                    MethodSignature methodSignature1 = new MethodSignature(string9, string6, string8);
                                    ChangeLogMethodSignature changeLogMethodSignature = new ChangeLogMethodSignature(string10, string6, string8, false);
                                    this.putResolvedMethodMapping(
                                            string11, string, methodSignature1, changeLogMethodSignature, hashMap1, twoKeyMap, twoKeyMap1, twoKeyMap2
                                    );
                                }
                            }
                        }
                    }
                }
            }

            this.rawMethodMappings = null;
        }
    }

    public String getReferenceObfuscationClass() {
        return this.referenceObfuscationClass;
    }

    public MethodSignature withAddedParameters(MethodSignature methodSignature1, Object object, TwoKeyMap twoKeyMap) {
        AddedParameter[] addedParameters1 = (AddedParameter[]) twoKeyMap.getValue(object, methodSignature1);
        if (addedParameters1 == null) {
            return methodSignature1;
        }

        String string = ChangedMethodDescriptor.buildParameterDescriptor(
                MethodSignature.splitParameterDescriptors(methodSignature1.getParameterDescriptor()), addedParameters1
        );
        return new MethodSignature(methodSignature1.getName(), string, methodSignature1.getReturnDescriptor());
    }

    @Override
    public void addMemberFlowObfuscationData(Object object, Object object1) {
        String string = (String) object;
        ChangeLogSimpleNode.getOpaqueStrings();
        if (this.classMappings != null && this.classMappings.containsKey(string)) {
            String string1 = (String) this.classMappings.get(string);
            String string2 = (String) ((List) object1).get(0);

            try {
                int[] ba = AbstractChangeLog.decodeIntPair(string2, ZkmUtils.dotsToSlashes(string1));
                ChangeLogMemberEntry changeLogMemberEntry = (ChangeLogMemberEntry) this.flowEntryList.get(ba[0]);
                ChangeLogMemberEntry changeLogMemberEntry1 = (ChangeLogMemberEntry) this.flowEntryList.get(ba[1]);
                if (changeLogMemberEntry != null && changeLogMemberEntry1 != null) {
                    ObjectPair objectPair = new ObjectPair(changeLogMemberEntry, changeLogMemberEntry1);
                    this.memberClassEntries.put(string, objectPair);
                }
            } catch (NumberFormatException numberFormatException) {
                this.reportError("Corrupt Data:  '" + string2 + "' for " + "MemberClass:" + " '" + string + "' => '" + string1 + "' (D)");
            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                this.reportError("Corrupt Data:  '" + string2 + "' for " + "MemberClass:" + " '" + string + "' => '" + string1 + "' (E)");
            }
        } else {
            String string3 = "MemberClass: '" + string + "' does not exist in the main body of the change log";
            this.reportError(string3);
        }
    }

    public void mergeTraceBackEntries(ChangeLogMapping changeLogMapping2, Map map1) throws ZkmException, IOException {
        if (this.traceBackEntries == null) {
            this.traceBackEntries = ZkmUtils.createHashSetFrom(changeLogMapping2.traceBackEntries);
        } else {
            ArrayList arrayList = new ArrayList(changeLogMapping2.traceBackEntries);
            Collections.sort(arrayList);
            String string = null;
            ListMultimap listMultimap = groupEntriesByClass(this.traceBackEntries);
            ArrayList arrayList1 = null;
            Iterator iterator = arrayList.iterator();

            while (iterator.hasNext()) {
                ChangeLogMemberEntry changeLogMemberEntry = (ChangeLogMemberEntry) iterator.next();
                String string1 = changeLogMemberEntry.getClassName();
                if (string == null || !string1.equals(string)) {
                    string = string1;
                    List list1 = listMultimap.getValues(string1);
                    if (list1 != null) {
                        arrayList1 = new ArrayList(list1);
                        Collections.sort(arrayList1);
                    } else {
                        arrayList1 = null;
                    }
                }

                Set set1;
                if (arrayList1 != null) {
                    if (arrayList1.size() > 0) {
                        if (changeLogMemberEntry.getFieldName() == null) {
                            ChangeLogMemberEntry changeLogMemberEntry3 = (ChangeLogMemberEntry) arrayList1.remove(0);
                            map1.put(new IdentityValueHolder(changeLogMemberEntry), new IdentityValueHolder(changeLogMemberEntry3));
                            continue;
                        }

                        ChangeLogMemberEntry changeLogMemberEntry2 = null;
                        int ba = arrayList1.indexOf(changeLogMemberEntry);
                        if (ba > -1) {
                            changeLogMemberEntry2 = (ChangeLogMemberEntry) arrayList1.remove(ba);
                        }

                        if (changeLogMemberEntry2 != null) {
                            if (!changeLogMemberEntry2.getFieldDescriptor().equals(changeLogMemberEntry.getFieldDescriptor())) {
                                this.logError(
                                        "Field '"
                                                + changeLogMemberEntry2.getFieldName()
                                                + "' in class '"
                                                + string1
                                                + "' has type '"
                                                + ConstantPoolEntry.descriptorToJavaType(changeLogMemberEntry2.getFieldDescriptor())
                                                + "' in this log but type '"
                                                + ConstantPoolEntry.descriptorToJavaType(changeLogMemberEntry.getFieldDescriptor())
                                                + "' in '"
                                                + changeLogMapping2.changeLogName
                                                + "'. Logs could not be fully merged. You must distribute this application as a whole. (1)"
                                );
                            }

                            if (changeLogMemberEntry.hasSetterMethod()) {
                                if (changeLogMemberEntry2.hasSetterMethod()) {
                                    if (!changeLogMemberEntry2.getSetterMethodName().equals(changeLogMemberEntry.getSetterMethodName())) {
                                        this.logError(
                                                "Method '"
                                                        + changeLogMemberEntry2.getSetterMethodName()
                                                        + '('
                                                        + changeLogMemberEntry2.getFieldTypeName()
                                                        + ")' in class '"
                                                        + string1
                                                        + "' in this log is inconsistent with method '"
                                                        + changeLogMemberEntry.getSetterMethodName()
                                                        + '('
                                                        + changeLogMemberEntry.getFieldTypeName()
                                                        + ")' in '"
                                                        + changeLogMapping2.changeLogName
                                                        + "'. Logs could not be fully merged. You must distribute this application as a whole. (1)"
                                        );
                                    }

                                    if (!changeLogMemberEntry2.getGetterMethodName().equals(changeLogMemberEntry.getGetterMethodName())) {
                                        this.logError(
                                                "Method '"
                                                        + changeLogMemberEntry2.getFieldTypeName()
                                                        + " "
                                                        + changeLogMemberEntry2.getGetterMethodName()
                                                        + "()' in class '"
                                                        + string1
                                                        + "' in this log is inconsistent with method '"
                                                        + changeLogMemberEntry.getFieldTypeName()
                                                        + " "
                                                        + changeLogMemberEntry.getGetterMethodName()
                                                        + "()' in '"
                                                        + changeLogMapping2.changeLogName
                                                        + "'. Logs could not be fully merged. You must distribute this application as a whole. (1)"
                                        );
                                    }

                                    if (changeLogMemberEntry2.getAltGetterMethodName() != null
                                            && !changeLogMemberEntry2.getAltGetterMethodName().equals(changeLogMemberEntry.getAltGetterMethodName())) {
                                        this.logError(
                                                "Method '"
                                                        + changeLogMemberEntry2.getFieldTypeName()
                                                        + " "
                                                        + changeLogMemberEntry2.getAltGetterMethodName()
                                                        + "()' in class '"
                                                        + string1
                                                        + "' in this log is inconsistent with method '"
                                                        + changeLogMemberEntry.getFieldTypeName()
                                                        + " "
                                                        + changeLogMemberEntry.getAltGetterMethodName()
                                                        + "()' in '"
                                                        + changeLogMapping2.changeLogName
                                                        + "'. Logs could not be fully merged. You must distribute this application as a whole. (1)"
                                        );
                                    }

                                    if (changeLogMemberEntry2.isAlternateVariant() != changeLogMemberEntry.isAlternateVariant()) {
                                        this.logError(
                                                "Field '"
                                                        + changeLogMemberEntry2.getFieldName()
                                                        + "' in class '"
                                                        + string1
                                                        + "' in this log is inconsistent in nature with the equivalent field in '"
                                                        + changeLogMapping2.changeLogName
                                                        + "'. Logs could not be fully merged. You must distribute this application as a whole. (1)"
                                        );
                                    }
                                } else {
                                    changeLogMemberEntry2.setSetterMethodName(changeLogMemberEntry.getSetterMethodName());
                                    changeLogMemberEntry2.setGetterMethodName(changeLogMemberEntry.getGetterMethodName());
                                    changeLogMemberEntry2.setAltGetterMethodName(changeLogMemberEntry.getAltGetterMethodName());
                                    changeLogMemberEntry2.setNewSetterMethodName(changeLogMemberEntry.getNewSetterMethodName());
                                    changeLogMemberEntry2.setNewGetterMethodName(changeLogMemberEntry.getNewGetterMethodName());
                                    changeLogMemberEntry2.setNewAltGetterMethodName(changeLogMemberEntry.getNewAltGetterMethodName());
                                    changeLogMemberEntry2.setAlternateVariant(changeLogMemberEntry.isAlternateVariant());
                                }
                            }

                            map1.put(new IdentityValueHolder(changeLogMemberEntry), new IdentityValueHolder(changeLogMemberEntry2));
                        } else {
                            Iterator iterator1 = arrayList1.iterator();

                            while (iterator1.hasNext()) {
                                ChangeLogMemberEntry changeLogMemberEntry1 = (ChangeLogMemberEntry) iterator1.next();
                                if (changeLogMemberEntry1.getFieldName() == null) {
                                    changeLogMemberEntry2 = changeLogMemberEntry1;
                                    iterator1.remove();
                                }
                            }

                            if (changeLogMemberEntry2 != null) {
                                this.traceBackEntries.remove(changeLogMemberEntry2);
                                this.traceBackEntries.add(changeLogMemberEntry);
                                map1.put(new IdentityValueHolder(changeLogMemberEntry2), new IdentityValueHolder(changeLogMemberEntry));
                            } else {
                                this.traceBackEntries.add(changeLogMemberEntry);
                            }
                        }
                        continue;
                    }

                    set1 = this.traceBackEntries;
                } else {
                    set1 = this.traceBackEntries;
                }

                set1.add(changeLogMemberEntry);
            }
        }
    }

    public boolean isNewClassName(String string) throws IOException {
        return this.reverseClassMappings != null && this.reverseClassMappings.containsKey(ZkmUtils.slashesToDots(string));
    }

    @Override
    public void addSourceNameMapping(Object object, Object object1) {
        if (this.sourceNameMappings == null) {
            this.sourceNameMappings = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(this.classCount));
        }

        this.sourceNameMappings.put(object, object1);
    }

    public ArrayList getOldClassNames() {
        ArrayList arrayList = new ArrayList(this.classCount);
        if (this.classMappings != null) {
            CollectionSnapshotEnumeration collectionSnapshotEnumeration = new CollectionSnapshotEnumeration(this.classMappings.keySet());

            while (collectionSnapshotEnumeration.hasMoreElements()) {
                String string = (String) collectionSnapshotEnumeration.nextElement();
                arrayList.add(ZkmUtils.dotsToSlashes(string));
            }
        }

        return arrayList;
    }

    @Override
    public void addPackageFlowObfuscationData(Object object, Object object1, Object object2) throws ZkmException, IOException {
        String string = (String) object;
        List list1 = (List) object2;
        String string1 = (String) object1;
        if (!this.errorsFound) {
            this.addFlowObfuscationEntry(string1, list1, false, "ForwardClass:", string);
        }
    }

    public void logMessage(String string) {
        this.scriptEnvironment.logMessage("Input change log '" + this.changeLogName + "': " + string + this.getSourceFileSuffix());
    }

    public String getNewPackageName(String string) throws IOException {
        if (this.packageMappings == null) {
            return null;
        }

        String string1 = (String) this.packageMappings.get(ZkmUtils.slashesToDots(string));
        if (string1 == null) {
            return null;
        }

        String string2 = ZkmUtils.dotsToSlashes(string1);
        return string2.equals(string) ? null : string2;
    }

    public String getNewClassName(String string) throws IOException {
        if (this.classMappings == null) {
            return null;
        }

        String string1 = ZkmUtils.slashesToDots(string);
        String string2 = (String) this.classMappings.get(string1);
        return string2 != null && !string2.equals(string1) ? ZkmUtils.dotsToSlashes(string2) : null;
    }

    public String getModuleReferenceObfuscationClass(Object object) {
        return (String) this.moduleReferenceObfuscationClasses.get(object);
    }

    public boolean hasLineNumberMappings() {
        return this.lineNumberMappings != null && this.lineNumberMappings.getKeyCount() > 0;
    }

    public boolean hasAddedParameters(Object object) {
        if (this.addedParametersByMethod == null) {
            return false;
        }

        AddedParameter[] addedParameters1 = (AddedParameter[]) this.addedParametersByMethod.get(object);
        return addedParameters1 == null ? false : addedParameters1.length > 0;
    }

    @Override
    public void incrementClassCount() {
        this.classCount++;
    }

    public Enumeration getSortedTraceBackEntries() {
        ArrayList arrayList = new ArrayList(this.traceBackEntries);
        Collections.sort(arrayList);
        return Collections.enumeration(arrayList);
    }

    @Override
    public void addModuleMethodParameterChangeClasses(Object object, Object object1) {
        List list1 = (List) object;
        ObjectTriple objectTriple = new ObjectTriple(list1.get(0), list1.get(1), list1.get(2));
        Map map1;
        if (this.canonicalClassTriples.containsKey(objectTriple)) {
            objectTriple = (ObjectTriple) this.canonicalClassTriples.get(objectTriple);
            map1 = this.moduleMethodParameterChangeClasses;
        } else {
            map1 = this.moduleMethodParameterChangeClasses;
        }

        map1.put(object1, objectTriple);
    }

    public Set getPackagesMappedTo(String string) throws IOException {
        if (this.reversePackageMappings == null) {
            return null;
        }

        Map map1 = this.reversePackageMappings.getInnerMap(ZkmUtils.slashesToDots(string));
        if (map1 == null) {
            return null;
        }

        HashSet hashSet = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(map1.size()));
        Iterator iterator = map1.keySet().iterator();

        while (iterator.hasNext()) {
            String string1 = ZkmUtils.dotsToSlashes((String) iterator.next());
            hashSet.add(string1);
        }

        return hashSet;
    }

    public boolean containsMethodMapping(String string, String string1, String[] strings, String string2) throws IOException {
        if (this.rawMethodMappings != null) {
            String string3 = ZkmUtils.slashesToDots(string);
            String string4 = this.joinTypeNames(strings);
            String string5 = string2;
            String string6 = string4;
            String string7 = string1;
            ChangeLogMemberKey changeLogMemberKey = this.createMethodKey(string7, string6, string5);
            return this.rawMethodMappings.containsInnerKey(string3, changeLogMemberKey);
        } else {
            return false;
        }
    }

    public boolean hasPackageMapping(String string) throws IOException {
        return this.packageMappings != null && this.packageMappings.containsKey(ZkmUtils.slashesToDots(string));
    }

    public void checkAddedParameterClashes(HashMap hashMap) {
        TwoKeyMap twoKeyMap = new TwoKeyMap();
        TwoKeyMap twoKeyMap1 = new TwoKeyMap();
        Iterator iterator = this.addedParametersByMethod.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            AbstractMethodInfo abstractMethodInfo = (AbstractMethodInfo) entry.getKey();
            MethodSignature methodSignature1 = abstractMethodInfo.getOriginalSignature();
            String string = ChangedMethodDescriptor.buildParameterDescriptor(
                    MethodSignature.splitParameterDescriptors(abstractMethodInfo.getParameterDescriptor()), (AddedParameter[]) entry.getValue()
            );
            MethodSignature methodSignature2 = new MethodSignature(abstractMethodInfo.getJvmName(), string, abstractMethodInfo.getReturnDescriptor());
            if ((MethodSignature) twoKeyMap.putValue(abstractMethodInfo.getOwningClass(), methodSignature1, methodSignature2) != null) {
                MethodSignature methodSignature3 = MethodSignature.remapClassNames(methodSignature2, hashMap);
                this.reportError(
                        "Method '"
                                + abstractMethodInfo.getModifierString()
                                + " "
                                + abstractMethodInfo.toOriginalDisplayString()
                                + "' in class '"
                                + abstractMethodInfo.getOriginalDottedName()
                                + "' could not be changed to '"
                                + methodSignature3
                                + "' because of a method signature clash."
                );
            }

            MethodSignature methodSignature5 = (MethodSignature) twoKeyMap1.putValue(abstractMethodInfo.getOwningClass(), methodSignature2, methodSignature1);
            if (methodSignature5 != null) {
                MethodSignature methodSignature4 = MethodSignature.remapClassNames(methodSignature2, hashMap);
                this.reportError(
                        "Method '"
                                + abstractMethodInfo.getModifierString()
                                + " "
                                + abstractMethodInfo.toOriginalDisplayString()
                                + "' in class '"
                                + abstractMethodInfo.getOriginalDottedName()
                                + "' could not be changed to '"
                                + methodSignature4
                                + "' because of a method '"
                                + methodSignature5
                                + "' is mapped to the same new signature."
                );
            }
        }
    }

    public boolean hasErrors() {
        return this.errorsFound;
    }

    public ChangeLogMethodSignature reconcileInheritedMethodMapping(
            String string, String string1, MethodSignature methodSignature1, ChangeLogMethodSignature changeLogMethodSignature, HashMap hashMap
    ) throws IOException {
        String string2 = ZkmUtils.slashesToDots(string1);
        String string3 = ZkmUtils.slashesToDots(string);
        MethodSignature methodSignature2 = (MethodSignature) this.methodMappings.removeValue(string3, methodSignature1);
        ChangeLogMethodSignature changeLogMethodSignature1 = (ChangeLogMethodSignature) this.methodMappings
                .putValue(string2, methodSignature1, changeLogMethodSignature);
        if (changeLogMethodSignature1 != null && !changeLogMethodSignature1.equals(changeLogMethodSignature)) {
            String string4 = (String) hashMap.get(string);
            if (string4 == null) {
                string4 = string3;
            }

            String string5 = (String) hashMap.get(string1);
            if (string5 == null) {
                string5 = string2;
            }

            String string6 = "Method '"
                    + methodSignature1.getNameWithParameters(hashMap)
                    + " in class '"
                    + string4
                    + "' is mapped to new name '"
                    + changeLogMethodSignature.getName()
                    + "' but the corresponding method in class '"
                    + string5
                    + "' is mapped to new name '"
                    + changeLogMethodSignature1.getName()
                    + "'. Using mapping of '"
                    + changeLogMethodSignature1.getName()
                    + "' for both methods.";
            this.logWarning(string6);
            this.methodMappings.putValue(string2, methodSignature1, changeLogMethodSignature1);
            return changeLogMethodSignature1;
        } else {
            return changeLogMethodSignature;
        }
    }

    public ListMultimap getSortedForwardClassEntries() {
        ListMultimap listMultimap = new ListMultimap(this.forwardClassEntries.getKeyCount());
        Enumeration enumeration = this.forwardClassEntries.keys();

        while (enumeration.hasMoreElements()) {
            String string = (String) enumeration.nextElement();
            Set set1 = this.forwardClassEntries.getValues(string);
            ArrayList arrayList = new ArrayList(set1.size());
            Iterator iterator = set1.iterator();

            while (iterator.hasNext()) {
                ChangeLogMemberEntry changeLogMemberEntry = (ChangeLogMemberEntry) iterator.next();
                arrayList.add(changeLogMemberEntry);
            }

            Collections.sort(arrayList);
            listMultimap.putValues(string, arrayList);
        }

        return listMultimap;
    }

    public MethodSignature getNewMethodSignature(String string, Object object) throws IOException {
        if (this.methodMappings == null) {
            return null;
        } else {
            ChangeLogMethodSignature changeLogMethodSignature = (ChangeLogMethodSignature) this.methodMappings.getValue(ZkmUtils.slashesToDots(string), object);
            if (changeLogMethodSignature != null) {
                return changeLogMethodSignature.isParametersObfuscated()
                        ? new MethodSignature(changeLogMethodSignature.getName(), "([Ljava/lang/Object;)", changeLogMethodSignature.getReturnDescriptor())
                        : new MethodSignature(
                        changeLogMethodSignature.getName(), changeLogMethodSignature.getParameterDescriptor(), changeLogMethodSignature.getReturnDescriptor()
                );
            } else {
                return null;
            }
        }
    }

    public ChangeLogClassKey findFieldKeyByHash(String string, Object object) {
        Iterator iterator = this.rawFieldMappings.getMultimap(string).keySet().iterator();

        while (iterator.hasNext()) {
            ChangeLogClassKey changeLogClassKey = (ChangeLogClassKey) iterator.next();
            if (ZkmUtils.md5Hex(changeLogClassKey.getName() + " " + MethodSignature.javaTypeToDescriptor(changeLogClassKey.getType()) + "java.lang.Object[]")
                    .equals(object)) {
                return changeLogClassKey;
            }
        }

        return null;
    }

    public void putRawMethodMapping(Object object, ChangeLogMemberKey changeLogMemberKey, Object object1) {
        if (this.rawMethodMappings == null) {
            this.rawMethodMappings = new NestedMultiMap(this.classCount, 5, 5);
        }

        this.rawMethodMappings.addValue(object, changeLogMemberKey, object1);
        if (changeLogMemberKey.getType() != null) {
            this.methodsHaveReturnTypes = true;
        } else {
            this.methodsHaveReturnTypes = false;
        }
    }

    public ArrayList listFieldEntries(String string) {
        ArrayList arrayList = new ArrayList();
        if (this.rawFieldMappings != null) {
            ListMultimap listMultimap = this.rawFieldMappings.getMultimap(string);
            if (listMultimap != null) {
                Enumeration enumeration = listMultimap.keys();

                while (enumeration.hasMoreElements()) {
                    ChangeLogClassKey changeLogClassKey = (ChangeLogClassKey) enumeration.nextElement();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(changeLogClassKey.getType());
                    stringBuilder.append(" ");
                    stringBuilder.append(changeLogClassKey.getName());
                    arrayList.add(stringBuilder.toString());
                }
            }
        }

        return arrayList;
    }

    public ObjectTriple getModuleMethodParameterChangeClasses(Object object) {
        return (ObjectTriple) this.moduleMethodParameterChangeClasses.get(object);
    }

    public boolean hasFlowData(String string) throws IOException {
        return this.flowDataClasses.contains(ZkmUtils.slashesToDots(string));
    }

    public AddedParameter[] reconcileAddedParameters(
            AbstractMethodInfo abstractMethodInfo, AbstractMethodInfo abstractMethodInfo1, AddedParameter[] addedParameters1
    ) {
        AddedParameter[] addedParameters3 = (AddedParameter[]) this.addedParametersByMethod.remove(abstractMethodInfo);
        boolean bl = this.hasAddedParametersEntry(abstractMethodInfo1);
        AddedParameter[] addedParameters2 = ((com.zelix.klassmaster.obfuscator.parameters.AddedParameter[]) (this.addedParametersByMethod.put(abstractMethodInfo1, addedParameters1)));
        if (bl) {
            if (addedParameters1.length == 0 && addedParameters2.length > 0) {
                MethodSignature methodSignature4 = createChangedMethodSignature(abstractMethodInfo1, addedParameters1);
                String string2 = "Method '"
                        + abstractMethodInfo.getOriginalNameWithParameters()
                        + " in class '"
                        + abstractMethodInfo.getOriginalDottedName()
                        + "' is specified to have no parameter list change but the corresponding method in class '"
                        + abstractMethodInfo1.getOriginalDottedName()
                        + "' is mapped to new parameter list '"
                        + methodSignature4.toDeclarationString()
                        + "'. Using mapping of '"
                        + methodSignature4.toDeclarationString()
                        + "' for both methods.";
                this.logWarning(string2);
                this.addedParametersByMethod.put(abstractMethodInfo1, addedParameters2);
                return addedParameters2;
            }

            if (addedParameters1.length > 0 && addedParameters2.length == 0) {
                MethodSignature methodSignature3 = createChangedMethodSignature(abstractMethodInfo, addedParameters1);
                String string1 = "Method '"
                        + abstractMethodInfo.getOriginalNameWithParameters()
                        + " in class '"
                        + abstractMethodInfo.getOriginalDottedName()
                        + "' is mapped to new parameter list '"
                        + methodSignature3.toDeclarationString()
                        + "' but the corresponding method in class '"
                        + abstractMethodInfo1.getOriginalDottedName()
                        + "' is specified to have no parameter list change. Using a specification of no parameter list change for both methods.";
                this.logWarning(string1);
                this.addedParametersByMethod.put(abstractMethodInfo1, addedParameters2);
                return addedParameters2;
            }

            if (!ZkmUtils.arraysEqual(addedParameters1, addedParameters2)) {
                MethodSignature methodSignature1 = createChangedMethodSignature(abstractMethodInfo, addedParameters1);
                MethodSignature methodSignature2 = createChangedMethodSignature(abstractMethodInfo1, addedParameters1);
                String string = "Method '"
                        + abstractMethodInfo.getOriginalNameWithParameters()
                        + " in class '"
                        + abstractMethodInfo.getOriginalDottedName()
                        + "' is mapped to parameter list '"
                        + methodSignature1.toDeclarationString()
                        + "' but the corresponding method in class '"
                        + abstractMethodInfo1.getOriginalDottedName()
                        + "' is mapped to parameter list '"
                        + methodSignature2.toDeclarationString()
                        + "'. Using mapping of '"
                        + methodSignature2.toDeclarationString()
                        + "' for both methods.";
                this.logWarning(string);
                this.addedParametersByMethod.put(abstractMethodInfo1, addedParameters2);
                return addedParameters2;
            }
        }

        return addedParameters1;
    }

    public static String decodeFlowFieldType(String string, List list1, String string1) {
        char ba = string.charAt(0);
        if (ba >= '0' && ba <= '5') {
            switch (ba) {
                case '1':
                    return "Z";
                case '2':
                    return "I";
                case '3':
                    return "Ljava/lang/String;";
                case '4':
                    return "[I";
                case '5':
                    return "[Ljava/lang/String;";
                default:
                    return null;
            }
        } else {
            int bb = AbstractChangeLog.getNonDigitPrefixLength(string);
            int bc = (int) ZkmUtils.parseCustomBase(string.substring(0, bb), "MQoSAkmVrHyDwxKuhLPOURetTIFYqWnpbzfjEsXcZlNCdvgJGBia");
            String string2;
            if (bc < list1.size() && list1.get(bc) != null) {
                ChangeLogMemberEntry changeLogMemberEntry = (ChangeLogMemberEntry) list1.get(bc);
                string2 = changeLogMemberEntry.getInternalClassName();
            } else {
                if (bc != list1.size()) {
                    return null;
                }

                string2 = ZkmUtils.dotsToSlashes(string1);
            }

            return "[L" + string2 + ';';
        }
    }

    public void logWarning(String string) {
        this.scriptEnvironment.logWarning("Input change log '" + this.changeLogName + "': " + string + this.getSourceFileSuffix());
    }

    public boolean hasNewPackageName(String string) throws IOException {
        return this.getNewPackageName(string) != null;
    }

    public Enumeration getRenamedFields(String string) throws IOException {
        String string1 = string;
        string1 = ZkmUtils.slashesToDots(string1);
        if (this.reverseFieldMappings != null) {
            Map map1 = this.reverseFieldMappings.getInnerMap(string1);
            if (map1 != null) {
                ArrayList arrayList = new ArrayList(map1.size());
                Iterator iterator = map1.entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry entry = (Entry) iterator.next();
                    FieldSignature fieldSignature = (FieldSignature) entry.getKey();
                    FieldSignature fieldSignature1 = (FieldSignature) entry.getValue();
                    if (!fieldSignature.equals(fieldSignature1)) {
                        arrayList.add(fieldSignature);
                    }
                }

                return Collections.enumeration(arrayList);
            }
        }

        return null;
    }

    public void mergePackageMappings(ChangeLogMapping changeLogMapping2) {
        ArrayList arrayList = new ArrayList(changeLogMapping2.packageMappings.keySet());
        Collections.sort(arrayList);
        int ba = arrayList.size();

        for (int i = 0; i < ba; i++) {
            String string = (String) arrayList.get(i);
            if (this.packageMappings != null && this.packageMappings.containsKey(string)) {
                String string7 = (String) this.packageMappings.get(string);
                String string8 = (String) changeLogMapping2.packageMappings.get(string);
                if (!string7.equals(string8)) {
                    this.logWarning(
                            "Package name \""
                                    + string
                                    + "\" mapping clash while merging with \""
                                    + changeLogMapping2.changeLogName
                                    + "\". Will be mapped to \""
                                    + string7
                                    + "\" and not \""
                                    + string8
                                    + "\""
                    );
                }
            } else {
                String string1 = (String) changeLogMapping2.packageMappings.get(string);
                int bc = string.lastIndexOf(46);
                if (bc == -1) {
                    this.putPackageMapping(string1, string, false);
                } else {
                    String string2 = string.substring(0, bc);
                    String string3 = (String) this.packageMappings.get(string2);
                    String string4 = (String) changeLogMapping2.packageMappings.get(string2);
                    if (string4.equals(string1)) {
                        this.putPackageMapping(string3, string, false);
                        if (!string1.equals(string3)) {
                            this.logWarning(
                                    "Package name \""
                                            + string
                                            + "\" partial mapping clash (A) while merging with \""
                                            + changeLogMapping2.changeLogName
                                            + "\". Will be mapped to \""
                                            + string3
                                            + "\" and not \""
                                            + string1
                                            + "\""
                            );
                        }
                    } else {
                        int bd = ZkmStringUtils.countChar(string1, '.') + 1;
                        String string5;
                        if (ZkmStringUtils.countChar(string4, '.') + 1 >= bd) {
                            string5 = string3;
                        } else {
                            int be = string1.lastIndexOf(46);
                            String string6 = string1.substring(be + 1);
                            string5 = string3 + "." + string6;
                        }

                        this.putPackageMapping(string5, string, false);
                        if (!string1.equals(string5)) {
                            this.logWarning(
                                    "Package name \""
                                            + string
                                            + "\" partial mapping clash (B) while merging with \""
                                            + changeLogMapping2.changeLogName
                                            + "\". Will be mapped to \""
                                            + string5
                                            + "\" and not \""
                                            + string1
                                            + "\""
                            );
                        }
                    }
                }
            }
        }
    }

    public ChangeLogMethodSignature lookupMethodMapping(String string, Object object) throws IOException {
        return this.methodMappings == null ? null : (ChangeLogMethodSignature) this.methodMappings.getValue(ZkmUtils.slashesToDots(string), object);
    }

    public ArrayList getMethodMappingPairs(String string) throws IOException {
        String string1 = string;
        string1 = ZkmUtils.slashesToDots(string1);
        if (this.methodMappings != null) {
            Map map1 = this.methodMappings.getInnerMap(string1);
            if (map1 != null) {
                ArrayList arrayList = new ArrayList();
                Iterator iterator = map1.entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry entry = (Entry) iterator.next();
                    arrayList.add(new ObjectPair(entry.getKey(), entry.getValue()));
                }

                return arrayList;
            }
        }

        return null;
    }

    public void resolveFieldMappings(
            HashMap hashMap, HashMap hashMap1, SetMultiMap setMultiMap, ClassMemberLookup classMemberLookup1, ClasspathClassLoader classpathClassLoader1
    ) throws ZkmException, IOException {
        this.fieldMappings = new TwoKeyMap(this.classCount);
        this.reverseFieldMappings = new TwoKeyMap(this.classCount);
        if (this.rawFieldMappings != null) {
            Enumeration enumeration = this.rawFieldMappings.keys();

            while (enumeration.hasMoreElements()) {
                String string = (String) enumeration.nextElement();
                String string1 = ZkmUtils.dotsToSlashes(string);
                String string3 = (String) hashMap.get(string1);
                String string2;
                if (string3 == null) {
                    string3 = string1;
                    string2 = string;
                } else {
                    string2 = ZkmUtils.slashesToDots(string3);
                }

                ClassFileBase classFileBase = ClassHierarchyNode.findClassFile(string3);
                if (classFileBase == null) {
                    classFileBase = classpathClassLoader1.findClassFile(string1);
                }

                boolean programClass = classFileBase.isProgramClass();
                ListMultimap listMultimap = this.rawFieldMappings.getMultimap(string);
                Enumeration enumeration1 = listMultimap.keys();

                label103:
                while (enumeration1.hasMoreElements()) {
                    ChangeLogClassKey changeLogClassKey = (ChangeLogClassKey) enumeration1.nextElement();
                    String string4 = changeLogClassKey.getName();
                    String string5 = changeLogClassKey.getType();
                    List list1 = listMultimap.getValues(changeLogClassKey);
                    int ba = list1.size();
                    if (this.fieldsHaveTypes) {
                        if (ba > 1) {
                            this.reportFatalError("Field \"" + string5 + " " + string4 + "\" in class \"" + string + "\" appears more than once in log.");
                        }

                        String string7 = (String) list1.get(0);
                        String string8 = MethodSignature.javaTypeToDescriptor(string5, hashMap);
                        AbstractFieldInfo abstractFieldInfo;
                        if (programClass) {
                            abstractFieldInfo = classMemberLookup1.findField(string3, string4, string8);
                        } else {
                            abstractFieldInfo = classFileBase.findField(string4, string8);
                        }

                        FieldSignature fieldSignature3 = new FieldSignature(string4, string8);
                        if (abstractFieldInfo == null && (programClass || setMultiMap == null || !setMultiMap.containsValue(classFileBase, fieldSignature3))) {
                            if (!programClass && !changeLogClassKey.isPrivate()) {
                                this.logWarning(
                                        "Contains field '"
                                                + fieldSignature3.formatDeclaration(hashMap1)
                                                + "' in class '"
                                                + string
                                                + "' but there is no corresponding field in "
                                                + classFileBase.getLocationName()
                                                + " (1)."
                                );
                            }
                        } else {
                            FieldSignature fieldSignature5 = new FieldSignature(string7, string8);
                            this.putResolvedFieldMapping(string, string2, fieldSignature3, fieldSignature5, hashMap1);
                        }
                    } else if (ba > 1) {
                        this.reportError(
                                "There are " + ba + " field entries named \"" + string4 + "\" for class \"" + string + "\" but no field types are present in log."
                        );
                    } else {
                        String string6 = (String) list1.get(0);
                        if (!programClass && setMultiMap != null) {
                            Set set1 = setMultiMap.getValues(classFileBase);
                            if (set1 != null) {
                                Iterator iterator = set1.iterator();

                                while (iterator.hasNext()) {
                                    FieldSignature fieldSignature = (FieldSignature) iterator.next();
                                    if (fieldSignature.getName().equals(string4)) {
                                        FieldSignature fieldSignature1 = new FieldSignature(string6, fieldSignature.getDescriptor());
                                        this.putResolvedFieldMapping(string, string2, fieldSignature, fieldSignature1, hashMap1);
                                        continue label103;
                                    }
                                }
                            }
                        }

                        AbstractFieldInfo[] abstractFieldInfos;
                        if (programClass) {
                            abstractFieldInfos = classMemberLookup1.findFieldsByName(string3, string4);
                        } else {
                            abstractFieldInfos = classFileBase.findFieldsByName(string4);
                        }

                        if (abstractFieldInfos != null && abstractFieldInfos.length != 0) {
                            if (abstractFieldInfos.length == 1) {
                                AbstractFieldInfo abstractFieldInfo1 = abstractFieldInfos[0];
                                String string9 = abstractFieldInfo1.getDescriptor();
                                FieldSignature fieldSignature4 = new FieldSignature(string4, string9);
                                FieldSignature fieldSignature2 = new FieldSignature(string6, string9);
                                this.putResolvedFieldMapping(string, string2, fieldSignature4, fieldSignature2, hashMap1);
                            } else {
                                this.reportError(
                                        "There are "
                                                + abstractFieldInfos.length
                                                + " fields named \""
                                                + string4
                                                + "\" in class \""
                                                + string
                                                + "\" but no field types are present in log."
                                );
                            }
                        } else if (!classFileBase.isProgramClass()) {
                            this.logWarning(
                                    "Contains field '"
                                            + string4
                                            + "' in class '"
                                            + string
                                            + "' but there is no corresponding field in "
                                            + classFileBase.getLocationName()
                                            + " (2)."
                            );
                        }
                    }
                }
            }
        }
    }

    public List findOriginalMethods(String string, Object object) throws IOException {
        if (this.reverseMethodMappings == null) {
            this.buildReverseMethodMappings();
        }

        ArrayList arrayList = new ArrayList();
        if (this.reverseMethodMappings != null) {
            String string1 = ZkmUtils.slashesToDots(string);
            NestedMultiMap nestedMultiMap = this.reverseMethodMappings.getNestedMultiMap(string1);
            Enumeration enumeration = nestedMultiMap.keys();

            while (enumeration.hasMoreElements()) {
                ComparableKeyValue comparableKeyValue = (ComparableKeyValue) enumeration.nextElement();
                if (((String) comparableKeyValue.getKey()).equals(object)) {
                    ListMultimap listMultimap = nestedMultiMap.getMultimap(comparableKeyValue);
                    Enumeration enumeration1 = listMultimap.keys();

                    while (enumeration1.hasMoreElements()) {
                        String string2 = (String) enumeration1.nextElement();
                        String string3 = this.mapTypeList(string2, this.reverseClassMappings);
                        Iterator iterator = listMultimap.getValues(string2).iterator();

                        while (iterator.hasNext()) {
                            String string4 = (String) iterator.next();
                            arrayList.add(string4 + "(" + string3 + ")");
                        }
                    }
                }
            }
        }

        return arrayList;
    }

    public boolean isMethodRenamed(String string, MethodSignature methodSignature1) throws IOException {
        return this.lookupNewMethodName(string, methodSignature1) != null;
    }

    @Override
    public boolean acceptsFieldChanges() {
        return true;
    }

    public String getOriginalFieldName(String string, String string1, String string2) throws IOException {
        return this.lookupOriginalFieldName(string, string1, string2, this.rawFieldMappings);
    }

    public String lookupNewFieldName(String string, String string1, String string2, NestedMultiMap nestedMultiMap) throws IOException {
        if (nestedMultiMap != null) {
            String string3 = ZkmUtils.slashesToDots(string);
            ChangeLogClassKey changeLogClassKey = this.createFieldKey(string1, string2);
            List list1 = nestedMultiMap.getValues(string3, changeLogClassKey);
            if (list1 == null) {
                return null;
            }

            if (list1.size() == 1) {
                String string5 = (String) list1.get(0);
                return string1.equals(string5) ? null : string5;
            }

            if (this.methodsHaveReturnTypes) {
                String string4 = string2;
                this.reportFatalError("Class '" + string3 + "' has multiple fields '" + string4 + " " + string1 + "'.");
            } else {
                this.reportFatalError("Class '" + string3 + "' has multiple fields named '" + string1 + "' but field type not present.");
            }

            return null;
        } else {
            return null;
        }
    }

    public boolean hasClassMapping(String string) throws IOException {
        return this.classMappings != null && this.classMappings.containsKey(ZkmUtils.slashesToDots(string));
    }

    public void mergeForwardClassEntries(ChangeLogMapping changeLogMapping2, TwoKeyMap twoKeyMap) throws ZkmException, IOException {
        if (this.forwardClassEntries == null) {
            this.forwardClassEntries = changeLogMapping2.forwardClassEntries.deepCopy();
        } else {
            Enumeration enumeration = changeLogMapping2.forwardClassEntries.keys();

            while (enumeration.hasMoreElements()) {
                String string = (String) enumeration.nextElement();
                Set set1 = changeLogMapping2.forwardClassEntries.getValues(string);
                if (this.forwardClassEntries.containsKey(string)) {
                    ArrayList arrayList = new ArrayList(set1);
                    Collections.sort(arrayList);
                    String string1 = null;
                    ListMultimap listMultimap = groupEntriesByClass(this.forwardClassEntries.getValues(string));
                    ArrayList arrayList1 = null;

                    for (int i = 0; i < arrayList.size(); i++) {
                        ChangeLogMemberEntry changeLogMemberEntry = (ChangeLogMemberEntry) arrayList.get(i);
                        String string2 = changeLogMemberEntry.getClassName();
                        if (string1 == null || !string2.equals(string1)) {
                            string1 = string2;
                            List list1 = listMultimap.getValues(string2);
                            if (list1 != null) {
                                arrayList1 = new ArrayList(list1);
                                Collections.sort(arrayList1);
                            } else {
                                arrayList1 = null;
                            }
                        }

                        SetMultiMap setMultiMap;
                        if (arrayList1 != null) {
                            if (arrayList1.size() > 0) {
                                if (changeLogMemberEntry.getFieldName() == null) {
                                    ChangeLogMemberEntry changeLogMemberEntry3 = (ChangeLogMemberEntry) arrayList1.remove(0);
                                    twoKeyMap.putValue(string, new IdentityValueHolder(changeLogMemberEntry), new IdentityValueHolder(changeLogMemberEntry3));
                                    continue;
                                }

                                ChangeLogMemberEntry changeLogMemberEntry2 = null;
                                int bb = arrayList1.indexOf(changeLogMemberEntry);
                                if (bb > -1) {
                                    changeLogMemberEntry2 = (ChangeLogMemberEntry) arrayList1.remove(bb);
                                }

                                if (changeLogMemberEntry2 != null) {
                                    if (!changeLogMemberEntry2.getFieldDescriptor().equals(changeLogMemberEntry.getFieldDescriptor())) {
                                        this.logError(
                                                "Field '"
                                                        + changeLogMemberEntry2.getFieldName()
                                                        + "' in class '"
                                                        + string2
                                                        + "' has type '"
                                                        + ConstantPoolEntry.descriptorToJavaType(changeLogMemberEntry2.getFieldDescriptor())
                                                        + "' in this log but type '"
                                                        + ConstantPoolEntry.descriptorToJavaType(changeLogMemberEntry.getFieldDescriptor())
                                                        + "' in '"
                                                        + changeLogMapping2.changeLogName
                                                        + "'. Logs could not be fully merged. You must distribute this application as a whole. (2)"
                                        );
                                        if (changeLogMemberEntry.hasSetterMethod()) {
                                            if (changeLogMemberEntry2.hasSetterMethod()) {
                                                if (!changeLogMemberEntry2.getSetterMethodName().equals(changeLogMemberEntry.getSetterMethodName())) {
                                                    this.logError(
                                                            "Method '"
                                                                    + changeLogMemberEntry2.getSetterMethodName()
                                                                    + '('
                                                                    + changeLogMemberEntry2.getFieldTypeName()
                                                                    + ")' in class '"
                                                                    + string2
                                                                    + "' in this log is inconsistent with method '"
                                                                    + changeLogMemberEntry.getSetterMethodName()
                                                                    + '('
                                                                    + changeLogMemberEntry.getFieldTypeName()
                                                                    + ")' in '"
                                                                    + changeLogMapping2.changeLogName
                                                                    + "'. Logs could not be fully merged. You must distribute this application as a whole. (1)"
                                                    );
                                                }

                                                if (!changeLogMemberEntry2.getGetterMethodName().equals(changeLogMemberEntry.getGetterMethodName())) {
                                                    this.logError(
                                                            "Method '"
                                                                    + changeLogMemberEntry2.getFieldTypeName()
                                                                    + " "
                                                                    + changeLogMemberEntry2.getGetterMethodName()
                                                                    + "()' in class '"
                                                                    + string2
                                                                    + "' in this log is inconsistent with method '"
                                                                    + changeLogMemberEntry.getFieldTypeName()
                                                                    + " "
                                                                    + changeLogMemberEntry.getGetterMethodName()
                                                                    + "()' in '"
                                                                    + changeLogMapping2.changeLogName
                                                                    + "'. Logs could not be fully merged. You must distribute this application as a whole. (1)"
                                                    );
                                                }

                                                if (changeLogMemberEntry2.getAltGetterMethodName() != null
                                                        && !changeLogMemberEntry2.getAltGetterMethodName().equals(changeLogMemberEntry.getAltGetterMethodName())) {
                                                    this.logError(
                                                            "Method '"
                                                                    + changeLogMemberEntry2.getFieldTypeName()
                                                                    + " "
                                                                    + changeLogMemberEntry2.getAltGetterMethodName()
                                                                    + "()' in class '"
                                                                    + string2
                                                                    + "' in this log is inconsistent with method '"
                                                                    + changeLogMemberEntry.getFieldTypeName()
                                                                    + " "
                                                                    + changeLogMemberEntry.getAltGetterMethodName()
                                                                    + "()' in '"
                                                                    + changeLogMapping2.changeLogName
                                                                    + "'. Logs could not be fully merged. You must distribute this application as a whole. (1)"
                                                    );
                                                }

                                                if (changeLogMemberEntry2.isAlternateVariant() != changeLogMemberEntry.isAlternateVariant()) {
                                                    this.logError(
                                                            "Field '"
                                                                    + changeLogMemberEntry2.getFieldName()
                                                                    + "' in class '"
                                                                    + string2
                                                                    + "' in this log is inconsistent in nature with the equivalent field in '"
                                                                    + changeLogMapping2.changeLogName
                                                                    + "'. Logs could not be fully merged. You must distribute this application as a whole. (1)"
                                                    );
                                                }
                                            } else {
                                                changeLogMemberEntry2.setSetterMethodName(changeLogMemberEntry.getSetterMethodName());
                                                changeLogMemberEntry2.setGetterMethodName(changeLogMemberEntry.getGetterMethodName());
                                                changeLogMemberEntry2.setAltGetterMethodName(changeLogMemberEntry.getAltGetterMethodName());
                                                changeLogMemberEntry2.setNewSetterMethodName(changeLogMemberEntry.getNewSetterMethodName());
                                                changeLogMemberEntry2.setNewGetterMethodName(changeLogMemberEntry.getNewGetterMethodName());
                                                changeLogMemberEntry2.setNewAltGetterMethodName(changeLogMemberEntry.getNewAltGetterMethodName());
                                                changeLogMemberEntry2.setAlternateVariant(changeLogMemberEntry.isAlternateVariant());
                                            }
                                        }
                                    }

                                    twoKeyMap.putValue(string, new IdentityValueHolder(changeLogMemberEntry), new IdentityValueHolder(changeLogMemberEntry2));
                                    continue;
                                }

                                Iterator iterator = arrayList1.iterator();

                                while (iterator.hasNext()) {
                                    ChangeLogMemberEntry changeLogMemberEntry1 = (ChangeLogMemberEntry) iterator.next();
                                    if (changeLogMemberEntry1.getFieldName() == null) {
                                        changeLogMemberEntry2 = changeLogMemberEntry1;
                                        iterator.remove();
                                    }
                                }

                                if (changeLogMemberEntry2 != null) {
                                    this.forwardClassEntries.removeValue(string, changeLogMemberEntry2);
                                    this.forwardClassEntries.addValue(string, changeLogMemberEntry);
                                    twoKeyMap.putValue(string, new IdentityValueHolder(changeLogMemberEntry2), new IdentityValueHolder(changeLogMemberEntry));
                                } else {
                                    this.forwardClassEntries.addValue(string, changeLogMemberEntry);
                                }
                                continue;
                            }

                            setMultiMap = this.forwardClassEntries;
                        } else {
                            setMultiMap = this.forwardClassEntries;
                        }

                        setMultiMap.addValue(string, changeLogMemberEntry);
                    }
                } else {
                    this.forwardClassEntries.addValues(string, set1);
                }
            }
        }
    }

    @Override
    public void setAutoReflectionClass(Object object) {
        this.autoReflectionClass = (String) object;
    }

    public void removeExcludedParameterObfuscatedMethods(MethodParameterExclusions methodParameterExclusions, MethodOverrideAnalyzer methodOverrideAnalyzer) {
        if (this.parameterObfuscatedMethods != null) {
            Iterator iterator = this.parameterObfuscatedMethods.iterator();

            while (iterator.hasNext()) {
                AbstractMethodInfo abstractMethodInfo = (AbstractMethodInfo) iterator.next();
                if (abstractMethodInfo.isProgramMember()) {
                    AbstractMethodInfo abstractMethodInfo1 = methodOverrideAnalyzer.findRootMethod(abstractMethodInfo);
                    if (!methodParameterExclusions.isMethodExcluded((MethodInfo) abstractMethodInfo)
                            || abstractMethodInfo1 != null
                            && abstractMethodInfo1.isProgramMember()
                            && !methodParameterExclusions.isMethodExcluded((MethodInfo) abstractMethodInfo1)) {
                        iterator.remove();
                        this.logWarning(
                                "Method '"
                                        + abstractMethodInfo.getModifierString()
                                        + " "
                                        + abstractMethodInfo.getOriginalNameWithParameters()
                                        + "' in class '"
                                        + abstractMethodInfo.getOriginalDottedName()
                                        + "' could not have its parameter list obfuscated because it has been directly or indirectly excluded from "
                                        + "Method Parameter Obfuscation"
                                        + " and the input change log is specified as loose. Its parameter list will not be obfuscated. (A)"
                        );
                    }
                }
            }
        }
    }

    public String getModuleAutoReflectionClass(Object object) {
        return (String) this.moduleAutoReflectionClasses.get(object);
    }

    public static String getSimpleName(String string) {
        int ba = string.lastIndexOf(46);
        return ba == -1 ? string : string.substring(ba + 1);
    }

    public boolean isParameterObfuscatedMethod(Object object) {
        return this.parameterObfuscatedMethods != null && this.parameterObfuscatedMethods.contains(object);
    }

    public void mergeMemberClassEntries(ChangeLogMapping changeLogMapping2, HashMap hashMap, TwoKeyMap twoKeyMap) throws ZkmException, IOException {
        if (this.memberClassEntries == null) {
            this.memberClassEntries = ZkmUtils.copyToHashMap(changeLogMapping2.memberClassEntries);
        } else {
            Iterator iterator = changeLogMapping2.memberClassEntries.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                String string = (String) entry.getKey();
                ObjectPair objectPair = (ObjectPair) entry.getValue();
                if (!this.memberClassEntries.containsKey(string)) {
                    this.memberClassEntries.put(string, objectPair);
                }
            }

            IdentityValueHolder identityValueHolder1 = new IdentityValueHolder();
            Iterator iterator1 = this.memberClassEntries.entrySet().iterator();

            while (iterator1.hasNext()) {
                Entry entry1 = (Entry) iterator1.next();
                String string2 = (String) entry1.getKey();
                String string1 = getPackageName(string2);
                ObjectPair objectPair1 = (ObjectPair) entry1.getValue();
                ChangeLogMemberEntry changeLogMemberEntry = (ChangeLogMemberEntry) objectPair1.getFirst();
                ChangeLogMemberEntry changeLogMemberEntry1 = (ChangeLogMemberEntry) objectPair1.getSecond();
                identityValueHolder1.setValue(changeLogMemberEntry);
                IdentityValueHolder identityValueHolder;
                if ((identityValueHolder = (IdentityValueHolder) hashMap.get(identityValueHolder1)) != null) {
                    objectPair1.setFirst(identityValueHolder.getValue());
                }

                identityValueHolder1.setValue(changeLogMemberEntry1);
                if ((identityValueHolder = (IdentityValueHolder) twoKeyMap.getValue(string1, identityValueHolder1)) != null) {
                    objectPair1.setSecond(identityValueHolder.getValue());
                }
            }
        }
    }

    public static String substringAfterLast(String string, int ba) {
        int bb = string.lastIndexOf(ba);
        return bb != -1 ? string.substring(bb + 1) : string;
    }

    public String getSourceFileSuffix() {
        if (this.sourceFileSuffix != null) {
            return this.sourceFileSuffix;
        }

        this.sourceFileSuffix = describeSourceFile(this.changeLogName, this.scriptEnvironment);
        return this.sourceFileSuffix;
    }

    public Set getParameterObfuscatedMethods() {
        return this.parameterObfuscatedMethods;
    }

    @Override
    public void setMethodParameterChangeClasses(Object object) {
        List list1 = (List) object;
        ObjectTriple objectTriple = new ObjectTriple(list1.get(0), list1.get(1), list1.get(2));
        if (this.canonicalClassTriples.containsKey(objectTriple)) {
            objectTriple = (ObjectTriple) this.canonicalClassTriples.get(objectTriple);
        }

        this.methodParameterChangeClasses = objectTriple;
    }

    public boolean hasOriginalMethod(String string, String string1, String[] strings, String string2) throws IOException {
        if (this.reverseMethodMappings == null) {
            this.buildReverseMethodMappings();
        }

        if (this.reverseMethodMappings != null) {
            String string3 = ZkmUtils.slashesToDots(string);
            ComparableKeyValue comparableKeyValue = this.createNameTypeKey(string1, string2);
            String string4 = this.joinTypeNames(strings);
            return this.reverseMethodMappings.containsKeys(string3, comparableKeyValue, string4);
        } else {
            return false;
        }
    }

    @Override
    public void addModuleAutoReflectionClass(Object object, Object object1) {
        String string = (String) this.moduleAutoReflectionClasses.put(object1, object);
    }

    public void addRawMethodMapping(String string, String string1, String string2, String string3, String string4) throws IOException {
        String string5 = ZkmUtils.slashesToDots(string);
        ChangeLogMemberKey changeLogMemberKey = this.createMethodKeyForDescriptor(string1, string2, string3);
        NewNameMapping newNameMapping = this.createNewNameMapping(string4, string2);
        this.rawMethodMappings.addValue(string5, changeLogMemberKey, newNameMapping);
    }

    public void removePackageMapping(String string) throws IOException {
        String string1 = string;
        if (string1 != null) {
            string1 = ZkmUtils.slashesToDots(string1);
        }

        if (this.packageMappings != null) {
            String string2 = (String) this.packageMappings.get(string1);
            this.packageMappings.remove(string1);
            this.reversePackageMappings.removeValue(string2, string1);
            this.logWarning("Package '" + string1 + "' no longer appears in the opened classes and will be assumed to be no longer used.");
        }
    }

    public boolean isLookupClass(String string) throws IOException {
        String string1 = ZkmUtils.slashesToDots(string);
        return this.autoReflectionClass != null && this.autoReflectionClass.equals(string1)
                || this.referenceObfuscationClass != null && this.referenceObfuscationClass.equals(string1);
    }

    public void removeRawMethodMapping(String string, String string1, String string2, String string3) throws IOException {
        String string4 = ZkmUtils.slashesToDots(string);
        ChangeLogMemberKey changeLogMemberKey = this.createMethodKeyForDescriptor(string1, string2, string3);
        this.rawMethodMappings.removeInnerKey(string4, changeLogMemberKey);
    }

    public boolean isNewPackageName(String string) throws IOException {
        return this.reversePackageMappings != null && this.reversePackageMappings.containsKey(ZkmUtils.slashesToDots(string));
    }

    public static ListMultimap groupEntriesByClass(Set set1) {
        ListMultimap listMultimap = new ListMultimap(set1.size());
        Iterator iterator = set1.iterator();

        while (iterator.hasNext()) {
            ChangeLogMemberEntry changeLogMemberEntry = (ChangeLogMemberEntry) iterator.next();
            listMultimap.addValue(changeLogMemberEntry.getClassName(), changeLogMemberEntry);
        }

        return listMultimap;
    }

    public boolean hasClassesOpenedFromFileSystem() {
        return this.scriptEnvironment.isLooseClassFilesLoaded();
    }

    public void renameRawMethodMapping(String string, String string1, String string2, String string3, String string4) throws IOException {
        String string5 = ZkmUtils.slashesToDots(string);
        ChangeLogMemberKey changeLogMemberKey = this.createMethodKeyForDescriptor(string1, string2, string3);
        List list1 = this.rawMethodMappings.getValues(string5, changeLogMemberKey);
        if (list1.size() == 1) {
            NewNameMapping newNameMapping = (NewNameMapping) list1.get(0);
            this.rawMethodMappings.removeInnerKey(string5, changeLogMemberKey);
            ChangeLogMemberKey changeLogMemberKey1 = this.createMethodKeyForDescriptor(string4, string2, changeLogMemberKey.getType());
            this.rawMethodMappings.addValue(string5, changeLogMemberKey1, newNameMapping);
        } else {
            this.reportFatalError(
                    "Class '"
                            + string5
                            + "' has multiple method mappings for method '"
                            + changeLogMemberKey.getType()
                            + " "
                            + changeLogMemberKey.getName()
                            + '('
                            + string2
                            + ")'."
            );
        }
    }

    public boolean isFieldRenamed(String string, FieldSignature fieldSignature) throws IOException {
        return this.getResolvedNewFieldName(string, fieldSignature) != null;
    }

    public void logError(String string) {
        this.scriptEnvironment.logError("Input change log '" + this.changeLogName + "': " + string + this.getSourceFileSuffix());
    }

    public static MethodSignature createChangedMethodSignature(AbstractMethodInfo abstractMethodInfo, AddedParameter[] addedParameters1) {
        String string = ChangedMethodDescriptor.buildParameterDescriptor(
                MethodSignature.splitParameterDescriptors(abstractMethodInfo.getDescriptor()), addedParameters1
        );
        return new MethodSignature(abstractMethodInfo.getJvmName(), string, abstractMethodInfo.getReturnDescriptor());
    }

    @Override
    public void addClassMapping(Object object, Object object1) {
        String string = (String) object1;
        String string1 = (String) object;
        if (this.classMappings == null) {
            int ba = ZkmUtils.getPrimeCapacity(this.classCount);
            this.classMappings = ZkmUtils.createHashMap(ba);
            this.reverseClassMappings = ZkmUtils.createHashMap(ba);
        }

        String string3 = ((java.lang.String) (this.classMappings.put(string, string1)));
        Map map1;
        if (string3 != null) {
            this.reportError("Class \"" + string + "\" appears more than once.");
            map1 = this.reverseClassMappings;
        } else {
            map1 = this.reverseClassMappings;
        }

        String string2 = ((java.lang.String) (map1.put(string1, string)));
        if (string2 != null) {
            this.reportError("Classes \"" + string2 + "\" and \"" + string + "\" are both mapped to \"" + string1 + "\"");
        }
    }

    public boolean isLoose() {
        return this.looseChangeLog;
    }

    public void addFlowObfuscationEntry(String string, List list1, boolean bl, String string1, String string2) throws ZkmException, IOException {
        this.flowObfuscationDataPresent = true;
        String string3 = bl ? "" : " for package '" + string2 + "'";
        if (this.classMappings != null && this.classMappings.containsKey(string)) {
            String string4 = ZkmUtils.dotsToSlashes((String) ZkmUtils.mapOrSelf(string, this.classMappings));
            boolean bl1 = false;
            String string5 = null;
            String string6 = null;
            String string7 = null;
            String string8 = null;
            String string9 = null;
            boolean bl2 = false;
            if (list1.size() < 2) {
                this.flowEntryList.add(null);
            } else {
                if (((String) list1.get(0)).charAt(0) == '0') {
                    for (int i = 0; i < list1.size(); i++) {
                        String string10 = (String) list1.get(i);
                        if (string10.length() > 1) {
                            char bb = string10.charAt(0);
                            if (bb == '0') {
                                string10 = string10.substring(1);
                                ChangeLogClassKey changeLogClassKey = this.findFieldKeyByHash(string, string10);
                                string5 = changeLogClassKey.getName();
                                string6 = MethodSignature.javaTypeToDescriptor(changeLogClassKey.getType());
                                if (string6 == null) {
                                    this.reportError("Corrupt Data: '" + string10 + "' for " + string1 + string3 + " '" + ZkmUtils.slashesToDots(string) + "' (C)");
                                    bl1 = true;
                                    break;
                                }
                            } else {
                                string10 = string10.substring(1);
                                ChangeLogMemberKey changeLogMemberKey = this.findMethodKeyByHash(string, string10);
                                String string11 = changeLogMemberKey.getName();
                                switch (bb) {
                                    case 'a':
                                        string7 = string11;
                                        break;
                                    case 'b':
                                        string8 = string11;
                                        break;
                                    case 'c':
                                        string9 = string11;
                                }
                            }
                        } else {
                            bl2 = string10.charAt(0) >= 'A' && string10.charAt(0) <= 'Z';
                        }
                    }
                } else {
                    for (int i = 0; i < list1.size(); i++) {
                        if (i == 0) {
                            String string12 = (String) list1.get(i);
                            if ((string12.charAt(0) < '1' || string12.charAt(0) > '5')
                                    && "MQoSAkmVrHyDwxKuhLPOURetTIFYqWnpbzfjEsXcZlNCdvgJGBia".indexOf(string12.charAt(0)) == -1) {
                                this.reportError("Corrupt Data: '" + string12 + "' for " + string1 + string3 + " '" + ZkmUtils.slashesToDots(string) + "' (A)");
                                bl1 = true;
                                break;
                            }

                            try {
                                string5 = AbstractChangeLog.decodePrefixedName(string12, string4);
                            } catch (NumberFormatException numberFormatException) {
                                this.reportError("Corrupt Data: '" + string12 + "' for " + string1 + string3 + " '" + ZkmUtils.slashesToDots(string) + "' (B)");
                                bl1 = true;
                                break;
                            }

                            string6 = decodeFlowFieldType(string12, this.flowEntryList, string);
                            if (string6 == null) {
                                this.reportError("Corrupt Data: '" + string12 + "' for " + string1 + string3 + " '" + ZkmUtils.slashesToDots(string) + "' (C)");
                                bl1 = true;
                                break;
                            }
                        } else {
                            String string13 = (String) list1.get(i);
                            if (string13.length() > 1) {
                                String string14;
                                try {
                                    string14 = AbstractChangeLog.decodeName(string13.substring(1), string4);
                                } catch (NumberFormatException numberFormatException1) {
                                    this.reportError("Corrupt Data: '" + string13 + "' for " + string1 + string3 + " '" + ZkmUtils.slashesToDots(string) + "' (D)");
                                    bl1 = true;
                                    break;
                                }

                                switch (string13.charAt(0)) {
                                    case 'a':
                                        string7 = string14;
                                        break;
                                    case 'b':
                                        string8 = string14;
                                        break;
                                    case 'c':
                                        string9 = string14;
                                }
                            } else {
                                bl2 = string13.charAt(0) >= 'A' && string13.charAt(0) <= 'Z';
                            }
                        }
                    }
                }

                if (bl1) {
                    this.flowEntryList.add(null);
                } else {
                    ObservableHolder observableHolder = new ObservableHolder();
                    ChangeLogMemberEntry changeLogMemberEntry = new ChangeLogMemberEntry(
                            string, string5, string6, string7, string8, string9, bl2, string2, this, string1, observableHolder
                    );
                    if (!observableHolder.isValueNull()) {
                        this.reportError((String) observableHolder.getValue());
                    }

                    Set set1;
                    if (bl) {
                        boolean bl3 = this.traceBackEntries.add(changeLogMemberEntry);
                        if (!bl3) {
                            this.reportError(
                                    "Duplicate "
                                            + string1
                                            + " '"
                                            + ZkmUtils.slashesToDots(string)
                                            + "' entries with field name '"
                                            + changeLogMemberEntry.getFieldName()
                                            + "'"
                            );
                            set1 = this.flowDataClasses;
                        } else {
                            set1 = this.flowDataClasses;
                        }
                    } else {
                        boolean bl4 = this.forwardClassEntries.addValue(string2, changeLogMemberEntry);
                        if (!bl4) {
                            this.reportError(
                                    "Duplicate "
                                            + string1
                                            + string3
                                            + " '"
                                            + ZkmUtils.slashesToDots(string)
                                            + "' entries with field name '"
                                            + changeLogMemberEntry.getFieldName()
                                            + "'"
                            );
                            set1 = this.flowDataClasses;
                        } else {
                            set1 = this.flowDataClasses;
                        }
                    }

                    set1.add(string);
                    this.flowEntryList.add(changeLogMemberEntry);
                }
            }
        } else {
            this.reportError(
                    "Class '"
                            + ZkmUtils.slashesToDots(string)
                            + "' appears as a Flow Obfuscation "
                            + string1
                            + string3
                            + " but does not appear in the main part of the change log. (A)"
            );
            this.flowEntryList.add(null);
        }
    }

    public void resolveParameterChangeData(ClassResolver classResolver1) throws ZkmException, IOException {
        if (this.rawMethodMappings != null && this.hasParameterChangeData()) {
            this.parameterChangeKeysByMethod = ZkmUtils.createHashMap();
            this.addedParametersByMethod = ZkmUtils.createHashMap();
            this.parameterChangeKeysByClass = ZkmUtils.createHashMap();
            this.paramChangeNodeDataByMethod = ZkmUtils.createHashMap();
            this.paramChangeNodeDataByClass = ZkmUtils.createHashMap();
            ObservableHolder observableHolder = new ObservableHolder();
            Iterator iterator = this.rawMethodMappings.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                String string = (String) entry.getKey();
                String string1 = ZkmUtils.dotsToSlashes(string);
                ClassFileBase classFileBase = classResolver1.getClassFile(string1);
                Iterator iterator1 = ((ListMultimap) entry.getValue()).entrySet().iterator();

                while (iterator1.hasNext()) {
                    Entry entry1 = (Entry) iterator1.next();
                    ChangeLogMemberKey changeLogMemberKey = (ChangeLogMemberKey) entry1.getKey();
                    String string2 = MethodSignature.javaTypeToDescriptor(changeLogMemberKey.getType());
                    String string3 = changeLogMemberKey.getName();
                    MethodSignature methodSignature1 = new MethodSignature(
                            string3, MethodSignature.parseParameterList(changeLogMemberKey.getParameterTypes()), string2
                    );
                    NewNameMapping newNameMapping = (NewNameMapping) ((List) entry1.getValue()).get(0);
                    AbstractMethodInfo abstractMethodInfo = classFileBase.findMethod(methodSignature1);
                    String string8;
                    Map map2;
                    if (abstractMethodInfo == null) {
                        if (!methodSignature1.isStaticInitializer()) {
                            continue;
                        }

                        long bc = 3687986690820L;
                        string8 = string;
                        map2 = this.classMappings;
                    } else {
                        long bb = 3687986690820L;
                        string8 = string;
                        map2 = this.classMappings;
                    }

                    Map map1 = map2;
                    String string6 = string8;
                    String string4 = ZkmUtils.dotsToSlashes((String) ZkmUtils.mapOrSelf(string6, map1));
                    if (newNameMapping.hasParameterChangeData()) {
                        String string5 = newNameMapping.getParameterChangeData();
                        observableHolder.clearValue();
                        Long long1 = AbstractChangeLog.decodeParameterChangeData(
                                string5, MethodSignature.parseParameterList(newNameMapping.getNewParameterTypes()), observableHolder, string4
                        );
                        if (abstractMethodInfo != null) {
                            this.parameterChangeKeysByMethod.put(abstractMethodInfo, long1);
                            this.addedParametersByMethod.put(abstractMethodInfo, observableHolder.getValue());
                        } else {
                            this.parameterChangeKeysByClass.put(classFileBase, long1);
                        }
                    } else if (abstractMethodInfo != null) {
                        this.addedParametersByMethod.put(abstractMethodInfo, AddedParameter.EMPTY_ARRAY);
                    }

                    if (newNameMapping.hasParamChangeNodeData() && HiddenOptionFlags.DEBUG_PARAMETER_CHANGE_LOG) {
                        this.setParamChangeNodeDataPresent();
                        String string7 = newNameMapping.getParamChangeNodeData();
                        MutableLong mutableLong4 = new MutableLong();
                        MutableLong mutableLong = new MutableLong();
                        MutableLong mutableLong1 = new MutableLong();
                        MutableLong mutableLong2 = new MutableLong();
                        MutableLong mutableLong3 = new MutableLong();
                        AbstractChangeLog.decodeParamChangeNodeData(string7, mutableLong4, mutableLong, mutableLong1, mutableLong2, mutableLong3, string4);
                        long[] ba = new long[]{
                                mutableLong4.getValue(), mutableLong.getValue(), mutableLong1.getValue(), mutableLong2.getValue(), mutableLong3.getValue()
                        };
                        if (abstractMethodInfo != null) {
                            this.paramChangeNodeDataByMethod.put(abstractMethodInfo, ba);
                        } else {
                            this.paramChangeNodeDataByClass.put(classFileBase, ba);
                        }
                    }
                }
            }
        }
    }

    public boolean hasMethodMapping(String string, MethodSignature methodSignature1) throws IOException {
        if (string.equals("com/zelix/b/TestInterface11")) {
        }

        return this.methodMappings != null && this.methodMappings.containsKeys(ZkmUtils.slashesToDots(string), methodSignature1);
    }

    public boolean hasAddedParametersEntry(Object object) {
        return this.addedParametersByMethod == null ? false : this.addedParametersByMethod.containsKey(object);
    }

    @Override
    public void addMethodMappings(String string, ListMultimap listMultimap) {
        ChangeLogSimpleNode.getOpaqueStrings();
        if (this.rawMethodMappings == null) {
            this.rawMethodMappings = new NestedMultiMap(this.classCount, 5, 5);
        }

        this.rawMethodMappings.putMultimap(string, listMultimap);
        if (!this.hasParameterChangeData()) {
            Enumeration enumeration = listMultimap.allValues();

            while (enumeration.hasMoreElements()) {
                NewNameMapping newNameMapping = (NewNameMapping) enumeration.nextElement();
                if (newNameMapping.hasParameterChangeData()) {
                    this.setParameterChangeDataPresent();
                    break;
                }
            }
        }

        if (!this.hasParameterObfuscation()) {
            Enumeration enumeration1 = listMultimap.allValues();

            while (enumeration1.hasMoreElements()) {
                NewNameMapping newNameMapping1 = (NewNameMapping) enumeration1.nextElement();
                if (newNameMapping1.isParametersObfuscated()) {
                    this.markParameterObfuscationPresent();
                    break;
                }
            }
        }
    }

    public void applyLooseClassAndPackageExclusions(NameExclusionSet nameExclusionSet) {
        if (nameExclusionSet != null) {
            HashMap hashMap = null;
            TwoKeyMap twoKeyMap = null;
            HashSet hashSet = null;
            if (this.packageMappings != null) {
                hashSet = ZkmUtils.createHashSet();
                hashMap = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(this.packageMappings.size()));
                twoKeyMap = new TwoKeyMap(this.reversePackageMappings.getKeyCount());
                ArrayList arrayList = new ArrayList(this.packageMappings.keySet());
                Collections.sort(arrayList);

                for (int i = 0; i < arrayList.size(); i++) {
                    String string = (String) arrayList.get(i);
                    String string1 = (String) this.packageMappings.get(string);
                    if (nameExclusionSet.isPackageExcluded(ZkmUtils.dotsToSlashes(string))) {
                        if (!string.equals(string1)) {
                            hashSet.add(string);
                            hashMap.put(string, string);
                            this.logWarning(
                                    "Package '"
                                            + string
                                            + "' could not be renamed to '"
                                            + string1
                                            + "' because it has been excluded and the input change log is specified as loose. It will not be renamed."
                            );
                            hashMap.put(string, string);
                            twoKeyMap.putValue(string, string, string);
                        } else {
                            hashMap.put(string, string1);
                            twoKeyMap.putValue(string1, string, string);
                        }
                    } else {
                        String string2 = getPackageName(string);
                        String string3 = (String) this.packageMappings.get(string2);
                        String string4 = (String) hashMap.get(string2);
                        if (hashSet.contains(string2)) {
                            String string5 = derivePackageName(string1, string3, string4);
                            if (!string5.equals(string1)) {
                                hashSet.add(string);
                                this.logWarning(
                                        "Package '"
                                                + string
                                                + "' could not be renamed to '"
                                                + string1
                                                + "' because the mapping of its superpackage has been changed. It will be renamed to '"
                                                + string5
                                                + "'"
                                );
                            }

                            hashMap.put(string, string5);
                            twoKeyMap.putValue(string5, string, string);
                        } else if (nameExclusionSet.isPackageExcluded(ZkmUtils.dotsToSlashes(string2)) && !string1.startsWith(string3 + ".")) {
                            String string13 = derivePackageName(string1, string3, string4);
                            if (!string13.equals(string1)) {
                                hashSet.add(string);
                                this.logWarning(
                                        "Package '"
                                                + string
                                                + "' could not be renamed to '"
                                                + string1
                                                + "' because its superpackage has been excluded. It will be renamed to '"
                                                + string13
                                                + "'"
                                );
                            }

                            hashMap.put(string, string13);
                            twoKeyMap.putValue(string13, string, string);
                        } else {
                            hashMap.put(string, string1);
                            twoKeyMap.putValue(string1, string, string);
                        }
                    }
                }
            }

            if (hashMap != null) {
                this.packageMappings.clear();
                this.reversePackageMappings.clear();
                this.packageMappings = hashMap;
                this.reversePackageMappings = twoKeyMap;
            }

            HashMap hashMap1 = null;
            HashMap hashMap2 = null;
            if (this.classMappings != null) {
                hashMap1 = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(this.classMappings.size()));
                hashMap2 = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(this.reverseClassMappings.size()));
                Iterator iterator = this.classMappings.entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry entry = (Entry) iterator.next();
                    String string10 = (String) entry.getKey();
                    String string11 = (String) entry.getValue();
                    String string12 = getSimpleName(string10);
                    String string14 = getSimpleName(string11);
                    String string6 = getPackageName(string10);
                    String string7 = null;
                    if (hashMap != null) {
                        string7 = (String) hashMap.get(string6);
                    }

                    if (string7 == null) {
                        string7 = string6;
                    }

                    if (hashSet != null && hashSet.contains(string6)) {
                        String string15;
                        if (nameExclusionSet.isClassNameExcluded(ZkmUtils.dotsToSlashes(string10)) && !string12.equals(string14)) {
                            string15 = string7 + "." + string12;
                            this.logWarning(
                                    "Class '"
                                            + string10
                                            + "' could not be renamed to '"
                                            + string11
                                            + "' because it has been excluded and the mapping of its package name has been changed. It will be renamed to '"
                                            + string15
                                            + "'"
                            );
                        } else {
                            string15 = string7 + "." + string14;
                            this.logWarning(
                                    "Class '"
                                            + string10
                                            + "' could not be renamed to '"
                                            + string11
                                            + "' because the mapping of its package name has been changed. It will be renamed to '"
                                            + string15
                                            + "'"
                            );
                        }

                        hashMap1.put(string10, string15);
                        String string16 = ((java.lang.String) (hashMap2.put(string15, string10)));
                        if (string16 != null) {
                            this.reportFatalError(
                                    "Class name clash after package and class exclusions.  Both '"
                                            + string10
                                            + "' and '"
                                            + string16
                                            + "' are renamed to '"
                                            + string15
                                            + "'."
                            );
                        }
                    } else {
                        String string8;
                        if (nameExclusionSet.isClassNameExcluded(ZkmUtils.dotsToSlashes(string10)) && !string12.equals(string14)) {
                            string8 = string7 + "." + string12;
                            this.logWarning(
                                    "Class '"
                                            + string10
                                            + "' could not be renamed to '"
                                            + string11
                                            + "' because it has been excluded. It will be renamed to '"
                                            + string8
                                            + "'"
                            );
                        } else {
                            string8 = string11;
                        }

                        hashMap1.put(string10, string8);
                        String string9 = ((java.lang.String) (hashMap2.put(string8, string10)));
                        if (string9 != null) {
                            this.reportFatalError(
                                    "Class name clash after class exclusions.  Both '" + string10 + "' and '" + string9 + "' are renamed to '" + string8 + "'."
                            );
                        }
                    }
                }
            }

            if (hashMap1 != null) {
                this.classMappings.clear();
                this.reverseClassMappings.clear();
                this.classMappings = hashMap1;
                this.reverseClassMappings = hashMap2;
            }
        }
    }

    public void removeMethodMapping(String string, MethodSignature methodSignature1) throws IOException {
        if (this.methodMappings != null) {
            MethodSignature methodSignature2 = (MethodSignature) this.methodMappings.removeValue(ZkmUtils.slashesToDots(string), methodSignature1);
        }
    }

    public ChangeLogMemberKey findMethodKeyByHash(String string, Object object) {
        Iterator iterator = this.rawMethodMappings.getMultimap(string).keySet().iterator();

        while (iterator.hasNext()) {
            ChangeLogMemberKey changeLogMemberKey = (ChangeLogMemberKey) iterator.next();
            if (ZkmUtils.md5Hex(
                            changeLogMemberKey.getName()
                                    + MethodSignature.parseParameterList(changeLogMemberKey.getParameterTypes())
                                    + MethodSignature.javaTypeToDescriptor(changeLogMemberKey.getType())
                                    + "java.lang.Object[]"
                    )
                    .equals(object)) {
                return changeLogMemberKey;
            }
        }

        return null;
    }

    public ChangeLogClassKey createFieldKey(String string, String string1) {
        String string2;
        boolean bl;
        if (ConstantPoolEntry.isFieldDescriptor(string1)) {
            string2 = ConstantPoolEntry.descriptorToJavaType(string1);
            bl = this.fieldsHaveTypes;
        } else {
            string2 = string1;
            bl = this.fieldsHaveTypes;
        }

        ChangeLogClassKey changeLogClassKey;
        if (bl) {
            changeLogClassKey = new ChangeLogClassKey(string, string2, 0);
        } else {
            changeLogClassKey = new ChangeLogClassKey(string, null, 0);
        }

        return changeLogClassKey;
    }

    @Override
    public void addPackageMapping(Object object, Object object1) {
        String string = (String) object;
        String string1 = (String) object1;
        this.putPackageMapping(string, string1, true);
    }

    public static String derivePackageName(String string, String string1, String string2) {
        int ba = ZkmStringUtils.countChar(string, '.') + (string.length() > 0 ? 1 : 0);
        int bb = ZkmStringUtils.countChar(string1, '.') + (string1.length() > 0 ? 1 : 0);
        int bc = ZkmStringUtils.countChar(string2, '.') + (string2.length() > 0 ? 1 : 0);
        String string3;
        if (ba == bb + 1) {
            string3 = string2 + "." + getSimpleName(string);
        } else if (ba == bb) {
            if (!string.equals(string1) && bc >= bb) {
                string3 = getPackageName(string2) + "." + getSimpleName(string);
            } else {
                string3 = string2;
            }
        } else if (ba < bb) {
            string3 = string2;
        } else {
            int bd = 0;

            for (int i = 0; i < bb; i++) {
                bd = string.indexOf(46, bd);
            }

            string3 = string2 + "." + string.substring(bd + 1);
        }

        return string3;
    }

    public String getNewMethodName(String string, String string1, String[] strings, String string2) throws IOException {
        if (this.rawMethodMappings != null) {
            String string3 = ZkmUtils.slashesToDots(string);
            String string4 = this.joinTypeNames(strings);
            String string5 = string2;
            String string6 = string4;
            String string7 = string1;
            ChangeLogMemberKey changeLogMemberKey = this.createMethodKey(string7, string6, string5);
            List list1 = this.rawMethodMappings.getValues(string3, changeLogMemberKey);
            if (list1 != null && list1.size() > 0) {
                if (list1.size() == 1) {
                    NewNameMapping newNameMapping = (NewNameMapping) list1.get(0);
                    return !string1.equals(newNameMapping.getNewName()) ? newNameMapping.getNewName() : null;
                } else {
                    this.reportError("Class '" + string3 + "' has multiple methods '" + string1 + "(" + string4 + ")'.");
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void applyLooseMethodExclusions(NameExclusionSet nameExclusionSet, HashMap hashMap, ClassMemberLookup classMemberLookup1) throws IOException {
        if (this.methodMappings != null) {
            TwoKeyMap twoKeyMap = new TwoKeyMap(this.methodMappings.getKeyCount());
            TwoKeyMap twoKeyMap1 = new TwoKeyMap(this.methodMappings.getKeyCount());
            Iterator iterator = this.methodMappings.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                String string = (String) entry.getKey();
                Iterator iterator1 = ((Map) entry.getValue()).entrySet().iterator();

                while (iterator1.hasNext()) {
                    Entry entry1 = (Entry) iterator1.next();
                    MethodSignature methodSignature1 = (MethodSignature) entry1.getKey();
                    ChangeLogMethodSignature changeLogMethodSignature = (ChangeLogMethodSignature) entry1.getValue();
                    ProgramClass programClass1 = ClassHierarchyNode.findProgramClass(ZkmUtils.dotsToSlashes(string));
                    MethodInfo methodInfo1 = classMemberLookup1.findDeclaredMethod(programClass1, methodSignature1);
                    String string1 = (String) ZkmUtils.mapOrSelf(ZkmUtils.dotsToSlashes(string), hashMap);
                    string1 = ZkmUtils.slashesToDots(string1);
                    if (nameExclusionSet.isMethodExcluded(methodInfo1)) {
                        if (!methodSignature1.getName().equals(changeLogMethodSignature.getName())) {
                            ChangeLogMethodSignature changeLogMethodSignature1 = new ChangeLogMethodSignature(
                                    methodSignature1.getName(),
                                    changeLogMethodSignature.getParameterDescriptor(),
                                    changeLogMethodSignature.getReturnDescriptor(),
                                    changeLogMethodSignature.isParametersObfuscated()
                            );
                            this.logWarning(
                                    "Method '"
                                            + methodSignature1.getNameWithParameters(hashMap)
                                            + "' in class '"
                                            + string1
                                            + "' could not be renamed to '"
                                            + changeLogMethodSignature.getName()
                                            + "' because it has been excluded and the input change log is specified as loose. It will not be renamed."
                            );
                            twoKeyMap.putValue(string, methodSignature1, changeLogMethodSignature1);
                            MethodSignature methodSignature2 = (MethodSignature) twoKeyMap1.putValue(string, changeLogMethodSignature1, methodSignature1);
                            if (methodSignature2 != null) {
                                this.reportFatalError(
                                        "Method name clash in class '"
                                                + string1
                                                + "' after method exclusions with a loose input change log.  Both '"
                                                + methodSignature1.getNameWithParameters(hashMap)
                                                + "' and '"
                                                + methodSignature2.getNameWithParameters(hashMap)
                                                + "' are renamed to '"
                                                + changeLogMethodSignature1.getNameWithParameters(hashMap)
                                                + "'. (1)"
                                );
                            }
                        } else {
                            twoKeyMap.putValue(string, methodSignature1, changeLogMethodSignature);
                            MethodSignature methodSignature3 = (MethodSignature) twoKeyMap1.putValue(string, changeLogMethodSignature, methodSignature1);
                            if (methodSignature3 != null) {
                                this.reportFatalError(
                                        "Method name clash in class '"
                                                + string1
                                                + "' after method exclusions with a loose input change log.  Both '"
                                                + methodSignature1.getNameWithParameters(hashMap)
                                                + "' and '"
                                                + methodSignature3.getNameWithParameters(hashMap)
                                                + "' are renamed to '"
                                                + changeLogMethodSignature.getNameWithParameters(hashMap)
                                                + "'. (2)"
                                );
                            }
                        }
                    } else {
                        twoKeyMap.putValue(string, methodSignature1, changeLogMethodSignature);
                        MethodSignature methodSignature4 = (MethodSignature) twoKeyMap1.putValue(string, changeLogMethodSignature, methodSignature1);
                        if (methodSignature4 != null) {
                            this.reportFatalError(
                                    "Method name clash in class '"
                                            + string1
                                            + "' after method exclusions with a loose input change log.  Both '"
                                            + methodSignature1.getNameWithParameters(hashMap)
                                            + "' and '"
                                            + methodSignature4.getNameWithParameters(hashMap)
                                            + "' are renamed to '"
                                            + changeLogMethodSignature.getNameWithParameters(hashMap)
                                            + "'. (3)"
                            );
                        }
                    }
                }
            }

            TwoKeyMap twoKeyMap2 = this.methodMappings;
            this.methodMappings = twoKeyMap;
            twoKeyMap1.clear();
            twoKeyMap2.clear();
        }
    }

    public void mergeMethodMappings(ChangeLogMapping changeLogMapping2, List list1) {
        if (this.rawMethodMappings != null && this.methodsHaveReturnTypes != changeLogMapping2.methodsHaveReturnTypes) {
            this.reportError(
                    "Change log methods "
                            + (this.methodsHaveReturnTypes ? "have return types" : "have no return types")
                            + " which is inconsistent with change log \""
                            + changeLogMapping2.changeLogName
                            + "\" whose methods "
                            + (changeLogMapping2.methodsHaveReturnTypes ? "have return types" : "have no return types")
                            + ". All method entries in change logs being merged must have return types or no method entries can have return types."
            );
        } else if (this.rawMethodMappings != null && this.hasParameterChangeData() != changeLogMapping2.hasParameterChangeData()) {
            this.reportError(
                    "Change log methods "
                            + (this.hasParameterChangeData() ? "have new method parameter data" : "have no new method parameter data")
                            + " which is inconsistent with change log \""
                            + changeLogMapping2.changeLogName
                            + "\" whose methods "
                            + (changeLogMapping2.hasParameterChangeData() ? "have new method parameter data" : "have no new method parameter data")
                            + ". All method entries in change logs being merged must have new method parameter data or no method entries can have new method parameter data."
            );
        } else {
            int ba = list1.size();

            for (int i = 0; i < ba; i++) {
                String string = (String) list1.get(i);
                ListMultimap listMultimap = changeLogMapping2.rawMethodMappings.getMultimap(string);
                if (listMultimap != null) {
                    EnumerationBackedList enumerationBackedList = new EnumerationBackedList(listMultimap.keys());
                    Collections.sort(enumerationBackedList);

                    for (int j = 0; j < enumerationBackedList.size(); j++) {
                        ChangeLogMemberKey changeLogMemberKey = (ChangeLogMemberKey) enumerationBackedList.get(j);
                        String string1 = changeLogMemberKey.getName();
                        String string2 = changeLogMemberKey.getType();
                        String string3 = changeLogMemberKey.getParameterTypes();
                        List list2 = listMultimap.getValues(changeLogMemberKey);
                        if (list2.size() > 1) {
                            if (changeLogMapping2.methodsHaveReturnTypes) {
                                this.reportError(
                                        "More than one method \""
                                                + string2
                                                + " "
                                                + string1
                                                + "("
                                                + string3
                                                + ")\" appears in class \""
                                                + string
                                                + "\" in log \""
                                                + changeLogMapping2.changeLogName
                                                + "\"."
                                );
                            } else {
                                this.reportError(
                                        "More than one method \""
                                                + string1
                                                + "("
                                                + string3
                                                + ")\" appears in class \""
                                                + string
                                                + "\" in log \""
                                                + changeLogMapping2.changeLogName
                                                + "\" but there are no method return types to allow the mappings to be distinguished."
                                );
                            }
                        } else {
                            NewNameMapping newNameMapping = (NewNameMapping) list2.get(0);
                            if (this.rawMethodMappings != null) {
                                List list3 = this.rawMethodMappings.getValues(string, changeLogMemberKey);
                                if (list3 != null && list3.size() > 0) {
                                    if (list3.size() > 1) {
                                        if (this.methodsHaveReturnTypes) {
                                            this.reportError(
                                                    "More than one method \"" + string2 + " " + string1 + "(" + string3 + ")\" appears in class \"" + string + "\"."
                                            );
                                        } else {
                                            this.reportError(
                                                    "More than one method \""
                                                            + string1
                                                            + "("
                                                            + string3
                                                            + ")\" appears in class \""
                                                            + string
                                                            + "\" there are no method return types to allow the mappings to be distinguished."
                                            );
                                        }
                                    } else {
                                        NewNameMapping newNameMapping1 = (NewNameMapping) list3.get(0);
                                        if (!newNameMapping1.equals(newNameMapping)) {
                                            if (newNameMapping1.getNewName().equals(newNameMapping.getNewName())
                                                    && newNameMapping1.getEffectiveParameterTypes().equals(newNameMapping.getEffectiveParameterTypes())) {
                                                if (newNameMapping1.getParameterChangeData() == null && newNameMapping.getParameterChangeData() != null
                                                        || newNameMapping1.getParameterChangeData() != null && newNameMapping.getParameterChangeData() == null
                                                        || newNameMapping1.getParameterChangeData() != null
                                                        && newNameMapping.getParameterChangeData() != null
                                                        && !newNameMapping1.getParameterChangeData().equals(newNameMapping.getParameterChangeData())) {
                                                    this.logWarning(
                                                            "Method \""
                                                                    + (string2 != null ? string2 + " " : "")
                                                                    + string1
                                                                    + "("
                                                                    + string3
                                                                    + ")\" mapping clash in class \""
                                                                    + string
                                                                    + "\" while merging with \""
                                                                    + changeLogMapping2.changeLogName
                                                                    + "\". Parameter change data is different : '"
                                                                    + newNameMapping1.getParameterChangeData()
                                                                    + "'!='"
                                                                    + newNameMapping.getParameterChangeData()
                                                                    + ". The data from '"
                                                                    + this.getChangeLogName()
                                                                    + "' will be used."
                                                    );
                                                } else {
                                                    this.logWarning(
                                                            "Method \""
                                                                    + (string2 != null ? string2 + " " : "")
                                                                    + string1
                                                                    + "("
                                                                    + string3
                                                                    + ")\" mapping clash in class \""
                                                                    + string
                                                                    + "\" while merging with \""
                                                                    + changeLogMapping2.changeLogName
                                                                    + "\". Change log '"
                                                                    + this.getChangeLogName()
                                                                    + "' says "
                                                                    + (newNameMapping1.isParametersObfuscated() ? "" : "not ")
                                                                    + "parameter obfuscated but '"
                                                                    + changeLogMapping2.changeLogName
                                                                    + "' says "
                                                                    + (newNameMapping.isParametersObfuscated() ? "" : "not ")
                                                                    + "parameter obfuscated. Will be treated as "
                                                                    + (newNameMapping1.isParametersObfuscated() ? "" : "not ")
                                                                    + "parameter obfuscated."
                                                    );
                                                }
                                            } else {
                                                this.logWarning(
                                                        "Method \""
                                                                + (string2 != null ? string2 + " " : "")
                                                                + string1
                                                                + "("
                                                                + string3
                                                                + ")\" mapping clash in class \""
                                                                + string
                                                                + "\" while merging with \""
                                                                + changeLogMapping2.changeLogName
                                                                + "\". Will be mapped to \""
                                                                + newNameMapping1.getNameWithParameters()
                                                                + "\" and not \""
                                                                + newNameMapping.getNameWithParameters()
                                                                + "\""
                                                                + (newNameMapping1.isParametersObfuscated() ? " and will be parameter obfuscated." : ".")
                                                );
                                            }
                                        }
                                    }
                                } else {
                                    this.putRawMethodMapping(string, changeLogMemberKey, newNameMapping);
                                }
                            } else {
                                this.putRawMethodMapping(string, changeLogMemberKey, newNameMapping);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void addFieldMappings(Object object, Object object1) {
        ListMultimap listMultimap = (ListMultimap) object1;
        String string = (String) object;
        if (this.rawFieldMappings == null) {
            this.rawFieldMappings = new NestedMultiMap(this.classCount, 5, 5);
        }

        this.rawFieldMappings.putMultimap(string, listMultimap);
    }

    public NewNameMapping createNewNameMapping(String string, String string1) {
        NewNameMapping newNameMapping;
        if (this.hasParameterChangeData()) {
            String string2 = this.mapTypeList(string1, this.classMappings);
            newNameMapping = new NewNameMapping(string, string2);
        } else {
            newNameMapping = new NewNameMapping(string);
        }

        return newNameMapping;
    }

    public static String readChangeLogEncoding(File file1) throws ZkmException, IOException {
        BufferedReader bufferedReader = null;
        ObservableHolder observableHolder = new ObservableHolder(null);
        String string = null;

        try {
            bufferedReader = ZkmFileUtils.openReaderDetectingCharset(file1, observableHolder);
            string = bufferedReader.readLine();
        } catch (IOException iOException1) {
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException iOException) {
                }
            }
        }

        if (string != null) {
            StringTokenizer stringTokenizer = new StringTokenizer(string);

            while (stringTokenizer.hasMoreTokens()) {
                String string1 = stringTokenizer.nextToken();
                if (string1.startsWith("encoding=")) {
                    if (string1.length() > "encoding=".length() + 2) {
                        String string2 = string1.substring("encoding=".length() + 1, string1.length() - 1).trim();
                        if (string2.trim().length() > 0) {
                            return string2.trim();
                        }
                    }
                    break;
                }
            }
        }

        return (String) observableHolder.getValue();
    }

    public boolean isRemovedClass(String string) throws IOException {
        return this.removedClassMappings != null && this.removedClassMappings.containsKey(ZkmUtils.slashesToDots(string));
    }

    public ChangeLogMapping(String string, ScriptEnvironment scriptEnvironment1) {
        this(string, scriptEnvironment1, false);
    }

    public ListMultimap getLineNumberMappings(String string) throws IOException {
        if (this.lineNumberMappings == null) {
            return null;
        }

        String string1 = ZkmUtils.slashesToDots(string);
        return this.lineNumberMappings.getMultimap(string1);
    }

    public ArrayList getNewClassNames() {
        ArrayList arrayList = new ArrayList(this.classCount);
        if (this.reverseClassMappings != null) {
            CollectionSnapshotEnumeration collectionSnapshotEnumeration = new CollectionSnapshotEnumeration(this.reverseClassMappings.keySet());

            while (collectionSnapshotEnumeration.hasMoreElements()) {
                String string = (String) collectionSnapshotEnumeration.nextElement();
                arrayList.add(ZkmUtils.dotsToSlashes(string));
            }
        }

        return arrayList;
    }

    public boolean isSimpleNameChanged(String string) throws IOException {
        String string1 = this.getNewClassName(string);
        if (string1 != null) {
            String string2 = substringAfterLast(string, 47);
            return !substringAfterLast(string1, 47).equals(string2);
        } else {
            return false;
        }
    }

    public void checkFieldTypeConsistency(List list1) {
        if (this.rawFieldMappings != null) {
            boolean bl = true;
            int ba = list1.size();

            for (int i = 0; i < ba; i++) {
                String string = (String) list1.get(i);
                ListMultimap listMultimap = this.rawFieldMappings.getMultimap(string);
                if (listMultimap != null) {
                    EnumerationBackedList enumerationBackedList = new EnumerationBackedList(listMultimap.keys());
                    Collections.sort(enumerationBackedList);

                    for (int j = 0; j < enumerationBackedList.size(); j++) {
                        ChangeLogClassKey changeLogClassKey = (ChangeLogClassKey) enumerationBackedList.get(j);
                        String string1 = changeLogClassKey.getName();
                        if (bl) {
                            bl = false;
                            this.fieldsHaveTypes = changeLogClassKey.getType() != null;
                        } else if (this.fieldsHaveTypes != (changeLogClassKey.getType() != null)) {
                            this.reportError(
                                    "Field \""
                                            + string1
                                            + "\" in class \""
                                            + string
                                            + "\" "
                                            + (this.fieldsHaveTypes ? "has no type." : "has a type")
                                            + " which is inconsistent with the previous field entries. All field entries must have types or no field entries can have types."
                            );
                        }
                    }
                }
            }
        }
    }

    public ChangeLogMapping mergeWith(ChangeLogMapping changeLogMapping2) throws ZkmException, IOException {
        if (changeLogMapping2.packageMappings != null) {
            this.mergePackageMappings(changeLogMapping2);
        }

        if (this.hasErrors()) {
            return this;
        }

        ArrayList arrayList = null;
        if (changeLogMapping2.classMappings != null) {
            arrayList = new ArrayList(changeLogMapping2.classMappings.keySet());
            Collections.sort(arrayList);
            int ba = arrayList.size();

            for (int i = 0; i < ba; i++) {
                String string = (String) arrayList.get(i);
                if (this.classMappings != null && this.classMappings.containsKey(string)) {
                    String string6 = (String) this.classMappings.get(string);
                    String string7 = (String) changeLogMapping2.classMappings.get(string);
                    if (!string6.equals(string7)) {
                        this.logWarning(
                                "Class name \""
                                        + string
                                        + "\" mapping clash while merging with \""
                                        + changeLogMapping2.changeLogName
                                        + "\". Will be mapped to \""
                                        + string6
                                        + "\" and not \""
                                        + string7
                                        + "\""
                        );
                    }
                } else {
                    String string1 = (String) changeLogMapping2.classMappings.get(string);
                    if (this.reverseClassMappings != null && this.reverseClassMappings.containsKey(string1)) {
                        String string2 = (String) this.reverseClassMappings.get(string1);
                        this.reportError(
                                "Class name \""
                                        + string2
                                        + "\"  \""
                                        + string1
                                        + "\" mapping clash while merging with \""
                                        + changeLogMapping2.changeLogName
                                        + "\". Cannot also map \""
                                        + string
                                        + "\" to \""
                                        + string1
                                        + "\""
                        );
                    } else {
                        this.addClassMapping(string1, string);
                    }
                }
            }

            if (this.hasErrors()) {
                return this;
            }

            this.rebuildReverseClassMappings(changeLogMapping2.changeLogName);
        }

        if (this.hasErrors()) {
            return this;
        }

        if (changeLogMapping2.rawFieldMappings != null) {
            this.mergeFieldMappings(changeLogMapping2, arrayList);
        }

        if (this.hasErrors()) {
            return this;
        }

        if (changeLogMapping2.rawMethodMappings != null) {
            this.mergeMethodMappings(changeLogMapping2, arrayList);
        }

        if (this.hasErrors()) {
            return this;
        }

        HashMap hashMap = ZkmUtils.createHashMap();
        TwoKeyMap twoKeyMap = new TwoKeyMap();
        if (changeLogMapping2.traceBackEntries != null) {
            this.mergeTraceBackEntries(changeLogMapping2, hashMap);
        }

        if (changeLogMapping2.forwardClassEntries != null) {
            this.mergeForwardClassEntries(changeLogMapping2, twoKeyMap);
        }

        if (changeLogMapping2.memberClassEntries != null) {
            this.mergeMemberClassEntries(changeLogMapping2, hashMap, twoKeyMap);
        }

        if (this.hasErrors()) {
            return this;
        }

        if (changeLogMapping2.lineNumberMappings != null) {
            this.mergeLineNumberMappings(changeLogMapping2, arrayList);
        }

        if (changeLogMapping2.autoReflectionClass != null) {
            if (this.autoReflectionClass != null && !this.autoReflectionClass.equals(changeLogMapping2.autoReflectionClass)) {
                this.reportError(
                        "Clashing \"AutoReflectionClass:\" values. \""
                                + this.autoReflectionClass
                                + "\" clashes with \""
                                + changeLogMapping2.autoReflectionClass
                                + "\" in '"
                                + changeLogMapping2.getChangeLogName()
                                + "'."
                );
            } else {
                this.autoReflectionClass = changeLogMapping2.autoReflectionClass;
            }
        }

        if (changeLogMapping2.moduleAutoReflectionClasses != null && !changeLogMapping2.moduleAutoReflectionClasses.isEmpty()) {
            if (this.moduleAutoReflectionClasses == null) {
                this.moduleAutoReflectionClasses = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(changeLogMapping2.moduleAutoReflectionClasses.size()));
            }

            Iterator iterator = changeLogMapping2.moduleAutoReflectionClasses.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                String string3 = (String) entry.getKey();
                String string4 = (String) entry.getValue();
                String string5 = (String) this.moduleAutoReflectionClasses.get(string3);
                Map map3;
                if (string5 != null) {
                    if (!string5.equals(string4)) {
                        this.reportError(
                                "Clashing \"AutoReflectionClass:\" values for module '"
                                        + string3
                                        + "'. \""
                                        + string5
                                        + "\" clashes with \""
                                        + string4
                                        + "\" in '"
                                        + changeLogMapping2.getChangeLogName()
                                        + "'."
                        );
                        continue;
                    }

                    map3 = this.moduleAutoReflectionClasses;
                } else {
                    map3 = this.moduleAutoReflectionClasses;
                }

                map3.put(string3, string4);
            }
        }

        if (changeLogMapping2.referenceObfuscationClass != null) {
            if (this.referenceObfuscationClass != null && !this.referenceObfuscationClass.equals(changeLogMapping2.referenceObfuscationClass)) {
                this.reportError(
                        "Clashing \"ObfuscateReferencesClass:\" values. \""
                                + this.referenceObfuscationClass
                                + "\" clashes with \""
                                + changeLogMapping2.referenceObfuscationClass
                                + "\" in '"
                                + changeLogMapping2.getChangeLogName()
                                + "'."
                );
            } else {
                this.referenceObfuscationClass = changeLogMapping2.referenceObfuscationClass;
            }
        }

        if (changeLogMapping2.moduleReferenceObfuscationClasses != null && !changeLogMapping2.moduleReferenceObfuscationClasses.isEmpty()) {
            if (this.moduleReferenceObfuscationClasses == null) {
                this.moduleReferenceObfuscationClasses = ZkmUtils.createHashMap(
                        ZkmUtils.getPrimeCapacity(changeLogMapping2.moduleReferenceObfuscationClasses.size())
                );
            }

            Iterator iterator1 = changeLogMapping2.moduleReferenceObfuscationClasses.entrySet().iterator();

            while (iterator1.hasNext()) {
                Entry entry1 = (Entry) iterator1.next();
                String string8 = (String) entry1.getKey();
                String string10 = (String) entry1.getValue();
                String string11 = (String) this.moduleReferenceObfuscationClasses.get(string8);
                Map map1;
                if (string11 != null) {
                    if (!string11.equals(string10)) {
                        this.reportError(
                                "Clashing \"ObfuscateReferencesClass:\" values for module '"
                                        + string8
                                        + "'. \""
                                        + string11
                                        + "\" clashes with \""
                                        + string10
                                        + "\" in '"
                                        + changeLogMapping2.getChangeLogName()
                                        + "'."
                        );
                        continue;
                    }

                    map1 = this.moduleReferenceObfuscationClasses;
                } else {
                    map1 = this.moduleReferenceObfuscationClasses;
                }

                map1.put(string8, string10);
            }
        }

        if (changeLogMapping2.methodParameterChangeClasses != null) {
            ObjectTriple objectTriple = changeLogMapping2.methodParameterChangeClasses;
            if (this.methodParameterChangeClasses != null && !this.methodParameterChangeClasses.equals(changeLogMapping2.methodParameterChangeClasses)) {
                this.reportError(
                        "Clashing \"MethodParameterChangeClasses:\" values. \"{"
                                + (String) this.methodParameterChangeClasses.getFirst()
                                + ", "
                                + (String) this.methodParameterChangeClasses.getSecond()
                                + ", "
                                + (String) this.methodParameterChangeClasses.getThird()
                                + "}\" clash with \"{"
                                + (String) objectTriple.getFirst()
                                + ", "
                                + (String) objectTriple.getSecond()
                                + ", "
                                + (String) objectTriple.getThird()
                                + "}\" in '"
                                + changeLogMapping2.getChangeLogName()
                                + "'."
                );
            } else if (this.methodParameterChangeClasses == null || !this.methodParameterChangeClasses.equals(objectTriple)) {
                this.methodParameterChangeClasses = new ObjectTriple(objectTriple.getFirst(), objectTriple.getSecond(), objectTriple.getThird());
            }
        }

        if (changeLogMapping2.moduleMethodParameterChangeClasses != null && !changeLogMapping2.moduleMethodParameterChangeClasses.isEmpty()) {
            if (this.moduleMethodParameterChangeClasses == null) {
                this.moduleMethodParameterChangeClasses = ZkmUtils.createHashMap(
                        ZkmUtils.getPrimeCapacity(changeLogMapping2.moduleMethodParameterChangeClasses.size())
                );
            }

            Iterator iterator2 = changeLogMapping2.moduleMethodParameterChangeClasses.entrySet().iterator();

            while (iterator2.hasNext()) {
                Entry entry2 = (Entry) iterator2.next();
                String string9 = (String) entry2.getKey();
                ObjectTriple objectTriple1 = (ObjectTriple) entry2.getValue();
                ObjectTriple objectTriple2 = (ObjectTriple) this.moduleMethodParameterChangeClasses.get(string9);
                if (objectTriple2 != null && !objectTriple2.equals(objectTriple1)) {
                    this.reportError(
                            "Clashing \"MethodParameterChangeClasses:\" values for module '"
                                    + string9
                                    + "'. \"{"
                                    + (String) objectTriple2.getFirst()
                                    + ", "
                                    + (String) objectTriple2.getSecond()
                                    + ", "
                                    + (String) objectTriple2.getThird()
                                    + "}\" clash with \"{"
                                    + (String) objectTriple1.getFirst()
                                    + ", "
                                    + (String) objectTriple1.getSecond()
                                    + ", "
                                    + (String) objectTriple1.getThird()
                                    + "}\" in '"
                                    + changeLogMapping2.getChangeLogName()
                                    + "'."
                    );
                } else {
                    Map map2;
                    if (objectTriple2 != null) {
                        if (objectTriple2.equals(objectTriple1)) {
                            continue;
                        }

                        map2 = this.moduleMethodParameterChangeClasses;
                    } else {
                        map2 = this.moduleMethodParameterChangeClasses;
                    }

                    map2.put(string9, new ObjectTriple(objectTriple1.getFirst(), objectTriple1.getSecond(), objectTriple1.getThird()));
                }
            }
        }

        this.changeLogName = this.changeLogName + "<-" + changeLogMapping2.changeLogName;
        return this;
    }

    public String toOriginalTypeName(String string) throws IOException {
        if (this.reverseClassMappings == null) {
            return string;
        }

        int ba = string.indexOf(91);
        String string1;
        String string2;
        Map map1;
        if (ba > -1) {
            string1 = string.substring(0, ba);
            string2 = string.substring(ba);
            map1 = this.reverseClassMappings;
        } else {
            string1 = string;
            string2 = "";
            map1 = this.reverseClassMappings;
        }

        String string3 = (String) map1.get(ZkmUtils.slashesToDots(string1));
        return string3 == null ? string : string3 + string2;
    }

    public String lookupNewMethodName(String string, MethodSignature methodSignature1) throws IOException {
        if (this.methodMappings == null) {
            return null;
        }

        MethodSignature methodSignature2 = (MethodSignature) this.methodMappings.getValue(ZkmUtils.slashesToDots(string), methodSignature1);
        if (methodSignature2 == null) {
            return null;
        }

        String string1 = methodSignature2.getName();
        return string1.equals(methodSignature1.getName()) ? null : string1;
    }

    public void collectParameterObfuscatedMethods(int ba, ClassResolver classResolver1, int bb) throws ZkmException, IOException {
        ChangeLogSimpleNode.getOpaqueStrings();
        this.parameterObfuscatedMethods = ZkmUtils.createHashSet();
        if (this.hasParameterObfuscation()) {
            this.parameterObfuscatedMethods = ZkmUtils.createHashSet();
            Iterator iterator = this.rawMethodMappings.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                String string = (String) entry.getKey();
                String string1 = ZkmUtils.dotsToSlashes(string);
                ClassFileBase classFileBase = classResolver1.getClassFile(string1);
                Iterator iterator1 = ((ListMultimap) entry.getValue()).entrySet().iterator();

                while (true) {
                    if (iterator1.hasNext()) {
                        Entry entry1 = (Entry) iterator1.next();
                        ChangeLogMemberKey changeLogMemberKey = (ChangeLogMemberKey) entry1.getKey();
                        String string2 = MethodSignature.javaTypeToDescriptor(changeLogMemberKey.getType());
                        String string3 = changeLogMemberKey.getName();
                        MethodSignature methodSignature1 = new MethodSignature(
                                string3, MethodSignature.parseParameterList(changeLogMemberKey.getParameterTypes()), string2
                        );
                        List list1 = (List) entry1.getValue();
                        if (ba >= 0) {
                            NewNameMapping newNameMapping = (NewNameMapping) list1.get(0);
                            AbstractMethodInfo abstractMethodInfo = classFileBase.findMethod(methodSignature1);
                            if (bb <= 0 || abstractMethodInfo != null) {
                                boolean parametersObfuscated = newNameMapping.isParametersObfuscated();
                                if (bb > 0) {
                                    if (!parametersObfuscated) {
                                        continue;
                                    }

                                    parametersObfuscated = string3.equals(newNameMapping.getNewName());
                                }

                                if (!parametersObfuscated) {
                                    this.parameterObfuscatedMethods.add(abstractMethodInfo);
                                } else if (newNameMapping.isParametersObfuscated()) {
                                    this.reportFatalError(
                                            "Method '"
                                                    + abstractMethodInfo.toDisplayString()
                                                    + "' in class '"
                                                    + string
                                                    + "' is specified as having its parameters obfuscated but not having its name changed. This not supported. Has the change log been edited?"
                                    );
                                }
                            }
                            continue;
                        }
                    }

                    if (ba > 0) {
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void setReferenceObfuscationClass(Object object) {
        this.referenceObfuscationClass = (String) object;
    }

    public Map getMemberClassEntries() {
        return ZkmUtils.copyToHashMap(this.memberClassEntries);
    }

    public SetMultiMap getRenamedMethodSignatures() {
        SetMultiMap setMultiMap = new SetMultiMap(this.methodMappings.getKeyCount());
        Iterator iterator = this.methodMappings.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            String string = (String) entry.getKey();
            Iterator iterator1 = ((Map) entry.getValue()).entrySet().iterator();

            while (iterator1.hasNext()) {
                Entry entry1 = (Entry) iterator1.next();
                MethodSignature methodSignature1 = (MethodSignature) entry1.getKey();
                MethodSignature methodSignature2 = (MethodSignature) entry1.getValue();
                if (!methodSignature1.getName().equals(methodSignature2.getName())) {
                    setMultiMap.addValue(ZkmUtils.dotsToSlashes(string), methodSignature2);
                }
            }
        }

        return setMultiMap;
    }

    @Override
    public void incrementPackageCount() {
        this.packageCount++;
    }

    public void checkClassPackageNameClashes() {
        if (this.classMappings != null && this.packageMappings != null && this.scriptEnvironment.isLooseClassFilesLoaded()) {
            Iterator iterator = this.classMappings.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                String string = (String) entry.getKey();
                String string1 = (String) entry.getValue();
                if (this.reversePackageMappings.containsKey(string1)) {
                    Map map1 = this.reversePackageMappings.getInnerMap(string1);
                    ArrayList arrayList = new ArrayList(map1.keySet());
                    Collections.sort(arrayList);
                    this.reportError(
                            "Class \""
                                    + string
                                    + "\" and package \""
                                    + (String) arrayList.get(0)
                                    + "\" are both remapped to \""
                                    + string1
                                    + "\". This could cause problems because some classes have been opened from the file system."
                    );
                }
            }

            if (this.autoReflectionClass != null && this.reverseClassMappings.containsKey(this.autoReflectionClass)) {
                this.reportError(
                        "Class \""
                                + this.autoReflectionClass
                                + "\" appears as the AutoReflection lookup class but it also appears as a new class name for the class '"
                                + (String) this.reverseClassMappings.get(this.autoReflectionClass)
                                + "'."
                );
            }

            if (this.referenceObfuscationClass != null && this.reverseClassMappings.containsKey(this.referenceObfuscationClass)) {
                this.reportError(
                        "Class \""
                                + this.referenceObfuscationClass
                                + "\" appears as the "
                                + "Reference Obfuscation"
                                + " lookup class but it also appears as a new class name for the class '"
                                + (String) this.reverseClassMappings.get(this.referenceObfuscationClass)
                                + "'."
                );
            }
        }
    }

    public void mergeLineNumberMappings(ChangeLogMapping changeLogMapping2, List list1) throws IOException {
        Iterator iterator = list1.iterator();

        while (iterator.hasNext()) {
            String string = (String) iterator.next();
            if (this.getLineNumberMappings(string) == null) {
                ListMultimap listMultimap = changeLogMapping2.getLineNumberMappings(string);
                if (listMultimap != null) {
                    if (this.lineNumberMappings == null) {
                        this.lineNumberMappings = new NestedMultiMap(changeLogMapping2.classCount);
                    }

                    this.lineNumberMappings.putMultimap(string, new ListMultimap(listMultimap));
                }
            }
        }
    }

    @Override
    public void addModuleReferenceObfuscationClass(Object object, Object object1) {
        String string = (String) this.moduleReferenceObfuscationClasses.put(object1, object);
    }

    public void putResolvedFieldMapping(String string, String string1, FieldSignature fieldSignature, FieldSignature fieldSignature1, HashMap hashMap) {
        TwoKeyMap twoKeyMap;
        if ((FieldSignature) this.fieldMappings.putValue(string1, fieldSignature, fieldSignature1) != null) {
            this.reportError("Field \"" + fieldSignature.formatDeclaration(hashMap) + "\" appears more than once in class \"" + string + "\"");
            twoKeyMap = this.reverseFieldMappings;
        } else {
            twoKeyMap = this.reverseFieldMappings;
        }

        FieldSignature fieldSignature2 = (FieldSignature) twoKeyMap.putValue(string1, fieldSignature1, fieldSignature);
        if (fieldSignature2 != null) {
            this.reportError(
                    "Fields \""
                            + fieldSignature2.formatDeclaration(hashMap)
                            + "\" and \""
                            + fieldSignature.formatDeclaration(hashMap)
                            + "\" are both mapped to \""
                            + fieldSignature1.formatDeclaration(hashMap)
                            + "\" in class \""
                            + string
                            + "\""
            );
        }
    }

    public String describeNewMethod(String string, String string1, String[] strings, String string2) throws IOException {
        if (this.rawMethodMappings == null) {
            return null;
        }

        String string3 = ZkmUtils.slashesToDots(string);
        String string4 = this.joinTypeNames(strings);
        String string6 = string2;
        String string7 = string4;
        String string8 = string1;
        ChangeLogMemberKey changeLogMemberKey = this.createMethodKey(string8, string7, string6);
        List list1 = this.rawMethodMappings.getValues(string3, changeLogMemberKey);
        if (list1 != null && list1.size() > 0) {
            if (list1.size() != 1) {
                this.reportError("Class '" + string3 + "' has multiple methods '" + string1 + "(" + string4 + ")'.");
                return null;
            }

            NewNameMapping newNameMapping = (NewNameMapping) list1.get(0);
            StringBuilder stringBuilder = new StringBuilder();

            for (int i = 0; i < strings.length; i++) {
                stringBuilder.append(strings[i]);
                if (i < strings.length - 1) {
                    stringBuilder.append(", ");
                }
            }

            String string9 = newNameMapping.getEffectiveParameterTypes();
            if (!string1.equals(newNameMapping.getNewName())
                    || newNameMapping.isSignatureChanged()
                    || string9 != null && !string9.equals(stringBuilder.toString())) {
                StringBuilder stringBuilder1 = new StringBuilder();
                String string5 = changeLogMemberKey.getType();
                if (string5 != null && string5.length() > 0) {
                    stringBuilder1.append(this.toOriginalTypeName(string5));
                    stringBuilder1.append(' ');
                }

                stringBuilder1.append(newNameMapping.getNewName());
                stringBuilder1.append('(');
                stringBuilder1.append(string9);
                stringBuilder1.append(')');
                return stringBuilder1.toString();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public String getOriginalMethodName(String string, String string1, String[] strings, String string2) throws IOException {
        if (this.reverseMethodMappings == null) {
            this.buildReverseMethodMappings();
        }

        if (this.reverseMethodMappings != null) {
            String string3 = ZkmUtils.slashesToDots(string);
            ComparableKeyValue comparableKeyValue = this.createNameTypeKey(string1, string2);
            String string4 = this.joinTypeNames(strings);
            List list1 = this.reverseMethodMappings.getValues(string3, comparableKeyValue, string4);
            if (list1 != null && list1.size() > 0) {
                if (list1.size() == 1) {
                    String string5 = (String) list1.get(0);
                    return !string5.equals(string1) ? string5 : null;
                } else {
                    this.reportError("Class '" + string3 + "' has multiple methods '" + string1 + "(" + string4 + ")'.");
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void removeRawFieldMapping(String string, String string1, String string2) throws IOException {
        String string3 = ZkmUtils.slashesToDots(string);
        ChangeLogClassKey changeLogClassKey = this.createFieldKey(string1, string2);
        this.rawFieldMappings.removeInnerKey(string3, changeLogClassKey);
    }

    public void removeClassMapping(String string) throws IOException {
        String string1 = string;
        if (string1 != null) {
            string1 = ZkmUtils.slashesToDots(string1);
        }

        if (this.classMappings != null) {
            String string2 = (String) this.classMappings.get(string1);
            String string3 = (String) this.classMappings.remove(string1);
            Map map1;
            if (this.removedClassMappings == null) {
                this.removedClassMappings = ZkmUtils.createHashMap();
                map1 = this.removedClassMappings;
            } else {
                map1 = this.removedClassMappings;
            }

            map1.put(string1, string2);
            this.reverseClassMappings.remove(string2);
            if (this.rawFieldMappings != null) {
                ListMultimap listMultimap = this.rawFieldMappings.removeMultimap(string1);
                if (listMultimap != null) {
                    if (this.removedClassFieldMappings == null) {
                        this.removedClassFieldMappings = new NestedMultiMap();
                    }

                    this.removedClassFieldMappings.putMultimap(string1, listMultimap);
                }
            }

            if (this.rawMethodMappings != null) {
                this.rawMethodMappings.removeMultimap(string1);
            }

            if (this.memberClassEntries != null) {
                this.memberClassEntries.remove(string1);
            }

            if (this.sourceNameMappings != null) {
                this.sourceNameMappings.remove(string2);
            }

            if (this.lineNumberMappings != null) {
                this.lineNumberMappings.removeMultimap(string2);
            }

            this.logWarning("Class '" + string1 + "' no longer appears in the opened classes and will be assumed to be no longer used.");
        }
    }

    public Map getClassMappings() {
        return this.classMappings != null ? ZkmUtils.copyToHashMap(this.classMappings) : null;
    }

    public void putRawFieldMapping(Object object, ChangeLogClassKey changeLogClassKey, Object object1) {
        if (this.rawFieldMappings == null) {
            this.rawFieldMappings = new NestedMultiMap(this.classCount, 5, 5);
        }

        this.rawFieldMappings.addValue(object, changeLogClassKey, object1);
        if (changeLogClassKey.getType() != null) {
            this.fieldsHaveTypes = true;
        } else {
            this.fieldsHaveTypes = false;
        }
    }

    public void removeAddedParameters(Object object) {
        if (this.addedParametersByMethod != null) {
            AddedParameter[] addedParameters1 = (AddedParameter[]) this.addedParametersByMethod.remove(object);
        }
    }

    public Enumeration getFieldMappedClassNames() {
        if (this.fieldMappings == null) {
            return null;
        }

        ArrayList arrayList = new ArrayList(this.fieldMappings.getKeyCount());
        Enumeration enumeration = this.fieldMappings.keys();

        while (enumeration.hasMoreElements()) {
            String string = (String) enumeration.nextElement();
            arrayList.add(ZkmUtils.dotsToSlashes(string));
        }

        return Collections.enumeration(arrayList);
    }

    public Map getParameterChangeKeysByMethod() {
        HashMap hashMap = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(this.parameterChangeKeysByMethod.size()));
        Iterator iterator = this.parameterChangeKeysByMethod.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            hashMap.put((MethodInfo) entry.getKey(), entry.getValue());
        }

        return hashMap;
    }

    public ListMultimap copyRawMethodMappings(String string) {
        if (this.rawMethodMappings != null) {
            ListMultimap listMultimap = this.rawMethodMappings.getMultimap(string);
            return listMultimap == null ? null : new ListMultimap(listMultimap);
        } else {
            return null;
        }
    }

    public String getSourceName(String string) throws IOException {
        if (this.sourceNameMappings != null) {
            String string1 = ZkmUtils.slashesToDots(string);
            return (String) this.sourceNameMappings.get(string1);
        } else {
            return null;
        }
    }

    public List listMethodEntries(String string) {
        ArrayList arrayList = new ArrayList();
        if (this.rawMethodMappings != null) {
            ListMultimap listMultimap = this.rawMethodMappings.getMultimap(string);
            if (listMultimap != null) {
                StringBuilder stringBuilder = new StringBuilder();
                Enumeration enumeration = listMultimap.keys();

                while (enumeration.hasMoreElements()) {
                    ChangeLogMemberKey changeLogMemberKey = (ChangeLogMemberKey) enumeration.nextElement();
                    stringBuilder.setLength(0);
                    stringBuilder.append(changeLogMemberKey.getType());
                    stringBuilder.append(" ");
                    stringBuilder.append(changeLogMemberKey.getName());
                    stringBuilder.append('(');
                    stringBuilder.append(changeLogMemberKey.getParameterTypes());
                    stringBuilder.append(')');
                    arrayList.add(stringBuilder.toString());
                }
            }
        }

        return arrayList;
    }

    public ChangeLogMemberKey createMethodKey(String string, String string1, String string2) {
        String string6 = this.methodsHaveReturnTypes ? string2 : null;
        byte ba = 0;
        String string3 = string6;
        String string4 = string1;
        String string5 = string;
        return new ChangeLogMemberKey(string5, string4, string3, ba);
    }

    public void removeFieldMapping(String string, FieldSignature fieldSignature) throws IOException {
        String string1 = string;
        string1 = ZkmUtils.slashesToDots(string1);
        if (this.fieldMappings != null) {
            FieldSignature fieldSignature1 = (FieldSignature) this.fieldMappings.removeValue(string1, fieldSignature);
            FieldSignature fieldSignature2 = (FieldSignature) this.reverseFieldMappings.removeValue(string1, fieldSignature1);
        }
    }

    public ChangeLogMemberKey createMethodKeyForDescriptor(String string, String string1, String string2) {
        String string3;
        boolean bl;
        if (ConstantPoolEntry.isFieldDescriptor(string2)) {
            string3 = ConstantPoolEntry.descriptorToJavaType(string2);
            bl = this.methodsHaveReturnTypes;
        } else {
            string3 = string2;
            bl = this.methodsHaveReturnTypes;
        }

        ChangeLogMemberKey changeLogMemberKey;
        if (bl) {
            changeLogMemberKey = new ChangeLogMemberKey(string, string1, string3, 0);
        } else {
            changeLogMemberKey = new ChangeLogMemberKey(string, string1, null, 0);
        }

        return changeLogMemberKey;
    }

    public AddedParameter[] getAddedParameters(Object object) {
        return this.addedParametersByMethod == null ? null : (AddedParameter[]) this.addedParametersByMethod.get(object);
    }

    public void buildReverseMethodMappings() throws IOException {
        this.reverseMethodMappings = new ThreeKeyMultiMap();
        if (this.rawMethodMappings != null) {
            Enumeration enumeration = this.rawMethodMappings.keys();

            while (enumeration.hasMoreElements()) {
                String string = (String) enumeration.nextElement();
                String string1 = this.getNewClassName(string);
                NestedMultiMap nestedMultiMap;
                if (string1 == null) {
                    string1 = string;
                    nestedMultiMap = this.rawMethodMappings;
                } else {
                    string1 = ZkmUtils.slashesToDots(string1);
                    nestedMultiMap = this.rawMethodMappings;
                }

                ListMultimap listMultimap = nestedMultiMap.getMultimap(string);
                Enumeration enumeration1 = listMultimap.keys();

                while (enumeration1.hasMoreElements()) {
                    ChangeLogMemberKey changeLogMemberKey = (ChangeLogMemberKey) enumeration1.nextElement();
                    String string2 = changeLogMemberKey.getParameterTypes();
                    String string3 = this.mapTypeList(string2, this.classMappings);
                    String string4 = changeLogMemberKey.getType();
                    String string5 = null;
                    if (string4 != null) {
                        string5 = AbstractChangeLog.mapTypeName(string4, this.classMappings);
                    }

                    Iterator iterator = listMultimap.getValues(changeLogMemberKey).iterator();

                    while (iterator.hasNext()) {
                        NewNameMapping newNameMapping = (NewNameMapping) iterator.next();
                        ComparableKeyValue comparableKeyValue = new ComparableKeyValue(newNameMapping.getNewName(), string5);
                        String string6;
                        ThreeKeyMultiMap threeKeyMultiMap;
                        if (newNameMapping.hasParameterChangeData()) {
                            string6 = newNameMapping.getEffectiveParameterTypes();
                            threeKeyMultiMap = this.reverseMethodMappings;
                        } else {
                            string6 = string3;
                            threeKeyMultiMap = this.reverseMethodMappings;
                        }

                        threeKeyMultiMap.addValue(string1, comparableKeyValue, string6, changeLogMemberKey.getName());
                    }
                }
            }
        }
    }

    public List getLibraryMethodAddedParameters() {
        ArrayList arrayList = new ArrayList();
        if (this.addedParametersByMethod != null) {
            Iterator iterator = this.addedParametersByMethod.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                AbstractMethodInfo abstractMethodInfo = (AbstractMethodInfo) entry.getKey();
                if (!abstractMethodInfo.isProgramMember()) {
                    arrayList.add(new ObjectPair(abstractMethodInfo, entry.getValue()));
                }
            }
        }

        return arrayList;
    }

    public String getChangeLogName() {
        return this.changeLogName;
    }

    public ListMultimap copyRawFieldMappings(String string) {
        if (this.rawFieldMappings != null) {
            ListMultimap listMultimap = this.rawFieldMappings.getMultimap(string);
            return listMultimap == null ? null : new ListMultimap(listMultimap);
        } else {
            return null;
        }
    }

    public ComparableKeyValue createNameTypeKey(String string, String string1) {
        String string2 = this.methodsHaveReturnTypes ? string1 : null;
        String string3 = string;
        return new ComparableKeyValue(string3, string2);
    }

    public String getResolvedNewFieldName(String string, FieldSignature fieldSignature) throws IOException {
        if (this.fieldMappings == null) {
            return null;
        }

        FieldSignature fieldSignature1 = (FieldSignature) this.fieldMappings.getValue(ZkmUtils.slashesToDots(string), fieldSignature);
        return fieldSignature1 != null && !fieldSignature1.equals(fieldSignature) ? fieldSignature1.getName() : null;
    }

    public void applyLooseFieldExclusions(NameExclusionSet nameExclusionSet, HashMap hashMap, ClassMemberLookup classMemberLookup1) throws IOException {
        TwoKeyMap twoKeyMap = new TwoKeyMap(this.fieldMappings.getKeyCount());
        TwoKeyMap twoKeyMap1 = new TwoKeyMap(this.reverseFieldMappings.getKeyCount());
        Enumeration enumeration = this.fieldMappings.keys();

        while (enumeration.hasMoreElements()) {
            String string = (String) enumeration.nextElement();
            Iterator iterator = this.fieldMappings.getInnerMap(string).entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                FieldSignature fieldSignature = (FieldSignature) entry.getKey();
                FieldSignature fieldSignature1 = (FieldSignature) entry.getValue();
                FieldInfo fieldInfo = classMemberLookup1.findField(ZkmUtils.dotsToSlashes(string), fieldSignature.getName(), fieldSignature.getDescriptor());
                String string1 = (String) ZkmUtils.mapOrSelf(ZkmUtils.dotsToSlashes(string), hashMap);
                string1 = ZkmUtils.slashesToDots(string1);
                if (nameExclusionSet.isFieldExcluded(fieldInfo)) {
                    if (!fieldSignature.getName().equals(fieldSignature1.getName())) {
                        FieldSignature fieldSignature2 = (FieldSignature) fieldSignature.clone();
                        this.logWarning(
                                "Field '"
                                        + fieldSignature.getName()
                                        + "' in class '"
                                        + string1
                                        + "' could not be renamed to '"
                                        + fieldSignature1.getName()
                                        + "' because it has been excluded and the input change log is specified as loose. It will not be renamed."
                        );
                        twoKeyMap.putValue(string, fieldSignature, fieldSignature2);
                        FieldSignature fieldSignature3 = (FieldSignature) twoKeyMap1.putValue(string, fieldSignature2, fieldSignature);
                        if (fieldSignature3 != null) {
                            this.reportFatalError(
                                    "Field name clash in class '"
                                            + string1
                                            + "' after field exclusions.  Both '"
                                            + fieldSignature.getName()
                                            + "' and '"
                                            + fieldSignature3.getName()
                                            + "' are renamed to '"
                                            + fieldSignature2.getName()
                                            + "'. (1)"
                            );
                        }
                    } else {
                        twoKeyMap.putValue(string, fieldSignature, fieldSignature1);
                        FieldSignature fieldSignature4 = (FieldSignature) twoKeyMap1.putValue(string, fieldSignature1, fieldSignature);
                        if (fieldSignature4 != null) {
                            this.reportFatalError(
                                    "Field name clash in class '"
                                            + string1
                                            + "' after field exclusions.  Both '"
                                            + fieldSignature.getName()
                                            + "' and '"
                                            + fieldSignature4.getName()
                                            + "' are renamed to '"
                                            + fieldSignature1.getName()
                                            + "'. (2)"
                            );
                        }
                    }
                } else {
                    twoKeyMap.putValue(string, fieldSignature, fieldSignature1);
                    FieldSignature fieldSignature5 = (FieldSignature) twoKeyMap1.putValue(string, fieldSignature1, fieldSignature);
                    if (fieldSignature5 != null) {
                        this.reportFatalError(
                                "Field name clash in class '"
                                        + string1
                                        + "' after field exclusions.  Both '"
                                        + fieldSignature.getName()
                                        + "' and '"
                                        + fieldSignature5.getName()
                                        + "' are renamed to '"
                                        + fieldSignature1.getName()
                                        + "'. (3)"
                        );
                    }
                }
            }
        }

        this.fieldMappings = twoKeyMap;
        this.reverseFieldMappings = twoKeyMap1;
    }

    @Override
    public void addMainFlowObfuscationData(Object object, Object object1) throws ZkmException, IOException {
        List list1 = (List) object1;
        String string = (String) object;
        if (!this.errorsFound) {
            this.addFlowObfuscationEntry(string, list1, true, "TraceBackClass:", (String) null);
        }
    }

    public Map getParameterChangeKeysByClass() {
        return ZkmUtils.copyToHashMap(this.parameterChangeKeysByClass);
    }

    public ArrayList getRenamedPackageNames() {
        ArrayList arrayList = new ArrayList(this.packageCount);
        HashSet hashSet = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(this.packageCount));
        if (this.reversePackageMappings != null) {
            Enumeration enumeration = this.reversePackageMappings.keys();

            while (enumeration.hasMoreElements()) {
                String string = (String) enumeration.nextElement();
                Iterator iterator = this.reversePackageMappings.getInnerMap(string).keySet().iterator();

                while (iterator.hasNext()) {
                    String string1 = (String) iterator.next();
                    if (!string.equals(string1) && hashSet.add(string)) {
                        arrayList.add(ZkmUtils.dotsToSlashes(string));
                    }
                }
            }
        }

        return arrayList;
    }

    public void rebuildReverseClassMappings(String string) {
        this.reverseClassMappings.clear();
        Iterator iterator = this.classMappings.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            String string1 = (String) entry.getKey();
            String string2 = getPackageName(string1);
            String string3 = (String) entry.getValue();
            String string4 = getSimpleName(string3);
            if (string2.length() == 0) {
                String string5 = ((java.lang.String) (this.reverseClassMappings.put(string3, string1)));
                if (string5 != null) {
                    this.reportError(
                            "Mapping clash while merging with \""
                                    + string
                                    + "\". Class name \""
                                    + string5
                                    + "\"  \""
                                    + string3
                                    + "\". Cannot also map \""
                                    + string1
                                    + "\" to \""
                                    + string3
                                    + "\" (1)"
                    );
                    return;
                }
            } else {
                String string8 = (String) this.packageMappings.get(string2);
                String string6;
                if (string8.length() > 0) {
                    string6 = string8 + "." + string4;
                    entry.setValue(string6);
                } else {
                    string6 = string4;
                    entry.setValue(string6);
                }

                String string7 = ((java.lang.String) (this.reverseClassMappings.put(string6, string1)));
                if (string7 != null) {
                    this.reportError(
                            "Mapping clash while merging with \""
                                    + string
                                    + "\". Class name \""
                                    + string7
                                    + "\" already mapped to \""
                                    + string6
                                    + "\". Cannot also map \""
                                    + string1
                                    + "\" to \""
                                    + string6
                                    + "\" (2)"
                    );
                    return;
                }
            }
        }
    }

    public void checkMemberTypeConsistency() {
        if (this.classMappings != null) {
            ArrayList arrayList = new ArrayList(this.classMappings.keySet());
            Collections.sort(arrayList);
            this.checkFieldTypeConsistency(arrayList);
            this.checkMethodReturnTypeConsistency(arrayList);
        }
    }

    public ArrayList getFieldMappingPairs(String string) throws IOException {
        String string1 = string;
        string1 = ZkmUtils.slashesToDots(string1);
        if (this.fieldMappings != null) {
            Map map1 = this.fieldMappings.getInnerMap(string1);
            if (map1 != null) {
                ArrayList arrayList = new ArrayList();
                Iterator iterator = map1.entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry entry = (Entry) iterator.next();
                    arrayList.add(new ObjectPair(entry.getKey(), entry.getValue()));
                }

                return arrayList;
            }
        }

        return null;
    }

    public void derivePackageMappingsFromClasses() {
        if (this.classMappings != null) {
            HashMap hashMap = ZkmUtils.createHashMap();
            Iterator iterator = this.classMappings.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                String string = (String) entry.getKey();
                String string1 = getPackageName(string);
                String string2 = (String) entry.getValue();
                String string3 = getPackageName(string2);
                String string4 = null;
                if (this.packageMappings != null) {
                    string4 = (String) this.packageMappings.get(string1);
                }

                if (string1.length() > 0) {
                    if (string1.equals(string3)) {
                        if (string4 != null) {
                            if (!string3.equals(string4)) {
                                this.reportError(
                                        "Class \""
                                                + string
                                                + "\" is remapped to \""
                                                + string2
                                                + "\" but package '"
                                                + string1
                                                + "\" should be remapped to \""
                                                + string4
                                                + "\" (1)"
                                );
                            }
                        } else {
                            String string5 = ((java.lang.String) (hashMap.put(string1, string3)));
                            if (string5 != null && !string3.equals(string5)) {
                                this.reportError(
                                        "Class \""
                                                + string
                                                + "\" is remapped to \""
                                                + string2
                                                + "\" but the package remapping is inconsistent with that of another class : \""
                                                + string3
                                                + " != "
                                                + string5
                                                + "\"."
                                );
                            }
                        }
                    } else if (string4 != null) {
                        if (!string3.equals(string4)) {
                            this.reportError(
                                    "Class \""
                                            + string
                                            + "\" is remapped to \""
                                            + string2
                                            + "\" but package '"
                                            + string1
                                            + "\" should be remapped to \""
                                            + string4
                                            + "\" (2)"
                            );
                        }
                    } else {
                        this.reportError("Class \"" + string + "\" is remapped to \"" + string2 + "\" but there is no corresponding packge level remapping.");
                    }
                } else if (string3.length() > 0) {
                    this.reportError("Class \"" + string + "\" is remapped to \"" + string2 + "\" but current version cannot handle remapping of default package.");
                }
            }

            Entry[] entrys = new Entry[hashMap.size()];
            hashMap.entrySet().toArray(entrys);
            StringKeyEntryComparator stringKeyEntryComparator = new StringKeyEntryComparator(this);
            Arrays.sort(entrys, stringKeyEntryComparator);

            for (int i = 0; i < entrys.length; i++) {
                this.putPackageMapping((String) entrys[i].getValue(), (String) entrys[i].getKey(), false);
            }
        }
    }

    public String lookupOriginalFieldName(String string, String string1, String string2, NestedMultiMap nestedMultiMap) throws IOException {
        if (nestedMultiMap != null) {
            String string3 = this.getOriginalClassName(string);
            if (string3 == null) {
                string3 = string;
            } else {
                string3 = ZkmUtils.slashesToDots(string3);
            }

            String string4 = this.toOriginalTypeName(string2);
            if (string4 == null) {
                string4 = string2;
            } else {
                string4 = ZkmUtils.slashesToDots(string4);
            }

            ListMultimap listMultimap = nestedMultiMap.getMultimap(string3);
            if (listMultimap == null) {
                return string1;
            }

            Iterator iterator = listMultimap.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                ChangeLogClassKey changeLogClassKey = (ChangeLogClassKey) entry.getKey();
                if (changeLogClassKey.getType().equals(string4) && ((List) entry.getValue()).contains(string1)) {
                    return changeLogClassKey.getName();
                }
            }

            return string1;
        } else {
            return string1;
        }
    }

    public boolean containsFieldMapping(String string, String string1, String string2) throws IOException {
        if (this.rawFieldMappings != null) {
            String string3 = ZkmUtils.slashesToDots(string);
            ChangeLogClassKey changeLogClassKey = this.createFieldKey(string1, string2);
            return this.rawFieldMappings.containsInnerKey(string3, changeLogClassKey);
        } else {
            return false;
        }
    }

    public String getNewFieldName(String string, String string1, String string2) throws IOException {
        return this.lookupNewFieldName(string, string1, string2, this.rawFieldMappings);
    }

    public ArrayList getSortedPackageNames() {
        ArrayList arrayList = new ArrayList(this.packageCount);
        if (this.packageMappings != null) {
            Iterator iterator = this.packageMappings.keySet().iterator();

            while (iterator.hasNext()) {
                String string = (String) iterator.next();
                arrayList.add(ZkmUtils.dotsToSlashes(string));
            }
        }

        Collections.sort(arrayList);
        return arrayList;
    }

    public boolean hasFieldMapping(String string, FieldSignature fieldSignature) throws IOException {
        return this.fieldMappings != null && this.fieldMappings.containsKeys(ZkmUtils.slashesToDots(string), fieldSignature);
    }

    public static String describeSourceFile(String string, ScriptEnvironment scriptEnvironment1) {
        File file1 = new File(string);
        if (TempFileManager.isZkmTempFile(file1)) {
            String string1 = TempFileManager.getOriginalName(file1.getAbsolutePath());
            if (string1 != null) {
                return " : Original file is \"" + new File(string1).getAbsolutePath() + "\"";
            }

            File file2 = scriptEnvironment1.getDefaultDirectory();
            File file3 = new File(file2, "ZKM_saved_ChangeLog.txt");
            if (file3.exists()) {
                file3.delete();
            }

            try {
                ZkmFileUtils.copyFile(string, file3.getAbsolutePath());
                return " : Contents saved to \"" + file3.getAbsolutePath() + "\"";
            } catch (IOException iOException) {
                return "";
            }
        } else {
            return "";
        }
    }

    public void renameMappedClass(String string, String string1) throws IOException {
        String string2 = ZkmUtils.slashesToDots(string);
        String string3 = ZkmUtils.slashesToDots(string1);
        String string4;
        if (this.hasClassMapping(string2)) {
            string4 = (String) this.classMappings.get(string2);
            this.removeClassMapping(string2);
        } else {
            string4 = (String) this.removedClassMappings.get(string2);
        }

        this.addClassMapping(string4, string3);
    }

    public void mergeFieldMappings(ChangeLogMapping changeLogMapping2, List list1) {
        if (this.rawFieldMappings != null && this.fieldsHaveTypes != changeLogMapping2.fieldsHaveTypes) {
            this.reportError(
                    "Change log fields "
                            + (this.fieldsHaveTypes ? "have types" : "have no types")
                            + " which is inconsistent with change log \""
                            + changeLogMapping2.changeLogName
                            + "\" whose fields "
                            + (changeLogMapping2.fieldsHaveTypes ? "have types" : "have no types")
                            + ". All field entries in change logs being merged must have types or no field entries can have types."
            );
        } else {
            int ba = list1.size();

            for (int i = 0; i < ba; i++) {
                String string = (String) list1.get(i);
                ListMultimap listMultimap = changeLogMapping2.rawFieldMappings.getMultimap(string);
                if (listMultimap != null) {
                    EnumerationBackedList enumerationBackedList = new EnumerationBackedList(listMultimap.keys());
                    Collections.sort(enumerationBackedList);
                    int bc = enumerationBackedList.size();

                    for (int j = 0; j < bc; j++) {
                        ChangeLogClassKey changeLogClassKey = (ChangeLogClassKey) enumerationBackedList.get(j);
                        String string1 = changeLogClassKey.getName();
                        String string2 = changeLogClassKey.getType();
                        List list2 = listMultimap.getValues(changeLogClassKey);
                        if (list2.size() > 1) {
                            if (changeLogMapping2.fieldsHaveTypes) {
                                this.reportError(
                                        "More than one field \""
                                                + string2
                                                + " "
                                                + string1
                                                + "\" appears in class \""
                                                + string
                                                + "\" in log \""
                                                + changeLogMapping2.changeLogName
                                                + "\"."
                                );
                            } else {
                                this.reportError(
                                        "More than one field with the name \""
                                                + string1
                                                + "\" appears in class \""
                                                + string
                                                + "\" in log \""
                                                + changeLogMapping2.changeLogName
                                                + "\" but there are no field types to allow the mappings to be distinguished."
                                );
                            }
                        } else {
                            String string3 = (String) list2.get(0);
                            if (this.rawFieldMappings != null) {
                                List list3 = this.rawFieldMappings.getValues(string, changeLogClassKey);
                                if (list3 != null && list3.size() > 0) {
                                    if (list3.size() == 1) {
                                        String string4 = (String) list3.get(0);
                                        if (!string4.equals(string3)) {
                                            this.logWarning(
                                                    "Field \""
                                                            + (string2 != null ? string2 + " " : "")
                                                            + string1
                                                            + "\" mapping clash in class \""
                                                            + string
                                                            + "\" while merging with \""
                                                            + changeLogMapping2.changeLogName
                                                            + "\". Will be mapped to \""
                                                            + string4
                                                            + "\" and not \""
                                                            + string3
                                                            + "\""
                                            );
                                        }
                                    } else if (list3.size() > 1) {
                                        if (this.fieldsHaveTypes) {
                                            this.reportError("More than one field \"" + string2 + " " + string1 + "\" appears in class \"" + string + "\" in log.");
                                        } else {
                                            this.reportError(
                                                    "More than one field with the name \""
                                                            + string1
                                                            + "\" appears in class \""
                                                            + string
                                                            + "\" in log but there are no field types to allow the mappings to be distinguished."
                                            );
                                        }
                                    }
                                } else {
                                    this.putRawFieldMapping(string, changeLogClassKey, string3);
                                }
                            } else {
                                this.putRawFieldMapping(string, changeLogClassKey, string3);
                            }
                        }
                    }
                }
            }
        }
    }

    public void putResolvedMethodMapping(
            String string,
            String string1,
            MethodSignature methodSignature1,
            ChangeLogMethodSignature changeLogMethodSignature,
            HashMap hashMap,
            TwoKeyMap twoKeyMap,
            TwoKeyMap twoKeyMap1,
            TwoKeyMap twoKeyMap2
    ) {
        if ((MethodSignature) this.methodMappings.putValue(string1, methodSignature1, changeLogMethodSignature) != null) {
            this.reportError("Method '" + methodSignature1.getNameWithParameters(hashMap) + "' appears more than once in class '" + string + "'");
        }

        MethodSignature methodSignature2 = (MethodSignature) twoKeyMap.putValue(string1, changeLogMethodSignature, methodSignature1);
        String string2 = methodSignature1.formatParameters(hashMap);
        if (methodSignature2 != null) {
            this.reportError(
                    "Methods '"
                            + methodSignature2.getName()
                            + string2
                            + "' and '"
                            + methodSignature1.getName()
                            + string2
                            + "' are both mapped to the same name '"
                            + changeLogMethodSignature.getName()
                            + "' in class '"
                            + string
                            + "'"
            );
        }

        if (twoKeyMap1 != null) {
            MethodSignature methodSignature3 = this.withAddedParameters(changeLogMethodSignature, ZkmUtils.dotsToSlashes(string1), twoKeyMap2);
            MethodSignature methodSignature4 = (MethodSignature) twoKeyMap1.putValue(string1, methodSignature3, methodSignature1);
            if (methodSignature4 != null) {
                this.reportError(
                        "Methods '"
                                + methodSignature4.getNameWithParameters(hashMap)
                                + "' and '"
                                + methodSignature1.getNameWithParameters(hashMap)
                                + "' are both mapped to '"
                                + changeLogMethodSignature.toDeclarationString()
                                + "' in class '"
                                + string
                                + "'"
                );
            }
        }
    }

    public void addRawFieldMapping(String string, String string1, String string2, Object object) throws IOException {
        String string3 = ZkmUtils.slashesToDots(string);
        ChangeLogClassKey changeLogClassKey = this.createFieldKey(string1, string2);
        this.rawFieldMappings.addValue(string3, changeLogClassKey, object);
    }

    public ChangeLogMapping(String string, ScriptEnvironment scriptEnvironment1, boolean looseChangeLog) {
        this.changeLogName = string;
        this.scriptEnvironment = scriptEnvironment1;
        this.looseChangeLog = looseChangeLog;
    }

    @Override
    public boolean acceptsLineNumberChanges() {
        return true;
    }

    public void checkMethodReturnTypeConsistency(List list1) {
        if (this.rawMethodMappings != null) {
            boolean bl = true;
            int ba = list1.size();

            for (int i = 0; i < ba; i++) {
                String string = (String) list1.get(i);
                ListMultimap listMultimap = this.rawMethodMappings.getMultimap(string);
                if (listMultimap != null) {
                    EnumerationBackedList enumerationBackedList = new EnumerationBackedList(listMultimap.keys());
                    Collections.sort(enumerationBackedList);

                    for (int j = 0; j < enumerationBackedList.size(); j++) {
                        ChangeLogMemberKey changeLogMemberKey = (ChangeLogMemberKey) enumerationBackedList.get(j);
                        String string1 = changeLogMemberKey.getName();
                        String string2 = changeLogMemberKey.getType();
                        if (bl) {
                            bl = false;
                            this.methodsHaveReturnTypes = string2 != null;
                        } else if (this.methodsHaveReturnTypes != (string2 != null)) {
                            this.reportError(
                                    "Method \""
                                            + string1
                                            + "("
                                            + changeLogMemberKey.getParameterTypes()
                                            + ")\" in class \""
                                            + string
                                            + "\" "
                                            + (this.methodsHaveReturnTypes ? "has no return type" : "has a return type")
                                            + " which is inconsistent with the previous method entries. All method entries must have return types or no method entries can have return types."
                            );
                        }
                    }
                }
            }
        }
    }

    @Override
    public void addLineNumberMappings(String string, Map map1) {
        if (this.lineNumberMappings == null) {
            this.lineNumberMappings = new NestedMultiMap(this.classCount);
        }

        ListMultimap listMultimap = new ListMultimap(map1.size(), 3);
        Iterator iterator = map1.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            listMultimap.addValue(entry.getValue(), entry.getKey());
        }

        this.lineNumberMappings.putMultimap(string, listMultimap);
    }

    public static String getPackageName(String string) {
        int ba = string.lastIndexOf(46);
        return ba == -1 ? "" : string.substring(0, ba);
    }

    public void renameRawFieldMapping(String string, String string1, String string2, String string3) throws IOException {
        String string4 = ZkmUtils.slashesToDots(string);
        ChangeLogClassKey changeLogClassKey = this.createFieldKey(string1, string2);
        List list1 = this.rawFieldMappings.getValues(string4, changeLogClassKey);
        if (list1.size() == 1) {
            String string5 = (String) list1.get(0);
            this.rawFieldMappings.removeInnerKey(string4, changeLogClassKey);
            this.createFieldKey(string3, string2);
            this.rawFieldMappings.addValue(string4, changeLogClassKey, string5);
        } else {
            String string6 = ConstantPoolEntry.descriptorToJavaType(string2);
            this.reportFatalError("Class '" + string4 + "' has multiple field mappings for field '" + string6 + " " + string1 + "'.");
        }
    }

    public String getAutoReflectionClass() {
        return this.autoReflectionClass;
    }

    public void reportError(String string) {
        this.errorsFound = true;
        this.scriptEnvironment.logSeriousError_v("Input change log '" + this.changeLogName + "': " + string + this.getSourceFileSuffix());
    }

    public static boolean isAggressiveMethodOverloading(final File file) throws ZkmException {
        BufferedReader openReader = null;
        try {
            openReader = ZkmFileUtils.openReader(file);
            final String line = openReader.readLine();
            if (line != null) {
                final StringTokenizer stringTokenizer = new StringTokenizer(line);
                while (stringTokenizer.hasMoreTokens()) {
                    if (stringTokenizer.nextToken().equals("aggessiveMethodOverloading")) {
                        return true;
                    }
                }
            }
        } catch (final IOException ex) {
        } finally {
            if (openReader != null) {
                try {
                    openReader.close();
                } catch (final IOException ex2) {
                }
            }
        }
        return false;
    }

    public boolean hasTraceBackEntries() {
        return this.traceBackEntries != null && this.traceBackEntries.size() > 0;
    }

    public void reportFatalError(String string) {
        this.errorsFound = true;
        this.scriptEnvironment.logFatalError("Input change log '" + this.changeLogName + "': " + string + this.getSourceFileSuffix());
    }

    public ObjectTriple getMethodParameterChangeClasses() {
        return this.methodParameterChangeClasses;
    }

    public Set getFlowEntryFieldNames(String string) throws IOException {
        HashSet hashSet = ZkmUtils.createHashSet();
        String string1 = ZkmUtils.slashesToDots(string);
        int ba = 0;
        int bb = 0;

        for (List list1 = this.flowEntryList; bb < list1.size(); list1 = this.flowEntryList) {
            ChangeLogMemberEntry changeLogMemberEntry = (ChangeLogMemberEntry) this.flowEntryList.get(ba);
            if (changeLogMemberEntry.getClassName().equals(string1)) {
                hashSet.add(changeLogMemberEntry.getFieldName());
            }

            bb = ++ba;
        }

        return hashSet;
    }

    public String getOriginalClassName(String string) throws IOException {
        if (this.reverseClassMappings == null) {
            return null;
        }

        String string1 = (String) this.reverseClassMappings.get(ZkmUtils.slashesToDots(string));
        return string1 == null ? null : ZkmUtils.dotsToSlashes(string1);
    }

    public void markParsingComplete() {
        this.parsingComplete = true;
    }

    public boolean isParameterListUnchanged(Object object) {
        if (this.addedParametersByMethod == null) {
            return false;
        }

        AddedParameter[] addedParameters1 = (AddedParameter[]) this.addedParametersByMethod.get(object);
        return addedParameters1 == null ? false : addedParameters1.length == 0;
    }
}
