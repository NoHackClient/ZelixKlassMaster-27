package com.zelix.klassmaster.xml;

import com.zelix.klassmaster.archive.ArchiveZipFile;
import com.zelix.klassmaster.archive.SourceArchive;
import com.zelix.klassmaster.archive.ZkmFileUtils;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.log.MessageReporter;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.SetMultiMap;
import com.zelix.klassmaster.util.ThreeKeySetMultiMap;
import com.zelix.klassmaster.util.TwoKeyMap;
import com.zelix.klassmaster.util.ZkmStringUtils;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;

public class ResourceFileIndex {
    public ListMultimap archiveEntries = new ListMultimap();
    public Map standaloneFiles = ZkmUtils.createHashMap();
    public final ThreeKeySetMultiMap defaultMethodClasses = new ThreeKeySetMultiMap();
    public TwoKeyMap beanClassesByFile = new TwoKeyMap();
    public TwoKeyMap beanParentsByFile = new TwoKeyMap();
    public ListMultimap importersByFile = new ListMultimap();
    public ListMultimap importsByFile = new ListMultimap();

    public boolean matchesPathPattern(String string, String string1, String string2) {
        String string3 = string1.replace('/', ZkmFileUtils.FILE_SEPARATOR_CHAR);
        StringBuilder stringBuilder = new StringBuilder();
        if (string != null && string.length() != 0) {
            stringBuilder.append(string.replace('/', ZkmFileUtils.FILE_SEPARATOR_CHAR));
            if (stringBuilder.charAt(stringBuilder.length() - 1) != ZkmFileUtils.FILE_SEPARATOR_CHAR
                    && string3.charAt(string3.length() - 1) != ZkmFileUtils.FILE_SEPARATOR_CHAR) {
                stringBuilder.append(ZkmFileUtils.FILE_SEPARATOR_CHAR);
            }

            stringBuilder.append(string3);
        } else {
            stringBuilder.append(string3);
        }

        String string4 = "*" + string2.replace('/', ZkmFileUtils.FILE_SEPARATOR_CHAR);
        return ZkmStringUtils.matchesWildcard(stringBuilder.toString(), string4);
    }

    public void addFile(File file1) {
        this.standaloneFiles.put(file1.getAbsolutePath(), file1);
    }

    public List findMatchingPaths(String string, String string1) {
        ArrayList arrayList = new ArrayList();
        Enumeration enumeration = this.archiveEntries.keys();

        while (enumeration.hasMoreElements()) {
            String string2 = (String) enumeration.nextElement();
            if (this.matchesPathPattern(string1, string2, string)) {
                arrayList.add(string2);
            }
        }

        Iterator iterator = this.standaloneFiles.entrySet().iterator();

        while (iterator.hasNext()) {
            String string3 = (String) ((Entry) iterator.next()).getKey();
            if (this.matchesPathPattern(string1, string3, string)) {
                arrayList.add(string3);
            }
        }

        return arrayList;
    }

    public void addImport(String string, String string1) {
        this.importersByFile.addValue(string, string1);
        this.importsByFile.addValue(string1, string);
    }

    public void clear() {
        this.archiveEntries.clear();
        this.standaloneFiles.clear();
        this.defaultMethodClasses.clear();
        this.beanClassesByFile.clear();
        this.beanParentsByFile.clear();
        this.importersByFile.clear();
        this.importsByFile.clear();
    }

    public void addArchiveEntry(Object object, Object object1) {
        this.archiveEntries.addValue(object, object1);
    }

    public Map getBeanParents(Object object) {
        return this.beanParentsByFile.getInnerMap(object);
    }

