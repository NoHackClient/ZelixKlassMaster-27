package com.zelix.klassmaster.obfuscator.rename;

import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.config.MixedCaseNamesMode;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.Triple;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileClassNameGenerator extends ClassNameGenerator {
    public Map generatorsByPackage = ZkmUtils.createHashMap();
    public Map generatorsByOuterPrefix = ZkmUtils.createHashMap();
    public char[] firstChars;
    public char[] subsequentChars;
    public char[] archiveFirstChars;
    public char[] archiveSubsequentChars;
    public String namePrefix;
    public List nameFileNames;

    public FileClassNameGenerator(
            ClassRenameClashChecker classRenameClashChecker,
            boolean bl,
            boolean bl1,
            String string,
            int ba,
            int bb,
            boolean bl2,
            MixedCaseNamesMode mixedCaseNamesMode1,
            HashMap hashMap,
            ListMultimap listMultimap,
            Map map1,
            List list1
    ) {
        super(classRenameClashChecker, hashMap, listMultimap, map1, ba, bb, bl2);
        switch (mixedCaseNamesMode1.getValue()) {
            case 0:
                if (!bl) {
                    this.firstChars = SequentialNameGenerator.UNICODE_MIXED_CASE_CHARS.clone();
                    this.subsequentChars = SequentialNameGenerator.UNICODE_MIXED_CASE_CHARS.clone();
                    this.archiveFirstChars = SequentialNameGenerator.UNICODE_MIXED_CASE_CHARS.clone();
                    this.archiveSubsequentChars = SequentialNameGenerator.UNICODE_MIXED_CASE_CHARS.clone();
                } else if (bl1) {
                    this.firstChars = SequentialNameGenerator.LETTER_UNDERSCORE_CHARS.clone();
                    this.subsequentChars = SequentialNameGenerator.IDENTIFIER_CHARS.clone();
                    this.archiveFirstChars = SequentialNameGenerator.LETTER_UNDERSCORE_CHARS.clone();
                    this.archiveSubsequentChars = SequentialNameGenerator.IDENTIFIER_CHARS.clone();
                } else {
                    this.firstChars = SequentialNameGenerator.DIGIT_CHARS.clone();
                    this.subsequentChars = SequentialNameGenerator.IDENTIFIER_CHARS.clone();
                    this.archiveFirstChars = SequentialNameGenerator.DIGIT_CHARS.clone();
                    this.archiveSubsequentChars = SequentialNameGenerator.IDENTIFIER_CHARS.clone();
                }
                break;
            case 1:
                if (!bl) {
                    this.firstChars = SequentialNameGenerator.UNICODE_LOWER_CHARS.clone();
                    this.subsequentChars = SequentialNameGenerator.UNICODE_LOWER_CHARS.clone();
                    this.archiveFirstChars = SequentialNameGenerator.UNICODE_LOWER_CHARS.clone();
                    this.archiveSubsequentChars = SequentialNameGenerator.UNICODE_LOWER_CHARS.clone();
                } else if (bl1) {
                    this.firstChars = SequentialNameGenerator.LOWER_UNDERSCORE_CHARS.clone();
                    this.subsequentChars = SequentialNameGenerator.LOWER_DIGIT_UNDERSCORE_CHARS.clone();
                    this.archiveFirstChars = SequentialNameGenerator.LOWER_UNDERSCORE_CHARS.clone();
                    this.archiveSubsequentChars = SequentialNameGenerator.LOWER_DIGIT_UNDERSCORE_CHARS.clone();
                } else {
                    this.firstChars = SequentialNameGenerator.DIGIT_CHARS.clone();
                    this.subsequentChars = SequentialNameGenerator.LOWER_DIGIT_UNDERSCORE_CHARS.clone();
                    this.archiveFirstChars = SequentialNameGenerator.DIGIT_CHARS.clone();
                    this.archiveSubsequentChars = SequentialNameGenerator.LOWER_DIGIT_UNDERSCORE_CHARS.clone();
                }
                break;
            case 2:
                if (!bl) {
                    this.firstChars = SequentialNameGenerator.UNICODE_LOWER_CHARS.clone();
                    this.subsequentChars = SequentialNameGenerator.UNICODE_LOWER_CHARS.clone();
                    this.archiveFirstChars = SequentialNameGenerator.UNICODE_MIXED_CASE_CHARS.clone();
                    this.archiveSubsequentChars = SequentialNameGenerator.UNICODE_MIXED_CASE_CHARS.clone();
                } else if (bl1) {
                    this.firstChars = SequentialNameGenerator.LOWER_UNDERSCORE_CHARS.clone();
                    this.subsequentChars = SequentialNameGenerator.LOWER_DIGIT_UNDERSCORE_CHARS.clone();
                    this.archiveFirstChars = SequentialNameGenerator.LETTER_UNDERSCORE_CHARS.clone();
                    this.archiveSubsequentChars = SequentialNameGenerator.IDENTIFIER_CHARS.clone();
                } else {
                    this.firstChars = SequentialNameGenerator.DIGIT_CHARS.clone();
                    this.subsequentChars = SequentialNameGenerator.LOWER_DIGIT_UNDERSCORE_CHARS.clone();
                    this.archiveFirstChars = SequentialNameGenerator.DIGIT_CHARS.clone();
                    this.archiveSubsequentChars = SequentialNameGenerator.IDENTIFIER_CHARS.clone();
                }
        }

        this.namePrefix = string;
        this.nameFileNames = list1;
        if (classRenameClashChecker.isRandomizeOrder()) {
            ZkmUtils.shuffleChars(this.subsequentChars);
            ZkmUtils.shuffleChars(this.archiveSubsequentChars);
        }
    }

    public UniqueNameGenerator createNameGenerator(char[] ba, char[] bb, char[] bc, char[] bd, List list1) {
        return new UniqueNameGenerator(ba, bb, bc, bd, list1, super.randomizeNames);
    }

    @Override
    public String generateNewName(ClassHierarchyNode classHierarchyNode, String string, HashMap hashMap, String string1, Triple triple, boolean bl) throws IOException {
        String string2;
        String string3;
        if (triple != null) {
            string2 = (String) triple.getSecond();
            string3 = (String) triple.getThird();
            if (triple.getFirst() == null) {
                string2 = string2 + "zzzz";
            }
        } else {
            string2 = "";
            string3 = "";
        }

        boolean referencedFromXml = classHierarchyNode.getProgramClass().isReferencedFromXml();
        boolean bl2 = bl;
        HashMap hashMap1 = hashMap;
        String string4 = string3;
        String string5 = string2;
        String string6 = string1;
        String string7 = string;
        return this.buildUniqueClassName(string7, string6, string5, string4, hashMap1, bl2, referencedFromXml);
    }

    @Override
    public String generateFallbackName(ClassHierarchyNode classHierarchyNode, boolean bl) throws IOException {
        ProgramClass programClass1 = classHierarchyNode.getProgramClass();
        Triple triple = super.exclusionSet.getClassLink(programClass1);
        String string;
        String string1;
        FileClassNameGenerator fileClassNameGenerator1;
        String string6;
        String string7;
        if (triple != null) {
            string = (String) triple.getSecond();
            string1 = (String) triple.getThird();
            fileClassNameGenerator1 = this;
            string6 = "";
            string7 = classHierarchyNode.getClassName();
        } else {
            string = "";
            string1 = "";
            fileClassNameGenerator1 = this;
            string6 = "";
            string7 = classHierarchyNode.getClassName();
        }

        Boolean boolean2 = programClass1.isReferencedFromXml();
        Boolean boolean1 = bl;
        HashMap hashMap1 = (HashMap) null;
        boolean bl3 = boolean1;
        boolean bl1 = boolean2;
        boolean bl2 = bl3;
        HashMap hashMap = hashMap1;
        String string2 = string1;
        String string3 = string;
        String string4 = string7;
        String string5 = string6;
        return fileClassNameGenerator1.buildUniqueClassName(string5, string4, string3, string2, hashMap, bl2, bl1);
    }

    public String nextNameInScope(String string, String string1, HashMap hashMap, boolean bl) {
        if (string1.equals("")) {
            UniqueNameGenerator uniqueNameGenerator1 = (UniqueNameGenerator) this.generatorsByPackage.get(string);
            if (uniqueNameGenerator1 == null) {
                uniqueNameGenerator1 = this.createNameGenerator(
                        this.firstChars, this.subsequentChars, this.archiveFirstChars, this.archiveSubsequentChars, this.nameFileNames
                );
                this.generatorsByPackage.put(string, uniqueNameGenerator1);
            }

            return uniqueNameGenerator1.nextUniqueName(this.namePrefix, bl);
        } else {
            UniqueNameGenerator uniqueNameGenerator = (UniqueNameGenerator) this.generatorsByOuterPrefix.get(string1);
            if (uniqueNameGenerator == null) {
                uniqueNameGenerator = this.createNameGenerator(
                        this.firstChars, this.subsequentChars, this.archiveFirstChars, this.archiveSubsequentChars, this.nameFileNames
                );
                this.generatorsByOuterPrefix.put(string1, uniqueNameGenerator);
            }

            UniqueNameGenerator uniqueNameGenerator2 = uniqueNameGenerator;
            String string3 = this.namePrefix;

            while (true) {
                String string2 = uniqueNameGenerator2.nextUniqueName(string3, bl);
                if (string2 != null && HiddenOptionFlags.MARK_GENERATED_CLASS_NAMES) {
                    string2 = string2 + "__";
                }

                if (string2 != null) {
                    if (hashMap == null || !hashMap.containsValue(string2)) {
                        return string2;
                    }

                    uniqueNameGenerator2 = uniqueNameGenerator;
                    string3 = this.namePrefix;
                } else {
                    uniqueNameGenerator2 = uniqueNameGenerator;
                    string3 = this.namePrefix;
                }
            }
        }
    }

    public String buildUniqueClassName(String string, String string1, String string2, String string3, HashMap hashMap, boolean bl, boolean bl1) throws IOException {
        String string5 = "";
        if (string.equals("")) {
            string5 = this.getRenamedPackagePrefix(string1);
        }

        boolean bl2 = bl1
                && string.length() == 0
                && string2.length() == 0
                && (
                this.namePrefix == null
                        || this.namePrefix.length() == 0
                        || !Character.isUpperCase(this.namePrefix.charAt(0)) && Character.isLetter(this.namePrefix.charAt(0))
        );

        String string4;
        do {
            String string6 = this.nextNameInScope(string5, string, hashMap, bl2 ? true : bl);
            if (bl2 && !Character.isUpperCase(string6.charAt(0)) && Character.isLetter(string6.charAt(0))) {
                StringBuilder stringBuilder = new StringBuilder(string6);
                char ba = Character.toUpperCase(string6.charAt(0));
                stringBuilder.setCharAt(0, ba);
                string6 = stringBuilder.toString();
            }

            string4 = string5 + string + string2 + string6 + string3;
        } while (!this.isNameAvailable(string1, string4, bl, bl2));

        return string4;
    }
}
