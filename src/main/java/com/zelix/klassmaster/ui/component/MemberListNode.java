package com.zelix.klassmaster.ui.component;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.MemberInfo;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.IntegerCache;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

public class MemberListNode extends ListClassProperty {
    public int memberCount;
    public MemberInfo[] members;

    public boolean hasNextMember() {
        return super.cursor < this.memberCount;
    }

    public void updateMembers(MemberInfo[] memberInfos) throws ZkmException, IOException {
        this.initMembers(memberInfos);
        this.setChanged();
        this.notifyObservers();
    }

    public String nextMemberText() {
        return this.members[super.cursor++].getSourceName();
    }

    public int getMemberIndex(Object object) {
        if (super.indexMap == null) {
            super.indexMap = ZkmUtils.createHashMap((int) (this.memberCount * 1.5));
            int ba = 0;
            int bb = 0;

            for (int i = this.memberCount; bb < i; i = this.memberCount) {
                super.indexMap.put(this.members[ba], super.integerCache.valueOf(ba));
                bb = ++ba;
            }
        }

        Object object1 = super.indexMap.get(object);
        return object1 != null ? (Integer) object1 : -1;
    }

    public MemberListNode(String string, ClassFileComponent classFileComponent, MemberInfo[] memberInfos, IntegerCache integerCache1) {
        super(string, classFileComponent, integerCache1);
        this.initMembers(memberInfos);
    }

    public MemberInfo getMember(int ba) {
        return this.members[ba];
    }

    public void initMembers(MemberInfo[] memberInfos) {
        int ba = memberInfos.length;
        Vector vector = new Vector(ba);

        for (int i = 0; i < ba; i++) {
            if (memberInfos[i].isValid()) {
                vector.addElement(memberInfos[i]);
            }
        }

        this.memberCount = vector.size();
        this.members = new MemberInfo[this.memberCount];
        vector.copyInto(this.members);
        Arrays.sort(this.members);
        super.indexMap = null;
        this.resetCursor();
    }
}
