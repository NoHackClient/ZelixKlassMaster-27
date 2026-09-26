package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public class TrimOptionsAction extends AbstractAction {
    public final DeleteAttributesDialog deleteAttributesDialog;

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            if (this.deleteAttributesDialog.isEnabled()) {
                this.deleteAttributesDialog.onCancel();
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }

    public TrimOptionsAction(DeleteAttributesDialog deleteAttributesDialog1) {
        this.deleteAttributesDialog = deleteAttributesDialog1;
    }
}
