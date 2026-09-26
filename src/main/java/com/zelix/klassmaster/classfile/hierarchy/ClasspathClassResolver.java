package com.zelix.klassmaster.classfile.hierarchy;

import com.zelix.klassmaster.archive.InputFileLocation;
import com.zelix.klassmaster.changelog.TraceClassNotFoundException;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.exceptions.ClassLookupException;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.util.ArrayEnumeration;
import com.zelix.klassmaster.util.ListenerRegistry;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.NodeVisitor;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ObservableModel;
import com.zelix.klassmaster.util.ThreeKeyMultiMap;
import com.zelix.klassmaster.util.TwoKeyMap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.HashMap;

public class ClasspathClassResolver implements NodeVisitor {
    public static final Boolean CACHED_TRUE = Boolean.TRUE;
    public static final Boolean CACHED_FALSE = Boolean.FALSE;
    public ClassPathResolver classPathResolver;
    public HashMap classCache;
    public ListenerRegistry listenerRegistry = new ListenerRegistry();
    public ObservableHolder statusHolder = new ObservableHolder();
    public TwoKeyMap superclassCache = new TwoKeyMap();
    public TwoKeyMap interfaceCache = new TwoKeyMap();
    public ZkmClasspath classpath;
    public final boolean caseSensitive;

    public final boolean implementsInterface(String string, String string1) throws ZkmException, IOException {
        if (string.equals("java/lang/Object")) {
            return false;
        }

        Boolean boolean1 = (Boolean) this.interfaceCache.getValue(string1, string);
        if (boolean1 != null) {
            return boolean1;
        }

        ProgramClass programClass1 = this.loadProgramClass(string);
        ArrayEnumeration arrayEnumeration = new ArrayEnumeration(programClass1.getInterfaceNames());

        while (arrayEnumeration.hasMoreElements()) {
            String string2 = (String) arrayEnumeration.nextElement();
            if (string2.equals(string1)) {
                this.interfaceCache.putValue(string1, string, CACHED_TRUE);
                return true;
            }

            boolean bl = this.implementsInterface(string2, string1);
            if (bl) {
                this.interfaceCache.putValue(string1, string, CACHED_TRUE);
                return true;
            }
        }

        String string3 = programClass1.getSuperclassName();
        boolean bl1 = this.implementsInterface(string3, string1);
        this.interfaceCache.putValue(string1, string, bl1 ? CACHED_TRUE : CACHED_FALSE);
        return bl1;
    }

    public ClasspathClassResolver(ZkmClasspath zkmClasspath, boolean caseSensitive) throws ZkmException, IOException {
        this.classpath = zkmClasspath;
        this.caseSensitive = caseSensitive;
        this.classpath.addObserver(this);
        this.resetClassPathResolver();
    }

    @Override
    public void handleObservedChange(ObservableModel observableModel1, Object object, Object object1, Object object2) throws ZkmException, IOException {
        this.resetClassPathResolver();
    }

    public final boolean isSubclassOf(String string, String string1) throws ZkmException, IOException {
        if (string.equals("java/lang/Object")) {
            return false;
        } else {
            Boolean boolean1 = (Boolean) this.superclassCache.getValue(string1, string);
            if (boolean1 != null) {
                return boolean1;
            } else {
                String string2 = this.loadProgramClass(string).getSuperclassName();
                if (string2.equals(string1)) {
                    this.superclassCache.putValue(string1, string, CACHED_TRUE);
                    return true;
                } else if (string2.equals("java/lang/Object")) {
                    this.superclassCache.putValue(string1, string, CACHED_FALSE);
                    return false;
                } else {
                    boolean bl = this.isSubclassOf(string2, string1);
                    this.superclassCache.putValue(string1, string, bl ? CACHED_TRUE : CACHED_FALSE);
                    return bl;
                }
            }
        }
    }

    public void clearCaches() {
        if (this.classCache != null) {
            this.classCache.clear();
        }

        this.superclassCache.clear();
        this.interfaceCache.clear();
        this.listenerRegistry.removeAllObservers();
        if (this.classPathResolver != null) {
            this.classPathResolver.close();
        }

        if (this.classpath != null) {
            this.classpath.closeJrtFileSystem();
        }
    }

