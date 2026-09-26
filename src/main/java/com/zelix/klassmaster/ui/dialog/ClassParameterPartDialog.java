package com.zelix.klassmaster.ui.dialog;

import com.zelix.GuiResources;
import com.zelix.klassmaster.ui.component.RenameFilterModel;
import com.zelix.klassmaster.util.DialogCallback;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class ClassParameterPartDialog extends ExcludeParameterDialogBase {
    public PackageNameParamPanel packagePanel;
    public ExcludeParamTabPanel classPanel;

    @Override
    public final void showHelp() {
        if (super.exclusionType == 1) {
            GuiResources.showHelpTopic("028");
        } else {
            GuiResources.showHelpTopic("022");
        }
    }

    @Override
    public void buildTabs() {
        boolean bl1;
        int bb;
        if (super.exclusionType == 1) {
            bl1 = true;
            bb = super.exclusionType;
        } else {
            bl1 = false;
            bb = super.exclusionType;
        }

        int ba = bb;
        boolean bl = bl1;
        RenameFilterModel renameFilterModel = super.filterModel;
        ClassParameterPartDialog classParameterPartDialog1 = this;
        this.packagePanel = new PackageNameParamPanel(classParameterPartDialog1, renameFilterModel, bl, ba);
        this.classPanel = new ClassSpecifierPanel(this, super.filterModel, false, super.exclusionType);
        JTabbedPane jTabbedPane = super.tabbedPane;
        PackageNameParamPanel packageNameParamPanel1 = this.packagePanel;
        String string = GuiResources.getTooltipText("CONTAINING_PACKAGE_PART_OF_PARAM");
        PackageNameParamPanel packageNameParamPanel = packageNameParamPanel1;
        jTabbedPane.addTab("Containing package", (Icon) null, packageNameParamPanel, string);
        jTabbedPane = super.tabbedPane;
        ExcludeParamTabPanel excludeParamTabPanel1 = this.classPanel;
        String string1 = GuiResources.getTooltipText("CLASS_PART_OF_PARAM");
        ExcludeParamTabPanel excludeParamTabPanel = excludeParamTabPanel1;
        jTabbedPane.addTab("Class", (Icon) null, excludeParamTabPanel, string1);
        super.tabbedPane.setSelectedComponent(this.classPanel);
    }

    public ClassParameterPartDialog(JFrame jFrame, String string, RenameFilterModel renameFilterModel, int ba, DialogCallback dialogCallback1) {
        super(jFrame, string, renameFilterModel, ba, dialogCallback1);
    }
}
