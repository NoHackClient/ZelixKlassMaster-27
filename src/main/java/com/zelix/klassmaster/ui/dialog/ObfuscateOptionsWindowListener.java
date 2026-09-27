package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ObfuscateOptionsWindowListener extends WindowAdapter {
    public final ObfuscateOptionsDialogBase obfuscateOptionsDialog;

    @Override
    public void windowClosing(WindowEvent windowEvent) {
        try {
            this.obfuscateOptionsDialog.closeFrame();
            this.obfuscateOptionsDialog.callback.onDialogCancelled();
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public ObfuscateOptionsWindowListener(ObfuscateOptionsDialogBase obfuscateOptionsDialogBase) {
        this.obfuscateOptionsDialog = obfuscateOptionsDialogBase;
    }
}
