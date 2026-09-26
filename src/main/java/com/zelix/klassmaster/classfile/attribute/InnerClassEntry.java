package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.constpool.ClassConstantReplaceable;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ConstantUtf8;
import com.zelix.klassmaster.classfile.constpool.ResolvedClassConstant;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.classfile.constpool.Utf8ConstantReplaceable;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.obfuscator.trim.TrimProcessor;
import com.zelix.klassmaster.util.ByteConversionUtils;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.VisitableNode;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;

public class InnerClassEntry extends ClassFileComponent implements Utf8ConstantReplaceable, ClassConstantReplaceable {
    public boolean valid = true;
    public int accessFlags;
    public ResolvedClassConstant innerClass;
    public ResolvedClassConstant outerClass;
    public ConstantUtf8 innerName;
    public byte[] rawBytes;

    public boolean isStatic() {
        return (this.accessFlags & 8) != 0;
    }

    public void syncInnerNameWithClass() {
        if (this.innerName != null && this.innerName.getValue().length() > 0) {
            String string = this.innerClass.getClassName();
            int ba = string.lastIndexOf(36);
            String string1;
            ConstantUtf8 constantUtf8;
            if (ba != -1) {
                string1 = string.substring(ba + 1);
                constantUtf8 = this.innerName;
            } else {
                string1 = "";
                constantUtf8 = this.innerName;
            }

            constantUtf8.setValue(string1);
        }
    }

    public String getCommonOuterPrefix() {
        if (this.outerClass == null) {
            return null;
        }

        String string = this.outerClass.getClassName();
        String string1 = this.innerClass.getClassName();

        while (string1 != null && !string.startsWith(string1)) {
            int ba = string1.lastIndexOf("$");
            if (ba > 0) {
                string1 = string1.substring(0, ba);
            } else {
                string1 = null;
            }
        }

        return string1;
    }

    public boolean isInnerClassInSet(HashSet hashSet) {
        ProgramClass programClass1 = ClassHierarchyNode.findProgramClass(this.innerClass.getClassName());
        return programClass1 != null ? hashSet.contains(programClass1) : false;
    }

    @Override
    public void replaceUtf8Constant(ConstantUtf8 constantUtf8, ConstantUtf8 constantUtf81) {
        if (this.innerName == constantUtf8) {
            this.innerName = constantUtf81;
        }
    }

