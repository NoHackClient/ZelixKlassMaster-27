package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;
import com.zelix.klassmaster.proguard.ProGuardWildcardConverter;

import java.io.IOException;

public class ASTKeepPackageNamesOption extends ProGuardOptionNode {
    @Override
    public String getOptionName() {
        return "-keeppackagenames";
    }

    @Override
    public void translateOption(Object object) throws ZkmException, IOException {
        ProGuardConfigTranslator proGuardConfigTranslator = (ProGuardConfigTranslator) object;
        int ba = this.jjtGetNumChildren();
        if (ba > 0) {
            for (int i = 0; i < ba; i++) {
                String string = ((ASTQuote122Clause) this.jjtGetChild(i)).getValue();
                String string1 = "";
                String string2 = "";
                int bc;
                if ((bc = string.indexOf(":")) == -1 && (bc = string.indexOf("/")) == -1 && (bc = string.indexOf("\\")) == -1) {
                }

                if (bc > -1) {
                    proGuardConfigTranslator.logWarning(
                            "Package name in ProGuard '" + this.getOptionName() + "' command must not contain '" + string.charAt(bc) + "' character : '" + string + "'"
                    );
                } else {
                    if (string.charAt(0) == '!') {
                        string = string.substring(1);
                        string1 = "!(";
                        string2 = ")";
                    }

                    String string3 = ProGuardWildcardConverter.convertPackagePattern(string);
                    proGuardConfigTranslator.addExclude(string1 + string3 + string2);
                }
            }
        } else {
            proGuardConfigTranslator.addExclude("*.");
        }
    }

    public ASTKeepPackageNamesOption() {
        super(45);
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
    }
}
