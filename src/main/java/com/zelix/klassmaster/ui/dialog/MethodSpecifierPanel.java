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

public class MethodSpecifierPanel extends ExcludeParamTabPanel implements ItemListener, ListSelectionListener, FocusListener, ActionListener {
    public DefaultListModel modifierListModel;
    public JTextField argumentsTypeFld;
    public JTextField throwsFld;
    public PlatformAwareJList modifierList;
    public DefaultComboBoxModel accessComboModel;
    public JTextField annotatedByFld;
    public JTextField nameFld;
    public JComboBox accessModifierComboBox;
    public static String[] layoutConstraints = new String[]{
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
            "typeLbl.centerY=argumentsTypeFld.centerY",
            "typeLbl.left=accessModifierComboBox.right+15",
            "typeLbl.width=labelWidth",
            "argumentsTypeFld.left=typeLbl.right+1",
            "argumentsTypeFld.top=nameFld.bottom+5",
            "argumentsTypeFld.right=container.right-5",
            "throwsLbl.centerY=throwsFld.centerY",
            "throwsLbl.left=accessModifierComboBox.right+15",
            "throwsLbl.width=labelWidth",
            "throwsFld.left=throwsLbl.right+1",
            "throwsFld.top=argumentsTypeFld.bottom+5",
            "throwsFld.right=container.right-5",
            "annotatedByLbl.centerY=annotatedByFld.centerY",
            "annotatedByLbl.left=accessModifierComboBox.right+15",
            "annotatedByLbl.width=labelWidth",
            "annotatedByFld.left=annotatedByLbl.right+1",
            "annotatedByFld.top=throwsFld.bottom+5",
            "annotatedByFld.right=container.right-5",
            "hintLbl.left=5",
            "hintLbl.bottom=container.bottom-5",
            "hintLbl.right=container.right-5"
    };

