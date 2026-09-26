package com.zelix.klassmaster.ui.wizard;

import com.zelix.GuiResources;
import com.zelix.klassmaster.KlassMaster;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.component.BevelBorderPanel;
import com.zelix.klassmaster.ui.component.ConstraintLayout;
import com.zelix.klassmaster.ui.component.SwingUtils;
import com.zelix.klassmaster.ui.component.ZkmScrollPane;
import com.zelix.klassmaster.ui.dialog.HelperDialogMarker;
import com.zelix.klassmaster.ui.dialog.OwnedFrameDialogBase;
import com.zelix.klassmaster.util.DialogCallback;
import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;

public class HelperInfoDialog extends OwnedFrameDialogBase implements HelperDialogMarker, ActionListener, KeyListener {
    private static final long ENTER_KEY_CODE = -8975343950457995254L;
    public static String baseLayoutConstraints = "minWidth=btnWidth*3+20;mainPnl.top=0;mainPnl.left=0;mainPnl.right=container.right;mainPnl.bottom=container.bottom-btnHeight-10;btnWidth=max(previousBtn.defaultWidth, okBtn.defaultWidth, cancelBtn.defaultWidth);btnHeight=max(previousBtn.defaultHeight, okBtn.defaultHeight, cancelBtn.defaultHeight);previousBtn.width=btnWidth;previousBtn.height=btnHeight;previousBtn.top=mainPnl.bottom+5;previousBtn.centerX=container.width*16/100;okBtn.width=btnWidth;okBtn.height=btnHeight;okBtn.top=previousBtn.top;okBtn.centerX=container.width*50/100;cancelBtn.width=btnWidth;cancelBtn.height=btnHeight;cancelBtn.top=previousBtn.top;cancelBtn.centerX=container.width*84/100;";
    public BevelBorderPanel mainPnl;
    public JButton previousBtn;
    public JButton cancelBtn;
    public JEditorPane infoPane;
    public JButton okBtn;
    public boolean closedByButton;
    public JFrame parentFrame;
    public DialogCallback callback;

    @Override
    public void focusDefaultComponent() {
        SwingUtils.requestFocusOnEdt(this.okBtn);
    }

    @Override
    public void cancelDialog() throws ZkmException, IOException {
        this.closedByButton = true;
        this.closeFrame();
        this.callback.onDialogCancelled();
    }

    @Override
    public void closeFrame() throws ZkmException, IOException {
        super.closeFrame();
        if (!this.closedByButton) {
            this.callback.onDialogCancelled();
        }
    }

    public void goNext() throws ZkmException, IOException {
        this.closedByButton = true;
        this.closeFrame();
        this.callback.onDialogResult(1);
    }

    public void goPrevious() throws ZkmException, IOException {
        this.closedByButton = true;
        this.closeFrame();
        this.callback.onDialogResult(2);
    }

    public HelperInfoDialog(JFrame jFrame, String string, DialogCallback dialogCallback1) {
        super(jFrame, "Next", string, "004", 350, 250);
        this.parentFrame = jFrame;
        this.callback = dialogCallback1;
        this.showCenteredOnOwner();
        SwingUtils.requestFocusOnEdt(this.okBtn);
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        try {
            if (keyEvent.getKeyCode() == (int) ENTER_KEY_CODE) {
                if (keyEvent.getSource() == this.previousBtn) {
                    this.goPrevious();
                } else if (keyEvent.getSource() == this.okBtn) {
                    this.goNext();
                } else if (keyEvent.getSource() == this.cancelBtn) {
                    this.cancelDialog();
                }
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }

    public void loadInfoPage(String string) {
        this.mainPnl.setLayout(new BorderLayout());
        this.infoPane = null;
        this.infoPane = new JEditorPane();
        this.infoPane.setEditable(false);

        BevelBorderPanel bevelBorderPanel;
        label15:
        {
            try {
                this.infoPane.setPage(GuiResources.getHelpTopicUrl(string));
            } catch (IOException iOException) {
                this.infoPane.setText(iOException.toString() + " : " + string);
                bevelBorderPanel = this.mainPnl;
                break label15;
            }

            bevelBorderPanel = this.mainPnl;
        }

        bevelBorderPanel.add(new ZkmScrollPane(this.infoPane), "Center");
    }

    @Override
    public void buildContents(Object object, Object object1, Object object2, Object object3, Object object4, Object object5) {
        String string = (String) object;
        String string1 = (String) object1;
        String string2 = (String) object2;
        int ba = (Integer) object3;
        int bb = (Integer) object4;
        Container container1 = this.getContentPane();
        ConstraintLayout constraintLayout1 = new ConstraintLayout(container1);
        container1.setLayout(constraintLayout1);
        this.previousBtn = new JButton("Previous");
        this.previousBtn.setToolTipText(GuiResources.getTooltipText("PREVIOUS_SCREEN"));
        this.okBtn = new JButton(string);
        this.okBtn.setToolTipText(string1);
        this.cancelBtn = new JButton("Cancel");
        this.cancelBtn.setToolTipText(GuiResources.getTooltipText("CANCEL_DIALOG"));
        this.previousBtn.addActionListener(this);
        this.okBtn.addActionListener(this);
        this.cancelBtn.addActionListener(this);
        this.previousBtn.addKeyListener(this);
        this.okBtn.addKeyListener(this);
        this.cancelBtn.addKeyListener(this);
        container1.add(this.previousBtn, "previousBtn");
        container1.add(this.okBtn, "okBtn");
        container1.add(this.cancelBtn, "cancelBtn");
        this.mainPnl = new BevelBorderPanel(false);
        container1.add(this.mainPnl, "mainPnl");
        constraintLayout1.parseConstraints(baseLayoutConstraints + "layout.minWidth=minWidth;layout.width=max(minWidth," + ba + ");layout.height=" + bb + ";");
        this.loadInfoPage(string2);
        this.setIconImage(KlassMaster.getLogoImage(this));
        SwingUtils.packOnEdt(this);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            Object object = actionEvent.getSource();
            if (object == this.previousBtn) {
                this.goPrevious();
            } else if (object == this.okBtn) {
                this.goNext();
            } else if (object == this.cancelBtn) {
                this.cancelDialog();
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }
}
