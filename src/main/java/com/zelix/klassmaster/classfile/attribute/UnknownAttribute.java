package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ConstantUtf8;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.classfile.constpool.Utf8ConstantReplaceable;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.ByteConversionUtils;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.ThreeKeyMultiMap;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class UnknownAttribute extends Attribute implements Utf8ConstantReplaceable {
    public static final Object NON_UTF8_MARKER = new Object();
    public byte[] rawBytes = new byte[this.length];
    public boolean parsedAsIndices;
    public boolean commonAcrossClasses;
    public int[] rawIndices;
    public ArrayList referencedConstants;

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
        if (this.parsedAsIndices) {
            int ba = 0;
            int bb = ba;

            for (ArrayList arrayList = this.referencedConstants; bb < arrayList.size(); arrayList = this.referencedConstants) {
                Object object2 = this.referencedConstants.get(ba);
                if (object2 == null) {
                    dataOutputStream.writeShort(this.rawIndices[ba]);
                } else {
                    ConstantPoolEntry constantPoolEntry = (ConstantPoolEntry) object2;
                    ConstantPoolEntry constantPoolEntry1 = (ConstantPoolEntry) map1.get(constantPoolEntry);
                    if (constantPoolEntry1 != null) {
                        dataOutputStream.writeShort(constantPoolEntry1.getIndex());
                    } else {
                        dataOutputStream.writeShort(constantPoolEntry.getIndex());
                    }
                }

                bb = ++ba;
            }
        } else {
            dataOutputStream.write(this.rawBytes);
        }
    }

    public void setParsedAsIndices() {
        this.parsedAsIndices = false;
        if (!this.parsedAsIndices) {
            this.rawIndices = null;
            this.referencedConstants = null;
            this.commonAcrossClasses = false;
        }
    }

    public boolean isCommonAcrossClasses() {
        return this.commonAcrossClasses;
    }

    public void clearReferenceAt(int ba) {
        this.referencedConstants.set(ba, null);
    }

    @Override
    public void write(DataOutputStream dataOutputStream) throws IOException {
        super.write(dataOutputStream);
        if (this.parsedAsIndices) {
            int ba = 0;
            int bb = ba;

            for (ArrayList arrayList = this.referencedConstants; bb < arrayList.size(); arrayList = this.referencedConstants) {
                Object object = this.referencedConstants.get(ba);
                if (object == null) {
                    dataOutputStream.writeShort(this.rawIndices[ba]);
                } else {
                    ConstantPoolEntry constantPoolEntry = (ConstantPoolEntry) object;
                    dataOutputStream.writeShort(constantPoolEntry.getIndex());
                }

                bb = ++ba;
            }
        } else {
            dataOutputStream.write(this.rawBytes);
        }
    }

    @Override
    public void collectUsedConstants(char ba, int bb, UsedConstantsCollector usedConstantsCollector, char bc) {
        usedConstantsCollector.markUsed(this.nameConstant, this, this.getParent());
    }

    @Override
    public void replaceUtf8Constant(ConstantUtf8 constantUtf8, ConstantUtf8 constantUtf81) {
        if (this.nameConstant == constantUtf8) {
            this.nameConstant = constantUtf81;
        } else {
            if (this.parsedAsIndices) {
                int ba = 0;
                int bb = ba;

                for (ArrayList arrayList = this.referencedConstants; bb < arrayList.size(); arrayList = this.referencedConstants) {
                    if (this.referencedConstants.get(ba) == constantUtf8) {
                        this.referencedConstants.set(ba, constantUtf81);
                        break;
                    }

                    bb = ++ba;
                }
            }
        }
    }

    public UnknownAttribute(
            ClassFileComponent classFileComponent,
            int ba,
            String string,
            ClassFileInputStream classFileInputStream,
            ListMultimap listMultimap,
            ThreeKeyMultiMap threeKeyMultiMap
    ) throws IOException {
        super(classFileComponent, ba, string, classFileInputStream, listMultimap);
        classFileInputStream.read(this.rawBytes);
        if (this.rawBytes.length % 2 == 0) {
            String string1 = classFileComponent.getClass().getName();
            this.parsedAsIndices = true;
            this.commonAcrossClasses = true;
            int bb = this.rawBytes.length / 2;
            this.rawIndices = new int[bb];
            this.referencedConstants = new ArrayList(bb);
            int bc = 0;

            for (int i = 0; i < bb; i++) {
                int be = ByteConversionUtils.bytesToUnsignedShort(this.rawBytes[bc++], this.rawBytes[bc++]);
                this.rawIndices[i] = be;
                ConstantPoolEntry constantPoolEntry = classFileComponent.getConstantPoolEntry(be);
                ArrayList arrayList;
                if (constantPoolEntry != null) {
                    if (constantPoolEntry instanceof ConstantUtf8) {
                        this.referencedConstants.add(constantPoolEntry);
                        threeKeyMultiMap.addValue(string1, this.getAttributeName(), this, constantPoolEntry);
                        listMultimap.addValue((ConstantUtf8) constantPoolEntry, this);
                        continue;
                    }

                    arrayList = this.referencedConstants;
                } else {
                    arrayList = this.referencedConstants;
                }

                arrayList.add(null);
                threeKeyMultiMap.addValue(string1, this.getAttributeName(), this, NON_UTF8_MARKER);
            }
        }
    }

    public void setCommonAcrossClasses(boolean commonAcrossClasses) {
        this.commonAcrossClasses = commonAcrossClasses;
    }
}
