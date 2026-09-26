package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.constpool.ConstantUtf8;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.config.IgnoreMissingReferencesSpec;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NestedAnnotationValue extends AnnotationElementValue {
    public AnnotationEntry annotation;
    private static final String INVALID_NESTED_MESSAGE = "Invalid nested annotation : ";

    @Override
    public int getByteLength() {
        return 1 + this.annotation.getByteLength();
    }

    @Override
    public void updateAfterMethodRename() {
        this.annotation.updateAfterMethodRename();
    }

    @Override
    public void updateAfterFieldRename() {
        this.annotation.updateAfterFieldRename();
    }

    @Override
    public boolean hasClassValue() {
        return false;
    }

    @Override
    public void write(int ba, DataOutputStream dataOutputStream, int bb, int bc) throws IOException {
        long bg = ((long) ba << 32 | (long) bb << 48 >>> 32 | (long) bc << 48 >>> 48) ^ 0L;
        int bd = (int) ((((long) ba << 32 | (long) bb << 48 >>> 32 | (long) bc << 48 >>> 48) ^ 0L) >>> 32);
        int be = (int) ((((long) ba << 32 | (long) bb << 48 >>> 32 | (long) bc << 48 >>> 48) ^ 0L) << 32 >>> 48);
        int bf = (int) (bg << 48 >>> 48);
        dataOutputStream.writeByte(this.getTag());
        this.annotation.write(bd, dataOutputStream, (char) be, (char) bf);
    }

    @Override
    public void resolveReferences(Object object, Object object1, Object object2) throws ZkmException, IOException {
        ClassResolver classResolver1 = (ClassResolver) object;
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object2;
        IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1 = (IgnoreMissingReferencesSpec) object1;
        this.annotation.resolveReferences(classResolver1, ignoreMissingReferencesSpec1, scriptEnvironment1);
    }

    @Override
    public void updateAfterClassRename(Object object, Object object1) {
        HashMap hashMap1 = (HashMap) object;
        HashMap hashMap = (HashMap) object1;
        this.annotation.updateAfterClassRename(hashMap1, hashMap);
    }

    public NestedAnnotationValue(ClassFileComponent classFileComponent, int ba, ClassFileInputStream classFileInputStream, ListMultimap listMultimap) throws IOException {
        super(classFileComponent, ba);
        this.annotation = AnnotationEntry.read(this, classFileInputStream, listMultimap);
        if (!this.annotation.isValid()) {
            this.setValid();
            StringBuilder stringBuilder = new StringBuilder().append(INVALID_NESTED_MESSAGE);
            AnnotationEntry annotationEntry = this.annotation;
            Integer integer1 = 9539;
            Integer integer = 761259100;
            this.setErrorMessage(stringBuilder.append(annotationEntry.getErrorMessage(0, integer, integer1)).toString());
        }
    }

    @Override
    public String getClassValueName() {
        return null;
    }

    @Override
    public void collectReferencedMembers(Object object, Object object1, Object object2, Object object3) {
        Set set1 = (Set) object;
        Set set3 = (Set) object1;
        Set set4 = (Set) object3;
        Set set2 = (Set) object2;
        this.annotation.collectReferencedMembers(set1, set3, set2, set4);
    }

    @Override
    public boolean isArrayOrClassValue() {
        return false;
    }

    @Override
    public void collectReferencedClasses(Object object) {
        Set set1 = (Set) object;
        if (this.isValid()) {
            this.annotation.collectReferencedClasses(set1);
        }
    }

    @Override
    public void collectUsedConstants(char ba, int bb, UsedConstantsCollector usedConstantsCollector, char bc) {
        this.annotation.collectUsedConstants('\u0000', 1103830477, usedConstantsCollector, '鞍');
    }

    @Override
    public String getValueTypeDescriptor() {
        return this.annotation.getTypeDescriptor();
    }

    @Override
    public void writeRemapped(DataOutputStream dataOutputStream, Object object, Object object1) throws IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object1;
        Map map1 = (Map) object;
        dataOutputStream.writeByte(this.getTag());
        this.annotation.writeRemapped(dataOutputStream, map1, scriptEnvironment1);
    }

    @Override
    public void replaceUtf8Constant(ConstantUtf8 constantUtf8, ConstantUtf8 constantUtf81) {
    }
}
