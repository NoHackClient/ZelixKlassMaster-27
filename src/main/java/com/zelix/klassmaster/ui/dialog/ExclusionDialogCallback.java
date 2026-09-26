package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.CallbackAdapter;

import java.io.IOException;

public class ExclusionDialogCallback extends CallbackAdapter {
    public final TrimExclusionsBaseDialog dialog;

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        this.dialog.showDefaultCursor();
    }

    public ExclusionDialogCallback(TrimExclusionsBaseDialog trimExclusionsBaseDialog) {
        this.dialog = trimExclusionsBaseDialog;
    }
}
