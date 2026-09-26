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

public class EjbJarXmlHandler extends XmlConfigFileHandler {
    public String currentEjbClass;

    @Override
    public void onElementClosed(Object object) {
        String string = (String) object;
        if (!string.equals("entity")) {
            if (!string.equals("session")) {
                if (string.equals("message-driven")) {
                    this.currentEjbClass = null;
                }
            } else {
                this.currentEjbClass = null;
            }
        } else {
            this.currentEjbClass = null;
        }
    }

    @Override
    public void analyzeCharacterData(String string, String string1, Map map1, Object object) throws ZkmException, IOException {
        Map map2 = (Map) object;
        if (string1 == null) {
            throw new XmlContentException("Character data outside of tag : '" + ZkmStringUtils.escapeJavaString(string) + "' (4a)");
        }

        String string2 = "reference in '" + super.resourceName + "' by '" + string1 + "' tag";
        if (string1.equals("ejb-class")
                || string1.equals("home")
                || string1.equals("remote")
                || string1.equals("local")
                || string1.equals("local-home")
                || string1.equals("prim-key-class")
                || string1.equals("res-type")
                || string1.equals("dependent-class")) {
            String string3 = this.addClassReference(string, map1, string2);
            if (string1.equals("ejb-class")) {
                this.currentEjbClass = string3;
            }
        } else if (string1.equals("method-param")) {
            this.addTypeReference(string, map1, string2);
        } else if (!string1.equals("small-icon") && !string1.equals("large-icon")) {
            if (!string1.equals("field-name") && !string1.equals("primkey-field")) {
                this.addClassNameReferences(string, map1, string2, string1, true);
            } else if ((this.isWithinElement("entity") || this.isWithinElement("session") || this.isWithinElement("message-driven"))
                    && this.currentEjbClass != null) {
                this.addFieldReferenceByClassName(this.currentEjbClass, string, map2, string2);
            }
        }
    }

    public EjbJarXmlHandler(
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
            throw new XmlContentException("Character data outside of tag : '" + ZkmStringUtils.escapeJavaString(string) + "' (4)");
        }

        if (string1.equals("ejb-class")
                || string1.equals("home")
                || string1.equals("remote")
                || string1.equals("local")
                || string1.equals("local-home")
                || string1.equals("prim-key-class")
                || string1.equals("res-type")
                || string1.equals("dependent-class")) {
            String string3 = this.getRenamedClassName(string);
            if (string1.equals("ejb-class")) {
                this.currentEjbClass = string3;
            }

            list1.add(string3);
        } else if (string1.equals("method-param")) {
            list1.add(this.getRenamedTypeName(string));
        } else {
            ResourcePathTranslator resourcePathTranslator1;
            if (!string1.equals("small-icon")) {
                if (!string1.equals("large-icon")) {
                    if (!string1.equals("field-name") && !string1.equals("primkey-field")) {
                        list1.add(this.updateReferenceValue(string, string1, true));
                        return;
                    }

                    if ((this.isWithinElement("entity") || this.isWithinElement("session") || this.isWithinElement("message-driven"))
                            && this.currentEjbClass != null) {
                        list1.add(this.getRenamedFieldName(this.currentEjbClass, string));
                    } else {
                        list1.add(string);
                    }

                    return;
                }

                resourcePathTranslator1 = super.resourcePathTranslator;
            } else {
                resourcePathTranslator1 = super.resourcePathTranslator;
            }

            String string2 = resourcePathTranslator1.translateResourcePath(string);
            list1.add(string2);
        }
    }

    public EjbJarXmlHandler(String string, ClassMemberLookup classMemberLookup1, ClassResolver classResolver1, MessageReporter messageReporter1) {
        super(string, classMemberLookup1, classResolver1, messageReporter1);
    }
}
