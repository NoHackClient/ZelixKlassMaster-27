package com.zelix.klassmaster.ui.dialog;

import com.zelix.GuiResources;
import com.zelix.klassmaster.ui.component.RenameFilterModel;
import com.zelix.klassmaster.util.DialogCallback;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class ClassSuffixExcludeDialog extends ExcludeParameterDialogBase {
    public PackageNameParamPanel packagePanel;
    public ClassNameSpecifierPanel classNamePanel;

    @Override
    public void buildTabs() {
        this.packagePanel = new PackageNameParamPanel(this, super.filterModel, false, super.exclusionType);
        this.classNamePanel = new ClassNameSpecifierPanel(this, super.filterModel, super.exclusionType);
        JTabbedPane jTabbedPane = super.tabbedPane;
        PackageNameParamPanel packageNameParamPanel1 = this.packagePanel;
        String string = GuiResources.getTooltipText("CONTAINING_PACKAGE_PART_OF_PARAM");
        PackageNameParamPanel packageNameParamPanel = packageNameParamPanel1;
        jTabbedPane.addTab("Containing package", (Icon) null, packageNameParamPanel, string);
        jTabbedPane = super.tabbedPane;
        ClassNameSpecifierPanel classNameSpecifierPanel1 = this.classNamePanel;
        String string1 = GuiResources.getTooltipText("CLASS_SUFFIX_PART_OF_PARAM");
        ClassNameSpecifierPanel classNameSpecifierPanel = classNameSpecifierPanel1;
        jTabbedPane.addTab("Class", (Icon) null, classNameSpecifierPanel, string1);
        super.tabbedPane.setSelectedComponent(this.classNamePanel);
    }

    public ClassSuffixExcludeDialog(JFrame jFrame, RenameFilterModel renameFilterModel, DialogCallback dialogCallback1) {
        super(jFrame, "Zelix KlassMaster Class Suffix Exclude Parameter", renameFilterModel, 1, dialogCallback1);
    }

    @Override
    public final void showHelp() {
        GuiResources.showHelpTopic("031");
    }
}
