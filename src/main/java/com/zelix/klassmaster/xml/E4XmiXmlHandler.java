package com.zelix.klassmaster.xml;

import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClassMemberLookup;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.log.MessageReporter;
import com.zelix.klassmaster.util.EnumerableMap;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ReadOnlyMultiMap;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

public class E4XmiXmlHandler extends GenericXmlFileHandler {
    public E4XmiXmlHandler(
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

    public E4XmiXmlHandler(String string, ClassMemberLookup classMemberLookup1, ClassResolver classResolver1, MessageReporter messageReporter1) {
        super(string, classMemberLookup1, classResolver1, messageReporter1);
    }

    @Override
    public void rewriteElement(Object object, Object object1) throws ZkmException, IOException {
        XmlElementNode xmlElementNode = (XmlElementNode) object;
        boolean predicateFlag = XmlConfigFileHandler.getPredicateFlag();
        xmlElementNode.getTagName();
        boolean bl = predicateFlag;
        XmlElementNode xmlElementNode1 = this.getOpenElement(0);
        if (xmlElementNode1 != null) {
            xmlElementNode1.getTagName();
        }

        Enumeration enumeration = xmlElementNode.getAttributeNames();

        while (enumeration.hasMoreElements()) {
            String string = (String) enumeration.nextElement();
            ObservableHolder observableHolder = xmlElementNode.getAttributeValue(string);
            label47:
            if (observableHolder != null) {
                String string1 = (String) observableHolder.getValue();
                String string10;
                String string11;
                if (string1.startsWith("bundleclass://")) {
                    int ba = string1.lastIndexOf("/");
                    if (ba > -1 && ba < string1.length() - 1) {
                        String string2 = string1.substring(ba + 1);
                        ProgramClass programClass1 = this.findProgramClass(string2);
                        if (programClass1 != null && programClass1.isRenamed()) {
                            String string3 = string1.substring(0, ba + 1);
                            String string4 = string3 + programClass1.getDottedClassName();
                            observableHolder.setValue(string4);
                        }
                    }

                    if (!bl) {
                        break label47;
                    }

                    string10 = string1;
                    string11 = "platform:/plugin/";
                } else {
                    string10 = string1;
                    string11 = "platform:/plugin/";
                }

                if (string10.startsWith(string11)) {
                    int bb = string1.indexOf("/", "platform:/plugin/".length());
                    if (bb > -1 && bb < string1.length() - 1) {
                        string1.substring(0, bb + 1);
                        String string8 = string1.substring(bb + 1);
                        int bc = string8.lastIndexOf("/");
                        if (bc > -1) {
                            String string9 = string8.substring(0, bc);
                            String string5 = string8.substring(bc);
                            String string6 = this.renamePackagePath(string9);
                            if (!string9.equals(string6)) {
                                String string7 = string1.substring(0, bb + 1) + string6 + string5;
                                observableHolder.setValue(string7);
                            }
                        }
                    }
                }
            }

            if (bl) {
                break;
            }
        }

        ((List) object1).add(xmlElementNode);
    }

    @Override
    public void analyzeElement(XmlElementNode xmlElementNode, Map map1, Object object, Object object1, Object object2) throws ZkmException, IOException {
        String string = xmlElementNode.getTagName();
        XmlElementNode xmlElementNode1 = this.getOpenElement(0);
        if (xmlElementNode1 != null) {
            xmlElementNode1.getTagName();
        }

        Enumeration enumeration = xmlElementNode.getAttributeNames();

        while (enumeration.hasMoreElements()) {
            String string1 = (String) enumeration.nextElement();
            ObservableHolder observableHolder = xmlElementNode.getAttributeValue(string1);
            if (observableHolder != null) {
                String string2 = (String) observableHolder.getValue();
                if (string2.startsWith("bundleclass://")) {
                    int ba = string2.lastIndexOf("/");
                    if (ba > -1 && ba < string2.length()) {
                        String string3 = string2.substring(ba + 1);
                        ProgramClass programClass1 = this.findProgramClass(string3);
                        if (programClass1 != null) {
                            String string4 = "reference in '"
                                    + super.resourceName
                                    + "' by '"
                                    + string
                                    + "' tag '"
                                    + string1
                                    + "' attribute with value '"
                                    + string2
                                    + "'";
                            map1.put(programClass1, string4);
                        }
                    }
                }
            }
        }
    }
}
