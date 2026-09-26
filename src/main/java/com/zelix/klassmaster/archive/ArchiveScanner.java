package com.zelix.klassmaster.archive;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.log.MessageReporter;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ZkmStringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;

public class ArchiveScanner {
    public static boolean isSkippedPath(String string, List list1, List list2, ObservableHolder observableHolder, ObservableHolder observableHolder1) throws ZkmException, IOException {
        observableHolder.setValue(null);
        String string10;
        char bd;
        if (ZkmFileUtils.caseSensitiveFileSystem) {
            String string1 = string;
            string10 = string1;
            bd = '\\';
        } else {
            String string8 = string.toUpperCase();
            string10 = string8;
            bd = '\\';
        }

        String string9 = string10.replace(bd, '/');
        int ba = lastSeparatorIndex(string9);
        String string2 = string9.substring(ba + 1);
        String string3 = string9.substring(0, ba + 1);
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < list1.size(); i++) {
            String string4 = ((String) list1.get(i)).trim();
            if (string4.length() > 0) {
                stringBuilder.setLength(0);
                stringBuilder.append("*");
                stringBuilder.append(string4);
                String string5 = stringBuilder.toString();
                if (!ZkmFileUtils.caseSensitiveFileSystem) {
                    string5 = string5.toUpperCase();
                }

                int bc = lastSeparatorIndex(string5);
                String string6 = string5.substring(bc + 1);
                String string7 = string5.substring(0, bc + 1);
                if (ZkmStringUtils.matchesWildcard(string3, string7) && ZkmStringUtils.matchesWildcard(string2, string6)) {
                    observableHolder.setValue(string4);
                    if (list2 != null && isUnskippedPath(string3, string2, string7, string6, list2, observableHolder1)) {
                        return false;
                    }

                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isUnskippedPath(String string, String string1, String string2, String string3, List list1, ObservableHolder observableHolder) throws ZkmException, IOException {
        observableHolder.setValue(null);
        int ba = ZkmStringUtils.countChar(string2, '!');
        String string6 = getExtension(string3);
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < list1.size(); i++) {
            String string7 = ((String) list1.get(i)).trim();
            if (string7.length() > 0 && ZkmStringUtils.countChar(string7, '!') == ba) {
                stringBuilder.setLength(0);
                stringBuilder.append("*");
                stringBuilder.append(string7);
                String string8 = stringBuilder.toString();
                if (!ZkmFileUtils.caseSensitiveFileSystem) {
                    string8 = string8.toUpperCase();
                }

                int bc = lastSeparatorIndex(string8);
                String string9 = string8.substring(bc + 1);
                String string10 = string8.substring(0, bc + 1);
                String string11 = getExtension(string9);
                if (!string11.equals(string6)) {
                    String string4 = string6;
                    String string5 = string11;
                    if (!ZkmStringUtils.matchesWildcard(string5, string4)) {
                        string4 = string11;
                        string5 = string6;
                        if (!ZkmStringUtils.matchesWildcard(string5, string4)) {
                            continue;
                        }
                    }
                }

                String string12 = string10;
                String string13 = string;
                if (ZkmStringUtils.matchesWildcard(string13, string12)) {
                    string12 = string9;
                    string13 = string1;
                    if (ZkmStringUtils.matchesWildcard(string13, string12)) {
                        observableHolder.setValue(string7);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static int lastSeparatorIndex(String string) {
        int ba = string.lastIndexOf(47);
        int bb = string.lastIndexOf("!");
        return Math.max(ba, bb);
    }

    public static void scanArchive(
            File file1,
            Set set1,
            Map map1,
            OpenPathEntry openPathEntry,
            boolean bl,
            List list1,
            List list2,
            Set set2,
            Set set3,
            Set set4,
            Set set5,
            Set set6,
            Set set7,
            Set set8,
            Set set9,
            String string,
            SourceArchive sourceArchive1,
            ZipOutputTarget zipOutputTarget,
            Set set10,
            MessageReporter messageReporter1
    ) throws ZkmException, IOException {
        String string1;
        if (sourceArchive1 != null) {
            string1 = sourceArchive1.getNestedEntryPath(string);
        } else {
            string1 = file1.getAbsolutePath();
        }

        Vector vector = new Vector();

        try {
            SourceArchive sourceArchive2 = (SourceArchive) map1.get(string1);
            if (sourceArchive2 == null) {
                if (sourceArchive1 != null) {
                    sourceArchive2 = new SourceArchive(file1.getAbsolutePath(), file1.length(), string, sourceArchive1);
                } else {
                    sourceArchive2 = new SourceArchive(
                            file1.getAbsolutePath(), file1.length(), string, openPathEntry.getExpectedInitialSha256(), openPathEntry.getExpectedFinalSha256()
                    );
                }

                map1.put(string1, sourceArchive2);
            }

            ArchivePathFilter archivePathFilter;
            if (openPathEntry != null) {
                archivePathFilter = openPathEntry.getFilter();
            } else {
                archivePathFilter = ArchivePathFilter.ACCEPT_ALL;
            }

            if (!list1.isEmpty() && sourceArchive1 != null) {
                if (!matchesFilter(string1, archivePathFilter)) {
                    messageReporter1.reportInfo(
                            "INFO:",
                            "Filtering out nested archive '"
                                    + string1
                                    + "' because it does not match specified filter '"
                                    + archivePathFilter.toFilterExpression()
                                    + "' (C)"
                    );
                    return;
                }

                ObservableHolder observableHolder = new ObservableHolder();
                ObservableHolder observableHolder3 = new ObservableHolder();
                boolean bl1 = isSkippedPath(string1, list1, list2, observableHolder, observableHolder3);
                if (!observableHolder.isValueNull()) {
                    String string2 = (String) observableHolder.getValue();
                    messageReporter1.reportInfo("INFO:", "Skipping nested archive '" + string1 + "' because of parameter -\"" + string2 + "\"");
                    set2.add(string2);
                }

                if (!observableHolder3.isValueNull()) {
                    String string6 = (String) observableHolder3.getValue();
                    messageReporter1.reportInfo("INFO:", "Unskipping nested archive '" + string1 + "' because of parameter +\"" + string6 + "\"");
                    set3.add(string6);
                }

                if (bl1) {
                    set4.add(sourceArchive2);
                    return;
                }
            }

            boolean bl3 = false;
            ZipEntry zipEntry2 = null;
            ArchiveZipFile archiveZipFile = new ArchiveZipFile(file1);
            Enumeration<? extends ZipEntry> enumeration = archiveZipFile.entries();

            while (enumeration.hasMoreElements()) {
                ZipEntry zipEntry1 = (ZipEntry) enumeration.nextElement();
                if (!zipEntry1.isDirectory()) {
                    String string3 = zipEntry1.getName();
                    String string4 = (sourceArchive1 != null ? string1 : string) + "!" + string3;
                    if (matchesFilter(string4, archivePathFilter)) {
                        if (string3.endsWith(".class")) {
                            ObservableHolder observableHolder5 = new ObservableHolder();
                            ObservableHolder observableHolder7 = new ObservableHolder();
                            boolean bl6 = !list1.isEmpty() && isSkippedPath(string4, list1, list2, observableHolder5, observableHolder7);
                            if (!observableHolder5.isValueNull()) {
                                String string10 = (String) observableHolder5.getValue();
                                set2.add(string10);
                                messageReporter1.reportInfo("INFO:", "Skipping class file '" + string4 + "' because of parameter -\"" + string10 + "\"");
                            }

                            if (!observableHolder7.isValueNull()) {
                                String string11 = (String) observableHolder7.getValue();
                                messageReporter1.reportInfo("INFO:", "Unskipping class file '" + string4 + "' because of parameter +\"" + string11 + "\"");
                                set3.add(string11);
                            }

                            if (bl6) {
                                set5.add(new InputFileLocation(archiveZipFile, zipEntry1, sourceArchive2));
                                if (set10.add(zipEntry1.getName())) {
                                    try {
                                        ZipEntryWriter zipEntryWriter = new ZipEntryWriter(
                                                zipOutputTarget.getOrCreateZipOutputStream(),
                                                zipEntry1.getName(),
                                                false,
                                                archiveZipFile.getInputStream(zipEntry1),
                                                (int) zipEntry1.getSize()
                                        );
                                        zipEntryWriter.writeEntry();
                                    } catch (IOException iOException2) {
                                        StringBuffer stringBuffer1 = new StringBuffer();
                                        stringBuffer1.append("Could not write to \"" + zipOutputTarget.getTempFilePath() + "\" : ");
                                        stringBuffer1.append(iOException2.getMessage());
                                        stringBuffer1.append(" (1)");
                                        messageReporter1.reportFatalError("FILE ERROR:", stringBuffer1.toString());
                                    }
                                } else {
                                    messageReporter1.reportWarning(
                                            "WARNING:",
                                            "Skipped class file '"
                                                    + zipEntry1.getName()
                                                    + "' skipped in more than one archive. Class '"
                                                    + string4
                                                    + "' will not be automatically added to the effective classpath."
                                    );
                                }
                            } else {
                                InputFileLocation inputFileLocation = new InputFileLocation(archiveZipFile, zipEntry1, sourceArchive2);
                                set1.add(inputFileLocation);
                                bl3 = true;
                            }
                        } else if (ZkmFileUtils.isXmlFileName(string3)) {
                            ObservableHolder observableHolder4 = new ObservableHolder();
                            ObservableHolder observableHolder6 = new ObservableHolder();
                            boolean bl5 = !list1.isEmpty() && isSkippedPath(string4, list1, list2, observableHolder4, observableHolder6);
                            if (!observableHolder4.isValueNull()) {
                                String string8 = (String) observableHolder4.getValue();
                                MessageReporter messageReporter3;
                                String string13;
                                if (set2 != null) {
                                    set2.add(string8);
                                    messageReporter3 = messageReporter1;
                                    string13 = "INFO:";
                                } else {
                                    messageReporter3 = messageReporter1;
                                    string13 = "INFO:";
                                }

                                messageReporter3.reportInfo(string13, "Skipping file '" + string4 + "' because of parameter -\"" + string8 + "\"");
                            }

                            if (!observableHolder6.isValueNull()) {
                                String string9 = (String) observableHolder6.getValue();
                                messageReporter1.reportInfo("INFO:", "Unskipping file '" + string4 + "' because of parameter +\"" + string9 + "\"");
                                set3.add(string9);
                            }

                            if (bl5) {
                                set6.add(new InputFileLocation(archiveZipFile, zipEntry1, sourceArchive2));
                            }
                        } else if (!ZkmFileUtils.isYamlFileName(string3) && !ZkmFileUtils.isPropertiesFileName(string3)) {
                            boolean bl4 = false;

                            try {
                                bl4 = ZkmFileUtils.isNestedArchiveEntry(archiveZipFile, zipEntry1);
                            } catch (IOException iOException1) {
                                messageReporter1.reportError("FILE ERROR:", "File Error : " + iOException1);
                            }

                            if (bl4) {
                                File file2 = ZkmFileUtils.extractEntryToTempFile(archiveZipFile, zipEntry1);
                                if (bl) {
                                    vector.addElement(new PendingNestedArchive(sourceArchive2, string3, file2));
                                } else {
                                    SourceArchive sourceArchive3 = new SourceArchive(file2.getAbsolutePath(), file2.length(), string3, sourceArchive2);
                                    map1.put(sourceArchive2.getNestedEntryPath(string3), sourceArchive3);
                                    set4.add(sourceArchive3);
                                }
                            } else if (string3.equalsIgnoreCase("META-INF/MANIFEST.MF")) {
                                if (zipEntry2 == null) {
                                    zipEntry2 = zipEntry1;
                                } else {
                                    messageReporter1.reportError(
                                            "FILE ERROR:", "More than one MANIFEST in '" + string + "' : '" + zipEntry2.getName() + "' '" + zipEntry1.getName() + "'"
                                    );
                                }
                            }
                        } else {
                            ObservableHolder observableHolder1 = new ObservableHolder();
                            ObservableHolder observableHolder2 = new ObservableHolder();
                            boolean bl2 = !list1.isEmpty() && isSkippedPath(string4, list1, list2, observableHolder1, observableHolder2);
                            if (!observableHolder1.isValueNull()) {
                                String string5 = (String) observableHolder1.getValue();
                                MessageReporter messageReporter2;
                                String string12;
                                if (set2 != null) {
                                    set2.add(string5);
                                    messageReporter2 = messageReporter1;
                                    string12 = "INFO:";
                                } else {
                                    messageReporter2 = messageReporter1;
                                    string12 = "INFO:";
                                }

                                messageReporter2.reportInfo(string12, "Skipping file '" + string4 + "' because of parameter -\"" + string5 + "\"");
                            }

                            if (!observableHolder2.isValueNull()) {
                                String string7 = (String) observableHolder2.getValue();
                                messageReporter1.reportInfo("INFO:", "Unskipping file '" + string4 + "' because of parameter +\"" + string7 + "\"");
                                set3.add(string7);
                            }

                            if (bl2) {
                                if (ZkmFileUtils.isYamlFileName(string3)) {
                                    set7.add(new InputFileLocation(archiveZipFile, zipEntry1, sourceArchive2));
                                } else {
                                    set8.add(new InputFileLocation(archiveZipFile, zipEntry1, sourceArchive2));
                                }
                            }
                        }
                    } else {
                        set9.add(new InputFileLocation(archiveZipFile, zipEntry1, sourceArchive2));
                        messageReporter1.reportInfo(
                                "INFO:",
                                "Filtering out path '" + string4 + "' because it does not match specified filter '" + archivePathFilter.toFilterExpression() + "' (D)"
                        );
                    }
                }
            }

            if (zipEntry2 != null) {
                try {
                    ArchiveManifest archiveManifest = new ArchiveManifest(archiveZipFile, zipEntry2);
                    sourceArchive2.setManifest(archiveManifest);
                } catch (ZkmException zkmException) {
                    messageReporter1.reportFatalError("FILE ERROR:", "Invalid MANIFEST in '" + string + "' : " + zkmException.getMessage());
                } catch (IOException iOException) {
                    messageReporter1.reportFatalError("FILE ERROR:", "Error reading MANIFEST in '" + string + "' : " + iOException);
                }
            }

            if (!bl3) {
                archiveZipFile.close();
            }
        } catch (ZipException zipException) {
            map1.remove(string1);
            StringBuffer stringBuffer2 = new StringBuffer();
            stringBuffer2.append("\"" + string1 + "\" couldn't be written or opened as an archive file : ");
            if (!string1.equals(string)) {
                stringBuffer2.append(" \"" + string + "\" : ");
            }

            stringBuffer2.append(zipException.getMessage());
            messageReporter1.reportWarning("ARCHIVE FILE ERROR:", stringBuffer2.toString());
        } catch (IOException iOException3) {
            map1.remove(string1);
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("\"" + string1 + "\" couldn't be opened or written : ");
            StringBuffer stringBuffer3;
            String string14;
            if (!string1.equals(string)) {
                stringBuffer.append("\"" + string + "\" : ");
                stringBuffer3 = stringBuffer;
                string14 = iOException3.getMessage();
            } else {
                stringBuffer3 = stringBuffer;
                string14 = iOException3.getMessage();
            }

            stringBuffer3.append(string14);
            messageReporter1.reportFatalError("FILE ERROR:", stringBuffer.toString());
        }

        for (int i = 0; i < vector.size(); i++) {
            PendingNestedArchive pendingNestedArchive = (PendingNestedArchive) vector.elementAt(i);
            scanArchive(
                    pendingNestedArchive.extractedFile,
                    set1,
                    map1,
                    openPathEntry,
                    bl,
                    list1,
                    list2,
                    set2,
                    set3,
                    set4,
                    set5,
                    set6,
                    set7,
                    set8,
                    set9,
                    pendingNestedArchive.entryName,
                    pendingNestedArchive.parentArchive,
                    zipOutputTarget,
                    set10,
                    messageReporter1
            );
        }
    }

    public static String getExtension(String string) {
        int ba = string.lastIndexOf(46);
        if (ba > -1 && ba < string.length()) {
            return string.substring(ba + 1);
        } else {
            return string.equals("*") ? "*" : "";
        }
    }

    public static boolean matchesFilter(String string, ArchivePathFilter archivePathFilter) {
        return archivePathFilter == ArchivePathFilter.ACCEPT_ALL ? true : archivePathFilter.acceptsPath(string);
    }

    private ArchiveScanner() {
    }
}
