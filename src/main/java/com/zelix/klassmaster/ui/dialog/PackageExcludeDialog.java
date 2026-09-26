package com.zelix.klassmaster.ui.dialog;

import com.zelix.GuiResources;
import com.zelix.klassmaster.ui.component.RenameFilterModel;
import com.zelix.klassmaster.util.DialogCallback;

import javax.swing.JFrame;

public class PackageExcludeDialog extends ExcludeParameterDialogBase {
    public PackageNameParamPanel packagePanel;

    public PackageExcludeDialog(JFrame jFrame, RenameFilterModel renameFilterModel, DialogCallback dialogCallback1) {
        super(jFrame, "Zelix KlassMaster Package Exclude Parameter", renameFilterModel, 1, dialogCallback1);
    }

    @Override
    public final void showHelp() {
        GuiResources.showHelpTopic("027");
    }

    @Override
    public void buildTabs() {
        this.packagePanel = new PackageNameParamPanel(this, super.filterModel, false, super.exclusionType);
        super.tabbedPane.addTab("Package", this.packagePanel);
    }
}
