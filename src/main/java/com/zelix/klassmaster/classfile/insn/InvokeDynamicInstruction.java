package com.zelix.klassmaster.classfile.insn;

import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolProvider;
import com.zelix.klassmaster.classfile.constpool.ResolvedInvokeDynamic;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class InvokeDynamicInstruction extends ConstantRefInstruction {
    @Override
    public void printDisassembly(PrintWriter printWriter, StringBuilder stringBuilder) throws UnknownOpcodeException {
        StringBuilder stringBuilder1 = new StringBuilder(100);
        String string = this.getMnemonic();
        ResolvedInvokeDynamic resolvedInvokeDynamic = (ResolvedInvokeDynamic) this.constantEntry;
        stringBuilder1.append(string + " " + (resolvedInvokeDynamic != null ? resolvedInvokeDynamic.getIndex() : "null"));
        String string1 = Instruction.getDescriptionComment(this.opcode);
        string1 = Instruction.formatDescription(string1, resolvedInvokeDynamic != null ? resolvedInvokeDynamic.getNameAndTypeString() : "null");
        string1 = Instruction.formatDescription(
                string1,
                resolvedInvokeDynamic != null
                        ? (resolvedInvokeDynamic.getBootstrapMethodHandle() != null ? resolvedInvokeDynamic.getBootstrapMethodHandle().getDisplayString() : "null")
                        : "null"
        );
        if (string1.length() > 0) {
            stringBuilder1.append("\t" + string1);
        }

        printWriter.println(stringBuilder.toString() + stringBuilder.toString() + stringBuilder1);
    }

    @Override
    public void writeRemapped(DataOutputStream dataOutputStream, Map map1) throws IOException {
        super.writeRemapped(dataOutputStream, map1);
        dataOutputStream.writeShort(0);
    }

    @Override
    public int getLength() {
        return 5;
    }

    public ResolvedInvokeDynamic getInvokeDynamicRef() {
        return (ResolvedInvokeDynamic) this.constantEntry;
    }

    public InvokeDynamicInstruction(
            ClassFileInputStream classFileInputStream,
            ConstantPoolProvider constantPoolProvider,
            ListMultimap listMultimap,
            ListMultimap listMultimap1,
            ListMultimap listMultimap2,
            ListMultimap listMultimap3,
            ListMultimap listMultimap4,
            ListMultimap listMultimap5
    ) throws IOException {
        super(186, classFileInputStream, constantPoolProvider, listMultimap, listMultimap1, listMultimap2, listMultimap3, listMultimap4);
        classFileInputStream.skip(2L);
        listMultimap5.addValue((ResolvedInvokeDynamic) this.constantEntry, this);
    }

    public InvokeDynamicInstruction(ResolvedInvokeDynamic resolvedInvokeDynamic) {
        super(186, resolvedInvokeDynamic);
    }

    @Override
    public ConstantPoolEntry getConstantPoolEntry() {
        return this.getInvokeDynamicRef();
    }

    @Override
    public String toAssembly() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.getMnemonic());
        stringBuilder.append(' ');
        ResolvedInvokeDynamic resolvedInvokeDynamic = (ResolvedInvokeDynamic) this.constantEntry;
        stringBuilder.append(resolvedInvokeDynamic.getIndex());
        return stringBuilder.toString();
    }

    @Override
    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
        super.writeTo(dataOutputStream);
        dataOutputStream.writeShort(0);
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
