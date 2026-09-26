package com.zelix.klassmaster.classfile.constpool;

import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ResolvedClassConstant extends ClassConstantBase implements ConstantReferenceVisitable, Utf8ConstantReplaceable {
    public static final ConstantPoolTag TAG = ConstantPoolTag.CLASS;
    public ConstantUtf8 nameUtf8;

    @Override
    public boolean registerUsage(UsedConstantsCollector usedConstantsCollector, Object object, Object object1) {
        return usedConstantsCollector.markUsed(this, object, object1);
    }

    @Override
    public void setClassName(String string) {
        this.nameUtf8.setValue(string);
    }

    public String getTypeDescriptor() {
        return toTypeDescriptor(this.nameUtf8.getValue());
    }

    public ClassFileBase lookupClass() {
        String string = ClassFileBase.descriptorToClassName(this.getClassName());
        return string != null ? ClassHierarchyNode.findClassFile(string) : null;
    }

    @Override
    public String getValueString() {
        return this.getDottedClassName();
    }

    @Override
    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(TAG.getTagValue());
        dataOutputStream.writeShort(this.nameUtf8.getIndex());
    }

    public ResolvedClassConstant(AbstractConstantPool abstractConstantPool, ConstantUtf8 constantUtf8) {
        this(0, abstractConstantPool, constantUtf8, null);
    }

    @Override
    public void replaceUtf8Constant(ConstantUtf8 constantUtf8, ConstantUtf8 constantUtf81) {
        if (this.nameUtf8 == constantUtf8) {
            this.nameUtf8 = constantUtf81;
        }
    }

    public ResolvedClassConstant(ConstantClass constantClass, ConstantUtf8 constantUtf8, ListMultimap listMultimap) {
        super(constantClass.index, constantClass.constantPool);
        this.nameUtf8 = constantUtf8;
        listMultimap.addValue(constantUtf8, this);
    }

    @Override
    public String getClassName() {
        String string = this.nameUtf8.getValue();
        if (string.indexOf(46) != -1) {
            string = string.replace('.', '/');
        }

        return string;
    }

    private static String toTypeDescriptor(String string) {
        if (string.endsWith(";")) {
            return string;
        }

        int ba = 0;

        while (string.charAt(ba) == '[') {
            ba++;
        }

        String string1 = string.substring(0, ba);
        if (ba == string.length() - 1) {
            if (ba > 0) {
                switch (string.charAt(ba)) {
                    case 'B':
                    case 'C':
                    case 'D':
                    case 'F':
                    case 'I':
                    case 'J':
                    case 'S':
                    case 'Z':
                        return string;
                    case 'E':
                    case 'G':
                    case 'H':
                    case 'K':
                    case 'L':
                    case 'M':
                    case 'N':
                    case 'O':
                    case 'P':
                    case 'Q':
                    case 'R':
                    case 'T':
                    case 'U':
                    case 'V':
                    case 'W':
                    case 'X':
                    case 'Y':
                    default:
                        return string1 + "L" + string.charAt(ba) + ";";
                }
            } else {
                return string1 + "L" + string + ";";
            }
        } else {
            return string1 + "L" + string.substring(ba) + ";";
        }
    }

    @Override
    public String getDottedClassName() {
        return this.nameUtf8.getValue().replace('/', '.');
    }

    public ResolvedClassConstant(int ba, AbstractConstantPool abstractConstantPool, ConstantUtf8 constantUtf8, ListMultimap listMultimap) {
        super(0, abstractConstantPool);
        this.nameUtf8 = constantUtf8;
    }

    public boolean hasName(String string) {
        return this.getClassName().equals(string);
    }

    public void remapName(HashMap hashMap) {
        if (this.constantPool.getThisClassConstant() != this) {
            String string = this.nameUtf8.getValue();
            String string1 = (String) hashMap.get(string);
            if (string1 != null && !string1.equals(string)) {
                this.nameUtf8.setValue(string1);
            } else {
                string1 = ConstantPoolEntry.remapDescriptorClassNames(string, hashMap);
                if (!string.equals(string1)) {
                    this.nameUtf8.setValue(string1);
                }
            }
        }
    }

    public ConstantUtf8 getNameUtf8() {
        return this.nameUtf8;
    }

    public ProgramClass findProgramClass() {
        String string = ClassFileBase.descriptorToClassName(this.getClassName());
        return string != null ? ClassHierarchyNode.findProgramClass(string) : null;
    }

    @Override
    public ConstantPoolTag getTag() {
        return TAG;
    }

    @Override
    public void writeRemappedTo(DataOutputStream dataOutputStream, Map map1) throws IOException {
        dataOutputStream.writeByte(TAG.getTagValue());
        ConstantUtf8 constantUtf8 = (ConstantUtf8) map1.get(this.nameUtf8);
        if (constantUtf8 != null) {
            dataOutputStream.writeShort(constantUtf8.getIndex());
        } else {
            dataOutputStream.writeShort(this.nameUtf8.getIndex());
        }
    }

    public static ResolvedClassConstant findByName(String string, Collection collection1) {
        Iterator iterator = collection1.iterator();

        while (iterator.hasNext()) {
            ResolvedClassConstant resolvedClassConstant = (ResolvedClassConstant) iterator.next();
            if (resolvedClassConstant.getClassName().equals(string)) {
                return resolvedClassConstant;
            }
        }

        return null;
    }

    public boolean isArrayClass() {
        return this.getClassName().startsWith("[");
    }
}
