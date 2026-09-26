package com.zelix.klassmaster.classfile.hierarchy;

import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.NamedTypeRef;
import com.zelix.klassmaster.config.IgnoreMissingReferencesSpec;
import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public class ArrayTypeReference implements NamedTypeRef {
    private static final String LOOKUP_REASON = "looking for array type";
    public final String arrayDescriptor;
    public final int dimensions;
    public ClassFileBase elementClass;

    @Override
    public String getClassName() {
        return this.elementClass == null
                ? this.arrayDescriptor
                : ClassFileBase.toTypeDescriptor(this.elementClass.getClassName(), this.dimensions).replace('/', '.');
    }

    public ArrayTypeReference(String string, Integer integer, ClassResolver classResolver1, IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1) throws ZkmException, IOException {
        this.arrayDescriptor = string;
        this.dimensions = string.lastIndexOf("[") + 1;
        String string1 = ClassFileBase.extractClassName(this.arrayDescriptor);
        if (string1 != null) {
            this.elementClass = classResolver1.getVersionedClass(string1, integer, LOOKUP_REASON, ignoreMissingReferencesSpec1);
        }
    }

    @Override
    public boolean isPrimitive() {
        return false;
    }
}
