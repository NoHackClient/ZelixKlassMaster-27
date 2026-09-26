package com.zelix.klassmaster.ui.component;

import java.awt.Component;

public class FocusRequestRunnable implements Runnable {
    public final Component targetComponent;

    @Override
    public void run() {
        SwingUtils.requestFocusNow(this.targetComponent);
    }

    public FocusRequestRunnable(Component component1) {
        this.targetComponent = component1;
    }
}
