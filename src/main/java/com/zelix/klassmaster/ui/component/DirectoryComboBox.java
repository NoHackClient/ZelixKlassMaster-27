package com.zelix.klassmaster.ui.component;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

public class DirectoryComboBox extends JComboBox implements ItemListener, PropertyChangeListener, FileChooserComponent {
    public File currentDirectory;
    public DefaultComboBoxModel comboModel;
    public File[] fileSystemRoots;
    public boolean updatingSelection;
    public ArrayList ancestorDirectories;

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        if (itemEvent.getStateChange() == 1 && !this.updatingSelection) {
            File file1 = (File) this.getSelectedItem();
            if (file1.exists()) {
                this.currentDirectory = file1;
                this.firePropertyChange("directoryChanged", null, file1);
            } else {
                this.updatingSelection = true;
                this.setSelectedItem(this.currentDirectory);
                this.updatingSelection = false;
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if (propertyChangeEvent.getPropertyName().equals("directoryChanged")) {
            this.updatingSelection = true;
            this.setCurrentDirectory((File) propertyChangeEvent.getNewValue());
            this.updatingSelection = false;
        }
    }

    public DirectoryComboBox(File file1) {
        this.initRoots();
        this.setCurrentDirectory(file1);
        this.addItemListener(this);
    }

    public void setCurrentDirectory(File file1) {
        this.currentDirectory = file1;
        this.ancestorDirectories = new ArrayList();

        for (File file2 = this.currentDirectory.getParentFile(); file2 != null; file2 = file2.getParentFile()) {
            this.ancestorDirectories.add(file2);
        }

        Collections.reverse(this.ancestorDirectories);
        File file3 = null;
        if (this.ancestorDirectories.size() > 0) {
            file3 = (File) this.ancestorDirectories.get(0);
        }

        DefaultComboBoxModel defaultComboBoxModel = new DefaultComboBoxModel();
        int ba = 0;
        int bc = 0;

        for (File[] files = this.fileSystemRoots; bc < files.length; files = this.fileSystemRoots) {
            defaultComboBoxModel.addElement(this.fileSystemRoots[ba]);
            if (file3 != null && file3.equals(this.fileSystemRoots[ba])) {
                int bb = 1;
                bc = bb;

                for (ArrayList arrayList = this.ancestorDirectories; bc < arrayList.size(); arrayList = this.ancestorDirectories) {
                    defaultComboBoxModel.addElement(this.ancestorDirectories.get(bb));
                    bc = ++bb;
                }

                defaultComboBoxModel.addElement(this.currentDirectory);
            }

            bc = ++ba;
        }

        this.comboModel = defaultComboBoxModel;
        this.setModel(this.comboModel);
        this.setSelectedItem(this.currentDirectory);
    }

    public void initRoots() {
        this.fileSystemRoots = File.listRoots();
        this.comboModel = new DefaultComboBoxModel();
        this.setModel(this.comboModel);
        int ba = 0;
        int bb = 0;

        for (File[] files = this.fileSystemRoots; bb < files.length; files = this.fileSystemRoots) {
            this.comboModel.addElement(this.fileSystemRoots[ba]);
            bb = ++ba;
        }

        this.setRenderer(new DirectoryComboBoxRenderer(this));
        this.setToolTipText("Current folder being listed");
    }
}
