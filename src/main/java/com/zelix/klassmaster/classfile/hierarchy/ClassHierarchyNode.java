package com.zelix.klassmaster.classfile.hierarchy;

import com.zelix.klassmaster.changelog.ChangeLogMapping;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ClassFileLoadException;
import com.zelix.klassmaster.exceptions.MethodAnalysisException;
import com.zelix.klassmaster.exceptions.MissingClassException;
import com.zelix.klassmaster.exceptions.ZkmClassNotFoundException;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.obfuscator.constants.IntegerConstantEncryptor;
import com.zelix.klassmaster.obfuscator.constants.IntegerEncryptionExclusions;
import com.zelix.klassmaster.obfuscator.constants.LongConstantEncryptor;
import com.zelix.klassmaster.obfuscator.constants.LongEncryptionExclusionHandler;
import com.zelix.klassmaster.obfuscator.exclude.NameExclusionSet;
import com.zelix.klassmaster.obfuscator.parameters.MethodParameterChangeSet;
import com.zelix.klassmaster.obfuscator.parameters.MethodParameterChanger;
import com.zelix.klassmaster.obfuscator.parameters.ParameterListGenerator;
import com.zelix.klassmaster.obfuscator.rename.ClassNameGenerator;
import com.zelix.klassmaster.obfuscator.rename.MethodNameGeneratorBase;
import com.zelix.klassmaster.obfuscator.rename.MethodRenamer;
import com.zelix.klassmaster.obfuscator.string.StringEncryptionExclusionSpec;
import com.zelix.klassmaster.obfuscator.string.StringEncryptor;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.CountingBag;
import com.zelix.klassmaster.util.DisableableMap;
import com.zelix.klassmaster.util.EmptyEnumeration;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.MultiMapTable;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.NodeVisitor;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ObservableModel;
import com.zelix.klassmaster.util.PairMultiMap;
import com.zelix.klassmaster.util.SetMultiMap;
import com.zelix.klassmaster.util.ZkmAssert;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ClassHierarchyNode implements NodeVisitor, Comparable {
    public static boolean checkHierarchyGaps = false;
    private static Map nodesByName = HiddenOptionFlags.USE_PARALLEL ? new ConcurrentHashMap() : ZkmUtils.createHashMap();
    public ClassHierarchyNode outerClassNode;
    public ClassHierarchyNode enclosingNode;
    public int depth;
    private ClassHierarchyNode superclassNode;
    public List subclassNodes = null;
    private List interfaceNodes = null;
    public List implementorNodes = null;
    public List enclosedClassNodes = null;
    private String className;
    private ClassFileBase classFile;

    public final void analyzeSubclassOverrides(
            MethodOverrideAnalyzer methodOverrideAnalyzer,
            ClassResolver classResolver1,
            ObservableHolder observableHolder,
            ScriptEnvironment scriptEnvironment1,
            Random random1
    ) throws ZkmException, IOException {
        if (this.subclassNodes != null) {
            int ba = this.subclassNodes.size();

            for (int i = 0; i < ba; i++) {
                ClassHierarchyNode classHierarchyNode1 = (ClassHierarchyNode) this.subclassNodes.get(i);
                ClassFileBase classFileBase = classHierarchyNode1.getClassFile();
                if (classFileBase == null) {
                    String string = "analyzing subclasses of '"
                            + this.getJavaClassName()
                            + "'"
                            + (this.classFile != null ? " : '" + this.classFile.getLocationName() + "'" : "");

                    try {
                        classFileBase = classResolver1.getClassFile(classHierarchyNode1.getClassName(), string);
                    } catch (ZkmClassNotFoundException zkmClassNotFoundException) {
                        throw new MissingClassException(
                                "Class '" + ZkmUtils.slashesToDots(zkmClassNotFoundException.getClassName()) + "' not found while " + string + ". (3)"
                        );
                    } catch (ClassFileLoadException classFileLoadException) {
                        throw new MissingClassException(classFileLoadException.getMessage());
                    }
                }

                classFileBase.computeInheritedMethods(methodOverrideAnalyzer, observableHolder, scriptEnvironment1, random1);
                classHierarchyNode1.analyzeSubclassOverrides(methodOverrideAnalyzer, classResolver1, observableHolder, scriptEnvironment1, random1);
            }
        }
    }

    public final ProgramClass getProgramClass() {
        return this.classFile != null && this.classFile.isProgramClass() ? (ProgramClass) this.classFile : null;
    }

    public Enumeration enumerateInterfaces() {
        return this.interfaceNodes != null ? Collections.enumeration(this.interfaceNodes) : null;
    }

    public final void generateClassNames(
            ClassNameGenerator classNameGenerator, ChangeLogMapping changeLogMapping1, HashMap hashMap, HashMap hashMap1, Set set1, Set set2
    ) throws ZkmProcessingException, IOException {
        if (this.classFile.isProgramClass() && !set1.contains(this.classFile)) {
            if (changeLogMapping1 != null && changeLogMapping1.hasClassMapping(this.className)) {
                String string3 = changeLogMapping1.getNewClassName(this.className);
                String string5 = classNameGenerator.getRenamedPackagePrefix(this.className);
                String string6;
                if (string3 != null) {
                    string6 = ClassNameGenerator.getSimpleName(string3);
                } else {
                    string6 = ClassNameGenerator.getSimpleName(this.className);
                }

                String string7 = string5 + string6;
                hashMap.put(this.className, string7);
                Object object1 = hashMap1.put(string7, this.className);
                ZkmAssert.assertNull(object1, "Duplicate class '" + this.className + "' '" + string7 + "' '" + object1 + "' (C)");
            } else {
                HashMap hashMap2 = null;
                if (classNameGenerator.getKeepInnerClassInfoMode() != 1 && this.outerClassNode != null) {
                    hashMap2 = ZkmUtils.createHashMap(13);
                    Enumeration enumeration = this.enumerateOuterClasses();

                    while (enumeration.hasMoreElements()) {
                        ClassHierarchyNode classHierarchyNode1 = (ClassHierarchyNode) enumeration.nextElement();
                        String string = classHierarchyNode1.getClassName();
                        String string1 = ClassFileBase.stripPackage((String) ZkmUtils.mapOrSelf(string, hashMap));
                        String string2 = ClassFileBase.stripOuterClassPrefix(string1);
                        hashMap2.put(classHierarchyNode1, string2);
                    }
                }

                String string4 = classNameGenerator.createNewClassName(this, hashMap2);
                if (string4 != null) {
                    hashMap.put(this.className, string4);
                    Object object = hashMap1.put(string4, this.className);
                    ZkmAssert.assertNull(object, "Duplicate class '" + this.className + "' '" + string4 + "' '" + object + "' (D)");
                } else {
                    set2.add(this);
                }
            }
        }

        if (this.subclassNodes != null) {
            int ba = this.subclassNodes.size();

            for (int i = 0; i < ba; i++) {
                ClassHierarchyNode classHierarchyNode2 = (ClassHierarchyNode) this.subclassNodes.get(i);
                classHierarchyNode2.generateClassNames(classNameGenerator, changeLogMapping1, hashMap, hashMap1, set1, set2);
            }
        }
    }

    public final boolean hasImplementors() {
        return this.implementorNodes != null && this.implementorNodes.size() > 0;
    }

    public List getProgramSuperclasses() {
        ArrayList arrayList = new ArrayList();
        this.collectSuperclasses(arrayList);
        ArrayList arrayList1 = new ArrayList(arrayList.size());
        Iterator iterator = arrayList.iterator();

        while (iterator.hasNext()) {
            ClassHierarchyNode classHierarchyNode1 = (ClassHierarchyNode) iterator.next();
            if (classHierarchyNode1.isProgramClass()) {
                arrayList1.add(classHierarchyNode1.getProgramClass());
            }
        }

        return arrayList1;
    }

    public final void clearEnclosingLinks() {
        if (this.enclosingNode != null && this.enclosingNode.enclosedClassNodes != null) {
            this.enclosingNode.enclosedClassNodes.remove(this);
            if (this.enclosingNode.enclosedClassNodes.size() == 0) {
                this.enclosingNode.enclosedClassNodes = null;
            }
        }

        this.enclosingNode = null;
        this.outerClassNode = null;
        if (this.enclosedClassNodes != null) {
            int ba = 0;
            int bb = 0;

            for (List list1 = this.enclosedClassNodes; bb < list1.size(); list1 = this.enclosedClassNodes) {
                ClassHierarchyNode classHierarchyNode1 = (ClassHierarchyNode) this.enclosedClassNodes.get(ba);
                classHierarchyNode1.enclosingNode = null;
                if (classHierarchyNode1.outerClassNode == this) {
                    classHierarchyNode1.outerClassNode = null;
                }

                bb = ++ba;
            }
        }

        this.enclosedClassNodes = null;
    }

    public static Enumeration enumerateAllNodes() {
        return Collections.enumeration(nodesByName.values());
    }

    public static final boolean isUnknownClass(String string) {
        ClassHierarchyNode classHierarchyNode = (ClassHierarchyNode) nodesByName.get(string);
        return classHierarchyNode == null ? true : classHierarchyNode.hasNoClassFile();
    }

    public final String getClassName() {
        return this.className;
    }

    public static void clearAllEnclosingLinks() {
        Iterator iterator = nodesByName.values().iterator();

        while (iterator.hasNext()) {
            ClassHierarchyNode classHierarchyNode = (ClassHierarchyNode) iterator.next();
            classHierarchyNode.enclosingNode = null;
            classHierarchyNode.outerClassNode = null;
            classHierarchyNode.enclosedClassNodes = null;
        }
    }

    public void dispose() {
        this.superclassNode = null;
        if (this.classFile != null) {
            this.classFile.removeObserver(this);
            this.classFile = null;
            this.subclassNodes = null;
        } else {
            this.subclassNodes = null;
        }

        this.interfaceNodes = null;
        this.implementorNodes = null;
        this.enclosingNode = null;
        this.outerClassNode = null;
        this.enclosedClassNodes = null;
    }

    public final void setEnclosingNode(ClassHierarchyNode classHierarchyNode1) {
        this.enclosingNode = classHierarchyNode1;
    }

    public final boolean hasSubclasses() {
        return this.subclassNodes != null && this.subclassNodes.size() > 0;
    }

    public static boolean isProgramClassName(String string) {
        ClassHierarchyNode classHierarchyNode = findNode(string);
        return classHierarchyNode != null && classHierarchyNode.isProgramClass();
    }

    public final String getOriginalClassName() {
        return this.classFile != null ? this.classFile.getOriginalClassName() : this.className;
    }

    public final void registerClassName(ClassNameGenerator classNameGenerator, HashMap hashMap, HashMap hashMap1, Set set1, Set set2, Set set3) {
        String string = classNameGenerator.getPackageOnlyRename(this);
        if (string == null) {
            string = this.className;
        }

        hashMap.put(this.className, string);
        String string1 = ((java.lang.String) (hashMap1.put(string, this.className)));
        ZkmAssert.assertNull(string1, "Duplicate new class name. Both '" + this.className + "' and '" + string1 + "' mapped to '" + string + "'");
        set1.add(string);
        if (!this.getClassFile().isFromArchive()) {
            set2.add(string.toLowerCase());
        }

        set3.add(string.toLowerCase());
    }

    public final boolean tryGenerateClassName(ClassNameGenerator classNameGenerator, HashMap hashMap, HashMap hashMap1, Iterator iterator) throws ZkmProcessingException, IOException {
        HashMap hashMap2 = null;
        if (classNameGenerator.getKeepInnerClassInfoMode() != 1 && this.outerClassNode != null) {
            hashMap2 = ZkmUtils.createHashMap(13);
            Enumeration enumeration = this.enumerateOuterClasses();

            while (enumeration.hasMoreElements()) {
                ClassHierarchyNode classHierarchyNode1 = (ClassHierarchyNode) enumeration.nextElement();
                String string = classHierarchyNode1.getClassName();
                String string1 = ClassFileBase.stripOuterClassPrefix(ClassFileBase.stripPackage((String) ZkmUtils.mapOrSelf(string, hashMap)));
                hashMap2.put(classHierarchyNode1, string1);
            }
        }

        String string2 = classNameGenerator.createNewClassName(this, hashMap2);
        if (string2 != null) {
            hashMap.put(this.className, string2);
            Object object = hashMap1.put(string2, this.className);
            ZkmAssert.assertNull(object, "Duplicate class '" + this.className + "' '" + string2 + "' '" + object + "' (A)");
            iterator.remove();
            return true;
        } else {
            return false;
        }
    }

    public final void assignGeneratedClassName(ClassNameGenerator classNameGenerator, HashMap hashMap, HashMap hashMap1) throws IOException {
        String string = classNameGenerator.generateFallbackName(this, this.classFile != null ? this.classFile.isFromArchive() : false);
        hashMap.put(this.className, string);
        Object object = hashMap1.put(string, this.className);
        ZkmAssert.assertNull(object, "Duplicate class '" + this.className + "' '" + string + "' '" + object + "' (B)");
    }

    public final void setDepth(int depth) {
        this.depth = depth;
    }

    public static void setCheckHierarchyGaps(boolean bl) {
        checkHierarchyGaps = bl;
    }

    public final ClassHierarchyNode getSuperclassNode() {
        return this.superclassNode;
    }

    public final void renameEnclosedClassReferences(HashMap hashMap, HashMap hashMap1) throws ZkmException, IOException {
        if (this.enclosedClassNodes != null) {
            int ba = 0;
            int bb = 0;

            for (List list1 = this.enclosedClassNodes; bb < list1.size(); list1 = this.enclosedClassNodes) {
                ClassHierarchyNode classHierarchyNode1 = (ClassHierarchyNode) this.enclosedClassNodes.get(ba);
                ClassFileBase classFileBase = ((ClassHierarchyNode) this.enclosedClassNodes.get(ba)).classFile;
                if (classFileBase.isProgramClass()) {
                    ProgramClass programClass1 = (ProgramClass) classFileBase;
                    ClassHierarchyNode classHierarchyNode2 = classHierarchyNode1.getOuterClassNode();
                    if (classHierarchyNode2 != null) {
                        String string = classHierarchyNode2.getClassName();
                        programClass1.renamePackagePrefix(string, (String) hashMap.get(string), hashMap, hashMap1);
                    }
                }

                bb = ++ba;
            }
        }
    }

    public final void addEnclosedClass(Object object) {
        if (this.enclosedClassNodes == null) {
            this.enclosedClassNodes = new ArrayList(2);
        }

        this.enclosedClassNodes.add(object);
    }

    public final void setOuterClassNode(ClassHierarchyNode classHierarchyNode1) {
        this.outerClassNode = classHierarchyNode1;
    }

    public final void collectProgramImplementors(ArrayList arrayList) {
        if (this.implementorNodes != null) {
            int ba = 0;
            int bb = 0;

            for (List list1 = this.implementorNodes; bb < list1.size(); list1 = this.implementorNodes) {
                ClassHierarchyNode classHierarchyNode1 = (ClassHierarchyNode) this.implementorNodes.get(ba);
                if (classHierarchyNode1.isProgramClass()) {
                    arrayList.add(classHierarchyNode1);
                } else {
                    classHierarchyNode1.collectProgramImplementors(arrayList);
                }

                bb = ++ba;
            }
        }
    }

    private ClassHierarchyNode(String string, ClassFileBase classFileBase) {
        this.className = string;
        this.classFile = classFileBase;
        if (this.classFile != null) {
            this.classFile.addObserver(this);
        }
    }

    public final String getJavaClassName() {
        return this.className.replace('/', '.');
    }

    public final ClassHierarchyNode getOuterClassNode() {
        return this.outerClassNode;
    }

    public final void collectAllSubtypes(Map map1) {
        if (this.subclassNodes != null) {
            int ba = 0;
            int bd = 0;

            for (List list2 = this.subclassNodes; bd < list2.size(); list2 = this.subclassNodes) {
                ClassHierarchyNode classHierarchyNode1 = (ClassHierarchyNode) this.subclassNodes.get(ba);
                map1.put(classHierarchyNode1, classHierarchyNode1);
                classHierarchyNode1.collectAllSubtypes(map1);
                bd = ++ba;
            }
        }

        if (this.implementorNodes != null) {
            int bb = 0;
            int bc = 0;

            for (List list1 = this.implementorNodes; bc < list1.size(); list1 = this.implementorNodes) {
                ClassHierarchyNode classHierarchyNode2 = (ClassHierarchyNode) this.implementorNodes.get(bb);
                map1.put(classHierarchyNode2, classHierarchyNode2);
                classHierarchyNode2.collectAllSubtypes(map1);
                bc = ++bb;
            }
        }
    }

    public static ClassHierarchyNode findNode(String string) {
        return (ClassHierarchyNode) nodesByName.get(string);
    }

    public final Enumeration enumerateOuterClasses() {
        if (this.outerClassNode == null) {
            return new EmptyEnumeration();
        }

        HashSet hashSet = ZkmUtils.createHashSet(13);
        ArrayList arrayList = new ArrayList();
        ClassHierarchyNode classHierarchyNode1 = this.outerClassNode;

        do {
            arrayList.add(classHierarchyNode1);
            if (!hashSet.add(classHierarchyNode1)) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append('[');

                for (int i = 0; i < arrayList.size(); i++) {
                    ClassHierarchyNode classHierarchyNode2 = (ClassHierarchyNode) arrayList.get(i);
                    stringBuilder.append(
                            classHierarchyNode2.getClassName() + " " + classHierarchyNode2.getClassFile().getLocationName() + " " + classHierarchyNode1.hasNoClassFile()
                    );
                    if (i < arrayList.size() - 1) {
                        stringBuilder.append(", ");
                    }
                }

                stringBuilder.append(']');
                ZkmAssert.assertTrue(false, new String[]{"Loop : " + stringBuilder.toString()});
            }

            classHierarchyNode1 = classHierarchyNode1.getOuterClassNode();
        } while (classHierarchyNode1 != null);

        return Collections.enumeration(arrayList);
    }

    public final boolean hasEnclosingNode() {
        return this.enclosingNode != null;
    }

    public static String getHierarchyGapMessage(ClassFileBase classFileBase, ClassFileBase classFileBase1, int ba) {
        if (!checkHierarchyGaps) {
            return null;
        }

        if (isUnknownClass(classFileBase.getClassName()) && !isUnknownClass(classFileBase1.getClassName())) {
            String string = null;
            switch (ba) {
                case 1:
                    string = "extended";
                    break;
                case 2:
                    string = "implemented";
            }

            return "Class '"
                    + classFileBase1.getDottedClassName()
                    + "' in file '"
                    + classFileBase1.getDisplayLocationName()
                    + "' has been opened and is "
                    + string
                    + " by class '"
                    + classFileBase.getDottedClassName()
                    + "' in file '"
                    + classFileBase.getDisplayLocationName()
                    + "'. Class '"
                    + classFileBase.getDottedClassName()
                    + "' is used by the opened classes but it has not been opened.  Either '"
                    + classFileBase1.getDottedClassName()
                    + "' should not be opened or '"
                    + classFileBase.getDottedClassName()
                    + "' must also be opened for obfuscation. (B)";
        } else {
            return null;
        }
    }

    public final boolean hasNoClassFile() {
        return this.classFile == null;
    }

    public final void renameMethodsRecursively(
            NameExclusionSet nameExclusionSet,
            ChangeLogMapping changeLogMapping1,
            MethodRenamer methodRenamer1,
            MethodOverrideAnalyzer methodOverrideAnalyzer,
            MethodNameGeneratorBase methodNameGeneratorBase,
            Map map1,
            ListMultimap listMultimap,
            int ba,
            Map map2,
            DisableableMap disableableMap,
            DisableableMap disableableMap1,
            boolean bl,
            boolean bl1,
            Set set1,
            Map map3
    ) throws ZkmException, IOException {
        if (this.classFile.isProgramClass()) {
            ((ProgramClass) this.classFile)
                    .renameMethods(
                            nameExclusionSet,
                            changeLogMapping1,
                            methodRenamer1,
                            methodOverrideAnalyzer,
                            methodNameGeneratorBase,
                            map1,
                            listMultimap,
                            ba,
                            map2,
                            disableableMap,
                            disableableMap1,
                            bl,
                            bl1,
                            set1,
                            this,
                            map3
                    );
        } else {
            ((ClasspathClassFile) this.classFile).indexMethods(map1, listMultimap, map2, disableableMap, disableableMap1);
        }

        if (this.subclassNodes != null) {
            int bb = this.subclassNodes.size();

            for (int i = 0; i < bb; i++) {
                ((ClassHierarchyNode) this.subclassNodes.get(i))
                        .renameMethodsRecursively(
                                nameExclusionSet,
                                changeLogMapping1,
                                methodRenamer1,
                                methodOverrideAnalyzer,
                                methodNameGeneratorBase,
                                ZkmUtils.copyMap(map1),
                                new ListMultimap(listMultimap),
                                ba,
                                ZkmUtils.copyMap(map2),
                                disableableMap,
                                ZkmUtils.copyDisableableMap(disableableMap1),
                                bl,
                                bl1,
                                set1,
                                map3
                        );
            }
        }
    }

    public final void collectSuperclasses(List list1) {
        ClassHierarchyNode classHierarchyNode1 = this;

        while (classHierarchyNode1.superclassNode != null) {
            classHierarchyNode1 = classHierarchyNode1.superclassNode;
            list1.add(classHierarchyNode1);
        }
    }

    public void encryptIntegerConstants(
            IntegerEncryptionExclusions integerEncryptionExclusions,
            MultiMapTable multiMapTable,
            PairMultiMap pairMultiMap,
            PairMultiMap pairMultiMap1,
            IntegerConstantEncryptor integerConstantEncryptor,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            Set set1,
            MethodParameterChanger methodParameterChanger,
            Map map1,
            Map map2,
            ClassRepository classRepository1,
            List list1,
            ClassResolver classResolver1,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ScriptEnvironment scriptEnvironment1,
            boolean bl
    ) throws ZkmException, IOException {
        if (this.classFile.isProgramClass() && (!bl || this.classFile.isInterface())) {
            if (set1.add(this.classFile)) {
                try {
                    ((ProgramClass) this.classFile)
                            .encryptIntegerConstants(
                                    inheritedMemberAnalyzer,
                                    integerConstantEncryptor,
                                    classRepository1,
                                    classResolver1,
                                    pairMultiMap.getPairs((ProgramClass) this.classFile),
                                    pairMultiMap1.getPairs((ProgramClass) this.classFile),
                                    multiMapTable.getPairMultiMap((ProgramClass) this.classFile),
                                    integerEncryptionExclusions,
                                    methodParameterChanger,
                                    map1,
                                    map2,
                                    list1,
                                    commonSuperTypeResolver1,
                                    classRepository1
                            );
                } catch (MethodAnalysisException methodAnalysisException1) {
                    scriptEnvironment1.logMessage(
                            "Couldn't encrypt Integer Constants in '" + this.classFile.getDottedClassName() + "' : \"" + methodAnalysisException1.getMessage() + "\" (A)"
                    );
                }

                if (this.classFile.hasVersionedVariants()) {
                    Iterator iterator = this.classFile.getVersionedVariants().iterator();

                    while (iterator.hasNext()) {
                        ClassFileBase classFileBase = (ClassFileBase) iterator.next();

                        try {
                            ((ProgramClass) classFileBase)
                                    .encryptIntegerConstants(
                                            inheritedMemberAnalyzer,
                                            integerConstantEncryptor,
                                            classRepository1,
                                            classResolver1,
                                            pairMultiMap.getPairs((ProgramClass) this.classFile),
                                            pairMultiMap1.getPairs((ProgramClass) this.classFile),
                                            multiMapTable.getPairMultiMap((ProgramClass) this.classFile),
                                            integerEncryptionExclusions,
                                            methodParameterChanger,
                                            map1,
                                            map2,
                                            list1,
                                            commonSuperTypeResolver1,
                                            classRepository1
                                    );
                        } catch (MethodAnalysisException methodAnalysisException) {
                            scriptEnvironment1.logMessage(
                                    "Couldn't encrypt Integer Constants in '"
                                            + classFileBase.getDisplayLocationName()
                                            + "' : \""
                                            + methodAnalysisException.getMessage()
                                            + "\" (B)"
                            );
                        }
                    }
                }

                if (bl) {
                    Enumeration enumeration = this.enumerateImplementors();
                    if (enumeration != null) {
                        while (enumeration.hasMoreElements()) {
                            ClassHierarchyNode classHierarchyNode1 = (ClassHierarchyNode) enumeration.nextElement();
                            classHierarchyNode1.encryptIntegerConstants(
                                    integerEncryptionExclusions,
                                    multiMapTable,
                                    pairMultiMap,
                                    pairMultiMap1,
                                    integerConstantEncryptor,
                                    inheritedMemberAnalyzer,
                                    set1,
                                    methodParameterChanger,
                                    map1,
                                    map2,
                                    classRepository1,
                                    list1,
                                    classResolver1,
                                    commonSuperTypeResolver1,
                                    scriptEnvironment1,
                                    bl
                            );
                        }
                    }
                } else {
                    Enumeration enumeration1 = this.enumerateSubclasses();
                    if (enumeration1 != null) {
                        while (enumeration1.hasMoreElements()) {
                            ClassHierarchyNode classHierarchyNode2 = (ClassHierarchyNode) enumeration1.nextElement();
                            classHierarchyNode2.encryptIntegerConstants(
                                    integerEncryptionExclusions,
                                    multiMapTable,
                                    pairMultiMap,
                                    pairMultiMap1,
                                    integerConstantEncryptor,
                                    inheritedMemberAnalyzer,
                                    set1,
                                    methodParameterChanger,
                                    map1,
                                    map2,
                                    classRepository1,
                                    list1,
                                    classResolver1,
                                    commonSuperTypeResolver1,
                                    scriptEnvironment1,
                                    bl
                            );
                        }
                    }
                }
            }
        }
    }

    public static synchronized void resetNodeRegistry() {
        nodesByName = HiddenOptionFlags.USE_PARALLEL ? new ConcurrentHashMap() : ZkmUtils.createHashMap();
    }

    @Override
    public final boolean equals(Object object) {
        return object instanceof ClassHierarchyNode ? this.className.equals(((ClassHierarchyNode) object).className) : false;
    }

    public final void addImplementor(Object object) {
        if (this.implementorNodes == null) {
            this.implementorNodes = new ArrayList(2);
        }

        this.implementorNodes.add(object);
    }

    public final boolean hasOuterClassNode() {
        return this.outerClassNode != null;
    }

    public final void collectRenamableMethods(MethodRenamer methodRenamer1) throws ZkmException, IOException {
        this.classFile.renameMethods(this, methodRenamer1);
        if (this.subclassNodes != null) {
            int ba = this.subclassNodes.size();

            for (int i = 0; i < ba; i++) {
                ((ClassHierarchyNode) this.subclassNodes.get(i)).collectRenamableMethods(methodRenamer1);
            }
        }
    }

    public final void addSubclass(Object object) {
        if (this.subclassNodes == null) {
            this.subclassNodes = new ArrayList(2);
        }

        this.subclassNodes.add(object);
    }

    public void encryptStrings(
            StringEncryptionExclusionSpec stringEncryptionExclusionSpec,
            PairMultiMap pairMultiMap,
            ListMultimap listMultimap,
            MultiMapTable multiMapTable,
            StringEncryptor stringEncryptor,
            boolean bl,
            boolean bl1,
            boolean bl2,
            boolean bl3,
            Set set1,
            HashMap hashMap,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            Set set2,
            MethodParameterChanger methodParameterChanger,
            Map map1,
            Map map2,
            ClassRepository classRepository1,
            List list1,
            ClassResolver classResolver1,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ScriptEnvironment scriptEnvironment1,
            boolean bl4
    ) throws ZkmException, IOException {
        if (this.classFile.isProgramClass() && (!bl4 || this.classFile.isInterface())) {
            if (set2.add(this.classFile)) {
                boolean bl5 = false;
                boolean bl6 = true;
                if (hashMap.containsKey(this.classFile)) {
                    bl5 = true;
                    bl6 = (Boolean) hashMap.get(this.classFile);
                } else if (this.classFile.supportsJava6()) {
                    bl5 = true;
                } else if (bl) {
                    bl5 = true;
                }

                try {
                    ((ProgramClass) this.classFile)
                            .encryptStrings(
                                    inheritedMemberAnalyzer,
                                    stringEncryptor,
                                    bl1,
                                    bl2,
                                    bl5,
                                    bl6,
                                    bl3,
                                    classRepository1,
                                    classResolver1,
                                    pairMultiMap.getPairs((ProgramClass) this.classFile),
                                    listMultimap.getValues((ProgramClass) this.classFile),
                                    multiMapTable.getPairMultiMap((ProgramClass) this.classFile),
                                    stringEncryptionExclusionSpec,
                                    set1,
                                    methodParameterChanger,
                                    map1,
                                    map2,
                                    list1,
                                    commonSuperTypeResolver1,
                                    classRepository1
                            );
                } catch (MethodAnalysisException methodAnalysisException1) {
                    scriptEnvironment1.logMessage(
                            "Couldn't encrypt Strings in '" + this.classFile.getDottedClassName() + "' : \"" + methodAnalysisException1.getMessage() + "\" (A)"
                    );
                }

                if (this.classFile.hasVersionedVariants()) {
                    Iterator iterator = this.classFile.getVersionedVariants().iterator();

                    while (iterator.hasNext()) {
                        ClassFileBase classFileBase = (ClassFileBase) iterator.next();

                        try {
                            ((ProgramClass) classFileBase)
                                    .encryptStrings(
                                            inheritedMemberAnalyzer,
                                            stringEncryptor,
                                            bl1,
                                            bl2,
                                            bl5,
                                            bl6,
                                            bl3,
                                            classRepository1,
                                            classResolver1,
                                            pairMultiMap.getPairs((ProgramClass) classFileBase),
                                            listMultimap.getValues((ProgramClass) classFileBase),
                                            multiMapTable.getPairMultiMap((ProgramClass) classFileBase),
                                            stringEncryptionExclusionSpec,
                                            set1,
                                            methodParameterChanger,
                                            map1,
                                            map2,
                                            list1,
                                            commonSuperTypeResolver1,
                                            classRepository1
                                    );
                        } catch (MethodAnalysisException methodAnalysisException) {
                            scriptEnvironment1.logMessage(
                                    "Couldn't encrypt Strings in '" + classFileBase.getDisplayLocationName() + "' : \"" + methodAnalysisException.getMessage() + "\" (B)"
                            );
                        }
                    }
                }

                if (bl4) {
                    Enumeration enumeration = this.enumerateImplementors();
                    if (enumeration != null) {
                        while (enumeration.hasMoreElements()) {
                            ClassHierarchyNode classHierarchyNode1 = (ClassHierarchyNode) enumeration.nextElement();
                            classHierarchyNode1.encryptStrings(
                                    stringEncryptionExclusionSpec,
                                    pairMultiMap,
                                    listMultimap,
                                    multiMapTable,
                                    stringEncryptor,
                                    bl,
                                    bl1,
                                    bl2,
                                    bl3,
                                    set1,
                                    hashMap,
                                    inheritedMemberAnalyzer,
                                    set2,
                                    methodParameterChanger,
                                    map1,
                                    map2,
                                    classRepository1,
                                    list1,
                                    classResolver1,
                                    commonSuperTypeResolver1,
                                    scriptEnvironment1,
                                    bl4
                            );
                        }
                    }
                } else {
                    Enumeration enumeration1 = this.enumerateSubclasses();
                    if (enumeration1 != null) {
                        while (enumeration1.hasMoreElements()) {
                            ClassHierarchyNode classHierarchyNode2 = (ClassHierarchyNode) enumeration1.nextElement();
                            classHierarchyNode2.encryptStrings(
                                    stringEncryptionExclusionSpec,
                                    pairMultiMap,
                                    listMultimap,
                                    multiMapTable,
                                    stringEncryptor,
                                    bl,
                                    bl1,
                                    bl2,
                                    bl3,
                                    set1,
                                    hashMap,
                                    inheritedMemberAnalyzer,
                                    set2,
                                    methodParameterChanger,
                                    map1,
                                    map2,
                                    classRepository1,
                                    list1,
                                    classResolver1,
                                    commonSuperTypeResolver1,
                                    scriptEnvironment1,
                                    bl4
                            );
                        }
                    }
                }
            }
        }
    }

    @Override
    public int compareTo(Object object) {
        return this.compareByName((ClassHierarchyNode) object);
    }

    public final Enumeration enumerateSubclasses() {
        return this.subclassNodes != null ? Collections.enumeration(this.subclassNodes) : null;
    }

    public static synchronized void rehashNodeRegistry() {
        Collection collection1 = nodesByName.values();
        resetNodeRegistry();
        Iterator iterator = collection1.iterator();

        while (iterator.hasNext()) {
            ClassHierarchyNode classHierarchyNode = (ClassHierarchyNode) iterator.next();
            nodesByName.put(classHierarchyNode.className, classHierarchyNode);
        }
    }

    public final boolean isInterface() {
        return this.classFile != null && this.classFile.isInterface();
    }

    public static ClassFileBase findClassFile(String string) {
        ClassHierarchyNode classHierarchyNode = (ClassHierarchyNode) nodesByName.get(string);
        return classHierarchyNode == null ? null : classHierarchyNode.classFile;
    }

    public final void setSuperclassNode(ClassHierarchyNode classHierarchyNode1) {
        this.superclassNode = classHierarchyNode1;
    }

    public final void analyzeParameterChanges(MethodParameterChangeSet methodParameterChangeSet) throws ZkmException, IOException {
        this.classFile.applyParameterChanges(this, methodParameterChangeSet);
        if (this.subclassNodes != null) {
            int ba = this.subclassNodes.size();

            for (int i = 0; i < ba; i++) {
                ((ClassHierarchyNode) this.subclassNodes.get(i)).analyzeParameterChanges(methodParameterChangeSet);
            }
        }
    }

    public final boolean isProgramClass() {
        return this.classFile != null && this.classFile.isProgramClass();
    }

    public final void collectProgramSubclasses(ArrayList arrayList) {
        if (this.subclassNodes != null) {
            int ba = 0;
            int bb = 0;

            for (List list1 = this.subclassNodes; bb < list1.size(); list1 = this.subclassNodes) {
                ClassHierarchyNode classHierarchyNode1 = (ClassHierarchyNode) this.subclassNodes.get(ba);
                if (classHierarchyNode1.isProgramClass()) {
                    arrayList.add(classHierarchyNode1);
                } else {
                    classHierarchyNode1.collectProgramSubclasses(arrayList);
                }

                bb = ++ba;
            }
        }
    }

    public final void sumSubtreeCounts(MutableInt mutableInt, MutableInt mutableInt1, CountingBag countingBag, CountingBag countingBag1) {
        int ba = countingBag.getCount(this.classFile);
        int bb = countingBag1.getCount(this.classFile);
        if (this.subclassNodes != null) {
            int bc = 0;
            int bd = 0;

            for (List list1 = this.subclassNodes; bd < list1.size(); list1 = this.subclassNodes) {
                ClassHierarchyNode classHierarchyNode1 = (ClassHierarchyNode) this.subclassNodes.get(bc);
                MutableInt mutableInt2 = new MutableInt(0);
                MutableInt mutableInt3 = new MutableInt(0);
                classHierarchyNode1.sumSubtreeCounts(mutableInt2, mutableInt3, countingBag, countingBag1);
                ba += mutableInt2.getValue();
                bb += mutableInt3.getValue();
                bd = ++bc;
            }
        }

        mutableInt.setValue(ba);
        mutableInt1.setValue(bb);
        if (this.classFile.isProgramClass()) {
            countingBag.addCount((ProgramClass) this.classFile, ba);
            countingBag1.addCount((ProgramClass) this.classFile, bb);
        }
    }

    public final ClassFileBase getClassFile() {
        return this.classFile;
    }

    @Override
    public final void handleObservedChange(ObservableModel observableModel1, Object object, Object object1, Object object2) throws ZkmException, IOException {
        if (object instanceof MutableInt && ((MutableInt) object).getValue() == 0 && object1 instanceof ClassFileBase) {
            this.className = this.classFile.getClassName();
        }
    }

    public final Enumeration enumerateImplementors() {
        return this.implementorNodes != null ? Collections.enumeration(this.implementorNodes) : null;
    }

    @Override
    public final int hashCode() {
        return this.className.hashCode();
    }

    public static ProgramClass findProgramClass(String string) {
        ClassHierarchyNode classHierarchyNode = findNode(string);
        return classHierarchyNode == null ? null : classHierarchyNode.getProgramClass();
    }

    public final void collectAllInterfaces(Set set1) {
        if (this.interfaceNodes != null) {
            for (int i = 0; i < this.interfaceNodes.size(); i++) {
                ClassHierarchyNode classHierarchyNode1 = (ClassHierarchyNode) this.interfaceNodes.get(i);
                set1.add(classHierarchyNode1);
                classHierarchyNode1.collectAllInterfaces(set1);
            }
        }
    }

    public int compareByName(ClassHierarchyNode classHierarchyNode1) {
        return this.getClassName().compareToIgnoreCase(classHierarchyNode1.getClassName());
    }

    public final ClassHierarchyNode getEnclosingNode() {
        return this.enclosingNode;
    }

    public void encryptLongConstants(
            LongEncryptionExclusionHandler longEncryptionExclusionHandler,
            MultiMapTable multiMapTable,
            PairMultiMap pairMultiMap,
            PairMultiMap pairMultiMap1,
            LongConstantEncryptor longConstantEncryptor,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            Set set1,
            MethodParameterChanger methodParameterChanger,
            Map map1,
            Map map2,
            ClassRepository classRepository1,
            List list1,
            ClassResolver classResolver1,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ScriptEnvironment scriptEnvironment1,
            boolean bl
    ) throws ZkmException, IOException {
        if (this.classFile.isProgramClass() && (!bl || this.classFile.isInterface())) {
            if (set1.add(this.classFile)) {
                try {
                    ((ProgramClass) this.classFile)
                            .encryptLongConstants(
                                    inheritedMemberAnalyzer,
                                    longConstantEncryptor,
                                    classRepository1,
                                    classResolver1,
                                    pairMultiMap.getPairs((ProgramClass) this.classFile),
                                    pairMultiMap1.getPairs((ProgramClass) this.classFile),
                                    multiMapTable.getPairMultiMap((ProgramClass) this.classFile),
                                    longEncryptionExclusionHandler,
                                    methodParameterChanger,
                                    map1,
                                    map2,
                                    list1,
                                    commonSuperTypeResolver1,
                                    classRepository1
                            );
                } catch (MethodAnalysisException methodAnalysisException1) {
                    scriptEnvironment1.logMessage(
                            "Couldn't encrypt Longs Constants in '" + this.classFile.getDottedClassName() + "' : \"" + methodAnalysisException1.getMessage() + "\" (A)"
                    );
                }

                if (this.classFile.hasVersionedVariants()) {
                    Iterator iterator = this.classFile.getVersionedVariants().iterator();

                    while (iterator.hasNext()) {
                        ClassFileBase classFileBase = (ClassFileBase) iterator.next();

                        try {
                            ((ProgramClass) classFileBase)
                                    .encryptLongConstants(
                                            inheritedMemberAnalyzer,
                                            longConstantEncryptor,
                                            classRepository1,
                                            classResolver1,
                                            pairMultiMap.getPairs((ProgramClass) this.classFile),
                                            pairMultiMap1.getPairs((ProgramClass) this.classFile),
                                            multiMapTable.getPairMultiMap((ProgramClass) this.classFile),
                                            longEncryptionExclusionHandler,
                                            methodParameterChanger,
                                            map1,
                                            map2,
                                            list1,
                                            commonSuperTypeResolver1,
                                            classRepository1
                                    );
                        } catch (MethodAnalysisException methodAnalysisException) {
                            scriptEnvironment1.logMessage(
                                    "Couldn't encrypt Long Constants in '"
                                            + classFileBase.getDisplayLocationName()
                                            + "' : \""
                                            + methodAnalysisException.getMessage()
                                            + "\" (B)"
                            );
                        }
                    }
                }

                if (bl) {
                    Enumeration enumeration = this.enumerateImplementors();
                    if (enumeration != null) {
                        while (enumeration.hasMoreElements()) {
                            ClassHierarchyNode classHierarchyNode1 = (ClassHierarchyNode) enumeration.nextElement();
                            classHierarchyNode1.encryptLongConstants(
                                    longEncryptionExclusionHandler,
                                    multiMapTable,
                                    pairMultiMap,
                                    pairMultiMap1,
                                    longConstantEncryptor,
                                    inheritedMemberAnalyzer,
                                    set1,
                                    methodParameterChanger,
                                    map1,
                                    map2,
                                    classRepository1,
                                    list1,
                                    classResolver1,
                                    commonSuperTypeResolver1,
                                    scriptEnvironment1,
                                    bl
                            );
                        }
                    }
                } else {
                    Enumeration enumeration1 = this.enumerateSubclasses();
                    if (enumeration1 != null) {
                        while (enumeration1.hasMoreElements()) {
                            ClassHierarchyNode classHierarchyNode2 = (ClassHierarchyNode) enumeration1.nextElement();
                            classHierarchyNode2.encryptLongConstants(
                                    longEncryptionExclusionHandler,
                                    multiMapTable,
                                    pairMultiMap,
                                    pairMultiMap1,
                                    longConstantEncryptor,
                                    inheritedMemberAnalyzer,
                                    set1,
                                    methodParameterChanger,
                                    map1,
                                    map2,
                                    classRepository1,
                                    list1,
                                    classResolver1,
                                    commonSuperTypeResolver1,
                                    scriptEnvironment1,
                                    bl
                            );
                        }
                    }
                }
            }
        }
    }

    public final List getSubclassNodes() {
        return this.subclassNodes != null ? new ArrayList(this.subclassNodes) : null;
    }

    public static synchronized ClassHierarchyNode getOrCreateNode(String string, ClassFileBase classFileBase) {
        ClassHierarchyNode classHierarchyNode = (ClassHierarchyNode) nodesByName.get(string);
        if (classHierarchyNode == null) {
            classHierarchyNode = new ClassHierarchyNode(string, classFileBase);
            nodesByName.put(string, classHierarchyNode);
        }

        return classHierarchyNode;
    }

    public final void applyMethodRenamer(MethodRenamer methodRenamer1) throws IOException {
        if (!this.hasNoClassFile()) {
            this.classFile.propagateMethodRenames(methodRenamer1);
        }

        if (this.subclassNodes != null) {
            int ba = this.subclassNodes.size();

            for (int i = 0; i < ba; i++) {
                ((ClassHierarchyNode) this.subclassNodes.get(i)).applyMethodRenamer(methodRenamer1);
            }
        }
    }

    public final void applyParameterChanges(
            ChangeLogMapping changeLogMapping1,
            MethodParameterChangeSet methodParameterChangeSet,
            ParameterListGenerator parameterListGenerator,
            SetMultiMap setMultiMap,
            MethodOverrideAnalyzer methodOverrideAnalyzer,
            SetMultiMap setMultiMap1,
            boolean bl,
            boolean bl1,
            Map map1
    ) throws ZkmException, IOException {
        ((ProgramClass) this.classFile)
                .changeMethodParameterLists(
                        setMultiMap.getValues(this.classFile),
                        changeLogMapping1,
                        methodParameterChangeSet,
                        parameterListGenerator,
                        methodOverrideAnalyzer,
                        setMultiMap1,
                        bl,
                        bl1,
                        this,
                        map1
                );
        if (this.subclassNodes != null) {
            int ba = this.subclassNodes.size();

            for (int i = 0; i < ba; i++) {
                ((ClassHierarchyNode) this.subclassNodes.get(i))
                        .applyParameterChanges(
                                changeLogMapping1,
                                methodParameterChangeSet,
                                parameterListGenerator,
                                setMultiMap,
                                methodOverrideAnalyzer,
                                setMultiMap1.deepCopy(),
                                bl,
                                bl1,
                                map1
                        );
            }
        }
    }

    public final int getDepth() {
        return this.depth;
    }

    public final ProgramClass getSuperProgramClass() {
        return this.superclassNode != null ? this.superclassNode.getProgramClass() : null;
    }

    public final void registerParameterChangeMethods(MethodParameterChangeSet methodParameterChangeSet) {
        if (!this.hasNoClassFile()) {
            this.classFile.propagateParameterChanges(methodParameterChangeSet);
        }

        if (this.subclassNodes != null) {
            int ba = this.subclassNodes.size();

            for (int i = 0; i < ba; i++) {
                ((ClassHierarchyNode) this.subclassNodes.get(i)).registerParameterChangeMethods(methodParameterChangeSet);
            }
        }
    }

    public final String[] getInterfaceNames() {
        String[] strings;
        if (this.interfaceNodes != null) {
            strings = new String[this.interfaceNodes.size()];

            for (int i = 0; i < this.interfaceNodes.size(); i++) {
                strings[i] = ((ClassHierarchyNode) this.interfaceNodes.get(i)).getClassName();
            }
        } else {
            strings = new String[0];
        }

        return strings;
    }

    public final void addInterface(Object object) {
        if (this.interfaceNodes == null) {
            this.interfaceNodes = new ArrayList(2);
        }

        this.interfaceNodes.add(object);
    }

    public static synchronized void disposeAllNodes() {
        Iterator iterator = nodesByName.values().iterator();

        while (iterator.hasNext()) {
            ((ClassHierarchyNode) iterator.next()).dispose();
        }

        nodesByName = HiddenOptionFlags.USE_PARALLEL ? new ConcurrentHashMap() : ZkmUtils.createHashMap();
    }

    public static void unlinkRemovedClasses(HashSet hashSet) {
        Iterator iterator = nodesByName.values().iterator();

        while (iterator.hasNext()) {
            ClassHierarchyNode classHierarchyNode = (ClassHierarchyNode) iterator.next();
            if (hashSet.contains(classHierarchyNode.getProgramClass())) {
                classHierarchyNode.enclosingNode = null;
                classHierarchyNode.outerClassNode = null;
                classHierarchyNode.enclosedClassNodes = null;
            } else {
                if (classHierarchyNode.enclosingNode != null
                        && classHierarchyNode.enclosingNode.classFile != null
                        && hashSet.contains(classHierarchyNode.enclosingNode.classFile)) {
                    classHierarchyNode.enclosingNode = null;
                }

                if (classHierarchyNode.outerClassNode != null
                        && classHierarchyNode.outerClassNode.classFile != null
                        && hashSet.contains(classHierarchyNode.outerClassNode.classFile)) {
                    classHierarchyNode.outerClassNode = null;
                }

                if (classHierarchyNode.enclosedClassNodes != null) {
                    Iterator iterator1 = classHierarchyNode.enclosedClassNodes.iterator();

                    while (iterator1.hasNext()) {
                        ClassHierarchyNode classHierarchyNode1 = (ClassHierarchyNode) iterator1.next();
                        if (classHierarchyNode1.classFile != null && hashSet.contains(classHierarchyNode1.classFile)) {
                            iterator1.remove();
                        }
                    }

                    if (classHierarchyNode.enclosedClassNodes.size() == 0) {
                        classHierarchyNode.enclosedClassNodes = null;
                    }
                }
            }
        }
    }
}
