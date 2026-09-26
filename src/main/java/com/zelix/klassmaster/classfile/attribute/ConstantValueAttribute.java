package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.constpool.ConstantInteger;
import com.zelix.klassmaster.classfile.constpool.ConstantIntegerReplacer;
import com.zelix.klassmaster.classfile.constpool.ConstantLong;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ConstantUtf8;
import com.zelix.klassmaster.classfile.constpool.LoadableConstant;
import com.zelix.klassmaster.classfile.constpool.LongConstantReplaceable;
import com.zelix.klassmaster.classfile.constpool.ResolvedStringConstant;
import com.zelix.klassmaster.classfile.constpool.StringConstantReplaceable;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.exceptions.ClassFileFormatException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.ZkmAssert;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class ConstantValueAttribute extends Attribute implements StringConstantReplaceable, ConstantIntegerReplacer, LongConstantReplaceable {
    public ConstantPoolEntry constantValue;

    public ConstantLong getLongConstant() {
        return this.constantValue instanceof ConstantLong ? (ConstantLong) this.constantValue : null;
    }

    @Override
    public void replaceStringConstant(ResolvedStringConstant resolvedStringConstant, ResolvedStringConstant resolvedStringConstant1) {
        if (this.constantValue == resolvedStringConstant) {
            this.constantValue = resolvedStringConstant1;
        }
    }

    @Override
    public void replaceLongConstant(ConstantLong constantLong, ConstantLong constantLong1) {
        if (this.constantValue == constantLong) {
            this.constantValue = constantLong1;
        }
    }

    public ConstantValueAttribute(ClassFileComponent classFileComponent, ConstantUtf8 constantUtf8, LoadableConstant loadableConstant) {
        super(classFileComponent, constantUtf8, 2);
        this.constantValue = (ConstantPoolEntry) loadableConstant;
    }

    public long getLongValue() {
        if (this.constantValue instanceof ConstantLong) {
            return ((ConstantLong) this.constantValue).getValue();
        }

        ZkmAssert.assertTrue(false, new String[]{this.getClassName() + " " + this.constantValue.getClass().getName()});
        return 0L;
    }

    @Override
    public void collectUsedConstants(char ba, int bb, UsedConstantsCollector usedConstantsCollector, char bc) {
        usedConstantsCollector.markUsed(this.nameConstant, this, this.getParent());
        ((LoadableConstant) this.constantValue).registerUsage(usedConstantsCollector, this, this.getParent());
    }

    @Override
    public void write(DataOutputStream dataOutputStream) throws IOException {
        super.write(dataOutputStream);
        dataOutputStream.writeShort(this.constantValue.getIndex());
    }

    @Override
    public void replaceIntegerConstant(ConstantInteger constantInteger, ConstantInteger constantInteger1) {
        if (this.constantValue == constantInteger) {
            this.constantValue = constantInteger1;
        }
    }

    public ResolvedStringConstant getStringConstant() {
        return this.constantValue instanceof ResolvedStringConstant ? (ResolvedStringConstant) this.constantValue : null;
    }

    public ConstantInteger getIntegerConstant() {
        return this.constantValue instanceof ConstantInteger ? (ConstantInteger) this.constantValue : null;
    }

    public ConstantValueAttribute(
            ClassFileComponent classFileComponent,
            int ba,
            String string,
            ClassFileInputStream classFileInputStream,
            ListMultimap listMultimap,
            ListMultimap listMultimap1,
            ListMultimap listMultimap2,
            ListMultimap listMultimap3
    ) throws ClassFileFormatException, IOException {
        super(classFileComponent, ba, string, classFileInputStream, listMultimap);
        int bb = classFileInputStream.readUnsignedShort();
        this.constantValue = this.getConstantPoolEntry(bb);
        ConstantPoolEntry constantPoolEntry;
        if (this.constantValue instanceof ResolvedStringConstant) {
            listMultimap1.addValue((ResolvedStringConstant) this.constantValue, this);
            constantPoolEntry = this.constantValue;
        } else if (this.constantValue instanceof ConstantInteger) {
            listMultimap2.addValue((ConstantInteger) this.constantValue, this);
            constantPoolEntry = this.constantValue;
        } else if (this.constantValue instanceof ConstantLong) {
            listMultimap3.addValue((ConstantLong) this.constantValue, this);
            constantPoolEntry = this.constantValue;
        } else {
            constantPoolEntry = this.constantValue;
        }

        if (!(constantPoolEntry instanceof LoadableConstant)) {
            throw new ClassFileFormatException(this.getLocationName() + " : " + "Invalid ConstantValue Attribute");
        }
    }

    @Override
    public void writeRemapped(DataOutputStream dataOutputStream, Object object, Object object1) throws IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object1;
        Map map1 = (Map) object;
        ScriptEnvironment scriptEnvironment2 = scriptEnvironment1;
        Map map2 = map1;
        ScriptEnvironment scriptEnvironment3 = scriptEnvironment2;
        Map map3 = map2;
        DataOutputStream dataOutputStream1 = dataOutputStream;
        super.writeRemapped(dataOutputStream1, map3, scriptEnvironment3);
        ConstantPoolEntry constantPoolEntry = (ConstantPoolEntry) map1.get(this.constantValue);
        if (constantPoolEntry != null) {
            dataOutputStream.writeShort(constantPoolEntry.getIndex());
        } else {
            dataOutputStream.writeShort(this.constantValue.getIndex());
        }
    }
}
