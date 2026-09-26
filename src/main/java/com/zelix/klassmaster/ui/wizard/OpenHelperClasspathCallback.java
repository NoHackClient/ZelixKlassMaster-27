package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.CallbackAdapter;

import java.io.IOException;

public class OpenHelperClasspathCallback extends CallbackAdapter {
    public final OpenDialogHelper openHelper;

    public OpenHelperClasspathCallback(OpenDialogHelper openDialogHelper) {
        this.openHelper = openDialogHelper;
    }

    public void onClasspathResult(Integer integer) {
        OpenDialogHelper.accessOnClasspathResult(this.openHelper, integer);
    }

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        this.openHelper.finishHelper();
    }

    @Override
    public void onDialogResult_v(Object object1, Object object) throws ZkmException, IOException {
        Integer integer = (Integer) object;
        this.onClasspathResult(integer);
    }
}
