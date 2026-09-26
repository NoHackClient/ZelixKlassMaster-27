package com.zelix.klassmaster.ui.wizard;

import com.zelix.UserPreferences;
import com.zelix.klassmaster.archive.InputFileLocation;
import com.zelix.klassmaster.classfile.hierarchy.ZkmClasspath;
import com.zelix.klassmaster.config.ExclusionWizardSettings;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.config.ObfuscateOptions;
import com.zelix.klassmaster.config.ReferenceObfIncludeStore;
import com.zelix.klassmaster.config.TrimExcludeSettings;
import com.zelix.klassmaster.config.TrimOptions;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.log.AbstractMessageLog;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ast.ASTExcludeStatement;
import com.zelix.klassmaster.script.parser.ast.ASTObfuscateReferencesIncludeStatement;
import com.zelix.klassmaster.script.parser.ast.ASTTrimExcludeStatement;
import com.zelix.klassmaster.ui.ZkmMainWindow;
import com.zelix.klassmaster.ui.dialog.HelperDialogMarker;
import com.zelix.klassmaster.util.SystemEnvironmentConstants;
import com.zelix.klassmaster.util.ZkmStringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public abstract class AbstractHelperWizard implements HelperDialogMarker {
    private static String lastHelperScript;
    public static final String continuationIndent;
    public boolean classesChanged;
    public ExclusionWizardSettings exclusionSettings;
    public String saveDirectory;
    public ObfuscateOptions obfuscateOptions;
    public boolean trimExclusionsSkipped;
    public InputFileLocation[] openedLocations;
    public ReferenceObfIncludeStore referenceIncludeStore;
    public boolean trimSkipped;
    public TrimExcludeSettings trimExcludeSettings;
    public ZkmClasspath classpath;
    public TrimOptions trimOptions = new TrimOptions(SystemEnvironmentConstants.USER_DIR);
    public ZkmMainWindow mainWindow;
    public ScriptEnvironment scriptEnvironment;
    public UserPreferences userPreferences;

    public void onReferenceInclusionsResult(int ba) throws ZkmException, IOException {
        this.referenceIncludeStore.saveToFile(SystemEnvironmentConstants.USER_DIR, "ZKM_OB_REF.ser");
        if (ba == 1) {
            if (this.saveDirectory == null) {
                this.showDefaultSaveDialog();
            } else {
                this.showSaveDialog(this.saveDirectory);
            }
        } else if (ba == 2 || ba == 5) {
            this.showDefaultObfuscateOptions();
        }
    }

    public AbstractHelperWizard(ZkmMainWindow zkmMainWindow, ScriptEnvironment scriptEnvironment1, UserPreferences userPreferences1) {
        this.mainWindow = zkmMainWindow;
        this.scriptEnvironment = scriptEnvironment1;
        this.userPreferences = userPreferences1;
        zkmMainWindow.setEnabled(false);
        zkmMainWindow.setBusy(true);
        this.showIntroduction();
    }

    public abstract void showSaveDialog(String string);

    public abstract void finishWizard();


    static {
        setLastHelperScript();
        continuationIndent = ZkmStringUtils.pad("", 76, 12, 32);
    }


    public abstract void showDefaultObfuscateOptions();

    public static void setLastHelperScript() {
        lastHelperScript = null;
    }

    public static String getLastHelperScript() {
        return lastHelperScript;
    }

    public final String buildEquivalentScript(String string, InputFileLocation[] inputFileLocations) {
        StringBuilder stringBuilder = new StringBuilder(1000);
        stringBuilder.append(AbstractMessageLog.buildGeneratedByHeader(string));
        String string1 = this.classpath.getClasspath();
        if (string1 != null && string1.length() > 0) {
            StringTokenizer stringTokenizer = new StringTokenizer(string1, SystemEnvironmentConstants.PATH_SEPARATOR_String);
            int ba = stringTokenizer.countTokens();

            for (int i = 0; i < ba; i++) {
                String string2 = stringTokenizer.nextToken();
                if (i == 0) {
                    stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR + ZkmStringUtils.pad("classpath", 76, 12, 32) + "\"" + string2 + "\"");
                } else {
                    stringBuilder.append(ZkmStringUtils.pad("", 76, 12, 32) + "\"" + string2 + "\"");
                }

                if (i < ba - 1) {
                    stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
                } else {
                    stringBuilder.append(";" + HiddenOptionFlags.LINE_SEPARATOR);
                }
            }
        }

        if (inputFileLocations != null && inputFileLocations.length > 0) {
            stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR + ZkmStringUtils.pad("open", 76, 12, 32));
            boolean bl = false;
            if (!this.userPreferences.isOpenNestedArchives()) {
                stringBuilder.append("openNestedArchives=false" + HiddenOptionFlags.LINE_SEPARATOR);
                bl = true;
            }

            for (int i = 0; i < inputFileLocations.length; i++) {
                if (bl) {
                    stringBuilder.append(continuationIndent);
                }

                stringBuilder.append("\"" + inputFileLocations[i].getName() + "\"");
                if (i < inputFileLocations.length - 1) {
                    stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
                } else {
                    stringBuilder.append(";" + HiddenOptionFlags.LINE_SEPARATOR);
                }

                bl = true;
            }
        }

        if (!this.trimExclusionsSkipped && this.trimExcludeSettings != null) {
            List list1 = this.trimExcludeSettings.getEntryList();
            if (list1.size() > 0) {
                stringBuilder.append(ASTTrimExcludeStatement.formatTrimExcludeStatement(list1, 12));
            }
        }

        if (!this.trimSkipped && this.trimOptions != null) {
            ArrayList arrayList = new ArrayList();
            arrayList.add("deleteSourceFileAttributes=" + this.trimOptions.a);
            arrayList.add("deleteDeprecatedAttributes=" + this.trimOptions.b);
            arrayList.add("deleteAnnotationAttributes=" + this.trimOptions.d);
            arrayList.add("deleteExceptionAttributes=" + this.trimOptions.e);
            arrayList.add("deleteUnknownAttributes=" + this.trimOptions.c);
            stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR + ZkmStringUtils.pad("trim", 76, 12, 32));

            for (int i = 0; i < arrayList.size(); i++) {
                if (i > 0) {
                    stringBuilder.append(continuationIndent);
                }

                stringBuilder.append((String) arrayList.get(i));
                if (i < arrayList.size() - 1) {
                    stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
                }
            }

            stringBuilder.append(";" + HiddenOptionFlags.LINE_SEPARATOR);
        }

        List list2 = this.exclusionSettings.getEntryList();
        if (list2.size() > 0) {
            stringBuilder.append(ASTExcludeStatement.formatExcludeStatement(list2, 12));
        }

        if (this.referenceIncludeStore != null) {
            List list3 = this.referenceIncludeStore.getEntryList();
            if (list3.size() > 0) {
                stringBuilder.append(
                        ASTObfuscateReferencesIncludeStatement.formatIncludeStatement(list3, ASTObfuscateReferencesIncludeStatement.getKeywordLength() + 1)
                );
            }
        }

        if (this.obfuscateOptions != null) {
            ArrayList arrayList1 = new ArrayList();
            if (this.obfuscateOptions.c) {
                String string5;
                if (this.obfuscateOptions.e) {
                    string5 = "looseChangeLogFileIn";
                } else {
                    string5 = "changeLogFileIn";
                }

                arrayList1.add(string5 + "=\"" + this.obfuscateOptions.g[0].getFileName() + "\"");
            } else {
                arrayList1.add("changeLogFileIn=\"\"");
            }

            if (this.obfuscateOptions.d) {
                arrayList1.add("changeLogFileOut=\"" + this.obfuscateOptions.f + "\"");
            } else {
                arrayList1.add("changeLogFileOut=\"\"");
            }

            if (this.obfuscateOptions.b == 0) {
                arrayList1.add("keepInnerClassInfo=true");
            } else if (this.obfuscateOptions.b == 2) {
                arrayList1.add("keepInnerClassInfo=ifNameNotObfuscated");
            }

            if (this.obfuscateOptions.u == 1) {
                arrayList1.add("keepGenericsInfo=false");
            }

            switch (this.obfuscateOptions.o) {
                case 0:
                    arrayList1.add("obfuscateFlow=none");
                    break;
                case 1:
                    arrayList1.add("obfuscateFlow=light");
                    break;
                case 2:
                    arrayList1.add("obfuscateFlow=normal");
                    break;
                case 3:
                    arrayList1.add("obfuscateFlow=aggressive");
                    break;
                case 4:
                    arrayList1.add("obfuscateFlow=extraAggressive");
            }

            String string6;
            switch (this.obfuscateOptions.C) {
                case 1:
                    string6 = "light";
                    break;
                case 2:
                    string6 = "heavy";
                    break;
                default:
                    string6 = "none";
            }

            arrayList1.add("exceptionObfuscation=" + string6);
            switch (this.obfuscateOptions.as) {
                case 0:
                    arrayList1.add("encryptStringLiterals=none");
                    break;
                case 1:
                    arrayList1.add("encryptStringLiterals=normal");
                    break;
                case 2:
                    arrayList1.add("encryptStringLiterals=aggressive");
                    break;
                case 3:
                    arrayList1.add("encryptStringLiterals=flowObfuscate");
                    break;
                case 4:
                    arrayList1.add("encryptStringLiterals=enhanced");
            }

            switch (this.obfuscateOptions.dZ) {
                case 0:
                    arrayList1.add("encryptIntegerConstants=none");
                    break;
                case 1:
                    arrayList1.add("encryptIntegerConstants=normal");
                    break;
                case 2:
                    arrayList1.add("encryptIntegerConstants=aggressive");
            }

            switch (this.obfuscateOptions.dC) {
                case 0:
                    arrayList1.add("encryptLongConstants=none");
                    break;
                case 1:
                    arrayList1.add("encryptLongConstants=normal");
            }

            String string7;
            switch (this.obfuscateOptions.F.getValue()) {
                case 0:
                    string7 = "true";
                    break;
                case 1:
                    string7 = "false";
                    break;
                default:
                    string7 = "ifInArchive";
            }

            arrayList1.add("mixedCaseClassNames=" + string7);
            if (this.obfuscateOptions.w) {
                arrayList1.add("aggressiveMethodRenaming=true");
            }

            if (this.obfuscateOptions.z) {
                arrayList1.add("randomize=true");
            }

            ObfuscateOptions obfuscateOptions1;
            if (this.obfuscateOptions.s) {
                arrayList1.add("collapsePackagesWithDefault=\"" + this.obfuscateOptions.t + "\"");
                obfuscateOptions1 = this.obfuscateOptions;
            } else {
                obfuscateOptions1 = this.obfuscateOptions;
            }

            if (!obfuscateOptions1.E) {
                arrayList1.add("legalIdentifiers=false");
                obfuscateOptions1 = this.obfuscateOptions;
            } else {
                obfuscateOptions1 = this.obfuscateOptions;
            }

            String string3;
            switch (obfuscateOptions1.B) {
                case 0:
                    string3 = "delete";
                    break;
                case 1:
                    string3 = "keep";
                    break;
                case 2:
                    string3 = "obfuscate";
                    break;
                case 3:
                    string3 = "keepVisibleMethodParameters";
                    break;
                case 4:
                default:
                    string3 = "keepVisibleMethodParametersIfNotObfuscated";
                    break;
                case 5:
                    string3 = "keepMethodParametersIfNotObfuscated";
            }

            arrayList1.add("localVariables=" + string3);
            switch (this.obfuscateOptions.i) {
                case 0:
                    arrayList1.add("lineNumbers=delete");
                    break;
                case 1:
                    arrayList1.add("lineNumbers=scramble");
                    break;
                case 2:
                    arrayList1.add("lineNumbers=keep");
            }

            if (this.obfuscateOptions.x != null && this.obfuscateOptions.x.length() > 0) {
                arrayList1.add("newNamesPrefix=\"" + this.obfuscateOptions.x + "\"");
            }

            arrayList1.add("autoReflectionHandling=" + (this.obfuscateOptions.r == 1 ? "normal" : "none"));
            arrayList1.add("obfuscateReferences=" + (this.obfuscateOptions.aj == 1 ? "normal" : "none"));
            if (this.obfuscateOptions.aj != 0 && this.obfuscateOptions.m != 1) {
                arrayList1.add("obfuscateReferenceStructures=inReferencingClasses");
            }

            if (this.obfuscateOptions.aC != 0) {
                String string4;
                switch (this.obfuscateOptions.aC) {
                    case 1:
                        string4 = "normal";
                        break;
                    case 2:
                        string4 = "random";
                        break;
                    case 3:
                        string4 = "flowObfuscate";
                        break;
                    default:
                        string4 = null;
                }

                arrayList1.add("methodParameterChanges=" + string4);
            }

            if (this.obfuscateOptions.ar) {
                arrayList1.add("keepBalancedLocks=true");
            }

            if (this.obfuscateOptions.d7 != 0) {
                arrayList1.add("obfuscateParameters=normal");
            }

            if (!this.obfuscateOptions.j) {
                arrayList1.add("preverify=false");
            }

            stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR + ZkmStringUtils.pad("obfuscate", 76, 12, 32));

            for (int i = 0; i < arrayList1.size(); i++) {
                if (i > 0) {
                    stringBuilder.append(continuationIndent);
                }

                stringBuilder.append((String) arrayList1.get(i));
                if (i < arrayList1.size() - 1) {
                    stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
                }
            }

            stringBuilder.append(";" + HiddenOptionFlags.LINE_SEPARATOR);
        }

        if (this.saveDirectory != null && this.saveDirectory.length() > 0) {
            stringBuilder.append(
                    HiddenOptionFlags.LINE_SEPARATOR
                            + ZkmStringUtils.pad("saveAll", 76, 12, 32)
                            + "archiveCompression"
                            + "="
                            + "all"
                            + " \""
                            + this.saveDirectory
                            + "\";"
                            + HiddenOptionFlags.LINE_SEPARATOR
            );
        }

        return stringBuilder.toString();
    }

    public abstract void showDefaultSaveDialog();

    public abstract void showIntroduction();
}
