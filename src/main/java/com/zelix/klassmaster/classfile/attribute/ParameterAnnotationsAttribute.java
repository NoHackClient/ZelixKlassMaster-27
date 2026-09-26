



package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.config.IgnoreMissingReferencesSpec;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.util.VisitableNode;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.obfuscator.parameters.ChangedMethodDescriptor;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;

import java.util.HashMap;
import java.util.Map;
import java.io.DataOutputStream;
import java.io.IOException;

import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileBase;

import java.util.List;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.zelix.klassmaster.obfuscator.trim.TrimProcessor;
import com.zelix.klassmaster.script.ScriptEnvironment;

import java.util.Set;

public abstract class ParameterAnnotationsAttribute extends AnnotationAttributeBase {
    public AnnotationEntry[][] originalParameterAnnotations;
    public AnnotationEntry[][] parameterAnnotations;

    @Override
    public void collectReferencedClasses(final Set set) {
        if (super.valid) {
            int i;
            int n = i = 0;
            AnnotationEntry[][] array = this.parameterAnnotations;
            while (i < array.length) {
                int n2 = 0;
                int j = 0;
                AnnotationEntry[][] array2 = this.parameterAnnotations;
                while (j < array2[n].length) {
                    this.parameterAnnotations[n][n2].collectReferencedClasses(set);
                    n2 = (j = n2 + 1);
                    array2 = this.parameterAnnotations;
                }
                n = (i = n + 1);
                array = this.parameterAnnotations;
            }
        }
    }

    @Override
    public void applyMethodRenames() {
        if (super.valid) {
            int i;
            int n = i = 0;
            AnnotationEntry[][] array = this.parameterAnnotations;
            while (i < array.length) {
                int n2 = 0;
                int j = 0;
                AnnotationEntry[][] array2 = this.parameterAnnotations;
                while (j < array2[n].length) {
                    this.parameterAnnotations[n][n2].updateAfterMethodRename();
                    n2 = (j = n2 + 1);
                    array2 = this.parameterAnnotations;
                }
                n = (i = n + 1);
                array = this.parameterAnnotations;
            }
        }
    }

    public AnnotationEntry findAnnotationByType(final String s) {
        if (super.valid) {
            int i;
            int n = i = 0;
            AnnotationEntry[][] array = this.parameterAnnotations;
            while (i < array.length) {
                int n2 = 0;
                int j = 0;
                AnnotationEntry[][] array2 = this.parameterAnnotations;
                while (j < array2[n].length) {
                    if (s.equals(this.parameterAnnotations[n][n2].getTypeName())) {
                        return this.parameterAnnotations[n][n2];
                    }
                    n2 = (j = n2 + 1);
                    array2 = this.parameterAnnotations;
                }
                n = (i = n + 1);
                array = this.parameterAnnotations;
            }
        }
        return null;
    }

    public String[] getOriginalAnnotationTypes(final int n) {
        if (!super.valid) {
            return new String[0];
        }
        if (this.originalParameterAnnotations == null) {
            return this.getAnnotationTypes(n);
        }
        if (this.originalParameterAnnotations.length > n) {
            final String[] array = new String[this.originalParameterAnnotations[n].length];
            int n2 = 0;
            int i = 0;
            AnnotationEntry[][] array2 = this.originalParameterAnnotations;
            while (i < array2[n].length) {
                array[n2] = this.originalParameterAnnotations[n][n2].getTypeName();
                n2 = (i = n2 + 1);
                array2 = this.originalParameterAnnotations;
            }
            return array;
        }
        return new String[0];
    }

    public String[] getAnnotationTypes(final int n) {
        if (super.valid && this.parameterAnnotations.length > n) {
            final String[] array = new String[this.parameterAnnotations[n].length];
            int n2 = 0;
            int i = 0;
            AnnotationEntry[][] array2 = this.parameterAnnotations;
            while (i < array2[n].length) {
                array[n2] = this.parameterAnnotations[n][n2].getTypeName();
                n2 = (i = n2 + 1);
                array2 = this.parameterAnnotations;
            }
            return array;
        }
        return new String[0];
    }

