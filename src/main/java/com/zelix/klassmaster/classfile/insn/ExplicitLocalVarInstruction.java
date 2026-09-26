package com.zelix.klassmaster.classfile.insn;

import com.zelix.klassmaster.classfile.ClassFileInputStream;

import java.io.DataOutputStream;
import java.io.IOException;

public class ExplicitLocalVarInstruction extends LocalVariableInstruction {
    @Override
    public int getRealOpcode() {
        this.slot.getIndex();
        switch (LocalVarOpSwitchMap.ACCESS_KIND_SWITCH_MAP[this.accessKind.ordinal()]) {
            case 1:
                return 21;
            case 2:
                return 54;
            case 3:
                return 22;
            case 4:
                return 55;
            case 5:
                return 23;
            case 6:
                return 56;
            case 7:
                return 24;
            case 8:
                return 57;
            case 9:
                return 25;
            case 10:
                return 58;
            case 11:
                return 132;
            case 12:
                return 169;
            default:
                return -1;
        }
    }

    public ExplicitLocalVarInstruction(int ba, int bb, int bc, ClassFileInputStream classFileInputStream, LocalVariableProvider localVariableProvider) throws IOException {
        super(ba, bb, bc, classFileInputStream, localVariableProvider);
    }

    @Override
    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
        if (this.requiresWidePrefix()) {
            dataOutputStream.writeByte(196);
        }

        int index = this.slot.getIndex();
        switch (LocalVarOpSwitchMap.ACCESS_KIND_SWITCH_MAP[this.accessKind.ordinal()]) {
            case 1:
                dataOutputStream.writeByte(21);
                if (this.requiresWidePrefix()) {
                    dataOutputStream.writeShort(index);
                } else {
                    dataOutputStream.writeByte(index);
                }
                break;
            case 2:
                dataOutputStream.writeByte(54);
                if (this.requiresWidePrefix()) {
                    dataOutputStream.writeShort(index);
                } else {
                    dataOutputStream.writeByte(index);
                }
                break;
            case 3:
                dataOutputStream.writeByte(22);
                if (this.requiresWidePrefix()) {
                    dataOutputStream.writeShort(index);
                } else {
                    dataOutputStream.writeByte(index);
                }
                break;
            case 4:
                dataOutputStream.writeByte(55);
                if (this.requiresWidePrefix()) {
                    dataOutputStream.writeShort(index);
                } else {
                    dataOutputStream.writeByte(index);
                }
                break;
            case 5:
                dataOutputStream.writeByte(23);
                if (this.requiresWidePrefix()) {
                    dataOutputStream.writeShort(index);
                } else {
                    dataOutputStream.writeByte(index);
                }
                break;
            case 6:
                dataOutputStream.writeByte(56);
                if (this.requiresWidePrefix()) {
                    dataOutputStream.writeShort(index);
                } else {
                    dataOutputStream.writeByte(index);
                }
                break;
            case 7:
                dataOutputStream.writeByte(24);
                if (this.requiresWidePrefix()) {
                    dataOutputStream.writeShort(index);
                } else {
                    dataOutputStream.writeByte(index);
                }
                break;
            case 8:
                dataOutputStream.writeByte(57);
                if (this.requiresWidePrefix()) {
                    dataOutputStream.writeShort(index);
                } else {
                    dataOutputStream.writeByte(index);
                }
                break;
            case 9:
                dataOutputStream.writeByte(25);
                if (this.requiresWidePrefix()) {
                    dataOutputStream.writeShort(index);
                } else {
                    dataOutputStream.writeByte(index);
                }
                break;
            case 10:
                dataOutputStream.writeByte(58);
                if (this.requiresWidePrefix()) {
                    dataOutputStream.writeShort(index);
                } else {
                    dataOutputStream.writeByte(index);
                }
                break;
            case 11:
                dataOutputStream.writeByte(132);
                if (this.requiresWidePrefix()) {
                    dataOutputStream.writeShort(index);
                    dataOutputStream.writeShort(this.getIncrement());
                } else {
                    dataOutputStream.writeByte(index);
                    dataOutputStream.writeByte(this.getIncrement());
                }
                break;
            case 12:
                dataOutputStream.writeByte(169);
                if (this.requiresWidePrefix()) {
                    dataOutputStream.writeShort(index);
                } else {
                    dataOutputStream.writeByte(index);
                }
        }
    }
}
