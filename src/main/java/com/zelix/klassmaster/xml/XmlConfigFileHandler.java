package com.zelix.klassmaster.xml;

import com.zelix.klassmaster.classfile.AbstractFieldInfo;
import com.zelix.klassmaster.classfile.AbstractMethodInfo;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.FieldInfo;
import com.zelix.klassmaster.classfile.FieldSignature;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.classfile.hierarchy.ClassMemberLookup;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ClassFileLoadException;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.log.MessageReporter;
import com.zelix.klassmaster.util.BooleanFlag;
import com.zelix.klassmaster.util.EnumerableMap;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ReadOnlyMultiMap;
import com.zelix.klassmaster.util.TwoKeyMap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public abstract class XmlConfigFileHandler extends XmlResourceParserBase {
    private static boolean predicateFlag;
    public TwoKeyMap methodsByOriginalName;
    public TwoKeyMap fieldsByOriginalSignature;
    public EnumerableMap classRenameMap;
    public ReadOnlyMultiMap fieldRenameMap;
    public ReadOnlyMultiMap methodRenameMap;
    public ResourcePathTranslator resourcePathTranslator;
    public final ClassMemberLookup classMemberLookup;
    public final ClassResolver classResolver;
    public final MessageReporter messageReporter;

    public static boolean getPredicateFlag() {
        return predicateFlag;
    }

    public List findMethodsByOriginalName(Object object) {
        if (this.methodRenameMap == null) {
            return null;
        }

        if (this.methodsByOriginalName == null) {
            this.methodsByOriginalName = new TwoKeyMap();
            Enumeration enumeration = this.methodRenameMap.keys();

            while (enumeration.hasMoreElements()) {
                ClassFileBase classFileBase = (ClassFileBase) enumeration.nextElement();
                EnumerableMap enumerableMap = this.methodRenameMap.getInnerMap(classFileBase);
                Iterator iterator = enumerableMap.entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry entry = (Entry) iterator.next();
                    MethodSignature methodSignature1 = (MethodSignature) entry.getKey();
                    this.methodsByOriginalName.putValue(methodSignature1.getName(), classFileBase, methodSignature1);
                }
            }
        }

        ArrayList arrayList = new ArrayList();
        Map map1 = this.methodsByOriginalName.getInnerMap(object);
        if (map1 != null) {
            Iterator iterator1 = map1.entrySet().iterator();

            while (iterator1.hasNext()) {
                Entry entry1 = (Entry) iterator1.next();
                ClassFileBase classFileBase1 = (ClassFileBase) entry1.getKey();
                MethodSignature methodSignature2 = (MethodSignature) ZkmUtils.multiMapOrSelf(classFileBase1, entry1.getValue(), this.methodRenameMap);
                AbstractMethodInfo abstractMethodInfo = classFileBase1.findMethod(methodSignature2);
                arrayList.add(abstractMethodInfo);
            }
        }

        return arrayList;
    }

    public final void addFieldReferenceByClassName(String string, String string1, Map map1, Object object) {
        for (ClassHierarchyNode classHierarchyNode = ClassHierarchyNode.findNode(ZkmUtils.dotsToSlashes(string));
             classHierarchyNode != null;
             classHierarchyNode = classHierarchyNode.getSuperclassNode()
        ) {
            FieldInfo[] fieldInfos;
            if ((fieldInfos = this.classMemberLookup.findFieldsByName(classHierarchyNode.getClassName(), string1)) != null) {
                for (int i = 0; i < fieldInfos.length; i++) {
                    map1.put(fieldInfos[i], object);
                }
                break;
            }
        }
    }

    public final String resolveRenamedMethodName(String string, String string1, int ba, ObservableHolder observableHolder, BooleanFlag booleanFlag) throws ZkmException, IOException {
        booleanFlag.setValue(false);
        if (this.methodRenameMap != null) {
            ProgramClass programClass1 = ClassHierarchyNode.findProgramClass(ZkmUtils.dotsToSlashes(string));
            HashMap hashMap = null;

            ArrayList arrayList;
            for (arrayList = new ArrayList();
                 programClass1 != null && arrayList.size() == 0;
                 programClass1 = ClassHierarchyNode.findProgramClass(programClass1.getSuperclassName())
            ) {
                programClass1.getClassName();
                EnumerableMap enumerableMap = this.methodRenameMap.getInnerMap(programClass1);
                if (enumerableMap != null) {
                    hashMap = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(enumerableMap.size()));
                    Enumeration enumeration = enumerableMap.keys();

                    while (enumeration.hasMoreElements()) {
                        MethodSignature methodSignature1 = (MethodSignature) enumeration.nextElement();
                        MethodSignature methodSignature2 = (MethodSignature) enumerableMap.get(methodSignature1);
                        hashMap.put(methodSignature2, methodSignature1);
                        if (methodSignature1.getName().equals(string1) && programClass1.findMethod(methodSignature2).hasAccessFlags(ba)) {
                            arrayList.add(methodSignature2);
                        }
                    }
                }
            }

            if (arrayList.size() == 1) {
                return ((MethodSignature) arrayList.get(0)).getName();
            }

            if (arrayList.size() >= 1) {
                HashSet hashSet = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(arrayList.size()));
                ArrayList arrayList1 = new ArrayList(arrayList.size());
                Iterator iterator = arrayList.iterator();

                while (iterator.hasNext()) {
                    MethodSignature methodSignature3 = (MethodSignature) iterator.next();
                    hashSet.add(methodSignature3.getName());
                    arrayList1.add(((MethodSignature) ZkmUtils.mapOrSelf(methodSignature3, hashMap)).formatSignature());
                }

                if (hashSet.size() == 1) {
                    return (String) hashSet.iterator().next();
                }

                booleanFlag.setValue(true);
                observableHolder.setValue(
                        "More than one method in class '"
                                + string
                                + "' or its hierarchy matches method name '"
                                + string1
                                + "' : "
                                + ZkmUtils.toQuotedListString(arrayList1)
                );
                return null;
            }
        }

        return null;
    }

    @Override
    public final void rewriteEmptyElementTag(Object object, List list1) throws ZkmException, IOException {
        XmlElementNode xmlElementNode = (XmlElementNode) object;
        super.elementStack.push(xmlElementNode);
        List list2 = list1;
        XmlElementNode xmlElementNode1 = xmlElementNode;
        this.rewriteElement(xmlElementNode1, list2);
        super.elementStack.pop();
    }

    public final boolean addTypedFieldReference(ProgramClass programClass1, ClassFileBase classFileBase, String string, Map map1, Object object) {
        FieldSignature fieldSignature = new FieldSignature(string, ZkmUtils.concatStrings(new String[]{"L", classFileBase.getClassName(), ";"}));

        for (ProgramClass programClass2 = programClass1;
             programClass2 != null;
             programClass2 = ClassHierarchyNode.findProgramClass(programClass2.getSuperclassName())
        ) {
            FieldInfo fieldInfo = programClass2.findFieldBySignature(fieldSignature);
            if (fieldInfo != null) {
                map1.put(fieldInfo, object);
                return true;
            }
        }

        return false;
    }

    public final List findMatchingMethods(String string, DescriptorMatcher descriptorMatcher, String string1, int ba) throws ZkmException, IOException {
        ArrayList arrayList = new ArrayList();
        Enumeration enumeration = this.classMemberLookup.enumerateMethodSignaturesNamed(string);
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                Iterator iterator = ((ProgramClass) enumeration.nextElement()).findMethodsByName(string).iterator();

                while (iterator.hasNext()) {
                    AbstractMethodInfo abstractMethodInfo = (AbstractMethodInfo) iterator.next();
                    if (abstractMethodInfo.hasAccessFlags(ba)
                            && descriptorMatcher.matchesDescriptor(abstractMethodInfo.getDescriptor())
                            && (string1 == null || abstractMethodInfo.getAnnotationTypes().contains(string1))) {
                        arrayList.add((MethodInfo) abstractMethodInfo);
                    }
                }
            }
        }

        return arrayList;
    }

    public final String addClassReference(String string, Map map1, ObservableHolder observableHolder, Object object) throws ZkmException, IOException {
        String string1 = (String) ZkmUtils.mapOrSelf(ZkmUtils.dotsToSlashes(string), this.classRenameMap);
        ProgramClass programClass1 = ClassHierarchyNode.findProgramClass(string1);
        if (programClass1 != null) {
            map1.put(programClass1, object);
            if (observableHolder != null) {
                observableHolder.setValue(programClass1);
            }
        }

        return ZkmUtils.slashesToDots(string1);
    }

    public final void addClassNameReferences(String string, Map map1, String string1, String string2, boolean bl) throws ZkmException, IOException {
        if (string.length() > 3
                && string.indexOf(47) == -1
                && string.indexOf(91) == -1
                && string.indexOf(59) == -1
                && string.indexOf(60) == -1
                && string.indexOf(62) == -1
                && string.indexOf(47) == -1
                && string.lastIndexOf("*") == -1
                && string.indexOf(".") > 0
                && string.lastIndexOf(".") < string.length() - 1) {
            if (!HiddenOptionFlags.NO_COLON_SPLIT_XML_NAMES && string.indexOf(58) > 0) {
                for (String string3 : string.split(":")) {
                    int ba = string3.indexOf(46);
                    if (ba > 0 && ba < string3.length() - 1 && string3.length() > 3) {
                        this.addClassReference(string3, map1, string1);
                    }
                }
            } else if (this.isReferenceAttribute(string2, bl, "class")) {
                this.addClassReference(string, map1, string1);
            }
        }
    }

    public final String lookupRenamedMethodName(String string, Object object, DescriptorMatcher descriptorMatcher, int ba) throws ZkmException, IOException {
        if (this.methodRenameMap != null) {
            for (ProgramClass programClass1 = ClassHierarchyNode.findProgramClass(ZkmUtils.dotsToSlashes(string));
                 programClass1 != null;
                 programClass1 = ClassHierarchyNode.findProgramClass(programClass1.getSuperclassName())
            ) {
                programClass1.getClassName();
                EnumerableMap enumerableMap = this.methodRenameMap.getInnerMap(programClass1);
                if (enumerableMap != null) {
                    Enumeration enumeration = enumerableMap.keys();

                    while (enumeration.hasMoreElements()) {
                        MethodSignature methodSignature1 = (MethodSignature) enumeration.nextElement();
                        MethodSignature methodSignature2 = (MethodSignature) enumerableMap.get(methodSignature1);
                        if (methodSignature1.getName().equals(object)
                                && descriptorMatcher.matchesDescriptor(methodSignature1.getDescriptor())
                                && programClass1.findMethod(methodSignature2).hasAccessFlags(ba)) {
                            return methodSignature2.getName();
                        }
                    }
                }
            }
        }

        return null;
    }

    public XmlConfigFileHandler(
            String string,
            EnumerableMap enumerableMap,
            ReadOnlyMultiMap readOnlyMultiMap,
            ReadOnlyMultiMap readOnlyMultiMap1,
            ResourcePathTranslator resourcePathTranslator1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1,
            MessageReporter messageReporter1
    ) {
        super(string);
        this.classRenameMap = enumerableMap;
        this.fieldRenameMap = readOnlyMultiMap;
        this.methodRenameMap = readOnlyMultiMap1;
        this.resourcePathTranslator = resourcePathTranslator1;
        this.classMemberLookup = classMemberLookup1;
        this.classResolver = classResolver1;
        this.messageReporter = messageReporter1;
    }

    public static void setPredicateFlag() {
        predicateFlag = false;
    }

    public final String translateRelativePath(String string, String string1, boolean bl) {
        return this.resourcePathTranslator.translateRelativePath(string, string1, bl);
    }

    public static String buildAccessorName(String string, String string1) {
        StringBuilder stringBuilder = new StringBuilder(string.length() + string1.length());
        stringBuilder.append(string);
        char ba = string1.charAt(0);
        if (!Character.isLowerCase(ba) || string1.length() != 1 && Character.isUpperCase(string1.charAt(1))) {
            stringBuilder.append(string1);
        } else {
            stringBuilder.append(Character.toUpperCase(ba));
            stringBuilder.append(string1.substring(1));
        }

        return stringBuilder.toString();
    }

    public void analyzeElement(XmlElementNode xmlElementNode, Map map1, Object object, Object object1, Object object2) throws ZkmException, IOException {
        String string = "'" + super.resourceName + "' '" + xmlElementNode.getTagName() + "' tag '";
        Enumeration enumeration = xmlElementNode.getAttributeNames();

        while (enumeration.hasMoreElements()) {
            String string1 = (String) enumeration.nextElement();
            String string2 = (String) xmlElementNode.getAttributeValue(string1).getValue();
            String string3 = string + string1 + "' attribute";
            this.addClassNameReferences(string2, map1, string3, string1, false);
        }
    }

    public final String getRenamedMethodName(String string, MethodSignature methodSignature1, BooleanFlag booleanFlag) {
        if (this.methodRenameMap != null) {
            for (ProgramClass programClass1 = ClassHierarchyNode.findProgramClass(ZkmUtils.dotsToSlashes(string));
                 programClass1 != null;
                 programClass1 = ClassHierarchyNode.findProgramClass(programClass1.getSuperclassName())
            ) {
                programClass1.getClassName();
                MethodSignature methodSignature2 = (MethodSignature) this.methodRenameMap.getValue(programClass1, methodSignature1);
                if (methodSignature2 != null) {
                    booleanFlag.setValue(true);
                    return methodSignature2.getName();
                }
            }
        }

        booleanFlag.setValue(false);
        return methodSignature1.getName();
    }

    public final ClassFileBase resolveClass(String string) throws ZkmException, IOException {
        String string1 = (String) ZkmUtils.mapOrSelf(ZkmUtils.dotsToSlashes(string), this.classRenameMap);
        ClassFileBase classFileBase = null;

        try {
            classFileBase = this.classResolver.findClassFile(string1);
        } catch (ClassFileLoadException classFileLoadException) {
        }

        return classFileBase;
    }

    @Override
    public final void analyzeStartTag(XmlElementNode xmlElementNode, Map map1, Map map2, Map map3, TwoKeyMap twoKeyMap) throws ZkmException, IOException {
        super.elementStack.push(xmlElementNode);
        this.analyzeElement(xmlElementNode, map1, map2, map3, twoKeyMap);
    }

    public static XmlConfigFileType detectConfigFileType(String string, String string1) {
        String string2 = string.toLowerCase();
        String string3 = XmlResourceParserBase.extractDoctype(string1);
        String string4 = XmlResourceParserBase.extractSchemaLocation(string1);
        if (string.equalsIgnoreCase("META-INF/ejb-jar.xml")) {
            return XmlConfigFileType.EJB_JAR;
        }

        if (string.equalsIgnoreCase("META-INF/application.xml")) {
            return XmlConfigFileType.APPLICATION;
        }

        if (string.equalsIgnoreCase("META-INF/application-client.xml")) {
            return XmlConfigFileType.CLIENT;
        }

        if (string.equalsIgnoreCase("WEB-INF/web.xml")) {
            return XmlConfigFileType.WEB;
        }

        XmlConfigFileType xmlConfigFileType;
        if (string3.indexOf("spring-beans") <= -1) {
            if (string4.indexOf("spring-beans") <= -1) {
                String string5;
                String string7;
                if (string2.endsWith(".hbm.xml")) {
                    if (string3.indexOf("hibernate-mapping") > -1) {
                        return XmlConfigFileType.HIBERNATE;
                    }

                    if (string4.indexOf("hibernate-mapping") > -1) {
                        return XmlConfigFileType.HIBERNATE;
                    }

                    string5 = string2;
                    string7 = "androidmanifest.xml";
                } else {
                    string5 = string2;
                    string7 = "androidmanifest.xml";
                }

                String string6;
                if (string5.endsWith(string7)) {
                    if (string1.indexOf("xmlns:android") > -1) {
                        return XmlConfigFileType.ANDROID;
                    }

                    string6 = string1;
                    string7 = "ibatis";
                } else {
                    string6 = string1;
                    string7 = "ibatis";
                }

                if (string6.indexOf(string7) > -1 && string3.indexOf("sql-map") > -1) {
                    return XmlConfigFileType.IBATIS;
                }

                if (string2.endsWith(".tld")) {
                    return XmlConfigFileType.TAGLIB;
                }

                if (string2.endsWith(".fxml")) {
                    return XmlConfigFileType.FXML;
                }

                if (string2.endsWith(".e4xmi")) {
                    return XmlConfigFileType.E4XMI;
                }

                if (string1.toLowerCase().indexOf("http://www.osgi.org/xmlns/blueprint") > -1) {
                    return XmlConfigFileType.BLUEPRINT;
                }

                return XmlConfigFileType.GENERIC;
            }

            xmlConfigFileType = XmlConfigFileType.SPRING;
        } else {
            xmlConfigFileType = XmlConfigFileType.SPRING;
        }

        return xmlConfigFileType;
    }

    public final String getRenamedTypeName(String string) {
        int ba = string.indexOf("[]");
        String string1;
        String string2;
        if (ba == -1) {
            string1 = "";
            string2 = string;
        } else {
            string1 = string.substring(ba);
            string2 = string.substring(0, ba);
        }

        return !string2.equals("byte")
                && !string2.equals("short")
                && !string2.equals("char")
                && !string2.equals("int")
                && !string2.equals("long")
                && !string2.equals("float")
                && !string2.equals("double")
                && !string2.equals("boolean")
                ? this.getRenamedClassName(string2) + string1
                : string;
    }

    public final void addMethodReference(String string, MethodSignature methodSignature1, Map map1, Object object) {
        if (string != null) {
            ClassHierarchyNode classHierarchyNode = ClassHierarchyNode.findNode(ZkmUtils.dotsToSlashes(string));

            while (classHierarchyNode != null) {
                ProgramClass programClass1 = classHierarchyNode.getProgramClass();
                ClassHierarchyNode classHierarchyNode1;
                if (programClass1 != null) {
                    MethodInfo methodInfo1 = this.classMemberLookup.findDeclaredMethod(programClass1, methodSignature1);
                    if (methodInfo1 != null) {
                        map1.put(methodInfo1, object);
                        break;
                    }

                    classHierarchyNode1 = classHierarchyNode.getSuperclassNode();
                } else {
                    classHierarchyNode1 = classHierarchyNode.getSuperclassNode();
                }

                classHierarchyNode = classHierarchyNode1;
            }
        }
    }

    public void addTypeReference(String string, Map map1, String string1) throws ZkmException, IOException {
        int ba = string.indexOf("[]");
        String string2;
        if (ba == -1) {
            string2 = string;
        } else {
            string2 = string.substring(0, ba);
        }

        if (!string2.equals("byte")
                && !string2.equals("short")
                && !string2.equals("char")
                && !string2.equals("int")
                && !string2.equals("long")
                && !string2.equals("float")
                && !string2.equals("double")
                && !string2.equals("boolean")) {
            this.addClassReference(string2, map1, string1);
        }
    }

    public final String addClassReference(String string, Map map1, String string1) throws ZkmException, IOException {
        String string2 = string1;
        return this.addClassReference(string, map1, (ObservableHolder) null, string2);
    }

    public static boolean alwaysTrue() {
        return true;
    }

    public final List findFieldsOfType(String string, ClassFileBase classFileBase) {
        ArrayList arrayList = new ArrayList();
        FieldInfo[] fieldInfos = this.classMemberLookup
                .findFieldsBySignature(new FieldSignature(string, ZkmUtils.concatStrings(new String[]{"L", classFileBase.getClassName(), ";"})));
        if (fieldInfos != null) {
            for (FieldInfo fieldInfo : fieldInfos) {
                if (fieldInfo.hasAccessFlags(0) && ("javafx/fxml/FXML" == null || fieldInfo.getAnnotationTypes().contains("javafx/fxml/FXML"))) {
                    arrayList.add(fieldInfo);
                }
            }
        }

        return arrayList;
    }

    public final boolean addMatchingMethodReferences(
            String string,
            String string1,
            Object object,
            DescriptorMatcher descriptorMatcher,
            String string2,
            int ba,
            Map map1,
            Map map2,
            TwoKeyMap twoKeyMap,
            String string3
    ) throws ZkmException, IOException {
        List list1 = this.findMatchingMethods(string, descriptorMatcher, string2, ba);
        Iterator iterator = list1.iterator();

        while (iterator.hasNext()) {
            MethodInfo methodInfo1 = (MethodInfo) iterator.next();
            map2.put(methodInfo1, string3);
            map1.put(methodInfo1.getOwnerProgramClass(), string3);
            if (string1 != null) {
                twoKeyMap.putValue(methodInfo1, string1, object);
            }
        }

        return list1.size() > 0;
    }

    public static XmlContentHandler createAnalysisHandler(
            String string,
            String string1,
            ResourceFileIndex resourceFileIndex1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1,
            MessageReporter messageReporter1
    ) {
        XmlConfigFileType xmlConfigFileType = detectConfigFileType(string, string1);
        switch (XmlDescriptorTypeSwitchMap.CONFIG_FILE_TYPE_SWITCH[xmlConfigFileType.ordinal()]) {
            case 1:
                return new EjbJarXmlHandler(string, classMemberLookup1, classResolver1, messageReporter1);
            case 2:
                return new ApplicationXmlHandler(string, classMemberLookup1, classResolver1, messageReporter1);
            case 3:
                return new AppClientXmlHandler(string, classMemberLookup1, classResolver1, messageReporter1);
            case 4:
                return new WebXmlReferenceHandler(string, classMemberLookup1, classResolver1, messageReporter1);
            case 5:
                return new SpringBeansXmlHandler(string, resourceFileIndex1, classMemberLookup1, classResolver1, messageReporter1);
            case 6:
                return new HibernateMappingHandler(string, classMemberLookup1, classResolver1, messageReporter1);
            case 7:
                return new AndroidManifestXmlHandler(string, classMemberLookup1, classResolver1, messageReporter1);
            case 8:
                return new IbatisXmlHandler(string, classMemberLookup1, classResolver1, messageReporter1);
            case 9:
                return new TaglibXmlUpdater(string, classMemberLookup1, classResolver1, messageReporter1);
            case 10:
                return new FxmlFileUpdater(string, classMemberLookup1, classResolver1, messageReporter1);
            case 11:
                return new E4XmiXmlHandler(string, classMemberLookup1, classResolver1, messageReporter1);
            case 12:
                return new SpringXmlUpdater(string, classMemberLookup1, classResolver1, messageReporter1);
            case 13:
                return new GenericXmlFileHandler(string, classMemberLookup1, classResolver1, messageReporter1);
            default:
                return new GenericXmlFileHandler(string, classMemberLookup1, classResolver1, messageReporter1);
        }
    }

    public List findFieldsByOriginalSignature(String string, ClassFileBase classFileBase) {
        if (this.fieldRenameMap == null) {
            return null;
        }

        if (this.fieldsByOriginalSignature == null) {
            this.fieldsByOriginalSignature = new TwoKeyMap();
            Enumeration enumeration = this.fieldRenameMap.keys();

            while (enumeration.hasMoreElements()) {
                ClassFileBase classFileBase1 = (ClassFileBase) enumeration.nextElement();
                EnumerableMap enumerableMap = this.fieldRenameMap.getInnerMap(classFileBase1);
                Iterator iterator = enumerableMap.entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry entry = (Entry) iterator.next();
                    this.fieldsByOriginalSignature.putValue(entry.getKey(), classFileBase1, entry.getValue());
                }
            }
        }

        FieldSignature fieldSignature1 = new FieldSignature(string, ZkmUtils.concatStrings(new String[]{"L", classFileBase.getOriginalClassName(), ";"}));
        ArrayList arrayList = new ArrayList();
        Map map1 = this.fieldsByOriginalSignature.getInnerMap(fieldSignature1);
        if (map1 != null) {
            Iterator iterator1 = map1.entrySet().iterator();

            while (iterator1.hasNext()) {
                Entry entry1 = (Entry) iterator1.next();
                ClassFileBase classFileBase2 = (ClassFileBase) entry1.getKey();
                FieldSignature fieldSignature = (FieldSignature) ZkmUtils.multiMapOrSelf(classFileBase2, entry1.getValue(), this.fieldRenameMap);
                AbstractFieldInfo abstractFieldInfo = classFileBase2.findField(fieldSignature);
                arrayList.add(abstractFieldInfo);
            }
        }

        return arrayList;
    }

    public final String renameDirectoryPath(String string) {
        String string1 = string.replace('.', '/');
        String string2 = this.resourcePathTranslator.renameDirectory(string1);
        return string.indexOf(46) > -1 ? string2.replace('/', '.') : string2;
    }

    @Override
    public void analyzeProcessingInstruction(Object object) {
    }

    public final boolean addHierarchyMethodReferences(
            ProgramClass programClass1,
            String string,
            String string1,
            Object object,
            DescriptorMatcher descriptorMatcher,
            int ba,
            Map map1,
            TwoKeyMap twoKeyMap,
            Object object1
    ) throws ZkmException, IOException {
        ProgramClass programClass2 = programClass1;
        boolean bl = false;

        while (programClass2 != null) {
            List list1 = programClass2.findMethodsByName(string);
            Iterator iterator = list1.iterator();

            while (iterator.hasNext()) {
                AbstractMethodInfo abstractMethodInfo = (AbstractMethodInfo) iterator.next();
                if (abstractMethodInfo.hasAccessFlags(ba) && descriptorMatcher.matchesDescriptor(abstractMethodInfo.getDescriptor())) {
                    bl = true;
                    map1.put((MethodInfo) abstractMethodInfo, object1);
                    if (string1 != null) {
                        twoKeyMap.putValue((MethodInfo) abstractMethodInfo, string1, object);
                    }
                }
            }

            if (list1.size() > 0) {
                break;
            }

            String string2 = programClass2.getSuperclassName();
            programClass2 = ClassHierarchyNode.findProgramClass(string2);
        }

        return bl;
    }

    public final ProgramClass findProgramClass(String string) {
        return ClassHierarchyNode.findProgramClass((String) ZkmUtils.mapOrSelf(ZkmUtils.dotsToSlashes(string), this.classRenameMap));
    }

    @Override
    public final void rewriteStartTag(XmlElementNode xmlElementNode, List list1) throws ZkmException, IOException {
        super.elementStack.push(xmlElementNode);
        List list2 = list1;
        XmlElementNode xmlElementNode1 = xmlElementNode;
        this.rewriteElement(xmlElementNode1, list2);
    }

    public final String getRenamedTypedFieldName(ClassFileBase classFileBase, ClassFileBase classFileBase1, String string) {
        if (this.fieldRenameMap != null) {
            FieldSignature fieldSignature = new FieldSignature(string, ZkmUtils.concatStrings(new String[]{"L", classFileBase1.getOriginalClassName(), ";"}));

            for (ClassFileBase classFileBase2 = classFileBase;
                 classFileBase2 != null;
                 classFileBase2 = ClassHierarchyNode.findProgramClass(classFileBase2.getSuperclassName())
            ) {
                FieldSignature fieldSignature1 = (FieldSignature) this.fieldRenameMap.getValue(classFileBase, fieldSignature);
                if (fieldSignature1 != null) {
                    return fieldSignature1.getName();
                }
            }

            return null;
        } else {
            return string;
        }
    }

    public final boolean addMatchingFieldReferences(ClassFileBase classFileBase, String string, Map map1, Object object) {
        List list1 = this.findFieldsOfType(string, classFileBase);
        Iterator iterator = list1.iterator();

        while (iterator.hasNext()) {
            FieldInfo fieldInfo = (FieldInfo) iterator.next();
            map1.put(fieldInfo, object);
        }

        return list1.size() > 0;
    }

    public final String getRenamedFieldName(String string, String string1) {
        if (this.fieldRenameMap != null) {
            for (ProgramClass programClass1 = ClassHierarchyNode.findProgramClass(ZkmUtils.dotsToSlashes(string));
                 programClass1 != null;
                 programClass1 = ClassHierarchyNode.findProgramClass(programClass1.getSuperclassName())
            ) {
                programClass1.getClassName();
                EnumerableMap enumerableMap = this.fieldRenameMap.getInnerMap(programClass1);
                if (enumerableMap != null) {
                    HashMap hashMap = ZkmUtils.createHashMap();
                    Enumeration enumeration = enumerableMap.keys();

                    while (enumeration.hasMoreElements()) {
                        FieldSignature fieldSignature = (FieldSignature) enumeration.nextElement();
                        FieldSignature fieldSignature1 = (FieldSignature) enumerableMap.get(fieldSignature);
                        hashMap.put(fieldSignature1, fieldSignature);
                        if (fieldSignature.getName().equals(string1)) {
                            return fieldSignature1.getName();
                        }
                    }
                }
            }
        }

        return string1;
    }

    public XmlConfigFileHandler(String string, ClassMemberLookup classMemberLookup1, ClassResolver classResolver1, MessageReporter messageReporter1) {
        super(string);
        this.classMemberLookup = classMemberLookup1;
        this.classResolver = classResolver1;
        this.messageReporter = messageReporter1;
    }

    public final String resolveRenamedMethodByName(String string, DescriptorMatcher descriptorMatcher, ObservableHolder observableHolder) throws ZkmException, IOException {
        if (this.methodRenameMap == null) {
            return string;
        }

        ArrayList arrayList = new ArrayList();
        Iterator iterator = this.findMethodsByOriginalName(string).iterator();

        while (iterator.hasNext()) {
            AbstractMethodInfo abstractMethodInfo = (AbstractMethodInfo) iterator.next();
            if (abstractMethodInfo.hasAccessFlags(0)
                    && descriptorMatcher.matchesDescriptor(abstractMethodInfo.getDescriptor())
                    && ("javafx/fxml/FXML" == null || abstractMethodInfo.getAnnotationTypes().contains("javafx/fxml/FXML"))) {
                arrayList.add(abstractMethodInfo);
            }
        }

        int ba = arrayList.size();
        if (ba == 0) {
            observableHolder.setValue("No method found matching '" + string + "'");
            return null;
        }

        if (ba == 1) {
            return ((AbstractMethodInfo) arrayList.get(0)).getSourceName();
        }

        HashSet hashSet = ZkmUtils.createHashSet();
        Iterator iterator1 = arrayList.iterator();

        while (iterator1.hasNext()) {
            AbstractMethodInfo abstractMethodInfo1 = (AbstractMethodInfo) iterator1.next();
            hashSet.add(abstractMethodInfo1.getSourceName());
        }

        if (hashSet.size() == 1) {
            return (String) hashSet.iterator().next();
        }

        observableHolder.setValue("More than one candidate method found matching '" + string + "' : " + ZkmUtils.toQuotedListString(hashSet));
        return null;
    }

    public final String getPropertyNameFromAccessor(String string, String string1, String string2) {
        if (string1.startsWith(string) && string1.length() > string.length()) {
            String string3 = MethodSignature.toPropertyName(string1, string);
            return string2 != null && string3.toUpperCase().equals(string2.toUpperCase()) ? string2 : string3;
        } else {
            return null;
        }
    }

    public final boolean addHierarchyMethodReferences(
            ProgramClass programClass1,
            String string,
            String string1,
            Object object,
            DescriptorMatcher descriptorMatcher,
            Map map1,
            TwoKeyMap twoKeyMap,
            String string2
    ) throws ZkmException, IOException {
        return this.addHierarchyMethodReferences(programClass1, string, string1, object, descriptorMatcher, 0, map1, twoKeyMap, string2);
    }

    public final String resolveRenamedFieldName(ClassFileBase classFileBase, String string, ObservableHolder observableHolder) throws ZkmException, IOException {
        List list1 = this.findFieldsByOriginalSignature(string, classFileBase);
        if (list1 == null) {
            observableHolder.setValue("No field method found matching '" + string + "' (2)");
            return null;
        }

        ArrayList arrayList = new ArrayList();
        Iterator iterator = list1.iterator();

        while (iterator.hasNext()) {
            AbstractFieldInfo abstractFieldInfo = (AbstractFieldInfo) iterator.next();
            if (abstractFieldInfo.hasAccessFlags(0) && ("javafx/fxml/FXML" == null || abstractFieldInfo.getAnnotationTypes().contains("javafx/fxml/FXML"))) {
                arrayList.add(abstractFieldInfo);
            }
        }

        int ba = arrayList.size();
        if (ba == 0) {
            observableHolder.setValue("No field method found matching '" + string + "' (1)");
            return null;
        }

        if (ba == 1) {
            return ((AbstractFieldInfo) arrayList.get(0)).getSourceName();
        }

        HashSet hashSet = ZkmUtils.createHashSet();
        Iterator iterator1 = arrayList.iterator();

        while (iterator1.hasNext()) {
            AbstractFieldInfo abstractFieldInfo1 = (AbstractFieldInfo) iterator1.next();
            hashSet.add(abstractFieldInfo1.getSourceName());
        }

        if (hashSet.size() == 1) {
            return (String) hashSet.iterator().next();
        }

        observableHolder.setValue("More than one candidate method found matching '" + string + "' : " + ZkmUtils.toQuotedListString(hashSet));
        return null;
    }

    public boolean isReferenceAttribute(String string, boolean bl, String string1) {
        if (string == null || string.length() == 0) {
            return false;
        }

        if (string.endsWith("name")) {
            if (string.toLowerCase().indexOf(string1) > -1) {
                return true;
            }

            String string2;
            if (bl) {
                string2 = this.getOpenElementName(1);
            } else {
                string2 = this.getCurrentElementName();
            }

            return string2 != null && string2.toLowerCase().indexOf(string1) > -1;
        } else {
            return string.equals("ejb-link") ? false : string.indexOf("jndi") == -1;
        }
    }

    public final String getRenamedClassName(String string) {
        return ((String) ZkmUtils.mapOrSelf(string.replace('.', '/'), this.classRenameMap)).replace('/', '.');
    }

    public final void addFieldReference(ProgramClass programClass1, String string, Map map1, Object object) {
        for (ClassHierarchyNode classHierarchyNode = ClassHierarchyNode.findNode(programClass1.getClassName());
             classHierarchyNode != null;
             classHierarchyNode = classHierarchyNode.getSuperclassNode()
        ) {
            FieldInfo[] fieldInfos;
            if ((fieldInfos = this.classMemberLookup.findFieldsByName(classHierarchyNode.getClassName(), string)) != null) {
                for (int i = 0; i < fieldInfos.length; i++) {
                    map1.put(fieldInfos[i], object);
                }
                break;
            }
        }
    }

    public static XmlContentHandler createUpdateHandler(
            String string,
            String string1,
            EnumerableMap enumerableMap,
            EnumerableMap enumerableMap1,
            ReadOnlyMultiMap readOnlyMultiMap,
            ReadOnlyMultiMap readOnlyMultiMap1,
            ResourcePathTranslator resourcePathTranslator1,
            ResourceFileIndex resourceFileIndex1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1,
            MessageReporter messageReporter1
    ) {
        XmlConfigFileType xmlConfigFileType = detectConfigFileType(string, string1);
        switch (XmlDescriptorTypeSwitchMap.CONFIG_FILE_TYPE_SWITCH[xmlConfigFileType.ordinal()]) {
            case 1:
                return new EjbJarXmlHandler(
                        string, enumerableMap, readOnlyMultiMap, readOnlyMultiMap1, resourcePathTranslator1, classMemberLookup1, classResolver1, messageReporter1
                );
            case 2:
                return new ApplicationXmlHandler(
                        string, enumerableMap, readOnlyMultiMap, readOnlyMultiMap1, resourcePathTranslator1, classMemberLookup1, classResolver1, messageReporter1
                );
            case 3:
                return new AppClientXmlHandler(
                        string, enumerableMap, readOnlyMultiMap, readOnlyMultiMap1, resourcePathTranslator1, classMemberLookup1, classResolver1, messageReporter1
                );
            case 4:
                return new WebXmlReferenceHandler(
                        string, enumerableMap, readOnlyMultiMap, readOnlyMultiMap1, resourcePathTranslator1, classMemberLookup1, classResolver1, messageReporter1
                );
            case 5:
                return new SpringBeansXmlHandler(
                        string,
                        enumerableMap,
                        readOnlyMultiMap,
                        readOnlyMultiMap1,
                        resourcePathTranslator1,
                        resourceFileIndex1,
                        classMemberLookup1,
                        classResolver1,
                        messageReporter1
                );
            case 6:
                return new HibernateMappingHandler(
                        string, enumerableMap, readOnlyMultiMap, readOnlyMultiMap1, resourcePathTranslator1, classMemberLookup1, classResolver1, messageReporter1
                );
            case 7:
                return new AndroidManifestXmlHandler(
                        string, enumerableMap, readOnlyMultiMap, readOnlyMultiMap1, resourcePathTranslator1, classMemberLookup1, classResolver1, messageReporter1
                );
            case 8:
                return new IbatisXmlHandler(
                        string, enumerableMap, readOnlyMultiMap, readOnlyMultiMap1, resourcePathTranslator1, classMemberLookup1, classResolver1, messageReporter1
                );
            case 9:
                return new TaglibXmlUpdater(
                        string, enumerableMap, readOnlyMultiMap, readOnlyMultiMap1, resourcePathTranslator1, classMemberLookup1, classResolver1, messageReporter1
                );
            case 10:
                return new FxmlFileUpdater(
                        string,
                        enumerableMap,
                        enumerableMap1,
                        readOnlyMultiMap,
                        readOnlyMultiMap1,
                        resourcePathTranslator1,
                        classMemberLookup1,
                        classResolver1,
                        messageReporter1
                );
            case 11:
                return new E4XmiXmlHandler(
                        string, enumerableMap, readOnlyMultiMap, readOnlyMultiMap1, resourcePathTranslator1, classMemberLookup1, classResolver1, messageReporter1
                );
            case 12:
                return new SpringXmlUpdater(
                        string, enumerableMap, readOnlyMultiMap, readOnlyMultiMap1, resourcePathTranslator1, classMemberLookup1, classResolver1, messageReporter1
                );
            case 13:
                return new GenericXmlFileHandler(
                        string, enumerableMap, readOnlyMultiMap, readOnlyMultiMap1, resourcePathTranslator1, classMemberLookup1, classResolver1, messageReporter1
                );
            default:
                return new GenericXmlFileHandler(
                        string, enumerableMap, readOnlyMultiMap, readOnlyMultiMap1, resourcePathTranslator1, classMemberLookup1, classResolver1, messageReporter1
                );
        }
    }

    public final String updateReferenceValue(String string, String string1, boolean bl) throws IOException {
        String string2 = string;
        if (string.length() >= 3 && string1 != null && string1.length() > 0) {
            boolean bl2;
            if (string.indexOf(58) == -1) {
                if (string.indexOf(33) == -1) {
                    if (string.indexOf(60) == -1) {
                        if (string.indexOf(62) == -1) {
                            if (string.indexOf(38) == -1) {
                                if (string.indexOf(35) == -1) {
                                    if (string.indexOf(91) == -1) {
                                        if (string.indexOf(61) == -1) {
                                            int ba = string.indexOf(47, 1);
                                            if (ba > -1) {
                                                if (ba < string.length() - 1 && this.isReferenceAttribute(string1, bl, "file")) {
                                                    string2 = this.resourcePathTranslator.translateResourcePath(string);
                                                    return string2;
                                                }
                                            } else if (string.indexOf(47) == -1) {
                                                int bb = string.indexOf(46);
                                                if (bb > 0 && bb < string.length() - 1 && this.isReferenceAttribute(string1, bl, "class")) {
                                                    string2 = this.getRenamedClassName(string);
                                                }

                                                return string2;
                                            }

                                            return string2;
                                        }

                                        bl2 = HiddenOptionFlags.NO_COLON_SPLIT_XML_NAMES;
                                    } else {
                                        bl2 = HiddenOptionFlags.NO_COLON_SPLIT_XML_NAMES;
                                    }
                                } else {
                                    bl2 = HiddenOptionFlags.NO_COLON_SPLIT_XML_NAMES;
                                }
                            } else {
                                bl2 = HiddenOptionFlags.NO_COLON_SPLIT_XML_NAMES;
                            }
                        } else {
                            bl2 = HiddenOptionFlags.NO_COLON_SPLIT_XML_NAMES;
                        }
                    } else {
                        bl2 = HiddenOptionFlags.NO_COLON_SPLIT_XML_NAMES;
                    }
                } else {
                    bl2 = HiddenOptionFlags.NO_COLON_SPLIT_XML_NAMES;
                }
            } else {
                bl2 = HiddenOptionFlags.NO_COLON_SPLIT_XML_NAMES;
            }

            if (!bl2
                    && string1.equals("class")
                    && string.indexOf(58) > 3
                    && string.indexOf(33) == -1
                    && string.indexOf(60) == -1
                    && string.indexOf(62) == -1
                    && string.indexOf(38) == -1
                    && string.indexOf(35) == -1
                    && string.indexOf(91) == -1
                    && string.indexOf(61) == -1) {
                boolean bl1 = true;
                StringBuilder stringBuilder = new StringBuilder();
                String[] strings = string.split(":");

                for (int i = 0; i < strings.length && bl1; i++) {
                    String string3 = strings[i];
                    int bd = string3.indexOf(46);
                    if (bd > 0 && bd < string3.length() - 1 && string3.length() > 3) {
                        String string4 = this.getRenamedClassName(string3);
                        stringBuilder.append(string4);
                        if (i < strings.length - 1) {
                            stringBuilder.append(":");
                        }
                    } else {
                        bl1 = false;
                    }
                }

                if (bl1) {
                    string2 = stringBuilder.toString();
                }
            }
        }

        return string2;
    }

    public final String renamePackagePath(String string) {
        return this.resourcePathTranslator.renamePackagePath(string);
    }

    public void rewriteElement(Object object, Object object1) throws ZkmException, IOException {
        XmlElementNode xmlElementNode = (XmlElementNode) object;
        alwaysTrue();
        Enumeration enumeration = xmlElementNode.getAttributeNames();

        while (enumeration.hasMoreElements()) {
            String string = (String) enumeration.nextElement();
            ObservableHolder observableHolder = xmlElementNode.getAttributeValue(string);
            observableHolder.setValue(this.updateReferenceValue((String) observableHolder.getValue(), string, false));
        }

        ((List) object1).add(xmlElementNode);
    }

    @Override
    public final void analyzeEmptyElementTag(XmlElementNode xmlElementNode, Map map1, Map map2, Map map3, TwoKeyMap twoKeyMap) throws ZkmException, IOException {
        super.elementStack.push(xmlElementNode);
        this.analyzeElement(xmlElementNode, map1, map2, map3, twoKeyMap);
        super.elementStack.pop();
    }

    static {
        setPredicateFlag();
    }
}
