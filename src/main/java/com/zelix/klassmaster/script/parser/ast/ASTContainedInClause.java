package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.ScriptStatementInfo;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;

public class ASTContainedInClause extends ZkmScriptSimpleNode implements ScriptStatementInfo {
    public ASTRenameFilterParameter filterParameter;
    private static final String KEYWORD = "containedIn";

    @Override
    public int getStatementLine() {
        return ((ContainedInStatementBase) super.parent).getStatementLine();
    }

    @Override
    public String getStatementName() {
        return ((ContainedInStatementBase) super.parent).getStatementName();
    }

    @Override
    public void execute(Object object1, Object object) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        if (this.jjtGetNumChildren() > 0) {
            this.filterParameter = (ASTRenameFilterParameter) this.jjtGetChild(0);
            this.filterParameter.execute(this, scriptEnvironment1);
        }
    }

    public ASTContainedInClause() {
        super(148);
    }

    public String toScriptText() {
        int flowControlKey = ZkmScriptSimpleNode.getFlowControlKey();
        StringBuilder stringBuilder = new StringBuilder();
        int bb = this.jjtGetNumChildren();
        int ba = flowControlKey;
        stringBuilder.append(KEYWORD);
        StringBuilder stringBuilder1 = stringBuilder;
        if (ba != 0) {
            stringBuilder.append('{');
            if (bb > 0) {
                ASTRenameFilterParameter aSTRenameFilterParameter = (ASTRenameFilterParameter) this.jjtGetChild(0);
                stringBuilder.append(aSTRenameFilterParameter.toScriptText());
            }

            stringBuilder.append('}');
            stringBuilder1 = stringBuilder;
        }

        return stringBuilder1.toString();
    }

    public ASTRenameFilterParameter getFilterParameter() {
        return this.filterParameter;
    }
}
