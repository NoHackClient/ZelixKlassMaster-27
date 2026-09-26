package com.zelix.klassmaster.obfuscator.parameters;

import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.constpool.ConstantPool;
import com.zelix.klassmaster.classfile.constpool.ResolvedMethodRef;
import com.zelix.klassmaster.classfile.hierarchy.ClassMemberLookup;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.classfile.hierarchy.InheritedMemberAnalyzer;
import com.zelix.klassmaster.classfile.insn.ExceptionHandlerSpec;
import com.zelix.klassmaster.classfile.insn.Instruction;
import com.zelix.klassmaster.classfile.insn.LocalVariableIndex;
import com.zelix.klassmaster.classfile.insn.LocalVariableList;
import com.zelix.klassmaster.classfile.insn.PrimitiveBoxingGenerator;
import com.zelix.klassmaster.classfile.insn.SimpleInstruction;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.obfuscator.exclude.AbstractExclusionSpec;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.CountingBag;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.NestedMultiMap;
import com.zelix.klassmaster.util.TwoKeyMap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

public class MethodParameterObfuscator {
    public final Map originalSignatures = ZkmUtils.createHashMap();
    public final TwoKeyMap originalToCurrentSignatures;
    public final TwoKeyMap currentToOriginalSignatures;

    public MethodParameterObfuscator(
            ProgramClass[] programClass1,
            ClassRepository classRepository1,
            ListMultimap listMultimap,
            NestedMultiMap nestedMultiMap,
            Map map1,
            TwoKeyMap twoKeyMap,
            TwoKeyMap twoKeyMap1,
            Map map2,
            Set set1,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            ClassResolver classResolver1,
            ScriptEnvironment scriptEnvironment1,
            boolean bl
    ) throws ZkmException, IOException {
        this.originalToCurrentSignatures = twoKeyMap;
        this.currentToOriginalSignatures = twoKeyMap1;
        this.loadOriginalSignatures(programClass1);
        HashSet hashSet = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(programClass1.length));
        hashSet.addAll(listMultimap.keySet());
        hashSet.addAll(nestedMultiMap.keySet());
        if (HiddenOptionFlags.USE_PARALLEL && HiddenOptionFlags.PROCESSOR_COUNT >= 2) {
            Vector vector = new Vector();
            hashSet.parallelStream()
                    .forEach(
                            programClass4 -> {
                                try {
                                    try {
                                        com.zelix.klassmaster.util.ZkmUtils.<com.zelix.klassmaster.exceptions.ZkmProcessingException>mayThrow();
                                        this.obfuscateClassParameters(
                                                ((com.zelix.klassmaster.classfile.ProgramClass) programClass4),
                                                listMultimap.getValues(programClass4),
                                                nestedMultiMap.getMultimap(programClass4),
                                                set1,
                                                map2,
                                                classRepository1,
                                                inheritedMemberAnalyzer,
                                                classResolver1,
                                                bl
                                        );
                                    } catch (ZkmProcessingException zkmProcessingException) {
                                        vector.add(zkmProcessingException);
                                    }
                                } catch (Throwable throwable) {
                                    throw ZkmUtils.sneakyThrow(throwable);
                                }
                            }
                    );
            if (!vector.isEmpty()) {
                throw (ZkmProcessingException) vector.get(0);
            }
        } else {
            Iterator iterator = hashSet.iterator();

            while (iterator.hasNext()) {
                ProgramClass programClass2 = (ProgramClass) iterator.next();
                this.obfuscateClassParameters(
                        programClass2,
                        listMultimap.getValues(programClass2),
                        nestedMultiMap.getMultimap(programClass2),
                        set1,
                        map2,
                        classRepository1,
                        inheritedMemberAnalyzer,
                        classResolver1,
                        bl
                );
            }
        }

        int bc = 0;
        int bd = 0;
        PrintWriter printWriter = scriptEnvironment1.getLogWriter();
        Iterator iterator1 = listMultimap.entrySet().iterator();

