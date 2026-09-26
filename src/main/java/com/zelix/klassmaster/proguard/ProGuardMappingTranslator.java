package com.zelix.klassmaster.proguard;

import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Map.Entry;

public class ProGuardMappingTranslator {
    public final Map packageMappings = new TreeMap();
    public final Map classMappings = new LinkedHashMap();
    public final Set warnings = new LinkedHashSet();
    public final ListMultimap fieldMappings = new ListMultimap();
    public final ListMultimap methodMappings = new ListMultimap();
    public final String mappingFileName;

    public void addClassMapping(String string, String string1) {
        String string2 = ((java.lang.String) (this.classMappings.put(string, string1)));
        String string6;
        byte be;
        if (string2 != null) {
            if (!string2.equals(string1)) {
                this.addWarning("Old class name '" + string + "' is mapped to more than one new class name. : '" + string2 + "' and '" + string1 + "'");
                long bd = 52413964347291L;
                string6 = string;
                be = 46;
            } else {
                this.addWarning("Class mapping '" + string + " -> " + string1 + "' appears more than once.");
                long bb = 52413964347291L;
                string6 = string;
                be = 46;
            }
        } else {
            long bc = 52413964347291L;
            string6 = string;
            be = 46;
        }

        char ba = (char) be;
        String string5 = string6;
        String string3 = ClassFileBase.substringBeforeLast(string5, ba);
        if (string3.length() > 0) {
            String string4 = ClassFileBase.substringBeforeLast(string1, '.');
            this.addPackageMapping(string3, string4);
        }
    }

    public void addFieldMapping(Object object, String string, String string1, String string2) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(string);
        stringBuilder.append(' ');
        stringBuilder.append(string1);
        ListMultimap listMultimap;
        if (string1.equals(string2)) {
            stringBuilder.append('\t');
            stringBuilder.append("NameNotChanged");
            listMultimap = this.fieldMappings;
        } else {
            stringBuilder.append("\t=>\t");
            stringBuilder.append(string2);
            listMultimap = this.fieldMappings;
        }

