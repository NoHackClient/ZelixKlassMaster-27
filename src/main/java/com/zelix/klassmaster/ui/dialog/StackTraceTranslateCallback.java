package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.CallbackAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StackTraceTranslateCallback extends CallbackAdapter {
    public final StackTraceTranslateDialog translateDialog;

    public void onChangeLogsSelected(List list1, String string) {
        if (list1 != null && string != null) {
            StackTraceTranslateDialog.accessSetChangeLogFiles(this.translateDialog, new ArrayList(list1));
            StackTraceTranslateDialog.accessLogFileNameField(this.translateDialog).setText(string);
            StackTraceTranslateDialog.accessLogFileNameField(this.translateDialog).setCaretPosition(0);
        }
    }

    @Override
    public void onDialogResult(Object object, Object object1) {
        this.onChangeLogsSelected((List) object, (String) object1);
    }

    public StackTraceTranslateCallback(StackTraceTranslateDialog stackTraceTranslateDialog) {
        this.translateDialog = stackTraceTranslateDialog;
    }

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
    }
}