    public List loadMatchingConfigFiles(String string, String string1, XmlConfigFileType xmlConfigFileType, MessageReporter messageReporter1) throws ZkmException, IOException {
        List list1 = this.findMatchingPaths(string, string1);
        if (list1.size() == 0 && string1 != null) {
            list1 = this.findMatchingPaths(string, (String) null);
        }

        ArrayList arrayList = new ArrayList();
        Iterator iterator = list1.iterator();

        while (iterator.hasNext()) {
            String string2 = (String) iterator.next();
            if (this.archiveEntries.containsKey(string2)) {
                List list2 = this.archiveEntries.getValues(string2);
                Iterator iterator1 = list2.iterator();

                while (iterator1.hasNext()) {
                    SourceArchive sourceArchive1 = (SourceArchive) iterator1.next();
                    String string4 = sourceArchive1.getFilePath();

                    ArchiveZipFile archiveZipFile;
                    try {
                        archiveZipFile = new ArchiveZipFile(string4);
                    } catch (IOException iOException2) {
                        messageReporter1.reportError(
                                "ERROR:", "Could not open '" + string4 + "' as an archive file : " + sourceArchive1.getOriginalPath() + " : " + iOException2.getMessage()
                        );
                        continue;
                    }

                    ZipEntry zipEntry1 = archiveZipFile.getEntry(string2);
                    ObservableHolder observableHolder = new ObservableHolder();
                    MutableInt mutableInt = new MutableInt(0);
                    MutableInt mutableInt1 = new MutableInt(0);
                    MutableInt mutableInt2 = new MutableInt(Integer.MAX_VALUE);

                    String string3;
                    try {
                        string3 = XmlResourceProcessor.readZipEntryContent(archiveZipFile, zipEntry1, observableHolder, mutableInt, mutableInt1, mutableInt2);
                    } catch (IOException iOException1) {
                        messageReporter1.reportError(
                                "ERROR:",
                                "Could not get contents of  '"
                                        + zipEntry1.getName()
                                        + "' in '"
                                        + string4
                                        + "' : "
                                        + sourceArchive1.getOriginalPath()
                                        + " : "
                                        + iOException1.getMessage()
                        );
                        continue;
                    }

                    XmlConfigFileType xmlConfigFileType1 = XmlConfigFileHandler.detectConfigFileType(string2, string3);
                    if (xmlConfigFileType1 == xmlConfigFileType) {
                        arrayList.add(
                                new TextResourceContent(
                                        observableHolder, mutableInt, mutableInt1, mutableInt2, ZkmFileUtils.describeEntryWithOriginal(archiveZipFile, zipEntry1), string3
                                )
                        );
                    }
                }
            }

            if (this.standaloneFiles.containsKey(string2)) {
                ObservableHolder observableHolder1 = new ObservableHolder();
                MutableInt mutableInt3 = new MutableInt(0);
                MutableInt mutableInt4 = new MutableInt(0);
                MutableInt mutableInt5 = new MutableInt(Integer.MAX_VALUE);

                try {
                    String string5 = XmlResourceProcessor.readFileContent(
                            (File) this.standaloneFiles.get(string2), observableHolder1, mutableInt3, mutableInt4, mutableInt5
                    );
                    XmlConfigFileType xmlConfigFileType2 = XmlConfigFileHandler.detectConfigFileType(string2, string5);
                    if (xmlConfigFileType2 == xmlConfigFileType) {
                        arrayList.add(new TextResourceContent(observableHolder1, mutableInt3, mutableInt4, mutableInt5, string2, string5));
                    }
                } catch (IOException iOException) {
                    messageReporter1.reportError("ERROR:", "Could not get read contents of  '" + string2 + "' : " + iOException.getMessage());
                }
            }
        }

        return arrayList;
    }

    public void addBeanParents(String string, Map map1) {
        Iterator iterator = map1.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            String string1 = (String) this.beanParentsByFile.putValue(string, entry.getKey(), entry.getValue());
        }
    }

    public void addBeanClasses(String string, Map map1) {
        Iterator iterator = map1.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            ProgramClass programClass1 = (ProgramClass) this.beanClassesByFile.putValue(string, entry.getKey(), entry.getValue());
        }
    }

    public Map getBeanClasses(Object object) {
        return this.beanClassesByFile.getInnerMap(object);
    }

    public List getImportedFiles(Object object) {
        return this.importsByFile.getValues(object);
    }

    public void addDefaultMethodClass(String string, String string1, ProgramClass programClass1) {
        this.defaultMethodClasses.addValue(string, "beans", string1, programClass1);
    }

    public List getImportingFiles(Object object) {
        return this.importersByFile.getValues(object);
    }

    public SetMultiMap getDefaultMethodClasses(String string) {
        return this.defaultMethodClasses.getSetMultiMap(string, "beans");
    }
}
