package com.zelix;

import com.zelix.klassmaster.archive.TempFileManager;
import com.zelix.klassmaster.changelog.StackTraceTranslator;
import com.zelix.klassmaster.classfile.hierarchy.ZkmClasspath;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.engine.JvmVersionMessages;
import com.zelix.klassmaster.engine.ZkmApiBase;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.JavaRuntimeVersion;
import com.zelix.klassmaster.util.ZkmAssert;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Set;
import java.util.StringTokenizer;

public class ZKMStackTraceTranslate extends JvmVersionMessages {
    public static final int NO_PARAM_TYPES = 1;
    public static final int FULL_PARAM_TYPES = 3;
    public static final int UNQUALIFIED_PARAM_TYPES = 2;
    public ArrayList changeLogFileNames;
    public Set classpathErrorPaths;
    public StackTraceTranslator translator;

    public ZKMStackTraceTranslate(String[] strings) {
        this(strings, null);
    }

    public String getTranslatedStackTrace(String string, boolean bl) {
        return this.getTranslatedStackTrace(string, bl, 2);
    }

    public ZKMStackTraceTranslate(String string) {
        this(string, null);
    }

    public ZKMStackTraceTranslate(String[] strings, String string) {
        this.mandatoryContruction(strings, null);
    }

    public static void launchTranslator() {
        if (HiddenOptionFlags.TEST_JVM_VERSION && JavaRuntimeVersion.getRuntimeClassMajorVersion() < 52) {
            System.err.println(JvmVersionMessages.LINE_SEPARATOR + JvmVersionMessages.JVM_VERSION_ERROR + JvmVersionMessages.LINE_SEPARATOR);
            System.exit(1);
        }

        try {
            ZkmApiBase.showStackTraceTranslate();
        } catch (IllegalAccessException illegalAccessException) {
            System.err
                    .println(
                            JvmVersionMessages.LINE_SEPARATOR
                                    + "Unexpected Error [B]. Please report the problem to "
                                    + "bugs2@zelix.com"
                                    + JvmVersionMessages.LINE_SEPARATOR
                    );
        } catch (NoClassDefFoundError noClassDefFoundError) {
            String string = System.getProperty("java.class.path");
            if (string != null) {
                if (string.indexOf("ZKMTranslate.jar") == -1) {
                    System.err.println("Startup Error. (Check that ZKMTranslate.jar is in classpath) (1)");
                } else if (isTranslateJarOnClasspath(string)) {
                    System.err
                            .println(
                                    JvmVersionMessages.LINE_SEPARATOR
                                            + "Unexpected Error [C]. Please report the problem to "
                                            + "bugs2@zelix.com"
                                            + JvmVersionMessages.LINE_SEPARATOR
                            );
                } else {
                    System.err.println("Startup Error. (ZKMTranslate.jar not found where specified by classpath)");
                }
            } else {
                System.err.println("Startup Error. (Check that ZKMTranslate.jar is in classpath) (2)");
            }

            System.err.println("Classpath is: \"" + string + "\"");
        } catch (InvocationTargetException invocationTargetException) {
            Throwable throwable = invocationTargetException.getTargetException();
            System.err
                    .println(
                            JvmVersionMessages.LINE_SEPARATOR
                                    + "Unexpected Error [D]. Please report the problem to "
                                    + "bugs2@zelix.com"
                                    + JvmVersionMessages.LINE_SEPARATOR
                    );
            throwable.printStackTrace(System.err);
        } catch (NoSuchMethodException noSuchMethodException) {
            System.err
                    .println(
                            JvmVersionMessages.LINE_SEPARATOR
                                    + "Unexpected Error [E]. Please report the problem to "
                                    + "bugs2@zelix.com"
                                    + JvmVersionMessages.LINE_SEPARATOR
                    );
        } catch (Throwable throwable1) {
            System.err
                    .println(
                            JvmVersionMessages.LINE_SEPARATOR
                                    + "Unexpected Error [G]. Please report the problem to "
                                    + "bugs2@zelix.com"
                                    + JvmVersionMessages.LINE_SEPARATOR
                    );
            throwable1.printStackTrace(System.err);
        }
    }

