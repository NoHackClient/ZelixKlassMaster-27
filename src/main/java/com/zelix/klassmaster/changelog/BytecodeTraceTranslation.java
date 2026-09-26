package com.zelix.klassmaster.changelog;

import java.util.Enumeration;
import java.util.HashSet;

public class BytecodeTraceTranslation extends TranslatedStackTrace {
    public BytecodeTraceTranslation(FrameCandidateSets frameCandidateSets, LoadedChangeLog loadedChangeLog) throws StackTraceTranslateException {
        super.lines = new StackTraceLine[frameCandidateSets.getFrameCount()];
        int ba = 0;
        Enumeration enumeration = frameCandidateSets.enumerateFrameCandidates();

        while (enumeration.hasMoreElements()) {
            HashSet hashSet = (HashSet) enumeration.nextElement();
            AnalyzedStackTraceLine analyzedStackTraceLine = new AnalyzedStackTraceLine(hashSet, loadedChangeLog);
            super.lines[ba++] = analyzedStackTraceLine;
        }
    }
}
