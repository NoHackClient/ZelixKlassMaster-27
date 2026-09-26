package com.zelix.klassmaster.xml;

import java.util.ArrayList;
import java.util.List;

public class AbstractXmlBeanRef {
    public final List propertyElements = new ArrayList();
    private final XmlElementNode beanElement;
    private final XmlElementNode parentElement;

    public void addPropertyElement(Object object) {
        this.propertyElements.add(object);
    }

    public XmlElementNode getParentElement() {
        return this.parentElement;
    }

    public AbstractXmlBeanRef(XmlElementNode xmlElementNode, XmlElementNode xmlElementNode1) {
        this.beanElement = xmlElementNode;
        this.parentElement = xmlElementNode1;
    }

    public List getPropertyElements() {
        return this.propertyElements;
    }

    public XmlElementNode getBeanElement() {
        return this.beanElement;
    }
}
