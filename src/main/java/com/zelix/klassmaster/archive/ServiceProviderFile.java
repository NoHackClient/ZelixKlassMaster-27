package com.zelix.klassmaster.archive;

import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.EnumerableMap;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.OrderedIndexedMap;
import com.zelix.klassmaster.util.ZkmStringUtils;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ServiceProviderFile {
    public static final int SERVICES_PREFIX_LENGTH = "META-INF/services/".length();
    public OrderedIndexedMap lines = new OrderedIndexedMap();
    public ArrayList providerClassNames = new ArrayList();
    public boolean compressed;
    public String serviceName;

    public void mergeFrom(ServiceProviderFile serviceProviderFile1) {
        if (!this.compressed && !serviceProviderFile1.compressed) {
            this.compressed = false;
        } else {
            this.compressed = true;
        }

        HashSet hashSet = ZkmUtils.createHashSet();
        Iterator iterator = this.lines.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            String string = (String) entry.getValue();
            if (string != null) {
                hashSet.add(string);
            }
        }

        iterator = serviceProviderFile1.lines.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry1 = (Entry) iterator.next();
            String string2 = (String) entry1.getKey();
            String string1 = (String) entry1.getValue();
            if (string1 != null) {
                if (hashSet.add(string1)) {
                    this.lines.put(string2, string1);
                }
            } else if (!this.lines.containsKey(string2)) {
                this.lines.put(string2, null);
            }
        }
    }

    
    
    public void writeTranslated(ZipOutputStream zipOutputStream1, Long long1, EnumerableMap enumerableMap, boolean bl) throws IOException {
        ZipEntryWriter zipEntryWriter = new ZipEntryWriter(zipOutputStream1, this.getTranslatedEntryName(enumerableMap), bl);
        PrintWriter printWriter = null;
        boolean bl1 = false ;

        try {
            bl1 = true;
            printWriter = new PrintWriter(new OutputStreamWriter(zipEntryWriter.getOutputStream(), "UTF-8"));
            Iterator iterator = this.lines.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                String string = (String) entry.getKey();
                String string1 = (String) entry.getValue();
                if (string1 != null) {
                    String string2 = string1.replace('.', '/');
                    String string3 = (String) ZkmUtils.mapOrSelf(string2, enumerableMap);
                    if (!string2.equals(string3)) {
                        string = ZkmStringUtils.replaceAll(string, string1, string3.replace('/', '.'));
                    }
                }

                printWriter.println(string);
            }

            printWriter.flush();
            bl1 = false;
        } finally {
            if (bl1) {
                if (printWriter != null) {
                    printWriter.close();
                }
            }
        }

        printWriter.close();
        zipEntryWriter.writeEntry(long1);
    }

    public ServiceProviderFile(ZipFile zipFile1, ZipEntry zipEntry1) throws ZkmException, IOException {
        if (zipEntry1.getMethod() == 8) {
            this.compressed = true;
        } else {
            this.compressed = false;
        }

        this.serviceName = zipEntry1.getName().substring(SERVICES_PREFIX_LENGTH).trim();
        InputStream inputStream1 = null;
        BufferedReader bufferedReader = null;

        try {
            inputStream1 = zipFile1.getInputStream(zipEntry1);
            bufferedReader = ZkmFileUtils.createBomAwareReader(inputStream1, "UTF-8", (ObservableHolder) null);

            String string;
            while ((string = bufferedReader.readLine()) != null) {
                String string1 = string;
                int ba = string.indexOf("#");
                if (ba != -1) {
                    if (ba > 0) {
                        string1 = string.substring(0, ba - 1);
                    } else {
                        string1 = "";
                    }
                }

                string1 = string1.trim();
                if (string1.length() > 0) {
                    this.providerClassNames.add(string1);
                    this.lines.put(string, string1);
                } else {
                    this.lines.put(string, null);
                }
            }
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException iOException1) {
                }
            } else if (inputStream1 != null) {
                try {
                    inputStream1.close();
                } catch (IOException iOException) {
                }
            }
        }
    }

    public String getTranslatedEntryName(EnumerableMap enumerableMap) {
        String string = ((String) ZkmUtils.mapOrSelf(this.serviceName.replace('.', '/'), enumerableMap)).replace('/', '.');
        return "META-INF/services/" + string;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public boolean isCompressed() {
        return this.compressed;
    }

    public void collectReferencedClasses(String string, Map map1) {
        String string1 = "'META-INF/services/" + this.serviceName + "' in '" + string + "'";
        ProgramClass programClass1 = ClassHierarchyNode.findProgramClass(ZkmUtils.dotsToSlashes(this.serviceName));
        if (programClass1 != null) {
            map1.put(programClass1, string1);
        }

        int ba = 0;
        int bb = 0;

        for (ArrayList arrayList = this.providerClassNames; bb < arrayList.size(); arrayList = this.providerClassNames) {
            ProgramClass programClass2 = ClassHierarchyNode.findProgramClass(ZkmUtils.dotsToSlashes((String) this.providerClassNames.get(ba)));
            if (programClass2 != null) {
                map1.put(programClass2, string1);
            }

            bb = ++ba;
        }
    }
}
