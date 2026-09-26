package com.zelix.klassmaster.ui.component;

import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public interface EdtCallback {
    void handleChangeOnEdt(Object object, Object object1, Object object2, Object object3) throws ZkmException, IOException;
}
