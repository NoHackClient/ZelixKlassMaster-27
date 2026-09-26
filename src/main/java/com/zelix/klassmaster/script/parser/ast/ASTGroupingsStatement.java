package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ASTGroupingsStatement extends ParameterListStatement {
    @Override
    public void executeStatement(Object object, Object object1, Object object2, Object object3) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        int ba = (Integer) object1;
        int bc = (Integer) object3;
        int bb = (Integer) object2;
        scriptEnvironment1.addGroupingsStatement(this);
        this.printMessageSummary(scriptEnvironment1, ba, bb, bc, "while executing");
    }

    @Override
    public void printStartMessage(Object object) {
        PrintWriter printWriter = ((ScriptEnvironment) object).getLogWriter();
        String string = ZkmScriptSimpleNode.getTimestampPrefix() + " Setting groupings...";
        printWriter.println(string);
        System.out.println(string);
    }

    public ArrayList getGroupings() {
        ArrayList arrayList = new ArrayList();
        if (super.parameters != null) {
            int ba = 0;
            int bb = 0;

            for (List list1 = super.parameters; bb < list1.size(); list1 = super.parameters) {
                ASTGrouping aSTGrouping = (ASTGrouping) super.parameters.get(ba);
                ArrayList arrayList1 = new ArrayList();
                Enumeration enumeration = aSTGrouping.getFilterParameters();

                while (enumeration.hasMoreElements()) {
                    ASTRenameFilterParameter aSTRenameFilterParameter = (ASTRenameFilterParameter) enumeration.nextElement();
                    arrayList1.add(aSTRenameFilterParameter);
                }

                arrayList.add(arrayList1);
                bb = ++ba;
            }
        }

        return arrayList;
    }

    @Override
    public String getStatementName() {
        return "groupings";
    }

    public ASTGroupingsStatement() {
        super(41);
    }
}
