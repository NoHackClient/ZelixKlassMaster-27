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

public class ProceedConfirmDialog extends OwnedFrameDialogBase implements HelperDialogMarker, ActionListener, KeyListener {
    private static final long ENTER_KEY_CODE = 3040697181821468682L;
    public static String baseLayoutConstraints = "minWidth=btnWidth*3+20;mainPnl.top=0;mainPnl.left=0;mainPnl.right=container.right;mainPnl.bottom=container.bottom-btnHeight-10;btnWidth=max(okBtn.defaultWidth, cancelBtn.defaultWidth, helpBtn.defaultWidth);btnHeight=max(okBtn.defaultHeight, cancelBtn.defaultHeight, helpBtn.defaultHeight);okBtn.width=btnWidth;okBtn.height=btnHeight;okBtn.top=mainPnl.bottom+5;okBtn.centerX=container.width*16/100;cancelBtn.width=btnWidth;cancelBtn.height=btnHeight;cancelBtn.top=okBtn.top;cancelBtn.centerX=container.width*50/100;helpBtn.width=btnWidth;helpBtn.height=btnHeight;helpBtn.top=okBtn.top;helpBtn.centerX=container.width*84/100;";
    public BevelBorderPanel mainPnl;
    public boolean closedByButton;
    public JButton okBtn;
    public JButton cancelBtn;
    public String helpPageId;
    public JEditorPane infoPane;
    public JButton helpBtn;
    public JFrame parentFrame;
    public DialogCallback callback;

    @Override
    public void keyReleased(KeyEvent keyEvent) {
    }

    public ProceedConfirmDialog(
            JFrame jFrame, String string, String string1, String string2, String string3, String string4, int ba, int bb, DialogCallback dialogCallback1
    ) {
        super(jFrame, string, string1, string2, string3, string4, ba, bb);
        this.parentFrame = jFrame;
        this.callback = dialogCallback1;
        this.showCenteredOnOwner();
    }

    public void proceed() throws ZkmException, IOException {
        this.closedByButton = true;
        this.closeFrame();
        this.callback.onDialogResult(1);
    }

    @Override
    public void cancelDialog() throws ZkmException, IOException {
        this.closedByButton = true;
        this.closeFrame();
        this.callback.onDialogCancelled();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            Object object = actionEvent.getSource();
            if (object == this.okBtn) {
                this.proceed();
            } else if (object == this.cancelBtn) {
                this.cancelDialog();
            } else if (object == this.helpBtn) {
                this.showHelp();
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }

    @Override
    public void closeFrame() throws ZkmException, IOException {
        super.closeFrame();
        if (!this.closedByButton) {
            this.callback.onDialogCancelled();
        }
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }

    @Override
    public void focusDefaultComponent() {
        SwingUtils.requestFocusOnEdt(this.okBtn);
    }

    @Override
    public void buildContents(Object object, Object object1, Object object2, Object object3, Object object4, Object object5) {
        String string = (String) object;
        String string1 = (String) object1;
        String string2 = (String) object2;
        this.helpPageId = (String) object3;
        int ba = (Integer) object4;
        int bb = (Integer) object5;
        Container container1 = this.getContentPane();
        ConstraintLayout constraintLayout1 = new ConstraintLayout(container1);
        container1.setLayout(constraintLayout1);
        this.okBtn = new JButton(string);
        this.okBtn.setToolTipText(GuiResources.getTooltipText("OK_PROCEED"));
        this.cancelBtn = new JButton(string1);
        this.cancelBtn.setToolTipText(GuiResources.getTooltipText("CANCEL_DIALOG"));
        this.helpBtn = new JButton("Help");
        this.helpBtn.setToolTipText(GuiResources.getTooltipText("SCREEN_HELP"));
        this.okBtn.addActionListener(this);
        this.cancelBtn.addActionListener(this);
        this.helpBtn.addActionListener(this);
        this.okBtn.addKeyListener(this);
        this.cancelBtn.addKeyListener(this);
        this.helpBtn.addKeyListener(this);
        container1.add(this.okBtn, "okBtn");
        container1.add(this.cancelBtn, "cancelBtn");
        container1.add(this.helpBtn, "helpBtn");
        this.mainPnl = new BevelBorderPanel(false);
        container1.add(this.mainPnl, "mainPnl");
        constraintLayout1.parseConstraints(baseLayoutConstraints + "layout.minWidth=minWidth;layout.width=max(minWidth," + ba + ");layout.height=" + bb + ";");
        this.loadInfoPage(string2);
        this.setIconImage(KlassMaster.getLogoImage(this));
        SwingUtils.packOnEdt(this);
    }

    public final void showHelp() {
        GuiResources.showHelpTopic(this.helpPageId);
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        try {
            if (keyEvent.getKeyCode() == (int) ENTER_KEY_CODE) {
                if (keyEvent.getSource() == this.okBtn) {
                    this.proceed();
                } else if (keyEvent.getSource() == this.cancelBtn) {
                    this.cancelDialog();
                } else if (keyEvent.getSource() == this.helpBtn) {
                    this.showHelp();
                }
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }

    public void loadInfoPage(String string) {
        this.mainPnl.setLayout(new BorderLayout());
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
}
