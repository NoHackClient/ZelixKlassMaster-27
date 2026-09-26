package com.zelix.klassmaster.ui;

import com.zelix.klassmaster.ui.component.PlatformAwareJList;
import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class BrowserClassListListener implements ActionListener, ListSelectionListener {
    public ZkmMainWindow mainWindow;

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
    }

    public BrowserClassListListener(ZkmMainWindow zkmMainWindow) {
        this.mainWindow = zkmMainWindow;
    }

    @Override
    public void valueChanged(ListSelectionEvent listSelectionEvent) {
        try {
            PlatformAwareJList platformAwareJList = (PlatformAwareJList) listSelectionEvent.getSource();
            if (!listSelectionEvent.getValueIsAdjusting()) {
                int selectedIndex = platformAwareJList.getSelectedIndex();
                if (selectedIndex > -1) {
                    this.mainWindow.selectClass(selectedIndex);
                }
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }
}
