package com.zelix.klassmaster.ui.dialog;

import com.zelix.GuiResources;
import com.zelix.klassmaster.KlassMaster;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.ui.component.BevelBorderPanel;
import com.zelix.klassmaster.ui.component.ConstraintLayout;
import com.zelix.klassmaster.ui.component.RenameFilterModel;
import com.zelix.klassmaster.ui.component.ZkmScrollPane;
import com.zelix.klassmaster.util.DialogCallback;
import com.zelix.klassmaster.util.ObservableModel;
import com.zelix.klassmaster.util.ParamEditorMarker;
import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

public abstract class ExcludeParameterDialogBase extends OwnedFrameDialogBase implements ParamEditorMarker, ActionListener, KeyListener {
    public static String[] layoutSpec = new String[]{
            "layout.minWidth=300",
            "layout.minHeight=300",
            "tabbedPane.top=5",
            "tabbedPane.left=5",
            "tabbedPane.right=container.right-5",
            "paramLbl.centerY=displayPnl.centerY",
            "paramLbl.left=10",
            "displayPnl.top=tabbedPane.bottom+10",
            "displayPnl.left=paramLbl.right+3",
            "displayPnl.right=container.right-5",
            "explanationArea.left=5",
            "explanationArea.top=displayPnl.bottom+5",
            "explanationArea.right=container.right-5",
            "explanationArea.bottom=container.bottom-btnHeight-10",
            "btnWidth=max(okBtn.defaultWidth, cancelBtn.defaultWidth, helpBtn.defaultWidth)",
            "btnHeight=max(okBtn.defaultHeight, cancelBtn.defaultHeight, helpBtn.defaultHeight)",
            "okBtn.width=btnWidth",
            "okBtn.height=btnHeight",
            "okBtn.bottom=container.bottom-5",
            "okBtn.centerX=container.width*16/100",
            "cancelBtn.width=btnWidth",
            "cancelBtn.height=btnHeight",
            "cancelBtn.top=okBtn.top",
            "cancelBtn.centerX=container.width*50/100",
            "helpBtn.width=btnWidth",
            "helpBtn.height=btnHeight",
            "helpBtn.top=okBtn.top",
            "helpBtn.centerX=container.width*84/100"
    };
    public static String[] paramDisplayLayoutSpec = new String[]{"paramDisplay.top=1", "paramDisplay.left=2", "paramDisplay.right=container.right-2"};
    public JLabel paramDisplay;
    public JButton helpBtn;
    public int exclusionType;
    public JButton cancelBtn;
    public JButton okBtn;
    public JTextArea explanationArea;
    public RenameFilterModel filterModel;
    public JTabbedPane tabbedPane;
    public DialogCallback dialogCallback;

    public abstract void showHelp();

    public final void buildParamDisplayPanel(JPanel jPanel) {
        ConstraintLayout constraintLayout1 = new ConstraintLayout(jPanel);
        jPanel.setLayout(constraintLayout1);
        this.paramDisplay = new JLabel();
        this.paramDisplay.setText(this.filterModel.toScriptText());
        this.paramDisplay.setFont(this.getFont());
        this.paramDisplay.setToolTipText(GuiResources.getTooltipText("CURRENT_EXCLUDE_PARAM"));
        jPanel.add(this.paramDisplay, "paramDisplay");
        constraintLayout1.setConstraints(paramDisplayLayoutSpec);
    }

