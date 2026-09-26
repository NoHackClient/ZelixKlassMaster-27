package com.zelix.klassmaster.proguard;

import com.zelix.klassmaster.archive.ZkmFileUtils;
import com.zelix.klassmaster.classfile.attribute.Attribute;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.log.AbstractMessageLog;
import com.zelix.klassmaster.script.ExclusionStatementNames;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.SyncIndexedSet;
import com.zelix.klassmaster.util.ZkmStringUtils;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Map.Entry;

public class ProGuardConfigTranslator extends AbstractMessageLog {
    public static final int LINE_NUMBER_TABLE_MASK = (Integer) Attribute.ATTRIBUTE_NAME_FLAGS.get("LineNumberTable");
    public static final int SOURCE_FILE_MASK = (Integer) Attribute.ATTRIBUTE_NAME_FLAGS.get("SourceFile");
    public static final int LOCAL_VARIABLE_TABLE_MASK = (Integer) Attribute.ATTRIBUTE_NAME_FLAGS.get("LocalVariableTable");
    public static final int ANNOTATION_ATTRIBUTES_MASK = (Integer) Attribute.ATTRIBUTE_NAME_FLAGS.get("RuntimeInvisibleAnnotations")
            | (Integer) Attribute.ATTRIBUTE_NAME_FLAGS.get("RuntimeInvisibleParameterAnnotations")
            | (Integer) Attribute.ATTRIBUTE_NAME_FLAGS.get("RuntimeInvisibleTypeAnnotations")
            | (Integer) Attribute.ATTRIBUTE_NAME_FLAGS.get("RuntimeInvisibleTypeAnnotations")
            | (Integer) Attribute.ATTRIBUTE_NAME_FLAGS.get("RuntimeVisibleAnnotations")
            | (Integer) Attribute.ATTRIBUTE_NAME_FLAGS.get("RuntimeVisibleParameterAnnotations")
            | (Integer) Attribute.ATTRIBUTE_NAME_FLAGS.get("RuntimeVisibleTypeAnnotations")
            | (Integer) Attribute.ATTRIBUTE_NAME_FLAGS.get("AnnotationDefault");
    public static final int SIGNATURE_MASK;
    public static final int BRIDGE_MASK;
    public static final int LOCAL_VARIABLE_TYPE_TABLE_MASK;
    public static final int GENERICS_ATTRIBUTES_MASK;
    public static final int DEPRECATED_MASK;
    public static final int EXCEPTIONS_MASK;
    public static final int INNER_CLASSES_MASK;
    public static final int ENCLOSING_METHOD_MASK;
    public static final int METHOD_PARAMETERS_MASK;
    public static final int INNER_CLASS_INFO_MASK;
    public String ignoreMissingReferencesStatement;
    public String removeMethodCallsIncludeStatement;
    public String fixedClassesStatement;
    public String printMappingFile;
    public String classObfuscationDictionary;
    public String baseDirectory;
    public boolean dontOptimize;
    public boolean overloadAggressively;
    public String unexcludeStatement;
    public String printUsageFile;
    public String accessedByReflectionExcludeStatement;
    public String trimStatement;
    public String packageObfuscationDictionary;
    public String obfuscateFlowExcludeStatement;
    public String methodParamChangesIncludeStatement;
    public String obfuscateStatement;
    public String trimExcludeStatement;
    public String printSeedsFile;
    public boolean dontObfuscate;
    public String obfuscateReferencesIncludeStatement;
    public String classpathStatement;
    public boolean dontUseMixedCaseClassNames;
    public String accessedByReflectionStatement;
    public boolean dontPreverify;
    public String applyMappingFile;
    public String targetPackageName;
    public String obfuscationDictionary;
    public String stringEncryptionUnexcludeStatement;
    public boolean dontShrink;
    public String obfuscateReferencesExcludeStatement;
    public String excludeStatement;
    public String obfuscateFlowUnexcludeStatement;
    public boolean keepParameterNames;
    public String templateScriptPath;
    public String methodParamChangesExcludeStatement;
    public String trimUnexcludeStatement;
    public String stringEncryptionExcludeStatement;
    public String removeMethodCallsExcludeStatement;
    public SyncIndexedSet classpathEntries = new SyncIndexedSet();
    public SyncIndexedSet ignoreMissingReferences = new SyncIndexedSet();
    public LinkedHashMap openArchives = new LinkedHashMap();
    public SyncIndexedSet removeMethodCallsIncludes = new SyncIndexedSet();
    public SyncIndexedSet removeMethodCallsExcludes = new SyncIndexedSet();
    public SyncIndexedSet trimExcludes = new SyncIndexedSet();
    public SyncIndexedSet trimUnexcludes = new SyncIndexedSet();
    public SyncIndexedSet trimParameters = new SyncIndexedSet();
    public SyncIndexedSet excludes = new SyncIndexedSet();
    public SyncIndexedSet unexcludes = new SyncIndexedSet();
    public SyncIndexedSet obfuscateParameters = new SyncIndexedSet();
    public SyncIndexedSet outputArchives = new SyncIndexedSet();
    public Set keepAttributePatterns = ZkmUtils.createHashSet();
    public int keptAttributesMask = 0;

    public void addRemoveMethodCallsInclude(Object object) {
        this.removeMethodCallsIncludes.add(object);
    }

    public static void appendAndSeparatedStatement(String string, int ba, SyncIndexedSet syncIndexedSet, StringBuilder stringBuilder, boolean bl) {
        int bb = ba;
        bb = Math.max(bb, string.length() + 2);
        StringBuilder stringBuilder1;
        String string2;
        byte bf;
        if (syncIndexedSet.size() <= 0) {
            if (!bl) {
                return;
            }

            stringBuilder1 = stringBuilder;
            string2 = string;
            bf = 76;
        } else {
            stringBuilder1 = stringBuilder;
            string2 = string;
            bf = 76;
        }

        stringBuilder1.append(ZkmStringUtils.pad(string2, bf, bb - 1, 32));
        int bc = 0;
        int bd = bc;

        for (int i = syncIndexedSet.size(); bd < i; i = syncIndexedSet.size()) {
            String string1 = (String) syncIndexedSet.getElementAt(bc);
            if (bc == 0) {
                stringBuilder.append(string1);
            } else {
                stringBuilder.append(ZkmStringUtils.pad(" ", 82, bb - 1, 32));
                stringBuilder.append(string1);
            }

            if (bc < syncIndexedSet.size() - 1) {
                stringBuilder.append(' ');
                stringBuilder.append("and");
                stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
            }

            bd = ++bc;
        }

        stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
        stringBuilder.append(ZkmStringUtils.pad(";", 82, bb, 32));
        stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
        stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
    }

