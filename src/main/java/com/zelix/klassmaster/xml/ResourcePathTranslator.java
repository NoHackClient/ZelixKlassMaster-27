package com.zelix.klassmaster.xml;

import com.zelix.klassmaster.util.CaseInsensitiveComparator;
import com.zelix.klassmaster.util.EnumerableMap;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.ZkmStringUtils;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class ResourcePathTranslator {
    private static String markerString;
    public Map translatedDirCache = ZkmUtils.createHashMap();
    public EnumerableMap packageRenameMap;
    public Set classRootPrefixes;
    public String[] sortedClassRootPrefixes;

    public static String getMarkerString() {
        return markerString;
    }

    public ResourcePathTranslator(EnumerableMap enumerableMap, Set set1) {
        this.packageRenameMap = enumerableMap;
        this.classRootPrefixes = ZkmUtils.createHashSetFrom(set1);
        this.sortedClassRootPrefixes = new String[set1.size()];
        int ba = 0;
        Iterator iterator = set1.iterator();

        while (iterator.hasNext()) {
            String string = (String) iterator.next();
            this.sortedClassRootPrefixes[ba++] = string;
        }

        Arrays.sort(this.sortedClassRootPrefixes, CaseInsensitiveComparator.getInstance());
    }

    public static String renamePackagePath(String string, EnumerableMap enumerableMap) {
        return renamePackagePath(string, enumerableMap, new MutableInt());
    }

    public static String renamePackagePath(String string, EnumerableMap enumerableMap, MutableInt mutableInt) {
        String string1 = string;
        mutableInt.setValue(0);
        if (enumerableMap == null) {
            return string1;
        }

        string1 = string1.trim();
        StringBuilder stringBuilder = new StringBuilder(string1);
        StringBuilder stringBuilder1 = new StringBuilder();

        while (true) {
            String string2 = stringBuilder.toString();
            String string3 = (String) enumerableMap.get(string2);
            if (string3 != null) {
                if (!string3.equals(string2)) {
                    stringBuilder1.setLength(0);
                    stringBuilder1.append(string3);
                    String string4 = string1.substring(string2.length());
                    if (stringBuilder1.length() == 0 && string4.length() > 0 && string4.charAt(0) == '/') {
                        string4 = string4.substring(1);
                    }

                    stringBuilder1.append(string4);
                    mutableInt.setValue(ZkmStringUtils.countChar(string2, '/') + 1);
                    return stringBuilder1.toString();
                }
                break;
            }

            int ba = string2.lastIndexOf("/");
            if (ba <= -1) {
                break;
            }

            stringBuilder.setLength(ba);
        }

        return string1;
    }

    public String renameDirectory(String string) {
        String string1 = string;
        String string2 = "";
        String string3 = "";
        if (string1.charAt(0) == '/') {
            string1 = string1.substring(1);
            string2 = "/";
        }

        EnumerableMap enumerableMap;
        if (string1.charAt(string1.length() - 1) == '/') {
            string1 = string1.substring(0, string1.length() - 1);
            string3 = "/";
            enumerableMap = this.packageRenameMap;
        } else {
            enumerableMap = this.packageRenameMap;
        }

        String string4 = (String) enumerableMap.get(string1);
        return string4 != null ? string2 + string4 + (string4.length() > 0 ? string3 : "") : string;
    }

    public static String renameResourceParentPath(String string, EnumerableMap enumerableMap) {
        String string1 = string;
        if (enumerableMap == null) {
            return string1;
        }

        string1 = string1.trim();
        int ba = string1.lastIndexOf(47);
        if (ba <= 0) {
            return string1;
        }

        String string2 = string1.substring(ba);
        String string3 = string1.substring(0, ba);
        String string4;
        if (string3.length() > 0 && string3.charAt(0) == '/') {
            string4 = "/";
            string3 = string3.substring(1);
        } else {
            string4 = "";
        }

        StringBuilder stringBuilder = new StringBuilder(string3);
        StringBuilder stringBuilder1 = new StringBuilder();

        while (true) {
            String string5 = stringBuilder.toString();
            String string6 = (String) enumerableMap.get(string5);
            if (string6 != null) {
                if (!string6.equals(string5)) {
                    stringBuilder1.setLength(0);
                    stringBuilder1.append(string6);
                    String string7 = string3.substring(string5.length());
                    if (stringBuilder1.length() == 0 && string7.length() > 0 && string7.charAt(0) == '/') {
                        string7 = string7.substring(1);
                    }

                    stringBuilder1.append(string7);
                    if (stringBuilder1.length() == 0) {
                        string2 = string2.substring(1);
                    }

                    return string4 + stringBuilder1.toString() + string2;
                }
                break;
            }

            int bb = string5.lastIndexOf("/");
            if (bb <= -1) {
                break;
            }

            stringBuilder.setLength(bb);
        }

        return string1;
    }

    public static void setMarkerString() {
        markerString = "gb0eob";
    }

    public static String translateRelativePath(String string, String string1, boolean bl, EnumerableMap enumerableMap) {
        String string2 = string1;
        String string3 = string;
        boolean bl1 = false;
        if (string2.length() == 0) {
            return string2;
        }

        if (bl && string2.charAt(0) == '/') {
            bl1 = true;
            if (string2.length() == 1) {
                return string2;
            }

            string2 = string2.substring(1);
        }

        if (string3.indexOf(92) > -1) {
            string3 = string3.replace('\\', '/');
        }

        StringTokenizer stringTokenizer = new StringTokenizer(string3, "/");
        ArrayList arrayList = new ArrayList(stringTokenizer.countTokens());

        while (stringTokenizer.hasMoreTokens()) {
            arrayList.add(stringTokenizer.nextToken());
        }

        String string4 = "";
        StringBuilder stringBuilder = new StringBuilder();
        int ba = arrayList.size();

        for (int i = ba - 1; i >= 0; i += -1) {
            if (stringBuilder.length() > 0) {
                stringBuilder.insert(0, '/');
            }

            String string5 = (String) arrayList.get(i);
            String string10 = string5;
            stringBuilder.insert(0, string10);
            if (enumerableMap.containsKey(stringBuilder.toString())) {
                string4 = stringBuilder.toString();
            } else if (string4.length() > 0) {
                break;
            }
        }

        String string11 = null;
        String string12 = null;
        StringBuilder stringBuilder1 = new StringBuilder();
        StringTokenizer stringTokenizer1 = new StringTokenizer(string2, "/");
        ba = stringTokenizer1.countTokens();

        for (int i = 0; i < ba; i++) {
            stringBuilder.setLength(0);
            stringBuilder.append(string4);
            if (stringBuilder.length() > 0) {
                stringBuilder.append('/');
            }

            if (stringBuilder1.length() > 0) {
                stringBuilder1.append('/');
            }

            String string6 = stringTokenizer1.nextToken();
            stringBuilder1.append(string6);
            stringBuilder.append(stringBuilder1.toString());
            if (!enumerableMap.containsKey(stringBuilder.toString())) {
                break;
            }

            string11 = stringBuilder.toString();
            string12 = stringBuilder1.toString();
        }

        if (string11 != null) {
            String string13 = (String) enumerableMap.get(string11);
            if (string4.length() > 0) {
                String string15 = (String) enumerableMap.get(string4);
                if (string11.length() > string4.length()) {
                    String string16 = "";
                    if (string13.length() != string15.length()) {
                        if (string13.length() > string15.length()) {
                            string16 = string13.substring(string15.length() + (string13.indexOf(47) == -1 ? 0 : 1));
                        } else {
                            string16 = "..";
                        }
                    }

                    if (string12 != null && string12.length() < string2.length()) {
                        String string8 = string2.substring(string12.length() + (string16.length() > 0 ? 0 : 1));
                        String string9 = string16 + string8;
                        return bl1 ? '/' + string9 : string9;
                    } else {
                        return bl1 ? '/' + string16 : string16;
                    }
                } else {
                    return bl1 ? '/' + string2 : string2;
                }
            } else if (string12 != null && string12.length() < string2.length()) {
                String string14 = string2.substring(string12.length() + (string13.length() > 0 ? 0 : 1));
                String string7 = string13 + string14;
                return bl1 ? '/' + string7 : string7;
            } else {
                return bl1 ? '/' + string13 : string13;
            }
        } else {
            return bl1 ? '/' + string2 : string2;
        }
    }

    public boolean hasDirectoryMapping(String string) {
        String string1 = string;
        if (string1.charAt(0) == '/') {
            string1 = string1.substring(1);
        }

        EnumerableMap enumerableMap;
        if (string1.charAt(string1.length() - 1) == '/') {
            string1 = string1.substring(0, string1.length() - 1);
            enumerableMap = this.packageRenameMap;
        } else {
            enumerableMap = this.packageRenameMap;
        }

        return enumerableMap.containsKey(string1);
    }

    public String translateResourcePath(String string) throws IOException {
        if (string.indexOf("//") == -1
                && string.indexOf("\\") == -1
                && string.indexOf(":") == -1
                && string.indexOf("!") == -1
                && string.indexOf(" ") == -1
                && string.indexOf("'") == -1
                && string.indexOf("[") == -1
                && string.indexOf("(") == -1) {
            String string1 = string;
            String string2 = "";
            String string10;
            byte bd;
            if (string1.charAt(0) == '/') {
                string1 = string1.substring(1);
                string2 = "/";
                string10 = string1;
                bd = 47;
            } else {
                string10 = string1;
                bd = 47;
            }

            int ba = string10.lastIndexOf(bd);
            if (ba >= 0) {
                String string3 = string1.substring(ba + 1);
                String string4 = string1.substring(0, ba);
                String string5 = (String) this.translatedDirCache.get(string4);
                if (string5 != null) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(string2);
                    stringBuilder.append(string5);
                    if (string5.length() > 0
                            && string5.charAt(string5.length() - 1) != '/'
                            && (string3.length() > 0 && string3.charAt(0) != '/' || string3.length() == 0 && string.endsWith("/"))) {
                        stringBuilder.append("/");
                    }

                    stringBuilder.append(string3);
                    return stringBuilder.toString();
                }

                String string6 = "";

                for (int i = this.sortedClassRootPrefixes.length - 1; i >= 0; i += -1) {
                    if (string4.startsWith(this.sortedClassRootPrefixes[i])) {
                        string6 = this.sortedClassRootPrefixes[i];
                        string4 = string4.substring(this.sortedClassRootPrefixes[i].length());
                        break;
                    }
                }

                StringBuilder stringBuilder1 = new StringBuilder(string4);

                while (true) {
                    String string7 = stringBuilder1.toString();
                    String string8 = (String) this.packageRenameMap.get(string7);
                    if (string8 != null) {
                        StringBuilder stringBuilder2 = new StringBuilder(string8);
                        String string9 = string4.substring(string7.length());
                        if (stringBuilder2.length() == 0 && string9.length() > 0 && string9.charAt(0) == '/') {
                            string9 = string9.substring(1);
                        }

                        stringBuilder2.append(string9);
                        this.translatedDirCache.put(string6 + string4, string6 + stringBuilder2.toString());
                        if (stringBuilder2.length() > 0) {
                            stringBuilder2.append("/");
                        }

                        stringBuilder2.append(string3);
                        return string2 + string6 + stringBuilder2.toString();
                    }

                    int bc = string7.lastIndexOf("/");
                    if (bc <= -1) {
                        this.translatedDirCache.put(string6 + string4, string6 + string4);
                        break;
                    }

                    stringBuilder1.setLength(bc);
                }
            }

            return string;
        } else {
            return string;
        }
    }

    public String translateRelativePath(String string, String string1, boolean bl) {
        return translateRelativePath(string, string1, bl, this.packageRenameMap);
    }

    public String renamePackagePath(String string) {
        return renamePackagePath(string, this.packageRenameMap);
    }

    public String renameClassFilePath(String string, EnumerableMap enumerableMap) {
        if (enumerableMap != null) {
            int ba = string.indexOf(".class");
            String string1 = string.substring(0, ba);
            String string2 = "";
            Object object;
            if (string1.charAt(0) == '/') {
                string1 = string1.substring(1);
                string2 = "/";
                object = enumerableMap.get(string1);
            } else {
                object = enumerableMap.get(string1);
            }

            String string3 = (String) object;
            if (string3 != null) {
                return string2 + string3 + ".class";
            }

            String string4 = "";

            for (int i = this.sortedClassRootPrefixes.length - 1; i >= 0; i += -1) {
                if (string1.startsWith(this.sortedClassRootPrefixes[i])) {
                    string4 = this.sortedClassRootPrefixes[i];
                    break;
                }
            }

            if (string4.length() > 0) {
                return string2 + string4 + (String) ZkmUtils.mapOrSelf(string1.substring(string4.length()), enumerableMap) + ".class";
            }
        }

        return string;
    }

    public static String translateRelativeReference(String string, String string1, EnumerableMap enumerableMap) {
        String string2 = string;
        String string3 = string1;
        string2 = string2.trim();
        string3 = string3.trim();
        if (string2.length() > 0 && string2.charAt(0) == '/') {
            string2 = string2.substring(1);
        }

        int ba = string2.lastIndexOf(47);
        if (ba <= 0) {
            return string3;
        }

        String string4 = null;
        StringBuilder stringBuilder = new StringBuilder(string2.substring(0, ba));

        String string5;
        while (true) {
            String string6 = stringBuilder.toString();
            string5 = (String) enumerableMap.get(string6);
            if (string5 != null) {
                string4 = string6;
                break;
            }

            int bb = string6.lastIndexOf("/");
            if (bb <= -1) {
                break;
            }

            stringBuilder.setLength(bb);
        }

        if (string4 != null && !string4.equals(string5)) {
            String string9 = string2.substring(string4.length() + 1);
            if (string9.length() >= string3.length()) {
                return string3;
            }

            String string7;
            if (!string3.equals(string2)) {
                String string10 = string2.substring(0, string2.indexOf(string3) - 1);
                string7 = (String) enumerableMap.get(string10);
            } else {
                string7 = "";
            }

            String string8;
            if (string7.equals(string5)) {
                string8 = string9;
            } else if (string5.startsWith(string7)) {
                StringBuilder stringBuilder1 = new StringBuilder();
                int bc = string7.length();
                StringBuilder stringBuilder2;
                char bd;
                if (bc == 0) {
                    stringBuilder1.append(string5);
                    stringBuilder2 = stringBuilder1;
                    bd = '/';
                } else {
                    stringBuilder1.append(string5.substring(bc + 1));
                    stringBuilder2 = stringBuilder1;
                    bd = '/';
                }

                stringBuilder2.append(bd);
                stringBuilder1.append(string9);
                string8 = stringBuilder1.toString();
            } else {
                string8 = "../" + string9;
            }

            return string8;
        } else {
            return string3;
        }
    }

    static {
        setMarkerString();
    }
}
