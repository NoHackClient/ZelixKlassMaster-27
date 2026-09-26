package com.zelix.klassmaster.classfile.hierarchy;

import com.zelix.klassmaster.archive.InputFileLocation;
import com.zelix.klassmaster.classfile.AbstractFieldInfo;
import com.zelix.klassmaster.classfile.AbstractMethodInfo;
import com.zelix.klassmaster.classfile.AccessFlags;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.FieldSignature;
import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.attribute.Attribute;
import com.zelix.klassmaster.classfile.attribute.BootstrapMethodsAttribute;
import com.zelix.klassmaster.classfile.attribute.CodeAttributeBody;
import com.zelix.klassmaster.classfile.attribute.ExceptionsAttribute;
import com.zelix.klassmaster.classfile.attribute.RecordAttribute;
import com.zelix.klassmaster.classfile.attribute.ReferencingAttribute;
import com.zelix.klassmaster.classfile.constpool.AbstractConstantPool;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ConstantUtf8;
import com.zelix.klassmaster.classfile.constpool.LibraryConstantPool;
import com.zelix.klassmaster.classfile.constpool.ResolvedClassConstant;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.classfile.insn.ExceptionHandlerSpec;
import com.zelix.klassmaster.classfile.insn.LocalVariableList;
import com.zelix.klassmaster.classfile.insn.MethodBytecode;
import com.zelix.klassmaster.config.IgnoreMissingReferencesSpec;
import com.zelix.klassmaster.exceptions.ClassFileFormatException;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.log.MessageReporter;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.DisableableMap;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.PairValueMap;
import com.zelix.klassmaster.util.ThreeKeyMultiMap;
import com.zelix.klassmaster.util.TwoKeyMap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClasspathClassFile extends ClassFileBase {
    public LibraryConstantPool constantPool;
    public LibraryFieldInfo[] fields;
    public LibraryMethod[] methods;

    public void indexMethods(Map map1, ListMultimap listMultimap, Map map2, DisableableMap disableableMap, DisableableMap disableableMap1) {
        for (int i = 0; i < this.methodCount; i++) {
            LibraryMethod libraryMethod = this.methods[i];
            map1.put(libraryMethod.getSignature(), libraryMethod);
            listMultimap.addValue(libraryMethod.getDescriptor(), libraryMethod.getSourceName());
            map2.put(libraryMethod.getNameTypeSignature(), libraryMethod);
            if (this.isInterface()) {
                disableableMap.put(libraryMethod.getSourceName(), libraryMethod);
            }

            disableableMap1.put(libraryMethod.getSourceName(), libraryMethod);
        }
    }

    @Override
    public final boolean isProgramClass() {
        return false;
    }

    @Override
    public void collectUsedConstants(char ba, int bb, UsedConstantsCollector usedConstantsCollector, char bc) {
    }

    @Override
    public AbstractConstantPool getConstantPool() {
        return this.constantPool;
    }

    @Override
    public ConstantPoolEntry getConstantPoolEntry(int ba) {
        return this.constantPool.getConstantPoolEntry(ba);
    }

    public synchronized void addMethod(LibraryMethod libraryMethod) {
        LibraryMethod[] libraryMethods = new LibraryMethod[this.methodCount + 1];
        System.arraycopy(this.methods, 0, libraryMethods, 0, this.methodCount);
        libraryMethods[this.methodCount] = libraryMethod;
        libraryMethod.setParent(this);
        this.methods = libraryMethods;
        this.methodCount++;
    }

    public void applyMethodNameMapping(TwoKeyMap twoKeyMap, PairValueMap pairValueMap) throws ZkmException, IOException {
        Map map1 = twoKeyMap.getInnerMap(this.getClassName());

        for (int i = 0; i < this.methodCount; i++) {
            this.methods[i].applyMethodRename(map1, pairValueMap);
        }
    }

    @Override
    public void updateFieldReferences(ScriptEnvironment scriptEnvironment1, ClassMemberLookup classMemberLookup1) throws ZkmException, IOException {
        this.constantPool.resolveFieldRefs(scriptEnvironment1, classMemberLookup1);

        for (int i = 0; i < this.attributes.length; i++) {
            if (this.attributes[i] instanceof ReferencingAttribute) {
                ((ReferencingAttribute) this.attributes[i]).applyFieldRenames();
            } else if (this.attributes[i] instanceof RecordAttribute) {
                ((RecordAttribute) this.attributes[i]).applyFieldRenames();
            } else if (this.attributes[i] instanceof BootstrapMethodsAttribute) {
                ((BootstrapMethodsAttribute) this.attributes[i]).updateRecordObjectMethodsNames();
            }
        }

        for (int i = 0; i < this.fieldCount; i++) {
            this.fields[i].updateAttributesAfterFieldRename();
        }

        for (int i = 0; i < this.methodCount; i++) {
            this.methods[i].updateAttributesAfterFieldRename();
        }
    }

    public void resolveConstantPool(ClassMemberLookup classMemberLookup1, ClassResolver classResolver1, IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1) throws ZkmException, IOException {
        this.constantPool.resolveMemberRefTargets(classMemberLookup1, classResolver1, ignoreMissingReferencesSpec1);
    }

    public void updateClassNames(HashMap hashMap, MessageReporter messageReporter1) throws ZkmException, IOException {
        this.constantPool.applyClassRenames(hashMap, messageReporter1);
        LibraryMethod[] libraryMethods = this.getMethods();
        int ba = libraryMethods.length;

        for (int i = 0; i < ba; i++) {
            libraryMethods[i].remapDescriptor(hashMap);
        }
    }

    public ClasspathClassFile(ClassFileInputStream classFileInputStream, InputFileLocation inputFileLocation) throws ZkmProcessingException {
        super(inputFileLocation, classFileInputStream.getDigest(), 0);

        try {
            this.readMagicAndVersion(classFileInputStream);
            ListMultimap listMultimap = new ListMultimap();
            ListMultimap listMultimap1 = new ListMultimap();
            ListMultimap listMultimap2 = new ListMultimap();
            ListMultimap listMultimap3 = new ListMultimap();
            ListMultimap listMultimap4 = new ListMultimap();
            ListMultimap listMultimap5 = new ListMultimap();
            ListMultimap listMultimap6 = new ListMultimap();
            ListMultimap listMultimap7 = new ListMultimap();
            this.constantPool = new LibraryConstantPool(classFileInputStream, this, listMultimap, listMultimap1, listMultimap2, listMultimap6);
            super.constantPoolCount = this.constantPool.getEntryCount();
            this.accessFlags = new AccessFlags(this, classFileInputStream);
            int ba = classFileInputStream.readUnsignedShort();
            int bb = classFileInputStream.readUnsignedShort();
            super.interfaceCount = classFileInputStream.readUnsignedShort();
            int[] bc = new int[super.interfaceCount];
            int bd = 0;
            int bi = 0;

            for (int i = super.interfaceCount; bi < i; i = super.interfaceCount) {
                bc[bd] = classFileInputStream.readUnsignedShort();
                bi = ++bd;
            }

            Object object = null;
            int[] bf = bc;
            Integer integer = bb;
            this.readClassConstants(ba, integer, bf, (ListMultimap) object);
            this.fieldCount = classFileInputStream.readUnsignedShort();
            this.fields = new LibraryFieldInfo[this.fieldCount];

            for (int i = 0; i < this.fieldCount; i++) {
                this.fields[i] = new LibraryFieldInfo(this, classFileInputStream, listMultimap);
            }

            PrintWriter printWriter = new PrintWriter(new ByteArrayOutputStream());
            ThreeKeyMultiMap threeKeyMultiMap = new ThreeKeyMultiMap();
            this.methodCount = classFileInputStream.readUnsignedShort();
            this.methods = new LibraryMethod[this.methodCount];

            for (int i = 0; i < this.methodCount; i++) {
                this.methods[i] = new LibraryMethod(this, classFileInputStream, listMultimap, listMultimap1, listMultimap2, printWriter, threeKeyMultiMap);
            }

            this.attributeCount = classFileInputStream.readUnsignedShort();
            this.attributes = new Attribute[this.attributeCount];

            for (int i = 0; i < this.attributeCount; i++) {
                this.attributes[i] = Attribute.readClasspathAttribute(
                        this, classFileInputStream, listMultimap, listMultimap1, listMultimap2, printWriter, threeKeyMultiMap
                );
            }

            this.resolveInnerClassEntry();
            this.constantPool
                    .unshareConstants(listMultimap, listMultimap1, listMultimap3, listMultimap4, listMultimap5, listMultimap2, listMultimap6, listMultimap7);
        } catch (IOException iOException1) {
            throw new ZkmProcessingException(inputFileLocation.getQualifiedName() + " : " + iOException1 + " : File is probably corrupt (F)");
        } catch (ClassFileFormatException classFileFormatException) {
            throw new ZkmProcessingException(
                    inputFileLocation.getQualifiedName() + " : \"" + classFileFormatException.getMessage() + "\" : File is probably corrupt (G)"
            );
        } finally {
            if (classFileInputStream != null) {
                try {
                    classFileInputStream.close();
                } catch (IOException iOException) {
                    throw new ZkmProcessingException("IOException on closing : " + inputFileLocation.getQualifiedName() + " : " + iOException);
                }
            }
        }
    }

    @Override
    public AbstractMethodInfo[] getDeclaredMethods() {
        return this.getMethods();
    }

    public synchronized void addField(LibraryFieldInfo libraryFieldInfo) {
        LibraryFieldInfo[] libraryFieldInfos = new LibraryFieldInfo[this.fieldCount + 1];
        System.arraycopy(this.fields, 0, libraryFieldInfos, 0, this.fieldCount);
        libraryFieldInfos[this.fieldCount] = libraryFieldInfo;
        libraryFieldInfo.setParent(this);
        this.fields = libraryFieldInfos;
        this.fieldCount++;
    }

    public LibraryMethod[] getMethods() {
        return this.methods;
    }

    @Override
    public void remapClassNames(int ba, int bb, HashMap hashMap, Object object, Object object1, HashMap hashMap1, MessageReporter messageReporter1) throws ZkmException, IOException {
        this.constantPool.applyClassRenames(hashMap, messageReporter1);

        for (int i = 0; i < this.methodCount; i++) {
            this.methods[i].remapClassNames(ba, bb, hashMap1, hashMap);
        }

        for (int i = 0; i < this.fieldCount; i++) {
            this.fields[i].remapClassNames(ba, bb, hashMap1, hashMap);
        }

        this.trimInnerClassesAttribute();

        for (int i = 0; i < this.attributes.length; i++) {
            Attribute attribute = this.attributes[i];
            HashMap hashMap2 = hashMap1;
            Integer integer = bb;
            attribute.remapClassNames(ba, integer, hashMap2, hashMap);
        }
    }

    @Override
    public void updateMethodReferences(ScriptEnvironment scriptEnvironment1, ClassMemberLookup classMemberLookup1, Object object) throws ZkmException, IOException {
        this.constantPool.resolveMethodRefs(scriptEnvironment1, classMemberLookup1);

        for (int i = 0; i < this.attributes.length; i++) {
            if (this.attributes[i] instanceof ReferencingAttribute) {
                ((ReferencingAttribute) this.attributes[i]).applyMethodRenames();
            } else if (this.attributes[i] instanceof RecordAttribute) {
                ((RecordAttribute) this.attributes[i]).applyMethodRenames();
            }
        }

        for (int i = 0; i < this.fieldCount; i++) {
            this.fields[i].updateAttributesAfterMethodRename();
        }

        for (int i = 0; i < this.methodCount; i++) {
            this.methods[i].updateAttributesAfterMethodRename();
        }
    }

    public LibraryFieldInfo createField(String string, String string1, int ba) throws ZkmException, IOException {
        if (this.findField(new FieldSignature(string, string1)) != null) {
            return null;
        }

        ArrayList arrayList = new ArrayList(2);
        ConstantUtf8 constantUtf8 = new ConstantUtf8(0, this.constantPool, string);
        arrayList.add(constantUtf8);
        ConstantUtf8 constantUtf81 = new ConstantUtf8(0, this.constantPool, string1);
        arrayList.add(constantUtf81);
        Attribute[] attributes1 = new Attribute[0];
        LibraryFieldInfo libraryFieldInfo = new LibraryFieldInfo(this, constantUtf8, constantUtf81, attributes1);
        libraryFieldInfo.setStatic();
        switch (ba) {
            case 1:
                libraryFieldInfo.makePrivate();
                break;
            case 2:
                libraryFieldInfo.makePackagePrivate();
                break;
            case 3:
                libraryFieldInfo.makePublic();
                break;
            case 4:
                libraryFieldInfo.makePublic();
        }

        this.addField(libraryFieldInfo);
        this.constantPool.appendEntries(arrayList);
        this.setChanged();
        this.notifyObservers(new MutableInt(4), this, null);
        return libraryFieldInfo;
    }

    public LibraryMethod getOrCreateMethod(MethodSignature methodSignature1) {
        LibraryMethod libraryMethod = (LibraryMethod) this.findMethod(methodSignature1);
        if (libraryMethod != null) {
            return libraryMethod;
        }

        ArrayList arrayList = new ArrayList();
        LibraryConstantPool libraryConstantPool = (LibraryConstantPool) this.getConstantPool();
        ConstantUtf8 constantUtf8 = libraryConstantPool.createUtf8Constant(methodSignature1.getName(), arrayList);
        ConstantUtf8 constantUtf81 = libraryConstantPool.createUtf8Constant(methodSignature1.getDescriptor(), arrayList);
        ConstantUtf8 constantUtf82 = libraryConstantPool.createUtf8Constant("Exceptions", arrayList);
        ResolvedClassConstant[] resolvedClassConstants = new ResolvedClassConstant[]{
                libraryConstantPool.getOrCreateClassConstant("java/lang/Throwable", arrayList)
        };
        Attribute[] attributes1 = new Attribute[]{new ExceptionsAttribute(constantUtf82, resolvedClassConstants)};
        libraryMethod = new LibraryMethod(this, constantUtf8, constantUtf81, attributes1, true, 3);
        libraryConstantPool.appendEntries(arrayList);
        this.addMethod(libraryMethod);
        return libraryMethod;
    }

    public void applyFieldNameMapping(TwoKeyMap twoKeyMap) throws ZkmException, IOException {
        for (int i = 0; i < this.fieldCount; i++) {
            this.fields[i].applyFieldRename(twoKeyMap.getInnerMap(this.getClassName()));
        }
    }

    @Override
    public AbstractFieldInfo[] getFields() {
        return this.getFields_y7Arr();
    }

    public LibraryMethod createCodeMethod(
            String string,
            String string1,
            ArrayList arrayList,
            int ba,
            LocalVariableList localVariableList1,
            ExceptionHandlerSpec[] exceptionHandlerSpecs,
            List list1,
            InheritedMemberAnalyzer inheritedMemberAnalyzer
    ) throws ZkmProcessingException {
        MethodBytecode methodBytecode1 = new MethodBytecode(arrayList, localVariableList1, "Flow Obfuscation");
        ConstantUtf8 constantUtf8 = new ConstantUtf8(0, this.constantPool, string);
        list1.add(constantUtf8);
        ConstantUtf8 constantUtf81 = new ConstantUtf8(0, this.constantPool, string1);
        list1.add(constantUtf81);
        ConstantUtf8 constantUtf82 = new ConstantUtf8(0, this.constantPool, "Code");
        list1.add(constantUtf82);
        CodeAttributeBody codeAttributeBody = new CodeAttributeBody(constantUtf82, 1, ba, methodBytecode1, exceptionHandlerSpecs);
        Attribute[] attributes1 = new Attribute[]{codeAttributeBody};
        LibraryMethod libraryMethod = new LibraryMethod(this, constantUtf8, constantUtf81, attributes1, false, 2);
        libraryMethod.makePublic();
        libraryMethod.setStatic();
        this.addMethod(libraryMethod);
        inheritedMemberAnalyzer.addMethod(libraryMethod);
        return libraryMethod;
    }

    public LibraryFieldInfo[] getFields_y7Arr() {
        return this.fields;
    }
}
