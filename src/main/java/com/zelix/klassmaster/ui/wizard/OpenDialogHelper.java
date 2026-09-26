package com.zelix.klassmaster.ui.wizard;

import com.zelix.GuiResources;
import com.zelix.UserPreferences;
import com.zelix.klassmaster.archive.InputFileLocation;
import com.zelix.klassmaster.archive.SourceArchive;
import com.zelix.klassmaster.classfile.hierarchy.ZkmClasspath;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.OperationStatusCallback;
import com.zelix.klassmaster.ui.ZkmMainWindow;
import com.zelix.klassmaster.ui.dialog.ClasspathDialog;
import com.zelix.klassmaster.ui.dialog.HelperDialogMarker;
import com.zelix.klassmaster.util.ObservableHolder;

import java.io.IOException;
import java.util.Set;

public class OpenDialogHelper implements HelperDialogMarker {
    public static String helperTitle = "Open Dialog Helper";
    public boolean classesChanged;
    public InputFileLocation[] openedLocations;
    public ZkmMainWindow mainWindow;
    public UserPreferences userPreferences;

    public void finishHelper() {
        this.mainWindow.setEnabled(true);
        this.mainWindow.toFront();
        this.mainWindow.setBusy(false);
    }

    public static void accessOnClasspathResult(OpenDialogHelper openDialogHelper, Integer integer) {
        openDialogHelper.onClasspathResult(integer);
    }

    public void showOpenClassesDialog() {
        OpenClassesDialogCallback openClassesDialogCallback = new OpenClassesDialogCallback(this);
        this.mainWindow.setEnabled(false);
        new WizardOpenClassesDialog(
                this.mainWindow,
                helperTitle + " - Open Classes",
                "OK",
                true,
                this.userPreferences.getOpenClassesDirectory(),
                this.userPreferences.isOpenNestedArchives(),
                null,
                openClassesDialogCallback
        );
    }

    public void showInitialOpenClassesDialog() {
        this.showOpenClassesDialog();
    }

    public static void accessOnClassesSelected(
            OpenDialogHelper openDialogHelper,
            InputFileLocation[] inputFileLocations,
            SourceArchive[] sourceArchives1,
            InputFileLocation[] inputFileLocations1,
            InputFileLocation[] inputFileLocations2,
            ObservableHolder observableHolder,
            Boolean boolean1,
            Set set1,
            int ba
    ) throws ZkmException, IOException {
        openDialogHelper.onClassesSelected(inputFileLocations, sourceArchives1, inputFileLocations1, inputFileLocations2, observableHolder, boolean1, set1, ba);
    }

    public OpenDialogHelper(ZkmMainWindow zkmMainWindow, UserPreferences userPreferences1) {
        this.mainWindow = zkmMainWindow;
        this.userPreferences = userPreferences1;
        zkmMainWindow.setEnabled(false);
        zkmMainWindow.setBusy(true);
        this.showClasspathDialog(zkmMainWindow.getClasspath());
    }

    public void onClasspathResult(int ba) {
        if (ba == 1) {
            if (this.openedLocations == null) {
                this.showInitialOpenClassesDialog();
            } else {
                this.showOpenClassesDialog();
            }
        }
    }

    public void onClassesSelected(
            InputFileLocation[] inputFileLocations,
            SourceArchive[] sourceArchives1,
            InputFileLocation[] inputFileLocations1,
            InputFileLocation[] inputFileLocations2,
            ObservableHolder observableHolder,
            Boolean boolean1,
            Set set1,
            int ba
    ) throws ZkmException, IOException {
        if (inputFileLocations != null) {
            this.openedLocations = inputFileLocations;
            this.classesChanged = true;
        }

        String string = (String) observableHolder.getValue();
        if (string != null) {
            this.userPreferences.setOpenClassesDirectory(string);
        }

        if (ba == 1) {
            this.mainWindow
                    .openChosenClasses(
                            this.openedLocations, sourceArchives1, inputFileLocations1, inputFileLocations2, observableHolder, boolean1, set1, (OperationStatusCallback) null
                    );
        } else {
            ZkmClasspath zkmClasspath = this.mainWindow.getClasspath();
            if (zkmClasspath == null) {
                this.showDefaultClasspathDialog();
            } else {
                this.showClasspathDialog(zkmClasspath);
            }
        }
    }

    public void showDefaultClasspathDialog() {
        this.showClasspathDialog(this.mainWindow.getClasspath());
    }

    public static UserPreferences accessGetUserPreferences(OpenDialogHelper openDialogHelper) {
        return openDialogHelper.userPreferences;
    }

    public void showClasspathDialog(ZkmClasspath zkmClasspath) {
        OpenHelperClasspathCallback openHelperClasspathCallback = new OpenHelperClasspathCallback(this);
        new ClasspathDialog(
                this.mainWindow,
                helperTitle + " - Classpath",
                zkmClasspath,
                GuiResources.getTooltipText("CLASSPATH_MSG"),
                "Next",
                GuiResources.getTooltipText("OK_PROCEED"),
                this.userPreferences,
                openHelperClasspathCallback
        );
    }
}
