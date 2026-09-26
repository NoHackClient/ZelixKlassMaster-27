package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.ui.component.AbstractDocumentListener;

public class TranslateInputListener extends AbstractDocumentListener {
    public final ProGuardTranslateDialog translateDialog;

    @Override
    public void onDocumentChanged(Object object) {
        if (this.translateDialog.proGuardArea.getText().trim().length() > 0) {
            this.translateDialog.translateBtn.setEnabled(true);
        } else {
            this.translateDialog.translateBtn.setEnabled(false);
        }
    }

    public TranslateInputListener(ProGuardTranslateDialog proGuardTranslateDialog) {
        this.translateDialog = proGuardTranslateDialog;
    }
}
