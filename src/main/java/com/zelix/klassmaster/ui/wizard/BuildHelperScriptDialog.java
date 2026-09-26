package com.zelix.klassmaster.ui.wizard;

import com.zelix.GuiResources;
import com.zelix.UserPreferences;
import com.zelix.klassmaster.KlassMaster;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.component.AllFilesFilter;
import com.zelix.klassmaster.ui.component.BevelBorderPanel;
import com.zelix.klassmaster.ui.component.ConstraintLayout;
import com.zelix.klassmaster.ui.component.FileSelector;
import com.zelix.klassmaster.ui.component.SwingUtils;
import com.zelix.klassmaster.ui.component.TextFileFilter;
import com.zelix.klassmaster.ui.component.ZkmFileFilter;
import com.zelix.klassmaster.ui.component.ZkmScrollPane;
import com.zelix.klassmaster.ui.dialog.HelperDialogMarker;
import com.zelix.klassmaster.ui.dialog.MessageBoxDialog;
import com.zelix.klassmaster.ui.dialog.OwnedFrameDialogBase;
import com.zelix.klassmaster.util.DialogCallback;
import com.zelix.klassmaster.util.SystemEnvironmentConstants;
import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;

public class BuildHelperScriptDialog extends OwnedFrameDialogBase implements HelperDialogMarker, ActionListener, KeyListener {
    public static String[] layoutConstraints = new String[]{
            "layout.minWidth=btnWidth*4+25",
            "layout.minHeight=250",
            "mainPnl.top=0",
            "mainPnl.left=0",
            "mainPnl.right=container.right",
            "mainPnl.bottom=container.bottom-btnHeight-10",
            "btnWidth=max(previousBtn.defaultWidth, saveBtn.defaultWidth, cancelBtn.defaultWidth, helpBtn.defaultWidth)",
            "btnHeight=max(previousBtn.defaultHeight, saveBtn.defaultHeight, cancelBtn.defaultHeight, helpBtn.defaultHeight)",
            "previousBtn.top=mainPnl.bottom+5",
            "previousBtn.centerX=container.width*12/100",
            "previousBtn.width=btnWidth",
            "previousBtn.height=btnHeight",
            "saveBtn.top=previousBtn.top",
            "saveBtn.centerX=container.width*37/100",
            "saveBtn.width=btnWidth",
            "saveBtn.height=btnHeight",
            "cancelBtn.top=previousBtn.top",
            "cancelBtn.centerX=container.width*62/100",
            "cancelBtn.width=btnWidth",
            "cancelBtn.height=btnHeight",
            "helpBtn.top=previousBtn.top",
            "helpBtn.centerX=container.width*87/100",
            "helpBtn.width=btnWidth",
            "helpBtn.height=btnHeight"
    };
    public static String[] mainPanelConstraints = new String[]{
            "scriptArea.top=5", "scriptArea.left=5", "scriptArea.right=container.right-5", "scriptArea.bottom=container.bottom-5"
    };
    public JButton helpBtn;
    public String scriptText;
    public BevelBorderPanel mainPnl;
    public JTextArea scriptArea;
    public JButton cancelBtn;
    public JButton saveBtn;
    public boolean closedByButton;
    public JButton previousBtn;
    public JFrame parentFrame;
    public UserPreferences userPreferences;
    public DialogCallback callback;

    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }

    @Override
    public void closeFrame() throws ZkmException, IOException {
        super.closeFrame();
        if (!this.closedByButton) {
            this.callback.onDialogCancelled();
        }
    }

    @Override
    public void buildContents(Object object, Object object1, Object object2, Object object3, Object object4, Object object5) {
        this.scriptText = (String) object;
        Container container1 = this.getContentPane();
        ConstraintLayout constraintLayout1 = new ConstraintLayout(container1);
        container1.setLayout(constraintLayout1);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int ba = dimension.width;
        int bc = Math.min(600, ba);
        int bb = dimension.height;
        this.setSize(bc, Math.min(480, bb));
        this.previousBtn = new JButton("Previous");
        this.previousBtn.setToolTipText(GuiResources.getTooltipText("PREVIOUS_SCREEN"));
        this.saveBtn = new JButton("Save");
        this.saveBtn.setToolTipText(GuiResources.getTooltipText("SAVE_SCRIPT"));
        this.cancelBtn = new JButton("Exit");
        this.cancelBtn.setToolTipText(GuiResources.getTooltipText("CANCEL_DIALOG"));
        this.helpBtn = new JButton("Help");
        this.helpBtn.setToolTipText(GuiResources.getTooltipText("SCREEN_HELP"));
        this.previousBtn.addActionListener(this);
        this.saveBtn.addActionListener(this);
        this.cancelBtn.addActionListener(this);
        this.helpBtn.addActionListener(this);
        this.previousBtn.addKeyListener(this);
        this.saveBtn.addKeyListener(this);
        this.cancelBtn.addKeyListener(this);
        this.helpBtn.addKeyListener(this);
        container1.add(this.previousBtn, "previousBtn");
        container1.add(this.saveBtn, "saveBtn");
        container1.add(this.cancelBtn, "cancelBtn");
        container1.add(this.helpBtn, "helpBtn");
        this.mainPnl = new BevelBorderPanel(true);
        container1.add(this.mainPnl, "mainPnl");
        constraintLayout1.setConstraints(layoutConstraints);
        this.buildMainPanel();
        this.setIconImage(KlassMaster.getLogoImage(this));
    }

    public void showHelp() {
        GuiResources.showHelpTopic("034");
    }

    public void saveScript() {
        File parent;
        if (this.userPreferences.getScriptSaveDirectory() != null) {
            final File file = new File(this.userPreferences.getScriptSaveDirectory());
            if (file.exists() && file.isDirectory()) {
                parent = file;
            } else {
                parent = new File(SystemEnvironmentConstants.USER_DIR);
            }
        } else {
            parent = new File(SystemEnvironmentConstants.USER_DIR);
        }
        final FileSelector fileSelector = new FileSelector(parent, false, 1, new ZkmFileFilter[]{new TextFileFilter(), new AllFilesFilter()}, true);
        fileSelector.setSelectedFile(new File(parent, "script.txt"));
        if (fileSelector.showSaveDialog(this) == 1) {
            final File selectedFile = fileSelector.getSelectedFile();
            BufferedWriter bufferedWriter = null;
            try {
                bufferedWriter = new BufferedWriter(new FileWriter(selectedFile));
                bufferedWriter.write(this.scriptText, 0, this.scriptText.length());
                bufferedWriter.close();
                final String parent2 = selectedFile.getParent();
                if (parent2 != null) {
                    this.userPreferences.setScriptSaveDirectory(parent2);
                    this.userPreferences.savePreferences();
                }
                try {
                    bufferedWriter.close();
                } catch (final IOException ex) {
                }
            } catch (final IOException ex2) {
                final MessageBoxDialog messageBoxDialog = new MessageBoxDialog(this, "File Error", "Couldn't save \"" + selectedFile.getAbsolutePath() + "\" : " + ex2.getMessage());
            } finally {
                if (bufferedWriter != null) {
                    try {
                        bufferedWriter.close();
                    } catch (final IOException ex3) {
                    }
                }
            }
        }
    }

    public void goPrevious() throws ZkmException, IOException {
        this.closedByButton = true;
        this.closeFrame();
        this.callback.onDialogResult(2);
    }

    public BuildHelperScriptDialog(JFrame jFrame, String string, String string1, UserPreferences userPreferences1, DialogCallback dialogCallback1) {
        super(jFrame, string, string1);
        this.parentFrame = jFrame;
        this.userPreferences = userPreferences1;
        this.callback = dialogCallback1;
        this.showCenteredOnOwner();
        SwingUtils.requestFocusOnEdt(this.saveBtn);
    }

    @Override
    public void cancelDialog() throws ZkmException, IOException {
        this.closedByButton = true;
        this.closeFrame();
        this.callback.onDialogCancelled();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            Object object = actionEvent.getSource();
            if (object == this.previousBtn) {
                this.goPrevious();
            } else if (object == this.saveBtn) {
                this.saveScript();
            } else if (object == this.cancelBtn) {
                this.cancelDialog();
            } else if (object == this.helpBtn) {
                this.showHelp();
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        try {
            if (keyEvent.getKeyCode() == 10) {
                if (keyEvent.getSource() == this.previousBtn) {
                    this.goPrevious();
                } else if (keyEvent.getSource() == this.saveBtn) {
                    this.saveScript();
                } else if (keyEvent.getSource() == this.cancelBtn) {
                    this.cancelDialog();
                } else if (keyEvent.getSource() == this.helpBtn) {
                    this.showHelp();
                }
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }

    public void buildMainPanel() {
        ConstraintLayout constraintLayout1 = new ConstraintLayout(this.mainPnl);
        this.mainPnl.setLayout(constraintLayout1);
        this.scriptArea = new JTextArea(this.scriptText);
        this.scriptArea.setFont(new Font("Courier", 0, 14));
        this.scriptArea.setEditable(false);
        this.mainPnl.add(new ZkmScrollPane(this.scriptArea), "scriptArea");
        constraintLayout1.setConstraints(mainPanelConstraints);
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
    }
}
