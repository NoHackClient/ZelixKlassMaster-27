package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;

import java.io.IOException;

public abstract class PartiallySupportedProGuardOption extends ProGuardConfigOptionBase {
    @Override
    public final void translateOption(Object object) throws ZkmException, IOException {
    }

    @Override
    public final void translate(Object object1, Object object) throws ZkmException, IOException {
        ((ProGuardConfigTranslator) object).logWarning("ProGuard '" + this.getOptionName() + "' command is not directly supported.");
    }

    public PartiallySupportedProGuardOption(int ba) {
        super(ba);
    }
}
