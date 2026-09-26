package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;
import com.zelix.klassmaster.proguard.config.parser.ProGuardConfigNode;
import com.zelix.klassmaster.proguard.config.parser.ProGuardConfigSimpleNode;

import java.io.IOException;

public abstract class ProGuardValueNode extends ProGuardConfigSimpleNode {
    public String value;

    public final String getValue() {
        return this.value;
    }

    public ProGuardValueNode(int ba) {
        super(ba);
    }

    @Override
    public void translate(Object object, Object object1) throws ZkmException, IOException {
        ProGuardConfigNode proGuardConfigNode = (ProGuardConfigNode) object;
        ProGuardConfigTranslator proGuardConfigTranslator = (ProGuardConfigTranslator) object1;
        super.translate(proGuardConfigNode, proGuardConfigTranslator);
    }

    public final void setValue(String string) {
        this.value = string.trim();
    }
}
