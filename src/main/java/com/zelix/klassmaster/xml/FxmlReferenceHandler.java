package com.zelix.klassmaster.xml;

import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.TwoKeyMap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.Map;

public class FxmlReferenceHandler implements XmlClassReferenceHandler {
    public final FxmlFileUpdater fxmlUpdater;
    public final String referencePrefix;
    public final Map classReferences;
    public final Map fieldReferences;
    public final Map methodReferences;
    public final TwoKeyMap methodNameReferences;

    @Override
    public void handleResourceLocation(Object object, Object object1) throws ZkmException, IOException {
    }

    @Override
    public boolean handleControllerFieldId(
            XmlElementNode xmlElementNode, ProgramClass programClass1, ClassFileBase classFileBase, Object object, String string, ObservableHolder observableHolder
    ) throws ZkmException, IOException {
        String string1 = (String) object;
        String string2 = ZkmUtils.concatStrings(
                new String[]{this.referencePrefix, xmlElementNode.getTagName(), "' tag '", string1, "' attribute '", (String) observableHolder.getValue(), "' value"}
        );
        return this.fxmlUpdater.addTypedFieldReference(programClass1, classFileBase, string, this.fieldReferences, string2);
    }

    @Override
    public void handlePropertyTag(XmlElementNode xmlElementNode, ProgramClass programClass1) throws ZkmException, IOException {
        this.fxmlUpdater
                .addPropertyAccessorReferences(
                        programClass1,
                        xmlElementNode.getTagName(),
                        this.methodReferences,
                        this.methodNameReferences,
                        ZkmUtils.concatStrings(new String[]{this.referencePrefix, xmlElementNode.getTagName(), "' tag"})
                );
    }

    @Override
    public void handleStaticPropertyTag(XmlElementNode xmlElementNode, ProgramClass programClass1, Object object, String string) throws ZkmException, IOException {
        programClass1.markReferencedFromXml();
        this.fxmlUpdater
                .addPropertyAccessorReferences(
                        programClass1,
                        string,
                        this.methodReferences,
                        this.methodNameReferences,
                        ZkmUtils.concatStrings(new String[]{this.referencePrefix, xmlElementNode.getTagName(), "' tag"})
                );
    }

    @Override
    public void handleConstantAttribute(Object object, ProgramClass programClass1, Object object1, ObservableHolder observableHolder) throws ZkmException, IOException {
        String string = (String) object1;
        XmlElementNode xmlElementNode = (XmlElementNode) object;
        this.fxmlUpdater
                .addFieldReference(
                        programClass1,
                        (String) observableHolder.getValue(),
                        this.fieldReferences,
                        ZkmUtils.concatStrings(
                                new String[]{
                                        this.referencePrefix, xmlElementNode.getTagName(), "' tag '", string, "' attribute '", (String) observableHolder.getValue(), "' value"
                                }
                        )
                );
    }

    @Override
    public void handlePropertyAttribute(XmlElementNode xmlElementNode, ProgramClass programClass1, String string) throws ZkmException, IOException {
        String string1 = ZkmUtils.concatStrings(new String[]{this.referencePrefix, xmlElementNode.getTagName(), "' tag '", string, "' attribute"});
        this.fxmlUpdater.addPropertyAccessorReferences(programClass1, string, this.methodReferences, this.methodNameReferences, string1);
    }

    @Override
    public boolean handleAnnotatedEventHandler(XmlElementNode xmlElementNode, Object object, String string, ObservableHolder observableHolder) throws ZkmException, IOException {
        String string1 = (String) object;
        String string2 = ZkmUtils.concatStrings(
                new String[]{this.referencePrefix, xmlElementNode.getTagName(), "' tag '", string1, "' attribute '", (String) observableHolder.getValue(), "' value"}
        );
        FxmlFileUpdater fxmlFileUpdater = this.fxmlUpdater;
        JavaFxHandlerSignatureFilter javaFxHandlerSignatureFilter1 = FxmlFileUpdater.getHandlerSignatureFilter(this.fxmlUpdater);
        String string3 = string2;
        TwoKeyMap twoKeyMap = this.methodNameReferences;
        Map map2 = this.methodReferences;
        Map map1 = this.classReferences;
        Integer integer = 0;
        JavaFxHandlerSignatureFilter javaFxHandlerSignatureFilter = javaFxHandlerSignatureFilter1;
        Object object1 = null;
        return fxmlFileUpdater.addMatchingMethodReferences(
                string, (String) null, object1, javaFxHandlerSignatureFilter, "javafx/fxml/FXML", integer, map1, map2, twoKeyMap, string3
        );
    }

