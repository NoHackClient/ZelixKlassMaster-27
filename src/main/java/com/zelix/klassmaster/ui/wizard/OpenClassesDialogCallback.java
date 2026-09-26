package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.archive.InputFileLocation;
import com.zelix.klassmaster.archive.SourceArchive;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.CallbackAdapter;
import com.zelix.klassmaster.util.ObservableHolder;

import java.io.IOException;
import java.util.Set;

public class OpenClassesDialogCallback extends CallbackAdapter {
    public final OpenDialogHelper openHelper;

    public void onPreviousPressed(InputFileLocation[] inputFileLocations, ObservableHolder observableHolder) throws ZkmException, IOException {
        OpenDialogHelper openDialogHelper = this.openHelper;
        Boolean boolean2 = OpenDialogHelper.accessGetUserPreferences(this.openHelper).isOpenNestedArchives();
        Integer integer = 2;
        Object object2 = null;
        Boolean boolean1 = boolean2;
        ObservableHolder observableHolder1 = observableHolder;
        Object object1 = null;
        Object object = null;
        OpenDialogHelper.accessOnClassesSelected(
                openDialogHelper,
                inputFileLocations,
                (SourceArchive[]) null,
                (InputFileLocation[]) object,
                (InputFileLocation[]) object1,
                observableHolder1,
                boolean1,
                (Set) object2,
                integer
        );
    }

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        this.openHelper.finishHelper();
    }

    public void onClassesSelected(
            InputFileLocation[] inputFileLocations,
            SourceArchive[] sourceArchives1,
            InputFileLocation[] inputFileLocations1,
            InputFileLocation[] inputFileLocations2,
            ObservableHolder observableHolder,
            Boolean boolean1,
            Set set1
    ) throws ZkmException, IOException {
        OpenDialogHelper.accessOnClassesSelected(
                this.openHelper, inputFileLocations, sourceArchives1, inputFileLocations1, inputFileLocations2, observableHolder, boolean1, set1, 1
        );
    }

    @Override
    public void onDialogResult(Object object, Object object2, Object object3, Object object4, Object object5, Object object1) throws ZkmException, IOException {
        InputFileLocation[] inputFileLocations = (InputFileLocation[]) object;
        ObservableHolder observableHolder = (ObservableHolder) object1;
        this.onPreviousPressed(inputFileLocations, observableHolder);
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
        this.onClassesSelected(inputFileLocations, sourceArchives1, inputFileLocations1, inputFileLocations2, observableHolder, boolean1, set1);
    }

    public OpenClassesDialogCallback(OpenDialogHelper openDialogHelper) {
        this.openHelper = openDialogHelper;
    }
}
