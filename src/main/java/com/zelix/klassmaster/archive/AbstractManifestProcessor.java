package com.zelix.klassmaster.archive;

import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.log.MessageReporter;
import com.zelix.klassmaster.util.ArrayListMultiMap;
import com.zelix.klassmaster.util.BooleanFlag;
import com.zelix.klassmaster.util.EnumerableMap;
import com.zelix.klassmaster.util.ZkmUtils;
import com.zelix.klassmaster.xml.ResourcePathTranslator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public abstract class AbstractManifestProcessor {
    public static String sectionTerminator = "&";
    public ArrayListMultiMap headers = new ArrayListMultiMap();
    public boolean endOfStream;

    public static String translateClassListValue(
            String string,
            String string1,
            EnumerableMap enumerableMap,
            EnumerableMap enumerableMap1,
            BooleanFlag booleanFlag,
            String string2,
            boolean bl,
            MessageReporter messageReporter1
    ) {
        String string3 = string + ":" + " ";
        booleanFlag.setValue(false);
        String[] strings = string1.split("\\s*[,;:\"'=\\s]\\s*");
        BooleanFlag booleanFlag1 = new BooleanFlag();
        HashMap hashMap = ZkmUtils.createHashMap();
        ArrayList arrayList = new ArrayList();

        for (String string4 : strings) {
            String string5 = string4.trim();
            int ba = string5.indexOf(46);
            if (string5.length() >= 3 && ba > 0 && ba < string5.length() - 1 && string5.indexOf(47) == -1 && isDottedIdentifier(string5, ".")) {
                String string6 = translateClassOrPathName(string5, enumerableMap1, booleanFlag1);
                if (booleanFlag1.getValue()) {
                    hashMap.put(string5, string6);
                    arrayList.add(string5);
                } else {
                    String string7 = translateDottedClassName(string5, enumerableMap, booleanFlag1);
                    if (booleanFlag1.getValue()) {
                        hashMap.put(string5, string7);
                        arrayList.add(string5);
                    }
                }
            }
        }

        ArrayList arrayList1 = new ArrayList();
        int bd = 0;
        Iterator iterator = arrayList.iterator();

        while (iterator.hasNext()) {
            String string11 = (String) iterator.next();
            int be = string1.indexOf(string11, bd);
            String string12 = string1.substring(bd, be);
            if (string12.length() > 0) {
                arrayList1.add(string12);
            }

            arrayList1.add(string11);
            bd = be + string11.length();
        }

        if (bd < string1.length()) {
            String string10 = string1.substring(bd, string1.length());
            arrayList1.add(string10);
        }

        HashSet hashSet = ZkmUtils.createHashSet();
        HashSet hashSet1 = ZkmUtils.createHashSet();
        StringBuilder stringBuilder = new StringBuilder();
        boolean bl6 = false;
        String string13 = null;
        boolean bl7 = false;
        boolean bl1 = false;
        boolean bl2 = false;
        int bb = arrayList1.size();

        for (int i = 0; i < bb; i++) {
            String string8 = (String) arrayList1.get(i);
            if (hashMap.containsKey(string8)) {
                String string9 = (String) ZkmUtils.mapOrSelf(string8, hashMap);
                boolean bl3 = hashSet.add(string8);
                boolean bl4 = !hashSet1.add(string9) && bl3 && !HiddenOptionFlags.ALLOW_DUPLICATE_MANIFEST_ENTRIES;
                bl1 = bl1 || bl4;
                boolean bl5 = string9.length() == 0;
                bl7 = bl7 || bl5;
                if (!bl5 && !bl4) {
                    if (string13 != null && (string13.indexOf(59) > -1 || bl6 || stringBuilder.length() == 0)) {
                        stringBuilder.append(string13);
                        string13 = null;
                    }

                    stringBuilder.append(string9);
                    bl6 = true;
                }
            } else if (i < bb - 1) {
                bl2 = true;
                string13 = string8;
            } else {
                if (string13 != null && (string13.indexOf(59) > -1 || bl6 || stringBuilder.length() == 0)) {
                    if (string13.trim().endsWith(",") && string8.trim().startsWith(",")) {
                        int bf = string13.lastIndexOf(44);
                        string13 = string13.substring(0, bf) + (bf < string13.length() - 2 ? string13.substring(bf + 1) : "");
                    }

                    stringBuilder.append(string13);
                    string13 = null;
                    bl6 = false;
                }

                stringBuilder.append(string8);
            }
        }

        String string14 = stringBuilder.toString();
        booleanFlag.setValue(string14.length() == 0 || string14.length() == 1 && string14.equals(";"));
        if (bl && bl2 && (bl7 || bl1)) {
            messageReporter1.reportWarning(
                    "WARNING:",
                    "Package names were collapsed which may have caused invalid translation of the '"
                            + string
                            + "' attribute line in manifest file in '"
                            + TempFileManager.replaceTempPaths(string2)
                            + "' : '"
                            + string1
                            + (string14.length() > 0 ? "' to '" + string14 + "'" : "'. Attribute now empty.")
            );
        }

        return string3 + string14;
    }

    public final String translateClassListAttribute(
            String string,
            EnumerableMap enumerableMap,
            EnumerableMap enumerableMap1,
            BooleanFlag booleanFlag,
            String string1,
            boolean bl,
            MessageReporter messageReporter1
    ) {
        String string2 = this.getUnwrappedHeaderValue(string);
        return this.wrapManifestLine(translateClassListValue(string, string2, enumerableMap, enumerableMap1, booleanFlag, string1, bl, messageReporter1));
    }

    public String parseHeaderLine(String string) throws ZkmException {
        int ba = string.indexOf(":");
        if (ba > 0 && ba != string.length() - 1) {
            String string1 = string.substring(0, ba).trim();
            string1 = this.normalizeHeaderName(string1);
            if (!this.headers.containsKey(string1)) {
                String string2 = string.substring(ba + 1);
                ArrayListMultiMap arrayListMultiMap;
                if (string2.length() > 0) {
                    if (string2.charAt(0) == ' ') {
                        string2 = string2.substring(1);
                        arrayListMultiMap = this.headers;
                    } else {
                        arrayListMultiMap = this.headers;
                    }
                } else {
                    arrayListMultiMap = this.headers;
                }

                arrayListMultiMap.addValue(string1, string2);
                return string1;
            } else if (string1.equals("Name")) {
                throw new ZkmException("Two 'Name' headers in the one manifest section.  Header '" + string + "' must be preceded by a blank line.");
            } else {
                throw new ZkmException("Two '" + string1 + "' headers in the one manifest section: '" + string + "'");
            }
        } else {
            throw new ZkmException("Invalid Manifest attribute '" + string + "' - " + ZkmUtils.toHexArrayLiteral(string.getBytes()));
        }
    }

    public static String translateDottedClassName(String string, EnumerableMap enumerableMap, BooleanFlag booleanFlag) {
        return lookupRenamedName(string, enumerableMap, true, booleanFlag);
    }

    public static String translateClassOrPathName(String string, EnumerableMap enumerableMap, BooleanFlag booleanFlag) {
        boolean bl;
        if (string.indexOf(47) > 0) {
            if (string.indexOf(46) != -1) {
                booleanFlag.setValue(false);
                return string;
            }

            bl = true;
        } else {
            bl = false;
        }

        return lookupRenamedName(string, enumerableMap, !bl, booleanFlag);
    }

    public static boolean isDottedIdentifier(String string, String string1) {
        StringTokenizer stringTokenizer = new StringTokenizer(string, string1);

        while (stringTokenizer.hasMoreTokens()) {
            if (!Character.isJavaIdentifierStart(stringTokenizer.nextToken().charAt(0))) {
                return false;
            }
        }

        return true;
    }

    public final boolean isEndOfStream() {
        return this.endOfStream;
    }

    public static String translateResourcePath(String string, EnumerableMap enumerableMap, ResourcePathTranslator resourcePathTranslator1) throws IOException {
        if (string.endsWith(".class")) {
            return resourcePathTranslator1.renameClassFilePath(string, enumerableMap);
        } else {
            return resourcePathTranslator1.hasDirectoryMapping(string)
                    ? resourcePathTranslator1.renameDirectory(string)
                    : resourcePathTranslator1.translateResourcePath(string);
        }
    }

    public final String getUnwrappedHeaderValue(String string) {
        List list1 = this.headers.getValues(string);
        if (list1 != null) {
            StringBuilder stringBuilder = new StringBuilder();

            for (int i = 0; i < list1.size(); i++) {
                String string1 = (String) list1.get(i);
                if (i > 0) {
                    string1 = string1.substring(1);
                }

                stringBuilder.append(string1);
            }

            return stringBuilder.toString();
        } else {
            return null;
        }
    }

    public static String lookupRenamedName(String string, EnumerableMap enumerableMap, boolean bl, BooleanFlag booleanFlag) {
        String string1 = string.trim().replace('.', '/');
        String string2 = (String) enumerableMap.get(string1);
        if (string2 == null) {
            string2 = string1;
            booleanFlag.setValue(false);
        } else {
            booleanFlag.setValue(true);
        }

        return bl ? string2.replace('/', '.') : string2;
    }

    public final boolean hasNoHeaders() {
        return this.headers.getKeyCount() == 0;
    }

    public final String getRawHeaderValue(String string) {
        List list1 = this.headers.getValues(string);
        if (list1 != null) {
            StringBuilder stringBuilder = new StringBuilder();

            for (int i = 0; i < list1.size(); i++) {
                String string1 = (String) list1.get(i);
                stringBuilder.append(string1);
                if (i < list1.size() - 1) {
                    stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
                }
            }

            return stringBuilder.toString();
        } else {
            return null;
        }
    }

    public abstract void writeSection(
            PrintWriter printWriter,
            EnumerableMap enumerableMap,
            EnumerableMap enumerableMap1,
            Object object,
            String string,
            boolean bl,
            MessageReporter messageReporter1
    ) throws IOException;

    public AbstractManifestProcessor(BufferedReader bufferedReader) throws ZkmException, IOException {
        boolean bl = false;
        String string = null;

        String string1;
        while ((string1 = bufferedReader.readLine()) != null) {
            if (string1.length() == 0) {
                if (bl) {
                    return;
                }
            } else if (string1.equals(sectionTerminator)) {
                if (bl) {
                    return;
                }
            } else {
                bl = true;
                if (string1.charAt(0) == ' ') {
                    this.headers.addValue(string, string1);
                } else {
                    string = this.parseHeaderLine(string1);
                }
            }
        }

        this.endOfStream = true;
    }

    public abstract String normalizeHeaderName(String string);

    public static String translateName(String string, EnumerableMap enumerableMap) {
        return translateClassOrPathName(string, enumerableMap, new BooleanFlag());
    }

    public final String wrapManifestLine(String string) {
        int ba = string.length();
        if (ba <= 72) {
            return string;
        }

        int bb = 0;
        int bc = 72;
        StringBuilder stringBuilder = new StringBuilder(string.substring(bb, 72));

        while (ba - bc > 71) {
            bb = bc;
            bc = bb + 72 - 1;
            stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
            stringBuilder.append(' ');
            stringBuilder.append(string.substring(bb, bc));
        }

        if (bc < ba) {
            stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
            stringBuilder.append(' ');
            stringBuilder.append(string.substring(bc));
        }

        return stringBuilder.toString();
    }
}
