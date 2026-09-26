package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.KlassMaster;
import com.zelix.klassmaster.config.PersistedStringList;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmRuntimeException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.ZkmScriptTokenMgrError;
import com.zelix.klassmaster.script.parser.ZkmScriptParseException;
import com.zelix.klassmaster.script.parser.ZkmScriptParser;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;
import com.zelix.klassmaster.script.parser.ast.ASTRenameFilterParameter;
import com.zelix.klassmaster.script.parser.ast.ASTSingleRenameFilterParameter;
import com.zelix.klassmaster.ui.ZkmMainWindow;
import com.zelix.klassmaster.ui.component.BevelBorderPanel;
import com.zelix.klassmaster.ui.component.ConstraintLayout;
import com.zelix.klassmaster.ui.component.DisableableListItem;
import com.zelix.klassmaster.ui.component.RenameFilterModel;
import com.zelix.klassmaster.ui.component.SelfRenderingList;
import com.zelix.klassmaster.ui.component.SwingUtils;
import com.zelix.klassmaster.ui.component.ZkmScrollPane;
import com.zelix.klassmaster.util.DialogCallback;
import com.zelix.klassmaster.util.OrderedIndexedMap;
import com.zelix.klassmaster.util.ParamEditorMarker;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JTextArea;

public abstract class ParameterListDialog extends EscapeClosableFrame implements ParamEditorMarker {
    public boolean removingItem;
    public String lastSelectedParameter;
    public JButton cancelBtn;
    public JTextArea explanationArea;
    public SelfRenderingList paramList;
    public JButton mainDeleteBtn;
    public DefaultListModel paramListModel;
    public boolean closedByButton;
    public JButton mainModifyBtn;
    public JButton mainAddBtn;
    public JButton okBtn;
    public int editIndex;
    public OrderedIndexedMap paramIndexMap;
    public JButton helpBtn;
    public BevelBorderPanel mainPnl = new BevelBorderPanel(true);
    public ZkmMainWindow mainWindow;
    public DialogCallback callback;
    public ScriptEnvironment scriptEnvironment;
    public int paramKind;

    @Override
    public final Action createEscapeAction() {
        return new ExclusionDialogCancelAction(this);
    }

    public final void removeParameter(String string) throws ZkmException, IOException {
        this.removeParameterAt(this.paramIndexMap.indexOfKey(string));
    }

    public boolean canAccept() throws ZkmException, IOException {
        return true;
    }

    public final void showDefaultCursor() {
        Cursor cursor1 = Cursor.getDefaultCursor();
        this.setCursor(cursor1);
        this.explanationArea.setCursor(cursor1);
    }

    public ParameterListDialog(ZkmMainWindow zkmMainWindow, ScriptEnvironment scriptEnvironment1, DialogCallback dialogCallback1, int paramKind) {
        this.mainWindow = zkmMainWindow;
        this.callback = dialogCallback1;
        this.scriptEnvironment = scriptEnvironment1;
        this.paramKind = paramKind;
    }

    public final void showWaitCursor() {
        Cursor cursor1 = new Cursor(3);
        this.setCursor(cursor1);
        this.explanationArea.setCursor(cursor1);
    }

    public final void initComponents(String string) throws ZkmException, IOException {
        this.setTitle(string);
        Container container1 = this.getContentPane();
        ConstraintLayout constraintLayout1 = new ConstraintLayout(container1);
        container1.setLayout(constraintLayout1);
        ParamDialogWindowCloser paramDialogWindowCloser = new ParamDialogWindowCloser(this);
        this.addWindowListener(paramDialogWindowCloser);
        container1.add(this.mainPnl, "mainPnl");
        this.buildMainPanel();
        this.loadParameters(container1);
        this.layoutButtons(constraintLayout1, container1);
        this.initToolTips();
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        this.setIconImage(KlassMaster.getLogoImage(this));
        int bc = dimension.width;
        int be = Math.min(600, bc);
        int bd = dimension.height;
        this.setSize(be, Math.min(500, bd));
        Dimension dimension1 = this.getSize();
        Point point = this.mainWindow.getLocationOnScreen();
        Dimension dimension2 = this.mainWindow.getSize();
        int ba = dimension2.width / 2 - dimension1.width / 2 + point.x;
        int bb = dimension2.height / 2 - dimension1.height / 2 + point.y;
        ba = Math.max(0, ba);
        bb = Math.max(0, bb);
        this.setLocation(ba, bb);
        this.mainWindow.setEnabled(false);
        SwingUtils.setVisibleOnEdt(this);
    }

