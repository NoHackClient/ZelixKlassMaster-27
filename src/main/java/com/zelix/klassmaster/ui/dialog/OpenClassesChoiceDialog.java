package com.zelix.klassmaster.ui.dialog;

import com.zelix.GuiResources;
import com.zelix.UserPreferences;
import com.zelix.klassmaster.KlassMaster;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.component.BevelBorderPanel;
import com.zelix.klassmaster.ui.component.BorderedPanel;
import com.zelix.klassmaster.ui.component.ConstraintLayout;
import com.zelix.klassmaster.ui.component.SwingUtils;
import com.zelix.klassmaster.ui.component.ZkmScrollPane;
import com.zelix.klassmaster.util.DialogCallback;
import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JRadioButton;

public class OpenClassesChoiceDialog extends OwnedFrameDialogBase implements HelperDialogMarker, ActionListener, KeyListener {
    public static final String[] LAYOUT_CONSTRAINTS = new String[]{
            "btnWidth=max(okBtn.defaultWidth, cancelBtn.defaultWidth, helpBtn.defaultWidth)",
            "btnHeight=max(okBtn.defaultHeight, cancelBtn.defaultHeight, helpBtn.defaultHeight)",
            "layout.minWidth=btnWidth*3+25",
            "mainPnl.top=5",
            "mainPnl.left=5",
            "mainPnl.right=container.right-5",
            "mainPnl.bottom=container.bottom-btnHeight-radioPnl.defaultHeight-20",
            "radioPnl.left=5",
            "radioPnl.right=container.right-5",
            "radioPnl.bottom=container.bottom-btnHeight-15",
            "okBtn.width=btnWidth",
            "okBtn.height=btnHeight",
            "okBtn.bottom=container.bottom-5",
            "okBtn.centerX=container.width*16/100",
            "cancelBtn.width=btnWidth",
            "cancelBtn.height=btnHeight",
            "cancelBtn.bottom=container.bottom-5",
            "cancelBtn.centerX=container.width*50/100",
            "helpBtn.width=btnWidth",
            "helpBtn.height=btnHeight",
            "helpBtn.bottom=container.bottom-5",
            "helpBtn.centerX=container.width*84/100"
    };
    public JButton helpBtn;
    public boolean closedByButton;
    public JButton cancelBtn;
    public BevelBorderPanel mainPnl;
    public ButtonGroup choiceGroup;
    public JButton okBtn;
    public JRadioButton openRadioBtn;
    public UserPreferences userPreferences;
    public JRadioButton buildRadioBtn;
    public DialogCallback callback;

    public String[] getLayoutConstraints() {
        return LAYOUT_CONSTRAINTS;
    }

