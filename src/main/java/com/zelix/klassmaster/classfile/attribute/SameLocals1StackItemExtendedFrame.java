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

public class SameLocals1StackItemExtendedFrame extends StackMapTableFrame {
    public final int offsetDelta;
    public final VerificationTypeInfo stackItem;

    public SameLocals1StackItemExtendedFrame(
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
        IntegerCache integerCache1 = IntegerCache.getInstance();
        this.offsetDelta = classFileInputStream.readUnsignedShort();
        this.stackItem = VerificationTypeInfo.readTypeInfo(
                (StackMapTableAttribute) classFileComponent, classFileInputStream, listMultimap, listMultimap1, printWriter, map1, map2, integerCache1
        );
        if (!this.stackItem.isValid()) {
            this.valid = false;
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

    @Override
    public final void collectUsedConstants(char ba, int bb, UsedConstantsCollector usedConstantsCollector, char bc) {
        this.stackItem.collectUsedConstants('\u0000', 1103830477, usedConstantsCollector, '鞍');
    }

    @Override
    public int getFrameSize() {
        int ba = 1;
        ba += 2;
        return ba + this.stackItem.getByteSize();
    }

    @Override
    public int getFrameType() {
        return 247;
    }

    public SameLocals1StackItemExtendedFrame(
            StackMapTableAttribute stackMapTableAttribute, int offsetDelta, LabelInstruction labelInstruction, VerificationTypeInfo verificationTypeInfo
    ) {
        super(stackMapTableAttribute);
        this.label = labelInstruction;
        this.offsetDelta = offsetDelta;
        this.stackItem = verificationTypeInfo;
    }

    @Override
    public void writeRemapped(DataOutputStream dataOutputStream, Map map1) throws IOException {
        dataOutputStream.writeByte(this.getFrameType());
        dataOutputStream.writeShort(this.offsetDelta);
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
