package com.zelix.klassmaster.ui;

import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.exceptions.InvalidEditException;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.log.MessageReporter;
import com.zelix.klassmaster.ui.component.PlatformAwareJList;
import com.zelix.klassmaster.ui.component.SwingUtils;
import com.zelix.klassmaster.ui.dialog.MessageBoxDialog;
import com.zelix.klassmaster.util.ListenerRegistry;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.HashMap;
import javax.swing.DefaultListModel;

public class ClassModifyPanel extends NameModifiersEditPanel {
    public String packagePrefix;
    public ProgramClass programClass;

    @Override
    public void populateAccessChoices() {
        super.accessComboModel.addElement("public");
        super.accessComboModel.addElement("package");
    }

    @Override
    public void initModifierIndices() {
        super.publicIndex = 0;
        super.packageIndex = 1;
        super.abstractIndex = 0;
        super.finalIndex = 1;
        super.interfaceIndex = 2;
        super.superIndex = 3;
    }

    @Override
    public void handleChangeOnEdt(Object object2, Object object, Object object1, Object object3) throws ZkmException, IOException {
        if (object == null || object1 != null && object1 instanceof ProgramClass && object instanceof MutableInt && ((MutableInt) object).getValue() == 0) {
            super.originalName = this.programClass.getClassSimpleName();
            this.packagePrefix = this.programClass.getPackageName();
            if (!this.packagePrefix.equals("")) {
                this.packagePrefix = this.packagePrefix + ".";
            }

            super.existingNameLbl.setText(this.packagePrefix + super.originalName);
            super.propertyEditFld.setText(super.originalName);
        }

        if (object == null || object instanceof MutableInt && ((MutableInt) object).getValue() == 1) {
            if (this.programClass.isPublic()) {
                super.accessComboBox.setSelectedIndex(super.publicIndex);
                super.originalAccessIndex = super.publicIndex;
            } else {
                super.accessComboBox.setSelectedIndex(super.packageIndex);
                super.originalAccessIndex = super.packageIndex;
            }

            super.originalModifierIndices = ZkmUtils.createNamedSet();
            if (this.programClass.isAbstract()) {
                if (!super.modifierList.isSelectedIndex(super.abstractIndex)) {
                    super.modifierList.addSelectionInterval(super.abstractIndex, super.abstractIndex);
                }

                super.originalModifierIndices.add(super.abstractKey);
            } else {
                super.modifierList.removeSelectionInterval(super.abstractIndex, super.abstractIndex);
            }

            if (this.programClass.isFinal()) {
                if (!super.modifierList.isSelectedIndex(super.finalIndex)) {
                    super.modifierList.addSelectionInterval(super.finalIndex, super.finalIndex);
                }

                super.originalModifierIndices.add(super.finalKey);
            } else {
                super.modifierList.removeSelectionInterval(super.finalIndex, super.finalIndex);
            }

            if (this.programClass.isInterface()) {
                if (!super.modifierList.isSelectedIndex(super.interfaceIndex)) {
                    super.modifierList.addSelectionInterval(super.interfaceIndex, super.interfaceIndex);
                }

                super.originalModifierIndices.add(super.interfaceKey);
            } else {
                super.modifierList.removeSelectionInterval(super.interfaceIndex, super.interfaceIndex);
            }

            if (this.programClass.hasSuperFlag()) {
                if (!super.modifierList.isSelectedIndex(super.superIndex)) {
                    super.modifierList.addSelectionInterval(super.superIndex, super.superIndex);
                }

                super.originalModifierIndices.add(super.superKey);
            } else {
                super.modifierList.removeSelectionInterval(super.superIndex, super.superIndex);
            }
        }

        this.disableEditControls();
        super.modifierSelectionListener.setPreviousSelection(super.modifierList.getSelectedIndices());
        this.disableButtonsIfUnchanged();
        SwingUtils.requestFocusOnEdt(super.propertyEditFld);
    }

    @Override
    public boolean isModifierSelectionChanged() {
        return super.accessComboBox.getSelectedIndex() != super.originalAccessIndex
                || super.modifierList.isSelectedIndex(super.abstractIndex) != super.originalModifierIndices.contains(super.abstractKey)
                || super.modifierList.isSelectedIndex(super.finalIndex) != super.originalModifierIndices.contains(super.finalKey)
                || super.modifierList.isSelectedIndex(super.interfaceIndex) != super.originalModifierIndices.contains(super.interfaceKey)
                || super.modifierList.isSelectedIndex(super.superIndex) != super.originalModifierIndices.contains(super.superKey);
    }

    @Override
    public final void disableEditControls() {
        super.accessComboBox.setEnabled(false);
        super.modifierList.setEnabled(false);
        super.propertyEditFld.setEnabled(false);
    }

    public ClassModifyPanel(ProgramClass programClass1, ClassRepository classRepository1, ZkmMainWindow zkmMainWindow, ListenerRegistry listenerRegistry1) throws ZkmException, IOException {
        super(classRepository1, zkmMainWindow);
        this.programClass = programClass1;
        listenerRegistry1.addDefaultObserver(this.programClass, this);
        ClassModifyPanel classModifyPanel1 = this;
        this.programClass.notifyObserverWithoutArgs(classModifyPanel1);
    }

