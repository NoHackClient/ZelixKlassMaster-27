package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.constpool.ConstantPool;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.classfile.insn.UninitializedType;
import com.zelix.klassmaster.classfile.insn.VerifierType;
import com.zelix.klassmaster.util.IntegerCache;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.ZkmAssert;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

public class VerificationTypeInfo extends ClassFileComponent {
    public boolean valid = true;
    public final int tag;

    public final boolean isTop() {
        return this.tag == 0;
    }

    public static VerificationTypeInfo readTypeInfo(
            AbstractStackMapAttribute abstractStackMapAttribute,
            ClassFileInputStream classFileInputStream,
            ListMultimap listMultimap,
            ListMultimap listMultimap1,
            PrintWriter printWriter,
            Map map1,
            Map map2,
            IntegerCache integerCache1
    ) throws IOException {
        int ba = classFileInputStream.readUnsignedByte();
        Integer integer = integerCache1.valueOf(ba);
        VerificationTypeInfo verificationTypeInfo;
        switch (ba) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                if (map1.containsKey(integer)) {
                    verificationTypeInfo = (VerificationTypeInfo) map1.get(integer);
                } else {
                    verificationTypeInfo = new VerificationTypeInfo(abstractStackMapAttribute, ba);
                    map1.put(integer, verificationTypeInfo);
                }
                break;
            case 7:
                int bb = classFileInputStream.readUnsignedShort();
                Integer integer1 = integerCache1.valueOf(bb);
                if (map2.containsKey(integer1)) {
                    verificationTypeInfo = (VerificationTypeInfo) map2.get(integer1);
                } else {
                    verificationTypeInfo = new ObjectVerificationType(abstractStackMapAttribute, ba, bb, listMultimap1, printWriter);
                    map2.put(integer1, verificationTypeInfo);
                }
                break;
            case 8:
                verificationTypeInfo = new UninitializedVerificationType(abstractStackMapAttribute, ba, classFileInputStream, listMultimap);
                break;
            default:
                verificationTypeInfo = new VerificationTypeInfo(abstractStackMapAttribute, ba);
                verificationTypeInfo.valid = false;
                printWriter.println("ERROR: " + verificationTypeInfo.getLocationName() + " : " + "Invalid StackMap attribute" + " '" + ba + "' (D)");
        }

        return verificationTypeInfo;
    }

    public static VerificationTypeInfo[] convertVerifierTypes(
            AbstractStackMapAttribute abstractStackMapAttribute,
            VerifierType[] verifierTypes,
            ConstantPool constantPool1,
            Set set1,
            List list1,
            boolean bl,
            Map map1,
            Map map2
    ) {
        IntegerCache integerCache1 = IntegerCache.getInstance();
        ArrayList arrayList = new ArrayList(verifierTypes.length);

        for (int i = 0; i < verifierTypes.length; i++) {
            VerifierType verifierType = verifierTypes[i];
            if (!verifierType.isWideSecondSlot()) {
                VerificationTypeInfo verificationTypeInfo;
                if (verifierType == VerifierType.TOP) {
                    verificationTypeInfo = getOrCreateSimple(abstractStackMapAttribute, 0, map1, integerCache1);
                } else if (verifierType == VerifierType.BYTE
                        || verifierType == VerifierType.CHAR
                        || verifierType == VerifierType.SHORT
                        || verifierType == VerifierType.INT) {
                    verificationTypeInfo = getOrCreateSimple(abstractStackMapAttribute, 1, map1, integerCache1);
                } else if (verifierType == VerifierType.FLOAT) {
                    verificationTypeInfo = getOrCreateSimple(abstractStackMapAttribute, 2, map1, integerCache1);
                } else if (verifierType == VerifierType.DOUBLE) {
                    verificationTypeInfo = getOrCreateSimple(abstractStackMapAttribute, 3, map1, integerCache1);
                } else if (verifierType == VerifierType.LONG) {
                    verificationTypeInfo = getOrCreateSimple(abstractStackMapAttribute, 4, map1, integerCache1);
                } else if (verifierType == VerifierType.NULL) {
                    verificationTypeInfo = getOrCreateSimple(abstractStackMapAttribute, 5, map1, integerCache1);
                } else if (verifierType.isInitialized()) {
                    verificationTypeInfo = ObjectVerificationType.getOrCreateCached(
                            abstractStackMapAttribute, verifierType.getDescriptor(), constantPool1, set1, list1, map2
                    );
                } else if (verifierType.isUninitializedThis()) {
                    verificationTypeInfo = getOrCreateSimple(abstractStackMapAttribute, 6, map1, integerCache1);
                } else {
                    verificationTypeInfo = new UninitializedVerificationType(abstractStackMapAttribute, ((UninitializedType) verifierType).getNewInstruction());
                }

                arrayList.add(verificationTypeInfo);
            }
        }

        if (bl) {
            ListIterator listIterator = arrayList.listIterator();

            while (listIterator.hasNext()) {
                listIterator.next();
            }

            while (listIterator.hasPrevious()) {
                VerificationTypeInfo verificationTypeInfo1 = (VerificationTypeInfo) listIterator.previous();
                if (!verificationTypeInfo1.isTop()) {
                    break;
                }

                listIterator.remove();
            }
        }

        int bb = arrayList.size();
        VerificationTypeInfo[] verificationTypeInfos = new VerificationTypeInfo[bb];
        arrayList.toArray(verificationTypeInfos);
        return verificationTypeInfos;
    }

    public VerificationTypeInfo(ClassFileComponent classFileComponent, int tag) {
        super(classFileComponent);
        this.tag = tag;
    }

    public static VerificationTypeInfo getOrCreateSimple(AbstractStackMapAttribute abstractStackMapAttribute, int ba, Map map1, IntegerCache integerCache1) {
        Integer integer = integerCache1.valueOf(ba);
        VerificationTypeInfo verificationTypeInfo;
        if (map1.containsKey(integer)) {
            verificationTypeInfo = (VerificationTypeInfo) map1.get(integer);
        } else {
            verificationTypeInfo = new VerificationTypeInfo(abstractStackMapAttribute, ba);
            map1.put(integer, verificationTypeInfo);
        }

        return verificationTypeInfo;
    }

    public final boolean isValid() {
        return this.valid;
    }

    public boolean isSameType(VerificationTypeInfo verificationTypeInfo1) {
        return this.tag == verificationTypeInfo1.tag;
    }

    public final void writeTo(DataOutputStream dataOutputStream) throws IOException {
        this.writeRemapped(dataOutputStream, null);
    }

    public final String getHolderTypeName(Object object, Object object1, Object object2) {
        return "StackMap Type";
    }

    public final int getByteSize() {
        switch (this.tag) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                return 1;
            case 7:
                return 3;
            case 8:
                return 3;
            default:
                return 1;
        }
    }

    public boolean isCategory2() {
        switch (this.tag) {
            case 0:
            case 1:
            case 2:
            case 5:
            case 6:
            case 7:
            case 8:
                return false;
            case 3:
            case 4:
                return true;
            default:
                return false;
        }
    }

    @Override
    public void collectUsedConstants(char ba, int bb, UsedConstantsCollector usedConstantsCollector, char bc) {
    }

    public void writeRemapped(DataOutputStream dataOutputStream, Map map1) throws IOException {
        if (!this.valid) {
            ZkmAssert.assertTrue(false, new String[]{"Invalid StackMap Entry in " + this.getDottedClassName()});
        }

        dataOutputStream.writeByte(this.tag);
        switch (this.tag) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
        }
    }
}
