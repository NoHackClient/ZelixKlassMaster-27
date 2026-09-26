package com.zelix.klassmaster.xml;

import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.ObservableHolder;

import java.io.IOException;

public interface XmlClassReferenceHandler {
    void handleQualifiedClassTag(Object object, ProgramClass programClass1) throws ZkmException, IOException;

    void handlePropertyAttribute(XmlElementNode xmlElementNode, ProgramClass programClass1, String string) throws ZkmException, IOException;

    void handleConstantAttribute(Object object, ProgramClass programClass1, Object object1, ObservableHolder observableHolder) throws ZkmException, IOException;

    void handleResourceLocation(Object object, Object object1) throws ZkmException, IOException;

    void reportWarning(Object object, Object object1);

    void handleFactoryAttribute(Object object, ProgramClass programClass1, Object object1, ObservableHolder observableHolder) throws ZkmException, IOException;

    boolean handleAnnotatedEventHandler(XmlElementNode xmlElementNode, Object object, String string, ObservableHolder observableHolder) throws ZkmException, IOException;

    void handleSimpleClassTag(Object object, ProgramClass programClass1) throws ZkmException, IOException;

    void handleStaticPropertyAttribute(XmlElementNode xmlElementNode, ProgramClass programClass1, String string, Object object, String string1) throws ZkmException, IOException;

    boolean handleControllerFieldId(
            XmlElementNode xmlElementNode, ProgramClass programClass1, ClassFileBase classFileBase, Object object, String string, ObservableHolder observableHolder
    ) throws ZkmException, IOException;

    void handleControllerAttribute(Object object, Object object1, Object object2, Object object3) throws ZkmException, IOException;

    boolean handleAnnotatedFieldId(XmlElementNode xmlElementNode, ClassFileBase classFileBase, Object object, String string, ObservableHolder observableHolder) throws ZkmException, IOException;

    void handlePropertyTag(XmlElementNode xmlElementNode, ProgramClass programClass1) throws ZkmException, IOException;

    void handleStaticPropertyTag(XmlElementNode xmlElementNode, ProgramClass programClass1, Object object, String string) throws ZkmException, IOException;

    void handleResolvedClassElement(Object object, Object object1);

    boolean handleControllerEventHandler(
            XmlElementNode xmlElementNode, ProgramClass programClass1, Object object, String string, ObservableHolder observableHolder
    ) throws ZkmException, IOException;
}
