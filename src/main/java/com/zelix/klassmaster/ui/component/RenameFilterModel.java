package com.zelix.klassmaster.ui.component;

import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.obfuscator.exclude.AccessFlagsSpec;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.ZkmScriptTokenMgrError;
import com.zelix.klassmaster.script.parser.ZkmScriptParseException;
import com.zelix.klassmaster.script.parser.ZkmScriptParser;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;
import com.zelix.klassmaster.script.parser.ast.ASTComplexAnnotationSpecifier;
import com.zelix.klassmaster.script.parser.ast.ASTContainingClause;
import com.zelix.klassmaster.script.parser.ast.ASTRenameFilterParameter;
import com.zelix.klassmaster.util.ChangeObservable;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class RenameFilterModel extends ChangeObservable {
    public List interfaceNames = new ArrayList();
    public List parameterTypes = new ArrayList();
    public List throwsNames = new ArrayList();
    public AccessFlagsSpec classAccessFlags;
    public AccessFlagsSpec memberAccessFlags;
    public String archivePattern;
    public List packageNames;
    public boolean packageNameOnly;
    public boolean excludeContainingPackage;
    public List searchNames;
    public String className;
    public boolean excludeContainingClass;
    public ASTContainingClause containingClause;
    public String superclassName;
    public String classAnnotation;
    public String fieldName;
    public String fieldType;
    public String methodName;
    public String linkedMethodName;
    public String memberAnnotation;
    public boolean includeSignatureClasses;
    public int parameterKind;
    public ScriptEnvironment scriptEnvironment;

    public boolean isLinkedClass() {
        return this.parameterKind == 2 && this.className.contains("<link>");
    }

    public void setMemberProtected(Integer integer) throws ZkmException, IOException {
        if (this.memberAccessFlags == null) {
            this.memberAccessFlags = new AccessFlagsSpec(integer);
        }

        this.memberAccessFlags.requireProtected();
        this.setChanged();
        this.notifyObservers();
    }

    public void setMemberNotTransient(boolean bl) throws ZkmException, IOException {
        if (this.memberAccessFlags == null) {
            this.memberAccessFlags = new AccessFlagsSpec(2);
        }

        this.memberAccessFlags.setTransientForbidden(bl);
        this.setChanged();
        this.notifyObservers();
    }

    public String getLinkPrefix() {
        return this.className.substring(0, this.className.indexOf("<link>"));
    }

    public void setMemberTransient(boolean bl) throws ZkmException, IOException {
        if (this.memberAccessFlags == null) {
            this.memberAccessFlags = new AccessFlagsSpec(2);
        }

        this.memberAccessFlags.setTransientRequired(bl);
        this.setChanged();
        this.notifyObservers();
    }

    public void setClassNotFinal(boolean bl) throws ZkmException, IOException {
        if (this.classAccessFlags == null) {
            this.classAccessFlags = new AccessFlagsSpec(1);
        }

        this.classAccessFlags.setFinalForbidden(bl);
        this.setChanged();
        this.notifyObservers();
    }

    public void setMemberStatic(boolean bl, Integer integer) throws ZkmException, IOException {
        if (this.memberAccessFlags == null) {
            this.memberAccessFlags = new AccessFlagsSpec(integer);
        }

        this.memberAccessFlags.setStaticRequired(bl);
        this.setChanged();
        this.notifyObservers();
    }

    public void setFieldType(String string) throws ZkmException, IOException {
        this.fieldType = string;
        this.setChanged();
        this.notifyObservers();
    }

    public void setMethodName(String string) throws ZkmException, IOException {
        this.methodName = string;
        this.setChanged();
        this.notifyObservers();
    }

    public void setClassAnnotation(String string) throws ZkmException, IOException {
        String string1 = string;
        if (string1.startsWith("@")) {
            string1 = string1.substring(1);
            this.classAnnotation = string1;
        } else {
            this.classAnnotation = string1;
        }

        this.setChanged();
        this.notifyObservers();
    }

    public String getPackagePattern() {
        StringBuffer stringBuffer = new StringBuffer();
        int ba = this.packageNames.size();
        if (ba > 0) {
            for (int i = 0; i < ba; i++) {
                stringBuffer.append((String) this.packageNames.get(i) + ".");
            }
        }

        return stringBuffer.toString();
    }

    public String getMethodName() {
        return this.methodName;
    }

    public void setPackagePattern(String string) throws ZkmException, IOException {
        StringTokenizer stringTokenizer = new StringTokenizer(string, ".");
        this.packageNames = new ArrayList();

        while (stringTokenizer.hasMoreTokens()) {
            String string1 = stringTokenizer.nextToken().trim();
            this.packageNames.add(string1);
        }

        this.setChanged();
        this.notifyObservers();
    }

    public String getFieldType() {
        return this.fieldType;
    }

    public String getParameterTypesText() {
        StringBuffer stringBuffer = new StringBuffer();
        if (this.parameterTypes != null) {
            int ba = 0;
            int bb = 0;

            for (List list1 = this.parameterTypes; bb < list1.size(); list1 = this.parameterTypes) {
                stringBuffer.append((String) this.parameterTypes.get(ba));
                if (ba < this.parameterTypes.size() - 1) {
                    stringBuffer.append(", ");
                }

                bb = ++ba;
            }
        }

        return stringBuffer.toString();
    }

    public void setClassPublic() throws ZkmException, IOException {
        if (this.classAccessFlags == null) {
            this.classAccessFlags = new AccessFlagsSpec(1);
        }

        this.classAccessFlags.requirePublic();
        this.setChanged();
        this.notifyObservers();
    }

    public void setInterfaceNames(String string) throws ZkmException, IOException {
        StringTokenizer stringTokenizer = new StringTokenizer(string, ",");
        this.interfaceNames = new ArrayList();

        while (stringTokenizer.hasMoreTokens()) {
            this.interfaceNames.add(stringTokenizer.nextToken().trim());
        }

        this.setChanged();
        this.notifyObservers();
    }

    public void setClassNotAbstract(boolean bl) throws ZkmException, IOException {
        if (this.classAccessFlags == null) {
            this.classAccessFlags = new AccessFlagsSpec(1);
        }

        this.classAccessFlags.setAbstractForbidden(bl);
        this.setChanged();
        this.notifyObservers();
    }

    public void setMemberFinal(boolean bl, Integer integer) throws ZkmException, IOException {
        if (this.memberAccessFlags == null) {
            this.memberAccessFlags = new AccessFlagsSpec(integer);
        }

        this.memberAccessFlags.setFinalRequired(bl);
        this.setChanged();
        this.notifyObservers();
    }

    public String getThrowsText() {
        if (this.throwsNames != null && this.throwsNames.size() > 0) {
            StringBuffer stringBuffer = new StringBuffer();
            int ba = 0;
            int bb = 0;

            for (List list1 = this.throwsNames; bb < list1.size(); list1 = this.throwsNames) {
                stringBuffer.append((String) this.throwsNames.get(ba));
                if (ba < this.throwsNames.size() - 1) {
                    stringBuffer.append(", ");
                }

                bb = ++ba;
            }

            return stringBuffer.toString();
        } else {
            return "";
        }
    }

    public void setMemberNotSynchronized(boolean bl) throws ZkmException, IOException {
        if (this.memberAccessFlags == null) {
            this.memberAccessFlags = new AccessFlagsSpec(3);
        }

        this.memberAccessFlags.setSynchronizedForbidden(bl);
        this.setChanged();
        this.notifyObservers();
    }

    public void setMemberPrivate(Integer integer) throws ZkmException, IOException {
        if (this.memberAccessFlags == null) {
            this.memberAccessFlags = new AccessFlagsSpec(integer);
        }

        this.memberAccessFlags.requirePrivate();
        this.setChanged();
        this.notifyObservers();
    }

    public ScriptEnvironment getScriptEnvironment() {
        return this.scriptEnvironment;
    }

    public String getSuperclassName() {
        return this.superclassName != null ? this.superclassName : "";
    }

    public String getTextBeforeLink(String string) {
        return string.substring(0, string.indexOf("<link>"));
    }

    public static void executeFilterParameter(ScriptEnvironment scriptEnvironment1, String string) throws ZkmException, IOException {
        BufferedReader bufferedReader = new BufferedReader(new StringReader(string + ";"));
        ZkmScriptParser zkmScriptParser = new ZkmScriptParser(bufferedReader);

        try {
            zkmScriptParser.SingleRenameFilterParameter().execute(null, scriptEnvironment1);
        } catch (ZkmScriptParseException zkmScriptParseException) {
            throw new ZkmProcessingException(zkmScriptParseException.getMessage());
        } catch (ZkmScriptTokenMgrError zkmScriptTokenMgrError) {
            throw new ZkmProcessingException(zkmScriptTokenMgrError.getMessage());
        }
    }

    public void setMemberAbstract(boolean bl) throws ZkmException, IOException {
        if (this.memberAccessFlags == null) {
            this.memberAccessFlags = new AccessFlagsSpec(3);
        }

        this.memberAccessFlags.setAbstractRequired(bl);
        this.setChanged();
        this.notifyObservers();
    }

    public boolean isExcludeContainingPackage() {
        return this.excludeContainingPackage;
    }

    public void setExcludeContainingPackage(boolean excludeContainingPackage) throws ZkmException, IOException {
        this.excludeContainingPackage = excludeContainingPackage;
        this.setChanged();
        this.notifyObservers();
    }

    public void setMemberAnnotation(String string) throws ZkmException, IOException {
        String string1 = string;
        if (string1.startsWith("@")) {
            string1 = string1.substring(1);
            this.memberAnnotation = string1;
        } else {
            this.memberAnnotation = string1;
        }

        this.setChanged();
        this.notifyObservers();
    }

    public void setLinkedClassSuffix(String string) throws ZkmException, IOException {
        this.className = "<link>" + string;
        this.setChanged();
        this.notifyObservers();
    }

    public void setMemberNotVolatile(boolean bl) throws ZkmException, IOException {
        if (this.memberAccessFlags == null) {
            this.memberAccessFlags = new AccessFlagsSpec(2);
        }

        this.memberAccessFlags.setVolatileForbidden(bl);
        this.setChanged();
        this.notifyObservers();
    }

    public void setClassFinal(boolean bl) throws ZkmException, IOException {
        if (this.classAccessFlags == null) {
            this.classAccessFlags = new AccessFlagsSpec(1);
        }

        this.classAccessFlags.setFinalRequired(bl);
        this.setChanged();
        this.notifyObservers();
    }

    public void setMemberNotAbstract(boolean bl) throws ZkmException, IOException {
        if (this.memberAccessFlags == null) {
            this.memberAccessFlags = new AccessFlagsSpec(3);
        }

        this.memberAccessFlags.setAbstractForbidden(bl);
        this.setChanged();
        this.notifyObservers();
    }

    public final String toScriptText() {
        java.lang.StringBuilder stringBuilder9 = null;
        java.util.List list2 = null;
        int bl = 0;
        int bj = 0;
        java.lang.StringBuilder stringBuilder2 = null;
        java.lang.StringBuilder stringBuilder1 = null;
        int ba;
        StringBuilder stringBuilder;
        RenameFilterModel renameFilterModel1;
        int flowControlKey = ZkmScriptSimpleNode.getFlowControlKey();
        stringBuilder = new StringBuilder();
        ba = flowControlKey;
        renameFilterModel1 = this;
        label456:
        if (ba != 0) {
            if (this.classAnnotation != null) {
                renameFilterModel1 = this;
                if (ba == 0) {
                    break label456;
                }

                if (this.classAnnotation.length() > 0) {
                    String string1;
                    label450:
                    {
                        String string = this.classAnnotation;
                        string1 = "@";
                        if (ba != 0) {
                            if (this.classAnnotation.startsWith("@")) {
                                stringBuilder1 = stringBuilder;
                                string1 = this.classAnnotation;
                                break label450;
                            }

                            string = this.classAnnotation;
                            string1 = "(";
                        }

                        if (!string.startsWith(string1)) {
                            stringBuilder.append('@');
                            stringBuilder1 = stringBuilder;
                            string1 = this.classAnnotation;
                        } else {
                            stringBuilder1 = stringBuilder;
                            string1 = this.classAnnotation;
                        }
                    }

                    stringBuilder1.append(string1);
                    stringBuilder.append(" ");
                }
            }

            renameFilterModel1 = this;
        }

        label442:
        {
            if (ba != 0) {
                if (renameFilterModel1.classAccessFlags == null) {
                    break label442;
                }

                renameFilterModel1 = this;
            }

            if (renameFilterModel1.isClassPackagePrivate()) {
                stringBuilder.append("package ");
            }
        }

        String string2;
        label436:
        {
            AccessFlagsSpec accessFlagsSpec1;
            if (ba != 0) {
                if (this.classAccessFlags == null) {
                    string2 = "";
                    break label436;
                }

                accessFlagsSpec1 = this.classAccessFlags;
            } else {
                accessFlagsSpec1 = this.classAccessFlags;
            }

            string2 = accessFlagsSpec1.toSpecString();
        }

        int bb;
        label430:
        {
            stringBuilder.append(string2);
            bb = this.packageNames.size();
            if (ba != 0) {
                if (this.archivePattern == null) {
                    break label430;
                }

                stringBuilder.append("\"");
                stringBuilder.append(this.archivePattern);
                stringBuilder.append("\"");
            }

            stringBuilder.append("!");
        }

        label459:
        {
            label490:
            {
                label422:
                {
                    label461:
                    {
                        int bh = bb;
                        if (ba != 0) {
                            if (bb <= 0) {
                                break label461;
                            }

                            bh = 0;
                        }

                        int bc = bh;

                        while (bc < bb) {
                            stringBuilder2 = stringBuilder.append((String) this.packageNames.get(bc) + '.');
                            if (ba == 0) {
                                break label490;
                            }

                            bc++;
                            if (ba == 0) {
                                break;
                            }
                        }

                        renameFilterModel1 = this;
                        if (ba != 0) {
                            if (this.packageNameOnly) {
                                stringBuilder.append(".");
                            }

                            renameFilterModel1 = this;
                        }

                        if (ba == 0) {
                            break label422;
                        }

                        if (renameFilterModel1.excludeContainingPackage) {
                            stringBuilder.append("^");
                        }
                    }

                    renameFilterModel1 = this;
                }

                label400:
                if (ba != 0) {
                    if (renameFilterModel1.className != null) {
                        stringBuilder.append(this.className);
                        renameFilterModel1 = this;
                        if (ba == 0) {
                            break label400;
                        }

                        if (this.excludeContainingClass) {
                            stringBuilder.append("^");
                        }
                    }

                    renameFilterModel1 = this;
                }

                if (ba != 0) {
                    if (renameFilterModel1.containingClause != null) {
                        stringBuilder.append(' ' + this.containingClause.toScriptText());
                    }

                    renameFilterModel1 = this;
                }

                label391:
                if (ba != 0) {
                    if (renameFilterModel1.superclassName != null) {
                        renameFilterModel1 = this;
                        if (ba == 0) {
                            break label391;
                        }

                        if (this.superclassName.length() > 0) {
                            stringBuilder.append(" extends " + this.superclassName);
                        }
                    }

                    renameFilterModel1 = this;
                }

                label465:
                {
                    label383:
                    {
                        List list1 = renameFilterModel1.interfaceNames;
                        if (ba != 0) {
                            if (renameFilterModel1.interfaceNames.size() > 0) {
                                stringBuilder.append(" implements ");
                                int bd = 0;
                                int bi = 0;

                                for (List list3 = this.interfaceNames; bi < list3.size(); list3 = this.interfaceNames) {
                                    stringBuilder.append((String) this.interfaceNames.get(bd));
                                    if (ba != 0) {
                                        bj = bd;
                                        if (ba == 0) {
                                            break label383;
                                        }

                                        if (bd < this.interfaceNames.size() - 1) {
                                            stringBuilder.append(", ");
                                        }

                                        bd++;
                                    }

                                    if (ba == 0) {
                                        break;
                                    }

                                    bi = bd;
                                }
                            }

                            renameFilterModel1 = this;
                            if (ba == 0) {
                                break label465;
                            }

                            list1 = this.searchNames;
                        }

                        bj = list1.size();
                    }

                    if (bj > 0) {
                        stringBuilder.append(" search ");
                        int be = 0;
                        int bk = 0;

                        for (List list4 = this.searchNames; bk < list4.size(); list4 = this.searchNames) {
                            stringBuilder2 = stringBuilder;
                            string2 = (String) this.searchNames.get(be);
                            if (ba == 0) {
                                break label459;
                            }

                            stringBuilder.append(string2);
                            if (ba != 0) {
                                if (be < this.searchNames.size() - 1) {
                                    stringBuilder.append(", ");
                                }

                                be++;
                            }

                            if (ba == 0) {
                                break;
                            }

                            bk = be;
                        }
                    }

                    renameFilterModel1 = this;
                }

                label351:
                if (ba != 0) {
                    if (renameFilterModel1.memberAnnotation != null) {
                        renameFilterModel1 = this;
                        if (ba == 0) {
                            break label351;
                        }

                        if (this.memberAnnotation.length() > 0 && ba != 0) {
                            stringBuilder.append(" ");
                            StringBuilder stringBuilder3;
                            if (!this.memberAnnotation.startsWith("@")) {
                                if (!this.memberAnnotation.startsWith("(")) {
                                    stringBuilder.append('@');
                                    stringBuilder3 = stringBuilder;
                                    string2 = this.memberAnnotation;
                                } else {
                                    stringBuilder3 = stringBuilder;
                                    string2 = this.memberAnnotation;
                                }
                            } else {
                                stringBuilder3 = stringBuilder;
                                string2 = this.memberAnnotation;
                            }

                            stringBuilder3.append(string2);
                            stringBuilder.append(" ");
                        }
                    }

                    renameFilterModel1 = this;
                }

                if (renameFilterModel1.memberAccessFlags != null) {
                    bl = stringBuilder.length();
                    label342:
                    if (ba != 0) {
                        if (bl > 0) {
                            bl = stringBuilder.charAt(stringBuilder.length() - 1);
                            if (ba == 0) {
                                break label342;
                            }

                            if (bl != 32) {
                                stringBuilder.append(" ");
                            }
                        }

                        bl = ((this.isMemberPackagePrivate()) ? 1 : 0);
                    }

                    if (bl != 0) {
                        stringBuilder.append("package ");
                    }
                }

                stringBuilder2 = stringBuilder;
            }

            AccessFlagsSpec accessFlagsSpec2;
            if (ba != 0) {
                if (this.memberAccessFlags == null) {
                    string2 = "";
                    break label459;
                }

                accessFlagsSpec2 = this.memberAccessFlags;
            } else {
                accessFlagsSpec2 = this.memberAccessFlags;
            }

            string2 = accessFlagsSpec2.toSpecString();
        }

        stringBuilder2.append(string2);
        renameFilterModel1 = this;
        if (ba != 0) {
            label315:
            if (this.fieldType != null && ba != 0) {
                StringBuilder stringBuilder4;
                if (stringBuilder.length() > 0) {
                    if (ba == 0) {
                        break label315;
                    }

                    if (stringBuilder.charAt(stringBuilder.length() - 1) != ' ') {
                        stringBuilder.append(' ');
                        stringBuilder4 = stringBuilder;
                        string2 = this.fieldType;
                    } else {
                        stringBuilder4 = stringBuilder;
                        string2 = this.fieldType;
                    }
                } else {
                    stringBuilder4 = stringBuilder;
                    string2 = this.fieldType;
                }

                stringBuilder4.append(string2);
            }

            renameFilterModel1 = this;
        }

        if (ba != 0) {
            label302:
            if (renameFilterModel1.fieldName != null && ba != 0) {
                StringBuilder stringBuilder5;
                if (stringBuilder.length() > 0) {
                    if (ba == 0) {
                        break label302;
                    }

                    if (stringBuilder.charAt(stringBuilder.length() - 1) != ' ') {
                        stringBuilder.append(' ');
                        stringBuilder5 = stringBuilder;
                        string2 = this.fieldName;
                    } else {
                        stringBuilder5 = stringBuilder;
                        string2 = this.fieldName;
                    }
                } else {
                    stringBuilder5 = stringBuilder;
                    string2 = this.fieldName;
                }

                stringBuilder5.append(string2);
            }

            renameFilterModel1 = this;
        }

        label475:
        {
            label297:
            {
                label476:
                {
                    label477:
                    {
                        if (ba != 0) {
                            if (renameFilterModel1.linkedMethodName != null) {
                                break label477;
                            }

                            renameFilterModel1 = this;
                        }

                        if (ba == 0) {
                            break label297;
                        }

                        if (renameFilterModel1.methodName == null) {
                            break label476;
                        }
                    }

                    label478:
                    {
                        StringBuilder stringBuilder6 = stringBuilder;
                        if (ba != 0) {
                            if (stringBuilder.length() <= 0) {
                                break label478;
                            }

                            stringBuilder6 = stringBuilder;
                        }

                        if (ba != 0 && stringBuilder6.charAt(stringBuilder.length() - 1) != ' ') {
                            stringBuilder.append(" ");
                        }
                    }

                    label276:
                    {
                        StringBuilder stringBuilder7;
                        if (this.linkedMethodName != null) {
                            stringBuilder.append(this.linkedMethodName);
                            if (ba != 0) {
                                break label276;
                            }

                            stringBuilder7 = stringBuilder;
                            string2 = this.methodName;
                        } else {
                            stringBuilder7 = stringBuilder;
                            string2 = this.methodName;
                        }

                        stringBuilder7.append(string2);
                    }

                    label479:
                    {
                        label269:
                        {
                            StringBuilder stringBuilder8 = stringBuilder;
                            char bo = '(';
                            if (ba != 0) {
                                stringBuilder.append('(');
                                if (this.parameterTypes != null) {
                                    list2 = this.parameterTypes;
                                    if (ba == 0) {
                                        break label479;
                                    }

                                    if (this.parameterTypes.size() > 0) {
                                        int bf = 0;
                                        int bm = 0;

                                        for (List list5 = this.parameterTypes; bm < list5.size(); list5 = this.parameterTypes) {
                                            if (ba == 0) {
                                                break label269;
                                            }

                                            stringBuilder.append((String) this.parameterTypes.get(bf));
                                            if (ba != 0) {
                                                if (bf < this.parameterTypes.size() - 1) {
                                                    stringBuilder.append(", ");
                                                }

                                                bf++;
                                            }

                                            if (ba == 0) {
                                                break;
                                            }

                                            bm = bf;
                                        }
                                    }
                                }

                                stringBuilder8 = stringBuilder;
                                bo = ')';
                            }

                            stringBuilder8.append(bo);
                        }

                        renameFilterModel1 = this;
                        if (ba == 0) {
                            break label297;
                        }

                        list2 = this.throwsNames;
                    }

                    if (list2.size() > 0) {
                        stringBuilder.append(" throws ");
                        int bg = 0;
                        int bn = bg;

                        for (List list6 = this.throwsNames; bn < list6.size(); list6 = this.throwsNames) {
                            stringBuilder9 = stringBuilder;
                            string2 = (String) this.throwsNames.get(bg);
                            if (ba == 0) {
                                break label475;
                            }

                            stringBuilder.append(string2);
                            if (ba != 0) {
                                if (bg < this.throwsNames.size() - 1) {
                                    stringBuilder.append(", ");
                                }

                                bg++;
                            }

                            if (ba == 0) {
                                break;
                            }

                            bn = bg;
                        }
                    }
                }

                renameFilterModel1 = this;
            }

            if (!renameFilterModel1.includeSignatureClasses) {
                return stringBuilder.toString();
            }

            stringBuilder9 = stringBuilder;
            string2 = " +signatureClasses";
        }

        stringBuilder9.append(string2);
        return stringBuilder.toString();
    }

    public String getMemberAnnotation() {
        return this.memberAnnotation != null ? this.memberAnnotation : "";
    }

    public void setClassName(String string) throws ZkmException, IOException {
        boolean bl = !this.className.equals(string);
        this.className = string;
        if (bl) {
            this.setChanged();
            this.notifyObservers();
        }
    }

    public void setMemberSynchronized(boolean bl) throws ZkmException, IOException {
        if (this.memberAccessFlags == null) {
            this.memberAccessFlags = new AccessFlagsSpec(3);
        }

        this.memberAccessFlags.setSynchronizedRequired(bl);
        this.setChanged();
        this.notifyObservers();
    }

    public RenameFilterModel(ASTRenameFilterParameter aSTRenameFilterParameter) {
        if (aSTRenameFilterParameter.classAccessFlags != null) {
            this.classAccessFlags = (AccessFlagsSpec) aSTRenameFilterParameter.classAccessFlags.clone();
        }

        if (aSTRenameFilterParameter.memberAccessFlags != null) {
            this.memberAccessFlags = (AccessFlagsSpec) aSTRenameFilterParameter.memberAccessFlags.clone();
        }

        this.archivePattern = aSTRenameFilterParameter.archivePathPattern;
        this.packageNames = aSTRenameFilterParameter.getPackageSegments();
        if (aSTRenameFilterParameter.packagePattern != null) {
            this.packageNameOnly = aSTRenameFilterParameter.packagePattern.hasDotSuffix();
            this.excludeContainingPackage = aSTRenameFilterParameter.packagePattern.hasCaretTag();
        }

        if (aSTRenameFilterParameter.packagePattern != null) {
        }

        this.searchNames = ZkmUtils.copyArrayList(aSTRenameFilterParameter.linkPackageNames);
        if (aSTRenameFilterParameter.linkClassName != null) {
            this.className = aSTRenameFilterParameter.linkClassName;
        } else if (aSTRenameFilterParameter.classNamePattern != null) {
            this.className = aSTRenameFilterParameter.classNamePattern.getSpecText();
            this.excludeContainingClass = aSTRenameFilterParameter.classNamePattern.hasCaretTag();
            if (this.excludeContainingClass) {
                this.className = this.className.substring(0, this.className.length() - 1);
            }
        }

        if (aSTRenameFilterParameter.containingClause != null) {
            this.containingClause = aSTRenameFilterParameter.containingClause;
        }

        if (aSTRenameFilterParameter.extendsClassName != null) {
            this.superclassName = ASTRenameFilterParameter.toDottedName(aSTRenameFilterParameter.extendsClassName);
        }

        if (aSTRenameFilterParameter.implementsNames != null) {
            for (int i = 0; i < aSTRenameFilterParameter.implementsNames.size(); i++) {
                String string = (String) aSTRenameFilterParameter.implementsNames.get(i);
                this.interfaceNames.add(ASTRenameFilterParameter.toDottedName(string));
            }
        }

        if (aSTRenameFilterParameter.classAnnotation != null) {
            this.classAnnotation = aSTRenameFilterParameter.classAnnotation.getSpecText();
            if (this.classAnnotation.startsWith("@")) {
                this.classAnnotation = this.classAnnotation.substring(1);
            }
        }

        if (aSTRenameFilterParameter.fieldSpecifier != null) {
            this.fieldName = aSTRenameFilterParameter.fieldSpecifier.getSpecText();
        }

        if (aSTRenameFilterParameter.fieldType != null) {
            this.fieldType = ASTRenameFilterParameter.toJavaTypeName(aSTRenameFilterParameter.fieldType);
        }

        if (aSTRenameFilterParameter.methodSpecifier != null) {
            this.methodName = aSTRenameFilterParameter.methodSpecifier.getSpecText();
        }

        this.linkedMethodName = aSTRenameFilterParameter.linkMethodSignature;
        if (aSTRenameFilterParameter.argsPattern != null && aSTRenameFilterParameter.argsPattern.argsText.length() > 0) {
            String string1 = ASTRenameFilterParameter.formatArgsWithAnnotations(aSTRenameFilterParameter.argsPattern, (ASTComplexAnnotationSpecifier[]) null);
            StringTokenizer stringTokenizer = new StringTokenizer(string1, ",");

            while (stringTokenizer.hasMoreTokens()) {
                this.parameterTypes.add(stringTokenizer.nextToken());
            }
        }

        if (aSTRenameFilterParameter.memberAnnotation != null) {
            this.memberAnnotation = aSTRenameFilterParameter.memberAnnotation.getSpecText();
            if (this.memberAnnotation.startsWith("@")) {
                this.memberAnnotation = this.memberAnnotation.substring(1);
            }
        }

        RenameFilterModel renameFilterModel1;
        boolean bl;
        if (aSTRenameFilterParameter.throwsTypes != null) {
            for (int i = 0; i < aSTRenameFilterParameter.throwsTypes.size(); i++) {
                String string2 = (String) aSTRenameFilterParameter.throwsTypes.get(i);
                this.throwsNames.add(ASTRenameFilterParameter.toDottedName(string2));
            }

            renameFilterModel1 = this;
            bl = aSTRenameFilterParameter.plusSignatureClasses;
        } else {
            renameFilterModel1 = this;
            bl = aSTRenameFilterParameter.plusSignatureClasses;
        }

        renameFilterModel1.includeSignatureClasses = bl;
        this.parameterKind = aSTRenameFilterParameter.specifierKind;
        this.scriptEnvironment = aSTRenameFilterParameter.scriptEnvironment;
    }

    @Override
    public String toString() {
        return this.toScriptText();
    }

    public void setThrowsNames(String string) throws ZkmException, IOException {
        StringTokenizer stringTokenizer = new StringTokenizer(string, ",");
        this.throwsNames = new ArrayList();

        while (stringTokenizer.hasMoreTokens()) {
            this.throwsNames.add(stringTokenizer.nextToken().trim());
        }

        this.setChanged();
        this.notifyObservers();
    }

    public boolean isExcludeContainingClass() {
        return this.excludeContainingClass;
    }

    public boolean isClassExcluded() {
        return this.parameterKind == 2 || this.excludeContainingClass;
    }

    public String getClassAnnotation() {
        return this.classAnnotation != null ? this.classAnnotation : "";
    }

    public void setMemberPackagePrivate(Integer integer) throws ZkmException, IOException {
        if (this.memberAccessFlags == null) {
            this.memberAccessFlags = new AccessFlagsSpec(integer);
        }

        this.memberAccessFlags.requirePackage();
        this.setChanged();
        this.notifyObservers();
    }

    public void setFieldName(String string) throws ZkmException, IOException {
        this.fieldName = string;
        this.setChanged();
        this.notifyObservers();
    }

    public void clearClassAccess() throws ZkmException, IOException {
        if (this.classAccessFlags == null) {
            this.classAccessFlags = new AccessFlagsSpec(1);
        }

        this.classAccessFlags.clearRequiredAccess();
        this.setChanged();
        this.notifyObservers();
    }

    public void setMemberNotFinal(boolean bl, Integer integer) throws ZkmException, IOException {
        if (this.memberAccessFlags == null) {
            this.memberAccessFlags = new AccessFlagsSpec(integer);
        }

        this.memberAccessFlags.setFinalForbidden(bl);
        this.setChanged();
        this.notifyObservers();
    }

    public String buildDescription(int ba) {
        String string;
        String string1;
        switch (ba) {
            case 1:
                string = "Exclude the name of ";
                string1 = "Also exclude the name of ";
                break;
            case 2:
                string = "Exclude ";
                string1 = "Also exclude ";
                break;
            case 3:
                string = "Exclude references to ";
                string1 = "Also exclude references to ";
                break;
            default:
                string = "Exclude the name of ";
                string1 = "Also exclude the name of ";
        }

        int bb = 1;
        char bc = 'a';
        StringBuilder stringBuilder = new StringBuilder();
        switch (this.parameterKind) {
            case 1:
                stringBuilder.append(string + "all packages:" + HiddenOptionFlags.LINE_SEPARATOR);
                if (this.packageNames != null && this.packageNames.size() > 0) {
                    stringBuilder.append("\t" + bb + ") that match \"" + this.getPackagePattern() + "\"" + HiddenOptionFlags.LINE_SEPARATOR);
                }
                break;
            case 2:
                if (this.isLinkedClass()) {
                    if (this.hasLinkPrefix()) {
                        stringBuilder.append(
                                "Exclude the prefix \""
                                        + this.getLinkPrefix()
                                        + "\" and the suffix \""
                                        + this.getLinkSuffix()
                                        + "\" from being renamed and link the non-prefix and non-suffix parts of the name in all classes:"
                                        + HiddenOptionFlags.LINE_SEPARATOR
                        );
                    } else {
                        stringBuilder.append(
                                "Exclude the suffix \""
                                        + this.getLinkSuffix()
                                        + "\" from being renamed and link the non-suffix part of the name in all classes:"
                                        + HiddenOptionFlags.LINE_SEPARATOR
                        );
                    }

                    if (this.classAnnotation != null) {
                        stringBuilder.append(
                                "\t" + bb++ + ") that are annotated by a class matching \"" + this.classAnnotation + "\"" + HiddenOptionFlags.LINE_SEPARATOR
                        );
                    }

                    if (this.packageNames != null && this.packageNames.size() > 0) {
                        stringBuilder.append(
                                "\t" + bb++ + ") that are in a package that matches \"" + this.getPackagePattern() + "\"" + HiddenOptionFlags.LINE_SEPARATOR
                        );
                    } else {
                        stringBuilder.append("\t" + bb++ + ") that are not in a package" + HiddenOptionFlags.LINE_SEPARATOR);
                    }

                    if (this.hasLinkPrefix()) {
                        stringBuilder.append(
                                "\t"
                                        + bb++
                                        + ") with a name that starts with \""
                                        + this.getLinkPrefix()
                                        + "\" and ends with \""
                                        + this.getLinkSuffix()
                                        + "\""
                                        + HiddenOptionFlags.LINE_SEPARATOR
                        );
                    } else {
                        stringBuilder.append("\t" + bb++ + ") with a name that ends with \"" + this.getLinkSuffix() + "\"" + HiddenOptionFlags.LINE_SEPARATOR);
                    }

                    String string13 = this.getSuperclassName();
                    if (string13.length() > 0) {
                        stringBuilder.append("\t" + bb++ + ") that extend \"" + string13 + "\"" + HiddenOptionFlags.LINE_SEPARATOR);
                    }

                    String string15 = this.getInterfaceNamesText();
                    if (string15.length() > 0) {
                        stringBuilder.append("\t" + bb + ") that implement \"" + string15 + "\"" + HiddenOptionFlags.LINE_SEPARATOR);
                    }
                } else {
                    stringBuilder.append(string + "all classes:" + HiddenOptionFlags.LINE_SEPARATOR);
                    if (this.classAnnotation != null) {
                        stringBuilder.append(
                                "\t" + bb++ + ") that are annotated by a class matching \"" + this.classAnnotation + "\"" + HiddenOptionFlags.LINE_SEPARATOR
                        );
                    }

                    String string12 = this.getClassAccessText();
                    if (string12.length() > 0) {
                        stringBuilder.append("\t" + bb++ + ") that are \"" + string12 + "\"" + HiddenOptionFlags.LINE_SEPARATOR);
                    }

                    if (this.packageNames != null && this.packageNames.size() > 0) {
                        stringBuilder.append(
                                "\t" + bb++ + ") that are in a package that matches \"" + this.getPackagePattern() + "\"" + HiddenOptionFlags.LINE_SEPARATOR
                        );
                    } else {
                        stringBuilder.append("\t" + bb++ + ") that are not in a package" + HiddenOptionFlags.LINE_SEPARATOR);
                    }

                    stringBuilder.append("\t" + bb++ + ") with a name that matches \"" + this.className + "\"" + HiddenOptionFlags.LINE_SEPARATOR);
                    String string14 = this.getContainingText();
                    if (string14.length() > 0) {
                        stringBuilder.append("\t" + bb++ + ") that contains fields or methods matching \"" + string14 + "\"" + HiddenOptionFlags.LINE_SEPARATOR);
                    }

                    String string16 = this.getSuperclassName();
                    if (string16.length() > 0) {
                        stringBuilder.append("\t" + bb++ + ") that extend \"" + string16 + "\"" + HiddenOptionFlags.LINE_SEPARATOR);
                    }

                    String string17 = this.getInterfaceNamesText();
                    if (string17.length() > 0) {
                        stringBuilder.append("\t" + bb + ") that implement \"" + string17 + "\"" + HiddenOptionFlags.LINE_SEPARATOR);
                    }
                }

                if (this.packageNames != null && this.packageNames.size() > 0 && this.excludeContainingPackage) {
                    stringBuilder.append(string1 + "any package containing a matching class" + HiddenOptionFlags.LINE_SEPARATOR);
                }
                break;
            case 3:
                stringBuilder.append(string + "all fields:" + HiddenOptionFlags.LINE_SEPARATOR);
                String string2 = this.getMemberAccessText();
                if (this.memberAnnotation != null) {
                    stringBuilder.append(
                            "\t" + bb++ + ") that are annotated by a class matching \"" + this.memberAnnotation + "\"" + HiddenOptionFlags.LINE_SEPARATOR
                    );
                }

                if (string2.length() > 0) {
                    stringBuilder.append("\t" + bb++ + ") that are \"" + string2 + "\"" + HiddenOptionFlags.LINE_SEPARATOR);
                }

                if (this.fieldType != null && this.fieldType.length() > 0) {
                    stringBuilder.append("\t" + bb++ + ") that are of type \"" + this.fieldType + "\"" + HiddenOptionFlags.LINE_SEPARATOR);
                }

                stringBuilder.append("\t" + bb++ + ") that have a name matching \"" + this.fieldName + "\"" + HiddenOptionFlags.LINE_SEPARATOR);
                stringBuilder.append("\t" + bb + ") that are contained within a class:" + HiddenOptionFlags.LINE_SEPARATOR);
                if (this.classAnnotation != null) {
                    StringBuilder stringBuilder1 = new StringBuilder().append("\t\t");
                    bc = 'b';
                    stringBuilder.append(
                            stringBuilder1.append('a')
                                    .append(") that is annotated by a class matching \"")
                                    .append(this.classAnnotation)
                                    .append("\"")
                                    .append(HiddenOptionFlags.LINE_SEPARATOR)
                                    .toString()
                    );
                }

                String string3 = this.getClassAccessText();
                if (string3.length() > 0) {
                    stringBuilder.append("\t\t" + bc++ + ") that is \"" + string3 + "\"" + HiddenOptionFlags.LINE_SEPARATOR);
                }

                if (this.packageNames != null && this.packageNames.size() > 0) {
                    stringBuilder.append(
                            "\t\t" + bc++ + ") that is in a package that matches \"" + this.getPackagePattern() + "\"" + HiddenOptionFlags.LINE_SEPARATOR
                    );
                } else {
                    stringBuilder.append("\t\t" + bc++ + ") that is not in a package" + HiddenOptionFlags.LINE_SEPARATOR);
                }

                stringBuilder.append("\t\t" + bc++ + ") with a name that matches \"" + this.className + "\"" + HiddenOptionFlags.LINE_SEPARATOR);
                String string4 = this.getSuperclassName();
                if (string4.length() > 0) {
                    stringBuilder.append("\t\t" + bc++ + ") that extends \"" + string4 + "\"" + HiddenOptionFlags.LINE_SEPARATOR);
                }

                String string5 = this.getInterfaceNamesText();
                if (string5.length() > 0) {
                    stringBuilder.append("\t\t" + bc + ") that implements \"" + string5 + "\"" + HiddenOptionFlags.LINE_SEPARATOR);
                }

                if (this.packageNames != null && this.packageNames.size() > 0 && this.excludeContainingPackage) {
                    stringBuilder.append(string1 + "any package containing a class containing a matching field" + HiddenOptionFlags.LINE_SEPARATOR);
                }

                if (this.excludeContainingClass) {
                    stringBuilder.append(string1 + "any class containing a matching field" + HiddenOptionFlags.LINE_SEPARATOR);
                }
                break;
            case 4:
                if (this.isLinkedMethod()) {
                    stringBuilder.append(
                            "Exclude the suffix \""
                                    + this.getTextBeforeLink(this.linkedMethodName)
                                    + "\" from being renamed in all methods:"
                                    + HiddenOptionFlags.LINE_SEPARATOR
                    );
                } else {
                    stringBuilder.append(string + "all methods:" + HiddenOptionFlags.LINE_SEPARATOR);
                }

                if (this.memberAnnotation != null) {
                    stringBuilder.append(
                            "\t" + bb++ + ") that are annotated by a class matching \"" + this.memberAnnotation + "\"" + HiddenOptionFlags.LINE_SEPARATOR
                    );
                }

                String string6 = this.getMemberAccessText();
                String string18;
                if (string6.length() > 0) {
                    stringBuilder.append("\t" + bb++ + ") that are \"" + string6 + "\"" + HiddenOptionFlags.LINE_SEPARATOR);
                    string18 = this.linkedMethodName;
                } else {
                    string18 = this.linkedMethodName;
                }

                if (string18 != null) {
                    stringBuilder.append(
                            "\t"
                                    + bb++
                                    + ") that have a name matching starting with \""
                                    + this.getTextBeforeLink(this.linkedMethodName)
                                    + "\""
                                    + HiddenOptionFlags.LINE_SEPARATOR
                    );
                } else {
                    stringBuilder.append("\t" + bb++ + ") that have a name matching \"" + this.methodName + "\"" + HiddenOptionFlags.LINE_SEPARATOR);
                }

                String string7 = this.getParameterTypesText();
                if (string7.length() > 0) {
                    if (string7.equals("*")) {
                        stringBuilder.append("\t" + bb++ + ") that have any argument types" + HiddenOptionFlags.LINE_SEPARATOR);
                    } else {
                        stringBuilder.append("\t" + bb++ + ") that have arguments matching \"" + string7 + "\"" + HiddenOptionFlags.LINE_SEPARATOR);
                    }
                } else {
                    stringBuilder.append("\t" + bb++ + ") that have no arguments" + HiddenOptionFlags.LINE_SEPARATOR);
                }

                String string8 = this.getThrowsText();
                if (string8.length() > 0) {
                    stringBuilder.append("\t" + bb++ + ") that throw \"" + string8 + "\"" + HiddenOptionFlags.LINE_SEPARATOR);
                }

                stringBuilder.append("\t" + bb + ") that are contained within a class:" + HiddenOptionFlags.LINE_SEPARATOR);
                if (this.classAnnotation != null) {
                    stringBuilder.append(
                            "\t\t" + bc++ + ") that is annotated by a class matching \"" + this.classAnnotation + "\"" + HiddenOptionFlags.LINE_SEPARATOR
                    );
                }

                String string9 = this.getClassAccessText();
                if (string9.length() > 0) {
                    stringBuilder.append("\t\t" + bc++ + ") that is \"" + string9 + "\"" + HiddenOptionFlags.LINE_SEPARATOR);
                }

                if (this.packageNames != null && this.packageNames.size() > 0) {
                    stringBuilder.append(
                            "\t\t" + bc++ + ") that is in a package that matches \"" + this.getPackagePattern() + "\"" + HiddenOptionFlags.LINE_SEPARATOR
                    );
                } else {
                    stringBuilder.append("\t\t" + bc++ + ") that is not in a package" + HiddenOptionFlags.LINE_SEPARATOR);
                }

                stringBuilder.append("\t\t" + bc++ + ") with a name that matches \"" + this.className + "\"" + HiddenOptionFlags.LINE_SEPARATOR);
                String string10 = this.getSuperclassName();
                if (string10.length() > 0) {
                    stringBuilder.append("\t\t" + bc++ + ") that extends \"" + string10 + "\"" + HiddenOptionFlags.LINE_SEPARATOR);
                }

                String string11 = this.getInterfaceNamesText();
                if (string11.length() > 0) {
                    stringBuilder.append("\t\t" + bc + ") that implements \"" + string11 + "\"" + HiddenOptionFlags.LINE_SEPARATOR);
                }

                if (this.packageNames != null && this.packageNames.size() > 0 && this.excludeContainingPackage) {
                    stringBuilder.append(string1 + "any package containing a class containing a matching method" + HiddenOptionFlags.LINE_SEPARATOR);
                }

                if (this.excludeContainingClass) {
                    stringBuilder.append(string1 + "any class containing a matching method" + HiddenOptionFlags.LINE_SEPARATOR);
                }

                if (this.isLinkedMethod()) {
                    stringBuilder.append(
                            "Also rename the non-prefix part of the method name to match that of any corresponding field name in the class"
                                    + HiddenOptionFlags.LINE_SEPARATOR
                    );
                }

                if (this.includeSignatureClasses) {
                    stringBuilder.append(string1 + "any class that appears in the method arguments" + HiddenOptionFlags.LINE_SEPARATOR);
                }
        }

        if (ba == 3) {
            stringBuilder.append(
                    "Note that the ZKM Script language also allows you to specify the reference obfuscation"
                            + HiddenOptionFlags.LINE_SEPARATOR
                            + "based upon the methods from which the references are made."
                            + HiddenOptionFlags.LINE_SEPARATOR
            );
        }

        return stringBuilder.toString();
    }

    public String getContainingText() {
        return this.containingClause != null ? this.containingClause.toScriptText().substring("containing".length()) : "";
    }

    public void setClassNotInterface(boolean bl) throws ZkmException, IOException {
        if (this.classAccessFlags == null) {
            this.classAccessFlags = new AccessFlagsSpec(1);
        }

        this.classAccessFlags.setInterfaceForbidden(bl);
        this.setChanged();
        this.notifyObservers();
    }

    public void setMemberNative(boolean bl) throws ZkmException, IOException {
        if (this.memberAccessFlags == null) {
            this.memberAccessFlags = new AccessFlagsSpec(3);
        }

        this.memberAccessFlags.setNativeRequired(bl);
        this.setChanged();
        this.notifyObservers();
    }

    public void setMemberNotNative(boolean bl) throws ZkmException, IOException {
        if (this.memberAccessFlags == null) {
            this.memberAccessFlags = new AccessFlagsSpec(3);
        }

        this.memberAccessFlags.setNativeForbidden(bl);
        this.setChanged();
        this.notifyObservers();
    }

    public void clearMemberAccess(Integer integer) throws ZkmException, IOException {
        if (this.memberAccessFlags == null) {
            this.memberAccessFlags = new AccessFlagsSpec(integer);
        }

        this.memberAccessFlags.clearRequiredAccess();
        this.setChanged();
        this.notifyObservers();
    }

    public void setParameterTypes(String string) throws ZkmException, IOException {
        StringTokenizer stringTokenizer = new StringTokenizer(string, ",");
        this.parameterTypes = new ArrayList();

        while (stringTokenizer.hasMoreTokens()) {
            this.parameterTypes.add(stringTokenizer.nextToken().trim());
        }

        this.setChanged();
        this.notifyObservers();
    }

    public String getLinkSuffix() {
        return this.className.substring(this.className.indexOf("<link>") + "<link>".length());
    }

    public String getMemberAccessText() {
        StringBuffer stringBuffer = new StringBuffer();
        if (this.isMemberPackagePrivate()) {
            stringBuffer.append("package ");
        }

        stringBuffer.append(this.memberAccessFlags != null ? this.memberAccessFlags.toSpecString() : "");
        return stringBuffer.toString();
    }

    public void setClassInterface(boolean bl) throws ZkmException, IOException {
        if (this.classAccessFlags == null) {
            this.classAccessFlags = new AccessFlagsSpec(1);
        }

        this.classAccessFlags.setInterfaceRequired(bl);
        this.setChanged();
        this.notifyObservers();
    }

    public boolean isMemberPackagePrivate() {
        int flowControlKey = ZkmScriptSimpleNode.getFlowControlKey();
        RenameFilterModel renameFilterModel1 = this;
        if (flowControlKey != 0) {
            if (this.memberAccessFlags == null) {
                return false;
            }

            renameFilterModel1 = this;
        }

        boolean packageRequired = renameFilterModel1.memberAccessFlags.isPackageRequired();
        return flowControlKey == 0 ? packageRequired : packageRequired;
    }

    public void setClassAbstract(boolean bl) throws ZkmException, IOException {
        if (this.classAccessFlags == null) {
            this.classAccessFlags = new AccessFlagsSpec(1);
        }

        this.classAccessFlags.setAbstractRequired(bl);
        this.setChanged();
        this.notifyObservers();
    }

    public void setMemberVolatile(boolean bl) throws ZkmException, IOException {
        if (this.memberAccessFlags == null) {
            this.memberAccessFlags = new AccessFlagsSpec(2);
        }

        this.memberAccessFlags.setVolatileRequired(bl);
        this.setChanged();
        this.notifyObservers();
    }

    public void setExcludeContainingClass(boolean excludeContainingClass) throws ZkmException, IOException {
        this.excludeContainingClass = excludeContainingClass;
        this.setChanged();
        this.notifyObservers();
    }

    public String getClassName() {
        return this.className;
    }

    public boolean isClassPackagePrivate() {
        int flowControlKey = ZkmScriptSimpleNode.getFlowControlKey();
        RenameFilterModel renameFilterModel1 = this;
        if (flowControlKey != 0) {
            if (this.classAccessFlags == null) {
                return false;
            }

            renameFilterModel1 = this;
        }

        boolean packageRequired = renameFilterModel1.classAccessFlags.isPackageRequired();
        return flowControlKey == 0 ? packageRequired : packageRequired;
    }

    public void setMemberPublic(Integer integer) throws ZkmException, IOException {
        if (this.memberAccessFlags == null) {
            this.memberAccessFlags = new AccessFlagsSpec(integer);
        }

        this.memberAccessFlags.requirePublic();
        this.setChanged();
        this.notifyObservers();
    }

    public AccessFlagsSpec getMemberAccessFlags() {
        return this.memberAccessFlags;
    }

    public String getClassAccessText() {
        StringBuffer stringBuffer = new StringBuffer();
        if (this.isClassPackagePrivate()) {
            stringBuffer.append("package ");
        }

        stringBuffer.append(this.classAccessFlags != null ? this.classAccessFlags.toSpecString() : "");
        return stringBuffer.toString();
    }

    public String getInterfaceNamesText() {
        if (this.interfaceNames != null && this.interfaceNames.size() > 0) {
            StringBuffer stringBuffer = new StringBuffer();
            int ba = 0;
            int bb = 0;

            for (List list1 = this.interfaceNames; bb < list1.size(); list1 = this.interfaceNames) {
                stringBuffer.append((String) this.interfaceNames.get(ba));
                if (ba < this.interfaceNames.size() - 1) {
                    stringBuffer.append(", ");
                }

                bb = ++ba;
            }

            return stringBuffer.toString();
        } else {
            return "";
        }
    }

    public void setSuperclassName(String string) throws ZkmException, IOException {
        this.superclassName = string;
        this.setChanged();
        this.notifyObservers();
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public void setClassPackagePrivate() throws ZkmException, IOException {
        if (this.classAccessFlags == null) {
            this.classAccessFlags = new AccessFlagsSpec(1);
        }

        this.classAccessFlags.requirePackage();
        this.setChanged();
        this.notifyObservers();
    }

    public void setMemberNotStatic(boolean bl, Integer integer) throws ZkmException, IOException {
        if (this.memberAccessFlags == null) {
            this.memberAccessFlags = new AccessFlagsSpec(integer);
        }

        this.memberAccessFlags.setStaticForbidden(bl);
        this.setChanged();
        this.notifyObservers();
    }

    public int getParameterKind() {
        return this.parameterKind;
    }

    public boolean isLinkedMethod() {
        return this.parameterKind == 4 && this.linkedMethodName != null;
    }

    public AccessFlagsSpec getClassAccessFlags() {
        return this.classAccessFlags;
    }

    public boolean hasLinkPrefix() {
        return this.className.indexOf("<link>") > 0;
    }
}
