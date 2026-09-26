package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.MemberInfo;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.IOException;

public abstract class AnnotationAttributeBase extends ParsedAttributeBase implements ReferencingAttribute {
    @Override
    public String getOwnerName() {
        ClassFileComponent classFileComponent = this.getParent();
        if (classFileComponent instanceof ClassFileBase) {
            return ((ClassFileBase) classFileComponent).getLocationName();
        } else if (classFileComponent instanceof MemberInfo) {
            return ((MemberInfo) classFileComponent).toDisplayString();
        } else if (classFileComponent instanceof CodeAttributeBody) {
            return ((MethodInfo) ((CodeAttributeBody) classFileComponent).getParent()).toDisplayString();
        } else {
            return classFileComponent instanceof RecordComponent ? ((RecordAttribute) ((RecordComponent) classFileComponent).getParent()).getLocationName() : "";
        }
    }

    public AnnotationAttributeBase(
            ClassFileComponent classFileComponent, int ba, String string, ClassFileInputStream classFileInputStream, ListMultimap listMultimap
    ) throws IOException {
        super(classFileComponent, ba, string, classFileInputStream, listMultimap);
    }

    public boolean isClassLevel() {
        return this.getParent() instanceof ClassFileBase;
    }
}
