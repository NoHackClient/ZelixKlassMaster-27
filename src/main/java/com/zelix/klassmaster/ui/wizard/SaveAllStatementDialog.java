package com.zelix.klassmaster.ui.wizard;

import com.zelix.GuiResources;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.component.ConstraintLayout;
import com.zelix.klassmaster.ui.dialog.HelperDialogMarker;
import com.zelix.klassmaster.ui.dialog.SaveClassesDialog;
import com.zelix.klassmaster.util.DialogCallback;
import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;

public class SaveAllStatementDialog extends SaveClassesDialog implements HelperDialogMarker, ActionListener, KeyListener {
    public JButton previousBtn;
    public static String[] helperLayoutConstraints = new String[]{
            "layout.minWidth=btnWidth*4+25",
            "layout.minHeight=300",
            "fileSelector.left=5",
            "fileSelector.top=0",
            "fileSelector.bottom=container.bottom-btnHeight-15",
            "fileSelector.right=container.right-5",
            "btnWidth=max(okBtn.defaultWidth, cancelBtn.defaultWidth, previousBtn.defaultWidth, helpBtn.defaultWidth)",
            "btnHeight=max(okBtn.defaultHeight, cancelBtn.defaultHeight, previousBtn.defaultHeight, helpBtn.defaultHeight)",
            "previousBtn.centerX=container.width*12/100",
            "previousBtn.bottom=container.bottom-5",
            "previousBtn.width=btnWidth",
            "okBtn.centerX=container.width*37/100",
            "okBtn.bottom=container.bottom-5",
            "okBtn.width=btnWidth",
            "cancelBtn.centerX=container.width*62/100",
            "cancelBtn.bottom=container.bottom-5",
            "cancelBtn.width=btnWidth",
            "helpBtn.centerX=container.width*87/100",
            "helpBtn.bottom=container.bottom-5",
            "helpBtn.width=btnWidth"
    };

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        try {
            if (keyEvent.getKeyCode() == 10) {
                if (keyEvent.getSource() == super.okBtn || keyEvent.getSource() == super.fileSelector) {
                    this.confirmSave();
                } else if (keyEvent.getSource() == this.previousBtn) {
                    this.goPrevious();
                } else if (keyEvent.getSource() == super.cancelBtn) {
                    this.cancelDialog();
                } else if (keyEvent.getSource() == super.helpBtn) {
                    this.showHelp();
                }
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            Object object = actionEvent.getSource();
            if (object instanceof JButton) {
                JButton jButton = (JButton) object;
                if (jButton == this.previousBtn) {
                    this.goPrevious();
                } else if (jButton == super.okBtn) {
                    this.confirmSave();
                } else if (jButton == super.cancelBtn) {
                    this.cancelDialog();
                } else if (jButton == super.helpBtn) {
                    this.showHelp();
                }
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }

    public SaveAllStatementDialog(JFrame jFrame, String string, String string1, DialogCallback dialogCallback1) {
        super(jFrame, "ZKM Script Helper - \"saveAll\" Statement", "Next", string, string1, dialogCallback1);
    }

    public void goPrevious() throws ZkmException, IOException {
        File file1 = super.fileSelector.getSelectedFile();
        super.closedByButton = true;
        this.closeFrame();
        super.callback.onDialogResult_v(file1, 2);
    }

    @Override
    public void createButtons(String string, String string1, ConstraintLayout constraintLayout1, Container container1) {
        this.previousBtn = new JButton("Previous");
        this.previousBtn.setToolTipText(GuiResources.getTooltipText("PREVIOUS_SCREEN"));
        container1.add(this.previousBtn, "previousBtn");
        super.okBtn = new JButton(string);
        super.okBtn.setToolTipText(string1);
        container1.add(super.okBtn, "okBtn");
        super.okBtn.setEnabled(false);
        super.cancelBtn = new JButton("Cancel");
        super.cancelBtn.setToolTipText(GuiResources.getTooltipText("CANCEL_DIALOG"));
        container1.add(super.cancelBtn, "cancelBtn");
        super.helpBtn = new JButton("Help");
        super.helpBtn.setToolTipText(GuiResources.getTooltipText("SCREEN_HELP"));
        container1.add(super.helpBtn, "helpBtn");
        this.previousBtn.addActionListener(this);
        super.okBtn.addActionListener(this);
        super.cancelBtn.addActionListener(this);
        super.helpBtn.addActionListener(this);
        this.previousBtn.addKeyListener(this);
        super.okBtn.addKeyListener(this);
        super.cancelBtn.addKeyListener(this);
        super.helpBtn.addKeyListener(this);
        constraintLayout1.setConstraints(helperLayoutConstraints);
    }

    @Override
    public void confirmSave() throws ZkmException, IOException {
        File file1 = super.fileSelector.getSelectedFile();
        if (this.confirmOverwrite(file1, "The script will save some files to the same folder from which they were opened. Continue?")) {
            super.closedByButton = true;
            this.closeFrame();
            super.callback.onDialogResult_v(file1, 1);
        }
    }
}
