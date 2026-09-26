package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.NodeVisitor;
import com.zelix.klassmaster.util.ObservableModel;

import java.io.IOException;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

public class EscapeClosableFrame extends JFrame implements NodeVisitor {
    private static int controlValue;

    @Override
    public void validateTree() {
        super.validateTree();
    }

    public void onShown() {
    }

    public static void setControlValue(int ba) {
        controlValue = 21;
    }

    @Override
    public void doLayout() {
        super.doLayout();
    }

    @Override
    public void handleObservedChange(ObservableModel observableModel1, Object object, Object object1, Object object2) throws ZkmException, IOException {
    }

    public static int getInvertedControlValue() {
        return getControlValue() == 0 ? 95 : 0;
    }

    static {
        setControlValue(21);
    }

    @Override
    public void invalidate() {
        super.invalidate();
    }

    @Override
    public void validate() {
        super.validate();
    }

    @Override
    public JRootPane createRootPane() {
        JRootPane jRootPane = new JRootPane();
        KeyStroke keyStroke = KeyStroke.getKeyStroke("ESCAPE");
        Action action = this.createEscapeAction();
        jRootPane.getInputMap(2).put(keyStroke, "ESCAPE");
        jRootPane.getActionMap().put("ESCAPE", action);
        return jRootPane;
    }

    public void initFrame() {
    }

    public Action createEscapeAction() {
        return new EscapeKeyAction(this);
    }

    @Override
    public void setVisible(boolean bl) {
        super.setVisible(bl);
        if (bl) {
            this.onShown();
        }
    }

    public void closeFrame() throws ZkmException, IOException {
        this.setVisible(false);
        this.dispose();
    }

    public EscapeClosableFrame() {
        this.initFrame();
    }

    public static int getControlValue() {
        return controlValue;
    }

    public EscapeClosableFrame(String string) {
        super(string);
        this.initFrame();
    }
}
