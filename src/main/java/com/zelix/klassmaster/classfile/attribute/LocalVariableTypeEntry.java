package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.insn.LocalVariableList;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.IOException;
import java.util.HashMap;

public class LocalVariableTypeEntry extends LocalVariableEntry {
    private static final String ATTRIBUTE_NAME = "LocalVariableTypeTable";

    @Override
    public void remapDescriptorClassNames(Object object, Object object1, HashMap hashMap) throws ZkmProcessingException {
        int ba = (Integer) object1;
        int bb = (Integer) object;
        String string = this.getDescriptorConstant().getValue();
        String string1 = SignatureAttribute.remapSignature(string, bb, ba, hashMap, this);
        if (!string.equals(string1)) {
            this.getDescriptorConstant().setValue(string1);
        }
    }

    @Override
    public String getHolderTypeName(Object object, Object object1, Object object2) {
        return ATTRIBUTE_NAME;
    }

    public LocalVariableTypeEntry(
            ClassFileComponent classFileComponent,
            ClassFileInputStream classFileInputStream,
            LocalVariableList localVariableList1,
            ListMultimap listMultimap,
            ListMultimap listMultimap1
    ) throws IOException {
        super(classFileComponent, classFileInputStream, localVariableList1, listMultimap, listMultimap1);
    }
}