    @Override
    public void buildContents(Object object, Object object1, Object object2, Object object3, Object object4, Object object5) {
        this.userPreferences = (UserPreferences) object;
        Container container1 = this.getContentPane();
        ConstraintLayout constraintLayout1 = new ConstraintLayout(container1);
        container1.setLayout(constraintLayout1);
        this.buildRadioBtn = new JRadioButton("Run Build Helper");
        this.openRadioBtn = new JRadioButton("Set classpath and open classes");
        this.choiceGroup = new ButtonGroup();
        this.choiceGroup.add(this.openRadioBtn);
        this.choiceGroup.add(this.buildRadioBtn);
        this.okBtn = new JButton("Next");
        this.okBtn.setToolTipText(GuiResources.getTooltipText("OK_PROCEED"));
        if (this.userPreferences.isBuildHelperSelected()) {
            this.choiceGroup.setSelected(this.buildRadioBtn.getModel(), true);
        } else {
            this.choiceGroup.setSelected(this.openRadioBtn.getModel(), true);
        }

        this.cancelBtn = new JButton("Cancel");
        this.cancelBtn.setToolTipText(GuiResources.getTooltipText("CANCEL_DIALOG"));
        this.helpBtn = new JButton("Help");
        this.helpBtn.setToolTipText(GuiResources.getTooltipText("SCREEN_HELP"));
        this.okBtn.addActionListener(this);
        this.cancelBtn.addActionListener(this);
        this.helpBtn.addActionListener(this);
        this.okBtn.addKeyListener(this);
        this.cancelBtn.addKeyListener(this);
        this.helpBtn.addKeyListener(this);
        BorderedPanel borderedPanel = new BorderedPanel();
        borderedPanel.setLayout(new BoxLayout(borderedPanel, 1));
        borderedPanel.add(this.buildRadioBtn, "buildRadioBtn");
        borderedPanel.add(this.openRadioBtn, "openRadioBtn");
        container1.add(borderedPanel, "radioPnl");
        container1.add(this.okBtn, "okBtn");
        container1.add(this.cancelBtn, "cancelBtn");
        container1.add(this.helpBtn, "helpBtn");
        this.mainPnl = new BevelBorderPanel(false);
        this.mainPnl.setLayout(new BorderLayout());
        JEditorPane jEditorPane = new JEditorPane();
        jEditorPane.setEditable(false);

        BevelBorderPanel bevelBorderPanel;
        label20:
        {
            try {
                jEditorPane.setPage(GuiResources.getHelpTopicUrl("000"));
            } catch (IOException iOException) {
                jEditorPane.setText(iOException.toString() + " : " + "000");
                bevelBorderPanel = this.mainPnl;
                break label20;
            }

            bevelBorderPanel = this.mainPnl;
        }

        bevelBorderPanel.add(new ZkmScrollPane(jEditorPane), "Center");
        container1.add(this.mainPnl, "mainPnl");
        constraintLayout1.setConstraints(this.getLayoutConstraints());
        this.setIconImage(KlassMaster.getLogoImage(this));
        this.setSize(500, 400);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            Object object = actionEvent.getSource();
            if (object == this.okBtn) {
                this.confirmChoice();
            } else if (object == this.cancelBtn) {
                this.cancelDialog();
            } else if (object == this.helpBtn) {
                this.showHelp();
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public void showHelp() {
        GuiResources.showHelpTopic("010");
    }

    @Override
    public void closeFrame() throws ZkmException, IOException {
        super.closeFrame();
        if (!this.closedByButton) {
            this.callback.onDialogCancelled();
        }
    }

    public void confirmChoice() throws ZkmException, IOException {
        this.closedByButton = true;
        this.closeFrame();
        if (this.choiceGroup.isSelected(this.buildRadioBtn.getModel())) {
            this.userPreferences.setBuildHelperSelected(true);
            this.userPreferences.savePreferences();
            this.callback.onDialogResult(1);
        } else {
            this.userPreferences.setBuildHelperSelected(false);
            this.userPreferences.savePreferences();
            this.callback.onDialogResult(2);
        }
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }

    @Override
    public void cancelDialog() throws ZkmException, IOException {
        this.closedByButton = true;
        this.closeFrame();
        this.callback.onDialogCancelled();
    }

    public OpenClassesChoiceDialog(JFrame jFrame, UserPreferences userPreferences1, DialogCallback dialogCallback1) {
        super(jFrame, "Initial Helper Dialog", userPreferences1);
        this.callback = dialogCallback1;
        this.showCenteredOnOwner();
        SwingUtils.requestFocusOnEdt(this.okBtn);
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        try {
            if (keyEvent.getKeyCode() == 10) {
                if (keyEvent.getSource() == this.okBtn) {
                    this.confirmChoice();
                } else if (keyEvent.getSource() == this.cancelBtn) {
                    this.cancelDialog();
                } else if (keyEvent.getSource() == this.helpBtn) {
                    this.showHelp();
                }
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
    }

    @Override
    public void focusDefaultComponent() {
        SwingUtils.requestFocusOnEdt(this.buildRadioBtn);
    }
}
