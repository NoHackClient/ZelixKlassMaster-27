package com.zelix.klassmaster.ui;

import com.zelix.klassmaster.exceptions.ZkmException;

import java.awt.event.ActionEvent;
import java.io.IOException;

public interface PropertyEditListener {
    void onEditAction(ActionEvent actionEvent) throws ZkmException, IOException;

    void onEditTextChanged(String string);
}
