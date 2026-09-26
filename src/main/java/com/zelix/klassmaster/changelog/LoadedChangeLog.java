package com.zelix.klassmaster.changelog;

import com.zelix.klassmaster.changelog.parser.ChangeLogSimpleNode;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.NestedMultiMap;
import com.zelix.klassmaster.util.ObjectPair;
import com.zelix.klassmaster.util.ThreeKeyMultiMap;
import com.zelix.klassmaster.util.TwoKeyMap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class LoadedChangeLog extends AbstractChangeLog {
    public int packageCount;
    public Map sourceNameMappings;
    public Map classMappings;
    public ThreeKeyMultiMap methodMappingsByNewSignature;
    public int classCount;
    public TwoKeyMap lineNumberMappings;
    public NestedMultiMap originalMethodsByNewName;
    public boolean methodsHaveReturnTypes = true;
    public String changeLogName;

    public Integer lookupLineNumber(Object object, int ba) {
        return (Integer) this.lineNumberMappings.getValue(object, this.getCachedInteger(ba));
    }

    @Override
    public void addMemberFlowObfuscationData(Object object, Object object1) {
    }

    @Override
    public void addMethodMappings(String string, ListMultimap listMultimap) {
        ChangeLogSimpleNode.getOpaqueStrings();
        if (this.originalMethodsByNewName == null) {
            this.originalMethodsByNewName = new NestedMultiMap(this.classCount);
            this.methodMappingsByNewSignature = new ThreeKeyMultiMap(this.classCount);
        }

        Enumeration enumeration = listMultimap.keys();

        while (enumeration.hasMoreElements()) {
            ChangeLogMemberKey changeLogMemberKey = (ChangeLogMemberKey) enumeration.nextElement();
            String string1 = changeLogMemberKey.getName();
            String string2 = changeLogMemberKey.getType();
            String string3 = changeLogMemberKey.getParameterTypes();
            Iterator iterator = listMultimap.getValues(changeLogMemberKey).iterator();

            while (iterator.hasNext()) {
                NewNameMapping newNameMapping = (NewNameMapping) iterator.next();
                String string4 = newNameMapping.getNewName();
                NestedMultiMap nestedMultiMap;
                if (newNameMapping.hasParameterChangeData()) {
                    this.setParameterChangeDataPresent();
                    this.methodMappingsByNewSignature.addValue(string, string4, newNameMapping.getEffectiveParameterTypes(), new ObjectPair(string1, string2));
                    nestedMultiMap = this.originalMethodsByNewName;
                } else if (newNameMapping.isParametersObfuscated()) {
                    this.methodMappingsByNewSignature.addValue(string, string4, newNameMapping.getEffectiveParameterTypes(), new ObjectPair(string1, string2));
                    nestedMultiMap = this.originalMethodsByNewName;
                } else {
                    this.methodMappingsByNewSignature.addValue(string, string4, string3, new ObjectPair(string1, string2));
                    nestedMultiMap = this.originalMethodsByNewName;
                }

                nestedMultiMap.addValue(string, string4, new ObjectPair(string1, string3));
            }

            if (changeLogMemberKey.getType() != null) {
                this.methodsHaveReturnTypes = true;
            } else {
                this.methodsHaveReturnTypes = false;
            }
        }
    }

    @Override
    public void addModuleMethodParameterChangeClasses(Object object, Object object1) {
    }

    public String getSourceName(Object object) {
        return this.sourceNameMappings != null ? (String) this.sourceNameMappings.get(object) : null;
    }

    @Override
    public void addFieldMappings(Object object, Object object1) {
    }

    @Override
    public void incrementClassCount() {
        this.classCount++;
    }

    @Override
    public boolean acceptsLineNumberChanges() {
        return true;
    }

    @Override
    public void addPackageFlowObfuscationData(Object object, Object object1, Object object2) throws ZkmException, IOException {
    }

    @Override
    public void setReferenceObfuscationClass(Object object) {
    }

    public LoadedChangeLog(String string) {
        this.changeLogName = string;
    }

    public void mapMethodParameterTypes() {
        if (this.hasParameterChangeData()) {
            ThreeKeyMultiMap threeKeyMultiMap = new ThreeKeyMultiMap(this.methodMappingsByNewSignature.getKeyCount());
            Iterator iterator = this.methodMappingsByNewSignature.getEntrySnapshot().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                String string = (String) entry.getKey();
                Iterator iterator1 = ((NestedMultiMap) entry.getValue()).entrySet().iterator();

                while (iterator1.hasNext()) {
                    Entry entry1 = (Entry) iterator1.next();
                    String string1 = (String) entry1.getKey();
                    Iterator iterator2 = ((ListMultimap) entry1.getValue()).entrySet().iterator();

                    while (iterator2.hasNext()) {
                        Entry entry2 = (Entry) iterator2.next();
                        String string2 = (String) entry2.getKey();
                        String string3 = this.mapTypeList(string2, this.classMappings);
                        Iterator iterator3 = ((List) entry2.getValue()).iterator();

                        while (iterator3.hasNext()) {
                            ObjectPair objectPair = (ObjectPair) iterator3.next();
                            threeKeyMultiMap.addValue(string, string1, string3, objectPair);
                        }
                    }
                }
            }

            this.methodMappingsByNewSignature = threeKeyMultiMap;
        }
    }

    public boolean hasClassMapping(Object object) {
        return this.classMappings.containsKey(object);
    }

    public String getNewClassName(Object object) {
        return (String) this.classMappings.get(object);
    }

    @Override
    public void addMainFlowObfuscationData(Object object, Object object1) throws ZkmException, IOException {
    }

    @Override
    public void addClassMapping(Object object, Object object1) {
        if (this.classMappings == null) {
            this.classMappings = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(this.classCount));
        }

        this.classMappings.put(object, object1);
    }

    public boolean hasLineNumberMappings(Object object) {
        return this.lineNumberMappings != null && this.lineNumberMappings.containsKey(object);
    }

    @Override
    public boolean acceptsFieldChanges() {
        return false;
    }

    @Override
    public void addPackageMapping(Object object, Object object1) {
    }

    public List findOriginalMethods(Object object, Object object1) {
        return this.originalMethodsByNewName.getValues(object, object1);
    }

    public String toNewTypeName(String string) {
        return AbstractChangeLog.mapTypeName(string, this.classMappings);
    }

    public String getOriginalMethodName(String string, String string1, String[] strings, String string2) {
        if (string == null) {
            throw new IllegalArgumentException("New class name cannot be null");
        }

        if (string1 == null) {
            throw new IllegalArgumentException("New method name cannot be null");
        }

        if (strings == null) {
            throw new IllegalArgumentException("New argument types array cannot be null");
        }

        if (string2 == null) {
            throw new IllegalArgumentException("New return type cannot be null");
        }

        String string3 = (String) ZkmUtils.mapOrSelf(string, this.classMappings);
        String string4 = this.joinTypeNames(strings);
        String string5 = this.mapTypeList(string4, this.classMappings);
        String string6 = AbstractChangeLog.mapTypeName(string2, this.classMappings);
        List list1 = this.methodMappingsByNewSignature.getValues(string3, string1, string5);
        if (list1.size() == 1) {
            ObjectPair objectPair = (ObjectPair) list1.get(0);
            if (objectPair.getSecond() == null || string6.equals(objectPair.getSecond())) {
                return (String) objectPair.getFirst();
            }
        } else {
            Iterator iterator = list1.iterator();

            while (iterator.hasNext()) {
                ObjectPair objectPair1 = (ObjectPair) iterator.next();
                if (string6.equals(objectPair1.getSecond())) {
                    return (String) objectPair1.getFirst();
                }
            }
        }

        return string1;
    }

    @Override
    public void incrementPackageCount() {
        this.packageCount++;
    }

    public boolean isNewClassName(Object object) {
        return this.classMappings.containsValue(object);
    }

    @Override
    public void addModuleReferenceObfuscationClass(Object object, Object object1) {
    }

    @Override
    public void addSourceNameMapping(Object object, Object object1) {
        if (this.sourceNameMappings == null) {
            this.sourceNameMappings = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(this.classCount));
        }

        this.sourceNameMappings.put(object, object1);
    }

    public LoadedChangeLog mergeWith(LoadedChangeLog loadedChangeLog1, PrintWriter printWriter) throws ChangeLogMergeException {
        if (loadedChangeLog1.classMappings != null) {
            ArrayList arrayList = new ArrayList(loadedChangeLog1.classMappings.keySet());
            Collections.sort(arrayList);
            Iterator iterator = arrayList.iterator();

            while (iterator.hasNext()) {
                String string = (String) iterator.next();
                String string1 = (String) loadedChangeLog1.classMappings.get(string);
                if (this.classMappings != null && this.classMappings.containsKey(string)) {
                    String string2 = (String) this.classMappings.get(string);
                    if (!string2.equals(string1)) {
                        printWriter.println(
                                "ERROR: Class name '"
                                        + string
                                        + "' mapping clash while merging with '"
                                        + loadedChangeLog1.changeLogName
                                        + "'. '"
                                        + string2
                                        + "' and '"
                                        + string1
                                        + "' both map to '"
                                        + string
                                        + "'."
                        );
                    }
                } else {
                    this.addClassMapping(string, string1);
                }
            }
        }

        if (loadedChangeLog1.sourceNameMappings != null) {
            Iterator iterator4 = loadedChangeLog1.sourceNameMappings.entrySet().iterator();

            while (iterator4.hasNext()) {
                Entry entry = (Entry) iterator4.next();
                String string14 = (String) entry.getKey();
                String string15 = (String) entry.getValue();
                if (this.sourceNameMappings != null && this.sourceNameMappings.containsKey(string14)) {
                    String string3 = (String) this.sourceNameMappings.get(string14);
                    if (!string3.equals(string15)) {
                        printWriter.println(
                                "ERROR: Class name '"
                                        + string14
                                        + "' source name mapping clash while merging with '"
                                        + loadedChangeLog1.changeLogName
                                        + "'. '"
                                        + string14
                                        + "' maps to '"
                                        + string15
                                        + "' and '"
                                        + string15
                                        + "'."
                        );
                    }
                } else {
                    this.addSourceNameMapping(string14, string15);
                }
            }
        }

        if (loadedChangeLog1.methodMappingsByNewSignature != null) {
            if (this.methodMappingsByNewSignature != null && this.methodsHaveReturnTypes != loadedChangeLog1.methodsHaveReturnTypes) {
                String string10 = "Change log methods "
                        + (this.methodsHaveReturnTypes ? "have return types" : "have no return types")
                        + " which is inconsistent with change log \""
                        + loadedChangeLog1.changeLogName
                        + "\" whose methods "
                        + (loadedChangeLog1.methodsHaveReturnTypes ? "have return types" : "have no return types")
                        + ". All method entries in change logs being merged must have return types or no method entries can have return types.";
                throw new ChangeLogMergeException(string10);
            }

            if (this.methodMappingsByNewSignature != null && this.hasParameterChangeData() != loadedChangeLog1.hasParameterChangeData()) {
                String string11 = "Change log methods "
                        + (this.hasParameterChangeData() ? "have new method parameter data" : "have no new method parameter data")
                        + " which is inconsistent with change log \""
                        + loadedChangeLog1.changeLogName
                        + "\" whose methods "
                        + (loadedChangeLog1.hasParameterChangeData() ? "have new method parameter data" : "have no new method parameter data")
                        + ". All method entries in change logs being merged must have new method parameter data or no method entries can have new method parameter data.";
                throw new ChangeLogMergeException(string11);
            }

            Enumeration enumeration1 = loadedChangeLog1.methodMappingsByNewSignature.keys();

            while (enumeration1.hasMoreElements()) {
                String string12 = (String) enumeration1.nextElement();
                NestedMultiMap nestedMultiMap = loadedChangeLog1.methodMappingsByNewSignature.getNestedMultiMap(string12);
                if (this.methodMappingsByNewSignature != null && this.methodMappingsByNewSignature.containsKey(string12)) {
                    NestedMultiMap nestedMultiMap1 = this.methodMappingsByNewSignature.getNestedMultiMap(string12);
                    Enumeration enumeration4 = nestedMultiMap.keys();

                    while (enumeration4.hasMoreElements()) {
                        String string16 = (String) enumeration4.nextElement();
                        ListMultimap listMultimap2 = nestedMultiMap.getMultimap(string16);
                        if (nestedMultiMap1.containsKey(string16)) {
                            ListMultimap listMultimap3 = nestedMultiMap1.getMultimap(string16);
                            Enumeration enumeration5 = listMultimap2.keys();

                            while (enumeration5.hasMoreElements()) {
                                String string17 = (String) enumeration5.nextElement();
                                List list2 = listMultimap2.getValues(string17);
                                if (listMultimap3.containsKey(string17)) {
                                    List list3 = listMultimap3.getValues(string17);
                                    Iterator iterator2 = list2.iterator();

                                    while (iterator2.hasNext()) {
                                        ObjectPair objectPair1 = (ObjectPair) iterator2.next();
                                        String string6 = (String) objectPair1.getFirst();
                                        String string7 = (String) objectPair1.getSecond();
                                        Iterator iterator3 = list3.iterator();

                                        while (iterator3.hasNext()) {
                                            ObjectPair objectPair2 = (ObjectPair) iterator3.next();
                                            String string8 = (String) objectPair2.getFirst();
                                            String string9 = (String) objectPair2.getSecond();
                                            if (string7 != null) {
                                                if (string7.equals(string9) && !string6.equals(string8)) {
                                                    printWriter.println(
                                                            "ERROR: Method name clash name while merging with '"
                                                                    + loadedChangeLog1.changeLogName
                                                                    + "'. Both '"
                                                                    + string8
                                                                    + "' and '"
                                                                    + string6
                                                                    + "' map to new method name '"
                                                                    + string7
                                                                    + " "
                                                                    + string16
                                                                    + "("
                                                                    + string17
                                                                    + ")' in class '"
                                                                    + string12
                                                                    + "'."
                                                    );
                                                }
                                            } else if (!string6.equals(string8)) {
                                                printWriter.println(
                                                        "ERROR: Method name clash name while merging with '"
                                                                + loadedChangeLog1.changeLogName
                                                                + "'. Both '"
                                                                + string8
                                                                + "' and '"
                                                                + string6
                                                                + "' map to new method name '"
                                                                + string16
                                                                + "("
                                                                + string17
                                                                + ")' in class '"
                                                                + string12
                                                                + "'."
                                                );
                                            }
                                        }
                                    }
                                } else {
                                    printWriter.println(
                                            "WARNING: Different versions of class '"
                                                    + string12
                                                    + "' while merging with '"
                                                    + loadedChangeLog1.changeLogName
                                                    + "'.  Class '"
                                                    + string12
                                                    + "' in '"
                                                    + this.changeLogName
                                                    + "' does not have a new method name '"
                                                    + string16
                                                    + "("
                                                    + string17
                                                    + ")'."
                                    );
                                }
                            }
                        } else {
                            printWriter.println(
                                    "WARNING: Different versions of class '"
                                            + string12
                                            + "' while merging with '"
                                            + loadedChangeLog1.changeLogName
                                            + "'.  Class '"
                                            + string12
                                            + "' in '"
                                            + this.changeLogName
                                            + "' does not have a new method name '"
                                            + string16
                                            + "'."
                            );
                        }
                    }
                } else {
                    ListMultimap listMultimap1 = new ListMultimap();
                    Enumeration enumeration3 = nestedMultiMap.keys();

                    while (enumeration3.hasMoreElements()) {
                        String string4 = (String) enumeration3.nextElement();
                        ListMultimap listMultimap = nestedMultiMap.getMultimap(string4);
                        Enumeration enumeration = listMultimap.keys();

                        while (enumeration.hasMoreElements()) {
                            String string5 = (String) enumeration.nextElement();
                            List list1 = listMultimap.getValues(string5);
                            Iterator iterator1 = list1.iterator();

                            while (iterator1.hasNext()) {
                                ObjectPair objectPair = (ObjectPair) iterator1.next();
                                listMultimap1.addValue(
                                        new ChangeLogMemberKey((String) objectPair.getFirst(), string5, (String) objectPair.getSecond(), 0), new NewNameMapping(string4)
                                );
                            }
                        }
                    }

                    this.addMethodMappings(string12, listMultimap1);
                }
            }
        }

        if (loadedChangeLog1.lineNumberMappings != null) {
            Enumeration enumeration2 = loadedChangeLog1.lineNumberMappings.keys();

            while (enumeration2.hasMoreElements()) {
                String string13 = (String) enumeration2.nextElement();
                Map map1 = loadedChangeLog1.lineNumberMappings.getInnerMap(string13);
                if (this.lineNumberMappings != null && this.lineNumberMappings.containsKey(string13)) {
                    Map map2 = this.lineNumberMappings.getInnerMap(string13);
                    Iterator iterator5 = map1.entrySet().iterator();

                    while (iterator5.hasNext()) {
                        Entry entry1 = (Entry) iterator5.next();
                        Integer integer = (Integer) entry1.getKey();
                        Integer integer1 = (Integer) entry1.getValue();
                        if (map2.containsKey(integer)) {
                            if (!((Integer) map2.get(integer)).equals(integer1)) {
                                printWriter.println(
                                        "WARNING: Different line number mapping in class '"
                                                + string13
                                                + "' while merging with '"
                                                + loadedChangeLog1.changeLogName
                                                + "'. Line number "
                                                + integer
                                                + " is mapped to by "
                                                + integer1
                                                + " and "
                                                + map2.get(integer)
                                );
                            }
                        } else {
                            printWriter.println(
                                    "WARNING: Different line number mapping in class '"
                                            + string13
                                            + "' while merging with '"
                                            + loadedChangeLog1.changeLogName
                                            + "'. Line number "
                                            + integer
                                            + " does not appear in '"
                                            + this.changeLogName
                                            + "'."
                            );
                        }
                    }
                } else {
                    this.addLineNumberMappings(string13, this.lineNumberMappings.copyOf(map1));
                }
            }
        }

        return this;
    }

    @Override
    public void setMethodParameterChangeClasses(Object object) {
    }

    @Override
    public void addModuleAutoReflectionClass(Object object, Object object1) {
    }

    public String[] findOriginalMethodNames(String string, String string1, String string2, String string3) {
        String[] strings = null;
        if (this.methodMappingsByNewSignature != null) {
            List list1 = this.methodMappingsByNewSignature.getValues(string, string1, string3);
            if (list1 != null) {
                if (string2 == null) {
                    strings = new String[list1.size()];

                    for (int i = 0; i < strings.length; i++) {
                        strings[i] = (String) ((ObjectPair) list1.get(i)).getFirst();
                    }
                } else {
                    ArrayList arrayList = new ArrayList();

                    for (int i = 0; i < list1.size(); i++) {
                        ObjectPair objectPair = (ObjectPair) list1.get(i);
                        String string4 = (String) objectPair.getSecond();
                        if (string4 == null || string4.equals(string2)) {
                            arrayList.add(objectPair.getFirst());
                        }
                    }

                    if (arrayList.size() > 0) {
                        strings = new String[arrayList.size()];
                        strings = ((java.lang.String[]) (arrayList.toArray(strings)));
                    }
                }
            }
        }

        return strings;
    }

    @Override
    public void addLineNumberMappings(String string, Map map1) {
        if (this.lineNumberMappings == null) {
            this.lineNumberMappings = new TwoKeyMap(this.classCount);
        }

        this.lineNumberMappings.putInnerMap(string, map1);
    }

    @Override
    public void setAutoReflectionClass(Object object) {
    }
}
