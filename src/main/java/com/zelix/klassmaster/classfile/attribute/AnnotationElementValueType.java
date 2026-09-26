package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;

import java.io.DataOutputStream;
import java.io.IOException;

public interface AnnotationElementValueType {
    void updateAfterFieldRename();

    boolean isValid();

    void writeRemapped(DataOutputStream dataOutputStream, Object object, Object object1) throws IOException;

    void write(int ba, DataOutputStream dataOutputStream, int bb, int bc) throws IOException;

    void updateAfterClassRename(Object object, Object object1);

    int getByteLength();

    void collectReferencedClasses(Object object);

    void collectUsedConstants(char ba, int bb, UsedConstantsCollector usedConstantsCollector, char bc);

    void updateAfterMethodRename();
}
