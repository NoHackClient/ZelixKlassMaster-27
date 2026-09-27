package com.zelix.klassmaster.ui.dialog;

import com.zelix.GuiResources;
import com.zelix.UserPreferences;
import com.zelix.klassmaster.KlassMaster;
import com.zelix.klassmaster.archive.ZkmFileUtils;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.ZkmMainWindow;
import com.zelix.klassmaster.ui.component.AllFilesFilter;
import com.zelix.klassmaster.ui.component.BevelBorderPanel;
import com.zelix.klassmaster.ui.component.BorderedPanel;
import com.zelix.klassmaster.ui.component.ConstraintLayout;
import com.zelix.klassmaster.ui.component.FileSelector;
import com.zelix.klassmaster.ui.component.SwingUtils;
import com.zelix.klassmaster.ui.component.TextFileFilter;
import com.zelix.klassmaster.ui.component.ZkmFileFilter;
import com.zelix.klassmaster.ui.component.ZkmScrollPane;
import com.zelix.klassmaster.util.SystemEnvironmentConstants;
import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ProGuardTranslateDialog extends OwnedFrameDialogBase implements ActionListener, KeyListener, MouseListener {
    public static String initialStatusText = "Open a ProGuard configuration file or paste contents into upper area.";
    public static String resultsPlaceholderText = "1) Open a ProGuard configuration file or paste contents into upper area,"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "2) Click on the <Translate> button,"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "3) The translated ZKM Script will appear here.";
    public static String[] layoutConstraints = new String[]{
            "layout.minWidth=btnWidth*4",
            "layout.width=550",
            "layout.minHeight=rowHeight*15",
            "layout.height=550",
            "proGuardScriptFileNameLbl.left=5",
            "proGuardScriptFileNameLbl.bottom=proGuardScriptOpenBtn.bottom",
            "proGuardScriptOpenBtn.left=proGuardScriptFileNameLbl.right+10",
            "proGuardScriptOpenBtn.centerY=rowHeight/2+10",
            "proGuardScriptFileNameFld.centerY=proGuardScriptOpenBtn.centerY",
            "proGuardScriptFileNameFld.left=proGuardScriptOpenBtn.right+5",
            "proGuardScriptFileNameFld.right=container.right-5",
            "proGuardPnl.left=5",
            "proGuardPnl.top=proGuardScriptFileNameFld.bottom+10",
            "proGuardPnl.right=container.right-5",
            "proGuardPnl.height=rowHeight*6",
            "resultsPnl.left=5",
            "resultsPnl.top=proGuardPnl.bottom+10",
            "resultsPnl.right=container.right-5",
            "resultsPnl.bottom=container.bottom-statusPnl.height-btnHeight-25",
            "rowHeight=max(proGuardScriptFileNameLbl.defaultHeight, proGuardScriptOpenBtn.defaultHeight, proGuardScriptFileNameFld.defaultHeight)",
            "btnWidth=max(translateBtn.defaultWidth, cancelBtn.defaultWidth, helpBtn.defaultWidth)",
            "btnHeight=max(translateBtn.defaultHeight, cancelBtn.defaultHeight, helpBtn.defaultHeight)",
            "translateBtn.centerX=container.width*16/100",
            "translateBtn.top=resultsPnl.bottom+15",
            "translateBtn.width=btnWidth",
            "cancelBtn.centerX=container.width*50/100",
            "cancelBtn.top=translateBtn.top",
            "cancelBtn.width=btnWidth",
            "helpBtn.centerX=container.width*84/100",
            "helpBtn.top=translateBtn.top",
            "helpBtn.width=btnWidth",
            "statusPnl.left=0",
            "statusPnl.bottom=container.bottom",
            "statusPnl.right=container.right"
    };
    public static String[] proGuardPanelConstraints = new String[]{
            "proGuardAreaLbl.left=5",
            "proGuardAreaLbl.bottom=rowHeight+5",
            "proGuardAreaPasteBtn.bottom=proGuardAreaLbl.bottom",
            "proGuardAreaPasteBtn.right=container.right-5",
            "proGuardArea.left=5",
            "proGuardArea.top=proGuardAreaLbl.bottom+4",
            "proGuardArea.right=container.right-5",
            "proGuardArea.bottom=container.bottom-5",
            "rowHeight=max(proGuardAreaLbl.defaultHeight, proGuardAreaPasteBtn.defaultHeight)"
    };
    public static String[] resultsPanelConstraints = new String[]{
            "resultsAreaLbl.left=5",
            "resultsAreaLbl.bottom=rowHeight+5",
            "resultsAreaCopyBtn.bottom=resultsAreaLbl.bottom",
            "resultsAreaCopyBtn.right=container.right-5",
            "resultsAreaSaveBtn.bottom=resultsAreaLbl.bottom",
            "resultsAreaSaveBtn.right=resultsAreaCopyBtn.left-10",
            "resultsArea.left=5",
            "resultsArea.top=resultsAreaLbl.bottom+4",
            "resultsArea.right=container.right-5",
            "resultsArea.bottom=container.bottom-5",
            "rowHeight=max(resultsAreaLbl.defaultHeight, resultsAreaSaveBtn.defaultHeight, resultsAreaCopyBtn.defaultHeight)"
    };
    public JButton cancelBtn;
    public JButton resultsAreaSaveBtn;
    public JLabel statusLabel;
    public BevelBorderPanel statusPnl;
    public JButton proGuardAreaPasteBtn;
    public JTextArea proGuardArea;
    public JButton helpBtn;
    public JButton proGuardScriptOpenBtn;
    public JTextField proGuardScriptFileNameFld;
    public UserPreferences userPreferences;
    public JTextArea resultsArea;
    public JButton translateBtn;
    public JButton resultsAreaCopyBtn;
    public boolean firstShow = true;

    public ProGuardTranslateDialog(ZkmMainWindow zkmMainWindow, UserPreferences userPreferences1) {
        super(zkmMainWindow, "Zelix KlassMaster - ProGuard Configuration Translate");
        this.initPreferences(userPreferences1);
    }

    public void pasteProGuardConfig() {
        this.proGuardArea.selectAll();
        this.proGuardArea.paste();
        this.proGuardArea.setCaretPosition(0);
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }

    public void showDefaultCursor() {
        Cursor cursor1 = new Cursor(0);
        this.setCursor(cursor1);
        this.statusPnl.setCursor(cursor1);
        this.proGuardArea.setCursor(cursor1);
        this.resultsArea.setCursor(cursor1);
        this.proGuardScriptFileNameFld.setCursor(cursor1);
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2 && mouseEvent.getSource() == this.proGuardScriptFileNameFld) {
            this.openProGuardFile();
        }
    }

    @Override
    public void buildContents(Object object, Object object1, Object object2, Object object3, Object object4, Object object5) {
        this.setFont(KlassMaster.getDefaultFont());
        Container container1 = this.getContentPane();
        ConstraintLayout constraintLayout1 = new ConstraintLayout(container1);
        container1.setLayout(constraintLayout1);
        this.proGuardScriptFileNameFld = new JTextField();
        this.proGuardScriptFileNameFld.setEditable(false);
        this.proGuardScriptFileNameFld.setToolTipText(GuiResources.getTooltipText("PROGUARD_SCRIPT_FLD_PROGUARD_SCRIPT_TRANSLATE"));
        this.proGuardArea = new JTextArea();
        Font font = this.proGuardArea.getFont();
        Font font1 = new Font("Monospaced", font.getStyle(), font.getSize());
        this.proGuardArea.setFont(font1);
        this.proGuardArea.setToolTipText(GuiResources.getTooltipText("PROGUARD_AREA_PROGUARD_SCRIPT_TRANSLATE"));
        this.resultsArea = new JTextArea(resultsPlaceholderText);
        this.resultsArea.setFont(font1);
        this.resultsArea.setEditable(false);
        this.resultsArea.setToolTipText(GuiResources.getTooltipText("RESULTS_PROGUARD_SCRIPT_TRANSLATE"));
        this.proGuardScriptOpenBtn = new JButton("Open");
        this.proGuardScriptOpenBtn.setToolTipText(GuiResources.getTooltipText("OPEN_PROGUARD_SCRIPT_TRANSLATE"));
        this.resultsAreaSaveBtn = new JButton("Save");
        this.resultsAreaSaveBtn.setToolTipText(GuiResources.getTooltipText("SAVE_PROGUARD_SCRIPT_TRANSLATE"));
        this.proGuardAreaPasteBtn = new JButton("Paste");
        this.proGuardAreaPasteBtn.setToolTipText(GuiResources.getTooltipText("PASTE_CONTENTS"));
        this.resultsAreaCopyBtn = new JButton("Copy");
        this.resultsAreaCopyBtn.setToolTipText(GuiResources.getTooltipText("COPY_PROGUARD_SCRIPT_TRANSLATE"));
        this.translateBtn = new JButton("Translate");
        this.cancelBtn = new JButton("Cancel");
        this.cancelBtn.setToolTipText(GuiResources.getTooltipText("CANCEL_DIALOG"));
        this.helpBtn = new JButton("Help");
        this.helpBtn.setToolTipText(GuiResources.getTooltipText("SCREEN_HELP"));
        JLabel jLabel = new JLabel("ProGuard configuration file :");
        container1.add(this.proGuardScriptFileNameFld, "proGuardScriptFileNameFld");
        BorderedPanel borderedPanel = new BorderedPanel();
        ConstraintLayout constraintLayout2 = new ConstraintLayout(borderedPanel);
        borderedPanel.setLayout(constraintLayout2);
        borderedPanel.add(new ZkmScrollPane(this.proGuardArea), "proGuardArea");
        borderedPanel.add(this.proGuardAreaPasteBtn, "proGuardAreaPasteBtn");
        JLabel jLabel1 = new JLabel("Open a ProGuard configuration file or paste contents into here...");
        borderedPanel.add(jLabel1, "proGuardAreaLbl");
        container1.add(borderedPanel, "proGuardPnl");
        BorderedPanel borderedPanel1 = new BorderedPanel();
        ConstraintLayout constraintLayout3 = new ConstraintLayout(borderedPanel1);
        borderedPanel1.setLayout(constraintLayout3);
        borderedPanel1.add(new ZkmScrollPane(this.resultsArea), "resultsArea");
        borderedPanel1.add(this.resultsAreaSaveBtn, "resultsAreaSaveBtn");
        borderedPanel1.add(this.resultsAreaCopyBtn, "resultsAreaCopyBtn");
        JLabel jLabel2 = new JLabel("Translated ZKM Script will appear here...");
        borderedPanel1.add(jLabel2, "resultsAreaLbl");
        container1.add(borderedPanel1, "resultsPnl");
        this.statusPnl = new BevelBorderPanel(false);
        this.statusPnl.setLayout(new BorderLayout());
        container1.add(this.statusPnl, "statusPnl");
        this.statusLabel = new JLabel(initialStatusText);
        this.statusPnl.add(this.statusLabel, "Center");
        container1.add(this.proGuardScriptOpenBtn, "proGuardScriptOpenBtn");
        container1.add(this.translateBtn, "translateBtn");
        container1.add(this.cancelBtn, "cancelBtn");
        container1.add(this.helpBtn, "helpBtn");
        this.proGuardScriptOpenBtn.addActionListener(this);
        this.resultsAreaSaveBtn.addActionListener(this);
        this.proGuardAreaPasteBtn.addActionListener(this);
        this.resultsAreaCopyBtn.addActionListener(this);
        this.translateBtn.addActionListener(this);
        this.cancelBtn.addActionListener(this);
        this.helpBtn.addActionListener(this);
        this.proGuardScriptOpenBtn.addKeyListener(this);
        this.resultsAreaSaveBtn.addKeyListener(this);
        this.proGuardAreaPasteBtn.addKeyListener(this);
        this.resultsAreaCopyBtn.addKeyListener(this);
        this.translateBtn.addKeyListener(this);
        this.cancelBtn.addKeyListener(this);
        this.helpBtn.addKeyListener(this);
        this.proGuardScriptFileNameFld.addActionListener(this);
        this.proGuardScriptFileNameFld.addMouseListener(this);
        this.proGuardArea.getDocument().addDocumentListener(new TranslateInputListener(this));
        container1.add(jLabel, "proGuardScriptFileNameLbl");
        constraintLayout1.setConstraints(layoutConstraints);
        constraintLayout2.setConstraints(proGuardPanelConstraints);
        constraintLayout3.setConstraints(resultsPanelConstraints);
        this.translateBtn.setEnabled(false);
        this.setResizable(true);
        this.setIconImage(KlassMaster.getLogoImage(this));
        SwingUtils.packOnEdt(this);
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        try {
            if (keyEvent.getKeyCode() == 10) {
                Component component1 = (Component) keyEvent.getSource();
                if (component1 == this.proGuardScriptOpenBtn) {
                    this.openProGuardFile();
                } else if (component1 == this.resultsAreaSaveBtn) {
                    this.saveResults();
                } else if (component1 == this.proGuardAreaPasteBtn) {
                    this.pasteProGuardConfig();
                } else if (component1 == this.resultsAreaCopyBtn) {
                    this.copyResults();
                } else if (component1 == this.translateBtn) {
                    this.startTranslation();
                } else if (component1 == this.cancelBtn) {
                    this.cancelDialog();
                } else if (component1 == this.helpBtn) {
                    this.showHelp();
                }
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
    }

    public void initPreferences(UserPreferences userPreferences1) {
        this.userPreferences = userPreferences1;
        String string = userPreferences1.getBytecodeClasspath();
        if (string != null) {
            string.trim();
        }

        this.showCentered(super.ownerFrame != null);
        this.focusOpenButton();
    }

    public void copyResults() {
        this.resultsArea.selectAll();
        this.resultsArea.copy();
    }

    public void saveResults() {
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
                final String text = this.resultsArea.getText();
                bufferedWriter = new BufferedWriter(new FileWriter(selectedFile));
                bufferedWriter.write(text, 0, text.length());
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

    public void focusOpenButton() {
        SwingUtils.requestFocusOnEdt(this.proGuardScriptOpenBtn);
    }

    @Override
    public void cancelDialog() throws ZkmException, IOException {
        this.closeFrame();
    }

    public void showTranslationResult(String string) {
        this.resultsArea.setText(string);
        this.resultsArea.setCaretPosition(0);
        this.showDefaultCursor();
        this.statusLabel.setText(" ");
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            Object object = actionEvent.getSource();
            if (object == this.proGuardScriptOpenBtn) {
                this.openProGuardFile();
            } else if (object == this.resultsAreaSaveBtn) {
                this.saveResults();
            } else if (object == this.proGuardAreaPasteBtn) {
                this.pasteProGuardConfig();
            } else if (object == this.resultsAreaCopyBtn) {
                this.copyResults();
            } else if (object == this.translateBtn) {
                this.startTranslation();
            } else if (object == this.cancelBtn) {
                this.cancelDialog();
            } else if (object == this.helpBtn) {
                this.showHelp();
            } else if ((object == this.proGuardScriptFileNameFld || object == this.proGuardArea) && this.translateBtn.isEnabled()) {
                this.startTranslation();
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public void showHelp() {
        GuiResources.showHelpTopic("040");
    }

    public void startTranslation() {
        ProGuardTranslateThread proGuardTranslateThread = new ProGuardTranslateThread(this);
        this.resultsArea.setText("");
        this.statusLabel.setText("Analyzing.  Please wait...");
        this.showWaitCursor();
        proGuardTranslateThread.start();
    }

    public void openProGuardFile() {
        File file1;
        if (this.userPreferences.getProGuardConfigDirectory() != null) {
            File file2 = new File(this.userPreferences.getProGuardConfigDirectory());
            if (file2.exists() && file2.isDirectory()) {
                file1 = file2;
            } else {
                file1 = new File(SystemEnvironmentConstants.USER_DIR);
            }
        } else {
            file1 = new File(SystemEnvironmentConstants.USER_DIR);
        }

        ZkmFileFilter[] zkmFileFilters = new ZkmFileFilter[]{new TextFileFilter(), new AllFilesFilter()};
        FileSelector fileSelector1 = new FileSelector(file1, false, 1, zkmFileFilters, true);
        if (fileSelector1.showOpenDialog(this, "Select a ProGuard Configuration File") == 1) {
            File file3 = fileSelector1.getSelectedFile();

            File file5;
            label28:
            {
                try {
                    String string = ZkmFileUtils.readFileAsString(file3);
                    this.proGuardScriptFileNameFld.setText(file3.getAbsolutePath());
                    this.proGuardScriptFileNameFld.setCaretPosition(0);
                    this.proGuardArea.setText(string);
                    this.proGuardArea.setCaretPosition(0);
                } catch (IOException iOException) {
                    new MessageBoxDialog(this, "File Error", "'" + file3.getAbsolutePath() + "' : '" + iOException.getMessage() + "'");
                    file5 = file3.getParentFile();
                    break label28;
                }

                file5 = file3.getParentFile();
            }

            File file4 = file5;
            if (file4 != null && !file4.equals(file1)) {
                this.userPreferences.setProGuardConfigDirectory(file4.getAbsolutePath());
                this.userPreferences.savePreferences();
            }
        }
    }

    public void showWaitCursor() {
        Cursor cursor1 = new Cursor(3);
        this.setCursor(cursor1);
        this.statusPnl.setCursor(cursor1);
        this.proGuardArea.setCursor(cursor1);
        this.resultsArea.setCursor(cursor1);
        this.proGuardScriptFileNameFld.setCursor(cursor1);
    }

    @Override
    public void closeFrame() throws ZkmException, IOException {
        super.closeFrame();
        if (super.ownerFrame != null) {
            ((ZkmMainWindow) super.ownerFrame).setBusy(false);
        } else {
            System.exit(0);
        }
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
    }
}
