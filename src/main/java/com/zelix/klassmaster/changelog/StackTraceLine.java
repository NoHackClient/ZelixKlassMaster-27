package com.zelix.klassmaster.changelog;

import com.zelix.klassmaster.classfile.AbstractMethodInfo;
import com.zelix.klassmaster.classfile.FieldNameTypeSignature;
import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClasspathClassResolver;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ClassLookupException;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.ZkmStringUtils;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

public abstract class StackTraceLine {
    public static final String[] EMPTY_STRING_ARRAY = new String[0];
    public static String indent;
    public String className;
    public MethodNameAndArgs[] methodCandidates;
    public boolean callTraceBreak;
    public String sourceFileName;
    public boolean classMapped;
    public boolean translated;
    public StackTraceFrameLine frameLine;
    public Integer lineNumber;

    public String getClassName() {
        return this.className;
    }

    public void appendSourceLocation(StringBuffer stringBuffer) {
        if (this.sourceFileName != null && this.sourceFileName.length() > 0) {
            stringBuffer.append("(" + this.sourceFileName);
            if (this.lineNumber != null) {
                stringBuffer.append(":" + this.lineNumber);
            }

            stringBuffer.append(")");
        }
    }

    public boolean isStaticInitializer() {
        return this.methodCandidates != null && this.methodCandidates.length == 1 && this.methodCandidates[0].getMethodName().equals("static");
    }

    public String getErrorMessage() {
        return this.frameLine.getErrorMessage();
    }

    static {
        Integer integer = 32;
        indent = ZkmStringUtils.repeatChar(4, integer);
    }

    public boolean hasError() {
        return this.frameLine.hasError();
    }

    public String translateTypeName(String string, LoadedChangeLog loadedChangeLog) {
        String string1 = string;
        int ba = string1.indexOf("[");
        String string2;
        if (ba != -1) {
            string2 = string1.substring(ba);
            string1 = string1.substring(0, ba);
        } else {
            string2 = "";
        }

        String string3 = loadedChangeLog.getNewClassName(string1);
        if (string3 == null) {
            string3 = string1;
        }

        return string3 + string2;
    }

    public boolean isValidFrame() {
        return this.frameLine.isValidFrame();
    }

    public String[] translateArgumentTypes(List list1, StringBuilder stringBuilder, LoadedChangeLog loadedChangeLog) {
        int ba = list1.size();
        String[] strings = new String[ba];

        for (int i = 0; i < ba; i++) {
            String string = (String) list1.get(i);
            strings[i] = this.translateTypeName(string, loadedChangeLog);
            stringBuilder.append(strings[i]);
            if (i < ba - 1) {
                stringBuilder.append(", ");
            }
        }

        return strings;
    }