        while (iterator1.hasNext()) {
            Entry entry = (Entry) iterator1.next();
            bc++;
            if (scriptEnvironment1.isVerbose()) {
                printWriter.println(
                        "\tObfuscated parameter lists in class '" + AbstractExclusionSpec.formatClass((ClassFileBase) entry.getKey(), classRepository1, false) + "'"
                );
            }

            ProgramClass programClass3 = (ProgramClass) entry.getKey();
            Iterator iterator2 = ((List) entry.getValue()).iterator();

            while (iterator2.hasNext()) {
                MethodInfo methodInfo1 = (MethodInfo) iterator2.next();
                if (HiddenOptionFlags.MARK_OBFUSCATED_PARAMS_VARARGS && programClass3.supportsJava5()) {
                    methodInfo1.markVarargs();
                }

                String string = MethodSignature.formatParameterTypes(methodInfo1.getDescriptor(), map1);
                String string1 = "([Ljava/lang/Object;)" + methodInfo1.getReturnDescriptor();
                methodInfo1.setDescriptor(string1);
                bd++;
                if (scriptEnvironment1.isVerbose()) {
                    String string2 = MethodSignature.formatParameterTypes(methodInfo1.getDescriptor(), map1);
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(
                            "\t\tmethod '"
                                    + methodInfo1.getModifierString()
                                    + methodInfo1.getOriginalMemberName()
                                    + string
                                    + "' => '"
                                    + methodInfo1.getOriginalMemberName()
                                    + string2
                                    + "'"
                    );
                    if (methodInfo1.isRenamed()) {
                        stringBuilder.append(" (");
                        stringBuilder.append(methodInfo1.toDisplayString());
                        stringBuilder.append(')');
                    }

                    if (methodInfo1.isParameterSlotsShuffled()) {
                        int[] shuffledIndices = methodInfo1.getLocalVariableList().getShuffledIndices();
                        if (shuffledIndices != null) {
                            stringBuilder.append(" [");

                            for (int i = 0; i < shuffledIndices.length; i++) {
                                stringBuilder.append(shuffledIndices[i]);
                                if (i < shuffledIndices.length - 1) {
                                    stringBuilder.append(',');
                                }
                            }

                            stringBuilder.append(']');
                        }
                    }

                    printWriter.println(stringBuilder.toString());
                }
            }
        }

        if (scriptEnvironment1.isVerbose()) {
            scriptEnvironment1.getLogWriter()
                    .println(
                            "\tMethod Parameter Obfuscation : Obfuscated parameters in "
                                    + bd
                                    + " method"
                                    + (bd == 1 ? "" : 's')
                                    + " in "
                                    + bc
                                    + " class"
                                    + (bc == 1 ? "" : "es")
                                    + "."
                    );
        }

