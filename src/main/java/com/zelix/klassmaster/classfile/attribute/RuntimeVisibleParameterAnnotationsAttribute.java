package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.IOException;
import java.io.PrintWriter;

public class RuntimeVisibleParameterAnnotationsAttribute extends ParameterAnnotationsAttribute {
    private static final String ATTRIBUTE_NAME = "RuntimeVisibleParameterAnnotations";

    public RuntimeVisibleParameterAnnotationsAttribute(
            ClassFileComponent classFileComponent,
            int ba,
            String string,
            ClassFileInputStream classFileInputStream,
            ListMultimap listMultimap,
            PrintWriter printWriter
    ) throws IOException {
        super(classFileComponent, ba, string, classFileInputStream, listMultimap, printWriter, ATTRIBUTE_NAME);
    }
}
