package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.ProGuardClassNameClause;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;
import com.zelix.klassmaster.proguard.ProGuardModifierReceiver;
import com.zelix.klassmaster.proguard.ProGuardNameReceiver;
import com.zelix.klassmaster.proguard.ProGuardWildcardConverter;
import com.zelix.klassmaster.proguard.config.parser.ProGuardConfigSimpleNode;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class ASTProduction6 extends ProGuardConfigSimpleNode implements ProGuardClassNameClause, ProGuardModifierReceiver, ProGuardNameReceiver {
    public static final Map ACCESS_TO_NEGATED_ACCESS = ZkmUtils.createHashMap();
    public static final Set NEGATED_ACCESS_MODIFIERS = ZkmUtils.createHashSet();
    public String memberName;
    public String memberType;
    public List argumentTypes;
    public String annotationName;
    public boolean isField = false;
    public boolean isMethod = false;
    public List modifiers = new ArrayList();

    @Override
    public void setName(String string) {
        this.memberName = string;
    }

    public List normalizeAccessModifiers(List list1) {
        List list2 = list1;
        HashSet hashSet = ZkmUtils.createHashSet();
        HashSet hashSet1 = ZkmUtils.createHashSet();
        HashSet hashSet2 = ZkmUtils.createHashSet();
        Iterator iterator = list1.iterator();

        while (iterator.hasNext()) {
            String string = (String) iterator.next();
            if (ACCESS_TO_NEGATED_ACCESS.containsKey(string)) {
                hashSet.add(string);
            } else if (NEGATED_ACCESS_MODIFIERS.contains(string)) {
                hashSet1.add(string);
            } else {
                hashSet2.add(string);
            }
        }

        if (hashSet.size() > 1) {
            list2 = new ArrayList();
            iterator = ACCESS_TO_NEGATED_ACCESS.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                String string1 = (String) entry.getKey();
                if (!hashSet.contains(string1)) {
                    String string2 = (String) entry.getValue();
                    if (!hashSet1.contains(string2)) {
                        list2.add(string2);
                    }
                }
            }

            iterator = hashSet1.iterator();

            while (iterator.hasNext()) {
                String string3 = (String) iterator.next();
                list2.add(string3);
            }

            iterator = hashSet2.iterator();

            while (iterator.hasNext()) {
                String string4 = (String) iterator.next();
                list2.add(string4);
            }
        }

        return list2;
    }

    public void setArgumentTypes(List list1) {
        this.argumentTypes = list1;
    }

    public void setMemberType(String string) {
        this.memberType = string;
    }

    @Override
    public final void translate(Object object1, Object object) throws ZkmException, IOException {
        ProGuardConfigTranslator proGuardConfigTranslator = (ProGuardConfigTranslator) object;

        for (int i = 0; i < this.jjtGetNumChildren(); i++) {
            this.jjtGetChild(i).translate(this, proGuardConfigTranslator);
        }

        ASTLbraceClause aSTLbraceClause = (ASTLbraceClause) this.jjtGetParent();
        StringBuilder stringBuilder = new StringBuilder();
        if (this.annotationName != null) {
            stringBuilder.append('@');
            stringBuilder.append(ProGuardWildcardConverter.convertClassPattern(this.annotationName));
            stringBuilder.append(' ');
        }

        this.modifiers = this.normalizeAccessModifiers(this.modifiers);
        Iterator iterator = this.modifiers.iterator();

        while (iterator.hasNext()) {
            String string = (String) iterator.next();
            stringBuilder.append(string);
            stringBuilder.append(' ');
        }

        if (this.memberType != null
                && this.memberType.indexOf("*") == -1
                && this.memberType.indexOf("?") == -1
                && this.memberType.indexOf("%") == -1
                && this.memberType.indexOf(60) == -1) {
            stringBuilder.append(this.memberType);
            stringBuilder.append(' ');
        }

        if (this.isField) {
            stringBuilder.append(ProGuardWildcardConverter.convertFieldPattern(this.memberName));
        } else {
            stringBuilder.append(ProGuardWildcardConverter.convertMethodPattern(this.memberName));
        }

        if (this.isField) {
            aSTLbraceClause.addFieldSpec(stringBuilder.toString());
        }

        if (this.isMethod) {
            stringBuilder.append('(');
            int bb = 0;
            int bc = bb;

            for (List list1 = this.argumentTypes; bc < list1.size(); list1 = this.argumentTypes) {
                stringBuilder.append(ProGuardWildcardConverter.convertParameterPattern((String) this.argumentTypes.get(bb)));
                if (bb < this.argumentTypes.size() - 1) {
                    stringBuilder.append(',');
                }

                bc = ++bb;
            }

            stringBuilder.append(')');
            aSTLbraceClause.addMethodSpec(stringBuilder.toString());
        }
    }

    @Override
    public void setAnnotationName(String string) {
        this.annotationName = string;
    }

    public void markAsField() {
        this.isField = true;
    }

    static {
        ACCESS_TO_NEGATED_ACCESS.put("public", "!public");
        ACCESS_TO_NEGATED_ACCESS.put("protected", "!protected");
        ACCESS_TO_NEGATED_ACCESS.put("package", "!package");
        ACCESS_TO_NEGATED_ACCESS.put("private", "!private");
        NEGATED_ACCESS_MODIFIERS.add("!public");
        NEGATED_ACCESS_MODIFIERS.add("!protected");
        NEGATED_ACCESS_MODIFIERS.add("!package");
        NEGATED_ACCESS_MODIFIERS.add("!private");
    }

    @Override
    public void addModifier(Object object) {
        this.modifiers.add(object);
    }

    public ASTProduction6() {
        super(75);
    }

    public void markAsMethod() {
        this.isMethod = true;
    }
}
