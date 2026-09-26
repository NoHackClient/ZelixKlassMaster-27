package com.zelix.klassmaster.classfile.constpool;

import com.zelix.klassmaster.classfile.AbstractFieldInfo;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.FieldSignature;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.classfile.hierarchy.ClassMemberLookup;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.config.IgnoreMissingReferencesSpec;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ResolvedFieldRef extends ResolvedMemberRef {
    public static final ConstantPoolTag TAG = ConstantPoolTag.FIELDREF;

    public ResolvedFieldRef(
            ConstantMemberRef constantMemberRef, ResolvedClassConstant resolvedClassConstant, ResolvedNameAndType resolvedNameAndType, ListMultimap listMultimap
    ) {
        super(constantMemberRef, resolvedClassConstant, resolvedNameAndType, listMultimap);
    }

    @Override
    public void resolveMember(ClassMemberLookup classMemberLookup1, ClassResolver classResolver1, IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1) throws ZkmException, IOException {
        ObservableHolder observableHolder = new ObservableHolder();
        ObservableHolder observableHolder1 = new ObservableHolder();
        ObservableHolder observableHolder2 = new ObservableHolder();
        this.resolvedMember = this.lookupField(
                classMemberLookup1, classResolver1, observableHolder, observableHolder1, observableHolder2, ignoreMissingReferencesSpec1
        );
        ClassFileBase classFileBase = (ClassFileBase) observableHolder1.getValue();
        String string = (String) observableHolder.getValue();
        Set set1 = (Set) observableHolder2.getValue();
        if (set1 != null && this.resolvedMember != null) {
            Iterator iterator = set1.iterator();

            while (iterator.hasNext()) {
                ClassFileBase classFileBase1 = (ClassFileBase) iterator.next();
                if (classFileBase1.isProgramClass()) {
                    ((ProgramClass) classFileBase1).addInheritedFieldReferrer((AbstractFieldInfo) this.resolvedMember, classFileBase);
                }
            }
        }
    }

    public AbstractFieldInfo findFieldInClass(ClassFileBase classFileBase, FieldSignature fieldSignature, ClassMemberLookup classMemberLookup1) {
        return classFileBase.isProgramClass() && !classFileBase.isVersionedVariant()
                ? classMemberLookup1.findDeclaredField((ProgramClass) classFileBase, fieldSignature)
                : classFileBase.findField(fieldSignature);
    }

    public AbstractFieldInfo lookupField(
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1,
            ObservableHolder observableHolder,
            ObservableHolder observableHolder1,
            ObservableHolder observableHolder2,
            IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1
    ) throws ZkmException, IOException {
        AbstractFieldInfo abstractFieldInfo = null;
        FieldSignature fieldSignature = new FieldSignature(this.getMemberName(), this.getDescriptor());
        String string = this.getReferencedClassName();
        String string1 = string;
        ClassFileBase classFileBase = null;
        HashSet hashSet = null;
        if (string1.startsWith("[")) {
            string1 = "java/lang/Object";
        }

        String string2 = "looking for field '"
                + fieldSignature.formatDeclaration((Map) null)
                + "' in class '"
                + ZkmUtils.slashesToDots(string)
                + "' which is referenced in class '"
                + this.getOwnerFilePath()
                + "'";
        ClassFileBase classFileBase1 = this.constantPool.getClassFile();
        Integer integer = classFileBase1.hasReleaseVersion() ? classFileBase1.getReleaseVersion() : null;
        ClassFileBase classFileBase2 = classResolver1.getVersionedClass(string1, integer, string2, ignoreMissingReferencesSpec1);

        while (abstractFieldInfo == null && classFileBase2 != null) {
            abstractFieldInfo = this.findFieldInClass(classFileBase2, fieldSignature, classMemberLookup1);
            if (classFileBase == null && abstractFieldInfo == null) {
                classFileBase = classFileBase2;
                hashSet = ZkmUtils.createHashSet();
            }

            if (abstractFieldInfo != null) {
                break;
            }

            hashSet.add(classFileBase2);
            abstractFieldInfo = this.findFieldInSuperInterfaces(
                    fieldSignature, classFileBase2, integer, hashSet, classMemberLookup1, classResolver1, string2, ignoreMissingReferencesSpec1
            );
            if (abstractFieldInfo != null) {
                break;
            }

            string1 = classFileBase2.getSuperclassName();
            if (string1 == null) {
                break;
            }

            Integer integer1 = classFileBase2.hasReleaseVersion() ? classFileBase2.getReleaseVersion() : integer;
            classFileBase2 = classResolver1.getVersionedClass(string1, integer1, string2, ignoreMissingReferencesSpec1);
        }

        observableHolder.setValue(string1);
        observableHolder1.setValue(classFileBase);
        observableHolder2.setValue(hashSet);
        return abstractFieldInfo;
    }

    public String getStackFieldType() {
        String string = this.nameAndType.getDescriptor();
        return !string.equals("B") && !string.equals("C") && !string.equals("S") && !string.equals("Z") ? string : "I";
    }

    @Override
    public ConstantPoolTag getTag() {
        return TAG;
    }

    public ProgramClass getFieldTypeProgramClass() {
        String string = ClassFileBase.extractClassName(this.getDescriptor());
        return string != null ? ClassHierarchyNode.findProgramClass(string) : null;
    }

    public ResolvedFieldRef(
            AbstractConstantPool abstractConstantPool,
            ResolvedClassConstant resolvedClassConstant,
            ResolvedNameAndType resolvedNameAndType,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1,
            boolean bl
    ) throws ZkmException, IOException {
        super(abstractConstantPool, resolvedClassConstant, resolvedNameAndType, classMemberLookup1, classResolver1, bl);
    }

    public ResolvedFieldRef(
            AbstractConstantPool abstractConstantPool,
            ResolvedClassConstant resolvedClassConstant,
            ResolvedNameAndType resolvedNameAndType,
            AbstractFieldInfo abstractFieldInfo
    ) {
        super(abstractConstantPool, resolvedClassConstant, resolvedNameAndType, abstractFieldInfo);
    }

    public final AbstractFieldInfo findFieldInSuperInterfaces(
            FieldSignature fieldSignature,
            ClassFileBase classFileBase,
            Integer integer,
            HashSet hashSet,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1,
            String string,
            IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1
    ) throws ZkmException, IOException {
        String[] strings = classFileBase.getInterfaceNames();
        Integer integer1 = classFileBase.hasReleaseVersion() ? classFileBase.getReleaseVersion() : integer;

        for (int i = 0; i < strings.length; i++) {
            ClassFileBase classFileBase1 = classResolver1.getVersionedClass(strings[i], integer1, string, ignoreMissingReferencesSpec1);
            if (classFileBase1 != null) {
                AbstractFieldInfo abstractFieldInfo = this.findFieldInClass(classFileBase1, fieldSignature, classMemberLookup1);
                if (abstractFieldInfo != null) {
                    return abstractFieldInfo;
                }

                if (hashSet.add(classFileBase1)) {
                    abstractFieldInfo = this.findFieldInSuperInterfaces(
                            fieldSignature, classFileBase1, integer, hashSet, classMemberLookup1, classResolver1, string, ignoreMissingReferencesSpec1
                    );
                    if (abstractFieldInfo != null) {
                        return abstractFieldInfo;
                    }
                }
            }
        }

        return null;
    }
}
