package com.zelix.klassmaster.ui.wizard;

import com.zelix.GuiResources;
import com.zelix.UserPreferences;
import com.zelix.klassmaster.classfile.hierarchy.ZkmClasspath;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.dialog.ClasspathDialog;
import com.zelix.klassmaster.util.DialogCallback;
import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;

public class ClasspathStepDialog extends ClasspathDialog {
    public static String buttonConstraints;
    public JButton previousBtn;

    public void goPrevious() throws ZkmException, IOException {
        super.closedByButton = true;
        this.closeFrame();
        super.dialogCallback.onDialogResult_v(null, 2);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            if (actionEvent.getSource() == this.previousBtn) {
                this.goPrevious();
            } else {
                super.actionPerformed(actionEvent);
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    @Override
    public void createMainButtons(String string, String string1, StringBuffer stringBuffer, Container container1) {
        this.previousBtn = new JButton("Previous");
        this.previousBtn.setToolTipText(GuiResources.getTooltipText("PREVIOUS_SCREEN"));
        container1.add(this.previousBtn, "previousBtn");
        super.okBtn = new JButton(string);
        super.okBtn.setToolTipText(string1);
        container1.add(super.okBtn, "okBtn");
        super.cancelBtn = new JButton("Cancel");
        super.cancelBtn.setToolTipText(GuiResources.getTooltipText("CANCEL_DIALOG"));
        container1.add(super.cancelBtn, "cancelBtn");
        super.importBtn = new JButton("Import");
        super.importBtn.setToolTipText(GuiResources.getTooltipText("IMPORT_DIALOG"));
        container1.add(super.importBtn, "importBtn");
        super.exportBtn = new JButton("Export");
        super.exportBtn.setToolTipText(GuiResources.getTooltipText("EXPORT_DIALOG"));
        container1.add(super.exportBtn, "exportBtn");
        super.helpBtn = new JButton("Help");
        super.helpBtn.setToolTipText(GuiResources.getTooltipText("SCREEN_HELP"));
        container1.add(super.helpBtn, "helpBtn");
        this.previousBtn.addKeyListener(this);
        super.okBtn.addKeyListener(this);
        super.cancelBtn.addKeyListener(this);
        super.importBtn.addKeyListener(this);
        super.helpBtn.addKeyListener(this);
        this.previousBtn.addActionListener(this);
        super.okBtn.addActionListener(this);
        super.cancelBtn.addActionListener(this);
        super.importBtn.addActionListener(this);
        super.exportBtn.addActionListener(this);
        super.helpBtn.addActionListener(this);
        stringBuffer.append(buttonConstraints);
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        try {
            Object object = keyEvent.getSource();
            if (keyEvent.getKeyCode() == 10) {
                if (object == this.previousBtn) {
                    this.goPrevious();
                } else {
                    super.keyPressed(keyEvent);
                }
            } else {
                super.keyPressed(keyEvent);
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public ClasspathStepDialog(
            JFrame jFrame, String string, ZkmClasspath zkmClasspath, String string1, String string2, UserPreferences userPreferences1, DialogCallback dialogCallback1
    ) {
        super(jFrame, string, zkmClasspath, string1, "Next", string2, userPreferences1, dialogCallback1);
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
        buttonConstraints = "btnHeight=max(previousBtn.defaultHeight, okBtn.defaultHeight, cancelBtn.defaultHeight, importBtn.defaultHeight, exportBtn.defaultHeight, helpBtn.defaultHeight);btnWidth=max(previousBtn.defaultWidth, okBtn.defaultWidth, cancelBtn.defaultWidth, importBtn.defaultWidth, exportBtn.defaultWidth, helpBtn.defaultWidth);previousBtn.centerX=container.width*10/100;previousBtn.bottom=container.bottom-10;previousBtn.height=btnHeight;previousBtn.width=btnWidth;okBtn.centerX=container.width*26/100;okBtn.top=previousBtn.top;okBtn.height=btnHeight;okBtn.width=btnWidth;cancelBtn.centerX=container.width*42/100;cancelBtn.top=previousBtn.top;cancelBtn.height=btnHeight;cancelBtn.width=btnWidth;importBtn.centerX=container.width*58/100;importBtn.top=previousBtn.top;importBtn.height=btnHeight;importBtn.width=btnWidth;exportBtn.centerX=container.width*74/100;exportBtn.top=previousBtn.top;exportBtn.height=btnHeight;exportBtn.width=btnWidth;helpBtn.centerX=container.width*90/100;helpBtn.top=previousBtn.top;helpBtn.height=btnHeight;helpBtn.width=btnWidth;layout.minWidth=btnWidth * 7";
    }
}
