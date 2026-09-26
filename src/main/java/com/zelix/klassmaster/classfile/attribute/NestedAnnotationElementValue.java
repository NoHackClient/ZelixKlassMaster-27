package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.constpool.ConstantUtf8;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.config.IgnoreMissingReferencesSpec;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NestedAnnotationElementValue extends AnnotationElementValue {
    public AnnotationElementValue[] elementValues;
    private static final String INVALID_NESTED_MESSAGE = "Invalid nested annotation : ";

    @Override
    public void updateAfterClassRename(Object object, Object object1) {
        HashMap hashMap1 = (HashMap) object1;
        HashMap hashMap = (HashMap) object;
        int ba = 0;
        int bb = 0;

        for (AnnotationElementValue[] annotationElementValues = this.elementValues;
             bb < annotationElementValues.length;
             annotationElementValues = this.elementValues
        ) {
            this.elementValues[ba].updateAfterClassRename(hashMap, hashMap1);
            bb = ++ba;
        }
    }

    @Override
    public void updateAfterFieldRename() {
        int ba = 0;
        int bb = 0;

        for (AnnotationElementValue[] annotationElementValues = this.elementValues;
             bb < annotationElementValues.length;
             annotationElementValues = this.elementValues
        ) {
            this.elementValues[ba].updateAfterFieldRename();
            bb = ++ba;
        }
    }

    @Override
    public void collectReferencedMembers(Object object, Object object1, Object object2, Object object3) {
        Set set4 = (Set) object2;
        Set set2 = (Set) object;
        Set set1 = (Set) object3;
        Set set3 = (Set) object1;
        int ba = 0;
        int bb = 0;

        for (AnnotationElementValue[] annotationElementValues = this.elementValues;
             bb < annotationElementValues.length;
             annotationElementValues = this.elementValues
        ) {
            this.elementValues[ba].collectReferencedMembers(set2, set3, set4, set1);
            bb = ++ba;
        }
    }

    @Override
    public String getValueTypeDescriptor() {
        String string = null;
        int ba = 0;
        int bb = 0;

        for (AnnotationElementValue[] annotationElementValues = this.elementValues;
             bb < annotationElementValues.length;
             annotationElementValues = this.elementValues
        ) {
            if (ba == 0) {
                string = this.elementValues[ba].getValueTypeDescriptor();
                if (string == null) {
                    return null;
                }
            } else {
                String string1 = this.elementValues[ba].getValueTypeDescriptor();
                if (string1 == null || !string.equals(string1)) {
                    return null;
                }
            }

            bb = ++ba;
        }

        return string != null ? "[" + string : null;
    }

    public AnnotationElementValue[] getElementValues() {
        return this.elementValues;
    }

    @Override
    public void collectReferencedClasses(Object object) {
        Set set1 = (Set) object;
        if (this.isValid()) {
            int ba = 0;
            int bb = ba;

            for (AnnotationElementValue[] annotationElementValues = this.elementValues;
                 bb < annotationElementValues.length;
                 annotationElementValues = this.elementValues
            ) {
                this.elementValues[ba].collectReferencedClasses(set1);
                bb = ++ba;
            }
        }
    }

    @Override
    public void resolveReferences(Object object, Object object1, Object object2) throws ZkmException, IOException {
        ClassResolver classResolver1 = (ClassResolver) object;
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object2;
        IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1 = (IgnoreMissingReferencesSpec) object1;
        int ba = 0;
        int bb = 0;

        for (AnnotationElementValue[] annotationElementValues = this.elementValues;
             bb < annotationElementValues.length;
             annotationElementValues = this.elementValues
        ) {
            this.elementValues[ba].resolveReferences(classResolver1, ignoreMissingReferencesSpec1, scriptEnvironment1);
            bb = ++ba;
        }
    }

    @Override
    public boolean isArrayOrClassValue() {
        return true;
    }

    @Override
    public void write(int ba, DataOutputStream dataOutputStream, int bb, int bc) throws IOException {
        long bi = ((long) ba << 32 | (long) bb << 48 >>> 32 | (long) bc << 48 >>> 48) ^ 0L;
        int bd = (int) ((((long) ba << 32 | (long) bb << 48 >>> 32 | (long) bc << 48 >>> 48) ^ 0L) >>> 32);
        int be = (int) ((((long) ba << 32 | (long) bb << 48 >>> 32 | (long) bc << 48 >>> 48) ^ 0L) << 32 >>> 48);
        int bf = (int) (bi << 48 >>> 48);
        dataOutputStream.writeByte(this.getTag());
        dataOutputStream.writeShort(this.elementValues.length);
        int bg = 0;
        int bh = 0;
        AnnotationElementValue[] annotationElementValues = this.elementValues;

        while (true) {
            if (bh < annotationElementValues.length) {
                this.elementValues[bg].write(bd, dataOutputStream, (char) be, (char) bf);
            } else if (bc > 0) {
                return;
            }

            bh = ++bg;
            annotationElementValues = this.elementValues;
        }
    }

    @Override
    public String getClassValueName() {
        return null;
    }

    @Override
    public void collectUsedConstants(char bd, int ba, UsedConstantsCollector usedConstantsCollector, char be) {
        int bb = 0;
        int bc = 0;
        AnnotationElementValue[] annotationElementValues = this.elementValues;

        while (true) {
            if (bc < annotationElementValues.length) {
                this.elementValues[bb].collectUsedConstants('\u0000', 1103830477, usedConstantsCollector, '鞍');
            } else if (ba > 0) {
                return;
            }

            bc = ++bb;
            annotationElementValues = this.elementValues;
        }
    }

    @Override
    public boolean hasClassValue() {
        return false;
    }

    @Override
    public void writeRemapped(DataOutputStream dataOutputStream, Object object, Object object1) throws IOException {
        Map map1 = (Map) object;
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object1;
        dataOutputStream.writeByte(this.getTag());
        dataOutputStream.writeShort(this.elementValues.length);
        int ba = 0;
        int bb = 0;

        for (AnnotationElementValue[] annotationElementValues = this.elementValues;
             bb < annotationElementValues.length;
             annotationElementValues = this.elementValues
        ) {
            this.elementValues[ba].writeRemapped(dataOutputStream, map1, scriptEnvironment1);
            bb = ++ba;
        }
    }

    @Override
    public void updateAfterMethodRename() {
        int ba = 0;
        int bb = 0;

        for (AnnotationElementValue[] annotationElementValues = this.elementValues;
             bb < annotationElementValues.length;
             annotationElementValues = this.elementValues
        ) {
            this.elementValues[ba].updateAfterMethodRename();
            bb = ++ba;
        }
    }

    @Override
    public int getByteLength() {
        int ba = 1;
        ba += 2;
        int bb = 0;
        int bc = 0;

        for (AnnotationElementValue[] annotationElementValues = this.elementValues;
             bc < annotationElementValues.length;
             annotationElementValues = this.elementValues
        ) {
            ba += this.elementValues[bb].getByteLength();
            bc = ++bb;
        }

        return ba;
    }

    @Override
    public void replaceUtf8Constant(ConstantUtf8 constantUtf8, ConstantUtf8 constantUtf81) {
    }

    public NestedAnnotationElementValue(ClassFileComponent classFileComponent, int ba, ClassFileInputStream classFileInputStream, ListMultimap listMultimap) throws IOException {
        super(classFileComponent, ba);
        int bb = classFileInputStream.readUnsignedShort();
        this.elementValues = new AnnotationElementValue[bb];

        for (int i = 0; i < bb; i++) {
            AnnotationElementValue annotationElementValue = AnnotationElementValue.readElementValue(this, classFileInputStream, listMultimap);
            if (!annotationElementValue.isValid()) {
                this.setValid();
                this.setErrorMessage(INVALID_NESTED_MESSAGE + annotationElementValue.getErrorMessage());
                return;
            }

            this.elementValues[i] = annotationElementValue;
        }
    }
}
