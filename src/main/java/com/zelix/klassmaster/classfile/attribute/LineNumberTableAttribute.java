package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.classfile.constpool.Utf8ConstantReplaceable;
import com.zelix.klassmaster.classfile.insn.InstructionUsageClearFlag;
import com.zelix.klassmaster.classfile.insn.LabelInstruction;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.SetMultiMap;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

public class LineNumberTableAttribute extends Attribute implements Utf8ConstantReplaceable {
    public boolean valid = true;
    public int entryCount;
    public LineNumberEntry[] entries;
    public byte[] rawBytes;

    public boolean hasLineNumber(int ba) {
        if (!this.valid) {
            return false;
        }

        for (int i = 0; i < this.entries.length; i++) {
            if (this.entries[i].getLineNumber() == ba) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void writeRemapped(DataOutputStream dataOutputStream, Object object, Object object1) throws IOException {
        Map map1 = (Map) object;
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object1;
        ScriptEnvironment scriptEnvironment2 = scriptEnvironment1;
        Map map2 = map1;
        ScriptEnvironment scriptEnvironment3 = scriptEnvironment2;
        Map map3 = map2;
        DataOutputStream dataOutputStream1 = dataOutputStream;
        super.writeRemapped(dataOutputStream1, map3, scriptEnvironment3);
        if (this.valid) {
            dataOutputStream.writeShort(this.entryCount);

            for (int i = 0; i < this.entryCount; i++) {
                this.entries[i].writeTo(dataOutputStream);
            }
        } else {
            dataOutputStream.write(this.rawBytes);
        }
    }

    public void removeEntriesAtLabels(HashSet hashSet, SetMultiMap setMultiMap) {
        if (this.valid && hashSet.size() > 0) {
            ArrayList arrayList = new ArrayList(this.entries.length);

            for (int i = 0; i < this.entries.length; i++) {
                LabelInstruction labelInstruction = this.entries[i].getLabel();
                if (!hashSet.contains(labelInstruction)) {
                    arrayList.add(this.entries[i]);
                } else {
                    setMultiMap.removeValue(labelInstruction, this.entries[i]);
                    labelInstruction.clearUsageFlag(InstructionUsageClearFlag.CLEAR_USAGE_LINE_NUMBER);
                }
            }

            if (arrayList.size() < this.entries.length) {
                LineNumberEntry[] lineNumberEntrys = new LineNumberEntry[arrayList.size()];
                this.entries = ((com.zelix.klassmaster.classfile.attribute.LineNumberEntry[]) (arrayList.toArray(lineNumberEntrys)));
                this.entryCount = this.entries.length;
                this.length = this.entryCount * 4 + 2;
            }
        }
    }

    public void addEntriesTo(ArrayList arrayList) {
        if (this.valid) {
            for (int i = 0; i < this.entries.length; i++) {
                arrayList.add(this.entries[i]);
            }
        }
    }

    public LineNumberTableAttribute(
            ClassFileComponent classFileComponent,
            int ba,
            String string,
            ClassFileInputStream classFileInputStream,
            ListMultimap listMultimap,
            PrintWriter printWriter,
            ListMultimap listMultimap1
    ) throws IOException {
        super(classFileComponent, ba, string, classFileInputStream, listMultimap);
        if (this.length >= 2) {
            this.entryCount = classFileInputStream.readUnsignedShort();
            if (this.entryCount * 4 + 2 == this.length) {
                this.entries = new LineNumberEntry[this.entryCount];

                for (int i = 0; i < this.entryCount; i++) {
                    this.entries[i] = new LineNumberEntry(this, classFileInputStream, listMultimap1);
                }
            } else {
                this.valid = false;
                printWriter.println("ERROR: " + this.getLocationName() + " : " + "Invalid LineNumberTable Attribute" + " (B)");
                this.rawBytes = new byte[this.length];
                this.rawBytes[0] = (byte) (this.entryCount >>> 8 & 0xFF);
                this.rawBytes[1] = (byte) (this.entryCount >>> 0 & 0xFF);
                int bc = this.length - 2;
                classFileInputStream.read(this.rawBytes, 2, bc);
            }
        } else {
            this.valid = false;
            printWriter.println("ERROR: " + this.getLocationName() + " : " + "Invalid LineNumberTable Attribute" + " (A)");
            this.rawBytes = new byte[this.length];
            classFileInputStream.read(this.rawBytes);
        }
    }

    public int[] getSortedLineNumbers() {
        int[] ba = new int[this.entryCount];
        int bb = 0;

        for (LineNumberEntry lineNumberEntry : this.entries) {
            ba[bb++] = lineNumberEntry.getLineNumber();
        }

        Arrays.sort(ba);
        return ba;
    }

    @Override
    public void collectUsedConstants(char bb, int bc, UsedConstantsCollector usedConstantsCollector, char bd) {
        usedConstantsCollector.markUsed(this.nameConstant, this, this.getParent());
        if (this.valid) {
            for (int i = 0; i < this.entryCount; i++) {
                this.entries[i].collectUsedConstants('\u0000', 1103830477, usedConstantsCollector, '鞍');
            }
        }
    }

    @Override
    public void write(DataOutputStream dataOutputStream) throws IOException {
        super.write(dataOutputStream);
        if (this.valid) {
            dataOutputStream.writeShort(this.entryCount);

            for (int i = 0; i < this.entryCount; i++) {
                this.entries[i].writeTo(dataOutputStream);
            }
        } else {
            dataOutputStream.write(this.rawBytes);
        }
    }

    public void registerLabelTargets(SetMultiMap setMultiMap) {
        if (this.valid) {
            for (int i = 0; i < this.entries.length; i++) {
                this.entries[i].registerLabelTargets(setMultiMap);
            }
        }
    }
}
