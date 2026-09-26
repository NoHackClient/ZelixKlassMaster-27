package com.zelix.klassmaster.classfile.constpool;

import com.zelix.klassmaster.classfile.AbstractMethodInfo;
import com.zelix.klassmaster.classfile.hierarchy.ClassMemberLookup;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.IOException;

public class ResolvedInterfaceMethodRef extends ResolvedMethodRef {
    public static final ConstantPoolTag TAG = ConstantPoolTag.INTERFACE_METHODREF;

    public ResolvedInterfaceMethodRef(
            AbstractConstantPool abstractConstantPool,
            ResolvedClassConstant resolvedClassConstant,
            ResolvedNameAndType resolvedNameAndType,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        super(abstractConstantPool, resolvedClassConstant, resolvedNameAndType, classMemberLookup1, classResolver1);
    }

    public ResolvedInterfaceMethodRef(
            ConstantMemberRef constantMemberRef, ResolvedClassConstant resolvedClassConstant, ResolvedNameAndType resolvedNameAndType, ListMultimap listMultimap
    ) {
        super(constantMemberRef, resolvedClassConstant, resolvedNameAndType, listMultimap);
    }

    @Override
    public ConstantPoolTag getTag() {
        return TAG;
    }

    public ResolvedInterfaceMethodRef(
            AbstractConstantPool abstractConstantPool,
            ResolvedClassConstant resolvedClassConstant,
            ResolvedNameAndType resolvedNameAndType,
            AbstractMethodInfo abstractMethodInfo
    ) {
        super(abstractConstantPool, resolvedClassConstant, resolvedNameAndType, abstractMethodInfo);
    }
}
