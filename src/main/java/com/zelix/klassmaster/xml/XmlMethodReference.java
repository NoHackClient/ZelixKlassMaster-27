package com.zelix.klassmaster.xml;

import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ZkmAssert;

public class XmlMethodReference {
    public ObservableHolder methodNameHolder;
    public String beanName;
    public String referenceDescription;

    public static ObservableHolder getMethodNameHolderOf(XmlMethodReference xmlMethodReference) {
        return xmlMethodReference.getMethodNameHolder();
    }

    public static String getReferenceDescriptionOf(XmlMethodReference xmlMethodReference) {
        return xmlMethodReference.getReferenceDescription();
    }

    @Override
    public String toString() {
        return "<" + ZkmAssert.getSimpleClassName(this) + " " + this.beanName + ", " + this.methodNameHolder + ", " + this.referenceDescription + ", >";
    }

    public String getReferenceDescription() {
        return this.referenceDescription;
    }

    public static String getBeanNameOf(XmlMethodReference xmlMethodReference) {
        return xmlMethodReference.getBeanName();
    }

    public ObservableHolder getMethodNameHolder() {
        return this.methodNameHolder;
    }

    public XmlMethodReference(long ba, String string, ObservableHolder observableHolder, String string1, SyntheticAccessMarker syntheticAccessMarker) {
        this(string, observableHolder, string1);
    }

    public XmlMethodReference(String string, String string1) {
        this.beanName = string;
        this.referenceDescription = string1;
    }

    public String getBeanName() {
        return this.beanName;
    }

    private XmlMethodReference(String string, ObservableHolder observableHolder, String string1) {
        this.beanName = string;
        this.referenceDescription = string1;
        this.methodNameHolder = observableHolder;
    }
}
