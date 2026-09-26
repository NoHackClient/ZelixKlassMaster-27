package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.CallbackAdapter;

import java.io.IOException;

public class ParamDialogCallback extends CallbackAdapter {
    public final ParameterListDialog parameterListDialog;

    public void onParameterEdited(String string) throws ZkmException, IOException {
        this.parameterListDialog.updateEditedParameter(string);
        this.parameterListDialog.onParametersChanged();
        this.parameterListDialog.toFront();
    }

    @Override
    public void onDialogResult(Object object) throws ZkmException, IOException {
        this.onParameterEdited((String) object);
    }

    public ParamDialogCallback(ParameterListDialog parameterListDialog1) {
        this.parameterListDialog = parameterListDialog1;
    }

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        this.parameterListDialog.toFront();
    }
}
