package com.zelix.klassmaster.archive;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.log.MessageReporter;
import com.zelix.klassmaster.util.EnumerableMap;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ZkmUtils;
import com.zelix.klassmaster.xml.ResourcePathTranslator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ArchiveManifest {
    public List entrySections = new ArrayList();
    public String archiveName;
    public boolean compressed;
    public ManifestHandler mainSection;

    public boolean isCldc10() throws ZkmException {
        return this.mainSection.isCldc10();
    }

    public boolean isCompressed() {
        return this.compressed;
    }

    public void writeManifest(
            ZipOutputStream zipOutputStream1,
            Long long1,
            EnumerableMap enumerableMap,
            EnumerableMap enumerableMap1,
            ResourcePathTranslator resourcePathTranslator1,
            boolean bl,
            MessageReporter messageReporter1
    ) throws IOException {
        ZipEntryWriter zipEntryWriter = new ZipEntryWriter(zipOutputStream1, "META-INF/MANIFEST.MF", bl);
        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(zipEntryWriter.getOutputStream(), "UTF-8"));
        boolean bl1 = hasDuplicateValues(enumerableMap);
        char bc = '\uda1b';
        MessageReporter messageReporter2 = messageReporter1;
        boolean bl2 = bl1;
        String string = this.archiveName;
        ResourcePathTranslator resourcePathTranslator2 = resourcePathTranslator1;
        EnumerableMap enumerableMap2 = enumerableMap1;
        EnumerableMap enumerableMap3 = enumerableMap;
        PrintWriter printWriter1 = printWriter;
        this.mainSection.writeSection(printWriter1, enumerableMap3, enumerableMap2, resourcePathTranslator2, string, bl2, messageReporter2);
        int ba = 0;
        int bb = 0;

        for (List list1 = this.entrySections; bb < list1.size(); list1 = this.entrySections) {
            ManifestEntrySection manifestEntrySection = (ManifestEntrySection) this.entrySections.get(ba);
            bc = '\uda1b';
            MessageReporter messageReporter3 = messageReporter1;
            boolean bl3 = bl1;
            String string1 = this.archiveName;
            ResourcePathTranslator resourcePathTranslator3 = resourcePathTranslator1;
            EnumerableMap enumerableMap4 = enumerableMap1;
            EnumerableMap enumerableMap5 = enumerableMap;
            PrintWriter printWriter2 = printWriter;
            manifestEntrySection.writeSection(printWriter2, enumerableMap5, enumerableMap4, resourcePathTranslator3, string1, bl3, messageReporter3);
            bb = ++ba;
        }

        printWriter.flush();
        zipEntryWriter.writeEntry(long1);
    }

    public void collectEntryPointClasses(String string, Map map1, Map map2, Map map3) {
        this.mainSection.collectEntryPointClasses(string, map1, map2, map3);
        int ba = 0;
        int bb = 0;

        for (List list1 = this.entrySections; bb < list1.size(); list1 = this.entrySections) {
            ((ManifestEntrySection) this.entrySections.get(ba)).collectEntryPointClasses();
            bb = ++ba;
        }
    }

    public boolean hasMicroEditionConfiguration() {
        return this.mainSection.hasMicroEditionConfiguration();
    }

    public ArchiveManifest(ZipFile zipFile1, ZipEntry zipEntry1) throws ZkmException, IOException {
        InputStream inputStream1 = null;
        BufferedReader bufferedReader = null;
        this.archiveName = zipFile1.getName();
        if (zipEntry1.getMethod() == 8) {
            this.compressed = true;
        } else {
            this.compressed = false;
        }

        try {
            inputStream1 = zipFile1.getInputStream(zipEntry1);
            bufferedReader = ZkmFileUtils.createBomAwareReader(inputStream1, "UTF-8", (ObservableHolder) null);
            this.mainSection = new ManifestHandler(bufferedReader);
            boolean endOfStream = this.mainSection.isEndOfStream();

            while (!endOfStream) {
                ManifestEntrySection manifestEntrySection = new ManifestEntrySection(bufferedReader);
                if (!manifestEntrySection.hasNoHeaders()) {
                    this.entrySections.add(manifestEntrySection);
                }

                endOfStream = manifestEntrySection.isEndOfStream();
            }
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException iOException1) {
                }
            } else if (inputStream1 != null) {
                try {
                    inputStream1.close();
                } catch (IOException iOException) {
                }
            }
        }
    }

    public static boolean isVersionedEntryPath(String string, MutableInt mutableInt) {
        mutableInt.setValue(-1);
        if (string.startsWith("META-INF/versions/") && string.length() > "META-INF/versions/".length()) {
            String string1 = string.substring("META-INF/versions/".length());
            int ba = string1.indexOf(47);
            if (ba > 0) {
                String string2 = string1.substring(0, ba);

                try {
                    int bb = Integer.parseInt(string2);
                    mutableInt.setValue(bb);
                    return true;
                } catch (NumberFormatException numberFormatException) {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean hasDuplicateValues(EnumerableMap enumerableMap) {
        boolean bl = false;
        HashSet hashSet = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(enumerableMap.size()));
        Iterator iterator = enumerableMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            if (!hashSet.add(entry.getValue())) {
                bl = true;
                break;
            }
        }

        return bl;
    }

    public String getMainAttribute(String string) {
        return this.mainSection.getUnwrappedHeaderValue(string);
    }
}
