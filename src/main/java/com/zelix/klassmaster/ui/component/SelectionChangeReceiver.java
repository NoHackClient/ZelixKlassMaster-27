package com.zelix.klassmaster.ui.component;

public interface SelectionChangeReceiver {
    void handleSelectionDelta(int[] ba, int[] bb);

    void handleAccessSelection(int ba);
}
