package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.exclude.PackageNamePattern;
import com.zelix.klassmaster.obfuscator.exclude.SimpleNamePattern;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.TypeTextHolder;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;

public class ASTStandAloneAnnotation extends ZkmScriptSimpleNode implements TypeTextHolder {
    public String annotationName;

    public ASTStandAloneAnnotation() {
        super(178);
    }

    @Override
    public void execute(Object object, Object object1) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object1;
        this.jjtGetNumChildren();
        this.jjtGetChild(0).execute(this, scriptEnvironment1);
        ASTRenameFilterParameter aSTRenameFilterParameter = (ASTRenameFilterParameter) object;
        if (this.annotationName != null) {
            aSTRenameFilterParameter.markStandaloneAnnotation();
            String string = ClassFileBase.getPackagePath(this.annotationName);
            String string1 = ClassFileBase.stripPackage(this.annotationName);
            if (string.length() > 0) {
                PackageNamePattern packageNamePattern = new PackageNamePattern(string);
                aSTRenameFilterParameter.setPackagePattern(packageNamePattern);
            }

            ASTRenameFilterParameter aSTRenameFilterParameter1;
            String string2;
            if (string1.length() > 0) {
                SimpleNamePattern simpleNamePattern = new SimpleNamePattern(string1);
                aSTRenameFilterParameter.setClassNamePattern(simpleNamePattern);
                aSTRenameFilterParameter1 = aSTRenameFilterParameter;
                string2 = "java/lang/annotation/Annotation";
            } else {
                aSTRenameFilterParameter1 = aSTRenameFilterParameter;
                string2 = "java/lang/annotation/Annotation";
            }

            aSTRenameFilterParameter1.addImplementsName(string2);
            aSTRenameFilterParameter.addImplementsAnnotation(null);
            aSTRenameFilterParameter.addClassModifier("annotation");
        }
    }

    @Override
    public void setTypeText(Object object) {
        this.annotationName = (String) object;
    }
}
