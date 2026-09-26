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

public class SameLocalsOneStackItemFrame extends StackMapTableFrame {
    public final int frameType;
    public final VerificationTypeInfo stackItem;

    @Override
    public int getFrameType() {
        return this.frameType;
    }

    public SameLocalsOneStackItemFrame(
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
        int bb = this.frameType - 64;
        this.stackItem = VerificationTypeInfo.readTypeInfo(
                (StackMapTableAttribute) classFileComponent, classFileInputStream, listMultimap, listMultimap1, printWriter, map1, map2, integerCache1
        );
        if (!this.stackItem.isValid()) {
            this.valid = false;
        }

        int value = mutableInt.getValue();
        if (value == -1) {
            this.offset = bb;
        } else {
            this.offset = value + 1 + bb;
        }

        mutableInt.setValue(this.offset);
        listMultimap.addValue(integerCache1.valueOf(this.offset), this);
    }

    @Override
    public final void collectUsedConstants(char ba, int bb, UsedConstantsCollector usedConstantsCollector, char bc) {
        long bg = ((long) ba << 48 | (long) bb << 32 >>> 16 | (long) bc << 48 >>> 48) ^ 0L;
        int bd = (int) ((((long) ba << 48 | (long) bb << 32 >>> 16 | (long) bc << 48 >>> 48) ^ 0L) >>> 48);
        int be = (int) ((((long) ba << 48 | (long) bb << 32 >>> 16 | (long) bc << 48 >>> 48) ^ 0L) << 16 >>> 32);
        int bf = (int) (bg << 48 >>> 48);
        this.stackItem.collectUsedConstants((char) bd, be, usedConstantsCollector, (char) bf);
    }

    @Override
    public int getFrameSize() {
        return 1 + this.stackItem.getByteSize();
    }

    public SameLocalsOneStackItemFrame(
            StackMapTableAttribute stackMapTableAttribute, int frameType, LabelInstruction labelInstruction, VerificationTypeInfo verificationTypeInfo
    ) {
        super(stackMapTableAttribute);
        this.label = labelInstruction;
        this.frameType = frameType;
        this.stackItem = verificationTypeInfo;
    }

    @Override
    public void writeRemapped(DataOutputStream dataOutputStream, Map map1) throws IOException {
        dataOutputStream.writeByte(this.frameType);
        this.stackItem.writeRemapped(dataOutputStream, map1);
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
