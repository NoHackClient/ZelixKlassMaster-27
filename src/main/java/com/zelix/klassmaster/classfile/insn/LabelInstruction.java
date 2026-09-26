package com.zelix.klassmaster.classfile.insn;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class LabelInstruction extends Instruction {
    private static final String LABEL_PREFIX = "label_";
    private int offset = -1;
    private int instructionIndex = -1;
    private int usageFlags = 0;

    public int getInstructionIndex() {
        return this.instructionIndex;
    }

    @Override
    public final boolean consumesStackSlot(int ba, int bb) {
        return false;
    }

    public int getOffset() {
        return this.offset;
    }

    @Override
    public final boolean canFallThrough() {
        return true;
    }

    public LabelInstruction(int ba, int bb) {
        this(ba, false, bb);
    }

    @Override
    public StackFrameState computeFrameAfter(StackFrameState stackFrameState, Object object, Object object1, Object object2) throws ZkmException, IOException {
        return new StackFrameState(
                stackFrameState.getStack(), stackFrameState.getLocals(), stackFrameState.getSubroutineLocals(), stackFrameState.getHeldMonitors()
        );
    }

    @Override
    public String toAssembly() {
        return this.toString();
    }

    public boolean isVisible() {
        return this.hasUsageBits(16384);
    }

    @Override
    public boolean pushesWideValue() {
        return false;
    }

    @Override
    public String getMnemonic() {
        return this.getLabelName();
    }

    public LabelInstruction(int ba) {
        this(-1, false, ba);
    }

    public void addUsageBits(int ba) {
        this.usageFlags |= ba;
    }

    public void setInstructionIndex(int instructionIndex) {
        this.instructionIndex = instructionIndex;
    }

    private LabelInstruction(int offset, boolean bl, boolean bl1, int bb) {
        super(2147483646);
        this.addUsageBits(bb);
        this.offset = offset;
        if (bl) {
            this.addUsageBits(16384);
        }

        if (bl1) {
            this.addUsageBits(32768);
        }
    }

    public boolean isUnused() {
        return ZkmUtils.toUnsignedShort(this.usageFlags) == 0;
    }

    public boolean isMarker() {
        return (ZkmUtils.toUnsignedShort(this.usageFlags) & 8192) != 0;
    }

    public void addUsageFlag(InstructionUsageFlag instructionUsageFlag) {
        this.usageFlags = this.usageFlags | instructionUsageFlag.getBit();
    }

    @Override
    public final boolean requiresTypedValueAt(Object object, Object object1, Object object2) {
        return false;
    }

    @Override
    public int getLength() {
        return 0;
    }

    @Override
    public final boolean isExit() {
        return false;
    }

    @Override
    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
    }

    @Override
    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public void printDisassembly(PrintWriter printWriter, StringBuilder stringBuilder) throws UnknownOpcodeException {
        if (this.isVisible()) {
            printWriter.println(stringBuilder.toString() + this.getLabelName());
        }
    }

    public boolean hasUsageBits(int ba) {
        return (ZkmUtils.toUnsignedShort(this.usageFlags) & ba) != 0;
    }

    public void clearUsageFlag(InstructionUsageClearFlag instructionUsageClearFlag) {
        this.usageFlags = this.usageFlags & instructionUsageClearFlag.getMask();
    }

    public String getLabelName() {
        StringBuilder stringBuilder = new StringBuilder(10);
        stringBuilder.append(LABEL_PREFIX);
        stringBuilder.append(this.offset);
        if (this.isFixed()) {
            stringBuilder.append('*');
        }

        return stringBuilder.toString();
    }

    public int getUsageBits() {
        return ZkmUtils.toUnsignedShort(this.usageFlags);
    }

    public boolean isFixed() {
        return this.hasUsageBits(32768);
    }

    public LabelInstruction(int ba, boolean bl, int bb) {
        this(ba, bl, false, bb);
    }

    @Override
    public boolean isLabel() {
        return true;
    }

    public LabelInstruction() {
        this(0, true, true, 2);
    }

    @Override
    public boolean pushesWithoutPopping() {
        return false;
    }

    public LabelInstruction(boolean bl, int ba) {
        this(-1, true, ba);
    }

    public void setVisible() {
        this.addUsageBits(16384);
    }

    public boolean isOnlyJumpTarget() {
        return (ZkmUtils.toUnsignedShort(this.usageFlags) & 4095) == 1;
    }

    @Override
    public int getStackDelta() {
        return 0;
    }

    @Override
    public boolean pushesValue() {
        return false;
    }
}
