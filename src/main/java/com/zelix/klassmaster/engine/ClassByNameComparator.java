package com.zelix.klassmaster.engine;

import com.zelix.klassmaster.classfile.ProgramClass;

import java.util.Comparator;

public class ClassByNameComparator implements Comparator {
    public final ObfuscationEngine engine;

    public int compareByName(ProgramClass programClass1, ProgramClass programClass2) {
        return programClass1.getDisplayLocationName().compareTo(programClass2.getDisplayLocationName());
    }

    @Override
    public int compare(Object object, Object object1) {
        return this.compareByName((ProgramClass) object, (ProgramClass) object1);
    }

    public ClassByNameComparator(ObfuscationEngine obfuscationEngine) {
        this.engine = obfuscationEngine;
    }
}
