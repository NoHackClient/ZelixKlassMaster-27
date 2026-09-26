package com.zelix.klassmaster.ui;

import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainWindowCloseListener extends WindowAdapter {
    public final ZkmMainWindow mainWindow;

    public MainWindowCloseListener(ZkmMainWindow zkmMainWindow) {
        this.mainWindow = zkmMainWindow;
    }

    @Override
    public void windowClosing(WindowEvent windowEvent) {
        try {
            this.mainWindow.closeFrame();
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }

    @Override
    public void windowClosed(WindowEvent windowEvent) {
        System.gc();
    }
}
