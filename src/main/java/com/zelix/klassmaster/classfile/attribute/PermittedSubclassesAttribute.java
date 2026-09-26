


package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.attribute.ParsedAttributeBase;
import com.zelix.klassmaster.classfile.constpool.ClassConstantReplaceable;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ConstantUtf8;
import com.zelix.klassmaster.classfile.constpool.ResolvedClassConstant;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.classfile.constpool.Utf8ConstantReplaceable;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.obfuscator.trim.TrimProcessor;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

public class PermittedSubclassesAttribute
        extends ParsedAttributeBase
        implements Utf8ConstantReplaceable,
        ClassConstantReplaceable {
    public ResolvedClassConstant[] permittedSubclasses;

    public int removeTrimmedSubclasses(TrimProcessor trimProcessor) {
        if (this.valid) {
            ArrayList<ResolvedClassConstant> arrayList = new ArrayList<ResolvedClassConstant>(this.permittedSubclasses.length);
            for (ResolvedClassConstant resolvedClassConstant : this.permittedSubclasses) {
                ProgramClass programClass = ClassHierarchyNode.findProgramClass(resolvedClassConstant.getClassName());
                if (programClass != null && !trimProcessor.isClassOrOuterExcluded(programClass)) continue;
                arrayList.add(resolvedClassConstant);
            }
            if (arrayList.size() < this.permittedSubclasses.length) {
                this.permittedSubclasses = arrayList.toArray(new ResolvedClassConstant[arrayList.size()]);
            }
        }
        return this.permittedSubclasses.length;
    }

    @Override
    public void replaceUtf8Constant(ConstantUtf8 constantUtf8, ConstantUtf8 constantUtf82) {
        super.replaceUtf8Constant(constantUtf8, constantUtf82);
    }

    


    @Override
    public void replaceClassConstant(ResolvedClassConstant resolvedClassConstant, ResolvedClassConstant resolvedClassConstant2) {
        int n = 0;
        int n2 = 0;
        ResolvedClassConstant[] resolvedClassConstantArray = this.permittedSubclasses;
        while (n2 < resolvedClassConstantArray.length) {
            if (this.permittedSubclasses[n] == resolvedClassConstant) {
                this.permittedSubclasses[n] = resolvedClassConstant2;
                return;
            }
            n2 = ++n;
            resolvedClassConstantArray = this.permittedSubclasses;
        }
    }

    public void collectReferencedClasses(HashSet hashSet) {
        if (this.valid) {
            ResolvedClassConstant[] resolvedClassConstantArray = this.permittedSubclasses;
            int n = resolvedClassConstantArray.length;
            for (int i = 0; i < n; ++i) {
                ProgramClass programClass = ClassHierarchyNode.findProgramClass(resolvedClassConstantArray[i].getClassName());
                if (programClass == null) continue;
                hashSet.add(programClass);
            }
        }
    }

    @Override
    public void writeRemapped(DataOutputStream dataOutputStream, Object object, Object object2) throws IOException {
        super.write(dataOutputStream);
        if (this.valid) {
            dataOutputStream.writeShort(this.permittedSubclasses.length);
            for (ResolvedClassConstant resolvedClassConstant : this.permittedSubclasses) {
                ConstantPoolEntry constantPoolEntry = (ConstantPoolEntry) ((Map) object).get(resolvedClassConstant);
                if (constantPoolEntry != null) {
                    dataOutputStream.writeShort(constantPoolEntry.getIndex());
                    continue;
                }
                dataOutputStream.writeShort(resolvedClassConstant.getIndex());
            }
        } else {
            dataOutputStream.write(this.rawBytes);
        }
    }

    @Override
    public void remapClassNames(Object object, Object object2, Object object3, Object object4) throws ZkmProcessingException {
    }

    @Override
    public void collectUsedConstants(char c, int n, UsedConstantsCollector usedConstantsCollector, char c2) {
        usedConstantsCollector.markUsed(this.nameConstant, this, this.getParent());
        ResolvedClassConstant[] resolvedClassConstantArray = this.permittedSubclasses;
        int n2 = resolvedClassConstantArray.length;
        for (int i = 0; i < n2; ++i) {
            resolvedClassConstantArray[i].registerUsage(usedConstantsCollector, this, this);
        }
    }

    




    public PermittedSubclassesAttribute(ClassFileComponent classFileComponent, int n, String string, ClassFileInputStream classFileInputStream, ListMultimap listMultimap, ListMultimap listMultimap2, PrintWriter printWriter) throws IOException {
        super(classFileComponent, n, string, classFileInputStream, listMultimap);
        block9:
        {
            byte[] byArray = new byte[this.length];
            classFileInputStream.read(byArray);
            ClassFileInputStream classFileInputStream2 = ClassFileInputStream.fromBytes(byArray, false);
            Throwable throwable = null;
            try {
                int n2 = classFileInputStream2.readUnsignedShort();
                this.permittedSubclasses = new ResolvedClassConstant[n2];
                for (int i = 0; i < n2; ++i) {
                    int n3 = classFileInputStream2.readUnsignedShort();
                    ConstantPoolEntry constantPoolEntry = this.getConstantPoolEntry(n3);
                    if (constantPoolEntry instanceof ResolvedClassConstant) {
                        this.permittedSubclasses[i] = (ResolvedClassConstant) constantPoolEntry;
                        listMultimap2.addValue(this.permittedSubclasses[i], this);
                        continue;
                    }
                    this.valid = false;
                    this.rawBytes = byArray;
                    printWriter.println("ERROR: " + this.getDisplayLocationName() + " : " + this.getAttributeName() + " (A) Invalid permitted class index " + n3);
                }
                if (classFileInputStream2 == null) break block9;
            } catch (Throwable throwable2) {
                try {
                    throwable = throwable2;
                    throw throwable2;
                } catch (Throwable throwable3) {
                    if (classFileInputStream2 == null) throw throwable3;
                    if (throwable == null) {
                        classFileInputStream2.close();
                        throw throwable3;
                    }
                    try {
                        classFileInputStream2.close();
                        throw throwable3;
                    } catch (Throwable throwable4) {
                        throwable.addSuppressed(throwable4);
                        throw throwable3;
                    }
                }
            }
            classFileInputStream2.close();
        }
        if (!this.valid) return;
        ((ClassFileBase) classFileComponent).setHasPermittedSubclasses();
    }

    @Override
    public int getLength() {
        int n = 2 + this.permittedSubclasses.length * 2;
        this.replaceLength(n);
        return n;
    }

    @Override
    public void write(DataOutputStream dataOutputStream) throws IOException {
        super.write(dataOutputStream);
        if (this.valid) {
            dataOutputStream.writeShort(this.permittedSubclasses.length);
            for (ResolvedClassConstant resolvedClassConstant : this.permittedSubclasses) {
                dataOutputStream.writeShort(resolvedClassConstant.getIndex());
            }
        } else {
            dataOutputStream.write(this.rawBytes);
        }
    }
}
