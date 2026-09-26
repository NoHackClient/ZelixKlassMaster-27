package com.zelix.klassmaster.classfile;

import com.zelix.klassmaster.classfile.attribute.Attribute;
import com.zelix.klassmaster.classfile.constpool.AbstractConstantPool;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolProvider;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.util.IntegerCache;
import com.zelix.klassmaster.util.ObservableSupport;
import com.zelix.klassmaster.util.VisitableNode;

import java.io.IOException;

public abstract class ClassFileComponent extends ObservableSupport implements ConstantPoolProvider {
    private ClassFileComponent parent;
    private static VisitableNode[] flowGuardNodes;
    public static final IntegerCache integerCache = IntegerCache.getInstance();

    public String getClassSimpleName() {
        return this.parent.getClassSimpleName();
    }

    public String getInputPath() {
        return this.parent.getInputPath();
    }

    public void setParent(ClassFileComponent classFileComponent1) {
        this.parent = classFileComponent1;
    }

    public static VisitableNode[] getFlowGuardNodes() {
        return flowGuardNodes;
    }

    public abstract void collectUsedConstants(char ba, int bb, UsedConstantsCollector usedConstantsCollector, char bc);

    public String getOriginalClassName() {
        return this.parent.getOriginalClassName();
    }

    public String getLocationName() {
        return this.parent.getLocationName();
    }

    public static void setFlowGuardNodes(VisitableNode[] visitableNodes1) {
        flowGuardNodes = visitableNodes1;
    }

    public boolean isClassFile() {
        return false;
    }

    public String getDisplayLocationName() {
        return this.parent.getDisplayLocationName();
    }

    public ClassFileBase getOwningClass() {
        ClassFileComponent classFileComponent1 = this;

        while (classFileComponent1.parent != null) {
            classFileComponent1 = classFileComponent1.parent;
        }

        return (ClassFileBase) classFileComponent1;
    }

    public String getPackagePath() {
        return this.parent.getPackagePath();
    }

    @Override
    public ConstantPoolEntry getConstantPoolEntry(int ba) {
        return this.parent.getConstantPoolEntry(ba);
    }

    public String formatDeclaration() throws ZkmProcessingException, IOException {
        return "";
    }

    public ClassFileComponent(ClassFileComponent classFileComponent1) {
        this.parent = classFileComponent1;
    }

    public String getOriginalDottedName() {
        return this.parent.getOriginalDottedName();
    }

    public final ProgramClass selectMatchingProgramClass(ProgramClass programClass1) {
        return (ProgramClass) this.selectMatchingVariant(programClass1);
    }

    public ClassFileComponent getParent() {
        return this.parent;
    }

    public final Attribute getEnclosingAttribute() {
        ClassFileComponent classFileComponent1 = this;

        while (classFileComponent1.parent != null && !(classFileComponent1 instanceof Attribute)) {
            classFileComponent1 = classFileComponent1.parent;
        }

        return (Attribute) classFileComponent1;
    }

    public String getDottedClassName() {
        return this.parent.getDottedClassName();
    }

    public String getClassName() {
        return this.parent.getClassName();
    }

    public AbstractConstantPool getConstantPool() {
        return this.parent.getConstantPool();
    }

    static {
        VisitableNode[] visitableNodes1 = new VisitableNode[4];
        setFlowGuardNodes(visitableNodes1);
    }

    public final ClassFileBase selectMatchingVariant(ClassFileBase classFileBase) {
        ClassFileBase classFileBase2 = this.getOwningClass();
        ClassFileBase classFileBase1;
        if (classFileBase2.isVersionedVariant() && classFileBase.hasVersionedVariants()) {
            classFileBase1 = classFileBase.selectVersionForRelease(classFileBase2.getReleaseVersion());
        } else {
            classFileBase1 = classFileBase;
        }

        return classFileBase1;
    }

    public final int getParentKind() {
        if (this.parent instanceof ClassFileBase) {
            return 1;
        } else if (this.parent instanceof AbstractFieldInfo) {
            return 2;
        } else {
            return this.parent instanceof AbstractMethodInfo ? 3 : 4;
        }
    }
}
