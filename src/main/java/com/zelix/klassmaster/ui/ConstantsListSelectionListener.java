package com.zelix.klassmaster.ui;

import com.zelix.klassmaster.classfile.constpool.NumericConstantEntry;
import com.zelix.klassmaster.ui.component.ClassConstantsNode;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ConstantsListSelectionListener implements ListSelectionListener {
    public ZkmMainWindow mainWindow;
    public ClassConstantsNode constantsNode;

    public ConstantsListSelectionListener(ClassConstantsNode classConstantsNode, ZkmMainWindow zkmMainWindow) {
        this.mainWindow = zkmMainWindow;
        this.constantsNode = classConstantsNode;
    }

    @Override
    public void valueChanged(ListSelectionEvent listSelectionEvent) {
        if (!listSelectionEvent.getValueIsAdjusting()) {
            int selectedIndex = ((JList) listSelectionEvent.getSource()).getSelectedIndex();
            if (selectedIndex > -1) {
                NumericConstantEntry numericConstantEntry = this.constantsNode.getConstant(selectedIndex);
                this.mainWindow.showConstantEditor(numericConstantEntry, this.constantsNode.getDuplicateStrings());
            }
        }
    }
}
