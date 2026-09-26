package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ConstantUtf8;
import com.zelix.klassmaster.classfile.constpool.Utf8ConstantReplaceable;
import com.zelix.klassmaster.classfile.hierarchy.ClasspathClassFile;
import com.zelix.klassmaster.classfile.insn.LocalVariableList;
import com.zelix.klassmaster.exceptions.ClassFileFormatException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.util.EnumerableMap;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.ThreeKeyMultiMap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public abstract class Attribute extends ClassFileComponent implements Utf8ConstantReplaceable {
    public static final EnumerableMap ATTRIBUTE_NAME_FLAGS;
    public ConstantUtf8 nameConstant;
    private String attributeName;
    public int length;

    @Override
    public void replaceUtf8Constant(ConstantUtf8 constantUtf8, ConstantUtf8 constantUtf81) {
        if (this.nameConstant == constantUtf8) {
            this.nameConstant = constantUtf81;
        }
    }

    public void remapClassNames(Object object, Object object1, Object object2, Object object3) throws ZkmProcessingException {
    }

    public String getAttributeName() {
        return this.attributeName;
    }

    public final int getTotalSize() {
        return 6 + this.getLength();
    }

    public static Attribute readAttribute(
            ClassFileComponent classFileComponent,
            ClassFileInputStream classFileInputStream,
            ListMultimap listMultimap,
            ListMultimap listMultimap1,
            ListMultimap listMultimap2,
            ListMultimap listMultimap3,
            ListMultimap listMultimap4,
            ListMultimap listMultimap5,
            ListMultimap listMultimap6,
            ListMultimap listMultimap7,
            PrintWriter printWriter,
            ListMultimap listMultimap8,
            ThreeKeyMultiMap threeKeyMultiMap
    ) throws ZkmProcessingException, IOException {
        return readAttribute(
                classFileComponent,
                classFileInputStream,
                (LocalVariableList) null,
                listMultimap,
                listMultimap1,
                listMultimap2,
                listMultimap3,
                listMultimap4,
                listMultimap5,
                listMultimap6,
                listMultimap7,
                printWriter,
                listMultimap8,
                threeKeyMultiMap
        );
    }

    static {
        HashMap hashMap = ZkmUtils.createHashMap();
        hashMap.put("ConstantValue", 1);
        hashMap.put("Code", 2);
        hashMap.put("Exceptions", 4);
        hashMap.put("SourceFile", 8);
        hashMap.put("LineNumberTable", 16);
        hashMap.put("SourceDebugExtension", 32);
        hashMap.put("LocalVariableTable", 64);
        hashMap.put("LocalVariableTypeTable", 128);
        hashMap.put("InnerClasses", 256);
        hashMap.put("Synthetic", 512);
        hashMap.put("Deprecated", 1024);
        hashMap.put("StackMap", 2048);
        hashMap.put("StackMapTable", 4096);
        hashMap.put("Signature", 8192);
        hashMap.put("Bridge", 16384);
        hashMap.put("EnclosingMethod", 32768);
        hashMap.put("RuntimeVisibleAnnotations", 65536);
        hashMap.put("RuntimeInvisibleAnnotations", 131072);
        hashMap.put("RuntimeVisibleParameterAnnotations", 262144);
        hashMap.put("RuntimeInvisibleParameterAnnotations", 524288);
        hashMap.put("AnnotationDefault", 1048576);
        hashMap.put("RuntimeVisibleTypeAnnotations", 2097152);
        hashMap.put("RuntimeInvisibleTypeAnnotations", 4194304);
        hashMap.put("BootstrapMethods", 8388608);
        hashMap.put("MethodParameters", 16777216);
        ATTRIBUTE_NAME_FLAGS = new EnumerableMap(hashMap);
    }

    public static Attribute readClasspathAttribute(
            ClassFileComponent classFileComponent,
            ClassFileInputStream classFileInputStream,
            ListMultimap listMultimap,
            ListMultimap listMultimap1,
            ListMultimap listMultimap2,
            PrintWriter printWriter,
            ThreeKeyMultiMap threeKeyMultiMap
    ) throws ZkmProcessingException, IOException {
        int ba = classFileInputStream.readUnsignedShort();
        ConstantPoolEntry constantPoolEntry = classFileComponent.getConstantPoolEntry(ba);
        if (constantPoolEntry == null) {
            throw new ClassFileFormatException(
                    classFileComponent.getOwningClass().getLocationName() + " : Illegal constant pool index in attribute : " + ba + " : File is probably corrupt (C)"
            );
        }

        if (!(constantPoolEntry instanceof ConstantUtf8)) {
            throw new ClassFileFormatException(
                    classFileComponent.getOwningClass().getLocationName()
                            + " : Invalid attribute : "
                            + ba
                            + " : '"
                            + constantPoolEntry.getClass().getName()
                            + "' : File is probably corrupt (D)"
            );
        }

        String string = ((ConstantUtf8) constantPoolEntry).getValue();
        switch (string) {
            case "InnerClasses":
                return new InnerClassesAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap, listMultimap2, printWriter);
            case "EnclosingMethod":
                return new EnclosingMethodAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap, listMultimap1, listMultimap2, printWriter);
            case "NestHost":
                return new NestHostAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap, listMultimap2);
            case "NestMembers":
                return new NestMembersAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap);
            case "Code":
                return new RawCodeAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap);
            case "RuntimeVisibleAnnotations":
                if (classFileComponent instanceof ClasspathClassFile) {
                    return new RuntimeVisibleAnnotationsAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap, printWriter);
                }

                return new IgnoredAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap);
            case "RuntimeInvisibleAnnotations":
                if (classFileComponent instanceof ClasspathClassFile) {
                    return new RuntimeInvisibleAnnotationsAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap, printWriter);
                }

                return new IgnoredAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap);
            case "Record":
                return new RecordAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap, listMultimap2, threeKeyMultiMap, printWriter);
            default:
                return new IgnoredAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap);
        }
    }

    public void writeRemapped(DataOutputStream dataOutputStream, Object object, Object object1) throws IOException {
        ConstantUtf8 constantUtf8 = (ConstantUtf8) ((Map) object).get(this.nameConstant);
        if (constantUtf8 != null) {
            dataOutputStream.writeShort(constantUtf8.getIndex());
        } else {
            dataOutputStream.writeShort(this.nameConstant.getIndex());
        }

        dataOutputStream.writeInt(this.getLength());
    }

    public void write(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeShort(this.nameConstant.getIndex());
        dataOutputStream.writeInt(this.getLength());
    }

    public Attribute(ClassFileComponent classFileComponent, int ba, String string, ClassFileInputStream classFileInputStream, ListMultimap listMultimap) throws IOException {
        super(classFileComponent);
        this.nameConstant = (ConstantUtf8) this.getConstantPoolEntry(ba);
        this.attributeName = string;
        this.length = classFileInputStream.readInt();
        listMultimap.addValue(this.nameConstant, this);
    }

    public ConstantUtf8 getNameConstant() {
        return this.nameConstant;
    }

    public static Attribute readAttribute(
            ClassFileComponent classFileComponent,
            ClassFileInputStream classFileInputStream,
            LocalVariableList localVariableList1,
            ListMultimap listMultimap,
            ListMultimap listMultimap1,
            ListMultimap listMultimap2,
            ListMultimap listMultimap3,
            ListMultimap listMultimap4,
            ListMultimap listMultimap5,
            ListMultimap listMultimap6,
            ListMultimap listMultimap7,
            PrintWriter printWriter,
            ListMultimap listMultimap8,
            ThreeKeyMultiMap threeKeyMultiMap
    ) throws ZkmProcessingException, IOException {
        int ba = classFileInputStream.readUnsignedShort();
        ConstantPoolEntry constantPoolEntry = classFileComponent.getConstantPoolEntry(ba);
        if (constantPoolEntry == null) {
            throw new ClassFileFormatException(
                    classFileComponent.getOwningClass().getLocationName() + " : Illegal constant pool index in attribute : " + ba + " : File is probably corrupt (A)"
            );
        }

        if (!(constantPoolEntry instanceof ConstantUtf8)) {
            throw new ClassFileFormatException(
                    classFileComponent.getOwningClass().getLocationName()
                            + " : Invalid attribute : "
                            + ba
                            + " : '"
                            + constantPoolEntry.getClass().getName()
                            + "' : File is probably corrupt (B)"
            );
        }

        String string = ((ConstantUtf8) constantPoolEntry).getValue();
        switch (string) {
            case "ConstantValue":
                return new ConstantValueAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap, listMultimap2, listMultimap3, listMultimap4);
            case "Code":
                return new CodeAttributeBody(
                        classFileComponent,
                        ba,
                        string,
                        classFileInputStream,
                        listMultimap,
                        listMultimap1,
                        listMultimap2,
                        listMultimap3,
                        listMultimap4,
                        listMultimap5,
                        listMultimap6,
                        listMultimap7,
                        printWriter,
                        threeKeyMultiMap,
                        listMultimap8
                );
            case "Exceptions":
                return new ExceptionsAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap, listMultimap5);
            case "SourceFile":
                return new SourceFileAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap);
            case "Signature":
                return new SignatureAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap);
            case "LineNumberTable":
                return new LineNumberTableAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap, printWriter, listMultimap8);
            case "LocalVariableTable":
                return new LocalVariableTableAttribute(
                        classFileComponent, ba, string, classFileInputStream, localVariableList1, listMultimap, printWriter, listMultimap8
                );
            case "LocalVariableTypeTable":
                return new LocalVariableTypeTableAttribute(
                        classFileComponent, ba, string, classFileInputStream, localVariableList1, listMultimap, printWriter, listMultimap8
                );
            case "InnerClasses":
                return new InnerClassesAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap, listMultimap5, printWriter);
            case "Synthetic":
                return new SyntheticAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap);
            case "Deprecated":
                return new DeprecatedAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap);
            case "Bridge":
                return new BridgeAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap);
            case "RuntimeVisibleAnnotations":
                return new RuntimeVisibleAnnotationsAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap, printWriter);
            case "RuntimeInvisibleAnnotations":
                return new RuntimeInvisibleAnnotationsAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap, printWriter);
            case "RuntimeVisibleParameterAnnotations":
                return new RuntimeVisibleParameterAnnotationsAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap, printWriter);
            case "RuntimeInvisibleParameterAnnotations":
                return new RuntimeInvisibleParameterAnnotationsAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap, printWriter);
            case "RuntimeVisibleTypeAnnotations":
                return new RuntimeVisibleTypeAnnotationsAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap, listMultimap8, printWriter);
            case "RuntimeInvisibleTypeAnnotations":
                return new RuntimeInvisibleTypeAnnotationsAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap, listMultimap8, printWriter);
            case "AnnotationDefault":
                return new AnnotationDefaultAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap);
            case "EnclosingMethod":
                return new EnclosingMethodAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap, listMultimap1, listMultimap5, printWriter);
            case "StackMap":
                return new StackMapAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap, listMultimap5, printWriter, listMultimap8);
            case "StackMapTable":
                return new StackMapTableAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap, listMultimap5, printWriter, listMultimap8);
            case "BootstrapMethods":
                return new BootstrapMethodsAttribute(
                        classFileComponent, ba, string, classFileInputStream, listMultimap, listMultimap2, listMultimap3, listMultimap4, listMultimap5, printWriter
                );
            case "SourceDebugExtension":
                return new SourceDebugExtensionAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap);
            case "MethodParameters":
                return new MethodParametersAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap, printWriter);
            case "Module":
                return new ModuleAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap, listMultimap5);
            case "ModulePackages":
                return new ModulePackagesAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap);
            case "ModuleVersion":
                return new ModuleVersionAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap);
            case "ModuleMainClass":
                return new ModuleMainClassAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap, listMultimap5);
            case "ModuleTarget":
                return new ModuleTargetAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap);
            case "ModuleHashes":
                return new ModuleHashesAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap);
            case "ModuleResolution":
                return new ModuleResolutionAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap);
            case "NestHost":
                return new NestHostAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap, listMultimap5);
            case "NestMembers":
                return new NestMembersAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap);
            case "Record":
                return new RecordAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap, listMultimap5, threeKeyMultiMap, printWriter);
            case "PermittedSubclasses":
                return new PermittedSubclassesAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap, listMultimap5, printWriter);
            default:
                return new UnknownAttribute(classFileComponent, ba, string, classFileInputStream, listMultimap, threeKeyMultiMap);
        }
    }

    public int replaceLength(int length) {
        int bb = this.length;
        this.length = length;
        return bb;
    }

    public Attribute(ClassFileComponent classFileComponent, ConstantUtf8 constantUtf8, int length) {
        super(classFileComponent);
        this.nameConstant = constantUtf8;
        this.attributeName = constantUtf8.getValue();
        this.length = length;
    }

    public int getLength() {
        return this.length;
    }
}