    public void resetClassPathResolver() throws ZkmException, IOException {
        if (this.classPathResolver != null) {
            this.classPathResolver.close();
        }

        this.classPathResolver = new ClassPathResolver(this.classpath.getClasspath());
        this.classCache = ZkmUtils.createHashMap();
    }

    public ProgramClass loadProgramClass(String string) throws ZkmException, IOException {
        String string1 = string;
        HashMap hashMap;
        if (string1.startsWith("[")) {
            string1 = "java/lang/Object";
            hashMap = this.classCache;
        } else {
            hashMap = this.classCache;
        }

        ProgramClass programClass1 = (ProgramClass) hashMap.get(string1);
        if (programClass1 != null) {
            return programClass1;
        }

        InputFileLocation inputFileLocation = this.classPathResolver.findClassFile(string1, false);
        int ba;
        InputStream inputStream1;
        if (inputFileLocation == null) {
            if (!this.classpath.isJrtAvailable()) {
                throw new TraceClassNotFoundException(
                        string1, "Class '" + ZkmUtils.slashesToDots(string1) + "' not found. Check the classpath option and reopen classes. (4)"
                );
            }

            try {
                ObservableHolder observableHolder = new ObservableHolder();
                byte[] bb = this.classpath.readJrtClassBytes(string1, observableHolder);
                if (bb == null) {
                    throw new TraceClassNotFoundException(
                            string1, "Class '" + ZkmUtils.slashesToDots(string1) + "' not found. Check the classpath option and reopen classes. (3)"
                    );
                }

                inputFileLocation = new InputFileLocation((Path) observableHolder.getValue(), this.classpath);
                inputStream1 = new ByteArrayInputStream(bb);
                ba = bb.length;
            } catch (IOException iOException2) {
                throw new TraceClassNotFoundException(
                        string1, "Class '" + ZkmUtils.slashesToDots(string1) + "' not found. Check the classpath option and reopen classes. (1)"
                );
            }
        } else {
            MutableInt mutableInt = new MutableInt(0);

            try {
                inputStream1 = inputFileLocation.openInputStream(mutableInt);
                ba = mutableInt.getValue();
            } catch (IOException iOException1) {
                throw new TraceClassNotFoundException(string1, "'" + string1 + "' : File error (E) : '" + iOException1 + "'");
            }
        }

        try {
            if (inputStream1 != null) {
                ClassFileInputStream classFileInputStream = ClassFileInputStream.fromStream(inputStream1, ba);
                StringWriter stringWriter1 = new StringWriter();
                PrintWriter printWriter = new PrintWriter(stringWriter1);
                StringWriter stringWriter = new StringWriter();
                new PrintWriter(stringWriter);
                ThreeKeyMultiMap threeKeyMultiMap = new ThreeKeyMultiMap(4, 3, 5, false);

                try {
                    programClass1 = new ProgramClass(
                            classFileInputStream, inputFileLocation, this.statusHolder, this.listenerRegistry, printWriter, threeKeyMultiMap
                    );
                } catch (ZkmProcessingException zkmProcessingException) {
                    throw new ClassLookupException(zkmProcessingException.getMessage());
                }

                if (!string1.equals(programClass1.getClassName())) {
                    String string2 = inputFileLocation.getName();
                    String string3 = inputFileLocation.getArchivePath();
                    if (string3 != null) {
                        throw new ClassLookupException(
                                "File '"
                                        + string2
                                        + "' in '"
                                        + string3
                                        + "' contains the class '"
                                        + programClass1.getDottedClassName()
                                        + "' rather than '"
                                        + ZkmUtils.slashesToDots(string1)
                                        + "' as expected."
                        );
                    } else {
                        throw new ClassLookupException(
                                "File '"
                                        + string2
                                        + "' contains the class '"
                                        + programClass1.getDottedClassName()
                                        + "' rather than '"
                                        + ZkmUtils.slashesToDots(string1)
                                        + "' as expected."
                        );
                    }
                } else {
                    this.classCache.put(string1, programClass1);
                    return programClass1;
                }
            } else {
                throw new ClassLookupException("Class '" + ZkmUtils.slashesToDots(string1) + "' not found. Check the classpath option and reopen classes. (2)");
            }
        } catch (IOException iOException) {
            throw new ClassLookupException("'" + ZkmUtils.slashesToDots(string1) + "' : File error (F) : " + iOException);
        }
    }
}
