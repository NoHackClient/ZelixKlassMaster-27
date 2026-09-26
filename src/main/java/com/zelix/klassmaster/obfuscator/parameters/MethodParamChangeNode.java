package com.zelix.klassmaster.obfuscator.parameters;

import com.zelix.klassmaster.classfile.FieldInfo;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.constpool.ResolvedFieldRef;
import com.zelix.klassmaster.classfile.hierarchy.ClassMemberLookup;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.classfile.hierarchy.InheritedMemberAnalyzer;
import com.zelix.klassmaster.classfile.insn.ConstantRefInstruction;
import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MethodParamChangeNode {
    public int maxStack;
    public long effectiveKey;
    public long randomKey;
    public Long previousPrimaryKey;
    public long primaryKey;
    public ResolvedFieldRef keyFieldRef;
    public long derivedKey;
    public LongKeyNode secondKeyNode;
    public MethodParamChangeNode falseParent;
    public LongKeyNode firstKeyNode;
    public MethodParamChangeNode parent;
    public List initInstructions;
    public long secondaryKey;
    public final List dependents = new ArrayList();
    public final List falseDependents = new ArrayList();
    public final MethodInfo initMethod;
    public final boolean definesKeyField;
    public final boolean sharesKeyField;

    public long getSecondaryKey() {
        return this.secondaryKey;
    }

    public MethodParamChangeNode getParent() {
        return this.parent;
    }

    public void setKeys(LongKeyNode longKeyNode, LongKeyNode longKeyNode1, long primaryKey, long secondaryKey) {
        this.firstKeyNode = longKeyNode;
        this.secondKeyNode = longKeyNode1;
        this.primaryKey = primaryKey;
        this.secondaryKey = secondaryKey;
        this.derivedKey = this.secondKeyNode.getMixedKey();
    }

    public int getMaxStack() {
        return this.maxStack;
    }

    public long getEffectiveKey() {
        return this.effectiveKey;
    }

    public ResolvedFieldRef getKeyFieldRef() {
        return this.keyFieldRef;
    }

    public boolean hasKeyField() {
        return this.definesKeyField;
    }

    public long getPrimaryKey() {
        return this.primaryKey;
    }

    public List getDependents() {
        return new ArrayList(this.dependents);
    }

    public MethodParamChangeNode(MethodInfo methodInfo1) {
        this(methodInfo1, false, false);
    }

    public boolean hasFalseDependents() {
        return !this.falseDependents.isEmpty();
    }

    public LongKeyNode getSecondKeyNode() {
        return this.secondKeyNode;
    }

    public void buildInitInstructions(
            ProgramClass programClass1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1,
            List list1,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            MethodParameterChanger methodParameterChanger
    ) throws ZkmException, IOException {
        this.effectiveKey = this.derivedKey;
        MethodParamChangeNode methodParamChangeNode1 = null;
        if (!this.initMethod.isStaticInitializer() && this.parent != null && this.parent.sharesKeyField) {
            this.effectiveKey = 0L;
            methodParamChangeNode1 = this.parent;
        }

        this.maxStack = methodParameterChanger.buildInitKeyCode(programClass1, list1, this, methodParamChangeNode1, classMemberLookup1, classResolver1);
        if (this.definesKeyField) {
            boolean bl = programClass1.isInterface();
            FieldInfo fieldInfo = programClass1.createUniquelyNamedStaticField("J", bl ? 4 : 1, true, inheritedMemberAnalyzer, classMemberLookup1, 5);
            this.keyFieldRef = programClass1.getClassConstantPool()
                    .getOrCreateFieldRef(programClass1.getClassName(), fieldInfo.getSourceName(), fieldInfo.getDescriptor(), list1, fieldInfo);
            this.initInstructions.add(new ConstantRefInstruction(179, this.keyFieldRef));
        }
    }

    public void replacePrimaryKey(long primaryKey) {
        this.previousPrimaryKey = this.primaryKey;
        this.primaryKey = primaryKey;
    }

    public boolean isKeyFieldShared() {
        return this.sharesKeyField;
    }

    public long getFirstNodeXorMask() {
        return this.firstKeyNode.getXorMask();
    }

    public List getInitInstructions() {
        return Collections.unmodifiableList(this.initInstructions);
    }

    public LongKeyNode getFirstKeyNode() {
        return this.firstKeyNode;
    }

    public MethodParamChangeNode(MethodInfo methodInfo1, boolean definesKeyField, boolean sharesKeyField) {
        this.initMethod = methodInfo1;
        this.definesKeyField = definesKeyField;
        this.sharesKeyField = sharesKeyField;
    }

    public void setRandomKey(long randomKey) {
        this.randomKey = randomKey;
    }

    public boolean hasFalseParent() {
        return this.falseParent != null;
    }

    public long getRandomKey() {
        return this.randomKey;
    }

    public List getFalseDependents() {
        return new ArrayList(this.falseDependents);
    }

    public MethodInfo getInitMethod() {
        return this.initMethod;
    }

    public void setInitInstructions(List list1) {
        this.initInstructions = list1;
    }

    public MethodParamChangeNode getFalseParent() {
        return this.falseParent;
    }

    public boolean hasParent() {
        return this.parent != null;
    }

    public void addFalseDependent(MethodParamChangeNode methodParamChangeNode1) {
        this.falseDependents.add(methodParamChangeNode1);
        methodParamChangeNode1.falseParent = this;
    }

    public void addDependent(MethodParamChangeNode methodParamChangeNode1) {
        this.dependents.add(methodParamChangeNode1);
        methodParamChangeNode1.parent = this;
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
    }
}
