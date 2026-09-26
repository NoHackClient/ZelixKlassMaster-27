package com.zelix.klassmaster.classfile.insn;

import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.ZkmAssert;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class NewArrayInstruction extends Instruction {
    public static String[] typeNamesByCode = new String[12];
    public static Map typeCodesByDescriptor = ZkmUtils.createHashMap();
    public int arrayTypeCode;

    @Override
    public boolean pushesWideValue() {
        return false;
    }

    public NewArrayInstruction(ClassFileInputStream classFileInputStream) throws IOException {
        super(188);
        this.arrayTypeCode = classFileInputStream.read();
    }

    @Override
    public final boolean canFallThrough() {
        return true;
    }

    @Override
    public String toAssembly() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.getMnemonic());
        stringBuilder.append(' ');
        stringBuilder.append(typeNamesByCode[this.arrayTypeCode]);
        return stringBuilder.toString();
    }

    @Override
    public StackFrameState computeFrameAfter(StackFrameState stackFrameState, Object object, Object object1, Object object2) throws ZkmException, IOException {
        VerifierType[] verifierTypes = stackFrameState.getStack();
        VerifierType[] verifierTypes1 = stackFrameState.getLocals();
        int ba = verifierTypes.length;
        VerifierType[] verifierTypes2 = VerifierType.createArray(ba);
        System.arraycopy(verifierTypes, 0, verifierTypes2, 0, ba - 1);
        switch (this.arrayTypeCode) {
            case 4:
                verifierTypes2[ba - 1] = VerifierType.BOOLEAN_ARRAY;
                break;
            case 5:
                verifierTypes2[ba - 1] = VerifierType.CHAR_ARRAY;
                break;
            case 6:
                verifierTypes2[ba - 1] = VerifierType.FLOAT_ARRAY;
                break;
            case 7:
                verifierTypes2[ba - 1] = VerifierType.DOUBLE_ARRAY;
                break;
            case 8:
                verifierTypes2[ba - 1] = VerifierType.BYTE_ARRAY;
                break;
            case 9:
                verifierTypes2[ba - 1] = VerifierType.SHORT_ARRAY;
                break;
            case 10:
                verifierTypes2[ba - 1] = VerifierType.INT_ARRAY;
                break;
            case 11:
                verifierTypes2[ba - 1] = VerifierType.LONG_ARRAY;
                break;
            default:
                Integer integer = this.arrayTypeCode;
                ZkmAssert.assertTrueWithCode(integer);
        }

        return new StackFrameState(verifierTypes2, verifierTypes1, stackFrameState.getSubroutineLocals(), stackFrameState.getHeldMonitors());
    }

    @Override
    public void printDisassembly(PrintWriter printWriter, StringBuilder stringBuilder) throws UnknownOpcodeException {
        StringBuilder stringBuilder1 = new StringBuilder(100);
        String string = this.getMnemonic();
        stringBuilder1.append(string + " " + this.arrayTypeCode);
        String string1 = typeNamesByCode[this.arrayTypeCode];
        String string2 = this.getDescriptionComment();
        string2 = Instruction.formatDescription(string2, string1);
        if (string2.length() > 0) {
            stringBuilder1.append("\t" + string2);
        }

        printWriter.println(stringBuilder.toString() + stringBuilder.toString() + stringBuilder1);
    }

    public NewArrayInstruction(String string) {
        super(188);
        this.arrayTypeCode = (Integer) typeCodesByDescriptor.get(string);
    }

    public NewArrayInstruction(int arrayTypeCode) {
        super(188);
        this.arrayTypeCode = arrayTypeCode;
    }

    @Override
    public final boolean requiresTypedValueAt(Object object, Object object2, Object object1) {
        int ba = (Integer) object1;
        return (Integer) object == ba - 1;
    }

    @Override
    public final boolean consumesStackSlot(int ba, int bb) {
        return ba == bb - 1;
    }

    @Override
    public boolean pushesWithoutPopping() {
        return false;
    }

    @Override
    public int getLength() {
        return 2;
    }

    static {
        typeNamesByCode[4] = "boolean";
        typeNamesByCode[5] = "char";
        typeNamesByCode[6] = "float";
        typeNamesByCode[7] = "double";
        typeNamesByCode[8] = "byte";
        typeNamesByCode[9] = "short";
        typeNamesByCode[10] = "int";
        typeNamesByCode[11] = "long";
        typeCodesByDescriptor.put("Z", 4);
        typeCodesByDescriptor.put("C", 5);
        typeCodesByDescriptor.put("F", 6);
        typeCodesByDescriptor.put("D", 7);
        typeCodesByDescriptor.put("B", 8);
        typeCodesByDescriptor.put("S", 9);
        typeCodesByDescriptor.put("I", 10);
        typeCodesByDescriptor.put("J", 11);
    }

    @Override
    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
        super.writeTo(dataOutputStream);
        dataOutputStream.writeByte(this.arrayTypeCode);
    }

    @Override
    public int getStackDelta() {
        return 0;
    }

    @Override
    public final boolean isExit() {
        return false;
    }

    @Override
    public boolean pushesValue() {
        return true;
    }
}
