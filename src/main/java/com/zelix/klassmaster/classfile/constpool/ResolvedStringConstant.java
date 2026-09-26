package com.zelix.klassmaster.classfile.constpool;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class ResolvedStringConstant extends NumericConstantEntry implements Utf8ConstantReplaceable, ConstantKeyProvider {
    public static final ConstantPoolTag TAG = ConstantPoolTag.STRING;
    public ConstantUtf8 valueUtf8;
    public ResolvedStringConstant sourceConstant;
    public boolean fromClassFile;

    @Override
    public void replaceUtf8Constant(ConstantUtf8 constantUtf8, ConstantUtf8 constantUtf81) {
        if (this.valueUtf8 == constantUtf8) {
            this.valueUtf8 = constantUtf81;
        }
    }

    @Override
    public final String getEditableValue() {
        return this.getValueString();
    }

    @Override
    public String getAbbreviatedValue() {
        String string = ZkmUtils.escapeJavaString(this.getValueString());
        if (string.length() > 200) {
            string = string.substring(0, 50) + "...<" + (string.length() - 100) + " CHARS>..." + string.substring(string.length() - 50);
        }

        return string;
    }

    @Override
    public String getConstantKey() {
        return this.valueUtf8.getValue();
    }

    @Override
    public ConstantPoolTag getTag() {
        return TAG;
    }

    public ResolvedStringConstant getSourceConstant() {
        return this.sourceConstant;
    }

    @Override
    public String getDisplayString() {
        return "\"" + this.getValueString() + "\"";
    }

    public ResolvedStringConstant(
            int ba,
            AbstractConstantPool abstractConstantPool,
            ConstantUtf8 constantUtf8,
            ResolvedStringConstant resolvedStringConstant1,
            ListMultimap listMultimap,
            boolean fromClassFile
    ) {
        super(ba, abstractConstantPool);
        this.valueUtf8 = constantUtf8;
        if (listMultimap != null) {
            listMultimap.addValue(constantUtf8, this);
            this.sourceConstant = resolvedStringConstant1;
        } else {
            this.sourceConstant = resolvedStringConstant1;
        }

        this.fromClassFile = fromClassFile;
    }

    @Override
    public String getTypeName() {
        return "string";
    }

    @Override
    public boolean registerUsage(UsedConstantsCollector usedConstantsCollector, Object object, Object object1) {
        return usedConstantsCollector.markUsed(this, object, object1);
    }

    public ResolvedStringConstant(AbstractConstantPool abstractConstantPool, ConstantUtf8 constantUtf8, boolean bl) {
        this(0, abstractConstantPool, constantUtf8, null, null, bl);
    }

    public boolean isFromClassFile() {
        return this.fromClassFile;
    }

    public ResolvedStringConstant(ConstantString constantString, ConstantUtf8 constantUtf8, ListMultimap listMultimap) {
        super(constantString.index, constantString.constantPool);
        this.valueUtf8 = constantUtf8;
        listMultimap.addValue(constantUtf8, this);
        this.fromClassFile = true;
    }

    @Override
    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(TAG.getTagValue());
        dataOutputStream.writeShort(this.valueUtf8.getIndex());
    }

    public ConstantUtf8 getValueUtf8() {
        return this.valueUtf8;
    }

    @Override
    public void writeRemappedTo(DataOutputStream dataOutputStream, Map map1) throws IOException {
        dataOutputStream.writeByte(TAG.getTagValue());
        ConstantUtf8 constantUtf8 = (ConstantUtf8) map1.get(this.valueUtf8);
        if (constantUtf8 != null) {
            dataOutputStream.writeShort(constantUtf8.getIndex());
        } else {
            dataOutputStream.writeShort(this.valueUtf8.getIndex());
        }
    }

    @Override
    public void setValueFromString(String string) throws ZkmException, IOException {
        this.valueUtf8.setValue(string);
        this.notifyObservers();
    }

    @Override
    public String getValueString() {
        return this.valueUtf8.getValue();
    }

    public void setFromClassFile() {
        this.fromClassFile = false;
    }
}
