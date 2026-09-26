package com.zelix.klassmaster.archive;

import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.log.MessageReporter;
import com.zelix.klassmaster.util.BooleanFlag;
import com.zelix.klassmaster.util.EnumerableMap;
import com.zelix.klassmaster.util.NumericStringUtil;
import com.zelix.klassmaster.util.OrderedIndexedMap;
import com.zelix.klassmaster.util.ZkmAssert;
import com.zelix.klassmaster.util.ZkmUtils;
import com.zelix.klassmaster.xml.ResourcePathTranslator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Map.Entry;

public class JadFile {
    private static int[] flowControlTable;
    public static String sectionTerminator;
    public OrderedIndexedMap attributes = new OrderedIndexedMap();
    public String jadFilePath;
    public String jadFileName;
    public String expectedFinalSha256;

    public void requireAttribute(String string) throws ZkmException {
        if (!this.attributes.containsKey(string)) {
            throw new ZkmException("JAD file '" + this.jadFilePath + "' does not contain a " + string + " attribute.");
        }
    }


    static {
        g(new int[4]);
        sectionTerminator = "&";
    }


    public String parseAttributeLine(String string) throws ZkmException {
        int ba = string.indexOf(":");
        if (ba > 0 && ba != string.length() - 1) {
            String string1 = string.substring(0, ba).trim();
            OrderedIndexedMap orderedIndexedMap;
            if (string1.equalsIgnoreCase("MIDlet-Name")) {
                string1 = "MIDlet-Name";
                orderedIndexedMap = this.attributes;
            } else if (string1.equalsIgnoreCase("MIDlet-Jar-URL")) {
                string1 = "MIDlet-Jar-URL";
                orderedIndexedMap = this.attributes;
            } else if (string1.equalsIgnoreCase("MIDlet-Jar-Size")) {
                string1 = "MIDlet-Jar-Size";
                orderedIndexedMap = this.attributes;
            } else {
                orderedIndexedMap = this.attributes;
            }

            if (orderedIndexedMap.containsKey(string1)) {
                throw new ZkmException("Two '" + string1 + "' headers in the JAD: '" + string + "'");
            }

            String string2 = string.substring(ba + 1).trim();
            this.attributes.put(string1, string2);
            return string1;
        } else {
            throw new ZkmException("Invalid JAD attribute '" + string + "'");
        }
    }

