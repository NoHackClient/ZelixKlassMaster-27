package com.zelix.klassmaster.xml;

import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClassMemberLookup;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.log.MessageReporter;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SpringImportedBeansXmlHandler extends SpringXmlHandlerBase {
    public final Set visitedFiles;

    public SpringImportedBeansXmlHandler(
            String string,
            ResourceFileIndex resourceFileIndex1,
            Set set1,
            Map map1,
            Map map2,
            Map map3,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1,
            MessageReporter messageReporter1
    ) {
        super(string, resourceFileIndex1, classMemberLookup1, classResolver1, messageReporter1);
        this.visitedFiles = set1;
        set1.add(string);
        super.beanClasses.putAll(map1);
        super.abstractBeans.putAll(map2);
        super.beanParents.putAll(map3);
    }

    @Override
    public void analyzeElement(XmlElementNode xmlElementNode, Map map1, Object object, Object object1, Object object2) throws ZkmException, IOException {
        String string = xmlElementNode.getTagName();
        String string1 = this.getOpenElementName(1);
        if (string.equalsIgnoreCase("bean") && string1 != null && string1.equalsIgnoreCase("beans")) {
            String string8 = "reference in '" + super.resourceName + "' by '" + string + "' tag";
            String string9 = null;
            ObservableHolder observableHolder6 = xmlElementNode.getAttributeValue("class");
            List list1 = null;
            ObservableHolder observableHolder1 = xmlElementNode.getAttributeValue("id");
            XmlElementNode xmlElementNode3;
            String string17;
            if (observableHolder1 != null) {
                list1 = new ArrayList(1);
                list1.add(observableHolder1.getValue());
                xmlElementNode3 = xmlElementNode;
                long bc = 24856730383790L;
                string17 = "parent";
            } else {
                ObservableHolder observableHolder2 = xmlElementNode.getAttributeValue("name");
                if (observableHolder2 != null) {
                    list1 = this.splitBeanNames((String) observableHolder2.getValue());
                    xmlElementNode3 = xmlElementNode;
                    long ba = 24856730383790L;
                    string17 = "parent";
                } else {
                    xmlElementNode3 = xmlElementNode;
                    long bb = 24856730383790L;
                    string17 = "parent";
                }
            }

            String string7 = string17;
            ObservableHolder observableHolder7 = xmlElementNode3.getAttributeValue(string7);
            if (observableHolder6 != null) {
                String string4 = (String) observableHolder6.getValue();
                ObservableHolder observableHolder3 = new ObservableHolder();
                string9 = this.addClassReference(string4, map1, observableHolder3, string8 + " '" + "class" + "' attribute");
                if (!observableHolder3.isValueNull() && list1 != null) {
                    Iterator iterator = list1.iterator();

                    while (iterator.hasNext()) {
                        String string5 = (String) iterator.next();
                        ProgramClass programClass3 = (ProgramClass) super.beanClasses.put(string5, observableHolder3.getValue());
                    }
                }
            }

            if (string9 == null) {
                ObservableHolder observableHolder8 = xmlElementNode.getAttributeValue("factory-bean");
                if (observableHolder8 != null) {
                    String string11 = (String) observableHolder8.getValue();
                    ProgramClass programClass2 = (ProgramClass) super.beanClasses.get(string11);
                    if (programClass2 != null) {
                        string9 = programClass2.getDottedClassName();
                        if (list1 != null) {
                            Iterator iterator3 = list1.iterator();

                            while (iterator3.hasNext()) {
                                String string6 = (String) iterator3.next();
                                super.beanClasses.put(string6, programClass2);
                            }
                        }
                    }
                }
            }

            if (string9 == null && observableHolder7 != null && list1 != null) {
                String string10 = (String) observableHolder7.getValue();
                Iterator iterator2 = list1.iterator();

                while (iterator2.hasNext()) {
                    String string13 = (String) iterator2.next();
                    super.beanParents.put(string13, string10);
                    String string14 = string10;
                    ProgramClass programClass1 = null;

                    while (string14 != null && programClass1 == null) {
                        programClass1 = (ProgramClass) super.beanClasses.get(string10);
                        if (programClass1 != null) {
                            string9 = programClass1.getDottedClassName();
                        } else {
                            string14 = (String) super.beanParents.get(string14);
                        }
                    }

                    if (programClass1 != null) {
                        super.beanClasses.put(string13, programClass1);
                    }
                }
            }

            if (string9 == null) {
                ObservableHolder observableHolder9 = xmlElementNode.getAttributeValue("abstract");
                if (observableHolder9 != null && ((String) observableHolder9.getValue()).equalsIgnoreCase("true")) {
                    XmlElementNode xmlElementNode2 = this.getOpenElement(1);
                    AbstractXmlBeanRef abstractXmlBeanRef1 = new AbstractXmlBeanRef(xmlElementNode, xmlElementNode2);
                    if (list1 != null) {
                        Iterator iterator4 = list1.iterator();

                        while (iterator4.hasNext()) {
                            String string15 = (String) iterator4.next();
                            super.abstractBeans.put(string15, abstractXmlBeanRef1);
                        }
                    }
                }
            }

            if (observableHolder7 != null && list1 != null) {
                Iterator iterator1 = list1.iterator();

                while (iterator1.hasNext()) {
                    String string12 = (String) iterator1.next();
                    if (!super.beanParents.containsKey(string12)) {
                        super.beanParents.put(string12, observableHolder7.getValue());
                    }
                }
            }
        } else if (string.equalsIgnoreCase("property") && string1 != null && string1.equalsIgnoreCase("bean")) {
            XmlElementNode xmlElementNode1 = this.getOpenElement(1);
            ObservableHolder observableHolder4 = xmlElementNode1.getAttributeValue("abstract");
            if (observableHolder4 != null && ((String) observableHolder4.getValue()).equalsIgnoreCase("true")) {
                ObservableHolder observableHolder5 = xmlElementNode1.getAttributeValue("id");
                if (observableHolder5 != null) {
                    AbstractXmlBeanRef abstractXmlBeanRef = (AbstractXmlBeanRef) super.abstractBeans.get(observableHolder5.getValue());
                    if (abstractXmlBeanRef != null) {
                        abstractXmlBeanRef.addPropertyElement(xmlElementNode);
                    }
                }
            }
        } else if (string.equalsIgnoreCase("import") && string1 != null && string1.equalsIgnoreCase("beans")) {
            Enumeration enumeration = xmlElementNode.getAttributeNames();

            while (enumeration.hasMoreElements()) {
                String string2 = (String) enumeration.nextElement();
                if (string2.equalsIgnoreCase("resource")) {
                    ObservableHolder observableHolder = xmlElementNode.getAttributeValue(string2);
                    String string3 = (String) observableHolder.getValue();
                    this.processImportResource(string3);
                }
            }
        }
    }

    public Map copyAbstractBeans() {
        return ZkmUtils.copyToHashMap(super.abstractBeans);
    }

    @Override
    public List resolveImportedResources(String string, String string1) throws ZkmException, IOException {
        ArrayList arrayList = new ArrayList();
        Iterator iterator = super.resourceFileIndex.loadMatchingConfigFiles(string, string1, XmlConfigFileType.SPRING, super.messageReporter).iterator();

        while (iterator.hasNext()) {
            TextResourceContent textResourceContent = (TextResourceContent) iterator.next();
            if (!this.visitedFiles.contains(textResourceContent.resourceName)) {
                try {
                    SpringImportedBeansXmlHandler springImportedBeansXmlHandler1 = new SpringImportedBeansXmlHandler(
                            textResourceContent.resourceName,
                            super.resourceFileIndex,
                            this.visitedFiles,
                            super.beanClasses,
                            super.abstractBeans,
                            super.beanParents,
                            super.classMemberLookup,
                            super.classResolver,
                            super.messageReporter
                    );
                    new XmlResourceReader(
                            textResourceContent.resourceName,
                            textResourceContent.content,
                            textResourceContent.skipByteCount,
                            textResourceContent.bomLength,
                            textResourceContent.byteOrder,
                            (String) textResourceContent.encoding.getValue(),
                            springImportedBeansXmlHandler1
                    );
                    arrayList.add(textResourceContent.resourceName);
                    Map map1 = springImportedBeansXmlHandler1.copyBeanClasses();
                    super.beanClasses.putAll(map1);
                    Map map2 = springImportedBeansXmlHandler1.copyAbstractBeans();
                    super.abstractBeans.putAll(map2);
                    Map map3 = springImportedBeansXmlHandler1.copyBeanParents();
                    super.beanParents.putAll(map3);
                } catch (ZkmException zkmException) {
                    super.messageReporter
                            .reportError("FILE ERROR:", "Invalid XML file '" + textResourceContent.resourceName + "' (B) : " + zkmException.getMessage());
                } catch (IOException iOException) {
                    super.messageReporter
                            .reportError("FILE ERROR:", "Error reading XML file '" + textResourceContent.resourceName + "' (B) : " + iOException.getMessage());
                } catch (Exception exception) {
                    super.messageReporter
                            .reportError("FILE ERROR:", "Unexpected error analyzing XML file '" + super.resourceName + "' (B) : " + exception.getMessage());
                }
            }
        }

        return arrayList;
    }

    @Override
    public void rewriteElement(Object object, Object object1) throws ZkmException, IOException {
    }

    public Map copyBeanClasses() {
        return ZkmUtils.copyToHashMap(super.beanClasses);
    }

    public Map copyBeanParents() {
        return ZkmUtils.copyToHashMap(super.beanParents);
    }
}
