package com.zelix.klassmaster.ui;

import com.zelix.klassmaster.ui.component.SelectionChangeReceiver;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JComboBox;

public class AccessComboItemListener implements ItemListener {
    public SelectionChangeReceiver receiver;

    public AccessComboItemListener(SelectionChangeReceiver selectionChangeReceiver) {
        this.receiver = selectionChangeReceiver;
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        if (itemEvent.getStateChange() == 1) {
            JComboBox jComboBox = (JComboBox) itemEvent.getSource();
            this.receiver.handleAccessSelection(jComboBox.getSelectedIndex());
        }
    }
}