    @Override
    public void valueChanged(ListSelectionEvent listSelectionEvent) {
        try {
            PlatformAwareJList platformAwareJList = (PlatformAwareJList) listSelectionEvent.getSource();
            int size = this.modifierListModel.getSize();

            for (int i = 0; i < size; i++) {
                switch (i) {
                    case 0:
                        RenameFilterModel renameFilterModel2 = super.filterModel;
                        boolean bl2 = platformAwareJList.isSelectedIndex(i);
                        Integer integer = 3;
                        renameFilterModel2.setMemberStatic(bl2, integer);
                        break;
                    case 1:
                        RenameFilterModel renameFilterModel1 = super.filterModel;
                        boolean bl1 = platformAwareJList.isSelectedIndex(i);
                        Integer integer1 = 3;
                        renameFilterModel1.setMemberFinal(bl1, integer1);
                        break;
                    case 2:
                        super.filterModel.setMemberSynchronized(platformAwareJList.isSelectedIndex(i));
                        break;
                    case 3:
                        super.filterModel.setMemberNative(platformAwareJList.isSelectedIndex(i));
                        break;
                    case 4:
                        super.filterModel.setMemberAbstract(platformAwareJList.isSelectedIndex(i));
                        break;
                    case 5:
                        RenameFilterModel renameFilterModel = super.filterModel;
                        boolean bl = platformAwareJList.isSelectedIndex(i);
                        Integer integer2 = 3;
                        renameFilterModel.setMemberNotStatic(bl, integer2);
                        break;
                    case 6:
                        RenameFilterModel renameFilterModel3 = super.filterModel;
                        boolean bl3 = platformAwareJList.isSelectedIndex(i);
                        Integer integer3 = 3;
                        renameFilterModel3.setMemberNotFinal(bl3, integer3);
                        break;
                    case 7:
                        super.filterModel.setMemberNotSynchronized(platformAwareJList.isSelectedIndex(i));
                        break;
                    case 8:
                        super.filterModel.setMemberNotNative(platformAwareJList.isSelectedIndex(i));
                        break;
                    case 9:
                        super.filterModel.setMemberNotAbstract(platformAwareJList.isSelectedIndex(i));
                }
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            Object object = actionEvent.getSource();
            this.commitFieldValue(object);
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public MethodSpecifierPanel(JFrame jFrame, RenameFilterModel renameFilterModel, int ba) {
        super(jFrame, renameFilterModel, ba);
        this.buildPanel();
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
        JLabel jLabel1 = new JLabel("Arguments:", 2);
        this.argumentsTypeFld = new JTextField();
        JLabel jLabel2 = new JLabel("Throws:", 2);
        this.throwsFld = new JTextField();
        JLabel jLabel3 = new JLabel("Annotated by:", 2);
        this.annotatedByFld = new JTextField();
        super.hintLabel = new JLabel(" ");
        this.add(this.accessModifierComboBox, "accessModifierComboBox");
        this.add(new ZkmScrollPane(this.modifierList), "modifierList");
        this.add(this.nameFld, "nameFld");
        this.add(this.argumentsTypeFld, "argumentsTypeFld");
        this.add(this.throwsFld, "throwsFld");
        this.add(this.annotatedByFld, "annotatedByFld");
        this.add(jLabel, "nameLbl");
        this.add(jLabel1, "typeLbl");
        this.add(jLabel2, "throwsLbl");
        this.add(jLabel3, "annotatedByLbl");
        this.add(super.hintLabel, "hintLbl");
        constraintLayout1.setConstraints(layoutConstraints);
        this.accessComboModel.addElement("<access not specified>");
        this.accessComboModel.addElement("public");
        this.accessComboModel.addElement("protected");
        this.accessComboModel.addElement("private");
        this.accessComboModel.addElement("package");
        this.modifierListModel.addElement("static");
        this.modifierListModel.addElement("final");
        this.modifierListModel.addElement("synchronized");
        this.modifierListModel.addElement("native");
        this.modifierListModel.addElement("abstract");
        this.modifierListModel.addElement("!static");
        this.modifierListModel.addElement("!final");
        this.modifierListModel.addElement("!synchronized");
        this.modifierListModel.addElement("!native");
        this.modifierListModel.addElement("!abstract");
        this.selectModifiersFromSpec();
        this.nameFld.setText(super.filterModel.getMethodName());
        this.argumentsTypeFld.setText(super.filterModel.getParameterTypesText());
        this.throwsFld.setText(super.filterModel.getThrowsText());
        this.annotatedByFld.setText(super.filterModel.getMemberAnnotation());
        this.accessModifierComboBox.addItemListener(this);
        this.modifierList.addListSelectionListener(this);
        this.nameFld.addFocusListener(this);
        this.argumentsTypeFld.addFocusListener(this);
        this.throwsFld.addFocusListener(this);
        this.annotatedByFld.addFocusListener(this);
        this.nameFld.addActionListener(this);
        this.argumentsTypeFld.addActionListener(this);
        this.throwsFld.addActionListener(this);
        this.annotatedByFld.addActionListener(this);
    }

    @Override
    public void focusGained(FocusEvent focusEvent) {
        Object object = focusEvent.getSource();
        if (object == this.nameFld) {
            super.hintLabel.setText("Enter name of the method. May contain the \"*\" wildcard.");
        } else if (object == this.argumentsTypeFld) {
            super.hintLabel.setText("Enter comma separated fully qualified class names or primitive types.");
        } else if (object == this.throwsFld) {
            super.hintLabel.setText("Enter comma separated list of fully qualified class names. May not contain wildcards.");
        } else if (object == this.annotatedByFld) {
            super.hintLabel.setText("Enter a fully qualified class name. May contain the \"*\" wildcard.");
        }
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        try {
            if (itemEvent.getSource() == this.accessModifierComboBox && itemEvent.getStateChange() == 1) {
                switch (this.accessModifierComboBox.getSelectedIndex()) {
                    case 0:
                        super.filterModel.clearMemberAccess(3);
                        break;
                    case 1:
                        super.filterModel.setMemberPublic(3);
                        break;
                    case 2:
                        super.filterModel.setMemberProtected(3);
                        break;
                    case 3:
                        super.filterModel.setMemberPrivate(3);
                        break;
                    case 4:
                        super.filterModel.setMemberPackagePrivate(3);
                }
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public void selectModifiersFromSpec() {
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

            if (accessFlagsSpec1.isSynchronizedRequired()) {
                this.modifierList.addSelectionInterval(2, 2);
            }

            if (accessFlagsSpec1.isNativeRequired()) {
                this.modifierList.addSelectionInterval(3, 3);
            }

            if (accessFlagsSpec1.isAbstractRequired()) {
                this.modifierList.addSelectionInterval(4, 4);
            }

            if (accessFlagsSpec1.isStaticForbidden()) {
                this.modifierList.addSelectionInterval(5, 5);
            }

            if (accessFlagsSpec1.isFinalForbidden()) {
                this.modifierList.addSelectionInterval(6, 6);
            }

            if (accessFlagsSpec1.isSynchronizedForbidden()) {
                this.modifierList.addSelectionInterval(7, 7);
            }

            if (accessFlagsSpec1.isNativeForbidden()) {
                this.modifierList.addSelectionInterval(8, 8);
            }

            if (accessFlagsSpec1.isAbstractForbidden()) {
                this.modifierList.addSelectionInterval(9, 9);
            }
        }
    }

    @Override
    public void focusLost(FocusEvent focusEvent) {
        try {
            super.hintLabel.setText(" ");
            Object object = focusEvent.getSource();
            this.commitFieldValue(object);
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public void commitFieldValue(Object object) throws ZkmException, IOException {
        if (object == this.nameFld) {
            String string = this.nameFld.getText().trim();
            if (string.length() == 0) {
                this.nameFld.setText(super.filterModel.getMethodName());
                MessageBoxDialog.showMessageLater(super.ownerFrame, "Method name must be specified. Field restored.");
            } else {
                super.filterModel.setMethodName(string);
            }
        } else if (object == this.argumentsTypeFld) {
            String string1 = this.argumentsTypeFld.getText().trim();
            RenameFilterModel renameFilterModel;
            if (string1.length() > 1) {
                if (string1.indexOf("*") != -1) {
                    this.argumentsTypeFld.setText(super.filterModel.getParameterTypesText());
                    MessageBoxDialog.showMessageLater(
                            super.ownerFrame, "Argument type must either be a single \"*\" or else cannot contain wildcards. Field restored."
                    );
                    return;
                }

                renameFilterModel = super.filterModel;
            } else {
                renameFilterModel = super.filterModel;
            }

            renameFilterModel.setParameterTypes(string1);
        } else if (object == this.throwsFld) {
            String string2 = this.throwsFld.getText().trim();
            if (string2.indexOf("*") != -1) {
                this.throwsFld.setText(super.filterModel.getThrowsText());
                MessageBoxDialog.showMessageLater(super.ownerFrame, "Throws class names cannot contain wildcards. Field restored.");
            } else {
                super.filterModel.setThrowsNames(string2);
            }
        } else if (object == this.annotatedByFld) {
            super.filterModel.setMemberAnnotation(this.annotatedByFld.getText().trim());
        }
    }
}
