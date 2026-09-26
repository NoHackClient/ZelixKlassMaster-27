package com.zelix.klassmaster.classfile.insn;

import com.zelix.klassmaster.util.ListMultimap;

import java.util.List;
import java.util.Map;

public interface JumpingInstruction extends LabelTargetHolder {
    void addSuccessorBlocks(Map map1, ListMultimap listMultimap, List list1);
}
