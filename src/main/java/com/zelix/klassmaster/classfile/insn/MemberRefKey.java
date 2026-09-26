package com.zelix.klassmaster.classfile.insn;

public class MemberRefKey {
    public final String className;
    public final String memberName;
    public final String descriptor;
    private final int hash;

    @Override
    public int hashCode() {
        return this.hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof MemberRefKey)) {
            return false;
        }

        MemberRefKey memberRefKey1 = (MemberRefKey) object;
        return this.hash == memberRefKey1.hash
                && this.className.equals(memberRefKey1.className)
                && this.memberName.equals(memberRefKey1.memberName)
                && this.descriptor.equals(memberRefKey1.descriptor);
    }

    public MemberRefKey(String string, String string1, String string2) {
        this.className = string;
        this.memberName = string1;
        this.descriptor = string2;
        this.hash = string.hashCode() ^ string1.hashCode() ^ string2.hashCode();
    }
}
