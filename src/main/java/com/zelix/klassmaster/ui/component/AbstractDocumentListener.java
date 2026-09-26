package com.zelix.klassmaster.ui.component;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public abstract class AbstractDocumentListener implements DocumentListener {
    @Override
    public void insertUpdate(DocumentEvent documentEvent) {
        DocumentEvent documentEvent1 = documentEvent;
        this.onDocumentChanged(documentEvent1);
    }

    @Override
    public void removeUpdate(DocumentEvent documentEvent) {
        DocumentEvent documentEvent1 = documentEvent;
        this.onDocumentChanged(documentEvent1);
    }

    public abstract void onDocumentChanged(Object object);

    @Override
    public void changedUpdate(DocumentEvent documentEvent) {
        DocumentEvent documentEvent1 = documentEvent;
        this.onDocumentChanged(documentEvent1);
    }
}
