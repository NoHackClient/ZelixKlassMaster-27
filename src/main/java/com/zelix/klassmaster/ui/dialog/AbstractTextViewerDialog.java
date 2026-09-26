package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.ui.component.ConstraintLayout;

import java.io.BufferedReader;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

public abstract class AbstractTextViewerDialog extends EscapeClosingDialogBase {
    public static final String TRUNCATED_NOTICE = EscapeClosingDialogBase.LINE_SEPARATOR
            + EscapeClosingDialogBase.LINE_SEPARATOR
            + "Only the first "
            + 512000
            + " bytes shown";
    public JTextArea textArea;
    public JLabel messageLabel;
    public ConstraintLayout constraintLayout;
    public BufferedReader reader;

    public AbstractTextViewerDialog(JFrame jFrame, String string, String string1, BufferedReader bufferedReader, boolean bl, boolean bl1) {
        super(jFrame, string, bl1);
        this.buildLayout();
        this.messageLabel.setText(string1);
        this.reader = bufferedReader;
        String string2 = null;
        StringBuffer stringBuffer = new StringBuffer();
        this.textArea.setEditable(bl);
        int ba = 0;
        if (bufferedReader != null) {
            try {
                for (string2 = bufferedReader.readLine(); string2 != null && ba < 512000; string2 = bufferedReader.readLine()) {
                    ba += string2.length();
                    stringBuffer.append(string2 + EscapeClosingDialogBase.LINE_SEPARATOR);
                }
            } catch (IOException iOException1) {
            } finally {
                try {
                    bufferedReader.close();
                } catch (IOException iOException) {
                }
            }
        }

        JTextArea jTextArea;
        if (string2 != null) {
            stringBuffer.append(TRUNCATED_NOTICE);
            jTextArea = this.textArea;
        } else {
            jTextArea = this.textArea;
        }

        jTextArea.setText(stringBuffer.toString());
        this.textArea.setCaretPosition(0);
    }

    public abstract void buildLayout();
}
