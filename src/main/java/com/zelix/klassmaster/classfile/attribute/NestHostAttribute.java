


package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.attribute.ParsedAttributeBase;
import com.zelix.klassmaster.classfile.constpool.ClassConstantReplaceable;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ResolvedClassConstant;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.classfile.constpool.Utf8ConstantReplaceable;
import com.zelix.klassmaster.exceptions.ClassFileFormatException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class NestHostAttribute
        extends ParsedAttributeBase
        implements Utf8ConstantReplaceable,
        ClassConstantReplaceable {
    public ResolvedClassConstant hostClass;

    @Override
    public void remapClassNames(Object object, Object object2, Object object3, Object object4) throws ZkmProcessingException {
    }

    



    public NestHostAttribute(ClassFileComponent classFileComponent, int n, String string, ClassFileInputStream classFileInputStream, ListMultimap listMultimap, ListMultimap listMultimap2) throws ClassFileFormatException, IOException {
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
                throw new ClassFileFormatException(classFileComponent.getOwningClass().getLocationName() + " : Illegal constant pool index in '" + "NestHost" + "' attribute : " + n2 + " : '" + this.getLocationName() + "' : File is probably corrupt (AV)");
            }
            if (!(constantPoolEntry instanceof ResolvedClassConstant)) {
                this.valid = false;
                throw new ClassFileFormatException(classFileComponent.getOwningClass().getLocationName() + " : Invalid attribute : '" + "NestHost" + "' : " + n2 + " : '" + this.getLocationName() + "' : File is probably corrupt (AW)");
            }
            this.hostClass = (ResolvedClassConstant) constantPoolEntry;
            listMultimap2.addValue(this.hostClass, this);
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
    public void replaceClassConstant(ResolvedClassConstant resolvedClassConstant, ResolvedClassConstant resolvedClassConstant2) {
        if (this.hostClass == resolvedClassConstant) {
            this.hostClass = resolvedClassConstant2;
        }
    }

    @Override
    public void writeRemapped(DataOutputStream dataOutputStream, Object object, Object object2) throws IOException {
        super.write(dataOutputStream);
        if (this.valid) {
            ConstantPoolEntry constantPoolEntry = (ConstantPoolEntry) ((Map) object).get(this.hostClass);
            if (constantPoolEntry != null) {
                dataOutputStream.writeShort(constantPoolEntry.getIndex());
            } else {
                dataOutputStream.writeShort(this.hostClass.getIndex());
            }
        } else {
            dataOutputStream.write(this.rawBytes);
        }
    }

    public String getHostClassName() {
        return this.hostClass.getClassName();
    }

    @Override
    public void write(DataOutputStream dataOutputStream) throws IOException {
        super.write(dataOutputStream);
        if (this.valid) {
            dataOutputStream.writeShort(this.hostClass.getIndex());
        } else {
            dataOutputStream.write(this.rawBytes);
        }
    }

    @Override
    public void collectUsedConstants(char c, int n, UsedConstantsCollector usedConstantsCollector, char c2) {
        this.nameConstant.registerUsage(usedConstantsCollector, this, this.getParent());
        if (this.valid) {
            this.hostClass.registerUsage(usedConstantsCollector, this, this.getParent());
        }
    }
}
