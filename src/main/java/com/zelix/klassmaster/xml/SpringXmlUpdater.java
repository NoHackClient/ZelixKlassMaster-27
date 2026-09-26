package com.zelix.klassmaster.xml;

import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.classfile.hierarchy.ClassMemberLookup;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.log.MessageReporter;
import com.zelix.klassmaster.util.BooleanFlag;
import com.zelix.klassmaster.util.EnumerableMap;
import com.zelix.klassmaster.util.ObjectStack;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ReadOnlyMultiMap;
import com.zelix.klassmaster.util.TwoKeyMap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SpringXmlUpdater extends BeanPropertyXmlHandler {
    public final Map beanClasses = ZkmUtils.createHashMap();
    public final Map factoryMethodReferences = ZkmUtils.createHashMap();

    public SpringXmlUpdater(
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

    public SpringXmlUpdater(String string, ClassMemberLookup classMemberLookup1, ClassResolver classResolver1, MessageReporter messageReporter1) {
        super(string, classMemberLookup1, classResolver1, messageReporter1);
    }

    @Override
    public void beforeFlushOutput() throws ZkmException, IOException {
        boolean bl1 = XmlConfigFileHandler.alwaysTrue();
        Iterator iterator = this.factoryMethodReferences.entrySet().iterator();
        boolean bl = bl1;

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            String string = (String) entry.getKey();
            XmlMethodReference xmlMethodReference = (XmlMethodReference) entry.getValue();
            String string1 = XmlMethodReference.getBeanNameOf(xmlMethodReference);
            ObservableHolder observableHolder = XmlMethodReference.getMethodNameHolderOf(xmlMethodReference);
            String string2 = XmlMethodReference.getReferenceDescriptionOf(xmlMethodReference);
            ProgramClass programClass1 = (ProgramClass) this.beanClasses.get(string1);
            if (programClass1 != null) {
                ObservableHolder observableHolder1 = new ObservableHolder();
                BooleanFlag booleanFlag = new BooleanFlag();
                String string3 = this.resolveRenamedMethodName(programClass1.getDottedClassName(), string, 0, observableHolder1, booleanFlag);
                if (string3 != null) {
                    if (!string.equals(string3)) {
                        observableHolder.setValue(string3);
                    }
                } else if (booleanFlag.getValue()) {
                    super.messageReporter
                            .reportError(
                                    "ERROR:",
                                    "Could not update method name \""
                                            + string
                                            + "\" in  XML file \""
                                            + super.resourceName
                                            + "\" : \""
                                            + string2
                                            + "\" : \""
                                            + (String) observableHolder1.getValue()
                                            + "\""
                            );
                }
            }

            if (!bl) {
                break;
            }
        }
    }

    @Override
    public void finishAnalysis(Object object, Object object1) throws ZkmException, IOException {
        Map map1 = (Map) object;
        TwoKeyMap twoKeyMap = (TwoKeyMap) object1;
        Iterator iterator = this.factoryMethodReferences.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            String string = (String) entry.getKey();
            XmlMethodReference xmlMethodReference = (XmlMethodReference) entry.getValue();
            String string1 = XmlMethodReference.getBeanNameOf(xmlMethodReference);
            String string2 = XmlMethodReference.getReferenceDescriptionOf(xmlMethodReference);
            ProgramClass programClass1 = (ProgramClass) this.beanClasses.get(string1);
            if (programClass1 != null) {
                this.addHierarchyMethodReferences(
                        programClass1, string, (String) null, null, BeanPropertyXmlHandler.ANY_METHOD_DESCRIPTOR, 0, map1, twoKeyMap, string2
                );
            }
        }
    }

    @Override
    public void rewriteElement(Object object, Object object1) throws ZkmException, IOException {
        com.zelix.klassmaster.xml.XmlElementNode xmlElementNode2 = null;
        com.zelix.klassmaster.xml.XmlElementNode xmlElementNode1 = null;
        XmlElementNode xmlElementNode = (XmlElementNode) object;
        boolean bl1 = XmlConfigFileHandler.alwaysTrue();
        String string = xmlElementNode.getTagName();
        String string1 = this.getOpenElementName(1);
        boolean bl = bl1;
        ProgramClass programClass1 = null;
        String string2 = null;
        if (string.equalsIgnoreCase("bean")) {
            ObservableHolder observableHolder = xmlElementNode.getAttributeValue("class");
            if (observableHolder != null) {
                String string3 = (String) observableHolder.getValue();
                string2 = this.getRenamedClassName(string3);
                if (!string3.equals(string2)) {
                    observableHolder.setValue(string2);
                }

                if (string2 != null) {
                    programClass1 = ClassHierarchyNode.findProgramClass(ZkmUtils.dotsToSlashes(string2));
                }
            }

            ObservableHolder observableHolder1 = xmlElementNode.getAttributeValue("id");
            if (observableHolder1 != null) {
                String string11 = (String) observableHolder1.getValue();
                if (programClass1 != null) {
                    this.beanClasses.put(string11, programClass1);
                    string2 = programClass1.getDottedClassName();
                }
            }

            String string4 = null;
            ObservableHolder observableHolder2 = xmlElementNode.getAttributeValue("factory-ref");
            if (observableHolder2 != null) {
                string4 = (String) observableHolder2.getValue();
                if (programClass1 == null) {
                    programClass1 = (ProgramClass) this.beanClasses.get(string4);
                    if (programClass1 != null) {
                        string2 = programClass1.getDottedClassName();
                    }
                }
            }

            Enumeration enumeration = xmlElementNode.getAttributeNames();

            while (enumeration.hasMoreElements()) {
                String string5 = (String) enumeration.nextElement();
                ObservableHolder observableHolder3 = xmlElementNode.getAttributeValue(string5);
                String string6 = (String) observableHolder3.getValue();
                if (!string5.equalsIgnoreCase("class") && !string5.equalsIgnoreCase("id") && !string5.equalsIgnoreCase("factory-ref")) {
                    if (string5.equalsIgnoreCase("init-method")) {
                        MethodSignature methodSignature1 = new MethodSignature(string6, "()V");
                        String string7 = this.getRenamedMethodName(string2, methodSignature1, new BooleanFlag());
                        if (!string6.equals(string7)) {
                            observableHolder3.setValue(string7);
                        }
                    } else if (string5.equalsIgnoreCase("destroy-method")) {
                        MethodSignature methodSignature2 = new MethodSignature(string6, "()V");
                        String string28 = this.getRenamedMethodName(string2, methodSignature2, new BooleanFlag());
                        if (!string6.equals(string28)) {
                            observableHolder3.setValue(string28);
                        }
                    } else {
                        label213:
                        if (string5.equalsIgnoreCase("factory-method")) {
                            if (string2 != null) {
                                ObservableHolder observableHolder16 = new ObservableHolder();
                                BooleanFlag booleanFlag4 = new BooleanFlag();
                                String string8 = this.resolveRenamedMethodName(string2, string6, 0, observableHolder16, booleanFlag4);
                                if (string8 != null) {
                                    if (!string6.equals(string8)) {
                                        observableHolder3.setValue(string8);
                                    }
                                } else if (booleanFlag4.getValue()) {
                                    super.messageReporter
                                            .reportError(
                                                    "ERROR:",
                                                    "Could not update '"
                                                            + string5
                                                            + "' attribute of tag '"
                                                            + string
                                                            + "' in  XML file '"
                                                            + super.resourceName
                                                            + "' : existing value is '"
                                                            + string6
                                                            + "' : \""
                                                            + (String) observableHolder16.getValue()
                                                            + "\""
                                            );
                                }

                                if (bl) {
                                    break label213;
                                }
                            }

                            if (string4 != null) {
                                String string25 = "reference in '" + super.resourceName + "' by '" + "factory-method" + "' attribute in '" + string + "' tag";
                                this.factoryMethodReferences.put(string6, new XmlMethodReference(45314913602850L, string4, observableHolder3, string25, null));
                            }
                        }
                    }
                }

                if (!bl) {
                    break;
                }
            }
        } else if (string.equalsIgnoreCase("property") && string1 != null && string1.equalsIgnoreCase("bean")) {
            ObservableHolder observableHolder6 = xmlElementNode.getAttributeValue("name");
            if (observableHolder6 != null) {
                String string13 = (String) observableHolder6.getValue();
                String string16 = this.findEnclosingBeanClassName();
                if (string16 != null) {
                    string2 = this.getRenamedClassName(string16);
                    this.resolveRenamedPropertyName(string2, string16, string13, observableHolder6);
                }
            }
        } else {
            label240:
            {
                if (string.equalsIgnoreCase("registration-listener")) {
                    ObservableHolder observableHolder4 = xmlElementNode.getAttributeValue("ref");
                    if (observableHolder4 != null) {
                        ProgramClass programClass2;
                        String string14;
                        String string17;
                        String string30;
                        string14 = (String) observableHolder4.getValue();
                        programClass2 = (ProgramClass) this.beanClasses.get(string14);
                        string17 = "reference in '" + super.resourceName + "' by '" + "registration-listener" + "' tag";
                        ObservableHolder observableHolder8 = xmlElementNode.getAttributeValue("registration-method");
                        label193:
                        if (observableHolder8 != null) {
                            String string19 = (String) observableHolder8.getValue();
                            Map map1;
                            if (programClass2 != null) {
                                ObservableHolder observableHolder12 = new ObservableHolder();
                                BooleanFlag booleanFlag = new BooleanFlag();
                                String string23 = this.resolveRenamedMethodName(programClass2.getDottedClassName(), string19, 0, observableHolder12, booleanFlag);
                                if (string23 != null) {
                                    if (!string19.equals(string23)) {
                                        observableHolder8.setValue(string23);
                                    }
                                } else if (booleanFlag.getValue()) {
                                    super.messageReporter
                                            .reportError(
                                                    "ERROR:",
                                                    "Could not update 'registration-method' attribute of tag '"
                                                            + string
                                                            + "' in  XML file '"
                                                            + super.resourceName
                                                            + "' : existing value is '"
                                                            + string19
                                                            + "' : \""
                                                            + (String) observableHolder12.getValue()
                                                            + "\""
                                            );
                                }

                                if (bl) {
                                    xmlElementNode1 = xmlElementNode;
                                    long bf = 24856730383790L;
                                    string30 = "unregistration-method";
                                    break label193;
                                }

                                map1 = this.factoryMethodReferences;
                            } else {
                                map1 = this.factoryMethodReferences;
                            }

                            map1.put(string19, new XmlMethodReference(45314913602850L, string14, observableHolder8, string17, null));
                            xmlElementNode1 = xmlElementNode;
                            long ba = 24856730383790L;
                            string30 = "unregistration-method";
                        } else {
                            xmlElementNode1 = xmlElementNode;
                            long bb = 24856730383790L;
                            string30 = "unregistration-method";
                        }

                        String string9 = string30;
                        ObservableHolder observableHolder10 = xmlElementNode1.getAttributeValue(string9);
                        label184:
                        if (observableHolder10 != null) {
                            String string21 = (String) observableHolder10.getValue();
                            Map map2;
                            if (programClass2 != null) {
                                ObservableHolder observableHolder14 = new ObservableHolder();
                                BooleanFlag booleanFlag2 = new BooleanFlag();
                                String string26 = this.resolveRenamedMethodName(programClass2.getDottedClassName(), string21, 0, observableHolder14, booleanFlag2);
                                if (string26 != null) {
                                    if (!string21.equals(string26)) {
                                        observableHolder10.setValue(string26);
                                    }
                                } else if (booleanFlag2.getValue()) {
                                    super.messageReporter
                                            .reportError(
                                                    "ERROR:",
                                                    "Could not update 'unregistration-method' attribute of tag '"
                                                            + string
                                                            + "' in  XML file '"
                                                            + super.resourceName
                                                            + "' : existing value is '"
                                                            + string21
                                                            + "' : \""
                                                            + (String) observableHolder14.getValue()
                                                            + "\""
                                            );
                                }

                                if (bl) {
                                    break label184;
                                }

                                map2 = this.factoryMethodReferences;
                            } else {
                                map2 = this.factoryMethodReferences;
                            }

                            map2.put(string21, new XmlMethodReference(45314913602850L, string14, observableHolder10, string17, null));
                        }
                    }

                    if (bl) {
                        break label240;
                    }
                }

                if (string.equalsIgnoreCase("reference-listener")) {
                    ObservableHolder observableHolder5 = xmlElementNode.getAttributeValue("ref");
                    if (observableHolder5 != null) {
                        ProgramClass programClass3;
                        String string15;
                        String string18;
                        String string29;
                        string15 = (String) observableHolder5.getValue();
                        programClass3 = (ProgramClass) this.beanClasses.get(string15);
                        string18 = "reference in '" + super.resourceName + "' by '" + "reference-listener" + "' tag";
                        ObservableHolder observableHolder9 = xmlElementNode.getAttributeValue("bind-method");
                        label170:
                        if (observableHolder9 != null) {
                            String string20 = (String) observableHolder9.getValue();
                            Map map3;
                            if (programClass3 != null) {
                                ObservableHolder observableHolder13 = new ObservableHolder();
                                BooleanFlag booleanFlag1 = new BooleanFlag();
                                String string24 = this.resolveRenamedMethodName(programClass3.getDottedClassName(), string20, 0, observableHolder13, booleanFlag1);
                                if (string24 != null) {
                                    if (!string20.equals(string24)) {
                                        observableHolder9.setValue(string24);
                                    }
                                } else if (booleanFlag1.getValue()) {
                                    super.messageReporter
                                            .reportError(
                                                    "ERROR:",
                                                    "Could not update 'bind-method' attribute of tag '"
                                                            + string
                                                            + "' in  XML file '"
                                                            + super.resourceName
                                                            + "' : existing value is '"
                                                            + string20
                                                            + "' : \""
                                                            + (String) observableHolder13.getValue()
                                                            + "\""
                                            );
                                }

                                if (bl) {
                                    xmlElementNode2 = xmlElementNode;
                                    long bc = 24856730383790L;
                                    string29 = "unbind-method";
                                    break label170;
                                }

                                map3 = this.factoryMethodReferences;
                            } else {
                                map3 = this.factoryMethodReferences;
                            }

                            map3.put(string20, new XmlMethodReference(45314913602850L, string15, observableHolder9, string18, null));
                            xmlElementNode2 = xmlElementNode;
                            long bd = 24856730383790L;
                            string29 = "unbind-method";
                        } else {
                            xmlElementNode2 = xmlElementNode;
                            long be = 24856730383790L;
                            string29 = "unbind-method";
                        }

                        String string10 = string29;
                        ObservableHolder observableHolder11 = xmlElementNode2.getAttributeValue(string10);
                        label161:
                        if (observableHolder11 != null) {
                            String string22 = (String) observableHolder11.getValue();
                            Map map4;
                            if (programClass3 != null) {
                                ObservableHolder observableHolder15 = new ObservableHolder();
                                BooleanFlag booleanFlag3 = new BooleanFlag();
                                String string27 = this.resolveRenamedMethodName(programClass3.getDottedClassName(), string22, 0, observableHolder15, booleanFlag3);
                                if (string27 != null) {
                                    if (!string22.equals(string27)) {
                                        observableHolder11.setValue(string27);
                                    }
                                } else if (booleanFlag3.getValue()) {
                                    super.messageReporter
                                            .reportError(
                                                    "ERROR:",
                                                    "Could not update 'unbind-method' attribute of tag '"
                                                            + string
                                                            + "' in  XML file '"
                                                            + super.resourceName
                                                            + "' : existing value is '"
                                                            + string22
                                                            + "' : \""
                                                            + (String) observableHolder15.getValue()
                                                            + "\""
                                            );
                                }

                                if (bl) {
                                    break label161;
                                }

                                map4 = this.factoryMethodReferences;
                            } else {
                                map4 = this.factoryMethodReferences;
                            }

                            map4.put(string22, new XmlMethodReference(45314913602850L, string15, observableHolder11, string18, null));
                        }
                    }

                    if (bl) {
                        break label240;
                    }
                }

                Enumeration enumeration1 = xmlElementNode.getAttributeNames();

                while (enumeration1.hasMoreElements()) {
                    String string12 = (String) enumeration1.nextElement();
                    ObservableHolder observableHolder7 = xmlElementNode.getAttributeValue(string12);
                    observableHolder7.setValue(this.updateReferenceValue((String) observableHolder7.getValue(), string12, false));
                }
            }
        }

        ((List) object1).add(xmlElementNode);
    }

    @Override
    public void analyzeElement(XmlElementNode xmlElementNode, Map map1, Object object, Object object1, Object object2) throws ZkmException, IOException {
        Map map3 = (Map) object;
        Map map2 = (Map) object1;
        TwoKeyMap twoKeyMap = (TwoKeyMap) object2;
        String string = xmlElementNode.getTagName();
        String string1 = this.getOpenElementName(1);
        if (string.equalsIgnoreCase("bean")) {
            String string2 = "reference in '" + super.resourceName + "' by '" + string + "' tag";
            String string3 = null;
            ProgramClass programClass1 = null;
            ObservableHolder observableHolder = xmlElementNode.getAttributeValue("class");
            if (observableHolder != null) {
                String string4 = (String) observableHolder.getValue();
                string3 = this.addClassReference(string4, map1, string2 + " '" + "class" + "' attribute");
                if (string3 != null) {
                    programClass1 = ClassHierarchyNode.findProgramClass(ZkmUtils.dotsToSlashes(string3));
                }
            }

            ObservableHolder observableHolder1 = xmlElementNode.getAttributeValue("id");
            if (observableHolder1 != null) {
                String string24 = (String) observableHolder1.getValue();
                if (programClass1 != null) {
                    this.beanClasses.put(string24, programClass1);
                }
            }

            String string5 = null;
            ObservableHolder observableHolder2 = xmlElementNode.getAttributeValue("factory-ref");
            if (observableHolder2 != null) {
                string5 = (String) observableHolder2.getValue();
                if (programClass1 == null) {
                    programClass1 = (ProgramClass) this.beanClasses.get(string5);
                    if (programClass1 != null) {
                        string3 = programClass1.getDottedClassName();
                    }
                }
            }

            Enumeration enumeration = xmlElementNode.getAttributeNames();

            while (enumeration.hasMoreElements()) {
                String string6 = (String) enumeration.nextElement();
                String string7 = (String) xmlElementNode.getAttributeValue(string6).getValue();
                String string8 = string2 + " '" + string6 + "' attribute";
                if (!string6.equalsIgnoreCase("class") && !string6.equalsIgnoreCase("id") && !string6.equalsIgnoreCase("factory-ref")) {
                    if (string6.equalsIgnoreCase("init-method")) {
                        MethodSignature methodSignature1 = new MethodSignature(string7, "()V");
                        this.addMethodReference(string3, methodSignature1, map2, string8);
                    } else if (string6.equalsIgnoreCase("destroy-method")) {
                        MethodSignature methodSignature2 = new MethodSignature(string7, "()V");
                        this.addMethodReference(string3, methodSignature2, map2, string8);
                    } else if (string6.equalsIgnoreCase("factory-method")) {
                        if (programClass1 != null) {
                            this.addHierarchyMethodReferences(
                                    programClass1, string7, (String) null, null, BeanPropertyXmlHandler.ANY_METHOD_DESCRIPTOR, 0, map2, twoKeyMap, string2
                            );
                        } else if (string5 != null) {
                            this.factoryMethodReferences.put(string7, new XmlMethodReference(string5, string2));
                        }
                    }
                }
            }
        } else if (string.equalsIgnoreCase("service")) {
            ObservableHolder observableHolder3 = xmlElementNode.getAttributeValue("interface");
            if (observableHolder3 != null) {
                String string12 = (String) observableHolder3.getValue();
                String string16 = "reference in '" + super.resourceName + "' by '" + string + "' tag";
                this.addClassReference(string12, map1, string16 + " '" + "class" + "' attribute");
            }
        } else if (string.equalsIgnoreCase("registration-listener")) {
            ObservableHolder observableHolder4 = xmlElementNode.getAttributeValue("ref");
            if (observableHolder4 != null) {
                String string17 = (String) observableHolder4.getValue();
                ProgramClass programClass2 = (ProgramClass) this.beanClasses.get(string17);
                String string21 = "reference in '" + super.resourceName + "' by '" + "registration-listener" + "' tag";
                ObservableHolder observableHolder8 = xmlElementNode.getAttributeValue("registration-method");
                XmlElementNode xmlElementNode2;
                String string30;
                if (observableHolder8 != null) {
                    String string25 = (String) observableHolder8.getValue();
                    if (programClass2 != null) {
                        this.addHierarchyMethodReferences(
                                programClass2, string25, (String) null, null, BeanPropertyXmlHandler.ANY_METHOD_DESCRIPTOR, 0, map2, twoKeyMap, string21
                        );
                        xmlElementNode2 = xmlElementNode;
                        long bf = 24856730383790L;
                        string30 = "unregistration-method";
                    } else {
                        this.factoryMethodReferences.put(string25, new XmlMethodReference(string17, string21));
                        xmlElementNode2 = xmlElementNode;
                        long ba = 24856730383790L;
                        string30 = "unregistration-method";
                    }
                } else {
                    xmlElementNode2 = xmlElementNode;
                    long bb = 24856730383790L;
                    string30 = "unregistration-method";
                }

                String string9 = string30;
                ObservableHolder observableHolder10 = xmlElementNode2.getAttributeValue(string9);
                if (observableHolder10 != null) {
                    String string27 = (String) observableHolder10.getValue();
                    if (programClass2 != null) {
                        this.addHierarchyMethodReferences(
                                programClass2, string27, (String) null, null, BeanPropertyXmlHandler.ANY_METHOD_DESCRIPTOR, 0, map2, twoKeyMap, string21
                        );
                    } else {
                        this.factoryMethodReferences.put(string27, new XmlMethodReference(string17, string21));
                    }
                }
            }
        } else if (string.equalsIgnoreCase("reference-listener")) {
            ObservableHolder observableHolder5 = xmlElementNode.getAttributeValue("ref");
            if (observableHolder5 != null) {
                String string18 = (String) observableHolder5.getValue();
                ProgramClass programClass3 = (ProgramClass) this.beanClasses.get(string18);
                String string22 = "reference in '" + super.resourceName + "' by '" + "reference-listener" + "' tag";
                ObservableHolder observableHolder9 = xmlElementNode.getAttributeValue("bind-method");
                XmlElementNode xmlElementNode1;
                String string29;
                if (observableHolder9 != null) {
                    String string26 = (String) observableHolder9.getValue();
                    if (programClass3 != null) {
                        this.addHierarchyMethodReferences(
                                programClass3, string26, (String) null, null, BeanPropertyXmlHandler.ANY_METHOD_DESCRIPTOR, 0, map2, twoKeyMap, string22
                        );
                        xmlElementNode1 = xmlElementNode;
                        long bc = 24856730383790L;
                        string29 = "unbind-method";
                    } else {
                        this.factoryMethodReferences.put(string26, new XmlMethodReference(string18, string22));
                        xmlElementNode1 = xmlElementNode;
                        long bd = 24856730383790L;
                        string29 = "unbind-method";
                    }
                } else {
                    xmlElementNode1 = xmlElementNode;
                    long be = 24856730383790L;
                    string29 = "unbind-method";
                }

                String string10 = string29;
                ObservableHolder observableHolder11 = xmlElementNode1.getAttributeValue(string10);
                if (observableHolder11 != null) {
                    String string28 = (String) observableHolder11.getValue();
                    if (programClass3 != null) {
                        this.addHierarchyMethodReferences(
                                programClass3, string28, (String) null, null, BeanPropertyXmlHandler.ANY_METHOD_DESCRIPTOR, 0, map2, twoKeyMap, string22
                        );
                    } else {
                        this.factoryMethodReferences.put(string28, new XmlMethodReference(string18, string22));
                    }
                }
            }
        } else if (string.equalsIgnoreCase("reference")) {
            ObservableHolder observableHolder6 = xmlElementNode.getAttributeValue("interface");
            if (observableHolder6 != null) {
                String string13 = (String) observableHolder6.getValue();
                String string19 = "reference in '" + super.resourceName + "' by '" + string + "' tag";
                this.addClassReference(string13, map1, string19 + " '" + "class" + "' attribute");
            }
        } else if (string.equalsIgnoreCase("property") && string1 != null && string1.equalsIgnoreCase("bean")) {
            ObservableHolder observableHolder7 = xmlElementNode.getAttributeValue("name");
            if (observableHolder7 != null) {
                String string15 = (String) observableHolder7.getValue();
                String string20 = "reference in '" + super.resourceName + "' by '" + string + "' tag '" + "name" + "' attribute";
                String string23 = this.findEnclosingBeanClassName();
                if (string23 != null) {
                    ProgramClass programClass5 = this.findProgramClass(string23);
                    if (programClass5 != null) {
                        this.addPropertyAccessorReferences(programClass5, string15, map2, twoKeyMap, string20);
                    }
                }
            }
        } else if (string.equalsIgnoreCase("argument")) {
            String string11 = "reference in '" + super.resourceName + "' by '" + string + "' tag '" + "argument" + "' attribute";
            String string14 = this.findEnclosingBeanClassName();
            if (string14 != null) {
                ProgramClass programClass4 = this.findProgramClass(string14);
                if (programClass4 != null) {
                    this.addHierarchyMethodReferences(
                            programClass4, "<init>", (String) null, null, BeanPropertyXmlHandler.ANY_METHOD_DESCRIPTOR, 0, map2, twoKeyMap, string11
                    );
                }
            }
        } else {
            super.analyzeElement(xmlElementNode, map1, map3, map2, twoKeyMap);
        }
    }

    @Override
    public boolean shouldFlushOutput() {
        return false;
    }

    public String findEnclosingBeanClassName() {
        int ba = 0;
        int bb = 0;

        for (ObjectStack objectStack = super.elementStack; bb < objectStack.size(); objectStack = super.elementStack) {
            XmlElementNode xmlElementNode = (XmlElementNode) super.elementStack.peekAt(ba);
            ObservableHolder observableHolder;
            if (xmlElementNode.getTagName().equals("bean") && (observableHolder = xmlElementNode.getAttributeValue("class")) != null) {
                return (String) observableHolder.getValue();
            }

            bb = ++ba;
        }

        return null;
    }
}
