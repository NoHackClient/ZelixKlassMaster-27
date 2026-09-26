package com.zelix.klassmaster.ui.component;

import javax.swing.ListModel;

public class SelfRenderingList extends PlatformAwareJList {
    public void installRenderer() {
        this.setCellRenderer(new SelfRenderingCellRenderer(this));
    }

    public SelfRenderingList(ListModel listModel1) {
        super(listModel1);
        this.installRenderer();
    }
}
