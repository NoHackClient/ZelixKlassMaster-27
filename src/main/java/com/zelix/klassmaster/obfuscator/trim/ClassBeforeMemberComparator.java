package com.zelix.klassmaster.obfuscator.trim;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ProgramClass;

import java.util.Comparator;

public class ClassBeforeMemberComparator implements Comparator {
    public final TrimProcessor trimProcessor;

    public int compareComponents(ClassFileComponent classFileComponent, ClassFileComponent classFileComponent1) {
        if (classFileComponent instanceof ProgramClass && !(classFileComponent1 instanceof ProgramClass)) {
            return -1;
        } else {
            return (!(classFileComponent instanceof ProgramClass) || !(classFileComponent1 instanceof ProgramClass))
                    && (classFileComponent instanceof ProgramClass || classFileComponent1 instanceof ProgramClass)
                    ? 1
                    : 0;
        }
    }

    public ClassBeforeMemberComparator(TrimProcessor trimProcessor1) {
        this.trimProcessor = trimProcessor1;
    }

    @Override
    public int compare(Object object, Object object1) {
        return this.compareComponents((ClassFileComponent) object, (ClassFileComponent) object1);
    }
}
