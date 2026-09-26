package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.ZkmScriptTokenMgrError;
import com.zelix.klassmaster.script.parser.ZkmScriptParseException;
import com.zelix.klassmaster.script.parser.ZkmScriptParser;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;
import com.zelix.klassmaster.util.ZkmStringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

public class ASTTrimExcludeStatement extends ParameterListStatement {
    public static ParameterListStatement parseDefaultTrimExclude(String string, ScriptEnvironment scriptEnvironment1) throws ZkmException, IOException {
        BufferedReader bufferedReader = new BufferedReader(new StringReader(string));

        try {
            ZkmScriptParser zkmScriptParser = new ZkmScriptParser(bufferedReader);
            ZkmScriptSimpleNode zkmScriptSimpleNode = zkmScriptParser.DefaultTrimExcludeInput();
            zkmScriptSimpleNode.execute(null, scriptEnvironment1);
            return ((ASTDefaultTrimExcludeInput) zkmScriptSimpleNode).getParameterListStatement();
        } catch (ZkmScriptParseException zkmScriptParseException) {
            throw new ZkmProcessingException(zkmScriptParseException.toString());
        } catch (ZkmScriptTokenMgrError zkmScriptTokenMgrError) {
            throw new ZkmProcessingException(zkmScriptTokenMgrError.toString());
        }
    }

    @Override
    public void executeStatement(Object object, Object object1, Object object2, Object object3) throws ZkmException, IOException {
        int bb = (Integer) object1;
        int ba = (Integer) object3;
        int bc = (Integer) object2;
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        scriptEnvironment1.addTrimExcludeStatement(this);
        this.printMessageSummary(scriptEnvironment1, bb, bc, ba, "while executing");
    }

    public static String formatTrimExcludeStatement(List list1, int ba) {
        int bb = list1.size();
        if (bb > 0) {
            String string = ZkmStringUtils.pad("", 76, ba, 32);
            StringBuffer stringBuffer = new StringBuffer(100);
            stringBuffer.append(HiddenOptionFlags.LINE_SEPARATOR + ZkmStringUtils.pad("trimExclude", 76, ba, 32));

            for (int i = 0; i < bb; i++) {
                String string1 = (String) list1.get(i);
                if (i > 0) {
                    stringBuffer.append(string);
                }

                stringBuffer.append(string1);
                if (i < bb - 1) {
                    stringBuffer.append(" and" + HiddenOptionFlags.LINE_SEPARATOR);
                } else {
                    stringBuffer.append(";" + HiddenOptionFlags.LINE_SEPARATOR);
                }
            }

            return stringBuffer.toString();
        } else {
            return null;
        }
    }

    @Override
    public String getStatementName() {
        return "trimExclude";
    }

    @Override
    public void printStartMessage(Object object) {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        String string = ZkmScriptSimpleNode.getTimestampPrefix() + " Setting trim exclusions...";
        scriptEnvironment1.getLogWriter().println(string);
        System.out.println(string);
    }

    public ASTTrimExcludeStatement() {
        super(29);
    }
}
