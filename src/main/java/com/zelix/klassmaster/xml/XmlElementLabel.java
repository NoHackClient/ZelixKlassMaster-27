package com.zelix.klassmaster.xml;

public class XmlElementLabel {
    public final XmlElementNode element;

    public XmlElementLabel(XmlElementNode xmlElementNode) {
        this.element = xmlElementNode;
    }

    @Override
    public String toString() {
        return this.element.getTagName();
    }
}
