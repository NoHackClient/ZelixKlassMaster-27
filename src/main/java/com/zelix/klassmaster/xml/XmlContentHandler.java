package com.zelix.klassmaster.xml;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.TwoKeyMap;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface XmlContentHandler {
    void analyzeProcessingInstruction(Object object);

    void finishAnalysis(Object object, Object object1) throws ZkmException, IOException;

    void analyzeCharacterData(String string, String string1, Map map1, Object object) throws ZkmException, IOException;

    void rewriteCharacterData(String string, String string1, List list1) throws XmlContentException, IOException;

    void rewriteProcessingInstruction(Object object, List list1);

    boolean shouldFlushOutput();

    void analyzeStartTag(XmlElementNode xmlElementNode, Map map1, Map map2, Map map3, TwoKeyMap twoKeyMap) throws ZkmException, IOException;

    void analyzeEmptyElementTag(XmlElementNode xmlElementNode, Map map1, Map map2, Map map3, TwoKeyMap twoKeyMap) throws ZkmException, IOException;

    void rewriteStartTag(XmlElementNode xmlElementNode, List list1) throws ZkmException, IOException;

    void rewriteEmptyElementTag(Object object, List list1) throws ZkmException, IOException;

    void beforeFlushOutput() throws ZkmException, IOException;

    void handleEndTag(String string, List list1);
}
