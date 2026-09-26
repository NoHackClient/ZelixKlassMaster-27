package com.zelix.klassmaster.obfuscator.rename;

import com.zelix.klassmaster.util.EnumerableMap;

import java.util.Map;

public abstract class ComposableNameMapping {
    public Map mapping;

    public EnumerableMap getMappingCopy() {
        return this.mapping == null ? new EnumerableMap() : new EnumerableMap(this.mapping);
    }

    public abstract int composeWith(Map map1);

    public int getMappingSize() {
        return this.mapping != null ? this.mapping.size() : 0;
    }

    public void clearMapping() {
        if (this.mapping != null) {
            this.mapping.clear();
        }
    }
}
