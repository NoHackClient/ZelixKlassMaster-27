package com.zelix;

import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.dialog.HelpViewerDialog;
import com.zelix.klassmaster.util.IntPair;
import com.zelix.klassmaster.util.SerializableDimension;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GuiResources {
    public static HelpViewerDialog helpViewer;
    public String resourceBasePath;
    public static ResourceBundle tooltipBundle;
    public static final GuiResources INSTANCE = new GuiResources();
    public IntPair windowLocation;
    public SerializableDimension windowSize;

    public static String getTooltipText(String string) {
        if (tooltipBundle != null) {
            try {
                return tooltipBundle.getString(string);
            } catch (Throwable throwable) {
                return "";
            }
        } else {
            return "";
        }
    }

    public static void clearHelpViewer() {
        helpViewer = null;
    }

    public SerializableDimension getWindowSize() {
        return this.windowSize;
    }

    public static void closeHelpViewer() throws ZkmException, IOException {
        if (helpViewer != null) {
            helpViewer.closeFrame();
        }
    }

    public IntPair getWindowLocation() {
        return this.windowLocation;
    }

    public static void showHelpPage(String string, String string1) {
        HelpViewerDialog helpViewerDialog1;
        if (helpViewer == null) {
            helpViewer = new HelpViewerDialog(INSTANCE);
            helpViewerDialog1 = helpViewer;
        } else {
            helpViewerDialog1 = helpViewer;
        }

        helpViewerDialog1.showPageOnEdt(getHelpPageUrl(string, string1));
    }

    public static boolean isHelpViewerOpen() {
        return helpViewer != null;
    }

    public static URL getHelpPageUrl(String string, String string1) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(INSTANCE.resourceBasePath);
        if (string != null && string.trim().length() > 0) {
            stringBuffer.append(string.trim());
            stringBuffer.append(".html");
        }

        GuiResources guiResources1;
        if (string1 != null) {
            if (string1.trim().length() > 0) {
                stringBuffer.append("#");
                stringBuffer.append(string1.trim());
                guiResources1 = INSTANCE;
            } else {
                guiResources1 = INSTANCE;
            }
        } else {
            guiResources1 = INSTANCE;
        }

        return guiResources1.getClass().getResource(stringBuffer.toString());
    }

    public static void showHelpTopic(String string) {
        showHelpPage(string, (String) null);
    }

    private GuiResources() {
        String string = this.getClass().getName();
        String string1 = string.substring(0, string.lastIndexOf(".") + 1);
        this.resourceBasePath = "/" + string1.replace('.', '/');
        UserPreferences userPreferences1 = HiddenOptionFlags.USER_PREFERENCES;
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        this.windowLocation = userPreferences1.getHelpWindowLocation();
        if (this.windowLocation.getFirst() > dimension.getWidth() - 50.0 || this.windowLocation.getSecond() > dimension.getHeight() - 50.0) {
            this.windowLocation = new IntPair(0, 0);
        }

        this.windowSize = userPreferences1.getHelpWindowSize();
        if (this.windowSize.getWidth() >= dimension.getWidth() - 25.0) {
            this.windowSize = new SerializableDimension((int) dimension.getWidth() - 50, this.windowSize.getHeight());
        }

        if (this.windowSize.getHeight() >= dimension.getHeight() - 25.0) {
            this.windowSize = new SerializableDimension(this.windowSize.getWidth(), (int) dimension.getHeight() - 50);
        }

        if (this.windowSize.getWidth() < 300) {
            this.windowSize = new SerializableDimension(300, this.windowSize.getHeight());
        }

        if (this.windowSize.getHeight() < 300) {
            this.windowSize = new SerializableDimension(this.windowSize.getWidth(), 300);
        }

        try {
            tooltipBundle = ResourceBundle.getBundle(string1 + "tooltip");
        } catch (Throwable throwable) {
        }
    }

    public static URL getHelpTopicUrl(String string) {
        return getHelpPageUrl(string, (String) null);
    }
}
