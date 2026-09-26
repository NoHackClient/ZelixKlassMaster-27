package com.zelix.klassmaster.ui.wizard;

import com.zelix.GuiResources;
import com.zelix.klassmaster.config.TrimOptions;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.component.ConstraintLayout;
import com.zelix.klassmaster.ui.dialog.DeleteAttributesDialog;
import com.zelix.klassmaster.util.DialogCallback;

import java.awt.Container;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;

public class TrimOptionsWizardDialog extends DeleteAttributesDialog {
    public JButton skipBtn;

    @Override
    public void buildDialog(String string, String string1, String string2) {
        Container container1 = this.getContentPane();
        ConstraintLayout constraintLayout1 = this.createContentLayout(string, string1, string2, container1);
        SkippableStepButtonListener skippableStepButtonListener = new SkippableStepButtonListener(this);
        super.previousBtn.addActionListener(skippableStepButtonListener);
        super.okBtn.addActionListener(skippableStepButtonListener);
        this.skipBtn.addActionListener(skippableStepButtonListener);
        super.cancelBtn.addActionListener(skippableStepButtonListener);
        super.helpBtn.addActionListener(skippableStepButtonListener);
        WizardStepEnterKeyListener wizardStepEnterKeyListener = new WizardStepEnterKeyListener(this);
        super.previousBtn.addKeyListener(wizardStepEnterKeyListener);
        super.okBtn.addKeyListener(wizardStepEnterKeyListener);
        this.skipBtn.addKeyListener(wizardStepEnterKeyListener);
        super.cancelBtn.addKeyListener(wizardStepEnterKeyListener);
        super.helpBtn.addKeyListener(wizardStepEnterKeyListener);
        container1.add(super.mainPnl, "mainPnl");
        this.buildMainPanel();
        container1.add(super.previousBtn, "previousBtn");
        container1.add(super.okBtn, "okBtn");
        container1.add(this.skipBtn, "skipBtn");
        container1.add(super.cancelBtn, "cancelBtn");
        container1.add(super.helpBtn, "helpBtn");
        this.finishLayoutAndShow(constraintLayout1);
    }

    public TrimOptionsWizardDialog(String string, String string1, JFrame jFrame, TrimOptions trimOptions1, DialogCallback dialogCallback1) {
        super(string, "Next", string1, jFrame, trimOptions1, dialogCallback1);
    }

    @Override
    public String getButtonLayoutSpec() {
        return "btnWidth=max(previousBtn.defaultWidth,okBtn.defaultWidth,skipBtn.defaultWidth,cancelBtn.defaultWidth,helpBtn.defaultWidth);btnHeight=max(previousBtn.defaultHeight,okBtn.defaultHeight,skipBtn.defaultHeight,cancelBtn.defaultHeight,helpBtn.defaultHeight);previousBtn.width=btnWidth;previousBtn.height=btnHeight;previousBtn.centerX=container.width*10/100;previousBtn.bottom=container.bottom-5;okBtn.width=btnWidth;okBtn.height=btnHeight;okBtn.centerX=container.width*30/100;okBtn.bottom=container.bottom-5;skipBtn.width=btnWidth;skipBtn.height=btnHeight;skipBtn.centerX=container.width*50/100;skipBtn.bottom=container.bottom-5;cancelBtn.width=btnWidth;cancelBtn.height=btnHeight;cancelBtn.centerX=container.width*70/100;cancelBtn.bottom=container.bottom-5;helpBtn.width=btnWidth;helpBtn.height=btnHeight;helpBtn.centerX=container.width*90/100;helpBtn.bottom=container.bottom-5;";
    }

    @Override
    public void createButtons(String string, String string1) {
        super.previousBtn = new JButton("Previous");
        super.previousBtn.setToolTipText(GuiResources.getTooltipText("PREVIOUS_SCREEN"));
        super.okBtn = new JButton(string);
        super.okBtn.setToolTipText(string1);
        this.skipBtn = new JButton("Skip");
        this.skipBtn.setToolTipText(GuiResources.getTooltipText("SKIP_TO_NEXT"));
        super.cancelBtn = new JButton("Cancel");
        super.cancelBtn.setToolTipText(GuiResources.getTooltipText("CANCEL_DIALOG"));
        super.helpBtn = new JButton("Help");
        super.helpBtn.setToolTipText(GuiResources.getTooltipText("SCREEN_HELP"));
    }

    public void skipStep() throws ZkmException, IOException {
        super.completed = true;
        this.closeFrame();
        super.dialogCallback.onDialogResult_v(super.trimOptions, 4);
    }
}
