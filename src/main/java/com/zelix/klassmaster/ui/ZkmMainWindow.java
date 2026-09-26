package com.zelix.klassmaster.ui;

import com.zelix.GuiResources;
import com.zelix.UserPreferences;
import com.zelix.klassmaster.KlassMaster;
import com.zelix.klassmaster.archive.InputFileLocation;
import com.zelix.klassmaster.archive.SourceArchive;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.FieldInfo;
import com.zelix.klassmaster.classfile.MemberInfo;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.constpool.LoadableConstant;
import com.zelix.klassmaster.classfile.constpool.NumericConstantEntry;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.classfile.hierarchy.ZkmClasspath;
import com.zelix.klassmaster.config.ObfuscateOptions;
import com.zelix.klassmaster.config.TrimOptions;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.obfuscator.rename.RootPackageNode;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ast.ASTExcludeStatement;
import com.zelix.klassmaster.script.parser.ast.ASTObfuscateReferencesIncludeStatement;
import com.zelix.klassmaster.script.parser.ast.ASTTrimExcludeStatement;
import com.zelix.klassmaster.script.parser.ast.ParameterListStatement;
import com.zelix.klassmaster.ui.component.BevelBorderPanel;
import com.zelix.klassmaster.ui.component.ClassConstantsNode;
import com.zelix.klassmaster.ui.component.ClassListEntry;
import com.zelix.klassmaster.ui.component.ClassPropertyNode;
import com.zelix.klassmaster.ui.component.ConstraintLayout;
import com.zelix.klassmaster.ui.component.DisableableListItem;
import com.zelix.klassmaster.ui.component.EdtCallback;
import com.zelix.klassmaster.ui.component.EdtCallbackInvoker;
import com.zelix.klassmaster.ui.component.MemberListNode;
import com.zelix.klassmaster.ui.component.PlatformAwareJList;
import com.zelix.klassmaster.ui.component.SelectionHolder;
import com.zelix.klassmaster.ui.component.SelfRenderingList;
import com.zelix.klassmaster.ui.component.SplitLayout;
import com.zelix.klassmaster.ui.component.SwingUtils;
import com.zelix.klassmaster.ui.component.ZkmScrollPane;
import com.zelix.klassmaster.ui.dialog.AboutDialog;
import com.zelix.klassmaster.ui.dialog.ClasspathDialog;
import com.zelix.klassmaster.ui.dialog.CopyableMessageDialog;
import com.zelix.klassmaster.ui.dialog.EscapeClosableFrame;
import com.zelix.klassmaster.ui.dialog.FileSelectorDialog;
import com.zelix.klassmaster.ui.dialog.MessageBoxDialog;
import com.zelix.klassmaster.ui.dialog.OpenClassesChoiceDialog;
import com.zelix.klassmaster.ui.dialog.OpenClassesDialog;
import com.zelix.klassmaster.ui.dialog.ProGuardTranslateDialog;
import com.zelix.klassmaster.ui.dialog.SaveClassesDialog;
import com.zelix.klassmaster.ui.dialog.StackTraceTranslateDialog;
import com.zelix.klassmaster.ui.wizard.BuildHelperWizard;
import com.zelix.klassmaster.ui.wizard.HelperChoiceCallback;
import com.zelix.klassmaster.ui.wizard.ObfuscateWizardFlow;
import com.zelix.klassmaster.ui.wizard.ScriptHelperWizard;
import com.zelix.klassmaster.ui.wizard.TrimWizardController;
import com.zelix.klassmaster.util.DialogCallback;
import com.zelix.klassmaster.util.ListenerRegistry;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ObservableModel;
import com.zelix.klassmaster.util.SetMultiMap;
import com.zelix.klassmaster.util.VisitableNode;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;

public class ZkmMainWindow extends EscapeClosableFrame implements EdtCallback, MainWindowChangeListener {
    public static String[] propertiesLayoutConstraints;
    public static String[] layoutConstraints;
    public JMenuItem stackTraceTranslateItem;
    public JMenuItem scriptHelperItem;
    public JPanel propertiesAreaPnl;
    public JPanel mainPnl;
    public JPanel rightSplitPnl;
    public BevelBorderPanel statusPnl;
    public JLabel statusLabel;
    public JMenuItem browserHelpItem;
    public int selectedClassIndex;
    public JMenuItem obfuscateItem;
    public JMenuItem saveAllItem;
    public JTextArea classTextArea;
    public BevelBorderPanel textViewPnl;
    public MemberInfo selectedMember;
    public ListSelectionEvent propertySelectionEvent;
    public JMenuItem proGuardTranslateItem;
    public PlatformAwareJList memberList;
    public BevelBorderPanel classPropertiesPnl;
    public JMenuItem garbageCollectItem;
    public ZkmScrollPane textScrollPane;
    public JMenuItem trimItem;
    public SelfRenderingList classList;
    public BevelBorderPanel classListPnl;
    public JMenuItem exitItem;
    public JMenuItem buildHelperItem;
    public JMenuItem aboutItem;
    public int selectedPropertyIndex;
    public PlatformAwareJList classPropertiesList;
    public JMenuItem openItem;
    public JMenuItem overviewHelpItem;
    public JMenuItem classpathItem;
    public BevelBorderPanel tertiaryPnl;
    public boolean busy;
    public BevelBorderPanel secondaryPnl;
    public JMenuBar mainMenuBar = new JMenuBar();
    public final Font windowFont = KlassMaster.getDefaultFont();
    public final SelectionHolder selectedClassHolder = new SelectionHolder();
    public final SelectionHolder selectedItemHolder = new SelectionHolder();
    public final ListenerRegistry listenerRegistry = new ListenerRegistry();
    public ClassRepository classRepository;
    public final ObservableHolder statusHolder;
    public final ZkmClasspath classpath;
    public final String logFileName;
    public final PrintWriter logWriter;
    public final UserPreferences userPreferences;
    private final ClassPropertiesListView classPropertiesListView;
    private final BrowserTextViewUpdater textViewUpdater;

