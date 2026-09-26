package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.archive.InputFileLocation;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.CallbackAdapter;
import com.zelix.klassmaster.util.ObservableHolder;

import java.io.IOException;

public class ScriptHelperOpenCallback extends CallbackAdapter {
    public final ScriptHelperWizard wizard;

    public ScriptHelperOpenCallback(ScriptHelperWizard scriptHelperWizard) {
        this.wizard = scriptHelperWizard;
    }

    @Override
    public void onDialogResult(Object object, Object object2, Object object3, Object object4, Object object5, Object object1) throws ZkmException, IOException {
        InputFileLocation[] inputFileLocations = (InputFileLocation[]) object;
        ObservableHolder observableHolder = (ObservableHolder) object1;
        this.onPreviousPressed(inputFileLocations, observableHolder);
    }

    @Override
    public void onDialogResult(Object object, Object object3, Object object4, Object object5, Object object6, Object object1, Object object2, Object object7) throws ZkmException, IOException {
        InputFileLocation[] inputFileLocations = (InputFileLocation[]) object;
        ObservableHolder observableHolder1 = (ObservableHolder) object1;
        Boolean boolean1 = (Boolean) object2;
        ObservableHolder observableHolder = observableHolder1;
        this.onClassesSelected(inputFileLocations, observableHolder, boolean1);
    }

    public void onClassesSelected(InputFileLocation[] inputFileLocations, ObservableHolder observableHolder, Boolean boolean1) {
        ScriptHelperWizard.accessOnClassesSelected(this.wizard, inputFileLocations, observableHolder, boolean1, 1);
    }

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        this.wizard.finishWizard();
    }

    public void onPreviousPressed(InputFileLocation[] inputFileLocations, ObservableHolder observableHolder) {
        ScriptHelperWizard.accessOnClassesSelected(this.wizard, inputFileLocations, observableHolder, this.wizard.userPreferences.isOpenNestedArchives(), 2);
    }
}
