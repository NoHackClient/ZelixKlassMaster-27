package com.zelix.klassmaster.ui.dialog;

import java.net.URL;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;

public class HelpHyperlinkListener implements HyperlinkListener {
    public final ClassOpenWarningsDialog dialog;
    private static final String SEPARATOR = " : ";

    public HelpHyperlinkListener(ClassOpenWarningsDialog classOpenWarningsDialog) {
        this.dialog = classOpenWarningsDialog;
    }

    @Override
    public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
        if (hyperlinkEvent.getEventType() == EventType.ACTIVATED) {
            URL uRL = null;

            try {
                uRL = hyperlinkEvent.getURL();
                this.dialog.helpPane.setPage(uRL);
            } catch (Throwable throwable) {
                this.dialog.helpPane.setText(throwable.toString() + SEPARATOR + uRL);
            }
        }
    }
}
