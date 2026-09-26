package com.zelix.klassmaster.util;

import java.io.PrintWriter;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class SoftReferenceMap implements Map {
    private static String clearedMessageSuffix;
    private static long defaultCapacity;
    public PrintWriter[] logWriters;
    private SoftReference mapReference;
    public int initialCapacity;
    public String mapName;

    @Override
    public synchronized Object get(Object object) {
        Map map1 = (Map) this.mapReference.get();
        if (map1 == null) {
            map1 = this.recreateClearedMap();
        }

        return map1.get(object);
    }

    public SoftReferenceMap(String string) {
        this(string, (int) defaultCapacity);
    }

    public void resetMap() {
        this.mapReference = new SoftReference<>(ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(this.initialCapacity)));
    }

    @Override
    public synchronized Collection values() {
        Map map1 = (Map) this.mapReference.get();
        if (map1 == null) {
            this.recreateClearedMap();
            return Collections.unmodifiableSet(ZkmUtils.createHashSet());
        } else {
            return Collections.unmodifiableCollection(map1.values());
        }
    }

    @Override
    public synchronized int size() {
        Map map1 = (Map) this.mapReference.get();
        if (map1 == null) {
            map1 = this.recreateClearedMap();
        }

        return map1.size();
    }

    @Override
    public synchronized Set entrySet() {
        Map map1 = (Map) this.mapReference.get();
        if (map1 == null) {
            this.recreateClearedMap();
            return Collections.unmodifiableSet(ZkmUtils.createHashSet());
        } else {
            return Collections.unmodifiableSet(map1.entrySet());
        }
    }

    @Override
    public synchronized Object put(Object object, Object object1) {
        Map map1 = (Map) this.mapReference.get();
        if (map1 == null) {
            map1 = this.recreateClearedMap();
        }

        return map1.put(object, object1);
    }

    @Override
    public synchronized boolean isEmpty() {
        Map map1 = (Map) this.mapReference.get();
        if (map1 == null) {
            map1 = this.recreateClearedMap();
        }

        return map1.isEmpty();
    }

    @Override
    public synchronized Set keySet() {
        Map map1 = (Map) this.mapReference.get();
        if (map1 == null) {
            this.recreateClearedMap();
            return Collections.unmodifiableSet(ZkmUtils.createHashSet());
        } else {
            return Collections.unmodifiableSet(map1.keySet());
        }
    }

    public SoftReferenceMap(String string, int initialCapacity) {
        this.initialCapacity = initialCapacity;
        this.mapName = string;
        this.resetMap();
    }

    @Override
    public synchronized void putAll(Map map1) {
        Map map2 = (Map) this.mapReference.get();
        if (map2 == null) {
            map2 = this.recreateClearedMap();
            map2.putAll(map1);
        } else {
            map2.putAll(map1);
        }
    }

    public Map recreateClearedMap() {
        this.resetMap();
        Map map1 = (Map) this.mapReference.get();
        if (this.logWriters != null) {
            for (int i = 0; i < this.logWriters.length; i++) {
                this.logWriters[i].println(this.mapName + clearedMessageSuffix);
                this.logWriters[i].flush();
            }
        }

        return map1;
    }

    @Override
    public synchronized boolean containsKey(Object object) {
        Map map1 = (Map) this.mapReference.get();
        if (map1 == null) {
            this.recreateClearedMap();
            return false;
        } else {
            return map1.containsKey(object);
        }
    }

    @Override
    public synchronized boolean containsValue(Object object) {
        Map map1 = (Map) this.mapReference.get();
        if (map1 == null) {
            this.recreateClearedMap();
            return false;
        } else {
            return map1.containsValue(object);
        }
    }

    @Override
    public synchronized Object remove(Object object) {
        Map map1 = (Map) this.mapReference.get();
        if (map1 == null) {
            map1 = this.recreateClearedMap();
        }

        return map1.remove(object);
    }

    @Override
    public synchronized void clear() {
        Map map1 = (Map) this.mapReference.get();
        if (map1 == null) {
            map1 = this.recreateClearedMap();
        }

        map1.clear();
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
        clearedMessageSuffix = " cleared due to lack of memory...";
        defaultCapacity = 6566433580250038373L;
    }
}
