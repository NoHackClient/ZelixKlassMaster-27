package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.KlassMaster;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.component.BannerPnl;
import com.zelix.klassmaster.ui.component.ConstraintLayout;
import com.zelix.klassmaster.ui.component.SwingUtils;
import com.zelix.klassmaster.util.DialogCallback;
import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

public class EvaluationLicenseDialog extends EscapeClosingDialogBase implements ActionListener {
    public static String[] layoutSpec = new String[]{
            "btnHeight=max(okBtn.defaultHeight, cancelBtn.defaultHeight)",
            "btnWidth=max(okBtn.defaultWidth, cancelBtn.defaultWidth)",
            "bannerPnl.top=0",
            "bannerPnl.left=0",
            "bannerPnl.right=container.right",
            "okBtn.centerX=container.width*25/100",
            "cancelBtn.centerX=container.width*75/100",
            "okBtn.top=bannerPnl.bottom+10",
            "cancelBtn.top=okBtn.top",
            "okBtn.height=btnHeight",
            "okBtn.width=btnWidth",
            "cancelBtn.height=btnHeight",
            "cancelBtn.width=btnWidth",
            "okBtn.bottom=container.bottom-10"
    };
    public BannerPnl bannerPanel;
    public JButton cancelBtn;
    public JButton okBtn;
    public DialogCallback dialogCallback;
    public JFrame ownerFrame;

    @Override
    public void closeDialog() throws ZkmException, IOException {
        this.setVisible(false);
        this.dispose();
    }

    public void registerEscapeAction(JComponent jComponent) {
        jComponent.registerKeyboardAction(new EvaluationDialogEscapeAction(this), KeyStroke.getKeyStroke(27, 0), 1);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            Object object = actionEvent.getSource();
            this.closeDialog();
            if (object == this.okBtn) {
                this.dialogCallback.onDialogResult(this.ownerFrame);
            } else {
                this.dialogCallback.onDialogCancelled();
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public void buildLayout(KlassMaster klassMaster) throws ZkmException, IOException {
        Container container1 = this.getContentPane();
        ConstraintLayout constraintLayout1 = new ConstraintLayout(container1);
        container1.setLayout(constraintLayout1);
        this.bannerPanel = new BannerPnl(klassMaster);
        if (!this.bannerPanel.isPrimaryImageLoaded()) {
            this.setBackground(BannerPnl.FALLBACK_BACKGROUND);
        }

        Container container2;
        BannerPnl bannerPnl1;
        if (!this.bannerPanel.isPrimaryImageLoaded()) {
            container1.setBackground(BannerPnl.FALLBACK_BACKGROUND);
            container1.setForeground(Color.black);
            container2 = container1;
            bannerPnl1 = this.bannerPanel;
        } else {
            container2 = container1;
            bannerPnl1 = this.bannerPanel;
        }

        container2.add(bannerPnl1, "bannerPnl");
        this.okBtn = new JButton("I agree");
        container1.add(this.okBtn, "okBtn");
        this.cancelBtn = new JButton("I don't agree");
        container1.add(this.cancelBtn, "cancelBtn");
        if (!this.bannerPanel.isPrimaryImageLoaded()) {
            this.okBtn.setBackground(BannerPnl.FALLBACK_BACKGROUND);
            this.okBtn.setForeground(Color.black);
        }

        ConstraintLayout constraintLayout2;
        String[] strings;
        if (!this.bannerPanel.isPrimaryImageLoaded()) {
            this.cancelBtn.setBackground(BannerPnl.FALLBACK_BACKGROUND);
            this.cancelBtn.setForeground(Color.black);
            constraintLayout2 = constraintLayout1;
            strings = layoutSpec;
        } else {
            constraintLayout2 = constraintLayout1;
            strings = layoutSpec;
        }

        constraintLayout2.setConstraints(strings);
        SwingUtils.packOnEdt(this);
        this.setResizable(false);
        EvaluationDialogKeyListener evaluationDialogKeyListener = new EvaluationDialogKeyListener(this);
        this.okBtn.addKeyListener(evaluationDialogKeyListener);
        this.cancelBtn.addKeyListener(evaluationDialogKeyListener);
        this.okBtn.addActionListener(this);
        this.cancelBtn.addActionListener(this);
        EvaluationDialogWindowListener evaluationDialogWindowListener = new EvaluationDialogWindowListener(this);
        this.addWindowListener(evaluationDialogWindowListener);
        this.registerEscapeAction(this.getRootPane());
    }

    public EvaluationLicenseDialog(JFrame jFrame, KlassMaster klassMaster, DialogCallback dialogCallback1) throws ZkmException, IOException {
        super(jFrame, "Zelix KlassMaster Evaluation", true);
        this.dialogCallback = dialogCallback1;
        this.ownerFrame = jFrame;
        this.buildLayout(klassMaster);
        this.showCentered();
    }
}