    public void translateFreeText(StringBuffer stringBuffer, String string, LoadedChangeLog loadedChangeLog) {
        StringTokenizer stringTokenizer = new StringTokenizer(string, "@: ()\",", true);
        ArrayList arrayList = new ArrayList(stringTokenizer.countTokens());

        while (stringTokenizer.hasMoreTokens()) {
            String string1 = stringTokenizer.nextToken();
            arrayList.add(string1);
        }

        for (int i = 0; i < arrayList.size(); i++) {
            String string2 = (String) arrayList.get(i);
            switch (string2) {
                case "@":
                case ":":
                case " ":
                case ")":
                case ",":
                case "\"":
                case "(":
                    stringBuffer.append(string2);
                    break;
                default:
                    boolean bl = string2.indexOf("/") > -1;
                    int ba = 0;
                    String string3;
                    if (bl) {
                        string3 = string2.replace('/', '.');
                    } else {
                        string3 = string2;
                    }

                    if (string3.startsWith("[") && string3.endsWith(";")) {
                        while (string3.charAt(ba) == '[') {
                            ba++;
                        }

                        if (string3.charAt(ba) == 'L') {
                            string3 = string3.substring(ba + 1, string3.length() - 1);
                        }
                    }

                    if (string3.indexOf(46) > 0 && string3.indexOf(46) < string3.length() - 1) {
                        boolean bl1 = false;
                        int bb = string3.lastIndexOf(".");
                        if (arrayList.size() > i + 1
                                && ((String) arrayList.get(i + 1)).equals("(")
                                && arrayList.indexOf(")") > i + 1
                                && ZkmStringUtils.countChar(string3, '.') > 1
                                && bb < string3.length() - 1) {
                            String string4 = string3.substring(0, bb);
                            String string5 = loadedChangeLog.getNewClassName(string4);
                            if (string5 != null) {
                                String string6 = string3.substring(bb + 1);
                                StringBuilder stringBuilder = new StringBuilder();
                                int bc = i + 2;
                                int bf = bc;

                                for (int j = arrayList.indexOf(")"); bf < j; j = arrayList.indexOf(")")) {
                                    String string7 = (String) arrayList.get(bc);
                                    String string8 = loadedChangeLog.toNewTypeName(string7);
                                    stringBuilder.append(string8);
                                    bf = ++bc;
                                }

                                String string9 = stringBuilder.toString();
                                String[] strings = loadedChangeLog.findOriginalMethodNames(string5, string6, (String) null, string9);
                                String string11;
                                if (strings != null && strings.length > 0) {
                                    string11 = "." + strings[0];
                                } else {
                                    string11 = string3.substring(bb);
                                }

                                if (bl) {
                                    string5 = string5.replace('.', '/');
                                }

                                stringBuffer.append(string5);
                                stringBuffer.append(string11);
                                bl1 = true;
                            }
                        }

                        if (!bl1) {
                            if (arrayList.size() > i + 1 && ((String) arrayList.get(i + 1)).equals("(")) {
                                stringBuffer.append(string2);
                            } else {
                                String string10 = loadedChangeLog.getNewClassName(string3);
                                if (string10 != null) {
                                    if (bl) {
                                        string10 = string10.replace('.', '/');
                                    }

                                    stringBuffer.append(string10);
                                    if (ba > 0) {
                                        for (int j = 0; j < ba; j++) {
                                            stringBuffer.append("[]");
                                        }
                                    }
                                } else {
                                    stringBuffer.append(string2);
                                }
                            }
                        }
                    } else {
                        stringBuffer.append(string2);
                    }
            }
        }
    }

    public final boolean resolveClassAndLine(StackTraceLineInfo stackTraceLineInfo, LoadedChangeLog loadedChangeLog) throws StackTraceTranslateException {
        String string = stackTraceLineInfo.getClassName();
        String string1 = loadedChangeLog.getNewClassName(string);
        if (string1 != null) {
            this.classMapped = true;
            this.className = string1;
            String string2 = loadedChangeLog.getSourceName(string1);
            if (string2 != null) {
                this.sourceFileName = string2;
            }

            if (stackTraceLineInfo.hasLineNumber()) {
                Long long1 = 121982683753216L;
                int ba = stackTraceLineInfo.getLineNumber(0, long1);
                if (ba < 0) {
                    throw new StackTraceTranslateException("Line number \"" + ba + "\" is negative in '" + stackTraceLineInfo.getLineText() + "'.");
                }

                if (loadedChangeLog.hasLineNumberMappings(string1)) {
                    Integer integer = loadedChangeLog.lookupLineNumber(string1, ba);
                    if (integer == null) {
                        throw new StackTraceTranslateException(
                                "Line number '"
                                        + ba
                                        + "' in '"
                                        + stackTraceLineInfo.getLineText()
                                        + "' could not be matched. Change log does not correspond to obfuscated bytecode."
                        );
                    }

                    this.lineNumber = integer;
                    this.translated = true;
                } else {
                    this.lineNumber = ba;
                    this.translated = true;
                }
            } else {
                this.translated = true;
            }

            return true;
        } else {
            this.className = string;
            this.classMapped = false;
            this.translated = false;
            return false;
        }
    }