    @Override
    public final void actionPerformed(ActionEvent actionEvent) {
        try {
            Object object = actionEvent.getSource();
            if (object == this.okBtn) {
                this.acceptParameter();
            } else if (object == this.cancelBtn) {
                this.cancelDialog();
            } else if (object == this.helpBtn) {
                this.showHelp();
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    @Override
    public final void cancelDialog() throws ZkmException, IOException {
        this.closeFrame();
        this.dialogCallback.onDialogCancelled();
    }

    @Override
    public final void handleObservedChange(ObservableModel observableModel1, Object object, Object object1, Object object2) throws ZkmException, IOException {
        this.paramDisplay.setText(this.filterModel.toScriptText());
        this.explanationArea.setText(this.filterModel.buildDescription(this.exclusionType));
        this.explanationArea.setCaretPosition(0);
    }

    @Override
    public final void keyReleased(KeyEvent keyEvent) {
    }

    @Override
    public final void keyPressed(KeyEvent keyEvent) {
        try {
            if (keyEvent.getKeyCode() == 10) {
                if (keyEvent.getSource() == this.okBtn) {
                    this.acceptParameter();
                } else if (keyEvent.getSource() == this.cancelBtn) {
                    this.cancelDialog();
                } else if (keyEvent.getSource() == this.helpBtn) {
                    this.showHelp();
                }
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    @Override
    public final void keyTyped(KeyEvent keyEvent) {
    }

    @Override
    public final void buildContents(Object object, Object object1, Object object2, Object object3, Object object4, Object object5) {
        this.filterModel = (RenameFilterModel) object;
        this.exclusionType = (Integer) object1;
        Container container1 = this.getContentPane();
        ConstraintLayout constraintLayout1 = new ConstraintLayout(container1);
        container1.setLayout(constraintLayout1);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int ba = dimension.width;
        int bc = Math.min(500, ba);
        int bb = dimension.height;
        this.setSize(bc, Math.min(450, bb));
        this.okBtn = new JButton("OK");
        this.okBtn.setToolTipText(GuiResources.getTooltipText("PARAMETER_COMPLETE"));
        this.cancelBtn = new JButton("Cancel");
        this.cancelBtn.setToolTipText(GuiResources.getTooltipText("CANCEL_DIALOG"));
        this.helpBtn = new JButton("Help");
        this.helpBtn.setToolTipText(GuiResources.getTooltipText("SCREEN_HELP"));
        this.okBtn.addActionListener(this);
        this.cancelBtn.addActionListener(this);
        this.helpBtn.addActionListener(this);
        this.okBtn.addKeyListener(this);
        this.cancelBtn.addKeyListener(this);
        this.helpBtn.addKeyListener(this);
        container1.add(this.okBtn, "okBtn");
        container1.add(this.cancelBtn, "cancelBtn");
        container1.add(this.helpBtn, "helpBtn");
        this.tabbedPane = new JTabbedPane();
        container1.add(this.tabbedPane, "tabbedPane");
        JLabel jLabel = new JLabel("Parameter so far:");
        BevelBorderPanel bevelBorderPanel = new BevelBorderPanel(false);
        this.buildParamDisplayPanel(bevelBorderPanel);
        container1.add(jLabel, "paramLbl");
        container1.add(bevelBorderPanel, "displayPnl");
        this.explanationArea = new JTextArea();
        this.explanationArea.setEditable(false);
        if (this.exclusionType == 3) {
            this.explanationArea.setToolTipText(GuiResources.getTooltipText("EXPLAIN_INCLUSION_PARAM"));
        } else {
            this.explanationArea.setToolTipText(GuiResources.getTooltipText("EXPLAIN_EXCLUSION_PARAM"));
        }

        container1.add(new ZkmScrollPane(this.explanationArea), "explanationArea");
        this.explanationArea.setText(this.filterModel.buildDescription(this.exclusionType));
        this.explanationArea.setCaretPosition(0);
        constraintLayout1.setConstraints(layoutSpec);
        this.buildTabs();
    }

    public ExcludeParameterDialogBase(JFrame jFrame, String string, RenameFilterModel renameFilterModel, int ba, DialogCallback dialogCallback1) {
        super(jFrame, string, renameFilterModel, ba);
        this.setFont(KlassMaster.getDefaultFont());
        this.dialogCallback = dialogCallback1;
        this.setIconImage(KlassMaster.getLogoImage(this));
        this.showCenteredOnOwner();
        renameFilterModel.addObserver(this);
    }

    public final void acceptParameter() throws ZkmException, IOException {
        try {
            String string = this.filterModel.toScriptText();
            RenameFilterModel.executeFilterParameter(this.filterModel.getScriptEnvironment(), string);
            this.dialogCallback.onDialogResult(string);
            this.closeFrame();
        } catch (ZkmProcessingException zkmProcessingException) {
            new TextViewerDialog(this, zkmProcessingException.getMessage());
        }
    }

    public abstract void buildTabs();
}
