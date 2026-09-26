package com.zelix.klassmaster.ui.component;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class BorderedPanel extends JPanel {
    public BorderedPanel(int ba, int bb) {
        this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(bb, ba, bb, ba)));
    }

    public BorderedPanel() {
        this(1, 1);
    }
}