    @Override
    public final void closeFrame() throws ZkmException, IOException {
        super.closeFrame();
        if (!this.closedByButton) {
            this.callback.onDialogCancelled();
        }
    }

    public final void modifySelectedParameter() throws ZkmException, IOException {
        this.editIndex = this.paramList.getSelectedIndex();
        if (this.editIndex != -1) {
            String string = (String) ((DisableableListItem) this.paramList.getSelectedValue()).getItem();
            RenameFilterModel renameFilterModel = this.parseParameter(string);
            ParamDialogCallback paramDialogCallback = new ParamDialogCallback(this);
            this.editParameter(renameFilterModel, string, paramDialogCallback);
        }
    }

    public final void cancelDialog() throws ZkmException, IOException {
        this.closedByButton = true;
        this.closeFrame();
        this.callback.onDialogCancelled();
    }

    public void removeParameterAt(int ba) throws ZkmException, IOException {
        if (ba > -1) {
            this.removingItem = true;
            this.paramListModel.remove(ba);
            this.removingItem = false;
            this.paramIndexMap.removeAt(ba);
            int size = this.paramListModel.getSize();
            if (ba < size) {
                this.paramList.setSelectedIndex(ba);
            } else if (size > 0) {
                this.paramList.setSelectedIndex(size - 1);
            }
        }

        int selectedIndex = this.paramList.getSelectedIndex();
        if (selectedIndex == -1) {
            this.mainModifyBtn.setEnabled(false);
            this.mainDeleteBtn.setEnabled(false);
            this.explanationArea.setText("");
        } else {
            if (((DisableableListItem) this.paramList.getSelectedValue()).isDisabled()) {
                this.mainModifyBtn.setEnabled(false);
                this.mainDeleteBtn.setEnabled(false);
            } else {
                this.mainModifyBtn.setEnabled(true);
                this.mainDeleteBtn.setEnabled(true);
            }

            String string = (String) ((DisableableListItem) this.paramListModel.getElementAt(selectedIndex)).getItem();
            String string1 = this.parseParameter(string).buildDescription(this.paramKind);
            this.explanationArea.setText(string1);
            this.explanationArea.setCaretPosition(0);
        }

        this.onParametersChanged();
    }

    public abstract void editParameter(RenameFilterModel renameFilterModel, String string, DialogCallback dialogCallback1) throws ZkmException, IOException;

    public abstract void showHelp();

    public abstract String[] getParamListConstraints();

    public final void acceptParameters() throws ZkmException, IOException {
        if (this.canAccept()) {
            this.closedByButton = true;
            this.closeFrame();
            this.saveParameters();
            this.callback.onDialogResult(1);
        }
    }

    public abstract void openParameterEditor(String string, String string1, DialogCallback dialogCallback1) throws ZkmException, IOException;

    public final void removeSelectedParameter() throws ZkmException, IOException {
        int selectedIndex = this.paramList.getSelectedIndex();
        this.removeParameterAt(selectedIndex);
    }

    public final RenameFilterModel parseParameter(String string) throws ZkmException, IOException {
        BufferedReader bufferedReader = new BufferedReader(new StringReader(string + ";"));
        ZkmScriptParser zkmScriptParser = new ZkmScriptParser(bufferedReader);

        ZkmScriptSimpleNode zkmScriptSimpleNode;
        try {
            zkmScriptSimpleNode = zkmScriptParser.SingleRenameFilterParameter();
            zkmScriptSimpleNode.execute(null, this.scriptEnvironment);
        } catch (ZkmScriptParseException zkmScriptParseException) {
            throw new ZkmRuntimeException("Bad parameter '" + string + "' (A)");
        } catch (ZkmScriptTokenMgrError zkmScriptTokenMgrError) {
            throw new ZkmRuntimeException("Bad parameter '" + string + "' (B)");
        }

        ASTRenameFilterParameter aSTRenameFilterParameter = ((ASTSingleRenameFilterParameter) zkmScriptSimpleNode).getRenameFilterParameter();
        return new RenameFilterModel(aSTRenameFilterParameter);
    }

    public abstract void loadParameters(Object object) throws ZkmException, IOException;

    public final void addParameter(String string) {
        this.addParameterItem(string, false);
    }

    public void onParametersChanged() {
    }