    public final String formatLine(int ba, LoadedChangeLog loadedChangeLog, ClasspathClassResolver classpathClassResolver) {
        if (this.methodCandidates != null) {
            Arrays.sort(this.methodCandidates);
        }

        StringBuffer stringBuffer = new StringBuffer(64);
        if (this.frameLine.isValidFrame() && this.classMapped) {
            stringBuffer.append(indent + "at " + this.className);
            if (this.methodCandidates != null && this.methodCandidates.length != 0) {
                if (this.methodCandidates.length == 1) {
                    stringBuffer.append("." + this.methodCandidates[0].getMethodName());
                    this.appendArguments(this.methodCandidates[0], ba, stringBuffer);
                    this.appendSourceLocation(stringBuffer);
                } else {
                    int bc = stringBuffer.length() + 1;
                    Integer integer = 32;
                    String string1 = ZkmStringUtils.repeatChar(bc, integer);
                    int bb = 0;
                    bc = 0;

                    for (MethodNameAndArgs[] methodNameAndArgs = this.methodCandidates; bc < methodNameAndArgs.length; methodNameAndArgs = this.methodCandidates) {
                        if (bb == 0) {
                            stringBuffer.append(".");
                        } else {
                            stringBuffer.append(string1);
                        }

                        stringBuffer.append(this.methodCandidates[bb].getMethodName());
                        this.appendArguments(this.methodCandidates[bb], ba, stringBuffer);
                        if (bb == 0) {
                            this.appendSourceLocation(stringBuffer);
                            stringBuffer.append(HiddenOptionFlags.LINE_SEPARATOR);
                        } else if (bb < this.methodCandidates.length - 1) {
                            stringBuffer.append(HiddenOptionFlags.LINE_SEPARATOR);
                        }

                        bc = ++bb;
                    }
                }
            }

            stringBuffer.append(HiddenOptionFlags.LINE_SEPARATOR);
        } else {
            label90:
            {
                String string = this.frameLine.getLineText();
                StringBuffer stringBuffer1;
                String string2;
                if (!this.frameLine.hasError()) {
                    if (!this.frameLine.isValidFrame() || this.classMapped) {
                        if (loadedChangeLog != null) {
                            if (string.indexOf("Exception in ") > -1
                                    && string.indexOf("java.lang.NullPointerException") > -1
                                    && string.indexOf("Cannot invoke \"") > -1) {
                                this.translateNullPointerMessage(stringBuffer, string, loadedChangeLog, classpathClassResolver);
                                break label90;
                            }

                            this.translateFreeText(stringBuffer, string, loadedChangeLog);
                            break label90;
                        }

                        stringBuffer.append(string);
                        break label90;
                    }

                    stringBuffer1 = stringBuffer;
                    string2 = indent;
                } else {
                    stringBuffer1 = stringBuffer;
                    string2 = indent;
                }

                stringBuffer1.append(string2);
                stringBuffer.append(string);
            }

            stringBuffer.append(HiddenOptionFlags.LINE_SEPARATOR);
        }

        return stringBuffer.toString();
    }

    public boolean isClassMapped() {
        return this.classMapped;
    }

    public AbstractMethodInfo[] findMethodsInHierarchy(
            ProgramClass programClass1, FieldNameTypeSignature fieldNameTypeSignature, ClasspathClassResolver classpathClassResolver
    ) throws ZkmException, IOException {
        AbstractMethodInfo[] abstractMethodInfos = programClass1.findMethodsByNameAndType(fieldNameTypeSignature);
        if (abstractMethodInfos != null && abstractMethodInfos.length > 0) {
            return abstractMethodInfos;
        }

        String string = programClass1.getSuperclassName();
        if (!string.equals("java/lang/Object")) {
            ProgramClass programClass2 = classpathClassResolver.loadProgramClass(string);
            abstractMethodInfos = this.findMethodsInHierarchy(programClass2, fieldNameTypeSignature, classpathClassResolver);
            if (abstractMethodInfos != null && abstractMethodInfos.length > 0) {
                return abstractMethodInfos;
            }
        }

        String[] strings = programClass1.getInterfaceNames();

        for (String string1 : strings) {
            ProgramClass programClass3 = classpathClassResolver.loadProgramClass(string1);
            abstractMethodInfos = this.findMethodsInHierarchy(programClass3, fieldNameTypeSignature, classpathClassResolver);
            if (abstractMethodInfos != null && abstractMethodInfos.length > 0) {
                return abstractMethodInfos;
            }
        }

        return null;
    }

    public boolean hasCallTraceBreak() {
        return this.callTraceBreak;
    }

    public void appendArguments(MethodNameAndArgs methodNameAndArgs, int ba, StringBuffer stringBuffer) {
        String[] strings = methodNameAndArgs.getArgumentTypes();
        if (ba != 1 && strings != null) {
            if (ba == 2 || ba == 3) {
                stringBuffer.append("(");

                for (int i = 0; i < strings.length; i++) {
                    if (ba == 3) {
                        stringBuffer.append(strings[i]);
                    } else if (ba == 2) {
                        String string = strings[i];
                        int bc = string.lastIndexOf(".");
                        if (bc > -1) {
                            string = string.substring(bc + 1);
                        }

                        stringBuffer.append(string);
                    }

                    if (i < strings.length - 1) {
                        stringBuffer.append(", ");
                    }
                }

                stringBuffer.append(")");
            }
        }
    }