    @Override
    public boolean trimAnnotations(final Object o, final Object o2, final Object o3) {
        final ScriptEnvironment scriptEnvironment = (ScriptEnvironment) o2;
        final TrimProcessor trimProcessor = (TrimProcessor) o;
        if (super.valid) {
            final PrintWriter logWriter = scriptEnvironment.getLogWriter();
            boolean b = false;
            boolean b2 = true;
            final ArrayList list = new ArrayList();
            int n = 0;
            int i = 0;
            AnnotationEntry[][] array = this.parameterAnnotations;
            while (i < array.length) {
                final ArrayList list2 = new ArrayList();
                list.add(list2);
                int n2 = 0;
                int j = 0;
                AnnotationEntry[][] array2 = this.parameterAnnotations;
                while (j < array2[n].length) {
                    final AnnotationEntry annotationEntry = this.parameterAnnotations[n][n2];
                    final ClassFileBase annotationClass = annotationEntry.getAnnotationClass();
                    if (trimProcessor.isAnnotationRetained(annotationClass)) {
                        b2 = false;
                        list2.add(annotationEntry);
                        final String string = "Retaining element in '" + this.getAttributeName() + "' attribute in " + (this.isClassLevel() ? "" : ("'" + this.getOwnerName() + "' in ")) + "class '" + this.getDisplayLocationName() + "' because of annotation attribute exclusion for class '" + annotationClass.getInputPath() + "'.";
                        if (scriptEnvironment.isVerbose()) {
                            logWriter.println("\t" + string);
                        }
                        ((PrintWriter) o3).println(string);
                    } else {
                        b = true;
                    }
                    n2 = (j = n2 + 1);
                    array2 = this.parameterAnnotations;
                }
                n = (i = n + 1);
                array = this.parameterAnnotations;
            }
            if (b) {
                final int size = list.size();
                final AnnotationEntry[][] parameterAnnotations = new AnnotationEntry[size][];
                for (int k = 0; k < size; ++k) {
                    final List list3 = (List) list.get(k);
                    parameterAnnotations[k] = ((com.zelix.klassmaster.classfile.attribute.AnnotationEntry[]) (list3.toArray(new AnnotationEntry[list3.size()])));
                }
                this.parameterAnnotations = parameterAnnotations;
            }
            return b2;
        }
        return true;
    }

    @Override
    public void applyFieldRenames() {
        if (super.valid) {
            int i;
            int n = i = 0;
            AnnotationEntry[][] array = this.parameterAnnotations;
            while (i < array.length) {
                int n2 = 0;
                int j = 0;
                AnnotationEntry[][] array2 = this.parameterAnnotations;
                while (j < array2[n].length) {
                    this.parameterAnnotations[n][n2].updateAfterFieldRename();
                    n2 = (j = n2 + 1);
                    array2 = this.parameterAnnotations;
                }
                n = (i = n + 1);
                array = this.parameterAnnotations;
            }
        }
    }

    @Override
    public int getLength() {
        if (super.valid) {
            int length = 1;
            for (int length2 = this.parameterAnnotations.length, i = 0; i < length2; ++i) {
                length += 2;
                for (int length3 = this.parameterAnnotations[i].length, j = 0; j < length3; ++j) {
                    length += this.parameterAnnotations[i][j].getByteLength();
                }
            }
            return this.length = length;
        }
        return super.rawBytes.length;
    }

    @Override
    public void collectReferencedMembers(final Set set, final Set set2, final Set set3, final Set set4) {
        if (super.valid) {
            int i;
            int n = i = 0;
            AnnotationEntry[][] array = this.parameterAnnotations;
            while (i < array.length) {
                int n2 = 0;
                int j = 0;
                AnnotationEntry[][] array2 = this.parameterAnnotations;
                while (j < array2[n].length) {
                    this.parameterAnnotations[n][n2].collectReferencedMembers(set, set2, set3, set4);
                    n2 = (j = n2 + 1);
                    array2 = this.parameterAnnotations;
                }
                n = (i = n + 1);
                array = this.parameterAnnotations;
            }
        }
    }

