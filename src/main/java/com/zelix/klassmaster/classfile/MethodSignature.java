package com.zelix.klassmaster.classfile;

import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyQuery;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.ZkmAssert;
import com.zelix.klassmaster.util.ZkmStringUtils;
import com.zelix.klassmaster.util.ZkmUtils;

import java.beans.Introspector;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class MethodSignature extends MemberSignatureBase {
    public static final Map PRIMITIVE_TYPE_DESCRIPTORS = ZkmUtils.createHashMap(17);
    public static final MethodSignature DEFAULT_CONSTRUCTOR = new MethodSignature("<init>()V");
    public static final MethodSignature STATIC_INITIALIZER = new MethodSignature("<clinit>()V");
    public static final MethodSignature MAIN_METHOD = new MethodSignature("main([Ljava/lang/String;)V");
    private static final Set METHOD_HANDLE_POLYMORPHIC_NAMES;
    private static final Set VAR_HANDLE_POLYMORPHIC_NAMES;
    private FieldNameTypeSignature nameTypeSignature;
    private final String returnDescriptor;
    private final int hash;

    public MethodSignature(MemberSignatureBase memberSignatureBase) {
        super(memberSignatureBase);
        if (memberSignatureBase instanceof MethodSignature) {
            MethodSignature methodSignature2 = (MethodSignature) memberSignatureBase;
            this.returnDescriptor = methodSignature2.returnDescriptor;
        } else if (memberSignatureBase instanceof FieldNameTypeSignature) {
            FieldNameTypeSignature fieldNameTypeSignature = (FieldNameTypeSignature) memberSignatureBase;
            this.returnDescriptor = fieldNameTypeSignature.getReturnDescriptor();
            ZkmAssert.assertNotNull(this.returnDescriptor, "Null return type in '" + this.name + this.parameterDescriptor + "'");
        } else {
            this.returnDescriptor = null;
        }

        this.hash = (this.name + this.parameterDescriptor + this.returnDescriptor).hashCode();
    }

    public static List splitParameterDescriptors(String string) {
        return ConstantPoolEntry.getParameterTypes(string);
    }

    public String toDeclarationString() {
        return ConstantPoolEntry.descriptorToJavaType(this.returnDescriptor) + " " + this.formatSignature();
    }

    public static String formatParameterTypes(String string, Map map1) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('(');
        List list1 = ConstantPoolEntry.getParameterTypes(string);
        int ba = list1.size();

        for (int i = 0; i < ba; i++) {
            String string1 = (String) list1.get(i);
            stringBuilder.append(descriptorToJavaType(string1, map1));
            if (i + 1 < ba) {
                stringBuilder.append(", ");
            }
        }

        return stringBuilder.append(')').toString();
    }

    public static String parseParameterList(String string, Map map1) {
        String string2;
        if (string != null) {
            if (string.length() != 0) {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("(");
                StringTokenizer stringTokenizer = new StringTokenizer(string, ",");

                while (stringTokenizer.hasMoreTokens()) {
                    String string1 = stringTokenizer.nextToken().trim();
                    stringBuffer.append(javaTypeToDescriptor(string1, map1));
                }

                return stringBuffer.append(')').toString();
            }

            string2 = "()";
        } else {
            string2 = "()";
        }

        return string2;
    }

    public static String descriptorToJavaType(String string, Map map1) {
        String string1 = "";
        String string2 = ConstantPoolEntry.descriptorToJavaType(string, false, false);
        if (string2.endsWith("]")) {
            int ba = string2.indexOf(91);
            string1 = string2.substring(ba);
            string2 = string2.substring(0, ba);
        }

        String string3 = (String) ZkmUtils.mapOrSelf(string2, map1);
        return string3.replace('/', '.') + string1;
    }


    static {
        new MethodSignature("invoke([Ljava/lang/Object;)Ljava/lang/Object;");
        new MethodSignature("invokeExact([Ljava/lang/Object;)Ljava/lang/Object;");
        METHOD_HANDLE_POLYMORPHIC_NAMES = ZkmUtils.createHashSet();
        VAR_HANDLE_POLYMORPHIC_NAMES = ZkmUtils.createHashSet();
        PRIMITIVE_TYPE_DESCRIPTORS.put("byte", "B");
        PRIMITIVE_TYPE_DESCRIPTORS.put("char", "C");
        PRIMITIVE_TYPE_DESCRIPTORS.put("double", "D");
        PRIMITIVE_TYPE_DESCRIPTORS.put("float", "F");
        PRIMITIVE_TYPE_DESCRIPTORS.put("int", "I");
        PRIMITIVE_TYPE_DESCRIPTORS.put("long", "J");
        PRIMITIVE_TYPE_DESCRIPTORS.put("short", "S");
        PRIMITIVE_TYPE_DESCRIPTORS.put("boolean", "Z");
        PRIMITIVE_TYPE_DESCRIPTORS.put("void", "V");
        METHOD_HANDLE_POLYMORPHIC_NAMES.add("invoke");
        METHOD_HANDLE_POLYMORPHIC_NAMES.add("invokeExact");
        METHOD_HANDLE_POLYMORPHIC_NAMES.add("invokeBasic");
        METHOD_HANDLE_POLYMORPHIC_NAMES.add("linkToVirtual");
        METHOD_HANDLE_POLYMORPHIC_NAMES.add("linkToStatic");
        METHOD_HANDLE_POLYMORPHIC_NAMES.add("linkToSpecial");
        METHOD_HANDLE_POLYMORPHIC_NAMES.add("linkToInterface");
        VAR_HANDLE_POLYMORPHIC_NAMES.add("get");
        VAR_HANDLE_POLYMORPHIC_NAMES.add("set");
        VAR_HANDLE_POLYMORPHIC_NAMES.add("getVolatile");
        VAR_HANDLE_POLYMORPHIC_NAMES.add("setVolatile");
        VAR_HANDLE_POLYMORPHIC_NAMES.add("getOpaque");
        VAR_HANDLE_POLYMORPHIC_NAMES.add("setOpaque");
        VAR_HANDLE_POLYMORPHIC_NAMES.add("getAcquire");
        VAR_HANDLE_POLYMORPHIC_NAMES.add("setRelease");
        VAR_HANDLE_POLYMORPHIC_NAMES.add("compareAndSet");
        VAR_HANDLE_POLYMORPHIC_NAMES.add("compareAndExchange");
        VAR_HANDLE_POLYMORPHIC_NAMES.add("compareAndExchangeAcquire");
        VAR_HANDLE_POLYMORPHIC_NAMES.add("compareAndExchangeRelease");
        VAR_HANDLE_POLYMORPHIC_NAMES.add("weakCompareAndSet");
        VAR_HANDLE_POLYMORPHIC_NAMES.add("weakCompareAndSetPlain");
        VAR_HANDLE_POLYMORPHIC_NAMES.add("weakCompareAndSetAcquire");
        VAR_HANDLE_POLYMORPHIC_NAMES.add("weakCompareAndSetRelease");
        VAR_HANDLE_POLYMORPHIC_NAMES.add("getAndSet");
        VAR_HANDLE_POLYMORPHIC_NAMES.add("getAndSetAcquire");
        VAR_HANDLE_POLYMORPHIC_NAMES.add("getAndSetRelease");
        VAR_HANDLE_POLYMORPHIC_NAMES.add("getAndAdd");
        VAR_HANDLE_POLYMORPHIC_NAMES.add("getAndAddAcquire");
        VAR_HANDLE_POLYMORPHIC_NAMES.add("getAndAddReleasegetAndAddRelease");
        VAR_HANDLE_POLYMORPHIC_NAMES.add("getAndBitwiseOr");
        VAR_HANDLE_POLYMORPHIC_NAMES.add("getAndBitwiseOrAcquire");
        VAR_HANDLE_POLYMORPHIC_NAMES.add("getAndBitwiseOrRelease");
        VAR_HANDLE_POLYMORPHIC_NAMES.add("getAndBitwiseAnd");
        VAR_HANDLE_POLYMORPHIC_NAMES.add("getAndBitwiseAndAcquire");
        VAR_HANDLE_POLYMORPHIC_NAMES.add("getAndBitwiseAndRelease");
        VAR_HANDLE_POLYMORPHIC_NAMES.add("getAndBitwiseXor");
        VAR_HANDLE_POLYMORPHIC_NAMES.add("getAndBitwiseXorAcquire");
        VAR_HANDLE_POLYMORPHIC_NAMES.add("getAndBitwiseXorRelease");
    }


    public static final String getParameterPart(String string) {
        return string.substring(0, string.lastIndexOf(")") + 1);
    }

    public static String javaTypeToDescriptor(String string) {
        return javaTypeToDescriptor(string, (Map) null);
    }

    public static MethodSignature remapClassNames(MethodSignature methodSignature1, Map map1) {
        String string = methodSignature1.getDescriptor();
        String string1 = remapDescriptor(string, map1);
        return string1.equals(string) ? methodSignature1 : new MethodSignature(methodSignature1.getName(), string1);
    }

    public String formatDeclaration(Map map1, String string) {
        return this.formatReturnType(map1) + " " + (string != null && this.isConstructor() ? string : this.name) + this.formatParameters(map1);
    }

    public String toDisplayString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (this.returnDescriptor != null && this.returnDescriptor.length() > 0) {
            stringBuilder.append(ConstantPoolEntry.descriptorToJavaType(this.returnDescriptor));
            stringBuilder.append(" ");
        }

        stringBuilder.append(this.formatSignature());
        return stringBuilder.toString();
    }

    @Override
    public Object clone() {
        return new MethodSignature(this.name, this.parameterDescriptor, this.returnDescriptor, this.hash, this.nameTypeSignature);
    }

    public static boolean isSignaturePolymorphic(String string, String string1, ClassHierarchyQuery classHierarchyQuery) throws ZkmException, IOException {
        if (METHOD_HANDLE_POLYMORPHIC_NAMES.contains(string1)) {
            return string.equals("java/lang/invoke/MethodHandle") ? true : classHierarchyQuery.isSubclass(string, "java/lang/invoke/MethodHandle");
        } else if (VAR_HANDLE_POLYMORPHIC_NAMES.contains(string1)) {
            return string.equals("java/lang/invoke/VarHandle") ? true : classHierarchyQuery.isSubclass(string, "java/lang/invoke/VarHandle");
        } else {
            return false;
        }
    }

    public final String getDescriptor() {
        StringBuilder stringBuilder = new StringBuilder(this.parameterDescriptor.length() + this.returnDescriptor.length());
        stringBuilder.append(this.parameterDescriptor);
        stringBuilder.append(this.returnDescriptor);
        return stringBuilder.toString();
    }

    @Override
    public int hashCode() {
        return this.hash;
    }

    @Override
    public String toString() {
        return ConstantPoolEntry.descriptorToJavaType(this.returnDescriptor) + " " + this.formatSignature();
    }

    public MethodSignature(String string, String string1) {
        super(string, string1);
        this.returnDescriptor = string1.substring(this.parameterDescriptor.length()).intern();
        this.hash = (string + this.parameterDescriptor + this.returnDescriptor).hashCode();
    }

    public static boolean isPrimitiveKeyword(Object object) {
        return PRIMITIVE_TYPE_DESCRIPTORS.containsKey(object);
    }

    public static String remapDescriptor(String string, Map map1) {
        List list1 = ConstantPoolEntry.getParameterTypes(string);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(");

        for (int i = 0; i < list1.size(); i++) {
            String string1 = remapTypeDescriptor((String) list1.get(i), map1);
            stringBuilder.append(string1);
        }

        stringBuilder.append(")");
        String string2 = string.substring(string.indexOf(")") + 1);
        String string3 = remapTypeDescriptor(string2, map1);
        stringBuilder.append(string3);
        return stringBuilder.toString();
    }

    public static String toPropertyName(String string, String string1) {
        String string2 = string.substring(string1.length());
        return string2.length() > 0 ? Introspector.decapitalize(string2) : "";
    }

    public static String toTypeDescriptor(String string) {
        String string1 = string.replace('.', '/');
        StringBuilder stringBuilder = new StringBuilder(Math.max(string1.length(), 16));
        int ba = string1.length();
        if ((ba > 1 || ba == 1 && !ConstantPoolEntry.isPrimitiveDescriptor(string1)) && string1.charAt(0) != '[' && string1.charAt(ba - 1) != ';') {
            stringBuilder.append('L');
            stringBuilder.append(string1);
            stringBuilder.append(';');
            return stringBuilder.toString();
        }

        int bb = string1.lastIndexOf(91);
        if (bb > -1 && (bb < ba - 2 || bb < ba - 1 && !ConstantPoolEntry.isPrimitiveDescriptor(string1.substring(bb + 1)))) {
            String string2 = string1.substring(bb + 1);
            if (string2.charAt(0) != 'L' && string2.charAt(string2.length() - 1) != ';') {
                stringBuilder.append(string1.substring(0, bb + 1));
                stringBuilder.append('L');
                stringBuilder.append(string2);
                stringBuilder.append(';');
                return stringBuilder.toString();
            }
        }

        return string1;
    }

    private MethodSignature(String string, String string1, String string2, int hash, FieldNameTypeSignature fieldNameTypeSignature) {
        super(string, string1);
        this.returnDescriptor = string2.intern();
        this.hash = hash;
        this.nameTypeSignature = fieldNameTypeSignature;
    }

    public static String remapTypeDescriptor(String string, Map map1) {
        int ba = 0;

        for (int i = 0; i < string.length() && string.charAt(i) == '['; i++) {
            ba++;
        }

        String string2 = string.substring(ba);
        String string1;
        if (string2.length() > 1) {
            string2 = string2.substring(1, string2.length() - 1);
            string1 = "L" + (String) ZkmUtils.mapOrSelf(string2, map1) + ";";
        } else {
            string1 = string2;
        }

        return ba > 0 ? string.substring(0, ba) + string1 : string1;
    }

    public MethodSignature(String string, String string1, String string2) {
        super(string, string1);
        this.returnDescriptor = string2.intern();
        this.hash = (string + string1 + string2).hashCode();
    }

    public FieldNameTypeSignature getNameTypeSignature() {
        if (this.nameTypeSignature == null) {
            this.nameTypeSignature = new FieldNameTypeSignature(this.name, this.parameterDescriptor, this.returnDescriptor);
        }

        return this.nameTypeSignature;
    }

    public String formatReturnType(Map map1) {
        return descriptorToJavaType(this.returnDescriptor, map1);
    }

    public static String javaTypeToDescriptor(String string, Map map1) {
        StringBuilder stringBuilder = new StringBuilder(Math.max(string.length(), 16));
        int ba = 0;
        int bb = string.indexOf("[]");
        String string1;
        if (bb > -1) {
            ba = ZkmStringUtils.countOccurrences(string, "[]");
            string1 = string.substring(0, bb);
        } else {
            string1 = string;
        }

        for (int i = 0; i < ba; i++) {
            stringBuilder.append("[");
        }

        StringTokenizer stringTokenizer = new StringTokenizer(string1, ".");
        int bd = stringTokenizer.countTokens();
        if (bd == 1) {
            String string2 = stringTokenizer.nextToken();
            String string3 = (String) PRIMITIVE_TYPE_DESCRIPTORS.get(string2);
            if (string3 != null) {
                stringBuilder.append(string3);
            } else {
                string2 = (String) ZkmUtils.mapOrSelf(string2, map1);
                stringBuilder.append("L");
                stringBuilder.append(string2);
                stringBuilder.append(";");
            }
        } else {
            StringBuilder stringBuilder1 = new StringBuilder();

            for (int i = 0; i < bd; i++) {
                if (i > 0) {
                    stringBuilder1.append("/");
                }

                stringBuilder1.append(stringTokenizer.nextToken());
            }

            String string4 = (String) ZkmUtils.mapOrSelf(stringBuilder1.toString(), map1);
            stringBuilder.append("L");
            stringBuilder.append(string4);
            stringBuilder.append(";");
        }

        return stringBuilder.toString();
    }

    @Override
    public final String getReturnDescriptor() {
        return this.returnDescriptor;
    }

    public static String parseParameterList(String string) {
        return parseParameterList(string, (Map) null);
    }

    @Override
    public boolean equals(Object object) {
        if (object != null && object instanceof MethodSignature) {
            MethodSignature methodSignature2 = (MethodSignature) object;
            return this.hash == methodSignature2.hash
                    && this.name == methodSignature2.name
                    && this.parameterDescriptor == methodSignature2.parameterDescriptor
                    && this.returnDescriptor == methodSignature2.returnDescriptor;
        } else {
            return false;
        }
    }

    public static String toAccessorName(String string, String string1) {
        StringBuilder stringBuilder = new StringBuilder(string.length() + string1.length());
        stringBuilder.append(string);
        char ba = string1.charAt(0);
        if (!Character.isLowerCase(ba) || string.length() <= 0 || string1.length() != 1 && Character.isUpperCase(string1.charAt(1))) {
            stringBuilder.append(string1);
        } else {
            stringBuilder.append(Character.toUpperCase(ba));
            stringBuilder.append(string1.substring(1));
        }

        return stringBuilder.toString();
    }

    public MethodSignature(String string) {
        this(string.substring(0, string.indexOf("(")), string.substring(string.indexOf("(")));
    }

    public static final String getReturnPart(String string) {
        return string.substring(string.lastIndexOf(")") + 1);
    }

    public String formatDeclaration(Map map1) {
        return this.formatDeclaration(map1, (String) null);
    }
}
