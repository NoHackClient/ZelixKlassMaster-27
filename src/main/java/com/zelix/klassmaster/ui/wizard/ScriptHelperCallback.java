package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.classfile.hierarchy.ZkmClasspath;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.CallbackAdapter;

import java.io.IOException;

public class ScriptHelperCallback extends CallbackAdapter {
    public final ScriptHelperWizard wizard;

    @Override
    public void onDialogResult_v(Object object, Object object1) throws ZkmException, IOException {
        this.onClasspathResult((ZkmClasspath) object, (Integer) object1);
    }

    public void onClasspathResult(ZkmClasspath zkmClasspath, Integer integer) {
        ScriptHelperWizard.accessOnClasspathResult(this.wizard, zkmClasspath, integer);
    }

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        this.wizard.finishWizard();
    }

    public ScriptHelperCallback(ScriptHelperWizard scriptHelperWizard) {
        this.wizard = scriptHelperWizard;
    }
}
