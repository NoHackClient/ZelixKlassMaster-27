package com.zelix.klassmaster.xml;

import com.zelix.klassmaster.archive.ZipEntryWriter;
import com.zelix.klassmaster.archive.ZkmFileUtils;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.EnumerableMap;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class TextResourceRewriter {
    private static int predicateValue;
    private static long commentChar;

    public static void setPredicateValue() {
        predicateValue = 42;
    }

    public TextResourceRewriter(
            ZipFile zipFile1,
            ZipEntry zipEntry1,
            Long long1,
            MutableInt mutableInt,
            MutableInt mutableInt1,
            MutableInt mutableInt2,
            String string,
            ZipOutputStream zipOutputStream1,
            ResourcePathTranslator resourcePathTranslator1,
            boolean bl,
            EnumerableMap enumerableMap
    ) throws ZkmException, IOException {
        InputStream inputStream1 = null;

        try {
            inputStream1 = zipFile1.getInputStream(zipEntry1);
            String string1 = resourcePathTranslator1.translateResourcePath(zipEntry1.getName());
            ZipEntryWriter zipEntryWriter = new ZipEntryWriter(zipOutputStream1, string1, bl);
            OutputStream outputStream = zipEntryWriter.getOutputStream();
            int value = mutableInt.getValue();
            mutableInt1.getValue();
            int bb = mutableInt2.getValue();
            zipEntry1.getName();
            ObservableHolder observableHolder = new ObservableHolder();
            EnumerableMap enumerableMap1 = enumerableMap;
            Integer integer = bb;
            this.rewriteClassNames(inputStream1, outputStream, string, value, integer, enumerableMap1, observableHolder);
            zipEntryWriter.writeEntry(long1);
        } finally {
            try {
                if (inputStream1 != null) {
                    inputStream1.close();
                }
            } catch (IOException iOException) {
            }
        }
    }

    public static int getPredicateValue() {
        return predicateValue;
    }

    public TextResourceRewriter(final File file, final MutableInt mutableInt, final MutableInt mutableInt2, final MutableInt mutableInt3, final String s, final ZipOutputStream zipOutputStream, final boolean b, final EnumerableMap enumerableMap, final ObservableHolder observableHolder) throws ZkmException, IOException {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            final ZipEntryWriter zipEntryWriter = new ZipEntryWriter(zipOutputStream, file.getName(), b);
            final FileInputStream fileInputStream2 = fileInputStream;
            final OutputStream outputStream = zipEntryWriter.getOutputStream();
            final int value = mutableInt.getValue();
            mutableInt2.getValue();
            final int value2 = mutableInt3.getValue();
            file.getAbsolutePath();
            this.rewriteClassNames(fileInputStream2, outputStream, s, value, value2, enumerableMap, observableHolder);
            zipEntryWriter.writeEntry();
            try {
                fileInputStream.close();
            } catch (final IOException ex) {
            }
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (final IOException ex2) {
            }
        }
    }

    public void rewriteClassNames(final InputStream in, final OutputStream outputStream, final String s, final int n, final int n2, final EnumerableMap enumerableMap, final ObservableHolder observableHolder) throws ZkmException, IOException {
        String s2 = s;
        ZkmFileUtils.readBytes(in, n);
        BufferedReader bufferedReader = null;
        ByteArrayOutputStream out = null;
        PrintWriter printWriter = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(in, s2));
            out = new ByteArrayOutputStream();
            if (s2.equals("UTF-16") && n2 == 0) {
                s2 = "UTF-16LE";
            }
            printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(out, s2)));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                final int index = line.indexOf((int) TextResourceRewriter.commentChar);
                String substring;
                String substring2;
                if (index > -1) {
                    substring = line.substring(0, index);
                    substring2 = line.substring(index);
                } else {
                    substring = line;
                    substring2 = null;
                }
                final StringTokenizer stringTokenizer = new StringTokenizer(substring, " []{},\t", true);
                while (stringTokenizer.hasMoreTokens()) {
                    final String nextToken = stringTokenizer.nextToken();
                    if (nextToken.length() > 1) {
                        final String dotsToSlashes = ZkmUtils.dotsToSlashes(nextToken);
                        final String anObject = (String) ZkmUtils.mapOrSelf(dotsToSlashes, enumerableMap);
                        if (!dotsToSlashes.equals(anObject)) {
                            printWriter.print(ZkmUtils.slashesToDots(anObject));
                        } else {
                            printWriter.print(nextToken);
                        }
                    } else {
                        printWriter.print(nextToken);
                    }
                }
                if (substring2 != null) {
                    printWriter.println(substring2);
                } else {
                    printWriter.println("");
                }
            }
            printWriter.close();
            printWriter = null;
            final byte[] byteArray = out.toByteArray();
            observableHolder.setValue(byteArray);
            outputStream.write(byteArray);
            outputStream.flush();
            try {
                bufferedReader.close();
            } catch (final IOException ex) {
            }
            try {
                out.close();
            } catch (final IOException ex2) {
            }
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (final IOException ex3) {
                }
            }
            if (printWriter != null) {
                printWriter.close();
            }
            try {
                if (out != null) {
                    out.close();
                }
            } catch (final IOException ex4) {
            }
        }
    }

    public TextResourceRewriter(final File file, final MutableInt mutableInt, final MutableInt mutableInt2, final MutableInt mutableInt3, final String s, final File file2, final EnumerableMap enumerableMap) throws ZkmException, IOException {
        final File parentFile = file2.getParentFile();
        if (parentFile != null && !parentFile.exists()) {
            parentFile.mkdirs();
        }
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            fileOutputStream = new FileOutputStream(file2);
            final FileInputStream fileInputStream2 = fileInputStream;
            final FileOutputStream fileOutputStream2 = fileOutputStream;
            final int value = mutableInt.getValue();
            mutableInt2.getValue();
            final int value2 = mutableInt3.getValue();
            file.getAbsolutePath();
            this.rewriteClassNames(fileInputStream2, fileOutputStream2, s, value, value2, enumerableMap, new ObservableHolder());
            try {
                fileInputStream.close();
            } catch (final IOException ex) {
            }
            try {
                fileOutputStream.close();
            } catch (final IOException ex2) {
            }
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (final IOException ex3) {
            }
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (final IOException ex4) {
            }
        }
    }

    public static int alwaysZero() {
        return 0;
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
        setPredicateValue();
        commentChar = -8395585994711105501L;
    }
}