    public static void appendQuotedListStatement(String string, String string1, int ba, SyncIndexedSet syncIndexedSet, StringBuilder stringBuilder) {
        int bb = ba;
        bb = Math.max(bb, string.length() + 2);
        if (syncIndexedSet.size() > 0) {
            stringBuilder.append(ZkmStringUtils.pad(string, 76, bb - 1, 32));
            if (string1 != null) {
                stringBuilder.append(string1);
                stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
            }

            int bc = 0;
            int bd = 0;

            for (int i = syncIndexedSet.size(); bd < i; i = syncIndexedSet.size()) {
                String string2;
                label29:
                {
                    string2 = (String) syncIndexedSet.getElementAt(bc);
                    StringBuilder stringBuilder1;
                    String string3;
                    byte bf;
                    if (bc == 0) {
                        if (string1 == null) {
                            stringBuilder.append("\"");
                            break label29;
                        }

                        stringBuilder1 = stringBuilder;
                        string3 = "\"";
                        bf = 82;
                    } else {
                        stringBuilder1 = stringBuilder;
                        string3 = "\"";
                        bf = 82;
                    }

                    stringBuilder1.append(ZkmStringUtils.pad(string3, bf, bb, 32));
                }

                stringBuilder.append(string2);
                stringBuilder.append("\"");
                stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
                bd = ++bc;
            }

            stringBuilder.append(ZkmStringUtils.pad(";", 82, bb, 32));
            stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
            stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
        }
    }

    public String findScriptStatement(String string, String string1) {
        int ba = string1.indexOf(string);

        while (ba > -1 && !this.isWholeWordAt(ba, string, string1)) {
            ba = string1.indexOf(string, ba + 1);
        }

        if (ba > -1) {
            int bb = string1.indexOf(";", ba);
            if (bb > -1) {
                return string1.substring(ba, bb + 1);
            }
        }

        return null;
    }

    public void mergeTemplateClasspath(StringBuilder stringBuilder) {
        boolean bl = this.classpathEntries.size() > 0;
        SyncIndexedSet syncIndexedSet = new SyncIndexedSet();
        stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
        stringBuilder.append(
                "//"
                        + (bl ? (HiddenOptionFlags.PREPEND_PROGUARD_CLASSPATH ? "PREPENDING" : "Appending") : "Using")
                        + " '"
                        + "classpath"
                        + "' statement found in '"
                        + this.templateScriptPath
                        + "'"
        );
        stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
        int ba = this.classpathStatement.indexOf(34, 0);

        boolean bl1;
        while (true) {
            if (ba > -1) {
                int bb = this.classpathStatement.indexOf(34, ba + 1);
                if (bb > -1) {
                    syncIndexedSet.add(this.classpathStatement.substring(ba + 1, bb));
                    ba = this.classpathStatement.indexOf(34, bb + 1);
                    continue;
                }

                this.logMessage("Error in using 'classpath' statement found in '" + this.templateScriptPath + "'. Unbalanced '\"' characters.");
                bl1 = HiddenOptionFlags.PREPEND_PROGUARD_CLASSPATH;
                break;
            }

            bl1 = HiddenOptionFlags.PREPEND_PROGUARD_CLASSPATH;
            break;
        }

        if (!bl1) {
            this.classpathEntries.addAll(syncIndexedSet);
        } else {
            syncIndexedSet.addAll(this.classpathEntries);
            this.classpathEntries = syncIndexedSet;
        }
    }

    public ProGuardConfigTranslator(PrintWriter printWriter) {
        super(true);
        super.logWriter = printWriter;
    }

    public boolean isWholeWordAt(int ba, String string, String string1) {
        int bb = ba + string.length();
        return string1.length() > bb && !Character.isWhitespace(string1.charAt(bb)) && string1.charAt(bb) != ';'
                ? false
                : ba <= 0 || Character.isWhitespace(string1.charAt(ba - 1)) || string1.charAt(ba - 1) == ';';
    }

    public void addIgnoreMissingReference(Object object) {
        this.ignoreMissingReferences.add(object);
    }

    public void setObfuscationDictionary(String string) {
        String string1 = string;
        string1 = string1.trim();
        if (this.obfuscationDictionary != null && !this.obfuscationDictionary.equals(string1)) {
            this.logWarning("More than one value for ProGuard '-classobfuscationdictionary'. '" + string1 + "' replacing '" + this.obfuscationDictionary + "'.");
        }

        if (string1 != null && string1.length() > 0) {
            this.obfuscationDictionary = string1;
        }
    }

    public void setDontUseMixedCaseClassNames() {
        this.dontUseMixedCaseClassNames = true;
    }

    public void setPrintMapping(String string) {
        String string1 = string;
        string1 = string1.trim();
        if (this.printMappingFile != null && !this.printMappingFile.equals(string1)) {
            this.logWarning("More than one value for ProGuard '-printmapping'. '" + string1 + "' replacing '" + this.printMappingFile + "'.");
        }

        this.printMappingFile = string1;
    }

    public void addKeepAttributePattern(Object object) {
        this.keepAttributePatterns.add(object);
    }

    public String getParentDirPath(File file1, String string) {
        return ZkmFileUtils.isRelativePath(string)
                ? new File(file1, string).getParentFile().getAbsolutePath()
                : new File(string).getParentFile().getAbsolutePath();
    }

    public void setBaseDirectory(String string) {
        this.baseDirectory = string;
    }

    public String getProGuardLogDir() {
        File file1;
        if (this.baseDirectory != null) {
            file1 = new File(this.baseDirectory);
        } else {
            file1 = ZkmFileUtils.USER_DIR_FILE;
        }

        if (this.printSeedsFile != null) {
            return this.getParentDirPath(file1, this.printSeedsFile);
        } else if (this.printMappingFile != null && this.printMappingFile.trim().length() > 0) {
            return this.getParentDirPath(file1, this.printMappingFile);
        } else {
            return this.printUsageFile != null ? this.getParentDirPath(file1, this.printUsageFile) : file1.getAbsolutePath();
        }
    }

    public static void appendAndSeparatedStatement(String string, int ba, SyncIndexedSet syncIndexedSet, StringBuilder stringBuilder) {
        appendAndSeparatedStatement(string, ba, syncIndexedSet, stringBuilder, false);
    }

