package com.zelix.klassmaster.ui.dialog;

import java.net.URL;

public class HelpPageLoadTask implements Runnable {
    public final HelpViewerDialog helpDialog;
    public final URL pageUrl;

    public HelpPageLoadTask(HelpViewerDialog helpViewerDialog1, URL uRL) {
        this.helpDialog = helpViewerDialog1;
        this.pageUrl = uRL;
    }

    @Override
    public void run() {
        HelpViewerDialog.accessShowPage(this.helpDialog, this.pageUrl);
    }
}
