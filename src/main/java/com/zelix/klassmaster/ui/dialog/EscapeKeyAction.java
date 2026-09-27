package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public class EscapeKeyAction extends AbstractAction {
    public final EscapeClosableFrame frame;

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            if (this.frame.isEnabled()) {
                this.frame.closeFrame();
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public EscapeKeyAction(EscapeClosableFrame escapeClosableFrame) {
        this.frame = escapeClosableFrame;
    }
}
