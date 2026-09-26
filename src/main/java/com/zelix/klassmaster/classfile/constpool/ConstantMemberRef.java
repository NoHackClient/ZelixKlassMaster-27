package com.zelix.klassmaster.classfile.constpool;

import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.exceptions.ClassFileFormatException;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.DataOutputStream;
import java.io.IOException;

public abstract class ConstantMemberRef extends ConstantPoolEntry implements ResolvableConstant {
    public int classIndex;
    public int nameAndTypeIndex;

    @Override
    public boolean isUnresolved() {
        return true;
    }

    @Override
    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(this.getTag().getTagValue());
        dataOutputStream.writeShort(this.classIndex);
        dataOutputStream.writeShort(this.nameAndTypeIndex);
    }

    @Override
    public final ConstantPoolEntry resolve(ListMultimap listMultimap2, ListMultimap listMultimap, ListMultimap listMultimap1, ListMultimap listMultimap3) throws ClassFileFormatException {
        try {
            ConstantPoolEntry constantPoolEntry = this.constantPool.getConstantPoolEntry(this.classIndex);
            ConstantPoolEntry constantPoolEntry1 = this.constantPool.getConstantPoolEntry(this.nameAndTypeIndex);
            if (constantPoolEntry.isUnresolved() || constantPoolEntry1.isUnresolved()) {
                return null;
            }

            if (!(constantPoolEntry instanceof ResolvedClassConstant)) {
                String string2 = this.constantPool.getClassLocation()
                        + " : "
                        + "Invalid Constant Pool class index"
                        + " : "
                        + this.classIndex
                        + " : "
                        + ConstantPoolEntry.getTagName(this.getTag())
                        + " entry";
                throw new ClassFileFormatException(string2);
            }

            if (!(constantPoolEntry1 instanceof ResolvedNameAndType)) {
                String string1 = this.constantPool.getClassLocation()
                        + " : "
                        + "Invalid Constant Pool name and type index"
                        + " : "
                        + this.nameAndTypeIndex
                        + " : "
                        + ConstantPoolEntry.getTagName(this.getTag())
                        + " entry";
                throw new ClassFileFormatException(string1);
            }

            ResolvedMemberRef resolvedMemberRef = this.createResolvedRef(
                    this, (ResolvedClassConstant) constantPoolEntry, (ResolvedNameAndType) constantPoolEntry1, listMultimap1
            );
            if (listMultimap != null) {
                listMultimap.addValue((ResolvedNameAndType) constantPoolEntry1, resolvedMemberRef);
            }

            return resolvedMemberRef;
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

    public ConstantMemberRef(int ba, ClassFileInputStream classFileInputStream, AbstractConstantPool abstractConstantPool) throws IOException {
        super(ba, abstractConstantPool);
        this.classIndex = classFileInputStream.readUnsignedShort();
        this.nameAndTypeIndex = classFileInputStream.readUnsignedShort();
    }

    public abstract ResolvedMemberRef createResolvedRef(
            ConstantMemberRef constantMemberRef, ResolvedClassConstant resolvedClassConstant, ResolvedNameAndType resolvedNameAndType, ListMultimap listMultimap
    );
}