    @Override
    public boolean handleControllerEventHandler(
            XmlElementNode xmlElementNode, ProgramClass programClass1, Object object, String string, ObservableHolder observableHolder
    ) throws ZkmException, IOException {
        String string1 = (String) object;
        String string2 = ZkmUtils.concatStrings(
                new String[]{this.referencePrefix, xmlElementNode.getTagName(), "' tag '", string1, "' attribute '", (String) observableHolder.getValue(), "' value"}
        );
        FxmlFileUpdater fxmlFileUpdater = this.fxmlUpdater;
        JavaFxHandlerSignatureFilter javaFxHandlerSignatureFilter1 = FxmlFileUpdater.getHandlerSignatureFilter(this.fxmlUpdater);
        String string3 = string2;
        TwoKeyMap twoKeyMap = this.methodNameReferences;
        Map map1 = this.methodReferences;
        JavaFxHandlerSignatureFilter javaFxHandlerSignatureFilter = javaFxHandlerSignatureFilter1;
        Object object1 = null;
        return fxmlFileUpdater.addHierarchyMethodReferences(programClass1, string, (String) null, object1, javaFxHandlerSignatureFilter, map1, twoKeyMap, string3);
    }

    @Override
    public boolean handleAnnotatedFieldId(
            XmlElementNode xmlElementNode, ClassFileBase classFileBase, Object object, String string, ObservableHolder observableHolder
    ) throws ZkmException, IOException {
        String string1 = (String) object;
        String string2 = ZkmUtils.concatStrings(
                new String[]{this.referencePrefix, xmlElementNode.getTagName(), "' tag '", string1, "' attribute '", (String) observableHolder.getValue(), "' value"}
        );
        return this.fxmlUpdater.addMatchingFieldReferences(classFileBase, string, this.fieldReferences, string2);
    }

    @Override
    public void handleFactoryAttribute(Object object, ProgramClass programClass1, Object object1, ObservableHolder observableHolder) throws ZkmException, IOException {
        XmlElementNode xmlElementNode = (XmlElementNode) object;
        String string = (String) object1;
        String string1 = (String) observableHolder.getValue();
        String string2 = ZkmUtils.concatStrings(
                new String[]{this.referencePrefix, xmlElementNode.getTagName(), "' tag '", string, "' attribute '", string1, "' value"}
        );
        FxmlFileUpdater fxmlFileUpdater = this.fxmlUpdater;
        JavaFxHandlerSignatureFilter javaFxHandlerSignatureFilter1 = FxmlFileUpdater.getHandlerSignatureFilter(this.fxmlUpdater);
        String string3 = string2;
        TwoKeyMap twoKeyMap = this.methodNameReferences;
        Map map1 = this.methodReferences;
        JavaFxHandlerSignatureFilter javaFxHandlerSignatureFilter = javaFxHandlerSignatureFilter1;
        Object object2 = null;
        fxmlFileUpdater.addHierarchyMethodReferences(programClass1, string1, (String) null, object2, javaFxHandlerSignatureFilter, map1, twoKeyMap, string3);
    }

    @Override
    public void handleControllerAttribute(Object object, Object object1, Object object2, Object object3) throws ZkmException, IOException {
        String string = (String) object2;
        XmlElementNode xmlElementNode = (XmlElementNode) object;
        String string1 = ZkmUtils.concatStrings(new String[]{this.referencePrefix, xmlElementNode.getTagName(), "' tag '", string, "' attribute"});
        this.classReferences.put(object1, string1);
    }

    @Override
    public void handleQualifiedClassTag(Object object, ProgramClass programClass1) throws ZkmException, IOException {
        programClass1.markPackageReferencedFromXml();
        programClass1.markReferencedFromXml();
    }

    @Override
    public void handleResolvedClassElement(Object object, Object object1) {
        XmlElementNode xmlElementNode = (XmlElementNode) object;
        ProgramClass programClass1 = (ProgramClass) object1;
        this.classReferences.put(programClass1, ZkmUtils.concatStrings(new String[]{this.referencePrefix, xmlElementNode.getTagName(), "' tag"}));
    }

    @Override
    public void handleSimpleClassTag(Object object, ProgramClass programClass1) throws ZkmException, IOException {
        programClass1.markReferencedFromXml();
    }

    @Override
    public void handleStaticPropertyAttribute(XmlElementNode xmlElementNode, ProgramClass programClass1, String string, Object object, String string1) throws ZkmException, IOException {
        programClass1.markReferencedFromXml();
        String string2 = ZkmUtils.concatStrings(new String[]{this.referencePrefix, xmlElementNode.getTagName(), "' tag '", string, "' attribute"});
        this.classReferences.put(programClass1, string2);
        this.fxmlUpdater.addPropertyAccessorReferences(programClass1, string1, this.methodReferences, this.methodNameReferences, string2);
    }

    public FxmlReferenceHandler(FxmlFileUpdater fxmlFileUpdater, Map map1, Map map2, Map map3, TwoKeyMap twoKeyMap) {
        this.fxmlUpdater = fxmlFileUpdater;
        this.referencePrefix = "reference in '" + FxmlFileUpdater.getResourceName(this.fxmlUpdater) + "' by '";
        this.classReferences = map1;
        this.fieldReferences = map2;
        this.methodReferences = map3;
        this.methodNameReferences = twoKeyMap;
    }

    @Override
    public void reportWarning(Object object, Object object1) {
    }
}
