package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ConstantUtf8;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.classfile.constpool.Utf8ConstantReplaceable;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.exceptions.AssertionFailedException;
import com.zelix.klassmaster.exceptions.ClassFileFormatException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.exceptions.ZkmRuntimeException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.GenericTree;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.ObjectTreeNode;
import com.zelix.klassmaster.util.ZkmStringUtils;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class SignatureAttribute extends Attribute implements Utf8ConstantReplaceable {
    public static char[] classTypeStartChars = new char[]{'L', 'T'};
    public static char[] typeStartChars = new char[]{'L', 'T', '.'};
    public static char[] identifierEndChars = new char[]{';', '<', ':', '.'};
    public ConstantUtf8 signatureConstant;

    @Override
    public final void write(DataOutputStream dataOutputStream) throws IOException {
        super.write(dataOutputStream);
        dataOutputStream.writeShort(this.signatureConstant.getIndex());
    }

    public static void mergeInnerClassSegments(ArrayList arrayList, Iterator iterator, String string) {
        SignatureClassTypeSegment signatureClassTypeSegment = (SignatureClassTypeSegment) arrayList.get(0);
        SignatureClassTypeSegment signatureClassTypeSegment1 = (SignatureClassTypeSegment) arrayList.get(arrayList.size() - 1);

        for (int i = 0; i < arrayList.size() - 1; i++) {
            SignatureClassTypeSegment signatureClassTypeSegment2 = (SignatureClassTypeSegment) arrayList.get(i);
            iterator.remove();
            if (i < arrayList.size() - 2) {
                signatureClassTypeSegment2 = (SignatureClassTypeSegment) ((ObjectTreeNode) iterator.next()).getValue();
            }
        }

        signatureClassTypeSegment1.setPrefix(signatureClassTypeSegment.getPrefix());
        signatureClassTypeSegment1.setClassName(string);
        signatureClassTypeSegment1.setInnerClassSuffix();
    }

    public void collectReferencedClasses(Set set1) throws ZkmProcessingException {
        GenericTree genericTree = this.getSignatureTree();
        if (genericTree != null) {
            Enumeration enumeration = genericTree.values();

            while (enumeration.hasMoreElements()) {
                String string = null;
                SignatureClassTypeSegment signatureClassTypeSegment = (SignatureClassTypeSegment) enumeration.nextElement();
                if (!signatureClassTypeSegment.startsInnerClassChain()) {
                    if (!signatureClassTypeSegment.isInnerClassSuffix()) {
                        string = signatureClassTypeSegment.getClassName();
                    }
                } else {
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append(signatureClassTypeSegment.getClassName());

                    for (SignatureClassTypeSegment signatureClassTypeSegment1 = signatureClassTypeSegment.getInnerSegment();
                         signatureClassTypeSegment1 != null;
                         signatureClassTypeSegment1 = signatureClassTypeSegment1.getInnerSegment()
                    ) {
                        stringBuffer.append("$");
                        stringBuffer.append(signatureClassTypeSegment1.getClassName());
                    }

                    string = stringBuffer.toString();
                }

                if (string != null) {
                    ProgramClass programClass1 = ClassHierarchyNode.findProgramClass(string);
                    if (programClass1 != null) {
                        set1.add(programClass1);
                    }
                }
            }
        }
    }

    @Override
    public final void writeRemapped(DataOutputStream dataOutputStream, Object object, Object object1) throws IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object1;
        Map map1 = (Map) object;
        ScriptEnvironment scriptEnvironment2 = scriptEnvironment1;
        Map map2 = map1;
        ScriptEnvironment scriptEnvironment3 = scriptEnvironment2;
        Map map3 = map2;
        DataOutputStream dataOutputStream1 = dataOutputStream;
        super.writeRemapped(dataOutputStream1, map3, scriptEnvironment3);
        ConstantUtf8 constantUtf8 = (ConstantUtf8) map1.get(this.signatureConstant);
        if (constantUtf8 != null) {
            dataOutputStream.writeShort(constantUtf8.getIndex());
        } else {
            dataOutputStream.writeShort(this.signatureConstant.getIndex());
        }
    }

    public static String remapSignature(String string, int ba, int bb, HashMap hashMap, ClassFileComponent classFileComponent) throws ZkmProcessingException {
        if (bb == 1) {
            return string;
        }

        GenericTree genericTree = parseSignatureTree(string, classFileComponent);
        if (genericTree == null) {
            return string;
        }

        Iterator iterator = genericTree.nodeIterator();

        while (iterator.hasNext()) {
            ObjectTreeNode objectTreeNode = (ObjectTreeNode) iterator.next();
            SignatureClassTypeSegment signatureClassTypeSegment = (SignatureClassTypeSegment) objectTreeNode.getValue();
            if (signatureClassTypeSegment.startsInnerClassChain()) {
                ArrayList arrayList = new ArrayList();
                arrayList.add(signatureClassTypeSegment);

                for (SignatureClassTypeSegment signatureClassTypeSegment2 = signatureClassTypeSegment.getInnerSegment();
                     signatureClassTypeSegment2 != null;
                     signatureClassTypeSegment2 = signatureClassTypeSegment2.getInnerSegment()
                ) {
                    arrayList.add(signatureClassTypeSegment2);
                }

                StringBuffer stringBuffer = new StringBuffer();

                for (int i = 0; i < arrayList.size(); i++) {
                    if (i > 0) {
                        stringBuffer.append("$");
                    }

                    stringBuffer.append(((SignatureClassTypeSegment) arrayList.get(i)).getClassName());
                }

                String string11 = stringBuffer.toString();
                String string3 = ClassFileBase.stripPackage(string11);
                String string4 = (String) ZkmUtils.mapOrSelf(string11, hashMap);
                ClassFileBase.getPackagePath(string4);
                String string5 = ClassFileBase.stripPackage(string4);
                boolean bl;
                if (ba != 0 && (ba != 2 || !string3.equals(string5))) {
                    bl = false;
                } else {
                    bl = true;
                }

                if (!string11.equals(string4) || !bl) {
                    StringTokenizer stringTokenizer = new StringTokenizer(string5, "$");
                    stringTokenizer.countTokens();
                    new StringTokenizer(string3, "$").countTokens();
                    if (stringTokenizer.countTokens() == 1) {
                        mergeInnerClassSegments(arrayList, iterator, string4);
                    } else if (bl) {
                        int bd = 0;
                        StringBuffer stringBuffer1 = null;
                        StringBuffer stringBuffer2 = null;

                        for (Iterator iterator1 = arrayList.iterator(); iterator1.hasNext(); bd++) {
                            SignatureClassTypeSegment signatureClassTypeSegment1 = (SignatureClassTypeSegment) iterator1.next();
                            String string6 = signatureClassTypeSegment1.getClassName();
                            if (bd == 0) {
                                String string7 = (String) ZkmUtils.mapOrSelf(string6, hashMap);
                                if (!string6.equals(string7)) {
                                    signatureClassTypeSegment1.setClassName(string7);
                                }

                                stringBuffer1 = new StringBuffer(string6);
                                stringBuffer2 = new StringBuffer(string7);
                            } else {
                                String string12 = stringBuffer1.toString();
                                String string8 = stringBuffer2.toString();
                                stringBuffer1.append("$");
                                stringBuffer1.append(string6);
                                String string9 = stringBuffer1.toString();
                                String string10 = ((String) ZkmUtils.mapOrSelf(string9, hashMap)).substring(string8.length() + 1);
                                string9.substring(string12.length() + 1);
                                stringBuffer2.append("$");
                                stringBuffer2.append(string10);
                                signatureClassTypeSegment1.setClassName(string10);
                            }
                        }
                    } else {
                        mergeInnerClassSegments(arrayList, iterator, string4);
                    }
                }
            } else if (!signatureClassTypeSegment.isInnerClassSuffix()) {
                String string1 = signatureClassTypeSegment.getClassName();
                if (string1 != null) {
                    String string2 = (String) ZkmUtils.mapOrSelf(string1, hashMap);
                    if (!string1.equals(string2)) {
                        signatureClassTypeSegment.setClassName(string2);
                    }
                }
            }
        }

        StringBuilder stringBuilder = new StringBuilder(string.length());
        Enumeration enumeration = genericTree.values();

        while (enumeration.hasMoreElements()) {
            stringBuilder.append(((SignatureClassTypeSegment) enumeration.nextElement()).getSignatureText());
        }

        return stringBuilder.toString();
    }

    @Override
    public void collectUsedConstants(char ba, int bb, UsedConstantsCollector usedConstantsCollector, char bc) {
        usedConstantsCollector.markUsed(this.nameConstant, this, this.getParent());
        this.signatureConstant.registerUsage(usedConstantsCollector, this, this.getParent());
    }

    public void setSignatureConstant(ConstantPoolEntry constantPoolEntry) throws ClassFileFormatException {
        if (!(constantPoolEntry instanceof ConstantUtf8)) {
            throw new ClassFileFormatException(this.getClassName() + " : " + "Invalid Signature Attribute");
        }

        this.signatureConstant = (ConstantUtf8) constantPoolEntry;
    }

    @Override
    public void replaceUtf8Constant(ConstantUtf8 constantUtf8, ConstantUtf8 constantUtf81) {
        if (this.signatureConstant == constantUtf8) {
            this.signatureConstant = constantUtf81;
        } else {
            super.replaceUtf8Constant(constantUtf8, constantUtf81);
        }
    }

    @Override
    public void remapClassNames(Object object, Object object1, Object object3, Object object2) throws ZkmProcessingException {
        int bb = (Integer) object;
        HashMap hashMap = (HashMap) object2;
        int ba = (Integer) object1;
        String string = this.signatureConstant.getValue();
        String string1 = remapSignature(string, bb, ba, hashMap, this);
        if (!string.equals(string1)) {
            this.signatureConstant.setValue(string1);
        }
    }

    public static GenericTree parseSignatureTree(String string, ClassFileComponent classFileComponent) throws ZkmProcessingException {
        GenericTree genericTree;
        try {
            genericTree = new GenericTree(new SignatureClassTypeSegment(""));
            int ba = ZkmStringUtils.indexOfAny(string, 0, classTypeStartChars);
            if (ba == -1) {
                return null;
            }

            int bb = 0;

            while (ba > -1) {
                char bc = string.charAt(ba);
                int bd = ZkmStringUtils.indexOfAny(string, ba + 1, identifierEndChars);
                if (bd == -1) {
                    String string3 = "Parse error in " + (classFileComponent != null ? classFileComponent.getLocationName() : "") + " : '" + string + "' : " + ba;
                    throw new ZkmProcessingException(string3);
                }

                char be = string.charAt(bd);
                if (be == ':') {
                    ba = ZkmStringUtils.indexOfAny(string, bd + 1, typeStartChars);
                } else {
                    char bf = string.charAt(bb);
                    String string1;
                    SignatureClassTypeSegment signatureClassTypeSegment;
                    String string6;
                    char bj;
                    if (bc == 'T') {
                        string1 = string.substring(bb, bd);
                        signatureClassTypeSegment = new SignatureClassTypeSegment(string1);
                        string6 = string1;
                        bj = '<';
                    } else {
                        string1 = string.substring(bb, ba + 1);
                        String string2 = string.substring(ba + 1, bd);
                        boolean bl = bc == '.';
                        String string4 = string2;
                        String string5 = string1;
                        signatureClassTypeSegment = new SignatureClassTypeSegment(string5, string4, bl);
                        string6 = string1;
                        bj = '<';
                    }

                    int bg = ZkmStringUtils.countChar(string6, bj);
                    int bh = ZkmStringUtils.countChar(string1, '>') - bg;

                    for (int i = 0; i < bh; i++) {
                        genericTree.moveToParent();
                    }

                    if (!genericTree.isAtRoot() && (bb <= 0 || bf != '<' || bh == 0)) {
                        genericTree.addSibling(signatureClassTypeSegment);
                    } else {
                        genericTree.addChild(signatureClassTypeSegment);
                    }

                    bb = bd;
                    ba = ZkmStringUtils.indexOfAny(string, bb, typeStartChars);
                    if (ba == -1) {
                        string1 = string.substring(bb);
                        SignatureClassTypeSegment signatureClassTypeSegment3 = new SignatureClassTypeSegment(string1);
                        genericTree.addSibling(signatureClassTypeSegment3);
                    }
                }
            }
        } catch (AssertionFailedException assertionFailedException) {
            throw assertionFailedException;
        } catch (ZkmRuntimeException zkmRuntimeException) {
            throw new ZkmProcessingException(
                    "Tree error in " + classFileComponent.getLocationName() + " : '" + string + "' : '" + zkmRuntimeException.getMessage() + "'"
            );
        }

        Iterator iterator = genericTree.nodeIterator();

        while (iterator.hasNext()) {
            ObjectTreeNode objectTreeNode = (ObjectTreeNode) iterator.next();
            SignatureClassTypeSegment signatureClassTypeSegment1 = null;
            Enumeration enumeration = objectTreeNode.childNodes();

            while (enumeration.hasMoreElements()) {
                ObjectTreeNode objectTreeNode1 = (ObjectTreeNode) enumeration.nextElement();
                SignatureClassTypeSegment signatureClassTypeSegment2 = (SignatureClassTypeSegment) objectTreeNode1.getValue();
                signatureClassTypeSegment2.setTreeNode(objectTreeNode1);
                if (signatureClassTypeSegment2.isInnerClassSuffix()) {
                    signatureClassTypeSegment1.setInnerSegment(signatureClassTypeSegment2);
                }

                signatureClassTypeSegment1 = signatureClassTypeSegment2;
            }
        }

        return genericTree;
    }

    public GenericTree getSignatureTree() throws ZkmProcessingException {
        return parseSignatureTree(this.signatureConstant.getValue(), this);
    }

    public SignatureAttribute(ClassFileComponent classFileComponent, int ba, String string, ClassFileInputStream classFileInputStream, ListMultimap listMultimap) throws ClassFileFormatException, IOException {
        super(classFileComponent, ba, string, classFileInputStream, listMultimap);
        int bb = classFileInputStream.readUnsignedShort();
        ConstantPoolEntry constantPoolEntry = this.getConstantPoolEntry(bb);
        this.setSignatureConstant(constantPoolEntry);
        listMultimap.addValue(this.signatureConstant, this);
    }
}
