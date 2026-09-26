package com.zelix.klassmaster.ui;

import com.zelix.GuiResources;
import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.FieldInfo;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.ui.component.EdtCallback;
import com.zelix.klassmaster.ui.component.EdtCallbackInvoker;
import com.zelix.klassmaster.ui.component.SelectionHolder;
import com.zelix.klassmaster.ui.component.ZkmScrollPane;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.NodeVisitor;
import com.zelix.klassmaster.util.ObservableModel;

import java.io.IOException;
import javax.swing.JTextArea;

public class BrowserTextViewUpdater implements NodeVisitor, EdtCallback {
    public static final String LINE_SEPARATOR = HiddenOptionFlags.LINE_SEPARATOR;
    public static final String TRUNCATED_NOTICE = LINE_SEPARATOR + LINE_SEPARATOR + "Only the first " + 512000 + " bytes shown";
    public SelectionHolder selectionHolder;
    public JTextArea textArea;
    public ZkmScrollPane scrollPane;

    @Override
    public void handleChangeOnEdt(Object object, Object object1, Object object2, Object object3) throws ZkmException, IOException {
        ClassFileComponent classFileComponent = (ClassFileComponent) ((SelectionHolder) object).getSelectedNode();
        if (classFileComponent != null) {
            if (object1 != null && object1 instanceof MutableInt && ((MutableInt) object1).getValue() == 3 && classFileComponent instanceof ProgramClass) {
                return;
            }

            try {
                String string = classFileComponent.formatDeclaration();
                String string1;
                char ba;
                char bb;
                if (string.length() > 512000) {
                    string = string.substring(0, 512000) + TRUNCATED_NOTICE;
                    string1 = string;
                    ba = 0;
                    bb = '?';
                } else {
                    string1 = string;
                    ba = 0;
                    bb = '?';
                }

                string = string1.replace(ba, bb);
                this.textArea.setText(string);
                this.textArea.setCaretPosition(0);
                if (classFileComponent instanceof ProgramClass) {
                    this.textArea.setToolTipText(GuiResources.getTooltipText("BROWSER_VIEW_CLASS"));
                } else if (classFileComponent instanceof FieldInfo) {
                    this.textArea.setToolTipText(GuiResources.getTooltipText("BROWSER_VIEW_FIELD"));
                } else if (classFileComponent instanceof MethodInfo) {
                    this.textArea.setToolTipText(GuiResources.getTooltipText("BROWSER_VIEW_METHOD"));
                } else {
                    this.textArea.setToolTipText("");
                }
            } catch (ZkmProcessingException zkmProcessingException) {
            }
        } else {
            this.textArea.setText("");
        }
    }

    @Override
    public void handleObservedChange(ObservableModel observableModel1, Object object, Object object1, Object object2) throws ZkmException, IOException {
        new EdtCallbackInvoker(this, observableModel1, object, object1, object2);
    }

    public BrowserTextViewUpdater(SelectionHolder selectionHolder1, JTextArea jTextArea, ZkmScrollPane zkmScrollPane) {
        this.selectionHolder = selectionHolder1;
        this.textArea = jTextArea;
        this.scrollPane = zkmScrollPane;
        this.textArea.setEditable(false);
        selectionHolder1.addObserver(this);
    }
}
