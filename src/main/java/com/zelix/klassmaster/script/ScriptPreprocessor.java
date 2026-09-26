package com.zelix.klassmaster.script;

import com.zelix.klassmaster.archive.ZkmFileUtils;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Properties;

public class ScriptPreprocessor {
    public static final String USER_DIR = System.getProperty("user.dir");
    public File includeDirectory;
    public final String delimiter = "%";
    public final String directiveStart = '<' + this.delimiter;
    public final String directiveEnd = this.delimiter + '>';
    public Properties properties;
    public String processedText;

    public ScriptPreprocessor(String string, String string1, Properties properties1, String string2) throws ZkmException {
        this(string, null, properties1, null, null);
    }

    public boolean isBlank(String string) {
        int ba = string.length();

        for (int i = 0; i < ba; i++) {
            if (string.charAt(i) != ' ') {
                return false;
            }
        }

        return true;
    }

    public BufferedReader createReader() {
        return new BufferedReader(new StringReader(this.processedText));
    }

    public String describeIncludeChain(ArrayList arrayList) {
        StringBuffer stringBuffer = new StringBuffer();

        for (int i = arrayList.size() - 1; i >= 0; i += -1) {
            String string = (String) arrayList.get(i);
            if (i == arrayList.size() - 1) {
                stringBuffer.append("'" + string + "'");
            } else {
                stringBuffer.append(" included in '" + string + "'");
            }
        }

        return stringBuffer.toString();
    }

    public String processIncludeDirective(String string, ArrayList arrayList) throws ZkmException {
        int ba = string.indexOf("include");
        if (ba == -1) {
            throw new ZkmProcessingException(
                    "PREPROCESSOR ERROR: Missing 'include' within '"
                            + this.directiveStart
                            + string
                            + this.directiveEnd
                            + "' in file "
                            + this.describeIncludeChain(arrayList)
            );
        }

        if (!this.isBlank(string.substring(0, ba))) {
            throw new ZkmProcessingException(
                    "PREPROCESSOR ERROR: Invalid 'include' directive '"
                            + this.directiveStart
                            + string
                            + this.directiveEnd
                            + "'. String '"
                            + string.substring(0, ba)
                            + "' is extraneous in file "
                            + this.describeIncludeChain(arrayList)
            );
        }

        int bb = string.indexOf("\"", ba + "include".length());
        if (bb > -1) {
            if (this.isBlank(string.substring(ba + "include".length(), bb))) {
                int bc = string.indexOf("\"", bb + "\"".length());
                if (bc > -1) {
                    String string1 = string.substring(bb + "\"".length(), bc);
                    if (!this.isBlank(string.substring(bc + "\"".length()))) {
                        throw new ZkmProcessingException(
                                "PREPROCESSOR ERROR: Invalid 'include' directive '"
                                        + this.directiveStart
                                        + string
                                        + this.directiveEnd
                                        + "'. String '"
                                        + string.substring(bc + "\"".length())
                                        + "' is extraneous in file "
                                        + this.describeIncludeChain(arrayList)
                        );
                    }

                    String string2;
                    if (ZkmFileUtils.isRelativePath(string1)) {
                        if (this.includeDirectory != null) {
                            File file1 = new File(this.includeDirectory, string1);
                            if (!file1.exists()) {
                                File file2 = new File(USER_DIR, string1);
                                if (!file2.exists()) {
                                    throw new ZkmProcessingException(
                                            "PREPROCESSOR ERROR: Could not find either '"
                                                    + file1.getAbsolutePath()
                                                    + "' or '"
                                                    + file2.getAbsolutePath()
                                                    + "' as specified in '"
                                                    + "include"
                                                    + "' directive '"
                                                    + this.directiveStart
                                                    + string
                                                    + this.directiveEnd
                                                    + "' in file "
                                                    + this.describeIncludeChain(arrayList)
                                    );
                                }

                                string2 = file2.getAbsolutePath();
                            } else {
                                string2 = file1.getAbsolutePath();
                            }
                        } else {
                            File file3 = new File(USER_DIR, string1);
                            string2 = file3.getAbsolutePath();
                        }
                    } else {
                        string2 = string1;
                    }

                    return this.preprocessFile(string2, ZkmUtils.copyArrayList(arrayList));
                } else {
                    throw new ZkmProcessingException(
                            "PREPROCESSOR ERROR: Missing closing '\"' in 'include' directive '"
                                    + this.directiveStart
                                    + string
                                    + this.directiveEnd
                                    + "' in file "
                                    + this.describeIncludeChain(arrayList)
                    );
                }
            } else {
                throw new ZkmProcessingException(
                        "PREPROCESSOR ERROR: Invalid 'include' directive '"
                                + this.directiveStart
                                + string
                                + this.directiveEnd
                                + "'. String '"
                                + string.substring(ba + "include".length(), bb)
                                + "' is extraneous in file "
                                + this.describeIncludeChain(arrayList)
                );
            }
        } else {
            throw new ZkmProcessingException(
                    "PREPROCESSOR ERROR: Missing opening '\"' in 'include' directive '"
                            + this.directiveStart
                            + string
                            + this.directiveEnd
                            + "' in file "
                            + this.describeIncludeChain(arrayList)
            );
        }
    }

