package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.ui.component.AbstractDocumentListener;

public class TranslateButtonEnabler extends AbstractDocumentListener {
    public final StackTraceTranslateDialog translateDialog;

    @Override
    public void onDocumentChanged(Object object) {
        if (StackTraceTranslateDialog.accessLogFileNameField(this.translateDialog).getText().trim().length() > 0
                && this.translateDialog.traceArea.getText().trim().length() > 0) {
            this.translateDialog.translateBtn.setEnabled(true);
        } else {
            this.translateDialog.translateBtn.setEnabled(false);
        }
    }

    public TranslateButtonEnabler(StackTraceTranslateDialog stackTraceTranslateDialog) {
        this.translateDialog = stackTraceTranslateDialog;
    }
}
