package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.insn.LocalVariableList;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.IOException;
import java.io.PrintWriter;

public class LocalVariableTypeTableAttribute extends LocalVariableTableBase {
    private static final String INVALID_ATTRIBUTE_MESSAGE = "Invalid LocalVariableTypeTable Attribute";

    public LocalVariableTypeTableAttribute(
            ClassFileComponent classFileComponent,
            int ba,
            String string,
            ClassFileInputStream classFileInputStream,
            LocalVariableList localVariableList1,
            ListMultimap listMultimap,
            PrintWriter printWriter,
            ListMultimap listMultimap1
    ) throws IOException {
        super(classFileComponent, ba, string, classFileInputStream, localVariableList1, listMultimap, printWriter, listMultimap1, INVALID_ATTRIBUTE_MESSAGE);
    }

    @Override
    public final LocalVariableEntry readEntry(
            ClassFileInputStream classFileInputStream, LocalVariableList localVariableList1, ListMultimap listMultimap, ListMultimap listMultimap1
    ) throws IOException {
        return new LocalVariableTypeEntry(this, classFileInputStream, localVariableList1, listMultimap, listMultimap1);
    }
}
