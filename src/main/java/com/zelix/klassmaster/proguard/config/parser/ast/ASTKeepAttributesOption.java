package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;
import com.zelix.klassmaster.proguard.ProGuardWildcardConverter;
import com.zelix.klassmaster.util.ZkmStringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ASTKeepAttributesOption extends ProGuardOptionNode {
    public List attributeNames = new ArrayList();

    @Override
    public String getOptionName() {
        return "-keepattributes";
    }

    public ASTKeepAttributesOption() {
        super(48);
    }

    public void addAttributeName(Object object) {
        this.attributeNames.add(object);
    }

    @Override
    public void translateOption(Object object) throws ZkmException, IOException {
        ProGuardConfigTranslator proGuardConfigTranslator = (ProGuardConfigTranslator) object;
        Iterator iterator = this.attributeNames.iterator();

        while (iterator.hasNext()) {
            String string = (String) iterator.next();
            if (string.charAt(0) == '!') {
                proGuardConfigTranslator.logWarning("Negated attribute in ProGuard not supported : '" + string + "'");
            } else {
                string = ProGuardWildcardConverter.collapseWildcards(string);
                string = ZkmStringUtils.replaceAll(string, "?", "[a-zA-Z]");
                string = ZkmStringUtils.replaceAll(string, "*", "[a-zA-Z]*");
                proGuardConfigTranslator.addKeepAttributePattern(string);
            }
        }
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
