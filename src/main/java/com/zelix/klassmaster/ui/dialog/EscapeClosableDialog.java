package com.zelix.klassmaster.ui.dialog;

import java.awt.Frame;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

public abstract class EscapeClosableDialog extends JDialog {
    public Action createEscapeAction() {
        return new EscapeDialogCancelAction(this);
    }

    public EscapeClosableDialog(Frame frame1, String string) {
        super(frame1, string, true);
    }

    @Override
    public JRootPane createRootPane() {
        JRootPane jRootPane = new JRootPane();
        KeyStroke keyStroke = KeyStroke.getKeyStroke("ESCAPE");
        Action action = this.createEscapeAction();
        jRootPane.getInputMap(2).put(keyStroke, "ESCAPE");
        jRootPane.getActionMap().put("ESCAPE", action);
        return jRootPane;
    }

    public abstract void closeDialog();
}
