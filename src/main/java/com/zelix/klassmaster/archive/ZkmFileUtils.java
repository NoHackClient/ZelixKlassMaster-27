package com.zelix.klassmaster.archive;

import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.AssertionFailedException;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.StringKeyTransform;
import com.zelix.klassmaster.util.ZkmStringUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.PushbackInputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZkmFileUtils {
    public static final String USER_DIR = System.getProperty("user.dir");
    public static final File USER_DIR_FILE = new File(USER_DIR);
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    public static final char FILE_SEPARATOR_CHAR = FILE_SEPARATOR.charAt(0);
    public static final String PATH_SEPARATOR = System.getProperty("path.separator");
    public static final char PATH_SEPARATOR_CHAR = PATH_SEPARATOR.charAt(0);
    public static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");
    public static final String DEFAULT_ENCODING = System.getProperty("file.encoding", "UTF-8");
    public static boolean caseSensitiveFileSystem = true;
    public static final String OS_NAME = System.getProperty("os.name");
    public static List extraXmlFilePatterns;

    public static List listFilesInMatchingDirectories(String string, String string1, File file1, ObservableHolder observableHolder) throws ZkmException, IOException {
        int ba = string.lastIndexOf(FILE_SEPARATOR);
        String string2;
        String string3;
        if (ba > -1) {
            string2 = string.substring(0, ba);
            string3 = string.substring(ba + 1);
        } else {
            string2 = null;
            string3 = string;
        }

        File file2;
        if (string2 == null) {
            file2 = file1;
        } else if (isRelativePath(string2)) {
            file2 = new File(file1, string2);
        } else {
            file2 = new File(string2);
        }

        Vector vector = new Vector();
        if (!file2.exists()) {
            observableHolder.setValue("Directory '" + file2.getAbsolutePath() + "' does not exist.");
            return vector;
        }

        if (!file2.isDirectory()) {
            observableHolder.setValue("Path '" + file2.getAbsolutePath() + "' is not a directory.");
            return vector;
        }

        File[] files = file2.listFiles();
        if (files == null) {
            observableHolder.setValue("Unknown error reading '" + file2.getAbsolutePath() + "'.");
            return vector;
        }

        for (File file3 : files) {
            if (file3.isDirectory() && ZkmStringUtils.matchesWildcard(file3.getName(), string3)) {
                collectMatchingFiles(file3, string1, vector);
            }
        }

        return vector;
    }

    public static byte[] readBytes(InputStream inputStream1, int ba) throws IOException {
        byte[] bb = null;
        if (ba > 0) {
            bb = new byte[ba];
            readFully(inputStream1, bb);
        }

        return bb;
    }

    
    
    public static File copyStreamToTempFile(BufferedInputStream bufferedInputStream, String string) throws IOException {
        File file1 = TempFileManager.createTempFile(string);
        BufferedOutputStream bufferedOutputStream = null;
        boolean bl = false ;

        try {
            bl = true;
            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file1));
            bufferedInputStream.available();
            byte[] ba = new byte[2048];
            int bc = 0;
            int be = bufferedInputStream.read(ba);

            while (true) {
                int bb = be;
                if (be == -1) {
                    bl = false;
                    break;
                }

                bc += bb;
                int bd = bb;
                bufferedOutputStream.write(ba, 0, bd);
                be = bufferedInputStream.read(ba);
            }
        } finally {
            if (bl) {
                if (bufferedOutputStream != null) {
                    try {
                        bufferedOutputStream.close();
                    } catch (IOException iOException) {
                    }
                }
            }
        }

        try {
            bufferedOutputStream.close();
        } catch (IOException iOException1) {
        }

        return file1;
    }

    public static boolean isRelativePath(String string) {
        String string1 = string;
        if (string1 != null && string1.trim().length() != 0) {
            string1 = string1.trim();
            return string1.charAt(0) == File.separatorChar ? false : string1.length() <= 1 || !Character.isLetter(string1.charAt(0)) || string1.charAt(1) != ':';
        } else {
            return false;
        }
    }

    public static boolean isNestedArchiveEntry(ZipFile zipFile1, ZipEntry zipEntry1) throws IOException {
        return looksLikeArchiveName(zipEntry1.getName()) && entryHasZipSignature(zipFile1, zipEntry1);
    }

    public static String readFileAsString(File file1) throws IOException {
        return readFileAsString(file1, DEFAULT_ENCODING);
    }

    public static BufferedReader openReader(File file1, String string) throws ZkmException, IOException {
        return createBomAwareReader(new FileInputStream(file1), string, (ObservableHolder) null);
    }

    public static byte[] peekBytes(PushbackInputStream pushbackInputStream) {
        try {
            byte[] ba = new byte[4];
            int bb = readFully(pushbackInputStream, ba);
            if (bb == -1) {
                pushbackInputStream.unread(ba, 0, 0);
            } else {
                int bc = bb;
                pushbackInputStream.unread(ba, 0, bc);
            }

            return ba;
        } catch (IOException iOException) {
            return null;
        }
    }

    public static void copyFile(String string, String string1) throws IOException {
        copyFile(string, string1, (Integer) null, (Integer) null);
    }

    public static String computeRelativePath(String string, String string1) {
        if (string.equals(string1)) {
            return ".";
        }

        char bb = '/';
        int ba;
        if (((ba = string.lastIndexOf(FILE_SEPARATOR)) > -1 || (ba = string.lastIndexOf("\\")) > -1 || (ba = string.lastIndexOf("/")) > -1) && ba > -1) {
            bb = string.charAt(ba);
        }

        if (bb == 0
                && ((ba = string1.lastIndexOf(FILE_SEPARATOR)) > -1 || (ba = string1.lastIndexOf("\\")) > -1 || (ba = string1.lastIndexOf("/")) > -1)
                && ba > -1) {
            bb = string1.charAt(ba);
        }

        if (string.trim().length() == 0) {
            return string1.charAt(0) == bb ? string1.substring(1) : string1;
        }

        String string2 = String.valueOf(bb);
        StringTokenizer stringTokenizer = new StringTokenizer(string, string2, true);
        StringTokenizer stringTokenizer1 = new StringTokenizer(string1, string2, true);
        int bc = stringTokenizer.countTokens();
        int bd = stringTokenizer1.countTokens();
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < bc && i < bd; i++) {
            String string3 = stringTokenizer.nextToken();
            String string4 = stringTokenizer1.nextToken();
            if (!string3.equals(string4)) {
                break;
            }

            stringBuilder.append(string3);
        }

        String string6 = stringBuilder.toString();
        if (string6.length() == 0) {
            StringTokenizer stringTokenizer3 = new StringTokenizer(string, string2);
            int bi = stringTokenizer3.countTokens();
            StringBuilder stringBuilder2 = new StringBuilder();

            for (int i = 0; i < bi; i++) {
                stringBuilder2.append("..");
                if (i < bi - 1) {
                    stringBuilder2.append(string2);
                }
            }

            if (!string1.startsWith(string2)) {
                stringBuilder2.append(string2);
            }

            stringBuilder2.append(string1);
            return stringBuilder2.toString();
        } else {
            StringTokenizer stringTokenizer2 = new StringTokenizer(string6, string2, true);
            int bh = stringTokenizer2.countTokens();
            String string5 = string1.substring(string6.length());
            StringBuilder stringBuilder1 = new StringBuilder();
            if (bc == bh) {
                stringBuilder1.append(".");
            } else {
                int bf = bc - bh;
                stringBuilder1.append("..");
                bf += -1;

                for (int i = 0; i < bf / 2; i++) {
                    stringBuilder1.append(string2);
                    stringBuilder1.append("..");
                }
            }

            if (string5.length() > 0) {
                if (stringBuilder1.length() == 1 && stringBuilder1.toString().equals(".")) {
                    stringBuilder1.setLength(0);
                    if (string5.charAt(0) == bb) {
                        string5 = string5.substring(1);
                    }
                } else if (stringBuilder1.charAt(stringBuilder1.length() - 1) == bb) {
                    if (string5.charAt(0) == bb) {
                        stringBuilder1.setLength(stringBuilder1.length() - 1);
                    }
                } else if (string5.charAt(0) != bb) {
                    stringBuilder1.append(string2);
                }

                stringBuilder1.append(string5);
            }

            return stringBuilder1.toString();
        }
    }

    
    
    public static File extractEntryToTempFile(ZipFile zipFile1, ZipEntry zipEntry1) throws IOException {
        BufferedInputStream bufferedInputStream = null;
        boolean bl = false ;

        File file1;
        try {
            bl = true;
            bufferedInputStream = new BufferedInputStream(zipFile1.getInputStream(zipEntry1));
            file1 = copyStreamToTempFile(bufferedInputStream, zipFile1.getName() + "!" + zipEntry1.getName());
            bl = false;
        } finally {
            if (bl) {
                if (bufferedInputStream != null) {
                    try {
                        bufferedInputStream.close();
                    } catch (IOException iOException) {
                    }
                }
            }
        }

        try {
            bufferedInputStream.close();
        } catch (IOException iOException1) {
        }

        return file1;
    }

    public static boolean isXmlFileName(String string) {
        int ba = string.lastIndexOf(46);
        if (ba > -1) {
            String string1 = string.substring(ba, string.length());
            if (string1.equalsIgnoreCase(".xml") || string1.equalsIgnoreCase(".tld") || string1.equalsIgnoreCase(".fxml") || string1.equalsIgnoreCase(".e4xmi")) {
                return true;
            }
        }

        if (extraXmlFilePatterns != null) {
            Iterator iterator = extraXmlFilePatterns.iterator();

            while (iterator.hasNext()) {
                String string2 = (String) iterator.next();
                if (string2.indexOf("*") > -1 && ZkmStringUtils.matchesWildcard(string, string2)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static String detectBomEncoding(byte[] ba) {
        return detectBomEncoding(ba, new MutableInt(), new MutableInt(), new MutableInt());
    }

    public static BufferedReader openReaderDetectingCharset(File file1, ObservableHolder observableHolder) throws ZkmException, IOException {
        return createBomAwareReader(new FileInputStream(file1), (String) null, observableHolder);
    }

    public static String detectBomEncoding(byte[] ba, MutableInt mutableInt, MutableInt mutableInt1, MutableInt mutableInt2) {
        if (ba == null) {
            return null;
        } else if (ba.length != 4) {
            throw new IllegalArgumentException("Byte Order Mark byte array length not4 : " + ba.length);
        } else {
            int bb = ba[0] & 255;
            int bc = ba[1] & 255;
            int bd = ba[2] & 255;
            if (bb == 239 && bc == 187 && bd == 191) {
                mutableInt.setValue(3);
                mutableInt1.setValue(3);
                return "UTF-8";
            } else if (bb == 255 && bc == 254) {
                mutableInt1.setValue(2);
                mutableInt2.setValue(0);
                return "UTF-16";
            } else if (bb == 254 && bc == 255) {
                mutableInt1.setValue(2);
                mutableInt2.setValue(1);
                return "UTF-16";
            } else {
                return null;
            }
        }
    }

    
    
    public static File writeStringToFile(String string, File file1, ObservableHolder observableHolder) throws ZkmException, IOException {
        PrintWriter printWriter = null;
        boolean bl = false ;

        label52:
        {
            Object object;
            try {
                bl = true;
                printWriter = new PrintWriter(new BufferedWriter(new FileWriter(file1)));
                printWriter.println(string);
                printWriter.flush();
                bl = false;
                break label52;
            } catch (IOException iOException) {
                observableHolder.setValue(iOException.getMessage());
                object = null;
                bl = false;
            } finally {
                if (bl) {
                    if (printWriter != null) {
                        printWriter.close();
                    }
                }
            }

            if (printWriter != null) {
                printWriter.close();
            }

            return (File) object;
        }

        printWriter.close();
        return file1;
    }

    public static String resolveDotRelativePath(String string, File file1) {
        File file2 = file1;
        String string1 = string;
        if (string1 == null) {
            return null;
        }

        String string2;
        if (file2 == null) {
            string2 = USER_DIR;
            file2 = USER_DIR_FILE;
        } else {
            string2 = file2.getAbsolutePath();
        }

        string1 = string1.trim();
        StringBuilder stringBuilder = new StringBuilder();
        if (string1.startsWith(".." + FILE_SEPARATOR_CHAR)) {
            File file3 = file2.getParentFile();
            if (file3 == null) {
                stringBuilder.append(string2);
            } else {
                stringBuilder.append(file3.getAbsolutePath());
            }

            if (string1.length() > 2) {
                if (stringBuilder.charAt(stringBuilder.length() - 1) == FILE_SEPARATOR_CHAR && string1.charAt(2) == FILE_SEPARATOR_CHAR) {
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                }

                stringBuilder.append(string1.substring(2));
            }
        } else {
            if (!string1.startsWith("." + FILE_SEPARATOR_CHAR)) {
                return string1;
            }

            stringBuilder.append(string2);
            if (string1.length() > 1) {
                if (stringBuilder.charAt(stringBuilder.length() - 1) == FILE_SEPARATOR_CHAR && string1.charAt(1) == FILE_SEPARATOR_CHAR) {
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                }

                stringBuilder.append(string1.substring(1));
            }
        }

        return stringBuilder.toString();
    }

    public static BufferedReader openReaderForPath(String string, String string1) throws ZkmException, IOException {
        return createBomAwareReader(new FileInputStream(string.trim()), string1, (ObservableHolder) null);
    }

    public static boolean isPropertiesFileName(String string) {
        if (!HiddenOptionFlags.PROCESS_PROPERTIES) {
            return false;
        }

        int ba = string.lastIndexOf(46);
        return ba > -1 && string.substring(ba, string.length()).equalsIgnoreCase(".properties");
    }

    
    
    public static boolean fileHasZipSignature(File file1) throws IOException {
        FileInputStream fileInputStream = null;
        boolean bl1 = false ;

        boolean bl;
        try {
            bl1 = true;
            fileInputStream = new FileInputStream(file1);
            bl = streamHasZipSignature(fileInputStream);
            bl1 = false;
        } finally {
            if (bl1) {
                try {
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                } catch (AssertionFailedException assertionFailedException) {
                    throw assertionFailedException;
                } catch (Exception exception) {
                }
            }
        }

        try {
            fileInputStream.close();
        } catch (AssertionFailedException assertionFailedException1) {
            throw assertionFailedException1;
        } catch (Exception exception1) {
        }

        return bl;
    }

    public static boolean entryHasZipSignature(ZipFile zipFile1, ZipEntry zipEntry1) throws IOException {
        InputStream inputStream1 = null;

        try {
            inputStream1 = zipFile1.getInputStream(zipEntry1);
            return streamHasZipSignature(inputStream1);
        } finally {
            try {
                if (inputStream1 != null) {
                    inputStream1.close();
                }
            } catch (AssertionFailedException assertionFailedException) {
                throw assertionFailedException;
            } catch (Exception exception) {
            }
        }
    }

    
    
    public static File copyFileToTemp(File file1) throws IOException {
        BufferedInputStream bufferedInputStream = null;
        boolean bl = false ;

        File file2;
        try {
            bl = true;
            bufferedInputStream = new BufferedInputStream(new FileInputStream(file1));
            file2 = copyStreamToTempFile(bufferedInputStream, file1.getAbsolutePath());
            bl = false;
        } finally {
            if (bl) {
                if (bufferedInputStream != null) {
                    try {
                        bufferedInputStream.close();
                    } catch (IOException iOException) {
                    }
                }
            }
        }

        try {
            bufferedInputStream.close();
        } catch (IOException iOException1) {
        }

        return file2;
    }

    public static boolean isYamlFileName(String string) {
        if (!HiddenOptionFlags.PROCESS_YAML) {
            return false;
        }

        int ba = string.lastIndexOf(46);
        if (ba > -1) {
            String string1 = string.substring(ba, string.length());
            if (string1.equalsIgnoreCase(".yaml") || string1.equalsIgnoreCase(".yml")) {
                return true;
            }
        }

        return false;
    }

    public static BufferedReader openReaderForPath(String string) throws ZkmException, IOException {
        return createBomAwareReader(new FileInputStream(string.trim()), (String) null, (ObservableHolder) null);
    }

    public static String readEntryAsString(ZipFile zipFile1, ZipEntry zipEntry1, String string) throws IOException {
        return readStreamAsString(zipFile1.getInputStream(zipEntry1), string, zipEntry1.getSize(), (StringKeyTransform) null);
    }

    public static boolean looksLikeArchiveName(String string) {
        int ba = string.length();
        int bb = string.lastIndexOf(46);
        if (bb > 0 && (bb == ba - 4 || bb == ba - 5)) {
            String string1 = string.substring(bb).toLowerCase();
            if (string1.equals(".zip") || string1.equals(".jmod") || string1.endsWith("ar")) {
                return true;
            }
        }

        return false;
    }

    public static String splitPath(String string, ObservableHolder observableHolder, ObservableHolder observableHolder1) throws ZkmException, IOException {
        int ba;
        if ((ba = string.lastIndexOf(FILE_SEPARATOR)) <= -1 && (ba = string.lastIndexOf("\\")) <= -1 && (ba = string.lastIndexOf("/")) <= -1) {
            observableHolder.setValue("");
            observableHolder1.setValue(string);
            return "";
        } else {
            observableHolder.setValue(string.substring(0, ba));
            observableHolder1.setValue(string.substring(ba + 1));
            return string.substring(ba, ba + 1);
        }
    }

    
    
    public static boolean canWriteAndReadFile(String string, ObservableHolder observableHolder) throws ZkmException, IOException {
        PrintWriter printWriter = null;
        boolean bl1 = false ;

        label185:
        {
            try {
                bl1 = true;
                printWriter = new PrintWriter(new FileWriter(string));
                printWriter.println("xXyYzZ");
                bl1 = false;
                break label185;
            } catch (IOException iOException4) {
                if (observableHolder != null) {
                    observableHolder.setValue(iOException4.toString());
                    bl1 = false;
                } else {
                    bl1 = false;
                }
            } finally {
                if (bl1) {
                    if (printWriter != null) {
                        printWriter.close();
                    }
                }
            }

            if (printWriter != null) {
                printWriter.close();
            }

            return false;
        }

        printWriter.close();
        BufferedReader bufferedReader = null;
        boolean bl = false ;

        String string1;
        label186:
        {
            try {
                bl = true;
                bufferedReader = new BufferedReader(new FileReader(string));
                string1 = bufferedReader.readLine();
                bl = false;
                break label186;
            } catch (IOException iOException3) {
                if (observableHolder != null) {
                    observableHolder.setValue(iOException3.toString());
                    bl = false;
                } else {
                    bl = false;
                }
            } finally {
                if (bl) {
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (IOException iOException) {
                        }
                    }
                }
            }

            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException iOException1) {
                }
            }

            return false;
        }

        try {
            bufferedReader.close();
        } catch (IOException iOException2) {
        }

        return string1 != null && "xXyYzZ".equals(string1);
    }

    public static int readFully(InputStream inputStream1, byte[] ba) throws IOException {
        int bb = 0;

        do {
            int bc = inputStream1.read(ba, bb, ba.length - bb);
            if (bc == -1) {
                if (bb == 0) {
                    bb = -1;
                }
                break;
            }

            bb += bc;
        } while (bb < ba.length);

        return bb;
    }

    public static String getParentPath(String string) {
        String string1 = string;
        String string2;
        String string3;
        if (string1.length() > 1) {
            char ba = string1.charAt(string1.length() - 1);
            if (ba != FILE_SEPARATOR.charAt(0) && ba != "/".charAt(0) && ba != "\\".charAt(0)) {
                string2 = string1;
                string3 = FILE_SEPARATOR;
            } else {
                string1 = string1.substring(0, string1.length() - 1);
                string2 = string1;
                string3 = FILE_SEPARATOR;
            }
        } else {
            string2 = string1;
            string3 = FILE_SEPARATOR;
        }

        int bb;
        if ((bb = string2.lastIndexOf(string3)) <= -1 && (bb = string1.lastIndexOf("/")) <= -1 && (bb = string1.lastIndexOf("\\")) <= -1) {
            return string1.length() > 0 ? "" : null;
        } else if (bb == 0) {
            return string1.length() == 1 ? null : string1.substring(0, bb + 1);
        } else {
            return string1.substring(0, bb);
        }
    }

    public static BufferedReader createBomAwareReader(InputStream inputStream1, String string, ObservableHolder observableHolder) throws ZkmException, IOException {
        PushbackInputStream pushbackInputStream = new PushbackInputStream(inputStream1, 4);
        String string1 = null;
        if (string != null && string.trim().length() > 0) {
            string1 = string.trim();
        }

        boolean bl = false;
        if (string1 == null || string1.length() == 0 || string1.equals("UTF-8")) {
            String string2 = detectBomEncoding(peekBytes(pushbackInputStream));
            bl = string2 != null && string2.equals("UTF-8");
            if (string2 != null && string2.length() > 0 && (string1 == null || string1.length() == 0)) {
                string1 = string2;
            }
        }

        BufferedReader bufferedReader;
        if (string1 != null && string1.length() != 0) {
            if (bl) {
                readBytes(pushbackInputStream, 3);
            }

            bufferedReader = new BufferedReader(new InputStreamReader(pushbackInputStream, string1));
        } else {
            bufferedReader = new BufferedReader(new InputStreamReader(pushbackInputStream));
        }

        if (observableHolder != null) {
            observableHolder.setValue(string1);
        }

        return bufferedReader;
    }

    public static BufferedReader openReader(File file1) throws ZkmException, IOException {
        return createBomAwareReader(new FileInputStream(file1), (String) null, (ObservableHolder) null);
    }

    public static String describeEntryWithOriginal(ZipFile zipFile1, ZipEntry zipEntry1) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(zipFile1.getName());
        File file1 = new File(zipFile1.getName());
        if (TempFileManager.isZkmTempFile(file1)) {
            String string = TempFileManager.getOriginalName(file1.getAbsolutePath());
            if (string != null) {
                stringBuilder.append('(');
                stringBuilder.append(string);
                stringBuilder.append(')');
            }
        }

        stringBuilder.append("!");
        stringBuilder.append(zipEntry1.getName());
        return stringBuilder.toString();
    }

    public static String getEntryPath(ZipFile zipFile1, ZipEntry zipEntry1) {
        return zipFile1.getName() + "!" + zipEntry1.getName();
    }

    public static boolean isKnownArchiveName(String string) {
        int ba = string.length();
        if (ba > 4) {
            String string1 = string.substring(ba - 4);
            if (string1.equalsIgnoreCase(".zip")
                    || string1.equalsIgnoreCase(".jar")
                    || string1.equalsIgnoreCase(".ear")
                    || string1.equalsIgnoreCase(".war")
                    || string1.equalsIgnoreCase(".rar")
                    || string1.equalsIgnoreCase(".sar")
                    || string1.equalsIgnoreCase(".par")
                    || string1.equalsIgnoreCase(".aar")) {
                return true;
            }

            if (ba > 5 && string.substring(ba - 5).equalsIgnoreCase(".jmod")) {
                return true;
            }
        }

        return false;
    }

    public static boolean isArchiveFile(File file1) throws IOException {
        String string = file1.getName();
        return isKnownArchiveName(string) || looksLikeArchiveName(string) && fileHasZipSignature(file1);
    }

    public static String readFileTransformed(File file1, String string, StringKeyTransform stringKeyTransform) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file1);
        return readStreamAsString(fileInputStream, string, fileInputStream.available() + 10, stringKeyTransform);
    }

    public static File writeStringToTempFile(String string, ObservableHolder observableHolder) throws ZkmException, IOException {
        try {
            return writeStringToFile(string, TempFileManager.createTempFile(), observableHolder);
        } catch (IOException iOException) {
            observableHolder.setValue(iOException.getMessage());
            return null;
        }
    }

    public static void collectMatchingFiles(File file1, String string, List list1) {
        for (File file2 : file1.listFiles()) {
            if (file2.isDirectory()) {
                collectMatchingFiles(file2, string, list1);
            } else {
                String string1 = file2.getName();
                if (!caseSensitiveFileSystem) {
                    string1 = string1.toUpperCase();
                }

                if (ZkmStringUtils.matchesWildcard(string1, string)) {
                    list1.add(file2.getAbsolutePath());
                }
            }
        }
    }

    public static String normalizeSeparators(String string) {
        String string1 = string;
        string1 = string1.trim();
        String string2;
        char ba;
        if (string1.length() > 1) {
            if (Character.isLetter(string1.charAt(0))) {
                if (string1.charAt(1) == ':') {
                    if (File.separatorChar == '\\') {
                        return string1.replace('/', File.separatorChar);
                    }

                    return string1;
                }

                string2 = string1;
                ba = '\\';
            } else {
                string2 = string1;
                ba = '\\';
            }
        } else {
            string2 = string1;
            ba = '\\';
        }

        string1 = string2.replace(ba, File.separatorChar);
        return string1.replace('/', File.separatorChar);
    }

    public static String readFileAsString(File file1, String string) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file1);
        return readStreamAsString(fileInputStream, string, fileInputStream.available() + 10, (StringKeyTransform) null);
    }

    public static String urlDecode(String string) {
        String string1 = null;

        try {
            string1 = URLDecoder.decode(string, DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException unsupportedEncodingException1) {
            try {
                string1 = URLDecoder.decode(string, "UTF-8");
            } catch (UnsupportedEncodingException unsupportedEncodingException) {
            }
        }

        return string1 != null ? string1 : string;
    }

    
    
    public static String readStreamAsString(InputStream inputStream1, String string, long ba, StringKeyTransform stringKeyTransform) throws IOException {
        BufferedReader bufferedReader = null;
        StringWriter stringWriter;
        if (ba > -1L && ba < 2147483647L) {
            stringWriter = new StringWriter((int) ba + 10);
        } else {
            stringWriter = new StringWriter();
        }

        boolean bl1 = false ;

        try {
            bl1 = true;
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream1, string));
            boolean bl = true;

            String string1;
            while ((string1 = bufferedReader.readLine()) != null) {
                if (stringKeyTransform != null) {
                    string1 = stringKeyTransform.transformLine(string1);
                }

                if (bl) {
                    bl = false;
                    stringWriter.write(string1);
                } else {
                    stringWriter.append(LINE_SEPARATOR);
                    stringWriter.write(string1);
                }
            }

            bl1 = false;
        } finally {
            if (bl1) {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException iOException) {
                    }
                } else {
                    try {
                        inputStream1.close();
                    } catch (Exception exception) {
                    }
                }
            }
        }

        try {
            bufferedReader.close();
        } catch (IOException iOException1) {
            return stringWriter.toString();
        }

        return stringWriter.toString();
    }

    public static boolean containsWildcard(String string) {
        return string.indexOf("*") > -1;
    }

    public static boolean streamHasZipSignature(InputStream inputStream1) throws IOException {
        if (inputStream1.available() >= 4) {
            byte[] ba = readBytes(inputStream1, 4);
            return (ba[0] & 255) == 80 && (ba[1] & 255) == 75 && (ba[2] & 255) == 3 && (ba[3] & 255) == 4;
        } else {
            return false;
        }
    }

    static {
        String string = "aAzZyY".toUpperCase();
        int ba = 0;

        File file1;
        File file2;
        do {
            file1 = new File("aAzZyY" + ba + ".txt");
            String string2 = string + ba++ + ".txt";
            file2 = new File(string2);
        } while (file1.exists() || file2.exists());

        PrintWriter printWriter = null;

        label96:
        {
            label95:
            {
                try {
                    printWriter = new PrintWriter(new FileWriter(file1), true);
                    printWriter.println("aAzZyY");
                    printWriter.flush();
                    if (!file2.exists()) {
                        caseSensitiveFileSystem = true;
                    } else {
                        caseSensitiveFileSystem = false;
                    }
                    break label95;
                } catch (IOException iOException) {
                    if (!OS_NAME.startsWith("Windows") && !OS_NAME.startsWith("OS/2") && !OS_NAME.startsWith("Mac OS")) {
                        caseSensitiveFileSystem = true;
                    } else {
                        caseSensitiveFileSystem = false;
                    }
                } finally {
                    printWriter.close();
                }

                file1.delete();
                break label96;
            }

            file1.delete();
        }

        if (HiddenOptionFlags.EXTRA_XML_FILE_TYPES != null) {
            extraXmlFilePatterns = new ArrayList();
            StringTokenizer stringTokenizer = new StringTokenizer(HiddenOptionFlags.EXTRA_XML_FILE_TYPES, ";");

            while (stringTokenizer.hasMoreTokens()) {
                String string1 = stringTokenizer.nextToken();
                List list1;
                if (!caseSensitiveFileSystem) {
                    string1 = string1.toLowerCase();
                    list1 = extraXmlFilePatterns;
                } else {
                    list1 = extraXmlFilePatterns;
                }

                list1.add(string1);
            }
        }
    }

    public static String getLastPathComponent(String string) {
        if (string == null) {
            return null;
        }

        String string1 = string.trim();
        if (FILE_SEPARATOR_CHAR == '\\') {
            string1 = string1.replace('/', FILE_SEPARATOR_CHAR);
        } else if (FILE_SEPARATOR_CHAR == '/') {
            string1 = string1.replace('\\', FILE_SEPARATOR_CHAR);
        }

        while (string1.endsWith(FILE_SEPARATOR)) {
            string1 = string1.substring(0, string1.length() - FILE_SEPARATOR.length());
        }

        int ba = string1.lastIndexOf(FILE_SEPARATOR_CHAR);
        if (ba > -1) {
            return ba == string1.length() - 1 ? string1.substring(0, string1.length() - 1) : string1.substring(ba + 1);
        } else {
            return string1;
        }
    }

    public static List expandFilePath(String string, File file1, ObservableHolder observableHolder) throws ZkmException, IOException {
        observableHolder.clearValue();
        Vector vector = new Vector();
        ObservableHolder observableHolder1;
        String string5;
        if (string != null) {
            if (string.trim().length() != 0) {
                String string1 = string.trim();
                string1 = normalizeSeparators(string1);
                string1 = resolveDotRelativePath(string1, file1);
                if (containsWildcard(string1)) {
                    int ba = string1.lastIndexOf(FILE_SEPARATOR);
                    File file2;
                    String string2;
                    if (ba > 0) {
                        string2 = string1.substring(ba + 1, string1.length());
                        String string3 = string1.substring(0, ba);
                        if (containsWildcard(string3)) {
                            int bb = string3.indexOf("*");
                            int bc = string3.lastIndexOf(FILE_SEPARATOR);
                            if (bb < bc) {
                                observableHolder.setValue("Wildcard not in final directory qualifier : '" + string3 + "'");
                                return vector;
                            }

                            if (!caseSensitiveFileSystem) {
                                string2 = string2.toUpperCase();
                            }

                            return listFilesInMatchingDirectories(string3, string2, file1, observableHolder);
                        }

                        if (isRelativePath(string3)) {
                            file2 = new File(file1, string3);
                        } else {
                            file2 = new File(string3);
                        }
                    } else {
                        file2 = file1;
                        string2 = string1;
                    }

                    if (!caseSensitiveFileSystem) {
                        string2 = string2.toUpperCase();
                    }

                    if (file2.isDirectory() && file2.exists()) {
                        File[] files = file2.listFiles();
                        if (files != null) {
                            for (int i = 0; i < files.length; i++) {
                                String string4 = files[i].getName();
                                if (!caseSensitiveFileSystem) {
                                    string4 = string4.toUpperCase();
                                }

                                if (ZkmStringUtils.matchesWildcard(string4, string2)) {
                                    vector.add(files[i].getAbsolutePath());
                                }
                            }
                        } else {
                            observableHolder.setValue("' File error trying to access " + file2.getAbsolutePath() + "' : '" + string1 + "'");
                        }
                    } else {
                        observableHolder.setValue("'" + file2.getAbsolutePath() + "' is not a directory or does not exist : '" + string1 + "'");
                    }
                } else {
                    File file3 = new File(string1);
                    vector.add(file3.getAbsolutePath());
                }

                return vector;
            }

            observableHolder1 = observableHolder;
            string5 = "No file path specified";
        } else {
            observableHolder1 = observableHolder;
            string5 = "No file path specified";
        }

        observableHolder1.setValue(string5);
        return vector;
    }

    public static List getExtraXmlFilePatterns() {
        return extraXmlFilePatterns;
    }

    public static String resolveAgainstBase(String string, String string1) {
        String string2 = string;
        if (string2 == null) {
            return null;
        }

        if (string1 == null) {
            throw new IllegalArgumentException("Must specify a base directory. Cannot translate '" + string2 + "'.");
        }

        string2 = string2.trim();
        StringBuilder stringBuilder = new StringBuilder();
        if (string2.startsWith("..")) {
            String string3 = string2;
            String string4 = string1;
            String string7 = string3;

            for (String string8 = ".."; string7.startsWith(string8); string8 = "..") {
                String string5 = getParentPath(string4);
                if (string5 == null) {
                    throw new IllegalArgumentException("Base directory '" + string1 + "' has inadequate parent. Cannot translate '" + string2 + "'.");
                }

                string4 = string5;
                if (string3.length() > 2) {
                    string3 = string3.substring(3);
                } else {
                    string3 = "";
                }

                string7 = string3;
            }

            stringBuilder.append(string4);
            if (stringBuilder.length() > 0 && stringBuilder.charAt(stringBuilder.length() - 1) != '/' && string3.length() > 0) {
                stringBuilder.append('/');
            }

            stringBuilder.append(string3);
        } else if (string2.startsWith(".")) {
            if (string1.length() > 0) {
                stringBuilder.append(string1);
                if (string2.length() > 1) {
                    if (stringBuilder.charAt(stringBuilder.length() - 1) == '/' && string2.charAt(1) == '/') {
                        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                    }

                    stringBuilder.append(string2.substring(1));
                }
            } else {
                String string6 = string2.substring(1);
                if (string6.length() > 0 && string6.charAt(0) == '/') {
                    string6 = string6.substring(1);
                    stringBuilder.append(string6);
                }
            }
        } else {
            if (string2.charAt(0) == '/') {
                return string2;
            }

            if (string1.length() > 0) {
                stringBuilder.append(string1);
                if (stringBuilder.charAt(stringBuilder.length() - 1) != '/') {
                    stringBuilder.append('/');
                }
            }

            stringBuilder.append(string2);
        }

        return stringBuilder.toString();
    }

    public static BufferedReader createBomAwareReader(InputStream inputStream1) throws ZkmException, IOException {
        return createBomAwareReader(inputStream1, (String) null, (ObservableHolder) null);
    }

    
    
    public static void copyFile(String string, String string1, Integer integer, Integer integer1) throws IOException {
        if (new File(string).getAbsolutePath().equals(new File(string1).getAbsolutePath())) {
            throw new IllegalArgumentException("Source and destination files are the same : '" + new File(string).getAbsolutePath() + "'");
        }

        BufferedOutputStream bufferedOutputStream = null;
        BufferedInputStream bufferedInputStream = null;
        boolean bl = false ;

        try {
            bl = true;
            if (integer != null) {
                bufferedInputStream = new BufferedInputStream(new FileInputStream(string), integer);
            } else {
                bufferedInputStream = new BufferedInputStream(new FileInputStream(string));
            }

            int bf;
            if (integer1 != null) {
                bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(string1), integer1);
                bf = bufferedInputStream.available();
            } else {
                bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(string1));
                bf = bufferedInputStream.available();
            }

            int ba = bf;
            byte[] bb = new byte[ba];
            int bc = 0;

            while (bc < ba) {
                int be = ba - bc;
                int bd;
                if ((bd = bufferedInputStream.read(bb, bc, Math.min(8192, be))) == -1) {
                    break;
                }

                bc += bd;
            }

            bufferedOutputStream.write(bb);
            bl = false;
        } finally {
            if (bl) {
                if (bufferedOutputStream != null) {
                    try {
                        bufferedOutputStream.close();
                    } catch (IOException iOException1) {
                    }
                }

                if (bufferedInputStream != null) {
                    try {
                        bufferedInputStream.close();
                    } catch (IOException iOException) {
                    }
                }
            }
        }

        try {
            bufferedOutputStream.close();
        } catch (IOException iOException3) {
        }

        try {
            bufferedInputStream.close();
        } catch (IOException iOException2) {
        }
    }

    private ZkmFileUtils() {
    }
}
