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
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

public class IbatisXmlHandler extends BeanPropertyXmlHandler {
    public Map typeAliases = ZkmUtils.createHashMap();

    @Override
    public void analyzeElement(XmlElementNode xmlElementNode, Map map1, Object object, Object object1, Object object2) throws ZkmException, IOException {
        Map map2 = (Map) object;
        TwoKeyMap twoKeyMap = (TwoKeyMap) object2;
        Map map3 = (Map) object1;
        String string = xmlElementNode.getTagName();
        XmlElementNode xmlElementNode1 = this.getOpenElement(0);
        String string1 = null;
        if (xmlElementNode1 != null) {
            string1 = xmlElementNode1.getTagName();
        }

        if (string.equalsIgnoreCase("typeAlias")) {
            ObservableHolder observableHolder = xmlElementNode.getAttributeValue("alias");
            if (observableHolder != null) {
                String string2 = (String) observableHolder.getValue();
                if (string2 != null && string2.length() > 0) {
                    ObservableHolder observableHolder1 = xmlElementNode.getAttributeValue("type");
                    if (observableHolder1 != null) {
                        String string3 = (String) observableHolder1.getValue();
                        if (string3 != null && string3.length() > 0) {
                            this.typeAliases.put(string2.trim(), string3.trim());
                        }
                    }
                }
            }
        } else if (string.equalsIgnoreCase("property") && string1 != null && string1.equalsIgnoreCase("result-map")
                || string.equalsIgnoreCase("result") && string1 != null && string1.equalsIgnoreCase("resultMap")) {
            String string7 = "reference in '" + super.resourceName + "' by '" + string + "' tag";
            String string8 = null;
            ObservableHolder observableHolder3 = null;
            if (xmlElementNode1 != null) {
                observableHolder3 = xmlElementNode1.getAttributeValue("class");
            }

            if (observableHolder3 != null) {
                String string9 = (String) observableHolder3.getValue();
                string9 = (String) ZkmUtils.mapOrSelf(string9, this.typeAliases);
                string8 = this.addClassReference(string9, map1, string7 + " '" + "class" + "' attribute");
            }

            if (string8 != null) {
                Enumeration enumeration = xmlElementNode.getAttributeNames();

                while (enumeration.hasMoreElements()) {
                    String string4 = (String) enumeration.nextElement();
                    String string10 = (String) xmlElementNode.getAttributeValue(string4).getValue();
                    String string5 = string7 + " '" + string4 + "' attribute";
                    if (string4.equalsIgnoreCase("name") && string.equalsIgnoreCase("property")
                            || string4.equalsIgnoreCase("property") && string.equalsIgnoreCase("result")) {
                        ObservableHolder observableHolder2 = xmlElementNode.getAttributeValue(string4);
                        if (observableHolder2 != null) {
                            String string6 = (String) observableHolder2.getValue();
                            ProgramClass programClass1 = this.findProgramClass(string8);
                            if (programClass1 != null) {
                                this.addPropertyAccessorReferences(programClass1, string6, map3, twoKeyMap, string5);
                            }
                        }
                    }
                }
            }
        } else {
            super.analyzeElement(xmlElementNode, map1, map2, map3, twoKeyMap);
        }
    }

    public IbatisXmlHandler(String string, ClassMemberLookup classMemberLookup1, ClassResolver classResolver1, MessageReporter messageReporter1) {
        super(string, classMemberLookup1, classResolver1, messageReporter1);
    }

    public IbatisXmlHandler(
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

    @Override
    public void rewriteElement(Object object, Object object1) throws ZkmException, IOException {
        XmlElementNode xmlElementNode = (XmlElementNode) object;
        boolean predicateFlag = XmlConfigFileHandler.getPredicateFlag();
        String string = xmlElementNode.getTagName();
        boolean bl = predicateFlag;
        String string1 = null;
        XmlElementNode xmlElementNode1 = this.getOpenElement(0);
        if (xmlElementNode1 != null) {
            string1 = xmlElementNode1.getTagName();
        }

        label107:
        {
            if (string.equalsIgnoreCase("typeAlias")) {
                ObservableHolder observableHolder = xmlElementNode.getAttributeValue("alias");
                if (observableHolder != null) {
                    String string2 = (String) observableHolder.getValue();
                    if (string2 != null && string2.length() > 0) {
                        ObservableHolder observableHolder1 = xmlElementNode.getAttributeValue("type");
                        if (observableHolder1 != null) {
                            String string3 = (String) observableHolder1.getValue();
                            if (string3 != null && string3.length() > 0) {
                                this.typeAliases.put(string2.trim(), string3.trim());
                                observableHolder1.setValue(this.getRenamedClassName(string3));
                            }
                        }
                    }
                }

                if (!bl) {
                    break label107;
                }
            }

            if (string.equalsIgnoreCase("property") && string1 != null && string1.equalsIgnoreCase("result-map")
                    || string.equalsIgnoreCase("result") && string1 != null && string1.equalsIgnoreCase("resultMap")) {
                ObservableHolder observableHolder3 = null;
                String string7 = null;
                String string9 = null;
                if (xmlElementNode1 != null) {
                    observableHolder3 = xmlElementNode1.getAttributeValue("class");
                }

                if (observableHolder3 != null) {
                    String string8 = (String) observableHolder3.getValue();
                    string7 = (String) ZkmUtils.mapOrSelf(string8, this.typeAliases);
                    string9 = this.getRenamedClassName(string7);
                }

                if (string9 != null) {
                    Enumeration enumeration1 = xmlElementNode.getAttributeNames();

                    while (enumeration1.hasMoreElements()) {
                        String string4 = (String) enumeration1.nextElement();
                        String string10 = (String) xmlElementNode.getAttributeValue(string4).getValue();
                        if (string4.equalsIgnoreCase("name") && string.equalsIgnoreCase("property")
                                || string4.equalsIgnoreCase("property") && string.equalsIgnoreCase("result")) {
                            ObservableHolder observableHolder2 = xmlElementNode.getAttributeValue(string4);
                            if (observableHolder2 != null) {
                                String string5 = (String) observableHolder2.getValue();
                                this.resolveRenamedPropertyName(string9, string7, string5, observableHolder2);
                            }
                        }

                        if (bl) {
                            break;
                        }
                    }
                }
            } else {
                Enumeration enumeration = xmlElementNode.getAttributeNames();

                while (enumeration.hasMoreElements()) {
                    String string6 = (String) enumeration.nextElement();
                    ObservableHolder observableHolder4 = xmlElementNode.getAttributeValue(string6);
                    observableHolder4.setValue(this.updateReferenceValue((String) observableHolder4.getValue(), string6, false));
                }
            }
        }

        ((List) object1).add(xmlElementNode);
    }
}
