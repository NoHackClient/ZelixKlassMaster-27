package com.zelix.klassmaster.classfile.constpool;

import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.exceptions.ClassFileFormatException;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.DataOutputStream;
import java.io.IOException;

public class ConstantString extends ConstantPoolEntry implements ResolvableConstant {
    public static final ConstantPoolTag TAG = ConstantPoolTag.STRING;
    public int utf8Index;

    @Override
    public ConstantPoolTag getTag() {
        return TAG;
    }

    @Override
    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(TAG.getTagValue());
        dataOutputStream.writeShort(this.utf8Index);
    }

    @Override
    public ConstantPoolEntry resolve(ListMultimap listMultimap, ListMultimap listMultimap1, ListMultimap listMultimap2, ListMultimap listMultimap3) throws ClassFileFormatException {
        try {
            ConstantPoolEntry constantPoolEntry = this.constantPool.getConstantPoolEntry(this.utf8Index);
            if (constantPoolEntry instanceof ConstantUtf8) {
                return new ResolvedStringConstant(this, (ConstantUtf8) constantPoolEntry, listMultimap);
            }

            String string1 = this.constantPool.getClassLocation()
                    + " : "
                    + "Invalid Constant Pool string index"
                    + " : "
                    + this.utf8Index
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
    public boolean isUnresolved() {
        return true;
    }

    public ConstantString(int ba, ClassFileInputStream classFileInputStream, AbstractConstantPool abstractConstantPool) throws IOException {
        super(ba, abstractConstantPool);
        this.utf8Index = classFileInputStream.readUnsignedShort();
    }
}
