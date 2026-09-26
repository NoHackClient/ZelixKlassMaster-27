package com.zelix.klassmaster.config;

import com.zelix.klassmaster.util.ZkmUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

public class SystemPropertiesFileLoader {
    
    
    public static void loadSystemProperties() {
        try {
            Map<String, String> map1 = System.getenv();
            if (ZkmUtils.getHashedMapOrSystemProperty(
                            map1,
                            "1959b123224b9911f43aabc5015503e06cfb8cb223f861b863a0f371d47e95315e2fb40c4b7881ea46e13f51de29a1f4033898ee2338f7311fd58e583912e51a",
                            "false"
                    )
                    .equals("true")) {
                Iterator iterator = map1.entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry entry = (Entry) iterator.next();
                    if (System.getProperty((String) entry.getKey()) == null) {
                    }
                }
            }
        } catch (SecurityException securityException) {
        }

        File file1 = new File("ZKM_System.properties");
        if (file1.exists() && !file1.isDirectory()) {
            try {
                FileReader fileReader = new FileReader(file1);
                Throwable throwable2 = null;
                boolean bl = false ;

                try {
                    bl = true;
                    Properties properties1 = new Properties();
                    properties1.load(fileReader);
                    Iterator<String> iterator1 = properties1.stringPropertyNames().iterator();

                    while (iterator1.hasNext()) {
                        String string = iterator1.next();
                        System.setProperty(string, properties1.getProperty(string));
                    }

                    bl = false;
                } catch (Throwable throwable1) {
                    throwable2 = throwable1;
                    throw throwable1;
                } finally {
                    if (bl) {
                        if (throwable2 != null) {
                            try {
                                fileReader.close();
                            } catch (Throwable throwable) {
                                throwable2.addSuppressed(throwable);
                            }
                        } else {
                            fileReader.close();
                        }
                    }
                }

                fileReader.close();
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            } catch (IOException iOException) {
                iOException.printStackTrace();
            }
        }
    }

    private SystemPropertiesFileLoader() {
    }
}
