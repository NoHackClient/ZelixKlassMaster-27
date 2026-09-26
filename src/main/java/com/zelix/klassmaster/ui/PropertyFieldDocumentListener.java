package com.zelix.klassmaster.ui;

import com.zelix.klassmaster.ui.component.AbstractDocumentListener;

import javax.swing.event.DocumentEvent;

public class PropertyFieldDocumentListener extends AbstractDocumentListener {
    public PropertyEditListener editListener;
    public PropertyEditFld editField;

    public PropertyFieldDocumentListener(PropertyEditListener propertyEditListener, PropertyEditFld propertyEditFld1) {
        this.editListener = propertyEditListener;
        this.editField = propertyEditFld1;
    }

    @Override
    public void onDocumentChanged(Object object) {
        ((DocumentEvent) object).getDocument();
        if (!this.editField.isSettingText()) {
            this.editListener.onEditTextChanged(this.editField.getText().trim());
        }
    }
}
