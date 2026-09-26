package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.constpool.ConstantUtf8;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class StackMapAttribute extends AbstractStackMapAttribute {
    public StackMapAttribute(
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
        if (this.length >= 2) {
            this.frameCount = classFileInputStream.readUnsignedShort();
            this.frames = new StackMapEntry[this.frameCount];

            for (int i = 0; i < this.frameCount; i++) {
                this.frames[i] = new StackMapEntry(this, classFileInputStream, listMultimap2, listMultimap1, printWriter);
                if (!this.frames[i].isValid()) {
                    super.parsed = false;
                }
            }

            if (!super.parsed) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(this.length);
                DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
                this.writeFrames(dataOutputStream);
                super.rawBytes = byteArrayOutputStream.toByteArray();
                this.frames = null;
            }
        } else {
            super.parsed = false;
            printWriter.println("ERROR: " + this.getLocationName() + " : " + "Invalid StackMap attribute" + " (A)");
            super.rawBytes = new byte[this.length];
            classFileInputStream.read(super.rawBytes);
        }
    }

    @Override
    public void writeFrames(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeShort(this.frameCount);

        for (int i = 0; i < this.frameCount; i++) {
            ((StackMapEntry) this.frames[i]).writeTo(dataOutputStream);
        }
    }

    public StackMapAttribute(ClassFileComponent classFileComponent, ConstantUtf8 constantUtf8) {
        super(classFileComponent, constantUtf8);
    }

    @Override
    public void writeFramesRemapped(DataOutputStream dataOutputStream, Map map1) throws IOException {
        dataOutputStream.writeShort(this.frameCount);

        for (int i = 0; i < this.frameCount; i++) {
            ((StackMapEntry) this.frames[i]).writeRemapped(dataOutputStream, map1);
        }
    }
}
