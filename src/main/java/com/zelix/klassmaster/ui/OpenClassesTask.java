package com.zelix.klassmaster.ui;

import com.zelix.klassmaster.archive.InputFileLocation;
import com.zelix.klassmaster.archive.SourceArchive;
import com.zelix.klassmaster.log.DedupPrintWriter;
import com.zelix.klassmaster.log.MessageReporter;
import com.zelix.klassmaster.ui.dialog.ClassOpenWarningsDialog;
import com.zelix.klassmaster.ui.dialog.MessageBoxDialog;
import com.zelix.klassmaster.ui.dialog.TextViewerDialog;
import com.zelix.klassmaster.util.DialogCallback;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

public class OpenClassesTask implements Runnable {
    public final ZkmMainWindow mainWindow;
    public final MessageReporter messageReporter;
    public final InputFileLocation[] classFileLocations;
    public final SourceArchive[] sourceArchives;
    public final Set skippedArchives;
    public final InputFileLocation[] jadFiles;
    public final InputFileLocation[] xmlFiles;
    public final InputFileLocation[] yamlFiles;
    public final InputFileLocation[] propertiesFiles;
    public final boolean openNestedArchives;
    public final DialogCallback dialogCallback;
    public final OperationStatusCallback statusCallback;

    @Override
    public void run() {
        try {
            this.mainWindow.setBusy(true);
            PrintWriter printWriter = null;
            try {
                printWriter = new DedupPrintWriter(new FileWriter("ZKM_LoadLog.txt"));
            } catch (final IOException ex) {
                this.messageReporter.reportError("ERROR:", "Couldn't open ZKM_LoadLog.txt : " + ex.getClass().getName());
            }
            PrintWriter printWriter2 = null;
            try {
                printWriter2 = new PrintWriter(new FileWriter("ZKM_WarningLog.txt"), true);
            } catch (final IOException ex2) {
                this.messageReporter.reportError("ERROR:", "Couldn't open ZKM_WarningLog.txt : " + ex2.getClass().getName());
            }
            final MutableInt mutableInt = new MutableInt(0);
            try {
                ZkmMainWindow.accessClassRepository(this.mainWindow).openClasses(this.classFileLocations, this.sourceArchives, this.skippedArchives, null, null, null, null, null, null, this.jadFiles, this.xmlFiles, this.yamlFiles, this.propertiesFiles, this.openNestedArchives, this.messageReporter, this.dialogCallback, this.statusCallback, null, printWriter, printWriter2, mutableInt, ZkmMainWindow.getLogWriter(this.mainWindow));
                if (!ZkmMainWindow.accessClassRepository(this.mainWindow).hasNoClassesOpened() && !ZkmMainWindow.accessClassRepository(this.mainWindow).hasProgramClasses()) {
                    this.messageReporter.reportErrorWithDetail("FATAL ERROR:", "No classes other than module-info opened", "No classes other than module-info opened. module-info classes are supported but they cannot be directly changed. Use the \"File | Open\" menu to also open your ordinary classes.");
                }
            } catch (final Throwable t) {
                this.messageReporter.reportErrorWithDetail("FATAL ERROR:", "Unknown error : " + t.getClass().getName(), ZkmUtils.stackTraceToString(t));
            } finally {
                if (printWriter != null) {
                    printWriter.close();
                }
                if (printWriter2 != null) {
                    printWriter2.close();
                }
            }
            BufferedReader bufferedReader = null;
            BufferedReader bufferedReader2 = null;
            try {
                bufferedReader2 = new BufferedReader(new FileReader("ZKM_LoadLog.txt"));
                bufferedReader2.mark(1);
                if (bufferedReader2.read() != -1) {
                    bufferedReader2.reset();
                    new TextViewerDialog(this.mainWindow, "Class Open Errors", "The following errors were encounted...", bufferedReader2, false, true);
                }
                try {
                    bufferedReader2.close();
                } catch (final IOException ex3) {
                }
            } catch (final FileNotFoundException ex4) {
            } catch (final IOException ex5) {
                final MessageBoxDialog messageBoxDialog = new MessageBoxDialog(this.mainWindow, "Error", "Error reading ZKM_LoadLog.txt : " + ex5.getClass().getName());
            } finally {
                if (bufferedReader2 != null) {
                    try {
                        bufferedReader2.close();
                    } catch (final IOException ex6) {
                    }
                }
            }
            try {
                bufferedReader = new BufferedReader(new FileReader("ZKM_WarningLog.txt"));
                bufferedReader.mark(1);
                if (bufferedReader.read() != -1) {
                    bufferedReader.reset();
                    new ClassOpenWarningsDialog(this.mainWindow, bufferedReader);
                }
                try {
                    bufferedReader.close();
                } catch (final IOException ex7) {
                }
            } catch (final FileNotFoundException ex8) {
            } catch (final IOException ex9) {
                final MessageBoxDialog messageBoxDialog2 = new MessageBoxDialog(this.mainWindow, "Error", "Error reading ZKM_WarningLog.txt : " + ex9.getClass().getName());
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (final IOException ex10) {
                    }
                }
            }
            if (this.statusCallback != null && this.mainWindow.isVisible()) {
                this.statusCallback.onDialogCancelled();
            }
        } catch (final Throwable t2) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(t2);
        }
    }

    public OpenClassesTask(
            ZkmMainWindow zkmMainWindow,
            MessageReporter messageReporter1,
            InputFileLocation[] inputFileLocations,
            SourceArchive[] sourceArchives1,
            Set set1,
            InputFileLocation[] inputFileLocations1,
            InputFileLocation[] inputFileLocations2,
            InputFileLocation[] inputFileLocations3,
            InputFileLocation[] inputFileLocations4,
            boolean openNestedArchives,
            DialogCallback dialogCallback1,
            OperationStatusCallback operationStatusCallback
    ) {
        this.mainWindow = zkmMainWindow;
        this.messageReporter = messageReporter1;
        this.classFileLocations = inputFileLocations;
        this.sourceArchives = sourceArchives1;
        this.skippedArchives = set1;
        this.jadFiles = inputFileLocations1;
        this.xmlFiles = inputFileLocations2;
        this.yamlFiles = inputFileLocations3;
        this.propertiesFiles = inputFileLocations4;
        this.openNestedArchives = openNestedArchives;
        this.dialogCallback = dialogCallback1;
        this.statusCallback = operationStatusCallback;
    }
}
