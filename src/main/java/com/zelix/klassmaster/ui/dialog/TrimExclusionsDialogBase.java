package com.zelix.klassmaster.ui.dialog;

import com.zelix.GuiResources;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.config.TrimExcludeSettings;
import com.zelix.klassmaster.config.TrimOptions;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.ui.ZkmMainWindow;
import com.zelix.klassmaster.ui.component.ClassListEntry;
import com.zelix.klassmaster.ui.component.RenameFilterModel;
import com.zelix.klassmaster.ui.component.SwingUtils;
import com.zelix.klassmaster.util.DialogCallback;

import java.awt.Container;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

public abstract class TrimExclusionsDialogBase extends ExclusionWizardDialogBase {
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
    public static String[] optionsConstraints = new String[]{
            "startSecondColumn=max(applicationTypeLbl.defaultWidth, excludeMainClassLbl.defaultWidth)+10",
            "applicationTypeLbl.centerY=applicationTypeComboBox.centerY",
            "applicationTypeLbl.left=5",
            "applicationTypeComboBox.top=5",
            "applicationTypeComboBox.left=startSecondColumn",
            "applicationTypeComboBox.right=container.right-5",
            "excludeMainClassLbl.left=5",
            "excludeMainClassLbl.centerY=mainClassComboBox.centerY",
            "mainClassComboBox.left=startSecondColumn",
            "mainClassComboBox.top=applicationTypeComboBox.bottom+5",
            "mainClassComboBox.right=container.right-5",
            "container.defaultRightPad=5",
            "container.defaultBottomPad=5"
    };
    public ClassRepository classRepository;
    public TrimOptions trimOptions;

    @Override
    public void openParameterTypeDialog(DialogCallback dialogCallback1) {
        new TrimExcludeTypeDialog(this, dialogCallback1);
    }

    public TrimExclusionsDialogBase(
            String string,
            ZkmMainWindow zkmMainWindow,
            List list1,
            TrimExcludeSettings trimExcludeSettings1,
            ClassRepository classRepository1,
            TrimOptions trimOptions1,
            ScriptEnvironment scriptEnvironment1,
            DialogCallback dialogCallback1
    ) throws ZkmException, IOException {
        super(zkmMainWindow, list1, trimExcludeSettings1, scriptEnvironment1, dialogCallback1, 2);
        this.initComponents(string);
        this.classRepository = classRepository1;
        this.trimOptions = trimOptions1;
        SwingUtils.requestFocusOnEdt(super.okBtn);
    }

    @Override
    public final String buildMainClassParameter(String string, boolean bl) {
        String string1 = null;
        if (string != null) {
            if (bl) {
                string1 = string + "^" + " " + ExclusionWizardDialogBase.mainMethodSignature;
            } else {
                string1 = string;
            }
        }

        return string1;
    }

    @Override
    public String[] getParamListConstraints() {
        return paramListConstraints;
    }

    @Override
    public String[] getExtraPanelLayoutSpec() {
        return optionsConstraints;
    }

    @Override
    public void editParameter(RenameFilterModel renameFilterModel, String string, DialogCallback dialogCallback1) throws ZkmException, IOException {
        switch (renameFilterModel.getParameterKind()) {
            case 2:
                this.openParameterEditor("2", string, dialogCallback1);
                break;
            case 3:
                this.openParameterEditor("3", string, dialogCallback1);
                break;
            case 4:
                this.openParameterEditor("4", string, dialogCallback1);
        }
    }

    @Override
    public final void showHelp() {
        GuiResources.showHelpTopic("020");
    }

    @Override
    public void openParameterEditor(String string, String string1, DialogCallback dialogCallback1) throws ZkmException, IOException {
        String string2 = string1;
        if (string.equals("2")) {
            if (string2 == null) {
                string2 = "*.*";
            }

            new ClassParameterPartDialog(this, "Zelix KlassMaster Trim Class Exclude Parameter", this.parseParameter(string2), 2, dialogCallback1);
        } else if (string.equals("3")) {
            if (string2 == null) {
                string2 = "*.* *";
            }

            new FieldExcludeParameterDialog(this, "Zelix KlassMaster Trim Field Exclude Parameter", this.parseParameter(string2), 2, dialogCallback1);
        } else if (string.equals("4")) {
            if (string2 == null) {
                string2 = "*.* *(*)";
            }

            new MethodSpecifierDialog(this, "Zelix KlassMaster Trim Method Exclude Parameter", this.parseParameter(string2), 2, dialogCallback1);
        }
    }

    @Override
    public boolean canAccept() throws ZkmException, IOException {
        if (super.paramIndexMap.size() == 0) {
            new MessageBoxDialog(this, "Error - No trim exclusion parameters", "If there are no Trim exclusion parameters then every class will be trimmed.");
            return false;
        }

        boolean bl = false;
        Enumeration enumeration = super.paramIndexMap.keyEnumeration();

        while (enumeration.hasMoreElements()) {
            String string = (String) enumeration.nextElement();
            if (this.parseParameter(string).isClassExcluded()) {
                bl = true;
            }
        }

        if (bl) {
            return true;
        }

        new MessageBoxDialog(
                this, "Error - No trim exclusion parameters exclude a class", "If no Trim exclusion parameters can exclude a class then every class will be trimmed."
        );
        return false;
    }

    @Override
    public final String getMainClassParameter(ClassListEntry classListEntry) {
        if (classListEntry != null) {
            String string = classListEntry.getClassName();
            return this.buildMainClassParameter(string, classListEntry.isApplication());
        } else {
            return null;
        }
    }

    @Override
    public void buildExtraComponents(Container container1) throws ZkmException, IOException {
        this.buildMainClassSelector(container1, "Application entry point class:", GuiResources.getTooltipText("TRIM_EXCLUDE_MAIN_CLASS_LIST"));
    }
}
