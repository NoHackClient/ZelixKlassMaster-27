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
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ClassSpecifierPanel extends ExcludeParamTabPanel implements ItemListener, ListSelectionListener, FocusListener, ActionListener {
    public static String layoutSpec = "layout.height=nameFld.defaultHeight*8;column1Width=max(accessModifierComboBox.defaultWidth, modifierList.defaultWidth);accessModifierComboBox.left=5;accessModifierComboBox.top=5;accessModifierComboBox.width=column1Width;modifierList.left=5;modifierList.top=accessModifierComboBox.bottom+5;modifierList.width=column1Width;modifierList.bottom=container.bottom-hintLbl.defaultHeight-20;labelWidth=max(nameLbl.defaultWidth, extendsLbl.defaultWidth, implementsLbl.defaultWidth, annotatedByLbl.defaultWidth);nameLbl.centerY=nameFld.centerY;nameLbl.left=accessModifierComboBox.right+15;nameLbl.width=labelWidth;nameFld.left=nameLbl.right+1;nameFld.top=5;nameFld.right=container.right-5;extendsLbl.centerY=extendsFld.centerY;extendsLbl.left=accessModifierComboBox.right+15;extendsLbl.width=labelWidth;extendsFld.left=extendsLbl.right+1;extendsFld.top=nameFld.bottom+5;extendsFld.right=container.right-5;implementsLbl.centerY=implementsFld.centerY;implementsLbl.left=accessModifierComboBox.right+15;implementsLbl.width=labelWidth;implementsFld.left=implementsLbl.right+1;implementsFld.top=extendsFld.bottom+5;implementsFld.right=container.right-5;annotatedByLbl.centerY=annotatedByFld.centerY;annotatedByLbl.left=accessModifierComboBox.right+15;annotatedByLbl.width=labelWidth;annotatedByFld.left=annotatedByLbl.right+1;annotatedByFld.top=implementsFld.bottom+5;annotatedByFld.right=container.right-5;hintLbl.left=5;hintLbl.bottom=container.bottom-5;hintLbl.right=container.right-5;";
    public static String excludeCheckLayoutSpec = "higherLevelExcludeChk.left = annotatedByLbl.left;higherLevelExcludeChk.top  = annotatedByLbl.bottom+5";
    public JTextField annotatedByFld;
    public JCheckBox higherLevelExcludeChk;
    public JComboBox accessModifierComboBox;
    public DefaultComboBoxModel accessComboModel;
    public PlatformAwareJList modifierList;
    public JTextField extendsFld;
    public DefaultListModel modifierListModel;
    public JTextField nameFld;
    public JTextField implementsFld;
    public boolean showHigherLevelExclude;

    public ClassSpecifierPanel(JFrame jFrame, RenameFilterModel renameFilterModel, boolean showHigherLevelExclude, int ba) {
        super(jFrame, renameFilterModel, ba);
        this.showHigherLevelExclude = showHigherLevelExclude;
        this.buildPanel();
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

    @Override
    public void valueChanged(ListSelectionEvent listSelectionEvent) {
        try {
            PlatformAwareJList platformAwareJList = (PlatformAwareJList) listSelectionEvent.getSource();
            int[] selectedIndices = platformAwareJList.getSelectedIndices();
            StringBuffer stringBuffer = new StringBuffer();

            for (int i = 0; i < selectedIndices.length; i++) {
                stringBuffer.append(selectedIndices[i] + ",");
            }

            int size = this.modifierListModel.getSize();

            for (int i = 0; i < size; i++) {
                switch (i) {
                    case 0:
                        super.filterModel.setClassAbstract(platformAwareJList.isSelectedIndex(i));
                        break;
                    case 1:
                        super.filterModel.setClassFinal(platformAwareJList.isSelectedIndex(i));
                        break;
                    case 2:
                        super.filterModel.setClassInterface(platformAwareJList.isSelectedIndex(i));
                        break;
                    case 3:
                        super.filterModel.setClassNotAbstract(platformAwareJList.isSelectedIndex(i));
                        break;
                    case 4:
                        super.filterModel.setClassNotFinal(platformAwareJList.isSelectedIndex(i));
                        break;
                    case 5:
                        super.filterModel.setClassNotInterface(platformAwareJList.isSelectedIndex(i));
                }
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public void loadModifierSelection() {
        AccessFlagsSpec accessFlagsSpec1 = super.filterModel.getClassAccessFlags();
        if (accessFlagsSpec1 != null) {
            if (accessFlagsSpec1.isPublicRequired()) {
                this.accessModifierComboBox.setSelectedIndex(1);
            } else if (accessFlagsSpec1.isPackageRequired()) {
                this.accessModifierComboBox.setSelectedIndex(2);
            } else {
                this.accessModifierComboBox.setSelectedIndex(0);
            }

            if (accessFlagsSpec1.isAbstractRequired()) {
                this.modifierList.addSelectionInterval(0, 0);
            }

            if (accessFlagsSpec1.isFinalRequired()) {
                this.modifierList.addSelectionInterval(1, 1);
            }

            if (accessFlagsSpec1.isInterfaceRequired()) {
                this.modifierList.addSelectionInterval(2, 2);
            }

            if (accessFlagsSpec1.isAbstractForbidden()) {
                this.modifierList.addSelectionInterval(3, 3);
            }

            if (accessFlagsSpec1.isFinalForbidden()) {
                this.modifierList.addSelectionInterval(4, 4);
            }

            if (accessFlagsSpec1.isInterfaceForbidden()) {
                this.modifierList.addSelectionInterval(5, 5);
            }
        }
    }

    @Override
    public void focusGained(FocusEvent focusEvent) {
        Object object = focusEvent.getSource();
        if (object == this.nameFld) {
            super.hintLabel.setText("Enter name of the class. May contain the \"*\" wildcard.");
        } else if (object == this.extendsFld) {
            super.hintLabel.setText("Enter fully qualified class name. May not contain wildcards.");
        } else if (object == this.implementsFld) {
            super.hintLabel.setText("Enter comma separated list of fully qualified class names. May not contain wildcards.");
        } else if (object == this.annotatedByFld) {
            super.hintLabel.setText("Enter fully qualified class name.  May contain the \"*\" wildcard.");
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
        JLabel jLabel1 = new JLabel("Extends:", 2);
        this.extendsFld = new JTextField();
        JLabel jLabel2 = new JLabel("Implements:", 2);
        this.implementsFld = new JTextField();
        JLabel jLabel3 = new JLabel("Annotated by:", 2);
        this.annotatedByFld = new JTextField();
        super.hintLabel = new JLabel(" ");
        this.add(this.accessModifierComboBox, "accessModifierComboBox");
        this.add(new ZkmScrollPane(this.modifierList), "modifierList");
        this.add(this.nameFld, "nameFld");
        this.add(this.extendsFld, "extendsFld");
        this.add(this.implementsFld, "implementsFld");
        this.add(this.annotatedByFld, "annotatedByFld");
        this.add(jLabel, "nameLbl");
        this.add(jLabel1, "extendsLbl");
        this.add(jLabel2, "implementsLbl");
        this.add(jLabel3, "annotatedByLbl");
        this.add(super.hintLabel, "hintLbl");
        StringBuffer stringBuffer = new StringBuffer(layoutSpec);
        if (this.showHigherLevelExclude) {
            switch (super.exclusionType) {
                case 1:
                    this.higherLevelExcludeChk = new JCheckBox("Exclude class name as well", super.filterModel.isExcludeContainingClass());
                    break;
                case 2:
                    this.higherLevelExcludeChk = new JCheckBox("Exclude class as well", super.filterModel.isExcludeContainingClass());
            }

            this.higherLevelExcludeChk.addItemListener(this);
            this.add(this.higherLevelExcludeChk, "higherLevelExcludeChk");
            stringBuffer.append(excludeCheckLayoutSpec);
        }

        constraintLayout1.parseConstraints(stringBuffer.toString());
        this.accessComboModel.addElement("<access not specified>");
        this.accessComboModel.addElement("public");
        this.accessComboModel.addElement("package");
        this.modifierListModel.addElement("abstract");
        this.modifierListModel.addElement("final");
        this.modifierListModel.addElement("interface");
        this.modifierListModel.addElement("!abstract");
        this.modifierListModel.addElement("!final");
        this.modifierListModel.addElement("!interface");
        this.loadModifierSelection();
        this.nameFld.setText(super.filterModel.getClassName());
        this.extendsFld.setText(super.filterModel.getSuperclassName());
        this.implementsFld.setText(super.filterModel.getInterfaceNamesText());
        this.annotatedByFld.setText(super.filterModel.getClassAnnotation());
        this.accessModifierComboBox.addItemListener(this);
        this.modifierList.addListSelectionListener(this);
        this.nameFld.addFocusListener(this);
        this.extendsFld.addFocusListener(this);
        this.implementsFld.addFocusListener(this);
        this.annotatedByFld.addFocusListener(this);
        this.nameFld.addActionListener(this);
        this.extendsFld.addActionListener(this);
        this.implementsFld.addActionListener(this);
        this.annotatedByFld.addActionListener(this);
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        try {
            Object object = itemEvent.getSource();
            if (object == this.accessModifierComboBox) {
                if (itemEvent.getStateChange() == 1) {
                    switch (this.accessModifierComboBox.getSelectedIndex()) {
                        case 0:
                            super.filterModel.clearClassAccess();
                            break;
                        case 1:
                            super.filterModel.setClassPublic();
                            break;
                        case 2:
                            super.filterModel.setClassPackagePrivate();
                    }
                }
            } else if (object == this.higherLevelExcludeChk) {
                if (itemEvent.getStateChange() == 1) {
                    super.filterModel.setExcludeContainingClass(true);
                } else {
                    super.filterModel.setExcludeContainingClass(false);
                }
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
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
                this.nameFld.setText(super.filterModel.getClassName());
                MessageBoxDialog.showMessageLater(super.ownerFrame, "Class name must be specified. Field restored.");
            } else if (string.indexOf(46) == -1 && string.indexOf(47) == -1) {
                RenameFilterModel renameFilterModel;
                if (string.endsWith("^")) {
                    string = string.substring(0, string.length() - 1).trim();
                    this.nameFld.setText(string);
                    renameFilterModel = super.filterModel;
                } else {
                    renameFilterModel = super.filterModel;
                }

                renameFilterModel.setClassName(string);
            } else {
                this.nameFld.setText(super.filterModel.getClassName());
                MessageBoxDialog.showMessageLater(
                        super.ownerFrame, "Class name must be unqualified. Enter package qualifiers in the \"Containing package\" panel. Field restored."
                );
            }
        } else if (object == this.extendsFld) {
            String string1 = this.extendsFld.getText().trim();
            if (string1.indexOf("*") != -1) {
                this.extendsFld.setText(super.filterModel.getSuperclassName());
                MessageBoxDialog.showMessageLater(super.ownerFrame, "Extends class name cannot contain wildcards. Field restored.");
            } else {
                super.filterModel.setSuperclassName(string1);
            }
        } else if (object == this.implementsFld) {
            String string2 = this.implementsFld.getText().trim();
            if (string2.indexOf("*") != -1) {
                this.implementsFld.setText(super.filterModel.getInterfaceNamesText());
                MessageBoxDialog.showMessageLater(super.ownerFrame, "Implements class names cannot contain wildcards. Field restored.");
            } else {
                super.filterModel.setInterfaceNames(string2);
            }
        } else if (object == this.annotatedByFld) {
            super.filterModel.setClassAnnotation(this.annotatedByFld.getText().trim());
        }
    }
}
