package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.KlassMaster;
import com.zelix.klassmaster.classfile.hierarchy.ZkmClasspath;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.component.AllFilesFilter;
import com.zelix.klassmaster.ui.component.ArchiveAndFolderFileFilter;
import com.zelix.klassmaster.ui.component.BorderedPanel;
import com.zelix.klassmaster.ui.component.ConstraintLayout;
import com.zelix.klassmaster.ui.component.FileSelector;
import com.zelix.klassmaster.ui.component.MultiLineLabelCanvas;
import com.zelix.klassmaster.ui.component.SwingUtils;
import com.zelix.klassmaster.ui.component.ZkmFileFilter;
import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;

public class FileSelectorDialog extends EscapeClosingDialogBase implements ActionListener, KeyListener, PropertyChangeListener {
    public static String[] layoutSpec = new String[]{
            "layout.minWidth=300",
            "layout.minHeight=300",
            "etchedPanel.top=5",
            "etchedPanel.left=10",
            "etchedPanel.right=container.right-10",
            "fileSelector.top=etchedPanel.bottom",
            "fileSelector.left=10",
            "fileSelector.right=container.right-btnWidth-30",
            "fileSelector.bottom=container.bottom-5",
            "btnWidth=max(okBtn.defaultWidth, cancelBtn.defaultWidth)",
            "okBtn.centerY=container.height-etchedPanel.bottom*25/100+etchedPanel.bottom",
            "okBtn.left=fileSelector.right+15",
            "okBtn.width=btnWidth",
            "cancelBtn.centerY=container.height-etchedPanel.bottom*75/100+etchedPanel.bottom",
            "cancelBtn.left=fileSelector.right+15",
            "cancelBtn.width=btnWidth"
    };
    public FileSelector fileSelector;
    public JButton cancelBtn;
    public JButton okBtn;
    public String selectedPath;

    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }

    @Override
    public void closeDialog() throws ZkmException, IOException {
        super.closeDialog();
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if (propertyChangeEvent.getPropertyName().equals("selectionChanged")) {
            boolean bl = false;
            File[] files = (File[]) propertyChangeEvent.getNewValue();

            for (int i = 0; i < files.length; i++) {
                if (ZkmClasspath.isRuntimeArchivePath(files[i].getAbsolutePath())) {
                    bl = true;
                    break;
                }
            }

            if (bl) {
                this.okBtn.setEnabled(true);
            } else {
                this.okBtn.setEnabled(false);
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        try {
            if (keyEvent.getKeyCode() == 10) {
                if (keyEvent.getSource() == this.okBtn || keyEvent.getSource() == this.fileSelector) {
                    this.onOk();
                } else if (keyEvent.getSource() == this.cancelBtn) {
                    this.onCancel();
                }
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            Object object = actionEvent.getSource();
            if (object instanceof JButton) {
                JButton jButton = (JButton) object;
                if (jButton == this.okBtn) {
                    this.onOk();
                }

                if (jButton == this.cancelBtn) {
                    this.onCancel();
                }
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }

    public void buildLayout() {
        Container container1 = this.getContentPane();
        ConstraintLayout constraintLayout1 = new ConstraintLayout(container1);
        container1.setLayout(constraintLayout1);
        MultiLineLabelCanvas multiLineLabelCanvas = new MultiLineLabelCanvas(KlassMaster.getDefaultFont());
        BorderedPanel borderedPanel = new BorderedPanel(5, 5);
        borderedPanel.setLayout(new BorderLayout());
        borderedPanel.add(multiLineLabelCanvas, "Center");
        container1.add(borderedPanel, "etchedPanel");
        File file1 = null;
        if (this.selectedPath != null) {
            if (this.selectedPath.indexOf(File.pathSeparator) > -1) {
                this.selectedPath = this.selectedPath.substring(0, this.selectedPath.indexOf(File.pathSeparatorChar));
            }

            File file2 = new File(this.selectedPath);
            if (file2.exists()) {
                if (file2.isDirectory()) {
                    file1 = file2;
                } else {
                    file1 = file2.getParentFile();
                }
            }
        }

        ZkmFileFilter[] zkmFileFilters = new ZkmFileFilter[]{new ArchiveAndFolderFileFilter(), new AllFilesFilter()};
        this.fileSelector = new FileSelector(file1, true, 3, zkmFileFilters, false);
        container1.add(this.fileSelector, "fileSelector");
        this.fileSelector.addKeyListener(this);
        this.fileSelector.addPropertyChangeListener(this);
        this.okBtn = new JButton("OK");
        container1.add(this.okBtn, "okBtn");
        this.cancelBtn = new JButton("Cancel");
        container1.add(this.cancelBtn, "cancelBtn");
        this.okBtn.addActionListener(this);
        this.cancelBtn.addActionListener(this);
        this.okBtn.addKeyListener(this);
        this.cancelBtn.addKeyListener(this);
        constraintLayout1.setConstraints(layoutSpec);
        this.okBtn.setEnabled(false);
        SwingUtils.packOnEdt(this);
        this.setResizable(true);
    }

    private FileSelectorDialog(JFrame jFrame, String string, String string1) {
        super(jFrame, string, true);
        this.selectedPath = string1;
        this.buildLayout();
        this.showCenteredOnOwner();
    }

    public void onOk() throws ZkmException, IOException {
        File[] files = this.fileSelector.getSelectedFiles();
        StringBuffer stringBuffer = new StringBuffer();

        for (int i = 0; i < files.length; i++) {
            stringBuffer.append(files[i].getAbsolutePath());
            if (i < files.length - 1) {
                stringBuffer.append(File.pathSeparator);
            }
        }

        this.selectedPath = stringBuffer.toString();
        this.closeDialog();
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
    }

    public static String selectBootstrapArchive(JFrame jFrame, String string, String string1) {
        return (new FileSelectorDialog(jFrame, string, string1)).selectedPath;
    }

    public void onCancel() throws ZkmException, IOException {
        this.closeDialog();
    }
}
