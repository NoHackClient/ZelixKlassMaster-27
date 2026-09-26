package com.zelix.klassmaster.engine;

import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.SetMultiMap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

public class ClassInitOrderHandler {
    public SetMultiMap initializedAfterMap = new SetMultiMap();
    public Set laterInitializedClasses = ZkmUtils.createHashSet();
    public ScriptEnvironment scriptEnvironment;

    public ClassInitOrderHandler(ScriptEnvironment scriptEnvironment1) throws IOException {
        boolean bl = false;
        this.scriptEnvironment = scriptEnvironment1;
        SetMultiMap setMultiMap = scriptEnvironment1.getClassInitializationOrder();
        if (setMultiMap != null) {
            Iterator iterator = setMultiMap.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                String string = (String) entry.getKey();
                ProgramClass programClass1 = ClassHierarchyNode.findProgramClass(string);
                if (programClass1 != null) {
                    Iterator iterator1 = ((Set) entry.getValue()).iterator();

                    while (iterator1.hasNext()) {
                        String string1 = (String) iterator1.next();
                        ProgramClass programClass2 = ClassHierarchyNode.findProgramClass(string1);
                        if (programClass2 != null) {
                            this.initializedAfterMap.addValue(programClass1, programClass2);
                            this.laterInitializedClasses.add(programClass2);
                            ClassHierarchyNode classHierarchyNode = ClassHierarchyNode.findNode(string1);
                            this.laterInitializedClasses.addAll(classHierarchyNode.getProgramSuperclasses());
                        } else {
                            scriptEnvironment1.logWarning(
                                    "Class name '"
                                            + ZkmUtils.slashesToDots(string1)
                                            + "' appears in a '"
                                            + "classInitializationOrder"
                                            + "' statement but it has not been opened for obfuscation. It will be ignored."
                            );
                            scriptEnvironment1.logError(
                                    "Class '"
                                            + ZkmUtils.slashesToDots(string1)
                                            + "' not opened. It was specified as initialized after class '"
                                            + programClass1.getOriginalDottedName()
                                            + " in a '"
                                            + "classInitializationOrder"
                                            + "' statement. (5)"
                            );
                        }
                    }
                } else {
                    scriptEnvironment1.logError(
                            "Class '"
                                    + ZkmUtils.slashesToDots(string)
                                    + "' not opened. It was specified as initialized before "
                                    + (((Set) entry.getValue()).size() == 1 ? "another class" : "other classes")
                                    + " in a '"
                                    + "classInitializationOrder"
                                    + "' statement. (6)"
                    );
                }
            }

            SetMultiMap setMultiMap2;
            if (HiddenOptionFlags.NO_TRANSITIVE_INIT_ORDER) {
                setMultiMap2 = this.initializedAfterMap;
            } else {
                do {
                    bl = false;
                    SetMultiMap setMultiMap1 = this.initializedAfterMap.deepCopy();
                    Iterator iterator3 = setMultiMap1.entrySet().iterator();

                    while (iterator3.hasNext()) {
                        Entry entry2 = (Entry) iterator3.next();
                        ProgramClass programClass5 = (ProgramClass) entry2.getKey();
                        Set set2 = (Set) entry2.getValue();
                        Iterator iterator4 = set2.iterator();

                        while (iterator4.hasNext()) {
                            ProgramClass programClass6 = (ProgramClass) iterator4.next();
                            Set set1 = setMultiMap1.getValues(programClass6);
                            if (set1 != null) {
                                Iterator iterator2 = set1.iterator();

                                while (iterator2.hasNext()) {
                                    ProgramClass programClass3 = (ProgramClass) iterator2.next();
                                    if (this.initializedAfterMap.addValue(programClass5, programClass3)) {
                                        bl = true;
                                    }
                                }
                            }
                        }
                    }
                } while (bl);

                setMultiMap2 = this.initializedAfterMap;
            }

            iterator = setMultiMap2.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry1 = (Entry) iterator.next();
                ProgramClass programClass4 = (ProgramClass) entry1.getKey();
                HashSet hashSet = ZkmUtils.createHashSet();
                if (this.hasInitOrderCycle(programClass4, this.initializedAfterMap.getValues(programClass4), hashSet)) {
                    scriptEnvironment1.logSeriousError_v("Loop inside of 'classInitializationOrder' statement : '" + programClass4.getDisplayLocationName() + "'");
                }
            }
        }
    }

    public SetMultiMap getInitOrderMapCopy() {
        return (SetMultiMap) this.initializedAfterMap.clone();
    }

    public boolean hasInitOrderCycle(ProgramClass programClass1, Set set1, Set set2) {
        if (set1 != null) {
            if (set1.contains(programClass1)) {
                return true;
            }

            Iterator iterator = set1.iterator();

            while (iterator.hasNext()) {
                ProgramClass programClass2 = (ProgramClass) iterator.next();
                if (set2.add(programClass2) && this.hasInitOrderCycle(programClass1, this.initializedAfterMap.getValues(programClass2), set2)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isLaterInitializedClass(Object object) {
        return this.laterInitializedClasses.contains(object);
    }
}
