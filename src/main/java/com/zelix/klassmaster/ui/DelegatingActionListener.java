package com.zelix.klassmaster.ui;

import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DelegatingActionListener implements ActionListener {
    public PropertyEditListener editListener;

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            this.editListener.onEditAction(actionEvent);
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public DelegatingActionListener(PropertyEditListener propertyEditListener) {
        this.editListener = propertyEditListener;
    }
}
