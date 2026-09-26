package com.zelix.klassmaster.ui;

import com.zelix.klassmaster.classfile.MemberInfo;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.exceptions.ClassFileLoadException;
import com.zelix.klassmaster.exceptions.DuplicateFieldException;
import com.zelix.klassmaster.exceptions.InvalidEditException;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.component.PlatformAwareJList;
import com.zelix.klassmaster.ui.component.SwingUtils;
import com.zelix.klassmaster.ui.dialog.MessageBoxDialog;
import com.zelix.klassmaster.util.ListenerRegistry;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;

public abstract class MemberPropertiesPanel extends NameModifiersEditPanel {
    public MemberInfo memberInfo;

    @Override
    public final boolean isModifierSelectionChanged() {
        return super.accessComboBox.getSelectedIndex() != super.originalAccessIndex
                || super.modifierList.isSelectedIndex(super.abstractIndex) != super.originalModifierIndices.contains(super.abstractKey)
                || super.modifierList.isSelectedIndex(super.staticIndex) != super.originalModifierIndices.contains(super.staticKey)
                || super.modifierList.isSelectedIndex(super.finalIndex) != super.originalModifierIndices.contains(super.finalKey)
                || super.modifierList.isSelectedIndex(super.synchronizedIndex) != super.originalModifierIndices.contains(super.synchronizedKey)
                || super.modifierList.isSelectedIndex(super.nativeIndex) != super.originalModifierIndices.contains(super.nativeKey)
                || super.modifierList.isSelectedIndex(super.volatileIndex) != super.originalModifierIndices.contains(super.volatileKey)
                || super.modifierList.isSelectedIndex(super.transientIndex) != super.originalModifierIndices.contains(super.transientKey);
    }

    @Override
    public void handleChangeOnEdt(Object object1, Object object, Object object2, Object object3) throws ZkmException, IOException {
        label108:
        {
            MemberPropertiesPanel memberPropertiesPanel1;
            MemberInfo memberInfo1;
            if (object != null) {
                if (object == null || !(object instanceof MutableInt) || ((MutableInt) object).getValue() != 0) {
                    break label108;
                }

                memberPropertiesPanel1 = this;
                memberInfo1 = this.memberInfo;
            } else {
                memberPropertiesPanel1 = this;
                memberInfo1 = this.memberInfo;
            }

            memberPropertiesPanel1.originalName = memberInfo1.getSourceName();
            super.existingNameLbl.setText(super.originalName);
            super.propertyEditFld.setText(super.originalName);
        }

        if (object == null || object != null && object instanceof MutableInt && ((MutableInt) object).getValue() == 1) {
            super.originalModifierIndices = ZkmUtils.createNamedSet();
            if (this.memberInfo.isPublic()) {
                super.accessComboBox.setSelectedIndex(super.publicIndex);
                super.originalAccessIndex = super.publicIndex;
            } else if (this.memberInfo.isProtected()) {
                super.accessComboBox.setSelectedIndex(super.protectedIndex);
                super.originalAccessIndex = super.protectedIndex;
            } else if (this.memberInfo.isStrictlyPrivate()) {
                super.accessComboBox.setSelectedIndex(super.privateIndex);
                super.originalAccessIndex = super.privateIndex;
            } else {
                super.accessComboBox.setSelectedIndex(super.packageIndex);
                super.originalAccessIndex = super.packageIndex;
            }

            if (this.memberInfo.isAbstract()) {
                if (!super.modifierList.isSelectedIndex(super.abstractIndex)) {
                    super.modifierList.addSelectionInterval(super.abstractIndex, super.abstractIndex);
                }

                super.originalModifierIndices.add(super.abstractKey);
            } else if (super.modifierList.isSelectedIndex(super.abstractIndex)) {
                super.modifierList.removeSelectionInterval(super.abstractIndex, super.abstractIndex);
            }

            if (this.memberInfo.isStatic()) {
                if (!super.modifierList.isSelectedIndex(super.staticIndex)) {
                    super.modifierList.addSelectionInterval(super.staticIndex, super.staticIndex);
                }

                super.originalModifierIndices.add(super.staticKey);
            } else {
                super.modifierList.removeSelectionInterval(super.staticIndex, super.staticIndex);
            }

            if (this.memberInfo.isFinal()) {
                if (!super.modifierList.isSelectedIndex(super.finalIndex)) {
                    super.modifierList.addSelectionInterval(super.finalIndex, super.finalIndex);
                }

                super.originalModifierIndices.add(super.finalKey);
            } else {
                super.modifierList.removeSelectionInterval(super.finalIndex, super.finalIndex);
            }

            if (this.memberInfo.isSynchronized()) {
                if (!super.modifierList.isSelectedIndex(super.synchronizedIndex)) {
                    super.modifierList.addSelectionInterval(super.synchronizedIndex, super.synchronizedIndex);
                }

                super.originalModifierIndices.add(super.synchronizedKey);
            } else {
                super.modifierList.removeSelectionInterval(super.synchronizedIndex, super.synchronizedIndex);
            }

            if (this.memberInfo.isNative()) {
                if (!super.modifierList.isSelectedIndex(super.nativeIndex)) {
                    super.modifierList.addSelectionInterval(super.nativeIndex, super.nativeIndex);
                }

                super.originalModifierIndices.add(super.nativeKey);
            } else {
                super.modifierList.removeSelectionInterval(super.nativeIndex, super.nativeIndex);
            }

            if (this.memberInfo.isVolatile()) {
                if (!super.modifierList.isSelectedIndex(super.volatileIndex)) {
                    super.modifierList.addSelectionInterval(super.volatileIndex, super.volatileIndex);
                }

                super.originalModifierIndices.add(super.volatileKey);
            } else {
                super.modifierList.removeSelectionInterval(super.volatileIndex, super.volatileIndex);
            }

            if (this.memberInfo.isTransient()) {
                if (!super.modifierList.isSelectedIndex(super.transientIndex)) {
                    super.modifierList.addSelectionInterval(super.transientIndex, super.transientIndex);
                }

                super.originalModifierIndices.add(super.transientKey);
            } else {
                super.modifierList.removeSelectionInterval(super.transientIndex, super.transientIndex);
            }
        }

        this.disableEditControls();
        super.modifierSelectionListener.setPreviousSelection(super.modifierList.getSelectedIndices());
        this.disableButtonsIfUnchanged();
        SwingUtils.requestFocusOnEdt(super.propertyEditFld);
    }

