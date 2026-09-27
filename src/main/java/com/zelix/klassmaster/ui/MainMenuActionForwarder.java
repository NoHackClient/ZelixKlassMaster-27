package com.zelix.klassmaster.ui;

import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenuActionForwarder implements ActionListener {
    public final ZkmMainWindow mainWindow;

    public MainMenuActionForwarder(ZkmMainWindow zkmMainWindow) {
        this.mainWindow = zkmMainWindow;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            this.mainWindow.handleMenuAction(actionEvent);
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }
}
