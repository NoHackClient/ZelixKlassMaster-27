package com.zelix.klassmaster.archive;

import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.log.MessageReporter;
import com.zelix.klassmaster.util.BooleanFlag;
import com.zelix.klassmaster.util.EnumerableMap;
import com.zelix.klassmaster.util.NumericStringUtil;
import com.zelix.klassmaster.util.ZkmAssert;
import com.zelix.klassmaster.util.ZkmUtils;
import com.zelix.klassmaster.xml.ResourcePathTranslator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Map;
import java.util.StringTokenizer;

public class ManifestHandler extends AbstractManifestProcessor {
    public static final MethodSignature PREMAIN_WITH_INSTRUMENTATION = new MethodSignature("premain(Ljava/lang/String;Ljava/lang/instrument/Instrumentation;)V");
    public static final MethodSignature PREMAIN = new MethodSignature("premain(Ljava/lang/String;)V");
    public static final MethodSignature AGENTMAIN_WITH_INSTRUMENTATION = new MethodSignature(
            "agentmain(Ljava/lang/String;Ljava/lang/instrument/Instrumentation;)V"
    );
    public static final MethodSignature AGENTMAIN = new MethodSignature("agentmain(Ljava/lang/String;)V");

    public void addAgentEntryMethod(String string, String string1, MethodSignature methodSignature1, ProgramClass programClass1, Map map1, Map map2) {
        MethodInfo methodInfo1 = programClass1.findMethodBySignature(methodSignature1);
        if (methodInfo1 != null) {
            map1.put(methodInfo1, "MANIFEST.MF " + string + string1);
            map2.put(methodInfo1, "MANIFEST.MF " + string + string1);
        }
    }

    @Override
    public String normalizeHeaderName(String string) {
        if (string.equalsIgnoreCase("Manifest-Version")) {
            return "Manifest-Version";
        } else if (string.equalsIgnoreCase("Class-Path")) {
            return "Class-Path";
        } else if (string.equalsIgnoreCase("Rsrc-Class-Path")) {
            return "Rsrc-Class-Path";
        } else if (string.equalsIgnoreCase("Main-Class")) {
            return "Main-Class";
        } else if (string.equalsIgnoreCase("Rsrc-Main-Class")) {
            return "Rsrc-Main-Class";
        } else if (string.equalsIgnoreCase("Start-Class:")) {
            return "Start-Class:";
        } else if (string.equalsIgnoreCase("Premain-Class:")) {
            return "Premain-Class:";
        } else if (string.equalsIgnoreCase("Agent-Class:")) {
            return "Agent-Class:";
        } else if (string.equalsIgnoreCase("Launcher-Agent-Class:")) {
            return "Launcher-Agent-Class:";
        } else if (string.equalsIgnoreCase("Sealed")) {
            return "Sealed";
        } else if (string.equalsIgnoreCase("Signature-Version")) {
            return "Signature-Version";
        } else if (string.equalsIgnoreCase("MicroEdition-Profile")) {
            return "MicroEdition-Profile";
        } else if (string.equalsIgnoreCase("MicroEdition-Configuration")) {
            return "MicroEdition-Configuration";
        } else if (string.equalsIgnoreCase("JavaFX-Application-Class")) {
            return "JavaFX-Application-Class";
        } else if (string.equalsIgnoreCase("JavaFX-Preloader-Class")) {
            return "JavaFX-Preloader-Class";
        } else if (string.equalsIgnoreCase("JavaFX-Fallback-Class")) {
            return "JavaFX-Fallback-Class";
        } else {
            return string.equalsIgnoreCase("JavaFX-Class-Path") ? "JavaFX-Class-Path" : string;
        }
    }

    public ManifestHandler(BufferedReader bufferedReader) throws ZkmException, IOException {
        super(bufferedReader);
    }

    public boolean isCldc10() throws ZkmException {
        String string = this.getUnwrappedHeaderValue("MicroEdition-Configuration");
        if (string == null) {
            return false;
        } else {
            int ba = string.indexOf("-");
            if (ba > -1 && ba < string.length() - 1) {
                String string1 = string.substring(ba + 1);
                return string1.startsWith("1.0");
            } else {
                throw new ZkmException("Invalid MicroEdition-Configuration '" + string + "' in manifest");
            }
        }
    }

