package com.zelix.klassmaster.classfile.constpool;

import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.exceptions.ClassFileFormatException;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.DataOutputStream;
import java.io.IOException;

public class ConstantNameAndType extends ConstantPoolEntry implements ResolvableConstant {
    public static final ConstantPoolTag TAG = ConstantPoolTag.NAME_AND_TYPE;
    public int nameIndex;
    public int descriptorIndex;

    @Override
    public boolean isUnresolved() {
        return true;
    }

    @Override
    public ConstantPoolEntry resolve(ListMultimap listMultimap, ListMultimap listMultimap1, ListMultimap listMultimap2, ListMultimap listMultimap3) throws ClassFileFormatException {
        try {
            ConstantPoolEntry constantPoolEntry = this.constantPool.getConstantPoolEntry(this.nameIndex);
            if (!(constantPoolEntry instanceof ConstantUtf8)) {
                String string2 = this.constantPool.getClassLocation()
                        + " : "
                        + "Invalid Constant Pool name index"
                        + " : "
                        + this.nameIndex
                        + " : "
                        + ConstantPoolEntry.getTagName(this.getTag())
                        + " entry";
                throw new ClassFileFormatException(string2);
            }

            ConstantPoolEntry constantPoolEntry1 = this.constantPool.getConstantPoolEntry(this.descriptorIndex);
            if (!(this.constantPool.getConstantPoolEntry(this.descriptorIndex) instanceof ConstantUtf8)) {
                String string3 = this.constantPool.getClassLocation()
                        + " : "
                        + "Invalid Constant Pool descriptor index"
                        + " : "
                        + this.descriptorIndex
                        + " : "
                        + ConstantPoolEntry.getTagName(this.getTag())
                        + " entry";
                throw new ClassFileFormatException(string3);
            }

            String string1 = ((ConstantUtf8) constantPoolEntry1).getValue();
            if (ConstantPoolEntry.descriptorToJavaType(string1, true) == null) {
                String string4 = this.constantPool.getClassLocation()
                        + " : "
                        + "Invalid type descriptor"
                        + " : "
                        + "'"
                        + string1
                        + "'"
                        + " : "
                        + ConstantPoolEntry.getTagName(this.getTag())
                        + " entry";
                throw new ClassFileFormatException(string4);
            }

            ResolvedNameAndType resolvedNameAndType = new ResolvedNameAndType(this, (ConstantUtf8) constantPoolEntry, (ConstantUtf8) constantPoolEntry1);
            if (listMultimap != null) {
                listMultimap.addValue((ConstantUtf8) constantPoolEntry, resolvedNameAndType);
                listMultimap.addValue((ConstantUtf8) constantPoolEntry1, resolvedNameAndType);
            }

            return resolvedNameAndType;
        } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
            String string = this.constantPool.getClassLocation()
                    + " : "
                    + "Constant Pool index out of range"
                    + " : "
                    + arrayIndexOutOfBoundsException.getMessage()
                    + " : "
                    + ConstantPoolEntry.getTagName(this.getTag())
                    + " entry";
            throw new ClassFileFormatException(string);
        }
    }

    @Override
    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(TAG.getTagValue());
        dataOutputStream.writeShort(this.nameIndex);
        dataOutputStream.writeShort(this.descriptorIndex);
    }

    public ConstantNameAndType(int ba, ClassFileInputStream classFileInputStream, AbstractConstantPool abstractConstantPool) throws IOException {
        super(ba, abstractConstantPool);
        this.nameIndex = classFileInputStream.readUnsignedShort();
        this.descriptorIndex = classFileInputStream.readUnsignedShort();
    }

    @Override
    public ConstantPoolTag getTag() {
        return TAG;
    }
}
