package com.zelix.klassmaster.classfile.hierarchy;

import com.zelix.klassmaster.archive.ArchiveZipFile;
import com.zelix.klassmaster.archive.TempFileManager;
import com.zelix.klassmaster.archive.ZkmFileUtils;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ClassFileLoadException;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.log.MessageReporter;
import com.zelix.klassmaster.util.ChangeObservable;
import com.zelix.klassmaster.util.JavaRuntimeVersion;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.SetMultiMap;
import com.zelix.klassmaster.util.SystemEnvironmentConstants;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.ProviderNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class ZkmClasspath extends ChangeObservable {
    public static String objectClassFilePath = "java/lang/Object".replace('/', SystemEnvironmentConstants.FILE_SEPARATOR_CHAR) + ".class";
    public static final String JRT_FS_JAR_PATH = "lib" + SystemEnvironmentConstants.FILE_SEPARATOR + "jrt-fs.jar";
    public boolean ownsJrtFileSystem;
    public boolean runtimeClassesFound;
    public FileSystem jrtFileSystem;
    public SetMultiMap moduleDirsByPackage;
    public ClasspathClassLoader classLoader;
    public boolean jrtAvailable;
    public String classpath;
    public Integer releaseVersion;

    public static FileSystem openJrtFileSystem(String string) {
        FileSystem fileSystem = null;

        try {
            if (JavaRuntimeVersion.isRuntimeAtLeastJava9()) {
                HashMap hashMap = ZkmUtils.createHashMap(13);
                hashMap.put("java.home", string);
                fileSystem = FileSystems.newFileSystem(URI.create("jrt:/"), hashMap);
            } else {
                URL uRL = Paths.get(string, "lib", "jrt-fs.jar").toUri().toURL();
                URLClassLoader uRLClassLoader = new URLClassLoader(new URL[]{uRL});
                fileSystem = FileSystems.newFileSystem(URI.create("jrt:/"), Collections.emptyMap(), uRLClassLoader);
            }
        } catch (IOException iOException) {
        }

        return fileSystem;
    }

    public ZkmClasspath(String string) throws ZkmException, IOException {
        this(string, null, ZkmUtils.createHashSet());
    }

    public static boolean containsRuntimeClasses(String string) {
        StringTokenizer stringTokenizer = new StringTokenizer(string, SystemEnvironmentConstants.PATH_SEPARATOR_String);

        while (stringTokenizer.hasMoreTokens()) {
            String string1 = stringTokenizer.nextToken();
            File file1 = new File(string1);
            if (file1.exists()) {
                if (file1.isDirectory()) {
                    String string2 = (string1.endsWith(SystemEnvironmentConstants.FILE_SEPARATOR) ? string1 : string1 + SystemEnvironmentConstants.FILE_SEPARATOR)
                            + objectClassFilePath;
                    if (new File(string2).exists()) {
                        return true;
                    }
                } else if (ZkmFileUtils.isKnownArchiveName(string1) && isRuntimeArchivePath(string1)) {
                    return true;
                }
            }
        }

        return false;
    }

    public String getClasspath() {
        return this.classpath;
    }

    public static String describeClasspath(String string) {
        StringBuilder stringBuilder = new StringBuilder();
        StringTokenizer stringTokenizer = new StringTokenizer(string, SystemEnvironmentConstants.PATH_SEPARATOR_String);

        while (stringTokenizer.hasMoreTokens()) {
            String string1 = stringTokenizer.nextToken();
            stringBuilder.append(string1);
            if (TempFileManager.isZkmTempFilePath(string1)) {
                stringBuilder.append('(');
                stringBuilder.append(TempFileManager.getOriginalName(string1));
                stringBuilder.append(')');
            }

            if (stringTokenizer.hasMoreTokens()) {
                stringBuilder.append(SystemEnvironmentConstants.PATH_SEPARATOR_CHAR);
            }
        }

        return stringBuilder.toString();
    }

    public byte[] readJrtClassBytes(String string, ObservableHolder observableHolder) throws ZkmException, IOException {
        observableHolder.clearValue();
        if (!this.jrtAvailable) {
            return null;
        }

        String string1 = ClassFileBase.getPackagePath(string);
        if (string1 != null && string1.length() > 0) {
            Set set1 = this.moduleDirsByPackage.getValues(string1);
            if (set1 == null) {
                return null;
            }

            StringBuilder stringBuilder = new StringBuilder();
            Path path1 = null;
            Iterator iterator = set1.iterator();

            while (iterator.hasNext()) {
                String string2 = (String) iterator.next();
                stringBuilder.setLength(0);
                stringBuilder.append(string);
                stringBuilder.append(".class");
                Path path2 = this.jrtFileSystem.getPath(string2, stringBuilder.toString());
                if (Files.exists(path2)) {
                    path1 = path2;
                    break;
                }
            }

            if (path1 != null) {
                observableHolder.setValue(path1);
                return Files.readAllBytes(path1);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public ZkmClasspath(String string, Set set1) throws ZkmException, IOException {
        this(string, null, set1);
    }

    public List getUserClasspathEntries() {
        ArrayList arrayList = new ArrayList();
        StringTokenizer stringTokenizer = new StringTokenizer(this.classpath, SystemEnvironmentConstants.PATH_SEPARATOR_String);

        while (stringTokenizer.hasMoreTokens()) {
            String string = stringTokenizer.nextToken();
            if (!TempFileManager.isZkmTempFilePath(string)) {
                arrayList.add(string);
            }
        }

        return arrayList;
    }

    public static String findRuntimeLibrary() {
        String string = null;
        if (SystemEnvironmentConstants.BOOT_CLASS_PATH != null
                && SystemEnvironmentConstants.BOOT_CLASS_PATH.length() > 0
                && containsRuntimeClasses(SystemEnvironmentConstants.BOOT_CLASS_PATH)) {
            string = filterExistingPaths(SystemEnvironmentConstants.BOOT_CLASS_PATH);
        }

        if (string == null && SystemEnvironmentConstants.JAVA_HOME != null && SystemEnvironmentConstants.JAVA_HOME.length() > 0) {
            File file1 = new File(SystemEnvironmentConstants.JAVA_HOME);
            if (file1.exists() && file1.isDirectory()) {
                String string1 = file1.getAbsolutePath() + SystemEnvironmentConstants.FILE_SEPARATOR + "lib";
                File file2 = new File(string1);
                if (file2.exists() && file2.isDirectory()) {
                    File file3 = new File(file2, "rt.jar");
                    if (isRuntimeArchive(file3)) {
                        string = file3.getAbsolutePath();
                    } else {
                        File file4 = new File(file2, "classes.zip");
                        if (isRuntimeArchive(file4)) {
                            string = file4.getAbsolutePath();
                        }
                    }
                }
            }
        }

        return string;
    }

    public void closeJrtFileSystem() {
        if (this.jrtFileSystem != null) {
            label19:
            if (this.ownsJrtFileSystem) {
                try {
                    this.jrtFileSystem.close();
                } catch (IOException iOException) {
                    this.jrtFileSystem = null;
                    break label19;
                }

                this.jrtFileSystem = null;
            } else {
                this.jrtFileSystem = null;
            }

            this.moduleDirsByPackage = null;
            this.jrtAvailable = false;
            this.ownsJrtFileSystem = false;
            this.runtimeClassesFound = false;
        }
    }

    public static FileSystem getDefaultJrtFileSystem() {
        FileSystem fileSystem = null;

        try {
            fileSystem = FileSystems.getFileSystem(URI.create("jrt:/"));
        } catch (ProviderNotFoundException providerNotFoundException) {
        }

        return fileSystem;
    }

    public static String filterExistingPaths(String string) {
        StringBuffer stringBuffer = new StringBuffer();
        StringTokenizer stringTokenizer = new StringTokenizer(string, SystemEnvironmentConstants.PATH_SEPARATOR_String);

        while (stringTokenizer.hasMoreTokens()) {
            String string1 = stringTokenizer.nextToken();
            if (new File(string1).exists()) {
                if (stringBuffer.length() > 0) {
                    stringBuffer.append(SystemEnvironmentConstants.PATH_SEPARATOR_String);
                }

                stringBuffer.append(string1);
            }
        }

        return stringBuffer.toString();
    }

    public String prependClasspath(String string) throws ZkmException, IOException {
        String string1 = this.classpath;
        StringBuilder stringBuilder = new StringBuilder(expandClasspath(string));
        if (stringBuilder.length() > 0 && stringBuilder.charAt(stringBuilder.length() - 1) != SystemEnvironmentConstants.PATH_SEPARATOR_CHAR) {
            stringBuilder.append(SystemEnvironmentConstants.PATH_SEPARATOR_CHAR);
        }

        stringBuilder.append(this.classpath);
        this.classpath = stringBuilder.toString();
        this.setChanged();
        this.notifyObservers(this.classpath, null, null);
        return string1;
    }

    public static boolean isRuntimeArchive(File file1) {
        return file1.exists() && ClassPathResolver.archiveContainsClass(file1.getAbsolutePath());
    }

    public boolean indexJrtPackages() {
        Path path = this.jrtFileSystem.getPath("packages", new String[0]);
        this.moduleDirsByPackage = new SetMultiMap();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
            for (Path path2 : directoryStream) {
                for (Path path3 : Files.newDirectoryStream(path2)) {
                    String string = path2.getFileName().toString().replace('.', '/');
                    String string2 = "/modules/" + path3.getFileName().toString();
                    this.moduleDirsByPackage.addValue(string, string2);
                }
            }
        } catch (IOException | DirectoryIteratorException exception) {
            return false;
        }
        return true;
    }

    public void setClassLoader(ClasspathClassLoader classpathClassLoader1) {
        this.classLoader = classpathClassLoader1;
    }

    public boolean isRuntimeClassesFound() {
        return this.runtimeClassesFound;
    }

    public Integer getReleaseVersion() {
        return this.releaseVersion;
    }

    public String setClasspath(String string) throws ZkmException, IOException {
        String string1 = this.classpath;
        this.classpath = expandClasspath(string);
        this.setChanged();
        this.notifyObservers(this.classpath, null, null);
        return string1;
    }

    public static boolean hasManifestDerivedPath(Set set1) {
        if (set1.size() > 0) {
            Iterator iterator = set1.iterator();

            while (iterator.hasNext()) {
                if (((String) iterator.next()).endsWith(ClassPathResolver.MISSING_ENTRY_SUFFIX)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean validateClasspath(
            String string, File file1, ObservableHolder observableHolder, MessageReporter messageReporter1, ObservableHolder observableHolder1
    ) throws ZkmException, IOException {
        HashSet hashSet = ZkmUtils.createHashSet();
        List list1 = ClassPathResolver.parseClasspath(string, hashSet, file1, messageReporter1);
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < list1.size(); i++) {
            Object object = list1.get(i);
            boolean bl = false;
            if (object instanceof File) {
                stringBuilder.append(((File) list1.get(i)).getAbsolutePath());
                bl = true;
            } else {
                ArchiveZipFile archiveZipFile = (ArchiveZipFile) list1.get(i);
                if (!TempFileManager.isZkmTempFilePath(archiveZipFile.getName())) {
                    stringBuilder.append(archiveZipFile.getName());
                    bl = true;
                }
            }

            if (i + 1 < list1.size() && bl) {
                stringBuilder.append(SystemEnvironmentConstants.PATH_SEPARATOR_String);
            }
        }

        for (int i = 0; i < list1.size(); i++) {
            Object object1 = list1.get(i);
            if (object1 instanceof ArchiveZipFile) {
                try {
                    com.zelix.klassmaster.util.ZkmUtils.<java.io.IOException>mayThrow();
                    ((ArchiveZipFile) list1.get(i)).close();
                } catch (IOException iOException) {
                }
            }
        }

        observableHolder.setValue(stringBuilder.toString());
        if (hashSet.size() > 0) {
            observableHolder1.setValue(describeInvalidPaths(hashSet));
            return false;
        } else {
            return true;
        }
    }

    public static String describeInvalidPaths(Set set1) {
        StringBuilder stringBuilder = new StringBuilder();
        int ba = set1.size();
        int bb = 0;

        for (Iterator iterator = set1.iterator(); iterator.hasNext(); bb++) {
            String string = (String) iterator.next();
            stringBuilder.append("\"");
            stringBuilder.append(string);
            stringBuilder.append("\"");
            if (bb + 2 < ba) {
                stringBuilder.append(", ");
            } else if (bb + 1 < ba) {
                stringBuilder.append(" and ");
            }
        }

        if (set1.size() > 1) {
            stringBuilder.append(" are not valid paths.");
        } else {
            stringBuilder.append(" is not a valid path.");
        }

        if (hasManifestDerivedPath(set1)) {
            stringBuilder.append(" Any path marked with '");
            stringBuilder.append(ClassPathResolver.MISSING_ENTRY_SUFFIX);
            stringBuilder.append("' was derived from the \"Class-Path\" attribute contained in the manifest of one or more JARs.");
        }

        return stringBuilder.toString();
    }

    public String getClasspathDescription() {
        return describeClasspath(this.classpath);
    }

    public ClasspathClassLoader getClassLoader() {
        return this.classLoader;
    }

    public boolean isJrtAvailable() {
        return this.jrtAvailable;
    }

    public ZkmClasspath(String string, Integer integer) throws ZkmException, IOException {
        this(string, integer, ZkmUtils.createHashSet());
    }

    public boolean locateRuntimeClasses(ObservableHolder observableHolder, ObservableHolder observableHolder1) throws ZkmException, IOException {
        this.runtimeClassesFound = false;
        this.jrtAvailable = false;
        boolean bl = false;

        try {
            ClasspathClassLoader classpathClassLoader1 = this.classLoader;
            Integer integer = this.releaseVersion;
            Boolean boolean1 = false;
            classpathClassLoader1.loadVersionedClassFile(integer, (String) null, boolean1);
            bl = true;
        } catch (ClassFileLoadException classFileLoadException) {
        }

        if (!bl && JavaRuntimeVersion.isRuntimeAtLeastJava8()) {
            String string = this.findJavaHome();
            if (string != null) {
                this.jrtFileSystem = openJrtFileSystem(string);
                if (this.jrtFileSystem != null) {
                    bl = this.indexJrtPackages();
                    this.jrtAvailable = bl;
                    this.ownsJrtFileSystem = true;
                    if (this.jrtAvailable) {
                        observableHolder.setValue(string + SystemEnvironmentConstants.FILE_SEPARATOR + JRT_FS_JAR_PATH);
                    }
                }
            }
        }

        if (!bl) {
            String string1 = findRuntimeLibrary();
            if (string1 != null) {
                this.appendClasspath(string1);
                observableHolder1.setValue(string1);
                bl = true;
            }
        }

        if (!bl && JavaRuntimeVersion.isRuntimeAtLeastJava9()) {
            this.jrtFileSystem = getDefaultJrtFileSystem();
            if (this.jrtFileSystem != null) {
                bl = this.indexJrtPackages();
                this.jrtAvailable = bl;
                this.ownsJrtFileSystem = false;
                if (this.jrtAvailable) {
                    observableHolder1.setValue("jrt:/");
                }
            }
        }

        this.runtimeClassesFound = bl;
        return bl;
    }

    public String findJavaHome() {
        String string = null;
        String string1 = JRT_FS_JAR_PATH;

        for (String string2 : this.classpath.split(SystemEnvironmentConstants.PATH_SEPARATOR_String)) {
            if (ZkmFileUtils.caseSensitiveFileSystem ? string2.endsWith(string1) : string2.toLowerCase().endsWith(string1)) {
                File file1 = new File(string2);
                if (file1.exists() && !file1.isDirectory()) {
                    File file2 = file1.getParentFile().getParentFile();
                    if (file2 != null) {
                        string = file2.getAbsolutePath();
                        break;
                    }
                }
            }
        }

        return string;
    }

    public static boolean isRuntimeArchivePath(String string) {
        return isRuntimeArchive(new File(string));
    }

    public static String expandClasspath(String string, Set set1) throws ZkmException, IOException {
        List list1 = ClassPathResolver.parseClasspath(string, set1);
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < list1.size(); i++) {
            Object object = list1.get(i);
            boolean bl = false;
            if (object instanceof File) {
                stringBuilder.append(((File) list1.get(i)).getAbsolutePath());
                bl = true;
            } else {
                label43:
                {
                    ArchiveZipFile archiveZipFile = (ArchiveZipFile) list1.get(i);
                    StringBuilder stringBuilder1;
                    String string1;
                    if (!HiddenOptionFlags.ALLOW_TEMP_FILES_IN_CLASSPATH) {
                        if (TempFileManager.isZkmTempFilePath(archiveZipFile.getName())) {
                            break label43;
                        }

                        stringBuilder1 = stringBuilder;
                        string1 = archiveZipFile.getName();
                    } else {
                        stringBuilder1 = stringBuilder;
                        string1 = archiveZipFile.getName();
                    }

                    stringBuilder1.append(string1);
                    bl = true;
                }
            }

            if (i + 1 < list1.size() && bl) {
                stringBuilder.append(SystemEnvironmentConstants.PATH_SEPARATOR_CHAR);
            }
        }

        for (int i = 0; i < list1.size(); i++) {
            Object object1 = list1.get(i);
            if (object1 instanceof ArchiveZipFile) {
                try {
                    com.zelix.klassmaster.util.ZkmUtils.<java.io.IOException>mayThrow();
                    ((ArchiveZipFile) list1.get(i)).close();
                } catch (IOException iOException) {
                }
            }
        }

        return stringBuilder.toString();
    }

    public static String expandClasspath(String string) throws ZkmException, IOException {
        return expandClasspath(string, ZkmUtils.createHashSet());
    }

    public ZkmClasspath(String string, Integer integer, Set set1) throws ZkmException, IOException {
        this.classpath = expandClasspath(string, set1);
        this.releaseVersion = integer;
    }

    public String appendClasspath(String string) throws ZkmException, IOException {
        String string1 = this.classpath;
        StringBuilder stringBuilder = new StringBuilder(this.classpath);
        if (stringBuilder.length() > 0 && stringBuilder.charAt(stringBuilder.length() - 1) != SystemEnvironmentConstants.PATH_SEPARATOR_CHAR) {
            stringBuilder.append(SystemEnvironmentConstants.PATH_SEPARATOR_CHAR);
        }

        stringBuilder.append(expandClasspath(string));
        this.classpath = stringBuilder.toString();
        this.setChanged();
        this.notifyObservers(this.classpath, null, null);
        return string1;
    }
}
