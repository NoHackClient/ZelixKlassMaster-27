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
import java.util.Map;

public class ChopFrame extends StackMapTableFrame {
    public final int frameType;
    public final int offsetDelta;

    public ChopFrame(StackMapTableAttribute stackMapTableAttribute, int frameType, int offsetDelta, LabelInstruction labelInstruction) {
        super(stackMapTableAttribute);
        this.frameType = frameType;
        this.label = labelInstruction;
        this.offsetDelta = offsetDelta;
    }

    @Override
    public void writeRemapped(DataOutputStream dataOutputStream, Map map1) throws IOException {
        dataOutputStream.writeByte(this.frameType);
        dataOutputStream.writeShort(this.offsetDelta);
    }

    @Override
    public final void collectUsedConstants(char ba, int bb, UsedConstantsCollector usedConstantsCollector, char bc) {
    }

    @Override
    public int getFrameSize() {
        byte ba = 1;
        return ba + 2;
    }

    public ChopFrame(int frameType, ClassFileComponent classFileComponent, ClassFileInputStream classFileInputStream, ListMultimap listMultimap, MutableInt mutableInt) throws IOException {
        super(classFileComponent);
        this.frameType = frameType;
        IntegerCache integerCache1 = IntegerCache.getInstance();
        this.offsetDelta = classFileInputStream.readUnsignedShort();
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
        return this.frameType;
    }
}