    public void startTrim(List list1, TrimOptions trimOptions1, ScriptEnvironment scriptEnvironment1, OperationStatusCallback operationStatusCallback) throws ZkmException, IOException {
        if (this.classRepository.hasNoClassesOpened()) {
            new MessageBoxDialog(this, "No classes opened", "There are no classes to trim. Use the \"File | Open\" menu to open your classes.");
        } else if (!this.classRepository.hasProgramClasses()) {
            new MessageBoxDialog(
                    this,
                    "Only module-info.class files opened",
                    "The are no classes other than module-info to trim. Use the \"File | Open\" menu to open your classes."
            );
        } else if (!this.classRepository.isOpenedWithoutErrors()) {
            new CopyableMessageDialog(
                    this,
                    "Opened classes not useable",
                    "Opened classes not useable for the reason shown below.",
                    this.classRepository.getOpenErrorMessage() + " : Please adjust your " + "Zelix KlassMaster" + " classpath or class file selections."
            );
        } else if (this.classRepository.isClasspathChanged()) {
            new MessageBoxDialog(this, "Classpath changed", "Classpath has been changed but the classes have not been reopened. You must reopen the classes. (G)");
        } else {
            Vector vector = new Vector(1);
            String string = ASTTrimExcludeStatement.formatTrimExcludeStatement(list1, 12);
            if (string != null) {
                ParameterListStatement parameterListStatement;
                try {
                    parameterListStatement = ASTTrimExcludeStatement.parseDefaultTrimExclude(string, scriptEnvironment1);
                } catch (ZkmProcessingException zkmProcessingException) {
                    ScriptEnvironment scriptEnvironment2;
                    String string1;
                    if (operationStatusCallback != null) {
                        operationStatusCallback.setStatus((String) null);
                        scriptEnvironment2 = scriptEnvironment1;
                        string1 = zkmProcessingException.getMessage();
                    } else {
                        scriptEnvironment2 = scriptEnvironment1;
                        string1 = zkmProcessingException.getMessage();
                    }

                    scriptEnvironment2.logWarning(string1);
                    new MessageBoxDialog(this, "Error", "Unexpected error in exclude parameters. (3) See the log for more detail.");
                    if (operationStatusCallback != null) {
                        operationStatusCallback.onDialogCancelled();
                    }

                    return;
                }

                vector.addElement(parameterListStatement);
            }

            this.showWaitCursor();
            this.setBusy(true);
            GuiMessageReporter guiMessageReporter = new GuiMessageReporter(this, scriptEnvironment1);
            TrimTaskCallback trimTaskCallback = new TrimTaskCallback(this, operationStatusCallback);
            TrimReportViewTask trimReportViewTask = new TrimReportViewTask(
                    this, operationStatusCallback, guiMessageReporter, trimOptions1, vector, trimTaskCallback, scriptEnvironment1
            );
            new Thread(trimReportViewTask).start();
        }
    }

    public void showOpenClassesDialog() {
        MainFrameOpenCallback mainFrameOpenCallback = new MainFrameOpenCallback(this);
        this.setBusy(true);
        this.setEnabled(false);
        new OpenClassesDialog(
                this,
                "Zelix KlassMaster - Open Classes",
                "OK",
                true,
                this.userPreferences.getOpenClassesDirectory(),
                this.userPreferences.isOpenNestedArchives(),
                null,
                mainFrameOpenCallback
        );
    }

    public List collectEntryPointClasses() throws ZkmException, IOException {
        AbstractList abstractList = null;
        Enumeration enumeration = this.classRepository.enumerateClassesDeclaringMethod(MethodSignature.MAIN_METHOD);
        List list1 = this.classRepository.findSubclasses();
        if (enumeration != null && enumeration.hasMoreElements()) {
            abstractList = new Vector();

            while (enumeration.hasMoreElements()) {
                abstractList.add(new ClassListEntry((ClassFileBase) enumeration.nextElement(), true));
            }
        }

        if (list1 != null && list1.size() > 0) {
            if (abstractList == null) {
                abstractList = new ArrayList(list1.size());
            }

            for (int i = 0; i < list1.size(); i++) {
                abstractList.add(new ClassListEntry((ClassFileBase) list1.get(i), false));
            }
        }

        return abstractList;
    }

    public void showClassProperty(int selectedPropertyIndex, ListSelectionEvent listSelectionEvent) throws ZkmException, IOException {
        ProgramClass programClass1 = (ProgramClass) this.selectedClassHolder.getSelectedNode();
        this.selectedPropertyIndex = selectedPropertyIndex;
        this.propertySelectionEvent = listSelectionEvent;
        this.listenerRegistry.removeAllObservers();
        this.secondaryPnl.removeAll();
        this.tertiaryPnl.removeAll();
        if (this.selectedItemHolder.getSelectedNode() instanceof MemberInfo) {
            this.selectedItemHolder.setSelectedNode(this.selectedClassHolder.getSelectedNode());
        }

        ClassPropertyNode classPropertyNode = programClass1.getPropertyNode(selectedPropertyIndex);
        switch (selectedPropertyIndex) {
            case 0:
                ClassModifyPanel classModifyPanel = new ClassModifyPanel(
                        (ProgramClass) classPropertyNode.getOwnerComponent(), this.classRepository, this, this.listenerRegistry
                );
                this.secondaryPnl.add(classModifyPanel, "Center");
                this.secondaryPnl.validate();
                break;
            case 1:
            case 2:
                MemberListNode memberListNode1 = (MemberListNode) classPropertyNode;
                DefaultListModel defaultListModel1 = new DefaultListModel();
                this.memberList = new PlatformAwareJList(defaultListModel1);
                this.memberList.setSelectionMode(0);
                PlatformAwareJList platformAwareJList;
                if (selectedPropertyIndex == 1) {
                    this.memberList.setToolTipText(GuiResources.getTooltipText("BROWSER_FIELD_LIST"));
                    platformAwareJList = this.memberList;
                } else {
                    this.memberList.setToolTipText(GuiResources.getTooltipText("BROWSER_METHOD_LIST"));
                    platformAwareJList = this.memberList;
                }

                platformAwareJList.addListSelectionListener(new BrowserMemberListListener(memberListNode1, this));
                this.listenerRegistry.addDefaultObserver(memberListNode1, this);
                ZkmMainWindow zkmMainWindow1 = this;
                memberListNode1.notifyObserverWithoutArgs(zkmMainWindow1);
                this.secondaryPnl.add(new ZkmScrollPane(this.memberList), "Center");
                this.secondaryPnl.validate();
                break;
            case 3:
                ClassConstantsNode classConstantsNode = (ClassConstantsNode) classPropertyNode;
                DefaultListModel defaultListModel = new DefaultListModel();
                this.memberList = new PlatformAwareJList(defaultListModel);
                this.memberList.setSelectionMode(0);
                this.memberList.setToolTipText(GuiResources.getTooltipText("BROWSER_CONSTANTS_LIST"));
                this.memberList.addListSelectionListener(new ConstantsListSelectionListener(classConstantsNode, this));
                this.listenerRegistry.addDefaultObserver(classConstantsNode, this);
                ZkmMainWindow zkmMainWindow2 = this;
                classConstantsNode.notifyObserverWithoutArgs(zkmMainWindow2);
                this.secondaryPnl.add(new ZkmScrollPane(this.memberList), "Center");
                this.secondaryPnl.validate();
        }
    }

