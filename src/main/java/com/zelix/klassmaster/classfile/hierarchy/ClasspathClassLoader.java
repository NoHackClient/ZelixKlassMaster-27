package com.zelix.klassmaster.classfile.hierarchy;

import com.zelix.klassmaster.archive.InputFileLocation;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.config.IgnoreMissingReferencesSpec;
import com.zelix.klassmaster.exceptions.ClassFileLoadException;
import com.zelix.klassmaster.exceptions.ZkmClassNotFoundException;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ClasspathClassLoader {
    public List versionedReaderCaches;
    private final ClassPathReaderCache readerCache;
    public final boolean caseSensitive;

    public void resetReaderCaches() throws ZkmException, IOException {
        ClassPathReaderCache.resetCache(this.readerCache);
        if (this.versionedReaderCaches != null) {
            Iterator iterator = this.versionedReaderCaches.iterator();

            while (iterator.hasNext()) {
                ClassPathReaderCache.resetCache((ClassPathReaderCache) iterator.next());
            }
        }
    }

    public ClasspathClassFile loadClassFile(String string, Integer integer, boolean bl, String string1, IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1) throws ZkmException, IOException {
        return this.loadClassFile(string, integer, bl, string1, false, ignoreMissingReferencesSpec1);
    }

    public ClasspathClassFile tryLoadClassFile(String string, boolean bl) throws ZkmException, IOException {
        try {
            Boolean boolean1 = bl;
            return this.loadClassFile(string, false, (String) null, boolean1);
        } catch (ClassFileLoadException classFileLoadException) {
            return null;
        }
    }

    public static ClasspathClassFile readClassFile(InputFileLocation inputFileLocation) throws ClassFileLoadException {
        String string = inputFileLocation.getName();
        int ba = string.lastIndexOf(".class");
        if (ba == -1) {
            throw new ClassFileLoadException("\"" + string + "\" doesn't end with \".class\"");
        }

        String string1 = string.substring(0, ba);

        try {
            MutableInt mutableInt = new MutableInt(0);
            InputStream inputStream1 = inputFileLocation.openInputStream(mutableInt);
            if (inputStream1 != null) {
                ClassFileInputStream classFileInputStream = ClassFileInputStream.fromStream(inputStream1, mutableInt.getValue());
                return new ClasspathClassFile(classFileInputStream, inputFileLocation);
            } else {
                throw new ZkmClassNotFoundException(
                        string1, "Class '" + ZkmUtils.slashesToDots(string1) + "' not found. Check the classpath option and reopen classes. (3a)"
                );
            }
        } catch (IOException iOException) {
            throw new ClassFileLoadException(string1, "'" + string1 + "' : File error (D) : '" + iOException + "'");
        } catch (ZkmClassNotFoundException zkmClassNotFoundException) {
            throw zkmClassNotFoundException;
        } catch (ZkmProcessingException zkmProcessingException) {
            throw new ClassFileLoadException(zkmProcessingException.getMessage());
        }
    }

    public ClasspathClassFile loadClassFile(String string, boolean bl, String string1, boolean bl1) throws ZkmException, IOException {
        return this.loadClassFile(string, null, bl, string1, bl1, null);
    }

    public ClasspathClassFile loadClassFile(
            String string, Integer integer, boolean bl, String string1, boolean bl1, IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1
    ) throws ZkmException, IOException {
        if (string.startsWith("[")) {
            string = "java/lang/Object";
        }

        ClassPathReaderCache classPathReaderCache = this.readerCache;
        if (integer != null && this.versionedReaderCaches != null) {
            Iterator iterator = this.versionedReaderCaches.iterator();

            while (iterator.hasNext()) {
                ClassPathReaderCache classPathReaderCache1 = (ClassPathReaderCache) iterator.next();
                if (classPathReaderCache1.getClasspathRelease() <= integer) {
                    classPathReaderCache = classPathReaderCache1;
                }
            }
        }

        ClasspathClassFile classpathClassFile1 = (ClasspathClassFile) ClassPathReaderCache.getClassCache(classPathReaderCache).get(string);
        if (classpathClassFile1 != null) {
            return classpathClassFile1;
        }

        StringBuilder stringBuilder = new StringBuilder();
        if (string1 != null) {
            stringBuilder.append(" while ");
            stringBuilder.append(string1);
            if (integer != null) {
                stringBuilder.append(" (multi-release version ");
                stringBuilder.append(integer);
                stringBuilder.append(")");
            }
        }

        InputFileLocation inputFileLocation = ClassPathReaderCache.getResolver(classPathReaderCache).findClassFile(string, bl1);
        InputStream inputStream1 = null;
        int ba = 0;
        if (inputFileLocation == null) {
            if (ClassPathReaderCache.getClasspath(classPathReaderCache).isJrtAvailable()) {
                try {
                    ObservableHolder observableHolder = new ObservableHolder();
                    byte[] bb = ClassPathReaderCache.getClasspath(classPathReaderCache).readJrtClassBytes(string, observableHolder);
                    if (bb != null) {
                        inputFileLocation = new InputFileLocation((Path) observableHolder.getValue(), ClassPathReaderCache.getClasspath(classPathReaderCache));
                        inputStream1 = new ByteArrayInputStream(bb);
                        ba = bb.length;
                    }
                } catch (IOException iOException2) {
                    throw new ClassFileLoadException(string, "'" + string + "' : File error (A) : '" + iOException2 + "'");
                }
            }
        } else {
            MutableInt mutableInt = new MutableInt(0);

            try {
                inputStream1 = inputFileLocation.openInputStream(mutableInt);
                ba = mutableInt.getValue();
            } catch (IOException iOException1) {
                throw new ClassFileLoadException(string, "'" + string + "' : File error (B) : '" + iOException1 + "'");
            }
        }

        if (inputStream1 == null) {
            ObservableHolder observableHolder1 = new ObservableHolder();
            if (ignoreMissingReferencesSpec1 != null && ignoreMissingReferencesSpec1.isMissingClassIgnored(string, observableHolder1)) {
                ignoreMissingReferencesSpec1.getScriptEnvironment()
                        .logMessage(
                                "Class '"
                                        + ZkmUtils.slashesToDots(string)
                                        + "' could not be found but it has been specified as able to be ignored. : '"
                                        + (String) observableHolder1.getValue()
                                        + "'",
                                true
                        );
                return null;
            } else if (bl) {
                throw new ZkmClassNotFoundException(
                        string, "Class '" + ZkmUtils.slashesToDots(string) + "' not found" + stringBuilder + ". Check the classpath option and reopen classes. (1a)"
                );
            } else {
                return null;
            }
        } else {
            ClassFileInputStream classFileInputStream;
            try {
                classFileInputStream = ClassFileInputStream.fromStream(inputStream1, ba);
            } catch (IOException iOException) {
                throw new ClassFileLoadException(string, "'" + string + "' : File error (C) : " + iOException + "'");
            }

            synchronized (ClassPathReaderCache.getClassCache(classPathReaderCache)) {
                classpathClassFile1 = (ClasspathClassFile) ClassPathReaderCache.getClassCache(classPathReaderCache).get(string);
                if (classpathClassFile1 != null) {
                    return classpathClassFile1;
                }

                ClasspathClassFile classpathClassFile;
                try {
                    classpathClassFile = new ClasspathClassFile(classFileInputStream, inputFileLocation);
                    if (integer != null) {
                        classpathClassFile.setReleaseVersion(integer);
                    }
                } catch (ZkmProcessingException zkmProcessingException) {
                    throw new ClassFileLoadException(string, zkmProcessingException.getMessage());
                }

                if (!string.equals(classpathClassFile.getClassName())) {
                    String string2 = inputFileLocation.getName();
                    String string3 = inputFileLocation.getArchivePath();
                    if (string3 != null) {
                        throw new ClassFileLoadException(
                                string,
                                "File '"
                                        + string2
                                        + "' in '"
                                        + string3
                                        + "' contains the class '"
                                        + classpathClassFile.getDottedClassName()
                                        + "' rather than '"
                                        + ZkmUtils.slashesToDots(string)
                                        + "' as expected."
                        );
                    } else {
                        throw new ClassFileLoadException(
                                string,
                                "File '"
                                        + string2
                                        + "' contains the class '"
                                        + classpathClassFile.getDottedClassName()
                                        + "' rather than '"
                                        + ZkmUtils.slashesToDots(string)
                                        + "' as expected."
                        );
                    }
                } else {
                    ClassPathReaderCache.getClassCache(classPathReaderCache).put(string, classpathClassFile);
                    return classpathClassFile;
                }
            }
        }
    }

    public ClasspathClassFile readUncachedClassFile(String string) {
        InputFileLocation inputFileLocation = ClassPathReaderCache.getResolver(this.readerCache).findInOwnCodeSource(string);
        if (inputFileLocation != null) {
            try {
                return readClassFile(inputFileLocation);
            } catch (ClassFileLoadException classFileLoadException) {
            }
        }

        return null;
    }

    public ClasspathClassLoader(ZkmClasspath zkmClasspath, boolean caseSensitive) throws ZkmException, IOException {
        this.readerCache = new ClassPathReaderCache(zkmClasspath, caseSensitive);
        zkmClasspath.setClassLoader(this);
        this.caseSensitive = caseSensitive;
    }

    public ClasspathClassFile loadVersionedClassFile(Integer integer, String string, boolean bl) throws ZkmException, IOException {
        return this.loadClassFile("java/lang/Object", integer, true, string, bl, null);
    }

    public void addVersionedClasspath(ZkmClasspath zkmClasspath) throws ZkmException, IOException {
        if (this.versionedReaderCaches == null) {
            this.versionedReaderCaches = new ArrayList();
        }

        zkmClasspath.setClassLoader(this);
        ClassPathReaderCache classPathReaderCache = new ClassPathReaderCache(zkmClasspath, this.caseSensitive);
        this.versionedReaderCaches.add(classPathReaderCache);
    }

    public ClasspathClassFile findClassFile(String string) throws ZkmException, IOException {
        return this.findClassFile(string, false, (String) null);
    }

    public void putCachedClass(String string, ClasspathClassFile classpathClassFile) {
        ClassPathReaderCache.getClassCache(this.readerCache).put(string, classpathClassFile);
        if (this.versionedReaderCaches != null) {
            Iterator iterator = this.versionedReaderCaches.iterator();

            while (iterator.hasNext()) {
                ClassPathReaderCache.getClassCache((ClassPathReaderCache) iterator.next()).put(string, classpathClassFile);
            }
        }
    }

    public ClassPathResolver getClassPathResolver() {
        ClassPathReaderCache.setResolverRetained(this.readerCache);
        return ClassPathReaderCache.getResolver(this.readerCache);
    }

    public ClasspathClassFile getClassFile(String string) throws ZkmException, IOException {
        return this.findClassFile(string, true, (String) null);
    }

    public void releaseReaders() {
        this.readerCache.close();
        if (this.versionedReaderCaches != null) {
            Iterator iterator = this.versionedReaderCaches.iterator();

            while (iterator.hasNext()) {
                ((ClassPathReaderCache) iterator.next()).close();
            }
        }
    }

    public ClasspathClassFile findClassFile(String string, boolean bl, String string1) throws ZkmException, IOException {
        return this.loadClassFile(string, bl, string1, false);
    }

    public ClasspathClassFile findClassFileQuietly(String string) throws ZkmException, IOException {
        try {
            return this.findClassFile(string, false, (String) null);
        } catch (ClassFileLoadException classFileLoadException) {
            return null;
        }
    }

    public void removeCachedClass(String string) {
        ClassPathReaderCache.getClassCache(this.readerCache).remove(string);
        if (this.versionedReaderCaches != null) {
            Iterator iterator = this.versionedReaderCaches.iterator();

            while (iterator.hasNext()) {
                ClassPathReaderCache.getClassCache((ClassPathReaderCache) iterator.next()).remove(string);
            }
        }
    }
}
