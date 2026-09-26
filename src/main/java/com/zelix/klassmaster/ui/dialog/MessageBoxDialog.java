package com.zelix.klassmaster.ui.dialog;

import com.zelix.GuiResources;
import com.zelix.klassmaster.ui.component.ConstraintLayout;
import com.zelix.klassmaster.ui.component.SwingUtils;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class MessageBoxDialog extends EscapeClosableDialog implements ClipboardOwner {
    public Frame ownerFrame;
    public JLabel msgLbl;
    public JButton okBtn;
    public JButton copyBtn;
    public FontMetrics fontMetrics;

    public static void showMessage(Frame frame1, String string, String string1, boolean bl) {
        ShowErrorDialogTask showErrorDialogTask = new ShowErrorDialogTask(frame1, string, string1);
        if (bl) {
            if (!SwingUtilities.isEventDispatchThread()) {
                try {
                    SwingUtilities.invokeAndWait(showErrorDialogTask);
                } catch (InterruptedException interruptedException) {
                } catch (InvocationTargetException invocationTargetException) {
                }
            } else {
                new MessageBoxDialog(frame1, string, string1);
            }
        } else {
            SwingUtilities.invokeLater(showErrorDialogTask);
        }
    }

    public MessageBoxDialog(Frame frame1, String string, String string1) {
        super(frame1, string);
        this.ownerFrame = frame1;
        Container container1 = this.getContentPane();
        ConstraintLayout constraintLayout1 = new ConstraintLayout(container1);
        container1.setLayout(constraintLayout1);
        this.msgLbl = new JLabel(string1.trim(), 0);
        container1.add(this.msgLbl, "msgLbl");
        this.okBtn = new JButton("OK");
        container1.add(this.okBtn, "okBtn");
        this.copyBtn = new JButton("Copy");
        container1.add(this.copyBtn, "copyBtn");
        this.copyBtn.setToolTipText(GuiResources.getTooltipText("COPY_TEXT"));
        if (frame1 == null) {
            this.setFont(new Font("Dialog", 0, 12));
        }

        this.fontMetrics = this.msgLbl.getFontMetrics(this.msgLbl.getFont());
        int ba = this.fontMetrics.stringWidth(string1.trim());
        int bb = Math.max(200, ba + 60);
        int height = this.fontMetrics.getHeight();
        String string2 = "layout.minWidth=%0;layout.minHeight=btnHeight + %1 + 10;msgLbl.centerY=okBtn.top/2;msgLbl.centerX=container.width*50/100;btnWidth=max(okBtn.defaultWidth, copyBtn.defaultWidth);btnHeight=max(okBtn.defaultHeight, copyBtn.defaultHeight);okBtn.right=container.width*50/100-10;okBtn.bottom=container.bottom-5;okBtn.width=btnWidth;okBtn.height=btnHeight;copyBtn.left=container.width*50/100+10;copyBtn.bottom=container.bottom-5;copyBtn.width=btnWidth;copyBtn.height=btnHeight;"
                .replace("%0", String.valueOf(bb))
                .replace("%1", String.valueOf(height));
        constraintLayout1.parseConstraints(string2);
        this.setSize(bb, Math.max(100, height * 7));
        MessageDialogWindowListener messageDialogWindowListener = new MessageDialogWindowListener(this);
        this.addWindowListener(messageDialogWindowListener);
        MessageDialogEnterKeyListener messageDialogEnterKeyListener = new MessageDialogEnterKeyListener(this);
        this.okBtn.addKeyListener(messageDialogEnterKeyListener);
        this.copyBtn.addKeyListener(messageDialogEnterKeyListener);
        MessageDialogButtonListener messageDialogButtonListener = new MessageDialogButtonListener(this);
        this.okBtn.addActionListener(messageDialogButtonListener);
        this.copyBtn.addActionListener(messageDialogButtonListener);
        Dimension dimension = this.getSize();
        Point point = frame1.getLocationOnScreen();
        Dimension dimension1 = frame1.getSize();
        int bd = dimension1.width / 2 - dimension.width / 2 + point.x;
        int be = dimension1.height / 2 - dimension.height / 2 + point.y;
        bd = Math.max(0, bd);
        be = Math.max(0, be);
        this.setLocation(bd, be);
        SwingUtils.requestFocusOnEdt(this.okBtn);
        SwingUtils.setVisibleOnEdt(this);
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable transferable) {
    }

    @Override
    public void closeDialog() {
        this.setVisible(false);
        this.dispose();
    }

    public void copyMessageToClipboard() {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(this.msgLbl.getText()), this);
    }

    public static void showMessageLater(Frame frame1, String string) {
        showMessage(frame1, "Error", string, false);
    }
}
