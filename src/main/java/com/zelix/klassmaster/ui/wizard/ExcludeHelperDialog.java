package com.zelix.klassmaster.ui.wizard;

import com.zelix.GuiResources;
import com.zelix.klassmaster.config.ExclusionWizardSettings;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.rename.RootPackageNode;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.ui.ZkmMainWindow;
import com.zelix.klassmaster.ui.component.ConstraintLayout;
import com.zelix.klassmaster.ui.dialog.HelperDialogMarker;
import com.zelix.klassmaster.ui.dialog.PackageExclusionWizardDialog;
import com.zelix.klassmaster.util.DialogCallback;

import java.awt.Container;
import java.io.IOException;
import java.util.Vector;
import javax.swing.JButton;

public class ExcludeHelperDialog extends PackageExclusionWizardDialog implements HelperDialogMarker {
    public static String[] layoutConstraints = new String[]{
            "layout.minWidth=btnWidth*4+25",
            "layout.minHeight=250",
            "mainPnl.left=5",
            "mainPnl.top=5",
            "mainPnl.right=container.right-5",
            "mainPnl.bottom=container.bottom-btnHeight-extraPnl.defaultHeight-20",
            "extraPnl.left=5",
            "extraPnl.top=mainPnl.bottom+5",
            "extraPnl.right=container.right-5",
            "btnWidth=max(previousBtn.defaultWidth, okBtn.defaultWidth, cancelBtn.defaultWidth, helpBtn.defaultWidth)",
            "btnHeight=max(previousBtn.defaultHeight, okBtn.defaultHeight, cancelBtn.defaultHeight, helpBtn.defaultHeight)",
            "previousBtn.top=extraPnl.bottom+5",
            "previousBtn.centerX=container.width*12/100",
            "previousBtn.width=btnWidth",
            "previousBtn.height=btnHeight",
            "okBtn.top=previousBtn.top",
            "okBtn.centerX=container.width*37/100",
            "okBtn.width=btnWidth",
            "okBtn.height=btnHeight",
            "cancelBtn.top=previousBtn.top",
            "cancelBtn.centerX=container.width*62/100",
            "cancelBtn.width=btnWidth",
            "cancelBtn.height=btnHeight",
            "helpBtn.top=previousBtn.top",
            "helpBtn.centerX=container.width*87/100",
            "helpBtn.width=btnWidth",
            "helpBtn.height=btnHeight"
    };
    public JButton previousBtn;

    public final void goPrevious() throws ZkmException, IOException {
        if (this.canAccept()) {
            super.closedByButton = true;
            this.closeFrame();
            this.saveParameters();
            super.callback.onDialogResult(2);
        }
    }

    public ExcludeHelperDialog(
            ZkmMainWindow zkmMainWindow,
            ExclusionWizardSettings exclusionWizardSettings,
            RootPackageNode rootPackageNode1,
            Vector vector,
            ScriptEnvironment scriptEnvironment1,
            DialogCallback dialogCallback1
    ) throws ZkmException, IOException {
        super("ZKM Script Helper - \"exclude\" Statement", zkmMainWindow, exclusionWizardSettings, rootPackageNode1, vector, scriptEnvironment1, dialogCallback1);
    }

    @Override
    public void layoutButtons(ConstraintLayout constraintLayout1, Container container1) {
        this.previousBtn = new JButton("Previous");
        this.previousBtn.setToolTipText(GuiResources.getTooltipText("PREVIOUS_SCREEN"));
        super.okBtn = new JButton("Next");
        super.okBtn.setToolTipText(GuiResources.getTooltipText("OK_EXCLUDE"));
        super.cancelBtn = new JButton("Cancel");
        super.cancelBtn.setToolTipText(GuiResources.getTooltipText("CANCEL_DIALOG"));
        super.helpBtn = new JButton("Help");
        super.helpBtn.setToolTipText(GuiResources.getTooltipText("SCREEN_HELP"));
        ExclusionWizardButtonListener exclusionWizardButtonListener = new ExclusionWizardButtonListener(this);
        this.previousBtn.addActionListener(exclusionWizardButtonListener);
        super.okBtn.addActionListener(exclusionWizardButtonListener);
        super.cancelBtn.addActionListener(exclusionWizardButtonListener);
        super.helpBtn.addActionListener(exclusionWizardButtonListener);
        ExcludeHelperKeyListener excludeHelperKeyListener = new ExcludeHelperKeyListener(this);
        this.previousBtn.addKeyListener(excludeHelperKeyListener);
        super.okBtn.addKeyListener(excludeHelperKeyListener);
        super.cancelBtn.addKeyListener(excludeHelperKeyListener);
        super.helpBtn.addKeyListener(excludeHelperKeyListener);
        container1.add(this.previousBtn, "previousBtn");
        container1.add(super.okBtn, "okBtn");
        container1.add(super.cancelBtn, "cancelBtn");
        container1.add(super.helpBtn, "helpBtn");
        constraintLayout1.setConstraints(layoutConstraints);
    }
}
