package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.classfile.hierarchy.ZkmClasspath;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.CallbackAdapter;

import java.io.IOException;

public class BuildHelperClasspathCallback extends CallbackAdapter {
    public final BuildHelperWizard wizard;

    public void onClasspathResult(ZkmClasspath zkmClasspath, Integer integer) {
        BuildHelperWizard.accessOnClasspathResult(this.wizard, zkmClasspath, integer);
    }

    public BuildHelperClasspathCallback(BuildHelperWizard buildHelperWizard) {
        this.wizard = buildHelperWizard;
    }

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        this.wizard.finishWizard();
    }

    @Override
    public void onDialogResult_v(Object object, Object object1) throws ZkmException, IOException {
        this.onClasspathResult((ZkmClasspath) object, (Integer) object1);
    }
}
