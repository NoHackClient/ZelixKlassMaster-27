package com.zelix.klassmaster.ui;

import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.LoadableConstant;
import com.zelix.klassmaster.classfile.constpool.NumericConstantEntry;
import com.zelix.klassmaster.classfile.constpool.ResolvedStringConstant;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.exceptions.InvalidEditException;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.component.ConstraintLayout;
import com.zelix.klassmaster.ui.component.EdtCallback;
import com.zelix.klassmaster.ui.component.EdtCallbackInvoker;
import com.zelix.klassmaster.ui.component.ObservingPanel;
import com.zelix.klassmaster.ui.component.PlatformAwareJList;
import com.zelix.klassmaster.ui.component.SwingUtils;
import com.zelix.klassmaster.ui.dialog.MessageBoxDialog;
import com.zelix.klassmaster.util.ListenerRegistry;
import com.zelix.klassmaster.util.ObservableModel;
import com.zelix.klassmaster.util.SetMultiMap;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class ConstantPoolEntryEditPanel extends ObservingPanel implements PropertyEditListener, EdtCallback {
    public static String[] layoutConstraints = new String[]{
            "layout.minWidth=150",
            "layout.minHeight=120",
            "entryDetailsLbl.top=5",
            "entryDetailsLbl.left=5",
            "existingValueLbl.top=entryDetailsLbl.bottom+3",
            "existingValueLbl.left=5",
            "existingValueLbl.right=container.right-5",
            "propertyEditFld.top=existingValueLbl.bottom+2",
            "propertyEditFld.left=5",
            "propertyEditFld.right=container.right-5",
            "btnWidth=max(chngBtn.defaultWidth, undoBtn.defaultWidth)",
            "btnHeight=max(chngBtn.defaultHeight, undoBtn.defaultHeight)",
            "chngBtn.centerX=container.width*25/100",
            "chngBtn.top=propertyEditFld.bottom+15",
            "undoBtn.centerX=container.width*75/100",
            "undoBtn.top=chngBtn.top"
    };
    public Frame ownerFrame;
    public LoadableConstant constant;
    public SetMultiMap linkedStringConstants;
    public ClassRepository classRepository;
    public PlatformAwareJList constantsList;
    public String currentValue;
    public JLabel entryDetailsLbl;
    public JLabel existingValueLbl;
    public JTextField propertyEditFld;
    public JButton chngBtn;
    public JButton undoBtn;

    @Override
    public void onEditTextChanged(String string) {
        if (!string.equals(this.currentValue)) {
            this.chngBtn.setEnabled(true);
            this.undoBtn.setEnabled(true);
        } else {
            this.chngBtn.setEnabled(false);
            this.undoBtn.setEnabled(false);
        }
    }

    @Override
    public void handleObservedChange(ObservableModel observableModel1, Object object, Object object1, Object object2) throws ZkmException, IOException {
        new EdtCallbackInvoker(this, observableModel1, object, object1, object2);
    }

    public void applyValueChange() throws ZkmException, IOException {
        String string = this.propertyEditFld.getText();
        if (!string.equals(this.currentValue)) {
            try {
                synchronized (this.classRepository) {
                    this.constant.setValueFromString(string);
                    Set set1 = this.linkedStringConstants.getValues(this.constant);
                    JLabel jLabel;
                    if (set1 == null) {
                        jLabel = this.existingValueLbl;
                    } else {
                        Iterator iterator = set1.iterator();

                        while (iterator.hasNext()) {
                            ((ResolvedStringConstant) iterator.next()).setValueFromString(string);
                        }

                        jLabel = this.existingValueLbl;
                    }

                    jLabel.setText(string);
                    this.chngBtn.setEnabled(false);
                    this.undoBtn.setEnabled(false);
                    this.currentValue = string;
                    int selectedIndex = this.constantsList.getSelectedIndex();
                    ((DefaultListModel) this.constantsList.getModel()).setElementAt(string, selectedIndex);
                    this.constantsList.setSelectedIndex(selectedIndex);
                    this.constantsList.ensureIndexIsVisible(selectedIndex);
                    SwingUtils.requestFocusOnEdt(this.propertyEditFld);
                }
            } catch (InvalidEditException invalidEditException) {
                new MessageBoxDialog(this.ownerFrame, "Error", invalidEditException.getMessage());
            }
        }
    }

    @Override
    public void onEditAction(ActionEvent actionEvent) throws ZkmException, IOException {
        Component component1 = (Component) actionEvent.getSource();
        if (component1 == this.chngBtn) {
            this.applyValueChange();
        } else if (component1 == this.undoBtn) {
            this.propertyEditFld.setText(this.currentValue);
            this.chngBtn.setEnabled(false);
            this.undoBtn.setEnabled(false);
            SwingUtils.requestFocusOnEdt(this.propertyEditFld);
        } else {
            this.applyValueChange();
        }
    }

    public ConstantPoolEntryEditPanel(
            NumericConstantEntry numericConstantEntry,
            ClassRepository classRepository1,
            PlatformAwareJList platformAwareJList,
            Frame frame1,
            ListenerRegistry listenerRegistry1,
            SetMultiMap setMultiMap
    ) {
        this.ownerFrame = frame1;
        this.constant = numericConstantEntry;
        this.linkedStringConstants = setMultiMap;
        this.classRepository = classRepository1;
        this.constantsList = platformAwareJList;
        listenerRegistry1.addObserver(numericConstantEntry, this, "DescListObservers");
        DelegatingActionListener delegatingActionListener = new DelegatingActionListener(this);
        ConstraintLayout constraintLayout1 = new ConstraintLayout(this);
        this.setLayout(constraintLayout1);
        this.currentValue = this.constant.getEditableValue();
        String string = this.constant.getTypeName() + " Pool Entry #" + ((ConstantPoolEntry) this.constant).getIndex();
        this.entryDetailsLbl = new JLabel(string);
        this.add(this.entryDetailsLbl, "entryDetailsLbl");
        this.existingValueLbl = new JLabel(this.currentValue);
        this.add(this.existingValueLbl, "existingValueLbl");
        this.propertyEditFld = new JTextField(this.currentValue);
        this.propertyEditFld.addActionListener(delegatingActionListener);
        this.propertyEditFld.getDocument().addDocumentListener(new PropertyEditFieldListener(this));
        this.add(this.propertyEditFld, "propertyEditFld");
        this.propertyEditFld.setEnabled(false);
        this.chngBtn = new JButton("Change");
        this.chngBtn.setEnabled(false);
        this.chngBtn.addActionListener(delegatingActionListener);
        this.add(this.chngBtn, "chngBtn");
        this.undoBtn = new JButton("Restore");
        this.undoBtn.addActionListener(delegatingActionListener);
        this.undoBtn.setEnabled(false);
        this.add(this.undoBtn, "undoBtn");
        constraintLayout1.setConstraints(layoutConstraints);
    }

    @Override
    public void handleChangeOnEdt(Object object, Object object1, Object object2, Object object3) throws ZkmException, IOException {
        this.currentValue = this.constant.getEditableValue();
        String string = this.constant.getTypeName() + " Pool Entry #" + ((ConstantPoolEntry) this.constant).getIndex();
        this.entryDetailsLbl.setText(string);
        this.existingValueLbl.setText(this.currentValue);
        this.propertyEditFld.setText(this.currentValue);
        this.undoBtn.setEnabled(false);
        this.chngBtn.setEnabled(false);
        SwingUtils.requestFocusOnEdt(this.propertyEditFld);
    }
}