    public void selectClass(int selectedClassIndex) throws ZkmException, IOException {
        if (this.busy) {
            if (this.selectedClassIndex > -1 && this.selectedClassIndex != selectedClassIndex) {
                this.classList.setSelectedIndex(this.selectedClassIndex);
                this.classList.ensureIndexIsVisible(this.selectedClassIndex);
            }
        } else if (selectedClassIndex != this.selectedClassIndex) {
            this.listenerRegistry.removeAllObservers();
            this.tertiaryPnl.removeAll();
            this.selectedClassIndex = selectedClassIndex;
            ProgramClass programClass1 = this.classRepository.getHierarchyNode(selectedClassIndex).getProgramClass();
            this.selectedClassHolder.setSelectedNode(programClass1);
            this.selectedItemHolder.setSelectedNode(programClass1);
            if (programClass1 != null) {
                if (this.propertySelectionEvent != null) {
                    this.classPropertiesList.setSelectedIndex(this.selectedPropertyIndex);
                    this.classPropertiesList.ensureIndexIsVisible(this.selectedPropertyIndex);
                }
            } else {
                this.secondaryPnl.removeAll();
            }
        }
    }

    public void applyWaitCursor() {
        Cursor cursor1 = new Cursor(3);
        this.setCursor(cursor1);
        this.classListPnl.setCursor(cursor1);
        this.classPropertiesPnl.setCursor(cursor1);
        this.secondaryPnl.setCursor(cursor1);
        this.tertiaryPnl.setCursor(cursor1);
        this.textViewPnl.setCursor(cursor1);
        this.classList.setCursor(cursor1);
        this.classPropertiesList.setCursor(cursor1);
        this.classTextArea.setCursor(cursor1);
    }

    @Override
    public void closeFrame() throws ZkmException, IOException {
        if (GuiResources.isHelpViewerOpen()) {
            GuiResources.closeHelpViewer();
        }

        super.closeFrame();
        if (this.classpath != null) {
            this.classpath.closeJrtFileSystem();
        }
    }

    public void showClasspathDialog() {
        MainWindowUnbusyCallback mainWindowUnbusyCallback = new MainWindowUnbusyCallback(this);
        this.setBusy(true);
        new ClasspathDialog(
                this,
                "Classpath",
                this.classpath,
                GuiResources.getTooltipText("CLASSPATH_MSG"),
                "OK",
                GuiResources.getTooltipText("SET_CLASSPATH"),
                this.userPreferences,
                mainWindowUnbusyCallback
        );
    }

    public static SelectionHolder getSelectedItemHolder(ZkmMainWindow zkmMainWindow) {
        return zkmMainWindow.selectedItemHolder;
    }

    public void openChosenClasses(
            InputFileLocation[] inputFileLocations,
            SourceArchive[] sourceArchives1,
            InputFileLocation[] inputFileLocations1,
            InputFileLocation[] inputFileLocations2,
            ObservableHolder observableHolder,
            Boolean boolean1,
            Set set1,
            OperationStatusCallback operationStatusCallback
    ) throws ZkmException, IOException {
        String string = (String) observableHolder.getValue();
        this.userPreferences.setOpenClassesDirectory(string);
        OperationStatusCallback operationStatusCallback1 = operationStatusCallback;
        Set set2 = set1;
        this.openClasses(inputFileLocations, sourceArchives1, inputFileLocations1, inputFileLocations2, boolean1, set2, operationStatusCallback1);
    }

    public static ObservableHolder getStatusHolder(ZkmMainWindow zkmMainWindow) {
        return zkmMainWindow.statusHolder;
    }

    public ClassRepository getClassRepository() {
        return this.classRepository;
    }

    public static void invokeOpenClasses(
            ZkmMainWindow zkmMainWindow,
            InputFileLocation[] inputFileLocations,
            SourceArchive[] sourceArchives1,
            InputFileLocation[] inputFileLocations1,
            InputFileLocation[] inputFileLocations2,
            Boolean boolean1,
            Set set1,
            OperationStatusCallback operationStatusCallback
    ) throws ZkmException, IOException {
        Set set2 = set1;
        zkmMainWindow.openClasses(inputFileLocations, sourceArchives1, inputFileLocations1, inputFileLocations2, boolean1, set2, operationStatusCallback);
    }

    public static PrintWriter getLogWriter(ZkmMainWindow zkmMainWindow) {
        return zkmMainWindow.logWriter;
    }

    public void restoreDefaultCursor() {
        if (SwingUtilities.isEventDispatchThread()) {
            this.applyDefaultCursor();
        } else {
            SwingUtilities.invokeLater(new RestoreCursorTask(this));
        }
    }

    public void openClasses(
            InputFileLocation[] inputFileLocations,
            SourceArchive[] sourceArchives1,
            InputFileLocation[] inputFileLocations1,
            InputFileLocation[] inputFileLocations2,
            boolean bl,
            Set set1,
            OperationStatusCallback operationStatusCallback
    ) throws ZkmException, IOException {
        this.userPreferences.setOpenNestedArchives(bl);
        this.userPreferences.savePreferences();
        InputFileLocation[] inputFileLocations3 = new InputFileLocation[0];
        InputFileLocation[] inputFileLocations4 = new InputFileLocation[0];
        SwingUtils.requestFocusOnEdt(this);
        if (inputFileLocations != null) {
            this.selectedClassHolder.setSelectedNode((VisitableNode) null);
            this.selectedItemHolder.setSelectedNode((VisitableNode) null);
            this.secondaryPnl.removeAll();
            this.tertiaryPnl.removeAll();
            this.showWaitCursor();
            MainWindowErrorReporter mainWindowErrorReporter = new MainWindowErrorReporter(this, this, this.createScriptEnvironment());
            OpenClassesDoneCallback openClassesDoneCallback = new OpenClassesDoneCallback(this, operationStatusCallback);
            OpenClassesTask openClassesTask = new OpenClassesTask(
                    this,
                    mainWindowErrorReporter,
                    inputFileLocations,
                    sourceArchives1,
                    set1,
                    inputFileLocations1,
                    inputFileLocations2,
                    inputFileLocations3,
                    inputFileLocations4,
                    bl,
                    openClassesDoneCallback,
                    operationStatusCallback
            );
            new Thread(openClassesTask).start();
        }
    }

