package com.zelix.klassmaster.ui;

import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.config.TrimOptions;
import com.zelix.klassmaster.log.MessageReporter;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.ui.dialog.MessageBoxDialog;
import com.zelix.klassmaster.ui.dialog.TextViewerDialog;
import com.zelix.klassmaster.util.DialogCallback;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

public class TrimReportViewTask implements Runnable {
    public final ZkmMainWindow mainWindow;
    public final OperationStatusCallback statusCallback;
    public final MessageReporter messageReporter;
    public final TrimOptions trimOptions;
    public final Vector trimExcludeStatements;
    public final DialogCallback dialogCallback;
    public final ScriptEnvironment scriptEnvironment;

    public TrimReportViewTask(
            ZkmMainWindow zkmMainWindow,
            OperationStatusCallback operationStatusCallback,
            MessageReporter messageReporter1,
            TrimOptions trimOptions1,
            Vector vector,
            DialogCallback dialogCallback1,
            ScriptEnvironment scriptEnvironment1
    ) {
        this.mainWindow = zkmMainWindow;
        this.statusCallback = operationStatusCallback;
        this.messageReporter = messageReporter1;
        this.trimOptions = trimOptions1;
        this.trimExcludeStatements = vector;
        this.dialogCallback = dialogCallback1;
        this.scriptEnvironment = scriptEnvironment1;
    }

    @Override
    public void run() {
        try {
            PrintWriter printWriter = null;
            ZkmMainWindow zkmMainWindow = null;
            Label_0093:
            {
                Label_0089:
                {
                    try {
                        printWriter = new PrintWriter(new FileWriter("ZKM_TrimLog.txt"));
                    } catch (final IOException ex) {
                        if (this.statusCallback != null) {
                            this.statusCallback.setStatus(null);
                        }
                        this.messageReporter.reportError("ERROR:", "Couldn't open ZKM_TrimLog.txt : " + ex.getClass().getName());
                        break Label_0089;
                    }
                    zkmMainWindow = this.mainWindow;
                    break Label_0093;
                }
                zkmMainWindow = this.mainWindow;
            }
            ZkmMainWindow.accessClassRepository(zkmMainWindow).trim(this.trimOptions, this.trimExcludeStatements, null, null, null, printWriter, this.messageReporter, this.dialogCallback, this.statusCallback, this.scriptEnvironment);
            ZkmMainWindow.getSelectedItemHolder(this.mainWindow).setChangedAndNotify();
            final ProgramClass programClass = (ProgramClass) ZkmMainWindow.getSelectedClassHolder(this.mainWindow).getSelectedNode();
            if (programClass != null) {
                programClass.refreshPropertyNodes();
            }
            if (printWriter != null) {
                printWriter.close();
            }
            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader(new FileReader("ZKM_TrimLog.txt"));
                bufferedReader.mark(1);
                if (bufferedReader.read() != -1) {
                    bufferedReader.reset();
                    new TextViewerDialog(this.mainWindow, "Trim report", "Trim Report (ZKM_TrimLog.txt)...", bufferedReader);
                }
                try {
                    bufferedReader.close();
                } catch (final IOException ex2) {
                }
            } catch (final FileNotFoundException ex3) {
            } catch (final IOException ex4) {
                final MessageBoxDialog messageBoxDialog = new MessageBoxDialog(this.mainWindow, "Error", "Error reading ZKM_TrimLog.txt : " + ex4.getClass().getName());
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (final IOException ex5) {
                    }
                }
            }
            if (this.statusCallback != null) {
                this.statusCallback.onDialogCancelled();
            }
        } catch (final Throwable t) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(t);
        }
    }
}
