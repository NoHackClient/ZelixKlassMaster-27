package com.zelix.klassmaster.classfile.hierarchy;

import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.obfuscator.trim.TrimProcessor;
import com.zelix.klassmaster.util.ChangeObservable;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ZkmAssert;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

public class ClassHierarchy extends ChangeObservable {
    public Set permittedSubclassHolders;
    public List orderedNodes;
    public ListMultimap nestMembersByHost;
    public ListMultimap subclassesBySuperclass;
    public List rootProgramInterfaces;
    public List rootNodes;
    public Set innerClassesHolders;
    public boolean hasLibraryClasses = false;

    public Enumeration enumerateInnerClassesHolders() {
        return Collections.enumeration(this.innerClassesHolders);
    }

    public void rehashCollections() {
        if (this.innerClassesHolders != null && this.innerClassesHolders.size() > 0) {
            HashSet hashSet = ZkmUtils.createHashSet(this.innerClassesHolders.size());
            Iterator iterator = this.innerClassesHolders.iterator();

            while (iterator.hasNext()) {
                ClassHierarchyNode classHierarchyNode = (ClassHierarchyNode) iterator.next();
                hashSet.add(classHierarchyNode);
            }

            this.innerClassesHolders = hashSet;
        }

        if (this.permittedSubclassHolders != null && this.permittedSubclassHolders.size() > 0) {
            HashSet hashSet1 = ZkmUtils.createHashSet(this.permittedSubclassHolders.size());
            Iterator iterator1 = this.permittedSubclassHolders.iterator();

            while (iterator1.hasNext()) {
                ClassHierarchyNode classHierarchyNode2 = (ClassHierarchyNode) iterator1.next();
                hashSet1.add(classHierarchyNode2);
            }

            this.permittedSubclassHolders = hashSet1;
        }

        if (this.nestMembersByHost != null && !this.nestMembersByHost.isEmpty()) {
            ListMultimap listMultimap = new ListMultimap();
            Iterator iterator2 = this.nestMembersByHost.entrySet().iterator();

            while (iterator2.hasNext()) {
                Entry entry = (Entry) iterator2.next();
                listMultimap.appendValues(entry.getKey(), (Collection) entry.getValue());
            }

            this.nestMembersByHost = listMultimap;
        }

        if (this.subclassesBySuperclass != null) {
            ListMultimap listMultimap1 = new ListMultimap(this.subclassesBySuperclass.getKeyCount());
            Enumeration enumeration = ClassHierarchyNode.enumerateAllNodes();

            while (enumeration.hasMoreElements()) {
                ClassHierarchyNode classHierarchyNode3 = (ClassHierarchyNode) enumeration.nextElement();
                ClassHierarchyNode classHierarchyNode1 = classHierarchyNode3.getSuperclassNode();
                if (classHierarchyNode1 != null) {
                    listMultimap1.addValue(classHierarchyNode1, classHierarchyNode3);
                }
            }

            this.subclassesBySuperclass = listMultimap1;
        }
    }

    public void collectTopProgramNodes(ClassHierarchyNode classHierarchyNode, List list1) {
        if (!classHierarchyNode.isProgramClass()) {
            Enumeration enumeration = classHierarchyNode.enumerateSubclasses();
            if (enumeration != null) {
                while (enumeration.hasMoreElements()) {
                    ClassHierarchyNode classHierarchyNode1 = (ClassHierarchyNode) enumeration.nextElement();
                    this.collectTopProgramNodes(classHierarchyNode1, list1);
                }
            }
        } else {
            list1.add(classHierarchyNode);
        }
    }

