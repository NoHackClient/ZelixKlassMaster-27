package com.zelix.klassmaster;

import com.zelix.klassmaster.archive.ArchiveZipFile;
import com.zelix.klassmaster.archive.TempFileManager;
import com.zelix.klassmaster.archive.ZkmFileUtils;
import com.zelix.klassmaster.classfile.hierarchy.ClassPathResolver;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.classfile.hierarchy.ZkmClasspath;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.AssertionFailedException;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.exceptions.ZkmRuntimeException;
import com.zelix.klassmaster.license.LicenseDetails;
import com.zelix.klassmaster.license.LicenseExpiredException;
import com.zelix.klassmaster.license.ObfuscatedLongDecoder;
import com.zelix.klassmaster.log.ConsoleLogReporter;
import com.zelix.klassmaster.log.ConsoleMessageReporter;
import com.zelix.klassmaster.log.LoadLogWriterTask;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;
import com.zelix.klassmaster.proguard.ProGuardInputTranslator;
import com.zelix.klassmaster.proguard.ProGuardOptionNames;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.ScriptPreprocessor;
import com.zelix.klassmaster.script.ZkmScriptTokenMgrError;
import com.zelix.klassmaster.script.parser.ZkmScriptParseException;
import com.zelix.klassmaster.script.parser.ZkmScriptParser;
import com.zelix.klassmaster.script.parser.ast.ZkmScriptASTInput;
import com.zelix.klassmaster.ui.StartupDialogCallback;
import com.zelix.klassmaster.ui.ZkmMainWindow;
import com.zelix.klassmaster.ui.component.SwingUtils;
import com.zelix.klassmaster.ui.dialog.EvaluationLicenseDialog;
import com.zelix.klassmaster.util.BooleanFlag;
import com.zelix.klassmaster.util.NoOpCallback;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.SystemEnvironmentConstants;
import com.zelix.klassmaster.util.ZkmStringUtils;
import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.swing.UIManager;
import java.io.StringReader;

public class KlassMaster extends LicenseDetails {
    public static Font defaultFont;
    public static PrintStream savedStdout;
    public static String validatedExpiryCode;
    
    public static String evaluationMarker = "__evaluation_license_marker__";
    public static String defaultMidletScript = "trimExclude public *.* extends javax.microedition.midlet.MIDlet;"
            + HiddenOptionFlags.LINE_SEPARATOR
            + HiddenOptionFlags.LINE_SEPARATOR
            + "trim        deleteSourceFileAttributes=true"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "            deleteAnnotationAttributes=true"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "            deleteDeprecatedAttributes=true"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "            deleteUnknownAttributes=false;"
            + HiddenOptionFlags.LINE_SEPARATOR
            + HiddenOptionFlags.LINE_SEPARATOR
            + "exclude     public *.^* extends javax.microedition.midlet.MIDlet;"
            + HiddenOptionFlags.LINE_SEPARATOR
            + HiddenOptionFlags.LINE_SEPARATOR
            + "obfuscate   changeLogFileOut=\"ChangeLog.txt\""
            + HiddenOptionFlags.LINE_SEPARATOR
            + "            obfuscateFlow=aggressive"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "            encryptStringLiterals=none"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "            collapsePackagesWithDefault=\"\""
            + HiddenOptionFlags.LINE_SEPARATOR
            + "            exceptionObfuscation=light"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "            lineNumbers=delete;";
    public static long activeInstanceId = -1L;
    public static final String EVALUATION_SUFFIX = "";
    public String[] bannerLines;
    public String bannerTitle;

