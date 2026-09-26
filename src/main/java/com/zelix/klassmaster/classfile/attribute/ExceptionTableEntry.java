package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.constpool.ClassConstantReplaceable;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ResolvedClassConstant;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.classfile.insn.InstructionUsageClearFlag;
import com.zelix.klassmaster.classfile.insn.InstructionUsageFlag;
import com.zelix.klassmaster.classfile.insn.LabelInstruction;
import com.zelix.klassmaster.classfile.insn.LabelTargetHolder;
import com.zelix.klassmaster.exceptions.ClassFileFormatException;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.SetMultiMap;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ExceptionTableEntry extends ClassFileComponent implements LabelTargetHolder, ClassConstantReplaceable {
    public static volatile Comparator comparator;
    private Object[] rangeLabels = new Object[3];
    private ResolvedClassConstant catchType;
    private boolean newlyCreated;

    public static boolean isContiguousIndexRange(int ba, int bb, int[] bc) {
        int bd = Arrays.binarySearch(bc, ba);

        for (int i = ba + 1; i < bb; i++) {
            bd++;
            if (bd >= bc.length || i != bc[bd]) {
                return false;
            }
        }

        return true;
    }

    public void printEntry(PrintWriter printWriter, StringBuffer stringBuffer) {
        printWriter.println("");
        String string = "catch ";
        if (this.catchType != null) {
            string = string + this.catchType.getClassName();
        } else {
            string = string + "ANY (finally)";
        }

        printWriter.println(stringBuffer + string);
        printWriter.println(stringBuffer + "\tStart offset   : " + this.getLabelName(0));
        printWriter.println(stringBuffer + "\tEnd offset     : " + this.getLabelName(1));
        printWriter.println(stringBuffer + "\tHandler offset : " + this.getLabelName(2));
    }

    @Override
    public String getHolderTypeName(Object object, Object object1, Object object2) {
        return "ExceptionEntry";
    }

    public String getLabelName(int ba) {
        return ((LabelInstruction) this.rangeLabels[ba]).getLabelName();
    }

    public boolean isOutsideRange(int ba, int bb) {
        int bc = this.getOffset(0);
        int bd = this.getOffset(1);
        return bc >= bb || bd <= ba;
    }

    public int getEndIndex() {
        return ((LabelInstruction) this.rangeLabels[1]).getInstructionIndex();
    }

    public void writeRemapped(DataOutputStream dataOutputStream, Map map1) throws IOException {
        dataOutputStream.writeShort(this.getOffset(0));
        dataOutputStream.writeShort(this.getOffset(1));
        dataOutputStream.writeShort(this.getOffset(2));
        if (this.catchType != null) {
            ConstantPoolEntry constantPoolEntry = (ConstantPoolEntry) map1.get(this.catchType);
            if (constantPoolEntry != null) {
                dataOutputStream.writeShort(constantPoolEntry.getIndex());
            } else {
                dataOutputStream.writeShort(this.catchType.getIndex());
            }
        } else {
            dataOutputStream.writeShort(0);
        }
    }

    @Override
    public void replaceClassConstant(ResolvedClassConstant resolvedClassConstant, ResolvedClassConstant resolvedClassConstant1) {
        if (this.catchType != null && this.catchType == resolvedClassConstant) {
            this.catchType = resolvedClassConstant1;
        }
    }

    @Override
    public InstructionUsageFlag getUsageFlag() {
        return InstructionUsageFlag.SET_USAGE_EXCEPTION;
    }

    public ExceptionTableEntry(
            CodeAttributeBody codeAttributeBody,
            ResolvedClassConstant resolvedClassConstant,
            LabelInstruction labelInstruction,
            LabelInstruction labelInstruction1,
            LabelInstruction labelInstruction2
    ) {
        super(codeAttributeBody);
        this.catchType = resolvedClassConstant;
        labelInstruction.addUsageFlag(InstructionUsageFlag.SET_USAGE_EXCEPTION_TRY_START);
        labelInstruction1.addUsageFlag(InstructionUsageFlag.SET_USAGE_EXCEPTION_TRY_END);
        labelInstruction2.addUsageFlag(InstructionUsageFlag.SET_USAGE_EXCEPTION_HANDLER);
        Object[] objects = new Object[]{labelInstruction, labelInstruction1, labelInstruction2};
        this.rangeLabels = objects;
        this.newlyCreated = true;
    }

    public int getOffset(int ba) {
        return ((LabelInstruction) this.rangeLabels[ba]).getOffset();
    }

    public boolean overlapsPartially(int ba, int bb) {
        int bc = this.getOffset(0);
        int bd = this.getOffset(1);
        return bc > ba && bc < bb && bd > bb || bc < ba && bd > ba && bd < bb;
    }

    public int getStartIndex() {
        return ((LabelInstruction) this.rangeLabels[0]).getInstructionIndex();
    }

    public static Comparator getComparator() {
        Comparator comparator1;
        if (comparator == null) {
            comparator = new ExceptionRangeComparator();
            comparator1 = comparator;
        } else {
            comparator1 = comparator;
        }

        return comparator1;
    }

    public ResolvedClassConstant getCatchType() {
        return this.catchType;
    }

    public boolean isCatchAny() {
        return this.catchType == null;
    }

    public void resolveCatchType(int ba, ListMultimap listMultimap) throws ClassFileFormatException {
        if (ba != 0) {
            ConstantPoolEntry constantPoolEntry = this.getConstantPoolEntry(ba);
            if (!(constantPoolEntry instanceof ResolvedClassConstant)) {
                throw new ClassFileFormatException(this.getLocationName() + " : " + "Invalid Class index in Exception Entry" + " : " + ba);
            }

            this.catchType = (ResolvedClassConstant) constantPoolEntry;
            listMultimap.addValue(this.catchType, this);
        }
    }

    public LabelInstruction getLabel(ExceptionRangeField exceptionRangeField) {
        return (LabelInstruction) this.rangeLabels[exceptionRangeField.ordinal()];
    }

    @Override
    public void registerLabelTargets(SetMultiMap setMultiMap) {
        for (int i = 0; i < this.rangeLabels.length; i++) {
            setMultiMap.addValue((LabelInstruction) this.rangeLabels[i], this);
        }
    }

    public boolean isWithinRange(int ba, int bb) {
        int bc = this.getOffset(0);
        int bd = this.getOffset(1);
        return bc >= ba && bd <= bb;
    }

    public String getCatchTypeName() {
        return this.catchType != null ? this.catchType.getClassName() : "java/lang/Throwable";
    }

    public static InstructionUsageClearFlag getClearUsageFlag(ExceptionRangeField exceptionRangeField) {
        switch (ExceptionTablePcSwitchMap.RANGE_FIELD_SWITCH[exceptionRangeField.ordinal()]) {
            case 1:
                return InstructionUsageClearFlag.CLEAR_USAGE_EXCEPTION_TRY_START;
            case 2:
                return InstructionUsageClearFlag.CLEAR_USAGE_EXCEPTION_TRY_END;
            case 3:
                return InstructionUsageClearFlag.CLEAR_USAGE_EXCEPTION_HANDLER;
            default:
                throw new IllegalArgumentException();
        }
    }

    public boolean isNewlyCreated() {
        return this.newlyCreated;
    }

    public boolean clearUnsharedLabelUsage(LabelInstruction labelInstruction, SetMultiMap setMultiMap, ExceptionRangeField exceptionRangeField) {
        boolean bl = false;
        boolean bl1 = false;
        Set set1 = setMultiMap.getValues(labelInstruction);
        if (set1 != null) {
            Iterator iterator = set1.iterator();

            while (iterator.hasNext()) {
                LabelTargetHolder labelTargetHolder = (LabelTargetHolder) iterator.next();
                if (labelTargetHolder instanceof ExceptionTableEntry) {
                    bl = true;
                    if (((ExceptionTableEntry) labelTargetHolder).getLabel(exceptionRangeField) == labelInstruction) {
                        bl1 = true;
                        break;
                    }
                }
            }
        }

        if (!bl) {
            labelInstruction.clearUsageFlag(InstructionUsageClearFlag.CLEAR_USAGE_EXCEPTION);
            labelInstruction.clearUsageFlag(getClearUsageFlag(exceptionRangeField));
        } else if (!bl1) {
            labelInstruction.clearUsageFlag(getClearUsageFlag(exceptionRangeField));
        }

        return !bl1;
    }

    public ExceptionTableEntry(
            ClassFileComponent classFileComponent, ClassFileInputStream classFileInputStream, ListMultimap listMultimap, ListMultimap listMultimap1
    ) throws ClassFileFormatException, IOException {
        super(classFileComponent);
        int ba = classFileInputStream.readUnsignedShort();
        int bb = classFileInputStream.readUnsignedShort();
        int bc = classFileInputStream.readUnsignedShort();
        Integer integer = integerCache.valueOf(ba);
        listMultimap.addValue(integer, this);
        this.rangeLabels[ExceptionRangeField.START_PC.ordinal()] = integer;
        integer = integerCache.valueOf(bb);
        listMultimap.addValue(integer, this);
        this.rangeLabels[ExceptionRangeField.END_PC.ordinal()] = integer;
        integer = integerCache.valueOf(bc);
        listMultimap.addValue(integer, this);
        this.rangeLabels[ExceptionRangeField.HANDLER_PC.ordinal()] = integer;
        int bd = classFileInputStream.readUnsignedShort();
        this.resolveCatchType(bd, listMultimap1);
    }

    @Override
    public void bindLabel(Integer integer, LabelInstruction labelInstruction) {
        int ba = this.rangeLabels.length;
        labelInstruction.setVisible();

        for (int i = 0; i < ba; i++) {
            Object object = this.rangeLabels[i];
            if (object instanceof Integer && ((Integer) object).equals(integer)) {
                this.rangeLabels[i] = labelInstruction;
                switch (i) {
                    case 0:
                        labelInstruction.addUsageFlag(InstructionUsageFlag.SET_USAGE_EXCEPTION_TRY_START);
                        break;
                    case 1:
                        labelInstruction.addUsageFlag(InstructionUsageFlag.SET_USAGE_EXCEPTION_TRY_END);
                        break;
                    case 2:
                        labelInstruction.addUsageFlag(InstructionUsageFlag.SET_USAGE_EXCEPTION_HANDLER);
                }
            }
        }
    }

    @Override
    public void collectUsedConstants(char ba, int bb, UsedConstantsCollector usedConstantsCollector, char bc) {
        if (this.catchType != null) {
            this.catchType.registerUsage(usedConstantsCollector, this, this);
        }
    }

    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeShort(this.getOffset(0));
        dataOutputStream.writeShort(this.getOffset(1));
        dataOutputStream.writeShort(this.getOffset(2));
        dataOutputStream.writeShort(this.catchType == null ? 0 : this.catchType.getIndex());
    }

    public boolean unlinkIfRangeRemoved(HashSet hashSet, int[] ba, SetMultiMap setMultiMap) {
        LabelInstruction labelInstruction = (LabelInstruction) this.rangeLabels[0];
        LabelInstruction labelInstruction1 = (LabelInstruction) this.rangeLabels[1];
        LabelInstruction labelInstruction2 = (LabelInstruction) this.rangeLabels[2];
        if (hashSet.contains(labelInstruction2)
                || hashSet.contains(labelInstruction) && isContiguousIndexRange(labelInstruction.getInstructionIndex(), labelInstruction1.getInstructionIndex(), ba)) {
            setMultiMap.removeValue(labelInstruction, this);
            setMultiMap.removeValue(labelInstruction1, this);
            setMultiMap.removeValue(labelInstruction2, this);
            if (hashSet.contains(labelInstruction)) {
                this.clearUnsharedLabelUsage(labelInstruction, setMultiMap, ExceptionRangeField.START_PC);
            }

            if (hashSet.contains(labelInstruction1)) {
                this.clearUnsharedLabelUsage(labelInstruction1, setMultiMap, ExceptionRangeField.END_PC);
            }

            this.clearUnsharedLabelUsage(labelInstruction2, setMultiMap, ExceptionRangeField.HANDLER_PC);
            return true;
        } else {
            return false;
        }
    }
}
