package com.zelix.klassmaster.archive;

import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.log.MessageReporter;
import com.zelix.klassmaster.util.BooleanFlag;
import com.zelix.klassmaster.util.EnumerableMap;
import com.zelix.klassmaster.xml.ResourcePathTranslator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

public class ManifestEntrySection extends AbstractManifestProcessor {
    public String getSectionName() {
        return this.getUnwrappedHeaderValue("Name");
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
        ResourcePathTranslator resourcePathTranslator1 = (ResourcePathTranslator) object;
        String string7 = ResourcePathTranslator.getMarkerString();
        Enumeration enumeration = super.headers.keys();
        String string1 = string7;

        while (enumeration.hasMoreElements()) {
            String string2 = (String) enumeration.nextElement();
            if (string2.equals("Name")) {
                String string3 = this.getSectionName();
                if (string3 != null && string3.length() <= 0) {
                    printWriter.println(this.wrapManifestLine("Name: "));
                } else {
                    printWriter.println(
                            this.wrapManifestLine("Name: " + AbstractManifestProcessor.translateResourcePath(string3, enumerableMap1, resourcePathTranslator1))
                    );
                }
            } else if (string2.equals("Java-Bean")) {
                String string5 = this.getRawHeaderValue("Java-Bean");
                printWriter.println("Java-Bean: " + string5);
            } else if (string2.equals("Sealed")) {
                String string6 = this.getRawHeaderValue("Sealed");
                printWriter.println("Sealed: " + string6);
            } else if (!string2.equals("Magic") && string2.indexOf("Digest") == -1) {
                BooleanFlag booleanFlag = new BooleanFlag();
                String string4 = this.translateClassListAttribute(string2, enumerableMap, enumerableMap1, booleanFlag, string, bl, messageReporter1);
                if (!booleanFlag.getValue() || HiddenOptionFlags.KEEP_UNCHANGED_MANIFEST_ENTRIES) {
                    printWriter.println(string4);
                }
            }

            if (string1 == null) {
                break;
            }
        }

        printWriter.println();
    }

    public void collectEntryPointClasses() {
    }

    @Override
    public String normalizeHeaderName(String string) {
        if (string.equalsIgnoreCase("Name")) {
            return "Name";
        } else if (string.equalsIgnoreCase("Java-Bean")) {
            return "Java-Bean";
        } else {
            return string.equalsIgnoreCase("Sealed") ? "Sealed" : string;
        }
    }

    public ManifestEntrySection(BufferedReader bufferedReader) throws ZkmException, IOException {
        super(bufferedReader);
    }
}
