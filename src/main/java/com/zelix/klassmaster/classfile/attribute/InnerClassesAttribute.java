package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.constpool.ClassConstantBase;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.classfile.constpool.Utf8ConstantReplaceable;
import com.zelix.klassmaster.obfuscator.trim.TrimProcessor;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

public class InnerClassesAttribute extends Attribute implements Utf8ConstantReplaceable {
    public boolean valid = true;
    public int entryCount;
    public InnerClassEntry[] entries;
    public byte[] rawBytes;

    public void setValid() {
        this.valid = false;
    }

    public int removeTrimmedEntries(TrimProcessor trimProcessor1) {
        if (this.entries != null) {
            ArrayList arrayList = new ArrayList(this.entries.length);
            int ba = 0;
            int bb = 0;

            for (InnerClassEntry[] innerClassEntrys = this.entries; bb < innerClassEntrys.length; innerClassEntrys = this.entries) {
                if (!this.entries[ba].isInnerClassTrimmed(trimProcessor1)) {
                    arrayList.add(this.entries[ba]);
                }

                bb = ++ba;
            }

            ba = arrayList.size();
            if (ba < this.entries.length) {
                this.entries = ((com.zelix.klassmaster.classfile.attribute.InnerClassEntry[]) (arrayList.toArray(new InnerClassEntry[ba])));
                this.entryCount = ba;
            }
        }

        return this.entryCount;
    }

