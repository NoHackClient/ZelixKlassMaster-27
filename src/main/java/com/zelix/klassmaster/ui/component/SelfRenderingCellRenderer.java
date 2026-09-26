package com.zelix.klassmaster.ui.component;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public class SelfRenderingCellRenderer extends DefaultListCellRenderer {
    public final SelfRenderingList list;

    public SelfRenderingCellRenderer(SelfRenderingList selfRenderingList) {
        this.list = selfRenderingList;
    }

    @Override
    public Component getListCellRendererComponent(JList jList, Object object, int ba, boolean bl, boolean bl1) {
        super.getListCellRendererComponent(jList, object, ba, bl, bl1);
        ((ListItemWrapper) object).applyRenderStyle(this, jList, bl);
        return this;
    }
}
