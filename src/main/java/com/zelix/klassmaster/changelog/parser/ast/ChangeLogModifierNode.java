package com.zelix.klassmaster.changelog.parser.ast;

import com.zelix.klassmaster.changelog.parser.ChangeLogSimpleNode;
import com.zelix.klassmaster.classfile.AccessFlags;

public class ChangeLogModifierNode extends ChangeLogSimpleNode {
    public boolean manufactured;
    public int modifiers;

    public boolean isManufactured() {
        return this.manufactured;
    }

    public void setManufactured() {
        this.manufactured = true;
    }

    public int getModifiers() {
        return this.modifiers;
    }

    public void addStaticModifier() {
        this.modifiers = AccessFlags.addStatic(this.modifiers);
    }

    public ChangeLogModifierNode(int ba) {
        super(ba);
    }

    public void addPublicModifier() {
        this.modifiers = AccessFlags.makePublic(this.modifiers);
    }

    public void addProtectedModifier() {
        this.modifiers = AccessFlags.makeProtected(this.modifiers);
    }

    public void addPrivateModifier() {
        this.modifiers = AccessFlags.makePrivate(this.modifiers);
    }
}
