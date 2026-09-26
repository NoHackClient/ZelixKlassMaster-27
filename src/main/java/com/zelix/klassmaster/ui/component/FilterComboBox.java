package com.zelix.klassmaster.ui.component;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

public class FilterComboBox extends JComboBox implements ItemListener, FileChooserComponent {
    public DefaultComboBoxModel comboModel;

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        if (itemEvent.getStateChange() == 1) {
            ZkmFileFilter zkmFileFilter = (ZkmFileFilter) this.getSelectedItem();
            this.firePropertyChange("fileFilterChanged", null, zkmFileFilter);
        }
    }

    public void initFilters(ZkmFileFilter[] zkmFileFilters) {
        this.comboModel = new DefaultComboBoxModel();
        FilterComboBox filterComboBox2;
        DefaultComboBoxModel defaultComboBoxModel;
        if (zkmFileFilters != null && zkmFileFilters.length != 0) {
            for (int i = 0; i < zkmFileFilters.length; i++) {
                this.comboModel.addElement(zkmFileFilters[i]);
            }

            filterComboBox2 = this;
            defaultComboBoxModel = this.comboModel;
        } else {
            this.comboModel.addElement(new AllFilesFilter());
            filterComboBox2 = this;
            defaultComboBoxModel = this.comboModel;
        }

        filterComboBox2.setModel(defaultComboBoxModel);
        this.setSelectedIndex(0);
        this.addItemListener(this);
        this.setToolTipText("File types to display");
    }

    public FilterComboBox(ZkmFileFilter[] zkmFileFilters) {
        ZkmFileFilter[] zkmFileFilters1 = zkmFileFilters;
        this.initFilters(zkmFileFilters1);
    }
}
