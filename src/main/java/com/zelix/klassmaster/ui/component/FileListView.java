package com.zelix.klassmaster.ui.component;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.DefaultListModel;

public class FileListView extends FilePathList implements MouseListener, KeyListener, PropertyChangeListener, FileChooserComponent {
    public File currentDirectory;
    public File[] directoryFiles;
    public boolean adjustingSelection;
    public DefaultListModel listModel;
    public FileFilter fileFilter;
    public boolean multiSelection;
    public int fileSelectionMode;

    public void goToParentDirectory() {
        if (this.currentDirectory != null) {
            File file1 = this.currentDirectory.getParentFile();
            if (file1 != null) {
                this.setCurrentDirectory(file1);
                this.firePropertyChange("directoryChanged", null, file1);
            }
        }
    }

    public void selectFile(File file1, Boolean boolean1) {
        if (file1 != null && this.listModel.contains(file1)) {
            this.setSelectedValue(file1, boolean1);
        }
    }

    public static void onSelectionChanged(FileListView fileListView1) {
        fileListView1.filterSelection();
    }

    public void reloadFiles() {
        if (this.fileFilter != null) {
            this.directoryFiles = this.currentDirectory.listFiles(this.fileFilter);
        } else {
            this.directoryFiles = this.currentDirectory.listFiles();
        }

        if (this.directoryFiles == null) {
            this.directoryFiles = new File[0];
        }

        DirectoryFirstFileComparator directoryFirstFileComparator = DirectoryFirstFileComparator.getInstance();
        Arrays.sort(this.directoryFiles, directoryFirstFileComparator);
        DefaultListModel defaultListModel = new DefaultListModel();
        int ba = 0;
        int bb = 0;

        for (File[] files = this.directoryFiles; bb < files.length; files = this.directoryFiles) {
            defaultListModel.addElement(this.directoryFiles[ba]);
            bb = ++ba;
        }

        this.listModel = defaultListModel;
        this.setModel(defaultListModel);
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            int ba = this.locationToIndex(mouseEvent.getPoint());
            if (ba > -1) {
                File file1 = (File) this.listModel.get(ba);
                if (file1.isDirectory()) {
                    this.setCurrentDirectory(file1);
                    this.firePropertyChange("directoryChanged", null, file1);
                } else if ((this.fileSelectionMode == 1 || this.fileSelectionMode == 3) && this.getSelectedValuesList().size() == 1) {
                    this.firePropertyChange("finalSelection", null, file1);
                }
            }
        }
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
    }

    public void filterSelection() {
        int[] selectedIndices = this.getSelectedIndices();
        boolean bl = false;
        this.adjustingSelection = true;

        for (int i = 0; i < selectedIndices.length; i++) {
            int bc = selectedIndices[i];
            File file1 = (File) this.listModel.get(bc);
            switch (this.fileSelectionMode) {
                case 1:
                    if (file1.isDirectory()) {
                        this.removeSelectionInterval(bc, bc);
                    } else {
                        bl = true;
                    }
                    break;
                case 2:
                    if (!file1.isDirectory()) {
                        this.removeSelectionInterval(bc, bc);
                    } else {
                        bl = true;
                    }
                    break;
                case 3:
                    bl = true;
            }
        }

        this.adjustingSelection = false;
        if (bl) {
            this.firePropertyChange("selectionChanged", null, this.getSelectedFiles());
        }
    }

    public File getCurrentDirectory() {
        return this.currentDirectory;
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == 10) {
            List list1 = this.getSelectedValuesList();
            if (list1.size() == 1) {
                File file1 = (File) list1.get(0);
                if (!file1.isDirectory()) {
                    if (this.fileSelectionMode == 1 || this.fileSelectionMode == 3) {
                        this.firePropertyChange("finalSelection", null, file1);
                    }
                } else {
                    this.setCurrentDirectory(file1);
                    this.firePropertyChange("directoryChanged", null, file1);
                }
            }
        }
    }

    public FileListView(File file1, boolean multiSelection, int fileSelectionMode, FileFilter fileFilter1) {
        this.setShowFullPaths();
        this.listModel = new DefaultListModel();
        this.setModel(this.listModel);
        this.fileFilter = fileFilter1;
        this.multiSelection = multiSelection;
        if (multiSelection) {
            this.setSelectionMode(2);
        } else {
            this.setSelectionMode(0);
        }

        this.fileSelectionMode = fileSelectionMode;
        this.setCurrentDirectory(file1);
        FileListSelectionListener fileListSelectionListener = new FileListSelectionListener(this);
        this.addListSelectionListener(fileListSelectionListener);
        this.addMouseListener(this);
        this.addKeyListener(this);
    }

    public void setCurrentDirectory(File file1) {
        if (this.currentDirectory == null || !this.currentDirectory.equals(file1)) {
            this.currentDirectory = file1;
            this.reloadFiles();
            if (this.listModel.getSize() > 0) {
                this.ensureIndexIsVisible(0);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
    }

    public void setFileFilter(FileFilter fileFilter1) {
        if (this.fileFilter != fileFilter1) {
            this.fileFilter = fileFilter1;
            List list1 = this.getSelectedValuesList();
            this.reloadFiles();
            ArrayList arrayList = new ArrayList(list1.size());

            for (int i = 0; i < list1.size(); i++) {
                int bb = this.listModel.indexOf(list1.get(i));
                if (bb > -1) {
                    arrayList.add(bb);
                }
            }

            int[] bc = new int[arrayList.size()];

            for (int i = 0; i < arrayList.size(); i++) {
                bc[i] = (Integer) arrayList.get(i);
            }

            this.setSelectedIndices(bc);
            if (bc.length > 0) {
                this.ensureIndexIsVisible(bc[0]);
            }
        }
    }

    public boolean createFolder(String string) {
        File file1 = new File(this.currentDirectory, string);
        boolean bl = file1.mkdir();
        if (bl) {
            this.reloadFiles();
            this.setSelectedValue(file1, true);
            SwingUtils.requestFocusOnEdt(this);
        }

        return bl;
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if (propertyChangeEvent.getPropertyName().equals("directoryChanged")) {
            File file1 = (File) propertyChangeEvent.getNewValue();
            this.setCurrentDirectory(file1);
        } else if (propertyChangeEvent.getPropertyName().equals("fileFilterChanged")) {
            this.setFileFilter((FileFilter) propertyChangeEvent.getNewValue());
        } else if (propertyChangeEvent.getPropertyName().equals("finalSelection")) {
            this.firePropertyChange("finalSelection", null, propertyChangeEvent.getNewValue());
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
    }

    public File[] getSelectedFiles() {
        List list1 = this.getSelectedValuesList();
        File[] files = new File[list1.size()];
        return ((java.io.File[]) (list1.toArray(files)));
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
    }
}
