package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.NamePatternEntry;
import com.zelix.klassmaster.proguard.ProGuardClassNameListOwner;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;
import com.zelix.klassmaster.proguard.ProGuardWildcardConverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ASTDontWarnOption extends ProGuardOptionNode implements ProGuardClassNameListOwner {
    public List classNamePatterns = new ArrayList();

    @Override
    public void translateOption(Object object) throws ZkmException, IOException {
        ProGuardConfigTranslator proGuardConfigTranslator = (ProGuardConfigTranslator) object;
        if (this.classNamePatterns.size() == 0) {
            proGuardConfigTranslator.addIgnoreMissingReference("*.* +");
        } else {
            Iterator iterator = this.classNamePatterns.iterator();

            while (iterator.hasNext()) {
                NamePatternEntry namePatternEntry = (NamePatternEntry) iterator.next();
                if (!namePatternEntry.getPattern().equals("module-info")) {
                    proGuardConfigTranslator.addIgnoreMissingReference(namePatternEntry.getPattern() + " +");
                }
            }
        }
    }

    @Override
    public void translate(Object object1, Object object) throws ZkmException, IOException {
        ProGuardConfigTranslator proGuardConfigTranslator = (ProGuardConfigTranslator) object;
        ProGuardConfigOptionBase proGuardConfigOptionBase = this.getPreviousOption();
        if (proGuardConfigOptionBase != null && proGuardConfigOptionBase instanceof ASTIfOption) {
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

        Iterator iterator = this.classNamePatterns.iterator();

        while (iterator.hasNext()) {
            NamePatternEntry namePatternEntry = (NamePatternEntry) iterator.next();
            if (namePatternEntry.isNegated()) {
                proGuardConfigTranslator.logWarning(
                        "ProGuard negated class specification '!"
                                + namePatternEntry.getPattern()
                                + "' is not supported in '"
                                + this.getOptionName()
                                + "' at line "
                                + this.getLineNumber()
                                + "."
                );
                iterator.remove();
            } else {
                String string = ProGuardWildcardConverter.convertClassPattern(namePatternEntry.getPattern());
                namePatternEntry.setPattern(string);
            }
        }

        proGuardConfigTranslator.getMessageCount();
        proGuardConfigTranslator.getWarningCount();
        proGuardConfigTranslator.getErrorCount();
        this.translateOption(proGuardConfigTranslator);
    }

    public ASTDontWarnOption() {
        super(60);
    }

    @Override
    public String getOptionName() {
        return "-dontwarn";
    }

    @Override
    public void addClassNamePattern(Object object) {
        this.classNamePatterns.add(object);
    }
}
