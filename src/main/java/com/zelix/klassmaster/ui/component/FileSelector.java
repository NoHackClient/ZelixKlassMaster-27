package com.zelix.klassmaster.ui.component;

import com.zelix.klassmaster.ui.dialog.EscapeClosingDialogBase;

import java.awt.Container;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class FileSelector extends ObservingPanel implements PropertyChangeListener, FileChooserComponent {
    public static File defaultDirectory = new File(System.getProperty("user.dir"));
    public static String newFolderPrompt = "New Folder Name";
    public static String selectorLayoutSpec = "midPoint=container.width/2;directoryComboBox.top=5;directoryComboBox.left=5;directoryComboBox.right=container.right-5;upFolderButton.top=directoryComboBox.bottom+5;upFolderButton.left=container.left+5;upFolderButton.right=midPoint-3;newFolderButton.top=directoryComboBox.bottom+5;newFolderButton.left=upFolderButton.right+5;newFolderButton.right=container.right-5;fileList.left=5;fileList.right=container.right-5;fileList.top=upFolderButton.bottom+10;fileList.bottom=container.bottom-lowerComponetsHeight;filesDisplay.top=fileList.bottom+10;filesDisplay.right=container.right-5;filterComboBox.left=5;filterComboBox.top=filesDisplay.bottom+5;filterComboBox.right=container.right-5;lowerComponetsHeight=filesDisplay.defaultHeight+filterComboBox.defaultHeight+20;";
    public static String filesDisplayLayoutSpec = "filesDisplay.left=5";
    public static String labeledFilesDisplayLayoutSpec = "filesDisplayLbl.left=5;filesDisplayLbl.centerY=filesDisplay.centerY;filesDisplay.left=filesDisplayLbl.right+1";
    public static String[] dialogLayoutSpec = new String[]{
            "layout.minWidth=btnWidth*3+20",
            "btnWidth=max(approveBtn.defaultWidth,cancelBtn.defaultWidth)",
            "btnHeight=max(approveBtn.defaultHeight,cancelBtn.defaultHeight)",
            "fileSelector.top=5",
            "fileSelector.left=5",
            "fileSelector.right=container.right-5",
            "fileSelector.bottom=container.bottom-approveBtn.defaultHeight-10",
            "approveBtn.bottom=container.bottom-5",
            "approveBtn.centerX=container.width*25/100",
            "approveBtn.width=btnWidth",
            "cancelBtn.bottom=approveBtn.bottom",
            "cancelBtn.centerX=container.width*75/100",
            "cancelBtn.width=btnWidth"
    };
    public NewFolderButton newFolderButton;
    public DirectoryComboBox directoryComboBox;
    public JButton approveBtn;
    public EscapeClosingDialogBase dialog;
    public FilesDisplay filesDisplay;
    public UpFolderButton upFolderButton;
    public FilterComboBox filterComboBox;
    public JButton cancelBtn;
    public FileListView fileListView;
    public int dialogResult;
    public boolean multiSelection;
    public boolean showFileNameLabel;

    public static NewFolderButton getNewFolderButton(FileSelector fileSelector1) {
        return fileSelector1.newFolderButton;
    }

    public void buildComponents(File file1, boolean bl, int ba, ZkmFileFilter[] zkmFileFilters, boolean bl1) {
        ConstraintLayout constraintLayout1 = new ConstraintLayout(this);
        this.setLayout(constraintLayout1);
        this.fileListView = new FileListView(file1, bl, ba, zkmFileFilters[0]);
        this.directoryComboBox = new DirectoryComboBox(file1);
        this.upFolderButton = new UpFolderButton();
        this.newFolderButton = new NewFolderButton();
        this.add(this.directoryComboBox, "directoryComboBox");
        this.add(this.upFolderButton, "upFolderButton");
        this.add(this.newFolderButton, "newFolderButton");
        this.add(new ZkmScrollPane(this.fileListView), "fileList");
        String string = selectorLayoutSpec;
        if (bl1) {
            JLabel jLabel = new JLabel("File name:");
            this.add(jLabel, "filesDisplayLbl");
            string = string + labeledFilesDisplayLayoutSpec;
        } else {
            string = string + filesDisplayLayoutSpec;
        }

        this.filesDisplay = new FilesDisplay(file1, bl1, ba, bl);
        this.add(this.filesDisplay, "filesDisplay");
        this.filterComboBox = new FilterComboBox(zkmFileFilters);
        this.add(this.filterComboBox, "filterComboBox");
        this.filesDisplay.setFont(this.filterComboBox.getFont());
        this.fileListView.addPropertyChangeListener(this.directoryComboBox);
        this.fileListView.addPropertyChangeListener(this.filesDisplay);
        this.directoryComboBox.addPropertyChangeListener(this.fileListView);
        this.filesDisplay.addPropertyChangeListener(this.fileListView);
        this.fileListView.addPropertyChangeListener(this.filesDisplay);
        this.filesDisplay.addPropertyChangeListener(this);
        this.fileListView.addPropertyChangeListener(this);
        this.filterComboBox.addPropertyChangeListener(this.fileListView);
        FileSelectorButtonListener fileSelectorButtonListener = new FileSelectorButtonListener(this);
        FileSelectorKeyListener fileSelectorKeyListener = new FileSelectorKeyListener(this);
        this.upFolderButton.addActionListener(fileSelectorButtonListener);
        this.upFolderButton.addKeyListener(fileSelectorKeyListener);
        this.newFolderButton.addActionListener(fileSelectorButtonListener);
        this.newFolderButton.addKeyListener(fileSelectorKeyListener);
        constraintLayout1.parseConstraints(string);
    }

    public void promptCreateFolder() {
        String string = JOptionPane.showInputDialog(this, newFolderPrompt, newFolderPrompt, 3);
        if (string != null && string.trim().length() > 0 && !this.fileListView.createFolder(string)) {
            JOptionPane.showMessageDialog(this, "'" + string + "' could not be created", "Error", 0);
        }
    }

    public int showSelectorDialog(JFrame jFrame, String string, String string1, String string2, String string3, String string4, String string5, String string6) {
        this.dialog = new FileSelectorOwnedDialog(this, jFrame, string);
        Container container1 = this.dialog.getContentPane();
        ConstraintLayout constraintLayout1 = new ConstraintLayout(container1);
        container1.setLayout(constraintLayout1);
        container1.add(this, "fileSelector");
        this.approveBtn = new JButton(string1);
        this.approveBtn.setToolTipText(string2);
        container1.add(this.approveBtn, "approveBtn");
        this.cancelBtn = new JButton(string3);
        this.cancelBtn.setToolTipText(string4);
        container1.add(this.cancelBtn, "cancelBtn");
        constraintLayout1.setConstraints(dialogLayoutSpec);
        FileSelectorFieldKeyListener fileSelectorFieldKeyListener = new FileSelectorFieldKeyListener(this);
        this.approveBtn.addActionListener(fileSelectorFieldKeyListener);
        this.cancelBtn.addActionListener(fileSelectorFieldKeyListener);
        this.approveBtn.addKeyListener(fileSelectorFieldKeyListener);
        this.cancelBtn.addKeyListener(fileSelectorFieldKeyListener);
        this.fileListView.addPropertyChangeListener(new FileSelectionListener(this));
        this.filesDisplay.addPropertyChangeListener(new FileNameChangeListener(this));
        this.directoryComboBox.setToolTipText(string5);
        this.filesDisplay.setToolTipText(string6);
        SwingUtils.packOnEdt(this.dialog);
        this.dialog.showCenteredOnOwner();
        return this.dialogResult;
    }

    public static UpFolderButton getUpFolderButton(FileSelector fileSelector1) {
        return fileSelector1.upFolderButton;
    }

    public static JButton getApproveButton(FileSelector fileSelector1) {
        return fileSelector1.approveBtn;
    }

    public static JButton getCancelButton(FileSelector fileSelector1) {
        return fileSelector1.cancelBtn;
    }

    public void cancelDialog() {
        this.dialogResult = 0;
        this.dialog.setVisible(false);
        this.dialog.dispose();
    }

    public void setSelectedFile(File file1) {
        if (file1 != null) {
            this.filesDisplay.setSelectedFile(file1);
            this.filesDisplay.selectAll();
            SwingUtils.requestFocusOnEdt(this.filesDisplay);
        }
    }

    public File[] getSelectedFiles() {
        return this.filesDisplay.getSelectedFiles();
    }

    public FileSelector(File file1, boolean multiSelection, int ba, ZkmFileFilter[] zkmFileFilters, boolean showFileNameLabel) {
        label16:
        {
            this.dialogResult = -1;
            File file2;
            if (file1 != null) {
                if (file1.isDirectory()) {
                    this.multiSelection = multiSelection;
                    break label16;
                }

                file2 = defaultDirectory;
            } else {
                file2 = defaultDirectory;
            }

            file1 = file2;
            this.multiSelection = multiSelection;
        }

        this.showFileNameLabel = showFileNameLabel;
        this.buildComponents(file1, multiSelection, ba, zkmFileFilters, showFileNameLabel);
    }

    public void collectFilesRecursively(File file1, ArrayList arrayList) {
        if (file1.isDirectory()) {
            File[] files = this.listSubdirectories(file1);
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    arrayList.add(files[i]);
                    this.collectFilesRecursively(files[i], arrayList);
                }
            }
        } else {
            arrayList.add(file1);
        }
    }

    public String getCurrentDirectoryPath() {
        return this.getCurrentDirectory().getAbsolutePath();
    }

    public int showOpenDialog(JFrame jFrame, String string) {
        String string1;
        String string2;
        if (this.multiSelection) {
            string1 = "Open selected files";
            string2 = "Cancel";
        } else {
            string1 = "Open selected file";
            string2 = "Cancel";
        }

        return this.showSelectorDialog(jFrame, string, "OK", string1, string2, "Cancel", "List files at this level", "Selected files");
    }

    public File getCurrentDirectory() {
        return this.fileListView.getCurrentDirectory();
    }

    public void goToParentDirectory() {
        this.fileListView.goToParentDirectory();
    }

    public static EscapeClosingDialogBase getDialog(FileSelector fileSelector1) {
        return fileSelector1.dialog;
    }

    public File getSelectedFile() {
        return this.filesDisplay.getSelectedFile();
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if (propertyChangeEvent.getPropertyName().equals("selectionChanged")) {
            this.firePropertyChange("selectionChanged", propertyChangeEvent.getOldValue(), propertyChangeEvent.getNewValue());
        } else if (propertyChangeEvent.getPropertyName().equals("finalSelection")) {
            this.firePropertyChange("finalSelection", propertyChangeEvent.getOldValue(), propertyChangeEvent.getNewValue());
        }
    }

    public void approveSelection() {
        File[] files = this.filesDisplay.getSelectedFiles();
        EscapeClosingDialogBase escapeClosingDialogBase;
        if (files != null) {
            if (files.length > 0) {
                this.dialogResult = 1;
                escapeClosingDialogBase = this.dialog;
            } else {
                escapeClosingDialogBase = this.dialog;
            }
        } else {
            escapeClosingDialogBase = this.dialog;
        }

        escapeClosingDialogBase.setVisible(false);
        this.dialog.dispose();
    }

    public void selectFileInList(File file1) {
        this.fileListView.selectFile(file1, true);
    }

    public int showSaveDialog(JFrame jFrame) {
        return this.showSelectorDialog(jFrame, "Save ZKM Script File", "Save", "Save file in current folder", "Cancel", "Cancel save", "Save in", (String) null);
    }

    public File[] listSubdirectories(File file1) {
        File[] files = null;
        if (file1.isDirectory() && file1.exists()) {
            files = file1.listFiles(new DirectoryFilenameFilter());
        }

        return files;
    }

    public File[] listDirectoryTree(File file1) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(file1);
        this.collectFilesRecursively(file1, arrayList);
        arrayList.size();
        Collections.sort(arrayList, DirectoryFirstFileComparator.getInstance());
        return ((java.io.File[]) (arrayList.toArray(new File[arrayList.size()])));
    }
}
