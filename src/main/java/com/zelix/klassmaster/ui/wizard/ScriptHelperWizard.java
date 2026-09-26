package com.zelix.klassmaster.ui.wizard;

import com.zelix.GuiResources;
import com.zelix.UserPreferences;
import com.zelix.klassmaster.archive.InputFileLocation;
import com.zelix.klassmaster.classfile.hierarchy.ClassFileSetIndex;
import com.zelix.klassmaster.classfile.hierarchy.ZkmClasspath;
import com.zelix.klassmaster.config.ExclusionWizardSettings;
import com.zelix.klassmaster.config.ObfuscateOptions;
import com.zelix.klassmaster.config.ReferenceObfIncludeStore;
import com.zelix.klassmaster.config.TrimExcludeSettings;
import com.zelix.klassmaster.config.TrimOptions;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.obfuscator.rename.RootPackageNode;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.ui.ZkmMainWindow;
import com.zelix.klassmaster.ui.dialog.InclusionParametersDialog;
import com.zelix.klassmaster.util.DialogCallback;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.SystemEnvironmentConstants;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

public class ScriptHelperWizard extends AbstractHelperWizard {
    public Vector mainClassEntries;
    public ClassFileSetIndex classFileIndex;
    public RootPackageNode rootPackage;

    public void showCurrentTrimOptions() {
        this.showTrimOptionsDialog(super.trimOptions);
    }

    public void startMainClassSearch() {
        if (super.trimExcludeSettings == null) {
            super.trimExcludeSettings = TrimExcludeSettings.loadFromFile(SystemEnvironmentConstants.USER_DIR);
        }

        ScriptTrimExcludeCallback scriptTrimExcludeCallback = new ScriptTrimExcludeCallback(this);
        ScriptHelperMainClassesCallback scriptHelperMainClassesCallback = new ScriptHelperMainClassesCallback(this);
        MainClassSearchTask mainClassSearchTask = new MainClassSearchTask(this, scriptHelperMainClassesCallback, scriptTrimExcludeCallback);
        new Thread(mainClassSearchTask).start();
    }

    public static void accessShowTrimExcludeDialog(ScriptHelperWizard scriptHelperWizard, DialogCallback dialogCallback1) throws ZkmException, IOException {
        scriptHelperWizard.showTrimExcludeDialog(dialogCallback1);
    }

    public static void accessOnObfuscateOptionsResult(ScriptHelperWizard scriptHelperWizard, ObfuscateOptions obfuscateOptions1, Integer integer) throws ZkmException, IOException {
        scriptHelperWizard.onObfuscateOptionsResult(obfuscateOptions1, integer);
    }

    public void showScriptDialog(String string) {
        ScriptHelperStepCallback scriptHelperStepCallback = new ScriptHelperStepCallback(this);
        new BuildHelperScriptDialog(super.mainWindow, "ZKM Script Helper", string, super.userPreferences, scriptHelperStepCallback);
    }

    public static void accessOnExcludeResult(ScriptHelperWizard scriptHelperWizard, Integer integer) {
        scriptHelperWizard.onExcludeResult(integer);
    }

    public void showObfuscateOptionsDialog(ObfuscateOptions obfuscateOptions1) {
        ScriptHelperObfuscateCallback scriptHelperObfuscateCallback = new ScriptHelperObfuscateCallback(this);
        new ScriptHelperObfuscateDialog(
                GuiResources.getTooltipText("OK_PROCEED"),
                super.mainWindow,
                obfuscateOptions1,
                super.exclusionSettings,
                super.scriptEnvironment,
                scriptHelperObfuscateCallback
        );
    }

    @Override
    public void showIntroduction() {
        ScriptHelperIntroCallback scriptHelperIntroCallback = new ScriptHelperIntroCallback(this);
        new ProceedConfirmDialog(super.mainWindow, "ZKM Script Helper", "Next", "Cancel", "001", "011", 500, 400, scriptHelperIntroCallback);
    }

    public void onTrimOptionsResult(TrimOptions trimOptions1, int ba) throws ZkmException, IOException {
        super.trimOptions = trimOptions1;
        if (ba == 1) {
            super.trimOptions.saveToFile(SystemEnvironmentConstants.USER_DIR);
            super.trimSkipped = false;
            this.showExcludeDialog();
        } else if (ba == 2) {
            this.startMainClassSearch();
        } else if (ba == 4) {
            super.trimSkipped = true;
            this.showExcludeDialog();
        }
    }

    public static void accessOnTrimOptionsResult(ScriptHelperWizard scriptHelperWizard, TrimOptions trimOptions1, Integer integer) throws ZkmException, IOException {
        scriptHelperWizard.onTrimOptionsResult(trimOptions1, integer);
    }

    @Override
    public void showSaveDialog(String string) {
        SaveAllStepCallback saveAllStepCallback = new SaveAllStepCallback(this);
        new SaveAllStatementDialog(super.mainWindow, GuiResources.getTooltipText("OK_PROCEED"), string, saveAllStepCallback);
    }

