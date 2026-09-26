package com.zelix.klassmaster.ui.component;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ListRolloverListener implements MouseListener {
    public final PlatformAwareJList list;

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
        if (PlatformAwareJList.isRepaintWorkaroundEnabled(this.list)) {
            this.list.repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
    }

    public ListRolloverListener(PlatformAwareJList platformAwareJList) {
        this.list = platformAwareJList;
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
        if (PlatformAwareJList.isRepaintWorkaroundEnabled(this.list)) {
            this.list.repaint();
        }
    }
}
