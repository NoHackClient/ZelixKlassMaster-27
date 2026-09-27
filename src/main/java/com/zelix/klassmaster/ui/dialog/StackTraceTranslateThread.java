package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.changelog.StackTraceTranslateException;
import com.zelix.klassmaster.util.ZkmUtils;

import javax.swing.SwingUtilities;

public class StackTraceTranslateThread extends Thread {
    public String translatedTrace;
    public final StackTraceTranslateDialog translateDialog;
    public Runnable doneRunnable;

    @Override
    public void run() {
        try {
            Runnable runnable;
            label16:
            {
                try {
                    this.translatedTrace = this.translateDialog
                            .translator
                            .translate(this.translateDialog.traceArea.getText(), this.translateDialog.useBytecodeChk.isSelected());
                } catch (StackTraceTranslateException stackTraceTranslateException) {
                    this.translatedTrace = stackTraceTranslateException.getMessage();
                    runnable = this.doneRunnable;
                    break label16;
                }

                runnable = this.doneRunnable;
            }

            SwingUtilities.invokeLater(runnable);
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public StackTraceTranslateThread(StackTraceTranslateDialog stackTraceTranslateDialog) {
        this.translateDialog = stackTraceTranslateDialog;
        this.doneRunnable = new TranslationDoneRunnable(this);
    }
}
