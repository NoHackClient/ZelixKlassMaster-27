package com.zelix;

import com.zelix.klassmaster.archive.ZkmFileUtils;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.config.SystemPropertiesFileLoader;
import com.zelix.klassmaster.engine.StartupErrorMessages;
import com.zelix.klassmaster.engine.ZkmApiBase;
import com.zelix.klassmaster.exceptions.CorruptHierarchyException;
import com.zelix.klassmaster.license.LicenseExpiredException;
import com.zelix.klassmaster.util.JavaRuntimeVersion;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ZKM extends ZkmApiBase {
    public static void main(String[] stringArray) {
        try {
            boolean bl;
            Object object;
            Object object2;
            block24:
            {
                block23:
                {
                    block21:
                    {
                        block22:
                        {
                            SystemPropertiesFileLoader.loadSystemProperties();
                            if (!HiddenOptionFlags.DEBUG_REDIRECT_OUT) break block23;
                            object2 = null;
                            try {
                                object = new File(ZkmFileUtils.USER_DIR_FILE, "RedirectedOut_" + System.currentTimeMillis() + ".txt");
                                System.out.println("REDIRECTING System.out to '" + ((File) object).getAbsolutePath() + "'" + '\u0007');
                                object2 = new PrintStream(new BufferedOutputStream(new FileOutputStream((File) object), 4096), true);
                                ZkmApiBase.savedStdout = System.out;
                                System.setOut((PrintStream) object2);
                                ((PrintStream) object2).close();
                            } catch (IOException iOException) {
                                System.setOut(ZkmApiBase.savedStdout);
                                iOException.printStackTrace();
                                System.exit(1);
                                break block21;
                            } finally {
                                if (object2 != null) {
                                    ((PrintStream) object2).close();
                                }
                                break block22;
                            }


                        }
                        bl = HiddenOptionFlags.TEST_JVM_VERSION;
                        break block24;
                    }


                }
                bl = HiddenOptionFlags.TEST_JVM_VERSION;
            }
            if (bl && JavaRuntimeVersion.getRuntimeClassMajorVersion() < 52) {
                System.err.println(StartupErrorMessages.LINE_SEPARATOR + StartupErrorMessages.JVM_VERSION_ERROR + StartupErrorMessages.LINE_SEPARATOR);
                System.exit(1);
            }
            try {
                Class.forName("java.security.MessageDigest");
            } catch (ClassNotFoundException classNotFoundException) {
                System.err.println(StartupErrorMessages.LINE_SEPARATOR + StartupErrorMessages.MESSAGE_DIGEST_MISSING_ERROR + StartupErrorMessages.LINE_SEPARATOR);
                System.exit(1);
            }
            object2 = new ObservableHolder();
            try {
                object = new Class[]{String[].class, ObservableHolder.class};
                Constructor<?> constructor = Class.forName(ZkmUtils.decodeHiddenString()).getConstructor((Class<?>[]) object);
                Object[] objectArray = new Object[]{stringArray, object2};
                constructor.newInstance(objectArray);
            } catch (InstantiationException instantiationException) {
                System.err.println(StartupErrorMessages.LINE_SEPARATOR + "Unexpected Error (A). Please report the problem to " + "bugs2@zelix.com" + StartupErrorMessages.LINE_SEPARATOR);
                System.exit(1);
            } catch (IllegalAccessException illegalAccessException) {
                System.err.println(StartupErrorMessages.LINE_SEPARATOR + "Unexpected Error (B). Please report the problem to " + "bugs2@zelix.com" + StartupErrorMessages.LINE_SEPARATOR);
                System.exit(1);
            } catch (NoClassDefFoundError noClassDefFoundError) {
                PrintStream printStream;
                String string;
                block27:
                {
                    block26:
                    {
                        block25:
                        {
                            string = System.getProperty("java.class.path");
                            if (string == null) break block25;
                            if (string.indexOf("ZKM.jar") == -1) break block26;
                            if (ZkmApiBase.isZkmJarInClasspath(string)) {
                                System.err.println(StartupErrorMessages.LINE_SEPARATOR + "Unexpected Error (C). Please report the problem to " + "bugs2@zelix.com" + StartupErrorMessages.LINE_SEPARATOR);
                                printStream = System.err;
                            } else {
                                System.err.println("Startup Error. (ZKM.jar not found where specified by classpath)");
                                printStream = System.err;
                            }
                            break block27;
                        }
                        System.err.println("Startup Error. (Check that ZKM.jar is in classpath) (2)");
                        printStream = System.err;
                        break block27;
                    }
                    System.err.println("Startup Error. (Check that ZKM.jar is in classpath) (1)");
                    printStream = System.err;
                }
                printStream.println("Classpath is: \"" + string + "\"");
                System.exit(1);
            } catch (InvocationTargetException invocationTargetException) {
                Throwable throwable;
                block29:
                {
                    block30:
                    {
                        block28:
                        {
                            throwable = invocationTargetException.getTargetException();
                            if (throwable instanceof LicenseExpiredException) break block28;
                            if (!(throwable instanceof CorruptHierarchyException)) break block29;
                            PrintWriter printWriter = (PrintWriter) ((ObservableHolder) object2).getValue();
                            System.err.println(StartupErrorMessages.LINE_SEPARATOR + "Unexpected Error (D). Please report the problem to " + "bugs2@zelix.com" + StartupErrorMessages.LINE_SEPARATOR);
                            System.err.println(StartupErrorMessages.LINE_SEPARATOR + throwable.getMessage() + StartupErrorMessages.LINE_SEPARATOR);
                            printWriter.println(StartupErrorMessages.LINE_SEPARATOR + "Unexpected Error (D). Please report the problem to " + "bugs2@zelix.com");
                            printWriter.println(StartupErrorMessages.LINE_SEPARATOR + throwable.getMessage() + StartupErrorMessages.LINE_SEPARATOR);
                            Throwable throwable2 = throwable.getCause();
                            if (throwable2 == null) break block30;
                            throwable2.printStackTrace(printWriter);
                            break block30;
                        }
                        System.err.println(StartupErrorMessages.LINE_SEPARATOR + "Evaluation has expired : " + throwable.getMessage() + StartupErrorMessages.LINE_SEPARATOR);
                        System.exit(1);
                    }
                    System.exit(1);
                }
                System.err.println(StartupErrorMessages.LINE_SEPARATOR + "Unexpected Error (D). Please report the problem to " + "bugs2@zelix.com" + StartupErrorMessages.LINE_SEPARATOR);
                ZkmApiBase.printTrimmedStackTrace(throwable, (PrintWriter) ((ObservableHolder) object2).getValue());
                System.exit(1);
            } catch (NoSuchMethodException noSuchMethodException) {
                System.err.println(StartupErrorMessages.LINE_SEPARATOR + "Unexpected Error (E). Please report the problem to " + "bugs2@zelix.com" + StartupErrorMessages.LINE_SEPARATOR);
                System.exit(1);
            } catch (Throwable throwable) {
                System.err.println(StartupErrorMessages.LINE_SEPARATOR + "Unexpected Error (G). Please report the problem to " + "bugs2@zelix.com" + StartupErrorMessages.LINE_SEPARATOR);
                ZkmApiBase.printTrimmedStackTrace(throwable, (PrintWriter) ((ObservableHolder) object2).getValue());
                System.exit(1);
            }
            return;
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }

    private ZKM() {
    }
}