    public final void buildMainPanel() {
        ConstraintLayout constraintLayout1 = new ConstraintLayout(this.mainPnl);
        this.mainPnl.setLayout(constraintLayout1);
        this.mainAddBtn = new JButton("Add");
        this.mainModifyBtn = new JButton("Modify");
        this.mainDeleteBtn = new JButton("Delete");
        this.paramListModel = new DefaultListModel();
        this.paramList = new SelfRenderingList(this.paramListModel);
        this.paramList.setSelectionMode(0);
        this.paramList.setFont(this.getFont());
        this.paramIndexMap = new OrderedIndexedMap();
        this.explanationArea = new JTextArea();
        this.explanationArea.setEditable(false);
        this.mainPnl.add(this.mainAddBtn, "mainAddBtn");
        this.mainPnl.add(this.mainModifyBtn, "mainModifyBtn");
        this.mainPnl.add(this.mainDeleteBtn, "mainDeleteBtn");
        this.mainPnl.add(new ZkmScrollPane(this.paramList), "mainParamList");
        this.mainPnl.add(new ZkmScrollPane(this.explanationArea), "explanationArea");
        constraintLayout1.setConstraints(this.getParamListConstraints());
        this.mainModifyBtn.setEnabled(false);
        this.mainDeleteBtn.setEnabled(false);
        ExclusionListSelectionListener exclusionListSelectionListener = new ExclusionListSelectionListener(this);
        this.paramList.addListSelectionListener(exclusionListSelectionListener);
        ExclusionListButtonListener exclusionListButtonListener = new ExclusionListButtonListener(this);
        this.mainAddBtn.addActionListener(exclusionListButtonListener);
        this.mainModifyBtn.addActionListener(exclusionListButtonListener);
        this.mainDeleteBtn.addActionListener(exclusionListButtonListener);
        this.paramList.addMouseListener(new ParamListMouseListener(this));
        ParamListKeyListener paramListKeyListener = new ParamListKeyListener(this);
        this.mainAddBtn.addKeyListener(paramListKeyListener);
        this.mainModifyBtn.addKeyListener(paramListKeyListener);
        this.mainDeleteBtn.addKeyListener(paramListKeyListener);
        this.paramList.addKeyListener(paramListKeyListener);
    }

    public final void updateEditedParameter(String string) throws ZkmException, IOException {
        DisableableListItem disableableListItem = new DisableableListItem(string, false);
        this.paramListModel.setElementAt(disableableListItem, this.editIndex);
        if (this.paramIndexMap.containsKey(string) && this.paramIndexMap.indexOfKey(string) != this.editIndex) {
            this.removingItem = true;
            this.paramListModel.remove(this.editIndex);
            this.removingItem = false;
            this.paramIndexMap.removeAt(this.editIndex);
            this.paramList.setSelectedIndex(this.paramListModel.indexOf(disableableListItem));
        } else {
            this.paramIndexMap.replaceEntryAt(this.editIndex, string, disableableListItem);
            String string1 = this.parseParameter(string).buildDescription(this.paramKind);
            this.explanationArea.setText(string1);
            this.explanationArea.setCaretPosition(0);
        }
    }

    public abstract void initToolTips();

    public final void startAddParameter() {
        ParameterAddCallback parameterAddCallback = new ParameterAddCallback(this);
        this.openParameterTypeDialog(parameterAddCallback);
    }

    public final void addParameterItem(String string, Boolean boolean1) {
        if (!this.paramIndexMap.containsKey(string)) {
            DisableableListItem disableableListItem = new DisableableListItem(string, boolean1);
            this.paramListModel.addElement(disableableListItem);
            this.paramIndexMap.put(string, disableableListItem);
            this.paramList.setSelectedIndex(this.paramListModel.indexOf(disableableListItem));
            if (this.paramList.getSelectedIndex() == -1) {
                this.mainModifyBtn.setEnabled(false);
                this.mainDeleteBtn.setEnabled(false);
                this.explanationArea.setText("");
            } else {
                this.paramList.ensureIndexIsVisible(this.paramList.getSelectedIndex());
            }
        }
    }

    public abstract void openParameterTypeDialog(DialogCallback dialogCallback1);

    public abstract PersistedStringList getParameterStore();

    public void saveParameters() {
        ArrayList arrayList = new ArrayList();
        Enumeration enumeration = this.paramListModel.elements();

        while (enumeration.hasMoreElements()) {
            DisableableListItem disableableListItem = (DisableableListItem) enumeration.nextElement();
            if (!disableableListItem.isDisabled()) {
                arrayList.add(disableableListItem.getItem());
            }
        }

        this.getParameterStore().setEntries(arrayList);
    }

    public abstract void layoutButtons(ConstraintLayout constraintLayout1, Container container1);
}