    public AbstractMethodInfo[] findMethods(ProgramClass programClass1, String string, String string1, ClasspathClassResolver classpathClassResolver) throws ZkmException, IOException {
        FieldNameTypeSignature fieldNameTypeSignature = new FieldNameTypeSignature(string, MethodSignature.parseParameterList(string1));
        AbstractMethodInfo[] abstractMethodInfos = programClass1.findMethodsByNameAndType(fieldNameTypeSignature);
        return abstractMethodInfos != null && abstractMethodInfos.length > 0
                ? abstractMethodInfos
                : this.findMethodsInHierarchy(programClass1, fieldNameTypeSignature, classpathClassResolver);
    }

    public void translateNullPointerMessage(
            StringBuffer stringBuffer, String string, LoadedChangeLog loadedChangeLog, ClasspathClassResolver classpathClassResolver
    ) {
        String string1 = null;
        boolean bl = false;

        try {
            int ba = string.indexOf("Cannot invoke \"");
            if (ba > 0) {
                int bb = string.indexOf("\"", ba + "Cannot invoke \"".length());
                if (bb > -1) {
                    string1 = string.substring(ba + "Cannot invoke \"".length(), bb);
                    int bc = string1.indexOf(40);
                    if (bc > 1) {
                        int bd = string1.indexOf(41);
                        if (bd == string1.length() - 1) {
                            String string2 = string1.substring(0, bc);
                            int be = string2.lastIndexOf(".");
                            if (be > 0 && be < string2.length() - 1) {
                                String string3 = string2.substring(0, be);
                                String string4 = string2.substring(be + 1);
                                String string5 = string1.substring(bc + 1, bd);
                                StringBuilder stringBuilder = new StringBuilder();
                                StringTokenizer stringTokenizer = new StringTokenizer(string5, " ,");

                                while (stringTokenizer.hasMoreTokens()) {
                                    String string6 = loadedChangeLog.toNewTypeName(stringTokenizer.nextToken());
                                    stringBuilder.append(string6);
                                    if (stringTokenizer.hasMoreTokens()) {
                                        stringBuilder.append(", ");
                                    }
                                }

                                String string10 = loadedChangeLog.getNewClassName(string3);
                                if (string10 == null) {
                                    string10 = string3;
                                }

                                if (classpathClassResolver != null) {
                                    ProgramClass programClass1 = null;

                                    try {
                                        programClass1 = classpathClassResolver.loadProgramClass(ZkmUtils.dotsToSlashes(string3));
                                    } catch (ClassLookupException classLookupException) {
                                    }

                                    if (programClass1 != null) {
                                        AbstractMethodInfo[] abstractMethodInfos = this.findMethods(programClass1, string4, string5, classpathClassResolver);
                                        if (abstractMethodInfos != null && abstractMethodInfos.length == 1) {
                                            AbstractMethodInfo abstractMethodInfo = abstractMethodInfos[0];
                                            if (abstractMethodInfo.isProgramMember()) {
                                                String string7 = loadedChangeLog.getNewClassName(abstractMethodInfo.getDottedClassName());
                                                String string8 = stringBuilder.toString();
                                                String[] strings = loadedChangeLog.findOriginalMethodNames(string7, string4, (String) null, string8);
                                                stringBuffer.append(string.substring(0, ba + "Cannot invoke \"".length()));
                                                stringBuffer.append(string10);
                                                stringBuffer.append(".");
                                                stringBuffer.append(strings[0]);
                                                stringBuffer.append('(');
                                                stringBuffer.append(stringBuilder);
                                                stringBuffer.append(')');
                                                this.translateFreeText(stringBuffer, string.substring(bb), loadedChangeLog);
                                                bl = true;
                                            }
                                        }
                                    }
                                } else {
                                    String string9 = stringBuilder.toString();
                                    String[] strings1 = loadedChangeLog.findOriginalMethodNames(string10, string4, (String) null, string9);
                                    if (strings1 != null && strings1.length == 1) {
                                        stringBuffer.append(string.substring(0, ba + "Cannot invoke \"".length()));
                                        stringBuffer.append(string10);
                                        stringBuffer.append(".");
                                        stringBuffer.append(strings1[0]);
                                        stringBuffer.append('(');
                                        stringBuffer.append(stringBuilder);
                                        stringBuffer.append(')');
                                        this.translateFreeText(stringBuffer, string.substring(bb), loadedChangeLog);
                                        bl = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception exception) {
        }

        if (string1 == null || !bl) {
            this.translateFreeText(stringBuffer, string, loadedChangeLog);
        }
    }
}
