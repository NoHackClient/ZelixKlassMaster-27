package com.zelix.klassmaster.changelog.parser.ast;

import com.zelix.klassmaster.changelog.ChangeLogNameHolder;
import com.zelix.klassmaster.changelog.TypeNameSetter;
import com.zelix.klassmaster.changelog.parser.ChangeLogSimpleNode;

public abstract class ChangeLogMethodSignatureNode extends ChangeLogSimpleNode implements TypeNameSetter, ChangeLogNameHolder {
    public String methodName;
    public String argumentTypes;

    @Override
    public final void setParsedName(Object object) {
        this.methodName = (String) object;
    }

    public ChangeLogMethodSignatureNode(int ba) {
        super(ba);
    }

    @Override
    public final void setTypeName(Object object) {
        this.argumentTypes = (String) object;
    }
}
