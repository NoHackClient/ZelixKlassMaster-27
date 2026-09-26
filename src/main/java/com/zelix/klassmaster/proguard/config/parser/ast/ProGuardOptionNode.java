package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;

import java.io.IOException;

public abstract class ProGuardOptionNode extends ProGuardConfigOptionBase {
    public ProGuardOptionNode(int ba) {
        super(ba);
    }

    @Override
    public void translate(Object object1, Object object) throws ZkmException, IOException {
        ProGuardConfigTranslator proGuardConfigTranslator = (ProGuardConfigTranslator) object;
        ProGuardConfigOptionBase proGuardConfigOptionBase = this.getPreviousOption();
        if (proGuardConfigOptionBase != null && proGuardConfigOptionBase instanceof ASTIfOption && !(this instanceof ProGuardKeepOptionBase)) {
            ASTIfOption aSTIfOption = (ASTIfOption) proGuardConfigOptionBase;
            proGuardConfigTranslator.logError(
                    "ProGuard '"
                            + aSTIfOption.getOptionName()
                            + "' command at line "
                            + aSTIfOption.getLineNumber()
                            + " followed by '"
                            + this.getOptionName()
                            + "' rather than a keep command."
            );
        }

        for (int i = 0; i < this.jjtGetNumChildren(); i++) {
            this.jjtGetChild(i).translate(this, proGuardConfigTranslator);
        }

        proGuardConfigTranslator.getMessageCount();
        proGuardConfigTranslator.getWarningCount();
        proGuardConfigTranslator.getErrorCount();
        this.translateOption(proGuardConfigTranslator);
    }
}
