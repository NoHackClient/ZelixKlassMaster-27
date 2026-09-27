package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class HelpDialogKeyListener extends KeyAdapter {
    public final ClassOpenWarningsDialog dialog;

    public HelpDialogKeyListener(ClassOpenWarningsDialog classOpenWarningsDialog) {
        this.dialog = classOpenWarningsDialog;
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        try {
            if (keyEvent.getKeyCode() == 10) {
                this.dialog.closeDialog();
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
    }
}
