package com.zelix.klassmaster.ui.dialog;

import com.zelix.GuiResources;
import com.zelix.UserPreferences;
import com.zelix.klassmaster.KlassMaster;
import com.zelix.klassmaster.classfile.hierarchy.ZkmClasspath;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.log.MessageReporter;
import com.zelix.klassmaster.ui.component.ArchiveAndFolderFileFilter;
import com.zelix.klassmaster.ui.component.BorderedPanel;
import com.zelix.klassmaster.ui.component.ConstraintLayout;
import com.zelix.klassmaster.ui.component.FilePathList;
import com.zelix.klassmaster.ui.component.FileSelector;
import com.zelix.klassmaster.ui.component.SwingUtils;
import com.zelix.klassmaster.ui.component.ZkmFileFilter;
import com.zelix.klassmaster.ui.component.ZkmScrollPane;
import com.zelix.klassmaster.util.DialogCallback;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.SystemEnvironmentConstants;
import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.Container;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ClasspathDialog extends PathListDialog {
    public File lastDirectory = new File(SystemEnvironmentConstants.USER_DIR);
    public ZkmClasspath classpath;
    public DialogCallback dialogCallback;
    public UserPreferences userPreferences;

    @Override
    public void showHelp() {
        GuiResources.showHelpTopic("017");
    }

    public boolean validateClasspath(String string, Enumeration enumeration) throws ZkmException, IOException {
        boolean bl = false;
        ObservableHolder observableHolder = new ObservableHolder();
        if (!ZkmClasspath.validateClasspath(
                string, new File(SystemEnvironmentConstants.USER_DIR), new ObservableHolder(), (MessageReporter) null, observableHolder
        )) {
            SwingUtils.createMessageDialog(this, "Invalid classpath", "Invalid classpath", (String) observableHolder.getValue());
        } else {
            StringBuilder stringBuilder = new StringBuilder();

            while (enumeration.hasMoreElements()) {
                File file1 = (File) enumeration.nextElement();
                stringBuilder.append(file1.getAbsolutePath());
            }

            String string1 = this.classpath.getClasspath();
            this.classpath.setClasspath(stringBuilder.toString());
            if (!this.classpath.locateRuntimeClasses(new ObservableHolder(), new ObservableHolder())) {
                this.classpath.setClasspath(string1);
                this.classpath.locateRuntimeClasses(new ObservableHolder(), new ObservableHolder());
                SwingUtils.createMessageDialog(
                        this,
                        "Incomplete classpath",
                        "Incomplete classpath",
                        "No path to java.lang.Object. (Note that current 'java.home' is '" + SystemEnvironmentConstants.JAVA_HOME + "')"
                );
            }

            bl = true;
        }

        return bl;
    }

    @Override
    public final void acceptPaths() throws ZkmException, IOException {
        int size = super.pathListModel.getSize();
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < size; i++) {
            File file1 = (File) super.pathListModel.getElementAt(i);
            if (!file1.isAbsolute()) {
                stringBuilder.append(file1.getPath().trim());
            } else {
                stringBuilder.append(file1.getAbsolutePath().trim());
            }

            if (i < size - 1) {
                stringBuilder.append(SystemEnvironmentConstants.PATH_SEPARATOR_String);
            }
        }

        String string = stringBuilder.toString();
        String string1 = this.classpath.getClasspath();
        if (string1.equals(string) || this.validateClasspath(string, super.pathListModel.elements())) {
            if (!string1.equals(string)) {
                this.saveClasspathPreference(string);
                this.classpath.setClasspath(string);
                this.classpath.locateRuntimeClasses(new ObservableHolder(), new ObservableHolder());
                super.closedByButton = true;
            } else {
                super.closedByButton = true;
            }

            this.closeFrame();
            this.dialogCallback.onDialogResult_v(this.classpath, 1);
        }
    }

    @Override
    public void browseToAddPath() {
        ZkmFileFilter[] zkmFileFilters = new ZkmFileFilter[]{new ArchiveAndFolderFileFilter()};
        String string = this.lastDirectory.getAbsolutePath();
        FileSelector fileSelector1 = new FileSelector(this.lastDirectory, true, 3, zkmFileFilters, false);
        if (fileSelector1.showOpenDialog(this, "Classpath") == 1) {
            if (!string.equals(fileSelector1.getCurrentDirectory().getAbsolutePath())) {
                this.lastDirectory = fileSelector1.getCurrentDirectory();
            }

            File[] files = fileSelector1.getSelectedFiles();

            for (int i = 0; i < files.length; i++) {
                File file1 = files[i];
                String string1 = file1.getAbsolutePath();
                if (!file1.exists()) {
                    new MessageBoxDialog(this, "File Error", "'" + string1 + "' does not exist");
                    break;
                }

                if (this.addPath(string1)) {
                    this.updateButtonStates();
                }
            }
        }
    }

    public void saveClasspathPreference(String string) {
        this.userPreferences.setClasspath(string);
        this.userPreferences.savePreferences();
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
        super.addBtn.setToolTipText(GuiResources.getTooltipText("BROWSE_CLASSPATH"));
        borderedPanel.add(super.addBtn, "browseBtn");
        super.removeBtn = new JButton("Remove");
        super.removeBtn.setToolTipText(GuiResources.getTooltipText("REMOVE_FROM_CLASSPATH"));
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
    public void closeFrame() throws ZkmException, IOException {
        super.closeFrame();
        if (!super.closedByButton) {
            this.dialogCallback.onDialogCancelled();
        }
    }

    @Override
    public void cancelDialog() throws ZkmException, IOException {
        super.closedByButton = true;
        this.closeFrame();
        this.dialogCallback.onDialogCancelled();
    }

    public ClasspathDialog(
            JFrame jFrame,
            String string,
            ZkmClasspath zkmClasspath,
            String string1,
            String string2,
            String string3,
            UserPreferences userPreferences1,
            DialogCallback dialogCallback1
    ) {
        super(jFrame, string, zkmClasspath.getUserClasspathEntries(), string1, string2, string3);
        this.classpath = zkmClasspath;
        this.dialogCallback = dialogCallback1;
        this.userPreferences = userPreferences1;
        this.showCenteredOnOwner();
        SwingUtils.requestFocusOnEdt(super.okBtn);
    }
}
