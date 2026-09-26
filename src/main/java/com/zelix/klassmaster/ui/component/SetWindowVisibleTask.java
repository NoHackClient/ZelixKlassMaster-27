


package com.zelix.klassmaster.ui.component;

import java.awt.Window;

public class SetWindowVisibleTask
        implements Runnable {
    public final Window window;
    public final boolean visible;

    public SetWindowVisibleTask(Window window) {
        this.window = window;
        this.visible = true;
    }

    @Override
    public void run() {
        this.window.setVisible(this.visible);
    }
}
