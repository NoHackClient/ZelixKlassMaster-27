package com.zelix.klassmaster.classfile.insn;

import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.constpool.ClassConstantReplaceable;
import com.zelix.klassmaster.classfile.constpool.ConstantInteger;
import com.zelix.klassmaster.classfile.constpool.ConstantIntegerReplacer;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolProvider;
import com.zelix.klassmaster.classfile.constpool.LoadableConstant;
import com.zelix.klassmaster.classfile.constpool.ResolvedClassConstant;
import com.zelix.klassmaster.classfile.constpool.ResolvedConstantDynamic;
import com.zelix.klassmaster.classfile.constpool.ResolvedMethodHandleConstant;
import com.zelix.klassmaster.classfile.constpool.ResolvedMethodType;
import com.zelix.klassmaster.classfile.constpool.ResolvedStringConstant;
import com.zelix.klassmaster.classfile.constpool.StringConstantReplaceable;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.MultiMapTable;
import com.zelix.klassmaster.util.RankedValue;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

public class LdcInstruction extends Instruction implements ConstantPoolOperand, StringConstantReplaceable, ClassConstantReplaceable, ConstantIntegerReplacer {
    private ConstantPoolEntry constant;

    @Override
    public final boolean consumesStackSlot(int ba, int bb) {
        return false;
    }

    @Override
    public ConstantPoolEntry getConstantPoolEntry() {
        return this.constant;
    }

    @Override
    public void replaceIntegerConstant(ConstantInteger constantInteger, ConstantInteger constantInteger1) {
        if (this.constant == constantInteger) {
            this.constant = constantInteger1;
        }
    }

