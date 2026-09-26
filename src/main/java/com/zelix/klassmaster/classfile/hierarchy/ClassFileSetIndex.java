package com.zelix.klassmaster.classfile.hierarchy;

import com.zelix.klassmaster.archive.ArchiveZipFile;
import com.zelix.klassmaster.archive.ClassFileNameFilter;
import com.zelix.klassmaster.archive.InputFileLocation;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.obfuscator.rename.RootPackageNode;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.SystemEnvironmentConstants;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;

public class ClassFileSetIndex {
    public ClasspathClassFile[] classFiles;
    public Map classesByName;
    public ListMultimap classesByMethodKey;

    public synchronized List findSubclasses(Object object) {
        ArrayList arrayList = null;
        int ba = 0;
        int bb = 0;

        for (ClasspathClassFile[] classpathClassFiles = this.classFiles; bb < classpathClassFiles.length; classpathClassFiles = this.classFiles) {
            ClassFileBase classFileBase = this.classFiles[ba];

            do {
                String string = classFileBase.getSuperclassName();
                if (string.equals(object)) {
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                    }

                    arrayList.add(this.classFiles[ba]);
                    break;
                }

                if (string.equals("java/lang/Object")) {
                    break;
                }

                classFileBase = (ClassFileBase) this.classesByName.get(string);
            } while (classFileBase != null);

            bb = ++ba;
        }

        return arrayList;
    }

    public List getClassesDeclaringMethod(Object object) {
        List list1 = this.classesByMethodKey.getValues(object);
        return list1 == null ? null : new ArrayList(list1);
    }

    public ClassFileSetIndex(File[] files) throws ZkmProcessingException {
        ArrayList arrayList = new ArrayList(Math.max(files.length * 5, 10));

        for (int i = 0; i < files.length; i++) {
            if (files[i].exists() && files[i].isDirectory()) {
                String string1 = files[i].getAbsolutePath();
                String[] strings = files[i].list(new ClassFileNameFilter());
                if (strings != null) {
                    for (int j = 0; j < strings.length; j++) {
                        String string = string1
                                + (string1.endsWith(SystemEnvironmentConstants.FILE_SEPARATOR) ? "" : SystemEnvironmentConstants.FILE_SEPARATOR)
                                + strings[j];
                        arrayList.add(new InputFileLocation(string));
                    }
                }
            } else {
                try {
                    ArchiveZipFile archiveZipFile = new ArchiveZipFile(files[i]);
                    Enumeration<? extends ZipEntry> enumeration = archiveZipFile.entries();

                    while (enumeration.hasMoreElements()) {
                        ZipEntry zipEntry1 = (ZipEntry) enumeration.nextElement();
                        if (!zipEntry1.isDirectory() && zipEntry1.getName().endsWith(".class")) {
                            arrayList.add(new InputFileLocation(archiveZipFile, zipEntry1));
                        }
                    }
                } catch (IOException iOException) {
                    throw new ZkmProcessingException(iOException.toString() + " while reading " + files[i]);
                }
            }
        }

        this.classFiles = new ClasspathClassFile[arrayList.size()];
        this.classesByName = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(arrayList.size()));
        this.classesByMethodKey = new ListMultimap();

        for (int i = 0; i < arrayList.size(); i++) {
            InputFileLocation inputFileLocation = (InputFileLocation) arrayList.get(i);
            ClasspathClassFile classpathClassFile = ClasspathClassLoader.readClassFile(inputFileLocation);
            this.classFiles[i] = classpathClassFile;
            this.classesByName.put(classpathClassFile.getClassName(), classpathClassFile);
            LibraryMethod[] libraryMethods = classpathClassFile.getMethods();

            for (int j = 0; j < libraryMethods.length; j++) {
                this.classesByMethodKey.addValue(libraryMethods[j].getSourceNameAndDescriptor(), classpathClassFile);
            }
        }

        for (int i = 0; i < arrayList.size(); i++) {
            InputFileLocation inputFileLocation1 = (InputFileLocation) arrayList.get(i);
            inputFileLocation1.releaseResources();
        }
    }

    public RootPackageNode buildPackageTree() {
        RootPackageNode rootPackageNode1 = new RootPackageNode();
        rootPackageNode1.buildFromClasses(this.classFiles);
        return rootPackageNode1;
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
