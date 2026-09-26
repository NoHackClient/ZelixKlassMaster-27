package com.zelix.klassmaster.classfile.insn;

import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;

public interface ConstantPoolOperand {
    ConstantPoolEntry getConstantPoolEntry();
}
