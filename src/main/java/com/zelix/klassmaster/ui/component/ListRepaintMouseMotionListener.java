package com.zelix.klassmaster.ui.component;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class ListRepaintMouseMotionListener implements MouseMotionListener {
    public final PlatformAwareJList list;

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        if (PlatformAwareJList.isRepaintWorkaroundEnabled(this.list) && PlatformAwareJList.incrementMouseEventCount(this.list) % 30 == 0) {
            this.list.repaint();
        }
    }

    public ListRepaintMouseMotionListener(PlatformAwareJList platformAwareJList) {
        this.list = platformAwareJList;
    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {
        if (PlatformAwareJList.isRepaintWorkaroundEnabled(this.list) && PlatformAwareJList.incrementMouseEventCount(this.list) % 30 == 0) {
            this.list.repaint();
        }
    }
}
