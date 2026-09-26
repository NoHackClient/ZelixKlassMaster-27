package com.zelix.klassmaster.obfuscator.trim;

import com.zelix.klassmaster.classfile.FieldInfo;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.util.ZkmUtils;

import java.util.Iterator;
import java.util.Set;

public class ClassMemberSets {
    public final Set classes;
    public final Set fields;
    public final Set methods;

    public Set getMethods() {
        return this.methods;
    }

    public void removeClass(ProgramClass programClass1) {
        this.classes.remove(programClass1);

        for (FieldInfo fieldInfo : programClass1.getFieldInfos()) {
            this.fields.remove(fieldInfo);
        }

        for (MethodInfo methodInfo1 : programClass1.getMethodInfos()) {
            this.methods.remove(methodInfo1);
        }
    }

    public boolean containsMethod(Object object) {
        return this.methods.contains(object);
    }

    public void retainAcceptedMembers(TrimProcessor trimProcessor1) {
        Iterator iterator = this.fields.iterator();

        while (iterator.hasNext()) {
            FieldInfo fieldInfo = (FieldInfo) iterator.next();
            if (!trimProcessor1.isFieldExcluded(fieldInfo)) {
                iterator.remove();
            }
        }

        Iterator iterator1 = this.methods.iterator();

        while (iterator1.hasNext()) {
            MethodInfo methodInfo1 = (MethodInfo) iterator1.next();
            if (!trimProcessor1.isMethodExcluded(methodInfo1)) {
                iterator1.remove();
            }
        }
    }

    public ClassMemberSets(Set set1, Set set2, Set set3) {
        this.classes = ZkmUtils.createHashSetFrom(set1);
        this.fields = ZkmUtils.createHashSetFrom(set2);
        this.methods = ZkmUtils.createHashSetFrom(set3);
    }
}
