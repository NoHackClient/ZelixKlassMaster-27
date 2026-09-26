package com.zelix.klassmaster.classfile.hierarchy;

import com.zelix.klassmaster.classfile.AbstractMethodInfo;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.attribute.Attribute;
import com.zelix.klassmaster.classfile.attribute.CodeAttributeBody;
import com.zelix.klassmaster.classfile.attribute.ExceptionsAttribute;
import com.zelix.klassmaster.classfile.attribute.RawCodeAttribute;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ConstantUtf8;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.classfile.insn.MethodBytecode;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.PairValueMap;
import com.zelix.klassmaster.util.ThreeKeyMultiMap;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

public class LibraryMethod extends AbstractMethodInfo {
    public int codeAttributeIndex;
    public static int noAttributeIndex = -1;

    public LibraryMethod(ClassFileBase classFileBase, ConstantUtf8 constantUtf8, ConstantUtf8 constantUtf81, Attribute[] attributes1, boolean bl, int ba) {
        super(classFileBase, constantUtf8, constantUtf81, attributes1, ba);
        this.codeAttributeIndex = noAttributeIndex;

        for (int i = 0; i < attributes1.length; i++) {
            if (attributes1[i] instanceof CodeAttributeBody) {
                this.codeAttributeIndex = i;
            } else if (attributes1[i] instanceof ExceptionsAttribute) {
                this.exceptionsAttributeIndex = i;
            }

            attributes1[i].setParent(this);
        }

        this.parameterTypes = ConstantPoolEntry.getParameterTypes(this.descriptor);
        super.placeholder = bl;
    }

    public LibraryMethod(
            ClassFileComponent classFileComponent,
            ClassFileInputStream classFileInputStream,
            ListMultimap listMultimap,
            ListMultimap listMultimap1,
            ListMultimap listMultimap2,
            PrintWriter printWriter,
            ThreeKeyMultiMap threeKeyMultiMap
    ) throws ZkmProcessingException, IOException {
        super(classFileComponent, classFileInputStream, listMultimap);
        this.codeAttributeIndex = noAttributeIndex;
        this.parameterTypes = ConstantPoolEntry.parseParameterTypes(this.descriptor, true);
        this.attributes = new Attribute[this.attributeCount];

        for (int i = 0; i < this.attributes.length; i++) {
            this.attributes[i] = Attribute.readClasspathAttribute(
                    this, classFileInputStream, listMultimap, listMultimap1, listMultimap2, printWriter, threeKeyMultiMap
            );
            if (this.attributes[i] instanceof RawCodeAttribute) {
                this.codeAttributeIndex = i;
            } else if (this.attributes[i] instanceof ExceptionsAttribute) {
                this.exceptionsAttributeIndex = i;
            }
        }

        if (this.parameterTypes != null && ConstantPoolEntry.descriptorToJavaType(this.descriptor, true) != null) {
            this.setValid(true);
        } else {
            this.setValid(false);
        }
    }

    @Override
    public void trimAttribute(Object object, Object object1, Object object2, Object object3, Object object4) throws IOException {
    }

    @Override
    public void collectUsedConstants(char ba, int bb, UsedConstantsCollector usedConstantsCollector, char bc) {
    }

    @Override
    public final boolean isProgramMember() {
        return false;
    }

    @Override
    public MethodBytecode getBytecode() throws ZkmProcessingException {
        if (this.codeAttributeIndex != noAttributeIndex) {
            CodeAttributeBody codeAttributeBody;
            if (this.attributes[this.codeAttributeIndex] instanceof RawCodeAttribute) {
                try {
                    codeAttributeBody = new CodeAttributeBody((RawCodeAttribute) this.attributes[this.codeAttributeIndex]);
                } catch (IOException iOException) {
                    throw new ZkmProcessingException(
                            "Error reading method '" + this.toDisplayString() + "' in file '" + this.getLocationName() + "' : " + iOException.getMessage(), iOException
                    );
                }

                this.attributes[this.codeAttributeIndex] = codeAttributeBody;
            } else {
                codeAttributeBody = (CodeAttributeBody) this.attributes[this.codeAttributeIndex];
            }

            return codeAttributeBody.getBytecode();
        } else {
            return null;
        }
    }

