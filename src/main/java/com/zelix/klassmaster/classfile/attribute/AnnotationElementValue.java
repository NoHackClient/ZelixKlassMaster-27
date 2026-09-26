package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.constpool.Utf8ConstantReplaceable;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.IOException;

public abstract class AnnotationElementValue extends ClassFileComponent implements AnnotationElementValueType, Utf8ConstantReplaceable {
    public String errorMessage;
    public boolean valid = true;
    public int tag;

    public abstract void resolveReferences(Object object, Object object1, Object object2) throws ZkmException, IOException;

    @Override
    public final boolean isValid() {
        return this.valid;
    }

    public final void setValid() {
        this.valid = false;
    }

    public abstract String getClassValueName();

    public abstract String getValueTypeDescriptor();

    public final String getElementName() {
        ClassFileComponent classFileComponent = this.getParent();

        while (classFileComponent != null && !(classFileComponent instanceof AnnotationElementPair) && !(classFileComponent instanceof MethodInfo)) {
            classFileComponent = classFileComponent.getParent();
        }

        String string = null;
        if (classFileComponent == null) {
        }

        if (classFileComponent instanceof AnnotationElementPair) {
            string = ((AnnotationElementPair) classFileComponent).getElementName();
        } else if (classFileComponent instanceof MethodInfo) {
            string = ((MethodInfo) classFileComponent).getSourceName();
        }

        return string;
    }

    public abstract void collectReferencedMembers(Object object, Object object1, Object object2, Object object3);

    public final String getErrorMessage() {
        return this.errorMessage;
    }

    public final void setErrorMessage(String string) {
        this.errorMessage = string;
    }

    public static AnnotationElementValue readElementValue(
            ClassFileComponent classFileComponent, ClassFileInputStream classFileInputStream, ListMultimap listMultimap
    ) throws IOException {
        int ba = classFileInputStream.readUnsignedByte();
        switch (ba) {
            case 64:
                return new NestedAnnotationValue(classFileComponent, ba, classFileInputStream, listMultimap);
            case 65:
            case 69:
            case 71:
            case 72:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 80:
            case 81:
            case 82:
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 92:
            case 93:
            case 94:
            case 95:
            case 96:
            case 97:
            case 98:
            case 100:
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
            case 110:
            case 111:
            case 112:
            case 113:
            case 114:
            default:
                return null;
            case 66:
            case 67:
            case 73:
            case 83:
            case 90:
                return new IntElementValue(classFileComponent, ba, classFileInputStream);
            case 68:
                return new DoubleElementValue(classFileComponent, ba, classFileInputStream);
            case 70:
                return new FloatElementValue(classFileComponent, ba, classFileInputStream);
            case 74:
                return new LongElementValue(classFileComponent, ba, classFileInputStream);
            case 91:
                return new NestedAnnotationElementValue(classFileComponent, ba, classFileInputStream, listMultimap);
            case 99:
                return new ClassElementValue(classFileComponent, ba, classFileInputStream, listMultimap);
            case 101:
                return new EnumElementValue(classFileComponent, ba, classFileInputStream, listMultimap);
            case 115:
                return new StringElementValue(classFileComponent, ba, classFileInputStream, listMultimap);
        }
    }

    public final String getAnnotationTypeName() {
        ClassFileComponent classFileComponent = this.getParent();

        while (classFileComponent != null && !(classFileComponent instanceof AnnotationEntry) && !(classFileComponent instanceof ProgramClass)) {
            classFileComponent = classFileComponent.getParent();
        }

        String string = null;
        if (classFileComponent == null) {
        }

        if (classFileComponent instanceof AnnotationEntry) {
            string = ((AnnotationEntry) classFileComponent).getTypeName();
        } else if (classFileComponent instanceof ProgramClass) {
            string = ((ProgramClass) classFileComponent).getOriginalClassName();
        }

        return string;
    }

    public abstract boolean isArrayOrClassValue();

    public final int getTag() {
        return this.tag;
    }

    public abstract boolean hasClassValue();

    public AnnotationElementValue(ClassFileComponent classFileComponent, int tag) {
        super(classFileComponent);
        this.tag = tag;
    }
}
