package com.zelix.klassmaster.xml;

import com.zelix.klassmaster.classfile.hierarchy.ClassMemberLookup;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.log.MessageReporter;
import com.zelix.klassmaster.util.EnumerableMap;
import com.zelix.klassmaster.util.ReadOnlyMultiMap;
import com.zelix.klassmaster.util.ZkmStringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TaglibXmlUpdater extends XmlConfigFileHandler {
    @Override
    public void rewriteCharacterData(String string, String string1, List list1) throws XmlContentException, IOException {
        if (string1 == null) {
            throw new XmlContentException("Character data outside of tag : '" + ZkmStringUtils.escapeJavaString(string) + "' (7)");
        }

        if (!string1.equals("tagclass")
                && !string1.equals("teiclass")
                && !string1.equals("validator-class")
                && !string1.equals("listener-class")
                && !string1.equals("variable-class")) {
            list1.add(this.updateReferenceValue(string, string1, true));
        } else {
            String string2 = this.getRenamedClassName(string);
            list1.add(string2);
        }
    }

    public TaglibXmlUpdater(String string, ClassMemberLookup classMemberLookup1, ClassResolver classResolver1, MessageReporter messageReporter1) {
        super(string, classMemberLookup1, classResolver1, messageReporter1);
    }

    @Override
    public void analyzeCharacterData(String string, String string1, Map map1, Object object) throws ZkmException, IOException {
        if (string1 == null) {
            throw new XmlContentException("Character data outside of tag : '" + ZkmStringUtils.escapeJavaString(string) + "' (7a)");
        }

        String string2 = "reference in '" + super.resourceName + "' by '" + string1 + "' tag";
        if (!string1.equals("tagclass")
                && !string1.equals("teiclass")
                && !string1.equals("validator-class")
                && !string1.equals("listener-class")
                && !string1.equals("variable-class")) {
            this.addClassNameReferences(string, map1, string2, string1, true);
        } else {
            this.addClassReference(string, map1, string2);
        }
    }

    public TaglibXmlUpdater(
            String string,
            EnumerableMap enumerableMap,
            ReadOnlyMultiMap readOnlyMultiMap,
            ReadOnlyMultiMap readOnlyMultiMap1,
            ResourcePathTranslator resourcePathTranslator1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1,
            MessageReporter messageReporter1
    ) {
        super(string, enumerableMap, readOnlyMultiMap, readOnlyMultiMap1, resourcePathTranslator1, classMemberLookup1, classResolver1, messageReporter1);
    }
}
