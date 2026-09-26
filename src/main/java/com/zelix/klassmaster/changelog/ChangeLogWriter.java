package com.zelix.klassmaster.changelog;

import com.zelix.klassmaster.archive.SourceArchive;
import com.zelix.klassmaster.changelog.parser.ChangeLogSimpleNode;
import com.zelix.klassmaster.classfile.AbstractFieldInfo;
import com.zelix.klassmaster.classfile.AbstractMethodInfo;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.FieldSignature;
import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.ModuleInfoClass;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.config.ChangeLogInputFile;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.obfuscator.flow.FlowFieldPair;
import com.zelix.klassmaster.obfuscator.flow.FlowObfuscationManager;
import com.zelix.klassmaster.obfuscator.flow.OpaquePredicateField;
import com.zelix.klassmaster.obfuscator.parameters.ChangedMethodDescriptor;
import com.zelix.klassmaster.obfuscator.parameters.MethodParamChangeNode;
import com.zelix.klassmaster.util.IntegerCache;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.NestedMultiMap;
import com.zelix.klassmaster.util.ObjectPair;
import com.zelix.klassmaster.util.ObjectTriple;
import com.zelix.klassmaster.util.RankedValue;
import com.zelix.klassmaster.util.TwoKeyMap;
import com.zelix.klassmaster.util.ZkmAssert;
import com.zelix.klassmaster.util.ZkmStringUtils;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ChangeLogWriter implements LineSeparatorConstant {
    public final IntegerCache integerCache;

    public void writeModules(PrintWriter printWriter, Enumeration enumeration) {
        if (enumeration != null && enumeration.hasMoreElements()) {
            printWriter.println();
            ArrayList arrayList = new ArrayList();

            while (enumeration.hasMoreElements()) {
                ModuleInfoClass moduleInfoClass = (ModuleInfoClass) enumeration.nextElement();
                arrayList.add(new LabeledTuple(moduleInfoClass.getModuleName(), moduleInfoClass));
            }

            Collections.sort(arrayList);
            Iterator iterator = arrayList.iterator();

            while (iterator.hasNext()) {
                LabeledTuple labeledTuple = (LabeledTuple) iterator.next();
                printWriter.println("Module: " + labeledTuple.getLabel() + "\tNameNotChanged");
            }
        }
    }

    public void writeClassTripleEntries(PrintWriter printWriter, ObjectTriple objectTriple, Map map1) {
        if (objectTriple != null || map1.size() > 0) {
            printWriter.println();
            printWriter.println();
            if (objectTriple != null) {
                printWriter.println(
                        "MethodParameterChangeClasses:"
                                + " "
                                + ((ProgramClass) objectTriple.getFirst()).getDottedClassName()
                                + " "
                                + ((ProgramClass) objectTriple.getSecond()).getDottedClassName()
                                + " "
                                + ((ProgramClass) objectTriple.getThird()).getDottedClassName()
                );
            }

            if (map1.size() > 0) {
                ArrayList arrayList = new ArrayList();
                Iterator iterator = map1.entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry entry = (Entry) iterator.next();
                    SourceArchive sourceArchive1 = (SourceArchive) entry.getKey();
                    arrayList.add(new LabeledTuple(sourceArchive1.getModuleName(), sourceArchive1, entry.getValue()));
                }

                Collections.sort(arrayList);
                iterator = arrayList.iterator();

                while (iterator.hasNext()) {
                    LabeledTuple labeledTuple = (LabeledTuple) iterator.next();
                    ObjectTriple objectTriple1 = (ObjectTriple) labeledTuple.getSecond();
                    printWriter.println(
                            "MethodParameterChangeClasses:"
                                    + " "
                                    + ((ProgramClass) objectTriple1.getFirst()).getDottedClassName()
                                    + " "
                                    + ((ProgramClass) objectTriple1.getSecond()).getDottedClassName()
                                    + " "
                                    + ((ProgramClass) objectTriple1.getThird()).getDottedClassName()
                                    + " "
                                    + "Module: "
                                    + ((SourceArchive) labeledTuple.getFirst()).getModuleName()
                    );
                }
            }
        }
    }

    public void writeMethods(
            PrintWriter printWriter, ClassFileBase classFileBase, String string, String string1, HashMap hashMap, TwoKeyMap twoKeyMap, Map map1, Map map2, Map map3
    ) {
        printWriter.println("\tMethodsOf: " + string);
        java.util.Map hashMap1 = twoKeyMap.getInnerMap(string1);
        int methodCount = classFileBase.getMethodCount();
        if (methodCount > 0) {
            if (hashMap1 == null) {
                hashMap1 = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(methodCount));
            } else {
                hashMap1 = ZkmUtils.copyToHashMap(hashMap1);
            }

            AbstractMethodInfo[] abstractMethodInfos = classFileBase.getDeclaredMethods();
            HashMap hashMap2 = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(abstractMethodInfos.length));

            for (int i = 0; i < abstractMethodInfos.length; i++) {
                AbstractMethodInfo abstractMethodInfo = abstractMethodInfos[i];
                MethodSignature methodSignature1 = (MethodSignature) twoKeyMap.getValue(string1, abstractMethodInfo.getSignature());
                if (methodSignature1 != null) {
                    hashMap2.put(methodSignature1, abstractMethodInfo);
                } else if (abstractMethodInfo.isProgramMember()) {
                    methodSignature1 = abstractMethodInfo.getSignature();
                    hashMap2.put(methodSignature1, abstractMethodInfo);
                    hashMap1.put(methodSignature1, methodSignature1);
                }
            }

            ArrayList arrayList = new ArrayList(hashMap1.size());
            Iterator iterator = hashMap1.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                MethodSignature methodSignature2 = (MethodSignature) entry.getKey();
                MethodSignature methodSignature3 = (MethodSignature) entry.getValue();
                AbstractMethodInfo abstractMethodInfo1 = (AbstractMethodInfo) hashMap2.get(methodSignature3);
                Boolean boolean1;
                if ((map3 == null && map1 == null || !map3.containsKey(abstractMethodInfo1) && !map1.containsKey(abstractMethodInfo1))
                        && methodSignature3.equals(methodSignature2)) {
                    boolean1 = Boolean.FALSE;
                } else {
                    boolean1 = Boolean.TRUE;
                }

                String string2 = methodSignature3.formatDeclaration(hashMap);
                arrayList.add(new LabeledTuple(string2, methodSignature3, methodSignature2, boolean1));
            }

            ChangeLogEntryComparator changeLogEntryComparator = new ChangeLogEntryComparator(this);
            Collections.sort(arrayList, changeLogEntryComparator);

            for (int i = 0; i < arrayList.size(); i++) {
                LabeledTuple labeledTuple = (LabeledTuple) arrayList.get(i);
                MethodSignature methodSignature4 = (MethodSignature) labeledTuple.getFirst();
                MethodSignature methodSignature5 = (MethodSignature) labeledTuple.getSecond();
                AbstractMethodInfo abstractMethodInfo2 = (AbstractMethodInfo) hashMap2.get(methodSignature4);
                String string3 = labeledTuple.getLabel();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("\t\t");
                stringBuilder.append(abstractMethodInfo2.getModifierString());
                stringBuilder.append(string3);
                boolean bl;
                if ((Boolean) labeledTuple.getThird()) {
                    if (abstractMethodInfo2.hasChangedParameters() && abstractMethodInfo2.isNameChanged() && map1 != null && map1.containsKey(abstractMethodInfo2)) {
                        stringBuilder.append("\t=>\t");
                        ChangedMethodDescriptor changedMethodDescriptor = (ChangedMethodDescriptor) map1.get(abstractMethodInfo2);
                        stringBuilder.append(AbstractMethodInfo.formatMethodSignature(abstractMethodInfo2.getJvmName(), changedMethodDescriptor.getDescriptor()));
                    }

                    stringBuilder.append("\t=>\t");
                    stringBuilder.append(abstractMethodInfo2.formatNameAndParameters(true));
                    if (abstractMethodInfo2.isNameChanged()) {
                        stringBuilder.append('*');
                    }

                    if (map3 != null) {
                        if (map3.containsKey(abstractMethodInfo2)) {
                            Long long1 = (Long) map3.get(abstractMethodInfo2);
                            ChangedMethodDescriptor changedMethodDescriptor1 = (ChangedMethodDescriptor) map1.get(abstractMethodInfo2);
                            stringBuilder.append(" :");
                            if (abstractMethodInfo2.hasChangedParameters() && abstractMethodInfo2.isNameChanged()) {
                                new MethodSignature(abstractMethodInfo2.getJvmName(), changedMethodDescriptor1.getDescriptor());
                            }

                            stringBuilder.append(AbstractChangeLog.encodeParameterChangeData(changedMethodDescriptor1, long1, string1));
                            bl = HiddenOptionFlags.CHANGE_LOG_PARAMETER_DETAILS;
                        } else {
                            bl = HiddenOptionFlags.CHANGE_LOG_PARAMETER_DETAILS;
                        }
                    } else {
                        bl = HiddenOptionFlags.CHANGE_LOG_PARAMETER_DETAILS;
                    }
                } else {
                    stringBuilder.append("\tSignatureNotChanged:");
                    bl = HiddenOptionFlags.CHANGE_LOG_PARAMETER_DETAILS;
                }

                if (bl && map2 != null) {
                    MethodParamChangeNode methodParamChangeNode = (MethodParamChangeNode) map2.get(abstractMethodInfo2);
                    if (methodParamChangeNode != null) {
                        stringBuilder.append(' ');
                        stringBuilder.append(" ::");
                        stringBuilder.append(
                                AbstractChangeLog.encodeParamChangeNodeData(
                                        methodParamChangeNode.getEffectiveKey(),
                                        methodParamChangeNode.getPrimaryKey(),
                                        methodParamChangeNode.getSecondaryKey(),
                                        methodParamChangeNode.getRandomKey(),
                                        methodParamChangeNode.getFirstNodeXorMask(),
                                        string1
                                )
                        );
                    }
                }

                if (abstractMethodInfo2.isManufactured()) {
                    stringBuilder.append(' ');
                    stringBuilder.append("Manufactured:");
                    printWriter.println(stringBuilder);
                } else {
                    printWriter.println(stringBuilder);
                }
            }
        }
    }

    public ChangeLogWriter(
            ChangeLogInputFile[] changeLogInputFiles,
            PrintWriter printWriter,
            String string,
            String string1,
            HashMap hashMap,
            HashMap hashMap1,
            HashMap hashMap2,
            TwoKeyMap twoKeyMap,
            TwoKeyMap twoKeyMap1,
            NestedMultiMap nestedMultiMap,
            HashMap hashMap3,
            FlowObfuscationManager flowObfuscationManager,
            ProgramClass programClass1,
            Map map1,
            ProgramClass programClass2,
            Map map2,
            Map map3,
            Map map4,
            Map map5,
            ObjectTriple objectTriple,
            Map map6,
            ChangeLogMapping changeLogMapping1,
            IntegerCache integerCache1,
            Enumeration enumeration,
            boolean bl
    ) throws IOException {
        this.integerCache = integerCache1;
        if (printWriter != null) {
            printWriter.println(
                    "// [\""
                            + string
                            + "\" "
                            + "version="
                            + "27.0.0"
                            + " "
                            + (string1 != null ? "encoding=\"" + string1 + "\" " : "")
                            + (bl ? "aggessiveMethodOverloading " : "")
                            + ZkmUtils.getTimestamp()
                            + "]"
                            + LineSeparatorConstant.LINE_SEPARATOR
                            + "// DO NOT EDIT THIS FILE. You need it to interpret exception stack traces."
                            + LineSeparatorConstant.LINE_SEPARATOR
            );
            if (changeLogInputFiles != null && changeLogInputFiles.length > 0) {
                if (changeLogInputFiles.length == 1) {
                    printWriter.println(
                            "//\""
                                    + new File(changeLogInputFiles[0].getFileName()).getAbsolutePath()
                                    + "\" was used as an input change log."
                                    + LineSeparatorConstant.LINE_SEPARATOR
                    );
                } else {
                    StringBuilder stringBuilder = new StringBuilder();

                    for (int i = 0; i < changeLogInputFiles.length; i++) {
                        if (i > 0) {
                            stringBuilder.append(", ");
                        }

                        stringBuilder.append(new File(changeLogInputFiles[i].getFileName()).getAbsolutePath());
                    }

                    printWriter.println("//\"" + stringBuilder.toString() + "\" were used as input change logs." + LineSeparatorConstant.LINE_SEPARATOR);
                }
            }

            this.writeModules(printWriter, enumeration);
            writePackages(printWriter, hashMap);
            LabeledTuple[] labeledTuples = new LabeledTuple[hashMap1.size()];
            int bb = 0;
            Iterator iterator = hashMap1.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                String string2 = (String) entry.getKey();
                String string3 = (String) entry.getValue();
                Boolean boolean1;
                if (string2.equals(string3)) {
                    boolean1 = Boolean.FALSE;
                } else {
                    boolean1 = Boolean.TRUE;
                }

                ClassFileBase classFileBase = ClassHierarchyNode.findClassFile(string3);
                labeledTuples[bb++] = new LabeledTuple(string2, string3, classFileBase, boolean1);
            }

            Arrays.sort(labeledTuples);

            for (int i = 0; i < labeledTuples.length; i++) {
                String string6 = labeledTuples[i].getLabel();
                String string7 = (String) labeledTuples[i].getFirst();
                ClassFileBase classFileBase1 = (ClassFileBase) labeledTuples[i].getSecond();
                Boolean boolean2 = (Boolean) labeledTuples[i].getThird();
                String string8 = string6.replace('/', '.');
                String string4 = string7.replace('/', '.');
                if (boolean2) {
                    printWriter.print(
                            LineSeparatorConstant.LINE_SEPARATOR
                                    + "Class: "
                                    + classFileBase1.getModifierString()
                                    + string8
                                    + "\t=>\t"
                                    + string4
                                    + LineSeparatorConstant.LINE_SEPARATOR
                    );
                } else {
                    printWriter.print(
                            LineSeparatorConstant.LINE_SEPARATOR
                                    + "Class: "
                                    + classFileBase1.getModifierString()
                                    + string4
                                    + "\tNameNotChanged"
                                    + LineSeparatorConstant.LINE_SEPARATOR
                    );
                }

                String string5 = (String) hashMap3.get(string7);
                if (string5 == null && classFileBase1 instanceof ProgramClass) {
                    string5 = ((ProgramClass) classFileBase1).getSourceFileName();
                }

                if (string5 != null) {
                    printWriter.print("\tSource: \"" + ZkmStringUtils.replaceAll(string5, "\"", "\"") + '"' + LineSeparatorConstant.LINE_SEPARATOR);
                }

                this.writeFields(printWriter, hashMap2, twoKeyMap, classFileBase1, string8, string7, changeLogMapping1);
                this.writeMethods(printWriter, classFileBase1, string8, string7, hashMap2, twoKeyMap1, map3, map4, map5);
                this.writeLineNumbers(printWriter, string8, string7, nestedMultiMap);
            }

            this.writeLookupClassEntries("AutoReflectionClass:", printWriter, programClass1, map1);
            this.writeLookupClassEntries("ObfuscateReferencesClass:", printWriter, programClass2, map2);
            this.writeClassTripleEntries(printWriter, objectTriple, map6);
            this.writeFlowObfuscationData(printWriter, hashMap1, flowObfuscationManager, labeledTuples);
            printWriter.flush();
        }
    }

    public void writeFields(
            PrintWriter printWriter,
            HashMap hashMap,
            TwoKeyMap twoKeyMap,
            ClassFileBase classFileBase,
            String string,
            String string1,
            ChangeLogMapping changeLogMapping1
    ) throws IOException {
        printWriter.println("\tFieldsOf: " + string);
        Map map1 = twoKeyMap.getInnerMap(string1);
        if (map1 != null && map1.size() > 0) {
            AbstractFieldInfo[] abstractFieldInfos = classFileBase.getFields();
            HashMap hashMap1 = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(abstractFieldInfos.length));

            for (int i = 0; i < abstractFieldInfos.length; i++) {
                AbstractFieldInfo abstractFieldInfo = abstractFieldInfos[i];
                FieldSignature fieldSignature = (FieldSignature) twoKeyMap.getValue(string1, abstractFieldInfo.getSignature());
                if (fieldSignature != null) {
                    hashMap1.put(fieldSignature, abstractFieldInfo);
                } else {
                    if (!abstractFieldInfo.isStrictlyPrivate()
                            && (!abstractFieldInfo.getOwningClass().isInterface() || !abstractFieldInfo.isPublic() || !abstractFieldInfo.isFinal())) {
                        boolean bl = false;
                    } else {
                        boolean bl5 = true;
                    }

                    if (abstractFieldInfo.isStatic() && abstractFieldInfo.isManufactured() && !abstractFieldInfo.isRenamed()) {
                        boolean bl2 = true;
                    } else {
                        boolean bl1 = false;
                    }

                    if (!classFileBase.isProgramClass() && !changeLogMapping1.hasFieldMapping(string, fieldSignature)) {
                        boolean bl4 = true;
                    } else {
                        boolean bl3 = false;
                    }
                }
            }

            LabeledTuple[] labeledTuples = new LabeledTuple[map1.size()];
            int bb = 0;
            Iterator iterator = map1.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                FieldSignature fieldSignature1 = (FieldSignature) entry.getKey();
                FieldSignature fieldSignature2 = (FieldSignature) entry.getValue();
                Boolean boolean1;
                if (fieldSignature2.getName().equals(fieldSignature1.getName())) {
                    boolean1 = Boolean.FALSE;
                } else {
                    boolean1 = Boolean.TRUE;
                }

                labeledTuples[bb++] = new LabeledTuple(
                        fieldSignature2.getName() + " " + fieldSignature2.formatDeclaration(hashMap), fieldSignature2, fieldSignature1, boolean1
                );
            }

            Arrays.sort(labeledTuples);
            int bc = 0;

            for (int i = 0; i < labeledTuples.length; i++) {
                FieldSignature fieldSignature3 = (FieldSignature) labeledTuples[i].getFirst();
                FieldSignature fieldSignature4 = (FieldSignature) labeledTuples[i].getSecond();
                AbstractFieldInfo abstractFieldInfo1 = (AbstractFieldInfo) hashMap1.get(fieldSignature3);
                String string4;
                if (abstractFieldInfo1 == null) {
                    string4 = "public static ";
                } else {
                    string4 = abstractFieldInfo1.getModifierString();
                }

                String string2 = fieldSignature3.getName();
                String string3 = fieldSignature4.getName();
                if ((Boolean) labeledTuples[i].getThird()) {
                    printWriter.println(
                            "\t\t"
                                    + string4
                                    + MethodSignature.descriptorToJavaType(fieldSignature3.getDescriptor(), hashMap)
                                    + " "
                                    + string2
                                    + "\t=>\t"
                                    + string3
                                    + (abstractFieldInfo1.isManufactured() ? " Manufactured:" : "")
                    );
                } else {
                    printWriter.println(
                            "\t\t"
                                    + string4
                                    + MethodSignature.descriptorToJavaType(fieldSignature4.getDescriptor(), hashMap)
                                    + " "
                                    + string3
                                    + "\tNameNotChanged"
                                    + (abstractFieldInfo1.isManufactured() ? " Manufactured:" : "")
                    );
                }

                bc++;
            }
        }
    }

    public void writeFlowObfuscationData(PrintWriter printWriter, HashMap hashMap, FlowObfuscationManager flowObfuscationManager, LabeledTuple[] labeledTuples) throws IOException {
        if (flowObfuscationManager != null) {
            int ba = 0;
            HashMap hashMap1 = ZkmUtils.createHashMap();
            TwoKeyMap twoKeyMap = new TwoKeyMap();
            HashMap hashMap2 = ZkmUtils.createHashMap();
            List list1 = flowObfuscationManager.getGroupFieldsInUse();
            Collections.sort(list1);
            if (list1 != null && list1.size() > 0) {
                printWriter.println();
                printWriter.println();

                for (Iterator iterator = list1.iterator(); iterator.hasNext(); ba++) {
                    OpaquePredicateField opaquePredicateField = (OpaquePredicateField) iterator.next();
                    String string = opaquePredicateField.getOwnerClassName();
                    String string14 = (String) ZkmUtils.mapOrSelf(string, hashMap);
                    String string2 = ZkmUtils.slashesToDots(string);
                    String string3 = AbstractChangeLog.encodeOpaquePredicateData(opaquePredicateField, flowObfuscationManager.getRandom());
                    printWriter.println("TraceBackClass: " + string2 + "\t" + "Data: " + string3);
                    hashMap1.put(opaquePredicateField, this.integerCache.valueOf(ba));
                    hashMap2.put("[L" + string + ';', this.integerCache.valueOf(ba));
                }
            }

            ArrayList arrayList = new ArrayList();
            ListMultimap listMultimap = flowObfuscationManager.getPackageFieldsByPackage();
            if (listMultimap != null) {
                Enumeration enumeration = listMultimap.keys();

                while (enumeration.hasMoreElements()) {
                    String string1 = (String) enumeration.nextElement();
                    List list2 = listMultimap.getValues(string1);
                    Iterator iterator2 = list2.iterator();

                    while (iterator2.hasNext()) {
                        OpaquePredicateField opaquePredicateField1 = (OpaquePredicateField) iterator2.next();
                        arrayList.add(new ObjectPair(string1, opaquePredicateField1));
                    }
                }
            }

            ChangeLogKeyComparator changeLogKeyComparator = new ChangeLogKeyComparator(this);
            Collections.sort(arrayList, changeLogKeyComparator);
            Iterator iterator1 = arrayList.iterator();

            while (iterator1.hasNext()) {
                ObjectPair objectPair = (ObjectPair) iterator1.next();
                String string9 = (String) objectPair.getFirst();
                OpaquePredicateField opaquePredicateField4 = (OpaquePredicateField) objectPair.getSecond();
                String string4 = opaquePredicateField4.getOwnerClassName();
                String string13 = (String) ZkmUtils.mapOrSelf(string4, hashMap);
                String string5 = ZkmUtils.slashesToDots(string4);
                hashMap2.get(opaquePredicateField4.getFieldType());
                String string6 = AbstractChangeLog.encodeOpaquePredicateData(opaquePredicateField4, flowObfuscationManager.getRandom());
                TwoKeyMap twoKeyMap1;
                String string15;
                OpaquePredicateField opaquePredicateField6;
                IntegerCache integerCache1;
                if (string9.length() > 0) {
                    String string7 = ZkmUtils.slashesToDots(string9);
                    printWriter.println("ForwardClass: " + string5 + " " + "Package: " + string7 + "\t" + "Data: " + string6);
                    twoKeyMap1 = twoKeyMap;
                    string15 = string9;
                    long bd = 59172929949573L;
                    opaquePredicateField6 = opaquePredicateField4;
                    integerCache1 = this.integerCache;
                } else {
                    printWriter.println("ForwardClass: " + string5 + "\t" + "Data: " + string6);
                    twoKeyMap1 = twoKeyMap;
                    string15 = string9;
                    long bc = 59172929949573L;
                    opaquePredicateField6 = opaquePredicateField4;
                    integerCache1 = this.integerCache;
                }

                Integer integer1 = integerCache1.valueOf(ba++);
                OpaquePredicateField opaquePredicateField3 = opaquePredicateField6;
                twoKeyMap1.putValue(string15, opaquePredicateField3, integer1);
            }

            for (int i = 0; i < labeledTuples.length; i++) {
                LabeledTuple labeledTuple = labeledTuples[i];
                String string10 = labeledTuple.getLabel();
                String string11 = ClassFileBase.getPackagePath(string10);
                ClassFileBase classFileBase = (ClassFileBase) labeledTuple.getSecond();
                FlowFieldPair flowFieldPair = flowObfuscationManager.getFieldPair(classFileBase);
                if (flowFieldPair != null) {
                    OpaquePredicateField opaquePredicateField5 = flowFieldPair.getGroupField();
                    if (opaquePredicateField5 != null) {
                        OpaquePredicateField opaquePredicateField2 = flowFieldPair.getPackageField();
                        ZkmAssert.assertNotNull(opaquePredicateField2, "'" + string10 + "' (1)");
                        String string12 = (String) ZkmUtils.mapOrSelf(string10, hashMap);
                        Integer integer2 = (Integer) hashMap1.get(opaquePredicateField5);
                        ZkmAssert.assertNotNull(
                                integer2, "'" + string10 + "' '" + opaquePredicateField5.getOwnerClassName() + "' '" + opaquePredicateField5.getFieldName() + "' (2)"
                        );
                        Integer integer = (Integer) twoKeyMap.getValue(string11, opaquePredicateField2);
                        ZkmAssert.assertNotNull(
                                integer,
                                "'"
                                        + string10
                                        + "' '"
                                        + string11
                                        + "' '"
                                        + opaquePredicateField2.getOwnerClassName()
                                        + "' '"
                                        + opaquePredicateField2.getFieldName()
                                        + "' (3)"
                        );
                        String string8 = AbstractChangeLog.encodeMemberClassData(
                                integer2,
                                integer,
                                string12,
                                flowObfuscationManager.isPrimaryCallerClass(classFileBase),
                                flowObfuscationManager.isSecondaryCallerClass(classFileBase),
                                flowObfuscationManager.getRandom()
                        );
                        printWriter.println("MemberClass: " + ZkmUtils.slashesToDots(string10) + "\t" + "Data: " + string8);
                    }
                }
            }
        }
    }

    public static void writePackages(PrintWriter printWriter, HashMap hashMap) {
        printWriter.println();
        int ba = hashMap.size();
        LabeledTuple[] labeledTuples = new LabeledTuple[ba];
        int bb = 0;
        Iterator iterator = hashMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            String string = (String) entry.getKey();
            labeledTuples[bb++] = new LabeledTuple(string, entry.getValue(), null);
        }

        Arrays.sort(labeledTuples);

        for (int i = 0; i < ba; i++) {
            String string1 = (String) labeledTuples[i].getFirst();
            printWriter.println(
                    "Package: "
                            + labeledTuples[i].getLabel().replace('/', '.')
                            + (labeledTuples[i].getLabel().equals(string1) ? "\tNameNotChanged" : "\t=>\t" + string1.replace('/', '.'))
            );
        }
    }

    public void writeLookupClassEntries(String string, PrintWriter printWriter, ProgramClass programClass1, Map map1) {
        if (programClass1 != null || map1.size() > 0) {
            printWriter.println();
            printWriter.println();
            if (programClass1 != null) {
                printWriter.println(string + " " + programClass1.getDottedClassName());
            }

            if (map1.size() > 0) {
                ArrayList arrayList = new ArrayList();
                Iterator iterator = map1.entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry entry = (Entry) iterator.next();
                    SourceArchive sourceArchive1 = (SourceArchive) entry.getKey();
                    arrayList.add(new LabeledTuple(sourceArchive1.getModuleName(), sourceArchive1, entry.getValue()));
                }

                Collections.sort(arrayList);
                iterator = arrayList.iterator();

                while (iterator.hasNext()) {
                    LabeledTuple labeledTuple = (LabeledTuple) iterator.next();
                    printWriter.println(
                            string
                                    + " "
                                    + ((ProgramClass) labeledTuple.getSecond()).getDottedClassName()
                                    + " "
                                    + "Module: "
                                    + ((SourceArchive) labeledTuple.getFirst()).getModuleName()
                    );
                }
            }
        }
    }

    public void writeLineNumbers(PrintWriter printWriter, String string, String string1, NestedMultiMap nestedMultiMap) {
        ChangeLogSimpleNode.getOpaqueStrings();
        if (nestedMultiMap != null && nestedMultiMap.getKeyCount() > 0) {
            printWriter.println("\tLineNumbersOf: " + string);
            ListMultimap listMultimap = nestedMultiMap.getMultimap(string1);
            if (listMultimap != null) {
                Enumeration enumeration = listMultimap.keys();
                RankedValue[] rankedValues = new RankedValue[listMultimap.getKeyCount()];
                int ba = 0;

                while (enumeration.hasMoreElements()) {
                    Integer integer = (Integer) enumeration.nextElement();
                    List list1 = listMultimap.getValues(integer);
                    rankedValues[ba++] = new RankedValue(integer, list1);
                }

                Arrays.sort(rankedValues);

                for (int i = 0; i < rankedValues.length; i++) {
                    int rank = rankedValues[i].getRank();
                    List list2 = (List) rankedValues[i].getValue();
                    int bb = list2.size() - 1;
                    if (bb > 0) {
                        Collections.sort(list2);
                    }

                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append("\t\t" + rank + "\t=>\t");

                    for (int j = 0; j < bb; j++) {
                        stringBuffer.append(((Integer) list2.get(j)).intValue());
                        if (j < bb - 1) {
                            stringBuffer.append(", ");
                        }
                    }

                    if (bb > 0) {
                        stringBuffer.append(" and ");
                    }

                    stringBuffer.append(((Integer) list2.get(bb)).intValue());
                    printWriter.println(stringBuffer.toString());
                }
            }
        }
    }
}
