package com.zelix.klassmaster.proguard;

import com.zelix.klassmaster.util.ArrayCollection;
import com.zelix.klassmaster.util.ZkmUtils;

import java.util.Set;

public interface ProGuardOptionNames {
    String[] FILE_ARG_OPTIONS = new String[]{
            "-adaptclassstrings",
            "-adaptresourcefilecontents",
            "-adaptresourcefilenames",
            "-applymapping",
            "-basedirectory",
            "-classobfuscationdictionary",
            "-dontnote",
            "-dump",
            "-include",
            "-injars",
            "-keepdirectories",
            "-flattenpackagehierarchy",
            "-keeppackagenames",
            "-libraryjars",
            "-obfuscationdictionary",
            "-optimizations",
            "-outjars",
            "-packageobfuscationdictionary",
            "-printconfiguration",
            "-printmapping",
            "-printusage",
            "-printseeds",
            "-renamesourcefileattribute",
            "-repackageclasses",
            "-target"
    };
    String[] PROGUARD_SPECIFIC_OPTION_NAMES = new String[]{
            "-adaptclassstrings",
            "-adaptresourcefilecontents",
            "-adaptresourcefilenames",
            "-dontnote",
            "-dump",
            "-keepdirectories",
            "-flattenpackagehierarchy",
            "-keeppackagenames",
            "-printconfiguration",
            "-printmapping",
            "-printusage",
            "-printseeds",
            "-renamesourcefileattribute",
            "-repackageclasses"
    };
    Set PROGUARD_SPECIFIC_OPTIONS = ZkmUtils.createHashSetFrom(new ArrayCollection(PROGUARD_SPECIFIC_OPTION_NAMES));
}
