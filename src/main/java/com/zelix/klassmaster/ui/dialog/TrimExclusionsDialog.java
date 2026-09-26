package com.zelix.klassmaster.ui.dialog;

import com.zelix.GuiResources;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.config.TrimExcludeSettings;
import com.zelix.klassmaster.config.TrimOptions;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.ui.ZkmMainWindow;
import com.zelix.klassmaster.ui.component.ConstraintLayout;
import com.zelix.klassmaster.util.DialogCallback;

import java.awt.Container;
import java.io.IOException;
import java.util.List;
import javax.swing.JButton;

public class TrimExclusionsDialog extends TrimExclusionsBaseDialog {
    public static String[] layoutConstraints = new String[]{
            "layout.minWidth=btnWidth*6+35",
            "layout.minHeight=250",
            "mainPnl.left=5",
            "mainPnl.top=5",
            "mainPnl.right=container.right-5",
            "mainPnl.bottom=container.bottom-btnHeight-extraPnl.defaultHeight-20",
            "extraPnl.left=5",
            "extraPnl.top=mainPnl.bottom+5",
            "extraPnl.right=container.right-5",
            "btnWidth=max(okBtn.defaultWidth, testBtn.defaultWidth, cancelBtn.defaultWidth, helpBtn.defaultWidth)",
            "btnHeight=max(, okBtn.defaultHeight, testBtn.defaultHeight, cancelBtn.defaultHeight, helpBtn.defaultHeight)",
            "okBtn.top=extraPnl.bottom+5",
            "okBtn.centerX=container.width*12/100",
            "okBtn.width=btnWidth",
            "okBtn.height=btnHeight",
            "testBtn.top=okBtn.top",
            "testBtn.centerX=container.width*37/100",
            "testBtn.width=btnWidth",
            "testBtn.height=btnHeight",
            "cancelBtn.top=okBtn.top",
            "cancelBtn.centerX=container.width*62/100",
            "cancelBtn.width=btnWidth",
            "cancelBtn.height=btnHeight",
            "helpBtn.top=okBtn.top",
            "helpBtn.centerX=container.width*87/100",
            "helpBtn.width=btnWidth",
            "helpBtn.height=btnHeight"
    };

    @Override
    public void layoutButtons(ConstraintLayout constraintLayout1, Container container1) {
        super.okBtn = new JButton("Next");
        super.okBtn.setToolTipText(GuiResources.getTooltipText("OK_TRIM_EXCLUDE"));
        super.testBtn = new JButton("Test");
        super.testBtn.setToolTipText(GuiResources.getTooltipText("TEST_TRIM_EXCLUDE"));
        super.cancelBtn = new JButton("Cancel");
        super.cancelBtn.setToolTipText(GuiResources.getTooltipText("CANCEL_DIALOG"));
        super.helpBtn = new JButton("Help");
        super.helpBtn.setToolTipText(GuiResources.getTooltipText("SCREEN_HELP"));
        TrimExcludeDialogListener trimExcludeDialogListener = new TrimExcludeDialogListener(this);
        super.okBtn.addActionListener(trimExcludeDialogListener);
        super.testBtn.addActionListener(trimExcludeDialogListener);
        super.cancelBtn.addActionListener(trimExcludeDialogListener);
        super.helpBtn.addActionListener(trimExcludeDialogListener);
        TrimExclusionsKeyListener trimExclusionsKeyListener = new TrimExclusionsKeyListener(this);
        super.okBtn.addKeyListener(trimExclusionsKeyListener);
        super.testBtn.addKeyListener(trimExclusionsKeyListener);
        super.cancelBtn.addKeyListener(trimExclusionsKeyListener);
        super.helpBtn.addKeyListener(trimExclusionsKeyListener);
        container1.add(super.okBtn, "okBtn");
        container1.add(super.testBtn, "testBtn");
        container1.add(super.cancelBtn, "cancelBtn");
        container1.add(super.helpBtn, "helpBtn");
        constraintLayout1.setConstraints(layoutConstraints);
    }

    public TrimExclusionsDialog(
            ZkmMainWindow zkmMainWindow,
            List list1,
            TrimExcludeSettings trimExcludeSettings1,
            ClassRepository classRepository1,
            TrimOptions trimOptions1,
            ScriptEnvironment scriptEnvironment1,
            DialogCallback dialogCallback1
    ) throws ZkmException, IOException {
        super(
                "Zelix KlassMaster - Trim Exclusions", zkmMainWindow, list1, trimExcludeSettings1, classRepository1, trimOptions1, scriptEnvironment1, dialogCallback1
        );
    }
}
