package com.zelix.klassmaster.util;

import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public interface DialogCallback {
    void onDialogCancelled() throws ZkmException, IOException;

    void onDialogResult(Object object, Object object1, Object object2, Object object3, Object object4, Object object5) throws ZkmException, IOException;

    void onDialogResult(Object object) throws ZkmException, IOException;

    void onDialogResult_v(Object object, Object object1) throws ZkmException, IOException;

    void onDialogResult(Object object, Object object1, Object object2, Object object3, Object object4, Object object5, Object object6, Object object7) throws ZkmException, IOException;

    void onDialogResult(Object object, Object object1);
}
