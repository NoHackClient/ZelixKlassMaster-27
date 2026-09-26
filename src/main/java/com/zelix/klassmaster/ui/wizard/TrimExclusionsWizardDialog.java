package com.zelix.klassmaster.ui.wizard;

import com.zelix.GuiResources;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.config.TrimExcludeSettings;
import com.zelix.klassmaster.config.TrimOptions;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.ui.ZkmMainWindow;
import com.zelix.klassmaster.ui.component.ConstraintLayout;
import com.zelix.klassmaster.ui.dialog.HelperDialogMarker;
import com.zelix.klassmaster.ui.dialog.TrimExclusionsBaseDialog;
import com.zelix.klassmaster.util.DialogCallback;

import java.awt.Container;
import java.io.IOException;
import java.util.List;
import javax.swing.JButton;

public class TrimExclusionsWizardDialog extends TrimExclusionsBaseDialog implements HelperDialogMarker {
    public JButton skipBtn;
    public JButton previousBtn;
    public static String[] layoutConstraints = new String[]{
            "layout.minWidth=btnWidth*6+35",
            "layout.minHeight=250",
            "mainPnl.left=5",
            "mainPnl.top=5",
            "mainPnl.right=container.right-5",
            "mainPnl.bottom=container.bottom-btnHeight-extraPnl.defaultHeight-20",
            "extraPnl.left=5",
            "extraPnl.top=mainPnl.bottom+10",
            "extraPnl.right=container.right-5",
            "btnWidth=max(helpBtn.defaultWidth, previousBtn.defaultWidth, okBtn.defaultWidth, testBtn.defaultWidth, skipBtn.defaultWidth, cancelBtn.defaultWidth)",
            "btnHeight=max(helpBtn.defaultHeight, previousBtn.defaultHeight, okBtn.defaultHeight, testBtn.defaultHeight, skipBtn.defaultHeight, cancelBtn.defaultHeight)",
            "previousBtn.top=extraPnl.bottom+5",
            "previousBtn.centerX=container.width*10/100",
            "previousBtn.width=btnWidth",
            "previousBtn.height=btnHeight",
            "okBtn.top=previousBtn.top",
            "okBtn.centerX=container.width*26/100",
            "okBtn.width=btnWidth",
            "okBtn.height=btnHeight",
            "testBtn.top=previousBtn.top",
            "testBtn.centerX=container.width*42/100",
            "testBtn.width=btnWidth",
            "testBtn.height=btnHeight",
            "skipBtn.top=previousBtn.top",
            "skipBtn.centerX=container.width*58/100",
            "skipBtn.width=btnWidth",
            "skipBtn.height=btnHeight",
            "cancelBtn.top=previousBtn.top",
            "cancelBtn.centerX=container.width*74/100",
            "cancelBtn.width=btnWidth",
            "cancelBtn.height=btnHeight",
            "helpBtn.top=previousBtn.top",
            "helpBtn.centerX=container.width*90/100",
            "helpBtn.width=btnWidth",
            "helpBtn.height=btnHeight"
    };

    @Override
    public void layoutButtons(ConstraintLayout constraintLayout1, Container container1) {
        this.previousBtn = new JButton("Previous");
        this.previousBtn.setToolTipText(GuiResources.getTooltipText("PREVIOUS_SCREEN"));
        super.okBtn = new JButton("Next");
        super.okBtn.setToolTipText(GuiResources.getTooltipText("OK_TRIM_EXCLUDE"));
        super.testBtn = new JButton("Test");
        super.testBtn.setToolTipText(GuiResources.getTooltipText("TEST_TRIM_EXCLUDE"));
        this.skipBtn = new JButton("Skip");
        this.skipBtn.setToolTipText(GuiResources.getTooltipText("SKIP_TO_NEXT"));
        super.cancelBtn = new JButton("Cancel");
        super.cancelBtn.setToolTipText(GuiResources.getTooltipText("CANCEL_DIALOG"));
        super.helpBtn = new JButton("Help");
        super.helpBtn.setToolTipText(GuiResources.getTooltipText("SCREEN_HELP"));
        TrimExclusionsButtonListener trimExclusionsButtonListener = new TrimExclusionsButtonListener(this);
        this.previousBtn.addActionListener(trimExclusionsButtonListener);
        super.okBtn.addActionListener(trimExclusionsButtonListener);
        super.testBtn.addActionListener(trimExclusionsButtonListener);
        this.skipBtn.addActionListener(trimExclusionsButtonListener);
        super.cancelBtn.addActionListener(trimExclusionsButtonListener);
        super.helpBtn.addActionListener(trimExclusionsButtonListener);
        BuildHelperTrimExclKeyListener buildHelperTrimExclKeyListener = new BuildHelperTrimExclKeyListener(this);
        this.previousBtn.addKeyListener(buildHelperTrimExclKeyListener);
        super.okBtn.addKeyListener(buildHelperTrimExclKeyListener);
        super.testBtn.addKeyListener(buildHelperTrimExclKeyListener);
        this.skipBtn.addKeyListener(buildHelperTrimExclKeyListener);
        super.cancelBtn.addKeyListener(buildHelperTrimExclKeyListener);
        super.helpBtn.addKeyListener(buildHelperTrimExclKeyListener);
        container1.add(this.previousBtn, "previousBtn");
        container1.add(super.okBtn, "okBtn");
        container1.add(super.testBtn, "testBtn");
        container1.add(this.skipBtn, "skipBtn");
        container1.add(super.cancelBtn, "cancelBtn");
        container1.add(super.helpBtn, "helpBtn");
        constraintLayout1.setConstraints(layoutConstraints);
    }

    public TrimExclusionsWizardDialog(
            ZkmMainWindow zkmMainWindow,
            List list1,
            TrimExcludeSettings trimExcludeSettings1,
            ClassRepository classRepository1,
            TrimOptions trimOptions1,
            ScriptEnvironment scriptEnvironment1,
            DialogCallback dialogCallback1
    ) throws ZkmException, IOException {
        super("Build Helper - Trim Exclusions", zkmMainWindow, list1, trimExcludeSettings1, classRepository1, trimOptions1, scriptEnvironment1, dialogCallback1);
    }

    public final void goToPreviousStep() throws ZkmException, IOException {
        super.closedByButton = true;
        this.closeFrame();
        this.saveParameters();
        super.callback.onDialogResult(2);
    }

    public void skipStep() throws ZkmException, IOException {
        super.closedByButton = true;
        this.closeFrame();
        super.callback.onDialogResult(4);
    }
}
