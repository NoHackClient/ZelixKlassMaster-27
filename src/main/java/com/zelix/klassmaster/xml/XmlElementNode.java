package com.zelix.klassmaster.xml;

import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.EmptyEnumeration;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

public class XmlElementNode {
    private static final String NO_NAME_MESSAGE = "Element or attribute has no name : '";
    public ObservableHolder tagNameHolder;
    public ObservableHolder currentAttributeNameHolder;
    public Map attributeNameHolders;
    public String originalTagName;
    public Map attributeValues;
    public List tokens = new ArrayList(10);

    public void addAttributeValue(String string) throws ZkmException, IOException {
        String string1 = string;
        if (this.currentAttributeNameHolder == null) {
            throw new XmlContentException(NO_NAME_MESSAGE + string1 + "'");
        }

        if (HiddenOptionFlags.TRIM_XML_TEXT) {
            string1 = string1.trim();
        }

        ObservableHolder observableHolder = new ObservableHolder(string1);
        String string2 = (String) this.currentAttributeNameHolder.getValue();
        Map map1;
        if (this.attributeNameHolders == null) {
            this.attributeNameHolders = ZkmUtils.createHashMap(13);
            this.attributeValues = ZkmUtils.createHashMap(13);
            map1 = this.attributeNameHolders;
        } else {
            map1 = this.attributeNameHolders;
        }

        map1.put(string2, this.currentAttributeNameHolder);
        this.attributeValues.put(string2, observableHolder);
        this.tokens.add(observableHolder);
    }

    public void addRawText(String string) {
        if (string.length() > 0) {
            this.tokens.add(string);
        }
    }

    public boolean renameAttribute(String string, String string1) throws ZkmException, IOException {
        if (this.attributeValues != null) {
            ObservableHolder observableHolder = (ObservableHolder) this.attributeValues.remove(string);
            if (observableHolder != null) {
                this.attributeValues.put(string1, observableHolder);
                ((ObservableHolder) this.attributeNameHolders.get(string)).setValue(string1);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public ObservableHolder getAttributeValue(Object object) {
        return this.attributeValues != null ? (ObservableHolder) this.attributeValues.get(object) : null;
    }

    public Enumeration getAttributeNames() {
        return this.attributeValues != null ? Collections.enumeration(new ArrayList(this.attributeValues.keySet())) : new EmptyEnumeration();
    }

    public void addName(String string) throws ZkmException, IOException {
        ObservableHolder observableHolder = new ObservableHolder(string);
        List list1;
        if (this.tagNameHolder == null) {
            this.originalTagName = string;
            this.tagNameHolder = observableHolder;
            list1 = this.tokens;
        } else {
            this.currentAttributeNameHolder = observableHolder;
            list1 = this.tokens;
        }

        list1.add(observableHolder);
    }

    public String getOriginalTagName() {
        return this.originalTagName;
    }

    public void setTagName(Object object) throws ZkmException, IOException {
        this.tagNameHolder.setValue(object);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        boolean predicateFlag_boolean = XmlResourceParserBase.getPredicateFlag_boolean();
        int ba = this.tokens.size();
        boolean bl = predicateFlag_boolean;
        int bb = 0;

        while (bb < ba) {
            label23:
            {
                Object object;
                label22:
                {
                    object = this.tokens.get(bb);
                    if (bl) {
                        if (!(object instanceof ObservableHolder)) {
                            break label22;
                        }

                        stringBuilder.append((String) ((ObservableHolder) object).getValue());
                    }

                    if (bl) {
                        break label23;
                    }
                }

                stringBuilder.append(object);
            }

            bb++;
            if (!bl) {
                break;
            }
        }

        return stringBuilder.toString();
    }

    public String getTagName() {
        boolean predicateFlagClear = XmlResourceParserBase.isPredicateFlagClear();
        Object object = this;
        if (!predicateFlagClear) {
            if (this.tagNameHolder == null) {
                return null;
            }

            object = this.tagNameHolder.getValue();
        }

        return (String) object;
    }
}
