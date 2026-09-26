package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ConstantUtf8;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.classfile.constpool.Utf8ConstantReplaceable;
import com.zelix.klassmaster.exceptions.ClassFileFormatException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class SourceFileAttribute extends Attribute implements Utf8ConstantReplaceable {
    public ConstantUtf8 sourceFileName;

    public void setSourceFileConstant(ConstantPoolEntry constantPoolEntry) throws ClassFileFormatException {
        if (!(constantPoolEntry instanceof ConstantUtf8)) {
            String string = "Class name '"
                    + this.getClassName()
                    + "' : "
                    + "Invalid SourceFile Attribute"
                    + " : "
                    + (constantPoolEntry != null ? constantPoolEntry.getTag() + " " + constantPoolEntry.getIndex() + " " + constantPoolEntry.getClass().getName() : "");
            throw new ClassFileFormatException(string);
        }

        this.sourceFileName = (ConstantUtf8) constantPoolEntry;
    }

    @Override
    public void collectUsedConstants(char ba, int bb, UsedConstantsCollector usedConstantsCollector, char bc) {
        usedConstantsCollector.markUsed(this.nameConstant, this, this.getParent());
        this.sourceFileName.registerUsage(usedConstantsCollector, this, this.getParent());
    }

    @Override
    public void replaceUtf8Constant(ConstantUtf8 constantUtf8, ConstantUtf8 constantUtf81) {
        if (this.sourceFileName == constantUtf8) {
            this.sourceFileName = constantUtf81;
        } else {
            super.replaceUtf8Constant(constantUtf8, constantUtf81);
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
        ConstantUtf8 constantUtf8 = (ConstantUtf8) map1.get(this.sourceFileName);
        if (constantUtf8 != null) {
            dataOutputStream.writeShort(constantUtf8.getIndex());
        } else {
            dataOutputStream.writeShort(this.sourceFileName.getIndex());
        }
    }

    @Override
    public void write(DataOutputStream dataOutputStream) throws IOException {
        super.write(dataOutputStream);
        dataOutputStream.writeShort(this.sourceFileName.getIndex());
    }

    public SourceFileAttribute(
            ClassFileComponent classFileComponent, int ba, String string, ClassFileInputStream classFileInputStream, ListMultimap listMultimap
    ) throws ClassFileFormatException, IOException {
        super(classFileComponent, ba, string, classFileInputStream, listMultimap);
        int bb = classFileInputStream.readUnsignedShort();
        ConstantPoolEntry constantPoolEntry = this.getConstantPoolEntry(bb);
        this.setSourceFileConstant(constantPoolEntry);
        listMultimap.addValue(this.sourceFileName, this);
    }
}
