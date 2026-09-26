package com.zelix.klassmaster.classfile.constpool;

import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.exceptions.ClassFileFormatException;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.DataOutputStream;
import java.io.IOException;

public class ConstantClass extends ClassConstantBase implements ResolvableConstant {
    public int nameIndex;

    @Override
    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(ClassConstantBase.CLASS_TAG.getTagValue());
        dataOutputStream.writeShort(this.nameIndex);
    }

    public ConstantClass(int ba, ClassFileInputStream classFileInputStream, AbstractConstantPool abstractConstantPool) throws IOException {
        super(ba, abstractConstantPool);
        this.nameIndex = classFileInputStream.readUnsignedShort();
    }

    @Override
    public void setClassName(String string) {
        ((ConstantUtf8) this.constantPool.getConstantPoolEntry(this.nameIndex)).setValue(string);
    }

    @Override
    public boolean isUnresolved() {
        return true;
    }

    @Override
    public ConstantPoolEntry resolve(ListMultimap listMultimap, ListMultimap listMultimap1, ListMultimap listMultimap2, ListMultimap listMultimap3) throws ClassFileFormatException {
        try {
            ConstantPoolEntry constantPoolEntry = this.constantPool.getConstantPoolEntry(this.nameIndex);
            if (constantPoolEntry instanceof ConstantUtf8) {
                return new ResolvedClassConstant(this, (ConstantUtf8) constantPoolEntry, listMultimap);
            }

            String string1 = this.constantPool.getClassLocation()
                    + " : "
                    + "Invalid Constant Pool name index"
                    + " : "
                    + this.nameIndex
                    + " : "
                    + ConstantPoolEntry.getTagName(this.getTag())
                    + " entry";
            throw new ClassFileFormatException(string1);
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
    public String getDottedClassName() {
        return ((ConstantUtf8) this.constantPool.getConstantPoolEntry(this.nameIndex)).getValue().replace('/', '.');
    }

    @Override
    public String getClassName() {
        return ((ConstantUtf8) this.constantPool.getConstantPoolEntry(this.nameIndex)).getValue();
    }
}
