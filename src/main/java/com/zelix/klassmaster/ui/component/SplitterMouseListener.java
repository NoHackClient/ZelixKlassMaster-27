package com.zelix.klassmaster.ui.component;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SplitterMouseListener extends MouseAdapter {
    public final SplitPaneDivider divider;

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        if (this.divider.dragging) {
            this.divider.dragging = false;
            this.divider.splitLayout.onDividerReleased(mouseEvent.getPoint());
        }
    }

    public SplitterMouseListener(SplitPaneDivider splitPaneDivider) {
        this.divider = splitPaneDivider;
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
        this.divider.savedCursor = this.divider.getCursor();
        this.divider.updateResizeCursor();
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
        if (this.divider.savedCursor == null) {
            this.divider.savedCursor = new Cursor(0);
        }

        this.divider.setCursor(this.divider.savedCursor);
    }
}
