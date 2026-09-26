package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.exclude.AccessFlagsSpec;
import com.zelix.klassmaster.ui.component.ConstraintLayout;
import com.zelix.klassmaster.ui.component.PlatformAwareJList;
import com.zelix.klassmaster.ui.component.RenameFilterModel;
import com.zelix.klassmaster.ui.component.ZkmScrollPane;
import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class FieldSpecifierPanel extends ExcludeParamTabPanel implements ItemListener, ListSelectionListener, FocusListener, ActionListener {
    public PlatformAwareJList modifierList;
    public JTextField nameFld;
    public JTextField annotatedByFld;
    public JComboBox accessModifierComboBox;
    public static String[] layoutSpec = new String[]{
            "layout.height=nameFld.defaultHeight*8",
            "column1Width=max(accessModifierComboBox.defaultWidth, modifierList.defaultWidth)",
            "accessModifierComboBox.left=5",
            "accessModifierComboBox.top=5",
            "accessModifierComboBox.width=column1Width",
            "modifierList.left=5",
            "modifierList.top=accessModifierComboBox.bottom+5",
            "modifierList.width=column1Width",
            "modifierList.bottom=container.bottom-hintLbl.defaultHeight-20",
            "labelWidth=max(nameLbl.defaultWidth, typeLbl.defaultWidth, annotatedByLbl.defaultWidth)",
            "nameLbl.centerY=nameFld.centerY",
            "nameLbl.left=accessModifierComboBox.right+15",
            "nameLbl.width=labelWidth",
            "nameFld.left=nameLbl.right+1",
            "nameFld.top=5",
            "nameFld.right=container.right-5",
            "typeLbl.centerY=typeFld.centerY",
            "typeLbl.left=accessModifierComboBox.right+15",
            "typeLbl.width=labelWidth",
            "typeFld.left=typeLbl.right+1",
            "typeFld.top=nameFld.bottom+5",
            "typeFld.right=container.right-5",
            "annotatedByLbl.centerY=annotatedByFld.centerY",
            "annotatedByLbl.left=accessModifierComboBox.right+15",
            "annotatedByLbl.width=labelWidth",
            "annotatedByFld.left=annotatedByLbl.right+1",
            "annotatedByFld.top=typeFld.bottom+5",
            "annotatedByFld.right=container.right-5",
            "hintLbl.left=5",
            "hintLbl.bottom=container.bottom-5",
            "hintLbl.right=container.right-5"
    };
    public DefaultComboBoxModel accessComboModel;
    public JTextField typeFld;
    public DefaultListModel modifierListModel;

    @Override
    public void focusLost(FocusEvent focusEvent) {
        try {
            super.hintLabel.setText(" ");
            Object object = focusEvent.getSource();
            this.commitFieldValue(object);
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }

    public FieldSpecifierPanel(JFrame jFrame, RenameFilterModel renameFilterModel, int ba) {
        super(jFrame, renameFilterModel, ba);
        this.buildPanel();
    }

    public void commitFieldValue(Object object) throws ZkmException, IOException {
        if (object == this.nameFld) {
            String string = this.nameFld.getText().trim();
            if (string.length() == 0) {
                this.nameFld.setText(super.filterModel.getFieldName());
                MessageBoxDialog.showMessageLater(super.ownerFrame, "Field name must be specified. Field restored.");
            } else {
                super.filterModel.setFieldName(string);
            }
        } else if (object == this.typeFld) {
            String string1 = this.typeFld.getText().trim();
            if (string1.indexOf("*") != -1) {
                this.typeFld.setText(super.filterModel.getFieldType());
                MessageBoxDialog.showMessageLater(super.ownerFrame, "Field type cannot contain wildcards. Field restored.");
            } else {
                super.filterModel.setFieldType(string1);
            }
        } else if (object == this.annotatedByFld) {
            super.filterModel.setMemberAnnotation(this.annotatedByFld.getText().trim());
        }
    }

    public void loadModifierSelection() {
        AccessFlagsSpec accessFlagsSpec1 = super.filterModel.getMemberAccessFlags();
        if (accessFlagsSpec1 != null) {
            if (accessFlagsSpec1.isPublicRequired()) {
                this.accessModifierComboBox.setSelectedIndex(1);
            } else if (accessFlagsSpec1.isProtectedRequired()) {
                this.accessModifierComboBox.setSelectedIndex(2);
            } else if (accessFlagsSpec1.isPrivateRequired()) {
                this.accessModifierComboBox.setSelectedIndex(3);
            } else if (accessFlagsSpec1.isPackageRequired()) {
                this.accessModifierComboBox.setSelectedIndex(4);
            } else {
                this.accessModifierComboBox.setSelectedIndex(0);
            }

            if (accessFlagsSpec1.isStaticRequired()) {
                this.modifierList.addSelectionInterval(0, 0);
            }

            if (accessFlagsSpec1.isFinalRequired()) {
                this.modifierList.addSelectionInterval(1, 1);
            }

            if (accessFlagsSpec1.isVolatileRequired()) {
                this.modifierList.addSelectionInterval(2, 2);
            }

            if (accessFlagsSpec1.isTransientRequired()) {
                this.modifierList.addSelectionInterval(3, 3);
            }

            if (accessFlagsSpec1.isStaticForbidden()) {
                this.modifierList.addSelectionInterval(4, 4);
            }

            if (accessFlagsSpec1.isFinalForbidden()) {
                this.modifierList.addSelectionInterval(5, 5);
            }

            if (accessFlagsSpec1.isVolatileForbidden()) {
                this.modifierList.addSelectionInterval(6, 6);
            }

            if (accessFlagsSpec1.isTransientForbidden()) {
                this.modifierList.addSelectionInterval(7, 7);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            Object object = actionEvent.getSource();
            this.commitFieldValue(object);
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }

    @Override
    public void focusGained(FocusEvent focusEvent) {
        Object object = focusEvent.getSource();
        if (object == this.nameFld) {
            super.hintLabel.setText("Enter name of the field. May contain the \"*\" wildcard.");
        } else if (object == this.typeFld) {
            super.hintLabel.setText("Enter fully qualified class name or a primitive type. No wildcards.");
        } else if (object == this.annotatedByFld) {
            super.hintLabel.setText("Enter fully qualified class name. May contain the \"*\" wildcard.");
        }
    }

    @Override
    public void buildPanel() {
        ConstraintLayout constraintLayout1 = new ConstraintLayout(this);
        this.setLayout(constraintLayout1);
        this.accessComboModel = new DefaultComboBoxModel();
        this.accessModifierComboBox = new JComboBox(this.accessComboModel);
        this.modifierListModel = new DefaultListModel();
        this.modifierList = new PlatformAwareJList(this.modifierListModel);
        this.modifierList.setSelectionMode(2);
        JLabel jLabel = new JLabel("Name:", 2);
        this.nameFld = new JTextField();
        JLabel jLabel1 = new JLabel("Type:", 2);
        this.typeFld = new JTextField();
        JLabel jLabel2 = new JLabel("Annotated by:", 2);
        this.annotatedByFld = new JTextField();
        super.hintLabel = new JLabel(" ");
        this.add(this.accessModifierComboBox, "accessModifierComboBox");
        this.add(new ZkmScrollPane(this.modifierList), "modifierList");
        this.add(this.nameFld, "nameFld");
        this.add(this.typeFld, "typeFld");
        this.add(this.annotatedByFld, "annotatedByFld");
        this.add(super.hintLabel, "hintLbl");
        this.add(jLabel, "nameLbl");
        this.add(jLabel1, "typeLbl");
        this.add(jLabel2, "annotatedByLbl");
        constraintLayout1.setConstraints(layoutSpec);
        this.accessComboModel.addElement("<access not specified>");
        this.accessComboModel.addElement("public");
        this.accessComboModel.addElement("protected");
        this.accessComboModel.addElement("private");
        this.accessComboModel.addElement("package");
        this.modifierListModel.addElement("static");
        this.modifierListModel.addElement("final");
        this.modifierListModel.addElement("volatile");
        this.modifierListModel.addElement("transient");
        this.modifierListModel.addElement("!static");
        this.modifierListModel.addElement("!final");
        this.modifierListModel.addElement("!volatile");
        this.modifierListModel.addElement("!transient");
        this.loadModifierSelection();
        this.nameFld.setText(super.filterModel.getFieldName());
        this.typeFld.setText(super.filterModel.getFieldType());
        this.annotatedByFld.setText(super.filterModel.getMemberAnnotation());
        this.accessModifierComboBox.addItemListener(this);
        this.modifierList.addListSelectionListener(this);
        this.nameFld.addFocusListener(this);
        this.typeFld.addFocusListener(this);
        this.annotatedByFld.addFocusListener(this);
        this.nameFld.addActionListener(this);
        this.typeFld.addActionListener(this);
        this.annotatedByFld.addActionListener(this);
    }

    @Override
    public void valueChanged(ListSelectionEvent listSelectionEvent) {
        try {
            PlatformAwareJList platformAwareJList = (PlatformAwareJList) listSelectionEvent.getSource();
            int size = this.modifierListModel.getSize();

            for (int i = 0; i < size; i++) {
                switch (i) {
                    case 0:
                        RenameFilterModel renameFilterModel3 = super.filterModel;
                        boolean bl4 = platformAwareJList.isSelectedIndex(i);
                        Integer integer = 2;
                        renameFilterModel3.setMemberStatic(bl4, integer);
                        break;
                    case 1:
                        RenameFilterModel renameFilterModel2 = super.filterModel;
                        boolean bl3 = platformAwareJList.isSelectedIndex(i);
                        Integer integer1 = 2;
                        renameFilterModel2.setMemberFinal(bl3, integer1);
                        break;
                    case 2:
                        super.filterModel.setMemberVolatile(platformAwareJList.isSelectedIndex(i));
                        break;
                    case 3:
                        super.filterModel.setMemberTransient(platformAwareJList.isSelectedIndex(i));
                        break;
                    case 4:
                        RenameFilterModel renameFilterModel1 = super.filterModel;
                        boolean bl2 = platformAwareJList.isSelectedIndex(i);
                        Integer integer2 = 2;
                        renameFilterModel1.setMemberNotStatic(bl2, integer2);
                        break;
                    case 5:
                        RenameFilterModel renameFilterModel = super.filterModel;
                        boolean bl1 = platformAwareJList.isSelectedIndex(i);
                        Integer integer3 = 2;
                        renameFilterModel.setMemberNotFinal(bl1, integer3);
                        break;
                    case 6:
                        RenameFilterModel renameFilterModel4 = super.filterModel;
                        boolean bl6 = platformAwareJList.isSelectedIndex(i);
                        boolean bl5 = false;
                        boolean bl = bl6;
                        renameFilterModel4.setMemberNotVolatile(bl);
                        break;
                    case 7:
                        super.filterModel.setMemberNotTransient(platformAwareJList.isSelectedIndex(i));
                }
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        try {
            if (itemEvent.getSource() == this.accessModifierComboBox && itemEvent.getStateChange() == 1) {
                switch (this.accessModifierComboBox.getSelectedIndex()) {
                    case 0:
                        super.filterModel.clearMemberAccess(2);
                        break;
                    case 1:
                        super.filterModel.setMemberPublic(2);
                        break;
                    case 2:
                        super.filterModel.setMemberProtected(2);
                        break;
                    case 3:
                        super.filterModel.setMemberPrivate(2);
                        break;
                    case 4:
                        super.filterModel.setMemberPackagePrivate(2);
                }
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }
}
