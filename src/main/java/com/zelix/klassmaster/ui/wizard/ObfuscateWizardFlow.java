package com.zelix.klassmaster.ui.wizard;

import com.zelix.GuiResources;
import com.zelix.klassmaster.config.ObfuscateOptions;
import com.zelix.klassmaster.config.ReferenceObfIncludeStore;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.rename.RootPackageNode;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.ui.OperationStatusCallback;
import com.zelix.klassmaster.ui.ZkmMainWindow;
import com.zelix.klassmaster.ui.dialog.InclusionParametersDialog;
import com.zelix.klassmaster.ui.dialog.ObfuscateOptionsDialog;
import com.zelix.klassmaster.util.DialogCallback;
import com.zelix.klassmaster.util.SystemEnvironmentConstants;

import java.io.IOException;
import java.util.List;

public class ObfuscateWizardFlow extends ExclusionOptionsController {
    public ReferenceObfIncludeStore referenceIncludeStore;

    public void onReferenceInclusionsResult(int ba) throws ZkmException, IOException {
        this.referenceIncludeStore.saveToFile(SystemEnvironmentConstants.USER_DIR, "ZKM_OB_REF.ser");
        if (ba == 1) {
            this.restoreMainWindow();
            ZkmMainWindow zkmMainWindow = super.mainWindow;
            List list1 = super.exclusionSettings.getEntryList();
            List list2;
            ObfuscateOptions obfuscateOptions1;
            if (this.referenceIncludeStore == null) {
                list2 = null;
                obfuscateOptions1 = super.obfuscateOptions;
            } else {
                list2 = this.referenceIncludeStore.getEntryList();
                obfuscateOptions1 = super.obfuscateOptions;
            }

            zkmMainWindow.startObfuscation(list1, list2, obfuscateOptions1, super.scriptEnvironment, (OperationStatusCallback) null);
        } else if (ba == 2 || ba == 5) {
            ObfuscateOptionsCallback obfuscateOptionsCallback = new ObfuscateOptionsCallback(this);
            this.showOptionsDialog(
                    "Zelix KlassMaster - " + this.getOperationName() + " Options", super.mainWindow, super.obfuscateOptions, obfuscateOptionsCallback
            );
        }
    }

    @Override
    public String getOperationName() {
        return "Obfuscate";
    }

    @Override
    public void showOptionsDialog(String string, ZkmMainWindow zkmMainWindow, ObfuscateOptions obfuscateOptions1, DialogCallback dialogCallback1) {
        new ObfuscateOptionsDialog(
                string,
                GuiResources.getTooltipText("PERFORM_OBFUSCATE"),
                zkmMainWindow,
                obfuscateOptions1,
                super.exclusionSettings,
                super.scriptEnvironment,
                dialogCallback1
        );
    }

    public void showReferenceInclusionsDialog(ZkmMainWindow zkmMainWindow, DialogCallback dialogCallback1) throws ZkmException, IOException {
        this.referenceIncludeStore = ReferenceObfIncludeStore.loadFromFile(SystemEnvironmentConstants.USER_DIR);
        new InclusionParametersDialog(
                "Zelix KlassMaster - Reference Obfuscation Inclusions", zkmMainWindow, this.referenceIncludeStore, super.scriptEnvironment, dialogCallback1
        );
    }

    @Override
    public String getOptionsFileName() {
        return "ZKM_SO.ser";
    }

    public static void accessOnReferenceInclusionsResult(ObfuscateWizardFlow obfuscateWizardFlow, Integer integer) throws ZkmException, IOException {
        obfuscateWizardFlow.onReferenceInclusionsResult(integer);
    }

    @Override
    public void onOptionsResult(int ba) throws ZkmException, IOException {
        if (ba == 1) {
            super.obfuscateOptions.saveToFile(SystemEnvironmentConstants.USER_DIR);
            if (super.obfuscateOptions.aj == 1) {
                ReferenceInclusionsCallback referenceInclusionsCallback = new ReferenceInclusionsCallback(this);
                this.showReferenceInclusionsDialog(super.mainWindow, referenceInclusionsCallback);
            } else {
                this.referenceIncludeStore = null;
                this.restoreMainWindow();
                ZkmMainWindow zkmMainWindow = super.mainWindow;
                List list1 = super.exclusionSettings.getEntryList();
                Object object = null;
                ScriptEnvironment scriptEnvironment1 = super.scriptEnvironment;
                ObfuscateOptions obfuscateOptions1 = super.obfuscateOptions;
                zkmMainWindow.startObfuscation(list1, (List) null, obfuscateOptions1, scriptEnvironment1, (OperationStatusCallback) object);
            }
        } else {
            this.showNameExclusionsDialog();
        }
    }

    public ObfuscateWizardFlow(ZkmMainWindow zkmMainWindow, RootPackageNode rootPackageNode1, ScriptEnvironment scriptEnvironment1) throws ZkmException, IOException {
        super(zkmMainWindow, rootPackageNode1, scriptEnvironment1);
    }
}
