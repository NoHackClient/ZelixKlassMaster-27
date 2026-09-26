package com.zelix.klassmaster.classfile.insn;

public class CompactFrameStateKey extends FrameStateKey {
    static {
        new CompactFrameStateKey();
    }

    public CompactFrameStateKey(StackFrameState stackFrameState) {
        VerifierType[] verifierTypes = stackFrameState.getStack();
        StringBuilder stringBuilder = new StringBuilder();
        super.stackSlotCount = 0;
        int ba = verifierTypes.length;

        for (int i = 0; i < ba; i++) {
            VerifierType verifierType = verifierTypes[i];
            super.stackSlotCount = super.stackSlotCount + verifierType.getSlotSize();
            if (!verifierType.isInitialized()) {
                super.hasUninitializedThis = true;
            }

            stringBuilder.append(verifierType.toDisplayString());
            if (i < ba - 1) {
                stringBuilder.append("|");
            }
        }

        stringBuilder.append("||");
        boolean bl = false;
        VerifierType[] verifierTypes1 = stackFrameState.getLocals();
        int bc = verifierTypes1.length;

        for (int i = 0; i < bc; i++) {
            if (!verifierTypes1[i].isInitialized()) {
                if (!bl) {
                    stringBuilder.append("**");
                }

                bl = true;
                super.hasUninitializedThis = true;
            }
        }

        SubroutineLocalsBitSet subroutineLocalsBitSet = stackFrameState.getSubroutineLocals();
        if (subroutineLocalsBitSet != null) {
            stringBuilder.append("|||");
            stringBuilder.append(subroutineLocalsBitSet.getSubroutineEntry().hashCode());
        }

        super.keyString = stringBuilder.toString();
    }

    private CompactFrameStateKey() {
        super.keyString = "";
        super.stackSlotCount = 0;
        super.hasUninitializedThis = true;
    }
}
