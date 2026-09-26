package com.zelix.klassmaster.classfile.hierarchy;

import com.zelix.klassmaster.archive.ArchiveManifest;
import com.zelix.klassmaster.archive.ArchiveZipFile;
import com.zelix.klassmaster.archive.InputFileLocation;
import com.zelix.klassmaster.archive.SourceArchive;
import com.zelix.klassmaster.archive.TempFileManager;
import com.zelix.klassmaster.archive.ZkmFileUtils;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.AssertionFailedException;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.log.MessageReporter;
import com.zelix.klassmaster.util.ObjectPair;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ZkmAssert;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class ClassPathResolver {
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    public static final char FILE_SEPARATOR_CHAR = FILE_SEPARATOR.charAt(0);
    public static final String PATH_SEPARATOR = System.getProperty("path.separator");
    public static final String[] EMBEDDED_CLASS_DIRS = new String[]{"WEB-INF/classes/", "BOOT-INF/classes/"};
    public static Object NOT_FOUND_MARKER = new Object();
    public static final String MISSING_ENTRY_SUFFIX = String.valueOf('#');
    public List classpathEntries;
    public String classpathString;
    public Map lookupCache;
    public Set invalidEntries;
    public Set tempFiles = ZkmUtils.createHashSet();
    public Map resourceArchives = ZkmUtils.createHashMap();
    public boolean cacheMisses = false;

    public void setClasspath(String string, MessageReporter messageReporter1) throws ZkmException, IOException {
        this.classpathString = string;
        this.invalidEntries = new LinkedHashSet();
        this.classpathEntries = parseClasspath(string, this.invalidEntries, this.tempFiles, (File) null, messageReporter1);
        if (HiddenOptionFlags.USE_PARALLEL) {
            this.lookupCache = new ConcurrentHashMap();
        } else {
            this.lookupCache = ZkmUtils.createHashMap();
        }
    }

    public static void addManifestClassPath(ZipFile zipFile1, List list1, Set set1) {
        ZipEntry zipEntry1 = templateEntry(zipFile1, "META-INF/MANIFEST.MF");
        if (zipEntry1 != null) {
            ArchiveManifest archiveManifest;
            try {
                archiveManifest = new ArchiveManifest(zipFile1, zipEntry1);
            } catch (Exception exception) {
                return;
            }

            addManifestEntries(zipFile1, list1, set1, archiveManifest.getMainAttribute("Class-Path"));
            addManifestEntries(zipFile1, list1, set1, archiveManifest.getMainAttribute("Rsrc-Class-Path"));
        }
    }

    public Object[] getEntries() {
        return this.classpathEntries.toArray();
    }

    public boolean hasPackageDirectory(String string) {
        String string1 = string.replace('/', FILE_SEPARATOR_CHAR);
        int ba = 0;
        int bb = 0;

        for (List list1 = this.classpathEntries; bb < list1.size(); list1 = this.classpathEntries) {
            Object object = this.classpathEntries.get(ba);
            if (object instanceof File) {
                File file1 = (File) object;
                String string2 = file1.getAbsolutePath() + FILE_SEPARATOR + string1;
                File file2 = new File(string2);
                if (file2.exists() && file2.isDirectory()) {
                    return true;
                }
            } else if (object instanceof ZipFile) {
                ZipFile zipFile1 = (ZipFile) object;
                ZipEntry zipEntry1 = templateEntry(zipFile1, string);
                if (zipEntry1 != null && zipEntry1.isDirectory()) {
                    return true;
                }
            }

            bb = ++ba;
        }

        return false;
    }

    public static void extractNestedArchives(ZipFile zipFile1, List list1, Set set1, Set set2, MessageReporter messageReporter1) throws ZkmException, IOException {
        Enumeration<? extends ZipEntry> enumeration = zipFile1.entries();

        while (enumeration.hasMoreElements()) {
            ZipEntry zipEntry1 = (ZipEntry) enumeration.nextElement();
            String string = ZkmFileUtils.getEntryPath(zipFile1, zipEntry1);
            if (!zipEntry1.isDirectory()) {
                boolean bl = false;

                try {
                    bl = ZkmFileUtils.isNestedArchiveEntry(zipFile1, zipEntry1);
                } catch (IOException iOException) {
                    set1.add(string);
                }

                if (bl) {
                    File file1 = null;
                    boolean bl1 = false;

                    try {
                        file1 = ZkmFileUtils.extractEntryToTempFile(zipFile1, zipEntry1);
                        if (set2 != null) {
                            set2.add(file1.getAbsolutePath());
                        }
                    } catch (IOException iOException1) {
                        bl1 = true;
                        if (messageReporter1 != null) {
                            messageReporter1.reportFatalError(
                                    "FATAL ERROR:",
                                    "Could not create temporary file in '" + TempFileManager.tempDirPath + "' : '" + string + "' : " + iOException1 + " (4)"
                            );
                        }
                    }

                    if (!bl1 && file1 != null) {
                        try {
                            ArchiveZipFile archiveZipFile = new ArchiveZipFile(file1);
                            list1.add(archiveZipFile);
                            extractNestedArchives(archiveZipFile, list1, set1, set2, messageReporter1);
                        } catch (IOException iOException2) {
                            if (messageReporter1 != null) {
                                messageReporter1.reportFatalError(
                                        "FATAL ERROR:",
                                        "Could not open ZipFile '"
                                                + file1.getAbsolutePath()
                                                + "' in '"
                                                + TempFileManager.tempDirPath
                                                + "' : '"
                                                + string
                                                + "' : "
                                                + iOException2
                                );
                            }
                        }
                    }
                }
            }
        }
    }

    public InputFileLocation findInArchive(ZipFile zipFile1, String string) {
        String string1 = toClassFileName(string, false);
        ZipEntry zipEntry1 = templateEntry(zipFile1, string1);
        if (zipEntry1 == null) {
            for (String string2 : EMBEDDED_CLASS_DIRS) {
                String string3 = string2 + string1;
                zipEntry1 = templateEntry(zipFile1, string3);
                if (zipEntry1 != null) {
                    break;
                }
            }
        }

        if (zipEntry1 != null && !zipEntry1.isDirectory()) {
            File file1 = new File(zipFile1.getName());
            if (TempFileManager.isZkmTempFile(file1)) {
                String string4 = TempFileManager.getOriginalName(zipFile1.getName());
                if (string4 != null) {
                    SourceArchive sourceArchive1 = new SourceArchive(file1.getAbsolutePath(), file1.length(), string4);
                    return new InputFileLocation(zipFile1, zipEntry1, sourceArchive1);
                }
            }

            return new InputFileLocation(zipFile1, zipEntry1);
        } else {
            return null;
        }
    }

    public static List parseClasspath(String string, Set set1) throws ZkmException, IOException {
        Object object = null;
        return parseClasspath(string, set1, (File) null, (MessageReporter) object);
    }

    public static void addManifestEntries(ZipFile zipFile1, List list1, Set set1, String string) {
        if (string != null) {
            StringTokenizer stringTokenizer = new StringTokenizer(string, " ");
            File file1 = new File(zipFile1.getName()).getParentFile();

            while (stringTokenizer.hasMoreTokens()) {
                String string1 = ZkmFileUtils.normalizeSeparators(stringTokenizer.nextToken().trim());
                string1 = ZkmFileUtils.resolveDotRelativePath(string1, file1);
                File file2;
                if (ZkmFileUtils.isRelativePath(string1)) {
                    file2 = new File(file1, string1);
                } else {
                    file2 = new File(string1);
                }

                if (file2.exists()) {
                    list1.add(file2.getAbsolutePath());
                } else {
                    set1.add(file2.getAbsolutePath() + '#');
                }
            }
        }
    }

    public static boolean archiveContainsClass(String string) {
        File file1 = new File(string);

        ArchiveZipFile archiveZipFile;
        try {
            archiveZipFile = new ArchiveZipFile(file1);
        } catch (IOException iOException1) {
            return false;
        }

        String string1 = toClassFileName("java/lang/Object", false);
        ZipEntry zipEntry1 = templateEntry(archiveZipFile, string1);
        boolean bl;
        if (zipEntry1 != null && !zipEntry1.isDirectory()) {
            bl = true;
        } else {
            bl = false;
        }

        try {
            com.zelix.klassmaster.util.ZkmUtils.<java.io.IOException>mayThrow();
            archiveZipFile.close();
        } catch (IOException iOException) {
        }

        return bl;
    }

    public ClassPathResolver(String string) throws ZkmException, IOException {
        this(string, (MessageReporter) null);
    }

    public static List parseClasspath(String string, Set set1, File file1, MessageReporter messageReporter1) throws ZkmException, IOException {
        MessageReporter messageReporter2 = messageReporter1;
        File file2 = file1;
        return parseClasspath(string, set1, (Set) null, file2, messageReporter2);
    }

    private ClassPathResolver(String string, MessageReporter messageReporter1) throws ZkmException, IOException {
        this.setClasspath(string, messageReporter1);
    }

    public InputFileLocation findClassFile(String string, boolean bl) {
        Object object = this.lookupCache.get(string);
        if (object != null) {
            if (object == NOT_FOUND_MARKER) {
                return null;
            }

            if (object instanceof File) {
                return new InputFileLocation((File) object);
            }

            if (object instanceof ObjectPair) {
                ObjectPair objectPair = (ObjectPair) object;
                ZipFile zipFile1 = (ZipFile) objectPair.getFirst();
                ZipEntry zipEntry1 = (ZipEntry) objectPair.getSecond();
                return new InputFileLocation(zipFile1, zipEntry1);
            }
        }

        InputFileLocation inputFileLocation = null;
        int ba = 0;
        int bc = 0;

        for (List list1 = this.classpathEntries; bc < list1.size(); list1 = this.classpathEntries) {
            Object object1 = this.classpathEntries.get(ba);
            if (object1 instanceof File) {
                inputFileLocation = this.findInDirectory((File) object1, string);
                if (inputFileLocation != null) {
                    break;
                }
            } else if (object1 instanceof ZipFile) {
                inputFileLocation = this.findInArchive((ZipFile) object1, string);
                if (inputFileLocation != null) {
                    break;
                }
            }

            bc = ++ba;
        }

        if (bl && inputFileLocation == null) {
            URL uRL = this.getClass().getResource("/" + string.replace('.', '/') + ".class");
            if (uRL != null) {
                String string4 = uRL.getProtocol();
                String string1 = ZkmFileUtils.urlDecode(uRL.getPath());
                if (string4.equalsIgnoreCase("jar")) {
                    File file1;
                    if (string1.substring(0, 5).equalsIgnoreCase("file:")) {
                        String string2 = string1.substring(5, string1.length());
                        int bb = string2.indexOf(33);
                        boolean bl1 = bb > 0;
                        String[] strings = new String[]{uRL.toString()};
                        ZkmAssert.assertTrue(bl1, strings);
                        bl1 = bb == string2.lastIndexOf(33);
                        strings = new String[]{uRL.toString()};
                        ZkmAssert.assertTrue(bl1, strings);
                        String string3 = string2.substring(0, bb);
                        file1 = new File(string3);
                    } else {
                        file1 = new File(string1);
                    }

                    if (file1.exists() && !file1.isDirectory()) {
                        try {
                            String string5 = file1.getAbsolutePath();
                            ZipFile zipFile2;
                            if (this.resourceArchives.containsKey(string5)) {
                                zipFile2 = (ZipFile) this.resourceArchives.get(string5);
                            } else {
                                zipFile2 = new ArchiveZipFile(file1);
                                this.resourceArchives.put(string5, zipFile2);
                            }

                            inputFileLocation = this.findInArchive(zipFile2, string);
                        } catch (IOException iOException) {
                        }
                    }
                } else if (string4.equalsIgnoreCase("file")) {
                    File file2 = new File(string1);
                    if (file2.exists() && !file2.isDirectory()) {
                        inputFileLocation = new InputFileLocation(file2);
                    }
                }
            }
        }

        if (this.cacheMisses && inputFileLocation == null) {
            this.lookupCache.put(string, NOT_FOUND_MARKER);
        }

        return inputFileLocation;
    }

    public static boolean addUniquePath(String string, Set set1) {
        String string1 = string;
        if (!ZkmFileUtils.caseSensitiveFileSystem) {
            string1 = string1.toUpperCase();
        }

        return set1.add(string1);
    }

    public InputFileLocation findInOwnCodeSource(String string) {
        CodeSource codeSource = this.getClass().getProtectionDomain().getCodeSource();
        if (codeSource != null) {
            URL uRL = codeSource.getLocation();
            if (uRL != null) {
                String string1 = uRL.getProtocol();
                String string2 = ZkmFileUtils.urlDecode(uRL.getPath());
                if (string1.equalsIgnoreCase("file")) {
                    File file1 = new File(string2);
                    if (file1.exists()) {
                        InputFileLocation inputFileLocation = null;
                        if (file1.isDirectory()) {
                            inputFileLocation = this.findInDirectory(file1, string);
                        } else {
                            try {
                                ArchiveZipFile archiveZipFile = new ArchiveZipFile(file1);
                                inputFileLocation = this.findInArchive(archiveZipFile, string);
                            } catch (IOException iOException) {
                            }
                        }

                        return inputFileLocation;
                    }
                } else if (string1.equalsIgnoreCase("jar")) {
                    File file2;
                    boolean bl;
                    if (string2.substring(0, 5).equalsIgnoreCase("file:")) {
                        String string4 = string2.substring(5, string2.length());
                        int ba = string4.indexOf(33);
                        bl = ba > 0;
                        String[] strings = new String[]{uRL.toString()};
                        ZkmAssert.assertTrue(bl, strings);
                        bl = ba == string4.lastIndexOf(33);
                        strings = new String[]{uRL.toString()};
                        ZkmAssert.assertTrue(bl, strings);
                        String string3 = string4.substring(0, ba);
                        file2 = new File(string3);
                        bl = file2.exists();
                    } else {
                        file2 = new File(string2);
                        bl = file2.exists();
                    }

                    if (bl) {
                        InputFileLocation inputFileLocation1 = null;

                        try {
                            ArchiveZipFile archiveZipFile1 = new ArchiveZipFile(file2);
                            inputFileLocation1 = this.findInArchive(archiveZipFile1, string);
                        } catch (IOException iOException1) {
                        }

                        return inputFileLocation1;
                    }
                } else {
                    String[] strings1 = new String[]{string1};
                    ZkmAssert.assertTrue(false, strings1);
                }
            }
        }

        return null;
    }

    public static List parseClasspath(String string, Set set1, Set set2, File file1, MessageReporter messageReporter1) throws ZkmException, IOException {
        File file2 = file1;
        if (file2 == null) {
            file2 = ZkmFileUtils.USER_DIR_FILE;
        }

        Vector vector = new Vector();
        StringTokenizer stringTokenizer = new StringTokenizer(string, PATH_SEPARATOR);

        while (stringTokenizer.hasMoreTokens()) {
            String string1 = stringTokenizer.nextToken();
            if (string1.trim().length() > 0) {
                String string2 = ZkmFileUtils.normalizeSeparators(string1);
                string2 = ZkmFileUtils.resolveDotRelativePath(string2, file2);
                if (ZkmFileUtils.containsWildcard(string2)) {
                    ObservableHolder observableHolder = new ObservableHolder();
                    List list1 = ZkmFileUtils.expandFilePath(string2, file2, observableHolder);
                    if (observableHolder.isValueNull()) {
                        vector.addAll(list1);
                    } else {
                        set1.add(string2);
                    }
                } else {
                    vector.add(string2);
                }
            } else {
                set1.add(string1);
            }
        }

        ArrayList arrayList = new ArrayList(vector.size());
        HashSet hashSet = ZkmUtils.createHashSet();

        for (int i = 0; i < vector.size(); i++) {
            String string3 = (String) vector.get(i);
            if (addUniquePath(string3, hashSet)) {
                File file3;
                if (ZkmFileUtils.isRelativePath(string3)) {
                    file3 = new File(file2, string3);
                } else {
                    file3 = new File(string3);
                }

                if (file3.exists()) {
                    if (file3.isDirectory()) {
                        arrayList.add(file3);
                    } else {
                        try {
                            ArchiveZipFile archiveZipFile = new ArchiveZipFile(file3);
                            arrayList.add(archiveZipFile);
                            if (HiddenOptionFlags.FOLLOW_MANIFEST_CLASS_PATH) {
                                addManifestClassPath(archiveZipFile, vector, set1);
                            }

                            extractNestedArchives(archiveZipFile, arrayList, set1, set2, messageReporter1);
                        } catch (ZipException zipException) {
                            set1.add(file3.getAbsolutePath());
                        } catch (IOException iOException) {
                            set1.add(file3.getAbsolutePath());
                        }
                    }
                } else {
                    set1.add(file3.getAbsolutePath());
                }
            }
        }

        return arrayList;
    }

    public void close() {
        if (this.classpathEntries != null) {
            int ba = 0;
            int bb = 0;

            for (List list1 = this.classpathEntries; bb < list1.size(); list1 = this.classpathEntries) {
                Object object = this.classpathEntries.get(ba);
                if (object instanceof ZipFile) {
                    ZipFile zipFile1 = (ZipFile) object;

                    try {
                        zipFile1.close();
                    } catch (IOException iOException1) {
                    }
                }

                bb = ++ba;
            }
        }

        Iterator iterator = this.resourceArchives.values().iterator();

        while (iterator.hasNext()) {
            ZipFile zipFile2 = (ZipFile) iterator.next();

            try {
                zipFile2.close();
            } catch (IOException iOException) {
            }
        }

        if (this.tempFiles != null) {
            iterator = this.tempFiles.iterator();

            while (iterator.hasNext()) {
                String string = (String) iterator.next();

                try {
                    File file1 = new File(string);
                    file1.delete();
                } catch (AssertionFailedException assertionFailedException) {
                    throw assertionFailedException;
                } catch (Throwable throwable) {
                }
            }
        }
    }

    public InputFileLocation findInDirectory(File file1, String string) {
        String string1 = file1.getAbsolutePath() + FILE_SEPARATOR + toClassFileName(string, true);
        File file2 = new File(string1);
        return file2.exists() && !file2.isDirectory() ? new InputFileLocation(file2) : null;
    }

    public static String toClassFileName(String string, boolean bl) {
        return bl ? string.replace('/', FILE_SEPARATOR_CHAR) + ".class" : string + ".class";
    }

    private static ZipEntry templateEntry(ZipFile zipFile1, String string) {
        if (string != null && string.startsWith("com/zelix/")) {
            ZipEntry zipEntry1 = zipFile1.getEntry("zkm-templates/".concat(string));
            if (zipEntry1 != null) {
                return zipEntry1;
            }
        }

        return zipFile1.getEntry(string);
    }
}
