package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.config.ObfuscateOptions;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class RuntimeInvisibleAnnotationsAttribute extends AnnotationsAttribute {
    public RuntimeInvisibleAnnotationsAttribute(
            ClassFileComponent classFileComponent,
            int ba,
            String string,
            ClassFileInputStream classFileInputStream,
            ListMultimap listMultimap,
            PrintWriter printWriter
    ) throws IOException {
        super(classFileComponent, ba, string, classFileInputStream, listMultimap, printWriter, "RuntimeInvisibleAnnotations");
        if (super.valid && this.getOwningClass().isProgramClass()) {
            AnnotationEntry annotationEntry = this.findAnnotation("com/zelix/annotation/ZKMClassLevel", true);
            if (annotationEntry == null) {
                AnnotationEntry annotationEntry1 = this.findAnnotation("com/zelix/annotation/ZKMMethodLevel", true);
                if (annotationEntry1 != null) {
                    MethodInfo methodInfo1 = (MethodInfo) this.getParent();
                    this.applyMethodLevelAnnotation(methodInfo1, annotationEntry1, printWriter);
                }
            } else {
                ProgramClass programClass1 = (ProgramClass) this.getParent();
                this.applyClassLevelAnnotation(programClass1, annotationEntry, printWriter);
            }
        }
    }

    public void readSpecificationValues(AnnotationEntry annotationEntry, AnnotationValueMap annotationValueMap, PrintWriter printWriter) {
        for (AnnotationElementPair annotationElementPair : annotationEntry.elements) {
            String string = annotationElementPair.getElementName();
            AnnotationElementValue annotationElementValue = annotationElementPair.getValue();
            if (annotationElementValue.getTag() == 101) {
                EnumElementValue enumElementValue = (EnumElementValue) annotationElementValue;
                enumElementValue.getEnumTypeName();
                String string1 = enumElementValue.getEnumConstantName();
                annotationValueMap.putValue(string, ObfuscateOptions.getLevelNameForPolicy(string1));
            } else {
                printWriter.println(
                        "ERROR: "
                                + this.getLocationName()
                                + " : "
                                + " Invalid tag '"
                                + (char) annotationElementValue.getTag()
                                + "' in '"
                                + "com/zelix/annotation/ZKMClassLevel"
                                + "'"
                );
            }
        }
    }

    public AnnotationValueMap applyClassLevelAnnotation(ProgramClass programClass1, AnnotationEntry annotationEntry, PrintWriter printWriter) {
        AnnotationValueMap annotationValueMap = null;
        if (annotationEntry.elements.length > 0) {
            annotationValueMap = new AnnotationValueMap(this);
            this.readSpecificationValues(annotationEntry, annotationValueMap, printWriter);
            programClass1.setZkmAnnotationValues(annotationValueMap);
        }

        return annotationValueMap;
    }

    public AnnotationValueMap applyMethodLevelAnnotation(MethodInfo methodInfo1, AnnotationEntry annotationEntry, PrintWriter printWriter) {
        AnnotationValueMap annotationValueMap = null;
        if (annotationEntry.elements.length > 0) {
            annotationValueMap = new AnnotationValueMap(this);
            this.readSpecificationValues(annotationEntry, annotationValueMap, printWriter);
            methodInfo1.setZkmAnnotationValues(annotationValueMap);
        }

        return annotationValueMap;
    }

    public void removeAnnotationsOfType(String string, ScriptEnvironment scriptEnvironment1) {
        ArrayList arrayList = new ArrayList(super.annotations.length);

        for (AnnotationEntry annotationEntry : super.annotations) {
            if (annotationEntry.getTypeName().equals(string)) {
                if (scriptEnvironment1 != null && scriptEnvironment1.isVerbose()) {
                    ClassFileComponent classFileComponent = this.getParent();
                    scriptEnvironment1.getLogWriter()
                            .println(
                                    "Removing the processing specification annotation '"
                                            + ClassFileBase.toDottedName(string)
                                            + "' from "
                                            + (
                                            classFileComponent.isClassFile()
                                                    ? " class '" + ((ProgramClass) classFileComponent).getLocationName() + "'"
                                                    : " method '"
                                                    + ((MethodInfo) classFileComponent).toOriginalDisplayString()
                                                    + "' in class '"
                                                    + ((ProgramClass) classFileComponent.getParent()).getLocationName()
                                                    + "'"
                                    )
                            );
                }
            } else {
                arrayList.add(annotationEntry);
            }
        }

        super.annotations = ((com.zelix.klassmaster.classfile.attribute.AnnotationEntry[]) (arrayList.toArray(new AnnotationEntry[arrayList.size()])));
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
