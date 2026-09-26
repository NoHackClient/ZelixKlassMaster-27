package com.zelix.klassmaster.classfile.constpool;

public interface ConstantReferenceVisitable {
    boolean registerUsage(UsedConstantsCollector usedConstantsCollector, Object object, Object object1);
}
