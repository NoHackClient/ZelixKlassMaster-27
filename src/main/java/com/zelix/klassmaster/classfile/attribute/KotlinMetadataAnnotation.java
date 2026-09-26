package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.AbstractFieldInfo;
import com.zelix.klassmaster.classfile.AbstractMethodInfo;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ConstantUtf8;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.config.IgnoreMissingReferencesSpec;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.BooleanFlag;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.ZkmStringUtils;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KotlinMetadataAnnotation extends AnnotationEntry {
    public int kind;
    public String[] data1;
    public String[] data2;
    public String extraString;
    public String packageName;
    public int[] metadataVersion;
    public int[] bytecodeVersion;
    public Integer extraInt;

    @Override
    public void collectReferencedClasses(Object object) {
        Set set1 = (Set) object;
        if (super.valid) {
            if (super.annotationClass != null) {
                set1.add(super.annotationClass);
            }

            int ba = 0;
            int bb = 0;

            for (int i = super.elementCount; bb < i; i = super.elementCount) {
                super.elements[ba].collectReferencedClasses(set1);
                bb = ++ba;
            }
        }
    }

    @Override
    public void writeRemapped(DataOutputStream dataOutputStream, Object object, Object object1) throws IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object1;
        Map map1 = (Map) object;
        ConstantUtf8 constantUtf8 = (ConstantUtf8) map1.get(super.typeConstant);
        DataOutputStream dataOutputStream1;
        int bd;
        if (constantUtf8 != null) {
            dataOutputStream.writeShort(constantUtf8.getIndex());
            dataOutputStream1 = dataOutputStream;
            bd = super.elementCount;
        } else {
            dataOutputStream.writeShort(super.typeConstant.getIndex());
            dataOutputStream1 = dataOutputStream;
            bd = super.elementCount;
        }

        dataOutputStream1.writeShort(bd);
        int ba = 0;
        int bb = 0;

        for (int i = super.elementCount; bb < i; i = super.elementCount) {
            super.elements[ba].writeRemapped(dataOutputStream, map1, scriptEnvironment1);
            bb = ++ba;
        }
    }

    @Override
    public int getByteLength() {
        return super.getByteLength();
    }

    @Override
    public void replaceUtf8Constant(ConstantUtf8 constantUtf8, ConstantUtf8 constantUtf81) {
        if (super.typeConstant == constantUtf8) {
            super.typeConstant = constantUtf81;
        }
    }

    @Override
    public ClassFileBase getAnnotationClass() {
        return super.annotationClass;
    }

    public KotlinMetadataAnnotation(
            ClassFileComponent classFileComponent, int ba, ConstantPoolEntry constantPoolEntry, ClassFileInputStream classFileInputStream, ListMultimap listMultimap
    ) throws IOException {
        super(classFileComponent, ba, constantPoolEntry, classFileInputStream, listMultimap);
        int bb = 0;
        int bg = 0;

        for (int i = super.elementCount; bg < i; i = super.elementCount) {
            AnnotationElementPair annotationElementPair = super.elements[bb];
            AnnotationElementValue annotationElementValue = annotationElementPair.getValue();
            switch (annotationElementPair.getElementName()) {
                case "k":
                    this.kind = ((IntElementValue) annotationElementValue).getIntValue();
                    break;
                case "d1":
                    if (!annotationElementValue.isArrayOrClassValue()) {
                        break;
                    }

                    AnnotationElementValue[] annotationElementValues3 = ((NestedAnnotationElementValue) annotationElementValue).getElementValues();
                    this.data1 = new String[annotationElementValues3.length];

                    for (int j = 0; j < annotationElementValues3.length; j++) {
                        AnnotationElementValue annotationElementValue4 = annotationElementValues3[j];
                        if (annotationElementValue4 instanceof StringElementValue) {
                            StringElementValue stringElementValue3 = (StringElementValue) annotationElementValue4;
                            this.data1[j] = stringElementValue3.getStringValue();
                        }
                    }
                    break;
                case "d2":
                    if (!annotationElementValue.isArrayOrClassValue()) {
                        break;
                    }

                    AnnotationElementValue[] annotationElementValues2 = ((NestedAnnotationElementValue) annotationElementValue).getElementValues();
                    this.data2 = new String[annotationElementValues2.length];

                    for (int j = 0; j < annotationElementValues2.length; j++) {
                        AnnotationElementValue annotationElementValue3 = annotationElementValues2[j];
                        if (annotationElementValue3 instanceof StringElementValue) {
                            StringElementValue stringElementValue2 = (StringElementValue) annotationElementValue3;
                            this.data2[j] = stringElementValue2.getStringValue();
                        }
                    }
                    break;
                case "xs":
                    if (annotationElementValue instanceof StringElementValue) {
                        StringElementValue stringElementValue1 = (StringElementValue) annotationElementValue;
                        this.extraString = stringElementValue1.getStringValue();
                    }
                    break;
                case "pn":
                    if (annotationElementValue instanceof StringElementValue) {
                        StringElementValue stringElementValue = (StringElementValue) annotationElementValue;
                        this.packageName = stringElementValue.getStringValue();
                    }
                    break;
                case "mv":
                    if (!annotationElementValue.isArrayOrClassValue()) {
                        break;
                    }

                    AnnotationElementValue[] annotationElementValues1 = ((NestedAnnotationElementValue) annotationElementValue).getElementValues();
                    this.metadataVersion = new int[annotationElementValues1.length];

                    for (int j = 0; j < annotationElementValues1.length; j++) {
                        AnnotationElementValue annotationElementValue2 = annotationElementValues1[j];
                        if (annotationElementValue2 instanceof IntElementValue) {
                            IntElementValue intElementValue2 = (IntElementValue) annotationElementValue2;
                            this.metadataVersion[j] = intElementValue2.getIntValue();
                        }
                    }
                    break;
                case "bv":
                    if (!annotationElementValue.isArrayOrClassValue()) {
                        break;
                    }

                    AnnotationElementValue[] annotationElementValues = ((NestedAnnotationElementValue) annotationElementValue).getElementValues();
                    this.bytecodeVersion = new int[annotationElementValues.length];

                    for (int j = 0; j < annotationElementValues.length; j++) {
                        AnnotationElementValue annotationElementValue1 = annotationElementValues[j];
                        if (annotationElementValue1 instanceof IntElementValue) {
                            IntElementValue intElementValue1 = (IntElementValue) annotationElementValue1;
                            this.bytecodeVersion[j] = intElementValue1.getIntValue();
                        }
                    }
                    break;
                case "xi":
                    if (annotationElementValue instanceof IntElementValue) {
                        IntElementValue intElementValue = (IntElementValue) annotationElementValue;
                        this.extraInt = intElementValue.getIntValue();
                    }
            }

            bg = ++bb;
        }
    }

    @Override
    public boolean isValid() {
        return super.isValid();
    }

    @Override
    public void collectReferencedMembers(Set set1, Set set2, Set set3, Set set4) {
        if (super.annotationClass != null && super.annotationClass.isProgramClass()) {
            set1.add((ProgramClass) super.annotationClass);
        }

        int ba = 0;
        int bb = ba;

        for (int i = super.elementCount; bb < i; i = super.elementCount) {
            super.elements[ba].collectReferencedMembers(super.annotationClass, set1, set2, set3, set4);
            bb = ++ba;
        }
    }

    @Override
    public String getElementClassValue(String string, boolean bl) {
        int ba = 0;
        int bb = 0;

        for (int i = super.elementCount; bb < i; i = super.elementCount) {
            String string1;
            if (bl) {
                string1 = super.elements[ba].getResolvedElementName();
            } else {
                string1 = super.elements[ba].getElementName();
            }

            if (string.equals(string1)) {
                return super.elements[ba].getClassValueName();
            }

            bb = ++ba;
        }

        return null;
    }

    @Override
    public String getTypeName() {
        if (super.valid) {
            String string = super.typeConstant.getValue();
            return string.substring(1, string.length() - 1);
        } else {
            return null;
        }
    }

    @Override
    public String getErrorMessage(Object object, Object object1, Object object2) {
        int ba = (Integer) object1;
        int bb = (Integer) object2;
        long bg = (long) ((Integer) object).intValue() << 48 | (long) ba << 32 >>> 16;
        long bh = (bg | (long) bb << 48 >>> 48) ^ 0L;
        int bc = (int) (((bg | (long) bb << 48 >>> 48) ^ 0L) >>> 48);
        int bd = (int) (((bg | (long) bb << 48 >>> 48) ^ 0L) << 16 >>> 32);
        int be = (int) (bh << 48 >>> 48);
        char bf = (char) bc;
        Integer integer1 = Integer.valueOf((char) be);
        Integer integer = bd;
        return super.getErrorMessage(Integer.valueOf(bf), integer, integer1);
    }

    @Override
    public String getTypeDescriptor() {
        return super.typeConstant.getValue();
    }

    @Override
    public void updateAfterClassRename(Object object1, Object object) {
        HashMap hashMap = (HashMap) object;
        if (super.annotationClass != null) {
            String string = ClassFileBase.extractClassName(super.typeConstant.getValue());
            if (!super.annotationClass.getClassName().equals(string)) {
                String string1 = ClassFileBase.toTypeDescriptor(super.annotationClass.getClassName(), super.arrayDimensions);
                super.typeConstant.setValue(string1);
            }

            this.getOwningClass();
            int ba = 0;
            int bb = 0;
            int be = 0;

            for (int i = super.elementCount; be < i; i = super.elementCount) {
                AnnotationElementPair annotationElementPair = super.elements[bb];
                AnnotationElementValue annotationElementValue = annotationElementPair.getValue();
                switch (annotationElementPair.getElementName()) {
                    case "k":
                        ba = ((IntElementValue) annotationElementValue).getIntValue();
                        break;
                    case "d1":
                        if (ba != 4 || !annotationElementValue.isArrayOrClassValue()) {
                            break;
                        }

                        AnnotationElementValue[] annotationElementValues1 = ((NestedAnnotationElementValue) annotationElementValue).getElementValues();

                        for (int j = 0; j < annotationElementValues1.length; j++) {
                            AnnotationElementValue annotationElementValue2 = annotationElementValues1[j];
                            if (annotationElementValue2 instanceof StringElementValue) {
                                StringElementValue stringElementValue2 = (StringElementValue) annotationElementValue2;
                                String string8 = stringElementValue2.getStringValue();
                                if (ZkmStringUtils.countChar(string8, '/') >= 1) {
                                    BooleanFlag booleanFlag2 = new BooleanFlag();
                                    String string11 = ConstantPoolEntry.remapClassNameString(string8, hashMap, booleanFlag2);
                                    if (booleanFlag2.getValue() && !string11.equals(string8)) {
                                        stringElementValue2.setStringValue(string11);
                                    }
                                }
                            }
                        }
                        break;
                    case "d2":
                        if (!annotationElementValue.isArrayOrClassValue()) {
                            break;
                        }

                        AnnotationElementValue[] annotationElementValues = ((NestedAnnotationElementValue) annotationElementValue).getElementValues();
                        int bc = 0;

                        for (; bc < annotationElementValues.length; bc++) {
                            AnnotationElementValue annotationElementValue1 = annotationElementValues[bc];
                            if (annotationElementValue1 instanceof StringElementValue) {
                                StringElementValue stringElementValue1 = (StringElementValue) annotationElementValue1;
                                String string4 = stringElementValue1.getStringValue();
                                if (string4.startsWith("(") && string4.indexOf(41) < string4.length() && ZkmStringUtils.countChar(string4, ')') == 1) {
                                    String string9 = MethodSignature.getParameterPart(string4);
                                    String string10 = MethodSignature.getReturnPart(string4);
                                    String string6 = '(' + ConstantPoolEntry.remapDescriptorClassNames(string9.substring(1, string9.length() - 1), hashMap) + ')';
                                    String string7 = ConstantPoolEntry.remapClassNameString(string10, hashMap, new BooleanFlag());
                                    if (!string6.equals(string9) || !string7.equals(string10)) {
                                        stringElementValue1.setStringValue(string6 + string7);
                                    }
                                } else if (string4.startsWith("L") && string4.endsWith(";") || ZkmStringUtils.countChar(string4, '/') >= 1) {
                                    BooleanFlag booleanFlag1 = new BooleanFlag();
                                    String string5 = ConstantPoolEntry.remapClassNameString(string4, hashMap, booleanFlag1);
                                    if (booleanFlag1.getValue() && !string5.equals(string4)) {
                                        stringElementValue1.setStringValue(string5);
                                    }
                                }
                            }
                        }
                        break;
                    case "xs":
                        if (annotationElementValue instanceof StringElementValue) {
                            StringElementValue stringElementValue = (StringElementValue) annotationElementValue;
                            String string2 = stringElementValue.getStringValue();
                            if (ZkmStringUtils.countChar(string2, '/') >= 1) {
                                BooleanFlag booleanFlag = new BooleanFlag();
                                String string3 = ConstantPoolEntry.remapClassNameString(string2, hashMap, booleanFlag);
                                if (booleanFlag.getValue() && !string3.equals(string2)) {
                                    stringElementValue.setStringValue(string3);
                                }
                            }
                        }
                    case "mv":
                    case "bv":
                    case "pn":
                    case "xi":
                }

                be = ++bb;
            }
        }
    }

    @Override
    public void updateAfterFieldRename() {
        if (super.annotationClass != null && super.annotationClass.getOriginalClassName().equals("kotlin/Metadata")) {
            ClassFileBase classFileBase = this.getOwningClass();
            AbstractMethodInfo[] abstractMethodInfos = classFileBase.getDeclaredMethods();
            AbstractFieldInfo[] abstractFieldInfos = classFileBase.getFields();
            HashSet hashSet = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(abstractFieldInfos.length));

            for (AbstractMethodInfo abstractMethodInfo : abstractMethodInfos) {
                hashSet.add(abstractMethodInfo.getOriginalMemberName());
            }

            ListMultimap listMultimap = new ListMultimap(abstractFieldInfos.length);

            for (AbstractFieldInfo abstractFieldInfo : abstractFieldInfos) {
                listMultimap.addValue(abstractFieldInfo.getOriginalMemberName(), abstractFieldInfo);
            }

            int bb = 0;
            int bc = 0;

            for (int i = super.elementCount; bc < i; i = super.elementCount) {
                AnnotationElementPair annotationElementPair = super.elements[bb];
                if (annotationElementPair.getElementName().equals("d2")) {
                    NestedAnnotationElementValue nestedAnnotationElementValue = (NestedAnnotationElementValue) annotationElementPair.getValue();

                    for (AnnotationElementValue annotationElementValue : nestedAnnotationElementValue.getElementValues()) {
                        if (annotationElementValue instanceof StringElementValue) {
                            StringElementValue stringElementValue = (StringElementValue) annotationElementValue;
                            String string = stringElementValue.getStringValue();
                            List list1 = listMultimap.getValues(string);
                            if (list1 != null && !hashSet.contains(string)) {
                                boolean bl = true;
                                String string1 = null;
                                Iterator iterator = list1.iterator();

                                while (iterator.hasNext()) {
                                    AbstractFieldInfo abstractFieldInfo1 = (AbstractFieldInfo) iterator.next();
                                    if (string1 == null) {
                                        string1 = abstractFieldInfo1.getSourceName();
                                    } else if (!string1.equals(abstractFieldInfo1.getSourceName())) {
                                        bl = false;
                                        break;
                                    }
                                }

                                if (bl && !string.equals(string1)) {
                                    stringElementValue.setStringValue(string1);
                                }
                            }
                        }
                    }
                }

                bc = ++bb;
            }
        } else {
            int ba = 0;
            int be = ba;

            for (int i = super.elementCount; be < i; i = super.elementCount) {
                super.elements[ba].updateAfterFieldRename();
                be = ++ba;
            }
        }
    }

    @Override
    public String getResolvedTypeName() {
        if (super.valid) {
            return super.annotationClass != null ? super.annotationClass.getOriginalClassName() : this.getTypeName();
        } else {
            return null;
        }
    }

    @Override
    public void updateAfterMethodRename() {
        if ((HiddenOptionFlags.TRANSLATE_KOTLIN || HiddenOptionFlags.TRANSLATE_KOTLIN_METADATA)
                && super.annotationClass != null
                && super.annotationClass.getOriginalClassName().equals("kotlin/Metadata")) {
            ClassFileBase classFileBase = this.getOwningClass();
            AbstractFieldInfo[] abstractFieldInfos = classFileBase.getFields();
            HashSet hashSet = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(abstractFieldInfos.length));

            for (AbstractFieldInfo abstractFieldInfo : abstractFieldInfos) {
                hashSet.add(abstractFieldInfo.getOriginalMemberName());
            }

            AbstractMethodInfo[] abstractMethodInfos = classFileBase.getDeclaredMethods();
            ListMultimap listMultimap = new ListMultimap(abstractMethodInfos.length);

            for (AbstractMethodInfo abstractMethodInfo : abstractMethodInfos) {
                listMultimap.addValue(abstractMethodInfo.getOriginalMemberName(), abstractMethodInfo);
            }

            int bb = 0;
            int bc = 0;

            for (int i = super.elementCount; bc < i; i = super.elementCount) {
                AnnotationElementPair annotationElementPair = super.elements[bb];
                if (annotationElementPair.getElementName().equals("d2")) {
                    AnnotationElementValue annotationElementValue1 = annotationElementPair.getValue();
                    if (annotationElementValue1.isArrayOrClassValue()) {
                        NestedAnnotationElementValue nestedAnnotationElementValue = (NestedAnnotationElementValue) annotationElementValue1;

                        for (AnnotationElementValue annotationElementValue : nestedAnnotationElementValue.getElementValues()) {
                            if (annotationElementValue instanceof StringElementValue) {
                                StringElementValue stringElementValue = (StringElementValue) annotationElementValue;
                                String string = stringElementValue.getStringValue();
                                List list1 = listMultimap.getValues(string);
                                if (list1 != null && !hashSet.contains(string)) {
                                    boolean bl = true;
                                    String string1 = null;
                                    Iterator iterator = list1.iterator();

                                    while (iterator.hasNext()) {
                                        AbstractMethodInfo abstractMethodInfo1 = (AbstractMethodInfo) iterator.next();
                                        if (string1 == null) {
                                            string1 = abstractMethodInfo1.getSourceName();
                                        } else if (!string1.equals(abstractMethodInfo1.getSourceName())) {
                                            bl = false;
                                            break;
                                        }
                                    }

                                    if (bl && !string.equals(string1)) {
                                        stringElementValue.setStringValue(string1);
                                    }
                                }
                            }
                        }
                    }
                }

                bc = ++bb;
            }
        } else {
            int ba = 0;
            int be = ba;

            for (int i = super.elementCount; be < i; i = super.elementCount) {
                super.elements[ba].updateAfterMethodRename();
                be = ++ba;
            }
        }
    }

    @Override
    public void resolveReferences(ClassResolver classResolver1, IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1, ScriptEnvironment scriptEnvironment1) throws ZkmException, IOException {
        MutableInt mutableInt = new MutableInt(0);
        String string = ClassFileBase.extractClassName(super.typeConstant.getValue(), mutableInt);
        super.arrayDimensions = mutableInt.getValue();
        if (string != null) {
            super.annotationClass = classResolver1.getClassFile(
                    string, "analyzing annotations in class '" + this.getDisplayLocationName() + "' (C)", ignoreMissingReferencesSpec1
            );
        }

        int ba = 0;
        int bb = 0;

        for (int i = super.elementCount; bb < i; i = super.elementCount) {
            super.elements[ba].resolveReferences(super.annotationClass, classResolver1, ignoreMissingReferencesSpec1, scriptEnvironment1);
            bb = ++ba;
        }
    }

    @Override
    public void collectUsedConstants(char bd, int be, UsedConstantsCollector usedConstantsCollector, char bf) {
        if (super.valid) {
            super.typeConstant.registerUsage(usedConstantsCollector, this, this.getParent());
            int ba = 0;
            int bb = 0;

            for (int i = super.elementCount; bb < i; i = super.elementCount) {
                super.elements[ba].collectUsedConstants('\u0000', 1103830477, usedConstantsCollector, '鞍');
                bb = ++ba;
            }
        }
    }

    @Override
    public void write(int ba, DataOutputStream dataOutputStream, int bb, int bc) throws IOException {
        long bj = ((long) ba << 32 | (long) bb << 48 >>> 32 | (long) bc << 48 >>> 48) ^ 0L;
        int bd = (int) ((((long) ba << 32 | (long) bb << 48 >>> 32 | (long) bc << 48 >>> 48) ^ 0L) >>> 32);
        int be = (int) ((((long) ba << 32 | (long) bb << 48 >>> 32 | (long) bc << 48 >>> 48) ^ 0L) << 32 >>> 48);
        int bf = (int) (bj << 48 >>> 48);
        dataOutputStream.writeShort(super.typeConstant.getIndex());
        dataOutputStream.writeShort(super.elementCount);
        int bg = 0;
        int bi = 0;

        for (int i = super.elementCount; bi < i; i = super.elementCount) {
            super.elements[bg].write(bd, dataOutputStream, (char) be, (char) bf);
            bi = ++bg;
        }
    }
}
