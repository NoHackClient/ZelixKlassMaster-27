package com.zelix.klassmaster.classfile;

import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.util.ZkmUtils;

import java.util.Map;

public class PrimitiveTypeDescriptor implements NamedTypeRef {
    public final String descriptor;
    public static final Map BY_DESCRIPTOR;


    static {
        ZkmUtils.createHashMap();
        BY_DESCRIPTOR = ZkmUtils.createHashMap();
        BY_DESCRIPTOR.put("B", new PrimitiveTypeDescriptor("B"));
        BY_DESCRIPTOR.put("Z", new PrimitiveTypeDescriptor("Z"));
        BY_DESCRIPTOR.put("S", new PrimitiveTypeDescriptor("S"));
        BY_DESCRIPTOR.put("C", new PrimitiveTypeDescriptor("C"));
        BY_DESCRIPTOR.put("I", new PrimitiveTypeDescriptor("I"));
        BY_DESCRIPTOR.put("J", new PrimitiveTypeDescriptor("J"));
        BY_DESCRIPTOR.put("F", new PrimitiveTypeDescriptor("F"));
        BY_DESCRIPTOR.put("D", new PrimitiveTypeDescriptor("D"));
        BY_DESCRIPTOR.put("V", new PrimitiveTypeDescriptor("V"));
    }


    public static PrimitiveTypeDescriptor forDescriptor(Object object) {
        return (PrimitiveTypeDescriptor) BY_DESCRIPTOR.get(object);
    }

    @Override
    public String getClassName() {
        return this.descriptor;
    }

    public static String getTypeFieldName() {
        return "TYPE";
    }

    public String getWrapperClassName() {
        return ConstantPoolEntry.getWrapperClassName(this.descriptor);
    }

    private PrimitiveTypeDescriptor(String string) {
        this.descriptor = string;
    }

    public static String getTypeFieldDescriptor() {
        return "Ljava/lang/Class;";
    }

    @Override
    public boolean isPrimitive() {
        return true;
    }
}
