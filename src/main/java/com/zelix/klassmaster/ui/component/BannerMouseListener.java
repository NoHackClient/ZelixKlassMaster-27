package com.zelix.klassmaster.ui.component;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BannerMouseListener extends MouseAdapter {
    public final BannerPnl bannerPanel;

    public BannerMouseListener(BannerPnl bannerPnl1) {
        this.bannerPanel = bannerPnl1;
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        this.bannerPanel.dispatchEvent(mouseEvent);
    }
}
