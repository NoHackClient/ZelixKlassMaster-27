package com.zelix.klassmaster.obfuscator.rename;

import com.zelix.klassmaster.classfile.AbstractFieldInfo;
import com.zelix.klassmaster.classfile.FieldInfo;
import com.zelix.klassmaster.classfile.FieldSignature;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.NumericStringUtil;
import com.zelix.klassmaster.util.SetMultiMap;
import com.zelix.klassmaster.util.TwoKeyMap;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DefaultFieldNameGenerator extends FieldNameAssigner {
    public SequentialNameGenerator nameGenerator;
    public String consumedMarker = "";
    public boolean letterStartNames;
    public boolean asciiNames;
    public String namePrefix;
    public boolean reuseNames;
    public List nameFileNames;

    @Override
    public String assignFieldName(
            ClassHierarchyNode classHierarchyNode,
            FieldInfo fieldInfo,
            String string,
            String string1,
            Map map1,
            ListMultimap listMultimap,
            ListMultimap listMultimap1,
            Map map2,
            boolean bl,
            Map map3,
            HashMap hashMap,
            SetMultiMap setMultiMap,
            TwoKeyMap twoKeyMap
    ) {
        if (super.keepInnerClassInfoMode == 1
                || ((!string.startsWith("this$") || !bl) && !string.startsWith("this+") || !NumericStringUtil.isInteger(string.substring("this$".length())))
                && (!string.startsWith("val$") || !bl)
                && !string.startsWith("val+")) {
            ProgramClass programClass1 = classHierarchyNode.getProgramClass();
            if (this.reuseNames) {
                List list1 = listMultimap.getValues(string1);
                if (list1 != null) {
                    for (int i = 0; i < list1.size(); i++) {
                        String string3 = (String) list1.get(i);
                        if (string3 != this.consumedMarker) {
                            String string2 = string3;
                            List list2;
                            int bb;
                            String string5;
                            if (!string2.equals(string)) {
                                if (this.isNameAvailable(string, string2, fieldInfo, map2, map1, setMultiMap, twoKeyMap, programClass1, map3, hashMap)) {
                                    map2.put(string2, fieldInfo);
                                    list1.set(i, this.consumedMarker);
                                    return string2;
                                }

                                list2 = list1;
                                bb = i;
                                string5 = this.consumedMarker;
                            } else {
                                list2 = list1;
                                bb = i;
                                string5 = this.consumedMarker;
                            }

                            list2.set(bb, string5);
                        }
                    }
                }
            }

            boolean bl1 = HiddenOptionFlags.MARK_RENAMED_LOCALS;

            String string4;
            do {
                string4 = this.nextNameForClass(classHierarchyNode);
                if (bl1) {
                    string4 = string + '_' + string4;
                }
            } while (!this.isNameAvailable(string, string4, fieldInfo, map2, map1, setMultiMap, twoKeyMap, programClass1, map3, hashMap));

            map1.put(string4, fieldInfo);
            map2.put(string4, fieldInfo);
            listMultimap1.addValue(string1, string4);
            return string4;
        } else {
            map1.put(string, fieldInfo);
            map2.put(string, fieldInfo);
            return null;
        }
    }

    public DefaultFieldNameGenerator(int ba, boolean asciiNames, boolean letterStartNames, boolean bl2, String string, boolean reuseNames, List list1) {
        super(ba, bl2);
        this.letterStartNames = letterStartNames;
        this.asciiNames = asciiNames;
        this.namePrefix = string;
        this.reuseNames = reuseNames;
        this.nameFileNames = list1;
    }

    public boolean isNameAvailable(
            String string,
            String string1,
            FieldInfo fieldInfo,
            Map map1,
            Map map2,
            SetMultiMap setMultiMap,
            TwoKeyMap twoKeyMap,
            ProgramClass programClass1,
            Map map3,
            HashMap hashMap
    ) {
        if (string.equals(string1)) {
            return false;
        }

        if (map1.containsKey(string1)) {
            return false;
        }

        if (setMultiMap.containsValue(programClass1, new FieldSignature(string1, fieldInfo.getDescriptor()))) {
            return false;
        }

        Map map4 = twoKeyMap.getInnerMap(programClass1);
        if (map4 != null) {
            Iterator iterator = map4.keySet().iterator();

            while (iterator.hasNext()) {
                FieldInfo fieldInfo1 = (FieldInfo) iterator.next();
                if (this.hasAssignedName(string1, fieldInfo1, map3, hashMap)) {
                    return false;
                }
            }
        }

        Enumeration enumeration = programClass1.enumerateInheritedReferencedFields();
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                AbstractFieldInfo abstractFieldInfo = (AbstractFieldInfo) enumeration.nextElement();
                if (this.hasAssignedName(string1, abstractFieldInfo, map3, hashMap)) {
                    return false;
                }
            }
        }

        return !map2.containsKey(string1) || this.reuseNames;
    }

    public boolean hasAssignedName(String string, AbstractFieldInfo abstractFieldInfo, Map map1, HashMap hashMap) {
        return map1 != null && string.equals(map1.get(abstractFieldInfo)) || string.equals(hashMap.get(abstractFieldInfo));
    }

    public String nextNameForClass(ClassHierarchyNode classHierarchyNode) {
        if (classHierarchyNode != super.currentClassNode) {
            super.currentClassNode = classHierarchyNode;
            if (!this.asciiNames) {
                this.nameGenerator = new SequentialNameGenerator(
                        SequentialNameGenerator.UNICODE_MIXED_CASE_CHARS, SequentialNameGenerator.UNICODE_MIXED_CASE_CHARS, this.nameFileNames, super.randomizeNames
                );
            } else if (this.letterStartNames) {
                if (HiddenOptionFlags.ALT_FIELD_NAME_CHARS) {
                    this.nameGenerator = new SequentialNameGenerator(
                            SequentialNameGenerator.LETTER_CHARS, SequentialNameGenerator.ALPHANUMERIC_CHARS, this.nameFileNames, super.randomizeNames
                    );
                } else {
                    this.nameGenerator = new SequentialNameGenerator(
                            SequentialNameGenerator.LETTER_UNDERSCORE_CHARS, SequentialNameGenerator.IDENTIFIER_CHARS, this.nameFileNames, super.randomizeNames
                    );
                }
            } else {
                this.nameGenerator = new SequentialNameGenerator(
                        SequentialNameGenerator.DIGIT_CHARS, SequentialNameGenerator.IDENTIFIER_CHARS, this.nameFileNames, super.randomizeNames
                );
            }
        }

        return this.nameGenerator.nextPrefixedName(this.namePrefix);
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
    }
}
