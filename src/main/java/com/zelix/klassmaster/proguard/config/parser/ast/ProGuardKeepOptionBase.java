package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.ProGuardClassNameEntry;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;
import com.zelix.klassmaster.proguard.config.parser.ProGuardConfigNode;
import com.zelix.klassmaster.script.parser.ast.ASTRenameFilterParameter;
import com.zelix.klassmaster.util.ItemCollector;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

public abstract class ProGuardKeepOptionBase extends ProGuardClassSpecification implements ItemCollector {
    public Set keepModifiers = ZkmUtils.createHashSet();
    public boolean keepFromShrinking = true;
    public boolean keepFromObfuscation = true;
    public boolean includeDescriptorClasses = false;

    @Override
    public void translate(Object object, Object object1) throws ZkmException, IOException {
        ProGuardConfigNode proGuardConfigNode = (ProGuardConfigNode) object;
        ProGuardConfigTranslator proGuardConfigTranslator = (ProGuardConfigTranslator) object1;
        super.translate(proGuardConfigNode, proGuardConfigTranslator);
    }

    public void addKeepExclusion(ProGuardConfigTranslator proGuardConfigTranslator, String string, boolean bl) {
        if (this.keepFromShrinking) {
            if (bl) {
                proGuardConfigTranslator.addTrimUnexclude(string);
            } else {
                proGuardConfigTranslator.addTrimExclude(string);
            }
        }

        if (this.keepFromObfuscation) {
            if (bl) {
                proGuardConfigTranslator.addUnexclude(string);
            } else {
                proGuardConfigTranslator.addExclude(string);
            }
        }
    }

    @Override
    public final void translateOption(Object object) throws ZkmException, IOException {
        ProGuardConfigTranslator proGuardConfigTranslator = (ProGuardConfigTranslator) object;
        ProGuardConfigOptionBase proGuardConfigOptionBase = this.getPreviousOption();
        Set set1;
        if (proGuardConfigOptionBase != null) {
            if (proGuardConfigOptionBase instanceof ASTIfOption) {
                ASTIfOption aSTIfOption = (ASTIfOption) proGuardConfigOptionBase;
                this.setIfOption(aSTIfOption);
                set1 = this.keepModifiers;
            } else {
                set1 = this.keepModifiers;
            }
        } else {
            set1 = this.keepModifiers;
        }

        Iterator iterator1 = set1.iterator();

        while (iterator1.hasNext()) {
            String string = (String) iterator1.next();
            if (string.equals("allowshrinking")) {
                this.keepFromShrinking = false;
            } else if (string.equals("allowobfuscation")) {
                this.keepFromObfuscation = false;
            } else if (string.equals("includedescriptorclasses")) {
                this.includeDescriptorClasses = true;
            }
        }

        super.classNameEntries = this.buildClassNameEntries(proGuardConfigTranslator);
        iterator1 = super.classNameEntries.iterator();

        while (iterator1.hasNext()) {
            ProGuardClassNameEntry proGuardClassNameEntry = (ProGuardClassNameEntry) iterator1.next();
            String string1 = proGuardClassNameEntry.getClassName();
            if (super.memberSpecs.size() == 0) {
                this.addKeepExclusion(proGuardConfigTranslator, string1, proGuardClassNameEntry.isNegated());
            } else {
                Iterator iterator = super.memberSpecs.iterator();

                while (iterator.hasNext()) {
                    String string2 = (String) iterator.next();
                    String string3 = "";
                    if (this.includeDescriptorClasses) {
                        if (isMethodSpec(string2)) {
                            string3 = " +signatureClasses";
                        } else {
                            proGuardConfigTranslator.logWarning(
                                    "ProGuard keep modifier 'includedescriptorclasses' is not supported for fields in '"
                                            + this.getOptionName()
                                            + "' at line "
                                            + this.getLineNumber()
                                            + "."
                            );
                        }
                    }

                    String string4 = string1 + ' ' + string2 + string3;
                    this.addKeepExclusion(proGuardConfigTranslator, string4, proGuardClassNameEntry.isNegated());
                }

                if (this.appliesToClass() && !this.requiresMatchingMembers()) {
                    if (string1.charAt(string1.length() - 1) == ASTRenameFilterParameter.CARET_TAG) {
                        string1 = string1.substring(0, string1.length() - 1);
                    }

                    this.addKeepExclusion(proGuardConfigTranslator, string1, proGuardClassNameEntry.isNegated());
                }
            }
        }
    }

    public static boolean isMethodSpec(String string) {
        return string.indexOf("(") > -1;
    }

    @Override
    public final void addItem(Object object) {
        this.keepModifiers.add(object);
    }

    public ProGuardKeepOptionBase(int ba) {
        super(ba);
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