        classRepository1.resetClassReferences(programClass1, scriptEnvironment1, true);
        this.recordSignatureMappings(programClass1);
    }

    public ArrayList buildPackTwoObjectsCode(LocalVariableList localVariableList1) {
        ArrayList arrayList = new ArrayList(12);
        arrayList.add(Instruction.createObjectLoad(2, localVariableList1, 10));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createIntLoad(3, localVariableList1, 10));
        arrayList.add(Instruction.createObjectLoad(0, localVariableList1, 10));
        arrayList.add(SimpleInstruction.forOpcode(83));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createIntIncrement(3, 1, localVariableList1, 10));
        arrayList.add(Instruction.createIntLoad(3, localVariableList1, 10));
        arrayList.add(Instruction.createObjectLoad(1, localVariableList1, 10));
        arrayList.add(SimpleInstruction.forOpcode(83));
        arrayList.add(SimpleInstruction.forOpcode(176));
        return arrayList;
    }

    public ArrayList buildPackDoubleCode(
            LocalVariableList localVariableList1, List list1, ProgramClass programClass1, ClassMemberLookup classMemberLookup1, ClassResolver classResolver1
    ) throws ZkmException, IOException {
        ConstantPool constantPool1 = programClass1.getClassConstantPool();
        ArrayList arrayList = new ArrayList(9);
        arrayList.add(Instruction.createObjectLoad(2, localVariableList1, 10));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createIntLoad(3, localVariableList1, 10));
        PrimitiveBoxingGenerator.boxDoubleLocal(
                0, arrayList, programClass1.supportsJava5(), localVariableList1, list1, constantPool1, classMemberLookup1, classResolver1, 10
        );
        arrayList.add(SimpleInstruction.forOpcode(83));
        arrayList.add(SimpleInstruction.forOpcode(176));
        return arrayList;
    }

    public ResolvedMethodRef createPackDoubleMethod(
            ProgramClass programClass1,
            List list1,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        LocalVariableList localVariableList1 = new LocalVariableList(true, "(D[Ljava/lang/Object;I)[Ljava/lang/Object;", 4);
        ArrayList arrayList = this.buildPackDoubleCode(localVariableList1, list1, programClass1, classMemberLookup1, classResolver1);
        ExceptionHandlerSpec[] exceptionHandlerSpecs = new ExceptionHandlerSpec[0];
        MethodInfo methodInfo1 = programClass1.createUniquelyNamedStaticMethod(
                "(D[Ljava/lang/Object;I)[Ljava/lang/Object;",
                arrayList,
                5,
                4,
                1,
                localVariableList1,
                exceptionHandlerSpecs,
                "Method Parameter Obfuscation",
                list1,
                inheritedMemberAnalyzer,
                classMemberLookup1,
                10
        );
        return programClass1.getClassConstantPool().getOrCreateMethodRef(methodInfo1, list1);
    }

    public ResolvedMethodRef createPackFloatMethod(
            ProgramClass programClass1,
            List list1,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        LocalVariableList localVariableList1 = new LocalVariableList(true, "(F[Ljava/lang/Object;I)[Ljava/lang/Object;", 3);
        ArrayList arrayList = this.buildPackFloatCode(localVariableList1, list1, programClass1, classMemberLookup1, classResolver1);
        ExceptionHandlerSpec[] exceptionHandlerSpecs = new ExceptionHandlerSpec[0];
        MethodInfo methodInfo1 = programClass1.createUniquelyNamedStaticMethod(
                "(F[Ljava/lang/Object;I)[Ljava/lang/Object;",
                arrayList,
                4,
                3,
                1,
                localVariableList1,
                exceptionHandlerSpecs,
                "Method Parameter Obfuscation",
                list1,
                inheritedMemberAnalyzer,
                classMemberLookup1,
                10
        );
        return programClass1.getClassConstantPool().getOrCreateMethodRef(methodInfo1, list1);
    }

    public ResolvedMethodRef createPackIntMethod(
            ProgramClass programClass1,
            List list1,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        LocalVariableList localVariableList1 = new LocalVariableList(true, "(I[Ljava/lang/Object;I)[Ljava/lang/Object;", 3);
        ArrayList arrayList = this.buildPackIntCode(localVariableList1, list1, programClass1, classMemberLookup1, classResolver1);
        ExceptionHandlerSpec[] exceptionHandlerSpecs = new ExceptionHandlerSpec[0];
        MethodInfo methodInfo1 = programClass1.createUniquelyNamedStaticMethod(
                "(I[Ljava/lang/Object;I)[Ljava/lang/Object;",
                arrayList,
                4,
                3,
                1,
                localVariableList1,
                exceptionHandlerSpecs,
                "Method Parameter Obfuscation",
                list1,
                inheritedMemberAnalyzer,
                classMemberLookup1,
                10
        );
        return programClass1.getClassConstantPool().getOrCreateMethodRef(methodInfo1, list1);
    }

    public ArrayList buildPackThreeObjectsCode(LocalVariableList localVariableList1) {
        ArrayList arrayList = new ArrayList(12);
        arrayList.add(Instruction.createObjectLoad(3, localVariableList1, 10));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createIntLoad(4, localVariableList1, 10));
        arrayList.add(Instruction.createObjectLoad(0, localVariableList1, 10));
        arrayList.add(SimpleInstruction.forOpcode(83));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createIntIncrement(4, 1, localVariableList1, 10));
        arrayList.add(Instruction.createIntLoad(4, localVariableList1, 10));
        arrayList.add(Instruction.createObjectLoad(1, localVariableList1, 10));
        arrayList.add(SimpleInstruction.forOpcode(83));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createIntIncrement(4, 1, localVariableList1, 10));
        arrayList.add(Instruction.createIntLoad(4, localVariableList1, 10));
        arrayList.add(Instruction.createObjectLoad(2, localVariableList1, 10));
        arrayList.add(SimpleInstruction.forOpcode(83));
        arrayList.add(SimpleInstruction.forOpcode(176));
        return arrayList;
    }

    public ArrayList buildPackFloatCode(
            LocalVariableList localVariableList1, List list1, ProgramClass programClass1, ClassMemberLookup classMemberLookup1, ClassResolver classResolver1
    ) throws ZkmException, IOException {
        ConstantPool constantPool1 = programClass1.getClassConstantPool();
        ArrayList arrayList = new ArrayList(9);
        arrayList.add(Instruction.createObjectLoad(1, localVariableList1, 10));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createIntLoad(2, localVariableList1, 10));
        PrimitiveBoxingGenerator.boxFloatLocal(
                0, arrayList, programClass1.supportsJava5(), localVariableList1, list1, constantPool1, classMemberLookup1, classResolver1, 10
        );
        arrayList.add(SimpleInstruction.forOpcode(83));
        arrayList.add(SimpleInstruction.forOpcode(176));
        return arrayList;
    }

    public ArrayList buildPackLongCode(
            LocalVariableList localVariableList1, List list1, ProgramClass programClass1, ClassMemberLookup classMemberLookup1, ClassResolver classResolver1
    ) throws ZkmException, IOException {
        ConstantPool constantPool1 = programClass1.getClassConstantPool();
        ArrayList arrayList = new ArrayList(9);
        arrayList.add(Instruction.createObjectLoad(2, localVariableList1, 10));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createIntLoad(3, localVariableList1, 10));
        PrimitiveBoxingGenerator.boxLongLocal(
                0, arrayList, programClass1.supportsJava5(), localVariableList1, list1, constantPool1, classMemberLookup1, classResolver1, 10
        );
        arrayList.add(SimpleInstruction.forOpcode(83));
        arrayList.add(SimpleInstruction.forOpcode(176));
        return arrayList;
    }

    public ResolvedMethodRef createPackTwoObjectsMethod(
            ProgramClass programClass1, List list1, InheritedMemberAnalyzer inheritedMemberAnalyzer, ClassMemberLookup classMemberLookup1
    ) throws ZkmProcessingException {
        LocalVariableList localVariableList1 = new LocalVariableList(true, "(Ljava/lang/Object;Ljava/lang/Object;[Ljava/lang/Object;I)[Ljava/lang/Object;", 4);
        ArrayList arrayList = this.buildPackTwoObjectsCode(localVariableList1);
        ExceptionHandlerSpec[] exceptionHandlerSpecs = new ExceptionHandlerSpec[0];
        MethodInfo methodInfo1 = programClass1.createUniquelyNamedStaticMethod(
                "(Ljava/lang/Object;Ljava/lang/Object;[Ljava/lang/Object;I)[Ljava/lang/Object;",
                arrayList,
                4,
                4,
                1,
                localVariableList1,
                exceptionHandlerSpecs,
                "Method Parameter Obfuscation",
                list1,
                inheritedMemberAnalyzer,
                classMemberLookup1,
                10
        );
        return programClass1.getClassConstantPool().getOrCreateMethodRef(methodInfo1, list1);
    }

    public ResolvedMethodRef createPackThreeIntsMethod(
            ProgramClass programClass1,
            List list1,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        LocalVariableList localVariableList1 = new LocalVariableList(true, "(III[Ljava/lang/Object;I)[Ljava/lang/Object;", 5);
        ArrayList arrayList = this.buildPackThreeIntsCode(localVariableList1, list1, programClass1, classMemberLookup1, classResolver1);
        ExceptionHandlerSpec[] exceptionHandlerSpecs = new ExceptionHandlerSpec[0];
        MethodInfo methodInfo1 = programClass1.createUniquelyNamedStaticMethod(
                "(III[Ljava/lang/Object;I)[Ljava/lang/Object;",
                arrayList,
                4,
                5,
                1,
                localVariableList1,
                exceptionHandlerSpecs,
                "Method Parameter Obfuscation",
                list1,
                inheritedMemberAnalyzer,
                classMemberLookup1,
                10
        );
        return programClass1.getClassConstantPool().getOrCreateMethodRef(methodInfo1, list1);
    }

    public ResolvedMethodRef createPackTwoIntsMethod(
            ProgramClass programClass1,
            List list1,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        LocalVariableList localVariableList1 = new LocalVariableList(true, "(II[Ljava/lang/Object;I)[Ljava/lang/Object;", 4);
        ArrayList arrayList = this.buildPackTwoIntsCode(localVariableList1, list1, programClass1, classMemberLookup1, classResolver1);
        ExceptionHandlerSpec[] exceptionHandlerSpecs = new ExceptionHandlerSpec[0];
        MethodInfo methodInfo1 = programClass1.createUniquelyNamedStaticMethod(
                "(II[Ljava/lang/Object;I)[Ljava/lang/Object;",
                arrayList,
                4,
                4,
                1,
                localVariableList1,
                exceptionHandlerSpecs,
                "Method Parameter Obfuscation",
                list1,
                inheritedMemberAnalyzer,
                classMemberLookup1,
                10
        );
        return programClass1.getClassConstantPool().getOrCreateMethodRef(methodInfo1, list1);
    }

    public void obfuscateClassParameters(
            ProgramClass programClass1,
            List list1,
            ListMultimap listMultimap,
            Set set1,
            Map map1,
            ClassMemberLookup classMemberLookup1,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            ClassResolver classResolver1,
            boolean bl
    ) throws ZkmException, IOException {
        ArrayList arrayList = new ArrayList();
        ConstantPool constantPool1 = programClass1.getClassConstantPool();
        Map map2 = null;
        if (listMultimap != null && usesPackHelperMethods(programClass1)) {
            map2 = this.createPackHelperMethods(programClass1, listMultimap, set1, arrayList, inheritedMemberAnalyzer, classMemberLookup1, classResolver1);
        }

        HashSet hashSet = ZkmUtils.createHashSet(
                ZkmUtils.getPrimeCapacity((list1 != null ? list1.size() : 0) + (listMultimap != null ? listMultimap.getKeyCount() : 0))
        );
        if (list1 != null) {
            hashSet.addAll(list1);
        }

        if (listMultimap != null) {
            hashSet.addAll(listMultimap.keySet());
        }

        Iterator iterator = hashSet.iterator();

        while (iterator.hasNext()) {
            MethodInfo methodInfo1 = (MethodInfo) iterator.next();
            List list2 = null;
            if (listMultimap != null) {
                list2 = listMultimap.getValues(methodInfo1);
            }

            LocalVariableIndex localVariableIndex1 = null;
            if (map1 != null) {
                localVariableIndex1 = (LocalVariableIndex) map1.get(methodInfo1);
            }

            methodInfo1.applyParameterObfuscation(list2, map2, constantPool1, arrayList, localVariableIndex1, classMemberLookup1, classResolver1, bl);
        }

        if (!arrayList.isEmpty()) {
            constantPool1.appendEntries(arrayList);
        }
    }

    public ArrayList buildPackObjectCode(LocalVariableList localVariableList1) {
        ArrayList arrayList = new ArrayList(6);
        arrayList.add(Instruction.createObjectLoad(1, localVariableList1, 10));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createIntLoad(2, localVariableList1, 10));
        arrayList.add(Instruction.createObjectLoad(0, localVariableList1, 10));
        arrayList.add(SimpleInstruction.forOpcode(83));
        arrayList.add(SimpleInstruction.forOpcode(176));
        return arrayList;
    }

    public ResolvedMethodRef createPackLongMethod(
            ProgramClass programClass1,
            List list1,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        LocalVariableList localVariableList1 = new LocalVariableList(true, "(J[Ljava/lang/Object;I)[Ljava/lang/Object;", 4);
        ArrayList arrayList = this.buildPackLongCode(localVariableList1, list1, programClass1, classMemberLookup1, classResolver1);
        ExceptionHandlerSpec[] exceptionHandlerSpecs = new ExceptionHandlerSpec[0];
        MethodInfo methodInfo1 = programClass1.createUniquelyNamedStaticMethod(
                "(J[Ljava/lang/Object;I)[Ljava/lang/Object;",
                arrayList,
                5,
                4,
                1,
                localVariableList1,
                exceptionHandlerSpecs,
                "Method Parameter Obfuscation",
                list1,
                inheritedMemberAnalyzer,
                classMemberLookup1,
                10
        );
        return programClass1.getClassConstantPool().getOrCreateMethodRef(methodInfo1, list1);
    }

    public ArrayList buildPackBooleanCode(
            LocalVariableList localVariableList1, List list1, ProgramClass programClass1, ClassMemberLookup classMemberLookup1, ClassResolver classResolver1
    ) throws ZkmException, IOException {
        ConstantPool constantPool1 = programClass1.getClassConstantPool();
        ArrayList arrayList = new ArrayList(9);
        arrayList.add(Instruction.createObjectLoad(1, localVariableList1, 5));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createIntLoad(2, localVariableList1, 5));
        PrimitiveBoxingGenerator.boxBooleanLocal(
                0, arrayList, programClass1.supportsJava5(), localVariableList1, list1, constantPool1, classMemberLookup1, classResolver1, 10
        );
        arrayList.add(SimpleInstruction.forOpcode(83));
        arrayList.add(SimpleInstruction.forOpcode(176));
        return arrayList;
    }

    public Map createPackHelperMethods(
            ProgramClass programClass1,
            ListMultimap listMultimap,
            Set set1,
            List list1,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        CountingBag countingBag = new CountingBag();
        byte ba;
        if (listMultimap != null) {
            HashSet hashSet = ZkmUtils.createHashSet();
            Iterator iterator = listMultimap.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                hashSet.addAll((Collection) entry.getValue());
            }

            iterator = hashSet.iterator();

            while (iterator.hasNext()) {
                MethodInfo methodInfo1 = (MethodInfo) iterator.next();
                List list2 = methodInfo1.getParameterTypes();
                String string = null;
                Iterator iterator1 = list2.iterator();

                while (iterator1.hasNext()) {
                    String string1 = (String) iterator1.next();
                    if (string1.length() == 1) {
                        switch (string1.charAt(0)) {
                            case 'B':
                            case 'C':
                            case 'I':
                            case 'S':
                                if (string != null && string.equals("II")) {
                                    countingBag.add("III");
                                    countingBag.remove("II");
                                    string = "III";
                                } else {
                                    if (string != null) {
                                        if (string.equals("I")) {
                                            countingBag.add("II");
                                            countingBag.remove("I");
                                            string = "II";
                                            continue;
                                        }

                                        countingBag.add("I");
                                    } else {
                                        countingBag.add("I");
                                    }

                                    string = "I";
                                }
                                break;
                            default:
                                countingBag.add(string1);
                                string = string1;
                        }
                    } else if (string != null && string.equals("OO")) {
                        countingBag.add("OOO");
                        countingBag.remove("OO");
                        string = "OOO";
                    } else {
                        if (string != null) {
                            if (string.equals("O")) {
                                countingBag.add("OO");
                                countingBag.remove("O");
                                string = "OO";
                                continue;
                            }

                            countingBag.add("O");
                        } else {
                            countingBag.add("O");
                        }

                        string = "O";
                    }
                }
            }

            ba = 11;
        } else {
            ba = 11;
        }

        HashMap hashMap = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba));
        Iterator iterator2 = countingBag.iterator();

        while (iterator2.hasNext()) {
            ResolvedMethodRef resolvedMethodRef = null;
            String string2 = (String) iterator2.next();
            if (string2.equals("I")) {
                resolvedMethodRef = this.createPackIntMethod(programClass1, list1, inheritedMemberAnalyzer, classMemberLookup1, classResolver1);
                hashMap.put(string2, resolvedMethodRef);
            } else if (string2.equals("Z")) {
                resolvedMethodRef = this.createPackBooleanMethod(programClass1, list1, inheritedMemberAnalyzer, classMemberLookup1, classResolver1);
                hashMap.put(string2, resolvedMethodRef);
            } else if (string2.equals("J")) {
                resolvedMethodRef = this.createPackLongMethod(programClass1, list1, inheritedMemberAnalyzer, classMemberLookup1, classResolver1);
                hashMap.put(string2, resolvedMethodRef);
            } else if (string2.equals("F")) {
                resolvedMethodRef = this.createPackFloatMethod(programClass1, list1, inheritedMemberAnalyzer, classMemberLookup1, classResolver1);
                hashMap.put(string2, resolvedMethodRef);
            } else if (string2.equals("D")) {
                resolvedMethodRef = this.createPackDoubleMethod(programClass1, list1, inheritedMemberAnalyzer, classMemberLookup1, classResolver1);
                hashMap.put(string2, resolvedMethodRef);
            } else if (string2.equals("II")) {
                resolvedMethodRef = this.createPackTwoIntsMethod(programClass1, list1, inheritedMemberAnalyzer, classMemberLookup1, classResolver1);
                hashMap.put(string2, resolvedMethodRef);
            } else if (string2.equals("III")) {
                resolvedMethodRef = this.createPackThreeIntsMethod(programClass1, list1, inheritedMemberAnalyzer, classMemberLookup1, classResolver1);
                hashMap.put(string2, resolvedMethodRef);
            } else if (string2.equals("O")) {
                resolvedMethodRef = this.createPackObjectMethod(programClass1, list1, inheritedMemberAnalyzer, classMemberLookup1);
                hashMap.put(string2, resolvedMethodRef);
            } else if (string2.equals("OO")) {
                resolvedMethodRef = this.createPackTwoObjectsMethod(programClass1, list1, inheritedMemberAnalyzer, classMemberLookup1);
                hashMap.put(string2, resolvedMethodRef);
            } else if (string2.equals("OOO")) {
                resolvedMethodRef = this.createPackThreeObjectsMethod(programClass1, list1, inheritedMemberAnalyzer, classMemberLookup1);
                hashMap.put(string2, resolvedMethodRef);
            }

            set1.add((MethodInfo) resolvedMethodRef.getResolvedMember());
        }

        return hashMap;
    }

    public ResolvedMethodRef createPackBooleanMethod(
            ProgramClass programClass1,
            List list1,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        LocalVariableList localVariableList1 = new LocalVariableList(true, "(Z[Ljava/lang/Object;I)[Ljava/lang/Object;", 3);
        ArrayList arrayList = this.buildPackBooleanCode(localVariableList1, list1, programClass1, classMemberLookup1, classResolver1);
        ExceptionHandlerSpec[] exceptionHandlerSpecs = new ExceptionHandlerSpec[0];
        MethodInfo methodInfo1 = programClass1.createUniquelyNamedStaticMethod(
                "(Z[Ljava/lang/Object;I)[Ljava/lang/Object;",
                arrayList,
                4,
                3,
                1,
                localVariableList1,
                exceptionHandlerSpecs,
                "Method Parameter Obfuscation",
                list1,
                inheritedMemberAnalyzer,
                classMemberLookup1,
                10
        );
        return programClass1.getClassConstantPool().getOrCreateMethodRef(methodInfo1, list1);
    }

    public ArrayList buildPackTwoIntsCode(
            LocalVariableList localVariableList1, List list1, ProgramClass programClass1, ClassMemberLookup classMemberLookup1, ClassResolver classResolver1
    ) throws ZkmException, IOException {
        ConstantPool constantPool1 = programClass1.getClassConstantPool();
        ArrayList arrayList = new ArrayList(13);
        arrayList.add(Instruction.createObjectLoad(2, localVariableList1, 10));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createIntLoad(3, localVariableList1, 10));
        PrimitiveBoxingGenerator.boxIntLocal(
                0, arrayList, programClass1.supportsJava5(), localVariableList1, list1, constantPool1, classMemberLookup1, classResolver1, 10
        );
        arrayList.add(SimpleInstruction.forOpcode(83));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createIntIncrement(3, 1, localVariableList1, 10));
        arrayList.add(Instruction.createIntLoad(3, localVariableList1, 10));
        PrimitiveBoxingGenerator.boxIntLocal(
                1, arrayList, programClass1.supportsJava5(), localVariableList1, list1, constantPool1, classMemberLookup1, classResolver1, 10
        );
        arrayList.add(SimpleInstruction.forOpcode(83));
        arrayList.add(SimpleInstruction.forOpcode(176));
        return arrayList;
    }

    public ResolvedMethodRef createPackThreeObjectsMethod(
            ProgramClass programClass1, List list1, InheritedMemberAnalyzer inheritedMemberAnalyzer, ClassMemberLookup classMemberLookup1
    ) throws ZkmProcessingException {
        LocalVariableList localVariableList1 = new LocalVariableList(
                true, "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;[Ljava/lang/Object;I)[Ljava/lang/Object;", 5
        );
        ArrayList arrayList = this.buildPackThreeObjectsCode(localVariableList1);
        ExceptionHandlerSpec[] exceptionHandlerSpecs = new ExceptionHandlerSpec[0];
        MethodInfo methodInfo1 = programClass1.createUniquelyNamedStaticMethod(
                "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;[Ljava/lang/Object;I)[Ljava/lang/Object;",
                arrayList,
                4,
                5,
                1,
                localVariableList1,
                exceptionHandlerSpecs,
                "Method Parameter Obfuscation",
                list1,
                inheritedMemberAnalyzer,
                classMemberLookup1,
                10
        );
        return programClass1.getClassConstantPool().getOrCreateMethodRef(methodInfo1, list1);
    }

    public ArrayList buildPackIntCode(
            LocalVariableList localVariableList1, List list1, ProgramClass programClass1, ClassMemberLookup classMemberLookup1, ClassResolver classResolver1
    ) throws ZkmException, IOException {
        ConstantPool constantPool1 = programClass1.getClassConstantPool();
        ArrayList arrayList = new ArrayList(9);
        arrayList.add(Instruction.createObjectLoad(1, localVariableList1, 10));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createIntLoad(2, localVariableList1, 10));
        PrimitiveBoxingGenerator.boxIntLocal(
                0, arrayList, programClass1.supportsJava5(), localVariableList1, list1, constantPool1, classMemberLookup1, classResolver1, 10
        );
        arrayList.add(SimpleInstruction.forOpcode(83));
        arrayList.add(SimpleInstruction.forOpcode(176));
        return arrayList;
    }

    public ResolvedMethodRef createPackObjectMethod(
            ProgramClass programClass1, List list1, InheritedMemberAnalyzer inheritedMemberAnalyzer, ClassMemberLookup classMemberLookup1
    ) throws ZkmProcessingException {
        LocalVariableList localVariableList1 = new LocalVariableList(true, "(Ljava/lang/Object;[Ljava/lang/Object;I)[Ljava/lang/Object;", 3);
        ArrayList arrayList = this.buildPackObjectCode(localVariableList1);
        ExceptionHandlerSpec[] exceptionHandlerSpecs = new ExceptionHandlerSpec[0];
        MethodInfo methodInfo1 = programClass1.createUniquelyNamedStaticMethod(
                "(Ljava/lang/Object;[Ljava/lang/Object;I)[Ljava/lang/Object;",
                arrayList,
                4,
                3,
                1,
                localVariableList1,
                exceptionHandlerSpecs,
                "Method Parameter Obfuscation",
                list1,
                inheritedMemberAnalyzer,
                classMemberLookup1,
                10
        );
        return programClass1.getClassConstantPool().getOrCreateMethodRef(methodInfo1, list1);
    }

    public static boolean usesPackHelperMethods(ProgramClass programClass1) {
        return HiddenOptionFlags.OBFUSCATE_INTERFACE_PARAMETERS && (!programClass1.isInterface() || programClass1.supportsJava8());
    }

    public ArrayList buildPackThreeIntsCode(
            LocalVariableList localVariableList1, List list1, ProgramClass programClass1, ClassMemberLookup classMemberLookup1, ClassResolver classResolver1
    ) throws ZkmException, IOException {
        ConstantPool constantPool1 = programClass1.getClassConstantPool();
        ArrayList arrayList = new ArrayList(17);
        arrayList.add(Instruction.createObjectLoad(3, localVariableList1, 10));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createIntLoad(4, localVariableList1, 10));
        PrimitiveBoxingGenerator.boxIntLocal(
                0, arrayList, programClass1.supportsJava5(), localVariableList1, list1, constantPool1, classMemberLookup1, classResolver1, 10
        );
        arrayList.add(SimpleInstruction.forOpcode(83));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createIntIncrement(4, 1, localVariableList1, 10));
        arrayList.add(Instruction.createIntLoad(4, localVariableList1, 10));
        PrimitiveBoxingGenerator.boxIntLocal(
                1, arrayList, programClass1.supportsJava5(), localVariableList1, list1, constantPool1, classMemberLookup1, classResolver1, 10
        );
        arrayList.add(SimpleInstruction.forOpcode(83));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createIntIncrement(4, 1, localVariableList1, 10));
        arrayList.add(Instruction.createIntLoad(4, localVariableList1, 10));
        PrimitiveBoxingGenerator.boxIntLocal(
                2, arrayList, programClass1.supportsJava5(), localVariableList1, list1, constantPool1, classMemberLookup1, classResolver1, 10
        );
        arrayList.add(SimpleInstruction.forOpcode(83));
        arrayList.add(SimpleInstruction.forOpcode(176));
        return arrayList;
    }

    public void loadOriginalSignatures(ProgramClass[] programClass1) {
        for (ProgramClass programClass2 : programClass1) {
            if (!programClass2.isVersionedVariant()) {
                String string = programClass2.getClassName();

                for (MethodInfo methodInfo1 : programClass2.getMethodInfos()) {
                    MethodSignature methodSignature1 = methodInfo1.getSignature();
                    MethodSignature methodSignature2 = (MethodSignature) ZkmUtils.twoKeyMapOrSelf(string, methodSignature1, this.currentToOriginalSignatures);
                    this.originalSignatures.put(methodInfo1, methodSignature2);
                }
            }
        }
    }

    public void recordSignatureMappings(ProgramClass[] programClass1) {
        this.originalToCurrentSignatures.clear();
        this.currentToOriginalSignatures.clear();

        for (ProgramClass programClass2 : programClass1) {
            if (!programClass2.isVersionedVariant()) {
                String string = programClass2.getClassName();

                for (MethodInfo methodInfo1 : programClass2.getMethodInfos()) {
                    if (!methodInfo1.isStaticInitializer() && methodInfo1.getCreationKind() != 10) {
                        MethodSignature methodSignature1 = (MethodSignature) this.originalSignatures.get(methodInfo1);
                        if (methodSignature1 != null) {
                            MethodSignature methodSignature2 = methodInfo1.getSignature();
                            this.originalToCurrentSignatures.putValue(string, methodSignature1, methodSignature2);
                            this.currentToOriginalSignatures.putValue(string, methodSignature2, methodSignature1);
                        }
                    }
                }
            }
        }
    }
}
