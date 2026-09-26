package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.archive.DirectoryFileLister;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.config.TrimOptions;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.log.DedupPrintWriter;
import com.zelix.klassmaster.log.LogMessageReporter;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;
import com.zelix.klassmaster.ui.OperationStatusCallback;
import com.zelix.klassmaster.util.NoOpCallback;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class ASTTrimStatement extends KeyValueStatementNode {
    public void applyDeleteExceptionAttributes(TrimOptions trimOptions1) {
        trimOptions1.e = false;
        List list1 = super.parameterValues.getValues("deleteExceptionAttributes");
        if (list1 != null && list1.size() > 0) {
            String string = (String) list1.get(0);
            if (string != null && string.equals("true")) {
                trimOptions1.e = true;
            }
        }
    }

    public void applyDeleteUnknownAttributes(TrimOptions trimOptions1) {
        trimOptions1.c = false;
        List list1 = super.parameterValues.getValues("deleteUnknownAttributes");
        if (list1 != null && list1.size() > 0) {
            String string = (String) list1.get(0);
            if (string != null && string.equals("true")) {
                trimOptions1.c = true;
            }
        }
    }

    public void applyDeleteDeprecatedAttributes(TrimOptions trimOptions1) {
        trimOptions1.b = true;
        List list1 = super.parameterValues.getValues("deleteDeprecatedAttributes");
        if (list1 != null && list1.size() > 0) {
            String string = (String) list1.get(0);
            if (string != null && string.equals("false")) {
                trimOptions1.b = false;
            }
        }
    }

    @Override
    public String getStatementName() {
        return "trim";
    }

    public ASTTrimStatement() {
        super(55);
    }

    public void applyDeleteAnnotationAttributes(TrimOptions trimOptions1) {
        trimOptions1.d = false;
        List list1 = super.parameterValues.getValues("deleteAnnotationAttributes");
        if (list1 != null && list1.size() > 0) {
            String string = (String) list1.get(0);
            if (string != null && string.equals("true")) {
                trimOptions1.d = true;
            }
        }
    }

    public void applyDeleteDebugExtensionAttributes(TrimOptions trimOptions1) {
        trimOptions1.f = true;
        List list1 = super.parameterValues.getValues("deleteDebugExtensionAttributes");
        if (list1 != null && list1.size() > 0) {
            String string = (String) list1.get(0);
            if (string != null && string.equals("false")) {
                trimOptions1.f = false;
            }
        }
    }

    public String getProgressVerb() {
        return "Trimming";
    }

    @Override
    public void executeStatement(Object object, Object object1, Object object2, Object object3) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        int bb = (Integer) object1;
        int bc = (Integer) object3;
        int ba = (Integer) object2;
        int seriousErrorCount = scriptEnvironment1.getSeriousErrorCount();
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
                            + " with unusable classes : "
                            + classRepository1.getOpenErrorMessage()
            );
        } else if (!classRepository1.hasProgramClasses()) {
            scriptEnvironment1.logMessage(
                    "Only module-info.class classes opened. Statement \""
                            + this.getStatementName()
                            + "\" statement at line "
                            + this.getStatementLine()
                            + " will have no effect."
            );
            return;
        }

        PrintWriter printWriter = scriptEnvironment1.getLogWriter();
        LogMessageReporter logMessageReporter = new LogMessageReporter(scriptEnvironment1, ZkmScriptSimpleNode.getTimestampPrefix().length());
        NoOpCallback noOpCallback = NoOpCallback.getInstance();
        DedupPrintWriter dedupPrintWriter = null;

        try {
            File file1 = new File(scriptEnvironment1.getTrimLogFileName());
            File file2 = file1.getParentFile();
            if (!file2.exists()) {
                DirectoryFileLister.ensureDirectoryExists(file2.getAbsolutePath());
            }

            FileWriter fileWriter = new FileWriter(file1);
            dedupPrintWriter = new DedupPrintWriter(fileWriter);
        } catch (IOException iOException) {
            logMessageReporter.reportError("ERROR:", "Couldn't open " + scriptEnvironment1.getTrimLogFileName() + " : " + iOException.getClass().getName());
        }

        String string = ZkmScriptSimpleNode.getTimestampPrefix() + " " + this.getProgressVerb() + "...";
        printWriter.println(string);
        System.out.println(string);
        TrimOptions trimOptions1 = new TrimOptions();
        this.applyDeleteSourceFileAttributes(trimOptions1);
        this.applyDeleteDeprecatedAttributes(trimOptions1);
        this.applyDeleteAnnotationAttributes(trimOptions1);
        this.applyDeleteUnknownAttributes(trimOptions1);
        this.applyDeleteExceptionAttributes(trimOptions1);
        this.applyDeleteDebugExtensionAttributes(trimOptions1);
        if (scriptEnvironment1.isVerbose()) {
            printWriter.println("\tdeleteSourceFileAttributes=" + trimOptions1.a);
            printWriter.println("\tdeleteDeprecatedAttributes=" + trimOptions1.b);
            printWriter.println("\tdeleteAnnotationAttributes=" + trimOptions1.d);
            printWriter.println("\tdeleteUnknownAttributes=" + trimOptions1.c);
            printWriter.println("\tdeleteExceptionAttributes=" + trimOptions1.e);
            printWriter.println("\tdeleteDebugExtensionAttributes=" + trimOptions1.f);
        }

        List list1 = scriptEnvironment1.getTrimExcludeStatements();
        List list2 = scriptEnvironment1.getTrimUnexcludeStatements();
        List list3 = scriptEnvironment1.getExistingSerializedClassesStatements();
        List list4 = scriptEnvironment1.getFixedClassesStatements();
        ScriptEnvironment scriptEnvironment2 = scriptEnvironment1;
        classRepository1.trim(
                trimOptions1, list1, list2, list3, list4, dedupPrintWriter, logMessageReporter, noOpCallback, (OperationStatusCallback) null, scriptEnvironment2
        );
        ASTTrimStatement aSTTrimStatement1;
        ScriptEnvironment scriptEnvironment3;
        int be;
        int bf;
        int bg;
        String string1;
        if (dedupPrintWriter != null) {
            dedupPrintWriter.close();
            aSTTrimStatement1 = this;
            scriptEnvironment3 = scriptEnvironment1;
            be = bb;
            bf = ba;
            bg = bc;
            string1 = "while executing";
        } else {
            aSTTrimStatement1 = this;
            scriptEnvironment3 = scriptEnvironment1;
            be = bb;
            bf = ba;
            bg = bc;
            string1 = "while executing";
        }

        aSTTrimStatement1.printMessageSummary(scriptEnvironment3, be, bf, bg, string1);
        if (scriptEnvironment1.getSeriousErrorCount() > seriousErrorCount) {
            logMessageReporter.reportFatalErrorWithDetail(
                    "FATAL ERROR:", "Serious Errors detected during " + this.getStatementName(), scriptEnvironment1.getSeriousErrorsText()
            );
        }
    }

    public void applyDeleteSourceFileAttributes(TrimOptions trimOptions1) {
        trimOptions1.a = false;
        List list1 = super.parameterValues.getValues("deleteSourceFileAttributes");
        if (list1 != null && list1.size() > 0) {
            String string = (String) list1.get(0);
            if (string != null && string.equals("true")) {
                trimOptions1.a = true;
            }
        }
    }
}
