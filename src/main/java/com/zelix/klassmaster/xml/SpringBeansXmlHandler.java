package com.zelix.klassmaster.xml;

import com.zelix.klassmaster.archive.ZkmFileUtils;
import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.classfile.hierarchy.ClassMemberLookup;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.log.MessageReporter;
import com.zelix.klassmaster.util.BooleanFlag;
import com.zelix.klassmaster.util.EnumerableMap;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ReadOnlyMultiMap;
import com.zelix.klassmaster.util.SetMultiMap;
import com.zelix.klassmaster.util.TwoKeyMap;
import com.zelix.klassmaster.util.UniqueWorkQueue;
import com.zelix.klassmaster.util.ZkmAssert;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class SpringBeansXmlHandler extends SpringXmlHandlerBase {
    public ListMultimap childBeansByParent;

    @Override
    public void analyzeElement(XmlElementNode xmlElementNode, Map map1, Object object, Object object1, Object object2) throws ZkmException, IOException {
        TwoKeyMap twoKeyMap = (TwoKeyMap) object2;
        Map map3 = (Map) object1;
        Map map2 = (Map) object;
        String string = xmlElementNode.getTagName();
        String string1 = this.getOpenElementName(1);
        if (string.equalsIgnoreCase("bean") && string1 != null && string1.equalsIgnoreCase("beans")) {
            boolean bl = false;
            boolean bl1 = false;
            String string14 = "reference in '" + super.resourceName + "' by '" + string + "' tag";
            String string16 = null;
            ObservableHolder observableHolder6 = xmlElementNode.getAttributeValue("class");
            List list1 = null;
            ObservableHolder observableHolder7 = xmlElementNode.getAttributeValue("id");
            if (observableHolder7 != null) {
                list1 = new ArrayList(1);
                list1.add(observableHolder7.getValue());
            } else {
                ObservableHolder observableHolder8 = xmlElementNode.getAttributeValue("name");
                if (observableHolder8 != null) {
                    list1 = this.splitBeanNames((String) observableHolder8.getValue());
                }
            }

            if (observableHolder6 != null) {
                String string17 = (String) observableHolder6.getValue();
                ObservableHolder observableHolder3 = new ObservableHolder();
                string16 = this.addClassReference(string17, map1, observableHolder3, string14 + " '" + "class" + "' attribute");
                if (!observableHolder3.isValueNull() && list1 != null) {
                    Iterator iterator = list1.iterator();

                    while (iterator.hasNext()) {
                        String string5 = (String) iterator.next();
                        ProgramClass programClass7 = (ProgramClass) super.beanClasses.put(string5, observableHolder3.getValue());
                    }
                }
            }

            if (string16 == null) {
                ObservableHolder observableHolder9 = xmlElementNode.getAttributeValue("factory-bean");
                if (observableHolder9 != null) {
                    String string18 = (String) observableHolder9.getValue();
                    ProgramClass programClass4 = (ProgramClass) super.beanClasses.get(string18);
                    if (programClass4 != null && list1 != null) {
                        string16 = programClass4.getDottedClassName();
                        Iterator iterator4 = list1.iterator();

                        while (iterator4.hasNext()) {
                            String string6 = (String) iterator4.next();
                            super.beanClasses.put(string6, programClass4);
                        }
                    }
                }
            }

            if (string16 == null) {
                ObservableHolder observableHolder10 = xmlElementNode.getAttributeValue("parent");
                if (observableHolder10 != null && list1 != null) {
                    bl1 = true;
                    String string19 = (String) observableHolder10.getValue();
                    Iterator iterator2 = list1.iterator();

                    while (iterator2.hasNext()) {
                        String string20 = (String) iterator2.next();
                        super.beanParents.put(string20, string19);
                        String string7 = string19;
                        ProgramClass programClass2 = null;

                        while (string7 != null && programClass2 == null) {
                            programClass2 = (ProgramClass) super.beanClasses.get(string7);
                            if (programClass2 != null) {
                                string16 = programClass2.getDottedClassName();
                                super.beanClasses.put(string20, programClass2);
                            } else {
                                string7 = (String) super.beanParents.get(string7);
                            }
                        }
                    }
                }
            }

            if (string16 == null) {
                ObservableHolder observableHolder11 = xmlElementNode.getAttributeValue("abstract");
                if (observableHolder11 != null && ((String) observableHolder11.getValue()).equalsIgnoreCase("true")) {
                    bl = true;
                    XmlElementNode xmlElementNode2 = this.getOpenElement(1);
                    if (list1 != null) {
                        Iterator iterator3 = list1.iterator();

                        while (iterator3.hasNext()) {
                            String string21 = (String) iterator3.next();
                            AbstractXmlBeanRef abstractXmlBeanRef1 = new AbstractXmlBeanRef(xmlElementNode, xmlElementNode2);
                            super.abstractBeans.put(string21, abstractXmlBeanRef1);
                        }
                    }
                }
            }

            if (string16 == null) {
                if (!bl) {
                    if (HiddenOptionFlags.ASSERT_SPRING_BEAN_CLASS) {
                        Object[] objects2 = new Object[]{super.resourceName, xmlElementNode, 0};
                        ZkmAssert.assertNotNullArgs(string16, objects2);
                    } else {
                        super.messageReporter.reportError("ERROR:", "Could not identify bean class in : '" + super.resourceName + "' : '" + xmlElementNode + "' (A)");
                    }
                }

                return;
            }

            ProgramClass programClass3 = ClassHierarchyNode.findProgramClass(ZkmUtils.dotsToSlashes(string16));
            if (programClass3 != null) {
                DescriptorMatcher descriptorMatcher1 = WildcardStringMatcher.MATCH_ALL;
                String string12 = string14;
                TwoKeyMap twoKeyMap1 = twoKeyMap;
                Map map5 = map3;
                Map map4 = map1;
                Integer integer = 0;
                Object object4 = null;
                DescriptorMatcher descriptorMatcher = descriptorMatcher1;
                Object object3 = null;
                this.addMatchingMethodReferences("<init>", (String) null, object3, descriptorMatcher, (String) object4, integer, map4, map5, twoKeyMap1, string12);
            }

            boolean bl2 = false;
            boolean bl3 = false;
            Enumeration enumeration1 = xmlElementNode.getAttributeNames();

            while (enumeration1.hasMoreElements()) {
                String string22 = (String) enumeration1.nextElement();
                ObservableHolder observableHolder12 = xmlElementNode.getAttributeValue(string22);
                String string26 = (String) observableHolder12.getValue();
                String string8 = string14 + " '" + string22 + "' attribute";
                if (!string22.equalsIgnoreCase("class")) {
                    if (string22.equalsIgnoreCase("init-method")) {
                        MethodSignature methodSignature4 = new MethodSignature(string26, "()V");
                        Object[] objects1 = new Object[]{methodSignature4, super.resourceName, xmlElementNode, 1};
                        ZkmAssert.assertNotNullArgs(string16, objects1);
                        this.addMethodReference(string16, methodSignature4, map3, string8);
                        bl2 = true;
                    } else if (string22.equalsIgnoreCase("destroy-method")) {
                        MethodSignature methodSignature3 = new MethodSignature(string26, "()V");
                        Object[] objects = new Object[]{methodSignature3, super.resourceName, xmlElementNode, 2};
                        ZkmAssert.assertNotNullArgs(string16, objects);
                        this.addMethodReference(string16, methodSignature3, map3, string8);
                        bl3 = true;
                    } else if (string22.equalsIgnoreCase("factory-method")) {
                        if (programClass3 != null) {
                            this.addHierarchyMethodReferences(
                                    programClass3, string26, (String) null, null, BeanPropertyXmlHandler.ANY_METHOD_DESCRIPTOR, 8, map3, twoKeyMap, string14
                            );
                        }
                    } else if (string22.equalsIgnoreCase("parent")) {
                        bl1 = true;
                        if (list1 != null) {
                            Iterator iterator1 = list1.iterator();

                            while (iterator1.hasNext()) {
                                String string9 = (String) iterator1.next();
                                super.beanParents.put(string9, string26);
                            }
                        }
                    }
                }
            }

            if (bl1) {
                String string23 = null;
                Map map6;
                if (list1 != null) {
                    Iterator iterator5 = list1.iterator();

                    while (true) {
                        if (!iterator5.hasNext()) {
                            map6 = super.abstractBeans;
                            break;
                        }

                        String string27 = (String) iterator5.next();
                        string23 = (String) super.beanParents.get(string27);
                        if (string23 != null) {
                            map6 = super.abstractBeans;
                            break;
                        }
                    }
                } else {
                    map6 = super.abstractBeans;
                }

                AbstractXmlBeanRef abstractXmlBeanRef2 = (AbstractXmlBeanRef) map6.get(string23);

                while (string23 != null && abstractXmlBeanRef2 != null) {
                    XmlElementNode xmlElementNode4 = abstractXmlBeanRef2.getBeanElement();
                    if (!bl2
                            && xmlElementNode4.getAttributeValue("default-init-method") != null
                            && xmlElementNode4.getAttributeValue("default-init-method").getValue() != null) {
                        String string28 = (String) xmlElementNode4.getAttributeValue("default-init-method").getValue();
                        MethodSignature methodSignature5 = new MethodSignature(string28, "()V");
                        this.addMethodReference(
                                string16,
                                methodSignature5,
                                map3,
                                "reference in '" + super.resourceName + "' by '" + "bean" + "' tag " + "default-init-method" + "='" + string28 + "'"
                        );
                        bl2 = true;
                    }

                    if (!bl3
                            && xmlElementNode4.getAttributeValue("default-destroy-method") != null
                            && xmlElementNode4.getAttributeValue("default-destroy-method").getValue() != null) {
                        String string29 = (String) xmlElementNode4.getAttributeValue("default-destroy-method").getValue();
                        MethodSignature methodSignature6 = new MethodSignature(string29, "()V");
                        this.addMethodReference(
                                string16,
                                methodSignature6,
                                map3,
                                "reference in '" + super.resourceName + "' by '" + "bean" + "' tag " + "default-destroy-method" + "='" + string29 + "'"
                        );
                        bl3 = true;
                    }

                    XmlElementNode xmlElementNode5 = abstractXmlBeanRef2.getParentElement();
                    if (!bl2
                            && xmlElementNode5.getAttributeValue("default-init-method") != null
                            && xmlElementNode5.getAttributeValue("default-init-method").getValue() != null) {
                        String string30 = (String) xmlElementNode5.getAttributeValue("default-init-method").getValue();
                        MethodSignature methodSignature7 = new MethodSignature(string30, "()V");
                        this.addMethodReference(
                                string16,
                                methodSignature7,
                                map3,
                                "reference in '" + super.resourceName + "' by '" + "beans" + "' tag " + "default-init-method" + "='" + string30 + "'"
                        );
                        if (programClass3 != null) {
                            super.resourceFileIndex.addDefaultMethodClass(super.resourceName, "default-init-method", programClass3);
                        }

                        bl2 = true;
                    }

                    if (!bl3
                            && xmlElementNode5.getAttributeValue("default-destroy-method") != null
                            && xmlElementNode5.getAttributeValue("default-destroy-method").getValue() != null) {
                        String string31 = (String) xmlElementNode5.getAttributeValue("default-destroy-method").getValue();
                        MethodSignature methodSignature8 = new MethodSignature(string31, "()V");
                        this.addMethodReference(
                                string16,
                                methodSignature8,
                                map3,
                                "reference in '" + super.resourceName + "' by '" + "beans" + "' tag " + "default-destroy-method" + "='" + string31 + "'"
                        );
                        if (programClass3 != null) {
                            super.resourceFileIndex.addDefaultMethodClass(super.resourceName, "default-destroy-method", programClass3);
                        }

                        bl3 = true;
                    }

                    List list2 = abstractXmlBeanRef2.getPropertyElements();
                    Iterator iterator6 = list2.iterator();

                    while (iterator6.hasNext()) {
                        ObservableHolder observableHolder4 = ((XmlElementNode) iterator6.next()).getAttributeValue("name");
                        if (observableHolder4 != null) {
                            String string10 = (String) observableHolder4.getValue();
                            String string11 = "reference in '" + super.resourceName + "' by '" + string + "' tag '" + "name" + "' attribute (B)";
                            if (programClass3 != null) {
                                this.addPropertyAccessorReferences(programClass3, string10, map3, twoKeyMap, string11);
                            }
                        }
                    }

                    string23 = (String) super.beanParents.get(string23);
                    if (string23 != null) {
                        abstractXmlBeanRef2 = (AbstractXmlBeanRef) super.abstractBeans.get(string23);
                    }
                }
            }

            XmlElementNode xmlElementNode3 = this.getOpenElement(1);
            if (!bl2
                    && xmlElementNode3.getAttributeValue("default-init-method") != null
                    && xmlElementNode3.getAttributeValue("default-init-method").getValue() != null) {
                String string24 = (String) xmlElementNode3.getAttributeValue("default-init-method").getValue();
                MethodSignature methodSignature1 = new MethodSignature(string24, "()V");
                this.addMethodReference(
                        string16,
                        methodSignature1,
                        map3,
                        "reference in '" + super.resourceName + "' by '" + "beans" + "' tag " + "default-init-method" + "='" + string24 + "'"
                );
                if (programClass3 != null) {
                    super.resourceFileIndex.addDefaultMethodClass(super.resourceName, "default-init-method", programClass3);
                }
            }

            if (!bl3
                    && xmlElementNode3.getAttributeValue("default-destroy-method") != null
                    && xmlElementNode3.getAttributeValue("default-destroy-method").getValue() != null) {
                String string25 = (String) xmlElementNode3.getAttributeValue("default-destroy-method").getValue();
                MethodSignature methodSignature2 = new MethodSignature(string25, "()V");
                this.addMethodReference(
                        string16,
                        methodSignature2,
                        map3,
                        "reference in '" + super.resourceName + "' by '" + "beans" + "' tag " + "default-destroy-method" + "='" + string25 + "'"
                );
                if (programClass3 != null) {
                    super.resourceFileIndex.addDefaultMethodClass(super.resourceName, "default-destroy-method", programClass3);
                }
            }
        } else if (string.equalsIgnoreCase("import") && string1 != null && string1.equalsIgnoreCase("beans")) {
            Enumeration enumeration = xmlElementNode.getAttributeNames();

            while (enumeration.hasMoreElements()) {
                String string13 = (String) enumeration.nextElement();
                if (string13.equalsIgnoreCase("resource")) {
                    ObservableHolder observableHolder5 = xmlElementNode.getAttributeValue(string13);
                    String string15 = (String) observableHolder5.getValue();
                    this.processImportResource(string15);
                }
            }
        } else if (string.equalsIgnoreCase("property") && string1 != null && string1.equalsIgnoreCase("bean")) {
            ObservableHolder observableHolder = xmlElementNode.getAttributeValue("name");
            if (observableHolder != null) {
                String string2 = (String) observableHolder.getValue();
                String string3 = "reference in '" + super.resourceName + "' by '" + string + "' tag '" + "name" + "' attribute (A)";
                String string4 = this.findEnclosingBeanClassName();
                if (string4 != null) {
                    ProgramClass programClass1 = this.findProgramClass(string4);
                    if (programClass1 != null) {
                        this.addPropertyAccessorReferences(programClass1, string2, map3, twoKeyMap, string3);
                    }
                } else {
                    XmlElementNode xmlElementNode1 = this.getOpenElement(1);
                    ObservableHolder observableHolder1 = xmlElementNode1.getAttributeValue("abstract");
                    if (observableHolder1 != null && ((String) observableHolder1.getValue()).equalsIgnoreCase("true")) {
                        ObservableHolder observableHolder2 = xmlElementNode1.getAttributeValue("id");
                        if (observableHolder2 != null) {
                            AbstractXmlBeanRef abstractXmlBeanRef = (AbstractXmlBeanRef) super.abstractBeans.get(observableHolder2.getValue());
                            if (abstractXmlBeanRef != null) {
                                abstractXmlBeanRef.addPropertyElement(xmlElementNode);
                            }
                        }
                    }
                }
            }
        } else {
            super.analyzeElement(xmlElementNode, map1, map2, map3, twoKeyMap);
        }
    }

    public ProgramClass findChildBeanClass(Object object, ListMultimap listMultimap) {
        List list1 = listMultimap.getValues(object);
        ProgramClass programClass1 = null;
        if (list1 != null) {
            Iterator iterator = list1.iterator();

            while (iterator.hasNext()) {
                String string = (String) iterator.next();
                programClass1 = (ProgramClass) super.beanClasses.get(string);
                if (programClass1 != null) {
                    break;
                }

                programClass1 = this.findChildBeanClass(string, listMultimap);
                if (programClass1 != null) {
                    break;
                }
            }
        }

        return programClass1;
    }

    public String findEnclosingBeanClassName() {
        for (int i = 0; i < super.elementStack.size(); i++) {
            XmlElementNode xmlElementNode = (XmlElementNode) super.elementStack.peekAt(i);
            String string = xmlElementNode.getTagName();
            XmlElementNode xmlElementNode1;
            String string9;
            if (!string.equals("beans")) {
                if (!string.equals("bean")) {
                    continue;
                }

                xmlElementNode1 = xmlElementNode;
                long bj = 24856730383790L;
                string9 = "id";
            } else {
                xmlElementNode1 = xmlElementNode;
                long bb = 24856730383790L;
                string9 = "id";
            }

            String string3 = string9;
            ObservableHolder observableHolder = xmlElementNode1.getAttributeValue(string3);
            List list1 = null;
            if (observableHolder != null) {
                ProgramClass programClass1 = (ProgramClass) super.beanClasses.get(observableHolder.getValue());
                if (programClass1 != null) {
                    return programClass1.getDottedClassName();
                }
            } else {
                ObservableHolder observableHolder1 = xmlElementNode.getAttributeValue("name");
                if (observableHolder1 != null) {
                    list1 = this.splitBeanNames((String) observableHolder1.getValue());
                    Iterator iterator = list1.iterator();

                    while (iterator.hasNext()) {
                        String string1 = (String) iterator.next();
                        ProgramClass programClass2 = (ProgramClass) super.beanClasses.get(string1);
                        if (programClass2 != null) {
                            return programClass2.getDottedClassName();
                        }
                    }
                }
            }

            ObservableHolder observableHolder2 = xmlElementNode.getAttributeValue("class");
            if (observableHolder2 != null) {
                return (String) observableHolder2.getValue();
            }

            ObservableHolder observableHolder3 = xmlElementNode.getAttributeValue("factory-bean");
            if (observableHolder3 != null) {
                String string6 = (String) observableHolder3.getValue();
                ProgramClass programClass4 = (ProgramClass) super.beanClasses.get(string6);
                if (programClass4 != null) {
                    return programClass4.getDottedClassName();
                }

                xmlElementNode1 = xmlElementNode;
                long bc = 24856730383790L;
                string9 = "abstract";
            } else {
                xmlElementNode1 = xmlElementNode;
                long bd = 24856730383790L;
                string9 = "abstract";
            }

            label106:
            {
                String string4 = string9;
                ObservableHolder observableHolder4 = xmlElementNode1.getAttributeValue(string4);
                if (observableHolder == null) {
                    if (list1 == null) {
                        xmlElementNode1 = xmlElementNode;
                        long bi = 24856730383790L;
                        string9 = "parent";
                        break label106;
                    }

                    if (list1.size() <= 0) {
                        xmlElementNode1 = xmlElementNode;
                        long bh = 24856730383790L;
                        string9 = "parent";
                        break label106;
                    }
                }

                if (observableHolder4 != null) {
                    if (((String) observableHolder4.getValue()).equalsIgnoreCase("true")) {
                        if (this.childBeansByParent == null) {
                            this.childBeansByParent = new ListMultimap();
                            Iterator iterator1 = super.beanParents.entrySet().iterator();

                            while (iterator1.hasNext()) {
                                Entry entry = (Entry) iterator1.next();
                                this.childBeansByParent.addValue(entry.getValue(), entry.getKey());
                            }
                        }

                        ArrayList arrayList = new ArrayList();
                        if (observableHolder != null) {
                            String string7 = (String) observableHolder.getValue();
                            arrayList.add(string7);
                        } else {
                            arrayList.addAll(list1);
                        }

                        Iterator iterator2 = arrayList.iterator();

                        while (iterator2.hasNext()) {
                            String string2 = (String) iterator2.next();
                            ProgramClass programClass3 = this.findChildBeanClass(string2, this.childBeansByParent);
                            if (programClass3 != null) {
                                return programClass3.getDottedClassName();
                            }
                        }

                        xmlElementNode1 = xmlElementNode;
                        long be = 24856730383790L;
                        string9 = "parent";
                    } else {
                        xmlElementNode1 = xmlElementNode;
                        long bf = 24856730383790L;
                        string9 = "parent";
                    }
                } else {
                    xmlElementNode1 = xmlElementNode;
                    long bg = 24856730383790L;
                    string9 = "parent";
                }
            }

            String string5 = string9;
            ObservableHolder observableHolder5 = xmlElementNode1.getAttributeValue(string5);
            if (observableHolder5 != null) {
                String string8 = (String) observableHolder5.getValue();

                for (ProgramClass programClass5 = null; string8 != null && programClass5 == null; string8 = (String) super.beanParents.get(string8)) {
                    programClass5 = (ProgramClass) super.beanClasses.get(string8);
                    if (programClass5 != null) {
                        return programClass5.getDottedClassName();
                    }
                }
            }
        }

        return null;
    }

    @Override
    public void rewriteElement(Object object, Object object1) throws ZkmException, IOException {
        List list1 = (List) object1;
        XmlElementNode xmlElementNode = (XmlElementNode) object;
        String string = xmlElementNode.getTagName();
        String string1 = this.getOpenElementName(1);
        String string2 = this.findEnclosingBeanClassName();
        boolean predicateFlag = XmlConfigFileHandler.getPredicateFlag();
        String string3 = null;
        boolean bl = predicateFlag;
        if (string2 != null) {
            string3 = this.getRenamedClassName(string2);
        }

        if (string.equalsIgnoreCase("bean") && string1 != null && string1.equalsIgnoreCase("beans")) {
            ObservableHolder observableHolder = xmlElementNode.getAttributeValue("class");
            if (observableHolder != null) {
                String string4 = (String) observableHolder.getValue();
                if (!string4.equals(string3)) {
                    observableHolder.setValue(string3);
                }
            }
        }

        if (string.equalsIgnoreCase("beans") && string1 == null) {
            SetMultiMap setMultiMap = super.resourceFileIndex.getDefaultMethodClasses(super.resourceName);
            if (setMultiMap != null) {
                Object[] objects = new Object[0];
                Iterator iterator = setMultiMap.entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry entry = (Entry) iterator.next();
                    String string18 = (String) entry.getKey();
                    ObservableHolder observableHolder9 = xmlElementNode.getAttributeValue(string18);
                    if (observableHolder9 != null) {
                        String string21 = (String) observableHolder9.getValue();
                        MethodSignature methodSignature3 = new MethodSignature(string21, "()V");
                        Set set1 = (Set) entry.getValue();
                        StringBuilder stringBuilder = new StringBuilder();
                        String string26 = null;
                        BooleanFlag booleanFlag1 = new BooleanFlag();
                        Iterator iterator1 = set1.iterator();

                        while (iterator1.hasNext()) {
                            ProgramClass programClass1 = (ProgramClass) iterator1.next();
                            String string29 = this.getRenamedMethodName(programClass1.getDottedClassName(), methodSignature3, booleanFlag1);
                            if (stringBuilder.length() > 0) {
                                stringBuilder.append(",");
                            }

                            stringBuilder.append(programClass1.getDottedClassName());
                            if (string26 == null) {
                                if (booleanFlag1.getValue()) {
                                    string26 = string29;
                                }
                            } else if (booleanFlag1.getValue() && !string29.equals(string26)) {
                                super.messageReporter
                                        .reportError(
                                                "ERROR:",
                                                "Clashing new method names for attribute '"
                                                        + string18
                                                        + "' of tag '"
                                                        + "beans"
                                                        + "' in  XML file '"
                                                        + super.resourceName
                                                        + "' : '"
                                                        + string26
                                                        + "' '"
                                                        + string29
                                                        + "' : '"
                                                        + stringBuilder
                                                        + "'"
                                        );
                            }

                            if (bl) {
                                break;
                            }
                        }

                        if (!string21.equals(string26)) {
                            observableHolder9.setValue(string26);
                        }
                    }

                    if (bl) {
                        break;
                    }
                }
            }
        } else if (string.equalsIgnoreCase("bean") && string1 != null && string1.equalsIgnoreCase("beans")) {
            if (string3 == null) {
                list1.add(xmlElementNode);
                return;
            }

            Enumeration enumeration2 = xmlElementNode.getAttributeNames();

            while (enumeration2.hasMoreElements()) {
                String string16 = (String) enumeration2.nextElement();
                ObservableHolder observableHolder6 = xmlElementNode.getAttributeValue(string16);
                String string17 = (String) observableHolder6.getValue();
                if (!string16.equalsIgnoreCase("class")) {
                    if (string16.equalsIgnoreCase("init-method")) {
                        MethodSignature methodSignature1 = new MethodSignature(string17, "()V");
                        String string19 = this.getRenamedMethodName(string3, methodSignature1, new BooleanFlag());
                        if (!string17.equals(string19)) {
                            observableHolder6.setValue(string19);
                        }
                    } else if (string16.equalsIgnoreCase("destroy-method")) {
                        MethodSignature methodSignature2 = new MethodSignature(string17, "()V");
                        String string20 = this.getRenamedMethodName(string3, methodSignature2, new BooleanFlag());
                        if (!string17.equals(string20)) {
                            observableHolder6.setValue(string20);
                        }
                    } else if (string16.equalsIgnoreCase("factory-method")) {
                        ObservableHolder observableHolder8 = new ObservableHolder();
                        BooleanFlag booleanFlag = new BooleanFlag();
                        String string22 = this.resolveRenamedMethodName(string3, string17, 8, observableHolder8, booleanFlag);
                        if (string22 != null) {
                            if (!string17.equals(string22)) {
                                observableHolder6.setValue(string22);
                            }
                        } else if (booleanFlag.getValue()) {
                            super.messageReporter
                                    .reportError(
                                            "ERROR:",
                                            "Could not update '"
                                                    + string16
                                                    + "' attribute of tag '"
                                                    + string
                                                    + "' in  XML file '"
                                                    + super.resourceName
                                                    + "' : existing value is '"
                                                    + string17
                                                    + "' : \""
                                                    + (String) observableHolder8.getValue()
                                                    + "\""
                                    );
                        }
                    }
                }

                if (bl) {
                    break;
                }
            }
        } else if (string.equalsIgnoreCase("import") && string1 != null && string1.equalsIgnoreCase("beans")) {
            Enumeration enumeration1 = xmlElementNode.getAttributeNames();

            while (enumeration1.hasMoreElements()) {
                String string15 = (String) enumeration1.nextElement();
                ObservableHolder observableHolder5 = xmlElementNode.getAttributeValue(string15);
                String string5 = (String) observableHolder5.getValue();
                label145:
                if (string15.equalsIgnoreCase("resource")) {
                    if (string5.startsWith("classpath:") || string5.startsWith("classpath*:") || string5.startsWith("file:")) {
                        int ba = string5.indexOf(":");
                        String string6 = string5.substring(0, ba + 1);
                        String string7 = string5.substring(ba + 1);
                        if (string7.startsWith("/")) {
                            string6 = string6 + "/";
                            string7 = string7.substring(1);
                        }

                        ObservableHolder observableHolder2 = new ObservableHolder();
                        ObservableHolder observableHolder3 = new ObservableHolder();
                        String string8 = ZkmFileUtils.splitPath(string7, observableHolder2, observableHolder3);
                        String string9 = (String) observableHolder2.getValue();
                        if (string9.length() > 0) {
                            String string10 = (String) observableHolder3.getValue();
                            String string11 = this.renamePackagePath(string9);
                            if (!string11.equals(string9)) {
                                String string12 = string6 + string11 + string8 + string10;
                                observableHolder5.setValue(string12);
                            }
                        }

                        if (!bl) {
                            break label145;
                        }
                    }

                    ObservableHolder observableHolder7 = new ObservableHolder();
                    ObservableHolder observableHolder10 = new ObservableHolder();
                    ZkmFileUtils.splitPath(super.entryName, observableHolder7, observableHolder10);
                    String string23 = (String) observableHolder7.getValue();
                    String string24 = "";
                    String string25 = string5;
                    if (string25.startsWith("/")) {
                        new StringBuilder().append(string24).append("/").toString();
                        string25.substring(1);
                    }

                    String string27 = (String) observableHolder7.getValue();
                    if (string27.length() > 0) {
                        String string28 = this.translateRelativePath(string23, string5, true);
                        if (!string28.equals(string5)) {
                            observableHolder5.setValue(string28);
                        }
                    }
                }

                if (bl) {
                    break;
                }
            }
        } else if (string.equalsIgnoreCase("property") && string1 != null && string1.equalsIgnoreCase("bean")) {
            ObservableHolder observableHolder4 = xmlElementNode.getAttributeValue("name");
            if (observableHolder4 != null) {
                String string14 = (String) observableHolder4.getValue();
                if (string3 != null) {
                    this.resolveRenamedPropertyName(string3, string2, string14, observableHolder4);
                }
            }
        } else {
            Enumeration enumeration = xmlElementNode.getAttributeNames();

            while (enumeration.hasMoreElements()) {
                String string13 = (String) enumeration.nextElement();
                ObservableHolder observableHolder1 = xmlElementNode.getAttributeValue(string13);
                observableHolder1.setValue(this.updateReferenceValue((String) observableHolder1.getValue(), string13, false));
            }
        }

        list1.add(xmlElementNode);
    }

    @Override
    public List resolveImportedResources(String string, String string1) throws ZkmException, IOException {
        ArrayList arrayList = new ArrayList();
        Iterator iterator = super.resourceFileIndex.loadMatchingConfigFiles(string, string1, XmlConfigFileType.SPRING, super.messageReporter).iterator();

        while (iterator.hasNext()) {
            TextResourceContent textResourceContent = (TextResourceContent) iterator.next();

            try {
                HashSet hashSet = ZkmUtils.createHashSet();
                SpringImportedBeansXmlHandler springImportedBeansXmlHandler = new SpringImportedBeansXmlHandler(
                        textResourceContent.resourceName,
                        super.resourceFileIndex,
                        hashSet,
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
                        springImportedBeansXmlHandler
                );
                arrayList.add(textResourceContent.resourceName);
                Map map1 = springImportedBeansXmlHandler.copyBeanClasses();
                Iterator iterator1 = map1.entrySet().iterator();

                while (iterator1.hasNext()) {
                    Entry entry = (Entry) iterator1.next();
                    ProgramClass programClass1 = (ProgramClass) super.beanClasses.put(entry.getKey(), entry.getValue());
                }

                Map map2 = springImportedBeansXmlHandler.copyAbstractBeans();
                Iterator iterator2 = map2.entrySet().iterator();

                while (iterator2.hasNext()) {
                    Entry entry1 = (Entry) iterator2.next();
                    AbstractXmlBeanRef abstractXmlBeanRef = (AbstractXmlBeanRef) super.abstractBeans.put(entry1.getKey(), entry1.getValue());
                }

                Map map3 = springImportedBeansXmlHandler.copyBeanParents();
                Iterator iterator3 = map3.entrySet().iterator();

                while (iterator3.hasNext()) {
                    Entry entry2 = (Entry) iterator3.next();
                    String string2 = (String) super.beanParents.put(entry2.getKey(), entry2.getValue());
                }

                super.resourceFileIndex.addBeanClasses(super.resourceName, map1);
                super.resourceFileIndex.addBeanParents(super.resourceName, map3);
            } catch (ZkmException zkmException) {
                super.messageReporter.reportError("FILE ERROR:", "Invalid XML file '" + textResourceContent.resourceName + "' (C) : " + zkmException.getMessage());
            } catch (IOException iOException) {
                super.messageReporter
                        .reportError("FILE ERROR:", "Error reading XML file '" + textResourceContent.resourceName + "' (C) : " + iOException.getMessage());
            } catch (Exception exception) {
                super.messageReporter
                        .reportError("FILE ERROR:", "Unexpected error analyzing XML file '" + super.resourceName + "' (C) : " + exception.getMessage());
            }
        }

        return arrayList;
    }

    public SpringBeansXmlHandler(
            String string, ResourceFileIndex resourceFileIndex1, ClassMemberLookup classMemberLookup1, ClassResolver classResolver1, MessageReporter messageReporter1
    ) {
        super(string, resourceFileIndex1, classMemberLookup1, classResolver1, messageReporter1);
        Map map1 = resourceFileIndex1.getBeanClasses(string);
        if (map1 != null) {
            super.beanClasses.putAll(map1);
        }

        Map map2 = resourceFileIndex1.getBeanParents(string);
        if (map2 != null) {
            super.beanParents.putAll(map2);
        }
    }

    @Override
    public void finishAnalysis(Object object, Object object1) throws ZkmException, IOException {
        super.resourceFileIndex.addBeanParents(super.resourceName, super.beanParents);
        super.resourceFileIndex.addBeanClasses(super.resourceName, super.beanClasses);
        List list1 = super.resourceFileIndex.getImportedFiles(super.resourceName);
        if (list1 != null) {
            HashSet hashSet = ZkmUtils.createHashSet();
            UniqueWorkQueue uniqueWorkQueue = new UniqueWorkQueue();
            Iterator iterator = list1.iterator();

            while (iterator.hasNext()) {
                String string = (String) iterator.next();
                uniqueWorkQueue.enqueue(string);
                hashSet.add(string);
            }

            while (!uniqueWorkQueue.isEmpty()) {
                String string3 = (String) uniqueWorkQueue.dequeue();
                List list3 = super.resourceFileIndex.getImportingFiles(string3);
                if (list3 != null) {
                    Iterator iterator1 = list3.iterator();

                    while (iterator1.hasNext()) {
                        String string1 = (String) iterator1.next();
                        Map map1 = super.resourceFileIndex.getBeanParents(string1);
                        ResourceFileIndex resourceFileIndex1;
                        if (map1 != null) {
                            super.resourceFileIndex.addBeanParents(string3, map1);
                            resourceFileIndex1 = super.resourceFileIndex;
                        } else {
                            resourceFileIndex1 = super.resourceFileIndex;
                        }

                        Map map2 = resourceFileIndex1.getBeanClasses(string1);
                        if (map2 != null) {
                            super.resourceFileIndex.addBeanClasses(string3, map2);
                            resourceFileIndex1 = super.resourceFileIndex;
                        } else {
                            resourceFileIndex1 = super.resourceFileIndex;
                        }

                        List list2 = resourceFileIndex1.getImportedFiles(string3);
                        if (list2 != null) {
                            Iterator iterator2 = list2.iterator();

                            while (iterator2.hasNext()) {
                                String string2 = (String) iterator2.next();
                                if (hashSet.add(string2)) {
                                    uniqueWorkQueue.enqueue(string2);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public SpringBeansXmlHandler(
            String string,
            EnumerableMap enumerableMap,
            ReadOnlyMultiMap readOnlyMultiMap,
            ReadOnlyMultiMap readOnlyMultiMap1,
            ResourcePathTranslator resourcePathTranslator1,
            ResourceFileIndex resourceFileIndex1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1,
            MessageReporter messageReporter1
    ) {
        super(
                string,
                enumerableMap,
                readOnlyMultiMap,
                readOnlyMultiMap1,
                resourceFileIndex1,
                resourcePathTranslator1,
                classMemberLookup1,
                classResolver1,
                messageReporter1
        );
        Map map1 = resourceFileIndex1.getBeanClasses(string);
        if (map1 != null) {
            super.beanClasses.putAll(map1);
        }

        Map map2 = resourceFileIndex1.getBeanParents(string);
        if (map2 != null) {
            super.beanParents.putAll(map2);
        }
    }
}
