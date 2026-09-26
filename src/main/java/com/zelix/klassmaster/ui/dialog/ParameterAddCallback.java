package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.CallbackAdapter;

import java.io.IOException;

public class ParameterAddCallback extends CallbackAdapter {
    public final ParameterListDialog parameterListDialog;

    @Override
    public void onDialogResult(Object object) throws ZkmException, IOException {
        this.onParameterTypeChosen((String) object);
    }

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
    }

    public void onParameterTypeChosen(String string) throws ZkmException, IOException {
        ExclusionParamAddCallback exclusionParamAddCallback = new ExclusionParamAddCallback(this);
        ExclusionParamAddCallback exclusionParamAddCallback1 = exclusionParamAddCallback;
        this.parameterListDialog.openParameterEditor(string, (String) null, exclusionParamAddCallback1);
    }

    public ParameterAddCallback(ParameterListDialog parameterListDialog1) {
        this.parameterListDialog = parameterListDialog1;
    }
}