    public void addTrimUnexclude(String string) {
        String string1 = string;
        SyncIndexedSet syncIndexedSet;
        if (string1.indexOf(".^") > -1) {
            string1 = string1.replace(".^", ".");
            syncIndexedSet = this.trimUnexcludes;
        } else {
            syncIndexedSet = this.trimUnexcludes;
        }

        syncIndexedSet.add(string1);
    }

    public void setFlattenPackageHierarchy(String string) {
        this.targetPackageName = string;
    }

    public void setClassObfuscationDictionary(String string) {
        String string1 = string;
        string1 = string1.trim();
        if (this.classObfuscationDictionary != null && !this.classObfuscationDictionary.equals(string1)) {
            this.logWarning(
                    "More than one value for ProGuard '-classobfuscationdictionary'. '" + string1 + "' replacing '" + this.classObfuscationDictionary + "'."
            );
        }

        if (string1 != null && string1.length() > 0) {
            this.classObfuscationDictionary = string1;
        }
    }


    static {
        Attribute.ATTRIBUTE_NAME_FLAGS.get("StackMap");
        Attribute.ATTRIBUTE_NAME_FLAGS.get("StackMapTable");
        SIGNATURE_MASK = (Integer) Attribute.ATTRIBUTE_NAME_FLAGS.get("Signature");
        BRIDGE_MASK = (Integer) Attribute.ATTRIBUTE_NAME_FLAGS.get("Bridge");
        LOCAL_VARIABLE_TYPE_TABLE_MASK = (Integer) Attribute.ATTRIBUTE_NAME_FLAGS.get("LocalVariableTypeTable");
        GENERICS_ATTRIBUTES_MASK = SIGNATURE_MASK | BRIDGE_MASK | LOCAL_VARIABLE_TYPE_TABLE_MASK;
        DEPRECATED_MASK = (Integer) Attribute.ATTRIBUTE_NAME_FLAGS.get("Deprecated");
        EXCEPTIONS_MASK = (Integer) Attribute.ATTRIBUTE_NAME_FLAGS.get("Exceptions");
        INNER_CLASSES_MASK = (Integer) Attribute.ATTRIBUTE_NAME_FLAGS.get("InnerClasses");
        ENCLOSING_METHOD_MASK = (Integer) Attribute.ATTRIBUTE_NAME_FLAGS.get("EnclosingMethod");
        METHOD_PARAMETERS_MASK = (Integer) Attribute.ATTRIBUTE_NAME_FLAGS.get("MethodParameters");
        INNER_CLASS_INFO_MASK = INNER_CLASSES_MASK | ENCLOSING_METHOD_MASK;
    }


    public void addTrimExclude(String string) {
        String string1 = string;
        SyncIndexedSet syncIndexedSet;
        if (string1.indexOf(".^") > -1) {
            string1 = string1.replace(".^", ".");
            syncIndexedSet = this.trimExcludes;
        } else {
            syncIndexedSet = this.trimExcludes;
        }

        syncIndexedSet.add(string1);
    }

    public void appendTemplateStatement(String string, String string1, StringBuilder stringBuilder) {
        stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
        stringBuilder.append("//Using '" + string + "' statement found in '" + this.templateScriptPath + "'");
        stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
        stringBuilder.append(string1);
        stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
    }

    public void setPrintSeeds(String string) {
        String string1 = string;
        string1 = string1.trim();
        if (this.printSeedsFile != null && !this.printSeedsFile.equals(string1)) {
            this.logWarning("More than one value for ProGuard '-printseeds'. '" + string1 + "' replacing '" + this.printSeedsFile + "'.");
        }

        if (string1 != null && string1.length() > 0) {
            this.printSeedsFile = string1;
        }

        this.setVerbose();
    }

    public void setDontShrink() {
        this.dontShrink = true;
    }

    public void setRepackageClasses(String string) {
        this.targetPackageName = string;
    }

    public String getSaveDirectory() {
        File file1;
        if (this.baseDirectory != null) {
            file1 = new File(this.baseDirectory);
        } else {
            file1 = ZkmFileUtils.USER_DIR_FILE;
        }

        String string;
        if (this.outputArchives != null) {
            if (this.outputArchives.size() > 0) {
                return this.getParentDirPath(file1, (String) this.outputArchives.getElementAt(0));
            }

            string = file1.getAbsolutePath();
        } else {
            string = file1.getAbsolutePath();
        }

        return string;
    }

    public void setVerbose() {
        this.verbose = true;
    }

    public void setDontPreverify() {
        this.dontPreverify = true;
    }

    public void setDontOptimize() {
        this.dontOptimize = true;
    }

    public void addClasspathEntries(Set set1) {
        this.classpathEntries.addAll(set1);
    }

