package com.zelix.klassmaster.ui;

import com.zelix.klassmaster.log.MessageReporter;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.DialogCallback;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.File;

public class SaveClassesTask implements Runnable {
    public final ZkmMainWindow mainWindow;
    public final File saveLocation;
    public final MessageReporter messageReporter;
    public final ScriptEnvironment scriptEnvironment;
    public final DialogCallback progressCallback;
    public final DialogCallback completionCallback;

    @Override
    public void run() {
        try {
            ZkmMainWindow.accessClassRepository(this.mainWindow)
                    .saveAll(1, false, false, (String) null, this.saveLocation, this.messageReporter, this.scriptEnvironment, this.progressCallback);
            if (this.completionCallback != null) {
                this.completionCallback.onDialogCancelled();
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }

    public SaveClassesTask(
            ZkmMainWindow zkmMainWindow,
            File file1,
            MessageReporter messageReporter1,
            ScriptEnvironment scriptEnvironment1,
            DialogCallback dialogCallback1,
            DialogCallback dialogCallback2
    ) {
        this.mainWindow = zkmMainWindow;
        this.saveLocation = file1;
        this.messageReporter = messageReporter1;
        this.scriptEnvironment = scriptEnvironment1;
        this.progressCallback = dialogCallback1;
        this.completionCallback = dialogCallback2;
    }
}
