package com.zelix.klassmaster.proguard.mapping.parser.ast;

import com.zelix.klassmaster.proguard.mapping.parser.ProGuardMappingSimpleNode;

public abstract class ProGuardMappingNameNode extends ProGuardMappingSimpleNode {
    public String name;

    public final void setName(String string) {
        this.name = string.trim();
    }

    public ProGuardMappingNameNode(int ba) {
        super(ba);
    }
}
