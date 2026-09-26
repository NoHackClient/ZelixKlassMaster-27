package com.zelix.klassmaster.ui;

import com.zelix.klassmaster.archive.InputFileLocation;
import com.zelix.klassmaster.archive.SourceArchive;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.ObservableHolder;

import java.io.IOException;
import java.util.Set;

public class MainFrameOpenCallback extends CallbackAdapter {
    public final ZkmMainWindow mainWindow;

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        this.mainWindow.setBusy(false);
        ZkmMainWindow zkmMainWindow = this.mainWindow;
        InputFileLocation[] inputFileLocations = (InputFileLocation[]) null;
        SourceArchive[] sourceArchives1 = (SourceArchive[]) null;
        InputFileLocation[] inputFileLocations1 = (InputFileLocation[]) null;
        InputFileLocation[] inputFileLocations2 = (InputFileLocation[]) null;
        Boolean boolean1 = ZkmMainWindow.getUserPreferences(this.mainWindow).isOpenNestedArchives();
        OperationStatusCallback operationStatusCallback = (OperationStatusCallback) null;
        ZkmMainWindow.invokeOpenClasses(
                zkmMainWindow, inputFileLocations, sourceArchives1, inputFileLocations1, inputFileLocations2, boolean1, (Set) null, operationStatusCallback
        );
    }

    public void onClassesOpened(
            InputFileLocation[] inputFileLocations,
            SourceArchive[] sourceArchives1,
            InputFileLocation[] inputFileLocations1,
            InputFileLocation[] inputFileLocations2,
            ObservableHolder observableHolder,
            Boolean boolean1,
            Set set1
    ) throws ZkmException, IOException {
        this.mainWindow.setBusy(false);
        this.mainWindow
                .openChosenClasses(
                        inputFileLocations, sourceArchives1, inputFileLocations1, inputFileLocations2, observableHolder, boolean1, set1, (OperationStatusCallback) null
                );
    }

    @Override
    public void onDialogResult(Object object, Object object1, Object object2, Object object3, Object object7, Object object4, Object object5, Object object6) throws ZkmException, IOException {
        InputFileLocation[] inputFileLocations = (InputFileLocation[]) object;
        SourceArchive[] sourceArchives1 = (SourceArchive[]) object1;
        InputFileLocation[] inputFileLocations1 = (InputFileLocation[]) object2;
        InputFileLocation[] inputFileLocations2 = (InputFileLocation[]) object3;
        ObservableHolder observableHolder1 = (ObservableHolder) object4;
        Boolean boolean2 = (Boolean) object5;
        Set set1 = (Set) object6;
        Boolean boolean1 = boolean2;
        ObservableHolder observableHolder = observableHolder1;
        this.onClassesOpened(inputFileLocations, sourceArchives1, inputFileLocations1, inputFileLocations2, observableHolder, boolean1, set1);
    }

    public MainFrameOpenCallback(ZkmMainWindow zkmMainWindow) {
        this.mainWindow = zkmMainWindow;
    }
}
