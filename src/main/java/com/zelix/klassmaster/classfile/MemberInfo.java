package com.zelix.klassmaster.classfile;

import com.zelix.klassmaster.classfile.attribute.AnnotationEntry;
import com.zelix.klassmaster.classfile.attribute.AnnotationsAttribute;
import com.zelix.klassmaster.classfile.attribute.Attribute;
import com.zelix.klassmaster.classfile.attribute.DeprecatedAttribute;
import com.zelix.klassmaster.classfile.attribute.ParameterAnnotationsAttribute;
import com.zelix.klassmaster.classfile.attribute.ReferencingAttribute;
import com.zelix.klassmaster.classfile.attribute.SignatureAttribute;
import com.zelix.klassmaster.classfile.attribute.SignatureTypeReferences;
import com.zelix.klassmaster.classfile.attribute.SyntheticAttribute;
import com.zelix.klassmaster.classfile.attribute.UnknownAttribute;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ConstantUtf8;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.classfile.constpool.Utf8ConstantReplaceable;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyQuery;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.config.IgnoreMissingReferencesSpec;
import com.zelix.klassmaster.config.TrimOptions;
import com.zelix.klassmaster.exceptions.ClassFileFormatException;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.obfuscator.exclude.AbstractExclusionSpec;
import com.zelix.klassmaster.obfuscator.trim.TrimProcessor;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.EmptyEnumeration;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class MemberInfo extends ClassFileComponent implements Utf8ConstantReplaceable, Comparable {
    private int stateFlags;
    public AccessFlags accessFlags;
    public ConstantUtf8 nameConstant;
    public ConstantUtf8 descriptorConstant;
    public int attributeCount;
    public Attribute[] attributes;
    public final String originalName;
    public final String originalDescriptor;
    public int creationKind;

    public boolean isManufactured() {
        return this.creationKind != 0;
    }

    public String getSourceName() {
        return this.nameConstant.getValue();
    }

    public boolean isBridge() {
        if (this.getOwningClass().supportsJava5()) {
            return this.accessFlags.isBridge();
        }

        for (int i = 0; i < this.attributeCount; i++) {
            if (this.attributes[i].getAttributeName().equals("Bridge")) {
                return true;
            }
        }

        return false;
    }

    public void clearSynthetic() {
        ArrayList arrayList = new ArrayList(this.attributeCount);
        if (!this.isBridge()) {
            if (this.accessFlags.isSynthetic()) {
                this.setSynthetic();
            }

            for (int i = 0; i < this.attributeCount; i++) {
                if (!(this.attributes[i] instanceof SyntheticAttribute)) {
                    arrayList.add(this.attributes[i]);
                }
            }

            int bc = arrayList.size();
            if (bc < this.attributeCount) {
                this.attributes = new Attribute[bc];

                for (int i = 0; i < bc; i++) {
                    this.attributes[i] = (Attribute) arrayList.get(i);
                }

                this.attributeCount = bc;
                this.onAttributesChanged();
            }
        }
    }

    public final void makePublic() {
        this.accessFlags.makePublic();
    }

    public String getLowerCaseName() {
        return this.nameConstant.getValue().toLowerCase();
    }

    public final void collectSignatureTypeReferences(Set set1, Set set2, Set set3, Set set4) throws ZkmProcessingException {
        for (int i = 0; i < this.attributes.length; i++) {
            Attribute attribute = this.attributes[i];
            if (attribute instanceof SignatureAttribute) {
                ((SignatureAttribute) attribute).collectReferencedClasses(set2);
            } else if (attribute instanceof ReferencingAttribute) {
                ((ReferencingAttribute) attribute).collectReferencedMembers(set1, set2, set3, set4);
            } else {
                this.visitOtherAttribute();
            }
        }
    }

    public MemberInfo(ClassFileBase classFileBase, ConstantUtf8 constantUtf8, ConstantUtf8 constantUtf81, Attribute[] attributes1, int creationKind) {
        super(classFileBase);
        this.accessFlags = new AccessFlags(this);
        this.nameConstant = constantUtf8;
        this.descriptorConstant = constantUtf81;
        this.attributeCount = attributes1.length;
        this.attributes = attributes1;
        this.originalName = constantUtf8.getValue();
        this.originalDescriptor = constantUtf81.getValue();
        this.creationKind = creationKind;
    }

    public final boolean isSynchronized() {
        return this.accessFlags.isSynchronized();
    }

    public boolean isLibraryStub() {
        return this.creationKind == 3;
    }

    public final boolean isRenamed() {
        return !this.originalName.equals(this.nameConstant.getValue());
    }

    public void trimAttributes(TrimProcessor trimProcessor1, TrimOptions trimOptions1, ScriptEnvironment scriptEnvironment1, PrintWriter printWriter) throws ZkmException, IOException {
        ArrayList arrayList = new ArrayList();

        for (int i = 0; i < this.attributeCount; i++) {
            if (this.attributes[i] instanceof DeprecatedAttribute) {
                if (!trimOptions1.b) {
                    arrayList.add(this.attributes[i]);
                }
            } else if (this.attributes[i] instanceof ReferencingAttribute) {
                ReferencingAttribute referencingAttribute = (ReferencingAttribute) this.attributes[i];
                if (trimOptions1.d) {
                    if (trimProcessor1.hasAnnotationRetainedClasses() && !referencingAttribute.trimAnnotations(trimProcessor1, scriptEnvironment1, printWriter)) {
                        arrayList.add(this.attributes[i]);
                    }
                } else {
                    arrayList.add(this.attributes[i]);
                }
            } else if (this.attributes[i] instanceof UnknownAttribute) {
                if (trimOptions1.c) {
                    UnknownAttribute unknownAttribute = (UnknownAttribute) this.attributes[i];
                    printWriter.println(
                            "\tDeleting unknown attribute '"
                                    + unknownAttribute.getAttributeName()
                                    + "' in '"
                                    + this.toDisplayString()
                                    + "' in class "
                                    + AbstractExclusionSpec.formatClassWithModifiers(this.getOwningClass(), trimProcessor1)
                    );
                    if (scriptEnvironment1.isVerbose()) {
                        scriptEnvironment1.getLogWriter()
                                .println(
                                        "\tDeleting unknown attribute '"
                                                + unknownAttribute.getAttributeName()
                                                + "' in '"
                                                + this.toDisplayString()
                                                + "' in class "
                                                + AbstractExclusionSpec.formatClassWithModifiers(this.getOwningClass(), trimProcessor1)
                                );
                    }
                } else {
                    arrayList.add(this.attributes[i]);
                }
            } else {
                this.trimAttribute(trimProcessor1, trimOptions1, this.attributes[i], scriptEnvironment1, printWriter);
                arrayList.add(this.attributes[i]);
            }
        }

        if (arrayList.size() < this.attributeCount) {
            this.attributes = ((com.zelix.klassmaster.classfile.attribute.Attribute[]) (arrayList.toArray(new Attribute[arrayList.size()])));
            this.attributeCount = this.attributes.length;
            this.onAttributesChanged();
        }
    }

    public void resolveAttributeReferences(
            ClassResolver classResolver1, IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1, ScriptEnvironment scriptEnvironment1
    ) throws ZkmException, IOException {
        for (int i = 0; i < this.attributes.length; i++) {
            if (this.attributes[i] instanceof ReferencingAttribute) {
                ((ReferencingAttribute) this.attributes[i]).resolveReferences(classResolver1, ignoreMissingReferencesSpec1, scriptEnvironment1);
            }
        }
    }

    public final void makePackagePrivate() {
        this.accessFlags.makePackagePrivate();
    }

    public void collectAttributeReferences(Set set1) {
        for (int i = 0; i < this.attributeCount; i++) {
            if (this.attributes[i] instanceof ReferencingAttribute) {
                ((ReferencingAttribute) this.attributes[i]).collectReferencedClasses(set1);
            }
        }
    }

    public final void setStatic() {
        this.accessFlags.setStatic();
    }

    public MemberInfo(ClassFileComponent classFileComponent, ClassFileInputStream classFileInputStream, ListMultimap listMultimap, PrintWriter printWriter) throws ClassFileFormatException, IOException {
        super(classFileComponent);
        this.accessFlags = new AccessFlags(this, classFileInputStream);
        int ba = classFileInputStream.readUnsignedShort();
        int bb = classFileInputStream.readUnsignedShort();
        ConstantPoolEntry constantPoolEntry = this.getConstantPoolEntry(ba);
        ConstantPoolEntry constantPoolEntry1 = this.getConstantPoolEntry(bb);
        this.attributeCount = classFileInputStream.readUnsignedShort();
        this.setNameAndDescriptor(constantPoolEntry, constantPoolEntry1);
        if (listMultimap != null) {
            listMultimap.addValue(this.nameConstant, this);
            listMultimap.addValue(this.descriptorConstant, this);
        }

        this.originalName = this.nameConstant.getValue();
        this.originalDescriptor = this.descriptorConstant.getValue();
        this.creationKind = 0;
    }

    @Override
    public final void replaceUtf8Constant(ConstantUtf8 constantUtf8, ConstantUtf8 constantUtf81) {
        if (this.nameConstant == constantUtf8) {
            this.nameConstant = constantUtf81;
        } else if (this.descriptorConstant == constantUtf8) {
            this.descriptorConstant = constantUtf81;
        }
    }

    public abstract String toDisplayString();

    public final void remapDescriptor(HashMap hashMap) throws ZkmException, IOException {
        String string = this.getDescriptor();
        String string1 = ConstantPoolEntry.remapDescriptorClassNames(string, hashMap);
        if (!string1.equals(string)) {
            this.setDescriptor(string1);
        }
    }

    public boolean isFlowObfuscationMember() {
        return this.creationKind == 2;
    }

    public void setDescriptor(String string) throws ZkmException, IOException {
        this.descriptorConstant.setValue(string);
        this.setChanged();
        this.notifyObservers(new MutableInt(2), this, null);
    }

    public void setAccessFlags(int ba) throws ZkmException, IOException {
        this.accessFlags.setFlags(ba);
        this.setChanged();
        this.notifyObservers(new MutableInt(1), this, null);
    }

    public final int getAccessFlags() {
        return this.accessFlags.getFlags();
    }

    public final boolean isFinal() {
        return this.accessFlags.isFinal();
    }

    public abstract String toOriginalDisplayString();

    public String getDescriptor() {
        return this.descriptorConstant.getValue();
    }

    public final void write(DataOutputStream dataOutputStream, Map map1, ScriptEnvironment scriptEnvironment1) throws IOException {
        dataOutputStream.writeShort(this.accessFlags.getFlags());
        ConstantUtf8 constantUtf8 = (ConstantUtf8) map1.get(this.nameConstant);
        if (constantUtf8 != null) {
            dataOutputStream.writeShort(constantUtf8.getIndex());
        } else {
            dataOutputStream.writeShort(this.nameConstant.getIndex());
        }

        ConstantUtf8 constantUtf81 = (ConstantUtf8) map1.get(this.descriptorConstant);
        if (constantUtf81 != null) {
            dataOutputStream.writeShort(constantUtf81.getIndex());
        } else {
            dataOutputStream.writeShort(this.descriptorConstant.getIndex());
        }

        dataOutputStream.writeShort(this.attributeCount);

        for (int i = 0; i < this.attributeCount; i++) {
            if (this.attributes[i] instanceof UnknownAttribute && this.attributes[i].getLength() > 0) {
                UnknownAttribute unknownAttribute = (UnknownAttribute) this.attributes[i];
                if (unknownAttribute.isCommonAcrossClasses()) {
                    scriptEnvironment1.logWarning(
                            "Class members contain unknown attribute '"
                                    + unknownAttribute.getAttributeName()
                                    + "'. The integrity of this attribute may be affected by obfuscation."
                    );
                } else {
                    scriptEnvironment1.logWarning(
                            "Member '"
                                    + this.getSourceName()
                                    + "' in class '"
                                    + this.getDottedClassName()
                                    + "' contains unknown attribute '"
                                    + unknownAttribute.getAttributeName()
                                    + "'. The integrity of this attribute may be affected by obfuscation. Consider using the Trim function to delete it."
                    );
                }
            }

            Attribute attribute = this.attributes[i];
            Map map2 = map1;
            ScriptEnvironment scriptEnvironment2 = scriptEnvironment1;
            Map map3 = map2;
            DataOutputStream dataOutputStream1 = dataOutputStream;
            attribute.writeRemapped(dataOutputStream1, map3, scriptEnvironment2);
        }
    }

    public void onAttributesChanged() {
    }

    public final boolean isAbstract() {
        return this.accessFlags.isAbstract();
    }

    public final boolean isProtected() {
        return this.accessFlags.isProtected();
    }

    public final boolean hasAccessFlags(int ba) {
        return (this.accessFlags.getFlags() & ba) == ba;
    }

    public void updateAttributesAfterFieldRename() {
        for (int i = 0; i < this.attributes.length; i++) {
            if (this.attributes[i] instanceof ReferencingAttribute) {
                ((ReferencingAttribute) this.attributes[i]).applyFieldRenames();
            }
        }
    }

    public final boolean isValid() {
        return !this.hasStateFlag(1);
    }

    public final void remapClassNames(int ba, int bb, HashMap hashMap, HashMap hashMap1) throws ZkmException, IOException {
        String string = this.getDescriptor();
        String string1 = ConstantPoolEntry.remapDescriptorClassNames(string, hashMap1);
        if (!string1.equals(string)) {
            this.setDescriptor(string1);
        }

        for (int i = 0; i < this.attributes.length; i++) {
            Attribute attribute = this.attributes[i];
            HashMap hashMap3 = hashMap1;
            HashMap hashMap2 = hashMap;
            Integer integer = bb;
            attribute.remapClassNames(ba, integer, hashMap2, hashMap3);
        }
    }

    public final SignatureTypeReferences getSignatureTypeReferences() throws ZkmProcessingException {
        SignatureTypeReferences signatureTypeReferences = new SignatureTypeReferences();
        this.collectSignatureTypeReferences(
                signatureTypeReferences.getAnnotationClasses(),
                signatureTypeReferences.getReferencedClasses(),
                signatureTypeReferences.getReferencedFields(),
                signatureTypeReferences.getReferencedMethods()
        );
        return signatureTypeReferences;
    }

    public boolean isMethod() {
        return !this.isField();
    }

    public final void makeProtected() {
        this.accessFlags.makeProtected();
    }

    public void setNameAndDescriptor(ConstantPoolEntry constantPoolEntry, ConstantPoolEntry constantPoolEntry1) throws ClassFileFormatException {
        if (constantPoolEntry instanceof ConstantUtf8 && constantPoolEntry1 instanceof ConstantUtf8) {
            this.nameConstant = (ConstantUtf8) constantPoolEntry;
            this.descriptorConstant = (ConstantUtf8) constantPoolEntry1;
        } else {
            this.setValid(false);
            throw new ClassFileFormatException(this.getLocationName() + " : " + "Invalid Method or Field Descriptor" + " (A)" + "");
        }
    }

    public final String getOriginalDescriptor() {
        return this.originalDescriptor;
    }

    public final boolean isVolatile() {
        return this.accessFlags.isVolatile();
    }

    public MemberInfo(ClassFileComponent classFileComponent, ClassFileInputStream classFileInputStream, ListMultimap listMultimap) throws ClassFileFormatException, IOException {
        this(classFileComponent, classFileInputStream, listMultimap, (PrintWriter) null);
        this.creationKind = 0;
    }

    public final boolean isDescriptorChanged() {
        return !this.originalDescriptor.equals(this.descriptorConstant.getValue());
    }

    public final boolean isNative() {
        return this.accessFlags.isNative();
    }

    public final int getVisibilityRank() {
        return this.accessFlags.getVisibilityRank();
    }

    public abstract void trimAttribute(Object object, Object object1, Object object2, Object object3, Object object4) throws IOException;

    public void updateAttributesAfterMethodRename() {
        for (int i = 0; i < this.attributes.length; i++) {
            if (this.attributes[i] instanceof ReferencingAttribute) {
                ((ReferencingAttribute) this.attributes[i]).applyMethodRenames();
            }
        }
    }

    public boolean hasStateFlag(int ba) {
        return (this.stateFlags & ba) == ba;
    }

    public void setCreationKind(int creationKind) {
        this.creationKind = creationKind;
    }

    public final boolean isTransient() {
        return this.accessFlags.isTransient();
    }

    public final String getOriginalName() {
        return this.originalName;
    }

    @Override
    public void collectUsedConstants(char bc, int bd, UsedConstantsCollector usedConstantsCollector, char be) {
        this.accessFlags.collectUsedConstants('\u0000', 1103830477, usedConstantsCollector, '鞍');
        this.nameConstant.registerUsage(usedConstantsCollector, this, this.getParent());
        this.descriptorConstant.registerUsage(usedConstantsCollector, this, this.getParent());
        Attribute[] attributes1 = this.attributes;
        int ba = attributes1.length;

        for (int i = 0; i < ba; i++) {
            attributes1[i].collectUsedConstants('\u0000', 1103830477, usedConstantsCollector, '鞍');
        }
    }

    public final boolean isStrictlyPrivate() {
        return this.accessFlags.isPrivate() && !this.accessFlags.isProtected();
    }

    public String getOriginalMemberName() {
        return this.originalName;
    }

    public final boolean isSynthetic() {
        if (this.getOwningClass().supportsJava5()) {
            return this.accessFlags.isSynthetic();
        }

        for (int i = 0; i < this.attributeCount; i++) {
            if (this.attributes[i].getAttributeName().equals("Synthetic")) {
                return true;
            }
        }

        return false;
    }

    public final int compareByLowerCaseName(MemberInfo memberInfo2) {
        return this.getLowerCaseName().compareTo(memberInfo2.getLowerCaseName());
    }

    public Set getAnnotationTypes() {
        HashSet hashSet = ZkmUtils.createHashSet(13);

        for (int i = 0; i < this.attributeCount; i++) {
            if (this.attributes[i] instanceof AnnotationsAttribute) {
                String[] strings = ((AnnotationsAttribute) this.attributes[i]).getAnnotationTypeNames();

                for (int j = 0; j < strings.length; j++) {
                    hashSet.add(strings[j]);
                }
            }
        }

        return hashSet;
    }

    public Enumeration enumerateAnnotationTypes() {
        Set set1 = this.getAnnotationTypes();
        return set1.size() == 0 ? new EmptyEnumeration() : Collections.enumeration(set1);
    }

    @Override
    public int compareTo(Object object) {
        return this.compareByLowerCaseName((MemberInfo) object);
    }

    public void setName(String string) throws ZkmException, IOException {
        this.nameConstant.setValue(string);
        this.setChanged();
        this.notifyObservers(new MutableInt(0), this, null);
    }

    public Set collectAnnotationValues(String string, String string1, boolean bl) {
        HashSet hashSet = ZkmUtils.createHashSet(13);

        for (int i = 0; i < this.attributeCount; i++) {
            if (this.attributes[i] instanceof AnnotationsAttribute || this.attributes[i] instanceof ParameterAnnotationsAttribute) {
                ReferencingAttribute referencingAttribute = (ReferencingAttribute) this.attributes[i];
                AnnotationEntry annotationEntry;
                if (referencingAttribute instanceof AnnotationsAttribute) {
                    annotationEntry = ((AnnotationsAttribute) referencingAttribute).findAnnotation(string, bl);
                } else {
                    annotationEntry = ((ParameterAnnotationsAttribute) referencingAttribute).findAnnotationByType(string);
                }

                if (annotationEntry != null) {
                    String string2 = annotationEntry.getElementClassValue(string1, bl);
                    if (string2 != null) {
                        hashSet.add(string2);
                    }
                }
            }
        }

        return hashSet;
    }

    public final void setFinal() {
        this.accessFlags.setFinal(true);
    }

    public String getJvmName() {
        return this.getSourceName();
    }

    public abstract boolean isProgramMember();

    public void visitOtherAttribute() {
    }

    public final void setValid(boolean bl) {
        if (bl) {
            this.clearStateFlag(1);
        } else {
            this.setStateFlag(1);
        }
    }

    public abstract boolean isField();

    public String getModifierString() {
        return this.accessFlags.getModifierString();
    }

    public final void setSynthetic() {
        this.accessFlags.setSynthetic();
    }

    public final boolean isPublic() {
        return this.accessFlags.isPublic();
    }

    public final void makePrivate() {
        this.accessFlags.makePrivate();
    }

    public String getOwnerClassName() {
        return ((ClassFileBase) this.getParent()).getClassName();
    }

    public String formatAnnotations(ClassHierarchyQuery classHierarchyQuery) throws IOException {
        StringBuffer stringBuffer = new StringBuffer();
        Enumeration enumeration = this.enumerateAnnotationTypes();

        while (enumeration.hasMoreElements()) {
            stringBuffer.append('@');
            String string = (String) enumeration.nextElement();
            String string1 = classHierarchyQuery.getOriginalClassName(string);
            stringBuffer.append(ZkmUtils.slashesToDots(string1));
            stringBuffer.append(" ");
        }

        return stringBuffer.toString();
    }

    public void setStateFlag(int ba) {
        this.stateFlags |= ba;
    }

    public final boolean isPackagePrivate() {
        return this.accessFlags.isPackagePrivate();
    }

    public void clearStateFlag(int ba) {
        this.stateFlags &= ~ba;
    }

    public final boolean isStatic() {
        return this.accessFlags.isStatic();
    }
}
