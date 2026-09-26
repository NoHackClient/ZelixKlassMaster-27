package com.zelix.klassmaster.obfuscator.exclude;

import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;

import java.io.IOException;
import java.util.List;

public abstract class ExclusionHandlerBase extends ExclusionSetBase {
    public List unexclusionStatements;

    public abstract void unexcludeClass(ProgramClass programClass1, String string) throws ZkmException, IOException;

    public ExclusionHandlerBase(ClassRepository classRepository1, List list1, List list2, ScriptEnvironment scriptEnvironment1) {
        super(classRepository1, list1, scriptEnvironment1);
        this.unexclusionStatements = list2;
    }
}
