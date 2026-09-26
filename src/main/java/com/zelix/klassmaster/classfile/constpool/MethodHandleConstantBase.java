package com.zelix.klassmaster.classfile.constpool;

public abstract class MethodHandleConstantBase extends ConstantPoolEntry {
    public static final ConstantPoolTag TAG = ConstantPoolTag.METHOD_HANDLE;

    public MethodHandleConstantBase(int ba, AbstractConstantPool abstractConstantPool) {
        super(ba, abstractConstantPool);
    }

    @Override
    public final ConstantPoolTag getTag() {
        return TAG;
    }
}
