package com.zelix.klassmaster.ui.dialog;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class CollapsePackagesCheckListener implements ItemListener {
    public final ObfuscateOptionsDialog dialog;

    public CollapsePackagesCheckListener(ObfuscateOptionsDialog obfuscateOptionsDialog1) {
        this.dialog = obfuscateOptionsDialog1;
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        if (itemEvent.getSource() == this.dialog.collapsePackagesChk) {
            this.dialog.defaultCollapsePackageTxt.setEnabled(this.dialog.collapsePackagesChk.isSelected());
            if (!this.dialog.collapsePackagesChk.isSelected()) {
                this.dialog.defaultCollapsePackageTxt.select(0, 0);
            }
        }
    }
}
