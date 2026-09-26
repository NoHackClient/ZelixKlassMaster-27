package com.zelix.klassmaster.obfuscator.rename;

import com.zelix.klassmaster.changelog.ChangeLogMapping;
import com.zelix.klassmaster.classfile.hierarchy.ClassPathResolver;
import com.zelix.klassmaster.config.MixedCaseNamesMode;
import com.zelix.klassmaster.obfuscator.exclude.NameExclusionSet;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.TwoKeyMap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PackageNameGenerator extends PackageNameGeneratorBase {
    public final Map generatorsByParent = ZkmUtils.createHashMap();
    public final String namePrefix;
    public List nameFileNames;
    public char[] firstChars;
    public char[] subsequentChars;
    public char[] archiveFirstChars;
    public char[] archiveSubsequentChars;

    @Override
    public String generatePackageName(
            String string,
            boolean bl,
            boolean bl1,
            ListMultimap listMultimap,
            TwoKeyMap twoKeyMap,
            Set set1,
            ChangeLogMapping changeLogMapping1,
            NameExclusionSet nameExclusionSet
    ) throws IOException {
        UniqueNameGenerator uniqueNameGenerator = (UniqueNameGenerator) this.generatorsByParent.get(string);
        if (uniqueNameGenerator == null) {
            uniqueNameGenerator = new UniqueNameGenerator(
                    this.firstChars, this.subsequentChars, this.archiveFirstChars, this.archiveSubsequentChars, this.nameFileNames, this.isRandomizeNames()
            );
            this.generatorsByParent.put(string, uniqueNameGenerator);
        }

        boolean bl2 = bl1 && (this.namePrefix == null || this.namePrefix.length() == 0 || Character.isLetter(this.namePrefix.charAt(0)));

        String string1;
        do {
            string1 = this.nextName(uniqueNameGenerator, bl2 ? false : bl);
            if (bl2 && !Character.isLowerCase(string1.charAt(0))) {
                StringBuilder stringBuilder = new StringBuilder(string1);
                char ba = Character.toLowerCase(string1.charAt(0));
                stringBuilder.setCharAt(0, ba);
                string1 = stringBuilder.toString();
            }
        } while (!this.isPackageNameAvailable(string1, string, bl2, listMultimap, twoKeyMap, set1, changeLogMapping1, nameExclusionSet));

        return string1;
    }

    public PackageNameGenerator(
            ClassPathResolver classPathResolver1, boolean bl, boolean bl1, boolean bl2, String string, MixedCaseNamesMode mixedCaseNamesMode1, List list1
    ) {
        super(classPathResolver1, mixedCaseNamesMode1, bl2);
        this.namePrefix = string;
        this.nameFileNames = list1;
        switch (mixedCaseNamesMode1.getValue()) {
            case 0:
                if (!bl) {
                    this.firstChars = SequentialNameGenerator.UNICODE_MIXED_CASE_CHARS;
                    this.subsequentChars = SequentialNameGenerator.UNICODE_MIXED_CASE_CHARS;
                    this.archiveFirstChars = SequentialNameGenerator.UNICODE_MIXED_CASE_CHARS;
                    this.archiveSubsequentChars = SequentialNameGenerator.UNICODE_MIXED_CASE_CHARS;
                } else if (bl1) {
                    this.firstChars = SequentialNameGenerator.LETTER_UNDERSCORE_CHARS;
                    this.subsequentChars = SequentialNameGenerator.IDENTIFIER_CHARS;
                    this.archiveFirstChars = SequentialNameGenerator.LETTER_UNDERSCORE_CHARS;
                    this.archiveSubsequentChars = SequentialNameGenerator.IDENTIFIER_CHARS;
                } else {
                    this.firstChars = SequentialNameGenerator.DIGIT_CHARS;
                    this.subsequentChars = SequentialNameGenerator.IDENTIFIER_CHARS;
                    this.archiveFirstChars = SequentialNameGenerator.DIGIT_CHARS;
                    this.archiveSubsequentChars = SequentialNameGenerator.IDENTIFIER_CHARS;
                }
                break;
            case 1:
                if (!bl) {
                    this.firstChars = SequentialNameGenerator.UNICODE_LOWER_CHARS;
                    this.subsequentChars = SequentialNameGenerator.UNICODE_LOWER_CHARS;
                    this.archiveFirstChars = SequentialNameGenerator.UNICODE_LOWER_CHARS;
                    this.archiveSubsequentChars = SequentialNameGenerator.UNICODE_LOWER_CHARS;
                } else if (bl1) {
                    this.firstChars = SequentialNameGenerator.LOWER_UNDERSCORE_CHARS;
                    this.subsequentChars = SequentialNameGenerator.LOWER_DIGIT_UNDERSCORE_CHARS;
                    this.archiveFirstChars = SequentialNameGenerator.LOWER_UNDERSCORE_CHARS;
                    this.archiveSubsequentChars = SequentialNameGenerator.LOWER_DIGIT_UNDERSCORE_CHARS;
                } else {
                    this.firstChars = SequentialNameGenerator.DIGIT_CHARS;
                    this.subsequentChars = SequentialNameGenerator.LOWER_DIGIT_UNDERSCORE_CHARS;
                    this.archiveFirstChars = SequentialNameGenerator.DIGIT_CHARS;
                    this.archiveSubsequentChars = SequentialNameGenerator.LOWER_DIGIT_UNDERSCORE_CHARS;
                }
                break;
            case 2:
                if (!bl) {
                    this.firstChars = SequentialNameGenerator.UNICODE_LOWER_CHARS;
                    this.subsequentChars = SequentialNameGenerator.UNICODE_LOWER_CHARS;
                    this.archiveFirstChars = SequentialNameGenerator.UNICODE_MIXED_CASE_CHARS;
                    this.archiveSubsequentChars = SequentialNameGenerator.UNICODE_MIXED_CASE_CHARS;
                } else if (bl1) {
                    this.firstChars = SequentialNameGenerator.LOWER_UNDERSCORE_CHARS;
                    this.subsequentChars = SequentialNameGenerator.LOWER_DIGIT_UNDERSCORE_CHARS;
                    this.archiveFirstChars = SequentialNameGenerator.LETTER_UNDERSCORE_CHARS;
                    this.archiveSubsequentChars = SequentialNameGenerator.IDENTIFIER_CHARS;
                } else {
                    this.firstChars = SequentialNameGenerator.DIGIT_CHARS;
                    this.subsequentChars = SequentialNameGenerator.LOWER_DIGIT_UNDERSCORE_CHARS;
                    this.archiveFirstChars = SequentialNameGenerator.DIGIT_CHARS;
                    this.archiveSubsequentChars = SequentialNameGenerator.IDENTIFIER_CHARS;
                }
        }
    }

    public String nextName(UniqueNameGenerator uniqueNameGenerator, boolean bl) {
        return uniqueNameGenerator.nextUniqueName(this.namePrefix, bl);
    }
}
