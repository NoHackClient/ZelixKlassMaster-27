package com.zelix.klassmaster.classfile.constpool;

import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.IOException;

public class ConstantMethodref extends ConstantMemberRef implements ResolvableConstant {
    public static final ConstantPoolTag TAG = ConstantPoolTag.METHODREF;

    @Override
    public ResolvedMemberRef createResolvedRef(
            ConstantMemberRef constantMemberRef, ResolvedClassConstant resolvedClassConstant, ResolvedNameAndType resolvedNameAndType, ListMultimap listMultimap
    ) {
        return new ResolvedMethodRefConstant(constantMemberRef, resolvedClassConstant, resolvedNameAndType, listMultimap);
    }

    public ConstantMethodref(int ba, ClassFileInputStream classFileInputStream, AbstractConstantPool abstractConstantPool) throws IOException {
        super(ba, classFileInputStream, abstractConstantPool);
    }

    @Override
    public ConstantPoolTag getTag() {
        return TAG;
    }
}
