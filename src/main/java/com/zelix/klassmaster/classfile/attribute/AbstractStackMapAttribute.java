package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.constpool.ConstantUtf8;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.classfile.insn.InstructionUsageClearFlag;
import com.zelix.klassmaster.classfile.insn.LabelInstruction;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.SetMultiMap;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

public abstract class AbstractStackMapAttribute extends Attribute {
    public byte[] rawBytes;
    public StackMapFrame[] frames;
    public int frameCount;
    public boolean parsed = true;

    public abstract void writeFramesRemapped(DataOutputStream dataOutputStream, Map map1) throws IOException;

    public AbstractStackMapAttribute(
            ClassFileComponent classFileComponent, int ba, String string, ClassFileInputStream classFileInputStream, ListMultimap listMultimap
    ) throws IOException {
        super(classFileComponent, ba, string, classFileInputStream, listMultimap);
    }

    @Override
    public final ConstantUtf8 getNameConstant() {
        return this.nameConstant;
    }

    public final void setFrames(StackMapFrame[] stackMapFrames) {
        this.frameCount = stackMapFrames.length;
        this.frames = stackMapFrames;
    }

    @Override
    public void collectUsedConstants(char bc, int ba, UsedConstantsCollector usedConstantsCollector, char bd) {
        usedConstantsCollector.markUsed(this.nameConstant, this, this.getParent());
        int bb = 0;

        while (true) {
            if (bb < this.frameCount) {
                this.frames[bb].collectUsedConstants('\u0000', 1103830477, usedConstantsCollector, '鞍');
            } else if (ba >= 0) {
                return;
            }

            bb++;
        }
    }

    @Override
    public final void writeRemapped(DataOutputStream dataOutputStream, Object object, Object object1) throws IOException {
        Map map1 = (Map) object;
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object1;
        ScriptEnvironment scriptEnvironment2 = scriptEnvironment1;
        Map map2 = map1;
        ScriptEnvironment scriptEnvironment3 = scriptEnvironment2;
        Map map3 = map2;
        DataOutputStream dataOutputStream1 = dataOutputStream;
        super.writeRemapped(dataOutputStream1, map3, scriptEnvironment3);
        if (this.parsed) {
            this.writeFramesRemapped(dataOutputStream, map1);
        } else {
            dataOutputStream.write(this.rawBytes);
        }
    }

    public void removeFramesAtDeadLabels(HashSet hashSet, SetMultiMap setMultiMap) {
        if (this.parsed && hashSet.size() > 0) {
            ArrayList arrayList = new ArrayList(this.frameCount);

            for (int i = 0; i < this.frameCount; i++) {
                LabelInstruction labelInstruction = this.frames[i].getLabel();
                if (!hashSet.contains(labelInstruction)) {
                    arrayList.add(this.frames[i]);
                } else {
                    setMultiMap.removeValue(labelInstruction, this.frames[i]);
                    labelInstruction.clearUsageFlag(InstructionUsageClearFlag.CLEAR_USAGE_STACK_MAP);
                }
            }

            if (arrayList.size() < this.frames.length) {
                StackMapFrame[] stackMapFrames = new StackMapFrame[arrayList.size()];
                this.frames = ((com.zelix.klassmaster.classfile.attribute.StackMapFrame[]) (arrayList.toArray(stackMapFrames)));
                this.frameCount = this.frames.length;
                this.getLength();
            }
        }
    }

    public final void collectLabelReferences(SetMultiMap setMultiMap) {
        for (int i = 0; i < this.frameCount; i++) {
            this.frames[i].registerLabelTargets(setMultiMap);
        }
    }

    @Override
    public final int getLength() {
        int ba = 2;

        for (int i = 0; i < this.frameCount; i++) {
            ba += this.frames[i].getFrameSize();
        }

        this.length = ba;
        return ba;
    }

    public int getFrameCount() {
        return this.frameCount;
    }

    public abstract void writeFrames(DataOutputStream dataOutputStream) throws IOException;

    public AbstractStackMapAttribute(ClassFileComponent classFileComponent, ConstantUtf8 constantUtf8) {
        super(classFileComponent, constantUtf8, 0);
    }

    @Override
    public final void write(DataOutputStream dataOutputStream) throws IOException {
        super.write(dataOutputStream);
        if (this.parsed) {
            this.writeFrames(dataOutputStream);
        } else {
            dataOutputStream.write(this.rawBytes);
        }
    }
}
