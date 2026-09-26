package com.zelix.klassmaster.obfuscator.reflection;

import com.zelix.klassmaster.classfile.constpool.ResolvedMethodRef;
import com.zelix.klassmaster.classfile.insn.ConstantRefInstruction;
import com.zelix.klassmaster.classfile.insn.Instruction;
import com.zelix.klassmaster.classfile.insn.StackFrameState;
import com.zelix.klassmaster.classfile.insn.TrackedValue;

public class InstructionReasonNote implements TrackedValue {
    public Instruction instruction;
    public String reason;
    private static final String QUOTE_CLOSE_SUFFIX = "'>";

    @Override
    public String getDisplayText() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("<");
        if (this.instruction != null) {
            switch (this.instruction.getOpcode()) {
                case 182:
                case 183:
                case 185:
                    ConstantRefInstruction constantRefInstruction = (ConstantRefInstruction) this.instruction;
                    ResolvedMethodRef resolvedMethodRef = (ResolvedMethodRef) constantRefInstruction.getConstantPoolEntry();
                    stringBuffer.append(
                            constantRefInstruction.getMnemonic() + " " + resolvedMethodRef.getReferencedClassName() + "." + resolvedMethodRef.getMemberName() + " "
                    );
                    break;
                case 184:
                default:
                    stringBuffer.append(this.instruction.getMnemonic() + " ");
            }
        }

        stringBuffer.append("'" + this.reason + QUOTE_CLOSE_SUFFIX);
        return stringBuffer.toString();
    }

    @Override
    public int getPendingInstructionIndex() {
        return -1;
    }

    public String getReason() {
        return this.reason;
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    @Override
    public int hashCode() {
        boolean strictMergeDisabled = StackFrameState.isStrictMergeDisabled();
        int ba = 0;
        boolean bl = strictMergeDisabled;
        String string;
        if (!bl) {
            if (this.instruction != null) {
                ba = this.instruction.hashCode();
            }

            string = this.reason;
        } else {
            string = this.reason;
        }

        if (string != null) {
            ba ^= this.reason.hashCode();
        }

        return ba;
    }

    @Override
    public boolean isKnown() {
        return false;
    }

    public InstructionReasonNote(Instruction instruction1, String string) {
        this.instruction = instruction1;
        this.reason = string;
    }

    @Override
    public boolean isPending() {
        return false;
    }

    @Override
    public String getCombinedValueKey() {
        return this.getStringValue();
    }

    @Override
    public String getNormalizedName() {
        return this.getStringValue();
    }

    @Override
    public String getStringValue() {
        return null;
    }

    @Override
    public boolean equals(Object object) {
        boolean strictMergeEnabled = StackFrameState.isStrictMergeEnabled();
        boolean bl2 = object instanceof InstructionReasonNote;
        if (strictMergeEnabled) {
            if (bl2) {
                boolean bl1;
                InstructionReasonNote instructionReasonNote1;
                label75:
                {
                    instructionReasonNote1 = (InstructionReasonNote) object;
                    InstructionReasonNote instructionReasonNote2 = this;
                    if (strictMergeEnabled) {
                        if (this.instruction != null) {
                            label63:
                            {
                                Instruction instruction1;
                                if (strictMergeEnabled) {
                                    if (instructionReasonNote1.instruction == null) {
                                        break label63;
                                    }

                                    instruction1 = this.instruction;
                                } else {
                                    instruction1 = instructionReasonNote1.instruction;
                                }

                                bl1 = instruction1.equals(instructionReasonNote1.instruction);
                                if (strictMergeEnabled) {
                                    break label75;
                                }
                            }

                            bl1 = false;
                            if (strictMergeEnabled) {
                                break label75;
                            }
                        }

                        instructionReasonNote2 = instructionReasonNote1;
                    }

                    bl1 = instructionReasonNote2.instruction == null;
                }

                bl2 = bl1;
                if (strictMergeEnabled) {
                    if (bl1) {
                        InstructionReasonNote instructionReasonNote3 = this;
                        if (strictMergeEnabled) {
                            if (this.reason != null) {
                                String string;
                                if (strictMergeEnabled) {
                                    if (instructionReasonNote1.reason == null) {
                                        return false;
                                    }

                                    string = this.reason;
                                } else {
                                    string = instructionReasonNote1.reason;
                                }

                                return string.equals(instructionReasonNote1.reason);
                            }

                            instructionReasonNote3 = instructionReasonNote1;
                        }

                        return instructionReasonNote3.reason == null;
                    }

                    bl2 = false;
                }

                return bl2;
            }

            bl2 = false;
        }

        return bl2;
    }
}
