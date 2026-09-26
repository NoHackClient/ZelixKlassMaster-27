package com.zelix.klassmaster.changelog;

import com.zelix.klassmaster.archive.ZkmFileUtils;
import com.zelix.klassmaster.changelog.parser.ChangeLogNode;
import com.zelix.klassmaster.changelog.parser.ChangeLogParseException;
import com.zelix.klassmaster.changelog.parser.ChangeLogParser;
import com.zelix.klassmaster.changelog.parser.ChangeLogSimpleNode;
import com.zelix.klassmaster.changelog.parser.ChangeLogTokenMgrError;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.constpool.ResolvedMethodRef;
import com.zelix.klassmaster.classfile.hierarchy.ClasspathClassResolver;
import com.zelix.klassmaster.classfile.hierarchy.ZkmClasspath;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ClassLookupException;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmRuntimeException;
import com.zelix.klassmaster.util.BooleanFlag;
import com.zelix.klassmaster.util.ObjectPair;
import com.zelix.klassmaster.util.ThreeKeyConcurrentMap;
import com.zelix.klassmaster.util.TwoKeyMap;
import com.zelix.klassmaster.util.ZkmStringUtils;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;

public class StackTraceTranslator {
    private static String[] opaqueStrings;
    public static long ownerThreadId;
    public LoadedChangeLog changeLog;
    public ClasspathClassResolver classResolver;
    public boolean initialized;
    public String mergeMessages;
    private final int maxCallPaths = 16000;
    private final int maxCandidateSets = 2000;
    public List changeLogFileNames;
    public ZkmClasspath classpath;

    public List translateToTraces(String string, boolean bl) throws ZkmException, IOException {
        if (string == null) {
            throw new IllegalArgumentException("Stack trace is null");
        }

        if (!this.initialized) {
            this.initialize();
        }

        ParsedStackTrace parsedStackTrace = new ParsedStackTrace(string);
        List list1;
        if (this.classResolver != null && bl) {
            try {
                ArrayList arrayList = new ArrayList(parsedStackTrace.getLineCount());
                Enumeration enumeration = parsedStackTrace.enumerateReversed();

                while (enumeration.hasMoreElements()) {
                    StackTraceFrameLine stackTraceFrameLine = (StackTraceFrameLine) enumeration.nextElement();
                    arrayList.add(this.resolveFrameLine(stackTraceFrameLine, this.classResolver, this.changeLog));
                }

                List list2 = this.buildCandidateSets(arrayList, this.classResolver);
                list1 = this.createTranslations(list2);
            } catch (TraceClassNotFoundException traceClassNotFoundException) {
                throw new StackTraceTranslateException(
                        "Class '" + traceClassNotFoundException.getClassName() + "' not found in obfuscated bytecode classpath '" + this.classpath.getClasspath() + "'"
                );
            } catch (ClassLookupException classLookupException) {
                throw new StackTraceTranslateException(classLookupException.getMessage() + " : '" + this.classpath.getClasspath() + "'");
            }
        } else {
            list1 = new ArrayList(0);
            ParsedLineStackTrace parsedLineStackTrace = new ParsedLineStackTrace(parsedStackTrace, this.changeLog);
            list1.add(parsedLineStackTrace);
        }

        return list1;
    }

    public boolean canCall(MethodInfo methodInfo1, MethodInfo methodInfo2, HashMap hashMap, ClasspathClassResolver classpathClassResolver) throws ZkmException, IOException {
        TwoKeyMap twoKeyMap = this.getInvokedMethods(methodInfo1, hashMap, classpathClassResolver).getTwoKeyMap(methodInfo2.getNameAndDescriptor());
        if (twoKeyMap == null) {
            return false;
        }

        Enumeration enumeration = twoKeyMap.keys();

        while (enumeration.hasMoreElements()) {
            ProgramClass programClass1 = (ProgramClass) enumeration.nextElement();
            ProgramClass programClass2 = methodInfo2.getOwnerProgramClass();
            Iterator iterator = twoKeyMap.getInnerMap(programClass1).keySet().iterator();

            while (iterator.hasNext()) {
                switch ((Integer) iterator.next()) {
                    case 182:
                        if (programClass1 != programClass2
                                && !classpathClassResolver.isSubclassOf(programClass1.getClassName(), programClass2.getClassName())
                                && !classpathClassResolver.isSubclassOf(programClass2.getClassName(), programClass1.getClassName())) {
                            break;
                        }

                        return true;
                    case 183:
                        if (programClass1 != programClass2 && !classpathClassResolver.isSubclassOf(programClass1.getClassName(), programClass2.getClassName())) {
                            break;
                        }

                        return true;
                    case 184:
                        if (programClass1 == programClass2) {
                            return true;
                        }
                        break;
                    case 185:
                        if (classpathClassResolver.implementsInterface(programClass2.getClassName(), programClass1.getClassName())) {
                            return true;
                        }
                }
            }
        }

        return false;
    }