    public static String translateMidletValue(String string, String string1, EnumerableMap enumerableMap, EnumerableMap enumerableMap1) {
        String string2 = string + ":" + " ";
        StringBuilder stringBuilder = new StringBuilder();
        StringTokenizer stringTokenizer = new StringTokenizer(string1, ",", true);
        int ba = 0;

        while (stringTokenizer.hasMoreTokens()) {
            String string3 = stringTokenizer.nextToken();
            if (string3.equals(",")) {
                ba++;
            } else {
                switch (ba) {
                    case 0:
                        break;
                    case 1:
                        String string4 = ResourcePathTranslator.renameResourceParentPath(string3, enumerableMap);
                        if (!string3.trim().equals(string4)) {
                            stringBuilder.append(" " + string4);
                            continue;
                        }
                        break;
                    case 2:
                        String string5 = AbstractManifestProcessor.translateName(string3, enumerableMap1);
                        if (!string3.trim().equals(string5)) {
                            stringBuilder.append(" " + string5);
                            continue;
                        }
                        break;
                    default:
                        ZkmAssert.assertTrue(false, new String[]{"'" + string + "' has invalid value '" + string1 + "' : " + ba});
                }
            }

            stringBuilder.append(string3);
        }

        return string2 + stringBuilder.toString();
    }

    public String translateMidletAttribute(String string, EnumerableMap enumerableMap, EnumerableMap enumerableMap1) {
        String string1 = this.getUnwrappedHeaderValue(string);
        return this.wrapManifestLine(translateMidletValue(string, string1, enumerableMap, enumerableMap1));
    }

