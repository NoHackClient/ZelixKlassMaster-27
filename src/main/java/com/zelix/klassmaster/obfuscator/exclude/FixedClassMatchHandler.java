package com.zelix.klassmaster.obfuscator.exclude;

import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

public abstract class FixedClassMatchHandler extends ExclusionSetBase {
    public final Set matchedClasses = ZkmUtils.createHashSet();

    public boolean isMatchedClass(Object object) {
        return this.matchedClasses.contains(object);
    }

    public FixedClassMatchHandler(ClassRepository classRepository1, List list1, ScriptEnvironment scriptEnvironment1) {
        super(classRepository1, list1, scriptEnvironment1);
    }

    @Override
    public final boolean excludeClass(ProgramClass programClass1, String string) throws ZkmException, IOException {
        this.matchedClasses.add(programClass1);
        boolean bl = this.markClassExcluded(programClass1, programClass1);
        if (super.scriptEnvironment.isVerbose()) {
            super.logWriter.println("\tMatching class \"" + this.describeClass(programClass1) + "\" as fixed because of \"" + string + "\"");
        }

        return bl;
    }

    public boolean markClassExcluded(ProgramClass programClass1, Object object) {
        Object object1 = super.includedClasses.remove(programClass1);
        if (object1 != null) {
            super.excludedClasses.put(programClass1, object);
        }

        return object1 != null;
    }

    public Enumeration getMatchedClasses() {
        return Collections.enumeration(this.matchedClasses);
    }
}
