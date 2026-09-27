package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.component.SwingUtils;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.IOException;
import javax.swing.Action;
import javax.swing.JFrame;

public abstract class OwnedFrameDialogBase extends EscapeClosableFrame {
    public JFrame ownerFrame;
    public boolean disablesOwner;

    public abstract void buildContents(Object object, Object object1, Object object2, Object object3, Object object4, Object object5);

    @Override
    public Action createEscapeAction() {
        return new DialogEscapeAction(this);
    }

    public abstract void cancelDialog() throws ZkmException, IOException;

    public OwnedFrameDialogBase(JFrame jFrame, String string, Object object, Object object1, Object object2, Object object3, Object object4, Object object5) {
        this(jFrame, string, true, object, object1, object2, object3, object4, object5, null);
    }

    public OwnedFrameDialogBase(JFrame jFrame, String string, Object object) {
        this(jFrame, string, true, object, null, null, null, null, null, null);
    }

    public OwnedFrameDialogBase(JFrame jFrame, String string, Object object, Object object1, Object object2, Object object3) {
        this(jFrame, string, true, object, object1, object2, object3, null, null, null);
    }

    public OwnedFrameDialogBase(JFrame jFrame, String string, Object object, Object object1, Object object2, Object object3, Object object4) {
        this(jFrame, string, true, object, object1, object2, object3, object4, null, null);
    }

    public OwnedFrameDialogBase(
            JFrame jFrame, String string, boolean bl, Object object, Object object1, Object object2, Object object3, Object object4, Object object5, Object object6
    ) {
        super(string);
        this.ownerFrame = jFrame;
        this.disablesOwner = true;
        jFrame.setEnabled(false);
        DialogWindowCloseListener dialogWindowCloseListener = new DialogWindowCloseListener(this);
        this.addWindowListener(dialogWindowCloseListener);
        this.buildContents(object, object1, object2, object3, object4, object5);
    }

    public final void showCentered(boolean bl) {
        Dimension dimension = this.getSize();
        if (bl) {
            Point point = this.ownerFrame.getLocationOnScreen();
            Dimension dimension1 = this.ownerFrame.getSize();
            int ba = dimension1.width / 2 - dimension.width / 2 + point.x;
            int bb = dimension1.height / 2 - dimension.height / 2 + point.y;
            ba = Math.max(0, ba) + 0;
            bb = Math.max(0, bb) + 0;
            this.setLocation(ba, bb);
        } else {
            Dimension dimension2 = Toolkit.getDefaultToolkit().getScreenSize();
            int bc = dimension2.width / 2 - dimension.width / 2;
            int bd = dimension2.height / 2 - dimension.height / 2;
            bc = Math.max(0, bc) + 0;
            bd = Math.max(0, bd) + 0;
            this.setLocation(bc, bd);
        }

        SwingUtils.setVisibleOnEdt(this);
        this.toFront();
        this.focusDefaultComponent();
    }

    public OwnedFrameDialogBase(JFrame jFrame, String string, Object object, Object object1) {
        this(jFrame, string, true, object, object1, null, null, null, null, null);
    }

    @Override
    public void closeFrame() throws ZkmException, IOException {
        super.closeFrame();
        if (this.disablesOwner) {
            this.ownerFrame.setEnabled(true);
            this.ownerFrame.toFront();
        }
    }

    public void focusDefaultComponent() {
    }

    public OwnedFrameDialogBase(JFrame jFrame, Object object, Object object1, Object object2, Object object3, Object object4) {
        this(jFrame, "Build Helper", true, object, object1, object2, object3, object4, null, null);
    }

    public void showCenteredOnOwner() {
        this.showCentered(true);
    }

    public OwnedFrameDialogBase(JFrame jFrame, String string, Object object, Object object1, Object object2) {
        this(jFrame, string, true, object, object1, object2, null, null, null, null);
    }

    public OwnedFrameDialogBase(JFrame jFrame, String string) {
        this(jFrame, string, true, null, null, null, null, null, null, null);
    }
}
