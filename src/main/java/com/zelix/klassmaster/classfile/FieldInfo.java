package com.zelix.klassmaster.classfile;

import com.zelix.klassmaster.classfile.attribute.Attribute;
import com.zelix.klassmaster.classfile.attribute.ConstantValueAttribute;
import com.zelix.klassmaster.classfile.attribute.SignatureAttribute;
import com.zelix.klassmaster.classfile.attribute.TypeAnnotationsAttribute;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ConstantUtf8;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.obfuscator.exclude.NameExclusionSet;
import com.zelix.klassmaster.obfuscator.rename.FieldNameAssigner;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.SetMultiMap;
import com.zelix.klassmaster.util.ThreeKeyMultiMap;
import com.zelix.klassmaster.util.TwoKeyMap;
import com.zelix.klassmaster.util.ZkmAssert;
import com.zelix.klassmaster.util.ZkmStringUtils;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FieldInfo extends AbstractFieldInfo {
    public boolean isIntCompatibleType() {
        String string = this.descriptorConstant.getValue();
        if (string.length() == 1) {
            switch (string.charAt(0)) {
                case 'B':
                case 'C':
                case 'I':
                case 'S':
                    return true;
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean isStringType() {
        return this.descriptorConstant.getValue().equals("Ljava/lang/String;");
    }

    public boolean isLongType() {
        return this.descriptorConstant.getValue().equals("J");
    }

    public void stripGenericInfo(int ba) {
        if (ba == 1) {
            ArrayList arrayList = new ArrayList(this.attributes.length);

            for (int i = 0; i < this.attributes.length; i++) {
                if (!(this.attributes[i] instanceof SignatureAttribute)) {
                    if (this.attributes[i] instanceof TypeAnnotationsAttribute) {
                        TypeAnnotationsAttribute typeAnnotationsAttribute = (TypeAnnotationsAttribute) this.attributes[i];
                        typeAnnotationsAttribute.removeSignatureDependentAnnotations();
                        if (!typeAnnotationsAttribute.isEmpty()) {
                            arrayList.add(this.attributes[i]);
                        }
                    } else {
                        arrayList.add(this.attributes[i]);
                    }
                }
            }

            if (arrayList.size() < this.attributes.length) {
                Attribute[] attributes1 = new Attribute[arrayList.size()];
                this.attributes = ((com.zelix.klassmaster.classfile.attribute.Attribute[]) (arrayList.toArray(attributes1)));
                this.attributeCount = this.attributes.length;
            }

            if (this.isEnum()) {
                this.setEnum();
            }
        }
    }

    public void assignNewName(
            ClassHierarchyNode classHierarchyNode,
            FieldNameAssigner fieldNameAssigner,
            TwoKeyMap twoKeyMap,
            TwoKeyMap twoKeyMap1,
            Map map1,
            ListMultimap listMultimap,
            ListMultimap listMultimap1,
            Map map2,
            Map map3,
            String string,
            NameExclusionSet nameExclusionSet,
            Map map4,
            HashMap hashMap,
            HashMap hashMap1,
            SetMultiMap setMultiMap,
            TwoKeyMap twoKeyMap2,
            Map map5
    ) throws ZkmException, IOException {
        String string1 = this.getSourceName();
        this.getTypeName();
        String string2;
        if (nameExclusionSet.isFieldExcluded(this)) {
            string2 = string1;
        } else if (map3 != null && map3.containsKey(this)) {
            string2 = (String) map3.get(this);
        } else if (map5.containsKey(this)) {
            string2 = (String) map5.get(this);
            map1.put(string2, this);
            map2.put(string2, this);
            listMultimap1.addValue(this.getDescriptorKey(), string2);
        } else {
            String string3 = this.getDescriptorKey();
            string2 = fieldNameAssigner.assignFieldName(
                    classHierarchyNode, this, string1, string3, map1, listMultimap, listMultimap1, map2, this.isSynthetic(), map4, hashMap, setMultiMap, twoKeyMap2
            );
        }

        if (string2 == null) {
            string2 = string1;
        }

        FieldSignature fieldSignature2 = new FieldSignature(string1, this.getDescriptor());
        FieldSignature fieldSignature = new FieldSignature(string2, this.getDescriptor());
        FieldSignature fieldSignature3 = (FieldSignature) twoKeyMap.putValue(string, fieldSignature2, fieldSignature);
        FieldSignature fieldSignature1 = (FieldSignature) twoKeyMap1.putValue(string, fieldSignature, fieldSignature2);
        if (fieldSignature1 != null) {
            ZkmAssert.assertTrue(
                    false,
                    new String[]{
                            "Duplicate field name in class "
                                    + ZkmUtils.slashesToDots((String) ZkmUtils.mapOrSelf(string, hashMap1))
                                    + ". Both "
                                    + fieldSignature2.getName()
                                    + " and "
                                    + fieldSignature1.getName()
                                    + " renamed to '"
                                    + fieldSignature.getName()
                                    + "'."
                    }
            );
        }

        hashMap.put(this, string2);
        if (!string2.equals(string1)) {
            this.setName(string2);
        }
    }

    public static String toIndefiniteArticleName(String string) {
        int ba;
        String string6;
        String string7;
        if ((ba = string.lastIndexOf("/")) != -1) {
            String string1 = string.substring(ba + 1);
            long bd = 1520020032836L;
            string6 = string1;
            string7 = "[]";
        } else {
            String string4 = string;
            long bc = 1520020032836L;
            string6 = string4;
            string7 = "[]";
        }

        char bb;
        String string5;
        StringBuilder stringBuilder;
        label31:
        {
            String string3 = string7;
            String string2 = string6;
            string5 = ZkmStringUtils.replaceAll(string2, string3, "Array");
            stringBuilder = new StringBuilder();
            bb = Character.toUpperCase(string5.charAt(0));
            StringBuilder stringBuilder1;
            if (bb != 'A') {
                if (bb != 'E') {
                    if (bb != 'I') {
                        if (bb != 'O') {
                            if (bb != 'U') {
                                stringBuilder = stringBuilder.append("a");
                                break label31;
                            }

                            stringBuilder1 = stringBuilder;
                            string6 = "an";
                        } else {
                            stringBuilder1 = stringBuilder;
                            string6 = "an";
                        }
                    } else {
                        stringBuilder1 = stringBuilder;
                        string6 = "an";
                    }
                } else {
                    stringBuilder1 = stringBuilder;
                    string6 = "an";
                }
            } else {
                stringBuilder1 = stringBuilder;
                string6 = "an";
            }

            stringBuilder = stringBuilder1.append(string6);
        }

        stringBuilder.append(bb + string5.substring(1));
        return stringBuilder.toString();
    }

    public void replaceNamePrefix(String string, String string1, TwoKeyMap twoKeyMap, ProgramClass programClass1, ClassRepository classRepository1) throws ZkmException, IOException {
        String string2 = this.getClassName();
        String string3 = this.getSourceName();
        String string4 = string3.substring(string.length());
        String string5 = string1 + string4;
        if (classRepository1.hasFieldNamed(string2, string5)) {
            StringBuffer stringBuffer = new StringBuffer();
            if (string4.length() > 0) {
                for (int i = string4.length() - 1; i >= 0; i += -1) {
                    char bb = string4.charAt(i);
                    if (Character.isDigit(bb)) {
                        char bc = bb;
                        stringBuffer.insert(0, bc);
                    }
                }
            }

            String string6 = string5.substring(0, string5.length() - stringBuffer.length());
            int bd = -1;
            if (stringBuffer.length() > 0) {
                try {
                    bd = Integer.parseInt(stringBuffer.toString());
                } catch (NumberFormatException numberFormatException) {
                }
            }

            do {
                string5 = string6 + ++bd;
            } while (classRepository1.hasFieldNamed(string2, string5));
        }

        this.setName(string5);
        classRepository1.reindexRenamedField(programClass1, this, string3);
        twoKeyMap.putValue(string2, string3, string5);
    }

    public FieldInfo(
            ClassFileComponent classFileComponent,
            ClassFileInputStream classFileInputStream,
            ListMultimap listMultimap,
            ListMultimap listMultimap1,
            ListMultimap listMultimap2,
            ListMultimap listMultimap3,
            ListMultimap listMultimap4,
            ListMultimap listMultimap5,
            ListMultimap listMultimap6,
            PrintWriter printWriter,
            ThreeKeyMultiMap threeKeyMultiMap
    ) throws ZkmProcessingException, IOException {
        super(classFileComponent, classFileInputStream, listMultimap, printWriter);
        this.attributes = new Attribute[this.attributeCount];

        for (int i = 0; i < this.attributes.length; i++) {
            Object object = null;
            PrintWriter printWriter1 = printWriter;
            this.attributes[i] = Attribute.readAttribute(
                    this,
                    classFileInputStream,
                    listMultimap,
                    listMultimap1,
                    listMultimap2,
                    listMultimap3,
                    listMultimap4,
                    listMultimap5,
                    listMultimap6,
                    (ListMultimap) null,
                    printWriter1,
                    (ListMultimap) object,
                    threeKeyMultiMap
            );
        }

        if (ConstantPoolEntry.descriptorToJavaType(this.getDescriptor(), true) == null) {
            printWriter.println("ERROR: " + this.getLocationName() + " : " + "Invalid Field Descriptor");
            this.setValid(false);
        } else {
            this.setValid(true);
        }
    }

    @Override
    public ClassFileBase getOwningClass() {
        return this.getProgramClass();
    }

    public ProgramClass getProgramClass() {
        return (ProgramClass) this.getParent();
    }

    public FieldInfo(ClassFileBase classFileBase, ConstantUtf8 constantUtf8, ConstantUtf8 constantUtf81, Attribute[] attributes1, int ba) {
        super(classFileBase, constantUtf8, constantUtf81, attributes1, ba);

        for (int i = 0; i < attributes1.length; i++) {
            attributes1[i].setParent(this);
        }
    }

    public ProgramClass getTypeProgramClass() {
        String string = ClassFileBase.extractClassName(this.getDescriptor());
        return string != null ? ClassHierarchyNode.findProgramClass(string) : null;
    }

    @Override
    public boolean isProgramMember() {
        return true;
    }

    public boolean removeConstantValueAttribute() {
        ConstantValueAttribute constantValueAttribute = null;
        Attribute[] attributes1 = new Attribute[this.attributes.length - 1];
        int ba = 0;

        for (int i = 0; i < this.attributes.length; i++) {
            if (this.attributes[i] instanceof ConstantValueAttribute) {
                constantValueAttribute = (ConstantValueAttribute) this.attributes[i];
            } else if (ba < attributes1.length) {
                attributes1[ba++] = this.attributes[i];
            }
        }

        if (constantValueAttribute != null) {
            this.attributes = attributes1;
            this.attributeCount = this.attributes.length;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ConstantValueAttribute getConstantValueAttribute() {
        for (int i = 0; i < this.attributes.length; i++) {
            if (this.attributes[i] instanceof ConstantValueAttribute) {
                return (ConstantValueAttribute) this.attributes[i];
            }
        }

        return null;
    }
}
