package com.zelix.klassmaster.classfile;

import com.zelix.klassmaster.archive.InputFileLocation;
import com.zelix.klassmaster.classfile.attribute.Attribute;
import com.zelix.klassmaster.classfile.attribute.ModuleAttribute;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.ListenerRegistry;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ThreeKeyMultiMap;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public class ModuleInfoClass extends ProgramClass {
    public ModuleAttribute moduleAttribute;
    public String moduleName;
    private final String originalModuleName;

    public ModuleInfoClass(
            ClassFileInputStream classFileInputStream,
            InputFileLocation inputFileLocation,
            ObservableHolder observableHolder,
            ListenerRegistry listenerRegistry1,
            PrintWriter printWriter,
            ThreeKeyMultiMap threeKeyMultiMap
    ) throws ZkmException, IOException {
        super(classFileInputStream, inputFileLocation, observableHolder, listenerRegistry1, printWriter, threeKeyMultiMap);

        for (Attribute attribute : this.attributes) {
            if (attribute instanceof ModuleAttribute) {
                this.moduleAttribute = (ModuleAttribute) attribute;
                break;
            }
        }

        this.moduleName = this.moduleAttribute.getModuleName();
        this.originalModuleName = this.moduleName;
    }

    public void applyPackageRenames(HashMap hashMap) {
        this.constantPool.applyPackageRenames(hashMap);
    }

    public String getModuleName() {
        return this.moduleName;
    }
}