    @Override
    public void collectReachableMethods(Set set1, Set set2) throws ZkmProcessingException {
        if (set1.add(this) && this.codeAttributeIndex != noAttributeIndex) {
            CodeAttributeBody codeAttributeBody;
            if (this.attributes[this.codeAttributeIndex] instanceof RawCodeAttribute) {
                try {
                    codeAttributeBody = new CodeAttributeBody((RawCodeAttribute) this.attributes[this.codeAttributeIndex]);
                    this.attributes[this.codeAttributeIndex] = codeAttributeBody;
                } catch (IOException iOException) {
                    throw new ZkmProcessingException(
                            "Error reading method '" + this.toDisplayString() + "' in file '" + this.getLocationName() + "' : " + iOException.getMessage(), iOException
                    );
                }
            } else {
                codeAttributeBody = (CodeAttributeBody) this.attributes[this.codeAttributeIndex];
            }

            codeAttributeBody.collectReachableMethods(set1, set2);
        }
    }

    @Override
    public void collectCodeTypeReferences(Set set1, Set set2, Set set3, Set set4) throws ZkmProcessingException {
        if (set4.add(this)) {
            if (this.codeAttributeIndex != noAttributeIndex) {
                CodeAttributeBody codeAttributeBody;
                if (this.attributes[this.codeAttributeIndex] instanceof RawCodeAttribute) {
                    try {
                        codeAttributeBody = new CodeAttributeBody((RawCodeAttribute) this.attributes[this.codeAttributeIndex]);
                        this.attributes[this.codeAttributeIndex] = codeAttributeBody;
                    } catch (IOException iOException) {
                        throw new ZkmProcessingException(
                                "Error reading method '" + this.toDisplayString() + "' in file '" + this.getLocationName() + "' : " + iOException.getMessage(),
                                iOException
                        );
                    }
                } else {
                    codeAttributeBody = (CodeAttributeBody) this.attributes[this.codeAttributeIndex];
                }

                codeAttributeBody.collectReferencedClasses(set1, set2, set3, set4);
            }

            if (this.exceptionsAttributeIndex != noAttributeIndex) {
                ExceptionsAttribute exceptionsAttribute = (ExceptionsAttribute) this.attributes[this.exceptionsAttributeIndex];
                Enumeration enumeration = exceptionsAttribute.getExceptionClassNames();

                while (enumeration.hasMoreElements()) {
                    ClassFileBase classFileBase = ClassHierarchyNode.findClassFile((String) enumeration.nextElement());
                    if (classFileBase != null) {
                        set2.add(classFileBase);
                    }
                }
            }
        }
    }

    public void applyMethodRename(Map map1, PairValueMap pairValueMap) throws ZkmException, IOException {
        MethodSignature methodSignature1 = this.getSignature();
        MethodSignature methodSignature2 = (MethodSignature) map1.get(methodSignature1);
        PairValueMap pairValueMap1;
        LibraryMethod libraryMethod1;
        String string;
        if (methodSignature2 != null) {
            if (!methodSignature2.getName().equals(this.getJvmName())) {
                this.setName(methodSignature2.getName());
                pairValueMap.putPair(this, methodSignature1.getName(), methodSignature2.getName());
                return;
            }

            pairValueMap1 = pairValueMap;
            libraryMethod1 = this;
            string = this.getSourceName();
        } else {
            pairValueMap1 = pairValueMap;
            libraryMethod1 = this;
            string = this.getSourceName();
        }

        pairValueMap1.putPair(libraryMethod1, string, this.getSourceName());
    }
}
