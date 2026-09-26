package com.zelix.klassmaster.xml;

import com.zelix.klassmaster.archive.ZkmFileUtils;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;

public class FxmlNameResolver implements XmlClassReferenceHandler {
    public final FxmlFileUpdater fxmlUpdater;

    @Override
    public void handleFactoryAttribute(Object object, ProgramClass programClass1, Object object1, ObservableHolder observableHolder) throws ZkmException, IOException {
        String string = (String) observableHolder.getValue();
        String string1 = this.fxmlUpdater.lookupRenamedMethodName(programClass1.getDottedClassName(), string, BeanPropertyXmlHandler.GETTER_DESCRIPTOR, 8);
        if (string1 != null && !string1.equals(string)) {
            observableHolder.setValue(string1);
        }
    }

    @Override
    public void handleQualifiedClassTag(Object object, ProgramClass programClass1) throws ZkmException, IOException {
        XmlElementNode xmlElementNode = (XmlElementNode) object;
        xmlElementNode.getTagName();
        xmlElementNode.setTagName(programClass1.getDottedClassName());
    }

    @Override
    public boolean handleControllerFieldId(
            XmlElementNode xmlElementNode, ProgramClass programClass1, ClassFileBase classFileBase, Object object, String string, ObservableHolder observableHolder
    ) throws ZkmException, IOException {
        xmlElementNode.getTagName();
        String string1 = this.fxmlUpdater.getRenamedTypedFieldName(programClass1, classFileBase, string);
        if (string1 != null && !string1.equals(string)) {
            observableHolder.setValue(string1);
        }

        return string1 != null;
    }

    @Override
    public void handleResolvedClassElement(Object object, Object object1) {
    }

    @Override
    public void handleStaticPropertyAttribute(XmlElementNode xmlElementNode, ProgramClass programClass1, String string, Object object, String string1) throws ZkmException, IOException {
        String string2 = (String) object;
        xmlElementNode.getTagName();
        ObservableHolder observableHolder = new ObservableHolder(string1);
        String string3 = (String) ZkmUtils.mapOrSelf(programClass1.getClassName(), FxmlFileUpdater.getOriginalClassNames(this.fxmlUpdater));
        this.fxmlUpdater.resolveRenamedPropertyName(programClass1.getDottedClassName(), string3, string1, observableHolder);
        if (!((String) observableHolder.getValue()).equals(string1) || !programClass1.getClassName().equals(string3)) {
            String string4 = ZkmUtils.concatStrings(
                    new String[]{
                            string2.indexOf(46) == -1 ? programClass1.getSimpleName() : programClass1.getDottedClassName(), ".", (String) observableHolder.getValue()
                    }
            );
            xmlElementNode.renameAttribute(string, string4);
        }
    }

    @Override
    public void handleSimpleClassTag(Object object, ProgramClass programClass1) throws ZkmException, IOException {
        XmlElementNode xmlElementNode = (XmlElementNode) object;
        xmlElementNode.getTagName();
        xmlElementNode.setTagName(programClass1.getSimpleName());
    }

    @Override
    public boolean handleControllerEventHandler(
            XmlElementNode xmlElementNode, ProgramClass programClass1, Object object, String string, ObservableHolder observableHolder
    ) throws ZkmException, IOException {
        xmlElementNode.getTagName();
        String string1 = this.fxmlUpdater
                .lookupRenamedMethodName(programClass1.getDottedClassName(), string, FxmlFileUpdater.getHandlerSignatureFilter(this.fxmlUpdater), 0);
        if (string1 != null && !string1.equals(string)) {
            String string2 = ZkmUtils.concatStrings(new String[]{String.valueOf('#'), string1});
            observableHolder.setValue(string2);
        }

        return string1 != null;
    }

    @Override
    public void handleResourceLocation(Object object, Object object1) throws ZkmException, IOException {
        ObservableHolder observableHolder = (ObservableHolder) object1;
        FxmlFileUpdater.getEntryName(this.fxmlUpdater);
        String string = ZkmFileUtils.getParentPath(
                this.fxmlUpdater.resourcePathTranslator.translateResourcePath(FxmlFileUpdater.getEntryPathName(this.fxmlUpdater))
        );
        FxmlFileUpdater fxmlFileUpdater;
        if (string == null) {
            string = "";
            fxmlFileUpdater = this.fxmlUpdater;
        } else {
            fxmlFileUpdater = this.fxmlUpdater;
        }

        String string1 = ZkmFileUtils.getParentPath(FxmlFileUpdater.getOriginalEntryName(fxmlFileUpdater));
        if (string1 == null) {
            string1 = "";
        }

        String string2 = ((String) object).substring(1);
        String string3 = ZkmFileUtils.resolveAgainstBase(string2, string1);
        int ba = string3.indexOf(58);
        if (ba > -1) {
            if (ba < string3.length() - 1) {
                string3 = string3.substring(ba + 1);
                fxmlFileUpdater = this.fxmlUpdater;
            } else {
                fxmlFileUpdater = this.fxmlUpdater;
            }
        } else {
            fxmlFileUpdater = this.fxmlUpdater;
        }

        String string4 = fxmlFileUpdater.resourcePathTranslator.translateResourcePath(string3);
        String string5 = string4;
        if (string2.charAt(0) != '/') {
            string5 = ZkmFileUtils.computeRelativePath(string, string4);
        }

        if (!string5.equals(string2)) {
            observableHolder.setValue('@' + string5);
        }
    }

