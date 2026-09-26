package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;

import java.io.IOException;

public class ASTFlattenPackageHierarchyOption extends ProGuardOptionNode {
    private static final String OPTION_NAME = "-flattenpackagehierarchy";

    public ASTFlattenPackageHierarchyOption() {
        super(46);
    }

    @Override
    public void translateOption(Object object) throws ZkmException, IOException {
        ProGuardConfigTranslator proGuardConfigTranslator = (ProGuardConfigTranslator) object;
        if (this.jjtGetNumChildren() == 1) {
            proGuardConfigTranslator.setFlattenPackageHierarchy(((ASTQuote122Clause) this.jjtGetChild(0)).getValue());
        } else {
            proGuardConfigTranslator.setFlattenPackageHierarchy("");
        }
    }

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }
}
