package com.zelix.klassmaster.classfile;

import com.zelix.klassmaster.util.VisitableNode;

public class ClassMemberRef {
    private final MemberInfo member;
    private final ClassFileBase ownerClass;
    private final int hash;

    public String getOwnerLocationName() {
        return this.member.getOwningClass().getDisplayLocationName();
    }

    public String getDescriptor() {
        return this.member.getDescriptor();
    }

    public String getName() {
        return this.member.getSourceName();
    }

    public ClassMemberRef(MemberInfo memberInfo1, ClassFileBase classFileBase) {
        this.member = memberInfo1;
        this.ownerClass = classFileBase;
        this.hash = memberInfo1.hashCode() ^ classFileBase.hashCode();
    }

    public boolean isProgramMember() {
        return this.member.isProgramMember();
    }

    public MemberInfo getMember() {
        return this.member;
    }

    public String getPackagePath() {
        return this.member.getOwningClass().getPackagePath();
    }

    @Override
    public boolean equals(Object object) {
        VisitableNode[] visitableNodes1 = ClassFileComponent.getFlowGuardNodes();
        boolean bl = object instanceof ClassMemberRef;
        if (visitableNodes1 != null) {
            if (bl) {
                ClassMemberRef classMemberRef1 = (ClassMemberRef) object;
                ClassMemberRef classMemberRef2 = this;
                if (visitableNodes1 != null) {
                    if (this.member != classMemberRef1.member) {
                        return false;
                    }

                    classMemberRef2 = this;
                }

                if (classMemberRef2.ownerClass == classMemberRef1.ownerClass) {
                    return true;
                }

                return false;
            }

            bl = false;
        }

        return bl;
    }

    @Override
    public int hashCode() {
        return this.hash;
    }

    public boolean isField() {
        return this.member.isField();
    }

    public ClassFileBase getOwnerClass() {
        return this.ownerClass;
    }
}
