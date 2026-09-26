


package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.attribute.ModuleHashEntry;
import com.zelix.klassmaster.classfile.attribute.ParsedAttributeBase;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ConstantUtf8;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.classfile.constpool.Utf8ConstantReplaceable;
import com.zelix.klassmaster.exceptions.ClassFileFormatException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.exceptions.ZkmRuntimeException;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class ModuleHashesAttribute
        extends ParsedAttributeBase
        implements Utf8ConstantReplaceable {
    public ConstantUtf8 algorithm;
    public ModuleHashEntry[] hashEntries;

    


    @Override
    public void collectUsedConstants(char c, int n, UsedConstantsCollector usedConstantsCollector, char c2) {
        this.nameConstant.registerUsage(usedConstantsCollector, this, this.getParent());
        if (this.valid) {
            this.algorithm.registerUsage(usedConstantsCollector, this, this.getParent());
            int n2 = 0;
            int n3 = 0;
            ModuleHashEntry[] moduleHashEntryArray = this.hashEntries;
            while (n3 < moduleHashEntryArray.length) {
                this.hashEntries[n2].collectUsedConstants('\u0000', 1103830477, usedConstantsCollector, '\u978d');
                n3 = ++n2;
                moduleHashEntryArray = this.hashEntries;
            }
        }
    }

    


    @Override
    public void writeRemapped(DataOutputStream dataOutputStream, Object object, Object object2) throws IOException {
        Map map = (Map) object;
        super.write(dataOutputStream);
        if (this.valid) {
            ModuleHashEntry[] moduleHashEntryArray;
            DataOutputStream dataOutputStream2;
            ConstantPoolEntry constantPoolEntry = (ConstantPoolEntry) map.get(this.algorithm);
            if (constantPoolEntry != null) {
                dataOutputStream.writeShort(constantPoolEntry.getIndex());
                dataOutputStream2 = dataOutputStream;
                moduleHashEntryArray = this.hashEntries;
            } else {
                dataOutputStream.writeShort(this.algorithm.getIndex());
                dataOutputStream2 = dataOutputStream;
                moduleHashEntryArray = this.hashEntries;
            }
            dataOutputStream2.writeShort(moduleHashEntryArray.length);
            int n = 0;
            int n2 = 0;
            ModuleHashEntry[] moduleHashEntryArray2 = this.hashEntries;
            while (n2 < moduleHashEntryArray2.length) {
                this.hashEntries[n].writeRemapped(dataOutputStream, map);
                n2 = ++n;
                moduleHashEntryArray2 = this.hashEntries;
            }
            return;
        } else {
            dataOutputStream.write(this.rawBytes);
        }
    }

    


    @Override
    public void write(DataOutputStream dataOutputStream) throws IOException {
        super.write(dataOutputStream);
        if (this.valid) {
            dataOutputStream.writeShort(this.algorithm.getIndex());
            dataOutputStream.writeShort(this.hashEntries.length);
            int n = 0;
            int n2 = 0;
            ModuleHashEntry[] moduleHashEntryArray = this.hashEntries;
            while (n2 < moduleHashEntryArray.length) {
                this.hashEntries[n].writeTo(dataOutputStream);
                n2 = ++n;
                moduleHashEntryArray = this.hashEntries;
            }
            return;
        } else {
            dataOutputStream.write(this.rawBytes);
        }
    }

    @Override
    public void replaceUtf8Constant(ConstantUtf8 constantUtf8, ConstantUtf8 constantUtf82) {
        if (this.valid) {
            if (this.algorithm == constantUtf8) {
                this.algorithm = constantUtf82;
            } else {
                super.replaceUtf8Constant(constantUtf8, constantUtf82);
            }
        }
    }

    



    public ModuleHashesAttribute(ClassFileComponent classFileComponent, int n, String string, ClassFileInputStream classFileInputStream, ListMultimap listMultimap) throws ClassFileFormatException, IOException {
        super(classFileComponent, n, string, classFileInputStream, listMultimap);
        this.rawBytes = new byte[this.length];
        classFileInputStream.read(this.rawBytes);
        ClassFileInputStream classFileInputStream2 = ClassFileInputStream.fromBytes(this.rawBytes, false);
        Throwable throwable = null;
        try {
            int n2 = classFileInputStream2.readUnsignedShort();
            ConstantPoolEntry constantPoolEntry = classFileComponent.getConstantPoolEntry(n2);
            if (constantPoolEntry == null) {
                this.valid = false;
                throw new ClassFileFormatException(classFileComponent.getOwningClass().getLocationName() + " : Illegal constant pool index in attribute : " + n2 + " : File is probably corrupt (V)");
            }
            if (!(constantPoolEntry instanceof ConstantUtf8)) {
                this.valid = false;
                throw new ClassFileFormatException(classFileComponent.getOwningClass().getLocationName() + " : Invalid attribute : " + n2 + " : '" + constantPoolEntry.getClass().getName() + "' : File is probably corrupt (W)");
            }
            this.algorithm = (ConstantUtf8) constantPoolEntry;
            listMultimap.addValue(this.algorithm, this);
            int n3 = classFileInputStream2.readUnsignedShort();
            this.hashEntries = new ModuleHashEntry[n3];
            for (int i = 0; i < n3; ++i) {
                try {
                    this.hashEntries[i] = new ModuleHashEntry(this, classFileInputStream2);
                    continue;
                } catch (ZkmRuntimeException zkmRuntimeException) {
                    throw zkmRuntimeException;
                } catch (ClassFileFormatException classFileFormatException) {
                    this.valid = false;
                    throw classFileFormatException;
                }
            }
            if (classFileInputStream2 == null) return;
        } catch (Throwable throwable2) {
            try {
                throwable = throwable2;
                throw throwable2;
            } catch (Throwable throwable3) {
                if (classFileInputStream2 == null) throw throwable3;
                if (throwable != null) {
                    try {
                        classFileInputStream2.close();
                        throw throwable3;
                    } catch (Throwable throwable4) {
                        throwable.addSuppressed(throwable4);
                    }
                    throw throwable3;
                } else {
                    classFileInputStream2.close();
                }
                throw throwable3;
            }
        }
        classFileInputStream2.close();
        return;
    }

    @Override
    public void remapClassNames(Object object, Object object2, Object object3, Object object4) throws ZkmProcessingException {
    }
}
