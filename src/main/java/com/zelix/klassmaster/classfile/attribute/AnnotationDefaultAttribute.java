package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.config.IgnoreMissingReferencesSpec;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AnnotationDefaultAttribute extends AnnotationAttributeBase {
    private static final String INVALID_DEFAULT_VALUE_PREFIX = "Invalid default value : ";
    public AnnotationElementValue defaultValue;
    public String errorMessage;

    @Override
    public boolean trimAnnotations(Object object, Object object1, Object object2) {
        return false;
    }

    @Override
    public void write(DataOutputStream dataOutputStream) throws IOException {
        super.write(dataOutputStream);
        if (super.valid) {
            this.defaultValue.write(15571, dataOutputStream, 54846, 41447);
        } else {
            dataOutputStream.write(super.rawBytes);
        }
    }

    @Override
    public void remapClassNames(Object object2, Object object3, Object object, Object object1) throws ZkmProcessingException {
        HashMap hashMap1 = (HashMap) object;
        HashMap hashMap = (HashMap) object1;
        if (super.valid) {
            this.defaultValue.updateAfterClassRename(hashMap1, hashMap);
        }
    }

    @Override
    public void collectReferencedMembers(Set set1, Set set2, Set set3, Set set4) {
        if (super.valid) {
            this.defaultValue.collectReferencedMembers(set1, set2, set3, set4);
        }
    }

    @Override
    public void applyFieldRenames() {
        if (super.valid) {
            this.defaultValue.updateAfterFieldRename();
        }
    }

    @Override
    public void collectUsedConstants(char ba, int bb, UsedConstantsCollector usedConstantsCollector, char bc) {
        usedConstantsCollector.markUsed(this.nameConstant, this, this.getParent());
        if (super.valid) {
            this.defaultValue.collectUsedConstants('\u0000', 1103830477, usedConstantsCollector, '鞍');
        }
    }

    public AnnotationDefaultAttribute(
            ClassFileComponent classFileComponent, int ba, String string, ClassFileInputStream classFileInputStream, ListMultimap listMultimap
    ) throws IOException {
        super(classFileComponent, ba, string, classFileInputStream, listMultimap);
        byte[] bb = new byte[this.length];
        classFileInputStream.read(bb);
        ClassFileInputStream classFileInputStream1 = ClassFileInputStream.fromBytes(bb, false);

        try {
            this.defaultValue = AnnotationElementValue.readElementValue(this, classFileInputStream1, listMultimap);
            if (!this.defaultValue.isValid()) {
                super.valid = false;
                this.errorMessage = INVALID_DEFAULT_VALUE_PREFIX + this.defaultValue.getErrorMessage();
            }
        } finally {
            classFileInputStream1.close();
        }
    }

    @Override
    public void writeRemapped(DataOutputStream dataOutputStream, Object object, Object object1) throws IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object1;
        Map map1 = (Map) object;
        ScriptEnvironment scriptEnvironment2 = scriptEnvironment1;
        Map map2 = map1;
        ScriptEnvironment scriptEnvironment3 = scriptEnvironment2;
        Map map3 = map2;
        DataOutputStream dataOutputStream1 = dataOutputStream;
        super.writeRemapped(dataOutputStream1, map3, scriptEnvironment3);
        if (super.valid) {
            this.defaultValue.writeRemapped(dataOutputStream, map1, scriptEnvironment1);
        } else {
            dataOutputStream.write(super.rawBytes);
        }
    }

    @Override
    public void collectReferencedClasses(Set set1) {
        if (super.valid) {
            this.defaultValue.collectReferencedClasses(set1);
        }
    }

    @Override
    public void applyMethodRenames() {
        if (super.valid) {
            this.defaultValue.updateAfterMethodRename();
        }
    }

    @Override
    public void resolveReferences(ClassResolver classResolver1, IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1, ScriptEnvironment scriptEnvironment1) throws ZkmException, IOException {
        if (super.valid) {
            this.defaultValue.resolveReferences(classResolver1, ignoreMissingReferencesSpec1, scriptEnvironment1);
        }
    }
}
