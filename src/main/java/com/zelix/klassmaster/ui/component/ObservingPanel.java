package com.zelix.klassmaster.ui.component;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.NodeVisitor;
import com.zelix.klassmaster.util.ObservableModel;

import java.awt.Font;
import java.io.IOException;
import javax.swing.JPanel;

public class ObservingPanel extends JPanel implements NodeVisitor {
    private static String fontName;
    private static long fontSize;

    @Override
    public void handleObservedChange(ObservableModel observableModel1, Object object, Object object1, Object object2) throws ZkmException, IOException {
    }

    public ObservingPanel() {
        this.setFont(new Font(fontName, 0, (int) fontSize));
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
        fontName = "Dialog";
        fontSize = 7341899237031936012L;
    }
}
