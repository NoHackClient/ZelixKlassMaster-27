package com.zelix.klassmaster.engine;

import com.zelix.klassmaster.changelog.ChangeLogMapping;
import com.zelix.klassmaster.changelog.parser.ChangeLogNode;
import com.zelix.klassmaster.changelog.parser.ChangeLogParseException;
import com.zelix.klassmaster.changelog.parser.ChangeLogParser;
import com.zelix.klassmaster.changelog.parser.ChangeLogSimpleNode;
import com.zelix.klassmaster.changelog.parser.ChangeLogTokenMgrError;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.FieldInfo;
import com.zelix.klassmaster.classfile.FieldSignature;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchy;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.classfile.hierarchy.ClasspathClassFile;
import com.zelix.klassmaster.classfile.hierarchy.ClasspathClassLoader;
import com.zelix.klassmaster.config.ChangeLogInputFile;
import com.zelix.klassmaster.exceptions.ClassFileLoadException;
import com.zelix.klassmaster.exceptions.ZkmClassNotFoundException;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.license.LicenseCheckBase;
import com.zelix.klassmaster.log.MessageReporter;
import com.zelix.klassmaster.obfuscator.exclude.FixedClassesExclusionSet;
import com.zelix.klassmaster.obfuscator.exclude.NameExclusionSet;
import com.zelix.klassmaster.obfuscator.rename.RootPackageNode;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.ArrayEnumeration;
import com.zelix.klassmaster.util.IntegerCache;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.ObjectPair;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.Triple;
import com.zelix.klassmaster.util.TwoKeyMap;
import com.zelix.klassmaster.util.UniqueWorkQueue;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

public abstract class ObfuscatorEngineBase extends LicenseCheckBase {
    public final ClassRepository classRepository;
    public ProgramClass[] programClasses;
    public final RootPackageNode rootPackageNode;
    public final ClassHierarchy classHierarchy;
    public final ClasspathClassLoader classpathClassLoader;
    public final ClassResolver classResolver;
    public final boolean verbose;
    public final IntegerCache integerCache;

    public void removeGenericSignatures(int ba) {
        for (ProgramClass programClass1 : this.programClasses) {
            programClass1.removeGenericSignatures(ba);
            if (programClass1.hasVersionedVariants()) {
                Iterator iterator = programClass1.getVersionedVariants().iterator();

                while (iterator.hasNext()) {
                    ((ProgramClass) ((ClassFileBase) iterator.next())).removeGenericSignatures(ba);
                }
            }
        }
    }

    public void removeInnerClassInfo(int ba, HashMap hashMap, FixedClassesExclusionSet fixedClassesExclusionSet1, ScriptEnvironment scriptEnvironment1) throws IOException {
        if (ba != 0) {
            if (fixedClassesExclusionSet1 == null && ba == 1) {
                for (ProgramClass programClass3 : this.programClasses) {
                    programClass3.removeInnerClassAttributes();
                    if (programClass3.hasVersionedVariants()) {
                        Iterator iterator2 = programClass3.getVersionedVariants().iterator();

                        while (iterator2.hasNext()) {
                            ClassFileBase classFileBase1 = (ClassFileBase) iterator2.next();
                            ((ProgramClass) classFileBase1).removeInnerClassAttributes();
                        }
                    }
                }

                ClassHierarchyNode.clearAllEnclosingLinks();
                this.classHierarchy.clearInnerClassesHolders();
            } else {
                HashSet hashSet = ZkmUtils.createHashSet();
                Enumeration enumeration = this.classHierarchy.enumerateInnerClassesHolders();

                while (enumeration.hasMoreElements()) {
                    ClassHierarchyNode classHierarchyNode = (ClassHierarchyNode) enumeration.nextElement();
                    ProgramClass programClass1 = classHierarchyNode.getProgramClass();
                    String string = classHierarchyNode.getClassName();
                    if (programClass1 != null && classHierarchyNode.hasEnclosingNode()) {
                        if (ba == 1) {
                            hashSet.add(programClass1);
                        } else if (ba == 2
                                && hashMap.containsKey(string)
                                && !ClassFileBase.stripPackage((String) hashMap.get(string)).equals(ClassFileBase.stripPackage(string))) {
                            hashSet.add(programClass1);
                        }

                        if (fixedClassesExclusionSet1 != null && fixedClassesExclusionSet1.isMatchedClass(programClass1) && hashSet.contains(programClass1)) {
                            hashSet.remove(programClass1);
                            String string1 = ZkmUtils.slashesToDots((String) ZkmUtils.mapOrSelf(string, hashMap));
                            scriptEnvironment1.logWarning("Inner class information will not be removed from class '" + string1 + "' because it is a fixed class.");
                        }
                    }
                }

                Iterator iterator = new ArrayList(hashSet).iterator();

                while (iterator.hasNext()) {
                    ProgramClass programClass2 = (ProgramClass) iterator.next();
                    if (programClass2.hasVersionedVariants()) {
                        Iterator iterator1 = programClass2.getVersionedVariants().iterator();

                        while (iterator1.hasNext()) {
                            ClassFileBase classFileBase = (ClassFileBase) iterator1.next();
                            hashSet.add((ProgramClass) classFileBase);
                        }
                    }
                }

                for (ProgramClass programClass4 : this.programClasses) {
                    programClass4.removeInnerClassEntries(hashSet);
                    if (programClass4.hasVersionedVariants()) {
                        Iterator iterator3 = programClass4.getVersionedVariants().iterator();

                        while (iterator3.hasNext()) {
                            ((ProgramClass) ((ClassFileBase) iterator3.next())).removeInnerClassEntries(hashSet);
                        }
                    }
                }

                ClassHierarchyNode.unlinkRemovedClasses(hashSet);
                this.classHierarchy.removeHoldersWithoutInnerClasses();
            }
        }
    }