    public void loadTemplateStatements(String string, String string1) {
        this.templateScriptPath = string1;
        StringBuilder stringBuilder = new StringBuilder();
        int ba = 0;
        int bb = 0;

        for (String[] strings = ExclusionStatementNames.EXCLUSION_STATEMENT_NAMES;
             bb < strings.length;
             strings = ExclusionStatementNames.EXCLUSION_STATEMENT_NAMES
        ) {
            stringBuilder.append("'");
            stringBuilder.append(ExclusionStatementNames.EXCLUSION_STATEMENT_NAMES[ba]);
            stringBuilder.append("'");
            if (ba < ExclusionStatementNames.EXCLUSION_STATEMENT_NAMES.length - 2) {
                stringBuilder.append(", ");
            } else if (ba < ExclusionStatementNames.EXCLUSION_STATEMENT_NAMES.length - 1) {
                stringBuilder.append(" or ");
            }

            bb = ++ba;
        }

        this.logMessage("Will examine contents of file '" + string1 + "' for possible " + stringBuilder.toString() + " statements.");

        for (String string2 : ExclusionStatementNames.EXCLUSION_STATEMENT_NAMES) {
            String string3 = this.findScriptStatement(string2, string);
            if (string3 != null) {
                this.logMessage("Using '" + string2 + "' statement found in '" + string1 + "'");
                if (string2.equals("classpath")) {
                    this.classpathStatement = string3;
                } else if (string2.equals("ignoreMissingReferences")) {
                    this.ignoreMissingReferencesStatement = string3;
                } else if (string2.equals("removeMethodCallsInclude")) {
                    this.removeMethodCallsIncludeStatement = string3;
                } else if (string2.equals("removeMethodCallsExclude")) {
                    this.removeMethodCallsExcludeStatement = string3;
                } else if (string2.equals("trimExclude")) {
                    this.trimExcludeStatement = string3;
                } else if (string2.equals("trimUnexclude")) {
                    this.trimUnexcludeStatement = string3;
                } else if (string2.equals("trim")) {
                    this.trimStatement = string3;
                } else if (string2.equals("obfuscateFlowExclude")) {
                    this.obfuscateFlowExcludeStatement = string3;
                } else if (string2.equals("obfuscateFlowUnexclude")) {
                    this.obfuscateFlowUnexcludeStatement = string3;
                } else if (string2.equals("stringEncryptionExclude")) {
                    this.stringEncryptionExcludeStatement = string3;
                } else if (string2.equals("stringEncryptionUnexclude")) {
                    this.stringEncryptionUnexcludeStatement = string3;
                } else if (string2.equals("exclude")) {
                    this.excludeStatement = string3;
                } else if (string2.equals("unexclude")) {
                    this.unexcludeStatement = string3;
                } else if (string2.equals("accessedByReflection")) {
                    this.accessedByReflectionStatement = string3;
                } else if (string2.equals("accessedByReflectionExclude")) {
                    this.accessedByReflectionExcludeStatement = string3;
                } else if (string2.equals("obfuscateReferencesInclude")) {
                    this.obfuscateReferencesIncludeStatement = string3;
                } else if (string2.equals("obfuscateReferencesExclude")) {
                    this.obfuscateReferencesExcludeStatement = string3;
                } else if (string2.equals("fixedClasses")) {
                    this.fixedClassesStatement = string3;
                } else if (string2.equals("methodParameterChangesInclude")) {
                    this.methodParamChangesIncludeStatement = string3;
                } else if (string2.equals("methodParameterChangesExclude")) {
                    this.methodParamChangesExcludeStatement = string3;
                } else if (string2.equals("obfuscate")) {
                    this.obfuscateStatement = string3;
                }
            }
        }
    }

    public void addUnexclude(Object object) {
        this.unexcludes.add(object);
    }

