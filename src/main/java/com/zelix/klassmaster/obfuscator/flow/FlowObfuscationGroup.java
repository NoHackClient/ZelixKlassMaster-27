package com.zelix.klassmaster.obfuscator.flow;

import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.util.NamedSet;
import com.zelix.klassmaster.util.ZkmUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class FlowObfuscationGroup {
    public OpaquePredicateField groupField;
    public NamedSet memberClasses = ZkmUtils.createNamedSet();
    public Set packageNames = ZkmUtils.createHashSet();
    public Map packageFields = ZkmUtils.createHashMap();
    public ClassGroupingSet classGrouping;

    public Set getCommonClasses() {
        return this.classGrouping.getIntersection();
    }

    public OpaquePredicateField getPackageField(Object object) {
        return (OpaquePredicateField) this.packageFields.get(object);
    }

    public void setGroupField(OpaquePredicateField opaquePredicateField) {
        this.groupField = opaquePredicateField;
    }

    public boolean containsClass(ClassFileBase classFileBase) {
        return this.classGrouping.unionContains(classFileBase);
    }

    public FlowObfuscationGroup(ClassGroupingSet classGroupingSet) {
        this.classGrouping = classGroupingSet;
        Iterator iterator = classGroupingSet.getSets().iterator();

        while (iterator.hasNext()) {
            this.memberClasses.addAll((Collection) iterator.next());
        }
    }

    public List getGroupingSetNames() {
        return this.classGrouping.getSetNames();
    }

    public Set getMemberClasses() {
        return Collections.unmodifiableSet(this.memberClasses);
    }

    public OpaquePredicateField getGroupField() {
        return this.groupField;
    }

    public void putPackageField(Object object, Object object1) {
        this.packageFields.put(object, object1);
    }

    public void collectPackageNames() {
        Iterator iterator = this.memberClasses.iterator();

        while (iterator.hasNext()) {
            String string = ((ClassFileBase) iterator.next()).getPackagePath();
            this.packageNames.add(string);
        }
    }

    public boolean hasAllFields() {
        return this.groupField != null && this.packageFields.size() == this.packageNames.size();
    }

    public boolean isCommonClass(ClassFileBase classFileBase) {
        return this.classGrouping.intersectionContains(classFileBase);
    }

    public List getSortedPackageNames() {
        ArrayList arrayList = new ArrayList(this.packageNames);
        Collections.sort(arrayList);
        return arrayList;
    }

    public void removeMemberClass(Object object) {
        this.memberClasses.remove(object);
    }

    public Set getPackageNames() {
        return Collections.unmodifiableSet(this.packageNames);
    }

    public List getPackageFields() {
        ArrayList arrayList = new ArrayList(this.packageFields.size());
        Iterator iterator = this.packageFields.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            arrayList.add(entry.getValue());
        }

        return arrayList;
    }
}
