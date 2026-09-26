package com.zelix.klassmaster.classfile.hierarchy;

import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.insn.VerifierType;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.SyncIndexedSet;
import com.zelix.klassmaster.util.TwoKeyMap;
import com.zelix.klassmaster.util.ZkmStringUtils;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class CommonSuperTypeResolver {
    public final TwoKeyMap commonSuperTypeCache = new TwoKeyMap(HiddenOptionFlags.USE_PARALLEL);
    private Map versionByThread = ZkmUtils.createHashMap();
    private final ClassHierarchyQuery hierarchyQuery;
    private final ClassResolver classResolver;

    public CommonSuperTypeResolver(ClassHierarchyQuery classHierarchyQuery, ClassResolver classResolver1) {
        this.hierarchyQuery = classHierarchyQuery;
        this.classResolver = classResolver1;
    }

    public boolean isInterfaceType(VerifierType verifierType, String string) throws ZkmException, IOException {
        String string1 = stripClassDescriptor(verifierType.getDescriptor());
        return string1.charAt(0) == '[' ? false : this.classResolver.getVersionedClass(string1, this.getThreadVersion(), string).isInterface();
    }

    public void clearCache() {
        this.commonSuperTypeCache.clear();
    }

    public static String toClassDescriptor(String string) {
        return string.charAt(string.length() - 1) != ';' ? 'L' + string + ';' : string;
    }

    public String getCommonArraySuperType(String string, String string1, String string2) throws ZkmException, IOException {
        int ba = string.lastIndexOf("[") + 1;
        int bb = string1.lastIndexOf("[") + 1;
        String string3 = string.substring(ba);
        String string4 = string1.substring(bb);
        if (ba != bb) {
            String string6;
            if (ba < bb) {
                string6 = string3;
            } else {
                string6 = string4;
            }

            return string6.startsWith("L") && string6.endsWith(";")
                    ? ZkmStringUtils.pad("", 76, Math.min(ba, bb), 91) + "Ljava/lang/Object;"
                    : ZkmStringUtils.pad("", 76, Math.min(ba, bb) - 1, 91) + "Ljava/lang/Object;";
        } else if (string3.length() == 1 && string4.length() == 1) {
            if (string3.equals(string4)) {
                return string;
            } else {
                return ba == 1 ? "Ljava/lang/Object;" : ZkmStringUtils.pad("", 76, ba - 1, 91) + "Ljava/lang/Object;";
            }
        } else {
            String string7;
            if (string3.length() != 1) {
                if (string4.length() != 1) {
                    String string5 = this.getCommonSuperType(string3, string4, string2);
                    return ZkmStringUtils.pad(string5, 82, string5.length() + ba, 91);
                }

                string7 = "Ljava/lang/Object;";
            } else {
                string7 = "Ljava/lang/Object;";
            }

            return string7;
        }
    }

    public boolean isArrayAssignableTo(String string, String string1, String string2) throws ZkmException, IOException {
        int ba = string.lastIndexOf("[") + 1;
        int bb = string1.lastIndexOf("[") + 1;
        String string3 = string.substring(ba);
        String string4 = string1.substring(bb);
        if (ba < bb) {
            return false;
        } else if (ba > bb) {
            return string4.equals("Ljava/lang/Object;");
        } else if (string3.length() == 1 && string4.length() == 1) {
            return string3.equals(string4);
        } else {
            return string3.length() != 1 && string4.length() != 1 ? this.isAssignableTo(string3, string4, string2) : false;
        }
    }

    public boolean isAssignableTo(String string, String string1, String string2) throws ZkmException, IOException {
        if (string.equals(string1)) {
            return true;
        }

        string = stripClassDescriptor(string);
        string1 = stripClassDescriptor(string1);
        Integer integer = this.getThreadVersion();
        if (string.charAt(0) == '[') {
            if (string1.charAt(0) == '[') {
                return this.isArrayAssignableTo(string, string1, string2);
            }

            ClassFileBase classFileBase2 = this.classResolver.getVersionedClass(string1, integer, string2);
            return classFileBase2.isInterface() ? string1.equals("java/lang/Cloneable") : string1.equals("java/lang/Object");
        } else {
            if (string1.charAt(0) == '[') {
                return false;
            }

            ClassFileBase classFileBase = this.classResolver.getVersionedClass(string, integer, string2);
            ClassFileBase classFileBase1 = this.classResolver.getVersionedClass(string1, integer, string2);
            if (classFileBase.isInterface()) {
                return classFileBase1.isInterface() ? this.isSameOrImplements(string, string1) : string1.equals("java/lang/Object");
            }

            if (classFileBase1.isInterface()) {
                if (HiddenOptionFlags.LENIENT_INTERFACE_SUPERTYPE) {
                    return !this.isSameOrImplements(string, string1) ? string.equals("java/lang/Object") : true;
                } else {
                    return this.isSameOrImplements(string, string1);
                }
            } else {
                return this.isSameOrSubclass(string, string1);
            }
        }
    }

    public void setThreadVersion(Object object) {
        this.versionByThread.put(ZkmUtils.getCurrentThreadId(), object);
    }

    public static String stripClassDescriptor(String string) {
        int ba = string.length();
        if (string.charAt(0) == 'L' && string.charAt(ba - 1) == ';') {
            string = string.substring(1, ba - 1);
        }

        return string;
    }

    public boolean isSameOrImplements(String string, String string1) throws ZkmException, IOException {
        return string.equals(string1) ? true : this.hierarchyQuery.implementsInterface(string, string1);
    }

    public Integer getThreadVersion() {
        return (Integer) this.versionByThread.get(ZkmUtils.getCurrentThreadId());
    }

    public String getCommonSuperType(String string, String string1, String string2) throws ZkmException, IOException {
        Integer integer = this.getThreadVersion();
        if (string.equals(string1)) {
            return string;
        }

        String string3 = (String) this.commonSuperTypeCache.getValue(string, string1);
        if (string3 != null) {
            return string3;
        }

        String string4 = stripClassDescriptor(string);
        String string5 = stripClassDescriptor(string1);
        TwoKeyMap twoKeyMap;
        if (string4.charAt(0) == '[') {
            if (string5.charAt(0) == '[') {
                string3 = this.getCommonArraySuperType(string4, string5, string2);
                twoKeyMap = this.commonSuperTypeCache;
            } else {
                ClassFileBase classFileBase1 = this.classResolver.getVersionedClass(string5, integer, string2);
                if (classFileBase1.isInterface()) {
                    if (string5.equals("java/lang/Cloneable")) {
                        string3 = "Ljava/lang/Cloneable;";
                        twoKeyMap = this.commonSuperTypeCache;
                    } else {
                        string3 = "Ljava/lang/Object;";
                        twoKeyMap = this.commonSuperTypeCache;
                    }
                } else {
                    string3 = "Ljava/lang/Object;";
                    twoKeyMap = this.commonSuperTypeCache;
                }
            }
        } else if (string5.charAt(0) == '[') {
            ClassFileBase classFileBase = this.classResolver.getVersionedClass(string4, integer, string2);
            if (classFileBase.isInterface()) {
                if (string4.equals("java/lang/Cloneable")) {
                    string3 = "Ljava/lang/Cloneable;";
                    twoKeyMap = this.commonSuperTypeCache;
                } else {
                    string3 = "Ljava/lang/Object;";
                    twoKeyMap = this.commonSuperTypeCache;
                }
            } else {
                string3 = "Ljava/lang/Object;";
                twoKeyMap = this.commonSuperTypeCache;
            }
        } else {
            ClassFileBase classFileBase2 = this.classResolver.getVersionedClass(string4, integer, string2);
            ClassFileBase classFileBase3 = this.classResolver.getVersionedClass(string5, integer, string2);
            if (!classFileBase2.isInterface() && !classFileBase3.isInterface()) {
                ArrayList arrayList = classFileBase2.getSuperclassChain(this.classResolver, integer);
                ArrayList arrayList1 = classFileBase3.getSuperclassChain(this.classResolver, integer);
                int ba = arrayList.size();
                if (arrayList1.contains(string4)) {
                    string3 = toClassDescriptor(string4);
                    twoKeyMap = this.commonSuperTypeCache;
                } else if (arrayList.contains(string5)) {
                    string3 = toClassDescriptor(string5);
                    twoKeyMap = this.commonSuperTypeCache;
                } else {
                    for (int i = 0; i < ba; i++) {
                        String string6 = (String) arrayList.get(i);
                        if (arrayList1.indexOf(string6) != -1) {
                            string3 = toClassDescriptor(string6);
                            if (string3.equals("Ljava/lang/Object;")) {
                                string3 = this.findCommonInterface(string4, classFileBase2, string5, classFileBase3);
                            }
                            break;
                        }
                    }

                    if (string3 == null) {
                        string3 = "Ljava/lang/Object;";
                        twoKeyMap = this.commonSuperTypeCache;
                    } else {
                        twoKeyMap = this.commonSuperTypeCache;
                    }
                }
            } else {
                string3 = this.findCommonInterface(string4, classFileBase2, string5, classFileBase3);
                twoKeyMap = this.commonSuperTypeCache;
            }
        }

        twoKeyMap.putValue(string, string1, string3);
        this.commonSuperTypeCache.putValue(string1, string, string3);
        return string3;
    }

    public ClassResolver getClassResolver() {
        return this.classResolver;
    }

    public ClassHierarchyQuery getHierarchyQuery() {
        return this.hierarchyQuery;
    }

    public String mergeVerifierTypes(VerifierType verifierType, VerifierType verifierType1, String string) throws ZkmException, IOException {
        return this.getCommonSuperType(verifierType.getDescriptor(), verifierType1.getDescriptor(), string);
    }

    public boolean isVerifierTypeAssignable(VerifierType verifierType, VerifierType verifierType1, String string) throws ZkmException, IOException {
        return this.isAssignableTo(verifierType.getDescriptor(), verifierType1.getDescriptor(), string);
    }

    public String findCommonInterface(String string, ClassFileBase classFileBase, String string1, ClassFileBase classFileBase1) throws ZkmException, IOException {
        Integer integer = this.getThreadVersion();
        String string2 = null;
        SyncIndexedSet syncIndexedSet = classFileBase.collectAllInterfaces(this.classResolver, integer);
        SyncIndexedSet syncIndexedSet1 = classFileBase1.collectAllInterfaces(this.classResolver, integer);
        int ba = syncIndexedSet.size();
        if (classFileBase.isInterface() && syncIndexedSet1.contains(string)) {
            string2 = toClassDescriptor(string);
        }

        if (string2 == null && classFileBase1.isInterface() && syncIndexedSet.contains(string1)) {
            string2 = toClassDescriptor(string1);
        }

        if (string2 == null) {
            for (int i = 0; i < ba; i++) {
                String string3 = (String) syncIndexedSet.getElementAt(i);
                if (syncIndexedSet1.indexOfElement(string3) != -1) {
                    string2 = toClassDescriptor(string3);
                    break;
                }
            }

            if (string2 == null) {
                string2 = "Ljava/lang/Object;";
            }
        }

        return string2;
    }

    public boolean isDescriptorAssignable(String string, String string1) throws ZkmException, IOException {
        if (string.equals(string1)) {
            return true;
        }

        if (string.length() > 1 && string1.length() > 1) {
            return this.isAssignableTo(string, string1, null);
        }

        if (string.length() == 1 && string1.length() == 1) {
            char ba = string.charAt(0);
            char bb = string1.charAt(0);
            switch (ba) {
                case 'B':
                    return bb == 'B' || bb == 'C' || bb == 'I' || bb == 'J';
                case 'C':
                    return bb == 'C' || bb == 'I' || bb == 'J';
                case 'D':
                    return bb == 'D';
                case 'E':
                case 'G':
                case 'H':
                case 'K':
                case 'L':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'T':
                case 'U':
                case 'W':
                case 'X':
                case 'Y':
                default:
                    return false;
                case 'F':
                    return bb == 'F' || bb == 'D';
                case 'I':
                    return bb == 'I' || bb == 'J';
                case 'J':
                    return bb == 'J';
                case 'S':
                    return bb == 'S' || bb == 'I' || bb == 'J';
                case 'V':
                    return bb == 'V';
                case 'Z':
                    return bb == 'Z';
            }
        } else {
            return false;
        }
    }

    public Integer clearThreadVersion() {
        return (Integer) this.versionByThread.remove(ZkmUtils.getCurrentThreadId());
    }

    public boolean isSameOrSubclass(String string, String string1) throws ZkmException, IOException {
        return string.equals(string1) ? true : this.hierarchyQuery.isSubclass(string, string1);
    }
}
