package com.zelix.klassmaster.ui.dialog;

import com.zelix.GuiResources;
import com.zelix.UserPreferences;
import com.zelix.klassmaster.KlassMaster;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.component.AllFilesFilter;
import com.zelix.klassmaster.ui.component.BorderedPanel;
import com.zelix.klassmaster.ui.component.ConstraintLayout;
import com.zelix.klassmaster.ui.component.FilePathList;
import com.zelix.klassmaster.ui.component.FileSelector;
import com.zelix.klassmaster.ui.component.SwingUtils;
import com.zelix.klassmaster.ui.component.TextFileFilter;
import com.zelix.klassmaster.ui.component.ZkmFileFilter;
import com.zelix.klassmaster.ui.component.ZkmScrollPane;
import com.zelix.klassmaster.util.DialogCallback;
import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.Container;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ChangeLogListDialog extends PathListDialog {
    public List initialPaths;
    public String initialPathsText;
    public DialogCallback dialogCallback;
    public UserPreferences userPreferences;

    public ChangeLogListDialog(JFrame jFrame, List list1, String string, String string1, UserPreferences userPreferences1, DialogCallback dialogCallback1) {
        super(jFrame, "Change logs", list1, string1, "OK", "OK");
        this.initialPaths = list1;
        this.initialPathsText = string;
        this.dialogCallback = dialogCallback1;
        this.userPreferences = userPreferences1;
        this.showCenteredOnOwner();
        SwingUtils.requestFocusOnEdt(super.okBtn);
    }

    @Override
    public void browseToAddPath() {
        File file1 = null;
        if (this.userPreferences.getChangeLogDirectory() != null) {
            File file2 = new File(this.userPreferences.getChangeLogDirectory());
            if (file2.exists() && file2.isDirectory()) {
                file1 = file2;
            }
        }

        ZkmFileFilter[] zkmFileFilters = new ZkmFileFilter[]{new TextFileFilter(), new AllFilesFilter()};
        FileSelector fileSelector1 = new FileSelector(file1, true, 1, zkmFileFilters, false);
        if (fileSelector1.showOpenDialog(this, "Select Change Log") == 1) {
            File[] files = fileSelector1.getSelectedFiles();

            for (int i = 0; i < files.length; i++) {
                File file3 = files[i];
                String string = file3.getAbsolutePath();
                if (!file3.exists()) {
                    new MessageBoxDialog(this, "File Error", "'" + string + "' does not exist");
                    break;
                }

                String string2;
                if (this.addPath(string)) {
                    this.updateButtonStates();
                    string2 = file3.getParent();
                } else {
                    string2 = file3.getParent();
                }

                String string1 = string2;
                if (string1 != null) {
                    this.userPreferences.setChangeLogDirectory(string1);
                    this.userPreferences.savePreferences();
                }
            }
        }
    }

    @Override
    public void showHelp() {
        GuiResources.showHelpTopic("039");
    }

    @Override
    public void cancelDialog() throws ZkmException, IOException {
        super.closedByButton = true;
        this.closeFrame();
        this.dialogCallback.onDialogCancelled();
    }

    public boolean shouldApplyChanges() {
        return true;
    }

    @Override
    public void closeFrame() throws ZkmException, IOException {
        super.closeFrame();
        if (!super.closedByButton) {
            this.dialogCallback.onDialogCancelled();
        }
    }

    @Override
    public void buildContents(Object object, Object object1, Object object2, Object object3, Object object4, Object object5) {
        super.pathMap = ZkmUtils.createHashMap();
        List list1 = (List) object;
        String string = (String) object1;
        String string1 = (String) object2;
        String string2 = (String) object3;
        Container container1 = this.getContentPane();
        ConstraintLayout constraintLayout1 = new ConstraintLayout(container1);
        container1.setLayout(constraintLayout1);
        BorderedPanel borderedPanel = new BorderedPanel(5, 5);
        ConstraintLayout constraintLayout2 = new ConstraintLayout(borderedPanel);
        borderedPanel.setLayout(constraintLayout2);
        super.messageLabel = new JLabel(string);
        borderedPanel.add(super.messageLabel, "msgLbl");
        super.pathList = new FilePathList(new DefaultListModel());
        super.pathListModel = (DefaultListModel) super.pathList.getModel();
        if (list1 != null) {
            Iterator iterator = list1.iterator();

            while (iterator.hasNext()) {
                String string3 = (String) iterator.next();
                this.addPath(string3);
            }
        }

        borderedPanel.add(new ZkmScrollPane(super.pathList), "pathLst");
        super.pathList.setSelectionMode(2);
        super.addBtn = new JButton("Add");
        super.addBtn.setToolTipText(GuiResources.getTooltipText("ADD_CHANGELOG"));
        borderedPanel.add(super.addBtn, "browseBtn");
        super.removeBtn = new JButton("Remove");
        super.removeBtn.setToolTipText(GuiResources.getTooltipText("REMOVE_CHANGELOG"));
        borderedPanel.add(super.removeBtn, "removeBtn");
        super.moveUpBtn = new JButton("Move Up");
        super.moveUpBtn.setToolTipText(GuiResources.getTooltipText("MOVE_UP_IN_LIST"));
        borderedPanel.add(super.moveUpBtn, "upBtn");
        super.moveDownBtn = new JButton("Move Down");
        super.moveDownBtn.setToolTipText(GuiResources.getTooltipText("MOVE_DOWN_IN_LIST"));
        borderedPanel.add(super.moveDownBtn, "downBtn");
        constraintLayout2.setConstraints(PathListDialog.LAYOUT_CONSTRAINTS);
        container1.add(borderedPanel, "listPnl");
        super.pathList.addListSelectionListener(this);
        super.pathList.addKeyListener(this);
        super.removeBtn.addActionListener(this);
        super.moveUpBtn.addActionListener(this);
        super.moveDownBtn.addActionListener(this);
        super.addBtn.addActionListener(this);
        super.removeBtn.addKeyListener(this);
        super.moveUpBtn.addKeyListener(this);
        super.moveDownBtn.addKeyListener(this);
        super.addBtn.addKeyListener(this);
        this.updateButtonStates();
        StringBuffer stringBuffer = new StringBuffer(
                "layout.minWidth=btnWidth*6+25;layout.minHeight=250;listPnl.top=5;listPnl.left=5;listPnl.right=container.right-5;listPnl.bottom=container.bottom-btnHeight-20;"
        );
        this.createMainButtons(string1, string2, stringBuffer, container1);
        constraintLayout1.parseConstraints(stringBuffer.toString());
        this.setIconImage(KlassMaster.getLogoImage(this));
        SwingUtils.packOnEdt(this);
    }

    @Override
    public final void acceptPaths() throws ZkmException, IOException {
        int size = super.pathListModel.getSize();
        ArrayList arrayList = new ArrayList(size);
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < size; i++) {
            File file1 = (File) super.pathListModel.getElementAt(i);
            if (!file1.isAbsolute()) {
                String string = file1.getPath().trim();
                arrayList.add(string);
                stringBuilder.append(string);
            } else {
                String string3 = file1.getAbsolutePath().trim();
                arrayList.add(string3);
                stringBuilder.append(string3);
            }

            if (i < size - 1) {
                stringBuilder.append(HiddenOptionFlags.PATH_SEPARATOR);
            }
        }

        String string2 = stringBuilder.toString();
        if (!this.initialPathsText.equals(string2)) {
            super.pathListModel.elements();
            if (!this.shouldApplyChanges()) {
                return;
            }

            super.closedByButton = true;
        } else {
            super.closedByButton = true;
        }

        this.closeFrame();
        String string1 = string2;
        ArrayList arrayList1 = arrayList;
        this.dialogCallback.onDialogResult(arrayList1, string1);
    }
}
