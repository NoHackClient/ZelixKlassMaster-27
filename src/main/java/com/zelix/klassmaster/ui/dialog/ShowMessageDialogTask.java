package com.zelix.klassmaster.ui.dialog;

import javax.swing.JFrame;

public class ShowMessageDialogTask implements Runnable {
    public final JFrame ownerFrame;
    public final String title;
    public final String message;
    public final String details;

    @Override
    public void run() {
        new CopyableMessageDialog(this.ownerFrame, this.title, this.message, this.details);
    }

    public ShowMessageDialogTask(JFrame jFrame, String string, String string1, String string2) {
        this.ownerFrame = jFrame;
        this.title = string;
        this.message = string1;
        this.details = string2;
    }
}
