package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.config.TrimExcludeSettings;
import com.zelix.klassmaster.config.TrimOptions;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ast.ASTTrimExcludeStatement;
import com.zelix.klassmaster.script.parser.ast.ParameterListStatement;
import com.zelix.klassmaster.ui.GuiMessageReporter;
import com.zelix.klassmaster.ui.ZkmMainWindow;
import com.zelix.klassmaster.util.DialogCallback;

import java.io.IOException;
import java.util.List;
import java.util.Vector;
import javax.swing.JButton;

public abstract class TrimExclusionsBaseDialog extends TrimExclusionsDialogBase {
    public JButton testBtn;

    public TrimExclusionsBaseDialog(
            String string,
            ZkmMainWindow zkmMainWindow,
            List list1,
            TrimExcludeSettings trimExcludeSettings1,
            ClassRepository classRepository1,
            TrimOptions trimOptions1,
            ScriptEnvironment scriptEnvironment1,
            DialogCallback dialogCallback1
    ) throws ZkmException, IOException {
        super(string, zkmMainWindow, list1, trimExcludeSettings1, classRepository1, trimOptions1, scriptEnvironment1, dialogCallback1);
    }

    public final void runTestTrim() throws ZkmException, IOException {
        this.saveParameters();
        List list1 = super.wizardSettings.getEntryList();
        if (super.classRepository.hasNoClassesOpened()) {
            new MessageBoxDialog(this, "No classes opened", "The are no classes opened. Use the \"File | Open\" menu on the Browser window.");
        } else if (!super.classRepository.hasProgramClasses()) {
            new MessageBoxDialog(
                    this,
                    "Only module-info.class files opened",
                    "The are no classes other than module-info opened. Use the \"File | Open\" menu on the Browser window."
            );
        } else {
            Vector vector = new Vector(1);
            String string = ASTTrimExcludeStatement.formatTrimExcludeStatement(list1, 12);
            if (string != null) {
                ParameterListStatement parameterListStatement;
                try {
                    parameterListStatement = ASTTrimExcludeStatement.parseDefaultTrimExclude(string, super.scriptEnvironment);
                } catch (ZkmProcessingException zkmProcessingException) {
                    super.scriptEnvironment.logWarning(zkmProcessingException.getMessage());
                    new MessageBoxDialog(this, "Error", "Unexpected error in exclude parameters. (3) See the log for more detail.");
                    return;
                }

                vector.addElement(parameterListStatement);
            }

            GuiMessageReporter guiMessageReporter = new GuiMessageReporter(this, super.scriptEnvironment);
            ExclusionDialogCallback exclusionDialogCallback = new ExclusionDialogCallback(this);
            TestTrimReportTask testTrimReportTask = new TestTrimReportTask(this, vector, guiMessageReporter, exclusionDialogCallback);
            this.showWaitCursor();
            new Thread(testTrimReportTask).start();
        }
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
    }
}
