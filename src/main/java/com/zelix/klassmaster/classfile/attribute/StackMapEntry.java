package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.constpool.ConstantPool;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.classfile.insn.LabelInstruction;
import com.zelix.klassmaster.classfile.insn.StackFrameState;
import com.zelix.klassmaster.classfile.insn.VerifierType;
import com.zelix.klassmaster.util.IntegerCache;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.VisitableNode;
import com.zelix.klassmaster.util.ZkmAssert;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StackMapEntry extends StackMapFrame {
    public VerificationTypeInfo[] locals;
    public VerificationTypeInfo[] stackItems;

    @Override
    public int getFrameSize() {
        int ba = 6;
        int bb = 0;
        int bc = 0;

        for (VerificationTypeInfo[] verificationTypeInfos1 = this.locals; bc < verificationTypeInfos1.length; verificationTypeInfos1 = this.locals) {
            ba += this.locals[bb].getByteSize();
            bc = ++bb;
        }

        bb = 0;
        bc = 0;

        for (VerificationTypeInfo[] verificationTypeInfos = this.stackItems; bc < verificationTypeInfos.length; verificationTypeInfos = this.stackItems) {
            ba += this.stackItems[bb].getByteSize();
            bc = ++bb;
        }

        return ba;
    }

    @Override
    public String getHolderTypeName(Object object, Object object1, Object object2) {
        return "StackMap";
    }

    
    @Override
    public final void collectUsedConstants(char ba, int bb, UsedConstantsCollector usedConstantsCollector, char bc) {
        VisitableNode[] visitableNodes2 = ClassFileComponent.getFlowGuardNodes();
        int bd = this.locals.length;
        VisitableNode[] visitableNodes1 = visitableNodes2;
        int be = 0;

        label41:
        while (true) {
            if (be >= bd) {
                visitableNodes2 = this.stackItems;
                break;
            }

            if (bc > 0) {
                this.locals[be].collectUsedConstants('\u0000', 1103830477, usedConstantsCollector, '鞍');
            } else {
                if (this.locals != null) {
                    continue;
                }

                if (ba >= 0) {
                    visitableNodes2 = this.stackItems;
                    break;
                }
            }

            do {
                be++;
                if (visitableNodes1 != null) {
                    continue label41;
                }
            } while (ba < 0);

            visitableNodes2 = this.stackItems;
            break;
        }

        be = visitableNodes2.length;
        int bf = 0;

        while (true) {
            if (bf < be) {
                this.stackItems[bf].collectUsedConstants('\u0000', 1103830477, usedConstantsCollector, '鞍');
            } else if (bb >= 0) {
                return;
            }

            bf++;
        }
    }

    public StackMapEntry(
            StackMapAttribute stackMapAttribute,
            LabelInstruction labelInstruction,
            StackFrameState stackFrameState,
            ConstantPool constantPool1,
            Set set1,
            List list1,
            boolean bl,
            Map map1,
            Map map2
    ) {
        super(stackMapAttribute);
        this.label = labelInstruction;
        VerifierType[] verifierTypes;
        if (bl) {
            verifierTypes = stackFrameState.getLocalsMaskedByLiveness();
        } else {
            verifierTypes = stackFrameState.getLocals();
        }

        this.locals = VerificationTypeInfo.convertVerifierTypes(stackMapAttribute, verifierTypes, constantPool1, set1, list1, true, map1, map2);
        this.stackItems = VerificationTypeInfo.convertVerifierTypes(stackMapAttribute, stackFrameState.getStack(), constantPool1, set1, list1, false, map1, map2);
        this.valid = true;
    }

    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
        ZkmAssert.assertTrue(this.valid, new String[]{"Invalid StackMap Entry in " + this.getDottedClassName()});
        VerificationTypeInfo[] verificationTypeInfos;
        if (this.label != null) {
            dataOutputStream.writeShort(this.label.getOffset());
            verificationTypeInfos = this.locals;
        } else {
            dataOutputStream.writeShort(this.offset);
            verificationTypeInfos = this.locals;
        }

        int ba = verificationTypeInfos.length;
        dataOutputStream.writeShort(ba);

        for (int i = 0; i < ba; i++) {
            this.locals[i].writeTo(dataOutputStream);
        }

        int bd = this.stackItems.length;
        dataOutputStream.writeShort(bd);

        for (int i = 0; i < bd; i++) {
            this.stackItems[i].writeTo(dataOutputStream);
        }
    }

    public StackMapEntry(
            ClassFileComponent classFileComponent,
            ClassFileInputStream classFileInputStream,
            ListMultimap listMultimap,
            ListMultimap listMultimap1,
            PrintWriter printWriter
    ) throws IOException {
        super(classFileComponent);
        HashMap hashMap = ZkmUtils.createHashMap();
        HashMap hashMap1 = ZkmUtils.createHashMap();
        IntegerCache integerCache1 = IntegerCache.getInstance();
        this.offset = classFileInputStream.readUnsignedShort();
        listMultimap.addValue(integerCache1.valueOf(this.offset), this);
        int ba = classFileInputStream.readUnsignedShort();
        this.locals = new VerificationTypeInfo[ba];

        for (int i = 0; i < ba; i++) {
            this.locals[i] = VerificationTypeInfo.readTypeInfo(
                    (AbstractStackMapAttribute) classFileComponent, classFileInputStream, listMultimap, listMultimap1, printWriter, hashMap, hashMap1, integerCache1
            );
            if (!this.locals[i].isValid()) {
                this.valid = false;
            }
        }

        int bd = classFileInputStream.readUnsignedShort();
        this.stackItems = new VerificationTypeInfo[bd];

        for (int i = 0; i < bd; i++) {
            this.stackItems[i] = VerificationTypeInfo.readTypeInfo(
                    (AbstractStackMapAttribute) classFileComponent, classFileInputStream, listMultimap, listMultimap1, printWriter, hashMap, hashMap1, integerCache1
            );
            if (!this.stackItems[i].isValid()) {
                this.valid = false;
            }
        }
    }

    public void writeRemapped(DataOutputStream dataOutputStream, Map map1) throws IOException {
        ZkmAssert.assertTrue(this.valid, new String[]{"Invalid StackMap Entry in " + this.getDottedClassName()});
        VerificationTypeInfo[] verificationTypeInfos;
        if (this.label != null) {
            dataOutputStream.writeShort(this.label.getOffset());
            verificationTypeInfos = this.locals;
        } else {
            dataOutputStream.writeShort(this.offset);
            verificationTypeInfos = this.locals;
        }

        int ba = verificationTypeInfos.length;
        dataOutputStream.writeShort(ba);

        for (int i = 0; i < ba; i++) {
            this.locals[i].writeRemapped(dataOutputStream, map1);
        }

        int bd = this.stackItems.length;
        dataOutputStream.writeShort(bd);

        for (int i = 0; i < bd; i++) {
            this.stackItems[i].writeRemapped(dataOutputStream, map1);
        }
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
    }
}
