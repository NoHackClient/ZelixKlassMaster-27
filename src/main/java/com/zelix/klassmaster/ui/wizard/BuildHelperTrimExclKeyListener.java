package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class BuildHelperTrimExclKeyListener extends KeyAdapter {
    public final TrimExclusionsWizardDialog trimExclusionsDialog;

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        try {
            if (keyEvent.getKeyCode() == 10) {
                Object object = keyEvent.getSource();
                if (keyEvent.getSource() == this.trimExclusionsDialog.previousBtn) {
                    this.trimExclusionsDialog.goToPreviousStep();
                } else if (object == this.trimExclusionsDialog.okBtn) {
                    this.trimExclusionsDialog.acceptParameters();
                } else if (object == this.trimExclusionsDialog.testBtn) {
                    this.trimExclusionsDialog.runTestTrim();
                } else if (object == this.trimExclusionsDialog.skipBtn) {
                    this.trimExclusionsDialog.skipStep();
                } else if (object == this.trimExclusionsDialog.cancelBtn) {
                    this.trimExclusionsDialog.cancelDialog();
                } else if (object == this.trimExclusionsDialog.helpBtn) {
                    this.trimExclusionsDialog.showHelp();
                }
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public BuildHelperTrimExclKeyListener(TrimExclusionsWizardDialog trimExclusionsWizardDialog) {
        this.trimExclusionsDialog = trimExclusionsWizardDialog;
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
    }
}
