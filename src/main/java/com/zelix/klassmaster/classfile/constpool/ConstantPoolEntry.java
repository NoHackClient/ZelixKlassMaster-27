package com.zelix.klassmaster.classfile.constpool;

import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.exceptions.ClassFileFormatException;
import com.zelix.klassmaster.util.BooleanFlag;
import com.zelix.klassmaster.util.ZkmAssert;
import com.zelix.klassmaster.util.ZkmStringUtils;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public abstract class ConstantPoolEntry implements Comparable {
    private static int flowKey;
    public static final Map PRIMITIVE_TYPE_NAMES;
    public static final Map PRIMITIVE_WRAPPER_CLASSES;
    public static final Map WRAPPER_TO_PRIMITIVE;
    public int index;
    public AbstractConstantPool constantPool;

    public boolean isLongOrDouble() {
        ConstantPoolTag constantPoolTag = this.getTag();
        return constantPoolTag.equals(ConstantPoolTag.LONG) || constantPoolTag.equals(ConstantPoolTag.DOUBLE);
    }

    @Override
    public int compareTo(Object object) {
        return this.compareIndex((ConstantPoolEntry) object);
    }

    @Override
    public final boolean equals(Object object) {
        return super.equals(object);
    }

    public static ConstantPoolEntry readEntry(int ba, ClassFileInputStream classFileInputStream, ConstantPool constantPool1) throws ClassFileFormatException, IOException {
        int bb = classFileInputStream.readUnsignedByte();
        switch (bb) {
            case 1:
                return new ConstantUtf8(ba, classFileInputStream, constantPool1);
            case 2:
            case 13:
            case 14:
            default:
                throw new ClassFileFormatException(
                        "Class file '" + constantPool1.getClassLocation() + "' is corrupt. Tag '" + bb + "' at " + ba + " is invalid (1)"
                );
            case 3:
                return new ConstantInteger(ba, classFileInputStream, constantPool1);
            case 4:
                return new ConstantFloat(ba, classFileInputStream, constantPool1);
            case 5:
                return new ConstantLong(ba, classFileInputStream, constantPool1);
            case 6:
                return new ConstantDouble(ba, classFileInputStream, constantPool1);
            case 7:
                return new ConstantClass(ba, classFileInputStream, constantPool1);
            case 8:
                return new ConstantString(ba, classFileInputStream, constantPool1);
            case 9:
                return new ConstantFieldref(ba, classFileInputStream, constantPool1);
            case 10:
                return new ConstantMethodref(ba, classFileInputStream, constantPool1);
            case 11:
                return new ConstantInterfaceMethodref(ba, classFileInputStream, constantPool1);
            case 12:
                return new ConstantNameAndType(ba, classFileInputStream, constantPool1);
            case 15:
                return new ConstantMethodHandle(ba, classFileInputStream, constantPool1);
            case 16:
                return new ConstantMethodType(ba, classFileInputStream, constantPool1);
            case 17:
                return new ConstantDynamic(ba, classFileInputStream, constantPool1);
            case 18:
                return new ConstantInvokeDynamic(ba, classFileInputStream, constantPool1);
            case 19:
                return new ConstantModule(ba, classFileInputStream, constantPool1);
            case 20:
                return new ConstantPackage(ba, classFileInputStream, constantPool1);
        }
    }

    public static boolean isMethodDescriptor(String string) {
        return string.charAt(0) == '(' && string.indexOf(")") > 0;
    }

    public static String getReturnDescriptor(String string) {
        return string.substring(string.indexOf(")") + 1);
    }

    public static String getWrapperClassName(Object object) {
        return (String) PRIMITIVE_WRAPPER_CLASSES.get(object);
    }

    public abstract ConstantPoolTag getTag();

    public int compareIndex(ConstantPoolEntry constantPoolEntry1) {
        if (this.index < constantPoolEntry1.index) {
            return -1;
        } else {
            return this.index == constantPoolEntry1.index ? 0 : 1;
        }
    }

    public static String getTagName(ConstantPoolTag constantPoolTag) {
        switch (ConstantTagOrdinalSwitchMap.TAG_SWITCH_TABLE[constantPoolTag.ordinal()]) {
            case 1:
                return "CONSTANT_Utf8";
            case 2:
                return "CONSTANT_Integer";
            case 3:
                return "CONSTANT_Float";
            case 4:
                return "CONSTANT_Long";
            case 5:
                return "CONSTANT_Double";
            case 6:
                return "CONSTANT_Class";
            case 7:
                return "CONSTANT_String";
            case 8:
                return "CONSTANT_Fieldref";
            case 9:
                return "CONSTANT_Methodref";
            case 10:
                return "CONSTANT_InterfaceMethodref";
            case 11:
                return "CONSTANT_NameAndType";
            case 12:
                return "CONSTANT_MethodHandle";
            case 13:
                return "CONSTANT_MethodType";
            case 14:
                return "CONSTANT_Dynamic";
            case 15:
                return "CONSTANT_InvokeDynamic";
            case 16:
                return "CONSTANT_Module";
            case 17:
                return "CONSTANT_Package";
            default:
                return "";
        }
    }

    public static String getPrimitiveTypeName(Object object) {
        return (String) PRIMITIVE_TYPE_NAMES.get(object);
    }

    public static String getStackWrapperClassName(String string) {
        String string1 = string;
        Map map1;
        switch (string1.charAt(0)) {
            case 'B':
            case 'C':
            case 'S':
                string1 = "I";
                map1 = PRIMITIVE_WRAPPER_CLASSES;
                break;
            default:
                map1 = PRIMITIVE_WRAPPER_CLASSES;
        }

        return (String) map1.get(string1);
    }

    public static int countParameterSlots(List list1) {
        int ba = 0;
        Iterator iterator = list1.iterator();

        while (iterator.hasNext()) {
            if (isWideType((String) iterator.next())) {
                ba += 2;
            } else {
                ba++;
            }
        }

        return ba;
    }

    public static final String formatMemberSignature(String string, String string1) {
        StringBuilder stringBuilder = new StringBuilder();
        int ba = string1.indexOf(41);
        if (ba > -1) {
            stringBuilder.append(descriptorToJavaType(string1.substring(ba + 1)));
            stringBuilder.append(' ');
            stringBuilder.append(string);
            stringBuilder.append('(');
            List list1 = getParameterJavaTypes(string1);
            int bb = list1.size();

            for (int i = 0; i < bb; i++) {
                stringBuilder.append((String) list1.get(i));
                if (i < bb - 1) {
                    stringBuilder.append(", ");
                }
            }

            stringBuilder.append(')');
        } else {
            stringBuilder.append(descriptorToJavaType(string1));
            stringBuilder.append(' ');
            stringBuilder.append(string);
        }

        return stringBuilder.toString();
    }

    public static List getReferencedProgramClasses(String string) {
        ArrayList arrayList = new ArrayList();
        List list1 = getParameterAndReturnTypes(string);

        for (int i = 0; i < list1.size(); i++) {
            ProgramClass programClass1 = findProgramClassByDescriptor((String) list1.get(i));
            if (programClass1 != null) {
                arrayList.add(programClass1);
            }
        }

        return arrayList;
    }

    public final int getIndex() {
        return this.index;
    }

    public static List parseParameterTypes(String string, boolean ba) {
        int bb;
        String string1;
        int be;
        be = ConstantPoolTag.getFlowSeed();
        string1 = MethodSignature.getParameterPart(string);
        bb = be;
        be = (byte) (ba ? 1 : 0);
        label123:
        if (bb == 0) {
            if ((ba ? 1 : 0) != 0) {
                String string3 = string1;
                if (bb == 0) {
                    if (string1.length() < 2) {
                        return null;
                    }

                    string3 = string1;
                }

                be = string3.charAt(0);
                if (bb == 0) {
                    if (be != 40) {
                        return null;
                    }

                    be = string1.charAt(string1.length() - 1);
                }

                if (bb != 0) {
                    break label123;
                }

                if (be != 41) {
                    return null;
                }
            }

            be = string1.length();
        }

        if (bb == 0) {
            be = be >= 2 ? 1 : 0;
        }

        ZkmAssert.assertTrue(be != 0, new String[]{"'" + string1 + "' : '" + string + "'"});
        string1 = string1.substring(1, string1.indexOf(41));
        ArrayList arrayList = new ArrayList();

        while (true) {
            if (string1.length() != 0) {
                if (bb != 0) {
                    break;
                }

                int bc = 0;

                int bd;
                label99:
                {
                    label98:
                    {
                        while (true) {
                            if (string1.charAt(bc) == '[') {
                                bc++;
                                if (bb != 0) {
                                    break;
                                }

                                if (bb == 0) {
                                    continue;
                                }
                            }

                            be = string1.charAt(bc);
                            if (bb != 0) {
                                break label98;
                            }

                            if (be == 76) {
                                bd = string1.indexOf(59);
                                be = (byte) (ba ? 1 : 0);
                                if (bb == 0) {
                                    if ((ba ? 1 : 0) == 0) {
                                        break label99;
                                    }

                                    be = bd;
                                }

                                if (bb == 0) {
                                    if (be == -1) {
                                        return null;
                                    }

                                    be = bd;
                                }

                                if (be == bc + 1) {
                                    return null;
                                }
                                break label99;
                            }
                            break;
                        }

                        be = bc;
                    }

                    bd = be;
                }

                String string2 = string1.substring(0, bd + 1);
                be = (byte) ((arrayList.add(string2)) ? 1 : 0);
                label79:
                if (bb == 0) {
                    if ((ba ? 1 : 0) != 0) {
                        be = string2.length();
                        if (bb != 0) {
                            break label79;
                        }

                        if (be == 1 && PRIMITIVE_TYPE_NAMES.get(string2) == null) {
                            return null;
                        }
                    }

                    be = bd;
                }

                label72:
                {
                    if (be == string1.length() - 1) {
                        string1 = "";
                        if (bb == 0) {
                            break label72;
                        }
                    }

                    string1 = string1.substring(bd + 1);
                }

                if (bb == 0) {
                    continue;
                }
            }

            arrayList.trimToSize();
            break;
        }

        return arrayList;
    }

    public static List getParameterClassForNames(String string) {
        List list1 = getParameterTypes(string);
        int ba = list1.size();

        for (int i = 0; i < ba; i++) {
            String string1 = (String) list1.get(i);
            list1.set(i, toClassForNameFormat(string1));
        }

        return list1;
    }

    public abstract void writeTo(DataOutputStream dataOutputStream) throws IOException;

    public boolean isUnresolved() {
        return false;
    }

    public static String descriptorToJavaType(String string, boolean bl) {
        return descriptorToJavaType(string, bl, true);
    }

    public String getDisplayString() {
        return this.getValueString();
    }

    public boolean isWellFormed() {
        return true;
    }

    public static String stripClassDescriptor(String string) {
        int ba = string.length();
        return ba > 2 && string.charAt(0) == 'L' && string.charAt(ba - 1) == ';' ? string.substring(1, ba - 1) : string;
    }

    public static boolean isWideType(String string) {
        return string.equals("J") || string.equals("D");
    }

    public static int getCommaChar() {
        return 44;
    }

    public ConstantPoolEntry(int index, AbstractConstantPool abstractConstantPool) {
        this.index = index;
        this.constantPool = abstractConstantPool;
    }

    public final ClassFileBase getOwnerClass() {
        return this.constantPool.getClassFile();
    }

    public static String remapDescriptorClassNames(String string, HashMap hashMap) {
        int ba = 0;
        String string4 = string;

        for (byte bc = 76; (ba = string4.indexOf(bc, ba)) != -1; bc = 76) {
            int bb = string.indexOf(59, ba);
            if (bb == -1) {
                break;
            }

            String string1 = string.substring(ba + 1, bb);
            String string2 = null;
            if (string1.indexOf(46) > 0 && string1.lastIndexOf(46) < string1.length() && string1.indexOf(47) == -1) {
                string2 = string1.replace('.', '/');
            }

            String string3 = (String) hashMap.get(string2 == null ? string1 : string2);
            if (string3 != null) {
                if (string2 != null) {
                    string3 = string3.replace('/', '.');
                }

                if (!string3.equals(string1)) {
                    string = string.substring(0, ba + 1) + string3 + string.substring(bb);
                    bb = bb + string3.length() - string1.length();
                }
            }

            ba = bb;
            string4 = string;
        }

        return string;
    }

    public static List getParameterTypes(String string) {
        return parseParameterTypes(string, false);
    }

    public final void setIndex(int index) {
        this.index = index;
    }

    public static boolean isFieldDescriptor(String string) {
        if (string.indexOf(",") <= -1 && string.indexOf(93) <= -1 && string.indexOf(46) <= -1 && string.indexOf(" ") <= -1) {
            int ba = 0;

            while (string.charAt(ba) == '[') {
                ba++;
            }

            String string1 = string.substring(ba);
            if (string1.startsWith("L")) {
                return string1.endsWith(";") || string1.length() > 2;
            } else {
                return string1.length() == 1 ? PRIMITIVE_TYPE_NAMES.containsKey(string1) : false;
            }
        } else {
            return false;
        }
    }

    public static List getParameterAndReturnTypes(String string) {
        List list1 = getParameterTypes(string);
        String string1 = getReturnDescriptor(string);
        list1.add(string1);
        return list1;
    }

    public static String getWrapperPrimitiveDescriptor(Object object) {
        return (String) WRAPPER_TO_PRIMITIVE.get(object);
    }

    public void writeRemappedTo(DataOutputStream dataOutputStream, Map map1) throws IOException {
        this.writeTo(dataOutputStream);
    }

    public static List getStackParameterTypes(ResolvedNameAndType resolvedNameAndType) {
        List list1 = getParameterTypes(resolvedNameAndType.getDescriptor());
        int ba = list1.size();

        for (int i = 0; i < ba; i++) {
            String string = (String) list1.get(i);
            if (string.equals("B") || string.equals("C") || string.equals("S") || string.equals("Z")) {
                list1.set(i, "I");
            }
        }

        return list1;
    }

    public static String toDescriptor(String string) {
        if (string.length() == 1) {
            return isPrimitiveDescriptor(string) ? string : "L" + string + ";";
        }

        int ba = string.indexOf("[]");
        String string1;
        String string2;
        byte be;
        if (ba > -1) {
            string1 = string.substring(0, ba);
            string2 = string1;
            be = 91;
        } else {
            string1 = string;
            string2 = string1;
            be = 91;
        }

        int bb = string2.lastIndexOf(be);
        if (bb > -1) {
            string1 = string1.substring(bb + 1);
        }

        if (string1.length() == 1) {
            if (!isPrimitiveDescriptor(string1)) {
                string1 = "L" + string1 + ";";
            }
        } else {
            string1 = string1.replace('.', '/');
            if (!string1.startsWith("L") || !string1.endsWith(";")) {
                string1 = "L" + string1 + ";";
            }
        }

        int bc = 0;
        if (ba > -1) {
            bc = ZkmStringUtils.countOccurrences(string, "[]");
        } else if (bb > -1) {
            bc = bb + 1;
        }

        for (int i = 0; i < bc; i++) {
            string1 = '[' + string1;
        }

        return string1;
    }

    public static List getParameterJavaTypes(String string) {
        java.util.List list2 = null;
        int flowSeed = ConstantPoolTag.getFlowSeed();
        List list1 = getParameterTypes(string);
        int ba = flowSeed;
        int bb = list1.size();
        int bc = 0;

        while (true) {
            if (bc < bb) {
                list2 = list1;
                if (ba != 0) {
                    break;
                }

                String string1 = (String) list1.get(bc);
                list1.set(bc, descriptorToJavaType(string1));
                bc++;
                if (ba == 0) {
                    continue;
                }
            }

            list2 = list1;
            break;
        }

        return list2;
    }

    public final String getOwnerJavaName() {
        return this.constantPool.getExternalClassName();
    }

    public static String descriptorToJavaType(String string, boolean ba, boolean bb) {
        java.lang.String string7 = null;
        java.lang.String string6 = null;
        java.lang.String string5 = null;
        java.lang.String string4 = null;
        java.lang.String string3 = null;
        int bc;
        String string1;
        label109:
        {
            int flowSeed = ConstantPoolTag.getFlowSeed();
            int bd = string.indexOf(")");
            bc = flowSeed;
            if (bd > -1) {
                string1 = string.substring(bd + 1);
                if (bc == 0) {
                    break label109;
                }
            }

            string1 = string;
        }

        int be = 0;
        int bf = 0;

        byte bg;
        label102:
        {
            label113:
            {
                label114:
                {
                    label99:
                    {
                        while (true) {
                            if (bf < string1.length()) {
                                if (bc != 0) {
                                    break;
                                }

                                string3 = string1;
                                if (bc != 0) {
                                    break label99;
                                }

                                if (string1.charAt(bf) == '[') {
                                    be++;
                                    bf++;
                                    if (bc == 0) {
                                        continue;
                                    }
                                }
                            }

                            string4 = string1;
                            if (bc != 0) {
                                break label114;
                            }

                            string1 = string1.substring(be);
                            break;
                        }

                        string3 = string1;
                    }

                    if (string3.charAt(0) == 'L') {
                        bg = ((byte) (ba ? 1 : 0));
                        if (bc == 0) {
                            int bi;
                            label81:
                            {
                                if ((ba ? 1 : 0) != 0) {
                                    string5 = string1;
                                    if (bc == 0) {
                                        if (string1.length() <= 2) {
                                            return null;
                                        }

                                        string5 = string1;
                                    }

                                    bi = string1.length();
                                    if (bc != 0) {
                                        break label81;
                                    }

                                    if (string5.charAt(bi - 1) != ';') {
                                        return null;
                                    }
                                }

                                string5 = string1;
                                bi = 1;
                            }

                            string1 = string5.substring(bi, string1.length() - 1);
                            bg = ((byte) (bb ? 1 : 0));
                        }

                        if (bc != 0) {
                            break label102;
                        }

                        if (bg == 0) {
                            break label113;
                        }

                        string1 = string1.replace('/', '.');
                        if (bc == 0) {
                            break label113;
                        }
                    }

                    string4 = (String) PRIMITIVE_TYPE_NAMES.get(String.valueOf(string1.charAt(0)));
                }

                label68:
                {
                    String string2 = string4;
                    if ((ba ? 1 : 0) != 0) {
                        string6 = string2;
                        if (bc != 0) {
                            break label68;
                        }

                        if (string2 == null) {
                            return null;
                        }
                    }

                    string6 = string2;
                }

                string1 = string6;
            }

            bg = 0;
        }

        bf = bg;

        while (true) {
            if (bf < be) {
                string7 = string1 + "[]";
                if (bc != 0) {
                    break;
                }

                string1 = string7;
                bf++;
                if (bc == 0) {
                    continue;
                }
            }

            string7 = string1;
            break;
        }

        return string7;
    }

    public static ClassFileBase lookupClassByDescriptor(String string) {
        String string1 = ClassFileBase.extractClassName(string);
        if (string1 != null) {
            ClassFileBase classFileBase = ClassHierarchyNode.findClassFile(string1);
            if (classFileBase != null) {
                return classFileBase;
            }
        }

        return null;
    }

    public final String getOwnerClassName() {
        return this.constantPool.getClassName();
    }

    public static String toClassForNameFormat(String string) {
        String string1 = string.replace('.', '/');
        StringBuilder stringBuilder = new StringBuilder();
        int ba = 0;

        for (int i = 0; i < string.length() && string.charAt(i) == '['; i++) {
            ba++;
        }

        stringBuilder.append(string1.substring(0, ba));
        string1 = string1.substring(ba);
        if (string1.charAt(0) == 'L') {
            if (string1.length() <= 2 || string1.charAt(string1.length() - 1) != ';') {
                return null;
            }

            if (ba != 0) {
                string1 = string1.replace('/', '.');
            }

            stringBuilder.append(string1);
        } else {
            stringBuilder.append(string1.charAt(0));
        }

        return stringBuilder.toString();
    }

    public static void setFlowKey() {
        flowKey = 43;
    }

    public static boolean isPrimitiveDescriptor(String string) {
        return string.length() == 1 && PRIMITIVE_TYPE_NAMES.containsKey(string);
    }

    public static ConstantPoolEntry readLibraryEntry(int ba, ClassFileInputStream classFileInputStream, AbstractConstantPool abstractConstantPool) throws ClassFileFormatException, IOException {
        int bb = classFileInputStream.readUnsignedByte();
        switch (bb) {
            case 1:
                return new ConstantUtf8(ba, classFileInputStream, abstractConstantPool);
            case 2:
            case 13:
            case 14:
            default:
                throw new ClassFileFormatException(
                        "Class file '" + abstractConstantPool.getClassLocation() + "' is corrupt. Tag '" + bb + "' at " + ba + " is invalid (2)"
                );
            case 3:
                return new ConstantInteger(ba, classFileInputStream, abstractConstantPool);
            case 4:
                return new ConstantFloat(ba, classFileInputStream, abstractConstantPool);
            case 5:
                return new ConstantLong(ba, classFileInputStream, abstractConstantPool);
            case 6:
                return new ConstantDouble(ba, classFileInputStream, abstractConstantPool);
            case 7:
                return new ConstantClass(ba, classFileInputStream, abstractConstantPool);
            case 8:
                return new ConstantString(ba, classFileInputStream, abstractConstantPool);
            case 9:
                return new ConstantFieldref(ba, classFileInputStream, abstractConstantPool);
            case 10:
                return new ConstantMethodref(ba, classFileInputStream, abstractConstantPool);
            case 11:
                return new ConstantInterfaceMethodref(ba, classFileInputStream, abstractConstantPool);
            case 12:
                return new ConstantNameAndType(ba, classFileInputStream, abstractConstantPool);
            case 15:
                return new ConstantMethodHandle(ba, classFileInputStream, abstractConstantPool);
            case 16:
                return new ConstantMethodType(ba, classFileInputStream, abstractConstantPool);
            case 17:
                return new ConstantDynamic(ba, classFileInputStream, abstractConstantPool);
            case 18:
                return new ConstantInvokeDynamic(ba, classFileInputStream, abstractConstantPool);
            case 19:
                return new ConstantModule(ba, classFileInputStream, abstractConstantPool);
            case 20:
                return new ConstantPackage(ba, classFileInputStream, abstractConstantPool);
        }
    }

    public static int getFlowKey() {
        return flowKey;
    }

    public int getSlotCount() {
        return 1;
    }

    public static String parseClassNameCandidate(String string) {
        String string1 = string;
        int ba = string1.length();
        if (string1.endsWith(";")) {
            StringBuffer stringBuffer = new StringBuffer();
            String string3;
            int bi;
            if (!string1.startsWith("[")) {
                if (!string1.startsWith("L")) {
                    return null;
                }

                stringBuffer.append('L');
                string3 = string1;
                bi = stringBuffer.length();
            } else {
                stringBuffer.append('[');

                for (int i = 1; i < ba; i++) {
                    char bc = string1.charAt(i);
                    if (bc != '[') {
                        if (bc != 'L') {
                            return null;
                        }

                        stringBuffer.append('L');
                        break;
                    }

                    stringBuffer.append('[');
                }

                string3 = string1;
                bi = stringBuffer.length();
            }

            string1 = string3.substring(bi, ba - 1);
        }

        String string2;
        if (string1.indexOf(46) != -1) {
            string2 = string1.replace('.', '/');
        } else {
            string2 = string1;
        }

        int bh = string1.length();
        int bd = string1.indexOf(46);
        int be = string1.lastIndexOf(46);
        int bf = string1.indexOf(47);
        int bg = string1.lastIndexOf(47);
        return string2.length() < 3
                || !Character.isJavaIdentifierStart(string2.charAt(0))
                || (bd <= 0 || be >= bh - 1 || bf != -1) && (bf <= 0 || bg >= bh - 1 || bd != -1)
                ? null
                : string2;
    }

    public static String descriptorToJavaType(String string) {
        return descriptorToJavaType(string, false, true);
    }

    public static boolean isValidMethodDescriptor(String string) {
        if (string.length() < 2 || string.charAt(0) != '(' || string.indexOf(")") == -1) {
            return false;
        }

        if (string.indexOf(",") <= -1 && string.indexOf("]") <= -1 && string.indexOf(" ") <= -1) {
            String string1 = string.substring(1, string.lastIndexOf(")"));
            if (string.lastIndexOf(")") < string.length() - 1) {
                string1 = string1 + string.substring(string.lastIndexOf(")") + 1);
            }

            while (string1.length() != 0) {
                int ba = 0;

                while (string1.charAt(ba) == '[') {
                    ba++;
                }

                int bb;
                if (string1.charAt(ba) == 'L') {
                    bb = string1.indexOf(59);
                    if (bb == -1 || bb == ba + 1) {
                        return false;
                    }
                } else {
                    bb = ba;
                }

                String string2 = string1.substring(0, bb + 1);
                if (string2.length() == 1 && PRIMITIVE_TYPE_NAMES.get(string2) == null) {
                    return false;
                }

                if (bb == string1.length() - 1) {
                    string1 = "";
                } else {
                    string1 = string1.substring(bb + 1);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public final String getOwnerLocationDescription() {
        return this.constantPool.getClassLocationDescription();
    }

    public static ProgramClass findProgramClassByDescriptor(String string) {
        String string1 = ClassFileBase.extractClassName(string);
        if (string1 != null) {
            ProgramClass programClass1 = ClassHierarchyNode.findProgramClass(string1);
            if (programClass1 != null) {
                return programClass1;
            }
        }

        return null;
    }

    public static String remapClassNameString(String string, Map map1, BooleanFlag booleanFlag) {
        booleanFlag.setValue(false);
        String string1 = string;
        int ba = string1.length();
        StringBuilder stringBuilder = null;
        String string2 = null;
        if (string1.endsWith(";")) {
            stringBuilder = new StringBuilder();
            if (string1.startsWith("[")) {
                stringBuilder.append('[');

                for (int i = 1; i < ba; i++) {
                    char bc = string1.charAt(i);
                    if (bc != '[') {
                        if (bc != 'L') {
                            return string;
                        }

                        stringBuilder.append('L');
                        break;
                    }

                    stringBuilder.append('[');
                }
            } else {
                if (!string1.startsWith("L")) {
                    return string;
                }

                stringBuilder.append('L');
            }

            string2 = ";";
            string1 = string1.substring(stringBuilder.length(), ba - 1);
        }

        boolean bl = false;
        String string4;
        if (string1.indexOf(46) != -1 && string1.indexOf(47) == -1) {
            string4 = string1.replace('.', '/');
            bl = true;
        } else {
            string4 = string1;
        }

        String string3 = (String) map1.get(string4);
        booleanFlag.setValue(string3 != null);
        int bd = string1.length();
        int be = string1.indexOf(46);
        int bf = string1.lastIndexOf(46);
        int bg = string1.indexOf(47);
        int bh = string1.lastIndexOf(47);
        if (string3 != null
                && !string3.equals(string4)
                && string4.length() >= 3
                && Character.isJavaIdentifierStart(string4.charAt(0))
                && (be > 0 && bf < bd - 1 && bg == -1 || bg > 0 && bh < bd - 1 && be == -1)) {
            if (bl) {
                string3 = string3.replace('/', '.');
            }

            StringBuilder stringBuilder1 = new StringBuilder();
            if (stringBuilder != null) {
                stringBuilder1.append(stringBuilder.toString());
            }

            stringBuilder1.append(string3);
            if (string2 != null) {
                stringBuilder1.append(string2);
            }

            return stringBuilder1.toString();
        } else {
            return string;
        }
    }

    public String getAbbreviatedValue() {
        return this.getValueString();
    }

    public String getValueString() {
        return "";
    }

    public final ClassFileBase selectVersionedClass(ClassFileBase classFileBase) {
        ClassFileBase classFileBase2 = this.getOwnerClass();
        ClassFileBase classFileBase1;
        if (classFileBase2.isVersionedVariant() && classFileBase.hasVersionedVariants()) {
            classFileBase1 = classFileBase.selectVersionForRelease(classFileBase2.getReleaseVersion());
        } else {
            classFileBase1 = classFileBase;
        }

        return classFileBase1;
    }

    public static List getReferencedClasses(String string) {
        ArrayList arrayList = new ArrayList();
        List list1 = getParameterAndReturnTypes(string);

        for (int i = 0; i < list1.size(); i++) {
            ClassFileBase classFileBase = lookupClassByDescriptor((String) list1.get(i));
            if (classFileBase != null) {
                if (classFileBase.isMultiRelease()) {
                    arrayList.addAll(classFileBase.getAllVersions());
                } else {
                    arrayList.add(classFileBase);
                }
            }
        }

        return arrayList;
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    public final String getOwnerFilePath() {
        return this.constantPool.getClassLocation();
    }

    static {
        if (getFlowKey() != 0) {
            setFlowKey();
        }

        PRIMITIVE_TYPE_NAMES = ZkmUtils.createHashMap(17);
        PRIMITIVE_WRAPPER_CLASSES = ZkmUtils.createHashMap(17);
        WRAPPER_TO_PRIMITIVE = ZkmUtils.createHashMap(17);
        PRIMITIVE_TYPE_NAMES.put("B", "byte");
        PRIMITIVE_TYPE_NAMES.put("C", "char");
        PRIMITIVE_TYPE_NAMES.put("D", "double");
        PRIMITIVE_TYPE_NAMES.put("F", "float");
        PRIMITIVE_TYPE_NAMES.put("I", "int");
        PRIMITIVE_TYPE_NAMES.put("J", "long");
        PRIMITIVE_TYPE_NAMES.put("S", "short");
        PRIMITIVE_TYPE_NAMES.put("Z", "boolean");
        PRIMITIVE_TYPE_NAMES.put("V", "void");
        PRIMITIVE_WRAPPER_CLASSES.put("B", "java/lang/Byte");
        PRIMITIVE_WRAPPER_CLASSES.put("C", "java/lang/Character");
        PRIMITIVE_WRAPPER_CLASSES.put("D", "java/lang/Double");
        PRIMITIVE_WRAPPER_CLASSES.put("F", "java/lang/Float");
        PRIMITIVE_WRAPPER_CLASSES.put("I", "java/lang/Integer");
        PRIMITIVE_WRAPPER_CLASSES.put("J", "java/lang/Long");
        PRIMITIVE_WRAPPER_CLASSES.put("S", "java/lang/Short");
        PRIMITIVE_WRAPPER_CLASSES.put("Z", "java/lang/Boolean");
        PRIMITIVE_WRAPPER_CLASSES.put("V", "java/lang/Void");
        WRAPPER_TO_PRIMITIVE.put("java/lang/Byte", "B");
        WRAPPER_TO_PRIMITIVE.put("java/lang/Character", "C");
        WRAPPER_TO_PRIMITIVE.put("java/lang/Double", "D");
        WRAPPER_TO_PRIMITIVE.put("java/lang/Float", "F");
        WRAPPER_TO_PRIMITIVE.put("java/lang/Integer", "I");
        WRAPPER_TO_PRIMITIVE.put("java/lang/Long", "J");
        WRAPPER_TO_PRIMITIVE.put("java/lang/Short", "S");
        WRAPPER_TO_PRIMITIVE.put("java/lang/Boolean", "Z");
        WRAPPER_TO_PRIMITIVE.put("java/lang/Void", "V");
    }

    public final ProgramClass selectVersionedProgramClass(ProgramClass programClass1) {
        return (ProgramClass) this.selectVersionedClass(programClass1);
    }

    public static String remapQualifiedName(String string, Map map1, boolean bl) {
        if (string != null && string.length() != 0) {
            boolean bl1 = false;
            if (string.indexOf(".") > -1 && string.indexOf("/") == -1) {
                bl1 = true;
            }

            String string2 = bl1 ? string.replace(".", "/") : string;
            String string1;
            if (bl) {
                string1 = (String) ZkmUtils.mapOrSelf(string2, map1);
            } else if ((string.indexOf(".") != -1 || string.indexOf("/") != -1)
                    && (string.indexOf(".") == -1 || string.indexOf("/") == -1)
                    && string.charAt(0) != '.'
                    && string.charAt(0) != '/'
                    && string.charAt(string.length() - 1) != '.'
                    && string.charAt(string.length() - 1) != '/') {
                string1 = string.replace(".", "/");
                StringTokenizer stringTokenizer = new StringTokenizer(string1, "/");
                boolean bl2 = true;

                while (stringTokenizer.hasMoreTokens()) {
                    String string3 = stringTokenizer.nextToken();
                    if (!ConstantPool.isIdentifierPath(string3)) {
                        bl2 = false;
                        break;
                    }

                    if (string3.length() < 2) {
                        bl2 = false;
                        break;
                    }
                }

                if (bl2) {
                    string1 = (String) ZkmUtils.mapOrSelf(string2, map1);
                } else {
                    string1 = string;
                }
            } else {
                string1 = string;
            }

            return bl1 ? string1.replace("/", ".") : string1;
        } else {
            return string;
        }
    }

    public static String getStackReturnType(ResolvedNameAndType resolvedNameAndType) {
        String string = resolvedNameAndType.getDescriptor();
        String string1;
        if (isMethodDescriptor(string)) {
            string1 = getReturnDescriptor(string);
        } else {
            string1 = string;
        }

        if (string1.equals("V")) {
            return null;
        } else {
            return !string1.equals("B") && !string1.equals("C") && !string1.equals("S") && !string1.equals("Z") ? string1 : "I";
        }
    }
}
