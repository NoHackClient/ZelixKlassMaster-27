package com.zelix.klassmaster.ui.component;

import com.zelix.klassmaster.ui.dialog.EscapeClosableFrame;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JList;
import javax.swing.UIManager;

public class DisableableListItem extends ListItemWrapper {
    public boolean disabled;

    @Override
    public void applyRenderStyle(Component component1, JList jList, boolean bl) {
        if (this.disabled) {
            Color color = component1.getForeground();
            Color color1 = component1.getBackground();
            Color color2 = jList.getSelectionForeground();
            Color color3 = jList.getSelectionBackground();
            Color color4 = UIManager.getColor("Button.disabledForeground");
            if (color4 != null
                    && color4 instanceof Color
                    && (bl || SwingUtils.hasStrongContrast(color, color4))
                    && (bl || SwingUtils.hasStrongContrast(color1, color4))
                    && (!bl || SwingUtils.hasStrongContrast(color2, color4))
                    && (!bl || SwingUtils.hasStrongContrast(color3, color4))) {
                component1.setForeground(color4);
                return;
            }

            color4 = UIManager.getColor("Menu.disabledForeground");
            if (color4 != null
                    && color4 instanceof Color
                    && (bl || SwingUtils.hasStrongContrast(color, color4))
                    && (bl || SwingUtils.hasStrongContrast(color1, color4))
                    && (!bl || SwingUtils.hasStrongContrast(color2, color4))
                    && (!bl || SwingUtils.hasStrongContrast(color3, color4))) {
                component1.setForeground(color4);
                return;
            }

            Color color5;
            if (!bl && (SwingUtils.isNearBlack(color) || SwingUtils.isNearWhite(color))
                    || bl && (SwingUtils.isNearBlack(color1) || SwingUtils.isNearWhite(color2))) {
                if (!bl) {
                    if (SwingUtils.isDark(color3)) {
                        color5 = Color.gray;
                    } else {
                        color5 = Color.lightGray;
                    }
                } else if (SwingUtils.isDark(color1)) {
                    color5 = Color.gray;
                } else {
                    color5 = Color.lightGray;
                }
            } else if (bl) {
                color5 = this.adjustBrightness(color2, color2);
            } else {
                color5 = this.adjustBrightness(color, color);
            }

            if (SwingUtils.hasStrongContrast(color1, color5)) {
                component1.setForeground(color5);
                return;
            }

            byte ba = 100;
            int bb = 0;

            do {
                if (bl) {
                    color5 = this.adjustBrightness(color5, color2);
                } else {
                    color5 = this.adjustBrightness(color5, color);
                }

                if ((bl || SwingUtils.hasContrast(color, color5, ba))
                        && (bl || SwingUtils.hasContrast(color1, color5, ba))
                        && (!bl || SwingUtils.hasContrast(color2, color5, ba))) {
                    if (!bl) {
                        component1.setForeground(color5);
                        return;
                    }

                    if (SwingUtils.hasContrast(color3, color5, ba)) {
                        component1.setForeground(color5);
                        return;
                    }
                }
            } while (bb++ < 3);
        }
    }

    public DisableableListItem(Object object, boolean disabled) {
        super(object);
        this.disabled = disabled;
    }

    @Override
    public boolean equals(Object object) {
        int controlValue = EscapeClosableFrame.getControlValue();
        boolean bl = object instanceof DisableableListItem;
        if (controlValue != 0) {
            if (bl) {
                return super.item.equals(((DisableableListItem) object).item);
            }

            bl = false;
        }

        return bl;
    }

    public boolean isDisabled() {
        return this.disabled;
    }

    public Color adjustBrightness(Color color, Color color1) {
        return !SwingUtils.isNearWhite(color1) ? color.brighter() : color.darker();
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
    }
}