        listMultimap.addValue(object, stringBuilder.toString());
    }

    public void addWarning(String string) {
        String string1 = "WARNING: " + string;
        this.warnings.add(string1);
    }

    public void addPackageMapping(String string, String string1) {
        String string2 = ((java.lang.String) (this.packageMappings.put(string, string1)));
        if (string2 != null && !string2.equals(string1)) {
            this.addWarning("Old package name '" + string + "' is mapped to more than one new package name. : '" + string2 + "' and '" + string1 + "'");
        }
    }

    public void appendPackageMapping(String string, String string1, StringBuilder stringBuilder) {
        stringBuilder.append("Package: ");
        stringBuilder.append(string);
        if (string.equals(string1)) {
            stringBuilder.append('\t');
            stringBuilder.append("NameNotChanged");
        } else {
            stringBuilder.append("\t=>\t");
            stringBuilder.append(string1);
        }

        stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
    }

    public void appendMemberMappings(String string, String string1, List list1, StringBuilder stringBuilder) {
        if (list1 != null && list1.size() > 0) {
            stringBuilder.append(string1);
            stringBuilder.append(string);
            stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
            Iterator iterator = list1.iterator();

            while (iterator.hasNext()) {
                String string2 = (String) iterator.next();
                if (string2.indexOf("<init>") == -1 && string2.indexOf("<clinit>") == -1) {
                    stringBuilder.append('\t');
                    stringBuilder.append('\t');
                    stringBuilder.append(string2);
                    stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
                }
            }
        }
    }

    public String buildChangeLog() {
        StringBuilder stringBuilder = new StringBuilder(1000);
        stringBuilder.append("// Translated from '");
        stringBuilder.append(this.mappingFileName);
        stringBuilder.append("' by ");
        stringBuilder.append("Zelix KlassMaster");
        stringBuilder.append(' ');
        stringBuilder.append("version 27.0.0");
        stringBuilder.append(' ');
        stringBuilder.append(ZkmUtils.getTimestamp());
        stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
        stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
        if (this.warnings.size() > 0) {
            ArrayList arrayList = new ArrayList(this.warnings.size() + 2);
            arrayList.add(this.warnings.size() + " warnings reported during " + "ProGuard" + " mapping file translation.");
            arrayList.add("");
            arrayList.addAll(this.warnings);
            String[] strings = new String[arrayList.size()];
            arrayList.toArray(strings);
            stringBuilder.append(ZkmUtils.createCommentBox(false, strings));
            stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
        }

        if (this.packageMappings.size() > 0) {
            HashSet hashSet = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(this.packageMappings.size()));
            Iterator iterator1 = this.packageMappings.entrySet().iterator();

            while (iterator1.hasNext()) {
                Entry entry = (Entry) iterator1.next();
                String string = (String) entry.getKey();
                String string1 = (String) entry.getValue();
                StringBuilder stringBuilder1 = new StringBuilder();
                StringTokenizer stringTokenizer = new StringTokenizer(string, ".");
                int ba = stringTokenizer.countTokens();

                for (int i = 1; i <= ba - 1; i++) {
                    stringBuilder1.append(stringTokenizer.nextToken());
                    String string2 = stringBuilder1.toString();
                    if (!hashSet.contains(string2)) {
                        this.appendPackageMapping(string2, getPackagePrefix(string1, i), stringBuilder);
                        hashSet.add(string2);
                    }

                    if (i <= ba - 2) {
                        stringBuilder1.append(".");
                    }
                }

                this.appendPackageMapping(string, string1, stringBuilder);
                hashSet.add(string);
            }

            stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
        }

        if (this.classMappings.size() > 0) {
            Iterator iterator = this.classMappings.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry1 = (Entry) iterator.next();
                stringBuilder.append("Class: ");
                String string3 = (String) entry1.getKey();
                stringBuilder.append(string3);
                if (string3.equals(entry1.getValue())) {
                    stringBuilder.append('\t');
                    stringBuilder.append("NameNotChanged");
                } else {
                    stringBuilder.append("\t=>\t");
                    stringBuilder.append((String) entry1.getValue());
                }

                stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
                this.appendMemberMappings(string3, "\tFieldsOf: ", this.fieldMappings.getValues(string3), stringBuilder);
                this.appendMemberMappings(string3, "\tMethodsOf: ", this.methodMappings.getValues(string3), stringBuilder);
                stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
            }
        }

        return stringBuilder.toString();
    }

    public static String getPackagePrefix(String string, int ba) {
        StringTokenizer stringTokenizer = new StringTokenizer(string, ".");
        int bb = stringTokenizer.countTokens();
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < ba && i < bb; i++) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(".");
            }

            stringBuilder.append(stringTokenizer.nextToken());
        }

        return stringBuilder.toString();
    }

    public ProGuardMappingTranslator(String string) {
        this.mappingFileName = string;
    }

    public void addMethodMapping(Object object, String string, String string1, String string2, List list1) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(string);
        stringBuilder.append(' ');
        stringBuilder.append(string1);
        stringBuilder.append('(');
        if (list1 != null) {
            int ba = list1.size();

            for (int i = 0; i < ba; i++) {
                stringBuilder.append((String) list1.get(i));
                if (i < ba - 1) {
                    stringBuilder.append(',');
                }
            }
        }

        stringBuilder.append(')');
        ListMultimap listMultimap;
        if (string1.equals(string2)) {
            stringBuilder.append('\t');
            stringBuilder.append("NameNotChanged");
            listMultimap = this.methodMappings;
        } else {
            stringBuilder.append("\t=>\t");
            stringBuilder.append(string2);
            listMultimap = this.methodMappings;
        }

        listMultimap.addValue(object, stringBuilder.toString());
    }
}
