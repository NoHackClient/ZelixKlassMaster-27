package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.constpool.ConstantPool;
import com.zelix.klassmaster.classfile.insn.LabelInstruction;
import com.zelix.klassmaster.classfile.insn.VerifierType;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ZkmAssert;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class StackMapTableFrame extends StackMapFrame {
    public StackMapTableFrame(ClassFileComponent classFileComponent) {
        super(classFileComponent);
    }

    public final StackMapFrameKind getFrameKind() {
        int frameType = this.getFrameType();
        if (frameType >= 0 && frameType <= 63) {
            return StackMapFrameKind.SAME;
        } else if (frameType >= 64 && frameType <= 127) {
            return StackMapFrameKind.SAME_LOCALS_1_STACK_ITEM;
        } else if (frameType == 247) {
            return StackMapFrameKind.SAME_LOCALS_1_STACK_ITEM_EXTENDED;
        } else if (frameType >= 248 && frameType <= 250) {
            return StackMapFrameKind.CHOP;
        } else if (frameType == 251) {
            return StackMapFrameKind.SAME_EXTENDED;
        } else if (frameType >= 252 && frameType <= 254) {
            return StackMapFrameKind.APPEND;
        } else {
            return frameType == 255 ? StackMapFrameKind.FULL : null;
        }
    }

    public static StackMapTableFrame readFrame(
            StackMapTableAttribute stackMapTableAttribute,
            ClassFileInputStream classFileInputStream,
            ListMultimap listMultimap,
            ListMultimap listMultimap1,
            PrintWriter printWriter,
            MutableInt mutableInt,
            Map map1,
            Map map2
    ) throws IOException {
        StackMapTableFrame stackMapTableFrame = null;
        int ba = classFileInputStream.readUnsignedByte();
        if (ba == 255) {
            stackMapTableFrame = new FullStackMapFrame(
                    stackMapTableAttribute, classFileInputStream, listMultimap, listMultimap1, printWriter, mutableInt, map1, map2
            );
        } else if (ba == 251) {
            stackMapTableFrame = new SameFrameExtended(stackMapTableAttribute, classFileInputStream, listMultimap, mutableInt);
        } else if (ba >= 0 && ba <= 63) {
            stackMapTableFrame = new SameFrame(ba, stackMapTableAttribute, listMultimap, mutableInt);
        } else if (ba >= 64 && ba <= 127) {
            stackMapTableFrame = new SameLocalsOneStackItemFrame(
                    ba, stackMapTableAttribute, classFileInputStream, listMultimap, listMultimap1, printWriter, mutableInt, map1, map2
            );
        } else if (ba == 247) {
            stackMapTableFrame = new SameLocals1StackItemExtendedFrame(
                    stackMapTableAttribute, classFileInputStream, listMultimap, listMultimap1, printWriter, mutableInt, map1, map2
            );
        } else if (ba >= 248 && ba <= 250) {
            stackMapTableFrame = new ChopFrame(ba, stackMapTableAttribute, classFileInputStream, listMultimap, mutableInt);
        } else if (ba >= 252 && ba <= 254) {
            stackMapTableFrame = new AppendFrame(
                    ba, stackMapTableAttribute, classFileInputStream, listMultimap, listMultimap1, printWriter, mutableInt, map1, map2
            );
        } else {
            ZkmAssert.assertTrue(false, new String[]{"Invalid StackMapTable frame type '" + ba + "' in '" + stackMapTableAttribute.getMethodDescription() + "'"});
        }

        return stackMapTableFrame;
    }

    public final void writeFrame(DataOutputStream dataOutputStream) throws IOException {
        this.writeRemapped(dataOutputStream, null);
    }

    public abstract int getFrameType();

    public static boolean areTypesEqual(VerificationTypeInfo[] verificationTypeInfos, VerificationTypeInfo[] verificationTypeInfos1) {
        if (verificationTypeInfos.length == verificationTypeInfos1.length) {
            for (int i = 0; i < verificationTypeInfos.length; i++) {
                if (!verificationTypeInfos[i].isSameType(verificationTypeInfos1[i])) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public abstract void writeRemapped(DataOutputStream dataOutputStream, Map map1) throws IOException;

    public static StackMapTableFrame createFrame(
            StackMapTableAttribute stackMapTableAttribute,
            LabelInstruction labelInstruction,
            VerifierType[] verifierTypes,
            VerifierType[] verifierTypes1,
            ConstantPool constantPool1,
            Set set1,
            List list1,
            MutableInt mutableInt,
            ObservableHolder observableHolder,
            ObservableHolder observableHolder1,
            Map map1,
            Map map2
    ) throws ZkmException, IOException {
        int offset = labelInstruction.getOffset();
        int value = mutableInt.getValue();
        int bb;
        if (value == -1) {
            bb = offset;
        } else {
            bb = offset - value - 1;
        }

        mutableInt.setValue(offset);
        VerificationTypeInfo[] verificationTypeInfos = (VerificationTypeInfo[]) observableHolder1.getValue();
        VerificationTypeInfo[] verificationTypeInfos1 = VerificationTypeInfo.convertVerifierTypes(
                stackMapTableAttribute, verifierTypes1, constantPool1, set1, list1, true, map1, map2
        );
        VerificationTypeInfo[] verificationTypeInfos2 = VerificationTypeInfo.convertVerifierTypes(
                stackMapTableAttribute, verifierTypes, constantPool1, set1, list1, false, map1, map2
        );
        boolean bl = areTypesEqual(verificationTypeInfos, verificationTypeInfos1);
        StackMapTableFrame stackMapTableFrame = null;
        int bd = -1;
        if (HiddenOptionFlags.FORCE_FULL_STACK_MAP_FRAMES) {
            bd = 255;
            stackMapTableFrame = new FullStackMapFrame(stackMapTableAttribute, bb, labelInstruction, verificationTypeInfos2, verificationTypeInfos1);
        } else {
            label95:
            if (bl && verificationTypeInfos2.length == 0) {
                short bj;
                if (bb >= 0) {
                    if (bb <= 63) {
                        bd = bb;
                        stackMapTableFrame = new SameFrame(stackMapTableAttribute, bd, labelInstruction);
                        break label95;
                    }

                    bj = 251;
                } else {
                    bj = 251;
                }

                bd = bj;
                stackMapTableFrame = new SameFrameExtended(stackMapTableAttribute, labelInstruction, bb);
            } else {
                label86:
                if (bl && verificationTypeInfos2.length == 1) {
                    short bk;
                    if (bb >= 0) {
                        if (bb <= 63) {
                            bd = bb + 64;
                            stackMapTableFrame = new SameLocalsOneStackItemFrame(stackMapTableAttribute, bd, labelInstruction, verificationTypeInfos2[0]);
                            break label86;
                        }

                        bk = 247;
                    } else {
                        bk = 247;
                    }

                    bd = bk;
                    stackMapTableFrame = new SameLocals1StackItemExtendedFrame(stackMapTableAttribute, bb, labelInstruction, verificationTypeInfos2[0]);
                } else if (verificationTypeInfos2.length == 0) {
                    int be = verificationTypeInfos.length - verificationTypeInfos1.length;
                    if (be >= 1 && be <= 3) {
                        boolean bl1 = true;

                        for (int i = 0; i < verificationTypeInfos1.length; i++) {
                            if (!verificationTypeInfos[i].isSameType(verificationTypeInfos1[i])) {
                                bl1 = false;
                                break;
                            }
                        }

                        if (bl1) {
                            bd = 251 - be;
                            stackMapTableFrame = new ChopFrame(stackMapTableAttribute, bd, bb, labelInstruction);
                        }
                    }

                    if (bd == -1) {
                        int bh = verificationTypeInfos1.length - verificationTypeInfos.length;
                        if (bh >= 1 && bh <= 3) {
                            boolean bl2 = true;

                            for (int i = 0; i < verificationTypeInfos.length; i++) {
                                if (!verificationTypeInfos[i].isSameType(verificationTypeInfos1[i])) {
                                    bl2 = false;
                                    break;
                                }
                            }

                            if (bl2) {
                                bd = 251 + bh;
                                int bi = bh;
                                VerificationTypeInfo[] verificationTypeInfos3 = new VerificationTypeInfo[bi];
                                System.arraycopy(verificationTypeInfos1, verificationTypeInfos1.length - bi, verificationTypeInfos3, 0, bi);
                                stackMapTableFrame = new AppendFrame(stackMapTableAttribute, bd, bb, labelInstruction, verificationTypeInfos3);
                            }
                        }
                    }
                }
            }
        }

        if (bd == -1) {
            stackMapTableFrame = new FullStackMapFrame(stackMapTableAttribute, bb, labelInstruction, verificationTypeInfos2, verificationTypeInfos1);
        }

        observableHolder1.setValue(verificationTypeInfos1);
        observableHolder.setValue(verificationTypeInfos2);
        return stackMapTableFrame;
    }

    @Override
    public final String getHolderTypeName(Object object, Object object1, Object object2) {
        return "StackMapTable";
    }
}
