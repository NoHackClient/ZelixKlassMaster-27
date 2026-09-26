package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.constpool.AbstractConstantPool;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ConstantUtf8;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.classfile.constpool.Utf8ConstantReplaceable;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MethodParameterEntry extends ClassFileComponent implements Utf8ConstantReplaceable {
    private static String invalidNamePrefix;
    private static long nameSeparator;
    public String errorMessage;
    public int accessFlags;
    public boolean valid = true;
    private ConstantUtf8 nameConstant;

    @Override
    public void replaceUtf8Constant(ConstantUtf8 constantUtf8, ConstantUtf8 constantUtf81) {
        if (this.nameConstant != null && this.nameConstant == constantUtf8) {
            this.nameConstant = constantUtf81;
        }
    }

    public void obfuscateName() {
        if (this.nameConstant != null) {
            if (HiddenOptionFlags.MARK_RENAMED_LOCALS) {
                String string = this.nameConstant.getValue() + (int) nameSeparator + "a";
                this.nameConstant.setValue(string);
            } else {
                this.nameConstant.setValue("a");
            }
        }
    }

    @Override
    public void collectUsedConstants(char ba, int bb, UsedConstantsCollector usedConstantsCollector, char bc) {
        if (this.nameConstant != null) {
            usedConstantsCollector.markUsed(this.nameConstant, this, this.getParent());
        }
    }

    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
        if (this.nameConstant != null) {
            dataOutputStream.writeShort(this.nameConstant.getIndex());
        } else {
            dataOutputStream.writeShort(0);
        }

        dataOutputStream.writeShort(this.accessFlags);
    }

    public MethodParameterEntry(MethodParametersAttribute methodParametersAttribute, AbstractConstantPool abstractConstantPool, List list1) {
        super(methodParametersAttribute);
        ConstantUtf8 constantUtf8 = abstractConstantPool.createUtf8Constant("a", list1);
        this.nameConstant = constantUtf8;
    }

    public MethodParameterEntry(ClassFileComponent classFileComponent, ClassFileInputStream classFileInputStream, ListMultimap listMultimap) throws IOException {
        super(classFileComponent);
        int ba = classFileInputStream.readUnsignedShort();
        this.accessFlags = classFileInputStream.readUnsignedShort();
        if (ba != 0) {
            ConstantPoolEntry constantPoolEntry = this.getConstantPoolEntry(ba);
            if (constantPoolEntry instanceof ConstantUtf8) {
                this.nameConstant = (ConstantUtf8) constantPoolEntry;
                listMultimap.addValue(this.nameConstant, this);
            } else {
                this.valid = false;
                this.errorMessage = invalidNamePrefix + ConstantPoolEntry.getTagName(constantPoolEntry.getTag()) + "'";
            }
        }
    }

    public boolean isValid() {
        return this.valid;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void writeRemapped(DataOutputStream dataOutputStream, Map map1) throws IOException {
        DataOutputStream dataOutputStream1;
        int ba;
        if (this.nameConstant != null) {
            ConstantPoolEntry constantPoolEntry = (ConstantPoolEntry) map1.get(this.nameConstant);
            if (constantPoolEntry != null) {
                dataOutputStream.writeShort(constantPoolEntry.getIndex());
            } else {
                dataOutputStream.writeShort(this.nameConstant.getIndex());
            }

            dataOutputStream1 = dataOutputStream;
            ba = this.accessFlags;
        } else {
            dataOutputStream.writeShort(0);
            dataOutputStream1 = dataOutputStream;
            ba = this.accessFlags;
        }

        dataOutputStream1.writeShort(ba);
    }

    public int getEntrySize() {
        return 4;
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
        invalidNamePrefix = "Invalid MethodParameters nameEntry '";
        nameSeparator = -632555645119233953L;
    }
}
