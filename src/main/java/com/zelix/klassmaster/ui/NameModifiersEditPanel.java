package com.zelix.klassmaster.ui;

import com.zelix.GuiResources;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.component.ConstraintLayout;
import com.zelix.klassmaster.ui.component.EdtCallback;
import com.zelix.klassmaster.ui.component.EdtCallbackInvoker;
import com.zelix.klassmaster.ui.component.ObservingPanel;
import com.zelix.klassmaster.ui.component.PlatformAwareJList;
import com.zelix.klassmaster.ui.component.SelectionChangeReceiver;
import com.zelix.klassmaster.ui.component.SwingUtils;
import com.zelix.klassmaster.ui.component.ZkmScrollPane;
import com.zelix.klassmaster.ui.dialog.CopyableMessageDialog;
import com.zelix.klassmaster.ui.dialog.MessageBoxDialog;
import com.zelix.klassmaster.util.NamedSet;
import com.zelix.klassmaster.util.ObservableModel;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;

public abstract class NameModifiersEditPanel extends ObservingPanel implements PropertyEditListener, SelectionChangeReceiver, EdtCallback {
    public static String[] layoutConstraints = new String[]{
            "layout.minWidth=150",
            "layout.minHeight=120",
            "existingNameLbl.top=1",
            "existingNameLbl.left=5",
            "existingNameLbl.right=container.right-5",
            "propertyEditFld.top=existingNameLbl.bottom+1",
            "propertyEditFld.left=5",
            "propertyEditFld.right=container.right-5",
            "accessComboBox.top=propertyEditFld.bottom+3",
            "accessComboBox.left=5",
            "accessComboBox.right=container.right-btnWidth-10",
            "modifierLst.top=accessComboBox.bottom+3",
            "modifierLst.left=5",
            "modifierLst.right=accessComboBox.right",
            "modifierLst.bottom=container.bottom-3",
            "btnWidth=max(chngBtn.defaultWidth, undoBtn.defaultWidth)",
            "btnHeight=max(chngBtn.defaultHeight, undoBtn.defaultHeight)",
            "btnAreaHeight=container.height-propertyEditFld.bottom",
            "chngBtn.width=btnWidth",
            "chngBtn.height=btnHeight",
            "chngBtn.centerY=btnAreaHeight*30/100 + propertyEditFld.bottom",
            "chngBtn.right=container.right-5",
            "undoBtn.width=btnWidth",
            "undoBtn.height=btnHeight",
            "undoBtn.centerY=btnAreaHeight*70/100 + propertyEditFld.bottom",
            "undoBtn.right=container.right-5"
    };
    public NamedSet originalModifierIndices;
    public String originalName;
    public int originalAccessIndex;
    public int publicIndex = 99;
    public int protectedIndex = 99;
    public int privateIndex = 99;
    public int packageIndex = 99;
    public int abstractIndex = 99;
    public int staticIndex = 99;
    public int finalIndex = 99;
    public int synchronizedIndex = 99;
    public int nativeIndex = 99;
    public int volatileIndex = 99;
    public int transientIndex = 99;
    public int interfaceIndex = 99;
    public int superIndex = 99;
    public ClassRepository classRepository;
    public ZkmMainWindow mainWindow;
    public Integer abstractKey;
    public Integer staticKey;
    public Integer finalKey;
    public Integer synchronizedKey;
    public Integer nativeKey;
    public Integer volatileKey;
    public Integer transientKey;
    public Integer interfaceKey;
    public Integer superKey;
    public JLabel existingNameLbl;
    public PropertyEditFld propertyEditFld;
    public DefaultComboBoxModel accessComboModel;
    public JComboBox accessComboBox;
    public final PlatformAwareJList modifierList;
    public final DefaultListModel modifierListModel;
    public SelectionDeltaListener modifierSelectionListener;
    public JButton chngBtn;
    public JButton undoBtn;

    public void disableEditControls() {
    }

    public abstract void populateAccessChoices();

    public final boolean hasPendingChanges() {
        return !this.propertyEditFld.getText().trim().equals(this.originalName) || this.isModifierSelectionChanged();
    }

    @Override
    public final void handleAccessSelection(int ba) {
        if (this.originalAccessIndex != ba) {
            this.enableChangeButtons();
        } else {
            this.disableButtonsIfUnchanged();
        }
    }

    public abstract void applyChanges() throws ZkmException, IOException;

    @Override
    public void handleObservedChange(ObservableModel observableModel1, Object object, Object object1, Object object2) throws ZkmException, IOException {
        new EdtCallbackInvoker(this, observableModel1, object, object1, object2);
    }

    public final void disableButtonsIfUnchanged() {
        if (!this.hasPendingChanges()) {
            this.chngBtn.setEnabled(false);
            this.undoBtn.setEnabled(false);
        }
    }

    @Override
    public abstract void handleChangeOnEdt(Object object, Object object1, Object object2, Object object3) throws ZkmException, IOException;

