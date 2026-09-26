



package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.util.ZkmAssert;
import com.zelix.klassmaster.util.ZkmUtils;
import com.zelix.klassmaster.exceptions.ClassFileFormatException;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.ClassFileComponent;

import java.io.IOException;
import java.io.DataOutputStream;

import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.classfile.constpool.ResolvedPackageConstant;
import com.zelix.klassmaster.classfile.constpool.Utf8ConstantReplaceable;

public class ModulePackagesAttribute extends ParsedAttributeBase implements Utf8ConstantReplaceable {
    public final ResolvedPackageConstant[] packages;

    @Override
    public void collectUsedConstants(final char c, final int n, final UsedConstantsCollector usedConstantsCollector, final char c2) {
        this.nameConstant.registerUsage(usedConstantsCollector, this, this.getParent());
        if (super.valid) {
            final ResolvedPackageConstant[] packages = this.packages;
            for (int length = packages.length, i = 0; i < length; ++i) {
                packages[i].registerUsage(usedConstantsCollector, this, this.getParent());
            }
        }
    }

    @Override
    public void writeRemapped(final DataOutputStream dataOutputStream, final Object o, final Object o2) throws IOException {
        super.write(dataOutputStream);
        if (super.valid) {
            dataOutputStream.writeShort(this.packages.length);
            final ResolvedPackageConstant[] packages = this.packages;
            for (int length = packages.length, i = 0; i < length; ++i) {
                dataOutputStream.writeShort(packages[i].getIndex());
            }
        } else {
            dataOutputStream.write(super.rawBytes);
        }
    }

    public ModulePackagesAttribute(final ClassFileComponent classFileComponent, final int n, final String s, final ClassFileInputStream classFileInputStream, final ListMultimap listMultimap) throws ClassFileFormatException, IOException {
        super(classFileComponent, n, s, classFileInputStream, listMultimap);
        classFileInputStream.read(super.rawBytes = new byte[this.length]);
        final ClassFileInputStream fromBytes = ClassFileInputStream.fromBytes(super.rawBytes, false);
        Throwable t = null;
        ResolvedPackageConstant[] array = null;
        Label_0305:
        {
            Label_0301:
            {
                try {
                    final int unsignedShort = fromBytes.readUnsignedShort();
                    this.packages = new ResolvedPackageConstant[unsignedShort];
                    for (int i = 0; i < unsignedShort; ++i) {
                        final int unsignedShort2 = fromBytes.readUnsignedShort();
                        final ConstantPoolEntry constantPoolEntry = classFileComponent.getConstantPoolEntry(unsignedShort2);
                        if (constantPoolEntry == null) {
                            super.valid = false;
                            throw new ClassFileFormatException(classFileComponent.getOwningClass().getLocationName() + " : Illegal constant pool index in attribute : " + unsignedShort2 + " : File is probably corrupt (AD)");
                        }
                        if (!(constantPoolEntry instanceof ResolvedPackageConstant)) {
                            super.valid = false;
                            throw new ClassFileFormatException(classFileComponent.getOwningClass().getLocationName() + " : Invalid attribute : " + unsignedShort2 + " : '" + ((ResolvedPackageConstant) constantPoolEntry).getClass().getName() + "' : File is probably corrupt (AE)");
                        }
                        this.packages[i] = (ResolvedPackageConstant) constantPoolEntry;
                    }
                    if (fromBytes != null) {
                        fromBytes.close();
                        break Label_0301;
                    }
                } catch (final Throwable t2) {
                    t = t2;
                    throw t2;
                } finally {
                    if (fromBytes != null) {
                        if (t != null) {
                            try {
                                fromBytes.close();
                            } catch (final Throwable exception) {
                                t.addSuppressed(exception);
                            }
                        } else {
                            fromBytes.close();
                        }
                    }
                }
                array = this.packages;
                break Label_0305;
            }
            array = this.packages;
        }
        if (!ZkmUtils.allUnique(array)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Invalid 'Module' attribute in class '");
            sb.append(this.getLocationName());
            sb.append("' : Duplicate package names : ");
            int n2 = 0;
            int j = 0;
            ResolvedPackageConstant[] array2 = this.packages;
            while (j < array2.length) {
                sb.append(this.packages[n2].getValueString());
                if (n2 < this.packages.length - 1) {
                    sb.append(", ");
                }
                n2 = (j = n2 + 1);
                array2 = this.packages;
            }
            ZkmAssert.assertTrue(false, new String[]{sb.toString()});
        }
    }

    @Override
    public void remapClassNames(final Object o, final Object o2, final Object o3, final Object o4) throws ZkmProcessingException {
    }

    @Override
    public void write(final DataOutputStream dataOutputStream) throws IOException {
        super.write(dataOutputStream);
        if (super.valid) {
            dataOutputStream.writeShort(this.packages.length);
            final ResolvedPackageConstant[] packages = this.packages;
            for (int length = packages.length, i = 0; i < length; ++i) {
                dataOutputStream.writeShort(packages[i].getIndex());
            }
        } else {
            dataOutputStream.write(super.rawBytes);
        }
    }
}
