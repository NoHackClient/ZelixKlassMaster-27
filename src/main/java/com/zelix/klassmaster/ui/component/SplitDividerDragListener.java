package com.zelix.klassmaster.ui.component;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class SplitDividerDragListener extends MouseMotionAdapter {
    public final SplitPaneDivider divider;

    public SplitDividerDragListener(SplitPaneDivider splitPaneDivider) {
        this.divider = splitPaneDivider;
    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        this.divider.dragging = true;
        this.divider.splitLayout.onDividerDragged(mouseEvent.getPoint());
    }
}
