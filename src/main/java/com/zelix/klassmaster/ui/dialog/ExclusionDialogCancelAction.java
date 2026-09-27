package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public class ExclusionDialogCancelAction extends AbstractAction {
    public final ParameterListDialog dialog;

    public ExclusionDialogCancelAction(ParameterListDialog parameterListDialog1) {
        this.dialog = parameterListDialog1;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            if (this.isEnabled()) {
                this.dialog.cancelDialog();
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }
}
