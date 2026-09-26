package com.zelix.klassmaster.changelog;

import java.util.Enumeration;

public class ParsedLineStackTrace extends TranslatedStackTrace {
    public int lineCount;

    public ParsedLineStackTrace(ParsedStackTrace parsedStackTrace, LoadedChangeLog loadedChangeLog) throws StackTraceTranslateException {
        super.lines = new StackTraceLine[parsedStackTrace.getLineCount()];
        Enumeration enumeration = parsedStackTrace.enumerateReversed();

        while (enumeration.hasMoreElements()) {
            StackTraceFrameLine stackTraceFrameLine = (StackTraceFrameLine) enumeration.nextElement();
            ParsedLineStackFrame parsedLineStackFrame = new ParsedLineStackFrame(stackTraceFrameLine, loadedChangeLog);
            super.lines[this.lineCount++] = parsedLineStackFrame;
        }
    }
}
