package com.zelix.klassmaster.obfuscator.parameters;

public class AddedParameter {
    public static final AddedParameter[] EMPTY_ARRAY = new AddedParameter[0];
    private final int index;
    public final char typeChar;

    public int getIndex() {
        return this.index;
    }

    public AddedParameter(int index, char typeChar) {
        this.index = index;
        this.typeChar = typeChar;
    }

    @Override
    public boolean equals(Object object) {
        boolean predicateFlagClear = OpaquePredicateBase.isPredicateFlagClear();
        int ba = ((object instanceof AddedParameter) ? 1 : 0);
        if (!predicateFlagClear) {
            if ((ba != 0)) {
                AddedParameter addedParameter1 = (AddedParameter) object;
                ba = this.index;
                AddedParameter addedParameter2 = addedParameter1;
                if (!predicateFlagClear) {
                    if (this.index != addedParameter1.index) {
                        return false;
                    }

                    ba = this.typeChar;
                    if (predicateFlagClear) {
                        return this.typeChar != 0;
                    }

                    addedParameter2 = addedParameter1;
                }

                if (ba == addedParameter2.typeChar) {
                    return true;
                }

                return false;
            }

            ba = 0;
        }

        return ba != 0;
    }

    @Override
    public int hashCode() {
        return this.index + this.typeChar;
    }

    public int getBitWidth() {
        switch (this.typeChar) {
            case 'B':
                return 8;
            case 'C':
            case 'S':
                return 16;
            case 'I':
                return 32;
            default:
                return 0;
        }
    }

    public char getTypeChar() {
        return this.typeChar;
    }
}
