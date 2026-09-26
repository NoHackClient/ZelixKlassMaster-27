package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.util.ZkmUtils;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;

public class SignatureTypeReferences {
    public final HashSet annotationClasses = ZkmUtils.createHashSet(13);
    public final HashSet referencedClasses = ZkmUtils.createHashSet(13);
    public final HashSet referencedFields = ZkmUtils.createHashSet(13);
    public final HashSet referencedMethods = ZkmUtils.createHashSet(13);

    public HashSet getAnnotationClasses() {
        return this.annotationClasses;
    }

    public HashSet getReferencedMethods() {
        return this.referencedMethods;
    }

    public HashSet getReferencedClasses() {
        return this.referencedClasses;
    }

    public HashSet getReferencedFields() {
        return this.referencedFields;
    }

    public Enumeration enumerateReferencedFields() {
        return Collections.enumeration(this.referencedFields);
    }

    public Enumeration enumerateAnnotationClasses() {
        return Collections.enumeration(this.annotationClasses);
    }

    public Enumeration enumerateReferencedMethods() {
        return Collections.enumeration(this.referencedMethods);
    }

    public Enumeration enumerateReferencedClasses() {
        return Collections.enumeration(this.referencedClasses);
    }
}
