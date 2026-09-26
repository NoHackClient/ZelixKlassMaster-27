package com.zelix.klassmaster.changelog.parser.ast;

import java.util.ArrayList;
import java.util.List;

public abstract class FlowObfuscationDataNode extends ChangeLogEntryNode {
    public String className;
    public List values = new ArrayList();

    public final void setClassName(String string) {
        this.className = string;
    }

    public FlowObfuscationDataNode(int ba) {
        super(ba);
    }

    @Override
    public final void beginEntry(Object object, Object object1) {
    }

    public final void addValue(Object object) {
        this.values.add(object);
    }
}