    public ParameterAnnotationsAttribute(final ClassFileComponent classFileComponent, final int n, final String s, final ClassFileInputStream classFileInputStream, final ListMultimap listMultimap, final PrintWriter printWriter, final String str) throws IOException {
        super(classFileComponent, n, s, classFileInputStream, listMultimap);
        final byte[] array = new byte[this.length];
        classFileInputStream.read(array);
        final ClassFileInputStream fromBytes = ClassFileInputStream.fromBytes(array, false);
        try {
            if (this.length >= 1) {
                final int unsignedByte = fromBytes.readUnsignedByte();
                this.parameterAnnotations = new AnnotationEntry[unsignedByte][];
                Label_0247:
                for (int i = 0; i < unsignedByte; ++i) {
                    final int unsignedShort = fromBytes.readUnsignedShort();
                    this.parameterAnnotations[i] = new AnnotationEntry[unsignedShort];
                    for (int j = 0; j < unsignedShort; ++j) {
                        this.parameterAnnotations[i][j] = AnnotationEntry.read(this, fromBytes, listMultimap);
                        if (!this.parameterAnnotations[i][j].isValid()) {
                            super.valid = false;
                            printWriter.println("ERROR: " + this.getLocationName() + " : " + str + " (A) " + this.parameterAnnotations[i][j].getErrorMessage(0, 761259100, 9539));
                            super.rawBytes = array;
                            break Label_0247;
                        }
                    }
                }
            } else {
                super.valid = false;
                printWriter.println("ERROR: " + this.getLocationName() + " : " + str + " (B) : length=" + this.length);
                super.rawBytes = array;
            }
        } catch (final IOException obj) {
            super.valid = false;
            printWriter.println("ERROR: " + this.getLocationName() + " : " + str + " is possibly corrupt : " + obj);
            super.rawBytes = array;
        } finally {
            fromBytes.close();
        }
    }

    @Override
    public void writeRemapped(final DataOutputStream dataOutputStream, final Object o, final Object o2) throws IOException {
        final ScriptEnvironment scriptEnvironment = (ScriptEnvironment) o2;
        final Map map = (Map) o;
        super.writeRemapped(dataOutputStream, map, scriptEnvironment);
        if (super.valid) {
            dataOutputStream.writeByte(this.parameterAnnotations.length);
            int n = 0;
            int i = 0;
            AnnotationEntry[][] array = this.parameterAnnotations;
            while (i < array.length) {
                dataOutputStream.writeShort(this.parameterAnnotations[n].length);
                int n2 = 0;
                int j = 0;
                AnnotationEntry[][] array2 = this.parameterAnnotations;
                while (j < array2[n].length) {
                    this.parameterAnnotations[n][n2].writeRemapped(dataOutputStream, map, scriptEnvironment);
                    n2 = (j = n2 + 1);
                    array2 = this.parameterAnnotations;
                }
                n = (i = n + 1);
                array = this.parameterAnnotations;
            }
        } else {
            dataOutputStream.write(super.rawBytes);
        }
    }

    @Override
    public void remapClassNames(final Object o, final Object o2, final Object o3, final Object o4) throws ZkmProcessingException {
        final HashMap hashMap = (HashMap) o3;
        final HashMap hashMap2 = (HashMap) o4;
        if (super.valid) {
            int i;
            int n = i = 0;
            AnnotationEntry[][] array = this.parameterAnnotations;
            while (i < array.length) {
                int n2 = 0;
                int j = 0;
                AnnotationEntry[][] array2 = this.parameterAnnotations;
                while (j < array2[n].length) {
                    this.parameterAnnotations[n][n2].updateAfterClassRename(hashMap, hashMap2);
                    n2 = (j = n2 + 1);
                    array2 = this.parameterAnnotations;
                }
                n = (i = n + 1);
                array = this.parameterAnnotations;
            }
        }
    }

