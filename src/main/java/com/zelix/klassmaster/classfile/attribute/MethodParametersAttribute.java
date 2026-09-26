package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.constpool.AbstractConstantPool;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.obfuscator.parameters.ChangedMethodDescriptor;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class MethodParametersAttribute extends ParsedAttributeBase {
    private MethodParameterEntry[] parameters;

    @Override
    public void collectUsedConstants(char bc, int bd, UsedConstantsCollector usedConstantsCollector, char be) {
        usedConstantsCollector.markUsed(this.nameConstant, this, this.getParent());
        if (super.valid) {
            int ba = this.parameters.length;

            for (int i = 0; i < ba; i++) {
                this.parameters[i].collectUsedConstants('\u0000', 1103830477, usedConstantsCollector, '鞍');
            }
        }
    }

    public MethodParametersAttribute(
            ClassFileComponent classFileComponent,
            int ba,
            String string,
            ClassFileInputStream classFileInputStream,
            ListMultimap listMultimap,
            PrintWriter printWriter
    ) throws IOException {
        super(classFileComponent, ba, string, classFileInputStream, listMultimap);
        byte[] bb = new byte[this.length];
        classFileInputStream.read(bb);
        ClassFileInputStream classFileInputStream1 = ClassFileInputStream.fromBytes(bb, false);

        try {
            if (this.length >= 2) {
                int bc = classFileInputStream1.readUnsignedByte();
                this.parameters = new MethodParameterEntry[bc];

                for (int i = 0; i < bc; i++) {
                    this.parameters[i] = new MethodParameterEntry(this, classFileInputStream1, listMultimap);
                    if (!this.parameters[i].isValid()) {
                        super.valid = false;
                        super.rawBytes = bb;
                        printWriter.println(
                                "ERROR: " + this.getDisplayLocationName() + " : " + this.getAttributeName() + " (A) " + this.parameters[i].getErrorMessage()
                        );
                    }
                }
            } else {
                super.valid = false;
                super.rawBytes = bb;
                printWriter.println("ERROR: " + this.getDisplayLocationName() + " : " + this.getAttributeName() + " : length=" + this.length);
            }
        } catch (IOException iOException) {
            super.valid = false;
            super.rawBytes = bb;
            printWriter.println("ERROR: " + this.getDisplayLocationName() + " : " + this.getAttributeName() + " is possibly corrupt : " + iOException);
        } finally {
            classFileInputStream1.close();
        }
    }

    public void insertAddedParameters(ChangedMethodDescriptor changedMethodDescriptor, AbstractConstantPool abstractConstantPool, List list1) {
        int paramCount = changedMethodDescriptor.getParamCount();
        changedMethodDescriptor.getAddedParamCount();
        MethodParameterEntry[] methodParameterEntrys = new MethodParameterEntry[paramCount];
        int[] paramIndices = changedMethodDescriptor.getParamIndices();
        int bc = 0;
        int bd = paramIndices[0];
        int be = 0;

        for (int i = 0; i < paramCount; i++) {
            if (i == bd) {
                methodParameterEntrys[i] = new MethodParameterEntry(this, abstractConstantPool, list1);
                if (++bc < paramIndices.length) {
                    bd = paramIndices[bc];
                } else {
                    bd = 32767;
                }
            } else {
                methodParameterEntrys[i] = this.parameters[be++];
            }
        }

        this.parameters = methodParameterEntrys;
        this.getLength();
    }

    @Override
    public void write(DataOutputStream dataOutputStream) throws IOException {
        super.write(dataOutputStream);
        if (super.valid) {
            int ba = this.parameters.length;
            dataOutputStream.writeByte(ba);

            for (int i = 0; i < ba; i++) {
                this.parameters[i].writeTo(dataOutputStream);
            }
        } else {
            dataOutputStream.write(super.rawBytes);
        }
    }

    public void obfuscateNames() {
        int ba = this.parameters.length;

        for (int i = 0; i < ba; i++) {
            this.parameters[i].obfuscateName();
        }
    }

    @Override
    public int getLength() {
        if (!super.valid) {
            return super.rawBytes.length;
        }

        int ba = 1;
        int bb = this.parameters.length;

        for (int i = 0; i < bb; i++) {
            ba += this.parameters[i].getEntrySize();
        }

        this.length = ba;
        return this.length;
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
            int ba = this.parameters.length;
            dataOutputStream.writeByte(ba);

            for (int i = 0; i < ba; i++) {
                this.parameters[i].writeRemapped(dataOutputStream, map1);
            }
        } else {
            dataOutputStream.write(super.rawBytes);
        }
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
    }
}
