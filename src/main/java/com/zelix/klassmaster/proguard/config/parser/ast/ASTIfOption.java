package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;
import com.zelix.klassmaster.proguard.config.parser.ProGuardConfigNode;

import java.io.IOException;

public class ASTIfOption extends ProGuardClassSpecification {
    private static final String OPTION_NAME = "-if";

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }

    @Override
    public boolean appliesToClass() {
        return false;
    }

    @Override
    public boolean requiresMatchingMembers() {
        return false;
    }

    public ASTLbraceClause getClassSpecClause() {
        return (ASTLbraceClause) this.jjtGetChild(0);
    }

    @Override
    public void translateOption(Object object) throws ZkmException, IOException {
        ASTLbraceClause aSTLbraceClause = (ASTLbraceClause) this.jjtGetChild(0);
    }

    public ASTIfOption() {
        super(13);
    }

    @Override
    public void translate(Object object, Object object1) throws ZkmException, IOException {
        ProGuardConfigTranslator proGuardConfigTranslator = (ProGuardConfigTranslator) object1;
        ProGuardConfigNode proGuardConfigNode = (ProGuardConfigNode) object;
        super.translate(proGuardConfigNode, proGuardConfigTranslator);
    }
}