    public void insertAddedParameters(final ChangedMethodDescriptor changedMethodDescriptor) {
        final int paramCount = changedMethodDescriptor.getParamCount();
        final AnnotationEntry[][] array = new AnnotationEntry[paramCount][];
        final int addedParamCount = changedMethodDescriptor.getAddedParamCount();
        int n = 0;
        final int n2 = 0;
        final int n3 = 0;
        int n4 = n2 + 1;
        int n5 = changedMethodDescriptor.getParamIndex(n3);
        for (int i = 0; i < paramCount; ++i) {
            if (i == n5) {
                array[i] = new AnnotationEntry[0];
                if (n4 < addedParamCount) {
                    n5 = changedMethodDescriptor.getParamIndex(n4++);
                }
            } else if (n < this.parameterAnnotations.length) {
                array[i] = this.parameterAnnotations[n++];
            } else {
                array[i] = new AnnotationEntry[0];
            }
        }
        if (this.originalParameterAnnotations == null) {
            this.originalParameterAnnotations = this.parameterAnnotations;
            this.parameterAnnotations = array;
        } else {
            this.parameterAnnotations = array;
        }
        this.getLength();
    }

    @Override
    public void collectUsedConstants(final char c, final int n, final UsedConstantsCollector usedConstantsCollector, final char c2) {
        final VisitableNode[] flowGuardNodes = ClassFileComponent.getFlowGuardNodes();
        usedConstantsCollector.markUsed(this.nameConstant, this, this.getParent());
        Label_0126:
        {
            if (super.valid) {
                int i;
                int n2 = i = 0;
                AnnotationEntry[][] array = this.parameterAnnotations;
                Label_0047:
                while (i < array.length) {
                    int n3 = 0;
                    int j = 0;
                    AnnotationEntry[][] array2 = this.parameterAnnotations;
                    while (true) {
                        while (j < array2[n2].length) {
                            final VisitableNode[] array4;
                            final AnnotationEntry[] array3 = (AnnotationEntry[]) (array4 = this.parameterAnnotations[n2]);
                            if (c2 > '\0') {
                                array3[n3].collectUsedConstants('\0', 1103830477, usedConstantsCollector, '\u978d');
                                n3 = (j = n3 + 1);
                                array2 = this.parameterAnnotations;
                            } else {
                                if (array4 == null) {
                                    break Label_0126;
                                }
                                i = n2;
                                array = this.parameterAnnotations;
                                continue Label_0047;
                            }
                        }
                        ++n2;
                        VisitableNode[] array4 = flowGuardNodes;
                        continue;
                    }
                }
            }
        }
    }

    @Override
    public void write(final DataOutputStream dataOutputStream) throws IOException {
        super.write(dataOutputStream);
        if (super.valid) {
            dataOutputStream.writeByte(this.parameterAnnotations.length);
            int n = 0;
            int i = 0;
            AnnotationEntry[][] array = this.parameterAnnotations;
            while (i < array.length) {
                dataOutputStream.writeShort(this.parameterAnnotations[n].length);
                int n2 = 0;
                int j = 0;
                AnnotationEntry[][] array2 = this.parameterAnnotations;
                while (j < array2[n].length) {
                    this.parameterAnnotations[n][n2].write(15571, dataOutputStream, 54846, 41447);
                    n2 = (j = n2 + 1);
                    array2 = this.parameterAnnotations;
                }
                n = (i = n + 1);
                array = this.parameterAnnotations;
            }
        } else {
            dataOutputStream.write(super.rawBytes);
        }
    }

    @Override
    public void resolveReferences(final ClassResolver classResolver, final IgnoreMissingReferencesSpec ignoreMissingReferencesSpec, final ScriptEnvironment scriptEnvironment) throws ZkmException, IOException {
        if (super.valid) {
            int i;
            int n = i = 0;
            AnnotationEntry[][] array = this.parameterAnnotations;
            while (i < array.length) {
                int n2 = 0;
                int j = 0;
                AnnotationEntry[][] array2 = this.parameterAnnotations;
                while (j < array2[n].length) {
                    this.parameterAnnotations[n][n2].resolveReferences(classResolver, ignoreMissingReferencesSpec, scriptEnvironment);
                    n2 = (j = n2 + 1);
                    array2 = this.parameterAnnotations;
                }
                n = (i = n + 1);
                array = this.parameterAnnotations;
            }
        }
    }
}
