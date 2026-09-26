package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ExclusionListButtonListener implements ActionListener {
    public final ParameterListDialog dialog;

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            Object object = actionEvent.getSource();
            if (object == this.dialog.mainAddBtn) {
                this.dialog.startAddParameter();
            } else if (object == this.dialog.mainModifyBtn) {
                this.dialog.modifySelectedParameter();
            } else if (object == this.dialog.mainDeleteBtn) {
                this.dialog.removeSelectedParameter();
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }

    public ExclusionListButtonListener(ParameterListDialog parameterListDialog1) {
        this.dialog = parameterListDialog1;
    }
}
