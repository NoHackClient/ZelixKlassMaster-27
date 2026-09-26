package com.zelix.klassmaster.ui.wizard;

import com.zelix.GuiResources;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.config.TrimExcludeSettings;
import com.zelix.klassmaster.config.TrimOptions;
import com.zelix.klassmaster.exceptions.ClassFileLoadException;
import com.zelix.klassmaster.exceptions.ZkmClassNotFoundException;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.ui.OperationStatusCallback;
import com.zelix.klassmaster.ui.ZkmMainWindow;
import com.zelix.klassmaster.ui.dialog.DeleteAttributesDialog;
import com.zelix.klassmaster.ui.dialog.HelperDialogMarker;
import com.zelix.klassmaster.ui.dialog.MessageBoxDialog;
import com.zelix.klassmaster.ui.dialog.TrimExclusionsDialog;
import com.zelix.klassmaster.util.DialogCallback;
import com.zelix.klassmaster.util.SystemEnvironmentConstants;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.List;

public class TrimWizardController implements HelperDialogMarker {
    public final TrimOptions trimOptions = new TrimOptions(SystemEnvironmentConstants.USER_DIR);
    public ZkmMainWindow mainWindow;
    public ClassRepository classRepository;
    public ScriptEnvironment scriptEnvironment;
    public final TrimExcludeSettings trimExcludeSettings;

    public void onTrimExclusionsClosed(int ba, DialogCallback dialogCallback1) {
        if (ba == 1) {
            this.trimExcludeSettings.saveToFile(SystemEnvironmentConstants.USER_DIR, "ZKM_TEX.ser");
            this.showTrimOptionsDialog(this.mainWindow, this.trimOptions, dialogCallback1);
        }
    }

    public void restoreMainWindow() {
        this.mainWindow.setEnabled(true);
        this.mainWindow.toFront();
        this.mainWindow.setBusy(false);
    }

    public void showTrimOptionsDialog(ZkmMainWindow zkmMainWindow, TrimOptions trimOptions1, DialogCallback dialogCallback1) {
        new DeleteAttributesDialog(
                "Zelix KlassMaster - Trim Options", "OK", GuiResources.getTooltipText("PERFORM_TRIM"), zkmMainWindow, trimOptions1, dialogCallback1
        );
    }

    public void onTrimOptionsClosed(TrimOptions trimOptions1, int ba) throws ZkmException, IOException {
        trimOptions1.saveToFile(SystemEnvironmentConstants.USER_DIR);
        if (ba == 1) {
            this.restoreMainWindow();
            this.mainWindow.startTrim(this.trimExcludeSettings.getEntryList(), trimOptions1, this.scriptEnvironment, (OperationStatusCallback) null);
        } else {
            this.showTrimExclusionsDialog();
        }
    }

    public TrimWizardController(ZkmMainWindow zkmMainWindow, ClassRepository classRepository1, ScriptEnvironment scriptEnvironment1) throws ZkmException, IOException {
        this.mainWindow = zkmMainWindow;
        this.classRepository = classRepository1;
        this.scriptEnvironment = scriptEnvironment1;
        zkmMainWindow.setEnabled(false);
        zkmMainWindow.setBusy(true);
        this.trimExcludeSettings = TrimExcludeSettings.loadFromFile(SystemEnvironmentConstants.USER_DIR);
        this.showTrimExclusionsDialog();
    }

    public void showTrimExclusionsDialog() throws ZkmException, IOException {
        TrimWizardMainClassCallback trimWizardMainClassCallback = new TrimWizardMainClassCallback(this);
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

        new TrimExclusionsDialog(
                this.mainWindow, list1, this.trimExcludeSettings, this.classRepository, this.trimOptions, this.scriptEnvironment, trimWizardMainClassCallback
        );
    }
}
