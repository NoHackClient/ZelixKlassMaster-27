package com.zelix.klassmaster.log;

import com.zelix.klassmaster.KlassMaster;
import com.zelix.klassmaster.archive.InputFileLocation;
import com.zelix.klassmaster.archive.SourceArchive;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.ui.OperationStatusCallback;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.NoOpCallback;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

public class LoadLogWriterTask implements Runnable {
    public final KlassMaster application;
    public final ConsoleMessageReporter messageReporter;
    public final ClassRepository classRepository;
    public final NoOpCallback callback;
    public final PrintWriter logWriter;

    @Override
    public void run() {
        try {
            DedupPrintWriter dedupPrintWriter = null;

            try {
                FileWriter fileWriter = new FileWriter("ZKM_LoadLog.txt");
                dedupPrintWriter = new DedupPrintWriter(fileWriter);
            } catch (IOException iOException1) {
                this.messageReporter.reportError("ERROR:", "Couldn't open ZKM_LoadLog.txt : " + iOException1.getClass().getName());
            }

            PrintWriter printWriter = null;

            try {
                printWriter = new PrintWriter(new FileWriter("ZKM_WarningLog.txt"), true);
            } catch (IOException iOException) {
                this.messageReporter.reportError("ERROR:", "Couldn't open ZKM_WarningLog.txt : " + iOException.getClass().getName());
            }

            ClassRepository classRepository1 = this.classRepository;
            InputFileLocation[] inputFileLocations4 = new InputFileLocation[0];
            SourceArchive[] sourceArchives1 = new SourceArchive[0];
            String string1 = (String) null;
            InputFileLocation[] inputFileLocations5 = new InputFileLocation[0];
            InputFileLocation[] inputFileLocations6 = new InputFileLocation[0];
            InputFileLocation[] inputFileLocations7 = new InputFileLocation[0];
            InputFileLocation[] inputFileLocations8 = new InputFileLocation[0];
            ConsoleMessageReporter consoleMessageReporter1 = this.messageReporter;
            NoOpCallback noOpCallback1 = this.callback;
            ScriptEnvironment scriptEnvironment2 = (ScriptEnvironment) null;
            MutableInt mutableInt1 = new MutableInt(0);
            PrintWriter printWriter2 = this.logWriter;
            MutableInt mutableInt = mutableInt1;
            PrintWriter printWriter1 = printWriter;
            DedupPrintWriter dedupPrintWriter1 = dedupPrintWriter;
            ScriptEnvironment scriptEnvironment1 = scriptEnvironment2;
            Object object5 = null;
            NoOpCallback noOpCallback = noOpCallback1;
            ConsoleMessageReporter consoleMessageReporter = consoleMessageReporter1;
            Boolean boolean1 = true;
            InputFileLocation[] inputFileLocations3 = inputFileLocations8;
            InputFileLocation[] inputFileLocations2 = inputFileLocations7;
            InputFileLocation[] inputFileLocations1 = inputFileLocations6;
            InputFileLocation[] inputFileLocations = inputFileLocations5;
            String string = string1;
            Object object4 = null;
            Object object3 = null;
            Object object2 = null;
            Object object1 = null;
            Object object = null;
            classRepository1.openClasses(
                    inputFileLocations4,
                    sourceArchives1,
                    (Set) null,
                    (Set) object,
                    (Set) object1,
                    (Set) object2,
                    (Set) object3,
                    (Set) object4,
                    string,
                    inputFileLocations,
                    inputFileLocations1,
                    inputFileLocations2,
                    inputFileLocations3,
                    boolean1,
                    consoleMessageReporter,
                    noOpCallback,
                    (OperationStatusCallback) object5,
                    scriptEnvironment1,
                    dedupPrintWriter1,
                    printWriter1,
                    mutableInt,
                    printWriter2
            );
            if (dedupPrintWriter != null) {
                dedupPrintWriter.close();
            }

            if (printWriter != null) {
                printWriter.close();
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }

    public LoadLogWriterTask(
            KlassMaster klassMaster,
            ConsoleMessageReporter consoleMessageReporter,
            ClassRepository classRepository1,
            NoOpCallback noOpCallback,
            PrintWriter printWriter
    ) {
        this.application = klassMaster;
        this.messageReporter = consoleMessageReporter;
        this.classRepository = classRepository1;
        this.callback = noOpCallback;
        this.logWriter = printWriter;
    }
}
