package com.zelix.klassmaster.ui.dialog;

import com.zelix.GuiResources;
import com.zelix.klassmaster.config.PersistedStringList;
import com.zelix.klassmaster.config.ReferenceObfIncludeStore;
import com.zelix.klassmaster.exceptions.AssertionFailedException;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.ui.ZkmMainWindow;
import com.zelix.klassmaster.ui.component.ConstraintLayout;
import com.zelix.klassmaster.ui.component.RenameFilterModel;
import com.zelix.klassmaster.ui.component.SwingUtils;
import com.zelix.klassmaster.util.DialogCallback;

import java.awt.Container;
import java.io.IOException;
import java.util.Enumeration;
import javax.swing.JButton;

public class InclusionParametersDialog extends ParameterListDialog implements HelperDialogMarker {
    public static String noInclusionsHint = "You have selected \"obfuscate references\".\nYou now need to specify which references to obfuscate.\nOtherwise your selection will have no affect.\nClick on the <Add> button.";
    public static String[] paramListConstraints = new String[]{
            "mainParamList.top=5",
            "mainParamList.left=5",
            "mainParamList.right=container.right-btnWidth-10",
            "mainParamList.bottom=container.height*50/100",
            "explanationArea.left=5",
            "explanationArea.top=mainParamList.bottom+7",
            "explanationArea.right=container.right-5",
            "explanationArea.bottom=container.bottom-5",
            "btnWidth=max(mainAddBtn.defaultWidth, mainModifyBtn.defaultWidth, mainDeleteBtn.defaultWidth)",
            "mainAddBtn.width=btnWidth",
            "mainAddBtn.left=mainParamList.right+5",
            "mainAddBtn.centerY=container.height*20/200",
            "mainModifyBtn.width=btnWidth",
            "mainModifyBtn.left=mainAddBtn.left",
            "mainModifyBtn.centerY=container.height*50/200",
            "mainDeleteBtn.width=btnWidth",
            "mainDeleteBtn.left=mainAddBtn.left",
            "mainDeleteBtn.centerY=container.height*80/200"
    };
    public static String[] layoutConstraints = new String[]{
            "layout.minWidth=btnWidth*4+25",
            "layout.minHeight=250",
            "mainPnl.left=5",
            "mainPnl.top=5",
            "mainPnl.right=container.right-5",
            "mainPnl.bottom=container.bottom-btnHeight-20",
            "btnWidth=max(previousBtn.defaultWidth, okBtn.defaultWidth, cancelBtn.defaultWidth, helpBtn.defaultWidth)",
            "btnHeight=max(previousBtn.defaultHeight, okBtn.defaultHeight, cancelBtn.defaultHeight, helpBtn.defaultHeight)",
            "previousBtn.top=mainPnl.bottom+5",
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
    public ReferenceObfIncludeStore includeStore;

    public final void goPrevious() throws ZkmException, IOException {
        super.closedByButton = true;
        this.closeFrame();
        this.saveParameters();
        super.callback.onDialogResult(2);
    }

    @Override
    public void openParameterEditor(String string, String string1, DialogCallback dialogCallback1) throws ZkmException, IOException {
        String string2 = string1;
        if (string.equals("1")) {
            if (string2 == null) {
                string2 = "*.* *";
            }

            new FieldExcludeParameterDialog(this, "Zelix KlassMaster - Field Include Parameter", this.parseParameter(string2), 3, dialogCallback1);
        } else if (string.equals("2")) {
            if (string2 == null) {
                string2 = "*.* *(*)";
            }

            new MethodSpecifierDialog(this, "Zelix KlassMaster - Method Include Parameter", this.parseParameter(string2), 3, dialogCallback1);
        }
    }

    public InclusionParametersDialog(
            String string,
            ZkmMainWindow zkmMainWindow,
            ReferenceObfIncludeStore referenceObfIncludeStore,
            ScriptEnvironment scriptEnvironment1,
            DialogCallback dialogCallback1
    ) throws ZkmException, IOException {
        super(zkmMainWindow, scriptEnvironment1, dialogCallback1, 3);
        this.includeStore = referenceObfIncludeStore;
        this.initComponents(string);
        SwingUtils.requestFocusOnEdt(super.okBtn);
    }

    @Override
    public void openParameterTypeDialog(DialogCallback dialogCallback1) {
        new IncludeParamTypeDialog(this, dialogCallback1);
    }

    @Override
    public PersistedStringList getParameterStore() {
        return this.includeStore;
    }

    @Override
    public void layoutButtons(ConstraintLayout constraintLayout1, Container container1) {
        this.previousBtn = new JButton("Previous");
        super.okBtn = new JButton("Next");
        super.cancelBtn = new JButton("Cancel");
        super.helpBtn = new JButton("Help");
        InclusionParamsButtonListener inclusionParamsButtonListener = new InclusionParamsButtonListener(this);
        this.previousBtn.addActionListener(inclusionParamsButtonListener);
        super.okBtn.addActionListener(inclusionParamsButtonListener);
        super.cancelBtn.addActionListener(inclusionParamsButtonListener);
        super.helpBtn.addActionListener(inclusionParamsButtonListener);
        IncludeParamDialogEnterKeyListener includeParamDialogEnterKeyListener = new IncludeParamDialogEnterKeyListener(this);
        this.previousBtn.addKeyListener(includeParamDialogEnterKeyListener);
        super.okBtn.addKeyListener(includeParamDialogEnterKeyListener);
        super.cancelBtn.addKeyListener(includeParamDialogEnterKeyListener);
        super.helpBtn.addKeyListener(includeParamDialogEnterKeyListener);
        container1.add(this.previousBtn, "previousBtn");
        container1.add(super.okBtn, "okBtn");
        container1.add(super.cancelBtn, "cancelBtn");
        container1.add(super.helpBtn, "helpBtn");
        ConstraintLayout constraintLayout2;
        String[] strings;
        if (this.includeStore.getEntryCount() == 0) {
            super.explanationArea.setText(noInclusionsHint);
            constraintLayout2 = constraintLayout1;
            strings = layoutConstraints;
        } else {
            constraintLayout2 = constraintLayout1;
            strings = layoutConstraints;
        }

        constraintLayout2.setConstraints(strings);
    }

    @Override
    public String[] getParamListConstraints() {
        return paramListConstraints;
    }

    @Override
    public void editParameter(RenameFilterModel renameFilterModel, String string, DialogCallback dialogCallback1) throws ZkmException, IOException {
        switch (renameFilterModel.getParameterKind()) {
            case 3:
                this.openParameterEditor("1", string, dialogCallback1);
                break;
            case 4:
                this.openParameterEditor("2", string, dialogCallback1);
        }
    }

    @Override
    public void showHelp() {
        GuiResources.showHelpTopic("041");
    }

    @Override
    public void loadParameters(Object object) throws ZkmException, IOException {
        if (this.includeStore.isSaved()) {
            Enumeration enumeration = this.includeStore.enumerateEntries();

            while (enumeration.hasMoreElements()) {
                String string = (String) enumeration.nextElement();

                try {
                    RenameFilterModel renameFilterModel = this.parseParameter(string);
                    if (renameFilterModel.toScriptText().equals(string)) {
                        this.addParameter(string);
                    }
                } catch (AssertionFailedException assertionFailedException) {
                    throw assertionFailedException;
                } catch (Throwable throwable) {
                }
            }
        }
    }

    @Override
    public void initToolTips() {
        super.mainAddBtn.setToolTipText(GuiResources.getTooltipText("ADD_INCLUSION_PARAM"));
        super.mainModifyBtn.setToolTipText(GuiResources.getTooltipText("MODIFY_INCLUSION_PARAM"));
        super.mainDeleteBtn.setToolTipText(GuiResources.getTooltipText("DELETE_INCLUSION_PARAM"));
        super.paramList.setToolTipText(GuiResources.getTooltipText("CURRENT_INCLUSION_PARAMS"));
        super.explanationArea.setToolTipText(GuiResources.getTooltipText("EXPLAIN_SELECTED_INCLUSION_PARAM"));
        this.previousBtn.setToolTipText(GuiResources.getTooltipText("PREVIOUS_SCREEN"));
        super.okBtn.setToolTipText(GuiResources.getTooltipText("OK_INCLUDE"));
        super.cancelBtn.setToolTipText(GuiResources.getTooltipText("CANCEL_DIALOG"));
        super.helpBtn.setToolTipText(GuiResources.getTooltipText("SCREEN_HELP"));
    }

    public static JButton accessPreviousButton(InclusionParametersDialog inclusionParametersDialog1) {
        return inclusionParametersDialog1.previousBtn;
    }
}
