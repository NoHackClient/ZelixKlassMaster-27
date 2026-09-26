package com.zelix.klassmaster.obfuscator.exclude;

import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.script.ScriptEnvironment;

import java.util.List;

public abstract class ContainedInSpecList extends MemberExclusionSetsBase {
    public List unexclusionStatements;

    public ContainedInSpecList(ClassRepository classRepository1, List list1, List list2, ScriptEnvironment scriptEnvironment1) {
        super(classRepository1, list1, scriptEnvironment1);
        this.unexclusionStatements = list2;
    }
}