    public NameModifiersEditPanel(ClassRepository classRepository1, ZkmMainWindow zkmMainWindow) {
        this.classRepository = classRepository1;
        this.mainWindow = zkmMainWindow;
        this.initModifierIndices();
        this.abstractKey = this.abstractIndex;
        this.staticKey = this.staticIndex;
        this.finalKey = this.finalIndex;
        this.synchronizedKey = this.synchronizedIndex;
        this.nativeKey = this.nativeIndex;
        this.volatileKey = this.volatileIndex;
        this.transientKey = this.transientIndex;
        this.interfaceKey = this.interfaceIndex;
        this.superKey = this.superIndex;
        DelegatingActionListener delegatingActionListener = new DelegatingActionListener(this);
        ConstraintLayout constraintLayout1 = new ConstraintLayout(this);
        this.setLayout(constraintLayout1);
        this.existingNameLbl = new JLabel();
        this.propertyEditFld = new PropertyEditFld();
        this.propertyEditFld.addActionListener(delegatingActionListener);
        this.propertyEditFld.getDocument().addDocumentListener(new PropertyFieldDocumentListener(this, this.propertyEditFld));
        this.add(this.propertyEditFld, "propertyEditFld");
        this.add(this.existingNameLbl, "existingNameLbl");
        this.accessComboModel = new DefaultComboBoxModel();
        this.accessComboBox = new JComboBox(this.accessComboModel);
        this.accessComboBox.setToolTipText(GuiResources.getTooltipText("ACCESS_MODIFIERS"));
        this.populateAccessChoices();
        this.accessComboBox.addItemListener(new AccessComboItemListener(this));
        this.add(this.accessComboBox, "accessComboBox");
        this.modifierList = new PlatformAwareJList(new DefaultListModel());
        this.modifierListModel = (DefaultListModel) this.modifierList.getModel();
        this.populateModifierList(this.modifierList);
        this.modifierList.setSelectionMode(2);
        this.modifierSelectionListener = new SelectionDeltaListener(this);
        this.modifierList.addListSelectionListener(this.modifierSelectionListener);
        this.add(new ZkmScrollPane(this.modifierList), "modifierLst");
        this.chngBtn = new JButton("Change");
        this.chngBtn.setToolTipText(GuiResources.getTooltipText("CHANGE_DESC"));
        this.chngBtn.addActionListener(delegatingActionListener);
        this.add(this.chngBtn, "chngBtn");
        this.undoBtn = new JButton("Restore");
        this.undoBtn.setToolTipText(GuiResources.getTooltipText("RESTORE_DESC"));
        this.undoBtn.addActionListener(delegatingActionListener);
        this.add(this.undoBtn, "undoBtn");
        constraintLayout1.setConstraints(layoutConstraints);
    }

    @Override
    public final void onEditTextChanged(String string) {
        if (!string.equals(this.originalName)) {
            this.enableChangeButtons();
        } else {
            this.disableButtonsIfUnchanged();
        }
    }

    @Override
    public final void handleSelectionDelta(int[] ba, int[] bb) {
        for (int i = 0; i < ba.length; i++) {
            if (!this.originalModifierIndices.contains(ba[i])) {
                this.enableChangeButtons();
                return;
            }
        }

        for (int i = 0; i < bb.length; i++) {
            if (this.originalModifierIndices.contains(bb[i])) {
                this.enableChangeButtons();
                return;
            }
        }

        this.disableButtonsIfUnchanged();
    }

    public abstract void initModifierIndices();

    public abstract boolean isModifierSelectionChanged();

    @Override
    public final void onEditAction(ActionEvent actionEvent) throws ZkmException, IOException {
        Component component1 = (Component) actionEvent.getSource();
        if (component1 == this.chngBtn) {
            if (this.classRepository.isClasspathChanged()) {
                new MessageBoxDialog(
                        this.mainWindow, "Classpath changed", "Classpath has been changed but the classes have not been reopened. You must reopen the classes. (G)"
                );
                return;
            }

            if (!this.classRepository.isOpenedWithoutErrors()) {
                new CopyableMessageDialog(
                        this.mainWindow,
                        "Opened classes not useable",
                        "Opened classes not useable for the reason shown below.",
                        this.classRepository.getOpenErrorMessage() + " : Please adjust your " + "Zelix KlassMaster" + " classpath or class file selections."
                );
                return;
            }

            this.mainWindow.showWaitCursor();
            this.applyChanges();
            this.mainWindow.restoreDefaultCursor();
        } else if (component1 == this.undoBtn) {
            this.propertyEditFld.setText(this.originalName);
            this.accessComboBox.setSelectedIndex(this.originalAccessIndex);
            int size = this.modifierListModel.getSize();

            for (int i = 0; i < size; i++) {
                if (this.originalModifierIndices.contains(i)) {
                    if (!this.modifierList.isSelectedIndex(i)) {
                        this.modifierList.addSelectionInterval(i, i);
                    }
                } else {
                    this.modifierList.removeSelectionInterval(i, i);
                }
            }

            this.chngBtn.setEnabled(false);
            this.undoBtn.setEnabled(false);
            SwingUtils.requestFocusOnEdt(this.propertyEditFld);
        } else {
            if (this.classRepository.isClasspathChanged()) {
                new MessageBoxDialog(
                        this.mainWindow, "Classpath changed", "Classpath has been changed but the classes have not been reopened. You must reopen the classes. (G)"
                );
                return;
            }

            if (!this.classRepository.isOpenedWithoutErrors()) {
                new CopyableMessageDialog(
                        this.mainWindow,
                        "Opened classes not useable",
                        "Opened classes not useable for the reason shown below.",
                        this.classRepository.getOpenErrorMessage() + " : Please adjust your " + "Zelix KlassMaster" + " classpath or class file selections."
                );
                return;
            }

            this.mainWindow.showWaitCursor();
            this.applyChanges();
            this.mainWindow.restoreDefaultCursor();
        }
    }

    public final void enableChangeButtons() {
        this.chngBtn.setEnabled(true);
        this.undoBtn.setEnabled(true);
    }

    public abstract void populateModifierList(PlatformAwareJList platformAwareJList);
}
