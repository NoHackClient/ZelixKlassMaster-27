package com.zelix.klassmaster.obfuscator.rename;

import com.zelix.klassmaster.changelog.ChangeLogMapping;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.obfuscator.exclude.NameExclusionSet;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.Triple;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public abstract class ClassNameGenerator {
    public static final String[] RESERVED_DEVICE_NAMES = new String[]{
            "CON",
            "PRN",
            "AUX",
            "CLOCK$",
            "NUL",
            "COM0",
            "COM1",
            "COM2",
            "COM3",
            "COM4",
            "COM5",
            "COM6",
            "COM7",
            "COM8",
            "COM9",
            "LPT0",
            "LPT1",
            "LPT2",
            "LPT3",
            "LPT4",
            "LPT5",
            "LPT6",
            "LPT7",
            "LPT8",
            "LPT9"
    };
    public static final Set RESERVED_DEVICE_NAME_SET = ZkmUtils.createHashSetFrom(Arrays.asList(RESERVED_DEVICE_NAMES));
    public final Set assignedNames = ZkmUtils.createHashSet();
    public final Set fileClassLowerCaseNames = ZkmUtils.createHashSet();
    public final Set allLowerCaseNames = ZkmUtils.createHashSet();
    public ClassRenameClashChecker clashChecker;
    public final HashMap packageRenameMap;
    public final Map packageNodes;
    public final int keepInnerClassInfoMode;
    public final int keepGenericsInfoMode;
    public final boolean randomizeNames;
    public final Set fixedClasses;
    public final Set fixedClassNames;
    public final HashMap classRenameMap;
    public final Map classesByName;
    public final NameExclusionSet exclusionSet;
    public final ChangeLogMapping changeLogMapping;
    public final ListMultimap newToOldPackages;

    public String getPackageOnlyRename(ClassHierarchyNode classHierarchyNode) {
        String string = classHierarchyNode.getClassName();
        int ba = string.lastIndexOf(47);
        if (ba != -1) {
            String string1 = this.getRenamedPackagePrefix(string);
            String string2 = string.substring(ba + 1);
            String string3 = string1 + string2;
            return string3.equals(string) ? null : string3;
        } else {
            return null;
        }
    }

    public final boolean checkNameAvailable(String string, String string1, boolean bl, boolean bl1, boolean bl2, MutableInt mutableInt) throws IOException {
        if (this.changeLogMapping == null || !this.changeLogMapping.isNewClassName(string1) && !this.changeLogMapping.isLookupClass(string1)) {
            if (this.fixedClassNames.contains(string1)) {
                mutableInt.setValue(2);
                return false;
            }

            if (this.assignedNames.contains(string1)) {
                mutableInt.setValue(3);
                return false;
            }

            if (this.packageNodes.get(string1) != null) {
                mutableInt.setValue(4);
                return false;
            }

            if (!bl && string.equals(string1)) {
                mutableInt.setValue(5);
                return false;
            }

            boolean bl3;
            if (string1.length() == 1) {
                if (ConstantPoolEntry.isPrimitiveDescriptor(string1)) {
                    mutableInt.setValue(6);
                    return false;
                }

                bl3 = HiddenOptionFlags.ALWAYS_CHECK_RESERVED_CLASS_NAMES;
            } else {
                bl3 = HiddenOptionFlags.ALWAYS_CHECK_RESERVED_CLASS_NAMES;
            }

            Set set1;
            if (!bl3 && bl1) {
                set1 = this.fileClassLowerCaseNames;
            } else {
                if (RESERVED_DEVICE_NAME_SET.contains(ClassFileBase.stripPackage(string1).toUpperCase())) {
                    mutableInt.setValue(7);
                    return false;
                }

                set1 = this.fileClassLowerCaseNames;
            }

            if (set1.contains(string1.toLowerCase())) {
                mutableInt.setValue(8);
                return false;
            } else if (!bl1 && this.allLowerCaseNames.contains(string1.toLowerCase())) {
                mutableInt.setValue(9);
                return false;
            } else if (bl2 && !Character.isUpperCase(ClassFileBase.stripPackage(string1).charAt(0))) {
                mutableInt.setValue(10);
                return false;
            } else {
                mutableInt.setValue(0);
                return true;
            }
        } else {
            mutableInt.setValue(1);
            return false;
        }
    }

    public ClassNameGenerator(ClassRenameClashChecker classRenameClashChecker, HashMap hashMap, ListMultimap listMultimap, Map map1, int keepInnerClassInfoMode, int keepGenericsInfoMode, boolean randomizeNames) {
        this.clashChecker = classRenameClashChecker;
        this.packageRenameMap = hashMap;
        this.packageNodes = map1;
        this.keepInnerClassInfoMode = keepInnerClassInfoMode;
        this.keepGenericsInfoMode = keepGenericsInfoMode;
        this.randomizeNames = randomizeNames;
        this.fixedClasses = classRenameClashChecker.fixedClasses;
        this.fixedClassNames = classRenameClashChecker.fixedClassNames;
        this.classRenameMap = classRenameClashChecker.oldToNewNames;
        this.classesByName = classRenameClashChecker.classesByName;
        this.exclusionSet = classRenameClashChecker.exclusionSet;
        this.changeLogMapping = classRenameClashChecker.changeLogMapping;
        this.newToOldPackages = listMultimap;
        Iterator iterator = map1.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            Set set1;
            if (!((PackageTreeNode) entry.getValue()).isArchiveOnly()) {
                this.fileClassLowerCaseNames.add(((String) entry.getKey()).toLowerCase());
                set1 = this.allLowerCaseNames;
            } else {
                set1 = this.allLowerCaseNames;
            }

            set1.add(((String) entry.getKey()).toLowerCase());
        }
    }

    public static String getSimpleName(String string) {
        int ba = string.lastIndexOf(47);
        return ba == -1 ? string : string.substring(ba + 1);
    }

    public ArrayList getPackageClashCandidates(String string, String string1) {
        ArrayList arrayList = new ArrayList();
        if (string != null && this.newToOldPackages != null) {
            String string2;
            if (string1.length() > 0) {
                string2 = (String) this.packageRenameMap.get(string1);
            } else {
                string2 = string1;
            }

            int ba = string.lastIndexOf(47);
            String string3 = string.substring(ba + 1);
            List list1 = this.newToOldPackages.getValues(string2);
            if (list1 != null) {
                for (int i = 0; i < list1.size(); i++) {
                    String string4 = (String) list1.get(i);
                    arrayList.add(string4 + "/" + string3);
                }
            } else {
                arrayList.add(string);
            }
        }

        return arrayList;
    }

    public String getInnerClassSeparator(long ba, String string) {
        long bb = ba;
        bb = 66405443639848L ^ bb;
        String string3 = ZkmProcessingException.getBuildTag();
        int bc = string.lastIndexOf("$");
        String string1 = string3;
        String string2;
        if (bc > -1) {
            string2 = string.substring(bc, bc + 1);
            if (6204837985703228051L <= bb) {
                return string1;
            }
        } else {
            string2 = "";
        }

        return string2;
    }

    public abstract String generateNewName(ClassHierarchyNode classHierarchyNode, String string, HashMap hashMap, String string1, Triple triple, boolean bl) throws IOException;

    public int getKeepGenericsInfoMode() {
        return this.keepGenericsInfoMode;
    }

    public int getKeepInnerClassInfoMode() {
        return this.keepInnerClassInfoMode;
    }

    public final boolean isNameAvailable(String string, String string1, boolean bl, boolean bl1) throws IOException {
        return this.checkNameAvailable(string, string1, false, bl, bl1, new MutableInt(0));
    }

    public String getRenamedPackagePrefix(String string) {
        String string1 = "";
        int ba = string.lastIndexOf(47);
        if (ba != -1) {
            string1 = string.substring(0, ba);
            string1 = (String) this.packageRenameMap.get(string1);
            if (string1.length() > 0) {
                string1 = string1 + "/";
            }
        }

        return string1;
    }

    public final boolean isNameAvailableOrSame(String string, String string1, boolean bl, MutableInt mutableInt) throws IOException {
        return this.checkNameAvailable(string, string1, true, bl, false, mutableInt);
    }

    public final String createNewClassName(ClassHierarchyNode classHierarchyNode, HashMap hashMap) throws ZkmProcessingException, IOException {
        Triple triple = null;
        String string = "";
        String string1 = classHierarchyNode.getClassName();
        ProgramClass programClass1 = classHierarchyNode.getProgramClass();
        if (this.keepInnerClassInfoMode == 0 && classHierarchyNode.hasEnclosingNode() && classHierarchyNode.hasOuterClassNode()) {
            ClassHierarchyNode classHierarchyNode1 = classHierarchyNode.getOuterClassNode();
            String string9 = classHierarchyNode1.getClassName();
            if (this.classesByName.get(string9) == null) {
                throw new ZkmProcessingException("Missing outer class : " + ZkmUtils.slashesToDots(string9) + "' for '" + ZkmUtils.slashesToDots(string1) + "'");
            }

            if (hashMap != null) {
                Iterator iterator = hashMap.keySet().iterator();

                while (iterator.hasNext()) {
                    ClassHierarchyNode classHierarchyNode2 = (ClassHierarchyNode) iterator.next();
                    ProgramClass programClass3 = classHierarchyNode2.getProgramClass();
                    if (!this.classRenameMap.containsKey(classHierarchyNode2.getClassName()) && !this.fixedClasses.contains(programClass3)) {
                        return null;
                    }
                }
            }

            String string10;
            Object object;
            if ((object = this.classRenameMap.get(string9)) == null) {
                if (!this.fixedClasses.contains(classHierarchyNode1.getProgramClass())) {
                    return null;
                }

                string10 = string9;
            } else {
                string10 = (String) object;
            }

            String string11 = programClass1.getInnerSimpleName();
            if (string11 != null) {
                int bb = string9.length();
                int bc = string1.length();
                int ba = string11.length();
                if (bc - ba > bb) {
                    String string7 = string1.substring(bb, bc - ba);
                    string = string10 + string7;
                    string1 = string11;
                } else {
                    String string14 = this.getInnerClassSeparator(29969281254997L, string1);
                    string = string10 + string14;
                    string1 = string1.substring(string9.length() + 1);
                }
            } else {
                String string13 = this.getInnerClassSeparator(29969281254997L, string1);
                string = string10 + string13;
                string1 = string1.substring(string9.length() + 1);
            }
        } else {
            triple = this.exclusionSet.getClassLink(classHierarchyNode.getProgramClass());
            if (triple != null) {
                String string2 = (String) triple.getSecond();
                String string3 = (String) triple.getThird();
                ProgramClass programClass2 = (ProgramClass) triple.getFirst();
                if (programClass2 != null) {
                    String string4 = programClass2.getClassName();
                    String string5;
                    if ((string5 = (String) this.classRenameMap.get(string4)) == null) {
                        if (!this.fixedClasses.contains(programClass2)) {
                            return null;
                        }

                        String string12 = this.getRenamedPackagePrefix(classHierarchyNode.getProgramClass().getClassName())
                                + string2
                                + getSimpleName(string4)
                                + string3;
                        MutableInt mutableInt1 = new MutableInt(0);
                        if (!this.isNameAvailableOrSame(string1, string12, programClass1.isFromArchive(), mutableInt1)) {
                            throw new ZkmProcessingException(
                                    "Error renaming "
                                            + classHierarchyNode.getProgramClass().getDisplayLocationName()
                                            + ". Check <link> search paths. Code="
                                            + mutableInt1.getValue()
                                            + " : "
                                            + string2
                                            + " : "
                                            + string12
                                            + " : "
                                            + string3
                                            + " : "
                                            + string1
                                            + " (1)"
                            );
                        }

                        this.assignedNames.add(string12);
                        Set set2;
                        if (!programClass1.isFromArchive()) {
                            this.fileClassLowerCaseNames.add(string12.toLowerCase());
                            set2 = this.allLowerCaseNames;
                        } else {
                            set2 = this.allLowerCaseNames;
                        }

                        set2.add(string12.toLowerCase());
                        return string12;
                    }

                    String string6 = this.getRenamedPackagePrefix(classHierarchyNode.getProgramClass().getClassName()) + string2 + getSimpleName(string5) + string3;
                    MutableInt mutableInt = new MutableInt(0);
                    if (!this.isNameAvailableOrSame(string1, string6, programClass1.isFromArchive(), mutableInt)) {
                        throw new ZkmProcessingException(
                                "Error renaming "
                                        + classHierarchyNode.getProgramClass().getDisplayLocationName()
                                        + ". Check <link> search paths. Code="
                                        + mutableInt.getValue()
                                        + " : "
                                        + string2
                                        + " : "
                                        + string6
                                        + " : "
                                        + string3
                                        + " : "
                                        + string1
                                        + " (2)"
                        );
                    }

                    this.assignedNames.add(string6);
                    if (!programClass1.isFromArchive()) {
                        this.fileClassLowerCaseNames.add(string6.toLowerCase());
                        this.allLowerCaseNames.add(string6.toLowerCase());
                    }

                    return string6;
                }
            }
        }

        String string8 = this.generateNewName(classHierarchyNode, string, hashMap, string1, triple, programClass1.isFromArchive());
        this.assignedNames.add(string8);
        Set set1;
        if (!programClass1.isFromArchive()) {
            this.fileClassLowerCaseNames.add(string8.toLowerCase());
            set1 = this.allLowerCaseNames;
        } else {
            set1 = this.allLowerCaseNames;
        }

        set1.add(string8.toLowerCase());
        return string8;
    }

    public abstract String generateFallbackName(ClassHierarchyNode classHierarchyNode, boolean bl) throws IOException;
}
