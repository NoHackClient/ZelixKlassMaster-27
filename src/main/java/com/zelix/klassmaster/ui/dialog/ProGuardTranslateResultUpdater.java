package com.zelix.klassmaster.ui.dialog;

public class ProGuardTranslateResultUpdater implements Runnable {
    public final ProGuardTranslateThread translateThread;

    public ProGuardTranslateResultUpdater(ProGuardTranslateThread proGuardTranslateThread) {
        this.translateThread = proGuardTranslateThread;
    }

    @Override
    public void run() {
        this.translateThread.translateDialog.showTranslationResult(this.translateThread.translatedScript);
    }
}
