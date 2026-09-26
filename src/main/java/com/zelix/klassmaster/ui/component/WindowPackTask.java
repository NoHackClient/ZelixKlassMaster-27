package com.zelix.klassmaster.ui.component;

import java.awt.Window;

public class WindowPackTask implements Runnable {
    public final Window window;

    @Override
    public void run() {
        this.window.pack();
    }

    public WindowPackTask(Window window1) {
        this.window = window1;
    }
}