    public void showWaitCursor() {
        if (SwingUtilities.isEventDispatchThread()) {
            this.applyWaitCursor();
        } else {
            SwingUtilities.invokeLater(new ShowWaitCursorTask(this));
        }
    }

    public void startObfuscation(
            List list1, List list2, ObfuscateOptions obfuscateOptions1, ScriptEnvironment scriptEnvironment1, OperationStatusCallback operationStatusCallback
    ) throws ZkmException, IOException {
        this.toFront();
        if (this.classRepository.hasNoClassesOpened()) {
            new MessageBoxDialog(this, "No classes opened", "There are no classes to obfuscate. Use the \"File | Open\" menu to open your classes.");
        } else if (!this.classRepository.hasProgramClasses()) {
            new MessageBoxDialog(
                    this,
                    "Only module-info.class files opened",
                    "The are no classes other than module-info to obfuscate. Use the \"File | Open\" menu to open your classes."
            );
        } else if (!this.classRepository.isOpenedWithoutErrors()) {
            new CopyableMessageDialog(
                    this,
                    "Opened classes not useable",
                    "Opened classes not useable for the reason shown below.",
                    this.classRepository.getOpenErrorMessage() + " : Please adjust your " + "Zelix KlassMaster" + " classpath or class file selections."
            );
        } else if (this.classRepository.isClasspathChanged()) {
            new MessageBoxDialog(this, "Classpath changed", "Classpath has been changed but the classes have not been reopened. You must reopen the classes. (H)");
        } else {
            Vector vector = new Vector(1);
            String string = ASTExcludeStatement.formatExcludeStatement(list1, 8);
            if (string != null) {
                ParameterListStatement parameterListStatement;
                try {
                    parameterListStatement = ASTExcludeStatement.parseDefaultExcludes(string, scriptEnvironment1);
                } catch (ZkmProcessingException zkmProcessingException1) {
                    scriptEnvironment1.logWarning(zkmProcessingException1.getMessage());
                    new MessageBoxDialog(this, "Error", "Unexpected error in exclude parameters. (1) See the log for more detail.");
                    if (operationStatusCallback != null) {
                        operationStatusCallback.setStatus((String) null);
                        operationStatusCallback.onDialogCancelled();
                    }

                    return;
                }

                vector.addElement(parameterListStatement);
            }

            Vector vector1 = new Vector(1);
            if (list2 != null && list2.size() > 0) {
                String string1 = ASTObfuscateReferencesIncludeStatement.formatIncludeStatement(list2, 27);
                if (string1 != null) {
                    ParameterListStatement parameterListStatement1;
                    try {
                        parameterListStatement1 = ASTObfuscateReferencesIncludeStatement.parseSingleInclude(string1, scriptEnvironment1);
                    } catch (ZkmProcessingException zkmProcessingException) {
                        scriptEnvironment1.logWarning(zkmProcessingException.getMessage());
                        new MessageBoxDialog(this, "Error", "Unexpected error in obfuscateReferencesInclude parameters. (1) See the log for more detail.");
                        if (operationStatusCallback != null) {
                            operationStatusCallback.setStatus((String) null);
                            operationStatusCallback.onDialogCancelled();
                        }

                        return;
                    }

                    vector1.addElement(parameterListStatement1);
                }
            }

            this.showWaitCursor();
            GuiMessageReporter guiMessageReporter = new GuiMessageReporter(this, scriptEnvironment1);
            TaskDoneCallback taskDoneCallback = new TaskDoneCallback(this, operationStatusCallback);
            GuiObfuscateTask guiObfuscateTask = new GuiObfuscateTask(
                    this, obfuscateOptions1, vector, vector1, guiMessageReporter, taskDoneCallback, scriptEnvironment1, operationStatusCallback
            );
            new Thread(guiObfuscateTask).start();
        }
    }

    @Override
    public void handleObservedChange(ObservableModel observableModel1, Object object, Object object1, Object object2) throws ZkmException, IOException {
        new EdtCallbackInvoker(this, observableModel1, object, object1, object2);
    }

    public void showInitialHelper() throws ZkmException, IOException {
        if (!this.classpath.isRuntimeClassesFound()) {
            String string = this.classpath.getClasspath();
            String string1 = FileSelectorDialog.selectBootstrapArchive(this, "Zelix KlassMaster - Select Java Bootstrap classes path", string);
            if (string1 != null && ZkmClasspath.isRuntimeArchivePath(string1)) {
                this.userPreferences.setClasspath(string1);
                this.userPreferences.savePreferences();
                this.classpath.appendClasspath(string1);
            }
        }

        HelperChoiceCallback helperChoiceCallback = new HelperChoiceCallback(this);
        this.showWaitCursor();
        this.setBusy(true);
        new OpenClassesChoiceDialog(this, this.userPreferences, helperChoiceCallback);
    }

    public void applyDefaultCursor() {
        Cursor cursor1 = Cursor.getDefaultCursor();
        this.setCursor(cursor1);
        this.classListPnl.setCursor(cursor1);
        this.classPropertiesPnl.setCursor(cursor1);
        this.secondaryPnl.setCursor(cursor1);
        this.tertiaryPnl.setCursor(cursor1);
        this.textViewPnl.setCursor(cursor1);
        this.classList.setCursor(cursor1);
        this.classPropertiesList.setCursor(cursor1);
        this.classTextArea.setCursor(cursor1);
    }

    public static void applyWaitCursorOn(ZkmMainWindow zkmMainWindow) {
        zkmMainWindow.applyWaitCursor();
    }

    public static ClassRepository accessClassRepository(ZkmMainWindow zkmMainWindow) {
        return zkmMainWindow.classRepository;
    }

