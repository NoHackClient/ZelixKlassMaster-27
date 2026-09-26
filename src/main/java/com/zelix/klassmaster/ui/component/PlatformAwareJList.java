package com.zelix.klassmaster.ui.component;

import java.util.StringTokenizer;
import javax.swing.JList;
import javax.swing.ListModel;

public class PlatformAwareJList extends JList {
    public static String javaVersion;
    public static String javaVendor;
    public static String osName;
    public boolean repaintWorkaround;
    public int mouseEventCount;

    public PlatformAwareJList() {
        this.installRepaintListeners();
    }

    public void installRepaintListeners() {
        this.repaintWorkaround = needsRepaintWorkaround();
        this.addMouseListener(new ListRolloverListener(this));
        this.addMouseMotionListener(new ListRepaintMouseMotionListener(this));
    }

    public PlatformAwareJList(ListModel listModel1) {
        super(listModel1);
        this.installRepaintListeners();
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    public static boolean isRepaintWorkaroundEnabled(PlatformAwareJList platformAwareJList) {
        return platformAwareJList.repaintWorkaround;
    }

    public static boolean needsRepaintWorkaround() {
        String string1;
        if (javaVendor.indexOf("Sun") == -1) {
            if (javaVendor.indexOf("IBM") == -1) {
                return false;
            }

            string1 = osName;
        } else {
            string1 = osName;
        }

        if (string1.indexOf("Windows") != -1) {
            StringTokenizer stringTokenizer = new StringTokenizer(javaVersion, ".");

            for (int i = 0; stringTokenizer.hasMoreTokens(); i++) {
                String string = stringTokenizer.nextToken();
                switch (i) {
                    case 0:
                        try {
                            int bd = Integer.parseInt(string);
                            if (bd > 1) {
                                return false;
                            }
                            break;
                        } catch (NumberFormatException numberFormatException) {
                            return false;
                        }
                    case 1:
                        int bc;
                        try {
                            bc = Integer.parseInt(string);
                            if (bc < 4) {
                                return true;
                            }
                        } catch (NumberFormatException numberFormatException1) {
                            return false;
                        }

                        if (bc > 4) {
                            return false;
                        }
                        break;
                    case 2:
                        if (string.length() > 0) {
                            char bb = string.charAt(0);
                            if (bb == '0') {
                                return true;
                            }

                            return false;
                        }
                }
            }
        }

        return false;
    }

    public static int incrementMouseEventCount(PlatformAwareJList platformAwareJList) {
        return ++platformAwareJList.mouseEventCount;
    }

    private static void staticInit() {
        javaVendor = System.getProperty("java.vendor", "Sun");
        javaVersion = System.getProperty("java.version");
        osName = System.getProperty("os.name");
    }
}
