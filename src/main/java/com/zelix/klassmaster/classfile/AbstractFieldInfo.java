package com.zelix.klassmaster.classfile;

import com.zelix.klassmaster.classfile.attribute.Attribute;
import com.zelix.klassmaster.classfile.attribute.ConstantValueAttribute;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ConstantUtf8;
import com.zelix.klassmaster.classfile.insn.MemberRefKey;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ClassFileFormatException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

public abstract class AbstractFieldInfo extends MemberInfo {
    @Override
    public boolean isField() {
        return true;
    }

    public final MemberRefKey toMemberRefKey() {
        return new MemberRefKey(this.getOwnerClassName(), this.getSourceName(), this.getDescriptor());
    }

    public final boolean isEnum() {
        return this.accessFlags.isEnum();
    }

    public AbstractFieldInfo(
            ClassFileComponent classFileComponent, ClassFileInputStream classFileInputStream, ListMultimap listMultimap, PrintWriter printWriter
    ) throws ClassFileFormatException, IOException {
        super(classFileComponent, classFileInputStream, listMultimap, printWriter);
    }

    public final FieldSignature getSignature() {
        return new FieldSignature(this.getSourceName(), this.getDescriptor());
    }

    public final FieldSignature getOriginalSignature() {
        return new FieldSignature(this.getOriginalMemberName(), this.getOriginalDescriptor());
    }

    @Override
    public final String formatDeclaration() throws ZkmProcessingException, IOException {
        StringBuffer stringBuffer = new StringBuffer();
        Enumeration enumeration = this.enumerateAnnotationTypes();

        while (enumeration.hasMoreElements()) {
            stringBuffer.append("   ");
            stringBuffer.append('@');
            stringBuffer.append(ZkmUtils.slashesToDots((String) enumeration.nextElement()));
            stringBuffer.append(HiddenOptionFlags.LINE_SEPARATOR);
        }

        stringBuffer.append("   ");
        stringBuffer.append(this.getModifierString());
        stringBuffer.append(ConstantPoolEntry.descriptorToJavaType(this.getDescriptor()));
        stringBuffer.append(" ");
        stringBuffer.append(this.getSourceName());
        stringBuffer.append(HiddenOptionFlags.LINE_SEPARATOR);
        return stringBuffer.toString();
    }

    public AbstractFieldInfo(ClassFileBase classFileBase, ConstantUtf8 constantUtf8, ConstantUtf8 constantUtf81, Attribute[] attributes1, int ba) {
        super(classFileBase, constantUtf8, constantUtf81, attributes1, ba);

        for (int i = 0; i < attributes1.length; i++) {
            attributes1[i].setParent(this);
        }
    }

    public AbstractFieldInfo(ClassFileComponent classFileComponent, ClassFileInputStream classFileInputStream, ListMultimap listMultimap) throws ClassFileFormatException, IOException {
        super(classFileComponent, classFileInputStream, listMultimap);
    }

    @Override
    public String toDisplayString() {
        return ConstantPoolEntry.descriptorToJavaType(this.getDescriptor()) + " " + this.getSourceName();
    }

    public final String getTypeName() {
        return ConstantPoolEntry.descriptorToJavaType(this.getDescriptor());
    }

    public boolean isStringType() {
        return this.descriptorConstant.getValue().equals("Ljava/lang/String;");
    }

    public String getDescriptorKey() {
        return (this.isStatic() ? "static:" : ":") + this.getDescriptor();
    }

    public ConstantValueAttribute getConstantValueAttribute() {
        for (int i = 0; i < this.attributes.length; i++) {
            if (this.attributes[i] instanceof ConstantValueAttribute) {
                return (ConstantValueAttribute) this.attributes[i];
            }
        }

        return null;
    }

    public final void setEnum() {
        this.accessFlags.setEnum();
    }

    @Override
    public String toOriginalDisplayString() {
        return ConstantPoolEntry.descriptorToJavaType(this.getOriginalDescriptor()) + " " + this.getOriginalMemberName();
    }

    @Override
    public void trimAttribute(Object object, Object object1, Object object2, Object object3, Object object4) throws IOException {
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
    }
}
