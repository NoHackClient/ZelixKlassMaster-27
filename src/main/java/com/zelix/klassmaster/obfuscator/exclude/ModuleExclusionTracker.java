package com.zelix.klassmaster.obfuscator.exclude;

import com.zelix.klassmaster.classfile.ModuleInfoClass;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.CollectionSnapshotEnumeration;
import com.zelix.klassmaster.util.ZkmUtils;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class ModuleExclusionTracker extends ExclusionHandlerBase {
    public Set includedModules = ZkmUtils.createHashSet();
    public Set excludedModules = ZkmUtils.createHashSet();
    public Map modulesByName = ZkmUtils.createHashMap();

    public Enumeration getIncludedModules() {
        return new CollectionSnapshotEnumeration(this.includedModules);
    }

    public void collectModules() {
        Enumeration enumeration = this.classRepository.enumerateModuleInfoClasses();

        while (enumeration.hasMoreElements()) {
            ModuleInfoClass moduleInfoClass = (ModuleInfoClass) enumeration.nextElement();
            this.includedModules.add(moduleInfoClass);
            this.modulesByName.put(moduleInfoClass.getModuleName(), moduleInfoClass);
        }
    }

    public final void excludeModule(ModuleInfoClass moduleInfoClass, String string) {
        if (this.includedModules.contains(moduleInfoClass)) {
            this.includedModules.remove(moduleInfoClass);
            this.excludedModules.add(moduleInfoClass);
            String string1 = "Excluding module \"" + moduleInfoClass.getModuleName() + "\" from name obfuscation because of \"" + string + "\"";
            if (super.scriptEnvironment.isVerbose() && super.logWriter != null) {
                super.logWriter.println("\t" + string1);
            }
        }
    }

    public ModuleExclusionTracker(ClassRepository classRepository1, List list1, List list2, ScriptEnvironment scriptEnvironment1) {
        super(classRepository1, list1, list2, scriptEnvironment1);
    }
}
