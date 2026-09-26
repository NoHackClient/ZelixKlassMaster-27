package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.config.MixedCaseNamesMode;
import com.zelix.klassmaster.config.ObfuscateOptions;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.log.MessageReporter;
import com.zelix.klassmaster.obfuscator.rename.RootPackageNode;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.NoOpCallback;
import com.zelix.klassmaster.util.ObservableHolder;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class ASTObfuscateStatement extends ObfuscateStatementBase {
    public Boolean allowMethodParameterChanges = null;
    public boolean methodParameterChangesSpecified = false;

    public void readMethodParametersOption(ObfuscateOptions obfuscateOptions1) {
        obfuscateOptions1.am = 0;
        List list1 = super.parameterValues.getValues("methodParameters");
        if (list1 != null && list1.size() > 0) {
            String string = (String) list1.get(0);
            if (string != null) {
                if (string.equals("delete")) {
                    obfuscateOptions1.am = 0;
                } else if (string.equals("obfuscate")) {
                    obfuscateOptions1.am = 2;
                } else if (string.equals("keep")) {
                    obfuscateOptions1.am = 1;
                } else if (string.equals("keepVisible")) {
                    obfuscateOptions1.am = 3;
                } else if (string.equals("keepVisibleIfNotObfuscated")) {
                    obfuscateOptions1.am = 4;
                } else if (string.equals("keepIfNotObfuscated")) {
                    obfuscateOptions1.am = 5;
                }
            }
        }
    }

    public void readKeepBalancedLocksOption(ObfuscateOptions obfuscateOptions1) {
        obfuscateOptions1.ar = false;
        List list1 = super.parameterValues.getValues("keepBalancedLocks");
        if (list1 != null && list1.size() > 0) {
            String string = (String) list1.get(0);
            if (string != null && string.equals("true")) {
                obfuscateOptions1.ar = true;
            }
        }
    }

    public void readMakeClassesPublicOption(ObfuscateOptions obfuscateOptions1) {
        obfuscateOptions1.XN = false;
        List list1 = super.parameterValues.getValues("makeClassesPublic");
        if (list1 != null && list1.size() > 0) {
            String string = (String) list1.get(0);
            if (string != null && string.equals("true")) {
                obfuscateOptions1.XN = true;
            }
        }
    }

    public void readLocalVariablesOption(ObfuscateOptions obfuscateOptions1) {
        obfuscateOptions1.B = 0;
        List list1 = super.parameterValues.getValues("localVariables");
        if (list1 != null && list1.size() > 0) {
            String string = (String) list1.get(0);
            if (string != null) {
                if (string.equals("obfuscate")) {
                    obfuscateOptions1.B = 2;
                } else if (string.equals("keep")) {
                    obfuscateOptions1.B = 1;
                } else if (string.equals("keepVisibleMethodParameters")) {
                    obfuscateOptions1.B = 3;
                } else if (string.equals("keepVisibleMethodParametersIfNotObfuscated")) {
                    obfuscateOptions1.B = 4;
                } else if (string.equals("keepMethodParametersIfNotObfuscated")) {
                    obfuscateOptions1.B = 5;
                }
            }
        }
    }

    public void readObfuscateParametersOption(ObfuscateOptions obfuscateOptions1) {
        obfuscateOptions1.d7 = 0;
        List list1 = super.parameterValues.getValues("obfuscateParameters");
        if (list1 != null && list1.size() > 0) {
            String string = (String) list1.get(0);
            if (string != null) {
                if (string.equals("none")) {
                    obfuscateOptions1.d7 = 0;
                } else if (string.equals("normal")) {
                    obfuscateOptions1.d7 = 1;
                }
            }
        }
    }

    public void readEncryptLongConstantsOption(ObfuscateOptions obfuscateOptions1) {
        obfuscateOptions1.dC = 0;
        List list1 = super.parameterValues.getValues("encryptLongConstants");
        if (list1 != null && list1.size() > 0) {
            String string = (String) list1.get(0);
            if (string != null) {
                if (string.equals("none")) {
                    obfuscateOptions1.dC = 0;
                } else if (string.equals("normal")) {
                    obfuscateOptions1.dC = 1;
                }
            }
        }
    }

    @Override
    public String getProgressVerb() {
        return "Obfuscating";
    }

    public void readHideStaticMethodNamesOption(ObfuscateOptions obfuscateOptions1) {
        obfuscateOptions1.l = false;
        List list1 = super.parameterValues.getValues("hideStaticMethodNames");
        if (list1 != null && list1.size() > 0) {
            String string = (String) list1.get(0);
            if (string != null && string.equals("true")) {
                obfuscateOptions1.l = true;
            }
        }
    }

    public void readObfuscateFlowOption(ObfuscateOptions obfuscateOptions1) {
        obfuscateOptions1.o = 1;
        List list1 = super.parameterValues.getValues("obfuscateFlow");
        if (list1 != null && list1.size() > 0) {
            String string = (String) list1.get(0);
            if (string != null) {
                obfuscateOptions1.o = ObfuscateOptions.parseFlowLevel(string);
            }
        }
    }

    @Override
    public void executeObfuscation(
            List list1,
            List list2,
            ObfuscateOptions obfuscateOptions1,
            ScriptEnvironment scriptEnvironment1,
            NoOpCallback noOpCallback,
            MessageReporter messageReporter1
    ) throws ZkmException, IOException {
        ClassRepository classRepository1 = scriptEnvironment1.getClassRepository();
        PrintWriter printWriter = scriptEnvironment1.getLogWriter();
        if (scriptEnvironment1.isVerbose()) {
            String string = null;
            switch (obfuscateOptions1.u) {
                case 0:
                    string = "true";
                    break;
                case 1:
                    string = "false";
            }

            printWriter.println("\tkeepGenericsInfo=" + string);
            if (!obfuscateOptions1.E) {
                printWriter.println("\tlegalIdentifiers=" + obfuscateOptions1.E);
            }

            if (obfuscateOptions1.ar) {
                printWriter.println("\tkeepBalancedLocks=" + obfuscateOptions1.ar);
            }

            if (!obfuscateOptions1.j) {
                printWriter.println("\tpreverify=" + obfuscateOptions1.j);
            }

            if (obfuscateOptions1.XN) {
                printWriter.println("\tmakeClassesPublic=" + obfuscateOptions1.XN);
            }

            String string1 = null;
            switch (obfuscateOptions1.F.getValue()) {
                case 0:
                    string1 = "true";
                    break;
                case 1:
                    string1 = "false";
                    break;
                case 2:
                    string1 = "ifInArchive";
            }

            printWriter.println("\tmixedCaseClassNames=" + string1);
            printWriter.println("\tuniqueClassNames=" + obfuscateOptions1.ay);
            printWriter.println("\tuniqueMethodNames=" + obfuscateOptions1.dV);
            if (this.allowMethodParameterChanges != null) {
                printWriter.println("\tallowMethodParameterChanges=" + (this.allowMethodParameterChanges ? "true" : "false"));
            }

            if (this.methodParameterChangesSpecified) {
                printWriter.print("\tmethodParameterChanges=");
                switch (obfuscateOptions1.aC) {
                    case 0:
                        printWriter.println("none");
                        break;
                    case 1:
                        printWriter.println("normal");
                        break;
                    case 2:
                        printWriter.println("random");
                        break;
                    case 3:
                        printWriter.println("flowObfuscate");
                }

                if (obfuscateOptions1.aC != 0 && obfuscateOptions1.dN != null && obfuscateOptions1.dN.length() > 0) {
                    printWriter.println("\tmethodParameterChangesPackage=\"" + obfuscateOptions1.dN + "\"");
                }
            }

            if (obfuscateOptions1.k) {
                printWriter.println("\thideFieldNames=" + obfuscateOptions1.k);
            }

            if (obfuscateOptions1.l) {
                printWriter.println("\thideStaticMethodNames=" + obfuscateOptions1.l);
            }

            String string2 = null;
            switch (obfuscateOptions1.o) {
                case 0:
                    string2 = "none";
                    break;
                case 1:
                    string2 = "light";
                    break;
                case 2:
                    string2 = "normal";
                    break;
                case 3:
                    string2 = "aggressive";
                    break;
                case 4:
                    string2 = "extraAggressive";
            }

            printWriter.println("\tobfuscateFlow=" + string2);
            String string3 = null;
            switch (obfuscateOptions1.d7) {
                case 0:
                    string3 = "none";
                    break;
                case 1:
                    string3 = "normal";
            }

            printWriter.println("\tobfuscateParameters=" + string3);
            String string4 = null;
            switch (obfuscateOptions1.as) {
                case 0:
                    string4 = "none";
                    break;
                case 1:
                    string4 = "normal";
                    break;
                case 2:
                    string4 = "aggressive";
                    break;
                case 3:
                    string4 = "flowObfuscate";
                    break;
                case 4:
                    string4 = "enhanced";
            }

            printWriter.println("\tencryptStringLiterals=" + string4);
            String string5 = null;
            switch (obfuscateOptions1.dZ) {
                case 0:
                    string5 = "none";
                    break;
                case 1:
                    string5 = "normal";
                    break;
                case 2:
                    string5 = "aggressive";
            }

            printWriter.println("\tencryptIntegerConstants=" + string5);
            String string6 = null;
            switch (obfuscateOptions1.dC) {
                case 0:
                    string6 = "none";
                    break;
                case 1:
                    string6 = "normal";
                    break;
                case 2:
                    string6 = "aggressive";
            }

            printWriter.println("\tencryptLongConstants=" + string6);
            String string7 = null;
            switch (obfuscateOptions1.i) {
                case 0:
                    string7 = "delete";
                    break;
                case 1:
                    string7 = "scramble";
                    break;
                case 2:
                    string7 = "keep";
            }

            printWriter.println("\tlineNumbers=" + string7);
            String string8 = null;
            switch (obfuscateOptions1.B) {
                case 0:
                    string8 = "delete";
                    break;
                case 1:
                    string8 = "keep";
                    break;
                case 2:
                    string8 = "obfuscate";
                    break;
                case 3:
                    string8 = "keepVisibleMethodParameters";
                    break;
                case 4:
                    string8 = "keepVisibleMethodParametersIfNotObfuscated";
                    break;
                case 5:
                    string8 = "keepMethodParametersIfNotObfuscated";
            }

            printWriter.println("\tlocalVariables=" + string8);
            String string9 = null;
            switch (obfuscateOptions1.am) {
                case 0:
                    string9 = "delete";
                    break;
                case 1:
                    string9 = "keep";
                    break;
                case 2:
                    string9 = "obfuscate";
                    break;
                case 3:
                    string9 = "keepVisible";
                    break;
                case 4:
                    string9 = "keepVisibleIfNotObfuscated";
                    break;
                case 5:
                    string9 = "keepIfNotObfuscated";
            }

            printWriter.println("\tmethodParameters=" + string9);
            if (obfuscateOptions1.s) {
                printWriter.println("\tcollapsePackagesWithDefault=\"" + obfuscateOptions1.t.replace('/', '.') + "\"");
            }

            if (obfuscateOptions1.x != null && obfuscateOptions1.x.length() > 0) {
                printWriter.println("\tnewNamesPrefix=\"" + obfuscateOptions1.x + "\"");
            }

            printWriter.println("\tderiveGroupingsFromInputChangeLog=" + obfuscateOptions1.y);
            String string10 = null;
            switch (obfuscateOptions1.C) {
                case 0:
                    string10 = "none";
                    break;
                case 1:
                    string10 = "light";
                    break;
                case 2:
                    string10 = "heavy";
            }

            printWriter.println("\texceptionObfuscation=" + string10);
            printWriter.println("\tautoReflectionHandling=" + (obfuscateOptions1.r == 0 ? "none" : "normal"));
            if (obfuscateOptions1.q != null && obfuscateOptions1.q.length() > 0) {
                printWriter.println("\tautoReflectionPackage=\"" + obfuscateOptions1.q + "\"");
            }

            if (obfuscateOptions1.D != null && obfuscateOptions1.D.length() > 0) {
                printWriter.println("\tautoReflectionHash=" + obfuscateOptions1.D);
            }

            printWriter.println("\tobfuscateReferences=" + (obfuscateOptions1.aj == 0 ? "none" : "normal"));
            if (obfuscateOptions1.aj != 0) {
                printWriter.println("\tobfuscateReferenceStructures=" + (obfuscateOptions1.m == 0 ? "inReferencingClasses" : "inSpecialClass"));
                if (obfuscateOptions1.ai != null && obfuscateOptions1.ai.length() > 0) {
                    printWriter.println("\tobfuscateReferencesPackage=\"" + obfuscateOptions1.ai + "\"");
                }
            }
        }

        if (obfuscateOptions1.an != null && obfuscateOptions1.an.length() > 0) {
            printWriter.println("\tassumeRuntimeVersion=\"" + obfuscateOptions1.an + "\"");
        }

        if (obfuscateOptions1.dv != null && obfuscateOptions1.dv.length() > 0) {
            printWriter.println("\tnewPackageNameFile=\"" + obfuscateOptions1.dv + "\"");
        }

        if (obfuscateOptions1.dE != null && obfuscateOptions1.dE.length() > 0) {
            printWriter.println("\tnewClassNameFile=\"" + obfuscateOptions1.dE + "\"");
        }

        if (obfuscateOptions1.di != null && obfuscateOptions1.di.length() > 0) {
            printWriter.println("\tnewFieldNameFile=\"" + obfuscateOptions1.di + "\"");
        }

        if (obfuscateOptions1.dB != null && obfuscateOptions1.dB.length() > 0) {
            printWriter.println("\tnewMethodNameFile=\"" + obfuscateOptions1.dB + "\"");
        }

        if (!obfuscateOptions1.dr) {
            printWriter.println("\tnewNameCharacters=non-ASCII");
        }

        List list13 = scriptEnvironment1.getObfuscateFlowExcludeStatements();
        List list14 = scriptEnvironment1.getObfuscateFlowUnexcludeStatements();
        List list15 = scriptEnvironment1.getObfuscateExceptionsExcludeStatements();
        List list16 = scriptEnvironment1.getObfuscateExceptionsUnexcludeStatements();
        List list17 = scriptEnvironment1.getStringEncryptionExcludeStatements();
        List list18 = scriptEnvironment1.getStringEncryptionUnexcludeStatements();
        List list19 = scriptEnvironment1.getIntegerEncryptionExcludeStatements();
        List list20 = scriptEnvironment1.getIntegerEncryptionUnexcludeStatements();
        List list21 = scriptEnvironment1.getLongEncryptionExcludeStatements();
        List list22 = scriptEnvironment1.getLongEncryptionUnexcludeStatements();
        List list23 = scriptEnvironment1.getExistingSerializedClassesStatements();
        List list3 = scriptEnvironment1.getFixedClassesStatements();
        List list4 = scriptEnvironment1.getGroupingsStatements();
        List list5 = scriptEnvironment1.getAccessedByReflectionStatements();
        List list6 = scriptEnvironment1.getAccessedByReflectionExcludeStatements();
        List list7 = scriptEnvironment1.getObfuscateReferencesIncludeStatements();
        List list8 = scriptEnvironment1.getObfuscateReferencesExcludeStatements();
        List list9 = scriptEnvironment1.getParamChangesIncludeStatements();
        List list10 = scriptEnvironment1.getParamChangesExcludeStatements();
        List list11 = scriptEnvironment1.getParamObfuscationIncludeStatements();
        List list12 = scriptEnvironment1.getParamObfuscationExcludeStatements();
        boolean bl;
        String string11;
        if (obfuscateOptions1.m == 1) {
            bl = true;
            string11 = obfuscateOptions1.ai;
        } else {
            bl = false;
            string11 = obfuscateOptions1.ai;
        }

        classRepository1.obfuscate(
                obfuscateOptions1.g,
                obfuscateOptions1.e,
                obfuscateOptions1.h,
                obfuscateOptions1.f,
                obfuscateOptions1.v,
                list1,
                list2,
                list13,
                list14,
                list15,
                list16,
                list17,
                list18,
                list19,
                list20,
                list21,
                list22,
                list23,
                list3,
                list4,
                list5,
                list6,
                list7,
                list8,
                list9,
                list10,
                list11,
                list12,
                obfuscateOptions1.E,
                obfuscateOptions1.x,
                obfuscateOptions1.k,
                obfuscateOptions1.l,
                obfuscateOptions1.w,
                obfuscateOptions1.z,
                obfuscateOptions1.A,
                obfuscateOptions1.y,
                obfuscateOptions1.b,
                obfuscateOptions1.u,
                obfuscateOptions1.o,
                obfuscateOptions1.d7,
                obfuscateOptions1.as,
                obfuscateOptions1.dZ,
                obfuscateOptions1.dC,
                obfuscateOptions1.i,
                obfuscateOptions1.B,
                obfuscateOptions1.am,
                obfuscateOptions1.s,
                obfuscateOptions1.t,
                obfuscateOptions1.C,
                obfuscateOptions1.r,
                obfuscateOptions1.q,
                obfuscateOptions1.D,
                obfuscateOptions1.aj,
                bl,
                string11,
                obfuscateOptions1.j,
                obfuscateOptions1.XN,
                obfuscateOptions1.ar,
                obfuscateOptions1.F,
                obfuscateOptions1.ay,
                obfuscateOptions1.dV,
                obfuscateOptions1.aC,
                obfuscateOptions1.dN,
                obfuscateOptions1.at,
                obfuscateOptions1.dv,
                obfuscateOptions1.dE,
                obfuscateOptions1.di,
                obfuscateOptions1.dB,
                obfuscateOptions1.dr,
                messageReporter1,
                noOpCallback,
                scriptEnvironment1
        );
    }

    public void readHideFieldNamesOption(ObfuscateOptions obfuscateOptions1) {
        obfuscateOptions1.k = false;
        List list1 = super.parameterValues.getValues("hideFieldNames");
        if (list1 != null && list1.size() > 0) {
            String string = (String) list1.get(0);
            if (string != null && string.equals("true")) {
                obfuscateOptions1.k = true;
            }
        }
    }

    public void readMethodParameterChangesOption(ObfuscateOptions obfuscateOptions1) {
        obfuscateOptions1.aC = 0;
        List list1 = super.parameterValues.getValues("methodParameterChanges");
        if (list1 != null && list1.size() > 0) {
            this.methodParameterChangesSpecified = true;
            String string = (String) list1.get(0);
            if (string != null) {
                if (string.equals("flowObfuscate")) {
                    obfuscateOptions1.aC = 3;
                } else if (string.equals("random")) {
                    obfuscateOptions1.aC = 2;
                } else if (string.equals("normal")) {
                    obfuscateOptions1.aC = 1;
                }
            }
        }
    }

    public void readLegalIdentifiersOption(ObfuscateOptions obfuscateOptions1) {
        obfuscateOptions1.E = true;
        List list1 = super.parameterValues.getValues("legalIdentifiers");
        if (list1 != null && list1.size() > 0) {
            String string = (String) list1.get(0);
            if (string != null && string.equals("false")) {
                obfuscateOptions1.E = false;
            }
        }
    }

    public void readUniqueMethodNamesOption(ObfuscateOptions obfuscateOptions1) {
        obfuscateOptions1.dV = false;
        List list1 = super.parameterValues.getValues("uniqueMethodNames");
        if (list1 != null && list1.size() > 0) {
            String string = (String) list1.get(0);
            if (string != null) {
                if (string.equals("true")) {
                    obfuscateOptions1.dV = true;
                } else {
                    obfuscateOptions1.dV = false;
                }
            }
        }
    }

    public void readObfuscateRefStructuresOption(ObfuscateOptions obfuscateOptions1) {
        obfuscateOptions1.m = 1;
        List list1 = super.parameterValues.getValues("obfuscateReferenceStructures");
        if (list1 != null && list1.size() > 0) {
            String string = (String) list1.get(0);
            if (string != null && string.equals("inReferencingClasses")) {
                obfuscateOptions1.m = 0;
            }
        }
    }

    @Override
    public void applyStatementOptions(ObfuscateOptions obfuscateOptions1, ScriptEnvironment scriptEnvironment1) throws ZkmException, IOException {
        this.readLegalIdentifiersOption(obfuscateOptions1);
        this.readHideFieldNamesOption(obfuscateOptions1);
        this.readHideStaticMethodNamesOption(obfuscateOptions1);
        this.readObfuscateFlowOption(obfuscateOptions1);
        this.readObfuscateParametersOption(obfuscateOptions1);
        this.readEncryptStringLiteralsOption(obfuscateOptions1);
        this.readEncryptIntegerConstantsOption(obfuscateOptions1);
        this.readEncryptLongConstantsOption(obfuscateOptions1);
        this.readExceptionObfuscationOption(obfuscateOptions1);
        this.readLineNumbersOption(obfuscateOptions1);
        this.readLocalVariablesOption(obfuscateOptions1);
        this.readMethodParametersOption(obfuscateOptions1);
        this.readCollapsePackagesOption(obfuscateOptions1, scriptEnvironment1);
        this.readKeepGenericsInfoOption(obfuscateOptions1);
        obfuscateOptions1.x = this.getFirstParameterValue("newNamesPrefix");
        this.readDeriveGroupingsOption(obfuscateOptions1);
        this.readAutoReflectionHandlingOption(obfuscateOptions1);
        this.readObfuscateReferencesOption(obfuscateOptions1);
        this.readObfuscateRefStructuresOption(obfuscateOptions1);
        obfuscateOptions1.ai = this.getFirstParameterValue("obfuscateReferencesPackage");
        obfuscateOptions1.q = this.getFirstParameterValue("autoReflectionPackage");
        obfuscateOptions1.D = this.getFirstParameterValue("autoReflectionHash");
        obfuscateOptions1.dv = this.getFirstParameterValue("newPackageNameFile");
        obfuscateOptions1.dE = this.getFirstParameterValue("newClassNameFile");
        obfuscateOptions1.di = this.getFirstParameterValue("newFieldNameFile");
        obfuscateOptions1.dB = this.getFirstParameterValue("newMethodNameFile");
        this.readNewNameCharactersOption(obfuscateOptions1);
        this.readPreverifyOption(obfuscateOptions1);
        this.readMakeClassesPublicOption(obfuscateOptions1);
        this.readKeepBalancedLocksOption(obfuscateOptions1);
        this.readMixedCaseClassNamesOption(obfuscateOptions1);
        this.readUniqueClassNamesOption(obfuscateOptions1);
        this.readUniqueMethodNamesOption(obfuscateOptions1);
        this.readAllowMethodParamChangesOption();
        this.readMethodParameterChangesOption(obfuscateOptions1);
        obfuscateOptions1.dN = this.getFirstParameterValue("methodParameterChangesPackage");
        if (this.allowMethodParameterChanges != null) {
            if (this.methodParameterChangesSpecified) {
                if (this.allowMethodParameterChanges) {
                    if (obfuscateOptions1.aC == 0) {
                        scriptEnvironment1.logError(
                                "\"allowMethodParameterChanges\" set to \"true\" but \"methodParameterChanges\" set to \"none\". The \"methodParameterChanges\" will take precedence. The \"allowMethodParameterChanges\" clause is deprecated."
                        );
                    }
                } else if (obfuscateOptions1.aC != 0) {
                    scriptEnvironment1.logError(
                            "\"allowMethodParameterChanges\" set to \"false\" but \"methodParameterChanges\" set to \""
                                    + (obfuscateOptions1.aC == 1 ? "normal" : (obfuscateOptions1.aC == 2 ? "random" : "flowObfuscate"))
                                    + "\". The \""
                                    + "methodParameterChanges"
                                    + "\" will take precedence. The \""
                                    + "allowMethodParameterChanges"
                                    + "\" clause is deprecated."
                    );
                }
            } else if (this.allowMethodParameterChanges) {
                obfuscateOptions1.aC = 1;
            } else {
                obfuscateOptions1.aC = 0;
            }
        }

        if (obfuscateOptions1.D != null && obfuscateOptions1.D.length() > 0) {
            try {
                MessageDigest.getInstance(obfuscateOptions1.D);
            } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                scriptEnvironment1.logSeriousError_v(
                        "Hash algorithm \""
                                + obfuscateOptions1.D
                                + "\" specified in \""
                                + "autoReflectionHash"
                                + "\" parameter in \""
                                + this.getStatementName()
                                + "\" statement at line "
                                + this.getStatementLine()
                                + " could not be found."
                );
            }
        }

        obfuscateOptions1.an = this.getFirstParameterValue("assumeRuntimeVersion");
        if (obfuscateOptions1.an != null) {
            String string = obfuscateOptions1.an.trim();
            if (string.length() > 0) {
                String[] strings = string.split("\\.");

                try {
                    obfuscateOptions1.at = Integer.parseInt(strings[0]);
                } catch (NumberFormatException numberFormatException) {
                    scriptEnvironment1.logError("\"assumeRuntimeVersion\" must be numeric : \"" + string + "\".");
                }
            }
        }

        if (!obfuscateOptions1.A) {
            if (obfuscateOptions1.e) {
                obfuscateOptions1.A = true;
                scriptEnvironment1.logError(
                        "\"allClassesOpened\" is set to \"false\" when \"looseChangeLogFileIn\" is present in \""
                                + this.getStatementName()
                                + "\" statement at line "
                                + this.getStatementLine()
                                + ". This is not supported so \""
                                + "allClassesOpened"
                                + "\" will be set to \"true\"."
                );
            } else if (obfuscateOptions1.g == null || obfuscateOptions1.g.length == 0) {
                obfuscateOptions1.A = true;
                scriptEnvironment1.logWarning(
                        "\"allClassesOpened\" is set to \"false\" but no \"changeLogFileIn\" is present in \""
                                + this.getStatementName()
                                + "\" statement at line "
                                + this.getStatementLine()
                                + ". \""
                                + "allClassesOpened"
                                + "\" will be set to \"true\"."
                );
            }
        }

        if (!obfuscateOptions1.A) {
            int ba;
            if (obfuscateOptions1.r == 1) {
                obfuscateOptions1.r = 0;
                scriptEnvironment1.logError(
                        "\"autoReflectionHandling\" is set to \"normal\" while \"allClassesOpened\" is set to \"false\" in \""
                                + this.getStatementName()
                                + "\" statement at line "
                                + this.getStatementLine()
                                + ". This is not supported so \""
                                + "autoReflectionHandling"
                                + "\" will be set to \""
                                + "none"
                                + "\"."
                );
                ba = obfuscateOptions1.aj;
            } else {
                ba = obfuscateOptions1.aj;
            }

            if (ba == 1) {
                obfuscateOptions1.aj = 0;
                scriptEnvironment1.logError(
                        "\"obfuscateReferences\" is set to \"normal\" while \"allClassesOpened\" is set to \"false\" in \""
                                + this.getStatementName()
                                + "\" statement at line "
                                + this.getStatementLine()
                                + ". This is not supported so \""
                                + "obfuscateReferences"
                                + "\" will be set to \""
                                + "none"
                                + "\"."
                );
            }

            if (obfuscateOptions1.d7 == 1) {
                obfuscateOptions1.d7 = 0;
                scriptEnvironment1.logError(
                        "\"obfuscateParameters\" is set to \"normal\" while \"allClassesOpened\" is set to \"false\" in \""
                                + this.getStatementName()
                                + "\" statement at line "
                                + this.getStatementLine()
                                + ". This is not supported so \""
                                + "obfuscateParameters"
                                + "\" will be set to \""
                                + "none"
                                + "\"."
                );
            }

            if (obfuscateOptions1.aC != 0) {
                if (this.allowMethodParameterChanges != null && this.methodParameterChangesSpecified) {
                    scriptEnvironment1.logError(
                            "\"allowMethodParameterChanges\" is set to \"true\" while \"allClassesOpened\" is set to \"false\" in \""
                                    + this.getStatementName()
                                    + "\" statement at line "
                                    + this.getStatementLine()
                                    + ". This is not supported so \""
                                    + "allowMethodParameterChanges"
                                    + "\" will be set to \""
                                    + "false"
                                    + "\"."
                    );
                } else {
                    scriptEnvironment1.logError(
                            "\"methodParameterChanges\" set to \""
                                    + (obfuscateOptions1.aC == 1 ? "normal" : "random")
                                    + "\" while \""
                                    + "allClassesOpened"
                                    + "\" is set to \"false\" in \""
                                    + this.getStatementName()
                                    + "\" statement at line "
                                    + this.getStatementLine()
                                    + ". This is not supported so \""
                                    + "methodParameterChanges"
                                    + "\" will be set to \""
                                    + "none"
                                    + "\"."
                    );
                }

                obfuscateOptions1.aC = 0;
            }
        } else {
            if (obfuscateOptions1.r == 0) {
                if (obfuscateOptions1.q != null && obfuscateOptions1.q.length() > 0) {
                    scriptEnvironment1.logWarning(
                            "\"autoReflectionHandling\" is set to \"none\" but \"autoReflectionPackage\" has a value of \""
                                    + obfuscateOptions1.q
                                    + "\" in \""
                                    + this.getStatementName()
                                    + "\" statement at line "
                                    + this.getStatementLine()
                                    + ". The \""
                                    + "autoReflectionPackage"
                                    + "\" parameter will be ignored."
                    );
                }

                if (obfuscateOptions1.D != null && obfuscateOptions1.D.length() > 0) {
                    scriptEnvironment1.logWarning(
                            "\"autoReflectionHandling\" is set to \"none\" but \"autoReflectionHash\" has a value of \""
                                    + obfuscateOptions1.D
                                    + "\" in \""
                                    + this.getStatementName()
                                    + "\" statement at line "
                                    + this.getStatementLine()
                                    + ". The \""
                                    + "autoReflectionHash"
                                    + "\" parameter will be ignored."
                    );
                }
            }

            if (obfuscateOptions1.aj == 0 && obfuscateOptions1.ai != null && obfuscateOptions1.ai.length() > 0) {
                scriptEnvironment1.logWarning(
                        "\"obfuscateReferences\" is set to \"none\" but \"obfuscateReferencesPackage\" has a value of \""
                                + obfuscateOptions1.ai
                                + "\" in \""
                                + this.getStatementName()
                                + "\" statement at line "
                                + this.getStatementLine()
                                + ". The \""
                                + "obfuscateReferencesPackage"
                                + "\" parameter will be ignored."
                );
            }
        }

        if (obfuscateOptions1.o == 4) {
            scriptEnvironment1.logWarning(
                    "\"obfuscateFlow\" is set to \"extraAggressive\" which is now deprecated because of JVM bugs.  It will be set to \"aggressive\" instead."
            );
            obfuscateOptions1.o = 3;
        }

        if (obfuscateOptions1.dV && obfuscateOptions1.w) {
            scriptEnvironment1.logWarning(
                    "Both \"uniqueMethodNames\" and \"aggressiveMethodRenaming\" are set to \"true\" in \""
                            + this.getStatementName()
                            + "\" statement at line "
                            + this.getStatementLine()
                            + " which is contradictory. So \""
                            + "aggressiveMethodRenaming"
                            + "\" will be set to \"false\" instead."
            );
            obfuscateOptions1.w = false;
        }
    }

    public void readMixedCaseClassNamesOption(ObfuscateOptions obfuscateOptions1) {
        obfuscateOptions1.F = MixedCaseNamesMode.d;
        List list1 = super.parameterValues.getValues("mixedCaseClassNames");
        if (list1 != null && list1.size() > 0) {
            String string = (String) list1.get(0);
            if (string != null) {
                if (string.equals("true")) {
                    obfuscateOptions1.F = MixedCaseNamesMode.e;
                } else if (string.equals("false")) {
                    obfuscateOptions1.F = MixedCaseNamesMode.a;
                }
            }
        }
    }

    public void readDeriveGroupingsOption(ObfuscateOptions obfuscateOptions1) {
        obfuscateOptions1.y = false;
        List list1 = super.parameterValues.getValues("deriveGroupingsFromInputChangeLog");
        if (list1 != null && list1.size() > 0) {
            String string = (String) list1.get(0);
            if (string != null && string.equals("true")) {
                obfuscateOptions1.y = true;
            }
        }
    }

    public void readAutoReflectionHandlingOption(ObfuscateOptions obfuscateOptions1) {
        obfuscateOptions1.r = 0;
        List list1 = super.parameterValues.getValues("autoReflectionHandling");
        if (list1 != null && list1.size() > 0) {
            String string = (String) list1.get(0);
            if (string != null && string.equals("normal")) {
                obfuscateOptions1.r = 1;
            }
        }
    }

    public void readKeepGenericsInfoOption(ObfuscateOptions obfuscateOptions1) {
        obfuscateOptions1.u = 0;
        List list1 = super.parameterValues.getValues("keepGenericsInfo");
        if (list1 != null && list1.size() > 0) {
            String string = (String) list1.get(0);
            if (string != null && string.equals("false")) {
                obfuscateOptions1.u = 1;
            }
        }
    }

    public void readPreverifyOption(ObfuscateOptions obfuscateOptions1) {
        obfuscateOptions1.j = true;
        List list1 = super.parameterValues.getValues("preverify");
        if (list1 != null && list1.size() > 0) {
            String string = (String) list1.get(0);
            if (string != null && string.equals("false")) {
                obfuscateOptions1.j = false;
            }
        }
    }

    public void readAllowMethodParamChangesOption() {
        List list1 = super.parameterValues.getValues("allowMethodParameterChanges");
        if (list1 != null && list1.size() > 0) {
            String string = (String) list1.get(0);
            if (string != null) {
                if (string.equals("true")) {
                    this.allowMethodParameterChanges = Boolean.TRUE;
                } else {
                    this.allowMethodParameterChanges = Boolean.FALSE;
                }
            }
        }
    }

    public void readLineNumbersOption(ObfuscateOptions obfuscateOptions1) {
        obfuscateOptions1.i = 0;
        List list1 = super.parameterValues.getValues("lineNumbers");
        if (list1 != null && list1.size() > 0) {
            String string = (String) list1.get(0);
            if (string != null) {
                if (string.equals("keep")) {
                    obfuscateOptions1.i = 2;
                } else if (string.equals("scramble")) {
                    obfuscateOptions1.i = 1;
                }
            }
        }
    }

    public void readEncryptStringLiteralsOption(ObfuscateOptions obfuscateOptions1) {
        obfuscateOptions1.as = 4;
        List list1 = super.parameterValues.getValues("encryptStringLiterals");
        if (list1 != null && list1.size() > 0) {
            String string = (String) list1.get(0);
            if (string != null) {
                if (string.equals("none") || string.equals("false")) {
                    obfuscateOptions1.as = 0;
                } else if (string.equals("normal")) {
                    obfuscateOptions1.as = 1;
                } else if (string.equals("aggressive")) {
                    obfuscateOptions1.as = 2;
                } else if (string.equals("flowObfuscate")) {
                    obfuscateOptions1.as = 3;
                }
            }
        }
    }

    public void readExceptionObfuscationOption(ObfuscateOptions obfuscateOptions1) {
        obfuscateOptions1.C = 1;
        List list1 = super.parameterValues.getValues("exceptionObfuscation");
        if (list1 != null && list1.size() > 0) {
            String string = (String) list1.get(0);
            if (string != null) {
                obfuscateOptions1.C = ObfuscateOptions.parseExceptionLevel(string);
            }
        }
    }

    @Override
    public String getStatementName() {
        return "obfuscate";
    }

    public void readObfuscateReferencesOption(ObfuscateOptions obfuscateOptions1) {
        obfuscateOptions1.aj = 0;
        List list1 = super.parameterValues.getValues("obfuscateReferences");
        if (list1 != null && list1.size() > 0) {
            String string = (String) list1.get(0);
            if (string != null && string.equals("normal")) {
                obfuscateOptions1.aj = 1;
            }
        }
    }

    public void readEncryptIntegerConstantsOption(ObfuscateOptions obfuscateOptions1) {
        obfuscateOptions1.dZ = 0;
        List list1 = super.parameterValues.getValues("encryptIntegerConstants");
        if (list1 != null && list1.size() > 0) {
            String string = (String) list1.get(0);
            if (string != null) {
                if (string.equals("none")) {
                    obfuscateOptions1.dZ = 0;
                } else if (string.equals("normal")) {
                    obfuscateOptions1.dZ = 1;
                } else if (string.equals("aggressive")) {
                    obfuscateOptions1.dZ = 2;
                }
            }
        }
    }

    public void readNewNameCharactersOption(ObfuscateOptions obfuscateOptions1) {
        obfuscateOptions1.dr = true;
        List list1 = super.parameterValues.getValues("newNameCharacters");
        if (list1 != null && list1.size() > 0) {
            String string = (String) list1.get(0);
            if (string != null && string.equals("non-ASCII")) {
                obfuscateOptions1.dr = false;
            }
        }
    }

    public void readCollapsePackagesOption(ObfuscateOptions obfuscateOptions1, ScriptEnvironment scriptEnvironment1) throws ZkmException, IOException {
        List list1 = super.parameterValues.getValues("collapsePackagesWithDefault");
        if (list1 != null && list1.size() > 0) {
            String string = (String) list1.get(0);
            if (string != null) {
                obfuscateOptions1.s = true;
                ObservableHolder observableHolder = new ObservableHolder();
                String string1 = RootPackageNode.normalizePackageName(string, observableHolder);
                if (!observableHolder.isValueNull()) {
                    scriptEnvironment1.logFatalError(
                            "Invalid 'collapsePackagesWithDefault' value '" + string + "' : \"" + (String) observableHolder.getValue() + "\""
                    );
                    obfuscateOptions1.t = string1;
                } else {
                    obfuscateOptions1.t = string1;
                }
            } else {
                obfuscateOptions1.s = false;
                obfuscateOptions1.t = null;
            }
        }
    }

    public void readUniqueClassNamesOption(ObfuscateOptions obfuscateOptions1) {
        obfuscateOptions1.ay = false;
        List list1 = super.parameterValues.getValues("uniqueClassNames");
        if (list1 != null && list1.size() > 0) {
            String string = (String) list1.get(0);
            if (string != null) {
                if (string.equals("true")) {
                    obfuscateOptions1.ay = true;
                } else {
                    obfuscateOptions1.ay = false;
                }
            }
        }
    }
}