    public void onSaveAllResult(File file1, Integer integer) throws ZkmException, IOException {
        if (file1 != null) {
            super.saveDirectory = file1.getAbsolutePath();
        }

        int ba = integer;
        if (ba == 1) {
            if (super.saveDirectory != null) {
                super.userPreferences.setLastSavePath(super.saveDirectory);
            }

            super.userPreferences.savePreferences();
            this.showScriptDialog(this.buildEquivalentScript("ZKM Script Helper", super.openedLocations));
        } else if (ba == 2) {
            if (super.obfuscateOptions.aj == 1) {
                this.showReferenceInclusionsDialog();
            } else {
                this.showObfuscateOptionsDialog(super.obfuscateOptions);
            }
        }
    }

    public void showEmptyOpenClassesDialog() {
        this.showOpenClassesDialog((InputFileLocation[]) null);
    }

    public void showExcludeDialog() throws ZkmException, IOException {
        if (super.exclusionSettings == null) {
            super.exclusionSettings = ExclusionWizardSettings.loadFromFile(SystemEnvironmentConstants.USER_DIR);
        }

        ScriptHelperExcludeCallback scriptHelperExcludeCallback = new ScriptHelperExcludeCallback(this);
        new ExcludeHelperDialog(
                super.mainWindow, super.exclusionSettings, this.rootPackage, this.mainClassEntries, super.scriptEnvironment, scriptHelperExcludeCallback
        );
    }

    public ScriptHelperWizard(ZkmMainWindow zkmMainWindow, ScriptEnvironment scriptEnvironment1, UserPreferences userPreferences1) {
        super(zkmMainWindow, scriptEnvironment1, userPreferences1);
    }

    public void onTrimExcludeResult(int ba) {
        if (ba == 1) {
            super.trimExcludeSettings.saveToFile(SystemEnvironmentConstants.USER_DIR, "ZKM_TEX.ser");
            super.trimExclusionsSkipped = false;
            if (super.trimOptions == null) {
                this.showCurrentTrimOptions();
            } else {
                this.showTrimOptionsDialog(super.trimOptions);
            }
        } else if (ba == 2) {
            if (super.openedLocations == null) {
                this.showEmptyOpenClassesDialog();
            } else {
                this.showOpenClassesDialog(super.openedLocations);
            }
        } else if (ba == 4) {
            super.trimExclusionsSkipped = true;
            if (super.trimOptions == null) {
                this.showCurrentTrimOptions();
            } else {
                this.showTrimOptionsDialog(super.trimOptions);
            }
        }
    }

    public void showClasspathStep() {
        if (super.classpath == null) {
            this.showDefaultClasspathDialog();
        } else {
            this.showClasspathDialog(super.classpath);
        }
    }

    public void showClasspathDialog(ZkmClasspath zkmClasspath) {
        ScriptHelperCallback scriptHelperCallback = new ScriptHelperCallback(this);
        new ClasspathStepDialog(
                super.mainWindow,
                "ZKM Script Helper - \"classpath\" Statement",
                zkmClasspath,
                GuiResources.getTooltipText("CLASSPATH_MSG"),
                GuiResources.getTooltipText("OK_PROCEED"),
                super.userPreferences,
                scriptHelperCallback
        );
    }

    @Override
    public void finishWizard() {
        super.mainWindow.setEnabled(true);
        super.mainWindow.toFront();
        super.mainWindow.setBusy(false);
    }

    public void showTrimExcludeDialog(DialogCallback dialogCallback1) throws ZkmException, IOException {
        new TrimExcludeHelperDialog(
                super.mainWindow,
                this.mainClassEntries,
                super.trimExcludeSettings,
                super.mainWindow.getClassRepository(),
                super.trimOptions,
                super.scriptEnvironment,
                dialogCallback1
        );
    }

    public static void accessOnScriptDialogResult(ScriptHelperWizard scriptHelperWizard, Integer integer) {
        scriptHelperWizard.onScriptDialogResult(integer);
    }

    public static RootPackageNode accessSetRootPackage(ScriptHelperWizard scriptHelperWizard, RootPackageNode rootPackageNode1) {
        return scriptHelperWizard.rootPackage = rootPackageNode1;
    }

    public static void accessShowClasspathStep(ScriptHelperWizard scriptHelperWizard) {
        scriptHelperWizard.showClasspathStep();
    }

