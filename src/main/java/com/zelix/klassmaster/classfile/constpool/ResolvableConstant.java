package com.zelix.klassmaster.classfile.constpool;

import com.zelix.klassmaster.exceptions.ClassFileFormatException;
import com.zelix.klassmaster.util.ListMultimap;

public interface ResolvableConstant {
    int getIndex();

    ConstantPoolEntry resolve(ListMultimap listMultimap, ListMultimap listMultimap1, ListMultimap listMultimap2, ListMultimap listMultimap3) throws ClassFileFormatException;
}
