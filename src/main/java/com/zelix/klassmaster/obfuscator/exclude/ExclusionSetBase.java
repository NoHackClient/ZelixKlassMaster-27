package com.zelix.klassmaster.obfuscator.exclude;

import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.FieldInfo;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.ArrayEnumeration;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class ExclusionSetBase extends AbstractExclusionSpec {
    public Map includedMethods;
    public Map excludedClasses;
    public Map excludedFields;
    public Map includedFields;
    public Map includedClasses;
    public Map excludedMethods;

    public abstract boolean excludeClass(ProgramClass programClass1, String string) throws ZkmException, IOException;

    public final Enumeration findClassesDeclaringMethod(String string) {
        return this.classRepository.enumerateMethodSignaturesNamed(string);
    }

    public Enumeration findClassesDeclaringField(String string) {
        return this.classRepository.enumerateFieldsNamed(string);
    }

    public boolean hasNoIncludedMethods() {
        return this.includedMethods.size() == 0;
    }

    public final boolean isClassOrOuterExcluded(ProgramClass programClass1) {
        if (programClass1 == null) {
            return false;
        }

        boolean bl = this.excludedClasses.containsKey(programClass1);
        if (!bl && programClass1.hasVersionedVariants()) {
            Iterator iterator = programClass1.getVersionedVariants().iterator();

            while (iterator.hasNext()) {
                ClassFileBase classFileBase = (ClassFileBase) iterator.next();
                bl = this.excludedClasses.containsKey(classFileBase);
                if (bl) {
                    break;
                }
            }
        }

        return bl;
    }

    public Enumeration getExcludedMethods() {
        MethodInfo[] methodInfos = new MethodInfo[this.excludedMethods.size()];
        Iterator iterator = this.excludedMethods.keySet().iterator();
        int ba = 0;

        while (iterator.hasNext()) {
            methodInfos[ba++] = (MethodInfo) iterator.next();
        }

        return new ArrayEnumeration(methodInfos);
    }

    public final boolean hasNoIncludedClasses() {
        return this.includedClasses.size() == 0;
    }

    public Enumeration getIncludedFields() {
        FieldInfo[] fieldInfos = new FieldInfo[this.includedFields.size()];
        Iterator iterator = this.includedFields.keySet().iterator();
        int ba = 0;

        while (iterator.hasNext()) {
            fieldInfos[ba++] = (FieldInfo) iterator.next();
        }

        return new ArrayEnumeration(fieldInfos);
    }

    public final boolean hasNoExcludedClasses() {
        return this.excludedClasses.size() == 0;
    }

    public ExclusionSetBase(ClassRepository classRepository1, List list1, ScriptEnvironment scriptEnvironment1) {
        super(classRepository1, list1, scriptEnvironment1);
    }

    public boolean hasNoIncludedFields() {
        return this.includedFields.size() == 0;
    }

    public Enumeration getCandidateClasses() {
        return this.classRepository.enumerateProgramClasses();
    }

    public Enumeration getExcludedFields() {
        FieldInfo[] fieldInfos = new FieldInfo[this.excludedFields.size()];
        Iterator iterator = this.excludedFields.keySet().iterator();
        int ba = 0;

        while (iterator.hasNext()) {
            fieldInfos[ba++] = (FieldInfo) iterator.next();
        }

        return new ArrayEnumeration(fieldInfos);
    }

    public final boolean isClassNameExcluded(String string) {
        ClassHierarchyNode classHierarchyNode = ClassHierarchyNode.findNode(string);
        if (classHierarchyNode != null) {
            ProgramClass programClass1 = classHierarchyNode.getProgramClass();
            if (programClass1 != null) {
                return this.excludedClasses.containsKey(programClass1);
            }
        }

        return false;
    }

    public Enumeration getCandidateFields() {
        HashSet hashSet = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(this.excludedFields.size() + this.includedFields.size()));
        hashSet.addAll(this.excludedFields.keySet());
        hashSet.addAll(this.includedFields.keySet());
        return ArrayEnumeration.fromCollection(hashSet);
    }

    public Enumeration getIncludedMethods() {
        MethodInfo[] methodInfos = new MethodInfo[this.includedMethods.size()];
        Iterator iterator = this.includedMethods.keySet().iterator();
        int ba = 0;

        while (iterator.hasNext()) {
            methodInfos[ba++] = (MethodInfo) iterator.next();
        }

        return new ArrayEnumeration(methodInfos);
    }

    public final boolean isClassIncluded(Object object) {
        return this.includedClasses.containsKey(object);
    }

    public Enumeration getCandidateMethods() {
        HashSet hashSet = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(this.excludedMethods.size() + this.includedMethods.size()));
        hashSet.addAll(this.excludedMethods.keySet());
        hashSet.addAll(this.includedMethods.keySet());
        return ArrayEnumeration.fromCollection(hashSet);
    }

    public boolean isMethodExcluded(Object object) {
        return this.excludedMethods.containsKey(object);
    }

    public boolean isFieldExcluded(Object object) {
        return this.excludedFields.containsKey(object);
    }

    public final Enumeration getIncludedClasses() {
        return ArrayEnumeration.fromCollection(this.includedClasses.keySet());
    }

    public final boolean isClassExcluded(Object object) {
        return this.excludedClasses.containsKey(object);
    }

    public final Enumeration getExcludedClasses() {
        return ArrayEnumeration.fromCollection(this.excludedClasses.keySet());
    }
}
