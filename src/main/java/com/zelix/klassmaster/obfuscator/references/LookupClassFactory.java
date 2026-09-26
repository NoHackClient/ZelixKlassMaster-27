package com.zelix.klassmaster.obfuscator.references;

import com.zelix.klassmaster.archive.InputFileLocation;
import com.zelix.klassmaster.archive.SourceArchive;
import com.zelix.klassmaster.changelog.ChangeLogMapping;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.classfile.hierarchy.ClasspathClassLoader;
import com.zelix.klassmaster.exceptions.ZkmClassNotFoundException;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.obfuscator.parameters.LookupClassOption;
import com.zelix.klassmaster.obfuscator.rename.SequentialNameGenerator;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.CountingBag;
import com.zelix.klassmaster.util.EnumerableMap;
import com.zelix.klassmaster.util.JavaRuntimeVersion;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.ObjectTriple;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ReadOnlyMultiMapView;
import com.zelix.klassmaster.util.SetMultiMap;
import com.zelix.klassmaster.util.TwoKeyMap;
import com.zelix.klassmaster.util.ZkmAssert;
import com.zelix.klassmaster.util.ZkmStringUtils;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class LookupClassFactory {
    public static String describeLookupClasses(String string, Set set1, ListMultimap listMultimap) {
        String string1 = "\t" + string + " : Created special lookup class" + (set1.size() == 1 ? "" : "es") + " as ";
        String string2 = ZkmStringUtils.pad("\t", 76, string1.length(), 32);
        StringBuilder stringBuilder = new StringBuilder();
        Iterator iterator = set1.iterator();

        while (iterator.hasNext()) {
            ProgramClass programClass1 = (ProgramClass) iterator.next();
            StringBuilder stringBuilder1;
            char bb;
            if (stringBuilder.length() == 0) {
                stringBuilder.append(string1);
                stringBuilder1 = stringBuilder;
                bb = '\'';
            } else {
                stringBuilder.append(string2);
                stringBuilder1 = stringBuilder;
                bb = '\'';
            }

            stringBuilder1.append(bb);
            stringBuilder.append(programClass1.getDottedClassName());
            stringBuilder.append('\'');
            List list1 = listMultimap.getValues(programClass1);
            if (list1 != null) {
                for (int i = 0; i < list1.size(); i++) {
                    SourceArchive sourceArchive1 = (SourceArchive) list1.get(i);
                    if (i == 0) {
                        stringBuilder.append(" in module" + (list1.size() > 1 ? "s '" : " '"));
                    }

                    stringBuilder.append(sourceArchive1.getModuleName());
                    if (i < list1.size() - 1) {
                        stringBuilder.append("', ");
                    } else {
                        stringBuilder.append('\'');
                    }
                }

                stringBuilder.append(ZkmUtils.LINE_SEPARATOR);
            } else {
                stringBuilder.append(ZkmUtils.LINE_SEPARATOR);
            }
        }

        return stringBuilder.toString();
    }

    public static String describeLookupClassesByArchive(String string, Set set1, Map map1) {
        ListMultimap listMultimap = new ListMultimap();
        Iterator iterator = map1.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            listMultimap.addValue(entry.getValue(), entry.getKey());
        }

        return describeLookupClasses(string, set1, listMultimap);
    }

    public static ProgramClass[] createLookupClasses(
            Set set1,
            Map map1,
            ObservableHolder observableHolder,
            ObservableHolder observableHolder1,
            Map map2,
            Map map3,
            Set set2,
            Map map4,
            int ba,
            int bb,
            SyntheticClassFactory syntheticClassFactory,
            ClassRepository classRepository1,
            LookupClassOption lookupClassOption
    ) throws ZkmException, IOException {
        ProgramClass[] programClass1 = null;
        HashMap hashMap = ZkmUtils.createHashMap();
        Iterator iterator = set1.iterator();

        while (iterator.hasNext()) {
            String string = (String) iterator.next();
            LookupClassOption lookupClassOption1 = lookupClassOption;
            Integer integer1 = bb;
            Integer integer = 30276;
            ProgramClass programClass2 = syntheticClassFactory.createSyntheticClass(string, ba, integer, integer1, lookupClassOption1);
            programClass1 = classRepository1.addClass(programClass2);
            hashMap.put(string, programClass2);
            set2.add(programClass2);
            if (programClass2.supportsJava6()) {
                programClass2.markAllMethodsModified();
            }
        }

        if (lookupClassOption == null || lookupClassOption.shouldMapClassesToLookupClass()) {
            iterator = map1.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                ProgramClass programClass5 = (ProgramClass) entry.getKey();
                String string1 = (String) entry.getValue();
                ProgramClass programClass3 = (ProgramClass) hashMap.get(string1);
                map3.put(programClass5, programClass3);
            }

            iterator = map4.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry1 = (Entry) iterator.next();
                SourceArchive sourceArchive1 = (SourceArchive) entry1.getKey();
                String string2 = (String) entry1.getValue();
                ProgramClass programClass6 = (ProgramClass) hashMap.get(string2);
                map2.put(sourceArchive1, programClass6);
            }

            if (!observableHolder.isValueNull()) {
                ProgramClass programClass4 = (ProgramClass) hashMap.get(observableHolder.getValue());
                observableHolder1.setValue(programClass4);
            }
        }

        return programClass1;
    }

    public static void assignClassesToLookupNames(ObservableHolder observableHolder, Map map1, Set set1, Map map2, ReadOnlyMultiMapView readOnlyMultiMapView) {
        Iterator iterator = readOnlyMultiMapView.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            SourceArchive sourceArchive1 = (SourceArchive) entry.getKey();
            String string = (String) map1.get(sourceArchive1);
            Iterator iterator1 = ((List) entry.getValue()).iterator();

            while (iterator1.hasNext()) {
                ProgramClass programClass1 = (ProgramClass) iterator1.next();
                map2.put(programClass1, string);
            }
        }

        if (!set1.isEmpty()) {
            String string1 = (String) observableHolder.getValue();
            Iterator iterator2 = set1.iterator();

            while (iterator2.hasNext()) {
                ProgramClass programClass2 = (ProgramClass) iterator2.next();
                map2.put(programClass2, string1);
            }
        }
    }

    public static void checkCommonPackage(SourceArchive sourceArchive1, SetMultiMap setMultiMap, SetMultiMap setMultiMap1) throws ZkmProcessingException {
        if (setMultiMap1.getValues(sourceArchive1).isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("There are modules that have packages in common but there is no one package that is common to them all : '");
            Iterator iterator = setMultiMap.getValues(sourceArchive1).iterator();

            while (iterator.hasNext()) {
                SourceArchive sourceArchive2 = (SourceArchive) iterator.next();
                stringBuilder.append(sourceArchive2.getModuleName());
                stringBuilder.append(", ");
            }

            stringBuilder.append(sourceArchive1.getModuleName());
            stringBuilder.append('\'');
            throw new ZkmProcessingException(stringBuilder.toString());
        }
    }

    public static ReadOnlyMultiMapView filterModuleArchives(ReadOnlyMultiMapView readOnlyMultiMapView, SetMultiMap setMultiMap, SetMultiMap setMultiMap1) {
        ListMultimap listMultimap = new ListMultimap(readOnlyMultiMapView.getKeyCount());
        SetMultiMap setMultiMap2 = new SetMultiMap(readOnlyMultiMapView.getKeyCount());
        Iterator iterator = readOnlyMultiMapView.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            SourceArchive sourceArchive1 = (SourceArchive) entry.getKey();
            if (sourceArchive1.hasModuleName()) {
                List list1 = (List) entry.getValue();
                listMultimap.appendValues(sourceArchive1, list1);
                Iterator iterator1 = list1.iterator();

                while (iterator1.hasNext()) {
                    ProgramClass programClass1 = (ProgramClass) iterator1.next();
                    setMultiMap2.addValue(sourceArchive1, programClass1.getPackagePath());
                }
            }
        }

        computeCommonPackages(setMultiMap1, setMultiMap, setMultiMap2);
        return new ReadOnlyMultiMapView(listMultimap);
    }

    public static Set assignLookupClassNames(
            Set set1,
            String string,
            ObservableHolder observableHolder,
            Map map1,
            Map map2,
            String string1,
            String string2,
            Set set2,
            Set set3,
            ReadOnlyMultiMapView readOnlyMultiMapView,
            SetMultiMap setMultiMap,
            SetMultiMap setMultiMap1,
            EnumerableMap enumerableMap,
            EnumerableMap enumerableMap1,
            EnumerableMap enumerableMap2,
            Set set4,
            String string3,
            boolean bl,
            boolean bl1,
            List list1,
            ClasspathClassLoader classpathClassLoader1,
            ScriptEnvironment scriptEnvironment1
    ) throws ZkmException, IOException {
        String string4 = null;
        if (string2 != null && string2.length() > 0) {
            String string6 = ZkmUtils.dotsToSlashes(string2);
            String string5 = (String) enumerableMap.get(string6);
            if (string5 != null) {
                string4 = string5;
            } else {
                scriptEnvironment1.logWarning(
                        "Package '" + string2 + "' specified in '" + string1 + "' parameter but the package does not exist.  Another package will be used."
                );
            }
        }

        boolean bl2 = set2.size() > 0;
        HashSet hashSet = ZkmUtils.createHashSet((bl2 ? 1 : 0) + ZkmUtils.getPrimeCapacity(readOnlyMultiMapView.getKeyCount()));
        if (readOnlyMultiMapView.getKeyCount() == 0) {
            Boolean boolean2 = bl1;
            List list2 = list1;
            ClasspathClassLoader classpathClassLoader2 = classpathClassLoader1;
            Boolean boolean1 = bl;
            String string8 = string3;
            Set set7 = set4;
            EnumerableMap enumerableMap5 = enumerableMap2;
            EnumerableMap enumerableMap4 = enumerableMap1;
            EnumerableMap enumerableMap3 = enumerableMap;
            Set set6 = set1;
            String string10 = generateLookupClassName(
                    string,
                    string4,
                    (Collection) null,
                    set6,
                    enumerableMap3,
                    enumerableMap4,
                    enumerableMap5,
                    set7,
                    string8,
                    boolean1,
                    classpathClassLoader2,
                    list2,
                    boolean2
            );
            hashSet.add(string10);
            observableHolder.setValue(string10);
            Iterator iterator2 = set1.iterator();

            while (iterator2.hasNext()) {
                ProgramClass programClass1 = (ProgramClass) iterator2.next();
                if (!programClass1.isGenerated()) {
                    map2.put(programClass1, string10);
                }
            }

            return hashSet;
        } else {
            Iterator iterator = readOnlyMultiMapView.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                SourceArchive sourceArchive1 = (SourceArchive) entry.getKey();
                if (!map1.containsKey(sourceArchive1)) {
                    checkCommonPackage(sourceArchive1, setMultiMap, setMultiMap1);
                    if (!map1.containsKey(sourceArchive1)) {
                        String string7 = null;
                        if (setMultiMap.containsKey(sourceArchive1)) {
                            Set set5 = setMultiMap.getValues(sourceArchive1);
                            Iterator iterator1 = set5.iterator();

                            while (iterator1.hasNext()) {
                                SourceArchive sourceArchive2 = (SourceArchive) iterator1.next();
                                if (map1.containsKey(sourceArchive2)) {
                                    string7 = (String) map1.get(sourceArchive2);
                                    ZkmAssert.assertTrue(
                                            setMultiMap1.getValues(sourceArchive1).contains(ClassFileBase.getPackagePath(string7)),
                                            new String[]{"No common package names : '" + sourceArchive1.getModuleName() + "' and '" + sourceArchive2.getModuleName() + "'"}
                                    );
                                    break;
                                }
                            }
                        }

                        if (string7 == null) {
                            Set set10 = setMultiMap1.getValues(sourceArchive1);
                            Collection collection1 = (Collection) entry.getValue();
                            Boolean boolean3 = bl1;
                            string7 = generateLookupClassName(
                                    string,
                                    string4,
                                    set10,
                                    collection1,
                                    enumerableMap,
                                    enumerableMap1,
                                    enumerableMap2,
                                    set4,
                                    string3,
                                    bl,
                                    classpathClassLoader1,
                                    list1,
                                    boolean3
                            );
                        }

                        map1.put(sourceArchive1, string7);
                    }
                }
            }

            if (!set3.isEmpty() && observableHolder.isValueNull()) {
                HashSet hashSet1 = ZkmUtils.createHashSetFrom(set2);
                hashSet1.removeAll(set3);
                String string11 = null;
                Iterator iterator3 = hashSet1.iterator();

                while (iterator3.hasNext()) {
                    ProgramClass programClass2 = (ProgramClass) iterator3.next();
                    Enumeration enumeration = programClass2.enumerateInputLocations();

                    while (enumeration.hasMoreElements()) {
                        InputFileLocation inputFileLocation = (InputFileLocation) enumeration.nextElement();
                        if (inputFileLocation.isArchiveEntry()) {
                            SourceArchive sourceArchive3 = inputFileLocation.getSourceArchive();
                            if (sourceArchive3.hasModuleName()) {
                                SourceArchive sourceArchive5 = sourceArchive3;
                                string11 = (String) map1.get(sourceArchive5);
                            }
                        }
                    }
                }

                String string12 = string11;
                if (string12 == null) {
                    Boolean boolean5 = bl1;
                    List list3 = list1;
                    ClasspathClassLoader classpathClassLoader3 = classpathClassLoader1;
                    Boolean boolean4 = bl;
                    String string9 = string3;
                    Set set9 = set4;
                    EnumerableMap enumerableMap8 = enumerableMap2;
                    EnumerableMap enumerableMap7 = enumerableMap1;
                    EnumerableMap enumerableMap6 = enumerableMap;
                    Set set8 = set3;
                    string12 = generateLookupClassName(
                            string,
                            string4,
                            (Collection) null,
                            set8,
                            enumerableMap6,
                            enumerableMap7,
                            enumerableMap8,
                            set9,
                            string9,
                            boolean4,
                            classpathClassLoader3,
                            list3,
                            boolean5
                    );
                    hashSet.add(string12);
                }

                observableHolder.setValue(string12);
                Iterator iterator4 = set3.iterator();

                while (iterator4.hasNext()) {
                    ProgramClass programClass3 = (ProgramClass) iterator4.next();
                    map2.put(programClass3, string12);
                }
            }

            iterator = readOnlyMultiMapView.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry1 = (Entry) iterator.next();
                SourceArchive sourceArchive4 = (SourceArchive) entry1.getKey();
                String string13 = (String) map1.get(sourceArchive4);
                hashSet.add(string13);
                Iterator iterator5 = ((List) entry1.getValue()).iterator();

                while (iterator5.hasNext()) {
                    ProgramClass programClass4 = (ProgramClass) iterator5.next();
                    if (!programClass4.isGenerated()) {
                        map2.put(programClass4, string13);
                    }
                }
            }

            return hashSet;
        }
    }

    public static String validateChangeLogLookupName(
            String string, String string1, String string2, String string3, String string4, Set set1, ChangeLogMapping changeLogMapping1, EnumerableMap enumerableMap
    ) throws IOException {
        String string5 = ZkmUtils.dotsToSlashes(string1);
        if (enumerableMap.containsKey(string5)) {
            changeLogMapping1.logError(
                    "The class '"
                            + string1
                            + "' appears in the change log as a "
                            + string4
                            + " lookup class but that name is already used. Another name will be used. You must distribute this application as a whole. (1)"
            );
            return null;
        }

        if (ClassHierarchyNode.findNode(string5) != null) {
            ProgramClass programClass1 = ClassHierarchyNode.findProgramClass(string5);
            changeLogMapping1.logError(
                    "The class '"
                            + string1
                            + "' appears in the change log as a "
                            + string4
                            + " lookup class but that name is already used. Another name will be used. You must distribute this application as a whole."
                            + (programClass1 == null ? "" : " : " + programClass1.getCreationKind())
                            + " (2)"
                            + ""
            );
            return null;
        }

        String string6 = ClassFileBase.getPackagePath(string5);
        if (string3 != null) {
            String string7 = ZkmUtils.dotsToSlashes(string3);
            String string8 = changeLogMapping1.getNewPackageName(string7);
            if (string8 == null && changeLogMapping1.hasPackageMapping(string7)) {
                string8 = string7;
            }

            if (string8 == null || !string8.equals(string6)) {
                String string9 = "'"
                        + string2
                        + "' parameter in 'obfuscate' statement specifies package name '"
                        + string3
                        + "' but input change log '"
                        + changeLogMapping1.getChangeLogName()
                        + "' contains a '"
                        + string
                        + "' clause specifying package name '"
                        + ZkmUtils.slashesToDots(string6)
                        + "' which will be used.";
                changeLogMapping1.logWarning(string9);
            }
        }

        if (!set1.contains(string6)) {
            String string10 = ZkmUtils.toQuotedListString(set1);
            changeLogMapping1.logError(
                    "The class '"
                            + string1
                            + "' appears in the change log '"
                            + string
                            + "' clause as a "
                            + string4
                            + " lookup class but it is not in a suitable package. Another name will be used. You must distribute this application as a whole. : {"
                            + string10
                            + "}"
            );
            return null;
        } else {
            return string5;
        }
    }

    public static void findMinClassVersion(MutableInt mutableInt, MutableInt mutableInt1, ClassFileBase[] classFileBases, Integer integer) {
        int ba = Integer.MAX_VALUE;
        int bb = Integer.MAX_VALUE;

        for (ClassFileBase classFileBase : classFileBases) {
            int majorVersion = classFileBase.getMajorVersion();
            if (majorVersion < ba) {
                ba = majorVersion;
                bb = classFileBase.getMinorVersion();
            } else if (majorVersion == ba && classFileBase.getMinorVersion() < bb) {
                bb = classFileBase.getMinorVersion();
            }
        }

        if (integer != null) {
            int bd = JavaRuntimeVersion.toClassMajorVersion(integer);
            if (bd > ba) {
                ba = bd;
                bb = 0;
            }
        }

        mutableInt.setValue(ba);
        mutableInt1.setValue(bb);
    }

    public static Set partitionClassesByModule(Set set1, Set set2, Set set3, Set set4) throws ZkmClassNotFoundException {
        HashSet hashSet = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(set1.size()));
        Iterator iterator = set1.iterator();

        while (iterator.hasNext()) {
            ProgramClass programClass1 = (ProgramClass) iterator.next();
            if (!programClass1.isGenerated()) {
                ArrayList arrayList = new ArrayList();
                if (!programClass1.isMultiRelease()) {
                    hashSet.add(programClass1);
                } else {
                    Iterator iterator1 = programClass1.getAllVersions().iterator();

                    while (iterator1.hasNext()) {
                        ClassFileBase classFileBase = (ClassFileBase) iterator1.next();
                        arrayList.add((ProgramClass) classFileBase);
                    }

                    hashSet.addAll(arrayList);
                }

                boolean bl = false;
                boolean bl1 = false;
                Enumeration enumeration = programClass1.enumerateInputLocations();

                while (enumeration.hasMoreElements()) {
                    InputFileLocation inputFileLocation = (InputFileLocation) enumeration.nextElement();
                    if (inputFileLocation.isArchiveEntry()) {
                        if (inputFileLocation.getSourceArchive().hasModuleName()) {
                            bl = true;
                        } else {
                            bl1 = true;
                        }
                    } else {
                        bl1 = true;
                    }
                }

                if (bl1) {
                    if (programClass1.isMultiRelease()) {
                        set2.addAll(arrayList);
                        if (!bl) {
                            set3.addAll(arrayList);
                        }
                    } else {
                        set2.add(programClass1);
                        if (!bl) {
                            set3.add(programClass1);
                        }
                    }
                }
            }
        }

        iterator = set2.iterator();

        while (iterator.hasNext()) {
            ProgramClass programClass2 = (ProgramClass) iterator.next();
            set4.add(programClass2.getPackagePath());
        }

        return hashSet;
    }

    public static String describeLookupClassTriples(String string, ListMultimap listMultimap, Set set1) {
        String string1 = "\t" + string + " : Created special lookup classes as ";
        String string2 = ZkmStringUtils.pad("\t", 76, string1.length(), 32);
        StringBuilder stringBuilder = new StringBuilder();
        Iterator iterator = set1.iterator();

        while (iterator.hasNext()) {
            ObjectTriple objectTriple = (ObjectTriple) iterator.next();
            StringBuilder stringBuilder1;
            char bb;
            if (stringBuilder.length() == 0) {
                stringBuilder.append(string1);
                stringBuilder1 = stringBuilder;
                bb = '\'';
            } else {
                stringBuilder.append(string2);
                stringBuilder1 = stringBuilder;
                bb = '\'';
            }

            stringBuilder1.append(bb);
            stringBuilder.append(((ProgramClass) objectTriple.getFirst()).getDottedClassName());
            stringBuilder.append("', '");
            stringBuilder.append(((ProgramClass) objectTriple.getSecond()).getDottedClassName());
            stringBuilder.append("' and '");
            stringBuilder.append(((ProgramClass) objectTriple.getThird()).getDottedClassName());
            stringBuilder.append('\'');
            List list1 = listMultimap.getValues(objectTriple);
            if (list1 != null) {
                for (int i = 0; i < list1.size(); i++) {
                    SourceArchive sourceArchive1 = (SourceArchive) list1.get(i);
                    if (i == 0) {
                        stringBuilder.append(" in module" + (list1.size() > 1 ? "s '" : " '"));
                    }

                    stringBuilder.append(sourceArchive1.getModuleName());
                    if (i < list1.size() - 1) {
                        stringBuilder.append("', ");
                    } else {
                        stringBuilder.append('\'');
                    }
                }

                stringBuilder.append(ZkmUtils.LINE_SEPARATOR);
            } else {
                stringBuilder.append(ZkmUtils.LINE_SEPARATOR);
            }
        }

        return stringBuilder.toString();
    }

    public static String generateLookupClassName(
            String string,
            String string1,
            Collection collection1,
            Collection collection2,
            EnumerableMap enumerableMap,
            EnumerableMap enumerableMap1,
            EnumerableMap enumerableMap2,
            Set set1,
            String string2,
            boolean bl,
            ClasspathClassLoader classpathClassLoader1,
            List list1,
            Boolean boolean1
    ) throws ZkmException, IOException {
        HashSet hashSet = ZkmUtils.createHashSet();
        Iterator iterator = collection2.iterator();

        while (iterator.hasNext()) {
            ProgramClass programClass1 = (ProgramClass) iterator.next();
            hashSet.add(programClass1.getPackagePath());
        }

        if (string != null && hashSet.contains(ClassFileBase.getPackagePath(string))) {
            return string;
        }

        String string8 = null;
        if (string1 != null && hashSet.contains(string1)) {
            string8 = string1;
        }

        int bb = collection2.size();
        if (string8 == null) {
            HashSet hashSet1 = null;
            if (collection1 != null) {
                hashSet1 = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(collection1.size()));
                Iterator iterator1 = collection1.iterator();

                while (iterator1.hasNext()) {
                    String string4 = (String) iterator1.next();
                    String string5 = (String) ZkmUtils.mapOrSelf(string4, enumerableMap);
                    hashSet1.add(string5);
                }
            }

            CountingBag countingBag = new CountingBag(bb);
            Iterator iterator3 = collection2.iterator();

            while (iterator3.hasNext()) {
                ProgramClass programClass3 = (ProgramClass) iterator3.next();
                if (hashSet1 == null || hashSet1.contains(programClass3.getPackagePath())) {
                    programClass3.countReferencedProgramClasses(countingBag);
                    countingBag.add(programClass3);
                }
            }

            iterator3 = countingBag.iterator();

            while (iterator3.hasNext()) {
                ProgramClass programClass4 = (ProgramClass) iterator3.next();
                if (!collection2.contains(programClass4)) {
                    iterator3.remove();
                }
            }

            CountingBag countingBag1 = new CountingBag();
            CountingBag countingBag2 = new CountingBag();
            Iterator iterator2 = countingBag.iterator();

            while (iterator2.hasNext()) {
                ProgramClass programClass2 = (ProgramClass) iterator2.next();
                String string6 = programClass2.getPackagePath();
                countingBag1.add(string6);
                int ba = countingBag1.getCount(string6) + countingBag.getCount(programClass2) - 1;
                String string3 = string6;
                countingBag1.addCount(string3, ba);
                if (programClass2.isPublic()) {
                    countingBag2.add(string6);
                    ba = countingBag2.getCount(string6) + countingBag.getCount(programClass2) - 1;
                    string3 = string6;
                    countingBag2.addCount(string3, ba);
                }
            }

            List list2 = countingBag2.getSortedByCount();
            if (list2.size() > 0) {
                string8 = (String) list2.get(0);
            } else {
                List list3 = countingBag1.getSortedByCount();
                if (list3.size() > 0) {
                    string8 = (String) list3.get(0);
                }
            }

            if (string8 == null) {
                string8 = "";
            }
        }

        TwoKeyMap twoKeyMap = new TwoKeyMap();
        HashSet hashSet2 = ZkmUtils.createHashSet();
        HashSet hashSet3 = ZkmUtils.createHashSet();
        Iterator iterator4 = collection2.iterator();

        while (iterator4.hasNext()) {
            ProgramClass programClass5 = (ProgramClass) iterator4.next();
            twoKeyMap.putValue(programClass5.getPackagePath(), programClass5.getSimpleName(), programClass5);
            String string9 = programClass5.getLowerCaseName();
            hashSet2.add(string9);
            hashSet3.add(ClassFileBase.stripPackage(string9));
        }

        Map map1 = twoKeyMap.getInnerMap(string8);
        SequentialNameGenerator sequentialNameGenerator = new SequentialNameGenerator(
                SequentialNameGenerator.LOWER_UNDERSCORE_CHARS, SequentialNameGenerator.LOWER_DIGIT_UNDERSCORE_CHARS, list1, boolean1
        );
        String string10 = null;

        do {
            String string11 = sequentialNameGenerator.nextPrefixedName(string2);
            String string7 = (string8.length() > 0 ? string8 + '/' : "") + string11;
            if ((!bl || !hashSet3.contains(string11))
                    && !set1.contains(string7)
                    && (enumerableMap1 == null || !enumerableMap1.containsKey(string7))
                    && (enumerableMap2 == null || !enumerableMap2.containsKey(string7))
                    && (map1 == null || !map1.containsKey(string11))
                    && !hashSet2.contains(string7.toLowerCase())
                    && ClassHierarchyNode.findClassFile(string7) == null
                    && classpathClassLoader1.findClassFileQuietly(string7) == null) {
                string10 = string7;
            }
        } while (string10 == null);

        set1.add(string10);
        return string10;
    }

    public static void computeCommonPackages(SetMultiMap setMultiMap, SetMultiMap setMultiMap1, SetMultiMap setMultiMap2) {
        SetMultiMap setMultiMap3 = new SetMultiMap();
        Iterator iterator = setMultiMap2.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            SourceArchive sourceArchive1 = (SourceArchive) entry.getKey();
            Set set1 = (Set) entry.getValue();
            setMultiMap.addValues(sourceArchive1, set1);
            Iterator iterator1 = set1.iterator();

            while (iterator1.hasNext()) {
                String string = (String) iterator1.next();
                setMultiMap3.addValue(string, sourceArchive1);
            }
        }

        iterator = setMultiMap3.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry1 = (Entry) iterator.next();
            Set set3 = (Set) entry1.getValue();
            if (set3.size() > 1) {
                Iterator iterator3 = set3.iterator();
                HashSet hashSet = ZkmUtils.createHashSetFrom(setMultiMap.getValues(iterator3.next()));

                while (iterator3.hasNext()) {
                    SourceArchive sourceArchive3 = (SourceArchive) iterator3.next();
                    Set set2 = setMultiMap.getValues(sourceArchive3);
                    hashSet.retainAll(set2);
                }

                Iterator iterator4 = set3.iterator();

                while (iterator4.hasNext()) {
                    SourceArchive sourceArchive4 = (SourceArchive) iterator4.next();
                    setMultiMap.putValueSet(sourceArchive4, ZkmUtils.createHashSetFrom(hashSet));
                }

                iterator4 = set3.iterator();

                while (iterator4.hasNext()) {
                    SourceArchive sourceArchive5 = (SourceArchive) iterator4.next();
                    Iterator iterator2 = set3.iterator();

                    while (iterator2.hasNext()) {
                        SourceArchive sourceArchive2 = (SourceArchive) iterator2.next();
                        if (sourceArchive5 != sourceArchive2) {
                            setMultiMap1.addValue(sourceArchive5, sourceArchive2);
                        }
                    }
                }
            }
        }
    }

    private LookupClassFactory() {
    }
}
