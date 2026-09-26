package com.zelix.klassmaster.classfile.hierarchy;

import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.config.IgnoreMissingReferencesSpec;
import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public class ClassResolver {
    public ClasspathClassLoader classLoader;

    public ClassFileBase getVersionedClass(String string, Integer integer, String string1) throws ZkmException, IOException {
        return this.findVersionedClass(string, integer, string1);
    }

    public ClassFileBase getClassFile(String string) throws ZkmException, IOException {
        return this.findClassFile(string, true, (String) null);
    }

    public ClassFileBase getVersionedClass(String string, Integer integer, String string1, IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1) throws ZkmException, IOException {
        return this.resolveClass(string, integer, true, string1, ignoreMissingReferencesSpec1);
    }

    public ClassFileBase getClassFile(String string, String string1, IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1) throws ZkmException, IOException {
        return this.resolveClass(string, null, true, string1, ignoreMissingReferencesSpec1);
    }

    public ClassFileBase findClassFile(String string) throws ZkmException, IOException {
        return this.findClassFile(string, false, (String) null);
    }

    public void releaseLoader() {
        if (this.classLoader != null) {
            this.classLoader.releaseReaders();
        }
    }

    public ClassFileBase findClassFile(String string, boolean bl, String string1) throws ZkmException, IOException {
        return this.resolveClass(string, null, bl, string1, null);
    }

    public ClassFileBase resolveClass(String string, Integer integer, boolean bl, String string1, IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1) throws ZkmException, IOException {
        ClassFileBase classFileBase = ClassHierarchyNode.findClassFile(string);
        if (classFileBase != null) {
            return integer != null && classFileBase.hasVersionedVariants() ? classFileBase.selectVersionForRelease(integer) : classFileBase;
        } else {
            return this.classLoader.loadClassFile(string, integer, bl, string1, ignoreMissingReferencesSpec1);
        }
    }

    public ClassResolver(ClasspathClassLoader classpathClassLoader1) {
        this.classLoader = classpathClassLoader1;
    }

    public ClassFileBase findVersionedClass(String string, Integer integer, String string1) throws ZkmException, IOException {
        return this.resolveClass(string, integer, true, string1, null);
    }

    public ClassFileBase getClassFile(String string, String string1) throws ZkmException, IOException {
        return this.findClassFile(string, true, string1);
    }

    public ClassFileBase getVersionedClass(String string, Integer integer) throws ZkmException, IOException {
        return this.findVersionedClass(string, integer, (String) null);
    }
}
