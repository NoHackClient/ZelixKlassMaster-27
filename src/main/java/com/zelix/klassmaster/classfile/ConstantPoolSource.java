package com.zelix.klassmaster.classfile;

import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;

public interface ConstantPoolSource {
    ConstantPoolEntry getConstantPoolEntry(int ba);
}
