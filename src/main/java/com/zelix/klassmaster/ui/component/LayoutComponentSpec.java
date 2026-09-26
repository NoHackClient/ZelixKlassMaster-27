package com.zelix.klassmaster.ui.component;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;

public class LayoutComponentSpec implements LayoutConstraintMarker {
    public Point cachedLocation;
    public int bottomPad;
    public Dimension cachedSize;
    public int rightPad;
    public LayoutExpression[] preferredSizeExpressions;
    public LayoutExpression[] expressions = new LayoutExpression[10];
    public Integer[] values = new Integer[10];
    public String componentName;
    public Component component;
    public ConstraintLayout layout;

    public int getBottomPad() {
        return this.bottomPad;
    }

    public boolean isBoundsResolved() {
        return this.values[0] != null && this.values[1] != null && this.values[3] != null && this.values[2] != null;
    }

    public boolean areExpressionsResolved(boolean bl) {
        int ba = 0;
        int bb = 0;

        for (byte bc = 10; bb < bc; bc = 10) {
            LayoutExpression layoutExpression = this.expressions[ba];
            if (layoutExpression != null && !layoutExpression.tryEvaluate(bl)) {
                return false;
            }

            bb = ++ba;
        }

        return true;
    }

    public void evaluateProperty(int ba, LayoutExpression[] layoutExpressions, boolean[] bl, boolean bl1) {
        if (this.values[ba] == null) {
            LayoutExpression layoutExpression = layoutExpressions[ba];
            if (layoutExpression != null && layoutExpression.tryEvaluate(bl1)) {
                this.values[ba] = layoutExpression.getValue();
                bl[0] = true;
            }
        }
    }

    public int getRightPad() {
        return this.rightPad;
    }

    public void reset() {
        this.cachedSize = null;
        this.cachedLocation = null;
        int ba = 0;
        int bc = 0;

        for (byte bd = 10; bc < bd; bd = 10) {
            this.values[ba] = null;
            bc = ++ba;
        }

        ba = 0;
        bc = 0;

        for (byte bb = 10; bc < bb; bb = 10) {
            LayoutExpression layoutExpression = this.expressions[ba];
            if (layoutExpression != null) {
                layoutExpression.reset();
            }

            bc = ++ba;
        }
    }

    public String toDebugString() {
        StringBuffer stringBuffer = new StringBuffer(this.componentName + " : {");
        int ba = 0;
        int bc = 0;

        for (byte bd = 10; bc < bd; bd = 10) {
            LayoutExpression layoutExpression = this.expressions[ba];
            if (layoutExpression != null) {
                stringBuffer.append(" " + ConstraintLayout.getPropertyName(ba) + "=" + layoutExpression.getExpressionText());
            }

            bc = ++ba;
        }

        stringBuffer.append("} {");
        ba = 0;
        bc = 0;

        for (byte bb = 10; bc < bb; bb = 10) {
            stringBuffer.append(" " + ConstraintLayout.getPropertyName(ba) + "=" + this.values[ba]);
            bc = ++ba;
        }

        stringBuffer.append("}");
        return stringBuffer.toString();
    }

    public String getComponentName() {
        return this.componentName;
    }

    public LayoutComponentSpec(String string, Component component1, ConstraintLayout constraintLayout1) {
        this.componentName = string;
        this.component = component1;
        this.layout = constraintLayout1;
    }

    public int getPropertyValue(int ba) {
        if (ba == 10) {
            return this.component.getPreferredSize().width;
        } else {
            return ba == 11 ? this.component.getPreferredSize().height : this.values[ba];
        }
    }

    public Point getBottomRight() {
        return new Point(this.values[5], this.values[4]);
    }

    public boolean isPropertyResolved(int ba) {
        return ba == 10 || ba == 11 ? true : this.values[ba] != null;
    }

