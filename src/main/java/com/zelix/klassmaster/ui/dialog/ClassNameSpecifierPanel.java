package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.component.ConstraintLayout;
import com.zelix.klassmaster.ui.component.RenameFilterModel;
import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class ClassNameSpecifierPanel extends ExcludeParamTabPanel implements FocusListener, ActionListener {
    public JTextField implementsFld;
    public JTextField extendsFld;
    public JTextField nameFld;
    public static String[] layoutSpec = new String[]{
            "layout.height=nameFld.defaultHeight*8",
            "labelWidth=max(nameLbl.defaultWidth, extendsLbl.defaultWidth, implementsLbl.defaultWidth, annotatedByLbl.defaultWidth)",
            "nameLbl.centerY=nameFld.centerY",
            "nameLbl.left=5",
            "nameLbl.width=labelWidth",
            "nameFld.left=nameLbl.right+1",
            "nameFld.top=5",
            "nameFld.right=container.right-5",
            "extendsLbl.centerY=extendsFld.centerY",
            "extendsLbl.left=5",
            "extendsLbl.width=labelWidth",
            "extendsFld.left=extendsLbl.right+1",
            "extendsFld.top=nameFld.bottom+5",
            "extendsFld.right=container.right-5",
            "implementsLbl.centerY=implementsFld.centerY",
            "implementsLbl.left=5",
            "implementsLbl.width=labelWidth",
            "implementsFld.left=implementsLbl.right+1",
            "implementsFld.top=extendsFld.bottom+5",
            "implementsFld.right=container.right-5",
            "annotatedByLbl.centerY=annotatedByFld.centerY",
            "annotatedByLbl.left=5",
            "annotatedByLbl.width=labelWidth",
            "annotatedByFld.left=annotatedByLbl.right+1",
            "annotatedByFld.top=implementsFld.bottom+5",
            "annotatedByFld.right=container.right-5",
            "hintLbl.left=5",
            "hintLbl.bottom=container.bottom-5",
            "hintLbl.right=container.right-5"
    };
    public JTextField annotatedByFld;

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            Object object = actionEvent.getSource();
            this.commitFieldValue(object);
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }

    public void commitFieldValue(Object object) throws ZkmException, IOException {
        if (object == this.nameFld) {
            String string = this.nameFld.getText().trim();
            if (string.length() == 0) {
                this.nameFld.setText(super.filterModel.getLinkSuffix());
                MessageBoxDialog.showMessageLater(super.ownerFrame, "Class suffix must be specified. Field restored.");
            } else if (string.indexOf("*") != -1) {
                this.nameFld.setText(super.filterModel.getLinkSuffix());
                MessageBoxDialog.showMessageLater(super.ownerFrame, "Class suffix cannot contain wildcards. Field restored.");
            } else {
                super.filterModel.setLinkedClassSuffix(string);
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

    @Override
    public void focusGained(FocusEvent focusEvent) {
        Object object = focusEvent.getSource();
        if (object == this.nameFld) {
            super.hintLabel.setText("Enter the class name suffix. No wildcards.");
        } else if (object == this.extendsFld) {
            super.hintLabel.setText("Enter fully qualified class name. May not contain wildcards.");
        } else if (object == this.implementsFld) {
            super.hintLabel.setText("Enter comma separated list of fully qualified class names. May not contain wildcards.");
        } else if (object == this.annotatedByFld) {
            super.hintLabel.setText("Enter fully qualified class name. May contain the \"*\" wildcard.");
        }
    }

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

    public ClassNameSpecifierPanel(JFrame jFrame, RenameFilterModel renameFilterModel, int ba) {
        super(jFrame, renameFilterModel, ba);
        this.buildPanel();
    }

    @Override
    public void buildPanel() {
        ConstraintLayout constraintLayout1 = new ConstraintLayout(this);
        this.setLayout(constraintLayout1);
        JLabel jLabel = new JLabel("Suffix:", 2);
        this.nameFld = new JTextField();
        JLabel jLabel1 = new JLabel("Extends:", 2);
        this.extendsFld = new JTextField();
        JLabel jLabel2 = new JLabel("Implements:", 2);
        this.implementsFld = new JTextField();
        JLabel jLabel3 = new JLabel("Annotated by:", 2);
        this.annotatedByFld = new JTextField();
        super.hintLabel = new JLabel(" ");
        this.add(this.nameFld, "nameFld");
        this.add(this.extendsFld, "extendsFld");
        this.add(this.implementsFld, "implementsFld");
        this.add(this.annotatedByFld, "annotatedByFld");
        this.add(jLabel, "nameLbl");
        this.add(jLabel1, "extendsLbl");
        this.add(jLabel2, "implementsLbl");
        this.add(jLabel3, "annotatedByLbl");
        this.add(super.hintLabel, "hintLbl");
        constraintLayout1.setConstraints(layoutSpec);
        this.nameFld.setText(super.filterModel.getLinkSuffix());
        this.extendsFld.setText(super.filterModel.getSuperclassName());
        this.implementsFld.setText(super.filterModel.getInterfaceNamesText());
        this.annotatedByFld.setText(super.filterModel.getClassAnnotation());
        this.nameFld.addFocusListener(this);
        this.extendsFld.addFocusListener(this);
        this.implementsFld.addFocusListener(this);
        this.annotatedByFld.addFocusListener(this);
        this.nameFld.addActionListener(this);
        this.extendsFld.addActionListener(this);
        this.implementsFld.addActionListener(this);
        this.annotatedByFld.addActionListener(this);
    }
}
