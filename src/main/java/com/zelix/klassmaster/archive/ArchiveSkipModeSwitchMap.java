package com.zelix.klassmaster.archive;

public class ArchiveSkipModeSwitchMap {
    public static final int[] SKIP_MODE_SWITCH_MAP = new int[ArchiveSkipMode.values().length];

    static {
        try {
            SKIP_MODE_SWITCH_MAP[ArchiveSkipMode.TOP_LEVEL.ordinal()] = 1;
        } catch (NoSuchFieldError noSuchFieldError2) {
        }

        try {
            SKIP_MODE_SWITCH_MAP[ArchiveSkipMode.SKIP.ordinal()] = 2;
        } catch (NoSuchFieldError noSuchFieldError1) {
        }

        try {
            SKIP_MODE_SWITCH_MAP[ArchiveSkipMode.UNSKIP.ordinal()] = 3;
        } catch (NoSuchFieldError noSuchFieldError) {
        }
    }

    private ArchiveSkipModeSwitchMap() {
    }
}
