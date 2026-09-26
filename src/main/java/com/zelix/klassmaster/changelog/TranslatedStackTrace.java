package com.zelix.klassmaster.changelog;

import com.zelix.klassmaster.classfile.hierarchy.ClasspathClassResolver;
import com.zelix.klassmaster.config.HiddenOptionFlags;

public abstract class TranslatedStackTrace {
    public StackTraceLine[] lines;

    public boolean hasMappedClasses() {
        int ba = 0;
        int bb = 0;

        for (StackTraceLine[] stackTraceLines = this.lines; bb < stackTraceLines.length; stackTraceLines = this.lines) {
            if (this.lines[ba].isClassMapped()) {
                return true;
            }

            bb = ++ba;
        }

        return false;
    }

    public String format(int ba, LoadedChangeLog loadedChangeLog, ClasspathClassResolver classpathClassResolver) {
        StringBuffer stringBuffer = new StringBuffer(1024);
        boolean bl = false;
        StackTraceLine stackTraceLine1 = null;

        for (int i = this.lines.length - 1; i >= 0; i += -1) {
            StackTraceLine stackTraceLine2 = this.lines[i];
            if (bl) {
                if (stackTraceLine2.isValidFrame()) {
                    String string = stackTraceLine2.getClassName();
                    if (string.startsWith("java.") || string.startsWith("sun.")) {
                        stringBuffer.append("<Break in method call trace.>" + HiddenOptionFlags.LINE_SEPARATOR);
                    } else if (stackTraceLine1.isStaticInitializer()) {
                        stringBuffer.append("<Break in method call trace due to class initializer call.>" + HiddenOptionFlags.LINE_SEPARATOR);
                    } else {
                        stringBuffer.append("<Break in method call trace. Could be due to JIT compiler inlining of method.>" + HiddenOptionFlags.LINE_SEPARATOR);
                    }
                } else {
                    stringBuffer.append("<Break in method call trace.>" + HiddenOptionFlags.LINE_SEPARATOR);
                }
            }

            stringBuffer.append(stackTraceLine2.formatLine(ba, loadedChangeLog, classpathClassResolver));
            if (stackTraceLine2.hasError()) {
                stringBuffer.append("<" + stackTraceLine2.getErrorMessage() + ">" + HiddenOptionFlags.LINE_SEPARATOR);
            }

            bl = stackTraceLine2.hasCallTraceBreak();
            stackTraceLine1 = stackTraceLine2;
        }

        return stringBuffer.toString();
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
    }
}
