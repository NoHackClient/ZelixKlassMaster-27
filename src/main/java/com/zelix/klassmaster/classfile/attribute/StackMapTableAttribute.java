package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.constpool.ConstantUtf8;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class StackMapTableAttribute extends AbstractStackMapAttribute {
    public StackMapTableAttribute(
            ClassFileComponent classFileComponent,
            int ba,
            String string,
            ClassFileInputStream classFileInputStream,
            ListMultimap listMultimap,
            ListMultimap listMultimap1,
            PrintWriter printWriter,
            ListMultimap listMultimap2
    ) throws IOException {
        super(classFileComponent, ba, string, classFileInputStream, listMultimap);
        byte[] bb = new byte[this.length];
        classFileInputStream.read(bb);
        ClassFileInputStream classFileInputStream1 = ClassFileInputStream.fromBytes(bb, false);
        if (this.length >= 2) {
            this.frameCount = classFileInputStream1.readUnsignedShort();
            HashMap hashMap = ZkmUtils.createHashMap();
            HashMap hashMap1 = ZkmUtils.createHashMap();
            MutableInt mutableInt = new MutableInt(-1);
            this.frames = new StackMapTableFrame[this.frameCount];

            for (int i = 0; i < this.frameCount; i++) {
                this.frames[i] = StackMapTableFrame.readFrame(
                        this, classFileInputStream1, listMultimap2, listMultimap1, printWriter, mutableInt, hashMap, hashMap1
                );
                if (!this.frames[i].isValid()) {
                    super.parsed = false;
                }
            }

            if (!super.parsed) {
                super.rawBytes = bb;
            }
        } else {
            super.parsed = false;
            printWriter.println("ERROR: " + this.getLocationName() + " : " + "Invalid StackMapTable attribute" + " (B)");
            super.rawBytes = new byte[this.length];
            classFileInputStream1.read(super.rawBytes);
        }
    }

    public String getMethodDescription() {
        return ((CodeAttributeBody) this.getParent()).getMethodDescription();
    }

    @Override
    public void writeFrames(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeShort(this.frameCount);
        new MutableInt(-1);

        for (int i = 0; i < this.frameCount; i++) {
            ((StackMapTableFrame) this.frames[i]).writeFrame(dataOutputStream);
        }
    }

    @Override
    public void writeFramesRemapped(DataOutputStream dataOutputStream, Map map1) throws IOException {
        dataOutputStream.writeShort(this.frameCount);
        new MutableInt(-1);

        for (int i = 0; i < this.frameCount; i++) {
            ((StackMapTableFrame) this.frames[i]).writeRemapped(dataOutputStream, map1);
        }
    }

    public StackMapTableAttribute(ClassFileComponent classFileComponent, ConstantUtf8 constantUtf8) {
        super(classFileComponent, constantUtf8);
    }
}
