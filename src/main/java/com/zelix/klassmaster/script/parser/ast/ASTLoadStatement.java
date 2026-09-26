package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.archive.ArchivePathFilter;
import com.zelix.klassmaster.archive.ArchiveScanner;
import com.zelix.klassmaster.archive.ArchiveSkipMode;
import com.zelix.klassmaster.archive.ArchiveSkipModeSwitchMap;
import com.zelix.klassmaster.archive.ClassFileNameFilter;
import com.zelix.klassmaster.archive.DirectoryFileLister;
import com.zelix.klassmaster.archive.InputFileLocation;
import com.zelix.klassmaster.archive.OpenPathEntry;
import com.zelix.klassmaster.archive.SourceArchive;
import com.zelix.klassmaster.archive.TempFileManager;
import com.zelix.klassmaster.archive.ZipOutputTarget;
import com.zelix.klassmaster.archive.ZkmFileUtils;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.log.DedupPrintWriter;
import com.zelix.klassmaster.log.LogMessageReporter;
import com.zelix.klassmaster.log.MessageReporter;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptNode;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;
import com.zelix.klassmaster.ui.OperationStatusCallback;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.NoOpCallback;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.Sha256Digester;
import com.zelix.klassmaster.util.ZkmAssert;
import com.zelix.klassmaster.util.ZkmStringUtils;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class ASTLoadStatement extends SummarizingStatementNode {
    public List openPathEntries;
    public List nestedUnskipPaths;
    public List unskipPaths;
    public List nestedSkipPaths;
    public List skipPaths;
    public boolean openNestedArchives = true;

    public void verifySha256(File file1, String string, ScriptEnvironment scriptEnvironment1) {
        try {
            Sha256Digester sha256Digester = new Sha256Digester(file1.getAbsolutePath());
            String string1 = sha256Digester.getHexDigest().toLowerCase();
            if (!string.equals(string1)) {
                scriptEnvironment1.logFatalError(
                        "File '" + file1.getAbsolutePath() + "' was expected to have the SHA256 hash '" + string + "' but its actual hash is '" + string1 + "'"
                );
            }
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            scriptEnvironment1.logFatalError("'" + file1.getAbsolutePath() + "' couldn't be analyzed : '" + noSuchAlgorithmException + "' (3)");
        } catch (IOException iOException) {
            scriptEnvironment1.logFatalError("'" + file1.getAbsolutePath() + "' couldn't be analyzed : '" + iOException + "' (4)");
        }
    }

    public void printOpenedFiles(InputFileLocation[] inputFileLocations, String string, PrintWriter printWriter) {
        if (inputFileLocations != null && inputFileLocations.length > 0) {
            printWriter.println(string);

            for (InputFileLocation inputFileLocation : inputFileLocations) {
                printWriter.println("\t" + inputFileLocation.getQualifiedName() + " opened");
            }
        }
    }

    @Override
    public String getStatementName() {
        return "open";
    }

    public void expandPathPattern(String string, Map map1, ArchiveSkipMode archiveSkipMode, ScriptEnvironment scriptEnvironment1) throws ZkmException, IOException {
        String string1 = ZkmFileUtils.normalizeSeparators(string);
        File file1;
        if (ZkmFileUtils.isRelativePath(string1)) {
            file1 = new File(scriptEnvironment1.getDefaultDirectory(), string1);
        } else {
            file1 = new File(string1);
        }

        if (file1.exists()) {
            map1.put(file1.getAbsolutePath(), string1);
        } else if (string1.indexOf("*") > -1
                && (
                ZkmFileUtils.looksLikeArchiveName(string1)
                        || ZkmFileUtils.isXmlFileName(string1)
                        || ZkmFileUtils.isYamlFileName(string1)
                        || ZkmFileUtils.isPropertiesFileName(string1)
                        || string1.toLowerCase().endsWith(".class")
        )) {
            ObservableHolder observableHolder = new ObservableHolder();
            List list2 = ZkmFileUtils.expandFilePath(string1, scriptEnvironment1.getDefaultDirectory(), observableHolder);
            if (!observableHolder.isValueNull()) {
                scriptEnvironment1.logWarning(
                        (String) observableHolder.getValue()
                                + " : Specified path was "
                                + archiveSkipMode.getPrefix()
                                + "\""
                                + string
                                + "\". Default directory was '"
                                + scriptEnvironment1.getDefaultDirectory()
                                + "'"
                );
            }

            if (list2.size() > 0) {
                Iterator iterator = list2.iterator();

                while (iterator.hasNext()) {
                    String string4 = (String) iterator.next();
                    map1.put(string4, string1);
                }
            } else {
                scriptEnvironment1.logWarning(
                        "No paths matched "
                                + archiveSkipMode.getPrefix()
                                + "\""
                                + string
                                + "\". Default directory was '"
                                + scriptEnvironment1.getDefaultDirectory()
                                + "'"
                );
            }
        } else if (string1.endsWith("*")) {
            String string2 = string1.substring(0, string1.length() - 1);
            if (ZkmFileUtils.isRelativePath(string2)) {
                file1 = new File(scriptEnvironment1.getDefaultDirectory(), string2);
            } else {
                file1 = new File(string2);
            }

            if (file1.exists() && file1.isDirectory()) {
                map1.put(file1.getAbsolutePath(), string1);
                DirectoryFileLister directoryFileLister = new DirectoryFileLister(file1);
                List list1 = directoryFileLister.listSubdirectories();
                int ba = list1.size();

                for (int i = 0; i < ba; i++) {
                    String string3 = (String) list1.get(i);
                    map1.put(string3, string1);
                }
            } else if (file1.exists()) {
                scriptEnvironment1.logFatalError("\"" + file1.getAbsolutePath() + "\" is not a directory");
            } else {
                scriptEnvironment1.logFatalError(
                        "\""
                                + file1.getAbsolutePath()
                                + "\" does not exist"
                                + (string1.endsWith("**") ? ". '**' not supported." : "")
                                + " Specified path was '"
                                + string
                                + "' Default directory was '"
                                + scriptEnvironment1.getDefaultDirectory()
                                + "'. (1)"
                );
            }
        } else {
            switch (ArchiveSkipModeSwitchMap.SKIP_MODE_SWITCH_MAP[archiveSkipMode.ordinal()]) {
                case 1:
                    scriptEnvironment1.logFatalError(
                            "\""
                                    + file1.getAbsolutePath()
                                    + "\" does not exist. Specified path was '"
                                    + string
                                    + "' Default directory was '"
                                    + scriptEnvironment1.getDefaultDirectory()
                                    + "'. (2)"
                    );
                    break;
                case 2:
                case 3:
                    scriptEnvironment1.logWarning(
                            "\""
                                    + file1.getAbsolutePath()
                                    + "\" does not exist. Specified path was "
                                    + archiveSkipMode.getPrefix()
                                    + "\""
                                    + string
                                    + "\". Default directory was '"
                                    + scriptEnvironment1.getDefaultDirectory()
                                    + "'. (2)"
                    );
            }
        }
    }

    public InputFileLocation[] collectInputLocations(
            Map map1,
            ArrayList arrayList,
            ArrayList arrayList1,
            List list1,
            List list2,
            Set set1,
            Set set2,
            Set set3,
            Set set4,
            Set set5,
            Set set6,
            Set set7,
            Set set8,
            ZipOutputTarget zipOutputTarget,
            Set set9,
            ScriptEnvironment scriptEnvironment1,
            MessageReporter messageReporter1
    ) throws ZkmException, IOException {
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        Iterator iterator = this.openPathEntries.iterator();

        while (iterator.hasNext()) {
            OpenPathEntry openPathEntry = (OpenPathEntry) iterator.next();
            openPathEntry.getFilter();
            LinkedHashMap linkedHashMap1 = new LinkedHashMap();
            this.expandPathPattern(openPathEntry.getPath(), linkedHashMap1, ArchiveSkipMode.TOP_LEVEL, scriptEnvironment1);
            Iterator iterator1 = linkedHashMap1.entrySet().iterator();

            while (iterator1.hasNext()) {
                Entry entry = (Entry) iterator1.next();
                linkedHashMap.put(entry.getKey(), openPathEntry);
            }
        }

        LinkedHashMap linkedHashMap2 = new LinkedHashMap();
        Iterator iterator2 = this.skipPaths.iterator();

        while (iterator2.hasNext()) {
            String string = (String) iterator2.next();
            this.expandPathPattern(string, linkedHashMap2, ArchiveSkipMode.SKIP, scriptEnvironment1);
        }

        LinkedHashMap linkedHashMap3 = new LinkedHashMap();
        Iterator iterator3 = this.unskipPaths.iterator();

        while (iterator3.hasNext()) {
            String string1 = (String) iterator3.next();
            this.expandPathPattern(string1, linkedHashMap3, ArchiveSkipMode.UNSKIP, scriptEnvironment1);
        }

        iterator3 = linkedHashMap.entrySet().iterator();

        while (iterator3.hasNext()) {
            Entry entry1 = (Entry) iterator3.next();
            String string2 = (String) entry1.getKey();
            File file1 = new File(string2);
            OpenPathEntry openPathEntry1 = (OpenPathEntry) entry1.getValue();
            ArchivePathFilter archivePathFilter = openPathEntry1.getFilter();
            if (!ZkmFileUtils.isKnownArchiveName(string2) && !file1.isDirectory() && !archivePathFilter.acceptsPath(string2)) {
                messageReporter1.reportInfo(
                        "INFO:", "Filtering out path '" + string2 + "' because it does not match specified filter '" + archivePathFilter.toFilterExpression() + "' (A)"
                );
            } else {
                boolean bl = false;
                if (linkedHashMap2.containsKey(string2)) {
                    messageReporter1.reportInfo("INFO:", "Skipping path '" + string2 + "' because of parameter -\"" + (String) linkedHashMap2.get(string2) + "\" (A)");
                    set1.add(string2);
                    if (linkedHashMap3.containsKey(string2)) {
                        messageReporter1.reportInfo(
                                "INFO:", "Unskipping path '" + string2 + "' because of parameter +\"" + (String) linkedHashMap3.get(string2) + "\" (A)"
                        );
                        set2.add(string2);
                    } else {
                        bl = true;
                    }
                }

                if (!bl) {
                    this.processInputPath(
                            string2,
                            file1,
                            openPathEntry1,
                            linkedHashSet,
                            arrayList,
                            arrayList1,
                            list1,
                            list2,
                            map1,
                            set1,
                            set2,
                            set3,
                            set4,
                            set5,
                            set6,
                            set7,
                            set8,
                            scriptEnvironment1,
                            zipOutputTarget,
                            set9,
                            messageReporter1
                    );
                }
            }
        }

        InputFileLocation[] inputFileLocations = new InputFileLocation[linkedHashSet.size()];
        InputFileLocation[] inputFileLocations1 = ((com.zelix.klassmaster.archive.InputFileLocation[]) (linkedHashSet.toArray(inputFileLocations)));
        if (!zipOutputTarget.isNotOpened()) {
            try {
                zipOutputTarget.close();
            } catch (IOException iOException) {
                messageReporter1.reportFatalError("FILE ERROR:", iOException.toString());
            }
        }

        return inputFileLocations1;
    }

    @Override
    public void executeStatement(Object object, Object object1, Object object2, Object object3) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        int ba = (Integer) object2;
        int bb = (Integer) object1;
        int bc = (Integer) object3;
        ClassRepository classRepository1 = scriptEnvironment1.getClassRepository();
        PrintWriter printWriter = scriptEnvironment1.getLogWriter();
        LogMessageReporter logMessageReporter = new LogMessageReporter(scriptEnvironment1, ZkmScriptSimpleNode.getTimestampPrefix().length());
        NoOpCallback noOpCallback = NoOpCallback.getInstance();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream();
        ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
        DedupPrintWriter dedupPrintWriter = new DedupPrintWriter(byteArrayOutputStream);
        PrintWriter printWriter1 = new PrintWriter(byteArrayOutputStream1);
        PrintWriter printWriter2 = new PrintWriter(byteArrayOutputStream2);
        String string = ZkmScriptSimpleNode.getTimestampPrefix();
        String string1 = string + " Opening classes...";
        printWriter.println(string1);
        System.out.println(string1);
        HashMap hashMap = ZkmUtils.createHashMap(27);
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList1 = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        HashSet hashSet = ZkmUtils.createHashSet(13);
        HashSet hashSet1 = ZkmUtils.createHashSet(13);
        LinkedHashSet linkedHashSet = new LinkedHashSet(13);
        LinkedHashSet linkedHashSet1 = new LinkedHashSet(13);
        HashSet hashSet2 = ZkmUtils.createHashSet(13);
        HashSet hashSet3 = ZkmUtils.createHashSet(13);
        HashSet hashSet4 = ZkmUtils.createHashSet(13);
        HashSet hashSet5 = ZkmUtils.createHashSet(13);
        ZipOutputTarget zipOutputTarget = new ZipOutputTarget();
        HashSet hashSet6 = ZkmUtils.createHashSet(13);
        scriptEnvironment1.setLooseClassFilesLoaded(false);
        InputFileLocation[] inputFileLocations = this.collectInputLocations(
                hashMap,
                arrayList,
                arrayList1,
                arrayList2,
                arrayList3,
                hashSet,
                hashSet1,
                linkedHashSet,
                linkedHashSet1,
                hashSet2,
                hashSet3,
                hashSet4,
                hashSet5,
                zipOutputTarget,
                hashSet6,
                scriptEnvironment1,
                logMessageReporter
        );
        SourceArchive[] sourceArchives1 = new SourceArchive[hashMap.size()];
        int bd = 0;
        Iterator iterator = hashMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            sourceArchives1[bd++] = (SourceArchive) entry.getValue();
        }

        if (scriptEnvironment1.isVerbose()) {
            this.printInputLocations(inputFileLocations, printWriter);
        }

        InputFileLocation[] inputFileLocations3 = new InputFileLocation[arrayList.size()];
        arrayList.toArray(inputFileLocations3);
        InputFileLocation[] inputFileLocations4 = new InputFileLocation[arrayList1.size()];
        arrayList1.toArray(inputFileLocations4);
        InputFileLocation[] inputFileLocations1 = new InputFileLocation[arrayList2.size()];
        arrayList2.toArray(inputFileLocations1);
        InputFileLocation[] inputFileLocations2 = new InputFileLocation[arrayList3.size()];
        arrayList3.toArray(inputFileLocations2);
        this.warnUnmatchedSkipClauses(hashSet, hashSet1, logMessageReporter);
        ScriptEnvironment scriptEnvironment3;
        ASTLoadStatement aSTLoadStatement1;
        int bh;
        int bi;
        int bj;
        String string5;
        if (scriptEnvironment1.isVerbose()) {
            this.printOpenedFiles(inputFileLocations3, string + " Opening JAD files from file system...", printWriter);
            this.printOpenedFiles(inputFileLocations4, string + " Opening XML files from file system...", printWriter);
            if (inputFileLocations1.length > 0) {
                this.printOpenedFiles(inputFileLocations1, string + " Opening YAML files from file system...", printWriter);
            }

            if (inputFileLocations2.length > 0) {
                this.printOpenedFiles(inputFileLocations2, string + " Opening PROPERTIES files from file system...", printWriter);
                aSTLoadStatement1 = this;
                scriptEnvironment3 = scriptEnvironment1;
                bh = bb;
                bi = ba;
                bj = bc;
                string5 = "in parse of";
            } else {
                aSTLoadStatement1 = this;
                scriptEnvironment3 = scriptEnvironment1;
                bh = bb;
                bi = ba;
                bj = bc;
                string5 = "in parse of";
            }
        } else {
            aSTLoadStatement1 = this;
            scriptEnvironment3 = scriptEnvironment1;
            bh = bb;
            bi = ba;
            bj = bc;
            string5 = "in parse of";
        }

        aSTLoadStatement1.printMessageSummary(scriptEnvironment3, bh, bi, bj, string5);
        bb = scriptEnvironment1.getMessageCount();
        ba = scriptEnvironment1.getWarningCount();
        bc = scriptEnvironment1.getErrorCount();
        MutableInt mutableInt = new MutableInt(0);
        scriptEnvironment1.clearTrimExclusionMemberSets();
        scriptEnvironment1.setMicroEditionClassesPresent(false);
        String string6 = zipOutputTarget.getTempFilePath();
        PrintWriter printWriter4 = printWriter2;
        MutableInt mutableInt1 = mutableInt;
        PrintWriter printWriter3 = printWriter1;
        DedupPrintWriter dedupPrintWriter1 = dedupPrintWriter;
        ScriptEnvironment scriptEnvironment2 = scriptEnvironment1;
        int be = classRepository1.openClasses(
                inputFileLocations,
                sourceArchives1,
                linkedHashSet,
                linkedHashSet1,
                hashSet2,
                hashSet3,
                hashSet4,
                hashSet5,
                string6,
                inputFileLocations3,
                inputFileLocations4,
                inputFileLocations1,
                inputFileLocations2,
                this.openNestedArchives,
                logMessageReporter,
                noOpCallback,
                (OperationStatusCallback) null,
                scriptEnvironment2,
                dedupPrintWriter1,
                printWriter3,
                mutableInt1,
                printWriter4
        );
        dedupPrintWriter.close();
        printWriter1.close();
        printWriter2.close();
        this.printMessageSummary(scriptEnvironment1, bb, ba, bc, "while executing");
        String string2 = byteArrayOutputStream1.toString();
        String string3 = byteArrayOutputStream.toString();
        String string4 = byteArrayOutputStream2.toString();
        PrintStream printStream = System.out;
        StringBuilder stringBuilder = new StringBuilder();
        bh = string.length() + 1;
        Integer integer = 32;
        printStream.println(
                stringBuilder.append(ZkmStringUtils.repeatChar(bh, integer)).append("Opened ").append(be).append(be > 1 ? " classes" : " class").toString()
        );
        if (string3.length() > 0) {
            int bf = ZkmStringUtils.countOccurrences(string3, HiddenOptionFlags.LINE_SEPARATOR);
            printWriter.println("Errors detected during open...");
            printWriter.println(string3);
            PrintStream printStream1 = System.err;
            StringBuilder stringBuilder1 = new StringBuilder();
            bh = string.length() + 1;
            Integer integer1 = 32;
            printStream1.println(
                    stringBuilder1.append(ZkmStringUtils.repeatChar(bh, integer1))
                            .append(bf)
                            .append(bf > 1 ? " errors" : " error")
                            .append(" detected during open!")
                            .toString()
            );
        }

        if (mutableInt.getValue() > 0) {
            if (HiddenOptionFlags.REFLECTION_WARNINGS) {
                int value = mutableInt.getValue();
                printWriter.println("API calls detected that may not be handled automatically...");
                printWriter.println(string2);
                PrintStream printStream2 = System.out;
                StringBuilder stringBuilder2 = new StringBuilder();
                bh = string.length() + 1;
                Integer integer2 = 32;
                printStream2.println(
                        stringBuilder2.append(ZkmStringUtils.repeatChar(bh, integer2))
                                .append(value)
                                .append(value > 1 ? " API warnings" : " warning")
                                .append(" detected during open.")
                                .toString()
                );
            } else {
                printWriter.println("API call warnings disabled.");
                PrintStream printStream3 = System.out;
                StringBuilder stringBuilder3 = new StringBuilder();
                bh = string.length() + 1;
                Integer integer3 = 32;
                printStream3.println(stringBuilder3.append(ZkmStringUtils.repeatChar(bh, integer3)).append("API call warnings disabled.").toString());
            }
        }

        printWriter.println(string4);
    }

    public void printInputLocations(InputFileLocation[] inputFileLocations, PrintWriter printWriter) {
        for (InputFileLocation inputFileLocation : inputFileLocations) {
            printWriter.println(
                    "\t" + inputFileLocation.getName() + (inputFileLocation.isArchiveEntry() ? " opened from " + inputFileLocation.getArchivePath() : "")
            );
        }
    }

    public void warnUnmatchedClause(String string, String string1, MessageReporter messageReporter1) {
        if (string.toLowerCase().endsWith(".class")) {
            messageReporter1.reportWarning("WARNING:", string1 + " clause '" + string + "' not matched to any class");
        } else {
            messageReporter1.reportWarning("WARNING:", string1 + " clause '" + string + "' not matched to any file");
        }
    }

    public void warnUnmatchedSkipClauses(Set set1, Set set2, MessageReporter messageReporter1) {
        if (this.nestedSkipPaths.size() + this.skipPaths.size() > set1.size()) {
            int ba = 0;
            int be = 0;

            for (List list3 = this.skipPaths; be < list3.size(); list3 = this.skipPaths) {
                String string = (String) this.skipPaths.get(ba);
                if (!set1.contains(string)) {
                    this.warnUnmatchedClause(string, "Skip", messageReporter1);
                }

                be = ++ba;
            }

            ba = 0;
            be = 0;

            for (List list1 = this.nestedSkipPaths; be < list1.size(); list1 = this.nestedSkipPaths) {
                String string1 = (String) this.nestedSkipPaths.get(ba);
                if (!set1.contains(string1)) {
                    this.warnUnmatchedClause(string1, "Skip", messageReporter1);
                }

                be = ++ba;
            }
        }

        if (this.nestedUnskipPaths.size() + this.unskipPaths.size() > set2.size()) {
            int bb = 0;
            int bd = bb;

            for (List list2 = this.unskipPaths; bd < list2.size(); list2 = this.unskipPaths) {
                String string2 = (String) this.unskipPaths.get(bb);
                if (!set2.contains(string2)) {
                    this.warnUnmatchedClause(string2, "Unskip", messageReporter1);
                }

                bd = ++bb;
            }

            for (int i = 0; i < this.nestedUnskipPaths.size(); i++) {
                String string3 = (String) this.nestedUnskipPaths.get(i);
                if (!set2.contains(string3)) {
                    this.warnUnmatchedClause(string3, "Unskip", messageReporter1);
                }
            }
        }
    }

    public void addUniquePath(String string, Set set1, ScriptEnvironment scriptEnvironment1) {
        if (string.length() == 0) {
            scriptEnvironment1.logFatalError(
                    "Empty file path \"" + string + "\" in \"" + this.getStatementName() + "\" statement at line " + this.getStatementLine() + "."
            );
        } else if (!set1.add(string)) {
            scriptEnvironment1.logWarning(
                    "\""
                            + string
                            + "\" appears more than once in \""
                            + this.getStatementName()
                            + "\" statement at line "
                            + this.getStatementLine()
                            + ". First occurrence will be used."
            );
        }
    }

    public ASTLoadStatement() {
        super(2);
    }

    public String computeSha256(File file1, ScriptEnvironment scriptEnvironment1) {
        try {
            Sha256Digester sha256Digester = new Sha256Digester(file1.getAbsolutePath());
            return sha256Digester.getHexDigest().toLowerCase();
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            scriptEnvironment1.logFatalError("'" + file1.getAbsolutePath() + "' couldn't be analyzed : '" + noSuchAlgorithmException + "' (1)");
        } catch (IOException iOException) {
            scriptEnvironment1.logFatalError("'" + file1.getAbsolutePath() + "' couldn't be analyzed : '" + iOException + "' (2)");
        }

        return null;
    }

    @Override
    public void execute(Object object1, Object object) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        scriptEnvironment1.buildIgnoreMissingReferencesSpec();
        int ba = this.jjtGetNumChildren();
        int messageCount = scriptEnvironment1.getMessageCount();
        int warningCount = scriptEnvironment1.getWarningCount();
        int errorCount = scriptEnvironment1.getErrorCount();
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        LinkedHashSet linkedHashSet1 = new LinkedHashSet();
        LinkedHashSet linkedHashSet2 = new LinkedHashSet();
        LinkedHashSet linkedHashSet3 = new LinkedHashSet();
        LinkedHashSet linkedHashSet4 = new LinkedHashSet();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList1 = new ArrayList();
        ArrayList arrayList2 = new ArrayList();

        for (int i = 0; i < ba; i++) {
            ZkmScriptNode zkmScriptNode = this.jjtGetChild(i);
            zkmScriptNode.execute(this, scriptEnvironment1);
            if (zkmScriptNode instanceof ScriptValueNode) {
                String string = ((ScriptValueNode) zkmScriptNode).getValue().trim();
                if (zkmScriptNode instanceof ZkmScriptASTStringLiteral) {
                    this.addUniquePath(string, linkedHashSet, scriptEnvironment1);
                    arrayList.add(ArchivePathFilter.ACCEPT_ALL);
                    arrayList1.add(null);
                    arrayList2.add(null);
                } else if (zkmScriptNode instanceof ASTSkipArchivePath) {
                    if (string.indexOf("!") == -1) {
                        this.addUniquePath(string, linkedHashSet1, scriptEnvironment1);
                    } else {
                        String string1 = string.replace('\\', '/');
                        string1 = ZkmStringUtils.replaceAll(string1, "!/", "!");
                        if (!string1.equals(string)) {
                            scriptEnvironment1.logMessage(
                                    "-\""
                                            + string
                                            + "\" changed to -\""
                                            + string1
                                            + "\" in \""
                                            + this.getStatementName()
                                            + "\" statement at line "
                                            + this.getStatementLine()
                                            + "."
                            );
                        }

                        this.addUniquePath(string1, linkedHashSet3, scriptEnvironment1);
                    }
                } else if (zkmScriptNode instanceof ASTUnSkipArchivePath) {
                    if (string.indexOf("!") == -1) {
                        this.addUniquePath(string, linkedHashSet2, scriptEnvironment1);
                    } else {
                        String string2 = string.replace('\\', '/');
                        string2 = ZkmStringUtils.replaceAll(string2, "!/", "!");
                        if (!string2.equals(string)) {
                            scriptEnvironment1.logMessage(
                                    "+\""
                                            + string
                                            + "\" changed to +\""
                                            + string2
                                            + "\" in \""
                                            + this.getStatementName()
                                            + "\" statement at line "
                                            + this.getStatementLine()
                                            + "."
                            );
                        }

                        this.addUniquePath(string2, linkedHashSet4, scriptEnvironment1);
                    }
                } else {
                    scriptEnvironment1.logFatalError(
                            "'"
                                    + this.getStatementName()
                                    + "' statement had "
                                    + zkmScriptNode.getClass().getName()
                                    + " as child "
                                    + i
                                    + " at line "
                                    + this.getStatementLine()
                                    + ". (1)"
                    );
                }
            } else if (zkmScriptNode instanceof ASTFileFilter) {
                arrayList.set(arrayList.size() - 1, (ArchivePathFilter) zkmScriptNode);
            } else if (zkmScriptNode instanceof ASTLoadParameter) {
                ASTLoadParameter aSTLoadParameter = (ASTLoadParameter) zkmScriptNode;
                if (aSTLoadParameter.getParameterName().equals("expectedInitial_SHA256")) {
                    String string3 = (String) arrayList1.get(arrayList1.size() - 1);
                    if (string3 != null) {
                        scriptEnvironment1.logWarning(
                                "Extra 'expectedInitial_SHA256' value '"
                                        + aSTLoadParameter.getValue(0)
                                        + "' in '"
                                        + this.getStatementName()
                                        + "' statement. First value '"
                                        + string3
                                        + "' will be used."
                        );
                    } else {
                        arrayList1.set(arrayList1.size() - 1, aSTLoadParameter.getValue(0));
                    }
                } else {
                    String string4 = (String) arrayList2.get(arrayList2.size() - 1);
                    if (string4 != null) {
                        scriptEnvironment1.logWarning(
                                "Extra 'expectedFinal_SHA256' value '"
                                        + aSTLoadParameter.getValue(0)
                                        + "' in '"
                                        + this.getStatementName()
                                        + "' statement. First value '"
                                        + string4
                                        + "' will be used."
                        );
                    } else {
                        arrayList2.set(arrayList2.size() - 1, aSTLoadParameter.getValue(0));
                    }
                }
            } else if (zkmScriptNode instanceof ASTOpenNestedArchivesParameter) {
                ASTOpenNestedArchivesParameter aSTOpenNestedArchivesParameter = (ASTOpenNestedArchivesParameter) zkmScriptNode;
                if (aSTOpenNestedArchivesParameter.getValue(0).equals("false")) {
                    this.openNestedArchives = false;
                }
            } else {
                scriptEnvironment1.logFatalError(
                        "'"
                                + this.getStatementName()
                                + "' statement had "
                                + zkmScriptNode.getClass().getName()
                                + " as child "
                                + i
                                + " at line "
                                + this.getStatementLine()
                                + ". (2)"
                );
            }
        }

        this.openPathEntries = new ArrayList(linkedHashSet.size());
        int bf = 0;

        for (Iterator iterator = linkedHashSet.iterator(); iterator.hasNext(); bf++) {
            String string5 = (String) iterator.next();
            this.openPathEntries.add(new OpenPathEntry(string5, (ArchivePathFilter) arrayList.get(bf), (String) arrayList1.get(bf), (String) arrayList2.get(bf)));
        }

        this.skipPaths = new ArrayList(linkedHashSet1);
        this.unskipPaths = new ArrayList(linkedHashSet2);
        this.nestedSkipPaths = new ArrayList(linkedHashSet3);
        this.nestedUnskipPaths = new ArrayList(linkedHashSet4);
        Integer integer1 = errorCount;
        Integer integer = warningCount;
        this.executeStatement(scriptEnvironment1, messageCount, integer, integer1);
    }

    public void processInputPath(
            String string,
            File file1,
            OpenPathEntry openPathEntry,
            Set set1,
            List list1,
            List list2,
            List list3,
            List list4,
            Map map1,
            Set set2,
            Set set3,
            Set set4,
            Set set5,
            Set set6,
            Set set7,
            Set set8,
            Set set9,
            ScriptEnvironment scriptEnvironment1,
            ZipOutputTarget zipOutputTarget,
            Set set10,
            MessageReporter messageReporter1
    ) throws ZkmException, IOException {
        if (!file1.exists()) {
            scriptEnvironment1.logFatalError("'" + file1.getAbsolutePath() + "' does not exist. (3)");
        } else if (file1.isFile() && !file1.isDirectory()) {
            String string1 = openPathEntry.getExpectedInitialSha256();
            if (string1 != null) {
                this.verifySha256(file1, string1, scriptEnvironment1);
            }

            String string2 = openPathEntry.getExpectedFinalSha256();
            if (string.endsWith(".class")) {
                set1.add(new InputFileLocation(openPathEntry.getExpectedFinalSha256(), file1));
                scriptEnvironment1.setLooseClassFilesLoaded(true);
            } else {
                if (HiddenOptionFlags.DETERMINISTIC_OUTPUT && !HiddenOptionFlags.USE_PARALLEL && scriptEnvironment1.isVerbose()) {
                    String string3 = this.computeSha256(file1, scriptEnvironment1);
                    PrintWriter printWriter = scriptEnvironment1.getLogWriter();
                    printWriter.println("\tFile '" + file1.getAbsolutePath() + "' has SHA256 hash '" + string3 + "'");
                }

                boolean bl = false;

                try {
                    bl = ZkmFileUtils.isArchiveFile(file1);
                } catch (IOException iOException1) {
                    scriptEnvironment1.logFatalError("FILE ERROR '" + file1.getAbsolutePath() + "' : " + iOException1);
                }

                if (bl) {
                    File file3 = null;

                    try {
                        file3 = ZkmFileUtils.copyFileToTemp(file1);
                    } catch (IOException iOException) {
                        scriptEnvironment1.logFatalError("Could not create temporary file in '" + TempFileManager.tempDirPath + "' : " + iOException + " (1)");
                    }

                    ZkmAssert.assertNotNull(file3, "Null temporary file : '" + TempFileManager.tempDirPath + "'");
                    boolean openNestedArchives = this.openNestedArchives;
                    List list5 = this.nestedSkipPaths;
                    List list6 = this.nestedUnskipPaths;
                    String string4 = file1.getAbsolutePath();
                    MessageReporter messageReporter2 = messageReporter1;
                    Set set11 = set10;
                    ZipOutputTarget zipOutputTarget1 = zipOutputTarget;
                    ArchiveScanner.scanArchive(
                            file3,
                            set1,
                            map1,
                            openPathEntry,
                            openNestedArchives,
                            list5,
                            list6,
                            set2,
                            set3,
                            set4,
                            set5,
                            set6,
                            set7,
                            set8,
                            set9,
                            string4,
                            (SourceArchive) null,
                            zipOutputTarget1,
                            set11,
                            messageReporter2
                    );
                } else if (string.endsWith(".jad")) {
                    list1.add(new InputFileLocation(string2, file1));
                } else if (ZkmFileUtils.isXmlFileName(string)) {
                    list2.add(new InputFileLocation(string2, file1));
                } else if (ZkmFileUtils.isYamlFileName(string)) {
                    list3.add(new InputFileLocation(string2, file1));
                } else if (ZkmFileUtils.isPropertiesFileName(string)) {
                    list4.add(new InputFileLocation(string2, file1));
                } else {
                    scriptEnvironment1.logFatalError("File '" + file1.getAbsolutePath() + "' not a recognized type.");
                }
            }
        } else if (!file1.isFile() && file1.isDirectory()) {
            ArchivePathFilter archivePathFilter = openPathEntry.getFilter();
            String[] strings = file1.list(new ClassFileNameFilter());
            if (strings != null) {
                for (int i = 0; i < strings.length; i++) {
                    File file2 = new File(file1, strings[i]);
                    if (archivePathFilter.acceptsPath(file2.getAbsolutePath())) {
                        set1.add(new InputFileLocation(file2));
                        scriptEnvironment1.setLooseClassFilesLoaded(true);
                    } else {
                        messageReporter1.reportInfo(
                                "INFO:",
                                "Filtering out path '"
                                        + file2.getAbsolutePath()
                                        + "' because it does not match specified filter '"
                                        + archivePathFilter.toFilterExpression()
                                        + "' (B)"
                        );
                    }
                }
            }
        } else {
            scriptEnvironment1.logFatalError("'" + file1.getPath() + "' couldn't be recognized : " + file1.isDirectory() + " : " + file1.isFile());
        }
    }
}
