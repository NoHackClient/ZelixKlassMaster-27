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

public class WebXmlReferenceHandler extends XmlConfigFileHandler {
    public WebXmlReferenceHandler(String string, ClassMemberLookup classMemberLookup1, ClassResolver classResolver1, MessageReporter messageReporter1) {
        super(string, classMemberLookup1, classResolver1, messageReporter1);
    }

    public WebXmlReferenceHandler(
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
    public void rewriteCharacterData(String string, String string1, List list1) throws XmlContentException, IOException {
        if (string1 == null) {
            throw new XmlContentException("Character data outside of tag : '" + ZkmStringUtils.escapeJavaString(string) + "' (6)");
        }

        if (!string1.equals("servlet-class")
                && !string1.equals("exception-type")
                && !string1.equals("res-type")
                && !string1.equals("home")
                && !string1.equals("remote")
                && !string1.equals("local")
                && !string1.equals("local-home")
                && !string1.equals("filter-class")
                && !string1.equals("listener-class")
                && !string1.equals("resource-env-ref-type")) {
            ResourcePathTranslator resourcePathTranslator1;
            if (!string1.equals("small-icon")) {
                if (!string1.equals("large-icon")) {
                    if (!string1.equals("jsp-file")) {
                        if (!string1.equals("welcome-file")) {
                            if (!string1.equals("taglib-location")) {
                                if (!string1.equals("location")) {
                                    if (!string1.equals("form-login-page")) {
                                        if (!string1.equals("form-error-page")) {
                                            list1.add(this.updateReferenceValue(string, string1, true));
                                            return;
                                        }

                                        resourcePathTranslator1 = super.resourcePathTranslator;
                                    } else {
                                        resourcePathTranslator1 = super.resourcePathTranslator;
                                    }
                                } else {
                                    resourcePathTranslator1 = super.resourcePathTranslator;
                                }
                            } else {
                                resourcePathTranslator1 = super.resourcePathTranslator;
                            }
                        } else {
                            resourcePathTranslator1 = super.resourcePathTranslator;
                        }
                    } else {
                        resourcePathTranslator1 = super.resourcePathTranslator;
                    }
                } else {
                    resourcePathTranslator1 = super.resourcePathTranslator;
                }
            } else {
                resourcePathTranslator1 = super.resourcePathTranslator;
            }

            String string3 = resourcePathTranslator1.translateResourcePath(string);
            list1.add(string3);
        } else {
            String string2 = this.getRenamedClassName(string);
            list1.add(string2);
        }
    }

    @Override
    public void analyzeCharacterData(String string, String string1, Map map1, Object object) throws ZkmException, IOException {
        if (string1 == null) {
            throw new XmlContentException("Character data outside of tag : '" + ZkmStringUtils.escapeJavaString(string) + "' (6a)");
        }

        String string2 = "reference in '" + super.resourceName + "' by '" + string1 + "' tag";
        if (string1.equals("servlet-class")
                || string1.equals("exception-type")
                || string1.equals("res-type")
                || string1.equals("home")
                || string1.equals("remote")
                || string1.equals("local")
                || string1.equals("local-home")
                || string1.equals("filter-class")
                || string1.equals("listener-class")
                || string1.equals("resource-env-ref-type")) {
            this.addClassReference(string, map1, string2);
        } else if (!string1.equals("small-icon")
                && !string1.equals("large-icon")
                && !string1.equals("jsp-file")
                && !string1.equals("welcome-file")
                && !string1.equals("taglib-location")
                && !string1.equals("location")
                && !string1.equals("form-login-page")
                && !string1.equals("form-error-page")) {
            this.addClassNameReferences(string, map1, string2, string1, true);
        }
    }
}
