package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ParamDialogWindowCloser extends WindowAdapter {
    public final ParameterListDialog parameterListDialog;

    @Override
    public void windowClosing(WindowEvent windowEvent) {
        try {
            this.parameterListDialog.closeFrame();
            this.parameterListDialog.callback.onDialogCancelled();
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }

    public ParamDialogWindowCloser(ParameterListDialog parameterListDialog1) {
        this.parameterListDialog = parameterListDialog1;
    }
}
