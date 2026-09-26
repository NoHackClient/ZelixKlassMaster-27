package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.CallbackAdapter;

import java.io.IOException;

public class ExclusionParamAddCallback extends CallbackAdapter {
    public final ParameterAddCallback parentCallback;

    @Override
    public void onDialogResult(Object object) throws ZkmException, IOException {
        this.addParameter((String) object);
    }

    public ExclusionParamAddCallback(ParameterAddCallback parameterAddCallback) {
        this.parentCallback = parameterAddCallback;
    }

    public void addParameter(String string) {
        this.parentCallback.parameterListDialog.addParameter(string);
        this.parentCallback.parameterListDialog.onParametersChanged();
        this.parentCallback.parameterListDialog.paramList.ensureIndexIsVisible(this.parentCallback.parameterListDialog.paramListModel.getSize() - 1);
        this.parentCallback.parameterListDialog.toFront();
    }

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        this.parentCallback.parameterListDialog.toFront();
    }
}
