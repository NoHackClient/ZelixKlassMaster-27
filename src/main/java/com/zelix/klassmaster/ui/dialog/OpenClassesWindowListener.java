package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class OpenClassesWindowListener extends WindowAdapter {
    public final OpenClassesDialog openClassesDialog;

    public OpenClassesWindowListener(OpenClassesDialog openClassesDialog1) {
        this.openClassesDialog = openClassesDialog1;
    }

    @Override
    public void windowClosing(WindowEvent windowEvent) {
        try {
            this.openClassesDialog.closeFrame();
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }
}