    @Override
    public void handlePropertyTag(XmlElementNode xmlElementNode, ProgramClass programClass1) throws ZkmException, IOException {
        String string = xmlElementNode.getTagName();
        ObservableHolder observableHolder = new ObservableHolder(string);
        this.fxmlUpdater
                .resolveRenamedPropertyName(
                        programClass1.getDottedClassName(),
                        (String) ZkmUtils.mapOrSelf(programClass1.getClassName(), FxmlFileUpdater.getOriginalClassNames(this.fxmlUpdater)),
                        string,
                        observableHolder
                );
        if (!((String) observableHolder.getValue()).equals(string)) {
            xmlElementNode.setTagName((String) observableHolder.getValue());
        }
    }

    @Override
    public void handleControllerAttribute(Object object2, Object object, Object object3, Object object1) throws ZkmException, IOException {
        ProgramClass programClass1 = (ProgramClass) object;
        ((ObservableHolder) object1).setValue(programClass1.getDottedClassName());
    }

    @Override
    public void reportWarning(Object object, Object object1) {
        String string = (String) object1;
        String string1 = (String) object;
        this.fxmlUpdater.messageReporter.reportWarning(string1, string);
    }

    @Override
    public void handleStaticPropertyTag(XmlElementNode xmlElementNode, ProgramClass programClass1, Object object, String string) throws ZkmException, IOException {
        String string1 = this.fxmlUpdater.getRenamedFieldName(programClass1.getClassName(), string);
        if (!programClass1.getSimpleName().equals(object) || !string.equals(string1)) {
            String string2 = ZkmUtils.concatStrings(new String[]{programClass1.getSimpleName(), ".", string1});
            xmlElementNode.setTagName(string2);
        }
    }

    @Override
    public boolean handleAnnotatedEventHandler(XmlElementNode xmlElementNode, Object object, String string, ObservableHolder observableHolder) throws ZkmException, IOException {
        xmlElementNode.getTagName();
        ObservableHolder observableHolder1 = new ObservableHolder();
        String string1 = this.fxmlUpdater.resolveRenamedMethodByName(string, FxmlFileUpdater.getHandlerSignatureFilter(this.fxmlUpdater), observableHolder1);
        if (string1 != null && !string1.equals(string)) {
            String string2 = ZkmUtils.concatStrings(new String[]{String.valueOf('#'), string1});
            observableHolder.setValue(string2);
        }

        return string1 != null;
    }

    public FxmlNameResolver(FxmlFileUpdater fxmlFileUpdater) {
        this.fxmlUpdater = fxmlFileUpdater;
    }

    @Override
    public boolean handleAnnotatedFieldId(
            XmlElementNode xmlElementNode, ClassFileBase classFileBase, Object object, String string, ObservableHolder observableHolder
    ) throws ZkmException, IOException {
        xmlElementNode.getTagName();
        ObservableHolder observableHolder1 = new ObservableHolder();
        String string1 = this.fxmlUpdater.resolveRenamedFieldName(classFileBase, string, observableHolder1);
        if (string1 != null && !string1.equals(string)) {
            observableHolder.setValue(string1);
        }

        return string1 != null;
    }

    @Override
    public void handlePropertyAttribute(XmlElementNode xmlElementNode, ProgramClass programClass1, String string) throws ZkmException, IOException {
        xmlElementNode.getTagName();
        ObservableHolder observableHolder = new ObservableHolder(string);
        this.fxmlUpdater
                .resolveRenamedPropertyName(
                        programClass1.getDottedClassName(),
                        (String) ZkmUtils.mapOrSelf(programClass1.getClassName(), FxmlFileUpdater.getOriginalClassNames(this.fxmlUpdater)),
                        string,
                        observableHolder
                );
        if (!((String) observableHolder.getValue()).equals(string)) {
            xmlElementNode.renameAttribute(string, (String) observableHolder.getValue());
        }
    }

    @Override
    public void handleConstantAttribute(Object object, ProgramClass programClass1, Object object1, ObservableHolder observableHolder) throws ZkmException, IOException {
        String string = (String) observableHolder.getValue();
        String string1 = this.fxmlUpdater.getRenamedFieldName(programClass1.getDottedClassName(), string);
        if (!string1.equals(string)) {
            observableHolder.setValue(string1);
        }
    }
}