    public void saveClasses(File file1, DialogCallback dialogCallback1) {
        this.userPreferences.setLastSavePath(file1.getAbsolutePath());
        this.userPreferences.savePreferences();
        if (this.classRepository.hasNoClassesOpened()) {
            new MessageBoxDialog(this, "No classes opened", "There are no classes to save. Use the \"File | Open\" menu to open your classes.");
            this.setBusy(false);
        } else if (!this.classRepository.isOpenedWithoutErrors()) {
            new CopyableMessageDialog(
                    this,
                    "Opened classes not useable",
                    "Opened classes not useable for the reason shown below.",
                    this.classRepository.getOpenErrorMessage() + " : Please adjust your " + "Zelix KlassMaster" + " classpath or class file selections."
            );
            this.setBusy(false);
        } else {
            this.setBusy(true);
            this.showWaitCursor();
            ScriptEnvironment scriptEnvironment1 = this.createScriptEnvironment();
            GuiMessageReporter guiMessageReporter = new GuiMessageReporter(this, scriptEnvironment1);
            MainWindowCompletionCallback mainWindowCompletionCallback = new MainWindowCompletionCallback(this, dialogCallback1);
            SaveClassesTask saveClassesTask = new SaveClassesTask(
                    this, file1, guiMessageReporter, scriptEnvironment1, mainWindowCompletionCallback, dialogCallback1
            );
            new Thread(saveClassesTask).start();
        }
    }

    public static SelectionHolder getSelectedClassHolder(ZkmMainWindow zkmMainWindow) {
        return zkmMainWindow.selectedClassHolder;
    }

    public RootPackageNode getRootPackage() {
        return this.classRepository.getRootPackageNode();
    }

    public ZkmMainWindow(
            String string,
            ClassRepository classRepository1,
            ObservableHolder observableHolder,
            ZkmClasspath zkmClasspath,
            PrintWriter printWriter,
            UserPreferences userPreferences1
    ) throws ZkmException, IOException {
        this.classRepository = classRepository1;
        this.statusHolder = observableHolder;
        this.classpath = zkmClasspath;
        this.logFileName = "ZKM_log.txt";
        this.logWriter = printWriter;
        this.userPreferences = userPreferences1;
        observableHolder.addObserver(this);
        this.buildWindow(string);
        this.showWaitCursor();
        this.classPropertiesListView = new ClassPropertiesListView(this.selectedClassHolder, this.classPropertiesList);
        this.textViewUpdater = new BrowserTextViewUpdater(this.selectedItemHolder, this.classTextArea, this.textScrollPane);
    }

