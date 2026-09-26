package com.zelix.klassmaster.obfuscator.rename;

import com.zelix.klassmaster.changelog.ChangeLogMapping;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchy;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.engine.ObfuscatorEngineBase;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.log.MessageReporter;
import com.zelix.klassmaster.obfuscator.exclude.NameExclusionSet;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClassRenameClashChecker {
    public Set allLowerCaseNames;
    public Set fileClassLowerCaseNames;
    public ClassNameGenerator nameGenerator;
    public final Set fixedClasses = ZkmUtils.createHashSet();
    public final Set fixedClassNames = ZkmUtils.createHashSet();
    public final Map classesByName = ZkmUtils.createHashMap();
    public Set pendingNodes = ZkmUtils.createHashSet();
    public final ProgramClass[] programClasses;
    public final ClassFileBase[] allClassFiles;
    public ChangeLogMapping changeLogMapping;
    public boolean randomizeOrder;
    public final ClassHierarchy classHierarchy;
    public final ClassRepository classRepository;
    public final NameExclusionSet exclusionSet;
    public final HashMap oldToNewNames;
    public final HashMap newToOldNames;

    public void renameClasses(
            ClassNameGenerator classNameGenerator, HashMap hashMap, HashMap hashMap1, ListMultimap listMultimap, MessageReporter messageReporter1
    ) throws ZkmException, IOException {
        int keepInnerClassInfoMode = classNameGenerator.getKeepInnerClassInfoMode();
        int keepGenericsInfoMode = classNameGenerator.getKeepGenericsInfoMode();
        this.nameGenerator = classNameGenerator;
        this.checkChangeLogClashes(listMultimap);
        int bc = this.programClasses.length;
        this.fileClassLowerCaseNames = classNameGenerator.fileClassLowerCaseNames;
        this.allLowerCaseNames = classNameGenerator.allLowerCaseNames;

        for (int i = 0; i < bc; i++) {
            this.classesByName.put(this.programClasses[i].getClassName(), this.programClasses[i]);
            if (this.exclusionSet.isClassExcluded(this.programClasses[i]) || this.programClasses[i].getSimpleName().equals("package-info")) {
                this.programClasses[i].getClassName();
                this.fixedClasses.add(this.programClasses[i]);
            }
        }

        Iterator iterator1 = this.fixedClasses.iterator();

        while (iterator1.hasNext()) {
            ProgramClass programClass1 = (ProgramClass) iterator1.next();
            ClassHierarchyNode classHierarchyNode = ClassHierarchyNode.findNode(programClass1.getClassName());
            classHierarchyNode.registerClassName(
                    classNameGenerator, this.oldToNewNames, this.newToOldNames, this.fixedClassNames, this.fileClassLowerCaseNames, this.allLowerCaseNames
            );
        }

        List list1 = this.classHierarchy.getTopProgramNodes();
        if (HiddenOptionFlags.RANDOMIZE_OBFUSCATION) {
        }

        if (this.randomizeOrder || HiddenOptionFlags.RANDOMIZE_OBFUSCATION) {
            ZkmUtils.shuffleList(list1, ZkmUtils.createRandom(2));
        }

        for (int i = 0; i < list1.size(); i++) {
            ClassHierarchyNode classHierarchyNode1 = (ClassHierarchyNode) list1.get(i);
            classHierarchyNode1.generateClassNames(
                    classNameGenerator, this.changeLogMapping, this.oldToNewNames, this.newToOldNames, this.fixedClasses, this.pendingNodes
            );
        }

        int bf = this.pendingNodes.size();

        int bg;
        do {
            bg = bf;
            Iterator iterator = this.pendingNodes.iterator();

            while (iterator.hasNext()) {
                ClassHierarchyNode classHierarchyNode2 = (ClassHierarchyNode) iterator.next();
                boolean bl = classHierarchyNode2.tryGenerateClassName(classNameGenerator, this.oldToNewNames, this.newToOldNames, iterator);
                if (bl) {
                    bf = this.pendingNodes.size();
                }
            }
        } while (bf > 0 && bf != bg);

        Iterator iterator2 = this.pendingNodes.iterator();

        while (iterator2.hasNext()) {
            ClassHierarchyNode classHierarchyNode3 = (ClassHierarchyNode) iterator2.next();
            classHierarchyNode3.assignGeneratedClassName(classNameGenerator, this.oldToNewNames, this.newToOldNames);
        }

        if (HiddenOptionFlags.MARK_RENAMED_LOCALS) {
        }

        int bh = 0;
        int bi = 0;

        for (ProgramClass[] programClass2 = this.programClasses; bi < programClass2.length; programClass2 = this.programClasses) {
            String string = this.programClasses[bh].getClassName();
            String string1 = (String) this.oldToNewNames.get(string);
            if (string1 != null && !string1.equals(string)) {
                this.programClasses[bh].renameClass(string1, hashMap);
            }

            bi = ++bh;
        }

        if (this.changeLogMapping != null) {
            ObfuscatorEngineBase.applyChangeLogClassNames(
                    this.oldToNewNames, this.newToOldNames, hashMap, this.classRepository.getClasspathLoader(), this.changeLogMapping
            );
        }

        this.classRepository
                .applyNameChanges(this.allClassFiles, this.classRepository.getModuleInfoClasses(), keepInnerClassInfoMode, keepGenericsInfoMode, this.oldToNewNames, hashMap1, messageReporter1);
        this.classRepository.refreshIndexes();
    }

    public void checkChangeLogClashes(ListMultimap listMultimap) throws IOException {
        if (this.changeLogMapping != null) {
            ArrayList arrayList = this.changeLogMapping.getOldClassNames();
            int ba = arrayList.size();

            for (int i = 0; i < ba; i++) {
                String string = (String) arrayList.get(i);
                if (ClassHierarchyNode.isProgramClassName(string)
                        && (!this.exclusionSet.isClassNameExcluded(string) || !this.changeLogMapping.isSimpleNameChanged(string))) {
                    String string1 = this.changeLogMapping.getNewClassName(string);
                    if (string1 != null) {
                        ClassHierarchyNode classHierarchyNode = ClassHierarchyNode.findNode(string1);
                        if (classHierarchyNode != null && !classHierarchyNode.isProgramClass()) {
                            this.changeLogMapping
                                    .logWarning(
                                            "\""
                                                    + ZkmUtils.slashesToDots(string1)
                                                    + "\" is specified as a new class name but it clashes with an existing class that has not been opened."
                                    );
                        }
                    } else {
                        string1 = string;
                    }

                    if (listMultimap != null && listMultimap.containsKey(string1) && this.changeLogMapping.hasClassesOpenedFromFileSystem()) {
                        List list1 = listMultimap.getValues(string1);
                        String string2 = (String) list1.get(0);
                        this.changeLogMapping
                                .reportFatalError(
                                        "Class \""
                                                + ZkmUtils.slashesToDots(string)
                                                + "\" is mapped to \""
                                                + ZkmUtils.slashesToDots(string1)
                                                + "\" but this will result in a clash with package \""
                                                + ZkmUtils.slashesToDots(string2)
                                                + "\" that has been renamed to \""
                                                + ZkmUtils.slashesToDots(string1)
                                                + "\""
                                );
                    }

                    String string5 = ClassFileBase.getPackagePath(string);
                    ArrayList arrayList1 = this.nameGenerator.getPackageClashCandidates(string1, string5);

                    for (int j = 0; j < arrayList1.size(); j++) {
                        String string3 = (String) arrayList1.get(j);
                        if (!string3.equals(string) && ClassHierarchyNode.isProgramClassName(string3) && this.exclusionSet.isClassNameExcluded(string3)) {
                            String string4 = !this.changeLogMapping.hasClassMapping(string3) ? " new" : "";
                            this.changeLogMapping
                                    .reportFatalError(
                                            "Class \""
                                                    + ZkmUtils.slashesToDots(string)
                                                    + "\" is mapped to \""
                                                    + ZkmUtils.slashesToDots(string1)
                                                    + "\" but this will result in a clash with excluded"
                                                    + string4
                                                    + " class \""
                                                    + ZkmUtils.slashesToDots(string3)
                                                    + "\" that will be also be renamed to \""
                                                    + ZkmUtils.slashesToDots(string1)
                                                    + "\""
                                    );
                        }
                    }
                }
            }
        }
    }

    public ClassRenameClashChecker(
            NameExclusionSet nameExclusionSet,
            ChangeLogMapping changeLogMapping1,
            boolean randomizeOrder,
            ClassRepository classRepository1,
            ProgramClass[] programClass1,
            ClassFileBase[] classFileBases,
            ClassHierarchy classHierarchy1,
            HashMap hashMap,
            HashMap hashMap1
    ) {
        this.programClasses = programClass1;
        this.allClassFiles = classFileBases;
        this.changeLogMapping = changeLogMapping1;
        this.randomizeOrder = randomizeOrder;
        this.classHierarchy = classHierarchy1;
        this.classRepository = classRepository1;
        this.exclusionSet = nameExclusionSet;
        this.oldToNewNames = hashMap;
        this.newToOldNames = hashMap1;
    }

    public boolean isRandomizeOrder() {
        return this.randomizeOrder;
    }
}
