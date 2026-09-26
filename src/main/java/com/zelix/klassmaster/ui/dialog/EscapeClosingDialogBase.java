package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.component.SwingUtils;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.IOException;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

public abstract class EscapeClosingDialogBase extends JDialog {
    public JFrame ownerFrame_JFrame;
    public static final String LINE_SEPARATOR = SwingUtils.LINE_SEPARATOR;

    @Override
    public JRootPane createRootPane() {
        JRootPane jRootPane = new JRootPane();
        KeyStroke keyStroke = KeyStroke.getKeyStroke("ESCAPE");
        Action action = this.createEscapeAction();
        jRootPane.getInputMap(2).put(keyStroke, "ESCAPE");
        jRootPane.getActionMap().put("ESCAPE", action);
        return jRootPane;
    }

    public final void showCentered() {
        this.showCenteredWithOffset(0, 0, false);
    }

    public final void showCenteredWithOffset(int ba, int bb, boolean bl) {
        label17:
        {
            Dimension dimension = this.getSize();
            Toolkit toolkit;
            if (bl) {
                if (this.ownerFrame_JFrame.isVisible()) {
                    Point point = this.ownerFrame_JFrame.getLocationOnScreen();
                    Dimension dimension1 = this.ownerFrame_JFrame.getSize();
                    int bc = dimension1.width / 2 - dimension.width / 2 + point.x;
                    int bd = dimension1.height / 2 - dimension.height / 2 + point.y;
                    bc = Math.max(0, bc) + ba;
                    bd = Math.max(0, bd) + bb;
                    this.setLocation(bc, bd);
                    break label17;
                }

                toolkit = Toolkit.getDefaultToolkit();
            } else {
                toolkit = Toolkit.getDefaultToolkit();
            }

            Dimension dimension2 = toolkit.getScreenSize();
            int be = dimension2.width / 2 - dimension.width / 2;
            int bf = dimension2.height / 2 - dimension.height / 2;
            be = Math.max(0, be) + ba;
            bf = Math.max(0, bf) + bb;
            this.setLocation(be, bf);
        }

        SwingUtils.setVisibleOnEdt(this);
    }

    public final void showCenteredOnOwner() {
        this.showCenteredWithOffset(0, 0, true);
    }

    public EscapeClosingDialogBase(JFrame jFrame, String string, boolean bl) {
        super(jFrame, string, bl);
        this.ownerFrame_JFrame = jFrame;
        DialogCloseListener dialogCloseListener = new DialogCloseListener(this);
        this.addWindowListener(dialogCloseListener);
    }

    public Action createEscapeAction() {
        return new EscapeCloseAction(this);
    }

    public void closeDialog() throws ZkmException, IOException {
        this.setVisible(false);
        this.dispose();
    }
}
