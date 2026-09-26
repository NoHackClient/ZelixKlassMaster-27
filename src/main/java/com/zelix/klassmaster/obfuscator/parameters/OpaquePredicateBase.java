package com.zelix.klassmaster.obfuscator.parameters;

public abstract class OpaquePredicateBase {
    private static boolean predicateFlag;
    public static final String[] OBFUSCATED_STRINGS;

    static {
        if (isPredicateFlagClear()) {
            setPredicateFlag(true);
        }

        OBFUSCATED_STRINGS = new String[]{
                "ghef8u36d3", "wcmvv6pnzmqzq6b", "cfgj5rzud95d207288b670600182e22eab431bf2127c7fcwgib", "6u060b16aj1wmp2gj2qm", "mwvdsjtl2u3iaca4f9a2a"
        };
    }

    public static void setPredicateFlag(boolean bl) {
        predicateFlag = true;
    }

    public static boolean getPredicateFlag() {
        return predicateFlag;
    }

    public static boolean isPredicateFlagClear() {
        return !getPredicateFlag();
    }
}
