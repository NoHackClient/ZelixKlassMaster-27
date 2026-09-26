package com.zelix.klassmaster.ui.dialog;

import com.zelix.GuiResources;
import com.zelix.klassmaster.KlassMaster;
import com.zelix.klassmaster.config.TrimOptions;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.component.BevelBorderPanel;
import com.zelix.klassmaster.ui.component.ConstraintLayout;
import com.zelix.klassmaster.ui.component.SwingUtils;
import com.zelix.klassmaster.util.DialogCallback;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.io.IOException;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;

public class DeleteAttributesDialog extends EscapeClosableFrame implements HelperDialogMarker {
    public static final String[] LAYOUT_SPEC = new String[]{
            "container.defaultBottomPad=10",
            "deleteSourceFileAttributesChk.top=10",
            "deleteSourceFileAttributesChk.left=10",
            "deleteDeprectedAttributesChk.top=deleteSourceFileAttributesChk.bottom+10",
            "deleteDeprectedAttributesChk.left=deleteSourceFileAttributesChk.left",
            "deleteAnnotationAttributesChk.top=deleteDeprectedAttributesChk.bottom+10",
            "deleteAnnotationAttributesChk.left=deleteSourceFileAttributesChk.left",
            "deleteExceptionAttributesChk.top=deleteAnnotationAttributesChk.bottom+10",
            "deleteExceptionAttributesChk.left=deleteAnnotationAttributesChk.left",
            "deleteDebugExtensionAttributesChk.top=deleteExceptionAttributesChk.bottom+10",
            "deleteDebugExtensionAttributesChk.left=deleteExceptionAttributesChk.left",
            "deleteUnknownAttributesChk.top=deleteDebugExtensionAttributesChk.bottom+10",
            "deleteUnknownAttributesChk.left=deleteDebugExtensionAttributesChk.left"
    };
    public JButton okBtn;
    public JButton helpBtn;
    public JButton previousBtn;
    public JButton cancelBtn;
    public boolean completed;
    public BevelBorderPanel mainPnl = new BevelBorderPanel(true);
    public JCheckBox deleteSourceFileAttributesChk = new JCheckBox("delete Source File attributes");
    public JCheckBox deleteDeprectedAttributesChk = new JCheckBox("delete Deprecated attributes");
    public JCheckBox deleteAnnotationAttributesChk = new JCheckBox("delete annotation attributes");
    public JCheckBox deleteExceptionAttributesChk = new JCheckBox("delete Exception attributes");
    public JCheckBox deleteDebugExtensionAttributesChk = new JCheckBox("delete Source Debug Extension attributes");
    public JCheckBox deleteUnknownAttributesChk = new JCheckBox("delete unknown attributes");
    public TrimOptions trimOptions;
    public JFrame ownerFrame;
    public DialogCallback dialogCallback;

    @Override
    public Action createEscapeAction() {
        return new TrimOptionsAction(this);
    }

    public void storeOptions() {
        this.trimOptions.a = this.deleteSourceFileAttributesChk.isSelected();
        this.trimOptions.b = this.deleteDeprectedAttributesChk.isSelected();
        this.trimOptions.d = this.deleteAnnotationAttributesChk.isSelected();
        this.trimOptions.e = this.deleteExceptionAttributesChk.isSelected();
        this.trimOptions.f = this.deleteDebugExtensionAttributesChk.isSelected();
        this.trimOptions.c = this.deleteUnknownAttributesChk.isSelected();
    }

    public void buildDialog(String string, String string1, String string2) {
        Container container1 = this.getContentPane();
        ConstraintLayout constraintLayout1 = this.createContentLayout(string, string1, string2, container1);
        TrimOptionsActionListener trimOptionsActionListener = new TrimOptionsActionListener(this);
        this.previousBtn.addActionListener(trimOptionsActionListener);
        this.okBtn.addActionListener(trimOptionsActionListener);
        this.cancelBtn.addActionListener(trimOptionsActionListener);
        this.helpBtn.addActionListener(trimOptionsActionListener);
        AttributeOptionsKeyListener attributeOptionsKeyListener = new AttributeOptionsKeyListener(this);
        this.previousBtn.addKeyListener(attributeOptionsKeyListener);
        this.okBtn.addKeyListener(attributeOptionsKeyListener);
        this.cancelBtn.addKeyListener(attributeOptionsKeyListener);
        this.helpBtn.addKeyListener(attributeOptionsKeyListener);
        container1.add(this.mainPnl, "mainPnl");
        this.buildMainPanel();
        container1.add(this.previousBtn, "previousBtn");
        container1.add(this.okBtn, "okBtn");
        container1.add(this.cancelBtn, "cancelBtn");
        container1.add(this.helpBtn, "helpBtn");
        this.finishLayoutAndShow(constraintLayout1);
    }

    public final void onCancel() throws ZkmException, IOException {
        this.completed = true;
        this.closeFrame();
        this.dialogCallback.onDialogCancelled();
    }

    public void loadOptions() {
        this.deleteSourceFileAttributesChk.setSelected(this.trimOptions.a);
        this.deleteDeprectedAttributesChk.setSelected(this.trimOptions.b);
        this.deleteAnnotationAttributesChk.setSelected(this.trimOptions.d);
        this.deleteExceptionAttributesChk.setSelected(this.trimOptions.e);
        this.deleteDebugExtensionAttributesChk.setSelected(this.trimOptions.f);
        this.deleteUnknownAttributesChk.setSelected(this.trimOptions.c);
    }

