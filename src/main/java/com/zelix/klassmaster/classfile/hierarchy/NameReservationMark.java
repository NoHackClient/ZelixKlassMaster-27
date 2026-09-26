package com.zelix.klassmaster.classfile.hierarchy;

import com.zelix.klassmaster.classfile.MemberSignatureBase;

public class NameReservationMark {
    public final MemberSignatureBase reservedSignature;
    public final int kind;

    public NameReservationMark(int kind) {
        this.reservedSignature = null;
        this.kind = kind;
    }

    public NameReservationMark(MemberSignatureBase memberSignatureBase) {
        this.reservedSignature = memberSignatureBase;
        this.kind = 0;
    }
}
