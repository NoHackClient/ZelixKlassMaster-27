package com.zelix.klassmaster.classfile.constpool;

import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.IOException;

public class ConstantInterfaceMethodref extends ConstantMemberRef implements ResolvableConstant {
    public static final ConstantPoolTag TAG = ConstantPoolTag.INTERFACE_METHODREF;

    public ConstantInterfaceMethodref(int ba, ClassFileInputStream classFileInputStream, AbstractConstantPool abstractConstantPool) throws IOException {
        super(ba, classFileInputStream, abstractConstantPool);
    }

    @Override
    public ResolvedMemberRef createResolvedRef(
            ConstantMemberRef constantMemberRef, ResolvedClassConstant resolvedClassConstant, ResolvedNameAndType resolvedNameAndType, ListMultimap listMultimap
    ) {
        return new ResolvedInterfaceMethodRef(constantMemberRef, resolvedClassConstant, resolvedNameAndType, listMultimap);
    }

    @Override
    public ConstantPoolTag getTag() {
        return TAG;
    }
}
