package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.classfile.hierarchy.ZkmClasspath;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.CallbackAdapter;

import java.io.IOException;

public class StackTraceClasspathCallback extends CallbackAdapter {
    public final StackTraceTranslateDialog translateDialog;

    public void onClasspathSelected(ZkmClasspath zkmClasspath) {
        if (zkmClasspath != null) {
            ZkmClasspath zkmClasspath1 = zkmClasspath;
            this.translateDialog.bytecodePathFld.setText(zkmClasspath1.getClasspath());
            this.translateDialog.bytecodePathFld.setCaretPosition(0);
        }
    }

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
    }

    @Override
    public void onDialogResult_v(Object object, Object object1) throws ZkmException, IOException {
        this.onClasspathSelected((ZkmClasspath) object);
    }

    public StackTraceClasspathCallback(StackTraceTranslateDialog stackTraceTranslateDialog) {
        this.translateDialog = stackTraceTranslateDialog;
    }
}
