package com.zelix.klassmaster.obfuscator.rename;

import com.zelix.klassmaster.changelog.ChangeLogMapping;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClassPathResolver;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.config.MixedCaseNamesMode;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.exclude.NameExclusionSet;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.SystemEnvironmentConstants;
import com.zelix.klassmaster.util.TwoKeyMap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Map.Entry;

public class RootPackageNode extends PackageTreeNodeBase {
    public void buildFromClasses(ClassFileBase[] classFileBases) {
        super.children.clear();

        for (int i = 0; i < classFileBases.length; i++) {
            this.addClassPackage(classFileBases[i].getClassName());
        }

        this.markArchiveOnlyPackages(classFileBases);
    }

    public void addClassPackageWithFlags(String string, Map map1) {
        int ba = string.lastIndexOf(47);
        if (ba != -1) {
            String[] strings = splitPackagePath(string.substring(0, ba));
            if (strings.length > 0) {
                this.addPackagePath(0, strings, map1);
            }
        }
    }

    public HashMap assignPackageNames(
            NameExclusionSet nameExclusionSet,
            ChangeLogMapping changeLogMapping1,
            PackageNameGeneratorBase packageNameGeneratorBase,
            ListMultimap listMultimap,
            boolean bl,
            Map map1,
            boolean bl1
    ) throws IOException {
        java.lang.Object[] packageTreeNodes = null;
        List list1 = this.getAllPackagePaths();
        HashMap hashMap = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(list1.size()));
        ListMultimap listMultimap1 = new ListMultimap(list1.size());

        for (int i = 0; i < list1.size(); i++) {
            String string = (String) list1.get(i);
            if (nameExclusionSet.isPackageExcluded(string)) {
                hashMap.put(string, string);
                listMultimap1.addValue(string, string);
            } else if (changeLogMapping1 != null && changeLogMapping1.hasPackageMapping(string)) {
                String string3 = changeLogMapping1.getNewPackageName(string);
                if (string3 != null) {
                    hashMap.put(string, string3);
                    listMultimap1.addValue(string3, string);
                } else {
                    hashMap.put(string, string);
                    listMultimap1.addValue(string, string);
                }
            } else if (map1 != null && map1.containsKey(string)) {
                NamedFlag namedFlag = (NamedFlag) map1.get(string);
                if (!namedFlag.isBlocked()) {
                    String string1 = namedFlag.getName();
                    hashMap.put(string, string1);
                    listMultimap1.addValue(string1, string);
                }
            }
        }

        TwoKeyMap twoKeyMap = new TwoKeyMap();
        Enumeration enumeration = listMultimap1.keys();

        while (enumeration.hasMoreElements()) {
            String string4 = (String) enumeration.nextElement();
            List list3 = listMultimap1.getValues(string4);

            for (int i = 0; i < list3.size(); i++) {
                String string2 = (String) list3.get(i);
                List list2 = listMultimap.getValues(string2);
                if (list2 != null) {
                    for (int j = 0; j < list2.size(); j++) {
                        ProgramClass programClass1 = (ProgramClass) list2.get(j);
                        if (nameExclusionSet.isClassExcluded(programClass1)) {
                            twoKeyMap.putValue(string4, programClass1.getSimpleName(), programClass1);
                        }
                    }
                }
            }
        }

