package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.KlassMaster;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.component.BannerPnl;
import com.zelix.klassmaster.ui.component.ConstraintLayout;
import com.zelix.klassmaster.ui.component.SwingUtils;
import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;

public class AboutDialog extends EscapeClosingDialogBase implements ActionListener {
    public JButton okBtn;
    public static String[] layoutSpec = new String[]{
            "bannerPnl.top=0", "bannerPnl.left=0", "okBtn.centerX=container.width*50/100", "okBtn.top=bannerPnl.bottom+10", "container.defaultBottomPad=10"
    };
    public BannerPnl bannerPnl;

    public AboutDialog(JFrame jFrame, String string, KlassMaster klassMaster) throws ZkmException, IOException {
        super(jFrame, string, true);
        this.buildLayout(klassMaster);
        this.showCenteredOnOwner();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            this.closeDialog();
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public void buildLayout(KlassMaster klassMaster) throws ZkmException, IOException {
        Container container1 = this.getContentPane();
        ConstraintLayout constraintLayout1 = new ConstraintLayout(container1);
        container1.setLayout(constraintLayout1);
        this.bannerPnl = new BannerPnl(klassMaster);
        if (!this.bannerPnl.isPrimaryImageLoaded()) {
            container1.setBackground(BannerPnl.FALLBACK_BACKGROUND);
        }

        if (!this.bannerPnl.isPrimaryImageLoaded()) {
            this.setBackground(BannerPnl.FALLBACK_BACKGROUND);
            this.setForeground(Color.black);
        }

        container1.add(this.bannerPnl, "bannerPnl");
        this.okBtn = new JButton("OK");
        this.okBtn.addActionListener(this);
        container1.add(this.okBtn, "okBtn");
        ConstraintLayout constraintLayout2;
        String[] strings;
        if (!this.bannerPnl.isPrimaryImageLoaded()) {
            this.okBtn.setBackground(BannerPnl.FALLBACK_BACKGROUND);
            this.okBtn.setForeground(Color.black);
            constraintLayout2 = constraintLayout1;
            strings = layoutSpec;
        } else {
            constraintLayout2 = constraintLayout1;
            strings = layoutSpec;
        }

        constraintLayout2.setConstraints(strings);
        SwingUtils.packOnEdt(this);
        this.setResizable(false);
        AboutDialogEnterKeyListener aboutDialogEnterKeyListener = new AboutDialogEnterKeyListener(this);
        this.okBtn.addKeyListener(aboutDialogEnterKeyListener);
    }
}
