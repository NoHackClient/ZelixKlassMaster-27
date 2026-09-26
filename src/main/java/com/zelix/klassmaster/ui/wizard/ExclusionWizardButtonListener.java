package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ExclusionWizardButtonListener implements ActionListener {
    public final ExcludeHelperDialog excludeHelperDialog;

    public ExclusionWizardButtonListener(ExcludeHelperDialog excludeHelperDialog1) {
        this.excludeHelperDialog = excludeHelperDialog1;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            Object object = actionEvent.getSource();
            if (object == this.excludeHelperDialog.previousBtn) {
                this.excludeHelperDialog.goPrevious();
            } else if (object == this.excludeHelperDialog.okBtn) {
                this.excludeHelperDialog.acceptParameters();
            } else if (object == this.excludeHelperDialog.cancelBtn) {
                this.excludeHelperDialog.cancelDialog();
            } else if (object == this.excludeHelperDialog.helpBtn) {
                this.excludeHelperDialog.showHelp();
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }
}