    public JadFile(InputFileLocation inputFileLocation) throws ZkmException {
        if (inputFileLocation == null) {
            throw new IllegalArgumentException("Null JAD file");
        }

        this.jadFilePath = inputFileLocation.getQualifiedName();
        this.jadFileName = inputFileLocation.getFile().getName();
        if (inputFileLocation.getExpectedFinalSha256() != null) {
            this.expectedFinalSha256 = inputFileLocation.getExpectedFinalSha256().toLowerCase();
        }

        BufferedReader bufferedReader = null;

        try {
            String string = HiddenOptionFlags.JAD_ENCODING;
            String string2;
            if (string == null) {
                string = "UTF-8";
                string2 = this.jadFilePath;
            } else {
                string2 = this.jadFilePath;
            }

            bufferedReader = ZkmFileUtils.openReaderForPath(string2, string);

            String string1;
            while ((string1 = bufferedReader.readLine()) != null) {
                if (!string1.equals("") && !string1.equals(sectionTerminator)) {
                    this.parseAttributeLine(string1);
                }
            }

            this.requireAttribute("MIDlet-Name");
            this.requireAttribute("MIDlet-Jar-URL");
            this.requireAttribute("MIDlet-Jar-Size");
        } catch (IOException iOException1) {
            throw new ZkmException("IOException while reading JAD : '" + this.jadFilePath + "' : " + iOException1);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException iOException) {
                    throw new ZkmException("IOException while closing JAD : '" + this.jadFilePath + "' : " + iOException);
                }
            }
        }
    }

    public String getJadFilePath() {
        return this.jadFilePath;
    }

    public String getAttribute(Object object) {
        return (String) this.attributes.get(object);
    }

    
    
    public void writeTranslatedJad(File file1, EnumerableMap enumerableMap, EnumerableMap enumerableMap1, String string, MessageReporter messageReporter1) throws ZkmException {
        if (file1 == null) {
            throw new IllegalArgumentException("Null output JAD file");
        }

        boolean bl = false;
        HashSet hashSet = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(enumerableMap.size()));
        Iterator iterator = enumerableMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            if (!hashSet.add(entry.getValue())) {
                bl = true;
                break;
            }
        }

        PrintWriter printWriter = null;
        boolean bl1 = false ;

        try {
            bl1 = true;
            String string4 = HiddenOptionFlags.JAD_ENCODING;
            if (string4 == null) {
                string4 = "UTF-8";
            }

            printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file1), string4)));
            Enumeration enumeration = this.attributes.keyEnumeration();

            while (enumeration.hasMoreElements()) {
                String string1 = (String) enumeration.nextElement();
                String string2 = this.getAttribute(string1);
                if (string1.equals("MIDlet-Icon")) {
                    printWriter.println(string1 + ":" + " " + ResourcePathTranslator.renameResourceParentPath(string2, enumerableMap));
                } else if (string1.startsWith("MIDlet-") && NumericStringUtil.isInteger(string1.substring("MIDlet-".length()))) {
                    printWriter.println(ManifestHandler.translateMidletValue(string1, string2, enumerableMap, enumerableMap1));
                } else {
                    BooleanFlag booleanFlag = new BooleanFlag(false);
                    String string3 = AbstractManifestProcessor.translateClassListValue(
                            string1, string2, enumerableMap, enumerableMap1, booleanFlag, string, bl, messageReporter1
                    );
                    if (!booleanFlag.getValue() || HiddenOptionFlags.KEEP_UNCHANGED_MANIFEST_ENTRIES) {
                        printWriter.println(string3);
                    }
                }
            }

            printWriter.println();
            bl1 = false;
        } catch (IOException iOException) {
            throw new ZkmException("IOException while writing JAD : '" + file1.getAbsolutePath() + "' : " + iOException);
        } finally {
            if (bl1) {
                if (printWriter != null) {
                    printWriter.close();
                }
            }
        }

        printWriter.close();
    }

    public static void g(int[] ba) {
        flowControlTable = ba;
    }

    public long getJarSize() throws JadFileException {
        String string = (String) this.attributes.get("MIDlet-Jar-Size");
        if (string != null) {
            try {
                return Long.parseLong(string);
            } catch (NumberFormatException numberFormatException) {
                throw new JadFileException(
                        "JAD file '" + this.jadFilePath + "' has an invalid " + "MIDlet-Jar-Size" + " value of '" + string + "' : " + numberFormatException.toString()
                );
            }
        } else {
            throw new JadFileException("JAD file '" + this.jadFilePath + "' has no " + "MIDlet-Jar-Size" + " attribute");
        }
    }

    public static int[] getFlowControlTable() {
        return flowControlTable;
    }

    public String getJarFileName() throws ZkmException {
        String string = (String) this.attributes.get("MIDlet-Jar-URL");
        if (string != null) {
            int ba = string.lastIndexOf("/");
            String string1;
            if (ba > -1) {
                if (ba >= string.length()) {
                    throw new ZkmException("JAD file '" + this.jadFilePath + "' has an invalid " + "MIDlet-Jar-URL" + " value of '" + string + "'");
                }

                string1 = string.substring(ba + 1);
            } else {
                string1 = string;
            }

            int bb = string1.indexOf("?");
            if (bb > -1) {
                string1 = string1.substring(0, bb);
            }

            return string1;
        } else {
            throw new ZkmException("JAD file '" + this.jadFilePath + "' has no " + "MIDlet-Jar-URL" + " attribute");
        }
    }

    public void setJarSize(long ba) {
        String string = (String) this.attributes.put("MIDlet-Jar-Size", String.valueOf(ba));
    }

    public void collectMidletClasses(String string, Map map1) {
        String string2 = " in '" + string + "'";
        Enumeration enumeration = this.attributes.keyEnumeration();

        while (enumeration.hasMoreElements()) {
            String string3 = (String) enumeration.nextElement();
            if (string3.startsWith("MIDlet-") && NumericStringUtil.isInteger(string3.substring("MIDlet-".length()))) {
                String string7 = this.getAttribute(string3);
                StringTokenizer stringTokenizer1 = new StringTokenizer(string7, ",", true);
                int bb = 0;

                while (stringTokenizer1.hasMoreTokens()) {
                    String string8 = stringTokenizer1.nextToken().trim();
                    if (string8.equals(",")) {
                        bb++;
                    } else {
                        switch (bb) {
                            case 0:
                            case 1:
                                break;
                            case 2:
                                String string9 = ZkmUtils.dotsToSlashes(string8);
                                ProgramClass programClass2 = ClassHierarchyNode.findProgramClass(string9);
                                if (programClass2 != null) {
                                    map1.put(programClass2, string3 + string2);
                                }
                                break;
                            default:
                                String[] strings = new String[]{"'" + string3 + "' has invalid value '" + string7 + "' : " + bb};
                                ZkmAssert.assertTrue(false, strings);
                        }
                    }
                }
            } else if (!string3.equals("MIDlet-Icon")) {
                String string4 = this.getAttribute(string3);
                StringTokenizer stringTokenizer = new StringTokenizer(string4, " ,", true);

                while (stringTokenizer.hasMoreTokens()) {
                    String string5 = stringTokenizer.nextToken();
                    if (string5.length() >= 3 && string5.indexOf(".") > -1 && string5.indexOf("/") == -1) {
                        int ba = string5.indexOf(".");
                        if (ba > 0 && ba < string5.length() - 1) {
                            String string1 = ".";
                            if (AbstractManifestProcessor.isDottedIdentifier(string5, string1)) {
                                String string6 = ZkmUtils.dotsToSlashes(string5);
                                ProgramClass programClass1 = ClassHierarchyNode.findProgramClass(string6);
                                if (programClass1 != null) {
                                    map1.put(programClass1, string3 + string2);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public String getJadFileName() {
        return this.jadFileName;
    }
}
