package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.config.IgnoreMissingReferencesSpec;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.obfuscator.trim.TrimProcessor;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.SetMultiMap;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class TypeAnnotationsAttribute extends AnnotationAttributeBase {
    public AnnotationInfo[] annotations;

    public boolean isEmpty() {
        return this.annotations.length == 0;
    }

    public void registerLabelTargets(SetMultiMap setMultiMap) {
        AnnotationInfo[] annotationInfos = this.annotations;
        int ba = annotationInfos.length;

        for (int i = 0; i < ba; i++) {
            annotationInfos[i].collectLabelReferences(setMultiMap);
        }
    }

    @Override
    public final void remapClassNames(Object object2, Object object3, Object object, Object object1) throws ZkmProcessingException {
        HashMap hashMap = (HashMap) object;
        HashMap hashMap1 = (HashMap) object1;
        if (super.valid) {
            int ba = 0;
            int bb = ba;

            for (AnnotationInfo[] annotationInfos = this.annotations; bb < annotationInfos.length; annotationInfos = this.annotations) {
                this.annotations[ba].updateAfterClassRename(hashMap, hashMap1);
                bb = ++ba;
            }
        }
    }

    @Override
    public void write(DataOutputStream dataOutputStream) throws IOException {
        super.write(dataOutputStream);
        if (super.valid) {
            dataOutputStream.writeShort(this.annotations.length);
            int ba = 0;
            int bb = 0;

            for (AnnotationInfo[] annotationInfos = this.annotations; bb < annotationInfos.length; annotationInfos = this.annotations) {
                this.annotations[ba].write(15571, dataOutputStream, 54846, 41447);
                bb = ++ba;
            }
        } else {
            dataOutputStream.write(super.rawBytes);
        }
    }

    @Override
    public void resolveReferences(ClassResolver classResolver1, IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1, ScriptEnvironment scriptEnvironment1) throws ZkmException, IOException {
        if (super.valid) {
            int ba = 0;
            int bb = ba;

            for (AnnotationInfo[] annotationInfos = this.annotations; bb < annotationInfos.length; annotationInfos = this.annotations) {
                this.annotations[ba].resolveReferences(classResolver1, ignoreMissingReferencesSpec1, scriptEnvironment1);
                bb = ++ba;
            }
        }
    }

    @Override
    public boolean trimAnnotations(Object object, Object object1, Object object2) {
        TrimProcessor trimProcessor1 = (TrimProcessor) object;
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object1;
        if (super.valid) {
            boolean bl = false;
            boolean bl1 = true;
            ArrayList arrayList = new ArrayList();
            int ba = 0;
            int bb = 0;

            for (AnnotationInfo[] annotationInfos1 = this.annotations; bb < annotationInfos1.length; annotationInfos1 = this.annotations) {
                AnnotationInfo annotationInfo = this.annotations[ba];
                ClassFileBase classFileBase = annotationInfo.getAnnotationClass();
                if (trimProcessor1.isAnnotationRetained(classFileBase)) {
                    arrayList.add(annotationInfo);
                    bl1 = false;
                    String string = "Retaining element in '"
                            + this.getAttributeName()
                            + "' attribute in "
                            + (this.isClassLevel() ? "" : "'" + this.getOwnerName() + "' in ")
                            + "class '"
                            + this.getDisplayLocationName()
                            + "' because of annotation attribute exclusion for class '"
                            + annotationInfo.getInputPath()
                            + "'.";
                    if (scriptEnvironment1.isVerbose()) {
                        scriptEnvironment1.getLogWriter().println("\t" + string);
                    }

                    ((PrintWriter) object2).println(string);
                } else {
                    bl = true;
                }

                if (bl) {
                    AnnotationInfo[] annotationInfos = new AnnotationInfo[arrayList.size()];
                    this.annotations = ((com.zelix.klassmaster.classfile.attribute.AnnotationInfo[]) (arrayList.toArray(annotationInfos)));
                }

                bb = ++ba;
            }

            return bl1;
        } else {
            return false;
        }
    }

    @Override
    public void applyMethodRenames() {
        if (super.valid) {
            int ba = 0;
            int bb = ba;

            for (AnnotationInfo[] annotationInfos = this.annotations; bb < annotationInfos.length; annotationInfos = this.annotations) {
                this.annotations[ba].updateAfterMethodRename();
                bb = ++ba;
            }
        }
    }

    @Override
    public void collectReferencedClasses(Set set1) {
        if (super.valid) {
            int ba = 0;
            int bb = ba;

            for (AnnotationInfo[] annotationInfos = this.annotations; bb < annotationInfos.length; annotationInfos = this.annotations) {
                this.annotations[ba].collectReferencedClasses(set1);
                bb = ++ba;
            }
        }
    }

    public int removeThrowsAnnotations() {
        ArrayList arrayList = new ArrayList(this.annotations.length);

        for (AnnotationInfo annotationInfo : this.annotations) {
            if (!annotationInfo.isThrowsTarget()) {
                arrayList.add(annotationInfo);
            }
        }

        AnnotationInfo[] annotationInfos;
        if (arrayList.size() < this.annotations.length) {
            this.annotations = ((com.zelix.klassmaster.classfile.attribute.AnnotationInfo[]) (arrayList.toArray(new AnnotationInfo[arrayList.size()])));
            annotationInfos = this.annotations;
        } else {
            annotationInfos = this.annotations;
        }

        return annotationInfos.length;
    }

    @Override
    public void applyFieldRenames() {
        if (super.valid) {
            int ba = 0;
            int bb = ba;

            for (AnnotationInfo[] annotationInfos = this.annotations; bb < annotationInfos.length; annotationInfos = this.annotations) {
                this.annotations[ba].updateAfterFieldRename();
                bb = ++ba;
            }
        }
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
        if (super.valid) {
            dataOutputStream.writeShort(this.annotations.length);
            int ba = 0;
            int bb = 0;

            for (AnnotationInfo[] annotationInfos = this.annotations; bb < annotationInfos.length; annotationInfos = this.annotations) {
                this.annotations[ba].writeRemapped(dataOutputStream, map1, scriptEnvironment1);
                bb = ++ba;
            }
        } else {
            dataOutputStream.write(super.rawBytes);
        }
    }

    public TypeAnnotationsAttribute(
            ClassFileComponent classFileComponent,
            int ba,
            String string,
            ClassFileInputStream classFileInputStream,
            ListMultimap listMultimap,
            ListMultimap listMultimap1,
            PrintWriter printWriter,
            String string1
    ) throws IOException {
        super(classFileComponent, ba, string, classFileInputStream, listMultimap);
        byte[] bb = new byte[this.length];
        classFileInputStream.read(bb);
        ClassFileInputStream classFileInputStream1 = ClassFileInputStream.fromBytes(bb, false);

        try {
            if (this.length >= 2) {
                int bc = classFileInputStream1.readUnsignedShort();
                this.annotations = new AnnotationInfo[bc];

                for (int i = 0; i < bc; i++) {
                    this.annotations[i] = new AnnotationInfo(this, classFileInputStream1, listMultimap, listMultimap1);
                    if (!this.annotations[i].isValid()) {
                        super.valid = false;
                        super.rawBytes = bb;
                        printWriter.println("ERROR: " + this.getLocationName() + " : " + string1 + " (E) " + this.annotations[i].getErrorMessage());
                    }
                }
            } else {
                super.valid = false;
                super.rawBytes = bb;
                printWriter.println("ERROR: " + this.getLocationName() + " : " + string1 + " (F) : length=" + this.length);
            }
        } catch (IOException iOException) {
            super.valid = false;
            super.rawBytes = bb;
            printWriter.println("ERROR: " + this.getLocationName() + " : " + string1 + " is possibly corrupt : " + iOException);
        } finally {
            classFileInputStream1.close();
        }
    }

    public boolean removeSignatureDependentAnnotations() {
        boolean bl = false;
        ArrayList arrayList = new ArrayList(this.annotations.length);

        for (AnnotationInfo annotationInfo : this.annotations) {
            if (!annotationInfo.referencesTypeArguments()) {
                arrayList.add(annotationInfo);
            } else {
                bl = true;
            }
        }

        if (arrayList.size() < this.annotations.length) {
            this.annotations = ((com.zelix.klassmaster.classfile.attribute.AnnotationInfo[]) (arrayList.toArray(new AnnotationInfo[arrayList.size()])));
        }

        return bl;
    }

    public void removeAnnotationsAtLabels(HashSet hashSet, SetMultiMap setMultiMap) {
        ArrayList arrayList = new ArrayList(this.annotations.length);

        for (AnnotationInfo annotationInfo : this.annotations) {
            if (!annotationInfo.removeDeadLabelTargets(hashSet, setMultiMap)) {
                arrayList.add(annotationInfo);
            }
        }

        if (arrayList.size() < this.annotations.length) {
            this.annotations = ((com.zelix.klassmaster.classfile.attribute.AnnotationInfo[]) (arrayList.toArray(new AnnotationInfo[arrayList.size()])));
        }
    }

    @Override
    public void collectReferencedMembers(Set set1, Set set2, Set set3, Set set4) {
        if (super.valid) {
            int ba = 0;
            int bb = ba;

            for (AnnotationInfo[] annotationInfos = this.annotations; bb < annotationInfos.length; annotationInfos = this.annotations) {
                this.annotations[ba].collectReferencedMembers(set1, set2, set3, set4);
                bb = ++ba;
            }
        }
    }

    @Override
    public final void collectUsedConstants(char bc, int bd, UsedConstantsCollector usedConstantsCollector, char be) {
        usedConstantsCollector.markUsed(this.nameConstant, this, this.getParent());
        if (super.valid) {
            int ba = 0;
            int bb = ba;

            for (AnnotationInfo[] annotationInfos = this.annotations; bb < annotationInfos.length; annotationInfos = this.annotations) {
                this.annotations[ba].collectUsedConstants('\u0000', 1103830477, usedConstantsCollector, '鞍');
                bb = ++ba;
            }
        }
    }

    @Override
    public int getLength() {
        int ba = 2;

        for (AnnotationInfo annotationInfo : this.annotations) {
            ba += annotationInfo.getByteLength();
        }

        this.length = ba;
        return ba;
    }
}
