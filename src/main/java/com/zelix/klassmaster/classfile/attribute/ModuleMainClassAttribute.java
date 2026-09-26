


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

public class ModuleMainClassAttribute
        extends ParsedAttributeBase
        implements Utf8ConstantReplaceable,
        ClassConstantReplaceable {
    public ResolvedClassConstant mainClass;

    @Override
    public void collectUsedConstants(char c, int n, UsedConstantsCollector usedConstantsCollector, char c2) {
        this.nameConstant.registerUsage(usedConstantsCollector, this, this.getParent());
        if (this.valid) {
            this.mainClass.registerUsage(usedConstantsCollector, this, this.getParent());
        }
    }

    @Override
    public void replaceClassConstant(ResolvedClassConstant resolvedClassConstant, ResolvedClassConstant resolvedClassConstant2) {
        if (this.mainClass == resolvedClassConstant) {
            this.mainClass = resolvedClassConstant2;
        }
    }

    



    public ModuleMainClassAttribute(ClassFileComponent classFileComponent, int n, String string, ClassFileInputStream classFileInputStream, ListMultimap listMultimap, ListMultimap listMultimap2) throws ClassFileFormatException, IOException {
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
                throw new ClassFileFormatException(classFileComponent.getOwningClass().getLocationName() + " : Illegal constant pool index in attribute : " + n2 + " : File is probably corrupt (X)");
            }
            if (!(constantPoolEntry instanceof ResolvedClassConstant)) {
                this.valid = false;
                throw new ClassFileFormatException(classFileComponent.getOwningClass().getLocationName() + " : Invalid attribute : " + n2 + " : '" + constantPoolEntry.getClass().getName() + "' : File is probably corrupt (Y)");
            }
            this.mainClass = (ResolvedClassConstant) constantPoolEntry;
            listMultimap2.addValue(this.mainClass, this);
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

    @Override
    public void writeRemapped(DataOutputStream dataOutputStream, Object object, Object object2) throws IOException {
        super.write(dataOutputStream);
        if (this.valid) {
            ConstantPoolEntry constantPoolEntry = (ConstantPoolEntry) ((Map) object).get(this.mainClass);
            if (constantPoolEntry != null) {
                dataOutputStream.writeShort(constantPoolEntry.getIndex());
            } else {
                dataOutputStream.writeShort(this.mainClass.getIndex());
            }
        } else {
            dataOutputStream.write(this.rawBytes);
        }
    }

    @Override
    public void write(DataOutputStream dataOutputStream) throws IOException {
        super.write(dataOutputStream);
        if (this.valid) {
            dataOutputStream.writeShort(this.mainClass.getIndex());
        } else {
            dataOutputStream.write(this.rawBytes);
        }
    }
}
