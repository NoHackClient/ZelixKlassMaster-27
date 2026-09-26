package com.zelix.klassmaster.archive;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.SetMultiMap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class JarIndexListRewriter {
    public final ArrayList sections = new ArrayList();
    public SourceArchive sourceArchive;
    public String zipFileName;
    public boolean compressed;
    public final String versionHeader;

    public void writeIndexList(ZipOutputStream zipOutputStream1, Long long1, SetMultiMap setMultiMap, SetMultiMap setMultiMap1, Boolean boolean1) throws ZkmException, IOException {
        HashMap hashMap = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(setMultiMap.getKeyCount() + setMultiMap1.getKeyCount()));
        Iterator iterator = setMultiMap.keySet().iterator();

        while (iterator.hasNext()) {
            SourceArchive sourceArchive1 = (SourceArchive) iterator.next();
            hashMap.put(sourceArchive1.getQualifiedPath(), sourceArchive1);
        }

        iterator = setMultiMap1.keySet().iterator();

        while (iterator.hasNext()) {
            SourceArchive sourceArchive2 = (SourceArchive) iterator.next();
            hashMap.put(sourceArchive2.getQualifiedPath(), sourceArchive2);
        }

        ZipEntryWriter zipEntryWriter = new ZipEntryWriter(zipOutputStream1, "META-INF/INDEX.LIST", boolean1);
        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(zipEntryWriter.getOutputStream(), "UTF-8"));
        printWriter.println(this.versionHeader);
        printWriter.println();
        Iterator iterator1 = this.sections.iterator();

        while (iterator1.hasNext()) {
            ((JarIndexSection) iterator1.next()).writeSection(printWriter, this.sourceArchive, hashMap, setMultiMap, setMultiMap1);
        }

        printWriter.println();
        printWriter.flush();
        zipEntryWriter.writeEntry(long1);
    }

    public JarIndexListRewriter(SourceArchive sourceArchive1, ZipFile zipFile1, ZipEntry zipEntry1) throws ZkmException, IOException {
        InputStream inputStream1 = null;
        BufferedReader bufferedReader = null;
        this.sourceArchive = sourceArchive1;
        this.zipFileName = zipFile1.getName();
        if (zipEntry1.getMethod() == 8) {
            this.compressed = true;
        } else {
            this.compressed = false;
        }

        try {
            inputStream1 = zipFile1.getInputStream(zipEntry1);
            bufferedReader = ZkmFileUtils.createBomAwareReader(inputStream1, "UTF-8", (ObservableHolder) null);
            this.versionHeader = bufferedReader.readLine();
            if (this.versionHeader == null) {
                throw new ZkmException("Invalid INDEX.LIST (1)");
            }

            if (!this.versionHeader.startsWith("JarIndex-Version: ")) {
                throw new ZkmException("Invalid INDEX.LIST (2)");
            }

            boolean bl = false;

            while (!bl) {
                JarIndexSection jarIndexSection = new JarIndexSection(bufferedReader);
                if (!jarIndexSection.hasNoEntries()) {
                    ArrayList arrayList;
                    if (this.sections.isEmpty()) {
                        jarIndexSection.markAsFirstSection();
                        arrayList = this.sections;
                    } else {
                        arrayList = this.sections;
                    }

                    arrayList.add(jarIndexSection);
                }

                bl = jarIndexSection.isEndOfStream();
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

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
    }
}
