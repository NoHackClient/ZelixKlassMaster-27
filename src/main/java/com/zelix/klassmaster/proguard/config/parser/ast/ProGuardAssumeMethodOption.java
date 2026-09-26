package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.ProGuardClassNameEntry;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;

import java.io.IOException;
import java.util.Iterator;

public abstract class ProGuardAssumeMethodOption extends ProGuardClassSpecification {
    public void addAssumeMethodSpec(ProGuardConfigTranslator proGuardConfigTranslator, String string, boolean bl) {
        if (bl) {
            proGuardConfigTranslator.addRemoveMethodCallsExclude(string);
        } else {
            proGuardConfigTranslator.addRemoveMethodCallsInclude(string);
        }
    }

    public ProGuardAssumeMethodOption(int ba) {
        super(ba);
    }

    @Override
    public boolean appliesToClass() {
        return false;
    }

    @Override
    public boolean requiresMatchingMembers() {
        return false;
    }

    @Override
    public final void translateOption(Object object) throws ZkmException, IOException {
        ProGuardConfigTranslator proGuardConfigTranslator = (ProGuardConfigTranslator) object;
        super.classNameEntries = this.buildClassNameEntries(proGuardConfigTranslator);
        Iterator iterator = super.classNameEntries.iterator();

        while (iterator.hasNext()) {
            ProGuardClassNameEntry proGuardClassNameEntry = (ProGuardClassNameEntry) iterator.next();
            String string = proGuardClassNameEntry.getClassName();
            if (super.memberSpecs.size() == 0) {
                proGuardConfigTranslator.logWarning("ProGuard '" + this.getOptionName() + "' at line " + this.getLineNumber() + " requires a method specification.");
            } else {
                Iterator iterator1 = super.memberSpecs.iterator();

                while (iterator1.hasNext()) {
                    String string1 = (String) iterator1.next();
                    String string2 = string + ' ' + string1 + "";
                    this.addAssumeMethodSpec(proGuardConfigTranslator, string2, proGuardClassNameEntry.isNegated());
                }
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
