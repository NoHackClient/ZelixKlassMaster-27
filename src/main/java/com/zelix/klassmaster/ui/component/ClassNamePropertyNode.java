package com.zelix.klassmaster.ui.component;

import com.zelix.klassmaster.classfile.ClassFileComponent;

public class ClassNamePropertyNode extends ClassPropertyNode {
    public ClassNamePropertyNode(ClassFileComponent classFileComponent) {
        super("name", classFileComponent);
    }
}
