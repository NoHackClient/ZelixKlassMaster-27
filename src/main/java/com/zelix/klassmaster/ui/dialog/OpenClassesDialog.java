package com.zelix.klassmaster.ui.dialog;

import com.zelix.GuiResources;
import com.zelix.klassmaster.KlassMaster;
import com.zelix.klassmaster.archive.ArchivePathFilter;
import com.zelix.klassmaster.archive.ArchiveScanner;
import com.zelix.klassmaster.archive.ClassFileNameFilter;
import com.zelix.klassmaster.archive.InputFileLocation;
import com.zelix.klassmaster.archive.OpenPathEntry;
import com.zelix.klassmaster.archive.SourceArchive;
import com.zelix.klassmaster.archive.TempFileManager;
import com.zelix.klassmaster.archive.ZipOutputTarget;
import com.zelix.klassmaster.archive.ZkmFileUtils;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.GuiMessageReporter;
import com.zelix.klassmaster.ui.component.ConstraintLayout;
import com.zelix.klassmaster.ui.component.FilePathList;
import com.zelix.klassmaster.ui.component.FileSelector;
import com.zelix.klassmaster.ui.component.JadXmlFileFilter;
import com.zelix.klassmaster.ui.component.SwingUtils;
import com.zelix.klassmaster.ui.component.ZkmFileFilter;
import com.zelix.klassmaster.ui.component.ZkmScrollPane;
import com.zelix.klassmaster.util.DialogCallback;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class OpenClassesDialog extends EscapeClosableFrame implements ActionListener, KeyListener, PropertyChangeListener {
    public JButton removeBtn;
    public JLabel selectedDirLbl;
    public JCheckBox openNestedArchivesChk;
    public boolean closedByButton;
    public JButton cancelBtn;
    public JPanel dirPanel;
    public JButton removeAllBtn;
    public JButton addPlusBtn;
    public DefaultListModel selectedListModel;
    public InputFileLocation[] classLocations;
    public FileSelector fileSelector;
    public FilePathList selectedList;
    public JButton okBtn;
    public JButton helpBtn;
    public InputFileLocation[] inputRoots;
    public JButton addBtn;
    public HashSet selectedFileSet = ZkmUtils.createHashSet();
    public final JFrame ownerFrame;
    public boolean expandToClassFiles;
    public DialogCallback callback;

    public void addSelectionContents() {
        this.showWaitCursor();
        File[] files = this.fileSelector.getSelectedFiles();

        for (int i = 0; i < files.length; i++) {
            files[i].getAbsolutePath();
            File[] files1 = this.fileSelector.listDirectoryTree(files[i]);

            for (int j = 0; j < files1.length; j++) {
                File file1 = files1[j];
                if (this.selectedFileSet.add(file1)) {
                    this.selectedListModel.addElement(file1);
                }
            }
        }

        this.removeAllBtn.setEnabled(true);
        this.showDefaultCursor();
    }

    @Override
    public Action createEscapeAction() {
        return new OpenDialogEscapeAction(this);
    }

    public void afterAddContents() {
        this.removeAllBtn.setEnabled(true);
        this.afterAddSelected();
    }

    public void afterRemoveSelected() {
        SwingUtils.requestFocusOnEdt(this.fileSelector);
        this.removeBtn.setEnabled(false);
    }

    public void initComponents(String string, String string1, String string2, InputFileLocation[] inputFileLocations) {
        Container container1 = this.getContentPane();
        ConstraintLayout constraintLayout1 = new ConstraintLayout(container1);
        container1.setLayout(constraintLayout1);
        this.addWindowListener(new OpenClassesWindowListener(this));
        ZkmFileFilter[] zkmFileFilters = new ZkmFileFilter[]{new JadXmlFileFilter()};
        File file1 = null;
        if (string2 != null) {
            File file2 = new File(string2);
            if (file2.exists() && file2.isDirectory()) {
                file1 = file2;
            }
        }

        this.fileSelector = new FileSelector(file1, true, 3, zkmFileFilters, false);
        container1.add(this.fileSelector, "fileSelector");
        this.fileSelector.addPropertyChangeListener(this);
        this.selectedDirLbl = new JLabel("Selected Folders and Archives");
        this.dirPanel = new JPanel();
        this.dirPanel.setLayout(new BorderLayout());
        container1.add(this.dirPanel, "dirPanel");
        this.selectedListModel = new DefaultListModel();
        this.selectedFileSet = ZkmUtils.createHashSet();
        this.selectedList = new FilePathList(this.selectedListModel);
        this.selectedList.setSelectionMode(2);
        this.selectedList.setToolTipText(GuiResources.getTooltipText("OPEN_SELECTED_LIST"));
        this.dirPanel.add(new ZkmScrollPane(this.selectedList), "Center");
        container1.add(this.selectedDirLbl, "selectedDirLbl");
        if (inputFileLocations != null && inputFileLocations.length > 0) {
            for (int i = 0; i < inputFileLocations.length; i++) {
                File file3 = new File(inputFileLocations[i].getName());
                this.selectedListModel.addElement(file3);
                this.selectedFileSet.add(file3);
            }
        }

        this.openNestedArchivesChk = new JCheckBox("open nested archives", false);
        this.openNestedArchivesChk.setToolTipText(GuiResources.getTooltipText("OPEN_NESTED"));
        container1.add(this.openNestedArchivesChk, "openNestedArchivesChk");
        OpenClassesListListener openClassesListListener = new OpenClassesListListener(this);
        this.selectedList.addListSelectionListener(openClassesListListener);
        this.selectedList.addMouseListener(openClassesListListener);
        this.selectedList.addKeyListener(openClassesListListener);
        this.addBtn = new JButton(">");
        this.addBtn.setToolTipText(GuiResources.getTooltipText("OPEN_ADD_SELECTION"));
        OpenClassesButtonListener openClassesButtonListener = new OpenClassesButtonListener(this);
        this.addBtn.addActionListener(openClassesButtonListener);
        this.addBtn.addKeyListener(openClassesButtonListener);
        container1.add(this.addBtn, "addBtn");
        this.addPlusBtn = new JButton(">>");
        this.addPlusBtn.setToolTipText(GuiResources.getTooltipText("OPEN_ADD_SELECTION_PLUS"));
        this.addPlusBtn.addActionListener(openClassesButtonListener);
        this.addPlusBtn.addKeyListener(openClassesButtonListener);
        container1.add(this.addPlusBtn, "addPlusBtn");
        this.removeBtn = new JButton("<");
        this.removeBtn.setToolTipText(GuiResources.getTooltipText("OPEN_REMOVE_SELECTION"));
        OpenClassesRemoveListener openClassesRemoveListener = new OpenClassesRemoveListener(this);
        this.removeBtn.addActionListener(openClassesRemoveListener);
        this.removeBtn.addKeyListener(openClassesRemoveListener);
        container1.add(this.removeBtn, "removeBtn");
        this.removeAllBtn = new JButton("<<");
        this.removeAllBtn.setToolTipText(GuiResources.getTooltipText("OPEN_REMOVE_SELECTION_PLUS"));
        this.removeAllBtn.addActionListener(openClassesRemoveListener);
        this.removeAllBtn.addKeyListener(openClassesRemoveListener);
        container1.add(this.removeAllBtn, "removeAllBtn");
        StringBuffer stringBuffer = new StringBuffer(
                "layout.minWidth=majorBtnWidth*4+25;layout.minHeight=300;minorBtnHeight=max(addBtn.defaultHeight, addPlusBtn.defaultHeight, removeBtn.defaultHeight, removeAllBtn.defaultHeight);minorBtnWidth=max(addBtn.defaultWidth, addPlusBtn.defaultWidth, removeBtn.defaultWidth, removeAllBtn.defaultWidth);halfMinorBtnWidth=minorBtnWidth/2;containerCenterX=container.width*50/100;fileSelector.top=1;fileSelector.left=5;fileSelector.bottom=container.bottom-majorBtnHeight-10;fileSelector.right=container.width/2 - halfMinorBtnWidth - 5;selectedDirLbl.top=1;selectedDirLbl.left=container.width/2 + halfMinorBtnWidth + 10;dirPanel.top=selectedDirLbl.bottom+2;dirPanel.left=selectedDirLbl.left;dirPanel.bottom=openNestedArchivesChk.top;dirPanel.right=container.width-5;addBtn.centerX=containerCenterX;addBtn.centerY=container.height*30/100;addBtn.width=minorBtnWidth;addBtn.height=minorBtnHeight;addPlusBtn.centerX=containerCenterX;addPlusBtn.centerY=container.height*43/100;addPlusBtn.width=minorBtnWidth;addPlusBtn.height=minorBtnHeight;removeBtn.centerX=containerCenterX;removeBtn.centerY=container.height*56/100;removeBtn.width=minorBtnWidth;removeBtn.height=minorBtnHeight;removeAllBtn.centerX=containerCenterX;removeAllBtn.centerY=container.height*69/100;removeAllBtn.width=minorBtnWidth;removeAllBtn.height=minorBtnHeight;openNestedArchivesChk.left = selectedDirLbl.left;openNestedArchivesChk.bottom = container.height-majorBtnHeight-10;"
        );
        this.createMainButtons(string1, stringBuffer, container1);
        constraintLayout1.parseConstraints(stringBuffer.toString());
        this.setTitle(string);
        this.setIconImage(KlassMaster.getLogoImage(this));
        this.setSize(550, 400);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension dimension1 = this.getSize();
        int ba = dimension.width / 2 - dimension1.width / 2;
        int bb = dimension.height / 2 - dimension1.height / 2;
        this.setLocation(ba, bb);
    }

    public void addSelectedFiles() {
        File[] files = this.fileSelector.getSelectedFiles();

        for (int i = 0; i < files.length; i++) {
            if (this.selectedFileSet.add(files[i])) {
                this.selectedListModel.addElement(files[i]);
            }
        }

        this.removeAllBtn.setEnabled(true);
    }

    public void clearSelectedList() {
        this.selectedListModel = new DefaultListModel();
        this.selectedFileSet = ZkmUtils.createHashSet();
        this.selectedList.setModel(this.selectedListModel);
        this.dirPanel.validate();
    }

    public final void showHelp() {
        GuiResources.showHelpTopic("018");
    }

    public void setAddEnabled(boolean bl) {
        this.addBtn.setEnabled(bl);
    }

    public void createMainButtons(String string, StringBuffer stringBuffer, Container container1) {
        this.okBtn = new JButton(string);
        this.okBtn.setToolTipText(GuiResources.getTooltipText("OK_OPEN"));
        this.okBtn.addActionListener(this);
        this.okBtn.addKeyListener(this);
        container1.add(this.okBtn, "okBtn");
        this.cancelBtn = new JButton("Cancel");
        this.cancelBtn.setToolTipText(GuiResources.getTooltipText("CANCEL_DIALOG"));
        this.cancelBtn.addActionListener(this);
        this.cancelBtn.addKeyListener(this);
        container1.add(this.cancelBtn, "cancelBtn");
        this.helpBtn = new JButton("Help");
        this.helpBtn.setToolTipText(GuiResources.getTooltipText("SCREEN_HELP"));
        this.helpBtn.addActionListener(this);
        this.helpBtn.addKeyListener(this);
        container1.add(this.helpBtn, "helpBtn");
        stringBuffer.append(
                "okBtn.top=fileSelector.bottom+5;okBtn.centerX=container.width*16/100;okBtn.width=majorBtnWidth;okBtn.height=majorBtnHeight;cancelBtn.top=okBtn.top;cancelBtn.centerX=container.width*50/100;cancelBtn.width=majorBtnWidth;cancelBtn.height=majorBtnHeight;helpBtn.top=okBtn.top;helpBtn.centerX=container.width*84/100;helpBtn.width=majorBtnWidth;helpBtn.height=majorBtnHeight;majorBtnHeight=max(okBtn.defaultHeight, cancelBtn.defaultHeight, helpBtn.defaultHeight);majorBtnWidth=max(okBtn.defaultWidth, cancelBtn.defaultWidth, helpBtn.defaultWidth)"
        );
    }

    public void fireOpenCallback(SourceArchive[] sourceArchives1, InputFileLocation[] inputFileLocations, InputFileLocation[] inputFileLocations1, Set set1) throws ZkmException, IOException {
        String string = this.fileSelector.getCurrentDirectoryPath();
        this.callback
                .onDialogResult(
                        this.classLocations,
                        sourceArchives1,
                        inputFileLocations,
                        inputFileLocations1,
                        this.inputRoots,
                        new ObservableHolder(string),
                        this.openNestedArchivesChk.isSelected(),
                        set1
                );
    }

    public void confirmOpen() throws ZkmException, IOException {
        this.closedByButton = true;
        if (this.expandToClassFiles) {
            this.scanAndOpenClasses();
        } else {
            this.openSelectedLocations();
        }
    }

    public void showDefaultCursor() {
        Cursor cursor1 = new Cursor(0);
        this.setCursor(cursor1);
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if (propertyChangeEvent.getPropertyName().equals("selectionChanged")) {
            File[] files = (File[]) propertyChangeEvent.getNewValue();
            if (files != null && files.length > 0) {
                this.setAddEnabled(true);
                this.setAddPlusEnabled(true);
            } else {
                this.setAddEnabled(false);
                this.setAddPlusEnabled(false);
            }
        } else if (propertyChangeEvent.getPropertyName().equals("finalSelection")) {
            File file1 = (File) propertyChangeEvent.getNewValue();
            if (this.selectedFileSet.add(file1)) {
                this.selectedListModel.addElement(file1);
            }

            this.setRemoveEnabled(true);
            this.setRemoveAllEnabled(true);
        }
    }

    public void afterRemoveAll() {
        this.removeBtn.setEnabled(false);
        this.removeAllBtn.setEnabled(false);
        this.afterRemoveSelected();
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        try {
            if (keyEvent.getKeyCode() == 10) {
                Object object = keyEvent.getSource();
                if (object == this.okBtn) {
                    this.confirmOpen();
                } else if (object == this.cancelBtn) {
                    this.cancelOpen();
                } else if (object == this.helpBtn) {
                    this.showHelp();
                }
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    @Override
    public final void closeFrame() throws ZkmException, IOException {
        super.closeFrame();
        this.ownerFrame.setEnabled(true);
        this.ownerFrame.toFront();
        if (!this.closedByButton) {
            this.callback.onDialogCancelled();
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            Object object = actionEvent.getSource();
            if (object == this.okBtn) {
                this.confirmOpen();
            } else if (object == this.cancelBtn) {
                this.cancelOpen();
            } else if (object == this.helpBtn) {
                this.showHelp();
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public void setRemoveAllEnabled(boolean bl) {
        this.removeAllBtn.setEnabled(bl);
    }

    public void openSelectedLocations() throws ZkmException, IOException {
        String[] strings = this.getSelectedPaths();
        if (strings.length != 0) {
            this.classLocations = new InputFileLocation[strings.length];

            for (int i = 0; i < strings.length; i++) {
                this.classLocations[i] = new InputFileLocation(strings[i]);
            }

            this.closedByButton = true;
            this.closeFrame();
            this.fireOpenCallback((SourceArchive[]) null, (InputFileLocation[]) null, (InputFileLocation[]) null, (Set) null);
        } else {
            new MessageBoxDialog(this, "Error", "No directories or archives were selected");
        }
    }

    public int getSelectedCount() {
        int[] selectedIndices = this.selectedList.getSelectedIndices();
        return selectedIndices == null ? 0 : selectedIndices.length;
    }

    public void showWaitCursor() {
        Cursor cursor1 = new Cursor(3);
        this.setCursor(cursor1);
    }

    public void afterAddSelected() {
        this.removeAllBtn.setEnabled(true);
        SwingUtils.requestFocusOnEdt(this.fileSelector);
    }

    public void removeSelectedEntries() {
        List list1 = this.selectedList.getSelectedValuesList();

        for (int i = 0; i < list1.size(); i++) {
            this.selectedFileSet.remove(list1.get(i));
            this.selectedListModel.removeElement(list1.get(i));
        }

        if (this.selectedListModel.getSize() == 0) {
            this.removeAllBtn.setEnabled(false);
        }
    }

    public String[] getSelectedPaths() {
        int size = this.selectedListModel.getSize();
        String[] strings = new String[size];

        for (int i = 0; i < size; i++) {
            strings[i] = ((File) this.selectedListModel.getElementAt(i)).getAbsolutePath();
        }

        return strings;
    }

    public final void cancelOpen() throws ZkmException, IOException {
        this.closedByButton = true;
        this.closeFrame();
        this.callback.onDialogCancelled();
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }

    public void setAddPlusEnabled(boolean bl) {
        this.addPlusBtn.setEnabled(bl);
    }

    public OpenClassesDialog(
            JFrame jFrame,
            String string,
            String string1,
            boolean expandToClassFiles,
            String string2,
            boolean bl1,
            InputFileLocation[] inputFileLocations,
            DialogCallback dialogCallback1
    ) {
        this.ownerFrame = jFrame;
        this.expandToClassFiles = expandToClassFiles;
        this.callback = dialogCallback1;
        this.initComponents(string, string1, string2, inputFileLocations);
        this.setAddEnabled(false);
        this.setAddPlusEnabled(false);
        this.setRemoveEnabled(false);
        this.setRemoveAllEnabled(inputFileLocations != null && inputFileLocations.length > 0);
        this.openNestedArchivesChk.setSelected(bl1);
        SwingUtils.setVisibleOnEdt(this);
        SwingUtils.requestFocusOnEdt(this.okBtn);
    }

    public void scanAndOpenClasses() throws ZkmException, IOException {
        this.showWaitCursor();
        String[] strings = this.getSelectedPaths();
        HashMap hashMap = ZkmUtils.createHashMap(27);
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList1 = new ArrayList();
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        LinkedHashSet linkedHashSet1 = new LinkedHashSet(13);
        if (strings.length != 0) {
            this.inputRoots = new InputFileLocation[strings.length];

            for (int i = 0; i < strings.length; i++) {
                this.inputRoots[i] = new InputFileLocation(strings[i]);
            }

            String string2 = System.getProperty("file.separator");
            GuiMessageReporter guiMessageReporter = new GuiMessageReporter(this, null);

            for (int i = 0; i < strings.length; i++) {
                File file1 = new File(strings[i]);
                if (file1.exists()) {
                    if (file1.isDirectory()) {
                        String[] strings1 = file1.list(new ClassFileNameFilter());
                        if (strings1 != null) {
                            for (int j = 0; j < strings1.length; j++) {
                                String string = strings[i] + string2 + strings1[j];
                                linkedHashSet.add(new InputFileLocation(string));
                            }
                        }
                    } else if (file1.getName().endsWith(".jad")) {
                        arrayList.add(new InputFileLocation(file1));
                    } else if (ZkmFileUtils.isXmlFileName(file1.getName())) {
                        arrayList1.add(new InputFileLocation(file1));
                    } else {
                        boolean bl = false;
                        File file2 = null;

                        try {
                            file2 = ZkmFileUtils.copyFileToTemp(file1);
                        } catch (IOException iOException) {
                            bl = true;
                            new MessageBoxDialog(
                                    this, "File Error", "Could not create temporary file in '" + TempFileManager.tempDirPath + "' : " + iOException + " (2)"
                            );
                        }

                        if (!bl) {
                            OpenPathEntry openPathEntry = new OpenPathEntry(file1.getAbsolutePath(), ArchivePathFilter.ACCEPT_ALL, null, null);
                            boolean selected = this.openNestedArchivesChk.isSelected();
                            ArrayList arrayList2 = new ArrayList(1);
                            ArrayList arrayList3 = new ArrayList(1);
                            HashSet hashSet = ZkmUtils.createHashSet(7);
                            HashSet hashSet1 = ZkmUtils.createHashSet(7);
                            String string3 = file1.getAbsolutePath();
                            GuiMessageReporter guiMessageReporter1 = guiMessageReporter;
                            Object object6 = null;
                            Object object5 = null;
                            Object object4 = null;
                            String string1 = string3;
                            Object object3 = null;
                            Object object2 = null;
                            Object object1 = null;
                            Object object = null;
                            ArchiveScanner.scanArchive(
                                    file2,
                                    linkedHashSet,
                                    hashMap,
                                    openPathEntry,
                                    selected,
                                    arrayList2,
                                    arrayList3,
                                    hashSet,
                                    hashSet1,
                                    linkedHashSet1,
                                    (Set) null,
                                    (Set) object,
                                    (Set) object1,
                                    (Set) object2,
                                    (Set) object3,
                                    string1,
                                    (SourceArchive) object4,
                                    (ZipOutputTarget) object5,
                                    (Set) object6,
                                    guiMessageReporter1
                            );
                        }
                    }
                }
            }

            if (linkedHashSet.size() == 0) {
                new MessageBoxDialog(this, "Error", "No class files were found in the selected directories or archives");
            } else {
                SourceArchive[] sourceArchives1 = new SourceArchive[hashMap.size()];
                Iterator iterator = hashMap.values().iterator();

                for (int i = 0; i < sourceArchives1.length; i++) {
                    sourceArchives1[i] = (SourceArchive) iterator.next();
                }

                this.classLocations = new InputFileLocation[linkedHashSet.size()];
                this.classLocations = ((com.zelix.klassmaster.archive.InputFileLocation[]) (linkedHashSet.toArray(this.classLocations)));
                InputFileLocation[] inputFileLocations = new InputFileLocation[arrayList.size()];
                arrayList.toArray(inputFileLocations);
                InputFileLocation[] inputFileLocations1 = new InputFileLocation[arrayList1.size()];
                arrayList1.toArray(inputFileLocations1);
                this.closeFrame();
                this.fireOpenCallback(sourceArchives1, inputFileLocations, inputFileLocations1, linkedHashSet1);
            }
        } else {
            new MessageBoxDialog(this, "Error", "No directories or archives were selected");
        }

        this.showDefaultCursor();
    }

    public void setRemoveEnabled(boolean bl) {
        this.removeBtn.setEnabled(bl);
    }
}
