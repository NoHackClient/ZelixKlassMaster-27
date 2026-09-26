package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.classfile.constpool.Utf8ConstantReplaceable;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.BooleanFlag;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.ZkmStringUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class SourceDebugExtensionAttribute extends Attribute implements Utf8ConstantReplaceable {
    public byte[] debugExtension = new byte[this.length];

    @Override
    public void collectUsedConstants(char ba, int bb, UsedConstantsCollector usedConstantsCollector, char bc) {
        usedConstantsCollector.markUsed(this.nameConstant, this, this.getParent());
    }

    @Override
    public int getLength() {
        return this.debugExtension.length;
    }

    public SourceDebugExtensionAttribute(
            ClassFileComponent classFileComponent, int ba, String string, ClassFileInputStream classFileInputStream, ListMultimap listMultimap
    ) throws IOException {
        super(classFileComponent, ba, string, classFileInputStream, listMultimap);
        classFileInputStream.read(this.debugExtension);
    }

    @Override
    public void remapClassNames(Object object1, Object object2, Object object3, Object object) throws ZkmProcessingException {
        HashMap hashMap = (HashMap) object;
        String string = new String(this.debugExtension);
        if ((HiddenOptionFlags.TRANSLATE_KOTLIN || HiddenOptionFlags.TRANSLATE_KOTLIN_METADATA) && string.startsWith("SMAP") && string.indexOf("Kotlin") > -1) {
            boolean bl = false;
            StringBuilder stringBuilder = new StringBuilder();
            StringTokenizer stringTokenizer = new StringTokenizer(string, "\n");
            BooleanFlag booleanFlag = new BooleanFlag();

            while (stringTokenizer.hasMoreTokens()) {
                String string1 = stringTokenizer.nextToken();
                String string2 = null;
                booleanFlag.setValue(false);
                if (ZkmStringUtils.countChar(string1, '/') >= 1 || this.getClassName().equals(string1)) {
                    string2 = ConstantPoolEntry.remapClassNameString(string1, hashMap, booleanFlag);
                }

                if (stringBuilder.length() > 0) {
                    stringBuilder.append("\n");
                }

                if (booleanFlag.getValue() && string2 != null && !string2.equals(string1)) {
                    bl = true;
                    stringBuilder.append(string2);
                } else {
                    stringBuilder.append(string1);
                }
            }

            if (bl) {
                this.debugExtension = stringBuilder.toString().getBytes();
            }
        }
    }

    @Override
    public void write(DataOutputStream dataOutputStream) throws IOException {
        super.write(dataOutputStream);
        dataOutputStream.write(this.debugExtension);
    }

    @Override
    public void writeRemapped(DataOutputStream dataOutputStream, Object object, Object object1) throws IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object1;
        Map map1 = (Map) object;
        ScriptEnvironment scriptEnvironment2 = scriptEnvironment1;
        Map map2 = map1;
        ScriptEnvironment scriptEnvironment3 = scriptEnvironment2;
        Map map3 = map2;
        DataOutputStream dataOutputStream1 = dataOutputStream;
        super.writeRemapped(dataOutputStream1, map3, scriptEnvironment3);
        dataOutputStream.write(this.debugExtension);
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
    }
}
