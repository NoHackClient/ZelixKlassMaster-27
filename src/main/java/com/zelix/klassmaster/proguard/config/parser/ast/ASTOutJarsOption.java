package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;

import java.io.IOException;
import java.util.Set;

public class ASTOutJarsOption extends ProGuardOptionNode {
    public ASTOutJarsOption() {
        super(4);
    }

    @Override
    public String getOptionName() {
        return "-outjars";
    }

    @Override
    public void translateOption(Object object) throws ZkmException, IOException {
        ProGuardConfigTranslator proGuardConfigTranslator = (ProGuardConfigTranslator) object;
        int ba = this.jjtGetNumChildren();

        for (int i = 0; i < ba; i++) {
            ASTTildeClause aSTTildeClause = (ASTTildeClause) this.jjtGetChild(i);
            Set set1 = aSTTildeClause.getPaths();
            proGuardConfigTranslator.addOutputArchives(set1);
            if (aSTTildeClause.hasFileFilters()) {
                proGuardConfigTranslator.logWarning(
                        "Filters not supported in ProGuard '" + this.getOptionName() + "' command : '" + aSTTildeClause.buildClassPathText() + "'"
                );
            }
        }
    }
}
