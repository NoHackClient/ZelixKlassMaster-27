package com.zelix.klassmaster.classfile.constpool;

public abstract class ClassConstantBase extends ConstantPoolEntry {
    public static final ConstantPoolTag CLASS_TAG = ConstantPoolTag.CLASS;

    public abstract void setClassName(String string);

    @Override
    public ConstantPoolTag getTag() {
        return CLASS_TAG;
    }

    public ClassConstantBase(int ba, AbstractConstantPool abstractConstantPool) {
        super(ba, abstractConstantPool);
    }

    public abstract String getDottedClassName();

    public abstract String getClassName();
}
