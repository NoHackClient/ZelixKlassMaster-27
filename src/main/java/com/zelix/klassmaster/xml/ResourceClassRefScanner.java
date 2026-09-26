package com.zelix.klassmaster.xml;

import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.StringTokenizer;

public class ResourceClassRefScanner {
    public ResourceClassRefScanner(BufferedReader bufferedReader, String string, Map map1) throws IOException {
        String string1;
        while ((string1 = bufferedReader.readLine()) != null) {
            int ba = string1.indexOf(35);
            if (ba > -1) {
                string1 = string1.substring(0, ba);
            }

            StringTokenizer stringTokenizer = new StringTokenizer(string1, " []{},");

            while (stringTokenizer.hasMoreTokens()) {
                ProgramClass programClass1 = ClassHierarchyNode.findProgramClass(ZkmUtils.dotsToSlashes(stringTokenizer.nextToken()));
                if (programClass1 != null) {
                    map1.put(programClass1, "Referenced in '" + string + "'");
                }
            }
        }
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
    }
}
