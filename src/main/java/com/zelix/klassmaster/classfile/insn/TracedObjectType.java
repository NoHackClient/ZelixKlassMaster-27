package com.zelix.klassmaster.classfile.insn;

public class TracedObjectType implements TrackedValue {
    private static final String OBJECT_CLASS_NAME = "java/lang/Object";
    public Instruction sourceInstruction;
    public String className;

    public TracedObjectType(Instruction instruction1, String string) {
        this.sourceInstruction = instruction1;
        if (string.startsWith("[")) {
            this.className = OBJECT_CLASS_NAME;
        } else {
            this.className = string;
        }
    }

    @Override
    public String getNormalizedName() {
        return this.getStringValue();
    }

    @Override
    public int hashCode() {
        boolean strictMergeDisabled = StackFrameState.isStrictMergeDisabled();
        int ba = 0;
        boolean bl = strictMergeDisabled;
        String string;
        if (!bl) {
            if (this.sourceInstruction != null) {
                ba = this.sourceInstruction.hashCode();
            }

            string = this.className;
        } else {
            string = this.className;
        }

        if (string != null) {
            ba ^= this.className.hashCode();
        }

        return ba;
    }

    @Override
    public boolean isPending() {
        return false;
    }

    @Override
    public boolean equals(Object object) {
        boolean strictMergeEnabled = StackFrameState.isStrictMergeEnabled();
        boolean bl2 = object instanceof TracedObjectType;
        if (strictMergeEnabled) {
            if (bl2) {
                boolean bl1;
                TracedObjectType tracedObjectType1;
                label75:
                {
                    tracedObjectType1 = (TracedObjectType) object;
                    TracedObjectType tracedObjectType2 = this;
                    if (strictMergeEnabled) {
                        if (this.sourceInstruction != null) {
                            label63:
                            {
                                Instruction instruction1;
                                if (strictMergeEnabled) {
                                    if (tracedObjectType1.sourceInstruction == null) {
                                        break label63;
                                    }

                                    instruction1 = this.sourceInstruction;
                                } else {
                                    instruction1 = tracedObjectType1.sourceInstruction;
                                }

                                bl1 = instruction1.equals(tracedObjectType1.sourceInstruction);
                                if (strictMergeEnabled) {
                                    break label75;
                                }
                            }

                            bl1 = false;
                            if (strictMergeEnabled) {
                                break label75;
                            }
                        }

                        tracedObjectType2 = tracedObjectType1;
                    }

                    bl1 = tracedObjectType2.sourceInstruction == null;
                }

                bl2 = bl1;
                if (strictMergeEnabled) {
                    if (bl1) {
                        TracedObjectType tracedObjectType3 = this;
                        if (strictMergeEnabled) {
                            if (this.className != null) {
                                String string;
                                if (strictMergeEnabled) {
                                    if (tracedObjectType1.className == null) {
                                        return false;
                                    }

                                    string = this.className;
                                } else {
                                    string = tracedObjectType1.className;
                                }

                                return string.equals(tracedObjectType1.className);
                            }

                            tracedObjectType3 = tracedObjectType1;
                        }

                        return tracedObjectType3.className == null;
                    }

                    bl2 = false;
                }

                return bl2;
            }

            bl2 = false;
        }

        return bl2;
    }

    @Override
    public String getStringValue() {
        return this.className;
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public int getPendingInstructionIndex() {
        return -1;
    }

    @Override
    public String getDisplayText() {
        return this.className;
    }

    @Override
    public boolean isKnown() {
        return true;
    }

    @Override
    public String getCombinedValueKey() {
        return this.getStringValue();
    }
}
