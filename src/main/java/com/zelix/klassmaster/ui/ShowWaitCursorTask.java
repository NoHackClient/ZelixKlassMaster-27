package com.zelix.klassmaster.ui;

public class ShowWaitCursorTask implements Runnable {
    public final ZkmMainWindow mainWindow;

    @Override
    public void run() {
        ZkmMainWindow.applyWaitCursorOn(this.mainWindow);
    }

    public ShowWaitCursorTask(ZkmMainWindow zkmMainWindow) {
        this.mainWindow = zkmMainWindow;
    }
}
