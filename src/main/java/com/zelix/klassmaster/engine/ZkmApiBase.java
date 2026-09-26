package com.zelix.klassmaster.engine;

import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.config.SystemPropertiesFileLoader;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmRuntimeException;
import com.zelix.klassmaster.license.LicenseExpiredException;
import com.zelix.klassmaster.util.JavaRuntimeVersion;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

public abstract class ZkmApiBase extends StartupErrorMessages {
    public static PrintStream savedStdout;
    public static PrintStream savedStderr;

    public static void run(
            String string, String string1, String string2, String string3, String string4, String string5, boolean bl, boolean bl1, Hashtable hashtable
    ) throws Exception {
        Properties properties1 = toProperties(hashtable);
        run(string, string1, string2, string3, string4, (String) null, (String) null, string5, bl, bl1, properties1);
    }

    public static void run(
            String string,
            String string1,
            String string2,
            String string3,
            String string4,
            String string5,
            String string6,
            String string7,
            String string8,
            String string9,
            boolean bl,
            boolean bl1,
            Properties properties1
    ) throws Exception {
        run(string, string1, string2, string3, string4, string5, string6, string7, string8, string9, bl, bl1, properties1, false);
    }

    public static void run(
            String string, String string1, String string2, String string3, String string4, String string5, String string6, String string7, boolean bl, boolean bl1
    ) throws Exception {
        run(string, string1, string2, string3, string4, string5, string6, string7, bl, bl1, (Properties) null);
    }

