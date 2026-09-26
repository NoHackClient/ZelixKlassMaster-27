package com.zelix.klassmaster.ui;

public class RestoreCursorTask implements Runnable {
    public final ZkmMainWindow mainWindow;

    public RestoreCursorTask(ZkmMainWindow zkmMainWindow) {
        this.mainWindow = zkmMainWindow;
    }

    @Override
    public void run() {
        this.mainWindow.applyDefaultCursor();
    }
}