    public ChangeLogMapping parseChangeLog(
            String string, Reader reader1, MessageReporter messageReporter1, ScriptEnvironment scriptEnvironment1, Boolean boolean1
    ) throws ZkmException, IOException {
        ChangeLogSimpleNode changeLogSimpleNode = null;

        ChangeLogMapping changeLogMapping1;
        try {
            changeLogMapping1 = new ChangeLogMapping(string, scriptEnvironment1, boolean1);
            ChangeLogParser changeLogParser = ChangeLogParser.instance;
            if (changeLogParser == null) {
                new ChangeLogParser(reader1);
            } else {
                ChangeLogParser.ReInit(reader1);
            }

            changeLogSimpleNode = ChangeLogParser.Input();
            ChangeLogMapping changeLogMapping2 = changeLogMapping1;
            changeLogSimpleNode.interpret((ChangeLogNode) null, 30872, 34067, 41973, changeLogMapping2);
            if (changeLogSimpleNode.jjtGetNumChildren() == 0) {
                messageReporter1.reportError("INPUT CHANGE LOG ERROR:", "\"" + string + "\" appears to contain no change data");
                changeLogMapping1 = null;
            } else {
                changeLogMapping1.checkClassPackageNameClashes();
                changeLogMapping1.derivePackageMappingsFromClasses();
                changeLogMapping1.checkMemberTypeConsistency();
                changeLogMapping1.markParsingComplete();
            }
        } catch (ChangeLogTokenMgrError changeLogTokenMgrError) {
            String string2 = ChangeLogMapping.describeSourceFile(string, scriptEnvironment1);
            messageReporter1.reportErrorWithDetail(
                    "INPUT CHANGE LOG ERROR:", "Lexical error while reading \"" + string + "\"" + string2, changeLogTokenMgrError.getMessage()
            );
            changeLogMapping1 = null;
        } catch (ChangeLogParseException changeLogParseException) {
            String string1 = ChangeLogMapping.describeSourceFile(string, scriptEnvironment1);
            messageReporter1.reportErrorWithDetail(
                    "INPUT CHANGE LOG ERROR:", "Parse error while reading \"" + string + "\"" + string1, changeLogParseException.getMessage()
            );
            changeLogMapping1 = null;
        } finally {
            if (changeLogSimpleNode != null) {
                changeLogSimpleNode.dump();
            }

            if (reader1 != null) {
                try {
                    reader1.close();
                } catch (IOException iOException) {
                }
            }
        }

        return changeLogMapping1;
    }

