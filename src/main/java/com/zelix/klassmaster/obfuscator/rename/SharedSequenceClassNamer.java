package com.zelix.klassmaster.obfuscator.rename;

import com.zelix.klassmaster.config.MixedCaseNamesMode;
import com.zelix.klassmaster.util.ListMultimap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SharedSequenceClassNamer extends FileClassNameGenerator {
    public UniqueNameGenerator sharedGenerator;

    public SharedSequenceClassNamer(
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
        super(classRenameClashChecker, bl, bl1, string, ba, bb, bl2, mixedCaseNamesMode1, hashMap, listMultimap, map1, list1);
    }

    @Override
    public UniqueNameGenerator createNameGenerator(char[] ba, char[] bb, char[] bc, char[] bd, List list1) {
        if (this.sharedGenerator == null) {
            this.sharedGenerator = new UniqueNameGenerator(ba, bb, bc, bd, list1, super.randomizeNames);
        }

        return this.sharedGenerator;
    }
}
