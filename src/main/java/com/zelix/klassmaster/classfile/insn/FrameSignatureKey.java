package com.zelix.klassmaster.classfile.insn;

public class FrameSignatureKey extends FrameStateKey {
    public FrameSignatureKey(StackFrameState stackFrameState) {
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
        VerifierType[] verifierTypes1 = stackFrameState.getLocals();
        int bd = verifierTypes1.length;

        for (int i = 0; i < bd; i++) {
            VerifierType verifierType1 = verifierTypes1[i];
            if (!verifierType1.isInitialized()) {
                super.hasUninitializedThis = true;
            }

            stringBuilder.append(verifierType1.toDisplayString());
            if (i < bd - 1) {
                stringBuilder.append("|");
            }
        }

        SubroutineLocalsBitSet subroutineLocalsBitSet = stackFrameState.getSubroutineLocals();
        if (subroutineLocalsBitSet != null) {
            stringBuilder.append("|||");
            stringBuilder.append(subroutineLocalsBitSet.getSubroutineEntry().hashCode());
        }

        super.keyString = stringBuilder.toString();
    }

    static {
        new FrameSignatureKey();
    }

    private FrameSignatureKey() {
        super.keyString = "";
        super.stackSlotCount = 0;
        super.hasUninitializedThis = true;
    }
}
