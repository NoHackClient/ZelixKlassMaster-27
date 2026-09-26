package com.zelix.klassmaster.ui.dialog;

import com.zelix.GuiResources;
import com.zelix.klassmaster.config.ExclusionWizardSettings;
import com.zelix.klassmaster.config.MixedCaseNamesMode;
import com.zelix.klassmaster.config.ObfuscateOptions;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.rename.RootPackageNode;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.ui.ZkmMainWindow;
import com.zelix.klassmaster.ui.component.SwingUtils;
import com.zelix.klassmaster.util.DialogCallback;
import com.zelix.klassmaster.util.ObservableHolder;

import java.awt.Dimension;
import java.io.IOException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class ObfuscateOptionsDialog extends ObfuscateOptionsDialogBase {
    public DefaultComboBoxModel obfuscateFlowModel;
    public DefaultComboBoxModel lineNumbersModel;
    public JCheckBox randomizeChk;
    public DefaultComboBoxModel encryptStringsModel;
    public JComboBox exceptionObfuscationComboBox;
    public JComboBox mixedCaseComboBox;
    public JCheckBox collapsePackagesChk;
    public static String layoutConstraints = "container.defaultBottomPad=10;column2X=max(changeLogInChk.defaultWidth,changeLogOutChk.defaultWidth,obfuscateFlowLbl.defaultWidth,encryptStringsLbl.defaultWidth,collapsePackagesChk.defaultWidth,aggressiveMethodRenamingChk.defaultWidth,randomizeChk.defaultWidth,keepInnerClassesLbl.defaultWidth,keepGenericsLbl.defaultWidth,removeLineNumbersLbl.defaultWidth)+12;logFileTextWidth=max(logFileInTxt.defaultWidth, logFileOutTxt.defaultWidth);changeLogInChk.centerY=logFileInTxt.centerY;changeLogInChk.left=10;logFileInTxt.top=10;logFileInTxt.left=column2X;logFileInTxt.right=container.right-changeLogInBrowseBtn.defaultWidth-10;logFileInTxt.minWidth=logFileTextWidth;changeLogInBrowseBtn.centerY=logFileInTxt.centerY;changeLogInBrowseBtn.left=logFileInTxt.right+5;changeLogInBrowseBtn.right=container.right-10;changeLogOutChk.centerY=logFileOutTxt.centerY;changeLogOutChk.left=10;logFileOutTxt.top=logFileInTxt.bottom+10;logFileOutTxt.left=column2X;logFileOutTxt.right=logFileInTxt.right;logFileOutTxt.minWidth=logFileTextWidth;changeLogOutBrowseBtn.centerY=logFileOutTxt.centerY;changeLogOutBrowseBtn.left=changeLogInBrowseBtn.left;changeLogOutBrowseBtn.right=container.right-10;obfuscateFlowLbl.centerY=obfuscateFlowComboBox.centerY;obfuscateFlowLbl.left=10;obfuscateFlowComboBox.top=logFileOutTxt.bottom+10;obfuscateFlowComboBox.left=column2X;obfuscateFlowComboBox.right=container.right-10;exceptionObfuscationLbl.centerY=exceptionObfuscationComboBox.centerY;exceptionObfuscationLbl.left=10;exceptionObfuscationComboBox.top=obfuscateFlowComboBox.bottom+10;exceptionObfuscationComboBox.left=column2X;exceptionObfuscationComboBox.right=container.right-10;encryptStringsLbl.centerY=encryptStringsComboBox.centerY;encryptStringsLbl.left=10;encryptStringsComboBox.top=exceptionObfuscationComboBox.bottom+10;encryptStringsComboBox.left=column2X;encryptStringsComboBox.right=container.right-10;encryptIntegersLbl.centerY=encryptIntegersComboBox.centerY;encryptIntegersLbl.left=10;encryptIntegersComboBox.top=encryptStringsComboBox.bottom+10;encryptIntegersComboBox.left=column2X;encryptIntegersComboBox.right=container.right-10;encryptLongsLbl.centerY=encryptLongsComboBox.centerY;encryptLongsLbl.left=10;encryptLongsComboBox.top=encryptIntegersComboBox.bottom+10;encryptLongsComboBox.left=column2X;encryptLongsComboBox.right=container.right-10;mixedCaseLbl.centerY=mixedCaseComboBox.centerY;mixedCaseLbl.left=10;mixedCaseComboBox.top=encryptLongsComboBox.bottom+10;mixedCaseComboBox.left=column2X;mixedCaseComboBox.right=container.right-10;defaultCollapsePackageTxt.top=mixedCaseComboBox.bottom+10;collapsePackagesChk.centerY=defaultCollapsePackageTxt.centerY;collapsePackagesChk.left=10;defaultCollapsePackageTxt.left=column2X;defaultCollapsePackageTxt.right=container.right-10;aggressiveMethodRenamingChk.top=defaultCollapsePackageTxt.bottom+10;aggressiveMethodRenamingChk.left=10;randomizeChk.top=aggressiveMethodRenamingChk.bottom+10;randomizeChk.left=10;keepInnerClassesLbl.centerY=keepInnerClassesComboBox.centerY;keepInnerClassesLbl.left=10;keepInnerClassesComboBox.top=randomizeChk.bottom+10;keepInnerClassesComboBox.left=column2X;keepInnerClassesComboBox.right=container.right-10;keepGenericsLbl.centerY=keepGenericsComboBox.centerY;keepGenericsLbl.left=10;keepGenericsComboBox.top=keepInnerClassesComboBox.bottom+10;keepGenericsComboBox.left=column2X;keepGenericsComboBox.right=container.right-10;localVariablesLbl.centerY=localVariablesComboBox.centerY;localVariablesLbl.left=10;localVariablesComboBox.top=keepGenericsComboBox.bottom+10;localVariablesComboBox.left=column2X;localVariablesComboBox.right=container.right-10;removeLineNumbersLbl.centerY=removeLineNumbersComboBox.centerY;removeLineNumbersLbl.left=10;removeLineNumbersComboBox.top=localVariablesComboBox.bottom+10;removeLineNumbersComboBox.left=column2X;removeLineNumbersComboBox.right=container.right-10;obfuscateReferencesChk.top=removeLineNumbersLbl.bottom+10;obfuscateReferencesChk.left=10;autoReflectionChk.top=obfuscateReferencesChk.bottom+10;autoReflectionChk.left=10;methodParameterChangesLbl.centerY=methodParameterChangesComboBox.centerY;methodParameterChangesLbl.left=10;methodParameterChangesComboBox.top=autoReflectionChk.bottom+10;methodParameterChangesComboBox.left=column2X;methodParameterChangesComboBox.right=container.right-10;obfuscateMethodParametersChk.top=methodParameterChangesComboBox.bottom+10;obfuscateMethodParametersChk.left=10;keepBalancedLocksChk.top=obfuscateMethodParametersChk.bottom+10;keepBalancedLocksChk.left=10;preverifyChk.top=keepBalancedLocksChk.bottom+10;preverifyChk.left=10;";
    public JComboBox encryptIntegersComboBox;
    public DefaultComboBoxModel exceptionObfuscationModel;
    public JComboBox obfuscateFlowComboBox;
    public DefaultComboBoxModel mixedCaseModel;
    public JComboBox encryptStringsComboBox;
    public DefaultComboBoxModel keepGenericsModel;
    public JComboBox keepGenericsComboBox;
    public JCheckBox aggressiveMethodRenamingChk;
    public JComboBox removeLineNumbersComboBox;
    public DefaultComboBoxModel encryptLongsModel;
    public DefaultComboBoxModel localVariablesModel;
    public JComboBox localVariablesComboBox;
    public JCheckBox keepBalancedLocksChk;
    public JComboBox encryptLongsComboBox;
    public JCheckBox preverifyChk;
    public JTextField defaultCollapsePackageTxt;
    public DefaultComboBoxModel encryptIntegersModel;

    @Override
    public void buildOptionsPanel(StringBuffer stringBuffer) {
        JLabel jLabel = new JLabel("Obfuscate control flow:");
        this.obfuscateFlowModel = new DefaultComboBoxModel();
        this.obfuscateFlowComboBox = new JComboBox(this.obfuscateFlowModel);
        this.obfuscateFlowModel.addElement("none");
        this.obfuscateFlowModel.addElement("light");
        this.obfuscateFlowModel.addElement("normal");
        this.obfuscateFlowModel.addElement("aggressive");
        JLabel jLabel1 = new JLabel("Encrypt String literals:");
        this.encryptStringsModel = new DefaultComboBoxModel();
        this.encryptStringsComboBox = new JComboBox(this.encryptStringsModel);
        this.encryptStringsModel.addElement("none");
        this.encryptStringsModel.addElement("normal");
        this.encryptStringsModel.addElement("aggressive");
        this.encryptStringsModel.addElement("flow obfuscate");
        this.encryptStringsModel.addElement("enhanced");
        JLabel jLabel2 = new JLabel("Encrypt Integer Constants:");
        this.encryptIntegersModel = new DefaultComboBoxModel();
        this.encryptIntegersComboBox = new JComboBox(this.encryptIntegersModel);
        this.encryptIntegersModel.addElement("none");
        this.encryptIntegersModel.addElement("normal");
        this.encryptIntegersModel.addElement("aggressive");
        JLabel jLabel3 = new JLabel("Encrypt Long Constants:");
        this.encryptLongsModel = new DefaultComboBoxModel();
        this.encryptLongsComboBox = new JComboBox(this.encryptLongsModel);
        this.encryptLongsModel.addElement("none");
        this.encryptLongsModel.addElement("normal");
        JLabel jLabel4 = new JLabel("Mixed case class names:");
        this.mixedCaseModel = new DefaultComboBoxModel();
        this.mixedCaseComboBox = new JComboBox(this.mixedCaseModel);
        this.mixedCaseModel.addElement("true");
        this.mixedCaseModel.addElement("false");
        this.mixedCaseModel.addElement("if only in archive");
        this.collapsePackagesChk = new JCheckBox("collapse packages. Default name:", false);
        this.defaultCollapsePackageTxt = new JTextField();
        this.aggressiveMethodRenamingChk = new JCheckBox("aggressively rename methods", false);
        this.randomizeChk = new JCheckBox("randomize obfuscation", false);
        JLabel jLabel5 = new JLabel("Keep inner class information:");
        super.keepInnerClassesModel = new DefaultComboBoxModel();
        super.keepInnerClassesComboBox = new JComboBox(super.keepInnerClassesModel);
        super.keepInnerClassesModel.addElement("true");
        super.keepInnerClassesModel.addElement("false");
        super.keepInnerClassesModel.addElement("if name not obfuscated");
        JLabel jLabel6 = new JLabel("Keep generics information:");
        this.keepGenericsModel = new DefaultComboBoxModel();
        this.keepGenericsComboBox = new JComboBox(this.keepGenericsModel);
        this.keepGenericsModel.addElement("true");
        this.keepGenericsModel.addElement("false");
        JLabel jLabel7 = new JLabel("Local variable tables:");
        this.localVariablesModel = new DefaultComboBoxModel();
        this.localVariablesComboBox = new JComboBox(this.localVariablesModel);
        this.localVariablesModel.addElement("delete");
        this.localVariablesModel.addElement("keep");
        this.localVariablesModel.addElement("obfuscate");
        this.localVariablesModel.addElement("keep visible method parameters");
        this.localVariablesModel.addElement("keep visible parameters if name not obfuscated");
        this.localVariablesModel.addElement("keep parameters if name not obfuscated");
        JLabel jLabel8 = new JLabel("Line number tables:");
        this.lineNumbersModel = new DefaultComboBoxModel();
        this.removeLineNumbersComboBox = new JComboBox(this.lineNumbersModel);
        this.lineNumbersModel.addElement("delete");
        this.lineNumbersModel.addElement("scramble");
        this.lineNumbersModel.addElement("keep");
        JLabel jLabel9 = new JLabel("Exception obfuscation:");
        this.exceptionObfuscationModel = new DefaultComboBoxModel();
        this.exceptionObfuscationComboBox = new JComboBox(this.exceptionObfuscationModel);
        this.exceptionObfuscationModel.addElement("none");
        this.exceptionObfuscationModel.addElement("light");
        this.exceptionObfuscationModel.addElement("heavy");
        super.obfuscateReferencesChk = new JCheckBox("obfuscate references", true);
        JLabel jLabel10 = new JLabel("Method parameter changes:");
        super.methodParameterChangesModel = new DefaultComboBoxModel();
        super.methodParameterChangesComboBox = new JComboBox(super.methodParameterChangesModel);
        super.methodParameterChangesModel.addElement("none");
        super.methodParameterChangesModel.addElement("normal");
        super.methodParameterChangesModel.addElement("random");
        super.methodParameterChangesModel.addElement("flowObfuscate");
        super.obfuscateMethodParametersChk = new JCheckBox("obfuscate method parameters", true);
        super.autoReflectionChk = new JCheckBox("auto reflection handling", true);
        this.keepBalancedLocksChk = new JCheckBox("keep balanced locks", false);
        this.preverifyChk = new JCheckBox("preverify", true);
        CollapsePackagesCheckListener collapsePackagesCheckListener = new CollapsePackagesCheckListener(this);
        this.collapsePackagesChk.addItemListener(collapsePackagesCheckListener);
        super.mainPanel.add(super.changeLogInChk, "changeLogInChk");
        super.mainPanel.add(super.logFileInTxt, "logFileInTxt");
        super.mainPanel.add(super.changeLogInBrowseBtn, "changeLogInBrowseBtn");
        super.mainPanel.add(super.changeLogOutChk, "changeLogOutChk");
        super.mainPanel.add(super.logFileOutTxt, "logFileOutTxt");
        super.mainPanel.add(super.changeLogOutBrowseBtn, "changeLogOutBrowseBtn");
        super.mainPanel.add(jLabel, "obfuscateFlowLbl");
        super.mainPanel.add(this.obfuscateFlowComboBox, "obfuscateFlowComboBox");
        super.mainPanel.add(jLabel9, "exceptionObfuscationLbl");
        super.mainPanel.add(this.exceptionObfuscationComboBox, "exceptionObfuscationComboBox");
        super.mainPanel.add(jLabel1, "encryptStringsLbl");
        super.mainPanel.add(this.encryptStringsComboBox, "encryptStringsComboBox");
        super.mainPanel.add(jLabel2, "encryptIntegersLbl");
        super.mainPanel.add(this.encryptIntegersComboBox, "encryptIntegersComboBox");
        super.mainPanel.add(jLabel3, "encryptLongsLbl");
        super.mainPanel.add(this.encryptLongsComboBox, "encryptLongsComboBox");
        super.mainPanel.add(jLabel4, "mixedCaseLbl");
        super.mainPanel.add(this.mixedCaseComboBox, "mixedCaseComboBox");
        super.mainPanel.add(this.collapsePackagesChk, "collapsePackagesChk");
        super.mainPanel.add(this.defaultCollapsePackageTxt, "defaultCollapsePackageTxt");
        super.mainPanel.add(this.randomizeChk, "randomizeChk");
        super.mainPanel.add(this.aggressiveMethodRenamingChk, "aggressiveMethodRenamingChk");
        super.mainPanel.add(jLabel5, "keepInnerClassesLbl");
        super.mainPanel.add(super.keepInnerClassesComboBox, "keepInnerClassesComboBox");
        super.mainPanel.add(jLabel6, "keepGenericsLbl");
        super.mainPanel.add(this.keepGenericsComboBox, "keepGenericsComboBox");
        super.mainPanel.add(jLabel7, "localVariablesLbl");
        super.mainPanel.add(this.localVariablesComboBox, "localVariablesComboBox");
        super.mainPanel.add(jLabel8, "removeLineNumbersLbl");
        super.mainPanel.add(this.removeLineNumbersComboBox, "removeLineNumbersComboBox");
        super.mainPanel.add(super.obfuscateReferencesChk, "obfuscateReferencesChk");
        super.mainPanel.add(super.autoReflectionChk, "autoReflectionChk");
        super.mainPanel.add(jLabel10, "methodParameterChangesLbl");
        super.mainPanel.add(super.methodParameterChangesComboBox, "methodParameterChangesComboBox");
        super.mainPanel.add(super.obfuscateMethodParametersChk, "obfuscateMethodParametersChk");
        super.mainPanel.add(this.keepBalancedLocksChk, "keepBalancedLocksChk");
        super.mainPanel.add(this.preverifyChk, "preverifyChk");
        stringBuffer.append(layoutConstraints);
    }

    @Override
    public boolean validateInputs() throws ZkmException, IOException {
        if (this.collapsePackagesChk.isSelected()) {
            String string = this.defaultCollapsePackageTxt.getText();
            ObservableHolder observableHolder = new ObservableHolder();
            String string1 = RootPackageNode.normalizePackageName(string, observableHolder);
            if (!observableHolder.isValueNull()) {
                new MessageBoxDialog(this, "Error", "Invalid default package name '" + string + "' : \"" + (String) observableHolder.getValue() + "\"");
                SwingUtils.requestFocusOnEdt(this.defaultCollapsePackageTxt);
                return false;
            }

            this.defaultCollapsePackageTxt.setText(string1.replace('/', '.'));
        }

        if (this.removeLineNumbersComboBox.getSelectedItem().equals("scramble") && !super.changeLogOutChk.isSelected()) {
            new MessageBoxDialog(this, "Error", "Must produce a change log if scramble line numbers chosen");
            SwingUtils.requestFocusOnEdt(super.changeLogOutChk);
            return false;
        } else {
            return super.validateInputs();
        }
    }

    public ObfuscateOptionsDialog(
            String string,
            String string1,
            ZkmMainWindow zkmMainWindow,
            ObfuscateOptions obfuscateOptions1,
            ExclusionWizardSettings exclusionWizardSettings,
            ScriptEnvironment scriptEnvironment1,
            DialogCallback dialogCallback1
    ) {
        super(string, string1, zkmMainWindow, obfuscateOptions1, exclusionWizardSettings, scriptEnvironment1, dialogCallback1);
    }

    @Override
    public final void showHelp() {
        GuiResources.showHelpTopic("033");
    }

    @Override
    public void saveExtraOptions() {
        super.obfuscateOptions.u = this.keepGenericsComboBox.getSelectedIndex();
        super.obfuscateOptions.i = this.removeLineNumbersComboBox.getSelectedIndex();
        super.obfuscateOptions.C = this.exceptionObfuscationComboBox.getSelectedIndex();
        super.obfuscateOptions.o = this.obfuscateFlowComboBox.getSelectedIndex();
        super.obfuscateOptions.as = this.encryptStringsComboBox.getSelectedIndex();
        super.obfuscateOptions.dZ = this.encryptIntegersComboBox.getSelectedIndex();
        super.obfuscateOptions.dC = this.encryptLongsComboBox.getSelectedIndex();
        super.obfuscateOptions.F = MixedCaseNamesMode.fromValue(this.mixedCaseComboBox.getSelectedIndex());
        super.obfuscateOptions.B = this.localVariablesComboBox.getSelectedIndex();
        super.obfuscateOptions.z = this.randomizeChk.isSelected();
        super.obfuscateOptions.ar = this.keepBalancedLocksChk.isSelected();
        super.obfuscateOptions.j = this.preverifyChk.isSelected();
        super.obfuscateOptions.s = this.collapsePackagesChk.isSelected();
        super.obfuscateOptions.w = this.aggressiveMethodRenamingChk.isSelected();
        super.obfuscateOptions.t = this.defaultCollapsePackageTxt.getText().replace('.', '/');
        if (super.obfuscateOptions.o != 0) {
            System.out.println("Evaluation version obfuscates control flow in only one or two methods in each class");
        }
    }

    @Override
    public void loadExtraOptions() {
        this.obfuscateFlowComboBox.setSelectedIndex(super.obfuscateOptions.o);
        this.encryptStringsComboBox.setSelectedIndex(super.obfuscateOptions.as);
        this.encryptIntegersComboBox.setSelectedIndex(super.obfuscateOptions.dZ);
        this.encryptLongsComboBox.setSelectedIndex(super.obfuscateOptions.dC);
        if (!super.obfuscateOptions.isLoaded()) {
            if (super.exclusionSettings.getApplicationType() != 0 && super.exclusionSettings.getApplicationType() != 1) {
                this.aggressiveMethodRenamingChk.setSelected(true);
            } else {
                this.aggressiveMethodRenamingChk.setSelected(false);
            }
        } else if (super.exclusionSettings.getApplicationType() != 0 && super.exclusionSettings.getApplicationType() != 1) {
            this.aggressiveMethodRenamingChk.setSelected(super.obfuscateOptions.w);
        } else {
            this.aggressiveMethodRenamingChk.setSelected(false);
        }

        if (!super.obfuscateOptions.isLoaded()) {
            if (super.exclusionSettings.getApplicationType() != 2 && super.exclusionSettings.getApplicationType() != 3) {
                this.collapsePackagesChk.setSelected(false);
            } else {
                this.collapsePackagesChk.setSelected(true);
            }
        } else {
            this.collapsePackagesChk.setSelected(super.obfuscateOptions.s);
        }

        JTextField jTextField;
        if (super.obfuscateOptions.t != null) {
            this.defaultCollapsePackageTxt.setText(super.obfuscateOptions.t.replace('/', '.'));
            jTextField = this.defaultCollapsePackageTxt;
        } else {
            this.defaultCollapsePackageTxt.setText("");
            jTextField = this.defaultCollapsePackageTxt;
        }

        jTextField.setEnabled(this.collapsePackagesChk.isSelected());
        this.randomizeChk.setSelected(super.obfuscateOptions.z);
        this.keepGenericsComboBox.setSelectedIndex(super.obfuscateOptions.u);
        this.mixedCaseComboBox.setSelectedIndex(super.obfuscateOptions.F.getValue());
        this.localVariablesComboBox.setSelectedIndex(super.obfuscateOptions.B);
        this.removeLineNumbersComboBox.setSelectedIndex(super.obfuscateOptions.i);
        this.exceptionObfuscationComboBox.setSelectedIndex(super.obfuscateOptions.C);
        this.preverifyChk.setSelected(super.obfuscateOptions.j);
        this.keepBalancedLocksChk.setSelected(super.obfuscateOptions.ar);
    }

    @Override
    public Dimension getMinimumLayoutSize() {
        return new Dimension(250, 250);
    }
}
