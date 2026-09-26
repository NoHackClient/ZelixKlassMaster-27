package com.zelix.klassmaster.ui.dialog;

import com.zelix.GuiResources;
import com.zelix.klassmaster.ui.component.ConstraintLayout;
import com.zelix.klassmaster.ui.component.SplitLayout;
import com.zelix.klassmaster.ui.component.ZkmScrollPane;
import com.zelix.klassmaster.util.DialogCallback;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class ClassOpenWarningsDialog extends TextViewerDialog {
    public static String[] layoutSpec = new String[]{
            "msgLbl.top=5",
            "msgLbl.left=container.width*5/100",
            "splitPanel.top=msgLbl.bottom+5",
            "splitPanel.bottom=container.bottom-okBtn.defaultHeight-10",
            "splitPanel.left=container.width*5/100",
            "splitPanel.right=container.width*95/100",
            "okBtn.top=splitPanel.bottom+5",
            "okBtn.centerX=container.width*50/100",
            "layout.minWidth=250",
            "layout.minHeight=200"
    };
    public JEditorPane helpPane;

    public ClassOpenWarningsDialog(JFrame jFrame, BufferedReader bufferedReader) {
        this(jFrame, "Class Open Warnings", "API calls detected that may not be handled automatically...", bufferedReader, false, true);
    }

    public ClassOpenWarningsDialog(
            JFrame jFrame,
            String string,
            long ba,
            String string1,
            BufferedReader bufferedReader,
            boolean bl,
            boolean bl1,
            boolean bl2,
            DialogCallback dialogCallback1
    ) {
        super(jFrame, "Class Open Warnings", "API calls detected that may not be handled automatically...", bufferedReader);
    }

    public ClassOpenWarningsDialog(JFrame jFrame, String string, String string1, BufferedReader bufferedReader, boolean bl, boolean bl1) {
        this(
                jFrame, "Class Open Warnings", 88672482885612L, "API calls detected that may not be handled automatically...", bufferedReader, false, true, true, null
        );
    }

    @Override
    public void buildLayout() {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        Container container1 = this.getContentPane();
        super.constraintLayout = new ConstraintLayout(container1);
        container1.setLayout(super.constraintLayout);
        super.messageLabel = new JLabel();
        container1.add(super.messageLabel, "msgLbl");
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new SplitLayout(1, 60, 7));
        container1.add(jPanel, "splitPanel");
        super.textArea = new JTextArea();
        jPanel.add(new ZkmScrollPane(super.textArea), "viewArea");
        this.helpPane = new JEditorPane();
        this.helpPane.setEditable(false);

        try {
            this.helpPane.setPage(GuiResources.getHelpTopicUrl("036"));
        } catch (IOException iOException) {
            this.helpPane.setText(iOException.toString() + " : " + "036");
        }

        jPanel.add(new ZkmScrollPane(this.helpPane), "helpPane");
        super.okBtn = new JButton("OK");
        container1.add(super.okBtn, "okBtn");
        WarningsDialogWindowListener warningsDialogWindowListener = new WarningsDialogWindowListener(this);
        this.addWindowListener(warningsDialogWindowListener);
        HelpDialogKeyListener helpDialogKeyListener = new HelpDialogKeyListener(this);
        super.okBtn.addKeyListener(helpDialogKeyListener);
        super.textArea.addKeyListener(helpDialogKeyListener);
        WarningsDialogActionListener warningsDialogActionListener = new WarningsDialogActionListener(this);
        super.okBtn.addActionListener(warningsDialogActionListener);
        this.helpPane.addHyperlinkListener(new HelpHyperlinkListener(this));
        super.constraintLayout.setConstraints(layoutSpec);
        int ba = dimension.width;
        int bc = Math.min(600, ba);
        int bb = dimension.height;
        this.setSize(bc, Math.min(450, bb));
    }
}