    public void checkForHierarchyLoops(ClassFileBase[] classFileBases) throws ZkmException, IOException {
        for (int i = 0; i < classFileBases.length; i++) {
            LinkedHashSet linkedHashSet = new LinkedHashSet();
            ClassFileBase classFileBase = classFileBases[i];
            linkedHashSet.add(classFileBase);
            ClassHierarchyNode classHierarchyNode = ClassHierarchyNode.findNode(classFileBase.getClassName());

            for (ClassHierarchyNode classHierarchyNode1 = classHierarchyNode.getSuperclassNode();
                 classHierarchyNode1 != null;
                 classHierarchyNode1 = classHierarchyNode1.getSuperclassNode()
            ) {
                ClassFileBase classFileBase1 = classHierarchyNode1.getClassFile();
                if (!linkedHashSet.add(classFileBase1)) {
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append("Class '" + classFileBase.getDottedClassName() + "' has a loop in its inheritance hierarchy : ");
                    Iterator iterator = linkedHashSet.iterator();

                    while (iterator.hasNext()) {
                        stringBuffer.append(((ClassFileBase) iterator.next()).getDisplayLocationName());
                        stringBuffer.append(", ");
                    }

                    stringBuffer.append(classFileBase1.getDisplayLocationName());
                    throw new ZkmProcessingException(stringBuffer.toString());
                }
            }
        }

        for (int i = 0; i < classFileBases.length; i++) {
            LinkedHashSet linkedHashSet1 = new LinkedHashSet();
            ObservableHolder observableHolder = new ObservableHolder();
            ClassHierarchyNode classHierarchyNode2 = ClassHierarchyNode.findNode(classFileBases[i].getClassName());
            if (!this.checkImplementationLoop(classHierarchyNode2, linkedHashSet1, observableHolder)) {
                throw new ZkmProcessingException((String) observableHolder.getValue());
            }
        }
    }

    public boolean checkImplementationLoop(ClassHierarchyNode classHierarchyNode, LinkedHashSet linkedHashSet, ObservableHolder observableHolder) throws ZkmException, IOException {
        if (linkedHashSet.add(classHierarchyNode)) {
            Enumeration enumeration = classHierarchyNode.enumerateInterfaces();
            if (enumeration != null) {
                while (enumeration.hasMoreElements()) {
                    ClassHierarchyNode classHierarchyNode1 = (ClassHierarchyNode) enumeration.nextElement();
                    boolean bl = this.checkImplementationLoop(classHierarchyNode1, ZkmUtils.copyLinkedHashSet(linkedHashSet), observableHolder);
                    if (!bl) {
                        return false;
                    }
                }
            }

            return true;
        } else {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Loop in it implementation hierarchy : ");
            Iterator iterator = linkedHashSet.iterator();

            while (iterator.hasNext()) {
                ClassFileBase classFileBase = ((ClassHierarchyNode) iterator.next()).getClassFile();
                stringBuffer.append(classFileBase.getDisplayLocationName());
                stringBuffer.append(", ");
            }

            stringBuffer.append(classHierarchyNode.getClassFile().getDisplayLocationName());
            observableHolder.setValue(stringBuffer.toString());
            return false;
        }
    }

    public ClassHierarchy(ProgramClass[] programClass1) throws ZkmException, IOException {
        this.build(programClass1);
    }

