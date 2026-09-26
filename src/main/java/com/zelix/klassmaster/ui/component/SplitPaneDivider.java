package com.zelix.klassmaster.ui.component;

import java.awt.Cursor;
import java.awt.SystemColor;
import javax.swing.JPanel;

public class SplitPaneDivider extends JPanel {
    public Cursor savedCursor;
    public boolean dragging = false;
    public SplitLayout splitLayout;

    public void updateResizeCursor() {
        if (this.splitLayout.orientation == 0) {
            if (this.splitLayout.splitRatio < 0.5) {
                Cursor cursor1 = new Cursor(11);
                this.setCursor(cursor1);
            } else {
                Cursor cursor2 = new Cursor(10);
                this.setCursor(cursor2);
            }
        } else if (this.splitLayout.splitRatio < 0.5) {
            Cursor cursor3 = new Cursor(9);
            this.setCursor(cursor3);
        } else {
            Cursor cursor4 = new Cursor(8);
            this.setCursor(cursor4);
        }
    }

    public SplitPaneDivider(SplitLayout splitLayout1) {
        this.splitLayout = splitLayout1;
        this.setBackground(SystemColor.control.darker());
        this.addMouseListener(new SplitterMouseListener(this));
        this.addMouseMotionListener(new SplitDividerDragListener(this));
    }
}