    @Override
    public String toAssembly() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.getMnemonic());
        stringBuilder.append(' ');
        stringBuilder.append(this.constant.getAbbreviatedValue());
        return stringBuilder.toString();
    }

    @Override
    public void replaceClassConstant(ResolvedClassConstant resolvedClassConstant, ResolvedClassConstant resolvedClassConstant1) {
        if (this.constant == resolvedClassConstant) {
            this.constant = resolvedClassConstant1;
        }
    }

    public LdcInstruction(
            ClassFileInputStream classFileInputStream,
            ConstantPoolProvider constantPoolProvider,
            ListMultimap listMultimap,
            ListMultimap listMultimap1,
            ListMultimap listMultimap2
    ) throws IOException {
        super(18);
        int ba = classFileInputStream.read();
        this.constant = constantPoolProvider.getConstantPoolEntry(ba);
        if (this.constant instanceof ResolvedStringConstant) {
            listMultimap.addValue((ResolvedStringConstant) this.constant, this);
        } else if (this.constant instanceof ConstantInteger) {
            listMultimap1.addValue((ConstantInteger) this.constant, this);
        } else if (this.constant instanceof ResolvedClassConstant) {
            listMultimap2.addValue((ResolvedClassConstant) this.constant, this);
        }
    }

    @Override
    public StackFrameState computeFrameAfter(StackFrameState stackFrameState, Object object, Object object1, Object object2) throws ZkmException, IOException {
        VerifierType[] verifierTypes = stackFrameState.getStack();
        int ba = verifierTypes.length;
        VerifierType[] verifierTypes1 = VerifierType.createArray(ba + 1);
        VerifierType[] verifierTypes2 = stackFrameState.getLocals();
        System.arraycopy(verifierTypes, 0, verifierTypes1, 0, ba);
        if (this.constant instanceof LoadableConstant) {
            String string = ((LoadableConstant) this.constant).getTypeName();
            if (string.equals("integer")) {
                verifierTypes1[ba] = VerifierType.INT;
            } else if (string.equals("string")) {
                verifierTypes1[ba] = VerifierType.STRING;
            } else if (string.equals("float")) {
                verifierTypes1[ba] = VerifierType.FLOAT;
            }
        } else if (this.constant instanceof ResolvedClassConstant) {
            verifierTypes1[ba] = VerifierType.CLASS;
        } else if (this.constant instanceof ResolvedMethodType) {
            verifierTypes1 = VerifierType.createArray(ba + 1);
            System.arraycopy(verifierTypes, 0, verifierTypes1, 0, ba);
            verifierTypes1[ba] = VerifierType.forDescriptor("Ljava/lang/invoke/MethodType;");
        } else if (this.constant instanceof ResolvedMethodHandleConstant) {
            verifierTypes1 = VerifierType.createArray(ba + 1);
            System.arraycopy(verifierTypes, 0, verifierTypes1, 0, ba);
            verifierTypes1[ba] = VerifierType.forDescriptor("Ljava/lang/invoke/MethodHandle;");
        } else if (this.constant instanceof ResolvedConstantDynamic) {
            verifierTypes1 = VerifierType.createArray(ba + 1);
            System.arraycopy(verifierTypes, 0, verifierTypes1, 0, ba);
            ResolvedConstantDynamic resolvedConstantDynamic = (ResolvedConstantDynamic) this.constant;
            String string1 = resolvedConstantDynamic.getStackReturnType();
            verifierTypes1[ba] = VerifierType.forDescriptor(string1);
        }

        return new StackFrameState(verifierTypes1, verifierTypes2, stackFrameState.getSubroutineLocals(), stackFrameState.getHeldMonitors());
    }

    @Override
    public int getStackDelta() {
        return 1;
    }

    @Override
    public void printDisassembly(PrintWriter printWriter, StringBuilder stringBuilder) throws UnknownOpcodeException {
        StringBuilder stringBuilder1 = new StringBuilder(100);
        String string = this.getMnemonic();
        stringBuilder1.append(string + " " + this.constant.getAbbreviatedValue());
        String string1 = Instruction.getDescriptionComment(this.opcode);
        string1 = Instruction.formatDescription(string1, this.constant.getAbbreviatedValue());
        if (string1.length() > 0) {
            stringBuilder1.append("\t" + string1);
        }

        printWriter.println(stringBuilder.toString() + stringBuilder.toString() + stringBuilder1);
    }

    @Override
    public boolean isIntConstantLdc() {
        return this.constant instanceof ConstantInteger;
    }

    @Override
    public boolean pushesWithoutPopping() {
        return true;
    }

    @Override
    public final boolean canFallThrough() {
        return true;
    }

    @Override
    public void writeRemapped(DataOutputStream dataOutputStream, Map map1) throws IOException {
        super.writeTo(dataOutputStream);
        ConstantPoolEntry constantPoolEntry = (ConstantPoolEntry) map1.get(this.constant);
        if (constantPoolEntry != null) {
            dataOutputStream.writeByte(constantPoolEntry.getIndex());
        } else {
            dataOutputStream.writeByte(this.constant.getIndex());
        }
    }

    @Override
    public boolean pushesWideValue() {
        return false;
    }

    @Override
    public final boolean isExit() {
        return false;
    }

    @Override
    public void replaceStringConstant(ResolvedStringConstant resolvedStringConstant, ResolvedStringConstant resolvedStringConstant1) {
        if (this.constant == resolvedStringConstant) {
            this.constant = resolvedStringConstant1;
        }
    }

    @Override
    public int getLength() {
        return 2;
    }

    @Override
    public boolean collectStringConstant(MultiMapTable multiMapTable, Set set1, MethodInfo methodInfo1, int ba) {
        if (this.constant instanceof ResolvedStringConstant) {
            ResolvedStringConstant resolvedStringConstant = (ResolvedStringConstant) this.constant;
            if (resolvedStringConstant.getValueUtf8().getLength() >= 2 && resolvedStringConstant.isFromClassFile() && !set1.contains(resolvedStringConstant)) {
                multiMapTable.addEntry(methodInfo1.getOwnerProgramClass(), methodInfo1, resolvedStringConstant, new RankedValue(ba, this));
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean collectIntConstant(Object object, Object object1, Object object2, Object object3) {
        MultiMapTable multiMapTable = (MultiMapTable) object;
        MethodInfo methodInfo1 = (MethodInfo) object2;
        if (this.constant instanceof ConstantInteger) {
            ConstantInteger constantInteger = (ConstantInteger) this.constant;
            if (constantInteger.isInProgramPool() && !((Set) object1).contains(constantInteger)) {
                multiMapTable.addEntry(methodInfo1.getOwnerProgramClass(), methodInfo1, constantInteger, new RankedValue((Integer) object3, this));
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public final boolean requiresTypedValueAt(Object object, Object object1, Object object2) {
        return false;
    }

    @Override
    public Instruction adjustLdcWidth(Map map1) {
        ConstantPoolEntry constantPoolEntry1 = (ConstantPoolEntry) ZkmUtils.mapOrSelf(this.constant, map1);
        ConstantPoolEntry constantPoolEntry;
        if (constantPoolEntry1 != null) {
            constantPoolEntry = constantPoolEntry1;
        } else {
            constantPoolEntry = this.constant;
        }

        return constantPoolEntry.getIndex() > 255 ? new ConstantRefInstruction(19, this.constant) : null;
    }

    @Override
    public boolean pushesValue() {
        return true;
    }

    @Override
    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
        super.writeTo(dataOutputStream);
        dataOutputStream.writeByte(this.constant.getIndex());
    }

    public LdcInstruction(ConstantPoolEntry constantPoolEntry) {
        super(18);
        this.constant = constantPoolEntry;
    }

    @Override
    public boolean loadsConstantInteger() {
        return this.constant instanceof ConstantInteger;
    }

    @Override
    public boolean isStringConstantLoad() {
        return this.constant instanceof ResolvedStringConstant;
    }
}
