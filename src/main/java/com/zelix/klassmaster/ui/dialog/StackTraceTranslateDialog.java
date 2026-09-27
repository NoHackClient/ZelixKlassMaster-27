package com.zelix.klassmaster.ui.dialog;

import com.zelix.GuiResources;
import com.zelix.UserPreferences;
import com.zelix.klassmaster.KlassMaster;
import com.zelix.klassmaster.archive.ZkmFileUtils;
import com.zelix.klassmaster.changelog.StackTraceTranslator;
import com.zelix.klassmaster.classfile.hierarchy.ClasspathClassLoader;
import com.zelix.klassmaster.classfile.hierarchy.ZkmClasspath;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.ZkmMainWindow;
import com.zelix.klassmaster.ui.component.BevelBorderPanel;
import com.zelix.klassmaster.ui.component.BorderedPanel;
import com.zelix.klassmaster.ui.component.ConstraintLayout;
import com.zelix.klassmaster.ui.component.SwingUtils;
import com.zelix.klassmaster.ui.component.ZkmScrollPane;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.SystemEnvironmentConstants;
import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class StackTraceTranslateDialog extends OwnedFrameDialogBase implements ActionListener, KeyListener, ItemListener, MouseListener {
    public static String selectChangeLogStatus = "Select the Change Log File";
    public static String resultsPlaceholderText = "1) Select the change log file produced when the bytecode was obfuscated"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "2) Specify the obfuscated bytecode classpath which must include"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "   a) the obfuscated bytecode that generated the stack trace"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "   b) any supporting class libraries required to run the obfuscated bytecode"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "   c) an archive containing java.lang.Object (eg. rt.jar)"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "3) Paste the stack trace into the area above"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "4) Click on the <Translate> button"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "5) The translated stack trace will appear here";
    public static String[] layoutConstraints = new String[]{
            "layout.minWidth=btnWidth*4",
            "layout.width=550",
            "layout.minHeight=rowHeight*15",
            "layout.height=550",
            "logFileNameLbl.left=5",
            "logFileNameLbl.centerY=logFileNameFld.centerY",
            "logBrowseBtn.left=lblWidth+10",
            "logBrowseBtn.centerY=logFileNameFld.centerY",
            "logFileNameFld.top=5",
            "logFileNameFld.left=logBrowseBtn.right+5",
            "logFileNameFld.right=container.right-5",
            "bytecodePathLbl.left=5",
            "bytecodePathLbl.centerY=bytecodePathFld.centerY",
            "bytecodePathBrowseBtn.left=lblWidth+10",
            "bytecodePathBrowseBtn.centerY=bytecodePathFld.centerY",
            "bytecodePathFld.top=logFileNameFld.top+rowHeight+5",
            "bytecodePathFld.left=bytecodePathBrowseBtn.right+5",
            "bytecodePathFld.right=container.right-5",
            "useBytecodeChk.left=5",
            "useBytecodeChk.top=bytecodePathLbl.bottom+7",
            "tracePnl.left=5",
            "tracePnl.top=useBytecodeChk.bottom+10",
            "tracePnl.right=container.right-5",
            "tracePnl.height=rowHeight*6",
            "resultsPnl.left=5",
            "resultsPnl.top=tracePnl.bottom+10",
            "resultsPnl.right=container.right-5",
            "resultsPnl.bottom=container.bottom-statusPnl.height-btnHeight-25",
            "lblWidth=max(logFileNameLbl.defaultWidth, bytecodePathLbl.defaultWidth)",
            "rowHeight=max(logFileNameLbl.defaultHeight, logBrowseBtn.defaultHeight, logFileNameFld.defaultHeight)",
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
    public static String[] tracePanelConstraints = new String[]{
            "traceAreaLbl.left=5",
            "traceAreaLbl.bottom=rowHeight+5",
            "traceAreaPasteBtn.bottom=traceAreaLbl.bottom",
            "traceAreaPasteBtn.right=container.right-5",
            "traceArea.left=5",
            "traceArea.top=traceAreaLbl.bottom+4",
            "traceArea.right=container.right-5",
            "traceArea.bottom=container.bottom-5",
            "rowHeight=max(traceAreaLbl.defaultHeight, traceAreaPasteBtn.defaultHeight)"
    };
    public static String[] resultsPanelConstraints = new String[]{
            "resultsAreaLbl.left=5",
            "resultsAreaLbl.bottom=rowHeight+5",
            "resultsAreaCopyBtn.bottom=resultsAreaLbl.bottom",
            "resultsAreaCopyBtn.right=container.right-5",
            "resultsArea.left=5",
            "resultsArea.top=resultsAreaLbl.bottom+4",
            "resultsArea.right=container.right-5",
            "resultsArea.bottom=container.bottom-5",
            "rowHeight=max(resultsAreaLbl.defaultHeight, resultsAreaCopyBtn.defaultHeight)"
    };
    public JCheckBox useBytecodeChk;
    public JButton cancelBtn;
    public UserPreferences userPreferences;
    public JButton logBrowseBtn;
    public JButton translateBtn;
    public JButton traceAreaPasteBtn;
    public JButton resultsAreaCopyBtn;
    public JTextField logFileNameFld;
    public StackTraceTranslator translator;
    public JTextArea traceArea;
    public BevelBorderPanel statusPnl;
    public JButton helpBtn;
    public JTextField bytecodePathFld;
    public JLabel statusLabel;
    public JTextArea resultsArea;
    public JButton bytecodePathBrowseBtn;
    public List changeLogFiles = new ArrayList();
    public boolean translatorStale = true;
    public ZkmClasspath bytecodeClasspath;

    public void browseChangeLogs() {
        StackTraceTranslateCallback stackTraceTranslateCallback = new StackTraceTranslateCallback(this);
        new ChangeLogListDialog(
                this,
                this.changeLogFiles,
                this.logFileNameFld.getText().trim(),
                GuiResources.getTooltipText("CHANGELOG_LIST"),
                this.userPreferences,
                stackTraceTranslateCallback
        );
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

    public void focusLogBrowseButton() {
        SwingUtils.requestFocusOnEdt(this.logBrowseBtn);
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
    }

    public void browseBytecodeClasspath() {
        StackTraceClasspathCallback stackTraceClasspathCallback = new StackTraceClasspathCallback(this);
        new StackTraceClasspathDialog(
                this,
                this.bytecodeClasspath,
                GuiResources.getTooltipText("CLASSPATH_MSG_CHANGELOG_TRANSLATE"),
                GuiResources.getTooltipText("BROWSE_BYTECODE_OK_CHANGELOG_TRANSLATE"),
                this.userPreferences,
                stackTraceClasspathCallback
        );
    }

    @Override
    public void closeFrame() throws ZkmException, IOException {
        super.closeFrame();
        if (this.translator != null) {
            this.translator.dispose();
        }

        if (super.ownerFrame != null) {
            ((ZkmMainWindow) super.ownerFrame).setBusy(false);
        } else {
            System.exit(0);
        }
    }

    @Override
    public void buildContents(Object object, Object object1, Object object2, Object object3, Object object4, Object object5) {
        this.setFont(KlassMaster.getDefaultFont());
        Container container1 = this.getContentPane();
        ConstraintLayout constraintLayout1 = new ConstraintLayout(container1);
        container1.setLayout(constraintLayout1);
        this.logFileNameFld = new JTextField();
        this.logFileNameFld.setEditable(false);
        this.logFileNameFld.setToolTipText(GuiResources.getTooltipText("CHANGELOG_FLD_CHANGELOG_TRANSLATE"));
        this.bytecodePathFld = new JTextField();
        this.bytecodePathFld.setEditable(false);
        this.bytecodePathFld.setToolTipText(GuiResources.getTooltipText("CLASSPATH_MSG_CHANGELOG_TRANSLATE"));
        this.useBytecodeChk = new JCheckBox("Analyze obfuscated bytecode (highly recommended)");
        this.useBytecodeChk.setToolTipText(GuiResources.getTooltipText("CLASSPATH_CHK_CHANGELOG_TRANSLATE"));
        this.traceArea = new JTextArea();
        Font font = this.traceArea.getFont();
        Font font1 = new Font("Monospaced", font.getStyle(), font.getSize());
        this.traceArea.setFont(font1);
        this.traceArea.setToolTipText(GuiResources.getTooltipText("TRACE_CHANGELOG_TRANSLATE"));
        this.resultsArea = new JTextArea(resultsPlaceholderText);
        this.resultsArea.setFont(font1);
        this.resultsArea.setEditable(false);
        this.resultsArea.setToolTipText(GuiResources.getTooltipText("RESULTS_CHANGELOG_TRANSLATE"));
        this.logBrowseBtn = new JButton(". . .");
        this.logBrowseBtn.setToolTipText(GuiResources.getTooltipText("BROWSE_CHANGELOG_TRANSLATE"));
        this.bytecodePathBrowseBtn = new JButton(". . .");
        this.bytecodePathBrowseBtn.setToolTipText(GuiResources.getTooltipText("BROWSE_BYTECODE_CHANGELOG_TRANSLATE"));
        this.traceAreaPasteBtn = new JButton("Paste");
        this.traceAreaPasteBtn.setToolTipText(GuiResources.getTooltipText("PASTE_CONTENTS"));
        this.resultsAreaCopyBtn = new JButton("Copy");
        this.resultsAreaCopyBtn.setToolTipText(GuiResources.getTooltipText("COPY_CHANGELOG_TRANSLATE"));
        this.translateBtn = new JButton("Translate");
        this.translateBtn.setToolTipText(GuiResources.getTooltipText("TRANSLATE_CHANGELOG_TRANSLATE"));
        this.cancelBtn = new JButton("Cancel");
        this.cancelBtn.setToolTipText(GuiResources.getTooltipText("CANCEL_DIALOG"));
        this.helpBtn = new JButton("Help");
        this.helpBtn.setToolTipText(GuiResources.getTooltipText("SCREEN_HELP"));
        JLabel jLabel = new JLabel("Change log file :");
        container1.add(this.logFileNameFld, "logFileNameFld");
        JLabel jLabel1 = new JLabel("Obfuscated bytecode :");
        container1.add(this.bytecodePathFld, "bytecodePathFld");
        container1.add(this.useBytecodeChk, "useBytecodeChk");
        BorderedPanel borderedPanel = new BorderedPanel();
        ConstraintLayout constraintLayout2 = new ConstraintLayout(borderedPanel);
        borderedPanel.setLayout(constraintLayout2);
        borderedPanel.add(new ZkmScrollPane(this.traceArea), "traceArea");
        borderedPanel.add(this.traceAreaPasteBtn, "traceAreaPasteBtn");
        JLabel jLabel2 = new JLabel("Paste original stack trace here...");
        borderedPanel.add(jLabel2, "traceAreaLbl");
        container1.add(borderedPanel, "tracePnl");
        BorderedPanel borderedPanel1 = new BorderedPanel();
        ConstraintLayout constraintLayout3 = new ConstraintLayout(borderedPanel1);
        borderedPanel1.setLayout(constraintLayout3);
        borderedPanel1.add(new ZkmScrollPane(this.resultsArea), "resultsArea");
        borderedPanel1.add(this.resultsAreaCopyBtn, "resultsAreaCopyBtn");
        JLabel jLabel3 = new JLabel("Translated stack trace will appear here...");
        borderedPanel1.add(jLabel3, "resultsAreaLbl");
        container1.add(borderedPanel1, "resultsPnl");
        this.statusPnl = new BevelBorderPanel(false);
        this.statusPnl.setLayout(new BorderLayout());
        container1.add(this.statusPnl, "statusPnl");
        this.statusLabel = new JLabel(selectChangeLogStatus);
        this.statusPnl.add(this.statusLabel, "Center");
        container1.add(this.logBrowseBtn, "logBrowseBtn");
        container1.add(this.bytecodePathBrowseBtn, "bytecodePathBrowseBtn");
        container1.add(this.translateBtn, "translateBtn");
        container1.add(this.cancelBtn, "cancelBtn");
        container1.add(this.helpBtn, "helpBtn");
        this.logBrowseBtn.addActionListener(this);
        this.bytecodePathBrowseBtn.addActionListener(this);
        this.traceAreaPasteBtn.addActionListener(this);
        this.resultsAreaCopyBtn.addActionListener(this);
        this.translateBtn.addActionListener(this);
        this.cancelBtn.addActionListener(this);
        this.helpBtn.addActionListener(this);
        this.logBrowseBtn.addKeyListener(this);
        this.bytecodePathBrowseBtn.addKeyListener(this);
        this.traceAreaPasteBtn.addKeyListener(this);
        this.resultsAreaCopyBtn.addKeyListener(this);
        this.translateBtn.addKeyListener(this);
        this.cancelBtn.addKeyListener(this);
        this.helpBtn.addKeyListener(this);
        this.logFileNameFld.addActionListener(this);
        this.logFileNameFld.addMouseListener(this);
        this.bytecodePathFld.addMouseListener(this);
        this.useBytecodeChk.addItemListener(this);
        this.logFileNameFld.getDocument().addDocumentListener(new TranslateInputDocListener(this));
        this.traceArea.getDocument().addDocumentListener(new TranslateButtonEnabler(this));
        container1.add(jLabel, "logFileNameLbl");
        container1.add(jLabel1, "bytecodePathLbl");
        constraintLayout1.setConstraints(layoutConstraints);
        constraintLayout2.setConstraints(tracePanelConstraints);
        constraintLayout3.setConstraints(resultsPanelConstraints);
        this.translateBtn.setEnabled(false);
        this.setResizable(true);
        this.setIconImage(KlassMaster.getLogoImage(this));
        SwingUtils.packOnEdt(this);
    }

    public void startTranslation() {
        if (this.translatorStale || this.translator == null) {
            this.logFileNameFld.getText().trim();
            this.translator = new StackTraceTranslator(this.changeLogFiles, this.bytecodeClasspath);
            this.translatorStale = false;
        }

        StackTraceTranslateThread stackTraceTranslateThread = new StackTraceTranslateThread(this);
        this.resultsArea.setText("");
        this.statusLabel.setText("Analyzing.  Please wait...");
        this.showWaitCursor();
        stackTraceTranslateThread.start();
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        if (itemEvent.getSource() == this.useBytecodeChk) {
            boolean selected = this.useBytecodeChk.isSelected();
            this.bytecodePathBrowseBtn.setEnabled(selected);
            this.bytecodePathFld.setEnabled(selected);
            if (selected != this.userPreferences.isUseBytecodeClasspath()) {
                this.userPreferences.setUseBytecodeClasspath(selected);
                this.userPreferences.savePreferences();
            }
        }
    }

    public static JTextField accessLogFileNameField(StackTraceTranslateDialog stackTraceTranslateDialog) {
        return stackTraceTranslateDialog.logFileNameFld;
    }

    public static List accessSetChangeLogFiles(StackTraceTranslateDialog stackTraceTranslateDialog, List list1) {
        return stackTraceTranslateDialog.changeLogFiles = list1;
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            Object object = actionEvent.getSource();
            if (object == this.logBrowseBtn) {
                this.browseChangeLogs();
            } else if (object == this.bytecodePathBrowseBtn) {
                this.browseBytecodeClasspath();
            } else if (object == this.traceAreaPasteBtn) {
                this.pasteStackTrace();
            } else if (object == this.resultsAreaCopyBtn) {
                this.copyResults();
            } else if (object == this.translateBtn) {
                this.startTranslation();
            } else if (object == this.cancelBtn) {
                this.cancelDialog();
            } else if (object == this.helpBtn) {
                this.showHelp();
            } else if ((object == this.logFileNameFld || object == this.traceArea) && this.translateBtn.isEnabled()) {
                this.startTranslation();
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        try {
            if (keyEvent.getKeyCode() == 10) {
                Component component1 = (Component) keyEvent.getSource();
                if (component1 == this.logBrowseBtn) {
                    this.browseChangeLogs();
                } else if (component1 == this.bytecodePathBrowseBtn) {
                    this.browseBytecodeClasspath();
                } else if (component1 == this.traceAreaPasteBtn) {
                    this.pasteStackTrace();
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
    public void mousePressed(MouseEvent mouseEvent) {
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }

    public void showHelp() {
        GuiResources.showHelpTopic("035");
    }

    public void showDefaultCursor() {
        Cursor cursor1 = new Cursor(0);
        this.setCursor(cursor1);
        this.statusPnl.setCursor(cursor1);
        this.traceArea.setCursor(cursor1);
        this.resultsArea.setCursor(cursor1);
        this.logFileNameFld.setCursor(cursor1);
        this.bytecodePathFld.setCursor(cursor1);
    }

    public void copyResults() {
        this.resultsArea.selectAll();
        this.resultsArea.copy();
    }

    public StackTraceTranslateDialog(ZkmMainWindow zkmMainWindow, UserPreferences userPreferences1) throws ZkmException, IOException {
        super(zkmMainWindow, "Zelix KlassMaster - Stack Trace Translate");
        this.bytecodeClasspath = zkmMainWindow.getClasspath();
        this.initPreferences(userPreferences1);
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            Object object = mouseEvent.getSource();
            if (object == this.logFileNameFld) {
                this.browseChangeLogs();
            } else if (object == this.bytecodePathFld) {
                this.browseBytecodeClasspath();
            }
        }
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
    }

    public void showWaitCursor() {
        Cursor cursor1 = new Cursor(3);
        this.setCursor(cursor1);
        this.statusPnl.setCursor(cursor1);
        this.traceArea.setCursor(cursor1);
        this.resultsArea.setCursor(cursor1);
        this.logFileNameFld.setCursor(cursor1);
        this.bytecodePathFld.setCursor(cursor1);
    }

    public void initPreferences(UserPreferences userPreferences1) throws ZkmException, IOException {
        this.userPreferences = userPreferences1;
        String string = userPreferences1.getBytecodeClasspath();
        if (string != null && string.trim().length() > 0) {
            this.bytecodeClasspath = new ZkmClasspath(string.trim());
            ZkmClasspath zkmClasspath = this.bytecodeClasspath;
            ClasspathClassLoader classpathClassLoader2 = new ClasspathClassLoader(this.bytecodeClasspath, ZkmFileUtils.caseSensitiveFileSystem);
            zkmClasspath.setClassLoader(classpathClassLoader2);
        } else if (this.bytecodeClasspath == null) {
            this.bytecodeClasspath = new ZkmClasspath(SystemEnvironmentConstants.USER_DIR);
            ZkmClasspath zkmClasspath1 = this.bytecodeClasspath;
            ClasspathClassLoader classpathClassLoader1 = new ClasspathClassLoader(this.bytecodeClasspath, ZkmFileUtils.caseSensitiveFileSystem);
            zkmClasspath1.setClassLoader(classpathClassLoader1);
        }

        this.bytecodeClasspath.locateRuntimeClasses(new ObservableHolder(), new ObservableHolder());
        this.bytecodePathFld.setText(this.bytecodeClasspath.getClasspath());
        this.bytecodePathFld.setCaretPosition(0);
        boolean useBytecodeClasspath = userPreferences1.isUseBytecodeClasspath();
        this.useBytecodeChk.setSelected(useBytecodeClasspath);
        this.bytecodePathBrowseBtn.setEnabled(useBytecodeClasspath);
        this.bytecodePathFld.setEnabled(useBytecodeClasspath);
        this.showCentered(super.ownerFrame != null);
        this.focusLogBrowseButton();
    }

    public void pasteStackTrace() {
        this.traceArea.selectAll();
        this.traceArea.paste();
        this.traceArea.setCaretPosition(0);
    }
}