    public void deriveProperty(int ba, LayoutExpression[] layoutExpressions, boolean[] bl) {
        if (layoutExpressions[ba] == null && this.values[ba] == null) {
            int bb = Integer.MIN_VALUE;
            switch (ba) {
                case 0:
                    if (layoutExpressions[3] != null && layoutExpressions[5] != null) {
                        if (this.values[3] != null && this.values[5] != null) {
                            bb = this.values[5] - this.values[3];
                        }
                    } else if (layoutExpressions[3] != null && layoutExpressions[6] != null) {
                        if (this.values[3] != null && this.values[6] != null) {
                            bb = (this.values[6] - this.values[3]) * 2;
                        }
                    } else {
                        Component component1;
                        if (layoutExpressions[6] != null) {
                            if (layoutExpressions[5] != null) {
                                if (this.values[6] != null && this.values[5] != null) {
                                    bb = (this.values[5] - this.values[6]) * 2;
                                }
                                break;
                            }

                            component1 = this.component;
                        } else {
                            component1 = this.component;
                        }

                        bb = component1.getPreferredSize().width;
                        LayoutExpression layoutExpression1 = this.expressions[8];
                        if (layoutExpression1 != null) {
                            bb = Math.max(bb, layoutExpression1.getValue());
                        }
                    }
                    break;
                case 1:
                    if (layoutExpressions[4] != null && layoutExpressions[2] != null) {
                        if (this.values[4] != null && this.values[2] != null) {
                            bb = this.values[4] - this.values[2];
                        }
                    } else if (layoutExpressions[2] != null && layoutExpressions[7] != null) {
                        if (this.values[7] != null && this.values[2] != null) {
                            bb = (this.values[7] - this.values[2]) * 2;
                        }
                    } else {
                        Component component2;
                        if (layoutExpressions[7] != null) {
                            if (layoutExpressions[4] != null) {
                                if (this.values[7] != null && this.values[4] != null) {
                                    bb = (this.values[4] - this.values[7]) * 2;
                                }
                                break;
                            }

                            component2 = this.component;
                        } else {
                            component2 = this.component;
                        }

                        bb = component2.getPreferredSize().height;
                        LayoutExpression layoutExpression = this.expressions[9];
                        if (layoutExpression != null) {
                            bb = Math.max(bb, layoutExpression.getValue());
                        }
                    }
                    break;
                case 2:
                    if (layoutExpressions[7] == null && layoutExpressions[4] == null) {
                        bb = 0;
                    } else {
                        if (this.values[1] == null) {
                            return;
                        }

                        int bh = this.values[1];
                        if (this.values[4] != null) {
                            bb = this.values[4] - bh;
                        } else if (this.values[7] != null) {
                            bb = this.values[7] - bh / 2;
                        }
                    }
                    break;
                case 3:
                    if (layoutExpressions[6] == null && layoutExpressions[5] == null) {
                        bb = 0;
                    } else {
                        if (this.values[0] == null) {
                            return;
                        }

                        int bg = this.values[0];
                        if (this.values[5] != null) {
                            bb = this.values[5] - bg;
                        } else if (this.values[6] != null) {
                            bb = this.values[6] - bg / 2;
                        }
                    }
                    break;
                case 4:
                    if (this.values[1] == null) {
                        return;
                    }

                    int bc = this.values[1];
                    if (this.values[2] != null) {
                        bb = this.values[2] + bc;
                    } else if (this.values[7] != null) {
                        bb = this.values[7] + bc / 2;
                    }
                    break;
                case 5:
                    if (this.values[0] == null) {
                        return;
                    }

                    int bd = this.values[0];
                    if (this.values[3] != null) {
                        bb = this.values[3] + bd;
                    } else if (this.values[6] != null) {
                        bb = this.values[6] + bd / 2;
                    }
                    break;
                case 6:
                    if (this.values[0] == null) {
                        return;
                    }

                    int bf = this.values[0];
                    if (this.values[3] != null) {
                        bb = this.values[3] + bf / 2;
                    } else if (this.values[5] != null) {
                        bb = this.values[5] - bf / 2;
                    }
                    break;
                case 7:
                    if (this.values[1] == null) {
                        return;
                    }

                    int be = this.values[1];
                    if (this.values[2] != null) {
                        bb = this.values[2] + be / 2;
                    } else if (this.values[4] != null) {
                        bb = this.values[4] - be / 2;
                    }
            }

            if (bb != Integer.MIN_VALUE) {
                bl[0] = true;
                this.values[ba] = bb;
            }
        }
    }

    public Point getLocation() {
        return new Point(this.values[3], this.values[2]);
    }

    public String validateExpressions() {
        this.cachedSize = null;
        this.cachedLocation = null;
        int ba = 0;
        int bb = 0;

        for (byte bc = 10; bb < bc; bc = 10) {
            LayoutExpression layoutExpression = this.expressions[ba];
            if (layoutExpression != null) {
                String string = layoutExpression.prepare();
                if (string != null) {
                    return string;
                }
            }

            bb = ++ba;
        }

        return null;
    }

    public boolean resolveProperties(boolean bl, LayoutExpression[] layoutExpressions) {
        boolean[] bl1 = new boolean[]{false};
        int ba = 0;
        int bd = 0;

        for (byte be = 10; bd < be; be = 10) {
            this.evaluateProperty(ba, layoutExpressions, bl1, bl);
            bd = ++ba;
        }

        boolean bl4 = false;

        for (boolean bl2 = true; bl2; bl4 = bl2 ? true : bl4) {
            boolean[] bl3 = new boolean[]{false};
            int bb = 0;
            bd = 0;

            for (byte bc = 8; bd < bc; bc = 8) {
                this.deriveProperty(bb, layoutExpressions, bl3);
                bd = ++bb;
            }

            bl2 = bl3[0];
        }

        return bl1[0] || bl4;
    }

    public String setProperty(String string, String string1) {
        int ba = ConstraintLayout.getPropertyIndex(string);
        this.expressions[ba] = new LayoutExpression(this.layout, string1);
        return null;
    }

    public boolean resolve(boolean bl) {
        return this.resolveProperties(bl, this.expressions);
    }

    public Dimension getSize() {
        return new Dimension(this.values[0], this.values[1]);
    }

    public boolean resolvePreferredSize() {
        if (this.preferredSizeExpressions == null) {
            this.preferredSizeExpressions = new LayoutExpression[10];
            System.arraycopy(this.expressions, 0, this.preferredSizeExpressions, 0, 10);
            int ba = 0;
            int bb = 0;

            for (byte bc = 10; bb < bc; bc = 10) {
                LayoutExpression layoutExpression = this.preferredSizeExpressions[ba];
                if (layoutExpression != null && !layoutExpression.isResolved()) {
                    if (layoutExpression.isContainerSizeRelative()) {
                        this.preferredSizeExpressions[ba] = null;
                    } else if (layoutExpression.isContainerTrailingEdge()) {
                        if (layoutExpression.getContainerPropertyIndex() == 5) {
                            this.rightPad = layoutExpression.getPaddingValue();
                        } else {
                            this.bottomPad = layoutExpression.getPaddingValue();
                        }

                        this.preferredSizeExpressions[ba] = null;
                    } else if (layoutExpression.isContainerLeadingEdge()) {
                        layoutExpression.tryEvaluate(true);
                    }
                }

                bb = ++ba;
            }
        }

        boolean bl = false;

        while (this.resolveProperties(false, this.preferredSizeExpressions)) {
            bl = true;
        }

        return bl;
    }
}