    public String getOldClassName(String string) {
        try {
            String string1;
            try {
                string1 = this.translator.translateClassName(string);
            } catch (ZkmException zkmException) {
                string1 = "ZKM ERROR (2): \"" + zkmException.getMessage() + "\"" + JvmVersionMessages.LINE_SEPARATOR;
            }

            return string1;
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }

    public String getTranslatedStackTrace(String string, boolean bl, int ba) {
        try {
            String string1;
            try {
                string1 = this.translator.translate(string, bl, ba);
            } catch (ZkmException zkmException) {
                String string2 = null;
                if (this.classpathErrorPaths != null && this.classpathErrorPaths.size() > 0) {
                    string2 = ZkmUtils.toQuotedListString(ZkmUtils.describeTempFileNames(this.classpathErrorPaths));
                }

                string1 = "ZKM ERROR (1): \""
                        + zkmException.getMessage()
                        + "\""
                        + JvmVersionMessages.LINE_SEPARATOR
                        + (string2 != null ? "Initial classpath paths in error : " + string2 + ZkmAssert.lineSeparator : "");
            }

            return string1;
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }

    public String getOldMethodName(String string, String string1) {
        try {
            try {
                string1 = string1.trim();
                int ba = string1.indexOf(" ");
                if (ba <= -1) {
                    return "'" + string1 + "' is not a valid method signature (3)";
                }

                String string2 = string1.substring(0, ba);
                String string3 = string1.substring(ba + 1);
                string3 = string3.trim();
                int bb = string3.indexOf("(");
                if (bb <= 0) {
                    return "'" + string1 + "' is not a valid method signature (2)";
                }

                String string4 = string3.substring(0, bb);
                int bc = string3.indexOf(")", bb);
                if (bc <= -1) {
                    return "'" + string1 + "' is not a valid method signature (1)";
                }

                String string5 = string3.substring(bb + 1, bc);
                ArrayList arrayList = new ArrayList();
                StringTokenizer stringTokenizer = new StringTokenizer(string5, ",");

                while (stringTokenizer.hasMoreTokens()) {
                    arrayList.add(stringTokenizer.nextToken().trim());
                }

                String[] strings = new String[arrayList.size()];
                strings = ((java.lang.String[]) (arrayList.toArray(strings)));
                return this.translator.getOriginalMethodName(string, string4, strings, string2);
            } catch (ZkmException zkmException) {
                return "ZKM ERROR (5): \"" + zkmException.getMessage() + "\"" + JvmVersionMessages.LINE_SEPARATOR;
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }

    public static boolean isTranslateJarOnClasspath(String string) {
        StringTokenizer stringTokenizer = new StringTokenizer(string, JvmVersionMessages.PATH_SEPARATOR);

        while (stringTokenizer.hasMoreTokens()) {
            String string1 = stringTokenizer.nextToken();
            if (string1.endsWith("ZKMTranslate.jar") && new File(string1).exists()) {
                return true;
            }
        }

        return false;
    }

    public void close() {
        if (this.translator != null) {
            this.translator.dispose();
        }

        TempFileManager.deleteStaleTempFiles();
    }

    public void mandatoryContruction(String[] strings, String string1) {
        ZkmApiBase.isZkmJarInClasspath(null);
        this.changeLogFileNames = new ArrayList(strings.length);
        int ba = 0;

        for (String string : strings) {
            if (string == null) {
                throw new IllegalArgumentException("Change log file name " + ba + " must not be null");
            }

            this.changeLogFileNames.add(string);
            ba++;
        }

        Object object = null;
        this.translator = new StackTraceTranslator(this.changeLogFileNames, (ZkmClasspath) object);
    }

    public String getOldMethodName(String string, String string1, String[] strings, String string2) {
        try {
            try {
                return this.translator.getOriginalMethodName(string, string1, strings, string2);
            } catch (ZkmException zkmException) {
                return "ZKM ERROR (4): \"" + zkmException.getMessage() + "\"" + JvmVersionMessages.LINE_SEPARATOR;
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }

    public String getOldMethodSignatures(String string, String string1) {
        try {
            String string2;
            try {
                String[] strings = this.translator.getOriginalMethodSignatures(string, string1);
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("[");

                for (int i = 0; i < strings.length; i++) {
                    stringBuffer.append(strings[i]);
                    if (i < strings.length - 2) {
                        stringBuffer.append(", ");
                    } else if (i < strings.length - 1) {
                        stringBuffer.append(" or ");
                    }
                }

                stringBuffer.append("]");
                string2 = stringBuffer.toString();
            } catch (ZkmException zkmException) {
                string2 = "ZKM ERROR (3): \"" + zkmException.getMessage() + "\"" + JvmVersionMessages.LINE_SEPARATOR;
            }

            return string2;
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }

    public static void main(String[] strings) {
        TempFileManager.deleteStaleTempFiles();
        launchTranslator();
    }

    public ZKMStackTraceTranslate(String string, String string1) {
        if (string == null) {
            throw new IllegalArgumentException("Change log file name must not be null");
        }

        String[] strings = new String[]{string};
        this.mandatoryContruction(strings, null);
    }

    public String getTranslatedStackTrace(String string) {
        return this.getTranslatedStackTrace(string, true, 2);
    }
}
