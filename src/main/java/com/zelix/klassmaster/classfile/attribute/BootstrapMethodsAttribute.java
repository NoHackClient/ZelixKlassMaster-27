package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ConstantUtf8;
import com.zelix.klassmaster.classfile.constpool.ResolvedMethodHandleConstant;
import com.zelix.klassmaster.classfile.constpool.ResolvedStringConstant;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.MutableInt;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BootstrapMethodsAttribute extends ParsedAttributeBase {
    public BootstrapMethodEntry[] entries;

    public BootstrapMethodEntry addEntry(ResolvedMethodHandleConstant resolvedMethodHandleConstant, ConstantPoolEntry[] constantPoolEntrys) {
        BootstrapMethodEntry[] bootstrapMethodEntrys = new BootstrapMethodEntry[this.entries.length + 1];
        System.arraycopy(this.entries, 0, bootstrapMethodEntrys, 0, this.entries.length);
        BootstrapMethodEntry bootstrapMethodEntry = new BootstrapMethodEntry(this, resolvedMethodHandleConstant, constantPoolEntrys, this.entries.length);
        bootstrapMethodEntrys[this.entries.length] = bootstrapMethodEntry;
        this.entries = bootstrapMethodEntrys;
        this.length = this.getLength();
        return bootstrapMethodEntry;
    }

    public BootstrapMethodEntry getEntry(int ba) {
        if (ba >= 0 && ba < this.entries.length) {
            return this.entries[ba];
        } else {
            throw new IllegalArgumentException(
                    "Index out of bounds in '"
                            + this.getEnclosingAttribute().getAttributeName()
                            + "' attribute in class '"
                            + this.getDisplayLocationName()
                            + "' : "
                            + ba
                            + ">"
                            + (this.entries.length - 1)
            );
        }
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
        if (super.valid) {
            dataOutputStream.writeShort(this.entries.length);
            BootstrapMethodEntry[] bootstrapMethodEntrys = this.entries;
            int ba = bootstrapMethodEntrys.length;

            for (int i = 0; i < ba; i++) {
                bootstrapMethodEntrys[i].writeRemapped(dataOutputStream, map1);
            }
        } else {
            dataOutputStream.write(super.rawBytes);
        }
    }

    @Override
    public void collectUsedConstants(char bd, int ba, UsedConstantsCollector usedConstantsCollector, char be) {
        usedConstantsCollector.markUsed(this.nameConstant, this, this.getParent());
        int bb = 0;
        int bc = 0;
        BootstrapMethodEntry[] bootstrapMethodEntrys = this.entries;

        while (true) {
            if (bc < bootstrapMethodEntrys.length) {
                this.entries[bb].collectUsedConstants('\u0000', 1103830477, usedConstantsCollector, '鞍');
            } else if (ba >= 0) {
                return;
            }

            bc = ++bb;
            bootstrapMethodEntrys = this.entries;
        }
    }

    public boolean isEmpty() {
        return this.entries.length == 0;
    }

    public void collectMethodHandleTargets(Set set1) {
        if (super.valid) {
            BootstrapMethodEntry[] bootstrapMethodEntrys = this.entries;
            int ba = bootstrapMethodEntrys.length;

            for (int i = 0; i < ba; i++) {
                bootstrapMethodEntrys[i].collectMethodHandleTargets(set1);
            }
        }
    }

    @Override
    public void write(DataOutputStream dataOutputStream) throws IOException {
        super.write(dataOutputStream);
        if (super.valid) {
            dataOutputStream.writeShort(this.entries.length);
            BootstrapMethodEntry[] bootstrapMethodEntrys = this.entries;
            int ba = bootstrapMethodEntrys.length;

            for (int i = 0; i < ba; i++) {
                bootstrapMethodEntrys[i].write(dataOutputStream);
            }
        } else {
            dataOutputStream.write(super.rawBytes);
        }
    }

    public void retainEntries(Set set1) {
        ArrayList arrayList = new ArrayList(this.entries.length);

        for (BootstrapMethodEntry bootstrapMethodEntry : this.entries) {
            if (set1.contains(bootstrapMethodEntry)) {
                arrayList.add(bootstrapMethodEntry);
            }
        }

        if (arrayList.size() < this.entries.length) {
            this.entries = ((com.zelix.klassmaster.classfile.attribute.BootstrapMethodEntry[]) (arrayList.toArray(new BootstrapMethodEntry[arrayList.size()])));
            this.length = this.getLength();
        }
    }

    public BootstrapMethodsAttribute(ClassFileBase classFileBase, ConstantUtf8 constantUtf8) {
        super(classFileBase, constantUtf8);
        this.entries = new BootstrapMethodEntry[0];
    }

    public BootstrapMethodIndex buildStringConcatIndex() {
        int ba = this.entries.length;
        BootstrapMethodIndex bootstrapMethodIndex1 = new BootstrapMethodIndex((ProgramClass) this.getOwningClass(), this);

        for (int i = 0; i < ba; i++) {
            BootstrapMethodEntry bootstrapMethodEntry = this.entries[i];
            if (bootstrapMethodEntry.isInvokeStaticHandle() && bootstrapMethodEntry.matchesBootstrapMethod()) {
                ConstantPoolEntry[] constantPoolEntrys = bootstrapMethodEntry.copyArguments();
                ConstantPoolEntry constantPoolEntry = constantPoolEntrys[0];
                if (constantPoolEntry instanceof ResolvedStringConstant) {
                    String string = ((ResolvedStringConstant) constantPoolEntry).getEditableValue();
                    MutableInt mutableInt = new MutableInt();
                    List list1 = BootstrapMethodIndex.tokenizeRecipe(string, mutableInt);
                    if (list1 != null) {
                        boolean bl = false;
                        Iterator iterator = list1.iterator();

                        while (iterator.hasNext()) {
                            String string1 = (String) iterator.next();
                            if (string1.length() >= 2) {
                                bl = true;
                                break;
                            }
                        }

                        boolean bl1 = false;
                        if (constantPoolEntrys.length > 1) {
                            for (int j = 1; j < constantPoolEntrys.length; j++) {
                                if (!(constantPoolEntrys[j] instanceof ResolvedStringConstant)) {
                                    bl1 = true;
                                    break;
                                }

                                if (!bl && ((ResolvedStringConstant) constantPoolEntrys[j]).getValueString().length() >= 2) {
                                    bl = true;
                                }
                            }
                        }

                        if (bl && !bl1) {
                            bootstrapMethodIndex1.registerConcatBootstrap(bootstrapMethodEntry, list1, mutableInt.getValue());
                        }
                    }
                }
            }
        }

        if (!bootstrapMethodIndex1.isEmpty()) {
            bootstrapMethodIndex1.expandRecipeConstants();
        }

        return bootstrapMethodIndex1;
    }

    @Override
    public int getLength() {
        int ba = 2;

        for (BootstrapMethodEntry bootstrapMethodEntry : this.entries) {
            ba += bootstrapMethodEntry.getByteLength();
        }

        this.length = ba;
        return ba;
    }

    public BootstrapMethodsAttribute(
            ClassFileComponent classFileComponent,
            int ba,
            String string,
            ClassFileInputStream classFileInputStream,
            ListMultimap listMultimap,
            ListMultimap listMultimap1,
            ListMultimap listMultimap2,
            ListMultimap listMultimap3,
            ListMultimap listMultimap4,
            PrintWriter printWriter
    ) throws IOException {
        super(classFileComponent, ba, string, classFileInputStream, listMultimap);
        byte[] bb = new byte[this.length];
        classFileInputStream.read(bb);
        ClassFileInputStream classFileInputStream1 = ClassFileInputStream.fromBytes(bb, false);

        try {
            if (this.length >= 2) {
                int bc = classFileInputStream1.readUnsignedShort();
                this.entries = new BootstrapMethodEntry[bc];

                for (int i = 0; i < bc; i++) {
                    this.entries[i] = new BootstrapMethodEntry(this, classFileInputStream1, i, listMultimap1, listMultimap2, listMultimap3, listMultimap4);
                    if (!this.entries[i].isValid()) {
                        super.valid = false;
                        super.rawBytes = bb;
                        printWriter.println(
                                "ERROR: " + this.getDisplayLocationName() + " : " + this.getAttributeName() + " (C) " + this.entries[i].getErrorMessage()
                        );
                    }
                }
            } else {
                super.valid = false;
                super.rawBytes = bb;
                printWriter.println("ERROR: " + this.getDisplayLocationName() + " : " + this.getAttributeName() + " : length=" + this.length);
            }
        } catch (IOException iOException) {
            super.valid = false;
            super.rawBytes = bb;
            printWriter.println("ERROR: " + this.getDisplayLocationName() + " : " + this.getAttributeName() + " is possibly corrupt : " + iOException);
        } finally {
            classFileInputStream1.close();
        }
    }

    public void updateRecordObjectMethodsNames() throws ZkmException, IOException {
        if (super.valid && this.getOwningClass().getSuperclassName().equals("java/lang/Record")) {
            BootstrapMethodEntry[] bootstrapMethodEntrys = this.entries;
            int ba = bootstrapMethodEntrys.length;

            for (int i = 0; i < ba; i++) {
                bootstrapMethodEntrys[i].updateObjectMethodsNames();
            }
        }
    }

    public void renumberEntries() {
        int ba = 0;
        int bb = 0;

        for (BootstrapMethodEntry[] bootstrapMethodEntrys = this.entries; bb < bootstrapMethodEntrys.length; bootstrapMethodEntrys = this.entries) {
            this.entries[ba].setIndex(ba);
            bb = ++ba;
        }
    }
}
