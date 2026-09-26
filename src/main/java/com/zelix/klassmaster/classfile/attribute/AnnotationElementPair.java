package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.AbstractMethodInfo;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ConstantUtf8;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.classfile.constpool.Utf8ConstantReplaceable;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.config.IgnoreMissingReferencesSpec;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AnnotationElementPair extends ClassFileComponent implements AnnotationElementValueType, Utf8ConstantReplaceable {
    public AbstractMethodInfo elementMethod;
    public boolean valid = true;
    public ConstantUtf8 nameConstant;
    public String errorMessage;
    public AnnotationElementValue value;

    @Override
    public void replaceUtf8Constant(ConstantUtf8 constantUtf8, ConstantUtf8 constantUtf81) {
        if (this.nameConstant == constantUtf8) {
            this.nameConstant = constantUtf81;
        }
    }

    @Override
    public boolean isValid() {
        return this.valid;
    }

    public AnnotationElementValue getValue() {
        return this.value;
    }

    @Override
    public void collectReferencedClasses(Object object) {
        Set set1 = (Set) object;
        if (this.valid) {
            this.value.collectReferencedClasses(set1);
        }
    }

    public void collectReferencedMembers(ClassFileBase classFileBase, Set set1, Set set2, Set set3, Set set4) {
        if (classFileBase != null && this.elementMethod != null && this.elementMethod.isProgramMember()) {
            set4.add((MethodInfo) this.elementMethod);
        }

        this.value.collectReferencedMembers(set1, set2, set3, set4);
    }

    @Override
    public int getByteLength() {
        return 2 + this.value.getByteLength();
    }

    public void resolveReferences(
            ClassFileBase classFileBase, ClassResolver classResolver1, IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1, ScriptEnvironment scriptEnvironment1
    ) throws ZkmException, IOException {
        if (classFileBase != null) {
            String string = this.nameConstant.getValue();
            String string1 = this.value.getValueTypeDescriptor();
            if (string1 != null) {
                this.elementMethod = classFileBase.findMethod(new MethodSignature(string, "()", string1));
            }

            if (this.elementMethod == null) {
                List list1 = classFileBase.findMethodsByName(string);
                if (list1.size() == 1) {
                    this.elementMethod = (AbstractMethodInfo) list1.get(0);
                }
            }
        }

        this.value.resolveReferences(classResolver1, ignoreMissingReferencesSpec1, scriptEnvironment1);
    }

    public String getClassValueName() {
        return this.value.isValid() && this.value.hasClassValue() ? this.value.getClassValueName() : null;
    }

    @Override
    public void write(int ba, DataOutputStream dataOutputStream, int bb, int bc) throws IOException {
        long bg = ((long) ba << 32 | (long) bb << 48 >>> 32 | (long) bc << 48 >>> 48) ^ 0L;
        int bd = (int) ((((long) ba << 32 | (long) bb << 48 >>> 32 | (long) bc << 48 >>> 48) ^ 0L) >>> 32);
        int be = (int) ((((long) ba << 32 | (long) bb << 48 >>> 32 | (long) bc << 48 >>> 48) ^ 0L) << 32 >>> 48);
        int bf = (int) (bg << 48 >>> 48);
        dataOutputStream.writeShort(this.nameConstant.getIndex());
        this.value.write(bd, dataOutputStream, (char) be, (char) bf);
    }

    public AnnotationElementPair(ClassFileComponent classFileComponent, ClassFileInputStream classFileInputStream, ListMultimap listMultimap) throws IOException {
        super(classFileComponent);
        int ba = classFileInputStream.readUnsignedShort();
        ConstantPoolEntry constantPoolEntry = this.getConstantPoolEntry(ba);
        if (constantPoolEntry != null) {
            if (constantPoolEntry instanceof ConstantUtf8) {
                this.nameConstant = (ConstantUtf8) constantPoolEntry;
                listMultimap.addValue(this.nameConstant, this);
                this.value = AnnotationElementValue.readElementValue(this, classFileInputStream, listMultimap);
                if (!this.value.isValid()) {
                    this.valid = false;
                    this.errorMessage = "Invalid component value : " + this.value.getErrorMessage();
                    return;
                }

                return;
            }

            this.valid = false;
        } else {
            this.valid = false;
        }

        this.errorMessage = "Invalid component name index : "
                + ba
                + (constantPoolEntry != null ? " : tag=" + constantPoolEntry.getTag() + " : \"" + constantPoolEntry.getDisplayString() + "\"" : "");
    }

    @Override
    public void updateAfterFieldRename() {
        this.value.updateAfterFieldRename();
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    @Override
    public void updateAfterMethodRename() {
        if (this.elementMethod != null && !this.elementMethod.getJvmName().equals(this.nameConstant.getValue())) {
            this.nameConstant.setValue(this.elementMethod.getJvmName());
        }

        this.value.updateAfterMethodRename();
    }

    @Override
    public void writeRemapped(DataOutputStream dataOutputStream, Object object, Object object1) throws IOException {
        Map map1 = (Map) object;
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object1;
        ConstantUtf8 constantUtf8 = (ConstantUtf8) map1.get(this.nameConstant);
        AnnotationElementValue annotationElementValue;
        if (constantUtf8 != null) {
            dataOutputStream.writeShort(constantUtf8.getIndex());
            annotationElementValue = this.value;
        } else {
            dataOutputStream.writeShort(this.nameConstant.getIndex());
            annotationElementValue = this.value;
        }

        annotationElementValue.writeRemapped(dataOutputStream, map1, scriptEnvironment1);
    }

    @Override
    public void updateAfterClassRename(Object object, Object object1) {
        HashMap hashMap1 = (HashMap) object1;
        HashMap hashMap = (HashMap) object;
        this.value.updateAfterClassRename(hashMap, hashMap1);
    }

    @Override
    public void collectUsedConstants(char ba, int bb, UsedConstantsCollector usedConstantsCollector, char bc) {
        if (this.valid) {
            this.nameConstant.registerUsage(usedConstantsCollector, this, this.getParent());
            this.value.collectUsedConstants('\u0000', 1103830477, usedConstantsCollector, '鞍');
        }
    }

    public String getElementName() {
        return this.valid ? this.nameConstant.getValue() : null;
    }

    public String getResolvedElementName() {
        if (this.valid) {
            return this.elementMethod != null ? this.elementMethod.getOriginalMemberName() : this.getElementName();
        } else {
            return null;
        }
    }
}
