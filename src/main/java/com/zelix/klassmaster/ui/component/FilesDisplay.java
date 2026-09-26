package com.zelix.klassmaster.ui.component;

import com.zelix.klassmaster.archive.ZkmFileUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

public class FilesDisplay extends JTextField implements ActionListener, PropertyChangeListener, DocumentListener, FileChooserComponent {
    public static String quote = "\"";
    public File[] selectedFiles;
    public boolean textEdited;
    public File currentDirectory;
    public Document textDocument;
    public int fileSelectionMode;
    public boolean multiSelection;

    public FilesDisplay(File file1, boolean bl, int fileSelectionMode, boolean multiSelection) {
        this.currentDirectory = file1;
        this.textDocument = new PlainDocument();
        this.setDocument(this.textDocument);
        this.textDocument.addDocumentListener(this);
        this.setEditable(bl);
        this.fileSelectionMode = fileSelectionMode;
        this.multiSelection = multiSelection;
        this.addActionListener(this);
        this.setText("");
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if (propertyChangeEvent.getPropertyName().equals("selectionChanged")) {
            File[] files = this.selectedFiles;
            Object[] objects = (Object[]) propertyChangeEvent.getNewValue();
            int ba = objects.length;
            File[] files1 = new File[ba];
            System.arraycopy(objects, 0, files1, 0, ba);
            this.setSelectedFiles(files1);
            this.textEdited = false;
            this.firePropertyChange("selectionChanged", files, files1);
        } else if (propertyChangeEvent.getPropertyName().equals("directoryChanged")) {
            this.currentDirectory = (File) propertyChangeEvent.getNewValue();
        }
    }

    public void onTextEdited() {
        this.textEdited = true;
        this.firePropertyChange("fileNameChanged", null, this.getText().trim());
    }

    @Override
    public void changedUpdate(DocumentEvent documentEvent) {
        this.onTextEdited();
    }

    @Override
    public void insertUpdate(DocumentEvent documentEvent) {
        this.onTextEdited();
    }

    @Override
    public void removeUpdate(DocumentEvent documentEvent) {
        this.onTextEdited();
    }

    public File getSelectedFile() {
        if (this.textEdited) {
            String string = this.getText().trim();
            if (string.length() > 0) {
                File file1;
                if (ZkmFileUtils.isRelativePath(string)) {
                    file1 = new File(this.currentDirectory, string);
                } else {
                    file1 = new File(string);
                }

                return file1;
            } else {
                return null;
            }
        } else {
            return this.selectedFiles != null && this.selectedFiles.length > 0 ? this.selectedFiles[0] : null;
        }
    }

    public void setSelectedFiles(File[] files) {
        if (files != null && files.length != 0) {
            this.selectedFiles = files;
            int ba = files.length;
            ArrayList arrayList = new ArrayList();
            StringBuffer stringBuffer = new StringBuffer();

            for (int i = 0; i < ba; i++) {
                File file1 = files[i];
                StringBuffer stringBuffer1;
                String string;
                if (ba > 1) {
                    stringBuffer.append(quote);
                    stringBuffer1 = stringBuffer;
                    string = file1.getName();
                } else {
                    stringBuffer1 = stringBuffer;
                    string = file1.getName();
                }

                stringBuffer1.append(string);
                if (ba > 1) {
                    stringBuffer.append(quote);
                }

                if (i < ba - 1) {
                    stringBuffer.append(" ");
                }

                arrayList.add(file1);
            }

            this.selectedFiles = ((java.io.File[]) (arrayList.toArray(new File[arrayList.size()])));
            this.setText(stringBuffer.toString());
        } else {
            this.selectedFiles = new File[0];
            this.setText("");
        }
    }

    public File[] getSelectedFiles() {
        if (this.textEdited) {
            String string = this.getText().trim();
            if (string.length() > 0) {
                File[] files = new File[1];
                if (ZkmFileUtils.isRelativePath(string)) {
                    files[0] = new File(this.currentDirectory, string);
                } else {
                    files[0] = new File(string);
                }

                return files;
            } else {
                return null;
            }
        } else {
            return this.selectedFiles;
        }
    }

    public void setSelectedFile(File file1) {
        File[] files = new File[]{file1};
        this.setSelectedFiles(files);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (this.fileSelectionMode == 1 || this.fileSelectionMode == 3) {
            this.firePropertyChange("finalSelection", null, this.getSelectedFile());
        }
    }
}