    public void collectEntryPointClasses(String string, Map map1, Map map2, Map map3) {
        String string2 = " in '" + string + "'";
        Enumeration enumeration = super.headers.keys();

        while (enumeration.hasMoreElements()) {
            String string3 = (String) enumeration.nextElement();
            if (string3.equals("Main-Class")
                    || string3.equals("Rsrc-Main-Class")
                    || string3.equals("Start-Class:")
                    || string3.equals("Premain-Class:")
                    || string3.equals("Agent-Class:")
                    || string3.equals("Launcher-Agent-Class:")
                    || string3.equals("JavaFX-Application-Class")
                    || string3.equals("JavaFX-Preloader-Class")
                    || string3.equals("JavaFX-Fallback-Class")) {
                String string4 = this.getUnwrappedHeaderValue(string3);
                String string5 = ZkmUtils.dotsToSlashes(string4);
                ProgramClass programClass1 = ClassHierarchyNode.findProgramClass(string5);
                if (programClass1 != null) {
                    map1.put(programClass1, "MANIFEST.MF " + string3 + string2);
                    switch (string3) {
                        case "Main-Class":
                        case "Rsrc-Main-Class":
                        case "Start-Class:":
                            MethodInfo methodInfo1 = programClass1.findMethodBySignature(MethodSignature.MAIN_METHOD);
                            if (methodInfo1 != null) {
                                map2.put(methodInfo1, "MANIFEST.MF " + string3 + string2);
                                if (HiddenOptionFlags.FIX_MANIFEST_MAIN_METHODS) {
                                    map3.put(methodInfo1, "MANIFEST.MF " + string3 + string2);
                                }
                            }
                            break;
                        case "Premain-Class:":
                        case "Agent-Class:":
                        case "Launcher-Agent-Class:":
                            this.addAgentEntryMethod(string3, string2, PREMAIN_WITH_INSTRUMENTATION, programClass1, map2, map3);
                            this.addAgentEntryMethod(string3, string2, PREMAIN, programClass1, map2, map3);
                            this.addAgentEntryMethod(string3, string2, AGENTMAIN_WITH_INSTRUMENTATION, programClass1, map2, map3);
                            this.addAgentEntryMethod(string3, string2, AGENTMAIN, programClass1, map2, map3);
                        case "JavaFX-Application-Class":
                        case "JavaFX-Preloader-Class":
                        case "JavaFX-Fallback-Class":
                    }
                }
            } else if (string3.startsWith("MIDlet-") && NumericStringUtil.isInteger(string3.substring("MIDlet-".length()))) {
                String string7 = this.getUnwrappedHeaderValue(string3);
                StringTokenizer stringTokenizer1 = new StringTokenizer(string7, ",", true);
                int ba = 0;

                while (stringTokenizer1.hasMoreTokens()) {
                    String string9 = stringTokenizer1.nextToken().trim();
                    if (string9.equals(",")) {
                        ba++;
                    } else {
                        switch (ba) {
                            case 0:
                            case 1:
                                break;
                            case 2:
                                String string11 = ZkmUtils.dotsToSlashes(string9);
                                ProgramClass programClass3 = ClassHierarchyNode.findProgramClass(string11);
                                if (programClass3 != null) {
                                    map1.put(programClass3, "MANIFEST.MF " + string3 + string2);
                                }
                                break;
                            default:
                                String[] strings = new String[]{"'" + string3 + "' has invalid value '" + string7 + "' : " + ba};
                                ZkmAssert.assertTrue(false, strings);
                        }
                    }
                }
            } else if (!string3.equals("Signature-Version")) {
                String string6 = this.getUnwrappedHeaderValue(string3);
                StringTokenizer stringTokenizer = new StringTokenizer(string6, " ,;", true);

                while (stringTokenizer.hasMoreTokens()) {
                    String string8 = stringTokenizer.nextToken();
                    if (string8.length() >= 3 && string8.indexOf(".") > -1 && string8.indexOf("/") == -1) {
                        int bb = string8.indexOf(".");
                        if (bb > 0 && bb < string8.length() - 1) {
                            String string1 = ".";
                            if (AbstractManifestProcessor.isDottedIdentifier(string8, string1)) {
                                String string10 = ZkmUtils.dotsToSlashes(string8);
                                if (!string10.endsWith(";")
                                        && string10.indexOf("[") == -1
                                        && string10.indexOf(".") == -1
                                        && string10.indexOf("<") == -1
                                        && string10.indexOf(">") == -1) {
                                    ProgramClass programClass2 = ClassHierarchyNode.findProgramClass(string10);
                                    if (programClass2 != null) {
                                        map1.put(programClass2, "MANIFEST.MF " + string3 + string2);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void writeSection(
            PrintWriter printWriter,
            EnumerableMap enumerableMap,
            EnumerableMap enumerableMap1,
            Object object,
            String string,
            boolean bl,
            MessageReporter messageReporter1
    ) throws IOException {
        Enumeration enumeration = super.headers.keys();

        while (enumeration.hasMoreElements()) {
            String string1 = (String) enumeration.nextElement();
            if (string1.equals("Main-Class")
                    || string1.equals("Rsrc-Main-Class")
                    || string1.equals("Start-Class:")
                    || string1.equals("JavaFX-Application-Class")
                    || string1.equals("JavaFX-Preloader-Class")
                    || string1.equals("JavaFX-Fallback-Class")
                    || string1.equals("Premain-Class:")
                    || string1.equals("Agent-Class:")
                    || string1.equals("Launcher-Agent-Class:")) {
                String string5 = this.getUnwrappedHeaderValue(string1);
                printWriter.println(this.wrapManifestLine(string1 + ":" + " " + AbstractManifestProcessor.translateName(string5, enumerableMap1)));
            } else if (string1.equals("Manifest-Version") || string1.equals("Sealed")) {
                String string4 = this.getRawHeaderValue(string1);
                printWriter.println(string1 + ":" + " " + string4);
            } else if (string1.equals("MIDlet-Icon")) {
                printWriter.println(
                        this.wrapManifestLine(
                                string1 + ":" + " " + ResourcePathTranslator.renameResourceParentPath(this.getUnwrappedHeaderValue(string1), enumerableMap)
                        )
                );
            } else if (string1.startsWith("MIDlet-") && NumericStringUtil.isInteger(string1.substring("MIDlet-".length()))) {
                printWriter.println(this.translateMidletAttribute(string1, enumerableMap, enumerableMap1));
            } else if (!string1.equals("Signature-Version")) {
                if (!string1.equals("Bundle-SymbolicName")
                        && !string1.equals("Require-Bundle")
                        && !string1.equals("Fragment-Host")
                        && !string1.equals("Class-Path")
                        && !string1.equals("Rsrc-Class-Path")
                        && !string1.equals("JavaFX-Class-Path")) {
                    BooleanFlag booleanFlag = new BooleanFlag();
                    String string3 = this.translateClassListAttribute(string1, enumerableMap, enumerableMap1, booleanFlag, string, bl, messageReporter1);
                    if (!booleanFlag.getValue() || HiddenOptionFlags.KEEP_UNCHANGED_MANIFEST_ENTRIES) {
                        printWriter.println(string3);
                    }
                } else {
                    String string2 = this.wrapManifestLine(string1 + ":" + " " + this.getUnwrappedHeaderValue(string1));
                    printWriter.println(string2);
                }
            }
        }

        printWriter.println();
    }

    public boolean hasMicroEditionConfiguration() {
        return this.getUnwrappedHeaderValue("MicroEdition-Configuration") != null;
    }
}