        if (!bl) {
            label76:
            {
                packageTreeNodes = super.children.toArray(new PackageTreeNode[super.children.size()]);
                List list4;
                if (!bl1) {
                    if (!HiddenOptionFlags.RANDOMIZE_OBFUSCATION) {
                        Arrays.sort(packageTreeNodes);
                        break label76;
                    }

                    list4 = Arrays.asList(packageTreeNodes);
                } else {
                    list4 = Arrays.asList(packageTreeNodes);
                }

                ZkmUtils.shuffleList(list4, ZkmUtils.createRandom(8));
            }

            for (int i = 0; i < packageTreeNodes.length; i++) {
                PackageTreeNode packageTreeNode = ((com.zelix.klassmaster.obfuscator.rename.PackageTreeNode) (packageTreeNodes[i]));
                packageTreeNode.assignNewNamesRecursively(
                        nameExclusionSet, changeLogMapping1, packageNameGeneratorBase, hashMap, listMultimap1, twoKeyMap, listMultimap, bl1
                );
            }
        } else {
            Map map2 = this.getNodesByPath();
            Iterator iterator = map2.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                String string5 = (String) entry.getKey();
                String string6 = (String) hashMap.get(string5);
                if (string6 == null) {
                    NamedFlag namedFlag1 = (NamedFlag) map1.get(string5);
                    String string7 = namedFlag1.getName();
                    PackageTreeNodeBase packageTreeNodeBase = (PackageTreeNodeBase) map2.get(string7);
                    if (packageTreeNodeBase == null) {
                    }

                    boolean archiveOnly = ((PackageTreeNode) entry.getValue()).isArchiveOnly();
                    ChangeLogMapping changeLogMapping2 = changeLogMapping1;
                    string6 = packageNameGeneratorBase.generatePackageName(
                            string7, archiveOnly, false, listMultimap1, twoKeyMap, (Set) null, changeLogMapping2, nameExclusionSet
                    );
                    if (string7.length() > 0) {
                        string6 = string7 + "/" + string6;
                    }

                    hashMap.put(string5, string6);
                    listMultimap1.addValue(string6, string5);
                }
            }
        }

        this.rebuildFromRenamedPackages(listMultimap1);
        return hashMap;
    }

    public void checkChangeLogPackageClashes(TwoKeyMap twoKeyMap, NameExclusionSet nameExclusionSet, ChangeLogMapping changeLogMapping1) throws IOException {
        if (changeLogMapping1 != null) {
            ArrayList arrayList = changeLogMapping1.getSortedPackageNames();
            int ba = arrayList.size();

            for (int i = 0; i < ba; i++) {
                String string = (String) arrayList.get(i);
                String string1 = changeLogMapping1.getNewPackageName(string);
                if (string1 == null) {
                    string1 = string;
                }

                if (!nameExclusionSet.isPackageExcluded(string) && nameExclusionSet.isClassNameExcluded(string1)) {
                    changeLogMapping1.reportFatalError(
                            "Package \""
                                    + ZkmUtils.slashesToDots(string)
                                    + "\" is mapped to \""
                                    + ZkmUtils.slashesToDots(string1)
                                    + "\" but this would result in a clash with class \""
                                    + ZkmUtils.slashesToDots(string1)
                                    + "\" that will not be renamed."
                    );
                }
            }

            HashMap hashMap = ZkmUtils.createHashMap();
            Enumeration enumeration = twoKeyMap.keys();

            while (enumeration.hasMoreElements()) {
                String string7 = (String) enumeration.nextElement();
                if (nameExclusionSet.isPackageExcluded(string7) || changeLogMapping1.hasPackageMapping(string7) && !changeLogMapping1.hasNewPackageName(string7)) {
                    Map map1 = twoKeyMap.getInnerMap(string7);
                    if (map1 != null) {
                        Iterator iterator = map1.entrySet().iterator();

                        while (iterator.hasNext()) {
                            Entry entry = (Entry) iterator.next();
                            String string2 = (String) entry.getValue();
                            hashMap.put(string2, string2);
                        }
                    }
                }
            }

            enumeration = twoKeyMap.keys();

            while (enumeration.hasMoreElements()) {
                String string8 = (String) enumeration.nextElement();
                if (!nameExclusionSet.isPackageExcluded(string8) && changeLogMapping1.hasPackageMapping(string8) && changeLogMapping1.hasNewPackageName(string8)) {
                    String string9 = changeLogMapping1.getNewPackageName(string8);
                    Map map2 = twoKeyMap.getInnerMap(string8);
                    if (map2 != null) {
                        Iterator iterator1 = map2.entrySet().iterator();

                        while (iterator1.hasNext()) {
                            Entry entry1 = (Entry) iterator1.next();
                            String string3 = (String) entry1.getKey();
                            String string4 = (String) entry1.getValue();
                            String string5 = string9 + string3;
                            String string6 = ((java.lang.String) (hashMap.put(string5, string4)));
                            if (string6 != null) {
                                hashMap.put(string5, string6);
                                changeLogMapping1.reportFatalError(
                                        "Package \""
                                                + ZkmUtils.slashesToDots(string8)
                                                + "\" is mapped to \""
                                                + ZkmUtils.slashesToDots(string9)
                                                + "\" but this would result in \""
                                                + string6
                                                + "\" clashing with \""
                                                + string4
                                                + "\""
                                );
                            }
                        }
                    }
                }
            }
        }
    }

    public Map buildCollapseMappings(
            ListMultimap listMultimap,
            NameExclusionSet nameExclusionSet,
            ChangeLogMapping changeLogMapping1,
            String string,
            TwoKeyMap twoKeyMap,
            ScriptEnvironment scriptEnvironment1
    ) throws ZkmException, IOException {
        HashMap hashMap = this.buildCollapseTargetMap(nameExclusionSet, changeLogMapping1, string);
        HashMap hashMap1 = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(hashMap.size()));
        ListMultimap listMultimap1 = new ListMultimap();
        Iterator iterator = hashMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            listMultimap1.addValue(entry.getValue(), entry.getKey());
        }

        Enumeration enumeration = listMultimap1.keys();

        while (enumeration.hasMoreElements()) {
            String string2 = (String) enumeration.nextElement();
            Set set1 = null;
            if (changeLogMapping1 != null) {
                set1 = changeLogMapping1.getPackagesMappedTo(string2);
            }

            if (set1 == null) {
                set1 = ZkmUtils.createHashSet();
            }

            set1.add(string2);
            ObservableHolder observableHolder = new ObservableHolder();
            HashMap hashMap2 = ZkmUtils.createHashMap();
            HashMap hashMap3 = ZkmUtils.createHashMap();
            Iterator iterator1 = set1.iterator();

            while (iterator1.hasNext()) {
                String string1 = (String) iterator1.next();
                List list1 = listMultimap.getValues(string1);
                if (list1 != null && list1.size() > 0) {
                    boolean bl = this.checkSimpleNameClashes(hashMap2, list1, nameExclusionSet, changeLogMapping1, observableHolder);
                    if (bl) {
                        changeLogMapping1.reportFatalError("Package mapping conflicts with class name exclusions : " + (String) observableHolder.getValue());
                    }
                }

                Map map1 = twoKeyMap.getInnerMap(string1);
                if (map1 != null && map1.size() > 0) {
                    boolean bl1 = this.checkNonClassFileClashes(hashMap3, map1, observableHolder);
                    if (bl1) {
                        changeLogMapping1.reportFatalError("Package mapping conflict : " + (String) observableHolder.getValue());
                    }
                }
            }

            List list2 = listMultimap1.getValues(string2);
            Collections.sort(list2);
            if (list2.size() > 0) {
                for (int i = 0; i < list2.size(); i++) {
                    String string3 = (String) list2.get(i);
                    List list3 = listMultimap.getValues(string3);
                    NamedFlag namedFlag;
                    if (list3 != null) {
                        boolean bl2 = this.checkSimpleNameClashes(hashMap2, list3, nameExclusionSet, changeLogMapping1, observableHolder);
                        if (bl2) {
                            namedFlag = new NamedFlag(string2, true);
                            hashMap1.put(string3, namedFlag);
                            scriptEnvironment1.logWarning(
                                    "Cannot collapse package '"
                                            + ZkmUtils.slashesToDots(string3)
                                            + "' into '"
                                            + ZkmUtils.slashesToDots(string2)
                                            + "' because : \""
                                            + (String) observableHolder.getValue()
                                            + "\""
                            );
                        } else {
                            namedFlag = new NamedFlag(string2);
                            hashMap1.put(string3, namedFlag);
                        }
                    } else {
                        namedFlag = new NamedFlag(string2);
                        hashMap1.put(string3, namedFlag);
                    }

                    Map map2 = twoKeyMap.getInnerMap(string3);
                    if (map2 != null && map2.size() > 0) {
                        boolean bl3 = this.checkNonClassFileClashes(hashMap3, map2, observableHolder);
                        if (bl3) {
                            if (!namedFlag.isBlocked()) {
                                namedFlag.setBlocked();
                            }

                            scriptEnvironment1.logWarning(
                                    "Cannot collapse package '"
                                            + ZkmUtils.slashesToDots(string3)
                                            + "' into '"
                                            + ZkmUtils.slashesToDots(string2)
                                            + "' because : \""
                                            + (String) observableHolder.getValue()
                                            + "\""
                            );
                        }
                    }
                }
            } else {
                hashMap1.put(list2.get(0), new NamedFlag(string2));
            }
        }

        return hashMap1;
    }

    @Override
    public void indexInto(Object object) {
    }

    public Map getNodesByPath() {
        HashMap hashMap = ZkmUtils.createHashMap();
        this.indexAllNodes(hashMap);
        return hashMap;
    }

    @Override
    public String getPackagePath() {
        return "";
    }

    public void reportUnknownChangeLogPackages(ChangeLogMapping changeLogMapping1, Set set1) throws IOException {
        if (changeLogMapping1 != null) {
            ListIterator listIterator = new ArrayList(set1).listIterator();

            while (listIterator.hasNext()) {
                String string = (String) listIterator.next();
                String[] strings = getParentPackagePaths(string);

                for (int i = 0; i < strings.length; i++) {
                    set1.add(strings[i]);
                }
            }

            Map map1 = this.getNodesByPath();
            ArrayList arrayList = changeLogMapping1.getSortedPackageNames();

            for (int i = 0; i < arrayList.size(); i++) {
                String string1 = (String) arrayList.get(i);
                if (!map1.containsKey(string1) && !set1.contains(string1)) {
                    changeLogMapping1.removePackageMapping(string1);
                }
            }
        }
    }

    public static String[] getParentPackagePaths(String string) {
        String[] strings = splitPackagePath(string);
        int ba = Math.max(0, strings.length - 1);
        String[] strings1 = new String[ba];
        StringBuffer stringBuffer = new StringBuffer();

        for (int i = 0; i < ba; i++) {
            if (i > 0) {
                stringBuffer.append('/');
            }

            stringBuffer.append(strings[i]);
            strings1[i] = stringBuffer.toString();
        }

        return strings1;
    }

    public void markArchiveOnlyPackages(ClassFileBase[] classFileBases) {
        Map map1 = this.getNodesByPath();
        ListMultimap listMultimap = new ListMultimap(map1.size());

        for (ClassFileBase classFileBase : classFileBases) {
            PackageTreeNode packageTreeNode = (PackageTreeNode) map1.get(classFileBase.getPackagePath());
            if (packageTreeNode != null) {
                listMultimap.addValue(packageTreeNode, classFileBase);

                for (PackageTreeNodeBase packageTreeNodeBase = packageTreeNode.parent;
                     !packageTreeNodeBase.isRoot();
                     packageTreeNodeBase = ((PackageTreeNode) packageTreeNodeBase).parent
                ) {
                    listMultimap.addValue((PackageTreeNode) packageTreeNodeBase, classFileBase);
                }
            }
        }

        Iterator iterator = listMultimap.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            PackageTreeNode packageTreeNode1 = (PackageTreeNode) entry.getKey();
            boolean bl = true;
            Iterator iterator1 = ((List) entry.getValue()).iterator();

            while (iterator1.hasNext()) {
                ClassFileBase classFileBase1 = (ClassFileBase) iterator1.next();
                if (!classFileBase1.isFromArchive()) {
                    bl = false;
                    break;
                }
            }

            packageTreeNode1.setArchiveOnly(bl);
        }
    }

    public HashMap renamePackages(
            Enumeration enumeration,
            NameExclusionSet nameExclusionSet,
            ChangeLogMapping changeLogMapping1,
            boolean bl,
            Boolean boolean1,
            Boolean boolean2,
            String string,
            MixedCaseNamesMode mixedCaseNamesMode1,
            ClassPathResolver classPathResolver1,
            boolean bl1,
            String string1,
            Iterator iterator,
            List list1,
            ScriptEnvironment scriptEnvironment1
    ) throws ZkmException, IOException {
        TwoKeyMap twoKeyMap = this.groupNonClassFilesByPackage(iterator);
        this.checkChangeLogPackageClashes(twoKeyMap, nameExclusionSet, changeLogMapping1);
        Map map1 = null;
        Map map2 = this.getNodesByPath();
        ListMultimap listMultimap = new ListMultimap();

        while (enumeration.hasMoreElements()) {
            ProgramClass programClass1 = (ProgramClass) enumeration.nextElement();
            String string2 = programClass1.getPackagePath();
            listMultimap.addValue(string2, programClass1);
            if (programClass1.isPackageReferencedFromXml()) {
                PackageTreeNode packageTreeNode = (PackageTreeNode) map2.get(string2);
                if (packageTreeNode != null) {
                    packageTreeNode.getTopLevelAncestor().setRequireLowerCaseStart();
                }
            }
        }

        if (bl1) {
            map1 = this.buildCollapseMappings(listMultimap, nameExclusionSet, changeLogMapping1, string1, twoKeyMap, scriptEnvironment1);
        }

        PackageNameGenerator packageNameGenerator = new PackageNameGenerator(classPathResolver1, boolean1, boolean2, bl, string, mixedCaseNamesMode1, list1);
        HashMap hashMap = this.assignPackageNames(nameExclusionSet, changeLogMapping1, packageNameGenerator, listMultimap, bl1, map1, bl);
        if (changeLogMapping1 != null) {
            ArrayList arrayList = changeLogMapping1.getSortedPackageNames();

            for (int i = 0; i < arrayList.size(); i++) {
                String string3 = (String) arrayList.get(i);
                if (!hashMap.containsKey(string3)) {
                    String string4 = changeLogMapping1.getNewPackageName(string3);
                    if (string4 == null) {
                        string4 = string3;
                    }

                    hashMap.put(string3, string4);
                }
            }
        }

        return hashMap;
    }

    public void addClassPackage(String string) {
        this.addClassPackageWithFlags(string, (Map) null);
    }

    public boolean checkSimpleNameClashes(
            Map map1, List list1, NameExclusionSet nameExclusionSet, ChangeLogMapping changeLogMapping1, ObservableHolder observableHolder
    ) throws ZkmException, IOException {
        boolean bl = false;
        ArrayList arrayList = new ArrayList();

        for (int i = 0; i < list1.size(); i++) {
            ProgramClass programClass1 = (ProgramClass) list1.get(i);
            String string = programClass1.getClassName();
            if (changeLogMapping1 != null && changeLogMapping1.hasClassMapping(string)) {
                String string2 = changeLogMapping1.getNewClassName(string);
                if (string2 == null) {
                    string2 = string;
                }

                String string3 = ClassFileBase.stripPackage(string2);
                ProgramClass programClass3 = ((com.zelix.klassmaster.classfile.ProgramClass) (map1.put(string3, programClass1)));
                if (programClass3 != null && programClass3 != programClass1) {
                    map1.put(string3, programClass3);
                    observableHolder.setValue(
                            "Unqualified class names for classes '"
                                    + programClass3.getDottedClassName()
                                    + "' and '"
                                    + programClass1.getDottedClassName()
                                    + "' would clash (A)"
                    );
                    bl = true;
                    break;
                }

                arrayList.add(string3);
            } else if (nameExclusionSet.isClassExcluded(programClass1) || programClass1.getSimpleName().equals("package-info")) {
                String string1 = programClass1.getSimpleName();
                ProgramClass programClass2 = ((com.zelix.klassmaster.classfile.ProgramClass) (map1.put(programClass1.getSimpleName(), programClass1)));
                if (programClass2 != null && programClass2 != programClass1) {
                    map1.put(string1, programClass2);
                    observableHolder.setValue(
                            "Unqualified class names for classes '"
                                    + programClass2.getDottedClassName()
                                    + "' and '"
                                    + programClass1.getDottedClassName()
                                    + "' would clash (B)"
                    );
                    bl = true;
                    break;
                }

                arrayList.add(string1);
            }
        }

        if (bl) {
            for (int i = 0; i < arrayList.size(); i++) {
                map1.remove(arrayList.get(i));
            }
        }

        return bl;
    }

    public static String normalizePackageName(String string, ObservableHolder observableHolder) throws ZkmException, IOException {
        String string1 = string.trim();
        String string2 = null;
        if (string1.length() > 0) {
            string1 = string1.replace('.', '/');
            if (string1.startsWith("/")) {
                string2 = "Leading package separator character";
            } else if (string1.indexOf("//") > -1) {
                string2 = "Duplicate package separator character";
            } else {
                if (string1.endsWith("/")) {
                    string1 = string1.substring(0, string1.length() - 1);
                }

                StringTokenizer stringTokenizer = new StringTokenizer(string1, "/");

                while (stringTokenizer.hasMoreTokens() && string2 == null) {
                    String string3 = stringTokenizer.nextToken();

                    for (int i = 0; i < string3.length(); i++) {
                        if (!Character.isJavaIdentifierPart(string3.charAt(i))) {
                            string2 = "Invalid character '" + string3.charAt(i) + "'";
                            break;
                        }
                    }
                }
            }
        }

        observableHolder.setValue(string2);
        return string1;
    }

    public boolean hasNoChildren() {
        return super.children == null || super.children.size() == 0;
    }

    public static String[] splitPackagePath(String string) {
        StringTokenizer stringTokenizer = new StringTokenizer(string, "/");
        int ba = stringTokenizer.countTokens();
        String[] strings = new String[ba];

        for (int i = 0; i < ba; i++) {
            strings[i] = stringTokenizer.nextToken();
        }

        return strings;
    }

    public void rebuildFromRenamedPackages(ListMultimap listMultimap) {
        Map map1 = this.getNodesByPath();
        super.children.clear();
        TreeMap treeMap = new TreeMap();
        Iterator iterator = listMultimap.keySet().iterator();

        while (iterator.hasNext()) {
            String string = (String) iterator.next();
            boolean bl = true;
            Iterator iterator1 = listMultimap.getValues(string).iterator();

            while (iterator1.hasNext()) {
                String string1 = (String) iterator1.next();
                if (!((PackageTreeNode) map1.get(string1)).isArchiveOnly()) {
                    bl = false;
                }
            }

            treeMap.put(string, bl);
        }

        iterator = treeMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            this.addClassPackageWithFlags((String) entry.getKey() + '/', treeMap);
        }
    }

    public HashMap buildRetainedPackageMap(ChangeLogMapping changeLogMapping1) throws IOException {
        HashMap hashMap = ZkmUtils.createHashMap();
        if (changeLogMapping1 != null) {
            ArrayList arrayList = changeLogMapping1.getSortedPackageNames();
            Iterator iterator = arrayList.iterator();

            while (iterator.hasNext()) {
                String string = (String) iterator.next();
                String string1 = changeLogMapping1.getNewPackageName(string);
                if (string1 == null) {
                    string1 = string;
                }

                hashMap.put(string, string1);
            }
        }

        List list1 = this.getAllPackagePaths();
        Iterator iterator1 = list1.iterator();

        while (iterator1.hasNext()) {
            String string2 = (String) iterator1.next();
            if (!hashMap.containsKey(string2)) {
                hashMap.put(string2, string2);
            }
        }

        return hashMap;
    }

    @Override
    public boolean isRoot() {
        return true;
    }

    public List getAllPackagePaths() {
        ArrayList arrayList = new ArrayList();
        this.collectAllPaths(arrayList);
        return arrayList;
    }

    public TwoKeyMap groupNonClassFilesByPackage(Iterator iterator) {
        TwoKeyMap twoKeyMap = new TwoKeyMap();
        List list1 = this.getAllPackagePaths();
        Collections.sort(list1, Collections.reverseOrder());

        while (iterator.hasNext()) {
            String string = (String) iterator.next();
            string = string.replace(SystemEnvironmentConstants.PATH_SEPARATOR_CHAR, '/');
            boolean bl = false;

            for (int i = 0; i < list1.size(); i++) {
                String string1 = (String) list1.get(i);
                if (string.startsWith(string1) && string.length() > string1.length() && string.charAt(string1.length()) == '/') {
                    twoKeyMap.putValue(string1, string.substring(string1.length()), string);
                    bl = true;
                    break;
                }
            }

            if (!bl) {
                String string2;
                if (string.charAt(0) == '/') {
                    string2 = string;
                } else {
                    string2 = '/' + string;
                }

                twoKeyMap.putValue("", string2, string);
            }
        }

        return twoKeyMap;
    }

    public HashMap buildCollapseTargetMap(NameExclusionSet nameExclusionSet, ChangeLogMapping changeLogMapping1, String string) throws IOException {
        HashMap hashMap = ZkmUtils.createHashMap();
        Iterator iterator = super.children.iterator();

        while (iterator.hasNext()) {
            PackageTreeNode packageTreeNode = (PackageTreeNode) iterator.next();
            String string1 = packageTreeNode.getPackagePath();
            String string2;
            if (changeLogMapping1 != null && changeLogMapping1.hasPackageMapping(string1)) {
                String string3 = changeLogMapping1.getNewPackageName(string1);
                if (string3 != null) {
                    string2 = string3;
                } else {
                    string2 = string1;
                }
            } else if (nameExclusionSet.isPackageExcluded(string1)) {
                string2 = string1;
            } else {
                string2 = string;
            }

            hashMap.put(string1, string2);
            packageTreeNode.collectRetainedPackageNames(hashMap, nameExclusionSet, changeLogMapping1, string2);
        }

        return hashMap;
    }

    public boolean checkNonClassFileClashes(Map map1, Map map2, ObservableHolder observableHolder) throws ZkmException, IOException {
        boolean bl = false;
        ArrayList arrayList = new ArrayList();
        Iterator iterator = map2.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            String string = (String) entry.getKey();
            String string1 = (String) entry.getValue();
            String string2 = ((java.lang.String) (map1.put(string, string1)));
            if (string2 != null && !string2.equals(string1)) {
                bl = true;
                observableHolder.setValue("Non-class files '" + string2 + "' and '" + string1 + "' would clash");
                map1.put(string, string2);

                for (int i = 0; i < arrayList.size(); i++) {
                    map1.remove(arrayList.get(i));
                }
            } else {
                arrayList.add(string);
            }
        }

        return bl;
    }
}
