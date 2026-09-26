package com.zelix.klassmaster.ui.wizard;

import com.zelix.GuiResources;
import com.zelix.UserPreferences;
import com.zelix.klassmaster.archive.InputFileLocation;
import com.zelix.klassmaster.archive.SourceArchive;
import com.zelix.klassmaster.classfile.hierarchy.ZkmClasspath;
import com.zelix.klassmaster.config.ExclusionWizardSettings;
import com.zelix.klassmaster.config.ObfuscateOptions;
import com.zelix.klassmaster.config.ReferenceObfIncludeStore;
import com.zelix.klassmaster.config.TrimExcludeSettings;
import com.zelix.klassmaster.config.TrimOptions;
import com.zelix.klassmaster.exceptions.ClassFileLoadException;
import com.zelix.klassmaster.exceptions.ZkmClassNotFoundException;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.ui.ZkmMainWindow;
import com.zelix.klassmaster.ui.dialog.InclusionParametersDialog;
import com.zelix.klassmaster.ui.dialog.MessageBoxDialog;
import com.zelix.klassmaster.ui.dialog.NameExclusionsDialog;
import com.zelix.klassmaster.ui.dialog.SaveClassesDialog;
import com.zelix.klassmaster.util.DialogCallback;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.SystemEnvironmentConstants;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class BuildHelperWizard extends AbstractHelperWizard {
    public InputFileLocation[] inputRoots;
    public InputFileLocation[] xmlFileLocations;
    public SourceArchive[] sourceArchives;
    public InputFileLocation[] jadFileLocations;

    public static void accessOnClasspathResult(BuildHelperWizard buildHelperWizard, ZkmClasspath zkmClasspath, Integer integer) {
        buildHelperWizard.onClasspathResult(zkmClasspath, integer);
    }

    public void showClasspathStep() {
        if (super.classpath == null) {
            this.showDefaultClasspathDialog();
        } else {
            this.showClasspathDialog(super.classpath);
        }
    }

    public void showOpenClassesDialog(InputFileLocation[] inputFileLocations) {
        BuildHelperOpenClassesCallback buildHelperOpenClassesCallback = new BuildHelperOpenClassesCallback(this);
        super.mainWindow.setEnabled(false);
        new WizardOpenClassesDialog(
                super.mainWindow,
                "Build Helper - Open Classes",
                "Next",
                true,
                super.userPreferences.getOpenClassesDirectory(),
                super.userPreferences.isOpenNestedArchives(),
                inputFileLocations,
                buildHelperOpenClassesCallback
        );
    }

    @Override
    public void showIntroduction() {
        BuildHelperIntroCallback buildHelperIntroCallback = new BuildHelperIntroCallback(this);
        new ProceedConfirmDialog(super.mainWindow, "Build Helper", "Next", "Cancel", "002", "012", 500, 400, buildHelperIntroCallback);
    }

    public void onClassesSelected(
            InputFileLocation[] inputFileLocations,
            SourceArchive[] sourceArchives1,
            InputFileLocation[] inputFileLocations1,
            InputFileLocation[] inputFileLocations2,
            InputFileLocation[] inputFileLocations3,
            ObservableHolder observableHolder,
            Boolean boolean1,
            Set set1,
            int ba
    ) throws ZkmException, IOException {
        if (inputFileLocations != null) {
            super.openedLocations = inputFileLocations;
            super.classesChanged = true;
        }

        if (ba == 1) {
            this.sourceArchives = sourceArchives1;
            this.jadFileLocations = inputFileLocations1;
            this.xmlFileLocations = inputFileLocations2;
            this.inputRoots = inputFileLocations3;
            String string = (String) observableHolder.getValue();
            Boolean boolean2 = boolean1;
            UserPreferences userPreferences1;
            if (string != null) {
                super.userPreferences.setOpenClassesDirectory(string);
                userPreferences1 = super.userPreferences;
            } else {
                userPreferences1 = super.userPreferences;
            }

            userPreferences1.savePreferences();
            BuildHelperOpenCallback buildHelperOpenCallback = new BuildHelperOpenCallback(this);
            super.mainWindow
                    .openChosenClasses(
                            super.openedLocations,
                            this.sourceArchives,
                            this.jadFileLocations,
                            this.xmlFileLocations,
                            observableHolder,
                            boolean2,
                            set1,
                            buildHelperOpenCallback
                    );
        } else if (super.classpath == null) {
            this.showDefaultClasspathDialog();
        } else {
            this.showClasspathDialog(super.classpath);
        }
    }

    public void showTrimOptionsDialog(TrimOptions trimOptions1) {
        BuildHelperTrimOptionsCallback buildHelperTrimOptionsCallback = new BuildHelperTrimOptionsCallback(this);
        new TrimOptionsWizardDialog(
                "Build Helper - Trim Options", GuiResources.getTooltipText("PERFORM_TRIM"), super.mainWindow, trimOptions1, buildHelperTrimOptionsCallback
        );
    }

    public void onScriptDialogResult(int ba) {
        if (ba == 2) {
            this.showScriptPrompt();
        }
    }

    public static void accessOnOpeningInfoResult(BuildHelperWizard buildHelperWizard, Integer integer) throws ZkmException, IOException {
        buildHelperWizard.onOpeningInfoResult(integer);
    }

    public void showObfuscateOptionsDialog(ObfuscateOptions obfuscateOptions1) {
        BuildHelperObfuscateCallback buildHelperObfuscateCallback = new BuildHelperObfuscateCallback(this);
        new BuildHelperObfuscateOptionsDialog(
                "Build Helper - Obfuscate Options",
                GuiResources.getTooltipText("PERFORM_OBFUSCATE"),
                super.mainWindow,
                obfuscateOptions1,
                super.exclusionSettings,
                super.scriptEnvironment,
                buildHelperObfuscateCallback
        );
    }

    public static void accessShowTrimOptionsDialog(BuildHelperWizard buildHelperWizard, TrimOptions trimOptions1) {
        buildHelperWizard.showTrimOptionsDialog(trimOptions1);
    }

    @Override
    public void showSaveDialog(String string) {
        BuildHelperSaveDialogCallback buildHelperSaveDialogCallback = new BuildHelperSaveDialogCallback(this);
        new SaveClassesDialog(
                super.mainWindow, "Build Helper - Save Classes", "Next", GuiResources.getTooltipText("SAVE_CLASSES"), string, buildHelperSaveDialogCallback
        );
    }

    public static void accessOnTrimOptionsResult(BuildHelperWizard buildHelperWizard, TrimOptions trimOptions1, Integer integer) throws ZkmException, IOException {
        buildHelperWizard.onTrimOptionsResult(trimOptions1, integer);
    }

    public static void accessOnScriptDialogResult(BuildHelperWizard buildHelperWizard, Integer integer) {
        buildHelperWizard.onScriptDialogResult(integer);
    }

    public static void accessOnObfuscateOptionsResult(BuildHelperWizard buildHelperWizard, ObfuscateOptions obfuscateOptions1, Integer integer) throws ZkmException, IOException {
        buildHelperWizard.onObfuscateOptionsResult(obfuscateOptions1, integer);
    }

    public void onTrimExclusionsResult(int ba) {
        if (ba == 1) {
            super.trimExcludeSettings.saveToFile(SystemEnvironmentConstants.USER_DIR, "ZKM_TEX.ser");
            super.trimExclusionsSkipped = false;
            if (super.trimOptions == null) {
                this.showCurrentTrimOptions();
            } else {
                this.showTrimOptionsDialog(super.trimOptions);
            }
        } else if (ba == 2) {
            this.showOpeningInfo();
        } else if (ba == 4) {
            super.trimExclusionsSkipped = true;
            if (super.trimOptions == null) {
                this.showCurrentTrimOptions();
            } else {
                this.showTrimOptionsDialog(super.trimOptions);
            }
        }
    }

    public void onObfuscateOptionsResult(ObfuscateOptions obfuscateOptions1, int ba) throws ZkmException, IOException {
        super.obfuscateOptions = obfuscateOptions1;
        if (ba == 1) {
            super.obfuscateOptions.saveToFile(SystemEnvironmentConstants.USER_DIR);
            if (super.obfuscateOptions.aj == 1) {
                BuildHelperStepCallback buildHelperStepCallback = new BuildHelperStepCallback(this);
                this.showReferenceInclusionsDialog(super.mainWindow, buildHelperStepCallback);
            } else {
                super.referenceIncludeStore = null;
                BuildHelperSaveStepCallback buildHelperSaveStepCallback1 = new BuildHelperSaveStepCallback(this);
                super.mainWindow.setBusy(true);
                super.mainWindow.setEnabled(true);
                super.mainWindow.toFront();
                ZkmMainWindow zkmMainWindow = super.mainWindow;
                List list1 = super.exclusionSettings.getEntryList();
                BuildHelperSaveStepCallback buildHelperSaveStepCallback = buildHelperSaveStepCallback1;
                ScriptEnvironment scriptEnvironment1 = super.scriptEnvironment;
                ObfuscateOptions obfuscateOptions2 = super.obfuscateOptions;
                zkmMainWindow.startObfuscation(list1, (List) null, obfuscateOptions2, scriptEnvironment1, buildHelperSaveStepCallback);
            }
        } else if (ba == 2) {
            this.showNameExclusionsDialog();
        }
    }

    public void onTrimOptionsResult(TrimOptions trimOptions1, int ba) throws ZkmException, IOException {
        super.trimOptions = trimOptions1;
        if (ba == 1) {
            super.trimOptions.saveToFile(SystemEnvironmentConstants.USER_DIR);
            super.trimSkipped = false;
            BuildHelperTrimCallback buildHelperTrimCallback = new BuildHelperTrimCallback(this);
            super.mainWindow.setBusy(true);
            super.mainWindow.setEnabled(true);
            super.mainWindow.toFront();
            if (super.trimExclusionsSkipped) {
                super.mainWindow.startTrim(new Vector(), super.trimOptions, super.scriptEnvironment, buildHelperTrimCallback);
            } else {
                super.mainWindow.startTrim(super.trimExcludeSettings.getEntryList(), super.trimOptions, super.scriptEnvironment, buildHelperTrimCallback);
            }
        } else if (ba == 2) {
            this.showTrimExclusionsDialog();
        } else if (ba == 4) {
            super.trimSkipped = true;
            this.showNameExclusionsDialog();
        }
    }

    public void showTrimExclusionsDialog() throws ZkmException, IOException {
        if (super.trimExcludeSettings == null) {
            super.trimExcludeSettings = TrimExcludeSettings.loadFromFile(SystemEnvironmentConstants.USER_DIR);
        }

        BuildHelperTrimExcludeCallback buildHelperTrimExcludeCallback = new BuildHelperTrimExcludeCallback(this);
        List list1 = null;

        try {
            list1 = super.mainWindow.collectEntryPointClasses();
        } catch (ZkmClassNotFoundException zkmClassNotFoundException) {
            new MessageBoxDialog(
                    super.mainWindow,
                    "Classpath Error",
                    "Error while determining classes with main methods : Class '" + ZkmUtils.slashesToDots(zkmClassNotFoundException.getClassName()) + "' not found."
            );
        } catch (ClassFileLoadException classFileLoadException) {
            new MessageBoxDialog(
                    super.mainWindow, "File Error", "Error while determining classes with main methods : '" + classFileLoadException.getMessage() + "'"
            );
        }

        new TrimExclusionsWizardDialog(
                super.mainWindow,
                list1,
                super.trimExcludeSettings,
                super.mainWindow.getClassRepository(),
                super.trimOptions,
                super.scriptEnvironment,
                buildHelperTrimExcludeCallback
        );
    }

    public void showCurrentTrimOptions() {
        this.showTrimOptionsDialog(super.trimOptions);
    }

    public static void accessOnTrimExclusionsResult(BuildHelperWizard buildHelperWizard, Integer integer) {
        buildHelperWizard.onTrimExclusionsResult(integer);
    }

    public static void accessShowNameExclusionsDialog(BuildHelperWizard buildHelperWizard) throws ZkmException, IOException {
        buildHelperWizard.showNameExclusionsDialog();
    }

    public static void accessShowScriptDialog(BuildHelperWizard buildHelperWizard, String string) {
        buildHelperWizard.showScriptDialog(string);
    }

    @Override
    public void finishWizard() {
        super.mainWindow.setEnabled(true);
        super.mainWindow.toFront();
        super.mainWindow.setBusy(false);
    }

    public void onSaveDirectoryResult(File file1, int ba) {
        super.saveDirectory = file1.getAbsolutePath();
        if (ba == 1) {
            if (super.saveDirectory != null) {
                super.userPreferences.setLastSavePath(super.saveDirectory);
            }

            super.userPreferences.savePreferences();
            BuildHelperSaveCallback buildHelperSaveCallback = new BuildHelperSaveCallback(this);
            super.mainWindow.saveClasses(file1, buildHelperSaveCallback);
        } else if (ba == 2) {
            this.showObfuscateOptionsDialog(super.obfuscateOptions);
        }
    }

    @Override
    public void onReferenceInclusionsResult(int ba) throws ZkmException, IOException {
        super.referenceIncludeStore.saveToFile(SystemEnvironmentConstants.USER_DIR, "ZKM_OB_REF.ser");
        if (ba == 1) {
            BuildHelperContinueCallback buildHelperContinueCallback = new BuildHelperContinueCallback(this);
            super.mainWindow.setBusy(true);
            super.mainWindow.setEnabled(true);
            super.mainWindow.toFront();
            super.mainWindow
                    .startObfuscation(
                            super.exclusionSettings.getEntryList(),
                            super.referenceIncludeStore.getEntryList(),
                            super.obfuscateOptions,
                            super.scriptEnvironment,
                            buildHelperContinueCallback
                    );
        } else {
            BuildHelperWizard buildHelperWizard1;
            ObfuscateOptions obfuscateOptions1;
            if (ba != 2) {
                if (ba != 5) {
                    return;
                }

                buildHelperWizard1 = this;
                obfuscateOptions1 = super.obfuscateOptions;
            } else {
                buildHelperWizard1 = this;
                obfuscateOptions1 = super.obfuscateOptions;
            }

            buildHelperWizard1.showObfuscateOptionsDialog(obfuscateOptions1);
        }
    }

    public void showScriptPrompt() {
        BuildHelperScriptCallback buildHelperScriptCallback = new BuildHelperScriptCallback(this);
        new ProceedConfirmDialog(super.mainWindow, "Build Helper", "ZKM Script", "Exit", "003", "013", 300, 250, buildHelperScriptCallback);
    }

    public void onNameExclusionsResult(int ba) {
        if (ba == 1) {
            super.exclusionSettings.saveToFile(SystemEnvironmentConstants.USER_DIR, "ZKM_EX.ser");
            if (super.obfuscateOptions == null) {
                this.showDefaultObfuscateOptions();
            } else {
                this.showObfuscateOptionsDialog(super.obfuscateOptions);
            }
        } else if (ba == 2) {
            this.showCurrentTrimOptions();
        }
    }

    public void showNameExclusionsDialog() throws ZkmException, IOException {
        if (super.exclusionSettings == null) {
            super.exclusionSettings = ExclusionWizardSettings.loadFromFile(SystemEnvironmentConstants.USER_DIR);
        }

        NameExclusionsStepCallback nameExclusionsStepCallback = new NameExclusionsStepCallback(this);
        List list1 = null;

        try {
            list1 = super.mainWindow.collectEntryPointClasses();
        } catch (ZkmClassNotFoundException zkmClassNotFoundException) {
            new MessageBoxDialog(
                    super.mainWindow,
                    "Classpath Error",
                    "Error while determining classes with main methods : Class '" + ZkmUtils.slashesToDots(zkmClassNotFoundException.getClassName()) + "' not found."
            );
        } catch (ClassFileLoadException classFileLoadException) {
            new MessageBoxDialog(
                    super.mainWindow, "File Error", "Error while determining classes with main methods : '" + classFileLoadException.getMessage() + "'"
            );
        }

        new NameExclusionsDialog(
                "Build Helper - Obfuscate Name Exclusions",
                super.mainWindow,
                super.exclusionSettings,
                super.mainWindow.getRootPackage(),
                list1,
                super.scriptEnvironment,
                nameExclusionsStepCallback
        );
    }

    @Override
    public void showDefaultSaveDialog() {
        this.showSaveDialog(super.userPreferences.getLastSavePath());
    }

    public static void accessShowClasspathStep(BuildHelperWizard buildHelperWizard) {
        buildHelperWizard.showClasspathStep();
    }

    public BuildHelperWizard(ZkmMainWindow zkmMainWindow, ScriptEnvironment scriptEnvironment1, UserPreferences userPreferences1) {
        super(zkmMainWindow, scriptEnvironment1, userPreferences1);
    }

    public void showReferenceInclusionsDialog(ZkmMainWindow zkmMainWindow, DialogCallback dialogCallback1) throws ZkmException, IOException {
        super.referenceIncludeStore = ReferenceObfIncludeStore.loadFromFile(SystemEnvironmentConstants.USER_DIR);
        new InclusionParametersDialog(
                "Build Helper - Reference Obfuscation Inclusions", zkmMainWindow, super.referenceIncludeStore, super.scriptEnvironment, dialogCallback1
        );
    }

    public void showClasspathDialog(ZkmClasspath zkmClasspath) {
        BuildHelperClasspathCallback buildHelperClasspathCallback = new BuildHelperClasspathCallback(this);
        new ClasspathStepDialog(
                super.mainWindow,
                "Build Helper - Classpath",
                zkmClasspath,
                GuiResources.getTooltipText("CLASSPATH_MSG"),
                GuiResources.getTooltipText("SET_CLASSPATH"),
                super.userPreferences,
                buildHelperClasspathCallback
        );
    }

    public static void accessShowOpenClassesDialog(BuildHelperWizard buildHelperWizard, InputFileLocation[] inputFileLocations) {
        buildHelperWizard.showOpenClassesDialog(inputFileLocations);
    }

    public void onClasspathResult(ZkmClasspath zkmClasspath, int ba) {
        super.classpath = zkmClasspath;
        if (ba == 1) {
            if (this.inputRoots == null) {
                this.showEmptyOpenClassesDialog();
            } else {
                this.showOpenClassesDialog(this.inputRoots);
            }
        } else {
            this.showIntroduction();
        }
    }

    public void onOpeningInfoResult(int ba) throws ZkmException, IOException {
        if (ba == 2) {
            if (this.inputRoots == null) {
                this.showEmptyOpenClassesDialog();
            } else {
                this.showOpenClassesDialog(this.inputRoots);
            }
        } else if (ba == 1) {
            this.showTrimExclusionsDialog();
        }
    }

    public static void accessOnClassesSelected(
            BuildHelperWizard buildHelperWizard,
            InputFileLocation[] inputFileLocations,
            SourceArchive[] sourceArchives1,
            InputFileLocation[] inputFileLocations1,
            InputFileLocation[] inputFileLocations2,
            InputFileLocation[] inputFileLocations3,
            ObservableHolder observableHolder,
            Boolean boolean1,
            Set set1,
            int ba
    ) throws ZkmException, IOException {
        buildHelperWizard.onClassesSelected(
                inputFileLocations, sourceArchives1, inputFileLocations1, inputFileLocations2, inputFileLocations3, observableHolder, boolean1, set1, ba
        );
    }

    public void showDefaultClasspathDialog() {
        this.showClasspathDialog(super.mainWindow.getClasspath());
    }

    public void showOpeningInfo() {
        BuildHelperOpeningCallback buildHelperOpeningCallback = new BuildHelperOpeningCallback(this);
        new HelperInfoDialog(super.mainWindow, GuiResources.getTooltipText("OK_PROCEED"), buildHelperOpeningCallback);
    }

    public static void accessOnNameExclusionsResult(BuildHelperWizard buildHelperWizard, Integer integer) {
        buildHelperWizard.onNameExclusionsResult(integer);
    }

    public void showScriptDialog(String string) {
        BuildHelperCallback buildHelperCallback = new BuildHelperCallback(this);
        new BuildHelperScriptDialog(super.mainWindow, "Build Helper - Equivalent ZKM Script", string, super.userPreferences, buildHelperCallback);
    }

    public void showEmptyOpenClassesDialog() {
        this.showOpenClassesDialog((InputFileLocation[]) null);
    }

    public static void accessShowScriptPrompt(BuildHelperWizard buildHelperWizard) {
        buildHelperWizard.showScriptPrompt();
    }

    public static void accessOnSaveDirectoryResult(BuildHelperWizard buildHelperWizard, File file1, Integer integer) {
        buildHelperWizard.onSaveDirectoryResult(file1, integer);
    }

    public static void accessShowOpeningInfo(BuildHelperWizard buildHelperWizard) {
        buildHelperWizard.showOpeningInfo();
    }

    @Override
    public void showDefaultObfuscateOptions() {
        this.showObfuscateOptionsDialog(new ObfuscateOptions(SystemEnvironmentConstants.USER_DIR, "ZKM_SO.ser"));
    }
}
