package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.config.IgnoreMissingReferencesSpec;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;

import java.io.IOException;
import java.util.Set;

public interface ReferencingAttribute {
    void resolveReferences(ClassResolver classResolver1, IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1, ScriptEnvironment scriptEnvironment1) throws ZkmException, IOException;

    void applyMethodRenames();

    void collectReferencedClasses(Set set1);

    void applyFieldRenames();

    void collectReferencedMembers(Set set1, Set set2, Set set3, Set set4);

    void collectUsedConstants(char ba, int bb, UsedConstantsCollector usedConstantsCollector, char bc);

    String getOwnerName();

    boolean trimAnnotations(Object object, Object object1, Object object2);
}
