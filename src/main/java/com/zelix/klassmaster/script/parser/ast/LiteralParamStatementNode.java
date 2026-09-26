package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptNode;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class LiteralParamStatementNode extends SummarizingStatementNode {
    public Map namedValues = ZkmUtils.createHashMap();
    public List literalValues = new ArrayList();

    public LiteralParamStatementNode() {
        super(57);
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
            if (zkmScriptNode instanceof ZkmScriptASTStringLiteral) {
                ZkmScriptASTStringLiteral zkmScriptASTStringLiteral = (ZkmScriptASTStringLiteral) zkmScriptNode;
                String string = zkmScriptASTStringLiteral.getValue();
                if (this.literalValues.contains(string)) {
                    scriptEnvironment1.logWarning(
                            "\""
                                    + string
                                    + "\" appears more than once in \""
                                    + this.getStatementName()
                                    + "\" statement at line "
                                    + this.getStatementLine()
                                    + ". First occurrence will be used."
                    );
                } else {
                    this.literalValues.add(string);
                }
            } else if (zkmScriptNode instanceof SingleValueParameterNode) {
                SingleValueParameterNode singleValueParameterNode1 = (SingleValueParameterNode) zkmScriptNode;
                ZkmScriptNode zkmScriptNode1 = singleValueParameterNode1.jjtGetChild(0);
                zkmScriptNode1.execute(this, scriptEnvironment1);
                SingleValueParameterNode singleValueParameterNode = (SingleValueParameterNode) zkmScriptNode1;
                String string1 = ((java.lang.String) (this.namedValues.put(singleValueParameterNode.getParameterName(), singleValueParameterNode.getValue(0))));
                if (string1 != null) {
                    scriptEnvironment1.logWarning(
                            "\""
                                    + singleValueParameterNode.getParameterName()
                                    + "\" appears more than once in \""
                                    + this.getStatementName()
                                    + "\" statement at line "
                                    + this.getStatementLine()
                                    + ".  First occurrence will be used."
                    );
                    this.namedValues.put(singleValueParameterNode.getParameterName(), string1);
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
}
