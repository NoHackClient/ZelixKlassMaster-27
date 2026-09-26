package com.zelix.klassmaster.ui.component;

import com.zelix.klassmaster.KlassMaster;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmException;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.IOException;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class BannerPnl extends BevelBorderPanel {
    public static final Color FALLBACK_BACKGROUND = new Color(198, 198, 198);
    public static String[] layoutConstraints = new String[]{
            "centerX=container.width*50/100",
            "imagePnl.centerX=centerX",
            "imagePnl.top=container.top+10",
            "imagePnl.width=264",
            "imagePnl.height=60",
            "versionLbl.centerX=centerX",
            "versionLbl.top=imagePnl.bottom+5",
            "copyrightLbl.centerX=centerX",
            "copyrightLbl.top=versionLbl.bottom+5",
            "copyrightLbl.width=copyrightLbl.defaultWidth+40",
            "contactLbl.centerX=centerX",
            "contactLbl.top=copyrightLbl.bottom+5",
            "contactLbl.width=contactLbl.defaultWidth+25",
            "openSourceLbl.centerX=centerX",
            "openSourceLbl.top=contactLbl.bottom+5",
            "openSourceLbl.width=openSourceLbl.defaultWidth+25",
            "githubLbl.centerX=centerX",
            "githubLbl.top=openSourceLbl.bottom+5",
            "githubLbl.width=githubLbl.defaultWidth+25",
            "licenseePnl.left=container.left+5",
            "licenseePnl.right=container.right-5",
            "licenseePnl.top=githubLbl.bottom+20",
            "licenseePnl.bottom=container.bottom-5"
    };
    public static String[] licenseeLayoutConstraints = new String[]{
            "licenseeTextPnl.top=2", "licenseeTextPnl.centerX=container.width*50/100", "container.defaultBottomPad=2"
    };
    public ConstraintLayout constraintLayout;
    public JLabel contactLbl;
    public JLabel openSourceLbl;
    public JLabel githubLbl;
    public JLabel versionLbl;
    public JLabel copyrightLbl;
    public ImagePnl imagePnl;
    public final String[] licenseeLines = new String[8];

    public void buildBanner(KlassMaster klassMaster) throws ZkmException, IOException {
        this.setFont(KlassMaster.getDefaultFont());
        this.constraintLayout = new ConstraintLayout(this);
        this.setLayout(this.constraintLayout);
        Image image1 = this.getToolkit().getImage(HiddenOptionFlags.USER_PREFERENCES.getClass().getResource("1.png"));
        Image image2 = this.getToolkit().getImage(HiddenOptionFlags.USER_PREFERENCES.getClass().getResource("1.gif"));
        this.imagePnl = new ImagePnl(image1, image2);
        if (!this.imagePnl.isPrimaryImageLoaded()) {
            this.setBackground(FALLBACK_BACKGROUND);
            this.setForeground(Color.black);
        }

        this.add(this.imagePnl, "imagePnl");
        this.versionLbl = new JLabel();
        this.add(this.versionLbl, "versionLbl");
        this.copyrightLbl = new JLabel();
        this.add(this.copyrightLbl, "copyrightLbl");
        this.contactLbl = new JLabel();
        this.add(this.contactLbl, "contactLbl");
        this.openSourceLbl = new JLabel("OpenSource by NoHackClient (https://github.com/NoHackClient)");
        this.add(this.openSourceLbl, "openSourceLbl");
        this.githubLbl = new JLabel("github: https://github.com/NoHackClient/ZelixKlassMaster-27");
        this.add(this.githubLbl, "githubLbl");
        if ("version 27.0.0" != null) {
            this.versionLbl.setText("version 27.0.0");
            this.versionLbl.setHorizontalAlignment(0);
        }

        if ("Copyright 1997-2026 Zelix Pty Ltd (47 078 740 093) All rights reserved" != null) {
            this.copyrightLbl.setText("Copyright 1997-2026 Zelix Pty Ltd (47 078 740 093) All rights reserved");
            this.copyrightLbl.setHorizontalAlignment(0);
        }

        if ("www.zelix.com" != null) {
            this.contactLbl.setText("www.zelix.com");
            this.contactLbl.setHorizontalAlignment(0);
        }

        BevelBorderPanel bevelBorderPanel = new BevelBorderPanel(false);
        this.add(bevelBorderPanel, "licenseePnl");
        if (!this.imagePnl.isPrimaryImageLoaded()) {
            bevelBorderPanel.setBackground(FALLBACK_BACKGROUND);
            bevelBorderPanel.setForeground(Color.black);
        }

        ConstraintLayout constraintLayout1 = new ConstraintLayout(bevelBorderPanel);
        bevelBorderPanel.setLayout(constraintLayout1);
        JPanel jPanel = new JPanel();
        if (!this.imagePnl.isPrimaryImageLoaded()) {
            jPanel.setBackground(FALLBACK_BACKGROUND);
        }

        bevelBorderPanel.add(jPanel, "licenseeTextPnl");
        JPanel jPanel1 = new JPanel();
        if (!this.imagePnl.isPrimaryImageLoaded()) {
            jPanel1.setBackground(FALLBACK_BACKGROUND);
            jPanel.add(jPanel1);
        } else {
            jPanel.add(jPanel1);
        }

        GridLayout gridLayout = new GridLayout(0, 1);
        jPanel1.setLayout(gridLayout);
        JPanel jPanel2 = new JPanel();
        if (!this.imagePnl.isPrimaryImageLoaded()) {
            jPanel2.setBackground(FALLBACK_BACKGROUND);
            jPanel.add(jPanel2);
        } else {
            jPanel.add(jPanel2);
        }

        GridLayout gridLayout1 = new GridLayout(0, 1);
        jPanel2.setLayout(gridLayout1);
        JLabel jLabel = new JLabel(this.licenseeLines[0]);
        JLabel jLabel1 = new JLabel(this.licenseeLines[1]);
        JLabel jLabel2 = new JLabel(this.licenseeLines[2]);
        String string = KlassMaster.checkEvaluationExpiry(
                klassMaster.getEncodedExpiryTime(), klassMaster.getEncodedEvaluationPeriod(), klassMaster.getEncodedGracePeriod(), this
        );
        JLabel jLabel3 = new JLabel(this.licenseeLines[3] + " " + string);
        JLabel jLabel7 = new JLabel(this.licenseeLines[4]);
        JLabel jLabel4 = new JLabel(this.licenseeLines[5]);
        JLabel jLabel5 = new JLabel(this.licenseeLines[6]);
        JLabel jLabel6 = new JLabel(this.licenseeLines[7]);
        jLabel1.setFont(this.getFont());
        jLabel3.setFont(this.getFont());
        jLabel4.setFont(this.getFont());
        jLabel6.setFont(this.getFont());
        jPanel1.add(jLabel);
        jPanel2.add(jLabel1);
        jPanel1.add(jLabel2);
        jPanel2.add(jLabel3);
        jPanel1.add(jLabel7);
        jPanel2.add(jLabel4);
        jPanel1.add(jLabel5);
        jPanel2.add(jLabel6);
        this.constraintLayout.setConstraints(layoutConstraints);
        constraintLayout1.setConstraints(licenseeLayoutConstraints);
        BannerMouseListener bannerMouseListener = new BannerMouseListener(this);
        this.versionLbl.addMouseListener(bannerMouseListener);
        this.copyrightLbl.addMouseListener(bannerMouseListener);
        this.contactLbl.addMouseListener(bannerMouseListener);
        this.openSourceLbl.addMouseListener(bannerMouseListener);
        this.githubLbl.addMouseListener(bannerMouseListener);
        jLabel.addMouseListener(bannerMouseListener);
        jLabel1.addMouseListener(bannerMouseListener);
        jLabel2.addMouseListener(bannerMouseListener);
        jLabel3.addMouseListener(bannerMouseListener);
        jLabel7.addMouseListener(bannerMouseListener);
        jLabel4.addMouseListener(bannerMouseListener);
        jLabel5.addMouseListener(bannerMouseListener);
        jLabel6.addMouseListener(bannerMouseListener);
        bevelBorderPanel.addMouseListener(bannerMouseListener);
        jPanel.addMouseListener(bannerMouseListener);
        jPanel1.addMouseListener(bannerMouseListener);
        jPanel2.addMouseListener(bannerMouseListener);
    }

    public BannerPnl(KlassMaster klassMaster) throws ZkmException, IOException {
        super(true);
        this.loadLicenseeLines(klassMaster);
        this.buildBanner(klassMaster);
    }

    public void loadLicenseeLines(KlassMaster klassMaster) {
        String string = klassMaster.getLicenseToken();
        if ("58#\\u0001\\u00031*\\u0001<\\u000erm".equals(string)) {
            int ba = 0;
            int bb = ba;

            for (byte bc = 8; bb < bc; bc = 8) {
                this.licenseeLines[ba] = klassMaster.getRawLicenseField(ba).trim();
                bb = ++ba;
            }
        }
    }

    public boolean isPrimaryImageLoaded() {
        return this.imagePnl.isPrimaryImageLoaded();
    }
}
