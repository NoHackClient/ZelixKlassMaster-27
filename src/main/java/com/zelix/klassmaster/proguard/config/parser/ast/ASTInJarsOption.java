


package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.archive.ZkmFileUtils;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTTildeClause;
import com.zelix.klassmaster.proguard.config.parser.ast.ProGuardOptionNode;
import com.zelix.klassmaster.util.ObservableHolder;

import java.io.IOException;
import java.util.Set;

public class ASTInJarsOption
        extends ProGuardOptionNode {
    public static String getFileExtension(String string) {
        int n = string.lastIndexOf(".");
        if (n > -1 && n < string.length() - 1) {
            return string.substring(n + 1);
        }
        return "";
    }

    public static String buildFileFilterExpression(Set set, ObservableHolder observableHolder) throws ZkmException, IOException {
        StringBuilder stringBuilder = new StringBuilder();
        int n = set.size();
        int n2 = 0;
        boolean bl = false;
        String string = null;
        stringBuilder.append('{');
        for (String string2 : (java.lang.Iterable<String>) (Object) (set)) {
            int n3;
            StringBuilder stringBuilder2;
            boolean bl2;
            String string3;
            block6:
            {
                String string4;
                StringBuilder stringBuilder3;
                block12:
                {
                    block9:
                    {
                        block7:
                        {
                            String string5;
                            StringBuilder stringBuilder4;
                            block11:
                            {
                                block10:
                                {
                                    block8:
                                    {
                                        string3 = string2.trim();
                                        bl2 = false;
                                        if (string3.startsWith("!")) {
                                            bl2 = true;
                                            string3 = string3.substring(1);
                                        }
                                        if (string3.startsWith("**")) {
                                            string3 = string3.substring(1);
                                        }
                                        if (n2 <= 0) break block6;
                                        if (!bl) break block7;
                                        if (!bl2) break block8;
                                        if (n2 == n - 1) break block9;
                                        break block10;
                                    }
                                    stringBuilder4 = stringBuilder;
                                    string5 = " && ";
                                    break block11;
                                }
                                stringBuilder4 = stringBuilder;
                                string5 = " && ";
                            }
                            stringBuilder4.append(string5);
                            if (!ASTInJarsOption.getFileExtension(string3).equals(string)) {
                                observableHolder.setValue("File filters in ProGuard '-injars' command may be too complicated for translation : '" + string2 + "' preceded by negated filter with '" + string + "' suffix.");
                            }
                            break block6;
                        }
                        stringBuilder3 = stringBuilder;
                        string4 = " || ";
                        break block12;
                    }
                    stringBuilder3 = stringBuilder;
                    string4 = " || ";
                }
                stringBuilder3.append(string4);
            }
            if (bl2) {
                stringBuilder.append("!");
                bl = true;
                string = ASTInJarsOption.getFileExtension(string3);
                stringBuilder2 = stringBuilder;
                n3 = 34;
            } else {
                bl = false;
                string = null;
                stringBuilder2 = stringBuilder;
                n3 = 34;
            }
            stringBuilder2.append((char) n3);
            stringBuilder.append(string3);
            stringBuilder.append('\"');
            ++n2;
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    @Override
    public String getOptionName() {
        return "-injars";
    }

    @Override
    public void translateOption(Object object) throws ZkmException, IOException {
        ProGuardConfigTranslator proGuardConfigTranslator = (ProGuardConfigTranslator) object;
        this.jjtGetNumChildren();
        ASTTildeClause aSTTildeClause = (ASTTildeClause) this.jjtGetChild(0);
        Set set = aSTTildeClause.getPaths();
        int n = set.size();
        int n2 = 0;
        for (String string : (java.lang.Iterable<String>) (Object) (set)) {
            char c;
            if (!ZkmFileUtils.looksLikeArchiveName(string) && (c = string.charAt(string.length() - 1)) != '*') {
                string = string + (c == ZkmFileUtils.FILE_SEPARATOR_CHAR || c == '/' || c == '\\' ? "" : Character.valueOf(ZkmFileUtils.FILE_SEPARATOR_CHAR)) + "*";
            }
            if (n2 == n - 1 && aSTTildeClause.hasFileFilters()) {
                ObservableHolder observableHolder = new ObservableHolder();
                String string2 = ASTInJarsOption.buildFileFilterExpression(aSTTildeClause.getFileFilters(), observableHolder);
                proGuardConfigTranslator.addOpenArchive(string, string2);
                if (!observableHolder.isValueNull()) {
                    proGuardConfigTranslator.logWarning((String) observableHolder.getValue());
                }
            } else {
                proGuardConfigTranslator.addOpenArchive(string, "");
            }
            ++n2;
        }
        if (aSTTildeClause.hasArchiveFilters()) {
            proGuardConfigTranslator.logWarning("Archive level filters not supported in ProGuard '" + this.getOptionName() + "' command : '" + aSTTildeClause.buildArchiveFilterText() + "' ignored in '" + aSTTildeClause.buildClassPathText() + "'");
        }
    }

    public ASTInJarsOption() {
        super(3);
    }
}
