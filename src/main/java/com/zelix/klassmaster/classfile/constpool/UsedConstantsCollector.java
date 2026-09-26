package com.zelix.klassmaster.classfile.constpool;

import com.zelix.klassmaster.classfile.insn.Instruction;
import com.zelix.klassmaster.classfile.insn.MethodBytecode;
import com.zelix.klassmaster.util.NonNullSet;
import com.zelix.klassmaster.util.SetMultiMap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UsedConstantsCollector {
    private Set usedUtf8s;
    private Set usedNameAndTypes;
    private Set usedMemberRefs;
    private Set usedClasses;
    public Set usedStrings;
    private Set usedLiterals;
    public Set usedConstantDynamics;
    public Set usedInvokeDynamics;
    public Set usedMethodTypes;
    public Set usedMethodHandles;
    public Set usedModules;
    public Set usedPackages;
    private boolean trackLdcUsage;
    public SetMultiMap ldcConstantToMethods;
    public SetMultiMap ldcWideConstantToMethods;
    public SetMultiMap methodToLdcConstants;
    public SetMultiMap methodToLdcWideConstants;

    public boolean isUtf8Used(ConstantUtf8 constantUtf8) {
        return this.usedUtf8s.contains(constantUtf8);
    }

    public Set getUsedUtf8s() {
        return new NonNullSet(this.usedUtf8s);
    }

    private boolean markNameAndTypeUsed(ResolvedNameAndType resolvedNameAndType) {
        boolean bl = this.usedNameAndTypes.add(resolvedNameAndType);
        if (bl) {
            this.markUtf8Used(resolvedNameAndType.getNameUtf8());
            this.markUtf8Used(resolvedNameAndType.getDescriptorUtf8());
        }

        return bl;
    }

    public boolean isPackageUsed(Object object) {
        return this.usedPackages.contains(object);
    }

    public boolean markMethodTypeUsed(ResolvedMethodType resolvedMethodType) {
        boolean bl = this.usedMethodTypes.add(resolvedMethodType);
        if (bl) {
            this.markUtf8Used(resolvedMethodType.getDescriptorUtf8());
        }

        return bl;
    }

    public boolean isFloatUsed(Object object) {
        return this.usedLiterals.contains(object);
    }

    public Set getUsedClasses() {
        return new NonNullSet(this.usedClasses);
    }

    public List getLdcConstants() {
        return this.trackLdcUsage ? new ArrayList(this.ldcConstantToMethods.keySet()) : null;
    }

    public boolean markInvokeDynamicUsed(ResolvedInvokeDynamic resolvedInvokeDynamic) {
        boolean bl = this.usedInvokeDynamics.add(resolvedInvokeDynamic);
        if (bl) {
            this.markNameAndTypeUsed(resolvedInvokeDynamic.getNameAndType());
        }

        return bl;
    }

    public boolean isStringUsed(Object object) {
        return this.usedStrings.contains(object);
    }

    public boolean isIntegerUsed(Object object) {
        return this.usedLiterals.contains(object);
    }

    public boolean isInvokeDynamicUsed(Object object) {
        return this.usedInvokeDynamics.contains(object);
    }

    public List getLdcWideConstants() {
        return this.trackLdcUsage ? new ArrayList(this.ldcWideConstantToMethods.keySet()) : null;
    }

    public boolean markStringUsed(ResolvedStringConstant resolvedStringConstant) {
        this.usedStrings.add(resolvedStringConstant);
        boolean bl = this.usedLiterals.add(resolvedStringConstant);
        if (bl) {
            this.markUtf8Used(resolvedStringConstant.getValueUtf8());
        }

        return bl;
    }

    public boolean markMethodHandleUsed(ResolvedMethodHandleConstant resolvedMethodHandleConstant) {
        boolean bl = this.usedMethodHandles.add(resolvedMethodHandleConstant);
        if (bl) {
            this.markMemberRefUsed(resolvedMethodHandleConstant.getMemberRef());
        }

        return bl;
    }

    private boolean markUtf8Used(ConstantUtf8 constantUtf8) {
        return this.usedUtf8s.add(constantUtf8);
    }

    public boolean isMethodTypeUsed(Object object) {
        return this.usedMethodTypes.contains(object);
    }

    public boolean markDoubleUsed(Object object) {
        return this.usedLiterals.add(object);
    }

    public Set getLdcReferencingMethods(ConstantPoolEntry constantPoolEntry) {
        HashSet hashSet = ZkmUtils.createHashSet();
        Set set1 = this.ldcConstantToMethods.getValues(constantPoolEntry);
        SetMultiMap setMultiMap;
        if (set1 != null) {
            hashSet.addAll(set1);
            setMultiMap = this.ldcWideConstantToMethods;
        } else {
            setMultiMap = this.ldcWideConstantToMethods;
        }

        Set set2 = setMultiMap.getValues(constantPoolEntry);
        if (set2 != null) {
            hashSet.addAll(set2);
        }

        return hashSet;
    }

    public boolean isDoubleUsed(Object object) {
        return this.usedLiterals.contains(object);
    }

    public Set getUsedMethodTypes() {
        return new NonNullSet(this.usedMethodTypes);
    }

    public boolean markFloatUsed(Object object) {
        return this.usedLiterals.add(object);
    }

    public boolean markModuleUsed(ResolvedConstantModule resolvedConstantModule) {
        boolean bl = this.usedModules.add(resolvedConstantModule);
        if (bl) {
            this.markUtf8Used(resolvedConstantModule.getNameUtf8());
        }

        return bl;
    }

    public Set getUsedInvokeDynamics() {
        return new NonNullSet(this.usedInvokeDynamics);
    }

    public Set getUsedNameAndTypes() {
        return new NonNullSet(this.usedNameAndTypes);
    }

    public boolean markUsed(ConstantPoolEntry constantPoolEntry, Object object, Object object1) {
        if (this.trackLdcUsage && object instanceof Instruction && object1 instanceof MethodBytecode) {
            Instruction instruction1 = (Instruction) object;
            MethodBytecode methodBytecode1 = (MethodBytecode) object1;
            if (instruction1.getOpcode() == 18) {
                this.ldcConstantToMethods.addValue(constantPoolEntry, methodBytecode1);
                this.methodToLdcConstants.addValue(methodBytecode1, constantPoolEntry);
            } else if (instruction1.getOpcode() == 19) {
                this.ldcWideConstantToMethods.addValue(constantPoolEntry, methodBytecode1);
                this.methodToLdcWideConstants.addValue(methodBytecode1, constantPoolEntry);
            }
        }

        switch (ConstantTagSwitchMap.TAG_SWITCH_TABLE[constantPoolEntry.getTag().ordinal()]) {
            case 1:
                return this.markUtf8Used((ConstantUtf8) constantPoolEntry);
            case 2:
                return this.markIntegerUsed((ConstantInteger) constantPoolEntry);
            case 3:
                return this.markFloatUsed((ConstantFloat) constantPoolEntry);
            case 4:
                return this.markLongUsed((ConstantLong) constantPoolEntry);
            case 5:
                return this.markDoubleUsed((ConstantDouble) constantPoolEntry);
            case 6:
                return this.markClassUsed((ResolvedClassConstant) constantPoolEntry);
            case 7:
                return this.markStringUsed((ResolvedStringConstant) constantPoolEntry);
            case 8:
                return this.markMemberRefUsed((ResolvedMemberRef) constantPoolEntry);
            case 9:
                return this.markMemberRefUsed((ResolvedMemberRef) constantPoolEntry);
            case 10:
                return this.markMemberRefUsed((ResolvedMemberRef) constantPoolEntry);
            case 11:
                return this.markNameAndTypeUsed((ResolvedNameAndType) constantPoolEntry);
            case 12:
                return this.markMethodHandleUsed((ResolvedMethodHandleConstant) constantPoolEntry);
            case 13:
                return this.markMethodTypeUsed((ResolvedMethodType) constantPoolEntry);
            case 14:
                return this.markConstantDynamicUsed((ResolvedConstantDynamic) constantPoolEntry);
            case 15:
                return this.markInvokeDynamicUsed((ResolvedInvokeDynamic) constantPoolEntry);
            case 16:
                return this.markModuleUsed((ResolvedConstantModule) constantPoolEntry);
            case 17:
                return this.markPackageUsed((ResolvedPackageConstant) constantPoolEntry);
            default:
                return false;
        }
    }

    public Set getUsedConstantDynamics() {
        return new NonNullSet(this.usedConstantDynamics);
    }

    public boolean isUsed(ConstantPoolEntry constantPoolEntry) {
        switch (ConstantTagSwitchMap.TAG_SWITCH_TABLE[constantPoolEntry.getTag().ordinal()]) {
            case 1:
                return this.isUtf8Used((ConstantUtf8) constantPoolEntry);
            case 2:
                return this.isIntegerUsed((ConstantInteger) constantPoolEntry);
            case 3:
                return this.isFloatUsed((ConstantFloat) constantPoolEntry);
            case 4:
                return this.isLongUsed((ConstantLong) constantPoolEntry);
            case 5:
                return this.isDoubleUsed((ConstantDouble) constantPoolEntry);
            case 6:
                return this.isClassUsed((ResolvedClassConstant) constantPoolEntry);
            case 7:
                return this.isStringUsed((ResolvedStringConstant) constantPoolEntry);
            case 8:
                return this.isMemberRefUsed((ResolvedMemberRef) constantPoolEntry);
            case 9:
                return this.isMemberRefUsed((ResolvedMemberRef) constantPoolEntry);
            case 10:
                return this.isMemberRefUsed((ResolvedMemberRef) constantPoolEntry);
            case 11:
                return this.isNameAndTypeUsed((ResolvedNameAndType) constantPoolEntry);
            case 12:
                return this.isMethodHandleUsed((ResolvedMethodHandleConstant) constantPoolEntry);
            case 13:
                return this.isMethodTypeUsed((ResolvedMethodType) constantPoolEntry);
            case 14:
                return this.isConstantDynamicUsed((ResolvedConstantDynamic) constantPoolEntry);
            case 15:
                return this.isInvokeDynamicUsed((ResolvedInvokeDynamic) constantPoolEntry);
            case 16:
                return this.isModuleUsed((ResolvedConstantModule) constantPoolEntry);
            case 17:
                return this.isPackageUsed((ResolvedPackageConstant) constantPoolEntry);
            default:
                return false;
        }
    }

    public Set getUsedLiterals() {
        return new NonNullSet(this.usedLiterals);
    }

    public boolean markPackageUsed(ResolvedPackageConstant resolvedPackageConstant) {
        boolean bl = this.usedPackages.add(resolvedPackageConstant);
        if (bl) {
            this.markUtf8Used(resolvedPackageConstant.getNameUtf8());
        }

        return bl;
    }

    public boolean markIntegerUsed(Object object) {
        return this.usedLiterals.add(object);
    }

    public boolean isNameAndTypeUsed(ResolvedNameAndType resolvedNameAndType) {
        return this.usedNameAndTypes.contains(resolvedNameAndType);
    }

    private boolean markLongUsed(ConstantLong constantLong) {
        return this.usedLiterals.add(constantLong);
    }

    public boolean isMemberRefUsed(ResolvedMemberRef resolvedMemberRef) {
        return this.usedMemberRefs.contains(resolvedMemberRef);
    }

    private boolean markClassUsed(ResolvedClassConstant resolvedClassConstant) {
        boolean bl = this.usedClasses.add(resolvedClassConstant);
        if (bl) {
            this.markUtf8Used(resolvedClassConstant.getNameUtf8());
        }

        return bl;
    }

    private boolean markMemberRefUsed(ResolvedMemberRef resolvedMemberRef) {
        boolean bl = this.usedMemberRefs.add(resolvedMemberRef);
        if (bl) {
            this.markClassUsed(resolvedMemberRef.getClassConstant());
            this.markNameAndTypeUsed(resolvedMemberRef.getNameAndType());
        }

        return bl;
    }

    public boolean isModuleUsed(Object object) {
        return this.usedModules.contains(object);
    }

    public boolean isMethodHandleUsed(Object object) {
        return this.usedMethodHandles.contains(object);
    }

    public UsedConstantsCollector(int ba) {
        this(ba, false);
    }

    public UsedConstantsCollector(int ba, boolean trackLdcUsage) {
        int bb = ZkmUtils.getPrimeCapacity(ba);
        this.usedUtf8s = ZkmUtils.createHashSet(bb);
        this.usedNameAndTypes = ZkmUtils.createHashSet(bb);
        this.usedMemberRefs = ZkmUtils.createHashSet(bb);
        this.usedClasses = ZkmUtils.createHashSet(bb);
        this.usedStrings = ZkmUtils.createHashSet(bb);
        this.usedLiterals = ZkmUtils.createHashSet(bb);
        this.usedConstantDynamics = ZkmUtils.createHashSet(bb);
        this.usedInvokeDynamics = ZkmUtils.createHashSet(bb);
        this.usedMethodTypes = ZkmUtils.createHashSet(bb);
        this.usedMethodHandles = ZkmUtils.createHashSet(bb);
        this.usedModules = ZkmUtils.createHashSet(bb);
        this.usedPackages = ZkmUtils.createHashSet(bb);
        this.trackLdcUsage = trackLdcUsage;
        if (trackLdcUsage) {
            this.ldcConstantToMethods = new SetMultiMap(bb);
            this.ldcWideConstantToMethods = new SetMultiMap(bb);
            this.methodToLdcConstants = new SetMultiMap();
            this.methodToLdcWideConstants = new SetMultiMap();
        }
    }

    public boolean markConstantDynamicUsed(ResolvedConstantDynamic resolvedConstantDynamic) {
        boolean bl = this.usedConstantDynamics.add(resolvedConstantDynamic);
        if (bl) {
            this.markNameAndTypeUsed(resolvedConstantDynamic.getNameAndType());
        }

        return bl;
    }

    public boolean isLongUsed(ConstantLong constantLong) {
        return this.usedLiterals.contains(constantLong);
    }

    public boolean isClassUsed(ResolvedClassConstant resolvedClassConstant) {
        return this.usedClasses.contains(resolvedClassConstant);
    }

    public boolean isConstantDynamicUsed(Object object) {
        return this.usedConstantDynamics.contains(object);
    }

    public Set getUsedMethodHandles() {
        return new NonNullSet(this.usedMethodHandles);
    }

    public Set getUsedMemberRefs() {
        return new NonNullSet(this.usedMemberRefs);
    }
}
