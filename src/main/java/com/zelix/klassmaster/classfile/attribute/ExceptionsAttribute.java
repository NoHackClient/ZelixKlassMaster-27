package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.constpool.ClassConstantReplaceable;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ConstantUtf8;
import com.zelix.klassmaster.classfile.constpool.ResolvedClassConstant;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.exceptions.ClassFileFormatException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.ArrayEnumeration;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

public class ExceptionsAttribute extends Attribute implements ClassConstantReplaceable {
    public int exceptionCount;
    public ResolvedClassConstant[] exceptionClasses;

    public Enumeration getExceptionClassNames() {
        String[] strings = new String[this.exceptionClasses.length];
        int ba = 0;
        int bb = 0;

        for (ResolvedClassConstant[] resolvedClassConstants = this.exceptionClasses;
             bb < resolvedClassConstants.length;
             resolvedClassConstants = this.exceptionClasses
        ) {
            strings[ba] = this.exceptionClasses[ba].getClassName();
            bb = ++ba;
        }

        return new ArrayEnumeration(strings);
    }

    @Override
    public void write(DataOutputStream dataOutputStream) throws IOException {
        super.write(dataOutputStream);
        dataOutputStream.writeShort(this.exceptionCount);
        int ba = 0;
        int bb = 0;

        for (int i = this.exceptionCount; bb < i; i = this.exceptionCount) {
            dataOutputStream.writeShort(this.exceptionClasses[ba].getIndex());
            bb = ++ba;
        }
    }

    public ResolvedClassConstant getExceptionClass(int ba) {
        return this.exceptionClasses[ba];
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
        dataOutputStream.writeShort(this.exceptionCount);
        int ba = 0;
        int bb = 0;

        for (int i = this.exceptionCount; bb < i; i = this.exceptionCount) {
            ConstantPoolEntry constantPoolEntry = (ConstantPoolEntry) map1.get(this.exceptionClasses[ba]);
            if (constantPoolEntry != null) {
                dataOutputStream.writeShort(constantPoolEntry.getIndex());
            } else {
                dataOutputStream.writeShort(this.exceptionClasses[ba].getIndex());
            }

            bb = ++ba;
        }
    }

    public Enumeration getExceptionClasses() {
        ResolvedClassConstant[] resolvedClassConstants = new ResolvedClassConstant[this.exceptionClasses.length];
        int ba = 0;
        int bb = 0;

        for (ResolvedClassConstant[] resolvedClassConstants1 = this.exceptionClasses;
             bb < resolvedClassConstants1.length;
             resolvedClassConstants1 = this.exceptionClasses
        ) {
            resolvedClassConstants[ba] = this.exceptionClasses[ba];
            bb = ++ba;
        }

        return new ArrayEnumeration(resolvedClassConstants);
    }

    @Override
    public void collectUsedConstants(char ba, int bd, UsedConstantsCollector usedConstantsCollector, char be) {
        usedConstantsCollector.markUsed(this.nameConstant, this, this);
        int bb = 0;
        int bc = 0;
        ResolvedClassConstant[] resolvedClassConstants = this.exceptionClasses;

        while (true) {
            if (bc < resolvedClassConstants.length) {
                this.exceptionClasses[bb].registerUsage(usedConstantsCollector, this, this);
            } else if (ba >= 0) {
                return;
            }

            bc = ++bb;
            resolvedClassConstants = this.exceptionClasses;
        }
    }

    @Override
    public void replaceClassConstant(ResolvedClassConstant resolvedClassConstant, ResolvedClassConstant resolvedClassConstant1) {
        int ba = 0;
        int bb = 0;

        for (ResolvedClassConstant[] resolvedClassConstants = this.exceptionClasses;
             bb < resolvedClassConstants.length;
             resolvedClassConstants = this.exceptionClasses
        ) {
            if (this.exceptionClasses[ba] == resolvedClassConstant) {
                this.exceptionClasses[ba] = resolvedClassConstant1;
                break;
            }

            bb = ++ba;
        }
    }

    public ExceptionsAttribute(ConstantUtf8 constantUtf8, ResolvedClassConstant[] resolvedClassConstants) {
        super(null, constantUtf8, resolvedClassConstants.length * 2 + 2);
        this.exceptionCount = resolvedClassConstants.length;
        this.exceptionClasses = resolvedClassConstants;
    }

    public int getExceptionCount() {
        return this.exceptionCount;
    }

    public ExceptionsAttribute(
            ClassFileComponent classFileComponent,
            int ba,
            String string,
            ClassFileInputStream classFileInputStream,
            ListMultimap listMultimap,
            ListMultimap listMultimap1
    ) throws ClassFileFormatException, IOException {
        super(classFileComponent, ba, string, classFileInputStream, listMultimap);
        this.exceptionCount = classFileInputStream.readUnsignedShort();
        this.exceptionClasses = new ResolvedClassConstant[this.exceptionCount];
        int bb = 0;
        int bd = 0;

        for (int i = this.exceptionCount; bd < i; i = this.exceptionCount) {
            int bc = classFileInputStream.readUnsignedShort();
            ConstantPoolEntry constantPoolEntry = this.getConstantPoolEntry(bc);
            if (!(constantPoolEntry instanceof ResolvedClassConstant)) {
                throw new ClassFileFormatException(this.getClassName() + " : " + "Invalid Class index in Exception Attribute");
            }

            this.exceptionClasses[bb] = (ResolvedClassConstant) constantPoolEntry;
            listMultimap1.addValue(this.exceptionClasses[bb], this);
            bd = ++bb;
        }
    }
}
