package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;
import com.zelix.klassmaster.proguard.ProGuardFilterReceiver;
import com.zelix.klassmaster.proguard.config.parser.ProGuardConfigNode;
import com.zelix.klassmaster.proguard.config.parser.ProGuardConfigSimpleNode;
import com.zelix.klassmaster.util.SyncIndexedSet;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

public class ASTTildeClause extends ProGuardConfigSimpleNode implements ProGuardFilterReceiver {
    public Set paths = new SyncIndexedSet();
    public Set archiveFilters = new SyncIndexedSet();
    public Set fileFilters = new SyncIndexedSet();

    @Override
    public void translate(Object object1, Object object) throws ZkmException, IOException {
        ProGuardConfigTranslator proGuardConfigTranslator = (ProGuardConfigTranslator) object;

        for (int i = 0; i < this.jjtGetNumChildren(); i++) {
            ProGuardConfigNode proGuardConfigNode = this.jjtGetChild(i);
            proGuardConfigNode.translate(this, proGuardConfigTranslator);
            if (proGuardConfigNode instanceof ASTQuote122Clause) {
                String string = ((ASTQuote122Clause) proGuardConfigNode).getValue();
                this.paths.add(string.trim());
            } else if (proGuardConfigNode instanceof ASTLparenClause2) {
                ;
            }
        }
    }

    public String buildClassPathText() {
        StringBuilder stringBuilder = new StringBuilder();
        int ba = 0;

        for (Iterator iterator = this.paths.iterator(); iterator.hasNext(); ba++) {
            String string = (String) iterator.next();
            StringBuilder stringBuilder3;
            char bd;
            if (ba > 0) {
                stringBuilder.append(File.pathSeparatorChar);
                stringBuilder3 = stringBuilder;
                bd = '"';
            } else {
                stringBuilder3 = stringBuilder;
                bd = '"';
            }

            stringBuilder3.append(bd);
            stringBuilder.append(string);
            stringBuilder.append('"');
        }

        if (!this.fileFilters.isEmpty()) {
            stringBuilder.append('(');
            if (!this.archiveFilters.isEmpty()) {
                ba = 0;

                for (Iterator iterator1 = this.archiveFilters.iterator(); iterator1.hasNext(); ba++) {
                    String string1 = (String) iterator1.next();
                    StringBuilder stringBuilder1;
                    char bb;
                    if (ba > 0) {
                        stringBuilder.append(',');
                        stringBuilder1 = stringBuilder;
                        bb = '"';
                    } else {
                        stringBuilder1 = stringBuilder;
                        bb = '"';
                    }

                    stringBuilder1.append(bb);
                    stringBuilder.append(string1);
                    stringBuilder.append('"');
                }

                stringBuilder.append(File.pathSeparatorChar);
            }

            ba = 0;

            for (Iterator iterator2 = this.fileFilters.iterator(); iterator2.hasNext(); ba++) {
                String string2 = (String) iterator2.next();
                StringBuilder stringBuilder2;
                char bc;
                if (ba > 0) {
                    stringBuilder.append(',');
                    stringBuilder2 = stringBuilder;
                    bc = '"';
                } else {
                    stringBuilder2 = stringBuilder;
                    bc = '"';
                }

                stringBuilder2.append(bc);
                stringBuilder.append(string2);
                stringBuilder.append('"');
            }

            stringBuilder.append(')');
        }

        return stringBuilder.toString();
    }

    public Set getFileFilters() {
        return new SyncIndexedSet(this.fileFilters);
    }

    public ASTTildeClause() {
        super(70);
    }

    @Override
    public boolean addFileFilter(Object object) {
        return this.fileFilters.add(object);
    }

    public boolean hasArchiveFilters() {
        return !this.archiveFilters.isEmpty();
    }

    public Set getPaths() {
        return new SyncIndexedSet(this.paths);
    }

    @Override
    public boolean addArchiveFilter(Object object) {
        return this.archiveFilters.add(object);
    }

    public boolean hasFileFilters() {
        return !this.fileFilters.isEmpty();
    }

    public String buildArchiveFilterText() {
        StringBuilder stringBuilder = new StringBuilder();
        if (!this.archiveFilters.isEmpty()) {
            int ba = 0;

            for (Iterator iterator = this.archiveFilters.iterator(); iterator.hasNext(); ba++) {
                String string = (String) iterator.next();
                StringBuilder stringBuilder1;
                char bb;
                if (ba > 0) {
                    stringBuilder.append(',');
                    stringBuilder1 = stringBuilder;
                    bb = '"';
                } else {
                    stringBuilder1 = stringBuilder;
                    bb = '"';
                }

                stringBuilder1.append(bb);
                stringBuilder.append(string);
                stringBuilder.append('"');
            }

            stringBuilder.append(File.pathSeparatorChar);
        }

        return stringBuilder.toString();
    }
}
