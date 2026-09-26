package com.zelix.klassmaster.obfuscator.rename;

import com.zelix.klassmaster.util.TwoKeyMap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class CumulativeNameMapping extends ComposableNameMapping {
    @Override
    public int composeWith(Map map1) {
        if (super.mapping == null) {
            super.mapping = ZkmUtils.copyToHashMap(map1);
        } else if (map1 != null && map1.size() > 0) {
            TwoKeyMap twoKeyMap = new TwoKeyMap(super.mapping.size());
            Iterator iterator = super.mapping.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                Object object = entry.getKey();
                Object object1 = entry.getValue();
                twoKeyMap.putValue(object1, object, object);
            }

            iterator = map1.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry1 = (Entry) iterator.next();
                Object object3 = entry1.getKey();
                Object object4 = entry1.getValue();
                Map map2 = twoKeyMap.getInnerMap(object3);
                if (map2 != null) {
                    for (Object object2 : map2.keySet()) {
                        super.mapping.put(object2, object4);
                    }
                } else {
                    super.mapping.put(object3, object4);
                }
            }
        }

        return this.getMappingSize();
    }
}
