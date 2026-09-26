package com.zelix.klassmaster.ui.dialog;

import com.zelix.GuiResources;
import com.zelix.klassmaster.ui.component.ConstraintLayout;
import com.zelix.klassmaster.ui.component.SwingUtils;
import com.zelix.klassmaster.ui.component.ZkmScrollPane;
import com.zelix.klassmaster.util.ZkmStringUtils;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class CopyableMessageDialog extends EscapeClosableDialog {
    public static final String LINE_SEPARATOR = SwingUtils.LINE_SEPARATOR;
    public static String[] layoutSpec = new String[]{
            "msgLbl.top=5",
            "msgLbl.left=5",
            "msgPane.left=5",
            "msgPane.right=container.right-5",
            "msgPane.top=msgLbl.bottom+5",
            "msgPane.bottom=container.bottom-okBtn.defaultHeight-10",
            "btnWidth=max(okBtn.defaultWidth, copyBtn.defaultWidth)",
            "btnHeight=max(okBtn.defaultHeight, copyBtn.defaultHeight)",
            "okBtn.right=container.width*50/100-10",
            "okBtn.bottom=container.bottom-5",
            "okBtn.width=btnWidth",
            "okBtn.height=btnHeight",
            "copyBtn.left=container.width*50/100+10",
            "copyBtn.bottom=okBtn.bottom",
            "copyBtn.width=btnWidth",
            "copyBtn.height=btnHeight"
    };
    public JLabel msgLbl;
    public JEditorPane messagePane;
    public JButton okBtn;
    public JButton copyBtn;

    public static void showMessage(JFrame jFrame, String string, String string1, String string2) {
        ShowMessageDialogTask showMessageDialogTask = new ShowMessageDialogTask(jFrame, string, string1, string2);
        if (!SwingUtilities.isEventDispatchThread()) {
            try {
                SwingUtilities.invokeAndWait(showMessageDialogTask);
            } catch (InterruptedException interruptedException) {
            } catch (InvocationTargetException invocationTargetException) {
            }
        } else {
            new CopyableMessageDialog(jFrame, string, string1, string2);
        }
    }

    public void copyMessageToClipboard() {
        this.messagePane.selectAll();
        this.messagePane.copy();
    }

    public CopyableMessageDialog(JFrame jFrame, String string, String string1, String string2) {
        super(jFrame, string);
        if (jFrame.isVisible()) {
            Container container1 = this.getContentPane();
            ConstraintLayout constraintLayout1 = new ConstraintLayout(container1);
            container1.setLayout(constraintLayout1);
            this.msgLbl = new JLabel(string1);
            string2 = ZkmStringUtils.replaceAll(string2, "<", "&LT;");
            string2 = ZkmStringUtils.replaceAll(string2, ">", "&GT;");
            string2 = ZkmStringUtils.replaceAll(string2, LINE_SEPARATOR, "<br>");
            this.messagePane = new JEditorPane();
            this.messagePane.setEditable(false);
            SwingUtils.setHtmlTextOnEdt(this.messagePane, string2);
            container1.add(this.msgLbl, "msgLbl");
            container1.add(new ZkmScrollPane(this.messagePane), "msgPane");
            this.okBtn = new JButton("OK");
            container1.add(this.okBtn, "okBtn");
            this.copyBtn = new JButton("Copy");
            container1.add(this.copyBtn, "copyBtn");
            this.copyBtn.setToolTipText(GuiResources.getTooltipText("COPY_TEXT"));
            constraintLayout1.setConstraints(layoutSpec);
            this.setSize(400, 300);
            CopyableMessageWindowListener copyableMessageWindowListener = new CopyableMessageWindowListener(this);
            this.addWindowListener(copyableMessageWindowListener);
            MessageDialogKeyListener messageDialogKeyListener = new MessageDialogKeyListener(this);
            this.okBtn.addKeyListener(messageDialogKeyListener);
            this.copyBtn.addKeyListener(messageDialogKeyListener);
            CopyableMessageButtonListener copyableMessageButtonListener = new CopyableMessageButtonListener(this);
            this.okBtn.addActionListener(copyableMessageButtonListener);
            this.copyBtn.addActionListener(copyableMessageButtonListener);
            Dimension dimension = this.getSize();
            Point point = jFrame.getLocationOnScreen();
            Dimension dimension1 = jFrame.getSize();
            int ba = dimension1.width / 2 - dimension.width / 2 + point.x;
            int bb = dimension1.height / 2 - dimension.height / 2 + point.y;
            ba = Math.max(0, ba);
            bb = Math.max(0, bb);
            this.setLocation(ba, bb);
            SwingUtils.setVisibleOnEdt(this);
        }
    }

    @Override
    public void closeDialog() {
        this.setVisible(false);
        this.dispose();
    }
}
