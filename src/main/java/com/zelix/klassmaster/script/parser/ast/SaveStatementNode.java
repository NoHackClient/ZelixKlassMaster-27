package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.archive.InputFileLocation;
import com.zelix.klassmaster.archive.JadFile;
import com.zelix.klassmaster.archive.SourceArchive;
import com.zelix.klassmaster.archive.ZkmFileUtils;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.classfile.insn.BasicBlock;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.log.LogMessageReporter;
import com.zelix.klassmaster.log.MessageReporter;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;
import com.zelix.klassmaster.util.DialogCallback;
import com.zelix.klassmaster.util.NoOpCallback;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public abstract class SaveStatementNode extends LiteralParamStatementNode {
    public boolean isDeleteXmlComments() {
        boolean bl = false;
        String string = (String) super.namedValues.get("deleteXMLComments");
        if (string != null && string.equals("true")) {
            bl = true;
        }

        return bl;
    }

    public boolean isDeleteEmptyDirectories() {
        boolean bl = false;
        String string = (String) super.namedValues.get("deleteEmptyDirectories");
        if (string != null && string.equals("true")) {
            bl = true;
        }

        return bl;
    }

    public int getArchiveCompression() {
        byte ba = 3;
        String string = (String) super.namedValues.get("archiveCompression");
        if (string != null) {
            if (string.equals("all")) {
                ba = 1;
            } else if (string.equals("none")) {
                ba = 2;
            }
        }

        return ba;
    }

    @Override
    public void executeStatement(Object object, Object object1, Object object2, Object object3) throws ZkmException, IOException {
        int bc = (Integer) object1;
        int bb = (Integer) object3;
        int ba = (Integer) object2;
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        ClassRepository classRepository1 = scriptEnvironment1.getClassRepository();
        if (!classRepository1.isOpened()) {
            scriptEnvironment1.logFatalError(
                    "Attempt to use \"" + this.getStatementName() + "\" statement at line " + this.getStatementLine() + " before opening classes"
            );
        } else if (classRepository1.hasNoClassesOpened()) {
            scriptEnvironment1.logFatalError(
                    "Attempt to use \"" + this.getStatementName() + "\" statement at line " + this.getStatementLine() + " with no classes opened"
            );
        } else if (!classRepository1.isOpenedWithoutErrors()) {
            scriptEnvironment1.logFatalError(
                    "Attempt to use \""
                            + this.getStatementName()
                            + "\" statement at line "
                            + this.getStatementLine()
                            + " with unuseable classes : "
                            + classRepository1.getOpenErrorMessage()
            );
        }

        HashSet hashSet = ZkmUtils.createHashSet();
        SourceArchive[] sourceArchives1 = classRepository1.getSourceArchives();
        HashMap hashMap = ZkmUtils.createHashMap();

        for (SourceArchive sourceArchive1 : sourceArchives1) {
            String string = sourceArchive1.getFileName();
            Object object4 = hashMap.put(string, sourceArchive1);
            if (object4 != null) {
                hashSet.add(string);
            }
        }

        HashMap hashMap4 = classRepository1.getJadFileArchives();
        HashMap hashMap5 = ZkmUtils.createHashMap();
        Iterator iterator1 = hashMap4.keySet().iterator();

        while (iterator1.hasNext()) {
            JadFile jadFile1 = (JadFile) iterator1.next();
            String string6 = ZkmFileUtils.getLastPathComponent(jadFile1.getJadFilePath());
            Object object8 = hashMap5.put(string6, jadFile1);
            if (object8 != null) {
                hashSet.add(string6);
            }
        }

        InputFileLocation[] inputFileLocations = classRepository1.getXmlFileLocations();
        HashMap hashMap6 = ZkmUtils.createHashMap();

        for (InputFileLocation inputFileLocation : inputFileLocations) {
            String string1 = ZkmFileUtils.getLastPathComponent(inputFileLocation.getQualifiedName());
            Object object5 = hashMap6.put(string1, inputFileLocation);
            if (object5 != null) {
                hashSet.add(string1);
            }
        }

        InputFileLocation[] inputFileLocations1 = classRepository1.getYamlFileLocations();
        HashMap hashMap7 = ZkmUtils.createHashMap();

        for (InputFileLocation inputFileLocation1 : inputFileLocations1) {
            String string2 = ZkmFileUtils.getLastPathComponent(inputFileLocation1.getQualifiedName());
            Object object6 = hashMap7.put(string2, inputFileLocation1);
            if (object6 != null) {
                hashSet.add(string2);
            }
        }

        InputFileLocation[] inputFileLocations2 = classRepository1.getPropertiesFileLocations();
        HashMap hashMap8 = ZkmUtils.createHashMap();

        for (InputFileLocation inputFileLocation2 : inputFileLocations2) {
            String string3 = ZkmFileUtils.getLastPathComponent(inputFileLocation2.getQualifiedName());
            Object object7 = hashMap8.put(string3, inputFileLocation2);
            if (object7 != null) {
                hashSet.add(string3);
            }
        }

        HashMap hashMap1;
        HashMap hashMap2;
        HashMap hashMap3;
        PrintWriter printWriter;
        LogMessageReporter logMessageReporter;
        NoOpCallback noOpCallback;
        File file4;
        HashMap hashMap9;
        HashMap hashMap10;
        label214:
        {
            SaveStatementNode saveStatementNode1;
            List list1;
            label251:
            {
                printWriter = scriptEnvironment1.getLogWriter();
                logMessageReporter = new LogMessageReporter(scriptEnvironment1, ZkmScriptSimpleNode.getTimestampPrefix().length());
                noOpCallback = NoOpCallback.getInstance();
                file4 = null;
                hashMap9 = ZkmUtils.createHashMap();
                hashMap10 = ZkmUtils.createHashMap();
                hashMap1 = ZkmUtils.createHashMap();
                hashMap2 = ZkmUtils.createHashMap();
                hashMap3 = ZkmUtils.createHashMap();
                if (super.literalValues.size() == 1 && ZkmFileUtils.looksLikeArchiveName((String) super.literalValues.get(0))) {
                    if (sourceArchives1.length + inputFileLocations.length > 1) {
                        saveStatementNode1 = this;
                        list1 = super.literalValues;
                        break label251;
                    }

                    if (scriptEnvironment1.isLooseClassFilesLoaded()) {
                        saveStatementNode1 = this;
                        list1 = super.literalValues;
                        break label251;
                    }
                }

                Iterator iterator = super.literalValues.iterator();

                while (true) {
                    if (!iterator.hasNext()) {
                        break label214;
                    }

                    String string4 = (String) iterator.next();
                    if (string4.trim().length() == 0) {
                        scriptEnvironment1.logFatalError(
                                "Missing base save directory or archive name in '" + this.getStatementName() + "' statement at line " + this.getStatementLine() + "."
                        );
                    }

                    File file1 = this.resolveSavePath(string4, scriptEnvironment1.getDefaultDirectory());
                    String string5 = ZkmFileUtils.getLastPathComponent(string4);
                    if (hashMap.containsKey(string5)) {
                        File file2 = ((java.io.File) (hashMap9.put(hashMap.get(string5), file1)));
                        if (file2 != null) {
                            hashMap9.put(hashMap.get(string5), file2);
                            scriptEnvironment1.logWarning(
                                    "'"
                                            + string5
                                            + "' appears more than once in '"
                                            + this.getStatementName()
                                            + "' statement at line "
                                            + this.getStatementLine()
                                            + " : '"
                                            + string4
                                            + "' ignored."
                            );
                        }

                        if (hashSet.contains(string5)) {
                            scriptEnvironment1.logFatalError(
                                    "Ambiguous save file reference in '"
                                            + this.getStatementName()
                                            + "' statement at line "
                                            + this.getStatementLine()
                                            + " : '"
                                            + string5
                                            + "' appears more than once in 'open' statement. Cannot resolve save reference. (A)"
                            );
                        }
                    } else if (hashMap5.containsKey(string5)) {
                        File file6 = ((java.io.File) (hashMap10.put(hashMap5.get(string5), file1)));
                        if (file6 != null) {
                            hashMap10.put(hashMap5.get(string5), file6);
                            scriptEnvironment1.logWarning(
                                    "'"
                                            + string5
                                            + "' appears more than once in '"
                                            + this.getStatementName()
                                            + "' statement at line "
                                            + this.getStatementLine()
                                            + " : '"
                                            + string4
                                            + "' ignored."
                            );
                        }

                        if (hashSet.contains(string5)) {
                            scriptEnvironment1.logFatalError(
                                    "Ambiguous save file reference in '"
                                            + this.getStatementName()
                                            + "' statement at line "
                                            + this.getStatementLine()
                                            + " : '"
                                            + string5
                                            + "' appears more than once in 'open' statement. Cannot resolve save reference. (B)"
                            );
                        }
                    } else if (hashMap6.containsKey(string5)) {
                        File file7 = ((java.io.File) (hashMap1.put(hashMap6.get(string5), file1)));
                        if (file7 != null) {
                            hashMap1.put(hashMap6.get(string5), file7);
                            scriptEnvironment1.logWarning(
                                    "'"
                                            + string5
                                            + "' appears more than once in '"
                                            + this.getStatementName()
                                            + "' statement at line "
                                            + this.getStatementLine()
                                            + " : '"
                                            + string4
                                            + "' ignored."
                            );
                        }

                        if (hashSet.contains(string5)) {
                            scriptEnvironment1.logFatalError(
                                    "Ambiguous save file reference in '"
                                            + this.getStatementName()
                                            + "' statement at line "
                                            + this.getStatementLine()
                                            + " : '"
                                            + string5
                                            + "' appears more than once in 'open' statement. Cannot resolve save reference. (C)"
                            );
                        }
                    } else if (hashMap7.containsKey(string5)) {
                        File file8 = ((java.io.File) (hashMap2.put(hashMap7.get(string5), file1)));
                        if (file8 != null) {
                            hashMap2.put(hashMap7.get(string5), file8);
                            scriptEnvironment1.logWarning(
                                    "'"
                                            + string5
                                            + "' appears more than once in '"
                                            + this.getStatementName()
                                            + "' statement at line "
                                            + this.getStatementLine()
                                            + " : '"
                                            + string4
                                            + "' ignored."
                            );
                        }

                        if (hashSet.contains(string5)) {
                            scriptEnvironment1.logFatalError(
                                    "Ambiguous save file reference in '"
                                            + this.getStatementName()
                                            + "' statement at line "
                                            + this.getStatementLine()
                                            + " : '"
                                            + string5
                                            + "' appears more than once in 'open' statement. Cannot resolve save reference. (D)"
                            );
                        }
                    } else if (hashMap8.containsKey(string5)) {
                        File file9 = ((java.io.File) (hashMap3.put(hashMap8.get(string5), file1)));
                        if (file9 != null) {
                            hashMap3.put(hashMap8.get(string5), file9);
                            scriptEnvironment1.logWarning(
                                    "'"
                                            + string5
                                            + "' appears more than once in '"
                                            + this.getStatementName()
                                            + "' statement at line "
                                            + this.getStatementLine()
                                            + " : '"
                                            + string4
                                            + "' ignored."
                            );
                        }

                        if (hashSet.contains(string5)) {
                            scriptEnvironment1.logFatalError(
                                    "Ambiguous save file reference in '"
                                            + this.getStatementName()
                                            + "' statement at line "
                                            + this.getStatementLine()
                                            + " : '"
                                            + string5
                                            + "' appears more than once in 'open' statement. Cannot resolve save reference. (E)"
                            );
                        }
                    } else if (file4 == null) {
                        file4 = file1;
                    } else if (ZkmFileUtils.looksLikeArchiveName(file1.getName())) {
                        scriptEnvironment1.logWarning(
                                " '"
                                        + this.getStatementName()
                                        + "' statement at line "
                                        + this.getStatementLine()
                                        + " specifies a redundant path : '"
                                        + string4
                                        + "' ignored."
                        );
                    } else {
                        scriptEnvironment1.logWarning(
                                " '"
                                        + this.getStatementName()
                                        + "' statement at line "
                                        + this.getStatementLine()
                                        + " specifies more than one base save directory or archive : '"
                                        + string4
                                        + "' ignored."
                        );
                    }
                }
            }

            file4 = saveStatementNode1.resolveSavePath((String) list1.get(0), scriptEnvironment1.getDefaultDirectory());
        }

        if (file4 == null) {
            if (hashMap.size() > hashMap9.size()) {
                scriptEnvironment1.logFatalError(
                        " '"
                                + this.getStatementName()
                                + "' statement at line "
                                + this.getStatementLine()
                                + " doesn't specify a base save directory or archive name yet not all archives given a specific save path."
                );
            }

            if (hashMap5.size() > hashMap10.size()) {
                scriptEnvironment1.logFatalError(
                        " '"
                                + this.getStatementName()
                                + "' statement at line "
                                + this.getStatementLine()
                                + " doesn't specify a base save directory or archive name yet not all JAD files given a specific save path."
                );
            }

            if (hashMap6.size() > hashMap1.size()) {
                scriptEnvironment1.logFatalError(
                        " '"
                                + this.getStatementName()
                                + "' statement at line "
                                + this.getStatementLine()
                                + " doesn't specify a base save directory or archive name yet not all opened XML files given a specific save path."
                );
            }

            if (scriptEnvironment1.isLooseClassFilesLoaded()) {
                scriptEnvironment1.logFatalError(
                        " '"
                                + this.getStatementName()
                                + "' statement at line "
                                + this.getStatementLine()
                                + " doesn't specify a base save directory or archive name yet classes have been opened from the file system."
                );
            }
        }

        StringBuilder stringBuilder = new StringBuilder("");
        Iterator iterator2 = hashMap9.entrySet().iterator();

        while (iterator2.hasNext()) {
            Entry entry;
            entry = (Entry) iterator2.next();
            label196:
            if (iterator2.hasNext()) {
                StringBuilder stringBuilder1;
                String string7;
                if (file4 == null) {
                    if (stringBuilder.length() == 0) {
                        stringBuilder.append("\"");
                        break label196;
                    }

                    stringBuilder1 = stringBuilder;
                    string7 = ", \"";
                } else {
                    stringBuilder1 = stringBuilder;
                    string7 = ", \"";
                }

                stringBuilder1.append(string7);
            } else {
                stringBuilder.append(" and \"");
            }

            File file5 = (File) entry.getValue();
            stringBuilder.append(file5.getAbsolutePath());
            stringBuilder.append("\"");
            this.ensureDirectoryExists(file5.getParentFile(), scriptEnvironment1, printWriter);
        }

        Iterator iterator3 = hashMap10.entrySet().iterator();

        while (iterator3.hasNext()) {
            Entry entry1 = (Entry) iterator3.next();
            File file10 = (File) entry1.getValue();
            this.ensureDirectoryExists(file10.getParentFile(), scriptEnvironment1, printWriter);
        }

        Iterator iterator4 = hashMap1.entrySet().iterator();

        while (iterator4.hasNext()) {
            Entry entry2 = (Entry) iterator4.next();
            File file3 = (File) entry2.getValue();
            this.ensureDirectoryExists(file3.getParentFile(), scriptEnvironment1, printWriter);
        }

        printWriter.println(
                ZkmScriptSimpleNode.getTimestampPrefix()
                        + " Saving "
                        + classRepository1.getTotalClassCount()
                        + " classes in "
                        + (file4 == null ? "" : "\"" + file4.getAbsolutePath() + "\"")
                        + stringBuilder.toString()
                        + (BasicBlock.getIntegrityState() == -1 ? ' ' : "")
        );
        System.out.println(ZkmScriptSimpleNode.getTimestampPrefix() + " Saving " + classRepository1.getTotalClassCount() + " classes...");
        if (file4 == null) {
            file4 = scriptEnvironment1.getDefaultDirectory();
        }

        if (!ZkmFileUtils.looksLikeArchiveName(file4.getName())) {
            this.ensureDirectoryExists(file4, scriptEnvironment1, printWriter);
        } else if (file4.getParentFile() != null) {
            this.ensureDirectoryExists(file4.getParentFile(), scriptEnvironment1, printWriter);
        }

        classRepository1.setArchiveSaveFiles(hashMap9);
        classRepository1.setJadSaveFiles(hashMap10);
        classRepository1.setXmlSaveFiles(hashMap1);
        classRepository1.setYamlSaveFiles(hashMap2);
        classRepository1.setPropertiesSaveFiles(hashMap3);
        this.saveClasses(classRepository1, file4, logMessageReporter, scriptEnvironment1, noOpCallback);
        this.printMessageSummary(scriptEnvironment1, bc, ba, bb, "while executing");
    }

    public String getLastModifiedTime() {
        return (String) super.namedValues.get("lastModifiedTime");
    }

    public abstract void saveClasses(
            ClassRepository classRepository1, File file1, MessageReporter messageReporter1, ScriptEnvironment scriptEnvironment1, DialogCallback dialogCallback1
    ) throws ZkmException, IOException;

    public void ensureDirectoryExists(File file1, ScriptEnvironment scriptEnvironment1, PrintWriter printWriter) {
        if (!file1.exists()) {
            if (!file1.mkdirs()) {
                scriptEnvironment1.logFatalError(
                        "Could not create directory \""
                                + file1.getAbsolutePath()
                                + "\" in \""
                                + this.getStatementName()
                                + "\" statement at line "
                                + this.getStatementLine()
                                + "."
                );
            } else if (scriptEnvironment1.isVerbose()) {
                printWriter.println("\tCreated directory " + file1.getAbsolutePath() + "\"");
            }
        }
    }

    public File resolveSavePath(String string, File file1) {
        String string1 = ZkmFileUtils.normalizeSeparators(string.trim());
        File file2;
        if (ZkmFileUtils.isRelativePath(string1)) {
            if (string1.equals(".")) {
                file2 = file1;
            } else {
                file2 = new File(file1, string1);
            }
        } else {
            file2 = new File(string1);
        }

        return file2;
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