    public static void showStackTraceTranslate() throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Lookup lookup = MethodHandles.lookup();
        Class<?> class1 = Class.forName(ZkmUtils.decodeHiddenString());
        Object object = class1.newInstance();
        Class[] class2 = new Class[0];
        MethodType methodType1 = MethodType.methodType(void.class, class2);
        methodType1.parameterArray();
        try {
            try {
                lookup.findVirtual(class1, "showStackTraceTranslate", methodType1).invoke(object);
            } catch (Throwable throwable) {
                throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public static void run(File file1, String string, File file2, String string1, String string2) throws ZkmException, IOException {
        if (file2 == null) {
            throw new IllegalArgumentException("ZKM: Null output JAR file.");
        }

        if (string == null || string.length() <= 4) {
            throw new IllegalArgumentException("ZKM: Invalid input JAR file name: '" + string + "'");
        }

        if (string2 == null || string2.length() < 1) {
            throw new IllegalArgumentException("ZKM: Invalid project directory name: '" + string2 + "'");
        }

        if (string1 != null && string1.length() != 0) {
            SystemPropertiesFileLoader.loadSystemProperties();
            ObservableHolder observableHolder = new ObservableHolder();

            try {
                Class[] class1 = new Class[]{File.class, String.class, File.class, String.class, String.class, boolean.class, ObservableHolder.class};
                Constructor<?> constructor = Class.forName(ZkmUtils.decodeHiddenString()).getConstructor(class1);
                Object[] objects = new Object[]{file1, string, file2, string1, string2, Boolean.TRUE, observableHolder};
                constructor.newInstance(objects);
            } catch (InstantiationException instantiationException) {
                System.err.println("ZKM: Unexpected Error (A). Please report the problem to bugs2@zelix.com");
            } catch (IllegalAccessException illegalAccessException) {
                System.err.println("ZKM: Unexpected Error (B). Please report the problem to bugs2@zelix.com");
            } catch (NoClassDefFoundError noClassDefFoundError) {
                System.err.println("ZKM: Classpath is: \"" + string1 + "\"");
                System.err.println("ZKM: Unexpected Error (C). Please report the problem to bugs2@zelix.com");
            } catch (InvocationTargetException invocationTargetException) {
                Throwable throwable = invocationTargetException.getTargetException();
                if (throwable instanceof LicenseExpiredException) {
                    System.err.println("ERROR: ZKM Evaluation has expired: " + throwable.getMessage());
                } else if (throwable instanceof ZkmRuntimeException) {
                    System.err.println("ZKM: " + throwable.getMessage());
                } else {
                    printTrimmedStackTrace(throwable, (PrintWriter) observableHolder.getValue());
                    System.err.println("ZKM: Unexpected Error (D). Please report the problem to bugs2@zelix.com");
                }
            } catch (NoSuchMethodException noSuchMethodException) {
                System.err.println("ZKM: Unexpected Error (E). Please report the problem to bugs2@zelix.com");
            } catch (Throwable throwable1) {
                printTrimmedStackTrace(throwable1, (PrintWriter) observableHolder.getValue());
                System.err.println("ZKM: Unexpected Error (F). Please report the problem to bugs2@zelix.com");
            }
        } else {
            throw new IllegalArgumentException("ZKM: Invalid classpath: '" + string1 + "'");
        }
    }

    public static void printTrimmedStackTrace(Throwable throwable, PrintWriter printWriter) {
        String string = ZkmUtils.stackTraceToString(throwable);
        StringTokenizer stringTokenizer = new StringTokenizer(string, StartupErrorMessages.LINE_SEPARATOR);
        int ba = stringTokenizer.countTokens();
        int bb;
        if (ba > 5) {
            bb = ba - 2;
        } else {
            bb = ba - 1;
        }

        if (ba == 1) {
            String string1 = stringTokenizer.nextToken();
            System.err.println(string1);
            if (printWriter != null) {
                printWriter.println(string1);
            }
        } else {
            for (int i = 0; i < ba; i++) {
                String string2 = stringTokenizer.nextToken();
                if (i < bb) {
                    System.err.println(string2);
                    if (printWriter != null) {
                        printWriter.println(string2);
                    }
                }
            }
        }
    }

    public static void runWithProperties(
            String string, String string1, String string2, String string3, String string4, String string5, boolean bl, boolean bl1, Properties properties1
    ) throws Exception {
        run(string, string1, string2, string3, string4, (String) null, (String) null, string5, (String) null, (String) null, bl, bl1, properties1);
    }

    public static void run(String string, Map map1) throws Exception {
        Properties properties1 = toProperties(map1);
        String string1 = (String) null;
        String string2 = (String) null;
        String string3 = (String) null;
        String string4 = (String) null;
        String string5 = (String) null;
        String string6 = (String) null;
        String string7 = (String) null;
        String string8 = (String) null;
        String string9 = (String) null;
        Properties properties2 = properties1;
        run(string, string1, string2, string3, string4, string5, string6, string7, string8, string9, true, false, properties2, true);
    }

    public static boolean isZkmJarInClasspath(String string) {
        if (string == null) {
            return false;
        }

        StringTokenizer stringTokenizer = new StringTokenizer(string, StartupErrorMessages.PATH_SEPARATOR);

        while (stringTokenizer.hasMoreTokens()) {
            String string1 = stringTokenizer.nextToken();
            if (string1.endsWith("ZKM.jar") && new File(string1).exists()) {
                return true;
            }
        }

        return false;
    }

    public static void runWithHashtable(String string, String string1, boolean bl, boolean bl1, Hashtable hashtable) throws Exception {
        Properties properties1 = toProperties(hashtable);
        run(string, string1, (String) null, (String) null, (String) null, (String) null, (String) null, (String) null, bl, bl1, properties1);
    }

    public static void run(String string, String string1, boolean bl, boolean bl1) throws Exception {
        runWithHashtable(string, string1, bl, bl1, (Properties) null);
    }

    public static Properties toProperties(Map map1) {
        Properties properties1 = null;
        if (map1 != null) {
            properties1 = new Properties();
            properties1.putAll(map1);
        }

        return properties1;
    }

    public static void run(
            String string,
            String string1,
            String string2,
            String string3,
            String string4,
            String string5,
            String string6,
            String string7,
            boolean bl,
            boolean bl1,
            Properties properties1
    ) throws Exception {
        run(string, string1, string2, string3, string4, string5, string6, string7, (String) null, (String) null, bl, bl1, properties1);
    }

    public static void run(
            String string,
            String string1,
            String string2,
            String string3,
            String string4,
            String string5,
            String string6,
            String string7,
            String string8,
            String string9,
            boolean bl,
            boolean bl1,
            Properties properties1,
            boolean bl2
    ) throws Exception {
        SystemPropertiesFileLoader.loadSystemProperties();
        if (HiddenOptionFlags.TEST_JVM_VERSION && JavaRuntimeVersion.getRuntimeClassMajorVersion() < 52) {
            System.err.println(StartupErrorMessages.LINE_SEPARATOR + StartupErrorMessages.JVM_VERSION_ERROR + StartupErrorMessages.LINE_SEPARATOR);
            System.exit(1);
        }

        ObservableHolder observableHolder = new ObservableHolder();

        try {
            if (string8 != null && string8.trim().length() > 0) {
                PrintStream printStream = new PrintStream(new FileOutputStream(string8), true);
                savedStdout = System.out;
                System.setOut(printStream);
            }

            if (string9 != null && string9.trim().length() > 0 && savedStdout != null) {
                PrintStream printStream1;
                PrintStream printStream2;
                if (!new File(string8).getAbsolutePath().equals(new File(string9).getAbsolutePath())) {
                    printStream1 = new PrintStream(new FileOutputStream(string9), true);
                    printStream2 = System.err;
                } else {
                    printStream1 = System.out;
                    printStream2 = System.err;
                }

                savedStderr = printStream2;
                System.setErr(printStream1);
            }

            Class[] class1;
            Object[] objects;
            if (bl2) {
                class1 = new Class[]{String.class, Properties.class, ObservableHolder.class};
                objects = new Object[]{string, properties1, observableHolder};
            } else {
                class1 = new Class[]{
                        String.class,
                        String.class,
                        String.class,
                        String.class,
                        String.class,
                        String.class,
                        String.class,
                        String.class,
                        boolean.class,
                        boolean.class,
                        Properties.class,
                        ObservableHolder.class
                };
                objects = new Object[]{
                        string,
                        string1,
                        string2,
                        string3,
                        string4,
                        string5,
                        string6,
                        string7,
                        bl ? Boolean.TRUE : Boolean.FALSE,
                        bl1 ? Boolean.TRUE : Boolean.FALSE,
                        properties1,
                        observableHolder
                };
            }

            Class.forName(ZkmUtils.decodeHiddenString()).getConstructor(class1).newInstance(objects);
        } catch (InstantiationException instantiationException) {
            throw new Exception("ZKM: Unexpected Error (A). Please report the problem to bugs2@zelix.com");
        } catch (IllegalAccessException illegalAccessException) {
            throw new Exception("ZKM: Unexpected Error (B). Please report the problem to bugs2@zelix.com");
        } catch (NoClassDefFoundError noClassDefFoundError) {
            String string10 = System.getProperty("java.class.path");
            System.err.println("ZKM: Classpath is: \"" + string10 + "\"");
            if (string10 != null) {
                if (string10.indexOf("ZKM.jar") == -1) {
                    throw new Exception("ZKM: Startup Error. (Check that ZKM.jar is in classpath) (1)");
                }

                if (isZkmJarInClasspath(string10)) {
                    throw new Exception("ZKM: Unexpected Error (C). Please report the problem to bugs2@zelix.com");
                }

                throw new Exception("ZKM: Startup Error. (ZKM.jar not found where specified by classpath)");
            }

            throw new Exception("ZKM: Startup Error. (Check that ZKM.jar is in classpath) (2)");
        } catch (InvocationTargetException invocationTargetException) {
            Throwable throwable = invocationTargetException.getTargetException();
            if (throwable instanceof LicenseExpiredException) {
                throw new Exception("ZKM Evaluation has expired: " + throwable.getMessage());
            }

            if (throwable instanceof ZkmRuntimeException) {
                throw new Exception("ZKM: " + throwable.getMessage());
            }

            printTrimmedStackTrace(throwable, (PrintWriter) observableHolder.getValue());
            throw new Exception("ZKM: Unexpected Error (D). Please report the problem to bugs2@zelix.com");
        } catch (NoSuchMethodException noSuchMethodException) {
            throw new Exception("ZKM: Unexpected Error (E). Please report the problem to bugs2@zelix.com");
        } catch (Throwable throwable1) {
            printTrimmedStackTrace(throwable1, (PrintWriter) observableHolder.getValue());
            throw new Exception("ZKM: Unexpected Error (F). Please report the problem to bugs2@zelix.com");
        } finally {
            PrintStream printStream3;
            if (savedStderr != null) {
                if (savedStderr != System.err) {
                    System.setErr(savedStderr);
                    printStream3 = savedStdout;
                } else {
                    printStream3 = savedStdout;
                }
            } else {
                printStream3 = savedStdout;
            }

            if (printStream3 != null && savedStdout != System.out) {
                System.setOut(savedStdout);
            }
        }
    }

    public static void runWithHashtable(
            String string,
            String string1,
            String string2,
            String string3,
            String string4,
            String string5,
            String string6,
            String string7,
            boolean bl,
            boolean bl1,
            Hashtable hashtable
    ) throws Exception {
        Properties properties1 = toProperties(hashtable);
        run(string, string1, string2, string3, string4, string5, string6, string7, bl, bl1, properties1);
    }

    protected ZkmApiBase() {
    }
}
