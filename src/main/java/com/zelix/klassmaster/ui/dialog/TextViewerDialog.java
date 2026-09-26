package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.component.ConstraintLayout;
import com.zelix.klassmaster.ui.component.ZkmScrollPane;
import com.zelix.klassmaster.util.DialogCallback;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

public class TextViewerDialog extends AbstractTextViewerDialog {
    public static Vector openViewers = new Vector();
    public static String[] layoutConstraints = new String[]{
            "msgLbl.top=5",
            "msgLbl.left=container.width*5/100",
            "viewArea.top=msgLbl.bottom+5",
            "viewArea.left=container.width*5/100",
            "viewArea.right=container.width*95/100",
            "viewArea.bottom=container.bottom-okBtn.defaultHeight-10",
            "okBtn.top=viewArea.bottom+5",
            "okBtn.centerX=container.width*50/100",
            "layout.minWidth=250",
            "layout.minHeight=200"
    };
    public JButton okBtn;
    public DialogCallback closeCallback;

    public TextViewerDialog(JFrame jFrame, String string1, String string2, String string, boolean bl, boolean bl1, boolean bl2) {
        this(jFrame, "Test trim report", "Test Trim Report...", new BufferedReader(new StringReader(string)));
    }

    @Override
    public void buildLayout() {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        Container container1 = this.getContentPane();
        super.constraintLayout = new ConstraintLayout(container1);
        container1.setLayout(super.constraintLayout);
        super.messageLabel = new JLabel();
        container1.add(super.messageLabel, "msgLbl");
        super.textArea = new JTextArea();
        container1.add(new ZkmScrollPane(super.textArea), "viewArea");
        this.okBtn = new JButton("OK");
        container1.add(this.okBtn, "okBtn");
        TextViewerWindowListener textViewerWindowListener = new TextViewerWindowListener(this);
        this.addWindowListener(textViewerWindowListener);
        TextReportDialogKeyListener textReportDialogKeyListener = new TextReportDialogKeyListener(this);
        this.okBtn.addKeyListener(textReportDialogKeyListener);
        super.textArea.addKeyListener(textReportDialogKeyListener);
        MessageViewOkListener messageViewOkListener = new MessageViewOkListener(this);
        this.okBtn.addActionListener(messageViewOkListener);
        super.constraintLayout.setConstraints(layoutConstraints);
        int ba = dimension.width;
        int bc = Math.min(500, ba);
        int bb = dimension.height;
        this.setSize(bc, Math.min(400, bb));
    }

    public TextViewerDialog(JFrame jFrame, String string, String string1, BufferedReader bufferedReader, boolean bl, boolean bl1) {
        this(jFrame, string, string1, bufferedReader);
    }

    public TextViewerDialog(
            JFrame jFrame, String string, String string1, BufferedReader bufferedReader, boolean bl, boolean bl1, boolean bl2, DialogCallback dialogCallback1
    ) {
        super(jFrame, string, string1, bufferedReader, bl, bl1);
        openViewers.addElement(this);
        int ba = openViewers.size() * 10;
        this.closeCallback = null;
        this.showCenteredWithOffset(ba, ba, true);
    }

    public TextViewerDialog(JFrame jFrame, String string) {
        this(jFrame, "Error", "Error while parsing the whole exclude parameter", new BufferedReader(new StringReader(string)), false, true);
    }

    public TextViewerDialog(JFrame jFrame, String string, String string1, BufferedReader bufferedReader) {
        this(jFrame, string, string1, bufferedReader, false, true, true, null);
    }

    @Override
    public final void closeDialog() throws ZkmException, IOException {
        openViewers.removeElement(this);
        super.closeDialog();
        if (this.closeCallback != null) {
            this.closeCallback.onDialogCancelled();
        }
    }
}
