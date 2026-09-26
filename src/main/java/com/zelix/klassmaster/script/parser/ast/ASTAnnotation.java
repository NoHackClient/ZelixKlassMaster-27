package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.exclude.AnnotationMatcher;
import com.zelix.klassmaster.obfuscator.exclude.QualifiedNameMatcher;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptNode;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;
import com.zelix.klassmaster.util.ZkmStringUtils;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

public class ASTAnnotation extends ZkmScriptSimpleNode implements AnnotationMatcher {
    public String internalNamePattern;
    public String namePattern;

    public ASTAnnotation() {
        super(181);
    }

    @Override
    public String getSpecText() {
        return "@" + this.namePattern;
    }

    @Override
    public void execute(Object object1, Object object) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        StringBuilder stringBuilder = new StringBuilder();
        int ba = this.jjtGetNumChildren();

        for (int i = 0; i < ba; i++) {
            ZkmScriptNode zkmScriptNode = this.jjtGetChild(i);
            zkmScriptNode.execute(this, scriptEnvironment1);
            QualifiedNameMatcher qualifiedNameMatcher = (QualifiedNameMatcher) zkmScriptNode;
            stringBuilder.append(qualifiedNameMatcher.getSpecText());
        }

        this.namePattern = stringBuilder.toString();
        this.internalNamePattern = this.namePattern.replace('.', '/');
    }

    public boolean isLiteralName() {
        return this.internalNamePattern.indexOf("*") == -1;
    }

    @Override
    public boolean matchesAnyAnnotation(Set set1) {
        Iterator iterator = set1.iterator();

        while (iterator.hasNext()) {
            if (ZkmStringUtils.matchesWildcard((String) iterator.next(), this.internalNamePattern)) {
                return true;
            }
        }

        return false;
    }
}
