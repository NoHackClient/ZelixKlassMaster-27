package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.constpool.ClassConstantReplaceable;
import com.zelix.klassmaster.classfile.constpool.ConstantPool;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ResolvedClassConstant;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.ZkmAssert;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ObjectVerificationType extends VerificationTypeInfo implements ClassConstantReplaceable {
    private ResolvedClassConstant classConstant;

    @Override
    public void collectUsedConstants(char ba, int bb, UsedConstantsCollector usedConstantsCollector, char bc) {
        this.classConstant.registerUsage(usedConstantsCollector, this, this.getParent());
    }

    @Override
    public boolean isSameType(VerificationTypeInfo verificationTypeInfo) {
        if (this.tag == verificationTypeInfo.tag) {
            ObjectVerificationType objectVerificationType1 = (ObjectVerificationType) verificationTypeInfo;
            return this.classConstant.getClassName().equals(objectVerificationType1.classConstant.getClassName());
        } else {
            return false;
        }
    }

    @Override
    public void replaceClassConstant(ResolvedClassConstant resolvedClassConstant, ResolvedClassConstant resolvedClassConstant1) {
        if (this.classConstant == resolvedClassConstant) {
            this.classConstant = resolvedClassConstant1;
        }
    }

    public static VerificationTypeInfo getOrCreateCached(
            AbstractStackMapAttribute abstractStackMapAttribute, String string, ConstantPool constantPool1, Set set1, List list1, Map map1
    ) {
        ObjectVerificationType objectVerificationType;
        if (map1.containsKey(string)) {
            objectVerificationType = (ObjectVerificationType) map1.get(string);
        } else {
            objectVerificationType = new ObjectVerificationType(abstractStackMapAttribute, string, constantPool1, set1, list1);
            map1.put(string, objectVerificationType);
        }

        return objectVerificationType;
    }

    public ObjectVerificationType(ClassFileComponent classFileComponent, String string, ConstantPool constantPool1, Set set1, List list1) {
        super(classFileComponent, 7);
        String string1;
        if (string.startsWith("L") && string.endsWith(";")) {
            string1 = string.substring(1, string.length() - 1);
        } else {
            string1 = string;
        }

        ResolvedClassConstant resolvedClassConstant = ResolvedClassConstant.findByName(string1, set1);
        if (resolvedClassConstant == null) {
            this.classConstant = constantPool1.getOrCreateClassConstant(string1, list1);
        } else {
            this.classConstant = resolvedClassConstant;
            set1.remove(resolvedClassConstant);
        }
    }

    @Override
    public void writeRemapped(DataOutputStream dataOutputStream, Map map1) throws IOException {
        if (!this.valid) {
            ZkmAssert.assertTrue(false, new String[]{"Invalid StackMap Entry in " + this.getDottedClassName()});
        }

        dataOutputStream.writeByte(this.tag);
        if (map1 != null) {
            ConstantPoolEntry constantPoolEntry = (ConstantPoolEntry) map1.get(this.classConstant);
            if (constantPoolEntry != null) {
                dataOutputStream.writeShort(constantPoolEntry.getIndex());
            } else {
                dataOutputStream.writeShort(this.classConstant.getIndex());
            }
        } else {
            dataOutputStream.writeShort(this.classConstant.getIndex());
        }
    }

    public ObjectVerificationType(ClassFileComponent classFileComponent, int ba, int bb, ListMultimap listMultimap, PrintWriter printWriter) {
        super(classFileComponent, ba);
        ConstantPoolEntry constantPoolEntry = this.getConstantPoolEntry(bb);
        if (constantPoolEntry instanceof ResolvedClassConstant) {
            this.classConstant = (ResolvedClassConstant) constantPoolEntry;
            listMultimap.addValue(this.classConstant, this);
        } else {
            this.valid = false;
            printWriter.println("ERROR: " + this.getLocationName() + " : " + "Invalid StackMap attribute" + " (C)");
        }
    }
}
