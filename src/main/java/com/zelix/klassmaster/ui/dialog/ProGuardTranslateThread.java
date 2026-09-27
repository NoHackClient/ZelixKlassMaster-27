package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.proguard.ProGuardInputTranslator;
import com.zelix.klassmaster.util.ZkmUtils;

import java.util.Properties;
import javax.swing.SwingUtilities;

public class ProGuardTranslateThread extends Thread {
    public String translatedScript;
    public final ProGuardTranslateDialog translateDialog;
    public Runnable resultUpdater;

    public ProGuardTranslateThread(ProGuardTranslateDialog proGuardTranslateDialog) {
        this.translateDialog = proGuardTranslateDialog;
        this.resultUpdater = new ProGuardTranslateResultUpdater(this);
    }

    @Override
    public void run() {
        try {
            this.translatedScript = ProGuardInputTranslator.translateToZkmScript(this.translateDialog.proGuardArea.getText(), (Properties) null);
            SwingUtilities.invokeLater(this.resultUpdater);
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }
}
