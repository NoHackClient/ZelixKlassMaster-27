package com.zelix.klassmaster.ui.dialog;

import com.zelix.GuiResources;
import com.zelix.klassmaster.config.ExclusionWizardSettings;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.rename.RootPackageNode;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.ui.ZkmMainWindow;
import com.zelix.klassmaster.ui.component.ClassListEntry;
import com.zelix.klassmaster.ui.component.RenameFilterModel;
import com.zelix.klassmaster.ui.component.SwingUtils;
import com.zelix.klassmaster.util.CaseInsensitiveComparator;
import com.zelix.klassmaster.util.DialogCallback;
import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.Container;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;

public abstract class PackageExclusionWizardDialog extends ExclusionWizardDialogBase {
    public static String[] paramListConstraints = new String[]{
            "mainParamList.top=5",
            "mainParamList.left=5",
            "mainParamList.right=container.right-btnWidth-10",
            "mainParamList.bottom=container.height*50/100",
            "explanationArea.left=5",
            "explanationArea.top=mainParamList.bottom+5",
            "explanationArea.right=container.right-5",
            "explanationArea.bottom=container.bottom-5",
            "btnWidth=max(mainAddBtn.defaultWidth, mainModifyBtn.defaultWidth, mainDeleteBtn.defaultWidth)",
            "mainAddBtn.width=btnWidth",
            "mainAddBtn.left=mainParamList.right+5",
            "mainAddBtn.centerY=container.height*20/200",
            "mainModifyBtn.width=btnWidth",
            "mainModifyBtn.left=mainAddBtn.left",
            "mainModifyBtn.centerY=container.height*50/200",
            "mainDeleteBtn.width=btnWidth",
            "mainDeleteBtn.left=mainAddBtn.left",
            "mainDeleteBtn.centerY=container.height*80/200"
    };
    public static String[] optionsConstraints = new String[]{
            "applicationTypeLbl.centerY=applicationTypeComboBox.centerY",
            "applicationTypeLbl.left=5",
            "startSecondColumn=max(applicationTypeLbl.defaultWidth, excludePackageNamesLbl.defaultWidth, excludeMainClassLbl.defaultWidth)+10",
            "applicationTypeComboBox.top=5",
            "applicationTypeComboBox.left=startSecondColumn",
            "applicationTypeComboBox.right=container.right-5",
            "excludePackageNamesLbl.left=5",
            "excludePackageNamesLbl.centerY=excludedPackageComboBox.centerY",
            "excludedPackageComboBox.left=startSecondColumn",
            "excludedPackageComboBox.right=container.right-5",
            "excludedPackageComboBox.top=applicationTypeComboBox.bottom+5",
            "excludeMainClassLbl.left=5",
            "excludeMainClassLbl.centerY=mainClassComboBox.centerY",
            "mainClassComboBox.left=startSecondColumn",
            "mainClassComboBox.top=excludedPackageComboBox.bottom+5",
            "mainClassComboBox.right=container.right-5",
            "container.defaultRightPad=5",
            "container.defaultBottomPad=5"
    };
    public JComboBox excludedPackageComboBox;
    public boolean suppressComboEvents;
    public DefaultComboBoxModel packageComboModel;
    public RootPackageNode rootPackage;

    @Override
    public void openParameterEditor(String string, String string1, DialogCallback dialogCallback1) throws ZkmException, IOException {
        String string2 = string1;
        if (string.equals("1")) {
            if (string2 == null) {
                string2 = "*.";
            }

            new PackageExcludeDialog(this, this.parseParameter(string2), dialogCallback1);
        } else if (string.equals("2")) {
            if (string2 == null) {
                string2 = "*.*";
            }

            new ClassParameterPartDialog(this, "Zelix KlassMaster Class Exclude Parameter", this.parseParameter(string2), 1, dialogCallback1);
        } else if (string.equals("3")) {
            if (string2 == null) {
                string2 = "*.* *";
            }

            new FieldExcludeParameterDialog(this, "Zelix KlassMaster Field Exclude Parameter", this.parseParameter(string2), 1, dialogCallback1);
        } else if (string.equals("4")) {
            if (string2 == null) {
                string2 = "*.* *(*)";
            }

            new MethodSpecifierDialog(this, "Zelix KlassMaster Method Exclude Parameter", this.parseParameter(string2), 1, dialogCallback1);
        } else if (string.equals("5")) {
            if (string2 == null) {
                string2 = "*.<link>X";
            }

            new ClassSuffixExcludeDialog(this, this.parseParameter(string2), dialogCallback1);
        }
    }

