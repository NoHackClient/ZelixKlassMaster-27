package com.zelix.klassmaster.ui;

import com.zelix.klassmaster.ui.component.PlatformAwareJList;
import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ClassPropertiesSelectionListener implements ActionListener, ListSelectionListener {
    public ZkmMainWindow mainWindow;

    public ClassPropertiesSelectionListener(ZkmMainWindow zkmMainWindow) {
        this.mainWindow = zkmMainWindow;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
    }

    @Override
    public void valueChanged(ListSelectionEvent listSelectionEvent) {
        try {
            if (!listSelectionEvent.getValueIsAdjusting()) {
                PlatformAwareJList platformAwareJList = (PlatformAwareJList) listSelectionEvent.getSource();
                if (platformAwareJList.getSelectedIndex() > -1) {
                    this.mainWindow.showClassProperty(platformAwareJList.getSelectedIndex(), listSelectionEvent);
                }
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }
}