    @Override
    public void applyChanges() throws ZkmException, IOException {
        String string = null;
        int ba = -1;
        String string1 = null;
        String string2 = null;
        String string3 = super.propertyEditFld.getText().trim();
        if (!string3.equals(super.originalName)) {
            if (this.programClass.hasVersionedVariants()) {
                new MessageBoxDialog(
                        super.mainWindow,
                        "Error",
                        "Cannot rename a class with multi-release versions : "
                                + this.programClass.getLocationName()
                                + "' : '"
                                + ((ClassFileBase) this.programClass.getVersionedVariants().get(0)).getLocationName()
                                + "'"
                );
            } else if (string3.length() > 0) {
                string1 = this.packagePrefix + string3;
                string2 = string1.replace('.', '/');
                ClassHierarchyNode classHierarchyNode = ClassHierarchyNode.findNode(this.programClass.getClassName());
                if (!classHierarchyNode.hasEnclosingNode()) {
                    if (!ClassHierarchyNode.isProgramClassName(string2)) {
                        string = string2;
                    } else {
                        new MessageBoxDialog(super.mainWindow, "Error", "Class " + string1 + " already exists.");
                    }
                } else {
                    new MessageBoxDialog(super.mainWindow, "Error", "Cannot rename inner classes in this version : " + this.programClass.getLocationName() + "'");
                }
            } else {
                new MessageBoxDialog(super.mainWindow, "Error", "Class name cannot be blank");
            }
        }

        if (this.isModifierSelectionChanged()) {
            if (this.programClass.hasVersionedVariants()) {
                new MessageBoxDialog(
                        super.mainWindow,
                        "Error",
                        "Cannot modify a class with multi-release versions : "
                                + this.programClass.getLocationName()
                                + "' : '"
                                + ((ClassFileBase) this.programClass.getVersionedVariants().get(0)).getLocationName()
                                + "'"
                );
            } else {
                ba = this.programClass.getAccessFlags();
                ba &= -1586;
                if (super.accessComboBox.getSelectedIndex() == super.publicIndex) {
                    ba |= 1;
                }

                if (super.modifierList.isSelectedIndex(super.abstractIndex)) {
                    ba |= 1024;
                }

                if (super.modifierList.isSelectedIndex(super.finalIndex)) {
                    ba |= 16;
                }

                if (super.modifierList.isSelectedIndex(super.superIndex)) {
                    ba |= 32;
                }

                if (super.modifierList.isSelectedIndex(super.interfaceIndex)) {
                    ba |= 512;
                }
            }
        }

        if (string != null) {
            synchronized (super.classRepository) {
                super.existingNameLbl.setText(string1);
                HashMap hashMap = ZkmUtils.createHashMap();
                HashMap hashMap1 = ZkmUtils.createHashMap();
                this.programClass.renameClass(string2, hashMap, hashMap1);

                ClassRepository classRepository1;
                label69:
                {
                    try {
                        Object object = null;
                        super.classRepository.applyNameChangesToAll(hashMap1, (HashMap) null, (MessageReporter) object);
                    } catch (ZkmProcessingException zkmProcessingException) {
                        new MessageBoxDialog(super.mainWindow, "Error", zkmProcessingException.getMessage());
                        classRepository1 = super.classRepository;
                        break label69;
                    }

                    classRepository1 = super.classRepository;
                }

                classRepository1.applyFieldPrefixRenames(hashMap1);
                super.classRepository.refreshIndexes();
            }
        }

        if (ba != -1) {
            try {
                this.applyAccessFlags(ba);
                super.originalAccessIndex = super.accessComboBox.getSelectedIndex();
            } catch (InvalidEditException invalidEditException) {
                new MessageBoxDialog(super.mainWindow, "Error", invalidEditException.getMessage());
            }
        }
    }

    @Override
    public void populateModifierList(PlatformAwareJList platformAwareJList) {
        DefaultListModel defaultListModel = (DefaultListModel) platformAwareJList.getModel();
        defaultListModel.addElement("abstract");
        defaultListModel.addElement("final");
        defaultListModel.addElement("interface");
        defaultListModel.addElement("super");
    }

    public void applyAccessFlags(int ba) throws ZkmException, IOException {
        int accessFlags = this.programClass.getAccessFlags();
        ClassHierarchyNode classHierarchyNode = ClassHierarchyNode.findNode(this.programClass.getClassName());
        if ((ba & 1024) != 0 && (ba & 16) != 0) {
            throw new InvalidEditException("An abstract class cannot also be final");
        }

        if ((ba & 512) != 0 && (accessFlags & 512) == 0) {
            if (!this.programClass.getSuperclassName().equals("java/lang/Object")) {
                throw new InvalidEditException("An interface can only have Object as it's superclass");
            }

            if (classHierarchyNode.hasSubclasses()) {
                throw new InvalidEditException("This class has subclasses. It cannot be changed to an interface.");
            }
        }

        ClassRepository classRepository1;
        if ((ba & 512) == 0) {
            if ((accessFlags & 512) != 0) {
                if (classHierarchyNode.hasImplementors()) {
                    throw new InvalidEditException("This class is implemented by other classes. It must remain an interface.");
                }

                classRepository1 = super.classRepository;
            } else {
                classRepository1 = super.classRepository;
            }
        } else {
            classRepository1 = super.classRepository;
        }

        synchronized (classRepository1) {
            this.programClass.setAccessFlags(ba);
        }
    }
}
