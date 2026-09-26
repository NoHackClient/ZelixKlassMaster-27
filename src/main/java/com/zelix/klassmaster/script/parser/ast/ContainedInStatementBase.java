package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptNode;

import java.io.IOException;
import java.util.List;

public abstract class ContainedInStatementBase extends ParameterListStatement {
    public ContainedInStatementBase(int ba) {
        super(ba);
    }

    @Override
    public void processParameters(int ba, ScriptEnvironment scriptEnvironment1) throws ZkmException, IOException {
        ASTContainedInClause aSTContainedInClause = null;

        for (int i = 0; i < ba; i++) {
            ZkmScriptNode zkmScriptNode = this.jjtGetChild(i);
            if (zkmScriptNode instanceof ASTContainedInClause) {
                aSTContainedInClause = (ASTContainedInClause) zkmScriptNode;
            } else {
                ASTRenameFilterParameter aSTRenameFilterParameter = (ASTRenameFilterParameter) zkmScriptNode;
                List list1;
                if (aSTContainedInClause != null) {
                    aSTRenameFilterParameter.setContainedInClause(aSTContainedInClause);
                    aSTContainedInClause = null;
                    list1 = super.parameters;
                } else {
                    list1 = super.parameters;
                }

                list1.add(aSTRenameFilterParameter);
            }

            zkmScriptNode.execute(this, scriptEnvironment1);
        }
    }
}
