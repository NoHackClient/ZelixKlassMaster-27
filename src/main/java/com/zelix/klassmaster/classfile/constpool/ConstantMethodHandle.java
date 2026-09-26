package com.zelix.klassmaster.classfile.constpool;

import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.exceptions.ClassFileFormatException;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.DataOutputStream;
import java.io.IOException;

public class ConstantMethodHandle extends MethodHandleConstantBase implements ResolvableConstant {
    public final int referenceKind;
    public final int referenceIndex;

    @Override
    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(15);
        dataOutputStream.writeByte(this.referenceKind);
        dataOutputStream.writeShort(this.referenceIndex);
    }

    @Override
    public ConstantPoolEntry resolve(ListMultimap listMultimap1, ListMultimap listMultimap2, ListMultimap listMultimap3, ListMultimap listMultimap) throws ClassFileFormatException {
        return this.resolveMethodHandle(listMultimap);
    }

    @Override
    public boolean isUnresolved() {
        return true;
    }

    public ConstantMethodHandle(int ba, ClassFileInputStream classFileInputStream, AbstractConstantPool abstractConstantPool) throws IOException {
        super(ba, abstractConstantPool);
        this.referenceKind = classFileInputStream.readUnsignedByte();
        this.referenceIndex = classFileInputStream.readUnsignedShort();
    }

    public ResolvedMethodHandleConstant resolveMethodHandle(ListMultimap listMultimap) throws ClassFileFormatException {
        MethodHandleRefKind methodHandleRefKind = MethodHandleRefKind.fromKindValue(this.referenceKind);
        if (methodHandleRefKind == null) {
            throw new ClassFileFormatException(
                    this.constantPool.getClassLocationDescription() + " : " + "Invalid reference kind" + " : " + this.referenceKind + " (1)"
            );
        }

        ConstantPoolEntry constantPoolEntry = this.constantPool.getConstantPoolEntry(this.referenceIndex);
        if (constantPoolEntry.isUnresolved()) {
            return null;
        }

        try {
            switch (MethodHandleRefKindSwitchMap.REF_KIND_SWITCH_TABLE[methodHandleRefKind.ordinal()]) {
                case 1:
                case 2:
                case 3:
                case 4:
                    if (constantPoolEntry instanceof ResolvedFieldRef) {
                        return new ResolvedMethodHandleConstant(
                                this.getIndex(), this.constantPool, methodHandleRefKind, (ResolvedFieldRef) constantPoolEntry, listMultimap
                        );
                    }

                    throw new ClassFileFormatException(
                            this.constantPool.getClassLocationDescription()
                                    + " : "
                                    + "Invalid Constant Pool field index"
                                    + " : "
                                    + this.referenceIndex
                                    + " : "
                                    + ConstantPoolEntry.getTagName(this.getTag())
                                    + " entry"
                    );
                case 5:
                case 6:
                case 7:
                case 8:
                    if (constantPoolEntry instanceof ResolvedMethodRefConstant) {
                        if (!MethodHandleRefKind.isValidTargetName(methodHandleRefKind, ((ResolvedMethodRefConstant) constantPoolEntry).getMemberName())) {
                            throw new ClassFileFormatException(
                                    this.constantPool.getClassLocationDescription()
                                            + " : "
                                            + "Invalid method name for reference kind "
                                            + methodHandleRefKind
                                            + " : "
                                            + ((ResolvedMethodRefConstant) constantPoolEntry).getMemberName()
                                            + " : "
                                            + ConstantPoolEntry.getTagName(this.getTag())
                                            + " entry"
                            );
                        }

                        return new ResolvedMethodHandleConstant(
                                this.getIndex(), this.constantPool, methodHandleRefKind, (ResolvedMethodRefConstant) constantPoolEntry, listMultimap
                        );
                    } else {
                        if (constantPoolEntry instanceof ResolvedInterfaceMethodRef) {
                            if (!MethodHandleRefKind.isValidTargetName(methodHandleRefKind, ((ResolvedInterfaceMethodRef) constantPoolEntry).getMemberName())) {
                                throw new ClassFileFormatException(
                                        this.constantPool.getClassLocationDescription()
                                                + " : "
                                                + "Invalid method name for reference kind "
                                                + methodHandleRefKind
                                                + " : "
                                                + ((ResolvedInterfaceMethodRef) constantPoolEntry).getMemberName()
                                                + " : "
                                                + ConstantPoolEntry.getTagName(this.getTag())
                                                + " entry"
                                );
                            }

                            return new ResolvedMethodHandleConstant(
                                    this.getIndex(), this.constantPool, methodHandleRefKind, (ResolvedInterfaceMethodRef) constantPoolEntry, listMultimap
                            );
                        }

                        throw new ClassFileFormatException(
                                this.constantPool.getClassLocationDescription()
                                        + " : "
                                        + "Invalid Constant Pool field index"
                                        + " : "
                                        + this.referenceIndex
                                        + " : "
                                        + ConstantPoolEntry.getTagName(this.getTag())
                                        + " entry"
                        );
                    }
                case 9:
                    if (constantPoolEntry instanceof ResolvedInterfaceMethodRef) {
                        if (!MethodHandleRefKind.isValidTargetName(methodHandleRefKind, ((ResolvedInterfaceMethodRef) constantPoolEntry).getMemberName())) {
                            throw new ClassFileFormatException(
                                    this.constantPool.getClassLocationDescription()
                                            + " : "
                                            + "Invalid method name for reference kind "
                                            + methodHandleRefKind
                                            + " : "
                                            + ((ResolvedInterfaceMethodRef) constantPoolEntry).getMemberName()
                                            + " : "
                                            + ConstantPoolEntry.getTagName(this.getTag())
                                            + " entry"
                            );
                        }

                        return new ResolvedMethodHandleConstant(
                                this.getIndex(), this.constantPool, methodHandleRefKind, (ResolvedInterfaceMethodRef) constantPoolEntry, listMultimap
                        );
                    }

                    throw new ClassFileFormatException(
                            this.constantPool.getClassLocationDescription()
                                    + " : "
                                    + "Invalid Constant Pool field index"
                                    + " : "
                                    + this.referenceIndex
                                    + " : "
                                    + ConstantPoolEntry.getTagName(this.getTag())
                                    + " entry"
                    );
                default:
                    throw new ClassFileFormatException(
                            this.constantPool.getClassLocationDescription() + " : " + "Invalid reference kind" + " : " + this.referenceKind + " (2)"
                    );
            }
        } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
            String string = this.constantPool.getClassLocationDescription()
                    + " : "
                    + "Constant Pool index out of range"
                    + " : "
                    + arrayIndexOutOfBoundsException.getMessage()
                    + " : "
                    + ConstantPoolEntry.getTagName(this.getTag())
                    + " entry";
            throw new ClassFileFormatException(string);
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
