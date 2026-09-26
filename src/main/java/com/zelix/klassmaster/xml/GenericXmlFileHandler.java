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

public class GenericXmlFileHandler extends XmlConfigFileHandler {
    public GenericXmlFileHandler(
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

    @Override
    public final void analyzeCharacterData(String string, String string1, Map map1, Object object) throws ZkmException, IOException {
        if (string1 == null) {
            throw new XmlContentException("Character data outside of tag : '" + ZkmStringUtils.escapeJavaString(string) + "' (5a)");
        }

        String string2 = "reference in '" + super.resourceName + "' by '" + string1 + "' tag";
        this.addClassNameReferences(string, map1, string2, string1, true);
    }

    public GenericXmlFileHandler(String string, ClassMemberLookup classMemberLookup1, ClassResolver classResolver1, MessageReporter messageReporter1) {
        super(string, classMemberLookup1, classResolver1, messageReporter1);
    }

    @Override
    public final void rewriteCharacterData(String string, String string1, List list1) throws XmlContentException, IOException {
        if (string1 == null) {
            throw new XmlContentException("Character data outside of tag : '" + ZkmStringUtils.escapeJavaString(string) + "' (5)");
        }

        list1.add(this.updateReferenceValue(string, string1, true));
    }
}
