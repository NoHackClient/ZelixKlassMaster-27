package com.zelix.klassmaster.ui.dialog;

import com.zelix.GuiResources;
import com.zelix.klassmaster.KlassMaster;
import com.zelix.klassmaster.archive.ZkmFileUtils;
import com.zelix.klassmaster.changelog.AbstractChangeLog;
import com.zelix.klassmaster.changelog.ChangeLogMapping;
import com.zelix.klassmaster.config.ChangeLogInputFile;
import com.zelix.klassmaster.config.ExclusionWizardSettings;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.config.ObfuscateOptions;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.ui.ZkmMainWindow;
import com.zelix.klassmaster.ui.component.AllFilesFilter;
import com.zelix.klassmaster.ui.component.BevelBorderPanel;
import com.zelix.klassmaster.ui.component.ConstraintLayout;
import com.zelix.klassmaster.ui.component.FileSelector;
import com.zelix.klassmaster.ui.component.SwingUtils;
import com.zelix.klassmaster.ui.component.TextFileFilter;
import com.zelix.klassmaster.ui.component.ZkmFileFilter;
import com.zelix.klassmaster.util.DialogCallback;
import com.zelix.klassmaster.util.SystemEnvironmentConstants;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public abstract class ObfuscateOptionsDialogBase extends EscapeClosableFrame implements HelperDialogMarker {
    public Reader inputChangeLogReader;
    public JButton helpBtn;
    public JButton cancelBtn;
    public JCheckBox obfuscateReferencesChk;
    public JComboBox keepInnerClassesComboBox;
    public JCheckBox autoReflectionChk;
    public JButton okBtn;
    public boolean closedByButton;
    public DefaultComboBoxModel keepInnerClassesModel;
    public PrintWriter changeLogWriter;
    public JComboBox methodParameterChangesComboBox;
    public JCheckBox obfuscateMethodParametersChk;
    public FontMetrics fontMetrics;
    public DefaultComboBoxModel methodParameterChangesModel;
    public JButton previousBtn;
    public Font dialogFont = KlassMaster.getDefaultFont();
    public BevelBorderPanel mainPanel = new BevelBorderPanel(true);
    public JCheckBox changeLogInChk = new JCheckBox("use input change log file:");
    public JCheckBox changeLogOutChk = new JCheckBox("produce a change log file:");
    public JTextField logFileInTxt = new JTextField();
    public JButton changeLogInBrowseBtn = new JButton(". . .");
    public JTextField logFileOutTxt = new JTextField();
    public JButton changeLogOutBrowseBtn = new JButton(". . .");
    public ObfuscateOptions obfuscateOptions;
    public ExclusionWizardSettings exclusionSettings;
    public ZkmMainWindow mainWindow;
    public ScriptEnvironment scriptEnvironment;
    public DialogCallback callback;

    public void browseForFile(String string, JTextField jTextField) {
        ZkmFileFilter[] zkmFileFilters = new ZkmFileFilter[]{new TextFileFilter(), new AllFilesFilter()};
        FileSelector fileSelector1 = new FileSelector(new File(SystemEnvironmentConstants.USER_DIR), false, 1, zkmFileFilters, true);
        if (fileSelector1.showOpenDialog(this, string) == 1) {
            File file1 = fileSelector1.getSelectedFile();
            jTextField.setText(file1.getAbsolutePath());
        }
    }

    public boolean loadInputChangeLog(final Object o, final Object o2) throws ZkmException {
        final File file = (File) o2;
        BufferedReader bufferedReader = null;
        try {
            final String changeLogEncoding = ChangeLogMapping.readChangeLogEncoding(file);
            if (changeLogEncoding != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), changeLogEncoding));
            } else {
                bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            }
            final StringBuffer sb = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
                sb.append(HiddenOptionFlags.LINE_SEPARATOR);
            }
            this.closeInputChangeLogReader();
            this.inputChangeLogReader = new StringReader(sb.toString());
            try {
                bufferedReader.close();
            } catch (final IOException ex) {
            }
        } catch (final IOException ex2) {
            final MessageBoxDialog messageBoxDialog = new MessageBoxDialog(this, "File Error", "Error opening " + (String) o);
            this.logFileInTxt.selectAll();
            SwingUtils.requestFocusOnEdt(this.logFileInTxt);
            return false;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (final IOException ex3) {
                }
            }
        }
        return true;
    }

    public boolean validateInputs() throws ZkmException, IOException {
        if (this.changeLogInChk.isSelected()) {
            if (this.logFileInTxt.getText().trim().equals("")) {
                this.logFileInTxt.selectAll();
                new MessageBoxDialog(this, "Error", "A file name for the input change log must be entered");
                SwingUtils.requestFocusOnEdt(this.logFileInTxt);
                return false;
            }

            String string = this.logFileInTxt.getText().trim();
            File file1 = new File(string);
            if (file1.exists() && !file1.canRead()) {
                new MessageBoxDialog(this, "File Error", "Can't read from " + string);
                this.logFileInTxt.selectAll();
                SwingUtils.requestFocusOnEdt(this.logFileInTxt);
                return false;
            }

            if (this.clashesWithLogFile(file1.getAbsolutePath(), this.scriptEnvironment.getLogFileName(), "ZKM log", this.logFileInTxt)) {
                return false;
            }

            if (this.clashesWithLogFile(file1.getAbsolutePath(), this.scriptEnvironment.getTrimLogFileName(), "trim log", this.logFileInTxt)) {
                return false;
            }

            if (!this.loadInputChangeLog(string, file1)) {
                return false;
            }
        } else {
            this.closeInputChangeLogReader();
        }

        if (this.changeLogOutChk.isSelected()) {
            if (this.logFileOutTxt.getText().trim().equals("")) {
                this.logFileOutTxt.selectAll();
                new MessageBoxDialog(this, "Error", "A file name for the change log must be entered");
                SwingUtils.requestFocusOnEdt(this.logFileOutTxt);
                return false;
            } else {
                String string1 = this.logFileOutTxt.getText().trim();
                File file2 = new File(string1);
                if (file2.exists() && !file2.canWrite()) {
                    new MessageBoxDialog(this, "File Error", "Can't write to " + string1);
                    this.logFileOutTxt.selectAll();
                    SwingUtils.requestFocusOnEdt(this.logFileOutTxt);
                    return false;
                } else if (this.clashesWithLogFile(file2.getAbsolutePath(), this.scriptEnvironment.getLogFileName(), "ZKM log", this.logFileOutTxt)) {
                    return false;
                } else {
                    return this.clashesWithLogFile(file2.getAbsolutePath(), this.scriptEnvironment.getTrimLogFileName(), "trim log", this.logFileOutTxt)
                            ? false
                            : this.openOutputChangeLog(string1);
                }
            }
        } else {
            return true;
        }
    }

    public void saveExtraOptions() {
    }

    public void browseInputChangeLog() {
        this.browseForFile("Select Input Change Log File", this.logFileInTxt);
    }

    @Override
    public Action createEscapeAction() {
        return new ObfuscateOptionsEscapeAction(this);
    }

    public boolean openOutputChangeLog(Object object) {
        String string = (String) object;

        try {
            String string1 = AbstractChangeLog.getChangeLogEncoding();
            if (string1 != null) {
                this.changeLogWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(string), string1), 2048));
                this.obfuscateOptions.v = string1;
            } else {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(string));
                this.changeLogWriter = new PrintWriter(new BufferedWriter(outputStreamWriter, 2048));
                this.obfuscateOptions.v = "UTF-8";
            }

            return true;
        } catch (IOException iOException) {
            new MessageBoxDialog(this, "File Error", "Error opening " + string);
            this.logFileOutTxt.selectAll();
            SwingUtils.requestFocusOnEdt(this.logFileOutTxt);
            return false;
        }
    }

    public final void cancelOptions() throws ZkmException, IOException {
        this.closedByButton = true;
        this.closeFrame();
        this.callback.onDialogCancelled();
    }

    public final void acceptOptions() throws ZkmException, IOException {
        if (this.validateInputs()) {
            this.closedByButton = true;
            this.closeFrame();
            this.saveOptions();
            this.callback.onDialogResult_v(this.obfuscateOptions, 1);
        }
    }

    public abstract void buildOptionsPanel(StringBuffer stringBuffer);

    public abstract void showHelp();

    public boolean clashesWithLogFile(String string, String string1, String string2, JTextField jTextField) {
        if (!string.equals(string1) && (ZkmFileUtils.caseSensitiveFileSystem || !string.equalsIgnoreCase(string1))) {
            return false;
        }

        new MessageBoxDialog(this, "Error", "Output change log file \"" + string + "\" clashes with " + string2 + " file name.");
        jTextField.selectAll();
        SwingUtils.requestFocusOnEdt(jTextField);
        return true;
    }

    public void loadOptions() {
        if (this.obfuscateOptions.c) {
            this.changeLogInChk.setSelected(true);
        } else {
            this.changeLogInChk.setSelected(false);
        }

        if (this.obfuscateOptions.g != null && this.obfuscateOptions.g.length > 0) {
            this.logFileInTxt.setText(this.obfuscateOptions.g[0].getFileName());
        }

        this.logFileInTxt.setEnabled(this.changeLogInChk.isSelected());
        this.changeLogInBrowseBtn.setEnabled(this.changeLogInChk.isSelected());
        if (this.obfuscateOptions.d) {
            this.changeLogOutChk.setSelected(true);
        } else {
            this.changeLogOutChk.setSelected(false);
        }

        JTextField jTextField;
        if (this.obfuscateOptions.f != null) {
            this.logFileOutTxt.setText(this.obfuscateOptions.f);
            jTextField = this.logFileOutTxt;
        } else {
            jTextField = this.logFileOutTxt;
        }

        jTextField.setEnabled(this.changeLogOutChk.isSelected());
        this.changeLogOutBrowseBtn.setEnabled(this.changeLogOutChk.isSelected());
        this.keepInnerClassesComboBox.setSelectedIndex(this.obfuscateOptions.b);
        this.obfuscateReferencesChk.setSelected(this.obfuscateOptions.aj == 1);
        this.autoReflectionChk.setSelected(this.obfuscateOptions.r == 1);
        this.methodParameterChangesComboBox.setSelectedIndex(this.obfuscateOptions.aC);
        if (this.obfuscateOptions.d7 == 1) {
            this.obfuscateMethodParametersChk.setSelected(true);
        } else {
            this.obfuscateMethodParametersChk.setSelected(false);
        }

        this.loadExtraOptions();
    }

    public void layoutMainPanel() {
        ConstraintLayout constraintLayout1 = new ConstraintLayout(this.mainPanel);
        this.mainPanel.setLayout(constraintLayout1);
        StringBuffer stringBuffer = new StringBuffer();
        this.buildOptionsPanel(stringBuffer);
        constraintLayout1.parseConstraints(stringBuffer.toString());
    }

    public void createButtons(String string) {
        this.previousBtn = new JButton("Previous");
        this.previousBtn.setToolTipText(GuiResources.getTooltipText("PREVIOUS_SCREEN"));
        this.okBtn = new JButton("OK");
        this.okBtn.setToolTipText(string);
        this.cancelBtn = new JButton("Cancel");
        this.cancelBtn.setToolTipText(GuiResources.getTooltipText("CANCEL_DIALOG"));
        this.helpBtn = new JButton("Help");
        this.helpBtn.setToolTipText(GuiResources.getTooltipText("SCREEN_HELP"));
    }

    public ObfuscateOptionsDialogBase(
            String string,
            String string1,
            ZkmMainWindow zkmMainWindow,
            ObfuscateOptions obfuscateOptions1,
            ExclusionWizardSettings exclusionWizardSettings,
            ScriptEnvironment scriptEnvironment1,
            DialogCallback dialogCallback1
    ) {
        this.obfuscateOptions = obfuscateOptions1;
        this.exclusionSettings = exclusionWizardSettings;
        this.mainWindow = zkmMainWindow;
        this.scriptEnvironment = scriptEnvironment1;
        this.callback = dialogCallback1;
        this.setFont(this.dialogFont);
        this.initComponents(string, string1);
        SwingUtils.requestFocusOnEdt(this.okBtn);
    }

    public static void accessBrowseInputChangeLog(ObfuscateOptionsDialogBase obfuscateOptionsDialogBase) {
        obfuscateOptionsDialogBase.browseInputChangeLog();
    }

    public final void goPrevious() throws ZkmException, IOException {
        if (this.validateInputs()) {
            this.closedByButton = true;
            this.closeFrame();
            this.saveOptions();
            this.callback.onDialogResult_v(this.obfuscateOptions, 2);
        }
    }

    public void loadExtraOptions() {
    }

    public static void accessBrowseOutputChangeLog(ObfuscateOptionsDialogBase obfuscateOptionsDialogBase) {
        obfuscateOptionsDialogBase.browseOutputChangeLog();
    }

    public void saveOptions() {
        this.obfuscateOptions.c = this.changeLogInChk.isSelected();
        String string = this.logFileInTxt.getText().trim();
        if (string.length() > 0) {
            ChangeLogInputFile changeLogInputFile = new ChangeLogInputFile(string);
            ObfuscateOptions obfuscateOptions1;
            if (this.obfuscateOptions.c) {
                changeLogInputFile.setReader(this.inputChangeLogReader);
                obfuscateOptions1 = this.obfuscateOptions;
            } else {
                obfuscateOptions1 = this.obfuscateOptions;
            }

            obfuscateOptions1.g = new ChangeLogInputFile[1];
            this.obfuscateOptions.g[0] = changeLogInputFile;
        } else {
            this.obfuscateOptions.g = null;
        }

        this.obfuscateOptions.d = this.changeLogOutChk.isSelected();
        if (this.obfuscateOptions.d && this.logFileOutTxt.getText().trim().length() > 0) {
            this.obfuscateOptions.h = this.changeLogWriter;
        } else {
            this.obfuscateOptions.h = null;
        }

        this.obfuscateOptions.f = this.logFileOutTxt.getText().trim();
        this.obfuscateOptions.b = this.keepInnerClassesComboBox.getSelectedIndex();
        if (this.autoReflectionChk.isSelected()) {
            this.obfuscateOptions.r = 1;
        } else {
            this.obfuscateOptions.r = 0;
        }

        this.obfuscateOptions.aC = this.methodParameterChangesComboBox.getSelectedIndex();
        if (this.obfuscateReferencesChk.isSelected()) {
            this.obfuscateOptions.aj = 1;
        } else {
            this.obfuscateOptions.aj = 0;
        }

        if (this.obfuscateMethodParametersChk.isSelected()) {
            this.obfuscateOptions.d7 = 1;
        } else {
            this.obfuscateOptions.d7 = 0;
        }

        this.saveExtraOptions();
    }

    @Override
    public final void closeFrame() throws ZkmException, IOException {
        super.closeFrame();
        if (!this.closedByButton) {
            this.callback.onDialogCancelled();
        }
    }

    public void browseOutputChangeLog() {
        this.browseForFile("Select Output Change Log File", this.logFileOutTxt);
    }

    public abstract Dimension getMinimumLayoutSize();

    public void closeInputChangeLogReader() {
        if (this.inputChangeLogReader != null) {
            try {
                this.inputChangeLogReader.close();
            } catch (IOException iOException) {
            }
        }
    }

    public void initComponents(String string, String string1) {
        this.setTitle(string);
        Container container1 = this.getContentPane();
        ConstraintLayout constraintLayout1 = new ConstraintLayout(container1);
        container1.setLayout(constraintLayout1);
        Dimension dimension = this.getMinimumLayoutSize();
        this.createButtons(string1);
        this.changeLogInBrowseBtn.setToolTipText(GuiResources.getTooltipText("SELECT_CHANGE_LOG_IN"));
        this.changeLogOutBrowseBtn.setToolTipText(GuiResources.getTooltipText("SELECT_CHANGE_LOG_OUT"));
        ObfuscateOptionsWindowListener obfuscateOptionsWindowListener = new ObfuscateOptionsWindowListener(this);
        this.addWindowListener(obfuscateOptionsWindowListener);
        ObfuscateDialogButtonListener obfuscateDialogButtonListener = new ObfuscateDialogButtonListener(this);
        this.changeLogInBrowseBtn.addActionListener(obfuscateDialogButtonListener);
        this.changeLogOutBrowseBtn.addActionListener(obfuscateDialogButtonListener);
        this.previousBtn.addActionListener(obfuscateDialogButtonListener);
        this.okBtn.addActionListener(obfuscateDialogButtonListener);
        this.cancelBtn.addActionListener(obfuscateDialogButtonListener);
        this.helpBtn.addActionListener(obfuscateDialogButtonListener);
        ChangeLogOptionsKeyListener changeLogOptionsKeyListener = new ChangeLogOptionsKeyListener(this);
        this.previousBtn.addKeyListener(changeLogOptionsKeyListener);
        this.okBtn.addKeyListener(changeLogOptionsKeyListener);
        this.cancelBtn.addKeyListener(changeLogOptionsKeyListener);
        this.helpBtn.addKeyListener(changeLogOptionsKeyListener);
        ChangeLogOptionListener changeLogOptionListener = new ChangeLogOptionListener(this);
        this.changeLogInChk.addItemListener(changeLogOptionListener);
        this.changeLogOutChk.addItemListener(changeLogOptionListener);
        JScrollPane jScrollPane = new JScrollPane(this.mainPanel);
        container1.add(jScrollPane, "mainPnl");
        this.fontMetrics = this.getFontMetrics(this.dialogFont);
        this.layoutMainPanel();
        container1.add(this.previousBtn, "previousBtn");
        container1.add(this.okBtn, "okBtn");
        container1.add(this.cancelBtn, "cancelBtn");
        container1.add(this.helpBtn, "helpBtn");
        String string2 = "layout.minWidth=" + dimension.width + ";layout.minHeight=" + dimension.height;
        constraintLayout1.parseConstraints(
                "layout.minWidth=btnWidth*4+25;btnWidth=max(previousBtn.defaultWidth, okBtn.defaultWidth, cancelBtn.defaultWidth, helpBtn.defaultWidth);btnHeight=max(previousBtn.defaultHeight, okBtn.defaultHeight, cancelBtn.defaultHeight, helpBtn.defaultHeight);mainPnl.top=0;mainPnl.left=0;mainPnl.bottom=container.bottom-btnHeight-10;mainPnl.right=container.right;previousBtn.top=mainPnl.bottom+5;previousBtn.centerX=container.width*12/100;previousBtn.width=btnWidth;previousBtn.height=btnHeight;okBtn.top=mainPnl.bottom+5;okBtn.centerX=container.width*37/100;okBtn.width=btnWidth;okBtn.height=btnHeight;cancelBtn.top=mainPnl.bottom+5;cancelBtn.centerX=container.width*62/100;cancelBtn.width=btnWidth;cancelBtn.height=btnHeight;helpBtn.top=mainPnl.bottom+5;helpBtn.centerX=container.width*87/100;helpBtn.width=btnWidth;helpBtn.height=btnHeight;"
                        + string2
        );
        this.mainWindow.setEnabled(false);
        this.loadOptions();
        this.setIconImage(KlassMaster.getLogoImage(this));
        this.pack();
        Dimension dimension1 = this.getSize();
        Point point = this.mainWindow.getLocationOnScreen();
        Dimension dimension2 = this.mainWindow.getSize();
        int ba = dimension2.width / 2 - dimension1.width / 2 + point.x;
        int bb = dimension2.height / 2 - dimension1.height / 2 + point.y;
        ba = Math.max(0, ba);
        bb = Math.max(0, bb);
        this.setLocation(ba, bb);
        SwingUtils.setVisibleOnEdt(this);
    }
}
