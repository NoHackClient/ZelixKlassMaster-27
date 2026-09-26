package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ConstantUtf8;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.classfile.constpool.Utf8ConstantReplaceable;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.config.IgnoreMissingReferencesSpec;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.MutableInt;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AnnotationEntry extends ClassFileComponent implements AnnotationElementValueType, Utf8ConstantReplaceable {
    public int arrayDimensions;
    public ClassFileBase annotationClass;
    public boolean valid = true;
    public ConstantUtf8 typeConstant;
    public String errorMessage;
    public int elementCount;
    public AnnotationElementPair[] elements;

    public void resolveReferences(ClassResolver classResolver1, IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1, ScriptEnvironment scriptEnvironment1) throws ZkmException, IOException {
        MutableInt mutableInt = new MutableInt(0);
        String string = ClassFileBase.extractClassName(this.typeConstant.getValue(), mutableInt);
        this.arrayDimensions = mutableInt.getValue();
        if (string != null) {
            this.annotationClass = classResolver1.getClassFile(
                    string, "analyzing annotations in class '" + this.getDisplayLocationName() + "' (C)", ignoreMissingReferencesSpec1
            );
        }

        int ba = 0;
        int bb = 0;

        for (int i = this.elementCount; bb < i; i = this.elementCount) {
            this.elements[ba].resolveReferences(this.annotationClass, classResolver1, ignoreMissingReferencesSpec1, scriptEnvironment1);
            bb = ++ba;
        }
    }

    public String getResolvedTypeName() {
        if (this.valid) {
            return this.annotationClass != null ? this.annotationClass.getOriginalClassName() : this.getTypeName();
        } else {
            return null;
        }
    }

    @Override
    public void updateAfterMethodRename() {
        int ba = 0;
        int bb = 0;

        for (int i = this.elementCount; bb < i; i = this.elementCount) {
            this.elements[ba].updateAfterMethodRename();
            bb = ++ba;
        }
    }

    public String getElementClassValue(String string, boolean bl) {
        int ba = 0;
        int bb = 0;

        for (int i = this.elementCount; bb < i; i = this.elementCount) {
            String string1;
            if (bl) {
                string1 = this.elements[ba].getResolvedElementName();
            } else {
                string1 = this.elements[ba].getElementName();
            }

            if (string.equals(string1)) {
                return this.elements[ba].getClassValueName();
            }

            bb = ++ba;
        }

        return null;
    }

    @Override
    public void updateAfterFieldRename() {
        int ba = 0;
        int bb = 0;

        for (int i = this.elementCount; bb < i; i = this.elementCount) {
            this.elements[ba].updateAfterFieldRename();
            bb = ++ba;
        }
    }

    public void collectReferencedMembers(Set set1, Set set2, Set set3, Set set4) {
        if (this.annotationClass != null && this.annotationClass.isProgramClass()) {
            set1.add((ProgramClass) this.annotationClass);
        }

        int ba = 0;
        int bb = ba;

        for (int i = this.elementCount; bb < i; i = this.elementCount) {
            this.elements[ba].collectReferencedMembers(this.annotationClass, set1, set2, set3, set4);
            bb = ++ba;
        }
    }

    @Override
    public void collectUsedConstants(char bd, int be, UsedConstantsCollector usedConstantsCollector, char bf) {
        if (this.valid) {
            this.typeConstant.registerUsage(usedConstantsCollector, this, this.getParent());
            int ba = 0;
            int bb = 0;

            for (int i = this.elementCount; bb < i; i = this.elementCount) {
                this.elements[ba].collectUsedConstants('\u0000', 1103830477, usedConstantsCollector, '鞍');
                bb = ++ba;
            }
        }
    }

    @Override
    public void updateAfterClassRename(Object object, Object object1) {
        HashMap hashMap1 = (HashMap) object1;
        HashMap hashMap = (HashMap) object;
        if (this.annotationClass != null) {
            String string = ClassFileBase.extractClassName(this.typeConstant.getValue());
            if (!this.annotationClass.getClassName().equals(string)) {
                String string1 = ClassFileBase.toTypeDescriptor(this.annotationClass.getClassName(), this.arrayDimensions);
                this.typeConstant.setValue(string1);
            }

            int ba = 0;
            int bb = ba;

            for (int i = this.elementCount; bb < i; i = this.elementCount) {
                this.elements[ba].updateAfterClassRename(hashMap, hashMap1);
                bb = ++ba;
            }
        }
    }

    @Override
    public int getByteLength() {
        int ba = 4;
        int bb = 0;
        int bc = 0;

        for (int i = this.elementCount; bc < i; i = this.elementCount) {
            ba += this.elements[bb].getByteLength();
            bc = ++bb;
        }

        return ba;
    }

    public AnnotationEntry(
            ClassFileComponent classFileComponent, int ba, ConstantPoolEntry constantPoolEntry, ClassFileInputStream classFileInputStream, ListMultimap listMultimap
    ) throws IOException {
        super(classFileComponent);
        if (constantPoolEntry != null && constantPoolEntry instanceof ConstantUtf8) {
            this.typeConstant = (ConstantUtf8) constantPoolEntry;
            String string = this.typeConstant.getValue();
            if (string.startsWith("L")) {
                if (string.endsWith(";")) {
                    listMultimap.addValue(this.typeConstant, this);
                    this.elementCount = classFileInputStream.readUnsignedShort();
                    this.elements = new AnnotationElementPair[this.elementCount];
                    int bb = 0;
                    int bc = 0;

                    for (int i = this.elementCount; bc < i; i = this.elementCount) {
                        this.elements[bb] = new AnnotationElementPair(this, classFileInputStream, listMultimap);
                        if (!this.elements[bb].isValid()) {
                            this.valid = false;
                            this.errorMessage = this.elements[bb].getErrorMessage();
                            return;
                        }

                        bc = ++bb;
                    }

                    return;
                }

                this.valid = false;
            } else {
                this.valid = false;
            }

            this.errorMessage = "Invalid annotation type '" + string + "'";
        } else {
            this.valid = false;
            this.errorMessage = "Invalid type index : "
                    + ba
                    + (constantPoolEntry != null ? " : tag=" + constantPoolEntry.getTag() + " : \"" + constantPoolEntry.getDisplayString() + "\"" : "");
        }
    }

    public ClassFileBase getAnnotationClass() {
        return this.annotationClass;
    }

    public String getTypeName() {
        if (this.valid) {
            String string = this.typeConstant.getValue();
            return string.substring(1, string.length() - 1);
        } else {
            return null;
        }
    }

    public String getErrorMessage(Object object, Object object1, Object object2) {
        return this.errorMessage;
    }

    @Override
    public void replaceUtf8Constant(ConstantUtf8 constantUtf8, ConstantUtf8 constantUtf81) {
        if (this.typeConstant == constantUtf8) {
            this.typeConstant = constantUtf81;
        }
    }

    @Override
    public void collectReferencedClasses(Object object) {
        Set set1 = (Set) object;
        if (this.valid) {
            if (this.annotationClass != null) {
                set1.add(this.annotationClass);
            }

            int ba = 0;
            int bb = 0;

            for (int i = this.elementCount; bb < i; i = this.elementCount) {
                this.elements[ba].collectReferencedClasses(set1);
                bb = ++ba;
            }
        }
    }

    @Override
    public void writeRemapped(DataOutputStream dataOutputStream, Object object, Object object1) throws IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object1;
        Map map1 = (Map) object;
        ConstantUtf8 constantUtf8 = (ConstantUtf8) map1.get(this.typeConstant);
        DataOutputStream dataOutputStream1;
        int bd;
        if (constantUtf8 != null) {
            dataOutputStream.writeShort(constantUtf8.getIndex());
            dataOutputStream1 = dataOutputStream;
            bd = this.elementCount;
        } else {
            dataOutputStream.writeShort(this.typeConstant.getIndex());
            dataOutputStream1 = dataOutputStream;
            bd = this.elementCount;
        }

        dataOutputStream1.writeShort(bd);
        int ba = 0;
        int bb = 0;

        for (int i = this.elementCount; bb < i; i = this.elementCount) {
            this.elements[ba].writeRemapped(dataOutputStream, map1, scriptEnvironment1);
            bb = ++ba;
        }
    }

    public String getTypeDescriptor() {
        return this.typeConstant.getValue();
    }

    @Override
    public boolean isValid() {
        return this.valid;
    }

    public static AnnotationEntry read(ClassFileComponent classFileComponent, ClassFileInputStream classFileInputStream, ListMultimap listMultimap) throws IOException {
        int ba = classFileInputStream.readUnsignedShort();
        ConstantPoolEntry constantPoolEntry = classFileComponent.getConstantPoolEntry(ba);
        return constantPoolEntry == null
                || !(constantPoolEntry instanceof ConstantUtf8)
                || !((ConstantUtf8) constantPoolEntry).getValue().equals("Lkotlin/Metadata;")
                || !HiddenOptionFlags.TRANSLATE_KOTLIN && !HiddenOptionFlags.TRANSLATE_KOTLIN_METADATA
                ? new AnnotationEntry(classFileComponent, ba, constantPoolEntry, classFileInputStream, listMultimap)
                : new KotlinMetadataAnnotation(classFileComponent, ba, constantPoolEntry, classFileInputStream, listMultimap);
    }

    @Override
    public void write(int ba, DataOutputStream dataOutputStream, int bb, int bc) throws IOException {
        long bj = ((long) ba << 32 | (long) bb << 48 >>> 32 | (long) bc << 48 >>> 48) ^ 0L;
        int bd = (int) ((((long) ba << 32 | (long) bb << 48 >>> 32 | (long) bc << 48 >>> 48) ^ 0L) >>> 32);
        int be = (int) ((((long) ba << 32 | (long) bb << 48 >>> 32 | (long) bc << 48 >>> 48) ^ 0L) << 32 >>> 48);
        int bf = (int) (bj << 48 >>> 48);
        dataOutputStream.writeShort(this.typeConstant.getIndex());
        dataOutputStream.writeShort(this.elementCount);
        int bg = 0;
        int bi = 0;
        int elementCount = this.elementCount;

        while (true) {
            if (bi < elementCount) {
                this.elements[bg].write(bd, dataOutputStream, (char) be, (char) bf);
            } else if (ba > 0) {
                return;
            }

            bi = ++bg;
            elementCount = this.elementCount;
        }
    }
}
