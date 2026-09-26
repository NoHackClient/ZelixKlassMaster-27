package com.zelix.klassmaster.ui.component;

import java.awt.Component;
import java.util.StringTokenizer;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ZkmScrollPane extends JScrollPane implements ChangeListener {
    public static final String JAVA_VENDOR = System.getProperty("java.vendor", "Sun");
    public static final String JAVA_VERSION = System.getProperty("java.version");
    public static final String OS_NAME = System.getProperty("os.name");
    public boolean repaintOnScroll;
    public Component viewComponent;

    public ZkmScrollPane(Component component1) {
        super(component1);
        this.viewComponent = component1;
        this.installViewportListener();
    }

    @Override
    public void stateChanged(ChangeEvent changeEvent) {
        if (this.viewComponent != null) {
            this.viewComponent.repaint();
        }
    }

    public static boolean needsRepaintWorkaround() {
        if (JAVA_VENDOR.indexOf("Sun") != -1 && OS_NAME.indexOf("Windows") != -1) {
            StringTokenizer stringTokenizer = new StringTokenizer(JAVA_VERSION, ".");
            int ba = 0;

            while (stringTokenizer.hasMoreTokens()) {
                String string = stringTokenizer.nextToken();
                switch (ba) {
                    case 0:
                        try {
                            int bc = Integer.parseInt(string);
                            if (bc > 1) {
                                return false;
                            }
                        } catch (NumberFormatException numberFormatException1) {
                            return false;
                        }
                    default:
                        ba++;
                        break;
                    case 1:
                        try {
                            int bb = Integer.parseInt(string);
                            if (bb == 3) {
                                return true;
                            }

                            return false;
                        } catch (NumberFormatException numberFormatException) {
                            return false;
                        }
                }
            }
        }

        return false;
    }

    public void installViewportListener() {
        this.repaintOnScroll = needsRepaintWorkaround();
        this.getViewport().addChangeListener(this);
    }
}