    public void buildWindow(String string) {
        Container container1 = this.getContentPane();
        ConstraintLayout constraintLayout1 = new ConstraintLayout(container1);
        container1.setLayout(constraintLayout1);
        container1.setFont(this.windowFont);
        this.addWindowListener(new MainWindowCloseListener(this));
        this.mainPnl = new JPanel();
        this.rightSplitPnl = new JPanel();
        this.propertiesAreaPnl = new JPanel();
        this.statusPnl = new BevelBorderPanel(false);
        this.statusPnl.setLayout(new BorderLayout());
        this.classListPnl = new BevelBorderPanel(false);
        this.classPropertiesPnl = new BevelBorderPanel(false);
        this.secondaryPnl = new BevelBorderPanel(false);
        this.tertiaryPnl = new BevelBorderPanel(false);
        this.textViewPnl = new BevelBorderPanel(false);
        container1.add(this.mainPnl, "mainPnl");
        container1.add(this.statusPnl, "statusPnl");
        constraintLayout1.setConstraints(layoutConstraints);
        this.mainPnl.setLayout(new SplitLayout(0, 25, 4));
        this.mainPnl.add(this.classListPnl);
        this.mainPnl.add(this.rightSplitPnl);
        this.rightSplitPnl.setLayout(new SplitLayout(1, 40, 4));
        this.rightSplitPnl.add(this.propertiesAreaPnl);
        this.rightSplitPnl.add(this.textViewPnl);
        ConstraintLayout constraintLayout2 = new ConstraintLayout(this.propertiesAreaPnl);
        this.propertiesAreaPnl.setLayout(constraintLayout2);
        this.propertiesAreaPnl.add(this.classPropertiesPnl, "classPropertiesPnl");
        this.propertiesAreaPnl.add(this.secondaryPnl, "secondaryPnl");
        this.propertiesAreaPnl.add(this.tertiaryPnl, "tertiaryPnl");
        constraintLayout2.setConstraints(propertiesLayoutConstraints);
        this.classListPnl.setLayout(new BorderLayout());
        this.classList = new SelfRenderingList(new DefaultListModel());
        this.classList.setSelectionMode(0);
        this.classList.setToolTipText(GuiResources.getTooltipText("BROWSER_CLASS_HIERARCHY"));
        this.classList.addListSelectionListener(new BrowserClassListListener(this));
        this.classListPnl.add(new ZkmScrollPane(this.classList), "Center");
        this.classPropertiesPnl.setLayout(new BorderLayout());
        this.classPropertiesList = new PlatformAwareJList(new DefaultListModel());
        this.classPropertiesList.setSelectionMode(0);
        this.classPropertiesList.setToolTipText(GuiResources.getTooltipText("BROWSER_CLASS_PROPERTIES"));
        this.classPropertiesList.addListSelectionListener(new ClassPropertiesSelectionListener(this));
        this.classPropertiesPnl.add(new ZkmScrollPane(this.classPropertiesList), "Center");
        this.secondaryPnl.setLayout(new BorderLayout());
        this.tertiaryPnl.setLayout(new BorderLayout());
        this.textViewPnl.setLayout(new BorderLayout());
        this.classTextArea = new JTextArea();
        this.textScrollPane = new ZkmScrollPane(this.classTextArea);
        this.textViewPnl.add(this.textScrollPane, "Center");
        this.statusLabel = new JLabel("Status");
        this.statusPnl.add(this.statusLabel, "Center");
        MainMenuActionForwarder mainMenuActionForwarder = new MainMenuActionForwarder(this);
        int menuShortcutKeyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        JMenu jMenu = new JMenu("File");
        jMenu.setMnemonic('f');
        this.openItem = new JMenuItem("Open...");
        this.openItem.setMnemonic('o');
        int bd = menuShortcutKeyMask;
        this.openItem.setAccelerator(KeyStroke.getKeyStroke(79, bd));
        this.openItem.addActionListener(mainMenuActionForwarder);
        jMenu.add(this.openItem);
        this.saveAllItem = new JMenuItem("Save all...");
        this.saveAllItem.setMnemonic('s');
        int be = menuShortcutKeyMask;
        this.saveAllItem.setAccelerator(KeyStroke.getKeyStroke(83, be));
        this.saveAllItem.addActionListener(mainMenuActionForwarder);
        jMenu.add(this.saveAllItem);
        this.exitItem = new JMenuItem("Exit");
        this.exitItem.setMnemonic('e');
        int bf = menuShortcutKeyMask;
        this.exitItem.setAccelerator(KeyStroke.getKeyStroke(69, bf));
        this.exitItem.addActionListener(mainMenuActionForwarder);
        jMenu.add(this.exitItem);
        this.mainMenuBar.add(jMenu);
        JMenu jMenu1 = new JMenu("Tools");
        jMenu1.setMnemonic('t');
        this.trimItem = new JMenuItem("Trim...");
        this.trimItem.setMnemonic('t');
        int bg = menuShortcutKeyMask;
        this.trimItem.setAccelerator(KeyStroke.getKeyStroke(84, bg));
        this.trimItem.addActionListener(mainMenuActionForwarder);
        jMenu1.add(this.trimItem);
        this.obfuscateItem = new JMenuItem("Obfuscate...");
        this.obfuscateItem.setMnemonic('b');
        int bh = menuShortcutKeyMask;
        this.obfuscateItem.setAccelerator(KeyStroke.getKeyStroke(66, bh));
        this.obfuscateItem.addActionListener(mainMenuActionForwarder);
        jMenu1.add(this.obfuscateItem);
        this.buildHelperItem = new JMenuItem("Build Helper...");
        this.buildHelperItem.setMnemonic('u');
        int bi = menuShortcutKeyMask;
        this.buildHelperItem.setAccelerator(KeyStroke.getKeyStroke(85, bi));
        this.buildHelperItem.addActionListener(mainMenuActionForwarder);
        jMenu1.add(this.buildHelperItem);
        this.scriptHelperItem = new JMenuItem("ZKM Script Helper...");
        this.scriptHelperItem.setMnemonic('z');
        int bj = menuShortcutKeyMask;
        this.scriptHelperItem.setAccelerator(KeyStroke.getKeyStroke(90, bj));
        this.scriptHelperItem.addActionListener(mainMenuActionForwarder);
        jMenu1.add(this.scriptHelperItem);
        this.stackTraceTranslateItem = new JMenuItem("Stack Trace translate...");
        this.stackTraceTranslateItem.setMnemonic('c');
        int bk = menuShortcutKeyMask;
        this.stackTraceTranslateItem.setAccelerator(KeyStroke.getKeyStroke(67, bk));
        this.stackTraceTranslateItem.addActionListener(mainMenuActionForwarder);
        jMenu1.add(this.stackTraceTranslateItem);
        this.proGuardTranslateItem = new JMenuItem("ProGuard configuration translate...");
        this.proGuardTranslateItem.setMnemonic('d');
        int bl = menuShortcutKeyMask;
        this.proGuardTranslateItem.setAccelerator(KeyStroke.getKeyStroke(68, bl));
        this.proGuardTranslateItem.addActionListener(mainMenuActionForwarder);
        jMenu1.add(this.proGuardTranslateItem);
        this.mainMenuBar.add(jMenu1);
        JMenu jMenu2 = new JMenu("Options");
        jMenu2.setMnemonic('o');
        this.classpathItem = new JMenuItem("Classpath...");
        this.classpathItem.setMnemonic('p');
        int bm = menuShortcutKeyMask;
        this.classpathItem.setAccelerator(KeyStroke.getKeyStroke(80, bm));
        this.classpathItem.addActionListener(mainMenuActionForwarder);
        jMenu2.add(this.classpathItem);
        this.garbageCollectItem = new JMenuItem("Garbage Collect");
        this.garbageCollectItem.setMnemonic('g');
        int bn = menuShortcutKeyMask;
        this.garbageCollectItem.setAccelerator(KeyStroke.getKeyStroke(71, bn));
        this.garbageCollectItem.addActionListener(mainMenuActionForwarder);
        jMenu2.add(this.garbageCollectItem);
        this.mainMenuBar.add(jMenu2);
        JMenu jMenu3 = new JMenu("Help");
        jMenu3.setMnemonic('h');
        this.browserHelpItem = new JMenuItem("Browser help");
        this.browserHelpItem.setMnemonic('r');
        int bo = menuShortcutKeyMask;
        this.browserHelpItem.setAccelerator(KeyStroke.getKeyStroke(82, bo));
        this.browserHelpItem.addActionListener(mainMenuActionForwarder);
        jMenu3.add(this.browserHelpItem);
        this.overviewHelpItem = new JMenuItem("ZKM Overview");
        this.overviewHelpItem.setMnemonic('k');
        int bp = menuShortcutKeyMask;
        this.overviewHelpItem.setAccelerator(KeyStroke.getKeyStroke(75, bp));
        this.overviewHelpItem.addActionListener(mainMenuActionForwarder);
        jMenu3.add(this.overviewHelpItem);
        this.aboutItem = new JMenuItem("About");
        this.aboutItem.setMnemonic('a');
        int bq = menuShortcutKeyMask;
        this.aboutItem.setAccelerator(KeyStroke.getKeyStroke(65, bq));
        this.aboutItem.addActionListener(mainMenuActionForwarder);
        jMenu3.add(this.aboutItem);
        this.mainMenuBar.add(jMenu3);
        this.setJMenuBar(this.mainMenuBar);
        this.setTitle(string);
        this.setResizable(true);
        this.setIconImage(KlassMaster.getLogoImage(this));
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int br = dimension.width;
        int bt = Math.min(635, br);
        int bs = dimension.height - 50;
        this.setSize(bt, Math.min(475, bs));
        Dimension dimension1 = this.getSize();
        int bb = dimension.width / 2 - dimension1.width / 2;
        int bc = dimension.height / 2 - dimension1.height / 2;
        this.setLocation(bb, bc);
    }

