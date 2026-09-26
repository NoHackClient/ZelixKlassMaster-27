package com.zelix.klassmaster.xml;

import com.zelix.klassmaster.archive.ZkmFileUtils;
import com.zelix.klassmaster.classfile.hierarchy.ClassMemberLookup;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.log.MessageReporter;
import com.zelix.klassmaster.util.EnumerableMap;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ReadOnlyMultiMap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public abstract class SpringXmlHandlerBase extends BeanPropertyXmlHandler {
    public final Map beanClasses = ZkmUtils.createHashMap();
    public final Map abstractBeans = ZkmUtils.createHashMap();
    public final Map beanParents = ZkmUtils.createHashMap();
    public final ResourceFileIndex resourceFileIndex;

    public void processImportResource(String string) throws ZkmException, IOException {
        List list1;
        if (!string.startsWith("classpath:") && !string.startsWith("classpath*:") && !string.startsWith("file:")) {
            ObservableHolder observableHolder = new ObservableHolder();
            ObservableHolder observableHolder1 = new ObservableHolder();
            ZkmFileUtils.splitPath(super.entryName, observableHolder, observableHolder1);
            String string2 = (String) observableHolder.getValue();
            String string3 = string;
            if (string3.startsWith("/")) {
                string3 = string3.substring(1);
            }

            ZkmFileUtils.splitPath(string3, observableHolder, observableHolder1);
            String string5 = (String) observableHolder.getValue();
            string5 = (String) observableHolder1.getValue();
            list1 = this.resolveImportedResources(string3, string2);
        } else {
            int ba = string.indexOf(":");
            String string1 = string.substring(ba + 1);
            if (string1.startsWith("/")) {
                string1 = string1.substring(1);
            }

            list1 = this.resolveImportedResources(string1, (String) null);
        }

        if (list1 != null) {
            Iterator iterator = list1.iterator();

            while (iterator.hasNext()) {
                String string4 = (String) iterator.next();
                this.resourceFileIndex.addImport(string4, super.resourceName);
            }
        }
    }

    public SpringXmlHandlerBase(
            String string, ResourceFileIndex resourceFileIndex1, ClassMemberLookup classMemberLookup1, ClassResolver classResolver1, MessageReporter messageReporter1
    ) {
        super(string, classMemberLookup1, classResolver1, messageReporter1);
        this.resourceFileIndex = resourceFileIndex1;
    }

    public SpringXmlHandlerBase(
            String string,
            EnumerableMap enumerableMap,
            ReadOnlyMultiMap readOnlyMultiMap,
            ReadOnlyMultiMap readOnlyMultiMap1,
            ResourceFileIndex resourceFileIndex1,
            ResourcePathTranslator resourcePathTranslator1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1,
            MessageReporter messageReporter1
    ) {
        super(string, enumerableMap, readOnlyMultiMap, readOnlyMultiMap1, resourcePathTranslator1, classMemberLookup1, classResolver1, messageReporter1);
        this.resourceFileIndex = resourceFileIndex1;
    }

    public abstract List resolveImportedResources(String string, String string1) throws ZkmException, IOException;

    public List splitBeanNames(String string) {
        StringTokenizer stringTokenizer = new StringTokenizer(string, " ,;");
        ArrayList arrayList = new ArrayList(stringTokenizer.countTokens());

        while (stringTokenizer.hasMoreTokens()) {
            arrayList.add(stringTokenizer.nextToken());
        }

        return arrayList;
    }
}
