package com.zelix.klassmaster.changelog.parser.ast;

import com.zelix.klassmaster.changelog.AbstractChangeLog;
import com.zelix.klassmaster.changelog.TypeNameSetter;
import com.zelix.klassmaster.changelog.parser.ChangeLogNode;
import com.zelix.klassmaster.changelog.parser.ChangeLogSimpleNode;
import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;
import java.util.Vector;

public class ChangeLogASTQualifiedType extends ChangeLogSimpleNode {
    public int arrayDimensions;
    public Vector nameParts = new Vector();
    private static final String ARRAY_SUFFIX = "[]";

    public ChangeLogASTQualifiedType() {
        super(31);
    }

    @Override
    public void interpret(ChangeLogNode changeLogNode, int bd, int ba, int be, AbstractChangeLog abstractChangeLog) throws ZkmException, IOException {
        int bb = this.jjtGetNumChildren();

        for (int i = 0; i < bb; i++) {
            ChangeLogNode changeLogNode1 = this;
            if (ba >= 0) {
                changeLogNode1 = this.jjtGetChild(i);
            }

            changeLogNode1.interpret(this, 30872, 34067, 41973, abstractChangeLog);
        }

        TypeNameSetter typeNameSetter = (TypeNameSetter) changeLogNode;
        typeNameSetter.setTypeName(this.buildTypeName());
    }

    public String buildTypeName() {
        StringBuilder stringBuilder = new StringBuilder();
        int ba = this.nameParts.size();
        if (ba != 0) {
            for (int i = 0; i < ba; i++) {
                String string = (String) this.nameParts.elementAt(i);
                stringBuilder.append(string);
                if (i < this.nameParts.size() - 1) {
                    stringBuilder.append(".");
                }
            }
        }

        for (int i = 0; i < this.arrayDimensions; i++) {
            stringBuilder.append(ARRAY_SUFFIX);
        }

        return stringBuilder.toString();
    }

    public void addNamePart(String string) {
        this.nameParts.addElement(string);
    }

    public void incrementArrayDimensions() {
        this.arrayDimensions++;
    }
}
