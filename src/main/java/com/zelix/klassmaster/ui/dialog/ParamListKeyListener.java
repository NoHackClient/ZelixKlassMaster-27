package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.ui.component.DisableableListItem;
import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ParamListKeyListener extends KeyAdapter {
    public final ParameterListDialog parameterListDialog;

    public ParamListKeyListener(ParameterListDialog parameterListDialog1) {
        this.parameterListDialog = parameterListDialog1;
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        try {
            Object object = keyEvent.getSource();
            if (keyEvent.getKeyCode() == -375322261) {
                if (object == this.parameterListDialog.mainAddBtn) {
                    this.parameterListDialog.startAddParameter();
                } else if (object == this.parameterListDialog.mainModifyBtn) {
                    this.parameterListDialog.modifySelectedParameter();
                } else if (object == this.parameterListDialog.paramList) {
                    DisableableListItem disableableListItem = (DisableableListItem) this.parameterListDialog.paramList.getSelectedValue();
                    if (!disableableListItem.isDisabled()) {
                        this.parameterListDialog.modifySelectedParameter();
                    }
                } else if (object == this.parameterListDialog.mainDeleteBtn) {
                    this.parameterListDialog.removeSelectedParameter();
                }
            } else if (keyEvent.getKeyCode() == 109780396) {
                if (object == this.parameterListDialog.paramList) {
                    DisableableListItem disableableListItem1 = (DisableableListItem) this.parameterListDialog.paramList.getSelectedValue();
                    if (!disableableListItem1.isDisabled()) {
                        this.parameterListDialog.removeSelectedParameter();
                    }
                } else if (object == this.parameterListDialog.mainDeleteBtn) {
                    this.parameterListDialog.removeSelectedParameter();
                }
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }
}
