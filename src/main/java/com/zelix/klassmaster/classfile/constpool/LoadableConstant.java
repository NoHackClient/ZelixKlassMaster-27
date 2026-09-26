package com.zelix.klassmaster.classfile.constpool;

import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public interface LoadableConstant extends ConstantReferenceVisitable {
    void setValueFromString(String string) throws ZkmException, IOException;

    @Override
    boolean registerUsage(UsedConstantsCollector usedConstantsCollector, Object object, Object object1);

    String getEditableValue();

    String getTypeName();
}
