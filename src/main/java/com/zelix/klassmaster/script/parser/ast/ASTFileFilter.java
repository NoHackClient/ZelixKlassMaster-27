package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.archive.ArchivePathFilter;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.FileFilterComponentSink;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipException;

public class ASTFileFilter extends ZkmScriptSimpleNode implements ArchivePathFilter, FileFilterComponentSink {
    public LinkedList orClauses = new LinkedList();

    @Override
    public boolean acceptsPath(Object object) {
        String string = (String) object;
        boolean bl = false;
        Iterator iterator = this.orClauses.iterator();

        label21:
        while (iterator.hasNext()) {
            Iterator iterator1 = ((List) iterator.next()).iterator();

            while (iterator1.hasNext()) {
                if (!((FileFilterNodeBase) iterator1.next()).acceptsPath(string)) {
                    continue label21;
                }
            }

            bl = true;
            break;
        }

        return bl;
    }

    @Override
    public void execute(Object object1, Object object) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        int ba = this.jjtGetNumChildren();

        for (int i = 0; i < ba; i++) {
            this.jjtGetChild(i).execute(this, scriptEnvironment1);
        }
    }

    @Override
    public void addFileFilterComponent(Object object) {
        LinkedList linkedList = new LinkedList();
        linkedList.add(object);
        this.orClauses.add(linkedList);
    }

    public void addAndTerm(Object object) {
        ((List) this.orClauses.getLast()).add(object);
    }

    @Override
    public String toFilterExpression() throws ZipException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        boolean bl = true;
        Iterator iterator = this.orClauses.iterator();

        while (iterator.hasNext()) {
            List list1 = (List) iterator.next();
            if (!bl) {
                stringBuilder.append(" || ");
            } else {
                bl = false;
            }

            boolean bl1 = true;
            Iterator iterator1 = list1.iterator();

            while (iterator1.hasNext()) {
                FileFilterNodeBase fileFilterNodeBase = (FileFilterNodeBase) iterator1.next();
                if (!bl1) {
                    stringBuilder.append(" && ");
                } else {
                    bl1 = false;
                }

                stringBuilder.append(fileFilterNodeBase.toFilterExpression());
            }
        }

        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    public ASTFileFilter() {
        super(7);
    }

    public void addOrTerm(FileFilterNodeBase fileFilterNodeBase) {
        this.addFileFilterComponent(fileFilterNodeBase);
    }
}