    public static boolean applyChangeLogClassNames(
            HashMap hashMap, HashMap hashMap1, HashMap hashMap2, ClasspathClassLoader classpathClassLoader1, ChangeLogMapping changeLogMapping1
    ) throws ZkmException, IOException {
        boolean bl = false;
        if (changeLogMapping1 != null) {
            ArrayList arrayList = changeLogMapping1.getOldClassNames();
            ArrayList arrayList1 = new ArrayList();

            for (int i = 0; i < arrayList.size(); i++) {
                String string = (String) arrayList.get(i);
                if (!hashMap.containsKey(string)) {
                    String string1 = changeLogMapping1.getNewClassName(string);
                    if (string1 == null) {
                        string1 = string;
                    }

                    if (!string.equals(string1)) {
                        bl = true;
                    }

                    hashMap.put(string, string1);
                    hashMap1.put(string1, string);
                    String string2 = changeLogMapping1.getSourceName(string);
                    if (string2 != null) {
                        hashMap2.put(string1, string2);
                    }

                    ClasspathClassFile classpathClassFile = (ClasspathClassFile) ClassHierarchyNode.findNode(string).getClassFile();
                    if (!string.equals(string1)) {
                        classpathClassLoader1.removeCachedClass(string);
                        Triple triple = new Triple(string, string1, classpathClassFile);
                        arrayList1.add(triple);
                    }
                }
            }

            Iterator iterator = arrayList1.iterator();

            while (iterator.hasNext()) {
                Triple triple1 = (Triple) iterator.next();
                ((ClasspathClassFile) triple1.getThird()).renameClass((String) triple1.getSecond(), (HashMap) null);
                classpathClassLoader1.putCachedClass((String) triple1.getSecond(), (ClasspathClassFile) triple1.getThird());
            }
        }

        return bl;
    }

    public static ListMultimap invertToMultimap(HashMap hashMap) {
        ListMultimap listMultimap = new ListMultimap();
        Iterator iterator = hashMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            listMultimap.addValue(entry.getValue(), entry.getKey());
        }