    @Override
    public void initModifierIndices() {
        super.publicIndex = 0;
        super.protectedIndex = 1;
        super.privateIndex = 2;
        super.packageIndex = 3;
    }

    public abstract int getRetainedFlagsMask();

    @Override
    public void populateAccessChoices() {
        super.accessComboModel.addElement("public");
        super.accessComboModel.addElement("protected");
        super.accessComboModel.addElement("private");
        super.accessComboModel.addElement("package");
    }

    public abstract void renameMember(Object object) throws ZkmException, IOException;

    public MemberPropertiesPanel(MemberInfo memberInfo1, ClassRepository classRepository1, ZkmMainWindow zkmMainWindow, ListenerRegistry listenerRegistry1) {
        super(classRepository1, zkmMainWindow);
        this.memberInfo = memberInfo1;
        listenerRegistry1.addObserver(memberInfo1, this, "DescListObservers");
    }

    @Override
    public final void disableEditControls() {
        super.accessComboBox.setEnabled(false);
        super.modifierList.setEnabled(false);
        super.propertyEditFld.setEnabled(false);
    }

    @Override
    public void applyChanges() throws ZkmException, IOException {
        String string = null;
        int ba = -1;
        String string1 = super.propertyEditFld.getText().trim();
        if (!string1.equals(super.originalName)) {
            if (string1.length() > 0) {
                string = string1;
            } else {
                new MessageBoxDialog(super.mainWindow, "Error", "Name cannot be blank");
            }
        }

        if (this.isModifierSelectionChanged()) {
            ba = this.memberInfo.getAccessFlags();
            ba &= this.getRetainedFlagsMask();
            int selectedIndex = super.accessComboBox.getSelectedIndex();
            if (selectedIndex == super.publicIndex) {
                ba |= 1;
            } else if (selectedIndex == super.protectedIndex) {
                ba |= 4;
            } else if (selectedIndex == super.privateIndex) {
                ba |= 2;
            }

            PlatformAwareJList platformAwareJList;
            if (super.modifierList.isSelectedIndex(super.abstractIndex)) {
                ba |= 1024;
                platformAwareJList = super.modifierList;
            } else {
                platformAwareJList = super.modifierList;
            }

            if (platformAwareJList.isSelectedIndex(super.staticIndex)) {
                ba |= 8;
                platformAwareJList = super.modifierList;
            } else {
                platformAwareJList = super.modifierList;
            }

            if (platformAwareJList.isSelectedIndex(super.finalIndex)) {
                ba |= 16;
            }

            if (super.modifierList.isSelectedIndex(super.synchronizedIndex)) {
                ba |= 32;
                platformAwareJList = super.modifierList;
            } else {
                platformAwareJList = super.modifierList;
            }

            if (platformAwareJList.isSelectedIndex(super.nativeIndex)) {
                ba |= 256;
            }

            if (super.modifierList.isSelectedIndex(super.volatileIndex)) {
                ba |= 64;
            }

            if (super.modifierList.isSelectedIndex(super.transientIndex)) {
                ba |= 128;
            }
        }

        if (string != null) {
            try {
                this.renameMember(string1);
            } catch (DuplicateFieldException duplicateFieldException) {
                new MessageBoxDialog(super.mainWindow, "Error", duplicateFieldException.getMessage());
            } catch (InvalidEditException invalidEditException1) {
                new MessageBoxDialog(super.mainWindow, "Error", invalidEditException1.getMessage());
            } catch (ClassFileLoadException classFileLoadException) {
                new MessageBoxDialog(super.mainWindow, "Error", classFileLoadException.getMessage());
            }
        }

        if (ba != -1) {
            try {
                this.applyAccessFlags(ba);
            } catch (InvalidEditException invalidEditException) {
                new MessageBoxDialog(super.mainWindow, "Error", invalidEditException.getMessage());
            }
        }
    }

    public abstract void applyAccessFlags(int ba) throws ZkmException, IOException;
}
