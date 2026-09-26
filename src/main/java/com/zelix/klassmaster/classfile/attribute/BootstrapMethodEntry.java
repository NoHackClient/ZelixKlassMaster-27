package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.MemberInfo;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.constpool.ClassConstantReplaceable;
import com.zelix.klassmaster.classfile.constpool.ConstantInteger;
import com.zelix.klassmaster.classfile.constpool.ConstantIntegerReplacer;
import com.zelix.klassmaster.classfile.constpool.ConstantKindSwitchMap;
import com.zelix.klassmaster.classfile.constpool.ConstantLong;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolTag;
import com.zelix.klassmaster.classfile.constpool.LoadableConstant;
import com.zelix.klassmaster.classfile.constpool.LongConstantReplaceable;
import com.zelix.klassmaster.classfile.constpool.ResolvedClassConstant;
import com.zelix.klassmaster.classfile.constpool.ResolvedConstantDynamic;
import com.zelix.klassmaster.classfile.constpool.ResolvedMethodHandleConstant;
import com.zelix.klassmaster.classfile.constpool.ResolvedMethodType;
import com.zelix.klassmaster.classfile.constpool.ResolvedStringConstant;
import com.zelix.klassmaster.classfile.constpool.StringConstantReplaceable;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class BootstrapMethodEntry
        extends ClassFileComponent
        implements StringConstantReplaceable,
        ClassConstantReplaceable,
        ConstantIntegerReplacer,
        LongConstantReplaceable {
    public String errorMessage;
    public boolean valid = true;
    public ResolvedMethodHandleConstant methodHandle;
    public ConstantPoolEntry[] arguments;
    public int index;

    public boolean isValid() {
        return this.valid;
    }

    public BootstrapMethodEntry(
            BootstrapMethodsAttribute bootstrapMethodsAttribute1,
            ResolvedMethodHandleConstant resolvedMethodHandleConstant,
            ConstantPoolEntry[] constantPoolEntrys,
            int index
    ) {
        super(bootstrapMethodsAttribute1);
        this.methodHandle = resolvedMethodHandleConstant;
        this.arguments = constantPoolEntrys;
        this.index = index;
    }

    public String getBootstrapClassName() {
        return this.methodHandle != null ? this.methodHandle.getMemberClassName() : null;
    }

    public void writeRemapped(DataOutputStream dataOutputStream, Map map1) throws IOException {
        dataOutputStream.writeShort(this.methodHandle.getIndex());
        dataOutputStream.writeShort(this.arguments.length);

        for (ConstantPoolEntry constantPoolEntry : this.arguments) {
            ConstantPoolEntry constantPoolEntry1 = (ConstantPoolEntry) map1.get(constantPoolEntry);
            if (constantPoolEntry1 != null) {
                dataOutputStream.writeShort(constantPoolEntry1.getIndex());
            } else {
                dataOutputStream.writeShort(constantPoolEntry.getIndex());
            }
        }
    }

    public String getUniqueKey() {
        return String.valueOf(this.getIndex()) + '~' + System.identityHashCode(this);
    }

    public void write(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeShort(this.methodHandle.getIndex());
        dataOutputStream.writeShort(this.arguments.length);

        for (ConstantPoolEntry constantPoolEntry : this.arguments) {
            dataOutputStream.writeShort(constantPoolEntry.getIndex());
        }
    }

    public void renumberEntries() {
        ((BootstrapMethodsAttribute) this.getParent()).renumberEntries();
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    @Override
    public void replaceLongConstant(ConstantLong constantLong, ConstantLong constantLong1) {
        for (int i = 0; i < this.arguments.length; i++) {
            if (this.arguments[i] == constantLong) {
                this.arguments[i] = constantLong1;
                break;
            }
        }
    }

    public boolean isInvokeStaticHandle() {
        return this.methodHandle.isInvokeStatic();
    }

    public ConstantPoolEntry[] copyArguments() {
        ConstantPoolEntry[] constantPoolEntrys = new ConstantPoolEntry[this.arguments.length];
        System.arraycopy(this.arguments, 0, constantPoolEntrys, 0, this.arguments.length);
        return constantPoolEntrys;
    }

    public void collectReferencedProgramClasses(Set set1, Set set2, Set set3, Set set4) {
        this.methodHandle.collectReferencedProgramClasses(set1, set2, set3, set4);

        for (ConstantPoolEntry constantPoolEntry : this.arguments) {
            switch (ConstantKindSwitchMap.TAG_SWITCH[constantPoolEntry.getTag().ordinal()]) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                default:
                    break;
                case 6:
                    String string = ((ResolvedClassConstant) constantPoolEntry).getClassName();
                    ProgramClass programClass1 = ConstantPoolEntry.findProgramClassByDescriptor(string);
                    if (programClass1 != null) {
                        programClass1 = this.selectMatchingProgramClass(programClass1);
                        if (!string.startsWith("[")) {
                            set2.add(programClass1);
                        } else {
                            set1.add(programClass1);
                        }
                    }
                    break;
                case 7:
                    ((ResolvedMethodHandleConstant) constantPoolEntry).collectReferencedProgramClasses(set1, set2, set3, set4);
                    break;
                case 8:
                    ((ResolvedMethodType) constantPoolEntry).collectReferencedProgramClasses(set2);
                    break;
                case 9:
                    ((ResolvedConstantDynamic) constantPoolEntry).collectReferencedProgramClasses(set1, set2, set3, set4);
            }
        }
    }

    public int getByteLength() {
        return 4 + this.arguments.length * 2;
    }

    @Override
    public void collectUsedConstants(char ba, int bb, UsedConstantsCollector usedConstantsCollector, char bc) {
        this.methodHandle.registerUsage(usedConstantsCollector, this, this.getParent());

        for (ConstantPoolEntry constantPoolEntry : this.arguments) {
            usedConstantsCollector.markUsed(constantPoolEntry, this, this.getParent());
        }
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Set getNestedBootstrapEntries() {
        HashSet hashSet = new HashSet(13);

        for (ConstantPoolEntry constantPoolEntry : this.arguments) {
            if (constantPoolEntry instanceof ResolvedConstantDynamic) {
                ResolvedConstantDynamic resolvedConstantDynamic = (ResolvedConstantDynamic) constantPoolEntry;
                hashSet.add(resolvedConstantDynamic.getBootstrapMethod());
            }
        }

        return hashSet;
    }

    public ResolvedMethodHandleConstant getMethodHandle() {
        return this.methodHandle;
    }

    @Override
    public void replaceStringConstant(ResolvedStringConstant resolvedStringConstant, ResolvedStringConstant resolvedStringConstant1) {
        for (int i = 0; i < this.arguments.length; i++) {
            if (this.arguments[i] == resolvedStringConstant) {
                this.arguments[i] = resolvedStringConstant1;
                break;
            }
        }
    }

    public int getIndex() {
        return this.index;
    }

    public void collectMethodHandleTargets(Set set1) {
        for (ConstantPoolEntry constantPoolEntry : this.arguments) {
            if (constantPoolEntry.getTag() == ConstantPoolTag.METHOD_HANDLE) {
                ((ResolvedMethodHandleConstant) constantPoolEntry).collectProgramMethods(set1);
            }
        }
    }

    @Override
    public void replaceClassConstant(ResolvedClassConstant resolvedClassConstant, ResolvedClassConstant resolvedClassConstant1) {
        for (int i = 0; i < this.arguments.length; i++) {
            if (this.arguments[i] == resolvedClassConstant) {
                this.arguments[i] = resolvedClassConstant1;
                break;
            }
        }
    }

    public void truncateArguments() {
        ConstantPoolEntry[] constantPoolEntrys = new ConstantPoolEntry[0 + 1];
        System.arraycopy(this.arguments, 0, constantPoolEntrys, 0, constantPoolEntrys.length);
        this.arguments = constantPoolEntrys;
    }

    public BootstrapMethodEntry(
            ClassFileComponent classFileComponent,
            ClassFileInputStream classFileInputStream,
            int ba,
            ListMultimap listMultimap,
            ListMultimap listMultimap1,
            ListMultimap listMultimap2,
            ListMultimap listMultimap3
    ) throws IOException {
        super(classFileComponent);
        this.setIndex(ba);
        int bb = classFileInputStream.readUnsignedShort();
        int bc = classFileInputStream.readUnsignedShort();
        ConstantPoolEntry constantPoolEntry = this.getConstantPoolEntry(bb);
        if (constantPoolEntry instanceof ResolvedMethodHandleConstant) {
            this.methodHandle = (ResolvedMethodHandleConstant) constantPoolEntry;
        } else {
            this.valid = false;
            this.errorMessage = "Invalid bootstrap method handle entry '" + ConstantPoolEntry.getTagName(constantPoolEntry.getTag()) + "'";
        }

        if (this.valid) {
            this.arguments = new ConstantPoolEntry[bc];

            for (int i = 0; i < bc; i++) {
                int be = classFileInputStream.readUnsignedShort();
                ConstantPoolEntry constantPoolEntry1 = this.getConstantPoolEntry(be);
                if (!(constantPoolEntry1 instanceof ResolvedClassConstant)
                        && !(constantPoolEntry1 instanceof LoadableConstant)
                        && !(constantPoolEntry1 instanceof ResolvedMethodHandleConstant)
                        && !(constantPoolEntry1 instanceof ResolvedMethodType)
                        && !(constantPoolEntry1 instanceof ResolvedConstantDynamic)) {
                    this.valid = false;
                    this.errorMessage = "Invalid bootstrap method argument entry '" + ConstantPoolEntry.getTagName(constantPoolEntry1.getTag()) + "'";
                    break;
                }

                if (constantPoolEntry1 instanceof ResolvedStringConstant) {
                    listMultimap.addValue((ResolvedStringConstant) constantPoolEntry1, this);
                } else if (constantPoolEntry1 instanceof ConstantInteger) {
                    listMultimap1.addValue((ConstantInteger) constantPoolEntry1, this);
                } else if (constantPoolEntry1 instanceof ConstantLong) {
                    listMultimap2.addValue((ConstantLong) constantPoolEntry1, this);
                } else if (constantPoolEntry1 instanceof ResolvedClassConstant) {
                    listMultimap3.addValue((ResolvedClassConstant) constantPoolEntry1, this);
                }

                this.arguments[i] = constantPoolEntry1;
            }
        }
    }

    public void updateObjectMethodsNames() throws ZkmException, IOException {
        if (this.valid && this.getBootstrapClassName().equals("java/lang/runtime/ObjectMethods") && this.arguments != null && this.arguments.length >= 2) {
            int ba = 1;
            ConstantPoolEntry constantPoolEntry = this.arguments[ba++];
            if (constantPoolEntry instanceof ResolvedStringConstant) {
                ResolvedStringConstant resolvedStringConstant = (ResolvedStringConstant) constantPoolEntry;
                String string = resolvedStringConstant.getEditableValue();
                StringBuilder stringBuilder = new StringBuilder();
                StringTokenizer stringTokenizer = new StringTokenizer(string, ";");
                int bb = stringTokenizer.countTokens();

                for (int i = 0; i < bb; i++) {
                    if (i > 0) {
                        stringBuilder.append(";");
                    }

                    String string1 = stringTokenizer.nextToken();
                    if (ba < this.arguments.length) {
                        ConstantPoolEntry constantPoolEntry1 = this.arguments[ba++];
                        if (constantPoolEntry1 instanceof ResolvedMethodHandleConstant) {
                            MemberInfo memberInfo1 = ((ResolvedMethodHandleConstant) constantPoolEntry1).getReferencedMember();
                            if (memberInfo1 != null) {
                                stringBuilder.append(memberInfo1.getSourceName());
                            } else {
                                stringBuilder.append(string1);
                            }
                        } else {
                            stringBuilder.append(string1);
                        }
                    } else {
                        stringBuilder.append(string1);
                    }
                }

                String string2 = stringBuilder.toString();
                if (!string2.equals(string)) {
                    resolvedStringConstant.setValueFromString(string2);
                }
            }
        }
    }

    public void collectReferencedClasses(Set set1, Set set2, Set set3, Set set4) {
        this.methodHandle.collectReferencedClasses(set1, set2, set3, set4);

        for (ConstantPoolEntry constantPoolEntry : this.arguments) {
            switch (ConstantKindSwitchMap.TAG_SWITCH[constantPoolEntry.getTag().ordinal()]) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                default:
                    break;
                case 6:
                    String string = ((ResolvedClassConstant) constantPoolEntry).getClassName();
                    ClassFileBase classFileBase = ConstantPoolEntry.lookupClassByDescriptor(string);
                    if (classFileBase != null) {
                        Set set5;
                        if (!string.startsWith("[")) {
                            set5 = set2;
                        } else {
                            set5 = set1;
                        }

                        if (classFileBase.isMultiRelease()) {
                            set5.addAll(classFileBase.getAllVersions());
                        } else {
                            set5.add(classFileBase);
                        }
                    }
                    break;
                case 7:
                    ((ResolvedMethodHandleConstant) constantPoolEntry).collectReferencedClasses(set1, set2, set3, set4);
                    break;
                case 8:
                    ((ResolvedMethodType) constantPoolEntry).collectReferencedClasses(set2);
            }
        }
    }

    public boolean matchesBootstrapMethod() {
        return this.methodHandle.getMemberClassName().equals("java/lang/invoke/StringConcatFactory")
                && this.methodHandle.getMemberName().equals("makeConcatWithConstants")
                && this.methodHandle
                .getMemberDescriptor()
                .equals(
                        "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite;"
                );
    }

    @Override
    public void replaceIntegerConstant(ConstantInteger constantInteger, ConstantInteger constantInteger1) {
        for (int i = 0; i < this.arguments.length; i++) {
            if (this.arguments[i] == constantInteger) {
                this.arguments[i] = constantInteger1;
                break;
            }
        }
    }

    public String describe() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('[');
        stringBuilder.append(this.methodHandle.getValueString());
        stringBuilder.append(" : ");
        stringBuilder.append('[');

        for (int i = 0; i < this.arguments.length; i++) {
            stringBuilder.append(this.arguments[i].getValueString());
            if (i < this.arguments.length - 1) {
                stringBuilder.append(',');
            }
        }

        stringBuilder.append(']');
        stringBuilder.append(']');
        return stringBuilder.toString();
    }
}
