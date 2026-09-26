package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;

import java.io.IOException;

public class ASTIncludeOption extends ProGuardOptionNode {
    @Override
    public void translateOption(Object object) throws ZkmException, IOException {
        ((ProGuardConfigTranslator) object)
                .logSeriousError_v(
                        "ProGuard command '"
                                + this.getOptionName()
                                + "' should have already been expanded : '"
                                + (this.jjtGetNumChildren() > 0 ? ((ASTQuote122Clause) this.jjtGetChild(0)).getValue() : "")
                                + "'"
                );
    }

    public ASTIncludeOption() {
        super(2);
    }

    @Override
    public String getOptionName() {
        return "-include";
    }
}
