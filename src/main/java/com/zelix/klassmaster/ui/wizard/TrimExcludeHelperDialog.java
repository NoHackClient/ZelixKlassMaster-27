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
import com.zelix.klassmaster.ui.dialog.TrimExclusionsDialogBase;
import com.zelix.klassmaster.util.DialogCallback;

import java.awt.Container;
import java.io.IOException;
import java.util.Vector;
import javax.swing.JButton;

public class TrimExcludeHelperDialog extends TrimExclusionsDialogBase implements HelperDialogMarker {
    public JButton previousBtn;
    public static String[] layoutConstraints = new String[]{
            "layout.minWidth=btnWidth*5+30",
            "layout.minHeight=250",
            "mainPnl.left=5",
            "mainPnl.top=5",
            "mainPnl.right=container.right-5",
            "mainPnl.bottom=container.bottom-btnHeight-extraPnl.defaultHeight-20",
            "extraPnl.left=5",
            "extraPnl.top=mainPnl.bottom+10",
            "extraPnl.right=container.right-5",
            "btnWidth=max(previousBtn.defaultWidth, okBtn.defaultWidth, skipBtn.defaultWidth, cancelBtn.defaultWidth, helpBtn.defaultWidth)",
            "btnHeight=max(previousBtn.defaultHeight, okBtn.defaultHeight, skipBtn.defaultHeight, cancelBtn.defaultHeight, helpBtn.defaultHeight)",
            "previousBtn.top=extraPnl.bottom+5",
            "previousBtn.centerX=container.width*10/100",
            "previousBtn.width=btnWidth",
            "previousBtn.height=btnHeight",
            "okBtn.top=previousBtn.top",
            "okBtn.centerX=container.width*30/100",
            "okBtn.width=btnWidth",
            "okBtn.height=btnHeight",
            "skipBtn.top=previousBtn.top",
            "skipBtn.centerX=container.width*50/100",
            "skipBtn.width=btnWidth",
            "skipBtn.height=btnHeight",
            "cancelBtn.top=previousBtn.top",
            "cancelBtn.centerX=container.width*70/100",
            "cancelBtn.width=btnWidth",
            "cancelBtn.height=btnHeight",
            "helpBtn.top=previousBtn.top",
            "helpBtn.centerX=container.width*90/100",
            "helpBtn.width=btnWidth",
            "helpBtn.height=btnHeight"
    };
    public JButton skipBtn;

    @Override
    public void layoutButtons(ConstraintLayout constraintLayout1, Container container1) {
        this.previousBtn = new JButton("Previous");
        this.previousBtn.setToolTipText(GuiResources.getTooltipText("PREVIOUS_SCREEN"));
        super.okBtn = new JButton("Next");
        super.okBtn.setToolTipText(GuiResources.getTooltipText("OK_TRIM_EXCLUDE"));
        this.skipBtn = new JButton("Skip");
        this.skipBtn.setToolTipText(GuiResources.getTooltipText("SKIP_TO_NEXT"));
        super.cancelBtn = new JButton("Cancel");
        super.cancelBtn.setToolTipText(GuiResources.getTooltipText("CANCEL_DIALOG"));
        super.helpBtn = new JButton("Help");
        super.helpBtn.setToolTipText(GuiResources.getTooltipText("SCREEN_HELP"));
        TrimExcludeHelperButtonListener trimExcludeHelperButtonListener = new TrimExcludeHelperButtonListener(this);
        this.previousBtn.addActionListener(trimExcludeHelperButtonListener);
        super.okBtn.addActionListener(trimExcludeHelperButtonListener);
        this.skipBtn.addActionListener(trimExcludeHelperButtonListener);
        super.cancelBtn.addActionListener(trimExcludeHelperButtonListener);
        super.helpBtn.addActionListener(trimExcludeHelperButtonListener);
        ScriptTrimExcludeKeyListener scriptTrimExcludeKeyListener = new ScriptTrimExcludeKeyListener(this);
        this.previousBtn.addKeyListener(scriptTrimExcludeKeyListener);
        super.okBtn.addKeyListener(scriptTrimExcludeKeyListener);
        this.skipBtn.addKeyListener(scriptTrimExcludeKeyListener);
        super.cancelBtn.addKeyListener(scriptTrimExcludeKeyListener);
        super.helpBtn.addKeyListener(scriptTrimExcludeKeyListener);
        container1.add(this.previousBtn, "previousBtn");
        container1.add(super.okBtn, "okBtn");
        container1.add(this.skipBtn, "skipBtn");
        container1.add(super.cancelBtn, "cancelBtn");
        container1.add(super.helpBtn, "helpBtn");
        constraintLayout1.setConstraints(layoutConstraints);
    }

    public void goToPreviousStep() throws ZkmException, IOException {
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

    public TrimExcludeHelperDialog(
            ZkmMainWindow zkmMainWindow,
            Vector vector,
            TrimExcludeSettings trimExcludeSettings1,
            ClassRepository classRepository1,
            TrimOptions trimOptions1,
            ScriptEnvironment scriptEnvironment1,
            DialogCallback dialogCallback1
    ) throws ZkmException, IOException {
        super(
                "ZKM Script Helper - \"trimExclude\" Statement",
                zkmMainWindow,
                vector,
                trimExcludeSettings1,
                classRepository1,
                trimOptions1,
                scriptEnvironment1,
                dialogCallback1
        );
    }
}
