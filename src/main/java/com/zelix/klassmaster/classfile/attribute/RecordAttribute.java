


package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.attribute.ParsedAttributeBase;
import com.zelix.klassmaster.classfile.attribute.RecordComponent;
import com.zelix.klassmaster.classfile.constpool.ConstantUtf8;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.classfile.constpool.Utf8ConstantReplaceable;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.config.IgnoreMissingReferencesSpec;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.obfuscator.trim.TrimProcessor;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.ThreeKeyMultiMap;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class RecordAttribute
        extends ParsedAttributeBase
        implements Utf8ConstantReplaceable {
    public RecordComponent[] components;

    public void applyFieldRenames() {
        if (this.valid) {
            RecordComponent[] recordComponentArray = this.components;
            int n = recordComponentArray.length;
            for (int i = 0; i < n; ++i) {
                recordComponentArray[i].applyFieldRenames();
            }
        }
    }

    



    public RecordAttribute(ClassFileComponent classFileComponent, int n, String string, ClassFileInputStream classFileInputStream, ListMultimap listMultimap, ListMultimap listMultimap2, ThreeKeyMultiMap threeKeyMultiMap, PrintWriter printWriter) throws ZkmProcessingException, IOException {
        super(classFileComponent, n, string, classFileInputStream, listMultimap);
        byte[] byArray = new byte[this.length];
        classFileInputStream.read(byArray);
        ClassFileInputStream classFileInputStream2 = ClassFileInputStream.fromBytes(byArray, false);
        Throwable throwable = null;
        try {
            int n2 = classFileInputStream2.readUnsignedShort();
            this.components = new RecordComponent[n2];
            for (int i = 0; i < n2; ++i) {
                this.components[i] = new RecordComponent(this, classFileInputStream2, listMultimap, listMultimap2, threeKeyMultiMap, printWriter);
                if (this.components[i].isValid()) continue;
                this.valid = false;
                this.rawBytes = byArray;
                printWriter.println("ERROR: " + this.getDisplayLocationName() + " : " + this.getAttributeName() + " (A) " + this.components[i].getErrorMessage());
            }
            if (classFileInputStream2 == null) return;
        } catch (Throwable throwable2) {
            try {
                throwable = throwable2;
                throw throwable2;
            } catch (Throwable throwable3) {
                if (classFileInputStream2 == null) throw throwable3;
                if (throwable != null) {
                    try {
                        classFileInputStream2.close();
                        throw throwable3;
                    } catch (Throwable throwable4) {
                        throwable.addSuppressed(throwable4);
                    }
                    throw throwable3;
                } else {
                    classFileInputStream2.close();
                }
                throw throwable3;
            }
        }
        classFileInputStream2.close();
        return;
    }

    @Override
    public void writeRemapped(DataOutputStream dataOutputStream, Object object, Object object2) throws IOException {
        ScriptEnvironment scriptEnvironment = (ScriptEnvironment) object2;
        Map map = (Map) object;
        super.write(dataOutputStream);
        if (this.valid) {
            dataOutputStream.writeShort(this.components.length);
            RecordComponent[] recordComponentArray = this.components;
            int n = recordComponentArray.length;
            for (int i = 0; i < n; ++i) {
                recordComponentArray[i].writeRemapped(dataOutputStream, map, scriptEnvironment);
            }
        } else {
            dataOutputStream.write(this.rawBytes);
        }
    }

    public void applyMethodRenames() {
        if (this.valid) {
            RecordComponent[] recordComponentArray = this.components;
            int n = recordComponentArray.length;
            for (int i = 0; i < n; ++i) {
                recordComponentArray[i].applyMethodRenames();
            }
        }
    }

    @Override
    public void collectUsedConstants(char c, int n, UsedConstantsCollector usedConstantsCollector, char c2) {
        usedConstantsCollector.markUsed(this.nameConstant, this, this.getParent());
        RecordComponent[] recordComponentArray = this.components;
        int n2 = recordComponentArray.length;
        for (int i = 0; i < n2; ++i) {
            recordComponentArray[i].collectUsedConstants('\u0000', 1103830477, usedConstantsCollector, '\u978d');
        }
    }

    @Override
    public void remapClassNames(Object object, Object object2, Object object3, Object object4) throws ZkmProcessingException {
        HashMap hashMap = (HashMap) object3;
        int n = (Integer) object2;
        int n2 = (Integer) object;
        HashMap hashMap2 = (HashMap) object4;
        if (this.valid) {
            RecordComponent[] recordComponentArray = this.components;
            int n3 = recordComponentArray.length;
            for (int i = 0; i < n3; ++i) {
                recordComponentArray[i].remapClassNames(n2, n, hashMap, hashMap2);
            }
        }
    }

    public void trimComponentAttributes(TrimProcessor trimProcessor, ScriptEnvironment scriptEnvironment, PrintWriter printWriter) {
        if (this.valid) {
            RecordComponent[] recordComponentArray = this.components;
            int n = recordComponentArray.length;
            for (int i = 0; i < n; ++i) {
                recordComponentArray[i].trimAttributes(trimProcessor, scriptEnvironment, printWriter);
            }
        }
    }

    public void removeSignatureAttributes() {
        if (this.valid) {
            RecordComponent[] recordComponentArray = this.components;
            int n = recordComponentArray.length;
            for (int i = 0; i < n; ++i) {
                recordComponentArray[i].removeSignatureAttributes();
            }
        }
    }

    public void collectReferencedMembers(HashSet hashSet, HashSet hashSet2, HashSet hashSet3, HashSet hashSet4) {
        if (this.valid) {
            RecordComponent[] recordComponentArray = this.components;
            int n = recordComponentArray.length;
            for (int i = 0; i < n; ++i) {
                recordComponentArray[i].collectReferencedMembers(hashSet, hashSet2, hashSet3, hashSet4);
            }
        }
    }

    @Override
    public void replaceUtf8Constant(ConstantUtf8 constantUtf8, ConstantUtf8 constantUtf82) {
        super.replaceUtf8Constant(constantUtf8, constantUtf82);
        RecordComponent[] recordComponentArray = this.components;
        int n = recordComponentArray.length;
        for (int i = 0; i < n; ++i) {
            recordComponentArray[i].replaceUtf8Constant(constantUtf8, constantUtf82);
        }
    }

    public void resolveReferences(ClassResolver classResolver, IgnoreMissingReferencesSpec ignoreMissingReferencesSpec, ScriptEnvironment scriptEnvironment) throws ZkmException, IOException {
        if (this.valid) {
            RecordComponent[] recordComponentArray = this.components;
            int n = recordComponentArray.length;
            for (int i = 0; i < n; ++i) {
                recordComponentArray[i].resolveReferences(classResolver, ignoreMissingReferencesSpec, scriptEnvironment);
            }
        }
    }

    @Override
    public void write(DataOutputStream dataOutputStream) throws IOException {
        super.write(dataOutputStream);
        if (this.valid) {
            dataOutputStream.writeShort(this.components.length);
            RecordComponent[] recordComponentArray = this.components;
            int n = recordComponentArray.length;
            for (int i = 0; i < n; ++i) {
                recordComponentArray[i].writeTo(dataOutputStream);
            }
        } else {
            dataOutputStream.write(this.rawBytes);
        }
    }
}