    public InnerClassEntry(
            ClassFileComponent classFileComponent, ClassFileInputStream classFileInputStream, ListMultimap listMultimap, ListMultimap listMultimap1
    ) throws IOException {
        super(classFileComponent);
        int ba = classFileInputStream.readUnsignedShort();
        int bb = classFileInputStream.readUnsignedShort();
        int bc = classFileInputStream.readUnsignedShort();
        this.accessFlags = classFileInputStream.readUnsignedShort();
        ConstantPoolEntry constantPoolEntry = this.getConstantPoolEntry(ba);
        ConstantPoolEntry constantPoolEntry1 = this.getConstantPoolEntry(bb);
        ConstantPoolEntry constantPoolEntry2 = this.getConstantPoolEntry(bc);
        if (constantPoolEntry instanceof ResolvedClassConstant) {
            if (bb != 0 && !(constantPoolEntry1 instanceof ResolvedClassConstant)) {
                this.valid = false;
            } else {
                this.innerClass = (ResolvedClassConstant) constantPoolEntry;
                listMultimap1.addValue(this.innerClass, this);
                if (bb != 0) {
                    this.outerClass = (ResolvedClassConstant) constantPoolEntry1;
                    listMultimap1.addValue(this.outerClass, this);
                }

                if (bc != 0) {
                    if (constantPoolEntry2 instanceof ConstantUtf8) {
                        this.innerName = (ConstantUtf8) constantPoolEntry2;
                        listMultimap.addValue(this.innerName, this);
                    } else {
                        this.valid = false;
                    }
                }
            }
        } else {
            this.valid = false;
        }

        if (!this.valid) {
            this.rawBytes = new byte[8];
            this.rawBytes[0] = (byte) ByteConversionUtils.highByte(ba);
            this.rawBytes[1] = (byte) ByteConversionUtils.lowByte(ba);
            this.rawBytes[2] = (byte) ByteConversionUtils.highByte(bb);
            this.rawBytes[3] = (byte) ByteConversionUtils.lowByte(bb);
            this.rawBytes[4] = (byte) ByteConversionUtils.highByte(bc);
            this.rawBytes[5] = (byte) ByteConversionUtils.lowByte(bc);
            this.rawBytes[6] = (byte) ByteConversionUtils.highByte(this.accessFlags);
            this.rawBytes[7] = (byte) ByteConversionUtils.lowByte(this.accessFlags);
        }
    }

    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
        if (this.valid) {
            dataOutputStream.writeShort(this.innerClass.getIndex());
            dataOutputStream.writeShort(this.outerClass == null ? 0 : this.outerClass.getIndex());
            dataOutputStream.writeShort(this.innerName == null ? 0 : this.innerName.getIndex());
            dataOutputStream.writeShort(this.accessFlags);
        } else {
            dataOutputStream.write(this.rawBytes);
        }
    }

    public int getAccessFlags() {
        return this.accessFlags;
    }

    @Override
    public void collectUsedConstants(char bb, int ba, UsedConstantsCollector usedConstantsCollector, char bc) {
        VisitableNode[] visitableNodes1 = ClassFileComponent.getFlowGuardNodes();
        if (this.innerClass != null) {
            this.innerClass.registerUsage(usedConstantsCollector, this, this);
        }

        InnerClassEntry innerClassEntry2 = this;
        if (ba > 0) {
            if (this.outerClass != null) {
                this.outerClass.registerUsage(usedConstantsCollector, this, this);
            }

            innerClassEntry2 = this;
        }

        if (innerClassEntry2.innerName != null) {
            this.innerName.registerUsage(usedConstantsCollector, this, this);
        }
    }

    public String getInnerSimpleName() {
        if (this.innerName != null) {
            String string = this.innerName.getValue();
            if (!string.equals("")) {
                return string;
            }
        }

        return null;
    }

    @Override
    public void replaceClassConstant(ResolvedClassConstant resolvedClassConstant, ResolvedClassConstant resolvedClassConstant1) {
        if (resolvedClassConstant == this.innerClass) {
            this.innerClass = resolvedClassConstant1;
        }

        if (resolvedClassConstant == this.outerClass) {
            this.outerClass = resolvedClassConstant1;
        }
    }

    public void writeRemapped(DataOutputStream dataOutputStream, Map map1) throws IOException {
        if (this.valid) {
            ConstantPoolEntry constantPoolEntry = (ConstantPoolEntry) map1.get(this.innerClass);
            if (constantPoolEntry != null) {
                dataOutputStream.writeShort(constantPoolEntry.getIndex());
            } else {
                dataOutputStream.writeShort(this.innerClass.getIndex());
            }

            if (this.outerClass != null) {
                ConstantPoolEntry constantPoolEntry1 = (ConstantPoolEntry) map1.get(this.outerClass);
                if (constantPoolEntry1 != null) {
                    dataOutputStream.writeShort(constantPoolEntry1.getIndex());
                } else {
                    dataOutputStream.writeShort(this.outerClass.getIndex());
                }
            } else {
                dataOutputStream.writeShort(0);
            }

            if (this.innerName == null) {
                dataOutputStream.writeShort(0);
            } else {
                ConstantUtf8 constantUtf8 = (ConstantUtf8) map1.get(this.innerName);
                if (constantUtf8 != null) {
                    dataOutputStream.writeShort(constantUtf8.getIndex());
                } else {
                    dataOutputStream.writeShort(this.innerName.getIndex());
                }
            }

            dataOutputStream.writeShort(this.accessFlags);
        } else {
            dataOutputStream.write(this.rawBytes);
        }
    }

    public String getOuterClassName() {
        if (this.outerClass != null) {
            String string = this.outerClass.getClassName();
            if (!string.equals("")) {
                return string;
            }
        }

        return null;
    }

    public String getInnerClassName() {
        if (this.innerClass != null) {
            String string = this.innerClass.getClassName();
            if (!string.equals("")) {
                return string;
            }
        }

        return null;
    }

    public boolean isInnerClassTrimmed(TrimProcessor trimProcessor1) {
        ProgramClass programClass1 = ClassHierarchyNode.findProgramClass(this.innerClass.getClassName());
        return programClass1 != null ? !trimProcessor1.isClassOrOuterExcluded(programClass1) : false;
    }

    public void collectReferencedClasses(HashSet hashSet) {
        if (this.outerClass != null) {
            ProgramClass programClass1 = ClassHierarchyNode.findProgramClass(this.outerClass.getClassName());
            if (programClass1 != null) {
                hashSet.add(programClass1);
            }
        }

        if (this.innerClass != null) {
            ProgramClass programClass2 = ClassHierarchyNode.findProgramClass(this.innerClass.getClassName());
            if (programClass2 != null) {
                hashSet.add(programClass2);
            }
        }
    }
}