        return listMultimap;
    }

    public boolean initClassNameMaps(HashMap hashMap, HashMap hashMap1, HashMap hashMap2, ChangeLogMapping changeLogMapping1) throws ZkmException, IOException {
        int ba = 0;
        int bb = 0;

        for (ProgramClass[] programClass1 = this.programClasses; bb < programClass1.length; programClass1 = this.programClasses) {
            String string = this.programClasses[ba].getClassName();
            hashMap.put(string, string);
            hashMap1.put(string, string);
            bb = ++ba;
        }

        return applyChangeLogClassNames(hashMap, hashMap1, hashMap2, this.classpathClassLoader, changeLogMapping1);
    }

    public void excludeOuterClassesOfInners(int ba, NameExclusionSet nameExclusionSet, ChangeLogMapping changeLogMapping1) throws ZkmException, IOException {
        Enumeration enumeration = this.classHierarchy.enumerateInnerClassesHolders();
        UniqueWorkQueue uniqueWorkQueue = new UniqueWorkQueue();
        uniqueWorkQueue.enqueueAllElements(enumeration);

        while (!uniqueWorkQueue.isEmpty()) {
            ClassHierarchyNode classHierarchyNode = (ClassHierarchyNode) uniqueWorkQueue.dequeue();
            if (classHierarchyNode.hasEnclosingNode()
                    && !this.isClassNameExcluded(classHierarchyNode.getClassFile(), nameExclusionSet, changeLogMapping1)
                    && ba != 1) {
                ClassHierarchyNode classHierarchyNode1 = classHierarchyNode.getEnclosingNode();
                if (classHierarchyNode1 != null
                        && classHierarchyNode1.isProgramClass()
                        && !nameExclusionSet.isClassExcluded(classHierarchyNode1.getProgramClass())
                        && (changeLogMapping1 == null || !changeLogMapping1.hasClassMapping(classHierarchyNode1.getClassName()))) {
                    String string = "Inner class format retained and inner class '"
                            + ZkmUtils.slashesToDots(classHierarchyNode.getClassName())
                            + "' is excluded. (1)";
                    nameExclusionSet.excludeClassInternal(classHierarchyNode1.getProgramClass(), string, true);
                    uniqueWorkQueue.enqueue(classHierarchyNode1);
                }

                ClassHierarchyNode classHierarchyNode2 = classHierarchyNode.getOuterClassNode();
                if (classHierarchyNode2 != null
                        && classHierarchyNode2.isProgramClass()
                        && classHierarchyNode2 != classHierarchyNode1
                        && !nameExclusionSet.isClassExcluded(classHierarchyNode2.getProgramClass())
                        && (changeLogMapping1 == null || !changeLogMapping1.hasClassMapping(classHierarchyNode2.getClassName()))) {
                    String string1 = "Inner class format retained and inner class '"
                            + ZkmUtils.slashesToDots(classHierarchyNode.getClassName())
                            + "' is excluded. (2)";
                    nameExclusionSet.excludeClassInternal(classHierarchyNode2.getProgramClass(), string1, true);
                    uniqueWorkQueue.enqueue(classHierarchyNode2);
                }
            }
        }
    }

    public final ChangeLogMapping loadChangeLogs(
            ChangeLogInputFile[] changeLogInputFiles, MessageReporter messageReporter1, ScriptEnvironment scriptEnvironment1, boolean bl, boolean bl1
    ) throws ZkmException, IOException {
        ChangeLogMapping changeLogMapping1 = null;
        int seriousErrorCount = scriptEnvironment1.getSeriousErrorCount();
        boolean bl2 = false;

        for (int i = 0; i < changeLogInputFiles.length && seriousErrorCount == scriptEnvironment1.getSeriousErrorCount(); i++) {
            ChangeLogInputFile changeLogInputFile = changeLogInputFiles[i];
            String string = changeLogInputFile.getFileName();
            File file1 = new File(string);
            boolean bl3 = ChangeLogMapping.isAggressiveMethodOverloading(file1);
            ObfuscatorEngineBase obfuscatorEngineBase1;
            String string2;
            Reader reader1;
            if (bl3 != bl1) {
                String string1 = "Aggressive method overloading "
                        + (bl1 ? "is" : "is not")
                        + " set but change log '"
                        + file1.getAbsolutePath()
                        + "' was created "
                        + (bl3 ? "with" : "without")
                        + " aggressive method overloading. This can result in name clashes.";
                scriptEnvironment1.logWarning(string1);
                obfuscatorEngineBase1 = this;
                string2 = string;
                reader1 = changeLogInputFile.getReader();
            } else {
                obfuscatorEngineBase1 = this;
                string2 = string;
                reader1 = changeLogInputFile.getReader();
            }

            ChangeLogMapping changeLogMapping2 = obfuscatorEngineBase1.parseChangeLog(string2, reader1, messageReporter1, scriptEnvironment1, bl);
            if (changeLogMapping1 == null) {
                changeLogMapping1 = changeLogMapping2;
            } else {
                changeLogMapping1.mergeWith(changeLogMapping2);
                bl2 = true;
            }
        }

        if (bl2) {
            changeLogMapping1.checkClassPackageNameClashes();
        }

        return changeLogMapping1;
    }

    public void registerChangeLogPackages(ChangeLogMapping changeLogMapping1, boolean bl) throws ZkmException, IOException {
        if (changeLogMapping1 != null) {
            HashSet hashSet = ZkmUtils.createHashSet();
            ArrayList arrayList = changeLogMapping1.getOldClassNames();

            for (int i = 0; i < arrayList.size(); i++) {
                String string = (String) arrayList.get(i);
                if (!ClassHierarchyNode.isProgramClassName(string)) {
                    try {
                        if (!bl && this.classpathClassLoader.findClassFile(string) != null) {
                            String string1 = ClassFileBase.getPackagePath(string);
                            if (string1.length() > 0) {
                                hashSet.add(string1);
                            }
                        } else {
                            changeLogMapping1.removeClassMapping(string);
                        }
                    } catch (ClassFileLoadException classFileLoadException) {
                        throw new ZkmProcessingException(classFileLoadException.getMessage());
                    }
                } else {
                    String string2 = ClassFileBase.getPackagePath(string);
                    if (string2.length() > 0) {
                        hashSet.add(string2);
                    }
                }
            }

            this.rootPackageNode.reportUnknownChangeLogPackages(changeLogMapping1, hashSet);
        }
    }

    public boolean initFieldNameMaps(TwoKeyMap twoKeyMap, TwoKeyMap twoKeyMap1, ChangeLogMapping changeLogMapping1) throws ZkmException, IOException {
        int ba = 0;
        int bc = 0;

        for (ProgramClass[] programClass1 = this.programClasses; bc < programClass1.length; programClass1 = this.programClasses) {
            String string = this.programClasses[ba].getClassName();
            ArrayEnumeration arrayEnumeration = this.programClasses[ba].enumerateFields();

            while (arrayEnumeration.hasMoreElements()) {
                FieldInfo fieldInfo = (FieldInfo) arrayEnumeration.nextElement();
                FieldSignature fieldSignature = fieldInfo.getSignature();
                twoKeyMap.putValue(string, fieldSignature, fieldSignature);
                twoKeyMap1.putValue(string, fieldSignature, fieldSignature);
            }

            bc = ++ba;
        }

        boolean bl = false;
        if (changeLogMapping1 != null) {
            Enumeration enumeration = changeLogMapping1.getFieldMappedClassNames();

            while (enumeration.hasMoreElements()) {
                String string1 = (String) enumeration.nextElement();
                if (!twoKeyMap.containsKey(string1)) {
                    ArrayList arrayList = changeLogMapping1.getFieldMappingPairs(string1);

                    for (int i = 0; i < arrayList.size(); i++) {
                        ObjectPair objectPair = (ObjectPair) arrayList.get(i);
                        FieldSignature fieldSignature1 = (FieldSignature) objectPair.getFirst();
                        FieldSignature fieldSignature2 = (FieldSignature) objectPair.getSecond();
                        twoKeyMap.putValue(string1, fieldSignature1, fieldSignature2);
                        twoKeyMap1.putValue(string1, fieldSignature2, fieldSignature1);
                        if (!bl && !fieldSignature1.getName().equals(fieldSignature2.getName())) {
                            bl = true;
                        }
                    }

                    ClasspathClassFile classpathClassFile = (ClasspathClassFile) ClassHierarchyNode.findClassFile(string1);
                    classpathClassFile.applyFieldNameMapping(twoKeyMap);
                }
            }
        }

        return bl;
    }

    public ObfuscatorEngineBase(ClassRepository classRepository1, ProgramClass[] programClass1, RootPackageNode rootPackageNode1) {
        this.classRepository = classRepository1;
        this.programClasses = programClass1;
        this.rootPackageNode = rootPackageNode1;
        this.classHierarchy = classRepository1.getClassHierarchy();
        this.classpathClassLoader = classRepository1.getClasspathLoader();
        this.classResolver = classRepository1.getClassResolver();
        this.verbose = classRepository1.isVerbose();
        this.integerCache = IntegerCache.getInstance();
    }

    public boolean isClassNameExcluded(ClassFileBase classFileBase, NameExclusionSet nameExclusionSet, ChangeLogMapping changeLogMapping1) throws IOException {
        String string = classFileBase.getClassName();
        if (changeLogMapping1 != null && changeLogMapping1.hasClassMapping(string)) {
            return changeLogMapping1.isSimpleNameChanged(string);
        } else {
            return classFileBase.isProgramClass() ? nameExclusionSet.isClassIncluded((ProgramClass) classFileBase) : false;
        }
    }

    public ClassFileBase[] addChangeLogClasspathClasses(ChangeLogMapping changeLogMapping1) throws ZkmException, IOException {
        ArrayList arrayList = changeLogMapping1.getOldClassNames();
        ArrayList arrayList1 = new ArrayList();

        for (int i = 0; i < arrayList.size(); i++) {
            String string = (String) arrayList.get(i);
            ClassFileBase classFileBase = this.classResolver.getClassFile(string, "looking for input change log classes");
            if (!classFileBase.isProgramClass()) {
                if (this.verbose) {
                    changeLogMapping1.logMessage(
                            "Class '"
                                    + classFileBase.getDottedClassName()
                                    + "' no longer appears in the opened classes but its name mappings will be honored since the class was found in the classpath at '"
                                    + classFileBase.getLocationName()
                                    + "'."
                    );
                }

                arrayList1.add((ClasspathClassFile) classFileBase);
            }
        }

        ClassFileBase[] classFileBases = new ClassFileBase[this.programClasses.length + arrayList1.size()];
        arrayList1.toArray(classFileBases);
        System.arraycopy(this.programClasses, 0, classFileBases, arrayList1.size(), this.programClasses.length);
        this.classHierarchy.build(classFileBases);
        return classFileBases;
    }

    public void validateClassHierarchy(MessageReporter messageReporter1) throws ZkmException, IOException {
        try {
            ObservableHolder observableHolder = new ObservableHolder();
            if (!this.classHierarchy.validateLoadedHierarchies(observableHolder, this.classResolver)) {
                messageReporter1.reportErrorWithDetail("SERIOUS ERROR:", "Gap in inheritance or implementation hierarchy.", (String) observableHolder.getValue());
            }
        } catch (ZkmClassNotFoundException zkmClassNotFoundException) {
            messageReporter1.reportProblem(
                    "WARNING:",
                    "'"
                            + zkmClassNotFoundException.getClassName()
                            + "' not found. Class validation incomplete because classpath is incomplete or it has not been set BEFORE opening classes. You should correct the classpath and reopen classes."
            );
        } catch (ClassFileLoadException classFileLoadException) {
            messageReporter1.reportFatalError("FATAL ERROR:", classFileLoadException.getMessage());
        }
    }
}
