package com.zelix;

import com.zelix.klassmaster.changelog.ChangeLogMapping;
import com.zelix.klassmaster.changelog.ChangeLogMethodEntry;
import com.zelix.klassmaster.changelog.LineSeparatorHolder;
import com.zelix.klassmaster.changelog.parser.ChangeLogParseException;
import com.zelix.klassmaster.changelog.parser.ChangeLogParser;
import com.zelix.klassmaster.changelog.parser.ChangeLogSimpleNode;
import com.zelix.klassmaster.changelog.parser.ChangeLogTokenMgrError;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ZKMChangeLog extends LineSeparatorHolder {
    public int baselineFatalErrorCount;
    public int baselineWarningCount;
    public int baselineErrorCount;
    public int baselineMessageCount;
    public ChangeLogMapping changeLogMapping;
    public ScriptEnvironment scriptEnvironment = new ScriptEnvironment(
            null, null, "ZKM_log1.txt", (String) null, (String) null, (String) null, (String) null, (String) null, (String) null
    );
    public String logFileName;

    public List getOldClassNames() {
        try {
            return this.toDottedNames(this.changeLogMapping.getOldClassNames());
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public String getOldTypeName(String string) {
        try {
            if (string == null) {
                throw new IllegalArgumentException("New type name must not be null.");
            }

            String string1 = this.changeLogMapping.toOriginalTypeName(string);
            return string1 == null ? string : ZkmUtils.slashesToDots(string1);
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public String getNewFieldName(String string, String string1, String string2) {
        try {
            if (string == null) {
                throw new IllegalArgumentException("Old class name must not be null.");
            }

            if (this.changeLogMapping.hasClassMapping(string)) {
                if (this.changeLogMapping.containsFieldMapping(string, string1, string2)) {
                    this.recordLogCounts();
                    String string3 = this.changeLogMapping.getNewFieldName(string, string1, string2);
                    this.reportNewLogErrors();
                    return string3 == null ? string1 : string3;
                } else {
                    return string1;
                }
            } else {
                return string1;
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public void recordLogCounts() {
        this.baselineMessageCount = this.scriptEnvironment.getMessageCount();
        this.baselineWarningCount = this.scriptEnvironment.getWarningCount();
        this.baselineErrorCount = this.scriptEnvironment.getErrorCount();
        this.baselineFatalErrorCount = this.scriptEnvironment.getSeriousErrorCount();
    }

    public String getNewPackageName(String string) {
        try {
            if (string == null) {
                throw new IllegalArgumentException("New class name must not be null.");
            }

            String string1 = this.changeLogMapping.getNewPackageName(string);
            return string1 == null ? string : ZkmUtils.slashesToDots(string1);
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public List getOldMethodSignaturesForClass(String string) {
        if (string == null) {
            throw new IllegalArgumentException("Old class name must not be null.");
        } else {
            return this.changeLogMapping.listMethodEntries(string);
        }
    }

    public boolean isOldFieldPresent(String string, String string1) {
        try {
            if (string == null) {
                throw new IllegalArgumentException("Old class name must not be null.");
            }

            if (this.changeLogMapping.hasClassMapping(string)) {
                int ba = string1.indexOf(" ");
                if (ba > 0 && ba < string1.length() - 1) {
                    String string2 = string1.substring(0, ba);
                    String string3 = string1.substring(ba + 1);
                    return this.isOldFieldPresent(string, string3, string2);
                } else {
                    throw new IllegalArgumentException("'" + string1 + "' is not a valid field signature");
                }
            } else {
                return false;
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public ChangeLogMethodEntry parseMethodSignature(String string) {
        String string1 = string;
        string1 = string1.trim();
        int ba = string1.indexOf(" ");
        if (ba <= -1) {
            throw new IllegalArgumentException("'" + string1 + "' is not a valid method signature (1)");
        }

        String string2 = string1.substring(0, ba);
        String string3 = string1.substring(ba + 1);
        string3 = string3.trim();
        int bb = string3.indexOf("(");
        if (bb <= 0) {
            throw new IllegalArgumentException("'" + string1 + "' is not a valid method signature (2)");
        }

        String string4 = string3.substring(0, bb);
        int bc = string3.indexOf(")", bb);
        if (bc <= -1) {
            throw new IllegalArgumentException("'" + string1 + "' is not a valid method signature (3)");
        }

        string3 = string3.substring(bb + 1, bc);
        ArrayList arrayList = new ArrayList();
        StringTokenizer stringTokenizer = new StringTokenizer(string3, ",");

        while (stringTokenizer.hasMoreTokens()) {
            arrayList.add(stringTokenizer.nextToken().trim());
        }

        String[] strings = new String[arrayList.size()];
        strings = ((java.lang.String[]) (arrayList.toArray(strings)));
        return new ChangeLogMethodEntry(this, string2, string4, strings);
    }

    public String getNewMethodSignature(String string, String string1, String[] strings, String string2) {
        try {
            if (string == null) {
                throw new IllegalArgumentException("Old class name must not be null.");
            }

            if (string1 == null) {
                throw new IllegalArgumentException("Old method name must not be null.");
            }

            if (string2 == null) {
                throw new IllegalArgumentException("Old method return type must not be null.");
            }

            ChangeLogMethodEntry changeLogMethodEntry = new ChangeLogMethodEntry(this, string2, string1, strings);
            if (this.changeLogMapping.hasClassMapping(string)) {
                if (this.changeLogMapping.containsMethodMapping(string, string1, strings, string2)) {
                    this.recordLogCounts();
                    String string3 = this.changeLogMapping.describeNewMethod(string, string1, strings, string2);
                    this.reportNewLogErrors();
                    return string3 == null ? changeLogMethodEntry.getSignatureString() : string3;
                } else {
                    return changeLogMethodEntry.getSignatureString();
                }
            } else {
                return changeLogMethodEntry.getSignatureString();
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public void reportNewLogErrors() {
        boolean bl = false;
        boolean bl1 = false;
        int ba;
        if (this.baselineFatalErrorCount >= this.scriptEnvironment.getSeriousErrorCount() && this.baselineErrorCount >= this.scriptEnvironment.getErrorCount()) {
            ba = this.baselineWarningCount;
        } else {
            bl = true;
            bl1 = true;
            ba = this.baselineWarningCount;
        }

        if (ba < this.scriptEnvironment.getWarningCount() || this.baselineMessageCount < this.scriptEnvironment.getMessageCount()) {
            bl1 = true;
        }

        if (bl1) {
            System.err.println(this.scriptEnvironment.getLoggedMessagesText());
        }

        if (bl) {
            System.exit(1);
        }
    }

    public String getNewFieldName(String string, String string1) {
        try {
            if (string == null) {
                throw new IllegalArgumentException("Old class name must not be null.");
            } else {
                int ba = string1.indexOf(" ");
                if (ba > 0 && ba < string1.length() - 1) {
                    String string3 = string1.substring(0, ba);
                    String string2 = string1.substring(ba + 1);
                    return this.changeLogMapping.hasClassMapping(string) ? this.getNewFieldName(string, string2, string3) : string2;
                } else {
                    throw new IllegalArgumentException("'" + string1 + "' is not a valid field signature");
                }
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public ZKMChangeLog(String string) throws Exception {
        this.scriptEnvironment.clearLoggedMessages();
        this.logFileName = string;
        this.recordLogCounts();
        this.parseChangeLog();
        this.reportNewLogErrors();
    }

    public String getOldClassName(String string) {
        try {
            if (string == null) {
                throw new IllegalArgumentException("New class name must not be null.");
            }

            String string1 = this.changeLogMapping.getOriginalClassName(string);
            return string1 == null ? string : ZkmUtils.slashesToDots(string1);
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public String getOldMethodName(String string, String string1, String[] strings, String string2) {
        try {
            if (string == null) {
                throw new IllegalArgumentException("New class name must not be null.");
            }

            if (this.changeLogMapping.isNewClassName(string)) {
                if (this.changeLogMapping.hasOriginalMethod(string, string1, strings, string2)) {
                    this.recordLogCounts();
                    String string3 = this.changeLogMapping.getOriginalMethodName(string, string1, strings, string2);
                    this.reportNewLogErrors();
                    return string3 == null ? string1 : string3;
                } else {
                    return string1;
                }
            } else {
                return string1;
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public String getNewClassName(String string) {
        try {
            if (string == null) {
                throw new IllegalArgumentException("Old class name must not be null.");
            }

            String string1 = this.changeLogMapping.getNewClassName(string);
            return string1 == null ? string : ZkmUtils.slashesToDots(string1);
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public boolean isOldFieldPresent(String string, String string1, String string2) {
        try {
            if (string == null) {
                throw new IllegalArgumentException("Old class name must not be null.");
            } else {
                return this.changeLogMapping.hasClassMapping(string) ? this.changeLogMapping.containsFieldMapping(string, string1, string2) : false;
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public String getNewMethodName(String string, String string1, String[] strings, String string2) {
        try {
            if (string == null) {
                throw new IllegalArgumentException("Old class name must not be null.");
            }

            if (this.changeLogMapping.hasClassMapping(string)) {
                if (this.changeLogMapping.containsMethodMapping(string, string1, strings, string2)) {
                    this.recordLogCounts();
                    String string3 = this.changeLogMapping.getNewMethodName(string, string1, strings, string2);
                    this.reportNewLogErrors();
                    return string3 == null ? string1 : string3;
                } else {
                    return string1;
                }
            } else {
                return string1;
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public String getOldFieldName(String string, String string1, String string2) {
        try {
            if (string == null) {
                throw new IllegalArgumentException("New class name must not be null.");
            } else {
                return this.changeLogMapping.getOriginalFieldName(string, string1, string2);
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public String getLogFileName() {
        return this.logFileName;
    }

    public boolean isOldClassPresent(String string) {
        try {
            if (string == null) {
                throw new IllegalArgumentException("Old class name must not be null.");
            } else {
                return this.changeLogMapping.hasClassMapping(string);
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public void parseChangeLog() throws Exception {
        BufferedReader bufferedReader = null;
        ChangeLogSimpleNode input = null;
        try {
            this.changeLogMapping = new ChangeLogMapping(this.logFileName, this.scriptEnvironment);
            final ChangeLogParser instance = ChangeLogParser.instance;
            final String changeLogEncoding = ChangeLogMapping.readChangeLogEncoding(new File(this.logFileName));
            if (changeLogEncoding != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(this.logFileName), changeLogEncoding));
            } else {
                bufferedReader = new BufferedReader(new FileReader(this.logFileName));
            }
            if (instance == null) {
                new ChangeLogParser(bufferedReader);
            } else {
                ChangeLogParser.ReInit(bufferedReader);
            }
            input = ChangeLogParser.Input();
            input.interpret(null, 30872, 34067, 41973, this.changeLogMapping);
            ChangeLogMapping changeLogMapping;
            if (input.jjtGetNumChildren() == 0) {
                System.exit(1);
                changeLogMapping = this.changeLogMapping;
            } else {
                this.changeLogMapping.derivePackageMappingsFromClasses();
                changeLogMapping = this.changeLogMapping;
            }
            changeLogMapping.markParsingComplete();
            if (input != null) {
                input.dump();
            }
            try {
                bufferedReader.close();
            } catch (final IOException ex) {
            }
        } catch (final FileNotFoundException ex2) {
            throw new Exception("File Error : Couldn't open file \"" + this.logFileName + "\"");
        } catch (final ChangeLogTokenMgrError changeLogTokenMgrError) {
            throw new Exception("Lexical error while reading \"" + this.logFileName + "\" : \"" + changeLogTokenMgrError.getMessage() + "\"");
        } catch (final ChangeLogParseException ex3) {
            throw new Exception("Parse error while reading \"" + this.logFileName + "\" : \"" + ex3.getMessage() + "\"");
        } finally {
            if (input != null) {
                input.dump();
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (final IOException ex4) {
                }
            }
        }
    }

    public List getNewClassNames() {
        try {
            return this.toDottedNames(this.changeLogMapping.getNewClassNames());
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public boolean isOldMethodPresent(String string, String string1, String[] strings, String string2) {
        try {
            if (string == null) {
                throw new IllegalArgumentException("Old class name must not be null.");
            } else {
                return this.changeLogMapping.hasClassMapping(string) ? this.changeLogMapping.containsMethodMapping(string, string1, strings, string2) : false;
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public List getNewPackageNames() {
        try {
            return this.toDottedNames(this.changeLogMapping.getRenamedPackageNames());
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public String getNewMethodSignature(String string, String string1) {
        try {
            if (string == null) {
                throw new IllegalArgumentException("Old class name must not be null.");
            }

            ChangeLogMethodEntry changeLogMethodEntry = this.parseMethodSignature(string1);
            return this.changeLogMapping.hasClassMapping(string)
                    ? this.getNewMethodSignature(string, changeLogMethodEntry.methodName, changeLogMethodEntry.argumentTypes, changeLogMethodEntry.returnType)
                    : string1;
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public String getOldMethodName(String string, String string1) {
        try {
            if (string == null) {
                throw new IllegalArgumentException("New class name must not be null.");
            }

            ChangeLogMethodEntry changeLogMethodEntry = this.parseMethodSignature(string1);
            return this.changeLogMapping.isNewClassName(string)
                    ? this.getOldMethodName(string, changeLogMethodEntry.methodName, changeLogMethodEntry.argumentTypes, changeLogMethodEntry.returnType)
                    : changeLogMethodEntry.methodName;
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public ArrayList toDottedNames(ArrayList arrayList) throws IOException {
        for (int i = 0; i < arrayList.size(); i++) {
            String string = ZkmUtils.slashesToDots((String) arrayList.get(i));
            arrayList.set(i, string);
        }

        return arrayList;
    }

    public String getNewMethodName(String string, String string1) {
        try {
            if (string == null) {
                throw new IllegalArgumentException("Old class name must not be null.");
            }

            ChangeLogMethodEntry changeLogMethodEntry = this.parseMethodSignature(string1);
            return this.changeLogMapping.hasClassMapping(string)
                    ? this.getNewMethodName(string, changeLogMethodEntry.methodName, changeLogMethodEntry.argumentTypes, changeLogMethodEntry.returnType)
                    : changeLogMethodEntry.methodName;
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public boolean isOldPackagePresent(String string) {
        try {
            if (string == null) {
                throw new IllegalArgumentException("Old package name must not be null.");
            } else {
                return this.changeLogMapping.hasPackageMapping(string);
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public List getOldFieldSignaturesForClass(String string) {
        if (string == null) {
            throw new IllegalArgumentException("Old class name must not be null.");
        } else {
            return this.changeLogMapping.listFieldEntries(string);
        }
    }

    public boolean isOldMethodPresent(String string, String string1) {
        try {
            if (string == null) {
                throw new IllegalArgumentException("Old class name must not be null.");
            } else if (this.changeLogMapping.hasClassMapping(string)) {
                ChangeLogMethodEntry changeLogMethodEntry = this.parseMethodSignature(string1);
                return this.isOldMethodPresent(string, changeLogMethodEntry.methodName, changeLogMethodEntry.argumentTypes, changeLogMethodEntry.returnType);
            } else {
                return false;
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public List getOldMethodSignatures(String string, String string1) {
        try {
            if (string == null) {
                throw new IllegalArgumentException("New class name must not be null.");
            } else {
                return this.changeLogMapping.findOriginalMethods(string, string1);
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public List getOldPackageNames() {
        try {
            return this.toDottedNames(this.changeLogMapping.getSortedPackageNames());
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }
}
