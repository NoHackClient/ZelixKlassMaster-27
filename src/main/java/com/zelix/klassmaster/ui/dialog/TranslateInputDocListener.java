package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.ui.component.AbstractDocumentListener;

public class TranslateInputDocListener extends AbstractDocumentListener {
    public final StackTraceTranslateDialog translateDialog;

    public TranslateInputDocListener(StackTraceTranslateDialog stackTraceTranslateDialog) {
        this.translateDialog = stackTraceTranslateDialog;
    }

    @Override
    public void onDocumentChanged(Object object) {
        this.translateDialog.translatorStale = true;
        if (StackTraceTranslateDialog.accessLogFileNameField(this.translateDialog).getText().trim().length() > 0) {
            this.translateDialog.statusLabel.setText(" ");
        } else {
            this.translateDialog.statusLabel.setText(StackTraceTranslateDialog.selectChangeLogStatus);
        }

        if (StackTraceTranslateDialog.accessLogFileNameField(this.translateDialog).getText().trim().length() > 0
                && this.translateDialog.traceArea.getText().trim().length() > 0) {
            this.translateDialog.translateBtn.setEnabled(true);
        } else {
            this.translateDialog.translateBtn.setEnabled(false);
        }
    }
}
