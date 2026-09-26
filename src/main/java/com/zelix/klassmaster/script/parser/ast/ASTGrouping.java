package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.ScriptStatementInfo;
import com.zelix.klassmaster.script.parser.ZkmScriptNode;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

public class ASTGrouping extends ZkmScriptSimpleNode implements ScriptStatementInfo {
    public Vector filterParameters = new Vector();

    @Override
    public void execute(Object object, Object object1) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object1;
        ZkmScriptNode zkmScriptNode = (ZkmScriptNode) object;
        int ba = this.jjtGetNumChildren();

        for (int i = 0; i < ba; i++) {
            ASTRenameFilterParameter aSTRenameFilterParameter = (ASTRenameFilterParameter) this.jjtGetChild(i);
            aSTRenameFilterParameter.execute(this, scriptEnvironment1);
            this.filterParameters.add(aSTRenameFilterParameter);
        }
    }

    public ASTGrouping() {
        super(42);
    }

    @Override
    public int getStatementLine() {
        return ((ASTGroupingsStatement) super.parent).getStatementLine();
    }

    @Override
    public String getStatementName() {
        return ((ASTGroupingsStatement) super.parent).getStatementName();
    }

    public Enumeration getFilterParameters() {
        return this.filterParameters.elements();
    }
}
