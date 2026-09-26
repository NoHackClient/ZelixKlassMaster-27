package com.zelix.klassmaster.script;

public interface ExclusionStatementNames {
    String[] EXCLUSION_STATEMENT_NAMES = new String[]{
            "classpath",
            "ignoreMissingReferences",
            "removeMethodCallsInclude",
            "removeMethodCallsExclude",
            "trimExclude",
            "trimUnexclude",
            "trim",
            "exclude",
            "unexclude",
            "stringEncryptionExclude",
            "stringEncryptionUnexclude",
            "obfuscateFlowExclude",
            "obfuscateFlowUnexclude",
            "accessedByReflection",
            "accessedByReflectionExclude",
            "obfuscateReferencesInclude",
            "obfuscateReferencesExclude",
            "fixedClasses",
            "methodParameterChangesInclude",
            "methodParameterChangesExclude",
            "obfuscate"
    };
}
