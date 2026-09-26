package com.zelix.klassmaster.xml;

import com.zelix.klassmaster.classfile.AbstractFieldInfo;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClassMemberLookup;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.log.MessageReporter;
import com.zelix.klassmaster.util.EnumerableMap;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ReadOnlyMultiMap;
import com.zelix.klassmaster.util.TwoKeyMap;
import com.zelix.klassmaster.util.ZkmAssert;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HibernateMappingHandler extends BeanPropertyXmlHandler {
    public static Set builtInGeneratorNames;
    public String defaultPackage;
    public String currentClassName;
    public ProgramClass currentClass;
    public boolean useFieldAccess;

    public ProgramClass updateClassAttribute(XmlElementNode xmlElementNode, String string) throws ZkmException, IOException {
        ProgramClass programClass1 = null;
        ObservableHolder observableHolder = xmlElementNode.getAttributeValue(string);
        if (observableHolder != null) {
            String string1 = (String) observableHolder.getValue();
            programClass1 = this.findProgramClass(string1);
            boolean bl = false;
            if (programClass1 == null && this.defaultPackage != null) {
                programClass1 = this.findClassInPackage(string1, this.defaultPackage);
                bl = programClass1 != null;
            }

            if (programClass1 != null) {
                String string2;
                if (bl) {
                    string2 = programClass1.getSimpleName();
                } else {
                    string2 = programClass1.getDottedClassName();
                }

                observableHolder.setValue(string2);
            }
        }

        return programClass1;
    }

    public HibernateMappingHandler(String string, ClassMemberLookup classMemberLookup1, ClassResolver classResolver1, MessageReporter messageReporter1) {
        super(string, classMemberLookup1, classResolver1, messageReporter1);
    }

    public ProgramClass addClassAttributeReference(XmlElementNode xmlElementNode, String string, String string1, String string2, Map map1) throws ZkmException, IOException {
        Map map2 = map1;
        String string3 = string2;
        return this.resolveClassAttribute(xmlElementNode, string, string1, (ObservableHolder) null, string3, map2);
    }

    public ProgramClass findClassInPackage(String string, String string1) {
        ProgramClass programClass1 = this.findProgramClass(string);
        if (programClass1 == null && string1 != null) {
            programClass1 = this.findProgramClass(string1 + "." + string);
        }

        return programClass1;
    }

    public ProgramClass addClassAttributeReference(XmlElementNode xmlElementNode, String string, String string1, Map map1) throws ZkmException, IOException {
        if (xmlElementNode.getAttributeValue(string1) != null) {
            String string2 = xmlElementNode.getTagName();
            String string3 = "reference in '" + super.resourceName + "' by '" + string2 + "' tag '" + string1 + "' attribute";
            return this.addClassAttributeReference(xmlElementNode, string, string1, string3, map1);
        } else {
            return null;
        }
    }

    public ProgramClass resolveClassAttribute(
            XmlElementNode xmlElementNode, String string, String string1, ObservableHolder observableHolder, Object object, Map map1
    ) throws ZkmException, IOException {
        ObservableHolder observableHolder1 = xmlElementNode.getAttributeValue(string1);
        ProgramClass programClass1 = null;
        if (observableHolder1 != null) {
            String string2 = (String) observableHolder1.getValue();
            if (observableHolder != null) {
                observableHolder.setValue(string2);
            }

            programClass1 = this.findProgramClass(string2);
            if (programClass1 == null && string != null) {
                programClass1 = this.findClassInPackage(string2, string);
            }

            if (programClass1 != null) {
                map1.put(programClass1, object);
            }
        }

        return programClass1;
    }

    @Override
    public void analyzeElement(XmlElementNode xmlElementNode, Map map1, Object object, Object object1, Object object2) throws ZkmException, IOException {
        Map map2 = (Map) object1;
        TwoKeyMap twoKeyMap = (TwoKeyMap) object2;
        Map map3 = (Map) object;
        String string = xmlElementNode.getTagName();
        if (string.equalsIgnoreCase("hibernate-mapping")) {
            ObservableHolder observableHolder = xmlElementNode.getAttributeValue("package");
            XmlElementNode xmlElementNode3;
            String string22;
            if (observableHolder != null) {
                String string1 = (String) observableHolder.getValue();
                if (string1 != null) {
                    if (string1.length() > 0) {
                        this.defaultPackage = string1;
                        xmlElementNode3 = xmlElementNode;
                        long bd = 24856730383790L;
                        string22 = "default-access";
                    } else {
                        xmlElementNode3 = xmlElementNode;
                        long ba = 24856730383790L;
                        string22 = "default-access";
                    }
                } else {
                    xmlElementNode3 = xmlElementNode;
                    long bb = 24856730383790L;
                    string22 = "default-access";
                }
            } else {
                xmlElementNode3 = xmlElementNode;
                long bc = 24856730383790L;
                string22 = "default-access";
            }

            String string5 = string22;
            ObservableHolder observableHolder3 = xmlElementNode3.getAttributeValue(string5);
            if (observableHolder3 != null) {
                String string2 = (String) observableHolder3.getValue();
                if (string2 != null) {
                    this.useFieldAccess = string2.equals("field");
                }
            }
        } else if (string.equalsIgnoreCase("class")) {
            String string6 = "reference in '" + super.resourceName + "' by '" + string + "' tag '" + "name" + "' attribute";
            ObservableHolder observableHolder4 = new ObservableHolder();
            ProgramClass programClass3 = this.resolveClassAttribute(xmlElementNode, this.defaultPackage, "name", observableHolder4, string6, map1);
            if (!observableHolder4.isValueNull()) {
                this.currentClassName = (String) observableHolder4.getValue();
            }

            if (programClass3 != null) {
                this.currentClass = programClass3;
            }

            observableHolder4.setValue(null);
            String string3 = "reference in '" + super.resourceName + "' by '" + string + "' tag '" + "persister" + "' attribute";
            this.resolveClassAttribute(xmlElementNode, this.defaultPackage, "persister", observableHolder4, string3, map1);
            String string4 = "reference in '" + super.resourceName + "' by '" + string + "' tag '" + "entity-name" + "' attribute";
            this.resolveClassAttribute(xmlElementNode, this.defaultPackage, "entity-name", observableHolder4, string4, map1);
        } else {
            HibernateMappingHandler hibernateMappingHandler1;
            XmlElementNode xmlElementNode2;
            String string21;
            if (!string.equalsIgnoreCase("id")) {
                if (!string.equalsIgnoreCase("property")) {
                    if (!string.equalsIgnoreCase("many-to-one")) {
                        if (!string.equalsIgnoreCase("one-to-one")) {
                            if (!string.equalsIgnoreCase("composite-id")) {
                                if (string.equalsIgnoreCase("one-to-many") || string.equalsIgnoreCase("map-key-many-to-many")) {
                                    String string8 = "reference in '" + super.resourceName + "' by '" + string + "' tag '" + "class" + "' attribute";
                                    this.addClassAttributeReference(xmlElementNode, this.defaultPackage, "class", string8, map1);
                                    String string11 = "reference in '" + super.resourceName + "' by '" + string + "' tag '" + "entity-name" + "' attribute";
                                    this.addClassAttributeReference(xmlElementNode, this.defaultPackage, "entity-name", string11, map1);
                                    return;
                                }

                                if (string.equalsIgnoreCase("many-to-many")) {
                                    ObservableHolder observableHolder1 = xmlElementNode.getAttributeValue("entity-name");
                                    if (observableHolder1 != null && !observableHolder1.isValueNull()) {
                                        String string9 = "reference in '" + super.resourceName + "' by '" + string + "' tag '" + "entity-name" + "' attribute";
                                        this.addClassAttributeReference(xmlElementNode, this.defaultPackage, "entity-name", string9, map1);
                                        return;
                                    }

                                    return;
                                } else {
                                    if (string.equalsIgnoreCase("generator")) {
                                        String string7 = "reference in '" + super.resourceName + "' by '" + string + "' tag '" + "class" + "' attribute";
                                        ObservableHolder observableHolder5 = xmlElementNode.getAttributeValue("class");
                                        if (observableHolder5 != null) {
                                            String string16 = (String) observableHolder5.getValue();
                                            if (!builtInGeneratorNames.contains(string16)) {
                                                this.addClassAttributeReference(xmlElementNode, this.defaultPackage, "class", string7, map1);
                                            }

                                            return;
                                        }
                                    } else {
                                        if (!string.equalsIgnoreCase("set")) {
                                            if (!string.equalsIgnoreCase("list")) {
                                                if (!string.equalsIgnoreCase("map")) {
                                                    if (!string.equalsIgnoreCase("bag")) {
                                                        if (!string.equalsIgnoreCase("array")) {
                                                            if (!string.equalsIgnoreCase("primitive-array")) {
                                                                if (!string.equalsIgnoreCase("subclass")
                                                                        && !string.equalsIgnoreCase("joined-subclass")
                                                                        && !string.equalsIgnoreCase("union-subclass")) {
                                                                    super.analyzeElement(xmlElementNode, map1, map3, map2, twoKeyMap);
                                                                } else {
                                                                    this.addClassAttributeReference(xmlElementNode, this.defaultPackage, "name", map1);
                                                                    this.addClassAttributeReference(xmlElementNode, this.defaultPackage, "extends", map1);
                                                                    this.addClassAttributeReference(xmlElementNode, this.defaultPackage, "entity-name", map1);
                                                                    this.addClassAttributeReference(xmlElementNode, this.defaultPackage, "persister", map1);
                                                                }

                                                                return;
                                                            }

                                                            hibernateMappingHandler1 = this;
                                                            xmlElementNode2 = xmlElementNode;
                                                            string21 = "name";
                                                        } else {
                                                            hibernateMappingHandler1 = this;
                                                            xmlElementNode2 = xmlElementNode;
                                                            string21 = "name";
                                                        }
                                                    } else {
                                                        hibernateMappingHandler1 = this;
                                                        xmlElementNode2 = xmlElementNode;
                                                        string21 = "name";
                                                    }
                                                } else {
                                                    hibernateMappingHandler1 = this;
                                                    xmlElementNode2 = xmlElementNode;
                                                    string21 = "name";
                                                }
                                            } else {
                                                hibernateMappingHandler1 = this;
                                                xmlElementNode2 = xmlElementNode;
                                                string21 = "name";
                                            }
                                        } else {
                                            hibernateMappingHandler1 = this;
                                            xmlElementNode2 = xmlElementNode;
                                            string21 = "name";
                                        }

                                        hibernateMappingHandler1.addPropertyReference(xmlElementNode2, string21, map3, map2, twoKeyMap);
                                        ObservableHolder observableHolder2 = xmlElementNode.getAttributeValue("sort");
                                        if (observableHolder2 != null
                                                && !observableHolder2.isValueNull()
                                                && !((String) observableHolder2.getValue()).equals("unsorted")
                                                && !((String) observableHolder2.getValue()).equals("natural")) {
                                            String string10 = "reference in '" + super.resourceName + "' by '" + string + "' tag '" + "sort" + "' attribute";
                                            this.addClassAttributeReference(xmlElementNode, this.defaultPackage, "sort", string10, map1);
                                            return;
                                        }
                                    }

                                    return;
                                }
                            }

                            hibernateMappingHandler1 = this;
                            xmlElementNode2 = xmlElementNode;
                            string21 = "name";
                        } else {
                            hibernateMappingHandler1 = this;
                            xmlElementNode2 = xmlElementNode;
                            string21 = "name";
                        }
                    } else {
                        hibernateMappingHandler1 = this;
                        xmlElementNode2 = xmlElementNode;
                        string21 = "name";
                    }
                } else {
                    hibernateMappingHandler1 = this;
                    xmlElementNode2 = xmlElementNode;
                    string21 = "name";
                }
            } else {
                hibernateMappingHandler1 = this;
                xmlElementNode2 = xmlElementNode;
                string21 = "name";
            }

            hibernateMappingHandler1.addPropertyReference(xmlElementNode2, string21, map3, map2, twoKeyMap);
            ArrayList arrayList = new ArrayList();
            if (xmlElementNode.getAttributeValue("class") != null) {
                String string12 = "reference in '" + super.resourceName + "' by '" + string + "' tag '" + "class" + "' attribute";
                ProgramClass programClass4 = this.addClassAttributeReference(xmlElementNode, this.defaultPackage, "class", string12, map1);
                if (programClass4 != null) {
                    arrayList.add(programClass4);
                }
            }

            if ((string.equalsIgnoreCase("many-to-one") || string.equalsIgnoreCase("one-to-one")) && xmlElementNode.getAttributeValue("entity-name") != null) {
                String string13 = "reference in '" + super.resourceName + "' by '" + string + "' tag '" + "entity-name" + "' attribute";
                ProgramClass programClass5 = this.addClassAttributeReference(xmlElementNode, this.defaultPackage, "entity-name", string13, map1);
                if (programClass5 != null) {
                    arrayList.add(programClass5);
                }
            }

            XmlElementNode xmlElementNode1;
            String string20;
            if (!string.equalsIgnoreCase("property")) {
                if (xmlElementNode.getAttributeValue("access") != null) {
                    String string14 = "reference in '" + super.resourceName + "' by '" + string + "' tag '" + "access" + "' attribute";
                    this.addClassAttributeReference(xmlElementNode, this.defaultPackage, "access", string14, map1);
                    xmlElementNode1 = xmlElementNode;
                    string20 = "type";
                } else {
                    xmlElementNode1 = xmlElementNode;
                    string20 = "type";
                }
            } else {
                xmlElementNode1 = xmlElementNode;
                string20 = "type";
            }

            if (xmlElementNode1.getAttributeValue(string20) != null) {
                String string15 = "reference in '" + super.resourceName + "' by '" + string + "' tag '" + "type" + "' attribute";
                this.addClassAttributeReference(xmlElementNode, this.defaultPackage, "type", string15, map1);
            }

            if ((string.equalsIgnoreCase("one-to-one") || string.equalsIgnoreCase("many-to-one")) && xmlElementNode.getAttributeValue("property-ref") != null) {
                ObservableHolder observableHolder6 = xmlElementNode.getAttributeValue("property-ref");
                if (observableHolder6 != null && !observableHolder6.isValueNull()) {
                    String string17 = (String) observableHolder6.getValue();
                    String string18 = "reference in '" + super.resourceName + "' by '" + string + "' tag '" + "property-ref" + "' attribute";
                    if (arrayList.size() > 0) {
                        if (!this.useFieldAccess) {
                            Iterator iterator = arrayList.iterator();

                            while (iterator.hasNext()) {
                                ProgramClass programClass1 = (ProgramClass) iterator.next();
                                this.addPropertyAccessorReferences(programClass1, string17, map2, twoKeyMap, string18);
                            }
                        } else {
                            Iterator iterator1 = arrayList.iterator();

                            while (iterator1.hasNext()) {
                                ProgramClass programClass6 = (ProgramClass) iterator1.next();
                                this.addFieldReference(programClass6, string17, map3, string18);
                            }
                        }
                    } else if (xmlElementNode.getAttributeValue("name") != null && this.currentClass != null) {
                        String string19 = (String) xmlElementNode.getAttributeValue("name").getValue();
                        AbstractFieldInfo[] abstractFieldInfos = this.currentClass.findFieldsByName(string19);
                        if (abstractFieldInfos.length == 1) {
                            ProgramClass programClass2 = this.findProgramClass(abstractFieldInfos[0].getTypeName());
                            if (programClass2 != null) {
                                if (!this.useFieldAccess) {
                                    this.addPropertyAccessorReferences(programClass2, string17, map2, twoKeyMap, string18);
                                } else {
                                    this.addFieldReference(programClass2, string17, map3, string18);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void addPropertyReference(XmlElementNode xmlElementNode, String string, Map map1, Map map2, TwoKeyMap twoKeyMap) throws ZkmException, IOException {
        ObservableHolder observableHolder = xmlElementNode.getAttributeValue(string);
        if (observableHolder != null) {
            String string1 = (String) observableHolder.getValue();
            String string2 = xmlElementNode.getTagName();
            if (this.currentClass != null) {
                String string3 = "reference in '" + super.resourceName + "' by '" + string2 + "' tag '" + string + "' attribute";
                if (!this.useFieldAccess) {
                    this.addPropertyAccessorReferences(this.currentClass, string1, map2, twoKeyMap, string3);
                } else {
                    this.addFieldReference(this.currentClass, string1, map1, string3);
                }
            }
        }
    }

    public void updatePropertyName(String string, String string1, ObservableHolder observableHolder, ProgramClass programClass1) throws ZkmException, IOException {
        String string2 = programClass1.getDottedClassName();
        if (!this.useFieldAccess) {
            String string3 = null;
            String string4 = XmlConfigFileHandler.buildAccessorName("set", string1);
            String string5 = this.lookupRenamedMethodName(string2, string4, BeanPropertyXmlHandler.SETTER_DESCRIPTOR, 1);
            if (string5 == null && string.equalsIgnoreCase("id")) {
                string5 = this.lookupRenamedMethodName(string2, string4, BeanPropertyXmlHandler.SETTER_DESCRIPTOR, 2);
            }

            String string14;
            if (string5 != null) {
                String string6 = this.getPropertyNameFromAccessor("set", string5, string1);
                if (string6 == null) {
                    super.messageReporter
                            .reportError(
                                    "ERROR:",
                                    "Could not retain bean format for property '"
                                            + string1
                                            + "' in  XML file '"
                                            + super.resourceName
                                            + "' : '"
                                            + string4
                                            + "' '"
                                            + string5
                                            + "' : '"
                                            + programClass1.getDisplayLocationName()
                                            + "' (4)"
                            );
                    string14 = "get";
                } else {
                    string3 = string6;
                    observableHolder.setValue(string6);
                    string14 = "get";
                }
            } else {
                string14 = "get";
            }

            String string12 = XmlConfigFileHandler.buildAccessorName(string14, string1);
            String string7 = this.lookupRenamedMethodName(string2, string12, BeanPropertyXmlHandler.GETTER_DESCRIPTOR, 1);
            if (string7 != null) {
                String string8 = this.getPropertyNameFromAccessor("get", string7, string1);
                if (string8 == null) {
                    super.messageReporter
                            .reportError(
                                    "ERROR:",
                                    "Could not retain bean format for property '"
                                            + string1
                                            + "' in  XML file '"
                                            + super.resourceName
                                            + "' : '"
                                            + string12
                                            + "' '"
                                            + string7
                                            + "' : '"
                                            + programClass1.getDisplayLocationName()
                                            + "' (5)"
                            );
                } else {
                    if (string3 == null) {
                        string3 = string8;
                    } else {
                        boolean bl = string3.equals(string8);
                        String[] strings = new String[]{
                                "Could not retain bean format for property '"
                                        + string1
                                        + "' in  XML file '"
                                        + super.resourceName
                                        + "' : Clashing new values '"
                                        + string3
                                        + "' '"
                                        + string8
                                        + "' : '"
                                        + programClass1.getDisplayLocationName()
                                        + "' (A)"
                        };
                        ZkmAssert.assertTrue(bl, strings);
                    }

                    observableHolder.setValue(string8);
                }
            }

            String string13 = XmlConfigFileHandler.buildAccessorName("is", string1);
            String string9 = this.lookupRenamedMethodName(
                    string2,
                    string13,
                    HiddenOptionFlags.USE_BOOLEAN_IS_GETTER_TYPE ? BeanPropertyXmlHandler.BOOLEAN_GETTER_DESCRIPTOR : BeanPropertyXmlHandler.GETTER_DESCRIPTOR,
                    1
            );
            if (string9 != null) {
                String string10 = this.getPropertyNameFromAccessor("is", string9, string1);
                if (string10 == null) {
                    super.messageReporter
                            .reportError(
                                    "ERROR:",
                                    "Could not retain bean format for property '"
                                            + string1
                                            + "' in  XML file '"
                                            + super.resourceName
                                            + "' : '"
                                            + string13
                                            + "' '"
                                            + string9
                                            + "' : '"
                                            + programClass1.getDisplayLocationName()
                                            + "' (6)"
                            );
                } else {
                    if (string3 != null) {
                        boolean bl1 = string3.equals(string10);
                        String[] strings1 = new String[]{
                                "Could not retain bean format for property '"
                                        + string1
                                        + "' in  XML file '"
                                        + super.resourceName
                                        + "' : Clashing new values '"
                                        + string3
                                        + "' '"
                                        + string10
                                        + "' : '"
                                        + programClass1.getDisplayLocationName()
                                        + "' (B)"
                        };
                        ZkmAssert.assertTrue(bl1, strings1);
                    }

                    observableHolder.setValue(string10);
                }
            }
        } else {
            String string11 = this.getRenamedFieldName(string2, string1);
            observableHolder.setValue(string11);
        }
    }

    @Override
    public void rewriteElement(Object object, Object object1) throws ZkmException, IOException {
        XmlElementNode xmlElementNode = (XmlElementNode) object;
        boolean predicateFlag = XmlConfigFileHandler.getPredicateFlag();
        String string = xmlElementNode.getTagName();
        boolean bl = predicateFlag;
        if (string.equalsIgnoreCase("hibernate-mapping")) {
            ObservableHolder observableHolder = xmlElementNode.getAttributeValue("package");
            XmlElementNode xmlElementNode1;
            String string13;
            if (observableHolder != null) {
                String string1 = (String) observableHolder.getValue();
                if (string1 != null) {
                    if (string1.length() > 0) {
                        this.defaultPackage = string1;
                        observableHolder.setValue(this.renameDirectoryPath(string1));
                        xmlElementNode1 = xmlElementNode;
                        long bf = 24856730383790L;
                        string13 = "default-access";
                    } else {
                        xmlElementNode1 = xmlElementNode;
                        long ba = 24856730383790L;
                        string13 = "default-access";
                    }
                } else {
                    xmlElementNode1 = xmlElementNode;
                    long bb = 24856730383790L;
                    string13 = "default-access";
                }
            } else {
                xmlElementNode1 = xmlElementNode;
                long bc = 24856730383790L;
                string13 = "default-access";
            }

            String string5 = string13;
            ObservableHolder observableHolder3 = xmlElementNode1.getAttributeValue(string5);
            if (observableHolder3 != null) {
                String string2 = (String) observableHolder3.getValue();
                if (string2 != null) {
                    this.useFieldAccess = string2.equals("field");
                }
            }
        } else if (string.equalsIgnoreCase("class")) {
            ProgramClass programClass2 = this.updateClassAttribute(xmlElementNode, "name");
            HibernateMappingHandler hibernateMappingHandler1;
            XmlElementNode xmlElementNode3;
            String string9;
            if (programClass2 != null) {
                this.currentClass = programClass2;
                hibernateMappingHandler1 = this;
                xmlElementNode3 = xmlElementNode;
                string9 = "persister";
            } else {
                hibernateMappingHandler1 = this;
                xmlElementNode3 = xmlElementNode;
                string9 = "persister";
            }

            hibernateMappingHandler1.updateClassAttribute(xmlElementNode3, string9);
            this.updateClassAttribute(xmlElementNode, "entity-name");
        } else {
            label162:
            {
                label170:
                {
                    HibernateMappingHandler hibernateMappingHandler2;
                    XmlElementNode xmlElementNode4;
                    String string10;
                    if (!string.equalsIgnoreCase("id")) {
                        if (!string.equalsIgnoreCase("property")) {
                            if (!string.equalsIgnoreCase("many-to-one")) {
                                if (!string.equalsIgnoreCase("one-to-one")) {
                                    if (!string.equalsIgnoreCase("composite-id")) {
                                        break label170;
                                    }

                                    hibernateMappingHandler2 = this;
                                    xmlElementNode4 = xmlElementNode;
                                    string10 = "entity-name";
                                } else {
                                    hibernateMappingHandler2 = this;
                                    xmlElementNode4 = xmlElementNode;
                                    string10 = "entity-name";
                                }
                            } else {
                                hibernateMappingHandler2 = this;
                                xmlElementNode4 = xmlElementNode;
                                string10 = "entity-name";
                            }
                        } else {
                            hibernateMappingHandler2 = this;
                            xmlElementNode4 = xmlElementNode;
                            string10 = "entity-name";
                        }
                    } else {
                        hibernateMappingHandler2 = this;
                        xmlElementNode4 = xmlElementNode;
                        string10 = "entity-name";
                    }

                    hibernateMappingHandler2.updateClassAttribute(xmlElementNode4, string10);
                    ObservableHolder observableHolder1 = xmlElementNode.getAttributeValue("name");
                    if (observableHolder1 != null) {
                        String string7 = (String) observableHolder1.getValue();
                        if (this.currentClass != null) {
                            this.updatePropertyName(string, string7, observableHolder1, this.currentClass);
                        }
                    }

                    ProgramClass programClass3 = this.updateClassAttribute(xmlElementNode, "class");
                    this.updateClassAttribute(xmlElementNode, "type");
                    this.updateClassAttribute(xmlElementNode, "access");
                    if ((string.equalsIgnoreCase("one-to-one") || string.equalsIgnoreCase("many-to-one"))
                            && xmlElementNode.getAttributeValue("property-ref") != null) {
                        ObservableHolder observableHolder4 = xmlElementNode.getAttributeValue("property-ref");
                        if (observableHolder4 != null && !observableHolder4.isValueNull()) {
                            String string3 = (String) observableHolder4.getValue();
                            if (programClass3 != null) {
                                this.updatePropertyName(string, string3, observableHolder4, programClass3);
                            } else if (xmlElementNode.getAttributeValue("name") != null && this.currentClass != null) {
                                String string4 = this.getRenamedFieldName(this.currentClass.getClassName(), (String) xmlElementNode.getAttributeValue("name").getValue());
                                AbstractFieldInfo[] abstractFieldInfos = this.currentClass.findFieldsByName(string4);
                                if (abstractFieldInfos.length == 1) {
                                    ProgramClass programClass1 = this.findProgramClass(abstractFieldInfos[0].getTypeName());
                                    if (programClass1 != null) {
                                        this.updatePropertyName(string, string3, observableHolder4, programClass1);
                                    }
                                }
                            }
                        }
                    }

                    if (!bl) {
                        break label162;
                    }
                }

                if (string.equalsIgnoreCase("generator")) {
                    this.updateClassAttribute(xmlElementNode, "class");
                    this.updateClassAttribute(xmlElementNode, "entity-name");
                } else {
                    label171:
                    {
                        if (string.equalsIgnoreCase("one-to-many") || string.equalsIgnoreCase("map-key-many-to-many")) {
                            this.updateClassAttribute(xmlElementNode, "class");
                            this.updateClassAttribute(xmlElementNode, "entity-name");
                            if (!bl) {
                                break label171;
                            }
                        }

                        if (string.equalsIgnoreCase("many-to-many")) {
                            this.updateClassAttribute(xmlElementNode, "entity-name");
                            if (!bl) {
                                break label171;
                            }
                        }

                        if (string.equalsIgnoreCase("set")
                                || string.equalsIgnoreCase("list")
                                || string.equalsIgnoreCase("map")
                                || string.equalsIgnoreCase("bag")
                                || string.equalsIgnoreCase("array")
                                || string.equalsIgnoreCase("primitive-array")) {
                            XmlElementNode xmlElementNode2;
                            String string11;
                            if (this.currentClass != null) {
                                this.updatePropertyAttribute(xmlElementNode, this.currentClass);
                                xmlElementNode2 = xmlElementNode;
                                long bd = 24856730383790L;
                                string11 = "sort";
                            } else {
                                xmlElementNode2 = xmlElementNode;
                                long be = 24856730383790L;
                                string11 = "sort";
                            }

                            String string6 = string11;
                            ObservableHolder observableHolder2 = xmlElementNode2.getAttributeValue(string6);
                            if (observableHolder2 != null
                                    && !observableHolder2.isValueNull()
                                    && !((String) observableHolder2.getValue()).equals("unsorted")
                                    && !((String) observableHolder2.getValue()).equals("natural")) {
                                this.updateClassAttribute(xmlElementNode, "sort");
                            }

                            if (!bl) {
                                break label171;
                            }
                        }

                        label120:
                        {
                            HibernateMappingHandler hibernateMappingHandler3;
                            XmlElementNode xmlElementNode5;
                            String string12;
                            if (!string.equalsIgnoreCase("subclass")) {
                                if (!string.equalsIgnoreCase("joined-subclass")) {
                                    if (!string.equalsIgnoreCase("union-subclass")) {
                                        break label120;
                                    }

                                    hibernateMappingHandler3 = this;
                                    xmlElementNode5 = xmlElementNode;
                                    string12 = "name";
                                } else {
                                    hibernateMappingHandler3 = this;
                                    xmlElementNode5 = xmlElementNode;
                                    string12 = "name";
                                }
                            } else {
                                hibernateMappingHandler3 = this;
                                xmlElementNode5 = xmlElementNode;
                                string12 = "name";
                            }

                            hibernateMappingHandler3.updateClassAttribute(xmlElementNode5, string12);
                            this.updateClassAttribute(xmlElementNode, "extends");
                            this.updateClassAttribute(xmlElementNode, "entity-name");
                            this.updateClassAttribute(xmlElementNode, "persister");
                            if (!bl) {
                                break label171;
                            }
                        }

                        Enumeration enumeration = xmlElementNode.getAttributeNames();

                        while (enumeration.hasMoreElements()) {
                            String string8 = (String) enumeration.nextElement();
                            ObservableHolder observableHolder5 = xmlElementNode.getAttributeValue(string8);
                            observableHolder5.setValue(this.updateReferenceValue((String) observableHolder5.getValue(), string8, false));
                        }
                    }
                }
            }
        }

        ((List) object1).add(xmlElementNode);
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    public HibernateMappingHandler(
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

    public void updatePropertyAttribute(XmlElementNode xmlElementNode, ProgramClass programClass1) throws ZkmException, IOException {
        ObservableHolder observableHolder = xmlElementNode.getAttributeValue("name");
        if (observableHolder != null) {
            String string = xmlElementNode.getTagName();
            String string1 = (String) observableHolder.getValue();
            if (programClass1 != null) {
                this.updatePropertyName(string, string1, observableHolder, programClass1);
            }
        }
    }

    private static void staticInit() {
        builtInGeneratorNames = ZkmUtils.createHashSet();
        builtInGeneratorNames.add("increment");
        builtInGeneratorNames.add("identity");
        builtInGeneratorNames.add("sequence");
        builtInGeneratorNames.add("hilo");
        builtInGeneratorNames.add("seqhilo");
        builtInGeneratorNames.add("uuid");
        builtInGeneratorNames.add("guid");
        builtInGeneratorNames.add("native");
        builtInGeneratorNames.add("assigned");
        builtInGeneratorNames.add("select");
        builtInGeneratorNames.add("foreign");
        builtInGeneratorNames.add("sequence-identity");
    }
}
