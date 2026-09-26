package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.ui.component.RenameFilterModel;
import com.zelix.klassmaster.util.ParamEditorMarker;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public abstract class ExcludeParamTabPanel extends JPanel implements ParamEditorMarker {
    public JLabel hintLabel;
    public JTextField nameField;
    public int exclusionType;
    public JFrame ownerFrame;
    public RenameFilterModel filterModel;

    public ExcludeParamTabPanel(JFrame jFrame, RenameFilterModel renameFilterModel, int exclusionType) {
        this.exclusionType = exclusionType;
        this.ownerFrame = jFrame;
        this.filterModel = renameFilterModel;
    }

    public abstract void buildPanel();
}
