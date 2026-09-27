package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.config.TrimOptions;
import com.zelix.klassmaster.exceptions.ClassLoadFailureException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.log.MessageReporter;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.DialogCallback;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Vector;

public class TestTrimReportTask implements Runnable {
    public final TrimExclusionsBaseDialog trimDialog;
    public final Vector trimExcludeParams;
    public final MessageReporter messageReporter;
    public final DialogCallback callback;

    @Override
    public void run() {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PrintWriter printWriter = new PrintWriter(byteArrayOutputStream);

            try {
                ScriptEnvironment scriptEnvironment1 = this.trimDialog.scriptEnvironment;
                TrimOptions trimOptions1 = this.trimDialog.trimOptions;
                DialogCallback dialogCallback1 = this.callback;
                PrintWriter printWriter1 = printWriter;
                this.trimDialog
                        .classRepository
                        .reportTrimPreview(this.trimExcludeParams, (Vector) null, printWriter1, dialogCallback1, trimOptions1, scriptEnvironment1);
                printWriter.close();
                String string = byteArrayOutputStream.toString();
                if (string.length() > -1) {
                    new TextViewerDialog(this.trimDialog, "Test trim report", "Test Trim Report...", string, false, true, false);
                }
            } catch (ClassLoadFailureException classLoadFailureException) {
                this.messageReporter.reportError("FILE ERROR:", classLoadFailureException.getMessage());
            } catch (ZkmProcessingException zkmProcessingException) {
                this.messageReporter.reportError("ERROR:", zkmProcessingException.getMessage());
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public TestTrimReportTask(TrimExclusionsBaseDialog trimExclusionsBaseDialog, Vector vector, MessageReporter messageReporter1, DialogCallback dialogCallback1) {
        this.trimDialog = trimExclusionsBaseDialog;
        this.trimExcludeParams = vector;
        this.messageReporter = messageReporter1;
        this.callback = dialogCallback1;
    }
}