    public String translate(String string, boolean bl) throws ZkmException, IOException {
        return this.translate(string, bl, 2);
    }

    
    
    public LoadedChangeLog loadChangeLog(String string) throws ZkmException, IOException {
        BufferedReader bufferedReader = null;
        ChangeLogSimpleNode changeLogSimpleNode = null;
        boolean bl = false ;

        LoadedChangeLog loadedChangeLog;
        try {
            bl = true;
            loadedChangeLog = new LoadedChangeLog(string);
            String string1 = ChangeLogMapping.readChangeLogEncoding(new File(string));
            ChangeLogParser changeLogParser;
            if (string1 != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(string), string1));
                changeLogParser = ChangeLogParser.instance;
            } else {
                bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(string)));
                changeLogParser = ChangeLogParser.instance;
            }

            if (changeLogParser == null) {
                new ChangeLogParser(bufferedReader);
            } else {
                ChangeLogParser.ReInit(bufferedReader);
            }

            changeLogSimpleNode = ChangeLogParser.Input();
            LoadedChangeLog loadedChangeLog1 = loadedChangeLog;
            changeLogSimpleNode.interpret((ChangeLogNode) null, 30872, 34067, 41973, loadedChangeLog1);
            if (changeLogSimpleNode.jjtGetNumChildren() == 0) {
                loadedChangeLog = null;
                bl = false;
            } else {
                loadedChangeLog.mapMethodParameterTypes();
                bl = false;
            }
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
            throw new StackTraceTranslateException("File Error : UnsupportedEncodingException in \"" + string + "\"");
        } catch (FileNotFoundException fileNotFoundException) {
            throw new StackTraceTranslateException("File Error : Couldn't open file \"" + string + "\"");
        } catch (ChangeLogTokenMgrError changeLogTokenMgrError) {
            throw new StackTraceTranslateException("Lexical error while reading \"" + string + "\" : \"" + changeLogTokenMgrError.getMessage() + "\"");
        } catch (ChangeLogParseException changeLogParseException) {
            throw new StackTraceTranslateException("Parse error while reading \"" + string + "\" : \"" + changeLogParseException.getMessage() + "\"");
        } finally {
            if (bl) {
                if (changeLogSimpleNode != null) {
                    changeLogSimpleNode.dump();
                }

                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException iOException) {
                    }
                }
            }
        }

        if (changeLogSimpleNode != null) {
            changeLogSimpleNode.dump();
        }

        try {
            bufferedReader.close();
        } catch (IOException iOException1) {
        }

        return loadedChangeLog;
    }

    public void initialize() throws ZkmException, IOException {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        Iterator iterator = this.changeLogFileNames.iterator();

        while (iterator.hasNext()) {
            String string = (String) iterator.next();
            LoadedChangeLog loadedChangeLog = this.loadChangeLog(string);
            if (loadedChangeLog == null) {
                throw new StackTraceTranslateException("Change log '" + string + "' appears to contain no change log data.");
            }

            if (this.changeLog == null) {
                this.changeLog = loadedChangeLog;
            } else {
                try {
                    this.changeLog.mergeWith(loadedChangeLog, printWriter);
                } catch (ChangeLogMergeException changeLogMergeException) {
                    throw new StackTraceTranslateException(changeLogMergeException.getMessage());
                }
            }
        }

        this.mergeMessages = stringWriter.toString();
        if (this.classpath != null) {
            this.classResolver = new ClasspathClassResolver(this.classpath, ZkmFileUtils.caseSensitiveFileSystem);
        }

        this.initialized = true;
    }

    public List buildCandidateSets(List list1, ClasspathClassResolver classpathClassResolver) throws ZkmException, IOException {
        HashMap hashMap = ZkmUtils.createHashMap();
        ArrayList arrayList = new ArrayList();
        boolean bl = false;
        ResolvedStackTraceLine resolvedStackTraceLine = null;

        for (int i = 0; i < list1.size(); i++) {
            ResolvedStackTraceLine resolvedStackTraceLine1 = (ResolvedStackTraceLine) list1.get(i);
            List list2 = resolvedStackTraceLine1.getMethodCandidates();
            if (i == 0) {
                this.startCallPaths(arrayList, resolvedStackTraceLine1);
            } else if (!resolvedStackTraceLine1.isValidFrame()) {
                ListIterator listIterator1 = arrayList.listIterator();

                while (listIterator1.hasNext()) {
                    StackTraceCallPath stackTraceCallPath2 = (StackTraceCallPath) listIterator1.next();
                    stackTraceCallPath2.addUnresolvedFrame(resolvedStackTraceLine1);
                }
            } else if (!resolvedStackTraceLine.isValidFrame()) {
                this.extendCallPaths(resolvedStackTraceLine1, arrayList, list2, bl);
            } else {
                ArrayList arrayList1 = new ArrayList(arrayList.size());
                ListIterator listIterator = arrayList.listIterator();

                while (listIterator.hasNext()) {
                    StackTraceCallPath stackTraceCallPath = (StackTraceCallPath) listIterator.next();
                    listIterator.remove();
                    arrayList1.add(stackTraceCallPath);
                    MethodInfo methodInfo1 = stackTraceCallPath.getLastMethod();

                    for (int j = 0; j < list2.size(); j++) {
                        MethodInfo methodInfo2 = ((TraceMethodCandidate) list2.get(j)).method;
                        if (this.canCall(methodInfo1, methodInfo2, hashMap, classpathClassResolver)) {
                            StackTraceCallPath stackTraceCallPath1 = (StackTraceCallPath) stackTraceCallPath.clone();
                            stackTraceCallPath1.addMethodFrame(resolvedStackTraceLine1, methodInfo2);
                            listIterator.add(stackTraceCallPath1);
                        }
                    }
                }

                if (arrayList.size() == 0) {
                    arrayList = arrayList1;
                    this.extendCallPaths(resolvedStackTraceLine1, arrayList, list2, bl);
                }
            }

            resolvedStackTraceLine = resolvedStackTraceLine1;
            if (!bl) {
                bl = resolvedStackTraceLine1.isValidFrame();
            }

            if (arrayList.size() > 16000) {
                throw new StackTraceTranslateException(
                        "Bytecode analysis exceeded maximum complexity threshold (A). "
                                + arrayList.size()
                                + " > "
                                + 16000
                                + ". Try translating just the final lines of the stack trace."
                );
            }
        }

        ArrayList arrayList2 = new ArrayList(arrayList.size());

        for (int i = 0; i < arrayList.size(); i++) {
            arrayList2.add(new FrameCandidateSets((StackTraceCallPath) arrayList.get(i)));
        }

        if (arrayList.size() > 2000) {
            throw new StackTraceTranslateException(
                    "Bytecode analysis exceeded maximum complexity threshold (B). "
                            + arrayList.size()
                            + " > "
                            + 2000
                            + ". Try translating just the final lines of the stack trace."
            );
        }

        int bd;
        do {
            bd = arrayList2.size();

            for (int i = 0; i < arrayList2.size() - 1; i++) {
                FrameCandidateSets frameCandidateSets = (FrameCandidateSets) arrayList2.get(i);
                ListIterator listIterator2 = arrayList2.listIterator(i + 1);

                while (listIterator2.hasNext()) {
                    FrameCandidateSets frameCandidateSets1 = (FrameCandidateSets) listIterator2.next();
                    if (frameCandidateSets.isMergeableWith(frameCandidateSets1)) {
                        frameCandidateSets.mergeWith(frameCandidateSets1);
                        listIterator2.remove();
                    }
                }
            }
        } while (arrayList2.size() < bd);

        return arrayList2;
    }

    public void startCallPaths(List list1, ResolvedStackTraceLine resolvedStackTraceLine) {
        List list2 = resolvedStackTraceLine.getMethodCandidates();
        if (resolvedStackTraceLine.isValidFrame() && list2.size() > 0) {
            for (int i = 0; i < list2.size(); i++) {
                TraceMethodCandidate traceMethodCandidate = (TraceMethodCandidate) list2.get(i);
                MethodInfo methodInfo1 = traceMethodCandidate.method;
                if (methodInfo1 == null) {
                    methodInfo1 = resolvedStackTraceLine.getProgramClass().findMethodBySignature(new MethodSignature(traceMethodCandidate.methodNameAndDescriptor));
                }

                StackTraceCallPath stackTraceCallPath1 = new StackTraceCallPath();
                stackTraceCallPath1.addMethodFrame(resolvedStackTraceLine, methodInfo1);
                list1.add(stackTraceCallPath1);
            }
        } else {
            StackTraceCallPath stackTraceCallPath = new StackTraceCallPath();
            stackTraceCallPath.addUnresolvedFrame(resolvedStackTraceLine);
            list1.add(stackTraceCallPath);
        }
    }

    public ResolvedStackTraceLine resolveFrameLine(
            StackTraceFrameLine stackTraceFrameLine, ClasspathClassResolver classpathClassResolver, LoadedChangeLog loadedChangeLog
    ) throws ZkmException, IOException {
        boolean bl = false;
        if (stackTraceFrameLine.isValidFrame()) {
            bl = !loadedChangeLog.hasClassMapping(stackTraceFrameLine.getClassName());
        }

        return new ResolvedStackTraceLine(stackTraceFrameLine, classpathClassResolver, bl);
    }

    public ThreeKeyConcurrentMap getInvokedMethods(MethodInfo methodInfo1, HashMap hashMap, ClasspathClassResolver classpathClassResolver) throws ZkmException, IOException {
        ThreeKeyConcurrentMap threeKeyConcurrentMap = (ThreeKeyConcurrentMap) hashMap.get(methodInfo1);
        if (threeKeyConcurrentMap == null) {
            threeKeyConcurrentMap = new ThreeKeyConcurrentMap(27, 5, 5);
            HashMap hashMap1 = ZkmUtils.createHashMap(27);
            methodInfo1.collectMethodInvokeOpcodes(hashMap1);
            Iterator iterator = hashMap1.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                ResolvedMethodRef resolvedMethodRef = (ResolvedMethodRef) entry.getKey();
                if (!ClassFileBase.isSignaturePolymorphicOwner(resolvedMethodRef.getReferencedClassName())) {
                    Integer integer = (Integer) entry.getValue();
                    String string = resolvedMethodRef.getReferencedClassName();
                    ProgramClass programClass1 = classpathClassResolver.loadProgramClass(string);
                    threeKeyConcurrentMap.putValue(resolvedMethodRef.getSignatureString(), programClass1, integer, integer);
                }
            }

            hashMap.put(methodInfo1, threeKeyConcurrentMap);
        }

        return threeKeyConcurrentMap;
    }

    public String translate(String string, boolean bl, int ba) throws ZkmException, IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BooleanFlag booleanFlag = new BooleanFlag();
        String[] strings = this.translateAlternatives(string, bl, ba, booleanFlag);
        if (!booleanFlag.getValue()) {
            stringBuilder.append(
                    "NOTE: None of the classes in the stack trace were found in the change log or no stack trace lines found. Is the correct change log selected?  Do the stack trace lines start with 'at'?"
            );
            stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
            stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
        }

        if (strings.length == 1) {
            label37:
            {
                StringBuilder stringBuilder2;
                String string2;
                if (bl) {
                    if (this.classpath != null) {
                        break label37;
                    }

                    stringBuilder2 = stringBuilder;
                    string2 = "NOTE: Obfuscated bytecode not analyzed so there could be multiple possible methods for each trace line.";
                } else {
                    stringBuilder2 = stringBuilder;
                    string2 = "NOTE: Obfuscated bytecode not analyzed so there could be multiple possible methods for each trace line.";
                }

                stringBuilder2.append(string2);
                stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
                stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
            }

            stringBuilder.append(strings[0]);
        } else {
            stringBuilder.append("NOTE: " + strings.length + " translated stack traces are possible.");
            stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
            stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);

            for (int i = 0; i < strings.length; i++) {
                String string1 = "Possible stack trace " + (i + 1) + " of " + strings.length + "...";
                stringBuilder.append(string1 + HiddenOptionFlags.LINE_SEPARATOR);
                StringBuilder stringBuilder1 = new StringBuilder();
                int bc = string1.length();
                Integer integer = 61;
                stringBuilder.append(stringBuilder1.append(ZkmStringUtils.repeatChar(bc, integer)).append(HiddenOptionFlags.LINE_SEPARATOR).toString());
                stringBuilder.append(strings[i]);
                if (i < strings.length - 1) {
                    stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
                }
            }
        }

        if (this.mergeMessages != null && this.mergeMessages.length() > 0) {
            stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
            stringBuilder.append(this.mergeMessages);
        }

        return stringBuilder.toString();
    }

    public String[] translateAlternatives(String string, boolean bl, int ba, BooleanFlag booleanFlag) throws ZkmException, IOException {
        booleanFlag.setValue(false);
        List list1 = this.translateToTraces(string, bl);
        String[] strings = new String[list1.size()];

        for (int i = 0; i < strings.length; i++) {
            TranslatedStackTrace translatedStackTrace = (TranslatedStackTrace) list1.get(i);
            if (translatedStackTrace.hasMappedClasses()) {
                booleanFlag.setValue(true);
            }

            strings[i] = translatedStackTrace.format(ba, this.changeLog, bl ? this.classResolver : null);
        }

        Arrays.sort(strings);
        return strings;
    }

    public String translateClassName(String string) throws ZkmException, IOException {
        if (!this.initialized) {
            this.initialize();
        }

        boolean bl = string.indexOf("/") > -1;
        String string1;
        LoadedChangeLog loadedChangeLog;
        if (bl) {
            string1 = string.replace('/', '.');
            loadedChangeLog = this.changeLog;
        } else {
            string1 = string;
            loadedChangeLog = this.changeLog;
        }

        String string2 = loadedChangeLog.getNewClassName(string1);
        if (string2 == null) {
            return string;
        }

        if (bl) {
            string2 = string2.replace('.', '/');
        }

        return string2;
    }

    public static synchronized void releaseThreadOwnership() {
        long currentThreadId = ZkmUtils.getCurrentThreadId();
        if (ownerThreadId != -1L && currentThreadId == ownerThreadId) {
            ownerThreadId = -1L;
        }
    }


    static {
        d(new String[5]);
        ownerThreadId = -1L;
    }


    public String getOriginalMethodName(String string, String string1, String[] strings, String string2) throws ZkmException, IOException {
        if (!this.initialized) {
            this.initialize();
        }

        return this.changeLog.getOriginalMethodName(string, string1, strings, string2);
    }

    public static synchronized void acquireThreadOwnership() {
        long currentThreadId = ZkmUtils.getCurrentThreadId();
        if (ownerThreadId != -1L && currentThreadId != ownerThreadId) {
            throw new ZkmRuntimeException(
                    "FATAL ERROR: : Zelix KlassMaster Stack Trace Translate Tool is not thread safe. If you need to run more than once instance concurrently then each instance must run in a separate JVM."
            );
        }

        ownerThreadId = currentThreadId;
    }

    public StackTraceTranslator(List list1, ZkmClasspath zkmClasspath) {
        if (list1 != null && list1.size() != 0) {
            this.changeLogFileNames = list1;
            this.classpath = zkmClasspath;
            acquireThreadOwnership();
        } else {
            throw new IllegalArgumentException("Input change log file name must be provided");
        }
    }

    public List createTranslations(List list1) throws StackTraceTranslateException {
        ArrayList arrayList = new ArrayList(list1.size());

        for (int i = 0; i < list1.size(); i++) {
            FrameCandidateSets frameCandidateSets = (FrameCandidateSets) list1.get(i);
            arrayList.add(new BytecodeTraceTranslation(frameCandidateSets, this.changeLog));
        }

        return arrayList;
    }

    public static void d(String[] strings) {
        opaqueStrings = strings;
    }

    public void extendCallPaths(ResolvedStackTraceLine resolvedStackTraceLine, List list1, List list2, boolean bl) {
        ListIterator listIterator = list1.listIterator();

        while (listIterator.hasNext()) {
            StackTraceCallPath stackTraceCallPath = (StackTraceCallPath) listIterator.next();
            listIterator.remove();

            for (int i = 0; i < list2.size(); i++) {
                MethodInfo methodInfo1 = ((TraceMethodCandidate) list2.get(i)).method;
                StackTraceCallPath stackTraceCallPath1 = (StackTraceCallPath) stackTraceCallPath.clone();
                stackTraceCallPath1.addMethodFrame(resolvedStackTraceLine, methodInfo1, bl);
                listIterator.add(stackTraceCallPath1);
            }
        }
    }

    public static String[] getOpaqueStrings() {
        return opaqueStrings;
    }

    public String[] getOriginalMethodSignatures(String string, String string1) throws ZkmException, IOException {
        String string2 = string;
        String string4;
        char bb;
        if (!this.initialized) {
            this.initialize();
            string4 = string2;
            bb = '/';
        } else {
            string4 = string2;
            bb = '/';
        }

        string2 = string4.replace(bb, '.');
        List list1 = this.changeLog.findOriginalMethods(string2, string1);
        String[] strings;
        if (list1 != null && list1.size() != 0) {
            strings = new String[list1.size()];

            for (int i = 0; i < list1.size(); i++) {
                ObjectPair objectPair = (ObjectPair) list1.get(i);
                String string3 = (String) objectPair.getSecond();
                strings[i] = (String) objectPair.getFirst() + "(" + (string3 != null ? string3 : "") + ")";
            }
        } else {
            strings = new String[]{string1};
        }

        return strings;
    }

    public void dispose() {
        if (this.classResolver != null) {
            this.classResolver.clearCaches();
        }

        releaseThreadOwnership();
    }
}
