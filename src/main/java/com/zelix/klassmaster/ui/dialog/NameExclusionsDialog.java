package com.zelix.klassmaster.ui.dialog;

import com.zelix.GuiResources;
import com.zelix.klassmaster.config.ExclusionWizardSettings;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.rename.RootPackageNode;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.ui.ZkmMainWindow;
import com.zelix.klassmaster.ui.component.ConstraintLayout;
import com.zelix.klassmaster.util.DialogCallback;

import java.awt.Container;
import java.io.IOException;
import java.util.List;
import javax.swing.JButton;

public class NameExclusionsDialog extends PackageExclusionWizardDialog {
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
            "btnWidth=max(okBtn.defaultWidth, cancelBtn.defaultWidth, helpBtn.defaultWidth)",
            "btnHeight=max(okBtn.defaultHeight, cancelBtn.defaultHeight, helpBtn.defaultHeight)",
            "okBtn.top=extraPnl.bottom+5",
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

    @Override
    public void layoutButtons(ConstraintLayout constraintLayout1, Container container1) {
        super.okBtn = new JButton("Next");
        super.okBtn.setToolTipText(GuiResources.getTooltipText("OK_EXCLUDE"));
        super.cancelBtn = new JButton("Cancel");
        super.cancelBtn.setToolTipText(GuiResources.getTooltipText("CANCEL_DIALOG"));
        super.helpBtn = new JButton("Help");
        super.helpBtn.setToolTipText(GuiResources.getTooltipText("SCREEN_HELP"));
        ExcludeDialogActionListener excludeDialogActionListener = new ExcludeDialogActionListener(this);
        super.okBtn.addActionListener(excludeDialogActionListener);
        super.cancelBtn.addActionListener(excludeDialogActionListener);
        super.helpBtn.addActionListener(excludeDialogActionListener);
        ExcludeDialogKeyListener excludeDialogKeyListener = new ExcludeDialogKeyListener(this);
        super.okBtn.addKeyListener(excludeDialogKeyListener);
        super.cancelBtn.addKeyListener(excludeDialogKeyListener);
        super.helpBtn.addKeyListener(excludeDialogKeyListener);
        container1.add(super.okBtn, "okBtn");
        container1.add(super.cancelBtn, "cancelBtn");
        container1.add(super.helpBtn, "helpBtn");
        constraintLayout1.setConstraints(layoutConstraints);
    }

    public NameExclusionsDialog(
            String string,
            ZkmMainWindow zkmMainWindow,
            ExclusionWizardSettings exclusionWizardSettings,
            RootPackageNode rootPackageNode1,
            List list1,
            ScriptEnvironment scriptEnvironment1,
            DialogCallback dialogCallback1
    ) throws ZkmException, IOException {
        super(string, zkmMainWindow, exclusionWizardSettings, rootPackageNode1, list1, scriptEnvironment1, dialogCallback1);
    }
}
