package com.zelix.klassmaster.ui.component;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import javax.swing.JPanel;

public class ImagePnl extends JPanel {
    public Image image;
    public boolean primaryImageLoaded;
    public int imageWidth;
    public int imageHeight;

    public ImagePnl(Image image1, Image image2) {
        this.image = image1;
        this.waitForImage();
        this.primaryImageLoaded = true;
        ImagePnl imagePnl2;
        Image image3;
        if (!this.prepareImage(this.image, this)) {
            this.image = image2;
            this.primaryImageLoaded = false;
            this.waitForImage();
            this.prepareImage(this.image, this);
            imagePnl2 = this;
            image3 = this.image;
        } else {
            imagePnl2 = this;
            image3 = this.image;
        }

        imagePnl2.imageWidth = image3.getWidth(this);
        this.imageHeight = this.image.getHeight(this);
        this.repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(this.imageWidth, this.imageHeight);
    }

    public boolean isPrimaryImageLoaded() {
        return this.primaryImageLoaded;
    }

    @Override
    public Dimension getMinimumSize() {
        return this.getPreferredSize();
    }

    @Override
    public void update(Graphics graphics) {
        int imageHeight = this.imageHeight;
        int imageWidth = this.imageWidth;
        graphics.drawImage(this.image, 0, 0, imageWidth, imageHeight, this);
    }

    public void waitForImage() {
        MediaTracker mediaTracker = new MediaTracker(this);
        mediaTracker.addImage(this.image, 0);

        try {
            mediaTracker.waitForID(0);
        } catch (InterruptedException interruptedException) {
        }
    }

    @Override
    public void paint(Graphics graphics) {
        this.update(graphics);
    }
}
