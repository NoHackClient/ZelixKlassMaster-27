package com.zelix.klassmaster.xml;

import com.zelix.klassmaster.archive.ZkmFileUtils;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.TwoKeyMap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class XmlResourceReader {
    public XmlResourceReader(
            ZipFile zipFile1,
            ZipEntry zipEntry1,
            MutableInt mutableInt,
            MutableInt mutableInt1,
            MutableInt mutableInt2,
            String string,
            XmlContentHandler xmlContentHandler,
            Map map1,
            Map map2,
            Map map3,
            TwoKeyMap twoKeyMap
    ) throws ZkmException, IOException {
        InputStream inputStream1 = null;

        try {
            inputStream1 = zipFile1.getInputStream(zipEntry1);
            int value = mutableInt.getValue();
            mutableInt1.getValue();
            mutableInt2.getValue();
            String string1 = ZkmFileUtils.describeEntryWithOriginal(zipFile1, zipEntry1);
            TwoKeyMap twoKeyMap1 = twoKeyMap;
            Map map4 = map3;
            Map map5 = map2;
            Map map6 = map1;
            new XmlResourceProcessor(inputStream1, xmlContentHandler, string, value, map6, map5, map4, twoKeyMap1, string1);
            xmlContentHandler.finishAnalysis(map3, twoKeyMap);
        } finally {
            try {
                if (inputStream1 != null) {
                    inputStream1.close();
                }
            } catch (IOException iOException) {
            }
        }
    }

    public XmlResourceReader(final String s, final String s2, final MutableInt mutableInt, final MutableInt mutableInt2, final MutableInt mutableInt3, final String s3, final XmlContentHandler xmlContentHandler) throws ZkmException, IOException {
        final HashMap hashMap = ZkmUtils.createHashMap(13);
        final Map map = null;
        final Map map2 = null;
        final TwoKeyMap twoKeyMap = null;
        InputStream inputStream = null;
        try {
            final ByteArrayInputStream byteArrayInputStream;
            inputStream = (byteArrayInputStream = new ByteArrayInputStream(s2.getBytes()));
            final int value = mutableInt.getValue();
            mutableInt2.getValue();
            mutableInt3.getValue();
            final XmlResourceProcessor xmlResourceProcessor = new XmlResourceProcessor(byteArrayInputStream, xmlContentHandler, s3, value, hashMap, map, map2, twoKeyMap, s);
            try {
                inputStream.close();
            } catch (final IOException ex) {
            }
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (final IOException ex2) {
            }
        }
    }

    public XmlResourceReader(final File file, final MutableInt mutableInt, final MutableInt mutableInt2, final MutableInt mutableInt3, final String s, final XmlContentHandler xmlContentHandler, final Map map, final Map map2, final Map map3, final TwoKeyMap twoKeyMap) throws ZkmException, IOException {
        InputStream inputStream = null;
        try {
            final FileInputStream fileInputStream;
            inputStream = (fileInputStream = new FileInputStream(file));
            final int value = mutableInt.getValue();
            mutableInt2.getValue();
            mutableInt3.getValue();
            final XmlResourceProcessor xmlResourceProcessor = new XmlResourceProcessor(fileInputStream, xmlContentHandler, s, value, map, map2, map3, twoKeyMap, file.getAbsolutePath());
            xmlContentHandler.finishAnalysis(map3, twoKeyMap);
            try {
                inputStream.close();
            } catch (final IOException ex) {
            }
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (final IOException ex2) {
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
