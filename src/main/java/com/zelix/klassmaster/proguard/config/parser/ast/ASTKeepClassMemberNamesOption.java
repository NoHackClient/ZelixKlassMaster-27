package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;
import com.zelix.klassmaster.proguard.config.parser.ProGuardConfigNode;

import java.io.IOException;

public class ASTKeepClassMemberNamesOption extends ProGuardKeepOptionBase {
    @Override
    public boolean appliesToClass() {
        return false;
    }

    @Override
    public final void translate(Object object, Object object1) throws ZkmException, IOException {
        ProGuardConfigTranslator proGuardConfigTranslator = (ProGuardConfigTranslator) object1;
        ProGuardConfigNode proGuardConfigNode = (ProGuardConfigNode) object;
        this.addItem("allowshrinking");
        super.translate(proGuardConfigNode, proGuardConfigTranslator);
    }

    @Override
    public String getOptionName() {
        return "-keepclassmembernames";
    }

    @Override
    public boolean requiresMatchingMembers() {
        return false;
    }

    public ASTKeepClassMemberNamesOption() {
        super(19);
    }
}
