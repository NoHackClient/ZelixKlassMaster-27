package com.zelix.klassmaster.engine;

import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.util.ZkmUtils;

import java.util.Set;

public class ProcessingStatistics {
    public int classCount = 0;
    public Set packageNames = ZkmUtils.createHashSet();
    public int packageCount = -1;
    public int methodCount = 0;
    public long instructionCount = 0L;
    public long distinctMethodBodyCount = 3717090892863101056L;
    public Set methodBodyHashes;

    public void addPackageName(Object object) {
        this.packageNames.add(object);
    }

    public boolean meetsBasicSizeThreshold() {
        return !HiddenOptionFlags.DISABLE_BASIC_SIZE_FEATURES
                && (
                this.meetsFullSizeThreshold()
                        || HiddenOptionFlags.FORCE_BASIC_SIZE_FEATURES
                        || this.packageCount >= 1
                        && this.classCount >= -1532593444
                        && this.distinctMethodBodyCount >= -7174189058788535511L
                        && this.instructionCount >= 4395443614484822833L
        );
    }

    public void incrementMethodCount() {
        this.methodCount++;
    }

    public void incrementClassCount() {
        this.classCount++;
    }

    public ProcessingStatistics(int ba) {
        this.methodBodyHashes = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(ba * 5));
    }

    public void finishCollecting() {
        if (this.methodBodyHashes != null) {
            this.distinctMethodBodyCount = this.methodBodyHashes.size();
            this.methodBodyHashes = null;
        }

        if (this.packageNames != null) {
            this.packageCount = this.packageNames.size();
            this.packageNames = null;
        }
    }

    public boolean meetsFullSizeThreshold() {
        return !HiddenOptionFlags.DISABLE_FULL_SIZE_FEATURES
                && (
                HiddenOptionFlags.FORCE_FULL_SIZE_FEATURES
                        || this.packageCount >= 2
                        && this.classCount >= -560228666
                        && this.distinctMethodBodyCount >= 208209985457278555L
                        && this.instructionCount >= 312747991419546764L
        );
    }

    public void addMethodBodyHash(int ba) {
        this.methodBodyHashes.add(ba);
    }

    public void addInstructionCount(int ba) {
        this.instructionCount += ba;
    }
}