    @Override
    public final void saveParameters() {
        super.saveParameters();
        switch (this.excludedPackageComboBox.getSelectedIndex()) {
            case -1:
            case 0:
                ExclusionWizardSettings exclusionWizardSettings = (ExclusionWizardSettings) super.wizardSettings;
                Object object = null;
                exclusionWizardSettings.setPackageSelection(false, (String) object);
                break;
            case 1:
                ((ExclusionWizardSettings) super.wizardSettings).setPackageSelection(true, "*.");
                break;
            default:
                ExclusionWizardSettings exclusionWizardSettings1 = (ExclusionWizardSettings) super.wizardSettings;
                String string = (String) this.excludedPackageComboBox.getSelectedItem() + ".";
                exclusionWizardSettings1.setPackageSelection(true, string);
        }
    }

    @Override
    public void buildExtraComponents(Container container1) throws ZkmException, IOException {
        JLabel jLabel = new JLabel("Exclude package qualifiers:");
        container1.add(jLabel, "excludePackageNamesLbl");
        this.packageComboModel = new DefaultComboBoxModel();
        this.excludedPackageComboBox = new JComboBox(this.packageComboModel);
        container1.add(this.excludedPackageComboBox, "excludedPackageComboBox");
        this.excludedPackageComboBox.addItem("<None>");
        this.excludedPackageComboBox.addItem("<All>");
        PackageExclusionWizardDialog packageExclusionWizardDialog1;
        Container container2;
        String string5;
        if (this.rootPackage != null) {
            List list1 = this.rootPackage.getAllPackagePaths();
            Collections.sort(list1, CaseInsensitiveComparator.getInstance());

            for (int i = 0; i < list1.size(); i++) {
                String string = ((String) list1.get(i)).replace('/', '.');
                this.excludedPackageComboBox.addItem(string);
            }

            if (((ExclusionWizardSettings) super.wizardSettings).isPackageSelected() && super.wizardSettings.isSaved()) {
                String string3 = ((ExclusionWizardSettings) super.wizardSettings).getSelectedPackageName();
                if (string3.equals("*.")) {
                    this.excludedPackageComboBox.setSelectedIndex(1);
                } else {
                    String string4 = string3.substring(0, string3.length() - 1);
                    int bb = this.packageComboModel.getIndexOf(string4);
                    if (bb > -1) {
                        this.excludedPackageComboBox.setSelectedIndex(bb);
                    } else {
                        this.removeParameter(string3);
                        this.excludedPackageComboBox.setSelectedIndex(0);
                    }
                }
            } else {
                this.excludedPackageComboBox.setSelectedIndex(0);
            }

            packageExclusionWizardDialog1 = this;
            container2 = container1;
            long bd = 88845662142062L;
            string5 = "Don't change main class name:";
        } else {
            this.excludedPackageComboBox.setEnabled(false);
            packageExclusionWizardDialog1 = this;
            container2 = container1;
            long bc = 88845662142062L;
            string5 = "Don't change main class name:";
        }

        String string2 = GuiResources.getTooltipText("EXCLUDE_MAIN_CLASS_NAME_LIST");
        String string1 = string5;
        packageExclusionWizardDialog1.buildMainClassSelector(container2, string1, string2);
        this.excludedPackageComboBox.addItemListener(this);
        this.excludedPackageComboBox.setToolTipText(GuiResources.getTooltipText("EXCLUDE_PACKAGE_NAMES_LIST"));
    }

    @Override
    public String[] getExtraPanelLayoutSpec() {
        return optionsConstraints;
    }