    public void trimNestAttributes(TrimProcessor trimProcessor1) {
        Iterator iterator = this.nestMembersByHost.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            ProgramClass programClass1 = ((ClassHierarchyNode) entry.getKey()).getProgramClass();
            if (programClass1 != null) {
                programClass1.trimNestMembersAttribute(trimProcessor1);
                if (programClass1.hasVersionedVariants()) {
                    Iterator iterator1 = programClass1.getVersionedVariants().iterator();

                    while (iterator1.hasNext()) {
                        ClassFileBase classFileBase = (ClassFileBase) iterator1.next();
                        ((ProgramClass) classFileBase).trimNestMembersAttribute(trimProcessor1);
                    }
                }
            }

            Iterator iterator3 = ((List) entry.getValue()).iterator();

            while (iterator3.hasNext()) {
                ClassHierarchyNode classHierarchyNode = (ClassHierarchyNode) iterator3.next();
                ProgramClass programClass2 = classHierarchyNode.getProgramClass();
                if (programClass2 != null) {
                    programClass2.trimNestHostAttribute(trimProcessor1);
                    if (programClass2.hasVersionedVariants()) {
                        Iterator iterator2 = programClass2.getVersionedVariants().iterator();

                        while (iterator2.hasNext()) {
                            ((ProgramClass) ((ClassFileBase) iterator2.next())).trimNestHostAttribute(trimProcessor1);
                        }
                    }
                }
            }
        }
    }

    public void trimPermittedSubclasses(TrimProcessor trimProcessor1) {
        Iterator iterator = this.permittedSubclassHolders.iterator();

        while (iterator.hasNext()) {
            ProgramClass programClass1 = ((ClassHierarchyNode) iterator.next()).getProgramClass();
            if (programClass1 != null) {
                programClass1.trimPermittedSubclasses(trimProcessor1);
                if (programClass1.hasVersionedVariants()) {
                    Iterator iterator1 = programClass1.getVersionedVariants().iterator();

                    while (iterator1.hasNext()) {
                        ((ProgramClass) ((ClassFileBase) iterator1.next())).trimPermittedSubclasses(trimProcessor1);
                    }
                }
            }
        }
    }

    public List getTopProgramNodes() {
        ArrayList arrayList = new ArrayList();
        int ba = 0;
        int bb = 0;

        for (List list1 = this.rootNodes; bb < list1.size(); list1 = this.rootNodes) {
            this.collectTopProgramNodes((ClassHierarchyNode) this.rootNodes.get(ba), arrayList);
            bb = ++ba;
        }

        return arrayList;
    }

    public void visitSubtree(HierarchyNodeVisitor hierarchyNodeVisitor, Enumeration enumeration, int ba, Object object) {
        int bb = ba;
        if (enumeration != null) {
            bb++;

            while (enumeration.hasMoreElements()) {
                ClassHierarchyNode classHierarchyNode = (ClassHierarchyNode) enumeration.nextElement();
                hierarchyNodeVisitor.visitNode(classHierarchyNode, bb, (ArrayList) object);
                this.visitSubtree(hierarchyNodeVisitor, this.subclassesBySuperclass.valuesOf(classHierarchyNode), bb, object);
            }
        }
    }

    public ClassHierarchyNode getNodeAt(int ba) {
        return (ClassHierarchyNode) this.orderedNodes.get(ba);
    }

    public void collectTopLoadedNodes(ClassHierarchyNode classHierarchyNode, List list1) {
        if (classHierarchyNode.hasNoClassFile()) {
            Enumeration enumeration = classHierarchyNode.enumerateSubclasses();
            if (enumeration != null) {
                while (enumeration.hasMoreElements()) {
                    ClassHierarchyNode classHierarchyNode1 = (ClassHierarchyNode) enumeration.nextElement();
                    this.collectTopLoadedNodes(classHierarchyNode1, list1);
                }
            }
        } else {
            list1.add(classHierarchyNode);
        }
    }

    public String buildHierarchyGapMessage(ClassFileBase classFileBase, String string, ClassFileBase classFileBase1, ClassFileBase classFileBase2) {
        return "Class '"
                + classFileBase.getDisplayLocationName()
                + "' is "
                + string
                + " by '"
                + classFileBase1.getDisplayLocationName()
                + "' which has not been opened inside "
                + "Zelix KlassMaster"
                + ". '"
                + classFileBase1.getDottedClassName()
                + "' is then extended or implemented by '"
                + classFileBase2.getDisplayLocationName()
                + "'. There must be no gaps in the opened inheritance and implementation hierarchies. Either '"
                + classFileBase.getDottedClassName()
                + "' should not be opened or '"
                + classFileBase1.getDisplayLocationName()
                + "' must also be opened for obfuscation. (A)";
    }

    public boolean validateSupertypes(
            ClassFileBase classFileBase, ClassFileBase classFileBase1, ObservableHolder observableHolder, ClassResolver classResolver1, boolean bl
    ) throws ZkmException, IOException {
        if (classFileBase1.getClassName().equals("java/lang/Object")) {
            return true;
        }

        String string = classFileBase1.getSuperclassName();
        if (string != null) {
            ClassFileBase classFileBase2 = ClassHierarchyNode.findClassFile(string);
            if (classFileBase2 != null) {
                if (classFileBase2.isInterface()) {
                    observableHolder.setValue(
                            "Class '"
                                    + classFileBase1.getDisplayLocationName()
                                    + "' extends '"
                                    + classFileBase2.getDisplayLocationName()
                                    + "' but '"
                                    + classFileBase2.getSimpleName()
                                    + "' is now an interface."
                    );
                    return false;
                }

                if (bl) {
                    boolean bl1 = this.validateSupertypes(classFileBase2, classFileBase2, observableHolder, classResolver1, true);
                    if (!bl1) {
                        return false;
                    }
                } else if (HiddenOptionFlags.TEST_HIERARCHY) {
                    observableHolder.setValue(this.buildHierarchyGapMessage(classFileBase2, "extended", classFileBase1, classFileBase));
                    return false;
                }
            } else {
                String string1 = "looking for the superclass of '" + classFileBase1.getDisplayLocationName() + "'";
                ClassFileBase classFileBase3 = classResolver1.getClassFile(string, string1);
                boolean bl2 = this.validateSupertypes(classFileBase, classFileBase3, observableHolder, classResolver1, false);
                if (!bl2) {
                    return false;
                }
            }
        }

        String[] strings = classFileBase1.getInterfaceNames();

        for (int i = 0; i < strings.length; i++) {
            String string2 = strings[i];
            ClassFileBase classFileBase5 = ClassHierarchyNode.findClassFile(string2);
            if (classFileBase5 != null) {
                if (!classFileBase5.isInterface()) {
                    observableHolder.setValue(
                            "Class '"
                                    + classFileBase1.getDisplayLocationName()
                                    + "' implements '"
                                    + classFileBase5.getDisplayLocationName()
                                    + "' but '"
                                    + classFileBase5.getSimpleName()
                                    + "' is no longer an interface."
                    );
                    return false;
                }

                if (bl) {
                    boolean bl3 = this.validateSupertypes(classFileBase5, classFileBase5, observableHolder, classResolver1, true);
                    if (!bl3) {
                        return false;
                    }
                } else if (HiddenOptionFlags.TEST_HIERARCHY) {
                    observableHolder.setValue(this.buildHierarchyGapMessage(classFileBase5, "implemented", classFileBase1, classFileBase));
                    return false;
                }
            } else {
                String string3 = "looking for an implemented interface of '" + classFileBase1.getLocationName() + "'";
                ClassFileBase classFileBase4 = classResolver1.getClassFile(string2, string3);
                if (!this.validateSupertypes(classFileBase, classFileBase4, observableHolder, classResolver1, false)) {
                    return false;
                }
            }
        }

        return true;
    }

    public void removeHoldersWithoutInnerClasses() {
        Iterator iterator = this.innerClassesHolders.iterator();

        while (iterator.hasNext()) {
            ProgramClass programClass1 = ((ClassHierarchyNode) iterator.next()).getProgramClass();
            if (programClass1 != null && !programClass1.isInnerClassesTrimPending()) {
                iterator.remove();
            }
        }
    }

    public List getRootProgramInterfaces() {
        return this.rootProgramInterfaces;
    }

    public void trimInnerClassesAttributes(TrimProcessor trimProcessor1) {
        Iterator iterator = this.innerClassesHolders.iterator();

        while (iterator.hasNext()) {
            ProgramClass programClass1 = ((ClassHierarchyNode) iterator.next()).getProgramClass();
            if (programClass1 != null) {
                programClass1.pruneInnerClassesAttribute(trimProcessor1);
                if (programClass1.hasVersionedVariants()) {
                    Iterator iterator1 = programClass1.getVersionedVariants().iterator();

                    while (iterator1.hasNext()) {
                        ((ProgramClass) ((ClassFileBase) iterator1.next())).pruneInnerClassesAttribute(trimProcessor1);
                    }
                }
            }
        }
    }

    public List getTopLoadedNodes() {
        ArrayList arrayList = new ArrayList();
        int ba = 0;
        int bb = 0;

        for (List list1 = this.rootNodes; bb < list1.size(); list1 = this.rootNodes) {
            this.collectTopLoadedNodes((ClassHierarchyNode) this.rootNodes.get(ba), arrayList);
            bb = ++ba;
        }

        return arrayList;
    }

    public boolean validateLoadedHierarchies(ObservableHolder observableHolder, ClassResolver classResolver1) throws ZkmException, IOException {
        List list1 = this.getTopLoadedNodes();

        for (int i = 0; i < list1.size(); i++) {
            ClassFileBase classFileBase = ((ClassHierarchyNode) list1.get(i)).getClassFile();
            if (!this.validateSupertypes(classFileBase, classFileBase, observableHolder, classResolver1, true)) {
                return false;
            }
        }

        return true;
    }

    public void build(ClassFileBase[] classFileBases) throws ZkmException, IOException {
        this.subclassesBySuperclass = new ListMultimap();
        ListMultimap listMultimap = new ListMultimap();
        this.rootNodes = new ArrayList();
        this.rootProgramInterfaces = new ArrayList();
        this.innerClassesHolders = ZkmUtils.createHashSet();
        this.nestMembersByHost = new ListMultimap();
        this.permittedSubclassHolders = ZkmUtils.createHashSet();
        this.orderedNodes = new ArrayList();
        HashMap hashMap = ZkmUtils.createHashMap();

        for (int i = 0; i < classFileBases.length; i++) {
            ClassFileBase classFileBase = classFileBases[i];
            hashMap.put(classFileBase.getClassName(), classFileBase);
            if (!classFileBase.isProgramClass()) {
                this.hasLibraryClasses = true;
            }
        }

        HashMap hashMap1 = ZkmUtils.createHashMap();
        ClassHierarchyNode.disposeAllNodes();
        HashMap hashMap2 = ZkmUtils.createHashMap();

        for (int i = 0; i < classFileBases.length; i++) {
            ClassHierarchyNode classHierarchyNode = ClassHierarchyNode.getOrCreateNode(classFileBases[i].getClassName(), classFileBases[i]);
            ClassHierarchyNode classHierarchyNode1;
            if (!classFileBases[i].isModule()) {
                classHierarchyNode1 = ClassHierarchyNode.getOrCreateNode(
                        classFileBases[i].getSuperclassName(), (ClassFileBase) hashMap.get(classFileBases[i].getSuperclassName())
                );
            } else {
                classHierarchyNode1 = ClassHierarchyNode.getOrCreateNode("java/lang/Object", (ClassFileBase) hashMap.get("java/lang/Object"));
            }

            classHierarchyNode.setSuperclassNode(classHierarchyNode1);
            classHierarchyNode1.addSubclass(classHierarchyNode);
            this.subclassesBySuperclass.addValue(classHierarchyNode1, classHierarchyNode);
            hashMap2.put(classHierarchyNode, classHierarchyNode1);
            if (classFileBases[i].getInterfaceCount() > 0) {
                for (int j = 0; j < classFileBases[i].getInterfaceCount(); j++) {
                    String string = classFileBases[i].getInterfaceName(j);
                    ClassHierarchyNode classHierarchyNode2 = ClassHierarchyNode.getOrCreateNode(string, (ClassFileBase) hashMap.get(string));
                    listMultimap.addValue(classHierarchyNode, classHierarchyNode2);
                    classHierarchyNode.addInterface(classHierarchyNode2);
                    classHierarchyNode2.addImplementor(classHierarchyNode);
                }
            }

            if (classFileBases[i].isInnerClassesTrimPending()) {
                this.innerClassesHolders.add(classHierarchyNode);
            }

            if (classFileBases[i].hasPermittedSubclasses()) {
                this.permittedSubclassHolders.add(classHierarchyNode);
            }

            if (classFileBases[i].isInnerClass()) {
                String string2 = classFileBases[i].getEnclosingClassName();
                if (string2 != null) {
                    hashMap1.put(classFileBases[i], string2);
                }
            }
        }

        for (ClassFileBase classFileBase2 : classFileBases) {
            String string4 = classFileBase2.getNestHostName();
            if (string4 != null) {
                ClassHierarchyNode classHierarchyNode7 = ClassHierarchyNode.findNode(string4);
                if (classHierarchyNode7 != null) {
                    ClassHierarchyNode classHierarchyNode3 = ClassHierarchyNode.findNode(classFileBase2.getClassName());
                    this.nestMembersByHost.addValue(classHierarchyNode7, classHierarchyNode3);
                }
            }
        }

        this.checkForHierarchyLoops(classFileBases);
        Enumeration enumeration = this.subclassesBySuperclass.keys();

        while (enumeration.hasMoreElements()) {
            ClassHierarchyNode classHierarchyNode5 = (ClassHierarchyNode) enumeration.nextElement();
            ListMultimap listMultimap1;
            if (!hashMap2.containsKey(classHierarchyNode5)) {
                this.rootNodes.add(classHierarchyNode5);
                listMultimap1 = this.subclassesBySuperclass;
            } else {
                listMultimap1 = this.subclassesBySuperclass;
            }

            List list1 = listMultimap1.getValues(classHierarchyNode5);
            Collections.sort(list1);
        }

        Collections.sort(this.rootNodes);

        for (int i = 0; i < classFileBases.length; i++) {
            if (classFileBases[i].isProgramClass() && classFileBases[i].isInterface()) {
                ClassHierarchyNode classHierarchyNode6 = ClassHierarchyNode.findNode(classFileBases[i].getClassName());
                List list2 = listMultimap.getValues(classHierarchyNode6);
                if (list2 == null) {
                    this.rootProgramInterfaces.add(classHierarchyNode6);
                } else {
                    boolean bl = false;

                    for (int j = 0; j < list2.size(); j++) {
                        ClassHierarchyNode classHierarchyNode8 = (ClassHierarchyNode) list2.get(j);
                        if (classHierarchyNode8.isProgramClass()) {
                            bl = true;
                            break;
                        }
                    }

                    if (!bl) {
                        this.rootProgramInterfaces.add(classHierarchyNode6);
                    }
                }
            }
        }

        this.visitSubtree(new ClassListCollector(), Collections.enumeration(this.rootNodes), -1, this.orderedNodes);
        Iterator iterator = hashMap1.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            ClassFileBase classFileBase1 = (ClassFileBase) entry.getKey();
            String string3 = (String) entry.getValue();
            String string5 = classFileBase1.getClassName();
            ClassHierarchyNode classHierarchyNode9 = ClassHierarchyNode.findNode(string5);
            ClassHierarchyNode classHierarchyNode10 = ClassHierarchyNode.findNode(string3);
            if (classHierarchyNode10 != null) {
                classHierarchyNode10.addEnclosedClass(classHierarchyNode9);
                classHierarchyNode9.setEnclosingNode(classHierarchyNode10);
                String string1 = classHierarchyNode9.getClassFile().getOuterClassName();
                if (string1 != null) {
                    ClassHierarchyNode classHierarchyNode4 = ClassHierarchyNode.findNode(string1);
                    if (classHierarchyNode4 != null) {
                        classHierarchyNode9.setOuterClassNode(classHierarchyNode4);
                    } else {
                        new StringBuilder().append(ZkmAssert.getSimpleClassName(this)).append(" AZZERT DISABLED! ").append(classFileBase1.getInputPath()).toString();
                        ZkmAssert.noOp();
                    }
                }
            } else {
                classHierarchyNode9.getClassFile().setInnerClassesRemovalPending();
                this.innerClassesHolders.remove(classHierarchyNode9);
            }
        }

        for (int i = 0; i < classFileBases.length; i++) {
            if (classFileBases[i].isProgramClass()) {
                ProgramClass programClass1 = (ProgramClass) classFileBases[i];
                if (programClass1.isInnerClassesRemovalPending()) {
                    programClass1.removeInnerClassAttributes();
                }
            }
        }
    }

    public Enumeration enumerateOrderedNodes() {
        return Collections.enumeration(this.orderedNodes);
    }

    public void clearInnerClassesHolders() {
        this.innerClassesHolders.clear();
    }
}