    public static boolean createPreliminaryLogFile(final String[] array, final ObservableHolder observableHolder, final ObservableHolder observableHolder2) throws ZkmException, IOException {
        final File parent = new File(ZkmFileUtils.resolveDotRelativePath(ZkmFileUtils.normalizeSeparators((HiddenOptionFlags.DEFAULT_DIR != null) ? HiddenOptionFlags.DEFAULT_DIR : findDefaultDirectory(array)), null));
        if (!parent.exists()) {
            if (!parent.mkdirs()) {
                observableHolder2.setValue("ERROR: Could not create default directory '" + parent.getAbsolutePath() + "'.");
                return false;
            }
        } else if (!parent.isDirectory()) {
            observableHolder2.setValue("ERROR: Specified default directory '" + parent.getAbsolutePath() + "' is not a directory.");
            return false;
        }
        final File file = new File(parent, "ZKM_PG_log.txt");
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(new FileWriter(file));
            printWriter.close();
        } catch (final IOException ex) {
            observableHolder2.setValue("ERROR: Couldn't create preliminary log file '" + parent.getAbsolutePath() + "' : '" + ex.getMessage() + "'");
            return false;
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
        }
        observableHolder.setValue(file);
        return true;
    }

    public static String findDefaultDirectory(String[] strings) {
        for (int i = 0; i < strings.length; i++) {
            String string = strings[i];
            if (string.equals("-basedirectory") && i < strings.length - 1 && strings[i + 1].length() > 0 && strings[i + 1].charAt(0) != '-') {
                return strings[i + 1];
            }
        }

        for (int i = 0; i < strings.length; i++) {
            String string3 = strings[i];
            if ((string3.equals("-printseeds") || string3.equals("-printmapping") || string3.equals("-printusage"))
                    && i < strings.length - 1
                    && strings[i + 1].length() > 0
                    && strings[i + 1].charAt(0) != '-') {
                String string1 = strings[i + 1];
                String string2 = new File(string1).getParent();
                if (string2 != null) {
                    return string2;
                }
            }
        }

        return ZkmFileUtils.USER_DIR;
    }

    public void addOutputArchives(Set set1) {
        this.outputArchives.addAll(set1);
    }

    public String buildZkmScript(String string, boolean bl, boolean bl1, List list1) {
        this.processKeepAttributes();
        StringBuilder stringBuilder = new StringBuilder(1000);
        stringBuilder.append(AbstractMessageLog.buildGeneratedByHeader(string));
        stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
        StringBuilder stringBuilder1 = new StringBuilder();
        if (this.classpathStatement != null) {
            this.mergeTemplateClasspath(stringBuilder1);
        }

        List list2 = this.getLoggedMessages();
        if (list2.size() > 0) {
            StringBuilder stringBuilder2 = new StringBuilder();
            if (this.getMessageCount() > 0) {
                stringBuilder2.append(this.getMessageCount() + " message" + (this.getMessageCount() > 1 ? "s" : ""));
            }

            if (this.getWarningCount() > 0) {
                if (stringBuilder2.length() > 0) {
                    stringBuilder2.append(" and ");
                }

                stringBuilder2.append(this.getWarningCount() + " warning" + (this.getWarningCount() > 1 ? "s" : ""));
            }

            if (this.getErrorCount() > 0) {
                if (stringBuilder2.length() > 0) {
                    stringBuilder2.append(" and ");
                }

                stringBuilder2.append(this.getErrorCount() + " error" + (this.getErrorCount() > 1 ? "s" : ""));
            }

            list1.add(stringBuilder2.toString() + " reported during " + "ProGuard" + " command translation.");
            list1.add("");
            list1.addAll(list2);
            if (bl1) {
                String[] strings = new String[list1.size()];
                list1.toArray(strings);
                stringBuilder.append(ZkmUtils.createCommentBox(false, strings));
                stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
            }
        }

        if (bl && (this.verbose || this.baseDirectory != null || this.printUsageFile != null || this.printSeedsFile != null)) {
            StringBuilder stringBuilder4 = new StringBuilder(100);
            StringBuilder stringBuilder5 = new StringBuilder(100);
            stringBuilder5.append("java -jar ZKM.jar ");
            stringBuilder4.append("Some ProGuard options must appear as Zelix KlassMaster command line options as shown below.");
            if (this.verbose) {
                stringBuilder5.append("-v");
                stringBuilder5.append(' ');
            }

            if (this.baseDirectory != null) {
                stringBuilder5.append("-dd " + ProGuardWildcardConverter.quoteIfNeeded(this.baseDirectory) + " ");
            }

            if (this.printUsageFile != null) {
                stringBuilder5.append(
                        "-tl " + (this.printUsageFile.length() > 0 ? ProGuardWildcardConverter.quoteIfNeeded(this.printUsageFile) : "ZKM_TrimLog.txt") + " "
                );
            }

            StringBuilder stringBuilder6;
            String string6;
            if (this.printSeedsFile != null) {
                stringBuilder5.append(
                        "-l " + (this.printSeedsFile.length() > 0 ? ProGuardWildcardConverter.quoteIfNeeded(this.printSeedsFile) : "ZKM_log.txt") + " "
                );
                stringBuilder6 = stringBuilder5;
                string6 = "MyZKMScript.txt";
            } else {
                stringBuilder6 = stringBuilder5;
                string6 = "MyZKMScript.txt";
            }

            stringBuilder6.append(string6);
            String string1 = stringBuilder5.toString();
            String string2 = stringBuilder4.toString();
            stringBuilder.append(ZkmUtils.createCommentBox(new String[]{string2, string1}));
            stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
        }

        int statementNameWidth = this.getStatementNameWidth();
        String string5;
        if (stringBuilder1.length() > 0) {
            stringBuilder.append(stringBuilder1);
            long bb = 80260077325258L;
            string5 = "classpath";
        } else {
            long bc = 80260077325258L;
            string5 = "classpath";
        }

        StringBuilder stringBuilder3 = stringBuilder;
        SyncIndexedSet syncIndexedSet = this.classpathEntries;
        Integer integer = statementNameWidth;
        String string3 = string5;
        appendQuotedListStatement(string3, integer, syncIndexedSet, stringBuilder3);
        if (this.ignoreMissingReferencesStatement != null) {
            this.mergeTemplateStatement("ignoreMissingReferences", this.ignoreMissingReferencesStatement, this.ignoreMissingReferences, stringBuilder);
        }

        if (!this.ignoreMissingReferences.isEmpty() || this.ignoreMissingReferencesStatement != null) {
            appendAndSeparatedStatement("ignoreMissingReferences", statementNameWidth, this.ignoreMissingReferences, stringBuilder, true);
        }

        appendMapStatement(statementNameWidth, this.openArchives, stringBuilder);
        boolean bl2;
        if (this.removeMethodCallsIncludeStatement == null
                && this.removeMethodCallsExcludeStatement == null
                && this.removeMethodCallsIncludes.isEmpty()
                && this.removeMethodCallsExcludes.isEmpty()) {
            bl2 = HiddenOptionFlags.TRANSLATE_PROGUARD_UNEXCLUDES;
        } else {
            if (this.removeMethodCallsIncludeStatement != null) {
                this.mergeTemplateStatement("removeMethodCallsInclude", this.removeMethodCallsIncludeStatement, this.removeMethodCallsIncludes, stringBuilder);
            }

            if (!this.removeMethodCallsIncludes.isEmpty()) {
                appendAndSeparatedStatement("removeMethodCallsInclude", statementNameWidth, this.removeMethodCallsIncludes, stringBuilder);
            }

            if (this.removeMethodCallsExcludeStatement != null) {
                this.appendTemplateStatement("removeMethodCallsExclude", this.removeMethodCallsExcludeStatement, stringBuilder);
            }

            if (!this.removeMethodCallsExcludes.isEmpty()) {
                this.mergeTemplateStatement("removeMethodCallsExclude", this.removeMethodCallsExcludeStatement, this.removeMethodCallsExcludes, stringBuilder);
            }

            if (!this.dontOptimize) {
                appendLineSeparatedStatement("removeMethodCalls", statementNameWidth, new SyncIndexedSet(), stringBuilder);
                bl2 = HiddenOptionFlags.TRANSLATE_PROGUARD_UNEXCLUDES;
            } else {
                bl2 = HiddenOptionFlags.TRANSLATE_PROGUARD_UNEXCLUDES;
            }
        }

        label348:
        {
            if (bl2) {
                if (this.trimExcludeStatement != null) {
                    this.appendTemplateStatement("trimExclude", this.trimExcludeStatement, stringBuilder);
                    bl2 = HiddenOptionFlags.TRANSLATE_PROGUARD_UNEXCLUDES;
                    break label348;
                }

                bl2 = HiddenOptionFlags.TRANSLATE_PROGUARD_UNEXCLUDES;
            } else {
                bl2 = HiddenOptionFlags.TRANSLATE_PROGUARD_UNEXCLUDES;
            }

            if (!bl2 && this.trimExcludeStatement != null) {
                this.mergeTemplateStatement("trimExclude", this.trimExcludeStatement, this.trimExcludes, stringBuilder);
            }

            if (this.trimExcludes.size() > 0 && this.dontShrink) {
                this.trimExcludes.add("*.* + /*Exclude everything*/");
            }

            if (this.trimExcludes.size() > 0) {
                appendAndSeparatedStatement("trimExclude", statementNameWidth, this.trimExcludes, stringBuilder);
                bl2 = HiddenOptionFlags.TRANSLATE_PROGUARD_UNEXCLUDES;
            } else {
                bl2 = HiddenOptionFlags.TRANSLATE_PROGUARD_UNEXCLUDES;
            }
        }

        if (bl2 && this.trimUnexcludeStatement != null) {
            this.appendTemplateStatement("trimUnexclude", this.trimUnexcludeStatement, stringBuilder);
        } else if (!this.dontShrink) {
            if (!HiddenOptionFlags.TRANSLATE_PROGUARD_UNEXCLUDES && this.trimUnexcludeStatement != null) {
                this.mergeTemplateStatement("trimUnexclude", this.trimUnexcludeStatement, this.trimUnexcludes, stringBuilder);
            }

            if (this.trimUnexcludes.size() > 0) {
                appendAndSeparatedStatement("trimUnexclude", statementNameWidth, this.trimUnexcludes, stringBuilder);
            }
        } else if (this.trimUnexcludeStatement != null) {
            stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
            stringBuilder.append("//Ignoring 'trimUnexclude' statement found in '" + this.templateScriptPath + "' because '" + "-dontshrink" + "' specified.");
            stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
        }

        if (this.trimStatement != null) {
            this.appendTemplateStatement("trim", this.trimStatement, stringBuilder);
        } else if (this.trimExcludes.size() > 0 || !this.dontShrink) {
            appendLineSeparatedStatement("trim", statementNameWidth, this.trimParameters, stringBuilder);
        }

        if (this.stringEncryptionExcludeStatement != null) {
            this.appendTemplateStatement("stringEncryptionExclude", this.stringEncryptionExcludeStatement, stringBuilder);
        }

        if (this.stringEncryptionUnexcludeStatement != null) {
            this.appendTemplateStatement("stringEncryptionUnexclude", this.stringEncryptionUnexcludeStatement, stringBuilder);
        }

        if (this.obfuscateFlowExcludeStatement != null) {
            this.appendTemplateStatement("obfuscateFlowExclude", this.obfuscateFlowExcludeStatement, stringBuilder);
        }

        if (this.obfuscateFlowUnexcludeStatement != null) {
            this.appendTemplateStatement("obfuscateFlowUnexclude", this.obfuscateFlowUnexcludeStatement, stringBuilder);
        }

        if (HiddenOptionFlags.TRANSLATE_PROGUARD_UNEXCLUDES && this.excludeStatement != null) {
            this.appendTemplateStatement("exclude", this.excludeStatement, stringBuilder);
            bl2 = HiddenOptionFlags.TRANSLATE_PROGUARD_UNEXCLUDES;
        } else {
            if (this.dontObfuscate) {
                this.excludes.add("*.^* + /*Exclude everything*/");
                bl2 = HiddenOptionFlags.TRANSLATE_PROGUARD_UNEXCLUDES;
            } else {
                bl2 = HiddenOptionFlags.TRANSLATE_PROGUARD_UNEXCLUDES;
            }

            if (!bl2 && this.excludeStatement != null) {
                this.mergeTemplateStatement("exclude", this.excludeStatement, this.excludes, stringBuilder);
            }

            if (this.excludes.size() > 0) {
                appendAndSeparatedStatement("exclude", statementNameWidth, this.excludes, stringBuilder);
                bl2 = HiddenOptionFlags.TRANSLATE_PROGUARD_UNEXCLUDES;
            } else {
                bl2 = HiddenOptionFlags.TRANSLATE_PROGUARD_UNEXCLUDES;
            }
        }

        if (bl2 && this.unexcludeStatement != null) {
            this.appendTemplateStatement("unexclude", this.unexcludeStatement, stringBuilder);
        } else if (!this.dontObfuscate) {
            if (!HiddenOptionFlags.TRANSLATE_PROGUARD_UNEXCLUDES && this.unexcludeStatement != null) {
                this.mergeTemplateStatement("unexclude", this.unexcludeStatement, this.unexcludes, stringBuilder);
            }

            if (this.unexcludes.size() > 0) {
                appendAndSeparatedStatement("unexclude", statementNameWidth, this.unexcludes, stringBuilder);
            }
        }

        if (this.accessedByReflectionStatement != null) {
            this.appendTemplateStatement("accessedByReflection", this.accessedByReflectionStatement, stringBuilder);
        }

        if (this.accessedByReflectionExcludeStatement != null) {
            this.appendTemplateStatement("accessedByReflectionExclude", this.accessedByReflectionExcludeStatement, stringBuilder);
        }

        if (this.obfuscateReferencesIncludeStatement != null) {
            this.appendTemplateStatement("obfuscateReferencesInclude", this.obfuscateReferencesIncludeStatement, stringBuilder);
        }

        if (this.obfuscateReferencesExcludeStatement != null) {
            this.appendTemplateStatement("obfuscateReferencesExclude", this.obfuscateReferencesExcludeStatement, stringBuilder);
        }

        if (this.fixedClassesStatement != null) {
            this.appendTemplateStatement("fixedClasses", this.fixedClassesStatement, stringBuilder);
        }

        if (this.methodParamChangesIncludeStatement != null) {
            this.appendTemplateStatement("methodParameterChangesInclude", this.methodParamChangesIncludeStatement, stringBuilder);
        }

        if (this.methodParamChangesExcludeStatement != null) {
            this.appendTemplateStatement("methodParameterChangesExclude", this.methodParamChangesExcludeStatement, stringBuilder);
        }

        String string4;
        if (this.obfuscateStatement != null) {
            this.appendTemplateStatement("obfuscate", this.obfuscateStatement, stringBuilder);
            string4 = "saveAll";
        } else {
            if (this.applyMappingFile != null) {
                this.obfuscateParameters.add("changeLogFileIn=\"" + this.applyMappingFile + "\"");
            }

            if (this.printMappingFile != null) {
                this.obfuscateParameters.add("changeLogFileOut=\"" + this.printMappingFile + "\"");
            }

            this.obfuscateParameters.add("obfuscateFlow=none //can change from none to light, normal, aggressive or extraAggressive");
            this.obfuscateParameters.add("obfuscateParameters=none //can change from none normal");
            this.obfuscateParameters.add("encryptStringLiterals=none //can change from none to normal, aggressive, flowObfuscate or enhanced");
            this.obfuscateParameters.add("exceptionObfuscation=none //can change from none to light or heavy");
            this.obfuscateParameters.add("autoReflectionHandling=none //can change from none to normal");
            this.obfuscateParameters.add("obfuscateReferences=none //can change from none to normal");
            this.obfuscateParameters.add("randomize=false //can change from false to true");
            if (this.targetPackageName != null) {
                this.obfuscateParameters.add("collapsePackagesWithDefault=\"" + this.targetPackageName + "\"");
            }

            if (this.overloadAggressively) {
                this.obfuscateParameters.add("aggressiveMethodRenaming=true");
            }

            if (this.dontPreverify) {
                this.obfuscateParameters.add("preverify=false");
            }

            if (this.dontUseMixedCaseClassNames) {
                this.obfuscateParameters.add("mixedCaseClassNames=false");
            } else {
                this.obfuscateParameters.add("mixedCaseClassNames=ifInArchive");
            }

            this.obfuscateParameters.add("keepBalancedLocks=true");
            if (this.packageObfuscationDictionary != null && this.packageObfuscationDictionary.length() > 0) {
                this.obfuscateParameters.add("newPackageNameFile=\"" + this.packageObfuscationDictionary + '"');
            }

            if (this.classObfuscationDictionary != null && this.classObfuscationDictionary.length() > 0) {
                this.obfuscateParameters.add("newClassNameFile=\"" + this.classObfuscationDictionary + '"');
            }

            if (this.obfuscationDictionary != null && this.obfuscationDictionary.length() > 0) {
                this.obfuscateParameters.add("newFieldNameFile=\"" + this.obfuscationDictionary + '"');
                this.obfuscateParameters.add("newMethodNameFile=\"" + this.obfuscationDictionary + '"');
            }

            appendLineSeparatedStatement("obfuscate", statementNameWidth, this.obfuscateParameters, stringBuilder);
            string4 = "saveAll";
        }

        appendQuotedListStatement(string4, "archiveCompression=all", statementNameWidth, this.outputArchives, stringBuilder);
        return stringBuilder.toString();
    }

    public String getPrintUsageFile() {
        return this.printUsageFile;
    }

    public void setPrintUsage(String string) {
        String string1 = string;
        string1 = string1.trim();
        if (this.printUsageFile != null && !this.printUsageFile.equals(string1)) {
            this.logWarning("More than one value for ProGuard '-printusage'. '" + string1 + "' replacing '" + this.printUsageFile + "'.");
        }

        if (string1 != null && string1.length() > 0) {
            this.printUsageFile = string1;
        }
    }

    public String findOutputDirectory(Set set1) {
        if (this.baseDirectory != null) {
            set1.add(this.getBaseDirectory());
            return this.getBaseDirectory();
        }

        if (this.printSeedsFile != null) {
            String string2 = new File(this.printSeedsFile).getParent();
            if (string2 != null && string2.length() > 0) {
                set1.add(string2);
            }

            return string2;
        } else if (this.printMappingFile != null && this.printMappingFile.trim().length() > 0) {
            String string1 = new File(this.printMappingFile).getParent();
            if (string1 != null && string1.length() > 0) {
                set1.add(string1);
            }

            return string1;
        } else if (this.printUsageFile != null) {
            String string = new File(this.printUsageFile).getParent();
            if (string != null && string.length() > 0) {
                set1.add(string);
            }

            return string;
        } else {
            return null;
        }
    }

    public void setApplyMapping(String string) {
        String string1 = string;
        string1 = string1.trim();
        if (this.applyMappingFile != null && !this.applyMappingFile.equals(string1)) {
            this.logWarning("More than one value for ProGuard '-applymapping'. '" + string1 + "' replacing '" + this.applyMappingFile + "'.");
        }

        this.applyMappingFile = string1;
    }

    public void setPackageObfuscationDictionary(String string) {
        String string1 = string;
        string1 = string1.trim();
        if (this.packageObfuscationDictionary != null && !this.packageObfuscationDictionary.equals(string1)) {
            this.logWarning(
                    "More than one value for ProGuard '-packageobfuscationdictionary'. '" + string1 + "' replacing '" + this.packageObfuscationDictionary + "'."
            );
        }

        if (string1 != null && string1.length() > 0) {
            this.packageObfuscationDictionary = string1;
        }
    }

    public void processKeepAttributes() {
        Iterator iterator = this.keepAttributePatterns.iterator();

        while (iterator.hasNext()) {
            String string = (String) iterator.next();
            Iterator iterator1 = Attribute.ATTRIBUTE_NAME_FLAGS.entrySet().iterator();

            while (iterator1.hasNext()) {
                Entry entry = (Entry) iterator1.next();
                if (((String) entry.getKey()).matches(string)) {
                    this.keptAttributesMask = this.keptAttributesMask | (Integer) entry.getValue();
                }
            }
        }

        if ((this.keptAttributesMask & DEPRECATED_MASK) == DEPRECATED_MASK) {
            this.trimParameters.add("deleteDeprecatedAttributes=false");
        } else {
            this.trimParameters.add("deleteDeprecatedAttributes=true");
        }

        if ((this.keptAttributesMask & SOURCE_FILE_MASK) == 0) {
            this.trimParameters.add("deleteSourceFileAttributes=true");
        } else {
            this.trimParameters.add("deleteSourceFileAttributes=false //defaults to false");
        }

        if ((this.keptAttributesMask & ANNOTATION_ATTRIBUTES_MASK) != ANNOTATION_ATTRIBUTES_MASK) {
            this.trimParameters.add("deleteAnnotationAttributes=true");
        } else {
            this.trimParameters.add("deleteAnnotationAttributes=false");
        }

        if ((this.keptAttributesMask & EXCEPTIONS_MASK) == 0) {
            this.trimParameters.add("deleteExceptionAttributes=true");
        } else {
            this.trimParameters.add("deleteExceptionAttributes=false //defaults to false");
        }

        this.trimParameters.add("//deleteUnknownAttributes=true");
        if ((this.keptAttributesMask & LINE_NUMBER_TABLE_MASK) == LINE_NUMBER_TABLE_MASK) {
            this.obfuscateParameters.add("lineNumbers=keep //can specify delete, scramble or keep");
        }

        if ((this.keptAttributesMask & SIGNATURE_MASK) == SIGNATURE_MASK) {
            if ((this.keptAttributesMask & GENERICS_ATTRIBUTES_MASK) != GENERICS_ATTRIBUTES_MASK) {
                this.logMessage(
                        "'keepGenericsInfo' set to default to 'true' however both 'LocalVariableTypeTable' and 'Bridge' attributes were not specified to be retained."
                );
            }
        } else {
            this.obfuscateParameters.add("keepGenericsInfo=false");
        }

        if ((this.keptAttributesMask & INNER_CLASS_INFO_MASK) == INNER_CLASS_INFO_MASK) {
            this.obfuscateParameters.add("keepInnerClassInfo=true");
        } else {
            if ((this.keptAttributesMask & INNER_CLASSES_MASK) == INNER_CLASSES_MASK && (this.keptAttributesMask & ENCLOSING_METHOD_MASK) != ENCLOSING_METHOD_MASK
            ) {
                this.logWarning("'keepInnerClassInfo' set to 'false' because 'EnclosingMethod' attribute not retained.");
            }

            this.obfuscateParameters.add("keepInnerClassInfo=false");
        }

        if ((this.keptAttributesMask & LOCAL_VARIABLE_TABLE_MASK) == LOCAL_VARIABLE_TABLE_MASK) {
            String string1 = " //can specify delete, keepVisibleMethodParameters, keepVisibleMethodParametersIfNotObfuscated, keepMethodParametersIfNotObfuscated, obfuscate or keep";
            if (this.keepParameterNames) {
                this.obfuscateParameters.add("localVariables=keepMethodParametersIfNotObfuscated" + string1);
            } else {
                this.obfuscateParameters.add("localVariables=keep" + string1);
            }
        } else if (this.keepParameterNames) {
            this.obfuscateParameters.add("localVariables=keepMethodParametersIfNotObfuscated");
        }

        if ((this.keptAttributesMask & METHOD_PARAMETERS_MASK) == 0) {
            String string2 = " //can specify delete, keepVisible, keepVisibleIfNotObfuscated, keepIfNotObfuscated, obfuscate or keep";
            if (this.keepParameterNames) {
                this.obfuscateParameters.add("methodParameters=keepIfNotObfuscated" + string2);
            } else {
                this.obfuscateParameters.add("methodParameters=keepVisibleIfNotObfuscated" + string2);
            }
        } else {
            this.obfuscateParameters.add("methodParameters=keep");
        }
    }

    public String getBaseDirectory() {
        return this.baseDirectory;
    }

    public void mergeTemplateStatement(String string, String string1, SyncIndexedSet syncIndexedSet, StringBuilder stringBuilder) {
        stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
        stringBuilder.append("//Appending '" + string + "' statement found in '" + this.templateScriptPath + "'");
        stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
        StringTokenizer stringTokenizer = new StringTokenizer(string1);
        if (stringTokenizer.hasMoreTokens()) {
            String string2 = stringTokenizer.nextToken();
            if (string2.equals(string)) {
                StringBuilder stringBuilder1 = new StringBuilder();

                while (stringTokenizer.hasMoreTokens()) {
                    string2 = stringTokenizer.nextToken();
                    if (string2.equals("and")) {
                        if (stringBuilder1.length() > 0) {
                            syncIndexedSet.add(stringBuilder1.toString());
                            stringBuilder1.setLength(0);
                        }
                    } else {
                        if (string2.endsWith(";")) {
                            if (string2.length() > 1) {
                                if (stringBuilder1.length() > 0) {
                                    stringBuilder1.append(' ');
                                }

                                stringBuilder1.append(string2.substring(0, string2.length() - 1));
                            }

                            syncIndexedSet.add(stringBuilder1.toString());
                            break;
                        }

                        if (stringBuilder1.length() > 0) {
                            stringBuilder1.append(' ');
                        }

                        stringBuilder1.append(string2);
                    }
                }
            }
        }
    }

    public static void appendLineSeparatedStatement(String string, int ba, SyncIndexedSet syncIndexedSet, StringBuilder stringBuilder) {
        int bb = ba;
        bb = Math.max(bb, string.length() + 2);
        stringBuilder.append(ZkmStringUtils.pad(string, 76, bb - 1, 32));
        int bc = 0;
        int bd = 0;

        for (int i = syncIndexedSet.size(); bd < i; i = syncIndexedSet.size()) {
            String string1 = (String) syncIndexedSet.getElementAt(bc);
            if (bc == 0) {
                stringBuilder.append(string1);
            } else {
                stringBuilder.append(ZkmStringUtils.pad(" ", 82, bb - 1, 32));
                stringBuilder.append(string1);
            }

            if (bc < syncIndexedSet.size() - 1) {
                stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
            }

            bd = ++bc;
        }

        if (syncIndexedSet.size() > 0) {
            stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
            stringBuilder.append(ZkmStringUtils.pad(";", 82, bb, 32));
        } else {
            stringBuilder.append(";");
        }

        stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
        stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
    }

    public void setKeepParameterNames() {
        this.keepParameterNames = true;
    }

    public static void appendQuotedListStatement(String string, int ba, SyncIndexedSet syncIndexedSet, StringBuilder stringBuilder) {
        StringBuilder stringBuilder1 = stringBuilder;
        SyncIndexedSet syncIndexedSet1 = syncIndexedSet;
        Integer integer = ba;
        appendQuotedListStatement(string, (String) null, integer, syncIndexedSet1, stringBuilder1);
    }

    public static String resolveZkmLogDir(String string) {
        if (HiddenOptionFlags.DEFAULT_DIR != null) {
            File file1 = new File(HiddenOptionFlags.DEFAULT_DIR);
            if (file1.exists() && file1.isDirectory()) {
                return file1.getAbsolutePath();
            }
        }

        if (string != null) {
            File file2 = new File(string);
            if (file2.exists() && file2.isDirectory()) {
                return file2.getAbsolutePath();
            }
        }

        File file3 = new File(ZkmFileUtils.USER_DIR);
        return file3.getAbsolutePath();
    }

    public static void appendMapStatement(int ba, Map map1, StringBuilder stringBuilder) {
        int bb = ba;
        bb = Math.max(bb, "open".length() + 2);
        if (map1.size() > 0) {
            stringBuilder.append(ZkmStringUtils.pad("open", 76, bb - 1, 32));
            int bc = 0;

            for (Iterator iterator = map1.entrySet().iterator(); iterator.hasNext(); bc++) {
                Entry entry = (Entry) iterator.next();
                String string = (String) entry.getKey();
                if (bc == 0) {
                    stringBuilder.append("\"");
                } else {
                    stringBuilder.append(ZkmStringUtils.pad("\"", 82, bb, 32));
                }

                stringBuilder.append(string);
                stringBuilder.append("\"");
                String string1 = (String) entry.getValue();
                if (string1 != null && string1.length() > 0) {
                    stringBuilder.append(" ");
                    stringBuilder.append(string1);
                }

                stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
            }

            stringBuilder.append(ZkmStringUtils.pad(";", 82, bb, 32));
            stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
            stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
        }
    }

    public void setDontObfuscate() {
        this.dontObfuscate = true;
    }

    public void addExclude(Object object) {
        this.excludes.add(object);
    }

    public void addOpenArchive(Object object, Object object1) {
        this.openArchives.put(object, object1);
    }

    public void addRemoveMethodCallsExclude(Object object) {
        this.removeMethodCallsExcludes.add(object);
    }

    public void setOverloadAggressively() {
        this.overloadAggressively = true;
    }

    public String getPrintSeedsFile() {
        return this.printSeedsFile;
    }

    public int getStatementNameWidth() {
        if (this.removeMethodCallsIncludes != null && !this.removeMethodCallsIncludes.isEmpty()) {
            return "removeMethodCallsInclude".length() + 2;
        }

        if (this.removeMethodCallsExcludes != null && !this.removeMethodCallsExcludes.isEmpty()) {
            return "removeMethodCallsExclude".length() + 2;
        }

        String string;
        if (this.ignoreMissingReferences != null) {
            if (!this.ignoreMissingReferences.isEmpty()) {
                return "ignoreMissingReferences".length() + 2;
            }

            string = "trimUnexclude";
        } else {
            string = "trimUnexclude";
        }

        return string.length() + 2;
    }
}
