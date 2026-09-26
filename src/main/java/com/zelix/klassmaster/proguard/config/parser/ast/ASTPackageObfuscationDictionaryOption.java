package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;

import java.io.IOException;

public class ASTPackageObfuscationDictionaryOption extends ProGuardOptionNode {
    private static final String OPTION_NAME = "-packageobfuscationdictionary";

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }

    public ASTPackageObfuscationDictionaryOption() {
        super(41);
    }

    @Override
    public void translateOption(Object object) throws ZkmException, IOException {
        ProGuardConfigTranslator proGuardConfigTranslator = (ProGuardConfigTranslator) object;
        if (this.jjtGetNumChildren() > 0) {
            String string = ((ASTQuote122Clause) this.jjtGetChild(0)).getValue();
            proGuardConfigTranslator.setPackageObfuscationDictionary(string);
        }
    }
}
