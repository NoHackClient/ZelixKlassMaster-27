package com.zelix.klassmaster.ui.component;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.Point;

public class SplitLayout implements LayoutManager2 {
    public Dimension preferredSize;
    public Component secondComponent;
    public SplitPaneDivider divider;
    public Container container;
    public Component firstComponent;
    public int orientation = 0;
    public float splitRatio;
    public int dividerThickness;
    public boolean continuousLayout;

    @Override
    public Dimension preferredLayoutSize(Container container1) {
        return this.preferredSize != null ? this.preferredSize : new Dimension(100, 100);
    }

    public SplitLayout(int ba, int bb, int dividerThickness) {
        if (ba == 0) {
            this.orientation = 0;
        } else {
            if (ba != 1) {
                throw new IllegalArgumentException("Split type must be SplitLayout.VERTICAL or SplitLayout.HORIZONTAL");
            }

            this.orientation = 1;
        }

        byte bd = 100;
        this.splitRatio = bb / 100.0F;
        bd = 3;
        bd = 7;
        this.dividerThickness = dividerThickness;
        this.continuousLayout = false;
    }

    @Override
    public void layoutContainer(Container container1) {
        if (this.container == null) {
            this.container = container1;
        }

        if (this.divider == null) {
            this.divider = new SplitPaneDivider(this);
            this.container.add(this.divider, this);
        }

        Insets insets = container1.getInsets();
        Dimension dimension = this.getAvailableSize(container1, insets);
        if (this.orientation == 0) {
            int ba = Math.max(0, Math.round(dimension.width * this.splitRatio - this.dividerThickness));
            int bb = dimension.width - this.dividerThickness - ba;
            int bc = dimension.height;
            int bd = ba;
            this.firstComponent.setBounds(0, 0, bd, bc);
            int be = dimension.height;
            int dividerThickness = this.dividerThickness;
            this.divider.setBounds(ba, 0, dividerThickness, be);
            int bt = ba + this.dividerThickness;
            int bg = dimension.height;
            int bh = bb;
            this.secondComponent.setBounds(bt, 0, bh, bg);
        } else {
            int bq = Math.max(0, Math.round(dimension.height * this.splitRatio - this.dividerThickness));
            int br = dimension.height - this.dividerThickness - bq;
            int bi = bq;
            int bj = dimension.width;
            this.firstComponent.setBounds(0, 0, bj, bi);
            int bk = this.dividerThickness;
            int bl = dimension.width;
            int bm = bq;
            this.divider.setBounds(0, bm, bl, bk);
            int bs = bq + this.dividerThickness;
            int bn = br;
            int bo = dimension.width;
            int bp = bs;
            this.secondComponent.setBounds(0, bp, bo, bn);
        }
    }

    @Override
    public void invalidateLayout(Container container1) {
    }

    @Override
    public float getLayoutAlignmentX(Container container1) {
        return 0.5F;
    }

    public void moveDivider(Point point) {
        if (this.orientation == 0) {
            this.splitRatio = (float) (this.firstComponent.getSize().width + point.x) / this.container.getSize().width;
        } else {
            this.splitRatio = (float) (this.firstComponent.getSize().height + point.y) / this.container.getSize().height;
        }

        this.splitRatio = Math.max(0.0F, Math.min(1.0F, this.splitRatio));
        this.container.invalidate();
        this.container.validate();
    }

    @Override
    public void removeLayoutComponent(Component component1) {
        if (this.firstComponent == component1) {
            this.firstComponent = null;
        } else {
            if (this.secondComponent != component1) {
                throw new IllegalArgumentException("SplitLayout Error - Unknown Component");
            }

            this.secondComponent = null;
        }
    }

    @Override
    public Dimension maximumLayoutSize(Container container1) {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public void onDividerDragged(Point point) {
        if (this.continuousLayout) {
            this.moveDivider(point);
        }
    }

    @Override
    public float getLayoutAlignmentY(Container container1) {
        return 0.5F;
    }

    public Dimension getAvailableSize(Container container1, Insets insets) {
        Dimension dimension = container1.getSize();
        dimension.width = dimension.width - (insets.left + insets.left);
        dimension.height = dimension.height - (insets.top + insets.bottom);
        if (this.preferredSize != null) {
            int ba = Math.max(dimension.width, this.preferredSize.width);
            int bb = Math.max(dimension.height, this.preferredSize.height);
            return new Dimension(ba, bb);
        } else {
            return dimension;
        }
    }

    @Override
    public Dimension minimumLayoutSize(Container container1) {
        return this.preferredLayoutSize(container1);
    }

    public void onDividerReleased(Point point) {
        this.moveDivider(point);
    }

    public void addSplitComponent(Component component1) {
        if (this.firstComponent == null) {
            this.firstComponent = component1;
        } else if (this.firstComponent != component1 && this.secondComponent == null) {
            this.secondComponent = component1;
        } else if (this.secondComponent == component1) {
            throw new IllegalArgumentException("SplitLayout Error - More than two Components added");
        }
    }

    @Override
    public void addLayoutComponent(String string, Component component1) {
        this.addSplitComponent(component1);
    }

    @Override
    public void addLayoutComponent(Component component1, Object object) {
        if (object != this) {
            this.addSplitComponent(component1);
        }
    }
}
