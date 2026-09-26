package com.zelix.klassmaster.changelog;

import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.flow.OpaquePredicateField;
import com.zelix.klassmaster.obfuscator.parameters.AddedParameter;
import com.zelix.klassmaster.obfuscator.parameters.ChangedMethodDescriptor;
import com.zelix.klassmaster.util.BooleanFlag;
import com.zelix.klassmaster.util.IntegerCache;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.MutableLong;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ZkmStringUtils;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

public abstract class AbstractChangeLog {
    public static final IntegerCache INTEGER_CACHE = IntegerCache.getInstance();
    public static final char[] ENCODING_CHARS = "8WSNy5E-tIAreXQwVzuqnBoskGYJgKP2pfhFD$Z3TRbLc714xHdiO6UC9Mj_0amvl".toCharArray();
    public static final char PAD_CHAR = "8WSNy5E-tIAreXQwVzuqnBoskGYJgKP2pfhFD$Z3TRbLc714xHdiO6UC9Mj_0amvl".charAt(0);
    public static final char POSITIVE_MARKER_CHAR = ENCODING_CHARS[ENCODING_CHARS.length - 2];
    public static final char NEGATIVE_MARKER_CHAR = ENCODING_CHARS[ENCODING_CHARS.length - 1];
    public static final int[] EMPTY_INT_ARRAY = new int[0];
    public boolean parameterChangeDataPresent = false;
    public boolean paramChangeNodeDataPresent = false;
    public boolean parameterObfuscationPresent = false;

    public abstract void addSourceNameMapping(Object object, Object object1);

    public static String encodeMemberClassData(int ba, int bb, String string, boolean bl, boolean bl1, Random random1) {
        String string1 = ba + ZkmStringUtils.pad(String.valueOf(bb), 82, 4, 48);
        int bc = 0;

        for (int i = 0; i < string.length(); i++) {
            bc += string.charAt(i);
        }

        String string3 = Long.toOctalString(Long.parseLong(string1));
        long be = Long.parseLong(string3) + bc;
        StringBuilder stringBuilder = new StringBuilder();
        String string2 = String.valueOf(be);
        stringBuilder.append(string2);
        if (bl) {
            char bf = (char) (random1.nextInt(26) + 65);
            stringBuilder.append(' ');
            stringBuilder.append(bf);
        }

        if (bl1) {
            char bg = (char) (random1.nextInt(26) + 97);
            if (!bl) {
                stringBuilder.append(' ');
            }

            stringBuilder.append(bg);
        }

        return stringBuilder.toString();
    }

    public static String encodeOpaquePredicateData(OpaquePredicateField opaquePredicateField, Random random1) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("0");
        String string = opaquePredicateField.getFieldName() + " " + opaquePredicateField.getFieldType() + "java.lang.Object[]";
        stringBuilder.append(ZkmUtils.md5Hex(string));
        if (opaquePredicateField.hasSetter()) {
            stringBuilder.append(" a");
            String string1 = opaquePredicateField.getSetterName() + opaquePredicateField.getSetterDescriptor() + "java.lang.Object[]";
            stringBuilder.append(ZkmUtils.md5Hex(string1));
        }

        if (opaquePredicateField.hasGetter()) {
            stringBuilder.append(" b");
            String string2 = opaquePredicateField.getGetterName() + opaquePredicateField.getGetterDescriptor() + "java.lang.Object[]";
            stringBuilder.append(ZkmUtils.md5Hex(string2));
        }

        if (opaquePredicateField.hasNegatedGetter()) {
            stringBuilder.append(" c");
            String string3 = opaquePredicateField.getNegatedGetterName() + opaquePredicateField.getNegatedGetterDescriptor() + "java.lang.Object[]";
            stringBuilder.append(ZkmUtils.md5Hex(string3));
        }

        char ba;
        StringBuilder stringBuilder1;
        char bb;
        if (opaquePredicateField.isNonZeroValue()) {
            ba = (char) (random1.nextInt(26) + 65);
            stringBuilder1 = stringBuilder;
            bb = ' ';
        } else {
            ba = (char) (random1.nextInt(26) + 97);
            stringBuilder1 = stringBuilder;
            bb = ' ';
        }

