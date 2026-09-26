package com.zelix.klassmaster.ui.dialog;

import com.zelix.GuiResources;
import com.zelix.UserPreferences;
import com.zelix.klassmaster.KlassMaster;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.component.ConstraintLayout;
import com.zelix.klassmaster.ui.component.SwingUtils;
import com.zelix.klassmaster.ui.component.ZkmScrollPane;
import com.zelix.klassmaster.util.IntPair;
import com.zelix.klassmaster.util.SerializableDimension;
import com.zelix.klassmaster.util.SystemEnvironmentConstants;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;

public class HelpViewerDialog extends EscapeClosableFrame implements ActionListener, ComponentListener, HyperlinkListener {
    public static String[] layoutConstraints = new String[]{
            "msgArea.top=5",
            "msgArea.left=5",
            "msgArea.right=container.right-5",
            "msgArea.bottom=container.bottom-closeBtn.defaultHeight-10",
            "btnWidth=max(backBtn.defaultWidth,closeBtn.defaultWidth,forwardBtn.defaultWidth)",
            "btnHeight=max(backBtn.defaultHeight,closeBtn.defaultHeight,forwardBtn.defaultHeight)",
            "backBtn.bottom=container.bottom-5",
            "backBtn.right=closeBtn.left-5",
            "backBtn.width=btnWidth",
            "backBtn.height=btnHeight",
            "closeBtn.bottom=container.bottom-5",
            "closeBtn.centerX=container.width*50/100",
            "closeBtn.width=btnWidth",
            "closeBtn.height=btnHeight",
            "forwardBtn.bottom=container.bottom-5",
            "forwardBtn.left=closeBtn.right+5",
            "forwardBtn.width=btnWidth",
            "forwardBtn.height=btnHeight"
    };
    public static final boolean IS_IBM_JVM = SystemEnvironmentConstants.JAVA_VM_VENDOR.indexOf("IBM") > -1;
    public JButton forwardBtn;
    public long lastLinkClickTime;
    public JEditorPane editorPane;
    public JButton closeBtn;
    public int historyOffset;
    public JButton backBtn;
    public Vector history = new Vector();
    public GuiResources guiResources;

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == this.closeBtn) {
            this.setVisible(false);
        } else if (actionEvent.getSource() == this.backBtn) {
            this.goBack();
        } else if (actionEvent.getSource() == this.forwardBtn) {
            this.goForward();
        }
    }

    @Override
    public void closeFrame() throws ZkmException, IOException {
        super.closeFrame();
        GuiResources.clearHelpViewer();
    }

    public void updateNavigationButtons() {
        int ba = this.history.size();
        if (ba <= 1) {
            this.backBtn.setEnabled(false);
            this.forwardBtn.setEnabled(false);
        } else {
            if (this.historyOffset < ba - 1) {
                this.backBtn.setEnabled(true);
            } else {
                this.backBtn.setEnabled(false);
            }

            if (this.historyOffset > 0) {
                this.forwardBtn.setEnabled(true);
            } else {
                this.forwardBtn.setEnabled(false);
            }
        }
    }

    public static void accessShowPage(HelpViewerDialog helpViewerDialog1, URL uRL) {
        helpViewerDialog1.showPage(uRL);
    }

    @Override
    public void componentHidden(ComponentEvent componentEvent) {
    }

    public static void accessGoForward(HelpViewerDialog helpViewerDialog1) {
        helpViewerDialog1.goForward();
    }

    @Override
    public void componentShown(ComponentEvent componentEvent) {
    }

    public static void accessGoBack(HelpViewerDialog helpViewerDialog1) {
        helpViewerDialog1.goBack();
    }

    public HelpViewerDialog(GuiResources guiResources1) {
        this.guiResources = guiResources1;
        this.initComponents();
        IntPair intPair = guiResources1.getWindowLocation();
        this.setLocation(intPair.getFirst(), intPair.getSecond());
        SerializableDimension serializableDimension = guiResources1.getWindowSize();
        this.setSize(serializableDimension.getWidth(), serializableDimension.getHeight());
    }

    public void showPage(URL uRL) {
        try {
            this.editorPane.setPage(uRL);
            this.history.clear();
            this.historyOffset = 0;
            this.history.add(uRL);
            this.updateNavigationButtons();
        } catch (IOException iOException) {
            this.showLoadError(uRL, iOException);
        }

        if (!this.isVisible()) {
            this.setVisible(true);
        }

        if (this.getState() == 1) {
            this.setState(0);
        }

        this.toFront();
    }

    @Override
    public void onShown() {
        SwingUtils.requestFocusOnEdt(this.closeBtn);
    }

    public void initComponents() {
        this.setTitle("Zelix KlassMaster - Help");
        Container container1 = this.getContentPane();
        ConstraintLayout constraintLayout1 = new ConstraintLayout(container1);
        container1.setLayout(constraintLayout1);
        this.editorPane = new JEditorPane();
        this.editorPane.setEditable(false);
        container1.add(new ZkmScrollPane(this.editorPane), "msgArea");
        this.closeBtn = new JButton("Close");
        this.closeBtn.setToolTipText(GuiResources.getTooltipText("HELP_EXIT"));
        container1.add(this.closeBtn, "closeBtn");
        this.backBtn = new JButton("<");
        this.backBtn.setToolTipText(GuiResources.getTooltipText("HELP_BACK"));
        container1.add(this.backBtn, "backBtn");
        this.forwardBtn = new JButton(">");
        this.forwardBtn.setToolTipText(GuiResources.getTooltipText("HELP_FORWARD"));
        container1.add(this.forwardBtn, "forwardBtn");
        constraintLayout1.setConstraints(layoutConstraints);
        this.backBtn.addActionListener(this);
        this.closeBtn.addActionListener(this);
        this.forwardBtn.addActionListener(this);
        HelpViewerKeyListener helpViewerKeyListener = new HelpViewerKeyListener(this);
        this.backBtn.addKeyListener(helpViewerKeyListener);
        this.closeBtn.addKeyListener(helpViewerKeyListener);
        this.forwardBtn.addKeyListener(helpViewerKeyListener);
        this.addComponentListener(this);
        this.editorPane.addHyperlinkListener(this);
        this.updateNavigationButtons();

        try {
            this.setIconImage(KlassMaster.getLogoImage(this));
        } catch (Throwable throwable) {
        }
    }

    public void showLoadError(Object object, Throwable throwable) {
        this.editorPane.setText(throwable.toString() + " : " + object);
    }

    @Override
    public void componentMoved(ComponentEvent componentEvent) {
        UserPreferences userPreferences1 = HiddenOptionFlags.USER_PREFERENCES;
        Point point = this.getLocation();
        userPreferences1.setHelpWindowLocation((int) point.getX(), (int) point.getY());
        userPreferences1.savePreferences();
    }

    public void showPageOnEdt(URL uRL) {
        if (SwingUtilities.isEventDispatchThread()) {
            this.showPage(uRL);
        } else {
            SwingUtilities.invokeLater(new HelpPageLoadTask(this, uRL));
        }
    }

    @Override
    public void componentResized(ComponentEvent componentEvent) {
        UserPreferences userPreferences1 = HiddenOptionFlags.USER_PREFERENCES;
        Dimension dimension = this.getSize();
        userPreferences1.setHelpWindowSize(dimension.width, dimension.height);
        userPreferences1.savePreferences();
    }

    public void goBack() {
        int ba = this.history.size();
        if (this.historyOffset < ba) {
            URL uRL = (URL) this.history.get(ba - 2 - this.historyOffset);

            try {
                this.editorPane.setPage(uRL);
                this.historyOffset++;
                this.updateNavigationButtons();
            } catch (IOException iOException) {
                this.showLoadError(uRL, iOException);
            }
        }
    }

    @Override
    public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
        if (hyperlinkEvent.getEventType() == EventType.ACTIVATED) {
            URL uRL = null;

            try {
                uRL = hyperlinkEvent.getURL();
                if (IS_IBM_JVM) {
                    long ba = System.currentTimeMillis();
                    long bb = ba - this.lastLinkClickTime;
                    this.lastLinkClickTime = ba;
                    if (bb < 500L) {
                        return;
                    }
                }

                this.editorPane.setPage(uRL);
                int bc = 0;
                int bd = 0;

                for (int i = this.historyOffset; bd < i; i = this.historyOffset) {
                    this.history.removeElementAt(this.history.size() - 1);
                    bd = ++bc;
                }

                this.historyOffset = 0;
                this.history.add(uRL);
                this.updateNavigationButtons();
            } catch (Throwable throwable) {
                this.showLoadError(uRL, throwable);
            }
        }
    }

    public void goForward() {
        int ba = this.history.size();
        if (this.historyOffset > 0) {
            URL uRL = (URL) this.history.get(ba - this.historyOffset);

            try {
                this.editorPane.setPage(uRL);
                this.historyOffset--;
                this.updateNavigationButtons();
            } catch (IOException iOException) {
                this.showLoadError(uRL, iOException);
            }
        }
    }
}
