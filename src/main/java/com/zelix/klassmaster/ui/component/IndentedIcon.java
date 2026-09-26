package com.zelix.klassmaster.ui.component;

import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;

public class IndentedIcon implements Icon {
    public Icon icon = null;
    public int indentLevel = 0;

    @Override
    public int getIconWidth() {
        return this.icon == null ? 0 : this.icon.getIconWidth() + this.indentLevel * 10;
    }

    @Override
    public void paintIcon(Component component1, Graphics graphics, int ba, int bb) {
        if (this.icon != null) {
            this.icon.paintIcon(component1, graphics, ba + this.indentLevel * 10, bb);
        }
    }

    @Override
    public int getIconHeight() {
        return this.icon == null ? 0 : this.icon.getIconHeight();
    }
}
