package com.zelix.klassmaster.ui;

import com.zelix.klassmaster.ui.component.PlatformAwareJList;
import com.zelix.klassmaster.ui.component.SelectionChangeReceiver;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ZkmUtils;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class SelectionDeltaListener implements ListSelectionListener {
    public int[] previousSelection = new int[0];
    public SelectionChangeReceiver selectionReceiver;

    public SelectionDeltaListener(SelectionChangeReceiver selectionChangeReceiver) {
        this.selectionReceiver = selectionChangeReceiver;
    }

    @Override
    public void valueChanged(ListSelectionEvent listSelectionEvent) {
        try {
            int[] selectedIndices = ((PlatformAwareJList) listSelectionEvent.getSource()).getSelectedIndices();
            ObservableHolder observableHolder = new ObservableHolder();
            ObservableHolder observableHolder1 = new ObservableHolder();
            ZkmUtils.diffSortedIntArrays(this.previousSelection, selectedIndices, observableHolder, observableHolder1);
            this.selectionReceiver.handleSelectionDelta((int[]) observableHolder.getValue(), (int[]) observableHolder1.getValue());
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public void setPreviousSelection(int[] previousSelection) {
        this.previousSelection = previousSelection;
    }
}
