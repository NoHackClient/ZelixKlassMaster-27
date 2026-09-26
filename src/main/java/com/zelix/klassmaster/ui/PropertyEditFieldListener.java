package com.zelix.klassmaster.ui;

import com.zelix.klassmaster.ui.component.AbstractDocumentListener;

public class PropertyEditFieldListener extends AbstractDocumentListener {
    public final ConstantPoolEntryEditPanel editPanel;

    @Override
    public void onDocumentChanged(Object object) {
        this.editPanel.onEditTextChanged(this.editPanel.propertyEditFld.getText());
    }

    public PropertyEditFieldListener(ConstantPoolEntryEditPanel constantPoolEntryEditPanel) {
        this.editPanel = constantPoolEntryEditPanel;
    }
}