    public boolean containsUncommentedText(final String s) {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new StringReader(s));
            String s2;
            while ((s2 = bufferedReader.readLine()) != null) {
                final int index = s2.indexOf("//");
                if (index > -1) {
                    s2 = s2.substring(0, index);
                }
                if (s2.indexOf("-injars") > -1) {
                    try {
                        bufferedReader.close();
                    } catch (final IOException ex) {
                    }
                    return true;
                }
            }
            try {
                bufferedReader.close();
            } catch (final IOException ex2) {
            }
        } catch (final IOException ex3) {
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (final IOException ex4) {
                }
            }
        }
        return false;
    }

    public static Image getLogoImage(Component component1) {
        String string;
        if (!SystemEnvironmentConstants.OS_NAME.equals("SunOS")
                && !SystemEnvironmentConstants.OS_NAME.equals("Solaris")
                && !SystemEnvironmentConstants.OS_NAME.equals("HP_UX")) {
            string = "2.gif";
        } else {
            string = "3.gif";
        }

        return component1.getToolkit().getImage(HiddenOptionFlags.USER_PREFERENCES.getClass().getResource(string));
    }

    public void printUsageAndFail() {
        this.printUsage(System.err);
        System.exit(1);
    }

    public void printBanner(PrintStream printStream) {
        printStream.println(this.bannerTitle);
        if (this.bannerLines != null) {
            int ba = 0;
            int bb = 0;

            for (String[] strings = this.bannerLines; bb < strings.length; strings = this.bannerLines) {
                String string = this.bannerLines[ba];
                printStream.println(ZkmStringUtils.pad(
                        string, 82, string.length() + ZkmUtils.getBracketedTimestamp().length() + 1, 32
                ));
                bb = ++ba;
            }
        }
    }

    @Override
    public String getLicenseField(Object object1, Object object) {
        int ba = (Integer) object;
        String string = this.getLicenseToken();
        return "58#\\u0001\\u00031*\\u0001<\\u000erm".equals(string) ? this.getRawLicenseField(ba).trim() : null;
    }

    public void reportFatalError(String string, boolean bl) {
        System.err.println("ERROR: " + string);
        if (bl) {
            System.exit(1);
        } else {
            throw new RuntimeException("ERROR: " + string);
        }
    }

    public void printUsageAndExit() {
        this.printUsage(System.out);
        System.exit(0);
    }

    public static String filterExistingPaths(String string) {
        StringBuffer stringBuffer = new StringBuffer();
        StringTokenizer stringTokenizer = new StringTokenizer(string, SystemEnvironmentConstants.PATH_SEPARATOR_String);

        while (stringTokenizer.hasMoreTokens()) {
            String string1 = stringTokenizer.nextToken();
            if (new File(string1).exists()) {
                if (stringBuffer.length() > 0) {
                    stringBuffer.append(SystemEnvironmentConstants.PATH_SEPARATOR_String);
                }

                stringBuffer.append(string1);
            }
        }

        return stringBuffer.toString();
    }

    public static String getSavedClasspath() {
        String string = HiddenOptionFlags.USER_PREFERENCES.getClasspath();
        if (string != null && string.length() > 0) {
            string = filterExistingPaths(string);
        } else {
            string = System.getProperty("java.class.path");
        }

        return string;
    }

    public KlassMaster(String[] strings, ObservableHolder observableHolder) throws ZkmException, IOException {
        acquireInstanceLock();
        ObservableHolder observableHolder1 = new ObservableHolder();
        if (strings.length == 0) {
            this.startGui(observableHolder);
        } else if (!HiddenOptionFlags.PROGUARD_STYLE && !this.isProGuardStyleInput(strings, observableHolder1)) {
            String string9 = null;
            String string1 = null;
            String string2 = null;
            String string3 = null;
            String string4 = null;
            String string5 = null;
            String string6 = null;
            String string7 = null;
            boolean bl = false;
            boolean bl1 = false;
            String string8 = null;

            for (int i = 0; i < strings.length; i++) {
                if (i == strings.length - 1) {
                    if (strings[i].equalsIgnoreCase("-?")) {
                        this.printUsageAndExit();
                    } else if (strings[i].startsWith("-")) {
                        this.failWithUsage("\"" + strings[i] + "\" is not a valid ZKM Script file name");
                    }

                    string9 = strings[i];
                } else if (strings[i].equalsIgnoreCase("-p")) {
                    bl = true;
                } else if (strings[i].equalsIgnoreCase("-v")) {
                    bl1 = true;
                } else if (strings[i].equalsIgnoreCase("-?")) {
                    this.printUsageAndExit();
                } else if (strings[i].toLowerCase().startsWith("-l")) {
                    if (++i < strings.length) {
                        string1 = strings[i];
                        if (string1.startsWith("-")) {
                            this.failWithUsage("\"" + string1 + "\" is not a valid log file name");
                        }
                    } else {
                        this.failWithUsage("Missing log file name");
                    }
                } else if (strings[i].toLowerCase().startsWith("-tl")) {
                    if (++i < strings.length) {
                        string2 = strings[i];
                        if (string2.startsWith("-")) {
                            this.failWithUsage("\"" + string2 + "\" is not a valid trim log file name");
                        }
                    } else {
                        this.failWithUsage("Missing trim log file name");
                    }
                } else if (strings[i].toLowerCase().startsWith("-de")) {
                    if (++i < strings.length) {
                        string3 = strings[i];
                        if (string3.startsWith("-")) {
                            this.failWithUsage("\"" + string3 + "\" is not a valid default exclude file name");
                        }
                    } else {
                        this.failWithUsage("Missing default exclude file name");
                    }
                } else if (strings[i].toLowerCase().startsWith("-dte")) {
                    if (++i < strings.length) {
                        string4 = strings[i];
                        if (string4.startsWith("-")) {
                            this.failWithUsage("\"" + string4 + "\" is not a valid default trim exclude file name");
                        }
                    } else {
                        this.failWithUsage("Missing default trim exclude file name");
                    }
                } else if (strings[i].toLowerCase().startsWith("-dpe")) {
                    if (++i < strings.length) {
                        string5 = strings[i];
                        if (string5.startsWith("-")) {
                            this.failWithUsage("\"" + string5 + "\" is not a valid default method parameter changes exclude file name");
                        }
                    } else {
                        this.failWithUsage("Missing default method parameter changes exclude file name");
                    }
                } else if (strings[i].toLowerCase().startsWith("-dpo")) {
                    if (++i < strings.length) {
                        string6 = strings[i];
                        if (string6.startsWith("-")) {
                            this.failWithUsage("\"" + string6 + "\" is not a valid default method parameter obfuscation exclude file name");
                        }
                    } else {
                        this.failWithUsage("Missing default method parameter changes exclude file name");
                    }
                } else if (strings[i].toLowerCase().startsWith("-dd")) {
                    if (++i < strings.length) {
                        string7 = strings[i];
                        if (string7.startsWith("-")) {
                            this.failWithUsage("\"" + string7 + "\" is not a valid 'default directory' name");
                        }
                    } else {
                        this.failWithUsage("Missing 'files directory' name");
                    }
                } else if (strings[i].toLowerCase().startsWith("-ro")) {
                    if (++i < strings.length) {
                        string8 = strings[i];
                        if (string8.startsWith("-")) {
                            this.failWithUsage("\"" + string8 + "\" is not a valid stdout redirect file name");
                        }
                    } else {
                        this.failWithUsage("Missing stdout redirect file name");
                    }
                } else {
                    this.failWithUsage("\"" + strings[i] + "\" is not a valid parameter");
                }
            }

            if (string9 == null) {
                this.failWithUsage("Missing ZKM Script file name");
            }

            Integer integer = 3;
            String string10 = this.getLicenseField(30828586125445L, integer);
            boolean bl2 = string10.indexOf(evaluationMarker) != -1;
            ObservableHolder observableHolder2 = new ObservableHolder();
            ObservableHolder observableHolder3 = new ObservableHolder();

            try {
                if (string8 != null) {
                    redirectStdout(string8);
                }

                Object object = null;
                Boolean boolean5 = false;
                ObservableHolder observableHolder7 = observableHolder;
                Boolean boolean4 = false;
                Boolean boolean3 = bl2;
                Boolean boolean2 = false;
                ObservableHolder observableHolder6 = observableHolder3;
                ObservableHolder observableHolder5 = observableHolder2;
                this.executeScript(
                        string9,
                        string1,
                        string2,
                        string3,
                        string4,
                        string5,
                        string6,
                        string7,
                        bl1,
                        bl,
                        (Properties) null,
                        observableHolder5,
                        observableHolder6,
                        boolean2,
                        boolean3,
                        boolean4,
                        observableHolder7,
                        boolean5,
                        (PrintWriter) object
                );
            } catch (ZkmScriptTokenMgrError zkmScriptTokenMgrError) {
                System.err.println(zkmScriptTokenMgrError.getMessage());
                ScriptEnvironment scriptEnvironment3 = (ScriptEnvironment) observableHolder2.getValue();
                if (scriptEnvironment3 != null) {
                    scriptEnvironment3.getLogWriter().println(zkmScriptTokenMgrError.getMessage());
                    System.exit(1);
                } else {
                    System.exit(1);
                }
            } catch (ZkmScriptParseException zkmScriptParseException) {
                System.err.println(zkmScriptParseException.getMessage());
                ScriptEnvironment scriptEnvironment2 = (ScriptEnvironment) observableHolder2.getValue();
                if (scriptEnvironment2 != null) {
                    scriptEnvironment2.getLogWriter().println(zkmScriptParseException.getMessage());
                    System.exit(1);
                } else {
                    System.exit(1);
                }
            } catch (ZkmProcessingException zkmProcessingException) {
                System.err.println(zkmProcessingException.getMessage());
                System.exit(1);
            } finally {
                if (savedStdout != null && savedStdout != System.out) {
                    System.setOut(savedStdout);
                }

                try {
                    BufferedReader bufferedReader = (BufferedReader) observableHolder3.getValue();
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                } catch (IOException iOException) {
                }

                releaseInstanceLock();
            }

            ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) observableHolder2.getValue();
            if (scriptEnvironment1 != null) {
                scriptEnvironment1.closeLog();
                System.exit(0);
            } else {
                System.exit(0);
            }
        } else {
            String string;
            PrintStream printStream;
            if (observableHolder1.isValueNull()) {
                string = "System property 'ZKM_PROGUARD_STYLE' is set to 'true'.";
                printStream = System.out;
            } else {
                string = (String) observableHolder1.getValue();
                printStream = System.out;
            }

            printStream.println(ZkmUtils.getBracketedTimestamp() + " Treating input as " + "ProGuard" + " style input because " + string);
            Boolean boolean1 = true;
            ObservableHolder observableHolder4 = observableHolder;
            this.runProGuardStyle(strings, (Properties) null, observableHolder4, boolean1);
        }
    }

    public static void redirectStdout(String string) throws ZkmProcessingException {
        if (string != null && string.trim().length() > 0) {
            try {
                PrintStream printStream = new PrintStream(new FileOutputStream(string), true);
                savedStdout = System.out;
                System.setOut(printStream);
            } catch (IOException iOException) {
                throw new ZkmProcessingException(iOException.toString());
            }
        }
    }

    public void showMainWindow(ZkmMainWindow zkmMainWindow) throws ZkmException, IOException {
        zkmMainWindow.setEnabled(true);
        if (!zkmMainWindow.isVisible()) {
            SwingUtils.setVisibleOnEdt(zkmMainWindow);
        }

        zkmMainWindow.showInitialHelper();
    }

    public void initBannerText() throws ZkmException, IOException {
        if (this.bannerTitle == null || this.bannerLines == null) {
            String string = "Zelix KlassMaster" + EVALUATION_SUFFIX + " " + "27.0.0";
            Integer integer = 1;
            String string1 = this.getLicenseField(30828586125445L, integer);
            this.bannerTitle = ZkmUtils.getBracketedTimestamp() + " " + string + (string1 != null && !string1.equals("none") ? " - License #" + string1 : "");
            Integer integer1 = 3;
            String string2 = this.getLicenseField(30828586125445L, integer1);
            String string3 = checkEvaluationExpiry(this.getEncodedExpiryTime(), this.getEncodedEvaluationPeriod(), this.getEncodedGracePeriod(), this);
            string2 = string2 + " " + string3;
            Integer integer2 = 5;
            string3 = this.getLicenseField(30828586125445L, integer2);
            Integer integer3 = 7;
            String string4 = this.getLicenseField(30828586125445L, integer3);
            if (string2 != null) {
                int ba = string2.indexOf("(");
                if (ba == -1) {
                    this.bannerLines = new String[8];
                    this.bannerLines[0] = string2;
                    this.bannerLines[1] = string3;
                    this.bannerLines[2] = string4;
                    this.bannerLines[3] = "Copyright 1997-2026 Zelix Pty Ltd (47 078 740 093)";
                    this.bannerLines[4] = "All rights reserved";
                    this.bannerLines[5] = "OpenSource by NoHackClient (https://github.com/NoHackClient)";
                    this.bannerLines[6] = "github: https://github.com/NoHackClient/ZelixKlassMaster-27";
                    this.bannerLines[7] = "if u cant open this github repo, that means getting DMCA";
                } else {
                    this.bannerLines = new String[9];
                    this.bannerLines[0] = string2.substring(0, ba);
                    this.bannerLines[1] = string2.substring(ba);
                    this.bannerLines[2] = string3;
                    this.bannerLines[3] = string4;
                    this.bannerLines[4] = "Copyright 1997-2026 Zelix Pty Ltd (47 078 740 093)";
                    this.bannerLines[5] = "All rights reserved";
                    this.bannerLines[6] = "OpenSource by NoHackClient (https://github.com/NoHackClient)";
                    this.bannerLines[7] = "github: https://github.com/NoHackClient/ZelixKlassMaster-27";
                    this.bannerLines[8] = "if u cant open this github repo, that means getting DMCA";
                }
            }
        }
    }

    public static synchronized void releaseInstanceLock() {
        long currentThreadId = ZkmUtils.getCurrentThreadId();
        if (activeInstanceId != -1L && currentThreadId == activeInstanceId) {
            activeInstanceId = -1L;
        }
    }

    public String writeGeneratedScript(final String parent, final String str, final Object obj, final String str2) throws ZkmException, IOException {
        final File tempFile = TempFileManager.createTempFile();
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(new FileWriter(tempFile));
            printWriter.println("classpath   \"" + str + "\"; //added automatically");
            printWriter.println("open        \"" + obj + "\" //added automatically");
            printWriter.println("            \"" + str2 + "\"; //added automatically");
            final File file = new File(parent, "script.txt");
            if (file.exists()) {
                printWriter.println("//Reading from '" + file.getAbsoluteFile() + "'");
                BufferedReader openReader = null;
                try {
                    openReader = ZkmFileUtils.openReader(file, HiddenOptionFlags.SCRIPT_ENCODING);
                    String line;
                    while ((line = openReader.readLine()) != null) {
                        printWriter.println(line);
                    }
                } finally {
                    if (openReader != null) {
                        try {
                            openReader.close();
                        } catch (final IOException ex) {
                        }
                    }
                }
            } else {
                printWriter.println("//Using internal default script. '" + file.getAbsolutePath() + "' not found");
                printWriter.println(KlassMaster.defaultMidletScript);
            }
            printWriter.flush();
            printWriter.close();
        } finally {
            if (printWriter != null) {
                printWriter.flush();
                printWriter.close();
            }
        }
        return tempFile.getAbsolutePath();
    }

    public void runProGuardStyle(final String[] array, final Properties properties, final ObservableHolder observableHolder, final boolean b) throws ZkmException, IOException {
        acquireInstanceLock();
        final boolean b2 = this.getLicenseField(30828586125445L, 3).indexOf(KlassMaster.evaluationMarker) != -1;
        final ObservableHolder observableHolder2 = new ObservableHolder();
        final ObservableHolder observableHolder3 = new ObservableHolder();
        final ObservableHolder observableHolder4 = new ObservableHolder();
        final ObservableHolder observableHolder5 = new ObservableHolder();
        if (!ProGuardConfigTranslator.createPreliminaryLogFile(array, observableHolder4, observableHolder5)) {
            this.reportFatalError((String) observableHolder5.getValue(), b);
        }
        final File file = (File) observableHolder4.getValue();
        System.out.println(ZkmUtils.getBracketedTimestamp() + " Preliminary log file name is '" + file.getAbsolutePath() + "'");
        PrintWriter printWriter = null;
        try {
            final ObservableHolder observableHolder6 = new ObservableHolder();
            final ObservableHolder observableHolder7 = new ObservableHolder();
            final ObservableHolder observableHolder8 = new ObservableHolder();
            final BooleanFlag booleanFlag = new BooleanFlag();
            printWriter = new PrintWriter(new FileWriter(file), true);
            writeEnvironmentInfo(printWriter, null);
            this.initBannerText();
            this.writeBanner(printWriter);
            final ClassRepository executeScript = this.executeScript(ProGuardInputTranslator.translateCommandLine(array, properties, observableHolder6, observableHolder7, observableHolder8, booleanFlag, file.getAbsolutePath(), printWriter, b), observableHolder6.isValueNull() ? "ZKM_log.txt" : ((String) observableHolder6.getValue()), observableHolder7.isValueNull() ? "ZKM_TrimLog.txt" : ((String) observableHolder7.getValue()), null, null, null, null, (String) observableHolder8.getValue(), booleanFlag.getValue(), false, null, observableHolder2, observableHolder3, !b, b2, false, observableHolder, true, printWriter);
            try (final BufferedReader bufferedReader = (BufferedReader) observableHolder3.getValue()) {
            } catch (final IOException ex) {
            }
            if (executeScript != null) {
                executeScript.releaseResources();
            }
            final ScriptEnvironment scriptEnvironment = (ScriptEnvironment) observableHolder2.getValue();
            if (scriptEnvironment != null) {
                scriptEnvironment.closeLog();
            }
            printWriter.close();
            releaseInstanceLock();
        } catch (final IOException ex2) {
            final ScriptEnvironment scriptEnvironment2 = (ScriptEnvironment) observableHolder2.getValue();
            if (scriptEnvironment2 != null) {
                scriptEnvironment2.getLogWriter().println(ex2.getMessage());
            }
            if (b) {
                System.err.println(ex2.toString());
                System.exit(1);
                return;
            }
            throw new ZkmRuntimeException(ex2.toString());
        } catch (final ZkmScriptTokenMgrError zkmScriptTokenMgrError) {
            final ScriptEnvironment scriptEnvironment3 = (ScriptEnvironment) observableHolder2.getValue();
            if (scriptEnvironment3 != null) {
                scriptEnvironment3.getLogWriter().println(zkmScriptTokenMgrError.getMessage());
            }
            if (b) {
                System.err.println(zkmScriptTokenMgrError.toString());
                System.exit(1);
                return;
            }
            throw new ZkmRuntimeException(zkmScriptTokenMgrError.toString());
        } catch (final ZkmScriptParseException ex3) {
            final ScriptEnvironment scriptEnvironment4 = (ScriptEnvironment) observableHolder2.getValue();
            if (scriptEnvironment4 != null) {
                scriptEnvironment4.getLogWriter().println(ex3.getMessage());
            }
            if (b) {
                System.err.println(ex3.toString());
                System.exit(1);
                return;
            }
            throw new ZkmRuntimeException(ex3.toString());
        } catch (final ZkmProcessingException ex4) {
            final ScriptEnvironment scriptEnvironment5 = (ScriptEnvironment) observableHolder2.getValue();
            if (scriptEnvironment5 != null) {
                scriptEnvironment5.getLogWriter().println(ex4.getMessage());
            }
            if (b) {
                System.err.println(ex4.toString());
                System.exit(1);
                return;
            }
            throw new ZkmRuntimeException(ex4.toString());
        } finally {
            try (final BufferedReader bufferedReader2 = (BufferedReader) observableHolder3.getValue()) {
            } catch (final IOException ex5) {
            }
            final ScriptEnvironment scriptEnvironment6 = (ScriptEnvironment) observableHolder2.getValue();
            if (scriptEnvironment6 != null) {
                scriptEnvironment6.closeLog();
            }
            if (printWriter != null) {
                printWriter.close();
            }
            releaseInstanceLock();
        }
    }

    public void showStackTraceTranslate() {
    }

    public void writeBanner(PrintWriter printWriter) {
        printWriter.println(this.bannerTitle);
        if (this.bannerLines != null) {
            int ba = 0;
            int bb = 0;

            for (String[] strings = this.bannerLines; bb < strings.length; strings = this.bannerLines) {
                printWriter.println(ZkmStringUtils.pad(this.bannerLines[ba], 82, this.bannerLines[ba].length() + ZkmUtils.getBracketedTimestamp().length() + 1, 32));
                bb = ++ba;
            }
        }
    }

    public KlassMaster() {
    }

    public static Font getDefaultFont() {
        Font font;
        if (defaultFont == null) {
            defaultFont = new Font("Dialog", 0, 12);
            font = defaultFont;
        } else {
            font = defaultFont;
        }

        return font;
    }

    public KlassMaster(String string, Properties properties1, ObservableHolder observableHolder) throws ZkmException, IOException {
        String[] strings = new String[]{"-include", string};
        this.runProGuardStyle(strings, properties1, observableHolder, true);
    }

    public KlassMaster(File file1, String string, File file2, String string1, String string2, boolean bl, ObservableHolder observableHolder) throws ZkmException, IOException {
        acquireInstanceLock();
        Integer integer = 3;
        boolean bl1 = this.getLicenseField(30828586125445L, integer).indexOf(evaluationMarker) != -1;
        ObservableHolder observableHolder1 = new ObservableHolder();
        ObservableHolder observableHolder2 = new ObservableHolder();
        ClassRepository classRepository1 = null;

        try {
            String string3 = this.writeGeneratedScript(string2, string1, file1, string);
            classRepository1 = this.executeScript(
                    string3,
                    "ZKM_log.txt",
                    "ZKM_TrimLog.txt",
                    (String) null,
                    (String) null,
                    (String) null,
                    (String) null,
                    string2,
                    bl,
                    false,
                    (Properties) null,
                    observableHolder1,
                    observableHolder2,
                    true,
                    bl1,
                    true,
                    observableHolder,
                    false,
                    (PrintWriter) null
            );
            ScriptEnvironment scriptEnvironment6 = (ScriptEnvironment) observableHolder1.getValue();
            PrintWriter printWriter = scriptEnvironment6.getLogWriter();
            String string4 = ZkmUtils.getBracketedTimestamp();
            ConsoleLogReporter consoleLogReporter = new ConsoleLogReporter(printWriter, string4.length(), scriptEnvironment6);
            NoOpCallback noOpCallback = NoOpCallback.getInstance();
            printWriter.println(string4 + " Saving all classes into \"" + file2 + "\"");
            System.out.println(string4 + " Saving " + classRepository1.getTotalClassCount() + " classes...");
            classRepository1.saveAll(3, false, false, (String) null, file2.getParentFile(), consoleLogReporter, scriptEnvironment6, noOpCallback);
        } catch (IOException iOException1) {
            ScriptEnvironment scriptEnvironment5 = (ScriptEnvironment) observableHolder1.getValue();
            if (scriptEnvironment5 != null) {
                scriptEnvironment5.getLogWriter().println(iOException1.getMessage());
            }

            throw new ZkmRuntimeException(iOException1.toString());
        } catch (ZkmScriptTokenMgrError zkmScriptTokenMgrError) {
            ScriptEnvironment scriptEnvironment4 = (ScriptEnvironment) observableHolder1.getValue();
            if (scriptEnvironment4 != null) {
                scriptEnvironment4.getLogWriter().println(zkmScriptTokenMgrError.getMessage());
            }

            throw new ZkmRuntimeException(zkmScriptTokenMgrError.getMessage());
        } catch (ZkmScriptParseException zkmScriptParseException) {
            ScriptEnvironment scriptEnvironment3 = (ScriptEnvironment) observableHolder1.getValue();
            if (scriptEnvironment3 != null) {
                scriptEnvironment3.getLogWriter().println(zkmScriptParseException.getMessage());
            }

            throw new ZkmRuntimeException(zkmScriptParseException.getMessage());
        } catch (ZkmProcessingException zkmProcessingException) {
            ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) observableHolder1.getValue();
            if (scriptEnvironment1 != null) {
                scriptEnvironment1.getLogWriter().println(zkmProcessingException.getMessage());
            }

            throw new ZkmRuntimeException(zkmProcessingException.getMessage());
        } finally {
            try {
                BufferedReader bufferedReader = (BufferedReader) observableHolder2.getValue();
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException iOException) {
            }

            if (classRepository1 != null) {
                classRepository1.releaseResources();
            }

            releaseInstanceLock();
        }

        ScriptEnvironment scriptEnvironment2 = (ScriptEnvironment) observableHolder1.getValue();
        if (scriptEnvironment2 != null) {
            scriptEnvironment2.closeLog();
        }
    }

    public void printUsage(PrintStream printStream) {
        printStream.println();
        printStream.println("Usage: java -jar ZKM.jar [[-options] <scriptFileName>]");
        printStream.println();
        printStream.println("Where <scriptFileName> is name of ZKM Script file to execute (non-GUI mode).");
        printStream.println();
        printStream.println("   If all options and <scriptFileName> are missing then start in GUI mode.");
        printStream.println();
        printStream.println("Where the options are:");
        printStream.println("   -?\tDisplay usage");
        printStream.println("   -p\tOnly parse the ZKM Script file (Don't execute it)");
        printStream.println("   -v\tTurn on verbose mode");
        printStream.println("   -l <logFileName>");
        printStream.println("     \tUse specified log file. Default name is 'ZKM_log.txt'");
        printStream.println("   -tl <trimLogFileName>");
        printStream.println("     \tUse specified trim log file.");
        printStream.println("     \tDefault name is 'ZKM_TrimLog.txt'");
        printStream.println("   -de <defaultExcludeFileName>");
        printStream.println("     \tUse specified 'default exclusions' file.");
        printStream.println("     \tDefault name is 'defaultExclude.txt'");
        printStream.println("   -dte <defaultTrimExcludeFileName>");
        printStream.println("     \tUse specified 'default trim exclusions' file.");
        printStream.println("     \tDefault name is 'defaultTrimExclude.txt'");
        printStream.println("   -dpe <defaultMethodParameterChangesExcludeFileName>");
        printStream.println("     \tUse specified 'default method parameter changes exclusions' file.");
        printStream.println("     \tDefault name is 'defaultMethodParameterChangesExclude.txt'");
        printStream.println("   -dpo <defaultMethodParameterObfuscationExcludeFileName>");
        printStream.println("     \tUse specified 'default method parameter obfuscation exclusions' file.");
        printStream.println("     \tDefault name is 'defaultMethodParameterChangesExclude.txt'");
        printStream.println("   -dd <defaultDirectoryName>");
        printStream.println("     \tUse specified directory for reading and writing files");
        printStream.println("     \tthat have not be specified with an absolute path.");
        printStream.println("     \tDefaults to current working directory.");
        printStream.println("   -ro <fileName>");
        printStream.println("     \tRedirect stdout to the specified file. Redirects messages and warnings.");
        printStream.println("     \tErrors will still be written to stderr.");
        printStream.println();
    }

    
    
    public static String checkEvaluationExpiry(String string, String string1, String string2, Object object) throws ZkmException, IOException {
        if (isPermanentLicense()) {
            validatedExpiryCode = string;
            return "unlimited";
        }
        try {
            String string3 = object.getClass().getName();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy");
            TimeZone timeZone = TimeZone.getDefault();
            simpleDateFormat.setTimeZone(timeZone);
            long ba = ObfuscatedLongDecoder.decodeLong(string);
            long bb = ObfuscatedLongDecoder.decodeLong(string1);
            long bc = ObfuscatedLongDecoder.decodeLong(string2);
            Date date = new Date(ba);
            String string4 = simpleDateFormat.format(date);
            long bd = System.currentTimeMillis();
            if (bc * 4L * 4L != bb) {
                String string5 = string4 + (HiddenOptionFlags.SHOW_EXPIRY_ERROR_CODES ? " (01)" : "");
                throw new LicenseExpiredException(string5);
            }

            if (bd > ba) {
                String string6 = string4 + (HiddenOptionFlags.SHOW_EXPIRY_ERROR_CODES ? " (02)" : "");
                throw new LicenseExpiredException(string6);
            }

            if (bd < ba - bb) {
                String string7 = string4 + (HiddenOptionFlags.SHOW_EXPIRY_ERROR_CODES ? " (03)" : "");
                throw new LicenseExpiredException(string7);
            }

            validatedExpiryCode = string;
            long be = bd + bc;
            ClassPathResolver classPathResolver1 = new ClassPathResolver(System.getProperty("java.class.path"));
            Object[] objects = classPathResolver1.getEntries();

            for (int i = 0; i < objects.length; i++) {
                if (objects[i] instanceof File) {
                    long bh = ((File) objects[i]).lastModified();
                    if (bh > be) {
                        String string8 = string4 + (HiddenOptionFlags.SHOW_EXPIRY_ERROR_CODES ? " (04)" : "");
                        throw new LicenseExpiredException(string8);
                    }
                } else {
                    File file1 = new File(((ZipFile) objects[i]).getName());
                    if (file1.exists()) {
                        if (file1.lastModified() > be) {
                            String string9 = string4 + (HiddenOptionFlags.SHOW_EXPIRY_ERROR_CODES ? " (05)" : "");
                            throw new LicenseExpiredException(string9);
                        }

                        if (file1.getName().endsWith("ZKM.jar")) {
                            ArchiveZipFile archiveZipFile = null;
                            boolean bl = false ;

                            label204:
                            {
                                try {
                                    bl = true;
                                    archiveZipFile = new ArchiveZipFile(file1);
                                    ZipEntry zipEntry1 = archiveZipFile.getEntry(string3.replace('.', '/') + ".class");
                                    if (zipEntry1 != null) {
                                        long time = zipEntry1.getTime();
                                        if (time > be) {
                                            String string10 = string4 + (HiddenOptionFlags.SHOW_EXPIRY_ERROR_CODES ? " (06)" : "");
                                            throw new LicenseExpiredException(string10);
                                        }

                                        if (time != -1L) {
                                            if (bd > time + bb) {
                                                String string11 = string4 + (HiddenOptionFlags.SHOW_EXPIRY_ERROR_CODES ? " (07)" : "");
                                                throw new LicenseExpiredException(string11);
                                            }

                                            archiveZipFile.close();
                                            bl = false;
                                        } else {
                                            archiveZipFile.close();
                                            bl = false;
                                        }
                                    } else {
                                        bl = false;
                                    }
                                    break label204;
                                } catch (IOException iOException3) {
                                    bl = false;
                                } finally {
                                    if (bl) {
                                        if (archiveZipFile != null) {
                                            try {
                                                com.zelix.klassmaster.util.ZkmUtils.<java.io.IOException>mayThrow();
                                                archiveZipFile.close();
                                            } catch (IOException iOException) {
                                            }
                                        }
                                    }
                                }

                                try {
                                    if (archiveZipFile != null) {
                                        try {
                                            com.zelix.klassmaster.util.ZkmUtils.<java.io.IOException>mayThrow();
                                            archiveZipFile.close();
                                        } catch (IOException iOException1) {
                                        }
                                    }
                                    continue;
                                } catch (NumberFormatException numberFormatException) {
                                    throw numberFormatException;
                                }
                            }

                            try {
                                com.zelix.klassmaster.util.ZkmUtils.<java.io.IOException>mayThrow();
                                archiveZipFile.close();
                            } catch (IOException iOException2) {
                            }
                        }
                    }
                }
            }

            classPathResolver1.close();
            return string4;
        } catch (NumberFormatException numberFormatException1) {
            String string12 = HiddenOptionFlags.SHOW_EXPIRY_ERROR_CODES ? " (08)" : "";
            throw new LicenseExpiredException(string12);
        }
    }

    private static boolean isPermanentLicense() {
        return true;
    }

    public static void writeEnvironmentInfo(PrintWriter printWriter, Properties properties1) {
        Properties properties3;
        if (properties1 != null) {
            ZkmUtils.listProperties("extra", properties1, printWriter);
            printWriter.println();
            properties3 = System.getProperties();
        } else {
            properties3 = System.getProperties();
        }

        Properties properties2 = properties3;
        ZkmUtils.listProperties("system", properties2, printWriter);
        printWriter.println();
        printWriter.println("defaultCharset=" + Charset.defaultCharset());
        printWriter.println();
        long ba = Runtime.getRuntime().maxMemory();
        if (ba > -1L) {
            printWriter.println("Maximum memory is " + ba / 1024L + "K.");
            printWriter.println();
        }

        try {
            List<String> list1 = ManagementFactory.getRuntimeMXBean().getInputArguments();
            if (!list1.isEmpty()) {
                printWriter.println("JVM options: " + ZkmUtils.toQuotedListString(list1));
                printWriter.println();
            }
        } catch (Throwable throwable) {
        }

        if (HiddenOptionFlags.USE_PARALLEL) {
            if (HiddenOptionFlags.PROCESSOR_COUNT >= 2) {
                printWriter.println("Parallel processing active. " + HiddenOptionFlags.PROCESSOR_COUNT + " processors available.");
                printWriter.println();
            } else {
                printWriter.println("Only " + HiddenOptionFlags.PROCESSOR_COUNT + " processors available.");
                printWriter.println();
            }
        }
    }

    public static synchronized void acquireInstanceLock() {
        long currentThreadId = ZkmUtils.getCurrentThreadId();
        if (activeInstanceId != -1L && currentThreadId != activeInstanceId) {
            throw new ZkmRuntimeException(
                    "Zelix KlassMaster is not thread safe. If you need to run more than once instance concurrently then each instance must run in a separate JVM with different 'java.io.tmpdir' System property values."
            );
        }

        activeInstanceId = currentThreadId;
    }

    public void startGui(ObservableHolder observableHolder) throws ZkmException, IOException {
        Integer integer = 3;
        boolean bl = this.getLicenseField(30828586125445L, integer).indexOf(evaluationMarker) != -1;
        boolean bl1 = false;
        if (HiddenOptionFlags.LAF_NAME != null) {
            try {
                UIManager.setLookAndFeel(HiddenOptionFlags.LAF_NAME);
                bl1 = true;
            } catch (Exception exception1) {
            }
        }

        if (!bl1) {
            String string;
            if (HiddenOptionFlags.USER_PREFERENCES.isCrossPlatformLookAndFeel()) {
                string = UIManager.getCrossPlatformLookAndFeelClassName();
            } else {
                string = UIManager.getSystemLookAndFeelClassName();
            }

            if (string != null) {
                try {
                    UIManager.setLookAndFeel(string);
                } catch (Exception exception) {
                }
            }
        }

        ObservableHolder observableHolder1 = new ObservableHolder();
        String string1 = getSavedClasspath();
        ZkmClasspath zkmClasspath = new ZkmClasspath(string1);
        ClassRepository classRepository1 = new ClassRepository(observableHolder1, zkmClasspath, false, bl, ZkmFileUtils.caseSensitiveFileSystem, false, this);
        BooleanFlag booleanFlag = new BooleanFlag(false);
        ObservableHolder observableHolder2 = new ObservableHolder();
        ObservableHolder observableHolder3 = new ObservableHolder();
        boolean bl2 = zkmClasspath.locateRuntimeClasses(observableHolder3, observableHolder2);
        if (bl2) {
            HiddenOptionFlags.USER_PREFERENCES.setClasspath(zkmClasspath.getClasspath());
            HiddenOptionFlags.USER_PREFERENCES.savePreferences();
            classRepository1.loadObjectMethods();
        }

        booleanFlag.setValue(bl2);
        ConsoleMessageReporter consoleMessageReporter = new ConsoleMessageReporter();
        NoOpCallback noOpCallback = NoOpCallback.getInstance();
        ObservableHolder observableHolder4 = new ObservableHolder();
        if (!ZkmFileUtils.canWriteAndReadFile("ZKM_log.txt", observableHolder4)) {
            consoleMessageReporter.reportError(
                    "ERROR:", "Log file \"ZKM_log.txt\" could not be created, modified and read. : '" + (String) observableHolder4.getValue() + "' (A)"
            );
        }

        PrintWriter printWriter = null;

        try {
            printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream("ZKM_log.txt"), "UTF-8"), true);
        } catch (IOException iOException) {
            consoleMessageReporter.reportError("ERROR:", "Couldn't open ZKM_log.txt : " + iOException.getClass().getName());
        }

        PrintWriter printWriter1 = printWriter;
        observableHolder.setValue(printWriter1);
        ZkmUtils.listProperties(System.getProperties(), printWriter1);
        printWriter1.println();
        long ba = Runtime.getRuntime().maxMemory();
        if (ba > -1L) {
            printWriter1.println("Maximum memory is " + ba / 1024L + "K.");
            printWriter1.println();
        }

        try {
            List<String> list1 = ManagementFactory.getRuntimeMXBean().getInputArguments();
            if (!list1.isEmpty()) {
                printWriter1.println("JVM options: " + ZkmUtils.toQuotedListString(list1));
                printWriter1.println();
            }
        } catch (Throwable throwable) {
        }

        if (HiddenOptionFlags.USE_PARALLEL) {
            if (HiddenOptionFlags.PROCESSOR_COUNT >= 2) {
                printWriter1.println("Parallel processing active. " + HiddenOptionFlags.PROCESSOR_COUNT + " processors available.");
                printWriter1.println();
            } else {
                printWriter1.println("Only " + HiddenOptionFlags.PROCESSOR_COUNT + " processors available.");
                printWriter1.println();
            }
        }

        this.initBannerText();
        this.printBanner(System.out);
        this.writeBanner(printWriter1);
        if (bl2) {
            if (!observableHolder3.isValueNull()) {
                printWriter1.println(ZkmUtils.getBracketedTimestamp() + " Using \"" + (String) observableHolder3.getValue() + "\" as path to java.lang.Object (A)");
            } else if (!observableHolder2.isValueNull()) {
                printWriter1.println(ZkmUtils.getBracketedTimestamp() + " Using \"" + (String) observableHolder2.getValue() + "\" as path to java.lang.Object (B)");
            }
        }

        LoadLogWriterTask loadLogWriterTask = new LoadLogWriterTask(this, consoleMessageReporter, classRepository1, noOpCallback, printWriter1);
        new Thread(loadLogWriterTask).start();
        ZkmMainWindow zkmMainWindow = new ZkmMainWindow(
                "Zelix KlassMaster" + EVALUATION_SUFFIX, classRepository1, observableHolder1, zkmClasspath, printWriter1, HiddenOptionFlags.USER_PREFERENCES
        );
        classRepository1.addObserver(zkmMainWindow);
        Dimension dimension = zkmMainWindow.getSize();
        dimension.width--;
        zkmMainWindow.setSize(dimension);
        this.showMainWindow(zkmMainWindow);
    }

    public boolean isProGuardStyleInput(String[] strings, ObservableHolder observableHolder) throws ZkmException, IOException {
        for (int i = 0; i < strings.length; i++) {
            if (ProGuardOptionNames.PROGUARD_SPECIFIC_OPTIONS.contains(strings[i])) {
                observableHolder.setValue("the command line option '" + strings[i] + "' is present.");
                return true;
            }
        }

        for (int i = 0; i < strings.length; i++) {
            if (strings[i].length() > 0 && strings[i].charAt(0) != '-') {
                boolean bl = false;
                if (strings[i].charAt(0) == '@') {
                    bl = true;
                    strings[i] = strings[i].substring(1);
                }

                try {
                    File file1 = new File(strings[i]);
                    String string = ZkmFileUtils.readFileAsString(file1, ZkmFileUtils.DEFAULT_ENCODING);
                    if (this.containsUncommentedText(string)) {
                        observableHolder.setValue("file '" + file1.getAbsolutePath() + "' contains " + "ProGuard" + " command '" + "-injars" + "'");
                        if (strings[i].charAt(0) != '@') {
                            if (!bl) {
                                System.out
                                        .println(
                                                ZkmUtils.getBracketedTimestamp()
                                                        + " "
                                                        + "WARNING:"
                                                        + " Prepending '@' to front of command line argument '"
                                                        + strings[i]
                                                        + "' to make it valid "
                                                        + "ProGuard"
                                                        + " style input."
                                        );
                            }

                            strings[i] = '@' + strings[i];
                        }

                        return true;
                    }
                } catch (AssertionFailedException assertionFailedException) {
                    throw assertionFailedException;
                } catch (Throwable throwable) {
                }
            }
        }

        return false;
    }

    public void failWithUsage(String string) {
        System.err.println();
        System.err.println("ERROR: " + string);
        this.printUsageAndFail();
    }

    public KlassMaster(final String s, final String s2, final String s3, final String s4, final String s5, final String s6, final String s7, final String s8, final boolean b, final boolean b2, final Properties properties, final ObservableHolder observableHolder) throws ZkmException, IOException {
        acquireInstanceLock();
        final boolean b3 = this.getLicenseField(30828586125445L, 3).indexOf(KlassMaster.evaluationMarker) != -1;
        final ObservableHolder observableHolder2 = new ObservableHolder();
        final ObservableHolder observableHolder3 = new ObservableHolder();
        try {
            final ClassRepository executeScript = this.executeScript(s, s2, s3, s4, s5, s6, s7, s8, b, b2, properties, observableHolder2, observableHolder3, true, b3, false, observableHolder, false, null);
            try {
                final BufferedReader bufferedReader = (BufferedReader) observableHolder3.getValue();
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (final IOException ex) {
            }
            if (executeScript != null) {
                executeScript.releaseResources();
            }
            releaseInstanceLock();
        } catch (final ZkmScriptTokenMgrError zkmScriptTokenMgrError) {
            final ScriptEnvironment scriptEnvironment = (ScriptEnvironment) observableHolder2.getValue();
            if (scriptEnvironment != null) {
                scriptEnvironment.getLogWriter().println(zkmScriptTokenMgrError.getMessage());
            }
            throw new ZkmRuntimeException(zkmScriptTokenMgrError.getMessage());
        } catch (final ZkmScriptParseException ex2) {
            final ScriptEnvironment scriptEnvironment2 = (ScriptEnvironment) observableHolder2.getValue();
            if (scriptEnvironment2 != null) {
                scriptEnvironment2.getLogWriter().println(ex2.getMessage());
            }
            throw new ZkmRuntimeException(ex2.getMessage());
        } catch (final ZkmProcessingException ex3) {
            final ScriptEnvironment scriptEnvironment3 = (ScriptEnvironment) observableHolder2.getValue();
            if (scriptEnvironment3 != null) {
                scriptEnvironment3.getLogWriter().println(ex3.getMessage());
            }
            throw new ZkmRuntimeException(ex3.getMessage());
        } finally {
            try {
                final BufferedReader bufferedReader2 = (BufferedReader) observableHolder3.getValue();
                if (bufferedReader2 != null) {
                    bufferedReader2.close();
                }
            } catch (final IOException ex4) {
            }
            releaseInstanceLock();
        }
        final ScriptEnvironment scriptEnvironment4 = (ScriptEnvironment) observableHolder2.getValue();
        if (scriptEnvironment4 != null) {
            scriptEnvironment4.closeLog();
        }
    }

    public ClassRepository executeScript(
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
            Properties properties1,
            ObservableHolder observableHolder,
            ObservableHolder observableHolder1,
            boolean bl2,
            boolean bl3,
            boolean bl4,
            ObservableHolder observableHolder2,
            boolean bl5,
            PrintWriter printWriter
    ) throws ZkmException, ZkmScriptParseException, IOException {
        ObservableHolder observableHolder3 = new ObservableHolder();
        ZkmClasspath zkmClasspath = new ZkmClasspath(System.getProperty("java.class.path"));
        ClassRepository classRepository1 = new ClassRepository(observableHolder3, zkmClasspath, bl, bl3, ZkmFileUtils.caseSensitiveFileSystem, bl5, this);
        ScriptEnvironment scriptEnvironment1 = new ScriptEnvironment(
                classRepository1, zkmClasspath, bl, string1, string2, string3, string4, string5, string6, string7, bl2, bl4, bl5
        );
        observableHolder.setValue(scriptEnvironment1);
        String string8 = scriptEnvironment1.getLogFileName();
        File file1 = scriptEnvironment1.getDefaultDirectory();
        String string9 = ZkmFileUtils.resolveDotRelativePath(ZkmFileUtils.normalizeSeparators(string), file1);
        File file2;
        if (ZkmFileUtils.isRelativePath(string9)) {
            file2 = new File(file1, string9);
        } else {
            file2 = new File(string9);
        }

        ObservableHolder observableHolder4 = new ObservableHolder();
        ObservableHolder observableHolder5 = new ObservableHolder();
        boolean bl6 = zkmClasspath.locateRuntimeClasses(observableHolder5, observableHolder4);
        if (bl6) {
            classRepository1.loadObjectMethods();
        }

        PrintWriter printWriter1 = scriptEnvironment1.getLogWriter();
        observableHolder2.setValue(printWriter1);
        if (printWriter != null) {
            printWriter.println();
            printWriter.println(
                    ZkmUtils.getBracketedTimestamp() + " Default directory set to '" + scriptEnvironment1.getDefaultDirectory().getAbsolutePath() + "'"
            );
            printWriter.println(ZkmUtils.getBracketedTimestamp() + ' ' + "Zelix KlassMaster" + " log file is '" + string8 + "'");
        }

        if (bl) {
            writeEnvironmentInfo(printWriter1, properties1);
        }

        if (bl6) {
            if (!observableHolder5.isValueNull()) {
                printWriter1.println(ZkmUtils.getBracketedTimestamp() + " Using \"" + (String) observableHolder5.getValue() + "\" as path to java.lang.Object (C)");
            } else if (!observableHolder4.isValueNull()) {
                printWriter1.println(ZkmUtils.getBracketedTimestamp() + " Using \"" + (String) observableHolder4.getValue() + "\" as path to java.lang.Object (D)");
            }
        }

        this.initBannerText();
        this.printBanner(System.out);
        this.writeBanner(printWriter1);
        System.out.println(ZkmUtils.getBracketedTimestamp() + " Preprocessing ZKM Script file...");
        printWriter1.println(
                ZkmUtils.getBracketedTimestamp() + " Preprocessing ZKM Script file \"" + TempFileManager.replaceTempPaths(file2.getAbsolutePath()) + "\""
        );
        ScriptPreprocessor scriptPreprocessor = new ScriptPreprocessor(file2.getAbsolutePath(), properties1);
        BufferedReader bufferedReader = scriptPreprocessor.createReader();
        observableHolder1.setValue(bufferedReader);
        PrintStream printStream;
        if (bl) {
            printWriter1.println("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
            printWriter1.println(scriptPreprocessor.getProcessedText());
            printWriter1.println("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
            printWriter1.println();
            printStream = System.out;
        } else {
            printStream = System.out;
        }

        printStream.println(ZkmUtils.getBracketedTimestamp() + " Parsing ZKM Script file...");
        printWriter1.println(ZkmUtils.getBracketedTimestamp() + " Parsing ZKM Script file \"" + file2.getAbsolutePath() + "\"");

        try {
            ZkmFileUtils.copyStreamToTempFile(new BufferedInputStream(new FileInputStream(new File(string8))), string8);
        } catch (IOException iOException) {
        }

        ZkmScriptASTInput zkmScriptASTInput = (ZkmScriptASTInput) new ZkmScriptParser(bufferedReader).Input();
        if (bl4) {
            File file3 = new File(string7, "script.txt");
            if (zkmScriptASTInput.countSaveStatements() > 0) {
                printStream = System.out;
                StringBuilder stringBuilder1 = new StringBuilder();
                int bb = ZkmUtils.getBracketedTimestamp().length();
                Integer integer = 32;
                printStream.println(
                        stringBuilder1.append(ZkmStringUtils.repeatChar(bb, integer))
                                .append("WARNING: Detected a save statement in '")
                                .append(file3)
                                .append("'. It is unnecessary when using the plugin.")
                                .toString()
                );
                stringBuilder1 = new StringBuilder();
                bb = ZkmUtils.getBracketedTimestamp().length();
                Integer integer1 = 32;
                printWriter1.println(
                        stringBuilder1.append(ZkmStringUtils.repeatChar(bb, integer1))
                                .append("WARNING: Detected a save statement in '")
                                .append(file3)
                                .append("'. It is unnecessary when using the plugin.")
                                .toString()
                );
            }

            if (zkmScriptASTInput.countLoadStatements() > 1) {
                printStream = System.err;
                StringBuilder stringBuilder = new StringBuilder();
                int ba = ZkmUtils.getBracketedTimestamp().length();
                Integer integer2 = 32;
                printStream.println(
                        stringBuilder.append(ZkmStringUtils.repeatChar(ba, integer2))
                                .append("ERROR: Detected an 'open' statement in '")
                                .append(file3)
                                .append("'. The plugin MUST specify the JAR to be opened.")
                                .toString()
                );
                stringBuilder = new StringBuilder();
                ba = ZkmUtils.getBracketedTimestamp().length();
                Integer integer3 = 32;
                printWriter1.println(
                        stringBuilder.append(ZkmStringUtils.repeatChar(ba, integer3))
                                .append("ERROR: Detected an 'open' statement in '")
                                .append(file3)
                                .append("'. The plugin MUST specify the JAR to be opened.")
                                .toString()
                );
                printWriter1.flush();
            } else {
                printWriter1.flush();
            }
        } else {
            printWriter1.flush();
        }

        if (!bl1 && !HiddenOptionFlags.PARSE_ONLY) {
            zkmScriptASTInput.execute(null, scriptEnvironment1);
        }

        String string10 = ZkmUtils.getBracketedTimestamp() + " Terminating normally.";
        string10 = string10 + scriptEnvironment1.takeMessageSuffix();
        System.out.println(string10 + " See \"" + string8 + "\" for more detail.");
        printWriter1.println(string10);
        return classRepository1;
    }
}
