package com.zelix.klassmaster.xml;

import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClassMemberLookup;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.log.MessageReporter;
import com.zelix.klassmaster.util.EnumerableMap;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ReadOnlyMultiMap;
import com.zelix.klassmaster.util.TwoKeyMap;
import com.zelix.klassmaster.util.ZkmAssert;

import java.io.IOException;
import java.util.Map;

public class BeanPropertyXmlHandler extends GenericXmlFileHandler {
    public static final DescriptorMatcher ANY_METHOD_DESCRIPTOR = new WildcardStringMatcher("(*)*");
    public static final DescriptorMatcher SETTER_DESCRIPTOR = new WildcardStringMatcher("(*)V");
    public static final DescriptorMatcher GETTER_DESCRIPTOR = new WildcardStringMatcher("()*");
    public static final DescriptorMatcher BOOLEAN_GETTER_DESCRIPTOR = new WildcardStringMatcher("()Z");

    public void addPropertyAccessorReferences(ProgramClass programClass1, String string, Map map1, TwoKeyMap twoKeyMap, String string1) throws ZkmException, IOException {
        Object object = new Object();
        String string2 = "set";
        String string3 = XmlConfigFileHandler.buildAccessorName(string2, string);
        this.addHierarchyMethodReferences(programClass1, string3, string2, object, SETTER_DESCRIPTOR, map1, twoKeyMap, string1);
        string2 = "get";
        String string4 = XmlConfigFileHandler.buildAccessorName(string2, string);
        this.addHierarchyMethodReferences(programClass1, string4, string2, object, GETTER_DESCRIPTOR, map1, twoKeyMap, string1);
        string2 = "is";
        String string5 = XmlConfigFileHandler.buildAccessorName(string2, string);
        this.addHierarchyMethodReferences(
                programClass1,
                string5,
                string2,
                object,
                HiddenOptionFlags.USE_BOOLEAN_IS_GETTER_TYPE ? BOOLEAN_GETTER_DESCRIPTOR : GETTER_DESCRIPTOR,
                map1,
                twoKeyMap,
                string1
        );
    }

    public BeanPropertyXmlHandler(
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

    public BeanPropertyXmlHandler(String string, ClassMemberLookup classMemberLookup1, ClassResolver classResolver1, MessageReporter messageReporter1) {
        super(string, classMemberLookup1, classResolver1, messageReporter1);
    }

    public void resolveRenamedPropertyName(String string, String string1, String string2, ObservableHolder observableHolder) throws ZkmException, IOException {
        String string3 = null;
        String string4 = XmlConfigFileHandler.buildAccessorName("set", string2);
        String string5 = this.lookupRenamedMethodName(string, string4, SETTER_DESCRIPTOR, 1);
        String string13;
        if (string5 != null) {
            String string6 = this.getPropertyNameFromAccessor("set", string5, string2);
            if (string6 == null) {
                super.messageReporter
                        .reportError(
                                "ERROR:",
                                "Could not retain bean format for property '"
                                        + string2
                                        + "' in  XML file '"
                                        + super.resourceName
                                        + "' : '"
                                        + string4
                                        + "' '"
                                        + string5
                                        + "' : '"
                                        + string1
                                        + "' (1)"
                        );
                string13 = "get";
            } else {
                string3 = string6;
                observableHolder.setValue(string6);
                string13 = "get";
            }
        } else {
            string13 = "get";
        }

        String string11 = XmlConfigFileHandler.buildAccessorName(string13, string2);
        String string7 = this.lookupRenamedMethodName(string, string11, GETTER_DESCRIPTOR, 1);
        if (string7 != null) {
            String string8 = this.getPropertyNameFromAccessor("get", string7, string2);
            if (string8 == null) {
                super.messageReporter
                        .reportError(
                                "ERROR:",
                                "Could not retain bean format for property '"
                                        + string2
                                        + "' in  XML file '"
                                        + super.resourceName
                                        + "' : '"
                                        + string11
                                        + "' '"
                                        + string7
                                        + "' : '"
                                        + string1
                                        + "' (2)"
                        );
            } else {
                if (string3 == null) {
                    string3 = string8;
                } else {
                    boolean bl = string3.equals(string8);
                    String[] strings = new String[]{
                            "Could not retain bean format for property '"
                                    + string2
                                    + "' in  XML file '"
                                    + super.resourceName
                                    + "' : Clashing new values '"
                                    + string3
                                    + "' '"
                                    + string8
                                    + "' : '"
                                    + string1
                                    + "' (C)"
                    };
                    ZkmAssert.assertTrue(bl, strings);
                }

                observableHolder.setValue(string8);
            }
        }

        String string12 = XmlConfigFileHandler.buildAccessorName("is", string2);
        String string9 = this.lookupRenamedMethodName(
                string, string12, HiddenOptionFlags.USE_BOOLEAN_IS_GETTER_TYPE ? BOOLEAN_GETTER_DESCRIPTOR : GETTER_DESCRIPTOR, 1
        );
        if (string9 != null) {
            String string10 = this.getPropertyNameFromAccessor("is", string9, string2);
            if (string10 == null) {
                super.messageReporter
                        .reportError(
                                "ERROR:",
                                "Could not retain bean format for property '"
                                        + string2
                                        + "' in  XML file '"
                                        + super.resourceName
                                        + "' : '"
                                        + string12
                                        + "' '"
                                        + string9
                                        + "' : '"
                                        + string1
                                        + "' (3)"
                        );
            } else {
                if (string3 != null) {
                    boolean bl1 = string3.equals(string10);
                    String[] strings1 = new String[]{
                            "Could not retain bean format for property '"
                                    + string2
                                    + "' in  XML file '"
                                    + super.resourceName
                                    + "' : Clashing new values '"
                                    + string3
                                    + "' '"
                                    + string10
                                    + "' : '"
                                    + string1
                                    + "' (D)"
                    };
                    ZkmAssert.assertTrue(bl1, strings1);
                }

                observableHolder.setValue(string10);
            }
        }
    }
}
