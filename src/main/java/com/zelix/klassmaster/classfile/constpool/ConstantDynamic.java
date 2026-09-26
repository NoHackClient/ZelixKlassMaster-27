package com.zelix.klassmaster.classfile.constpool;

import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.exceptions.ClassFileFormatException;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.IOException;

public class ConstantDynamic extends DynamicConstantBase {
    public static final ConstantPoolTag TAG = ConstantPoolTag.CONSTANT_DYNAMIC;

    public ResolvedConstantDynamic resolveConstantDynamic(ListMultimap listMultimap) throws ClassFileFormatException {
        try {
            ConstantPoolEntry constantPoolEntry = this.constantPool.getConstantPoolEntry(super.nameAndTypeIndex);
            if (constantPoolEntry.isUnresolved()) {
                return null;
            }

            if (!(constantPoolEntry instanceof ResolvedNameAndType)) {
                String string1 = this.constantPool.getClassLocationDescription()
                        + " : "
                        + "Invalid Constant Pool name and type index"
                        + " : "
                        + super.nameAndTypeIndex
                        + " : "
                        + ConstantPoolEntry.getTagName(constantPoolEntry.getTag())
                        + " : "
                        + ConstantPoolEntry.getTagName(this.getTag())
                        + " entry at "
                        + this.getIndex();
                throw new ClassFileFormatException(string1);
            }

            ResolvedConstantDynamic resolvedConstantDynamic = new ResolvedConstantDynamic(
                    this.getIndex(), this.constantPool, super.bootstrapMethodIndex, (ResolvedNameAndType) constantPoolEntry
            );
            if (listMultimap != null) {
                listMultimap.addValue((ResolvedNameAndType) constantPoolEntry, resolvedConstantDynamic);
            }

            return resolvedConstantDynamic;
        } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
            String string = this.constantPool.getClassLocationDescription()
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
    public ConstantPoolTag getTag() {
        return TAG;
    }

    @Override
    public ConstantPoolEntry resolve(ListMultimap listMultimap1, ListMultimap listMultimap, ListMultimap listMultimap2, ListMultimap listMultimap3) throws ClassFileFormatException {
        return this.resolveConstantDynamic(listMultimap);
    }

    public ConstantDynamic(int ba, ClassFileInputStream classFileInputStream, AbstractConstantPool abstractConstantPool) throws IOException {
        super(ba, classFileInputStream, abstractConstantPool);
    }
}
