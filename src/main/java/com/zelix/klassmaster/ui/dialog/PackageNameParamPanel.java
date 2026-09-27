package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.component.ConstraintLayout;
import com.zelix.klassmaster.ui.component.RenameFilterModel;
import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class PackageNameParamPanel extends ExcludeParamTabPanel implements FocusListener, ActionListener, ItemListener {
    public static String baseConstraints = "layout.height=nameFld.defaultHeight*8;nameLbl.centerY=nameFld.centerY;nameLbl.left=5;nameFld.left=nameLbl.right+5;nameFld.top=5;nameFld.right=container.right-5;hintLbl.left=5;hintLbl.bottom=container.bottom-5;hintLbl.right=container.right-5;";
    public static String higherLevelChkConstraints = "higherLevelExcludeChk.left=nameLbl.left;higherLevelExcludeChk.top = nameLbl.bottom+5";
    public JCheckBox higherLevelExcludeChk;
    public boolean showHigherLevelOption;

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            Object object = actionEvent.getSource();
            this.commitFieldValue(object);
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public void commitFieldValue(Object object) throws ZkmException, IOException {
        if (object == super.nameField) {
            String string = super.nameField.getText().trim();
            if (super.filterModel.getParameterKind() == 1 && string.length() == 0) {
                super.nameField.setText(super.filterModel.getPackagePattern());
                MessageBoxDialog.showMessageLater(super.ownerFrame, "Package name must be entered. Field restored.");
            } else if (!string.startsWith("!") && string.indexOf("(") == -1 && string.indexOf(")") == -1) {
                RenameFilterModel renameFilterModel;
                if (string.endsWith("^")) {
                    string = string.substring(0, string.length() - 1).trim();
                    super.nameField.setText(string);
                    renameFilterModel = super.filterModel;
                } else {
                    renameFilterModel = super.filterModel;
                }

                renameFilterModel.setPackagePattern(string);
            } else {
                super.nameField.setText(super.filterModel.getPackagePattern());
                MessageBoxDialog.showMessageLater(super.ownerFrame, "Package name must be a simple package name. Field restored.");
            }
        }
    }

    @Override
    public void focusGained(FocusEvent focusEvent) {
        if (focusEvent.getSource() == super.nameField) {
            super.hintLabel.setText("Enter name of the package. May contain the \"*\" wildcard.");
        }
    }

    public PackageNameParamPanel(JFrame jFrame, RenameFilterModel renameFilterModel, boolean showHigherLevelOption, int ba) {
        super(jFrame, renameFilterModel, ba);
        this.showHigherLevelOption = showHigherLevelOption;
        this.buildPanel();
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

    @Override
    public void buildPanel() {
        ConstraintLayout constraintLayout1 = new ConstraintLayout(this);
        this.setLayout(constraintLayout1);
        JLabel jLabel = new JLabel("Name:", 2);
        super.nameField = new JTextField();
        StringBuffer stringBuffer = new StringBuffer(baseConstraints);
        if (this.showHigherLevelOption) {
            switch (super.exclusionType) {
                case 1:
                    this.higherLevelExcludeChk = new JCheckBox("Exclude package name as well", super.filterModel.isExcludeContainingPackage());
                    break;
                case 2:
                    this.higherLevelExcludeChk = new JCheckBox("Exclude package as well", super.filterModel.isExcludeContainingPackage());
            }

            this.higherLevelExcludeChk.addItemListener(this);
            this.add(this.higherLevelExcludeChk, "higherLevelExcludeChk");
            stringBuffer.append(higherLevelChkConstraints);
        }

        super.hintLabel = new JLabel(" ");
        this.add(jLabel, "nameLbl");
        this.add(super.nameField, "nameFld");
        this.add(super.hintLabel, "hintLbl");
        constraintLayout1.parseConstraints(stringBuffer.toString());
        super.nameField.setText(super.filterModel.getPackagePattern());
        super.nameField.addFocusListener(this);
        super.nameField.addActionListener(this);
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        try {
            if (itemEvent.getStateChange() == 1) {
                super.filterModel.setExcludeContainingPackage(true);
            } else {
                super.filterModel.setExcludeContainingPackage(false);
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }
}
