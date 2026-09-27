package com.zelix;

import com.zelix.klassmaster.archive.TempFileManager;
import com.zelix.klassmaster.archive.ZkmFileUtils;
import com.zelix.klassmaster.proguard.ProGuardInputTranslator;
import com.zelix.klassmaster.proguard.ProGuardTranslateConstants;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.File;
import java.io.PrintStream;
import java.util.Properties;

public class ZKMProGuardTranslate extends ProGuardTranslateConstants {
    public static void main(String[] strings) {
        try {
            if (strings.length != 2) {
                System.out.println("Usage: java com.zelix.ZKMProGuardTranslate <proguardConfigFileName> <outputFileName>");
                System.exit(1);
            }

            File file1 = new File(strings[0]);
            File file2 = new File(strings[1]);
            PrintStream printStream;
            if (file1.exists()) {
                if (!file1.isDirectory()) {
                    if (file2.isDirectory()) {
                        System.out.println("File '" + file2.getAbsolutePath() + "' is a directory");
                        return;
                    }

                    String string = ProGuardInputTranslator.translateToZkmScript(ZkmFileUtils.readFileAsString(file1), new Properties());
                    ObservableHolder observableHolder = new ObservableHolder();
                    ZkmFileUtils.writeStringToFile(string, file2, observableHolder);
                    if (observableHolder.isValueNull()) {
                        System.out.println("Translated contents of '" + file1.getAbsolutePath() + "' written to '" + file2.getAbsolutePath() + "'");
                    } else {
                        System.out.println("ERROR: '" + (String) observableHolder.getValue() + "'");
                    }

                    return;
                }

                printStream = System.out;
            } else {
                printStream = System.out;
            }

            printStream.println("File '" + file1.getAbsolutePath() + "' doesn't exist or is a directory");
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public void close() {
        TempFileManager.deleteStaleTempFiles();
    }

    public String getTranslatedStackTrace(String string) {
        try {
            return ProGuardInputTranslator.translateToZkmScript(string, new Properties());
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }
}
