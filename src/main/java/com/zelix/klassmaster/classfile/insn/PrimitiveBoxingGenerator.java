package com.zelix.klassmaster.classfile.insn;

import com.zelix.klassmaster.classfile.constpool.ConstantPool;
import com.zelix.klassmaster.classfile.constpool.ResolvedClassConstant;
import com.zelix.klassmaster.classfile.constpool.ResolvedMethodRefConstant;
import com.zelix.klassmaster.classfile.hierarchy.ClassMemberLookup;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;
import java.util.List;

public class PrimitiveBoxingGenerator {
    public static int boxFloatOnStack(
            List list1, boolean bl, List list2, ConstantPool constantPool1, ClassMemberLookup classMemberLookup1, ClassResolver classResolver1
    ) throws ZkmException, IOException {
        byte ba;
        if (bl) {
            ba = 1;
            ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                    "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", list2, classMemberLookup1, classResolver1
            );
            list1.add(new ConstantRefInstruction(184, resolvedMethodRefConstant));
        } else {
            ba = 4;
            ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/lang/Float", list2);
            list1.add(new TypeInstruction(resolvedClassConstant));
            list1.add(SimpleInstruction.forOpcode(90));
            list1.add(SimpleInstruction.forOpcode(95));
            ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                    "java/lang/Float", "<init>", "(F)V", list2, classMemberLookup1, classResolver1
            );
            list1.add(new ConstantRefInstruction(183, resolvedMethodRefConstant1));
        }

        return ba;
    }

    public static int boxByteLocal(
            int ba,
            List list1,
            boolean bl,
            LocalVariableList localVariableList1,
            List list2,
            ConstantPool constantPool1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        byte bb;
        if (bl) {
            list1.add(Instruction.createIntLoad(ba, localVariableList1, 4));
            ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                    "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", list2, classMemberLookup1, classResolver1
            );
            list1.add(new ConstantRefInstruction(184, resolvedMethodRefConstant));
            bb = 2;
        } else {
            ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/lang/Byte", list2);
            list1.add(new TypeInstruction(resolvedClassConstant));
            list1.add(SimpleInstruction.forOpcode(89));
            list1.add(Instruction.createIntLoad(ba, localVariableList1, 4));
            ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                    "java/lang/Byte", "<init>", "(B)V", list2, classMemberLookup1, classResolver1
            );
            list1.add(new ConstantRefInstruction(183, resolvedMethodRefConstant1));
            bb = 4;
        }

        return bb;
    }

    public static int boxBooleanOnStack(
            List list1, boolean bl, List list2, ConstantPool constantPool1, ClassMemberLookup classMemberLookup1, ClassResolver classResolver1
    ) throws ZkmException, IOException {
        byte ba;
        if (bl) {
            ba = 1;
            ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                    "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", list2, classMemberLookup1, classResolver1
            );
            list1.add(new ConstantRefInstruction(184, resolvedMethodRefConstant));
        } else {
            ba = 4;
            ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/lang/Boolean", list2);
            list1.add(new TypeInstruction(resolvedClassConstant));
            list1.add(SimpleInstruction.forOpcode(90));
            list1.add(SimpleInstruction.forOpcode(95));
            ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                    "java/lang/Boolean", "<init>", "(Z)V", list2, classMemberLookup1, classResolver1
            );
            list1.add(new ConstantRefInstruction(183, resolvedMethodRefConstant1));
        }

        return ba;
    }

    public static int boxLongOnStack(
            List list1, boolean bl, List list2, ConstantPool constantPool1, ClassMemberLookup classMemberLookup1, ClassResolver classResolver1
    ) throws ZkmException, IOException {
        byte ba;
        if (bl) {
            ba = 1;
            ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                    "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", list2, classMemberLookup1, classResolver1
            );
            list1.add(new ConstantRefInstruction(184, resolvedMethodRefConstant));
        } else {
            ba = 4;
            ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/lang/Long", list2);
            list1.add(new TypeInstruction(resolvedClassConstant));
            list1.add(SimpleInstruction.forOpcode(91));
            list1.add(SimpleInstruction.forOpcode(91));
            list1.add(SimpleInstruction.forOpcode(87));
            ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                    "java/lang/Long", "<init>", "(J)V", list2, classMemberLookup1, classResolver1
            );
            list1.add(new ConstantRefInstruction(183, resolvedMethodRefConstant1));
        }

        return ba;
    }

    public static int boxDoubleLocal(
            int ba,
            List list1,
            boolean bl,
            LocalVariableList localVariableList1,
            List list2,
            ConstantPool constantPool1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1,
            int bb
    ) throws ZkmException, IOException {
        byte bc;
        if (bl) {
            bc = 2;
            list1.add(Instruction.createDoubleLoad(ba, localVariableList1, bb));
            ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                    "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", list2, classMemberLookup1, classResolver1
            );
            list1.add(new ConstantRefInstruction(184, resolvedMethodRefConstant));
        } else {
            ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/lang/Double", list2);
            list1.add(new TypeInstruction(resolvedClassConstant));
            list1.add(SimpleInstruction.forOpcode(89));
            list1.add(Instruction.createDoubleLoad(ba, localVariableList1, bb));
            ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                    "java/lang/Double", "<init>", "(D)V", list2, classMemberLookup1, classResolver1
            );
            list1.add(new ConstantRefInstruction(183, resolvedMethodRefConstant1));
            bc = 4;
        }

        return bc;
    }

    public static int boxCharLocal(
            int ba,
            List list1,
            boolean bl,
            LocalVariableList localVariableList1,
            List list2,
            ConstantPool constantPool1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        byte bb;
        if (bl) {
            list1.add(Instruction.createIntLoad(ba, localVariableList1, 4));
            ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                    "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", list2, classMemberLookup1, classResolver1
            );
            list1.add(new ConstantRefInstruction(184, resolvedMethodRefConstant));
            bb = 2;
        } else {
            ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/lang/Character", list2);
            list1.add(new TypeInstruction(resolvedClassConstant));
            list1.add(SimpleInstruction.forOpcode(89));
            list1.add(Instruction.createIntLoad(ba, localVariableList1, 4));
            ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                    "java/lang/Character", "<init>", "(C)V", list2, classMemberLookup1, classResolver1
            );
            list1.add(new ConstantRefInstruction(183, resolvedMethodRefConstant1));
            bb = 4;
        }

        return bb;
    }

    public static int boxIntOnStack(
            List list1, boolean bl, List list2, ConstantPool constantPool1, ClassMemberLookup classMemberLookup1, ClassResolver classResolver1
    ) throws ZkmException, IOException {
        byte ba;
        if (bl) {
            ba = 1;
            ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                    "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", list2, classMemberLookup1, classResolver1
            );
            list1.add(new ConstantRefInstruction(184, resolvedMethodRefConstant));
        } else {
            ba = 4;
            ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/lang/Integer", list2);
            list1.add(new TypeInstruction(resolvedClassConstant));
            list1.add(SimpleInstruction.forOpcode(90));
            list1.add(SimpleInstruction.forOpcode(95));
            ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                    "java/lang/Integer", "<init>", "(I)V", list2, classMemberLookup1, classResolver1
            );
            list1.add(new ConstantRefInstruction(183, resolvedMethodRefConstant1));
        }

        return ba;
    }

    public static int boxBooleanLocal(
            int ba,
            List list1,
            boolean bl,
            LocalVariableList localVariableList1,
            List list2,
            ConstantPool constantPool1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1,
            int bb
    ) throws ZkmException, IOException {
        byte bc;
        if (bl) {
            list1.add(Instruction.createIntLoad(ba, localVariableList1, bb));
            ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                    "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", list2, classMemberLookup1, classResolver1
            );
            list1.add(new ConstantRefInstruction(184, resolvedMethodRefConstant));
            bc = 2;
        } else {
            ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/lang/Boolean", list2);
            list1.add(new TypeInstruction(resolvedClassConstant));
            list1.add(SimpleInstruction.forOpcode(89));
            list1.add(Instruction.createIntLoad(ba, localVariableList1, bb));
            ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                    "java/lang/Boolean", "<init>", "(Z)V", list2, classMemberLookup1, classResolver1
            );
            list1.add(new ConstantRefInstruction(183, resolvedMethodRefConstant1));
            bc = 4;
        }

        return bc;
    }

    public static int boxIntLocal(
            int ba,
            List list1,
            boolean bl,
            LocalVariableList localVariableList1,
            List list2,
            ConstantPool constantPool1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1,
            int bb
    ) throws ZkmException, IOException {
        byte bc;
        if (bl) {
            bc = 2;
            list1.add(Instruction.createIntLoad(ba, localVariableList1, bb));
            ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                    "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", list2, classMemberLookup1, classResolver1
            );
            list1.add(new ConstantRefInstruction(184, resolvedMethodRefConstant));
        } else {
            bc = 4;
            ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/lang/Integer", list2);
            list1.add(new TypeInstruction(resolvedClassConstant));
            list1.add(SimpleInstruction.forOpcode(89));
            list1.add(Instruction.createIntLoad(ba, localVariableList1, bb));
            ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                    "java/lang/Integer", "<init>", "(I)V", list2, classMemberLookup1, classResolver1
            );
            list1.add(new ConstantRefInstruction(183, resolvedMethodRefConstant1));
        }

        return bc;
    }

    public static int boxFloatLocal(
            int ba,
            List list1,
            boolean bl,
            LocalVariableList localVariableList1,
            List list2,
            ConstantPool constantPool1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1,
            int bb
    ) throws ZkmException, IOException {
        byte bc;
        if (bl) {
            bc = 2;
            list1.add(Instruction.createFloatLoad(ba, localVariableList1, bb));
            ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                    "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", list2, classMemberLookup1, classResolver1
            );
            list1.add(new ConstantRefInstruction(184, resolvedMethodRefConstant));
        } else {
            bc = 4;
            ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/lang/Float", list2);
            list1.add(new TypeInstruction(resolvedClassConstant));
            list1.add(SimpleInstruction.forOpcode(89));
            list1.add(Instruction.createFloatLoad(ba, localVariableList1, bb));
            ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                    "java/lang/Float", "<init>", "(F)V", list2, classMemberLookup1, classResolver1
            );
            list1.add(new ConstantRefInstruction(183, resolvedMethodRefConstant1));
        }

        return bc;
    }

    public static int boxShortLocal(
            int ba,
            List list1,
            boolean bl,
            LocalVariableList localVariableList1,
            List list2,
            ConstantPool constantPool1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        byte bb;
        if (bl) {
            list1.add(Instruction.createIntLoad(ba, localVariableList1, 4));
            ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                    "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", list2, classMemberLookup1, classResolver1
            );
            list1.add(new ConstantRefInstruction(184, resolvedMethodRefConstant));
            bb = 2;
        } else {
            ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/lang/Short", list2);
            list1.add(new TypeInstruction(resolvedClassConstant));
            list1.add(SimpleInstruction.forOpcode(89));
            list1.add(Instruction.createIntLoad(ba, localVariableList1, 4));
            ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                    "java/lang/Short", "<init>", "(S)V", list2, classMemberLookup1, classResolver1
            );
            list1.add(new ConstantRefInstruction(183, resolvedMethodRefConstant1));
            bb = 4;
        }

        return bb;
    }

    public static int boxLongLocal(
            int ba,
            List list1,
            boolean bl,
            LocalVariableList localVariableList1,
            List list2,
            ConstantPool constantPool1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1,
            int bb
    ) throws ZkmException, IOException {
        byte bc;
        if (bl) {
            bc = 2;
            list1.add(Instruction.createLongLoad(ba, localVariableList1, bb));
            ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                    "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", list2, classMemberLookup1, classResolver1
            );
            list1.add(new ConstantRefInstruction(184, resolvedMethodRefConstant));
        } else {
            ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/lang/Long", list2);
            list1.add(new TypeInstruction(resolvedClassConstant));
            list1.add(SimpleInstruction.forOpcode(89));
            list1.add(Instruction.createLongLoad(ba, localVariableList1, bb));
            ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                    "java/lang/Long", "<init>", "(J)V", list2, classMemberLookup1, classResolver1
            );
            list1.add(new ConstantRefInstruction(183, resolvedMethodRefConstant1));
            bc = 4;
        }

        return bc;
    }

    public static int boxDoubleOnStack(
            List list1, boolean bl, List list2, ConstantPool constantPool1, ClassMemberLookup classMemberLookup1, ClassResolver classResolver1
    ) throws ZkmException, IOException {
        byte ba;
        if (bl) {
            ba = 1;
            ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                    "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", list2, classMemberLookup1, classResolver1
            );
            list1.add(new ConstantRefInstruction(184, resolvedMethodRefConstant));
        } else {
            ba = 4;
            ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/lang/Double", list2);
            list1.add(new TypeInstruction(resolvedClassConstant));
            list1.add(SimpleInstruction.forOpcode(91));
            list1.add(SimpleInstruction.forOpcode(91));
            list1.add(SimpleInstruction.forOpcode(87));
            ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                    "java/lang/Double", "<init>", "(D)V", list2, classMemberLookup1, classResolver1
            );
            list1.add(new ConstantRefInstruction(183, resolvedMethodRefConstant1));
        }

        return ba;
    }

    private PrimitiveBoxingGenerator() {
    }
}
