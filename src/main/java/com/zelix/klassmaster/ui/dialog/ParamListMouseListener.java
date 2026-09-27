package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.ui.component.DisableableListItem;
import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ParamListMouseListener extends MouseAdapter {
    public final ParameterListDialog parameterListDialog;

    public ParamListMouseListener(ParameterListDialog parameterListDialog1) {
        this.parameterListDialog = parameterListDialog1;
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        try {
            if (mouseEvent.getClickCount() == 2) {
                int ba = this.parameterListDialog.paramList.locationToIndex(mouseEvent.getPoint());
                if (ba > -1 && !((DisableableListItem) this.parameterListDialog.paramListModel.getElementAt(ba)).isDisabled()) {
                    this.parameterListDialog.modifySelectedParameter();
                }
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }
}