    public final void onOk() throws ZkmException, IOException {
        if (this.validateOptions()) {
            this.completed = true;
            this.closeFrame();
            this.storeOptions();
            this.dialogCallback.onDialogResult_v(this.trimOptions, 1);
        }
    }

    public void buildMainPanel() {
        ConstraintLayout constraintLayout1 = new ConstraintLayout(this.mainPnl);
        this.mainPnl.setLayout(constraintLayout1);
        this.mainPnl.add(this.deleteSourceFileAttributesChk, "deleteSourceFileAttributesChk");
        this.mainPnl.add(this.deleteDeprectedAttributesChk, "deleteDeprectedAttributesChk");
        this.mainPnl.add(this.deleteAnnotationAttributesChk, "deleteAnnotationAttributesChk");
        this.mainPnl.add(this.deleteExceptionAttributesChk, "deleteExceptionAttributesChk");
        this.mainPnl.add(this.deleteDebugExtensionAttributesChk, "deleteDebugExtensionAttributesChk");
        this.mainPnl.add(this.deleteUnknownAttributesChk, "deleteUnknownAttributesChk");
        constraintLayout1.setConstraints(LAYOUT_SPEC);
    }

    public ConstraintLayout createContentLayout(String string, String string1, String string2, Container container1) {
        this.setTitle(string);
        ConstraintLayout constraintLayout1 = new ConstraintLayout(container1);
        container1.setLayout(constraintLayout1);
        this.createButtons(string1, string2);
        TrimOptionsWindowCloser trimOptionsWindowCloser = new TrimOptionsWindowCloser(this);
        this.addWindowListener(trimOptionsWindowCloser);
        return constraintLayout1;
    }

    public final void onPrevious() throws ZkmException, IOException {
        if (this.validateOptions()) {
            this.completed = true;
            this.closeFrame();
            this.storeOptions();
            this.dialogCallback.onDialogResult_v(this.trimOptions, 2);
        }
    }

    public final void showHelp() {
        GuiResources.showHelpTopic("032");
    }

    public DeleteAttributesDialog(String string, String string1, String string2, JFrame jFrame, TrimOptions trimOptions1, DialogCallback dialogCallback1) {
        this.trimOptions = trimOptions1;
        this.ownerFrame = jFrame;
        this.dialogCallback = dialogCallback1;
        this.setFont(KlassMaster.getDefaultFont());
        this.buildDialog(string, string1, string2);
        SwingUtils.requestFocusOnEdt(this.okBtn);
    }

    public void finishLayoutAndShow(ConstraintLayout constraintLayout1) {
        constraintLayout1.parseConstraints(
                "mainPnl.top=0;mainPnl.left=0;mainPnl.bottom=container.bottom-btnHeight-15;mainPnl.right=container.right;layout.minWidth=btnWidth*5+30;"
                        + this.getButtonLayoutSpec()
        );
        this.ownerFrame.setEnabled(false);
        this.loadOptions();
        this.setIconImage(KlassMaster.getLogoImage(this));
        SwingUtils.packOnEdt(this);
        Dimension dimension = this.getSize();
        Point point = this.ownerFrame.getLocationOnScreen();
        Dimension dimension1 = this.ownerFrame.getSize();
        int ba = dimension1.width / 2 - dimension.width / 2 + point.x;
        int bb = dimension1.height / 2 - dimension.height / 2 + point.y;
        ba = Math.max(0, ba);
        bb = Math.max(0, bb);
        this.setLocation(ba, bb);
        SwingUtils.setVisibleOnEdt(this);
    }

    public boolean validateOptions() {
        return true;
    }

    public String getButtonLayoutSpec() {
        return "btnWidth=max(previousBtn.defaultWidth,okBtn.defaultWidth,cancelBtn.defaultWidth,helpBtn.defaultWidth);btnHeight=max(previousBtn.defaultHeight,okBtn.defaultHeight,cancelBtn.defaultHeight,helpBtn.defaultHeight);previousBtn.width=btnWidth;previousBtn.height=btnHeight;previousBtn.centerX=container.width*12/100;previousBtn.bottom=container.bottom-5;okBtn.width=btnWidth;okBtn.height=btnHeight;okBtn.centerX=container.width*37/100;okBtn.bottom=container.bottom-5;cancelBtn.width=btnWidth;cancelBtn.height=btnHeight;cancelBtn.centerX=container.width*62/100;cancelBtn.bottom=container.bottom-5;helpBtn.width=btnWidth;helpBtn.height=btnHeight;helpBtn.centerX=container.width*87/100;helpBtn.bottom=container.bottom-5;";
    }

    @Override
    public final void closeFrame() throws ZkmException, IOException {
        super.closeFrame();
        if (!this.completed) {
            this.dialogCallback.onDialogCancelled();
        }
    }

    public void createButtons(String string, String string1) {
        this.previousBtn = new JButton("Previous");
        this.previousBtn.setToolTipText(GuiResources.getTooltipText("PREVIOUS_SCREEN"));
        this.okBtn = new JButton(string);
        this.okBtn.setToolTipText(string1);
        this.cancelBtn = new JButton("Cancel");
        this.cancelBtn.setToolTipText(GuiResources.getTooltipText("CANCEL_DIALOG"));
        this.helpBtn = new JButton("Help");
        this.helpBtn.setToolTipText(GuiResources.getTooltipText("SCREEN_HELP"));
    }
}
