package com.zelix.klassmaster.proguard.mapping.parser.ast;

import com.zelix.klassmaster.proguard.mapping.parser.ProGuardMappingSimpleNode;

public abstract class ProGuardNameChangeNode extends ProGuardMappingSimpleNode {
    public String newName;
    public String oldName;

    public final void setNewName(String string) {
        this.newName = string;
    }

    public final String getNewName() {
        return this.newName;
    }

    public final void setOldName(String string) {
        this.oldName = string;
    }

    public ProGuardNameChangeNode(int ba) {
        super(ba);
    }

    public final String getOldName() {
        return this.oldName;
    }
}
