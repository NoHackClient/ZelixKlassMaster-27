package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.AbstractMethodInfo;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.constpool.ClassConstantReplaceable;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.NameAndTypeHolder;
import com.zelix.klassmaster.classfile.constpool.ResolvedClassConstant;
import com.zelix.klassmaster.classfile.constpool.ResolvedNameAndType;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class EnclosingMethodAttribute extends ParsedAttributeBase implements NameAndTypeHolder, ClassConstantReplaceable {
    public AbstractMethodInfo enclosingMethod;
    public ResolvedClassConstant enclosingClassConstant;
    public ResolvedNameAndType methodNameAndType;

    @Override
    public void write(DataOutputStream dataOutputStream) throws IOException {
        super.write(dataOutputStream);
        if (super.valid) {
            dataOutputStream.writeShort(this.enclosingClassConstant.getIndex());
            dataOutputStream.writeShort(this.methodNameAndType == null ? 0 : this.methodNameAndType.getIndex());
        } else {
            dataOutputStream.write(super.rawBytes);
        }
    }

    @Override
    public void replaceClassConstant(ResolvedClassConstant resolvedClassConstant, ResolvedClassConstant resolvedClassConstant1) {
        if (this.enclosingClassConstant == resolvedClassConstant) {
            this.enclosingClassConstant = resolvedClassConstant1;
        }
    }

    @Override
    public ResolvedNameAndType replaceNameAndType(ResolvedNameAndType resolvedNameAndType) {
        ResolvedNameAndType resolvedNameAndType1 = this.methodNameAndType;
        this.methodNameAndType = resolvedNameAndType;
        return resolvedNameAndType1;
    }

    public EnclosingMethodAttribute(
            ClassFileComponent classFileComponent,
            int ba,
            String string,
            ClassFileInputStream classFileInputStream,
            ListMultimap listMultimap,
            ListMultimap listMultimap1,
            ListMultimap listMultimap2,
            PrintWriter printWriter
    ) throws IOException {
        super(classFileComponent, ba, string, classFileInputStream, listMultimap);
        byte[] bb = new byte[this.length];
        classFileInputStream.read(bb);
        ClassFileInputStream classFileInputStream1 = ClassFileInputStream.fromBytes(bb, false);
        if (this.length == 4) {
            int bd;
            label51:
            {
                int bc = classFileInputStream1.readUnsignedShort();
                bd = classFileInputStream1.readUnsignedShort();
                ConstantPoolEntry constantPoolEntry = this.getConstantPoolEntry(bc);
                if (constantPoolEntry != null) {
                    if (constantPoolEntry instanceof ResolvedClassConstant) {
                        this.enclosingClassConstant = (ResolvedClassConstant) constantPoolEntry;
                        listMultimap2.addValue(this.enclosingClassConstant, this);
                        break label51;
                    }

                    super.valid = false;
                } else {
                    super.valid = false;
                }

                printWriter.println(
                        "ERROR: "
                                + this.getLocationName()
                                + " : "
                                + "Invalid EnclosingMethod Attribute"
                                + " (A) : index="
                                + bc
                                + (constantPoolEntry != null ? " : tag=" + constantPoolEntry.getTag() : "")
                );
            }

            label39:
            if (bd > 0) {
                ConstantPoolEntry constantPoolEntry1 = this.getConstantPoolEntry(bd);
                if (constantPoolEntry1 != null) {
                    if (constantPoolEntry1 instanceof ResolvedNameAndType) {
                        this.methodNameAndType = (ResolvedNameAndType) constantPoolEntry1;
                        listMultimap1.addValue(this.methodNameAndType, this);
                        ((ClassFileBase) classFileComponent).markHasEnclosingMethod();
                        break label39;
                    }

                    super.valid = false;
                } else {
                    super.valid = false;
                }

                printWriter.println(
                        "ERROR: "
                                + this.getLocationName()
                                + " : "
                                + "Invalid EnclosingMethod Attribute"
                                + " (B) : index="
                                + bd
                                + (constantPoolEntry1 != null ? " : tag=" + constantPoolEntry1.getTag() : "")
                );
            }

            if (!super.valid) {
                super.rawBytes = bb;
            }
        } else {
            super.valid = false;
            super.rawBytes = bb;
            printWriter.println("ERROR: " + this.getLocationName() + " : " + "Invalid EnclosingMethod Attribute" + " (C) : length=" + this.length);
        }
    }

    public String getEnclosingClassName() {
        return super.valid ? this.enclosingClassConstant.getClassName() : null;
    }

    public void updateAfterMethodRename() {
        if (super.valid && this.methodNameAndType != null) {
            if (this.enclosingMethod != null && !this.enclosingMethod.getJvmName().equals(this.methodNameAndType.getName())) {
                this.methodNameAndType.setName(this.enclosingMethod.getJvmName());
            }

            if (this.enclosingMethod != null && !this.enclosingMethod.getDescriptor().equals(this.methodNameAndType.getDescriptor())) {
                this.methodNameAndType.setDescriptor(this.enclosingMethod.getDescriptor());
            }
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
            ResolvedClassConstant resolvedClassConstant = (ResolvedClassConstant) map1.get(this.enclosingClassConstant);
            if (resolvedClassConstant != null) {
                dataOutputStream.writeShort(resolvedClassConstant.getIndex());
            } else {
                dataOutputStream.writeShort(this.enclosingClassConstant.getIndex());
            }

            if (this.methodNameAndType == null) {
                dataOutputStream.writeShort(0);
            } else {
                ResolvedNameAndType resolvedNameAndType = (ResolvedNameAndType) map1.get(this.methodNameAndType);
                if (resolvedNameAndType != null) {
                    dataOutputStream.writeShort(resolvedNameAndType.getIndex());
                } else {
                    dataOutputStream.writeShort(this.methodNameAndType.getIndex());
                }
            }
        } else {
            dataOutputStream.write(super.rawBytes);
        }
    }

    @Override
    public void collectUsedConstants(char ba, int bb, UsedConstantsCollector usedConstantsCollector, char bc) {
        ClassFileComponent.getFlowGuardNodes();
        usedConstantsCollector.markUsed(this.nameConstant, this, this);
        if (super.valid) {
            EnclosingMethodAttribute enclosingMethodAttribute1 = this;
            if (ba >= 0) {
                if (this.enclosingClassConstant != null) {
                    this.enclosingClassConstant.registerUsage(usedConstantsCollector, this, this);
                }

                enclosingMethodAttribute1 = this;
            }

            if (enclosingMethodAttribute1.methodNameAndType != null) {
                this.methodNameAndType.registerUsage(usedConstantsCollector, this, this);
            }
        }
    }

    public void resolveEnclosingMethod(ClassResolver classResolver1) throws ZkmException, IOException {
        if (super.valid) {
            ClassFileBase classFileBase = this.getOwningClass();
            Integer integer = classFileBase.hasReleaseVersion() ? classFileBase.getReleaseVersion() : null;
            classFileBase = classResolver1.getVersionedClass(
                    this.enclosingClassConstant.getClassName(), integer, "analyzing enclosing method in class '" + this.getDisplayLocationName() + "' (A)"
            );
            if (classFileBase != null && this.methodNameAndType != null) {
                this.enclosingMethod = classFileBase.findMethod(new MethodSignature(this.methodNameAndType.getName(), this.methodNameAndType.getDescriptor()));
            }
        }
    }
}
