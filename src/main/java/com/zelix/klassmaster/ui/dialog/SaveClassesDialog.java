package com.zelix.klassmaster.ui.dialog;

import com.zelix.GuiResources;
import com.zelix.klassmaster.KlassMaster;
import com.zelix.klassmaster.exceptions.ZkmClassNotFoundException;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.ZkmMainWindow;
import com.zelix.klassmaster.ui.component.ConstraintLayout;
import com.zelix.klassmaster.ui.component.FileSelector;
import com.zelix.klassmaster.ui.component.FolderOnlyFileFilter;
import com.zelix.klassmaster.ui.component.SwingUtils;
import com.zelix.klassmaster.ui.component.ZkmFileFilter;
import com.zelix.klassmaster.util.DialogCallback;
import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class SaveClassesDialog extends OwnedFrameDialogBase implements ActionListener, KeyListener, PropertyChangeListener, HelperDialogMarker {
    public static String[] layoutConstraints = new String[]{
            "layout.minWidth=btnWidth*4+25",
            "layout.minHeight=300",
            "fileSelector.left=5",
            "fileSelector.top=0",
            "fileSelector.bottom=container.bottom-btnHeight-15",
            "fileSelector.right=container.right-5",
            "btnWidth=max(okBtn.defaultWidth, cancelBtn.defaultWidth, helpBtn.defaultWidth)",
            "btnHeight=max(okBtn.defaultHeight, cancelBtn.defaultHeight, helpBtn.defaultHeight)",
            "okBtn.centerX=container.width*16/100",
            "okBtn.bottom=container.bottom-5",
            "okBtn.width=btnWidth",
            "cancelBtn.centerX=container.width*50/100",
            "cancelBtn.bottom=container.bottom-5",
            "cancelBtn.width=btnWidth",
            "helpBtn.centerX=container.width*84/100",
            "helpBtn.bottom=container.bottom-5",
            "helpBtn.width=btnWidth"
    };
    public JButton okBtn;
    public JButton cancelBtn;
    public JButton helpBtn;
    public FileSelector fileSelector;
    public boolean closedByButton;
    public DialogCallback callback;
    public String initialDirectory;

    public boolean confirmOverwrite(Object object, Object object1) throws ZkmClassNotFoundException {
        if (((ZkmMainWindow) super.ownerFrame).getClassRepository().getInputRootDirectories().contains(object)) {
            String[] strings = new String[]{"Yes", "No"};
            String string = strings[0];
            String[] strings1 = strings;
            Object object2 = null;
            if (JOptionPane.showOptionDialog(this, object1, "Class overwrite warning", 2, 0, (Icon) object2, strings1, string) == 1) {
                return false;
            }
        }

        return true;
    }

    public SaveClassesDialog(JFrame jFrame, String string, String string1, String string2, String string3, DialogCallback dialogCallback1) {
        super(jFrame, string, string3, string1, string2);
        this.callback = dialogCallback1;
        this.initialDirectory = string3;
        this.showCenteredOnOwner();
        SwingUtils.requestFocusOnEdt(this.okBtn);
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        try {
            if (keyEvent.getKeyCode() == 10) {
                if (keyEvent.getSource() == this.okBtn || keyEvent.getSource() == this.fileSelector) {
                    this.confirmSave();
                } else if (keyEvent.getSource() == this.cancelBtn) {
                    this.cancelDialog();
                } else if (keyEvent.getSource() == this.helpBtn) {
                    this.showHelp();
                }
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    @Override
    public void cancelDialog() throws ZkmException, IOException {
        this.closedByButton = true;
        this.closeFrame();
        this.callback.onDialogCancelled();
    }

    @Override
    public final void closeFrame() throws ZkmException, IOException {
        super.closeFrame();
        if (!this.closedByButton) {
            this.callback.onDialogCancelled();
        }
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }

    @Override
    public void buildContents(Object object, Object object1, Object object2, Object object3, Object object4, Object object5) {
        Container container1 = this.getContentPane();
        ConstraintLayout constraintLayout1 = new ConstraintLayout(container1);
        container1.setLayout(constraintLayout1);
        File file1 = null;
        File file2 = null;
        String string = (String) object;
        String string1 = (String) object1;
        String string2 = (String) object2;
        if (string != null) {
            File file3 = new File(string);
            if (file3.exists() && file3.isDirectory()) {
                file2 = file3;
                file1 = file2.getParentFile();
            }
        }

        ZkmFileFilter[] zkmFileFilters = new ZkmFileFilter[]{new FolderOnlyFileFilter()};
        this.fileSelector = new FileSelector(file1, false, 2, zkmFileFilters, false);
        container1.add(this.fileSelector, "fileSelector");
        this.fileSelector.addKeyListener(this);
        this.fileSelector.addPropertyChangeListener(this);
        this.createButtons(string1, string2, constraintLayout1, container1);
        this.fileSelector.selectFileInList(file2);
        this.setResizable(true);
        this.setIconImage(KlassMaster.getLogoImage(this));
        SwingUtils.packOnEdt(this);
    }

    public final void showHelp() {
        GuiResources.showHelpTopic("019");
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            Object object = actionEvent.getSource();
            if (object instanceof JButton) {
                JButton jButton = (JButton) object;
                if (jButton == this.okBtn) {
                    this.confirmSave();
                } else if (jButton == this.cancelBtn) {
                    this.cancelDialog();
                } else if (jButton == this.helpBtn) {
                    this.showHelp();
                }
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
    }

    public void confirmSave() throws ZkmException, IOException {
        File file1 = this.fileSelector.getSelectedFile();
        if (this.confirmOverwrite(file1, "Some files will be saved to the same folder from which they were opened. Continue?")) {
            this.closedByButton = true;
            this.closeFrame();
            this.callback.onDialogResult_v(file1, 1);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if (propertyChangeEvent.getPropertyName().equals("selectionChanged")) {
            File[] files = (File[]) propertyChangeEvent.getNewValue();
            JButton jButton;
            if (files != null) {
                if (files.length != 0) {
                    this.okBtn.setEnabled(true);
                    return;
                }

                jButton = this.okBtn;
            } else {
                jButton = this.okBtn;
            }

            jButton.setEnabled(false);
        }
    }

    public void createButtons(String string, String string1, ConstraintLayout constraintLayout1, Container container1) {
        this.okBtn = new JButton(string);
        this.okBtn.setToolTipText(string1);
        container1.add(this.okBtn, "okBtn");
        this.okBtn.setEnabled(false);
        this.cancelBtn = new JButton("Cancel");
        this.cancelBtn.setToolTipText(GuiResources.getTooltipText("CANCEL_DIALOG"));
        container1.add(this.cancelBtn, "cancelBtn");
        this.helpBtn = new JButton("Help");
        this.helpBtn.setToolTipText(GuiResources.getTooltipText("SCREEN_HELP"));
        container1.add(this.helpBtn, "helpBtn");
        this.okBtn.addActionListener(this);
        this.cancelBtn.addActionListener(this);
        this.helpBtn.addActionListener(this);
        this.okBtn.addKeyListener(this);
        this.cancelBtn.addKeyListener(this);
        this.helpBtn.addKeyListener(this);
        constraintLayout1.setConstraints(layoutConstraints);
    }
}
