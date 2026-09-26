package com.zelix.klassmaster.classfile;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.ObservableModel;

import java.io.IOException;

public interface MethodChangeListener extends ConstantPoolSource {
    void handleObservedChange(ObservableModel observableModel1, Object object, Object object1, Object object2) throws ZkmException, IOException;
}
