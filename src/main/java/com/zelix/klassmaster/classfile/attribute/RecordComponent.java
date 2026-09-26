package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.AbstractFieldInfo;
import com.zelix.klassmaster.classfile.AbstractMethodInfo;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.FieldInfo;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ConstantUtf8;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.classfile.constpool.Utf8ConstantReplaceable;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.config.IgnoreMissingReferencesSpec;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.obfuscator.trim.TrimProcessor;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.ThreeKeyMultiMap;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class RecordComponent extends ClassFileComponent implements Utf8ConstantReplaceable {
    public AbstractMethodInfo accessorMethod;
    public AbstractFieldInfo field;
    public boolean valid = true;
    public ConstantUtf8 nameConstant;
    public String errorMessage;
    public ConstantUtf8 descriptorConstant;
    public Attribute[] attributes;

    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeShort(this.nameConstant.getIndex());
        dataOutputStream.writeShort(this.descriptorConstant.getIndex());
        dataOutputStream.writeShort(this.attributes.length);
        Attribute[] attributes1 = this.attributes;
        int ba = attributes1.length;

        for (int i = 0; i < ba; i++) {
            attributes1[i].write(dataOutputStream);
        }
    }

    @Override
    public void collectUsedConstants(char bc, int bd, UsedConstantsCollector usedConstantsCollector, char be) {
        usedConstantsCollector.markUsed(this.nameConstant, this, this.getParent());
        usedConstantsCollector.markUsed(this.descriptorConstant, this, this.getParent());
        Attribute[] attributes1 = this.attributes;
        int ba = attributes1.length;

        for (int i = 0; i < ba; i++) {
            attributes1[i].collectUsedConstants('\u0000', 1103830477, usedConstantsCollector, '鞍');
        }
    }

    public void removeSignatureAttributes() {
        ArrayList arrayList = new ArrayList(this.attributes.length);

        for (Attribute attribute : this.attributes) {
            if (!(attribute instanceof SignatureAttribute)) {
                arrayList.add(attribute);
            }
        }

        if (arrayList.size() < this.attributes.length) {
            this.attributes = ((com.zelix.klassmaster.classfile.attribute.Attribute[]) (arrayList.toArray(new Attribute[arrayList.size()])));
        }
    }

    public void resolveReferences(ClassResolver classResolver1, IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1, ScriptEnvironment scriptEnvironment1) throws ZkmException, IOException {
        ClassFileBase classFileBase = this.getOwningClass();
        String string = this.descriptorConstant.getValue();
        this.field = classFileBase.findField(this.nameConstant.getValue(), string);
        if (this.field == null) {
            AbstractFieldInfo[] abstractFieldInfos = classFileBase.getFields();
            ArrayList arrayList = new ArrayList();

            for (AbstractFieldInfo abstractFieldInfo : abstractFieldInfos) {
                if (abstractFieldInfo.getDescriptor().equals(string)) {
                    arrayList.add(abstractFieldInfo);
                }
            }

            if (arrayList.size() == 1) {
                this.field = (AbstractFieldInfo) arrayList.get(0);
            }
        }

        String string1 = "()" + string;
        MethodSignature methodSignature1 = new MethodSignature(this.nameConstant.getValue(), string1);
        this.accessorMethod = classFileBase.findMethod(methodSignature1);
        if (this.accessorMethod == null) {
            AbstractMethodInfo[] abstractMethodInfos = classFileBase.getDeclaredMethods();
            ArrayList arrayList1 = new ArrayList();

            for (AbstractMethodInfo abstractMethodInfo : abstractMethodInfos) {
                if (abstractMethodInfo.getDescriptor().equals(string1)) {
                    String string2;
                    if (abstractMethodInfo.getSourceName().equals("hashCode")) {
                        if (abstractMethodInfo.getDescriptor().equals("()I")) {
                            continue;
                        }

                        string2 = abstractMethodInfo.getSourceName();
                    } else {
                        string2 = abstractMethodInfo.getSourceName();
                    }

                    if (!string2.equals("toString") || !abstractMethodInfo.getDescriptor().equals("()Ljava/lang/String;")) {
                        arrayList1.add(abstractMethodInfo);
                    }
                }
            }

            if (arrayList1.size() == 1) {
                this.accessorMethod = (AbstractMethodInfo) arrayList1.get(0);
            }
        }

        int ba = 0;
        int bb = ba;

        for (Attribute[] attributes1 = this.attributes; bb < attributes1.length; attributes1 = this.attributes) {
            if (this.attributes[ba] instanceof ReferencingAttribute) {
                ((ReferencingAttribute) this.attributes[ba]).resolveReferences(classResolver1, ignoreMissingReferencesSpec1, scriptEnvironment1);
            }

            bb = ++ba;
        }
    }

    public void applyFieldRenames() {
        if (this.valid) {
            if (this.field != null) {
                String string = this.field.getSourceName();
                if (!this.nameConstant.getValue().equals(string)) {
                    this.nameConstant.setValue(string);
                }
            }

            for (Attribute attribute : this.attributes) {
                if (attribute instanceof ReferencingAttribute) {
                    ((ReferencingAttribute) attribute).applyFieldRenames();
                }
            }
        }
    }

    public void remapClassNames(int ba, int bb, HashMap hashMap, HashMap hashMap1) throws ZkmProcessingException {
        if (this.valid) {
            String string = this.descriptorConstant.getValue();
            String string1 = ConstantPoolEntry.remapDescriptorClassNames(string, hashMap1);
            Attribute[] attributes1;
            if (!string1.equals(string)) {
                this.descriptorConstant.setValue(string1);
                attributes1 = this.attributes;
            } else {
                attributes1 = this.attributes;
            }

            for (Attribute attribute : attributes1) {
                HashMap hashMap3 = hashMap1;
                HashMap hashMap2 = hashMap;
                Integer integer = bb;
                attribute.remapClassNames(ba, integer, hashMap2, hashMap3);
            }
        }
    }

    @Override
    public void replaceUtf8Constant(ConstantUtf8 constantUtf8, ConstantUtf8 constantUtf81) {
        if (this.nameConstant == constantUtf8) {
            this.nameConstant = constantUtf81;
        } else if (this.descriptorConstant == constantUtf8) {
            this.descriptorConstant = constantUtf81;
        }
    }

    public boolean isValid() {
        return this.valid;
    }

    public RecordComponent(
            ClassFileComponent classFileComponent,
            ClassFileInputStream classFileInputStream,
            ListMultimap listMultimap,
            ListMultimap listMultimap1,
            ThreeKeyMultiMap threeKeyMultiMap,
            PrintWriter printWriter
    ) throws ZkmProcessingException, IOException {
        super(classFileComponent);
        int ba = classFileInputStream.readUnsignedShort();
        ConstantPoolEntry constantPoolEntry = this.getConstantPoolEntry(ba);
        if (constantPoolEntry instanceof ConstantUtf8) {
            this.nameConstant = (ConstantUtf8) constantPoolEntry;
            listMultimap.addValue(this.nameConstant, this);
        } else {
            this.valid = false;
            this.errorMessage = "Name index does not point to a CONSTANT_Utf8 : " + constantPoolEntry.getTag();
        }

        int bb = classFileInputStream.readUnsignedShort();
        ConstantPoolEntry constantPoolEntry1 = this.getConstantPoolEntry(bb);
        if (constantPoolEntry1 instanceof ConstantUtf8) {
            this.descriptorConstant = (ConstantUtf8) constantPoolEntry1;
            listMultimap.addValue(this.descriptorConstant, this);
        } else {
            this.valid = false;
            this.errorMessage = "Descriptor index does not point to a CONSTANT_Utf8 : " + constantPoolEntry1.getTag();
        }

        int bc = classFileInputStream.readUnsignedShort();
        this.attributes = new Attribute[bc];
        ListMultimap listMultimap2 = new ListMultimap();
        ListMultimap listMultimap3 = new ListMultimap();
        ListMultimap listMultimap4 = new ListMultimap();
        ListMultimap listMultimap5 = new ListMultimap();
        ListMultimap listMultimap6 = new ListMultimap();
        ListMultimap listMultimap7 = new ListMultimap();
        ListMultimap listMultimap8 = new ListMultimap();

        for (int i = 0; i < bc; i++) {
            try {
                this.attributes[i] = Attribute.readAttribute(
                        this,
                        classFileInputStream,
                        listMultimap,
                        listMultimap2,
                        listMultimap3,
                        listMultimap4,
                        listMultimap5,
                        listMultimap1,
                        listMultimap6,
                        listMultimap7,
                        printWriter,
                        listMultimap8,
                        threeKeyMultiMap
                );
            } catch (ZkmProcessingException zkmProcessingException) {
                this.valid = false;
                this.errorMessage = "Invalid attribute : '" + zkmProcessingException.getMessage() + "'";
                throw zkmProcessingException;
            }
        }
    }

    public void writeRemapped(DataOutputStream dataOutputStream, Map map1, ScriptEnvironment scriptEnvironment1) throws IOException {
        ConstantPoolEntry constantPoolEntry = (ConstantPoolEntry) map1.get(this.nameConstant);
        Map map4;
        ConstantUtf8 constantUtf8;
        if (constantPoolEntry != null) {
            dataOutputStream.writeShort(constantPoolEntry.getIndex());
            map4 = map1;
            constantUtf8 = this.descriptorConstant;
        } else {
            dataOutputStream.writeShort(this.nameConstant.getIndex());
            map4 = map1;
            constantUtf8 = this.descriptorConstant;
        }

        ConstantPoolEntry constantPoolEntry1 = (ConstantPoolEntry) map4.get(constantUtf8);
        DataOutputStream dataOutputStream2;
        Attribute[] attributes1;
        if (constantPoolEntry1 != null) {
            dataOutputStream.writeShort(constantPoolEntry1.getIndex());
            dataOutputStream2 = dataOutputStream;
            attributes1 = this.attributes;
        } else {
            dataOutputStream.writeShort(this.descriptorConstant.getIndex());
            dataOutputStream2 = dataOutputStream;
            attributes1 = this.attributes;
        }

        dataOutputStream2.writeShort(attributes1.length);

        for (Attribute attribute : this.attributes) {
            Map map2 = map1;
            ScriptEnvironment scriptEnvironment2 = scriptEnvironment1;
            Map map3 = map2;
            DataOutputStream dataOutputStream1 = dataOutputStream;
            attribute.writeRemapped(dataOutputStream1, map3, scriptEnvironment2);
        }
    }

    public void applyMethodRenames() {
        if (this.valid) {
            for (Attribute attribute : this.attributes) {
                if (attribute instanceof ReferencingAttribute) {
                    ((ReferencingAttribute) attribute).applyMethodRenames();
                }
            }
        }
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void trimAttributes(TrimProcessor trimProcessor1, ScriptEnvironment scriptEnvironment1, PrintWriter printWriter) {
        ArrayList arrayList = new ArrayList(this.attributes.length);

        for (Attribute attribute : this.attributes) {
            if (attribute instanceof ReferencingAttribute) {
                if (!((ReferencingAttribute) attribute).trimAnnotations(trimProcessor1, scriptEnvironment1, printWriter)) {
                    arrayList.add(attribute);
                }
            } else {
                arrayList.add(attribute);
            }
        }

        if (arrayList.size() < this.attributes.length) {
            this.attributes = ((com.zelix.klassmaster.classfile.attribute.Attribute[]) (arrayList.toArray(new Attribute[arrayList.size()])));
        }
    }

    public void collectReferencedMembers(HashSet hashSet, HashSet hashSet1, HashSet hashSet2, HashSet hashSet3) {
        if (this.field != null && this.field.isProgramMember()) {
            hashSet2.add((FieldInfo) this.field);
        }

        if (this.accessorMethod != null && this.accessorMethod.isProgramMember()) {
            hashSet3.add((MethodInfo) this.accessorMethod);
        }

        int ba = 0;
        int bb = ba;

        for (Attribute[] attributes1 = this.attributes; bb < attributes1.length; attributes1 = this.attributes) {
            if (this.attributes[ba] instanceof ReferencingAttribute) {
                ((ReferencingAttribute) this.attributes[ba]).collectReferencedMembers(hashSet, hashSet1, hashSet2, hashSet3);
            }

            bb = ++ba;
        }
    }
}
