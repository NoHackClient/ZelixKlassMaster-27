package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.proguard.NamePatternEntry;
import com.zelix.klassmaster.proguard.ProGuardClassNameEntry;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public abstract class ProGuardClassSpecification extends ProGuardOptionNode {
    public String inheritanceText;
    public String modifiersText;
    public String annotationText;
    public ASTIfOption ifOption;
    public List classNameEntries;
    public Set memberSpecs = new LinkedHashSet();
    public List classNamePatterns = new ArrayList();

    public List buildClassNameEntries(ProGuardConfigTranslator proGuardConfigTranslator) {
        ArrayList arrayList = new ArrayList(this.classNamePatterns.size());
        Iterator iterator = this.classNamePatterns.iterator();

        while (iterator.hasNext()) {
            NamePatternEntry namePatternEntry = (NamePatternEntry) iterator.next();
            String string = this.translateClassSpec(namePatternEntry.getPattern(), proGuardConfigTranslator);
            arrayList.add(new ProGuardClassNameEntry(string, namePatternEntry.isNegated(), namePatternEntry.isInList()));
        }

        return arrayList;
    }

    public final void addClassNamePattern(Object object) {
        this.classNamePatterns.add(object);
    }

    public static String qualifyClassPattern(String string) {
        return string.indexOf(46) > -1 ? string : "*." + string;
    }

    public void setIfOption(ASTIfOption aSTIfOption) {
        this.ifOption = aSTIfOption;
    }

    public abstract boolean appliesToClass();

    public static String toClassNameOnlyPattern(String string) {
        int ba = string.length();
        int bb = string.lastIndexOf(46);
        if (bb > 0 && bb < ba - 1 && string.charAt(bb + 1) == ')') {
            bb++;
        }

        if (bb > 0 && bb < ba - 1) {
            return string.substring(0, bb + 1) + "^" + string.substring(bb + 1) + "^";
        } else {
            return string.charAt(0) == '.' && string.charAt(ba - 1) == '.' ? string : "*.^" + string + "^";
        }
    }

    public String translateClassSpec(String string, ProGuardConfigTranslator proGuardConfigTranslator) {
        String string1 = string;
        boolean bl = false;
        ASTLbraceClause aSTLbraceClause = null;
        if (string1.indexOf("<") > -1) {
            if (string1.equals("<1>")) {
                if (this.ifOption != null) {
                    aSTLbraceClause = this.ifOption.getClassSpecClause();
                    List list1 = aSTLbraceClause.getClassNamePatterns();
                    if (list1.size() == 1) {
                        String string2 = ((NamePatternEntry) list1.get(0)).getPattern();
                        if (string2.equals("*.*")) {
                            string1 = "*.*";
                        } else if (string2.startsWith("*")) {
                            String string3 = string2.substring(1);
                            if (string3.length() > 0) {
                                string1 = "<link>" + string3;
                                bl = true;
                            }
                        } else {
                            proGuardConfigTranslator.logWarning(
                                    "Does not support more complicated <n> syntax in '"
                                            + this.getOptionName()
                                            + "' at line "
                                            + this.getLineNumber()
                                            + ". : '"
                                            + string1
                                            + "' : '"
                                            + string2
                                            + "'. (A)"
                            );
                        }
                    } else {
                        proGuardConfigTranslator.logWarning(
                                "Does not support more complicated <n> syntax in '"
                                        + this.getOptionName()
                                        + "' at line "
                                        + this.getLineNumber()
                                        + ". : '"
                                        + string1
                                        + "' : "
                                        + this.ifOption.classNameEntries.size()
                                        + " (B)"
                        );
                    }
                } else {
                    proGuardConfigTranslator.logWarning(
                            "<n> syntax in '" + this.getOptionName() + "' at line " + this.getLineNumber() + " with no preceding '-if' command. : '" + string1 + "'. (B)"
                    );
                }
            } else {
                proGuardConfigTranslator.logWarning(
                        "Does not support more complicated <n> syntax in '" + this.getOptionName() + "' at line " + this.getLineNumber() + ". : '" + string1 + "'. (C)"
                );
            }
        }

        StringBuilder stringBuilder = new StringBuilder();
        if (aSTLbraceClause != null && aSTLbraceClause.hasAnnotation()) {
            String string5 = aSTLbraceClause.getAnnotationText(6471, 31638, -27990);
            if (bl) {
                proGuardConfigTranslator.logWarning(
                        "Does not support more complicated <n> syntax in '"
                                + this.getOptionName()
                                + "' at line "
                                + this.getLineNumber()
                                + ". : '"
                                + string5
                                + "' : '"
                                + string1
                                + "'. (D)"
                );
            }

            stringBuilder.append(string5);
        }

        if (this.annotationText != null) {
            StringBuilder stringBuilder2;
            String string9;
            if (bl) {
                proGuardConfigTranslator.logWarning(
                        "Does not support more complicated <n> syntax in '"
                                + this.getOptionName()
                                + "' at line "
                                + this.getLineNumber()
                                + ". : '"
                                + this.annotationText
                                + "' : '"
                                + string1
                                + "'. (E)"
                );
                stringBuilder2 = stringBuilder;
                string9 = this.annotationText;
            } else {
                stringBuilder2 = stringBuilder;
                string9 = this.annotationText;
            }

            stringBuilder2.append(string9);
        }

        if (aSTLbraceClause != null && aSTLbraceClause.hasModifiers()) {
            String string6 = aSTLbraceClause.getModifiersText(103429512437761L);
            if (bl) {
                proGuardConfigTranslator.logWarning(
                        "Does not support more complicated <n> syntax in '"
                                + this.getOptionName()
                                + "' at line "
                                + this.getLineNumber()
                                + ". : '"
                                + string1
                                + "' : '"
                                + string6
                                + "'. (F)"
                );
            } else {
                stringBuilder.append(string6);
            }
        }

        if (this.modifiersText != null) {
            if (bl) {
                proGuardConfigTranslator.logWarning(
                        "Does not support more complicated <n> syntax in '"
                                + this.getOptionName()
                                + "' at line "
                                + this.getLineNumber()
                                + ". : '"
                                + string1
                                + "' : '"
                                + this.modifiersText
                                + "'. (G)"
                );
            } else {
                stringBuilder.append(this.modifiersText);
            }
        }

        if (this.appliesToClass()) {
            stringBuilder.append(toClassNameOnlyPattern(string1));
            if (this.memberSpecs.size() == 0 && (aSTLbraceClause == null || !aSTLbraceClause.hasFieldSpecs() && !aSTLbraceClause.hasMethodSpecs())) {
                stringBuilder.setLength(stringBuilder.length() - 1);
            }
        } else {
            stringBuilder.append(qualifyClassPattern(string1));
        }

        int ba = aSTLbraceClause != null ? aSTLbraceClause.getFieldSpecs().size() + aSTLbraceClause.getMethodSpecs().size() : 0;
        int bb = this.memberSpecs.size();
        if (this.requiresMatchingMembers() && bb > 0 || ba > 0) {
            if (bl) {
                proGuardConfigTranslator.logWarning(
                        "Does not support more complicated <n> syntax in '" + this.getOptionName() + "' at line " + this.getLineNumber() + ". : '" + string1 + "'. (H)"
                );
            } else {
                stringBuilder.append(" containing {");
                if (bb > 1) {
                    stringBuilder.append('(');
                }

                stringBuilder.append(this.buildMemberSpecsText());
                if (bb > 1) {
                    stringBuilder.append(')');
                }

                stringBuilder.append("}");
            }
        }

        boolean bl1 = false;
        boolean bl2 = false;
        String string7;
        if (aSTLbraceClause != null) {
            if (aSTLbraceClause.hasInheritanceClause()) {
                String string4 = aSTLbraceClause.getInheritanceText(4252833208356L);
                stringBuilder.append(string4);
                if (string4.indexOf("extends ") > -1) {
                    bl1 = true;
                }

                if (string4.indexOf("implements ") > -1) {
                    bl2 = true;
                    string7 = this.inheritanceText;
                } else {
                    string7 = this.inheritanceText;
                }
            } else {
                string7 = this.inheritanceText;
            }
        } else {
            string7 = this.inheritanceText;
        }

        if (string7 != null) {
            if (bl1 && this.inheritanceText.indexOf("extends ") > -1) {
                proGuardConfigTranslator.logWarning(
                        "Does not support more complicated <n> syntax in '"
                                + this.getOptionName()
                                + "' at line "
                                + this.getLineNumber()
                                + ". Two 'extends' clauses : '"
                                + this.inheritanceText
                                + "' : '"
                                + string1
                                + "'. (I)"
                );
            } else {
                StringBuilder stringBuilder1;
                String string8;
                if (bl2) {
                    if (this.inheritanceText.indexOf("implements ") > -1) {
                        proGuardConfigTranslator.logWarning(
                                "Does not support more complicated <n> syntax in '"
                                        + this.getOptionName()
                                        + "' at line "
                                        + this.getLineNumber()
                                        + ". Two 'implements' clauses : '"
                                        + this.inheritanceText
                                        + "' : '"
                                        + string1
                                        + "'. (J)"
                        );
                        return stringBuilder.toString();
                    }

                    stringBuilder1 = stringBuilder;
                    string8 = this.inheritanceText;
                } else {
                    stringBuilder1 = stringBuilder;
                    string8 = this.inheritanceText;
                }

                stringBuilder1.append(string8);
            }
        }

        return stringBuilder.toString();
    }

    public void setInheritanceText(String string) {
        this.inheritanceText = string;
    }

    public void addMemberSpec(Object object) {
        this.memberSpecs.add(object);
    }

    public ProGuardClassSpecification(int ba) {
        super(ba);
    }

    public String buildMemberSpecsText() {
        StringBuilder stringBuilder = new StringBuilder();
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        LinkedHashSet linkedHashSet1;
        Set set1;
        if (this.ifOption != null) {
            ASTLbraceClause aSTLbraceClause = this.ifOption.getClassSpecClause();
            if (aSTLbraceClause.hasFieldSpecs()) {
                linkedHashSet.addAll(aSTLbraceClause.getFieldSpecs());
            }

            if (aSTLbraceClause.hasMethodSpecs()) {
                linkedHashSet.addAll(aSTLbraceClause.getMethodSpecs());
                linkedHashSet1 = linkedHashSet;
                set1 = this.memberSpecs;
            } else {
                linkedHashSet1 = linkedHashSet;
                set1 = this.memberSpecs;
            }
        } else {
            linkedHashSet1 = linkedHashSet;
            set1 = this.memberSpecs;
        }

        linkedHashSet1.addAll(set1);
        int bb = linkedHashSet.size();
        int ba = 0;

        for (Iterator iterator = linkedHashSet.iterator(); iterator.hasNext(); ba++) {
            String string = (String) iterator.next();
            stringBuilder.append(string);
            if (ba < bb - 1) {
                stringBuilder.append(" && ");
            }
        }

        return stringBuilder.toString();
    }

    public void setModifiersText(String string) {
        this.modifiersText = string;
    }

    public abstract boolean requiresMatchingMembers();

    public void setAnnotationText(String string) {
        this.annotationText = string;
    }
}