    @Override
    public void writeRemapped(DataOutputStream dataOutputStream, Object object, Object object1) throws IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object1;
        Map map1 = (Map) object;
        ScriptEnvironment scriptEnvironment2 = scriptEnvironment1;
        Map map2 = map1;
        ScriptEnvironment scriptEnvironment3 = scriptEnvironment2;
        Map map3 = map2;
        DataOutputStream dataOutputStream1 = dataOutputStream;
        super.writeRemapped(dataOutputStream1, map3, scriptEnvironment3);
        if (this.valid) {
            dataOutputStream.writeShort(this.entryCount);
            int ba = 0;
            int bb = 0;

            for (int i = this.entryCount; bb < i; i = this.entryCount) {
                this.entries[ba].writeRemapped(dataOutputStream, map1);
                bb = ++ba;
            }
        } else {
            dataOutputStream.write(this.rawBytes);
        }
    }

    @Override
    public void write(DataOutputStream dataOutputStream) throws IOException {
        super.write(dataOutputStream);
        if (this.valid) {
            this.writeEntries(dataOutputStream);
        } else {
            dataOutputStream.write(this.rawBytes);
        }
    }

    public int removeEntriesForClasses(HashSet hashSet) {
        if (this.entries != null) {
            ArrayList arrayList = new ArrayList(this.entries.length);
            int ba = 0;
            int bb = 0;

            for (InnerClassEntry[] innerClassEntrys = this.entries; bb < innerClassEntrys.length; innerClassEntrys = this.entries) {
                if (!this.entries[ba].isInnerClassInSet(hashSet)) {
                    arrayList.add(this.entries[ba]);
                }

                bb = ++ba;
            }

            ba = arrayList.size();
            if (ba < this.entries.length) {
                this.entries = ((com.zelix.klassmaster.classfile.attribute.InnerClassEntry[]) (arrayList.toArray(new InnerClassEntry[ba])));
                this.entryCount = ba;
            }
        }

        return this.entryCount;
    }

    public InnerClassesAttribute(
            ClassFileComponent classFileComponent,
            int ba,
            String string,
            ClassFileInputStream classFileInputStream,
            ListMultimap listMultimap,
            ListMultimap listMultimap1,
            PrintWriter printWriter
    ) throws IOException {
        super(classFileComponent, ba, string, classFileInputStream, listMultimap);
        if (this.length >= 2) {
            this.entryCount = classFileInputStream.readUnsignedShort();
            if (this.entryCount * 8 + 2 == this.length) {
                this.entries = new InnerClassEntry[this.entryCount];
                int bb = 0;
                int bd = 0;

                for (int i = this.entryCount; bd < i; i = this.entryCount) {
                    this.entries[bb] = new InnerClassEntry(this, classFileInputStream, listMultimap, listMultimap1);
                    if (this.valid) {
                        if (!this.entries[bb].valid) {
                            this.setValid();
                            printWriter.println("ERROR: " + this.getClassName() + " : " + "Invalid InnerClasses Attribute" + " (3)");
                        } else {
                            String string1 = this.entries[bb].getInnerSimpleName();
                            if (string1 != null) {
                                String string2 = this.entries[bb].getInnerClassName();
                                String string3 = this.entries[bb].getOuterClassName();
                                if (string3 != null && string3.length() > string2.length() - string1.length()) {
                                    printWriter.println(
                                            "ERROR: "
                                                    + this.getClassName()
                                                    + " : "
                                                    + "Invalid InnerClasses Attribute"
                                                    + " (5) : '"
                                                    + string3
                                                    + "' : '"
                                                    + string2
                                                    + "' : '"
                                                    + string1
                                                    + "'"
                                    );
                                }
                            }
                        }
                    }

                    bd = ++bb;
                }

                if (!this.valid) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(this.length);
                    DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
                    this.writeEntries(dataOutputStream);
                    this.rawBytes = byteArrayOutputStream.toByteArray();
                    this.entries = null;
                }
            } else {
                this.setValid();
                printWriter.println("ERROR: " + this.getClassName() + " : " + "Invalid InnerClasses Attribute" + " (1)");
                this.rawBytes = new byte[this.length];
                this.rawBytes[0] = (byte) (this.entryCount >>> 8 & 0xFF);
                this.rawBytes[1] = (byte) (this.entryCount >>> 0 & 0xFF);
                int bc = this.length - 2;
                classFileInputStream.read(this.rawBytes, 2, bc);
            }
        } else {
            this.setValid();
            printWriter.println("ERROR: " + this.getClassName() + " : " + "Invalid InnerClasses Attribute" + " (2)");
            this.rawBytes = new byte[this.length];
            classFileInputStream.read(this.rawBytes);
        }

        if (this.valid) {
            ((ClassFileBase) classFileComponent).setInnerClassesTrimPending();
        } else {
            ((ClassFileBase) classFileComponent).setInnerClassesRemovalPending();
        }
    }

    @Override
    public int getLength() {
        return 2 + this.entryCount * 8;
    }

    @Override
    public void collectUsedConstants(char bc, int bd, UsedConstantsCollector usedConstantsCollector, char be) {
        usedConstantsCollector.markUsed(this.nameConstant, this, this.getParent());
        if (this.entries != null) {
            int ba = 0;
            int bb = ba;

            for (InnerClassEntry[] innerClassEntrys = this.entries; bb < innerClassEntrys.length; innerClassEntrys = this.entries) {
                this.entries[ba].collectUsedConstants('\u0000', 1103830477, usedConstantsCollector, '鞍');
                bb = ++ba;
            }
        }
    }

    public void syncInnerNames() {
        if (this.valid) {
            int ba = 0;
            int bb = ba;

            for (int i = this.entryCount; bb < i; i = this.entryCount) {
                this.entries[ba].syncInnerNameWithClass();
                bb = ++ba;
            }
        }
    }

    public void writeEntries(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeShort(this.entryCount);
        int ba = 0;
        int bb = 0;

        for (int i = this.entryCount; bb < i; i = this.entryCount) {
            this.entries[ba].writeTo(dataOutputStream);
            bb = ++ba;
        }
    }

    public void collectReferencedClasses(HashSet hashSet) {
        if (this.valid) {
            int ba = 0;
            int bb = ba;

            for (int i = this.entryCount; bb < i; i = this.entryCount) {
                this.entries[ba].collectReferencedClasses(hashSet);
                bb = ++ba;
            }
        }
    }

    public InnerClassEntry findEntryForClass(ClassConstantBase classConstantBase) {
        if (this.valid) {
            int ba = 0;
            int bb = ba;

            for (int i = this.entryCount; bb < i; i = this.entryCount) {
                InnerClassEntry innerClassEntry1 = this.entries[ba];
                if (innerClassEntry1.innerClass != null && innerClassEntry1.innerClass.getClassName().equals(classConstantBase.getClassName())) {
                    return innerClassEntry1;
                }

                bb = ++ba;
            }
        }

        return null;
    }
}
