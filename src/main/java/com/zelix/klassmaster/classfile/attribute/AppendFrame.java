package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.classfile.insn.LabelInstruction;
import com.zelix.klassmaster.util.IntegerCache;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.MutableInt;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class AppendFrame extends StackMapTableFrame {
    public final int frameType;
    public final int offsetDelta;
    public final VerificationTypeInfo[] appendedLocals;

    @Override
    public int getFrameType() {
        return this.frameType;
    }

    public AppendFrame(
            int frameType,
            ClassFileComponent classFileComponent,
            ClassFileInputStream classFileInputStream,
            ListMultimap listMultimap,
            ListMultimap listMultimap1,
            PrintWriter printWriter,
            MutableInt mutableInt,
            Map map1,
            Map map2
    ) throws IOException {
        super(classFileComponent);
        this.frameType = frameType;
        IntegerCache integerCache1 = IntegerCache.getInstance();
        this.offsetDelta = classFileInputStream.readUnsignedShort();
        int bb = this.frameType - 251;
        this.appendedLocals = new VerificationTypeInfo[bb];

        for (int i = 0; i < bb; i++) {
            this.appendedLocals[i] = VerificationTypeInfo.readTypeInfo(
                    (StackMapTableAttribute) classFileComponent, classFileInputStream, listMultimap, listMultimap1, printWriter, map1, map2, integerCache1
            );
            if (!this.appendedLocals[i].isValid()) {
                this.valid = false;
            }
        }

        int value = mutableInt.getValue();
        if (value == -1) {
            this.offset = this.offsetDelta;
        } else {
            this.offset = value + 1 + this.offsetDelta;
        }

        mutableInt.setValue(this.offset);
        listMultimap.addValue(integerCache1.valueOf(this.offset), this);
    }

    public AppendFrame(
            StackMapTableAttribute stackMapTableAttribute, int frameType, int offsetDelta, LabelInstruction labelInstruction, VerificationTypeInfo[] verificationTypeInfos
    ) {
        super(stackMapTableAttribute);
        this.frameType = frameType;
        this.label = labelInstruction;
        this.offsetDelta = offsetDelta;
        this.appendedLocals = verificationTypeInfos;
    }

    @Override
    public int getFrameSize() {
        int ba = 1;
        ba += 2;

        for (VerificationTypeInfo verificationTypeInfo : this.appendedLocals) {
            ba += verificationTypeInfo.getByteSize();
        }

        return ba;
    }

    public VerificationTypeInfo[] getAppendedLocals() {
        return this.appendedLocals.clone();
    }

    @Override
    public final void collectUsedConstants(char bc, int bd, UsedConstantsCollector usedConstantsCollector, char be) {
        VerificationTypeInfo[] verificationTypeInfos = this.appendedLocals;
        int ba = verificationTypeInfos.length;

        for (int i = 0; i < ba; i++) {
            verificationTypeInfos[i].collectUsedConstants('\u0000', 1103830477, usedConstantsCollector, '鞍');
        }
    }

    @Override
    public void writeRemapped(DataOutputStream dataOutputStream, Map map1) throws IOException {
        dataOutputStream.writeByte(this.frameType);
        dataOutputStream.writeShort(this.offsetDelta);
        VerificationTypeInfo[] verificationTypeInfos = this.appendedLocals;
        int ba = verificationTypeInfos.length;

        for (int i = 0; i < ba; i++) {
            verificationTypeInfos[i].writeRemapped(dataOutputStream, map1);
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
