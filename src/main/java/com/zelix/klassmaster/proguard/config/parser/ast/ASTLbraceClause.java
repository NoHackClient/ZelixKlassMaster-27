package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.NamePatternEntry;
import com.zelix.klassmaster.proguard.ProGuardClassNameClause;
import com.zelix.klassmaster.proguard.ProGuardClassNameListOwner;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;
import com.zelix.klassmaster.proguard.ProGuardModifierReceiver;
import com.zelix.klassmaster.proguard.ProGuardWildcardConverter;
import com.zelix.klassmaster.proguard.config.parser.ProGuardConfigSimpleNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ASTLbraceClause extends ProGuardConfigSimpleNode implements ProGuardClassNameClause, ProGuardModifierReceiver, ProGuardClassNameListOwner {
    public String implementsClassName;
    public String extendsAnnotation;
    public String implementsAnnotation;
    public String extendsClassName;
    public String annotationName;
    public List modifiers = new ArrayList();
    public List classNamePatterns = new ArrayList();
    public List fieldSpecs = new ArrayList();
    public List methodSpecs = new ArrayList();

    @Override
    public void setAnnotationName(String string) {
        this.annotationName = string;
    }

    public boolean hasModifiers() {
        return !this.modifiers.isEmpty();
    }

    public ASTLbraceClause() {
        super(74);
    }

    public boolean hasAnnotation() {
        return this.annotationName != null && this.annotationName.length() > 0;
    }

    @Override
    public final void translate(Object object1, Object object) throws ZkmException, IOException {
        ProGuardConfigTranslator proGuardConfigTranslator = (ProGuardConfigTranslator) object;

        for (int i = 0; i < this.jjtGetNumChildren(); i++) {
            this.jjtGetChild(i).translate(this, proGuardConfigTranslator);
        }

        ProGuardClassSpecification proGuardClassSpecification = (ProGuardClassSpecification) this.jjtGetParent();
        if (this.annotationName != null) {
            proGuardClassSpecification.setAnnotationText(this.buildAnnotationText());
        }

        if (this.modifiers.size() > 0) {
            proGuardClassSpecification.setModifiersText(this.buildModifiersText());
        }

        if (this.extendsClassName != null || this.implementsClassName != null) {
            proGuardClassSpecification.setInheritanceText(this.buildInheritanceText());
        }

        Iterator iterator = this.fieldSpecs.iterator();

        while (iterator.hasNext()) {
            String string = (String) iterator.next();
            proGuardClassSpecification.addMemberSpec(string);
        }

        iterator = this.methodSpecs.iterator();

        while (iterator.hasNext()) {
            String string2 = (String) iterator.next();
            proGuardClassSpecification.addMemberSpec(string2);
        }

        iterator = this.classNamePatterns.iterator();

        while (iterator.hasNext()) {
            NamePatternEntry namePatternEntry = (NamePatternEntry) iterator.next();
            if (namePatternEntry.isNegated() && !namePatternEntry.isInList()) {
                proGuardConfigTranslator.logWarning(
                        "ProGuard negated class specification '!"
                                + namePatternEntry.getPattern()
                                + "' is not supported outside of a comma separated list in '"
                                + proGuardClassSpecification.getOptionName()
                                + "' at line "
                                + proGuardClassSpecification.getLineNumber()
                                + "."
                );
            } else {
                String string1 = ProGuardWildcardConverter.convertClassPattern(namePatternEntry.getPattern());
                namePatternEntry.setPattern(string1);
                proGuardClassSpecification.addClassNamePattern(namePatternEntry);
            }
        }
    }

    public void setExtendsAnnotation(String string, ProGuardConfigTranslator proGuardConfigTranslator) {
        if (this.extendsAnnotation != null) {
            proGuardConfigTranslator.logWarning(
                    "More than one 'extends' clause annotation in class specification : '" + string + "' will replace '" + this.extendsAnnotation + "'"
            );
        }

        this.extendsAnnotation = string;
    }

    public void setImplementsClassName(String string) {
        this.implementsClassName = string;
    }

    public List getClassNamePatterns() {
        return this.classNamePatterns;
    }

    public boolean hasInheritanceClause() {
        return this.extendsClassName != null || this.implementsClassName != null;
    }

    @Override
    public void addClassNamePattern(Object object) {
        this.classNamePatterns.add(object);
    }

    public List getMethodSpecs() {
        return this.methodSpecs;
    }

    public void addFieldSpec(Object object) {
        this.fieldSpecs.add(object);
    }

    public String buildModifiersText() {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator iterator = this.modifiers.iterator();

        while (iterator.hasNext()) {
            String string = (String) iterator.next();
            stringBuilder.append(string);
            stringBuilder.append(' ');
        }

        return stringBuilder.toString();
    }

    public String getAnnotationText(int ba, int bb, int bc) {
        return this.buildAnnotationText();
    }

    public void setImplementsAnnotation(String string, ProGuardConfigTranslator proGuardConfigTranslator) {
        if (this.implementsAnnotation != null) {
            proGuardConfigTranslator.logWarning(
                    "More than one 'implements' clause in class specification : '" + string + "' will replace '" + this.implementsAnnotation + "'"
            );
        }

        this.implementsAnnotation = string;
    }

    public String getInheritanceText(long ba) {
        return this.buildInheritanceText();
    }

    public String buildAnnotationText() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('@');
        stringBuilder.append(ProGuardWildcardConverter.convertClassPattern(this.annotationName));
        stringBuilder.append(' ');
        return stringBuilder.toString();
    }

    public void addMethodSpec(Object object) {
        this.methodSpecs.add(object);
    }

    @Override
    public void addModifier(Object object) {
        this.modifiers.add(object);
    }

    public List getFieldSpecs() {
        return this.fieldSpecs;
    }

    public String buildInheritanceText() {
        StringBuilder stringBuilder = new StringBuilder();
        if (this.extendsClassName != null) {
            stringBuilder.append(" extends ");
            if (this.extendsAnnotation != null) {
                stringBuilder.append("@");
                stringBuilder.append(ProGuardWildcardConverter.convertClassPattern(this.extendsAnnotation));
                stringBuilder.append(' ');
            }

            stringBuilder.append(ProGuardWildcardConverter.convertClassPattern(this.extendsClassName));
        }

        if (this.implementsClassName != null) {
            stringBuilder.append(" implements ");
            StringBuilder stringBuilder1;
            String string;
            if (this.implementsAnnotation != null) {
                stringBuilder.append("@");
                stringBuilder.append(ProGuardWildcardConverter.convertClassPattern(this.implementsAnnotation));
                stringBuilder.append(' ');
                stringBuilder1 = stringBuilder;
                string = this.implementsClassName;
            } else {
                stringBuilder1 = stringBuilder;
                string = this.implementsClassName;
            }

            stringBuilder1.append(ProGuardWildcardConverter.convertClassPattern(string));
        }

        return stringBuilder.toString();
    }

    public String getModifiersText(long ba) {
        return this.buildModifiersText();
    }

    public void setExtendsClassName(String string, ProGuardConfigTranslator proGuardConfigTranslator) {
        if (this.extendsClassName != null) {
            proGuardConfigTranslator.logWarning(
                    "More than one 'extends' clause in class specification : '" + string + "' will replace '" + this.extendsClassName + "'"
            );
        }

        this.extendsClassName = string;
    }

    public boolean hasFieldSpecs() {
        return !this.fieldSpecs.isEmpty();
    }

    public boolean hasMethodSpecs() {
        return !this.methodSpecs.isEmpty();
    }
}
