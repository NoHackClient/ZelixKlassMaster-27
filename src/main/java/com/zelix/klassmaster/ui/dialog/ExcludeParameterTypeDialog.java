package com.zelix.klassmaster.ui.dialog;

import com.zelix.GuiResources;
import com.zelix.klassmaster.KlassMaster;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.component.BevelBorderPanel;
import com.zelix.klassmaster.ui.component.ConstraintLayout;
import com.zelix.klassmaster.util.DialogCallback;
import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Enumeration;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class ExcludeParameterTypeDialog extends OwnedFrameDialogBase implements ActionListener, KeyListener {
    public static String[] layoutSpec = new String[]{
            "mainPnlWidth=mainPnl.defaultWidth+15",
            "widthOfBtns=btnWidth*3+20",
            "layout.minWidth=max(widthOfBtns, mainPnlWidth)",
            "layout.minHeight=255",
            "btnWidth=max(okBtn.defaultWidth, cancelBtn.defaultWidth, helpBtn.defaultWidth)",
            "btnHeight=max(okBtn.defaultHeight, cancelBtn.defaultHeight, helpBtn.defaultHeight)",
            "mainPnl.left=0",
            "mainPnl.top=0",
            "mainPnl.right=container.right",
            "mainPnl.bottom=container.bottom-btnHeight-10",
            "okBtn.top=mainPnl.bottom+5",
            "okBtn.centerX=container.width*16/100",
            "okBtn.width=btnWidth",
            "okBtn.height=btnHeight",
            "cancelBtn.top=okBtn.top",
            "cancelBtn.centerX=container.width*50/100",
            "cancelBtn.width=btnWidth",
            "cancelBtn.height=btnHeight",
            "helpBtn.top=okBtn.top",
            "helpBtn.centerX=container.width*84/100",
            "helpBtn.width=btnWidth",
            "helpBtn.height=btnHeight"
    };
    public static String[] mainPanelLayoutSpec = new String[]{
            "typeLbl.centerX=container.width*50/100",
            "typeLbl.centerY=container.height*10/100",
            "chkPanel.centerX=container.width*50/100",
            "chkPanel.centerY=container.height*50/100"
    };
    public static String[] radioPanelLayoutSpec = new String[]{
            "packageTypeChk.top=0",
            "packageTypeChk.left=0",
            "classTypeChk.top=packageTypeChk.bottom+5",
            "classTypeChk.left=0",
            "fieldTypeChk.top=classTypeChk.bottom+5",
            "fieldTypeChk.left=0",
            "methodTypeChk.top=fieldTypeChk.bottom+5",
            "methodTypeChk.left=0",
            "classSuffixTypeChk.top=methodTypeChk.bottom+5",
            "classSuffixTypeChk.left=0"
    };
    public JButton okBtn;
    public JButton helpBtn;
    public ButtonGroup typeButtonGroup;
    public JRadioButton classSuffixTypeChk;
    public JButton cancelBtn;
    public JRadioButton methodTypeChk;
    public JRadioButton packageTypeChk;
    public JRadioButton fieldTypeChk;
    public BevelBorderPanel mainPnl;
    public JRadioButton classTypeChk;
    public DialogCallback dialogCallback;

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        try {
            if (keyEvent.getKeyCode() == 10) {
                if (keyEvent.getSource() == this.okBtn) {
                    this.onOk();
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

    @Override
    public void buildContents(Object object, Object object1, Object object2, Object object3, Object object4, Object object5) {
        Container container1 = this.getContentPane();
        ConstraintLayout constraintLayout1 = new ConstraintLayout(container1);
        container1.setLayout(constraintLayout1);
        this.okBtn = new JButton("OK");
        this.okBtn.setToolTipText(GuiResources.getTooltipText("OK_PROCEED"));
        this.cancelBtn = new JButton("Exit");
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
        this.mainPnl = new BevelBorderPanel(true);
        container1.add(this.mainPnl, "mainPnl");
        constraintLayout1.setConstraints(layoutSpec);
        this.buildMainPanel();
        this.setIconImage(KlassMaster.getLogoImage(this));
        this.pack();
    }

    public void buildMainPanel() {
        ConstraintLayout constraintLayout1 = new ConstraintLayout(this.mainPnl);
        this.mainPnl.setLayout(constraintLayout1);
        JLabel jLabel = new JLabel("Select the exclude parameter type:");
        this.mainPnl.add(jLabel, "typeLbl");
        this.typeButtonGroup = new ButtonGroup();
        this.packageTypeChk = new JRadioButton("Package", true);
        this.classTypeChk = new JRadioButton("Class", false);
        this.fieldTypeChk = new JRadioButton("Field", false);
        this.methodTypeChk = new JRadioButton("Method", false);
        this.classSuffixTypeChk = new JRadioButton("Class Suffix", false);
        this.typeButtonGroup.add(this.packageTypeChk);
        this.typeButtonGroup.add(this.classTypeChk);
        this.typeButtonGroup.add(this.fieldTypeChk);
        this.typeButtonGroup.add(this.methodTypeChk);
        this.typeButtonGroup.add(this.classSuffixTypeChk);
        JPanel jPanel = new JPanel();
        this.buildRadioPanel(jPanel);
        this.mainPnl.add(jPanel, "chkPanel");
        constraintLayout1.setConstraints(mainPanelLayoutSpec);
    }

    @Override
    public void cancelDialog() throws ZkmException, IOException {
        this.closeFrame();
        this.dialogCallback.onDialogCancelled();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            Object object = actionEvent.getSource();
            if (object == this.okBtn) {
                this.onOk();
            } else if (object == this.cancelBtn) {
                this.cancelDialog();
            } else if (object == this.helpBtn) {
                this.showHelp();
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }

    public final void showHelp() {
        GuiResources.showHelpTopic("026");
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
    }

    public void onOk() throws ZkmException, IOException {
        this.closeFrame();
        JRadioButton jRadioButton = null;
        Enumeration<AbstractButton> enumeration = this.typeButtonGroup.getElements();

        while (enumeration.hasMoreElements()) {
            JRadioButton jRadioButton1 = (JRadioButton) enumeration.nextElement();
            if (jRadioButton1.isSelected()) {
                jRadioButton = jRadioButton1;
                break;
            }
        }

        if (jRadioButton == this.packageTypeChk) {
            this.dialogCallback.onDialogResult("1");
        } else if (jRadioButton == this.classTypeChk) {
            this.dialogCallback.onDialogResult("2");
        } else if (jRadioButton == this.fieldTypeChk) {
            this.dialogCallback.onDialogResult("3");
        } else if (jRadioButton == this.methodTypeChk) {
            this.dialogCallback.onDialogResult("4");
        } else if (jRadioButton == this.classSuffixTypeChk) {
            this.dialogCallback.onDialogResult("5");
        }
    }

    public ExcludeParameterTypeDialog(JFrame jFrame, DialogCallback dialogCallback1) {
        super(jFrame, "Zelix KlassMaster Exclude Parameter Type");
        this.dialogCallback = dialogCallback1;
        this.showCenteredOnOwner();
    }

    public void buildRadioPanel(JPanel jPanel) {
        ConstraintLayout constraintLayout1 = new ConstraintLayout(jPanel);
        jPanel.setLayout(constraintLayout1);
        jPanel.add(this.packageTypeChk, "packageTypeChk");
        jPanel.add(this.classTypeChk, "classTypeChk");
        jPanel.add(this.fieldTypeChk, "fieldTypeChk");
        jPanel.add(this.methodTypeChk, "methodTypeChk");
        jPanel.add(this.classSuffixTypeChk, "classSuffixTypeChk");
        constraintLayout1.setConstraints(radioPanelLayoutSpec);
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }
}
