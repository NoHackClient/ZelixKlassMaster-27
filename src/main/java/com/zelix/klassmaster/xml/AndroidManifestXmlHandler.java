package com.zelix.klassmaster.xml;

import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClassMemberLookup;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.log.MessageReporter;
import com.zelix.klassmaster.util.EnumerableMap;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ReadOnlyMultiMap;
import com.zelix.klassmaster.util.TwoKeyMap;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

public class AndroidManifestXmlHandler extends GenericXmlFileHandler {
    public String manifestPackage;

    @Override
    public void analyzeElement(XmlElementNode xmlElementNode, Map map1, Object object, Object object1, Object object2) throws ZkmException, IOException {
        Map map3 = (Map) object;
        TwoKeyMap twoKeyMap = (TwoKeyMap) object2;
        Map map2 = (Map) object1;
        String string = xmlElementNode.getTagName();
        if (string.equalsIgnoreCase("manifest")) {
            ObservableHolder observableHolder = xmlElementNode.getAttributeValue("package");
            if (observableHolder != null) {
                String string1 = (String) observableHolder.getValue();
                if (string1 != null && string1.length() > 0) {
                    this.manifestPackage = string1;
                }
            }
        } else if (!string.equalsIgnoreCase("activity")
                && !string.equalsIgnoreCase("provider")
                && !string.equalsIgnoreCase("service")
                && !string.equalsIgnoreCase("receiver")) {
            super.analyzeElement(xmlElementNode, map1, map3, map2, twoKeyMap);
        } else {
            String string3 = "reference in '" + super.resourceName + "' by '" + string + "' tag";
            ObservableHolder observableHolder1 = xmlElementNode.getAttributeValue("android:name");
            if (observableHolder1 != null) {
                String string2 = (String) observableHolder1.getValue();
                ProgramClass programClass1 = this.findProgramClass(string2);
                if (programClass1 == null && this.manifestPackage != null) {
                    programClass1 = this.findProgramClass(this.qualifyClassName(string2));
                }

                if (programClass1 != null) {
                    map1.put(programClass1, string3);
                }
            }
        }
    }

    @Override
    public void rewriteElement(Object object, Object object1) throws ZkmException, IOException {
        XmlElementNode xmlElementNode;
        label56:
        {
            xmlElementNode = (XmlElementNode) object;
            boolean predicateFlag = XmlConfigFileHandler.getPredicateFlag();
            String string = xmlElementNode.getTagName();
            boolean bl = predicateFlag;
            if (string.equalsIgnoreCase("manifest")) {
                ObservableHolder observableHolder = xmlElementNode.getAttributeValue("package");
                if (observableHolder != null) {
                    String string1 = (String) observableHolder.getValue();
                    if (string1 != null && string1.length() > 0) {
                        this.manifestPackage = string1;
                        observableHolder.setValue(this.renameDirectoryPath(string1));
                    }
                }

                if (!bl) {
                    break label56;
                }
            }

            XmlElementNode xmlElementNode1;
            String string5;
            if (!string.equalsIgnoreCase("activity")) {
                if (!string.equalsIgnoreCase("provider")) {
                    if (!string.equalsIgnoreCase("service")) {
                        if (!string.equalsIgnoreCase("receiver")) {
                            Enumeration enumeration = xmlElementNode.getAttributeNames();

                            while (true) {
                                if (!enumeration.hasMoreElements()) {
                                    break label56;
                                }

                                String string3 = (String) enumeration.nextElement();
                                ObservableHolder observableHolder1 = xmlElementNode.getAttributeValue(string3);
                                observableHolder1.setValue(this.updateReferenceValue((String) observableHolder1.getValue(), string3, false));
                            }
                        }

                        xmlElementNode1 = xmlElementNode;
                        long bd = 24856730383790L;
                        string5 = "android:name";
                    } else {
                        xmlElementNode1 = xmlElementNode;
                        long ba = 24856730383790L;
                        string5 = "android:name";
                    }
                } else {
                    xmlElementNode1 = xmlElementNode;
                    long bb = 24856730383790L;
                    string5 = "android:name";
                }
            } else {
                xmlElementNode1 = xmlElementNode;
                long bc = 24856730383790L;
                string5 = "android:name";
            }

            String string2 = string5;
            ObservableHolder observableHolder2 = xmlElementNode1.getAttributeValue(string2);
            if (observableHolder2 != null) {
                String string4 = (String) observableHolder2.getValue();
                ProgramClass programClass1 = this.findProgramClass(string4);
                if (programClass1 == null && this.manifestPackage != null) {
                    programClass1 = this.findProgramClass(this.qualifyClassName(string4));
                }

                if (programClass1 != null) {
                    observableHolder2.setValue(programClass1.getDottedClassName());
                }
            }
        }

        ((List) object1).add(xmlElementNode);
    }

    public String qualifyClassName(String string) {
        return string.startsWith(".") ? this.manifestPackage + string : this.manifestPackage + "." + string;
    }

    public AndroidManifestXmlHandler(
            String string,
            EnumerableMap enumerableMap,
            ReadOnlyMultiMap readOnlyMultiMap,
            ReadOnlyMultiMap readOnlyMultiMap1,
            ResourcePathTranslator resourcePathTranslator1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1,
            MessageReporter messageReporter1
    ) {
        super(string, enumerableMap, readOnlyMultiMap, readOnlyMultiMap1, resourcePathTranslator1, classMemberLookup1, classResolver1, messageReporter1);
    }

    public AndroidManifestXmlHandler(String string, ClassMemberLookup classMemberLookup1, ClassResolver classResolver1, MessageReporter messageReporter1) {
        super(string, classMemberLookup1, classResolver1, messageReporter1);
    }
}
