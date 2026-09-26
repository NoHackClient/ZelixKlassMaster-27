package com.zelix.klassmaster.classfile.constpool;

import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.IOException;

public class ConstantFieldref extends ConstantMemberRef implements ResolvableConstant {
    public static final ConstantPoolTag TAG = ConstantPoolTag.FIELDREF;

    @Override
    public ResolvedMemberRef createResolvedRef(
            ConstantMemberRef constantMemberRef, ResolvedClassConstant resolvedClassConstant, ResolvedNameAndType resolvedNameAndType, ListMultimap listMultimap
    ) {
        return new ResolvedFieldRef(constantMemberRef, resolvedClassConstant, resolvedNameAndType, listMultimap);
    }

    @Override
    public ConstantPoolTag getTag() {
        return TAG;
    }

    public ConstantFieldref(int ba, ClassFileInputStream classFileInputStream, AbstractConstantPool abstractConstantPool) throws IOException {
        super(ba, classFileInputStream, abstractConstantPool);
    }
}
