package com.zelix.klassmaster.ui.dialog;

import com.zelix.GuiResources;
import com.zelix.klassmaster.ui.component.RenameFilterModel;
import com.zelix.klassmaster.util.DialogCallback;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class MethodSpecifierDialog extends ExcludeParameterDialogBase {
    public ExcludeParamTabPanel classPanel;
    public PackageNameParamPanel packagePanel;
    public MethodSpecifierPanel methodPanel;

    @Override
    public void buildTabs() {
        boolean bl3;
        int bd;
        if (super.exclusionType == 1) {
            bl3 = true;
            bd = super.exclusionType;
        } else {
            bl3 = false;
            bd = super.exclusionType;
        }

        int ba = bd;
        boolean bl = bl3;
        RenameFilterModel renameFilterModel = super.filterModel;
        MethodSpecifierDialog methodSpecifierDialog1 = this;
        int bb = ba;
        boolean bl1 = bl;
        RenameFilterModel renameFilterModel1 = renameFilterModel;
        MethodSpecifierDialog methodSpecifierDialog2 = methodSpecifierDialog1;
        this.packagePanel = new PackageNameParamPanel(methodSpecifierDialog2, renameFilterModel1, bl1, bb);
        if (super.exclusionType != 3) {
            bl3 = true;
            bd = super.exclusionType;
        } else {
            bl3 = false;
            bd = super.exclusionType;
        }

        ba = bd;
        bl = bl3;
        renameFilterModel = super.filterModel;
        methodSpecifierDialog1 = this;
        int bc = ba;
        boolean bl2 = bl;
        RenameFilterModel renameFilterModel2 = renameFilterModel;
        MethodSpecifierDialog methodSpecifierDialog3 = methodSpecifierDialog1;
        this.classPanel = new ClassSpecifierPanel(methodSpecifierDialog3, renameFilterModel2, bl2, bc);
        this.methodPanel = new MethodSpecifierPanel(this, super.filterModel, super.exclusionType);
        JTabbedPane jTabbedPane = super.tabbedPane;
        PackageNameParamPanel packageNameParamPanel1 = this.packagePanel;
        String string = GuiResources.getTooltipText("CONTAINING_PACKAGE_PART_OF_PARAM");
        PackageNameParamPanel packageNameParamPanel = packageNameParamPanel1;
        jTabbedPane.addTab("Containing package", (Icon) null, packageNameParamPanel, string);
        jTabbedPane = super.tabbedPane;
        ExcludeParamTabPanel excludeParamTabPanel1 = this.classPanel;
        String string1 = GuiResources.getTooltipText("CONTAINING_CLASS_PART_OF_PARAM");
        ExcludeParamTabPanel excludeParamTabPanel = excludeParamTabPanel1;
        jTabbedPane.addTab("Containing class", (Icon) null, excludeParamTabPanel, string1);
        jTabbedPane = super.tabbedPane;
        MethodSpecifierPanel methodSpecifierPanel1 = this.methodPanel;
        String string2 = GuiResources.getTooltipText("METHOD_PART_OF_PARAM");
        MethodSpecifierPanel methodSpecifierPanel = methodSpecifierPanel1;
        jTabbedPane.addTab("Method", (Icon) null, methodSpecifierPanel, string2);
        super.tabbedPane.setSelectedComponent(this.methodPanel);
    }

    @Override
    public final void showHelp() {
        if (super.exclusionType == 1) {
            GuiResources.showHelpTopic("030");
        } else if (super.exclusionType == 2) {
            GuiResources.showHelpTopic("024");
        } else if (super.exclusionType == 3) {
            GuiResources.showHelpTopic("044");
        }
    }

    public MethodSpecifierDialog(JFrame jFrame, String string, RenameFilterModel renameFilterModel, int ba, DialogCallback dialogCallback1) {
        super(jFrame, string, renameFilterModel, ba, dialogCallback1);
    }
}
