package com.zelix.klassmaster.classfile;

public class FieldNameTypeSignature extends MemberSignatureBase {
    public final String returnDescriptor;
    private final int hash;

    public boolean hasReturnType() {
        return this.returnDescriptor != null;
    }

    @Override
    public int hashCode() {
        return this.hash;
    }

    public boolean matchesMethod(MethodSignature methodSignature1) {
        return this.equals(methodSignature1.getNameTypeSignature());
    }

    @Override
    public final String getReturnDescriptor() {
        return this.returnDescriptor;
    }

    public FieldNameTypeSignature(String string, String string1, String string2) {
        super(string, string1);
        if (string2 != null) {
            if (string2.length() == 0) {
                this.returnDescriptor = null;
            } else {
                this.returnDescriptor = string2.intern();
            }
        } else {
            this.returnDescriptor = null;
        }

        this.hash = (string + string1).hashCode();
    }

    public FieldNameTypeSignature(String string, String string1) {
        super(string, string1);
        String string2 = string1.substring(this.parameterDescriptor.length());
        if (string2.length() == 0) {
            this.returnDescriptor = null;
        } else {
            this.returnDescriptor = string2.intern();
        }

        this.hash = (string + this.parameterDescriptor).hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (object != null && object instanceof FieldNameTypeSignature) {
            FieldNameTypeSignature fieldNameTypeSignature1 = (FieldNameTypeSignature) object;
            return this.hash == fieldNameTypeSignature1.hash
                    && this.name == fieldNameTypeSignature1.name
                    && this.parameterDescriptor == fieldNameTypeSignature1.parameterDescriptor;
        } else {
            return false;
        }
    }
}