        stringBuilder1.append(bb);
        stringBuilder.append(ba);
        return stringBuilder.toString();
    }

    public abstract void addModuleReferenceObfuscationClass(Object object, Object object1);

    public abstract void setMethodParameterChangeClasses(Object object);

    public abstract void incrementClassCount();

    public static int getNonDigitPrefixLength(String string) {
        char ba = string.charAt(0);
        if (ba >= '0' && ba <= '9') {
            return 1;
        }

        char[] bb = string.toCharArray();
        int bc = bb.length;

        for (int i = 1; i < bb.length; i++) {
            char be = string.charAt(i);
            if (be >= '0' && be <= '9') {
                bc = i;
                break;
            }
        }

        return bc;
    }

    public abstract boolean acceptsFieldChanges();

    public Integer getCachedInteger(int ba) {
        return INTEGER_CACHE.valueOf(ba);
    }

    public abstract void setAutoReflectionClass(Object object);

    public static String encodeParameterChangeData(ChangedMethodDescriptor changedMethodDescriptor, Long long1, String string) {
        int[] ba;
        if (changedMethodDescriptor == null) {
            ba = EMPTY_INT_ARRAY;
        } else {
            ba = changedMethodDescriptor.getParamIndices();
        }

        String string1 = ZkmStringUtils.pad(ZkmUtils.toCustomBase(ZkmUtils.xorLongWithKey(long1, string), ENCODING_CHARS), 82, 8, ENCODING_CHARS[0]);
        long bb = 0L;

        for (int i = 0; i < 3; i++) {
            if (i < ba.length) {
                bb |= ba[i];
            } else {
                bb |= 256L;
            }

            if (i < 2) {
                bb <<= 9;
            }
        }

        long bd = ZkmUtils.xorLongWithKey(bb, string);
        String string2 = ZkmStringUtils.pad(ZkmUtils.toCustomBase(bd, ENCODING_CHARS), 82, 6, ENCODING_CHARS[0]);
        return string1 + string2;
    }

    public void setParameterChangeDataPresent() {
        this.parameterChangeDataPresent = true;
    }

    public static String getChangeLogEncoding() {
        return HiddenOptionFlags.NEW_CHANGE_LOG_ENCODING;
    }

    public static String encodeParamChangeNodeData(long ba, long bb, long bc, long bd, long be, String string) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(encodeSignedLong(ba, string));
        stringBuilder.append(encodeSignedLong(bb, string));
        stringBuilder.append(encodeSignedLong(bc, string));
        stringBuilder.append(encodeSignedLong(bd, string));
        stringBuilder.append(encodeSignedLong(be, string));
        return stringBuilder.toString();
    }

    public static void decodeParamChangeNodeData(
            String string,
            MutableLong mutableLong,
            MutableLong mutableLong1,
            MutableLong mutableLong2,
            MutableLong mutableLong3,
            MutableLong mutableLong4,
            String string1
    ) {
        String string2 = string.substring(0, 12);
        boolean bl = string2.charAt(0) == NEGATIVE_MARKER_CHAR;
        mutableLong.setValue(decodeLong(string2.substring(1), string1, bl));
        int bb = 24;
        string2 = string.substring(12, 24);
        bl = string2.charAt(0) == NEGATIVE_MARKER_CHAR;
        mutableLong1.setValue(decodeLong(string2.substring(1), string1, bl));
        int ba = bb;
        bb = ba + 11 + 1;
        string2 = string.substring(ba, bb);
        bl = string2.charAt(0) == NEGATIVE_MARKER_CHAR;
        mutableLong2.setValue(decodeLong(string2.substring(1), string1, bl));
        ba = bb;
        bb = ba + 11 + 1;
        string2 = string.substring(ba, bb);
        bl = string2.charAt(0) == NEGATIVE_MARKER_CHAR;
        mutableLong3.setValue(decodeLong(string2.substring(1), string1, bl));
        ba = bb;
        bb = ba + 11 + 1;
        string2 = string.substring(ba, bb);
        bl = string2.charAt(0) == NEGATIVE_MARKER_CHAR;
        mutableLong4.setValue(decodeLong(string2.substring(1), string1, bl));
    }

    public abstract void addClassMapping(Object object, Object object1);

    public abstract void addLineNumberMappings(String string, Map map1);

    public static String decodeName(String string, String string1) {
        long ba = Long.parseLong(string);
        int bb = 0;

        for (int i = 0; i < string1.length(); i++) {
            bb += string1.charAt(i);
        }

        long be = ba - bb;
        String string2 = String.valueOf(Long.parseLong(String.valueOf(be), 8));
        StringBuilder stringBuilder = new StringBuilder();

        while (string2.length() > 3) {
            int bd = string2.length() - 3;
            String string3 = string2.substring(bd);
            stringBuilder.append((char) Integer.parseInt(string3));
            string2 = string2.substring(0, bd);
        }

        stringBuilder.append((char) Integer.parseInt(string2));
        return stringBuilder.reverse().toString();
    }

    public abstract void addModuleAutoReflectionClass(Object object, Object object1);

    public abstract void incrementPackageCount();

    public abstract void addPackageFlowObfuscationData(Object object, Object object1, Object object2) throws ZkmException, IOException;

    public abstract void addPackageMapping(Object object, Object object1);

    public static long decodeLong(String string, String string1, boolean bl) {
        long ba = ZkmUtils.parseCustomBase(string, "8WSNy5E-tIAreXQwVzuqnBoskGYJgKP2pfhFD$Z3TRbLc714xHdiO6UC9Mj_0amvl");
        if (bl) {
            ba *= -1L;
        }

        return ZkmUtils.xorLongWithKey(ba, string1);
    }

    public static String encodeLongMagnitude(long ba, String string, BooleanFlag booleanFlag) {
        long bb = ZkmUtils.xorLongWithKey(ba, string);
        long bc;
        if (bb < 0L) {
            booleanFlag.setValue(true);
            bc = bb * -1L;
        } else {
            booleanFlag.setValue(false);
            bc = bb;
        }

        return ZkmUtils.toCustomBase(bc, ENCODING_CHARS);
    }

    public void setParamChangeNodeDataPresent() {
        this.paramChangeNodeDataPresent = true;
    }

    public abstract void setReferenceObfuscationClass(Object object);

    public static String mapTypeName(String string, Map map1) {
        int ba = string.indexOf(91);
        String string1;
        String string2;
        if (ba > -1) {
            string1 = string.substring(0, ba);
            string2 = string.substring(ba);
        } else {
            string1 = string;
            string2 = "";
        }

        if (!MethodSignature.isPrimitiveKeyword(string1)) {
            String string3 = (String) ZkmUtils.mapOrSelf(string1, map1);
            return string3 + string2;
        } else {
            return string;
        }
    }

    public static AddedParameter[] createAddedParameters(String string, List list1) {
        AddedParameter[] addedParameters1 = new AddedParameter[list1.size()];
        List list2 = MethodSignature.splitParameterDescriptors(string);

        for (int i = 0; i < list1.size(); i++) {
            int bb = (Integer) list1.get(i);
            String string1 = (String) list2.get(bb);
            addedParameters1[i] = new AddedParameter(bb, ((String) list2.get(bb)).charAt(0));
        }

        return addedParameters1;
    }

    public String mapTypeList(String string, Map map1) {
        if (string != null && string.length() != 0) {
            StringBuilder stringBuilder = new StringBuilder();
            StringTokenizer stringTokenizer = new StringTokenizer(string, ",");

            while (stringTokenizer.hasMoreTokens()) {
                String string1 = stringTokenizer.nextToken().trim();
                stringBuilder.append(mapTypeName(string1, map1));
                if (stringTokenizer.hasMoreTokens()) {
                    stringBuilder.append(", ");
                }
            }

            return stringBuilder.toString();
        } else {
            return "";
        }
    }

    public static Long decodeShortParameterChangeData(String string, String string1, ObservableHolder observableHolder, String string2) throws ZkmException, IOException {
        long ba = ZkmUtils.xorLongWithKey(ZkmUtils.parseCustomBase(string, "8WSNy5E-tIAreXQwVzuqnBoskGYJgKP2pfhFD$Z3TRbLc714xHdiO6UC9Mj_0amvl"), string2);
        ArrayList arrayList = new ArrayList();

        for (int i = 0; i < 3; i++) {
            int bc = (int) (ba & 511L);
            long bd;
            byte be;
            if (bc <= 255) {
                arrayList.add(INTEGER_CACHE.valueOf(bc));
                bd = ba;
                be = 9;
            } else {
                bd = ba;
                be = 9;
            }

            ba = bd >>> be;
        }

        Collections.reverse(arrayList);
        observableHolder.setValue(createAddedParameters(string1, arrayList));
        return ba;
    }

    public String joinTypeNames(String[] strings) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < strings.length; i++) {
            stringBuilder.append(strings[i]);
            if (i < strings.length - 1) {
                stringBuilder.append(", ");
            }
        }

        return stringBuilder.toString();
    }

    public abstract void addFieldMappings(Object object, Object object1);

    public static String decodePrefixedName(String string, String string1) {
        String string2 = string;
        int ba = getNonDigitPrefixLength(string2);
        string2 = string2.substring(ba);
        return decodeName(string2, string1);
    }

    public abstract void addModuleMethodParameterChangeClasses(Object object, Object object1);

    public abstract void addMethodMappings(String string, ListMultimap listMultimap);

    public static String encodeSignedLong(long ba, String string) {
        BooleanFlag booleanFlag = new BooleanFlag();
        String string1 = encodeLongMagnitude(ba, string, booleanFlag);
        char bb;
        if (booleanFlag.getValue()) {
            bb = NEGATIVE_MARKER_CHAR;
        } else {
            bb = POSITIVE_MARKER_CHAR;
        }

        return bb + ZkmStringUtils.pad(string1, 82, 11, PAD_CHAR);
    }

    public abstract void addMainFlowObfuscationData(Object object, Object object1) throws ZkmException, IOException;

    public boolean hasParameterObfuscation() {
        return this.parameterObfuscationPresent;
    }

    public abstract void addMemberFlowObfuscationData(Object object, Object object1);

    public static Long decodeParameterChangeData(String string, String string1, ObservableHolder observableHolder, String string2) throws ZkmException, IOException {
        if (string.length() < 14) {
            return decodeShortParameterChangeData(string, string1, observableHolder, string2);
        }

        String string3 = string.substring(0, 8);
        String string4 = string.substring(8);
        long ba = ZkmUtils.xorLongWithKey(ZkmUtils.parseCustomBase(string3, "8WSNy5E-tIAreXQwVzuqnBoskGYJgKP2pfhFD$Z3TRbLc714xHdiO6UC9Mj_0amvl"), string2);
        long bb = ZkmUtils.xorLongWithKey(ZkmUtils.parseCustomBase(string4, "8WSNy5E-tIAreXQwVzuqnBoskGYJgKP2pfhFD$Z3TRbLc714xHdiO6UC9Mj_0amvl"), string2);
        ArrayList arrayList = new ArrayList();

        for (int i = 0; i < 3; i++) {
            int bd = (int) (bb & 511L);
            long be;
            byte bf;
            if (bd <= 255) {
                arrayList.add(INTEGER_CACHE.valueOf(bd));
                be = bb;
                bf = 9;
            } else {
                be = bb;
                bf = 9;
            }

            bb = be >>> bf;
        }

        Collections.reverse(arrayList);
        observableHolder.setValue(createAddedParameters(string1, arrayList));
        return ba;
    }

    public boolean hasParameterChangeData() {
        return this.parameterChangeDataPresent;
    }

    public void markParameterObfuscationPresent() {
        this.parameterObfuscationPresent = true;
    }

    public static int[] decodeIntPair(String string, String string1) {
        long ba = Long.parseLong(string);
        int bb = 0;

        for (int i = 0; i < string1.length(); i++) {
            bb += string1.charAt(i);
        }

        long bg = ba - bb;
        String string2 = String.valueOf(Long.parseLong(String.valueOf(bg), 8));
        if (string2.length() < 5) {
            string2 = ZkmStringUtils.pad(string2, 82, 5, 48);
        }

        int bd = string2.length();
        int be = Integer.parseInt(string2.substring(0, bd - 4));
        int bf = Integer.parseInt(string2.substring(bd - 4));
        return new int[]{be, bf};
    }

    public abstract boolean acceptsLineNumberChanges();
}
