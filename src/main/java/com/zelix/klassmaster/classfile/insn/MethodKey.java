package com.zelix.klassmaster.classfile.insn;

public class MethodKey {
    private final String className;
    private final String methodName;
    private final String descriptor;
    private final int cachedHash;

    public MethodKey(String string, String string1, String string2) {
        this.className = string;
        this.methodName = string1;
        this.descriptor = string2;
        this.cachedHash = string.hashCode() ^ string1.hashCode() ^ string2.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof MethodKey)) {
            return false;
        }

        MethodKey methodKey1 = (MethodKey) object;
        return this.cachedHash == methodKey1.cachedHash
                && this.className.equals(methodKey1.className)
                && this.methodName.equals(methodKey1.methodName)
                && this.descriptor.equals(methodKey1.descriptor);
    }

    @Override
    public int hashCode() {
        return this.cachedHash;
    }
}
