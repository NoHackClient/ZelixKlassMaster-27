



package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.util.ZkmAssert;
import com.zelix.klassmaster.util.ZkmUtils;
import com.zelix.klassmaster.exceptions.ClassFileFormatException;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.ClassFileComponent;

import java.io.IOException;
import java.util.Map;

import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;

import java.io.DataOutputStream;

import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;

import java.util.ArrayList;

import com.zelix.klassmaster.obfuscator.trim.TrimProcessor;
import com.zelix.klassmaster.classfile.constpool.ResolvedClassConstant;
import com.zelix.klassmaster.classfile.constpool.ClassConstantReplaceable;
import com.zelix.klassmaster.classfile.constpool.Utf8ConstantReplaceable;

public class NestMembersAttribute extends ParsedAttributeBase implements Utf8ConstantReplaceable, ClassConstantReplaceable {
    public ResolvedClassConstant[] memberClasses;

    @Override
    public int getLength() {
        return 2 + this.memberClasses.length * 2;
    }

    public int removeTrimmedMembers(final TrimProcessor trimProcessor) {
        if (super.valid) {
            final ArrayList list = new ArrayList(this.memberClasses.length);
            final ResolvedClassConstant[] memberClasses = this.memberClasses;
            for (int length = memberClasses.length, i = 0; i < length; ++i) {
                final ResolvedClassConstant resolvedClassConstant = memberClasses[i];
                final ProgramClass programClass = ClassHierarchyNode.findProgramClass(resolvedClassConstant.getClassName());
                if (programClass == null || trimProcessor.isClassOrOuterExcluded(programClass)) {
                    list.add(resolvedClassConstant);
                }
            }
            if (list.size() < this.memberClasses.length) {
                this.memberClasses = (ResolvedClassConstant[]) list.toArray(new ResolvedClassConstant[list.size()]);
            }
        }
        return this.memberClasses.length;
    }

    @Override
    public void writeRemapped(final DataOutputStream dataOutputStream, final Object o, final Object o2) throws IOException {
        super.write(dataOutputStream);
        if (super.valid) {
            dataOutputStream.writeShort(this.memberClasses.length);
            final ResolvedClassConstant[] memberClasses = this.memberClasses;
            for (int length = memberClasses.length, i = 0; i < length; ++i) {
                final ResolvedClassConstant resolvedClassConstant = memberClasses[i];
                final ConstantPoolEntry constantPoolEntry = ((com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry) (((Map) o).get(resolvedClassConstant)));
                if (constantPoolEntry != null) {
                    dataOutputStream.writeShort(constantPoolEntry.getIndex());
                } else {
                    dataOutputStream.writeShort(resolvedClassConstant.getIndex());
                }
            }
        } else {
            dataOutputStream.write(super.rawBytes);
        }
    }

    public NestMembersAttribute(final ClassFileComponent classFileComponent, final int n, final String s, final ClassFileInputStream classFileInputStream, final ListMultimap listMultimap) throws ClassFileFormatException, IOException {
        super(classFileComponent, n, s, classFileInputStream, listMultimap);
        classFileInputStream.read(super.rawBytes = new byte[this.length]);
        final ClassFileInputStream fromBytes = ClassFileInputStream.fromBytes(super.rawBytes, false);
        Throwable t = null;
        ResolvedClassConstant[] array = null;
        Label_0333:
        {
            Label_0329:
            {
                try {
                    final int unsignedShort = fromBytes.readUnsignedShort();
                    this.memberClasses = new ResolvedClassConstant[unsignedShort];
                    for (int i = 0; i < unsignedShort; ++i) {
                        final int unsignedShort2 = fromBytes.readUnsignedShort();
                        final ConstantPoolEntry constantPoolEntry = classFileComponent.getConstantPoolEntry(unsignedShort2);
                        if (constantPoolEntry == null) {
                            super.valid = false;
                            throw new ClassFileFormatException(classFileComponent.getOwningClass().getLocationName() + " : Illegal constant pool index in '" + "NestMembers" + "' attribute : " + unsignedShort2 + " : '" + this.getLocationName() + "' : File is probably corrupt (AX)");
                        }
                        if (!(constantPoolEntry instanceof ResolvedClassConstant)) {
                            super.valid = false;
                            throw new ClassFileFormatException(classFileComponent.getOwningClass().getLocationName() + " : Invalid '" + "NestMembers" + "' attribute : " + unsignedShort2 + " : '" + this.getLocationName() + "' : File is probably corrupt (AY)");
                        }
                        this.memberClasses[i] = (ResolvedClassConstant) constantPoolEntry;
                    }
                    if (fromBytes != null) {
                        fromBytes.close();
                        break Label_0329;
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
                array = this.memberClasses;
                break Label_0333;
            }
            array = this.memberClasses;
        }
        if (!ZkmUtils.allUnique(array)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Invalid 'NestMembers' attribute in class '");
            sb.append(this.getLocationName());
            sb.append("' : Duplicate class names : ");
            int n2 = 0;
            int j = 0;
            ResolvedClassConstant[] array2 = this.memberClasses;
            while (j < array2.length) {
                sb.append(this.memberClasses[n2].getValueString());
                if (n2 < this.memberClasses.length - 1) {
                    sb.append(", ");
                }
                n2 = (j = n2 + 1);
                array2 = this.memberClasses;
            }
            ZkmAssert.assertTrue(false, new String[]{sb.toString()});
        }
    }

    @Override
    public void replaceClassConstant(final ResolvedClassConstant resolvedClassConstant, final ResolvedClassConstant resolvedClassConstant2) {
        int n = 0;
        int i = 0;
        ResolvedClassConstant[] array = this.memberClasses;
        while (i < array.length) {
            if (this.memberClasses[n] == resolvedClassConstant) {
                this.memberClasses[n] = resolvedClassConstant2;
            }
            n = (i = n + 1);
            array = this.memberClasses;
        }
    }

    @Override
    public void collectUsedConstants(final char c, final int n, final UsedConstantsCollector usedConstantsCollector, final char c2) {
        this.nameConstant.registerUsage(usedConstantsCollector, this, this.getParent());
        if (super.valid) {
            int i;
            int n2 = i = 0;
            ResolvedClassConstant[] array = this.memberClasses;
            while (i < array.length) {
                this.memberClasses[n2].registerUsage(usedConstantsCollector, this, this.getParent());
                n2 = (i = n2 + 1);
                array = this.memberClasses;
            }
        }
    }

    @Override
    public void write(final DataOutputStream dataOutputStream) throws IOException {
        super.write(dataOutputStream);
        if (super.valid) {
            dataOutputStream.writeShort(this.memberClasses.length);
            final ResolvedClassConstant[] memberClasses = this.memberClasses;
            for (int length = memberClasses.length, i = 0; i < length; ++i) {
                dataOutputStream.writeShort(memberClasses[i].getIndex());
            }
        } else {
            dataOutputStream.write(super.rawBytes);
        }
    }

    @Override
    public void remapClassNames(final Object o, final Object o2, final Object o3, final Object o4) throws ZkmProcessingException {
    }
}