    public void showMemberProperties(MemberInfo memberInfo1) throws ZkmException, IOException {
        this.showWaitCursor();
        this.listenerRegistry.removeObserversForKey("DescListObservers");
        this.tertiaryPnl.removeAll();
        MemberPropertiesPanel memberPropertiesPanel = null;
        if (memberInfo1 instanceof MethodInfo) {
            MethodInfo methodInfo1 = (MethodInfo) memberInfo1;
            ListenerRegistry listenerRegistry1 = this.listenerRegistry;
            ZkmMainWindow zkmMainWindow1 = this;
            memberPropertiesPanel = new MethodPropertiesPanel(methodInfo1, this.classRepository, zkmMainWindow1, listenerRegistry1);
            this.selectedItemHolder.setSelectedNode(methodInfo1);
            this.selectedMember = memberInfo1;
        } else if (memberInfo1 instanceof FieldInfo) {
            FieldInfo fieldInfo = (FieldInfo) memberInfo1;
            ListenerRegistry listenerRegistry2 = this.listenerRegistry;
            ZkmMainWindow zkmMainWindow2 = this;
            memberPropertiesPanel = new FieldModifierListModel(fieldInfo, this.classRepository, zkmMainWindow2, listenerRegistry2);
            this.selectedItemHolder.setSelectedNode(fieldInfo);
            this.selectedMember = memberInfo1;
        } else {
            this.selectedMember = memberInfo1;
        }

        MemberPropertiesPanel memberPropertiesPanel1 = memberPropertiesPanel;
        MemberPropertiesPanel memberPropertiesPanel2 = memberPropertiesPanel1;
        memberInfo1.notifyObserverWithoutArgs(memberPropertiesPanel2);
        this.restoreDefaultCursor();
        this.tertiaryPnl.add(memberPropertiesPanel, "Center");
        this.tertiaryPnl.validate();
    }

