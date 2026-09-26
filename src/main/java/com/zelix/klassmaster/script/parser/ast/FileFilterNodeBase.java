package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.archive.ArchivePathFilter;
import com.zelix.klassmaster.archive.ZkmFileUtils;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.FileFilterComponentSink;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;
import com.zelix.klassmaster.util.ZkmStringUtils;

import java.io.IOException;
import java.util.zip.ZipException;

public abstract class FileFilterNodeBase extends ZkmScriptSimpleNode implements ArchivePathFilter {
    public String fileNamePattern;
    public String patternText;
    public String directoryPattern;
    public final boolean negated;

    public String normalizePath(String string) {
        String string1 = string.replace('\\', '/');
        boolean bl;
        if (ZkmFileUtils.FILE_SEPARATOR_CHAR != '\\') {
            string1 = string.replace(ZkmFileUtils.FILE_SEPARATOR_CHAR, '/');
            bl = ZkmFileUtils.caseSensitiveFileSystem;
        } else {
            bl = ZkmFileUtils.caseSensitiveFileSystem;
        }

        if (!bl) {
            string1 = string1.toUpperCase();
        }

        return string1;
    }

    public FileFilterNodeBase(int ba, boolean negated) {
        super(ba);
        this.negated = negated;
    }

    @Override
    public boolean acceptsPath(Object object) {
        boolean bl1;
        label21:
        {
            String string = (String) object;
            String string3 = this.normalizePath(string);
            int ba = lastSeparatorIndex(string3);
            String string4 = string3.substring(ba + 1);
            String string5 = string3.substring(0, ba + 1);
            String string1 = this.directoryPattern;
            String string2 = string5;
            if (ZkmStringUtils.matchesWildcard(string2, string1)) {
                string1 = this.fileNamePattern;
                string2 = string4;
                if (ZkmStringUtils.matchesWildcard(string2, string1)) {
                    bl1 = true;
                    break label21;
                }
            }

            bl1 = false;
        }

        boolean bl = bl1;
        return this.negated ? !bl : bl;
    }

    public static int lastSeparatorIndex(String string) {
        int ba = string.lastIndexOf(47);
        int bb = string.lastIndexOf("!");
        return Math.max(ba, bb);
    }

    @Override
    public String toFilterExpression() throws ZipException {
        return (this.negated ? "!" : "") + "\"" + this.patternText + "\"";
    }

    @Override
    public final void execute(Object object, Object object1) throws ZkmException, IOException {
        this.jjtGetNumChildren();
        ZkmScriptASTStringLiteral zkmScriptASTStringLiteral = (ZkmScriptASTStringLiteral) this.jjtGetChild(0);
        this.patternText = zkmScriptASTStringLiteral.getValue();
        String string = this.normalizePath(this.patternText);
        int ba = lastSeparatorIndex(string);
        this.directoryPattern = string.substring(0, ba + 1);
        this.directoryPattern = "*" + this.directoryPattern;
        this.fileNamePattern = string.substring(ba + 1);
        ((FileFilterComponentSink) object).addFileFilterComponent(this);
    }
}
