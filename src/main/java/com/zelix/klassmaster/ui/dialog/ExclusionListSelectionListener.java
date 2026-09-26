package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.ui.component.DisableableListItem;
import com.zelix.klassmaster.util.ZkmUtils;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ExclusionListSelectionListener implements ListSelectionListener {
    public final ParameterListDialog dialog;

    @Override
    public void valueChanged(ListSelectionEvent listSelectionEvent) {
        try {
            if (!this.dialog.removingItem) {
                if (this.dialog.paramList.getSelectedIndex() > -1) {
                    DisableableListItem disableableListItem = (DisableableListItem) this.dialog.paramList.getSelectedValue();
                    if (!disableableListItem.isDisabled()) {
                        this.dialog.mainModifyBtn.setEnabled(true);
                        this.dialog.mainDeleteBtn.setEnabled(true);
                    } else {
                        this.dialog.mainModifyBtn.setEnabled(false);
                        this.dialog.mainDeleteBtn.setEnabled(false);
                    }

                    String string = (String) disableableListItem.getItem();
                    String string1 = this.dialog.parseParameter(string).buildDescription(this.dialog.paramKind);
                    this.dialog.explanationArea.setText(string1);
                    this.dialog.explanationArea.setCaretPosition(0);
                }
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }

    public ExclusionListSelectionListener(ParameterListDialog parameterListDialog1) {
        this.dialog = parameterListDialog1;
    }
}
