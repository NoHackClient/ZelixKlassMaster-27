package com.zelix.klassmaster.classfile.insn;

import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmRuntimeException;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.SetMultiMap;
import com.zelix.klassmaster.util.TruncatedStringDisplay;
import com.zelix.klassmaster.util.ZkmAssert;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BranchInstruction extends Instruction implements JumpingInstruction {
    public int bytecodeOffset;
    public LabelInstruction targetLabel;

    @Override
    public InstructionUsageFlag getUsageFlag() {
        return InstructionUsageFlag.SET_USAGE_JUMP_DESTINATION;
    }

    @Override
    public boolean isJump() {
        return true;
    }

    public void setTargetLabel(LabelInstruction labelInstruction) {
        this.targetLabel = labelInstruction;
    }

    @Override
    public final boolean isExit() {
        return false;
    }

    @Override
    public List expandWideJump() {
        int relativeOffset = this.getRelativeOffset();
        if (relativeOffset >= -32768 && relativeOffset <= 32767) {
            return null;
        }

        ArrayList arrayList = new ArrayList(5);
        LabelInstruction labelInstruction = this.targetLabel;
        LabelInstruction labelInstruction1 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction2 = new LabelInstruction(true, 1);
        this.setTargetLabel(labelInstruction1);
        arrayList.add(this);
        arrayList.add(new GotoInstruction(labelInstruction2));
        arrayList.add(labelInstruction1);
        arrayList.add(new WideJumpInstruction(200, labelInstruction));
        arrayList.add(labelInstruction2);
        return arrayList;
    }

    @Override
    public String toAssembly() {
        return this.getMnemonic();
    }

    @Override
    public void printDisassembly(PrintWriter printWriter, StringBuilder stringBuilder) throws UnknownOpcodeException {
        StringBuilder stringBuilder1 = new StringBuilder(100);
        String string = this.getMnemonic();
        stringBuilder1.append(string + " " + this.targetLabel.getLabelName());
        String string1 = Instruction.getDescriptionComment(this.opcode);
        string1 = Instruction.formatDescription(string1, this.targetLabel.getLabelName());
        if (string1.length() > 0) {
            stringBuilder1.append("\t" + string1);
        }

        printWriter.println(stringBuilder.toString() + stringBuilder.toString() + stringBuilder1);
    }

    @Override
    public int getStackDelta() {
        switch (this.opcode) {
            case 153:
            case 154:
            case 155:
            case 156:
            case 157:
            case 158:
            case 198:
            case 199:
                return -1;
            case 159:
            case 160:
            case 161:
            case 162:
            case 163:
            case 164:
            case 165:
            case 166:
                return -2;
            case 167:
            case 168:
            case 200:
            case 201:
                return 0;
            case 169:
            case 170:
            case 171:
            case 172:
            case 173:
            case 174:
            case 175:
            case 176:
            case 177:
            case 178:
            case 179:
            case 180:
            case 181:
            case 182:
            case 183:
            case 184:
            case 185:
            case 186:
            case 187:
            case 188:
            case 189:
            case 190:
            case 191:
            case 192:
            case 193:
            case 194:
            case 195:
            case 196:
            case 197:
            default:
                ZkmAssert.assertTrue(false, new String[]{"invalid opcode " + this.opcode});
                return 0;
        }
    }

    @Override
    public final boolean consumesStackSlot(int ba, int bb) {
        switch (this.opcode) {
            case 153:
            case 154:
            case 155:
            case 156:
            case 157:
            case 158:
            case 159:
            case 160:
            case 161:
            case 162:
            case 163:
            case 164:
            case 165:
            case 166:
            case 198:
            case 199:
                return ba >= bb;
            case 167:
            case 168:
            case 200:
            case 201:
                return false;
            case 169:
            case 170:
            case 171:
            case 172:
            case 173:
            case 174:
            case 175:
            case 176:
            case 177:
            case 178:
            case 179:
            case 180:
            case 181:
            case 182:
            case 183:
            case 184:
            case 185:
            case 186:
            case 187:
            case 188:
            case 189:
            case 190:
            case 191:
            case 192:
            case 193:
            case 194:
            case 195:
            case 196:
            case 197:
            default:
                ZkmAssert.assertTrue(false, new String[]{"invalid opcode " + this.opcode});
                return false;
        }
    }

    public BranchInstruction(int ba, LabelInstruction labelInstruction) {
        super(ba);
        this.bytecodeOffset = -1;
        this.targetLabel = labelInstruction;
    }

    @Override
    public StackFrameState computeFrameAfter(StackFrameState stackFrameState, Object object1, Object object2, Object object) throws ZkmException, IOException {
        TruncatedStringDisplay truncatedStringDisplay = new TruncatedStringDisplay((String) object);
        VerifierType[] verifierTypes = stackFrameState.getStack();
        VerifierType[] verifierTypes1 = stackFrameState.getLocals();
        int ba = verifierTypes.length;
        SubroutineLocalsBitSet subroutineLocalsBitSet = stackFrameState.getSubroutineLocals();
        Set set1 = stackFrameState.getHeldMonitors();
        switch (this.opcode) {
            case 153:
            case 154:
            case 155:
            case 156:
            case 157:
            case 158:
                VerifierType[] verifierTypes5 = VerifierType.createArray(ba - 1);
                System.arraycopy(verifierTypes, 0, verifierTypes5, 0, ba - 1);
                return new StackFrameState(verifierTypes5, verifierTypes1, subroutineLocalsBitSet, set1);
            case 159:
            case 160:
            case 161:
            case 162:
            case 163:
            case 164:
                VerifierType[] verifierTypes4 = VerifierType.createArray(ba - 2);
                System.arraycopy(verifierTypes, 0, verifierTypes4, 0, ba - 2);
                return new StackFrameState(verifierTypes4, verifierTypes1, subroutineLocalsBitSet, set1);
            case 165:
            case 166:
                VerifierType[] verifierTypes3 = VerifierType.createArray(ba - 2);
                System.arraycopy(verifierTypes, 0, verifierTypes3, 0, ba - 2);
                return new StackFrameState(verifierTypes3, verifierTypes1, subroutineLocalsBitSet, set1);
            case 167:
            case 168:
            case 200:
            case 201:
                return null;
            case 169:
            case 170:
            case 171:
            case 172:
            case 173:
            case 174:
            case 175:
            case 176:
            case 177:
            case 178:
            case 179:
            case 180:
            case 181:
            case 182:
            case 183:
            case 184:
            case 185:
            case 186:
            case 187:
            case 188:
            case 189:
            case 190:
            case 191:
            case 192:
            case 193:
            case 194:
            case 195:
            case 196:
            case 197:
            default:
                ZkmAssert.assertTrue(false, new String[]{"invalid opcode " + this.opcode + " " + truncatedStringDisplay});
                return null;
            case 198:
            case 199:
                VerifierType[] verifierTypes2 = VerifierType.createArray(ba - 1);
                System.arraycopy(verifierTypes, 0, verifierTypes2, 0, ba - 1);
                return new StackFrameState(verifierTypes2, verifierTypes1, subroutineLocalsBitSet, set1);
        }
    }

    @Override
    public boolean pushesWideValue() {
        return false;
    }

    @Override
    public final boolean continuesToNext() {
        switch (this.opcode) {
            case 153:
            case 154:
            case 155:
            case 156:
            case 157:
            case 158:
            case 159:
            case 160:
            case 161:
            case 162:
            case 163:
            case 164:
            case 165:
            case 166:
            case 168:
            case 198:
            case 199:
            case 201:
                return true;
            case 167:
            case 200:
                return false;
            case 169:
            case 170:
            case 171:
            case 172:
            case 173:
            case 174:
            case 175:
            case 176:
            case 177:
            case 178:
            case 179:
            case 180:
            case 181:
            case 182:
            case 183:
            case 184:
            case 185:
            case 186:
            case 187:
            case 188:
            case 189:
            case 190:
            case 191:
            case 192:
            case 193:
            case 194:
            case 195:
            case 196:
            case 197:
            default:
                ZkmAssert.assertTrue(false, new String[]{"invalid opcode " + this.opcode});
                return true;
        }
    }

    @Override
    public final void registerLabelTargets(SetMultiMap setMultiMap) {
        setMultiMap.addValue(this.targetLabel, this);
    }

    @Override
    public int getLength() {
        return 3;
    }

    @Override
    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
        super.writeTo(dataOutputStream);
        this.writeRelativeOffset(dataOutputStream);
    }

    @Override
    public boolean pushesWithoutPopping() {
        return this.pushesValue();
    }

    @Override
    public boolean pushesValue() {
        switch (this.opcode) {
            case 153:
            case 154:
            case 155:
            case 156:
            case 157:
            case 158:
            case 159:
            case 160:
            case 161:
            case 162:
            case 163:
            case 164:
            case 165:
            case 166:
            case 167:
            case 198:
            case 199:
            case 200:
                return false;
            case 168:
            case 201:
                return true;
            case 169:
            case 170:
            case 171:
            case 172:
            case 173:
            case 174:
            case 175:
            case 176:
            case 177:
            case 178:
            case 179:
            case 180:
            case 181:
            case 182:
            case 183:
            case 184:
            case 185:
            case 186:
            case 187:
            case 188:
            case 189:
            case 190:
            case 191:
            case 192:
            case 193:
            case 194:
            case 195:
            case 196:
            case 197:
            default:
                ZkmAssert.assertTrue(false, new String[]{"invalid opcode " + this.opcode});
                return false;
        }
    }

    public LabelInstruction getTargetLabel() {
        return this.targetLabel;
    }

    @Override
    public final boolean requiresTypedValueAt(Object object, Object object2, Object object1) {
        int bb = (Integer) object;
        int ba = (Integer) object1;
        switch (this.opcode) {
            case 153:
            case 154:
            case 155:
            case 156:
            case 157:
            case 158:
            case 159:
            case 160:
            case 161:
            case 162:
            case 163:
            case 164:
                return bb >= ba;
            case 165:
            case 166:
            case 198:
            case 199:
                return false;
            case 167:
            case 168:
            case 200:
            case 201:
                return false;
            case 169:
            case 170:
            case 171:
            case 172:
            case 173:
            case 174:
            case 175:
            case 176:
            case 177:
            case 178:
            case 179:
            case 180:
            case 181:
            case 182:
            case 183:
            case 184:
            case 185:
            case 186:
            case 187:
            case 188:
            case 189:
            case 190:
            case 191:
            case 192:
            case 193:
            case 194:
            case 195:
            case 196:
            case 197:
            default:
                ZkmAssert.assertTrue(false, new String[]{"invalid opcode " + this.opcode});
                return false;
        }
    }

    @Override
    public final boolean canFallThrough() {
        switch (this.opcode) {
            case 153:
            case 154:
            case 155:
            case 156:
            case 157:
            case 158:
            case 159:
            case 160:
            case 161:
            case 162:
            case 163:
            case 164:
            case 165:
            case 166:
            case 198:
            case 199:
                return true;
            case 167:
            case 168:
            case 200:
            case 201:
                return false;
            case 169:
            case 170:
            case 171:
            case 172:
            case 173:
            case 174:
            case 175:
            case 176:
            case 177:
            case 178:
            case 179:
            case 180:
            case 181:
            case 182:
            case 183:
            case 184:
            case 185:
            case 186:
            case 187:
            case 188:
            case 189:
            case 190:
            case 191:
            case 192:
            case 193:
            case 194:
            case 195:
            case 196:
            case 197:
            default:
                ZkmAssert.assertTrue(false, new String[]{"invalid opcode " + this.opcode});
                return true;
        }
    }

    public int readTargetOffset(ClassFileInputStream classFileInputStream) throws IOException {
        short ba = classFileInputStream.readShort();
        return this.bytecodeOffset + ba;
    }

    @Override
    public String getHolderTypeName(Object object, Object object1, Object object2) {
        return this.getMnemonic();
    }

    @Override
    public void bindLabel(Integer integer, LabelInstruction labelInstruction) {
        this.targetLabel = labelInstruction;
        labelInstruction.setVisible();
    }

    @Override
    public final void addSuccessorBlocks(Map map1, ListMultimap listMultimap, List list1) {
        BasicBlock basicBlock;
        if ((basicBlock = (BasicBlock) map1.get(this.targetLabel)) == null) {
            basicBlock = new BasicBlock();
            map1.put(this.targetLabel, basicBlock);
            list1.add(basicBlock);
        }

        listMultimap.addValue(this, basicBlock);
    }

    public BranchInstruction(int ba, ClassFileInputStream classFileInputStream, int bytecodeOffset, ListMultimap listMultimap) throws IOException {
        super(ba);
        this.bytecodeOffset = bytecodeOffset;
        int bc = this.readTargetOffset(classFileInputStream);
        if (bc < 0) {
            throw new ZkmRuntimeException("Bad offset : " + ba + " : " + bytecodeOffset + " : " + this.bytecodeOffset + " : " + bc);
        }

        listMultimap.addValue(Instruction.integerCache.valueOf(bc), this);
    }

    public int getRelativeOffset() {
        return this.targetLabel.getOffset() - this.bytecodeOffset;
    }

    public void writeRelativeOffset(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeShort(this.getRelativeOffset());
    }

    @Override
    public void setOffset(int bytecodeOffset) {
        this.bytecodeOffset = bytecodeOffset;
    }
}
