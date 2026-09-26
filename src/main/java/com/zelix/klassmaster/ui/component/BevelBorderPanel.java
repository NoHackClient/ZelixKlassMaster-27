package com.zelix.klassmaster.ui.component;

import javax.swing.BorderFactory;

public class BevelBorderPanel extends ObservingPanel {
    @Override
    public void removeAll() {
        super.removeAll();
        this.repaint();
    }

    public BevelBorderPanel(boolean bl) {
        if (bl) {
            this.setBorder(BorderFactory.createRaisedBevelBorder());
        } else {
            this.setBorder(BorderFactory.createLoweredBevelBorder());
        }
    }
}