    @Override
    public final void showHelp() {
        GuiResources.showHelpTopic("025");
    }

    public PackageExclusionWizardDialog(
            String string,
            ZkmMainWindow zkmMainWindow,
            ExclusionWizardSettings exclusionWizardSettings,
            RootPackageNode rootPackageNode1,
            List list1,
            ScriptEnvironment scriptEnvironment1,
            DialogCallback dialogCallback1
    ) throws ZkmException, IOException {
        super(zkmMainWindow, list1, exclusionWizardSettings, scriptEnvironment1, dialogCallback1, 1);
        this.rootPackage = rootPackageNode1;
        this.initComponents(string);
        SwingUtils.requestFocusOnEdt(super.okBtn);
    }

    @Override
    public void onParametersChanged() {
        super.onParametersChanged();
        this.resetPackageComboIfRemoved();
    }

    @Override
    public String[] getParamListConstraints() {
        return paramListConstraints;
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        try {
            if (itemEvent.getSource() == this.excludedPackageComboBox) {
                if (this.suppressComboEvents) {
                    return;
                }

                int ba = this.packageComboModel.getIndexOf(itemEvent.getItem());
                switch (ba) {
                    case -1:
                    case 0:
                        break;
                    case 1:
                        if (itemEvent.getStateChange() == 1) {
                            this.addParameter("*.");
                        } else {
                            this.suppressComboEvents = true;
                            this.removeParameter("*.");
                            this.suppressComboEvents = false;
                        }
                        break;
                    default:
                        String string = (String) this.packageComboModel.getElementAt(ba) + ".";
                        if (itemEvent.getStateChange() == 1) {
                            this.addParameter(string);
                        } else {
                            this.suppressComboEvents = true;
                            this.removeParameter(string);
                            this.suppressComboEvents = false;
                        }
                }
            } else {
                super.itemStateChanged(itemEvent);
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }

    @Override
    public void editParameter(RenameFilterModel renameFilterModel, String string, DialogCallback dialogCallback1) throws ZkmException, IOException {
        switch (renameFilterModel.getParameterKind()) {
            case 1:
                this.openParameterEditor("1", string, dialogCallback1);
                break;
            case 2:
                if (renameFilterModel.isLinkedClass()) {
                    this.openParameterEditor("5", string, dialogCallback1);
                } else {
                    this.openParameterEditor("2", string, dialogCallback1);
                }
                break;
            case 3:
                this.openParameterEditor("3", string, dialogCallback1);
                break;
            case 4:
                this.openParameterEditor("4", string, dialogCallback1);
        }
    }

    @Override
    public void openParameterTypeDialog(DialogCallback dialogCallback1) {
        new ExcludeParameterTypeDialog(this, dialogCallback1);
    }

    public void resetPackageComboIfRemoved() {
        if (!this.suppressComboEvents) {
            switch (this.excludedPackageComboBox.getSelectedIndex()) {
                case -1:
                case 0:
                    break;
                case 1:
                    if (!super.paramIndexMap.containsKey("*.")) {
                        this.excludedPackageComboBox.setSelectedIndex(0);
                    }
                    break;
                default:
                    if (!super.paramIndexMap.containsKey((String) this.excludedPackageComboBox.getSelectedItem() + ".")) {
                        this.excludedPackageComboBox.setSelectedIndex(0);
                    }
            }
        }
    }

    @Override
    public String buildMainClassParameter(String string, boolean bl) {
        String string1 = null;
        if (string != null) {
            int ba = string.lastIndexOf(46);
            if (ba != -1) {
                string1 = string.substring(0, ba + 1) + "^" + string.substring(ba + 1);
            } else {
                string1 = string;
            }

            if (bl) {
                string1 = string1 + "^" + " " + ExclusionWizardDialogBase.mainMethodSignature;
            }
        }

        return string1;
    }

    @Override
    public String getMainClassParameter(ClassListEntry classListEntry) {
        if (classListEntry != null) {
            String string = classListEntry.getClassName();
            return this.buildMainClassParameter(string, classListEntry.isApplication());
        } else {
            return null;
        }
    }
}
