package com.zelix.klassmaster.obfuscator.exclude;

import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.ArrayEnumeration;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;

public abstract class MemberExclusionSetsBase extends AbstractExclusionSpec {
    public HashSet matchedFields;
    public HashSet unmatchedMethods;
    public HashSet matchedClasses;
    public HashSet matchedMethods;
    public HashSet unmatchedFields;
    public HashSet unmatchedClasses;

    public Enumeration getUnmatchedMethods() {
        return ArrayEnumeration.fromCollection(this.unmatchedMethods);
    }

    public Enumeration getUnmatchedFields() {
        return ArrayEnumeration.fromCollection(this.unmatchedFields);
    }

    public MemberExclusionSetsBase(ClassRepository classRepository1, List list1, ScriptEnvironment scriptEnvironment1) {
        super(classRepository1, list1, scriptEnvironment1);
    }

    public Enumeration getMatchedMethods() {
        return ArrayEnumeration.fromCollection(this.matchedMethods);
    }

    public abstract Enumeration getMemberOwnerClasses();

    public Enumeration getMatchedFields() {
        return ArrayEnumeration.fromCollection(this.matchedFields);
    }

    public boolean hasNoMatchedMethods() {
        return this.matchedMethods.size() == 0;
    }

    public final boolean hasNoMatchedClasses() {
        return this.matchedClasses.size() == 0;
    }

    public boolean hasNoMatchedFields() {
        return this.matchedFields.size() == 0;
    }
}