    public void onExcludeResult(int ba) {
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

    public void onScriptDialogResult(int ba) {
        if (ba == 2) {
            this.showSaveDialog(super.saveDirectory);
        }
    }

    public final void showReferenceInclusionsDialog() throws ZkmException, IOException {
        ScriptHelperRefIncludeCallback scriptHelperRefIncludeCallback = new ScriptHelperRefIncludeCallback(this);
        super.referenceIncludeStore = ReferenceObfIncludeStore.loadFromFile(SystemEnvironmentConstants.USER_DIR);
        new InclusionParametersDialog(
                "ZKM Script Helper - \"obfuscateReferencesInclude\" Statement",
                super.mainWindow,
                super.referenceIncludeStore,
                super.scriptEnvironment,
                scriptHelperRefIncludeCallback
        );
    }

    public void onClasspathResult(ZkmClasspath zkmClasspath, int ba) {
        super.classpath = zkmClasspath;
        if (ba == 1) {
            if (super.openedLocations == null) {
                this.showEmptyOpenClassesDialog();
            } else {
                this.showOpenClassesDialog(super.openedLocations);
            }
        } else {
            this.showIntroduction();
        }
    }

    public static Vector accessSetMainClassEntries(ScriptHelperWizard scriptHelperWizard, Vector vector) {
        return scriptHelperWizard.mainClassEntries = vector;
    }

    public static void accessOnClasspathResult(ScriptHelperWizard scriptHelperWizard, ZkmClasspath zkmClasspath, Integer integer) {
        scriptHelperWizard.onClasspathResult(zkmClasspath, integer);
    }

    public static void accessOnSaveAllResult(ScriptHelperWizard scriptHelperWizard, File file1, Integer integer) throws ZkmException, IOException {
        scriptHelperWizard.onSaveAllResult(file1, integer);
    }

    public void showOpenClassesDialog(InputFileLocation[] inputFileLocations) {
        ScriptHelperOpenCallback scriptHelperOpenCallback = new ScriptHelperOpenCallback(this);
        super.mainWindow.setEnabled(false);
        new WizardOpenClassesDialog(
                super.mainWindow,
                "ZKM Script Helper - \"open\" Statement",
                "Next",
                false,
                super.userPreferences.getOpenClassesDirectory(),
                super.userPreferences.isOpenNestedArchives(),
                inputFileLocations,
                scriptHelperOpenCallback
        );
    }

    @Override
    public void showDefaultObfuscateOptions() {
        this.showObfuscateOptionsDialog(new ObfuscateOptions(SystemEnvironmentConstants.USER_DIR, "ZKM_SO.ser"));
    }

    public void onObfuscateOptionsResult(ObfuscateOptions obfuscateOptions1, int ba) throws ZkmException, IOException {
        super.obfuscateOptions = obfuscateOptions1;
        if (ba == 1) {
            super.obfuscateOptions.saveToFile(SystemEnvironmentConstants.USER_DIR);
            if (super.obfuscateOptions.aj == 1) {
                this.showReferenceInclusionsDialog();
            } else {
                super.referenceIncludeStore = null;
                if (super.saveDirectory == null) {
                    this.showDefaultSaveDialog();
                } else {
                    this.showSaveDialog(super.saveDirectory);
                }
            }
        } else if (ba == 2) {
            this.showExcludeDialog();
        }
    }

    public ClassFileSetIndex getClassFileIndex(InputFileLocation[] inputFileLocations) throws ZkmProcessingException {
        if (super.classesChanged) {
            File[] files = new File[inputFileLocations.length];

            for (int i = 0; i < inputFileLocations.length; i++) {
                files[i] = inputFileLocations[i].getFile();
            }

            this.classFileIndex = new ClassFileSetIndex(files);
            super.classesChanged = false;
        }

        return this.classFileIndex;
    }

    public void onClassesSelected(InputFileLocation[] inputFileLocations, ObservableHolder observableHolder, Boolean boolean1, int ba) {
        if (inputFileLocations != null) {
            super.openedLocations = inputFileLocations;
            super.classesChanged = true;
        }

        String string = (String) observableHolder.getValue();
        if (string != null) {
            super.userPreferences.setOpenClassesDirectory(string);
        }

        if (ba == 1) {
            Boolean boolean2 = boolean1;
            super.userPreferences.setOpenNestedArchives(boolean2);
            super.userPreferences.savePreferences();
            this.startMainClassSearch();
        } else if (super.classpath == null) {
            this.showDefaultClasspathDialog();
        } else {
            this.showClasspathDialog(super.classpath);
        }
    }

    public void showDefaultClasspathDialog() {
        this.showClasspathDialog(super.mainWindow.getClasspath());
    }

    public static void accessOnClassesSelected(
            ScriptHelperWizard scriptHelperWizard, InputFileLocation[] inputFileLocations, ObservableHolder observableHolder, Boolean boolean1, int ba
    ) {
        scriptHelperWizard.onClassesSelected(inputFileLocations, observableHolder, boolean1, ba);
    }

    public static void accessOnTrimExcludeResult(ScriptHelperWizard scriptHelperWizard, Integer integer) {
        scriptHelperWizard.onTrimExcludeResult(integer);
    }

    public void showTrimOptionsDialog(TrimOptions trimOptions1) {
        ScriptHelperTrimCallback scriptHelperTrimCallback = new ScriptHelperTrimCallback(this);
        new TrimOptionsWizardDialog(
                "ZKM Script Helper - \"trim\" Statement", GuiResources.getTooltipText("OK_PROCEED"), super.mainWindow, trimOptions1, scriptHelperTrimCallback
        );
    }

    @Override
    public void showDefaultSaveDialog() {
        this.showSaveDialog(super.userPreferences.getLastSavePath());
    }
}
