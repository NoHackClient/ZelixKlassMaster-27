


package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.attribute.ParsedAttributeBase;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ConstantUtf8;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.classfile.constpool.Utf8ConstantReplaceable;
import com.zelix.klassmaster.exceptions.ClassFileFormatException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.DataOutputStream;
import java.io.IOException;

public class ModuleVersionAttribute
        extends ParsedAttributeBase
        implements Utf8ConstantReplaceable {
    public ConstantUtf8 moduleVersion;

    



    public ModuleVersionAttribute(ClassFileComponent classFileComponent, int n, String string, ClassFileInputStream classFileInputStream, ListMultimap listMultimap) throws ClassFileFormatException, IOException {
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
                throw new ClassFileFormatException(classFileComponent.getOwningClass().getLocationName() + " : Illegal constant pool index in attribute : " + n2 + " : File is probably corrupt (AT)");
            }
            if (!(constantPoolEntry instanceof ConstantUtf8)) {
                this.valid = false;
                throw new ClassFileFormatException(classFileComponent.getOwningClass().getLocationName() + " : Invalid attribute : " + n2 + " : '" + constantPoolEntry.getClass().getName() + "' : File is probably corrupt (AU)");
            }
            this.moduleVersion = (ConstantUtf8) constantPoolEntry;
            listMultimap.addValue(this.moduleVersion, this);
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
    public void replaceUtf8Constant(ConstantUtf8 constantUtf8, ConstantUtf8 constantUtf82) {
        if (this.moduleVersion == constantUtf8) {
            this.moduleVersion = constantUtf82;
        } else {
            super.replaceUtf8Constant(constantUtf8, constantUtf82);
        }
    }

    @Override
    public void writeRemapped(DataOutputStream dataOutputStream, Object object, Object object2) throws IOException {
        this.write(dataOutputStream);
    }

    @Override
    public void remapClassNames(Object object, Object object2, Object object3, Object object4) throws ZkmProcessingException {
    }

    @Override
    public void write(DataOutputStream dataOutputStream) throws IOException {
        super.write(dataOutputStream);
        if (this.valid) {
            dataOutputStream.writeShort(this.moduleVersion.getIndex());
        } else {
            dataOutputStream.write(this.rawBytes);
        }
    }

    @Override
    public void collectUsedConstants(char c, int n, UsedConstantsCollector usedConstantsCollector, char c2) {
        this.nameConstant.registerUsage(usedConstantsCollector, this, this.getParent());
        if (this.valid) {
            this.moduleVersion.registerUsage(usedConstantsCollector, this, this.getParent());
        }
    }
}