    @Override
    public void handleChangeOnEdt(Object object, Object object1, Object object2, Object object3) throws ZkmException, IOException {
        ObservableModel observableModel1 = (ObservableModel) object;
        if (observableModel1 == this.classRepository) {
            Enumeration enumeration = this.classRepository.enumerateHierarchyNodes();
            DefaultListModel defaultListModel = new DefaultListModel();
            int ba = -1;
            int bb = 0;
            ProgramClass programClass1 = (ProgramClass) this.selectedClassHolder.getSelectedNode();

            while (enumeration.hasMoreElements()) {
                ClassHierarchyNode classHierarchyNode = (ClassHierarchyNode) enumeration.nextElement();
                if (programClass1 != null && classHierarchyNode.getProgramClass() == programClass1) {
                    ba = bb;
                }

                Serializable serializable = classHierarchyNode.getJavaClassName();
                StringBuffer stringBuffer = new StringBuffer();
                int depth = classHierarchyNode.getDepth();

                for (int i = 0; i < depth; i++) {
                    stringBuffer.append("      ");
                }

                String string2 = String.valueOf(stringBuffer) + serializable;
                boolean bl = !classHierarchyNode.isProgramClass();
                String string = string2;
                DisableableListItem disableableListItem = new DisableableListItem(string, bl);
                defaultListModel.addElement(disableableListItem);
                bb++;
            }

            this.classList.setModel(defaultListModel);
            if (ba != -1) {
                this.classList.setSelectedIndex(ba);
                this.classList.ensureIndexIsVisible(ba);
            } else if (programClass1 != null) {
                this.selectedClassIndex = -1;
                this.selectedClassHolder.setSelectedNode((VisitableNode) null);
                this.selectedItemHolder.setSelectedNode((VisitableNode) null);
                this.secondaryPnl.removeAll();
                this.tertiaryPnl.removeAll();
                this.listenerRegistry.removeAllObservers();
            }

            if (this.classList.getModel().getSize() > 0 && this.classList.getSelectedIndex() == -1) {
                this.selectedClassIndex = 0;
                this.classList.setSelectedIndex(0);
                this.classList.ensureIndexIsVisible(0);
            }

            this.classListPnl.validate();
        } else if (observableModel1 == this.statusHolder && this.statusLabel != null) {
            String string1 = (String) object1;
            if (string1.length() != 0) {
                this.statusLabel.setText(string1);
            } else {
                this.statusLabel.setText(" ");
            }
        } else if (observableModel1 instanceof MemberListNode) {
            int selectedIndex = this.memberList.getSelectedIndex();
            MemberListNode memberListNode1 = (MemberListNode) observableModel1;
            if (object1 == null) {
                memberListNode1.resetCursor();
                DefaultListModel defaultListModel1 = new DefaultListModel();

                while (memberListNode1.hasNextMember()) {
                    defaultListModel1.addElement(memberListNode1.nextMemberText());
                }

                this.memberList.setModel(defaultListModel1);
                if (selectedIndex != -1) {
                    int bh = memberListNode1.getMemberIndex(this.selectedMember);
                    if (bh > -1) {
                        this.memberList.setSelectedIndex(bh);
                        this.memberList.ensureIndexIsVisible(bh);
                    } else {
                        this.selectedMember = null;
                        this.tertiaryPnl.removeAll();
                        this.selectedItemHolder.setSelectedNode(this.selectedClassHolder.getSelectedNode());
                    }
                }

                this.secondaryPnl.validate();
            } else if (object1 instanceof MutableInt
                    && ((MutableInt) object1).getValue() == 0
                    && (this.selectedPropertyIndex == 1 && object2 instanceof FieldInfo || this.selectedPropertyIndex == 2 && object2 instanceof MethodInfo)) {
                MemberInfo memberInfo1 = (MemberInfo) object2;
                int bi = memberListNode1.getMemberIndex(memberInfo1);
                if (bi != -1) {
                    ((DefaultListModel) this.memberList.getModel()).setElementAt(memberInfo1.getSourceName(), bi);
                }

                if (selectedIndex != -1) {
                    this.memberList.setSelectedIndex(selectedIndex);
                    this.memberList.ensureIndexIsVisible(selectedIndex);
                }
            }
        } else if (observableModel1 instanceof ClassConstantsNode) {
            ClassConstantsNode classConstantsNode = (ClassConstantsNode) observableModel1;
            int bf = this.memberList.getSelectedIndex();
            int size = this.memberList.getModel().getSize();
            if (object1 == null) {
                classConstantsNode.resetCursor();
                boolean bl1 = size > classConstantsNode.getConstantCount();
                DefaultListModel defaultListModel2 = new DefaultListModel();

                while (classConstantsNode.hasNextConstant()) {
                    defaultListModel2.addElement(classConstantsNode.nextConstantText());
                }

                this.memberList.setModel(defaultListModel2);
                if (bf != -1 && !bl1) {
                    this.memberList.setSelectedIndex(bf);
                }

                this.secondaryPnl.validate();
                if (bl1) {
                    this.tertiaryPnl.removeAll();
                }
            } else if (object1 instanceof MutableInt && ((MutableInt) object1).getValue() == 3) {
                LoadableConstant loadableConstant = (LoadableConstant) object2;
                int bj = classConstantsNode.getConstantIndex(loadableConstant);
                ((DefaultListModel) this.memberList.getModel()).setElementAt(loadableConstant.getEditableValue(), bj);
                this.memberList.setSelectedIndex(bf);
                this.memberList.ensureIndexIsVisible(bf);
            }
        }
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    public ScriptEnvironment createScriptEnvironment() {
        return new ScriptEnvironment(this.classRepository, this.classpath, this.logFileName, this.logWriter);
    }

    public ZkmClasspath getClasspath() {
        return this.classpath;
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    public void handleMenuAction(ActionEvent actionEvent) throws ZkmException, IOException {
        JMenuItem jMenuItem = (JMenuItem) actionEvent.getSource();
        if (jMenuItem == this.exitItem) {
            this.closeFrame();
        }

        if (!this.busy) {
            if (jMenuItem == this.openItem) {
                this.showOpenClassesDialog();
            } else if (jMenuItem == this.saveAllItem) {
                SaveClassesFolderCallback saveClassesFolderCallback = new SaveClassesFolderCallback(this);
                this.setBusy(true);
                new SaveClassesDialog(
                        this,
                        "Zelix KlassMaster - Select a Folder",
                        "OK",
                        GuiResources.getTooltipText("SAVE_CLASSES"),
                        this.userPreferences.getLastSavePath(),
                        saveClassesFolderCallback
                );
            } else if (jMenuItem == this.trimItem) {
                if (this.classRepository.isClasspathChanged()) {
                    new MessageBoxDialog(
                            this, "Classpath changed", "Classpath has been changed but the classes have not been reopened. You must reopen the classes. (D)"
                    );
                    return;
                }

                ScriptEnvironment scriptEnvironment1 = this.createScriptEnvironment();
                new TrimWizardController(this, this.classRepository, scriptEnvironment1);
            } else if (jMenuItem == this.obfuscateItem) {
                if (this.classRepository.isClasspathChanged()) {
                    new MessageBoxDialog(
                            this, "Classpath changed", "Classpath has been changed but the classes have not been reopened. You must reopen the classes. (E)"
                    );
                    return;
                }

                ScriptEnvironment scriptEnvironment2 = this.createScriptEnvironment();
                new ObfuscateWizardFlow(this, this.classRepository.getRootPackageNode(), scriptEnvironment2);
            } else if (jMenuItem == this.buildHelperItem) {
                ScriptEnvironment scriptEnvironment3 = this.createScriptEnvironment();
                new BuildHelperWizard(this, scriptEnvironment3, this.userPreferences);
            } else if (jMenuItem == this.scriptHelperItem) {
                ScriptEnvironment scriptEnvironment4 = this.createScriptEnvironment();
                new ScriptHelperWizard(this, scriptEnvironment4, this.userPreferences);
            } else if (jMenuItem == this.stackTraceTranslateItem) {
                this.setBusy(true);
                new StackTraceTranslateDialog(this, this.userPreferences);
            } else if (jMenuItem == this.proGuardTranslateItem) {
                this.setBusy(true);
                new ProGuardTranslateDialog(this, this.userPreferences);
            } else if (jMenuItem == this.classpathItem) {
                this.showClasspathDialog();
            } else if (jMenuItem == this.aboutItem) {
                new AboutDialog(this, "About Zelix KlassMaster" + KlassMaster.EVALUATION_SUFFIX, this.classRepository.getApplication());
            } else if (jMenuItem == this.browserHelpItem) {
                GuiResources.showHelpTopic("016");
            } else if (jMenuItem == this.overviewHelpItem) {
                GuiResources.showHelpTopic("015");
            } else if (jMenuItem == this.garbageCollectItem) {
                Runtime runtime1 = Runtime.getRuntime();
                int ba = (int) ((runtime1.totalMemory() - runtime1.freeMemory()) / 1024L);
                this.statusHolder.setValue(ba + "K in use before garbage collection");
                runtime1.gc();
            }
        }
    }

    public void showConstantEditor(NumericConstantEntry numericConstantEntry, SetMultiMap setMultiMap) {
        this.showWaitCursor();
        this.tertiaryPnl.removeAll();
        ConstantPoolEntryEditPanel constantPoolEntryEditPanel = new ConstantPoolEntryEditPanel(
                numericConstantEntry, this.classRepository, this.memberList, this, this.listenerRegistry, setMultiMap
        );
        this.restoreDefaultCursor();
        this.tertiaryPnl.add(constantPoolEntryEditPanel, "Center");
        this.tertiaryPnl.validate();
    }

    public static UserPreferences getUserPreferences(ZkmMainWindow zkmMainWindow) {
        return zkmMainWindow.userPreferences;
    }

    private static void staticInit() {
        layoutConstraints = new String[]{
                "mainPnl.top=0",
                "mainPnl.left=0",
                "mainPnl.right=container.right",
                "mainPnl.bottom=container.bottom-statusPnl.height",
                "statusPnl.left=0",
                "statusPnl.top=mainPnl.bottom",
                "statusPnl.right=container.right"
        };
        propertiesLayoutConstraints = new String[]{
                "classPropertiesPnl.width=container.width*33/100",
                "classPropertiesPnl.height=container.height",
                "classPropertiesPnl.top=0",
                "classPropertiesPnl.left=0",
                "secondaryPnl.width=container.width*33/100",
                "secondaryPnl.height=container.height",
                "secondaryPnl.top=0",
                "secondaryPnl.left=container.width*33/100",
                "tertiaryPnl.width=container.width*34/100",
                "tertiaryPnl.height=container.height",
                "tertiaryPnl.top=0",
                "tertiaryPnl.left=container.width*66/100"
        };
    }
}
