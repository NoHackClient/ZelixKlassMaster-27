package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.log.MessageReporter;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.DialogCallback;

import java.io.File;
import java.io.IOException;

public class ASTSaveAllStatement extends SaveStatementNode {
    private static final String STATEMENT_NAME = "saveAll";

    @Override
    public String getStatementName() {
        return STATEMENT_NAME;
    }

    @Override
    public void saveClasses(
            ClassRepository classRepository1, File file1, MessageReporter messageReporter1, ScriptEnvironment scriptEnvironment1, DialogCallback dialogCallback1
    ) throws ZkmException, IOException {
        classRepository1.saveAll(
                this.getArchiveCompression(),
                this.isDeleteEmptyDirectories(),
                this.isDeleteXmlComments(),
                this.getLastModifiedTime(),
                file1,
                messageReporter1,
                scriptEnvironment1,
                dialogCallback1
        );
    }
}
