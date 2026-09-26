package com.zelix.klassmaster.obfuscator.rename;

import com.zelix.klassmaster.classfile.AbstractMethodInfo;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.exclude.NameExclusionSet;
import com.zelix.klassmaster.obfuscator.parameters.MethodParameterExclusions;
import com.zelix.klassmaster.util.DisableableMap;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MethodNameAssigner extends MethodNameGeneratorBase {
    public ClassHierarchyNode currentClassNode;
    public boolean asciiNames;
    public final boolean letterStartNames;
    public final String namePrefix;
    public final boolean reuseNames;
    public final Map generatorsByDescriptor;
    public List nameFileNames;

    public MethodNameAssigner(
            MethodRenamer methodRenamer1,
            NameExclusionSet nameExclusionSet,
            MethodParameterExclusions methodParameterExclusions,
            boolean asciiNames,
            boolean letterStartNames,
            boolean bl2,
            String string,
            boolean reuseNames,
            List list1
    ) {
        super(methodRenamer1, nameExclusionSet, methodParameterExclusions, bl2);
        this.asciiNames = asciiNames;
        this.letterStartNames = letterStartNames;
        this.namePrefix = string;
        this.reuseNames = reuseNames;
        this.generatorsByDescriptor = ZkmUtils.createHashMap();
        this.nameFileNames = list1;
    }

    @Override
    public String assignMethodName(
            ClassHierarchyNode classHierarchyNode,
            MethodInfo methodInfo1,
            Map map1,
            ListMultimap listMultimap,
            boolean bl,
            Map map2,
            Map map3,
            Map map4,
            DisableableMap disableableMap,
            DisableableMap disableableMap1,
            Set set1,
            boolean bl1,
            boolean bl2
    ) throws ZkmException, IOException {
        MethodSignature methodSignature1 = methodInfo1.getSignature();
        methodSignature1.getDescriptor();
        String string1;
        if (bl2) {
            string1 = MethodParameterExclusions.getPackedDescriptor(methodInfo1);
        } else {
            string1 = methodSignature1.getDescriptor();
        }

        ProgramClass programClass1 = classHierarchyNode.getProgramClass();
        if (this.reuseNames) {
            List list1 = listMultimap.getValues(string1);
            if (list1 != null) {
                for (int i = 0; i < list1.size(); i++) {
                    String string = (String) list1.get(i);
                    MethodSignature methodSignature2 = new MethodSignature(string, methodSignature1.getDescriptor());
                    MethodSignature methodSignature3 = new MethodSignature(string, string1);
                    if (!methodSignature2.equals(methodSignature1)
                            && !map2.containsKey(methodSignature2)
                            && !super.overrideAnalyzer.isSignatureReserved(methodSignature2)
                            && !super.overrideAnalyzer
                            .isReserved(classHierarchyNode, super.overrideAnalyzer.isFullDescriptorMode() ? methodSignature2 : methodSignature2.getNameTypeSignature())
                            && (
                            !bl2
                                    || !map2.containsKey(methodSignature3)
                                    && !super.overrideAnalyzer.isSignatureReserved(methodSignature3)
                                    && !super.overrideAnalyzer
                                    .isReserved(
                                            classHierarchyNode, super.overrideAnalyzer.isFullDescriptorMode() ? methodSignature3 : methodSignature3.getNameTypeSignature()
                                    )
                    )) {
                        AbstractMethodInfo abstractMethodInfo = (AbstractMethodInfo) map1.get(methodSignature2);
                        if (abstractMethodInfo != null) {
                            ProgramClass programClass2 = (ProgramClass) programClass1.findInheritedMethodReferrer(abstractMethodInfo);
                            if (programClass2 == null) {
                                if (bl2 && !methodInfo1.getSourceName().equals(string)) {
                                    methodInfo1.setNameChanged(true);
                                }

                                return string;
                            }
                        }
                    }
                }
            }
        }

        String string2 = this.assignNewName(classHierarchyNode, methodInfo1, map1, bl, map2, map3, map4, disableableMap, disableableMap1, set1, bl1, bl2);
        listMultimap.addValue(string1, string2);
        return string2;
    }

    @Override
    public String nextCandidateName(ClassHierarchyNode classHierarchyNode, String string) {
        Map map1;
        if (classHierarchyNode != this.currentClassNode) {
            this.generatorsByDescriptor.clear();
            this.currentClassNode = classHierarchyNode;
            map1 = this.generatorsByDescriptor;
        } else {
            map1 = this.generatorsByDescriptor;
        }

        SequentialNameGenerator sequentialNameGenerator = (SequentialNameGenerator) map1.get(string);
        if (sequentialNameGenerator == null) {
            if (!this.asciiNames) {
                sequentialNameGenerator = new SequentialNameGenerator(
                        SequentialNameGenerator.UNICODE_MIXED_CASE_CHARS, SequentialNameGenerator.UNICODE_MIXED_CASE_CHARS, this.nameFileNames, super.randomizeNames
                );
                map1 = this.generatorsByDescriptor;
            } else if (this.letterStartNames) {
                if (HiddenOptionFlags.ALT_METHOD_NAME_CHARS) {
                    sequentialNameGenerator = new SequentialNameGenerator(
                            SequentialNameGenerator.LETTER_CHARS, SequentialNameGenerator.ALPHANUMERIC_CHARS, this.nameFileNames, super.randomizeNames
                    );
                    map1 = this.generatorsByDescriptor;
                } else {
                    sequentialNameGenerator = new SequentialNameGenerator(
                            SequentialNameGenerator.LETTER_UNDERSCORE_CHARS, SequentialNameGenerator.IDENTIFIER_CHARS, this.nameFileNames, super.randomizeNames
                    );
                    map1 = this.generatorsByDescriptor;
                }
            } else {
                sequentialNameGenerator = new SequentialNameGenerator(
                        SequentialNameGenerator.DIGIT_CHARS, SequentialNameGenerator.IDENTIFIER_CHARS, this.nameFileNames, super.randomizeNames
                );
                map1 = this.generatorsByDescriptor;
            }

            map1.put(string, sequentialNameGenerator);
        }

        return sequentialNameGenerator.nextPrefixedName(this.namePrefix);
    }
}
