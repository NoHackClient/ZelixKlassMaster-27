package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptNode;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.IOException;

public abstract class KeyValueStatementNode extends SummarizingStatementNode {
    public ListMultimap parameterValues = new ListMultimap(11, 3);

    public boolean isParameterAccepted(Object object, Object object1) {
        return true;
    }

    @Override
    public final void execute(Object object1, Object object) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        int ba = this.jjtGetNumChildren();
        int messageCount = scriptEnvironment1.getMessageCount();
        int warningCount = scriptEnvironment1.getWarningCount();
        int errorCount = scriptEnvironment1.getErrorCount();

        for (int i = 0; i < ba; i++) {
            ZkmScriptNode zkmScriptNode = this.jjtGetChild(i);
            if (zkmScriptNode instanceof DelegatingParameterNode) {
                DelegatingParameterNode delegatingParameterNode = (DelegatingParameterNode) zkmScriptNode;
                delegatingParameterNode.execute(this, scriptEnvironment1);
                String string = delegatingParameterNode.getParameterName();
                if (this.parameterValues.containsKey(string)) {
                    scriptEnvironment1.logWarning(
                            "\""
                                    + string
                                    + "\" appears more than once in \""
                                    + this.getStatementName()
                                    + "\" statement at line "
                                    + this.getStatementLine()
                                    + ".  First occurrence will be used."
                    );
                } else if (this.isParameterAccepted(delegatingParameterNode, scriptEnvironment1)) {
                    int valueCount = delegatingParameterNode.getValueCount();

                    for (int j = 0; j < valueCount; j++) {
                        this.parameterValues.addValue(string, delegatingParameterNode.getValue(j));
                    }
                }
            } else {
                scriptEnvironment1.logFatalError(
                        this.getClass().getName() + " had " + zkmScriptNode.getClass().getName() + " as child " + i + " at line " + this.getStatementLine() + "."
                );
            }
        }

        Integer integer1 = errorCount;
        Integer integer = warningCount;
        this.executeStatement(scriptEnvironment1, messageCount, integer, integer1);
    }

    public KeyValueStatementNode(int ba) {
        super(ba);
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
