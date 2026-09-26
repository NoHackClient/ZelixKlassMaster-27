package com.zelix.klassmaster.obfuscator.rename;

import com.zelix.klassmaster.changelog.ChangeLogMapping;
import com.zelix.klassmaster.classfile.hierarchy.ClassPathResolver;
import com.zelix.klassmaster.config.MixedCaseNamesMode;
import com.zelix.klassmaster.obfuscator.exclude.NameExclusionSet;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.TwoKeyMap;

import java.io.IOException;
import java.util.Set;

public abstract class PackageNameGeneratorBase {
    public ClassPathResolver classPathResolver;
    private final MixedCaseNamesMode mixedCaseNamesMode;
    public boolean randomizeNames;

    public abstract String generatePackageName(
            String string,
            boolean bl,
            boolean bl1,
            ListMultimap listMultimap,
            TwoKeyMap twoKeyMap,
            Set set1,
            ChangeLogMapping changeLogMapping1,
            NameExclusionSet nameExclusionSet
    ) throws IOException;

    public boolean isRandomizeNames() {
        return this.randomizeNames;
    }

    public final boolean isPackageNameAvailable(
            String string,
            String string1,
            boolean bl,
            ListMultimap listMultimap,
            TwoKeyMap twoKeyMap,
            Set set1,
            ChangeLogMapping changeLogMapping1,
            NameExclusionSet nameExclusionSet
    ) throws IOException {
        String string2 = string1 + (string1.length() > 0 ? "/" : "") + string;
        if (listMultimap.containsKey(string2)) {
            return false;
        } else if (changeLogMapping1 != null && changeLogMapping1.isNewPackageName(string2)) {
            return false;
        } else if (changeLogMapping1 != null && changeLogMapping1.isNewClassName(string2)) {
            return false;
        } else if (twoKeyMap.containsKeys(string1, string)) {
            return false;
        } else if (set1 != null && set1.contains(string)) {
            return false;
        } else {
            return bl && !Character.isLowerCase(string.charAt(0))
                    ? false
                    : !nameExclusionSet.isPackageExcluded(string2) || !this.classPathResolver.hasPackageDirectory(string2);
        }
    }

    public PackageNameGeneratorBase(ClassPathResolver classPathResolver1, MixedCaseNamesMode mixedCaseNamesMode1, boolean randomizeNames) {
        this.classPathResolver = classPathResolver1;
        this.mixedCaseNamesMode = mixedCaseNamesMode1;
        this.randomizeNames = randomizeNames;
    }
}
