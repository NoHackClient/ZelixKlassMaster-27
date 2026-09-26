package com.zelix.klassmaster.ui.dialog;

public class TranslationDoneRunnable implements Runnable {
    public final StackTraceTranslateThread translateThread;

    public TranslationDoneRunnable(StackTraceTranslateThread stackTraceTranslateThread) {
        this.translateThread = stackTraceTranslateThread;
    }

    @Override
    public void run() {
        this.translateThread.translateDialog.showTranslationResult(this.translateThread.translatedTrace);
    }
}
