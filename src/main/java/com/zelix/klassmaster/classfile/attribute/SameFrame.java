package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.classfile.insn.LabelInstruction;
import com.zelix.klassmaster.util.IntegerCache;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.MutableInt;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class SameFrame extends StackMapTableFrame {
    public final int frameType;

    @Override
    public int getFrameType() {
        return this.frameType;
    }

    @Override
    public int getFrameSize() {
        return 1;
    }

    public SameFrame(int frameType, ClassFileComponent classFileComponent, ListMultimap listMultimap, MutableInt mutableInt) {
        super(classFileComponent);
        this.frameType = frameType;
        IntegerCache integerCache1 = IntegerCache.getInstance();
        int bb = this.frameType;
        int value = mutableInt.getValue();
        if (value == -1) {
            this.offset = bb;
        } else {
            this.offset = value + 1 + bb;
        }

        mutableInt.setValue(this.offset);
        listMultimap.addValue(integerCache1.valueOf(this.offset), this);
    }

    public SameFrame(StackMapTableAttribute stackMapTableAttribute, int frameType, LabelInstruction labelInstruction) {
        super(stackMapTableAttribute);
        this.frameType = frameType;
        this.label = labelInstruction;
        this.offset = labelInstruction.getOffset();
        this.valid = true;
    }

    @Override
    public void writeRemapped(DataOutputStream dataOutputStream, Map map1) throws IOException {
        dataOutputStream.writeByte(this.frameType);
    }

    @Override
    public final void collectUsedConstants(char ba, int bb, UsedConstantsCollector usedConstantsCollector, char bc) {
    }
}
