package com.zelix.klassmaster.classfile.constpool;

public enum MethodHandleRefKind {
    REF_GET_FIELD(1),
    REF_GET_STATIC(2),
    REF_PUT_FIELD(3),
    REF_PUT_STATIC(4),
    REF_INVOKE_VIRTUAL(5),
    REF_INVOKE_STATIC(6),
    REF_INVOKE_SPECIAL(7),
    REF_NEW_INVOKE_SPECIAL(8),
    REF_INVOKE_INTERFACE(9);

    public static final MethodHandleRefKind[] ALL_KINDS = new MethodHandleRefKind[]{
            REF_GET_FIELD,
            REF_GET_STATIC,
            REF_PUT_FIELD,
            REF_PUT_STATIC,
            REF_INVOKE_VIRTUAL,
            REF_INVOKE_STATIC,
            REF_INVOKE_SPECIAL,
            REF_NEW_INVOKE_SPECIAL,
            REF_INVOKE_INTERFACE
    };
    public final int kindValue;

    public static MethodHandleRefKind[] getAllKinds() {
        return ALL_KINDS.clone();
    }

    public int getKindValue() {
        return this.kindValue;
    }

    MethodHandleRefKind(int kindValue) {
        this.kindValue = kindValue;
    }

    public static boolean isValidTargetName(MethodHandleRefKind methodHandleRefKind, String string) {
        switch (MethodHandleKindSwitchMap.REF_KIND_SWITCH_TABLE[methodHandleRefKind.ordinal()]) {
            case 5:
            case 6:
            case 7:
            case 9:
                return !string.equals("<init>") && !string.equals("<clinit>");
            case 8:
                return string.equals("<init>");
            default:
                return false;
        }
    }

    public static MethodHandleRefKind fromKindValue(int ba) {
        switch (ba) {
            case 1:
                return REF_GET_FIELD;
            case 2:
                return REF_GET_STATIC;
            case 3:
                return REF_PUT_FIELD;
            case 4:
                return REF_PUT_STATIC;
            case 5:
                return REF_INVOKE_VIRTUAL;
            case 6:
                return REF_INVOKE_STATIC;
            case 7:
                return REF_INVOKE_SPECIAL;
            case 8:
                return REF_NEW_INVOKE_SPECIAL;
            case 9:
                return REF_INVOKE_INTERFACE;
            default:
                return null;
        }
    }
}
