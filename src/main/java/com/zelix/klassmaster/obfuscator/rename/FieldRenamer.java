package com.zelix.klassmaster.obfuscator.rename;

import com.zelix.klassmaster.changelog.ChangeLogMapping;
import com.zelix.klassmaster.classfile.AbstractFieldInfo;
import com.zelix.klassmaster.classfile.AbstractMethodInfo;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.FieldInfo;
import com.zelix.klassmaster.classfile.FieldSignature;
import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchy;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.classfile.hierarchy.ClasspathClassFile;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.obfuscator.exclude.NameExclusionSet;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.BooleanFlag;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.NestedMultiMap;
import com.zelix.klassmaster.util.ObjectPair;
import com.zelix.klassmaster.util.SetMultiMap;
import com.zelix.klassmaster.util.TwoKeyMap;
import com.zelix.klassmaster.util.UniqueWorkQueue;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FieldRenamer {
    public final ClassHierarchy classHierarchy;
    public final ProgramClass[] programClasses;
    public final ClassFileBase[] allClassFiles;
    public final ClassRepository classRepository;
    public final NameExclusionSet exclusionSet;
    public final ChangeLogMapping changeLogMapping;
    public final HashMap classRenameMap;
    public final HashMap fixedFieldNames;
    public final TwoKeyMap oldToNewFieldMap;
    public final TwoKeyMap newToOldFieldMap;
    public final boolean randomizeNames;

    public Map buildChangeLogFieldNames(ChangeLogMapping changeLogMapping1) throws IOException {
        if (changeLogMapping1 == null) {
            return null;
        }

        Enumeration enumeration = changeLogMapping1.getFieldMappedClassNames();
        if (enumeration == null) {
            return null;
        }

        HashMap hashMap = ZkmUtils.createHashMap();

        while (enumeration.hasMoreElements()) {
            String string = (String) enumeration.nextElement();
            ClassFileBase classFileBase = ClassHierarchyNode.findClassFile(string);
            if (classFileBase != null) {
                Iterator iterator = changeLogMapping1.getFieldMappingPairs(string).iterator();

                while (iterator.hasNext()) {
                    ObjectPair objectPair = (ObjectPair) iterator.next();
                    FieldSignature fieldSignature = (FieldSignature) objectPair.getFirst();
                    FieldSignature fieldSignature1 = (FieldSignature) objectPair.getSecond();
                    AbstractFieldInfo abstractFieldInfo = classFileBase.findField(fieldSignature);
                    hashMap.put(abstractFieldInfo, fieldSignature1.getName());
                }
            }
        }

        return hashMap;
    }

    public void addFixedFieldNames(HashMap hashMap, NameExclusionSet nameExclusionSet) {
        Enumeration enumeration = nameExclusionSet.getExcludedFields();

        while (enumeration.hasMoreElements()) {
            FieldInfo fieldInfo = (FieldInfo) enumeration.nextElement();
            hashMap.put(fieldInfo, fieldInfo.getSourceName());
        }
    }

    public FieldRenamer(
            NameExclusionSet nameExclusionSet,
            ChangeLogMapping changeLogMapping1,
            ClassRepository classRepository1,
            ProgramClass[] programClass1,
            ClassFileBase[] classFileBases,
            ClassHierarchy classHierarchy1,
            HashMap hashMap,
            TwoKeyMap twoKeyMap,
            TwoKeyMap twoKeyMap1,
            boolean randomizeNames
    ) {
        this.classHierarchy = classHierarchy1;
        this.programClasses = programClass1;
        this.allClassFiles = classFileBases;
        this.classRepository = classRepository1;
        this.exclusionSet = nameExclusionSet;
        this.changeLogMapping = changeLogMapping1;
        this.classRenameMap = hashMap;
        this.fixedFieldNames = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(programClass1.length * 5));
        this.oldToNewFieldMap = twoKeyMap;
        this.newToOldFieldMap = twoKeyMap1;
        this.randomizeNames = randomizeNames;
    }

    public void handleInheritedField() {
    }

    public void reserveFieldNameUpward(ClassHierarchyNode classHierarchyNode, FieldSignature fieldSignature, SetMultiMap setMultiMap) {
        if (setMultiMap.addValue(classHierarchyNode.getClassFile(), fieldSignature)) {
            this.reserveInSupertypes(classHierarchyNode, fieldSignature, setMultiMap);
        }
    }

    public void reserveFieldNameDownward(ClassHierarchyNode classHierarchyNode, FieldSignature fieldSignature, SetMultiMap setMultiMap) {
        if (setMultiMap.addValue((ProgramClass) classHierarchyNode.getClassFile(), fieldSignature)) {
            List list1 = classHierarchyNode.getSubclassNodes();
            if (list1 != null) {
                Iterator iterator = list1.iterator();

                while (iterator.hasNext()) {
                    ClassHierarchyNode classHierarchyNode1 = (ClassHierarchyNode) iterator.next();
                    this.reserveFieldNameDownward(classHierarchyNode1, fieldSignature, setMultiMap);
                }
            }
        }
    }

    public SetMultiMap buildReservedFieldNames(NameExclusionSet nameExclusionSet, Map map1, Map map2) {
        SetMultiMap setMultiMap = new SetMultiMap(this.programClasses.length, 5);
        if (nameExclusionSet != null) {
            Enumeration enumeration = nameExclusionSet.getExcludedFields();

            while (enumeration.hasMoreElements()) {
                FieldInfo fieldInfo = (FieldInfo) enumeration.nextElement();
                FieldSignature fieldSignature = fieldInfo.getSignature();
                ClassHierarchyNode classHierarchyNode = ClassHierarchyNode.findNode(fieldInfo.getClassName());
                this.reserveInSupertypes(classHierarchyNode, fieldSignature, setMultiMap);
                ClassFileBase classFileBase = classHierarchyNode.getClassFile();
                if (classFileBase != null && classFileBase.isProgramClass()) {
                    Enumeration enumeration1 = ((ProgramClass) classFileBase).enumerateInheritedReferencedFields();
                    if (enumeration1 != null) {
                        while (enumeration1.hasMoreElements()) {
                            AbstractFieldInfo abstractFieldInfo = (AbstractFieldInfo) enumeration1.nextElement();
                            Enumeration enumeration2 = ((ProgramClass) classFileBase).enumerateInheritedFieldReferrers(abstractFieldInfo);

                            while (enumeration2.hasMoreElements()) {
                                ClassFileBase classFileBase1 = (ClassFileBase) enumeration2.nextElement();
                                if (ClassHierarchyNode.findNode(classFileBase1.getClassName()) != null) {
                                    this.handleInheritedField();
                                }
                            }
                        }
                    }
                }
            }
        }

        if (map1 != null) {
            int ba = 0;
            int be = 0;

            for (ProgramClass[] programClass4 = this.programClasses; be < programClass4.length; programClass4 = this.programClasses) {
                ProgramClass programClass1 = this.programClasses[ba];
                FieldInfo[] fieldInfos = programClass1.getFieldInfos();

                for (int i = 0; i < fieldInfos.length; i++) {
                    FieldInfo fieldInfo2 = fieldInfos[i];
                    String string1 = (String) map1.get(fieldInfo2);
                    if (string1 != null) {
                        FieldSignature fieldSignature1 = new FieldSignature(string1, fieldInfo2.getDescriptor());
                        ClassHierarchyNode classHierarchyNode1 = ClassHierarchyNode.findNode(programClass1.getClassName());
                        this.reserveInSupertypes(classHierarchyNode1, fieldSignature1, setMultiMap);
                    }
                }

                Enumeration enumeration4 = programClass1.enumerateInheritedReferencedFields();
                if (enumeration4 != null) {
                    while (enumeration4.hasMoreElements()) {
                        AbstractFieldInfo abstractFieldInfo2 = (AbstractFieldInfo) enumeration4.nextElement();
                        String string2 = (String) map1.get(abstractFieldInfo2);
                        if (string2 != null) {
                            new FieldSignature(string2, abstractFieldInfo2.getDescriptor());
                            Enumeration enumeration6 = programClass1.enumerateInheritedFieldReferrers(abstractFieldInfo2);

                            while (enumeration6.hasMoreElements()) {
                                ClassFileBase classFileBase3 = (ClassFileBase) enumeration6.nextElement();
                                ClassHierarchyNode.findNode(classFileBase3.getClassName());
                                this.handleInheritedField();
                            }
                        }
                    }
                }

                be = ++ba;
            }
        }

        Iterator iterator = nameExclusionSet.getLinkedFields().iterator();

        while (iterator.hasNext()) {
            FieldInfo fieldInfo1 = (FieldInfo) iterator.next();
            if (!nameExclusionSet.isFieldExcluded(fieldInfo1) && (map1 == null || !map1.containsKey(fieldInfo1))) {
                Set set1 = nameExclusionSet.getLinkedMethodsOfField(fieldInfo1);
                AbstractMethodInfo abstractMethodInfo = set1 != null && set1.size() > 0 ? (AbstractMethodInfo) set1.iterator().next() : null;
                if (abstractMethodInfo != null) {
                    String string = nameExclusionSet.getLinkPrefix(abstractMethodInfo);
                    String string3 = MethodSignature.toPropertyName(abstractMethodInfo.getSourceName(), string);
                    FieldSignature fieldSignature2 = new FieldSignature(string3, fieldInfo1.getDescriptor());
                    setMultiMap.addValue(fieldInfo1.getProgramClass(), fieldSignature2);
                    ClassHierarchyNode classHierarchyNode2 = ClassHierarchyNode.findNode(fieldInfo1.getClassName());
                    this.reserveInSupertypes(classHierarchyNode2, fieldSignature2, setMultiMap);
                    map2.put(fieldInfo1, string3);
                }
            }
        }

        if (!HiddenOptionFlags.SKIP_INHERITED_FIELD_NAMES) {
            int bb = 0;
            int bd = bb;

            for (ProgramClass[] programClass3 = this.programClasses; bd < programClass3.length; programClass3 = this.programClasses) {
                ProgramClass programClass2 = this.programClasses[bb];
                Enumeration enumeration3 = programClass2.enumerateInheritedReferencedFields();
                if (enumeration3 != null) {
                    while (enumeration3.hasMoreElements()) {
                        AbstractFieldInfo abstractFieldInfo1 = (AbstractFieldInfo) enumeration3.nextElement();
                        if (ClassHierarchyNode.isUnknownClass(abstractFieldInfo1.getClassName())) {
                            abstractFieldInfo1.getSignature();
                            Enumeration enumeration5 = programClass2.enumerateInheritedFieldReferrers(abstractFieldInfo1);

                            while (enumeration5.hasMoreElements()) {
                                ClassFileBase classFileBase2 = (ClassFileBase) enumeration5.nextElement();
                                ClassHierarchyNode.findNode(classFileBase2.getClassName());
                                this.handleInheritedField();
                            }
                        }
                    }
                }

                bd = ++bb;
            }
        }

        return setMultiMap;
    }

    public boolean checkChangeLogFieldClashes() throws IOException {
        boolean bl = true;
        if (this.changeLogMapping == null) {
            return true;
        }

        Enumeration enumeration = this.changeLogMapping.getFieldMappedClassNames();
        if (enumeration == null) {
            return true;
        }

        while (enumeration.hasMoreElements()) {
            String string = (String) enumeration.nextElement();
            ProgramClass programClass1 = ClassHierarchyNode.findProgramClass(string);
            if (programClass1 != null) {
                Enumeration enumeration1 = this.changeLogMapping.getRenamedFields(string);
                if (enumeration1 != null) {
                    while (enumeration1.hasMoreElements()) {
                        FieldSignature fieldSignature = (FieldSignature) enumeration1.nextElement();
                        if (this.classRepository.containsField(fieldSignature, programClass1)) {
                            FieldInfo fieldInfo = programClass1.findFieldBySignature(fieldSignature);
                            if (this.exclusionSet.isFieldExcluded(fieldInfo)) {
                                String string1 = this.changeLogMapping.getOriginalClassName(string);
                                string1 = ZkmUtils.slashesToDots(string1);
                                this.changeLogMapping
                                        .reportError(
                                                "\""
                                                        + fieldSignature.formatDeclaration(this.classRenameMap)
                                                        + "\" is specified as a new field name in class \""
                                                        + string1
                                                        + "\" but there is an existing field of that name which will not be renamed."
                                        );
                                bl = false;
                            }
                        }
                    }
                }
            }
        }

        return bl;
    }

    public void reserveInSupertypes(ClassHierarchyNode classHierarchyNode, FieldSignature fieldSignature, SetMultiMap setMultiMap) {
        Enumeration enumeration = classHierarchyNode.enumerateInterfaces();
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                ClassHierarchyNode classHierarchyNode1 = (ClassHierarchyNode) enumeration.nextElement();
                if (classHierarchyNode1.isProgramClass()) {
                    this.reserveFieldNameUpward(classHierarchyNode1, fieldSignature, setMultiMap);
                }
            }
        }

        ClassHierarchyNode classHierarchyNode2 = classHierarchyNode.getSuperclassNode();
        if (classHierarchyNode2.isProgramClass()) {
            this.reserveFieldNameUpward(classHierarchyNode2, fieldSignature, setMultiMap);
        }
    }

    public void renameFields(FieldNameAssigner fieldNameAssigner, ScriptEnvironment scriptEnvironment1) throws ZkmException, IOException {
        if (this.checkChangeLogFieldClashes()) {
            Map map1 = this.buildChangeLogFieldNames(this.changeLogMapping);
            HashMap hashMap = ZkmUtils.createHashMap();
            SetMultiMap setMultiMap = this.buildReservedFieldNames(this.exclusionSet, map1, hashMap);
            this.addFixedFieldNames(this.fixedFieldNames, this.exclusionSet);
            List list1 = this.classHierarchy.getRootProgramInterfaces();
            List list2 = this.classHierarchy.getTopProgramNodes();
            if (!HiddenOptionFlags.SKIP_INTERFACE_FIELD_NAMES) {
                Iterator iterator = list2.iterator();

                while (iterator.hasNext()) {
                    ClassHierarchyNode classHierarchyNode = (ClassHierarchyNode) iterator.next();
                    if (!classHierarchyNode.isInterface()) {
                        ProgramClass programClass1 = classHierarchyNode.getProgramClass();
                        List list3 = this.classRepository.getSuperclasses(programClass1.getClassName());
                        Iterator iterator1 = list3.iterator();

                        while (iterator1.hasNext()) {
                            ClassFileBase classFileBase = (ClassFileBase) iterator1.next();
                            AbstractFieldInfo[] abstractFieldInfos = classFileBase.getFields();
                            AbstractFieldInfo[] abstractFieldInfos1 = abstractFieldInfos;
                            int ba = abstractFieldInfos1.length;

                            for (int i = 0; i < ba; i += 1) {
                                AbstractFieldInfo abstractFieldInfo = abstractFieldInfos1[i];
                                if ((!abstractFieldInfo.isFinal() || !abstractFieldInfo.isStatic())
                                        && (
                                        abstractFieldInfo.isPublic()
                                                || abstractFieldInfo.isProtected()
                                                || !abstractFieldInfo.isStrictlyPrivate() && programClass1.getPackagePath().equals(classFileBase.getPackagePath())
                                )) {
                                    this.reserveFieldNameDownward(classHierarchyNode, abstractFieldInfo.getSignature(), setMultiMap);
                                }
                            }
                        }
                    }
                }
            }

            UniqueWorkQueue uniqueWorkQueue = new UniqueWorkQueue(Math.max(this.programClasses.length, 5));

            for (int i = 0; i < list1.size(); i++) {
                uniqueWorkQueue.enqueue(list1.get(i));
            }

            label243:
            for (int i = 0; i < list2.size(); i++) {
                ClassHierarchyNode classHierarchyNode3 = (ClassHierarchyNode) list2.get(i);
                if (!classHierarchyNode3.isInterface()) {
                    Enumeration enumeration1 = classHierarchyNode3.enumerateInterfaces();
                    if (enumeration1 != null) {
                        while (enumeration1.hasMoreElements()) {
                            ClassHierarchyNode classHierarchyNode4 = (ClassHierarchyNode) enumeration1.nextElement();
                            if (classHierarchyNode4.isProgramClass()) {
                                continue label243;
                            }
                        }
                    }

                    uniqueWorkQueue.enqueue(classHierarchyNode3);
                }
            }

            TwoKeyMap twoKeyMap = new TwoKeyMap();
            TwoKeyMap twoKeyMap1 = new TwoKeyMap(this.programClasses.length, 20);
            NestedMultiMap nestedMultiMap = new NestedMultiMap(this.programClasses.length);
            boolean bl = true;
            boolean bl1 = true;

            while (bl && !uniqueWorkQueue.isEmpty()) {
                bl = false;
                int be = uniqueWorkQueue.size();

                label222:
                for (int i = 0; i < be; i += 1) {
                    ClassHierarchyNode classHierarchyNode6 = (ClassHierarchyNode) uniqueWorkQueue.dequeue();
                    ProgramClass programClass2 = classHierarchyNode6.getProgramClass();
                    if (bl1 && programClass2.isInterface() && programClass2.getFieldCount() > 0) {
                        Enumeration enumeration3 = programClass2.enumerateInheritedReferencedFields();
                        if (enumeration3 != null) {
                            while (enumeration3.hasMoreElements()) {
                                AbstractFieldInfo abstractFieldInfo1 = (AbstractFieldInfo) enumeration3.nextElement();
                                if (!this.fixedFieldNames.containsKey(abstractFieldInfo1) && !ClassHierarchyNode.isUnknownClass(abstractFieldInfo1.getClassName())) {
                                    ClassFileBase classFileBase1 = abstractFieldInfo1.getOwningClass();
                                    if (!classFileBase1.isInterface()
                                            || !this.classRepository.implementsInterface(classFileBase1.getClassName(), programClass2.getClassName())) {
                                        uniqueWorkQueue.enqueue(classHierarchyNode6);
                                        continue label222;
                                    }
                                }
                            }
                        }
                    }

                    ArrayList arrayList1 = new ArrayList();
                    boolean bl2 = true;
                    ClassHierarchyNode classHierarchyNode7 = classHierarchyNode6.getSuperclassNode();
                    if (classHierarchyNode7.isProgramClass() && !twoKeyMap1.containsKey(classHierarchyNode7)) {
                        bl2 = false;
                    } else {
                        if (classHierarchyNode7.isProgramClass()) {
                            arrayList1.add(classHierarchyNode7);
                        }

                        Enumeration enumeration = classHierarchyNode6.enumerateInterfaces();
                        if (enumeration != null) {
                            while (enumeration.hasMoreElements()) {
                                ClassHierarchyNode classHierarchyNode1 = (ClassHierarchyNode) enumeration.nextElement();
                                if (classHierarchyNode1.isProgramClass()) {
                                    arrayList1.add(classHierarchyNode1);
                                    if (!twoKeyMap1.containsKey(classHierarchyNode1)) {
                                        bl2 = false;
                                    }
                                }
                            }
                        }
                    }

                    if (!bl2) {
                        uniqueWorkQueue.enqueue(classHierarchyNode6);
                    } else {
                        bl = true;
                        HashMap hashMap1 = ZkmUtils.createHashMap();
                        ListMultimap listMultimap1 = new ListMultimap();
                        Iterator iterator2 = arrayList1.iterator();

                        while (iterator2.hasNext()) {
                            ClassHierarchyNode classHierarchyNode2 = (ClassHierarchyNode) iterator2.next();
                            Map map2 = twoKeyMap1.getInnerMap(classHierarchyNode2);
                            if (map2 != null) {
                                hashMap1.putAll(map2);
                            }

                            ListMultimap listMultimap = nestedMultiMap.getMultimap(classHierarchyNode2);
                            if (listMultimap != null) {
                                listMultimap1.addAllFrom(listMultimap);
                            }
                        }

                        Enumeration enumeration4 = classHierarchyNode6.enumerateSubclasses();
                        if (enumeration4 != null) {
                            while (enumeration4.hasMoreElements()) {
                                ClassHierarchyNode classHierarchyNode8 = (ClassHierarchyNode) enumeration4.nextElement();
                                if (classHierarchyNode8.isProgramClass()) {
                                    uniqueWorkQueue.enqueue(classHierarchyNode8);
                                } else {
                                    ArrayList arrayList2 = new ArrayList();
                                    classHierarchyNode8.collectProgramSubclasses(arrayList2);
                                    uniqueWorkQueue.enqueueAll(arrayList2);
                                }
                            }
                        }

                        Enumeration enumeration5 = classHierarchyNode6.enumerateImplementors();
                        if (enumeration5 != null) {
                            while (enumeration5.hasMoreElements()) {
                                ClassHierarchyNode classHierarchyNode9 = (ClassHierarchyNode) enumeration5.nextElement();
                                if (classHierarchyNode9.isProgramClass()) {
                                    uniqueWorkQueue.enqueue(classHierarchyNode9);
                                } else {
                                    ArrayList arrayList3 = new ArrayList();
                                    classHierarchyNode9.collectProgramImplementors(arrayList3);
                                    uniqueWorkQueue.enqueueAll(arrayList3);
                                }
                            }
                        }

                        BooleanFlag booleanFlag = new BooleanFlag(false);
                        programClass2.renameFields(
                                fieldNameAssigner,
                                this.oldToNewFieldMap,
                                this.newToOldFieldMap,
                                hashMap1,
                                new ListMultimap(listMultimap1),
                                listMultimap1,
                                this.exclusionSet,
                                this.changeLogMapping,
                                this.classRenameMap,
                                map1,
                                this.fixedFieldNames,
                                setMultiMap,
                                twoKeyMap,
                                hashMap,
                                booleanFlag,
                                this.classRepository,
                                this.randomizeNames
                        );
                        if (!bl1 && booleanFlag.getValue()) {
                            bl1 = true;
                        }

                        twoKeyMap1.putInnerMap(classHierarchyNode6, hashMap1);
                        nestedMultiMap.putMultimap(classHierarchyNode6, listMultimap1);
                    }
                }

                if (!bl && !uniqueWorkQueue.isEmpty() && bl1) {
                    bl1 = false;
                    bl = true;
                }
            }

            if (!uniqueWorkQueue.isEmpty()) {
                StringBuffer stringBuffer = new StringBuffer();

                while (!uniqueWorkQueue.isEmpty()) {
                    ClassHierarchyNode classHierarchyNode5 = (ClassHierarchyNode) uniqueWorkQueue.dequeue();
                    stringBuffer.append(classHierarchyNode5.getClassName());
                    if (!uniqueWorkQueue.isEmpty()) {
                        stringBuffer.append(", ");
                    }
                }

                throw new ZkmProcessingException("Cyclic dependency within : " + stringBuffer);
            }

            if (this.changeLogMapping != null) {
                Enumeration enumeration2 = this.changeLogMapping.getFieldMappedClassNames();

                while (enumeration2.hasMoreElements()) {
                    String string = (String) enumeration2.nextElement();
                    if (!ClassHierarchyNode.isProgramClassName(string)) {
                        ArrayList arrayList = this.changeLogMapping.getFieldMappingPairs(string);
                        if (arrayList != null) {
                            for (int i = 0; i < arrayList.size(); i += 1) {
                                ObjectPair objectPair = (ObjectPair) arrayList.get(i);
                                FieldSignature fieldSignature = (FieldSignature) objectPair.getFirst();
                                FieldSignature fieldSignature1 = (FieldSignature) objectPair.getSecond();
                                this.oldToNewFieldMap.putValue(string, fieldSignature, fieldSignature1);
                                this.newToOldFieldMap.putValue(string, fieldSignature1, fieldSignature);
                            }

                            ClasspathClassFile classpathClassFile = (ClasspathClassFile) ClassHierarchyNode.findClassFile(string);
                            classpathClassFile.applyFieldNameMapping(this.oldToNewFieldMap);
                        }
                    }
                }
            }

            this.classRepository.applyFieldRenamesToVersions(this.allClassFiles, this.oldToNewFieldMap);
            this.classRepository.refreshClassReferences(this.allClassFiles, scriptEnvironment1);
            this.classRepository.indexFields();
        }
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
    }
}
