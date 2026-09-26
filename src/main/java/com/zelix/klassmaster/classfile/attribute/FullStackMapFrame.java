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

public class FullStackMapFrame extends StackMapTableFrame {
    private final int frameType = 255;
    public final int offsetDelta;
    private final VerificationTypeInfo[] stackItems;
    private final VerificationTypeInfo[] locals;

    @Override
    public int getFrameSize() {
        int ba = 1;
        int bb = this.locals.length;
        int bc = this.stackItems.length;
        ba += 2;
        ba += 2;

        for (int i = 0; i < bb; i++) {
            ba += this.locals[i].getByteSize();
        }

        ba += 2;

        for (int i = 0; i < bc; i++) {
            ba += this.stackItems[i].getByteSize();
        }

        return ba;
    }

    @Override
    public void collectUsedConstants(char ba, int be, UsedConstantsCollector usedConstantsCollector, char bf) {
        int bb = this.locals.length;
        int bc = this.stackItems.length;

        int bd;
        for (bd = 0; bd < bb; bd++) {
            VerificationTypeInfo verificationTypeInfo1 = this.locals[bd];
            UsedConstantsCollector usedConstantsCollector1 = usedConstantsCollector;
            verificationTypeInfo1.collectUsedConstants('\u0000', 1103830477, usedConstantsCollector1, '鞍');
        }

        if (ba >= 0) {
            bd = 0;
        }

        while (bd < bc) {
            VerificationTypeInfo verificationTypeInfo = this.stackItems[bd];
            UsedConstantsCollector usedConstantsCollector2 = usedConstantsCollector;
            verificationTypeInfo.collectUsedConstants('\u0000', 1103830477, usedConstantsCollector2, '鞍');
            bd++;
        }
    }

    @Override
    public void writeRemapped(DataOutputStream dataOutputStream, Map map1) throws IOException {
        int ba = this.locals.length;
        int bb = this.stackItems.length;
        dataOutputStream.writeByte(255);
        dataOutputStream.writeShort(this.offsetDelta);
        dataOutputStream.writeShort(ba);

        for (int i = 0; i < ba; i++) {
            this.locals[i].writeRemapped(dataOutputStream, map1);
        }

        dataOutputStream.writeShort(bb);

        for (int i = 0; i < bb; i++) {
            this.stackItems[i].writeRemapped(dataOutputStream, map1);
        }
    }

    public FullStackMapFrame(
            StackMapTableAttribute stackMapTableAttribute,
            int offsetDelta,
            LabelInstruction labelInstruction,
            VerificationTypeInfo[] verificationTypeInfos,
            VerificationTypeInfo[] verificationTypeInfos1
    ) {
        super(stackMapTableAttribute);
        this.label = labelInstruction;
        this.offsetDelta = offsetDelta;
        this.offset = labelInstruction.getOffset();
        this.stackItems = verificationTypeInfos;
        this.locals = verificationTypeInfos1;
    }

    public FullStackMapFrame(
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
        int ba = classFileInputStream.readUnsignedShort();
        this.locals = new VerificationTypeInfo[ba];

        for (int i = 0; i < ba; i++) {
            this.locals[i] = VerificationTypeInfo.readTypeInfo(
                    (StackMapTableAttribute) classFileComponent, classFileInputStream, listMultimap, listMultimap1, printWriter, map1, map2, integerCache1
            );
            if (!this.locals[i].isValid()) {
                this.valid = false;
            }
        }

        int bd = classFileInputStream.readUnsignedShort();
        this.stackItems = new VerificationTypeInfo[bd];

        for (int i = 0; i < bd; i++) {
            this.stackItems[i] = VerificationTypeInfo.readTypeInfo(
                    (StackMapTableAttribute) classFileComponent, classFileInputStream, listMultimap, listMultimap1, printWriter, map1, map2, integerCache1
            );
            if (!this.stackItems[i].isValid()) {
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

    @Override
    public int getFrameType() {
        return 255;
    }
}
