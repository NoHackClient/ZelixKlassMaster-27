package com.zelix.klassmaster.ui.wizard;

import com.zelix.GuiResources;
import com.zelix.klassmaster.archive.InputFileLocation;
import com.zelix.klassmaster.archive.SourceArchive;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.dialog.HelperDialogMarker;
import com.zelix.klassmaster.ui.dialog.OpenClassesDialog;
import com.zelix.klassmaster.util.DialogCallback;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;

public class WizardOpenClassesDialog extends OpenClassesDialog implements HelperDialogMarker {
    public JButton previousBtn;

    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        try {
            if (keyEvent.getKeyCode() == 10) {
                Object object = keyEvent.getSource();
                if (object == this.previousBtn) {
                    this.goToPreviousStep();
                } else if (object == super.okBtn) {
                    this.confirmOpen();
                } else if (object == super.cancelBtn) {
                    this.cancelOpen();
                } else if (object == super.helpBtn) {
                    this.showHelp();
                }
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public void goToPreviousStep() throws ZkmException, IOException {
        super.closedByButton = true;
        this.closeFrame();
        String string = super.fileSelector.getCurrentDirectoryPath();
        super.callback
                .onDialogResult(
                        super.classLocations,
                        (SourceArchive[]) null,
                        (InputFileLocation[]) null,
                        (InputFileLocation[]) null,
                        (InputFileLocation[]) null,
                        new ObservableHolder(string)
                );
    }

    @Override
    public void createMainButtons(String string, StringBuffer stringBuffer, Container container1) {
        this.previousBtn = new JButton("Previous");
        this.previousBtn.setToolTipText(GuiResources.getTooltipText("PREVIOUS_SCREEN"));
        this.previousBtn.addActionListener(this);
        this.previousBtn.addKeyListener(this);
        container1.add(this.previousBtn, "previousBtn");
        super.okBtn = new JButton(string);
        super.okBtn.setToolTipText(GuiResources.getTooltipText("OK_OPEN"));
        super.okBtn.addActionListener(this);
        super.okBtn.addKeyListener(this);
        container1.add(super.okBtn, "okBtn");
        super.cancelBtn = new JButton("Cancel");
        super.cancelBtn.setToolTipText(GuiResources.getTooltipText("CANCEL_DIALOG"));
        super.cancelBtn.addActionListener(this);
        super.cancelBtn.addKeyListener(this);
        container1.add(super.cancelBtn, "cancelBtn");
        super.helpBtn = new JButton("Help");
        super.helpBtn.setToolTipText(GuiResources.getTooltipText("SCREEN_HELP"));
        super.helpBtn.addActionListener(this);
        super.helpBtn.addKeyListener(this);
        container1.add(super.helpBtn, "helpBtn");
        stringBuffer.append(
                "previousBtn.top=fileSelector.bottom+5;previousBtn.centerX=container.width*12/100;previousBtn.width=majorBtnWidth;previousBtn.height=majorBtnHeight;okBtn.top=previousBtn.top;okBtn.centerX=container.width*37/100;okBtn.width=majorBtnWidth;okBtn.height=majorBtnHeight;cancelBtn.top=previousBtn.top;cancelBtn.centerX=container.width*62/100;cancelBtn.width=majorBtnWidth;cancelBtn.height=majorBtnHeight;helpBtn.top=previousBtn.top;helpBtn.centerX=container.width*87/100;helpBtn.width=majorBtnWidth;helpBtn.height=majorBtnHeight;majorBtnHeight=max(previousBtn.defaultHeight, okBtn.defaultHeight, cancelBtn.defaultHeight, helpBtn.defaultHeight);majorBtnWidth=max(previousBtn.defaultWidth, okBtn.defaultWidth, cancelBtn.defaultWidth, helpBtn.defaultWidth)"
        );
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            Object object = actionEvent.getSource();
            if (object == super.okBtn) {
                this.confirmOpen();
            } else if (object == super.cancelBtn) {
                this.cancelOpen();
            } else if (object == this.previousBtn) {
                this.goToPreviousStep();
            } else if (object == super.helpBtn) {
                this.showHelp();
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public WizardOpenClassesDialog(
            JFrame jFrame,
            String string,
            String string1,
            boolean bl,
            String string2,
            boolean bl1,
            InputFileLocation[] inputFileLocations,
            DialogCallback dialogCallback1
    ) {
        super(jFrame, string, string1, bl, string2, bl1, inputFileLocations, dialogCallback1);
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
    }
}
