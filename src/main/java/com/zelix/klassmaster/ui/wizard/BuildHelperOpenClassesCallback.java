package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.archive.InputFileLocation;
import com.zelix.klassmaster.archive.SourceArchive;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.CallbackAdapter;
import com.zelix.klassmaster.util.ObservableHolder;

import java.io.IOException;
import java.util.Set;

public class BuildHelperOpenClassesCallback extends CallbackAdapter {
    public final BuildHelperWizard wizard;

    @Override
    public void onDialogResult(Object object, Object object1, Object object2, Object object3, Object object4, Object object5, Object object6, Object object7) throws ZkmException, IOException {
        this.onClassesSelected(
                (InputFileLocation[]) object,
                (SourceArchive[]) object1,
                (InputFileLocation[]) object2,
                (InputFileLocation[]) object3,
                (InputFileLocation[]) object4,
                (ObservableHolder) object5,
                (Boolean) object6,
                (Set) object7
        );
    }

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        this.wizard.finishWizard();
    }

    @Override
    public void onDialogResult(Object object, Object object2, Object object3, Object object4, Object object5, Object object1) throws ZkmException, IOException {
        InputFileLocation[] inputFileLocations = (InputFileLocation[]) object;
        ObservableHolder observableHolder = (ObservableHolder) object1;
        this.onPreviousPressed(inputFileLocations, observableHolder);
    }

    public void onPreviousPressed(InputFileLocation[] inputFileLocations, ObservableHolder observableHolder) throws ZkmException, IOException {
        BuildHelperWizard buildHelperWizard = this.wizard;
        SourceArchive[] sourceArchives1 = (SourceArchive[]) null;
        InputFileLocation[] inputFileLocations1 = (InputFileLocation[]) null;
        InputFileLocation[] inputFileLocations2 = (InputFileLocation[]) null;
        InputFileLocation[] inputFileLocations3 = (InputFileLocation[]) null;
        Boolean boolean1 = this.wizard.userPreferences.isOpenNestedArchives();
        Integer integer = 2;
        BuildHelperWizard.accessOnClassesSelected(
                buildHelperWizard,
                inputFileLocations,
                sourceArchives1,
                inputFileLocations1,
                inputFileLocations2,
                inputFileLocations3,
                observableHolder,
                boolean1,
                (Set) null,
                integer
        );
    }

    public void onClassesSelected(
            InputFileLocation[] inputFileLocations,
            SourceArchive[] sourceArchives1,
            InputFileLocation[] inputFileLocations1,
            InputFileLocation[] inputFileLocations2,
            InputFileLocation[] inputFileLocations3,
            ObservableHolder observableHolder,
            Boolean boolean1,
            Set set1
    ) throws ZkmException, IOException {
        BuildHelperWizard.accessOnClassesSelected(
                this.wizard, inputFileLocations, sourceArchives1, inputFileLocations1, inputFileLocations2, inputFileLocations3, observableHolder, boolean1, set1, 1
        );
    }

    public BuildHelperOpenClassesCallback(BuildHelperWizard buildHelperWizard) {
        this.wizard = buildHelperWizard;
    }
}
