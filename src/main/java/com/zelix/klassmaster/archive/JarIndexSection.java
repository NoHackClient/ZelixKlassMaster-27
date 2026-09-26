package com.zelix.klassmaster.archive;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.SetMultiMap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

public class JarIndexSection {
    public static String sectionTerminator;
    public boolean firstSection;
    public ArrayList entries = new ArrayList();
    public String jarName;
    public boolean endOfStream;

    public boolean hasNoEntries() {
        return this.entries.size() == 0;
    }

    public Set resolveEntries(String string, SourceArchive sourceArchive1, Map map1, SetMultiMap setMultiMap, SetMultiMap setMultiMap1) throws ZkmException {
        HashMap hashMap = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(map1.size()));
        String string1 = sourceArchive1.getQualifiedPath();
        if (this.firstSection) {
            hashMap.put(string1, sourceArchive1);
        } else {
            Iterator iterator = map1.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                String string2 = (String) entry.getKey();
                if (!string2.equals(string1) && string2.endsWith(string.replace("\\", "/"))) {
                    hashMap.put(string2, entry.getValue());
                }
            }
        }

        if (hashMap.size() == 0) {
            return null;
        }

        SourceArchive sourceArchive3;
        if (hashMap.size() > 1) {
            HashMap hashMap1 = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(hashMap.size()));
            Iterator iterator2 = hashMap.entrySet().iterator();

            label70:
            while (iterator2.hasNext()) {
                Entry entry1 = (Entry) iterator2.next();
                String string3 = (String) entry1.getKey();
                SourceArchive sourceArchive2 = (SourceArchive) entry1.getValue();
                Iterator iterator1 = this.entries.iterator();

                while (iterator1.hasNext()) {
                    String string4 = (String) iterator1.next();
                    if (string4.indexOf("/") == -1 && !string4.endsWith(".class") && !setMultiMap.getValues(sourceArchive2).contains(string4)) {
                        continue label70;
                    }
                }

                hashMap1.put(string3, sourceArchive2);
            }

            if (hashMap1.size() == 0) {
                return null;
            }

            if (hashMap1.size() > 1) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("ERROR:");
                stringBuilder.append(" More than one matching JAR file for name '");
                stringBuilder.append(string);
                stringBuilder.append("' : '");
                int ba = 0;
                Set set3 = hashMap1.keySet();
                int bb = set3.size();
                Iterator iterator3 = set3.iterator();

                while (iterator3.hasNext()) {
                    String string5 = (String) iterator3.next();
                    stringBuilder.append(string5);
                    stringBuilder.append("'");
                    if (++ba < bb) {
                        stringBuilder.append(", ");
                    }
                }

                throw new ZkmException(stringBuilder.toString());
            }

            sourceArchive3 = (SourceArchive) ((Entry) hashMap.entrySet().iterator().next()).getValue();
        } else {
            sourceArchive3 = (SourceArchive) ((Entry) hashMap.entrySet().iterator().next()).getValue();
        }

        TreeSet treeSet = new TreeSet();
        Set set1 = setMultiMap.getValues(sourceArchive3);
        if (set1 != null) {
            treeSet.addAll(set1);
        }

        Set set2 = setMultiMap1.getValues(sourceArchive3);
        if (set2 != null) {
            treeSet.addAll(set2);
        }

        return treeSet;
    }

    public boolean isEndOfStream() {
        return this.endOfStream;
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    public void markAsFirstSection() {
        this.firstSection = true;
    }

    public void writeSection(PrintWriter printWriter, SourceArchive sourceArchive1, Map map1, SetMultiMap setMultiMap, SetMultiMap setMultiMap1) throws ZkmException {
        printWriter.println(this.jarName);
        Set set1 = this.resolveEntries(this.jarName, sourceArchive1, map1, setMultiMap, setMultiMap1);
        ArrayList arrayList;
        if (set1 != null) {
            this.entries = new ArrayList(set1);
            arrayList = this.entries;
        } else {
            arrayList = this.entries;
        }

        Iterator iterator = arrayList.iterator();

        while (iterator.hasNext()) {
            String string = (String) iterator.next();
            printWriter.println(string);
        }

        printWriter.println();
    }

    public JarIndexSection(BufferedReader bufferedReader) throws IOException {
        boolean bl = false;

        String string;
        while ((string = bufferedReader.readLine()) != null) {
            if (string.equals("")) {
                if (bl) {
                    return;
                }
            } else if (string.equals(sectionTerminator)) {
                if (bl) {
                    return;
                }
            } else if (!bl) {
                bl = true;
                this.jarName = string;
            } else if (string.charAt(0) == ' ') {
                String string1 = (String) this.entries.get(this.entries.size() - 1);
                String string2 = string1 + string.substring(1, string.length() - 1);
                this.entries.add(string2);
            } else {
                this.entries.add(string);
            }
        }

        this.endOfStream = true;
    }

    private static void staticInit() {
        sectionTerminator = "&";
    }
}
