package com.zelix.klassmaster.xml;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.ObjectStack;
import com.zelix.klassmaster.util.ZkmStringUtils;

import java.io.IOException;
import java.util.List;

public abstract class XmlResourceParserBase implements XmlContentHandler {
    private static boolean predicateFlag;
    public ObjectStack elementStack = new ObjectStack();
    public final String resourceName;
    public final String entryName;

    @Override
    public void beforeFlushOutput() throws ZkmException, IOException {
    }

    public boolean isWithinElement(String string) {
        return this.elementStack.isEmpty() ? false : this.findOpenElementDepth(string) != -1;
    }

    @Override
    public boolean shouldFlushOutput() {
        return true;
    }

    @Override
    public void rewriteProcessingInstruction(Object object, List list1) {
        list1.add(object);
    }

    public static String extractDoctype(String string) {
        int ba = string.indexOf("<!DOCTYPE");
        if (ba > -1) {
            int bb = string.indexOf(">", ba);
            if (bb > ba) {
                return string.substring(ba, bb);
            }
        }

        return "";
    }

    public final XmlElementNode getOpenElement(int ba) {
        int bb = this.elementStack.size();
        return !this.elementStack.isEmpty() && ba < bb ? (XmlElementNode) this.elementStack.peekAt(ba) : null;
    }

    public static boolean isPredicateFlagClear() {
        return !getPredicateFlag_boolean();
    }

    @Override
    public void rewriteCharacterData(String string, String string1, List list1) throws XmlContentException, IOException {
        if (string1 == null) {
            throw new XmlContentException("Character data outside of tag : '" + ZkmStringUtils.escapeJavaString(string) + "' (1)");
        }

        list1.add(string);
    }

    @Override
    public void rewriteStartTag(XmlElementNode xmlElementNode, List list1) throws ZkmException, IOException {
        this.elementStack.push(xmlElementNode);
        list1.add(xmlElementNode);
    }

    public static String extractSchemaLocation(String string) {
        int ba = string.indexOf(":schemaLocation=\"");
        if (ba > -1) {
            int bb = string.indexOf("\"", ba + ":schemaLocation=\"".length());
            if (bb > ba) {
                return string.substring(ba + ":schemaLocation=\"".length(), bb);
            }
        }

        return "";
    }

    public static boolean getPredicateFlag_boolean() {
        return predicateFlag;
    }

    public String getCurrentElementName() {
        return this.elementStack.isEmpty() ? null : ((XmlElementNode) this.elementStack.peek()).getTagName();
    }

    public XmlResourceParserBase(String string) {
        this.resourceName = string;
        int ba = string.lastIndexOf("!");
        if (ba > 0) {
            this.entryName = string.substring(ba + 1);
        } else {
            this.entryName = string;
        }
    }

    public void onElementClosed(Object object) {
    }

    public static void setPredicateFlag_v() {
        predicateFlag = true;
    }

    public int findOpenElementDepth(Object object) {
        int ba = 0;
        int bb = 0;

        for (ObjectStack objectStack = this.elementStack; bb < objectStack.size(); objectStack = this.elementStack) {
            if (((XmlElementNode) this.elementStack.peekAt(ba)).getOriginalTagName().equals(object)) {
                return ba;
            }

            bb = ++ba;
        }

        return -1;
    }

    @Override
    public final void handleEndTag(String string, List list1) {
        int ba = this.findOpenElementDepth(string);
        if (ba != -1) {
            XmlElementNode xmlElementNode = null;
            int bb = ba + 1;

            for (int i = 0; i < bb; i++) {
                xmlElementNode = (XmlElementNode) this.elementStack.pop();
            }

            if (list1 != null) {
                xmlElementNode.getClass();
                list1.add(new XmlElementLabel(xmlElementNode));
            }

            this.onElementClosed(string);
        }
    }

    @Override
    public void rewriteEmptyElementTag(Object object, List list1) throws ZkmException, IOException {
        list1.add(object);
    }

    public String getOpenElementName(int ba) {
        int bb = this.elementStack.size();
        return !this.elementStack.isEmpty() && ba < bb ? ((XmlElementNode) this.elementStack.peekAt(ba)).getTagName() : null;
    }

    @Override
    public void finishAnalysis(Object object, Object object1) throws ZkmException, IOException {
    }

    static {
        setPredicateFlag_v();
    }
}