    public String substituteProperties(String string) {
        StringBuffer stringBuffer = new StringBuffer((int) (string.length() * 1.2));
        int ba = 0;

        int bb;
        while ((bb = string.indexOf(this.delimiter, ba)) > -1) {
            stringBuffer.append(string.substring(ba, bb));
            StringBuffer stringBuffer1;
            String string4;
            if (bb != 0 && string.charAt(bb - 1) == '<') {
                stringBuffer1 = stringBuffer;
                string4 = this.delimiter;
            } else if (bb < string.length() - 3) {
                if (string.charAt(bb + 1) != '>') {
                    int bc = string.indexOf(this.delimiter, bb + this.delimiter.length());
                    if (bc > -1) {
                        label54:
                        if (bc > bb + this.delimiter.length()) {
                            String string3;
                            int bd;
                            String string5;
                            if (bb > this.delimiter.length()) {
                                if (string.charAt(bb - this.delimiter.length()) == '<') {
                                    if (string.length() - bc >= this.delimiter.length()) {
                                        if (string.charAt(bc + this.delimiter.length()) == '>') {
                                            stringBuffer1 = stringBuffer;
                                            string4 = this.delimiter;
                                            break label54;
                                        }

                                        string3 = string;
                                        bd = bb;
                                        string5 = this.delimiter;
                                    } else {
                                        string3 = string;
                                        bd = bb;
                                        string5 = this.delimiter;
                                    }
                                } else {
                                    string3 = string;
                                    bd = bb;
                                    string5 = this.delimiter;
                                }
                            } else {
                                string3 = string;
                                bd = bb;
                                string5 = this.delimiter;
                            }

                            String string1 = string3.substring(bd + string5.length(), bc);
                            String string2 = null;
                            if (this.properties != null) {
                                string2 = this.properties.getProperty(string1);
                            }

                            if (string2 == null) {
                                string2 = System.getProperty(string1);
                            }

                            if (string2 != null) {
                                stringBuffer.append(string2);
                                ba = bc + this.delimiter.length();
                            } else {
                                stringBuffer.append(this.delimiter);
                                ba = bb + this.delimiter.length();
                            }
                            continue;
                        } else {
                            stringBuffer1 = stringBuffer;
                            string4 = this.delimiter;
                        }
                    } else {
                        stringBuffer1 = stringBuffer;
                        string4 = this.delimiter;
                    }

                    stringBuffer1.append(string4);
                    ba = bb + this.delimiter.length();
                    continue;
                }

                stringBuffer1 = stringBuffer;
                string4 = this.delimiter;
            } else {
                stringBuffer1 = stringBuffer;
                string4 = this.delimiter;
            }

            stringBuffer1.append(string4);
            ba = bb + this.delimiter.length();
        }

        stringBuffer.append(string.substring(ba));
        return stringBuffer.toString();
    }

    public String preprocessFile(String string, ArrayList arrayList) throws ZkmException {
        arrayList.add(string);
        StringBuffer stringBuffer = new StringBuffer(1028);
        BufferedReader bufferedReader = null;

        try {
            bufferedReader = ZkmFileUtils.openReaderForPath(string, HiddenOptionFlags.SCRIPT_ENCODING);
            StringBuffer stringBuffer1 = new StringBuffer();
            int be = bufferedReader.read();

            while (true) {
                int ba = be;
                if (be == -1) {
                    String string1 = stringBuffer1.toString();
                    string1 = this.substituteProperties(string1);
                    int bb = 0;
                    String string3 = string1;

                    int bc;
                    for (String string4 = this.directiveStart; (bc = string3.indexOf(string4, bb)) > -1; string4 = this.directiveStart) {
                        stringBuffer.append(string1.substring(bb, bc));
                        int bd = string1.indexOf(this.directiveEnd, bc);
                        if (bd <= -1) {
                            throw new ZkmProcessingException(
                                    "PREPROCESSOR ERROR: '"
                                            + this.directiveStart
                                            + "' without a matching '"
                                            + this.directiveEnd
                                            + "' in file "
                                            + this.describeIncludeChain(arrayList)
                            );
                        }

                        String string2 = this.processIncludeDirective(string1.substring(bc + this.directiveStart.length(), bd), arrayList);
                        stringBuffer.append(string2);
                        bb = bd + this.directiveEnd.length();
                        string3 = string1;
                    }

                    stringBuffer.append(string1.substring(bb));
                    return stringBuffer.toString();
                }

                stringBuffer1.append((char) ba);
                be = bufferedReader.read();
            }
        } catch (FileNotFoundException fileNotFoundException) {
            throw new ZkmProcessingException("PREPROCESSOR ERROR: Couldn't find file " + this.describeIncludeChain(arrayList));
        } catch (IOException iOException1) {
            throw new ZkmProcessingException("PREPROCESSOR ERROR: " + iOException1.toString() + " in " + this.describeIncludeChain(arrayList));
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException iOException) {
                }
            }
        }
    }

    public ScriptPreprocessor(String string, String string1, Properties properties1, String string2, String string3) throws ZkmException {
        this.properties = properties1;
        if (!ZkmUtils.getHashedProperty(
                        "b90e12da2adf99f479ecad90a530ea33b8701422fbbe250b5d5f8edb8f7096c8b61dba24a593c4406c81f08cf9f129d95040be9487d1c7f1b0a53713dd37cb2d",
                        "true",
                        properties1
                )
                .equals("false")) {
            HiddenOptionFlags.skipFrameCompatibilityCheck = true;
        } else {
            HiddenOptionFlags.skipFrameCompatibilityCheck = false;
        }

        ArrayList arrayList = new ArrayList();
        this.processedText = this.preprocessFile(string, arrayList);
    }

    public String getProcessedText() {
        return this.processedText;
    }

    public ScriptPreprocessor(String string, Properties properties1) throws ZkmException {
        this(string, null, properties1, null);
    }
}
