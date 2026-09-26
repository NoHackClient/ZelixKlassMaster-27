package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.config.ExclusionWizardSettings;
import com.zelix.klassmaster.config.ObfuscateOptions;
import com.zelix.klassmaster.exceptions.ClassFileLoadException;
import com.zelix.klassmaster.exceptions.ZkmClassNotFoundException;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.rename.RootPackageNode;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.ui.ZkmMainWindow;
import com.zelix.klassmaster.ui.dialog.HelperDialogMarker;
import com.zelix.klassmaster.ui.dialog.MessageBoxDialog;
import com.zelix.klassmaster.ui.dialog.NameExclusionsDialog;
import com.zelix.klassmaster.util.DialogCallback;
import com.zelix.klassmaster.util.SystemEnvironmentConstants;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.List;

public abstract class ExclusionOptionsController implements HelperDialogMarker {
    public ObfuscateOptions obfuscateOptions;
    public ZkmMainWindow mainWindow;
    public RootPackageNode rootPackage;
    public ScriptEnvironment scriptEnvironment;
    public final ExclusionWizardSettings exclusionSettings;

    public abstract void onOptionsResult(int ba) throws ZkmException, IOException;

    public ExclusionOptionsController(ZkmMainWindow zkmMainWindow, RootPackageNode rootPackageNode1, ScriptEnvironment scriptEnvironment1) throws ZkmException, IOException {
        this.mainWindow = zkmMainWindow;
        this.rootPackage = rootPackageNode1;
        this.scriptEnvironment = scriptEnvironment1;
        zkmMainWindow.setEnabled(false);
        zkmMainWindow.setBusy(true);
        this.exclusionSettings = ExclusionWizardSettings.loadFromFile(SystemEnvironmentConstants.USER_DIR);
        this.showNameExclusionsDialog();
    }

    public abstract String getOperationName();

    public void onNameExclusionsResult(int ba, DialogCallback dialogCallback1) {
        if (ba == 1) {
            this.obfuscateOptions = new ObfuscateOptions(SystemEnvironmentConstants.USER_DIR, this.getOptionsFileName());
            this.exclusionSettings.saveToFile(SystemEnvironmentConstants.USER_DIR, "ZKM_EX.ser");
            this.showOptionsDialog("Zelix KlassMaster - " + this.getOperationName() + " Options", this.mainWindow, this.obfuscateOptions, dialogCallback1);
        }
    }

    public final void restoreMainWindow() {
        this.mainWindow.setEnabled(true);
        this.mainWindow.toFront();
        this.mainWindow.setBusy(false);
    }

    public final void showNameExclusionsDialog() throws ZkmException, IOException {
        OptionsDialogCallback optionsDialogCallback1 = new OptionsDialogCallback(this);
        List list1 = null;

        try {
            list1 = this.mainWindow.collectEntryPointClasses();
        } catch (ZkmClassNotFoundException zkmClassNotFoundException) {
            new MessageBoxDialog(
                    this.mainWindow,
                    "Classpath Error",
                    "Error while determining classes with main methods : Class '" + ZkmUtils.slashesToDots(zkmClassNotFoundException.getClassName()) + "' not found."
            );
        } catch (ClassFileLoadException classFileLoadException) {
            new MessageBoxDialog(
                    this.mainWindow, "File Error", "Error while determining classes with main methods : '" + classFileLoadException.getMessage() + "'"
            );
        }

        new NameExclusionsDialog(
                "Zelix KlassMaster - " + this.getOperationName() + " Name Exclusions",
                this.mainWindow,
                this.exclusionSettings,
                this.rootPackage,
                list1,
                this.scriptEnvironment,
                optionsDialogCallback1
        );
    }

    public abstract void showOptionsDialog(String string, ZkmMainWindow zkmMainWindow, ObfuscateOptions obfuscateOptions1, DialogCallback dialogCallback1);

    public abstract String getOptionsFileName();
}
